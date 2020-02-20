/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;

/**
 * 进行机构选择使用的Dialog
 * @author sjz
 *
 */
public class SelectUnitDialog extends TitleAreaDialog {
	private Table table;
	private TableViewer tableViewer;
	private List<TsOrganDto> selectedOrgs;
	//JAF数据绑定对象
	private DataBindingContext context;

	public SelectUnitDialog(Shell parentShell) {
		super(parentShell);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("机构信息");
	}
	
	/**
	 * 设置窗口的缺省大小
	 */
	protected Point getInitialSize() {
		return new Point(530, 398);
	}
	
	protected Control createDialogArea(Composite parent) {
		context = DataBindingContext.createInstance("c7f46af5-39fd-44f4-a8de-51c1bebe029d");
		
		setTitle("请选择接收机构");
		Composite area = (Composite)super.createDialogArea(parent);
		
		final Composite composite = new Composite(area, SWT.NONE);
		composite.setLayout(new FormLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(0, 370);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, 20);
		formData.left = new FormAttachment(0,5);
		
		final Button btnAllSelect = new Button(composite, SWT.CHECK);
		btnAllSelect.setText("全选");
		btnAllSelect.setBounds(30, 38, 57, 30);

		table = new Table (composite, SWT.CHECK | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(formData);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		table.addSelectionListener(new OneRowSelection());
		tableViewer = new TableViewer(table);
		
		TableFacadeX tableFacade = new TableFacadeX(tableViewer);
		tableFacade.addColumn("接收机构代码  ", 150, "sorgcode");
		tableFacade.addColumn("接收机构名称  ", 200, "sorgname");

		btnAllSelect.addSelectionListener(new AllButtonSelection());
		context.bindTableViewer2X(tableViewer, "allRecvOrgs");
		
		return area;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", false);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	@Override
	protected void okPressed() {
		selectedOrgs = new ArrayList<TsOrganDto>();
		for (TableItem item : table.getItems()){
			if (item.getChecked()){
				//如果被选中
				TsOrganDto one = (TsOrganDto)item.getData();
				selectedOrgs.add(one);
			}
		}
		super.okPressed();
	}

	public List<TsOrganDto> getSelectedOrgs() {
		return selectedOrgs;
	}

	public void setSelectedOrgs(List<TsOrganDto> selectedOrgs) {
		this.selectedOrgs = selectedOrgs;
	}
	
	/**
	 * 全选框的点击事件
	 * 选中全选框，接收机构全部选中，取消全选框，取消全部选择
	 * @author linxia
	 * 2007-12-14 上午01:02:25
	 */
	private class AllButtonSelection extends SelectionAdapter{
		public void widgetSelected(final SelectionEvent e) {
			Button btn = (Button)e.getSource();
			boolean bSelection  = btn.getSelection();
			for (TableItem item : table.getItems()){
				item.setChecked(bSelection);
			}
		}
	}
	
	/**
	 * Table中一行被选中的事件
	 * @author Administrator
	 */
	private class OneRowSelection extends SelectionAdapter{
		public void widgetSelected(SelectionEvent e) {
			TableItem item = (TableItem)e.item;
			if (item != null){
				if (item.getChecked()){
					item.setChecked(false);
				}else{
					item.setChecked(true);
				}
			}
			super.widgetSelected(e);
		}
	}
	
}
