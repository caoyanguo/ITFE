package com.cfcc.itfe.processors;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.recbiz.fundinto.FundIntoBean;
import com.cfcc.itfe.client.recbiz.fundpay.FundPayBean;
import com.cfcc.itfe.client.recbiz.fundpay.PayoutMainForGroupListener;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.SearchUtil;
import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * @author hua
 * @time   12-09-27 09:19:18
 */
public class PayouttjProcessor implements ICompositeProcessor {
	/**
	 * ����һ�����ɵ�Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public Composite process(Composite composite, ContainerMetaData metadata){
		AbstractMetaDataEditorPart editor = MVCUtils.getEditor(metadata.editorid);
		FundPayBean bean = (FundPayBean)editor.getModel();
		if ("�������".equals(metadata.name)) {
			Control[] controls = SearchUtil.findControl(composite, new Class[] {
					Text.class, Combo.class });
			int length = controls.length;
			Control control = controls[length - 1];
			control.addKeyListener(new PayoutMainForGroupListener(
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
			control.addKeyListener(new PayoutMainForGroupListener(
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
			control.addKeyListener(new PayoutMainForGroupListener(
					composite, metadata,  (BasicModel) bean,metadata.name));
		}
		return composite;
	}

}
