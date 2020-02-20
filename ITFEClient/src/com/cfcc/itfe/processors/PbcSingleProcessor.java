package com.cfcc.itfe.processors;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.recbiz.pbcdirectmodule.PBCMainListener;
import com.cfcc.itfe.client.recbiz.pbcdirectmodule.PbcDirectImportBean;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.SearchUtil;
import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * @author hua
 * @time   12-07-02 09:37:35
 */
public class PbcSingleProcessor implements ICompositeProcessor {
	/**
	 * ����һ�����ɵ�Composite
	 * �������а���ֱ��֧��ҵ��
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public Composite process(Composite composite, ContainerMetaData metadata){
		AbstractMetaDataEditorPart editor = MVCUtils.getEditor(metadata.editorid);
		PbcDirectImportBean bean = (PbcDirectImportBean)editor.getModel();
		if ("�������".equals(metadata.name)) {
			Control[] controls = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controls.length;
			Control control = controls[length - 1];
			control.addKeyListener(new PBCMainListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
			if(metadata.focus ==true){						
				metadata.focus = true;						
				controls[0].setFocus();
			}

		}else if("�����������".equals(metadata.name)) {
			Control[] controls = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controls.length;
			Control control = controls[length - 1];
			control.addKeyListener(new PBCMainListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
			if(metadata.focus ==true){						
				metadata.focus = true;						
				controls[0].setFocus();						
			}
		}else if ("�տ����˺���Ϣ��ʾ".equals(metadata.name)) {
			Control[] controls = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controls.length;
			Control control = controls[length - 1];
			control.addKeyListener(new PBCMainListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
		}
		return composite;
	}

}
