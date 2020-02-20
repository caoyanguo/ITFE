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
		<BatchHead5101>
			<BillOrg>${cfx.MSG.BatchHead5101.BillOrg}</BillOrg>
			<PayeeBankNo>${cfx.MSG.BatchHead5101.PayeeBankNo}</PayeeBankNo>
			<EntrustDate>${cfx.MSG.BatchHead5101.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead5101.PackNo}</PackNo>
			<TreCode>${cfx.MSG.BatchHead5101.TreCode}</TreCode>
			<AllNum>${cfx.MSG.BatchHead5101.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead5101.AllAmt}</AllAmt>
			<PayoutVouType>${cfx.MSG.BatchHead5101.PayoutVouType}</PayoutVouType>
		</BatchHead5101>
		<#list cfx.MSG.Bill5101 as var1>
		<Bill5101>
			<TraNo>${var1.TraNo}</TraNo>
			<VouNo>${var1.VouNo}</VouNo>
			<VouDate>${var1.VouDate}</VouDate>
			<PayerAcct>${var1.PayerAcct}</PayerAcct>
			<PayerName>${var1.PayerName}</PayerName>
			<#if var1.PayerAddr?exists>
			<PayerAddr>${var1.PayerAddr}</PayerAddr>
			</#if>
			<Amt>${var1.Amt}</Amt>
			<PayeeOpBkNo>${var1.PayeeOpBkNo}</PayeeOpBkNo>
			<PayeeAcct>${var1.PayeeAcct}</PayeeAcct>
			<PayeeName>${var1.PayeeName}</PayeeName>
			<#if var1.AddWord?exists>
			<AddWord>${var1.AddWord}</AddWord>
			</#if>
			<BdgOrgCode>${var1.BdgOrgCode}</BdgOrgCode>
			<BudgetOrgName>${var1.BudgetOrgName}</BudgetOrgName>
			<OfYear>${var1.OfYear}</OfYear>
			<BudgetType>${var1.BudgetType}</BudgetType>
			<TrimSign>${var1.TrimSign}</TrimSign>
			<StatInfNum>${var1.StatInfNum}</StatInfNum>
			<#list var1.Detail5101 as var2>
			<Detail5101>
				<SeqNo>${var2.SeqNo}</SeqNo>
				<FuncBdgSbtCode>${var2.FuncBdgSbtCode}</FuncBdgSbtCode>
				<#if var2.EcnomicSubjectCode?exists>
				<EcnomicSubjectCode>${var2.EcnomicSubjectCode}</EcnomicSubjectCode>
				</#if>
				<#if var2.BudgetPrjCode?exists>
				<BudgetPrjCode>${var2.BudgetPrjCode}</BudgetPrjCode>
				</#if>
				<Amt>${var2.Amt}</Amt>
			</Detail5101>
			</#list>
		</Bill5101>
		</#list>
	</MSG>
</CFX>