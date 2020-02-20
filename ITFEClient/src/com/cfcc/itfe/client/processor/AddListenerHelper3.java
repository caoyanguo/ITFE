package com.cfcc.itfe.client.processor;

import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author gaoym
 * 
 */
public class AddListenerHelper3 {
	
	private static final ArrowTraverseListener goupdownArrowListener = new ArrowTraverseListener(
			true, true);

	private static final ArrowTraverseListener goupArrowListener = new ArrowTraverseListener(
			true, false);

	private static final ArrowTraverseListener godownArrowListener = new ArrowTraverseListener(
			false, true);

	/**
	 * 为Composite上每个Text控件，Combo控件加上光标上下键监听，回车键监听。
	 * 
	 * @param composite
	 */
	public static Control addListeners(Control[] list) {

		if (list != null && list.length == 1) {
			return (Control) list[0];
		}
		if (list != null && list.length > 0) {
			int isize = list.length;
			// first
			list[0].addTraverseListener(godownArrowListener);
			// detail
			for (int k = 1; k < isize - 1; k++) {
				list[k].addTraverseListener(goupdownArrowListener);
			}
			// last
			list[isize - 1].addTraverseListener(goupArrowListener);
			return (Control) list[isize - 1];
		}
		return null;
	}


}
