package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDefraySubDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckpaybankDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ֧������(3512)ת��
 * 
 * @author renqingbin
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3582 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3582.class);
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		vDto.setSdealno(VoucherUtil.getGrantSequence());
		return getVoucher(vDto);
	}
	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List returnList = new ArrayList();
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List<TsConvertfinorgDto> tsConvertfinorgList=(List<TsConvertfinorgDto>) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("���⣺"+vDto.getStrecode()+"��Ӧ�Ĳ������ش������δά����");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("���⣺"+cDto.getStrecode()+"��Ӧ����������δά����");
			}
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}
		
		String stockDayRptDetailSql="SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE >= ? AND S_RPTDATE <= ? ";
		if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
			stockDayRptDetailSql+=" and S_TRECODE = ? ";
		if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))
			stockDayRptDetailSql+=" and S_BUDGETTYPE = ? ";
		if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))
			stockDayRptDetailSql+=" and S_TAXORGCODE = ? ";
		stockDayRptDetailSql+=" ORDER BY S_RPTDATE ASC";
		List<IDto> detailList=new ArrayList<IDto>();
		try {
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			if(vDto.getSext5()!=null&&"35122".equals(vDto.getSext5()))
			{
				String sql = "select * from TN_CONPAYCHECKPAYBANK where s_orgcode=? and s_trecode=? and s_bankcode=? and s_bgtlevel=? and s_bgttypecode=? and s_paytypecode=? and s_ext2=? order by S_SUBJECTCODE ";
				execDetail.addParam(vDto.getSorgcode());//��������
				execDetail.addParam(vDto.getStrecode());//�������
				execDetail.addParam(vDto.getSpaybankcode());//��������
				execDetail.addParam(vDto.getSext3());//Ͻ����־
				execDetail.addParam(vDto.getSext2());//Ԥ������
				execDetail.addParam(vDto.getSext1());//֧����ʽ
				execDetail.addParam(vDto.getSext4());//�·�
				detailList.addAll((List<TnConpaycheckpaybankDto>)execDetail.runQueryCloseCon(sql,TnConpaycheckpaybankDto.class).getDtoCollection());
				if(detailList==null||detailList.size()==0)
					return null;
			}else
			{
				execDetail.addParam(vDto.getScheckdate());
				execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
					execDetail.addParam(vDto.getStrecode());
				if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))
					execDetail.addParam(vDto.getShold1());
				if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))
					execDetail.addParam(vDto.getScheckvouchertype());
				detailList=  (List<IDto>) execDetail.runQuery(stockDayRptDetailSql,TrTaxorgPayoutReportDto.class).getDtoCollection();
				stockDayRptDetailSql=StringUtil.replace(stockDayRptDetailSql, "HTR_TAXORG_PAYOUT_REPORT", "TR_TAXORG_PAYOUT_REPORT");
				execDetail.addParam(vDto.getScheckdate());
				execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
					execDetail.addParam(vDto.getStrecode());
				if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))
					execDetail.addParam(vDto.getShold1());
				if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))
					execDetail.addParam(vDto.getScheckvouchertype());
				detailList.addAll((List<TrTaxorgPayoutReportDto>)execDetail.runQueryCloseCon(stockDayRptDetailSql,TrTaxorgPayoutReportDto.class).getDtoCollection());
				if(detailList==null||detailList.size()==0){
					return null;
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ"+stockDayRptDetailSql+"�����ϸ��Ϣ�쳣��",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		List<List> sendList = this.getSubLists(detailList, 500);
		if(sendList!=null&&sendList.size()>0)
		{
			List<IDto> tempList = null;
			String danhao=null;
			for(int i=0;i<sendList.size();i++)
			{
				tempList = sendList.get(i);
				//3512-dto.shold1Ԥ������dto.scheckvouchertype��������dto.spaybankcode���ʽ�ֹ����dto.scheckdate������ʼ����dto.screatdateƾ֤����dto.strecode�������
				//TrTaxorgPayoutReportDto dto=new TrTaxorgPayoutReportDto();dto.setStrecode(vDto.getStrecode());//�������dto.setSbudgettype(vDto.getShold1());//Ԥ������
				//dto.setStaxorgcode(vDto.getScheckvouchertype());//��������-���ݿ���һ��Ԥ��֧���ձ���1-ʵ���ʽ��ձ���2-����֧���ձ���3--ƾ֤��淶-1һ��Ԥ��֧������-2����֧��
				String FileName=null;
				String dirsep = File.separator; 
				String mainvou=VoucherUtil.getGrantSequence();
				vDto.setSdealno(mainvou);
				vDto.setSvoucherno(mainvou);
				if(danhao==null)
					danhao=mainvou;
				FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
				TvVoucherinfoDto dto=new TvVoucherinfoDto();			
				dto.setSorgcode(vDto.getSorgcode());
				dto.setSadmdivcode(vDto.getSadmdivcode());
				dto.setSvtcode(vDto.getSvtcode());
				dto.setScreatdate(TimeFacade.getCurrentStringTime());
				dto.setStrecode(vDto.getStrecode());
				dto.setSfilename(FileName);
				dto.setSdealno(mainvou);
				dto.setSattach(danhao);//���˵���
				dto.setShold3(String.valueOf(sendList.size()));//������
				dto.setShold4(String.valueOf(i+1));//�������
				dto.setSstyear(dto.getScreatdate().substring(0, 4));				
				dto.setScheckdate(vDto.getScheckdate());
				dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
				dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				dto.setSdemo("����ɹ�");
				dto.setSvoucherflag("1");
				dto.setSvoucherno(mainvou);	
				if(vDto.getSext5()!=null&&"35122".equals(vDto.getSext5()))
				{
					dto.setSext1(vDto.getSext1());
					dto.setSext2(vDto.getSext2());
					dto.setSext3(vDto.getSext3());
					dto.setSext4(vDto.getSext4());
					dto.setSext5(vDto.getSext5());
					dto.setShold3(vDto.getShold3());
					dto.setShold4(vDto.getShold4());
				}else
				{
					dto.setShold1(vDto.getShold1()==null?"":vDto.getShold1());//Ԥ������
					dto.setScheckvouchertype(vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//��������
					dto.setIcount(tempList.size());
					dto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
				}
				List mapList = new ArrayList();
				mapList.add(vDto);
				mapList.add(cDto);
				mapList.add(tempList);
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map=null;
				if(vDto.getSext5()!=null&&"35122".equals(vDto.getSext5()))
					map = tranfor35122(mapList,sendList.size(),i+1,danhao,idtoList);
				else
					map = tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
				dto.setIcount(tempList.size());
				String amt = ((Map)map.get("Voucher")).get("AllAmt").toString();
				dto.setNmoney(new BigDecimal(amt));
				List vouList=new ArrayList();
				vouList.add(map);
				vouList.add(dto);
				vouList.add(idtoList);
				returnList.add(vouList);
			}
		}
		return returnList;
	}
	private Map tranfor(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			TsConvertfinorgDto cDto = (TsConvertfinorgDto)mapList.get(1);
			List<TrTaxorgPayoutReportDto> detailList=(List<TrTaxorgPayoutReportDto>) mapList.get(2);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//������������
			vouchermap.put("StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
			vouchermap.put("VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
			vouchermap.put("VouDate",vDto.getScreatdate());//ƾ֤����
			vouchermap.put("VoucherNo",vDto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",danhao);//���˵���
			vouchermap.put("ChildPackNum",count);//�Ӱ�����
			vouchermap.put("CurPackNo",xuhao);//�������
			vouchermap.put("BillKind","1".equals(vDto.getScheckvouchertype())?"1":"2");//��������1,��Ӧ1-һ��Ԥ��֧������3��Ӧ2-����֧��
			vouchermap.put("FinOrgCode",cDto.getSfinorgcode());//�������ش���
			vouchermap.put("TreCode",vDto.getStrecode());//�����������
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());
			vouchermap.put("BgtTypeCode",vDto.getShold1());//Ԥ�����ͱ���
			vouchermap.put("BgtTypeName","1".equals(vDto.getShold1())?"Ԥ����":"Ԥ����");//Ԥ����������
			vouchermap.put("ClearBankCode","");//�������б���
			vouchermap.put("ClearBankName","");//������������
			vouchermap.put("ClearAccNo","");//�����˺�
			vouchermap.put("ClearAccNanme","");//�����˻�����
			vouchermap.put("BeginDate",vDto.getScheckdate());//������ʼ����
			vouchermap.put("EndDate",vDto.getSpaybankcode());//������ֹ����
			vouchermap.put("AllNum",detailList.size());//�ܱ���
			vouchermap.put("Hold1","");//Ԥ���ֶ�1
			vouchermap.put("Hold2",vDto.getSverifyusercode()==null?"":vDto.getSverifyusercode());//Ԥ���ֶ�2
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			TsTreasuryDto trdto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			for (TrTaxorgPayoutReportDto dto:detailList) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",vDto.getSdealno()+(++id));//���
				Detailmap.put("ExpFuncCode",dto.getSbudgetsubcode());//֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName",dto.getSbudgetsubname());//֧�����ܷ����Ŀ����
				Detailmap.put("CurPayAmt",String.valueOf(dto.getNmoneyday()));//���ڽ��
				Detailmap.put("SumPayAmt",String.valueOf(dto.getNmoneyyear()));//�ۼƽ��
				if(vDto.getSorgcode().startsWith("13")){//��������
					Detailmap.put("Hold1","001001");//Ԥ���ֶ�1
					Detailmap.put("Hold2",trdto.getStrename());//Ԥ���ֶ�2
				}else
				{
					Detailmap.put("Hold1","");//Ԥ���ֶ�1
					Detailmap.put("Hold2","");//Ԥ���ֶ�2
				}
				
				Detailmap.put("Hold3","");//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4
				if(dto.getSbudgetsubcode().length()==3)
					allamt=allamt.add(dto.getNmoneyday());
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}
			vDto.setNmoney(allamt);
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	private Map tranfor35122(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			TsConvertfinorgDto cDto = (TsConvertfinorgDto)mapList.get(1);
			List<TnConpaycheckpaybankDto> detailList=(List<TnConpaycheckpaybankDto>) mapList.get(2);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//������������
			vouchermap.put("StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
			vouchermap.put("VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
			vouchermap.put("VouDate",vDto.getScreatdate());//ƾ֤����
			vouchermap.put("VoucherNo",vDto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",danhao);//���˵���
			vouchermap.put("ChildPackNum",count);//�Ӱ�����
			vouchermap.put("CurPackNo",xuhao);//�������
			vouchermap.put("BillKind","1");//һ��Ԥ��֧��
			vouchermap.put("BelongFlag", vDto.getSext3());//Ͻ����־
			vouchermap.put("FinOrgCode",cDto.getSfinorgcode());//�������ش���
			vouchermap.put("TreCode",vDto.getStrecode());//�����������
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());
			vouchermap.put("BgtTypeCode",vDto.getSext2());//Ԥ�����ͱ���
			vouchermap.put("BgtTypeName","1".equals(vDto.getSext2())?"Ԥ����":"Ԥ����");//Ԥ����������
			vouchermap.put("PayTypeCode","1".equals(vDto.getSext1())?"12":"11");//֧����ʽ����
			vouchermap.put("PayTypeName","1".equals(vDto.getSext1())?"��Ȩ֧��":"ֱ��֧��");//������������
			vouchermap.put("ClearBankCode","");//�������б���
			vouchermap.put("ClearBankName","");//������������
			vouchermap.put("ClearAccNo","");//�����˺�
			vouchermap.put("ClearAccNanme","");//�����˻�����
			vouchermap.put("BeginDate",vDto.getShold3());//������ʼ����
			vouchermap.put("EndDate",vDto.getShold4());//������ֹ����
			vouchermap.put("AllNum",detailList.size());//�ܱ���
			vouchermap.put("AllAmt",new BigDecimal(0));//�ܽ��
			vouchermap.put("Hold1",vDto.getSpaybankcode());//Ԥ���ֶ�1
			vouchermap.put("Hold2","");//Ԥ���ֶ�2
			BigDecimal allamt=new BigDecimal("0.00");	
			BigDecimal allmonthamt=new BigDecimal("0.00");
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			TsTreasuryDto trdto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			for (TnConpaycheckpaybankDto dto:detailList) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",vDto.getSdealno()+(++id));//���
				Detailmap.put("ExpFuncCode",dto.getSsubjectcode());//֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName",dto.getSsubjectname());//֧�����ܷ����Ŀ����
				Detailmap.put("CurPayAmt",String.valueOf(dto.getNmonthamt()));//���ڽ��
				Detailmap.put("SumPayAmt",String.valueOf(dto.getNyearamt()));//�ۼƽ��
				if(vDto.getSorgcode().startsWith("13")){//��������
					Detailmap.put("Hold1","001001");//Ԥ���ֶ�1
					Detailmap.put("Hold2",trdto.getStrename());//Ԥ���ֶ�2
				}else
				{
					Detailmap.put("Hold1","");//Ԥ���ֶ�1
					Detailmap.put("Hold2","");//Ԥ���ֶ�2
				}
				
				Detailmap.put("Hold3","");//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4
				if(dto.getSsubjectcode().length()==3)
				{
					allamt=allamt.add(dto.getNyearamt());
					allmonthamt = allmonthamt.add(dto.getNmonthamt());
				}
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}
			vDto.setNmoney(allamt);
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));
			vouchermap.put("Hold2",MtoCodeTrans.transformString(allmonthamt));
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	/**
	 * DTOת��XML����
	 * 
	 * @param List
	 *            	���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TrTaxorgPayoutReportDto> list, TvVoucherinfoDto vDto) throws ITFEBizException{
		try{
			HashMap<String, Object> returnmap = new HashMap<String, Object>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			//ȡ�ù�������
			String treName = BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()) == null ? "" : BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()).getStrename();
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode","");//������������
			vouchermap.put("StYear","");//ҵ�����
			vouchermap.put("VtCode","");//ƾ֤���ͱ��
			vouchermap.put("VouDate","");//ƾ֤����
			vouchermap.put("VoucherNo","");//ƾ֤��
			vouchermap.put("VoucherCheckNo","");//���˵���
			vouchermap.put("ChildPackNum","");//�Ӱ�����
			vouchermap.put("CurPackNo","");//�������
			vouchermap.put("BillKind","");//��������
			vouchermap.put("FinOrgCode","");//�������ش���
			vouchermap.put("TreCode","");//�����������
			vouchermap.put("TreName","");//������������
			vouchermap.put("BgtTypeCode","");//Ԥ�����ͱ���
			vouchermap.put("BgtTypeName","");//Ԥ����������
			vouchermap.put("BeginDate","");//������ʼ����
			vouchermap.put("EndDate","");//������ֹ����
			vouchermap.put("AllNum","");//�ܱ���
			vouchermap.put("AllAmt","");//�ܽ��
			vouchermap.put("Hold1","");//Ԥ���ֶ�1
			vouchermap.put("Hold2","");//Ԥ���ֶ�2
			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getSstyear());		
			vouchermap.put("VtCode", vDto.getSvtcode());		
			vouchermap.put("VouDate", vDto.getScreatdate());		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("BillKind", vDto.getScheckvouchertype());//��������
			vouchermap.put("ReportDate", vDto.getScheckdate());		
			vouchermap.put("FinOrgCode", vDto.getSpaybankcode());
			vouchermap.put("TreCode", vDto.getStrecode());
			vouchermap.put("TreName", treName);
			vouchermap.put("BgtTypeCode", "");
			vouchermap.put("BgtTypeName", "");
			vouchermap.put("Hold1", "");	
			vouchermap.put("Hold2", "");
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();
			for (TrTaxorgPayoutReportDto dto:list) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id","");//���
				Detailmap.put("ExpFuncCode","");//֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName","");//֧�����ܷ����Ŀ����
				Detailmap.put("CurPayAmt","");//���ڽ��
				Detailmap.put("SumPayAmt","");//�ۼƽ��
				Detailmap.put("Hold1","");//Ԥ���ֶ�1
				Detailmap.put("Hold2","");//Ԥ���ֶ�2
				Detailmap.put("Hold3","");//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4
				Detailmap.put("ExpFuncCode", dto.getSbudgetsubcode()); // ��������
				Detailmap.put("ExpFuncName", dto.getSbudgetsubname()); 
				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
				Detailmap.put("TenDayAmt", MtoCodeTrans.transformString(dto.getNmoneytenday())); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("QuarterAmt", MtoCodeTrans.transformString(dto.getNmoneyquarter())); 
				Detailmap.put("YearAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
				if(dto.getSbudgetsubcode().length()==3)
					allamt=allamt.add(dto.getNmoneyday());
				Detail.add(Detailmap);
			}		
			vouchermap.put("SumMoney",MtoCodeTrans.transformString(allamt));		
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			returnmap.put("SumMoney", allamt);
			returnmap.put("Map", map);
			return returnmap;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
		
	}
	private List getSubLists(List list,int subsize)
	{
		List getList = null;
		if(list!=null&&list.size()>0)
		{
			if(subsize<1)
				subsize=500;
			int count = list.size()/subsize;
			int yu = list.size()%subsize;
			getList = new ArrayList();
			for(int i=0;i<count;i++)
				getList.add(list.subList(i*subsize, subsize*(i+1)));
			if(yu>0)
				getList.add(list.subList(count*subsize, (count*subsize)+yu));
		}
		return getList;
	}
	private TfReportDefrayMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReportDefrayMainDto mainDto = new TfReportDefrayMainDto();
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		mainDto.setSdemo("����ɹ�");
		mainDto.setSorgcode(vDto.getSorgcode());
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		mainDto.setIvousrlno(Long.valueOf(voucherno));
		mainDto.setSadmdivcode(getString(mainMap,"AdmDivCode"));//AdmDivCode",vDto.getSadmdivcode());//������������
		mainDto.setSstyear(getString(mainMap,"StYear"));//StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
		mainDto.setSvtcode(getString(mainMap,"VtCode"));//VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
		mainDto.setSvoudate(getString(mainMap,"VouDate"));//VouDate",vDto.getScreatdate());//ƾ֤����
		mainDto.setSvoucherno(getString(mainMap,"VoucherNo"));//VoucherNo",vDto.getSvoucherno());//ƾ֤��
		mainDto.setSvouchercheckno(getString(mainMap,"VoucherCheckNo"));//VoucherCheckNo",danhao);//���˵���
		mainDto.setSchildpacknum(getString(mainMap,"ChildPackNum"));//ChildPackNum",count);//�Ӱ�����
		mainDto.setScurpackno(getString(mainMap,"CurPackNo"));//CurPackNo",xuhao);//�������
		mainDto.setSbillkind(getString(mainMap,"BillKind"));//BillKind","1".equals(vDto.getScheckvouchertype())?"1":"2");//��������1,��Ӧ1-һ��Ԥ��֧������3��Ӧ2-����֧��
		mainDto.setSfinorgcode(getString(mainMap,"FinOrgCode"));//FinOrgCode",cDto.getSfinorgcode());//�������ش���
		mainDto.setStrecode(getString(mainMap,"TreCode"));//TreCode",vDto.getStrecode());//�����������
		mainDto.setStrename(getString(mainMap,"TreName"));//TreName"
		mainDto.setSbgttypecode(getString(mainMap,"BgtTypeCode"));//BgtTypeCode",vDto.getShold1());//Ԥ�����ͱ���
		mainDto.setSbgttypename(getString(mainMap,"BgtTypeName"));//BgtTypeName","1".equals(vDto.getShold1())?"Ԥ����":"Ԥ����");//Ԥ����������
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//������ʼ����
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//������ֹ����
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//�ܱ���
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//�ܽ��
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
		mainDto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
		return mainDto;
	}
	private TfReportDefraySubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReportDefraySubDto subDto = new TfReportDefraySubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//���
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//ExpFuncCode",dto.getSbudgetsubcode());//֧�����ܷ����Ŀ����
		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//ExpFuncName",dto.getSbudgetsubname());//֧�����ܷ����Ŀ����
		subDto.setNcurpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"CurPayAmt")));//CurPayAmt",dto.getNmoneyday());//���ڽ��
		subDto.setNsumpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"SumPayAmt")));//SumPayAmt",dto.getNmoneymonth());//�ۼƽ��
		subDto.setShold1(getString(subMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
		subDto.setShold2(getString(subMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
		subDto.setShold3(getString(subMap,"Hold3"));//Hold3","");//Ԥ���ֶ�3
		subDto.setShold4(getString(subMap,"Hold4"));//Hold4","");//Ԥ���ֶ�4
		subDto.setSxcheckresult("0");//���˽��Ĭ�ϳɹ�
		return subDto;
	}
	private String getString(Map datamap,String key)
	{
		if(datamap==null||key==null)
			return "";
		else
			return String.valueOf(datamap.get(key));
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}
}
