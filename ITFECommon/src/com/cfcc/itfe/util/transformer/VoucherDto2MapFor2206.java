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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ֱ��֧���ձ���2206ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor2206 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2206.class);
											
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
							dto.setSattach(danhao);//���ʵ���
							dto.setShold1(vDto.getShold1());//������
							dto.setShold2(String.valueOf(i+1));//�������
							dto.setSstyear(dto.getScreatdate().substring(0, 4));				
							dto.setScheckdate(vDto.getScheckdate());
							dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
							dto.setShold3(vDto.getShold3());
							dto.setShold4(vDto.getShold4());
							dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
							dto.setSdemo("����ɹ�");
							dto.setSvoucherflag("1");
							dto.setSvoucherno(mainvou);	
							dto.setIcount(tempList.size());
							dto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
							mapList.add(vDto);
							mapList.add(cDto);
							mapList.add(tempList);
							Map map=tranfor(mapList,sendList.size(),i+1,danhao);
							dto.setNmoney(vDto.getNmoney());
							dto.setIcount(vDto.getIcount());
							List vouList=new ArrayList();
							vouList.add(map);
							vouList.add(dto);
							returnList.add(vouList);
						}
					}
				}
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
	private Map tranfor(List mapList,int count,int xuhao,String danhao) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			List<IDto> detailList=(List<IDto>) mapList.get(2);
			MainAndSubDto mdto = (MainAndSubDto)detailList.get(0);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("Id",vDto.getSdealno());//������Ȩ֧���ձ���Id
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//������������
			vouchermap.put("StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
			vouchermap.put("VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
			vouchermap.put("VouDate",vDto.getScreatdate());//ƾ֤����
			vouchermap.put("VoucherNo",vDto.getSvoucherno());//ƾ֤��
			BigDecimal allamt=new BigDecimal("0.00");
			BigDecimal returnamt=new BigDecimal("0.00");
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			for(IDto idto:detailList)
			{
				mdto = (MainAndSubDto)idto;
				if(mdto.getMainDto() instanceof TfDirectpaymsgmainDto)
				{
					TfDirectpaymsgmainDto gdto = (TfDirectpaymsgmainDto)mdto.getMainDto();
					if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TfDirectpaymsgsubDto gsdto = (TfDirectpaymsgsubDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							if(vouchermap.get("FundTypeCode")==null)
								vouchermap.put("FundTypeCode",gdto.getSfundtypecode()==null?"null":gdto.getSfundtypecode());//�ʽ����ʱ���
							if(vouchermap.get("FundTypeName")==null)
								vouchermap.put("FundTypeName",gdto.getSfundtypename()==null?"null":gdto.getSfundtypename());//�ʽ���������
							if(vouchermap.get("PayTypeCode")==null)
								vouchermap.put("PayTypeCode",gdto.getSpaytypecode()==null?"null":gdto.getSpaytypecode());//֧����ʽ����
							if(vouchermap.get("PayTypeName")==null)
								vouchermap.put("PayTypeName",gdto.getSpaytypename()==null?"null":gdto.getSpaytypename());//֧����ʽ����
							if(vouchermap.get("PayBankCode")==null)
								vouchermap.put("PayBankCode",gdto.getSpaybankcode()==null?"null":gdto.getSpaybankcode());//�������б���
							if(vouchermap.get("PayBankName")==null)
								vouchermap.put("PayBankName",gdto.getSpaybankname()==null?"null":gdto.getSpaybankname());//������������
							
							Detailmap.put("Id",vDto.getSvoucherno()+id);//֧����ϸId
							Detailmap.put("VoucherBillId",gsdto.getSvoucherbillid());//������Ȩ֧��ƾ֤Id
							Detailmap.put("DayBillNo",gsdto.getSvoucherbillno());//������Ȩ֧���ձ�����
							Detailmap.put("VoucherNo",gsdto.getSvoucherno());//֧���������
							Detailmap.put("BgtTypeCode",gsdto.getSbgttypecode());//Ԥ�����ͱ���
							Detailmap.put("BgtTypeName",gsdto.getSbgttypename());//Ԥ����������
							Detailmap.put("FundTypeCode",gsdto.getSfundtypecode());//�ʽ����ʱ���
							Detailmap.put("FundTypeName",gsdto.getSfundtypename());//�ʽ���������
							Detailmap.put("PayTypeCode",gdto.getSpaytypecode());//֧����ʽ����
							Detailmap.put("PayTypeName",gdto.getSpaytypename());//֧����ʽ����
							Detailmap.put("SetModeCode",gdto.getSsetmodecode());//���㷽ʽ����
							Detailmap.put("SetModeName",gdto.getSsetmodename());//���㷽ʽ����
							Detailmap.put("AgencyCode",gsdto.getSagencycode());//����Ԥ�㵥λ����
							Detailmap.put("AgencyName",gsdto.getSagencyname());//����Ԥ�㵥λ����
							Detailmap.put("ExpFuncCode",gsdto.getSexpfunccode());//֧�����ܷ����Ŀ����
							Detailmap.put("ExpFuncName",gsdto.getSexpfuncname());//֧�����ܷ����Ŀ����
							Detailmap.put("ExpEcoCode",gsdto.getSexpecocode());//֧�����÷����Ŀ����
							Detailmap.put("ExpEcoName",gsdto.getSexpeconame());//֧�����÷����Ŀ����
							Detailmap.put("ProCatCode",gsdto.getSprocatcode());//��֧�������
							Detailmap.put("ProCatName",gsdto.getSprocatname());//��֧��������
							Detailmap.put("DepProCode",gsdto.getSdepprocode());//Ԥ����Ŀ����
							Detailmap.put("DepProName",gsdto.getSdepproname());//Ԥ����Ŀ����
							Detailmap.put("CheckNo",gdto.getScheckno());//֧Ʊ�ţ�����ţ�
							Detailmap.put("PayeeAcctNo",gsdto.getSpayeeacctno());//�տ����˺�
							Detailmap.put("PayeeAcctName",gsdto.getSpayeeacctname());//�տ�������
							Detailmap.put("PayeeAcctBankName",gsdto.getSpayeeacctbankname());//�տ�������
							Detailmap.put("PayAcctNo",gdto.getSpayacctno());//�������˺�
							Detailmap.put("PayAcctName",gdto.getSpayacctname());//����������
							Detailmap.put("PayAcctBankName",gdto.getSpayacctbankname());//����������
							Detailmap.put("PayBankCode",gdto.getSpaybankcode());//�������б���
							Detailmap.put("PayBankName",gdto.getSpaybankname());//������������
							Detailmap.put("PayDate",gsdto.getSxpaydate());//֧������
							Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//֧�����
							Detailmap.put("PaySummaryCode",gdto.getSpaysummarycode());//��;����
							Detailmap.put("PaySummaryName",gdto.getSpaysummaryname());//��;����
							Detailmap.put("PayMgrCode",gsdto.getSpaykindcode());//֧���������ͱ���
							Detailmap.put("PayMgrName",gsdto.getSpaykindname());//֧��������������
							Detailmap.put("Remark",gsdto.getSremark());//��ע
							Detailmap.put("Hold1","");//Ԥ���ֶ�1
							Detailmap.put("Hold2","");//Ԥ���ֶ�2
							Detailmap.put("Hold3","");//Ԥ���ֶ�3
							Detailmap.put("Hold4","");//Ԥ���ֶ�4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSpayeeacctbankno());
							allamt=allamt.add(gsdto.getNpayamt());
							Detail.add(Detailmap);
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
							if(vouchermap.get("FundTypeCode")==null)
								vouchermap.put("FundTypeCode",gdto.getSfundtypecode()==null?"null":gdto.getSfundtypecode());//�ʽ����ʱ���
							if(vouchermap.get("FundTypeName")==null)
								vouchermap.put("FundTypeName",gdto.getSfundtypename()==null?"null":gdto.getSfundtypename());//�ʽ���������
							if(vouchermap.get("PayTypeCode")==null)
								vouchermap.put("PayTypeCode",gdto.getSpaytypecode()==null?"null":gdto.getSpaytypecode());//֧����ʽ����
							if(vouchermap.get("PayTypeName")==null)
								vouchermap.put("PayTypeName",gdto.getSpaytypename()==null?"null":gdto.getSpaytypename());//֧����ʽ����
							if(vouchermap.get("PayBankCode")==null)
								vouchermap.put("PayBankCode",gdto.getSpaybankcode()==null?"null":gdto.getSpaybankcode());//�������б���
							if(vouchermap.get("PayBankName")==null)
								vouchermap.put("PayBankName",gdto.getSpaybankname()==null?"null":gdto.getSpaybankname());//������������
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//֧����ϸId
							Detailmap.put("VoucherBillId",gsdto.getSvoucherbillid());//������Ȩ֧��ƾ֤Id
							Detailmap.put("DayBillNo",gsdto.getSvoucherbillno());//������Ȩ֧���ձ�����
							Detailmap.put("VoucherNo",gsdto.getSvoucherno());//֧���������
							Detailmap.put("BgtTypeCode",gsdto.getSbgttypecode());//Ԥ�����ͱ���
							Detailmap.put("BgtTypeName",gsdto.getSbgttypename());//Ԥ����������
							Detailmap.put("FundTypeCode",gsdto.getSfundtypecode());//�ʽ����ʱ���
							Detailmap.put("FundTypeName",gsdto.getSfundtypename());//�ʽ���������
							Detailmap.put("PayTypeCode",gdto.getSpaytypecode());//֧����ʽ����
							Detailmap.put("PayTypeName",gdto.getSpaytypename());//֧����ʽ����
							Detailmap.put("SetModeCode",gdto.getSsetmodecode());//���㷽ʽ����
							Detailmap.put("SetModeName",gdto.getSsetmodename());//���㷽ʽ����
							Detailmap.put("AgencyCode",gsdto.getSagencycode());//����Ԥ�㵥λ����
							Detailmap.put("AgencyName",gsdto.getSagencyname());//����Ԥ�㵥λ����
							Detailmap.put("ExpFuncCode",gsdto.getSexpfunccode());//֧�����ܷ����Ŀ����
							Detailmap.put("ExpFuncName",gsdto.getSexpfuncname());//֧�����ܷ����Ŀ����
							Detailmap.put("ExpEcoCode",gsdto.getSexpecocode());//֧�����÷����Ŀ����
							Detailmap.put("ExpEcoName",gsdto.getSexpeconame());//֧�����÷����Ŀ����
							Detailmap.put("ProCatCode",gsdto.getSprocatcode());//��֧�������
							Detailmap.put("ProCatName",gsdto.getSprocatname());//��֧��������
							Detailmap.put("DepProCode",gsdto.getSdepprocode());//Ԥ����Ŀ����
							Detailmap.put("DepProName",gsdto.getSdepproname());//Ԥ����Ŀ����
							Detailmap.put("CheckNo",gdto.getScheckno());//֧Ʊ�ţ�����ţ�
							Detailmap.put("PayeeAcctNo",gsdto.getSpayeeacctno());//�տ����˺�
							Detailmap.put("PayeeAcctName",gsdto.getSpayeeacctname());//�տ�������
							Detailmap.put("PayeeAcctBankName",gsdto.getSpayeeacctbankname());//�տ�������
							Detailmap.put("PayAcctNo",gdto.getSpayacctno());//�������˺�
							Detailmap.put("PayAcctName",gdto.getSpayacctname());//����������
							Detailmap.put("PayAcctBankName",gdto.getSpayacctbankname());//����������
							Detailmap.put("PayBankCode",gdto.getSpaybankcode());//�������б���
							Detailmap.put("PayBankName",gdto.getSpaybankname());//������������
							Detailmap.put("PayDate",gsdto.getSxpaydate());//֧������
							Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//֧�����
							Detailmap.put("PaySummaryCode",gdto.getSpaysummarycode());//��;����
							Detailmap.put("PaySummaryName",gdto.getSpaysummaryname());//��;����
							Detailmap.put("PayMgrCode",gsdto.getSpaykindcode());//֧���������ͱ���
							Detailmap.put("PayMgrName",gsdto.getSpaykindname());//֧��������������
							Detailmap.put("Remark",gsdto.getSremark());//��ע
							Detailmap.put("Hold1","");//Ԥ���ֶ�1
							Detailmap.put("Hold2","");//Ԥ���ֶ�2
							Detailmap.put("Hold3","");//Ԥ���ֶ�3
							Detailmap.put("Hold4","");//Ԥ���ֶ�4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSpayeeacctbankno());
							allamt=allamt.add(gsdto.getNpayamt());
							returnamt = returnamt.add(gsdto.getNpayamt());
							Detail.add(Detailmap);
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
								Detailmap.put("Id",vDto.getSvoucherno()+id);//֧����ϸId
								Detailmap.put("VoucherBillId",gsdto.getSvoucherbillid());//������Ȩ֧��ƾ֤Id
								Detailmap.put("DayBillNo","null");//������Ȩ֧���ձ�����
								Detailmap.put("VoucherNo","null");//֧���������
								Detailmap.put("BgtTypeCode","null");//Ԥ�����ͱ���
								Detailmap.put("BgtTypeName","null");//Ԥ����������
								Detailmap.put("FundTypeCode","null");//�ʽ����ʱ���
								Detailmap.put("FundTypeName","null");//�ʽ���������
								Detailmap.put("PayTypeCode",gdto.getSpaytypecode());//֧����ʽ����
								Detailmap.put("PayTypeName",gdto.getSpaytypename());//֧����ʽ����
								Detailmap.put("SetModeCode","null");//���㷽ʽ����
								Detailmap.put("SetModeName","null");//���㷽ʽ����
								Detailmap.put("AgencyCode","null");//����Ԥ�㵥λ����
								Detailmap.put("AgencyName","null");//����Ԥ�㵥λ����
								Detailmap.put("ExpFuncCode","null");//֧�����ܷ����Ŀ����
								Detailmap.put("ExpFuncName","null");//֧�����ܷ����Ŀ����
								Detailmap.put("ExpEcoCode","null");//֧�����÷����Ŀ����
								Detailmap.put("ExpEcoName","null");//֧�����÷����Ŀ����
								Detailmap.put("ProCatCode","null");//��֧�������
								Detailmap.put("ProCatName","null");//��֧��������
								Detailmap.put("DepProCode","null");//Ԥ����Ŀ����
								Detailmap.put("DepProName","null");//Ԥ����Ŀ����
								Detailmap.put("CheckNo","null");//֧Ʊ�ţ�����ţ�
								Detailmap.put("PayeeAcctNo",gsdto.getSpayeeacctno());//�տ����˺�
								Detailmap.put("PayeeAcctName",gsdto.getSpayeeacctname());//�տ�������
								Detailmap.put("PayeeAcctBankName",gsdto.getSpayeeacctbankname());//�տ�������
								Detailmap.put("PayAcctNo","null");//�������˺�
								Detailmap.put("PayAcctName","null");//����������
								Detailmap.put("PayAcctBankName","null");//����������
								Detailmap.put("PayBankCode","null");//�������б���
								Detailmap.put("PayBankName","null");//������������
								Detailmap.put("PayDate","null");//֧������
								Detailmap.put("PayAmt","null");//֧�����
								Detailmap.put("PaySummaryCode","null");//��;����
								Detailmap.put("PaySummaryName","null");//��;����
								Detailmap.put("PayMgrCode","null");//֧���������ͱ���
								Detailmap.put("PayMgrName","null");//֧��������������
								Detailmap.put("Remark",gsdto.getSremark());//��ע
								Detailmap.put("Hold1","");//Ԥ���ֶ�1
								Detailmap.put("Hold2","");//Ԥ���ֶ�2
								Detailmap.put("Hold3","");//Ԥ���ֶ�3
								Detailmap.put("Hold4","");//Ԥ���ֶ�4
								if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
									vDto.setSpaybankcode(gdto.getSpaysndbnkno());
								returnamt=returnamt.add(gsdto.getNpayamt().abs());
								Detail.add(Detailmap);
							}
						}
					}
				}
			}
			vDto.setIcount(id);
			vDto.setNmoney(allamt.compareTo(returnamt)>0?allamt:returnamt);
			vouchermap.put("PayAmt",String.valueOf(allamt));//����֧�����
			vouchermap.put("PosAmt","");//����֧����
			vouchermap.put("NegAmt",String.valueOf(returnamt));//�����˿���
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
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0)
			{
				if(vDto.getShold2()==null||"".equals(vDto.getShold1())||"1".equals(vDto.getShold2()))
				{
					//����ֱ��֧��ƾ֤5201����
					sql = new StringBuffer("SELECT * FROM HTF_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//��ѯ���л����Ѿ��ص�������
					sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5201+"' ");
					sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
					sql.append("AND S_CONFIRUSERCODE ='"+vDto.getScheckdate()+"')");
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
				}
				if(vDto.getShold2()==null||"".equals(vDto.getShold1())||"2".equals(vDto.getShold2()))
				{
					sql = new StringBuffer("SELECT * FROM HTF_PAYBANK_REFUNDMAIN WHERE I_VOUSRLNO in(");//�տ������˿�֪ͨ2252������ʷ��
					sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_2252+"' ");
					sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
					sql.append("AND S_CONFIRUSERCODE ='"+vDto.getScheckdate()+"')");
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
						Map<String,MainAndSubDto> map5201 = get5201Map(StringUtil.replace(sql.toString(), "HTF_PAYBANK_REFUNDMAIN", "HTF_DIRECTPAYMSGMAIN"),StringUtil.replace(subsql,"HTF_PAYBANK_REFUNDSUB","HTF_DIRECTPAYMSGSUB"),execDetail);
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
	private List<List<MainAndSubDto>> getSplitPack(List<IDto> dataList,boolean ispackage)
	{
		List<List<MainAndSubDto>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,List<MainAndSubDto>> tempMap = new HashMap<String,List<MainAndSubDto>>();
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
				}else if(dto.getMainDto() instanceof TfDirectpaymsgmainDto)
				{
					fdto = (TfDirectpaymsgmainDto)dto.getMainDto();
					if(tempMap.get("5201"+fdto.getSfundtypecode()+fdto.getSpaytypecode())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put("5201"+fdto.getSfundtypecode()+fdto.getSpaytypecode(), tempList);
					}else
					{
						tempList = tempMap.get("5201"+fdto.getSfundtypecode()+fdto.getSpaytypecode());
						tempList.add(dto);
						tempMap.put("5201"+fdto.getSfundtypecode()+fdto.getSpaytypecode(),tempList);
					}
				}
				else if(dto.getMainDto() instanceof TfPaybankRefundmainDto)
				{
					if(dto.getExtdto()!=null&&dto.getExtdto().getSubDtoList()!=null)
					{
						fdto = (TfDirectpaymsgmainDto)dto.getExtdto().getMainDto();
						if(tempMap.get("2252"+fdto.getSfundtypecode()+fdto.getSpaytypecode())==null)
						{
							tempList = new ArrayList<MainAndSubDto>();
							tempList.add(dto);
							tempMap.put("2252"+fdto.getSfundtypecode()+fdto.getSpaytypecode(), tempList);
						}else
						{
							tempList = tempMap.get("2252"+fdto.getSfundtypecode()+fdto.getSpaytypecode());
							tempList.add(dto);
							tempMap.put("2252"+fdto.getSfundtypecode()+fdto.getSpaytypecode(),tempList);
						}
					}else
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
}
