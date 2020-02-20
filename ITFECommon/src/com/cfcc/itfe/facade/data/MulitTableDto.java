package com.cfcc.itfe.facade.data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class MulitTableDto implements IDto {
	private List<IDto> fatherDtos;
	private List<IDto> sonDtos;
	private List<IDto> packDtos;   //�ļ��������ˮ�Ŷ�Ӧ��ϵ(���㳬1000�ʷְ�)
	private BigDecimal famt;    //�ܽ��
	private int totalCount ;  //�ܱ���
	private String fileName;
	private String biztype;
	private String specFlag;// �����־
	private String sbookorgcode;// �����������
	private Date dacctDate;// ��������
	private HashMap<String, String> map;   //����+ֵ
	private List<String> voulist = new ArrayList<String>(); //ƾ֤��ż�	
	private List<String> errorList = new ArrayList<String>(); //������Ϣ�б�
	private int errorCount ; //������Ϣ����
	private List<String> errNameList = new ArrayList<String>(); //�����ļ������б�
	private List<TvVoucherinfoDto> errCheckList = new ArrayList<TvVoucherinfoDto>(); //����У���DTO
	private List<IDto> updateIdtoList = new ArrayList<IDto>();//����ɽ���������񷢷�ftp����������
	
	public MulitTableDto() {
		super();
	}
	
	public MulitTableDto(List<IDto> fatherDtos, List<IDto> sonDtos,
			List<IDto> packDtos, List<String> voulist) {
		super();
		this.fatherDtos = fatherDtos;
		this.sonDtos = sonDtos;
		this.packDtos = packDtos;
		this.voulist = voulist;
	}
	public void addUpdateIdtoList(IDto idto)
	{
		if(this.updateIdtoList!=null)
			this.updateIdtoList.add(idto);
		else
		{
			this.updateIdtoList = new ArrayList<IDto>();
			this.updateIdtoList.add(idto);
		}
	}
	public Date getDacctDate() {
		return dacctDate;
	}

	public void setDacctDate(Date dacctDate) {
		this.dacctDate = dacctDate;
	}

	public String getSbookorgcode() {
		return sbookorgcode;
	}

	public void setSbookorgcode(String sbookorgcode) {
		this.sbookorgcode = sbookorgcode;
	}

	public String getSpecFlag() {
		return specFlag;
	}

	public void setSpecFlag(String specFlag) {
		this.specFlag = specFlag;
	}

	private String zffs;// ֧����ʽ
	private String skgk;// �տ����
	private Long zxh;// �����

	public Long getZxh() {
		return zxh;
	}

	public void setZxh(Long zxh) {
		this.zxh = zxh;
	}

	public String getZffs() {
		return zffs;
	}

	public void setZffs(String zffs) {
		this.zffs = zffs;
	}

	public String getSkgk() {
		return skgk;
	}

	public void setSkgk(String skgk) {
		this.skgk = skgk;
	}

	public String getBiztype() {
		return biztype;
	}

	public void setBiztype(String biztype) {
		this.biztype = biztype;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public BigDecimal getFamt() {
		return famt;
	}

	public void setFamt(BigDecimal famt) {
		this.famt = famt;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IDto> getFatherDtos() {
		return fatherDtos;
	}

	public void setFatherDtos(List<IDto> fatherDtos) {
		this.fatherDtos = fatherDtos;
	}

	public List<IDto> getSonDtos() {
		return sonDtos;
	}

	public void setSonDtos(List<IDto> sonDtos) {
		this.sonDtos = sonDtos;
	}

	public List<IDto> getPackDtos() {
		return packDtos;
	}

	public List<IDto> getUpdateIdtoList() {
		return updateIdtoList;
	}

	public void setUpdateIdtoList(List<IDto> updateIdtoList) {
		this.updateIdtoList = updateIdtoList;
	}

	public void setPackDtos(List<IDto> packDtos) {
		this.packDtos = packDtos;
	}

	public HashMap<String, String> getMap() {
		return map;
	}

	public void setMap(HashMap<String, String> map) {
		this.map = map;
	}

	public List<String> getVoulist() {
		return voulist;
	}

	public void setVoulist(List<String> voulist) {
		this.voulist = voulist;
	}
	
	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	
	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public List<String> getErrNameList() {
		return errNameList;
	}

	public void setErrNameList(List<String> errNameList) {
		this.errNameList = errNameList;
	}

	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TvVoucherinfoDto> getErrCheckList() {
		return errCheckList;
	}

	public void setErrCheckList(List<TvVoucherinfoDto> errCheckList) {
		this.errCheckList = errCheckList;
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
