package com.cfcc.itfe.client.dialog;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.processor.AddListenerHelper;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.rcpx.processor.BlankKeyListener;

public class ModifySubcodeDialog extends Dialog {
	
	/** 文本 :科目代码 */
	private Text subjectcode; 
	/** 科目代码 */
	private String funsubjectcode;
	
	/**提示消息*/
	private IDto dto;
	
	/**是否历史库*/
	private String info;

	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public ModifySubcodeDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public ModifySubcodeDialog(Shell parentShell,IDto dto,String info) {
		super(parentShell);
		this.dto = dto;
		this.info = info;
	}

	
	protected Point getInitialSize() {
		return new Point(500, 200);
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("修改科目代码");
		super.configureShell(newShell);
	}
	
	
	protected Control createDialog(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		return composite;
	}

	protected Control createDialogArea(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final FormData formData = new FormData();
		formData.top = new FormAttachment(0, 35);
		formData.left = new FormAttachment(0, 30);
		formData.height = 15;
		formData.width = 60;
		Label label1 = new Label(composite, SWT.NONE);
		label1.setLayoutData(formData);
		label1.setText("科目代码:");

		final FormData formData1 = new FormData();
		formData1.top = new FormAttachment(0, 30);
		formData1.left = new FormAttachment(0, 100);
		formData1.height = 15;
		formData1.width = 350;
		subjectcode = new Text(composite, SWT.BORDER);
		subjectcode.setTextLimit(30);
		subjectcode.setLayoutData(formData1);
		subjectcode.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
		
		/**根据是否历史库设置科目代码文本框初始值*/
		if(info.equals("0")){
			TvPayoutmsgsubDto subdto=(TvPayoutmsgsubDto) dto;
			subjectcode.setText(subdto.getSfunsubjectcode());
		}else if(info.equals("1")){
			HtvPayoutmsgsubDto hsubdto=(HtvPayoutmsgsubDto) dto;
			subjectcode.setText(hsubdto.getSfunsubjectcode());
		}
		subjectcode.setFocus();
		
		Control[] list = new Control[] {subjectcode };
		AddListenerHelper.addListeners(list, new BlankKeyListener());
		subjectcode.addTraverseListener(new TraverseListener() {

			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					buttonPressed(IDialogConstants.OK_ID);
				}
			}
		});

		return composite;
	}
	
	protected void buttonPressed(int buttonId) {

		if (buttonId == IDialogConstants.OK_ID) {
			this.funsubjectcode = subjectcode.getText();
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		} 
	}

	protected void createButtonsForButtonBar(Composite parent) {
		parent.setToolTipText("");
		final Button button = createButton(parent, IDialogConstants.OK_ID,"修改", false);
		button.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
		final Button button_1 = createButton(parent,IDialogConstants.CANCEL_ID, "取消", false);
		button_1.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
	}

	public Text getSubjectcode() {
		return subjectcode;
	}

	public void setSubjectcode(Text subjectcode) {
		this.subjectcode = subjectcode;
	}

	public String getFunsubjectcode() {
		return funsubjectcode;
	}

	public void setFunsubjectcode(String funsubjectcode) {
		this.funsubjectcode = funsubjectcode;
	}

	public IDto getDto() {
		return dto;
	}

	public void setDto(IDto dto) {
		this.dto = dto;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}


	
}
