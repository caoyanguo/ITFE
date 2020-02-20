/**
 * 
 */
package com.cfcc.itfe.client.recbiz.certrec;

import itferesourcepackage.RetCodeEnumFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.common.report.ReportComposite;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.persistence.dto.ComboListDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.rcp.databinding.binding.ReportBinding;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.util.Mapper;
import com.jasperassistant.designer.viewer.util.JasperConstants;

/**
 * 报文接收窗口
 * @author sjz
 *
 */
public class ReceiveUIComposite extends Composite {
	//receive date list
	CCombo comboRecvDate;
	//download path
	Text textDownLoadPath;
	//backup path
	Text textBackupPath;
	//receive logs table
	Table recvLogTable;
	TableViewer tableViewer;
	//button to select all datas 
	Button btnSelectAll;
	//statics report
	private ReportComposite report;
	//JAF数据绑定对象
	private DataBindingContext context;
	
	/**
	 * @param parent
	 */
	public ReceiveUIComposite(Composite parent) {
		super(parent, SWT.NONE);
		try {
			context = DataBindingContext.createInstance("7dc3f614-bb7d-4118-8cbf-a24f4eda8b4a");
			
			setLayout(new FormLayout());
			final Composite composite = new Composite(this, SWT.NONE);
			final FormData formData = new FormData();
			formData.bottom = new FormAttachment(100, -5);
			formData.right = new FormAttachment(100, -5);
			formData.top = new FormAttachment(0, 0);
			formData.left = new FormAttachment(0, 0);
			composite.setLayoutData(formData);
			
			//Receive date
			Label label_1 = new Label(composite,SWT.NONE);
			label_1.setText("接收日期");
			label_1.setBounds(9, 15, 52, 16);
			comboRecvDate = new CCombo(composite,SWT.BORDER);
			comboRecvDate.setBounds(64, 10, 105, 20);
			comboRecvDate.setEnabled(true);
			comboRecvDate.setEditable(true);
			comboRecvDate.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					queryRecvLog(e);
				}
			});
			comboRecvDate.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e) {
					if((e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)){
						queryRecvLog(e);
					}
				}
			});
			
			//download path
			Label label_2 = new Label(composite,SWT.NONE);
			label_2.setBounds(176, 41, 47, 16);
			label_2.setText("下载路径");
			textDownLoadPath = new Text(composite, SWT.BORDER);
			textDownLoadPath.setEditable(false);
			textDownLoadPath.setBounds(232, 37, 610, 20);
			Button btnDownLoadPath = new Button(composite,SWT.NONE);
			btnDownLoadPath.setText("选择下载路径");
			btnDownLoadPath.setBounds(848, 37, 100, 20);
			btnDownLoadPath.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					((ReceiveUIBean)context.getModelHolder().getModel()).pathSelect(e);
				}
				
			});
			
			//backup path
			Label label_3 = new Label(composite,SWT.NONE);
			label_3.setBounds(176, 15, 53, 16);
			label_3.setText("备份路径");
			textBackupPath = new Text(composite,SWT.BORDER);
			textBackupPath.setEditable(false);
			textBackupPath.setBounds(232, 10, 610, 20);
			Button btnBackupPath = new Button(composite,SWT.NONE);
			btnBackupPath.setText("选择备份路径");
			btnBackupPath.setBounds(848, 10, 100, 20);
			btnBackupPath.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					((ReceiveUIBean)context.getModelHolder().getModel()).backupPathSelect(e);
				}
			});
			
			//select all button
			btnSelectAll = new Button(composite,SWT.CHECK);
			btnSelectAll.setBounds(10,45,100,15);
			btnSelectAll.setText("全选");
			btnSelectAll.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					Button btn = (Button)e.getSource();
					boolean bSelection  = btn.getSelection();
					for (TableItem item : recvLogTable.getItems()){
						item.setChecked(bSelection);
					}
				}
			});
			
			//table to show receive logs
			recvLogTable = new Table (composite, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
			recvLogTable.setBounds(6, 60, 980, 280);
			recvLogTable.setLinesVisible (true);
			recvLogTable.setHeaderVisible (true);
			recvLogTable.setEnabled(true);
			recvLogTable.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
			tableViewer = new TableViewer(recvLogTable);
			tableViewer.getTable().setData(MetaDataConstants.TABLE_LABEL_PROVIDER, "com.cfcc.itfe.client.common.table.LogLabelProvider");
			//all Column in table
			//处理码的枚举值
			RetCodeEnumFactory enumRet = new RetCodeEnumFactory();
			List<Mapper> retList = enumRet.getEnums(null);//处理码
			
			TableFacadeX tableFacade = new TableFacadeX(tableViewer);
			tableFacade.addColumn("处理状态", 150, "sretcode",retList);
			tableFacade.addColumn("接收时间  ", 200, "srecvtime");
			tableFacade.addColumn("发送机构  ", 100, "sorgname");
			tableFacade.addColumn("文件类型  ", 100, "soperationtypename");
			tableFacade.addColumn("文件标题  ", 300, "stitle");
			//click adapter
			recvLogTable.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					TableItem item = (TableItem)e.item;
					if (item != null){
						int row = recvLogTable.indexOf(item);
						if (item.getChecked()){
							item.setChecked(false);
							recvLogTable.select(row);
						}else{
							item.setChecked(true);
						}
					}
					super.widgetSelected(e);
				}
			});
			
			//Report table
			report = new ReportComposite(composite, JasperConstants.ALL);
			report.setBounds(6, 341, 980, 200);
			//设置背景颜色，和ActiveX控件一致
			report.setBackground(new Color(composite.getDisplay(),new RGB(192,192,192)));
			
			//some buttons
			Button btnQuery = new Button(composite,SWT.NONE);
			btnQuery.setBounds(89, 545, 90, 20);
			btnQuery.setText("查    询");
			btnQuery.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					queryRecvLog(e);
				}
			});
			Button btnDetail = new Button(composite,SWT.NONE);
			btnDetail.setBounds(268, 545, 90, 20);
			btnDetail.setText("详细信息");
			btnDetail.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					if (recvLogTable.getSelectionCount() == 0){
						MessageDialog.openInformation(recvLogTable.getShell(), "提示", "请选择要查看的记录。");
						return;
					}
					TvRecvLogShowDto selected = (TvRecvLogShowDto)(recvLogTable.getSelection())[0].getData();
					((ReceiveUIBean)context.getModelHolder().getModel()).setSelectedRow(selected);
					((ReceiveUIBean)context.getModelHolder().getModel()).queryCerDetail(e);
				}
			});
			Button btnRecvCancel = new Button(composite,SWT.NONE);
			btnRecvCancel.setBounds(447, 545, 90, 20);
			btnRecvCancel.setText("作   废");
			btnRecvCancel.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					if (recvLogTable.getSelectionCount() == 0){
						MessageDialog.openInformation(recvLogTable.getShell(), "提示", "请选择要作废的记录。");
						return;
					}
					if (MessageDialog.openQuestion(recvLogTable.getShell(), "提示", "是否要作废选中的记录？")){
						TvRecvLogShowDto selected = (TvRecvLogShowDto)(recvLogTable.getSelection())[0].getData();
						((ReceiveUIBean)context.getModelHolder().getModel()).setSelectedRow(selected);
						((ReceiveUIBean)context.getModelHolder().getModel()).recvDelete(e);
					}
				}
			});
			Button btnRecvReport = new Button(composite,SWT.NONE);
			btnRecvReport.setBounds(626, 545, 90, 20);
			btnRecvReport.setText("接收信息统计");
			btnRecvReport.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					((ReceiveUIBean)context.getModelHolder().getModel()).queryRecvLogReport(e);
				}
			});
			Button btnDownLoad = new Button(composite,SWT.NONE);
			btnDownLoad.setBounds(805, 545, 90, 20);
			btnDownLoad.setText("附件下载");
			btnDownLoad.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					//find all selected rows in table
					List<TvRecvLogShowDto> selectedRows = new ArrayList<TvRecvLogShowDto>();
					for (TableItem row : recvLogTable.getItems()){
						if (row.getChecked()){
							selectedRows.add((TvRecvLogShowDto)row.getData());
						}
					}
					((ReceiveUIBean)context.getModelHolder().getModel()).setSelectedRecvLogs(selectedRows);
					((ReceiveUIBean)context.getModelHolder().getModel()).downloadSelectedFiles(e);
				}
			});
			
			//data bind
			List<ComboListDto> dates = ((ReceiveUIBean)context.getModelHolder().getModel()).getDateList();
			for (ComboListDto one : dates){
				comboRecvDate.add(one.getData());
			}
			comboRecvDate.setText(((ReceiveUIBean)context.getModelHolder().getModel()).getRecvDate());
			context.bindText(textDownLoadPath, "downloadPath");
			context.bindText(textBackupPath, "backupPath");
			context.bindTableViewer2X(tableViewer, "recvLogs");
			
			//显示统计信息报表
			((ReceiveUIBean)context.getModelHolder().getModel()).queryRecvLog(report);
			
			this.layout(true);
		} catch (Throwable e) {
//			MessageDialog.openErrorDialog(null, e);
		}
		
		
	}
	
	/**
	 * 查询指定日期的接收日志
	 * @param o
	 */
	private void queryRecvLog(Object o){
		//设置查询日期
		((ReceiveUIBean)context.getModelHolder().getModel()).setRecvDate(comboRecvDate.getText());
		//查询接收日志
		((ReceiveUIBean)context.getModelHolder().getModel()).queryRecvLog(report);
		//如果全选按钮被选中，那么设置为未选中
		if (btnSelectAll.getSelection()){
			btnSelectAll.setSelection(false);
		}
	}
	
	public ReportComposite getReport() {
		return report;
	}

}
