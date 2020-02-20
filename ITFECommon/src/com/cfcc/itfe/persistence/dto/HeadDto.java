package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class HeadDto implements IDto {
	
	/**
	 * 报文头格式
	 * <CFX>
	 *		<HEAD>
	 *			<VER>${cfx.HEAD.VER}</VER>
	 *			<SRC>${cfx.HEAD.SRC}</SRC>
	 *			<DES>${cfx.HEAD.DES}</DES>
	 *			<APP>${cfx.HEAD.APP}</APP>
	 *			<MsgNo>${cfx.HEAD.MsgNo}</MsgNo>
	 *			<MsgID>${cfx.HEAD.MsgID}</MsgID>
	 *			<MsgRef>${cfx.HEAD.MsgRef}</MsgRef>
	 *			<WorkDate>${cfx.HEAD.WorkDate}</WorkDate>
	 *		</HEAD>
	 *</CFX>
	 *
	 *报文接口说明	
	 *		VER					版本号	String	3		M 
	 *		SRC 				发起节点代码	NString	12		M
	 *		DES					接收节点代码	NString	12		M
	 *		APP					应用名称	String	[1,80]	填写为“TIPS”	M
	 *		MsgNo				报文编号	NString	4	所发报文的编号	M
	 *		MsgID				报文标识号	NString	20	用于发起方唯一标识一笔报文，可由发起方自定义	M
	 *		MsgRef				报文参考号	NString	20	由交易发起者产生，回应报文自动带回，具体内容由发起者填写，用于标识回应报文对应的原报文，从而便于发起者完成原报文与回执报文的匹配处理，发起请求报文时报文参考号同报文标识号	M
	 *		WorkDate			工作日期	Date		TIPS的工作日期，指当前交易所属工作日期，一般只用于TIPS发起或转发的交易，TIPS发起或转发时必填。 	O
	 *		Reserve				预留字段	GBString	[1,20]	预留	O
	 */
	
	/**
     * Field _VER 版本信息
     */
    private String _VER;

    /**
     * Field _SRC 源节点-ITFE报文节点
     */
    private String _SRC;

    /**
     * Field _DES 目的节点-TIPS报文节点
     */
    private String _DES;

    /**
     * Field _APP 应用名称 - ITFE
     */
    private String _APP;

    /**
     * Field _msgNo 报文编号
     */
    private String _msgNo;

    /**
     * Field _msgID 报文标识号
     */
    private String _msgID;

    /**
     * Field _msgRef 报文参考号
     */
    private String _msgRef;

    /**
     * Field _workDate 工作日期
     */
    private String _workDate;

    /**
     * Field _reserve 预留字段
     */
    private String _reserve;

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValidExcept(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub

	}

	public String get_VER() {
		return _VER;
	}

	public void set_VER(String _ver) {
		_VER = _ver;
	}

	public String get_SRC() {
		return _SRC;
	}

	public void set_SRC(String _src) {
		_SRC = _src;
	}

	public String get_DES() {
		return _DES;
	}

	public void set_DES(String _des) {
		_DES = _des;
	}

	public String get_APP() {
		return _APP;
	}

	public void set_APP(String _app) {
		_APP = _app;
	}

	public String get_msgNo() {
		return _msgNo;
	}

	public void set_msgNo(String no) {
		_msgNo = no;
	}

	public String get_msgID() {
		return _msgID;
	}

	public void set_msgID(String _msgid) {
		_msgID = _msgid;
	}

	public String get_msgRef() {
		return _msgRef;
	}

	public void set_msgRef(String ref) {
		_msgRef = ref;
	}

	public String get_workDate() {
		return _workDate;
	}

	public void set_workDate(String date) {
		_workDate = date;
	}

	public String get_reserve() {
		return _reserve;
	}

	public void set_reserve(String _reserve) {
		this._reserve = _reserve;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
