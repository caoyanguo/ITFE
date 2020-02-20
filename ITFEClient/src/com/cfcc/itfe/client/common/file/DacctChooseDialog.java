package com.cfcc.itfe.client.common.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.scheduling.timer.TimerFactoryBean;

import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.util.CommonUtil;


public class DacctChooseDialog extends TitleAreaDialog {
	private Text dacct;
	private String dacctstr;

	public DacctChooseDialog(Shell parentShell)   {
		super(parentShell);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("输入8位账务日期(YYYYMMDD)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	protected Point getInitialSize() {
		return new Point(550, 300);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse
	 * .swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		setTitle("输入账务日期(YYYYMMDD)");
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 100;
		gridLayout.horizontalSpacing = 60;
		gridLayout.marginTop = 50;
		gridLayout.marginRight = 50;
		gridLayout.marginLeft = 50;
		gridLayout.marginBottom = 50;
		container.setLayout(gridLayout);
		final GridData gd_container = new GridData(SWT.CENTER, SWT.FILL, true, true);
		gd_container.widthHint = 400;
		container.setLayoutData(gd_container);


		final Label lab_errtype = new Label(container, SWT.NONE);
		lab_errtype.setText("请输入8位账务日期:");
		dacct=new Text(container,SWT.NONE);
		dacct.setText(TimeFacade.getCurrentStringTime());
		final GridData gd_serrtype = new GridData(SWT.FILL, SWT.CENTER, true, false);
		dacct.setLayoutData(gd_serrtype);

		return area;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", false);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {

		if (IDialogConstants.OK_ID == buttonId) {
			this.dacctstr=this.dacct.getText();
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}


	public String getDacctstr() {
		return dacctstr;
	}


	public void setDacctstr(String dacctstr) {
		this.dacctstr = dacctstr;
	}

	
	

}
