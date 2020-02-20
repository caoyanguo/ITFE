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
import com.cfcc.jaf.rcpx.processor.AddListenerHelper;
import com.cfcc.jaf.rcpx.processor.BlankKeyListener;


public class LoginSetDialog extends TitleAreaDialog {

	private Text ssl;

	private Text port;

	private Text ip;

	private String strssl;

	private String strport;

	private String strip;

	public String getStrip() {
		return strip;
	}

	public void setStrip(String strip) {
		this.strip = strip;
	}

	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public LoginSetDialog(Shell parentShell) {
		
		
		super(parentShell);
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

		final Label label1 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label1.setAlignment(SWT.RIGHT);
		label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		//label1.setText("IP地址：");
		label1.setText("IP地址：");

		ip = new Text(container, SWT.BORDER);
		ip.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		// ip.setTextLimit(12);
		final GridData gridData1 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData1.heightHint = 16;
		gridData1.widthHint = 129;
		ip.setLayoutData(gridData1);

		final Label label2 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		//label2.setText("端口：");
		label2.setText("HTTP端口：");

		port = new Text(container, SWT.BORDER);
		// port.setTextLimit(30);
		port.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData2.heightHint = 16;
		gridData2.widthHint = 150;
		port.setLayoutData(gridData2);

		final Label label3 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label3.setAlignment(SWT.RIGHT);
		label3.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		//label3.setText("SSL：");
		label3.setText("HTTPS端口：");

		ssl = new Text(container, SWT.BORDER);
		// ssl.setTextLimit(8);
		ssl.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final GridData gridData3 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData3.heightHint = 16;
		gridData3.widthHint = 93;
		ssl.setLayoutData(gridData3);

		setMessage("请输入系统登录配置信息");

		Control[] list = new Control[] { ip, port, ssl };
		AddListenerHelper.addListeners(list, new BlankKeyListener());
		ssl.addTraverseListener(new TraverseListener() {

			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					buttonPressed(IDialogConstants.OK_ID);
				}
			}
		});
		this.port.setText(SetupHelper.getSPort());
		this.ssl.setText(SetupHelper.getSPortSSL());
		this.ip.setText(SetupHelper.getSServerAddress());
		
		return area;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			this.strip = ip.getText();
			this.strssl = ssl.getText();
			this.strport = port.getText();
			
			SetupHelper.setSPort(this.strport);
			SetupHelper.setSPortSSL(this.strssl);
			SetupHelper.setSServerAddress(this.strip);
			SetupHelper.loginSetupWriteToDisk();
			// ApplicationContext.getInstance().setLoginInfo(userCode.getText(),
			// password.getText());
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		final Button button = createButton(parent, IDialogConstants.OK_ID,
				"确定", false);
		button.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button_1 = createButton(parent,
				IDialogConstants.CANCEL_ID, "取消", false);
		button_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
	}

	protected Point getInitialSize() {
		return new Point(500, 375);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("用户登录设置");
	}

	public String getStrssl() {
		return strssl;
	}

	public void setStrssl(String strssl) {
		this.strssl = strssl;
	}

	public String getStrport() {
		return strport;
	}

	public void setStrport(String strport) {
		this.strport = strport;
	}

}
