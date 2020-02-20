package com.cfcc.itfe.client.login;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import com.cfcc.itfe.client.processor.BlankKeyListener;
import com.cfcc.itfe.facade.StringPatternCheckFacade;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.SWTResourceManager;

public class ModifyPasswordDialog extends TitleAreaDialog {
	private Text oldPasswordText;
	private Text newPasswordText;
	private Text confirmNewPasswordText;

	private TsUsersDto userdto;
	private String oldPassword;
	private String newPassword;
	private String confirmNewPassword;

	/**
	 * service
	 * 
	 * @wbp.parser.constructor
	 */

	public ModifyPasswordDialog(Shell parentShell) {
		super(parentShell);
	}

	public ModifyPasswordDialog(Shell parentShell, TsUsersDto userdto) {
		this(parentShell);
		this.userdto = userdto;
	}

	protected Control createContents(Composite parent) {
		super.createContents(parent);
		ITFELoginInfo loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		if (!loginInfo.isInvalidation()) {
			this.setMessage("系统提示:\n密码已过期,请修改密码! .密码规则：数字、字符和字母组成，长度最低八位!",
					IMessageProvider.WARNING);
		}else {
			this.setMessage("系统提示:\n初次登录请先修改初始密码 .密码规则：数字、字符和字母组成，长度最低八位!",
					IMessageProvider.WARNING);
		}
		
		
		return parent;
	}

	protected void configureShell(Shell newShell) {
		newShell
				.setText("\u56FD\u5E93\u524D\u7F6E\u7CFB\u7EDF-\u7528\u6237\u5BC6\u7801\u4FEE\u6539");
		super.configureShell(newShell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

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

		Label label = new Label(container, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label.setAlignment(SWT.RIGHT);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label.setText("原密码");

		oldPasswordText = new Text(container, SWT.PASSWORD | SWT.BORDER);
		oldPasswordText.setTextLimit(32);
		oldPasswordText.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gridData.widthHint = 150;
		oldPasswordText.setLayoutData(gridData);

		Label label1 = new Label(container, SWT.NONE);
		label1.setText("\u65B0\u5BC6\u7801");
		label1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label1.setAlignment(SWT.RIGHT);
		label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label1.setText("新密码");

		newPasswordText = new Text(container, SWT.PASSWORD | SWT.BORDER);
		newPasswordText.setTextLimit(32);
		newPasswordText.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		newPasswordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));

		Label label2 = new Label(container, SWT.NONE);
		label2.setText("\u786E\u8BA4\u5BC6\u7801");
		label2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label2.setText("确认密码");

		confirmNewPasswordText = new Text(container, SWT.PASSWORD | SWT.BORDER);
		confirmNewPasswordText.setTextLimit(32);
		confirmNewPasswordText.setFont(SWTResourceManager.getFont("", 11,
				SWT.NONE));
		confirmNewPasswordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false));
		
		
		Control[] list  = {oldPasswordText,newPasswordText,confirmNewPasswordText};
		AddListenerHelper.addListeners(list, new BlankKeyListener());
		
		confirmNewPasswordText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					buttonPressed(IDialogConstants.OK_ID);
				}
			}
		});

		return container;

	}

	protected Point getInitialSize() {
		return new Point(525, 375);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		parent.setToolTipText("");
		final Button button = createButton(parent, IDialogConstants.OK_ID,
				"确定", false);
		button.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button_1 = createButton(parent,
				IDialogConstants.CANCEL_ID, "取消", false);
		button_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			oldPassword = oldPasswordText.getText();
			newPassword = newPasswordText.getText();
			confirmNewPassword = confirmNewPasswordText.getText();
			if (checkValid())
				okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected boolean checkValid() {
		String promptMessage = "";
		if (StringUtils.isBlank(oldPassword)) {
			promptMessage = "原密码不能为空";
		} else if (StringUtils.isBlank(newPassword)) {
			promptMessage = "新密码不能为空";
		} else if (StringUtils.isBlank(confirmNewPassword)) {
			promptMessage = "确认密码不能为空";
		} else if (!newPassword.equals(confirmNewPassword)) {
			promptMessage = "两次密码不一样";
		} else if (!new Md5App().makeMd5(oldPassword).equals(userdto.getSpassword())) {
			promptMessage = "原密码输入错误";
		} else if (oldPassword.equals(newPassword)) {
			promptMessage = "请输入一个不同的新密码";
		} else if (!StringPatternCheckFacade.isNumCharAndCell(newPassword)) {
			promptMessage = "新密码必须是数字，字母和字符的组合！";
		} else if (newPassword.length() < 8) {
			promptMessage = "新密码长度小于8位";
		}

		if (!"".equals(promptMessage)) {
			MessageDialog.openMessageDialog(null, promptMessage);
			return false;
		}
		return true;

	}

	public Text getOldPasswordText() {
		return oldPasswordText;
	}

	public void setOldPasswordText(Text oldPasswordText) {
		this.oldPasswordText = oldPasswordText;
	}

	public Text getNewPasswordText() {
		return newPasswordText;
	}

	public void setNewPasswordText(Text newPasswordText) {
		this.newPasswordText = newPasswordText;
	}

	public Text getConfirmNewPasswordText() {
		return confirmNewPasswordText;
	}

	public void setConfirmNewPasswordText(Text confirmNewPasswordText) {
		this.confirmNewPasswordText = confirmNewPasswordText;
	}

	public TsUsersDto getUserdto() {
		return userdto;
	}

	public void setUserdto(TsUsersDto userdto) {
		this.userdto = userdto;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}
}
