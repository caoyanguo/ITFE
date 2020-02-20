package com.cfcc.itfe.client.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.SWTResourceManager;

public class BuLUBankNoDialog extends TitleAreaDialog {
	/** service */
	protected ICommonDataAccessService iservice;
	private Shell shell;
	private Table table;
	private TableViewer tableViewer;
	/** �б� :����¼���ݲ�ѯ��� */
	private Table bululistT;
	// JAF���ݰ󶨶���
	private DataBindingContext context;
	/** ���� (��ѯ����) */
	private List<TsPaybankDto> banknolist;

	private TsPaybankDto paybankdto;

	/** �ı��� :���� (��ѯ����) */
	private Text banknameT;
	/** ���� (��ѯ����) */
	private String bankname;

	/** ��¼�к� */
	private String banknnoB;
	/** ��¼���� */
	private String banknameB;

	/** ��¼���� */
	private String banknameY;

	/** ��ʾ��Ϣ */
	private String msg;

	/** ��ѯ����� */
	List<TsPaybankDto> banklist;
	/** ҵ������ */
	private String biztype;
	/** ������ :ѡ����˸�ԭ�� */
	private Combo backreason;
	/** ѡ����˸�ԭ�� */
	private String selectbackreason;
	/** �˸�ԭ���б� */
	private List<TsDwbkReasonDto> listdto;
	
	private String ifmatch;
	
	private Label label1;
	
	private Label label4;
	
	private Button button_2;
	
	private ITFELoginInfo loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
	
	/**
	 * Instantiate a new title area dialog.
	 * 
	 * @param parentShell
	 */
	public BuLUBankNoDialog(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}

	/**
	 * ���ô��ڵ�ȱʡ��С
	 */
	protected Point getInitialSize() {
		if(this.ifmatch.equals(StateConstant.IF_MATCHBNKNAME_NO)){
			label1.setVisible(false);
			banknameT.setVisible(false);
			table.setVisible(false);
			button_2.setVisible(false);
		}
		//����(��������������36��ͷ)����Ҫ��¼�˸�ԭ����룬�ɲ�����д�������ڲ�¼ʱ�˴�����ʾ�˸�ԭ�����ؼ�
		if(loginInfo.getSorgcode().startsWith("36")||this.biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)){
			label4.setVisible(false);
			backreason.setVisible(false);
		}
		return new Point(530, 398);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public BuLUBankNoDialog(Shell parentShell, String msg) {
		super(parentShell);
		this.shell = parentShell;
		this.msg = msg;
	}

	public BuLUBankNoDialog(Shell parentShell, IDto dto,
			List<TsPaybankDto> banklist, String bankname, String biztype,
			ICommonDataAccessService iservice, List<TsDwbkReasonDto> listdto) {
		super(parentShell);
		this.shell = parentShell;
		this.iservice = iservice;
		this.paybankdto = null;
		if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)) {
			if (bankname != null && !bankname.equals("")) {
				this.bankname = bankname;
			} else {
				this.bankname = ((TvPayoutmsgmainDto) dto).getSrecbankname();
			}
			banknameY = ((TvPayoutmsgmainDto) dto).getSrecbankname();
			this.ifmatch = ((TvPayoutmsgmainDto) dto).getSifmatch();
		} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
			if (bankname != null && !bankname.equals("")) {
				this.bankname = bankname;
			} else {
				this.bankname = ((TvDwbkDto) dto).getSrecbankname();
				this.banknameB=((TvDwbkDto) dto).getSinputrecbankname();
				this.banknnoB=((TvDwbkDto) dto).getSinputrecbankno();
				
			}
			banknameY = ((TvDwbkDto) dto).getSrecbankname();
			this.ifmatch = ((TvDwbkDto) dto).getSifmatch();
		} else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
			if (bankname != null && !bankname.equals("")) {
				this.bankname = bankname;
			} else {
				this.bankname = ((TfDirectpaymsgmainDto) dto).getSpayeeacctbankname();
			}
			banknameY = ((TfDirectpaymsgmainDto) dto).getSpayeeacctbankname();
			this.ifmatch = ((TfDirectpaymsgmainDto) dto).getSifmatch();
		}
		this.banknolist = banklist;
		this.biztype = biztype;
		this.listdto = listdto;
	}

	protected Control createDialogArea(Composite parent) {
		context = DataBindingContext
				.createInstance("c5dc725e-2b29-42bf-9e9f-77f1b06143a4");
		if(ifmatch.equals(StateConstant.IF_MATCHBNKNAME_YES)){
			setTitle("��������֧�ֹؼ���ģ����ѯ,�ؼ���֮���ÿո�ָ�   \n"
					+ "�磺��ѯ [�й��������б���������֧��],������д [����   ������]");
		}
		Composite area = (Composite) super.createDialogArea(parent);

		final Composite composite = new Composite(area, SWT.NONE);
		composite.setLayout(new FormLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final FormData formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(0, 30);
		formData.height = 25;
		formData.width = 50;
		label1 = new Label(composite, SWT.NONE);
		label1.setLayoutData(formData);
		label1.setText("��������:");

		final FormData formData2 = new FormData();
		formData2.top = new FormAttachment(label1, 0, SWT.TOP);
		formData2.left = new FormAttachment(label1, 20, SWT.RIGHT);
		formData2.right = new FormAttachment(100, -30);
		formData2.bottom = new FormAttachment(label1, 0, SWT.BOTTOM);
		banknameT = new Text(composite, SWT.BORDER);
		banknameT.setTextLimit(100);
		banknameT.setLayoutData(formData2);
		banknameT.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		banknameT.addKeyListener(new TextControlListener(composite));
		banknameT.setText(bankname);

		final FormData formData4 = new FormData();
		formData4.top = new FormAttachment(label1, 10, SWT.BOTTOM);
		formData4.left = new FormAttachment(0, 30);
		formData4.height = 25;
		formData4.width = 50;

		label4 = new Label(composite, SWT.NONE);
		label4.setLayoutData(formData4);
		label4.setText("�˸�ԭ��:");

		/** �˸�ԭ�� */
		backreason = new Combo(composite, SWT.BORDER);
		backreason.setTextLimit(200);
		backreason.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		if(listdto!=null && listdto.size()>0){
			for (TsDwbkReasonDto ddto : listdto) {
				backreason.add(ddto.getSdrawbackreacode() + "_"
						+ ddto.getSdrawbackreacmt());
			}
		}
		final FormData formData5 = new FormData();
		formData5.top = new FormAttachment(label4, 0, SWT.TOP);
		formData5.left = new FormAttachment(label4, 20, SWT.RIGHT);
		formData5.right = new FormAttachment(100, -30);
		formData5.bottom = new FormAttachment(label4, 0, SWT.BOTTOM);
		backreason.setLayoutData(formData5);
		backreason.setFocus();

		final FormData formData3 = new FormData();
		formData3.bottom = new FormAttachment(100, -5);
		formData3.right = new FormAttachment(100, -5);
		formData3.top = new FormAttachment(label1, 40, SWT.BOTTOM);
		formData3.left = new FormAttachment(0, 5);

		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(formData3);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new OneRowSelect());
		tableViewer = new TableViewer(table);
		TableFacadeX tableFacade = new TableFacadeX(tableViewer);
		tableFacade.addColumn("֧��ϵͳ�к�  ", 150, "sbankno");
		tableFacade.addColumn("֧��ϵͳ����  ", 360, "sbankname");
		context.bindTableViewer2X(tableViewer, "banklist");

		return area;
	}

	/**
	 * Table��һ�б�ѡ�е��¼�
	 * 
	 * @author Administrator
	 */
	private class OneRowSelect extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			TableItem item = (TableItem) e.item;
			if (item != null) {
				paybankdto = (TsPaybankDto) item.getData();
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

	protected void buttonPressed(int buttonId) {

		if (buttonId == IDialogConstants.OK_ID) {
			// �����ѡ��
			TsPaybankDto one = paybankdto;
			boolean flag = true;
			if(this.ifmatch.equals(StateConstant.IF_MATCHBNKNAME_YES)&& one!=null){
				String msg = "����������" + banknameY + "����Ӧ֧��ϵͳ������"
				+ one.getSbankname() + "��,�Ƿ�ȷ�ϲ�¼?";
				flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "��ʾ", msg);
			}
			if (flag) {
				if(one!=null){
					this.banknnoB = one.getSbankno();
					this.banknameB = one.getSbankname();
				}
				if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
					this.selectbackreason = backreason.getText();
				}
				okPressed();
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		} else if (IDialogConstants.SELECT_TYPES_ID == buttonId) {
			// ����������ѯ֧��ϵͳ�к�
			TsPaybankDto bankdto = new TsPaybankDto();
			bankdto.setSstate(StateConstant.COMMON_YES);
			try {
				String wheresql = "";
				String[] banknamearr = banknameT.getText().split(" ");
				for (String banknames : banknamearr) {
					wheresql = wheresql + "S_BANKNAME LIKE '%" + banknames
							+ "%'" + " AND ";
				}
				wheresql = wheresql.substring(0, wheresql.length() - 4);
				List<TsPaybankDto> banklistss = iservice.findRsByDto(bankdto,
						" AND ( " + wheresql + " ) ");
				this.banklist = banklistss;
				tableViewer.setInput(banklist);
			} catch (Exception e) {
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		button_2 = createButton(parent,
				IDialogConstants.SELECT_TYPES_ID, "��ѯ", false);
		button_2.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button = createButton(parent, IDialogConstants.OK_ID,
				"ȷ��", false);
		button.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
		final Button button_1 = createButton(parent,
				IDialogConstants.CANCEL_ID, "ȡ��", false);
		button_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("����֧����ֽ��ǰ��-ҵ��Ҫ�ز�¼");
		super.configureShell(newShell);
	}

	public Table getBululistT() {
		return bululistT;
	}

	public void setBululistT(Table bululistT) {
		this.bululistT = bululistT;
	}

	public Text getBanknameT() {
		return banknameT;
	}

	public void setBanknameT(Text banknameT) {
		this.banknameT = banknameT;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBanknnoB() {
		return banknnoB;
	}

	public void setBanknnoB(String banknnoB) {
		this.banknnoB = banknnoB;
	}

	public String getBanknameB() {
		return banknameB;
	}

	public void setBanknameB(String banknameB) {
		this.banknameB = banknameB;
	}

	public IService getService(Class clazz) {
		return ServiceFactory.getService(clazz);
	}

	public List<TsPaybankDto> getBanknolist() {
		return banknolist;
	}

	public void setBanknolist(List<TsPaybankDto> banknolist) {
		this.banknolist = banknolist;
	}

	public String getBanknameY() {
		return banknameY;
	}

	public void setBanknameY(String banknameY) {
		this.banknameY = banknameY;
	}

	public List<TsPaybankDto> getBanklist() {
		return banklist;
	}

	public void setBanklist(List<TsPaybankDto> banklist) {
		this.banklist = banklist;
	}

	public String getBiztype() {
		return biztype;
	}

	public void setBiztype(String biztype) {
		this.biztype = biztype;
	}

	public Combo getBackreason() {
		return backreason;
	}

	public void setBackreason(Combo backreason) {
		this.backreason = backreason;
	}

	public String getSelectbackreason() {
		return selectbackreason;
	}

	public void setSelectbackreason(String selectbackreason) {
		this.selectbackreason = selectbackreason;
	}

	public List<TsDwbkReasonDto> getListdto() {
		return listdto;
	}

	public void setListdto(List<TsDwbkReasonDto> listdto) {
		this.listdto = listdto;
	}

	public TsPaybankDto getPaybankdto() {
		return paybankdto;
	}

	public void setPaybankdto(TsPaybankDto paybankdto) {
		this.paybankdto = paybankdto;
	}

	public String getIfmatch() {
		return ifmatch;
	}

	public void setIfmatch(String ifmatch) {
		this.ifmatch = ifmatch;
	}

	public Label getLabel1() {
		return label1;
	}

	public void setLabel1(Label label1) {
		this.label1 = label1;
	}

	public Label getLabel4() {
		return label4;
	}

	public void setLabel4(Label label4) {
		this.label4 = label4;
	}

	public Button getButton_2() {
		return button_2;
	}

	public void setButton_2(Button button_2) {
		this.button_2 = button_2;
	}
	
}
