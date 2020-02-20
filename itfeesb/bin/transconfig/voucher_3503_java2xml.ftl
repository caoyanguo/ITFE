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
	<AllAmt>${Voucher.AllAmt}</AllAmt>
	<XCheckResult>${Voucher.XCheckResult}</XCheckResult>
	<XCheckReason>${Voucher.XCheckReason}</XCheckReason>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<#list Voucher.DetailList as var1>
	<DetailList>
	  <#list var1.Detail as var>
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
	  	  <ClearAmt>${var.ClearAmt}</ClearAmt>
	  	  <XCheckResult>${var.XCheckResult}</XCheckResult>
		  <XCheckReason>${var.XCheckReason}</XCheckReason>	  	 
		  <Hold1>${var.Hold1}</Hold1>
	  	  <Hold2>${var.Hold2}</Hold2>
		  <Hold3>${var.Hold3}</Hold3>
		  <Hold4>${var.Hold4}</Hold4>		  
	 </Detail>
	</#list> 
	</DetailList>
	</#list> 
</Voucher>