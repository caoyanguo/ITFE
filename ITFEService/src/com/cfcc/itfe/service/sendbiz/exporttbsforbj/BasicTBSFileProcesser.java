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
 * ����TBS�����ִ�ļ����㴦����
 * @author hua
 */
public abstract class BasicTBSFileProcesser implements IProcessHandler{
	private static Log log = LogFactory.getLog(BasicTBSFileProcesser.class);
	/**���崦�����Ļ���**/
	private static final String BASE_PROCESSER_NAME = "com.cfcc.itfe.service.sendbiz.exporttbsforbj.processer.Process";
	
	/**����Դbean id*/
	private static final String ITFE_DATASOURCE_BEAN_ID = "DataSource.DB.ITFEDB";
	
	/**�ļ��ϴ����ظ�·������*/
	private static final String ITFE_FILESYSTEM_CONFIG_BEAN_ID = "fileSystemConfig.ITFE.ID";
	
	/**�����ļ����Ͳ�����Ӧ���ļ�������**/
	public static IProcessHandler generateProcesser(String fileType) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		return (IProcessHandler) Class.forName(BASE_PROCESSER_NAME + fileType.toUpperCase()).newInstance();
	}
	
	/**���ݴ�����ļ�����·���������ļ�����������**/
	public static String resolveFileType(String fullFileName){
		return fullFileName.substring(fullFileName.length()-8, fullFileName.lastIndexOf("."));
	}

	/**���������Ľ�������ܽ����,���ڷ���**/
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
	
	/**����TBS��ִ���ݸ�����������Ϣ**/
	public void updateVouStatusBySlip(List<TBSFileResultDto> updateList){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			//��ô���£� ����ˮ�ţ���Ȩ�����ý�
			String vtCode = updateList.get(0).getVtcode();
			String sql= "";
			if(MsgConstant.VOUCHER_NO_5106.equals(vtCode)) { //��Ȩ��Ȳ�������Բ����ý��ƥ��,����ƥ������к�
				sql = "UPDATE TV_VOUCHERINFO SET S_STATUS=?,S_DEMO=? WHERE S_VTCODE=? AND S_STATUS=? AND S_DEALNO IN (SELECT I_VOUSRLNO FROM TV_GRANTPAYMSGMAIN WHERE S_TRECODE=? AND N_MONEY=? AND S_PACKAGETICKETNO LIKE ? AND S_PAYBANKNO=?)";
			}else {
				sql = "UPDATE TV_VOUCHERINFO SET S_STATUS=?,S_DEMO=? WHERE S_VTCODE=? AND S_STATUS=? AND S_TRECODE=? AND N_MONEY=? AND S_VOUCHERNO LIKE ? ";
			}
			conn = getConnection();
			conn.setAutoCommit(false); //�Լ���������,Ҫôȫ�����³ɹ�,Ҫôȫ��������
			st = conn.prepareStatement(sql);
			for(int i=0; updateList!=null && updateList.size()>0 && i<updateList.size(); i++) {
				TBSFileResultDto dto = updateList.get(i);
				st.setString(1, DealCodeConstants.VOUCHER_RECIPE);
				st.setString(2, "������");
				st.setString(3, dto.getVtcode());
				st.setString(4, DealCodeConstants.VOUCHER_SENDED);
				st.setString(5, dto.getTreCode());
				st.setBigDecimal(6, dto.getFamt());
				st.setString(7, "%"+dto.getVouno()); //��ȡlike��8λ�ķ�ʽ 
				if(MsgConstant.VOUCHER_NO_5106.equals(vtCode)) {
					st.setString(8, dto.getSagentbnkcode());
				}
				st.addBatch();
				if((i+1)%1000==0) { //��ȡÿ1000���ύһ�ε�����,��ֹҵ������������ڴ����
					st.executeBatch();
					st.clearBatch();
				}
			}
			st.executeBatch();
			conn.commit();
			log.debug("����TBS��ִ�ļ�����ƾ֤״̬���: vtCode:["+vtCode+"], updateSql:["+sql+"], ����������:["+updateList.size()+"]");
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("���ݻع������쳣!",e);
			}
			throw new RuntimeException("����ƾ֤״̬�쳣(TBS��ִ����)!",e);
		} finally {
			releaseConnection(conn, st, rs);
		}
	}
	
	/**���ݴ�����ļ�·���õ��������ϴ��ļ���Ÿ��ļ���**/
	public static String getServerFilePath(String partFileName){
		// �õ��ļ��ϴ�����
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
				.getApplicationContext().getBean(ITFE_FILESYSTEM_CONFIG_BEAN_ID);
		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();
		StringBuffer sb = new StringBuffer(root); // ���÷������ϴ���·��
		// ��ǰ̨���������·�����ϸ�·��
		sb.append(partFileName.replace("/", File.separator).replace("\\",File.separator));
		return sb.toString();
	}
	
	/**�����ݿ����ӳػ�ȡһ������**/
	public static Connection getConnection() throws SQLException{
		DataSource dataSource = (DataSource) ContextFactory.getApplicationContext().getBean(ITFE_DATASOURCE_BEAN_ID);
		return dataSource.getConnection();
	}
	
	/**���ո����ķָ��������ļ�**/
	public List<String[]> readFile(String file, String split) {
		try {
			return FileUtil.getInstance().readFileWithLine(file, split);
		} catch (FileOperateException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** �ͷ��Լ���ȡ��������Դ **/
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
			log.error("�ͷ���Դ�����쳣",e);
		} finally {
			st = null;
			rs = null;
			conn = null;
		}
	}
}
