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
import com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundSubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 *���������˸���������3509��ִ5509ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5509 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5509.class);
											
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
			MainAndSubDto datadto =  get3509Data(vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5509);
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
			TfReconcileRefundMainDto main3509dto = (TfReconcileRefundMainDto)datadto.getMainDto();
			TfReconcileRefundSubDto sub3509dto = null;
			map.put("Voucher", vouchermap);// ���ñ��Ľڵ� Voucher
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",main3509dto.getSadmdivcode());//������������
			vouchermap.put("StYear",main3509dto.getSstyear());//ҵ�����
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5509);//ƾ֤���ͱ��
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//ƾ֤����
			vouchermap.put("VoucherNo",dto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",main3509dto.getSvouchercheckno());//���˵���
			vouchermap.put("ChildPackNum",main3509dto.getSchildpacknum());//�Ӱ�����
			vouchermap.put("CurPackNo",main3509dto.getScurpackno());//�������
			vouchermap.put("TreCode",main3509dto.getStrecode());//�����������
			vouchermap.put("ClearBankCode","");//�������б���
			vouchermap.put("ClearBankName","");//������������
			vouchermap.put("ClearAccNo",main3509dto.getSclearaccno());//�����˺�
			vouchermap.put("ClearAccNanme",main3509dto.getSclearaccnanme());//�����˻�����
			vouchermap.put("BeginDate",main3509dto.getSbegindate());//������ʼ����
			vouchermap.put("EndDate",main3509dto.getSenddate());//������ֹ����
			vouchermap.put("AllNum",getMap.get("allcount"));//�ܱ���
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//�ܽ��
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			int error = 0;
			TvDwbkDto dwdto = null;
			TvVoucherinfoAllocateIncomeDto alldto = null;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3509dto = (TfReconcileRefundSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3509dto.getSid());//���
				Detailmap.put("PayDetailId",sub3509dto.getSpaydetailid());//�˸���ϸId
				Detailmap.put("FundTypeCode",sub3509dto.getSfundtypecode());//�ʽ����ʱ���
				Detailmap.put("FundTypeName",sub3509dto.getSfundtypecode());//�ʽ���������
				Detailmap.put("PayeeAcctNo",sub3509dto.getSpayeeacctno());//�տ����˺�
				Detailmap.put("PayeeAcctName",sub3509dto.getSpayeeacctname());//�տ�������
				Detailmap.put("PayeeAcctBankName",sub3509dto.getSpayeeacctbankname());//�տ�������
				Detailmap.put("PayAcctNo",sub3509dto.getSpayacctno());//�����˻��˺�
				Detailmap.put("PayAcctName",sub3509dto.getSpayacctname());//�����˻�����
				Detailmap.put("PayAcctBankName",sub3509dto.getSpayacctbankname());//�����˻�����
				Detailmap.put("AgencyCode",sub3509dto.getSagencycode());//Ԥ�㵥λ����
				Detailmap.put("AgencyName",sub3509dto.getSagencyname());//Ԥ�㵥λ����
				Detailmap.put("IncomeSortCode",sub3509dto.getSincomesortcode());//��������Ŀ����
				Detailmap.put("IncomeSortName",sub3509dto.getSincomesortname());//��������Ŀ����
				Detailmap.put("PayAmt",String.valueOf(sub3509dto.getNpayamt()));//�˸����
				tempdto = getObject(getMap,sub3509dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					Detailmap.put("XCheckReason","�޴�ƾ֤");//����ԭ��
				}else if(tempdto instanceof TvDwbkDto)
				{
					dwdto = (TvDwbkDto)tempdto;
					if(dwdto.getFamt()!=null&&sub3509dto.getNpayamt()!=null&&dwdto.getFamt().abs().toString().equals(sub3509dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","����");//����ԭ��
					}
					
				}else if (tempdto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					alldto = (TvVoucherinfoAllocateIncomeDto)tempdto;
					if(alldto.getNmoney()!=null&&sub3509dto.getNpayamt()!=null&&alldto.getNmoney().abs().toString().equals(sub3509dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","����");//����ԭ��
					}
				}
				Detailmap.put("Hold1","");//Ԥ���ֶ�1
				Detailmap.put("Hold2","");//Ԥ���ֶ�2
				Detailmap.put("Hold3","");//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4

				Detail.add(Detailmap);
			}
			if(main3509dto.getSallnum()!=null&&main3509dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3509dto.getNallamt()!=null&&main3509dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
				if(main3509dto.getNallamt()!=null&&main3509dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
	private List<IDto> getDataList(MainAndSubDto dto3507) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReconcileRefundMainDto vDto = (TfReconcileRefundMainDto)dto3507.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			
			sql = new StringBuffer("SELECT * FROM HTV_DWBK WHERE D_ACCEPT BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode());
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvDwbkDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_DWBK","TV_DWBK"),TvDwbkDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			sql = new StringBuffer("SELECT * FROM TV_VOUCHERINFO_ALLOCATE_INCOME WHERE S_COMMITDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode());
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvVoucherinfoAllocateIncomeDto.class).getDtoCollection();//��ʷ������
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
			throw new ITFEBizException("��ѯ"+sql==null?"":sql.toString()+"�����ϸ��Ϣ�쳣��",e);
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
			int count=0;
			for(IDto idto:dataList)
			{
				count++;
				if(idto instanceof TvDwbkDto)
				{
					TvDwbkDto maindto = (TvDwbkDto)idto;
					getMap.put(maindto.getSbizno(), maindto);
					
				}else if(idto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					TvVoucherinfoAllocateIncomeDto maindto = (TvVoucherinfoAllocateIncomeDto)idto;
					getMap.put(maindto.getSdealno(), maindto);
				}
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", count);
		}
		return getMap;
	}
	private MainAndSubDto get3509Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_RECONCILE_REFUND_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReconcileRefundMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_RECONCILE_REFUND_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReconcileRefundSubDto.class).getDtoCollection());
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
	private Object getObject(Map map,TfReconcileRefundSubDto sub3509dto)
	{
		Object getObject = null;
		if(map!=null&&sub3509dto!=null)
		{
			getObject = map.get(sub3509dto.getSid());
		}
		return getObject;
	}
}
