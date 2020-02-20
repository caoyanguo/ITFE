package com.cfcc.itfe.client.dataquery.composite;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.rcp.util.MessageDialog;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.subsysmanage.dataquery.DataQueryBean;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;


public class DataQueryComposite extends Composite {

	private Table tableList;
	private Combo dbType;
	private Table resultTable;
	private Text sqlText;
	private Text effectNum;
	private Group group_1;
	private Group group_2;
	private List<TableColumn> cols = new ArrayList<TableColumn>();
	DataBindingContext context;
	DataQueryBean bean;
	
	/**
	 * 必须重写构造行数，否则打开视图时报异常
	 * 
	 * @param parent
	 */
	public DataQueryComposite(Composite parent) {
		this(parent, SWT.NONE);
	}
	
	public DataQueryComposite(Composite parent, int style) {
		super(parent, style);
		
		org.eclipse.swt.graphics.Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);//白色
		org.eclipse.swt.graphics.Color yellow = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);//
		
		context = DataBindingContext.createInstance("9e374098-7cd6-4593-92d8-be7a75ed25cb");
		bean = (DataQueryBean) context.getModelHolder().getModel();
		
		setLayout(new FormLayout());
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		setLayout(gridLayout);		
		
		final Group group = new Group(this, SWT.NONE);
		final GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gridData.heightHint = 120;
		gridData.widthHint = 961;
		group.setLayoutData(gridData);
		group.setText("执行语句");
		group.setLayout(new FormLayout());

		sqlText = new Text(group, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.H_SCROLL);
		sqlText.setTextLimit(10000);
		final FormData formData = new FormData();
		formData.right = new FormAttachment(0, 715);
		formData.bottom = new FormAttachment(0, 80);
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		sqlText.setLayoutData(formData);
		sqlText.setBackground(white);
		final Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(bean.search(sqlText.getText(),effectNum.getText())){
					createColumn(resultTable, bean);
				}
			}
		});
		final FormData formData_1 = new FormData();
		formData_1.bottom = new FormAttachment(0, 79);
		formData_1.top = new FormAttachment(0, 30);
		formData_1.right = new FormAttachment(0, 839);
		formData_1.left = new FormAttachment(0, 724);
		button.setLayoutData(formData_1);
		button.setText("执行");

//		dbType = new Combo(group, SWT.READ_ONLY);
//		dbType.setItems(new String[] {TmisCodeConstants.MGDB, TmisCodeConstants.ODDB, TmisCodeConstants.PTDB, TmisCodeConstants.RPDB});
////		dbType.addSelectionListener(new SelectionAdapter() {
////			public void widgetSelected(final SelectionEvent e) {
////			}
////		});
//		final FormData formData_2 = new FormData();
//		formData_2.left = new FormAttachment(0, 795);
//		formData_2.right = new FormAttachment(100, -5);
//		formData_2.top = new FormAttachment(0, 5);
//		dbType.setLayoutData(formData_2);
//
//		Label label;
//		label = new Label(group, SWT.NONE);
//		final FormData formData_3 = new FormData();
//		formData_3.bottom = new FormAttachment(0, 23);
//		formData_3.top = new FormAttachment(0, 8);
//		formData_3.right = new FormAttachment(0, 775);
//		formData_3.left = new FormAttachment(0, 725);
//		label.setLayoutData(formData_3);
//		label.setText("数据库：");

		Button export = new Button(group, SWT.NONE);
		export.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog saveDlg = new FileDialog(getShell(), SWT.SAVE);
				String filters[] = new String[1];
				filters[0] = "*.xls";
				saveDlg.setFilterExtensions(filters);
				String saveFile = saveDlg.open();
				if(saveFile == null || saveFile.length() <= 0){
					return;
				}
				export(saveFile);
			}
		});
		final FormData formData_9 = new FormData();
		formData_9.left = new FormAttachment(button, 5, SWT.RIGHT);
		formData_9.top = new FormAttachment(button, -49, SWT.BOTTOM);
		formData_9.bottom = new FormAttachment(button, 0, SWT.BOTTOM);
		formData_9.right = new FormAttachment(button, 100, SWT.RIGHT);
		export.setLayoutData(formData_9);
		export.setText("导出EXCEL");

		
		Label label2 = new Label(group, SWT.NONE);
		final FormData formData_4 = new FormData();
//		formData_4.bottom = new FormAttachment(0, 23);
		formData_4.top = new FormAttachment(0, 92);
//		formData_4.right = new FormAttachment(0, 775);
		formData_4.left = new FormAttachment(0, 10);
		label2.setLayoutData(formData_4);
		label2.setText("影响行数：");
		effectNum = new Text(group,  SWT.BORDER | SWT.WRAP );
		effectNum.setTextLimit(10000);
		final FormData formData_5 = new FormData();
//		formData_5.right = new FormAttachment(0, 715);
//		formData_5.bottom = new FormAttachment(0,0);
		formData_5.top = new FormAttachment(0,90);
		formData_5.left = new FormAttachment(0, 80);
		effectNum.setLayoutData(formData_5);
		effectNum.setBackground(white);
		label2 = new Label(group, SWT.NONE);
		final FormData formData_4_4 = new FormData();
//		formData_4.bottom = new FormAttachment(0, 23);
		formData_4_4.top = new FormAttachment(0, 92);
//		formData_4.right = new FormAttachment(0, 775);
		formData_4_4.left = new FormAttachment(0, 170);
		label2.setLayoutData(formData_4_4);
		label2.setText("(update和delete需要填写影响行数，填写的行数必须与实际影响行数一致)");
		label2.setBackground(yellow);
		////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		group_1 = new Group(this, SWT.NONE);
		group_1.setLayout(new FormLayout());
		group_1.setText("查询结果");
		group_1.setLayoutData(new GridData(960, 464));

		resultTable = new Table(group_1, SWT.FULL_SELECTION | SWT.BORDER);
		final FormData formData_6 = new FormData();
		formData_6.bottom = new FormAttachment(100, -5);
		formData_6.left = new FormAttachment(0, 5);
		formData_6.right = new FormAttachment(100, -5);
		formData_6.top = new FormAttachment(0, 5);
		resultTable.setLayoutData(formData_6);
		resultTable.setLinesVisible(true);
		resultTable.setHeaderVisible(true);
	}
	
	/**
	 * @param parent
	 * @param model
	 */
	private void createColumn(Table parent, DataQueryBean model){
		List colNames = model.getColNames();
		if(colNames == null || colNames.size() <= 0){
			return;
		}
		
		int size = cols.size();
		for(int i = 0; i < size; i++){
			cols.get(i).dispose();
		}
		cols.clear();
		
		size = colNames.size();
		for(int i = 0; i < size; i++){
			cols.add(new TableColumn(resultTable, SWT.NONE));
		}
		
		//设定表列属性
		TableColumn col = null;
		for(int i = 0; i < size; i++){
			col = cols.get(i);
			col.setWidth(100);
			col.setText((String)colNames.get(i));
		}
		
		//创建数据项目
		createItem(parent, model);
	}
	
	/**
	 * 
	 * @param parent
	 * @param model
	 */
	private void createItem(Table parent, DataQueryBean model){
		parent.removeAll();
		
		List list = model.getRows();
		int rowCount = model.getRowCount();
		int colCount = model.getColNames().size();
		if(rowCount <= 0) return;
		
		group_1.setText("查询结果[记录数:" + rowCount + "]");
		
		//生成表列
		List<TableItem> rows = new ArrayList<TableItem>(rowCount);
		for(int i = 0; i < rowCount; i++){
			rows.add(new TableItem(parent, SWT.BORDER));
		}

		//设定表列属性
		TableItem row = null;
		Object obj = "";
		for(int i = 0; i < rowCount; i++){
			row = rows.get(i);
			for(int j = 0; j < colCount; j++){
				obj = list.get(i * colCount + j);
				if(obj instanceof String){
					row.setText(j, (String)obj);
				}else if(obj instanceof Integer){
					row.setText(j, ((Integer)obj).toString());
				}else if(obj instanceof Date){
					row.setText(j, ((Date)obj).toString());
				}else if(obj instanceof Timestamp){
					row.setText(j, ((Timestamp)obj).toString());
				}else if(obj instanceof BigDecimal){
					row.setText(j, ((BigDecimal)obj).toString());
				}else if(obj instanceof Long){
					row.setText(j, ((Long)obj).toString());
				}
			}
		}
	}
	
	/**
	 * 导出查询出的数据
	 */
	private void export(String file){
		if(resultTable.getColumnCount() <= 0 || resultTable.getItemCount() <= 0){
			MessageDialog.openMessageDialog(null, "导出的数据不存在!");
			return;
		}
		
		try {
			if(!file.endsWith(".xls")){
				file += ".xls";
			}
			
			File f = new File(file);
			if(f.exists()){
				if(!org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "确认", "文件已存在要覆盖吗?")){
					return;
				}
			}
			
			//创建Excel文件
			HSSFWorkbook wb = new HSSFWorkbook();
			
			//创建表单
			HSSFSheet sheet = wb.createSheet();
			wb.setSheetName(0, f.getName());
			
			//单元格格式
			HSSFCellStyle cs = wb.createCellStyle();
			cs.setWrapText(true);
			
			//创建第一行
			HSSFRow rowHead = sheet.createRow(0);			
			TableColumn[] columns = resultTable.getColumns();
			HSSFCell cell = null;
			for (int i = 0; i < columns.length; i++) {
				cell = rowHead.createCell((short)i);
				cell.setCellStyle(cs);
				cell.setCellValue(new HSSFRichTextString(columns[i].getText()));
			}
			
			//创建剩余的行
			int rows = resultTable.getItemCount();
			TableItem items[] = resultTable.getItems();
			HSSFRow row = null;
			for (int i = 0; i < rows; i++) {
				row = sheet.createRow(i + 1);
				for(int j = 0; j < columns.length; j++){
					cell = row.createCell((short) j);
					cell.setCellValue(new HSSFRichTextString(items[i].getText(j)));
				}
			}
			//取消样式，否则出错 2012-10-10，李涛
//			for (int i = 0; i < columns.length; i++) {
//				sheet.autoSizeColumn((short) i);
//			}
			
			FileOutputStream fileOut = new FileOutputStream(f);
			wb.write(fileOut);
			fileOut.close();
		}catch (Throwable e) {
			MessageDialog.openMessageDialog(null, e.getMessage());
			return;
		}
		
		MessageDialog.openMessageDialog(null, "导出成功!");
	}

	public List<TableColumn> getCols() {
		return cols;
	}

	public void setCols(List<TableColumn> cols) {
		this.cols = cols;
	}

	public Table getTableList() {
		return tableList;
	}

	public Combo getDbType() {
		return dbType;
	}

	public Table getResultTable() {
		return resultTable;
	}

	public Text getSqlText() {
		return sqlText;
	}

	public Group getGroup_1() {
		return group_1;
	}

	public Group getGroup_2() {
		return group_2;
	}

	public DataBindingContext getContext() {
		return context;
	}

	public DataQueryBean getBean() {
		return bean;
	}

	public void setTableList(Table tableList) {
		this.tableList = tableList;
	}

	public void setDbType(Combo dbType) {
		this.dbType = dbType;
	}

	public void setResultTable(Table resultTable) {
		this.resultTable = resultTable;
	}

	public void setSqlText(Text sqlText) {
		this.sqlText = sqlText;
	}

	public void setGroup_1(Group group_1) {
		this.group_1 = group_1;
	}

	public void setGroup_2(Group group_2) {
		this.group_2 = group_2;
	}

	public void setContext(DataBindingContext context) {
		this.context = context;
	}

	public void setBean(DataQueryBean bean) {
		this.bean = bean;
	}
}

