package com.cfcc.itfe.processors;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.dataquery.shbudgetsubcodesearch.ShbudgetsubcodesearchBean;
import com.cfcc.itfe.client.dataquery.shbudgetsubcodesearch.SubjectCodeTimerSearchListener;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.SearchUtil;
import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * @author db2admin
 * @time   13-09-27 09:53:53
 */
public class SubjectCodeTimerSearchProcessor implements ICompositeProcessor {
	/**
	 * 操作一个生成的Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public Composite process(Composite composite, ContainerMetaData metadata){
		AbstractMetaDataEditorPart editor = MVCUtils.getEditor(metadata.editorid);
		ShbudgetsubcodesearchBean bean = (ShbudgetsubcodesearchBean)editor.getModel();
		if ("预算科目序时查询查询条件".equals(metadata.name)) {
			Control[] controllists = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controllists.length;
			Control control = controllists[length-1];
			control.addKeyListener(new SubjectCodeTimerSearchListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
			
			if(metadata.focus ==true){						
				metadata.focus = true;						
				controllists[0].setFocus();
			}
	   }
		return composite;
	}

}
