package com.cfcc.itfe.facade.data;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

/**
 *Table ����
 * 
 */
public class TableDesDto implements IDto {
	// ����
	private String tabEname;
	// ������
	private String tabCname;
	public String getTabEname() {
		return tabEname;
	}

	public void setTabEname(String tabEname) {
		this.tabEname = tabEname;
	}

	public String getTabCname() {
		return tabCname;
	}

	public void setTabCname(String tabCname) {
		this.tabCname = tabCname;
	}

	public String getTabModuleName() {
		return tabModuleName;
	}

	public void setTabModuleName(String tabModuleName) {
		this.tabModuleName = tabModuleName;
	}

	public String getTabUIName() {
		return tabUIName;
	}

	public void setTabUIName(String tabUIName) {
		this.tabUIName = tabUIName;
	}

	public String getTabDtoClsName() {
		return tabDtoClsName;
	}

	public void setTabDtoClsName(String tabDtoClsName) {
		this.tabDtoClsName = tabDtoClsName;
	}

	public String getTabBeanClsName() {
		return tabBeanClsName;
	}

	public void setTabBeanClsName(String tabBeanClsName) {
		this.tabBeanClsName = tabBeanClsName;
	}

	//��Ӧ��ģ������
	private String tabModuleName;
	//��Ӧ��UI�������
	private String tabUIName;
	//��Ӧ��Dto����
	private String tabDtoClsName;
	//��Ӧ��bean����
	private String tabBeanClsName;

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

}
