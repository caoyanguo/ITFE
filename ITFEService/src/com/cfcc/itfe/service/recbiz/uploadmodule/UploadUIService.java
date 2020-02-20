package com.cfcc.itfe.service.recbiz.uploadmodule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.IServiceManagerServer;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time   09-10-22 13:49:49
 * codecomment: 
 */

public class UploadUIService extends AbstractUploadUIService {
	
	private static Log logger = LogFactory.getLog(UploadUIService.class);
	
	/**
	 * 加载数据
	 * @generated
	 * @throws ITFEBizException	 
	 */
    public void UploadDate(FileResultDto fileResultDto) throws ITFEBizException {
		String smsgno = fileResultDto.getSmsgno();

		String sorgcode = this.getLoginInfo().getSorgcode();
		String susercode = this.getLoginInfo().getSuserCode();
		String iscollect = this.getLoginInfo().getIscollect();
		String area = this.getLoginInfo().getArea();
		if(iscollect==null||"".equals(iscollect)||"null".equals(iscollect.toLowerCase()))
			iscollect="0";
		fileResultDto.setIscollect(iscollect);
		fileResultDto.setArea(area);
		/**
		 * 说明：要调用此方法，必须在conf/config/bizconfig/MsgManagerServer.xml中配置好。
		 */
		// 取得对应的报文处理类
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory
				.getApplicationContext().getBean(MsgConstant.SPRING_SERVICE_SERVER+ smsgno);

		iservice.dealFileDto(fileResultDto, sorgcode, susercode);
	}

	public int commitByTraSrlNo(String strasrlno, List fileList) throws ITFEBizException {
		// 取得操作员的机构代码
		int rtnCount = 0;
		String orgcode = this.getLoginInfo().getSorgcode();
		
		// 先根据文件列表名找到对应的包流水号，然后调用发送报文的处理
		// 第一步，先构造查询条件
		StringBuffer sqlbuf = new StringBuffer("(");
		for(int i = 0 ; i < fileList.size(); i++){
			if(i == fileList.size() - 1){
				sqlbuf.append("'" + ((TvInfileDto)fileList.get(i)).getSfilename() + "')");
			}else{
				sqlbuf.append("'" + ((TvInfileDto)fileList.get(i)).getSfilename() + "',");
			}
		}
		
		//校验包状态
		String selectSQL = "SELECT COUNT(S_FILENAME) as COUNT  from TV_FILEPACKAGEREF "
			+ " where S_ORGCODE = ? and S_RETCODE = ? and S_FILENAME in " + sqlbuf;
		
		SQLExecutor sqlExec = null;
		try{
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(orgcode);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_NO_SEND);
			
			SQLResults result = sqlExec.runQueryCloseCon(selectSQL);
			int row = result.getRowCount();
			if(row > 0){
				rtnCount = result.getInt(0, 0);
				if(rtnCount == 0){
					return rtnCount;
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error("检索包与文件对应关系的时候出现异常！", e);
			throw new ITFEBizException("检索包与文件对应关系的时候出现异常！", e);
		}finally {
			sqlExec.closeConnection();
		}
		
		// 更新包状态为处理中
		String updatepackSQL = "update TV_FILEPACKAGEREF set S_RETCODE = ? where S_DEMO = ? and S_ORGCODE = ? and S_FILENAME in " + sqlbuf ;
		SQLExecutor packsqlExec = null;
		try {
			packsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			packsqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // 状态为待处理 
			packsqlExec.addParam(strasrlno);
			packsqlExec.addParam(orgcode);
			packsqlExec.runQueryCloseCon(updatepackSQL);
		} catch (JAFDatabaseException e) {
			logger.error("更新税票包状态的时候出现异常！", e);
			throw new ITFEBizException("更新税票包状态的时候出现异常！", e);
		}finally {
			if (null != packsqlExec) {
				packsqlExec.closeConnection();
			}
		}
		
		return rtnCount; 
		
//		// 更新包状态为处理中
//		String updatedetailSQL = "update TV_INFILE set S_STATUS = ? where S_TRASRLNO = ? and S_ORGCODE = ? and S_FILENAME in " + sqlbuf ;
//		SQLExecutor detailsqlExec = null;
//		try {
//			detailsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			detailsqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // 状态为处理中 
//			detailsqlExec.addParam(strasrlno);
//			detailsqlExec.addParam(orgcode);
//			detailsqlExec.runQueryCloseCon(updatedetailSQL);
//		} catch (JAFDatabaseException e) {
//			logger.error("更新税票明细状态的时候出现异常！", e);
//			throw new ITFEBizException("更新税票明细状态的时候出现异常！", e);
//		}finally {
//			if (null != detailsqlExec) {
//				detailsqlExec.closeConnection();
//			}
//		}
		
//		// 查询包流水号信息
//		String trasrlsql = "select S_FILENAME,S_PACKAGENO from TV_INFILE where S_TRASRLNO = ? and S_ORGCODE = ? and S_FILENAME in " + sqlbuf + " group by S_FILENAME,S_PACKAGENO ";
//		SQLExecutor sqlExec = null;
//		try {
//			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			sqlExec.addParam(strasrlno);
//			sqlExec.addParam(orgcode);
//			SQLResults fileSrlnoRs = sqlExec.runQueryCloseCon(trasrlsql);
//			int row = fileSrlnoRs.getRowCount();
//			
//			// 取得对应的报文处理类
//			String smsgno = MsgConstant.MSG_NO_7211;
//			IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
//					MsgConstant.SPRING_SERVICE_SERVER + smsgno);
//			
//			
//			for(int i = 0 ;i < row ; i++){
//				String filename = fileSrlnoRs.getString(i, 0);
//				String packageno = fileSrlnoRs.getString(i, 1);
//				
//				String filetime = PublicSearchFacade.getFileObjByFileName(filename).getSdate();
//				
//				iservice.sendMsg(filename, orgcode, packageno, smsgno, filetime, null,false);
//			}
//		} catch (JAFDatabaseException e) {
//			logger.error("查询数据的时候出现异常!", e);
//			throw new ITFEBizException("查询数据的时候出现异常!", e);
//		} finally {
//			if (null != sqlExec) {
//				sqlExec.closeConnection();
//			}
//		}
	}

	public void delByTraSrlNo(String strasrlno) throws ITFEBizException {
		String delSQL = "delete from TV_INFILE where S_TRASRLNO = ? and S_ORGCODE = ? ";
		String delDetailSQL = "delete from TV_INFILE_DETAIL where S_TRASRLNO = ? and S_ORGCODE = ? ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(strasrlno);
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.runQuery(delSQL);
			
			sqlExec.addParam(strasrlno);
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.runQueryCloseCon(delDetailSQL);
			
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSdemo(strasrlno);
			packdto.setSorgcode(this.getLoginInfo().getSorgcode());
			
			CommonFacade.getODB().deleteRsByDto(packdto);
		} catch (JAFDatabaseException e) {
			logger.error("删除数据的时候出现异常!", e);
			throw new ITFEBizException("删除数据的时候出现异常!", e);
		} catch (ValidateException e) {
			logger.error("删除数据的时候出现异常!", e);
			throw new ITFEBizException("删除数据的时候出现异常!", e);
		} finally{
			if(null != sqlExec){
				sqlExec.closeConnection();
			}
		}
	}

	public List searchByTraSrlNo(String strasrlno,BigDecimal totalmoney) throws ITFEBizException {
//		String selectSQL="select S_TRASRLNO ,S_FILENAME ,N_MONEY,S_STATUS FROM (";
//		selectSQL += "select S_TRASRLNO as S_TRASRLNO ,S_FILENAME as S_FILENAME , sum(N_MONEY) as N_MONEY,max(S_STATUS) as S_STATUS from TV_INFILE "
//				+ " where S_TRASRLNO = ? and S_ORGCODE = ? group by S_FILENAME,S_TRASRLNO)";
		
		String selectSQL="select a.S_TRASRLNO ,a.S_FILENAME ,a.N_MONEY,a.S_STATUS FROM (";
		selectSQL += "(select S_ORGCODE,S_FILENAME,S_TRASRLNO as S_TRASRLNO , sum(N_MONEY) as N_MONEY from TV_INFILE "
			+ " where S_ORGCODE = ? ";
		if(strasrlno!=null && !strasrlno.trim().equals("")){
			selectSQL+=" and S_TRASRLNO ='"+strasrlno +"'";
		}
		selectSQL+=" group by  S_ORGCODE,S_TRASRLNO,S_FILENAME)) b, (select S_ORGCODE,S_TRASRLNO as S_TRASRLNO ,S_FILENAME, sum(N_MONEY) as N_MONEY,max(S_STATUS) as S_STATUS from TV_INFILE ";
		selectSQL+=" where S_ORGCODE = ? group by S_ORGCODE,S_FILENAME,S_TRASRLNO) a where a.S_TRASRLNO = b.S_TRASRLNO and a.S_ORGCODE = b.S_ORGCODE AND a.S_FILENAME= b.S_FILENAME";
		if(totalmoney!=null){
			selectSQL+=" and b.N_MONEY ="+totalmoney;
		}
		//selectSQL+=" group by S_FILENAME,S_TRASRLNO";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			int row = trasrlnoRs.getRowCount();
			
			List<TvInfileDto> dtolist = new ArrayList<TvInfileDto>();
			for(int i = 0 ;i < row ; i++){
				TvInfileDto tmpdto = new TvInfileDto();
				tmpdto.setStrasrlno(trasrlnoRs.getString(i, 0));
				tmpdto.setSfilename(trasrlnoRs.getString(i, 1));
				tmpdto.setNmoney(trasrlnoRs.getBigDecimal(i, 2));
				tmpdto.setSstatus(trasrlnoRs.getString(i, 3));
				dtolist.add(tmpdto);
			}
			
			return dtolist;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	public List searchByTraSrlNo(String strasrlno) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delByCheckFileList(List checkList) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			for(int i=0;i<checkList.size();i++){
				TvInfileDto idto=(TvInfileDto) checkList.get(i);
				
				TvFilepackagerefDto packdto = new TvFilepackagerefDto();
				packdto.setSdemo(idto.getStrasrlno());
				packdto.setSorgcode(this.getLoginInfo().getSorgcode());
				packdto.setSfilename(idto.getSfilename());
				
				List<TvFilepackagerefDto> packagelsit=CommonFacade.getODB().findRsByDto(packdto);
				for(TvFilepackagerefDto dto:packagelsit){
					String sretcode=dto.getSretcode();
					String packageno=dto.getSpackageno();
					if((!sretcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIVER))&& (!sretcode.equals(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING))&&(!sretcode.equals(DealCodeConstants.DEALCODE_ITFE_DEALING)) && (!sretcode.equals(DealCodeConstants.DEALCODE_ITFE_SEND))){
						String delSQL = "delete from TV_INFILE where S_TRASRLNO = ? and S_ORGCODE = ? and S_FILENAME=? and S_PACKAGENO = ?";
						String delDetailSQL = "delete from TV_INFILE_DETAIL where S_TRASRLNO = ? and S_ORGCODE = ? and S_FILENAME=? and S_PACKAGENO = ?";
						
						sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						sqlExec.addParam(idto.getStrasrlno());
						sqlExec.addParam(this.getLoginInfo().getSorgcode());
						sqlExec.addParam(idto.getSfilename());
						sqlExec.addParam(packageno);
						sqlExec.runQuery(delSQL);
						
						sqlExec.addParam(idto.getStrasrlno());
						sqlExec.addParam(this.getLoginInfo().getSorgcode());
						sqlExec.addParam(idto.getSfilename());
						sqlExec.addParam(packageno);
						sqlExec.runQueryCloseCon(delDetailSQL);
						
						CommonFacade.getODB().deleteRsByDto(dto);
					}else{
						throw new ITFEBizException("报文已经收妥或正在后台提交，不允许删除！");
					}
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error("删除数据的时候出现异常!", e);
			throw new ITFEBizException("删除数据的时候出现异常!", e);
		} catch (ValidateException e) {
			logger.error("删除数据的时候出现异常!", e);
			throw new ITFEBizException("删除数据的时候出现异常!", e);
		} finally{
			if(null != sqlExec){
				sqlExec.closeConnection();
			}
		}
	}

}