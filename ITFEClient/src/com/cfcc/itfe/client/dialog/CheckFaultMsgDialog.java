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
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.rcpx.processor.AddListenerHelper;
import com.cfcc.jaf.rcpx.processor.BlankKeyListener;
import com.cfcc.jaf.ui.util.Mapper;

import itferesourcepackage.RetCodeEnumFactory;

public class CheckFaultMsgDialog extends TitleAreaDialog {

	private List<TsCheckfailreasonDto> checkfaillist;

	private Shell shell;
	/** 下拉框 :选择的失败原因 */
	private Combo msgc;
	/** 选择的失败原因 */
	private String selectmsg;
	/** 文本 :失败原因 */
	private Text message;
	/** 失败原因 */
	private String inputmessage;

	/** 提示消息 */
	private String msg;
	
	/** 文本 :失败原因 */
	private Text failcode;
	/** 失败原因 */
	private String inputfailcode;
	
	
	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public CheckFaultMsgDialog(Shell parentShell,
			List<TsCheckfailreasonDto> checkfail) {
		super(parentShell);
		this.shell = parentShell;
		this.checkfaillist = checkfail;
	}

	/**
	 * @wbp.parser.constructor
	 */
	public CheckFaultMsgDialog(Shell parentShell, String msg) {
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
		label1.setText("失败原因：");

		/** 失败原因 */
		msgc = new Combo(container, SWT.BORDER);
		msgc.setTextLimit(200);
		msgc.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		for (TsCheckfailreasonDto dto : checkfaillist) {
			msgc.add(dto.getScheckfailcode() + "_" + dto.getScheckfaildsp());
		}

		final GridData gridData1 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData1.heightHint = 20;
		gridData1.widthHint = 250;
		msgc.setLayoutData(gridData1);
		msgc.addModifyListener(new valuechange());
		msgc.setFocus();
		
		final Label label2 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label2.setAlignment(SWT.RIGHT);
		label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label2.setText("失败原因代码:");
		
		failcode = new Text(container, SWT.BORDER);
		failcode.setTextLimit(10);
		failcode.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

		final GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData2.heightHint = 20;
		gridData2.widthHint = 250;
		failcode.setLayoutData(gridData2);
		failcode.setFocus();
		
		final Label label3 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label3.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label3.setAlignment(SWT.RIGHT);
		label3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label3.setText("失败原因描述:");

		message = new Text(container, SWT.BORDER);
		message.setTextLimit(100);
		message.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));

		final GridData gridData3 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData3.heightHint = 120;
		gridData3.widthHint = 250;
		message.setLayoutData(gridData3);
		message.setFocus();
		
		Control[] list = new Control[] { msgc,failcode, message };
		AddListenerHelper.addListeners(list, new BlankKeyListener());
		return area;
	}

	protected void buttonPressed(int buttonId) {

		if (buttonId == IDialogConstants.OK_ID) {
			this.selectmsg = msgc.getText();
			this.inputfailcode = failcode.getText();
			this.inputmessage = message.getText();
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
		newShell.setText("财政支出无纸化-失败原因");
		super.configureShell(newShell);
	}

	/**
	 * 下拉框修改事件
	 * 
	 * @author Administrator
	 */
	private class valuechange implements ModifyListener {
		
		public void modifyText(ModifyEvent e) {
			Combo c = (Combo) e.getSource();
			if (c != null) {
				if (c.getText() != null && !c.getText().equals("")) {
					failcode.setEditable(false);
					message.setEditable(false);
				} else {
					failcode.setEditable(true);
					message.setEditable(true);
				}
			}

		}
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Text getMessage() {
		return message;
	}

	public void setMessage(Text message) {
		this.message = message;
	}

	public Combo getMsgc() {
		return msgc;
	}

	public void setMsgc(Combo msgc) {
		this.msgc = msgc;
	}

	public List<TsCheckfailreasonDto> getCheckfaillist() {
		return checkfaillist;
	}

	public void setCheckfaillist(List<TsCheckfailreasonDto> checkfaillist) {
		this.checkfaillist = checkfaillist;
	}

	public String getSelectmsg() {
		return selectmsg;
	}

	public void setSelectmsg(String selectmsg) {
		this.selectmsg = selectmsg;
	}

	public String getInputmessage() {
		return inputmessage;
	}

	public void setInputmessage(String inputmessage) {
		this.inputmessage = inputmessage;
	}

	public Text getFailcode() {
		return failcode;
	}

	public void setFailcode(Text failcode) {
		this.failcode = failcode;
	}

	public String getInputfailcode() {
		return inputfailcode;
	}

	public void setInputfailcode(String inputfailcode) {
		this.inputfailcode = inputfailcode;
	}
	
}
