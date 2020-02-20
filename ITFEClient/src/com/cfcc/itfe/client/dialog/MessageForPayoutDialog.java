package com.cfcc.itfe.client.dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MessageForPayoutDialog extends MessageDialog {
	
	private Control customArea;
	
	public MessageForPayoutDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 打开一个信息提示框
	 * 
	 * @param shell
	 * @param info
	 */
	public static void openMessageDialog(Shell shell, String info) {
		// 打开一个提示框。
		MessageForPayoutDialog dialog =new MessageForPayoutDialog(
				shell, "信息提示",
				null, info, // text to be displayed
				org.eclipse.jface.dialogs.MessageDialog.INFORMATION, // dialog type
				new String[] { "确定" }, // button labels
				0);
		dialog.open();
	}
	
	 protected boolean customShouldTakeFocus() {
	        if (customArea instanceof Label) {
				return false;
			}
	        if (customArea instanceof CLabel) {
				return (customArea.getStyle() & SWT.NO_FOCUS) > 0;
			}
	        return true;
	    }
	
	 protected Button createButton(Composite parent, int id, String label,
	            boolean defaultButton) {
	        Button button = super.createButton(parent, id, label, defaultButton);
	        //Be sure to set the focus if the custom area cannot so as not
	        //to lose the defaultButton.
	        if (defaultButton && !customShouldTakeFocus()) {
				button.forceFocus();
			}
	        return button;
	    }
	 
	public static void main(String[] args) {
//		MessageForPayoutDialog.openMessageDialog(new Shell(), "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111测试一下什么情况");
		MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
				new Shell(), "销号提示", null, "查找到对应记录，是否确认销号\n",
				org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
						"确认", "取消" }, 1);
		if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
			System.out.println("我看看！！！");
		}
	}
}

