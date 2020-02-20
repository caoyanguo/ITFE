package com.cfcc.itfe.service.sendbiz.exporttbsforbj;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.interfaces.support.ServiceContextHelper;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;

/**
 * 导入TBS处理回执文件顶层处理器
 * @author hua
 */
public abstract class BasicTBSFileProcesser implements IProcessHandler{
	private static Log log = LogFactory.getLog(BasicTBSFileProcesser.class);
	/**定义处理器的基名**/
	private static final String BASE_PROCESSER_NAME = "com.cfcc.itfe.service.sendbiz.exporttbsforbj.processer.Process";
	
	/**数据源bean id*/
	private static final String ITFE_DATASOURCE_BEAN_ID = "DataSource.DB.ITFEDB";
	
	/**文件上传下载根路径配置*/
	private static final String ITFE_FILESYSTEM_CONFIG_BEAN_ID = "fileSystemConfig.ITFE.ID";
	
	/**根据文件类型产生相应的文件处理器**/
	public static IProcessHandler generateProcesser(String fileType) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		return (IProcessHandler) Class.forName(BASE_PROCESSER_NAME + fileType.toUpperCase()).newInstance();
	}
	
	/**根据传入的文件完整路径解析出文件所属的类型**/
	public static String resolveFileType(String fullFileName){
		return fullFileName.substring(fullFileName.length()-8, fullFileName.lastIndexOf("."));
	}

	/**将处理器的结果并入总结果中,用于返回**/
	public static void copyProcessRes2Result(String fileName, MulitTableDto oriResult, MulitTableDto destResult) {
		if(oriResult != null) {
			List<String> errorList = oriResult.getErrorList();
			if(errorList != null && errorList.size() > 0) {
				if(fileName!=null && !"".equals(fileName)) {
					fileName.substring(fileName.replace("/", File.separator).replace("\\",File.separator).lastIndexOf(File.separator)+1);
				}
				destResult.getErrNameList().add(fileName);
				destResult.getErrorList().addAll(errorList);
			}
		}
	}
	
	/**根据TBS回执数据更新索引表信息**/
	public void updateVouStatusBySlip(List<TBSFileResultDto> updateList){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			//怎么更新？ 包流水号？授权不能用金额？
			String vtCode = updateList.get(0).getVtcode();
			String sql= "";
			if(MsgConstant.VOUCHER_NO_5106.equals(vtCode)) { //授权额度拆过包所以不能用金额匹配,换成匹配代理行号
				sql = "UPDATE TV_VOUCHERINFO SET S_STATUS=?,S_DEMO=? WHERE S_VTCODE=? AND S_STATUS=? AND S_DEALNO IN (SELECT I_VOUSRLNO FROM TV_GRANTPAYMSGMAIN WHERE S_TRECODE=? AND N_MONEY=? AND S_PACKAGETICKETNO LIKE ? AND S_PAYBANKNO=?)";
			}else {
				sql = "UPDATE TV_VOUCHERINFO SET S_STATUS=?,S_DEMO=? WHERE S_VTCODE=? AND S_STATUS=? AND S_TRECODE=? AND N_MONEY=? AND S_VOUCHERNO LIKE ? ";
			}
			conn = getConnection();
			conn.setAutoCommit(false); //自己控制事务,要么全部更新成功,要么全部不更新
			st = conn.prepareStatement(sql);
			for(int i=0; updateList!=null && updateList.size()>0 && i<updateList.size(); i++) {
				TBSFileResultDto dto = updateList.get(i);
				st.setString(1, DealCodeConstants.VOUCHER_RECIPE);
				st.setString(2, "已收妥");
				st.setString(3, dto.getVtcode());
				st.setString(4, DealCodeConstants.VOUCHER_SENDED);
				st.setString(5, dto.getTreCode());
				st.setBigDecimal(6, dto.getFamt());
				st.setString(7, "%"+dto.getVouno()); //采取like后8位的方式 
				if(MsgConstant.VOUCHER_NO_5106.equals(vtCode)) {
					st.setString(8, dto.getSagentbnkcode());
				}
				st.addBatch();
				if((i+1)%1000==0) { //采取每1000条提交一次的做法,防止业务量过大造成内存溢出
					st.executeBatch();
					st.clearBatch();
				}
			}
			st.executeBatch();
			conn.commit();
			log.debug("导入TBS回执文件更新凭证状态完成: vtCode:["+vtCode+"], updateSql:["+sql+"], 更新总条数:["+updateList.size()+"]");
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("数据回滚出现异常!",e);
			}
			throw new RuntimeException("更新凭证状态异常(TBS回执导入)!",e);
		} finally {
			releaseConnection(conn, st, rs);
		}
	}
	
	/**根据传入的文件路径得到服务器上传文件存放根文件夹**/
	public static String getServerFilePath(String partFileName){
		// 得到文件上传配置
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
				.getApplicationContext().getBean(ITFE_FILESYSTEM_CONFIG_BEAN_ID);
		// 文件上传根路径
		String root = sysconfig.getRoot();
		StringBuffer sb = new StringBuffer(root); // 放置服务器上传根路径
		// 将前台传来的相对路径加上根路径
		sb.append(partFileName.replace("/", File.separator).replace("\\",File.separator));
		return sb.toString();
	}
	
	/**从数据库连接池获取一个连接**/
	public static Connection getConnection() throws SQLException{
		DataSource dataSource = (DataSource) ContextFactory.getApplicationContext().getBean(ITFE_DATASOURCE_BEAN_ID);
		return dataSource.getConnection();
	}
	
	/**按照给定的分隔符解析文件**/
	public List<String[]> readFile(String file, String split) {
		try {
			return FileUtil.getInstance().readFileWithLine(file, split);
		} catch (FileOperateException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** 释放自己获取的连接资源 **/
	public static void releaseConnection(Connection conn, Statement st,ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			
			if (st != null) {
				st.close();
			}
			
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			log.error("释放资源出现异常",e);
		} finally {
			st = null;
			rs = null;
			conn = null;
		}
	}
}
