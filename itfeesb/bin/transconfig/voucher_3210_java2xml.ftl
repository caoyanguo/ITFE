<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
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
	<#if Voucher.AgencyCode?exists>
	<AgencyCode>${Voucher.AgencyCode}</AgencyCode>
	</#if>
	<#if Voucher.AgencyName?exists>
	<AgencyName>${Voucher.AgencyName}</AgencyName>
	</#if>
	<#if Voucher.Remark?exists>
	<Remark>${Voucher.Remark}</Remark>
	</#if>
	<#if Voucher.PayAmt?exists>
	<PayAmt>${Voucher.PayAmt}</PayAmt>
	</#if>
	<#if Voucher.ReturnReasonName?exists>
	<ReturnReasonName>${Voucher.ReturnReasonName}</ReturnReasonName>
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
	<#if Voucher.BudgetLevelCode?exists>
	<BudgetLevelCode>${Voucher.BudgetLevelCode}</BudgetLevelCode>
	</#if>
	<#if Voucher.BudgetLevelName?exists>
	<BudgetLevelName>${Voucher.BudgetLevelName}</BudgetLevelName>
	</#if>
	<PeriodType>正常业务</PeriodType>
	<PeriodTypeCode>01</PeriodTypeCode>
	<#list Voucher.DetailList as var1>
	<DetailList>
	  <#list var1.Detail as var>
	 <Detail>
		<Id>${var.Id}</Id>
		<VoucherBillId>${var.VoucherBillId}</VoucherBillId>
		<#if var.VoucherDetailNo?exists>
		<VoucherDetailNo>${var.VoucherDetailNo}</VoucherDetailNo>
		</#if>
		<#if var.BgtTypeCode?exists>
		<BgtTypeCode>${var.BgtTypeCode}</BgtTypeCode>
		</#if>
		<#if var.BgtTypeName?exists>
		<BgtTypeName>${var.BgtTypeName}</BgtTypeName>
		</#if>
		<#if var.ProCatCode?exists>
		<ProCatCode>${var.ProCatCode}</ProCatCode>		
		</#if>
		<#if var.ProCatName?exists>
		<ProCatName>${var.ProCatName}</ProCatName>		
		</#if>
		<#if var.PayeeAcctNo?exists>
		<PayeeAcctNo>${var.PayeeAcctNo}</PayeeAcctNo>
		</#if>
		<#if var.PayeeAcctName?exists>
		<PayeeAcctName>${var.PayeeAcctName}</PayeeAcctName>
		</#if>
		<#if var.PayeeAcctBankName?exists>
		<PayeeAcctBankName>${var.PayeeAcctBankName}</PayeeAcctBankName>
		</#if>
		<#if var.PayeeAcctBankNo?exists>
		<PayeeAcctBankNo>${var.PayeeAcctBankNo}</PayeeAcctBankNo>
		</#if>
		<#if var.AgencyCode?exists>
		<AgencyCode>${var.AgencyCode}</AgencyCode>
		</#if>
		<#if var.AgencyName?exists>
		<AgencyName>${var.AgencyName}</AgencyName>
		</#if>
		<#if var.IncomeSortCode?exists>
		<IncomeSortCode>${var.IncomeSortCode}</IncomeSortCode>
		</#if>
		<#if var.IncomeSortName?exists>
		<IncomeSortName>${var.IncomeSortName}</IncomeSortName>
		</#if>
		<#if var.IncomeSortCode1?exists>
		<IncomeSortCode1>${var.IncomeSortCode1}</IncomeSortCode1>
		</#if>
		<#if var.IncomeSortName1?exists>
		<IncomeSortName1>${var.IncomeSortName1}</IncomeSortName1>
		</#if>
		<#if var.IncomeSortCode2?exists>
		<IncomeSortCode2>${var.IncomeSortCode2}</IncomeSortCode2>
		</#if>
		<#if var.IncomeSortName2?exists>
		<IncomeSortName2>${var.IncomeSortName2}</IncomeSortName2>
		</#if>
		<#if var.IncomeSortCode3?exists>
		<IncomeSortCode3>${var.IncomeSortCode3}</IncomeSortCode3>
		</#if>
		<#if var.IncomeSortName3?exists>
		<IncomeSortName3>${var.IncomeSortName3}</IncomeSortName3>
		</#if>
		<#if var.IncomeSortCode4?exists>
		<IncomeSortCode4>${var.IncomeSortCode4}</IncomeSortCode4>
		</#if>
		<#if var.IncomeSortName4?exists>
		<IncomeSortName4>${var.IncomeSortName4}</IncomeSortName4>
		</#if>
		<PayAmt>${var.PayAmt}</PayAmt>
		<#if var.Hold1?exists>
		<Hold1>${var.Hold1}</Hold1>
		</#if>
		<#if var.Hold2?exists>
		<Hold2>${var.Hold2}</Hold2>
		</#if>
		<#if var.Hold3?exists>
		<Hold3>${var.Hold3}</Hold3>
		</#if>
		<#if var.Hold4?exists>
		<Hold4>${var.Hold4}</Hold4>
		</#if>
	 </Detail>
	 </#list> 
	</DetailList>
	</#list>
</Voucher>