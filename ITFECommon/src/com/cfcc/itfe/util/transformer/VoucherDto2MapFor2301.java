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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ֱ��֧������ص���2301��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor2301 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2301.class);
	private Map<String,TsInfoconnorgaccDto> accMap = null;
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
		cDto.setStrecode(vDto.getStrecode());
		SQLExecutor execDetail = null;
		try {
			List<TsConvertfinorgDto> tsConvertfinorgList=(List<TsConvertfinorgDto>) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("���⣺"+vDto.getStrecode()+"��Ӧ�Ĳ������ش������δά����");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("���⣺"+cDto.getStrecode()+"��Ӧ����������δά����");
			}
			List<TsInfoconnorgaccDto> accList = null;
			TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
			accdto.setStrecode(vDto.getStrecode());
			accdto.setSorgcode(vDto.getSorgcode());
			accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB()
					.findRsByDto(accdto);
			if (accList != null && accList.size() > 0) {
				accMap = new HashMap<String, TsInfoconnorgaccDto>();
				for (TsInfoconnorgaccDto tempdto : accList) {
					if (tempdto.getSpayeraccount().indexOf(
							vDto.getSorgcode() + "271") >= 0)
						accMap.put("271", tempdto); // �Ϻ��в����ֹ����
					else if (tempdto.getSpayeraccount().indexOf(vDto.getSorgcode() + "371") >= 0 && !(tempdto.getSpayername().indexOf("��Ȩ") >= 0))
						accMap.put("371", tempdto);
				}
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			List<MainAndSubDto> dataList = getDataList(vDto,execDetail);
			if(dataList!=null&&dataList.size()>0)
			{
				List<MainAndSubDto> sendList = null;
				List mapList=null;
				for(int k=0;k<dataList.size();k++)
				{
					sendList = new ArrayList<MainAndSubDto>();
					sendList.add(dataList.get(k));
					mapList=new ArrayList();
					String FileName=null;
					String dirsep = File.separator; 
					String mainvou=VoucherUtil.getGrantSequence();
					vDto.setSdealno(mainvou);
					vDto.setSvoucherno(mainvou);
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
					dto.setShold1(vDto.getShold1());//�������ʱ��4����1��3����
					dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					dto.setSdemo("����ɹ�");	
					dto.setSvoucherflag("1");
					dto.setSvoucherno(mainvou);	
					dto.setIcount(sendList.size());
					mapList.add(vDto);
					mapList.add(sendList);
					List<IDto> idtoList = new ArrayList<IDto>();
					Map map=tranfor(mapList,idtoList);
					dto.setNmoney(MtoCodeTrans.transformBigDecimal(((Map)map.get("Voucher")).get("XPayAmt")));
					dto.setIcount(Integer.valueOf(String.valueOf(((Map)map.get("Voucher")).get("AllNum"))));
					List vouList=new ArrayList();
					vouList.add(map);
					vouList.add(dto);
					vouList.add(idtoList);
					returnList.add(vouList);
				}
			}
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return returnList;
	}
	private Map tranfor(List mapList,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			List<IDto> detailList=(List<IDto>) mapList.get(1);
			MainAndSubDto mdto = (MainAndSubDto)detailList.get(0);
			TfDirectpaymsgmainDto maindto = null;
			TfDirectpaymsgsubDto subdto = null;
			
			
			maindto = (TfDirectpaymsgmainDto)mdto.getMainDto();
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("Id",getString(vDto.getSvoucherno()));//���뻮��ƾ֤Id
			vouchermap.put("AdmDivCode",getString(vDto.getSadmdivcode()));//������������
			vouchermap.put("StYear",getString(vDto.getScreatdate().substring(0,4)));//ҵ�����
			vouchermap.put("VtCode",getString(vDto.getSvtcode()));//ƾ֤���ͱ��
			vouchermap.put("VouDate",getString(vDto.getScreatdate()));//ƾ֤����
			vouchermap.put("VoucherNo",getString(vDto.getSvoucherno()));//ƾ֤��
			vouchermap.put("TreCode",getString(vDto.getStrecode()));//�����������
			vouchermap.put("FinOrgCode",getString(maindto.getSfinorgcode()));//�������ش���
			vouchermap.put("BgtTypeCode",getString(maindto.getSbgttypecode()));//Ԥ�����ͱ���
			vouchermap.put("BgtTypeName",getString(maindto.getSbgttypename()));//Ԥ����������
			vouchermap.put("FundTypeCode",getString(maindto.getSfundtypecode()));//�ʽ����ʱ���
			vouchermap.put("FundTypeName",getString(maindto.getSfundtypename()));//�ʽ���������
			vouchermap.put("PayTypeCode","11");//֧����ʽ����
			vouchermap.put("PayTypeName","ֱ��֧��");//֧����ʽ����
			vouchermap.put("AgentAcctNo",getString(accMap.get("371").getSpayeraccount()));//�տ������˺�
			vouchermap.put("AgentAcctName",getString(accMap.get("371").getSpayername()));//�տ������˻�����
			vouchermap.put("AgentAcctBankName",getString(maindto.getSpayacctbankname()));//�տ�����
			vouchermap.put("ClearAcctNo",getString(accMap.get("271").getSpayeraccount()));//�����˺�271
			vouchermap.put("ClearAcctName",getString(accMap.get("271").getSpayername()));//�����˻�����
			vouchermap.put("ClearAcctBankName",getString(maindto.getSpayacctbankname()));//��������
//			vouchermap.put("PayAmt",getString(""));//����������
			//�����������ȡϵͳ��������    
			if("4".equals(vDto.getShold1())){
				vouchermap.put("PayBankName",getString(maindto.getSpaybankname()));//������������
				vouchermap.put("PayBankNo",getString(maindto.getSpaybankcode()));//���������к�
			}else{
				vouchermap.put("PayBankName",getString(maindto.getSinputrecbankname()));//������������  ϵͳ��¼
				vouchermap.put("PayBankNo",getString(maindto.getSpayeeacctbankno()));//���������к�    ϵͳ��¼
			}
			
			vouchermap.put("Remark",getString(""));//ժҪ
			vouchermap.put("MoneyCorpCode",getString(""));//���ڻ�������
			vouchermap.put("XPaySndBnkNo",getString(""));//֧���������к�
			vouchermap.put("XAddWord",getString(""));//���� 
			vouchermap.put("XClearDate",getString(vDto.getScheckdate()));//��������
			vouchermap.put("XPayAmt",getString(""));//����������
			vouchermap.put("Hold1",getString(""));//Ԥ���ֶ�1
			vouchermap.put("Hold2",getString(""));//Ԥ���ֶ�2
			BigDecimal allamt=new BigDecimal("0.00");
			BigDecimal xallamt=new BigDecimal("0.00");
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			for(IDto idto:detailList)
			{
				id++;
				mdto = (MainAndSubDto)idto;
				maindto = (TfDirectpaymsgmainDto)mdto.getMainDto();
				if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
				{
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id",String.valueOf(maindto.getIvousrlno())+id);//���
					Detailmap.put("VoucherNo",getString(maindto.getSvoucherno()));//֧��ƾ֤����
					Detailmap.put("SupDepCode",getString(((TfDirectpaymsgsubDto)mdto.getSubDtoList().get(0)).getSagencycode()));//һ��Ԥ�㵥λ����	ȡ��ϸ�������
					Detailmap.put("SupDepName",getString(((TfDirectpaymsgsubDto)mdto.getSubDtoList().get(0)).getSagencyname()));//һ��Ԥ�㵥λ����	ȡ��ϸ�������
					Detailmap.put("ExpFuncCode",getString(maindto.getSexpfunccode()));//֧�����ܷ����Ŀ����
					Detailmap.put("ExpFuncName",getString(maindto.getSexpfuncname()));//֧�����ܷ����Ŀ����
					Detailmap.put("PaySummaryName",getString(""));//ժҪ����
					Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
					Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
					Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
					Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
					
					for(int i=0;i<mdto.getSubDtoList().size();i++)
					{
						subdto = (TfDirectpaymsgsubDto)mdto.getSubDtoList().get(i);
						if(subdto.getNxpayamt()!=null)
							xallamt=xallamt.add(subdto.getNxpayamt());
						else
							xallamt=xallamt.add(subdto.getNpayamt());
						allamt=allamt.add(subdto.getNpayamt());
					}
					Detailmap.put("PayAmt",allamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : MtoCodeTrans.transformString(allamt));//֧�����
					Detail.add(Detailmap);
				}
			}
			vouchermap.put("AllNum",id);//�ܱ���
			vouchermap.put("PayAmt",allamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : MtoCodeTrans.transformString(allamt));//�ܽ��
			vouchermap.put("XPayAmt",xallamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : MtoCodeTrans.transformString(xallamt));//�ܽ��
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
//	
//	private TfReconcilePayquotaMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
//	{
//		TfReconcilePayquotaMainDto mainDto = new TfReconcilePayquotaMainDto();
//		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
//		mainDto.setSdemo("����ɹ�");
//		mainDto.setSorgcode(vDto.getSorgcode());
//		mainDto.setIvousrlno(Long.valueOf(getString(mainMap,"VoucherNo")));
//		mainDto.setSadmdivcode(getString(mainMap,"AdmDivCode"));//AdmDivCode",vDto.getSadmdivcode());//������������
//		mainDto.setSstyear(getString(mainMap,"StYear"));//StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
//		mainDto.setSvtcode(getString(mainMap,"VtCode"));//VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
//		mainDto.setSvoudate(getString(mainMap,"VouDate"));//VouDate",vDto.getScreatdate());//ƾ֤����
//		mainDto.setSvoucherno(getString(mainMap,"VoucherNo"));//VoucherNo",vDto.getSvoucherno());//ƾ֤��
//		mainDto.setSvouchercheckno(getString(mainMap,"VoucherCheckNo"));//VoucherCheckNo",danhao);//���˵���
//		mainDto.setSchildpacknum(getString(mainMap,"ChildPackNum"));//ChildPackNum",count);//�Ӱ�����
//		mainDto.setScurpackno(getString(mainMap,"CurPackNo"));//CurPackNo",xuhao);//�������
//		mainDto.setStrecode(getString(mainMap,"TreCode"));//TreCode",vDto.getStrecode());//�����������
//		mainDto.setSclearbankcode(getString(mainMap,"ClearBankCode"));//�������б���
//		mainDto.setSclearbankname(getString(mainMap,"ClearBankName"));//������������
//		mainDto.setSclearaccno(getString(mainMap,"ClearAccNo"));//�����˺�
//		mainDto.setSclearaccnanme(getString(mainMap,"ClearAccNanme"));//�����˻�����
//		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//������ʼ����
//		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//������ֹ����
//		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//�ܱ���
//		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//�ܽ��
//		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
//		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
//		return mainDto;
//	}
//	private TfReconcilePayquotaSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
//	{
//		TfReconcilePayquotaSubDto subDto = new TfReconcilePayquotaSubDto();
//		subDto.setIvousrlno(Long.valueOf(getString(mainMap,"VoucherNo")));
//		subDto.setIseqno(Long.valueOf(getString(subMap,"Id")));//Id",vDto.getSdealno()+(++id));//���
//		subDto.setSid(getString(subMap,"Id"));
//		subDto.setSsupdepcode(getString(subMap,"SupDepCode"));//һ��Ԥ�㵥λ����
//		subDto.setSsupdepname(getString(subMap,"SupDepName"));//һ��Ԥ�㵥λ����
//		subDto.setSfundtypecode(getString(subMap,"FundTypeCode"));//�ʽ����ʱ���
//		subDto.setSfundtypename(getString(subMap,"FundTypeName"));//�ʽ���������
//		subDto.setSpaybankcode(getString(subMap,"PayBankCode"));//�������б���
//		subDto.setSpaybankname(getString(subMap,"PayBankName"));//������������
//		subDto.setSpaybankno(getString(subMap,"PayBankNo"));//���������к�
//		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//֧�����ܷ����Ŀ����
//		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//֧�����ܷ����Ŀ����
//		subDto.setSprocatcode(getString(subMap,"ProCatCode"));//��֧�������
//		subDto.setSprocatname(getString(subMap,"ProCatName"));//��֧��������
//		subDto.setSpaytypecode(getString(subMap,"PayTypeCode"));//֧����ʽ����
//		subDto.setSpaytypename(getString(subMap,"PayTypeName"));//֧����ʽ����
//		subDto.setNpredatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"PreDateMoney"))?"0":getString(subMap,"PreDateMoney")));//���ڶ�����
//		subDto.setNclearamt(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"ClearAmt"))?"0":getString(subMap,"ClearAmt")));//��������������
//		subDto.setNcurreckmoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurReckMoney"))?"0":getString(subMap,"CurReckMoney")));//������������
//		subDto.setNcurdatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurDateMoney"))?"0":getString(subMap,"CurDateMoney")));//���ڶ�����
//		subDto.setShold1(getString(subMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
//		subDto.setShold2(getString(subMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
//		subDto.setShold3(getString(subMap,"Hold3"));//Hold3","");//Ԥ���ֶ�3
//		subDto.setShold4(getString(subMap,"Hold4"));//Hold4","");//Ԥ���ֶ�4
//		return subDto;
//	}
//	private String getString(Map datamap,String key)
//	{
//		if(datamap==null||key==null)
//			return "";
//		else
//			return String.valueOf(datamap.get(key));
//	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<MainAndSubDto> getDataList(TvVoucherinfoDto vDto,SQLExecutor execDetail) throws ITFEBizException
	{
		MainAndSubDto datadto = null;
		StringBuffer sql = null;
		List<MainAndSubDto> dataList = null;
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sql = new StringBuffer("SELECT * FROM TF_DIRECTPAYMSGMAIN WHERE S_TRECODE='");//��ѯ-����ֱ��֧��ƾ֤5201����
			sql.append(vDto.getStrecode()+"' and S_XPAYDATE='"+vDto.getScheckdate()+"' and S_STATUS='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' ");
			if("4".equals(vDto.getShold1()))//����
				sql.append(" and S_BUSINESSTYPECODE='4' order by I_VOUSRLNO desc");
			else//����
				sql.append(" and (S_BUSINESSTYPECODE='1' or S_BUSINESSTYPECODE='3') order by I_VOUSRLNO desc");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TfDirectpaymsgmainDto.class).getDtoCollection();//��ѯ����
			if(detailList!=null&&detailList.size()>0)
			{
				StringBuffer subsql = new StringBuffer("select * from TF_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+") order by I_VOUSRLNO desc");//����ֱ��֧��ƾ֤5201�ӱ�����
				datadto = new MainAndSubDto();
				datadto.setMainDtoList(detailList);
				datadto.setSubDtoList((List<IDto>) execDetail.runQuery(subsql.toString(),TfDirectpaymsgsubDto.class).getDtoCollection());//�ӱ�����
			}
			if(datadto!=null&&datadto.getMainDtoList()!=null&&datadto.getMainDtoList().size()>0&&datadto.getSubDtoList()!=null&&datadto.getSubDtoList().size()>0)
			{
				Map<String,MainAndSubDto> tempMap = new HashMap<String,MainAndSubDto>();
				TfDirectpaymsgmainDto dto = null;
				MainAndSubDto tempdto = null;
				for(IDto idto:datadto.getMainDtoList())
				{
					dto = (TfDirectpaymsgmainDto)idto;
					tempdto = new MainAndSubDto();
					tempdto.setMainDto(dto);
					tempMap.put(String.valueOf(dto.getIvousrlno()), tempdto);
				}
				TfDirectpaymsgsubDto subdto = null;
				for(IDto idto:datadto.getSubDtoList())
				{
					subdto = (TfDirectpaymsgsubDto)idto;
					tempdto = tempMap.get(String.valueOf(subdto.getIvousrlno()));
					if(tempdto!=null)
					{
						if(tempdto.getSubDtoList()!=null)
							tempdto.getSubDtoList().add(subdto);
						else
						{
							tempdto.setSubDtoList(new ArrayList<IDto>());
							tempdto.getSubDtoList().add(subdto);
						}
					}	
				}
				if(tempMap!=null&&tempMap.size()>0)
				{
					dataList = new ArrayList<MainAndSubDto>();
					for(String mapkey:tempMap.keySet())
						dataList.add(tempMap.get(mapkey));
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
		return dataList;
	}
	private List<List<MainAndSubDto>> getSplitPack(List<MainAndSubDto> dataList)
	{
		List<List<MainAndSubDto>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,List<MainAndSubDto>> tempMap = new HashMap<String,List<MainAndSubDto>>();
			List<MainAndSubDto> tempList = null;
			MainAndSubDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				TfDirectpaymsgmainDto pdto = (TfDirectpaymsgmainDto)dto.getMainDto();
				if(tempMap.get(pdto.getSbgttypecode())==null)
				{
					tempList = new ArrayList<MainAndSubDto>();
					tempList.add(dto);
					tempMap.put(pdto.getSbgttypecode(), tempList);
				}else{
					tempList = tempMap.get(pdto.getSbgttypecode());
					tempList.add(dto);
					tempMap.put(pdto.getSbgttypecode(), tempList);
				}
			}
			if(tempMap!=null&&tempMap.size()>0)
			{
				getList = new ArrayList<List<MainAndSubDto>>();
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
	public Map<String, TsInfoconnorgaccDto> getAccMap() {
		return accMap;
	}
	public void setAccMap(Map<String, TsInfoconnorgaccDto> accMap) {
		this.accMap = accMap;
	}
}
