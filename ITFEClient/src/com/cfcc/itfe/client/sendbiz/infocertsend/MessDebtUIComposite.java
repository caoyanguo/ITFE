/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.infocertsend;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cfcc.devplatform.model.Textarea;
import com.cfcc.devplatform.model.impl.TextareaImpl;
import com.cfcc.itfe.client.common.file.FileOperFacade;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcpx.component.TextareaCreator;
import com.cfcc.jaf.ui.metadata.TextareaMetaData;

/**
 * 
 * @author sjz
 * 2009-10-12 19:20:02
 */
public class MessDebtUIComposite extends Composite{
	private Text sendUnitName;
	private Text sDate;
	private Text recvOrgText; 
	private Text contentText;
	private Text titleText;
	private Text textPathData;
	private Table affixTable;
	//JAF���ݰ󶨶���
	private DataBindingContext context;
	
	public MessDebtUIComposite(Composite parent) {
		super(parent, SWT.NONE);
		try {
			context = DataBindingContext.createInstance("1c2aac13-fb27-4990-8fa5-e713828fb7b0");

			setLayout(new FormLayout());
			final Composite composite = new Composite(this, SWT.NONE);
			final FormData formData = new FormData();
			formData.top = new FormAttachment(0, 5);
			formData.right = new FormAttachment(100, -5);
			formData.bottom = new FormAttachment(100, -5);
			formData.left = new FormAttachment(0, 0);
			composite.setLayoutData(formData);

			//���ͻ���
			final Label slabel = new Label(composite, SWT.NONE);
			slabel.setText("���ͻ������ƣ�");
			slabel.setBounds(103, 10, 80, 20);

			sendUnitName = new Text(composite, SWT.BORDER);
			sendUnitName.setEnabled(false);
			sendUnitName.setBounds(190, 7, 150, 22);

			//��������
			final Label dlabel = new Label(composite, SWT.NONE);
			dlabel.setText("�������ڣ�");
			dlabel.setBounds(350, 10, 59, 20);

			sDate = new Text(composite, SWT.BORDER);
			sDate.setBounds(420, 7, 80, 21);
			sDate.setEnabled(false);

			final Label label_5 = new Label(composite, SWT.NONE);
			label_5.setText("���ջ������ƣ�");
			label_5.setBounds(103, 43, 80, 17);

			recvOrgText = new Text(composite, SWT.BORDER);
			recvOrgText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			recvOrgText.setBounds(190, 40, 686, 25);
			recvOrgText.setEditable(false);

			final Button sUnit = new Button(composite, SWT.NONE);
			sUnit.setText("���ջ���ѡ��");
			sUnit.setBounds(880, 37, 84, 24);
			sUnit.addSelectionListener(new OpenSelectViewSelection());


			final Label label = new Label(composite, SWT.NONE);
			label.setText("��      �⣺");
			label.setBounds(101, 79, 80, 17);
			
			titleText = new Text(composite, SWT.BORDER);
			titleText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			titleText.setBounds(190, 76, 686, 25);
			

			final Label label_1 = new Label(composite, SWT.NONE);
			label_1.setText("��      �ݣ�");
			label_1.setBounds(102, 127, 79, 18);

			contentText = new Text(composite, SWT.BORDER | SWT.MULTI);
			contentText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			contentText.setBounds(190, 108, 686, 109);
			
			Label labelPathData = new Label(composite, SWT.NONE);
			labelPathData.setText("��     ����");
			labelPathData.setBounds(101, 251, 64, 17);
			
			textPathData = new Text(composite, SWT.BORDER);
			textPathData.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			textPathData.setBounds(190, 248, 686, 25);
			textPathData.setText("");
			
			Button btnDataSelect = new Button(composite, SWT.NONE);
			btnDataSelect.setBounds(882, 248, 84, 21);
			btnDataSelect.setText("�ļ�ѡ��");
			btnDataSelect.addSelectionListener(new FileSlectViewSelection(textPathData));

			
			final Button bUpload = new Button(composite, SWT.NONE);
			bUpload.setText("�� ��");
			bUpload.setBounds(882, 274, 84, 21);
			bUpload.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					String file = textPathData.getText().trim();
					if ((file == null) || (file.length() == 0)){
						MessageDialog.openMessageDialog(textPathData.getShell(), "��ѡ��Ҫ���ص��ļ���");
						return;
					}
					//����ļ��Ƿ��Ѿ��ϴ�
					if (isUpload(file)){
						MessageDialog.openMessageDialog(textPathData.getShell(), "�ļ�" + file + "�Ѿ����ء�");
						return;
					}
					((MessDebtSendUIBean)(context.getModelHolder().getModel())).uploadOneFile(file);
					//���ļ��ϴ��б�������һ���ļ���
					addTableItem(file);
					super.widgetSelected(e);
				}
			});
			
			final Button btnDeletFile = new Button(composite, SWT.NONE);
			btnDeletFile.setText("ɾ ��");
			btnDeletFile.setBounds(750, 274, 84, 21);
			btnDeletFile.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					int row = affixTable.getSelectionIndex();
					if (row == -1){
						MessageDialog.openMessageDialog(affixTable.getShell(), "��ѡ���Ѿ����ص��ļ���");
						return;
					}
					deleteFile(row);
					super.widgetSelected(e);
				}
			});
			
			final Label label_6 = new Label(composite, SWT.NONE);
			label_6.setText("ѡ���긽�����������ذ�ť�ϴ����������ɾ����ť����ɾ���Ѿ��ϴ��ĸ�����");
			label_6.setBounds(190, 280, 600, 21);
			
			affixTable = new Table (composite, SWT.MULTI | SWT.BORDER);
			affixTable.setBounds(101, 309, 865, 184);
			affixTable.setLinesVisible (true);
			affixTable.setHeaderVisible (true);
			
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			TableColumn column = null;
			data.heightHint = 200;
			affixTable.setLayoutData(data);
			String[] aTitles = {"       ��   ��   ��   ��                   "};
			
			for (int i=0; i<aTitles.length; i++) {
				column = new TableColumn (affixTable, SWT.CENTER);
				column.setWidth(500);
				column.setAlignment(SWT.CENTER | SWT.COLOR_WHITE);
				column.setText (aTitles [i]);
				affixTable.getColumn (i).pack ();
			}	

			final Label label_3 = new Label(composite, SWT.NONE);
			label_3.setText("�Ѽ��ظ�����Ϣ");
			label_3.setBounds(101, 290, 120, 14);

			final Button bSend = new Button(composite, SWT.NONE);
			bSend.setText("ƾ֤��Ϣ����");
			bSend.setBounds(515, 499, 95, 30);
			bSend.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					((MessDebtSendUIBean)(context.getModelHolder().getModel())).upload();
					super.widgetSelected(e);
				}
			});

			final Label label_4 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			label_4.setBounds(10, 217, 1049, 30);

			//View�еĿؼ���Model�ĳ�Ա�������а�
			context.bindText(sendUnitName, "userOrgName");
			context.bindText(sDate, "sendDate");
			context.bindText(titleText, "title");
			context.bindText(contentText, "content");
			
			this.layout(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * ����ļ��Ƿ��Ѿ�����
	 * @param file Ҫ���ص��ļ�
	 * @return true-�Ѿ����أ�false-δ����
	 */
	private boolean isUpload(String file){
		//����ļ���
		String fileName = FileOperFacade.getFileName(file);
		//����ļ��Ƿ��Ѿ��ϴ�
		TableItem[] allItems = affixTable.getItems();
		int i=0;
		for (i=0; i<allItems.length; i++){
			if (allItems[i].getText().equals(fileName))
				break;
		}
		if (i < allItems.length)
			return true;
		else
			return false;
	}
	
	/**
	 * �����ص��ļ������ӵ��б���
	 * @param file �����ļ����ļ���
	 */
	private void addTableItem(String file){
		//����ļ���
		String fileName = FileOperFacade.getFileName(file);
		TableItem item = new TableItem(affixTable,SWT.FULL_SELECTION);
		item.setText(fileName);
	}
	
	/**
	 * ɾ���б���ָ������
	 * @param index �б��е��к�
	 */
	private void deleteFile(int index){
		TableItem item = affixTable.getItem(index);
		String fileName = item.getText();
		((MessDebtSendUIBean)(context.getModelHolder().getModel())).deleteOneFile(fileName);
		//ɾ���б��е�һ���ļ�
		affixTable.remove(index);
	}
	
	/**
	 * �ļ�ѡ�񴰿�
	 */
	private class FileSlectViewSelection extends SelectionAdapter{
		private Text _textShow;
	
		public FileSlectViewSelection(Text textShow){
			_textShow = textShow;
		}
		public void widgetSelected(final SelectionEvent e){
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			FileDialog dialog = new FileDialog(shell,0);
			dialog.setFilterExtensions(new String[]{"*.*","*.*"});
			dialog.open();
			String path = dialog.getFilterPath();
			String name = dialog.getFileName();
			if ((path!=null) && (path.length() > 0) && (name!=null) && (name.length() > 0)){
				_textShow.setText(path + "\\" + name);
			}else{
				_textShow.setText("");
			}
		}
	}

	
	/**
	 * �򿪽��ջ���ѡ�񴰿�
	 */
	private class OpenSelectViewSelection extends SelectionAdapter{
		public OpenSelectViewSelection(){
			
		}
		public void widgetSelected(final SelectionEvent e){
			try {
				//��ѡ���
				SelectUnitDialog dialog = new SelectUnitDialog(recvOrgText.getShell());
				if (dialog.open() == IDialogConstants.OK_ID){
					//�����û�ѡ��Ľ��ջ���
					List<TsOrganDto> orgs = dialog.getSelectedOrgs();
					if ((orgs == null) || (orgs.size() == 0)){
						MessageDialog.openMessageDialog(recvOrgText.getShell(), "��ѡ����ջ�����");
					}
					((MessDebtSendUIBean)(context.getModelHolder().getModel())).setRecvOrgs(orgs);
					recvOrgText.setText(((MessDebtSendUIBean)(context.getModelHolder().getModel())).getRecvOrgNames());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}