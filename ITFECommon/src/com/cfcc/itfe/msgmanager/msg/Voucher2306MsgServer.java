package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;

@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher2306MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher2306MsgServer.class);
	private Voucher voucher;

	/**
	 * �����������е������˿�ƾ֤�ص�
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("�����������˿�2306���ĳ��ִ���", e);
			throw new ITFEBizException("�����������˿�2306���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
//		String AdmDivCode = "";// ������������
//		String StYear = "";// ���
//		String VtCode = "";// ƾ֤����
//		Date currentDate = TimeFacade.getCurrentDateTime();// ��ǰϵͳ����
//		List<String> voucherList = new ArrayList<String>();

		// ��ȡ�����������롢��Ⱥ�ƾ֤����
//		if (VoucherBodyList.size() > 0) {
//			Element element = (Element) VoucherBodyList.get(0);
//			AdmDivCode = element.attribute("AdmDivCode").getText();
//			StYear = element.attribute("StYear").getText();
//			VtCode = element.attribute("VtCode").getText();
//		}

		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		TvPayreckBigdataBackDto maindto = null;
		// ��������
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// ����ͷ�뱨����ƾ֤��Ų�һ�µ���ǩ���쳣�����ģ�
				String VoucherNo = element.attribute("VoucherNo").getText();
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				// Voucher
				Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				//����У����Ϣdto
				/**
				 * ƾ֤��Ϣ
				 */
				String VtCode = elementVoucher.elementText("VtCode");// ������Ϣ
				String S_XPAYSNDBNKNO= elementVoucher.elementText("XPaySndBnkNo");//'֧���������к�';
				String S_XADDWORD= elementVoucher.elementText("XAddWord");//'����';
				String S_XCLEARDATE= elementVoucher.elementText("XClearDate");//'��������';
				String S_HOLD1= elementVoucher.elementText("Hold1");//'Ԥ���ֶ�';
				String S_HOLD2= elementVoucher.elementText("Hold2");//'Ԥ���ֶ�';
				String S_ID= elementVoucher.elementText("Id");//'ƾ֤id';
				String S_VOUDATE= elementVoucher.elementText("VouDate");//'ƾ֤����';
				String S_VOUCHERNO= elementVoucher.elementText("VoucherNo");//'ƾ֤��';
				String S_TRECODE= elementVoucher.elementText("TreCode");//'�������';
				String S_FINORGCODE= elementVoucher.elementText("FinOrgCode");//'�������ش���';
				String S_FUNDTYPECODE= elementVoucher.elementText("FundTypeCode");//'�ʽ����ʱ���';
				String S_FUNDTYPENAME= elementVoucher.elementText("FundTypeName");//'�ʽ���������';
				String S_PAYTYPECODE= elementVoucher.elementText("PayTypeCode");//'֧����ʽ����';
				String S_PAYTYPENAME= elementVoucher.elementText("PayTypeName");//'֧����ʽ����';
				String S_AGENTACCTNO= elementVoucher.elementText("AgentAcctNo");//'�տ������˺�';
				String S_AGENTACCTNAME= elementVoucher.elementText("AgentAcctName");//'�տ������˻�����';
				String S_AGENTACCTBANKNAME= elementVoucher.elementText("AgentAcctBankName");//'�տ���������';
				String S_STYEAR= elementVoucher.elementText("StYear");//'ҵ�����';
				String S_CLEARACCTNAME= elementVoucher.elementText("ClearAcctName");//'�����˻�����';
				String S_CLEARACCTBANKNAME= elementVoucher.elementText("ClearAcctBankName");//'��������';
				String N_PAYAMT= elementVoucher.elementText("PayAmt");//'������';
				if(N_PAYAMT==null||"".equals(N_PAYAMT))
					N_PAYAMT="0.00";
				String S_PAYBANKNAME= elementVoucher.elementText("PayBankName");//'������������';
				String S_PAYBANKNO= elementVoucher.elementText("PayBankNo");//'���������к�';
				String S_REMARK= elementVoucher.elementText("Remark");//'ժҪ';
				String S_MONEYCORPCODE= elementVoucher.elementText("MoneyCorpCode");//'���ڻ�������';
				String S_ADMDIVCODE= elementVoucher.elementText("AdmDivCode");//'������������';
				String S_CLEARACCTNO= elementVoucher.elementText("ClearAcctNo");//'�����˺�';
//				String S_BGTTYPECODE= elementVoucher.elementText("BgtTypeCode");//'Ԥ�����ͱ���';
				String S_BGTTYPENAME= elementVoucher.elementText("BgtTypeName");//'Ԥ����������';
				String N_XPAYAMT= elementVoucher.elementText("XPayamt");//'�ص����';
				if(N_XPAYAMT==null||"".equals(N_XPAYAMT))
					N_XPAYAMT="0.00";
				String S_ALLNUMBER= elementVoucher.elementText("AllNumber");//'�ܱ���';
				String S_ALLPACKET= elementVoucher.elementText("AllPacket");//'�ܰ���';
				String S_NOWADAYPACKET= elementVoucher.elementText("NowadayPacket");//'��ǰ�Ӱ�';

				maindto = new TvPayreckBigdataBackDto();
				maindto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
				maindto.setSvtcode(VtCode);
				maindto.setSxpaysndbnkno(S_XPAYSNDBNKNO);//֧���������к�;
				maindto.setSxaddword(S_XADDWORD);//����;
				maindto.setSxcleardate(S_XCLEARDATE);//��������;
				maindto.setShold1(S_HOLD1);//Ԥ���ֶ�;
				maindto.setShold2(S_HOLD2);//Ԥ���ֶ�;
				maindto.setSid(S_ID);//ƾ֤id;
				maindto.setSvoudate(S_VOUDATE);//ƾ֤����;
				maindto.setSvoucherno(S_VOUCHERNO);//ƾ֤��;
				maindto.setStrecode(S_TRECODE);//�������;
				maindto.setSfinorgcode(S_FINORGCODE);//�������ش���;
				maindto.setSfundtypecode(S_FUNDTYPECODE);// �ʽ����ʱ���;
				maindto.setSfundtypename(S_FUNDTYPENAME);//�ʽ���������;
				maindto.setSpaytypecode(S_PAYTYPECODE);//֧����ʽ����;
				maindto.setSpaytypename(S_PAYTYPENAME);//֧����ʽ����;
				maindto.setSagentacctno(S_AGENTACCTNO);//�տ������˺�;
				maindto.setSagentacctname(S_AGENTACCTNAME);//�տ������˻�����;
				maindto.setSagentacctbankname(S_AGENTACCTBANKNAME);//�տ���������;
				maindto.setSstyear(S_STYEAR);//ҵ�����;
				maindto.setSclearacctname(S_CLEARACCTNAME);//�����˻�����;
				maindto.setSclearacctbankname(S_CLEARACCTBANKNAME);//��������;
				maindto.setNpayamt(BigDecimal.valueOf(Double.valueOf(N_PAYAMT)));//������;
				maindto.setSpaybankname(S_PAYBANKNAME);//������������;
				maindto.setSpaybankno(S_PAYBANKNO);//���������к�;
				maindto.setSremark(S_REMARK);//ժҪ;
				maindto.setSmoneycorpcode(S_MONEYCORPCODE);//���ڻ�������;
				maindto.setSadmdivcode(S_ADMDIVCODE);//������������;
				maindto.setSclearacctno(S_CLEARACCTNO);//�����˺�;
				maindto.setSbgttypecode(MsgConstant.BDG_KIND_IN);//Ԥ������(Ĭ��Ԥ����)
				maindto.setSbgttypename(S_BGTTYPENAME);//Ԥ����������;
				maindto.setNxpayamt(BigDecimal.valueOf(Double.valueOf(N_XPAYAMT)));//�ص����;
				maindto.setSallnumber(S_ALLNUMBER);//�ܱ���;
				maindto.setSallpacket(S_ALLPACKET);//�ܰ���;
				maindto.setSnowadaypacket(S_NOWADAYPACKET);//��ǰ�Ӱ�;
				/**
				 * ��װTvPayreckBankListDto����
				 */
				List<TvPayreckBigdataBackListDto> subDtoList = new ArrayList<TvPayreckBigdataBackListDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					/**
					 * У�鱨����ϸId�ڵ��Ƿ����
					 * 1�����ڵ㲻���ڣ����ϰ汾����������ϸId
					 * 2�����ڵ���ڣ����°汾��������ϸId
					 */
					String sN_PAYAMT = elementDetail.elementText("PayAmt");//֧�����
					if(sN_PAYAMT==null||"".equals(sN_PAYAMT))
						sN_PAYAMT="0.00";
					String S_PAYSUMMARYNAME = elementDetail.elementText("PaySummaryName");//ժҪ����
					String sS_HOLD1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�
					String sS_HOLD2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�
					String S_HOLD3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�
					String S_HOLD4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�
					String sS_VOUCHERNO = elementDetail.elementText("VoucherNo");//֧��ƾ֤����
					String S_SUPDEPCODE = elementDetail.elementText("SupDepCode");//Ԥ�㵥λ����
					String S_SUPDEPNAME = elementDetail.elementText("SupDepName");//Ԥ�㵥λ����
					String S_EXPFUNCCODE = elementDetail.elementText("ExpFuncCode");//���ܷ������
					String S_EXPFUNCNAME = elementDetail.elementText("ExpFuncName");//���ܷ�������
					String S_PAYEEACCOUNT = elementDetail.elementText("PayeeAccount");//�տ����˻�
					String S_PAYEEACCOUNTNAME = elementDetail.elementText("PayeeAccountName");//�տ����˻�����
					String S_PAYEEBANKACCOUNTNUMBER = elementDetail.elementText("PayeeBankAccountNumber");//�տ��˿������к�
					String S_PAYEEBANKNAME = elementDetail.elementText("PayeeBankName");//�տ��˿���������
					String S_USES = elementDetail.elementText("Uses");//��;
					
					TvPayreckBigdataBackListDto subdto = new TvPayreckBigdataBackListDto();
					// �˴�����ֵ��ȷ��
					subdto.setSseqno(Integer.valueOf(j+1));//���
					subdto.setIvousrlno(Long.valueOf(mainvou));// �ӱ����к�
					subdto.setNpayamt(BigDecimal.valueOf(Double.valueOf(sN_PAYAMT)));//֧�����
					subdto.setSpaysummaryname(S_PAYSUMMARYNAME);//ժҪ����
					subdto.setShold1(sS_HOLD1);//Ԥ���ֶ�
					subdto.setShold2(sS_HOLD2);//Ԥ���ֶ�
					subdto.setShold3(S_HOLD3);//Ԥ���ֶ�
					subdto.setShold4(S_HOLD4);//Ԥ���ֶ�
					subdto.setSvoucherno(sS_VOUCHERNO);//֧��ƾ֤����
					subdto.setSsupdepcode(S_SUPDEPCODE);//Ԥ�㵥λ����
					subdto.setSsupdepname(S_SUPDEPNAME);//Ԥ�㵥λ����
					subdto.setSexpfunccode(S_EXPFUNCCODE);//���ܷ������
					subdto.setSexpfuncname(S_EXPFUNCNAME);//���ܷ�������
					subdto.setSpayeeaccount(S_PAYEEACCOUNT);//�տ����˻�
					subdto.setSpayeeaccountname(S_PAYEEACCOUNTNAME);//�տ����˻�����
					subdto.setSpayeebankaccountnumber(S_PAYEEBANKACCOUNTNUMBER);//�տ��˿������к�
					subdto.setSpayeebankname(S_PAYEEBANKNAME);//�տ��˿���������
					subdto.setSuses(S_USES);//��;
					subDtoList.add(subdto);
				}
				
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setScreatdate(vDto.getScreatdate());
				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				DatabaseFacade.getODB().create(maindto);
				if(subDtoList!=null&&subDtoList.size()>1000)
				{
					List<List<TvPayreckBigdataBackListDto>> gelist = getSubLists(subDtoList,1000);
					for(int k=0;k<gelist.size();k++)
					{
						TvPayreckBigdataBackListDto[] subDtos = new TvPayreckBigdataBackListDto[gelist.get(k).size()];
						subDtos = (TvPayreckBigdataBackListDto[]) gelist.get(k).toArray(subDtos);
						DatabaseFacade.getODB().create(subDtos);
					}
				}else
				{
					TvPayreckBigdataBackListDto[] subDtos = new TvPayreckBigdataBackListDto[subDtoList.size()];
					subDtos = (TvPayreckBigdataBackListDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				}
				voucher.voucherConfirmSuccess(vDto.getSdealno());	
			}catch(Exception e){
				logger.error("2306���������쳣:",e);
				voucher.voucherComfail(mainvou, "���Ĳ��淶��"+e.getMessage());
				continue;
			}
		}
		return;
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
	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

}
