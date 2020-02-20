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
		<Reserve>${cfx.HEAD.Reserve}</Reserve>
	</HEAD>
	<MSG>
		<BatchHead2201>
			<AgentBnkCode>${cfx.MSG.BatchHead2201.AgentBnkCode}</AgentBnkCode>
			<FinOrgCode>${cfx.MSG.BatchHead2201.FinOrgCode}</FinOrgCode>
			<TreCode>${cfx.MSG.BatchHead2201.TreCode}</TreCode>
			<EntrustDate>${cfx.MSG.BatchHead2201.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead2201.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead2201.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead2201.AllAmt}</AllAmt>
			<PayoutVouType>${cfx.MSG.BatchHead2201.PayoutVouType}</PayoutVouType>
			<PayMode>${cfx.MSG.BatchHead2201.PayMode}</PayMode>
		</BatchHead2201>
		<#list cfx.MSG.Bill2201 as var1>
		<Bill2201>
			<TraNo>${var1.TraNo}</TraNo>
			<VouNo>${var1.VouNo}</VouNo>
			<VouDate>${var1.VouDate}</VouDate>
			<PayerAcct>${var1.PayerAcct}</PayerAcct>
			<PayerName>${var1.PayerName}</PayerName>
			<PayerAddr>${var1.PayerAddr}</PayerAddr>
			<PayeeAcct>${var1.PayeeAcct}</PayeeAcct>
			<PayeeName>${var1.PayeeName}</PayeeName>
			<PayeeAddr>${var1.PayeeAddr}</PayeeAddr>
			<PayeeOpnBnkNo>${var1.PayeeOpnBnkNo}</PayeeOpnBnkNo>
			<#if var1.AddWord?exists>
			<AddWord>${var1.AddWord}</AddWord>
			</#if>
			<BudgetType>${var1.BudgetType}</BudgetType>
			<TrimSign>${var1.TrimSign}</TrimSign>
			<OfYear>${var1.OfYear}</OfYear>
			<Amt>${var1.Amt}</Amt>
			<StatInfNum>${var1.StatInfNum}</StatInfNum>
			<#list var1.Detail2201 as var2>
			<Detail2201>
			    <SeqNo>${var2.SeqNo}</SeqNo>
			    <BdgOrgCode>${var2.BdgOrgCode}</BdgOrgCode>
			    <FuncSbtCode>${var2.FuncSbtCode}</FuncSbtCode>
			    <EcnomicSubjectCode>${var2.EcnomicSubjectCode}</EcnomicSubjectCode>
			    <Amt>${var2.Amt}</Amt>
			    <AcctProp>${var2.AcctProp}</AcctProp>
			</Detail2201>
			</#list>
		</Bill2201>
	</#list>
	</MSG>
</CFX>