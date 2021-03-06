<?xml version="1.0" encoding="GBK"?>
<CFX>
	<HEAD>
		<VER>${cfx.HEAD.VER}</VER>
		<SRC>${cfx.HEAD.SRC}</SRC>
		<DES>${cfx.HEAD.DES}</DES>
		<APP>${cfx.HEAD.APP}</APP>
		<MsgNo>${cfx.HEAD.MsgNo}</MsgNo>
		<MsgID>${cfx.HEAD.MsgID}</MsgID>
		<MsgRef>${cfx.HEAD.MsgRef}</MsgRef>
		<WorkDate>${cfx.HEAD.WorkDate}</WorkDate>
	</HEAD>
	<MSG>
		<BatchHead2102>
			<PayBkCode>${cfx.MSG.BatchHead2102.PayBkCode}</PayBkCode>
			<EntrustDate>${cfx.MSG.BatchHead2102.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead2102.PackNo}</PackNo>
			<OriTaxOrgCode>${cfx.MSG.BatchHead2102.OriTaxOrgCode}</OriTaxOrgCode>
			<OriEntrustDate>${cfx.MSG.BatchHead2102.OriEntrustDate}</OriEntrustDate>
			<OriPackNo>${cfx.MSG.BatchHead2102.OriPackNo}</OriPackNo>
			<AllNum>${cfx.MSG.BatchHead2102.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead2102.AllAmt}</AllAmt>
			<SuccNum>${cfx.MSG.BatchHead2102.SuccNum}</SuccNum>
			<SuccAmt>${cfx.MSG.BatchHead2102.SuccAmt}</SuccAmt>
			<Result>${cfx.MSG.BatchHead2102.Result}</Result>
			<AddWord>${cfx.MSG.BatchHead2102.AddWord}</AddWord>
		</BatchHead2102>
		<#list cfx.MSG.BatchReturn2102 as var1>
		<BatchReturn2102>
			<OriTraNo>${var1.OriTraNo}</OriTraNo>
			<TraAmt>${var1.TraAmt}</TraAmt>
			<TaxVouNo>${var1.TaxVouNo}</TaxVouNo>
			<TaxDate>${var1.TaxDate}</TaxDate>
			<Result>${var1.Result}</Result>
			<AddWord>${var1.AddWord}</AddWord>
		</BatchReturn2102>
		</#list>
	</MSG>
</CFX>