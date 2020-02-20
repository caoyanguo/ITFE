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
	<#if Voucher.PreDateMoney?exists>
	<PreDateMoney>${Voucher.PreDateMoney}</PreDateMoney>
	</#if>
	<#if Voucher.ClearAmt?exists>
	<ClearAmt>${Voucher.ClearAmt}</ClearAmt>
	</#if>
	<#if Voucher.CurReckMoney?exists>
	<CurReckMoney>${Voucher.CurReckMoney}</CurReckMoney>
	</#if>
	<#if Voucher.CurDateMoney?exists>
	<CurDateMoney>${Voucher.CurDateMoney}</CurDateMoney>
	</#if>
	<AllNum>${Voucher.AllNum}</AllNum>
	<AllAmt>${Voucher.AllAmt}</AllAmt>
	<#if Voucher.PayTypeCode?exists>
	<PayTypeCode>${Voucher.PayTypeCode}</PayTypeCode>
	</#if>
	<#if Voucher.PayTypeName?exists>
	<PayTypeName>${Voucher.PayTypeName}</PayTypeName>
	</#if>
	<XCheckResult></XCheckResult>
	<XDiffNum></XDiffNum>
	<XCheckReason></XCheckReason>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<DetailList>
	  <#list Voucher.DetailList.Detail as var>
	  <Detail>
		<Id>${var.Id}</Id>
		<SupDepCode>${var.SupDepCode}</SupDepCode>
		<SupDepName>${var.SupDepName}</SupDepName>
		<FundTypeCode>${var.FundTypeCode}</FundTypeCode>
		<FundTypeName>${var.FundTypeName}</FundTypeName>
		<PayBankCode>${var.PayBankCode}</PayBankCode>
		<PayBankName>${var.PayBankName}</PayBankName>
		<PayBankNo>${var.PayBankNo}</PayBankNo>
		<ExpFuncCode>${var.ExpFuncCode}</ExpFuncCode>
		<ExpFuncName>${var.ExpFuncName}</ExpFuncName>
		<ProCatCode>${var.ProCatCode}</ProCatCode>
		<ProCatName>${var.ProCatName}</ProCatName>
		<PayTypeCode>${var.PayTypeCode}</PayTypeCode>
		<PayTypeName>${var.PayTypeName}</PayTypeName>
		<PreDateMoney>${var.PreDateMoney}</PreDateMoney>
		<ClearAmt>${var.ClearAmt}</ClearAmt>
		<CurReckMoney>${var.CurReckMoney}</CurReckMoney>
		<CurDateMoney>${var.CurDateMoney}</CurDateMoney>
		<XPreDateMoney></XPreDateMoney>
		<XClearAmt></XClearAmt>
		<XCurReckMoney></XCurReckMoney>
		<XCurDateMoney></XCurDateMoney>
		<XCheckResult></XCheckResult>
		<XCheckReason></XCheckReason>
		<Hold1>${var.Hold1}</Hold1>
		<Hold2>${var.Hold2}</Hold2>
		<Hold3>${var.Hold3}</Hold3>
		<Hold4>${var.Hold4}</Hold4>	  
	 </Detail>
	</#list> 
	</DetailList>  
</Voucher>