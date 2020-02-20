package com.cfcc.itfe.persistence.dto;

import java.util.List;
import java.util.Map;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

	
@SuppressWarnings("serial")
public class MainAndSubDto implements IDto {
	private IDto mainDto = null;
	private List<IDto> mainDtoList = null;
	private List<IDto> subDtoList = null;
	private Map<String,Object> paramMap = null;
	private MainAndSubDto extdto = null;
	public String checkValid() {
		return null;
	}

	public String checkValid(String[] arg0) {
		return null;
	}

	public String checkValidExcept(String[] arg0) {
		return null;
	}

	public IDto[] getChildren() {
		return null;
	}

	public IPK getPK() {
		return null;
	}

	public boolean isParent() {
		return false;
	}

	public void setChildren(IDto[] arg0) {
		
	}

	public IDto getMainDto() {
		return mainDto;
	}

	public void setMainDto(IDto mainDto) {
		this.mainDto = mainDto;
	}

	public List<IDto> getSubDtoList() {
		return subDtoList;
	}

	public void setSubDtoList(List<IDto> subDtoList) {
		this.subDtoList = subDtoList;
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}

	public List<IDto> getMainDtoList() {
		return mainDtoList;
	}

	public void setMainDtoList(List<IDto> mainDtoList) {
		this.mainDtoList = mainDtoList;
	}

	public MainAndSubDto getExtdto() {
		return extdto;
	}

	public void setExtdto(MainAndSubDto extdto) {
		this.extdto = extdto;
	}
}
