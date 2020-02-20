package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �����ʽ����������3560��
 * 
 * @author hejianrong
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3560 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3560.class);
	private static final String Eta_BUDGETSUBCODE = "110019101";//ʡ˰��Ԥ�ַ�����Ŀ����
	private static final String Tax_BUDGETSUBCODE = "110019001";//��˰������Ŀ����
	private static final String S_TAXORGCODE = "000000000000";//ȫϽ���ջ��� 
	private int S_BUDGETTYPE ;//Ԥ������
	private BigDecimal Total;//�ܽ��
	
	/**
	 * ����ƾ֤
	 * @param vDto
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		List list=new ArrayList();
		List lists=new ArrayList();
		int max;
		if(StringUtils.isBlank(vDto.getShold1())){
			max=2;
			vDto.setShold1("1");
		}else
			max=Integer.parseInt(vDto.getShold1());		
		for(S_BUDGETTYPE=Integer.parseInt(vDto.getShold1());S_BUDGETTYPE<=max;S_BUDGETTYPE++){
			list=findReport(vDto);
			if(list!=null&&list.size()>0)
				lists.add(voucherTranfor(vDto, list));			
		}return lists;
	}
	
	/**
	 * ����ƾ֤
	 * ����ƾ֤����
	 * @param vDto
	 * @param list
	 * @throws ITFEBizException
	 */
	public List voucherTranfor(TvVoucherinfoDto vDto,List list) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();		
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");		
		String mainvou=VoucherUtil.getGrantSequence();
		dto.setSdealno(mainvou);		
		dto.setSvoucherno(mainvou);
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");
		dto.setShold1(S_BUDGETTYPE+"");
		Total=new BigDecimal("0.00");
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(list);				
		List voucherList=new ArrayList();
		voucherList.add(tranfor(lists));
		dto.setNmoney(Total);		
		voucherList.add(dto);
		return voucherList;		
	}
	
	/**
	 * DTOת��XML����
	 * 
	 * @param List
	 *            	���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor(List lists) throws ITFEBizException{
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);
			List<TrIncomedayrptDto> resultList=(List) lists.get(1);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("Id", vDto.getSdealno());//�����ʽ����������Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//������������
			vouchermap.put("StYear", vDto.getSstyear());//ҵ�����		
			vouchermap.put("VtCode", vDto.getSvtcode());//ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());//ƾ֤����		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//ƾ֤��		
			vouchermap.put("BudgetType", vDto.getShold1());//Ԥ������
			vouchermap.put("ReportDate", vDto.getScheckdate());//������������
			vouchermap.put("TreCode", vDto.getStrecode());//�����������			
			vouchermap.put("TreName", vDto.getShold2());//������������
			vouchermap.put("FinOrgCode", vDto.getShold3());//�������ش���			
			vouchermap.put("Hold1", "");//Ԥ���ֶ�1		
			vouchermap.put("Hold2", "");//Ԥ���ֶ�2
			BigDecimal SumEtaMoney=new BigDecimal("0.00");
			BigDecimal SumTaxMoney=new BigDecimal("0.00");
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();			
			for(TrIncomedayrptDto mainDto:resultList){
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("AdmDivCode", findAdmDivCodeByStrecode(mainDto.getStrecode()));//������������
				Detailmap.put("StYear", vDto.getSstyear());//ҵ�����
				Detailmap.put("TaxOrgCode",S_TAXORGCODE);//���ջ��ش���
				Detailmap.put("TaxOrgName", "ȫϽ"); //���ջ�������
				Detailmap.put("BankCode", mainDto.getStrecode());//�����������
				Detailmap.put("TreName", findStreNameByStrecode(mainDto.getStrecode()));//������������
				Detailmap.put("BudgetType", S_BUDGETTYPE);//�ʽ�����
				Detailmap.put("TotalAmt", MtoCodeTrans.transformString(mainDto.getNmoneyquarter().add(mainDto.getNmoneyyear())));//���غϼƽ��
				Detailmap.put("EtaAmt", MtoCodeTrans.transformString(mainDto.getNmoneyquarter()));//ʡ˰��Ԥ�ַ������
				Detailmap.put("TaxAmt", MtoCodeTrans.transformString(mainDto.getNmoneyyear()));//��˰�������		
				Detailmap.put("Hold1", "");//Ԥ���ֶ�1 
				Detailmap.put("Hold2", "");//Ԥ���ֶ�2 	
				Detailmap.put("Hold3", "");//Ԥ���ֶ�3 
				Detailmap.put("Hold4", "");//Ԥ���ֶ�4
				Detail.add(Detailmap);
				SumEtaMoney=SumEtaMoney.add(mainDto.getNmoneyquarter());
				SumTaxMoney=SumTaxMoney.add(mainDto.getNmoneyyear());
				Total=Total.add(mainDto.getNmoneyquarter());
				Total=Total.add(mainDto.getNmoneyyear());
			}
			vouchermap.put("SumTotalMoney", MtoCodeTrans.transformString(Total));//���غϼƽ��
			vouchermap.put("SumEtaMoney", MtoCodeTrans.transformString(SumEtaMoney));//ʡ˰��Ԥ�ַ����ϼƽ��
			vouchermap.put("SumTaxMoney", MtoCodeTrans.transformString(SumTaxMoney));//��˰�����ϼƽ��
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;	
		}catch(ITFEBizException e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage());
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��",e);
		}
	
	}
	
	/**
	 * ���ݹ�������ѯ��������
	 * @param strecode
	 * @return
	 * @throws ITFEBizException
	 */
	public String findStreNameByStrecode(String strecode) throws ITFEBizException{
		TsTreasuryDto dto=new TsTreasuryDto();
		dto.setStrecode(strecode);
		List<TsTreasuryDto> list;
		try {
			list = CommonFacade.getODB().findRsByDto(dto);
			if(list!=null && list.size() > 0)
				  return (list.get(0).getStrename());			 
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ���������Ϣ�쳣��");
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ���������Ϣ�쳣��");
		}throw new ITFEBizException("�����������"+strecode+"��[����������Ϣ����]��δά����");		  
	}
	
	 /**
     * ���ݹ�����������Ӧ����������
     * @return
	 * @throws ITFEBizException 
     */
    public String findAdmDivCodeByStrecode(String strecode) throws ITFEBizException{
    	TsConvertfinorgDto finorgDto=new TsConvertfinorgDto();
    	finorgDto.setStrecode(strecode);
    	try {
			List list = CommonFacade.getODB().findRsByDto(finorgDto);
			if(list==null||list.size()==0)
				throw new ITFEBizException("�������"+strecode+"��Ӧ�Ĳ����������δά����");							
			finorgDto=(TsConvertfinorgDto) list.get(0);
			if(StringUtils.isBlank(finorgDto.getSadmdivcode()))
				throw new ITFEBizException("��������"+finorgDto.getSfinorgcode()+"��Ӧ�������������δά����");
			else
				return 	finorgDto.getSadmdivcode();	
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ϣ�쳣��");		
		}	catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ϣ�쳣��");		
		}
    }
    
	/**
	 * ��ѯ������Ϣ
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findReport(TvVoucherinfoDto dto) throws ITFEBizException{		
		try {
			SQLExecutor sqlExecutor=null;
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.clearParams();
			SQLResults rs = sqlExecutor.runQueryCloseCon(getSql(dto),TrIncomedayrptDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ������Ϣ����",e);
		}
	}
	
	/**
	 * ��ѯSQL���
	 * @param dto
	 * @return
	 */
	private String getSql(TvVoucherinfoDto dto){
//		return " SELECT (CASE WHEN t1.S_BUDGETSUBCODE IS NULL THEN t2.S_TRECODE ELSE t1.S_TRECODE END) AS S_TRECODE,"+
//				" (CASE WHEN t1.S_FINORGCODE IS NULL THEN t2.S_FINORGCODE ELSE t1.S_FINORGCODE END) AS S_FINORGCODE,"+
//				" VALUE(N_MONEYQUARTER,0) AS N_MONEYQUARTER,VALUE(N_MONEYYEAR,0) AS N_MONEYYEAR FROM "+
//				" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYDAY) AS N_MONEYQUARTER FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+
//				" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND S_TAXORGCODE = '"+S_TAXORGCODE+"' AND  S_BUDGETSUBCODE ='"+Eta_BUDGETSUBCODE+"' "+
//				" AND S_RPTDATE='"+dto.getScheckdate()+"' AND S_BUDGETLEVELCODE= S_TRELEVEL AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE"+
//				" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE ) t1 FULL  JOIN  "+
//				" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYDAY) AS N_MONEYYEAR FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+ 
//				" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND S_TAXORGCODE = '"+S_TAXORGCODE+"' AND  S_BUDGETSUBCODE ='"+Tax_BUDGETSUBCODE+"' "+
//				" AND S_RPTDATE='"+dto.getScheckdate()+"' AND S_BUDGETLEVELCODE= S_TRELEVEL AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE " +
//				" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE) t2 ON t1.S_TRECODE=t2.S_TRECODE";

		return "SELECT S_TRECODE,S_FINORGCODE,SUM(N_MONEYQUARTER) AS N_MONEYQUARTER,SUM(N_MONEYYEAR) AS N_MONEYYEAR FROM ("+
		" SELECT (CASE WHEN t1.S_BUDGETSUBCODE IS NULL THEN t2.S_TRECODE ELSE t1.S_TRECODE END) AS S_TRECODE,"+
		" (CASE WHEN t1.S_FINORGCODE IS NULL THEN t2.S_FINORGCODE ELSE t1.S_FINORGCODE END) AS S_FINORGCODE,"+
		" VALUE(N_MONEYQUARTER,0) AS N_MONEYQUARTER,VALUE(N_MONEYYEAR,0) AS N_MONEYYEAR FROM "+
		" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYYEAR) AS N_MONEYQUARTER FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+
		" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND (S_TAXORGCODE = '"+S_TAXORGCODE+"' OR S_TAXORGCODE = '12290020000') AND  S_BUDGETSUBCODE ='"+Eta_BUDGETSUBCODE+"' "+
		" AND S_RPTDATE='"+dto.getScheckdate()+"' AND (((S_TRELEVEL = '3' OR S_TRELEVEL = '2') AND S_BUDGETLEVELCODE= S_TRELEVEL) OR ( S_TRELEVEL = '4' AND S_BUDGETLEVELCODE>= S_TRELEVEL)) AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE"+
		" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE ) t1 FULL  JOIN  "+
		" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYYEAR) AS N_MONEYYEAR FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+ 
		" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND (S_TAXORGCODE = '"+S_TAXORGCODE+"' OR S_TAXORGCODE = '12290020000') AND  S_BUDGETSUBCODE ='"+Tax_BUDGETSUBCODE+"' "+
		" AND S_RPTDATE='"+dto.getScheckdate()+"' AND (((S_TRELEVEL = '3' OR S_TRELEVEL = '2') AND (S_BUDGETLEVELCODE= S_TRELEVEL)) OR (S_TRELEVEL = '4' AND S_BUDGETLEVELCODE>= S_TRELEVEL)) AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE " +
		" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE) t2 ON t1.S_TRECODE=t2.S_TRECODE"
		+") GROUP BY S_TRECODE,S_FINORGCODE ORDER BY S_TRECODE";
}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
