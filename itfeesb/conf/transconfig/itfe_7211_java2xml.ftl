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
		<BatchHead7211>
			<TaxOrgCode>${cfx.MSG.BatchHead7211.TaxOrgCode}</TaxOrgCode>
			<EntrustDate>${cfx.MSG.BatchHead7211.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead7211.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead7211.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead7211.AllAmt}</AllAmt>
		</BatchHead7211>
		<TurnAccount7211>
			<BizType>${cfx.MSG.TurnAccount7211.BizType}</BizType>
			<FundSrlNo>${cfx.MSG.TurnAccount7211.FundSrlNo}</FundSrlNo>
			<#if cfx.MSG.TurnAccount7211.PayBnkNo?exists>
			<PayBnkNo>${cfx.MSG.TurnAccount7211.PayBnkNo}</PayBnkNo>
			</#if>
			<PayeeTreCode>${cfx.MSG.TurnAccount7211.PayeeTreCode}</PayeeTreCode>
			<#if cfx.MSG.TurnAccount7211.PayeeTreName?exists>
			<PayeeTreName>${cfx.MSG.TurnAccount7211.PayeeTreName}</PayeeTreName>
			</#if>
		</TurnAccount7211>
		<TaxBody7211>
			<#list cfx.MSG.TaxBody7211.TaxInfo7211 as var1>
			<TaxInfo7211>
				<Payment7211>
					<TraNo>${var1.Payment7211.TraNo}</TraNo>
					<TraAmt>${var1.Payment7211.TraAmt}</TraAmt>
					<PayOpBnkNo>${var1.Payment7211.PayOpBnkNo}</PayOpBnkNo>
					<#if var1.Payment7211.PayOpBnkName?exists>
					<PayOpBnkName>${var1.Payment7211.PayOpBnkName}</PayOpBnkName>
					</#if>
					<HandOrgName>${var1.Payment7211.HandOrgName}</HandOrgName>
					<PayAcct>${var1.Payment7211.PayAcct}</PayAcct>
				</Payment7211>
				<TaxVou7211>
					<TaxVouNo>${var1.TaxVou7211.TaxVouNo}</TaxVouNo>
					<BillDate>${var1.TaxVou7211.BillDate}</BillDate>
					<TaxPayCode>${var1.TaxVou7211.TaxPayCode}</TaxPayCode>
					<TaxPayName>${var1.TaxVou7211.TaxPayName}</TaxPayName>
					<BudgetType>${var1.TaxVou7211.BudgetType}</BudgetType>
					<TrimSign>${var1.TaxVou7211.TrimSign}</TrimSign>
					<#if var1.TaxVou7211.CorpCode?exists>
					<CorpCode>${var1.TaxVou7211.CorpCode}</CorpCode>
					</#if>
					<#if var1.TaxVou7211.CorpName?exists>
					<CorpName>${var1.TaxVou7211.CorpName}</CorpName>
					</#if>
					<#if var1.TaxVou7211.CorpType?exists>
					<CorpType>${var1.TaxVou7211.CorpType}</CorpType>
					</#if>
					<#if var1.TaxVou7211.Remark?exists>
					<Remark>${var1.TaxVou7211.Remark}</Remark>
					</#if>
					<#if var1.TaxVou7211.Remark1?exists>
					<Remark1>${var1.TaxVou7211.Remark1}</Remark1>
					</#if>
					<#if var1.TaxVou7211.Remark2?exists>
					<Remark2>${var1.TaxVou7211.Remark2}</Remark2>
					</#if>
				</TaxVou7211>
				<TaxType7211>
					<BudgetSubjectCode>${var1.TaxType7211.BudgetSubjectCode}</BudgetSubjectCode>
					<#if var1.TaxType7211.BudgetSubjectName?exists>
					<BudgetSubjectName>${var1.TaxType7211.BudgetSubjectName}</BudgetSubjectName>
					</#if>
					<LimitDate>${var1.TaxType7211.LimitDate}</LimitDate>
					<#if var1.TaxType7211.TaxTypeCode?exists>
					<TaxTypeCode>${var1.TaxType7211.TaxTypeCode}</TaxTypeCode>
					</#if>
					<#if var1.TaxType7211.TaxTypeName?exists>
					<TaxTypeName>${var1.TaxType7211.TaxTypeName}</TaxTypeName>
					</#if>
					<BudgetLevelCode>${var1.TaxType7211.BudgetLevelCode}</BudgetLevelCode>
					<BudgetLevelName>${var1.TaxType7211.BudgetLevelName}</BudgetLevelName>
					<TaxStartDate>${var1.TaxType7211.TaxStartDate}</TaxStartDate>
					<TaxEndDate>${var1.TaxType7211.TaxEndDate}</TaxEndDate>
					<ViceSign>${var1.TaxType7211.ViceSign}</ViceSign>
					<TaxType>${var1.TaxType7211.TaxType}</TaxType>
					<HandBookKind>${var1.TaxType7211.HandBookKind}</HandBookKind>
					<DetailNum>${var1.TaxType7211.DetailNum}</DetailNum>
					<#list var1.TaxType7211.SubjectList7211 as var2>
					<SubjectList7211>
						<DetailNo>${var2.DetailNo}</DetailNo>
						<TaxSubjectCode>${var2.TaxSubjectCode}</TaxSubjectCode>
						<TaxSubjectName>${var2.TaxSubjectName}</TaxSubjectName>
						<TaxNumber>${var2.TaxNumber}</TaxNumber>
						<TaxAmt>${var2.TaxAmt}</TaxAmt>
						<TaxRate>${var2.TaxRate}</TaxRate>
						<ExpTaxAmt>${var2.ExpTaxAmt}</ExpTaxAmt>
						<DiscountTaxAmt>${var2.DiscountTaxAmt}</DiscountTaxAmt>
						<FactTaxAmt>${var2.FactTaxAmt}</FactTaxAmt>
					</SubjectList7211>
					</#list>
				</TaxType7211>
			</TaxInfo7211>
			</#list>
		</TaxBody7211>
	</MSG>
</CFX>