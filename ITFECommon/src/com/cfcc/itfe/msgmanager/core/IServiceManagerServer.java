package com.cfcc.itfe.msgmanager.core;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;

public interface IServiceManagerServer {

	/**
	 * 处理从客户端传送过来的文件对象
	 * 
	 * @param FileResultDto
	 *            fileResultDto 文件结果集对象DTO
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	public abstract void dealFileDto(FileResultDto fileResultDto ,String sorgcode, String susercode) throws ITFEBizException;

	/**
	 * 发送报文处理
	 * 
	 * @param String
	 *            sfilename 文件名称
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            spackno 包流水号
	 * @param String
	 *            msgno 报文编号
	 * @param String
	 *            commitdate 委托日期
	 * @param String
	 *            msgcontent 文件内容(只有实拨资金[5101]需要)
	 * @param boolean isRepeat 是否重发
	 * 
	 * @throws ITFEBizException
	 */
	public void sendMsg(String sfilename, String sorgcode, String spackno, String msgno, String commitdate, String msgcontent,boolean isRepeat) throws ITFEBizException;

}
