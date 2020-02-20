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
 * ��ϵͳ: RecBiz
 * ģ��:UploadModuleForZj
 * ���:UploadUIForZj
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
	boolean flag = false; // �ϴ��Ĵ����־ 
	Exception err = null; // �����ϴ��Ĵ����쳣
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
	 * Direction: ���ݼ����ύ ename: upload ���÷���: viewers: * messages:
	 */
	public String upload(Object o) {
		
		// ��ȡ�����ļ��ľ���·�������� 
		if(null == fileList || fileList.size() == 0){
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
			return super.upload(o);
		}
		
		for(int i = 0 ; i < fileList.size(); i++){
			File f = (File) this.fileList.get(i);		//�ļ�����
			String fName = f.getName().toLowerCase();   // �ļ�����
			String fPath = f.getAbsolutePath(); // �ļ�·��
			
			if(null == f || null == fName || null == fPath){
				MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
				return super.upload(o);
			}
			
			try {
				VerifyFileName.verifyIncomeFile(fName);
				IFileParser fileparse = new FileParserImpl();
				fileResultDto = fileparse.dealFile(fPath, fName, loginfo.getSorgcode(), sequenceHelperService);
				
				if (BizTypeConstant.BIZ_TYPE_INCOME.equals(fileResultDto.getSbiztype())) {
					// ����ҵ��ҪУ���ʽ�������ˮ���Ƿ���д
					if(null == inputsrlnodto.getStrasrlno() || "".equals(inputsrlnodto.getStrasrlno().trim())
							|| inputsrlnodto.getStrasrlno().trim().length() != 18){
						MessageDialog.openMessageDialog(null, "����ҵ���ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
						return super.upload(o);
					}
					
					fileResultDto.setIsError(false);
					fileResultDto.setStrasrlno(inputsrlnodto.getStrasrlno().trim());
				}else{
					if(null == inputsrlnodto.getNmoney()){
						MessageDialog.openMessageDialog(null, "֧����ҵ���ļ��ܽ�������д��");
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
					// ��˰�ӿڸ�ʽ����
					String sUploadFilePath = ClientFileTransferUtil.uploadFile(f.getAbsolutePath());
					fileResultDto.setSfilename(fName); // �ļ�����
					fileResultDto.setSmaininfo(sUploadFilePath);
//					MessageDialog.openMessageDialog(null, "�ļ�[" + fName + "]���سɹ����������ļ�·��" + sUploadFilePath );
				}
			} catch (FileTransferException e) {
				MessageDialog.openErrorDialog(null, e);
				return super.upload(o);
			}
			
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("���ڶ�[" + fileResultDto.getSfilename()+"]������...,���Ժ�", 10);
					
					try {
						uploadUIForZjService.UploadDate(fileResultDto);
					} catch (Throwable e) {
						if(null != e.getCause() && null != e.getCause().getMessage()){
							errorinfo = "�����ļ�[" + fileResultDto.getSfilename()+"]����ʱ�����쳣��\r\n" + e.getCause().getMessage();
						}else{
							errorinfo = "�����ļ�[" + fileResultDto.getSfilename()+"]����ʱ�����쳣��\r\n" + e.getMessage();
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
				errorinfo = "�����ļ�[" + fileResultDto.getSfilename()+"]����ʱ�����쳣";
				log.error(errorinfo,e1);
				flag = true;
				err = new Exception(errorinfo,e1);
			} catch (InvocationTargetException e1) {
				errorinfo = "�����ļ�[" + fileResultDto.getSfilename()+"]����ʱ�����쳣";
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
				MessageDialog.openMessageDialog(null, "�ļ�[" + fName +"]���سɹ�!");
			}
		}
		
		
		return super.upload(o);
	}

	/**
	 * Direction: ����ȷ���ύ ename: commitincome ���÷���: viewers: * messages:
	 */
	public String commitincome(Object o) {
		if (null == affirmsrlnodto.getStrasrlno() || "".equals(affirmsrlnodto.getStrasrlno().trim())
				|| affirmsrlnodto.getStrasrlno().trim().length() != 18) {
			MessageDialog.openMessageDialog(null, " �ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
			return "";
		}
		
		if(null == checkFileList || checkFileList.size() == 0){
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫȷ���ύ�����ݣ�");
			return "";
		}
		
		// У�������ļ������ܽ��һ��
		BigDecimal sumamt = new BigDecimal("0.00");
		for(int i = 0 ; i< checkFileList.size(); i++){
			sumamt = sumamt.add(checkFileList.get(i).getNmoney());
		}
		
		if(null == affirmsrlnodto.getNmoney()){
			MessageDialog.openMessageDialog(null, " �ʽ�������ˮ�ܽ�������д��");
			return "";
		}
		
		if(sumamt.compareTo(affirmsrlnodto.getNmoney()) != 0){
			MessageDialog.openMessageDialog(null, " ѡ��Ҫȷ���ύ�����ݵ��ܽ��[" + sumamt +"]���ʽ�������ˮ�ܽ�ƥ�䣡");
			return "";
		}
		
		List<TvInfileDto> tmplist = new ArrayList<TvInfileDto>(); 
		for(int i = 0 ; i< checkFileList.size(); i++){
			tmplist.clear();
			tmplist.add(checkFileList.get(i));
			try {
				uploadUIForZjService.commitByTraSrlNo(affirmsrlnodto.getStrasrlno().trim(), tmplist);
				MessageDialog.openMessageDialog(null, "�ļ�����Ϊ[" + checkFileList.get(i).getSfilename() +"]�������ύ����ɹ���");
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "�ļ�����Ϊ[" + checkFileList.get(i).getSfilename() +"]�������ύ����ʧ�ܣ�");
				MessageDialog.openErrorDialog(null, e);
			}
		}

		return super.goback(o);
	}

	/**
	 * Direction: ɾ���������� ename: delErrorData ���÷���: viewers: * messages:
	 */
	public String delErrorData(Object o) {
		if (null == affirmsrlnodto.getStrasrlno() || "".equals(affirmsrlnodto.getStrasrlno().trim())
				|| affirmsrlnodto.getStrasrlno().trim().length() != 18) {
			MessageDialog.openMessageDialog(null, " �ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
			return "";
		}
		TvInfileDto findto = new TvInfileDto();
		findto.setStrasrlno(affirmsrlnodto.getStrasrlno().trim());
		findto.setSorgcode(loginfo.getSorgcode());
		try {
			List<TvInfileDto> resultList = commonDataAccessService.findRsByDto(findto);
			if(resultList==null||resultList.size()<=0)
			{
				MessageDialog.openMessageDialog(null,"�ʽ�������ˮ��Ϊ["+affirmsrlnodto.getStrasrlno().trim() + "]������Ϊ�գ�");
				return super.delErrorData(o);
			}
		} catch (ITFEBizException e1) {
			MessageDialog.openErrorDialog(null, e1);
			return super.delErrorData(o);
		}
		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "ɾ������ȷ��", "�Ƿ�ȷ��Ҫɾ���������ݣ�\n �ʽ�������ˮ��["
				+ affirmsrlnodto.getStrasrlno().trim() + "]");
		if (flag) {
			boolean flag1 = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "ɾ�������ٴ�ȷ��", "�Ƿ�ȷ��Ҫɾ���������ݣ�\n �ʽ�������ˮ��["
					+ affirmsrlnodto.getStrasrlno().trim() + "]");
			if (flag1) {
				try {
					uploadUIForZjService.delByTraSrlNo(affirmsrlnodto.getStrasrlno().trim());
					MessageDialog.openMessageDialog(null, " ����ɾ������ɹ���");
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
	 * Direction: ���ص����ݼ��ش��� ename: goback ���÷���: viewers: ���ݼ��ؽ��� messages:
	 */
	public String goback(Object o) {
		return super.goback(o);
	}

	/**
	 * Direction: ��ת����ȷ���ύ ename: gotocommitincome ���÷���: viewers: ����ȷ���ύ
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
	 * Direction: ��ѯ�ļ��б� ename: searchFileListBySrlno ���÷���: viewers: * messages:
	 */
	public String searchFileListBySrlno(Object o) {
		if(null == affirmsrlnodto.getStrasrlno() || "".equals(affirmsrlnodto.getStrasrlno().trim())
				|| affirmsrlnodto.getStrasrlno().trim().length() != 18){
			MessageDialog.openMessageDialog(null, " �ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
			return super.searchFileListBySrlno(o);
		}
		
		try {
			affirmList = uploadUIForZjService.searchByTraSrlNo(affirmsrlnodto.getStrasrlno().trim());
			if(null == affirmList || affirmList.size() == 0){
				MessageDialog.openMessageDialog(null, "û���ҵ����������ļ�¼!");
			}
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return super.searchFileListBySrlno(o);
		}

		editor.fireModelChanged();
		return super.searchFileListBySrlno(o);
	}

	/**
	 * Direction: �رմ��� ename: close ���÷���: viewers: * messages:
	 */
	public String close(Object o) {
		// MessageDialog.openUnkownProgressDialog(null, "ȷ���˳�ϵͳ��", );
		
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