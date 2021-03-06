<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<VoucherCheckNo>${Voucher.VoucherCheckNo}</VoucherCheckNo>
	<ChildPackNum>${Voucher.ChildPackNum}</ChildPackNum>
	<CurPackNo>${Voucher.CurPackNo}</CurPackNo>
	<FundTypeCode>${Voucher.FundTypeCode}</FundTypeCode>
	<BelongFlag>${Voucher.BelongFlag}</BelongFlag>
	<BudgetLevelCode>${Voucher.BudgetLevelCode}</BudgetLevelCode>
	<BillKind>${Voucher.BillKind}</BillKind>
	<FinOrgCode>${Voucher.FinOrgCode}</FinOrgCode>
	<TreCode>${Voucher.TreCode}</TreCode>
	<TreName>${Voucher.TreName}</TreName>
	<BeginDate>${Voucher.BeginDate}</BeginDate>
	<EndDate>${Voucher.EndDate}</EndDate>
	<AllNum>${Voucher.AllNum}</AllNum>
	<AllAmt>${Voucher.AllAmt}</AllAmt>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<DetailList>
	  <#list Voucher.DetailList.Detail as var>
	  <Detail>
		<Id>${var.Id}</Id>
		<AdmDivCode>${var.AdmDivCode}</AdmDivCode>
		<StYear>${var.StYear}</StYear>
		<TaxOrgCode>${var.TaxOrgCode}</TaxOrgCode>
		<TaxOrgName>${var.TaxOrgName}</TaxOrgName>
		<BudgetType>${var.BudgetType}</BudgetType>
		<BudgetLevelCode>${var.BudgetLevelCode}</BudgetLevelCode>
		<BudgetSubjectCode>${var.BudgetSubjectCode}</BudgetSubjectCode>
		<BudgetSubjectName>${var.BudgetSubjectName}</BudgetSubjectName>
		<CurIncomeAmt>${var.CurIncomeAmt}</CurIncomeAmt>
		<SumIncomeAmt>${var.SumIncomeAmt}</SumIncomeAmt>
		<Hold1>${var.Hold1}</Hold1>
		<Hold2>${var.Hold2}</Hold2>
		<Hold3>${var.Hold3}</Hold3>
		<Hold4>${var.Hold4}</Hold4>	  
	 </Detail>
	</#list> 
	</DetailList>  
</Voucher>