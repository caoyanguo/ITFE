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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaSubDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �����ȶ��ˣ�3510��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3580 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3580.class);
	private Map<String,String> bankmap = null;
	private HashMap<String, TsBudgetsubjectDto> subjectMap = null;
	/**
	 * ƾ֤����
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	*/
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		if(vDto==null)
			return null;						
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
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
			if(bankmap==null)
			{
				TsConvertbanktypeDto finddto = new TsConvertbanktypeDto();
				finddto.setSorgcode(vDto.getSorgcode());
				List<TsConvertbanktypeDto> resultList = CommonFacade.getODB().findRsByDto(finddto);
				if(resultList!=null&&resultList.size()>0)
				{
					bankmap = new HashMap<String,String>();
					for(TsConvertbanktypeDto temp:resultList)
					{
						bankmap.put(temp.getSbankcode()+temp.getStrecode(), temp.getSbankname());
					}
				}
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			List<IDto> alldata = getDataList(vDto,execDetail);
			if(alldata!=null&&alldata.size()>0){
				subjectMap = SrvCacheFacade.cacheTsBdgsbtInfo(vDto.getSorgcode());
				//��ȡȫ������¼��Ŀ�Ŀ����
				TsBudgetsubjectDto tdto = new TsBudgetsubjectDto();
				tdto.setSorgcode(vDto.getSorgcode());
				tdto.setSwriteflag(StateConstant.COMMON_NO);
				CommonQto qto = SqlUtil.IDto2CommonQto(tdto);
				List <TsBudgetsubjectDto> tlist =  DatabaseFacade.getODB().findWithUR(tdto.getClass(), qto);
				for (TsBudgetsubjectDto t :tlist) {
					subjectMap.put(t.getSsubjectcode(), t);
				}
				if(vDto.getSorgcode().startsWith("13")){//��������
					Map<String, List>  map = getFundTypeMap(alldata);
					for(java.util.Map.Entry<String, List> entry : map.entrySet()){
						createVoucher(vDto, returnList, cDto, entry.getValue());
					}
				}else {
					if(ITFECommonConstant.PUBLICPARAM.contains(",3510pack=paybank,"))
					{
						Map<String, List>  map = getPaybankAmtTypeMap(alldata);
						for(java.util.Map.Entry<String, List> entry : map.entrySet())
							createVoucher(vDto, returnList, cDto, entry.getValue());
					}else
					{
						createVoucher(vDto, returnList, cDto, alldata);
					}
				}
			}
		}catch (JAFDatabaseException e2) {		
			logger.error(e2.getMessage(),e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}catch (ValidateException e2) {
			logger.error(e2.getMessage(),e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}catch (Exception e2) {
			logger.error(e2.getMessage(),e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return returnList;
	}
	private void createVoucher(TvVoucherinfoDto vDto, List returnList,
			TsConvertfinorgDto cDto, List<IDto> alldata)
			throws ITFEBizException {
		List<Map<String,String>> dataList = getSplitPack(alldata);
		if(dataList!=null&&dataList.size()>0)
		{
			List<List> sendList = null;
			List mapList=null;
			sendList = this.getSubLists(dataList, 500);
			if(sendList!=null&&sendList.size()>0)
			{
				BigDecimal preDateMoney=new BigDecimal("0.00");
				BigDecimal clearAmt=new BigDecimal("0.00");
				BigDecimal curReckMoney=new BigDecimal("0.00");
				BigDecimal curDateMoney=new BigDecimal("0.00");
				String temp=null;
				for(Map<String,String> dataMap:dataList)
				{
					temp = dataMap.get("PreDateMoney");//���ڶ�����
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						preDateMoney = preDateMoney.add(new BigDecimal(temp));
					temp = dataMap.get("ClearAmt");//��������������
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						clearAmt = clearAmt.add(new BigDecimal(temp));
					temp = dataMap.get("CurReckMoney");//������������
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						curReckMoney = curReckMoney.add(new BigDecimal(temp));
					temp = dataMap.get("CurDateMoney");//���ڶ�����
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						curDateMoney = curDateMoney.add(new BigDecimal(temp));
					
				}
				List<Map<String,String>> tempList = null;
				String danhao=null;
				for(int i=0;i<sendList.size();i++)
				{
					mapList=new ArrayList();
					tempList = sendList.get(i);
					String FileName=null;
					String dirsep = File.separator; 
					String mainvou = "";
		            if (ITFECommonConstant.SRC_NODE.equals("201053200014"))
		              mainvou = VoucherUtil.getCheckNo(vDto,new ArrayList<IDto>(),i);
		            else {
		              mainvou = VoucherUtil.getGrantSequence();
		            }
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
					dto.setSstyear(dto.getScreatdate().substring(0, 4));				
					dto.setScheckdate(vDto.getScheckdate());
					dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
					dto.setShold3(vDto.getShold3());
					dto.setShold4(vDto.getShold4());
					dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					dto.setSdemo("����ɹ�");
					dto.setSvoucherflag("1");
					dto.setSvoucherno(mainvou);
					dto.setSattach(danhao);//���˵���
					dto.setShold1(String.valueOf(sendList.size()));//������
					dto.setShold2(String.valueOf(i+1));//�����
					dto.setSext2(getString(vDto.getSext2()));
					dto.setSext3(getString(vDto.getSext3()));
					dto.setSext4(getString(vDto.getSext4()));
					dto.setSext5(getString(vDto.getSext5()));
					dto.setSext4(vDto.getSext4());//���Ÿ��Ի���ʾ��tcbs������������
					dto.setIcount(tempList.size());
					dto.setSext1(getString(vDto.getSext1()));//����ʽ1:���з���,2:��������,3:���з���
					mapList.add(vDto);
					mapList.add(cDto);
					mapList.add(tempList);
					List<IDto> idtoList = new ArrayList<IDto>();
					Map map=tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
					dto.setNmoney(MtoCodeTrans.transformBigDecimal(((Map)map.get("Voucher")).get("AllAmt")));
					dto.setIcount(Integer.valueOf(String.valueOf(((Map)map.get("Voucher")).get("AllNum"))));
					((Map)map.get("Voucher")).put("PreDateMoney", preDateMoney);
					((Map)map.get("Voucher")).put("ClearAmt", clearAmt);
					((Map)map.get("Voucher")).put("CurReckMoney", curReckMoney);
					((Map)map.get("Voucher")).put("CurDateMoney", curDateMoney);
					if(dto.getSpaybankcode()==null||"".equals(dto.getSpaybankcode()))
						dto.setSpaybankcode(tempList.get(0).get("PayBankCode"));
//					if(dto.getSext1()==null||"".equals(dto.getSext1()))
//						dto.setSext1("11".equals(tempList.get(0).get("PayTypeCode"))?"0":"1");
					List vouList=new ArrayList();
					vouList.add(map);
					vouList.add(dto);
					vouList.add(idtoList);
					returnList.add(vouList);
				}
			}
		}
	}
	private Map tranfor(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
//			TsConvertfinorgDto cDto = (TsConvertfinorgDto)mapList.get(1);
			List<Map<String,String>> detailList=(List<Map<String,String>>) mapList.get(2);
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
			vouchermap.put("TreCode",vDto.getStrecode());//�����������
			vouchermap.put("ClearAccNo","");//�����˺�
			vouchermap.put("ClearAccNanme","");//�����˻�����
			TsTreasuryDto dto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			if(vDto.getSorgcode().startsWith("13")){//��������
				vouchermap.put("ClearBankCode","001001");//Ԥ���ֶ�1
				vouchermap.put("ClearBankName",dto.getStrename());//Ԥ���ֶ�2
			}else
			{
				vouchermap.put("ClearBankCode",dto.getStrecode());//�������б���
				vouchermap.put("ClearBankName",dto.getStrename());//������������
			}
			vouchermap.put("BeginDate",vDto.getShold3());//������ʼ����
			vouchermap.put("EndDate",vDto.getShold4());//������ֹ����
			if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.grantPay.equals(vDto.getSext1())||"12".equals(vDto.getSext1())))//����֧����ʽΪ001002��001001
			{
				vouchermap.put("PayTypeCode",getString("001002"));//֧����ʽ����;
				vouchermap.put("PayTypeName","��Ȩ֧��");//֧����ʽ����
			}
			else if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.directPay.equals(vDto.getSext1())||"11".equals(vDto.getSext1())))//����֧����ʽΪ001002��001001
			{
				vouchermap.put("PayTypeCode",getString("001001"));//֧����ʽ����;
				vouchermap.put("PayTypeName","ֱ��֧��");//֧����ʽ����
			}else
			{
				vouchermap.put("PayTypeCode",getString(vDto.getSext1()));//֧����ʽ����
				if(MsgConstant.grantPay.equals(vDto.getSext1())||"12".equals(vDto.getSext1()))
					vouchermap.put("PayTypeName","��Ȩ֧��");//֧����ʽ����
				else if(MsgConstant.directPay.equals(vDto.getSext1())||"12".equals(vDto.getSext1()))
					vouchermap.put("PayTypeName","ֱ��֧��");//֧����ʽ����
				else
					vouchermap.put("PayTypeName","ȫ��");//֧����ʽ����
			}
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
			{
				vouchermap.put("BgtTypeCode",vDto.getSext3());//Ԥ������
				vouchermap.put("BgtTypeName","1".equals(vDto.getSext3())?"Ԥ����":"Ԥ����");//Ԥ����������
			}
			if(vDto.getStrecode().startsWith("13")){//����Ԥ���ֶ����Ԥ������
				vouchermap.put("Hold1",detailList.get(0).get("FundTypeCode"));//Ԥ���ֶ�1
				vouchermap.put("Hold2",detailList.get(0).get("FundTypeName"));//Ԥ���ֶ�2
			}else{
				vouchermap.put("Hold1",(detailList!=null&&detailList.size()>0)?detailList.get(0).get("PayBankCode"):"");//Ԥ���ֶ�1
				vouchermap.put("Hold2",getString(vDto.getSverifyusercode()));//Ԥ���ֶ�2
			}
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for(Map<String,String> dataMap:detailList)
			{
				id++;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",dataMap.get("Id"));//���
				Detailmap.put("SupDepCode",dataMap.get("SupDepCode"));//һ��Ԥ�㵥λ����
				Detailmap.put("SupDepName",dataMap.get("SupDepName"));//һ��Ԥ�㵥λ����
				Detailmap.put("FundTypeCode",dataMap.get("FundTypeCode"));//�ʽ����ʱ���
				Detailmap.put("FundTypeName",dataMap.get("FundTypeName"));//�ʽ���������
				Detailmap.put("PayBankCode",dataMap.get("PayBankCode"));//�������б���
				Detailmap.put("PayBankName",dataMap.get("PayBankName"));//������������
				Detailmap.put("PayBankNo",dataMap.get("PayBankNo"));//���������к�
				Detailmap.put("ExpFuncCode",dataMap.get("ExpFuncCode"));//֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName",dataMap.get("ExpFuncName"));//֧�����ܷ����Ŀ����
				Detailmap.put("ProCatCode",dataMap.get("ProCatCode"));//��֧�������
				Detailmap.put("ProCatName",dataMap.get("ProCatName"));//��֧��������
//				Detailmap.put("PayTypeCode",dataMap.get("PayTypeCode"));//֧����ʽ����
				if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.grantPay.equals(dataMap.get("PayTypeCode"))||"12".equals(dataMap.get("PayTypeCode"))))//����֧����ʽΪ001002��001001
				{
					Detailmap.put("PayTypeCode",getString("001002"));//֧����ʽ����;
					Detailmap.put("PayTypeName","��Ȩ֧��");//֧����ʽ����
				}
				else if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.directPay.equals(dataMap.get("PayTypeCode"))||"11".equals(dataMap.get("PayTypeCode"))))//����֧����ʽΪ001002��001001
				{
					Detailmap.put("PayTypeCode",getString("001001"));//֧����ʽ����;
					Detailmap.put("PayTypeName","ֱ��֧��");//֧����ʽ����
				}
				else
				{
					Detailmap.put("PayTypeCode",getString(dataMap.get("PayTypeCode")));//֧����ʽ����
					if(MsgConstant.grantPay.equals(dataMap.get("PayTypeCode"))||"12".equals(dataMap.get("PayTypeCode")))
						Detailmap.put("PayTypeName","��Ȩ֧��");//֧����ʽ����
					else if(MsgConstant.directPay.equals(dataMap.get("PayTypeCode"))||"12".equals(dataMap.get("PayTypeCode")))
						Detailmap.put("PayTypeName","ֱ��֧��");//֧����ʽ����
					else
						Detailmap.put("PayTypeName","ȫ��");//֧����ʽ����
				}
				Detailmap.put("PreDateMoney",(dataMap.get("PreDateMoney")==null||"".equals(String.valueOf(dataMap.get("PreDateMoney"))))?"0":dataMap.get("PreDateMoney"));//���ڶ�����
				Detailmap.put("ClearAmt",(dataMap.get("ClearAmt")==null||"".equals(String.valueOf(dataMap.get("ClearAmt"))))?"0":dataMap.get("ClearAmt"));//��������������
				Detailmap.put("CurReckMoney",(dataMap.get("CurReckMoney")==null||"".equals(String.valueOf(dataMap.get("CurReckMoney"))))?"0":dataMap.get("CurReckMoney"));//������������
				Detailmap.put("CurDateMoney",(dataMap.get("CurDateMoney")==null||"".equals(String.valueOf(dataMap.get("CurDateMoney"))))?"0":dataMap.get("CurDateMoney"));//���ڶ�����
				Detailmap.put("Hold1",dataMap.get("Hold1"));//Ԥ���ֶ�1
				Detailmap.put("Hold2",dataMap.get("Hold2"));//Ԥ���ֶ�2
				Detailmap.put("Hold3",dataMap.get("Hold3"));//Ԥ���ֶ�3
				Detailmap.put("Hold4",dataMap.get("Hold4"));//Ԥ���ֶ�4
				if(dataMap.get("ClearAmt")!=null&&!"null".equals(dataMap.get("ClearAmt")))
					allamt=allamt.add(new BigDecimal(dataMap.get("ClearAmt")));
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}
			vouchermap.put("AllNum",id);//�ܱ���
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));//�ܽ��
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	
	private TfReconcilePayquotaMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReconcilePayquotaMainDto mainDto = new TfReconcilePayquotaMainDto();
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
		mainDto.setStrecode(getString(mainMap,"TreCode"));//TreCode",vDto.getStrecode());//�����������
		mainDto.setSclearbankcode(getString(mainMap,"ClearBankCode"));//�������б���
		mainDto.setSclearbankname(getString(mainMap,"ClearBankName"));//������������
		mainDto.setSclearaccno(getString(mainMap,"ClearAccNo"));//�����˺�
		mainDto.setSclearaccnanme(getString(mainMap,"ClearAccNanme"));//�����˻�����
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//������ʼ����
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//������ֹ����
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//�ܱ���
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//�ܽ��
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
		mainDto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
		return mainDto;
	}
	private TfReconcilePayquotaSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReconcilePayquotaSubDto subDto = new TfReconcilePayquotaSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		try {
			if(!id.matches("[0-9]+")){
				subDto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
			}else{
				subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//���
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSsupdepcode(getString(subMap,"SupDepCode"));//һ��Ԥ�㵥λ����
		subDto.setSsupdepname(getString(subMap,"SupDepName"));//һ��Ԥ�㵥λ����
		subDto.setSfundtypecode(getString(subMap,"FundTypeCode"));//�ʽ����ʱ���
		subDto.setSfundtypename(getString(subMap,"FundTypeName"));//�ʽ���������
		subDto.setSpaybankcode(getString(subMap,"PayBankCode"));//�������б���
		subDto.setSpaybankname(getString(subMap,"PayBankName"));//������������
		subDto.setSpaybankno(getString(subMap,"PayBankNo"));//���������к�
		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//֧�����ܷ����Ŀ����
		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//֧�����ܷ����Ŀ����
		subDto.setSprocatcode(getString(subMap,"ProCatCode"));//��֧�������
		subDto.setSprocatname(getString(subMap,"ProCatName"));//��֧��������
		subDto.setSpaytypecode(getString(subMap,"PayTypeCode"));//֧����ʽ����
		subDto.setSpaytypename(getString(subMap,"PayTypeName"));//֧����ʽ����
		subDto.setNpredatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"PreDateMoney"))?"0":getString(subMap,"PreDateMoney")));//���ڶ�����
		subDto.setNclearamt(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"ClearAmt"))?"0":getString(subMap,"ClearAmt")));//��������������
		subDto.setNcurreckmoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurReckMoney"))?"0":getString(subMap,"CurReckMoney")));//������������
		subDto.setNcurdatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurDateMoney"))?"0":getString(subMap,"CurDateMoney")));//���ڶ�����
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
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<IDto> getDataList(TvVoucherinfoDto vDto,SQLExecutor execDetail) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			if("-1".equals(vDto.getSext1()))
				vDto.setSext1(null);
			if ("TCBS".equals(vDto.getSext4()))
			{
				sql = new StringBuffer("SELECT * FROM TN_CONPAYCHECKBILL WHERE S_BOOKORGCODE=? ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_BNKNO=? ");
				sql.append(vDto.getSext1()==null?"":" AND C_AMTKIND=? ");
				sql.append(vDto.getSext3()==null?"":" AND S_ECOSBTCODE=? ");
				sql.append(vDto.getStrecode()==null?"":" and S_TRECODE=? ");
				sql.append("AND D_STARTDATE BETWEEN ? AND ? ");
				sql.append(" order by S_BDGORGCODE,S_FUNCSBTCODE ");
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
				{
					if("0".equals(vDto.getSext1()))
						execDetail.addParam("1");
					else
						execDetail.addParam("2");
				}
				if(vDto.getSext3()!=null)
					execDetail.addParam(vDto.getSext3());
				if(vDto.getStrecode()!=null)
					execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(CommonUtil.strToDate(vDto.getShold3()));
				execDetail.addParam(CommonUtil.strToDate(vDto.getShold4()));
				execDetail.setMaxRows(500000);
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TnConpaycheckbillDto.class).getDtoCollection();//��ѯ����
				if(execDetail!=null)
					execDetail.closeConnection();
				MainAndSubDto datadto = new MainAndSubDto();
				datadto.setMainDtoList(detailList);
//				subMap = new HashMap<String,TsBudgetsubjectDto>();
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.add(datadto);
				return getList;
			}
			if(vDto.getSext1()==null||MsgConstant.grantPay.equals(vDto.getSext1()))
			{
				sql = new StringBuffer("SELECT * FROM HTV_GRANTPAYMSGMAIN WHERE I_VOUSRLNO in(");//��ѯ��Ȩ֧���Ѿ��ص�������
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
				sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
				sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					sql.append(" AND S_BUDGETTYPE=? ");
				execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvGrantpaymsgmainDto.class).getDtoCollection();//��ʷ������
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from HTV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//��ѯ��Ȩ֧���ӱ�����
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
					subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TvGrantpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TvGrantpaymsgsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
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
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())+String.valueOf(tempdto.getSpackageticketno())));//��ʷ���ӱ�
						getList.add(datadto);
					}
				}
				execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"),TvGrantpaymsgmainDto.class).getDtoCollection();//��ʽ������
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from TV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"), "*", "I_VOUSRLNO")+")";//��ѯ
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
					subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TvGrantpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TvGrantpaymsgsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
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
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())+String.valueOf(tempdto.getSpackageticketno())));//��ʽ���ӱ�
						getList.add(datadto);
					}
				}
			}
			if(vDto.getSext1()==null||MsgConstant.directPay.equals(vDto.getSext1()))
			{
				//5108��ѯֱ��֧���Ѿ��ص�������
				sql = new StringBuffer("SELECT * FROM HTV_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//��ѯֱ��֧���Ѿ��ص�������
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
				sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
				sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					sql.append(" AND S_BUDGETTYPE=? ");
				execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvDirectpaymsgmainDto.class).getDtoCollection();//��ʷ������
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from HTV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
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
				execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvDirectpaymsgmainDto.class).getDtoCollection();//��ʽ������
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from TV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"),"HTV_VOUCHERINFO","TV_VOUCHERINFO"), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
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
			}
		} catch (JAFDatabaseException e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e);
			throw new ITFEBizException("��ѯ"+sql==null?"":sql.toString()+"�����ϸ��Ϣ�쳣��",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return getList;
	}
	private List getSplitPack(List<IDto> dataList)
	{
		List<Map<String,String>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,Map> tempMap = new HashMap<String,Map>();
			TvDirectpaymsgmainDto ddto = null;
			TvGrantpaymsgmainDto gdto = null;
			MainAndSubDto dto = null;
			Map<String,String> dataMap = null;
			//Ԥ�㵥λ����+�ʽ����ʱ���+���������к�+֧�����ܷ����Ŀ����+֧����ʽ����(11ֱ��֧��12��Ȩ֧��)
			//sub-S_AGENCYCODE+main-S_FUNDTYPECODE+main-S_PAYBANKNO+sub-S_FUNSUBJECTCODE+(11ֱ��֧��12��Ȩ֧��)
			//main-S_BUDGETUNITCODE+main-S_FUNDTYPECODE+main-S_PAYBANKNO+sub-S_FUNSUBJECTCODE+(11ֱ��֧��12��Ȩ֧��)
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(dto.getMainDto()!=null&&dto.getMainDto() instanceof TvDirectpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					
					ddto = (TvDirectpaymsgmainDto)dto.getMainDto();
					TvDirectpaymsgsubDto subdto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvDirectpaymsgsubDto)dto.getSubDtoList().get(sub);
						//Ԥ�㵥λ����+�ʽ����ʱ���+���������к�+֧�����ܷ����Ŀ����+֧����ʽ����(11ֱ��֧��12��Ȩ֧��)
//						if(tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11")==null)
//						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(ddto.getSid())+(subdto.getSid()==null||"null".equals(subdto.getSid())?subdto.getStaxticketno():subdto.getSid()));//���
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
							if (ddto.getStrecode().startsWith("33")) {
					              dataMap.put("Id", subdto.getSid());//���
					              tempMap.put(subdto.getSid(), dataMap);
					        } else {
					              dataMap.put("Id", String.valueOf(ddto.getSid()) + subdto.getSid());//���
					              tempMap.put(String.valueOf(ddto.getSid()) + subdto.getSid() + "11", dataMap);
					        }
//						}else
//						{
//							dataMap = tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11");
//							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
//						}
					}
					
				}else if(dto.getMainDto()!=null&&dto.getMainDto() instanceof TvGrantpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					gdto = (TvGrantpaymsgmainDto)dto.getMainDto();
					TvGrantpaymsgsubDto subdto = null;
					TsBudgetsubjectDto budto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvGrantpaymsgsubDto)dto.getSubDtoList().get(sub);
						//Ԥ�㵥λ����+�ʽ����ʱ���+���������к�+֧�����ܷ����Ŀ����+֧����ʽ����(11ֱ��֧��12��Ȩ֧��)
//						if(tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12")==null)
//						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(gdto.getSid())+(subdto.getSid()==null||"null".equals(subdto.getSid())?subdto.getSdealno():subdto.getSid()));//���
							dataMap.put("SupDepCode",getString(subdto.getSbudgetunitcode()));//һ��Ԥ�㵥λ����
							dataMap.put("SupDepName",getString(subdto.getSsupdepname()));//һ��Ԥ�㵥λ����
							dataMap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//�ʽ����ʱ���
							dataMap.put("FundTypeName",getString(gdto.getSfundtypename()));//�ʽ���������
							dataMap.put("PayBankCode",getString(gdto.getSpaybankcode()));//�������б���
							dataMap.put("PayBankName",getString(gdto.getSpaybankname()));//������������
							dataMap.put("PayBankNo",getString(gdto.getSpaybankno()));//���������к�
							dataMap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//֧�����ܷ����Ŀ����
							budto =  subjectMap==null?null:subjectMap.get(subdto.getSfunsubjectcode());
							dataMap.put("ExpFuncName",budto==null?"δ֪":budto.getSsubjectname());//֧�����ܷ����Ŀ����
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
							if (ITFECommonConstant.SRC_NODE.equals("201053200014")) {
								dataMap.put("Id",String.valueOf(subdto.getSid()));//���
					            tempMap.put(subdto.getSid(), dataMap);
					        } else {
					        	dataMap.put("Id",String.valueOf(gdto.getSid())+subdto.getSid());//���
					            tempMap.put(String.valueOf(gdto.getSid()) + subdto.getSid()+"12",dataMap);
					        }
//						}else
//						{
//							dataMap = tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12");
//							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
//						}
					}
				}else if(dto.getMainDtoList()!=null&&dto.getMainDtoList().size()>0)
				{
					List<IDto> templist = dto.getMainDtoList();
					TnConpaycheckbillDto tempdto = null;
					TsBudgetsubjectDto budto = null;
					for(IDto bdto:templist)
					{
						tempdto = (TnConpaycheckbillDto)bdto;
						dataMap = new HashMap<String,String>();
						dataMap.put("Id",TimeFacade.getCurrentStringTime()+String.valueOf(tempdto.getIenrolsrlno()));//���
						dataMap.put("SupDepCode",getString(tempdto.getSbdgorgcode()));//һ��Ԥ�㵥λ����
						dataMap.put("SupDepName",getString(tempdto.getSbdgorgname()));//һ��Ԥ�㵥λ����
						dataMap.put("FundTypeCode",getString(tempdto.getSecosbtcode()));//�ʽ����ʱ���
						dataMap.put("FundTypeName",getString("1".equals(tempdto.getSecosbtcode())?"Ԥ����":"Ԥ����"));//�ʽ���������
						dataMap.put("PayBankCode",getString(tempdto.getSbnkno()));//�������б���
						dataMap.put("PayBankName",getString(getBankmap()==null?"":getBankmap().get(tempdto.getSbnkno()+tempdto.getStrecode())));//������������
						dataMap.put("PayBankNo",getString(tempdto.getSbnkno()));//���������к�
						dataMap.put("ExpFuncCode",getString(tempdto.getSfuncsbtcode()));//֧�����ܷ����Ŀ����
						budto =  subjectMap==null?null:subjectMap.get(tempdto.getSfuncsbtcode());
						dataMap.put("ExpFuncName",budto==null?"δ֪":budto.getSsubjectname());//֧�����ܷ����Ŀ����
						dataMap.put("ProCatCode","99999");//��֧�������
						dataMap.put("ProCatName","99999");//��֧��������
						dataMap.put("PayTypeCode","1".equals(tempdto.getCamtkind())?"11":"12");//֧����ʽ����
						dataMap.put("PayTypeName","1".equals(tempdto.getCamtkind())?"ֱ��֧��":"��Ȩ֧��");//֧����ʽ����
						dataMap.put("PreDateMoney",String.valueOf(tempdto.getFlastmonthzeroamt()));//���ڶ�����
						dataMap.put("ClearAmt",String.valueOf(tempdto.getFcursmallamt()));//��������������
						dataMap.put("CurReckMoney",String.valueOf(tempdto.getFcurreckzeroamt()));//������������
						dataMap.put("CurDateMoney",String.valueOf(tempdto.getFcurzeroamt()));//���ڶ�����
						dataMap.put("Hold1","");//Ԥ���ֶ�1
						dataMap.put("Hold2","");//Ԥ���ֶ�2
						dataMap.put("Hold3","");//Ԥ���ֶ�3
						dataMap.put("Hold4","");//Ԥ���ֶ�4
						tempMap.put(String.valueOf(tempdto.getIenrolsrlno())+tempdto.getSbnkno(),dataMap);
					}
				}
			}
			if(tempMap!=null&&tempMap.size()>0)
			{
				getList = new ArrayList<Map<String,String>>();
				for(String mapkey:tempMap.keySet())
					getList.add(tempMap.get(mapkey));
			}
		}
		return getList;
	}
	private String getString(String key)
	{
		if(key==null)
			key="";
		return key;
	}
	public Map<String, String> getBankmap() {
		return bankmap;
	}
	public void setBankmap(Map<String, String> bankmap) {
		this.bankmap = bankmap;
	}
	public HashMap<String, TsBudgetsubjectDto> getSubjectMap() {
		return subjectMap;
	}
	public void setSubjectMap(HashMap<String, TsBudgetsubjectDto> subMap) {
		this.subjectMap = subMap;
	}
	
	/**
	 * ����ѯ������ʽ����ͷְ�
	 * @param alldata
	 * @return
	 */
	private Map<String, List>  getFundTypeMap(List<IDto> alldata){
		Map<String, List> map = new HashMap<String, List>();
		for(IDto msdto : alldata){
			MainAndSubDto dto = (MainAndSubDto) msdto;
			if(dto.getMainDto() instanceof TvDirectpaymsgmainDto){
				if(map.get(((TvDirectpaymsgmainDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvDirectpaymsgmainDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvDirectpaymsgmainDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}else if(dto.getMainDto() instanceof TvGrantpaymsgmainDto){
				if(map.get(((TvGrantpaymsgmainDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvGrantpaymsgmainDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvGrantpaymsgmainDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}else if(dto.getMainDtoList()!=null&&dto.getMainDtoList().size()>0)
			{
				TnConpaycheckbillDto tncondto = null;
				MainAndSubDto tnmain = new MainAndSubDto();
				List<IDto> tnmainlist = new ArrayList<IDto>();
				List<IDto> data = new ArrayList<IDto>();
				for(int i=0;i<dto.getMainDtoList().size();i++)
				{
					tncondto = (TnConpaycheckbillDto)dto.getMainDtoList().get(i);
					tnmainlist.add(tncondto);
				}
				tnmain.setMainDtoList(tnmainlist);
				data.add(tnmain);
				map.put("data", data);
			}
			
		}
		return map;
	}
	/**
	 * ����ѯ������������ж������ְ�
	 * @param alldata
	 * @return
	 */
	private Map<String, List>  getPaybankAmtTypeMap(List<IDto> alldata){
		Map<String, List> map = new HashMap<String, List>();
		TvDirectpaymsgmainDto ddto = null;
		TvGrantpaymsgmainDto gdto = null;
		TnConpaycheckbillDto cdto = null;
		for(IDto msdto : alldata){
			MainAndSubDto dto = (MainAndSubDto) msdto;
			if(dto.getMainDto() instanceof TvDirectpaymsgmainDto){
				ddto = (TvDirectpaymsgmainDto)dto.getMainDto();
				if(map.get(ddto.getSpaybankcode()+ddto.getSamttype())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(ddto.getSpaybankcode()+ddto.getSamttype(), data);
				}else{
					List<IDto> data = map.get(ddto.getSpaybankcode()+ddto.getSamttype());
					data.add(dto);
				}
			}else if(dto.getMainDto() instanceof TvGrantpaymsgmainDto){
				gdto = (TvGrantpaymsgmainDto)dto.getMainDto();
				if(map.get(gdto.getSpaybankcode()+gdto.getSamttype())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(gdto.getSpaybankcode()+gdto.getSamttype(), data);
				}else{
					List<IDto> data = map.get(gdto.getSpaybankcode()+gdto.getSamttype());
					data.add(dto);
				}
			}else if(dto.getMainDtoList()!=null&&dto.getMainDtoList().size()>0)
			{
				MainAndSubDto tnmain = null;
				List<IDto> data = null;
				List<IDto> mdata = null;
				for(int i=0;i<dto.getMainDtoList().size();i++)
				{
					cdto = (TnConpaycheckbillDto)dto.getMainDtoList().get(i);
					if(map.get(cdto.getSbnkno()+cdto.getCamtkind())==null)
					{
						tnmain = new MainAndSubDto();
						data = new ArrayList<IDto>();
						data.add(cdto);
						tnmain.setMainDtoList(data);
						mdata = new ArrayList<IDto>();
						mdata.add(tnmain);
						map.put(cdto.getSbnkno()+cdto.getCamtkind(),mdata);
					}else
					{
						mdata = map.get(cdto.getSbnkno()+cdto.getCamtkind());
						tnmain = (MainAndSubDto)mdata.get(0);
						tnmain.getMainDtoList().add(cdto);
					}
				}
			}
			
		}
		return map;
	}
}
