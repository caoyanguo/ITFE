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
 * @time 13-01-30 09:25:38 ��ϵͳ: Para ģ��:paramTransform ���:ParamTransformUI
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
	 * Direction: ���� ename: tbsParamExport ���÷���: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String tbsParamExport(Object o) {

		if (checkList.size() < 1) {
			MessageDialog.openMessageDialog(null,"��ѡ��Ҫ�����Ĳ�����");
			return null;
		}
		if(separator == null){
			MessageDialog.openMessageDialog(null,"��ѡ���ֶηָ���");
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
						msg.append(serverpath.split(":")[1]+"������\n");
					}else{
						File file=new File(clientPath+fsp+serverpath);
						if(file.exists())
							file.delete();
						ClientFileTransferUtil.downloadFile(serverpath, clientPath+fsp+serverpath);
						msg.append(serverpath+"�����ɹ�\n");
					}
				} catch (FileTransferException e) {
					log.error("����ʧ��", e);
					msg.append(serverpath+"����ʧ��\n");
				}
			}
			MessageDialog.openMessageDialog(null, msg.toString()+"");
		} catch (ITFEBizException e) {
			log.error("ҵ���쳣", e);
			MessageDialog.openErrorDialog(null, e);
		} catch (Exception e) {
			log.error("ҵ���쳣", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.tbsParamExport(o);
	}

	/**
	 * Direction: ȫѡ ename: selAll ���÷���: viewers: * messages:
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