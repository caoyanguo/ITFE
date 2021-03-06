
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
	// ole控件对象
	protected OleControlSite controlSite;
	// ole对象方法调用代理
	protected OleAutomation automation;
	// JAF数据绑定对象
	private static DataBindingContext context;
	
	// 总记录数
	private static Text allCount;

	public static String ls_AllCount = "";
	// 当前记录
	private Text nowCount;
	// 当前联数
	private static Text nowPageCount;
	private String voucherno="";
	
	// 上一条按钮
	Button buttonBefore;
	// 下一条按钮
	Button buttonNext;
	// 上一联按钮
	Button buttonBeforeGroup;
	// 下一联按钮
	Button buttonNextGroup;
	// 打印当前按钮
	Button btnPrint;
	// 打印所有按钮
	Button btnPrintAll;
	// 返回按钮
	Button btnReturn;

	// 打印所有按钮
	Button btnPrintAllNew;

	private int listCount = 0;
	private static int jointCount = 0;
	
	// 传入的记录列表
	static List<HtvVoucherinfoDto> voucherDtoList = new ArrayList<HtvVoucherinfoDto>();

	// 报表显示区域
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
	 * 凭证显示
	 * 
	 * @param num
	 */
	public static void init(int num) {
		voucherDtoList = ((VoucherHisSearchBean) context.getModelHolder().getModel())
				.getCheckList();
		ls_AllCount = voucherDtoList.size() + "";
		
		allCount.setText(ls_AllCount);
		// 初始化为第一条凭证的信息
		HtvVoucherinfoDto infoDto = voucherDtoList.get(num);
		Refresh (infoDto.getSvoucherno());
		int count=((VoucherHisSearchBean) context.getModelHolder().getModel()).queryVoucherJOintCount(infoDto);
		if(count==-1){
			nowPageCount.setText("联数未维护");
		}else if(count==-2){
			nowPageCount.setText("查询异常");
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
								.openMessageDialog(null, "ocx调用异常： " + err);
					}
				}
			} else {
				err = GetLastErr();
				if (err != null) {
					MessageDialog.openMessageDialog(null, "ocx调用异常： " + err);
				}
			}
		} else {
			err = GetLastErr();
			if (err != null) {
				MessageDialog.openMessageDialog(null, "ocx调用异常： " + err);
			}
		}

	}

	/**
	 * 凭证打印
	 * 
	 * @param num
	 */
	public static void print() {
		// SelectionEvent e=new SelectionEvent("");
		// new BtnPrintAll().widgetSelected(e);
		// SelectionEvent{Button {打印所有} time=1707228 data=null item=null
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

			// 创建显示电子凭证的区域
			createStampContents(composite);
			this.layout(true);
		} catch (Throwable e) {

		}

	}

	/**
	 * 调用AcitveX控件显示电子凭证的区域
	 */
	protected void createStampContents(Composite composite) {

		// 总记录数
		final Label slabel = new Label(composite, SWT.NONE);
		slabel.setText("总记录数：");
		slabel.setBounds(6, 10, 84, 20);

		allCount = new Text(composite, SWT.BORDER);
		allCount.setEnabled(false);
		allCount.setBounds(90, 7, 80, 22);

		// 当前记录
		final Label dlabel = new Label(composite, SWT.NONE);
		dlabel.setText("当前记录：");
		dlabel.setBounds(450, 10, 59, 20);
	
		nowCount = new Text(composite, SWT.BORDER);
		nowCount.setBounds(520, 7, 80, 21);
		nowCount.setEnabled(false);
		nowCount.setText("" + (listCount + 1));

		// 打印联数
		final Label labelpage = new Label(composite, SWT.NONE);
		labelpage.setText("当前联数：");
		labelpage.setBounds(250, 10, 59, 20);
		labelpage.setVisible(true);

		nowPageCount = new Text(composite, SWT.BORDER);
		nowPageCount.setBounds(320, 7, 80, 21);
		nowPageCount.setEnabled(false);
		nowPageCount.setText("" + (joint + 1));
		
		// 凭证列表按钮
		buttonBefore = new Button(composite, SWT.NONE);
		buttonBefore.setBounds(30, 35, 80, 21);
		buttonBefore.setText("上一条");
		buttonBefore.setEnabled(true);
		buttonBefore.addSelectionListener(new BtnBeforeOne());

		buttonBeforeGroup = new Button(composite, SWT.NONE);
		buttonBeforeGroup.setBounds(130, 35, 80, 21);
		buttonBeforeGroup.setText("上一联");
		buttonBeforeGroup.setEnabled(true);
		buttonBeforeGroup.addSelectionListener(new BtnBeforeGroup());
		buttonBeforeGroup.setVisible(true);

		buttonNextGroup = new Button(composite, SWT.NONE);
		buttonNextGroup.setBounds(225, 35, 80, 21);
		buttonNextGroup.setText("下一联");
		buttonNextGroup.setEnabled(true);
		buttonNextGroup.addSelectionListener(new BtnNextGroup());
		buttonNextGroup.setVisible(true);

		buttonNext = new Button(composite, SWT.NONE);
		buttonNext.setBounds(325, 35, 80, 21);
		buttonNext.setText("下一条");
		buttonNext.setEnabled(true);
		buttonNext.addSelectionListener(new BtnNextOne());

		btnPrint = new Button(composite, SWT.NONE);
		btnPrint.setBounds(425, 35, 80, 21);
		btnPrint.setText("打印当前");
		btnPrint.setEnabled(true);
		
		btnPrint.addSelectionListener(new BtnPrintSelection());
		btnPrintAll = new Button(composite, SWT.NONE);
		btnPrintAll.setBounds(523, 35, 80, 21);
		btnPrintAll.setText("打印所有");
		btnPrintAll.setEnabled(true);
		btnPrintAll.addSelectionListener(new BtnPrintAll());
		

		btnReturn = new Button(composite, SWT.NONE);
		btnReturn.setBounds(920, 35, 80, 21);
		btnReturn.setText("返回");
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
		// 不需要设置内嵌控件的大小，因为上面已经设置了父控件的大小，内嵌控件会按照父控件的大小显示
		// _site.doVerb(OLE.OLEIVERB_PRIMARY);
		_site.doVerb(OLE.OLEIVERB_UIACTIVATE);
		_site.doVerb(OLE.OLEIVERB_SHOW);
		_frame.setVisible(true);

		// 报表显示区域
		ReportComposite report = new ReportComposite(composite,
				JasperConstants.ALL);
		report.setBounds(6, 60, 1300, 680);
		// 设置背景颜色，和ActiveX控件一致
		report.setBackground(new Color(composite.getDisplay(), new RGB(192,
				192, 192)));
		report.setVisible(true);
		/**修改为由工具类根据配置(itfeesb_linux.properties文件中的ocxservice.isClientProxy参数)决定是否使用代理方式来产生访问的URL地址 20141001**/
		String ls_URL = ClientOcxServiceProxyUtils.getOcxVoucherServerURL();
		String stampurl = ClientOcxServiceProxyUtils.getOCXStampServerURL();
		if(!loginInfo.getPublicparam().contains(",notinitocx,"))
			asynsetUrl(ls_URL, stampurl);
	}
	
	//新线程初始化凭证库
	public void asynsetUrl(final String voucherUrl, final String stampUrl) {
		// 新定义线程来处理发送
		new Thread() {
			public void run() {
				// 调用ocx的初始化方法
				Variant v1 = execActivexMethod(OcxConstant.SETURL,
						new Object[] { voucherUrl + OcxConstant.EVOUCHERSERVICEURL });
				if(v1.getInt()!=0){
					logger.error("初始化电子凭证库服务地址失败!");
				}
				Variant v2 = execActivexMethod(OcxConstant.SETEstampURL,
						new Object[] { stampUrl + (loginInfo.getPublicparam().contains(",ocxnewinterface=true,")?OcxConstant.ESTAMPSERVICEURL.replace("realware", "estamp"):OcxConstant.ESTAMPSERVICEURL) });
				if(v2.getInt()!=0){
					logger.error("初始化电子签章服务地址失败!");
				}
			}
		}.start();
	}

	public void KoalStampHandler(OleControlSite controlSite) {
		this.controlSite = controlSite;
		this.automation = new OleAutomation(controlSite);
	}


	/**
	 * 盛放ocx的最外层容器
	 */
	private OleFrame _frame;

	/**
	 * 存放ocx的空间 最后会放入oleframe 中去
	 */
	private OleControlSite _site;

	/**
	 * 用于执行方法
	 */
	private static OleAutomation _auto;

	public Shell shell = null;

	private static int joint = 0; // 默认显示联

	/*
	 * public AsspOcx ( Shell shell ){
	 * 
	 * this.shell = shell; _frame = new OleFrame(shell, SWT.BORDER); _site = new
	 * OleControlSite(_frame, SWT.NONE,
	 * "{1744C1CD-A4B0-4449-A139-7FB4917EC709}"); _auto = new
	 * OleAutomation(_site);this.setUrl(
	 * "http://192.168.1.24:8089/realware/signer/result!getMethod.action"); }
	 *//**
	 * 防止外部调用
	 */
	/*
	 * public AsspOcx(){ this( new Shell()); }
	 */

	/**
	 * 初始化ocx控件
	 * 
	 * @param certId
	 *            证书id
	 * @param year
	 *            业务年度
	 * @param voucherType
	 *            凭证类型
	 * @param stepCode
	 *            第几步
	 * @param operateType
	 *            操作类型
	 * @param defaultPage
	 *            默认业
	 * @param displayMode
	 *            显示模式
	 */
	public static int initStampOcx(String certId, String rgCode, long year,
			String voucherType, String stepCode, long operateType,
			long defaultPage, long displayMode) {

		// 初始化默认显示联
		joint = (int) defaultPage;

		// 调用ocx的初始化方法
		Variant v = execActivexMethod(OcxConstant.INITOCX,
				new Object[] { certId, rgCode, new Long(year), voucherType,
						stepCode, new Long(operateType), new Long(defaultPage),
						new Long(displayMode) });

		return v.getInt();

	}

	/**
	 *获取ocx调用过程异常信息
	 * 
	 */
	public static String GetLastErr() {
		Variant v = execActivexMethod(OcxConstant.GetLastErr,
				new Object[] {});
		return v.getString();
	}

	public void setUrl(String url) {

		// 调用ocx的初始化方法
		execActivexMethod(OcxConstant.SETURL,
				new Object[] { url });
	}

	/**
	 * 在ocx控件中添加凭证
	 * 
	 * @param voucherNO
	 *            凭证号
	 * @param xmlBody
	 *            按照指定格式拼装的xml
	 * @return 是否成功
	 */
	public boolean addVoucher(String voucherNO, String xmlBody) {
		// 调用ocx添加凭证的方法
		Variant v = execActivexMethod(OcxConstant.ADDVOUCHER,
				new Object[] { voucherNO, xmlBody });
		// 0表示成功，-1表示失败
		if (v.getLong() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 在ocx控件中添加凭证
	 * 
	 * @param voucherNO
	 *            凭证号
	 * @param
	 * @return 是否成功
	 */
	public static int addVoucher(String voucherNO) {
		// 调用ocx添加凭证的方法
		Variant v = execActivexMethod(
				OcxConstant.ADDVOUCHERFROMSERVER,
				new Object[] { voucherNO });
		// 0表示成功，-1表示失败
		return v.getInt();
	}

	/**
	 * 清空凭证信息
	 * 
	 * @return 是否清除成功
	 */
	public boolean clearVouchers() {
		// 调用ocx添加凭证的方法
		Variant v = execActivexMethod(OcxConstant.CLEARVOUCHER,
				null);
		// 0表示成功，-1表示失败
		if (v.getLong() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 设置当前显示的凭证
	 * 
	 * @param voucherNO
	 *            凭证号
	 * @return
	 */
	public static void ZoomToFit() {
		// 设置当前显示的方法
		Variant v = execActivexMethod(
				OcxConstant.ZoomToFit,
				new String[] { });
		
		return ;
	}
	
	/**
	 * 设置当前显示的凭证
	 * 
	 * @param voucherNO
	 *            凭证号
	 * @return
	 */
	public static int setCurrentVoucher(String voucherNO) {
		// 设置当前显示的方法
		Variant v = execActivexMethod(
				OcxConstant.SETCURRENTVOUCHER,
				new String[] { voucherNO });
		// 0表示成功，-1表示失败
		return v.getInt();
	}

	/**
	 * 获取ocx对凭证签名后的数据
	 * 
	 * @return 签名后的字符串
	 */
	public String getSignedVouchers() {
		// 设置当前显示的方法
		Variant v = execActivexMethod(
				OcxConstant.GETSIGNEDVOUCHERS, null);
		// 0表示成功，-1表示失败
		if (null == v) {
			return null;
		} else {
			return v.getString();
		}
	}

	/**
	 * 获取操作报文，完成作废、退回、撤销签章、发送、打印等操作
	 * 
	 * @param certId
	 *            证书id
	 * @param rgCode
	 *            区划
	 * @param setYear
	 *            年度
	 * @param voucherType
	 *            凭证类型
	 * @param voucherNO
	 *            需要操作的凭证报文
	 * @param opCode
	 *            操作类型 完成作废、退回、撤销签章、发送、打印
	 * @return 对报文的操作信息
	 */
	public String getOperateVouchers(String certId, String rgCode,
			long setYear, String voucherType, String voucherNO, String opCode) {
		// 执行ocx对操作报文的签名
		Variant v = execActivexMethod(
				OcxConstant.GETOPERATEVOUCHRS, new Object[] {
						certId, rgCode, new Long(setYear), voucherType,
						voucherNO, opCode });
		// 0表示成功，-1表示失败
		if (null == v) {
			return null;
		} else {
			return v.getString();
		}
	}

	/**
	 * 执行ocx中的数据
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
	 * 打印电子凭证
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
	 * 打印电子凭证（后台打印，不显示OCX）
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
	 * 显示上一联
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
	 * 显示下一联
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
	 * 打印当前
	 * 
	 */
	private class BtnPrintSelection extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {											
				voucherDtoList = ((VoucherHisSearchBean) context.getModelHolder()
						.getModel()).getCheckList();
				// 初始化为第一条凭证的信息
				HtvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
				btnPrint.setEnabled(false);
				int i = printVoucher(infoDto.getSvoucherno(), joint);
				btnPrint.setEnabled(true);				
				if (i == 0) {
					MessageDialog.openMessageDialog(null, "凭证编号:  "
							+ infoDto.getSvoucherno() + " 打印成功");
				} else {
					String err = GetLastErr();
					if (err != null && !err.equals("")) {
						MessageDialog.openMessageDialog(null, "凭证编号:  "
								+ infoDto.getSvoucherno() + " 凭证打印失败 ：" + err);
					} else {
						MessageDialog.openMessageDialog(null, "凭证编号:  "
								+ infoDto.getSvoucherno() + " 打印失败");
					}
				}
				init(listCount);
			} catch (Exception ex) {
				MessageDialog.openErrorDialog(report.getShell(), ex);
			}
		}
	}
		
	/**
	 * 打印所有
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
					// 打印完毕后，当前按钮失效，防止重复打印
					int i = printVoucher(null, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(),
							voucherNo);
					if (i == 0) {
						j++;
						if(lists.size()==j){
							MessageDialog.openMessageDialog(null, "国库："+vDto.getStrecode()+" 凭证已经全部打印！");						
						}else{
							MessageDialog.openMessageDialog(null, "国库："+vDto.getStrecode()+" 凭证已经全部打印，请点击确定继续打印！");
						}
					} else {
						String err = GetLastErr();
						if (err != null && !err.equals("")) {
							MessageDialog.openMessageDialog(null, "凭证打印失败："
									+ err);
						} else {
							MessageDialog.openMessageDialog(null, "凭证打印失败！");
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
	 * 返回
	 * 
	 * @author Administrator
	 */
	private class BtnReturn extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				IModelHolder holder = MVCUtils.openEditor("电子凭证历史查询");
				MVCUtils.openComposite("电子凭证历史查询", "维护界面");
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
	 * 上一条
	 * 
	 * @author wangyunbin
	 */
	private class BtnBeforeOne extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {

				if (listCount == 0) {
					MessageDialog.openMessageDialog(null, "当前数据是第一条！");
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
	 * 下一条
	 * 
	 * @author wangyunbin
	 */
	private class BtnNextOne extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {

				if (listCount == voucherDtoList.size() - 1) {
					MessageDialog.openMessageDialog(null, "当前数据是最后一条！");
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
	 * 上一联
	 * 
	 * @author wangyunbin
	 */
	private class BtnBeforeGroup extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			try {
				joint--;

				if (joint <= -1) {
					MessageDialog.openMessageDialog(null, "当前数据是第一联！");
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
	 * 下一联
	 * 
	 * @author wangyunbin
	 */
	private class BtnNextGroup extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			try {

				joint++;

				if (joint >= (jointCount)) {
					MessageDialog.openMessageDialog(null, "当前数据是最后一联！");
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