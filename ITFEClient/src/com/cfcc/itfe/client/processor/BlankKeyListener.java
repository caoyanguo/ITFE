/**
 * 
 */
package com.cfcc.itfe.client.processor;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * @author gaoym
 * 
 */
public class BlankKeyListener extends KeyAdapter {

	private static final int KEY_BLANK = 32;

	private static final int CHAR_BLANK = ' ';

	public BlankKeyListener() {
	}

	public void keyPressed(KeyEvent event) {
		Object obj = event.getSource();
		if (obj instanceof Combo) {
			// event.doit = false;
			return;
		}
		boolean blnlegend = false;
		if (obj instanceof Text) {
			String str = ((Text) obj).getText();
			if ((str == null || ("").equals(str)) && event.keyCode == KEY_BLANK)
				blnlegend = true;
			else if ((str == null || ("").equals(str))
					&& event.character == CHAR_BLANK)
				blnlegend = true;
		}

		if (blnlegend) {
			MessageDialog.openMessageDialog(null, "¿Õ¸ñÁªÏë");
			event.doit = false;

		}
	}
}
