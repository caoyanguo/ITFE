package com.cfcc.itfe.client.dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MessageInfoDialog extends MessageDialog {
	
	private Control customArea;
	
	public MessageInfoDialog(Shell parentShell, String dialogTitle,
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
		MessageInfoDialog dialog =new MessageInfoDialog(
				shell, "信息提示",
				null, info, // text to be displayed
				org.eclipse.jface.dialogs.MessageDialog.INFORMATION, // dialog type
				new String[] { "确定" }, // button labels
				0);
		dialog.open();
	}
	
	protected Control createDialogArea(Composite parent) {
        // create message area
        createMessageArea(parent);
        // create the top level composite for the dialog area
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginLeft =700;
//        layout.marginBottom=0;
//        layout.marginTop=0;
//        layout.marginTop=400;
//        layout.marginRight=300;
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        composite.setLayoutData(data);
        // allow subclasses to add custom controls
        customArea = createCustomArea(composite);
        //If it is null create a dummy label for spacing purposes
        if (customArea == null) {
			customArea = new Label(composite, SWT.NULL);
		}
        return composite;
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
	
	public static void main(String[] args) {
		MessageInfoDialog.openMessageDialog(new Shell(), "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111测试一下什么情况");
	}
}

