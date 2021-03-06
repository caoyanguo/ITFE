<?xml version="1.0" encoding="GBK"?>
<MSG>
<HEAD> 
	<Sender>${MSG.HEAD.Sender}</Sender>
	<Receiver>${MSG.HEAD.Receiver}</Receiver> 
	<MsgDate>${MSG.HEAD.MsgDate}</MsgDate>
	<FileName>${MSG.HEAD.FileName}</FileName> 
	<MsgClass>${MSG.HEAD.MsgClass}</MsgClass>
	<MsgType>${MSG.HEAD.MsgType}</MsgType> 
	<RetDate>${MSG.HEAD.RetDate}</RetDate>
</HEAD> 
<DATA> 
	<SUM>
		<Sender>${MSG.DATA.SUM.Sender}</Sender>
		<DealDate>${MSG.DATA.SUM.DealDate}</DealDate>
		<SumCount>${MSG.DATA.SUM.SumCount}</SumCount>
		<SumMoney>${MSG.DATA.SUM.SumMoney}</SumMoney>
		<Idle>${MSG.DATA.SUM.Idle}</Idle>
	</SUM>
	<DETAILS>
	  <#list MSG.DATA.DETAIL as var>
	  <DETAIL>
		  <Sender>${var.Sender}</Sender>
		  <TaxCode>${var.TaxCode}</TaxCode>
		  <TaxName>${var.TaxName}</TaxName>
		  <CommitDate>${var.CommitDate}</CommitDate>
		  <DealNo>${var.DealNo}</DealNo>
	  	  <VouNo>${var.VouNo}</VouNo>
		  <GenDate>${var.GenDate}</GenDate>
	  	  <TreCode>${var.TreCode}</TreCode>
		  <TreName>${var.TreName}</TreName>
		  <PayeeCode>${var.PayeeCode}</PayeeCode>
		  <PayeeType>${var.PayeeType}</PayeeType>
		  <PayeeTypeName>${var.PayeeTypeName}</PayeeTypeName>
		  <PayeeName>${var.PayeeName}</PayeeName>
	  	  <PayeeBankNo>${var.PayeeBankNo}</PayeeBankNo>
		  <PayeeBankName>${var.PayeeBankName}</PayeeBankName>
	  	  <PayeeAcc>${var.PayeeAcc}</PayeeAcc>
		  <BackMoney>${var.BackMoney}</BackMoney>
		  <BudgetSubjectCode>${var.BudgetSubjectCode}</BudgetSubjectCode>
		  <BudgetSubjectName>${var.BudgetSubjectName}</BudgetSubjectName>
		  <BudgetLevel>${var.BudgetLevel}</BudgetLevel>
		  <BudgetLevelName>${var.BudgetLevelName}</BudgetLevelName>
		  <OriginNo>${var.OriginNo}</OriginNo>
		  <OriginDate>${var.OriginDate}</OriginDate>
		  <OriginTaxItemName>${var.OriginTaxItemName}</OriginTaxItemName>
		  <BackFlag>${var.BackFlag}</BackFlag>
		  <BackReason>${var.BackReason}</BackReason>
		  <BackReasonName>${var.BackReasonName}</BackReasonName>
		  <Idle>${var.Idle}</Idle>
		 </DETAIL>
	</DETAILS>
	</#list> 
</DATA>
</MSG>