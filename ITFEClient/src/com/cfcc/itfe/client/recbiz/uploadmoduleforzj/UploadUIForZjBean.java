package com.cfcc.itfe.client.recbiz.uploadmoduleforzj;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.file.core.IFileParser;
import com.cfcc.itfe.client.common.file.parser.FileParserImpl;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
/**
 * codecomment: 
 * @author zhangliang
 * @time   14-07-22 16:57:14
 * 子系统: RecBiz
 * 模块:UploadModuleForZj
 * 组件:UploadUIForZj
 */
@SuppressWarnings("unchecked")
public class UploadUIForZjBean extends AbstractUploadUIForZjBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(UploadUIForZjBean.class);
	private ITFELoginInfo loginfo ;
	private FileResultDto fileResultDto;
	private TvInfileDto inputsrlnodto;
	private TvInfileDto affirmsrlnodto;
	private List<TvInfileDto> affirmList;
	private List<TvInfileDto> checkFileList;
	private String errorinfo ;
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	boolean flag = false; // 上传的错误标志 
	Exception err = null; // 定义上传的错误异常
    public UploadUIForZjBean() {
      super();
      fileList = new ArrayList();
      errorinfo = null;
	  affirmList = new ArrayList<TvInfileDto>();
	  checkFileList = new ArrayList<TvInfileDto>();
	  inputsrlnodto = new TvInfileDto();
	  affirmsrlnodto = new TvInfileDto();
	  fileResultDto = new FileResultDto();
	  loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    }
    /**
	 * Direction: 数据加载提交 ename: upload 引用方法: viewers: * messages:
	 */
	public String upload(Object o) {
		
		// 提取加载文件的绝对路径和名称 
		if(null == fileList || fileList.size() == 0){
			MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
			return super.upload(o);
		}
		
		for(int i = 0 ; i < fileList.size(); i++){
			File f = (File) this.fileList.get(i);		//文件对象
			String fName = f.getName().toLowerCase();   // 文件名称
			String fPath = f.getAbsolutePath(); // 文件路径
			
			if(null == f || null == fName || null == fPath){
				MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
				return super.upload(o);
			}
			
			try {
				VerifyFileName.verifyIncomeFile(fName);
				IFileParser fileparse = new FileParserImpl();
				fileResultDto = fileparse.dealFile(fPath, fName, loginfo.getSorgcode(), sequenceHelperService);
				
				if (BizTypeConstant.BIZ_TYPE_INCOME.equals(fileResultDto.getSbiztype())) {
					// 收入业务，要校验资金收纳流水号是否填写
					if(null == inputsrlnodto.getStrasrlno() || "".equals(inputsrlnodto.getStrasrlno().trim())
							|| inputsrlnodto.getStrasrlno().trim().length() != 18){
						MessageDialog.openMessageDialog(null, "收入业务：资金收纳流水号必须填写，且必须为18位！");
						return super.upload(o);
					}
					
					fileResultDto.setIsError(false);
					fileResultDto.setStrasrlno(inputsrlnodto.getStrasrlno().trim());
				}else{
					if(null == inputsrlnodto.getNmoney()){
						MessageDialog.openMessageDialog(null, "支出类业务：文件总金额必须填写！");
						return super.upload(o);
					}
					fileResultDto.setFsumamt(inputsrlnodto.getNmoney());
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
				return super.upload(o);
			}
			
			try {
				if (MsgConstant.MSG_SOURCE_PLACE.equals(fileResultDto.getCsourceflag())) {
					// 地税接口格式数据
					String sUploadFilePath = ClientFileTransferUtil.uploadFile(f.getAbsolutePath());
					fileResultDto.setSfilename(fName); // 文件名称
					fileResultDto.setSmaininfo(sUploadFilePath);
//					MessageDialog.openMessageDialog(null, "文件[" + fName + "]加载成功！服务器文件路径" + sUploadFilePath );
				}
			} catch (FileTransferException e) {
				MessageDialog.openErrorDialog(null, e);
				return super.upload(o);
			}
			
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("正在对[" + fileResultDto.getSfilename()+"]处理中...,请稍候！", 10);
					
					try {
						uploadUIForZjService.UploadDate(fileResultDto);
					} catch (Throwable e) {
						if(null != e.getCause() && null != e.getCause().getMessage()){
							errorinfo = "导入文件[" + fileResultDto.getSfilename()+"]处理时出现异常。\r\n" + e.getCause().getMessage();
						}else{
							errorinfo = "导入文件[" + fileResultDto.getSfilename()+"]处理时出现异常。\r\n" + e.getMessage();
						}
						log.error(errorinfo,e);
						flag = true;
						err = new Exception(errorinfo,e);;
						return ;
					}
					
					monitor.worked(1);
					monitor.done();
				}
			};

			
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
			try {
				dialog.run(false, false, runnable);
			} catch (InterruptedException e1) {
				errorinfo = "导入文件[" + fileResultDto.getSfilename()+"]处理时出现异常";
				log.error(errorinfo,e1);
				flag = true;
				err = new Exception(errorinfo,e1);
			} catch (InvocationTargetException e1) {
				errorinfo = "导入文件[" + fileResultDto.getSfilename()+"]处理时出现异常";
				log.error(errorinfo,e1);
				flag = true;
				err = new Exception(errorinfo,e1);
			} 
			
			if(flag){
				if(null != err.getCause() && null != err.getCause().getMessage()){
					Exception errinfoEx = null;
					if(null != errorinfo){
						errinfoEx = new Exception(errorinfo,err);
					}else{
						errinfoEx = new Exception(err.getCause().getMessage(),err);
					}
					
					MessageDialog.openErrorDialog(null, errinfoEx);
				}else{
					MessageDialog.openErrorDialog(null, err);
				}
			
				flag = false;
			}else{
				MessageDialog.openMessageDialog(null, "文件[" + fName +"]加载成功!");
			}
		}
		
		
		return super.upload(o);
	}

	/**
	 * Direction: 收入确认提交 ename: commitincome 引用方法: viewers: * messages:
	 */
	public String commitincome(Object o) {
		if (null == affirmsrlnodto.getStrasrlno() || "".equals(affirmsrlnodto.getStrasrlno().trim())
				|| affirmsrlnodto.getStrasrlno().trim().length() != 18) {
			MessageDialog.openMessageDialog(null, " 资金收纳流水号必须填写，且必须为18位！");
			return "";
		}
		
		if(null == checkFileList || checkFileList.size() == 0){
			MessageDialog.openMessageDialog(null, " 请选择要确认提交的数据！");
			return "";
		}
		
		// 校验金额与文件名称总金额一致
		BigDecimal sumamt = new BigDecimal("0.00");
		for(int i = 0 ; i< checkFileList.size(); i++){
			sumamt = sumamt.add(checkFileList.get(i).getNmoney());
		}
		
		if(null == affirmsrlnodto.getNmoney()){
			MessageDialog.openMessageDialog(null, " 资金收纳流水总金额必须填写！");
			return "";
		}
		
		if(sumamt.compareTo(affirmsrlnodto.getNmoney()) != 0){
			MessageDialog.openMessageDialog(null, " 选择要确认提交的数据的总金额[" + sumamt +"]与资金收纳流水总金额不匹配！");
			return "";
		}
		
		List<TvInfileDto> tmplist = new ArrayList<TvInfileDto>(); 
		for(int i = 0 ; i< checkFileList.size(); i++){
			tmplist.clear();
			tmplist.add(checkFileList.get(i));
			try {
				uploadUIForZjService.commitByTraSrlNo(affirmsrlnodto.getStrasrlno().trim(), tmplist);
				MessageDialog.openMessageDialog(null, "文件名称为[" + checkFileList.get(i).getSfilename() +"]的数据提交处理成功！");
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "文件名称为[" + checkFileList.get(i).getSfilename() +"]的数据提交处理失败！");
				MessageDialog.openErrorDialog(null, e);
			}
		}

		return super.goback(o);
	}

	/**
	 * Direction: 删除错误数据 ename: delErrorData 引用方法: viewers: * messages:
	 */
	public String delErrorData(Object o) {
		if (null == affirmsrlnodto.getStrasrlno() || "".equals(affirmsrlnodto.getStrasrlno().trim())
				|| affirmsrlnodto.getStrasrlno().trim().length() != 18) {
			MessageDialog.openMessageDialog(null, " 资金收纳流水号必须填写，且必须为18位！");
			return "";
		}
		TvInfileDto findto = new TvInfileDto();
		findto.setStrasrlno(affirmsrlnodto.getStrasrlno().trim());
		findto.setSorgcode(loginfo.getSorgcode());
		try {
			List<TvInfileDto> resultList = commonDataAccessService.findRsByDto(findto);
			if(resultList==null||resultList.size()<=0)
			{
				MessageDialog.openMessageDialog(null,"资金收纳流水号为["+affirmsrlnodto.getStrasrlno().trim() + "]的数据为空！");
				return super.delErrorData(o);
			}
		} catch (ITFEBizException e1) {
			MessageDialog.openErrorDialog(null, e1);
			return super.delErrorData(o);
		}
		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "删除数据确认", "是否确认要删除这批数据！\n 资金收纳流水号["
				+ affirmsrlnodto.getStrasrlno().trim() + "]");
		if (flag) {
			boolean flag1 = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "删除数据再次确认", "是否确认要删除这批数据！\n 资金收纳流水号["
					+ affirmsrlnodto.getStrasrlno().trim() + "]");
			if (flag1) {
				try {
					uploadUIForZjService.delByTraSrlNo(affirmsrlnodto.getStrasrlno().trim());
					MessageDialog.openMessageDialog(null, " 数据删除处理成功！");
				} catch (Exception e) {
					MessageDialog.openErrorDialog(null, e);
					return super.delErrorData(o);
				}
			}
		}else{
			return "";
		}

		return super.goback(o);
	}

	/**
	 * Direction: 返回到数据加载窗口 ename: goback 引用方法: viewers: 数据加载界面 messages:
	 */
	public String goback(Object o) {
		return super.goback(o);
	}

	/**
	 * Direction: 跳转收入确认提交 ename: gotocommitincome 引用方法: viewers: 收入确认提交
	 * messages:
	 */
	public String gotocommitincome(Object o) {
		affirmsrlnodto = new TvInfileDto();
		affirmList = new ArrayList<TvInfileDto>();
		checkFileList  = new ArrayList<TvInfileDto>();

		editor.fireModelChanged();
		
		return super.gotocommitincome(o);
	}

	/**
	 * Direction: 查询文件列表 ename: searchFileListBySrlno 引用方法: viewers: * messages:
	 */
	public String searchFileListBySrlno(Object o) {
		if(null == affirmsrlnodto.getStrasrlno() || "".equals(affirmsrlnodto.getStrasrlno().trim())
				|| affirmsrlnodto.getStrasrlno().trim().length() != 18){
			MessageDialog.openMessageDialog(null, " 资金收纳流水号必须填写，且必须为18位！");
			return super.searchFileListBySrlno(o);
		}
		
		try {
			affirmList = uploadUIForZjService.searchByTraSrlNo(affirmsrlnodto.getStrasrlno().trim());
			if(null == affirmList || affirmList.size() == 0){
				MessageDialog.openMessageDialog(null, "没有找到符合条件的记录!");
			}
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return super.searchFileListBySrlno(o);
		}

		editor.fireModelChanged();
		return super.searchFileListBySrlno(o);
	}

	/**
	 * Direction: 关闭窗口 ename: close 引用方法: viewers: * messages:
	 */
	public String close(Object o) {
		// MessageDialog.openUnkownProgressDialog(null, "确认退出系统吗？", );
		
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public FileResultDto getFileResultDto() {
		return fileResultDto;
	}

	public void setFileResultDto(FileResultDto fileResultDto) {
		this.fileResultDto = fileResultDto;
	}

	public List<TvInfileDto> getAffirmList() {
		return affirmList;
	}

	public void setAffirmList(List<TvInfileDto> affirmList) {
		this.affirmList = affirmList;
	}

	public TvInfileDto getInputsrlnodto() {
		return inputsrlnodto;
	}

	public void setInputsrlnodto(TvInfileDto inputsrlnodto) {
		this.inputsrlnodto = inputsrlnodto;
	}

	public TvInfileDto getAffirmsrlnodto() {
		return affirmsrlnodto;
	}

	public void setAffirmsrlnodto(TvInfileDto affirmsrlnodto) {
		this.affirmsrlnodto = affirmsrlnodto;
	}

	public List<TvInfileDto> getCheckFileList() {
		return checkFileList;
	}

	public void setCheckFileList(List<TvInfileDto> checkFileList) {
		this.checkFileList = checkFileList;
	}

}