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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
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
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ������Ϣ����3507ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3587 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3587.class);
											
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
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			List<IDto> alldata = getDataList(vDto,execDetail);
			if(vDto.getSorgcode().startsWith("13")){//��������
				Map<String, List>  map = getFundTypeMap(alldata);
				for(java.util.Map.Entry<String, List> entry : map.entrySet()){
					createVoucher(vDto, returnList, cDto, entry.getValue());
				}
			}else {
				createVoucher(vDto, returnList, cDto, alldata);
			}
			
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
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
		List<List<MainAndSubDto>> dataList = getSplitPack(alldata,true);
		if(dataList!=null&&dataList.size()>0)
		{
			List<List> sendList = null;
			List mapList=null;
			for(int k=0;k<dataList.size();k++)
			{
				sendList = this.getSubLists(dataList.get(k), 500);
				if(sendList!=null&&sendList.size()>0)
				{
					List<IDto> tempList = null;
					String danhao=null;
					for(int i=0;i<sendList.size();i++)
					{
						mapList=new ArrayList();
						tempList = sendList.get(i);
						String FileName=null;
						String dirsep = File.separator; 
						String mainvou = "";
						TvVoucherinfoDto dto=new TvVoucherinfoDto();			
						dto.setSorgcode(vDto.getSorgcode());
						dto.setSadmdivcode(vDto.getSadmdivcode());
						dto.setSvtcode(vDto.getSvtcode());
						dto.setScreatdate(TimeFacade.getCurrentStringTime());
						dto.setStrecode(vDto.getStrecode());
						dto.setShold1(String.valueOf(sendList.size()));//������
						dto.setShold2(String.valueOf(i+1));//�������
						dto.setSstyear(dto.getScreatdate().substring(0, 4));				
						dto.setScheckdate(vDto.getScheckdate());
						dto.setShold3(vDto.getShold3());
						dto.setShold4(vDto.getShold4());
						dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
						dto.setSdemo("����ɹ�");
						dto.setSvoucherflag("1");
						dto.setSext1(getString(vDto.getSext1()));//����ʽ1:���з���,2:��������,3:���з���
						dto.setSext2(getString(vDto.getSext2()));
						dto.setSext3(getString(vDto.getSext3()));
						dto.setSext4(getString(vDto.getSext4()));
						dto.setSext5(getString(vDto.getSext5()));
						if (ITFECommonConstant.SRC_NODE.equals("201053200014")){
				              mainvou = VoucherUtil.getCheckNo(dto,tempList,i);
						} else {
				              mainvou = VoucherUtil.getGrantSequence();
				        }
						vDto.setSdealno(mainvou);
						vDto.setSvoucherno(mainvou);
						if(danhao==null)
							danhao=mainvou;
						FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
						dto.setSfilename(FileName);
						mapList.add(vDto);
						mapList.add(cDto);
						mapList.add(tempList);
						List<IDto> idtoList = new ArrayList<IDto>();
						Map map=tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
						dto.setSattach(danhao);//���ʵ���
						dto.setNmoney(MtoCodeTrans.transformBigDecimal(((Map)map.get("Voucher")).get("AllAmt")));
						dto.setIcount(Integer.valueOf(String.valueOf(((Map)map.get("Voucher")).get("AllNum"))));
						//�˴���д���󣬲�����д�˺ţ�Ӧ����ϸ�л�ȡ���˻�����
						//dto.setSpaybankcode(String.valueOf(((Map)map.get("Voucher")).get("ClearAccNo")));
						 List <Map> list = (List) (((Map)((Map)map.get("Voucher")).get("DetailList"))).get("Detail");
						 String paybankno = (String) list.get(0).get("PayBankNo");
						if(((MainAndSubDto)tempList.get(0)).getMainDto() instanceof TfDirectpaymsgmainDto)
						{
							TfDirectpaymsgmainDto tempdto = (TfDirectpaymsgmainDto)((MainAndSubDto)tempList.get(0)).getMainDto();
							if("1".equals(tempdto.getSbusinesstypecode())||"3".equals(tempdto.getSbusinesstypecode()))
							{
								dto.setSpaybankcode("011");
							}else
							{
								dto.setSpaybankcode(paybankno);
							}
						}else
						{
							 dto.setSpaybankcode(paybankno);
						}
						dto.setSdealno(mainvou);
						dto.setSvoucherno(mainvou);	
						List vouList=new ArrayList();
						vouList.add(map);
						vouList.add(dto);
						vouList.add(idtoList);
						returnList.add(vouList);
					}
				}
			}
		}
	}
	private Map tranfor(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			List<IDto> detailList=(List<IDto>) mapList.get(2);
			MainAndSubDto mdto = (MainAndSubDto)detailList.get(0);
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
			if(mdto.getMainDto() instanceof TvPayreckBankDto)
			{
				vouchermap.put("ClearAccNo",((TvPayreckBankDto)mdto.getMainDto()).getSpayeracct());//�����˺�
				vouchermap.put("ClearAccNanme",((TvPayreckBankDto)mdto.getMainDto()).getSpayername());//�����˻�����
				vouchermap.put("ClearAccName",((TvPayreckBankDto)mdto.getMainDto()).getSpayername());//�����˻�����
				vouchermap.put("Hold1",((TvPayreckBankDto)mdto.getMainDto()).getSagentbnkcode());//Ԥ���ֶ�1
			}else if(mdto.getMainDto() instanceof TvPayreckBankBackDto)
			{
				vouchermap.put("ClearAccNo",((TvPayreckBankBackDto)mdto.getMainDto()).getSpayeracct());//�����˺�
				vouchermap.put("ClearAccNanme",((TvPayreckBankBackDto)mdto.getMainDto()).getSpayername());//�����˻�����
				vouchermap.put("ClearAccName",((TvPayreckBankBackDto)mdto.getMainDto()).getSpayername());//�����˻�����
				vouchermap.put("Hold1",((TvPayreckBankBackDto)mdto.getMainDto()).getSagentbnkcode());//Ԥ���ֶ�1
			}else if(mdto.getMainDto() instanceof TfDirectpaymsgmainDto)
			{
				vouchermap.put("ClearAccNo",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSclearbankcode());//�����˺�
				vouchermap.put("ClearAccNanme",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSclearbankcode());//�����˻�����
				vouchermap.put("ClearAccName",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSclearbankcode());//�����˻�����
				vouchermap.put("Hold1",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSpaybankcode());//Ԥ���ֶ�1
			}else if(mdto.getMainDto() instanceof TfPaybankRefundmainDto)
			{
				vouchermap.put("ClearAccNo",((TfPaybankRefundmainDto)mdto.getMainDto()).getSpaysndbnkno());//�����˺�
				vouchermap.put("ClearAccNanme","");//�����˻�����-2252�淶�޴��ֶ�
				vouchermap.put("ClearAccName","");//�����˻�����-2252�淶�޴��ֶ�
				vouchermap.put("Hold1",((TvPayreckBankDto)mdto.getMainDto()).getSagentbnkcode());//Ԥ���ֶ�1
			}

			TsTreasuryDto dto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			if(vDto.getSorgcode().startsWith("13")){//��������
				vouchermap.put("ClearBankCode","001001");//Ԥ���ֶ�1
				vouchermap.put("ClearBankName",dto.getStrename());//Ԥ���ֶ�2
			}else{
				vouchermap.put("ClearBankCode",dto.getStrecode());//�������б���
				vouchermap.put("ClearBankName",dto.getStrename());//������������
			}
			vouchermap.put("BeginDate",vDto.getShold3());//������ʼ����
			vouchermap.put("EndDate",vDto.getShold4());//������ֹ����
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
			{
				vouchermap.put("BgtTypeCode",vDto.getSext3());//Ԥ������
				vouchermap.put("BgtTypeName","1".equals(vDto.getSext3())?"Ԥ����":"Ԥ����");//Ԥ����������
			}
			if(vDto.getStrecode().startsWith("13")){//����Ԥ���ֶ�����ʽ�����
				if(mdto.getMainDto() instanceof TvPayreckBankDto){
					vouchermap.put("Hold1",((TvPayreckBankDto)mdto.getMainDto()).getSfundtypecode());//Ԥ���ֶ�1
					vouchermap.put("Hold2",((TvPayreckBankDto)mdto.getMainDto()).getSfundtypename());//Ԥ���ֶ�2
				}else if(mdto.getMainDto() instanceof TvPayreckBankBackDto){
					vouchermap.put("Hold1",((TvPayreckBankBackDto)mdto.getMainDto()).getSfundtypecode());//Ԥ���ֶ�1
					vouchermap.put("Hold2",((TvPayreckBankBackDto)mdto.getMainDto()).getSfundtypename());//Ԥ���ֶ�2
				}
			}else{
				vouchermap.put("Hold2",getString(vDto.getSverifyusercode()));//Ԥ���ֶ�2
			}
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for(IDto idto:detailList)
			{
				mdto = (MainAndSubDto)idto;
				if(mdto.getMainDto() instanceof TvPayreckBankDto)
				{
					TvPayreckBankDto ddto = (TvPayreckBankDto) mdto.getMainDto();
					if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TvPayreckBankListDto dsdto = (TvPayreckBankListDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//���
							Detailmap.put("PayAgentBillNo",ddto.getSvouno());//���㵥��
							Detailmap.put("PayDetailId",dsdto.getSid()==null?dsdto.getSvouchern0():dsdto.getSid());//������ϸID
							Detailmap.put("VoucherNo",getString(dsdto.getSvouchern0()));//֧��ƾ֤����
							Detailmap.put("SupDepCode",getString(dsdto.getSbdgorgcode()));//Ԥ�㵥λ����
							Detailmap.put("SupDepName",getString(dsdto.getSsupdepname()));//һ��Ԥ�㵥λ����
							Detailmap.put("FundTypeCode",getString(ddto.getSfundtypecode()));//�ʽ����ʱ���
							Detailmap.put("FundTypeName",getString(ddto.getSfundtypename()));//�ʽ���������
							Detailmap.put("PayBankCode",getString(ddto.getSagentbnkcode()));//�������б���
							Detailmap.put("PayBankName",getString(ddto.getSpaybankname()));//������������
							Detailmap.put("PayBankNo",getString(ddto.getSagentbnkcode()));//���������к�
							Detailmap.put("ExpFuncCode",getString(dsdto.getSfuncbdgsbtcode()));//֧�����ܷ����Ŀ����
							Detailmap.put("ExpFuncName",getString(dsdto.getSexpfuncname()));//֧�����ܷ����Ŀ����
							Detailmap.put("ProCatCode",getString(""));//��֧�������
							Detailmap.put("ProCatName",getString(""));//��֧��������
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(ddto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001002"));//֧����ʽ����;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(ddto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001001"));//֧����ʽ����;
							else
								Detailmap.put("PayTypeCode",getString(ddto.getSpaytypecode()));//֧����ʽ����
							Detailmap.put("PayTypeName",getString(ddto.getSpaytypename()));//֧����ʽ����
							Detailmap.put("PayAmt",String.valueOf(dsdto.getFamt()));//֧�����
							if(vDto.getStrecode().startsWith("13")){
								Detailmap.put("Hold1",getString(dsdto.getSvouchern0()));//Ԥ���ֶ�1
							}else{
								Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
							}
							Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
							Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
							Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(ddto.getSagentbnkcode());
							allamt=allamt.add(dsdto.getFamt());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TvPayreckBankBackDto)
				{
					TvPayreckBankBackDto gdto = (TvPayreckBankBackDto)mdto.getMainDto();
					if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TvPayreckBankBackListDto gsdto = (TvPayreckBankBackListDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//���
							Detailmap.put("PayAgentBillNo",gdto.getSvouno());//���㵥��
							Detailmap.put("PayDetailId",gsdto.getSid()==null?gsdto.getSvoucherno():gsdto.getSid());//������ϸID
							Detailmap.put("VoucherNo",getString(gsdto.getSvoucherno()));//֧��ƾ֤����
							Detailmap.put("SupDepCode",getString(gsdto.getSbdgorgcode()));//Ԥ�㵥λ����
							Detailmap.put("SupDepName",getString(gsdto.getSsupdepname()));//һ��Ԥ�㵥λ����
							Detailmap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//�ʽ����ʱ���
							Detailmap.put("FundTypeName",getString(gdto.getSfundtypename()));//�ʽ���������
							Detailmap.put("PayBankCode",getString(gdto.getSagentbnkcode()));//�������б���
							Detailmap.put("PayBankName",getString(gdto.getSpaybankname()));//������������
							Detailmap.put("PayBankNo",getString(gdto.getSagentbnkcode()));//���������к�
							Detailmap.put("ExpFuncCode",getString(gsdto.getSfuncbdgsbtcode()));//֧�����ܷ����Ŀ����
							Detailmap.put("ExpFuncName",getString(gsdto.getSexpfuncname()));//֧�����ܷ����Ŀ����
							Detailmap.put("ProCatCode",getString(""));//��֧�������
							Detailmap.put("ProCatName",getString(""));//��֧��������
							if("0".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("11"));//֧����ʽ����
							else if("1".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("12"));//֧����ʽ����
							else
								Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//֧����ʽ����
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001002"));//֧����ʽ����;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001001"));//֧����ʽ����;
							Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//֧����ʽ����
							if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
								Detailmap.put("PayAmt",String.valueOf(gsdto.getFamt()));//֧�����
							}else{
								Detailmap.put("PayAmt","-"+String.valueOf(gsdto.getFamt()));//֧�����
							}
							if(vDto.getStrecode().startsWith("13")){
								Detailmap.put("Hold1",getString(gsdto.getSvoucherno()));//Ԥ���ֶ�1
							}else{
								Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
							}
							Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
							Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
							Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSagentbnkcode());
							allamt=allamt.subtract(gsdto.getFamt().abs());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TfDirectpaymsgmainDto)
				{
					TfDirectpaymsgmainDto gdto = (TfDirectpaymsgmainDto)mdto.getMainDto();
					if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TfDirectpaymsgsubDto gsdto = (TfDirectpaymsgsubDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//���
							Detailmap.put("PayAgentBillNo",gdto.getSvoucherno());//���㵥��
							Detailmap.put("PayDetailId",gsdto.getSid()==null?gsdto.getSvoucherno():gsdto.getSid());//������ϸID
							Detailmap.put("VoucherNo",getString(gsdto.getSvoucherno()));//֧��ƾ֤����
							Detailmap.put("SupDepCode","".equals(getString(gsdto.getSsupdepcode()))?gsdto.getSagencycode():gsdto.getSsupdepcode());//Ԥ�㵥λ����
							Detailmap.put("SupDepName","".equals(getString(gsdto.getSsupdepname()))?gsdto.getSagencyname():gsdto.getSsupdepname());//һ��Ԥ�㵥λ����
							Detailmap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//�ʽ����ʱ���
							Detailmap.put("FundTypeName",getString(gdto.getSfundtypename()));//�ʽ���������
							Detailmap.put("PayBankCode",getString(gdto.getSpaybankcode()));//�������б���
							Detailmap.put("PayBankName",getString(gdto.getSpaybankname()));//������������
							Detailmap.put("PayBankNo",getString(gdto.getSpayeeacctbankno()));//���������к�
							Detailmap.put("ExpFuncCode",getString(gsdto.getSexpfunccode()));//֧�����ܷ����Ŀ����
							Detailmap.put("ExpFuncName",getString(gsdto.getSexpfuncname()));//֧�����ܷ����Ŀ����
							Detailmap.put("ProCatCode",getString(""));//��֧�������
							Detailmap.put("ProCatName",getString(""));//��֧��������
							if("0".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("11"));//֧����ʽ����
							else if("1".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("12"));//֧����ʽ����
							else
								Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//֧����ʽ����
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001002"));//֧����ʽ����;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001001"));//֧����ʽ����;
							Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//֧����ʽ����
							Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//֧�����
							Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
							Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
							Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
							Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSpayeeacctbankno());
							allamt=allamt.add(gsdto.getNpayamt());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TfPaybankRefundmainDto)
				{
					if(mdto.getExtdto()!=null&&mdto.getExtdto().getSubDtoList()!=null)
					{
						TfDirectpaymsgmainDto gdto = (TfDirectpaymsgmainDto)mdto.getExtdto().getMainDto();
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TfDirectpaymsgsubDto gsdto = (TfDirectpaymsgsubDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//���
							Detailmap.put("PayAgentBillNo",gdto.getSvoucherno());//���㵥��
							Detailmap.put("PayDetailId",gsdto.getSid()==null?gsdto.getSvoucherno():gsdto.getSid());//������ϸID
							Detailmap.put("VoucherNo",getString(gsdto.getSvoucherno()));//֧��ƾ֤����
							Detailmap.put("SupDepCode",getString(gsdto.getSsupdepcode()==null?gsdto.getSagencycode():gsdto.getSsupdepcode()));//Ԥ�㵥λ����
							Detailmap.put("SupDepName",getString(gsdto.getSsupdepname())==null?gsdto.getSagencyname():gsdto.getSsupdepname());//һ��Ԥ�㵥λ����
							Detailmap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//�ʽ����ʱ���
							Detailmap.put("FundTypeName",getString(gdto.getSfundtypename()));//�ʽ���������
							Detailmap.put("PayBankCode",getString(gdto.getSpaybankcode()));//�������б���
							Detailmap.put("PayBankName",getString(gdto.getSpaybankname()));//������������
							Detailmap.put("PayBankNo",getString(gdto.getSclearbankcode()));//���������к�
							Detailmap.put("ExpFuncCode",getString(gsdto.getSexpfunccode()));//֧�����ܷ����Ŀ����
							Detailmap.put("ExpFuncName",getString(gsdto.getSexpfuncname()));//֧�����ܷ����Ŀ����
							Detailmap.put("ProCatCode",getString(""));//��֧�������
							Detailmap.put("ProCatName",getString(""));//��֧��������
							if("0".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("11"));//֧����ʽ����
							else if("1".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("12"));//֧����ʽ����
							else
								Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//֧����ʽ����
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001002"));//֧����ʽ����;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
								Detailmap.put("PayTypeCode",getString("001001"));//֧����ʽ����;
							Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//֧����ʽ����
							Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//֧�����
//							Detailmap.put("PayAmt","-"+String.valueOf(gsdto.getNpayamt()));//֧�����
							Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
							Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
							Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
							Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSpayeeacctbankno());
							allamt=allamt.subtract(gsdto.getNpayamt());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}else
					{
						TfPaybankRefundmainDto gdto = (TfPaybankRefundmainDto)mdto.getMainDto();
						if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
						{
							for(int i=0;i<mdto.getSubDtoList().size();i++)
							{
								id++;
								TfPaybankRefundsubDto gsdto = (TfPaybankRefundsubDto)mdto.getSubDtoList().get(i);
								HashMap<String, Object> Detailmap = new HashMap<String, Object>();
								Detailmap.put("Id",vDto.getSvoucherno()+id);//���
								Detailmap.put("PayAgentBillNo",gdto.getSvoucherno());//���㵥��
								Detailmap.put("PayDetailId",gsdto.getSid());//������ϸID
								Detailmap.put("VoucherNo",getString(""));//֧��ƾ֤����
								Detailmap.put("SupDepCode",getString(""));//Ԥ�㵥λ����
								Detailmap.put("SupDepName",getString(""));//һ��Ԥ�㵥λ����
								Detailmap.put("FundTypeCode",getString(""));//�ʽ����ʱ���
								Detailmap.put("FundTypeName",getString(""));//�ʽ���������
								Detailmap.put("PayBankCode",getString(""));//�������б���
								Detailmap.put("PayBankName",getString(""));//������������
								Detailmap.put("PayBankNo",getString(gdto.getSpaysndbnkno()));//���������к�
								Detailmap.put("ExpFuncCode",getString(""));//֧�����ܷ����Ŀ����
								Detailmap.put("ExpFuncName",getString(""));//֧�����ܷ����Ŀ����
								Detailmap.put("ProCatCode",getString(""));//��֧�������
								Detailmap.put("ProCatName",getString(""));//��֧��������
								if("0".equals(gdto.getSpaytypecode()))
									Detailmap.put("PayTypeCode",getString("11"));//֧����ʽ����
								else if("1".equals(gdto.getSpaytypecode()))
									Detailmap.put("PayTypeCode",getString("12"));//֧����ʽ����
								else
									Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//֧����ʽ����
								if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
									Detailmap.put("PayTypeCode",getString("001002"));//֧����ʽ����;
								else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//����֧����ʽΪ001002��001001
									Detailmap.put("PayTypeCode",getString("001001"));//֧����ʽ����;
								Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//֧����ʽ����
								Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//֧�����
//								Detailmap.put("PayAmt","-"+String.valueOf(gsdto.getNpayamt()));//֧�����
								Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
								Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
								Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
								Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
								if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
									vDto.setSpaybankcode(gdto.getSpaysndbnkno());
								allamt=allamt.subtract(gsdto.getNpayamt().abs());
								Detail.add(Detailmap);
								subdtolist.add(getSubDto(Detailmap,vouchermap));
							}
						}
					}
				}
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
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	private TfReconcilePayinfoMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReconcilePayinfoMainDto mainDto = new TfReconcilePayinfoMainDto();
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
		mainDto.setSclearaccno(getString(mainMap,"ClearAccNo"));//�����˺�
		mainDto.setSclearaccnanme(getString(mainMap,"ClearAccNanme"));//�����˻�����
		mainDto.setSclearbankcode(getString(mainMap,"ClearBankCode"));//�������б���
		mainDto.setSclearbankname(getString(mainMap,"ClearBankName"));//������������
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//������ʼ����
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//������ֹ����
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//�ܱ���
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//�ܽ��
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
		mainDto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
		return mainDto;
	}
	private TfReconcilePayinfoSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReconcilePayinfoSubDto subDto = new TfReconcilePayinfoSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//���
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSsupdepcode(getString(subMap,"SupDepCode"));//Ԥ�㵥λ����
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
		subDto.setNpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"PayAmt")));//֧�����
		subDto.setSxcheckresult("0");//���˽��Ĭ�ϳɹ�
		subDto.setShold1(getString(subMap,"Hold1"));//Ԥ���ֶ�1
		subDto.setShold2(getString(subMap,"Hold2"));//Ԥ���ֶ�2
		subDto.setShold3(getString(subMap,"Hold3"));//Ԥ���ֶ�3
		subDto.setShold4(getString(subMap,"Hold4"));//Ԥ���ֶ�4
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
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			//2301��ѯ���л����Ѿ��ص�������
			sql = new StringBuffer("SELECT * FROM HTV_PAYRECK_BANK WHERE I_VOUSRLNO in(");
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
			sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
			sql.append(vDto.getSext1()==null?"":" AND S_EXT1=? ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_EXT4=? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");//S_CONFIRUSERCODE
			execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayreckBankDto.class).getDtoCollection();//��ʷ������
			List<IDto> mainsublist = null;
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYRECK_BANK_LIST where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//���л����ӱ�����
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				execDetail.setMaxRows(500000);
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
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//��ʷ���ӱ�
							getList.add(datadto);
						}
					}
				}
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYRECK_BANK","TV_PAYRECK_BANK"),TvPayreckBankDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYRECK_BANK_LIST where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYRECK_BANK","TV_PAYRECK_BANK"),"*", "I_VOUSRLNO")+")";//���л����ӱ�����
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				execDetail.setMaxRows(500000);
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
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//��ʷ���ӱ�
							getList.add(datadto);
						}
					}
				}
			}
			//2302��ѯ�����˿��Ѿ��ص�������
			sql = new StringBuffer("SELECT * FROM HTV_PAYRECK_BANK_BACK WHERE I_VOUSRLNO in(");//��ѯ���л����Ѿ��ص�������
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE (S_VTCODE= ? ");
			/*
			 * ���Ϊ�Ϻ�ģʽ����Ҫ��ֱ��֧���˿����ݼ���ȥ
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				sql.append(" OR S_VTCODE= ? ");
			}
			sql.append(") AND S_ORGCODE= ? ");
			sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
			sql.append(vDto.getSext1()==null?"":" AND S_EXT1=? ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_EXT4=? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");
			execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
			if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
				execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayreckBankBackDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
				if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
					execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				execDetail.setMaxRows(500000);
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
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//�ӱ�
							getList.add(datadto);
						}
					}
				}
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
			if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
				execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_PAYRECK_BANK_BACK","TV_PAYRECK_BANK_BACK"),TvPayreckBankBackDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
				if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
					execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				String subsql = "select * from TV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_PAYRECK_BANK_BACK","TV_PAYRECK_BANK_BACK"), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				execDetail.setMaxRows(500000);
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
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//�ӱ�
							getList.add(datadto);
						}
					}
				}
			}
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&(vDto.getSext1()==null||MsgConstant.directPay.equals(vDto.getSext1())))
			{
				//����ֱ��֧��ƾ֤5201����
				sql = new StringBuffer("SELECT * FROM HTF_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//��ѯ���л����Ѿ��ص�������
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5201+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE='"+vDto.getSpaybankcode()+"' ");
				sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
				sql.append("AND S_CONFIRUSERCODE BETWEEN '"+vDto.getShold3()+"' AND '"+vDto.getShold4()+"')");
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
						mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
						if(mainsublist!=null&&mainsublist.size()>0)
						{
							for(int j=0;j<mainsublist.size();j++)
							{
								datadto = new MainAndSubDto();
								datadto.setMainDto(tempdto);
								subList = new ArrayList<IDto>();
								subList.add(mainsublist.get(j));
								datadto.setSubDtoList(subList);//�ӱ�
								getList.add(datadto);
							}
						}
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
						mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
						if(mainsublist!=null&&mainsublist.size()>0)
						{
							for(int j=0;j<mainsublist.size();j++)
							{
								datadto = new MainAndSubDto();
								datadto.setMainDto(tempdto);
								subList = new ArrayList<IDto>();
								subList.add(mainsublist.get(j));
								datadto.setSubDtoList(subList);//�ӱ�
								getList.add(datadto);
							}
						}
					}
				}
				/**
				 * �Ϻ�ֱ��֧���˿�2252�ύʱ�����2252�����ݸ��Ƶ���TV_PAYRECK_BANK_BACK�������˿���Ϣ��Ҫ�ڸ�TV_PAYRECK_BANK_BACK����Ϣȥ���ݣ�������TF_PAYBANK_REFUNDMAINȥ����
				 */
//				sql = new StringBuffer("SELECT * FROM HTF_PAYBANK_REFUNDMAIN WHERE I_VOUSRLNO in(");//�տ������˿�֪ͨ2252������ʷ��
//				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_2252+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
//				sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
//				sql.append("AND S_CREATDATE BETWEEN '"+vDto.getShold3()+"' AND '"+vDto.getShold4()+"')");
//				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TfPaybankRefundmainDto.class).getDtoCollection();//��ʷ������
//				if(detailList!=null&&detailList.size()>0)
//				{
//					if(getList==null)
//						getList = new ArrayList<IDto>();
//					String subsql = "select * from HTF_PAYBANK_REFUNDSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
//					List<IDto> subList = null;
//					subList = (List<IDto>)execDetail.runQuery(subsql, TfPaybankRefundsubDto.class).getDtoCollection();
//					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
//					if(subList!=null&&subList.size()>0)
//					{
//						List<IDto> tempList = null;
//						TfPaybankRefundsubDto subdto = null;
//						for(IDto tempdto:subList)
//						{
//							subdto = (TfPaybankRefundsubDto)tempdto;
//							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
//							if(tempList==null)
//							{
//								tempList = new ArrayList<IDto>();
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}else
//							{
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}
//						}
//					}
//					Map<String,MainAndSubDto> map5201 = get5201Map(StringUtil.replace(sql.toString(), "HTF_PAYBANK_REFUNDMAIN", "HTF_DIRECTPAYMSGMAIN"),StringUtil.replace(subsql,"HTF_PAYBANK_REFUNDSUB","HTF_DIRECTPAYMSGSUB"),execDetail);
//					MainAndSubDto datadto = null;
//					TfPaybankRefundmainDto tempdto = null;
//					for(int i=0;i<detailList.size();i++)
//					{
//						tempdto = (TfPaybankRefundmainDto)detailList.get(i);
//						datadto = new MainAndSubDto();
//						datadto.setMainDto(tempdto);
//						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//��ʷ���ӱ�
//						datadto.setExtdto(map5201.get(String.valueOf(tempdto.getIvousrlno())));
//						getList.add(datadto);
//					}
//					
//				}
//				detailList=  (List<IDto>) execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_PAYBANK_REFUNDMAIN","TF_PAYBANK_REFUNDMAIN"),TfPaybankRefundmainDto.class).getDtoCollection();//����
//				if(detailList!=null&&detailList.size()>0)
//				{
//					if(getList==null)
//						getList = new ArrayList<IDto>();
//					String subsql = "select * from TF_PAYBANK_REFUNDSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_PAYBANK_REFUNDMAIN","TF_PAYBANK_REFUNDMAIN"), "*", "I_VOUSRLNO")+")";
//					List<IDto> subList = null;
//					subList = (List<IDto>)execDetail.runQuery(subsql, TfPaybankRefundsubDto.class).getDtoCollection();
//					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
//					if(subList!=null&&subList.size()>0)
//					{
//						List<IDto> tempList = null;
//						TfPaybankRefundsubDto subdto = null;
//						for(IDto tempdto:subList)
//						{
//							subdto = (TfPaybankRefundsubDto)tempdto;
//							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
//							if(tempList==null)
//							{
//								tempList = new ArrayList<IDto>();
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}else
//							{
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}
//						}
//					}
//					MainAndSubDto datadto = null;
//					TfPaybankRefundmainDto tempdto = null;
//					for(int i=0;i<detailList.size();i++)
//					{
//						tempdto = (TfPaybankRefundmainDto)detailList.get(i);
//						datadto = new MainAndSubDto();
//						datadto.setMainDto(tempdto);
//						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//�ӱ�
//						getList.add(datadto);
//					}
//				}
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
	public Map<String, MainAndSubDto> get5201Map(String mainsql5201, String subsql5201,SQLExecutor execDetail) throws JAFDatabaseException {
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
	private List<List<MainAndSubDto>> getSplitPack(List<IDto> dataList,boolean ispackage)
	{
		List<List<MainAndSubDto>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,List<MainAndSubDto>> tempMap = new HashMap<String,List<MainAndSubDto>>();
			TvPayreckBankDto ddto = null;
			TvPayreckBankBackDto gdto = null;
			TfDirectpaymsgmainDto fdto = null;
			TfPaybankRefundmainDto pdto = null;
			List<MainAndSubDto> tempList = null;
			MainAndSubDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(!ispackage)
				{
					if(tempMap.get("alldata")==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put("alldata", tempList);
					}else
					{
						tempList = tempMap.get("alldata");
						tempList.add(dto);
						tempMap.put("alldata",tempList);
					}
				}else if(dto.getMainDto() instanceof TvPayreckBankDto)
				{
					ddto = (TvPayreckBankDto)dto.getMainDto();
					if(tempMap.get(ddto.getSagentbnkcode())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(ddto.getSagentbnkcode(), tempList);
					}else
					{
						tempList = tempMap.get(ddto.getSagentbnkcode());
						tempList.add(dto);
						tempMap.put(ddto.getSagentbnkcode(),tempList);
					}
				}else if(dto.getMainDto() instanceof TvPayreckBankBackDto)
				{
					gdto = (TvPayreckBankBackDto)dto.getMainDto();
					if(tempMap.get(gdto.getSagentbnkcode())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(gdto.getSagentbnkcode(), tempList);
					}else
					{
						tempList = tempMap.get(gdto.getSagentbnkcode());
						tempList.add(dto);
						tempMap.put(gdto.getSagentbnkcode(),tempList);
					}
				}
				else if(dto.getMainDto() instanceof TfDirectpaymsgmainDto)
				{
					fdto = (TfDirectpaymsgmainDto)dto.getMainDto();
					if("1".equals(fdto.getSbusinesstypecode())||"3".equals(fdto.getSbusinesstypecode()))
					{
						if(tempMap.get("011")==null)
						{
							tempList = new ArrayList<MainAndSubDto>();
							tempList.add(dto);
							tempMap.put("011", tempList);
						}else
						{
							tempList = tempMap.get("011");
							tempList.add(dto);
							tempMap.put("011",tempList);
						}
					}else
					{
						if(tempMap.get(fdto.getSpaybankcode())==null)
						{
							tempList = new ArrayList<MainAndSubDto>();
							tempList.add(dto);
							tempMap.put(fdto.getSpaybankcode(), tempList);
						}else
						{
							tempList = tempMap.get(fdto.getSpaybankcode());
							tempList.add(dto);
							tempMap.put(fdto.getSpaybankcode(),tempList);
						}
					}
				}
				else if(dto.getMainDto() instanceof TfPaybankRefundmainDto)
				{
					pdto = (TfPaybankRefundmainDto)dto.getMainDto();
					if(tempMap.get(pdto.getSpaysndbnkno())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(pdto.getSpaysndbnkno(), tempList);
					}else
					{
						tempList = tempMap.get(pdto.getSpaysndbnkno());
						tempList.add(dto);
						tempMap.put(pdto.getSpaysndbnkno(),tempList);
					}
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
	
	
	/**
	 * ����ѯ������ʽ����ͷְ�
	 * @param alldata
	 * @return
	 */
	private Map<String, List>  getFundTypeMap(List<IDto> alldata){
		Map<String, List> map = new HashMap<String, List>();
		if(alldata==null||alldata.size()<=0)
			return map;
		for(IDto msdto : alldata){
			MainAndSubDto dto = (MainAndSubDto) msdto;
			if(dto.getMainDto() instanceof TvPayreckBankDto){
				if(map.get(((TvPayreckBankDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayreckBankDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvPayreckBankDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}else if(dto.getMainDto() instanceof TvPayreckBankBackDto){
				if(map.get(((TvPayreckBankBackDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayreckBankBackDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvPayreckBankBackDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}
			
		}
		return map;
	}
	
}
