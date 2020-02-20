/**
 * 
 */
package com.cfcc.itfe.client.dataquery.querylogconsole;

import itferesourcepackage.RetCodeEnumFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.ComboListDto;
import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * 报文接收窗口
 * @author sjz
 *
 */
public class QueryLogConsoleComposite extends Composite {
	//receive date list
	private CCombo comboRecvDate;
	//send data list
	private CCombo comboSendDate;
	//receive logs table
	private Table recvLogTable;
	private TableViewer tableViewer;
	//send logs table
	private Table sendLogTable;
	private TableViewer sendTableViewer;
	//JAF数据绑定对象
	private DataBindingContext context;
	
	/**
	 * @param parent
	 */
	public QueryLogConsoleComposite(Composite parent) {
		super(parent, SWT.NONE);
		try {
			context = DataBindingContext.createInstance("42294061-0479-4400-a23e-7b87acdb6e39");
			
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
			
			//table to show receive logs
			recvLogTable = new Table (composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
			recvLogTable.setBounds(6, 35, 980, 240);
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
			
			//Receive date
			Label label_2 = new Label(composite,SWT.NONE);
			label_2.setText("发送日期");
			label_2.setBounds(9, 282, 52, 16);
			comboSendDate = new CCombo(composite,SWT.BORDER);
			comboSendDate.setBounds(64, 280, 105, 20);
			comboSendDate.setEnabled(true);
			comboSendDate.setEditable(true);
			comboSendDate.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					querySendLog(e);
				}
			});
			comboSendDate.addKeyListener(new KeyAdapter(){
				public void keyReleased(KeyEvent e) {
					if((e.keyCode == SWT.CR) || (e.keyCode == SWT.KEYPAD_CR)){
						querySendLog(e);
					}
				}
			});
			
			//table to show receive logs
			sendLogTable = new Table (composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
			sendLogTable.setBounds(6, 302, 980, 240);
			sendLogTable.setLinesVisible(true);
			sendLogTable.setHeaderVisible(true);
			sendLogTable.setEnabled(true);
			sendLogTable.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
			sendTableViewer = new TableViewer(sendLogTable);
			sendTableViewer.getTable().setData(MetaDataConstants.TABLE_LABEL_PROVIDER, "com.cfcc.itfe.client.common.table.LogLabelProvider");
			//all Column in table
			//处理码的枚举值
			TableFacadeX sendTableFacade = new TableFacadeX(sendTableViewer);
			sendTableFacade.addColumn("处理状态", 150, "sretcode",retList);
			sendTableFacade.addColumn("发送时间  ", 200, "ssendtime");
			sendTableFacade.addColumn("接收机构  ", 100, "srecvorgcode");
			sendTableFacade.addColumn("文件类型  ", 100, "soperationtypename");
			sendTableFacade.addColumn("文件标题  ", 300, "stitle");
			
			//some buttons
			Button btnQuery = new Button(composite,SWT.NONE);
			btnQuery.setBounds(89, 545, 90, 20);
			btnQuery.setText("查    询");
			btnQuery.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					queryRecvLog(e);
					querySendLog(e);
				}
			});
			
			//data bind
			List<ComboListDto> recvDateList = ((QueryLogConsoleBean)context.getModelHolder().getModel()).getRecvDateList();
			for (ComboListDto one : recvDateList){
				comboRecvDate.add(one.getData());
			}
			comboRecvDate.setText(((QueryLogConsoleBean)context.getModelHolder().getModel()).getRecvDate());
			List<ComboListDto> sendDateList = ((QueryLogConsoleBean)context.getModelHolder().getModel()).getSendDateList();
			for (ComboListDto one : sendDateList){
				comboSendDate.add(one.getData());
			}
			comboSendDate.setText(((QueryLogConsoleBean)context.getModelHolder().getModel()).getSendDate());
			//接收日志
			context.bindTableViewer2X(tableViewer, "recvLogs");
			//发送日志
			context.bindTableViewer2X(sendTableViewer, "sendLogs");
			
			//检索收发日志
			((QueryLogConsoleBean)context.getModelHolder().getModel()).queryRecvLog();
			((QueryLogConsoleBean)context.getModelHolder().getModel()).querySendLog();
			
//			Timer timer = new Timer();
//			timer.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					try {
//						((QueryLogConsoleBean)context.getModelHolder().getModel()).queryRecvLog();
//						((QueryLogConsoleBean)context.getModelHolder().getModel()).querySendLog();
//					}catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}, 10000, 10000);

			this.layout(true);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(comboRecvDate.getShell(), e);
		}
		
		
	}
	
	/**
	 * 查询指定日期的接收日志
	 * @param o
	 */
	private void queryRecvLog(Object o){
		try {
			//设置查询日期
			((QueryLogConsoleBean)context.getModelHolder().getModel()).setRecvDate(comboRecvDate.getText());
			//查询接收日志
			((QueryLogConsoleBean)context.getModelHolder().getModel()).queryRecvLog();
		} catch (Exception e) {
			MessageDialog.openErrorDialog(comboRecvDate.getShell(), e);
		}
	}

	/**
	 * 查询指定日期的发送日志
	 * @param o
	 */
	private void querySendLog(Object o){
		try {
			//设置查询日期
			((QueryLogConsoleBean)context.getModelHolder().getModel()).setSendDate(comboSendDate.getText());
			//查询接收日志
			((QueryLogConsoleBean)context.getModelHolder().getModel()).querySendLog();
		} catch (Exception e) {
			MessageDialog.openErrorDialog(comboRecvDate.getShell(), e);
		}
	}

}
