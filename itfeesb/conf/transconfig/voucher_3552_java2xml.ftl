<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id> 
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode> 
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode> 
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo> 
	<SetMonth>${Voucher.SetMonth}</SetMonth>	
	<AllAmt>${Voucher.AllAmt}</AllAmt>
	<AcctNo>${Voucher.AcctNo}</AcctNo>	
	<AcctName>${Voucher.AcctName}</AcctName>
	<XCurDateMoney>${Voucher.XCurDateMoney}</XCurDateMoney>
	<XDiffMoney>${Voucher.XDiffMoney}</XDiffMoney>
	<#if Voucher.Hold1?exists>
	<Hold1>${Voucher.Hold1}</Hold1>
	</#if>
	<#if Voucher.Hold2?exists>
	<Hold2>${Voucher.Hold2}</Hold2>
	</#if>
	<#list Voucher.DetailList as var1>
	<DetailList>
	  <#list var1.Detail as var>
	  <Detail>
	  	  <#if var.CreateDate?exists>
		  <CreateDate>${var.CreateDate}</CreateDate>
		  </#if>
		  <#if var.PbcProCat?exists>
		  <PbcProCat>${var.PbcProCat}</PbcProCat>
		  </#if>
		  <#if var.PbcPayAmt?exists>
		  <PbcPayAmt>${var.PbcPayAmt}</PbcPayAmt>
		  </#if>
		  <#if var.MofProCat?exists>
		  <MofProCat>${var.MofProCat}</MofProCat>
		  </#if>
		  <#if var.MofPayAmt?exists>
		  <MofPayAmt>${var.MofPayAmt}</MofPayAmt>
		  </#if>
		  <#if var.Remark?exists>
	  	  <Remark>${var.Remark}</Remark>
	  	  </#if>
	  	  <#if var.Hold1?exists>
	  	  <Hold1>${var.Hold1}</Hold1>
	  	  </#if>
	  	  <#if var.Hold2?exists>
	  	  <Hold2>${var.Hold2}</Hold2>
	  	  </#if>
	  	  <#if var.Hold3?exists>
	  	  <Hold3>${var.Hold3}</Hold3>
	  	  </#if>
	  	  <#if var.Hold4?exists>
	  	  <Hold4>${var.Hold4}</Hold4>
	  	  </#if>
	 </Detail>
	</#list> 
	</DetailList>
	</#list>	
</Voucher>