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
 * ���л���ѡ��ʹ�õ�Dialog
 * @author sjz
 *
 */
public class SelectUnitDialog extends TitleAreaDialog {
	private Table table;
	private TableViewer tableViewer;
	private List<TsOrganDto> selectedOrgs;
	//JAF���ݰ󶨶���
	private DataBindingContext context;

	public SelectUnitDialog(Shell parentShell) {
		super(parentShell);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("������Ϣ");
	}
	
	/**
	 * ���ô��ڵ�ȱʡ��С
	 */
	protected Point getInitialSize() {
		return new Point(530, 398);
	}
	
	protected Control createDialogArea(Composite parent) {
		context = DataBindingContext.createInstance("c7f46af5-39fd-44f4-a8de-51c1bebe029d");
		
		setTitle("��ѡ����ջ���");
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
		btnAllSelect.setText("ȫѡ");
		btnAllSelect.setBounds(30, 38, 57, 30);

		table = new Table (composite, SWT.CHECK | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(formData);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		table.addSelectionListener(new OneRowSelection());
		tableViewer = new TableViewer(table);
		
		TableFacadeX tableFacade = new TableFacadeX(tableViewer);
		tableFacade.addColumn("���ջ�������  ", 150, "sorgcode");
		tableFacade.addColumn("���ջ�������  ", 200, "sorgname");

		btnAllSelect.addSelectionListener(new AllButtonSelection());
		context.bindTableViewer2X(tableViewer, "allRecvOrgs");
		
		return area;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "ȷ��", false);
		createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
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
				//�����ѡ��
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
	 * ȫѡ��ĵ���¼�
	 * ѡ��ȫѡ�򣬽��ջ���ȫ��ѡ�У�ȡ��ȫѡ��ȡ��ȫ��ѡ��
	 * @author linxia
	 * 2007-12-14 ����01:02:25
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
	 * Table��һ�б�ѡ�е��¼�
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
