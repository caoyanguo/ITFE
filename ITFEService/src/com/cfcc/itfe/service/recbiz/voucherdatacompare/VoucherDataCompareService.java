package com.cfcc.itfe.service.recbiz.voucherdatacompare;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbackSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbacklistSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbanklistSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherCompareDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.sxservice.VoucherReadShanXiService;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author Administrator
 * @time   13-10-23 15:30:16
 * codecomment: 
 */

public class VoucherDataCompareService extends AbstractVoucherDataCompareService {
	private static Log log = LogFactory.getLog(VoucherDataCompareService.class);	
	private  Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
	private  VoucherReadShanXiService voucherReadShanXiService = (VoucherReadShanXiService) ContextFactory.getApplicationContext().getBean("VoucherReadShanXiService");

	/**
	 * ƾ֤�ȶ�
	 * @author sunyan
	 * @generated
	 * @param checkList
	 * @throws ITFEBizException	 
	 */
    public MulitTableDto voucherDataCompare(List checkList) throws ITFEBizException {
    	MulitTableDto mulit = new MulitTableDto();
    	Integer right = 0;
    	Integer wrong = 0;
    	TvVoucherinfoSxDto dto = (TvVoucherinfoSxDto)checkList.get(0);
    	String vtCode = dto.getSvtcode();
    	SQLExecutor sqlExce = null;
    	String selectczsql = "";
    	String selectpzksql = "";
    	if(MsgConstant.VOUCHER_NO_5207.equals(vtCode)){//ʵ���ʽ���ʱ���ջ���ƾ֤�ദ��
    		selectczsql = "SELECT a.S_ID AS ID, "  
    			//��ȱ������������(���ȶ�)
    			+ "a.S_OFYEAR AS STYEAR, "
    			//��ȱƾ֤���ͱ��(���ȶ�)
    			+ "a.S_COMMITDATE AS VOUDATE, "  // ί������
    			+ "a.S_TAXTICKETNO AS VOUCHERNO, "  // ˰Ʊ����
    			+ "a.S_TRECODE AS TRECODE, " //�����������
    			+ "a.S_PAYUNIT AS FINORGCODE, " //��Ʊ��λ
    			+ "a.N_MONEY AS PAYAMT, " //���׽��
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_RECACCT AS PAYEEACCTNO, " //�տ����ʺ�
    			+ "a.S_RECNAME AS PAYEEACCTNAME, " //�տ�������
    			+ "a.S_RECBANKNAME AS PAYEEACCTBANKNAME, " //�տ���������
    			+ "a.S_PAYERACCT AS PAYACCTNO, " //�������ʺ�
    			+ "a.S_PAYERNAME AS PAYACCTNAME, " //����������
    			+ "a.S_PAYERBANKNAME AS PAYACCTBANKNAME, " //������������
    			//��ȱ�������б��룬�����������ƣ����������к�(���ȶ�)
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_AGENCYCODE AS SUPDEPCODE, "
    			+ "b.S_AGENCYNAME AS SUPDEPNAME, "
    			+ "b.S_FUNSUBJECTCODE AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME AS EXPFUNCNAME, "
    			//+ "b.S_EXPFUNCNAME3 AS EXPFUNCNAME3, " (���ȶ�)
    			+ "b.S_PROCATCODE AS PROCATCODE, "
    			+ "b.S_PROCATNAME AS PROCATNAME, "
    			+ "b.N_MONEY AS SUBPAYAMT "
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4 "
    		+ "FROM TV_PAYOUTMSGMAIN_SX a, TV_PAYOUTMSGSUB_SX b, TV_VOUCHERINFO_SX tvsx "
    	    + "WHERE a.S_TAXTICKETNO= ? AND a.S_COMMITDATE = ? AND a.S_TRECODE = ? AND a.S_BIZNO=b.S_BIZNO AND a.S_BIZNO = tvsx.S_DEALNO AND tvsx.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    			
    			
    		selectpzksql = "SELECT a.S_ID AS ID, "  
    			//��ȱ������������(���ȶ�)
    			+ "a.S_OFYEAR AS STYEAR, "
    			//��ȱƾ֤���ͱ��(���ȶ�)
    			+ "a.S_COMMITDATE AS VOUDATE, "  // ί������
    			+ "a.S_TAXTICKETNO AS VOUCHERNO, "  // ˰Ʊ����
    			+ "a.S_TRECODE AS TRECODE, " //�����������
    			+ "a.S_PAYUNIT AS FINORGCODE, " //��Ʊ��λ
    			+ "a.N_MONEY AS PAYAMT, " //���׽��
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_RECACCT AS PAYEEACCTNO, " //�տ����ʺ�
    			+ "a.S_RECNAME AS PAYEEACCTNAME, " //�տ�������
    			//�տ�����hanghao  payeebankno
    			+ "a.S_RECBANKNAME AS PAYEEACCTBANKNAME, " //�տ���������
    			+ "a.S_PAYERACCT AS PAYACCTNO, " //�������ʺ�
    			+ "a.S_PAYERNAME AS PAYACCTNAME, " //����������
    			+ "a.S_PAYERBANKNAME AS PAYACCTBANKNAME, " //������������
    			//��ȱ�������б��룬�����������ƣ����������к�(���ȶ�)
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_AGENCYCODE AS SUPDEPCODE, "
    			+ "b.S_AGENCYNAME AS SUPDEPNAME, "
    			+ "b.S_FUNSUBJECTCODE AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME AS EXPFUNCNAME, "
    			//+ "b.S_EXPFUNCNAME3 AS EXPFUNCNAME3, "(���ȶ�)
    			+ "b.S_PROCATCODE AS PROCATCODE, "
    			+ "b.S_PROCATNAME AS PROCATNAME, "
    			+ "b.N_MONEY AS SUBPAYAMT "
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4 "
		    + "FROM TV_PAYOUTMSGMAIN a, TV_PAYOUTMSGSUB b, TV_VOUCHERINFO tv "
		    + "WHERE a.S_TAXTICKETNO= ? AND a.S_COMMITDATE = ? AND a.S_TRECODE = ? AND a.S_BIZNO=b.S_BIZNO AND a.S_BIZNO = tv.S_DEALNO AND tv.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    	}else if(MsgConstant.VOUCHER_NO_2301.equals(vtCode)) //���뻮��ƾ֤�ص�
    	{
    		selectczsql = "SELECT a.S_ID AS ID, " 
    			+ "a.S_ADMDIVCODE AS ADMDIVCODE, "
    			+ "a.S_OFYEAR AS STYEAR, "
    			+ "a.S_VTCODE AS VTCODE, "
    			+ "VARCHAR(a.D_VOUDATE) AS VOUDATE, "
    			+ "a.S_VOUNO AS VOUCHERNO, "
    			+ "a.S_TRECODE AS TRECODE, "
    			+ "a.S_FINORGCODE AS FINORGCODE, "
    			+ "a.S_XPAYAMT AS PAYAMT, "
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_PAYEEACCT AS PAYEEACCTNO, "
    			+ "a.S_PAYEENAME AS PAYEEACCTNAME, "
    			+ "a.S_AGENTACCTBANKNAME AS PAYEEACCTBANKNAME, "
    			+ "a.S_PAYERACCT AS PAYACCTNO, "
    			+ "a.S_PAYERNAME AS PAYACCTNAME, "
    			+ "a.S_CLEARACCTBANKNAME AS PAYACCTBANKNAME, "
    			//��ȱ�������б��� (���ȶ�)
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_AGENTBNKCODE AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_BDGORGCODE AS SUPDEPCODE, "
    			+ "b.S_SUPDEPNAME AS SUPDEPNAME, "
    			+ "b.S_FUNCBDGSBTCODE AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME AS EXPFUNCNAME, "
    			//��ȱ֧�����ܷ����Ŀ�����ƣ���֧������룬��֧��������(���ȶ�)
    			+ "b.F_AMT AS SUBPAYAMT "
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4 "
		    + "FROM TV_PAYRECKBANK_SX a, TV_PAYRECKBANKLIST_SX b, TV_VOUCHERINFO_SX tvsx "
		    + "WHERE a.S_VOUNO= ? AND VARCHAR(a.D_ENTRUSTDATE) = ? AND a.S_TRECODE = ? AND a.S_AGENTBNKCODE = ? AND a.I_VOUSRLNO=b.I_VOUSRLNO AND a.I_VOUSRLNO = tvsx.S_DEALNO AND tvsx.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    		
    		selectpzksql = "SELECT a.S_ID AS ID, " 
    			+ "a.S_ADMDIVCODE AS ADMDIVCODE, "
    			+ "a.S_OFYEAR AS STYEAR, "
    			+ "a.S_VTCODE AS VTCODE, "
    			+ "VARCHAR(a.D_VOUDATE) AS VOUDATE, "
    			+ "a.S_VOUNO AS VOUCHERNO, "
    			+ "a.S_TRECODE AS TRECODE, "
    			+ "a.S_FINORGCODE AS FINORGCODE, "
    			+ "a.S_XPAYAMT AS PAYAMT, "
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_PAYEEACCT AS PAYEEACCTNO, "
    			+ "a.S_PAYEENAME AS PAYEEACCTNAME, "
    			+ "a.S_AGENTACCTBANKNAME AS PAYEEACCTBANKNAME, "
    			+ "a.S_PAYERACCT AS PAYACCTNO, "
    			+ "a.S_PAYERNAME AS PAYACCTNAME, "
    			+ "a.S_CLEARACCTBANKNAME AS PAYACCTBANKNAME, "
    			//��ȱ�������б���(���ȶ�)
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_AGENTBNKCODE AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_BDGORGCODE AS SUPDEPCODE, "
    			+ "b.S_SUPDEPNAME AS SUPDEPNAME, "
    			+ "b.S_FUNCBDGSBTCODE AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME AS EXPFUNCNAME, "
    			//��ȱ֧�����ܷ����Ŀ�����ƣ���֧������룬��֧��������(���ȶ�)
    			+ "b.F_AMT AS SUBPAYAMT "
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4 "
		    + "FROM TV_PAYRECK_BANK a, TV_PAYRECK_BANK_LIST b, TV_VOUCHERINFO tv "
		    + "WHERE a.S_VOUNO= ? AND VARCHAR(a.D_ENTRUSTDATE) = ? AND a.S_TRECODE = ? AND a.S_AGENTBNKCODE = ? AND a.I_VOUSRLNO=b.I_VOUSRLNO AND a.I_VOUSRLNO = tv.S_DEALNO AND tv.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    	}else if(MsgConstant.VOUCHER_NO_2302.equals(vtCode))//�����˿�ƾ֤�ص�
    	{
    		selectczsql = "SELECT a.S_ID AS ID, " 
    			+ "a.S_ADMDIVCODE AS ADMDIVCODE, "
    			+ "a.S_OFYEAR AS STYEAR, "
    			+ "a.S_VTCODE AS VTCODE, "
    			+ "VARCHAR(a.D_VOUDATE) AS VOUDATE, "
    			+ "a.S_VOUNO AS VOUCHERNO, "
    			+ "a.S_TRECODE AS TRECODE, "
    			+ "a.S_FINORGCODE AS FINORGCODE, "
    			+ "a.S_XPAYAMT AS PAYAMT, "
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_PAYEEACCT AS PAYEEACCTNO, "
    			+ "a.S_PAYEENAME AS PAYEEACCTNAME, "
    			+ "a.S_AGENTACCTBANKNAME AS PAYEEACCTBANKNAME, "
    			+ "a.S_PAYERACCT AS PAYACCTNO, "
    			+ "a.S_PAYERNAME AS PAYACCTNAME, "
    			+ "a.S_CLEARACCTBANKNAME AS PAYACCTBANKNAME, "
    			//��ȱ�������б��� (���ȶ�)
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_AGENTBNKCODE AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_BDGORGCODE AS SUPDEPCODE, "
    			+ "b.S_SUPDEPNAME AS SUPDEPNAME, "
    			+ "b.S_FUNCBDGSBTCODE AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME AS EXPFUNCNAME, "
    			//��ȱ֧�����ܷ����Ŀ�����ƣ���֧������룬��֧��������(���ȶ�)
    			+ "b.F_AMT AS SUBPAYAMT "
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4 "
		    + "FROM TV_PAYRECKBANKBACK_SX a, TV_PAYRECKBANKBACKLIST_SX b, TV_VOUCHERINFO_SX tvsx "
		    + "WHERE a.S_VOUNO= ? AND VARCHAR(a.D_ENTRUSTDATE) = ? AND a.S_TRECODE = ? AND a.S_AGENTBNKCODE = ? AND a.I_VOUSRLNO=b.I_VOUSRLNO AND a.I_VOUSRLNO = tvsx.S_DEALNO AND tvsx.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    		
    		selectpzksql = "SELECT a.S_ID AS ID, " 
    			+ "a.S_ADMDIVCODE AS ADMDIVCODE, "
    			+ "a.S_OFYEAR AS STYEAR, "
    			+ "a.S_VTCODE AS VTCODE, "
    			+ "VARCHAR(a.D_VOUDATE) AS VOUDATE, "
    			+ "a.S_VOUNO AS VOUCHERNO, "
    			+ "a.S_TRECODE AS TRECODE, "
    			+ "a.S_FINORGCODE AS FINORGCODE, "
    			+ "a.S_XPAYAMT AS PAYAMT, "
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_PAYEEACCT AS PAYEEACCTNO, "
    			+ "a.S_PAYEENAME AS PAYEEACCTNAME, "
    			+ "a.S_AGENTACCTBANKNAME AS PAYEEACCTBANKNAME, "
    			+ "a.S_PAYERACCT AS PAYACCTNO, "
    			+ "a.S_PAYERNAME AS PAYACCTNAME, "
    			+ "a.S_CLEARACCTBANKNAME AS PAYACCTBANKNAME, "
    			//��ȱ�������б���(���ȶ�)
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_AGENTBNKCODE AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_BDGORGCODE AS SUPDEPCODE, "
    			+ "b.S_SUPDEPNAME AS SUPDEPNAME, "
    			+ "b.S_FUNCBDGSBTCODE AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME AS EXPFUNCNAME, "
    			//��ȱ֧�����ܷ����Ŀ�����ƣ���֧������룬��֧��������(���ȶ�)
    			+ "b.F_AMT AS SUBPAYAMT "
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4 "
		    + "FROM TV_PAYRECK_BANK_BACK a, TV_PAYRECK_BANK_BACK_LIST b, TV_VOUCHERINFO tv "
		    + "WHERE a.S_VOUNO= ? AND VARCHAR(a.D_ENTRUSTDATE) = ? AND a.S_TRECODE = ? AND a.S_AGENTBNKCODE = ? AND a.I_VOUSRLNO=b.I_VOUSRLNO AND a.I_VOUSRLNO = tv.S_DEALNO AND tv.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    	}else if(MsgConstant.VOUCHER_NO_5108.equals(vtCode))//ֱ��֧��������֪ͨ��
    	{
    		selectczsql = "SELECT a.S_ID AS ID, "
    			//��ȱ������������(���ȶ�)
    			+ "a.S_OFYEAR AS STYEAR, "
    			//��ȱƾ֤���ͱ��(���ȶ�)
    			+ "a.S_COMMITDATE AS VOUDATE, "
    			+ "a.S_TAXTICKETNO AS VOUCHERNO, "
    			+ "a.N_MONEY AS PLANAMT, " //���׽��(�ϼ������Ƚ��)
    			//��ȱ�ƻ��·ݣ���ȱһ��Ԥ�㵥λ����(���ȶ�)
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			//��ȱ�������б��룬������������(���ȶ�)
    			+ "a.S_PAYBANKCODE AS PAYBANKCODE, "
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_PAYBANKNO AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_AGENCYCODE AS SUPDEPCODE, "
    			+ "b.S_AGENCYNAME AS SUPDEPNAME, "
    			+ "b.S_EXPFUNCCODE1 AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME1 AS EXPFUNCNAME, "
    			+ "b.N_MONEY AS SUBPLANAMT "//������(�����Ƚ��)
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4  "
		    + "FROM TV_DIRECTPAYMSGMAIN_SX a, TV_DIRECTPAYMSGSUB_SX b, TV_VOUCHERINFO_SX tvsx "
		    + "WHERE a.S_TAXTICKETNO= ? AND a.S_COMMITDATE = ? AND a.S_TRECODE = ? AND a.I_VOUSRLNO=b.I_VOUSRLNO AND a.I_VOUSRLNO = tvsx.S_DEALNO AND tvsx.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    		
    		selectpzksql = "SELECT a.S_ID AS ID, "
    			//��ȱ������������(���ȶ�)
    			+ "a.S_OFYEAR AS STYEAR, "
    			//��ȱƾ֤���ͱ��(���ȶ�)
    			+ "a.S_COMMITDATE AS VOUDATE, "
    			+ "a.S_TAXTICKETNO AS VOUCHERNO, "
    			+ "a.N_MONEY AS PLANAMT, " //���׽��(�ϼ������Ƚ��)
    			//��ȱ�ƻ��·ݣ���ȱһ��Ԥ�㵥λ����(���ȶ�)
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			//��ȱ�������б��룬������������(���ȶ�)
    			+ "a.S_PAYBANKCODE AS PAYBANKCODE, "
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_PAYBANKNO AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_AGENCYCODE AS SUPDEPCODE, "
    			+ "b.S_AGENCYNAME AS SUPDEPNAME, "
    			+ "b.S_EXPFUNCCODE1 AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME1 AS EXPFUNCNAME, "
    			+ "b.N_MONEY AS SUBPLANAMT "//������(�����Ƚ��)
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4  "
		    + "FROM TV_DIRECTPAYMSGMAIN a, TV_DIRECTPAYMSGSUB b, TV_VOUCHERINFO tv "
		    + "WHERE a.S_TAXTICKETNO= ? AND a.S_COMMITDATE = ? AND a.S_TRECODE = ? AND a.I_VOUSRLNO=b.I_VOUSRLNO AND a.I_VOUSRLNO = tv.S_DEALNO AND tv.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    	}else if(MsgConstant.VOUCHER_NO_5106.equals(vtCode))//��Ȩ֧��������֪ͨ��
    	{
    		selectczsql = "SELECT a.S_ID AS ID, " 
    			//��ȱ������������(���ȶ�)
    			+ "a.S_OFYEAR AS STYEAR, "
    			//��ȱƾ֤���ͱ��(���ȶ�)
    			+ "a.S_COMMITDATE AS VOUDATE, "
//    			+ "a.S_PACKAGETICKETNO AS VOUCHERNO, "
    			+ "a.N_MONEY AS PLANAMT, " //���׽��(�ϼ������Ƚ��)
    			+ "a.S_OFMONTH AS SETMONTH, "
    			+ "a.S_DEPTNUM AS DEPTNUM, "
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_CLEARBANKCODE AS CLEARBANKCODE, "
    			+ "a.S_CLEARBANKNAME AS CLEARBANKNAME, "
    			+ "a.S_PAYBANKCODE AS PAYBANKCODE, "
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_PAYBANKNO AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_BUDGETUNITCODE AS SUPDEPCODE, "
    			+ "b.S_SUPDEPNAME AS SUPDEPNAME, "
    			+ "b.S_EXPFUNCCODE1 AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME1 AS EXPFUNCNAME, "
    			+ "b.N_MONEY AS SUBPLANAMT "//������(�����Ƚ��)
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4  "
		    + "FROM TV_GRANTPAYMSGMAIN_SX a, TV_GRANTPAYMSGSUB_SX b, TV_VOUCHERINFO_SX tvsx "
		    + "WHERE a.I_VOUSRLNO = ? AND a.I_VOUSRLNO = b.I_VOUSRLNO AND a.I_VOUSRLNO = tvsx.S_DEALNO AND tvsx.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    		
    		selectpzksql = "SELECT a.S_ID AS ID, " 
    			//��ȱ������������(���ȶ�)
    			+ "a.S_OFYEAR AS STYEAR, "
    			//��ȱƾ֤���ͱ��(���ȶ�)
    			+ "a.S_COMMITDATE AS VOUDATE, "
//    			+ "a.S_PACKAGETICKETNO AS VOUCHERNO, "
    			+ "a.N_MONEY AS PLANAMT, " //���׽��(�ϼ������Ƚ��)
    			+ "a.S_OFMONTH AS SETMONTH, "
    			+ "a.S_DEPTNUM AS DEPTNUM, "
    			+ "a.S_FUNDTYPECODE AS FUNDTYPECODE, "
    			+ "a.S_FUNDTYPENAME AS FUNDTYPENAME, "
    			+ "a.S_CLEARBANKCODE AS CLEARBANKCODE, "
    			+ "a.S_CLEARBANKNAME AS CLEARBANKNAME, "
    			+ "a.S_PAYBANKCODE AS PAYBANKCODE, "
    			+ "a.S_PAYBANKNAME AS PAYBANKNAME, "
    			+ "a.S_PAYBANKNO AS PAYBANKNO, "
//    			+ "a.S_HOLD1 AS HOLD1, "
//    			+ "a.S_HOLD2 AS HOLD2, "
    			+ "b.S_BUDGETUNITCODE AS SUPDEPCODE, "
    			+ "b.S_SUPDEPNAME AS SUPDEPNAME, "
    			+ "b.S_EXPFUNCCODE1 AS EXPFUNCCODE, "
    			+ "b.S_EXPFUNCNAME1 AS EXPFUNCNAME, "
    			+ "b.N_MONEY AS SUBPLANAMT "//������(�����Ƚ��)
//    			+ "b.S_HOLD1 AS SUBHOLD1, "
//    			+ "b.S_HOLD2 AS SUBHOLD2, "
//    			+ "b.S_HOLD3 AS SUBHOLD3, "
//    			+ "b.S_HOLD4 AS SUBHOLD4  "
		    + "FROM TV_GRANTPAYMSGMAIN a, TV_GRANTPAYMSGSUB b, TV_VOUCHERINFO tv "
		    + "WHERE a.I_VOUSRLNO = "
		    + "(SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VOUCHERNO = ? AND S_CREATDATE = ? AND S_TRECODE = ?) "
		    + "AND a.I_VOUSRLNO=b.I_VOUSRLNO AND a.I_VOUSRLNO = tv.S_DEALNO AND tv.S_STATUS ='"+DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"'";
    	}
    	try {
			sqlExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			for(int i = 0; i < checkList.size(); i++){
				//��ѯ����ϵͳ��ƾ֤��Ϣ
				TvVoucherinfoSxDto curentdto = (TvVoucherinfoSxDto)checkList.get(i);
				//������ܵı��Ĳ���У��ɹ��򲻽��бȶ�
				if(!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(curentdto.getSstatus()))
				{
					throw new ITFEBizException("ֻ��״̬ΪУ��ɹ��ļ�¼�ſ��Խ��бȶԣ�");
				}
				if(curentdto.getSdemo() != null)
				{
					if(curentdto.getSdemo().indexOf("���ݱȶ�") >= 0)
					{
						throw new ITFEBizException("ֻ��δ���й��ȶԵļ�¼�ſ��Խ��бȶԣ�");
					}
				}
				if(MsgConstant.VOUCHER_NO_2301.equals(vtCode) || MsgConstant.VOUCHER_NO_2302.equals(vtCode)) 
				{
					sqlExce.addParam(curentdto.getSvoucherno());
					sqlExce.addParam(TimeFacade.formatDate(TimeFacade.parseDate(curentdto.getScreatdate()),"yyyy-MM-dd")); 
					sqlExce.addParam(curentdto.getStrecode());
					sqlExce.addParam(curentdto.getSpaybankcode());
				}else if(MsgConstant.VOUCHER_NO_5106.equals(vtCode))
				{
					sqlExce.addParam(curentdto.getSdealno());
				}else
				{
					sqlExce.addParam(curentdto.getSvoucherno());
					sqlExce.addParam(curentdto.getScreatdate());
				    sqlExce.addParam(curentdto.getStrecode());
				}
				SQLResults czresult = sqlExce.runQuery(selectczsql, VoucherCompareDto.class);
				List<VoucherCompareDto> czlist = (List<VoucherCompareDto>) czresult.getDtoCollection();
				//��ѯƾ֤���ƾ֤��Ϣ
				sqlExce.clearParams();
				if(MsgConstant.VOUCHER_NO_2301.equals(vtCode) || MsgConstant.VOUCHER_NO_2302.equals(vtCode)) 
				{
					sqlExce.addParam(curentdto.getSvoucherno());
					sqlExce.addParam(TimeFacade.formatDate(TimeFacade.parseDate(curentdto.getScreatdate()),"yyyy-MM-dd")); 
					sqlExce.addParam(curentdto.getStrecode());
					sqlExce.addParam(curentdto.getSpaybankcode());
				}else if(MsgConstant.VOUCHER_NO_5106.equals(vtCode))
				{
					sqlExce.addParam(curentdto.getSvoucherno());
					sqlExce.addParam(curentdto.getScreatdate());
				    sqlExce.addParam(curentdto.getStrecode());
				}else
				{
					sqlExce.addParam(curentdto.getSvoucherno());
					sqlExce.addParam(curentdto.getScreatdate());
				    sqlExce.addParam(curentdto.getStrecode());
				}
				SQLResults pzkresult = sqlExce.runQuery(selectpzksql, VoucherCompareDto.class);
				List<VoucherCompareDto> pzklist = (List<VoucherCompareDto>) pzkresult.getDtoCollection();
				TvVoucherinfoDto dtos = null;
				if(pzklist.size() == 0)
				{
					TvVoucherinfoSxDto vsxDto = curentdto;
					vsxDto.setSreturnerrmsg("���ݱȶ�ʧ�ܣ�ƾ֤��û�ж�ȡ��ƾ֤���Ϊ"+curentdto.getSvoucherno()+"����Ϣ");
					DatabaseFacade.getDb().update(vsxDto);
					wrong++;
					continue;
				}else
				{
					TvVoucherinfoDto queryDto = new TvVoucherinfoDto();
					queryDto.setSadmdivcode(curentdto.getSadmdivcode());
					queryDto.setSvtcode(curentdto.getSvtcode());
					queryDto.setSvoucherno(curentdto.getSvoucherno());
					queryDto.setScreatdate(curentdto.getScreatdate());
					List<TvVoucherinfoDto> dtoslist = null;
					try 
					{
					  dtoslist = CommonFacade.getODB().findRsByDto(queryDto);
					} catch (ValidateException e) 
					{
						log.error("��ѯƾ֤����������Ϣ�����쳣:"+e);
						throw new ITFEBizException("��ѯƾ֤����������Ϣ�����쳣��");
					}
					
					dtos = dtoslist.get(0);
				}
				
				//�Ƚ�����
				String returnmessage = compareList(czlist, pzklist);
				System.out.println(returnmessage);
				
				if(returnmessage == null){
					//У��ɹ�
					TvVoucherinfoDto vDto = dtos;
					vDto.setSdemo("���ݱȶԳɹ�");
					vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
					vDto.setSreturnerrmsg(null);
					DatabaseFacade.getDb().update(vDto);
					
					TvVoucherinfoSxDto vsxDto = curentdto;
					vsxDto.setSdemo("���ݱȶԳɹ�");
					vsxDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
					vsxDto.setSreturnerrmsg(null);
					DatabaseFacade.getDb().update(vsxDto);
					right++;
				}else{
					//У��ʧ��
					TvVoucherinfoDto vDto = dtos;
					vDto.setSdemo("���ݱȶ�ʧ��");
					vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_FAIL);
					vDto.setSreturnerrmsg(returnmessage);
					DatabaseFacade.getDb().update(vDto);
					
					TvVoucherinfoSxDto vsxDto = curentdto;
					vsxDto.setSdemo("���ݱȶ�ʧ��");
					vsxDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_FAIL);
					vsxDto.setSreturnerrmsg(returnmessage);
					DatabaseFacade.getDb().update(vsxDto);
					wrong++;
				}
			}
			String msg = "���ݱȶԣ��ɹ�����"+right+"  ʧ������"+wrong;
			List<String> msl = new ArrayList<String>();
			msl.add(msg);
			mulit.setErrorList(msl);
			return mulit;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯҵ�����Ϣ�����쳣��");
		}finally{
			if(sqlExce!=null){
				sqlExce.closeConnection();
			}
		}

    }

	/**
	 * ƾ֤�ص�
	 	 
	 * @generated
	 * @param succList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
    public Integer voucherReturnSuccess(List succList) throws ITFEBizException {
      return null;
    }

	/**
	 * ƾ֤У��
	 	 
	 * @generated
	 * @param checkList
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 * @author sunyan
	 */
    public Integer voucherVerify(List checkList) throws ITFEBizException {
    	int count = 0;
		List lists = new ArrayList();
		List list = null;
		try{
			for (TvVoucherinfoSxDto vDto : (List<TvVoucherinfoSxDto>) checkList) {
				list = new ArrayList();
				/**
				 * ����ƾ֤�б��ȡ ���� �ӱ�Ԥ�㵥λ�����б�
				 * 
				 */
				if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)) {	
					
						TvPayoutmsgmainSxDto mainDto = new TvPayoutmsgmainSxDto();
						TvPayoutmsgsubSxDto subDto = new TvPayoutmsgsubSxDto();
						List<TvPayoutmsgsubSxDto> subList = new ArrayList<TvPayoutmsgsubSxDto>();
						subDto.setSbizno(vDto.getSdealno());
						mainDto.setSbizno(vDto.getSdealno());
						// ����
						mainDto = (TvPayoutmsgmainSxDto) CommonFacade.getODB()
									.findRsByDto(mainDto).get(0);					
						subList = CommonFacade.getODB().findRsByDto(subDto);
						ArrayList<String> expFuncCodeList = new ArrayList<String>();
						// �ӱ�Ԥ�㵥λ�����б�
						for (TvPayoutmsgsubSxDto sDto : subList) {
							expFuncCodeList.add(sDto.getSfunsubjectcode());
							
						}
						list.add(mainDto);
						list.add(vDto);
						list.add(expFuncCodeList);
						list.add(subList);
						lists.add(list);
					
				} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)) {
						TvPayreckbankSxDto mainDto = new TvPayreckbankSxDto();
						TvPayreckbanklistSxDto subDto = new TvPayreckbanklistSxDto();
						List<TvPayreckbanklistSxDto> subList = new ArrayList<TvPayreckbanklistSxDto>();
						subDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						// ����
						mainDto = (TvPayreckbankSxDto) CommonFacade.getODB()
								.findRsByDto(mainDto).get(0);
						subList = CommonFacade.getODB().findRsByDto(subDto);
						ArrayList<String> expFuncCodeList = new ArrayList<String>();
						List agencyCodeList =  new ArrayList<String>();
						// �ӱ�Ԥ�㵥λ�����б�
						for (TvPayreckbanklistSxDto sDto : subList) {
							expFuncCodeList.add(sDto.getSfuncbdgsbtcode());
							agencyCodeList.add(mainDto.getStrecode()+sDto.getSbdgorgcode());
						}
						list.add(mainDto);
						list.add(vDto);
						list.add(expFuncCodeList);
						list.add(agencyCodeList);
						lists.add(list);
					
				} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)) {

						TvPayreckbankbackSxDto mainDto = new TvPayreckbankbackSxDto();
						TvPayreckbankbacklistSxDto subDto = new TvPayreckbankbacklistSxDto();
						List<TvPayreckbankbacklistSxDto> subList = new ArrayList<TvPayreckbankbacklistSxDto>();
						subDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						// ����
						mainDto = (TvPayreckbankbackSxDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
						subList = CommonFacade.getODB().findRsByDto(subDto);
						ArrayList<String> expFuncCodeList = new ArrayList<String>();
						List agencyCodeList =  new ArrayList<String>();//Ԥ�㵥λlist
						// �ӱ�Ԥ�㵥λ�����б�
						for (TvPayreckbankbacklistSxDto sDto : subList) {
							expFuncCodeList.add(sDto.getSfuncbdgsbtcode());
							agencyCodeList.add(mainDto.getStrecode()+sDto.getSbdgorgcode());
						}
						list.add(mainDto);
						list.add(vDto);
						list.add(expFuncCodeList);
						list.add(agencyCodeList);
						lists.add(list);				
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
					List<TvGrantpaymsgmainSxDto> mainDtoList = new ArrayList<TvGrantpaymsgmainSxDto>();
					TvGrantpaymsgmainSxDto mainDto = new TvGrantpaymsgmainSxDto();
					TvGrantpaymsgsubSxDto subDto = new TvGrantpaymsgsubSxDto();
					List<TvGrantpaymsgsubSxDto> subList = new ArrayList<TvGrantpaymsgsubSxDto>();
					mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					
					//���ܿ�Ŀ�����б�
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					// �ӱ�Ԥ�㵥λ�����б�
					List agencyCodeList =  new ArrayList<String>();
					// ����
					mainDtoList = CommonFacade.getODB().findRsByDto(mainDto);

					if (null!=mainDtoList &&  mainDtoList.size()> 0) {
						mainDto =mainDtoList.get(0);
					}
					//��Ȩ֧������漰��ƾ֤��֣�����Ҫ��ѯ��mainDtoList
					if(mainDtoList!=null&&mainDtoList.size()>0)
						mainDto = mainDtoList.get(0);
					for(TvGrantpaymsgmainSxDto tempMainDto:mainDtoList){
						subDto.setIvousrlno(tempMainDto.getIvousrlno());
						List<TvGrantpaymsgsubSxDto> tempSubDtoList = CommonFacade.getODB().findRsByDto(subDto);
						if(tempSubDtoList!=null&&tempSubDtoList.size()>0)
							subList.addAll(tempSubDtoList);
						for (TvGrantpaymsgsubSxDto sDto : tempSubDtoList) {
							agencyCodeList.add(mainDto.getStrecode()+sDto.getSbudgetunitcode());
							expFuncCodeList.add(sDto.getSfunsubjectcode());
						}
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);
					list.add(agencyCodeList);
					lists.add(list);
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {	
					TvDirectpaymsgmainSxDto mainDto = new TvDirectpaymsgmainSxDto();
					TvDirectpaymsgsubSxDto subDto = new TvDirectpaymsgsubSxDto();
					List<TvDirectpaymsgsubSxDto> subList = new ArrayList<TvDirectpaymsgsubSxDto>();
					subDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					// ����
					mainDto = (TvDirectpaymsgmainSxDto) CommonFacade.getODB()
								.findRsByDto(mainDto).get(0);					
					subList = CommonFacade.getODB().findRsByDto(subDto);
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					List agencyCodeList =  new ArrayList<String>();
					// �ӱ�Ԥ�㵥λ�����б�
					for (TvDirectpaymsgsubSxDto sDto : subList) {
						expFuncCodeList.add(sDto.getSfunsubjectcode());
						agencyCodeList.add(mainDto.getStrecode()+sDto.getSagencycode());
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);
					list.add(agencyCodeList);
					lists.add(list);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("����������Ϣ�쳣��",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("����������Ϣ�쳣��",e);
		}
		
		try {
			count = voucher.voucherVerifyForSX(lists, ((TvVoucherinfoSxDto) checkList
					.get(0)).getSvtcode());
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("ƾ֤У���쳣��",e);
		}
		return count;
    }

	/**
	 * ƾ֤��ȡ
	 	 
	 * @generated
	 * @param svtcode
	 * @param sorgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public Integer voucherRead(String svtcode, String sorgcode) throws ITFEBizException {
    	int count = 0;
    	VoucherReadShanXiService readservice= new VoucherReadShanXiService();
    	count = readservice.ReadVoucher(svtcode, sorgcode);
        return count;
    }
    
    /**
     * �Ƚ϶�ȡ����ϵͳ��������ƾ֤���ȡ������
     * @param czlist
     * @param pzklist
     * @return
     * @throws ITFEBizException 
     */
    public String compareList(List<VoucherCompareDto> czlist, List<VoucherCompareDto> pzklist) throws ITFEBizException{
    	VoucherCompareDto czdto = (VoucherCompareDto)czlist.get(0);
    	VoucherCompareDto pzkdto = (VoucherCompareDto)pzklist.get(0);
    	StringBuffer mainsb = new StringBuffer() ;
    	/**
    	 * �ȱȽ�������Ϣ
    	 */
    	Map<String,String> properties = new HashMap<String,String>();
//    	properties.put("id", "֪ͨ��ID");
    	properties.put("trecode", "�������");
    	properties.put("finorgcode", "��������");
    	properties.put("admdivcode", "������������");
    	properties.put("vtcode", "ƾ֤���ͱ��");
    	properties.put("voudate", "ƾ֤����");
    	properties.put("voucherno", "ƾ֤���");
    	properties.put("styear", "�������");
    	properties.put("setMonth", "�ƻ��·�");
    	properties.put("deptnum", "Ԥ�㵥λ����");
    	properties.put("fundtypecode", "�ʽ����ʱ���");
    	properties.put("fundtypename", "�ʽ���������");
    	properties.put("payacctno", "�����˺�");
    	properties.put("payacctname", "����������");
    	properties.put("payacctbankname", "������������");
    	properties.put("payeeacctno", "�տ����˺�");
    	properties.put("payeeacctname", "�տ�������");
    	properties.put("payeeacctbankname", "�տ���������");
    	properties.put("clearbankcode", "�������б���");
    	properties.put("clearbankname", "������������");
    	properties.put("paybankno", "���������к�");
    	properties.put("paybankcode", "�������б���");
    	properties.put("paybankname", "������������");
    	properties.put("payamt", "������");
    	properties.put("planamt", "�����Ƚ��");
//    	properties.put("hold1", "Ԥ���ֶ�1");
//    	properties.put("hold2", "Ԥ���ֶ�2");
    	
    	Iterator ite = properties.entrySet().iterator();
    	while(ite.hasNext())
    	{
			Map.Entry<String, String> entry = (Map.Entry<String, String>) ite.next();
			String key = entry.getKey().trim();
			String value = entry.getValue().trim();
			try 
			{
				Field fieldCZ = czdto.getClass().getDeclaredField(key);
				Field fieldPZ = pzkdto.getClass().getDeclaredField(key);
				fieldCZ.setAccessible(true);
				fieldPZ.setAccessible(true);
				Object ValueCZ = fieldCZ.get(czdto);
				Object ValuePZ = fieldPZ.get(pzkdto);
				if(ValueCZ != null && !"".equals(ValueCZ) && ValuePZ != null && !"".equals(ValuePZ))
				{
					if(ValueCZ.getClass() == String.class)
					{
						String cvStr = (String)ValueCZ;
						String pzStr = (String)ValuePZ;
						if(!cvStr.equals(pzStr))
						{
							mainsb.append(value+"����ͬ��");
						}
					}else if(ValueCZ.getClass() == BigDecimal.class)
					{
						BigDecimal cvNum = (BigDecimal)ValueCZ;
						BigDecimal pvNum = (BigDecimal)ValuePZ;
						if(cvNum.compareTo(pvNum) != 0)
						{
							mainsb.append(value+"����ͬ��");
						}
					}
				}else if(ValueCZ != null && !"".equals(ValueCZ) && ValuePZ == null 
						|| ValuePZ != null && !"".equals(ValuePZ) && ValueCZ == null)
				{
					mainsb.append(value+"����ͬ��");
				}
				
			} catch (SecurityException e)
			{
				log.error(e);
				throw new ITFEBizException("���ݱȶԳ����쳣��");
			} catch (NoSuchFieldException e) 
			{
				log.error(e);
				throw new ITFEBizException("���ݱȶԳ����쳣��");
			} catch (IllegalArgumentException e) {
				log.error(e);
				throw new ITFEBizException("���ݱȶԳ����쳣��");
			} catch (IllegalAccessException e) {
				log.error(e);
				throw new ITFEBizException("���ݱȶԳ����쳣��");
			} catch(Exception e)
			{
				log.error(e);
				throw new ITFEBizException(e.getMessage());
			}
		}
    	if(mainsb.toString().length() != 0){
    		return "����Ϣ�ȶ�ʧ�ܣ�" + mainsb.toString();
    	}
    	/**
    	 * ������Ϣ��ͬ���ٱȽ��ӱ���Ϣ
    	 */
    	StringBuffer subsb = new StringBuffer() ;
    	 List<VoucherCompareDto> temp = new ArrayList<VoucherCompareDto>();
		 for(int i=0; i < czlist.size(); i++){
			  temp.add(czlist.get(i));
		 }
		 temp.retainAll(pzklist);
		 czlist.removeAll(temp);
		 pzklist.removeAll(temp);
		 if(czlist.size() > 0){
			 subsb.append("�������ݱȶԲ��ɹ���¼��");
			 for(int i =0; i < czlist.size(); i++){
				 VoucherCompareDto currentdto = (VoucherCompareDto)czlist.get(i);
				 subsb.append("[");
				 subsb.append("Ԥ�㵥λ����:"+currentdto.getSupdepcode());
				 subsb.append(",");
				 subsb.append("Ԥ�㵥λ����:"+currentdto.getSupdepname());
				 subsb.append(",");
				 subsb.append("���ܷ����Ŀ����:"+currentdto.getExpfunccode());
				 subsb.append(",");
				 subsb.append("֧�����ܷ����Ŀ����:"+currentdto.getExpfuncname());
				 subsb.append(",");
				 subsb.append("��֧�������:"+currentdto.getProcatcode());
				 subsb.append(",");
				 subsb.append("��֧��������:"+currentdto.getProcatname());
				 subsb.append(",");
				 if(currentdto.getPayamt() != null)
				 {
					 subsb.append("������:"+currentdto.getPayamt());
				 }else if(currentdto.getPlanamt() != null)
				 {
					 subsb.append("�����Ƚ��:"+currentdto.getPlanamt());
				 }
				 subsb.append("]");
			 }
		 }
		 if(pzklist.size() > 0){
			 subsb.append("ƾ֤����ϸ�ȶԲ��ɹ���¼��");
			 for(int i =0; i < pzklist.size(); i++){
				 VoucherCompareDto currentdto = (VoucherCompareDto)pzklist.get(i);
				 subsb.append("[");
				 subsb.append("Ԥ�㵥λ����:"+currentdto.getSupdepcode());
				 subsb.append(",");
				 subsb.append("Ԥ�㵥λ����:"+currentdto.getSupdepname());
				 subsb.append(",");
				 subsb.append("���ܷ����Ŀ����:"+currentdto.getExpfunccode());
				 subsb.append(",");
				 subsb.append("֧�����ܷ����Ŀ����:"+currentdto.getExpfuncname());
				 subsb.append(",");
				 subsb.append("��֧�������:"+currentdto.getProcatcode());
				 subsb.append(",");
				 subsb.append("��֧��������:"+currentdto.getProcatname());
				 subsb.append(",");
				 if(currentdto.getPayamt() != null)
				 {
					 subsb.append("������:"+currentdto.getPayamt());
				 }else if(currentdto.getPlanamt() != null)
				 {
					 subsb.append("�����Ƚ��:"+currentdto.getPlanamt());
				 }
				 subsb.append("]");
			 }
		 }
		 if(subsb.toString().length() != 0){
			 return "��ϸ�ȶ�ʧ�ܣ�" + subsb.toString();
		 }else{
			 return null;
		 }
    }

	public void generateData(List checkList) throws ITFEBizException
	{
		for(int i = 0; i < checkList.size(); i++)
		{
			//����������
			TvVoucherinfoSxDto curentdto = (TvVoucherinfoSxDto)checkList.get(i);
			TvVoucherinfoDto insertDto = new TvVoucherinfoDto();
			this.copyProperties(insertDto, curentdto);
			try 
			{
				TvVoucherinfoDto qdo = new TvVoucherinfoDto();
				qdo.setSvoucherno(curentdto.getSvoucherno());
				qdo.setStrecode(curentdto.getStrecode());
				qdo.setScreatdate(curentdto.getScreatdate());
				List<TvVoucherinfoDto> isExcist = CommonFacade.getODB().findRsByDto(qdo);
				if(isExcist != null && isExcist.size() > 0)
				{
					throw new ITFEBizException("ƾ֤���Ѵ��ڴ����ݣ�");
				}
				DatabaseFacade.getDb().create(insertDto);
			} catch (JAFDatabaseException e) 
			{
				log.error("��ѯƾ֤������������쳣:"+e);
				throw new ITFEBizException("��ѯƾ֤������������쳣��");
			} catch (ValidateException e) 
			{
				log.error("��ѯƾ֤������������쳣:"+e);
				throw new ITFEBizException("��ѯƾ֤������������쳣��");
			} catch(Exception e)
			{
				log.error(e);
				throw new ITFEBizException(e.getMessage());
			}
			
			String svtcode = curentdto.getSvtcode();
			String sdealno = curentdto.getSdealno();
			
			if(MsgConstant.VOUCHER_NO_2301.equals(svtcode))
	    	{
				//������ҵ���
				TvPayreckbankSxDto pbCurentDto = new TvPayreckbankSxDto();
				TvPayreckBankDto pbInsertDto = new TvPayreckBankDto();
				pbCurentDto.setIvousrlno(Long.valueOf(sdealno));
				try 
				{
					List<TvPayreckbankSxDto> dtos = (List<TvPayreckbankSxDto>)CommonFacade.getODB().findRsByDto(pbCurentDto);
					if(dtos != null && dtos.size()>0)
					{
						pbCurentDto = dtos.get(0);
					}
					this.copyProperties(pbInsertDto, pbCurentDto);
					DatabaseFacade.getDb().create(pbInsertDto);
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch (ValidateException e) {
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}
				
				//������ҵ���
				try
				{
					List<TvPayreckbanklistSxDto> lists = (List<TvPayreckbanklistSxDto>)DatabaseFacade.getDb().find(TvPayreckbanklistSxDto.class, "where I_VOUSRLNO ="+Long.valueOf(sdealno));
					for(TvPayreckbanklistSxDto subPbCurentDto: lists)
					{
						TvPayreckBankListDto subPbInsertDto = new TvPayreckBankListDto();
						this.copyProperties(subPbInsertDto, subPbCurentDto);
						DatabaseFacade.getDb().create(subPbInsertDto);
					}
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����ϸ������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����ϸ������쳣��");
				} catch(Exception e)
				{
					log.error("��ѯҵ����ϸ������쳣:"+e);
					throw new ITFEBizException(e.getMessage());
				}
	    	}else if(MsgConstant.VOUCHER_NO_2302.equals(svtcode))
	    	{
	    		//������ҵ���
	    		TvPayreckbankbackSxDto pbCurentDto = new TvPayreckbankbackSxDto();
	    		TvPayreckBankBackDto pbInsertDto = new TvPayreckBankBackDto();
				pbCurentDto.setIvousrlno(Long.valueOf(sdealno));
				try 
				{
					List<TvPayreckbankbackSxDto> dtos = (List<TvPayreckbankbackSxDto>)CommonFacade.getODB().findRsByDto(pbCurentDto);
					if(dtos != null && dtos.size()>0)
					{
						pbCurentDto = dtos.get(0);
					}
					this.copyProperties(pbInsertDto, pbCurentDto);
					DatabaseFacade.getDb().create(pbInsertDto);
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch (ValidateException e) {
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}
				
				//������ҵ���
				try
				{
					List<TvPayreckbankbacklistSxDto> lists = (List<TvPayreckbankbacklistSxDto>)DatabaseFacade.getDb().find(TvPayreckbankbacklistSxDto.class, "where I_VOUSRLNO ="+Long.valueOf(sdealno));
					for(TvPayreckbankbacklistSxDto subPbCurentDto: lists)
					{
						TvPayreckBankBackListDto subPbInsertDto = new TvPayreckBankBackListDto();
						this.copyProperties(subPbInsertDto, subPbCurentDto);
						DatabaseFacade.getDb().create(subPbInsertDto);
					}
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����ϸ������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����ϸ������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}	    		
	    	}else if(MsgConstant.VOUCHER_NO_5106.equals(svtcode))
	    	{
	    		//������ҵ���
	    		TvGrantpaymsgmainSxDto pbCurentDto = new TvGrantpaymsgmainSxDto();
	    		List<String> curentPackictnos = new ArrayList<String>();
	    		TvGrantpaymsgmainDto pbInsertDto = new TvGrantpaymsgmainDto();
				pbCurentDto.setIvousrlno(Long.valueOf(sdealno));
				List<TvGrantpaymsgmainSxDto> dtoslist = null;
				try 
				{   dtoslist = CommonFacade.getODB().findRsByDto(pbCurentDto);
				    for(TvGrantpaymsgmainSxDto pbCurentdto: dtoslist)
				    {
				    	curentPackictnos.add(pbCurentdto.getSpackageticketno());
				    	this.copyProperties(pbInsertDto, pbCurentdto);
						DatabaseFacade.getDb().create(pbInsertDto);
				    }
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch (ValidateException e) 
				{
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}
				
				//������ҵ���
				try
				{
					for(String crentpack: curentPackictnos)
					{
						TvGrantpaymsgsubSxDto qto = new TvGrantpaymsgsubSxDto();
						qto.setIvousrlno(Long.valueOf(sdealno));
						qto.setSpackageticketno(crentpack);
						List<TvGrantpaymsgsubSxDto> lists = (List<TvGrantpaymsgsubSxDto>)CommonFacade.getODB().findRsByDto(qto);
						for(TvGrantpaymsgsubSxDto subPbCurentDto: lists)
						{
							TvGrantpaymsgsubDto subPbInsertDto = new TvGrantpaymsgsubDto();
							this.copyProperties(subPbInsertDto, subPbCurentDto);
							DatabaseFacade.getDb().create(subPbInsertDto);
						}
					}
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����ϸ������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����ϸ������쳣��");
				} catch (ValidateException e) {
					log.error("��ѯҵ����ϸ������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����ϸ������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}	    		
	    	}else if(MsgConstant.VOUCHER_NO_5108.equals(svtcode))
	    	{
	    		//������ҵ���
	    		TvDirectpaymsgmainSxDto pbCurentDto = new TvDirectpaymsgmainSxDto();
	    		TvDirectpaymsgmainDto pbInsertDto = new TvDirectpaymsgmainDto();
				pbCurentDto.setIvousrlno(Long.valueOf(sdealno));
				try 
				{
					List<TvDirectpaymsgmainSxDto> dtos = (List<TvDirectpaymsgmainSxDto>)CommonFacade.getODB().findRsByDto(pbCurentDto);
					if(dtos != null && dtos.size()>0)
					{
						pbCurentDto = dtos.get(0);
					}
					this.copyProperties(pbInsertDto, pbCurentDto);
					DatabaseFacade.getDb().create(pbInsertDto);
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch (ValidateException e) {
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}
				
			    //������ҵ���
				try
				{
					List<TvDirectpaymsgsubSxDto> lists = (List<TvDirectpaymsgsubSxDto>)DatabaseFacade.getDb().find(TvDirectpaymsgsubSxDto.class, "where I_VOUSRLNO ="+Long.valueOf(sdealno));
					for(TvDirectpaymsgsubSxDto subPbCurentDto: lists)
					{
						TvDirectpaymsgsubDto subPbInsertDto = new TvDirectpaymsgsubDto();
						this.copyProperties(subPbInsertDto, subPbCurentDto);
						DatabaseFacade.getDb().create(subPbInsertDto);
					}
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����ϸ������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����ϸ������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}	    		
	    	}else if(MsgConstant.VOUCHER_NO_5207.equals(svtcode))
	    	{
	    		//������ҵ���
	    		TvPayoutmsgmainSxDto pbCurentDto = new TvPayoutmsgmainSxDto();
	    		TvPayoutmsgmainDto pbInsertDto = new TvPayoutmsgmainDto();
				pbCurentDto.setSbizno(sdealno);
				try 
				{
					List<TvPayoutmsgmainSxDto> dtos = (List<TvPayoutmsgmainSxDto>)CommonFacade.getODB().findRsByDto(pbCurentDto);
					if(dtos != null && dtos.size()>0)
					{
						pbCurentDto = dtos.get(0);
					}
					this.copyProperties(pbInsertDto, pbCurentDto);
					DatabaseFacade.getDb().create(pbInsertDto);
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch (ValidateException e) {
					log.error("��ѯҵ����������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}
				
			    //������ҵ���
				try
				{
					List<TvPayoutmsgsubSxDto> lists = (List<TvPayoutmsgsubSxDto>)DatabaseFacade.getDb().find(TvPayoutmsgsubSxDto.class, "where S_BIZNO = '"+sdealno+"'");
					for(TvPayoutmsgsubSxDto subPbCurentDto: lists)
					{
						TvPayoutmsgsubDto subPbInsertDto = new TvPayoutmsgsubDto();
						this.copyProperties(subPbInsertDto, subPbCurentDto);
						DatabaseFacade.getDb().create(subPbInsertDto);
					}
				} catch (JAFDatabaseException e) 
				{
					log.error("��ѯҵ����ϸ������쳣:"+e);
					throw new ITFEBizException("��ѯҵ����ϸ������쳣��");
				} catch(Exception e)
				{
					log.error(e);
					throw new ITFEBizException(e.getMessage());
				}	    		
	    	}
		}
	}
	
	private void copyProperties (Object dest, Object orig) throws ITFEBizException
	{
		Field[] origFields = orig.getClass().getDeclaredFields();
		for(Field field: origFields)
		{
			try 
			{
				field.setAccessible(true);
				Object origValue = field.get(orig);
				String origFieldName = field.getName();
				Field inField = dest.getClass().getDeclaredField(origFieldName);
				inField.setAccessible(true);
				inField.set(dest,origValue);
			} catch (IllegalArgumentException e) 
			{
				log.error(e);
				throw new ITFEBizException("������ʽ���ݳ���");
			} catch (IllegalAccessException e) 
			{
				log.error(e);
				throw new ITFEBizException("������ʽ���ݳ���");
			} catch (SecurityException e)
			{
				log.error(e);
				throw new ITFEBizException("������ʽ���ݳ���");
			} catch (NoSuchFieldException e)
			{
				log.error(e);
				throw new ITFEBizException("������ʽ���ݳ���");
			} catch(Exception e)
			{
				log.error(e);
				throw new ITFEBizException(e.getMessage());
			}
			
		}
	}

	public void sendReturnVoucher(List checkList) throws ITFEBizException
	{
		this.voucherReadShanXiService.sendReturnVoucher(checkList);
	}

	public void updateStatus(String loginSorgcode) throws ITFEBizException 
	{
		String condition = "";
		String setValue = "";
		try 
		{
//			condition = "'"+DealCodeConstants.VOUCHER_SENDED+"','"+DealCodeConstants.VOUCHER_RECIPE+"'";
//			setValue = "v.S_STATUS";
//			DatabaseFacade.getDb().execSql(this.returnSql(condition, setValue, loginSorgcode, null));
//			DatabaseFacade.getDb().execSql(this.returnSql(condition, setValue, loginSorgcode, ""));
			
			condition = "'"+DealCodeConstants.VOUCHER_SUCCESS_NO_BACK+"','"+DealCodeConstants.VOUCHER_RETURN_SUCCESS_ALREADY+"','"+DealCodeConstants.VOUCHER_STAMP+"'";
			setValue = "'"+DealCodeConstants.VOUCHER_SUCCESS_NO_BACK+"'";
			DatabaseFacade.getDb().execSql(this.returnSql(condition, setValue, loginSorgcode, null));
			DatabaseFacade.getDb().execSql(this.returnSql(condition, setValue, loginSorgcode, ""));
			
			condition = "'"+DealCodeConstants.VOUCHER_FAIL_TCBS+"','"+DealCodeConstants.VOUCHER_FAIL+"'";
			setValue = "'"+DealCodeConstants.VOUCHER_FAIL_TCBS+"'";
			DatabaseFacade.getDb().execSql(this.returnSql(condition, setValue, loginSorgcode, null));
			DatabaseFacade.getDb().execSql(this.returnSql(condition, setValue, loginSorgcode, ""));
			
			
		} catch (JAFDatabaseException e) 
		{
			log.error(e);
			throw new ITFEBizException("ͬ��״̬����");
		} catch(Exception e)
		{
			log.error(e);
			throw new ITFEBizException(e.getMessage());
		}
	}
		
    private String returnSql(String condition, String setValue, String loginSorgcode, String type)
    {
    	String sql;
    	if(null == type)
    	{
	    	 sql = "MERGE INTO (SELECT * FROM TV_VOUCHERINFO_SX WHERE S_ORGCODE ='"+loginSorgcode+"' AND S_STATUS='20') AS vsx"
	   			 +" USING (SELECT S_ORGCODE, S_TRECODE, S_VTCODE, S_CREATDATE, S_VOUCHERNO, S_STATUS"  
	             +" FROM TV_VOUCHERINFO WHERE S_ORGCODE ='"+loginSorgcode+"' AND S_STATUS in ("+condition+") AND S_VTCODE NOT IN('2301','2302')) AS v"
	   			 +" ON vsx.S_VOUCHERNO = v.S_VOUCHERNO AND vsx.S_TRECODE = v.S_TRECODE AND vsx.S_CREATDATE = v.S_CREATDATE AND vsx.S_VTCODE = v.S_VTCODE"
	   			 +" WHEN MATCHED"
	   			 +" THEN UPDATE SET vsx.S_STATUS = "+setValue;
	       	     return sql;
    	}else
    	{
    		
    		 sql = "MERGE INTO (SELECT * FROM TV_VOUCHERINFO_SX WHERE S_ORGCODE ='"+loginSorgcode+"' AND S_STATUS='20') AS vsx"
      			 +" USING (SELECT S_ORGCODE, S_TRECODE, S_VTCODE, S_CREATDATE, S_VOUCHERNO, S_STATUS"  
      			 +" FROM TV_VOUCHERINFO WHERE S_ORGCODE ='"+loginSorgcode+"' AND S_STATUS in ("+condition+") AND S_VTCODE IN('2301','2302')) AS v"
      			 +" ON vsx.S_VOUCHERNO = v.S_VOUCHERNO AND vsx.S_TRECODE = v.S_TRECODE AND vsx.S_CREATDATE = v.S_CREATDATE AND vsx.S_VTCODE = v.S_VTCODE"
      			 +" WHEN MATCHED"
      			 +" THEN UPDATE SET vsx.S_STATUS = "+setValue;
          	     return sql;
    	}
    }
}