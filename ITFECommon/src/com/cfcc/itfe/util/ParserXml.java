package com.cfcc.itfe.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsBankcodeDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;

public class ParserXml implements ElementHandler{
	
	private static Log log = LogFactory.getLog(ParserXml.class);
	List<TsPaybankDto> bankcodeList = new ArrayList<TsPaybankDto>();
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ParserXml parser = new ParserXml();
//		parser.parser5101Xml("F:\\5101.xml","110000000002","1232144.txt","zc");
//		parser.parserBankCodeXml("F:\\bankno.xml");
		String filePath = "F:/test.xml";
		String filePath1 = "F:/bb.xml";
//		String command = "cmd.exe /c C:\WINDOWS\sed\bin\sed.exe -e "s &nbsp;  g" F:/20090611大额.XML>F:/aaa.del";
		parser.callShellWithRes("cmd.exe /c C:\\WINDOWS\\sed\\bin\\sed.exe -e \"s &nbsp; ; g\" " + filePath + ">" + filePath1);
	}

	/**
	 * 解析5101报文（实拨资金业务）
	 * 
	 * @param String
	 *            fileName 文件名称
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            sfilename 文件名称
	 * @param String
	 *            usercode 用户代码
	 * @return List 0、总笔数 1、总金额 2、主记录列表 3、子记录列表
	 * @throws ITFEBizException
	 */
	public List parser5101Xml(String fileName,String sorgcode,String sfilename,String usercode) throws ITFEBizException {
		List<TvPayoutmsgmainDto> mainlist = new ArrayList<TvPayoutmsgmainDto>();
		List<TvPayoutmsgsubDto> sublist = new ArrayList<TvPayoutmsgsubDto>();
		
		String sBillOrg = null;
		String sPayeeBankNo = null; // 收款行行号
		String sEntrustDate = null;
		String sPackNo = null;
		String sTreCode = null;
		int iAllNum = 0; 
		BigDecimal iAllAmt = new BigDecimal("0.00");
		String sPayoutVouType = null;
		
		// 1.分别产生File和SAXReader
		File inputXml = new File(fileName);
		SAXReader saxReader = new SAXReader();// SAXReader 解析xml的
		try {
			// 2. 利用SAXReader对象读取文件的信息，并返回Document对象
			Document doc = saxReader.read(inputXml);
			// 3.利用Document对象对象产生根元素
			Element employees = doc.getRootElement();
			// 4.遍历根元素的内容
			for (Iterator i = employees.elementIterator(); i.hasNext();) {
				// 5.根元素的集合对象来获取子元素
				Element employee = (Element) i.next();
				// 6.遍历子元素内容
				for (Iterator j = employee.elementIterator(); j.hasNext();) {
					// 7.获取子元素节点信息
					Element node = (Element) j.next();
					if("BatchHead5101".equals(node.getName())){
						for(Iterator k = node.elementIterator(); k.hasNext();){
							Element head5101 = (Element) k.next();
							if("BillOrg".equals(head5101.getName())){
								sBillOrg = head5101.getText();
							}
							if("PayeeBankNo".equals(head5101.getName())){
								sPayeeBankNo = head5101.getText();
							}
							if("EntrustDate".equals(head5101.getName())){
								sEntrustDate = head5101.getText();
							}
							if("PackNo".equals(head5101.getName())){
								sPackNo = head5101.getText();
							}
							if("TreCode".equals(head5101.getName())){
								sTreCode = head5101.getText();
							}
							if("AllNum".equals(head5101.getName())){
								iAllNum = Integer.parseInt(head5101.getText());
							}
							if("AllAmt".equals(head5101.getName())){
								iAllAmt = new BigDecimal(head5101.getText());
							}
							if("PayoutVouType".equals(head5101.getName())){
								sPayoutVouType = head5101.getText();
							}
							
						}
					}else if("Bill5101".equals(node.getName())){
						TvPayoutmsgmainDto tmpmaindto = new TvPayoutmsgmainDto();
						
						try {
							tmpmaindto.setSbizno(MsgSeqFacade.getMsgSendSeq());
						} catch (SequenceException e) {
							throw new ITFEBizException("取交易流水号时出现异常！", e);
						}
//						tmpmaindto.setSbizno(Long.valueOf((new java.util.Date().getTime())).toString());//TODO
						tmpmaindto.setSorgcode(sorgcode); // 机构代码
						tmpmaindto.setScommitdate(sEntrustDate); // 委托日期
						tmpmaindto.setSaccdate(TimeFacade.getCurrentStringTime()); // 账务日期
						tmpmaindto.setSfilename(sfilename); // 文件名称
						tmpmaindto.setStrecode(sTreCode); // 国库代码
						tmpmaindto.setSpackageno(sPackNo); // 包序号
						tmpmaindto.setSpayunit(sBillOrg); // 出票单位
						tmpmaindto.setSpayeebankno(sPayeeBankNo); // 收款行行号
						tmpmaindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING); // 交易状态
						tmpmaindto.setSusercode(usercode); // 操作员代码
						
						for(Iterator k = node.elementIterator(); k.hasNext();){
							Element bill5101 = (Element) k.next();
							
							if("Detail5101".equals(bill5101.getName())){
								TvPayoutmsgsubDto tmpsubdto = new TvPayoutmsgsubDto();
								tmpsubdto.setSbizno(tmpmaindto.getSbizno());
								tmpsubdto.setSaccdate(TimeFacade.getCurrentStringTime()); // 账务日期
								
								for(Iterator l = bill5101.elementIterator(); l.hasNext();){
									Element detail5101 = (Element) l.next();
									
									
									if("SeqNo".equals(detail5101.getName())){
										tmpsubdto.setSseqno(Integer.parseInt(detail5101.getText()));
									}
									if("FuncBdgSbtCode".equals(detail5101.getName())){
										tmpsubdto.setSfunsubjectcode(detail5101.getText());
									}
									if("EcnomicSubjectCode".equals(detail5101.getName())){
										tmpsubdto.setSecnomicsubjectcode(detail5101.getText());
									}
									if("BudgetPrjCode".equals(detail5101.getName())){
										tmpsubdto.setSbudgetprjcode(detail5101.getText());
									}
									if("Amt".equals(detail5101.getName())){
										tmpsubdto.setNmoney(new BigDecimal(detail5101.getText()));
									}
								}
								
								sublist.add(tmpsubdto);
							}else{
								if("TraNo".equals(bill5101.getName())){
									tmpmaindto.setSdealno(bill5101.getText());
								}
								if("VouNo".equals(bill5101.getName())){
									tmpmaindto.setStaxticketno(bill5101.getText());
								}
								if("VouDate".equals(bill5101.getName())){
									tmpmaindto.setSgenticketdate(bill5101.getText());
								}
								if("PayerAcct".equals(bill5101.getName())){
									tmpmaindto.setSpayeracct(bill5101.getText());
								}
								if("PayerName".equals(bill5101.getName())){
									tmpmaindto.setSpayername(bill5101.getText());
								}
								if("PayerAddr".equals(bill5101.getName())){
									tmpmaindto.setSpayeraddr((bill5101.getText()));
								}
								if("Amt".equals(bill5101.getName())){
									tmpmaindto.setNmoney(new BigDecimal(bill5101.getText()));
								}
								if("PayeeOpBkNo".equals(bill5101.getName())){
									tmpmaindto.setSrecbankno(bill5101.getText());
									//收款行行号设为收款人开户行账号 20100610 严威提出的需求
									tmpmaindto.setSpayeebankno(bill5101.getText()); // 收款行行号
								}
								if("PayeeAcct".equals(bill5101.getName())){
									tmpmaindto.setSrecacct(bill5101.getText());
								}
								if("PayeeName".equals(bill5101.getName())){
									tmpmaindto.setSrecname(bill5101.getText());
								}
								if("AddWord".equals(bill5101.getName())){
									tmpmaindto.setSaddword(bill5101.getText());
								}
								if("BdgOrgCode".equals(bill5101.getName())){
									tmpmaindto.setSbudgetunitcode(bill5101.getText());
								}
								if("BudgetOrgName".equals(bill5101.getName())){
									tmpmaindto.setSunitcodename(bill5101.getText());
								}
								if("OfYear".equals(bill5101.getName())){
									tmpmaindto.setSofyear(bill5101.getText());
								}
								if("BudgetType".equals(bill5101.getName())){
									tmpmaindto.setSbudgettype(bill5101.getText());
								}
								if("TrimSign".equals(bill5101.getName())){
									tmpmaindto.setStrimflag(bill5101.getText());
								}
								
							}
						}
						
						mainlist.add(tmpmaindto);
					}else{
					}
				}
			}
			
			List rtnList = new ArrayList();
			rtnList.add(0,iAllNum); // 总笔数
			rtnList.add(1,iAllAmt); // 总金额
			rtnList.add(2,mainlist); // 主记录列表
			rtnList.add(3,sublist); // 子记录列表
			
			return rtnList;
		} catch (DocumentException e) {
			throw new ITFEBizException("解析文档出现异常！", e);
		}
	}

	/**
	 * 解析银行code的XML文件
	 * 
	 * @param String
	 *            fileName 文件名称
	 * @return List 银行code列表
	 * @throws ITFEBizException
	 */
	public List parserBankCodeXml(String fileName) throws ITFEBizException {
		
		List list = new ArrayList();
		File inputXml = new File(fileName);
		try {
			start(inputXml);
			
			//银行账号LIST
			list = getBankcodeList();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ITFEBizException("解析文档出现异常,文档可能含有非法字符(<、&&)或格式错误，请修改文档后重新导入！", e);
		}
		return list;
	}
           
    private void start(File file) throws DocumentException {   
        SAXReader reader = new SAXReader();   
        reader.addHandler("/CNAPS_DATA/CNAPS_BANK_DATA/CNAPS_BANK", this);   
        reader.setDefaultHandler(new PruningElementHandler());   
        reader.read(file); 
    }

    public void onStart(ElementPath elementPath) {   
        elementPath.getCurrent().detach();   
    }   
  
    /* (non-Javadoc)
     * @see org.dom4j.ElementHandler#onEnd(org.dom4j.ElementPath)
     */
    public void onEnd(ElementPath elementPath) {   
        Element elm = elementPath.getCurrent();   
  
        /*// TODO process with elm 
		String sBankcode = null;
		String sBankstatus = null;
		String sBankName = null;
		sBankcode = elm.elementText("CNAPS_BANK_BNKCODE"); //银行账号代码
		sBankstatus = elm.elementText("CNAPS_BANK_STATUS"); //银行账号状态
		sBankName = elm.elementText("CNAPS_BANK_LNAME"); //银行账号名称
		TsBankcodeDto bankcodedto = new TsBankcodeDto();
		bankcodedto .setSbnkcode(sBankcode);
		bankcodedto.setSacctstatus(sBankstatus);
		bankcodedto.setSbnkname(sBankName.replace(";", ""));*/
		
		String sbankno=elm.elementText("CNAPS_BANK_BNKCODE");
		String sbankname=elm.elementText("CNAPS_BANK_LNAME");;
		String spaybankno=elm.elementText("CNAPS_BANK_DRECCODE");;
		String sorgcode="000000000000";
		String sstate=elm.elementText("CNAPS_BANK_STATUS");;
		
		TsPaybankDto dto=new TsPaybankDto();
		dto.setSbankno(sbankno);
		dto.setSbankname(sbankname);
		dto.setSpaybankno(spaybankno);
		dto.setSorgcode(sorgcode);
		dto.setSstate(sstate);
		
		bankcodeList.add(dto);

        elm.detach();   
        elm = null;   
    }

    private List getBankcodeList()
    {
    	return bankcodeList;
    }
    /**
	 * 带返回结果集的CallShell
	 * 
	 * @param command
	 *            调用的shell命令
	 * @return 调用信息
	 * @throws Exception
	 */
	public static byte[] callShellWithResAndReplace(String command) throws Exception {

		try {

			log.debug("Shell:" + command);

			Process child = Runtime.getRuntime().exec(command);

			InputStream in = child.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes = new byte[64 * 1024];
			int length = 0; // read content
			for (int i = in.read(bytes); i != -1; i = in.read(bytes)) {

				out.write(bytes, 0, i);

				length += i;
			}
			in.close();

			child.waitFor();
			return out.toByteArray();
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e.toString());

		}

	}
	/**
	 * 带返回结果集的CallShell
	 * 
	 * @param command
	 *            调用的shell命令
	 * @return 调用信息
	 * @throws Exception
	 */
	private byte[] callShellWithRes(String command) throws Exception {

		try {

				log.debug("Shell:" + command);
	
				Process child = Runtime.getRuntime().exec(command);
	
		        String line = null;   
		        StringBuffer result = new StringBuffer();  
		        BufferedReader brIn = new BufferedReader(new InputStreamReader(child.getInputStream()));   
				InputStream in = child.getInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		        while ((line = brIn.readLine()) != null)   
		        {   
		            result.append(line + "\n");   
		        }   
		        brIn.close();   
	
		        BufferedReader brErr = new BufferedReader(new InputStreamReader(child.getErrorStream()));   
		        while ((line = brErr.readLine()) != null)   
		        {   
		            result.append(line + "\n");   
		        }   
		        brErr.close();
		        return result.toString().getBytes();   
	        } catch (Exception e) {
	        	log.error(e);
	        	throw new Exception(e.toString());
		}

	}
	
}

class PruningElementHandler implements ElementHandler {   
    public final void onStart(ElementPath elementPath) {   
        elementPath.getCurrent().detach();   
    }   
  
    public void onEnd(ElementPath elementPath) {   
        Element elem = elementPath.getCurrent();   
        elem.detach();   
        elem = null;   
    }   
}  

