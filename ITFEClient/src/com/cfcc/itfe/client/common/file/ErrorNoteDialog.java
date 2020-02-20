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

import com.cfcc.itfe.persistence.dto.ShiboDto;


public class ErrorNoteDialog extends TitleAreaDialog {

	private Combo shibocombo;
	private ShiboDto shibo  = new ShiboDto();
	
	//差错类型
	private List<ShiboDto> shibolist;
	private List<String> shibonamelist  = new ArrayList<String>();
	private Map<String,ShiboDto> shibomap = new HashMap<String,ShiboDto>();

	public ErrorNoteDialog(Shell parentShell,List shiboList)   {
		super(parentShell);
		this.shibolist = shiboList;
		for(ShiboDto dto:shibolist){
			shibonamelist.add(dto.getPayeracct() + "-" + dto.getPayername());    //账户名称+用户姓名
			shibomap.put(dto.getPayeracct() + "-" + dto.getPayername(), dto);
		}
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
		newShell.setText("选择付款人信息");
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
		setTitle("选择付款人信息");
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 30;
		gridLayout.horizontalSpacing = 30;
		gridLayout.marginTop = 50;
		gridLayout.marginRight = 50;
		gridLayout.marginLeft = 50;
		gridLayout.marginBottom = 50;
		container.setLayout(gridLayout);
		final GridData gd_container = new GridData(SWT.CENTER, SWT.FILL, true, true);
		gd_container.widthHint = 444;
		container.setLayoutData(gd_container);


		final Label lab_errtype = new Label(container, SWT.NONE);
		lab_errtype.setText("请选择付款人信息:");

		shibocombo = new Combo(container, SWT.NONE);
		final GridData gd_serrtype = new GridData(SWT.FILL, SWT.CENTER, true, false);
		shibocombo.setLayoutData(gd_serrtype);
		shibocombo.setItems(shibonamelist.toArray(new String[shibonamelist.size()]));
		shibocombo.setText(shibonamelist.get(0));


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
			this.setShibo(shibomap.get(shibocombo.getText()));

			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	public ShiboDto getShibo() {
		return shibo;
	}

	public void setShibo(ShiboDto shibo) {
		this.shibo = shibo;
	}

	

}
