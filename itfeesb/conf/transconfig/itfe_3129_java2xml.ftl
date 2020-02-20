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
		<BatchHead3129>
			<FinOrgCode>${cfx.MSG.BatchHead3129.FinOrgCode}</FinOrgCode>
			<ApplyDate>${cfx.MSG.BatchHead3129.ApplyDate}</ApplyDate>
			<PackNo>${cfx.MSG.BatchHead3129.PackNo}</PackNo>
			<TreCode>${cfx.MSG.BatchHead3129.TreCode}</TreCode>
			<#if cfx.MSG.BatchHead3129.TreName?exists>
			<TreName>${cfx.MSG.BatchHead3129.TreName}</TreName>
			</#if>
			<AllNum>${cfx.MSG.BatchHead3129.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead3129.AllAmt}</AllAmt>
			<ChildPackNum>${cfx.MSG.BatchHead3129.ChildPackNum}</ChildPackNum>
			<CurPackNo>${cfx.MSG.BatchHead3129.CurPackNo}</CurPackNo>
			<CurPackNum>${cfx.MSG.BatchHead3129.CurPackNum}</CurPackNum>
			<CurPackAmt>${cfx.MSG.BatchHead3129.CurPackAmt}</CurPackAmt>
		</BatchHead3129>
		<TaxBody3129>
			<#list cfx.MSG.TaxBody3129.TaxInfo3129 as var1>
			<TaxInfo3129>
				<TaxVou3129>
					<TaxOrgCode>${var1.TaxVou3129.TaxOrgCode}</TaxOrgCode>
					<#if var1.TaxVou3129.PayBnkNo?exists>
					<PayBnkNo>${var1.TaxVou3129.PayBnkNo}</PayBnkNo>
					</#if>
					<TraNo>${var1.TaxVou3129.TraNo}</TraNo>
					<OriMsgNo>${var1.TaxVou3129.OriMsgNo}</OriMsgNo>
					<TraAmt>${var1.TaxVou3129.TraAmt}</TraAmt>
					<#if var1.TaxVou3129.PayerOpBkNo?exists>
					<PayerOpBkNo>${var1.TaxVou3129.PayerOpBkNo}</PayerOpBkNo>
					</#if>
					<#if var1.TaxVou3129.PayerOpBkName?exists>
					<PayerOpBkName>${var1.TaxVou3129.PayerOpBkName}</PayerOpBkName>
					</#if>
					<HandOrgName>${var1.TaxVou3129.HandOrgName}</HandOrgName>
					<#if var1.TaxVou3129.PayAcct?exists>
					<PayAcct>${var1.TaxVou3129.PayAcct}</PayAcct>
					</#if>
					<TaxVouNo>${var1.TaxVou3129.TaxVouNo}</TaxVouNo>
					<BillDate>${var1.TaxVou3129.BillDate}</BillDate>
					<TaxPayCode>${var1.TaxVou3129.TaxPayCode}</TaxPayCode>
					<TaxPayName>${var1.TaxVou3129.TaxPayName}</TaxPayName>
					<BudgetType>${var1.TaxVou3129.BudgetType}</BudgetType>
					<TrimSign>${var1.TaxVou3129.TrimSign}</TrimSign>
					<CorpCode>${var1.TaxVou3129.CorpCode}</CorpCode>
					<CorpName>${var1.TaxVou3129.CorpName}</CorpName>
					<#if var1.TaxVou3129.CorpType?exists>
					<CorpType>${var1.TaxVou3129.CorpType}</CorpType>
					</#if>
					<BudgetSubjectCode>${var1.TaxVou3129.BudgetSubjectCode}</BudgetSubjectCode>
					<#if var1.TaxVou3129.BudgetSubjectName?exists>
					<BudgetSubjectName>${var1.TaxVou3129.BudgetSubjectName}</BudgetSubjectName>
					</#if>
					<LimitDate>${var1.TaxVou3129.LimitDate}</LimitDate>
					<#if var1.TaxVou3129.TaxTypeCode?exists>
					<TaxTypeCode>${var1.TaxVou3129.TaxTypeCode}</TaxTypeCode>
					</#if>
					<#if var1.TaxVou3129.TaxTypeName?exists>
					<TaxTypeName>${var1.TaxVou3129.TaxTypeName}</TaxTypeName>
					</#if>
					<BudgetLevelCode>${var1.TaxVou3129.BudgetLevelCode}</BudgetLevelCode>
					<#if var1.TaxVou3129.BudgetLevelName?exists>
					<BudgetLevelName>${var1.TaxVou3129.BudgetLevelName}</BudgetLevelName>
					</#if>
					<TaxStartDate>${var1.TaxVou3129.TaxStartDate}</TaxStartDate>
					<TaxEndDate>${var1.TaxVou3129.TaxEndDate}</TaxEndDate>
					<#if var1.TaxVou3129.ViceSign?exists>
					<ViceSign>${var1.TaxVou3129.ViceSign}</ViceSign>
					</#if>
					<TaxType>${var1.TaxVou3129.Remark}</TaxType>
					<#if var1.TaxVou3129.PayAcct?exists>
					<Remark>${var1.TaxVou3129.Remark}</Remark>
					</#if>
					<#if var1.TaxVou3129.Remark1?exists>
					<Remark1>${var1.TaxVou3129.Remark1}</Remark1>
					</#if>
					<#if var1.TaxVou3129.Remark2?exists>
					<Remark2>${var1.TaxVou3129.Remark2}</Remark2>
					</#if>
					<OpStat>${var1.TaxVou3129.OpStat}</OpStat>
				</TaxVou3129>
			</TaxInfo3129>
			</#list>
		</TaxBody3129>
	</MSG>
</CFX>