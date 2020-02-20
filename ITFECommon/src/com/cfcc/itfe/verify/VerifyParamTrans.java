package com.cfcc.itfe.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * ��Ҫ����:У�����ת���Ƿ�ɹ� 
 * @author zhouchuan
 *
 */
public class VerifyParamTrans {

	private static Log logger = LogFactory.getLog(VerifyParamTrans.class);	
	
	/**
	 * У��˰Ʊ�������ת���Ƿ�ɹ�
	 * У���ֶΣ����ջ���(S_TBS_TAXORGCODE)��������־(S_TBS_ASSITSIGN)
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @throws ITFEBizException
	 */
	public static void verifyTBSIncomeTrans(String orgcode, String filename)
			throws ITFEBizException {
		// �Ȱ��� [��������+�ļ�����] ��Ϊԭʼ����(��Ҫ�ڲ�������Ψһ������ֱ������PK)
		String updateTaxSql = " update TV_INFILE_TMP a "
				+ " set a.S_TAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_RECVTRECODE = b.S_TRECODE and a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_TAXORGCODE = b.S_TBSTAXORGCODE ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";

//		String updateAssistSql1 = " update TV_INFILE_TMP a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE) "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
//		
//		String updateAssistSql2 = " update TV_INFILE_TMP a "
//			+ " set a.S_ASSITSIGN = "
//			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and b.S_BUDGETSUBCODE ='N' and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
//			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
		
		//���������Ϳ�Ŀ���뾫ȷƥ��
		String updateAssistSql1 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_RECVTRECODE = b.S_TRECODE and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN ) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
	
		//������뾫ȷƥ�䡢��Ŀ����ΪN
		String updateAssistSql2 = " update TV_INFILE_TMP a "
		+ " set a.S_ASSITSIGN = "
		+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_RECVTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE ='N' and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
		+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
		
		//��Ŀ���뾫ȷƥ�䡢�������ΪN
		String updateAssistSql3 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and b.S_TRECODE = 'N' and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
			
		//��Ŀ����ΪN���������ΪN
		String updateAssistSql4 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and b.S_TRECODE = 'N' and b.S_BUDGETSUBCODE = 'N' and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
			
		String selectTaxSql = " select distinct S_RECVTRECODE,S_TBS_TAXORGCODE from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and (S_TAXORGCODE is NULL)";
		String selectAssistsign = "SELECT DISTINCT S_BUDGETSUBCODE FROM TV_INFILE_TMP WHERE S_ORGCODE = ? AND S_FILENAME = ? AND S_BUDGETLEVELCODE= ? AND (S_ASSITSIGN ='' OR S_ASSITSIGN IS NULL)";
		SQLExecutor updatesqlExec = null;
		SQLExecutor selectsqlExec = null;
		SQLExecutor selectAsssqlExec = null;
		try {
			// ��һ�� �Ƚ������ջ��ز���ת��
			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateTaxSql);
			
			// �ڶ��� ���е�һ�θ�����־����ת��
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql1);
			
			// ������ ���е�һ�θ�����־����ת��
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql2);
			
			// ���Ĳ� ���е�һ�θ�����־����ת��
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql3);
			
			// ���岽 ���еڶ��θ�����־����ת��
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQueryCloseCon(updateAssistSql4);
			
			// ������ �ж����ջ��ز���ת���Ƿ�ɹ�,������־�������ж�
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			int count = rs.getRowCount();
			
			//���߲� �жϹ����εĸ�����־�Ƿ�Ϊ��
			selectAsssqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectAsssqlExec.addParam(orgcode);
			selectAsssqlExec.addParam(filename);
			selectAsssqlExec.addParam(MsgConstant.BUDGET_LEVEL_SHARE);
			SQLResults rsAss = selectAsssqlExec.runQueryCloseCon(selectAssistsign);
			int countAss = rsAss.getRowCount();
			if(count == 0 && countAss == 0){
				return ;
			}else{
				StringBuffer sbuf= new StringBuffer("");
				if(count > 0){
					for(int i = 0 ; i < count; i++){
						sbuf.append("���� [")
						.append(rs.getString(i, 0))
						.append("] ��Ӧ�����ջ��ش��� [")
						.append(rs.getString(i, 1))
						.append("] û��ά���������ձ�!")
						.append("\r\n");
					}
				}
				if(countAss > 0 && !"000001900000".equals(ITFECommonConstant.SRC_NODE)){
					sbuf.append("�����ο�Ŀ");
					for(int i = 0 ; i < countAss; i++){
						sbuf.append(" [")
						.append(rsAss.getString(i, 0))
						.append("]");
						if(i!=countAss-1){
							sbuf.append(",");
						}
					}
					sbuf.append("�ĸ�����־Ϊ�ջ���û��ά��������־���ձ�!");
				}
				if("".equals(sbuf.toString()))
					return ;
				throw new ITFEBizException(sbuf.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			
			logger.error("��������˰Ʊ����ת����ʱ������쳣!", e);
			throw new ITFEBizException("��������˰Ʊ����ת����ʱ������쳣!", e);
		}finally{
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
		}
	}
	/**
	 * ��TCBS���ջ�����ΪTBS���ջ��أ�TCBS������־��ΪTBS������־(����)
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @throws ITFEBizException
	 */
	public static void SetTCBSIncomeTrans(String orgcode, String filename)
			throws ITFEBizException {
		String updateTaxSql = " update TV_INFILE_TMP a "
				+ " set a.S_TAXORGCODE = a.S_TBS_TAXORGCODE"
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and a.s_dealno=a.S_DEALNO";

		String updateAssistSql1 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN =a.S_TBS_ASSITSIGN "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and a.s_dealno=a.S_DEALNO ";
	
		SQLExecutor updatesqlExec = null;
		try {
			// ��һ�� �Ƚ������ջ��ز���ת��
			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateTaxSql);
			
			// �ڶ��� ���е�һ�θ�����־����ת��
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql1);
			
		} catch (JAFDatabaseException e) {
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			logger.error("��������˰Ʊ����ת����ʱ������쳣!", e);
			throw new ITFEBizException("��������˰Ʊ����ת����ʱ������쳣!", e);
		}finally{
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
		}
	}
	
	/**
	 * У���˰˰Ʊ�������ת���Ƿ�ɹ�
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            s_operbatch �ļ�����
	 * @throws ITFEBizException
	 */
	public static List<FileObjDto> verifyPlaceIncomeTrans(String orgcode,String s_operbatch) throws ITFEBizException{
		
		SQLExecutor updateTreExec = null;
		SQLExecutor selectTreExec = null;
		
		SQLExecutor updateTaxExec = null;
		SQLExecutor selectTaxExec = null;
		
		SQLExecutor selectTaxTreRefExce = null;
		SQLExecutor tmpSelectExce = null;
		
		SQLExecutor updateBnkExec = null;
		
		SQLExecutor updateassistflagExce = null;
		
		SQLExecutor updatelevelExce = null;
		
		try {
			// ��һ�� �Ƚ��й������ת����У��
			String updateTreSql = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_TCBSTRECODE = "
				+ "(select b.S_TRECODE from TS_TREASURY b where a.S_TRECODE = b.S_TRECODEAREA and b.S_ORGCODE = ? and b.S_TRIMFLAG = '0' ) "
				+ " where a.S_OPERBATCH = ? ";
			String selectTreSql = " select distinct S_TRECODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and (S_TCBSTRECODE is NULL or S_TCBSTRECODE = '')";
			// 1.1 ת��
			updateTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTreExec.addParam(orgcode);
			updateTreExec.addParam(s_operbatch);
			updateTreExec.runQueryCloseCon(updateTreSql);
			// 1.2У��
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(s_operbatch);
			SQLResults treRs = selectTreExec.runQueryCloseCon(selectTreSql);
			int treCount = treRs.getRowCount();
			if(treCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < treCount; i++){
					sbuf.append("��˰������� [")
					.append(treRs.getString(i, 0))
					.append("] ")
					.append("û���ڹ���������������ά����")
					.append("\r\n");
				}
				throw new ITFEBizException(sbuf.toString());
			}
		
			// �ڶ��� �Ƚ������ջ��ز���ת����У��
			String updateTaxSql = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_TCBSTAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_TCBSTRECODE = b.S_TRECODE and b.S_ORGCODE = ? and a.S_TAXORGCODE = b.S_AREATAXORGCODE ) "
				+ " where a.S_OPERBATCH = ? ";
			String selectTaxSql = " select distinct S_TAXORGCODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and (S_TCBSTAXORGCODE is NULL or S_TCBSTAXORGCODE = '')";
			// 1.1 ת��
			updateTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTaxExec.addParam(orgcode);
			updateTaxExec.addParam(s_operbatch);
			updateTaxExec.runQueryCloseCon(updateTaxSql);
			// 1.2У��
			selectTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxExec.addParam(s_operbatch);
			SQLResults taxRs = selectTaxExec.runQueryCloseCon(selectTaxSql);
			int taxCount = taxRs.getRowCount();
			if(taxCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < taxCount; i++){
					sbuf.append("��˰���ջ��ش��� [")
					.append(taxRs.getString(i, 0))
					.append("] ")
					.append("û�������ջ��ض��ղ�������ά����")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			// ������  У�����ջ��غ͹�������Ӧ��ϵ
			List<FileObjDto> rtndtos = new ArrayList<FileObjDto>();
			String wheresql = " where S_ORGCODE = ? and ( " ; 
			String selectTaxTreRefSql = " select distinct S_TCBSTAXORGCODE , S_TCBSTRECODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? ";
			selectTaxTreRefExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxTreRefExce.addParam(s_operbatch);
			SQLResults taxTreRs = selectTaxTreRefExce.runQueryCloseCon(selectTaxTreRefSql);
			int taxTreCount = taxTreRs.getRowCount();
			if(taxTreCount == 0){
				logger.error("У�����ջ��غ͹�������Ӧ��ϵʱ,û���ҵ����������ļ�¼!");
				throw new ITFEBizException("У�����ջ��غ͹�������Ӧ��ϵʱ,û���ҵ����������ļ�¼!");
			}else{
				for(int i = 0 ; i < taxTreCount ; i++){
					FileObjDto tmpdto = new FileObjDto();
					String tmptaxorgcode = taxTreRs.getString(i, 0);
					String tmptrecode = taxTreRs.getString(i, 1);
					
					tmpdto.setStaxorgcode(tmptaxorgcode);
					tmpdto.setStrecode(tmptrecode);
					
					rtndtos.add(tmpdto);
					
					if(i == taxTreCount -1){
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') ) " ;
					}else{
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') or ";
					}
				}
			}
			
			HashMap<String, String> tmpmap = new HashMap<String, String>();
			String tmptaxSql = " select distinct S_TAXORGCODE,S_TRECODE from TS_TAXORG " + wheresql;
			tmpSelectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			tmpSelectExce.addParam(orgcode);
			SQLResults tmpRs = tmpSelectExce.runQueryCloseCon(tmptaxSql);
			int tmpCount = tmpRs.getRowCount();
			for(int i = 0 ; i < tmpCount ; i++){
				String tmptaxorgcode = tmpRs.getString(i, 0);
				String tmptrecode = tmpRs.getString(i, 1);
				
				tmpmap.put(tmptaxorgcode + "_" + tmptrecode, tmptaxorgcode + "_" + tmptrecode);
			}
			
			for(int j = 0 ; j < rtndtos.size(); j++){
				String tmpkey = rtndtos.get(j).getStaxorgcode() + "_" + rtndtos.get(j).getStrecode();
				if(null == tmpmap.get(tmpkey)){
					throw new ITFEBizException("���ջ��� [" + rtndtos.get(j).getStaxorgcode() + "] ����� [" + rtndtos.get(j).getStrecode() + "] �Ķ�Ӧ��ϵû��ά��!");
				}
			}
			
			// ���Ĳ� ���п����е�ת��
			String updateBnkSql = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_PAYOPBNKNO = "
				+ "(select b.S_PAYERACCCODE from TS_BANKANDPAY b where a.S_PAYBNKNO = b.S_AREATAXBANK and b.S_ORGCODE = ? ) "
				+ " where a.S_OPERBATCH = ? ";
			updateBnkExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateBnkExec.addParam(orgcode);
			updateBnkExec.addParam(s_operbatch);
			updateBnkExec.runQueryCloseCon(updateBnkSql);
			
			// ���岽 ���и�����־����ת��
			updateassistflagExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			//1.1 ƥ��Ԥ�㼶��ȥУ��
//			String updateAssistSql4 = " update TV_INFILE_TMP_PLACE a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and b.S_TRECODE = 'N') "
//				+ " where a.S_OPERBATCH = ? and (a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '') ";
//
//			String updateAssistSql3 = " update TV_INFILE_TMP_PLACE a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBJECTCODE = b.S_BUDGETSUBJECT and b.S_TRECODE = 'N') "
//				+ " where a.S_OPERBATCH = ? and (a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '') ";
//
//			String updateAssistSql2 = " update TV_INFILE_TMP_PLACE a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and a.S_TCBSTRECODE = b.S_TRECODE) "
//				+ " where a.S_OPERBATCH = ? and (a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '') ";

			String updateAssistSql1 = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_ASSITSIGN = "
				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBJECTCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE) "
				+ " where a.S_OPERBATCH = ? ";
//			String selectAssignSql = " select distinct S_TCBSTRECODE,S_BUDGETSUBJECTCODE from TV_INFILE_TMP_PLACE "
//				+ "where S_OPERBATCH = ? and S_BUDGETLEVELCODE = '6' and (S_ASSITSIGN is NULL or S_ASSITSIGN = '')";
			String selectAssignSql = "select a.S_BUDGETLEVELCODE,a.S_TCBSTRECODE,a.S_BUDGETSUBJECTCODE from "
				+"(select distinct S_BUDGETLEVELCODE,S_TCBSTRECODE,S_BUDGETSUBJECTCODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and S_BUDGETLEVELCODE = '6') a"
				+ " where not exists (select S_BUDGETLEVEL,S_TRECODE,S_BUDGETSUBJECT from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBJECTCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE)";

//			//1.1 ת��(ƥ�伶�Ρ������Ԥ���Ŀ��
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQuery(updateAssistSql1);

			
//			//1.2  ת��(ƥ��Ԥ�㼶�κ͹��⣩
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQuery(updateAssistSql2);

			//1.1 У��
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(s_operbatch);
			selectTreExec.addParam(orgcode);
			SQLResults assignRs = selectTreExec.runQueryCloseCon(selectAssignSql);
			int assignCount = assignRs.getRowCount();
			if(assignCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < assignCount; i++){
					sbuf.append("������� [")
					.append(assignRs.getString(i, 1))
					.append("] ��Ԥ���Ŀ [")
					.append(assignRs.getString(i, 2))
					.append("] û��ά��������־����ת��!")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			//1.2 ת��(ƥ�伶�Ρ������Ԥ���Ŀ��
			updateassistflagExce.addParam(orgcode);
			updateassistflagExce.addParam(s_operbatch);
			updateassistflagExce.runQuery(updateAssistSql1);
//			//1.3 ƥ��Ԥ�㼶�κ͹������ȥУ��
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQuery(updateAssistSql3);
//			
//			//1.4 ƥ��Ԥ�㼶�Ρ�Ԥ���Ŀ�͹������ȥУ��
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQueryCloseCon(updateAssistSql4);
			
			// ������ ����Ԥ�㼶�ε�ת��
			updatelevelExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updatelevelSql = " update TV_INFILE_TMP_PLACE set S_BUDGETLEVELCODE = '0' where S_BUDGETLEVELCODE = '6' and S_OPERBATCH = ?";
			updatelevelExce.addParam(s_operbatch);
			updatelevelExce.runQueryCloseCon(updatelevelSql);
			
			// ���߲� ˰�ʸ�ֵΪ1.00
			updatelevelExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updatetaxrateSql = " update TV_INFILE_TMP_PLACE set N_TAXRATE = 1.00 where S_OPERBATCH = ?";
			updatelevelExce.addParam(s_operbatch);
			updatelevelExce.runQueryCloseCon(updatetaxrateSql);
			
			return rtndtos;
		} catch (JAFDatabaseException e) {
			if (null != updateTreExec) {
				updateTreExec.closeConnection();
			}
			if (null != selectTreExec) {
				selectTreExec.closeConnection();
			}

			if (null != updateTaxExec) {
				updateTaxExec.closeConnection();
			}
			if (null != selectTaxExec) {
				selectTaxExec.closeConnection();
			}

			if (null != selectTaxTreRefExce) {
				selectTaxTreRefExce.closeConnection();
			}
			if (null != tmpSelectExce) {
				tmpSelectExce.closeConnection();
			}

			if (null != updateBnkExec) {
				updateBnkExec.closeConnection();
			}
			if (null != updateassistflagExce) {
				updateassistflagExce.closeConnection();
			}
			
			if(null != updatelevelExce){
				updatelevelExce.closeConnection();
			}

			logger.error("��������˰Ʊ����ת����ʱ������쳣!", e);
			throw new ITFEBizException("��������˰Ʊ����ת����ʱ������쳣!", e);
		}
	}
	
	/**
	 * У���˰˰Ʊ�������ת���Ƿ�ɹ�
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @throws ITFEBizException
	 */
	public static List<FileObjDto> verifyNationIncomeTrans(String orgcode,String filename) throws ITFEBizException{
		
		SQLExecutor updateTreExec = null;
		SQLExecutor selectTreExec = null;
		
		SQLExecutor updateTaxExec = null;
		SQLExecutor selectTaxExec = null;
		
		SQLExecutor selectTaxTreRefExce = null;
		SQLExecutor tmpSelectExce = null;
		
		SQLExecutor updateBnkExec = null;
		
		SQLExecutor updateassistflagExce = null;
		
		SQLExecutor updatelevelExce = null;
		
		try {
			// ��һ�� �Ƚ��й������ת����У��
			String updateTreSql = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_TCBSTRECODE = "
				+ "(select b.S_TRECODE from TS_TREASURY b where a.S_PAYEETRECODE = b.S_TRECODENATION and b.S_ORGCODE = ? and b.S_TRIMFLAG = '0' ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
			String selectTreSql = " select distinct S_PAYEETRECODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and (S_TCBSTRECODE is NULL or S_TCBSTRECODE = '')";
			// 1.1 ת��
			updateTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTreExec.addParam(orgcode);
			updateTreExec.addParam(orgcode);
			updateTreExec.addParam(filename);
			updateTreExec.runQueryCloseCon(updateTreSql);
			// 1.2У��
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(orgcode);
			selectTreExec.addParam(filename);
			SQLResults treRs = selectTreExec.runQueryCloseCon(selectTreSql);
			int treCount = treRs.getRowCount();
			if(treCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < treCount; i++){
					sbuf.append("��˰������� [")
					.append(treRs.getString(i, 0))
					.append("] ")
					.append("û���ڹ���������������ά����")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
		
			// �ڶ��� �Ƚ������ջ��ز���ת����У��
			String updateTaxSql = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_TCBSTAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_TCBSTRECODE = b.S_TRECODE and b.S_ORGCODE = ? and a.S_TAXORGCODE = b.S_NATIONTAXORGCODE ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
			String selectTaxSql = " select distinct S_TAXORGCODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and (S_TCBSTAXORGCODE is NULL or S_TCBSTAXORGCODE = '')";
			// 1.1 ת��
			updateTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTaxExec.addParam(orgcode);
			updateTaxExec.addParam(orgcode);
			updateTaxExec.addParam(filename);
			updateTaxExec.runQueryCloseCon(updateTaxSql);
			// 1.2У��
			selectTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxExec.addParam(orgcode);
			selectTaxExec.addParam(filename);
			SQLResults taxRs = selectTaxExec.runQueryCloseCon(selectTaxSql);
			int taxCount = taxRs.getRowCount();
			if(taxCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < taxCount; i++){
					sbuf.append("��˰���ջ��ش��� [")
					.append(taxRs.getString(i, 0))
					.append("] ")
					.append("û�������ջ��ض��ղ�������ά����")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			// ������  У�����ջ��غ͹�������Ӧ��ϵ
			List<FileObjDto> rtndtos = new ArrayList<FileObjDto>();
			String wheresql = " where S_ORGCODE = ? and ( " ; 
			String selectTaxTreRefSql = " select distinct S_TCBSTAXORGCODE , S_TCBSTRECODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? ";
			selectTaxTreRefExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxTreRefExce.addParam(orgcode);
			selectTaxTreRefExce.addParam(filename);
			SQLResults taxTreRs = selectTaxTreRefExce.runQueryCloseCon(selectTaxTreRefSql);
			int taxTreCount = taxTreRs.getRowCount();
			if(taxTreCount == 0){
				logger.error("У�����ջ��غ͹�������Ӧ��ϵʱ,û���ҵ����������ļ�¼!");
				throw new ITFEBizException("У�����ջ��غ͹�������Ӧ��ϵʱ,û���ҵ����������ļ�¼!");
			}else{
				for(int i = 0 ; i < taxTreCount ; i++){
					FileObjDto tmpdto = new FileObjDto();
					String tmptaxorgcode = taxTreRs.getString(i, 0);
					String tmptrecode = taxTreRs.getString(i, 1);
					
					tmpdto.setStaxorgcode(tmptaxorgcode);
					tmpdto.setStrecode(tmptrecode);
					
					rtndtos.add(tmpdto);
					
					if(i == taxTreCount -1){
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') ) " ;
					}else{
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') or ";
					}
				}
			}
			
			HashMap<String, String> tmpmap = new HashMap<String, String>();
			String tmptaxSql = " select distinct S_TAXORGCODE,S_TRECODE from TS_TAXORG " + wheresql;
			tmpSelectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			tmpSelectExce.addParam(orgcode);
			SQLResults tmpRs = tmpSelectExce.runQueryCloseCon(tmptaxSql);
			int tmpCount = tmpRs.getRowCount();
			for(int i = 0 ; i < tmpCount ; i++){
				String tmptaxorgcode = tmpRs.getString(i, 0);
				String tmptrecode = tmpRs.getString(i, 1);
				
				tmpmap.put(tmptaxorgcode + "_" + tmptrecode, tmptaxorgcode + "_" + tmptrecode);
			}
			
			for(int j = 0 ; j < rtndtos.size(); j++){
				String tmpkey = rtndtos.get(j).getStaxorgcode() + "_" + rtndtos.get(j).getStrecode();
				if(null == tmpmap.get(tmpkey)){
					throw new ITFEBizException("���ջ��� [" + rtndtos.get(j).getStaxorgcode() + "] ����� [" + rtndtos.get(j).getStrecode() + "] �Ķ�Ӧ��ϵû��ά��!");
				}
			}
			
			// ���Ĳ� ���п����е�ת��
			String updateBnkSql = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_OPENACCBANKCODE = "
				+ "(select b.S_PAYERACCCODE from TS_BANKANDPAY b where a.S_PAYBNKNO = b.S_NATIONTAXBANK and b.S_ORGCODE = ? ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
			updateBnkExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateBnkExec.addParam(orgcode);
			updateBnkExec.addParam(orgcode);
			updateBnkExec.addParam(filename);
			updateBnkExec.runQueryCloseCon(updateBnkSql);
			
			// ���岽 ���и�����־����ת��
			updateassistflagExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			String updateAssistSql4 = " update TV_INFILE_TMP_COUNTRY a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and b.S_TRECODE = 'N') "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and ( a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '' )";
//			String updateAssistSql3 = " update TV_INFILE_TMP_COUNTRY a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE = b.S_BUDGETSUBJECT and b.S_TRECODE = 'N') "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and ( a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '' )";
//			String updateAssistSql2 = " update TV_INFILE_TMP_COUNTRY a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and a.S_TCBSTRECODE = b.S_TRECODE ) "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and ( a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '' )";
			String updateAssistSql1 = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_ASSITSIGN = "
				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
//			String selectAssignSql = " select distinct a.S_TCBSTRECODE,a.S_BUDGETSUBCODE from TV_INFILE_TMP_COUNTRY a, TS_ASSITFLAGTRANS b "
//				+ "where a.S_ORGCODE = ? and a.S_FILENAME = ? and a.S_BUDGETLEVELCODE = '6' and (a.S_BUDGETLEVELCODE <> b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE <> b.S_BUDGETSUBJECT and a.S_TCBSTRECODE <> b.S_TRECODE)";
			String selectAssignSql = "select a.S_BUDGETLEVELCODE,a.S_TCBSTRECODE,a.S_BUDGETSUBCODE from "
				+"(select distinct S_BUDGETLEVELCODE,S_TCBSTRECODE,S_BUDGETSUBCODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and S_BUDGETLEVELCODE = '6') a"
				+ " where not exists (select S_BUDGETLEVEL,S_TRECODE,S_BUDGETSUBJECT from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE)";

//			//1.1 ת����ƥ�伶�Ρ������Ԥ���Ŀ��
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);
//			updateassistflagExce.runQuery(updateAssistSql1);
		
//			//1.2ת����ƥ��Ԥ�㼶�κ͹��⣩
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);
//			updateassistflagExce.runQuery(updateAssistSql2);
			
			//1.1 У��
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(orgcode);
			selectTreExec.addParam(filename);
			selectTreExec.addParam(orgcode);
			SQLResults assignRs = selectTreExec.runQueryCloseCon(selectAssignSql);
			int assignCount = assignRs.getRowCount();
			if(assignCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < assignCount; i++){
					sbuf.append("������� [")
					.append(assignRs.getString(i, 1))
					.append("] ��Ԥ���Ŀ [")
					.append(assignRs.getString(i, 2))
					.append("] û��ά��������־����ת��!")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			//1.2 ת����ƥ�伶�Ρ������Ԥ���Ŀ��
			updateassistflagExce.addParam(orgcode);
			updateassistflagExce.addParam(orgcode);
			updateassistflagExce.addParam(filename);
			updateassistflagExce.runQuery(updateAssistSql1);
			
//			//1.3ƥ��Ԥ�㼶�κ͹������ȥУ��
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);
//			updateassistflagExce.runQuery(updateAssistSql3);
//			
//			//1.4 ƥ��Ԥ�㼶�Ρ�Ԥ���Ŀ�͹������ȥУ��
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);;
//			updateassistflagExce.runQueryCloseCon(updateAssistSql4);
			
			// ������ ����Ԥ�㼶�ε�ת��
			updatelevelExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updatelevelSql = " update TV_INFILE_TMP_COUNTRY set S_BUDGETLEVELCODE = '0' where S_BUDGETLEVELCODE = '6' and S_ORGCODE = ? and S_FILENAME = ?";
			updatelevelExce.addParam(orgcode);
			updatelevelExce.addParam(filename);
			updatelevelExce.runQueryCloseCon(updatelevelSql);
			
			return rtndtos;
		} catch (JAFDatabaseException e) {
			if (null != updateTreExec) {
				updateTreExec.closeConnection();
			}
			if (null != selectTreExec) {
				selectTreExec.closeConnection();
			}

			if (null != updateTaxExec) {
				updateTaxExec.closeConnection();
			}
			if (null != selectTaxExec) {
				selectTaxExec.closeConnection();
			}

			if (null != selectTaxTreRefExce) {
				selectTaxTreRefExce.closeConnection();
			}
			if (null != tmpSelectExce) {
				tmpSelectExce.closeConnection();
			}

			if (null != updateBnkExec) {
				updateBnkExec.closeConnection();
			}
			if (null != updateassistflagExce) {
				updateassistflagExce.closeConnection();
			}
			
			if(null != updatelevelExce){
				updatelevelExce.closeConnection();
			}

			logger.error("��������˰Ʊ����ת����ʱ������쳣!", e);
			throw new ITFEBizException("��������˰Ʊ����ת����ʱ������쳣!", e);
		}
	}
	
	/**
	 * У���˰˰Ʊ�������ת���Ƿ�ɹ�
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            s_operbatch �ļ�����
	 * @throws ITFEBizException
	 */
	public static List<FileObjDto> verifyTIPSIncomeTrans(String orgcode,String filename) throws ITFEBizException{
		return null;
	}
	
	/**
	 * У��ֱ��֧��(����Ȩ֧��)��ȵ�Ԥ������
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            tablename ����
	 * @throws ITFEBizException
	 */
	public static String verifyPayOutBdgKind(String orgcode, String filename,String tablename) throws ITFEBizException {
		String selectTaxSql = " select distinct S_TZBM from " + tablename
				+ " where S_ORGCODE = ? and S_FILENAME = ? ";
		
		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			int count = rs.getRowCount();
			if(count == 0 ){
				logger.error("��������ʧ��,��ȷ��!");
				throw new ITFEBizException("��������ʧ��,��ȷ��!");
			}
			
			boolean bdgkind_in = false;
			boolean bdgkind_out = false;
			
			for(int i = 0 ; i < count ; i++){
				String sbdgkind = rs.getString(0, 0).replace(".","").replace("0", "").trim();
				if (MsgConstant.BDG_KIND_IN.equals(sbdgkind)
						|| MsgConstant.BDG_KIND_IN_CASH.equals(sbdgkind)) {
					bdgkind_in = true;
				}else if(MsgConstant.BDG_KIND_OUT.equals(sbdgkind)){
					bdgkind_out = false;
				}
			}
			
			if(bdgkind_in && bdgkind_out){
				logger.error("�������ݶ�Ӧ��Ԥ�����಻һ��,��ȷ��!");
				throw new ITFEBizException("�������ݶ�Ӧ��Ԥ�����಻һ��,��ȷ��!");
			}
			
			if(bdgkind_in){
				return MsgConstant.BDG_KIND_IN;
			}else if(bdgkind_out){
				return MsgConstant.BDG_KIND_OUT;
			}else{
				logger.error("�������ݶ�Ӧ��Ԥ���������,��ȷ��!");
				throw new ITFEBizException("�������ݶ�Ӧ��Ԥ���������,��ȷ��!");
			}
		} catch (JAFDatabaseException e) {
			logger.error("��������˰Ʊ����ת����ʱ������쳣!", e);
			throw new ITFEBizException("��������˰Ʊ����ת����ʱ������쳣!", e);
		}
	}
	
	/**
	 * У���ļ��ܽ���Ƿ�һ��
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            tablename ����
	 * @param BigDecimal
	 *            fileamt �ļ��ܽ��
	 * @throws ITFEBizException
	 */
	public static void verifySumAmt(String orgcode, String filename, String tablename, BigDecimal fileamt) throws ITFEBizException {
		String selectTaxSql = " select sum(N_MONEY) from " + tablename + " where S_ORGCODE = ? and S_FILENAME = ? ";

		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			BigDecimal dbamt = rs.getBigDecimal(0, 0);
			if(null == dbamt){
				logger.error("¼���ļ�������ļ�ʵ�ʽ����,��ȷ��!");
				throw new ITFEBizException("¼���ļ�������ļ�ʵ�ʽ����,��ȷ��!");
			}
			
			if(fileamt.compareTo(dbamt) != 0){
				logger.error("¼���ļ����[" + fileamt +"]���ļ�ʵ�ʽ��[" + dbamt +"]�����,��ȷ��!");
				throw new ITFEBizException("¼���ļ����[" + fileamt +"]���ļ�ʵ�ʽ��[" + dbamt +"]�����,��ȷ��!");
			}
		} catch (JAFDatabaseException e) {
			logger.error("���㵼���ļ��Ľ���ʱ������쳣!", e);
			throw new ITFEBizException("���㵼���ļ��Ľ���ʱ������쳣!", e);
		}
	}

	/**
	 * У��Ԥ���Ŀ
	 * 
	 * @param String
	 *            csourceflag ������Դ
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            s_operbatch �ļ�����
	 * @throws ITFEBizException
	 */
	public static void verifyBdgSbt(String csourceflag,String orgcode, String filename,String s_operbatch) throws ITFEBizException{
		SQLExecutor selectsqlExec = null;
		try {
			// Ĭ��Ԥ���Ŀ�ĵ�����־Ϊ�ǵ���,¼���־Ϊ��¼��,��Ŀ����Ϊ����,Ԥ������ΪԤ���ڡ�Ԥ�������,
			// ��Ŀ����:����Ϊ0 ����ʱ,��Ŀ���Ա���Ϊ3 ����. ����Ϊ1 ����ʱ,��Ŀ���Բ���Ϊ�ط��̶�.
			// ����Ϊ����[2,3,4,5]��ʱ��,��Ŀ���Բ���Ϊ����̶�
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			SQLResults rs = null;
			if (MsgConstant.MSG_SOURCE_TBS.equals(csourceflag)) {
				String tbssql = "select tmp.S_BUDGETSUBCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
						+ " from ( select * from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? ) tmp left join TS_BUDGETSUBJECT param "
						+ " on tmp.S_ORGCODE = param.S_ORGCODE and tmp.S_BUDGETSUBCODE = param.S_SUBJECTCODE "
						+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//						+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') "
						+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
						+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
						+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(orgcode);
				selectsqlExec.addParam(filename);
				rs = selectsqlExec.runQueryCloseCon(tbssql);
			} else if (MsgConstant.MSG_SOURCE_NATION.equals(csourceflag)) {
				String nationSql = "select tmp.S_BUDGETSUBCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
					+ " from ( select * from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? ) tmp left join TS_BUDGETSUBJECT param "
					+ " on tmp.S_ORGCODE = param.S_ORGCODE and tmp.S_BUDGETSUBCODE = param.S_SUBJECTCODE "
					+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//					+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') "
					+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
					+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
					+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(orgcode);
				selectsqlExec.addParam(filename);
				rs = selectsqlExec.runQueryCloseCon(nationSql);
			} else if (MsgConstant.MSG_SOURCE_PLACE.equals(csourceflag)) {
				String placeSql = "select tmp.S_BUDGETSUBJECTCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
					+ " from (select * from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? ) tmp left join TS_BUDGETSUBJECT param "
					+ " on tmp.S_BUDGETSUBJECTCODE = param.S_SUBJECTCODE "
					+ " and param.S_ORGCODE = ? "
					+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//					+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') " 
					+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
					+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
					+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(s_operbatch);
				selectsqlExec.addParam(orgcode);
				rs = selectsqlExec.runQueryCloseCon(placeSql);
			} else if (MsgConstant.MSG_SOURCE_TIPS.equals(csourceflag)) {
				String tipsSql = "select tmp.S_BUDGETSUBCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
					+ " from (select * from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ?) tmp left join TS_BUDGETSUBJECT param "
					+ " on tmp.S_ORGCODE = param.S_ORGCODE and tmp.S_BUDGETSUBCODE = param.S_SUBJECTCODE "
					+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//					+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') "
					+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
					+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
					+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(orgcode);
				selectsqlExec.addParam(filename);
				rs = selectsqlExec.runQueryCloseCon(tipsSql);
			} else{
				logger.error("������Դ��־[" + csourceflag +"]����!");
				throw new ITFEBizException("������Դ��־[" + csourceflag +"]����!");
			}
			
			StringBuffer errinfo = new StringBuffer();
			boolean errflag = false;
			int count = rs.getRowCount();
			int sumcout = 0 ;
			for(int i = 0 ; i < count ; i++){
				String bdgsbtcode = rs.getString(i, 0);
				String bdglevel = rs.getString(i, 1);
				String param = rs.getString(i, 2);
				if(null == param){
					errflag = true;
					sumcout++;
					errinfo.append("Ԥ���Ŀ [" + bdgsbtcode + "] ��Ԥ�㼶�� [" + bdglevel +"] У�����!\r\n");
					if(sumcout > 20){
						throw new ITFEBizException(errinfo.toString());
					}
				}
			}
			
			if(errflag){
				throw new ITFEBizException(errinfo.toString());
			}
		}catch (JAFDatabaseException e) {
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			
			logger.error("����Ԥ���ĿУ���ʱ������쳣!", e);
			throw new ITFEBizException("����Ԥ���ĿУ���ʱ������쳣!", e);
		}
	}
	
	/**
	 * У���տ����к��Ƿ����
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @throws ITFEBizException
	 */
	public static void verifyBankno(String orgcode, String filename) throws ITFEBizException {
		String selectBanknoSql = "select a.S_PAYEEBANKNO from "
			+"(select distinct S_PAYEEBANKNO from TV_PAYOUTMSGMAIN where S_ORGCODE = ? and S_FILENAME = ?) a"
			+ " where not exists (select b.S_BNKCODE from TS_BANKCODE b where b.S_BNKCODE = a.S_PAYEEBANKNO and b.S_ACCTSTATUS = '1')";

		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults banknoRs = selectsqlExec.runQueryCloseCon(selectBanknoSql);
			int assignCount = banknoRs.getRowCount();
			if(assignCount != 0){
				StringBuffer sbuf = new StringBuffer("�����տ����к�û��ά�������˺���Ϣά��!");
				for(int i = 0 ; i < assignCount; i++){
					sbuf.append(banknoRs.getString(i, 0));
					sbuf.append("\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
		} catch (JAFDatabaseException e) {
			logger.error("�տ����к�У���ʱ������쳣!", e);
			throw new ITFEBizException("�տ����к�У���ʱ������쳣!", e);
		}
	}
	
	/**
	 * У����⼶����Ԥ�㼶�εĹ�ϵ�����룩
	 * 1��TCBS�����޸�ǰ��
	 * ���⼶��Ϊ�м���Ԥ�㼶��ֻ��Ϊ�С����롢����
	 * ���⼶��Ϊ�ؼ���Ԥ�㼶��ֻ��Ϊ�ء����롢����
	 * 2��TCBS�����޸ĺ�
	 * ���⼶��Ϊ�м���Ԥ�㼶��ֻ��Ϊ�С�������ʡ�����롢����
	 * ���⼶��Ϊ�ؼ���Ԥ�㼶��ֻ��Ϊ�ء��С�������ʡ�����롢����
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            verifyFlag У����
	 * @throws ITFEBizException
	 */
	public static void verifyTreasuryLevel(String orgcode, String filename, String verifyFlag) throws ITFEBizException {
		String selectTreLevel="";
		if("1".equals(verifyFlag)){//TCBS�����޸�ǰ
			selectTreLevel = "SELECT a.S_RECVTRECODE,a.S_TBS_TAXORGCODE,a.S_BUDGETLEVELCODE,b.S_TRELEVEL FROM TV_INFILE_TMP a,TS_TREASURY b"
								+ " WHERE a.S_ORGCODE = ? AND a.S_FILENAME = ? AND a.S_RECVTRECODE = b.S_TRECODE"
									+ " AND ((b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_PREFECTURE + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"','"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"'))"
									+ " OR (b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_DISTRICT + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"')))";
		}else if("2".equals(verifyFlag)){//TCBS�����޸ĺ�
			selectTreLevel = "SELECT a.S_RECVTRECODE,a.S_TBS_TAXORGCODE,a.S_BUDGETLEVELCODE,b.S_TRELEVEL FROM TV_INFILE_TMP a,TS_TREASURY b"
				+ " WHERE a.S_ORGCODE = ? AND a.S_FILENAME = ? AND a.S_RECVTRECODE = b.S_TRECODE"
					+ " AND ((b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_DISTRICT + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+(orgcode.startsWith("20")?MsgConstant.BUDGET_LEVEL_PROVINCE+"','":"")+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"'))"
					+ " OR (b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_PREFECTURE + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+(orgcode.startsWith("20")?MsgConstant.BUDGET_LEVEL_PROVINCE+"','":"")+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"','"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"')))";
		}else{
			return;
		}
		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults trelevelRs = selectsqlExec.runQueryCloseCon(selectTreLevel);
			int count = trelevelRs.getRowCount();
			if(count != 0){
				StringBuffer errinfo = new StringBuffer("���¹��⼶����Ԥ�㼶�εĶ�Ӧ��ϵ��!\n");
				for(int i = 0 ; i < count; i++){
					String trecode = trelevelRs.getString(i, 0);
					String taxorgcode = trelevelRs.getString(i, 1);
					String budgetLevel = trelevelRs.getString(i, 2);
					errinfo.append("�������[" + trecode +"],���ջ��� ["+ taxorgcode + "]��Ԥ�㼶��[" + budgetLevel +"]У�����!\r\n");
				}
				
				throw new ITFEBizException(errinfo.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			logger.error(" У����⼶����Ԥ�㼶�ι�ϵ��ʱ������쳣!", e);
			throw new ITFEBizException(" У����⼶����Ԥ�㼶�ι�ϵ��ʱ������쳣!", e);
		}finally{
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
		}
	}
	/**
	 * У������Ƿ��ǻ����ɲ����Ĺ��⡣
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            verifyFlag У����
	 * @throws ITFEBizException
	 */
	public static void verifyTreasury(String orgcode, String filename) throws ITFEBizException {
		if(!orgcode.startsWith("20"))
			return;
		String selectTreLevel="SELECT S_RECVTRECODE,S_DESCTRECODE FROM TV_INFILE_TMP where S_RECVTRECODE!=S_DESCTRECODE and s_orgcode=? and S_FILENAME=?";
		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults trelevelRs = selectsqlExec.runQueryCloseCon(selectTreLevel);
			int count = trelevelRs.getRowCount();
			if(count != 0){
				StringBuffer errinfo = new StringBuffer("�����տ�����Ŀ�Ĺ��ⲻһ��!\n");
				for(int i = 0 ; i < count; i++){
					String trecode = trelevelRs.getString(i, 0);
					String desorgcode = trelevelRs.getString(i, 1);
					errinfo.append("�տ�������Ŀ�Ĺ�����벻һ��[" + trecode+"]["+desorgcode+"]У�����!\n");
				}
				throw new ITFEBizException(errinfo.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			logger.error(" У���տ�������Ŀ�Ĺ�����벻һ��ʱ�����쳣!", e);
			throw new ITFEBizException(" У���տ�������Ŀ�Ĺ�����벻һ��ʱ�����쳣!", e);
		}finally{
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
		}
	}
	/**
	 * У����⼶����Ԥ�㼶�εĹ�ϵ���˿⣩
	 * 1��TCBS�����޸�ǰ��
	 * ���⼶��Ϊ�м���Ԥ�㼶��ֻ��Ϊ�С����롢����
	 * ���⼶��Ϊ�ؼ���Ԥ�㼶��ֻ��Ϊ�ء����롢����
	 * 2��TCBS�����޸ĺ�
	 * ���⼶��Ϊ�м���Ԥ�㼶��ֻ��Ϊ�С����롢����
	 * ���⼶��Ϊ�ؼ���Ԥ�㼶��ֻ��Ϊ�ء��С����롢����
	 * @param String
	 *            trecode �������
	 * @param String
	 *            trelevel ���⼶��
	 * @param String
	 *            bdglevel Ԥ�㼶��
	 * @throws ITFEBizException
	 */
	public static String verifyTreasuryLevelDW(String trecode, String trelevel, String bdglevel) throws ITFEBizException {
		String errorInfo="";
		//�ؿ��Ӧ��Ԥ�㼶���б�
		List<String> provinceList = new ArrayList<String>();
		provinceList.add(MsgConstant.BUDGET_LEVEL_PREFECTURE);
		provinceList.add(MsgConstant.BUDGET_LEVEL_CENTER);
		provinceList.add(MsgConstant.BUDGET_LEVEL_SHARE);
		//�п��Ӧ��Ԥ�㼶���б�
		List<String> cityList = new ArrayList<String>();
		cityList.add(MsgConstant.BUDGET_LEVEL_DISTRICT);
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))//����У��ʡ
			cityList.add(MsgConstant.BUDGET_LEVEL_PROVINCE);
		cityList.add(MsgConstant.BUDGET_LEVEL_CENTER);
		cityList.add(MsgConstant.BUDGET_LEVEL_SHARE);
		//�ؿ��Ӧ��Ԥ�㼶���б�TCBS�����޸ĺ�
		List<String> provinceListA = new ArrayList<String>();
		provinceListA.add(MsgConstant.BUDGET_LEVEL_PREFECTURE);
		provinceListA.add(MsgConstant.BUDGET_LEVEL_DISTRICT);
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))//����У��ʡ
			cityList.add(MsgConstant.BUDGET_LEVEL_PROVINCE);
		provinceListA.add(MsgConstant.BUDGET_LEVEL_CENTER);
		provinceListA.add(MsgConstant.BUDGET_LEVEL_SHARE);
		List<String> sbdglist = null;
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))//����У��ʡ
		{
			sbdglist = new ArrayList<String>();
			sbdglist.add(MsgConstant.BUDGET_LEVEL_PROVINCE);
			sbdglist.add(MsgConstant.BUDGET_LEVEL_CENTER);
			sbdglist.add(MsgConstant.BUDGET_LEVEL_SHARE);
		}
		if("1".equals(ITFECommonConstant.IFVERIFYTRELEVEL)){
			if(MsgConstant.BUDGET_LEVEL_DISTRICT.equals(trelevel)){//�м�
				if(!cityList.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PREFECTURE.equals(trelevel)){//�ؼ�
				if(!provinceList.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(trelevel)&&sbdglist!=null&&sbdglist.size()>0)//ʡ��
			{
				if(!sbdglist.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}

		}else if("2".equals(ITFECommonConstant.IFVERIFYTRELEVEL)){
			if(MsgConstant.BUDGET_LEVEL_DISTRICT.equals(trelevel)){
				if(!cityList.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PREFECTURE.equals(trelevel)){
				if(!provinceListA.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(trelevel)&&sbdglist!=null&&sbdglist.size()>0)//ʡ��
			{
				if(!sbdglist.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}
		}
		return errorInfo;
	}
	
	/**
	 * �ж��ַ����Ƿ����������
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
           return false;
        }
        else
        {
           return true;
        }
    }
	
	/**
	 * �ж��ֶ��Ƿ�Ϊ�գ����Ϊ�շ���true�����򷵻�false
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str)
    {
        if("".equals(str.trim())|| str==null)
           return true;
        else
           return false;
    }

	/**
	 * У�鴫����ַ����Ƿ�����Ƨ�ֵȷǷ��ַ�(' ������Ҫ����У�飬��Ϊ�治�����ݿ���)
	 * chiStr--ҪУ����ַ���
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException 
	 */
	public static String verifyNotUsableChinese(String chiStr) throws ITFEBizException, JAFDatabaseException {
		HashMap<String,String> validateChineseMap = SrvCacheFacade.cacheNotUsableChinese();
		String errChi = "";
		for(int i  = 0 ; i < chiStr.length();i++) {
			Character chi = chiStr.charAt(i);
			if(validateChineseMap.containsKey(chi+"") || (chi+"").equals("'")) {
				if(i<(chiStr.length()-1)) {
					errChi += chi+"��";
				}else {
					errChi += chi;
				}
			}
		}
		if(errChi.endsWith("��")) {
			return errChi.substring(0, errChi.length()-1);
		}
		return errChi;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * У��˰Ʊ�������ת���Ƿ�ɹ�
	 * У���ֶΣ����ջ���(S_TBS_TAXORGCODE)��������־(S_TBS_ASSITSIGN)
	 * 
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @throws ITFEBizException
	 */
	public static void verifyTBSIncomeTransForZJ(String orgcode, String filename)
			throws ITFEBizException {
		// �Ȱ��� [��������+�ļ�����] ��Ϊԭʼ����(��Ҫ�ڲ�������Ψһ������ֱ������PK)
		String updateTaxSql = " update TV_INFILE_TMP a "
				+ " set a.S_TAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_RECVTRECODE = b.S_TRECODE and a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_TAXORGCODE = b.S_TBSTAXORGCODE ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";

		String updateAssistSql1 = " update TV_INFILE_TMP a "
				+ " set a.S_ASSITSIGN = "
				+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
		
		String updateAssistSql2 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and b.S_BUDGETSUBCODE ='N' and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
		
		String selectTaxSql = " select distinct S_RECVTRECODE , S_TBS_TAXORGCODE from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and (S_TAXORGCODE is NULL)";
		
		SQLExecutor updatesqlExec = null;
		SQLExecutor selectsqlExec = null;
		try {
			// ��һ�� �Ƚ������ջ��ز���ת��
			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateTaxSql);
			
			// �ڶ��� ���е�һ�θ�����־����ת��
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql1);
			
			// �ڶ��� ���еڶ��θ�����־����ת��
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQueryCloseCon(updateAssistSql2);
			
			// ������ �ж����ջ��ز���ת���Ƿ�ɹ�,������־�������ж�
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			int count = rs.getRowCount();
			if(count == 0){
				return ;
			}else{
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < count; i++){
					sbuf.append("���� [")
					.append(rs.getString(i, 0))
					.append("] ��Ӧ�����ջ��ش��� [")
					.append(rs.getString(i, 1))
					.append("] û��ά���������ձ�!")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			
			logger.error("��������˰Ʊ����ת����ʱ������쳣!", e);
			throw new ITFEBizException("��������˰Ʊ����ת����ʱ������쳣!", e);
		}
	}

}
