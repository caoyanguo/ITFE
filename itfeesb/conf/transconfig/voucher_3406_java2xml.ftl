<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id> 
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear> 
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<#if Voucher.TreCode?exists>
	<TreCode>${Voucher.TreCode}</TreCode>
	</#if>
	<#if Voucher.TreName?exists>
	<TreName>${Voucher.TreName}</TreName>
	</#if>
	<#if Voucher.FinOrgCode?exists>
	<FinOrgCode>${Voucher.FinOrgCode}</FinOrgCode>
	</#if>
	<FundTypeCode>${Voucher.FundTypeCode}</FundTypeCode>
	<BudgetType>${Voucher.BudgetType}</BudgetType>
	<#if Voucher.ReportDate?exists>
	<ReportDate>${Voucher.ReportDate}</ReportDate>
	</#if>
	<#if Voucher.SumMoney?exists>
	<SumMoney>${Voucher.SumMoney}</SumMoney>
	</#if>	
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<DetailList>
	  <#list Voucher.DetailList.Detail as var>
	  <Detail>
	  	<Id>${var.Id}</Id>
		<TreCode>${var.TreCode}</TreCode>
		<TreName>${var.TreName}</TreName>
		<DayAmt>${var.DayAmt}</DayAmt>
		<MonthAmt>${var.MonthAmt}</MonthAmt>
		<YearAmt>${var.YearAmt}</YearAmt>
		<Hold1>${var.Hold1}</Hold1>
	  	<Hold2>${var.Hold2}</Hold2>
	  	<Hold3>${var.Hold3}</Hold3>
	  	<Hold4>${var.Hold4}</Hold4>	  
	 </Detail>
	</#list> 
	</DetailList>  
</Voucher>