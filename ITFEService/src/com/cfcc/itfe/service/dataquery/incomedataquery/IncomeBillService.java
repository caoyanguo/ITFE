package com.cfcc.itfe.service.dataquery.incomedataquery;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.msgmanager.core.IServiceManagerServer;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time 09-11-06 18:18:43 codecomment:
 */

public class IncomeBillService extends AbstractIncomeBillService {
	private static Log log = LogFactory.getLog(IncomeBillService.class);

	/**
	 * 分页查询税票收入信息
	 * 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findIncomeByPage(TvInfileDto findDto, PageRequest pageRequest) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			findDto.setSorgcode(orgcode);
			// 取得票据编号 - 支持模糊查询
			String wherestr = PublicSearchFacade.changeFileName(null, findDto.getSfilename());
			findDto.setSfilename(null);

			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, null, " S_ORGCODE,S_RECVTRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, wherestr, " S_ORGCODE,S_RECVTRECODE ");
			}
			
		} catch (JAFDatabaseException e) {
			log.error("分页查询税票收入信息时错误!", e);
			throw new ITFEBizException("分页查询税票收入信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询税票收入信息时错误!", e);
			throw new ITFEBizException("分页查询税票收入信息时错误!", e);
		}
	}
	/**
	 * 分页查询税票收入信息
	 * 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findIncomeByPage(TvInfileDetailDto findDto, PageRequest pageRequest) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			findDto.setSorgcode(orgcode);
			// 取得票据编号 - 支持模糊查询
			String wherestr = PublicSearchFacade.changeFileName(null, findDto.getSfilename());
			findDto.setSfilename(null);

			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, null, " S_ORGCODE,S_RECVTRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, wherestr, " S_ORGCODE,S_RECVTRECODE ");
			}
			
		} catch (JAFDatabaseException e) {
			log.error("分页查询税票收入信息时错误!", e);
			throw new ITFEBizException("分页查询税票收入信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询税票收入信息时错误!", e);
			throw new ITFEBizException("分页查询税票收入信息时错误!", e);
		}
	}

	/**
	 * 重发TIPS没有收到的报文
	 * 
	 * @generated
	 * @param scommitdate
	 * @param spackageno
	 * @param sorgcode
	 * @param sfilename
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String reSendMsg(String scommitdate, String spackageno, String sorgcode, String sfilename) throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		
		if(!orgcode.equals(sorgcode)){
			log.error("没有权限操作[" + sorgcode +"]机构的数据!");
			throw new ITFEBizException("没有权限操作[" + sorgcode +"]机构的数据!");
		}
		
		String smsgno = MsgConstant.MSG_NO_7211;

		// 取得对应的报文处理类
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
				MsgConstant.SPRING_SERVICE_SERVER + smsgno);

		iservice.sendMsg(sfilename, sorgcode, spackageno, smsgno, scommitdate, null,true);

		return null;
	}

	/**
	 * 导出收入税票信息（以便修改后重新发送）
	 * 
	 * @generated
	 * @param String spackageno
	 * @param TvInfileDto findDt
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String exportIncomeData(String spackageno, TvInfileDto exportDto) throws ITFEBizException {
		try {
			List<TvInfileDto> list = CommonFacade.getODB().findRsByDto(exportDto);
			if(null == list || list.size() == 0){
				return null;
			}
			
			StringBuffer sbuf = new StringBuffer();
			for(int i = 0 ; i < list.size(); i++){
				TvInfileDto tmpdto = list.get(i);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSunitcode())); // 单位代码
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // 收款国库代码
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // 目的国库代码
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStbstaxorgcode())); // TBS征收机关代码
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(""); // 凭证编号
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybookkind())); // 缴款书种类代码
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // 开户银行
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // 预算级次	
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // 预算种类
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // 科目代码
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStbsassitsign())); // 辅助标志
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // 发生额(#.00)
//				sbuf.append(StateConstant.SPACE_SPLIT);
				
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxorgcode())); // 征收机关代码
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // 收款国库代码
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // 发生额(#.00)
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSgenticketdate())); // 开票日期
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // 预算科目
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // 预算级次	
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // 预算种类
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSassitsign())); // 辅助标志 	
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpaycode())); // 纳税人编码
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpayname())); // 纳税人名称
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybnkno())); // 付款行行号
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpayacct())); // 付款帐户
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // 付款开户行号
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxstartdate())); // 所属起始日期
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxenddate())); // 所属终止日期
				sbuf.append(StateConstant.SPACE_SPLIT);
			}
			
			return sbuf.toString();
		} catch (JAFDatabaseException e) {
			log.error("根据导出数据条件查询对应税票信息的时候出现错误!", e);
			throw new ITFEBizException("根据导出数据条件查询对应税票信息的时候出现错误!", e);
		} catch (ValidateException e) {
			log.error("根据导出数据条件查询对应税票信息的时候出现错误!", e);
			throw new ITFEBizException("根据导出数据条件查询对应税票信息的时候出现错误!", e);
		}
	}
	
	/**
	 * 导出所查询的所有税票信息
	 * 
	 * @generated
	 * @param List exportAlldataList
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String exportAllIncomeData(List exportAlldataList, String ifdetail)
			throws ITFEBizException {
		StringBuffer sbuf = new StringBuffer();
		try{
			if("1".equals(ifdetail)) {
				List<TvInfileDetailDto> list=(List<TvInfileDetailDto>)exportAlldataList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDetailDto tmp = list.get(i);
					TvInfileDto tmpdto = new TvInfileDto();
					tmpdto.setSrecvtrecode(tmp.getSrecvtrecode());
					tmpdto.setScommitdate(tmp.getScommitdate());
					tmpdto.setNmoney(tmp.getNmoney());
					tmpdto.setSfilename(tmp.getSfilename());
					tmpdto.setSpackageno(tmp.getSpackageno());
					tmpdto.setSdealno(tmp.getSdealno());
					tmpdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
					tmpdto.setStaxorgcode(tmp.getStaxorgcode());
					tmpdto.setStbsassitsign(tmp.getStbsassitsign());
					tmpdto.setSassitsign(tmp.getSassitsign());
					tmpdto.setSbudgettype(tmp.getSbudgettype());
					tmpdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
					tmpdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
					tmpdto.setSpaybookkind(tmp.getSpaybookkind());
					tmpdto.setStrasrlno(tmp.getStrasrlno());
					
					List<TvInfileDto> list1 = CommonFacade.getODB().findRsByDto(tmpdto);
					if(null!=list1&&list1.size()>0){
						for(int j = 0 ; j < list1.size(); j++){
							TvInfileDto tmpdto1 = list1.get(j);
							
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxorgcode())); // 征收机关代码
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSrecvtrecode())); // 收款国库代码
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getNmoney())); // 发生额(#.00)
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSgenticketdate())); // 开票日期
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetsubcode())); // 预算科目
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetlevelcode())); // 预算级次	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgettype())); // 预算种类
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSassitsign())); // 辅助标志 	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpaycode())); // 纳税人编码
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpayname())); // 纳税人名称
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpaybnkno())); // 付款行行号
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpayacct())); // 付款帐户
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSopenaccbankcode())); // 付款开户行号
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxstartdate())); // 所属起始日期
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxenddate())); // 所属终止日期
							sbuf.append(StateConstant.SPACE_SPLIT);
						}
					}
				}
			}else if("0".equals(ifdetail)){
				List<TvInfileDto> list=(List<TvInfileDto>)exportAlldataList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDto tmpdto = list.get(i);
					
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxorgcode())); // 征收机关代码
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // 收款国库代码
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // 发生额(#.00)
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSgenticketdate())); // 开票日期
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // 预算科目
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // 预算级次	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // 预算种类
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSassitsign())); // 辅助标志 	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpaycode())); // 纳税人编码
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpayname())); // 纳税人名称
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybnkno())); // 付款行行号
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpayacct())); // 付款帐户
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // 付款开户行号
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxstartdate())); // 所属起始日期
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxenddate())); // 所属终止日期
					sbuf.append(StateConstant.SPACE_SPLIT);
				}
			}
		
		} catch (JAFDatabaseException e) {
			log.error("根据导出数据条件查询对应税票信息的时候出现错误!", e);
			throw new ITFEBizException("根据导出数据条件查询对应税票信息的时候出现错误!", e);
		} catch (ValidateException e) {
			log.error("根据导出数据条件查询对应税票信息的时候出现错误!", e);
			throw new ITFEBizException("根据导出数据条件查询对应税票信息的时候出现错误!", e);
		}
		return sbuf.toString();
	}
	
	
	/**
	 * 导出所查询的选择税票信息
	 * 
	 * @generated
	 * @param List exportSelectedList
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String exportSelectedIncomeData(List exportSelectedList, String ifdetail)
			throws ITFEBizException {
		StringBuffer sbuf = new StringBuffer();
		try{
			if("1".equals(ifdetail)) {
				List<TvInfileDetailDto> list=(List<TvInfileDetailDto>)exportSelectedList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDetailDto tmp = list.get(i);
					TvInfileDto tmpdto = new TvInfileDto();
					tmpdto.setSrecvtrecode(tmp.getSrecvtrecode());
					tmpdto.setScommitdate(tmp.getScommitdate());
					tmpdto.setNmoney(tmp.getNmoney());
					tmpdto.setSfilename(tmp.getSfilename());
					tmpdto.setSpackageno(tmp.getSpackageno());
					tmpdto.setSdealno(tmp.getSdealno());
					tmpdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
					tmpdto.setStaxorgcode(tmp.getStaxorgcode());
					tmpdto.setStbsassitsign(tmp.getStbsassitsign());
					tmpdto.setSassitsign(tmp.getSassitsign());
					tmpdto.setSbudgettype(tmp.getSbudgettype());
					tmpdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
					tmpdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
					tmpdto.setSpaybookkind(tmp.getSpaybookkind());
					tmpdto.setStrasrlno(tmp.getStrasrlno());
					
					List<TvInfileDto> list1 = CommonFacade.getODB().findRsByDto(tmpdto);
					
					if(null!=list1&&list1.size()>0){
						for(int j = 0 ; j < list1.size(); j++){
							TvInfileDto tmpdto1 = list1.get(j);
							
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxorgcode())); // 征收机关代码
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSrecvtrecode())); // 收款国库代码
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getNmoney())); // 发生额(#.00)
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSgenticketdate())); // 开票日期
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetsubcode())); // 预算科目
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetlevelcode())); // 预算级次	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgettype())); // 预算种类
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSassitsign())); // 辅助标志 	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpaycode())); // 纳税人编码
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpayname())); // 纳税人名称
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpaybnkno())); // 付款行行号
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpayacct())); // 付款帐户
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSopenaccbankcode())); // 付款开户行号
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxstartdate())); // 所属起始日期
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxenddate())); // 所属终止日期
							sbuf.append(StateConstant.SPACE_SPLIT);
						}
					}
				}
			}else if("0".equals(ifdetail)){
				List<TvInfileDto> list=(List<TvInfileDto>)exportSelectedList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDto tmpdto = list.get(i);
					
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxorgcode())); // 征收机关代码
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // 收款国库代码
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // 发生额(#.00)
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSgenticketdate())); // 开票日期
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // 预算科目
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // 预算级次	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // 预算种类
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSassitsign())); // 辅助标志 	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpaycode())); // 纳税人编码
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpayname())); // 纳税人名称
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybnkno())); // 付款行行号
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpayacct())); // 付款帐户
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // 付款开户行号
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxstartdate())); // 所属起始日期
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxenddate())); // 所属终止日期
					sbuf.append(StateConstant.SPACE_SPLIT);
				}
			}
		
		} catch (JAFDatabaseException e) {
			log.error("根据导出数据条件查询对应税票信息的时候出现错误!", e);
			throw new ITFEBizException("根据导出数据条件查询对应税票信息的时候出现错误!", e);
		} catch (ValidateException e) {
			log.error("根据导出数据条件查询对应税票信息的时候出现错误!", e);
			throw new ITFEBizException("根据导出数据条件查询对应税票信息的时候出现错误!", e);
		}
		
		return sbuf.toString();
	}
	
	
	/**
	 * 分页查询税票收入信息
	 * 
	 * @generated
	 * @param findDto
	 * @return List
	 * @throws ITFEBizException
	 */
	public List findIncomeByDto(IDto findDto, String ifdetail) throws ITFEBizException {
		List list=new ArrayList();
		try {
			if("1".equals(ifdetail)) {
				TvInfileDetailDto tvinfiledetaildto=(TvInfileDetailDto)findDto;
				// 取得操作员的机构代码
				String orgcode = this.getLoginInfo().getSorgcode();
				tvinfiledetaildto.setSorgcode(orgcode);
				// 取得票据编号 - 支持模糊查询
				String wherestr = PublicSearchFacade.changeFileName(null, tvinfiledetaildto.getSfilename());
				tvinfiledetaildto.setSfilename(null);

				if (null == wherestr) {
					list= CommonFacade.getODB().findRsByDto(tvinfiledetaildto);
				} else {
					list= CommonFacade.getODB().findRsByDtoForWhere(tvinfiledetaildto, " and "+wherestr);
				}
			}else if("0".equals(ifdetail)){
				TvInfileDto tvinfileDto=(TvInfileDto)findDto;
				// 取得操作员的机构代码
				String orgcode = this.getLoginInfo().getSorgcode();
				tvinfileDto.setSorgcode(orgcode);
				// 取得票据编号 - 支持模糊查询
				String wherestr = PublicSearchFacade.changeFileName(null, tvinfileDto.getSfilename());
				tvinfileDto.setSfilename(null);

				if (null == wherestr) {
					list= CommonFacade.getODB().findRsByDto(tvinfileDto);
				} else {
					list= CommonFacade.getODB().findRsByDtoForWhere(tvinfileDto, " and "+wherestr);
				}
			}
			return list;
		} catch (JAFDatabaseException e) {
			log.error("查询税票收入信息时错误!", e);
			throw new ITFEBizException("查询税票收入信息时错误!", e);
		} catch (ValidateException e) {
			log.error("查询税票收入信息时错误!", e);
			throw new ITFEBizException("查询税票收入信息时错误!", e);
		}
	}
}