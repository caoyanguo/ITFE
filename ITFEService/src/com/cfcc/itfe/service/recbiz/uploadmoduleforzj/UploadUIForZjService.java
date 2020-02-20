package com.cfcc.itfe.service.recbiz.uploadmoduleforzj;

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
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhangliang
 * @time   14-07-22 16:57:14
 * codecomment: 
 */

public class UploadUIForZjService extends AbstractUploadUIForZjService {
	private static Log log = LogFactory.getLog(UploadUIForZjService.class);	


	/**
	 * 加载数据
	 * @generated
	 * @throws ITFEBizException	 
	 */
    public void UploadDate(FileResultDto fileResultDto) throws ITFEBizException {
		
    	String smsgno = fileResultDto.getSmsgno();
		String sorgcode = this.getLoginInfo().getSorgcode();
		String susercode = this.getLoginInfo().getSuserCode();

		/**
		 * 说明：要调用此方法，必须在conf/config/bizconfig/MsgManagerServer.xml中配置好。
		 */
		// 取得对应的报文处理类
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory
				.getApplicationContext().getBean(MsgConstant.SPRING_SERVICE_SERVER+ smsgno);

		iservice.dealFileDto(fileResultDto, sorgcode, susercode);
	}

	public void commitByTraSrlNo(String strasrlno, List fileList) throws ITFEBizException {
		// 取得操作员的机构代码
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
			log.error("更新税票包状态的时候出现异常！", e);
			throw new ITFEBizException("更新税票包状态的时候出现异常！", e);
		}finally {
			if (null != packsqlExec) {
				packsqlExec.closeConnection();
			}
		}

	}

	public void delByTraSrlNo(String strasrlno) throws ITFEBizException {
		String delSQL = "delete from TV_INFILE where S_TRASRLNO = ? and S_ORGCODE = ? ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(strasrlno);
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.runQueryCloseCon(delSQL);
			
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSdemo(strasrlno);
			packdto.setSorgcode(this.getLoginInfo().getSorgcode());
			
			CommonFacade.getODB().deleteRsByDto(packdto);
		} catch (JAFDatabaseException e) {
			log.error("删除数据的时候出现异常!", e);
			throw new ITFEBizException("删除数据的时候出现异常!", e);
		} catch (ValidateException e) {
			log.error("删除数据的时候出现异常!", e);
			throw new ITFEBizException("删除数据的时候出现异常!", e);
		} finally{
			if(null != sqlExec){
				sqlExec.closeConnection();
			}
		}
	}

	public List searchByTraSrlNo(String strasrlno) throws ITFEBizException {
		String selectSQL = "select S_TRASRLNO as S_TRASRLNO ,S_FILENAME as S_FILENAME , sum(N_MONEY) as N_MONEY from TV_INFILE "
				+ " where S_TRASRLNO = ? and S_ORGCODE = ? and S_FILENAME IN " +
						"(SELECT S_FILENAME FROM TV_FILEPACKAGEREF WHERE S_ORGCODE = ? AND S_RETCODE = ? ) " +
						"  group by S_FILENAME,S_TRASRLNO ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(strasrlno);
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_NO_SEND);
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			int row = trasrlnoRs.getRowCount();
			
			List<TvInfileDto> dtolist = new ArrayList<TvInfileDto>();
			for(int i = 0 ;i < row ; i++){
				TvInfileDto tmpdto = new TvInfileDto();
				tmpdto.setStrasrlno(trasrlnoRs.getString(i, 0));
				tmpdto.setSfilename(trasrlnoRs.getString(i, 1));
				tmpdto.setNmoney(trasrlnoRs.getBigDecimal(i, 2));
				
				dtolist.add(tmpdto);
			}
			
			return dtolist;
		} catch (JAFDatabaseException e) {
			log.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

}