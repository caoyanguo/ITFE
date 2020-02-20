package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class HeadDto implements IDto {
	
	/**
	 * ����ͷ��ʽ
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
	 *���Ľӿ�˵��	
	 *		VER					�汾��	String	3		M 
	 *		SRC 				����ڵ����	NString	12		M
	 *		DES					���սڵ����	NString	12		M
	 *		APP					Ӧ������	String	[1,80]	��дΪ��TIPS��	M
	 *		MsgNo				���ı��	NString	4	�������ĵı��	M
	 *		MsgID				���ı�ʶ��	NString	20	���ڷ���Ψһ��ʶһ�ʱ��ģ����ɷ����Զ���	M
	 *		MsgRef				���Ĳο���	NString	20	�ɽ��׷����߲�������Ӧ�����Զ����أ����������ɷ�������д�����ڱ�ʶ��Ӧ���Ķ�Ӧ��ԭ���ģ��Ӷ����ڷ��������ԭ�������ִ���ĵ�ƥ�䴦������������ʱ���Ĳο���ͬ���ı�ʶ��	M
	 *		WorkDate			��������	Date		TIPS�Ĺ������ڣ�ָ��ǰ���������������ڣ�һ��ֻ����TIPS�����ת���Ľ��ף�TIPS�����ת��ʱ��� 	O
	 *		Reserve				Ԥ���ֶ�	GBString	[1,20]	Ԥ��	O
	 */
	
	/**
     * Field _VER �汾��Ϣ
     */
    private String _VER;

    /**
     * Field _SRC Դ�ڵ�-ITFE���Ľڵ�
     */
    private String _SRC;

    /**
     * Field _DES Ŀ�Ľڵ�-TIPS���Ľڵ�
     */
    private String _DES;

    /**
     * Field _APP Ӧ������ - ITFE
     */
    private String _APP;

    /**
     * Field _msgNo ���ı��
     */
    private String _msgNo;

    /**
     * Field _msgID ���ı�ʶ��
     */
    private String _msgID;

    /**
     * Field _msgRef ���Ĳο���
     */
    private String _msgRef;

    /**
     * Field _workDate ��������
     */
    private String _workDate;

    /**
     * Field _reserve Ԥ���ֶ�
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
