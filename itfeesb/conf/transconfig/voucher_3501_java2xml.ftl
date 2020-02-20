<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode> 
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode> 
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo> 
	<TreCode>${Voucher.TreCode}</TreCode>
	<#if Voucher.ClearBankCode?exists>
	<ClearBankCode>${Voucher.ClearBankCode}</ClearBankCode>
	</#if>
	<#if Voucher.ClearBankName?exists>
	<ClearBankName>${Voucher.ClearBankName}</ClearBankName>
	</#if>
	<CheckDate>${Voucher.CheckDate}</CheckDate>
	<EVoucherType>${Voucher.EVoucherType}</EVoucherType>
	<AllNum>${Voucher.AllNum}</AllNum>
	<AllAmt>${Voucher.AllAmt}</AllAmt>
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
	<#list Voucher.DetailList as var1>
	<DetailList>
	  <#list var1.Detail as var>
	  <Detail>
		  <Id>${var.Id}</Id>
		  <VoucherNo>${var.VoucherNo}</VoucherNo>
		  <Amt>${var.Amt}</Amt>
		  <ProState>${var.ProState}</ProState>
		  <Hold1>${var.Hold1}</Hold1>
	  	  <Hold2>${var.Hold2}</Hold2>
	 </Detail>
	</#list> 
	</DetailList>
	</#list> 	
</Voucher>