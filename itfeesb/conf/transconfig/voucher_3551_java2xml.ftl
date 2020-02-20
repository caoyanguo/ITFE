<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>	
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode> 
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<EVoucherType>${Voucher.EVoucherType}</EVoucherType> 
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<TreCode>${Voucher.TreCode}</TreCode>
	<TreName>${Voucher.TreName}</TreName>
	<BeginDate>${Voucher.BeginDate}</BeginDate>
	<EndDate>${Voucher.EndDate}</EndDate>
	<AllNum>${Voucher.AllNum}</AllNum>
	<AllAmt>${Voucher.AllAmt}</AllAmt>
	<PayBankCode>${Voucher.PayBankCode}</PayBankCode>
	<PayBankName>${Voucher.PayBankName}</PayBankName>
	<PayBankNo>${Voucher.PayBankNo}</PayBankNo>
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
		  <PreDateMoney>${var.PreDateMoney}</PreDateMoney>
		  <CurAddMoney>${var.CurAddMoney}</CurAddMoney>
		  <CurReckMoney>${var.CurReckMoney}</CurReckMoney>
		  <CurDateMoney>${var.CurDateMoney}</CurDateMoney>
	  	  <XCheckResult>${var.XCheckResult}</XCheckResult>
	  	  <XCurFinReckMoney>${var.XCurFinReckMoney}</XCurFinReckMoney>
	  	  <XCurFinAddMoney>${var.XCurFinAddMoney}</XCurFinAddMoney>
	  	  <Remark>${var.Remark}</Remark>	  	 
		  <Hold1>${var.Hold1}</Hold1>
	  	  <Hold2>${var.Hold2}</Hold2>
		  <Hold3>${var.Hold3}</Hold3>
		  <Hold4>${var.Hold4}</Hold4>		  
	 </Detail>
	</#list> 
	</DetailList>
	</#list> 
</Voucher>