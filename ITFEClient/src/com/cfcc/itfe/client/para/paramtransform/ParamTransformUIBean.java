package com.cfcc.itfe.client.para.paramtransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.ParamConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-01-30 09:25:38 子系统: Para 模块:paramTransform 组件:ParamTransformUI
 */
public class ParamTransformUIBean extends AbstractParamTransformUIBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(ParamTransformUIBean.class);

	private ITFELoginInfo loginfo;
	private String separator;

	public ParamTransformUIBean() {
		super();
		tabList = new ArrayList();
		checkList = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		separator="	";
		init();
	}

	/**
	 * Direction: 导出 ename: tbsParamExport 引用方法: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String tbsParamExport(Object o) {

		if (checkList.size() < 1) {
			MessageDialog.openMessageDialog(null,"请选择要导出的参数表");
			return null;
		}
		if(separator == null){
			MessageDialog.openMessageDialog(null,"请选择字段分隔符");
			return null;
		}
		try {
			StringBuffer msg = new StringBuffer();
			DirectoryDialog directory = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String clientPath = directory.open();
			if(clientPath==null){
				return null;
			}
			List<String> paths = this.paramTransformService.export(checkList,
					separator, loginfo.getSorgcode());
			String fsp = System.getProperty("file.separator");
			for(String serverpath:paths){
				try {
					if(serverpath.startsWith("NODATA")){
						msg.append(serverpath.split(":")[1]+"无数据\n");
					}else{
						File file=new File(clientPath+fsp+serverpath);
						if(file.exists())
							file.delete();
						ClientFileTransferUtil.downloadFile(serverpath, clientPath+fsp+serverpath);
						msg.append(serverpath+"导出成功\n");
					}
				} catch (FileTransferException e) {
					log.error("下载失败", e);
					msg.append(serverpath+"下载失败\n");
				}
			}
			MessageDialog.openMessageDialog(null, msg.toString()+"");
		} catch (ITFEBizException e) {
			log.error("业务异常", e);
			MessageDialog.openErrorDialog(null, e);
		} catch (Exception e) {
			log.error("业务异常", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.tbsParamExport(o);
	}

	/**
	 * Direction: 全选 ename: selAll 引用方法: viewers: * messages:
	 */
	public String selAll(Object o) {
		return super.selAll(o);
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

	private void init() {
		for(int i=0; i<ParamConstant.TABS.length; i++){
				TdEnumvalueDto dto = new TdEnumvalueDto();
				dto.setSvalue(ParamConstant.TABS[i][0]);
				dto.setSvaluecmt(ParamConstant.TABS[i][1]);
				tabList.add(dto);
		}
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

}