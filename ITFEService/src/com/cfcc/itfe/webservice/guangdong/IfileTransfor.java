package com.cfcc.itfe.webservice.guangdong;


import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 提供给广东调用的webservice接口
 * @author 
 *
 */
@WebService
public interface IfileTransfor {
	
	/**
	 * 业务数据发送
	 * @param fileHandler 文件内容
	 * @param biztype 业务类型
	 * @param paramStr 原前置界面需要录入的要素，以逗号分隔
	 * @param fileName 文件名称
	 * @param treCode 国库代码
	 * @return
	 * @throws Exception 
	 */
	public String sendCommBizData(@WebParam(name="fileContent") String fileContent,
								  @WebParam(name="biztype") String biztype,
								  @WebParam(name="paramStr") String paramStr,
								  @WebParam(name="fileName") String fileName,
								  @WebParam(name="treCode") String treCode);
	/**
	 * 发生数据状态查询(暂时不提供)
	 * @param fileName 文件名称
	 * @param treCode 国库代码
	 * @return
	 */
	/*
	public String queryCommBizData(@WebParam(name="fileName")String fileName,
								   @WebParam(name="treCode")String treCode);
    */
	
	/**
	 * 业务流水明细数据导出 
	 * @param billType 业务类型
	 * @param params 原报表数据导出界面查询条件，以字符串传入，以逗号分隔；<br>
	 * 		依次分别为国库代码 ，报表日期，预算种类，辖属标志，含款合计标志，调整期标志，征收机关代码
	 * @param separator 分隔符 
	 * @return
	 */
	public String readBizSeriData(@WebParam(name="billType") String billType,
								  @WebParam(name="params") String params,
								  @WebParam(name="separator") String separator);
}
