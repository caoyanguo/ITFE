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
		<BatchHead3135>
			<TreCode>${cfx.MSG.BatchHead3135.TreCode}</TreCode>
			<BillOrg>${cfx.MSG.BatchHead3135.BillOrg}</BillOrg>
			<EntrustDate>${cfx.MSG.BatchHead3135.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead3135.PackNo}</PackNo>
			<OriEntrustDate>${cfx.MSG.BatchHead3135.OriEntrustDate}</OriEntrustDate>
			<OriPackNo>${cfx.MSG.BatchHead3135.OriPackNo}</OriPackNo>
			<AllNum>${cfx.MSG.BatchHead3135.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead3135.AllAmt}</AllAmt>
			<PayoutVouType>${cfx.MSG.BatchHead3135.PayoutVouType}</PayoutVouType>
			<PayMode>${cfx.MSG.BatchHead3135.PayMode}</PayMode>
		</BatchHead3135>
		<#list cfx.MSG.BatchReturn3135 as var1>
		<BatchReturn3135>
			<VouNo>${var1.VouNo}</VouNo>
			<VouDate>${var1.VouDate}</VouDate>
			<OriTraNo>${var1.OriTraNo}</OriTraNo>
			<Amt>${var1.Amt}</Amt>
			<AcctDate>${var1.AcctDate}</AcctDate>
			<Result>${var1.Result}</Result>
			<Description>${var1.Description}</Description>
		</BatchReturn3135>
		</#list>
	</MSG>
</CFX>


