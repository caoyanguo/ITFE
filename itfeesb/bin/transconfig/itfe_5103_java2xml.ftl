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
		<BatchHead5103>
			<TreCode>${cfx.MSG.BatchHead5103.TreCode}</TreCode>
			<BillOrg>${cfx.MSG.BatchHead5103.BillOrg}</BillOrg>
			<#if cfx.MSG.BatchHead5103.TransBankNo?exists>
			<TransBankNo>${cfx.MSG.BatchHead5103.TransBankNo}</TransBankNo>
			</#if>
			<EntrustDate>${cfx.MSG.BatchHead5103.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead5103.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead5103.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead5103.AllAmt}</AllAmt>
			<PayoutVouType>${cfx.MSG.BatchHead5103.PayoutVouType}</PayoutVouType>
		</BatchHead5103>
		<#list cfx.MSG.Bill5103 as var1>
		<Bill5103>
			<TraNo>${var1.TraNo}</TraNo>
			<VouNo>${var1.VouNo}</VouNo>
			<VouDate>${var1.VouDate}</VouDate>
			<SumAmt>${var1.SumAmt}</SumAmt>
			<BdgOrgCode>${var1.BdgOrgCode}</BdgOrgCode>
			<BudgetType>${var1.BudgetType}</BudgetType>
			<OfYear>${var1.OfYear}</OfYear>
			<OfMonth>${var1.OfMonth}</OfMonth>
			<RransactOrg>${var1.RransactOrg}</RransactOrg>
			<AmtKind>${var1.AmtKind}</AmtKind>
			<StatInfNum>${var1.StatInfNum}</StatInfNum>
			<#list var1.Detail5103 as var2>
			<Detail5103>
				<SeqNo>${var2.SeqNo}</SeqNo>
				<FuncBdgSbtCode>${var2.FuncBdgSbtCode}</FuncBdgSbtCode>
				<#if var2.EcnomicSubjectCode?exists>
				<EcnomicSubjectCode>${var2.EcnomicSubjectCode}</EcnomicSubjectCode>
				</#if>
				<Amt>${var2.Amt}</Amt>
				<AcctProp>${var2.AcctProp}</AcctProp>
			</Detail5103>
			</#list>
		</Bill5103>
		</#list>
	</MSG>
</CFX>