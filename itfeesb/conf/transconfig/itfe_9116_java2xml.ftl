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
        <GetMsg9116>
            <SendOrgCode>${cfx.MSG.GetMsg9116.SendOrgCode}</SendOrgCode>
            <EntrustDate>${cfx.MSG.GetMsg9116.EntrustDate}</EntrustDate>
            <OriPackMsgNo>${cfx.MSG.GetMsg9116.OriPackMsgNo}</OriPackMsgNo>
            <OriChkDate>${cfx.MSG.GetMsg9116.OriChkDate}</OriChkDate>
            <OriChkAcctType>${cfx.MSG.GetMsg9116.OriChkAcctType}</OriChkAcctType>
            <#if cfx.MSG.GetMsg9116.OriReportMonth?exists>
            <OriReportMonth>${cfx.MSG.GetMsg9116.OriReportMonth}</OriReportMonth>
            </#if>
            <#if cfx.MSG.GetMsg9116.OirDocNameMonth?exists>
            <OirDocNameMonth>${cfx.MSG.GetMsg9116.OirDocNameMonth}</OirDocNameMonth>
            </#if>
        </GetMsg9116>
    </MSG>
</CFX>