<?xml version="1.0" encoding="GBK"?>
<Voucher>
	<Id>${Voucher.Id}</Id>
	<AdmDivCode>${Voucher.AdmDivCode}</AdmDivCode>
	<StYear>${Voucher.StYear}</StYear>
	<VtCode>${Voucher.VtCode}</VtCode>
	<VouDate>${Voucher.VouDate}</VouDate>
	<VoucherNo>${Voucher.VoucherNo}</VoucherNo>
	<TreCode>${Voucher.TreCode}</TreCode>
	<FinOrgCode>${Voucher.FinOrgCode}</FinOrgCode>
	<BgtTypeCode>${Voucher.BgtTypeCode}</BgtTypeCode>
	<BgtTypeName>${Voucher.BgtTypeName}</BgtTypeName>
	<FundTypeCode>${Voucher.FundTypeCode}</FundTypeCode>
	<FundTypeName>${Voucher.FundTypeName}</FundTypeName>
	<PayTypeCode>${Voucher.PayTypeCode}</PayTypeCode>
	<PayTypeName>${Voucher.PayTypeName}</PayTypeName>
	<AgentAcctNo>${Voucher.AgentAcctNo}</AgentAcctNo>      
	<AgentAcctName>${Voucher.AgentAcctName}</AgentAcctName>    
	<AgentAcctBankName>${Voucher.AgentAcctBankName}</AgentAcctBankName>
	<ClearAcctNo>${Voucher.ClearAcctNo}</ClearAcctNo>
	<ClearAcctName>${Voucher.ClearAcctName}</ClearAcctName>
	<ClearAcctBankName>${Voucher.ClearAcctBankName}</ClearAcctBankName>
	<PayDictateNo>${Voucher.PayDictateNo}</PayDictateNo>     
	<PayMsgNo>${Voucher.PayMsgNo}</PayMsgNo>
	<PayEntrustDate>${Voucher.PayEntrustDate}</PayEntrustDate>
	<PayAmt>${Voucher.PayAmt}</PayAmt>
	<PayBankName>${Voucher.PayBankName}</PayBankName>
	<PayBankNo>${Voucher.PayBankNo}</PayBankNo>
	<Remark>${Voucher.Remark}</Remark>
	<MoneyCorpCode>${Voucher.MoneyCorpCode}</MoneyCorpCode>
	<TKLX>${Voucher.TKLX}</TKLX>
	<XPaySndBnkNo>${Voucher.XPaySndBnkNo}</XPaySndBnkNo>
	<XAddWord>${Voucher.XAddWord}</XAddWord>
	<XClearDate>${Voucher.XClearDate}</XClearDate>
	<XPayAmt>${Voucher.XPayAmt}</XPayAmt>
	<Hold1>${Voucher.Hold1}</Hold1>
	<Hold2>${Voucher.Hold2}</Hold2>
	<DetailList>
		  <#list Voucher.DetailList.Detail as var>
		  <Detail>
				<Id>${var.Id}</Id>
				<VoucherNo>${var.VoucherNo}</VoucherNo>
				<SupDepCode>${var.SupDepCode}</SupDepCode>
				<SupDepName>${var.SupDepName}</SupDepName>
				<ExpFuncCode>${var.ExpFuncCode}</ExpFuncCode>
				<ExpFuncName>${var.ExpFuncName}</ExpFuncName>
				<PayAmt>${var.PayAmt}</PayAmt>
				<PaySummaryName>${var.PaySummaryName}</PaySummaryName>
				<Hold1>${var.Hold1}</Hold1>
				<Hold2>${var.Hold2}</Hold2>
				<Hold3>${var.Hold3}</Hold3>
				<Hold4>${var.Hold4}</Hold4>	  
		 </Detail>
		 </#list> 
	</DetailList>  
</Voucher>