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
 * 产生TBS格式文件工具类
 * @author hua
 * @time  14-09-10 10:08:23
 */
@SuppressWarnings("unchecked")
public class BaseTBSFileExportUtil implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**公共数据访问服务**/
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
	
	/**导出文件的数据服务**/
	protected IExportTBSForBJService exportTBSForBJService = (IExportTBSForBJService)ServiceFactory.getService(IExportTBSForBJService.class);
	
	/**重用别人的数据服务**/
	protected IExportTBSfiletxtService exportTBSfiletxtService = (IExportTBSfiletxtService)ServiceFactory.getService(IExportTBSfiletxtService.class);
	
	/**子类的基名**/
	private static final String BASE_PROCESSER_NAME= "com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer.Process";
	
	/**TBS业务类型**/
	private String bizType ;
	
	/**账务日期**/
	private Date acctDate ;
	
	/**调整期标志**/
	private String trimFlag;
	
	public BaseTBSFileExportUtil() {
		super();
	}

	/**
	 * 开始导出数据(根据业务类型) 
	 * @param mainList
	 * @param baseDir
	 */
	public String expdata(List<IDto> mainList, String baseDir){
		try {
			/** 1. 初始化导出文件所需对象 */
			List<IDto> logList = new ArrayList<IDto>();
			List<List<IDto>> allList = new ArrayList<List<IDto>>(); //allList里面放入每一个List就相当于是一个需要导出的文件
			List<TbsFileInfo> fileInfoList = new ArrayList<TbsFileInfo>();
			String fileName = "";
			String finalSaveDir = generateSaveDir(baseDir);
			
			/** 2.根据业务类型获得相应的处理器对象 */
			IProcessHandler processer = (IProcessHandler) Class.forName(BASE_PROCESSER_NAME+bizType).newInstance();
			
			/** 3.由于额度一个文件只能放入一条凭证,所以需要进行特殊处理 */
			if(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)||BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {
				for(IDto dto : mainList) {
					List<IDto> singleList = new ArrayList<IDto>();
					singleList.add(dto);
					allList.add(singleList);
				}
			} else {
				allList.add(mainList);
			}
			
			/** 4.处理导出操作 */
			for(List<IDto> tempList : allList) {
				String simpleFileName = generateFileName();
				fileName = finalSaveDir + File.separator + simpleFileName;
				TbsFileInfo fileInfo = processer.process(tempList, fileName);
				if(fileInfo!=null&&!"".equals(fileInfo.getFileContent())){
					
					/** 4.1 如果文件已存在则先删除,并且只有在确认可以导出数据时才进行删除,防止误删除 **/
					File file = new File(fileName);
					if(file.exists()) {
						file.delete();
					}
					FileUtil.getInstance().writeFile(fileName, fileInfo.getFileContent());
					
					/** 4.2 文件写到本地后,将相关信息放入到信息列表中 */
					fileInfo.setFileName(simpleFileName);
					fileInfo.setFullFileName(fileName);
					fileInfoList.add(fileInfo);
					logList.add(generateLogDto(fileInfo,bizType));
				}
			}
			
			/** 5.生成统计信息文件并保存 */
			String expInfo = syncGenerateExpInfoCSV(fileInfoList,finalSaveDir);
			if(StringUtils.isNotBlank(expInfo)) {
				FileUtil.getInstance().writeFile(finalSaveDir+File.separator+expInfo.split("#")[1],expInfo.split("#")[0]);
			}
			
			/** 6.记录日志和其他操作 **/
			exportTBSForBJService.writeTbsFileLog(logList);
			fileName = "";
			release(allList,fileInfoList,logList);//GC更快回收
			return finalSaveDir;
		} catch (Exception e) {
			throw new RuntimeException("导出数据出现异常",e);
		}
	}

	/**同步导出相关的统计信息:TXT格式,由于在记事本和UE等文本工具显示格式不一样,改成导出CSV文件*/
	@SuppressWarnings("unused")
	private String syncGenerateExpInfoTXT(List<TbsFileInfo> fileInfoList, String finalSaveDir) {
		String title = "文件名\t\t\t\t\t\t\t\t笔数\t\t金额\r\n";
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
			sb.append("总计:\t\t\t\t\t\t\t\t\t"+finalCount+"\t\t\t\t"+finalFamt);
			return sb.toString()+"#"+expInfoFileName+"文件清单信息("+finalSaveDir.substring(finalSaveDir.lastIndexOf(File.separator)+1)+").txt";
		}
	}

	/**同步导出相关的统计信息:CSV格式*/
	private String syncGenerateExpInfoCSV(List<TbsFileInfo> fileInfoList, String finalSaveDir) {
		String split = ",";
		String title = "文件名"+split+"笔数"+split+"金额\r\n";
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
			sb.append("总计,"+finalCount+","+finalFamt);
			return sb.toString()+"#"+expInfoFileName+"文件清单信息("+finalSaveDir.substring(finalSaveDir.lastIndexOf(File.separator)+1)+").CSV";
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
	
	/**生成要保存的导出文件日志*/
	private TvSendlogDto generateLogDto(TbsFileInfo fileInfo, String bizType2){
		TvSendlogDto sendlogDto = new TvSendlogDto();
		sendlogDto.setStitle("客户端路径："+fileInfo.getFullFileName());
		sendlogDto.setSoperationtypecode(bizType2);
		sendlogDto.setStrecode(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
		sendlogDto.setIcount(fileInfo.getTotalCount());
		sendlogDto.setNmoney(fileInfo.getTotalFamt());
		return sendlogDto;
	}
	
	/**
	 * 根据结果对象生成提示信息(导入TBS回执文件)
	 * @param resultDto
	 * @param serverFileList
	 */
    public String generateMessage(MulitTableDto resultDto,List<String> serverFileList){
    	List<String> errorInfoList = resultDto.getErrorList(); //错误信息列表
		List<String> errorFileList = resultDto.getErrNameList(); //错误文件列表
		if(errorFileList==null || errorFileList.size()==0) {
			/**如果错误文件名列表为空,则说明全部导入成功**/
			return "文件加载成功,本次共加载成功"+serverFileList.size()+"个文件!";
			
		}else {
			/**处理错误日志信息**/
			String errorLogName = StateConstant.Import_Errinfo_DIR+ "TBS回执导入错误信息("
								+ new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new java.util.Date()) + ").txt";
			StringBuffer errorLog = new StringBuffer(""); //存放全部的错误日志信息
			StringBuffer errorMessage = new StringBuffer(""); //存放要提示给用户的错误日志信息，由于数据较长，所以只显示前15条
			for(int i=0; errorInfoList.size()>0 && i<errorInfoList.size(); i++) {
				String errorInfo = errorInfoList.get(i);
				errorLog.append(errorInfo + "\r\n");
				if(i<15) {
					errorMessage.append(errorInfo + "\r\n");
				}
			}
			if (!"".equals(errorLog.toString())) {
				try {
					/**清理掉10天之前的错误日志信息**/
					FileUtil.getInstance().deleteFileWithDays(StateConstant.Import_Errinfo_DIR,
															  Integer.parseInt(StateConstant.Import_Errinfo_BackDays));
					/**将完整的错误信息保存到本地**/
					FileUtil.getInstance().writeFile(errorLogName,errorLog.toString());
				} catch (Exception e) {
					/**如果保存日志出现异常,则放弃保存**/
					e.printStackTrace();//用于调试
				}
			}
			
			/**错误文件列表不为空,说明存在错误文件**/
			StringBuffer sb = new StringBuffer("本次加载文件共"+serverFileList.size()+"个,其中"
									+(serverFileList.size()-errorFileList.size())+"个成功,"
									+errorFileList.size()+"个失败,部分信息如下【详细错误信息请查看" + errorLogName + "】：\r\n");
			
			sb.append(errorMessage.toString());
			return sb.toString();
		}
    }
	
    /**
     * 根据用户选择的路径生产最终保存的目录
     * @param baseDir
     */
    public String generateSaveDir(String baseDir){
    	String timeDir = new SimpleDateFormat("HH时mm分ss秒").format(new java.util.Date());
    	if(!baseDir.endsWith("/") && !baseDir.endsWith("\\")) { //判断传入的路径后面是否有斜线
    		baseDir += "\\";
		}
    	return baseDir += "数据导出目录("+new Date(new java.util.Date().getTime()).toString()+")" + File.separator + timeDir;
    }
    
	/**
	 * 根绝业务类型生成文件名 
	 * 文件名称：yyyymmddxxxxttm.txt
	 */
	public String generateFileName() {
		try {
			StringBuffer fileName = new StringBuffer();
			/** 文件名前8位日期，根据账务日期**/
			fileName.append(TimeFacade.formatDate(null==acctDate?TimeFacade.getCurrentDate():acctDate,"yyyyMMdd"));
			/** 批次号2或4位生成文件的批次号，用于在国库系统唯一标识导入文件，没有国库业务含义**/
			fileName.append(exportTBSfiletxtService.getTBSNum());
			/** 业务类型**/
			fileName.append(bizType);
			/** m为调整期标志(1位)0－正常期；1－调整期**/
			fileName.append(null == trimFlag?"0":trimFlag);//默认正常期
			/** 生成文件导出的路径和文件名**/
			fileName.append(".txt");
			return fileName.toString();
		} catch (Exception e) {
			throw new RuntimeException("生成文件名时出现异常!",e);
		}
	}
	/**
	 * 根据传入的凭证号码，返回一个8位的字符串
	 * 规则为，如果长度大于8则截取8位，否则原样返回
	 * @return newVouno
	 */
	public String generateVouno(String orivouno){
		if(orivouno.length()>8) {
			return orivouno.substring(orivouno.length() -8 ,orivouno.length());
		}
		return orivouno;
	}
	
	/**
     * 对于要导入的文件路径集合进行校验：
     * 1、长度不能等于0
     * 2、文件必须为txt格式
     * 3、必须在可处理的业务类型范围之类
     */
    public String validateImportFile(List<File> fList){
    	StringBuffer sb = new StringBuffer("错误提示:\n");
    	if(fList==null || fList.size()==0) {
    		sb.append("请先选择文件!");
    	} else {
    		for(int i=0; i<fList.size(); i++) {
    			File file = fList.get(i);
    			String fileName = file.getName();
    			if(!fileName.toLowerCase().endsWith("txt")) {
    				sb.append(fileName+" 文件类型非法!\n");
    				continue;
    			}
    			if(!isInValid(fileName.substring(fileName.length()-8, fileName.lastIndexOf(".")))){
    				sb.append(fileName+" 文件类型非法!\n");
    				continue;
    			}
    		}
    	}
		return sb.toString().equals("错误提示:\n")?"":sb.toString();
    }
    
    /** 校验文件类型 */
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
