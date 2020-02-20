package com.cfcc.itfe.client.recbiz.uploadmodule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.ViewMetaData;

public class IncomeBizListener extends KeyAdapter{
	private static Log log = LogFactory.getLog(IncomeBizListener.class);
	private Composite composite;
	private ContainerMetaData metadata;
	AbstractMetaDataEditorPart editor;
	private String mename;
	private UploadUIBean bean;

	public IncomeBizListener(Composite composite, ContainerMetaData metadata,BasicModel bean,String metaname) {
		this.composite = composite;
		this.metadata = metadata;
		this.editor = MVCUtils.getEditor(metadata.editorid);
		this.bean = (UploadUIBean) bean;
		this.mename = metaname;
	}

	public void keyPressed(KeyEvent e) {
		if (e.keyCode != SWT.Selection && e.keyCode != SWT.KEYPAD_CR) {
			return;
		}
		if (e.getSource() instanceof Text) {
			composite.setFocus();
			bean.searchFileListBySrlno(null);
			
		}
	}
	
	
}
