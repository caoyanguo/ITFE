package com.cfcc.itfe.processors;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.recbiz.returnkuinto.DwbkMainListener;
import com.cfcc.itfe.client.recbiz.returnkuinto.ReturnKuIntoBean;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.SearchUtil;
import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * @author hua
 * @time   12-03-21 15:26:25
 */
public class DwbkfjProcessor implements ICompositeProcessor {
	/**
	 * 操作一个生成的Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public Composite process(Composite composite, ContainerMetaData metadata){
		AbstractMetaDataEditorPart editor = MVCUtils.getEditor(metadata.editorid);
		ReturnKuIntoBean bean = (ReturnKuIntoBean)editor.getModel();
		if ("逐笔销号".equals(metadata.name)) {
			Control[] controls = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controls.length;
			Control control = controls[length - 1];
			control.addKeyListener(new DwbkMainListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
			if(metadata.focus ==true){						
				metadata.focus = true;						
				controls[0].setFocus();						
			}
		}else if("所属国库代码".equals(metadata.name)) {
			Control[] controls = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controls.length;
			Control control = controls[length - 1];
			control.addKeyListener(new DwbkMainListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
			if(metadata.focus ==true){						
				metadata.focus = true;						
				controls[0].setFocus();						
			}
		}
		return composite;
	}

}
