package com.cfcc.itfe.client.dialog;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.processor.AddListenerHelper;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.rcpx.processor.BlankKeyListener;

public class BursarAffirmDialog extends TitleAreaDialog {
	
	

	private Shell shell;
	
	/** 文本 :密码 */
	private Text password; 
	/** 文本 :用户ID */
	private Text userCode;
	/** 密码 */
	private String loginPassword;
	/** 用户ID */
	private String loginUserCode;
	
	/**提示消息*/
	private String msg;

	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public BursarAffirmDialog(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public BursarAffirmDialog(Shell parentShell,String msg) {
		super(parentShell);
		this.shell = parentShell;
		this.msg = msg;
	}

	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 20;
		gridLayout.marginTop = 50;
		gridLayout.marginRight = 100;
		gridLayout.marginLeft = 100;
		gridLayout.marginBottom = 50;
		gridLayout.horizontalSpacing = 20;
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
	
		

		/** 用户编号 */
		final Label label2 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label2.setText("用户名：");

		userCode = new Text(container, SWT.BORDER);
		userCode.setTextLimit(6);
		userCode.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

		final GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, false,false);
		gridData2.heightHint = 16;
		gridData2.widthHint = 150;
		userCode.setLayoutData(gridData2);
		userCode.setFocus();

		/** 用户密码： */
		final Label label3 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label3.setAlignment(SWT.RIGHT);
		label3.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label3.setText("密码：");

		password = new Text(container, SWT.PASSWORD | SWT.BORDER);
		password.setTextLimit(32);
		password.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final GridData gridData3 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData3.heightHint = 16;
		gridData3.widthHint = 93;
		password.setLayoutData(gridData3);

		setMessage(msg);
		
		
		Control[] list = new Control[] {userCode, password };
		AddListenerHelper.addListeners(list, new BlankKeyListener());
		password.addTraverseListener(new TraverseListener() {

			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					buttonPressed(IDialogConstants.OK_ID);
				}
			}
		});
		return area;
	}

	protected void buttonPressed(int buttonId) {

		if (buttonId == IDialogConstants.OK_ID) {
			this.loginPassword = password.getText();
			this.loginUserCode = userCode.getText();
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		} 
	}

	protected void createButtonsForButtonBar(Composite parent) {
		parent.setToolTipText("");
		final Button button = createButton(parent, IDialogConstants.OK_ID,
				"\u5F55\u5165/\u786E\u8BA4", false);
		button.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button_1 = createButton(parent,
				IDialogConstants.CANCEL_ID, "取消", false);
		button_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
	}

	protected Point getInitialSize() {
		return new Point(500, 375);
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("\u56FD\u5E93\u524D\u7F6E\u7CFB\u7EDF-\u4E3B\u7BA1\u4F1A\u8BA1\u786E\u8BA4");
		super.configureShell(newShell);
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getLoginUserCode() {
		return loginUserCode;
	}

	public void setLoginUserCode(String loginUserCode) {
		this.loginUserCode = loginUserCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	

}
