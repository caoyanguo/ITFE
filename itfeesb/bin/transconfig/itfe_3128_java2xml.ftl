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
		<BillHead3128>
			<FinOrgCode>${cfx.MSG.BillHead3128.FinOrgCode}</FinOrgCode>
			<ReportDate>${cfx.MSG.BillHead3128.ReportDate}</ReportDate>
		</BillHead3128>
		<#if (cfx.MSG.NrBudget3128)?exists>
		<NrBudget3128>
			<#list cfx.MSG.NrBudget3128.NrBudgetBill3128 as var1>
			<NrBudgetBill3128>
				<TaxOrgCode>${var1.TaxOrgCode}</TaxOrgCode>
				<TreCode>${var1.TreCode}</TreCode>
				<BudgetType>${var1.BudgetType}</BudgetType>
				<BudgetLevelCode>${var1.BudgetLevelCode}</BudgetLevelCode>
				<BudgetSubjectCode>${var1.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var1.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var1.DayAmt}</DayAmt>
				<TenDayAmt>${var1.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var1.MonthAmt}</MonthAmt>
				<QuarterAmt>${var1.QuarterAmt}</QuarterAmt>
				<YearAmt>${var1.YearAmt}</YearAmt>
			</NrBudgetBill3128>
			</#list>
		</NrBudget3128>
		</#if>
		<#if (cfx.MSG.NrDrawBack3128)?exists>
		<NrDrawBack3128>
			<#list cfx.MSG.NrDrawBack3128.NrDrawBackBill3128 as var2>
			<NrDrawBackBill3128>
				<TaxOrgCode>${var2.TaxOrgCode}</TaxOrgCode>
				<TreCode>${var2.TreCode}</TreCode>
				<BudgetType>${var2.BudgetType}</BudgetType>
				<BudgetLevelCode>${var2.BudgetLevelCode}</BudgetLevelCode>
				<BudgetSubjectCode>${var2.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var2.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var2.DayAmt}</DayAmt>
				<TenDayAmt>${var2.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var2.MonthAmt}</MonthAmt>
				<QuarterAmt>${var2.QuarterAmt}</QuarterAmt>
				<YearAmt>${var2.YearAmt}</YearAmt>
			</NrDrawBackBill3128>
			</#list>
		</NrDrawBack3128>
		</#if>
		<#if (cfx.MSG.NrRemove3128)?exists>
		<NrRemove3128>
			<#list cfx.MSG.NrRemove3128.NrRemoveBill3128 as var3>
			<NrRemoveBill3128>
				<TaxOrgCode>${var3.TaxOrgCode}</TaxOrgCode>
				<TreCode>${var3.TreCode}</TreCode>
				<BudgetType>${var3.BudgetType}</BudgetType>
				<BudgetLevelCode>${var3.BudgetLevelCode}</BudgetLevelCode>
				<BudgetSubjectCode>${var3.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var3.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var3.DayAmt}</DayAmt>
				<TenDayAmt>${var3.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var3.MonthAmt}</MonthAmt>
				<QuarterAmt>${var3.QuarterAmt}</QuarterAmt>
				<YearAmt>${var3.YearAmt}</YearAmt>
			</NrRemoveBill3128>
			</#list>
		</NrRemove3128>
		</#if>
		<#if (cfx.MSG.Amount3128)?exists>
		<Amount3128>
			<#list cfx.MSG.Amount3128.AmountBill3128 as var4>
			<AmountBill3128>
				<TreCode>${var4.TreCode}</TreCode>
				<BudgetType>${var4.BudgetType}</BudgetType>
				<BudgetLevelCode>${var4.BudgetLevelCode}</BudgetLevelCode>
				<BudgetSubjectCode>${var4.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var4.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var4.DayAmt}</DayAmt>
				<TenDayAmt>${var4.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var4.MonthAmt}</MonthAmt>
				<QuarterAmt>${var4.QuarterAmt}</QuarterAmt>
				<YearAmt>${var4.YearAmt}</YearAmt>
			</AmountBill3128>
			</#list>
		</Amount3128>
		</#if>
		<#if (cfx.MSG.NrShare3128)?exists>
		<NrShare3128>
			<#list cfx.MSG.NrShare3128.NrShareBill3128 as var5>
			<NrShareBill3128>
				<TaxOrgCode>${var5.TaxOrgCode}</TaxOrgCode>
				<BudgetType>${var5.BudgetType}</BudgetType>
				<TreCode>${var5.TreCode}</TreCode>
				<BudgetLevelCode>${var5.BudgetLevelCode}</BudgetLevelCode>
				<DivideGroup>${var5.DivideGroup}</DivideGroup>
				<BudgetSubjectCode>${var5.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var5.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var5.DayAmt}</DayAmt>
				<TenDayAmt>${var5.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var5.MonthAmt}</MonthAmt>
				<QuarterAmt>${var5.QuarterAmt}</QuarterAmt>
				<YearAmt>${var5.YearAmt}</YearAmt>
			</NrShareBill3128>
			</#list>
		</NrShare3128>
		</#if>
		<#if (cfx.MSG.TrBudget3128)?exists>
		<TrBudget3128>
			<#list cfx.MSG.TrBudget3128.TrBudgetBill3128 as var6>
			<TrBudgetBill3128>
				<TaxOrgCode>${var6.TaxOrgCode}</TaxOrgCode>
				<TreCode>${var6.TreCode}</TreCode>
				<BudgetType>${var6.BudgetType}</BudgetType>
				<BudgetLevelCode>${var6.BudgetLevelCode}</BudgetLevelCode>
				<BudgetSubjectCode>${var6.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var6.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var6.DayAmt}</DayAmt>
				<TenDayAmt>${var6.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var6.MonthAmt}</MonthAmt>
				<QuarterAmt>${var6.QuarterAmt}</QuarterAmt>
				<YearAmt>${var6.YearAmt}</YearAmt>
			</TrBudgetBill3128>
			</#list>
		</TrBudget3128>
		</#if>
		<#if (cfx.MSG.TrDrawBack3128)?exists>
		<TrDrawBack3128>
			<#list cfx.MSG.TrDrawBack3128.TrDrawBackBill3128 as var7>
			<TrDrawBackBill3128>
				<TaxOrgCode>${var7.TaxOrgCode}</TaxOrgCode>
				<TreCode>${var7.TreCode}</TreCode>
				<BudgetType>${var7.BudgetType}</BudgetType>
				<BudgetLevelCode>${var7.BudgetLevelCode}</BudgetLevelCode>
				<BudgetSubjectCode>${var7.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var7.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var7.DayAmt}</DayAmt>
				<TenDayAmt>${var7.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var7.MonthAmt}</MonthAmt>
				<QuarterAmt>${var7.QuarterAmt}</QuarterAmt>
				<YearAmt>${var7.YearAmt}</YearAmt>
			</TrDrawBackBill3128>
			</#list>
		</TrDrawBack3128>
		</#if>
		<#if (cfx.MSG.TrRemove3128)?exists>
		<TrRemove3128>
			<#list cfx.MSG.TrRemove3128.TrRemoveBill3128 as var8>
			<TrRemoveBill3128>
				<TaxOrgCode>${var8.TaxOrgCode}</TaxOrgCode>
				<TreCode>${var8.TreCode}</TreCode>
				<BudgetType>${var8.BudgetType}</BudgetType>
				<BudgetLevelCode>${var8.BudgetLevelCode}</BudgetLevelCode>
				<BudgetSubjectCode>${var8.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var8.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var8.DayAmt}</DayAmt>
				<TenDayAmt>${var8.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var8.MonthAmt}</MonthAmt>
				<QuarterAmt>${var8.QuarterAmt}</QuarterAmt>
				<YearAmt>${var8.YearAmt}</YearAmt>
			</TrRemoveBill3128>
			</#list>
		</TrRemove3128>
		</#if>
		<#if (cfx.MSG.TrShare3128)?exists>
		<TrShare3128>
			<#list cfx.MSG.TrShare3128.TrShareBill3128 as var9>
			<TrShareBill3128>
				<TaxOrgCode>${var9.TaxOrgCode}</TaxOrgCode>
				<BudgetType>${var9.BudgetType}</BudgetType>
				<TreCode>${var9.TreCode}</TreCode>
				<BudgetLevelCode>${var9.BudgetLevelCode}</BudgetLevelCode>
				<DivideGroup>${var9.DivideGroup}</DivideGroup>
				<BudgetSubjectCode>${var9.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var9.BudgetSubjectName}</BudgetSubjectName>
				<DayAmt>${var9.DayAmt}</DayAmt>
				<TenDayAmt>${var9.TenDayAmt}</TenDayAmt>
				<MonthAmt>${var9.MonthAmt}</MonthAmt>
				<QuarterAmt>${var9.QuarterAmt}</QuarterAmt>
				<YearAmt>${var9.YearAmt}</YearAmt>
			</TrShareBill3128>
			</#list>
		</TrShare3128>
		</#if>
		<#if (cfx.MSG.Stock3128)?exists>
		<Stock3128>
			<#list cfx.MSG.Stock3128.StockBill3128 as var10>
			<StockBill3128>
				<TreCode>${var10.TreCode}</TreCode>
				<AcctCode>${var10.AcctCode}</AcctCode>
				<AcctName>${var10.AcctName}</AcctName>
				<AcctDate>${var10.AcctDate}</AcctDate>
				<YesterdayBalance>${var10.YesterdayBalance}</YesterdayBalance>
				<TodayReceipt>${var10.TodayReceipt}</TodayReceipt>
				<TodayPay>${var10.TodayPay}</TodayPay>
				<TodayBalance>${var10.TodayBalance}</TodayBalance>
			</StockBill3128>
			</#list>
		</Stock3128>
		</#if>
	</MSG>
</CFX>