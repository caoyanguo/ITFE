<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<TreCode>${Voucher.TreCode}</TreCode>
	<#if Voucher.FinOrgCode?exists>
	<FinOrgCode>${Voucher.FinOrgCode}</FinOrgCode>
	</#if>
	<#if Voucher.FundTypeCode?exists>
	<FundTypeCode>${Voucher.FundTypeCode}</FundTypeCode>
	</#if>
	<#if Voucher.FundTypeName?exists>
	<FundTypeName>${Voucher.FundTypeName}</FundTypeName>
	</#if>
	<#if Voucher.ClearBankCode?exists>
	<ClearBankCode>${Voucher.ClearBankCode}</ClearBankCode>
	</#if>
	<#if Voucher.ClearBankName?exists>
	<ClearBankName>${Voucher.ClearBankName}</ClearBankName>
	</#if>
	<#if Voucher.PayeeAcctNo?exists>
	<PayeeAcctNo>${Voucher.PayeeAcctNo}</PayeeAcctNo>
	</#if>
	<#if Voucher.PayeeAcctName?exists>
	<PayeeAcctName>${Voucher.PayeeAcctName}</PayeeAcctName>
	</#if>
	<#if Voucher.PayeeAcctBankName?exists>
	<PayeeAcctBankName>${Voucher.PayeeAcctBankName}</PayeeAcctBankName>
	</#if>
	<#if Voucher.AgentBusinessNo?exists>
	<AgentBusinessNo>${Voucher.AgentBusinessNo}</AgentBusinessNo>
	</#if>
	<#if Voucher.OriBillNo?exists>
	<OriBillNo>${Voucher.OriBillNo}</OriBillNo>
	</#if>
	<#if Voucher.OriVouDate?exists>
	<OriVouDate>${Voucher.OriVouDate}</OriVouDate>
	</#if>
	<#if Voucher.OriPayDate?exists>
	<OriPayDate>${Voucher.OriPayDate}</OriPayDate>
	</#if>
	<#if Voucher.PayeeAcctBankNo?exists>
	<PayeeAcctBankNo>${Voucher.PayeeAcctBankNo}</PayeeAcctBankNo>
	</#if>
	<#if Voucher.PayAcctNo?exists>
	<PayAcctNo>${Voucher.PayAcctNo}</PayAcctNo>
	</#if>
	<#if Voucher.PayAcctName?exists>
	<PayAcctName>${Voucher.PayAcctName}</PayAcctName>
	</#if>
	<#if Voucher.PayAcctBankName?exists>
	<PayAcctBankName>${Voucher.PayAcctBankName}</PayAcctBankName>
	</#if>
	<#if Voucher.ReturnReasonName?exists>
	<ReturnReasonName>${Voucher.ReturnReasonName}</ReturnReasonName>
	</#if>
	<#if Voucher.PayAmt?exists>
	<PayAmt>${Voucher.PayAmt}</PayAmt>
	</#if>
	<#if Voucher.XpayAmt?exists>
	<XpayAmt>${Voucher.XpayAmt}</XpayAmt>
	</#if>
	<#if Voucher.XPayDate?exists>
	<XPayDate>${Voucher.XPayDate}</XPayDate>
	</#if>
	<#if Voucher.XAgentBusinessNo?exists>
	<XAgentBusinessNo>${Voucher.XAgentBusinessNo}</XAgentBusinessNo>
	</#if>
	<#if Voucher.Hold1?exists>
	<Hold1>${Voucher.Hold1}</Hold1>
	</#if>
	<#if Voucher.Hold2?exists>
	<Hold2>${Voucher.Hold2}</Hold2>	
	</#if>
	<DetailList>
	 <Detail>
		<Id>${Voucher.Id}</Id>
		<VoucherBillId>${Voucher.VoucherBillId}</VoucherBillId>
		<#if Voucher.BgtTypeCode?exists>
		<BgtTypeCode>${Voucher.BgtTypeCode}</BgtTypeCode>
		</#if>
		<#if Voucher.BgtTypeName?exists>
		<BgtTypeName>${Voucher.BgtTypeName}</BgtTypeName>
		</#if>
		<#if Voucher.ProCatCode?exists>
		<ProCatCode>${Voucher.ProCatCode}</ProCatCode>		
		</#if>
		<#if Voucher.ProCatName?exists>
		<ProCatName>${Voucher.ProCatName}</ProCatName>		
		</#if>
		<#if Voucher.AgencyCode?exists>
		<AgencyCode>${Voucher.AgencyCode}</AgencyCode>
		</#if>
		<#if Voucher.AgencyName?exists>
		<AgencyName>${Voucher.AgencyName}</AgencyName>
		</#if>
		<#if Voucher.IncomeSortCode?exists>
		<IncomeSortCode>${Voucher.IncomeSortCode}</IncomeSortCode>
		</#if>
		<#if Voucher.IncomeSortName?exists>
		<IncomeSortName>${Voucher.IncomeSortName}</IncomeSortName>
		</#if>
		<#if Voucher.IncomeSortCode1?exists>
		<IncomeSortCode1>${Voucher.IncomeSortCode1}</IncomeSortCode1>
		</#if>
		<#if Voucher.IncomeSortName1?exists>
		<IncomeSortName1>${Voucher.IncomeSortName1}</IncomeSortName1>
		</#if>
		<#if Voucher.IncomeSortCode2?exists>
		<IncomeSortCode2>${Voucher.IncomeSortCode2}</IncomeSortCode2>
		</#if>
		<#if Voucher.IncomeSortName2?exists>
		<IncomeSortName2>${Voucher.IncomeSortName2}</IncomeSortName2>
		</#if>
		<#if Voucher.IncomeSortCode3?exists>
		<IncomeSortCode3>${Voucher.IncomeSortCode3}</IncomeSortCode3>
		</#if>
		<#if Voucher.IncomeSortName3?exists>
		<IncomeSortName3>${Voucher.IncomeSortName3}</IncomeSortName3>
		</#if>
		<#if Voucher.IncomeSortCode4?exists>
		<IncomeSortCode4>${Voucher.IncomeSortCode4}</IncomeSortCode4>
		</#if>
		<#if Voucher.IncomeSortName4?exists>
		<IncomeSortName4>${Voucher.IncomeSortName4}</IncomeSortName4>
		</#if>
		<PayAmt>${Voucher.PayAmt}</PayAmt>
		<#if Voucher.Hold1?exists>
		<Hold1>${Voucher.Hold1}</Hold1>
		</#if>
		<#if Voucher.Hold2?exists>
		<Hold2>${Voucher.Hold2}</Hold2>
		</#if>
		<#if Voucher.Hold3?exists>
		<Hold3>${Voucher.Hold3}</Hold3>
		</#if>
		<#if Voucher.Hold4?exists>
		<Hold4>${Voucher.Hold4}</Hold4>
		</#if>	
	 </Detail>
	</DetailList>
</Voucher>