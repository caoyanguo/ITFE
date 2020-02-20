package com.cfcc.itfe.client.sendbiz.exporttbsforbj;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.sendbiz.exporttbsfiletxt.IExportTBSfiletxtService;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IExportTBSForBJService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ����TBS��ʽ�ļ�������
 * @author hua
 * @time  14-09-10 10:08:23
 */
@SuppressWarnings("unchecked")
public class BaseTBSFileExportUtil implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**�������ݷ��ʷ���**/
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
	
	/**�����ļ������ݷ���**/
	protected IExportTBSForBJService exportTBSForBJService = (IExportTBSForBJService)ServiceFactory.getService(IExportTBSForBJService.class);
	
	/**���ñ��˵����ݷ���**/
	protected IExportTBSfiletxtService exportTBSfiletxtService = (IExportTBSfiletxtService)ServiceFactory.getService(IExportTBSfiletxtService.class);
	
	/**����Ļ���**/
	private static final String BASE_PROCESSER_NAME= "com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer.Process";
	
	/**TBSҵ������**/
	private String bizType ;
	
	/**��������**/
	private Date acctDate ;
	
	/**�����ڱ�־**/
	private String trimFlag;
	
	public BaseTBSFileExportUtil() {
		super();
	}

	/**
	 * ��ʼ��������(����ҵ������) 
	 * @param mainList
	 * @param baseDir
	 */
	public String expdata(List<IDto> mainList, String baseDir){
		try {
			/** 1. ��ʼ�������ļ�������� */
			List<IDto> logList = new ArrayList<IDto>();
			List<List<IDto>> allList = new ArrayList<List<IDto>>(); //allList�������ÿһ��List���൱����һ����Ҫ�������ļ�
			List<TbsFileInfo> fileInfoList = new ArrayList<TbsFileInfo>();
			String fileName = "";
			String finalSaveDir = generateSaveDir(baseDir);
			
			/** 2.����ҵ�����ͻ����Ӧ�Ĵ��������� */
			IProcessHandler processer = (IProcessHandler) Class.forName(BASE_PROCESSER_NAME+bizType).newInstance();
			
			/** 3.���ڶ��һ���ļ�ֻ�ܷ���һ��ƾ֤,������Ҫ�������⴦�� */
			if(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)||BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {
				for(IDto dto : mainList) {
					List<IDto> singleList = new ArrayList<IDto>();
					singleList.add(dto);
					allList.add(singleList);
				}
			} else {
				allList.add(mainList);
			}
			
			/** 4.���������� */
			for(List<IDto> tempList : allList) {
				String simpleFileName = generateFileName();
				fileName = finalSaveDir + File.separator + simpleFileName;
				TbsFileInfo fileInfo = processer.process(tempList, fileName);
				if(fileInfo!=null&&!"".equals(fileInfo.getFileContent())){
					
					/** 4.1 ����ļ��Ѵ�������ɾ��,����ֻ����ȷ�Ͽ��Ե�������ʱ�Ž���ɾ��,��ֹ��ɾ�� **/
					File file = new File(fileName);
					if(file.exists()) {
						file.delete();
					}
					FileUtil.getInstance().writeFile(fileName, fileInfo.getFileContent());
					
					/** 4.2 �ļ�д�����غ�,�������Ϣ���뵽��Ϣ�б��� */
					fileInfo.setFileName(simpleFileName);
					fileInfo.setFullFileName(fileName);
					fileInfoList.add(fileInfo);
					logList.add(generateLogDto(fileInfo,bizType));
				}
			}
			
			/** 5.����ͳ����Ϣ�ļ������� */
			String expInfo = syncGenerateExpInfoCSV(fileInfoList,finalSaveDir);
			if(StringUtils.isNotBlank(expInfo)) {
				FileUtil.getInstance().writeFile(finalSaveDir+File.separator+expInfo.split("#")[1],expInfo.split("#")[0]);
			}
			
			/** 6.��¼��־���������� **/
			exportTBSForBJService.writeTbsFileLog(logList);
			fileName = "";
			release(allList,fileInfoList,logList);//GC�������
			return finalSaveDir;
		} catch (Exception e) {
			throw new RuntimeException("�������ݳ����쳣",e);
		}
	}

	/**ͬ��������ص�ͳ����Ϣ:TXT��ʽ,�����ڼ��±���UE���ı�������ʾ��ʽ��һ��,�ĳɵ���CSV�ļ�*/
	@SuppressWarnings("unused")
	private String syncGenerateExpInfoTXT(List<TbsFileInfo> fileInfoList, String finalSaveDir) {
		String title = "�ļ���\t\t\t\t\t\t\t\t����\t\t���\r\n";
		StringBuffer sb = new StringBuffer(title);
		int finalCount = 0;
		BigDecimal finalFamt = new BigDecimal("0.00");
		String expInfoFileName = "";
		for(int i =0; isNotBlankList(fileInfoList)&&i<fileInfoList.size(); i++){
			TbsFileInfo info = fileInfoList.get(i);
			finalCount += info.getTotalCount();
			finalFamt = finalFamt.add(info.getTotalFamt());
			sb.append(info.getFileName()+"\t\t"+info.getTotalCount()+"\t\t\t\t"+info.getTotalFamt()+"\r\n");
			if(i ==0) {
				expInfoFileName = info.getFileName().substring(0,8);
			}
		}
		if(title.equalsIgnoreCase(sb.toString())){
			return "";
		}else {
			sb.append("\t\t\t\t\t\t\t\t\t\t\t----\t\t-------\r\n");
			sb.append("�ܼ�:\t\t\t\t\t\t\t\t\t"+finalCount+"\t\t\t\t"+finalFamt);
			return sb.toString()+"#"+expInfoFileName+"�ļ��嵥��Ϣ("+finalSaveDir.substring(finalSaveDir.lastIndexOf(File.separator)+1)+").txt";
		}
	}

	/**ͬ��������ص�ͳ����Ϣ:CSV��ʽ*/
	private String syncGenerateExpInfoCSV(List<TbsFileInfo> fileInfoList, String finalSaveDir) {
		String split = ",";
		String title = "�ļ���"+split+"����"+split+"���\r\n";
		StringBuffer sb = new StringBuffer(title);
		int finalCount = 0;
		BigDecimal finalFamt = new BigDecimal("0.00");
		String expInfoFileName = "";
		for(int i =0; isNotBlankList(fileInfoList)&&i<fileInfoList.size(); i++){
			TbsFileInfo info = fileInfoList.get(i);
			finalCount += info.getTotalCount();
			finalFamt = finalFamt.add(info.getTotalFamt());
			sb.append(info.getFileName()+","+info.getTotalCount()+","+info.getTotalFamt()+"\r\n");
			if(i ==0) {
				expInfoFileName = info.getFileName().substring(0,8);
			}
		}
		if(title.equalsIgnoreCase(sb.toString())){
			return "";
		}else {
			sb.append("�ܼ�,"+finalCount+","+finalFamt);
			return sb.toString()+"#"+expInfoFileName+"�ļ��嵥��Ϣ("+finalSaveDir.substring(finalSaveDir.lastIndexOf(File.separator)+1)+").CSV";
		}
	}
	
	private boolean isNotBlankList(List list) {
		return (list!=null)&&(list.size()>0);
	}
	
	private void release(List list,List list1,List list2){
		list = null;
		list1 = null;
		list2 = null;
	}
	
	/**����Ҫ����ĵ����ļ���־*/
	private TvSendlogDto generateLogDto(TbsFileInfo fileInfo, String bizType2){
		TvSendlogDto sendlogDto = new TvSendlogDto();
		sendlogDto.setStitle("�ͻ���·����"+fileInfo.getFullFileName());
		sendlogDto.setSoperationtypecode(bizType2);
		sendlogDto.setStrecode(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
		sendlogDto.setIcount(fileInfo.getTotalCount());
		sendlogDto.setNmoney(fileInfo.getTotalFamt());
		return sendlogDto;
	}
	
	/**
	 * ���ݽ������������ʾ��Ϣ(����TBS��ִ�ļ�)
	 * @param resultDto
	 * @param serverFileList
	 */
    public String generateMessage(MulitTableDto resultDto,List<String> serverFileList){
    	List<String> errorInfoList = resultDto.getErrorList(); //������Ϣ�б�
		List<String> errorFileList = resultDto.getErrNameList(); //�����ļ��б�
		if(errorFileList==null || errorFileList.size()==0) {
			/**��������ļ����б�Ϊ��,��˵��ȫ������ɹ�**/
			return "�ļ����سɹ�,���ι����سɹ�"+serverFileList.size()+"���ļ�!";
			
		}else {
			/**���������־��Ϣ**/
			String errorLogName = StateConstant.Import_Errinfo_DIR+ "TBS��ִ���������Ϣ("
								+ new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new java.util.Date()) + ").txt";
			StringBuffer errorLog = new StringBuffer(""); //���ȫ���Ĵ�����־��Ϣ
			StringBuffer errorMessage = new StringBuffer(""); //���Ҫ��ʾ���û��Ĵ�����־��Ϣ���������ݽϳ�������ֻ��ʾǰ15��
			for(int i=0; errorInfoList.size()>0 && i<errorInfoList.size(); i++) {
				String errorInfo = errorInfoList.get(i);
				errorLog.append(errorInfo + "\r\n");
				if(i<15) {
					errorMessage.append(errorInfo + "\r\n");
				}
			}
			if (!"".equals(errorLog.toString())) {
				try {
					/**�����10��֮ǰ�Ĵ�����־��Ϣ**/
					FileUtil.getInstance().deleteFileWithDays(StateConstant.Import_Errinfo_DIR,
															  Integer.parseInt(StateConstant.Import_Errinfo_BackDays));
					/**�������Ĵ�����Ϣ���浽����**/
					FileUtil.getInstance().writeFile(errorLogName,errorLog.toString());
				} catch (Exception e) {
					/**���������־�����쳣,���������**/
					e.printStackTrace();//���ڵ���
				}
			}
			
			/**�����ļ��б�Ϊ��,˵�����ڴ����ļ�**/
			StringBuffer sb = new StringBuffer("���μ����ļ���"+serverFileList.size()+"��,����"
									+(serverFileList.size()-errorFileList.size())+"���ɹ�,"
									+errorFileList.size()+"��ʧ��,������Ϣ���¡���ϸ������Ϣ��鿴" + errorLogName + "����\r\n");
			
			sb.append(errorMessage.toString());
			return sb.toString();
		}
    }
	
    /**
     * �����û�ѡ���·���������ձ����Ŀ¼
     * @param baseDir
     */
    public String generateSaveDir(String baseDir){
    	String timeDir = new SimpleDateFormat("HHʱmm��ss��").format(new java.util.Date());
    	if(!baseDir.endsWith("/") && !baseDir.endsWith("\\")) { //�жϴ����·�������Ƿ���б��
    		baseDir += "\\";
		}
    	return baseDir += "���ݵ���Ŀ¼("+new Date(new java.util.Date().getTime()).toString()+")" + File.separator + timeDir;
    }
    
	/**
	 * ����ҵ�����������ļ��� 
	 * �ļ����ƣ�yyyymmddxxxxttm.txt
	 */
	public String generateFileName() {
		try {
			StringBuffer fileName = new StringBuffer();
			/** �ļ���ǰ8λ���ڣ�������������**/
			fileName.append(TimeFacade.formatDate(null==acctDate?TimeFacade.getCurrentDate():acctDate,"yyyyMMdd"));
			/** ���κ�2��4λ�����ļ������κţ������ڹ���ϵͳΨһ��ʶ�����ļ���û�й���ҵ����**/
			fileName.append(exportTBSfiletxtService.getTBSNum());
			/** ҵ������**/
			fileName.append(bizType);
			/** mΪ�����ڱ�־(1λ)0�������ڣ�1��������**/
			fileName.append(null == trimFlag?"0":trimFlag);//Ĭ��������
			/** �����ļ�������·�����ļ���**/
			fileName.append(".txt");
			return fileName.toString();
		} catch (Exception e) {
			throw new RuntimeException("�����ļ���ʱ�����쳣!",e);
		}
	}
	/**
	 * ���ݴ����ƾ֤���룬����һ��8λ���ַ���
	 * ����Ϊ��������ȴ���8���ȡ8λ������ԭ������
	 * @return newVouno
	 */
	public String generateVouno(String orivouno){
		if(orivouno.length()>8) {
			return orivouno.substring(orivouno.length() -8 ,orivouno.length());
		}
		return orivouno;
	}
	
	/**
     * ����Ҫ������ļ�·�����Ͻ���У�飺
     * 1�����Ȳ��ܵ���0
     * 2���ļ�����Ϊtxt��ʽ
     * 3�������ڿɴ����ҵ�����ͷ�Χ֮��
     */
    public String validateImportFile(List<File> fList){
    	StringBuffer sb = new StringBuffer("������ʾ:\n");
    	if(fList==null || fList.size()==0) {
    		sb.append("����ѡ���ļ�!");
    	} else {
    		for(int i=0; i<fList.size(); i++) {
    			File file = fList.get(i);
    			String fileName = file.getName();
    			if(!fileName.toLowerCase().endsWith("txt")) {
    				sb.append(fileName+" �ļ����ͷǷ�!\n");
    				continue;
    			}
    			if(!isInValid(fileName.substring(fileName.length()-8, fileName.lastIndexOf(".")))){
    				sb.append(fileName+" �ļ����ͷǷ�!\n");
    				continue;
    			}
    		}
    	}
		return sb.toString().equals("������ʾ:\n")?"":sb.toString();
    }
    
    /** У���ļ����� */
    private boolean isInValid(String fileType) {
    	boolean ok = false;
    	if(fileType.toUpperCase().equalsIgnoreCase(StateConstant.TBS_FILESLIP_YSZC)) {
    		ok = true;
    	} else if(fileType.toUpperCase().equalsIgnoreCase(StateConstant.TBS_FILESLIP_ZJED)) {
    		ok = true;
    	} else if(fileType.toUpperCase().equalsIgnoreCase(StateConstant.TBS_FILESLIP_SQED)) {
    		ok = true;
    	} else if(fileType.toUpperCase().equalsIgnoreCase(StateConstant.TBS_FILESLIP_ZFQS)) {
    		ok = true;
    	} else if(fileType.toUpperCase().equalsIgnoreCase(StateConstant.TBS_FILESLIP_ZFTH)) {
    		ok = true;
    	} else if(fileType.toUpperCase().equalsIgnoreCase(StateConstant.TBS_FILESLIP_SRTK)) {
    		ok = true;
    	}
		return ok;
	}
	
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public Date getAcctDate() {
		return acctDate;
	}
	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}
	public String getTrimFlag() {
		return trimFlag;
	}
	public void setTrimFlag(String trimFlag) {
		this.trimFlag = trimFlag;
	}
}
