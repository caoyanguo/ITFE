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
		<LoginInfo9006>
			<Password>${cfx.MSG.LoginInfo9006.Password}</Password>
			<#if cfx.MSG.LoginInfo9006.NewPassword?exists>
			<NewPassword>${cfx.MSG.LoginInfo9006.NewPassword}</NewPassword>
			</#if>
		</LoginInfo9006>
	</MSG>
</CFX>