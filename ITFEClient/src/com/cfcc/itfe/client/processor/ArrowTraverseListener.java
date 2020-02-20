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
 * 用上下键来进行traverse
 */
public class ArrowTraverseListener implements TraverseListener {

	private boolean blnArrowUp;

	private boolean blnArrowDown;

	public ArrowTraverseListener() {
		this.blnArrowUp = true;
		this.blnArrowDown = true;
	}

	public ArrowTraverseListener(boolean blnArrowUp, boolean blnArrowDown) {
		this.blnArrowUp = blnArrowUp;
		this.blnArrowDown = blnArrowDown;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see
	 * org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse.swt.
	 * events.TraverseEvent)
	 */
	public void keyTraversed(TraverseEvent event) {

		switch (event.detail) {
		case SWT.TRAVERSE_RETURN:
			if ((event.stateMask & SWT.SHIFT) != 0) {
				if (!blnArrowUp && blnArrowDown) {
					break;
				}
				event.detail = SWT.TRAVERSE_TAB_PREVIOUS;
			} else {
				if (blnArrowUp && !blnArrowDown) {
					break;
				}
				event.detail = SWT.TRAVERSE_TAB_NEXT;
			}
			break;
		default:
			break;
		}

		switch (event.keyCode) {
		case SWT.ARROW_UP:
			if (blnArrowUp) {
				event.detail = SWT.TRAVERSE_TAB_PREVIOUS;
				event.doit = true;

			}
			break;
		case SWT.ARROW_DOWN:
			if (blnArrowDown) {
				event.detail = SWT.TRAVERSE_TAB_NEXT;
				event.doit = true;
			}
			break;
		default:
			return;
		}
	}

}