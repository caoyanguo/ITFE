package com.cfcc.test;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.HtrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvDwbkDto;
import com.cfcc.itfe.persistence.dto.HtvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.HtvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.HtvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.HtvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.HtvInfileDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceSubDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.HtvTaxCancelDto;
import com.cfcc.itfe.persistence.dto.HtvTaxDto;
import com.cfcc.itfe.persistence.dto.HtvTaxItemDto;
import com.cfcc.itfe.persistence.dto.HtvTaxKindDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.itfe.persistence.dto.TvTaxDto;
import com.cfcc.itfe.persistence.dto.TvTaxItemDto;
import com.cfcc.itfe.persistence.dto.TvTaxKindDto;
import com.cfcc.itfe.util.DBTools;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class DataMoveUtil {
	
	private static Log log = LogFactory.getLog(DataMoveUtil.class);
	
	public static void timerTaskForDataMove() throws ITFEBizException{
		
		Date currentDate = TimeFacade.getCurrentDateTime(); // ��ǰϵͳ��ʱ��
		
		log.debug("==========================������������������=========================="+currentDate);
		
		// ȡ��ϵͳ���е�������������
		int clsdays = 30;
		int movedays = 7;
		try {
			List<TsSystemDto> list = DatabaseFacade.getDb().find(TsSystemDto.class);
			if(null != list && list.size() != 0){
				clsdays = list.get(0).getIcleardays(); // ������������������ʷ��������
				movedays = list.get(0).getItransdays(); // ����ת������  (�ӵ�ǰ��ת�Ƶ���ʷ��)
			}
			
			//����ת�ư��ղ����趨���������У���������������ⰴ�ղ����趨������������ҵ����Ϊ�������ϴ󣬰�������ת������*10������
			int clsDaysForInCome = movedays*10;
			String clssql = createSqlByDays(currentDate, clsdays , null);
			String movesql = createSqlByDays(currentDate, movedays , null);
			String clsresql = createSqlByDays(currentDate, clsdays , null,"1"); //�˿����
			String moveresql = createSqlByDays(currentDate, movedays , null,"1");
			String clssqlforincome = createSqlByDays(currentDate, clsDaysForInCome , null);
			
			// ������ҵ������(��Ҫ����ʷ���¼)
			deleteVouTable(HtvInfileDto.tableName(), null, clssqlforincome);  //����
			String detailclssql = createSqlByDays(currentDate, clsDaysForInCome, "S_COMMITDATE");
			deleteVouTable(HtvInfileDetailDto.tableName(), null, detailclssql);//����
			deleteVouTable(HtvGrantpaymsgmainDto.tableName(), HtvGrantpaymsgsubDto.tableName(), clssql); //��Ȩ֧��
			deleteVouTable(HtvDirectpaymsgmainDto.tableName(),HtvDirectpaymsgmainDto.tableName(),clssql);//ֱ��֧��
			deleteVouTable(HtvPayoutmsgmainDto.tableName(),HtvPayoutmsgsubDto.tableName(),clssql);//ʵ���ʽ�
			String batchclssql = createSqlByDays(currentDate, clsdays , "S_VOUDATE");
			deleteBatchTable(HtvPayoutfinanceMainDto.tableName(),HtvPayoutfinanceSubDto.tableName(),batchclssql);//��������
			deleteVouTable(HtvDwbkDto.tableName(), null, clsresql);  //�˿�
			deleteVouTable(HtvInCorrhandbookDto.tableName(), null, clsresql);  //����
			String onlinetaxclssql = createSqlByDays(currentDate, clsdays , "S_ACCEPTDATE");
			deleteOnlineTable(HtvTaxDto.tableName(), HtvTaxKindDto.tableName(), HtvTaxItemDto.tableName(),onlinetaxclssql);  //ʵʱ��˰
			String onlineclssql = createSqlByDays(currentDate, clsdays , "S_ORIENTRUSTDATE");
			deleteVouTable(HtvTaxCancelDto.tableName(),null, onlineclssql);  //����
			
			// Ȼ��ת��ҵ������(���ͱ�����Ϣ)
			String filepacksql = createSqlByDays(currentDate, clsdays , null); // ���ͱ�����Ϣ����
			deleteVouTable(TvFilepackagerefDto.tableName(), null, filepacksql);
			
			dataMoveToHTable(TvInfileDto.tableName(), null, movesql); //����
			String detailmovsql = createSqlByDays(currentDate, movedays , "S_COMMITDATE");
			dataMoveToHTable(TvInfileDetailDto.tableName(), null, detailmovsql); //����
			dataMoveToHTable(TvDirectpaymsgmainDto.tableName(), TvDirectpaymsgsubDto.tableName(), movesql); //ֱ��֧�����
			dataMoveToHTable(TvGrantpaymsgmainDto.tableName(), TvGrantpaymsgsubDto.tableName(), movesql); //��Ȩ֧�����
			dataMoveToHTable(TvPayoutmsgmainDto.tableName(), TvPayoutmsgsubDto.tableName(), movesql); //ʵ���ʽ�
			String batchmovsql = createSqlByDays(currentDate, movedays , "S_VOUDATE");
			dataBatchMoveToHTable(TvPayoutfinanceMainDto.tableName(), TvPayoutfinanceSubDto.tableName(), batchmovsql); //��������
			dataMoveToHTable(TvDwbkDto.tableName(), null, moveresql); //�˿�
			dataMoveToHTable(TvInCorrhandbookDto.tableName(), null, moveresql); //����
			String onlinetaxmovsql = createSqlByDays(currentDate, movedays , "S_ACCEPTDATE");
			dataOnlineMoveToHTable(TvTaxDto.tableName(), TvTaxKindDto.tableName(), TvTaxItemDto.tableName(),onlinetaxmovsql);  //ʵʱ��˰
			String onlinemovsql = createSqlByDays(currentDate, movedays , "S_ORIENTRUSTDATE");
			dataMoveToHTable(TvTaxCancelDto.tableName(), null, onlinemovsql); //����
			
			//ɾ���ձ�������˰Ʊ�������ˮ����ʷ��
			String incomesql = createSqlByDays(currentDate, clsdays/4 , "S_ACCT");
			deleteVouTable(HtvFinIncomeonlineDto.tableName(), null, incomesql);
			String detailsql = createSqlByDays(currentDate, clsdays/4 , "S_INTREDATE");
			deleteVouTable(HtvFinIncomeDetailDto.tableName(), null, detailsql);
			String incomeDaysql = createSqlByDays(currentDate, clsdays/4 , "S_RPTDATE");
			deleteVouTable(HtrIncomedayrptDto.tableName(), null, incomeDaysql);
			deleteVouTable(HtrStockdayrptDto.tableName(), null, incomeDaysql);
			//ɾ����ǰ���ظ�����ļ�¼����ֹ����ת��������ͻ
			deleteVouTable(TrIncomedayrptDto.tableName(), null, incomeDaysql);
			deleteVouTable(TrStockdayrptDto.tableName(), null, incomeDaysql);
			
			//ת���ձ�������˰Ʊ�������ˮ
			String incomemovsql = createSqlByDays(currentDate, 1 , "S_ACCT");
			dataMoveToHTable(TvFinIncomeonlineDto.tableName(), null, incomemovsql); 
			String detailmov = createSqlByDays(currentDate, 1 , "S_INTREDATE");
			dataMoveToHTable(TvFinIncomeDetailDto.tableName(), null, detailmov); 
			String incomemovDaysql = createSqlByDays(currentDate, 1 , "S_RPTDATE");
			dataMoveToHTable(TrIncomedayrptDto.tableName(), null, incomemovDaysql); 
			dataMoveToHTable(TrStockdayrptDto.tableName(), null, incomemovDaysql);
			
			
			// ���������������ջ�����־Ҳ������
			String recvsendsql = createSqlByDays(currentDate, clsdays/10 , "S_DATE"); // �շ���־\������־��Ϣ����
			deleteVouTable(TvRecvlogDto.tableName(), null, recvsendsql);
			deleteVouTable(TvSendlogDto.tableName(), null, recvsendsql);
			deleteVouTable(TsSyslogDto.tableName(), null, recvsendsql);
			
			//������ʱ��
			String mainTableWhere ="  s_status = '"+StateConstant.COMMON_YES+"'" ;
			
			String subTableWhere = "  I_VOUSRLNO IN ( select I_VOUSRLNO FROM  TBS_TV_DIRECTPAYPLAN_MAIN WHERE s_status = '"+StateConstant.COMMON_YES+"' )"; 
			deleteVouTable(TbsTvDwbkDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvDirectpayplanSubDto.tableName(), null, subTableWhere);
			deleteVouTable(TbsTvDirectpayplanMainDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvGrantpayplanSubDto.tableName(), null,subTableWhere.replaceAll("TBS_TV_DIRECTPAYPLAN_MAIN", "TBS_TV_GRANTPAYPLAN_MAIN"));
			deleteVouTable(TbsTvGrantpayplanMainDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvInCorrhandbookDto.tableName(), null, mainTableWhere);
			deleteVouTable(TbsTvPayoutDto.tableName(), null, mainTableWhere);
			deleteVouTable(TvPayoutfinanceDto.tableName(), null, mainTableWhere);
			//�����˰���ɵ�������
//			deletePlaceNoUseTable();
			//�кŰ������Զ���Ч +1days
			currentDate = TSystemFacade.findAfterDBSystemDate(1);
			updateBankState(currentDate);

		} catch (JAFDatabaseException e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException("��������ʧ�ܣ�", e);
		}
		
		log.debug("==========================�ռ����������ڽ���=========================="+currentDate);
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
		// ��װ����������
		String propMainString = DBTools.findColumByTable(maintablename);
		String hMaintablename = "H" + maintablename;
		String moveMainSql = "insert into " + hMaintablename + " ( " + propMainString + ") " +
				" select " + propMainString + " from " + maintablename + " where " + moveSql ;
		// ���������¼
//			SQLExecutor moveMainExec=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			moveMainExec.runQueryCloseCon(moveMainSql);
		System.out.println("ִ�е�sql �� "+moveMainSql);
		
		// ��װ�ӱ�������
		if(null != subtablename && !"".equals(subtablename.trim())){
			// û���ӱ�,����Ҫ����
			String propSubString = DBTools.findColumByTable(subtablename);
			String hSubtablename = "H" + subtablename;
			String moveSubSql = "insert into " + hSubtablename + " (" + propSubString + ") " +
					" select " + propSubString + " from " + subtablename + " where " + moveSql ;
			// �����ӱ��¼
//				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				moveSubExec.runQueryCloseCon(moveSubSql);
			System.out.println("ִ�е�sql �� "+moveSubSql);
		}

		// ������ ɾ���������ӱ�
		deleteVouTable(maintablename, subtablename, moveSql);
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
		// ��װ����������
		String propMainString = DBTools.findColumByTable(maintablename);
		String hMaintablename = "H" + maintablename;
		String moveMainSql = "insert into " + hMaintablename + " ( " + propMainString + ") " +
				" select " + propMainString + " from " + maintablename + " where " + moveSql ;
		// ���������¼
//			SQLExecutor moveMainExec=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			moveMainExec.runQueryCloseCon(moveMainSql);
		System.out.println("ִ�е�sql �� "+moveMainSql);
		
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
//				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				moveSubExec.runQueryCloseCon(moveSubSql);
			System.out.println("ִ�е�sql �� "+moveSubSql);
		}

		// ������ ɾ���������ӱ�
		deleteBatchTable(maintablename, subtablename, moveSql);
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
		// ��װ����������
		String propMainString = DBTools.findColumByTable(maintable);
		String hMaintablename = "H" + maintable;
		String moveMainSql = "insert into " + hMaintablename + " ( " + propMainString + ") " +
				" select " + propMainString + " from " + maintable + " where " + moveSql ;
		// ���������¼
//			SQLExecutor moveMainExec=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			moveMainExec.runQueryCloseCon(moveMainSql);
		System.out.println("ִ�е�sql �� "+moveMainSql);
		
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
//				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				moveSubExec.runQueryCloseCon(moveSubSql);
			System.out.println("ִ�е�sql �� "+moveSubSql);
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
//				SQLExecutor moveSubExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				moveSubExec.runQueryCloseCon(moveItemSql);
			System.out.println("ִ�е�sql �� "+moveItemSql);
		}

		// ������ ɾ���������ӱ�
		deleteOnlineTable(maintable, taxkindtable,taxItemtable, moveSql);
	}
	
	/**
	 * �������� - �����ӱ���������
	 * @param maintable
	 * @param subtable
	 * @param moveSql
	 * @throws ITFEBizException
	 */
	public static void deleteVouTable(String maintable,String subtable,String moveSql) throws ITFEBizException{
		if(null != subtable && !"".equals(subtable.trim())){
			// ��һ�� ɾ�������ӱ�
			String delsubSql="delete from " + subtable + " where " + moveSql;
//				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
//				delSubExec.runQueryCloseCon(delsubSql);
			System.out.println("ִ�е�sql �� "+delsubSql);
		}
		
		if(moveSql != null && !"".equals(moveSql)){
			// �ڶ��� ɾ����������
			String delmainSql="delete from " + maintable + " where " + moveSql ;
//				SQLExecutor delMainExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				delMainExec.runQueryCloseCon(delmainSql);
			System.out.println("ִ�е�sql �� "+delmainSql);
		}else{
			//������ʱ��
			// �ڶ��� ɾ����������
			String delmainSql="delete from " + maintable ;
//				SQLExecutor delTempExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				delTempExec.runQueryCloseCon(delmainSql);
			System.out.println("ִ�е�sql �� "+delmainSql);
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
		if(null != subtable && !"".equals(subtable.trim())){
			// ��һ�� ɾ�������ӱ�
			String delsubSql="delete from " + subtable + " where " + 
			"I_VOUSRLNO in (SELECT I_VOUSRLNO FROM HTV_PAYOUTFINANCE_MAIN " + " WHERE " +  moveSql + ")";
			
//				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
//				delSubExec.runQueryCloseCon(delsubSql);
			System.out.println("ִ�е�sql �� "+delsubSql);
		}
		
		if(moveSql != null && !"".equals(moveSql)){
			// �ڶ��� ɾ����������
			String delmainSql="delete from " + maintable + " where " + moveSql ;
//				SQLExecutor delMainExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				delMainExec.runQueryCloseCon(delmainSql);
			System.out.println("ִ�е�sql �� "+delmainSql);
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
		if(null != taxItemtable && !"".equals(taxItemtable.trim())){
			// ��һ�� ɾ��˰����ϸ�ӱ�
			String delsubSql="delete from " + taxItemtable + " where " + 
			"S_SEQ in (SELECT S_SEQ FROM " + maintable  + " WHERE " +  moveSql + ")";
			
			delsubSql = delsubSql + " and I_PROJECTID in (select a.I_PROJECTID from " + taxkindtable + " a," + maintable + " b where ";
			delsubSql = delsubSql + "a.S_SEQ = b.S_SEQ and b." +  moveSql + ")";
			
//				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
//				delSubExec.runQueryCloseCon(delsubSql);
			System.out.println("ִ�е�sql �� "+delsubSql);
		}
		
		if(null != taxkindtable && !"".equals(taxkindtable.trim())){
			// �ڶ��� ɾ��˰���ӱ�
			String delkindSql="delete from " + taxkindtable + " where " + 
			"S_SEQ in (SELECT S_SEQ FROM " + maintable  + " WHERE " +  moveSql + ")";
				
//				SQLExecutor delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
//				delSubExec.runQueryCloseCon(delkindSql);
			System.out.println("ִ�е�sql �� "+delkindSql);
		}
		
		if(moveSql != null && !"".equals(moveSql)){
			// ������ ɾ����������
			String delmainSql="delete from " + maintable + " where " + moveSql ;
//				SQLExecutor delMainExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//				delMainExec.runQueryCloseCon(delmainSql);
			System.out.println("ִ�е�sql �� "+delmainSql);
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
		String sql="update ts_paybank set s_state = '1' where d_affdate = ? and s_state = '0'" ;
		sqlExec.addParam(currentDate);
		sqlExec.runQueryCloseCon(sql);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date currentDate = TimeFacade.getCurrentDateTime(); // ��ǰϵͳ��ʱ��
//		createSqlByDays(currentDate, 4 , null);
//		try {
//			deletePlaceNoUseTable();
//		} catch (ITFEBizException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			timerTaskForDataMove();
//		} catch (ITFEBizException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		int count = daysOfTwo(java.sql.Date.valueOf("2012-05-02"), java.sql.Date.valueOf("2012-05-24"));
		System.out.println("��������� �� "+count);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(java.sql.Date.valueOf("2012-05-02"));
		String time = TimeFacade.getCurrentStringTime("yyyyMMddHHmmss");
		for(int i = 1 ; i <= count+1 ; i++) {
			System.out.println(DateUtil.date2String2(calendar.getTime()));
			calendar.setTime(java.sql.Date.valueOf("2012-05-02"));
			calendar.add(Calendar.DATE, i);
		}
	}
	/**
	 * �����Ƚ���������֮�����������
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	public static int daysOfTwo(Date fDate, Date oDate) {
	       Calendar aCalendar = Calendar.getInstance();
	       aCalendar.setTime(fDate);
	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       aCalendar.setTime(oDate);
	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       return day2-day1;
	}
}