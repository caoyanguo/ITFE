package com.cfcc.itfe.client.common.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.cfcc.jaf.rcp.util.SWTResourceManager;




public class AboutSysDialog extends TitleAreaDialog {

	private String strnewpswconfirm;

	private String strnewpsw;
	
	private String sedition;
	private String pubtime;

	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public AboutSysDialog(Shell parentShell) {

		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
//		gridLayout.verticalSpacing = 20;
		gridLayout.verticalSpacing = 15;
		gridLayout.marginTop = 20;
		gridLayout.marginRight = 50;
		gridLayout.marginLeft = 50;
		gridLayout.marginBottom = 40;
		gridLayout.horizontalSpacing = 20;
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

		final Label label1 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label1.setAlignment(SWT.RIGHT);
		label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label1.setText("系统名称：");

		final Label label = new Label(container, SWT.NONE);
//		label.setFont(SWTResourceManager.getFont("", 11, SWT.BOLD));
		label.setFont(SWTResourceManager.getFont("隶书", 13, SWT.BOLD));
		label.setAlignment(SWT.LEFT);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label.setText("财政支出无纸化项目");

		
		
		final Label label4 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label4.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label4.setAlignment(SWT.RIGHT);
		label4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label4.setText("开发单位：");

		final Label label_4 = new Label(container, SWT.NONE);
//		label_4.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label_4.setFont(SWTResourceManager.getFont("隶书", 13, SWT.BOLD));
		label_4.setAlignment(SWT.LEFT);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label_4.setText("中国金融电子化公司");
		
		final Label label3 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label3.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label3.setAlignment(SWT.RIGHT);
		label3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label3.setText("系统版本：");
		

		final Label label_3 = new Label(container, SWT.NONE);
//		label_3.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label_3.setFont(SWTResourceManager.getFont("隶书", 13, SWT.BOLD));
		label_3.setAlignment(SWT.LEFT);
		label_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		label_3.setText("推广版2.0");
		label_3.setText(sedition);
		
		final Label label2 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label2.setText("发布时间：");

		final Label label_1 = new Label(container, SWT.NONE);
//		label_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label_1.setFont(SWTResourceManager.getFont("隶书", 13, SWT.BOLD));
		label_1.setAlignment(SWT.LEFT);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label_1.setText(this.getPubtime());
		
		setMessage("");

		return area;
	}
	

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		final Button button = createButton(parent, IDialogConstants.OK_ID,
				"确定", false);
		button.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
	}

	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("关于系统");
	}

	public String getSedition() {
		return sedition;
	}

	public void setSedition(String sedition) {
		this.sedition = sedition;
	}

	public String getPubtime() {
		return pubtime;
	}

	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}
	
}
