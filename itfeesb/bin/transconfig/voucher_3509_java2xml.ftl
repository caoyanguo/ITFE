<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<VoucherCheckNo>${Voucher.VoucherCheckNo}</VoucherCheckNo>
	<ChildPackNum>${Voucher.ChildPackNum}</ChildPackNum>
	<CurPackNo>${Voucher.CurPackNo}</CurPackNo>
	<TreCode>${Voucher.TreCode}</TreCode>
	<ClearBankCode>${Voucher.ClearBankCode}</ClearBankCode>
	<ClearBankName>${Voucher.ClearBankName}</ClearBankName>
	<ClearAccNo>${Voucher.ClearAccNo}</ClearAccNo>
	<ClearAccNanme>${Voucher.ClearAccNanme}</ClearAccNanme>
	<BeginDate>${Voucher.BeginDate}</BeginDate>
	<EndDate>${Voucher.EndDate}</EndDate>
	<AllNum>${Voucher.AllNum}</AllNum>
	<AllAmt>${Voucher.AllAmt}</AllAmt>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<DetailList>
	  <#list Voucher.DetailList.Detail as var>
	  <Detail>
		<Id>${var.Id}</Id>
		<PayDetailId>${var.PayDetailId}</PayDetailId>
		<FundTypeCode>${var.FundTypeCode}</FundTypeCode>
		<FundTypeName>${var.FundTypeName}</FundTypeName>
		<PayeeAcctNo>${var.PayeeAcctNo}</PayeeAcctNo>
		<PayeeAcctName>${var.PayeeAcctName}</PayeeAcctName>
		<PayeeAcctBankName>${var.PayeeAcctBankName}</PayeeAcctBankName>
		<#if var.PayAcctNo?exists>
		<PayAcctNo>${var.PayAcctNo}</PayAcctNo>
		</#if>
		<#if var.PayAcctName?exists>
		<PayAcctName>${var.PayAcctName}</PayAcctName>
		</#if>
		<#if var.PayAcctBankName?exists>
		<PayAcctBankName>${var.PayAcctBankName}</PayAcctBankName>
		</#if>
		<#if var.AgencyCode?exists>
		<AgencyCode>${var.AgencyCode}</AgencyCode>
		</#if>
		<#if var.AgencyName?exists>
		<AgencyName>${var.AgencyName}</AgencyName>
		</#if>
		<IncomeSortCode>${var.IncomeSortCode}</IncomeSortCode>
		<IncomeSortName>${var.IncomeSortName}</IncomeSortName>
		<PayAmt>${var.PayAmt}</PayAmt>
		<Hold1>${var.Hold1}</Hold1>
		<Hold2>${var.Hold2}</Hold2>
		<Hold3>${var.Hold3}</Hold3>
		<Hold4>${var.Hold4}</Hold4>
	 </Detail>
	</#list> 
	</DetailList>  
</Voucher>