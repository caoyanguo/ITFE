package com.cfcc.itfe.client.processor;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.cfcc.jaf.rcp.util.SearchUtil;

/**
 * 
 * @author gaoym
 * 
 */
public class AddListenerHelper {

	private static final ArrowTraverseListener goupdownArrowListener = new ArrowTraverseListener(
			true, true);

	private static final ArrowTraverseListener goupArrowListener = new ArrowTraverseListener(
			true, false);

	private static final ArrowTraverseListener godownArrowListener = new ArrowTraverseListener(
			false, true);

	private static final BlankKeyListener blankKeyListener = new BlankKeyListener();

	/**
	 * 为Composite上每个Text控件，Combo控件加上光标上下键监听，回车键监听。
	 * 
	 * @param composite
	 */
	public static void setArrowKeyTraversable(Composite composite) {
		Control[] controls = SearchUtil.findControl(composite, new Class[] {
				Text.class, Combo.class });
		addListeners(controls, blankKeyListener);
	}

	public static Control addListeners(Control[] list, KeyListener keyListener) {

		if (list != null && list.length == 1) {
			list[0].addKeyListener(keyListener);
			return (Control) list[0];
		}
		if (list != null && list.length > 0) {
			int isize = list.length;
			// first
			list[0].addKeyListener(keyListener);
			list[0].addTraverseListener(godownArrowListener);
			// detail
			for (int k = 1; k < isize - 1; k++) {
				list[k].addKeyListener(keyListener);
				list[k].addTraverseListener(goupdownArrowListener);
			}
			// last
			list[isize - 1].addTraverseListener(goupArrowListener);
			return (Control) list[isize - 1];
		}
		return null;
	}

}
