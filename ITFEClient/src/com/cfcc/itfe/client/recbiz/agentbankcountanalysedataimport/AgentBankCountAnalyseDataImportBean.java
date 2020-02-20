package com.cfcc.itfe.client.recbiz.agentbankcountanalysedataimport;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.databinding.Mapper;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.recbiz.agentbankcountanalysedataimport.IAgentBankCountAnalyseDataImportService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 15-03-16 11:24:44 子系统: RecBiz 模块:AgentBankCountAnalyseDataImport
 *       组件:AgentBankCountAnalyseDataImport
 */
public class AgentBankCountAnalyseDataImportBean extends
		AbstractAgentBankCountAnalyseDataImportBean implements
		IPageDataProvider {

	private static Log log = LogFactory
			.getLog(AgentBankCountAnalyseDataImportBean.class);
	private ITFELoginInfo loginfo = null;

	public AgentBankCountAnalyseDataImportBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
		treCode = "";
		filePath = new ArrayList();
		srSearchDto = new TrIncomedayrptDto();
		kcSearchDto = new TrStockdayrptDto();
		treCodelist = new ArrayList();
		rptDate = "";
		bizTypeList = new ArrayList();
		bizType = "";
		msg = "请选择需要导入的库存和收入报表数据，（库存文件kcrb2801000000-20141110-1110.kcr或收入报表k20141110t2801000000sr.txt）";
		init();
	}

	/**
	 * 初始化列表信息
	 */
	private void init() {
		try {
			treCodelist = commonDataAccessService.getSubTreCode(loginfo	.getSorgcode());
			if(null != treCodelist && treCodelist.size() > 0)
				treCode = ((TsTreasuryDto)treCodelist.get(0)).getStrecode();
			rptDate = commonDataAccessService.getSysDBDate();
//			Mapper mapper1 = new Mapper();
//			mapper1.setDisplayValue("库存报表");
//			mapper1.setUnderlyValue("1");
//			Mapper mapper2 = new Mapper();
//			mapper2.setDisplayValue("收入报表");
//			mapper2.setUnderlyValue("2");
//			bizTypeList.add(mapper1);
//			bizTypeList.add(mapper2);
//			bizType = "1";
			
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	/**
	 * Direction: 代理库统计分析数据导入 ename: dataImport 引用方法: viewers: * messages:
	 */
	public String dataImport(Object o) {
		List<String> failList = new ArrayList<String>();
		List<String> uploadServerFiles = new ArrayList<String>();
		try {
			if(null == filePath || filePath.size() == 0){
				MessageDialog.openMessageDialog(null, "请选择需要加载的文件信息！");
				filePath = new ArrayList();
				return null;
			}
			List resultList = fileFilter(filePath);
			if (resultList.size() > 1 && null != resultList.get(1) && StringUtils.isNotBlank(resultList.get(1).toString())) {
				MessageDialog.openMessageDialog(null, "选择文件中包含以下不合法文件，请确认！\n\n"
						+ resultList.get(1).toString());
				filePath = new ArrayList();
				return null;
			}
			//加载合法文件
			for(File f : (List<File>)resultList.get(0)){
				uploadServerFiles.add(ClientFileTransferUtil.uploadFile(f.getAbsolutePath()));
			}
			/**
			 * serverResultList
			 * 0:成功文件信息
			 * 1：失败文件信息
			 * 2：失败文件具体信息
			 */
			List<List> serverResultList = agentBankCountAnalyseDataImportService.dataImport(uploadServerFiles);
			if(null != serverResultList){
				List<File> succeeList = serverResultList.get(0);
				failList = serverResultList.get(1);
				StringBuffer resultSb = new StringBuffer();
				resultSb.append("上传文件个数：" + uploadServerFiles.size() + "\n解析成功文件个数:" + succeeList.size() + "\n解析失败文件个数：" + failList.size());
				if(failList.size() > 0){
					String filename = "C:\\client\\errInfo\\代理库统计分析数据" + new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new java.util.Date()) + ".txt";
					resultSb.append("\n详细信息保存在：" + filename);
					StringBuffer failFileLocalContent = new StringBuffer();
					failFileLocalContent.append("上传文件个数：" + uploadServerFiles.size() + ",解析成功文件个数:" + succeeList.size() + "解析失败文件个数：" + failList.size() + "\n");
					failFileLocalContent.append(serverResultList.get(2));
					FileUtil.getInstance().writeFile(filename,failFileLocalContent.toString());
				}
				MessageDialog.openMessageDialog(null, resultSb.toString());
				
			}
			filePath = new ArrayList();
		} catch (Exception e) {
			filePath = new ArrayList();
			failList.addAll(uploadServerFiles);
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		} finally {
//			if(null != failList && failList.size() > 0){
//				//删除服务器上传失败文件
//				try {
//					DeleteServerFileUtil.delFile(commonDataAccessService, failList);
//				} catch (ITFEBizException e1) {
//					log.error("删除服务器文件失败！", e1);
//					MessageDialog.openErrorDialog(null, e1);
//				}
//			}
			this.editor.fireModelChanged();
		}
		return super.dataImport(o);
	}

	/**
	 * Direction: 发送报表数据 ename: sendDate 引用方法: viewers: * messages:
	 */
	public String sendDate(Object o) {
		try {
			List searchList = new ArrayList();
			searchList.add(treCode);
			searchList.add(rptDate);
			searchList.add(bizType);
			MessageDialog.openMessageDialog(null, agentBankCountAnalyseDataImportService.sendData(searchList));
		} catch (ITFEBizException e) {
			log.error(e.getMessage(),e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.sendDate(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	private List<String> fileFilter(List<File> files) {
		List resultList = new ArrayList();
		List<File> importFiles = new ArrayList<File>();
		StringBuffer resultSb = new StringBuffer();
		resultList.add(importFiles);
//		resultList.add(resultSb);
		int count = 0;
		for (File file : files) {
			if (!(file.getName().toLowerCase().endsWith(".kcr") || file
					.getName().toLowerCase().endsWith("sr.txt"))) {
				count++;
				if (count < 10)
					resultSb.append(file.getName() + "不是合法文件！\n");
			} else {
				importFiles.add(file);
			}
		}
		if (StringUtils.isNotBlank(resultSb.toString())) {
			resultList.add(resultSb.toString());
		}
		return resultList;
	}

}