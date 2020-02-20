package com.cfcc.itfe.tipsfileparser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoAllocateIncomePK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 功能：解析资金报文
 * @author hejianrong
 * @time  2014-04-02
 */
public class VoucherAllocateIncomeFileOper extends AbstractTipsFileOper{
	private static Log logger = LogFactory.getLog(VoucherAllocateIncomeFileOper.class);
			
	/**
	 * 解析CSV文件
	 * @param file
	 * @param sbookorgcode
	 * @param biztype
	 * @param paramdto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParser(String file, String sbookorgcode,String userid, String biztype, 
			String filekind, IDto paramdto,Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {		
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);
		mulitDto.setSbookorgcode(sbookorgcode);
		List<IDto> freedtos = new ArrayList<IDto>();			
		try {
			//按照空格" "读取文件，并讲读取的每行记录存储在列表中
			List<String[]> fileContent = super.readFile(file, " ");
			//获取参数资金业务类型vtcodeKind,并释放parameterDto
			TvVoucherinfoAllocateIncomeDto parameterDto =(TvVoucherinfoAllocateIncomeDto) paramdto;
			String fundType = parameterDto.getSreportkind();
			parameterDto=null;
			//按照资金报文类型进行解析
			if(StateConstant.CMT100.equals(fundType)){
				freedtos = analysisCmt100Data(fileContent, paramdto, biztype);
			}else if (StateConstant.CMT108.equals(fundType)){
				freedtos = analysisCmt108Data(fileContent, paramdto, biztype);
			}else if (StateConstant.CMT103.equals(fundType)) {
				freedtos = analysisCmt103Data(fileContent, paramdto, biztype);
			}else if (StateConstant.PKG001.equals(fundType)) {
				freedtos = analysisPKG001Data(fileContent, paramdto, biztype);
			}else if (StateConstant.PKG007.equals(fundType)) {
				freedtos = analysisPKG007Data(fileContent, paramdto, biztype);
			}else if (StateConstant.TYPE999.equals(fundType)){
				freedtos = analysisType999(fileContent, paramdto, biztype);
			}else if (StateConstant.HVPS111.equals(fundType)){
				freedtos = analysisHVPS111Data(fileContent, paramdto, biztype);
			}else if (StateConstant.HVPS112.equals(fundType)){
				freedtos = analysisHVPS112Data(fileContent, paramdto, biztype);
			}else if (StateConstant.BEPS121.equals(fundType)){
				freedtos = analysisBEPS121Data(fileContent, paramdto, biztype);
			}else if (StateConstant.BEPS122.equals(fundType)){
				freedtos = analysisBEPS122Data(fileContent, paramdto, biztype);
			}
		} catch (FileOperateException e) {
			logger.error(e);
			throw new ITFEBizException("文件解析异常",e);
		}catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("选择的导入文件格式有误，请查证！"+e.getMessage(),e);	
		}
		mulitDto.setFatherDtos(freedtos);	
		return mulitDto;
	}
	
	/**
	 * 删除重复数据
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void deleteRepeateData2(TvVoucherinfoAllocateIncomeDto dto) throws ITFEBizException{
		TvVoucherinfoAllocateIncomePK pk=new TvVoucherinfoAllocateIncomePK();
		pk.setSdealno(dto.getSdealno());
		try {
			DatabaseFacade.getODB().delete(pk);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("删除重复数据异常",e);
		}
	}
	/**
	 * 删除重复数据
	 * @param dto
	 * @throws ITFEBizException
	 */
	private boolean isRepeateData(TvVoucherinfoAllocateIncomeDto dto) throws ITFEBizException{
		TvVoucherinfoAllocateIncomePK pk=new TvVoucherinfoAllocateIncomePK();
		pk.setSdealno(dto.getSdealno());
		
		TvVoucherinfoAllocateIncomeDto tmp;
		try {
			tmp = (TvVoucherinfoAllocateIncomeDto) DatabaseFacade.getODB().find(pk);
			if(tmp!=null)
			{
				throw new ITFEBizException("文件中数据是重复导入数据,编号："+dto.getSdealno());
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("删除重复数据异常",e);
		}
		return null!=tmp?true:false;
	}
	
	/**
	 * 获取列内容
	 * @param cols
	 * @return
	 */
	public String[] getColumnsContent(String[] cols){
		StringBuffer rows = new StringBuffer();
		for(int i = 0 ; i < cols.length ; i ++){
			if(cols[i].trim().length() != 0){
				if(!cols[i].trim().equals("\"")){
					rows.append(cols[i].trim().replaceAll("\"", "").replaceAll(",", "").trim()).append(",");
				}
			}
		}
		return rows.toString().trim().split(",");
	}
	
	
	
	
	/**
	 * 解析资金报文类型为[CMT100]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisCmt100Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			dto.setSpaydealno(cols[1]);//支付交易序号
			cols= getColumnsContent(fileContent.get(++i));
			if(!cols[1].equalsIgnoreCase(StateConstant.CMT100NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[CMT100]");
			}
			dto.setSreportkind(StateConstant.CMT100);//报文种类CMT100
			dto.setSvtcodedes(cols[1]);//用预留字段1存储报文种类来充当备注
			dto.setStradekind(cols[3]+" "+cols[4]);//交易种类
			dto.setSvtcodekind(cols[cols.length-1]);//业务类型
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			dto.setScommitdate(cols[cols.length-1].replaceAll("-", ""));//委托日期
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//发起行名称		
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号：
			dto.setSreceivedate(cols[cols.length-1].replaceAll("-", ""));//收报日期
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//收款人账号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//收款人名称
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//拨款金额
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//附言
			dto.setSescortmarks(cols[cols.length-1]);//核押标志
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//借
//			dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//贷
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[CMT100]的文件出错："+e.getMessage());
//			}
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * 解析资金报文类型为[CMT108]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisCmt108Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("报文种类"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.CMT108NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[CMT108]");
			}
			dto.setSreportkind(StateConstant.CMT108);//报文种类CMT108
			dto.setSvtcodedes(cols[1]);//用预留字段1存储报文种类来充当备注
			dto.setStradekind(cols[3]+" "+cols[4]);//交易种类
			dto.setSpaydealno(cols[cols.length-1]);//支付交易序号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname("");//付款人开户行行号
			dto.setScommitdate(cols[cols.length-1].replaceAll("-", ""));//委托日期
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//发起行名称	
			dto.setShold2(getColumnsContent(fileContent.get(++i))[1]);//原委托日期
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname("");//收款行号行名
			dto.setSreceivedate(cols[cols.length-1].replaceAll("-", ""));//收报日期
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//收款人账号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//收款人名称
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//拨款金额
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//附言
			dto.setSescortmarks(cols[cols.length-1]);//核押标志
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//借
//			dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//贷
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[CMT108]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * 解析资金报文类型为[CMT103]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisCmt103Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=3) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("报文种类"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.CMT103NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[CMT103]");
			}
			dto.setSreportkind(StateConstant.CMT103);//报文种类CMT103
			dto.setSvtcodedes(cols[1]);//用预留字段1存储报文种类来充当备注
			dto.setStradekind(cols[3]+" "+cols[4]);//交易种类
			dto.setSvtcodekind(cols[6]);//业务类型
			dto.setSpaydealno(cols[cols.length-1]);//支付交易序号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			dto.setScommitdate(cols[cols.length-1].replaceAll("-", ""));//委托日期
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//发起行名称		
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号：
			dto.setSreceivedate(cols[cols.length-1].replaceAll("-", ""));//收报日期
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//收款人账号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//收款人名称
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//拨款金额
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//附言
			dto.setSescortmarks(cols[cols.length-1]);//核押标志
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//借
//			dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//贷
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[0]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[CMT103]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}
		return freedtos;
	}
	
	/**
	 * 解析资金报文类型为[PKG001]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisPKG001Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("报文种类"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.PKG001NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[PKG001]");
			}
			dto.setSreportkind(StateConstant.PKG001);//报文种类PKG001
			dto.setSvtcodedes(cols[1]);//用预留字段1存储报文种类来充当备注
			dto.setStradekind(cols[3]);//交易种类
			dto.setSvtcodekindno(cols[cols.length-1]);//业务类型号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpaydealno(cols[1]);//支付交易序号
			dto.setSvtcodekind(cols[3]);//业务种类
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			dto.setScommitdate(trasforDate(cols[cols.length-1]));//委托日期
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//发起行名称		
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号：
			dto.setSreceivedate(trasforDate(cols[cols.length-1]));//收报日期
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//收款人账号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//收款人名称
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//拨款金额
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//附言
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[2]);//借
			//dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//贷
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[PKG001]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}
		return freedtos;
	}
	
	/**
	 * 解析资金报文类型为[PKG007]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisPKG007Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("报文种类"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.PKG007NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[PKG007]");
			}
			dto.setSreportkind(StateConstant.PKG007);//报文种类PKG007
			dto.setSvtcodedes(cols[1]);//报文种类说明
			dto.setStradekind(cols[3]);//交易种类
			dto.setSvtcodekindno(cols[5]);//业务类型号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpaydealno(cols[cols.length-1]);//支付交易序号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setScommitdate(trasforDate(cols[cols.length-1]));//委托日期
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctbankname(cols[1]);//发起行名称
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctno(cols[1]);//付款人账号
			dto.setSoricommitdate(trasforDate(cols[3]));//原委托日期
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctname(cols[1]);//付款人名称
			dto.setSoripaydealno(cols[3]);//原支付交易序号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSreceivedate(trasforDate(cols[cols.length-1]));//收报日期
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//收款人账号
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//收款人名称
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//拨款金额
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//附言
			dto.setSescortmarks(cols[cols.length-1]);//核押标志
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//借
			//dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//贷
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[PKG007]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}
		return freedtos;
	}
	
	
	/**
	 * 解析资金业务类型为[ 999-核算主体间调拨收入]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisType999(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		
		for (int i = 0; i < fileContent.size(); i++) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols =getColumnsContent(fileContent.get(i));
			if(!(cols!=null&&cols.length>1&&cols[0].contains("业务类型")))
			{
				continue;
			}
			//用于判断是否是核算主体间调拨收入文件
			if(!cols[0].contains("业务类型")){
				throw new ITFEBizException("导入的文件资金报文类型不是[核算主体间调拨收入]");
			}
			dto.setSvtcodedes("调拨收入");//用预留字段1存储业务类型来充当备注
			dto.setSvtcodekind(MsgConstant.FUND_BIZ_TYPE_ORGCODEINNERINCOME);//用“00”代替“系统内不同核算主体调拨收入”的业务类型
			dto.setScommitdate(trasforDate(cols[3]));//账务日期
			cols =getColumnsContent(fileContent.get(++i));
			dto.setSpayacctbankname(cols[3]);//发起核算主体代码
			dto.setSforwardbankname(cols[1]);//发起核算主体名称
			dto.setSforwardbankno(dto.getSpayacctbankname());
			cols =getColumnsContent(fileContent.get(++i));
			dto.setShold1(cols[1]);//接收核算主体名称
			dto.setSpayeeacctbankname(cols[3]);//接收核算主体代码
			cols =getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("名称"))
			{
				dto.setSpayacctno(cols[3]);//付款人账号
				dto.setSpayacctname(cols[1]);//付款人名称
			}else
			{
				dto.setSpayacctno(cols[1]);//付款人账号
				dto.setSpayacctname(cols[3]);//付款人名称
			}
			cols =getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("名称"))
			{
				dto.setSpayeeacctno(cols[3]);//收款人账号
				dto.setSpayeeacctname(cols[1]);//收款人名称
			}else
			{
				dto.setSpayeeacctno(cols[1]);//收款人账号
				dto.setSpayeeacctname(cols[3]);//收款人名称
			}
			cols =getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("摘要名称"))
			{
				dto.setSforwardbankno(cols[3]);//流水账摘要代码
				dto.setSforwardbankname(cols[1]);//流水账摘要名称
				cols =getColumnsContent(fileContent.get(++i));
				if(cols[0].contains("金额"))
					dto.setNmoney(new BigDecimal(cols.length==4?cols[3]:cols[1]));//金额
				
			}else if(cols[0].contains("金额"))
			{
				dto.setNmoney(new BigDecimal(cols.length==4?cols[3]:cols[1]));//金额
				cols =getColumnsContent(fileContent.get(++i));
				if(cols[0].contains("摘要名称"))
				{
					dto.setSforwardbankno(cols[3]);//流水账摘要代码
					dto.setSforwardbankname(cols[1]);//流水账摘要名称
				}
			}
			if(!cols[0].contains("借"))
				cols =getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//借
			cols =getColumnsContent(fileContent.get(++i));
			dto.setSlend(cols[1]);//贷
			cols =getColumnsContent(fileContent.get(++i));
			StringBuffer sbf=new StringBuffer();
			for (int j = 1; j < cols.length; j++) {
				sbf.append(cols[j]);
			}
			dto.setSdemo(sbf.toString());//附言
			cols =getColumnsContent(fileContent.get(++i));
			dto.setSdealno(cols[1]);//交易流水号
			dto.setSvtcode(biztype);//凭证类型
			dto.setStradekind("调拨收入");//交易种类
			dto.setSpaydealno(cols[1].substring(cols[1].length()-8,cols[1].length()));//支付交易序号
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金业务类型为[核算主体间调拨收入]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * 解析资金报文类型为[HVPS111]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisHVPS111Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("报文种类"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.HVPS111NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[HVPS111]");
			}
			dto.setSreportkind(StateConstant.HVPS111);//报文种类HVPS111
			dto.setSvtcodekind(cols[5]);//业务类型
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[2]+cols[3]);//交易种类
			dto.setSpaydealno(cols[5].substring(8));//支付交易序号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//收报日期
			dto.setScommitdate(cols[3].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//委托日期
			
			cols = getColumnsContent(fileContent.get(i+1));
			if(cols!=null&&cols[0].contains("验签标志"))
			{
				cols= getColumnsContent(fileContent.get(++i));
				dto.setSescortmarks(cols[1]==null?"":cols[1].replace("验签", ""));//验签标志
			}
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols.length>3?cols[1]:cols.length>1?cols[1]:"");//发起行名称
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//用Hold1代替收款行行名
			if(getColumnsContent(fileContent.get(i+1))[0].contains("清算行"))
				++i;//收款清算行
			if(getColumnsContent(fileContent.get(i+1))[0].contains("发起行"))
				++i;//发起行行号
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//收款人账号
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//收款人名称
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//金额
			cols = getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("附言")&&cols.length==1)//当前行只有附言两字换行是具体原因
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("借:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //附言
				if(dto.getSdemo()!=null&&dto.getSdemo().getBytes().length>83)
					dto.setSdemo(CommonUtil.subString(dto.getSdemo(), 83));
			}else if(cols[0].contains("附言")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //附言
				if(dto.getSdemo().getBytes().length>83)
					dto.setSdemo(CommonUtil.subString(dto.getSdemo(), 83));
			}else if(cols[0].contains("附言")&&cols.length>2)
			{
				StringBuffer fy = new StringBuffer("");
				for(int num=1;num<cols.length;num++)
				{
					fy.append(cols[num]);
				}	
				dto.setSdemo(fy.toString().getBytes().length>83 ? CommonUtil.subString(fy.toString(), 83) : fy.toString()); //附言
			}else if(cols[0].contains("借"))
			{
				--i;
			}else
			{
				cols = getColumnsContent(fileContent.get(++i));
				if(cols[0].contains("退汇原因"))
					dto.setSdemo(cols[1]);
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//借
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//贷
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[HVPS111]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * 解析资金报文类型为[HVPS112]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisHVPS112Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("报文种类"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.HVPS112NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[HVPS112]");
			}
			dto.setSreportkind(StateConstant.HVPS112);//报文种类HVPS112
			dto.setSvtcodekind(cols[5]);//业务类型
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[3]);//交易种类
			dto.setSpaydealno(cols[5].substring(8));//支付交易序号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//收报日期
			dto.setScommitdate(cols[3].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//委托日期
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSescortmarks(cols[1]);//验签标志
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols[1]);//发起行名称
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号：
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//用Hold1代替收款行行名
			
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//收款人账号
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//收款人名称
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//金额
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("附言")&&cols.length==1)//当前行只有附言两字换行是具体原因
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("借:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("附言")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("借"))
			{
				--i;
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//借
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//贷
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[HVPS112]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * 解析资金报文类型为[BEPS121]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS121Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		//获取资金文件的第一行
		String[] firstCols = null;
		for(int i=0;i<fileContent.size();i++)
		{
			firstCols = getColumnsContent(fileContent.get(i));
			if(firstCols==null||firstCols.length<5)
				continue;
			else
				break;
		}
		
		//取得资金业务类型
		String vtcodekind = firstCols[5].trim();
		List<IDto> freedtos = new ArrayList<IDto>();
		if(null!=vtcodekind&&(vtcodekind.equals("A100")||vtcodekind.equals("A102")||vtcodekind.equals("A108"))){
			//解析资金报文类型[BEPS121]，业务类型为[A100 - 普通汇兑]的资金报文
			freedtos = analysisBEPS121DataForA100(fileContent, paramdto, biztype);
		}else if(null!=vtcodekind&&vtcodekind.equals("A105")){
			//解析资金报文类型[BEPS121]，业务类型为[A105 - 退汇]的资金报文
			freedtos = analysisBEPS121DataForA105(fileContent, paramdto, biztype);
		}
		return freedtos;
	}
	
	
	/**
	 * 解析资金报文类型[BEPS121]，业务类型为[A100 - 普通汇兑]的资金报文
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS121DataForA100(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		
		for (int i = 0; i < fileContent.size(); i++) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			if(!(cols!=null&&cols.length>1&&cols[0].contains("报文种类")))
			{
				continue;
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.BEPS121NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[BEPS121]");
			}
			dto.setSreportkind(StateConstant.BEPS121);//报文种类BEPS121
			dto.setSvtcodekind(cols[5]);//业务类型
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[2]+cols[3]);//交易种类
			dto.setSpaydealno(cols[5].substring(8));//支付交易序号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSescortmarks(cols[1].replace("验签", ""));//验签标志
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//收报日期
			dto.setScommitdate(cols[3].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//委托日期
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols.length>3?cols[1]:"");//发起行名称
			if(cols[0].contains("付款行名称"))
				dto.setSforwardbankname(cols[1]);//发起行名称
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号：
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//用Hold1代替收款行行名
			
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//收款人账号
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//收款人名称
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//金额
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("附言")&&cols.length==1)//当前行只有附言两字换行是具体原因
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("借:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("附言")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("借"))
			{
				--i;
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//借
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols.length>1){
				dto.setSlend(cols[1]);//贷
			}
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[BEPS121]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	
	
	
	/**
	 * 解析资金报文类型[BEPS121]，业务类型为[A105 - 退汇]的资金报文
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS121DataForA105(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			if(!(cols!=null&&cols.length>1&&cols[0].contains("报文种类")))
			{
				continue;
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.BEPS121NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[BEPS121]");
			}
			dto.setSreportkind(StateConstant.BEPS121);//报文种类BEPS121
			dto.setSvtcodekind(cols[5]);//业务类型
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[2]+cols[3]);//交易种类
			dto.setSpaydealno(cols[5].substring(8));//支付交易序号
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[1]!=null&&cols[1].contains("验签"))
				cols[1] = cols[1].replace("验签", "");
			dto.setSescortmarks(cols[1]);//验签标志
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//收报日期
			dto.setScommitdate(cols[3].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//委托日期
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols.length>3?cols[1]:"");//发起行名称
			
			cols= getColumnsContent(fileContent.get(++i));
			
			dto.setSpayacctno(cols[1]);//付款人账号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctname(cols[1]);//付款人名称

			cols= getColumnsContent(fileContent.get(++i));//发起直接参与机构和接收直接参与机构
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号：
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setShold1(cols[1]);//用Hold1代替收款行行名
			
			cols= getColumnsContent(fileContent.get(++i));//原发起间接参与机构和原报文标识号
			if(cols.length>3)
				dto.setSsecretsign(cols[3]);//密押字段存放原报文标号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//收款人账号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//收款人名称
			
			cols= getColumnsContent(fileContent.get(++i));//原业务类型和原报文编号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[3]));//金额
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("附言")&&cols.length==1)//当前行只有附言两字换行是具体原因
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("借:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("附言")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("退汇"))
			{
				--i;
			}
			
			cols= getColumnsContent(fileContent.get(++i));//退汇原因
			if(cols!=null&&cols[0].contains("原因")&&cols.length>1)
				dto.setSdemo(cols[1]+dto.getSdemo());
			else if(cols!=null&&cols[0].contains("借:")&&cols.length>1)
				dto.setSborrow(cols[1]);//借
			else
				cols= getColumnsContent(fileContent.get(++i));
			
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols!=null&&cols[0].contains("借:")&&cols.length>1)
			{
				dto.setSborrow(cols[1]);//借
				cols= getColumnsContent(fileContent.get(++i));
			}
			if(cols!=null&&cols[0].contains("贷:")&&cols.length>1){
				dto.setSlend(cols[1]);//贷
			}
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[BEPS121]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	
	
	/**
	 * 解析资金报文类型为[BEPS122]的文件
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS122Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("报文种类"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.BEPS122NAME)){
				throw new ITFEBizException("导入的文件资金报文类型不是[BEPS122]");
			}
			dto.setSreportkind(StateConstant.BEPS122);//报文种类BEPS122
			dto.setSvtcodekind(cols[5]);//业务类型
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]);//交易种类
			dto.setSpaydealno(cols[5].substring(8));//支付交易序号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSescortmarks(cols[1]);//验签标志
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//收报日期
			dto.setScommitdate(cols[3].replaceAll("年", "").replaceAll("月", "").replaceAll("日", ""));//委托日期
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//发起行行号
			dto.setSpayacctbankname(cols[3]);//付款人开户行行号
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols[1]);//发起行名称
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//付款人账号
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//付款人名称
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//接收行行号
			dto.setSpayeeacctbankname(cols[3]);//收款人开户行行号：
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//用Hold1代替收款行行名
			
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//收款人账号
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//收款人名称
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//金额
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("附言")&&cols.length==1)//当前行只有附言两字换行是具体原因
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("借:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("附言")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //附言
			}else if(cols[0].contains("借"))
			{
				--i;
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//借
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols.length>1){
				dto.setSlend(cols[1]);//贷
			}
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//流水号
			dto.setSvtcode(biztype);//凭证类型
//			try {
//				deleteRepeateData(dto);//删除重复数据
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("解析资金资金报文类型为[BEPS122]的文件出错："+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}
	
	private String trasforDate(String oriDate){
		return oriDate.replace("年", "").replace("月", "").replace("日", "");
	}
}
