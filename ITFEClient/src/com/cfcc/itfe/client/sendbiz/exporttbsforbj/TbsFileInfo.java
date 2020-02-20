package com.cfcc.itfe.client.sendbiz.exporttbsforbj;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ���ڱ���������������Ϣ��javabean 
 * @author hua
 */
public class TbsFileInfo implements Serializable{
	private static final long serialVersionUID = -82380713628279229L;
	
	/**��DTOת������ļ�����**/
	private String fileContent;
	
	/**����ܱ���**/
	private int totalCount;
	
	/**����ܽ��**/
	private BigDecimal totalFamt;
	
	/**�������ļ���*/
	private String fileName;
	
	/**�����������ļ�����(������·��)*/
	private String fullFileName;
	
	public String getFileContent() {
		return fileContent;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public BigDecimal getTotalFamt() {
		return totalFamt;
	}
	public void setTotalFamt(BigDecimal totalFamt) {
		this.totalFamt = totalFamt;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFullFileName() {
		return fullFileName;
	}
	public void setFullFileName(String fullFileName) {
		this.fullFileName = fullFileName;
	}
	@Override
	public String toString() {
		return "TbsFileInfo [fileContent=" + fileContent + ", totalCount="
				+ totalCount + ", totalFamt=" + totalFamt +", fullFileName=" + fullFileName + "]";
	}
}
