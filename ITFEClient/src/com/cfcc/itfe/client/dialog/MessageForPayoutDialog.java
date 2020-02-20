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
	 * ��һ����Ϣ��ʾ��
	 * 
	 * @param shell
	 * @param info
	 */
	public static void openMessageDialog(Shell shell, String info) {
		// ��һ����ʾ��
		MessageForPayoutDialog dialog =new MessageForPayoutDialog(
				shell, "��Ϣ��ʾ",
				null, info, // text to be displayed
				org.eclipse.jface.dialogs.MessageDialog.INFORMATION, // dialog type
				new String[] { "ȷ��" }, // button labels
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
//		MessageForPayoutDialog.openMessageDialog(new Shell(), "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111����һ��ʲô���");
		MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
				new Shell(), "������ʾ", null, "���ҵ���Ӧ��¼���Ƿ�ȷ������\n",
				org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
						"ȷ��", "ȡ��" }, 1);
		if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
			System.out.println("�ҿ���������");
		}
	}
}

