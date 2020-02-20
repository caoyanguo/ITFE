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
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * ������Ϣ����3507��ִ2507ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor2507 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2507.class);
											
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
			MainAndSubDto datadto =  get3507Data(vDto);
			List<IDto> alldata = getDataList(datadto,vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_2507);
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
				dto.setSext1(vDto.getSdealno());
				dto.setSpaybankcode(vDto.getSpaybankcode());
				dto.setIcount(getMap.size());
				dto.setNmoney(((TfReconcilePayinfoMainDto)datadto.getMainDto()).getNallamt());
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
			TfReconcilePayinfoMainDto main3507dto = (TfReconcilePayinfoMainDto)datadto.getMainDto();
			TfReconcilePayinfoSubDto sub3507dto = null;
			map.put("Voucher", vouchermap);// ���ñ��Ľڵ� Voucher
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",main3507dto.getSadmdivcode());//������������
			vouchermap.put("StYear",main3507dto.getSstyear());//ҵ�����
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_2507);//ƾ֤���ͱ��
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//ƾ֤����
			vouchermap.put("VoucherNo",dto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",main3507dto.getSvouchercheckno());//���˵���
			vouchermap.put("ChildPackNum",main3507dto.getSchildpacknum());//�Ӱ�����
			vouchermap.put("CurPackNo",main3507dto.getScurpackno());//�������
			vouchermap.put("TreCode",main3507dto.getStrecode());//�����������
			vouchermap.put("ClearBankCode","");//�������б���
			vouchermap.put("ClearBankName","");//������������
			vouchermap.put("ClearAccNo",main3507dto.getSclearaccno());//�����˺�
			vouchermap.put("ClearAccNanme",main3507dto.getSclearaccnanme());//�����˻�����
			vouchermap.put("BeginDate",main3507dto.getSbegindate());//������ʼ����
			vouchermap.put("EndDate",main3507dto.getSenddate());//������ֹ����
			vouchermap.put("AllNum",getMap.get("allcount"));//�ܱ���
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//�ܽ��	
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			TvPayreckBankListDto sub2301 = null;
			TvPayreckBankBackListDto sub2302 = null;
			TfDirectpaymsgsubDto sub5201 = null;
			TfPaybankRefundsubDto sub2252 = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3507dto = (TfReconcilePayinfoSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3507dto.getSid());//���
				Detailmap.put("PayAgentBillNo",sub3507dto.getSpayagentbillno());//���㵥��
				Detailmap.put("PayDetailId",sub3507dto.getSpaydetailid());//������ϸID
				Detailmap.put("SupDepCode",sub3507dto.getSsupdepcode());//һ��Ԥ�㵥λ����
				Detailmap.put("SupDepName",sub3507dto.getSexpfuncname());//һ��Ԥ�㵥λ����
				Detailmap.put("FundTypeCode",sub3507dto.getSfundtypecode());//�ʽ����ʱ���
				Detailmap.put("FundTypeName",sub3507dto.getSfundtypename());//�ʽ���������
				Detailmap.put("PayBankCode",sub3507dto.getSpaybankcode());//�������б���
				Detailmap.put("PayBankName",sub3507dto.getSpaybankname());//������������
				Detailmap.put("PayBankNo",sub3507dto.getSpaybankno());//���������к�
				Detailmap.put("ExpFuncCode",sub3507dto.getSexpfunccode());//֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName",sub3507dto.getSexpfuncname());//֧�����ܷ����Ŀ����
				Detailmap.put("ProCatCode",sub3507dto.getSprocatcode());//��֧�������
				Detailmap.put("ProCatName",sub3507dto.getSprocatname());//��֧��������
				Detailmap.put("PayTypeCode",sub3507dto.getSpaytypecode());//֧����ʽ����
				Detailmap.put("PayTypeName",sub3507dto.getSpaytypename());//֧����ʽ����
				Detailmap.put("PayAmt",String.valueOf(sub3507dto.getNpayamt()));//֧�����
				tempdto = getObject(getMap,sub3507dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					Detailmap.put("XCheckReason","�޴�ƾ֤");//����ԭ��
				}else if(tempdto instanceof TvPayreckBankListDto)
				{
					sub2301 = (TvPayreckBankListDto)tempdto;
					if(sub2301.getFamt()!=null&&sub3507dto.getNpayamt()!=null&&sub2301.getFamt().abs().toString().equals(sub3507dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","������ԭ��:�Է����"+main3507dto.getNallamt()+"-�������-"+sub2301.getFamt());//����ԭ��
					}
					
				}else if (tempdto instanceof TvPayreckBankBackListDto)
				{
					sub2302 = (TvPayreckBankBackListDto)tempdto;
					if(sub2302.getFamt()!=null&&sub3507dto.getNpayamt()!=null&&sub2302.getFamt().abs().toString().equals(sub3507dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","������ԭ��:�Է����"+main3507dto.getNallamt()+"-�������-"+sub2302.getFamt());//����ԭ��
					}
				}else if(tempdto instanceof TfDirectpaymsgsubDto)
				{
					sub5201 = (TfDirectpaymsgsubDto)tempdto;
					if(sub5201.getNpayamt()!=null&&sub3507dto.getNpayamt()!=null&&sub5201.getNpayamt().abs().toString().equals(sub3507dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","������ԭ��:�Է����"+main3507dto.getNallamt()+"-�������-"+sub5201.getNpayamt());//����ԭ��
					}
				}else if(tempdto instanceof TfPaybankRefundsubDto)
				{
					sub2252 = (TfPaybankRefundsubDto)tempdto;
					if(sub2252.getNpayamt()!=null&&sub3507dto.getNpayamt()!=null&&sub2252.getNpayamt().abs().toString().equals(sub3507dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","������ԭ��:�Է����"+main3507dto.getNallamt()+"-�������-"+sub2252.getNpayamt());//����ԭ��
					}
				}
				Detailmap.put("Hold1","");//Ԥ���ֶ�1
				Detailmap.put("Hold2","");//Ԥ���ֶ�2
				Detailmap.put("Hold3","");//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4

				Detail.add(Detailmap);
			}
			if(main3507dto.getSallnum()!=null&&main3507dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3507dto.getNallamt()!=null&&main3507dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
					vouchermap.put("XDiffNum",error);//��������
				}else
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					vouchermap.put("XDiffNum",error);//��������
				}
			}else
			{
				if(main3507dto.getNallamt()!=null&&main3507dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
	private List<IDto> getDataList(MainAndSubDto dto3507,TvVoucherinfoDto dto) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReconcilePayinfoMainDto vDto = (TfReconcilePayinfoMainDto)dto3507.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			//2301��ѯ���л����Ѿ��ص�������
			sql = new StringBuffer("SELECT * FROM HTV_PAYRECK_BANK WHERE I_VOUSRLNO in(");
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CREATDATE BETWEEN ? AND ?)");
			if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno())){
				sql.append(" and S_PAYERACCT= ?");
			}
			if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode())){
				sql.append(" and S_AGENTBNKCODE= ?");
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getSbegindate());
			execDetail.addParam(vDto.getSenddate());
			if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
				execDetail.addParam(vDto.getSclearaccno());
			if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
				execDetail.addParam(dto.getSpaybankcode());
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayreckBankDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYRECK_BANK_LIST where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//���л����ӱ�����
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getSbegindate());
				execDetail.addParam(vDto.getSenddate());
				if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
					execDetail.addParam(vDto.getSclearaccno());
				if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
					execDetail.addParam(dto.getSpaybankcode());
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankListDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayreckBankDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʷ���ӱ�
					getList.add(datadto);
				}
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getSbegindate());
			execDetail.addParam(vDto.getSenddate());
			if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
				execDetail.addParam(vDto.getSclearaccno());
			if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
				execDetail.addParam(dto.getSpaybankcode());
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYRECK_BANK","TV_PAYRECK_BANK"),TvPayreckBankDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYRECK_BANK_LIST where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYRECK_BANK","TV_PAYRECK_BANK"),"*", "I_VOUSRLNO")+")";//���л����ӱ�����
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getSbegindate());
				execDetail.addParam(vDto.getSenddate());
				if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
					execDetail.addParam(vDto.getSclearaccno());
				if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
					execDetail.addParam(dto.getSpaybankcode());
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankListDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayreckBankDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʽ���ӱ�
					getList.add(datadto);
				}
			}
			//2302��ѯ�����˿��Ѿ��ص�������
			sql = new StringBuffer("SELECT * FROM HTV_PAYRECK_BANK_BACK WHERE I_VOUSRLNO in(");//��ѯ���л����Ѿ��ص�������
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CREATDATE BETWEEN ? AND ?)");
			if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno())){
				sql.append(" and S_PAYERACCT= '"+vDto.getSclearaccno()+"'");
			}
			if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode())){
				sql.append(" and S_AGENTBNKCODE= '"+dto.getSpaybankcode()+"'");
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getSbegindate());
			execDetail.addParam(vDto.getSenddate());
			if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
				execDetail.addParam(vDto.getSclearaccno());
			if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
				execDetail.addParam(dto.getSpaybankcode());
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayreckBankBackDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getSbegindate());
				execDetail.addParam(vDto.getSenddate());
				if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
					execDetail.addParam(vDto.getSclearaccno());
				if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
					execDetail.addParam(dto.getSpaybankcode());
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankBackListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankListDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayreckBankBackDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankBackDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʷ���ӱ�
					getList.add(datadto);
				}
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getSbegindate());
			execDetail.addParam(vDto.getSenddate());
			if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
				execDetail.addParam(vDto.getSclearaccno());
			if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
				execDetail.addParam(dto.getSpaybankcode());
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_PAYRECK_BANK_BACK","TV_PAYRECK_BANK_BACK"),TvPayreckBankBackDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_PAYRECK_BANK_BACK","TV_PAYRECK_BANK_BACK"), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getSbegindate());
				execDetail.addParam(vDto.getSenddate());
				if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno()))
					execDetail.addParam(vDto.getSclearaccno());
				if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode()))
					execDetail.addParam(dto.getSpaybankcode());
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankBackListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankBackListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankBackListDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayreckBankBackDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankBackDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʽ���ӱ�
					getList.add(datadto);
				}
			}
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0)
			{
				//����ֱ��֧��ƾ֤5201����
				sql = new StringBuffer("SELECT * FROM HTF_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//��ѯ���л����Ѿ��ص�������
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5201+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
				sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
				sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"')");
				if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno())){
					sql.append(" and S_CLEARBANKCODE= '"+vDto.getSclearaccno()+"'");
				}
				if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode())){
					sql.append(" and S_PAYEEACCTBANKNO= '"+dto.getSpaybankcode()+"'");
				}
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TfDirectpaymsgmainDto.class).getDtoCollection();//��ʷ������
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from HTF_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					subList = (List<IDto>)execDetail.runQuery(subsql, TfDirectpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TfDirectpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TfDirectpaymsgsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}
						}
					}
					MainAndSubDto datadto = null;
					TfDirectpaymsgmainDto tempdto = null;
					for(int i=0;i<detailList.size();i++)
					{
						tempdto = (TfDirectpaymsgmainDto)detailList.get(i);
						datadto = new MainAndSubDto();
						datadto.setMainDto(tempdto);
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʷ���ӱ�
						getList.add(datadto);
					}
				}
				detailList=  (List<IDto>) execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_DIRECTPAYMSGMAIN","TF_DIRECTPAYMSGMAIN"),TfDirectpaymsgmainDto.class).getDtoCollection();//����
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from TF_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_DIRECTPAYMSGMAIN","TF_DIRECTPAYMSGMAIN"), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					subList = (List<IDto>)execDetail.runQuery(subsql, TfDirectpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TfDirectpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TfDirectpaymsgsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}
						}
					}
					MainAndSubDto datadto = null;
					TfDirectpaymsgmainDto tempdto = null;
					for(int i=0;i<detailList.size();i++)
					{
						tempdto = (TfDirectpaymsgmainDto)detailList.get(i);
						datadto = new MainAndSubDto();
						datadto.setMainDto(tempdto);
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//�ӱ�
						getList.add(datadto);
					}
				}
				sql = new StringBuffer("SELECT * FROM HTF_PAYBANK_REFUNDMAIN WHERE I_VOUSRLNO in(");//�տ������˿�֪ͨ2252������ʷ��
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_2252+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
				sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
				sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"')");
				if(null!=vDto.getSclearaccno()&&!"".equals(vDto.getSclearaccno())){
					sql.append(" and S_PAYSNDBNKNO= '"+vDto.getSclearaccno()+"'");
				}
				if(null!=dto.getSpaybankcode()&&!"".equals(dto.getSpaybankcode())){
					sql.append(" and S_PAYSNDBNKNO= '"+dto.getSpaybankcode()+"'");
				}
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TfPaybankRefundmainDto.class).getDtoCollection();//��ʷ������
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from HTF_PAYBANK_REFUNDSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					subList = (List<IDto>)execDetail.runQuery(subsql, TfPaybankRefundsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TfPaybankRefundsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TfPaybankRefundsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}
						}
					}
					Map<String,MainAndSubDto> map5201 = get5201Map(StringUtil.replace(StringUtil.replace(sql.toString(), "S_PAYSNDBNKNO", "S_CLEARBANKCODE"), "HTF_PAYBANK_REFUNDMAIN", "HTF_DIRECTPAYMSGMAIN"),StringUtil.replace(StringUtil.replace(subsql, "S_PAYSNDBNKNO", "S_CLEARBANKCODE"),"HTF_PAYBANK_REFUNDSUB","HTF_DIRECTPAYMSGSUB"),execDetail);
					MainAndSubDto datadto = null;
					TfPaybankRefundmainDto tempdto = null;
					for(int i=0;i<detailList.size();i++)
					{
						tempdto = (TfPaybankRefundmainDto)detailList.get(i);
						datadto = new MainAndSubDto();
						datadto.setMainDto(tempdto);
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʷ���ӱ�
						datadto.setExtdto(map5201.get(String.valueOf(tempdto.getIvousrlno())));
						getList.add(datadto);
					}
					
				}
				detailList=  (List<IDto>) execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_PAYBANK_REFUNDMAIN","TF_PAYBANK_REFUNDMAIN"),TfPaybankRefundmainDto.class).getDtoCollection();//����
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from TF_PAYBANK_REFUNDSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_PAYBANK_REFUNDMAIN","TF_PAYBANK_REFUNDMAIN"), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					subList = (List<IDto>)execDetail.runQuery(subsql, TfPaybankRefundsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TfPaybankRefundsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TfPaybankRefundsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
							}
						}
					}
					MainAndSubDto datadto = null;
					TfPaybankRefundmainDto tempdto = null;
					for(int i=0;i<detailList.size();i++)
					{
						tempdto = (TfPaybankRefundmainDto)detailList.get(i);
						datadto = new MainAndSubDto();
						datadto.setMainDto(tempdto);
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//�ӱ�
						getList.add(datadto);
					}
				}
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
	private Map<String, MainAndSubDto> get5201Map(String mainsql5201, String subsql5201,SQLExecutor execDetail) throws JAFDatabaseException {
		Map<String,MainAndSubDto> getMap = null;
		if(mainsql5201!=null&&subsql5201!=null)
		{
			List list5201 = (List<IDto>)execDetail.runQuery(mainsql5201,TfDirectpaymsgmainDto.class).getDtoCollection();
			if(list5201!=null&&list5201.size()>0)
			{
				getMap = new HashMap<String,MainAndSubDto>();
				List<IDto> subList = (List<IDto>)execDetail.runQuery(subsql5201, TfDirectpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TfDirectpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TfDirectpaymsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				TfDirectpaymsgmainDto tempdto = null;
				MainAndSubDto datadto = null;
				for(int i=0;i<list5201.size();i++)
				{
					tempdto = (TfDirectpaymsgmainDto)list5201.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));
					getMap.put(String.valueOf(tempdto.getIvousrlno()), datadto);
				}
			}
		}
		return getMap;
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
				if(dto.getMainDto() instanceof TvPayreckBankDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					TvPayreckBankDto maindto = (TvPayreckBankDto)dto.getMainDto();
					TvPayreckBankListDto subdto = null;
					for(int i=0;i<dto.getSubDtoList().size();i++)
					{
						count++;
						subdto = (TvPayreckBankListDto)dto.getSubDtoList().get(i);
						getMap.put(maindto.getSvouno()+subdto.getSid(), subdto);
						getMap.put(maindto.getSvouno()+subdto.getSbdgorgcode()+maindto.getSfundtypecode()+maindto.getSagentbnkcode(),subdto);
						allamt=allamt.add(subdto.getFamt());
					}
				}else if(dto.getMainDto() instanceof TvPayreckBankBackDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					TvPayreckBankBackDto maindto = (TvPayreckBankBackDto)dto.getMainDto();
					TvPayreckBankBackListDto subdto = null;
					for(int i=0;i<dto.getSubDtoList().size();i++)
					{
						count++;
						subdto = (TvPayreckBankBackListDto)dto.getSubDtoList().get(i);
						getMap.put(maindto.getSvouno()+subdto.getSid(), subdto);
						getMap.put(maindto.getSvouno()+subdto.getSbdgorgcode()+maindto.getSfundtypecode()+maindto.getSagentbnkcode(),subdto);
						allamt=allamt.subtract(subdto.getFamt().abs());
					}
				}
				else if(dto.getMainDto() instanceof TfDirectpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto)dto.getMainDto();
					TfDirectpaymsgsubDto subdto = null;
					for(int i=0;i<dto.getSubDtoList().size();i++)
					{
						count++;
						subdto = (TfDirectpaymsgsubDto)dto.getSubDtoList().get(i);
						getMap.put(maindto.getSvoucherno()+subdto.getSid(), subdto);
						getMap.put(maindto.getSvoucherno()+subdto.getSsupdepcode()+maindto.getSfundtypecode()+maindto.getSpaybankcode(),subdto);
						allamt=allamt.add(subdto.getNpayamt());
					}
				}
				else if(dto.getMainDto() instanceof TfPaybankRefundmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					
					if(dto.getExtdto().getMainDto()!=null&&dto.getExtdto().getSubDtoList()!=null&&dto.getExtdto().getSubDtoList().size()>0)
					{
						TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto)dto.getExtdto().getMainDto();
						TfDirectpaymsgsubDto subdto = null;
						for(int i=0;i<dto.getSubDtoList().size();i++)
						{
							count++;
							subdto = (TfDirectpaymsgsubDto)dto.getSubDtoList().get(i);
							getMap.put(maindto.getSvoucherno()+subdto.getSid(), subdto);
							getMap.put(maindto.getSvoucherno()+subdto.getSsupdepcode()+maindto.getSfundtypecode()+maindto.getSpaybankcode(),subdto);
							allamt=allamt.subtract(subdto.getNpayamt());
						}
					}else
					{
						TfPaybankRefundmainDto maindto = (TfPaybankRefundmainDto)dto.getMainDto();
						TfPaybankRefundsubDto subdto = null;
						for(int i=0;i<dto.getSubDtoList().size();i++)
						{
							count++;
							subdto = (TfPaybankRefundsubDto)dto.getSubDtoList().get(i);
							getMap.put(maindto.getSvoucherno()+subdto.getSid(), subdto);
							getMap.put(maindto.getSvoucherno()+maindto.getSpaysndbnkno(),subdto);
							allamt=allamt.subtract(subdto.getNpayamt().abs());
						}
					}
				}
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", count);
		}
		return getMap;
	}
	private MainAndSubDto get3507Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_RECONCILE_PAYINFO_MAIN where I_VOUSRLNO=?";
				execDetail.addParam(vDto.getSdealno());
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReconcilePayinfoMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_RECONCILE_PAYINFO_SUB where I_VOUSRLNO=?";
					execDetail.addParam(vDto.getSdealno());
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReconcilePayinfoSubDto.class).getDtoCollection());
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
	private Object getObject(Map map,TfReconcilePayinfoSubDto sub3507dto)
	{
		Object getObject = null;
		if(map!=null&&sub3507dto!=null)
		{
			getObject = map.get(sub3507dto.getSpayagentbillno()+sub3507dto.getSpaydetailid());
			if(getObject==null)
			{
				getObject = map.get(sub3507dto.getSpayagentbillno()+sub3507dto.getSsupdepcode()+sub3507dto.getSfundtypecode()+sub3507dto.getSpaybankno());
				if(getObject==null)
					getObject = map.get(sub3507dto.getSpayagentbillno()+sub3507dto.getSpaybankno());
			}
		}
		return getObject;
	}
}
