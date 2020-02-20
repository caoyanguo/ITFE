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
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 *����ʵ����Ϣ��������3508��ִ5508ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5508 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5508.class);
											
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
			MainAndSubDto datadto =  get3508Data(vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5508);
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
			TfReconcileRealdialMainDto main3508dto = (TfReconcileRealdialMainDto)datadto.getMainDto();
			TfReconcileRealdialSubDto sub3508dto = null;
			map.put("Voucher", vouchermap);// ���ñ��Ľڵ� Voucher
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",main3508dto.getSadmdivcode());//������������
			vouchermap.put("StYear",main3508dto.getSstyear());//ҵ�����
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5508);//ƾ֤���ͱ��
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//ƾ֤����
			vouchermap.put("VoucherNo",dto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",main3508dto.getSvouchercheckno());//���˵���
			vouchermap.put("ChildPackNum",main3508dto.getSchildpacknum());//�Ӱ�����
			vouchermap.put("CurPackNo",main3508dto.getScurpackno());//�������
			vouchermap.put("TreCode",main3508dto.getStrecode());//�����������
			vouchermap.put("ClearBankCode",main3508dto.getSclearbankcode());//�������б���
			vouchermap.put("ClearBankName",main3508dto.getSclearbankname());//������������
			vouchermap.put("ClearAccNo",main3508dto.getSclearaccno());//�����˺�
			vouchermap.put("ClearAccNanme",main3508dto.getSclearaccnanme());//�����˻�����
			vouchermap.put("BeginDate",main3508dto.getSbegindate());//������ʼ����
			vouchermap.put("EndDate",main3508dto.getSenddate());//������ֹ����
			vouchermap.put("AllNum",getMap.get("allcount"));//�ܱ���
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//�ܽ��
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			TvPayoutmsgsubDto sub5207 = null;
			TvPayoutbackmsgSubDto sub5207back = null;
			TvVoucherinfoAllocateIncomeDto sub5207import = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3508dto = (TfReconcileRealdialSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3508dto.getSid());//���
				Detailmap.put("PayDetailId",sub3508dto.getSpaydetailid());//������ϸId
				Detailmap.put("BgtTypeCode",sub3508dto.getSbgttypecode());//Ԥ�����ͱ���
				Detailmap.put("BgtTypeName",sub3508dto.getSbgttypename());//Ԥ����������
				Detailmap.put("FundTypeCode",sub3508dto.getSfundtypecode());//�ʽ����ʱ���
				Detailmap.put("FundTypeName",sub3508dto.getSfundtypename());//�ʽ���������
				Detailmap.put("PayTypeCode",sub3508dto.getSpaytypecode());//֧����ʽ����
				Detailmap.put("PayTypeName",sub3508dto.getSpaytypename());//֧����ʽ����
				Detailmap.put("PayeeAcctNo",sub3508dto.getSpayeeacctno());//�տ����˺�
				Detailmap.put("PayeeAcctName",sub3508dto.getSpayeeacctname());//�տ�������
				Detailmap.put("PayeeAcctBankName",sub3508dto.getSpayeeacctbankname());//�տ�������
				Detailmap.put("PayAcctNo",sub3508dto.getSpayacctno());//�����˻��˺�
				Detailmap.put("PayAcctName",sub3508dto.getSpayacctname());//�����˻�����
				Detailmap.put("PayAcctBankName",sub3508dto.getSpayacctbankname());//�����˻�����
				Detailmap.put("AgencyCode",sub3508dto.getSagencycode());//Ԥ�㵥λ����
				Detailmap.put("AgencyName",sub3508dto.getSagencyname());//Ԥ�㵥λ����
				Detailmap.put("ExpFuncCode",sub3508dto.getSexpfunccode());//֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName",sub3508dto.getSexpfuncname());//֧�����ܷ����Ŀ����
				Detailmap.put("ExpEcoCode",sub3508dto.getSexpecocode());//���÷����Ŀ����
				Detailmap.put("ExpEcoName",sub3508dto.getSexpeconame());//���÷����Ŀ����
				Detailmap.put("PayAmt",String.valueOf(sub3508dto.getNpayamt()));//������
				Detailmap.put("XCheckResult","");//���˽��
				Detailmap.put("XCheckReason","");//����ԭ��
				Detailmap.put("Hold1","");//Ԥ���ֶ�1
				Detailmap.put("Hold2","");//Ԥ���ֶ�2
				Detailmap.put("Hold3","");//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4

				tempdto = getObject(getMap,main3508dto,sub3508dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					Detailmap.put("XCheckReason","�޴�ƾ֤");//����ԭ��
				}else if(tempdto instanceof TvPayoutmsgsubDto)
				{
					sub5207 = (TvPayoutmsgsubDto)tempdto;
					if(sub5207.getNmoney()!=null&&sub3508dto.getNpayamt()!=null&&sub5207.getNmoney().abs().toString().equals(sub3508dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","����");//����ԭ��
					}
					
				}else if (tempdto instanceof TvPayoutbackmsgSubDto)
				{
					sub5207back = (TvPayoutbackmsgSubDto)tempdto;
					if(sub5207back.getNmoney()!=null&&sub3508dto.getNpayamt()!=null&&sub5207back.getNmoney().abs().toString().equals(sub3508dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","����");//����ԭ��
					}
				}else if(tempdto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					sub5207import = (TvVoucherinfoAllocateIncomeDto)tempdto;
					if(sub5207import.getNmoney()!=null&&sub3508dto.getNpayamt()!=null&&sub5207import.getNmoney().abs().toString().equals(sub3508dto.getNpayamt().abs().toString()))
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
			if(main3508dto.getSallnum()!=null&&main3508dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3508dto.getNallamt()!=null&&main3508dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
					vouchermap.put("XDiffNum",error);//��������
				}else
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					vouchermap.put("XDiffNum",error);//�������
				}
			}else
			{
				if(main3508dto.getNallamt()!=null&&main3508dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
	private List<IDto> getDataList(MainAndSubDto dto3508) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReconcileRealdialMainDto vDto = (TfReconcileRealdialMainDto)dto3508.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			//5207ʵ���ʽ��Ѿ��ص�������
			sql = new StringBuffer("SELECT * FROM HTV_PAYOUTMSGMAIN WHERE I_VOUSRLNO in(");
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5207+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"') and S_PAYERACCT='"+vDto.getSclearaccno()+"'");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYOUTMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//ʵ���ʽ�����
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getSbizno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getSbizno())));//��ʷ���ӱ�
					getList.add(datadto);
				}
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),TvPayoutmsgmainDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),"*", "I_VOUSRLNO")+")";//ʵ���ʽ��ӱ�����
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getSbizno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getSbizno())));//��ʽ���ӱ�
					getList.add(datadto);
				}
			}
			sql = new StringBuffer("SELECT * FROM TV_PAYOUTBACKMSG_MAIN WHERE S_VOUNO IN(");//��ѯtcbs��ִ�����˿��Ѿ�����ƾ֤�������
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_3208+"'  AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"') and S_PAYERACCT='"+vDto.getSclearaccno()+"'");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutbackmsgMainDto.class).getDtoCollection();//��ʷ������
			detailList.addAll(execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvPayoutbackmsgMainDto.class).getDtoCollection());//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTBACKMSG_SUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutbackmsgSubDto.class).getDtoCollection();
				subList.addAll(execDetail.runQuery(StringUtil.replace(subsql,"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvPayoutbackmsgSubDto.class).getDtoCollection());
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutbackmsgSubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutbackmsgSubDto)tempdto;
						tempList = subMap.get(subdto.getSbizno());
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayoutbackmsgMainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutbackmsgMainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(tempdto.getSbizno()));//��ʷ���ӱ�
					getList.add(datadto);
				}
			}
			sql = new StringBuffer("SELECT * FROM TV_VOUCHERINFO_ALLOCATE_INCOME WHERE S_DEALNO IN(");//��ѯʵ���ʽ���ʷ���Ѿ��ص�������
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_3208+"'  AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"') and S_PAYACCTNO='"+vDto.getSclearaccno()+"'");
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvVoucherinfoAllocateIncomeDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				MainAndSubDto datadto = null;
				TvVoucherinfoAllocateIncomeDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvVoucherinfoAllocateIncomeDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					getList.add(datadto);
				}
			}
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e);
			throw new ITFEBizException("��ѯ"+sql==null?"":sql.toString()+"��ϸ��Ϣ�쳣��",e);
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
			MainAndSubDto dto = null;
			int count=0;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(dto.getMainDto() instanceof TvPayoutmsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					TvPayoutmsgmainDto maindto = (TvPayoutmsgmainDto)dto.getMainDto();
					TvPayoutmsgsubDto subdto = null;
					for(int i=0;i<dto.getSubDtoList().size();i++)
					{
						count++;
						subdto = (TvPayoutmsgsubDto)dto.getSubDtoList().get(i);
						getMap.put(maindto.getStaxticketno()+subdto.getSid(), subdto);
						getMap.put(subdto.getSid(), subdto);
						getMap.put(subdto.getSbgttypecode()+maindto.getSfundtypecode()+maindto.getSpaytypecode()
								+maindto.getSrecacct()+maindto.getSpayeracct()+maindto.getSbudgetunitcode()+subdto.getSexpecocode()+subdto.getNmoney(),subdto);//ƾ֤���+Ԥ�㵥λ����+�տ��˱��+�����˱��+���
						//Ԥ�����ͱ���+�ʽ����ʱ���+֧����ʽ����+�տ����˺�+�����˻��˺�+Ԥ�㵥λ����+���÷����Ŀ����+������
						allamt=allamt.add(subdto.getNmoney());
					}
				}else if(dto.getMainDto() instanceof TvPayoutbackmsgMainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					TvPayoutbackmsgMainDto maindto = (TvPayoutbackmsgMainDto)dto.getMainDto();
					TvPayoutbackmsgSubDto subdto = null;
					for(int i=0;i<dto.getSubDtoList().size();i++)
					{
						count++;
						subdto = (TvPayoutbackmsgSubDto)dto.getSubDtoList().get(i);
						getMap.put(maindto.getSvouno()+subdto.getSseqno(), subdto);
						getMap.put(subdto.getSbudgetprjcode()+maindto.getSpayeeacct()+maindto.getSpayeracct()+maindto.getSbudgetunitcode()+subdto.getSecnomicsubjectcode()+subdto.getNmoney(),subdto
								);//Ԥ�����ͱ���+�տ����˺�+�����˻��˺�+Ԥ�㵥λ����+���÷����Ŀ����+������
						allamt=allamt.subtract(subdto.getNmoney().abs());
					}
				}
				else if(dto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto)
				{
					count++;
					TvVoucherinfoAllocateIncomeDto maindto = (TvVoucherinfoAllocateIncomeDto)dto.getMainDto();
					getMap.put(maindto.getSpaydealno(), maindto);
					getMap.put(maindto.getSdealno(), maindto);
					getMap.put(maindto.getSpayacctno()+maindto.getSpayeeacctno()+maindto.getNmoney(),maindto);
					allamt=allamt.subtract(maindto.getNmoney().abs());
				}
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", count);
		}
		return getMap;
	}
	private MainAndSubDto get3508Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_RECONCILE_REALDIAL_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReconcileRealdialMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_RECONCILE_REALDIAL_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReconcileRealdialSubDto.class).getDtoCollection());
				}
				
			} catch (JAFDatabaseException e) {
				logger.error(e.getMessage(),e);
				throw new ITFEBizException("��ִ5508ʱ��ѯ3508������Ϣ�쳣��",e);
			}finally
			{
				if(execDetail!=null)
					execDetail.closeConnection();
			}
		}
		return dataDto;
	}
	private Object getObject(Map map,TfReconcileRealdialMainDto main3508dto,TfReconcileRealdialSubDto sub3508dto)
	{
		Object getObject = null;
		if(map!=null&&sub3508dto!=null)
		{
			getObject = map.get(sub3508dto.getSpaydetailid());
			if(getObject==null)
			{
				getObject = map.get(sub3508dto.getSid());
				if(getObject==null)
				{
					getObject = map.get(sub3508dto.getSbgttypecode()+sub3508dto.getSfundtypecode()+sub3508dto.getSpaytypecode()+sub3508dto.getSpayeeacctno()+
						sub3508dto.getSpayacctno()+sub3508dto.getSagencycode()+sub3508dto.getSexpecocode()+sub3508dto.getNpayamt()
						);//Ԥ�����ͱ���+�ʽ����ʱ���+֧����ʽ����+�տ����˺�+�����˻��˺�+Ԥ�㵥λ����+���÷����Ŀ����+������
					if(getObject==null)
					{
						getObject = map.get(sub3508dto.getSbgttypecode()+sub3508dto.getSpayeeacctno()+
								sub3508dto.getSpayacctno()+sub3508dto.getSagencycode()+sub3508dto.getSexpecocode()+sub3508dto.getNpayamt()
								);//Ԥ�����ͱ���+�տ����˺�+�����˻��˺�+Ԥ�㵥λ����+���÷����Ŀ����+������
					}
				}
			}
		}
		return getObject;
	}
}
