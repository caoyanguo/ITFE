package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class Voucher2252MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher2252MsgServer.class);
	private Voucher voucher;

	/**
	 * ���з������е��տ������˿�֪ͨƾ֤2252
	 * @author �Ż��
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		String ls_FinOrgCode = (String) muleMessage.getProperty("finOrgCode");// ��������
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("�����տ������˿�֪ͨƾ֤2252���ĳ��ִ���", e);
			throw new ITFEBizException("�����տ������˿�֪ͨƾ֤2252���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode = "";// ������������
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����

		// ��ȡ�����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		TfPaybankRefundmainDto maindto = null;
		List<IDto> subDtoList =null;
		List lists = new ArrayList();
		List list = null;
		//�������list
		ArrayList<BigDecimal> moneyList = null; 
		//����list
		ArrayList<BigDecimal> zeroList = null; 
		
		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// ��������
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				String mainFailReason = null;
				// ��ϸ�ϼƽ��
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// ����ͷ�뱨����ƾ֤��Ų�һ�µ���ǩ���쳣
				String VoucherNo = element.attribute("VoucherNo").getText();//ƾ֤���,�տ������˿�֪ͨ���ţ�����3λ���б���||YYYYMMDD||4λ���кš�����
				// Voucher
				Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				//����У����Ϣdto
				 VoucherVerifyDto verifydto = new VoucherVerifyDto();
				 VoucherVerify voucherVerify = new VoucherVerify();	
				/**
				 * ƾ֤��Ϣ
				 */
//				String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");// ������Ϣ
				String Id = elementVoucher.elementText("Id");// �տ������˿�֪ͨId
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String OriBillNo = elementVoucher.elementText("OriBillNo");// ԭҵ�񵥾ݺ�,ԭ����ҵ��֧����ϸ������ƾ֤��
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); // ֧����ʽ����  11-ֱ��֧����12-��Ȩ֧����91-ʵ��
				String PayTypeName = elementVoucher.elementText("PayTypeName");// ֧����ʽ����
				String OriginalVoucherNo = elementVoucher.elementText("OriginalVoucherNo");// ��֧��ƾ֤���,ֱ��֧������Ȩ֧��ƾ֤���
				String PayAmt = elementVoucher.elementText("PayAmt");// �˿���ܽ��,�����
				String verifyPayAmt = PayAmt;
				String payDictateNo = elementVoucher.elementText("PayDictateNo");// ֧���������,����ͨ���ִ���֧��ϵͳ�㻮�ʽ�ʱ����
				String payMsgNo = elementVoucher.elementText("PayMsgNo");// ֧�����ı��,���֧��ϵͳΪ100��С��֧��ϵͳΪ001
				String payEntrustDate = elementVoucher.elementText("PayEntrustDate");// ֧��ί������
				String paySndBnkNo = elementVoucher.elementText("PaySndBnkNo");// ֧���������к�
				String Remark = elementVoucher.elementText("Remark");// ժҪ����Ա������Ҫ¼��
				String PayDate = elementVoucher.elementText("PayDate");//ʵ���˿�����
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2
				/**
				 * 20150423�Ϻ������ֶ�
				 * FundTypeCode	�ʽ����ʱ���  S_EXT2
				 * FundTypeName	�ʽ���������	 S_EXT3
				 */
				String FundTypeCode = elementVoucher.elementText("FundTypeCode");
				String FundTypeName = elementVoucher.elementText("FundTypeName");
				
				/**
				 * ��װTfPaybankRefundmainDto����
				 **/
				maindto = new TfPaybankRefundmainDto();
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				maindto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
				maindto.setSorgcode(ls_OrgCode);//�����������
				maindto.setStrecode("");//�ӿڹ淶��û����Ӧ�Ĺ������,����Ĭ��Ϊ���ַ���,��������������Ĺ���Ϊ�����¸�ֵ
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬ ������
				maindto.setSdemo("");//����
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳʱ��
				maindto.setSid(Id);// �տ������˿�֪ͨId
				maindto.setSadmdivcode(AdmDivCode);//������������
				maindto.setSstyear(StYear);// ҵ�����
				maindto.setSvtcode(VtCode);//ƾ֤���ͱ��
				maindto.setSvoudate(VouDate);// ƾ֤����
				maindto.setSvoucherno(VoucherNo);//ƾ֤���,�տ������˿�֪ͨ���ţ�����3λ���б���||YYYYMMDD||4λ���кš�����
				maindto.setSoribillno(OriBillNo);//ԭҵ�񵥾ݺ�,ԭ����ҵ��֧����ϸ������ƾ֤��
				maindto.setSpaytypecode(PayTypeCode);// ֧����ʽ���롡11-ֱ��֧����12-��Ȩ֧����91-ʵ��
				if(StringUtils.isNotBlank(PayTypeCode)&&PayTypeCode.equals(StateConstant.DIRECT_PAY_CODE)){
					maindto.setSpaytypecode(MsgConstant.directPay);				
				}				
				maindto.setSpaytypename(PayTypeName);// ֧����ʽ����
				maindto.setSoriginalvoucherno(OriginalVoucherNo);// ��֧��ƾ֤���,ֱ��֧������Ȩ֧��ƾ֤���
				maindto.setNpayamt(new BigDecimal(PayAmt).abs());// �˿���ܽ��,�����
				maindto.setSpaydictateno(payDictateNo);// ���֧���˿�����
				maindto.setSpaymsgno(payMsgNo);// ֧�����ı��
				maindto.setSpayentrustdate(payEntrustDate);// ֧��ί������
				maindto.setSpaysndbnkno(paySndBnkNo);// ֧���������к�
				maindto.setSremark(Remark);// ժҪ����Ա������Ҫ¼��
				maindto.setSpaydate(PayDate);//ʵ���˿�����
				maindto.setSext1(listDetail.size()+"");
				maindto.setShold1(Hold1);// Ԥ���ֶ�1
				maindto.setShold2(Hold2);// Ԥ���ֶ�2
				maindto.setSext2(FundTypeCode);
				maindto.setSext3(FundTypeName);
				
				if(StringUtils.isBlank(FundTypeCode) || StringUtils.isBlank(FundTypeName) || StringUtils.isBlank(Remark)){
					mainFailReason = "ƾ֤���:" + VoucherNo + "������FundTypeCode��FundTypeName��RemarkΪ�գ�";
				}
				
				
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				vDto.setShold2(maindto.getSpaytypecode());//���ֶγ䵱֧����ʽ
				/**
				 * ��������ʧ��ԭ��Ϊ�յļ�¼,У��
				 */
				if(StringUtils.isNotBlank(mainFailReason)){
					voucher.voucherComfail(vDto.getSdealno(), mainFailReason);
					continue;
				}
				
				
				
				
				
				
				
				//�����list
				moneyList = new ArrayList<BigDecimal>();
				
				//����list @author �Ż��
				zeroList = new ArrayList<BigDecimal>();
				
				//�˿�ԭ��У��@���޹�
				String  bkReMsg = null ;
				
				//�ӱ���ϸId����
				List<String>  subDtoIdList = new ArrayList<String>();
				
				/**
				 * ��װTfPaybankRefundsubDto����
				 */
				subDtoList = new ArrayList<IDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					String subId = elementDetail.elementText("Id");// �˿���ϸId,ԭ����ҵ��֧����ϸ����ϸId
					String VoucherBillId = elementDetail.elementText("VoucherBillId");// �տ������˿�֪ͨId
					String PayAcctNo = elementDetail.elementText("PayAcctNo");// ԭ�������˺�
					String PayAcctName = elementDetail.elementText("PayAcctName");// ԭ����������
					String PayAcctBankName = elementDetail.elementText("PayAcctBankName");// ԭ����������
					String PayeeAcctNo = elementDetail.elementText("PayeeAcctNo");// ԭ�տ����˺�
					String PayeeAcctName = elementDetail.elementText("PayeeAcctName");//ԭ�տ�������
					String PayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");// ԭ�տ�����������
					String PayeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");// ԭ�տ��������к�
					String sPayAmt = elementDetail.elementText("PayAmt");// �˿���
					String subRemark = elementDetail.elementText("Remark");// �˿�ԭ��
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4
					//�Ϻ����������ֶ�
					String expFuncCode = elementDetail.elementText("ExpFuncCode");//���ܷ����Ŀ����S_EXT1
					String expFuncName = elementDetail.elementText("ExpFuncName");//���ܷ����Ŀ����S_EXT2
					String bkyt = elementDetail.elementText("bkyt");// ������;	S_EXT3
					TfPaybankRefundsubDto subdto = new TfPaybankRefundsubDto();
					subdto.setSext1(expFuncCode);
					subdto.setSext2(expFuncName);
					subdto.setSext3(bkyt);
					subdto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
					subdto.setIseqno(Long.valueOf(j+1));//��ϸ��
					subdto.setSid(subId);// �˿���ϸId,ԭ����ҵ��֧����ϸ����ϸId
					subdto.setSvoucherbillid(VoucherBillId);// �տ������˿�֪ͨId
					subdto.setSpayacctno(PayAcctNo);// ԭ�������˺�
					subdto.setSpayacctname(PayAcctName);// ԭ����������
					subdto.setSpayacctbankname(PayAcctBankName);// ԭ����������
					subdto.setSpayeeacctno(PayeeAcctNo);// ԭ�տ����˺�
					subdto.setSpayeeacctname(PayeeAcctName);//ԭ�տ�������
					subdto.setSpayeeacctbankno(PayeeAcctBankNo);// ԭ�տ��������к�
					subdto.setSpayeeacctbankname(PayeeAcctBankName);// ԭ�տ�����������
					BigDecimal subPayAmt = new BigDecimal(sPayAmt);
					
					//������б�
					if(subPayAmt.signum()==1){
						moneyList.add(subPayAmt);
					}
					//�����б� @author �Ż��
					if(subPayAmt.equals(BigDecimal.ZERO)){
						zeroList.add(subPayAmt);
					}
					//�˿�ԭ��M�ж�
					if (null ==subRemark||subRemark.trim().length()==0) {
						bkReMsg="ƾ֤���:" + VoucherNo +",��ϸID:" + subId + "��Remark����Ϊ�գ�";
						break;
					}
					//������;M�ж�
					if(StringUtils.isBlank(bkyt)){
						bkReMsg="ƾ֤���:" + VoucherNo +",��ϸID:" + subId + "��bkyt����Ϊ�գ�";
						break;
					}
					
					subdto.setNpayamt(subPayAmt.abs());// �˿���
					subdto.setSremark(subRemark);// �˿�ԭ��
					subdto.setShold1(sdetailHold1);// Ԥ���ֶ�1
					subdto.setShold2(sdetailHold2);// Ԥ���ֶ�2
					subdto.setShold3(sdetailHold3);// Ԥ���ֶ�3
					subdto.setShold4(sdetailHold4);// Ԥ���ֶ�4
					
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(sPayAmt)));
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());
				}
				/**
				 * ��ϸ����ʧ��ԭ��Ϊ�յļ�¼,У��
				 */
				if(bkReMsg != null && bkReMsg.length()>0){
					voucher.voucherComfail(vDto.getSdealno(), bkReMsg);
					continue;
				}
				
				
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setStrecode(vDto.getStrecode());
				maindto.setSadmdivcode(vDto.getSadmdivcode());
				maindto.setSfinorgcode(ls_FinOrgCode);//������������
				maindto.setSdealno(mainvou.substring(8, 16));//������ˮ��
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());//ί������
				maindto.setSfilename(ls_FileName);//�ļ���
				maindto.setSpackageno("");
				maindto.setSdemo(vDto.getSattach());
				maindto.setSbackflag("0");
				maindto.setSext1(subDtoList.size()+"");//��ϸ�˻ر���
				/**
				 * ��װverifydto,���б���У��
				 */
				verifydto.setTrecode(maindto.getStrecode());
				verifydto.setFinorgcode(ls_FinOrgCode);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setPayVoucherNo(OriBillNo);
				verifydto.setPaybankname(payDictateNo);//�����д������֧���������
				verifydto.setAgentAcctNo(payMsgNo);//���տ������˺Ŵ���֧�����ı��
				verifydto.setAgentAcctName(payEntrustDate);//���տ������˻����ƴ���֧��ί������
				verifydto.setClearAcctNo(paySndBnkNo);//�ø����˺Ŵ���֧���������к�
				verifydto.setClearAcctName(PayDate);//�ø����˺����ƴ���ʵ���˿�����
				//������ȣ��ܽ���У�� by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(verifyPayAmt);
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_2252);
				
				if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				
				
				
				/**
				 *У���ӱ����Ƿ�������Ϻ���
				 *@author �Ż��
				 */
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&zeroList.size() > 0){
					String errMsg="��ϸ��Ϣ�а��������������!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				
				
				/**
				 *У���ӱ����Ƿ��������
				 *
				 */
				if(moneyList.size() > 0){
					String errMsg="��ϸ��Ϣ�а���������������!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				
				
				
				/**
				 *У���������Ƿ����ӱ������
				 * 
				 */
				if (new BigDecimal(PayAmt).compareTo(sumAmt) != 0) {
					String errMsg = "�����������ϸ�ۼƽ���ȣ�������" + PayAmt
							+ " ��ϸ�ۼƽ� " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 *��ϸ�������ܳ���500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "��ϸ��������С��500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				
				/**
				 * У����ϸ��Ϣ�Ϸ���
				 * @author �Ż��
				 */
				if(null!=subDtoList&&subDtoList.size()>0){
					String returnMsg = voucherVerify.checkVerify(subDtoList,MsgConstant.VOUCHER_NO_2252);
					if(StringUtils.isNotBlank(returnMsg)){
						voucher.voucherComfail(vDto.getSdealno(), returnMsg);
						continue;
					}
					returnMsg = voucherVerify.checkVerify(subDtoList,maindto);
					if(StringUtils.isNotBlank(returnMsg)){
						voucher.voucherComfail(vDto.getSdealno(), returnMsg);
						continue;
					}
				}
				
				
				/**
				 * У����ϸId�Ƿ�Ϊ�ջ��ظ�
				 */
				String checkIdMsg=voucherVerify.checkValidSudDtoId(subDtoIdList);
				if(checkIdMsg!=null){
					//���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;				
				}
				
				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				TfPaybankRefundsubDto[] subDtos = new TfPaybankRefundsubDto[subDtoList.size()];
				subDtos = (TfPaybankRefundsubDto[]) subDtoList.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfail(mainvou, "���Ĳ��淶��"+e.getMessage());
				continue;
			}
			// ǩ�ճɹ�
			try {
				voucher.voucherConfirmSuccess(vDto.getSdealno());
			} catch (ITFEBizException e) {
				logger.error(e);
				continue;
			}

			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			list.add(subDtoList);
			lists.add(list);
		}

		/**
		 * У��ƾ֤��Ϣģ��
		 */
		try {
			if(lists.size()>0){
				voucher.voucherVerify(lists, VtCode);
			}
			
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("У��ƾ֤����"+VtCode+"�����쳣",e);
		}
		return;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

}
