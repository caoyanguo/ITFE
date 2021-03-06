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
        <BatchHead3122>
            <ChkDate>${cfx.MSG.BatchHead3122.ChkDate}</ChkDate>
            <PackNo>${cfx.MSG.BatchHead3122.PackNo}</PackNo>
            <TaxOrgCode>${cfx.MSG.BatchHead3122.TaxOrgCode}</TaxOrgCode>
            <AllNum>${cfx.MSG.BatchHead3122.AllNum}</AllNum>
            <AllAmt>${cfx.MSG.BatchHead3122.AllAmt}</AllAmt>
            <ChildPackNum>${cfx.MSG.BatchHead3122.ChildPackNum}</ChildPackNum>
            <CurPackNo>${cfx.MSG.BatchHead3122.CurPackNo}</CurPackNo>
            <CurPackNum>${cfx.MSG.BatchHead3122.CurPackNum}</CurPackNum>
            <CurPackAmt>${cfx.MSG.BatchHead3122.CurPackAmt}</CurPackAmt>
        </BatchHead3122>
        <#list cfx.MSG.CompDeduct3122 as var1>
        <CompDeduct3122>
            <PayeeOrgCode>${var1.PayeeOrgCode}</PayeeOrgCode>
            <OriMsgNo>${var1.OriMsgNo}</OriMsgNo>
            <OriSendOrgCode>${var1.OriSendOrgCode}</OriSendOrgCode>
            <OriEntrustDate>${var1.OriEntrustDate}</OriEntrustDate>
            <OriPackNo>${var1.OriPackNo}</OriPackNo>
            <OriTraNo>${var1.OriTraNo}</OriTraNo>
            <OriTaxVouNo>${var1.OriTaxVouNo}</OriTaxVouNo>
            <TraAmt>${var1.TraAmt}</TraAmt>
            <TaxDate>${var1.TaxDate}</TaxDate>
            <OpStat>${var1.OpStat}</OpStat>
            <AddWord>${var1.AddWord}</AddWord>
        </CompDeduct3122>
        </#list>
    </MSG>
</CFX>
