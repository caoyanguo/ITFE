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
		<BatchHead1105>
			<TaxOrgCode>${cfx.MSG.BatchHead1105.TaxOrgCode}</TaxOrgCode>
			<EntrustDate>${cfx.MSG.BatchHead1105.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead1105.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead1105.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead1105.AllAmt}</AllAmt>
		</BatchHead1105>
		<#list cfx.MSG.CorrectBody1105 as var1>
		<CorrectBody1105>
			<CorrectInfo1105>
				<TraNo>${var1.CorrectInfo1105.TraNo}</TraNo>
				<#if var1.CorrectInfo1105.OriTraNo?exists>
				<OriTraNo>${var1.CorrectInfo1105.OriTraNo}</OriTraNo>
				</#if>
				<BillDate>${var1.CorrectInfo1105.BillDate}</BillDate>
				<#if var1.CorrectInfo1105.CorrVouNo?exists>
				<CorrVouNo>${var1.CorrectInfo1105.CorrVouNo}</CorrVouNo>
				</#if>
				<ElectroTaxVouNo>${var1.CorrectInfo1105.ElectroTaxVouNo}</ElectroTaxVouNo>
				<TrimSign>${var1.CorrectInfo1105.TrimSign}</TrimSign>
				<CorrReaCode>${var1.CorrectInfo1105.CorrReaCode}</CorrReaCode>
				<#if var1.CorrectInfo1105.Remark?exists>
				<Remark>${var1.CorrectInfo1105.Remark}</Remark>
				</#if>
			</CorrectInfo1105>
			<#if var1.CorrectOld1105?exists>
			<CorrectOld1105>
				<#if var1.CorrectOld1105.OriTaxVouNo?exists>
				<OriTaxVouNo>${var1.CorrectOld1105.OriTaxVouNo}</OriTaxVouNo>
				</#if>
				<OriBudgetType>${var1.CorrectOld1105.OriBudgetType}</OriBudgetType>
				<OriBudgetSubjectCode>${var1.CorrectOld1105.OriBudgetSubjectCode}</OriBudgetSubjectCode>
				<OriBudgetLevCode>${var1.CorrectOld1105.OriBudgetLevCode}</OriBudgetLevCode>
				<#if var1.CorrectOld1105.OriViceSign?exists>
				<OriViceSign>${var1.CorrectOld1105.OriViceSign}</OriViceSign>
				</#if>
				<OriTreCode>${var1.CorrectOld1105.OriTreCode}</OriTreCode>
				<OriRevAmt>${var1.CorrectOld1105.OriRevAmt}</OriRevAmt>
			</CorrectOld1105>
			</#if>
			<#if var1.CorrectNow1105?exists>
			<CorrectNow1105>
				<CurBudgetType>${var1.CorrectNow1105.CurBudgetType}</CurBudgetType>
				<CurBudgetSubjectCode>${var1.CorrectNow1105.CurBudgetSubjectCode}</CurBudgetSubjectCode>
				<CurBudgetLevCode>${var1.CorrectNow1105.CurBudgetLevCode}</CurBudgetLevCode>
				<#if var1.CorrectNow1105.CurViceSign?exists>
				<CurViceSign>${var1.CorrectNow1105.CurViceSign}</CurViceSign>
				</#if>
				<CurTreCode>${var1.CorrectNow1105.CurTreCode}</CurTreCode>
				<CurTraAmt>${var1.CorrectNow1105.CurTraAmt}</CurTraAmt>
			</CorrectNow1105>
			</#if>
		</CorrectBody1105>
		</#list>
	</MSG>
</CFX>
