<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>	
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode> 
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>	
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>	
	<PayeeAcctNo>${Voucher.PayeeAcctNo}</PayeeAcctNo>
	<PayeeAcctName>${Voucher.PayeeAcctName}</PayeeAcctName>
	<#if Voucher.PayeeAcctBankName?exists>
		<PayeeAcctBankName>${Voucher.PayeeAcctBankName}</PayeeAcctBankName>
	</#if>
	<PayAcctNo>${Voucher.PayAcctNo}</PayAcctNo>
	<PayAcctName>${Voucher.PayAcctName}</PayAcctName>
	<#if Voucher.PayAcctBankName?exists>
		<PayAcctBankName>${Voucher.PayAcctBankName}</PayAcctBankName>
	</#if>
	<#if Voucher.PaySummaryCode?exists>
		<PaySummaryCode>${Voucher.PaySummaryCode}</PaySummaryCode>
	</#if>
	<#if Voucher.PaySummaryName?exists>
		<PaySummaryName>${Voucher.PaySummaryName}</PaySummaryName>
	</#if>
	<PayAmt>${Voucher.PayAmt}</PayAmt>
	<#if Voucher.AgencyCode?exists>
		<AgencyCode>${Voucher.AgencyCode}</AgencyCode>
	</#if>
	<#if Voucher.AGencyName?exists>
		<AGencyName>${Voucher.AGencyName}</AGencyName>
	</#if>
	<#if Voucher.MsgType?exists>
		<MsgType>${Voucher.MsgType}</MsgType>
	</#if>
	<#if Voucher.PayTraseqNo?exists>
		<PayTraseqNo>${Voucher.PayTraseqNo}</PayTraseqNo>
	</#if>
	<#if Voucher.TrasType?exists>
		<TrasType>${Voucher.TrasType}</TrasType>
	</#if>
	<#if Voucher.BizType?exists>
		<BizType>${Voucher.BizType}</BizType>
	</#if>
	<#if Voucher.SndBnkNo?exists>
		<SndBnkNo>${Voucher.SndBnkNo}</SndBnkNo>
	</#if>
	<#if Voucher.PayerOpnBnkNo?exists>
		<PayerOpnBnkNo>${Voucher.PayerOpnBnkNo}</PayerOpnBnkNo>
	</#if>
	<#--20141015_V2.0最新接口字段PayAcctBankNo-->
	<#if Voucher.PayAcctBankNo?exists>
		<PayAcctBankNo>${Voucher.PayAcctBankNo}</PayAcctBankNo>
	</#if>
	<#if Voucher.RcvBnkNo?exists>
		<RcvBnkNo>${Voucher.RcvBnkNo}</RcvBnkNo>
	</#if>
	<#if Voucher.PayeeOpnbnkNo?exists>
		<PayeeOpnbnkNo>${Voucher.PayeeOpnbnkNo}</PayeeOpnbnkNo>
	</#if>
	<#--20141015_V2.0最新接口字段PayeeAcctBankNo-->
	<#if Voucher.PayeeAcctBankNo?exists>
		<PayeeAcctBankNo>${Voucher.PayeeAcctBankNo}</PayeeAcctBankNo>
	</#if>
	<#if Voucher.DebitAcct?exists>
		<DebitAcct>${Voucher.DebitAcct}</DebitAcct>
	</#if>
	<#if Voucher.LoanAcct?exists>
		<LoanAcct>${Voucher.LoanAcct}</LoanAcct>
	</#if>
	<#if Voucher.ChkSignFlag?exists>
		<ChkSignFlag>${Voucher.ChkSignFlag}</ChkSignFlag>
	</#if>
	<#if Voucher.ChkSign?exists>
		<ChkSign>${Voucher.ChkSign}</ChkSign>
	</#if>
	<#if Voucher.RecvMsgDate?exists>
		<RecvMsgDate>${Voucher.RecvMsgDate}</RecvMsgDate>
	</#if>
	<#if Voucher.EntrustDate?exists>
		<EntrustDate>${Voucher.EntrustDate}</EntrustDate>
	</#if>
	<#if Voucher.AddWord?exists>
		<AddWord>${Voucher.AddWord}</AddWord>
	</#if>
	<#if Voucher.TrasrlNo?exists>
		<TrasrlNo>${Voucher.TrasrlNo}</TrasrlNo>
	</#if>
	<#--20141015_V2.0最新接口字段AgentBusinessNo-->
	<#if Voucher.AgentBusinessNo?exists>
		<AgentBusinessNo>${Voucher.AgentBusinessNo}</AgentBusinessNo>
	</#if>
	<#if Voucher.PrintTime?exists>
		<PrintTime>${Voucher.PrintTime}</PrintTime>
	</#if>
	<#if Voucher.PrintNum?exists>
		<PrintNum>${Voucher.PrintNum}</PrintNum>
	</#if>
	<#if Voucher.PrintOper?exists>
		<PrintOper>${Voucher.PrintOper}</PrintOper>
	</#if>
	<#if Voucher.Hold1?exists>
		<Hold1>${Voucher.Hold1}</Hold1>
	</#if>
	<#if Voucher.Hold2?exists>
		<Hold2>${Voucher.Hold2}</Hold2>
	</#if>
	<#--20141015_V2.0最新接口字段Hold3、Hold4-->
	<#if Voucher.Hold3?exists>
		<Hold3>${Voucher.Hold3}</Hold3>
	</#if>
	<#if Voucher.Hold4?exists>
		<Hold4>${Voucher.Hold4}</Hold4>
	</#if>
</Voucher>