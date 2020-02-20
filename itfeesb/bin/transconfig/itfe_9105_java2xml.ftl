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
		<FreeFormat9105>
			<SrcNodeCode>${cfx.MSG.FreeFormat9105.SrcNodeCode}</SrcNodeCode>
			<DesNodeCode>${cfx.MSG.FreeFormat9105.DesNodeCode}</DesNodeCode>
			<#if cfx.MSG.FreeFormat9105.SendOrgCode?exists>
			<SendOrgCode>${cfx.MSG.FreeFormat9105.SendOrgCode}</SendOrgCode>
			</#if>
			<#if cfx.MSG.FreeFormat9105.RcvOrgCode?exists>
			<RcvOrgCode>${cfx.MSG.FreeFormat9105.RcvOrgCode}</RcvOrgCode>
			</#if>
			<Content>${cfx.MSG.FreeFormat9105.Content}</Content>
		</FreeFormat9105>
	</MSG>
</CFX>
