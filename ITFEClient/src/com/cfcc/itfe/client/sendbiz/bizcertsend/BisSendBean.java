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
 * 子系统: SendBiz
 * 模块:BizCertSend
 * 组件:BisSend
 */
public class BisSendBean extends AbstractBisSendBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(BisSendBean.class);
    //显示报表所需的参数
    private Map<String, Object> params;
    //显示报表所需的明细信息
    private Collection<Object> details;
    //报表显示所需的模版
    private TsOperationmodelDto model;
    //显示电子印鉴的报表所需的参数
    private List<Object> stampParam;
    //用户盖章权限
    private Map<String, UserStampFuncDto> userStampFunc;
    //Xml文件的内容
    private String content;
    //报表显示比例
    private String percent;
    //报表配置文件路径
	private String reportIndex;
	//登陆人员所属机构名称
	private String userOrgName;
	//当前服务器日期yyyy-mm-dd格式
	private String sendDate;
	//用户登录信息
	private ITFELoginInfo loginInfo;
	//全部的接收机构的集合
	private List<TsOrganDto> allRecvOrgs;
	//用户所选择的接收机构集合
	private List<TsOrganDto> recvOrgs;
	//用户所选择的接收机构的名称字符串，显示使用
	private String recvOrgNames;
    
    
    public BisSendBean() {
      super();
      try{
    	  //获得用户信息
    	  loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
          userOrgName = loginInfo.getSorgName();
          //获得系统日期
          sendDate = commonDataAccessService.getSysDBDate();
          //所有接收机构的列表
          allRecvOrgs = bizDataUploadService.getAllConnOrgs();
         //接收机构默认为国库局
          recvOrgs = new ArrayList<TsOrganDto>();
          TsOrganDto organ = bizDataUploadService.getDefaultConnOrgs();
          if (organ != null){
        	  recvOrgs.add(organ);
        	  setRecvOrgs(recvOrgs);
          }
          //获得公共证书的内容
          String cert = bizDataUploadService.getCertContent();
          if ((cert == null) || (cert == "")){
        	  throw new ITFEBizException("公共证书没有设置。");
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
        		MessageDialog.openMessageDialog(null, "请选择接收机构。");
        		return -1;
        	}
        	if ((filePath == null) || (filePath.length() == 0)){
        		MessageDialog.openMessageDialog(null, "请选择要上传的业务凭证。");
        		return -1;
        	}
    		//上载文件
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
     * 显示报表的处理方法
     * @param filePath 业务凭证的全路径
     * @param fileName 业务凭证文件名称
     * @return <0处理过程中出现错误，1-清算文件（需要用电子印鉴显示的），9-其他的文件（不需要用电子印鉴显示，用IReport显示）
     */
    public int showReport(String filePath,String fileName){
    	int ret = 0;
    	try{
    		//确定文件类型，文件类型就是文件名的前两位
    		String fileType = fileName.substring(0,2).toUpperCase();
    		TsOperationtypeDto operationType = stampDataAccessService.getOperationTypeByFileType(fileType);
    		if (operationType == null){
    			MessageDialog.openMessageDialog(null, "文件" + fileName + "不是本系统能处理的文件，请重新选择。");
    			return -1;
    		}
    		//获得文件类型对应的模版信息
    		model = stampDataAccessService.getModelByOperationType(operationType.getSoperationtypecode());
    		if (model == null){
    			MessageDialog.openMessageDialog(null, "找不到文件" + fileName + "处理方式，请选择其他类型的文件。");
    			return -1;
    		}
        	//读取Xml文件中的所有内容
        	content = FileOperFacade.readFile(filePath);
        	if (fileType.equals("QS")){
        		//清算信息处理
        		//获得用户盖章权限
        		userStampFunc = (Map<String, UserStampFuncDto>)stampDataAccessService.getUserStampFuction(model.getSmodelid());
        		//获得印鉴显示所需的其他参数
        		stampParam = stampDataAccessService.getStampInfoByModel(model);
        		if (stampParam.size() != 3){
        			MessageDialog.openMessageDialog(null, "文件" + fileName + "的处理参数配置有误，请联系管理员。");
        			return -1;
           		}
        		percent = operationType.getSshowscale().toString();
        		ret = 1;
        	}else if (fileType.equals("TK")){
        		//退款信息处理
        		XmlTkFile file = new XmlTkFile();
        		XmlFileParser.parseXml(content, file);
        		//报表的参数
        		params = new HashMap<String, Object>();
        		params.put("sendName",  file.getSendName());
        		params.put("payDesc",   file.getPayDesc());
        		params.put("vouDate",   file.getVouDate().substring(0,4) + "年" + file.getVouDate().substring(4,6) + "月" + file.getVouDate().substring(6) + "日");
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
        		//报表的明细数据
        		details = new ArrayList<Object>();
        		details.addAll(file.getDetails());
        		BigDecimal total = new BigDecimal(0.0);
        		//计算明细信息的总笔数和总金额
        		for (XmlFileBaseObject one : file.getDetails()){
        			XmlTkDetail detail = (XmlTkDetail)one;
        			total = total.add(new BigDecimal(detail.getMoney()));
        		}
        		params.put("sumCount", new Integer(details.size()));
        		params.put("mxMoney",  total);
        		reportIndex = model.getSmodelsavepath();
        		ret = 9;
        	}else if (fileType.equals("DZ")){
        		//对帐信息处理
        		XmlDzFile file = new XmlDzFile();
        		XmlFileParser.parseXml(content, file);
        		//报表的参数
        		params = new HashMap<String, Object>();
        		params.put("sendName",  file.getSendName());
        		params.put("payDesc",   file.getPayDesc());
        		params.put("month",     file.getMonth());
        		//报表的明细数据
        		details = new ArrayList<Object>();
        		details.addAll(file.getDetails());
        		params.put("sumCount", new Integer(details.size()));
        		reportIndex = model.getSmodelsavepath();
        		ret = 9;
        	}else{
        		MessageDialog.openMessageDialog(null, "文件" + fileName + "不是系统中要处理的类型，请重新选择。");
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
     * 检查待发送的文件是否已经发送
     * @param fileName
     */
    public int isFileExists(String fileName){
    	try{
        	if (bizDataUploadService.isFileExists(fileName)){
        		return 1;//已经存在
        	}else{
        		return 0;//不存在
        	}
    	}catch(Throwable e){
    		log.error(e);
    		MessageDialog.openErrorDialog(null, e);
    		return -1;//出现错误
    	}
    }
    
    /**
     * 查询凭证编号
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
    		return -99;//出现错误
    	}
    }
    
    /**
     * 生成凭证编号
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
    		return -99;//出现错误
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