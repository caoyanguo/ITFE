package com.cfcc.itfe.client.recbiz.banknameenter;

import itferesourcepackage.ScheckstatusEnumFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.report.ReportComposite;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ClientOcxServiceProxyUtils;
import com.cfcc.itfe.client.sendbiz.bizcertsend.OcxConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.PagingTableComposite;
import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.mvc.IModelHolder;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.jasperassistant.designer.viewer.util.JasperConstants;

public class ShowCompositeVoucherOcx extends Composite {

	private static Log logger = LogFactory
			.getLog(ShowCompositeVoucherOcx.class);
	// ole�ؼ�����
	protected OleControlSite controlSite;
	// ole���󷽷����ô���
	protected OleAutomation automation;

	// JAF���ݰ󶨶���
	private static DataBindingContext context;

	// �ܼ�¼��
	private static Text allCount;

	public static String ls_AllCount = "";
	// ��ǰ��¼
	private Text nowCount;
	// ��ǰ����
	private static Text nowPageCount;

	// ��ǰ����
	private static Text printCount;
	// ��һ����ť
	Button buttonBefore;
	// ��һ����ť
	Button buttonNext;
	// ��һ����ť
	Button buttonBeforeGroup;
	// ��һ����ť
	Button buttonNextGroup;
	// ���ذ�ť
	// Button btnReturn;
	// ��ӡ��ǰ��ť
	Button btnPrint;
	// ��ӡ���а�ť
	Button btnPrintAll;
	// ���ذ�ť
	Button btnReturn;
	// �ύ��ť
	Button btnCommit;
	// �ص���ť
	Button btnVoucherWriter;
	// �˻ذ�ť
	Button btnVoucherBack;
	// ��˰�ť
	Button btnVOucherVerify;

	// ��ӡ���а�ť
	Button btnPrintAllNew;
	

	private int listCount = 0;
	private static int jointCount = 0;
	static Composite composite_table;
	static Composite composite_dwbk;
	static Composite composite_direct;
	Composite composite_OCX;

	// ����ļ�¼�б�
	static List<TvVoucherinfoDto> voucherDtoList = new ArrayList<TvVoucherinfoDto>();
	static List<IDto> selectDtoList = new ArrayList<IDto>();
	static String admDivCode;

	// ������ʾ����
	ReportComposite report;

	private CheckboxTableViewer checkBizTableView;
	private CheckboxTableViewer checkBizTableViewDwbk;
	private CheckboxTableViewer checkBizTableViewDirect;
	private PagingTableComposite paging;
	private PagingTableComposite pagingdwbk;
	private PagingTableComposite pagingdirect;
	private TvPayoutmsgmainDto payoutdto;

	private static ITFELoginInfo loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
//	private static Combo stampTypeComposite;
	
	/**
	 * ����ǩ�±��ģ���ȡUKEYӡ�£�
	 * 
	 * @param dto
	 * @throws FileOperateException
	 */
	public static String getVoucherStamp(TvVoucherinfoDto vDto, String certID,String stampPosition, String stampID) {
		String voucherStampXML="";
		String stamp="";
		String err=null;
		String voucherXML="";
		try {
			voucherXML = ((BankNameEnterBean) context.getModelHolder().getModel()).getVoucherXMl(vDto);
			if(!vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)){
				logger.debug("ƾ֤ǩ��д���ǩ�±��ģ�"+voucherXML);
			}
		} catch (Exception e) {
			logger.error(e);
			err="ƾ֤���Ľ����쳣";
			return err+"@";
		}
		
		try {
			stamp = getStampPotisionXML(stampPosition, stampID);
			logger.debug("ƾ֤ǩ��д���ǩ��λ�ñ��ģ�"+stamp);
		} catch (Exception e) {
			logger.error(e);
			err="��װǩ��λ�ñ����쳣";
			return err+"@";
		
		}
		voucherStampXML=getVoucherStamp(certID, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(), stamp, voucherXML);
		if(voucherStampXML==null||voucherStampXML.equals("")){
			 err = GetLastErr();
			if (err != null && !err.equals("")) {
					return err+"@";
			}
			return "��װƾ֤ǩ�±����쳣"+"@";
		}
		return voucherStampXML;
	
	
	}

	/**
	 * ��װǩ��λ�ñ���
	 * 
	 */
	public static String getStampPotisionXML(String stampPosition,
			String stampID) throws Exception {
		Document stampDoc = null;
		stampDoc = DocumentHelper.createDocument();
		stampDoc.setXMLEncoding("GBK");
		Element Root = stampDoc.addElement("MOF");
		Element Stamp = Root.addElement("Stamp");
		Stamp.addAttribute("No", stampPosition);
		Stamp.setText(stampID);
		return stampDoc.asXML().replaceAll("&lt;", "<").replaceAll("&gt;", ">");
	}

	/**
	 * ��װƾ֤ǩ�±���
	 * 
	 */
	public static String getVoucherStampXML(TvVoucherinfoDto dto)
			throws Exception {

		Document fxrDoc;// ��ʱ��ƾ֤����
		Document successDoc;// ���ظ�ƾ֤��ĳɹ�����
		successDoc = DocumentHelper.createDocument();
		successDoc.setXMLEncoding("GBK");
		Element root = successDoc.addElement("MOF");
		Element VoucherCount = root.addElement("VoucherCount");

		VoucherCount.setText("1");
		Element newBody = root.addElement("VoucherBody");
		newBody.addAttribute("AdmDivCode", dto.getSadmdivcode());
		newBody.addAttribute("StYear", dto.getSstyear());
		newBody.addAttribute("VtCode", dto.getSvtcode());
		newBody.addAttribute("VoucherNo", dto.getSvoucherno());
		Element VoucherFlag = newBody.addElement("VoucherFlag");
		VoucherFlag.setText("1");
		Element Return_Reason = newBody.addElement("Return_Reason");
		Element Attach = newBody.addElement("Attach");
		Attach.setText("SUCCESS");
		Element Voucher = newBody.addElement("Voucher");
		Voucher.setText(base64Encode(com.cfcc.itfe.util.FileUtil.getInstance()
				.readFile("M:/rev5207_20150401045211524.msg")));

		return successDoc.asXML().replaceAll("&lt;", "<").replaceAll("&gt;",
				">");
	}

	public static List getListByAdmdivcode(List<TvVoucherinfoDto> list) {
		Map map = new HashMap();
		for (TvVoucherinfoDto dto : list) {
			map.put(dto.getSadmdivcode(), "");
		}
		if (map.size() == 0) {
			return null;
		}
		List<List> lists = new ArrayList<List>();
		try {

			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvVoucherinfoDto> newList = new ArrayList<TvVoucherinfoDto>();
				for (TvVoucherinfoDto dto : list) {
					String admdivcode = dto.getSadmdivcode();

					if (admdivcode.equals(key)) {

						newList.add(dto);
					}
				}
				lists.add(newList);
			}

		} catch (Exception e) {

			MessageDialog.openErrorDialog(null, e);
		}

		return lists;
	}

	public static String Refresh(String voucherno) {
		Variant v = execActivexMethod(ShowCompositeVoucherOcx.Refresh,
				new Object[] { voucherno });
		return v.getString();
	}

	/**
	 * �����ݽ���ǩ��
	 * 
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param stamp
	 * @param voucher
	 * @return
	 */
	public static String getVoucherStamp(String certID, String admDivCode,
			long stYear, String vtCode, String stamp, String voucher) {
		Variant v = execActivexMethod(
				OcxConstant.GETVOUCHERSTAMP, new Object[] {
						certID, admDivCode, new Long(stYear), vtCode, stamp,
						voucher });
		if (null == v) {
			return null;
		} else {
			return v.getString();
		}
	}

	/**
	 * ƾ֤��ʾ
	 * 
	 * @param num
	 */
	public static void init(int num, IDto idto) {
		if(!loginInfo.getPublicparam().contains(",bankenterdisplay=false,"))//��¼����̫�����Ż�
		{
			voucherDtoList = ((BankNameEnterBean) context.getModelHolder()
					.getModel()).queryVoucherIndexInfo(idto);
			if (null ==voucherDtoList || voucherDtoList.size()==0) {
				MessageDialog.openMessageDialog(null, "ƾ֤�������в�ѯ����������¼����Ϣ��");
				return;
			} else{
				ls_AllCount = voucherDtoList.size() + "";
				// ��ʼ��Ϊ��һ��ƾ֤����Ϣ
				TvVoucherinfoDto infoDto = voucherDtoList.get(num);
				
				//�㽭ǩ��ר��
		//		if (infoDto.getSorgcode().startsWith("11")) {
				
				//��ʼ��ǩ������
		//		List<Mapper> retList =new ArrayList<Mapper>();
		//		retList=((BankNameEnterBean) context.getModelHolder().getModel()).getStampEnums(infoDto);
		//		getStampTypeComposite().removeAll();
		//		
		//		for(Mapper map : retList){
		//			getStampTypeComposite().add(map.getDisplayValue().toString());
		//		}
		//		
		//		}
				int i = -1;
				String err = null;
		
				i = initStampOcx("0", infoDto.getSadmdivcode(), Integer
						.parseInt(infoDto.getSstyear()), infoDto.getSvtcode().trim(),
						"0", 2, 0, 0);
				if (i == 0) {
					i = addVoucher(infoDto.getSvoucherno());
					if (i == 0) {
						i = setCurrentVoucher(infoDto.getSvoucherno());
						Refresh (infoDto.getSvoucherno());
						if (i != 0) {
							err = GetLastErr();
							if (err != null) {
								MessageDialog
										.openMessageDialog(null, "ocx�����쳣�� " + err);
							}
						}
					} else {
						err = GetLastErr();
						if (err != null) {
							MessageDialog.openMessageDialog(null, "ocx�����쳣�� " + err);
						}
					}
				} else {
					err = GetLastErr();
					if (err != null) {
						MessageDialog.openMessageDialog(null, "ocx�����쳣�� " + err);
					}
				}
			}
		}

	}

	/**
	 * ƾ֤��ʾ
	 * 
	 * @param num
	 */
	public static void init(int num) {
		if(!loginInfo.getPublicparam().contains(",bankenterdisplay=false,"))//��¼����̫�����Ż�
		{
			voucherDtoList = ((BankNameEnterBean) context.getModelHolder()
					.getModel()).getChecklist();
			ls_AllCount = voucherDtoList.size() + "";
	
			allCount.setText(ls_AllCount);
			// ��ʼ��Ϊ��һ��ƾ֤����Ϣ
			TvVoucherinfoDto infoDto = voucherDtoList.get(num);
			int count = ((BankNameEnterBean) context.getModelHolder().getModel())
					.queryVoucherJOintCount(infoDto);
			if (count == -1) {
				nowPageCount.setText("����δά��");
			} else if (count == -2) {
				nowPageCount.setText("��ѯ�쳣");
			} else {
				jointCount = count;
				nowPageCount.setText("" + 1);
			}
			
			String printCounts = ((BankNameEnterBean) context.getModelHolder()
					.getModel()).queryVoucherPrintCount(infoDto);
			printCount.setText(printCounts);
			int i = -1;
			String err = null;
			i = initStampOcx("0", infoDto.getSadmdivcode(), Integer
					.parseInt(infoDto.getSstyear()), infoDto.getSvtcode().trim(),
					"0", 2, 0, 2);
			if (i == 0) {
				i = addVoucher(infoDto.getSvoucherno());
				if (i == 0) {
					i = setCurrentVoucher(infoDto.getSvoucherno());
					Refresh (infoDto.getSvoucherno());
					if (i != 0) {
						err = GetLastErr();
						if (err != null) {
							MessageDialog
									.openMessageDialog(null, "ocx�����쳣�� " + err);
						}
					}
				} else {
					err = GetLastErr();
					if (err != null) {
						MessageDialog.openMessageDialog(null, "ocx�����쳣�� " + err);
					}
				}
			} else {
				err = GetLastErr();
				if (err != null) {
					MessageDialog.openMessageDialog(null, "ocx�����쳣�� " + err);
				}
			}
		}
	}

	/**
	 * @param parent
	 */
	public ShowCompositeVoucherOcx(Composite parent) {

		super(parent, SWT.BORDER);
		FormLayout formLayout = new FormLayout();
		setLayout(formLayout);
		try {
			context = DataBindingContext
					.createInstance("c5dc725e-2b29-42bf-9e9f-77f1b06143a4");
			// ����table����
			composite_table = new Composite(this, SWT.BORDER);
			createStampContentsForPayTable(composite_table, context);
			
		    composite_dwbk = new Composite(this, SWT.BORDER);
			createStampContentsForDwbkTable(composite_dwbk, context);
			
			 composite_direct = new Composite(this, SWT.BORDER);
			 createStampContentsForDirectTable(composite_direct, context);
			// ������ʾ����ƾ֤������
			composite_OCX = new Composite(this, SWT.BORDER);
			createStampContents(composite_OCX, composite_table, context);
			// ������ť����
			Composite composite_Button = new Composite(this, SWT.BORDER);
			createStampContentsForButton(composite_Button, composite_table,
					composite_OCX, context);

			this.layout(true);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);

		}

	}

	/**
	 * ��ʾBUTTON����
	 */
	protected void createStampContentsForButton(Composite composite,
			Composite compositeTAB, Composite compositeOCX,
			final DataBindingContext context) {
		final FormData formData = new FormData();
		formData.top = new FormAttachment(compositeTAB, 5, SWT.BOTTOM);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(compositeOCX, 0, SWT.BOTTOM);
		formData.left = new FormAttachment(compositeOCX, 2, SWT.RIGHT);
		composite.setLayoutData(formData);
		composite.setLayout(new FormLayout());

		final Button selectAll = new Button(composite, SWT.NONE);
		final FormData formData_1 = new FormData();
		formData_1.height = 30;
		formData_1.width = 110;
		formData_1.top = new FormAttachment(compositeOCX, 40, SWT.TOP);
		formData_1.left = new FormAttachment(compositeOCX, 10, SWT.RIGHT);
		selectAll.setLayoutData(formData_1);
		selectAll.setText("ȫѡ");
		selectAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				List list = new ArrayList();
				List listpayout = new ArrayList();
				List listdwbk = new ArrayList();
				List listdirect = new ArrayList();
				if (checkBizTableView.getCheckedElements() != null
						&& checkBizTableView.getCheckedElements().length > 0) {
					list = new ArrayList();
					listpayout = new ArrayList();
					checkBizTableView.setAllChecked(false);
					
				} else {
					checkBizTableView.setAllChecked(true);
					Object[] objs = checkBizTableView.getCheckedElements();
					for (Object obj : objs) {
						list.add(obj);
						listpayout.add(obj);
					}
				}
				((BankNameEnterBean) context.getModelHolder().getModel())
				.setPagingList(listpayout);
				if (checkBizTableViewDwbk.getCheckedElements() != null
						&& checkBizTableViewDwbk.getCheckedElements().length > 0) {
					list = new ArrayList();
					listdwbk = new ArrayList();
					checkBizTableViewDwbk.setAllChecked(false);
					
					checkBizTableViewDwbk.getTable().deselectAll();
				} else {
					checkBizTableViewDwbk.setAllChecked(true);
					checkBizTableViewDwbk.getTable().selectAll();
					Object[] objs = checkBizTableViewDwbk.getCheckedElements();
					for (Object obj : objs) {
						list.add(obj);
						listdwbk.add(obj);
					}
				}
				if (checkBizTableViewDirect.getCheckedElements() != null
						&& checkBizTableViewDirect.getCheckedElements().length > 0) {
					list = new ArrayList();
					listdirect = new ArrayList();
					checkBizTableViewDirect.setAllChecked(false);
					
					checkBizTableViewDirect.getTable().deselectAll();
				} else {
					checkBizTableViewDirect.setAllChecked(true);
					checkBizTableViewDirect.getTable().selectAll();
					Object[] objs = checkBizTableViewDirect.getCheckedElements();
					for (Object obj : objs) {
						list.add(obj);
						listdirect.add(obj);
					}
				}
				((BankNameEnterBean) context.getModelHolder().getModel())
				.setSelectlist(list);
				((BankNameEnterBean) context.getModelHolder().getModel())
				.setPagingList2(listdwbk);
				((BankNameEnterBean) context.getModelHolder().getModel())
				.setPagingListDirect(listdirect);
			}
		});

		final Button addRecord = new Button(composite, SWT.NONE);
		final FormData formData_2 = new FormData();
		formData_2.height = 30;
		formData_2.width = 110;
		formData_2.top = new FormAttachment(selectAll, 10);
		formData_2.left = new FormAttachment(selectAll, 0, SWT.LEFT);
		addRecord.setLayoutData(formData_2);
		addRecord.setText("��¼");
		addRecord.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				((BankNameEnterBean) context.getModelHolder().getModel())
						.addRecord(null);
			}
		});

		final Button auditOK = new Button(composite, SWT.NONE);
		final FormData formData_3 = new FormData();
		formData_3.height = 30;
		formData_3.width = 110;
		formData_3.top = new FormAttachment(addRecord, 10);
		formData_3.left = new FormAttachment(addRecord, 0, SWT.LEFT);
		auditOK.setLayoutData(formData_3);
		auditOK.setText("���ͨ��");
		auditOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				((BankNameEnterBean) context.getModelHolder().getModel())
						.checkSuccess(null);
			}
		});

		final Button auditNO = new Button(composite, SWT.NONE);
		final FormData formData_4 = new FormData();
		formData_4.height = 30;
		formData_4.width = 110;
		formData_4.top = new FormAttachment(auditOK, 10);
		formData_4.left = new FormAttachment(auditOK, 0, SWT.LEFT);
		auditNO.setLayoutData(formData_4);
		auditNO.setText("���ʧ��");
		auditNO.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				((BankNameEnterBean) context.getModelHolder().getModel())
						.checkfault(null);
			}
		});

		final Button checkOK = new Button(composite, SWT.NONE);
		final FormData formData_5 = new FormData();
		formData_5.height = 30;
		formData_5.width = 110;
		formData_5.top = new FormAttachment(auditNO, 10);
		formData_5.left = new FormAttachment(auditNO, 0, SWT.LEFT);
		checkOK.setLayoutData(formData_5);
		checkOK.setText("����ͨ��");
		checkOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				((BankNameEnterBean) context.getModelHolder().getModel())
						.bufusuccess(null);
			}
		});

		final Button checkno = new Button(composite, SWT.NONE);
		final FormData formData_6 = new FormData();
		formData_6.height = 30;
		formData_6.width = 110;
		formData_6.top = new FormAttachment(checkOK, 10);
		formData_6.left = new FormAttachment(checkOK, 0, SWT.LEFT);
		checkno.setLayoutData(formData_6);
		checkno.setText("����ʧ��");
		checkno.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				((BankNameEnterBean) context.getModelHolder().getModel())
						.reviewfault(null);
			}
		});
		
		final Button Btn2PrintAll = new Button(composite, SWT.NONE);
		final FormData formData_7 = new FormData();
		formData_7.height = 30;
		formData_7.width = 110;
		formData_7.top = new FormAttachment(checkno, 10);
		formData_7.left = new FormAttachment(checkno, 0, SWT.LEFT);
		Btn2PrintAll.setLayoutData(formData_7);
		Btn2PrintAll.setText("������ӡ");
		Btn2PrintAll.addSelectionListener(new Btn2PrintAll());
		
		
		//ҵ��Ҫ�ز�¼����ǩ˽�°�ť�Ƿ���ʾ
		if (loginInfo.getPublicparam().indexOf(",stampbutton=true,")>=0) {
	
			//ǩ��
			final Button SingStamp = new Button(composite, SWT.NONE);
			final FormData formData_8 = new FormData();
			formData_8.height = 30;
			formData_8.width = 110;
			formData_8.top = new FormAttachment(Btn2PrintAll, 10);
			formData_8.left = new FormAttachment(Btn2PrintAll, 0, SWT.LEFT);
			SingStamp.setLayoutData(formData_8);
			SingStamp.setText("ǩ˽��");
			SingStamp.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.singStamp(null);
				}
			});
//		final Label stamptypelabel = new Label(composite, SWT.NONE);
//		stamptypelabel.setText("ǩ������");
		
	/*	��ʾǩ������
		setStampTypeComposite(new  Combo(composite,SWT.NONE)); 
//		stampTypeComposite.setText("��ѡ��ǩ������");
		final FormData formData_9 = new FormData();
		
		formData_9.height = 30;
		formData_9.width = 110;
		formData_9.top = new FormAttachment(SingStamp, 10);
		formData_9.left = new FormAttachment(SingStamp, 0, SWT.LEFT);
		getStampTypeComposite().setLayoutData(formData_9);
//		stampTypeComposite.setText("ǩ������");
//		stampTypeComposite.add("1");
//		stampTypeComposite.add("2");
//		stampTypeComposite.add("3");
		getStampTypeComposite().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				
			}
		});
		*/
			final Button retBack = new Button(composite, SWT.NONE);
			final FormData formData_fh = new FormData();
			formData_fh.height = 30;
			formData_fh.width = 110;
			formData_fh.top = new FormAttachment(SingStamp, 10);
			formData_fh.left = new FormAttachment(SingStamp, 0, SWT.LEFT);
			retBack.setLayoutData(formData_fh);
			retBack.setText("����");
			retBack.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.exit(null);
				}
			});
		}else {
			final Button retBack = new Button(composite, SWT.NONE);
			final FormData formData_fh = new FormData();
			formData_fh.height = 30;
			formData_fh.width = 110;
			formData_fh.top = new FormAttachment(Btn2PrintAll, 10);
			formData_fh.left = new FormAttachment(Btn2PrintAll, 0, SWT.LEFT);
			retBack.setLayoutData(formData_fh);
			retBack.setText("����");
			retBack.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.exit(null);
				}
			});
		}
		
		
		
	}

	/**
	 * ��ʾ��ѯTABLE����
	 * 
	 * @throws Exception
	 */
	protected void createStampContentsForPayTable(Composite composite,
			final DataBindingContext context) throws Exception {

		final FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(2, 5, 0);
		formData.left = new FormAttachment(0, 0);
		composite.setLayoutData(formData);
		composite.setLayout(new FormLayout());
		paging = new PagingTableComposite(composite, "pagingList", 0.5, 0.6);// ��ҳ��ʾ
		GridLayout gridLayout = (GridLayout) paging.getLayout();
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		checkBizTableView = (CheckboxTableViewer) paging.getTableViewer();
		// ��ȡ��¼����ö��ֵ
		ScheckstatusEnumFactory enumRet = new ScheckstatusEnumFactory();
		List<Mapper> retList = enumRet.getEnums(null);// ������
		TableFacadeX payoutFacade = new TableFacadeX(checkBizTableView);
		payoutFacade.addColumn("ƾ֤���", 90, "staxticketno", true);
		payoutFacade.addColumn("���", 100, "nmoney", true);
		payoutFacade.addColumn("�������", 80, "strecode", true);
		payoutFacade.addColumn("Ԥ�㵥λ", 80, "sbudgetunitcode", true);
		payoutFacade.addColumn("�տ�������", 120, "srecname", true);
		payoutFacade.addColumn("�տ����˺�", 100, "srecacct", true);
		payoutFacade.addColumn("�տ��˿���������", 120, "srecbankname", true);
		payoutFacade.addColumn("�տ��˿������к�", 90, "srecbankno", true);
		payoutFacade.addColumn("֧��ϵͳ����", 120, "sinputrecbankname", true);
		payoutFacade.addColumn("��¼����״̬", 80, "scheckstatus", retList, true);
		payoutFacade.addColumn("ί������", 80, "scommitdate", true);

		checkBizTableView.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				((BankNameEnterBean) context.getModelHolder().getModel())
						.setSelDto((IDto) selection.getFirstElement());
				((BankNameEnterBean) context.getModelHolder().getModel())
						.doubleclickTObulu(selection.getFirstElement());
			}

		});
		checkBizTableView
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						if (null != selection) {
							IDto idto = (IDto) selection.getFirstElement();
							if(null!=idto){
								((BankNameEnterBean) context.getModelHolder()
										.getModel()).setSelDto(idto);
								init(0, idto);
							}
						}
					}

				});
		checkBizTableView.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object obj = event.getElement();
				if (event.getChecked()) {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.setSelDto((IDto) obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
							.getSelectlist().add(obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
					.getPagingList().add(obj);
				} else {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.setSelDto(null);
					((BankNameEnterBean) context.getModelHolder().getModel())
							.getSelectlist().remove(obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
					.getPagingList().remove(obj);
				}
				init(0, (IDto) obj);
			}
		});

		context
				.bindPagingCompositeX(paging, "tablePagingContext",
						"pagingList");

	}

	/**
	 * ��ʾ��ѯTABLE����
	 * 
	 * @throws Exception
	 */
	protected void createStampContentsForDwbkTable( Composite composite,
			final DataBindingContext context) throws Exception {

		final FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(2, 5, 0);
		formData.left = new FormAttachment(0, 0);
		composite.setLayoutData(formData);
		composite.setLayout(new FormLayout());
		pagingdwbk = new PagingTableComposite(composite, "pagingList2", 0.5, 0.6);// ��ҳ��ʾ
		GridLayout gridLayout = (GridLayout) pagingdwbk.getLayout();
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		checkBizTableViewDwbk = (CheckboxTableViewer) pagingdwbk.getTableViewer();
		// ��ȡ��¼����ö��ֵ
		ScheckstatusEnumFactory enumRet = new ScheckstatusEnumFactory();
		List<Mapper> retList = enumRet.getEnums(null);// ������
		TableFacadeX payoutFacade = new TableFacadeX(checkBizTableViewDwbk);
		payoutFacade.addColumn("ƾ֤���", 100, "selecvouno", true);
		payoutFacade.addColumn("���", 100, "famt", true);
		payoutFacade.addColumn("�������", 80, "spayertrecode", true);
		payoutFacade.addColumn("Ԥ�㵥λ", 100, "staxorgcode", true);
		payoutFacade.addColumn("�տ��˿���������", 120, "srecbankname", true);
		payoutFacade.addColumn("�տ��˿������к�", 90, "spayeeopnbnkno", true);
		payoutFacade.addColumn("֧��ϵͳ����", 120, "sinputrecbankname", true);
		payoutFacade.addColumn("�˸�ԭ�����", 120, "sdwbkreasoncode", true);
		/**
		 * ����(��������������36��ͷ)����Ҫ��¼�˸�ԭ�����,�ɲ�����д,
		 * �����ڴ˴���Ҫ��������д���˸�ԭ�������˸�ԭ������ֱ����ʾ����
		 */
		if(loginInfo.getSorgcode().startsWith("36")){
			payoutFacade.addColumn("�˸�ԭ��", 150, "sreturnreasonname", true);
		}else{
			List<Mapper> dwbkreasonlist =((BankNameEnterBean) context.getModelHolder().getModel()).getMapOfDwbkR();
			payoutFacade.addColumn("�˸�ԭ��", 150, "sdwbkreasoncode",dwbkreasonlist, true);
		}
		payoutFacade.addColumn("��¼����״̬", 80, "scheckstatus", retList, true);
		payoutFacade.addColumn("ί������", 80, "daccept", true);
		checkBizTableViewDwbk.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				((BankNameEnterBean) context.getModelHolder().getModel())
						.setSelDto((IDto) selection.getFirstElement());
				((BankNameEnterBean) context.getModelHolder().getModel())
						.doubleclickTObulu(selection.getFirstElement());

			}
		});
		checkBizTableViewDwbk
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						if (null != selection) {
							IDto idto = (IDto) selection.getFirstElement();
							if(null!=idto){
								((BankNameEnterBean) context.getModelHolder()
										.getModel()).setSelDto(idto);
								init(0, idto);
							}
						}
					}

				});
		checkBizTableViewDwbk.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object obj = event.getElement();
				if (event.getChecked()) {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.setSelDto((IDto) obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
							.getSelectlist().add(obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
					.getPagingList2().add(obj);
				} else {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.setSelDto(null);
					((BankNameEnterBean) context.getModelHolder().getModel())
							.getSelectlist().remove(obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
					.getPagingList2().remove(obj);
				}
				init(0, (IDto) obj);
			}
		});
		context
				.bindPagingCompositeX(pagingdwbk, "tableDwbkPagingContext",
						"pagingList2");
	}
	
	/**
	 * ��ʾ��ѯTABLE����
	 * ֱ��֧��(5201)
	 * @param composite
	 * @param context
	 * @throws Exception
	 */
	protected void createStampContentsForDirectTable( Composite composite,
			final DataBindingContext context) throws Exception {

		final FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(2, 5, 0);
		formData.left = new FormAttachment(0, 0);
		composite.setLayoutData(formData);
		composite.setLayout(new FormLayout());
		pagingdirect = new PagingTableComposite(composite, "pagingListDirect", 0.5, 0.6);// ��ҳ��ʾ
		GridLayout gridLayout = (GridLayout) pagingdirect.getLayout();
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		checkBizTableViewDirect = (CheckboxTableViewer) pagingdirect.getTableViewer();
		// ��ȡ��¼����ö��ֵ
		ScheckstatusEnumFactory enumRet = new ScheckstatusEnumFactory();
		List<Mapper> retList = enumRet.getEnums(null);// ������
		TableFacadeX payoutFacade = new TableFacadeX(checkBizTableViewDirect);
		payoutFacade.addColumn("ƾ֤���", 150, "svoucherno", true);
		payoutFacade.addColumn("���", 120, "npayamt", true);
		payoutFacade.addColumn("�������", 80, "strecode", true);
		payoutFacade.addColumn("�տ��˿���������", 150, "spayeeacctbankname", true);
		payoutFacade.addColumn("�տ��˿������к�", 100, "spayeeacctbankno", true);
		payoutFacade.addColumn("֧��ϵͳ����", 150, "sinputrecbankname", true);
		payoutFacade.addColumn("��¼����״̬", 80, "scheckstatus", retList, true);
		payoutFacade.addColumn("ί������", 80, "scommitdate", true);

		checkBizTableViewDirect.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				((BankNameEnterBean) context.getModelHolder().getModel())
						.setSelDto((IDto) selection.getFirstElement());
				((BankNameEnterBean) context.getModelHolder().getModel())
						.doubleclickTObulu(selection.getFirstElement());
			}

		});
		checkBizTableViewDirect
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						if (null != selection) {
							IDto idto = (IDto) selection.getFirstElement();
							if(null!=idto){
								((BankNameEnterBean) context.getModelHolder()
										.getModel()).setSelDto(idto);
								init(0, idto);
							}
						}
					}

				});
		checkBizTableViewDirect.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object obj = event.getElement();
				if (event.getChecked()) {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.setSelDto((IDto) obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
							.getSelectlist().add(obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
					.getPagingList().add(obj);
				} else {
					((BankNameEnterBean) context.getModelHolder().getModel())
							.setSelDto(null);
					((BankNameEnterBean) context.getModelHolder().getModel())
							.getSelectlist().remove(obj);
					((BankNameEnterBean) context.getModelHolder().getModel())
					.getPagingList().remove(obj);
				}
				init(0, (IDto) obj);
			}
		});

		context
				.bindPagingCompositeX(pagingdirect, "tableDirectPagingContext",
						"pagingListDirect");

	}

	/**
	 * ����AcitveX�ؼ���ʾ����ƾ֤������
	 */
	protected void createStampContents(Composite composite,
			Composite composite1, DataBindingContext context) {

		final FormData formData = new FormData();
		formData.top = new FormAttachment(composite1, 5, SWT.BOTTOM);
		formData.width = 860;
		formData.height = 500;
		formData.left = new FormAttachment(0, 0);
		composite.setLayoutData(formData);
		composite.setLayout(new FormLayout());
		_frame = new OleFrame(composite, SWT.BORDER);
		_site = new OleControlSite(_frame, SWT.NONE,
				"ESTAMPOCX.EstampOcxCtrl.1");
		_auto = new OleAutomation(_site);
		_frame.setBounds(0, 480, 850, 400);
		// ����Ҫ������Ƕ�ؼ��Ĵ�С����Ϊ�����Ѿ������˸��ؼ��Ĵ�С����Ƕ�ؼ��ᰴ�ո��ؼ��Ĵ�С��ʾ
		// _site.doVerb(OLE.OLEIVERB_PRIMARY);
		_site.doVerb(OLE.OLEIVERB_UIACTIVATE);
		_site.doVerb(OLE.OLEIVERB_SHOW);
		_frame.setVisible(true);

		// ������ʾ����
		ReportComposite report = new ReportComposite(composite,
				JasperConstants.ALL);
		report.setBounds(0, 480, 850, 400);
		// ���ñ�����ɫ����ActiveX�ؼ�һ��
		report.setBackground(new Color(composite.getDisplay(), new RGB(192,
				192, 192)));
		report.setVisible(true);

		/**�޸�Ϊ�ɹ������������(itfeesb_linux.properties�ļ��е�ocxservice.isClientProxy����)�����Ƿ�ʹ�ô���ʽ���������ʵ�URL��ַ 20141001**/
		String ls_URL = ClientOcxServiceProxyUtils.getOcxVoucherServerURL();
		String stampurl = ClientOcxServiceProxyUtils.getOCXStampServerURL();
		asynsetUrl(ls_URL, stampurl);
	}
	
	//���̳߳�ʼ��ƾ֤��
	public void asynsetUrl(final String voucherUrl, final String stampUrl) {
		// �¶����߳���������
		new Thread() {
			public void run() {
				// ����ocx�ĳ�ʼ������
				Variant v1 = execActivexMethod(OcxConstant.SETURL,
						new Object[] { voucherUrl + OcxConstant.EVOUCHERSERVICEURL });
				if(v1.getInt()!=0){
					logger.error("��ʼ������ƾ֤������ַʧ��!"+voucherUrl + OcxConstant.EVOUCHERSERVICEURL );
				}
				Variant v2 = execActivexMethod(OcxConstant.SETEstampURL,
						new Object[] { stampUrl + (loginInfo.getPublicparam().contains(",ocxnewinterface=true,")?OcxConstant.ESTAMPSERVICEURL.replace("realware", "estamp"):OcxConstant.ESTAMPSERVICEURL) });
				if(v2.getInt()!=0){
					logger.error("��ʼ������ǩ�·����ַʧ��!" + stampUrl + (loginInfo.getPublicparam().contains(",ocxnewinterface=true,")?OcxConstant.ESTAMPSERVICEURL.replace("realware", "estamp"):OcxConstant.ESTAMPSERVICEURL) );
				}
			}
		}.start();
	}

	public void KoalStampHandler(OleControlSite controlSite) {
		this.controlSite = controlSite;
		this.automation = new OleAutomation(controlSite);
	}

	/**
	 * ��ʼ��ocx�ؼ�
	 */
	private final static String INITOCX = "Initialize";

	/**
	 * ���ƾ֤�ĳ�����
	 */
	private final static String ADDVOUCHER = "AddVoucher";
	/**
	 * ���ƾ֤�ĳ�����(����������)
	 */
	private final static String ADDVOUCHERFROMSERVER = "AddVoucherfromServer";

	/**
	 * ���ƾ֤��Ϣ�ĳ�����
	 */
	private final static String CLEARVOUCHER = "EstampOcxClearVouchers";
	private final static String Refresh = "Refresh";
	/**
	 * չʾ��ǰѡ����ƾ֤��Ϣ
	 */
	private final static String SETCURRENTVOUCHER = "SetCurrentVoucher";

	/**
	 * ����Ӧ��ʾ
	 */
	private final static String ZOOMTOFIT = "ZoomToFit";
	/**
	 * ��С��ʾ
	 */
	private final static String ZOOMOUT = "ZoomOut";

	/**
	 * �Ŵ���ʾ
	 */
	private final static String ZOOMIN = "ZoomIn";
	/**
	 * ���ǩ�������ݵĳ���
	 */
	private final static String GETSIGNEDVOUCHERS = "EstampOcxGetSingedVouchers";

	/**
	 *��ò������ĵĳ�����
	 */
	private final static String GETOPERATEVOUCHRS = "EstampOcxGetVoucherOperate";

	/**
	 * ����url
	 */
	private final static String SETURL = "SetEvoucherServiceUrl";

	/**
	 * ���ǩ�µ����ݵĳ���
	 */
	private final static String GETVOUCHERSTAMP = "GetVoucherStamp";

	/**
	 * ��ȡocx���ù����쳣��Ϣ
	 */
	private final static String GetLastErr = "GetLastErr";

	/**
	 * ����ƾ֤��ӡ����̨��ӡ������ʾOCX��
	 */
	private final static String PRINTALLVOUCHER = "PrintAllVoucher";

	/**
	 * ����ƾ֤��ӡ
	 */
	private final static String PRINTONEVOUCHER = "PrintVoucherByNo";

	/**
	 * ��һ��
	 */
	private final static String PAGEUP = "PageUp";

	/**
	 * ��һ��
	 */
	private final static String PAGEDOWN = "PageDown";

	/**
	 * ʢ��ocx�����������
	 */
	private OleFrame _frame;

	/**
	 * ���ocx�Ŀռ� �������oleframe ��ȥ
	 */
	private OleControlSite _site;

	/**
	 * ����ִ�з���
	 */
	private static OleAutomation _auto;

	public Shell shell = null;

	private static int joint = 0; // Ĭ����ʾ��

	/**
	 * ��ʼ��ocx�ؼ�
	 * 
	 * @param certId
	 *            ֤��id
	 * @param year
	 *            ҵ�����
	 * @param voucherType
	 *            ƾ֤����
	 * @param stepCode
	 *            �ڼ���
	 * @param operateType
	 *            ��������
	 * @param defaultPage
	 *            Ĭ��ҵ
	 * @param displayMode
	 *            ��ʾģʽ
	 */
	public static int initStampOcx(String certID, String admDivCode, int stYear, String vtCode, String reserver1, 
			int displayMode, int ParamValue, int defaultPage) {
	
		// ��ʼ��Ĭ����ʾ��
		joint = (int) defaultPage;

		// ����ocx�ĳ�ʼ������
		Variant v = execActivexMethod(ShowCompositeVoucherOcx.INITOCX,
				new Object[] { certID, admDivCode, new Long(stYear), vtCode,
				reserver1, new Long(displayMode), new Long(ParamValue),
						new Long(defaultPage) });

		return v.getInt();

	}

	/**
	 *��ȡocx���ù����쳣��Ϣ
	 * 
	 */
	public static String GetLastErr() {
		Variant v = execActivexMethod(ShowCompositeVoucherOcx.GetLastErr,
				new Object[] {});
		return v.getString();
	}

	public void setUrl(String url) {

		// ����ocx�ĳ�ʼ������
		execActivexMethod(ShowCompositeVoucherOcx.SETURL, new Object[] { url });
	}

	/**
	 * ��ocx�ؼ������ƾ֤
	 * 
	 * @param voucherNO
	 *            ƾ֤��
	 * @param xmlBody
	 *            ����ָ����ʽƴװ��xml
	 * @return �Ƿ�ɹ�
	 */
	public boolean addVoucher(String voucherNO, String xmlBody) {
		// ����ocx���ƾ֤�ķ���
		Variant v = execActivexMethod(ShowCompositeVoucherOcx.ADDVOUCHER,
				new Object[] { voucherNO, xmlBody });
		// 0��ʾ�ɹ���-1��ʾʧ��
		if (v.getLong() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ocx�ؼ������ƾ֤
	 * 
	 * @param voucherNO
	 *            ƾ֤��
	 * @param
	 * @return �Ƿ�ɹ�
	 */
	public static int addVoucher(String voucherNO) {
		// ����ocx���ƾ֤�ķ���
		Variant v = execActivexMethod(
				ShowCompositeVoucherOcx.ADDVOUCHERFROMSERVER,
				new Object[] { voucherNO });
		// 0��ʾ�ɹ���-1��ʾʧ��
		return v.getInt();
	}

	/**
	 * ���ƾ֤��Ϣ
	 * 
	 * @return �Ƿ�����ɹ�
	 */
	public boolean clearVouchers() {
		// ����ocx���ƾ֤�ķ���
		Variant v = execActivexMethod(ShowCompositeVoucherOcx.CLEARVOUCHER,
				null);
		// 0��ʾ�ɹ���-1��ʾʧ��
		if (v.getLong() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ���õ�ǰ��ʾ��ƾ֤
	 * 
	 * @param voucherNO
	 *            ƾ֤��
	 * @return
	 */
	public static int setCurrentVoucher(String voucherNO) {
		// ���õ�ǰ��ʾ�ķ���
		Variant v = execActivexMethod(
				ShowCompositeVoucherOcx.SETCURRENTVOUCHER,
				new String[] { voucherNO });
		// 0��ʾ�ɹ���-1��ʾʧ��
		return v.getInt();
	}

	/**
	 * ����Ӧ��ʾƾ֤
	 * 
	 * @return
	 */
	public static void ZoomToFit() {
		// ���õ�ǰ��ʾ�ķ���
		execActivexMethod(ShowCompositeVoucherOcx.ZOOMTOFIT, new String[] {});

	}

	/**
	 * ��Сƾ֤��ʾ
	 * 
	 * @return
	 */
	public static void ZoomOut() {
		// ���õ�ǰ��ʾ�ķ���
		execActivexMethod(ShowCompositeVoucherOcx.ZOOMOUT, new String[] {});

	}

	/**
	 * �Ŵ�ƾ֤��ʾ
	 * 
	 * @return
	 */
	public static void ZoomIn() {
		// ���õ�ǰ��ʾ�ķ���
		execActivexMethod(ShowCompositeVoucherOcx.ZOOMIN, new String[] {});

	}

	/**
	 * ��ȡocx��ƾ֤ǩ���������
	 * 
	 * @return ǩ������ַ���
	 */
	public String getSignedVouchers() {
		// ���õ�ǰ��ʾ�ķ���
		Variant v = execActivexMethod(
				ShowCompositeVoucherOcx.GETSIGNEDVOUCHERS, null);
		// 0��ʾ�ɹ���-1��ʾʧ��
		if (null == v) {
			return null;
		} else {
			return v.getString();
		}
	}

	/**
	 * ��ȡ�������ģ�������ϡ��˻ء�����ǩ�¡����͡���ӡ�Ȳ���
	 * 
	 * @param certId
	 *            ֤��id
	 * @param rgCode
	 *            ����
	 * @param setYear
	 *            ���
	 * @param voucherType
	 *            ƾ֤����
	 * @param voucherNO
	 *            ��Ҫ������ƾ֤����
	 * @param opCode
	 *            �������� ������ϡ��˻ء�����ǩ�¡����͡���ӡ
	 * @return �Ա��ĵĲ�����Ϣ
	 */
	public String getOperateVouchers(String certId, String rgCode,
			long setYear, String voucherType, String voucherNO, String opCode) {
		// ִ��ocx�Բ������ĵ�ǩ��
		Variant v = execActivexMethod(
				ShowCompositeVoucherOcx.GETOPERATEVOUCHRS, new Object[] {
						certId, rgCode, new Long(setYear), voucherType,
						voucherNO, opCode });
		// 0��ʾ�ɹ���-1��ʾʧ��
		if (null == v) {
			return null;
		} else {
			return v.getString();
		}
	}

	/**
	 * ִ��ocx�е�����
	 * 
	 * @param methodName
	 * @param params
	 * @return
	 */
	protected static Variant execActivexMethod(String methodName,
			Object[] params) {
		// MessageDialog.openMessageDialog(null, "enter.............");
		int[] methodIDs = _auto.getIDsOfNames(new String[] { methodName });
		// MessageDialog.openMessageDialog(null,
		// "enter1............."+methodIDs.length);
		int activeXMethodID = methodIDs[0];
		// MessageDialog.openMessageDialog(null,
		// "enter2............."+activeXMethodID);
		Variant[] paramVariant = null;
		if (params == null) {
			paramVariant = new Variant[0];
		} else {
			paramVariant = new Variant[params.length];
		}
		// MessageDialog.openMessageDialog(null, "enter3.............");
		for (int i = 0, n = params.length; i <= n - 1; i++) {
			if (params[i] == null) {
				paramVariant[i] = null;
			} else if (params[i] instanceof String) {
				paramVariant[i] = new Variant((String) params[i]);
			} else if (params[i] instanceof Long) {
				Long l = (Long) params[i];
				paramVariant[i] = new Variant(l.longValue());
			} else if (params[i] instanceof Short) {
				Short l = (Short) params[i];
				paramVariant[i] = new Variant(l.shortValue());
			} else if (params[i] instanceof Integer) {
				Integer l = (Integer) params[i];
				paramVariant[i] = new Variant(l.intValue());
			}
		}
		Variant variant = _auto.invoke(activeXMethodID, paramVariant);// VT_144{999}
		return variant;
	}

	/**
	 * ��ӡ����ƾ֤
	 * 
	 * @param voucherNo
	 * @param pageNo
	 * @return
	 */
	public int printVoucher(String voucherNo, int pageNo) {
		voucherNo = voucherNo.trim();
		Variant v = ShowCompositeVoucherOcx.execActivexMethod(PRINTONEVOUCHER,
				new Object[] { voucherNo, new Long(joint) });
		return v.getInt();
	}

	/**
	 * ��ӡ����ƾ֤����̨��ӡ������ʾOCX��
	 * 
	 * @param voucherNo
	 * @param pageNo
	 * @return
	 */
	public static  int printVoucher(String certID, String admDivCode, int stYear,
			String vtCode, String voucherNo) {
		certID = "001";

		Variant v = ShowCompositeVoucherOcx.execActivexMethod(PRINTALLVOUCHER,
				new Object[] { certID, admDivCode, new Long(stYear), vtCode,
						new Long(0), voucherNo });
		return v.getInt();
	}
//	public static int printVoucherAll(String certID, String admDivCode, int stYear,
//			String vtCode, String voucherNo) {
//		certID = "001";
//		
//		Variant v = ShowCompositeVoucherOcx.execActivexMethod(PRINTALLVOUCHER,
//				new Object[] { certID, admDivCode, new Long(stYear), vtCode,
//				new Long(0), voucherNo });
//		return v.getInt();
//	}

	/**
	 * ��ʾ��һ��
	 * 
	 * @return
	 */
	public int pageUp() {
		Variant v = ShowCompositeVoucherOcx.execActivexMethod(PAGEUP,
				new Object[] {});
		int result = v.getInt();

		return result;
	}

	/**
	 * ��ʾ��һ��
	 * 
	 * @return
	 */
	public int pageDown() {
		Variant v = ShowCompositeVoucherOcx.execActivexMethod(PAGEDOWN,
				new Object[] {});

		int result = v.getInt();

		return v.getInt();
	}

	/**
	 * ��ӡ��ǰ
	 * 
	 * @author Administrator
	 */
	private class BtnPrintSelection extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				btnPrint.setEnabled(false);
				voucherDtoList = ((BankNameEnterBean) context.getModelHolder()
						.getModel()).getChecklist();
				// ��ʼ��Ϊ��һ��ƾ֤����Ϣ
				TvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
				int i = printVoucher(infoDto.getSvoucherno(), joint);
				btnPrint.setEnabled(true);
				if (i == 0) {
					MessageDialog.openMessageDialog(null, "ƾ֤���:  "
							+ infoDto.getSvoucherno() + " ��ӡ�ɹ�");
				} else {
					String err = GetLastErr();
					if (err != null && !err.equals("")) {
						MessageDialog.openMessageDialog(null, "ƾ֤���:  "
								+ infoDto.getSvoucherno() + " ��ӡ�����쳣 ��" + err);
					} else {
						MessageDialog.openMessageDialog(null, "ƾ֤���:  "
								+ infoDto.getSvoucherno() + " ��ӡʧ��");
					}
				}
				init(listCount);

			} catch (Exception ex) {
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}

	}
	/**
	 * ��ӡ����
	 * 
	 * @author Administrator
	 */
	private class Btn2PrintAll extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			selectDtoList = ((BankNameEnterBean) context.getModelHolder()
					.getModel()).getSelectlist();
			HashMap<String,String> admDivCodeMap=((BankNameEnterBean) context.getModelHolder()
					.getModel()).getAdmDivCodeMap();
			 ITFELoginInfo loginInfo =(ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
			 String biztype = null;
				if (selectDtoList == null || selectDtoList.size() == 0) {
					MessageDialog.openMessageDialog(null, "��ѡ��Ҫ��ӡ��ƾ֤ !!!");
				}
				
				HashMap<String, String> map = new HashMap<String, String>();
				String voucherNos;
				//5207
				if (selectDtoList.get(0) instanceof TvPayoutmsgmainDto) {
//					biztype=MsgConstant.VOUCHER_NO_5207;
					biztype=((BankNameEnterBean) context.getModelHolder().getModel()).getBiztype();//��������ʵ���ʽ�ר���������д������Ϊ5207
					for (int i = 0; i < selectDtoList.size(); i++) {
						TvPayoutmsgmainDto obj = (TvPayoutmsgmainDto) selectDtoList.get(i);
						 String admDivCode = admDivCodeMap.get(obj.getStrecode());
						 if (map.containsKey(biztype+","+admDivCode)) {
						    	map.put(biztype+","+admDivCode,map.get(biztype+","+admDivCode)+","+obj.getStaxticketno());
							}else{
								map.put(biztype+","+admDivCode,obj.getStaxticketno());
							}
					}
				}
				//5209
				if (selectDtoList.get(0) instanceof TvDwbkDto) {
					biztype=MsgConstant.VOUCHER_NO_5209;
					for (int i = 0; i < selectDtoList.size(); i++) {
						TvDwbkDto obj = (TvDwbkDto) selectDtoList.get(i);
						 String admDivCode = admDivCodeMap.get(obj.getSpayertrecode());
						if (map.containsKey(biztype+","+admDivCode)) {
							map.put(biztype+","+admDivCode,map.get(biztype+","+admDivCode)+","+obj.getSelecvouno());
						}else{
							map.put(biztype+","+admDivCode,obj.getSelecvouno());
						}
					}
				}
				//5201
				if (selectDtoList.get(0) instanceof TfDirectpaymsgmainDto) {
					biztype=MsgConstant.VOUCHER_NO_5201;
					for (int i = 0; i < selectDtoList.size(); i++) {
						TfDirectpaymsgmainDto obj = (TfDirectpaymsgmainDto) selectDtoList.get(i);
						 String admDivCode = admDivCodeMap.get(obj.getStrecode());
						if (map.containsKey(biztype+","+admDivCode)) {
							map.put(biztype+","+admDivCode,map.get(biztype+","+admDivCode)+","+obj.getSvoucherno());
						}else{
							map.put(biztype+","+admDivCode,obj.getSvoucherno());
						}
					}
				}
			System.out.println("**************���ô�ӡ���нӿ�*******************");
			if (map.size()>1) {
				String msg="ѡ���ӡ�ļ�¼�з�����ͬ�Ĺ��⣬ϵͳֻ�ܰ��չ���ֱ���д�ӡ,�Ƿ����?";
				boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
						null, "��ʾ", msg);
				if (!flag) {
					return;
				}
			}
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String arg[]=key.split(",");
				String value =map.get(key);
				int i = printVoucher("001", arg[1],Integer.valueOf(loginInfo.getCurrentDate().substring(0, 4)), arg[0], map.get(key));
				if (i!=0) {
					MessageDialog.openMessageDialog(null, key+" ƾ֤��ӡʧ��!!!");
					return;
				} 
			}
		    MessageDialog.openMessageDialog(null, " ƾ֤��ӡ�ɹ�!!!");
			
		}
	}
	/**
	 * ��ӡ����
	 * 
	 * @author Administrator
	 */
	private class BtnPrintAll extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				int j = 0;
				String voucherNo = "";
				voucherDtoList = ((BankNameEnterBean) context.getModelHolder()
						.getModel()).getSelectlist();
				List lists = getListByAdmdivcode(voucherDtoList);
				TvVoucherinfoDto vDto = new TvVoucherinfoDto();
				for (List list : (List<List>) lists) {
					for (TvVoucherinfoDto dto : (List<TvVoucherinfoDto>) list) {
						voucherNo = voucherNo + "," + dto.getSvoucherno();
						vDto = dto;
					}
					voucherNo = voucherNo.substring(1, voucherNo.length());
					btnPrintAll.setEnabled(false);
					// ��ӡ��Ϻ󣬵�ǰ��ťʧЧ����ֹ�ظ���ӡ
					int i = printVoucher(null, vDto.getSadmdivcode(), Integer
							.parseInt(vDto.getSstyear()), vDto.getSvtcode(),
							voucherNo);
					if (i == 0) {
						j++;
						if (lists.size() == j) {
							MessageDialog.openMessageDialog(null, "���⣺"
									+ vDto.getStrecode() + " ƾ֤�Ѿ�ȫ����ӡ��");

						} else {
							MessageDialog.openMessageDialog(null, "���⣺"
									+ vDto.getStrecode()
									+ " ƾ֤�Ѿ�ȫ����ӡ������ȷ��������ӡ��");
						}
					} else {
						String err = GetLastErr();
						if (err != null && !err.equals("")) {
							MessageDialog.openMessageDialog(null, "ƾ֤��ӡ�����쳣 ��"
									+ err);
						} else {
							MessageDialog.openMessageDialog(null, "ƾ֤��ӡʧ�ܣ�");
						}
					}
					btnPrintAll.setEnabled(true);
					voucherNo = "";
				}
				init(listCount);
			} catch (Exception ex) {
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}
	}

	/**
	 * ����
	 * 
	 * @author Administrator
	 */
	private class BtnReturn extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				IModelHolder holder = MVCUtils.openEditor("ҵ��ƾ֤����");
				MVCUtils.openComposite("ҵ��ƾ֤����", "ά������");
				((BankNameEnterBean) context.getModelHolder().getModel())
						.getChecklist().clear();
				((AbstractMetaDataEditorPart) ((BankNameEnterBean) context
						.getModelHolder().getModel()).getModelHolder())
						.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
				ls_AllCount = "";
				listCount = 0;
				nowCount.setText("" + (listCount + 1));
			} catch (Exception ex) {
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}

		}

	}

	/**
	 * ��һ��
	 * 
	 * @author wangyunbin
	 */
	private class BtnBeforeOne extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {

				if (listCount == 0) {
					MessageDialog.openMessageDialog(null, "��ǰ�����ǵ�һ����");
					return;
				}
				listCount--;
				init(listCount);
				nowCount.setText("" + (listCount + 1));
			} catch (Exception ex) {
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}

	}

	/**
	 * ��һ��
	 * 
	 * @author wangyunbin
	 */
	private class BtnNextOne extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {

				if (listCount == voucherDtoList.size() - 1) {
					MessageDialog.openMessageDialog(null, "��ǰ���������һ����");
					return;
				}
				listCount++;
				nowCount.setText("" + (listCount + 1));
				init(listCount);
			} catch (Exception ex) {
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}

	}

	/**
	 * ��һ��
	 * 
	 * @author wangyunbin
	 */
	private class BtnBeforeGroup extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			try {
				joint--;

				if (joint <= -1) {
					MessageDialog.openMessageDialog(null, "��ǰ�����ǵ�һ����");
					joint++;
					return;
				}

				nowPageCount.setText("" + (joint + 1));
				pageUp();
			} catch (Exception ex) {
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}

	}

	/**
	 * ��һ��
	 * 
	 * @author wangyunbin
	 */
	private class BtnNextGroup extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			try {

				joint++;

				if (joint >= (jointCount)) {
					MessageDialog.openMessageDialog(null, "��ǰ���������һ����");
					joint--;
					return;
				}

				nowPageCount.setText("" + (joint + 1));
				pageDown();
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}

	}

	public PagingTableComposite getPaging() {
		return paging;
	}

	public void setPaging(PagingTableComposite paging) {
		this.paging = paging;
	}

	public CheckboxTableViewer getCheckBizTableView() {
		return checkBizTableView;
	}

	public CheckboxTableViewer getCheckBizTableViewDwbk() {
		return checkBizTableViewDwbk;
	}

	public void setCheckBizTableViewDwbk(CheckboxTableViewer checkBizTableViewDwbk) {
		this.checkBizTableViewDwbk = checkBizTableViewDwbk;
	}

	public void setCheckBizTableView(CheckboxTableViewer checkBizTableView) {
		this.checkBizTableView = checkBizTableView;
	}

	public static DataBindingContext getContext() {
		return context;
	}

	public static void setContext(DataBindingContext context) {
		ShowCompositeVoucherOcx.context = context;
	}

	/**
	 * ���ַ�������base64����
	 * 
	 * @param src�ַ���
	 * @return base64�ַ���
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Encode(String src)
			throws UnsupportedEncodingException {
		byte[] outByte = encode(src.getBytes());
		String ret = null;
		ret = new String(outByte, "gb2312");
		return ret;
	}

	/**
	 * �����
	 */
	private static final byte[] ENCODINGTABLE = { (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
			(byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',
			(byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q',
			(byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V',
			(byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a',
			(byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
			(byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k',
			(byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p',
			(byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
			(byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
			(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
			(byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
			(byte) '+', (byte) '/' };

	/**
	 * Base64����
	 */
	public static byte[] encode(byte[] data) {
		byte[] bytes;

		int modulus = data.length % 3;
		if (modulus == 0) {
			bytes = new byte[4 * data.length / 3];
		} else {
			bytes = new byte[4 * ((data.length / 3) + 1)];
		}

		int dataLength = (data.length - modulus);
		int a1, a2, a3;
		for (int i = 0, j = 0; i < dataLength; i += 3, j += 4) {
			a1 = data[i] & 0xff;
			a2 = data[i + 1] & 0xff;
			a3 = data[i + 2] & 0xff;

			bytes[j] = ENCODINGTABLE[(a1 >>> 2) & 0x3f];
			bytes[j + 1] = ENCODINGTABLE[((a1 << 4) | (a2 >>> 4)) & 0x3f];
			bytes[j + 2] = ENCODINGTABLE[((a2 << 2) | (a3 >>> 6)) & 0x3f];
			bytes[j + 3] = ENCODINGTABLE[a3 & 0x3f];
		}

		/*
		 * process the tail end.
		 */
		int b1, b2, b3;
		int d1, d2;

		switch (modulus) {
		case 0: /* nothing left to do */
			break;
		case 1:
			d1 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = (d1 << 4) & 0x3f;

			bytes[bytes.length - 4] = ENCODINGTABLE[b1];
			bytes[bytes.length - 3] = ENCODINGTABLE[b2];
			bytes[bytes.length - 2] = (byte) '=';
			bytes[bytes.length - 1] = (byte) '=';
			break;
		case 2:
			d1 = data[data.length - 2] & 0xff;
			d2 = data[data.length - 1] & 0xff;

			b1 = (d1 >>> 2) & 0x3f;
			b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
			b3 = (d2 << 2) & 0x3f;

			bytes[bytes.length - 4] = ENCODINGTABLE[b1];
			bytes[bytes.length - 3] = ENCODINGTABLE[b2];
			bytes[bytes.length - 2] = ENCODINGTABLE[b3];
			bytes[bytes.length - 1] = (byte) '=';
			break;
		}

		return bytes;
	}

	/*
	 * �����
	 */
	private static final byte[] DECODINGTABLE;

	static {
		DECODINGTABLE = new byte[128];
		for (int i = 'A'; i <= 'Z'; i++) {
			DECODINGTABLE[i] = (byte) (i - 'A');
		}

		for (int i = 'a'; i <= 'z'; i++) {
			DECODINGTABLE[i] = (byte) (i - 'a' + 26);
		}

		for (int i = '0'; i <= '9'; i++) {
			DECODINGTABLE[i] = (byte) (i - '0' + 52);
		}

		DECODINGTABLE['+'] = 62;
		DECODINGTABLE['/'] = 63;
	}

	/**
	 * Base64����
	 */
	public static byte[] decode(byte[] data) {
		byte[] bytes;
		byte b1, b2, b3, b4;

		if (data[data.length - 2] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 1];
		} else if (data[data.length - 1] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 2];
		} else {
			bytes = new byte[((data.length / 4) * 3)];
		}

		for (int i = 0, j = 0; i < data.length - 4; i += 4, j += 3) {
			b1 = DECODINGTABLE[data[i]];
			b2 = DECODINGTABLE[data[i + 1]];
			b3 = DECODINGTABLE[data[i + 2]];
			b4 = DECODINGTABLE[data[i + 3]];

			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte) ((b3 << 6) | b4);
		}

		if (data[data.length - 2] == '=') {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];

			bytes[bytes.length - 1] = (byte) ((b1 << 2) | (b2 >> 4));
		} else if (data[data.length - 1] == '=') {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			b3 = DECODINGTABLE[data[data.length - 2]];

			bytes[bytes.length - 2] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte) ((b2 << 4) | (b3 >> 2));
		} else {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			b3 = DECODINGTABLE[data[data.length - 2]];
			b4 = DECODINGTABLE[data[data.length - 1]];

			bytes[bytes.length - 3] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte) ((b3 << 6) | b4);
		}

		return bytes;
	}

	public static void sortStringArray(String[] arrStr) {
		String temp;
		int a;
		int b;
		for (int i = 0; i < arrStr.length; i++) {
			for (int j = arrStr.length - 1; j > i; j--) {

				a = Integer.parseInt(arrStr[i]);
				b = Integer.parseInt(arrStr[j]);
				if (a > b) {
					temp = arrStr[i];
					arrStr[i] = arrStr[j];
					arrStr[j] = temp;
				}
			}
		}
	}

	/**
	 * ����
	 */
	public static byte[] decode(String data) {
		byte[] bytes;
		byte b1, b2, b3, b4;

		if (data.charAt(data.length() - 2) == '=') {
			bytes = new byte[(((data.length() / 4) - 1) * 3) + 1];
		} else if (data.charAt(data.length() - 1) == '=') {
			bytes = new byte[(((data.length() / 4) - 1) * 3) + 2];
		} else {
			bytes = new byte[((data.length() / 4) * 3)];
		}

		for (int i = 0, j = 0; i < data.length() - 4; i += 4, j += 3) {
			b1 = DECODINGTABLE[data.charAt(i)];
			b2 = DECODINGTABLE[data.charAt(i + 1)];
			b3 = DECODINGTABLE[data.charAt(i + 2)];
			b4 = DECODINGTABLE[data.charAt(i + 3)];

			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte) ((b3 << 6) | b4);
		}

		if (data.charAt(data.length() - 2) == '=') {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];

			bytes[bytes.length - 1] = (byte) ((b1 << 2) | (b2 >> 4));
		} else if (data.charAt(data.length() - 1) == '=') {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			b3 = DECODINGTABLE[data.charAt(data.length() - 2)];

			bytes[bytes.length - 2] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte) ((b2 << 4) | (b3 >> 2));
		} else {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			b3 = DECODINGTABLE[data.charAt(data.length() - 2)];
			b4 = DECODINGTABLE[data.charAt(data.length() - 1)];

			bytes[bytes.length - 3] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte) ((b3 << 6) | b4);
		}

		return bytes;
	}

	public Composite getComposite_table() {
		return composite_table;
	}

	public void setComposite_table(Composite composite_table) {
		this.composite_table = composite_table;
	}

	public Composite getComposite_dwbk() {
		return composite_dwbk;
	}

	public void setComposite_dwbk(Composite composite_dwbk) {
		this.composite_dwbk = composite_dwbk;
	}

	public static Composite getComposite_direct() {
		return composite_direct;
	}

	public static void setComposite_direct(Composite composite_direct) {
		ShowCompositeVoucherOcx.composite_direct = composite_direct;
	}

	public CheckboxTableViewer getCheckBizTableViewDirect() {
		return checkBizTableViewDirect;
	}

	public void setCheckBizTableViewDirect(
			CheckboxTableViewer checkBizTableViewDirect) {
		this.checkBizTableViewDirect = checkBizTableViewDirect;
	}

//	public static void setStampTypeComposite(Combo stampTypeComposite) {
//		ShowCompositeVoucherOcx.stampTypeComposite = stampTypeComposite;
//	}
//
//	public static Combo getStampTypeComposite() {
//		return stampTypeComposite;
//	}

}