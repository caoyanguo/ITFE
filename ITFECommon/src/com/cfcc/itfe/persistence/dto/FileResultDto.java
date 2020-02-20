package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class FileResultDto implements IDto {
	
	/**
	 * 业务类型
	 */
	private String sbiztype;
	
	/**
	 * 总笔数
	 */
	private int iallnum;
	
	/**
	 * 总金额
	 */
	private BigDecimal fsumamt;
	
	/**
	 * 主体信息
	 */
	private String smaininfo;
	
	/**
	 * 明细信息
	 */
	private String sdetailinfo;
	
	/**
	 * 文件名称
	 */
	private String sfilename;
	
	/**
	 * 报文编号
	 */
	private String smsgno;
	
	/**
	 * 包流水号集合
	 */
	private List packnos;
	
	/**
	 * 资金收纳流水号
	 */
	private String strasrlno;
	
	/**
	 *  关键字段集合 key-value
	 */
	private HashMap<String, Object> map;
	
	/**
	 * 是否重复导入
	 */
	private Boolean repeatimportflag;
	
	/**
	 * 是否异常处理
	 */
	private Boolean isError;
	
	/**
	 * 数据来源标志
	 */
	private String csourceflag;
	
	/**
	 * 核算主体是否配置收入汇总
	 */
	private String iscollect;
	
	/**
	 * 地方特色模式
	 */
	private String area;
	
	/**
	 * 是否包含资金收纳流水号（字段数目是否是13位） 
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
	 * 国税信息对象
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
