package com.cfcc.itfe.msgmanager.msg;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;


/**
 *（报文编号涉及：3140）
 * 
 * 只转发财政，不做任何处理
 * 
 */
public class Recv3140MsgServer extends AbstractMsgManagerServer {

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try{
			// 接收Tips发起业务--广西MQ消息规则初始化
			eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
//			HashMap headMap = (HashMap) cfxMap.get("HEAD");
	
			// 报文头信息CFX->HEAD
//			String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
//			String sdescode = (String) headMap.get("DES");// 接收节点代码
//			String msgNo = (String) headMap.get("MsgNo");// 报文编号
//			String msgID = (String) headMap.get("MsgID");// 报文标识号
			// String msgRef = (String) headMap.get("MsgRef");// 报文参考号
			// String sdate = (String) headMap.get("WorkDate");// 工作日期
	
			/**
			 * 取得回执头信息
			 */
			HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3140");
	
			String strecode = (String) batchHeadMap.get("DrawBackTreCode"); //退库国库代码
			String sfinorgcode = (String) batchHeadMap.get("FinOrgCode");// 财政机关代码
			String entrustDate = (String) batchHeadMap.get("EntrustDate");// 委托日期
//			String sentrustDate = (String) batchHeadMap.get("EntrustDate");// 委托日期
			String spackno = (String) batchHeadMap.get("PackNo"); // 包流水号
			
			List retTreasury3140 = (List) msgMap.get("RetTreasury3140");// 税种明细
			if(retTreasury3140!=null&&retTreasury3140.size()>0)
			{
				HashMap retTreasury3140Map = null;
				List<IDto> dtolist = new ArrayList<IDto>();
				List<IDto> vdtolist = new ArrayList<IDto>();
				TvDwbkDto dwbkdto = null;
				TvVoucherinfoDto vdto = null;
				TsConvertfinorgDto cDto=new TsConvertfinorgDto();
				cDto.setSfinorgcode(sfinorgcode);
				cDto.setStrecode(strecode);
				List<TsConvertfinorgDto> cdtoList=CommonFacade.getODB().findRsByDto(cDto);
				for(int i=0;i<retTreasury3140.size();i++)
				{
					retTreasury3140Map = (HashMap) retTreasury3140.get(i);
					String traNo = (String) retTreasury3140Map.get("TraNo");// 交易流水号
					String taxOrgCode = (String) retTreasury3140Map.get("TaxOrgCode");// 征收机关代码
//					String payeeBankNo = (String) retTreasury3140Map.get("PayeeBankNo");// 收款行行号
					String payeeOpBkCode = (String) retTreasury3140Map.get("PayeeOpBkCode");//收款开户行行号
					String PayeeAcct = (String) retTreasury3140Map.get("PayeeAcct");// 收款人账号
					String payeeName = (String) retTreasury3140Map.get("PayeeName");// 收款人名称
					String taxPayCode = (String) retTreasury3140Map.get("TaxPayCode");// 纳税人编码
//					String taxPayName = (String) retTreasury3140Map.get("TaxPayName");// 纳税人名称
					String ElectroTaxVouNo = (String) retTreasury3140Map.get("ElectroTaxVouNo");//退库凭证编号
//					String electroTaxVouNo = (String) retTreasury3140Map.get("ElectroTaxVouNo");// 电子凭证号码
//					String oriTaxVouNo = (String) retTreasury3140Map.get("OriTaxVouNo");// 原税票号码
					String traAmt = (String) retTreasury3140Map.get("TraAmt");// 交易金额
					String billDate = (String) retTreasury3140Map.get("BillDate");// 开票日期
//					String corpCode = (String) retTreasury3140Map.get("CorpCode");//企业代码
					String budgetType = (String) retTreasury3140Map.get("BudgetType");// 预算种类
					String budgetSubjectCode = (String) retTreasury3140Map.get("BudgetSubjectCode");// 预算科目代码
					String budgetLevelCode = (String) retTreasury3140Map.get("BudgetLevelCode");// 预算级次代码
					String trimSign = (String) retTreasury3140Map.get("TrimSign");// 整理期标志
					String viceSign = (String) retTreasury3140Map.get("ViceSign");//辅助标志
					String drawBackReasonCode = (String) retTreasury3140Map.get("DrawBackReasonCode");// 退库原因代码
					String drawBackVou = (String) retTreasury3140Map.get("DrawBackVou");// 退库依据
//					String examOrg = (String) retTreasury3140Map.get("ExamOrg");// 审批机关
//					String authNo = (String) retTreasury3140Map.get("AuthNo");// 批准文号
//					String transType = (String) retTreasury3140Map.get("TransType");//转账类型
//					String opStat = (String) retTreasury3140Map.get("OpStat");// 处理状态
					
					dwbkdto = new TvDwbkDto();
					vdto = new TvVoucherinfoDto();
					String mainvou = VoucherUtil.getGrantSequence();
					vdto.setSdealno(mainvou);
					dwbkdto.setIvousrlno(Long.valueOf(mainvou));
					dwbkdto.setSastflag(viceSign);
					
					dwbkdto.setSbizno(mainvou);
					dwbkdto.setSdealno(mainvou.substring(8, 16));
					dwbkdto.setSelecvouno(traNo);
					dwbkdto.setSdwbkvoucode(traNo);
					dwbkdto.setSpayertrecode(strecode);
					dwbkdto.setSaimtrecode(strecode);
					dwbkdto.setCbckflag(StateConstant.MSG_BACK_FLAG_NO);
					dwbkdto.setCbdgkind(MsgConstant.BDG_KIND_IN);
					dwbkdto.setCtrimflag(trimSign);
					dwbkdto.setSpayeeopnbnkno(payeeOpBkCode);
					dwbkdto.setCbdglevel(budgetLevelCode);
					dwbkdto.setSpackageno(spackno);
					dwbkdto.setSpayeeacct(PayeeAcct);
					dwbkdto.setSpayeecode(taxPayCode);
					dwbkdto.setSbdgsbtcode(budgetSubjectCode);//科目代码
					dwbkdto.setSpayeename(payeeName);
					dwbkdto.setDaccept(TimeFacade.getCurrentDateTime());
					dwbkdto.setDvoucher(CommonUtil.strToDate(billDate));
					dwbkdto.setDacct(TimeFacade.getCurrentDateTime());
					dwbkdto.setDbill(TimeFacade.getCurrentDateTime());
					dwbkdto.setSbookorgcode(cdtoList.get(0).getSorgcode());
					dwbkdto.setSfilename((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
					dwbkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//状态
					dwbkdto.setSfundtypecode(budgetType);//资金性质编码
					dwbkdto.setSfundtypename(MsgConstant.BDG_KIND_IN.equals(budgetType)?"预算内":"预算外");//资金性质名称
					dwbkdto.setSclearbankcode(strecode);
					dwbkdto.setSclearbankname("");
					dwbkdto.setSdwbkreasoncode(drawBackReasonCode);//退回原因
					dwbkdto.setSdemo("Tips下发退库报文3140");
					dwbkdto.setSdwbkby(drawBackVou);
					dwbkdto.setXagentbusinessno("");
					dwbkdto.setSpayacctno("");//付款人账号
					dwbkdto.setSpayacctname("");//付款人名称
					dwbkdto.setSpayacctbankname("");//付款人银行
					dwbkdto.setSbgttypecode(budgetType);//预算类型编码
					dwbkdto.setSbgttypename(MsgConstant.BDG_KIND_IN.equals(budgetType)?"预算内":"预算外");//预算类型编码
					dwbkdto.setSprocatcode("");//收支管理编码
					dwbkdto.setSprocatname("");//收支管理名称
					dwbkdto.setSagencyname("");//预算单位编码
					dwbkdto.setSincomesortname("");//收入分类科目名称
					dwbkdto.setSincomesortname1("");//收入分类科目名称
					dwbkdto.setSincomesortname2("");//收入分类科目名称
					dwbkdto.setSincomesortname3("");//收入分类科目名称
					dwbkdto.setSincomesortname4("");//收入分类科目名称
					dwbkdto.setSincomesortcode1("");//收入分类科目类编码
					dwbkdto.setSincomesortcode2("");//收入分类科目类编码
					dwbkdto.setSincomesortcode3("");//收入分类科目类编码
					dwbkdto.setSincomesortcode4("");//收入分类科目类编码
					dwbkdto.setSbiztype("5230");
					dwbkdto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
					dwbkdto.setFamt(BigDecimal.valueOf(Double.valueOf(traAmt)));//金额
					dwbkdto.setFdwbkamt(dwbkdto.getFamt());
					dwbkdto.setShold2(String.valueOf(dwbkdto.getFamt()));
					dwbkdto.setStaxorgcode(taxOrgCode);
					vdto.setNmoney(MtoCodeTrans.transformBigDecimal(traAmt));
					vdto.setSadmdivcode(cdtoList.get(0).getSadmdivcode());
					vdto.setScreatdate(TimeFacade.getCurrentStringTime());
					vdto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));
					vdto.setSfilename((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
					vdto.setSorgcode(cdtoList.get(0).getSorgcode());
					vdto.setShold1("");
					vdto.setShold2("");
					vdto.setShold3("");
					vdto.setShold4("");
					vdto.setSext1("");// 支付方式
					vdto.setSext3("");// 业务类型
					vdto.setSext2("");// 
					vdto.setSext4("");// 预算类型
					vdto.setSext5("");//
					vdto.setSstyear(vdto.getScreatdate().substring(0,4));
					vdto.setStrecode(strecode);
					vdto.setSvoucherflag("1");
					vdto.setSvoucherno(ElectroTaxVouNo);
					vdto.setSvtcode("5230");
					vdto.setSpaybankcode(sfinorgcode); // 出票单位
					vdto.setSext1(StateConstant.PAYOUT_PAY_CODE);
					vdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					vdto.setSdemo("Tips下发退库3140报文");
					vdto.setSattach(drawBackReasonCode);
					dtolist.add(dwbkdto);
					vdtolist.add(vdto);
				}
				if(dtolist!=null&&dtolist.size()>0)
				{
					DatabaseFacade.getDb().create(CommonUtil.listTArray(dtolist));
					saveDownloadReportCheck(entrustDate,strecode,dtolist.size());
				}
				if(vdtolist!=null&&vdtolist.size()>0)
					DatabaseFacade.getDb().create(CommonUtil.listTArray(vdtolist));
			}
		}catch(Exception e)
		{
			
		}
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
	private void saveDownloadReportCheck(String date,String trecode,int size)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setItuikucount(new Integer(size));
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if(dto.getItuikucount()!=null&&dto.getItuikucount()>=0)
				{
					dto.setItuikucount(dto.getItuikucount()+new Integer(size));
				}else
				{
					dto.setItuikucount(new Integer(size));
				}
				DatabaseFacade.getODB().update(dto);
			}
		} catch (JAFDatabaseException e) {
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}catch(Exception e)
		{
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}
	}
}	

