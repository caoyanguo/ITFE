package com.cfcc.itfe.processors;

import java.util.List;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.dataquery.budgetincomequerysubjectreport.BudgetIncomeQuerySubjectReportBean;
import com.cfcc.itfe.client.dataquery.budgetincomequerysubjectreport.BudgetSubCodeListener;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.SearchUtil;
import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * @author db2admin
 * @time   13-09-23 14:53:50
 */
public class BudgetSubCodeProcessor implements ICompositeProcessor {
	/**
	 * 操作一个生成的Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public Composite process(Composite composite, ContainerMetaData metadata){
		AbstractMetaDataEditorPart editor = MVCUtils.getEditor(metadata.editorid);
		BudgetIncomeQuerySubjectReportBean bean = (BudgetIncomeQuerySubjectReportBean)editor.getModel();
		if ("分科目统计分析查询条件".equals(metadata.name)) {
			Control[] controllists = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controllists.length;
			Control control = controllists[length-1];
			control.addKeyListener(new BudgetSubCodeListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
			
			if(metadata.focus ==true){						
				metadata.focus = true;						
				controllists[0].setFocus();
			}
	   }
		return composite;
	}
}