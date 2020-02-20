<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode> 
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode> 
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo> 
	<TreCode>${Voucher.TreCode}</TreCode>
	<PayTypeCode>${Voucher.PayTypeCode}</PayTypeCode>
	<PayTypeName>${Voucher.PayTypeName}</PayTypeName>		
	<PayBankCode>${Voucher.PayBankCode}</PayBankCode>
	<PayBankName>${Voucher.PayBankName}</PayBankName>
	<CheckDate>${Voucher.CheckDate}</CheckDate>
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
		  <ExpFuncCode>${var.ExpFuncCode}</ExpFuncCode>
		  <ExpFuncName>${var.ExpFuncName}</ExpFuncName>
		  <MonthAmt>${var.MonthAmt}</MonthAmt>
		  <YearAmt>${var.YearAmt}</YearAmt>
		  <Hold1>${var.Hold1}</Hold1>
	  	  <Hold2>${var.Hold2}</Hold2>
	 </Detail>
	</#list> 
	</DetailList>
	</#list>	
</Voucher>