package com.cfcc.itfe.client.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.processor.AddListenerHelper3;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.SWTResourceManager;

public class BudgetSubCodeDialog extends Dialog {

		/** service */
	    protected ICommonDataAccessService commonDataAccessService;
		
		private Shell shell;
		
		private Table table;
		
		private TableViewer tableViewer;
		/** 列表 :预算科目代码查询结果 */
		private Table subjectTable;
		/** JAF数据绑定对象*/
		private DataBindingContext context;

		/** 文本框 :科目代码 */
		private Text subjectcode;
		
		/** 文本框 :科目名称 */
		private Text subjectname;
		
		/**
		 * 科目代码-标签
		 */
		private Label label1;
		/**
		 * 科目名称-标签
		 */
		private Label label2;
		/**
		 * 查询按钮
		 */
		private Button button;
		
		/** 查询结果绑定 */
		List<TsBudgetsubjectDto> subjectList = new ArrayList<TsBudgetsubjectDto>();
		
		/** 选择结果绑定 */
		List<TsBudgetsubjectDto> checkSubList = new ArrayList<TsBudgetsubjectDto>();
		
		/*
		 * editorid
		 */
		private String editorid;
		
		ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		
		public BudgetSubCodeDialog(Shell parentShell) {
			super(parentShell);
		}
		
		/**
		 * Instantiate a new title area dialog.
		 * 
		 * @param parentShell
		 */
		public BudgetSubCodeDialog(Shell parentShell,ICommonDataAccessService commonDataAccessService,String editorid) {
			super(parentShell);
			this.commonDataAccessService=commonDataAccessService;
			this.editorid=editorid;
		}

		/**
		 * 设置窗口的缺省大小
		 */
		protected Point getInitialSize() {
			return new Point(750, 500);
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
			context = DataBindingContext.createInstance(editorid);

			final Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new FormLayout());
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));

			final FormData formData = new FormData();
			formData.top = new FormAttachment(0, 20);
			formData.left = new FormAttachment(0, 20);
			formData.height = 15;
			formData.width = 60;
			label1 = new Label(composite, SWT.NONE);
			label1.setLayoutData(formData);
			label1.setText("科目代码:");

			final FormData formData1 = new FormData();
			formData1.top = new FormAttachment(0, 15);
			formData1.left = new FormAttachment(0, 90);
			formData1.height = 15;
			formData1.width = 150;
			subjectcode = new Text(composite, SWT.BORDER);
			subjectcode.setTextLimit(30);
			subjectcode.setLayoutData(formData1);
			subjectcode.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
			

			final FormData formData2 = new FormData();
			formData2.top = new FormAttachment( 0, 20);
			formData2.left = new FormAttachment(0, 300);
			formData2.height = 15;
			formData2.width = 60;

			label2 = new Label(composite, SWT.NONE);
			label2.setLayoutData(formData2);
			label2.setText("科目名称:");
			
			final FormData formData3 = new FormData();
			formData3.top = new FormAttachment( 0, 15);
			formData3.left = new FormAttachment(0, 370);
			formData3.height = 15;
			formData3.width = 350;
			subjectname = new Text(composite, SWT.BORDER);
			subjectname.setTextLimit(100);
			subjectname.setLayoutData(formData3);
			subjectname.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
			subjectname.addKeyListener(new TextControlListener(composite));
			subjectcode.setFocus();


			final FormData formData4 = new FormData();
			formData4.bottom = new FormAttachment(100, -5);
			formData4.right = new FormAttachment(100, -15);
			formData4.top = new FormAttachment(label1, 20, SWT.BOTTOM);
			formData4.left = new FormAttachment(0, 15);

			table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
			table.setLayoutData(formData4);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.addSelectionListener(new OneRowSelect());
			tableViewer = new TableViewer(table);
			TableFacadeX tableFacade = new TableFacadeX(tableViewer);
			tableFacade.addColumn("科目代码  ", 200, "ssubjectcode");
			tableFacade.addColumn("科目名称  ", 400, "ssubjectname");
			context.bindTableViewer2X(tableViewer, "subjectList");
			
			final FormData formData5 = new FormData();
			formData5.bottom = new FormAttachment(100, -5);
			formData5.right = new FormAttachment(100, -5);
			formData5.top = new FormAttachment(table, 20, SWT.BOTTOM);
			formData5.left = new FormAttachment(0, 50);
			
			Control[] list = new Control[] { subjectcode,subjectname };
			AddListenerHelper3.addListeners(list);

			return composite;
		}

		/**
		 * Table中一行被选中的事件
		 * 
		 * @author Administrator
		 */
		private class OneRowSelect extends SelectionAdapter{
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				if (item != null){
					TsBudgetsubjectDto dto=(TsBudgetsubjectDto) item.getData();
					if (item.getChecked()){
						item.setChecked(false);
						checkSubList.remove(dto);
					}else{
						item.setChecked(true);
						checkSubList.add(dto);
					}
				}
				super.widgetSelected(e);
			}
		}

		private class TextControlListener extends KeyAdapter {

			public TextControlListener(Composite composite) {
				super();
			}

			public void keyPressed(KeyEvent e) {
				if (e.keyCode != SWT.Selection && e.keyCode != SWT.KEYPAD_CR) {
					return;
				}
				if (e.getSource() instanceof Text) {
					buttonPressed(IDialogConstants.SELECT_TYPES_ID);

				}
			}
		}

		public void buttonPressed(int buttonId) {

			if (buttonId == IDialogConstants.OK_ID) {
				okPressed();
			} else if (IDialogConstants.CANCEL_ID == buttonId) {
				cancelPressed();
			}else if(IDialogConstants.SELECT_TYPES_ID == buttonId){
				//根据预算科目代码和预算科目名称查询结果
				TsBudgetsubjectDto budgetsubcodedto = new TsBudgetsubjectDto();
				budgetsubcodedto.setSorgcode(loginfo.getSorgcode());
				try {
					String wheresql = " 1=1 ";
					String subcode = subjectcode.getText();
					String subname = subjectname.getText();
					if(null!=subcode&&!subcode.equals("")){
						wheresql+=" AND S_SUBJECTCODE LIKE '%" + subcode + "%'";
					}
					if(null!=subname&&!subname.equals("")){
						wheresql+=" AND S_SUBJECTNAME LIKE '%" + subname + "%'";
					}
					List<TsBudgetsubjectDto> subjectlist =  commonDataAccessService.findRsByDto(budgetsubcodedto," AND ( " + wheresql + " ) ");
					if(null==subjectlist||subjectlist.size()<=0){
						MessageDialog.openMessageDialog(null, "所查询科目代码不存在！");
						return;
					}
					this.subjectList = subjectlist;
					tableViewer.setInput(subjectList);
				} catch (Exception e) {
					MessageDialog.openErrorDialog(null, e);
				}
			}
		}

		protected void createButtonsForButtonBar(Composite parent) {
			
			button = createButton(parent,IDialogConstants.SELECT_TYPES_ID, "查询", false);
			button.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
			button.addKeyListener(new TextControlListener(parent));
			
			final Button button = createButton(parent, IDialogConstants.OK_ID,"确认", false);
			button.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
			
			final Button button_1 = createButton(parent,IDialogConstants.CANCEL_ID, "取消", false);
			button_1.setFont(SWTResourceManager.getFont("", 10, SWT.NONE));
		}

		protected void configureShell(Shell newShell) {
			newShell.setText("预算科目查询");
			super.configureShell(newShell);
		}

		public ICommonDataAccessService getCommonDataAccessService() {
			return commonDataAccessService;
		}

		public void setCommonDataAccessService(
				ICommonDataAccessService commonDataAccessService) {
			this.commonDataAccessService = commonDataAccessService;
		}

		public Text getSubjectcode() {
			return subjectcode;
		}

		public void setSubjectcode(Text subjectcode) {
			this.subjectcode = subjectcode;
		}

		public Text getSubjectname() {
			return subjectname;
		}

		public void setSubjectname(Text subjectname) {
			this.subjectname = subjectname;
		}

		public Table getTable() {
			return table;
		}

		public void setTable(Table table) {
			this.table = table;
		}

		public Table getSubjectTable() {
			return subjectTable;
		}

		public void setSubjectTable(Table subjectTable) {
			this.subjectTable = subjectTable;
		}

		public Label getLabel1() {
			return label1;
		}

		public void setLabel1(Label label1) {
			this.label1 = label1;
		}

		public Label getLabel2() {
			return label2;
		}

		public void setLabel2(Label label2) {
			this.label2 = label2;
		}

		public DataBindingContext getContext() {
			return context;
		}

		public void setContext(DataBindingContext context) {
			this.context = context;
		}

		public Button getButton() {
			return button;
		}

		public void setButton(Button button) {
			this.button = button;
		}

		public List<TsBudgetsubjectDto> getSubjectList() {
			return subjectList;
		}

		public void setSubjectList(List<TsBudgetsubjectDto> subjectList) {
			this.subjectList = subjectList;
		}

		public List<TsBudgetsubjectDto> getCheckSubList() {
			return checkSubList;
		}

		public void setCheckSubList(List<TsBudgetsubjectDto> checkSubList) {
			this.checkSubList = checkSubList;
		}

		public String getEditorid() {
			return editorid;
		}

		public void setEditorid(String editorid) {
			this.editorid = editorid;
		}
		
}
