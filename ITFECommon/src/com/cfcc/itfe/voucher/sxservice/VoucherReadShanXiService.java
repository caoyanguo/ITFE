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
	 * ��ȡƾ֤��
	 * @param ����ʵ�����ͱ���
	 * @param ʱ���
	 */
	public int ReadVoucher(String vtcode, String orgcode)
	{
		int totalCount = 0;
		//��ǰʱ��YYYYMMDD
		String currentDate = TimeFacade.getCurrentStringTime();
		//��ǰ���
		String year = currentDate.substring(0, 4);
		logger.debug("=====��������ͳһ���нӿ�ƾ֤��ȡ��ʼ=====");
		//�û���½
		try 
		{
//			login(String user, String pwd, String year, String spid, String appid)
//			login("caizheng","1",year,"renhang","renhang");
			boolean login_flag= login("caizheng","1",year,"renhang","renhang");
			if (!login_flag) {
				logger.debug("��������ͳһ���нӿ�,��¼ʧ��......");
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
			logger.error("������������ͳһ���нӿڵ�½�쳣:"+e);
			logout();
			VoucherException.saveErrInfo(vtcode, "[������������ͳһ���нӿڵ�½�쳣]"+e);
		}
		//�û��˳�
		logout();
		return totalCount;
	}

	/**
	 * ��ȡĳһ��ƾ֤��
	 */
	private int getCZVoucher(String vtcode, String orgcode, String currentDate) throws ITFEBizException
	{
		int returnInt = 0;
		//��ȡ���ĵ���ʼʱ�����
		String dateTimepram = null;
		//�趨��ʼʱ��  
		String initialTime = "2014-01-01 00:00:00";
		// ���ȵõ�����XML�����������ʵ��
		IXmlParser xmlParser = (IXmlParser) ContextFactory.getApplicationContext().getBean(MsgConstant.SPRING_SXXMLPRA_SERVER + transferBizCode(vtcode));
		//��ѯ���ݿ�����ƾ֤���ݣ������ж��Ƿ��һ�ζ�ȡ��ҵ������ƾ֤�����ݿ��д��ڸ�ҵ���������ݴ���������ȡ����ҵ������ƾ֤����֮����һ�ζ�ȡ��
		TvVoucherinfoSxDto qdto = new TvVoucherinfoSxDto();
		qdto.setSvtcode(vtcode);
		List<TvVoucherinfoSxDto> isExcist;
		try 
		{
			isExcist = CommonFacade.getODB().findRsByDto(qdto);
		} catch (Exception e) 
		{
			logger.error("��ѯ����������["+vtcode+"]ƾ֤�쳣:"+e);
			VoucherException.saveErrInfo(vtcode, "��ѯ����������"+vtcode+"ƾ֤�쳣:"+e);
			return 0;
		}
			if(isExcist != null && isExcist.size() > 0)  //������ȡ��ƾ֤
			{
				//��������ȡ�����ʱ�䣬��������ʱ��Ϊnull�����ݷ��������
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
			}else  //��һ�ζ�ȡ
			{
				dateTimepram = initialTime;
			}
			//���ð�ʱ�����ѯ���ݽӿ�,���ظ�ʽΪxml�ַ���
			logger.info("=====��ȡ��������ҵ������["+vtcode+"]:["+transferBizCode(vtcode)+"]ʱ�䣺["+ dateTimepram +"]֮�������=====");
			String resByDateXml  = getByDate(transferBizCode(vtcode),dateTimepram,"");
			if (resByDateXml.trim().length()<=0) {
				logger.info("��������ͳһ���нӿ�ƾ֤����["+vtcode+"]δ��ȡ��ԭʼ����");
				return 0;
			}
			if(resByDateXml.indexOf("error") > 0)
			{
				logger.error("=====��ȡ��������ͳһ���нӿ�,ƾ֤����["+vtcode+"]��������=====");
				return 0;
			}
			logger.info("��������ͳһ���нӿ�ƾ֤����["+vtcode+"]ԭʼ���ģ�");	
			logger.info("�������ݣ�"+resByDateXml);
			//��ʼ������װ���ݽ������
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
					logger.error("������������ͳһ���нӿ�"+vtcode+"ƾ֤���ĳ����쳣:"+e);
					VoucherException.saveErrInfo(vtcode, "������������ͳһ���нӿ�"+vtcode+"ƾ֤���ĳ����쳣:"+e);
					return 0;
				}
				Document fxrDocVoucher = null;
				List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
				//ƾ֤����
				returnInt = Integer.parseInt(fxrDoc.selectSingleNode("MOF").selectSingleNode("VoucherCount").getText());
				HashMap map = new HashMap();
				HashMap<String, String> dealnomap = new HashMap<String, String>();
				TvVoucherinfoSxDto voucherDto = new TvVoucherinfoSxDto();
				for (int i = 0; i < listNodes.size(); i++) {
					try
					{
						//��������
						String AdmDiveCode = ((Element) listNodes.get(i)).attribute("AdmDivCode").getText();
						//���
						String StYear = ((Element) listNodes.get(i)).attribute("StYear").getText();
						//ƾ֤����
						String VtCode = ((Element) listNodes.get(i)).attribute("VtCode").getText();
						//ƾ֤���
						String VoucherNo = ((Element) listNodes.get(i)).attribute("VoucherNo").getText();
						//ʱ���
						String TimeStamp = ((Element) listNodes.get(i)).attribute("TimeStamp").getText();
						Node voucherNode = ((Node) listNodes.get(i)).selectSingleNode("Voucher");
						String Voucher = voucherNode.asXML();
						logger.info("��������ͳһ���нӿ�ƾ֤����"+vtcode+"ƾ֤���"+VoucherNo+"�������ģ�\r\n\t"+Voucher);
						String TreCode = voucherNode.selectSingleNode("TreCode").getText();
						String billOrgCode = VoucherUtil.getSpaybankcode(VtCode, voucherNode);//��Ʊ��λ
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
						voucherDto.setSdemo("���������쳣");
						voucherDto.setSstyear(StYear);
						voucherDto.setStrecode(TreCode);
						voucherDto.setSvoucherno(VoucherNo);
						voucherDto.setSvoucherflag("0");
						voucherDto.setSvtcode(VtCode);
						voucherDto.setSpaybankcode(billOrgCode); //��Ʊ��λ
						DatabaseFacade.getDb().create(voucherDto);
					} catch (Exception e) 
					{
						logger.error("������������ͳһ���нӿ�,ƾ֤����["+vtcode+"]���Ĳ��淶�����쳣:"+e);
						VoucherException.saveErrInfo(vtcode, "������������ͳһ���нӿ�,ƾ֤����["+vtcode+"]���Ĳ��淶�����쳣:"+e);
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
					logger.error("����"+vtcode+"ƾ֤�����ļ������쳣:"+e);
					VoucherException.saveErrInfo(vtcode, "����"+vtcode+"ƾ֤�����ļ������쳣:"+e);
					logout();
					throw new ITFEBizException("����"+vtcode+"ƾ֤�����ļ������쳣��");
				}
				xmlParser.dealMsg(map, voucherXml);
			}
		
		return returnInt;
	}
	/**
     * ����ƾ֤��ҵ������ת��Ϊ������������ʵ������
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
    		throw new ITFEBizException("��������ͳһ���нӿ�û�д˱�������");
    	}
    	return czBizcode;
    }
/**
 * ���ͻص�
 * @param checkList
 * @throws ITFEBizException
 */
    public void sendReturnVoucher(List checkList) throws ITFEBizException
    {
    	//��ǰʱ��YYYYMMDD
		String currentDate = TimeFacade.getCurrentStringTime();
		//��ǰ���
		String year = currentDate.substring(0, 4);
		logger.debug("============��������ͳһ���нӿڷ��ͻص���ʼ==============");
		try 
		{
			//�û���½
			
//			login("caizheng","1",year,"renhang","renhang");
			boolean login_flag=login("caizheng","1",year,"renhang","renhang");
			if (!login_flag) {
				logger.debug("��������ͳһ���нӿ�,��¼ʧ��......");
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
								throw new ITFEBizException("��ѯҵ��������Ϣ�����ݣ�");
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
								throw new ITFEBizException("��ѯҵ��������Ϣ�����ݣ�");
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
						logger.debug("����ÿ��ƾ֤֪ͨ�����ģ�"+voucherbody.asXML());
						root.add((Element)voucherbody.clone());
						break;
				    }
				}
			}
			String putStr = root.asXML();
			logger.debug("���Ͳ���ƾ֤���ģ�"+putStr);
			String back = put(transferBizCode(vtcode),putStr);
			logger.debug("�������ر��ģ�"+back);
			if(back.indexOf("error") > 0)
			{
				logout();
				throw new ITFEBizException("���ͻص������쳣���쳣ԭ����ͳһ���нӿ�ƽ̨�в������ݣ�");
			}
			//�˳�
			logout();
		} catch (Exception e) 
		{
			logger.error("������������ͳһ���нӿڵ�½�쳣:"+e);
			logout();
			throw new ITFEBizException("������������ͳһ���нӿڵ�½�쳣��");
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
