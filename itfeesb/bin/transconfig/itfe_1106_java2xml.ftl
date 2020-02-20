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
		<BatchHead1106>
			<TaxOrgCode>${cfx.MSG.BatchHead1106.TaxOrgCode}</TaxOrgCode>
			<EntrustDate>${cfx.MSG.BatchHead1106.EntrustDate}</EntrustDate>
			<PackNo>${cfx.MSG.BatchHead1106.PackNo}</PackNo>
			<AllNum>${cfx.MSG.BatchHead1106.AllNum}</AllNum>
			<AllAmt>${cfx.MSG.BatchHead1106.AllAmt}</AllAmt>
		</BatchHead1106>
		<#list cfx.MSG.FreeInfo1106 as var1>
		<FreeInfo1106>
			<TraNo>${var1.TraNo}</TraNo>
			<BillDate>${var1.BillDate}</BillDate>
			<#if var1.FreeVouNo?exists>
			<FreeVouNo>${var1.FreeVouNo}</FreeVouNo>
			</#if>
			<#if var1.ElectroTaxVouNo?exists>
			<ElectroTaxVouNo>${var1.ElectroTaxVouNo}</ElectroTaxVouNo>
			</#if>
			<FreePluType>${var1.FreePluType}</FreePluType>
			<FreePluSubjectCode>${var1.FreePluSubjectCode}</FreePluSubjectCode>
			<FreePluLevCode>${var1.FreePluLevCode}</FreePluLevCode>
			<#if var1.FreePluSign?exists>
			<FreePluSign>${var1.FreePluSign}</FreePluSign>
			</#if>
			<FreePluPTreCode>${var1.FreePluPTreCode}</FreePluPTreCode>
			<FreePluAmt>${var1.FreePluAmt}</FreePluAmt>
			<FreeMiKind>${var1.FreeMiKind}</FreeMiKind>
			<FreeMiSubject>${var1.FreeMiSubject}</FreeMiSubject>
			<FreeMiLevel>${var1.FreeMiLevel}</FreeMiLevel>
			<#if var1.ViceSign?exists>
			<FreeMiSign>${var1.FreeMiSign}</FreeMiSign>
			</#if>
			<FreeMiPTre>${var1.FreeMiPTre}</FreeMiPTre>
			<FreeMiAmt>${var1.FreeMiAmt}</FreeMiAmt>
			<TrimSign>${var1.TrimSign}</TrimSign>
			<CorpCode>${var1.CorpCode}</CorpCode>
		</FreeInfo1106>
		</#list>
	</MSG>
</CFX>
