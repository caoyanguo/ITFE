package com.cfcc.itfe.client.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.rcpx.processor.AddListenerHelper;
import com.cfcc.jaf.rcpx.processor.BlankKeyListener;
import com.cfcc.jaf.ui.util.Mapper;

import itferesourcepackage.RetCodeEnumFactory;
import itferesourcepackage.StampuserEnumFactory;

public class EditStampUserMsgDialog extends TitleAreaDialog {
	
	private Shell shell;
	/** 下拉框 :选择的签章用户 */
	private Combo msgc;

	/** 提示消息 */
	private String msg;
	private List<Mapper> maperlist;
	private String selectmsg = null;
	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public EditStampUserMsgDialog(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}
	/**
	 * @wbp.parser.constructor
	 */
	public EditStampUserMsgDialog(Shell parentShell, String msg) {
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
		gridLayout.marginTop = 10;
		gridLayout.marginRight = 100;
		gridLayout.marginLeft = 50;
		gridLayout.marginBottom = 50;
		gridLayout.horizontalSpacing = 20;
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

		final Label label1 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label1.setAlignment(SWT.RIGHT);
		label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label1.setText("签章用户：");

		/** 签章用户 */
		msgc = new Combo(container, SWT.BORDER);
		msgc.setTextLimit(200);
		msgc.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		if(maperlist==null||maperlist.size()>0)
			maperlist = new StampuserEnumFactory().getEnums(null);
		for (Mapper map : maperlist) {
			msgc.add(String.valueOf(map.getUnderlyValue()) + "***" + map.getDisplayValue());
		}
		final GridData gridData1 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData1.heightHint = 20;
		gridData1.widthHint = 250;
		msgc.setLayoutData(gridData1);
		msgc.setFocus();
		Control[] list = new Control[] { msgc };
		AddListenerHelper.addListeners(list, new BlankKeyListener());
		return area;
	}

	protected void buttonPressed(int buttonId) {

		if (buttonId == IDialogConstants.OK_ID) {
			this.selectmsg = msgc.getText();
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		final Button button = createButton(parent, IDialogConstants.OK_ID,
				"确认", false);
		button.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button_1 = createButton(parent,
				IDialogConstants.CANCEL_ID, "取消", false);
		button_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
	}

	protected Point getInitialSize() {
		return new Point(500, 375);
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("财政支出无纸化-批量修改签章用户");
		super.configureShell(newShell);
	}
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Combo getMsgc() {
		return msgc;
	}

	public void setMsgc(Combo msgc) {
		this.msgc = msgc;
	}
	public List<Mapper> getMaperlist() {
		return maperlist;
	}

	public void setMaperlist(List<Mapper> maperlist) {
		this.maperlist = maperlist;
	}

	public String getSelectmsg() {
		return selectmsg;
	}

	public void setSelectmsg(String selectmsg) {
		this.selectmsg = selectmsg;
	}
}
