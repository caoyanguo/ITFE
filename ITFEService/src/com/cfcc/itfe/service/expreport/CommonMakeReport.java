package com.cfcc.itfe.service.expreport;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dao.TvInfileDao;
import com.cfcc.itfe.persistence.dto.AddColumnPayOutDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.dataquery.findatastatdown.AbstractFinDataStatDownService;
import com.cfcc.itfe.service.dataquery.findatastatdown.FinDataStatDownService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time 12-02-25 21:02:16 codecomment:
 */

public class CommonMakeReport implements IService {
	private static Log log = LogFactory.getLog(CommonMakeReport.class);

	/**
	 * 
	 * 
	 * @return
	 */
	public List getReportDataByBllType(TrIncomedayrptDto idto, String billType) {
		return null;

	}
	
	/**
	 * ���ɱ���ȡ��������
	 * 
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public static String makesqlwhere(IDto idto) throws ITFEBizException {
		
		TrIncomedayrptDto incomedto = (TrIncomedayrptDto) idto;
		// �����������
		String strecode = incomedto.getStrecode();
		// ���ջ��ش���
		String staxorg = incomedto.getStaxorgcode();
		List<String> taxorglist=getTaxorgList(strecode,staxorg);
		String uniontaxorg="";
		if(staxorg!=null){
			for(String str:taxorglist){
				uniontaxorg+="'"+str+"',";
			}
			uniontaxorg=uniontaxorg.substring(0, uniontaxorg.length()-1);
		}
		
		// Ԥ������
		String bugtype = incomedto.getSbudgettype();
		// Ͻ����־
		String sbelong = incomedto.getSbelongflag();
		// �����ڱ�־
		String strimflag = incomedto.getStrimflag();
		// ����
		String sdate = incomedto.getSrptdate();
		// �Ƿ񺬿�ϼ�
		String slesumitem = incomedto.getSdividegroup();
		String sqlwhere = "";
		
		
		// ȫϽ--���ջ��ش���--�������
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode ='" + strecode + "' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					sqlwhere += " and s_trecode ='" + strecode + "' "
							+ " and s_TaxOrgCode in (" + uniontaxorg + ") "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if(staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
					//�麣�������⴦��
					if ("1913000000".equals(strecode)
							&& (null != incomedto.getSbillkind()
							&& !"".equals(incomedto.getSbillkind())
							&& (StateConstant.REPORTTYPE_FLAG_NRSHAREBILL.equals(incomedto.getSbillkind()) || StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL.equals(incomedto.getSbillkind())|| StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL.equals(incomedto.getSbillkind())|| StateConstant.REPORTTYPE_FLAG_TRSHAREBILL.equals(incomedto.getSbillkind())))) { 
						//�������Ȿ�����뱨��
						sqlwhere += " and (( s_trecode ='"
							+ strecode
							+ "' "
							+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
							+ strecode + "') " 
							+ " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF+ "' ) ";
						
						//��Ͻ�����ȫϽ����
						// ����ȫϽ"
						sqlwhere += " or ( s_trecode in ( "
								+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
								+ strecode + "') " + " and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_SELF + "')) ";
					}else {
						/**
						 * ����������뱨����ʱ����ѡ��ȫϽʱֻ�ܵ����¼�����ȫϽ�������⣬������������TCBS�·��������뱨��ʱ
						 * Ĭ�������ջ��ش���Ϊ444444444444�����Ե�ȡ��������ʱ���޷�ȡ����
						 * ��Ҫ�޸�����(�Ķ���С)��
						 * 		1-�����ջ��ش��뷶Χд��Ϊ444444444444;
						 * 		2-Ͻ����־��Χ��ΪȫϽ;
						 * by hua -20160822
						 * 
						 * �����޸ģ��������ջ��ط�ΧΪ111111111111~555555555555
						 * by hua -20161027
						 */
						if(null != incomedto && null != incomedto.getSbillkind() && !"".equals(incomedto.getSbillkind()) && StateConstant.REPORTTYPE_FLAG_NRREMOVEBILL.equals(incomedto.getSbillkind())) {
							//�������Ȿ�����뱨��
							sqlwhere += " and (( s_trecode ='"
								+ strecode
								+ "' "
								+ " and s_TaxOrgCode IN ('"+MsgConstant.MSG_TAXORG_NATION_CLASS+"','"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"','"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"','"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"','"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"') " 
								+ " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_ALL+ "' ) ";
						} else {
							//�������Ȿ�����뱨��
							sqlwhere += " and (( s_trecode ='"
								+ strecode
								+ "' "
								+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
								+ strecode + "') " 
								+ " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF+ "' ) ";
						}
						
						//��Ͻ�����ȫϽ����
						// ����ȫϽ"
						sqlwhere += " or ( s_trecode in ( "
								+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
								+ strecode + "') " + " and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_ALL + "')) ";
					}
			    } else{
					throw new ITFEBizException("��ѯ������Ͻ����־λȫϽ��ʱ�����ջ���ֻ����ѡ�����մ��࣡");
				}
			}
		}
		// ����--���ջ��ش����������ջ���--�������
		if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode ='" + strecode + "' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {// ���ջ��ش���
					sqlwhere += " and s_trecode ='"
						+ strecode
						+ "' "
						+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
						+ strecode + "' AND S_TAXPROP='"
						+ staxorg.substring(0, 1) + "' ) "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS) || staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)) {// �������ջ���
					sqlwhere += " and s_trecode ='"
						+ strecode
						+ "' "
						+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
						+ strecode + "' ) " + " and S_BELONGFLAG ='"
						+ sbelong + "' ";
				} else {// �������ջ���
					sqlwhere += " and s_trecode ='" + strecode + "' "
							+ " and s_TaxOrgCode in (" + uniontaxorg + ") "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
				}
			}
		}
		// ȫϽ�Ǳ���--���ջ��ش���--������룺����Ͻ�������ȫϽ����֮�ͣ�����������������ȫϽ��
		if (MsgConstant.RULE_SIGN_ALLNOTSELF.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode ='" + strecode + "' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					// ����ȫϽ"
					sqlwhere += " and s_trecode in ( "
							+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
							+ strecode + "' " + " and s_TaxOrgCode in ("
							+ uniontaxorg + ") " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL + "') ";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
					// ����ȫϽ"
					sqlwhere += " and s_trecode in ( "
							+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
							+ strecode + "' " + " and s_TaxOrgCode not in ('"
							+  MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL + "') ";
					
				}else {
					throw new ITFEBizException(
							"��ѯ������Ͻ����־λȫϽ�Ǳ�����ʱ�����ջ���ֻ����ѡ�����մ��࣡");
				}
			}
		}
		return sqlwhere;

	}

	
	/**
	 * ���ɱ���ȡ��������
	 * 
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public static String makesqlwherebyarea(IDto idto,String sbookorgcode) throws ITFEBizException {
		
		TrIncomedayrptDto incomedto = (TrIncomedayrptDto) idto;
		// �����������
		String strecode = incomedto.getStrecode();
		// ���ջ��ش���
		String staxorg = incomedto.getStaxorgcode();
		
		
		// Ԥ������
		String bugtype = incomedto.getSbudgettype();
		// Ͻ����־
		String sbelong = incomedto.getSbelongflag();
		// �����ڱ�־
		String strimflag = incomedto.getStrimflag();
		// ����
		String sdate = incomedto.getSrptdate();
		// �Ƿ񺬿�ϼ�
		String slesumitem = incomedto.getSdividegroup();
		String sqlwhere = "";
		//�����¼��������ļ���
		StringBuffer trecodes=new StringBuffer();
		
		String uniontaxorg="";
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.ALL_AREA)) {
			//�ϲ������¼������������ջ���(������)
			/*List<String> taxorglist=getTaxorgList(strecode,staxorg);
			uniontaxorg="";
			if(staxorg!=null){
				for(String str:taxorglist){
					uniontaxorg+="'"+str+"',";
				}
				uniontaxorg=uniontaxorg.substring(0, uniontaxorg.length()-1);
			}*/
			// ȡ�ܱ������⼰�¼�����������ӹ���
	    	List<TsTreasuryDto> trelist=getSubTreCode(sbookorgcode);
	    	
	    	for(int i=0;i<trelist.size();i++){
	    		if(i==trelist.size()-1){
	    			trecodes.append("'").append(trelist.get(i).getStrecode()).append("'");
	    		}else{
	    			trecodes.append("'").append(trelist.get(i).getStrecode()).append("',");
	    		}
	    	}
		}else{
			List<String> taxorglist=getTaxorgList(strecode,staxorg);
			uniontaxorg="";
			if(staxorg!=null){
				for(String str:taxorglist){
					uniontaxorg+="'"+str+"',";
				}
				uniontaxorg=uniontaxorg.substring(0, uniontaxorg.length()-1);
			}
		}
		
		
		// ȫϽ--���ջ��ش���--�������
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.SELF_AREA)) {
			if (null == staxorg) {
				sqlwhere += " AND  s_trecode  = '"+strecode+"' "+" and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					        + " and s_TaxOrgCode in (" + uniontaxorg + ") "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if(staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
						// ����ȫϽ"
						sqlwhere += " AND  s_trecode  = '"+strecode+"' "
						        +" and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_ALL + "' ";
			    } else{
					throw new ITFEBizException("��ѯ������Ͻ����־λȫϽ��ʱ�����ջ���ֻ����ѡ�����մ��࣡");
				}
			}
		}else if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.ALL_AREA)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode  IN ( "+trecodes.toString()+" ) "+" and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					sqlwhere += " and s_trecode  IN ( "+trecodes.toString()+" ) "
					        //��ʱȥ���ϲ������ջ��ش��������
							+ " and s_TaxOrgCode = '"+staxorg+"' "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if(staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
						// ����ȫϽ"
						sqlwhere += " and s_trecode  IN ( "+trecodes.toString()+" ) "
						        +" and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_ALL + "'";
			    } else{
					throw new ITFEBizException("��ѯ������Ͻ����־λȫϽ��ʱ�����ջ���ֻ����ѡ�����մ��࣡");
				}
			}
		}
		// ����--���ջ��ش����������ջ���--�������
		if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {// ���ջ��ش���
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					    + " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE  s_trecode  = '"+strecode+"' "
					    + " AND S_TAXPROP='"+ staxorg.substring(0, 1) + "' ) "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS) || staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)) {// �������ջ���
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					    + " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE s_trecode  = '"+strecode+"' ) "
					    + " and S_BELONGFLAG ='"+ sbelong + "' ";
				} else {// �������ջ���
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					         + " and s_TaxOrgCode in (" + uniontaxorg + ") "
							 + " and S_BELONGFLAG ='" + sbelong + "' ";
				}
			}
		}
		// ȫϽ�Ǳ���--���ջ��ش���--������룺����Ͻ�������ȫϽ����֮�ͣ�����������������ȫϽ��
		if (MsgConstant.RULE_SIGN_ALLNOTSELF.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.SELF_AREA)) {
			if (null == staxorg) {
				sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					// ����ȫϽ"
					sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
					         + " and s_TaxOrgCode in ("
							+ uniontaxorg + ") " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL +"'";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
					// ����ȫϽ"
					sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
					        +" and s_TaxOrgCode not in ('"
							+  MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL +"'";
					
				}else {
					throw new ITFEBizException(
							"��ѯ������Ͻ����־λȫϽ�Ǳ�����ʱ�����ջ���ֻ����ѡ�����մ��࣡");
				}
			}
		}
		return sqlwhere;

	}
	
	/**
	 * �������ر��������õ�����ҵ������
	 * 
	 * @return
	 */
	public static String getReportTypeByBillType(TrIncomedayrptDto idto,
			String billType) {
		if (idto.getStrimflag().equals(StateConstant.TRIMSIGN_FLAG_NORMAL)) {// ������
			if (billType.equals(StateConstant.REPORTTYPE_1)) {
				return StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_3)) {
				return StateConstant.REPORTTYPE_FLAG_NRREMOVEBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_8)) {
				return StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_7)) {
				return StateConstant.REPORTTYPE_FLAG_AMOUNTBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_4)) {
				return StateConstant.REPORTTYPE_FLAG_NRSHAREBILL;
			}
		} else {
			if (billType.equals(StateConstant.REPORTTYPE_1)) {
				return StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_3)) {
				return StateConstant.REPORTTYPE_FLAG_TRREMOVEBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_8)) {
				return StateConstant.REPORTTYPE_FLAG_TRDRAWBACKBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_7)) {
				return StateConstant.REPORTTYPE_FLAG_AMOUNTBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_4)) {
				return StateConstant.REPORTTYPE_FLAG_TRSHAREBILL;
			}
		}
		return null;

	}

	/**
	 * ���ݱ������͵õ��ļ�����
	 * 
	 * @return
	 */
	public static String getExpFileNameByBillType(TrIncomedayrptDto idto,
			String billType) {
		String filetype = getExpFileBillType(idto, billType);
		String filename = "s" + idto.getStrecode() + "_" + filetype
				+ idto.getSbelongflag();
		if (idto.getSbudgettype().equals(StateConstant.BudgetType_OUT)) {
			filename += StateConstant.BudgetType_OUT;
		}
		filename += idto.getSrptdate() + ".txt";
		return filename;
	}
	
	
	
	/**
	 * ���ݱ������͵õ��ļ�����
	 * 
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getExpFileNameByBillTypeForGZ(TrIncomedayrptDto idto,
			String billType,Map map) throws ITFEBizException {
		String filetype = getExpFileBillType(idto, billType);
		String filename = "s" + idto.getStrecode() + "_" + filetype
				+ idto.getSbelongflag();
		if (idto.getSbudgettype().equals(StateConstant.BudgetType_OUT)) {
			filename += StateConstant.BudgetType_OUT;
		}
		if (null==idto.getStaxorgcode()) {
			filename += idto.getSrptdate()+"_0000000000.txt";
		}else if (map.containsKey(idto.getStaxorgcode())) {
			String staxorgcode =(String) map.get(idto.getStaxorgcode());
			filename += idto.getSrptdate() +"_"+staxorgcode+".txt";
		}else {
			throw new ITFEBizException("�������" + idto.getStrecode()+ "��TCBS���ջ��ش��루" + idto.getStaxorgcode()+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
		}
		return filename;
	}

	/**
	 * ���ݱ������͵õ��ļ������е�ҵ������
	 * 
	 * @return
	 */
	public static String getExpFileBillType(TrIncomedayrptDto idto,
			String billType) {
		if (billType.equals(StateConstant.REPORTTYPE_5)) {
			return StateConstant.ReportName_c;
		} else if (billType.equals(StateConstant.REPORTTYPE_6)) {
			return StateConstant.ReportName_7;
		} else if (billType.equals(StateConstant.REPORTTYPE_7)) {
			return StateConstant.ReportName_0;
		} else if (billType.equals(StateConstant.REPORTTYPE_8)) {
			return StateConstant.ReportName_6;
		} else if (billType.equals(StateConstant.REPORTTYPE_9)) {
			return StateConstant.ReportName_8;
		} else if (billType.equals(StateConstant.REPORTTYPE_10)) {
			return StateConstant.ReportName_z;
		} else if (billType.equals(StateConstant.REPORTTYPE_11)) {
			return StateConstant.ReportName_t;
		} else if (billType.equals(StateConstant.REPORTTYPE_12)) {
			return StateConstant.ReportName_g;
		} else {
			return billType;
		}

	}
	/**
	 * ��ȡ�ϲ�ǰ�ͺϲ������ջ��ش���
	 * @param afterTaxorg�ϲ������ջ��ش���
	 * @return �ϲ�ǰ�ͺϲ������ջ��ش��뼯��
	 * @throws ITFEBizException 
	 */
	public static List<String> getTaxorgList(String trecode,String afterTaxorg) throws ITFEBizException{
		List<String> taxorglist=new ArrayList<String>();
		try {
			HashMap<String, TsTreasuryDto> mapTreInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String sbookorgcode = mapTreInfo.get(trecode).getSorgcode();
			TdTaxorgMergerDto tmdto=new TdTaxorgMergerDto();
			tmdto.setSbookorgcode(sbookorgcode);
			tmdto.setSaftertaxorgcode(afterTaxorg);
			taxorglist.add(afterTaxorg);
			List<TdTaxorgMergerDto> tmdtos=CommonFacade.getODB().findRsByDto(tmdto);
			if(tmdtos!=null&&tmdtos.size()>0){
				for(TdTaxorgMergerDto tmpdto:tmdtos){
					taxorglist.add(tmpdto.getSpretaxorgcode());
				}
			}
		} catch (Exception e) {
			log.error("��ȡ���ջ��غϲ����������쳣��"+e.getMessage());
			throw new ITFEBizException("��ȡ���ջ��غϲ����������쳣��"+e.getMessage());
		}
		return taxorglist;
	}
	/**
	 * ��ȡ�ϲ�ǰ�ͺϲ������ջ��ش���
	 * @param afterTaxorg�������
	 * @return �ϲ�ǰ�ͺϲ������ջ��ش��뼯��
	 * @throws ITFEBizException 
	 */
	public static List<String> getTaxorgListByTrecode(String trecode,String staxorgcode) throws ITFEBizException{
		List<String> taxorglist=new ArrayList<String>();
		try {
			HashMap<String, TsTreasuryDto> mapTreInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String sbookorgcode = mapTreInfo.get(trecode).getSorgcode();
			TsTaxorgDto tstaxorgdto=new TsTaxorgDto();
			tstaxorgdto.setStrecode(trecode);
			tstaxorgdto.setSorgcode(sbookorgcode);
			if(staxorgcode!=null)
				tstaxorgdto.setStaxprop(staxorgcode.substring(0, 1));
			List<TsTaxorgDto> tmdtos=CommonFacade.getODB().findRsByDto(tstaxorgdto);
			if(tmdtos!=null&&tmdtos.size()>0){
				for(TsTaxorgDto tmptaxorg:tmdtos){
					String taxorgcode=tmptaxorg.getStaxorgcode();
					taxorglist.addAll(getTaxorgList(trecode,taxorgcode));
				}
			}
		} catch (Exception e) {
			log.error("��ȡ���ջ��غϲ����������쳣��"+e.getMessage());
			throw new ITFEBizException("��ȡ���ջ��غϲ����������쳣��"+e.getMessage());
		}
		return taxorglist;
	}
	
	/**
	 * �޸�SQL�������ջ��ش�������
	 */
	public String alertSqlStringWithTaxorg(String strecode,String staxorg,String sql) {
		//����ǲ������ջ��أ�������
		if(!staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)&& !staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)) {
			if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {
				sql = sql + " and S_TAXORGCODE IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
				+ strecode + "' AND S_TAXPROP='"
				+ staxorg.substring(0, 1) + "' ) ";
			}else {
				sql = sql + " and S_TAXORGCODE='"+staxorg+"' ";
			}
		}
		return sql;
	}
	
	public static List<TsTreasuryDto> getSubTreCode(TsTreasuryDto dto) throws ITFEBizException 
	{
		List<TsTreasuryDto> trelist = null;
		try{
			if(dto.getStrecode()==null||"".equals(dto.getStrecode().trim()))
				return trelist;
			else
			{
				trelist = new ArrayList<TsTreasuryDto>();
				TsTreasuryDto finddto = new TsTreasuryDto();
				finddto.setSgoverntrecode(dto.getStrecode());
				trelist.add(dto);
				List<TsTreasuryDto> templist = CommonFacade.getODB().findRsByDto(finddto);
				if(templist!=null&&templist.size()>0)
				{
					trelist.addAll(templist);
					TsTreasuryDto tempdto = null;
					for(int i=0;i<templist.size();i++)
					{
						tempdto = templist.get(i);
						if(!tempdto.getStrecode().equals(tempdto.getSgoverntrecode()))
							trelist.addAll(getSubTreCode(tempdto));
					}
				}	
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		} catch (ValidateException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		}
		return trelist;
	}
	public static List<TsTreasuryDto> getSubTreCode(String orgCode) throws ITFEBizException 
	{
		List<TsTreasuryDto> returnList = null;
		try{
			if(orgCode==null||"".equals(orgCode.trim()))
				return returnList;
			else
			{
				List<TsTreasuryDto> trelist = new ArrayList<TsTreasuryDto>();
				returnList = new ArrayList<TsTreasuryDto>();
				TsTreasuryDto finddto = new TsTreasuryDto();
				finddto.setSorgcode(orgCode);
				List<TsTreasuryDto> templist = CommonFacade.getODB().findRsByDto(finddto);
				if(templist!=null&&templist.size()>0)
				{
					trelist.addAll(templist);
					for(int i=0;i<templist.size();i++)
						trelist.addAll(getSubTreCode(templist.get(i)));
				}
				Set<String> tmpTreSet = new HashSet<String>();
				if(trelist!=null&&trelist.size()>0)
				{
					for(int i=0;i<trelist.size();i++)
					{
						if(tmpTreSet.add(trelist.get(i).getStrecode()))
							returnList.add(trelist.get(i));
					}
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		} catch (ValidateException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		}
		return returnList;
	}
}