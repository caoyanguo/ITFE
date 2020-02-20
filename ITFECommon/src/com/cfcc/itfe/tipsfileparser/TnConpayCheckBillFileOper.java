package com.cfcc.itfe.tipsfileparser;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckpaybankDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * 解析额度对账单导入文件
 * @author hjr
 * 20131226
 */
public class TnConpayCheckBillFileOper extends AbstractTipsFileOper{
	private Log logger=LogFactory.getLog(TnConpayCheckBillFileOper.class);
	
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {		
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// 业务类型
		mulitDto.setSbookorgcode(sbookorgcode);// 核算主体代码
		TnConpaycheckpaybankDto _dto = (TnConpaycheckpaybankDto) paramdto;
		_dto.setSorgcode(sbookorgcode);
		List<IDto> freedtos = new ArrayList<IDto>();
		if(_dto!=null&&"2".equals(_dto.getSext1()))
			return fileParserPaybank(file, mulitDto, freedtos, _dto);
		if(file.endsWith(".csv"))
			return fileParser(file, mulitDto, freedtos, _dto);
		try {
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(sbookorgcode); //法人代码缓存
			List<String[]> fileContent = super.readFile(file, ",");
			for (int i = 1; i < fileContent.size(); i++) {
				String[] cols = fileContent.get(i);				
				BigDecimal curBalance  = new BigDecimal(cols[8]);//本期额度余额
			    BigDecimal curPayAmt  = new BigDecimal(cols[6]);//本期支出额度
			    BigDecimal curBackAmt  = new BigDecimal(cols[7]);//本期退回额度
			    BigDecimal curAddAmt  = new BigDecimal(cols[5]);//本期下达额度 
				TnConpaycheckbillDto dto = new TnConpaycheckbillDto();
				dto.setSbookorgcode(sbookorgcode);
				dto.setStrecode(_dto.getStrecode());
				dto.setSfuncsbtcode(cols[2]);
				String payKind ="";
				if (StateConstant.COMMON_YES.equals(cols[3])) {
					payKind =StateConstant.Conpay_Grant;
				}else{
					payKind =StateConstant.Conpay_Direct;
				}
				dto.setCamtkind(payKind);
				dto.setSbnkno(cols[4]);
				dto.setFlastmonthzeroamt(curBalance.add(curAddAmt.negate()).add(curPayAmt.add(curBackAmt.negate())));//上期额度余额：系统额度余额 + 本期清算额度 - 录入额度发生额
				dto.setFcurzeroamt(curBalance);//本期额度余额
				dto.setFcurreckzeroamt(curPayAmt.add(curBackAmt.negate()));//本期清算额度：支出额度发生额-退回额度
				dto.setFcursmallamt(curAddAmt);//本期增加额度(小额现金发生额字段暂时不用用来存储【本期增加额】)
				dto.setFlastmonthsmallamt(new BigDecimal(0));//上月额度小余额
				dto.setFcurrecksmallamt(new BigDecimal(0));//本期清算小额度支出额度
				dto.setDstartdate(CommonUtil.strToDate(cols[9]));
				dto.setDenddate(CommonUtil.strToDate(cols[10]));
				if (i==1) {
					TnConpaycheckbillDto tmp = new TnConpaycheckbillDto();
				    tmp.setDstartdate(CommonUtil.strToDate(cols[9]));
					tmp.setDenddate(CommonUtil.strToDate(cols[10]));
					tmp.setStrecode(_dto.getStrecode());
					tmp.setCamtkind(payKind);
					CommonFacade.getODB().deleteRsByDto(tmp);
					
				}
				dto.setSbdgorgcode(cols[1]);//预算单位代码
				dto.setSbdgorgname(rpmap.get(cols[1])==null?"":rpmap.get(cols[1]).getSbookorgcode());//预算单位名称
				freedtos.add(dto);				    
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("删除重复数据异常",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("删除重复数据异常",e);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("额度对账单文件解析失败！",e);
		} 
		mulitDto.setFatherDtos(freedtos);
		return mulitDto;
	}
	
	/**
	 * 解析.csv文件
	 * @param file
	 * @param mulitDto
	 * @param freedtos
	 * @param _dto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParser(String file, MulitTableDto mulitDto ,
			List<IDto> freedtos,TnConpaycheckpaybankDto _dto) throws ITFEBizException{		
		try {
			List<String[]> fileContent = super.readFile(file, " ");
			String[] cols2=null;
			String startdate="";
			String enddate="";
			String bgttype=null;
			for (int i = 5; i < fileContent.size();i++) {		//第6行：
				if(i==5)
				{
					cols2 = getColumnsContent(fileContent.get(i-4));//第3行： 预算种类
					if(cols2[0]!=null&&cols2[0].contains("预算内"))
						bgttype = MsgConstant.BDG_KIND_IN;
					else if(cols2[0]!=null&&cols2[0].contains("预算外"))
						bgttype = MsgConstant.BDG_KIND_OUT;
					cols2 = getColumnsContent(fileContent.get(i-3));//第3行：   代理银行名称	 起始日期		终止日期
					if(cols2.length>=3&&cols2[2].contains("年"))
					{
						startdate=cols2[2].replace("年", "").replace("月", "").replace("日", "");	//起始日期
						if(cols2.length>=4&&cols2[3].contains("年"))
							enddate=cols2[3].replace("年", "").replace("月", "").replace("日", "");	//终止日期
					}
					else if(cols2.length>=4&&cols2[3].contains("年"))
					{
						startdate=cols2[3].replace("年", "").replace("月", "").replace("日", "");	//起始日期
						if(cols2.length>=5&&cols2[4].contains("年"))
							enddate=cols2[4].replace("年", "").replace("月", "").replace("日", "");	//终止日期
					}
					//TCBS导出文件变化，终止日期变为第四列
//					enddate=cols2[4].replace("年", "").replace("月", "").replace("日", "");	//终止日期	
//					enddate=cols2[3].replace("年", "").replace("月", "").replace("日", "");	//终止日期	
				}
				String[] cols= getColumnsContent(fileContent.get(i));//第6行：   序号  支付种类  预算单位代码  预算单位名称 预算科目代码 .....  
				String payKind = null;
				
				if(cols!=null&&cols.length>7){
					if(cols[1] != null && !"".equals(cols[1])){
						if(!"直接支付".equals(cols[1]) && !"授权支付".equals(cols[1])){
							logger.error(cols[1]);
							continue;
						}
					}
					payKind = getAmtkind(cols[1]);
				}
				if("1".equals(payKind)||"2".equals(payKind))
				{
					TnConpaycheckbillDto dto = new TnConpaycheckbillDto();							
					dto.setDstartdate(CommonUtil.strToDate(startdate));//起始日期
					dto.setDenddate(CommonUtil.strToDate(enddate));//终止日期		
					dto.setSbookorgcode(mulitDto.getSbookorgcode());//核算主体代码
					dto.setStrecode(_dto.getStrecode());//国库主体代码
					dto.setSbdgorgcode(cols[2]);//预算单位代码
					if(cols.length==9)
					{
						dto.setSbdgorgname(cols[3]);//预算单位名称					
						dto.setSbnkno(_dto.getSbankcode());//代理银行代码
						dto.setSfuncsbtcode(cols[4]);//功能类科目代码 
						dto.setSecosbtcode(bgttype);//预算类型			
						dto.setCamtkind(getAmtkind(cols[1]));//额度种类
						dto.setFlastmonthzeroamt(new BigDecimal(cols[5]));//上期额度余额 
						dto.setFcursmallamt(new BigDecimal(cols[6]));//本期增加额度
						dto.setFcurreckzeroamt(new BigDecimal(cols[7]));//本期零已清算额度 
						dto.setFcurzeroamt(new BigDecimal(cols[8]));//本期额度余额
					}else if(cols.length==8)
					{
						dto.setSbdgorgname("未知");//预算单位名称					
						dto.setSbnkno(_dto.getSbankcode());//代理银行代码
						dto.setSfuncsbtcode(cols[3]);//功能类科目代码 
						dto.setSecosbtcode(bgttype);//预算类型			
						dto.setCamtkind(getAmtkind(cols[1]));//额度种类
						dto.setFlastmonthzeroamt(new BigDecimal(cols[4]));//上期额度余额 
						dto.setFcursmallamt(new BigDecimal(cols[5]));//本期增加额度
						dto.setFcurreckzeroamt(new BigDecimal(cols[6]));//本期零已清算额度 
						dto.setFcurzeroamt(new BigDecimal(cols[7]));//本期额度余额
					}
					dto.setFlastmonthsmallamt(null);//上月小额现金额度余额
					dto.setFcurrecksmallamt(null);//本期小额现金已清算额度
					dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));						
					freedtos.add(dto);
					if(i==5)
					{
						TnConpaycheckbillDto tmp = new TnConpaycheckbillDto();
						tmp.setDstartdate(CommonUtil.strToDate(startdate));
						tmp.setDenddate(CommonUtil.strToDate(enddate));
						tmp.setStrecode(_dto.getStrecode());
						tmp.setSbnkno(_dto.getSbankcode());
						tmp.setCamtkind(payKind);
						tmp.setSecosbtcode(bgttype);//预算类型
						CommonFacade.getODB().deleteRsByDto(tmp);
					}
				}
			}	
		} catch (FileOperateException e) {
			logger.error(e);
			throw new ITFEBizException("文件解析异常",e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("删除重复数据异常",e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("选择的导入文件格式有误，请查证！",e);	
		}	
		mulitDto.setFatherDtos(freedtos);
		return mulitDto;	
	}
	/**
	 * 解析.csv文件
	 * @param file
	 * @param mulitDto
	 * @param freedtos
	 * @param _dto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParserPaybank(String file, MulitTableDto mulitDto ,
			List<IDto> freedtos,TnConpaycheckpaybankDto dto) throws ITFEBizException{	
		SQLExecutor execDetail = null;
		try {
			List<String[]> fileContent = super.readFile(file, " ");
			String[] cols2=null;
			String startdate="";
			Matcher match = null;
			Pattern patt = Pattern.compile("[0-9]{1,60}");// 匹配小于32位数字
			for (int i = 0; i < fileContent.size();i++) {		//第6行：
				cols2 = fileContent.get(i);
				List<String> templist = new ArrayList<String>();
				for(String temp:cols2)
				{
					if(temp!=null&&!"".equals(temp))
						templist.add(temp);
				}
				cols2 = new String[templist.size()];
				cols2 = templist.toArray(cols2);
				if(cols2[0].contains("编制日期"))
				{
					startdate = cols2[1];
					startdate = startdate.replace("年", "");
					startdate = startdate.replace("月", "");
					startdate = startdate.replace("日", "");
					dto.setSacctdate(startdate);
				}else
				{
					match = patt.matcher(cols2[0]);
					if(match.matches()==true&&cols2.length==4)
					{
						TnConpaycheckpaybankDto tmpdto = new TnConpaycheckpaybankDto();
						tmpdto.setSdealno(VoucherUtil.getGrantSequence());
						tmpdto.setSorgcode(dto.getSorgcode());
						tmpdto.setStrecode(dto.getStrecode());
						tmpdto.setSacctdate(dto.getSacctdate());
						tmpdto.setSbankcode(dto.getSbankcode());
						tmpdto.setSbgtlevel(dto.getSbgtlevel());
						tmpdto.setSext2(dto.getSext2());//对账单月份
						tmpdto.setSbgttypecode(dto.getSbgttypecode());
						tmpdto.setSpaytypecode(dto.getSpaytypecode());
						tmpdto.setSsubjectcode(cols2[0]);
						tmpdto.setSsubjectname(cols2[1]);
						tmpdto.setNmonthamt(new BigDecimal(StringUtil.replace(cols2[2], "\"", "").replace(",", "")));
						tmpdto.setNyearamt(new BigDecimal(StringUtil.replace(cols2[3],"\"","").replace(",", "")));
						freedtos.add(tmpdto);
					}else
					{
						continue;
					}
					
				}
			}
			if(freedtos!=null&&freedtos.size()>0)
			{
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = "delete from TN_CONPAYCHECKPAYBANK where s_orgcode=? and s_trecode=? and s_bankcode=? and s_bgtlevel=? and s_bgttypecode=? and s_paytypecode=? and s_ext2=?";
				execDetail.addParam(dto.getSorgcode());
				execDetail.addParam(dto.getStrecode());
				execDetail.addParam(dto.getSbankcode());
				execDetail.addParam(dto.getSbgtlevel());
				execDetail.addParam(dto.getSbgttypecode());
				execDetail.addParam(dto.getSpaytypecode());
				execDetail.addParam(dto.getSext2());
				execDetail.runQuery(sql);
			}
				
		} catch (FileOperateException e) {
			logger.error(e);
			throw new ITFEBizException("文件解析异常",e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("删除重复数据异常",e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("选择的导入文件格式有误，请查证！",e);	
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}	
		mulitDto.setFatherDtos(freedtos);
		return mulitDto;	
	}
	/**
	 * 获取支付方式
	 * @param amtkind
	 * @return
	 * @throws ITFEBizException
	 */
	public String getAmtkind(String amtkind) throws ITFEBizException{
		if(amtkind.equals("授权支付")){
			return "2";
		}else if(amtkind.equals("直接支付")){
			return "1";
		}else if(amtkind.equals("支付种类")){
			return "0";
		}else{
			throw new ITFEBizException("选择的导入文件格式有误，支付种类的值不是 \"授权支付\" 或 \"直接支付\" ！");
		}		
	}
	
	/**
	 * 根据银行名称查找银行代码
	 * @param sbankname
	 * @param ls_OrgCode
	 * @return
	 */
	public String getSbanknoBybankname(String sbankname,String ls_OrgCode){
		Map<String, TsConvertbanknameDto> bankInfo=new HashMap<String, TsConvertbanknameDto>();
	//	Map<String, TsConvertbanknameDto> bankInfo=BusinessFacade.getBankInfo(ls_OrgCode);
		if(bankInfo!=null&&bankInfo.size()>0){
			TsConvertbanknameDto dto = bankInfo.get(sbankname);
			if(dto!=null)
				return dto.getSbankcode();
		}return null;
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
		}
		return rows.toString().trim().split(",");
	}
	
	
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}

}
