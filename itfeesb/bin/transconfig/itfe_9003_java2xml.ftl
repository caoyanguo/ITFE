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
		<TraStatusCheck9003>
			<SendOrgCode>${cfx.MSG.TraStatusCheck9003.SendOrgCode}</SendOrgCode>
			<SearchType>${cfx.MSG.TraStatusCheck9003.SearchType}</SearchType>
			<OriMsgNo>${cfx.MSG.TraStatusCheck9003.OriMsgNo}</OriMsgNo>
			<OriEntrustDate>${cfx.MSG.TraStatusCheck9003.OriEntrustDate}</OriEntrustDate>
			<#if cfx.MSG.TraStatusCheck9003.OriPackNo?exists>
			<OriPackNo>${cfx.MSG.TraStatusCheck9003.OriPackNo}</OriPackNo>
			</#if>
			<#if cfx.MSG.TraStatusCheck9003.OriTraNo?exists>
			<OriTraNo>${cfx.MSG.TraStatusCheck9003.OriTraNo}</OriTraNo>
			</#if>
		</TraStatusCheck9003>
	</MSG>
</CFX>