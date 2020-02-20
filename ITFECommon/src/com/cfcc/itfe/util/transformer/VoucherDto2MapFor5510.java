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
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaSubDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * ���������ȶ�������3510��ִ5510ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5510 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5510.class);
											
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
			MainAndSubDto datadto =  get3510Data(vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5510);
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
			TfReconcilePayquotaMainDto main3510dto = (TfReconcilePayquotaMainDto)datadto.getMainDto();
			TfReconcilePayquotaSubDto sub3510dto = null;
			map.put("Voucher", vouchermap);// ���ñ��Ľڵ� Voucher
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",main3510dto.getSadmdivcode());//������������
			vouchermap.put("StYear",main3510dto.getSstyear());//ҵ�����
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5510);//ƾ֤���ͱ��
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//ƾ֤����
			vouchermap.put("VoucherNo",main3510dto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",main3510dto.getSvouchercheckno());//���˵���
			vouchermap.put("ChildPackNum",main3510dto.getSchildpacknum());//�Ӱ�����
			vouchermap.put("CurPackNo",main3510dto.getScurpackno());//�������
			vouchermap.put("TreCode",main3510dto.getStrecode());//�����������
			vouchermap.put("ClearBankCode",main3510dto.getSclearbankcode());//�������б���
			vouchermap.put("ClearBankName",main3510dto.getSclearbankname());//������������
			vouchermap.put("ClearAccNo",main3510dto.getSclearaccno());//�����˺�
			vouchermap.put("ClearAccNanme",main3510dto.getSclearaccnanme());//�����˻�����
			vouchermap.put("BeginDate",main3510dto.getSbegindate());//������ʼ����
			vouchermap.put("EndDate",main3510dto.getSenddate());//������ֹ����
			vouchermap.put("AllNum",getMap.get("allnum"));//�ܱ���
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//�ܽ��
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			Map tempMap = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3510dto = (TfReconcilePayquotaSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3510dto.getSid());//���
				Detailmap.put("SupDepCode",sub3510dto.getSsupdepcode());//һ��Ԥ�㵥λ����
				Detailmap.put("SupDepName",sub3510dto.getSsupdepname());//һ��Ԥ�㵥λ����
				Detailmap.put("FundTypeCode",sub3510dto.getSfundtypecode());//�ʽ����ʱ���
				Detailmap.put("FundTypeName",sub3510dto.getSfundtypename());//�ʽ���������
				Detailmap.put("PayBankCode",sub3510dto.getSpaybankcode());//�������б���
				Detailmap.put("PayBankName",sub3510dto.getSpaybankname());//������������
				Detailmap.put("PayBankNo",sub3510dto.getSpaybankno());//���������к�
				Detailmap.put("ExpFuncCode",sub3510dto.getSexpfunccode());//֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName",sub3510dto.getSexpfuncname());//֧�����ܷ����Ŀ����
				Detailmap.put("ProCatCode",sub3510dto.getSprocatcode());//��֧�������
				Detailmap.put("ProCatName",sub3510dto.getSprocatname());//��֧��������
				Detailmap.put("PayTypeCode",sub3510dto.getSpaytypecode());//֧����ʽ����
				Detailmap.put("PayTypeName",sub3510dto.getSpaytypename());//֧����ʽ����
				Detailmap.put("PreDateMoney",sub3510dto.getNpredatemoney());//���ڶ�����
				Detailmap.put("ClearAmt",String.valueOf(sub3510dto.getNclearamt()));//��������������
				Detailmap.put("CurReckMoney",String.valueOf(sub3510dto.getNcurreckmoney()));//������������
				Detailmap.put("CurDateMoney",String.valueOf(sub3510dto.getNclearamt()));//���ڶ�����
				tempdto = getObject(getMap,sub3510dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					Detailmap.put("XCheckReason","���Ҳ���ƾ֤");//����ԭ��
				}else
				{
					tempMap = (Map)tempdto;
					if(tempMap.get("ClearAmt")!=null&&sub3510dto.getNclearamt()!=null&&tempMap.get("ClearAmt").toString().equals(sub3510dto.getNclearamt().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						Detailmap.put("XCheckReason","����,�������:"+tempMap.get("ClearAmt"));//����ԭ��
					}
					
				}
				Detailmap.put("Hold1",sub3510dto);//Ԥ���ֶ�1
				Detailmap.put("Hold2",sub3510dto);//Ԥ���ֶ�2
				Detailmap.put("Hold3",sub3510dto);//Ԥ���ֶ�3
				Detailmap.put("Hold4",sub3510dto);//Ԥ���ֶ�4

				Detail.add(Detailmap);
			}
			if(main3510dto.getSallnum()!=null&&main3510dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3510dto.getNallamt()!=null&&main3510dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
				if(main3510dto.getNallamt()!=null&&main3510dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
		TfReconcilePayinfoMainDto vDto = (TfReconcilePayinfoMainDto)dto3507.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			//2301��ѯ���л����Ѿ��ص�������
			sql = new StringBuffer("SELECT * FROM HTV_GRANTPAYMSGMAIN WHERE I_VOUSRLNO in(");
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5106+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"')");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvGrantpaymsgmainDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//��Ȩ֧���ӱ�����
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvGrantpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvGrantpaymsgsubDto)tempdto;
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
				TvGrantpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvGrantpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʷ���ӱ�
					getList.add(datadto);
				}
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"),TvGrantpaymsgmainDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"),"*", "I_VOUSRLNO")+")";//��Ȩ֧���ӱ�����
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvGrantpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvGrantpaymsgsubDto)tempdto;
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
				TvGrantpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvGrantpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʽ���ӱ�
					getList.add(datadto);
				}
			}
			//��ѯֱ��֧���Ѿ��ص�������
			sql = new StringBuffer("SELECT * FROM HTV_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//��ѯֱ��֧���Ѿ��ص�������
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5108+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"')");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvDirectpaymsgmainDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvDirectpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvDirectpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvDirectpaymsgsubDto)tempdto;
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
				TvDirectpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvDirectpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʷ���ӱ�
					getList.add(datadto);
				}
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"),TvDirectpaymsgmainDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvDirectpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvDirectpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvDirectpaymsgsubDto)tempdto;
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
				TvDirectpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvDirectpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʽ���ӱ�
					getList.add(datadto);
				}
			}
			
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e.getMessage(),e);
			throw new ITFEBizException("��ѯ"+sql==null?"":sql.toString()+"5510�����쳣��",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return getList;
	}
	private Map<String,Object> getSplitPack(List<IDto> dataList)
	{
		Map<String,Object> tempMap = new HashMap<String,Object>();
		BigDecimal allamt=new BigDecimal("0.00");
		if(dataList!=null&&dataList.size()>0)
		{
			MainAndSubDto dto = null;
			TvDirectpaymsgmainDto ddto = null;
			TvGrantpaymsgmainDto gdto = null;
			Map<String,String> dataMap = null;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(dto.getMainDto() instanceof TvDirectpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					
					ddto = (TvDirectpaymsgmainDto)dto.getMainDto();
					TvDirectpaymsgsubDto subdto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvDirectpaymsgsubDto)dto.getSubDtoList().get(sub);
						//Ԥ�㵥λ����+�ʽ����ʱ���+���������к�+֧�����ܷ����Ŀ����+֧����ʽ����(11ֱ��֧��12��Ȩ֧��)
						if(tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11")==null)
						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(ddto.getIvousrlno())+subdto.getIdetailseqno());//���
							dataMap.put("SupDepCode",getString(subdto.getSagencycode()));//һ��Ԥ�㵥λ����
							dataMap.put("SupDepName",getString(subdto.getSagencyname()));//һ��Ԥ�㵥λ����
							dataMap.put("FundTypeCode",getString(ddto.getSfundtypecode()));//�ʽ����ʱ���
							dataMap.put("FundTypeName",getString(ddto.getSfundtypename()));//�ʽ���������
							dataMap.put("PayBankCode",getString(ddto.getSpaybankcode()));//�������б���
							dataMap.put("PayBankName",getString(ddto.getSpaybankname()));//������������
							dataMap.put("PayBankNo",getString(ddto.getSpaybankno()));//���������к�
							dataMap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//֧�����ܷ����Ŀ����
							dataMap.put("ExpFuncName",getString(subdto.getSexpfuncname()));//֧�����ܷ����Ŀ����
							dataMap.put("ProCatCode",getString(subdto.getSprocatcode()));//��֧�������
							dataMap.put("ProCatName",getString(subdto.getSprocatname()));//��֧��������
							dataMap.put("PayTypeCode","11");//֧����ʽ����
							dataMap.put("PayTypeName","ֱ��֧��");//֧����ʽ����
							dataMap.put("PreDateMoney","");//���ڶ�����
							dataMap.put("ClearAmt",String.valueOf(subdto.getNmoney()));//��������������
							dataMap.put("CurReckMoney","");//������������
							dataMap.put("CurDateMoney","");//���ڶ�����
							dataMap.put("Hold1","");//Ԥ���ֶ�1
							dataMap.put("Hold2","");//Ԥ���ֶ�2
							dataMap.put("Hold3","");//Ԥ���ֶ�3
							dataMap.put("Hold4","");//Ԥ���ֶ�4
							tempMap.put(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11",dataMap);
						}else
						{
							dataMap = (Map)tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11");
							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
						}
					}
					
				}else if(dto.getMainDto() instanceof TvGrantpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					gdto = (TvGrantpaymsgmainDto)dto.getMainDto();
					TvGrantpaymsgsubDto subdto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvGrantpaymsgsubDto)dto.getSubDtoList().get(sub);
						//Ԥ�㵥λ����+�ʽ����ʱ���+���������к�+֧�����ܷ����Ŀ����+֧����ʽ����(11ֱ��֧��12��Ȩ֧��)
						if(tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12")==null)
						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(gdto.getIvousrlno())+subdto.getIdetailseqno());//���
							dataMap.put("SupDepCode",getString(subdto.getSbudgetunitcode()));//һ��Ԥ�㵥λ����
							dataMap.put("SupDepName",getString(subdto.getSsupdepname()));//һ��Ԥ�㵥λ����
							dataMap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//�ʽ����ʱ���
							dataMap.put("FundTypeName",getString(gdto.getSfundtypename()));//�ʽ���������
							dataMap.put("PayBankCode",getString(gdto.getSpaybankcode()));//�������б���
							dataMap.put("PayBankName",getString(gdto.getSpaybankname()));//������������
							dataMap.put("PayBankNo",getString(gdto.getSpaybankno()));//���������к�
							dataMap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//֧�����ܷ����Ŀ����
							dataMap.put("ExpFuncName",getString(subdto.getSexpfunccode1()));//֧�����ܷ����Ŀ����
							dataMap.put("ProCatCode",getString(subdto.getSprocatcode()));//��֧�������
							dataMap.put("ProCatName",getString(subdto.getSprocatname()));//��֧��������
							dataMap.put("PayTypeCode","12");//֧����ʽ����
							dataMap.put("PayTypeName","��Ȩ֧��");//֧����ʽ����
							dataMap.put("PreDateMoney","");//���ڶ�����
							dataMap.put("ClearAmt",String.valueOf(subdto.getNmoney()));//��������������
							dataMap.put("CurReckMoney","");//������������
							dataMap.put("CurDateMoney","");//���ڶ�����
							dataMap.put("Hold1","");//Ԥ���ֶ�1
							dataMap.put("Hold2","");//Ԥ���ֶ�2
							dataMap.put("Hold3","");//Ԥ���ֶ�3
							dataMap.put("Hold4","");//Ԥ���ֶ�4
							tempMap.put(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12",dataMap);
						}else
						{
							dataMap = (Map)tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12");
							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
						}
					}
				}
			}
			tempMap.put("allamt",allamt);
			tempMap.put("allcount", tempMap.size());
		}
		return tempMap;
	}
	private MainAndSubDto get3510Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_RECONCILE_PAYQUOTA_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReconcilePayquotaMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * TF_RECONCILE_PAYQUOTA_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReconcilePayquotaSubDto.class).getDtoCollection());
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
	private Object getObject(Map map,TfReconcilePayquotaSubDto sub3510dto)
	{
		Object getObject = null;
		if(map!=null&&sub3510dto!=null)
		{
			//Ԥ�㵥λ����+�ʽ����ʱ���+���������к�+֧�����ܷ����Ŀ����+֧����ʽ����(11ֱ��֧��12��Ȩ֧��)
			getObject = map.get(sub3510dto.getSsupdepcode()+sub3510dto.getSfundtypecode()+sub3510dto.getSpaybankcode()+sub3510dto.getSexpfunccode()+sub3510dto.getSpaytypecode());
		}
		return getObject;
	}
	private String getString(String key)
	{
		if(key==null)
			key="";
		return key;
	}
}
