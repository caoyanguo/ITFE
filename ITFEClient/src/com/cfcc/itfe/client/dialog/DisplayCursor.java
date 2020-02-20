package com.cfcc.itfe.client.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DisplayCursor {

	public static void setCursor(int style) {

		Shell shel = Display.getDefault().getShells()[0];
		shel.setActive();
		Cursor cursor = shel.getCursor();
		if (cursor != null) {
			cursor.dispose();
		}
		Cursor waitCursor = new Cursor(Display.getDefault(), style);
		shel.setCursor(waitCursor);
	}

}
