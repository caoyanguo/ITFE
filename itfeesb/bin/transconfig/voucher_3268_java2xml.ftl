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
	<FundTypeCode>${Voucher.FundTypeCode}</FundTypeCode>
	<FundTypeName>${Voucher.FundTypeName}</FundTypeName>
	<PayTypeCode>${Voucher.PayTypeCode}</PayTypeCode>
	<PayTypeName>${Voucher.PayTypeName}</PayTypeName>
	<#if Voucher.ProCatCode?exists>
	<ProCatCode>${Voucher.ProCatCode}</ProCatCode>
	</#if>
	<#if Voucher.ProCatName?exists>
	<ProCatName>${Voucher.ProCatName}</ProCatName>
	</#if>
	<#--20141015_V2.0最新接口增加字段AgencyCode、AgencyName-->
	<#if Voucher.AgencyCode?exists>
	<AgencyCode>${Voucher.AgencyCode}</AgencyCode>
	</#if>
	<#if Voucher.AgencyName?exists>
	<AgencyName>${Voucher.AgencyName}</AgencyName>
	</#if>
	<PayAcctNo>${Voucher.PayAcctNo}</PayAcctNo>
	<PayAcctName>${Voucher.PayAcctName}</PayAcctName>
	<PayAcctBankName>${Voucher.PayAcctBankName}</PayAcctBankName>
	<PayeeAcctNo>${Voucher.PayeeAcctNo}</PayeeAcctNo>
	<PayeeAcctName>${Voucher.PayeeAcctName}</PayeeAcctName>
	<PayeeAcctBankName>${Voucher.PayeeAcctBankName}</PayeeAcctBankName>
	<#if Voucher.PayeeAcctBankNo?exists>
	<PayeeAcctBankNo>${Voucher.PayeeAcctBankNo}</PayeeAcctBankNo>
	</#if>
	<#if Voucher.PaySummaryCode?exists>
	<PaySummaryCode>${Voucher.PaySummaryCode}</PaySummaryCode>
	</#if>
	<#if Voucher.PaySummaryName?exists>
	<PaySummaryName>${Voucher.PaySummaryName}</PaySummaryName>
	</#if>
	<#--20141015_V2.0最新接口增加字段Remark-->
	<#if Voucher.Remark?exists>
	<Remark>${Voucher.Remark}</Remark>
	</#if>
	<PayAmt>${Voucher.PayAmt}</PayAmt>
	<#if Voucher.Hold1?exists>
	<Hold1>${Voucher.Hold1}</Hold1>
	</#if>
	<#if Voucher.Hold2?exists>
	<Hold2>${Voucher.Hold2}</Hold2>	
	</#if>
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
		<#if var.DepProCode?exists>
		<DepProCode>${var.DepProCode}</DepProCode>		
		</#if>
		<#if var.DepProName?exists>
		<DepProName>${var.DepProName}</DepProName>		
		</#if>
		<FundTypeCode>${Voucher.FundTypeCode}</FundTypeCode>
		<FundTypeName>${Voucher.FundTypeName}</FundTypeName>
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
		<PayTypeCode>${Voucher.PayTypeCode}</PayTypeCode>
		<PayTypeName>${Voucher.PayTypeName}</PayTypeName>
		<#--20141015_V2.0最新接口增加字段PayeeAcctNo、PayeeAcctName、PayeeAcctBankName、PayeeAcctBankNo-->
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
		<#if var.ProjectCategoryCode?exists>
		<ProjectCategoryCode>${var.ProjectCategoryCode}</ProjectCategoryCode>
		</#if>
		<#if var.ProjectCategoryName?exists>
		<ProjectCategoryName>${var.ProjectCategoryName}</ProjectCategoryName>
		</#if>
		<ExpFuncCode>${var.ExpFuncCode}</ExpFuncCode>
		<ExpFuncName>${var.ExpFuncName}</ExpFuncName>
		<#if var.ExpEcoCode?exists>
		<ExpEcoCode>${var.ExpEcoCode}</ExpEcoCode>
		</#if>
		<#if var.ExpEcoName?exists>
		<ExpEcoName>${var.ExpEcoName}</ExpEcoName>
		</#if>
		<Amt>${var.Amt}</Amt>
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