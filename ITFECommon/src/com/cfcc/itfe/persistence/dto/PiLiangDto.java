/**
 * 
 */
package com.cfcc.itfe.persistence.dto;

import java.util.Map;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 * @author Administrator
 *
 */
public class PiLiangDto implements IDto {
	private String trecode;        //TS_TREASURY.S_TRECODE IS '�����������';
	private String budgetype;      //  Ԥ������
	private String funccode;       //�������Ŀ����
	private String sbdgorgcode; //Ԥ�㵥λ����
	private String type; //����������һ��������������ɽ���������񷢷�
	private Map<String,TvBatchpayDto> idtoMap = null;//����ɽ���������񷢷�ftp����������

	public String getTrecode() {
		return trecode;
	}
	public PiLiangDto(String trecode, String budgetype, String funccode) {
		super();
		this.trecode = trecode;
		this.budgetype = budgetype;
		this.funccode = funccode;
	}
	public PiLiangDto(String trecode, String budgetype, String funccode,String sbdgorgcode) {
		super();
		this.trecode = trecode;
		this.budgetype = budgetype;
		this.funccode = funccode;
		this.sbdgorgcode=sbdgorgcode;
	}
	public PiLiangDto(String trecode, String budgetype, String funccode,String sbdgorgcode,String type) {
		super();
		this.trecode = trecode;
		this.budgetype = budgetype;
		this.funccode = funccode;
		this.sbdgorgcode=sbdgorgcode;
		this.type=type;
	}
	public PiLiangDto(String trecode, String budgetype, String funccode,String sbdgorgcode,String type,Map<String,TvBatchpayDto> idtoMap) {
		super();
		this.trecode = trecode;
		this.budgetype = budgetype;
		this.funccode = funccode;
		this.sbdgorgcode=sbdgorgcode;
		this.type = type;
		this.idtoMap = idtoMap;
	}
	public String getSbdgorgcode() {
		return sbdgorgcode;
	}

	public void setSbdgorgcode(String sbdgorgcode) {
		this.sbdgorgcode = sbdgorgcode;
	}

	public void setTrecode(String trecode) {
		this.trecode = trecode;
	}

	public String getBudgetype() {
		return budgetype;
	}

	public void setBudgetype(String budgetype) {
		this.budgetype = budgetype;
	}

	public String getFunccode() {
		return funccode;
	}

	public void setFunccode(String funccode) {
		this.funccode = funccode;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid()
	 */
	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValid(java.lang.String[])
	 */
	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#checkValidExcept(java.lang.String[])
	 */
	public String checkValidExcept(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getChildren()
	 */
	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#getPK()
	 */
	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#isParent()
	 */
	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.persistence.jaform.parent.IDto#setChildren(com.cfcc.jaf.persistence.jaform.parent.IDto[])
	 */
	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub

	}
	public Map<String, TvBatchpayDto> getIdtoMap() {
		return idtoMap;
	}
	public void setIdtoMap(Map<String, TvBatchpayDto> idtoMap) {
		this.idtoMap = idtoMap;
	}

}
