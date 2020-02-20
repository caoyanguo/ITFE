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
		<GetMsg9113>
			<SendOrgCode>${cfx.MSG.GetMsg9113.SendOrgCode}</SendOrgCode>
			<EntrustDate>${cfx.MSG.GetMsg9113.EntrustDate}</EntrustDate>
			<OriPackMsgNo>${cfx.MSG.GetMsg9113.OriPackMsgNo}</OriPackMsgNo>
			<ChkDate>${cfx.MSG.GetMsg9113.ChkDate}</ChkDate>
			<#if cfx.MSG.GetMsg9113.PackNo?exists>
			<PackNo>${cfx.MSG.GetMsg9113.PackNo}</PackNo>
			</#if>
		</GetMsg9113>
	</MSG>
</CFX>