package com.cfcc.itfe.client.sendbiz.bizcertsend;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Shell;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class AsspOcx{
	private static Log logger=LogFactory.getLog(AsspOcx.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)ServiceFactory.getService(IVoucherLoadService.class);
	/**
	 * 初始化ocx控件
	 */
	private final static String INITOCX = "Initialize";
	
	/**
	 * 获取证书id
	 */
	final static String DlgSelectCertId = "DlgSelectCertId";
	/**
	 *  获取印章id
	 */
	final static String DlgSelectStampId= "DlgSelectStampId";
	private final static String saveStampVoucher= "saveStampVoucher";
	/**
	 * 获取异常信息
	 */
	private final static String GetLastErr = "GetLastErr";
	/**
	 * 添加凭证的常量类
	 */
	private final static String ADDVOUCHER = "AddVoucher";
	private final static String AddVoucherfromServer="AddVoucherfromServer";
	
	/**
	 * 清空凭证信息的常量类
	 */
	private final static String CLEARVOUCHER = "EstampOcxClearVouchers";
	
	/**
	 * 展示当前选定的凭证信息
	 */
	private final static String SETCURRENTVOUCHER = "SetCurrentVoucher";
	
	/**
	 * 获得签名档数据的常量
	 */
	private final static String GETSIGNEDVOUCHERS = "EstampOcxGetSingedVouchers";
	
	/**
	 *获得操作报文的常量类
	 */
	private final static String GETOPERATEVOUCHRS = "EstampOcxGetVoucherOperate";
	
	
	/**
	 * 设置url
	 */
	private final static String SETURL = "SetEvoucherServiceUrl";
	
	/**
	 * 获得签章档数据的常量
	 */
	private final static String GETVOUCHERSTAMP="GetVoucherStamp";
	
	
	/**
	 * 电子凭证打印（后台打印，不显示OCX）
	 */
	private final static String PRINTALLVOUCHER="PrintAllVoucher";
	
	/**
	 * 电子凭证打印
	 */
	private final static String PRINTONEVOUCHER="PrintVoucherByNo";
	
	/**
	 * 查询凭证打印次数
	 */
	private final static String QUERYVOUCHERPRINTCOUNT="queryVoucherPrintCount";
	
	/**
	 * 上一联
	 */
	private final static String PAGEUP="PageUp";
	
	/**
	 * 下一联
	 */
	private final static String PAGEDOWN="PageDown";//
//	private final static String DlgSelectStampId="DlgSelectStampId";
	
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
    private OleAutomation _auto;
    

    public Shell shell = null;
    
    
    private static int joint = 0; //默认显示联
    private ITFELoginInfo loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    public static void main(String str[]){
    	AsspOcx aa = new AsspOcx(new Shell());
    	aa.DlgSelectCertId();
    	aa.DlgSelectStampId();
    }
    
    public AsspOcx ( Shell shell ){
    	this.shell = shell;
	    _frame = new OleFrame(shell, SWT.BORDER);//
	    _site = new OleControlSite(_frame, SWT.NONE, "{4FC4CDDF-84E5-437C-8527-B23F6D70866C}");
	    _auto = new OleAutomation(_site);
	    /*String ls_URL = null;
	    String stampurl = null;
		try {
			ls_URL = voucherLoadService.getOCXServerURL();
			stampurl = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			logger.error("获取WEBSEVICE或电子印章地址出错！",e);
		    MessageDialog.openErrorDialog(null, e);
		}*/
	    /**修改为由工具类根据配置(itfeesb_linux.properties文件中的ocxservice.isClientProxy参数)决定是否使用代理方式来产生访问的URL地址 20141001**/
		String ls_URL = ClientOcxServiceProxyUtils.getOcxVoucherServerURL();
		String stampurl = ClientOcxServiceProxyUtils.getOCXStampServerURL();
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
    
    
    
    /**
     * 防止外部调用
     */
    public AsspOcx(){
    	this( new Shell());
    }
    public Variant execActivexMethod(String methodName, Object[] params){
		int[] methodIDs = _auto.getIDsOfNames(new String[] { methodName });
		int activeXMethodID = methodIDs[0];
		Variant[] paramVariant = null;
		if( params == null ){
			paramVariant = new Variant[0];
		}else{
			paramVariant = new Variant[params.length];
		}
		
		for(int i=0,n=params.length; i<=n-1; i++){
			if( params[i] == null ){
				paramVariant[i] = null;
			}else if( params[i] instanceof String ){
				paramVariant[i] = new Variant((String)params[i]);
			} else if( params[i] instanceof Long  ){
				Long l = (Long)params[i];
				paramVariant[i] = new Variant( l.longValue() );
			}else if( params[i] instanceof Short ){
				Short l = (Short)params[i];
				paramVariant[i] = new Variant( l.shortValue() );
			}else if( params[i] instanceof Integer ){
				Integer l = (Integer)params[i];
				paramVariant[i] = new Variant( l.intValue() );
			}
		}
		Variant variant = _auto.invoke(activeXMethodID, paramVariant);
		return variant;
	}
//    public void voucherView(String certId,String rgCode, long year,  String voucherType,
//    		String stepCode, long operateType, long defaultPage, long displayMode,String VoucherNo){
//    	System.out.println(getLatErr());
//    	try{
//    		boolean result=initStampOcx("0","420800",2013,"5207","0", 2, 0, 0);
//        	System.out.println(getLatErr());
//        	System.out.println("初始化结果： "+result);
//        	
//        	if(result==true){
//        		boolean addVoucherResult=AddVoucherfromServer("voucher00010");
//        		System.out.println("添加凭证结果： "+addVoucherResult);
//        		if(addVoucherResult==true){
//        			boolean setVoucherResult=setCurrentVoucher("voucher00010");
//        			System.out.println("set凭证结果： "+setVoucherResult);
//        		}else{
//        			System.out.println(getLatErr());
//        		}
//        	}
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
//    	
//    }
    
    public void  voucherPintAll(List<TvVoucherinfoDto> voucherDtoList){
    	initStampOcx("0","420800",2013,"5207","0", 2, 0, 0);
		String voucherNo = "";
		for(int i = 0 ; i < voucherDtoList.size(); i++){
			voucherNo = voucherNo+","+voucherDtoList.get(i).getSvoucherno().trim();
		}
		voucherNo = 	voucherNo.substring(1,voucherNo.length());
		
		int i = printVoucher(null, voucherDtoList.get(0).getSadmdivcode(), Integer.parseInt(voucherDtoList.get(0).getSstyear()), voucherDtoList.get(0).getSvtcode(), voucherNo);
		System.out.println(i);
    }
    public void saveStampVoucher(){
    	byte[] signedVoucher=null;
		try {
			signedVoucher = FileUtil.getInstance().readFile("d:/rev5207 - stamp.msg").getBytes();
		} catch (FileOperateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
			saveStampVoucher("001", "420800",2013, "5207", signedVoucher);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void GetVoucherStamp(){
    	boolean result=initStampOcx("001","420800",2013,"5207","0", 2, 0, 0);
    	System.out.println(getLatErr());
    	System.out.println("初始化结果： "+result);
		String stamp="<?xml version=\"1.0\" encoding=\"GBK\"?><Root><Stamp No=\"rh_ywzqz\">add5cab97d344bea8100614cb787a397</Stamp></Root>";
//凭证签名(章)位置报文
    	String voucher="";
    	try {
    		voucher=FileUtil.getInstance().readFile("d:/rev5207.msg");
		} catch (FileOperateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//客户端待签名 (章)凭证报文
    	String xml=getVoucherStamp("001", "420800",
        		2013,"5207",stamp,  voucher);
    	System.out.println(xml);
    }
    public Map<String,String> saveStampVoucher(String certID, String admDivCode, long year, String vtCode, byte[] signedVoucher) throws Exception{
    	Variant v = execActivexMethod( AsspOcx.saveStampVoucher, new Object[]{certID, admDivCode,new Long(year), vtCode, signedVoucher } );
    	//0表示成功，-1表示失败
    	
    	return null;
    	
    }
    /**
     * 取得错误信息
     * @return
     */
    public String getLatErr(  ){
    	//调用ocx添加凭证的方法
    	Variant v = execActivexMethod( AsspOcx.GetLastErr, new Object[]{ } );
    	return v.getString();
    }
    public void print(String certId,String rgCode, long year,  String voucherType,
    		String stepCode, long operateType, long defaultPage, long displayMode,String VoucherNo){
    	
    	boolean result=initStampOcx("0","420800",2013,"5207","0", 2, 0, 0);
    	
    	System.out.println("初始化结果： "+result);
    	AddVoucherfromServer("voucher00003");
		printVoucher("voucher00003",1);
    }
    
   
    public String DlgSelectStampId(){
    	Variant v=execActivexMethod( AsspOcx.DlgSelectStampId , new Object[]{} );
    	System.out.println("印章ID： "+v.getString());
    	return v.getString();
    }
    public String DlgSelectCertId(){
    	Variant v=execActivexMethod( AsspOcx.DlgSelectCertId , new Object[]{} );
    	System.out.println("证书ID： "+v.getString());
    	return v.getString();
    }
    /**
     * 初始化ocx控件
     * @param certId 证书id
     * @param year 业务年度
     * @param voucherType 凭证类型
     * @param stepCode 第几步
     * @param operateType 操作类型
     * @param defaultPage 默认业
     * @param displayMode 显示模式
     */
    public boolean initStampOcx( String certId,String rgCode, long year,  String voucherType,
    		String stepCode, long operateType, long defaultPage, long displayMode ){

    	//初始化默认显示联
    	joint = (int)defaultPage;
    	
    	//调用ocx的初始化方法
    	
    	Variant v=execActivexMethod( AsspOcx.INITOCX, new Object[]{ certId,rgCode, new Long( year ),
    			voucherType, stepCode, new Long( operateType ),
    			new Long( defaultPage ), new Long( displayMode ) } );
    	if(v.getInt()==0){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    
    public void setUrl( String url ){

    	//调用ocx的初始化方法
    	execActivexMethod( AsspOcx.SETURL, new Object[]{ url }   );
    }
    
    /**
     * 在ocx控件中添加凭证
     * @param voucherNO 凭证号
     *
     * @return 是否成功
     */
   
    public boolean AddVoucherfromServer( String voucherNO ){
    	//调用ocx添加凭证的方法
    	Variant v = execActivexMethod( AsspOcx.AddVoucherfromServer, new Object[]{ voucherNO} );
    	//0表示成功，-1表示失败
    	if( v.getInt() == 0 ){
    		return true;
    	}else{
    		
    		return false;
    	}
    }
    /**
     * 在ocx控件中添加凭证
     * @param voucherNO 凭证号
     * @param xmlBody 按照指定格式拼装的xml
     * @return 是否成功
     */
    public boolean addVoucher( String voucherNO, String xmlBody ){
    	//调用ocx添加凭证的方法
    	Variant v = execActivexMethod( AsspOcx.ADDVOUCHER, new Object[]{ voucherNO, xmlBody } );
    	//0表示成功，-1表示失败
    	if( v.getLong() == 0 ){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * 清空凭证信息
     * @return 是否清除成功
     */
    public boolean clearVouchers(){
    	//调用ocx添加凭证的方法
    	Variant v = execActivexMethod( AsspOcx.CLEARVOUCHER, null );
    	//0表示成功，-1表示失败
    	if( v.getLong() == 0 ){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * 设置当前显示的凭证
     * @param voucherNO 凭证号
     * @return
     */
    public boolean setCurrentVoucher( String voucherNO ){
    	//设置当前显示的方法
    	Variant v = execActivexMethod( AsspOcx.SETCURRENTVOUCHER, new String[]{ voucherNO } );
    	//0表示成功，-1表示失败
    	if( v.getInt() == 0 ){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * 获取ocx对凭证签名后的数据cc00
     * @return 签名后的字符串
     */
    public String getSignedVouchers(){
      	//设置当前显示的方法
    	Variant v = execActivexMethod( AsspOcx.GETSIGNEDVOUCHERS, null );
    	//0表示成功，-1表示失败
    	if( null == v ){
    		return null;
    	}else{
    		return v.getString();
    	}
    }
    

    /**
     * 获取操作报文，完成作废、退回、撤销签章、发送、打印等操作
     * @param certId 证书id 
     * @param rgCode 区划
     * @param setYear 年度
     * @param voucherType 凭证类型
     * @param voucherNO 需要操作的凭证报文
     * @param opCode 操作类型 完成作废、退回、撤销签章、发送、打印
     * @return 对报文的操作信息
     */
    public String getOperateVouchers(
    		String certId, String rgCode, long setYear,
    		String voucherType, String voucherNO,
    		String opCode ){
       	//执行ocx对操作报文的签名
    	Variant v = execActivexMethod( AsspOcx.GETOPERATEVOUCHRS, 
    			new Object[]{ certId, rgCode, new Long( setYear ),
    			voucherType, voucherNO, opCode} );
    	//0表示成功，-1表示失败
    	if( null == v ){
    		return null;
    	}else{
    		return v.getString();
    	}
    }
    

    /**
     * 执行ocx中的数据
     * @param methodName
     * @param params
     * @return
     */
//	protected Variant execActivexMethod(String methodName, Object[] params  ){
//		int[] methodIDs = _auto.getIDsOfNames(new String[] { methodName });
//		int activeXMethodID = methodIDs[0];
//		Variant[] paramVariant = null;
//		if( params == null ){
//			paramVariant = new Variant[0];
//		}else{
//			paramVariant = new Variant[params.length];
//		}
//		
//		for(int i=0,n=params.length; i<=n-1; i++){
//			if( params[i] == null ){
//			//	paramVariant[i] = Variant.NULL;
//			}else if( params[i] instanceof String ){
//				paramVariant[i] = new Variant((String)params[i]);
//			} else if( params[i] instanceof Long  ){
//				Long l = (Long)params[i];
//				paramVariant[i] = new Variant( l.longValue() );
//			}else if( params[i] instanceof Short ){
//				Short l = (Short)params[i];
//				paramVariant[i] = new Variant( l.shortValue() );
//			}else if( params[i] instanceof Integer ){
//				Integer l = (Integer)params[i];
//				paramVariant[i] = new Variant( l.intValue() );
//			}
//		}
//		Variant variant = _auto.invoke(activeXMethodID, paramVariant);
//		return variant;
//	}

	/**
	 * 对数据进行签章
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param stamp
	 * @param voucher
	 * @return
	 */
    public String getVoucherStamp(String certID, String admDivCode,
    		long stYear, String vtCode, String stamp, String voucher){
      	Variant v = execActivexMethod( 
      			AsspOcx.GETVOUCHERSTAMP, 
      			new Object[]{ certID, admDivCode,new Long( stYear),vtCode,stamp,voucher } );
    	if(null==v){
    		return null;
    	}else{
    		return v.getString();
    	}
    }
    
    
    /**
     * 打印电子凭证
     * @param voucherNo
     * @param pageNo
     * @return
     */
    public int printVoucher(String voucherNo, int pageNo){
    	Variant v = this.execActivexMethod(PRINTONEVOUCHER, new Object[]{voucherNo, new Long(joint)});
    	
    	return v.getInt();
    }
    
    
    /**
     * 打印电子凭证（后台打印，不显示OCX）
     * @param voucherNo
     * @param pageNo
     * @return 
     */
    public int printVoucher(String certID, String admDivCode, int stYear, String vtCode, String voucherNo){
    	Variant v = this.execActivexMethod(PRINTALLVOUCHER, new Object[]{certID, admDivCode, new Long(stYear), vtCode, new Long(joint), voucherNo});
    	
    	return v.getInt();
    }
   
    
    /**
     * 显示上一联
     * @return
     */
    public int pageUp(){
    	Variant v = this.execActivexMethod(PAGEUP, new Object[]{});
    	int result = v.getInt();
    	if(result != -1){
    		joint--;
    	}
    	return result;
    }

    /**
     * 显示下一联
     * @return
     */
    public int pageDown(){
    	Variant v = this.execActivexMethod(PAGEDOWN, new Object[]{});
    	
    	int result = v.getInt();
    	if(result != -1){
    		joint++;
    	}
    	
    	return v.getInt();
    }
    
  

 }
