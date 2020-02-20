package com.cfcc.itfe.client.processor;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.cfcc.jaf.rcp.util.SearchUtil;

/**
 * 
 * @author gaoym
 * 
 */
public class AddListenerHelper2 {
	
	private static final ArrowTraverseListener goupdownArrowListener = new ArrowTraverseListener(
			true, true);

	private static final ArrowTraverseListener goupArrowListener = new ArrowTraverseListener(
			true, false);

	private static final ArrowTraverseListener2 goupdownArrowListener2 = new ArrowTraverseListener2();

	private static final BlankKeyListener blankKeyListener = new BlankKeyListener();

	/**
	 * 为Composite上每个Text控件，Combo控件加上光标上下键监听，回车键监听。
	 * 
	 * @param composite
	 */
	public static void setArrowKeyTraversable(Composite composite) {
		Control[] controls = SearchUtil.findControl(composite, new Class[] {
				Text.class });
		addListeners(controls, blankKeyListener);
	}

	public static Control addListeners(Control[] list, KeyListener keyListener) {

		if (list != null && list.length == 1) {
			list[0].addKeyListener(keyListener);
			list[0].addTraverseListener(goupdownArrowListener2);
			return (Control) list[0];
		}else{
			int length = list.length;
			if(length == 0 )
				return null;
//			list[0].addKeyListener(keyListener);
//			list[0].addTraverseListener(goupdownArrowListener);
			for(int i = 0 ; i < length ; i ++ ){
				list[i].addKeyListener(keyListener);
				list[i].addTraverseListener(goupdownArrowListener);
			}
			list[length - 1].addKeyListener(keyListener);
			list[length - 1].addTraverseListener(goupdownArrowListener2);
			return (Control) list[list.length - 1];
		}
	}

}
