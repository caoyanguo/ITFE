/**
 * 
 */
package com.cfcc.itfe.client.sendbiz.bizcertsend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.common.encrypt.KoalClientPkcs7Encrypt;
import com.cfcc.itfe.client.common.file.FileOperFacade;
import com.cfcc.itfe.client.common.report.ReportComposite;
import com.cfcc.itfe.client.common.report.ReportGen;
import com.cfcc.itfe.client.common.stamp.BaseStampHandler;
import com.cfcc.itfe.client.common.stamp.koal.KoalStampHandler;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.persistence.dto.TsOperationformDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOperationplaceDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.UserStampFuncDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.jasperassistant.designer.viewer.util.JasperConstants;

/**
 * 
 * @author sjz
 * 2009-10-12 19:20:02
 */
public class ActiveXComposite extends Composite{
	//��ʱ�ļ����Ŀ¼
    private static String tempDir = "c:/temp/";
	//����
	private Text sDate;
	//���ͻ���
	private Text sendUnitName;
	//���ջ���
	private Text recvUnitName;
	//���°�ť
	Button btnSignEseal;
	//���Ͱ�ť
	Button btnSendBill;
	//��ӡ��ť
	Button btnPrint;
	//Xml����
	private String dataXml;
	//ģ������
	private String modelXml;
	//����Ϣ
	private TsOperationformDto form;
	//����λ����Ϣ
	private Map<String, TsOperationplaceDto> place;
	//ƾ֤��ʾ�ı���
	private String percent;
	//�û�ѡ����ļ������ƣ�����Ŀ¼��
	private String fileName;
	//����״̬ 0000-δ�£�0001-�ѸǸ����£�0010-������Ͽ��Է���
	private String status;
	private OleFrame oleframe;
	private ReportComposite report;
	//����ӡ���ĵ��ö���
	private BaseStampHandler stampHandler;
	//JAF���ݰ󶨶���
	private DataBindingContext context;

	/**
	 * @param parent
	 */
	public ActiveXComposite(Composite parent) {
		super(parent, SWT.NONE);
		try {
			context = DataBindingContext.createInstance("c7f46af5-39fd-44f4-a8de-51c1bebe029d");

			setLayout(new FormLayout());
			final Composite composite = new Composite(this, SWT.NONE);
			final FormData formData = new FormData();
			formData.bottom = new FormAttachment(100, -5);
			formData.right = new FormAttachment(100, -5);
			formData.top = new FormAttachment(0, 0);
			formData.left = new FormAttachment(0, 0);
			composite.setLayoutData(formData);
			
			//���ͻ���
			final Label slabel = new Label(composite, SWT.NONE);
			slabel.setText("���ͻ������ƣ�");
			slabel.setBounds(6, 10, 84, 20);

			sendUnitName = new Text(composite, SWT.BORDER);
			sendUnitName.setEnabled(false);
			sendUnitName.setBounds(90, 7, 150, 22);

			//��������
			final Label dlabel = new Label(composite, SWT.NONE);
			dlabel.setText("�������ڣ�");
			dlabel.setBounds(250, 10, 59, 20);

			sDate = new Text(composite, SWT.BORDER);
			sDate.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			sDate.setBounds(320, 7, 80, 21);
			sDate.setEnabled(false);

			//���ջ���
			final Label rlabel = new Label(composite, SWT.NONE);
			rlabel.setText("���ջ�����");
			rlabel.setBounds(410, 10, 59, 20);

			recvUnitName = new Text(composite, SWT.BORDER);
			recvUnitName.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			recvUnitName.setBounds(475, 7, 380, 21);
			recvUnitName.setEditable(false);

			final Button sUnit = new Button(composite, SWT.NONE);
			sUnit.addSelectionListener(new OpenSelectViewSelection());
			sUnit.setText("���ջ���ѡ��");
			sUnit.setBounds(860, 7, 110, 22);

			//ҵ��ƾ֤�ļ�ѡ��
			Label labelPathData = new Label(composite, SWT.NONE);
			labelPathData.setText("ҵ��ƾ֤�ļ���");
			labelPathData.setBounds(6, 38, 83, 20);
			
			Text textPathData = new Text(composite, SWT.BORDER);
			textPathData.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			textPathData.setBounds(90, 35, 500, 21);
			
			Button btnDataSelect = new Button(composite, SWT.NONE);
			btnDataSelect.setBounds(600, 35, 80, 21);
			btnDataSelect.setText("�ļ�ѡ��");
			btnDataSelect.addSelectionListener(new FileSlectViewSelection(composite.getShell(),textPathData));
			
			//���¡����Ͱ�ť
			btnSignEseal = new Button(composite, SWT.NONE);
			btnSignEseal.setBounds(697, 35, 80, 21);
			btnSignEseal.setText("�� ��");
			btnSignEseal.addSelectionListener(new BtnSignEsealSelection(textPathData));
			btnSignEseal.setEnabled(false);
			btnPrint = new Button(composite, SWT.NONE);
			btnPrint.setBounds(794, 35, 80, 21);
			btnPrint.setText("��ӡ");
			btnPrint.setEnabled(false);
			btnPrint.addSelectionListener(new BtnPrintSelection());
			btnSendBill = new Button(composite, SWT.NONE);
			btnSendBill.setBounds(890, 35, 80, 21);
			btnSendBill.setText("�� ��");
			btnSendBill.addSelectionListener(new BtnSendBillSelection());
			btnSendBill.setEnabled(false);
			
			
			//������ʾ����ƾ֤������
			createStampContents(composite);
			
			//View�еĿؼ���Model�ĳ�Ա�������а�
			context.bindText(sendUnitName, "userOrgName");
			context.bindText(sDate, "sendDate");
			context.bindText(recvUnitName, "recvOrgNames");
			
			//�����ʱĿ¼�Ƿ����
			FileOperFacade.createDir(tempDir);
			this.layout(true);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(oleframe.getShell(), e);
		}

	}

	/**
	 * ����AcitveX�ؼ���ʾ����ƾ֤������
	 */
	protected void createStampContents(Composite composite) {
		oleframe = new OleFrame(composite, SWT.BORDER);
		oleframe.setBounds(6, 60, 980, 500);
		OleControlSite controlSite = new OleControlSite(oleframe, SWT.NONE | SWT.MAX,	"EsealForm.MainForm.1");//KoalForm.MainWin.1
		//����Ҫ������Ƕ�ؼ��Ĵ�С����Ϊ�����Ѿ������˸��ؼ��Ĵ�С����Ƕ�ؼ��ᰴ�ո��ؼ��Ĵ�С��ʾ
		controlSite.doVerb(OLE.OLEIVERB_PRIMARY);
		controlSite.doVerb(OLE.OLEIVERB_UIACTIVATE);
		controlSite.doVerb(OLE.OLEIVERB_SHOW);
		stampHandler = new KoalStampHandler(controlSite);
		oleframe.setVisible(true);
		
		//������ʾ����
		report = new ReportComposite(composite, JasperConstants.ALL);
		report.setBounds(6, 60, 980, 500);
		//���ñ�����ɫ����ActiveX�ؼ�һ��
		report.setBackground(new Color(composite.getDisplay(),new RGB(192,192,192)));
		report.setVisible(false);
	}

	/**
	 * ���Ͱ�ť�ĵ���¼�
	 */
	private class BtnSendBillSelection extends SelectionAdapter{
		public void widgetSelected(final SelectionEvent e) {
			//�������
			Cursor arrowCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_ARROW);
			try{
				//�������͵��ļ��Ƿ����
				int ret = ((BisSendBean)context.getModelHolder().getModel()).isFileExists(fileName);
				if (ret == -1){
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}else if(ret == 1){
					oleframe.getShell().setCursor(arrowCursor);
					MessageDialog.openMessageDialog(oleframe.getShell(), "ƾ֤" + fileName + "�Ѿ����ͣ��������·��͡�\n�������·��ͣ����������Ѿ����ͳɹ���ƾ֤��");
					return;
				}
				
				//�ȴ����
				Cursor waitCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_WAIT);
				oleframe.getShell().setCursor(waitCursor);
				//�ȰѰ�ť״̬���óɲ����ã���ֹ�û��ظ����
				btnSendBill.setEnabled(false);
				//���ļ����ݽ���ǩ��dataXml
				String signed = KoalClientPkcs7Encrypt.getInstance().pkcs7sign(dataXml);
				if (signed == ""){
					MessageDialog.openMessageDialog(oleframe.getShell(), KoalClientPkcs7Encrypt.getInstance().getLastError());
					btnSendBill.setEnabled(true);
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}
				
				//���ļ����ݽ��м���dataXml
				String ecnrypted = KoalClientPkcs7Encrypt.getInstance().pkcs7Envelop(signed);
				if (ecnrypted == ""){
					MessageDialog.openMessageDialog(oleframe.getShell(), KoalClientPkcs7Encrypt.getInstance().getLastError());
					btnSendBill.setEnabled(true);
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}
				
				//�����ܺ����Ϣ��������ʱ�ļ��У������͵�������
				FileOperFacade.writeFile(tempDir + fileName, ecnrypted);
				ret = ((BisSendBean)context.getModelHolder().getModel()).upload(tempDir + fileName);
				if (ret == 0){
		        	MessageDialog.openMessageDialog(oleframe.getShell(), "�ļ�" + fileName + "�Ѿ��ɹ����͡�");
				}else{
					//����ʧ��
					btnSendBill.setEnabled(true);
				}
				//ɾ����ʱ�ļ�
				FileOperFacade.deleteFile(tempDir + fileName);
				oleframe.getShell().setCursor(arrowCursor);
			}catch(Throwable ex){
				oleframe.getShell().setCursor(arrowCursor);
				MessageDialog.openErrorDialog(oleframe.getShell(), ex);
			}
		}
	}
	
	/**
	 * ���°�ť����¼�
	 */
	private class BtnSignEsealSelection extends SelectionAdapter{
		private Text _textShow;
		
		public BtnSignEsealSelection(Text textShow){
			_textShow = textShow;
		}
		
		public void widgetSelected(SelectionEvent e) {
			//�������
			Cursor arrowCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_ARROW);
			try{
				//�ȴ����
				Cursor waitCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_WAIT);
				oleframe.getShell().setCursor(waitCursor);
				
				String base64Data = stampHandler.base64Encode(dataXml);
				String base64Model = stampHandler.base64Encode(modelXml);
				//�ȰѰ�ť״̬���óɲ����ã���ֹ�û��ظ����
				btnSignEseal.setEnabled(false);
				//���ݵ�ǰ״̬��ô�����λ�õ���Ϣ
				TsOperationplaceDto signingPlace = place.get(status);
				if (signingPlace == null){
					MessageDialog.openMessageDialog(oleframe.getShell(), "ҵ��ƾ֤�ĸ���״̬" + status + "����������ѡ����ٸ��¡�");
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}
				//����û�����Ȩ��
				Map<String, UserStampFuncDto> userStampFunc = ((BisSendBean)(context.getModelHolder().getModel())).getUserStampFunc();
				UserStampFuncDto userFunc = userStampFunc.get(signingPlace.getSplaceid());
				if (userFunc == null){
					MessageDialog.openMessageDialog(oleframe.getShell(), "��û��" + btnSignEseal.getText() + "��Ȩ�ޣ�������û����ٸ��¡�");
					oleframe.getShell().setCursor(arrowCursor);
					btnSignEseal.setEnabled(true);
					return;
				}
				String signed = stampHandler.signEseal(base64Data, base64Model, signingPlace.getSformid(), 
						signingPlace.getSplaceid(), signingPlace.getSstamptypecode(), userFunc.getSstampid());
//				String test = stampHandler.getLastError();
//				signed = stampHandler.getDataXMLBase64();
				if (signed == ""){
					//���ִ���
					MessageDialog.openMessageDialog(oleframe.getShell(), stampHandler.getLastError());
					btnSignEseal.setEnabled(true);
				}else{
					dataXml = stampHandler.base64Decode(signed);
					//������º�Ľ����ԭ�ļ���
					FileOperFacade.writeFile(_textShow.getText(), dataXml);
					//��ʾ���º�Ľ��
					String ret = stampHandler.showBill(signed, base64Model, signingPlace.getSformid(),"1",percent);
					if ((ret != null) && (ret.length() > 0)){
						MessageDialog.openMessageDialog(oleframe.getShell(), ret);
					}
					status = signingPlace.getSafterstatus();
					TsOperationplaceDto nextPlace = place.get(status);
					if (nextPlace == null){
						//�Ҳ�����һ������λ���ˣ���ô�������
						//�޸İ�ť״̬
						btnSendBill.setEnabled(true);
						btnSignEseal.setText("����");
					}else{
						//������ʾҪ�ǵ��µ�����
						btnSignEseal.setEnabled(true);
						btnSignEseal.setText("��" + nextPlace.getSplacedesc());
					}
				}
				oleframe.getShell().setCursor(arrowCursor);
			}catch(Exception ex){
				oleframe.getShell().setCursor(arrowCursor);
				MessageDialog.openErrorDialog(oleframe.getShell(), ex);
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
				SelectUnitDialog dialog = new SelectUnitDialog(oleframe.getShell());
				if (dialog.open() == IDialogConstants.OK_ID){
					//�����û�ѡ��Ľ��ջ���
					List<TsOrganDto> orgs = dialog.getSelectedOrgs();
					if ((orgs == null) || (orgs.size() == 0)){
						MessageDialog.openMessageDialog(oleframe.getShell(), "��ѡ����ջ�����");
					}
					((BisSendBean)(context.getModelHolder().getModel())).setRecvOrgs(orgs);
					recvUnitName.setText(((BisSendBean)(context.getModelHolder().getModel())).getRecvOrgNames());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * �ļ�ѡ�񴰿�
	 */
	private class FileSlectViewSelection extends SelectionAdapter{
		private Shell _shell;
		private Text _textShow;
	
		public FileSlectViewSelection(Shell shell,Text textShow){
			_shell = shell;
			_textShow = textShow;
		}
		public void widgetSelected(final SelectionEvent e){
			try{
				//�������а�ť 
				btnSendBill.setEnabled(false);
				btnSignEseal.setEnabled(false);
				btnPrint.setEnabled(false);
				//ѡ���ļ�
				FileDialog dialog = new FileDialog(_shell,0);
				dialog.setFilterExtensions(new String[]{"*.xml","*.*"});
				dialog.open();
				String path = dialog.getFilterPath();
				fileName = dialog.getFileName();
				status = ITFECommonConstant.STATUS_BEGIN;//δ����״̬
				//�������
				Cursor arrowCursor = new Cursor(_shell.getDisplay(),SWT.CURSOR_ARROW);
				if ((path!=null) && (path.length() > 0) && (fileName!=null) && (fileName.length() > 0)){
					//�ȴ����
					Cursor waitCursor = new Cursor(_shell.getDisplay(),SWT.CURSOR_WAIT);
					_shell.setCursor(waitCursor);
					
					_textShow.setText(path + "\\" + fileName);
//					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					int ret = ((BisSendBean)(context.getModelHolder().getModel())).showReport(_textShow.getText(), fileName);
					if (ret > 0){
						btnPrint.setEnabled(true);
						//�ļ����ɹ�����
						//���Ҫ��ʾ���ļ�����
						dataXml = ((BisSendBean)(context.getModelHolder().getModel())).getContent();
						if (ret == 1){
							//����������ļ�����ô��Ҫ��ʾActiveX�ؼ�
							oleframe.setVisible(true);
							report.setVisible(false);
							//��ø�����������λ�á�ģ�����ݵȲ���
							List<Object> stampParam = ((BisSendBean)(context.getModelHolder().getModel())).getStampParam();
							form = (TsOperationformDto)((List<IDto>)stampParam.get(0)).get(0);
							List<IDto> places = (List<IDto>)stampParam.get(1);
							place = new HashMap<String, TsOperationplaceDto>();
							for (IDto one : places){
								TsOperationplaceDto onePlace = (TsOperationplaceDto)one;
								place.put(onePlace.getSbeforestatus(), onePlace);
							}
							//���ģ������
							modelXml = stampParam.get(2).toString();
							String modelBase64 = stampHandler.base64Encode(modelXml);
							//��������ļ��е��Ѹ���λ��
							int stampPos = dataXml.indexOf("<SIGN>"); 
							if (stampPos > 0){
								if (dataXml.indexOf("stamp1",stampPos) > 0){
									//�Ѿ��������һ����
									status = ITFECommonConstant.STATUS_SUCCESS;
								}else if(dataXml.indexOf("stamp0",stampPos) > 0){
									status = place.get(status).getSafterstatus();
								}
							}
							//���ڻ�û�и��µ�ҵ��ƾ֤��������Ҫ����ƾ֤���
							int vouNo = 0;
							//���ƾ֤����Ƿ��Ѿ����� �ģ�ѡ���ҵ��ƾ֤�Ƿ��Ѿ�����
							vouNo = ((BisSendBean)(context.getModelHolder().getModel())).getVouNo("QS", fileName);
							if (vouNo == -99){
								//���ִ��󣬷���
								_shell.setCursor(arrowCursor);
								return;
							}
							if (ITFECommonConstant.STATUS_BEGIN.equals(status)){
								if ((vouNo == -2) || (vouNo == -3)){
									//���ɱ��
									vouNo = ((BisSendBean)(context.getModelHolder().getModel())).genVouNo("QS", fileName);
									if (vouNo == -99){
										//���ִ��󣬷���
										_shell.setCursor(arrowCursor);
										return;
									}
								}
								if (vouNo != -1){
									//�����ظ����͵ģ�����Ҫ�ѱ��д��ԭʼƾ֤��
									int iPos1 = dataXml.toUpperCase().indexOf("<VOU_NUM>") + "<VOU_NUM>".length();
									int iPos2 = dataXml.toUpperCase().indexOf("</VOU_NUM>");
									String strVouNo = "0000" + vouNo;
									dataXml = dataXml.substring(0,iPos1) + (strVouNo.substring(strVouNo.length() - 4)) + dataXml.substring(iPos2);
									//����ƾ֤��ŵ�ԭ�ļ���
									FileOperFacade.writeFile(_textShow.getText(), dataXml);
								}
							}
							//��ʾƾ֤
							String xmlBase64 = stampHandler.base64Encode(dataXml);
							percent = ((BisSendBean)(context.getModelHolder().getModel())).getPercent();
							String retString = stampHandler.showBill(xmlBase64, modelBase64, form.getSformid(),"1", percent);
							if ((retString != null) && (retString.length() > 0)){
								MessageDialog.openMessageDialog(oleframe.getShell(), retString);
								_shell.setCursor(arrowCursor);
								return;
							}
							if (vouNo == -1){
								MessageDialog.openMessageDialog(oleframe.getShell(), "ƾ֤" + fileName + "�Ѿ����ͣ��������·��͡�\n�������·��ͣ����������Ѿ����ͳɹ���ƾ֤��");
								_shell.setCursor(arrowCursor);
								return;
							}
							//����ҵ��ƾ֤��״̬��ȷ����ť��״̬
							TsOperationplaceDto firstPlace = place.get(status);
							if (firstPlace == null){
								//������ϣ����Է���
								btnSignEseal.setText("����");
								btnSendBill.setEnabled(true);
								btnSignEseal.setEnabled(false);
							}else{
								//δ���¡�������û�и���
								btnSignEseal.setText("��" + firstPlace.getSplacedesc());
								btnSendBill.setEnabled(false);
								btnSignEseal.setEnabled(true);
							}
						}else{
							//����������ļ�����ô��ʾIReport������
							oleframe.setVisible(false);
							report.setVisible(true);
							//����ҵ��ƾ֤��������Ҫ����ƾ֤���
							int vouNo = 0;
							String strVouNo = "";
							if (fileName.toUpperCase().startsWith("TK")){
								//���ƾ֤����Ƿ��Ѿ�����
								vouNo = ((BisSendBean)(context.getModelHolder().getModel())).getVouNo("TK", fileName);
								if (vouNo == -99){
									//���ִ��󣬷���
									_shell.setCursor(arrowCursor);
									return;
								}
								if ((vouNo == -2) || (vouNo == -3)){
									//���ɱ��
									vouNo = ((BisSendBean)(context.getModelHolder().getModel())).genVouNo("TK", fileName);
									if (vouNo == -99){
										//���ִ��󣬷���
										_shell.setCursor(arrowCursor);
										return;
									}
								}
								if (vouNo != -1){
									//�����ظ����͵ģ�����Ҫ�ѱ��д��ԭʼƾ֤��
									int iPos1 = dataXml.toUpperCase().indexOf("<VOU_NUM>") + "<VOU_NUM>".length();
									int iPos2 = dataXml.toUpperCase().indexOf("</VOU_NUM>");
									strVouNo = "0000" + vouNo;
									strVouNo = strVouNo.substring(strVouNo.length() - 4);
									dataXml = dataXml.substring(0,iPos1) + strVouNo + dataXml.substring(iPos2);
									//����ƾ֤��ŵ�ԭ�ļ���
									FileOperFacade.writeFile(_textShow.getText(), dataXml);
								}
							}
							try {
								Map<String,Object> params = ((BisSendBean)(context.getModelHolder().getModel())).getParams();
								if (vouNo != -1){
									params.put("vouNum",strVouNo);
								}
								ReportGen.genReport(params, 
										((BisSendBean)(context.getModelHolder().getModel())).getDetails(), report,
										((BisSendBean)(context.getModelHolder().getModel())).getReportIndex());
								if (vouNo == -1){
									MessageDialog.openMessageDialog(oleframe.getShell(), "ƾ֤" + fileName + "�Ѿ����ͣ��������·��͡�\n�������·��ͣ����������Ѿ����ͳɹ���ƾ֤��");
								}else{
									btnSendBill.setEnabled(true);
									btnSignEseal.setEnabled(false);
								}
							} catch (Throwable ex) {
								MessageDialog.openErrorDialog(_shell, ex);
							}
						}
					}
				}else{
					_textShow.setText("");
				}
				_shell.setCursor(arrowCursor);
			}catch(Exception ex){
				MessageDialog.openErrorDialog(_shell, ex);
			}
		}
	}
	
	/**
	 * ��ӡ
	 * @author Administrator
	 */
	private class BtnPrintSelection extends SelectionAdapter{
		public void widgetSelected(SelectionEvent e) {
			try{
				btnPrint.setEnabled(false);
				TsOperationmodelDto model = ((BisSendBean)(context.getModelHolder().getModel())).getModel();
				if ("QS".equals(model.getSoperationtypecode())){
					//�����ļ�������ActiveX�ؼ��Ĵ�ӡ����ʾƾ֤
					String xmlBase64 = stampHandler.base64Encode(dataXml);
					//ģ������
					String modelBase64 = stampHandler.base64Encode(modelXml);
					String ret = stampHandler.printBills(xmlBase64, modelBase64, form.getSformid());
					if ((ret != null) && (ret.length() > 0)){
						MessageDialog.openMessageDialog(oleframe.getShell(), ret);
					}
				}else{
					//�����ļ�������IReport�Ĵ�ӡ
					JasperPrint print = report.getReportViewer().getDocument();
					JasperPrintManager.printReport(print, false);
				}
				btnPrint.setEnabled(true);
			}catch(Exception ex){
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}
		
	}

}