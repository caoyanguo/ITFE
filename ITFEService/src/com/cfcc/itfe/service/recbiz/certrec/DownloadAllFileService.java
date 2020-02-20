package com.cfcc.itfe.service.recbiz.certrec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.pk.TvRecvlogPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.encrypt.KoalPkcs7Encrypt;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 总库业务凭证（信息凭证）下载窗口
 * @author sjz
 * @time   09-11-12 21:47:44
 * codecomment: 
 */

public class DownloadAllFileService extends AbstractDownloadAllFileService {
	private static Log log = LogFactory.getLog(DownloadAllFileService.class);	


	/**
	 * 获得指定日期之前的接收日志，如果日期为空，那么查询全部接收日志	 
	 * @generated
	 * @param recvDate
	 * @return java.util.List
	 * @throws ITFEBizException	
	 */
    public List<TvRecvLogShowDto> getRecvLogBeforeDate(String recvDate) throws ITFEBizException {
    	try{
        	ITFELoginInfo user = getLoginInfo();
        	String sql = "select a.s_recvno,a.s_sendno,a.s_date,a.s_operationtypecode,b.s_operationtypename,"
        		+ "a.s_sendorgcode,c.s_orgname,a.s_title,a.s_recvtime,a.s_retcode,a.s_retcodedesc "
        		+ "from tv_recvlog a,ts_operationtype b,ts_organ c "
        		+ "where a.s_recvorgcode='" + user.getSorgcode() + "'";
        	if ((recvDate != null) && (recvDate.length() > 0)){
        		//如果日期不为空，那么增加日期参数
        		sql += " and a.s_date='" + recvDate + "'";
        	}
        	sql += " and a.s_operationtypecode=b.s_operationtypecode and a.s_sendorgcode=c.s_orgcode "
        		+ "order by a.s_recvtime desc";
        	SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
        	SQLResults ret = exec.runQueryCloseCon(sql,TvRecvLogShowDto.class);
        	List<TvRecvLogShowDto> result = (List<TvRecvLogShowDto>)ret.getDtoCollection();
        	return result;
    	}catch(Exception e){
    		log.error(e);
    		throw new ITFEBizException(e);
    	}
    }

	/**
	 * 根据发送流水号获得业务凭证（信息文件）的详细信息，一个发送流水号可以找到一个	 
	 * @generateddesc
	 * @param no
	 * @return com.cfcc.itfe.persistence.dto.TvFilesDto
	 * @throws ITFEBizException	 
	 */
    public TvFilesDto getFileInfoBySendNo(String sendNo) throws ITFEBizException {
    	try{
        	String sql = "select distinct s_date, s_content from tv_files where s_no=?";
        	SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
        	exec.addParam(sendNo);
        	SQLResults ret = exec.runQueryCloseCon(sql, TvFilesDto.class);
        	List<TvFilesDto> result = (List<TvFilesDto>)ret.getDtoCollection();
        	if ((result == null) || (result.size() == 0)){
        		return new TvFilesDto();
        	}
        	return result.get(0);
    	}catch(Exception e){
    		log.error(e);
    		throw new ITFEBizException(e);
    	}
    }

	/**
	 * 根据发送流水号查找发送凭证的所有附件，一个发送记录可能会有多个附件	 
	 * @generated
	 * @param sendNo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List<TvFilesDto> getFileListBySendNo(TvRecvLogShowDto recvlog, String sendNo) throws ITFEBizException {
    	try{
        	String sql = "select s_date, s_content, s_savepath from tv_files where s_no='" + sendNo + "'";
        	SQLResults ret = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql,TvFilesDto.class);
        	List<TvFilesDto> result = (List<TvFilesDto>)ret.getDtoCollection();
        	if ((result == null) || (result.size() == 0)){
        		return new ArrayList<TvFilesDto>();
        	}
        	TsOperationtypeDto fileType = StampFacade.getOperationTypeByFileType(recvlog.getSoperationtypecode());
        	boolean isEncrypt = true;
        	if ((fileType == null) || (fileType.getSoperationtypecode() == null) || (fileType.getSoperationtypecode().equals("99"))){
        		//如果不是本系统中的业务报文，那么不需要解密
        		isEncrypt = false;
        	}
        	List<TvFilesDto> returnList = new ArrayList<TvFilesDto>();
        	for (TvFilesDto file : result){
        		if ((null == file.getSsavepath()) || (file.getSsavepath().length() == 0))
        			continue;
				if (isEncrypt){
					//业务报文需要进行解密
					String content = StampFacade.getFileByPath(file.getSsavepath());
					//解数字信封
					String unEncrypt = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelop(content);
					if (unEncrypt != ""){
						List<String> unSigned = KoalPkcs7Encrypt.getInstance().pkcs7VerifySign(unEncrypt);
						if (unSigned.size() == 0){
							log.error("验证数字签名时发生错误：" + KoalPkcs7Encrypt.getInstance().getLastError());
						}else{
							file.setScontent(unSigned.get(1));
						}
					}else{
						log.error("解数字信封时发生错误：" + KoalPkcs7Encrypt.getInstance().getLastError());
					}
				}
				returnList.add(file);
        	}
        	return returnList;
    	}catch(Exception e){
    		log.error(e);
    		throw new ITFEBizException(e);
    	}
    }

	/**
	 * 修改已经下载的接收日志的状态
	 */
    public void updateStatus(List recvLogs, String status) throws ITFEBizException {
    	SQLExecutor exec = null;
    	try{
        	exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
        	for (Object o : recvLogs){
        		TvRecvLogShowDto recvLog = (TvRecvLogShowDto)o;
        		//跳过已经作废的记录
        		if (recvLog.getSretcode().equals(ITFECommonConstant.STATUS_CANCELED)) 
        			continue;
        		//修改接收日志状态
        		String updateSql = "update tv_recvlog set s_retcode='" + status + "' where s_recvno='" + recvLog.getSrecvno() + "'";
        		exec.runQuery(updateSql);
        		//修改发送日志状态
        		updateSql = "update tv_sendlog set s_retcode='" + status + "' where s_sendno='" + recvLog.getSsendno() + "'";
        		exec.runQuery(updateSql);
        	}
    	}catch(Exception e){
    		setRollbackOnly();
    		log.error(e);
    		throw new ITFEBizException(e);
    	}finally{
    		if (null != exec){
    			exec.closeConnection();
    		}
    	}
	}

	/**
	 * 接收日志统计
	 */
    public List<TvRecvLogShowDto> getRecvLogReport(String recvDate) throws ITFEBizException {
    	try{
        	ITFELoginInfo user = getLoginInfo();
        	String sql = "select a.s_sendorgcode,c.s_orgname,a.s_operationtypecode,b.s_operationtypename,count(a.s_recvtime) as icount "
        		+ "from tv_recvlog a,ts_operationtype b,ts_organ c "
        		+ "where a.s_recvorgcode='" + user.getSorgcode() + "'";
        	if ((recvDate != null) && (recvDate.length() > 0)){
        		//如果日期不为空，那么增加日期参数
        		sql += " and a.s_date='" + recvDate + "'";
        	}
        	sql += " and a.s_operationtypecode=b.s_operationtypecode and a.s_sendorgcode=c.s_orgcode "
        		+ "group by a.s_sendorgcode,c.s_orgname,a.s_operationtypecode,b.s_operationtypename "
        		+ "order by a.s_sendorgcode,a.s_operationtypecode";
        	SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
        	SQLResults ret = exec.runQueryCloseCon(sql,TvRecvLogShowDto.class);
        	List<TvRecvLogShowDto> result = (List<TvRecvLogShowDto>)ret.getDtoCollection();
        	return result;
    	}catch(Exception e){
    		log.error(e);
    		throw new ITFEBizException(e);
    	}
	}

	/**
	 * 作废已经接收的业务凭证
	 */
    public void recvDelete(TvRecvLogShowDto recvLog) throws ITFEBizException {
		try{
			TvRecvlogPK pk = new TvRecvlogPK();
			pk.setSrecvno(recvLog.getSrecvno());
			TvRecvlogDto recvDto = (TvRecvlogDto)DatabaseFacade.getDb().find(pk);
			if (recvDto == null){
				throw new ITFEBizException("找不到流水号为" + pk.getSrecvno() + "的接收日志。");
			}
			if (!recvDto.getSretcode().trim().equals(recvDto.getSretcode().trim())){
				throw new ITFEBizException("您所作废的业务凭证已经被修改，请重新查询后再作废。");
			}
			//修改发送日志和接收日志的处理标志
			String sql = "update tv_sendlog set s_retcode='" + ITFECommonConstant.STATUS_CANCELED + "' where s_sendno='" + recvDto.getSsendno() + "'";
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			sql = "update tv_recvlog set s_retcode='" + ITFECommonConstant.STATUS_CANCELED + "' where s_sendno='" + recvDto.getSsendno() + "'";
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
		} catch (Exception e) {
			setRollbackOnly();
			log.error(e);
			throw new ITFEBizException("作废接收业务凭证时错误，", e);
		}
	}
}