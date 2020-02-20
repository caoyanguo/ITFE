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
	 * ��ʼ��ocx�ؼ�
	 */
	private final static String INITOCX = "Initialize";
	
	/**
	 * ��ȡ֤��id
	 */
	final static String DlgSelectCertId = "DlgSelectCertId";
	/**
	 *  ��ȡӡ��id
	 */
	final static String DlgSelectStampId= "DlgSelectStampId";
	private final static String saveStampVoucher= "saveStampVoucher";
	/**
	 * ��ȡ�쳣��Ϣ
	 */
	private final static String GetLastErr = "GetLastErr";
	/**
	 * ���ƾ֤�ĳ�����
	 */
	private final static String ADDVOUCHER = "AddVoucher";
	private final static String AddVoucherfromServer="AddVoucherfromServer";
	
	/**
	 * ���ƾ֤��Ϣ�ĳ�����
	 */
	private final static String CLEARVOUCHER = "EstampOcxClearVouchers";
	
	/**
	 * չʾ��ǰѡ����ƾ֤��Ϣ
	 */
	private final static String SETCURRENTVOUCHER = "SetCurrentVoucher";
	
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
	private final static String GETVOUCHERSTAMP="GetVoucherStamp";
	
	
	/**
	 * ����ƾ֤��ӡ����̨��ӡ������ʾOCX��
	 */
	private final static String PRINTALLVOUCHER="PrintAllVoucher";
	
	/**
	 * ����ƾ֤��ӡ
	 */
	private final static String PRINTONEVOUCHER="PrintVoucherByNo";
	
	/**
	 * ��ѯƾ֤��ӡ����
	 */
	private final static String QUERYVOUCHERPRINTCOUNT="queryVoucherPrintCount";
	
	/**
	 * ��һ��
	 */
	private final static String PAGEUP="PageUp";
	
	/**
	 * ��һ��
	 */
	private final static String PAGEDOWN="PageDown";//
//	private final static String DlgSelectStampId="DlgSelectStampId";
	
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
    private OleAutomation _auto;
    

    public Shell shell = null;
    
    
    private static int joint = 0; //Ĭ����ʾ��
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
			logger.error("��ȡWEBSEVICE�����ӡ�µ�ַ����",e);
		    MessageDialog.openErrorDialog(null, e);
		}*/
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
    
    
    
    /**
     * ��ֹ�ⲿ����
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
//        	System.out.println("��ʼ������� "+result);
//        	
//        	if(result==true){
//        		boolean addVoucherResult=AddVoucherfromServer("voucher00010");
//        		System.out.println("���ƾ֤����� "+addVoucherResult);
//        		if(addVoucherResult==true){
//        			boolean setVoucherResult=setCurrentVoucher("voucher00010");
//        			System.out.println("setƾ֤����� "+setVoucherResult);
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
    	System.out.println("��ʼ������� "+result);
		String stamp="<?xml version=\"1.0\" encoding=\"GBK\"?><Root><Stamp No=\"rh_ywzqz\">add5cab97d344bea8100614cb787a397</Stamp></Root>";
//ƾ֤ǩ��(��)λ�ñ���
    	String voucher="";
    	try {
    		voucher=FileUtil.getInstance().readFile("d:/rev5207.msg");
		} catch (FileOperateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//�ͻ��˴�ǩ�� (��)ƾ֤����
    	String xml=getVoucherStamp("001", "420800",
        		2013,"5207",stamp,  voucher);
    	System.out.println(xml);
    }
    public Map<String,String> saveStampVoucher(String certID, String admDivCode, long year, String vtCode, byte[] signedVoucher) throws Exception{
    	Variant v = execActivexMethod( AsspOcx.saveStampVoucher, new Object[]{certID, admDivCode,new Long(year), vtCode, signedVoucher } );
    	//0��ʾ�ɹ���-1��ʾʧ��
    	
    	return null;
    	
    }
    /**
     * ȡ�ô�����Ϣ
     * @return
     */
    public String getLatErr(  ){
    	//����ocx���ƾ֤�ķ���
    	Variant v = execActivexMethod( AsspOcx.GetLastErr, new Object[]{ } );
    	return v.getString();
    }
    public void print(String certId,String rgCode, long year,  String voucherType,
    		String stepCode, long operateType, long defaultPage, long displayMode,String VoucherNo){
    	
    	boolean result=initStampOcx("0","420800",2013,"5207","0", 2, 0, 0);
    	
    	System.out.println("��ʼ������� "+result);
    	AddVoucherfromServer("voucher00003");
		printVoucher("voucher00003",1);
    }
    
   
    public String DlgSelectStampId(){
    	Variant v=execActivexMethod( AsspOcx.DlgSelectStampId , new Object[]{} );
    	System.out.println("ӡ��ID�� "+v.getString());
    	return v.getString();
    }
    public String DlgSelectCertId(){
    	Variant v=execActivexMethod( AsspOcx.DlgSelectCertId , new Object[]{} );
    	System.out.println("֤��ID�� "+v.getString());
    	return v.getString();
    }
    /**
     * ��ʼ��ocx�ؼ�
     * @param certId ֤��id
     * @param year ҵ�����
     * @param voucherType ƾ֤����
     * @param stepCode �ڼ���
     * @param operateType ��������
     * @param defaultPage Ĭ��ҵ
     * @param displayMode ��ʾģʽ
     */
    public boolean initStampOcx( String certId,String rgCode, long year,  String voucherType,
    		String stepCode, long operateType, long defaultPage, long displayMode ){

    	//��ʼ��Ĭ����ʾ��
    	joint = (int)defaultPage;
    	
    	//����ocx�ĳ�ʼ������
    	
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

    	//����ocx�ĳ�ʼ������
    	execActivexMethod( AsspOcx.SETURL, new Object[]{ url }   );
    }
    
    /**
     * ��ocx�ؼ������ƾ֤
     * @param voucherNO ƾ֤��
     *
     * @return �Ƿ�ɹ�
     */
   
    public boolean AddVoucherfromServer( String voucherNO ){
    	//����ocx���ƾ֤�ķ���
    	Variant v = execActivexMethod( AsspOcx.AddVoucherfromServer, new Object[]{ voucherNO} );
    	//0��ʾ�ɹ���-1��ʾʧ��
    	if( v.getInt() == 0 ){
    		return true;
    	}else{
    		
    		return false;
    	}
    }
    /**
     * ��ocx�ؼ������ƾ֤
     * @param voucherNO ƾ֤��
     * @param xmlBody ����ָ����ʽƴװ��xml
     * @return �Ƿ�ɹ�
     */
    public boolean addVoucher( String voucherNO, String xmlBody ){
    	//����ocx���ƾ֤�ķ���
    	Variant v = execActivexMethod( AsspOcx.ADDVOUCHER, new Object[]{ voucherNO, xmlBody } );
    	//0��ʾ�ɹ���-1��ʾʧ��
    	if( v.getLong() == 0 ){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * ���ƾ֤��Ϣ
     * @return �Ƿ�����ɹ�
     */
    public boolean clearVouchers(){
    	//����ocx���ƾ֤�ķ���
    	Variant v = execActivexMethod( AsspOcx.CLEARVOUCHER, null );
    	//0��ʾ�ɹ���-1��ʾʧ��
    	if( v.getLong() == 0 ){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * ���õ�ǰ��ʾ��ƾ֤
     * @param voucherNO ƾ֤��
     * @return
     */
    public boolean setCurrentVoucher( String voucherNO ){
    	//���õ�ǰ��ʾ�ķ���
    	Variant v = execActivexMethod( AsspOcx.SETCURRENTVOUCHER, new String[]{ voucherNO } );
    	//0��ʾ�ɹ���-1��ʾʧ��
    	if( v.getInt() == 0 ){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * ��ȡocx��ƾ֤ǩ���������cc00
     * @return ǩ������ַ���
     */
    public String getSignedVouchers(){
      	//���õ�ǰ��ʾ�ķ���
    	Variant v = execActivexMethod( AsspOcx.GETSIGNEDVOUCHERS, null );
    	//0��ʾ�ɹ���-1��ʾʧ��
    	if( null == v ){
    		return null;
    	}else{
    		return v.getString();
    	}
    }
    

    /**
     * ��ȡ�������ģ�������ϡ��˻ء�����ǩ�¡����͡���ӡ�Ȳ���
     * @param certId ֤��id 
     * @param rgCode ����
     * @param setYear ���
     * @param voucherType ƾ֤����
     * @param voucherNO ��Ҫ������ƾ֤����
     * @param opCode �������� ������ϡ��˻ء�����ǩ�¡����͡���ӡ
     * @return �Ա��ĵĲ�����Ϣ
     */
    public String getOperateVouchers(
    		String certId, String rgCode, long setYear,
    		String voucherType, String voucherNO,
    		String opCode ){
       	//ִ��ocx�Բ������ĵ�ǩ��
    	Variant v = execActivexMethod( AsspOcx.GETOPERATEVOUCHRS, 
    			new Object[]{ certId, rgCode, new Long( setYear ),
    			voucherType, voucherNO, opCode} );
    	//0��ʾ�ɹ���-1��ʾʧ��
    	if( null == v ){
    		return null;
    	}else{
    		return v.getString();
    	}
    }
    

    /**
     * ִ��ocx�е�����
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
	 * �����ݽ���ǩ��
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
     * ��ӡ����ƾ֤
     * @param voucherNo
     * @param pageNo
     * @return
     */
    public int printVoucher(String voucherNo, int pageNo){
    	Variant v = this.execActivexMethod(PRINTONEVOUCHER, new Object[]{voucherNo, new Long(joint)});
    	
    	return v.getInt();
    }
    
    
    /**
     * ��ӡ����ƾ֤����̨��ӡ������ʾOCX��
     * @param voucherNo
     * @param pageNo
     * @return 
     */
    public int printVoucher(String certID, String admDivCode, int stYear, String vtCode, String voucherNo){
    	Variant v = this.execActivexMethod(PRINTALLVOUCHER, new Object[]{certID, admDivCode, new Long(stYear), vtCode, new Long(joint), voucherNo});
    	
    	return v.getInt();
    }
   
    
    /**
     * ��ʾ��һ��
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
     * ��ʾ��һ��
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
