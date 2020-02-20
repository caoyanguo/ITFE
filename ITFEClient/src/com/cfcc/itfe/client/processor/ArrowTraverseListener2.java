/**
 * 
 */
package com.cfcc.itfe.client.processor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

/**
 * @author gaoym
 * 
 * �����¼�������traverse
 */
public class ArrowTraverseListener2 implements TraverseListener {


	/*
	 * ���� Javadoc��
	 * 
	 * @see
	 * org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse.swt.
	 * events.TraverseEvent)
	 */
	public void keyTraversed(TraverseEvent event) {


		switch (event.keyCode) {
		
		case 13:
			event.detail = SWT.TRAVERSE_TAB_PREVIOUS;
			event.doit = true;
			break;
		default:
			return;
		}
	}

}