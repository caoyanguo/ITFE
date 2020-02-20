package com.cfcc.itfe.client.sendbiz.bizcertsend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.encrypt.KoalClientPkcs7Encrypt;
import com.cfcc.itfe.client.common.file.FileOperFacade;
import com.cfcc.itfe.client.common.file.XmlFileBaseObject;
import com.cfcc.itfe.client.common.file.XmlFileParser;
import com.cfcc.itfe.client.sendbiz.bizcertsend.model.XmlDzFile;
import com.cfcc.itfe.client.sendbiz.bizcertsend.model.XmlTkDetail;
import com.cfcc.itfe.client.sendbiz.bizcertsend.model.XmlTkFile;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.UserStampFuncDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   09-10-20 13:54:35
 * ��ϵͳ: SendBiz
 * ģ��:BizCertSend
 * ���:BisSend
 */
public class BisSendBean extends AbstractBisSendBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(BisSendBean.class);
    //��ʾ��������Ĳ���
    private Map<String, Object> params;
    //��ʾ�����������ϸ��Ϣ
    private Collection<Object> details;
    //������ʾ�����ģ��
    private TsOperationmodelDto model;
    //��ʾ����ӡ���ı�������Ĳ���
    private List<Object> stampParam;
    //�û�����Ȩ��
    private Map<String, UserStampFuncDto> userStampFunc;
    //Xml�ļ�������
    private String content;
    //������ʾ����
    private String percent;
    //���������ļ�·��
	private String reportIndex;
	//��½��Ա������������
	private String userOrgName;
	//��ǰ����������yyyy-mm-dd��ʽ
	private String sendDate;
	//�û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	//ȫ���Ľ��ջ����ļ���
	private List<TsOrganDto> allRecvOrgs;
	//�û���ѡ��Ľ��ջ�������
	private List<TsOrganDto> recvOrgs;
	//�û���ѡ��Ľ��ջ����������ַ�������ʾʹ��
	private String recvOrgNames;
    
    
    public BisSendBean() {
      super();
      try{
    	  //����û���Ϣ
    	  loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
          userOrgName = loginInfo.getSorgName();
          //���ϵͳ����
          sendDate = commonDataAccessService.getSysDBDate();
          //���н��ջ������б�
          allRecvOrgs = bizDataUploadService.getAllConnOrgs();
         //���ջ���Ĭ��Ϊ�����
          recvOrgs = new ArrayList<TsOrganDto>();
          TsOrganDto organ = bizDataUploadService.getDefaultConnOrgs();
          if (organ != null){
        	  recvOrgs.add(organ);
        	  setRecvOrgs(recvOrgs);
          }
          //��ù���֤�������
          String cert = bizDataUploadService.getCertContent();
          if ((cert == null) || (cert == "")){
        	  throw new ITFEBizException("����֤��û�����á�");
          }
          KoalClientPkcs7Encrypt.getInstance().setStrCerContent(cert);
          if (KoalClientPkcs7Encrypt.getInstance().getLastRetCode() < 0){
        	  throw new ITFEBizException(KoalClientPkcs7Encrypt.getInstance().getLastError());
          }
      }catch(Throwable e){
    	  log.error(e);
    	  MessageDialog.openErrorDialog(null, e);
      }
                  
    }
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
    
    /**
     * 
     * @param filePath
     */
    public int upload(String filePath){
    	try{
        	if ((recvOrgs == null) || (recvOrgs.size() == 0)){
        		MessageDialog.openMessageDialog(null, "��ѡ����ջ�����");
        		return -1;
        	}
        	if ((filePath == null) || (filePath.length() == 0)){
        		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�ϴ���ҵ��ƾ֤��");
        		return -1;
        	}
    		//�����ļ�
        	String fileUploadPath = ClientFileTransferUtil.uploadFile(filePath);
        	bizDataUploadService.upload(model, fileUploadPath, recvOrgs);
    	}catch(Throwable e){
    		try{
    			String errMsg = "";
    			if ((null == e.getCause()) || (null == e.getCause().getMessage())){
    				errMsg = e.getMessage();
    			}else{
    				errMsg = e.getCause().getMessage(); 
    			}
    			errMsg = errMsg.substring(0,(errMsg.length()>198?198:errMsg.length()));
        		bizDataUploadService.addErrorSendLog(model, FileOperFacade.getFileName(filePath), errMsg);
    		}catch(Throwable ex){
    			log.error(ex);
    		}
    		log.error(e);
    		if (null == e.getCause())
    			MessageDialog.openErrorDialog(null, e);
    		else
    			MessageDialog.openErrorDialog(null, e.getCause());
    		return -1;
    	}
    	return 0;
    	
    }
    
    /**
     * ��ʾ����Ĵ�����
     * @param filePath ҵ��ƾ֤��ȫ·��
     * @param fileName ҵ��ƾ֤�ļ�����
     * @return <0��������г��ִ���1-�����ļ�����Ҫ�õ���ӡ����ʾ�ģ���9-�������ļ�������Ҫ�õ���ӡ����ʾ����IReport��ʾ��
     */
    public int showReport(String filePath,String fileName){
    	int ret = 0;
    	try{
    		//ȷ���ļ����ͣ��ļ����;����ļ�����ǰ��λ
    		String fileType = fileName.substring(0,2).toUpperCase();
    		TsOperationtypeDto operationType = stampDataAccessService.getOperationTypeByFileType(fileType);
    		if (operationType == null){
    			MessageDialog.openMessageDialog(null, "�ļ�" + fileName + "���Ǳ�ϵͳ�ܴ�����ļ���������ѡ��");
    			return -1;
    		}
    		//����ļ����Ͷ�Ӧ��ģ����Ϣ
    		model = stampDataAccessService.getModelByOperationType(operationType.getSoperationtypecode());
    		if (model == null){
    			MessageDialog.openMessageDialog(null, "�Ҳ����ļ�" + fileName + "����ʽ����ѡ���������͵��ļ���");
    			return -1;
    		}
        	//��ȡXml�ļ��е���������
        	content = FileOperFacade.readFile(filePath);
        	if (fileType.equals("QS")){
        		//������Ϣ����
        		//����û�����Ȩ��
        		userStampFunc = (Map<String, UserStampFuncDto>)stampDataAccessService.getUserStampFuction(model.getSmodelid());
        		//���ӡ����ʾ�������������
        		stampParam = stampDataAccessService.getStampInfoByModel(model);
        		if (stampParam.size() != 3){
        			MessageDialog.openMessageDialog(null, "�ļ�" + fileName + "�Ĵ������������������ϵ����Ա��");
        			return -1;
           		}
        		percent = operationType.getSshowscale().toString();
        		ret = 1;
        	}else if (fileType.equals("TK")){
        		//�˿���Ϣ����
        		XmlTkFile file = new XmlTkFile();
        		XmlFileParser.parseXml(content, file);
        		//����Ĳ���
        		params = new HashMap<String, Object>();
        		params.put("sendName",  file.getSendName());
        		params.put("payDesc",   file.getPayDesc());
        		params.put("vouDate",   file.getVouDate().substring(0,4) + "��" + file.getVouDate().substring(4,6) + "��" + file.getVouDate().substring(6) + "��");
        		params.put("vouNum",    file.getVouNum());
        		params.put("payorName", file.getPayorName());
        		params.put("payorAcc",  file.getPayorAcc());
        		params.put("payorBank", file.getPayorBank());
        		params.put("payeeName", file.getPayeeName());
        		params.put("payeeAcc",  file.getPayeeAcc());
        		params.put("payeeBank", file.getPayeeBank());
        		params.put("sumMoney",  file.getSumMoney());
        		params.put("summary",   file.getSummary());
        		params.put("remark",    file.getRemark());
        		//�������ϸ����
        		details = new ArrayList<Object>();
        		details.addAll(file.getDetails());
        		BigDecimal total = new BigDecimal(0.0);
        		//������ϸ��Ϣ���ܱ������ܽ��
        		for (XmlFileBaseObject one : file.getDetails()){
        			XmlTkDetail detail = (XmlTkDetail)one;
        			total = total.add(new BigDecimal(detail.getMoney()));
        		}
        		params.put("sumCount", new Integer(details.size()));
        		params.put("mxMoney",  total);
        		reportIndex = model.getSmodelsavepath();
        		ret = 9;
        	}else if (fileType.equals("DZ")){
        		//������Ϣ����
        		XmlDzFile file = new XmlDzFile();
        		XmlFileParser.parseXml(content, file);
        		//����Ĳ���
        		params = new HashMap<String, Object>();
        		params.put("sendName",  file.getSendName());
        		params.put("payDesc",   file.getPayDesc());
        		params.put("month",     file.getMonth());
        		//�������ϸ����
        		details = new ArrayList<Object>();
        		details.addAll(file.getDetails());
        		params.put("sumCount", new Integer(details.size()));
        		reportIndex = model.getSmodelsavepath();
        		ret = 9;
        	}else{
        		MessageDialog.openMessageDialog(null, "�ļ�" + fileName + "����ϵͳ��Ҫ��������ͣ�������ѡ��");
        		ret = -1;
        	}
    	}catch(Throwable e){
    		log.error(e);
    		MessageDialog.openErrorDialog(null, e);
    		ret = -1;
    	}
    	return ret;
    }
    
    /**
     * �������͵��ļ��Ƿ��Ѿ�����
     * @param fileName
     */
    public int isFileExists(String fileName){
    	try{
        	if (bizDataUploadService.isFileExists(fileName)){
        		return 1;//�Ѿ�����
        	}else{
        		return 0;//������
        	}
    	}catch(Throwable e){
    		log.error(e);
    		MessageDialog.openErrorDialog(null, e);
    		return -1;//���ִ���
    	}
    }
    
    /**
     * ��ѯƾ֤���
     * @param vouType
     * @param fileName
     * @return
     */
    public int getVouNo(String vouType,String fileName){
    	try{
        	return bizDataUploadService.getVouNo(vouType, fileName);
    	}catch(Throwable e){
    		log.error(e);
    		MessageDialog.openErrorDialog(null, e);
    		return -99;//���ִ���
    	}
    }
    
    /**
     * ����ƾ֤���
     * @param vouType
     * @param fileName
     * @return
     */
    public int genVouNo(String vouType,String fileName){
    	try{
        	return bizDataUploadService.addVouNo(vouType, fileName);
    	}catch(Throwable e){
    		log.error(e);
    		MessageDialog.openErrorDialog(null, e);
    		return -99;//���ִ���
    	}
    }
    
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public Collection<Object> getDetails() {
		return details;
	}
	public void setDetails(Collection<Object> details) {
		this.details = details;
	}
	public String getReportIndex() {
		return reportIndex;
	}
	public void setReportIndex(String reportIndex) {
		this.reportIndex = reportIndex;
	}
	public String getUserOrgName() {
		return userOrgName;
	}
	public void setUserOrgName(String userOrgName) {
		this.userOrgName = userOrgName;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public List<Object> getStampParam() {
		return stampParam;
	}
	public void setStampParam(List<Object> stampParam) {
		this.stampParam = stampParam;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TsOperationmodelDto getModel() {
		return model;
	}
	public void setModel(TsOperationmodelDto model) {
		this.model = model;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public List<TsOrganDto> getRecvOrgs() {
		return recvOrgs;
	}
	public void setRecvOrgs(List<TsOrganDto> recvOrgs) {
		this.recvOrgs = recvOrgs;
		String name = "";
		for (TsOrganDto one : this.recvOrgs){
			name += (one.getSorgname()==null?"":one.getSorgname()) + ";";
		}
		setRecvOrgNames(name);
	}
	public String getRecvOrgNames() {
		return recvOrgNames;
	}
	public void setRecvOrgNames(String recvOrgNames) {
		this.recvOrgNames = recvOrgNames;
	}
	public List<TsOrganDto> getAllRecvOrgs() {
		return allRecvOrgs;
	}
	public void setAllRecvOrgs(List<TsOrganDto> allRecvOrgs) {
		this.allRecvOrgs = allRecvOrgs;
	}
	public Map<String, UserStampFuncDto> getUserStampFunc() {
		return userStampFunc;
	}
	public void setUserStampFunc(Map<String, UserStampFuncDto> userStampFunc) {
		this.userStampFunc = userStampFunc;
	}

}