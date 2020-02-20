package com.cfcc.itfe.util.datamove;

import java.sql.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvDwbkDto;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.HtvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.HtvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.HtvPbcpaySubDto;
import com.cfcc.itfe.persistence.dto.HtvTaxCancelDto;
import com.cfcc.itfe.persistence.dto.HtvTaxDto;
import com.cfcc.itfe.persistence.dto.HtvTaxItemDto;
import com.cfcc.itfe.persistence.dto.HtvTaxKindDto;
import com.cfcc.itfe.persistence.dto.HtvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpaySubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvExceptionmanDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.itfe.persistence.dto.TvTaxDto;
import com.cfcc.itfe.persistence.dto.TvTaxItemDto;
import com.cfcc.itfe.persistence.dto.TvTaxKindDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.DBTools;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class DataMoveUtil {
	
	private static Log log = LogFactory.getLog(DataMoveUtil.class);
	
	@SuppressWarnings("unchecked")
	public static void timerTaskForDataMove() throws ITFEBizException{
		
		Date currentDate = TimeFacade.getCurrentDateTime(); // ��ǰϵͳ��ʱ��
		
		log.debug("==========================������������������=========================="+currentDate);
		
		/**
		 *  ȡ��ϵͳ���е�������������
		 */
		// ������������������ʷ��������
		int clsdays = 30;
		// ����ת������  (�ӵ�ǰ��ת�Ƶ���ʷ��)
		int movedays = 7;
		try {
			List<TsSystemDto> list = DatabaseFacade.getDb().find(TsSystemDto.class);
			if(null != list && list.size() != 0){
				clsdays = list.get(0).getIcleardays(); 
				movedays = list.get(0).getItransdays(); 
			}
			
			/**
			 * ����ת�Ʋ������ղ����趨��ת���������У�
			 * ��������������ղ����趨��������������
			 */
			String clssql = createSqlByDays(currentDate, clsdays , null);
			String movesql = createSqlByDays(currentDate, movedays , null);
			String clsresql = createSqlByDays(currentDate, clsdays , null,"1"); //�˿����
			String moveresql = createSqlByDays(currentDate, movedays , null,"1");
			String pbcmovesql = createSqlByDays(currentDate, movedays , "d_acct");
			String pbcclssql = createSqlByDays(currentDate, clsdays , "d_acct");
			//�кŰ������Զ���Ч +1days
			currentDate = TSystemFacade.findAfterDBSystemDate(1);
			updateBankState(currentDate);
			updateSecrKeyByDate(currentDate);
			// ������ҵ������(��Ҫ����ʷ���¼)
			deleteVouTable(HtvGrantpaymsgmainDto.tableName(), HtvGrantpaymsgsubDto.tableName(), clssql); //��Ȩ֧��
			deleteVouTable(HtvDirectpaymsgmainDto.tableName(),HtvDirectpaymsgmainDto.tableName(),clssql);//ֱ��֧��
			deleteVouTable(HtvPayoutmsgmainDto.tableName(),HtvPayoutmsgsubDto.tableName(),clssql);//ʵ���ʽ�
//			String batchclssql = createSqlByDays(currentDate, clsdays , "S_VOUDATE");
			deleteVouTable(HtvDwbkDto.tableName(), null, clsresql);  //�˿�
			deleteVouTable(HtvInCorrhandbookDto.tableName(), null, clsresql);  //����
			String onlinetaxclssql = createSqlByDays(currentDate, clsdays , "S_ACCEPTDATE");
			deleteOnlineTable(HtvTaxDto.tableName(), HtvTaxKindDto.tableName(), HtvTaxItemDto.tableName(),onlinetaxclssql);  //ʵʱ��˰
			String onlineclssql = createSqlByDays(currentDate, clsdays , "S_ORIENTRUSTDATE");
			deleteVouTable(HtvTaxCancelDto.tableName(),null, onlineclssql);  //����
			//����֧����������
			String subclswhere = " I_VOUSRLNO IN(select I_VOUSRLNO from TV_PBCPAY_MAIN where "+ pbcclssql +")";
			deleteVouTable(HtvPbcpayMainDto.tableName(), HtvPbcpaySubDto.tableName(), pbcclssql,subclswhere); //���а���֧��;
			//���л�������
			String subclswhereForReck = " I_VOUSRLNO IN(SELECT I_VOUSRLNO FROM HTV_PAYRECK_BANK WHERE d_acceptdate<= '"+DateUtil.dateBefore(currentDate, clsdays, "D")+"')";
			deleteVouTable(HtvPayreckBankDto.tableName(), HtvPayreckBankListDto.tableName(), " d_acceptdate <= '"+DateUtil.dateBefore(currentDate, clsdays, "D")+"' ",subclswhereForReck); 
			//���л��������˻�
			String subclswhereForReckBack = " I_VOUSRLNO IN(SELECT I_VOUSRLNO FROM tv_payreck_bank_back WHERE d_acceptdate <= '"+DateUtil.dateBefore(currentDate, clsdays, "D")+"')";
			deleteVouTable(HtvPayreckBankBackDto.tableName(), HtvPayreckBankBackListDto.tableName(), " d_acceptdate <= '"+DateUtil.dateBefore(currentDate, clsdays, "D")+"' ",subclswhereForReckBack); 
			//��ֽ��ƾ֤������
			deleteVouTable(HtvVoucherinfoDto.tableName(), null, "S_CREATDATE <= '"+DateUtil.dateBefore(currentDate, clsdays*10, "D").replace("-", "")+"'");
			
			//������ʱ��
			String mainTableWhere ="  s_status = '"+StateConstant.COMMON_YES+"'" ;
			String subTableWhere = "  I_VOUSRLNO IN ( select I_VOUSRLNO FROM  TBS_TV_DIRECTPAYPLAN_MAIN WHERE s_status = '"+StateConstant.COMMON_YES+"' )";
			String mainTableWhereWithState = " s_state = '"+StateConstant.COMMON_YES+"' ";
			String subTableWhereWithState = "  I_VOUSRLNO IN ( select I_VOUSRLNO FROM  TBS_TV_BNKPAY_MAIN WHERE s_state = '"+StateConstant.COMMON_YES+"' )";
			deleteVouTable(TbsTvDwbkDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvDirectpayplanSubDto.tableName(), null, subTableWhere);
			deleteVouTable(TbsTvDirectpayplanMainDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvGrantpayplanSubDto.tableName(), null,subTableWhere.replaceAll("TBS_TV_DIRECTPAYPLAN_MAIN", "TBS_TV_GRANTPAYPLAN_MAIN"));
			deleteVouTable(TbsTvGrantpayplanMainDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvInCorrhandbookDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvPayoutDto.tableName(), null, mainTableWhere);
			deleteVouTable(TvPayoutfinanceDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvPbcpayDto.tableName(),null,mainTableWhere);
			deleteVouTable(TbsTvBnkpaySubDto.tableName(), null, subTableWhereWithState); //���л�������
			deleteVouTable(TbsTvBnkpayMainDto.tableName(), null, mainTableWhereWithState);
			deleteTvMqmessage(currentDate,clsdays);//����mq��Ϣͷid��Ϣ��
		
			// Ȼ��ת��ҵ������(���ͱ�����Ϣ)
//			String filepacksql = "  S_CHKSTATE in('"+StateConstant.CONFIRMSTATE_YES+"','"+StateConstant.CONFIRMSTATE_FAIL+"') and "+createSqlByDays(currentDate, clsdays , null); // ���ͱ�����Ϣ����
			String filepacksql = "  S_CHKSTATE='"+StateConstant.CONFIRMSTATE_YES+"' and S_RETCODE='"+DealCodeConstants.DEALCODE_ITFE_RECEIPT+"'  and "+createSqlByDays(currentDate, clsdays , null); // ���ͱ�����Ϣ����
			deleteVouTable(TvFilepackagerefDto.tableName(), null, filepacksql);
			
			dataMoveToHTable(TvDirectpaymsgmainDto.tableName(), TvDirectpaymsgsubDto.tableName(), movesql); //ֱ��֧�����
			dataMoveToHTable(TvGrantpaymsgmainDto.tableName(), TvGrantpaymsgsubDto.tableName(), movesql); //��Ȩ֧�����
			dataMoveToHTable(TvPayoutmsgmainDto.tableName(), TvPayoutmsgsubDto.tableName(), movesql); //ʵ���ʽ�
			deleteVouTable("TV_FILEPACKAGEREF", null, movesql);//��ת����������ļ������Ӧ��ϵ
			
			dataMoveToHTable(TvDwbkDto.tableName(), null, moveresql); //�˿�
			dataMoveToHTable(TvInCorrhandbookDto.tableName(), null, moveresql); //����
			String onlinetaxmovsql = createSqlByDays(currentDate, movedays , "S_ACCEPTDATE");
			dataOnlineMoveToHTable(TvTaxDto.tableName(), TvTaxKindDto.tableName(), TvTaxItemDto.tableName(),onlinetaxmovsql);  //ʵʱ��˰
			String onlinemovsql = createSqlByDays(currentDate, movedays , "S_ORIENTRUSTDATE");
			dataMoveToHTable(TvTaxCancelDto.tableName(), null, onlinemovsql); //����
			String subwhere = " I_VOUSRLNO IN(select I_VOUSRLNO from TV_PBCPAY_MAIN where "+ pbcmovesql +")";
			dataMoveToHTable(TvPbcpayMainDto.tableName(), TvPbcpaySubDto.tableName(), pbcmovesql,subwhere); //���а���֧��
			String subwhereForReck = " I_VOUSRLNO IN(select I_VOUSRLNO from TV_PAYRECK_BANK where d_acceptdate<='"+DateUtil.dateBefore(currentDate, movedays, "D")+"' and S_RESULT='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"')"; //��������
			dataMoveToHTable(TvPayreckBankDto.tableName(), TvPayreckBankListDto.tableName(), "d_acceptdate<='"+DateUtil.dateBefore(currentDate, movedays, "D")+"' and S_RESULT='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' " , subwhereForReck);
			String subwhereForReckback = " I_VOUSRLNO IN(select I_VOUSRLNO from TV_PAYRECK_BANK_BACK where d_acceptdate<='"+DateUtil.dateBefore(currentDate, movedays, "D")+"' and S_RESULT='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"')"; //��������
			dataMoveToHTable(TvPayreckBankBackDto.tableName(), TvPayreckBankBackListDto.tableName(), "d_acceptdate<='"+DateUtil.dateBefore(currentDate, movedays, "D")+"' and S_RESULT='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' " , subwhereForReckback);
			
			//��ֽ��ƾ֤������
			dataMoveToHTable(TvVoucherinfoDto.tableName(), null, "S_CREATDATE <= '"+DateUtil.dateBefore(currentDate, movedays, "D").replace("-", "")+"'");
			
			// ���������������ջ�����־Ҳ������
			String recvsendsql = createSqlByDays(currentDate, clsdays , "S_DATE"); // �շ���־\������־��Ϣ����
			deleteVouTable(TvRecvlogDto.tableName(), null, recvsendsql);
			deleteVouTable(TvSendlogDto.tableName(), null, recvsendsql);
			deleteVouTable(TsSyslogDto.tableName(), null, recvsendsql);
			deleteVouTable(TvExceptionmanDto.tableName(),null," D_WORKDATE <= '"+DateUtil.dateBefore(currentDate, movedays, "D").replace("-", "")+"'");
			SrvCacheFacade.reloadBuffer(null);
			
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			//throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
		
		log.debug("==========================�ռ����������ڽ���=========================="+currentDate);
	} 
	
	private static void deleteTvMqmessage(Date currentDate,int days) {
		String date = DateUtil.dateBefore(currentDate, days, "D").replace("-", "");
		String delsubSql="delete from " + TvMqmessageDto.tableName() + " where S_ENTRUSTDATE<='" + date+"'";
		SQLExecutor delSubExec=null;
		try {
			delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			delSubExec.runQueryCloseCon(delsubSql);
		} catch (JAFDatabaseException e) {
			log.error("����mq��Ϣͷid��Ϣ������ʧ�ܣ�", e);
		}finally{
			if(delSubExec!=null)
				delSubExec.closeConnection();
		}
	}

	/**
	 * ��������(�����ӱ�ļ�¼�ӽ��׿���˵���ʷ��)
	 * @param String maintablename �������� 
	 * @param String subtablename �ӱ�����
	 * @param String moveSql ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static void dataMoveToHTable(String maintablename,String subtablename,String moveSql) throws ITFEBizException{
		try{
			// ��װ����������
//			String propMainString = DBTools.findColumByTable(maintablename);
			String hMaintablename = "H" + maintablename;
			String moveMainSql = "insert into " + hMaintablename  +
					" select * from " + maintablename + " where " + moveSql ;
//			String moveMainSql = "insert_update into " + hMaintablename + " ( " + propMainString + ") " +
//			" commitcount 100000 select " + propMainString + " from " + maintablename + " where " + moveSql ;
			// ���������¼
			SQLExecutor moveMainExec=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			moveMainExec.runQueryCloseCon(moveMainSql);
			
			// ��װ�ӱ�������
			if(null != subtablename && !"".equals(subtablename.trim())){
				// û���ӱ�,����Ҫ����
				String propSubString = DBTools.findColumByTable(subtablename);
				String hSubtablename = "H" + subtablename;
				String moveSubSql = "insert into " + hSubtablename + " (" + propSubString + ") " +
						" select " + propSubString + " from " + subtablename + " where " + moveSql ;
				// �����ӱ��¼
				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				moveSubExec.runQueryCloseCon(moveSubSql);
			}
		
			// ������ ɾ���������ӱ�
			deleteVouTable(maintablename, subtablename, moveSql);
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * ��������(�����ӱ�ļ�¼�ӽ��׿���˵���ʷ��)
	 * @param String maintablename �������� 
	 * @param String subtablename �ӱ�����
	 * @param String moveSql ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static void dataMoveToHTable(String maintablename,String subtablename,String moveSql,String subMoveSql) throws ITFEBizException{
		try{
			// ��װ����������
			String propMainString = DBTools.findColumByTable(maintablename);
			String hMaintablename = "H" + maintablename;
			String moveMainSql = "insert into " + hMaintablename + " ( " + propMainString + ") " +
					" select " + propMainString + " from " + maintablename + " where " + moveSql ;
			// ���������¼
			SQLExecutor moveMainExec=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			moveMainExec.runQueryCloseCon(moveMainSql);
			
			// ��װ�ӱ�������
			if(null != subtablename && !"".equals(subtablename.trim())){
				// û���ӱ�,����Ҫ����
				String propSubString = DBTools.findColumByTable(subtablename);
				String hSubtablename = "H" + subtablename;
				String moveSubSql = "insert into " + hSubtablename + " (" + propSubString + ") " +
						" select " + propSubString + " from " + subtablename + " where " + subMoveSql ;
				// �����ӱ��¼
				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				moveSubExec.runQueryCloseCon(moveSubSql);
			}
		
			// ������ ɾ���������ӱ�
			deleteVouTable(maintablename, subtablename, moveSql,subMoveSql);
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	
	/**
	 * ��������(��������)(�����ӱ�ļ�¼�ӽ��׿���˵���ʷ��)
	 * @param String maintablename �������� 
	 * @param String subtablename �ӱ�����
	 * @param String moveSql ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static void dataBatchMoveToHTable(String maintablename,String subtablename,String moveSql) throws ITFEBizException{
		try{
			// ��װ����������
			String propMainString = DBTools.findColumByTable(maintablename);
			String hMaintablename = "H" + maintablename;
			String moveMainSql = "insert into " + hMaintablename + " ( " + propMainString + ") " +
					" select " + propMainString + " from " + maintablename + " where " + moveSql ;
			// ���������¼
			SQLExecutor moveMainExec=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			moveMainExec.runQueryCloseCon(moveMainSql);
			
			// ��װ�ӱ�������
			if(null != subtablename && !"".equals(subtablename.trim())){
				// û���ӱ�,����Ҫ����
				String propSubString = DBTools.findColumByTable(subtablename);
				String propSelSubString = findColumByTable(subtablename,"a");
				String hSubtablename = "H" + subtablename;
				String moveSubSql = "insert into " + hSubtablename + " (" + propSubString + ") " +
						" select " + propSelSubString + " from " + subtablename + " a, " + 
						maintablename + " b where a.I_VOUSRLNO = b.I_VOUSRLNO and " +  "b." + moveSql ;
				// �����ӱ��¼
				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				moveSubExec.runQueryCloseCon(moveSubSql);
			}
		
			// ������ ɾ���������ӱ�
			deleteBatchTable(maintablename, subtablename, moveSql);
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * ��������(ʵʱ��˰)(�����ӱ�ļ�¼�ӽ��׿���˵���ʷ��)
	 * @param String maintable �������� 
	 * @param String taxkindtable �ӱ�����
	 * @param String taxItemtable �ӱ�����
	 * @param String moveSql ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static void dataOnlineMoveToHTable(String maintable,String taxkindtable,String taxItemtable,String moveSql) throws ITFEBizException{
		try{
			// ��װ����������
			String propMainString = DBTools.findColumByTable(maintable);
			String hMaintablename = "H" + maintable;
			String moveMainSql = "insert into " + hMaintablename + " ( " + propMainString + ") " +
					" select " + propMainString + " from " + maintable + " where " + moveSql ;
			// ���������¼
			SQLExecutor moveMainExec=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			moveMainExec.runQueryCloseCon(moveMainSql);
			
			// ��װ�ӱ�������
			if(null != taxkindtable && !"".equals(taxkindtable.trim())){
				// û���ӱ�,����Ҫ����
				String propSubString = DBTools.findColumByTable(taxkindtable);
				String propSelSubString = findColumByTable(taxkindtable,"a");
				String hSubtablename = "H" + taxkindtable;
				String moveSubSql = "insert into " + hSubtablename + " (" + propSubString + ") " +
						" select " + propSelSubString + " from " + taxkindtable + " a, " + 
						maintable + " b where a.S_SEQ = b.S_SEQ and " +  "b." + moveSql ;
				// �����ӱ��¼
				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				moveSubExec.runQueryCloseCon(moveSubSql);
			}
		
			if(null != taxItemtable && !"".equals(taxItemtable.trim())){
				// û���ӱ�,����Ҫ����
				String propItemString = DBTools.findColumByTable(taxItemtable);
				String propItemSubString = findColumByTable(taxItemtable,"a");
				String hItemtablename = "H" + taxItemtable;
				String moveItemSql = "insert into " + hItemtablename + " (" + propItemString + ") " +
						" select " + propItemSubString + " from " + taxItemtable + " a, " + taxkindtable + " c," + 
						maintable + " b where a.S_SEQ = c.S_SEQ and a.I_PROJECTID = c.I_PROJECTID and c.S_SEQ = b.S_SEQ and " +  "b." + moveSql ;
				// �����ӱ��¼
				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				moveSubExec.runQueryCloseCon(moveItemSql);
			}
		
			// ������ ɾ���������ӱ�
			deleteOnlineTable(maintable, taxkindtable,taxItemtable, moveSql);
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * �������� - �����ӱ���������
	 * @param maintable
	 * @param subtable
	 * @param moveSql
	 * @throws ITFEBizException
	 */
	public static void deleteVouTable(String maintable,String subtable,String moveSql) throws ITFEBizException{
		try {
			String delMainSql = "";
			if(maintable.equals("TV_DIRECTPAYMSGMAIN")||maintable.equals("TV_GRANTPAYMSGMAIN")||maintable.equals("TV_PAYOUTMSGMAIN")){
				delMainSql = moveSql  +" and S_STATUS='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"'";
			}
			if(maintable.equals("TV_VOUCHERINFO")){
				delMainSql = moveSql  +" and S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"'";
			}
			if(null != subtable && !"".equals(subtable.trim())){
				// ��һ�� ɾ�������ӱ�
				String delsubSql = "";
				if(maintable.equals("TV_DIRECTPAYMSGMAIN")||maintable.equals("TV_GRANTPAYMSGMAIN")){
					delsubSql="delete from " + subtable + " where I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM "+maintable+" where " + delMainSql+")";
				}else if(maintable.equals("TV_PAYOUTMSGMAIN")){
					delsubSql="delete from " + subtable + " where S_BIZNO IN (SELECT S_BIZNO FROM "+maintable+" where " + delMainSql+")";
				}else{
					delsubSql="delete from " + subtable + " where " + moveSql;
				}
				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
				delSubExec.runQueryCloseCon(delsubSql);
			}
			
			if(moveSql != null && !"".equals(moveSql)){
				// �ڶ��� ɾ����������
				String delmainSql="";
				if(maintable.equals("TV_DIRECTPAYMSGMAIN")||maintable.equals("TV_GRANTPAYMSGMAIN")||maintable.equals("TV_PAYOUTMSGMAIN")||maintable.equals("TV_VOUCHERINFO")){
					delmainSql="delete from " + maintable + " where " + delMainSql ;
				}else if(maintable.equals("TV_FILEPACKAGEREF"))
				{
					String date = DateUtil.dateBefore(TimeFacade.getCurrentDateTime(), 20, "D").replace("-", "");
					delmainSql="delete from " + maintable + " where S_ACCDATE <= '" + date + "'";
				}else{
					delmainSql="delete from " + maintable + " where " + moveSql ;
				}
				SQLExecutor delMainExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				delMainExec.runQueryCloseCon(delmainSql);
			}else{
				//������ʱ��
				// �ڶ��� ɾ����������
				String delmainSql="delete from " + maintable ;
				SQLExecutor delTempExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				delTempExec.runQueryCloseCon(delmainSql);
			}
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * �������� - �����ӱ���������
	 * @param maintable
	 * @param subtable
	 * @param moveSql
	 * @throws ITFEBizException
	 */
	public static void deleteVouTable(String maintable,String subtable,String moveSql,String subwhere) throws ITFEBizException{
		try {
			if(null != subtable && !"".equals(subtable.trim())){
				// ��һ�� ɾ�������ӱ�
				String delsubSql="delete from " + subtable + " where " + subwhere;
				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
				delSubExec.runQueryCloseCon(delsubSql);
			}
			
			if(moveSql != null && !"".equals(moveSql)){
				// �ڶ��� ɾ����������
				String delmainSql="delete from " + maintable + " where " + moveSql ;
				SQLExecutor delMainExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				delMainExec.runQueryCloseCon(delmainSql);
			}else{
				//������ʱ��
				// �ڶ��� ɾ����������
				String delmainSql="delete from " + maintable ;
				SQLExecutor delTempExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				delTempExec.runQueryCloseCon(delmainSql);
			}
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * ��������(��������) - �����ӱ���������
	 * @param maintable
	 * @param subtable
	 * @param moveSql
	 * @throws ITFEBizException
	 */
	public static void deleteBatchTable(String maintable,String subtable,String moveSql) throws ITFEBizException{
		try {
			if(null != subtable && !"".equals(subtable.trim())){
				// ��һ�� ɾ�������ӱ�
				String delsubSql="delete from " + subtable + " where " + 
				"I_VOUSRLNO in (SELECT I_VOUSRLNO FROM HTV_PAYOUTFINANCE_MAIN " + " WHERE " +  moveSql + ")";
				
				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
				delSubExec.runQueryCloseCon(delsubSql);
			}
			
			if(moveSql != null && !"".equals(moveSql)){
				// �ڶ��� ɾ����������
				String delmainSql="delete from " + maintable + " where " + moveSql ;
				SQLExecutor delMainExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				delMainExec.runQueryCloseCon(delmainSql);
			}
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * ��������(ʵʱ��˰) - �����ӱ���������
	 * @param maintable
	 * @param taxkindtable
	 * @param taxItemtable
	 * @param moveSql
	 * @throws ITFEBizException
	 */
	public static void deleteOnlineTable(String maintable,String taxkindtable,String taxItemtable,String moveSql) throws ITFEBizException{
		try {
			if(null != taxItemtable && !"".equals(taxItemtable.trim())){
				// ��һ�� ɾ��˰����ϸ�ӱ�
				String delsubSql="delete from " + taxItemtable + " where " + 
				"S_SEQ in (SELECT S_SEQ FROM " + maintable  + " WHERE " +  moveSql + ")";
				
				delsubSql = delsubSql + " and I_PROJECTID in (select a.I_PROJECTID from " + taxkindtable + " a," + maintable + " b where ";
				delsubSql = delsubSql + "a.S_SEQ = b.S_SEQ and b." +  moveSql + ")";
				
				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
				delSubExec.runQueryCloseCon(delsubSql);
			}
			
			if(null != taxkindtable && !"".equals(taxkindtable.trim())){
				// �ڶ��� ɾ��˰���ӱ�
				String delkindSql="delete from " + taxkindtable + " where " + 
				"S_SEQ in (SELECT S_SEQ FROM " + maintable  + " WHERE " +  moveSql + ")";
					
				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
				delSubExec.runQueryCloseCon(delkindSql);
			}
			
			if(moveSql != null && !"".equals(moveSql)){
				// ������ ɾ����������
				String delmainSql="delete from " + maintable + " where " + moveSql ;
				SQLExecutor delMainExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				delMainExec.runQueryCloseCon(delmainSql);
			}
		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
	}
	
	public static String createSqlByDays(Date currentDate,int days ,String dbprop){
		String date = DateUtil.dateBefore(currentDate, days, "D").replace("-", "");
		if(null == dbprop || "".equals(dbprop.trim())){
			return " S_ACCDATE <= '" + date + "'";
		}else{
			return dbprop + " <= '" + date + "'";
		}
	}
	
	public static String createSqlByDays(Date currentDate,int days ,String dbprop,String bizTye){
		String date = DateUtil.dateBefore(currentDate, days, "D");
		if(null == dbprop || "".equals(dbprop.trim())){
			return " D_ACCEPT <= '" + date + "'";
		}else{
			return dbprop + " <= '" + date + "'";
		}
	}
	/**
	 * ��˰��������������
	 * @throws ITFEBizException
	 */
	public static void deletePlaceNoUseTable() throws ITFEBizException{
		try {
			SQLExecutor proexcutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			proexcutor.runStoredProcExecCloseCon("PRC_DEL_INFILE_PLACE");
		} catch (JAFDatabaseException e) {
			log.error("��˰��������������ʧ�ܣ�", e);
			throw new ITFEBizException("��˰��������������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * ���ݱ����õ�����ֶ�
	 * @param tabName
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String findColumByTable(String tabName, String alias) throws ITFEBizException { 
		List<String> l = DBTools.lookColumnByTabName(tabName);
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < l.size(); i++) {
			String col = l.get(i);
			b.append(alias + ".");
			b.append(col);
			if (i != l.size() - 1) {
				b.append(",");
			}

		}
		return b.toString();
	}
	/**
	 * �к���Ч���ڵ��ڵ�ǰ���ڵļ�¼���Զ���Ϊ��Ч״̬
	 * @throws ITFEBizException 
	 * @throws JAFDatabaseException 
	 */
	public static void updateBankState(Date currentDate) throws  JAFDatabaseException { 
		
		SQLExecutor sqlExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
		String sql="update ts_paybank set s_state = '1' where d_affdate <= ? and s_state = '0'" ;
		sqlExec.addParam(currentDate);
		sqlExec.runQueryCloseCon(sql);
	}
	
	/**
	 * ��Կ����Ч�����Զ���Ч
	 * @throws ITFEBizException 
	 * @throws JAFDatabaseException 
	 */
	public static void updateSecrKeyByDate(Date currentDate) throws  JAFDatabaseException { 
		
		SQLExecutor sqlExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
		String sql="update ts_mankey set s_key = s_newkey,S_ENCRYPTKEY=S_NEWENCRYPTKEY,I_MODICOUNT=1 where (d_affdate <= ? or d_affdate is null) AND  I_MODICOUNT =2 " ;
		sqlExec.addParam(currentDate);
		sqlExec.runQueryCloseCon(sql);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date currentDate = TimeFacade.getCurrentDateTime(); // ��ǰϵͳ��ʱ��
		createSqlByDays(currentDate, 4 , null);
		try {
			deletePlaceNoUseTable();
		} catch (ITFEBizException e) {
			e.printStackTrace();
		}
	}

}
