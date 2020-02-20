package com.cfcc.itfe.client.dialog;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.rcpx.processor.AddListenerHelper;
import com.cfcc.jaf.rcpx.processor.BlankKeyListener;

public class CheckVoucherStatusMsgDialog extends TitleAreaDialog {

	private List<TsCheckfailreasonDto> checkfaillist;
	private List<TvVoucherinfoDto> checklist;
	private TvVoucherinfoDto vDto;
	private Shell shell;
	private ITFELoginInfo loginInfo;
	/** 下拉框 :选择的凭证状态 */
	private Combo voucherStatusCombo;
	/** 下拉框 :选择的失败原因 */
	private Combo msgc;
	/** 选择的失败原因 */
	private String selectmsg;
	private String selectVoucherStatus;
	/** 文本 :实际支付日期 */
	private Text xpaydate;
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
	/** 实际支付日期 */
	private String inputxpaydate;

	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public CheckVoucherStatusMsgDialog(Shell parentShell,
			List<TsCheckfailreasonDto> checkfail, List<TvVoucherinfoDto> checklist) {
		super(parentShell);
		this.shell = parentShell;
		this.checkfaillist = checkfail;
		this.checklist = checklist;
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
	}

	/**
	 * @wbp.parser.constructor
	 */
	public CheckVoucherStatusMsgDialog(Shell parentShell, String msg) {
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
		gridLayout.verticalSpacing = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginRight = 100;
		gridLayout.marginLeft = 50;
		gridLayout.marginBottom = 50;
		gridLayout.horizontalSpacing = 20;
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
		
		final Label label = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label.setAlignment(SWT.RIGHT);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label.setText("选择凭证状态：");

		/** 选择凭证状态*/
		voucherStatusCombo = new Combo(container, SWT.BORDER);
		voucherStatusCombo.setTextLimit(200);
		voucherStatusCombo.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		getVoucherStatusMsg(checklist.get(0));
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData.heightHint = 10;
		gridData.widthHint = 250;
		voucherStatusCombo.setLayoutData(gridData);
		voucherStatusCombo.addModifyListener(new valuechangeVoucherStatusCombo());
		voucherStatusCombo.setFocus();
		
		final Label label4 = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
		label4.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		label4.setAlignment(SWT.RIGHT);
		label4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label4.setText("实际支付日期:");
		
		xpaydate = new Text(container, SWT.BORDER);
		xpaydate.setTextLimit(8);
		xpaydate.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		xpaydate.setText(TimeFacade.getCurrentStringTime());
		
		final GridData gridData4 = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gridData4.heightHint = 18;
		gridData4.widthHint = 250;
		xpaydate.setLayoutData(gridData4);
		xpaydate.setFocus();
		
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
		gridData1.heightHint = 30;
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
		gridData2.heightHint = 30;
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
		gridData3.heightHint = 50;
		gridData3.widthHint = 250;
		message.setLayoutData(gridData3);
		message.setFocus();
						
		Control[] list = new Control[] { voucherStatusCombo,msgc,failcode, message,xpaydate };
		AddListenerHelper.addListeners(list, new BlankKeyListener());
		return area;
	}

	protected void buttonPressed(int buttonId) {

		if (buttonId == IDialogConstants.OK_ID) {
			this.selectVoucherStatus=voucherStatusCombo.getText();
			this.selectmsg = msgc.getText();
			this.inputfailcode = failcode.getText();
			this.inputmessage = message.getText();
			this.inputxpaydate=xpaydate.getText();
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
		newShell.setText("财政支出无纸化-更改凭证状态");
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
	
	/**
	 * 下拉框修改事件
	 * 
	 * @author Administrator
	 */
	private class valuechangeVoucherStatusCombo implements ModifyListener {
		
		public void modifyText(ModifyEvent e) {
			Combo c = (Combo) e.getSource();
			if (c != null) {
				if (c.getText() != null && !c.getText().equals("")&&c.getText().equals("处理成功") ){					
					msgc.setEnabled(false);
					failcode.setEditable(false);
					message.setEditable(false);
					xpaydate.setEnabled(true);
				}else{
					xpaydate.setEnabled(false);
					msgc.setEnabled(true);
					failcode.setEditable(true);
					message.setEditable(true);		
				}
			}

		}
	}
	
	protected void getVoucherStatusMsg(TvVoucherinfoDto vDto) {
		
//		if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0 && (vDto.getSstatus().equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)|| vDto.getSstatus().equals(DealCodeConstants.VOUCHER_CHECKSUCCESS) || vDto.getSstatus().equals(DealCodeConstants.VOUCHER_CHECKFAULT)|| vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SENDED)|| vDto.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL_TIPS)|| vDto.getSstatus().equals(DealCodeConstants.PROCESS_FAIL)|| vDto.getSstatus().equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS) ))
//			voucherStatusCombo.add("退票成功");
			voucherStatusCombo.add("处理成功");	
			voucherStatusCombo.add("处理失败");
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

	public TvVoucherinfoDto getVDto() {
		return vDto;
	}

	public void setVDto(TvVoucherinfoDto dto) {
		vDto = dto;
	}

	public Combo getVoucherStatusCombo() {
		return voucherStatusCombo;
	}

	public void setVoucherStatusCombo(Combo voucherStatusCombo) {
		this.voucherStatusCombo = voucherStatusCombo;
	}

	public String getSelectVoucherStatus() {
		return selectVoucherStatus;
	}

	public void setSelectVoucherStatus(String selectVoucherStatus) {
		this.selectVoucherStatus = selectVoucherStatus;
	}

	public List<TvVoucherinfoDto> getChecklist() {
		return checklist;
	}

	public void setChecklist(List<TvVoucherinfoDto> checklist) {
		this.checklist = checklist;
	}

	public Text getXpaydate() {
		return xpaydate;
	}

	public void setXpaydate(Text xpaydate) {
		this.xpaydate = xpaydate;
		
	}

	public String getInputxpaydate() {
		return inputxpaydate;
	}

	public void setInputxpaydate(String inputxpaydate) {
		this.inputxpaydate = inputxpaydate;
	}
	
}
