package com.cfcc.itfe.client.sendbiz.exporttbsforbj;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用于保存解析数据相关信息的javabean 
 * @author hua
 */
public class TbsFileInfo implements Serializable{
	private static final long serialVersionUID = -82380713628279229L;
	
	/**将DTO转换后的文件内容**/
	private String fileContent;
	
	/**存放总笔数**/
	private int totalCount;
	
	/**存放总金额**/
	private BigDecimal totalFamt;
	
	/**导出的文件名*/
	private String fileName;
	
	/**导出的完整文件名称(带完整路径)*/
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
