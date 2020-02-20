
package com.cfcc.itfe.client.sendbiz.bizcertsend;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.report.ReportComposite;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.recvbusinessreconciliation.RecvBusinessReconciliationBean;
import com.cfcc.itfe.client.sendbiz.recvreportreconciliation.RecvReportReconciliationBean;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.databinding.support.DataBindingContext;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.jasperassistant.designer.viewer.util.JasperConstants;

@SuppressWarnings("unchecked")
public class ActiveXCompositeRecvBussOcx extends Composite {
	
	private static Log logger=LogFactory.getLog(ActiveXCompositeRecvBussOcx.class);
	// ole控件对象
	protected OleControlSite controlSite;
	// ole对象方法调用代理
	protected OleAutomation automation;
	public List stampTypes;
	// JAF数据绑定对象
	private static DataBindingContext context;

	// 总记录数
	private static Text allCount;

	public static String ls_AllCount = "";
	// 当前记录
	private Text nowCount;
	// 当前联数
	private static Text nowPageCount;
	private static String stampName;
	
	// 当前次数
	private static Text printCount;
	private String voucherno="";
	// 上一条按钮
	Button buttonBefore;
	// 下一条按钮
	Button buttonNext;
	// 上一联按钮
	Button buttonBeforeGroup;
	// 下一联按钮
	Button buttonNextGroup;
	// 返回按钮
	// Button btnReturn;
	// 打印当前按钮
	Button btnPrint;
	// 打印所有按钮
	Button btnPrintAll;
	// 返回按钮
	Button btnReturn;
	// 提交按钮
	Button btnCommit;
	// 回单按钮
	Button btnVoucherWriter;
	// 退回按钮
	Button btnVoucherBack;
	// 审核按钮
	Button btnVOucherVerify;

	// 打印所有按钮
	Button btnPrintAllNew;
	//签章类型
	Button btnStampType;
	//签章类型
	private static Combo comboStampType;
	//签章
	Button btnStamp;

	private int listCount = 0;
	private static int jointCount = 0;

	// 传入的记录列表
	static List<TvVoucherinfoDto> voucherDtoList = new ArrayList<TvVoucherinfoDto>();
	private ITFELoginInfo loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
	// 报表显示区域
	ReportComposite report;
	public static String modelId = "456fe4fc-6688-45a1-a95a-034f61ea20a9";
	/**
	 * 生成签章报文（读取UKEY印章）
	 * 
	 * @param dto
	 * @throws FileOperateException 
	 */
	public static String getVoucherStamp(TvVoucherinfoDto vDto,String certID,String stampPosition,String stampID)  {
		String voucherStampXML="";
		String stamp="";
		String err=null;
		String voucherXML="";
		try {
			if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
				voucherXML = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).getVoucherXMl(vDto);
			else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
				voucherXML = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getVoucherXMl(vDto);;
			if(!vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513)&&!vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512)&&!vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511)){
				logger.debug("凭证签章写入的签章报文："+voucherXML);
			}
		} catch (Exception e) {
			logger.error(e);
			err="凭证报文解析异常";
			return err+"@";
		}
		
		try {
			stamp = getStampPotisionXML(stampPosition, stampID);
			logger.debug("凭证签章写入的签章位置报文："+stamp);
		} catch (Exception e) {
			logger.error(e);
			err="组装签章位置报文异常";
			return err+"@";
		
		}
		voucherStampXML=getVoucherStamp(certID, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(), stamp, voucherXML);
		if(voucherStampXML==null||voucherStampXML.equals("")){
			 err = GetLastErr();
			if (err != null && !err.equals("")) {
					return err+"@";
			}
			return "组装凭证签章报文异常"+"@";
		}
		return voucherStampXML;
	
	
	}
	
	/**
	 * 组装签章位置报文
	 * 
	 */
	public static String getStampPotisionXML(String stampPosition,String stampID)throws Exception{
		Document stampDoc=null;
		stampDoc=DocumentHelper.createDocument();
		stampDoc.setXMLEncoding("GBK");
		Element Root=stampDoc.addElement("Root");
		Element Stamp=Root.addElement("Stamp");
		Stamp.addAttribute("No",stampPosition);
		Stamp.setText(stampID);
		return stampDoc.asXML().replaceAll("&lt;", "<")
		.replaceAll("&gt;", ">");
	}
	

	
	/**
	 * 对字符串进行base64编码
	 * 
	 * @param src字符串
	 * @return base64字符串
	 * @throws UnsupportedEncodingException 
	 */
	public static String base64Encode(String src) throws UnsupportedEncodingException {
		byte[] outByte = encode(src.getBytes());
		String ret = null;
		ret = new String(outByte, "gb2312");
		return ret;
	}
	
	
	/**
	 * 编码表
	 */
	private static final byte[] ENCODINGTABLE =	{
		(byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
		(byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
		(byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U',
		(byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
		(byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
		(byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
		(byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u',
		(byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z',
		(byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6',
		(byte)'7', (byte)'8', (byte)'9',
		(byte)'+', (byte)'/'
	};

	/**
	 * Base64编码
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
		int	b1, b2, b3;
		int	d1, d2;
		
		switch (modulus) {
		case 0: /* nothing left to do */
			break;
		case 1:
			d1 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = (d1 << 4) & 0x3f;
			
			bytes[bytes.length - 4] = ENCODINGTABLE[b1];
			bytes[bytes.length - 3] = ENCODINGTABLE[b2];
			bytes[bytes.length - 2] = (byte)'=';
			bytes[bytes.length - 1] = (byte)'=';
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
			bytes[bytes.length - 1] = (byte)'=';
			break;
		}
		
		return bytes;
	}
	
	/*
	 * 解码表
	 */
	private static final byte[] DECODINGTABLE;
	
	static {
		DECODINGTABLE = new byte[128];
		for (int i = 'A'; i <= 'Z'; i++) {
			DECODINGTABLE[i] = (byte)(i - 'A');
		}
		
		for (int i = 'a'; i <= 'z'; i++) {
			DECODINGTABLE[i] = (byte)(i - 'a' + 26);
		}
		
		for (int i = '0'; i <= '9'; i++) {
			DECODINGTABLE[i] = (byte)(i - '0' + 52);
		}
		
		DECODINGTABLE['+'] = 62;
		DECODINGTABLE['/'] = 63;
	}
	
	/**
	 * Base64解码
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
			
			bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte)((b3 << 6) | b4);
		}
		
		if (data[data.length - 2] == '=') {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			
			bytes[bytes.length - 1] = (byte)((b1 << 2) | (b2 >> 4));
		} else if (data[data.length - 1] == '=') {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			b3 = DECODINGTABLE[data[data.length - 2]];
			
			bytes[bytes.length - 2] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte)((b2 << 4) | (b3 >> 2));
		} else {
			b1 = DECODINGTABLE[data[data.length - 4]];
			b2 = DECODINGTABLE[data[data.length - 3]];
			b3 = DECODINGTABLE[data[data.length - 2]];
			b4 = DECODINGTABLE[data[data.length - 1]];
			
			bytes[bytes.length - 3] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte)((b3 << 6) | b4);
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
	 * 解码
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
			
			bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte)((b3 << 6) | b4);
		}
		
		if (data.charAt(data.length() - 2) == '=') {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			
			bytes[bytes.length - 1] = (byte)((b1 << 2) | (b2 >> 4));
		} else if (data.charAt(data.length() - 1) == '=') {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			b3 = DECODINGTABLE[data.charAt(data.length() - 2)];
			
			bytes[bytes.length - 2] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte)((b2 << 4) | (b3 >> 2));
		} else {
			b1 = DECODINGTABLE[data.charAt(data.length() - 4)];
			b2 = DECODINGTABLE[data.charAt(data.length() - 3)];
			b3 = DECODINGTABLE[data.charAt(data.length() - 2)];
			b4 = DECODINGTABLE[data.charAt(data.length() - 1)];
			
			bytes[bytes.length - 3] = (byte)((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte)((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte)((b3 << 6) | b4);
		}
		
		return bytes;
	}

	public static List getListByAdmdivcode(List<TvVoucherinfoDto> list){
		Map map=new HashMap();
		for(TvVoucherinfoDto dto:list){
			map.put(dto.getSadmdivcode(), "");
		}
		if(map.size()==0){
			return null;
		}
		List<List> lists=new ArrayList<List>();
		try{
			
			for(Iterator<String> it=map.keySet().iterator();it.hasNext();){
				String key=(String)it.next();
				List<TvVoucherinfoDto> newList=new ArrayList<TvVoucherinfoDto>();
				for(TvVoucherinfoDto dto:list){
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
	
	/**
	 * 对数据进行签章
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
	 * 凭证显示
	 * 
	 * @param num
	 */
	public static void init(int num) {
		comboStampType.removeAll();
		if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
			voucherDtoList = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).getCheckList();
		else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
			voucherDtoList = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getCheckList();
		ls_AllCount = voucherDtoList.size() + "";
		
		allCount.setText(ls_AllCount);
		// 初始化为第一条凭证的信息
		TvVoucherinfoDto infoDto = voucherDtoList.get(num);
		Refresh (infoDto.getSvoucherno());
		String err = GetLastErr();
	
		int count=0;
		if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
			count = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).queryVoucherJOintCount(infoDto);
		else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
			count = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).queryVoucherJOintCount(infoDto);
		if(count==-1){
			nowPageCount.setText("联数未维护");
		}else if(count==-2){
			nowPageCount.setText("查询异常");
		}else{
			jointCount=count;
			nowPageCount.setText(""+1);
		}
		
		List<Mapper> retList =new ArrayList<Mapper>();
		if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
			retList=((RecvReportReconciliationBean) context.getModelHolder().getModel()).getStampEnums(infoDto);
		else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
			retList=((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getStampEnums(infoDto);
		
		for(Mapper map : retList){
			comboStampType.add(map.getDisplayValue().toString());
		}		
		String printCounts= "";
		if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
			printCounts=((RecvReportReconciliationBean) context.getModelHolder().getModel()).queryVoucherPrintCount(infoDto);
		else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
			printCounts=((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).queryVoucherPrintCount(infoDto);	
		printCount.setText(printCounts.equals("-1")==true?"0":printCounts);
		int i = -1;
		err = null;
		i = initStampOcx("0", infoDto.getSadmdivcode(), Integer
				.parseInt(infoDto.getSstyear()), infoDto.getSvtcode().trim(),
				"0", 2, 0, 0);
		if (i == 0) {
			try {
				if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
					i = AddVoucherXmls(((RecvReportReconciliationBean) context.getModelHolder().getModel()).getVoucherXMl(infoDto));
				else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
					i = AddVoucherXmls(((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getVoucherXMl(infoDto));	
			} catch (Exception e) {
				logger.error(e);
				i = addVoucher(infoDto.getSvoucherno());
			}	

			if (i == 0) {
				i = setCurrentVoucher(infoDto.getSvoucherno());
				ZoomToFit();
				Refresh (infoDto.getSvoucherno());
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
	 * @param parent
	 */
	public ActiveXCompositeRecvBussOcx(Composite parent) {

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
		// 打印次数
		final Label labelprint = new Label(composite, SWT.NONE);
		labelprint.setText("打印次数：");
		labelprint.setBounds(620, 10, 59, 20);
		labelprint.setVisible(true);

		printCount = new Text(composite, SWT.BORDER);
		printCount.setBounds(700, 7, 80, 21);
		printCount.setEnabled(false);
		printCount.setText("");
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
		
		final Label stamptypelabel = new Label(composite, SWT.NONE);
		stamptypelabel.setText("签章类型");
		stamptypelabel.setBounds(630, 40, 50, 21);
		
		comboStampType = new Combo(composite, SWT.NONE);
		comboStampType.setBounds(685, 35, 110, 21);	
		comboStampType.setEnabled(true);
		
		
		
		btnStamp = new Button(composite, SWT.NONE);
		btnStamp.setBounds(810, 35, 80, 21);
		btnStamp.setText("签章");
		btnStamp.setEnabled(true);
		btnStamp.addSelectionListener(new BtnStamp());
		
		
		btnReturn = new Button(composite, SWT.NONE);
		btnReturn.setBounds(920, 35, 80, 21);
		btnReturn.setText("返回");
		btnReturn.setEnabled(true);
		btnReturn.addSelectionListener(new BtnReturn());
		
		context = DataBindingContext.createInstance(modelId);

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
	

	public static String Refresh(String voucherno) {
		Variant v = execActivexMethod(OcxConstant.Refresh,
				new Object[] {voucherno});
		return v.getString();
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
	public static int setCurrentVoucher(String voucherNO) {
		// 设置当前显示的方法
		Variant v = execActivexMethod(
				OcxConstant.SETCURRENTVOUCHER,
				new String[] { voucherNO });
		// 0表示成功，-1表示失败
		return v.getInt();
	}
	
	/**
	 * 指定原文显示凭证
	 * 
	 * @param voucherNO
	 *            凭证号
	 * @return
	 */
	public static int AddVoucherXmls(String XmlVouchers) {
		// 设置当前显示的方法
		Variant v = execActivexMethod(
				"AddVoucherXmls",
				new String[] { XmlVouchers,"0"});
		// 0表示成功，-1表示失败
		return v.getInt();
	}
	
	/**
	 * 自动调整显示凭证
	 * 
	 * @param voucherNO
	 *            凭证号
	 * @return
	 */
	public static void ZoomToFit() {
		// 设置当前显示的方法
		execActivexMethod(OcxConstant.ZoomToFit,new String[] { });
		
		return ;
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
		Variant v = ActiveXCompositeRecvBussOcx.execActivexMethod(
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
		
		Variant v = ActiveXCompositeRecvBussOcx.execActivexMethod(
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
		Variant v = ActiveXCompositeRecvBussOcx.execActivexMethod(OcxConstant.PAGEUP,
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
		Variant v = ActiveXCompositeRecvBussOcx.execActivexMethod(OcxConstant.PAGEDOWN,
				new Object[] {});
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
				if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
				{
					voucherDtoList = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).getCheckList();
				}else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
				{
					voucherDtoList = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getCheckList();
				}
				// 初始化为第一条凭证的信息
				TvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
				if(!(verifyPrintCount(infoDto)&&verifyPrintPermission())){
					return ;
				}
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
				if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
				{
					voucherDtoList = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).getCheckList();
				}else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
				{
					voucherDtoList = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getCheckList();
				}
				for(TvVoucherinfoDto dto:voucherDtoList){
					if(!verifyPrintCount(dto)){
						return ;
					}
				}
				if(!verifyPrintPermission()){
					return;
				}
				List lists=getListByAdmdivcode(voucherDtoList);
				TvVoucherinfoDto vDto=new TvVoucherinfoDto();
				for(List list:(List<List>)lists){
					for(TvVoucherinfoDto dto:(List<TvVoucherinfoDto>)list){
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
	
	public boolean verifyPrintCount(TvVoucherinfoDto dto){		
		if(!(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)||
				dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)||
				dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)||
				dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5502)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2502))){
			try {
				if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
				{
					dto=((RecvReportReconciliationBean) context.getModelHolder().getModel()).getDto(dto);
				}else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
				{
					dto=((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getDto(dto);
				}
			} catch (ITFEBizException e) {
				logger.error(e);				
				MessageDialog.openErrorDialog(
						Display.getDefault().getActiveShell(),e);
				return false;			
			}
		}
		String printCount="";
		if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
		{
			printCount = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).queryVoucherPrintCount(dto);
		}else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
		{
			printCount = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).queryVoucherPrintCount(dto);
		}
		if(printCount.indexOf("异常")>0){
			MessageDialog.openMessageDialog(null, "凭证编号:  "+ dto.getSvoucherno() + "查询打印次数异常，不能打印！");
			return false;
		}
		int i=1;
		if(loginInfo.getPublicparam().contains(",printvoucher=2,"))
			i=2;
		else if(loginInfo.getPublicparam().contains(",printvoucher=null,"))
			i=50;
		if(Integer.parseInt(printCount)>=(jointCount*i)){
			voucherno=dto.getSvoucherno();
		}	
		return true;
	}
	
	public boolean verifyPrintPermission(){
		if(voucherno!=null&&!voucherno.equals("")){
			String msg = "凭证编号："+voucherno+" 打印次数大于等于1，需主管授权才能多次打印凭证！";
			voucherno="";
			if(!AdminConfirmDialogFacade.open("B_247", "收入报表凭证", "授权打印凭证", msg)){
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				return false;
			}
		}
		return true;
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
				if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
				{
					 MVCUtils.openEditor("发送报表对账回单");
					MVCUtils.openComposite("发送报表对账回单", "维护界面");
					((RecvReportReconciliationBean) context.getModelHolder().getModel()).getCheckList().clear();
					((AbstractMetaDataEditorPart) ((RecvReportReconciliationBean) context.getModelHolder().getModel()).getModelHolder()).fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
				{
					MVCUtils.openEditor("发送业务对账回单");
					MVCUtils.openComposite("发送业务对账回单", "维护界面");
					((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getCheckList().clear();
					((AbstractMetaDataEditorPart) ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getModelHolder()).fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}
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
				 * TvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
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
				 * TvVoucherinfoDto infoDto = voucherDtoList.get(listCount);
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
	

	private class BtnStamp extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			try{

				String stampNameCace=comboStampType.getText();
				if((stampNameCace==null||stampNameCace.equals(""))&&(stampName==null||stampName.equals(""))){
					MessageDialog.openMessageDialog(null, "请选择签章类型！");
					return;
				}else{
					if(stampNameCace!=null&&!stampNameCace.equals("")){
						stampName=stampNameCace;
					}
				}
				
				List list=new ArrayList();
				list.add(stampName);
				if(context.getModelHolder().getModel() instanceof RecvReportReconciliationBean)
				{
					list.add(((RecvReportReconciliationBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)));
					String stamptCounts= ((RecvReportReconciliationBean) context.getModelHolder().getModel()).voucherStamp(list);
					if(stamptCounts.equals("0")){
//						MessageDialog.openMessageDialog(null, ((VoucherReportBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)).getSreturnerrmsg());
						String returnmsg = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)).getSreturnerrmsg();
						((RecvReportReconciliationBean) context.getModelHolder().getModel()).getCheckList().get(listCount).setSreturnerrmsg(returnmsg);
						MessageDialog.openMessageDialog(null, returnmsg);
					}else if(stamptCounts.equals("1")){
//						MessageDialog.openMessageDialog(null, stampName+"签章成功！");
						String demo = ((RecvReportReconciliationBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)).getSdemo();
						((RecvReportReconciliationBean) context.getModelHolder().getModel()).getCheckList().get(listCount).setSstatus(DealCodeConstants.VOUCHER_STAMP);
						((RecvReportReconciliationBean) context.getModelHolder().getModel()).getCheckList().get(listCount).setSdemo(demo);
						MessageDialog.openMessageDialog(null, stampName+"签章成功！");
					}
				}else if(context.getModelHolder().getModel() instanceof RecvBusinessReconciliationBean)
				{
					list.add(((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)));
					String stamptCounts= ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).voucherStamp(list);
					if(stamptCounts.equals("0")){
//						MessageDialog.openMessageDialog(null, ((VoucherReportBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)).getSreturnerrmsg());
						String returnmsg = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)).getSreturnerrmsg();
						((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getCheckList().get(listCount).setSreturnerrmsg(returnmsg);
						MessageDialog.openMessageDialog(null, returnmsg);
					}else if(stamptCounts.equals("1")){
//						MessageDialog.openMessageDialog(null, stampName+"签章成功！");
						String demo = ((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getDto(voucherDtoList.get(listCount)).getSdemo();
						((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getCheckList().get(listCount).setSstatus(DealCodeConstants.VOUCHER_STAMP);
						((RecvBusinessReconciliationBean) context.getModelHolder().getModel()).getCheckList().get(listCount).setSdemo(demo);
						MessageDialog.openMessageDialog(null, stampName+"签章成功！");
					}
				}
				init(listCount);
			}catch(ITFEBizException e1){
				logger.error(e1);
				Exception e2=new Exception("凭证签章操作出现异常！",e1);
				MessageDialog.openErrorDialog(
						Display.getDefault().getActiveShell(),e2);
			}catch(Exception e1){
				logger.error(e1);
				Exception e2=new Exception("凭证签章操作出现异常！",e1);
				MessageDialog.openErrorDialog(
						Display.getDefault().getActiveShell(),e2);
			}
		}
	}

	public List getStampTypes() {
		return stampTypes;
	}

	public void setStampTypes(List stampTypes) {
		this.stampTypes = stampTypes;
	}

	public static String getStampName() {
		return stampName;
	}

	public static void setStampName(String stampName) {
		ActiveXCompositeRecvBussOcx.stampName = stampName;
	}

	public static String getModelId() {
		return modelId;
	}

	public static void setModelId(String modelId) {
		ActiveXCompositeRecvBussOcx.modelId = modelId;
	}
}