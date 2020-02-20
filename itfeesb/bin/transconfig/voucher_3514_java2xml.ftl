<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<AcctNo>${Voucher.AcctNo}</AcctNo>
	<AcctName>${Voucher.AcctName}</AcctName>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<DetailList>
	  <#list Voucher.DetailList.Detail as var>
	  <Detail>
		<Id>${var.Id}</Id>
		<VoucherBillId>${var.VoucherBillId}</VoucherBillId>
		<VoucherNo>${var.VoucherNo}</VoucherNo>
		<VouDate>${var.VouDate}</VouDate>
		<DebitAcctAmt>${var.DebitAcctAmt}</DebitAcctAmt>
		<LoanAcctAmt>${var.LoanAcctAmt}</LoanAcctAmt>
		<CurDateMoney>${var.CurDateMoney}</CurDateMoney>
		<TypeName>${var.TypeName}</TypeName>
		<Remark>${var.Remark}</Remark>
		<Hold1>${var.Hold1}</Hold1>
		<Hold2>${var.Hold2}</Hold2>
		<Hold3>${var.Hold3}</Hold3>
		<Hold4>${var.Hold4}</Hold4>	  
	 </Detail>
	</#list> 
	</DetailList>  
</Voucher>