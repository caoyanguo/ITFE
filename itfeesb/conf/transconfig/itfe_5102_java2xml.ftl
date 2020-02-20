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
		<BatchHead5102>
			<TreCode>${cfx.MSG.BatchHead5102.TreCode}</TreCode>
			<BillOrg>${cfx.MSG.BatchHead5102.BillOrg}</BillOrg>
			<#if cfx.MSG.BatchHead5102.TransBankNo?exists>
			<TransBankNo>${cfx.MSG.BatchHead5102.TransBankNo}</TransBankNo>
			</#if>
			<EntrustDate>${cfx.MSG.BatchHead5102.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead5102.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead5102.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead5102.AllAmt}</AllAmt>
			<PayoutVouType>${cfx.MSG.BatchHead5102.PayoutVouType}</PayoutVouType>
		</BatchHead5102>
		<#list cfx.MSG.Bill5102 as var1>
		<Bill5102>
			<TraNo>${var1.TraNo}</TraNo>
			<VouNo>${var1.VouNo}</VouNo>
			<VouDate>${var1.VouDate}</VouDate>
			<SumAmt>${var1.SumAmt}</SumAmt>
			<BudgetType>${var1.BudgetType}</BudgetType>
			<OfYear>${var1.OfYear}</OfYear>
			<RransactOrg>${var1.RransactOrg}</RransactOrg>
			<AmtKind>${var1.AmtKind}</AmtKind>
			<StatInfNum>${var1.StatInfNum}</StatInfNum>
			<#list var1.Detail5102 as var2>
			<Detail5102>
				<SeqNo>${var2.SeqNo}</SeqNo>
				<BdgOrgCode>${var2.BdgOrgCode}</BdgOrgCode>
				<FuncBdgSbtCode>${var2.FuncBdgSbtCode}</FuncBdgSbtCode>
				<#if var2.EcnomicSubjectCode?exists>
				<EcnomicSubjectCode>${var2.EcnomicSubjectCode}</EcnomicSubjectCode>
				</#if>
				<Amt>${var2.Amt}</Amt>
			</Detail5102>
			</#list>
		</Bill5102>
		</#list>
	</MSG>
</CFX>
