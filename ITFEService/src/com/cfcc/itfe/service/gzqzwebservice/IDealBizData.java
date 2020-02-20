package com.cfcc.itfe.service.gzqzwebservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.cfcc.itfe.exception.ITFEBizException;

/**
 * 广州接口
 * @author Administrator
 *
 */
@WebService
public interface IDealBizData {
	
	/**
	 * 联机读取地方横联系统税票、退税、支出、实拨、集中支付额度、清算等数据到国库综合前置。
	 * @param fileStr   业务内容(前置上传的文件内容)
	 * @param biztype   业务类型
	 * @param paramStr 原前置界面需要录入要素，字符串传入，以逗号分隔
	 * @return          是否成功 0-失败  1-成功
	 * @throws ITFEBizException
	 */
	String readCommBizData(@WebParam(name = "fileStr") String fileStr,@WebParam(name = "biztype") String biztype,@WebParam(name = "paramStr") String paramStr,String fileName) throws ITFEBizException;
	
	/**
	 * 导出全省各级财政收支业务流水明细数据(税票、社保费收入和其他非税收入、退库、支出业务等)
	 * @param strecodeList 国库代码列表
	 * @param paramsList   原前置界面需要录入要素，通过List传入
	 * @param exptList     需要导出业务流水明细列表
	 * @return             业务流水明细数据，通过list返回
	 * @throws ITFEBizException
	 */
	String sendBizSeriData(@WebParam String params) throws ITFEBizException;
}
