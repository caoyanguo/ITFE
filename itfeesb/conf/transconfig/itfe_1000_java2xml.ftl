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
		<BatchHead1000>
			<BillOrg>${cfx.MSG.BatchHead1000.BillOrg}</BillOrg>
			<EntrustDate>${cfx.MSG.BatchHead1000.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead1000.PackNo}</PackNo>
			<TreCode>${cfx.MSG.BatchHead1000.TreCode}</TreCode>
			<ChangeNo>${cfx.MSG.BatchHead1000.ChangeNo}</ChangeNo>
			<AllNum>${cfx.MSG.BatchHead1000.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead1000.AllAmt}</AllAmt>
			<PayoutVouType>${cfx.MSG.BatchHead1000.PayoutVouType}</PayoutVouType>
		</BatchHead1000>
		<#list cfx.MSG.BillSend1000 as var1>
		<BillSend1000>
			<TraNo>${var1.TraNo}</TraNo>
			<VouNo>${var1.VouNo}</VouNo>
			<VouDate>${var1.VouDate}</VouDate>
			<PayerAcct>${var1.PayerAcct}</PayerAcct>
			<PayerName>${var1.PayerName}</PayerName>
			<#if var1.PayerAddr?exists>
			<PayerAddr>${var1.PayerAddr}</PayerAddr>
			</#if>
			<Amt>${var1.Amt}</Amt>
			<PayeeBankNo>${var1.PayeeBankNo}</PayeeBankNo>
			<PayeeOpBkNo>${var1.PayeeOpBkNo}</PayeeOpBkNo>
			<PayeeAcct>${var1.PayeeAcct}</PayeeAcct>
			<PayeeName>${var1.PayeeName}</PayeeName>
			<#if var1.PayReason?exists>
			<PayReason>${var1.PayReason}</PayReason>
			</#if>
			<#if var1.BudgetSubjectCode?exists>
			<BudgetSubjectCode>${var1.BudgetSubjectCode}</BudgetSubjectCode>
			</#if>
			<#if var1.AddWord?exists>
			<AddWord>${var1.AddWord}</AddWord>
			</#if>
			<OfYear>${var1.OfYear}</OfYear>
			<Flag>${var1.Flag}</Flag>
		</BillSend1000>
		</#list>
	</MSG>
</CFX>