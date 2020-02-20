
package com.cfcc.itfe.client.sendbiz.bizcertsend;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.report.ReportComposite;
import com.cfcc.itfe.client.dataquery.voucherhissearch.VoucherHisSearchBean;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.mvc.IModelHolder;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.jasperassistant.designer.viewer.util.JasperConstants;


public class ActiveXCompositeVoucherHisOcx extends Composite {
	
	private static Log logger=LogFactory.getLog(ActiveXCompositeVoucherHisOcx.class);
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
	private String voucherno="";
	
	// ��һ����ť
	Button buttonBefore;
	// ��һ����ť
	Button buttonNext;
	// ��һ����ť
	Button buttonBeforeGroup;
	// ��һ����ť
	Button buttonNextGroup;
	// ��ӡ��ǰ��ť
	Button btnPrint;
	// ��ӡ���а�ť
	Button btnPrintAll;
	// ���ذ�ť
	Button btnReturn;

	// ��ӡ���а�ť
	Button btnPrintAllNew;

	private int listCount = 0;
	private static int jointCount = 0;
	
	// ����ļ�¼�б�
	static List<HtvVoucherinfoDto> voucherDtoList = new ArrayList<HtvVoucherinfoDto>();

	// ������ʾ����
	ReportComposite report;
	private ITFELoginInfo loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
	public static List getListByAdmdivcode(List<HtvVoucherinfoDto> list){
		Map map=new HashMap();
		for(HtvVoucherinfoDto dto:list){
			map.put(dto.getSadmdivcode(), "");
		}
		if(map.size()==0){
			return null;
		}
		List<List> lists=new ArrayList<List>();
		try{
			
			for(Iterator<String> it=map.keySet().iterator();it.hasNext();){
				String key=(String)it.next();
				List<HtvVoucherinfoDto> newList=new ArrayList<HtvVoucherinfoDto>();
				for(HtvVoucherinfoDto dto:list){
					String admdivcode=dto.getSadmdivcode();
					
					if(admdivcode.equals(key)){
						
						newList.add(dto);
					}
				}lists.add(newList);
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return lists;
	}
	
	public static String Refresh(String voucherno) {
		Variant v = execActivexMethod(OcxConstant.Refresh,
				new Object[] {voucherno});
		return v.getString();
	}

	/**
	 * ƾ֤��ʾ
	 * 
	 * @param num
	 */
	public static void init(int num) {
		voucherDtoList = ((VoucherHisSearchBean) context.getModelHolder().getModel())
				.getCheckList();
		ls_AllCount = voucherDtoList.size() + "";
		
		allCount.setText(ls_AllCount);
		// ��ʼ��Ϊ��һ��ƾ֤����Ϣ
		HtvVoucherinfoDto infoDto = voucherDtoList.get(num);
		Refresh (infoDto.getSvoucherno());
		int count=((VoucherHisSearchBean) context.getModelHolder().getModel()).queryVoucherJOintCount(infoDto);
		if(count==-1){
			nowPageCount.setText("����δά��");
		}else if(count==-2){
			nowPageCount.setText("��ѯ�쳣");
		}else{
			jointCount=count;
			nowPageCount.setText(""+1);
		}
		
		int i = -1;
		String err = null;
		i = initStampOcx("0", infoDto.getSadmdivcode(), Integer
				.parseInt(infoDto.getSstyear()), infoDto.getSvtcode().trim(),
				"0", 2, 0, 0);
		if (i == 0) {
			i = addVoucher(infoDto.getSvoucherno());
			if (i == 0) {
				i = setCurrentVoucher(infoDto.getSvoucherno());
			
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

	/**
	 * ƾ֤��ӡ
	 * 
	 * @param num
	 */
	public static void print() {
		// SelectionEvent e=new SelectionEvent("");
		// new BtnPrintAll().widgetSelected(e);
		// SelectionEvent{Button {��ӡ����} time=1707228 data=null item=null
		// detail=0 x=0 y=0 width=0 height=0 stateMask=0 text=null doit=true}
	}

	/**
	 * @param parent
	 */
	public ActiveXCompositeVoucherHisOcx(Composite parent) {

		super(parent, SWT.NONE);
		setLayout(new FormLayout());
		try {
			final Composite composite = new Composite(this, SWT.NONE);

			// ������ʾ����ƾ֤������
			createStampContents(composite);
			this.layout(true);
		} catch (Throwable e) {

		}

	}

	/**
	 * ����AcitveX�ؼ���ʾ����ƾ֤������
	 */
	protected void createStampContents(Composite composite) {

		// �ܼ�¼��
		final Label slabel = new Label(composite, SWT.NONE);
		slabel.setText("�ܼ�¼����");
		slabel.setBounds(6, 10, 84, 20);

		allCount = new Text(composite, SWT.BORDER);
		allCount.setEnabled(false);
		allCount.setBounds(90, 7, 80, 22);

		// ��ǰ��¼
		final Label dlabel = new Label(composite, SWT.NONE);
		dlabel.setText("��ǰ��¼��");
		dlabel.setBounds(450, 10, 59, 20);
	
		nowCount = new Text(composite, SWT.BORDER);
		nowCount.setBounds(520, 7, 80, 21);
		nowCount.setEnabled(false);
		nowCount.setText("" + (listCount + 1));

		// ��ӡ����
		final Label labelpage = new Label(composite, SWT.NONE);
		labelpage.setText("��ǰ������");
		labelpage.setBounds(250, 10, 59, 20);
		labelpage.setVisible(true);

		nowPageCount = new Text(composite, SWT.BORDER);
		nowPageCount.setBounds(320, 7, 80, 21);
		nowPageCount.setEnabled(false);
		nowPageCount.setText("" + (joint + 1));
		
		// ƾ֤�б�ť
		buttonBefore = new Button(composite, SWT.NONE);
		buttonBefore.setBounds(30, 35, 80, 21);
		buttonBefore.setText("��һ��");
		buttonBefore.setEnabled(true);
		buttonBefore.addSelectionListener(new BtnBeforeOne());

		buttonBeforeGroup = new Button(composite, SWT.NONE);
		buttonBeforeGroup.setBounds(130, 35, 80, 21);
		buttonBeforeGroup.setText("��һ��");
		buttonBeforeGroup.setEnabled(true);
		buttonBeforeGroup.addSelectionListener(new BtnBeforeGroup());
		buttonBeforeGroup.setVisible(true);

		buttonNextGroup = new Button(composite, SWT.NONE);
		buttonNextGroup.setBounds(225, 35, 80, 21);
		buttonNextGroup.setText("��һ��");
		buttonNextGroup.setEnabled(true);
		buttonNextGroup.addSelectionListener(new BtnNextGroup());
		buttonNextGroup.setVisible(true);

		buttonNext = new Button(composite, SWT.NONE);
		buttonNext.setBounds(325, 35, 80, 21);
		buttonNext.setText("��һ��");
		buttonNext.setEnabled(true);
		buttonNext.addSelectionListener(new BtnNextOne());

		btnPrint = new Button(composite, SWT.NONE);
		btnPrint.setBounds(425, 35, 80, 21);
		btnPrint.setText("��ӡ��ǰ");
		btnPrint.setEnabled(true);
		
		btnPrint.addSelectionListener(new BtnPrintSelection());
		btnPrintAll = new Button(composite, SWT.NONE);
		btnPrintAll.setBounds(523, 35, 80, 21);
		btnPrintAll.setText("��ӡ����");
		btnPrintAll.setEnabled(true);
		btnPrintAll.addSelectionListener(new BtnPrintAll());
		

		btnReturn = new Button(composite, SWT.NONE);
		btnReturn.setBounds(920, 35, 80, 21);
		btnReturn.setText("����");
		btnReturn.setEnabled(true);
		btnReturn.addSelectionListener(new BtnReturn());

		context = DataBindingContext
				.createInstance("33a666d8-c199-4fe5-a785-c012e3413caf");

		// this.shell = new Shell();
		_frame = new OleFrame(composite, SWT.BORDER);
		_site = new OleControlSite(_frame, SWT.NONE,
				"ESTAMPOCX.EstampOcxCtrl.1");
		_auto = new OleAutomation(_site);
		_frame.setBounds(6, 60, 1300, 680);
		// ����Ҫ������Ƕ�ؼ��Ĵ�С����Ϊ�����Ѿ������˸��ؼ��Ĵ�С����Ƕ�ؼ��ᰴ�ո��ؼ��Ĵ�С��ʾ
		// _site.doVerb(OLE.OLEIVERB_PRIMARY);
		_site.doVerb(OLE.OLEIVERB_UIACTIVATE);
		_site.doVerb(OLE.OLEIVERB_SHOW);
		_frame.setVisible(true);

		// ������ʾ����
		ReportComposite report = new ReportComposite(composite,
				JasperConstants.ALL);
		report.setBounds(6, 60, 1300, 680);
		// ���ñ�����ɫ����ActiveX�ؼ�һ��
		report.setBackground(new Color(composite.getDisplay(), new RGB(192,
				192, 192)));
		report.setVisible(true);
		/**�޸�Ϊ�ɹ������������(itfeesb_linux.properties�ļ��е�ocxservice.isClientProxy����)�����Ƿ�ʹ�ô���ʽ���������ʵ�URL��ַ 20141001**/
		String ls_URL = ClientOcxServiceProxyUtils.getOcxVoucherServerURL();
		String stampurl = ClientOcxServiceProxyUtils.getOCXStampServerURL();
		if(!loginInfo.getPublicparam().contains(",notinitocx,"))
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
					logger.error("��ʼ������ƾ֤������ַʧ��!");
				}
				Variant v2 = execActivexMethod(OcxConstant.SETEstampURL,
						new Object[] { stampUrl + (loginInfo.getPublicparam().contains(",ocxnewinterface=true,")?OcxConstant.ESTAMPSERVICEURL.replace("realware", "estamp"):OcxConstant.ESTAMPSERVICEURL) });
				if(v2.getInt()!=0){
					logger.error("��ʼ������ǩ�·����ַʧ��!");
				}
			}
		}.start();
	}

	public void KoalStampHandler(OleControlSite controlSite) {
		this.controlSite = controlSite;
		this.automation = new OleAutomation(controlSite);
	}


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

	/*
	 * public AsspOcx ( Shell shell ){
	 * 
	 * this.shell = shell; _frame = new OleFrame(shell, SWT.BORDER); _site = new
	 * OleControlSite(_frame, SWT.NONE,
	 * "{1744C1CD-A4B0-4449-A139-7FB4917EC709}"); _auto = new
	 * OleAutomation(_site);this.setUrl(
	 * "http://192.168.1.24:8089/realware/signer/result!getMethod.action"); }
	 *//**
	 * ��ֹ�ⲿ����
	 */
	/*
	 * public AsspOcx(){ this( new Shell()); }
	 */

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
	public static int initStampOcx(String certId, String rgCode, long year,
			String voucherType, String stepCode, long operateType,
			long defaultPage, long displayMode) {

		// ��ʼ��Ĭ����ʾ��
		joint = (int) defaultPage;

		// ����ocx�ĳ�ʼ������
		Variant v = execActivexMethod(OcxConstant.INITOCX,
				new Object[] { certId, rgCode, new Long(year), voucherType,
						stepCode, new Long(operateType), new Long(defaultPage),
						new Long(displayMode) });

		return v.getInt();

	}

	/**
	 *��ȡocx���ù����쳣��Ϣ
	 * 
	 */
	public static String GetLastErr() {
		Variant v = execActivexMethod(OcxConstant.GetLastErr,
				new Object[] {});
		return v.getString();
	}

	public void setUrl(String url) {

		// ����ocx�ĳ�ʼ������
		execActivexMethod(OcxConstant.SETURL,
				new Object[] { url });
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
		Variant v = execActivexMethod(OcxConstant.ADDVOUCHER,
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
				OcxConstant.ADDVOUCHERFROMSERVER,
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
		Variant v = execActivexMethod(OcxConstant.CLEARVOUCHER,
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
	public static void ZoomToFit() {
		// ���õ�ǰ��ʾ�ķ���
		Variant v = execActivexMethod(
				OcxConstant.ZoomToFit,
				new String[] { });
		
		return ;
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
				OcxConstant.SETCURRENTVOUCHER,
				new String[] { voucherNO });
		// 0��ʾ�ɹ���-1��ʾʧ��
		return v.getInt();
	}

	/**
	 * ��ȡocx��ƾ֤ǩ���������
	 * 
	 * @return ǩ������ַ���
	 */
	public String getSignedVouchers() {
		// ���õ�ǰ��ʾ�ķ���
		Variant v = execActivexMethod(
				OcxConstant.GETSIGNEDVOUCHERS, null);
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
				OcxConstant.GETOPERATEVOUCHRS, new Object[] {
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
		Variant v = ActiveXCompositeVoucherHisOcx.execActivexMethod(
				OcxConstant.PRINTONEVOUCHER, new Object[] { voucherNo, new Long(joint) });
		return v.getInt();
	}

	/**
	 * ��ӡ����ƾ֤����̨��ӡ������ʾOCX��
	 * 
	 * @param voucherNo
	 * @param pageNo
	 * @return
	 */
	public int printVoucher(String certID, String admDivCode, int stYear,
			String vtCode, String voucherNo) {
		certID = "001";
		
		Variant v = ActiveXCompositeVoucherHisOcx.execActivexMethod(
				OcxConstant.PRINTALLVOUCHER, new Object[] { certID, admDivCode,
						new Long(stYear), vtCode, new Long(joint), voucherNo });
		return v.getInt();
	}

	/**
	 * ��ʾ��һ��
	 * 
	 * @return
	 */
	public int pageUp() {
		Variant v = ActiveXCompositeVoucherHisOcx.execActivexMethod(OcxConstant.PAGEUP,
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
		Variant v = ActiveXCompositeVoucherHisOcx.execActivexMethod(OcxConstant.PAGEDOWN,
				new Object[] {});

		int result = v.getInt();

		return v.getInt();
	}
	
	/**
	 * ��ӡ��ǰ
	 * 
	 */
	private class BtnPrintSelection extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {											
				voucherDtoList = ((VoucherHisSearchBean) context.getModelHolder()
						.getModel()).getCheckList();
				// ��ʼ��Ϊ��һ��ƾ֤����Ϣ
				HtvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
				btnPrint.setEnabled(false);
				int i = printVoucher(infoDto.getSvoucherno(), joint);
				btnPrint.setEnabled(true);				
				if (i == 0) {
					MessageDialog.openMessageDialog(null, "ƾ֤���:  "
							+ infoDto.getSvoucherno() + " ��ӡ�ɹ�");
				} else {
					String err = GetLastErr();
					if (err != null && !err.equals("")) {
						MessageDialog.openMessageDialog(null, "ƾ֤���:  "
								+ infoDto.getSvoucherno() + " ƾ֤��ӡʧ�� ��" + err);
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
	private class BtnPrintAll extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				int j=0;
				String voucherNo = "";
				voucherDtoList = ((VoucherHisSearchBean) context.getModelHolder()
						.getModel()).getCheckList();
				List lists=getListByAdmdivcode(voucherDtoList);
				HtvVoucherinfoDto vDto=new HtvVoucherinfoDto();
				for(List list:(List<List>)lists){
					for(HtvVoucherinfoDto dto:(List<HtvVoucherinfoDto>)list){
						voucherNo = voucherNo + ","+ dto.getSvoucherno();
						vDto=dto;
					}
					voucherNo = voucherNo.substring(1, voucherNo.length());
					btnPrintAll.setEnabled(false);
					// ��ӡ��Ϻ󣬵�ǰ��ťʧЧ����ֹ�ظ���ӡ
					int i = printVoucher(null, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(),
							voucherNo);
					if (i == 0) {
						j++;
						if(lists.size()==j){
							MessageDialog.openMessageDialog(null, "���⣺"+vDto.getStrecode()+" ƾ֤�Ѿ�ȫ����ӡ��");						
						}else{
							MessageDialog.openMessageDialog(null, "���⣺"+vDto.getStrecode()+" ƾ֤�Ѿ�ȫ����ӡ������ȷ��������ӡ��");
						}
					} else {
						String err = GetLastErr();
						if (err != null && !err.equals("")) {
							MessageDialog.openMessageDialog(null, "ƾ֤��ӡʧ�ܣ�"
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
				IModelHolder holder = MVCUtils.openEditor("����ƾ֤��ʷ��ѯ");
				MVCUtils.openComposite("����ƾ֤��ʷ��ѯ", "ά������");
				((VoucherHisSearchBean) context.getModelHolder().getModel())
						.getCheckList().clear();
				((AbstractMetaDataEditorPart) ((VoucherHisSearchBean) context
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
				/*
				 * HtvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
				 * execActivexMethod("ResetContent", new Object[]{} );
				 * execActivexMethod("RemoveVoucherByNo", new
				 * Object[]{infoDto.getSvoucherno().trim()} ); initStampOcx("0",
				 * infoDto.getSadmdivcode().substring(0,6),
				 * Integer.parseInt(infoDto.getSstyear()),
				 * infoDto.getSvtcode().trim(), "l_mof_130000", 1, 0, 0);
				 * addVoucher(infoDto.getSvoucherno().trim());
				 * setCurrentVoucher(
				 * voucherDtoList.get(listCount).getSvoucherno());
				 */
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
				// listCount ++;
				/*
				 * HtvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
				 * Variant v = execActivexMethod("ResetContent", new Object[]{}
				 * ); MessageDialog.openMessageDialog(null,
				 * "why111............."+v.getInt()); Variant r =
				 * execActivexMethod("RemoveVoucherByNo", new
				 * Object[]{infoDto.getSvoucherno().trim()} );
				 * MessageDialog.openMessageDialog(null,
				 * "why2222............."+r.getInt()); initStampOcx("0",
				 * infoDto.getSadmdivcode().substring(0,6),
				 * Integer.parseInt(infoDto.getSstyear()),
				 * infoDto.getSvtcode().trim(), "l_mof_130000", 1, 0, 0);
				 * MessageDialog.openMessageDialog(null,
				 * "tttttttt............."+v.getInt());
				 * addVoucher(voucherDtoList.get(listCount).getSvoucherno());
				 * setCurrentVoucher
				 * (voucherDtoList.get(listCount).getSvoucherno());
				 */
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
	
}