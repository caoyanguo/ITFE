package com.cfcc.itfe.client.login;

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

import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.rcpx.processor.BlankKeyListener;
import com.cfcc.itfe.client.processor.AddListenerHelper;

public class LoginTitleDialog extends TitleAreaDialog {

	private Shell shell;
	
	/** �ı� :���� */
	private Text password; 
	/** �ı� :�û�ID */
	private Text userCode;
	/** �ı� :�������� */
	private Text orgcode;
	/** ���� */
	private String loginPassword;
	/** �û�ID */
	private String loginUserCode;
	/** �������� */
	private String loginOrgCode;

	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public LoginTitleDialog(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
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

		/** �������� */
		final Label label1 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label1.setAlignment(SWT.RIGHT);
		label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label1.setText("��¼����������룺");
		String orguser = SetupHelper.getloginorg();
		String[] orgusers = orguser.split("-");
		orgcode = new Text(container, SWT.BORDER);
		orgcode.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		orgcode.setTextLimit(12);
		orgcode.setText(orgusers[0].replace("*ORG*", ""));
		
		final GridData gridData1 = new GridData(SWT.FILL, SWT.CENTER, false,false);
		gridData1.heightHint = 16;
		gridData1.widthHint = 129;
		orgcode.setLayoutData(gridData1);
	
		

		/** �û���� */
		final Label label2 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label2.setText("�û���ţ�");

		userCode = new Text(container, SWT.BORDER);
		userCode.setTextLimit(6);
		userCode.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		userCode.setText(orgusers.length>1?orgusers[1].replace("*US*", ""):"");
		final GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, false,false);
		gridData2.heightHint = 16;
		gridData2.widthHint = 150;
		userCode.setLayoutData(gridData2);
		if ( null!= orgcode.getText()&& orgcode.getText().trim().length()>0) {
			userCode.setFocus();
		}

		/** �û����룺 */
		final Label label3 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label3.setAlignment(SWT.RIGHT);
		label3.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label3.setText("�û����룺");

		password = new Text(container, SWT.PASSWORD | SWT.BORDER);
		password.setTextLimit(32);
		password.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		if ( null!= userCode.getText()&& userCode.getText().trim().length()>0) {
			password.setFocus();
		}
		final GridData gridData3 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData3.heightHint = 16;
		gridData3.widthHint = 93;
		password.setLayoutData(gridData3);

		setMessage("������ϵͳ��¼��Ϣ");

		Control[] list = new Control[] { orgcode, userCode, password };
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
		// �������ļ�
		try {
			SetupHelper.setloginorg(orgcode.getText(),userCode.getText());
			SetupHelper.loginSetupReadFromDisk();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (buttonId == IDialogConstants.OK_ID) {
			this.loginOrgCode = orgcode.getText();
			this.loginPassword = password.getText();
			this.loginUserCode = userCode.getText();
			// ApplicationContext.getInstance().setLoginInfo(userCode.getText(),
			// password.getText());
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			System.exit(0);
			cancelPressed();

		} else if (IDialogConstants.DETAILS_ID == buttonId) {
			// д�����ļ�
			LoginSetDialog dialog = new LoginSetDialog(shell);
			if (dialog.open() == IDialogConstants.DETAILS_ID) {
				SetupHelper.loginSetupWriteToDisk();
			}
			;
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		final Button button = createButton(parent, IDialogConstants.OK_ID,
				"��¼", false);
		button.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button2 = createButton(parent,
				IDialogConstants.DETAILS_ID, "����", false);
		button2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button_1 = createButton(parent,
				IDialogConstants.CANCEL_ID, "ȡ��", false);
		button_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
	}

	protected Point getInitialSize() {
		return new Point(500, 375);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("����֧����ֽ����Ŀ�����û���¼");
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

	public String getLoginOrgCode() {
		return loginOrgCode;
	}

	public void setLoginOrgCode(String loginOrgCode) {
		this.loginOrgCode = loginOrgCode;
	}

}
