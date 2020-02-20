<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<TreCode>${Voucher.TreCode}</TreCode>
	<FinOrgCode>${Voucher.FinOrgCode}</FinOrgCode>
	<#if Voucher.TaxOrgCode?exists>
	<TaxOrgCode>${Voucher.TaxOrgCode}</TaxOrgCode>
	</#if>
	<#if Voucher.TaxOrgName?exists>
	<TaxOrgName>${Voucher.TaxOrgName}</TaxOrgName>
	</#if>
	<#if Voucher.BudgetLevelCode?exists>
	<BudgetLevelCode>${Voucher.BudgetLevelCode}</BudgetLevelCode>
	</#if>
	<#if Voucher.BudgetType?exists>
	<BudgetType>${Voucher.BudgetType}</BudgetType>
	</#if>
	<FundTypeCode>${Voucher.FundTypeCode}</FundTypeCode>
	<FundTypeName>${Voucher.FundTypeName}</FundTypeName>
	<ClearBankCode>${Voucher.ClearBankCode}</ClearBankCode>
	<ClearBankName>${Voucher.ClearBankName}</ClearBankName>
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
	<PayAcctNo>${Voucher.PayAcctNo}</PayAcctNo>
	<PayAcctName>${Voucher.PayAcctName}</PayAcctName>
	<PayAcctBankName>${Voucher.PayAcctBankName}</PayAcctBankName>
	<#if Voucher.AgencyCode?exists>
	<AgencyCode>${Voucher.AgencyCode}</AgencyCode>
	</#if>
	<#if Voucher.AgencyName?exists>
	<AgencyName>${Voucher.AgencyName}</AgencyName>
	</#if>
	<nReturnReasonCode>${Voucher.nReturnReasonCode}</nReturnReasonCode>
	<nReturnReasonName>${Voucher.nReturnReasonName}</nReturnReasonName>
	<PayAmt>${Voucher.PayAmt}</PayAmt>
	<TrimSign>${Voucher.TrimSign}</TrimSign>
	<XPayAmt>${Voucher.XPayAmt}</XPayAmt>
	<XPayDate>${Voucher.XPayDate}</XPayDate>
	<#if Voucher.XAgentBusinessNo?exists>
	<XAgentBusinessNo>${Voucher.XAgentBusinessNo}</XAgentBusinessNo>
	</#if>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<#list Voucher.DetailList as var1>
	<DetailList>
		  <#list var1.Detail as var>
		 <Detail>
			<Id>${var.Id}</Id>
			<VoucherBillId>${var.VoucherBillId}</VoucherBillId>
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
			<#if var.XPayAmt?exists>
			<XPayAmt>${var.XPayAmt}</XPayAmt>
			</#if>
			<#if var.XPayDate?exists>
			<XPayDate>${var.XPayDate}</XPayDate>
			</#if>
			<#if var.XAgentBusinessNo?exists>
			<XAgentBusinessNo>${var.XAgentBusinessNo}</XAgentBusinessNo>
			</#if>
			<#if var.XAddWord?exists>
			<XAddWord>${var.XAddWord}</XAddWord>
			</#if>
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