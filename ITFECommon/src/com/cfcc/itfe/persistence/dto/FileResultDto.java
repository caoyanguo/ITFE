package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class FileResultDto implements IDto {
	
	/**
	 * ҵ������
	 */
	private String sbiztype;
	
	/**
	 * �ܱ���
	 */
	private int iallnum;
	
	/**
	 * �ܽ��
	 */
	private BigDecimal fsumamt;
	
	/**
	 * ������Ϣ
	 */
	private String smaininfo;
	
	/**
	 * ��ϸ��Ϣ
	 */
	private String sdetailinfo;
	
	/**
	 * �ļ�����
	 */
	private String sfilename;
	
	/**
	 * ���ı��
	 */
	private String smsgno;
	
	/**
	 * ����ˮ�ż���
	 */
	private List packnos;
	
	/**
	 * �ʽ�������ˮ��
	 */
	private String strasrlno;
	
	/**
	 *  �ؼ��ֶμ��� key-value
	 */
	private HashMap<String, Object> map;
	
	/**
	 * �Ƿ��ظ�����
	 */
	private Boolean repeatimportflag;
	
	/**
	 * �Ƿ��쳣����
	 */
	private Boolean isError;
	
	/**
	 * ������Դ��־
	 */
	private String csourceflag;
	
	/**
	 * ���������Ƿ������������
	 */
	private String iscollect;
	
	/**
	 * �ط���ɫģʽ
	 */
	private String area;
	
	/**
	 * �Ƿ�����ʽ�������ˮ�ţ��ֶ���Ŀ�Ƿ���13λ�� 
	 */
	private int fileColumnLen;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
	
	public int getFileColumnLen() {
		return fileColumnLen;
	}

	public void setFileColumnLen(int fileColumnLen) {
		this.fileColumnLen = fileColumnLen;
	}

	/**
	 * ��˰��Ϣ����
	 */
	private List<TvInfileTmpCountryDto> mainlist;
	
	public String getSbiztype() {
		return sbiztype;
	}

	public void setSbiztype(String sbiztype) {
		this.sbiztype = sbiztype;
	}

	public int getIallnum() {
		return iallnum;
	}

	public void setIallnum(int iallnum) {
		this.iallnum = iallnum;
	}

	public String getSmaininfo() {
		return smaininfo;
	}

	public void setSmaininfo(String smaininfo) {
		this.smaininfo = smaininfo;
	}

	public String getSdetailinfo() {
		return sdetailinfo;
	}

	public void setSdetailinfo(String sdetailinfo) {
		this.sdetailinfo = sdetailinfo;
	}

	public String getSfilename() {
		return sfilename;
	}

	public void setSfilename(String sfilename) {
		this.sfilename = sfilename;
	}

	public String getSmsgno() {
		return smsgno;
	}

	public void setSmsgno(String smsgno) {
		this.smsgno = smsgno;
	}

	public List getPacknos() {
		return packnos;
	}

	public void setPacknos(List packnos) {
		this.packnos = packnos;
	}

	public HashMap<String, Object> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}

	public Boolean getRepeatimportflag() {
		return repeatimportflag;
	}

	public void setRepeatimportflag(Boolean repeatimportflag) {
		this.repeatimportflag = repeatimportflag;
	}

	public Boolean getIsError() {
		return isError;
	}

	public void setIsError(Boolean isError) {
		this.isError = isError;
	}

	public String getStrasrlno() {
		return strasrlno;
	}

	public void setStrasrlno(String strasrlno) {
		this.strasrlno = strasrlno;
	}

	public BigDecimal getFsumamt() {
		return fsumamt;
	}

	public void setFsumamt(BigDecimal fsumamt) {
		this.fsumamt = fsumamt;
	}

	public String getCsourceflag() {
		return csourceflag;
	}

	public void setCsourceflag(String csourceflag) {
		this.csourceflag = csourceflag;
	}

	public List<TvInfileTmpCountryDto> getMainlist() {
		return mainlist;
	}

	public void setMainlist(List<TvInfileTmpCountryDto> mainlist) {
		this.mainlist = mainlist;
	}

	public String getIscollect() {
		return iscollect;
	}

	public void setIscollect(String iscollect) {
		this.iscollect = iscollect;
	}

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
