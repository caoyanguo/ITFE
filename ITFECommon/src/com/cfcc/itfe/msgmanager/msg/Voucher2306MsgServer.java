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
	 * 财政发往人行的申请退款凭证回单
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
			logger.error("解析大数据退款2306报文出现错误！", e);
			throw new ITFEBizException("解析大数据退款2306报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
//		String AdmDivCode = "";// 行政区划代码
//		String StYear = "";// 年度
//		String VtCode = "";// 凭证类型
//		Date currentDate = TimeFacade.getCurrentDateTime();// 当前系统日期
//		List<String> voucherList = new ArrayList<String>();

		// 获取行政区划代码、年度和凭证类型
//		if (VoucherBodyList.size() > 0) {
//			Element element = (Element) VoucherBodyList.get(0);
//			AdmDivCode = element.attribute("AdmDivCode").getText();
//			StYear = element.attribute("StYear").getText();
//			VtCode = element.attribute("VtCode").getText();
//		}

		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		TvPayreckBigdataBackDto maindto = null;
		// 解析报文
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// 报文头与报文体凭证编号不一致导致签收异常（宁夏）
				String VoucherNo = element.attribute("VoucherNo").getText();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				// Voucher
				Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				//报文校验信息dto
				/**
				 * 凭证信息
				 */
				String VtCode = elementVoucher.elementText("VtCode");// 附加信息
				String S_XPAYSNDBNKNO= elementVoucher.elementText("XPaySndBnkNo");//'支付发起行行号';
				String S_XADDWORD= elementVoucher.elementText("XAddWord");//'附言';
				String S_XCLEARDATE= elementVoucher.elementText("XClearDate");//'清算日期';
				String S_HOLD1= elementVoucher.elementText("Hold1");//'预留字段';
				String S_HOLD2= elementVoucher.elementText("Hold2");//'预留字段';
				String S_ID= elementVoucher.elementText("Id");//'凭证id';
				String S_VOUDATE= elementVoucher.elementText("VouDate");//'凭证日期';
				String S_VOUCHERNO= elementVoucher.elementText("VoucherNo");//'凭证号';
				String S_TRECODE= elementVoucher.elementText("TreCode");//'国库代码';
				String S_FINORGCODE= elementVoucher.elementText("FinOrgCode");//'财政机关代码';
				String S_FUNDTYPECODE= elementVoucher.elementText("FundTypeCode");//'资金性质编码';
				String S_FUNDTYPENAME= elementVoucher.elementText("FundTypeName");//'资金性质名称';
				String S_PAYTYPECODE= elementVoucher.elementText("PayTypeCode");//'支付方式编码';
				String S_PAYTYPENAME= elementVoucher.elementText("PayTypeName");//'支付方式名称';
				String S_AGENTACCTNO= elementVoucher.elementText("AgentAcctNo");//'收款银行账号';
				String S_AGENTACCTNAME= elementVoucher.elementText("AgentAcctName");//'收款银行账户名称';
				String S_AGENTACCTBANKNAME= elementVoucher.elementText("AgentAcctBankName");//'收款银行名称';
				String S_STYEAR= elementVoucher.elementText("StYear");//'业务年度';
				String S_CLEARACCTNAME= elementVoucher.elementText("ClearAcctName");//'付款账户名称';
				String S_CLEARACCTBANKNAME= elementVoucher.elementText("ClearAcctBankName");//'付款银行';
				String N_PAYAMT= elementVoucher.elementText("PayAmt");//'清算金额';
				if(N_PAYAMT==null||"".equals(N_PAYAMT))
					N_PAYAMT="0.00";
				String S_PAYBANKNAME= elementVoucher.elementText("PayBankName");//'代理银行名称';
				String S_PAYBANKNO= elementVoucher.elementText("PayBankNo");//'代理银行行号';
				String S_REMARK= elementVoucher.elementText("Remark");//'摘要';
				String S_MONEYCORPCODE= elementVoucher.elementText("MoneyCorpCode");//'金融机构编码';
				String S_ADMDIVCODE= elementVoucher.elementText("AdmDivCode");//'行政区划编码';
				String S_CLEARACCTNO= elementVoucher.elementText("ClearAcctNo");//'付款账号';
//				String S_BGTTYPECODE= elementVoucher.elementText("BgtTypeCode");//'预算类型编码';
				String S_BGTTYPENAME= elementVoucher.elementText("BgtTypeName");//'预算类型名称';
				String N_XPAYAMT= elementVoucher.elementText("XPayamt");//'回单金额';
				if(N_XPAYAMT==null||"".equals(N_XPAYAMT))
					N_XPAYAMT="0.00";
				String S_ALLNUMBER= elementVoucher.elementText("AllNumber");//'总笔数';
				String S_ALLPACKET= elementVoucher.elementText("AllPacket");//'总包数';
				String S_NOWADAYPACKET= elementVoucher.elementText("NowadayPacket");//'当前子包';

				maindto = new TvPayreckBigdataBackDto();
				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSvtcode(VtCode);
				maindto.setSxpaysndbnkno(S_XPAYSNDBNKNO);//支付发起行行号;
				maindto.setSxaddword(S_XADDWORD);//附言;
				maindto.setSxcleardate(S_XCLEARDATE);//清算日期;
				maindto.setShold1(S_HOLD1);//预留字段;
				maindto.setShold2(S_HOLD2);//预留字段;
				maindto.setSid(S_ID);//凭证id;
				maindto.setSvoudate(S_VOUDATE);//凭证日期;
				maindto.setSvoucherno(S_VOUCHERNO);//凭证号;
				maindto.setStrecode(S_TRECODE);//国库代码;
				maindto.setSfinorgcode(S_FINORGCODE);//财政机关代码;
				maindto.setSfundtypecode(S_FUNDTYPECODE);// 资金性质编码;
				maindto.setSfundtypename(S_FUNDTYPENAME);//资金性质名称;
				maindto.setSpaytypecode(S_PAYTYPECODE);//支付方式编码;
				maindto.setSpaytypename(S_PAYTYPENAME);//支付方式名称;
				maindto.setSagentacctno(S_AGENTACCTNO);//收款银行账号;
				maindto.setSagentacctname(S_AGENTACCTNAME);//收款银行账户名称;
				maindto.setSagentacctbankname(S_AGENTACCTBANKNAME);//收款银行名称;
				maindto.setSstyear(S_STYEAR);//业务年度;
				maindto.setSclearacctname(S_CLEARACCTNAME);//付款账户名称;
				maindto.setSclearacctbankname(S_CLEARACCTBANKNAME);//付款银行;
				maindto.setNpayamt(BigDecimal.valueOf(Double.valueOf(N_PAYAMT)));//清算金额;
				maindto.setSpaybankname(S_PAYBANKNAME);//代理银行名称;
				maindto.setSpaybankno(S_PAYBANKNO);//代理银行行号;
				maindto.setSremark(S_REMARK);//摘要;
				maindto.setSmoneycorpcode(S_MONEYCORPCODE);//金融机构编码;
				maindto.setSadmdivcode(S_ADMDIVCODE);//行政区划编码;
				maindto.setSclearacctno(S_CLEARACCTNO);//付款账号;
				maindto.setSbgttypecode(MsgConstant.BDG_KIND_IN);//预算种类(默认预算内)
				maindto.setSbgttypename(S_BGTTYPENAME);//预算类型名称;
				maindto.setNxpayamt(BigDecimal.valueOf(Double.valueOf(N_XPAYAMT)));//回单金额;
				maindto.setSallnumber(S_ALLNUMBER);//总笔数;
				maindto.setSallpacket(S_ALLPACKET);//总包数;
				maindto.setSnowadaypacket(S_NOWADAYPACKET);//当前子包;
				/**
				 * 组装TvPayreckBankListDto对象
				 */
				List<TvPayreckBigdataBackListDto> subDtoList = new ArrayList<TvPayreckBigdataBackListDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					/**
					 * 校验报文明细Id节点是否存在
					 * 1、若节点不存在，是老版本，不更新明细Id
					 * 2、若节点存在，是新版本，更新明细Id
					 */
					String sN_PAYAMT = elementDetail.elementText("PayAmt");//支付金额
					if(sN_PAYAMT==null||"".equals(sN_PAYAMT))
						sN_PAYAMT="0.00";
					String S_PAYSUMMARYNAME = elementDetail.elementText("PaySummaryName");//摘要名称
					String sS_HOLD1 = elementDetail.elementText("Hold1");//预留字段
					String sS_HOLD2 = elementDetail.elementText("Hold2");//预留字段
					String S_HOLD3 = elementDetail.elementText("Hold3");//预留字段
					String S_HOLD4 = elementDetail.elementText("Hold4");//预留字段
					String sS_VOUCHERNO = elementDetail.elementText("VoucherNo");//支付凭证单号
					String S_SUPDEPCODE = elementDetail.elementText("SupDepCode");//预算单位编码
					String S_SUPDEPNAME = elementDetail.elementText("SupDepName");//预算单位名称
					String S_EXPFUNCCODE = elementDetail.elementText("ExpFuncCode");//功能分类代码
					String S_EXPFUNCNAME = elementDetail.elementText("ExpFuncName");//功能分类名称
					String S_PAYEEACCOUNT = elementDetail.elementText("PayeeAccount");//收款人账户
					String S_PAYEEACCOUNTNAME = elementDetail.elementText("PayeeAccountName");//收款人账户名称
					String S_PAYEEBANKACCOUNTNUMBER = elementDetail.elementText("PayeeBankAccountNumber");//收款人开户行行号
					String S_PAYEEBANKNAME = elementDetail.elementText("PayeeBankName");//收款人开户行名称
					String S_USES = elementDetail.elementText("Uses");//用途
					
					TvPayreckBigdataBackListDto subdto = new TvPayreckBigdataBackListDto();
					// 此处的设值待确认
					subdto.setSseqno(Integer.valueOf(j+1));//序号
					subdto.setIvousrlno(Long.valueOf(mainvou));// 子表序列号
					subdto.setNpayamt(BigDecimal.valueOf(Double.valueOf(sN_PAYAMT)));//支付金额
					subdto.setSpaysummaryname(S_PAYSUMMARYNAME);//摘要名称
					subdto.setShold1(sS_HOLD1);//预留字段
					subdto.setShold2(sS_HOLD2);//预留字段
					subdto.setShold3(S_HOLD3);//预留字段
					subdto.setShold4(S_HOLD4);//预留字段
					subdto.setSvoucherno(sS_VOUCHERNO);//支付凭证单号
					subdto.setSsupdepcode(S_SUPDEPCODE);//预算单位编码
					subdto.setSsupdepname(S_SUPDEPNAME);//预算单位名称
					subdto.setSexpfunccode(S_EXPFUNCCODE);//功能分类代码
					subdto.setSexpfuncname(S_EXPFUNCNAME);//功能分类名称
					subdto.setSpayeeaccount(S_PAYEEACCOUNT);//收款人账户
					subdto.setSpayeeaccountname(S_PAYEEACCOUNTNAME);//收款人账户名称
					subdto.setSpayeebankaccountnumber(S_PAYEEBANKACCOUNTNUMBER);//收款人开户行行号
					subdto.setSpayeebankname(S_PAYEEBANKNAME);//收款人开户行名称
					subdto.setSuses(S_USES);//用途
					subDtoList.add(subdto);
				}
				
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setScreatdate(vDto.getScreatdate());
				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
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
				logger.error("2306解析发生异常:",e);
				voucher.voucherComfail(mainvou, "报文不规范："+e.getMessage());
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
