package com.cfcc.itfe.voucher.sxservice;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbackSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.xmlparse.IXmlParser;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.sn.dep.IMApplication;


public class VoucherReadShanXiService extends IMApplication {

	private static Log logger = LogFactory.getLog(VoucherReadShanXiService.class);
	
//	private static String filepath = "C:/config.properties";
	
	public VoucherReadShanXiService() {
//		super(filepath);
	}
	
	/**
	 * 读取凭证类
	 * @param 单据实体类型编码
	 * @param 时间戳
	 */
	public int ReadVoucher(String vtcode, String orgcode)
	{
		int totalCount = 0;
		//当前时间YYYYMMDD
		String currentDate = TimeFacade.getCurrentStringTime();
		//当前年份
		String year = currentDate.substring(0, 4);
		logger.debug("=====陕西财政统一银行接口凭证读取开始=====");
		//用户登陆
		try 
		{
//			login(String user, String pwd, String year, String spid, String appid)
//			login("caizheng","1",year,"renhang","renhang");
			boolean login_flag= login("caizheng","1",year,"renhang","renhang");
			if (!login_flag) {
				logger.debug("陕西财政统一银行接口,登录失败......");
				return 0;
			}
			if(StringUtils.isBlank(vtcode))
	    	{
				totalCount += getCZVoucher(MsgConstant.VOUCHER_NO_2301,orgcode,currentDate);
				totalCount += getCZVoucher(MsgConstant.VOUCHER_NO_2302,orgcode,currentDate);
				totalCount += getCZVoucher(MsgConstant.VOUCHER_NO_5108,orgcode,currentDate);
				totalCount += getCZVoucher(MsgConstant.VOUCHER_NO_5106,orgcode,currentDate);
				totalCount += getCZVoucher(MsgConstant.VOUCHER_NO_5207,orgcode,currentDate);
	    	}else
	    	{
	    		totalCount = getCZVoucher(vtcode,orgcode,currentDate);
	    	}
		} catch (Exception e){
			logger.error("调用陕西财政统一银行接口登陆异常:"+e);
			logout();
			VoucherException.saveErrInfo(vtcode, "[调用陕西财政统一银行接口登陆异常]"+e);
		}
		//用户退出
		logout();
		return totalCount;
	}

	/**
	 * 读取某一种凭证类
	 */
	private int getCZVoucher(String vtcode, String orgcode, String currentDate) throws ITFEBizException
	{
		int returnInt = 0;
		//获取报文的起始时间参数
		String dateTimepram = null;
		//设定初始时间  
		String initialTime = "2014-01-01 00:00:00";
		// 首先得到进行XML解析服务的类实例
		IXmlParser xmlParser = (IXmlParser) ContextFactory.getApplicationContext().getBean(MsgConstant.SPRING_SXXMLPRA_SERVER + transferBizCode(vtcode));
		//查询数据库已有凭证数据，用于判断是否第一次读取该业务类型凭证，数据库中存在该业务类型数据代表曾经读取过该业务类型凭证，反之，第一次读取。
		TvVoucherinfoSxDto qdto = new TvVoucherinfoSxDto();
		qdto.setSvtcode(vtcode);
		List<TvVoucherinfoSxDto> isExcist;
		try 
		{
			isExcist = CommonFacade.getODB().findRsByDto(qdto);
		} catch (Exception e) 
		{
			logger.error("查询财政索引表["+vtcode+"]凭证异常:"+e);
			VoucherException.saveErrInfo(vtcode, "查询财政索引表"+vtcode+"凭证异常:"+e);
			return 0;
		}
			if(isExcist != null && isExcist.size() > 0)  //曾经读取过凭证
			{
				//进行排序，取出最大时间，并将接收时间为null的数据放在最后面
				Collections.sort(isExcist, new Comparator<TvVoucherinfoSxDto>()  
						{
							public int compare(TvVoucherinfoSxDto dto1, TvVoucherinfoSxDto dto2) 
							{
								if(dto2.getSrecvtime() != null && dto1.getSrecvtime() != null)
								{
									return dto2.getSrecvtime().compareTo(dto1.getSrecvtime());
								}else if(dto1.getSrecvtime() == null && dto2.getSrecvtime() != null)
								{
									return 1;
								}else if(dto1.getSrecvtime() != null && dto2.getSrecvtime() == null)
								{
									return -1;
								}else
								{
									return 0;
								}
							}
				        }
				);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateTimepram = dateFormat.format(isExcist.get(0).getSrecvtime());
			}else  //第一次读取
			{
				dateTimepram = initialTime;
			}
			//调用按时间戳查询数据接口,返回格式为xml字符串
			logger.info("=====读取陕西财政业务类型["+vtcode+"]:["+transferBizCode(vtcode)+"]时间：["+ dateTimepram +"]之后的数据=====");
			String resByDateXml  = getByDate(transferBizCode(vtcode),dateTimepram,"");
			if (resByDateXml.trim().length()<=0) {
				logger.info("陕西财政统一银行接口凭证类型["+vtcode+"]未获取到原始报文");
				return 0;
			}
			if(resByDateXml.indexOf("error") > 0)
			{
				logger.error("=====读取陕西财政统一银行接口,凭证报文["+vtcode+"]查无数据=====");
				return 0;
			}
			logger.info("陕西财政统一银行接口凭证类型["+vtcode+"]原始报文：");	
			logger.info("报文内容："+resByDateXml);
			//开始解析组装数据进行入库
			if(!"".equals(resByDateXml) && resByDateXml !=null){
				resByDateXml = resByDateXml.replaceAll("<MOF xmlns=\"mof:sn:etti\">", "<MOF>");
				String dirsep = File.separator; 
				
				String FileName = ITFECommonConstant.FILE_ROOT_PATH
									+ dirsep
									+ "VoucherSXCZ"
									+ dirsep
									+ currentDate
									+ dirsep
									+ "rev"
									+ vtcode
									+ "_"
									+ new SimpleDateFormat("yyyyMMddhhmmssSSS")
											.format(System.currentTimeMillis())
									+ ".msg";
				Document fxrDoc = null;
				try
				{
				  fxrDoc = DocumentHelper.parseText(resByDateXml);
				} catch(DocumentException e)
				{
					logger.error("解析陕西财政统一银行接口"+vtcode+"凭证报文出现异常:"+e);
					VoucherException.saveErrInfo(vtcode, "解析陕西财政统一银行接口"+vtcode+"凭证报文出现异常:"+e);
					return 0;
				}
				Document fxrDocVoucher = null;
				List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
				//凭证条数
				returnInt = Integer.parseInt(fxrDoc.selectSingleNode("MOF").selectSingleNode("VoucherCount").getText());
				HashMap map = new HashMap();
				HashMap<String, String> dealnomap = new HashMap<String, String>();
				TvVoucherinfoSxDto voucherDto = new TvVoucherinfoSxDto();
				for (int i = 0; i < listNodes.size(); i++) {
					try
					{
						//区划代码
						String AdmDiveCode = ((Element) listNodes.get(i)).attribute("AdmDivCode").getText();
						//年度
						String StYear = ((Element) listNodes.get(i)).attribute("StYear").getText();
						//凭证类型
						String VtCode = ((Element) listNodes.get(i)).attribute("VtCode").getText();
						//凭证编号
						String VoucherNo = ((Element) listNodes.get(i)).attribute("VoucherNo").getText();
						//时间戳
						String TimeStamp = ((Element) listNodes.get(i)).attribute("TimeStamp").getText();
						Node voucherNode = ((Node) listNodes.get(i)).selectSingleNode("Voucher");
						String Voucher = voucherNode.asXML();
						logger.info("陕西财政统一银行接口凭证类型"+vtcode+"凭证编号"+VoucherNo+"解析报文：\r\n\t"+Voucher);
						String TreCode = voucherNode.selectSingleNode("TreCode").getText();
						String billOrgCode = VoucherUtil.getSpaybankcode(VtCode, voucherNode);//出票单位
						String Total =VoucherUtil.getTotalAmt(VtCode, voucherNode);		
						String mainvou = VoucherUtil.getGrantSequence();										
						dealnomap.put(VoucherNo, mainvou);										
						voucherDto = new TvVoucherinfoSxDto();
						voucherDto.setSdealno(mainvou);
						voucherDto.setNmoney(MtoCodeTrans.transformBigDecimal(Total));
						voucherDto.setSadmdivcode(AdmDiveCode);
						voucherDto.setScreatdate(currentDate);
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						voucherDto.setSrecvtime(new Timestamp(sf.parse(TimeStamp).getTime()));
						voucherDto.setSfilename(FileName);
						voucherDto.setSorgcode(orgcode);
						voucherDto.setSstatus(DealCodeConstants.VOUCHER_ACCEPT);
						voucherDto.setSdemo("解析报文异常");
						voucherDto.setSstyear(StYear);
						voucherDto.setStrecode(TreCode);
						voucherDto.setSvoucherno(VoucherNo);
						voucherDto.setSvoucherflag("0");
						voucherDto.setSvtcode(VtCode);
						voucherDto.setSpaybankcode(billOrgCode); //出票单位
						DatabaseFacade.getDb().create(voucherDto);
					} catch (Exception e) 
					{
						logger.error("解析陕西财政统一银行接口,凭证报文["+vtcode+"]报文不规范出现异常:"+e);
						VoucherException.saveErrInfo(vtcode, "解析陕西财政统一银行接口,凭证报文["+vtcode+"]报文不规范出现异常:"+e);
						returnInt--;
						continue;
					} 
				}
				map.put("orgcode", orgcode);
				map.put("filename", FileName);
				map.put("dealnomap", dealnomap);
				String voucherXml = fxrDoc.asXML().replaceAll("&lt;", "<").replaceAll("&gt;", ">");
				try 
				{
					FileUtil.getInstance().writeFile(FileName, voucherXml);
				} catch (Exception e) 
				{
					logger.error("保存"+vtcode+"凭证报文文件出现异常:"+e);
					VoucherException.saveErrInfo(vtcode, "保存"+vtcode+"凭证报文文件出现异常:"+e);
					logout();
					throw new ITFEBizException("保存"+vtcode+"凭证报文文件出现异常！");
				}
				xmlParser.dealMsg(map, voucherXml);
			}
		
		return returnInt;
	}
	/**
     * 根据凭证库业务类型转换为陕西财政单据实体类型
     * @param vtcode
     * @return
     * @throws ITFEBizException 
     */
    public String transferBizCode(String vtcode) throws ITFEBizException{
    	String czBizcode="";
    	if(MsgConstant.VOUCHER_NO_5207.equals(vtcode)){
    		czBizcode = "C025";
    	}else if(MsgConstant.VOUCHER_NO_5106.equals(vtcode)){
    		czBizcode = "C017";
    	}else if(MsgConstant.VOUCHER_NO_5108.equals(vtcode)){
    		czBizcode = "C021";
    	}else if(MsgConstant.VOUCHER_NO_2301.equals(vtcode)){
    		czBizcode = "C026";
    	}else if(MsgConstant.VOUCHER_NO_2302.equals(vtcode)){
    		czBizcode = "C027";
    	}else{
    		throw new ITFEBizException("陕西财政统一银行接口没有此报文类型");
    	}
    	return czBizcode;
    }
/**
 * 发送回单
 * @param checkList
 * @throws ITFEBizException
 */
    public void sendReturnVoucher(List checkList) throws ITFEBizException
    {
    	//当前时间YYYYMMDD
		String currentDate = TimeFacade.getCurrentStringTime();
		//当前年份
		String year = currentDate.substring(0, 4);
		logger.debug("============陕西财政统一银行接口发送回单开始==============");
		try 
		{
			//用户登陆
			
//			login("caizheng","1",year,"renhang","renhang");
			boolean login_flag=login("caizheng","1",year,"renhang","renhang");
			if (!login_flag) {
				logger.debug("陕西财政统一银行接口,登录失败......");
				return;
			}
//			Document successDoc = DocumentHelper.createDocument();
//			successDoc.setXMLEncoding("UTF-8");
//			Element root = successDoc.addElement("MOF");
			Element root = DocumentHelper.createElement("MOF");
			Element VoucherCount = root.addElement("VoucherCount");
			VoucherCount.setText(String.valueOf(checkList.size()));
			String vtcode = ((TvVoucherinfoSxDto)checkList.get(0)).getSvtcode();
			for(int i = 0; i < checkList.size(); i++)
			{
				TvVoucherinfoSxDto curentdto = (TvVoucherinfoSxDto)checkList.get(i);
				String sourceXML = FileUtil.getInstance().readFile(curentdto.getSfilename());
				Document fxrDoc = DocumentHelper.parseText(sourceXML);
				List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
				for(int j = 0; j < listNodes.size(); j++)
				{
					String VoucherNo = ((Element) listNodes.get(j)).attribute("VoucherNo").getText();
					if (VoucherNo.equals(curentdto.getSvoucherno())) 
				    {
						Element voucherbody = (Element) listNodes.get(j);
						Node voucherNode = voucherbody.selectSingleNode("Voucher");
						if (MsgConstant.VOUCHER_NO_2301.equals(curentdto.getSvtcode())) 
						{
							TvPayreckbankSxDto mainDto = new TvPayreckbankSxDto();
							mainDto.setSvouno(curentdto.getSvoucherno());
							mainDto.setIvousrlno(Long.valueOf(curentdto.getSdealno()));
							mainDto.setStrecode(curentdto.getStrecode());
							List <TvPayreckbankSxDto> list  = CommonFacade.getODB().findRsByDto(mainDto);
							if (null!=list && list.size()>0) {
								mainDto = list.get(0);
							}else{
								logout();
								throw new ITFEBizException("查询业务主表信息无数据！");
							}
							Date xpaydate = mainDto.getSxcleardate();
							if (null == xpaydate) {
								xpaydate =mainDto.getDentrustdate();
							}
							String XPayDate = TimeFacade.formatDate(xpaydate, "yyyyMMdd");
							BigDecimal XPayAmt = mainDto.getSxpayamt();
							if(ITFECommonConstant.ISCHECKPAYPLAN.equals("1")){
								 XPayDate = TimeFacade.getCurrentStringTime();
								 XPayAmt = curentdto.getNmoney();
							}
							voucherNode.selectSingleNode("XPayAmt").setText(
									XPayAmt + "");
							voucherNode.selectSingleNode("XClearDate").setText(
									XPayDate + "");
							voucherNode.selectSingleNode("VouDate").setText(
									TimeFacade.formatDate(mainDto.getDvoudate(), "yyyyMMdd") + "");
						} else if (MsgConstant.VOUCHER_NO_2302.equals(curentdto.getSvtcode())) 
						{
							TvPayreckbankbackSxDto mainDto = new TvPayreckbankbackSxDto();
							mainDto.setSvouno(curentdto.getSvoucherno());
							mainDto.setIvousrlno(Long.valueOf(curentdto.getSdealno()));
							mainDto.setStrecode(curentdto.getStrecode());
							List <TvPayreckbankbackSxDto> list  = CommonFacade.getODB().findRsByDto(mainDto);
							if (null!=list && list.size()>0) {
								mainDto = list.get(0);
							}else{
								logout();
								throw new ITFEBizException("查询业务主表信息无数据！");
							}
							Date xpaydate = mainDto.getSxcleardate();
							if (null == xpaydate) {
								xpaydate =mainDto.getDentrustdate();
							}
							String XPayDate = TimeFacade.formatDate(xpaydate, "yyyyMMdd");
							BigDecimal XPayAmt = mainDto.getSxpayamt();
							String xpayamt = "";
							if(ITFECommonConstant.ISCHECKPAYPLAN.equals("1")){
								 XPayDate = TimeFacade.getCurrentStringTime();
								 XPayAmt = curentdto.getNmoney();
							}
							Double dpayamt = Math.abs(Double.valueOf(XPayAmt.toString()));
							xpayamt ="-"+ new DecimalFormat("#.00").format(dpayamt);
							voucherNode.selectSingleNode("XPayAmt").setText(
									xpayamt + "");
							voucherNode.selectSingleNode("XClearDate").setText(
									XPayDate + "");
							voucherNode.selectSingleNode("VouDate").setText(
									TimeFacade.formatDate(mainDto.getDvoudate(), "yyyyMMdd") + "");
						}
						logger.debug("财政每笔凭证通知单报文："+voucherbody.asXML());
						root.add((Element)voucherbody.clone());
						break;
				    }
				}
			}
			String putStr = root.asXML();
			logger.debug("发送财政凭证报文："+putStr);
			String back = put(transferBizCode(vtcode),putStr);
			logger.debug("财政返回报文："+back);
			if(back.indexOf("error") > 0)
			{
				logout();
				throw new ITFEBizException("发送回单出现异常，异常原因：在统一银行接口平台中查无数据！");
			}
			//退出
			logout();
		} catch (Exception e) 
		{
			logger.error("调用陕西财政统一银行接口登陆异常:"+e);
			logout();
			throw new ITFEBizException("调用陕西财政统一银行接口登陆异常！");
		}
    }

@Override
public void handleMessage(Message arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void handleMessage(Message arg0, String arg1) {
	// TODO Auto-generated method stub
	
}

@Override
public void handleRequest(Packet arg0) {
	// TODO Auto-generated method stub
	
}

}
