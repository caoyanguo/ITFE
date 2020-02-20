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
		<BatchHead1104>
			<TaxOrgCode>${cfx.MSG.BatchHead1104.TaxOrgCode}</TaxOrgCode>
			<EntrustDate>${cfx.MSG.BatchHead1104.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead1104.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead1104.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead1104.AllAmt}</AllAmt>
		</BatchHead1104>
		<#list cfx.MSG.RetTreasury1104 as var1>
		<RetTreasury1104>
			<TraNo>${var1.TraNo}</TraNo>
			<DrawBackTreCode>${var1.DrawBackTreCode}</DrawBackTreCode>
			<#if var1.PayeeBankNo?exists>
			<PayeeBankNo>${var1.PayeeBankNo}</PayeeBankNo>
			</#if>
			<PayeeOpBkCode>${var1.PayeeOpBkCode}</PayeeOpBkCode>
			<PayeeAcct>${var1.PayeeAcct}</PayeeAcct>
			<PayeeName>${var1.PayeeName}</PayeeName>
			<#if var1.TaxPayCode?exists>
			<TaxPayCode>${var1.TaxPayCode}</TaxPayCode>
			</#if>
			<#if var1.TaxPayName?exists>
			<TaxPayName>${var1.TaxPayName}</TaxPayName>
			</#if>
			<DrawBackVouNo>${var1.DrawBackVouNo}</DrawBackVouNo>
			<#if var1.ElectroTaxVouNo?exists>
			<ElectroTaxVouNo>${var1.ElectroTaxVouNo}</ElectroTaxVouNo>
			</#if>
			<#if var1.OriTaxVouNo?exists>
			<OriTaxVouNo>${var1.OriTaxVouNo}</OriTaxVouNo>
			</#if>
			<TraAmt>${var1.TraAmt}</TraAmt>
			<BillDate>${var1.BillDate}</BillDate>
			<#if var1.CorpCode?exists>
			<CorpCode>${var1.CorpCode}</CorpCode>
			</#if>
			<BudgetType>${var1.BudgetType}</BudgetType>
			<BudgetSubjectCode>${var1.BudgetSubjectCode}</BudgetSubjectCode>
			<BudgetLevelCode>${var1.BudgetLevelCode}</BudgetLevelCode>
			<TrimSign>${var1.TrimSign}</TrimSign>
			<#if var1.ViceSign?exists>
			<ViceSign>${var1.ViceSign}</ViceSign>
			</#if>
			<DrawBackReasonCode>${var1.DrawBackReasonCode}</DrawBackReasonCode>
			<#if var1.DrawBackVou?exists>
			<DrawBackVou>${var1.DrawBackVou}</DrawBackVou>
			</#if>
			<#if var1.ExamOrg?exists>
			<ExamOrg>${var1.ExamOrg}</ExamOrg>
			</#if>
			<#if var1.AuthNo?exists>
			<AuthNo>${var1.AuthNo}</AuthNo>
			</#if>
			<#if var1.TransType?exists>
			<TransType>${var1.TransType}</TransType>
			</#if>
		</RetTreasury1104>
		</#list>
	</MSG>
</CFX>
