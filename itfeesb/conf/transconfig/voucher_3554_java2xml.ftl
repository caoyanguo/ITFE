<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>	
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode> 
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<EVoucherType>${Voucher.EVoucherType}</EVoucherType> 
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>	
	<BeginDate>${Voucher.BeginDate}</BeginDate>
	<EndDate>${Voucher.EndDate}</EndDate>
	<AllNum>${Voucher.AllNum}</AllNum>
	<AllAmt>${Voucher.AllAmt}</AllAmt>
	<PayBankCode>${Voucher.PayBankCode}</PayBankCode>
	<PayBankName>${Voucher.PayBankName}</PayBankName>
	<PayBankNo>${Voucher.PayBankNo}</PayBankNo>
	<PayTypeCode>${Voucher.PayTypeCode}</PayTypeCode>
	<PayTypeName>${Voucher.PayTypeName}</PayTypeName>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<#list Voucher.DetailList as var1>
	<DetailList>
	  <#list var1.Detail as var>
	  <Detail>
		  <SupDepCode>${var.SupDepCode}</SupDepCode>
		  <SupDepName>${var.SupDepName}</SupDepName>
		  <ExpFuncCode>${var.ExpFuncCode}</ExpFuncCode>
		  <ExpFuncName>${var.ExpFuncName}</ExpFuncName> 
	  	  <MonthAmt>${var.MonthAmt}</MonthAmt>		 
	  	  <XCheckResult>${var.XCheckResult}</XCheckResult>
	  	  <XCurFinReckMoney>${var.XCurFinReckMoney}</XCurFinReckMoney>
	  	  <XDiffMoney>${var.XDiffMoney}</XDiffMoney>
	  	  <Remark>${var.Remark}</Remark>	  	 
		  <Hold1>${var.Hold1}</Hold1>
	  	  <Hold2>${var.Hold2}</Hold2>			  
	 </Detail>
	</#list> 
	</DetailList>
	</#list> 
</Voucher>