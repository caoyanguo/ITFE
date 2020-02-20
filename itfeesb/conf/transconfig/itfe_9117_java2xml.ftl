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
		<GetMsg9117>
			<SendOrgCode>${cfx.MSG.GetMsg9117.SendOrgCode}</SendOrgCode>
			<EntrustDate>${cfx.MSG.GetMsg9117.EntrustDate}</EntrustDate>
			<OriPackMsgNo>${cfx.MSG.GetMsg9117.OriPackMsgNo}</OriPackMsgNo>
			<OriChkDate>${cfx.MSG.GetMsg9117.OriChkDate}</OriChkDate>
			<#if cfx.MSG.GetMsg9117.OriPackNo?exists>
			<OriPackNo>${cfx.MSG.GetMsg9117.OriPackNo}</OriPackNo>
			</#if>
			<OrgType>${cfx.MSG.GetMsg9117.OrgType}</OrgType>
		</GetMsg9117>
	</MSG>
</CFX>
	