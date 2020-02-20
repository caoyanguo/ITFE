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
	<BillKind>${Voucher.BillKind}</BillKind>
	<FinOrgCode>${Voucher.FinOrgCode}</FinOrgCode>
	<TreCode>${Voucher.TreCode}</TreCode>
	<TreName>${Voucher.TreName}</TreName>
	<#if Voucher.PayTypeCode?exists>
	<PayTypeCode>${Voucher.PayTypeCode}</PayTypeCode>
	</#if>
	<#if Voucher.PayTypeName?exists>
	<PayTypeName>${Voucher.PayTypeName}</PayTypeName>
	</#if>
	<#if Voucher.BelongFlag?exists>
	<BelongFlag>${Voucher.BelongFlag}</BelongFlag>
	</#if>
	<BgtTypeCode>${Voucher.BgtTypeCode}</BgtTypeCode>
	<BgtTypeName>${Voucher.BgtTypeName}</BgtTypeName>
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
		<ExpFuncCode>${var.ExpFuncCode}</ExpFuncCode>
		<ExpFuncName>${var.ExpFuncName}</ExpFuncName>
		<CurPayAmt>${var.CurPayAmt}</CurPayAmt>
		<SumPayAmt>${var.SumPayAmt}</SumPayAmt>
		<Hold1>${var.Hold1}</Hold1>
		<Hold2>${var.Hold2}</Hold2>
		<Hold3>${var.Hold3}</Hold3>
		<Hold4>${var.Hold4}</Hold4>	  
	 </Detail>
	</#list> 
	</DetailList>  
</Voucher>