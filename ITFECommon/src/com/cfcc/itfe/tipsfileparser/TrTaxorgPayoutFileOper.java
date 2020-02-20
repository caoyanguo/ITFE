package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@SuppressWarnings("unchecked")
public class TrTaxorgPayoutFileOper extends AbstractTipsFileOper{
	private static Log log = LogFactory.getLog(TrTaxorgPayoutFileOper.class);
	@SuppressWarnings("deprecation")
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {		
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// 业务类型
		mulitDto.setSbookorgcode(sbookorgcode);// 核算主体代码
		List<IDto> freedtos = new ArrayList<IDto>();
		TrTaxorgPayoutReportDto inputDto = (TrTaxorgPayoutReportDto)paramdto;
		if(file.contains("调整期")){
			inputDto.setStrimflag(MsgConstant.TIME_FLAG_TRIM);
		}else{
			inputDto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL);
		}
		if(file!=null&&file.endsWith(".xml"))
			return fileParse(file, freedtos, mulitDto, inputDto);
		if(match(file.substring(file.lastIndexOf(File.separator)+1, file.length())))
			return fileParser(file, mulitDto, freedtos, inputDto);
		try {
			String sbillType = null ;
			String sbillDate = null;
			List<String[]> fileContent = super.readFile(file, " ");
			for (int i = 0; i < fileContent.size(); i++) {
				if (i==0) {//第0行取报表种类
					String[] cols = fileContent.get(i);
					String titleStr = StringUtils.concatenate(cols);
					// 财政机构代码缓存 key为国库代码
					HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade.cacheFincInfo(null);
					if (titleStr.contains(("一般预算支出日报表"))  ) {//一般预算支出报表 赋值 1
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_1;
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					}else if(titleStr.contains("实拨资金日报表")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_2;
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					}else if(titleStr.contains("调拨支出日报表")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_3;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					}else if(titleStr.contains("直接支付日报表")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_4;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else if(titleStr.contains("授权支付日报表")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_5;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else if(titleStr.contains("直接支付退回日报表")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_6;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else if(titleStr.contains("授权支付退回日报表")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_7;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else{
						throw new ITFEBizException("选择的导入文件有误，请查证！");
					}									
					inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					if(titleStr.contains("功能分类")){
						inputDto.setSpaytype("0");
					}else if(titleStr.contains("经济分类")){
						inputDto.setSpaytype("1");
					}
					//第1行取辖属标志和预算类型
					String[] cols1 = fileContent.get(i+1);
					String titleStr1 = StringUtils.concatenate(cols1);
					if(titleStr1.contains("本级")){
						inputDto.setSbelongflag(MsgConstant.RULE_SIGN_SELF);
					}else if(titleStr1.contains("全辖")){
						inputDto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
					}
					
					if(titleStr1.contains("预算内")){
						inputDto.setSbudgettype(MsgConstant.BDG_KIND_IN);
					}else if(titleStr1.contains("预算外")){
						inputDto.setSbudgettype(MsgConstant.BDG_KIND_OUT);
					}
					
					String[] cols2 = fileContent.get(i+2);
					for (int j = 0; j < cols2.length; j++) {
						if (cols2[j].contains("年")) {
							String year = cols2[j].substring(0,cols2[j].indexOf("年"));
							String month = cols2[j].substring(cols2[j].indexOf("年")+1,cols2[j].indexOf("月"));
							String day = cols2[j].substring(cols2[j].indexOf("月")+1,cols2[j].indexOf("日"));
							if(month!=null&&month.length()!=2)
								month = "0"+month;
							if(day!=null&&day.length()!=2)
								day = "0"+day;
							sbillDate = year+month+day;
							if (!inputDto.getSrptdate().equals(sbillDate)) {
								throw new ITFEBizException("输入的报表日期"+inputDto.getSrptdate()+"与文件解析的报表日期"+sbillDate+"不符！");
							}
						}
					}
					do{
						++i;//i += 4;	//定位报表第一行            例如： 科目代码    科目名称    本日发生额    本月累计   本年累计
					}while(fileContent.get(i)!=null&&!fileContent.get(i)[0].contains("科目代码"));
					++i;	
				}
				String[] cols = fileContent.get(i);
				if (StringUtils.concatenate(cols).contains("日报表")) {
					do{
						++i;//i += 4;	//定位报表第一行            例如： 科目代码    科目名称    本日发生额    本月累计   本年累计
					}while(fileContent.get(i)!=null&&!fileContent.get(i)[0].contains("科目代码"));
				    cols = fileContent.get(++i);
				}
				if(StringUtils.concatenate(cols).contains("合计数")){//如果找到合计数字样 代表结束     例如：合计数    "286,666.25"    "299,666.25"   "299,666.25"
					i = fileContent.size();		//跳出循环
					continue;
				}
				StringBuffer rows = new StringBuffer();
				for(int x = 0 ; x < cols.length ; x ++){
					if(cols[x].trim().length() != 0){
						rows.append(cols[x].replaceAll("\"", "").replaceAll(",", "").trim()).append(",");
					}
				}
				String[] row = rows.toString().split(",");
				if(row.length == 5){
					BigDecimal yearAmt = null;
					BigDecimal monAmt = null;
					BigDecimal dayAmt = null;
					yearAmt = new BigDecimal(row[row.length -1].replaceAll("\"", "").replaceAll(",", ""));
					monAmt = new BigDecimal(row[row.length -2].replaceAll("\"", "").replaceAll(",", ""));
					dayAmt = new BigDecimal(row[row.length -3].replaceAll("\"", "").replaceAll(",", ""));
					TrTaxorgPayoutReportDto dto = new TrTaxorgPayoutReportDto();
					dto.setStrecode(inputDto.getStrecode());
					dto.setSfinorgcode(inputDto.getSfinorgcode());
					dto.setStaxorgcode(sbillType);
					dto.setSbudgettype(inputDto.getSbudgettype());
					dto.setSrptdate(inputDto.getSrptdate());
					dto.setSbudgetlevelcode(inputDto.getSbudgetlevelcode());
					dto.setSbudgetsubcode(row[0]);
					dto.setSbudgetsubname(row[1]);
					dto.setStrimflag(inputDto.getStrimflag());
					dto.setSpaytype(inputDto.getSpaytype());
					dto.setSbelongflag(inputDto.getSbelongflag());
					dto.setNmoneyday(dayAmt);
					dto.setNmoneymonth(monAmt);
					dto.setNmoneyyear(yearAmt);
					dto.setNmoneytenday(new BigDecimal("0.00"));
					dto.setNmoneyquarter(new BigDecimal("0.00"));
					freedtos.add(dto);
				//四栏式报表，如果是统计项内容（即数据列数不为5时）	
				}else if(i > 3 && row.length != 5){
					continue;
				}
				else{	
					throw new ITFEBizException("导入文件格式不准确，系统仅支持四栏式报表的导入,请检查!");
				}					
			}
			TrTaxorgPayoutReportDto  tmpdto = new TrTaxorgPayoutReportDto();
			tmpdto.setStrecode(inputDto.getStrecode());
			tmpdto.setSrptdate(inputDto.getSrptdate());
			tmpdto.setStaxorgcode(sbillType);//
			CommonFacade.getODB().deleteRsByDto(tmpdto);//删除重复数据
		} catch (FileOperateException e) {
			throw new ITFEBizException("报表解析异常",e);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("查询报表出现异常",e);
		}catch(ITFEBizException e){
			throw new ITFEBizException(e.getMessage(),e);
		}catch(Exception e){
			throw new ITFEBizException("选择的导入文件格式有误，请查证！");
		}
		mulitDto.setFatherDtos(freedtos);
		saveDownloadReportCheck(inputDto.getSrptdate(),inputDto.getStrecode());//保存下载报表情况检查
		return mulitDto;		
	}
	
	/**
	 * 校验集中支付日报表导入文件命名规则： 
	 * [银行代码(12位数字)_支付种类(1-直接支付 2授权支付 3直接支付退回 4授权支付退 回)+”.CSV”]
	 * @param fileName
	 * @return
	 */
	public boolean match(String fileName){
		if(fileName==null||fileName.length()!=18||!fileName.endsWith(".csv"))
			return false;			
		if(!(fileName.substring(13, 14).equals("1")||fileName.substring(13, 14).equals("2")||
				fileName.substring(13, 14).equals("3")||fileName.substring(13, 14).equals("4")))
			return false;
		Pattern pattern=Pattern.compile("[0-9]{12}");//匹配12位数字
		Matcher match=pattern.matcher(fileName.substring(0, 12));
		return match.matches()&&fileName.indexOf("_")!=-1;	
	}
	
	/**
	 * 解析集中支付报表导入
	 * @param file
	 * @param mulitDto
	 * @param freedtos
	 * @param inputDto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParser(String file, MulitTableDto mulitDto ,
			List<IDto> freedtos,TrTaxorgPayoutReportDto inputDto) throws ITFEBizException{
			String sbillType=(Integer.parseInt(file.substring(file.lastIndexOf(File.separator)+1, file.length()).substring(13, 14)+"")+3)+"";
			inputDto.setSfinorgcode(file.substring(file.lastIndexOf(File.separator)+1, file.length()).substring(0, 12));			
			try {
				List<String[]> fileContent = super.readFile(file, " ");
				for (int i = 0; i < fileContent.size(); ) {
					if(i+3==fileContent.size())
						break;
					String year = getColumnsContent(fileContent.get(i+2))[1].substring(0,getColumnsContent(fileContent.get(i+2))[1].indexOf("年"));
					String month = getColumnsContent(fileContent.get(i+2))[1].substring(getColumnsContent(fileContent.get(i+2))[1].indexOf("年")+1,getColumnsContent(fileContent.get(i+2))[1].indexOf("月"));
					String day = getColumnsContent(fileContent.get(i+2))[1].substring(getColumnsContent(fileContent.get(i+2))[1].indexOf("月")+1,getColumnsContent(fileContent.get(i+2))[1].indexOf("日"));
					if(month!=null&&month.length()!=2)
						month = "0"+month;
					if(day!=null&&day.length()!=2)
						day = "0"+day;
					String rptdate=year+month+day;//(getColumnsContent(fileContent.get(i+2)))[1].replace("年", "").replace("月", "").replace("日", "");//第4行： 编制日期
					if(!rptdate.equals(inputDto.getSrptdate()))
						throw new ITFEBizException("输入的报表日期"+inputDto.getSrptdate()+"与文件解析的报表日期"+rptdate+"不符！");
					String[] cols= getColumnsContent(fileContent.get(i += 4));//第6行：   科目代码   科目名称   本日发生额  本月累计  本年累计 
					while(cols.length==5){
						TrTaxorgPayoutReportDto dto = new TrTaxorgPayoutReportDto();
						dto.setStrecode(inputDto.getStrecode());
						dto.setSfinorgcode(inputDto.getSfinorgcode());
						dto.setStaxorgcode(sbillType);
						dto.setSbudgettype(inputDto.getSbudgettype());
						dto.setSrptdate(inputDto.getSrptdate());
						dto.setSbudgetlevelcode(inputDto.getSbudgetlevelcode());						
						dto.setSbudgetsubcode(cols[0]);//科目代码
						dto.setSbudgetsubname(cols[1]);//科目名称
						dto.setNmoneyday(new BigDecimal(cols[2]));//本日发生额
						dto.setNmoneymonth(new BigDecimal(cols[3]));//本月累计
						dto.setNmoneyyear(new BigDecimal(cols[4]));//本年累计												
						freedtos.add(dto);
						if(++i==fileContent.size())
							break;
						cols=getColumnsContent(fileContent.get(i));
					}
				}
				//查询报表是否已经导入，如果已经导入提示用户，不允许重复导入
				TrTaxorgPayoutReportDto  tmpdto = new TrTaxorgPayoutReportDto();
			
				tmpdto.setStrecode(inputDto.getStrecode());
				tmpdto.setSrptdate(inputDto.getSrptdate());
				tmpdto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);//代理行导出的xml文件都是一般预算支出类型
				List list = CommonFacade.getODB().findRsByDtoWithUR(tmpdto);
				//如果报表数据大于0，说明本日报表已经导入，需要先删除再 进行 导入。
				if (list.size()>0) {
					CommonFacade.getODB().deleteRsByDto(tmpdto);
				}
			} catch (FileOperateException e) {			
				throw new ITFEBizException("文件解析异常",e);
			} catch (JAFDatabaseException e) {			
				throw new ITFEBizException("删除重复数据异常",e);
			}catch (ITFEBizException e) {			
				throw new ITFEBizException(e.getMessage(),e);
			} catch (Exception e) {
				throw new ITFEBizException("选择的导入文件格式有误，请查证！",e);	
			}	
			mulitDto.setFatherDtos(freedtos);
			saveDownloadReportCheckjzzf(inputDto.getSrptdate(),inputDto.getStrecode());//保存下载报表情况检查
			return mulitDto;	
	}
	
	/**
	 * 获取列内容
	 * @param cols
	 * @return
	 */
	public String[] getColumnsContent(String[] cols){
		StringBuffer rows = new StringBuffer();
		for(int x = 0 ; x < cols.length ; x ++){
			if(cols[x].trim().length() != 0){
				rows.append(cols[x].trim().replaceAll("\"", "").replaceAll(",", "").trim()).append(",");
			}
		}return rows.toString().trim().split(",");
	}
	
	/**
	 * 解析XML文件
	 * @param file
	 * @param freedtos
	 * @param mulitDto
	 * @param inputDto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParse(String file, 
			List<IDto> freedtos, MulitTableDto mulitDto,
			TrTaxorgPayoutReportDto inputDto) throws ITFEBizException{
		File inputXml = new File(file);
		SAXReader saxReader = new SAXReader();// SAXReader 解析xml的
		try {
			// 2. 利用SAXReader对象读取文件的信息，并返回Document对象
			Document doc = saxReader.read(inputXml);
			Element cfx = doc.getRootElement();
			if(cfx!=null)
			{
				Element msg = cfx.element("MSG");
				if(msg==null)
					throw new ITFEBizException("xml文件内容格式不对！");
				Element payBody6104Element = msg.element("PayBody6104");
				if(payBody6104Element==null)
					throw new ITFEBizException("xml文件内容格式不对！");
				Element paybill6104Element = payBody6104Element.element("PayBill6104");
				if(paybill6104Element==null)
					throw new ITFEBizException("xml文件内容格式不对！");
				List<Element> payDetail6104ElementList = paybill6104Element.elements("PayDetail6104");
				if(payDetail6104ElementList!=null&&payDetail6104ElementList.size()>0)
				{
					// 财政机构代码缓存 key为国库代码
					HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade.cacheFincInfo(null);
					inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					//查询报表是否已经导入，如果已经导入提示用户，不允许重复导入
					TrTaxorgPayoutReportDto  tmpdto = new TrTaxorgPayoutReportDto();
				
					tmpdto.setStrecode(inputDto.getStrecode());
					tmpdto.setSrptdate(inputDto.getSrptdate());
					tmpdto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);//代理行导出的xml文件都是一般预算支出类型
					List list = CommonFacade.getODB().findRsByDtoWithUR(tmpdto);
					//如果报表数据大于0，说明本日报表已经导入，需要先删除再 进行 导入。
					if (list.size()>0) {
						CommonFacade.getODB().deleteRsByDto(tmpdto);
					}
					for(Element payDetail6104Element:payDetail6104ElementList)
					{
						tmpdto = new TrTaxorgPayoutReportDto();
						tmpdto.setStrecode(inputDto.getStrecode());//国库代码
						tmpdto.setSrptdate(inputDto.getSrptdate());//报表日期
						tmpdto.setSfinorgcode(inputDto.getSfinorgcode());//财政机关代码
						tmpdto.setSbudgetlevelcode(inputDto.getSbudgetlevelcode());//预算级次
						tmpdto.setSbudgettype(payDetail6104Element.elementText("BudgetType"));//预算种类
						tmpdto.setSbudgetsubcode(payDetail6104Element.elementText("BudgetSubjectCode"));//预算科目代码
						tmpdto.setSbudgetsubname(payDetail6104Element.elementText("BudgetSubjectName"));//预算科目名称
						tmpdto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);//代理行导出的xml文件都是一般预算支出类型
						tmpdto.setSeconmicsubcode(payDetail6104Element.elementText("EcnomicSubjectCode"));//经济科目代码
						tmpdto.setSeconmicsubname(payDetail6104Element.elementText("EcnomicSubjectName"));//经济科目名称
						tmpdto.setNmoneyday(new BigDecimal(payDetail6104Element.elementText("DayAmt")));//日金额
						tmpdto.setNmoneytenday(new BigDecimal(payDetail6104Element.elementText("TenDayAmt")));//旬金额
						tmpdto.setNmoneymonth(new BigDecimal(payDetail6104Element.elementText("MonthAmt")));//月金额
						tmpdto.setNmoneyquarter(new BigDecimal(payDetail6104Element.elementText("QuarterAmt")));//季金额
						tmpdto.setNmoneyyear(new BigDecimal(payDetail6104Element.elementText("YearAmt")));//年金额
						freedtos.add(tmpdto);
					}
					mulitDto.setFatherDtos(freedtos);
					saveDownloadReportCheck(inputDto.getSrptdate(),inputDto.getStrecode());//保存下载报表情况检查
					return mulitDto;
				}else
					throw new ITFEBizException("xml文件为空数据文件！");
			}
		}catch (DocumentException e) {
			throw new ITFEBizException("解析xml文件出现异常！", e);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("解析xml查询数据库中是否已经导入出现异常！", e);
		} catch (ValidateException e) {
			throw new ITFEBizException("解析xml查询数据库中是否已经导入出现异常！", e);
		}
		return mulitDto;	
	}
	private void saveDownloadReportCheck(String date,String trecode)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setSzhichu("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSzhichu())||null==dto.getSzhichu())
				{
					dto.setSzhichu("1");
					DatabaseFacade.getODB().update(dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}catch(Exception e)
		{
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}
	}
	private void saveDownloadReportCheckjzzf(String date,String trecode)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setSext1("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSext1())||null==dto.getSext1())
				{
					dto.setSext1("1");
					DatabaseFacade.getODB().update(dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}catch(Exception e)
		{
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}
	}
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}

}
