package com.cfcc.itfe.client.recbiz.dealerrorincome;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 10-01-31 20:39:37 ��ϵͳ: RecBiz ģ��:dealErrorIncome ���:DealErrorIncome
 */
public class DealErrorIncomeBean extends AbstractDealErrorIncomeBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(DealErrorIncomeBean.class);
	
	private ITFELoginInfo loginfo ;
	
	private List fileList = null;
	private TvInfileDto inputsrlnodto;
	private FileResultDto fileResultDto;
	private String errorinfo ;
	boolean flag = false; // �ϴ��Ĵ����־ 
	Exception err = null; // �����ϴ��Ĵ����쳣

	public DealErrorIncomeBean() {
		super();
		errorinfo = null;
		fileList = new ArrayList();
		inputsrlnodto = new TvInfileDto();
		fileResultDto = new FileResultDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();

	}

	/**
	 * Direction: ���ݼ��ع��� ename: upload ���÷���: viewers: * messages:
	 */
	public String upload(Object o) {
		// ��ȡ�����ļ��ľ���·�������� 
		if(null == fileList || fileList.size() == 0){
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
			return super.upload(o);
		}
		
		File f = (File) this.fileList.get(0);		//�ļ�����
		String fName = f.getName().toLowerCase();   // �ļ�����
		String fPath = f.getAbsolutePath(); // �ļ�·��
		
		if(null == f || null == fName || null == fPath){
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
			return super.upload(o);
		}
		
		try {
			//TODO ��Ҫ�����ļ����Ƶ�У��
			VerifyFileName.verifyIncomeFile(fName);
			IFileParser fileparse = new FileParserImpl();
			fileResultDto = fileparse.dealFile(fPath, fName, loginfo.getSorgcode(), null);
			
			if (BizTypeConstant.BIZ_TYPE_INCOME.equals(fileResultDto.getSbiztype())) {
				// ����ҵ��ҪУ���ʽ�������ˮ���Ƿ���д
				if(null == inputsrlnodto.getStrasrlno() || "".equals(inputsrlnodto.getStrasrlno().trim())
						|| inputsrlnodto.getStrasrlno().trim().length() != 18){
					MessageDialog.openMessageDialog(null, " �ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
					return super.upload(o);
				}
				
				if(null == inputsrlnodto.getNmoney()){
					MessageDialog.openMessageDialog(null, " �ļ���Ӧ���ܽ�������д��");
					return super.upload(o);
				}
				
				fileResultDto.setIsError(true);
				fileResultDto.setFsumamt(inputsrlnodto.getNmoney());
				fileResultDto.setStrasrlno(inputsrlnodto.getStrasrlno().trim());
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return super.upload(o);
		}
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				monitor.beginTask("���ڴ�����...,���Ժ�", 10);
				
				try {
					uploadUIService.UploadDate(fileResultDto);
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
		} catch (Throwable e1) {
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
				if(errinfoEx.toString().contains("HttpInvokeServletException")||errinfoEx.toString().contains("HttpInvokerException")){
					MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧ��\r\n�����µ�¼��");
				}else{
					MessageDialog.openErrorDialog(null, errinfoEx);
				}
			}else{
				if(err.toString().contains("HttpInvokeServletException")||err.toString().contains("HttpInvokerException")){
					MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧ��\r\n�����µ�¼��");
				}else{
					MessageDialog.openErrorDialog(null, err);
				}
			}
		
			flag = false;
		}else{
			MessageDialog.openMessageDialog(null, "�ļ�[" + fName +"]���سɹ�!");
		}
		
		return super.upload(o);
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

	public List getFileList() {
		return fileList;
	}

	public void setFileList(List fileList) {
		this.fileList = fileList;
	}

	public TvInfileDto getInputsrlnodto() {
		return inputsrlnodto;
	}

	public void setInputsrlnodto(TvInfileDto inputsrlnodto) {
		this.inputsrlnodto = inputsrlnodto;
	}

}