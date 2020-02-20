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
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeSubDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * 8.5.3.9	�������뱨���������3511��ִ5511ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5511 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5511.class);
											
	/**
	 * ƾ֤����
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	*/
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		if(vDto==null)
			return null;
		return getVoucher(vDto);	
	}
	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List returnList = new ArrayList();
		try {
			MainAndSubDto datadto =  get3511Data(vDto);
			List<IDto> alldata = getDataList(datadto);
			Map<String,Object> getMap = getSplitPack(alldata);
			if(getMap!=null&&getMap.size()>0&&datadto!=null&&datadto.getMainDto()!=null)
			{
				String FileName=null;
				String dirsep = File.separator; 
				String mainvou=VoucherUtil.getGrantSequence();
				FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
				TvVoucherinfoDto dto=new TvVoucherinfoDto();			
				dto.setSorgcode(vDto.getSorgcode());
				dto.setSadmdivcode(vDto.getSadmdivcode());
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5511);
				dto.setScreatdate(TimeFacade.getCurrentStringTime());
				dto.setStrecode(vDto.getStrecode());
				dto.setSfilename(FileName);
				dto.setSdealno(mainvou);		
				dto.setSstyear(dto.getScreatdate().substring(0, 4));				
				dto.setScheckdate(vDto.getScheckdate());
				dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
				dto.setShold3(vDto.getShold3());
				dto.setShold4(vDto.getShold4());
				dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				dto.setSdemo("����ɹ�");
				dto.setSvoucherflag("1");
				dto.setSvoucherno(mainvou);	
				dto.setIcount(getMap.size());
				dto.setSext1(vDto.getSdealno());
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map=tranfor(datadto,getMap,dto);
				List vouList=new ArrayList();
				vouList.add(map);
				vouList.add(dto);
				vouList.add(idtoList);
				returnList.add(vouList);
			}
		} catch (Exception e2) {		
			logger.error(e2);
			throw new ITFEBizException("��Ϣ�쳣��",e2);
		}
		return returnList;
	}
	private Map tranfor(MainAndSubDto datadto,Map<String,Object> getMap,TvVoucherinfoDto dto) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TfReportIncomeMainDto main3511dto = (TfReportIncomeMainDto)datadto.getMainDto();
			TfReportIncomeSubDto sub3511dto = null;
			map.put("Voucher", vouchermap);// ���ñ��Ľڵ� Voucher
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",main3511dto.getSadmdivcode());//������������
			vouchermap.put("StYear",main3511dto.getSstyear());//ҵ�����
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5511);//ƾ֤���ͱ��
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//ƾ֤����
			vouchermap.put("VoucherNo",dto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",main3511dto.getSvouchercheckno());//���˵���
			vouchermap.put("ChildPackNum",main3511dto.getSchildpacknum());//�Ӱ�����
			vouchermap.put("CurPackNo",main3511dto.getScurpackno());//�������
			vouchermap.put("FundTypeCode",main3511dto.getSfundtypecode());//�ʽ�����
			vouchermap.put("BelongFlag",main3511dto.getSbelongflag());//Ͻ����־
			vouchermap.put("BudgetLevelCode",main3511dto.getSbudgetlevelcode());//Ԥ�㼶��
			vouchermap.put("BillKind",main3511dto.getSbillkind());//��������
			vouchermap.put("FinOrgCode",main3511dto.getSfinorgcode());//�������ش���
			vouchermap.put("TreCode",main3511dto.getStrecode());//�����������
			vouchermap.put("TreName",main3511dto.getStrename());//������������
			vouchermap.put("BeginDate",main3511dto.getSbegindate());//������ʼ����
			vouchermap.put("EndDate",main3511dto.getSenddate());//������ֹ����
			vouchermap.put("AllNum",getMap.get("allnum"));//�ܱ���
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//�ܽ��	
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			Map<String,String> tempMap = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3511dto = (TfReportIncomeSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("AdmDivCode",sub3511dto.getSadmdivcode());//������������
				Detailmap.put("StYear",sub3511dto.getSstyear());//ҵ�����
				Detailmap.put("TaxOrgCode",sub3511dto.getStaxorgcode());//���ջ��ش���
				Detailmap.put("TaxOrgName",sub3511dto.getStaxorgname());//���ջ�������
				Detailmap.put("BudgetType",sub3511dto.getSbudgettype());//�ʽ�����
				Detailmap.put("BudgetLevelCode",sub3511dto.getSbudgetlevelcode());//Ԥ�㼶��
				Detailmap.put("BudgetSubjectCode",sub3511dto.getSbudgetsubjectcode());//��������Ŀ����
				Detailmap.put("BudgetSubjectName",sub3511dto.getSbudgetsubjectname());//��������Ŀ����
				Detailmap.put("CurIncomeAmt",String.valueOf(sub3511dto.getNcurincomeamt()));//���ڽ��
				Detailmap.put("SumIncomeAmt",String.valueOf(sub3511dto.getNsumincomeamt()));//�ۼƽ��
				tempdto = getObject(getMap,sub3511dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					Detailmap.put("XCheckReason","�޴�ƾ֤");//����ԭ��
				}else if(tempdto instanceof Map)
				{
					tempMap = (Map<String,String>)tempdto;
					if(sub3511dto.getNcurincomeamt()!=null&&sub3511dto.getNcurincomeamt()!=null&&sub3511dto.getNcurincomeamt().toString().equals(tempMap.get("CurIncomeAmt")))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","����,�������:"+tempMap.get("CurIncomeAmt"));//����ԭ��
					}
					
				}
				Detailmap.put("Hold1","");//Ԥ���ֶ�1
				Detailmap.put("Hold2","");//Ԥ���ֶ�2
				Detailmap.put("Hold3","");//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4

				Detail.add(Detailmap);
			}
			if(main3511dto.getSallnum()!=null&&main3511dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3511dto.getNallamt()!=null&&main3511dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
					vouchermap.put("XDiffNum","0");//��������
				}else
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					vouchermap.put("XDiffNum",error);//�������
				}
			}else
			{
				if(main3511dto.getNallamt()!=null&&main3511dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					vouchermap.put("XDiffNum",error);//��������
				}else
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					vouchermap.put("XDiffNum",error);//��������
				}
			}
			vouchermap.put("Hold1","");//Ԥ���ֶ�1
			vouchermap.put("Hold2","");//Ԥ���ֶ�2
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<IDto> getDataList(MainAndSubDto dto3511) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReportIncomeMainDto vDto = (TfReportIncomeMainDto)dto3511.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();

			sql = new StringBuffer("SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getSbelongflag()!=null&&!"".equals(vDto.getSbelongflag().trim()))
				sql.append(" and S_BELONGFLAG='"+vDto.getSbelongflag()+"' ");
			if(vDto.getSbudgetlevelcode()!=null&&!"".equals(vDto.getSbudgetlevelcode().trim()))
				sql.append(" and S_BUDGETLEVELCODE='"+vDto.getSbelongflag()+"' ");
			if(vDto.getSbillkind()!=null&&!"".equals(vDto.getSbillkind().trim()))
				sql.append(" and S_BILLKIND='"+vDto.getSbillkind()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode().trim()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode()+"' ");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTR_INCOMEDAYRPT","TR_INCOMEDAYRPT"),TrIncomedayrptDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e.getMessage(),e);
			throw new ITFEBizException("��ѯ"+sql==null?"":sql.toString()+"�����ձ���ִ��Ϣ5511�쳣��",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return getList;
	}
	private Map<String,Object> getSplitPack(List<IDto> dataList)
	{
		Map<String,Object> getMap = null;
		BigDecimal allamt=new BigDecimal("0.00");
		if(dataList!=null&&dataList.size()>0)
		{
			getMap = new HashMap<String,Object>();
			Map<String,String> dataMap = null;
			TrIncomedayrptDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (TrIncomedayrptDto)idto;
				if(getMap.get(dto.getStaxorgcode()+dto.getSbudgetlevelcode()+dto.getSbudgetsubcode())==null)
				{
					dataMap = new HashMap<String,String>();
					dataMap.put("AdmDivCode","");//������������
					dataMap.put("StYear","");//ҵ�����
					dataMap.put("TaxOrgCode",dto.getStaxorgcode());//���ջ��ش���
					dataMap.put("TaxOrgName","");//���ջ�������
					dataMap.put("BudgetType",dto.getSbudgettype());//�ʽ�����
					dataMap.put("BudgetLevelCode",dto.getSbudgetlevelcode());//Ԥ�㼶��
					dataMap.put("BudgetSubjectCode",dto.getSbudgetsubcode());//��������Ŀ����
					dataMap.put("BudgetSubjectName",dto.getSbudgetsubname());//��������Ŀ����
					dataMap.put("CurIncomeAmt",String.valueOf(dto.getNmoneyday()));//���ڽ��
					dataMap.put("SumIncomeAmt",String.valueOf(dto.getNmoneyyear()));//�ۼƽ��
					dataMap.put("Hold1","");//Ԥ���ֶ�1
					dataMap.put("Hold2","");//Ԥ���ֶ�2
					dataMap.put("Hold3","");//Ԥ���ֶ�3
					dataMap.put("Hold4","");//Ԥ���ֶ�4
					getMap.put(dto.getStaxorgcode()+dto.getSbudgetlevelcode()+dto.getSbudgetsubcode(),dataMap);
					allamt=allamt.add(dto.getNmoneyday());
				}else
				{
					dataMap = (Map<String,String>)getMap.get(dto.getStaxorgcode()+dto.getSbudgetlevelcode()+dto.getSbudgetsubcode());
					dataMap.put("CurIncomeAmt",String.valueOf(new BigDecimal(dataMap.get("CurIncomeAmt")).add(dto.getNmoneyday())));
					dataMap.put("SumIncomeAmt",String.valueOf(dto.getNmoneyyear()));//�ۼƽ��
				}
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", getMap.size());
		}
		return getMap;
	}
	private MainAndSubDto get3511Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_REPORT_INCOME_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReportIncomeMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_REPORT_INCOME_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReportIncomeSubDto.class).getDtoCollection());
				}
				
			} catch (JAFDatabaseException e) {
				logger.error(e.getMessage(),e);
				throw new ITFEBizException("��ִ2507ʱ��ѯ3507������Ϣ�쳣��",e);
			}finally
			{
				if(execDetail!=null)
					execDetail.closeConnection();
			}
		}
		return dataDto;
	}
	private Object getObject(Map map,TfReportIncomeSubDto sub3511dto)
	{
		Object getObject = null;
		if(map!=null&&sub3511dto!=null)
		{
			getObject = map.get(sub3511dto.getStaxorgcode()+sub3511dto.getSbudgetlevelcode()+sub3511dto.getSbudgetsubjectcode());
			
		}
		return getObject;
	}
}
