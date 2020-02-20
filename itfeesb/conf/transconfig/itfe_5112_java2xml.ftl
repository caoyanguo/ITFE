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
		<BatchHead5112>
			<TreCode>${cfx.MSG.BatchHead5112.TreCode}</TreCode>
			<BillOrg>${cfx.MSG.BatchHead5112.BillOrg}</BillOrg>
			<PayeeBankNo>${cfx.MSG.BatchHead5112.PayeeBankNo}</PayeeBankNo>
			<PayerAcct>${cfx.MSG.BatchHead5112.PayerAcct}</PayerAcct>
			<PayerName>${cfx.MSG.BatchHead5112.PayerName}</PayerName>
			<#if cfx.MSG.BatchHead5112.PayerAddr?exists>
			<PayerAddr>${cfx.MSG.BatchHead5112.PayerAddr}</PayerAddr>
			</#if>
			<EntrustDate>${cfx.MSG.BatchHead5112.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead5112.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead5112.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead5112.AllAmt}</AllAmt>
			<PayoutVouType>${cfx.MSG.BatchHead5112.PayoutVouType}</PayoutVouType>
		</BatchHead5112>
		<Bill5112>
			<VouNo>${cfx.MSG.Bill5112.VouNo}</VouNo>
			<VouDate>${cfx.MSG.Bill5112.VouDate}</VouDate>
			<#if cfx.MSG.Bill5112.AddWord?exists>
			<AddWord>${cfx.MSG.Bill5112.AddWord}</AddWord>
			</#if>
			<BudgetType>${cfx.MSG.Bill5112.BudgetType}</BudgetType>
			<BdgOrgCode>${cfx.MSG.Bill5112.BdgOrgCode}</BdgOrgCode>
			<FuncSbtCode>${cfx.MSG.Bill5112.FuncSbtCode}</FuncSbtCode>
			<#if cfx.MSG.Bill5112.EcnomicSubjectCode?exists>
			<EcnomicSubjectCode>${cfx.MSG.Bill5112.EcnomicSubjectCode}</EcnomicSubjectCode>
			</#if>
			<Amt>${cfx.MSG.Bill5112.Amt}</Amt>
			<#list cfx.MSG.Bill5112.Detail5112 as var2>
			<Detail5112>
				<TraNo>${var2.TraNo}</TraNo>
				<PayeeAcct>${var2.PayeeAcct}</PayeeAcct>
				<PayeeName>${var2.PayeeName}</PayeeName>
				<#if var2.PayeeAddr?exists>
				<PayeeAddr>${var2.PayeeAddr}</PayeeAddr>
				</#if>
				<PayeeOpnBnkNo>${var2.PayeeOpnBnkNo}</PayeeOpnBnkNo>
				<Amt>${var2.Amt}</Amt>
			</Detail5112>
			</#list>
		</Bill5112>
	</MSG>
</CFX>
