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
	//临时文件存放目录
    private static String tempDir = "c:/temp/";
	//日期
	private Text sDate;
	//发送机构
	private Text sendUnitName;
	//接收机构
	private Text recvUnitName;
	//盖章按钮
	Button btnSignEseal;
	//发送按钮
	Button btnSendBill;
	//打印按钮
	Button btnPrint;
	//Xml内容
	private String dataXml;
	//模版内容
	private String modelXml;
	//联信息
	private TsOperationformDto form;
	//盖章位置信息
	private Map<String, TsOperationplaceDto> place;
	//凭证显示的比例
	private String percent;
	//用户选择的文件的名称（不含目录）
	private String fileName;
	//盖章状态 0000-未章；0001-已盖个人章；0010-盖章完毕可以发送
	private String status;
	private OleFrame oleframe;
	private ReportComposite report;
	//电子印鉴的调用对象
	private BaseStampHandler stampHandler;
	//JAF数据绑定对象
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
			
			//发送机构
			final Label slabel = new Label(composite, SWT.NONE);
			slabel.setText("发送机构名称：");
			slabel.setBounds(6, 10, 84, 20);

			sendUnitName = new Text(composite, SWT.BORDER);
			sendUnitName.setEnabled(false);
			sendUnitName.setBounds(90, 7, 150, 22);

			//发送日期
			final Label dlabel = new Label(composite, SWT.NONE);
			dlabel.setText("发送日期：");
			dlabel.setBounds(250, 10, 59, 20);

			sDate = new Text(composite, SWT.BORDER);
			sDate.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			sDate.setBounds(320, 7, 80, 21);
			sDate.setEnabled(false);

			//接收机构
			final Label rlabel = new Label(composite, SWT.NONE);
			rlabel.setText("接收机构：");
			rlabel.setBounds(410, 10, 59, 20);

			recvUnitName = new Text(composite, SWT.BORDER);
			recvUnitName.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			recvUnitName.setBounds(475, 7, 380, 21);
			recvUnitName.setEditable(false);

			final Button sUnit = new Button(composite, SWT.NONE);
			sUnit.addSelectionListener(new OpenSelectViewSelection());
			sUnit.setText("接收机构选择");
			sUnit.setBounds(860, 7, 110, 22);

			//业务凭证文件选择
			Label labelPathData = new Label(composite, SWT.NONE);
			labelPathData.setText("业务凭证文件：");
			labelPathData.setBounds(6, 38, 83, 20);
			
			Text textPathData = new Text(composite, SWT.BORDER);
			textPathData.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			textPathData.setBounds(90, 35, 500, 21);
			
			Button btnDataSelect = new Button(composite, SWT.NONE);
			btnDataSelect.setBounds(600, 35, 80, 21);
			btnDataSelect.setText("文件选择");
			btnDataSelect.addSelectionListener(new FileSlectViewSelection(composite.getShell(),textPathData));
			
			//盖章、发送按钮
			btnSignEseal = new Button(composite, SWT.NONE);
			btnSignEseal.setBounds(697, 35, 80, 21);
			btnSignEseal.setText("盖 章");
			btnSignEseal.addSelectionListener(new BtnSignEsealSelection(textPathData));
			btnSignEseal.setEnabled(false);
			btnPrint = new Button(composite, SWT.NONE);
			btnPrint.setBounds(794, 35, 80, 21);
			btnPrint.setText("打印");
			btnPrint.setEnabled(false);
			btnPrint.addSelectionListener(new BtnPrintSelection());
			btnSendBill = new Button(composite, SWT.NONE);
			btnSendBill.setBounds(890, 35, 80, 21);
			btnSendBill.setText("发 送");
			btnSendBill.addSelectionListener(new BtnSendBillSelection());
			btnSendBill.setEnabled(false);
			
			
			//创建显示电子凭证的区域
			createStampContents(composite);
			
			//View中的控件与Model的成员变量进行绑定
			context.bindText(sendUnitName, "userOrgName");
			context.bindText(sDate, "sendDate");
			context.bindText(recvUnitName, "recvOrgNames");
			
			//检查临时目录是否存在
			FileOperFacade.createDir(tempDir);
			this.layout(true);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(oleframe.getShell(), e);
		}

	}

	/**
	 * 调用AcitveX控件显示电子凭证的区域
	 */
	protected void createStampContents(Composite composite) {
		oleframe = new OleFrame(composite, SWT.BORDER);
		oleframe.setBounds(6, 60, 980, 500);
		OleControlSite controlSite = new OleControlSite(oleframe, SWT.NONE | SWT.MAX,	"EsealForm.MainForm.1");//KoalForm.MainWin.1
		//不需要设置内嵌控件的大小，因为上面已经设置了父控件的大小，内嵌控件会按照父控件的大小显示
		controlSite.doVerb(OLE.OLEIVERB_PRIMARY);
		controlSite.doVerb(OLE.OLEIVERB_UIACTIVATE);
		controlSite.doVerb(OLE.OLEIVERB_SHOW);
		stampHandler = new KoalStampHandler(controlSite);
		oleframe.setVisible(true);
		
		//报表显示区域
		report = new ReportComposite(composite, JasperConstants.ALL);
		report.setBounds(6, 60, 980, 500);
		//设置背景颜色，和ActiveX控件一致
		report.setBackground(new Color(composite.getDisplay(),new RGB(192,192,192)));
		report.setVisible(false);
	}

	/**
	 * 发送按钮的点击事件
	 */
	private class BtnSendBillSelection extends SelectionAdapter{
		public void widgetSelected(final SelectionEvent e) {
			//正常光标
			Cursor arrowCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_ARROW);
			try{
				//检查待发送的文件是否存在
				int ret = ((BisSendBean)context.getModelHolder().getModel()).isFileExists(fileName);
				if (ret == -1){
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}else if(ret == 1){
					oleframe.getShell().setCursor(arrowCursor);
					MessageDialog.openMessageDialog(oleframe.getShell(), "凭证" + fileName + "已经发送，不能重新发送。\n如需重新发送，请先作废已经发送成功的凭证。");
					return;
				}
				
				//等待光标
				Cursor waitCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_WAIT);
				oleframe.getShell().setCursor(waitCursor);
				//先把按钮状态设置成不可用，防止用户重复点击
				btnSendBill.setEnabled(false);
				//对文件内容进行签名dataXml
				String signed = KoalClientPkcs7Encrypt.getInstance().pkcs7sign(dataXml);
				if (signed == ""){
					MessageDialog.openMessageDialog(oleframe.getShell(), KoalClientPkcs7Encrypt.getInstance().getLastError());
					btnSendBill.setEnabled(true);
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}
				
				//对文件内容进行加密dataXml
				String ecnrypted = KoalClientPkcs7Encrypt.getInstance().pkcs7Envelop(signed);
				if (ecnrypted == ""){
					MessageDialog.openMessageDialog(oleframe.getShell(), KoalClientPkcs7Encrypt.getInstance().getLastError());
					btnSendBill.setEnabled(true);
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}
				
				//将加密后的信息保存在临时文件中，并发送到服务器
				FileOperFacade.writeFile(tempDir + fileName, ecnrypted);
				ret = ((BisSendBean)context.getModelHolder().getModel()).upload(tempDir + fileName);
				if (ret == 0){
		        	MessageDialog.openMessageDialog(oleframe.getShell(), "文件" + fileName + "已经成功发送。");
				}else{
					//发送失败
					btnSendBill.setEnabled(true);
				}
				//删除临时文件
				FileOperFacade.deleteFile(tempDir + fileName);
				oleframe.getShell().setCursor(arrowCursor);
			}catch(Throwable ex){
				oleframe.getShell().setCursor(arrowCursor);
				MessageDialog.openErrorDialog(oleframe.getShell(), ex);
			}
		}
	}
	
	/**
	 * 盖章按钮点击事件
	 */
	private class BtnSignEsealSelection extends SelectionAdapter{
		private Text _textShow;
		
		public BtnSignEsealSelection(Text textShow){
			_textShow = textShow;
		}
		
		public void widgetSelected(SelectionEvent e) {
			//正常光标
			Cursor arrowCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_ARROW);
			try{
				//等待光标
				Cursor waitCursor = new Cursor(oleframe.getDisplay(),SWT.CURSOR_WAIT);
				oleframe.getShell().setCursor(waitCursor);
				
				String base64Data = stampHandler.base64Encode(dataXml);
				String base64Model = stampHandler.base64Encode(modelXml);
				//先把按钮状态设置成不可用，防止用户重复点击
				btnSignEseal.setEnabled(false);
				//根据当前状态获得待盖章位置的信息
				TsOperationplaceDto signingPlace = place.get(status);
				if (signingPlace == null){
					MessageDialog.openMessageDialog(oleframe.getShell(), "业务凭证的盖章状态" + status + "错误，请重新选择后再盖章。");
					oleframe.getShell().setCursor(arrowCursor);
					return;
				}
				//获得用户盖章权限
				Map<String, UserStampFuncDto> userStampFunc = ((BisSendBean)(context.getModelHolder().getModel())).getUserStampFunc();
				UserStampFuncDto userFunc = userStampFunc.get(signingPlace.getSplaceid());
				if (userFunc == null){
					MessageDialog.openMessageDialog(oleframe.getShell(), "您没有" + btnSignEseal.getText() + "的权限，请更换用户后再盖章。");
					oleframe.getShell().setCursor(arrowCursor);
					btnSignEseal.setEnabled(true);
					return;
				}
				String signed = stampHandler.signEseal(base64Data, base64Model, signingPlace.getSformid(), 
						signingPlace.getSplaceid(), signingPlace.getSstamptypecode(), userFunc.getSstampid());
//				String test = stampHandler.getLastError();
//				signed = stampHandler.getDataXMLBase64();
				if (signed == ""){
					//出现错误
					MessageDialog.openMessageDialog(oleframe.getShell(), stampHandler.getLastError());
					btnSignEseal.setEnabled(true);
				}else{
					dataXml = stampHandler.base64Decode(signed);
					//保存盖章后的结果到原文件中
					FileOperFacade.writeFile(_textShow.getText(), dataXml);
					//显示盖章后的结果
					String ret = stampHandler.showBill(signed, base64Model, signingPlace.getSformid(),"1",percent);
					if ((ret != null) && (ret.length() > 0)){
						MessageDialog.openMessageDialog(oleframe.getShell(), ret);
					}
					status = signingPlace.getSafterstatus();
					TsOperationplaceDto nextPlace = place.get(status);
					if (nextPlace == null){
						//找不到下一个盖章位置了，那么盖章完毕
						//修改按钮状态
						btnSendBill.setEnabled(true);
						btnSignEseal.setText("盖章");
					}else{
						//否则显示要盖的章的名字
						btnSignEseal.setEnabled(true);
						btnSignEseal.setText("盖" + nextPlace.getSplacedesc());
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
	 * 打开接收机构选择窗口
	 */
	private class OpenSelectViewSelection extends SelectionAdapter{
		public OpenSelectViewSelection(){
			
		}
		public void widgetSelected(final SelectionEvent e){
			try {
				//打开选择框
				SelectUnitDialog dialog = new SelectUnitDialog(oleframe.getShell());
				if (dialog.open() == IDialogConstants.OK_ID){
					//设置用户选择的接收机构
					List<TsOrganDto> orgs = dialog.getSelectedOrgs();
					if ((orgs == null) || (orgs.size() == 0)){
						MessageDialog.openMessageDialog(oleframe.getShell(), "请选择接收机构。");
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
	 * 文件选择窗口
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
				//禁用所有按钮 
				btnSendBill.setEnabled(false);
				btnSignEseal.setEnabled(false);
				btnPrint.setEnabled(false);
				//选择文件
				FileDialog dialog = new FileDialog(_shell,0);
				dialog.setFilterExtensions(new String[]{"*.xml","*.*"});
				dialog.open();
				String path = dialog.getFilterPath();
				fileName = dialog.getFileName();
				status = ITFECommonConstant.STATUS_BEGIN;//未盖章状态
				//正常光标
				Cursor arrowCursor = new Cursor(_shell.getDisplay(),SWT.CURSOR_ARROW);
				if ((path!=null) && (path.length() > 0) && (fileName!=null) && (fileName.length() > 0)){
					//等待光标
					Cursor waitCursor = new Cursor(_shell.getDisplay(),SWT.CURSOR_WAIT);
					_shell.setCursor(waitCursor);
					
					_textShow.setText(path + "\\" + fileName);
//					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					int ret = ((BisSendBean)(context.getModelHolder().getModel())).showReport(_textShow.getText(), fileName);
					if (ret > 0){
						btnPrint.setEnabled(true);
						//文件被成功处理
						//获得要显示的文件内容
						dataXml = ((BisSendBean)(context.getModelHolder().getModel())).getContent();
						if (ret == 1){
							//如果是清算文件，那么需要显示ActiveX控件
							oleframe.setVisible(true);
							report.setVisible(false);
							//获得盖章联、盖章位置、模版内容等参数
							List<Object> stampParam = ((BisSendBean)(context.getModelHolder().getModel())).getStampParam();
							form = (TsOperationformDto)((List<IDto>)stampParam.get(0)).get(0);
							List<IDto> places = (List<IDto>)stampParam.get(1);
							place = new HashMap<String, TsOperationplaceDto>();
							for (IDto one : places){
								TsOperationplaceDto onePlace = (TsOperationplaceDto)one;
								place.put(onePlace.getSbeforestatus(), onePlace);
							}
							//获得模版内容
							modelXml = stampParam.get(2).toString();
							String modelBase64 = stampHandler.base64Encode(modelXml);
							//获得清算文件中的已盖章位置
							int stampPos = dataXml.indexOf("<SIGN>"); 
							if (stampPos > 0){
								if (dataXml.indexOf("stamp1",stampPos) > 0){
									//已经盖完最后一个章
									status = ITFECommonConstant.STATUS_SUCCESS;
								}else if(dataXml.indexOf("stamp0",stampPos) > 0){
									status = place.get(status).getSafterstatus();
								}
							}
							//对于还没有盖章的业务凭证，根据需要增加凭证编号
							int vouNo = 0;
							//检查凭证编号是否已经生成 的，选择的业务凭证是否已经发送
							vouNo = ((BisSendBean)(context.getModelHolder().getModel())).getVouNo("QS", fileName);
							if (vouNo == -99){
								//出现错误，返回
								_shell.setCursor(arrowCursor);
								return;
							}
							if (ITFECommonConstant.STATUS_BEGIN.equals(status)){
								if ((vouNo == -2) || (vouNo == -3)){
									//生成编号
									vouNo = ((BisSendBean)(context.getModelHolder().getModel())).genVouNo("QS", fileName);
									if (vouNo == -99){
										//出现错误，返回
										_shell.setCursor(arrowCursor);
										return;
									}
								}
								if (vouNo != -1){
									//不是重复发送的，就需要把编号写入原始凭证中
									int iPos1 = dataXml.toUpperCase().indexOf("<VOU_NUM>") + "<VOU_NUM>".length();
									int iPos2 = dataXml.toUpperCase().indexOf("</VOU_NUM>");
									String strVouNo = "0000" + vouNo;
									dataXml = dataXml.substring(0,iPos1) + (strVouNo.substring(strVouNo.length() - 4)) + dataXml.substring(iPos2);
									//保存凭证编号到原文件中
									FileOperFacade.writeFile(_textShow.getText(), dataXml);
								}
							}
							//显示凭证
							String xmlBase64 = stampHandler.base64Encode(dataXml);
							percent = ((BisSendBean)(context.getModelHolder().getModel())).getPercent();
							String retString = stampHandler.showBill(xmlBase64, modelBase64, form.getSformid(),"1", percent);
							if ((retString != null) && (retString.length() > 0)){
								MessageDialog.openMessageDialog(oleframe.getShell(), retString);
								_shell.setCursor(arrowCursor);
								return;
							}
							if (vouNo == -1){
								MessageDialog.openMessageDialog(oleframe.getShell(), "凭证" + fileName + "已经发送，不能重新发送。\n如需重新发送，请先作废已经发送成功的凭证。");
								_shell.setCursor(arrowCursor);
								return;
							}
							//根据业务凭证的状态，确定按钮的状态
							TsOperationplaceDto firstPlace = place.get(status);
							if (firstPlace == null){
								//盖章完毕，可以发送
								btnSignEseal.setText("盖章");
								btnSendBill.setEnabled(true);
								btnSignEseal.setEnabled(false);
							}else{
								//未盖章、还有章没有盖完
								btnSignEseal.setText("盖" + firstPlace.getSplacedesc());
								btnSendBill.setEnabled(false);
								btnSignEseal.setEnabled(true);
							}
						}else{
							//如果是其他文件，那么显示IReport报表窗口
							oleframe.setVisible(false);
							report.setVisible(true);
							//对于业务凭证，根据需要增加凭证编号
							int vouNo = 0;
							String strVouNo = "";
							if (fileName.toUpperCase().startsWith("TK")){
								//检查凭证编号是否已经生成
								vouNo = ((BisSendBean)(context.getModelHolder().getModel())).getVouNo("TK", fileName);
								if (vouNo == -99){
									//出现错误，返回
									_shell.setCursor(arrowCursor);
									return;
								}
								if ((vouNo == -2) || (vouNo == -3)){
									//生成编号
									vouNo = ((BisSendBean)(context.getModelHolder().getModel())).genVouNo("TK", fileName);
									if (vouNo == -99){
										//出现错误，返回
										_shell.setCursor(arrowCursor);
										return;
									}
								}
								if (vouNo != -1){
									//不是重复发送的，就需要把编号写入原始凭证中
									int iPos1 = dataXml.toUpperCase().indexOf("<VOU_NUM>") + "<VOU_NUM>".length();
									int iPos2 = dataXml.toUpperCase().indexOf("</VOU_NUM>");
									strVouNo = "0000" + vouNo;
									strVouNo = strVouNo.substring(strVouNo.length() - 4);
									dataXml = dataXml.substring(0,iPos1) + strVouNo + dataXml.substring(iPos2);
									//保存凭证编号到原文件中
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
									MessageDialog.openMessageDialog(oleframe.getShell(), "凭证" + fileName + "已经发送，不能重新发送。\n如需重新发送，请先作废已经发送成功的凭证。");
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
	 * 打印
	 * @author Administrator
	 */
	private class BtnPrintSelection extends SelectionAdapter{
		public void widgetSelected(SelectionEvent e) {
			try{
				btnPrint.setEnabled(false);
				TsOperationmodelDto model = ((BisSendBean)(context.getModelHolder().getModel())).getModel();
				if ("QS".equals(model.getSoperationtypecode())){
					//清算文件，调用ActiveX控件的打印，显示凭证
					String xmlBase64 = stampHandler.base64Encode(dataXml);
					//模版内容
					String modelBase64 = stampHandler.base64Encode(modelXml);
					String ret = stampHandler.printBills(xmlBase64, modelBase64, form.getSformid());
					if ((ret != null) && (ret.length() > 0)){
						MessageDialog.openMessageDialog(oleframe.getShell(), ret);
					}
				}else{
					//其他文件，调用IReport的打印
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