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
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialSubDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
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
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * ʵ����Ϣ��������(3508)ת��
 * 
 * @author renqingbin
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3588 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3588.class);
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
		if (ITFECommonConstant.SRC_NODE.equals("000087100006"))
		{
			List returnList = new ArrayList();
			if(vDto.getSext3()==null||"".equals(vDto.getSext3()))
			{
				vDto.setSext3("011");//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�1����2��˰
				returnList.addAll(getVoucher(vDto));
				if(ITFECommonConstant.PUBLICPARAM.contains(",send3508=more,"))
				{
					vDto.setSext3("012");
					returnList.addAll(getVoucher(vDto));
				}
			}else
			{
				returnList.addAll(getVoucher(vDto));
			}
			return returnList;
		}else
		{
			return getVoucher(vDto);
		}
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
		List<List<MainAndSubDto>> dataList = getSplitPack(alldata);
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
			            if (ITFECommonConstant.SRC_NODE.equals("201053200014"))
			              mainvou = VoucherUtil.getCheckNo(vDto,tempList,i);
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
						dto.setSattach(danhao);//���˵���
						dto.setShold1(String.valueOf(sendList.size()));//������
						dto.setShold2(String.valueOf(i+1));//�����
						dto.setSstyear(dto.getScreatdate().substring(0, 4));				
						dto.setScheckdate(vDto.getScheckdate());
						dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
						dto.setShold3(vDto.getShold3());
						dto.setShold4(vDto.getShold4());
//							if(ITFECommonConstant.PUBLICPARAM.indexOf(",stampmode=sign,")<0)//�Ƿ����ǩ��
//							{
						dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
						dto.setSdemo("����ɹ�");
						dto.setSvoucherflag("1");
						dto.setSvoucherno(mainvou);	
						dto.setIcount(tempList.size());
						dto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
						dto.setSext2(getString(vDto.getSext2()));
						dto.setSext3(getString(vDto.getSext3()));
						dto.setSext4(getString(vDto.getSext4()));
						dto.setSext5(getString(vDto.getSext5()));
						if(ITFECommonConstant.PUBLICPARAM.contains(",send3508=more,")&&"012".equals(vDto.getSext3()))
						{
							dto.setSext5(vDto.getSext3());
						}
						mapList.add(vDto);
						mapList.add(cDto);
						mapList.add(tempList);
						List<IDto> idtoList = new ArrayList<IDto>();
						Map map=tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
						dto.setNmoney(MtoCodeTrans.transformBigDecimal(map.get("Voucher")==null?null:((Map)map.get("Voucher")).get("AllAmt")));
						dto.setIcount(Integer.valueOf(String.valueOf((map.get("Voucher")==null?null:((Map)map.get("Voucher")).get("AllNum")))));
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
			MainAndSubDto mdto = null;
			TvPayoutmsgmainDto pdto = null;
			TvPayoutbackmsgMainDto badto = null;
			TvVoucherinfoAllocateIncomeDto tadto = null;
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
			vouchermap.put("BeginDate",vDto.getShold3());//������ʼ����
			vouchermap.put("EndDate",vDto.getShold4());//������ֹ����
			vouchermap.put("ClearBankCode",getString(""));//�������б���
			vouchermap.put("ClearBankName",getString(""));//������������
			vouchermap.put("ClearAccNo",getString(""));//�����˺�
			vouchermap.put("ClearAccNanme",getString(""));//�����˻�����
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
			{
				vouchermap.put("BgtTypeCode",vDto.getSext3());//Ԥ������
				vouchermap.put("BgtTypeName","1".equals(vDto.getSext3())?"Ԥ����":"Ԥ����");//Ԥ����������
			}
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for(IDto idto:detailList)
			{
				mdto = (MainAndSubDto)idto;
				setIdtoToMap(vouchermap,mdto.getMainDto());
				if(mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0&&mdto.getMainDto() instanceof TvPayoutmsgmainDto)
				{
					pdto = (TvPayoutmsgmainDto)mdto.getMainDto();
					TvPayoutmsgsubDto subdto = null;
					for(int i=0;i<mdto.getSubDtoList().size();i++)
					{
						id++;
						subdto = (TvPayoutmsgsubDto)mdto.getSubDtoList().get(i);
						HashMap<String, Object> Detailmap = new HashMap<String, Object>();
						Detailmap.put("Id",vDto.getSvoucherno()+id);//���
						Detailmap.put("PayDetailId",subdto.getSid()==null?subdto.getStaxticketno():subdto.getSid());//������ϸId
						Detailmap.put("VoucherNo",getString(pdto.getStaxticketno()));//Ԥ���ֶ�1
						Detailmap.put("BgtTypeCode",getString(subdto.getSbgttypecode()));//Ԥ�����ͱ���
						Detailmap.put("BgtTypeName",getString(subdto.getSbgttypename()));//Ԥ����������
						Detailmap.put("FundTypeCode",getString(pdto.getSfundtypecode()));//�ʽ����ʱ���
						Detailmap.put("FundTypeName",getString(pdto.getSfundtypename()));//�ʽ���������
						Detailmap.put("PayTypeCode",getString("91"));//֧����ʽ����
						Detailmap.put("PayTypeName",getString("ʵ���ʽ�"));//֧����ʽ����
						Detailmap.put("PayeeAcctNo","".equals(getString(subdto.getSpayeeacctno()))?getString(pdto.getSrecacct()):getString(subdto.getSpayeeacctno()));//�տ����˺�
						Detailmap.put("PayeeAcctName","".equals(getString(subdto.getSpayeeacctname()))?getString(pdto.getSrecname()):getString(subdto.getSpayeeacctname()));//�տ�������
						Detailmap.put("PayeeAcctBankName","".equals(getString(subdto.getSpayeeacctbankname()))?getString(pdto.getSrecbankname()):getString(subdto.getSpayeeacctbankname()));//�տ�������
						Detailmap.put("PayAcctNo",getString(pdto.getSpayeracct()));//�����˻��˺�
						Detailmap.put("PayAcctName",getString(pdto.getSpayername()));//�����˻�����
						Detailmap.put("PayAcctBankName",getString(pdto.getSpayerbankname()));//�����˻�����
						Detailmap.put("AgencyCode","".equals(getString(pdto.getSbudgetunitcode()))?getString(subdto.getSagencycode()):getString(pdto.getSbudgetunitcode()));//Ԥ�㵥λ����
						Detailmap.put("AgencyName","".equals(getString(pdto.getSunitcodename()))?getString(subdto.getSagencyname()):getString(pdto.getSunitcodename()));//Ԥ�㵥λ����
						Detailmap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//֧�����ܷ����Ŀ����
						Detailmap.put("ExpFuncName",getString(subdto.getSexpfuncname()));//֧�����ܷ����Ŀ����
						Detailmap.put("ExpEcoCode",getString(subdto.getSexpecocode()));//���÷����Ŀ����
						Detailmap.put("ExpEcoName",getString(subdto.getSexpeconame()));//���÷����Ŀ����
						Detailmap.put("PayAmt",String.valueOf(subdto.getNmoney()));//������
						Detailmap.put("Hold1",getString(subdto.getStaxticketno()));//Ԥ���ֶ�1
						Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
						Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
						Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
						allamt=allamt.add(subdto.getNmoney());
						Detail.add(Detailmap);
						subdtolist.add(getSubDto(Detailmap,vouchermap));
					}
					if("1".equals(pdto.getSbackflag()))
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							subdto = (TvPayoutmsgsubDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//���
							Detailmap.put("PayDetailId",subdto.getSid()==null?subdto.getStaxticketno():subdto.getSid());//������ϸId
							Detailmap.put("VoucherNo",getString(pdto.getStaxticketno()));//Ԥ���ֶ�1
							Detailmap.put("BgtTypeCode",getString(subdto.getSbgttypecode()));//Ԥ�����ͱ���
							Detailmap.put("BgtTypeName",getString(subdto.getSbgttypename()));//Ԥ����������
							Detailmap.put("FundTypeCode",getString(pdto.getSfundtypecode()));//�ʽ����ʱ���
							Detailmap.put("FundTypeName",getString(pdto.getSfundtypename()));//�ʽ���������
							Detailmap.put("PayTypeCode",getString("91"));//֧����ʽ����
							Detailmap.put("PayTypeName",getString("ʵ���ʽ��˿�"));//֧����ʽ����
							Detailmap.put("PayeeAcctNo","".equals(getString(subdto.getSpayeeacctno()))?getString(pdto.getSrecacct()):getString(subdto.getSpayeeacctno()));//�տ����˺�
							Detailmap.put("PayeeAcctName","".equals(getString(subdto.getSpayeeacctname()))?getString(pdto.getSrecname()):getString(subdto.getSpayeeacctname()));//�տ�������
							Detailmap.put("PayeeAcctBankName","".equals(getString(subdto.getSpayeeacctbankname()))?getString(pdto.getSrecbankname()):getString(subdto.getSpayeeacctbankname()));//�տ�������
							Detailmap.put("PayAcctNo",getString(pdto.getSpayeracct()));//�����˻��˺�
							Detailmap.put("PayAcctName",getString(pdto.getSpayername()));//�����˻�����
							Detailmap.put("PayAcctBankName",getString(pdto.getSpayerbankname()));//�����˻�����
							Detailmap.put("AgencyCode",getString(pdto.getSbudgetunitcode()));//Ԥ�㵥λ����
							Detailmap.put("AgencyName",getString(pdto.getSunitcodename()));//Ԥ�㵥λ����
							Detailmap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//֧�����ܷ����Ŀ����
							Detailmap.put("ExpFuncName",getString(subdto.getSexpfuncname()));//֧�����ܷ����Ŀ����
							Detailmap.put("ExpEcoCode",getString(subdto.getSexpecocode()));//���÷����Ŀ����
							Detailmap.put("ExpEcoName",getString(subdto.getSexpeconame()));//���÷����Ŀ����
							if (ITFECommonConstant.SRC_NODE.equals("201053200014")){//�ൺ
								Detailmap.put("PayDetailId", pdto.getStaxticketno());
								Detailmap.put("PayAmt","-"+String.valueOf(pdto.getShold2()));//������
							}else{
					            Detailmap.put("PayDetailId", pdto.getSdealno() + subdto.getSbizno());
					            Detailmap.put("PayAmt","-"+String.valueOf(subdto.getNmoney()));//������
					        }
							Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
							Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
							Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
							Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
							allamt=allamt.subtract(subdto.getNmoney());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto)
				{
					tadto = (TvVoucherinfoAllocateIncomeDto)mdto.getMainDto();
					id++;
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id",vDto.getSvoucherno()+String.valueOf(tadto.getSdealno())+id);//���
					Detailmap.put("PayDetailId",tadto.getSdealno());//������ϸId
					Detailmap.put("VoucherNo",getString(tadto.getSdealno()));//Ԥ���ֶ�1
					Detailmap.put("BgtTypeCode",getString(""));//Ԥ�����ͱ���
					Detailmap.put("BgtTypeName",getString(""));//Ԥ����������
					Detailmap.put("FundTypeCode",getString("9"));//�ʽ����ʱ���
					Detailmap.put("FundTypeName",getString("�����ʽ�"));//�ʽ���������
					Detailmap.put("PayTypeCode",getString("91"));//֧����ʽ����
					Detailmap.put("PayTypeName",getString("ʵ���˿�"));//֧����ʽ����
					Detailmap.put("PayeeAcctNo",getString(tadto.getSpayeeacctno()));//�տ����˺�
					Detailmap.put("PayeeAcctName",getString(tadto.getSpayeeacctname()));//�տ�������
					Detailmap.put("PayeeAcctBankName",getString(tadto.getSpayeeacctbankname()));//�տ�������
					Detailmap.put("PayAcctNo",getString(tadto.getSpayacctno()));//�����˻��˺�
					Detailmap.put("PayAcctName",getString(tadto.getSpayacctname()));//�����˻�����
					Detailmap.put("PayAcctBankName",getString(tadto.getSpayacctbankname()));//�����˻�����
					Detailmap.put("AgencyCode",getString("99999"));//Ԥ�㵥λ����
					Detailmap.put("AgencyName",getString("99999"));//Ԥ�㵥λ����
					Detailmap.put("ExpFuncCode",getString("99999"));//֧�����ܷ����Ŀ����
					Detailmap.put("ExpFuncName",getString("99999"));//֧�����ܷ����Ŀ����
					Detailmap.put("ExpEcoCode",getString(""));//���÷����Ŀ����
					Detailmap.put("ExpEcoName",getString(""));//���÷����Ŀ����
					Detailmap.put("PayAmt",String.valueOf(tadto.getNmoney()));//������
//					Detailmap.put("PayAmt","-"+String.valueOf(tadto.getNmoney()));//������
					Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
					Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
					Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
					Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
					allamt=allamt.subtract(tadto.getNmoney());
					Detail.add(Detailmap);
					subdtolist.add(getSubDto(Detailmap,vouchermap));
				}else if(mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0&&mdto.getMainDto() instanceof TvPayoutbackmsgMainDto)
				{
					badto = (TvPayoutbackmsgMainDto)mdto.getMainDto();
					TvPayoutbackmsgSubDto subdto = null;
					for(int i=0;i<mdto.getSubDtoList().size();i++)
					{
						id++;
						subdto = (TvPayoutbackmsgSubDto)mdto.getSubDtoList().get(i);
						HashMap<String, Object> Detailmap = new HashMap<String, Object>();
						Detailmap.put("Id",vDto.getSvoucherno()+id);//���
						Detailmap.put("PayDetailId",subdto.getSseqno());//������ϸId
						Detailmap.put("VoucherNo",getString(badto.getSvouno()));//Ԥ���ֶ�1
						Detailmap.put("BgtTypeCode",getString(subdto.getSbudgetprjcode()));//Ԥ�����ͱ���
						Detailmap.put("BgtTypeName",getString(""));//Ԥ����������
						Detailmap.put("FundTypeCode",getString("9"));//�ʽ����ʱ���
						Detailmap.put("FundTypeName",getString("�����ʽ�"));//�ʽ���������
						Detailmap.put("PayTypeCode",getString("91"));//֧����ʽ����
						Detailmap.put("PayTypeName",getString("ʵ���˿�"));//֧����ʽ����
						Detailmap.put("PayeeAcctNo",getString(badto.getSpayeeacct()));//�տ����˺�
						Detailmap.put("PayeeAcctName",getString(badto.getSpayeename()));//�տ�������
						Detailmap.put("PayeeAcctBankName",getString(badto.getSpayeeopbkno()));//�տ�������
						Detailmap.put("PayAcctNo",getString(badto.getSpayeracct()));//�����˻��˺�
						Detailmap.put("PayAcctName",getString(badto.getSpayername()));//�����˻�����
						Detailmap.put("PayAcctBankName",getString(""));//�����˻�����
						Detailmap.put("AgencyCode",getString(badto.getSbudgetunitcode()));//Ԥ�㵥λ����
						Detailmap.put("AgencyName",getString(badto.getSunitcodename()));//Ԥ�㵥λ����
						Detailmap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//֧�����ܷ����Ŀ����
						Detailmap.put("ExpFuncName",getString(subdto.getSfunsubjectname()));//֧�����ܷ����Ŀ����
						Detailmap.put("ExpEcoCode",getString(subdto.getSecnomicsubjectcode()));//���÷����Ŀ����
						Detailmap.put("ExpEcoName",getString(""));//���÷����Ŀ����
						Detailmap.put("PayAmt",String.valueOf(subdto.getNmoney()));//������
//						Detailmap.put("PayAmt","-"+String.valueOf(subdto.getNmoney()));//������
						Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
						Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
						Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
						Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
						allamt=allamt.subtract(subdto.getNmoney());
						Detail.add(Detailmap);
						subdtolist.add(getSubDto(Detailmap,vouchermap));
					}
				}
			}
			vouchermap.put("AllNum",id);//�ܱ���
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));//�ܽ��
			if(vDto.getStrecode().startsWith("13")){//����Ԥ���ֶ�����ʽ�����
				vouchermap.put("Hold1",getString(pdto.getSfundtypecode()));//Ԥ���ֶ�1
				vouchermap.put("Hold2",getString(pdto.getSfundtypename()));//Ԥ���ֶ�2
			}else{
				vouchermap.put("Hold1","");//Ԥ���ֶ�1
				vouchermap.put("Hold2",getString(vDto.getSverifyusercode()));//Ԥ���ֶ�2
			}
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
	private TfReconcileRealdialMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReconcileRealdialMainDto mainDto = new TfReconcileRealdialMainDto();
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
	private TfReconcileRealdialSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReconcileRealdialSubDto subDto = new TfReconcileRealdialSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//���
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSpaydetailid(getString(subMap,"PayDetailId"));//������ϸId
		subDto.setSbgttypecode(getString(subMap,"BgtTypeCode"));//Ԥ�����ͱ���
		subDto.setSbgttypename(getString(subMap,"BgtTypeName"));//Ԥ����������
		subDto.setSfundtypecode(getString(subMap,"FundTypeCode"));//�ʽ����ʱ���
		subDto.setSfundtypename(getString(subMap,"FundTypeName"));//�ʽ���������
		subDto.setSpaytypecode(getString(subMap,"PayTypeCode"));//֧����ʽ����
		subDto.setSpaytypename(getString(subMap,"PayTypeName"));//֧����ʽ����
		subDto.setSpayeeacctno(getString(subMap,"PayeeAcctNo"));//�տ����˺�
		subDto.setSpayeeacctname(getString(subMap,"PayeeAcctName"));//�տ�������
		subDto.setSpayeeacctbankname(getString(subMap,"PayeeAcctBankName"));//�տ�������
		subDto.setSpayacctno(getString(subMap,"PayAcctNo"));//�����˻��˺�
		subDto.setSpayacctname(getString(subMap,"PayAcctName"));//�����˻�����
		subDto.setSpayacctbankname(getString(subMap,"PayAcctBankName"));//�����˻�����
		subDto.setSagencycode(getString(subMap,"AgencyCode"));//Ԥ�㵥λ����
		subDto.setSagencyname(getString(subMap,"AgencyName"));//Ԥ�㵥λ����
		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//֧�����ܷ����Ŀ����
		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//֧�����ܷ����Ŀ����
		subDto.setShold1(getString(subMap,"ExpEcoCode"));//���÷����Ŀ����
		subDto.setShold1(getString(subMap,"ExpEcoName"));//���÷����Ŀ����
		subDto.setNpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"PayAmt")));//������
		subDto.setShold1(getString(subMap,"Hold1"));//Ԥ���ֶ�1
		subDto.setShold2(getString(subMap,"Hold2"));//Ԥ���ֶ�2
		subDto.setShold3(getString(subMap,"Hold3"));//Ԥ���ֶ�3
		subDto.setShold4(getString(subMap,"Hold4"));//Ԥ���ֶ�4
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
			sql = new StringBuffer("SELECT * FROM HTV_PAYOUTMSGMAIN WHERE S_BIZNO in(");//��ѯʵ���ʽ���ʷ���Ѿ��ص�������
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE (S_VTCODE= ? "+(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,")?" OR S_VTCODE= ?":"")+")  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ? ");
			if(vDto.getSext3()!=null&&"011".equals(vDto.getSext3()))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
					sql.append(" and S_ATTACH<>?");
			else if(vDto.getSext3()!=null&&"012".equals(vDto.getSext3()))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
				sql.append(" and S_ATTACH=?");
			sql.append(") ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_BUDGETTYPE=? ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();//��ʷ������
			List<IDto> mainsublist = null;
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";//��ѯʵ���ʽ��ӱ���ʷ������
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					tempdto.setSbackflag("0");
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			//��ѯʵ���ʽ���ʽ���Ѿ��ص�������
			execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),TvPayoutmsgmainDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"), "*", "S_BIZNO")+")";//��ѯʵ���ʽ��ӱ���ʽ�������
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					tempdto.setSbackflag("0");
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			//ǰ�÷���ʵ���ʽ��˿�����
			sql = new StringBuffer("SELECT * FROM HTV_PAYOUTMSGMAIN WHERE S_BIZNO in(");//��ѯʵ���ʽ���ʷ���Ѿ��ص�������
			sql.append("SELECT S_EXT4 FROM HTV_VOUCHERINFO WHERE S_EXT4 is not null and S_EXT4<>'' and (S_VTCODE= ? "+(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,")?" OR S_VTCODE= ?":"")+")  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ? ");
			if(vDto.getSext3()!=null&&"011".equals(vDto.getSext3()))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
					sql.append(" and S_ATTACH<>?");
			else if(vDto.getSext3()!=null&&"012".equals(vDto.getSext3()))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
				sql.append(" and S_ATTACH=?");
			sql.append(") ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_BUDGETTYPE=? ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";//��ѯʵ���ʽ��ӱ���ʷ������
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			//��ѯʵ���ʽ���ʽ���Ѿ��ص�������
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),TvPayoutmsgmainDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"), "*", "S_BIZNO")+")";//��ѯʵ���ʽ��ӱ���ʽ�������
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			sql = new StringBuffer("SELECT * FROM TV_PAYOUTBACKMSG_MAIN WHERE S_VOUNO IN(");//��ѯtcbs��ִ�����˿��Ѿ�����ƾ֤�������
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ?  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?) ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_BUDGETTYPE=? ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutbackmsgMainDto.class).getDtoCollection();//��ʷ������
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList.addAll(execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvPayoutbackmsgMainDto.class).getDtoCollection());//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTBACKMSG_SUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutbackmsgSubDto.class).getDtoCollection();
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
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
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			sql = new StringBuffer("SELECT * FROM TV_VOUCHERINFO_ALLOCATE_INCOME WHERE S_DEALNO IN(");//��ѯʵ���ʽ���ʷ���Ѿ��ص�������
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ?  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?) ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvVoucherinfoAllocateIncomeDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
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
	private List<List<MainAndSubDto>> getSplitPack(List<IDto> dataList)
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
				if(dto.getMainDto() instanceof TvPayoutmsgmainDto)
				{
					TvPayoutmsgmainDto pdto = (TvPayoutmsgmainDto)dto.getMainDto();
					if(tempMap.get(pdto.getSpayeracct())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(pdto.getSpayeracct(), tempList);
					}else
					{
						tempList = tempMap.get(pdto.getSpayeracct());
						tempList.add(dto);
						tempMap.put(pdto.getSpayeracct(), tempList);
					}
				}else if(dto.getMainDto() instanceof TvPayoutbackmsgMainDto)
				{
					TvPayoutbackmsgMainDto bdto = (TvPayoutbackmsgMainDto)dto.getMainDto();
					if(tempMap.get(bdto.getSpayeracct())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(bdto.getSpayeracct(), tempList);
					}else
					{
						tempList = tempMap.get(bdto.getSpayeracct());
						tempList.add(dto);
						tempMap.put(bdto.getSpayeracct(), tempList);
					}
				}else if(dto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto)
				{
					TvVoucherinfoAllocateIncomeDto adto = (TvVoucherinfoAllocateIncomeDto)dto.getMainDto();
					if(tempMap.get(adto.getSpayacctno())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(adto.getSpayacctno(), tempList);
					}else
					{
						tempList = tempMap.get(adto.getSpayacctno());
						tempList.add(dto);
						tempMap.put(adto.getSpayacctno(), tempList);
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
		if(key==null||"null".equals(key)||"NULL".equals(key))
			key="";
		return key;
	}
	public static Map tranfor(List<TrTaxorgPayoutReportDto> list,TvVoucherinfoDto vDto) throws ITFEBizException {
		try{
			//ȡ�ù�������
			String treName = BusinessFacade.findTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()) == null ? "" : BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()).getStrename();
			HashMap<String, Object> returnmap = new HashMap<String, Object>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
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
				Detailmap.put("ExpFuncCode", dto.getSbudgetsubcode()); // ��������
				Detailmap.put("ExpFuncName", dto.getSbudgetsubname()); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("YearAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
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
	private void setIdtoToMap(Map vouchermap,IDto idto)
	{
		if(vouchermap!=null&&idto!=null)
		{
			if(vouchermap.get("ClearBankCode")==null)
				vouchermap.put("ClearBankCode","");
			if(vouchermap.get("ClearBankName")==null)
				vouchermap.put("ClearBankName","");
			if(idto instanceof TvPayoutmsgmainDto)
			{
				TvPayoutmsgmainDto pdto = (TvPayoutmsgmainDto)idto;
				if(vouchermap.get("ClearBankCode")==null||"".equals(vouchermap.get("ClearBankCode")))
				{
					if(!"".equals(getString(pdto.getSclearbankcode())))
						vouchermap.put("ClearBankCode",pdto.getSclearbankcode());//�������б���
				}
				if(vouchermap.get("ClearBankName")==null||"".equals(vouchermap.get("ClearBankName")))
				{
					if(!"".equals(getString(pdto.getSclearbankname())))
						vouchermap.put("ClearBankName",pdto.getSclearbankname());//������������
				}
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayeracct())))
						vouchermap.put("ClearAccNo",pdto.getSpayeracct());//�����˺�
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayername())))
						vouchermap.put("ClearAccNanme",pdto.getSpayername());//�����˻�����
				}
			}else if(idto instanceof TvPayoutbackmsgMainDto)
			{
				TvPayoutbackmsgMainDto pdto = (TvPayoutbackmsgMainDto)idto;
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayeracct())))
						vouchermap.put("ClearAccNo",pdto.getSpayeracct());//�����˺�
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayername())))
						vouchermap.put("ClearAccNanme",pdto.getSpayername());//�����˻�����
				}
			}else if(idto instanceof TvVoucherinfoAllocateIncomeDto)
			{
				TvVoucherinfoAllocateIncomeDto pdto = (TvVoucherinfoAllocateIncomeDto)idto;
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayacctno())))
						vouchermap.put("ClearAccNo",pdto.getSpayacctno());//�����˺�
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayacctname())))
						vouchermap.put("ClearAccNanme",pdto.getSpayacctname());//�����˻�����
				}
			}
		}
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
			if(dto.getMainDto() instanceof TvPayoutmsgmainDto){
				if(map.get(((TvPayoutmsgmainDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayoutmsgmainDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvPayoutmsgmainDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}
//			else if(dto.getMainDto() instanceof TvPayoutbackmsgMainDto){
//				if(map.get(((TvPayoutbackmsgMainDto)dto.getMainDto()).getSfundtypecode())==null){
//					List<IDto> data = new ArrayList<IDto>();
//					data.add(dto);
//					map.put(((TvPayoutbackmsgMainDto)dto.getMainDto()).getSfundtypecode(), data);
//				}else{
//					List<IDto> data = map.get(((TvPayoutbackmsgMainDto)dto.getMainDto()).getSfundtypecode());
//					data.add(dto);
//				}
//			}else if(dto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto){
//				if(map.get(((TvVoucherinfoAllocateIncomeDto)dto.getMainDto()).getSfundtypecode())==null){
//					List<IDto> data = new ArrayList<IDto>();
//					data.add(dto);
//					map.put(((TvVoucherinfoAllocateIncomeDto)dto.getMainDto()).getSfundtypecode(), data);
//				}else{
//					List<IDto> data = map.get(((TvVoucherinfoAllocateIncomeDto)dto.getMainDto()).getSfundtypecode());
//					data.add(dto);
//				}
//			}
			
		}
		return map;
	}
}
