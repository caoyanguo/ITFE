package com.cfcc.itfe.service.dataquery.directpayquery;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.IServiceManagerServer;
import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time 09-11-07 14:32:31 codecomment:
 */
@SuppressWarnings("unchecked")
public class DirectPayService extends AbstractDirectPayService {
	private static Log log = LogFactory.getLog(DirectPayService.class);
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;
	/**
	 * 分页查询直接支付额度主信息
	 * 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findMainByPage(TvDirectpaymsgmainDto mainDto,
			PageRequest pageRequest,String expfunccode,String payamt) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			// 取得票据编号 - 支持模糊查询
			String wherestr = PublicSearchFacade.changeFileName(null,  mainDto.getSfilename());
			mainDto.setSfilename(null);
			if(mainDto.getSdemo()!=null)
			{
				if(wherestr!=null&&!"".equals(wherestr))
					wherestr=" and "+mainDto.getSdemo();
				else
					wherestr =mainDto.getSdemo();
				mainDto.setSdemo(null);
				mainDto.setScommitdate(null);
			}
			wherestr = CommonUtil.getFuncCodeSql(wherestr, mainDto, expfunccode,payamt);
			if(null == wherestr){
				return CommonFacade.getODB().findRsByDtoPaging(mainDto,
						pageRequest,null," S_ORGCODE,S_TRECODE ");
			}else{
				return CommonFacade.getODB().findRsByDtoPaging(mainDto,
						pageRequest,wherestr," S_ORGCODE,S_TRECODE ");
			}
		} catch (JAFDatabaseException e) {
			log.error("分页查询直接支付主信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付主信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询直接支付主信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付主信息时错误!", e);
		}
	}
	/**
	 * 分页查询直接支付额度主信息(历史表)
	 * 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findMainByPageForHis(HtvDirectpaymsgmainDto mainDto,
			PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			// 取得票据编号 - 支持模糊查询
			String wherestr = PublicSearchFacade.changeFileName(null,  mainDto.getSfilename());
			mainDto.setSfilename(null);
			if(mainDto.getSdemo()!=null)
			{
				if(wherestr!=null&&!"".equals(wherestr))
					wherestr+=" and "+mainDto.getSdemo();
				else
					wherestr =mainDto.getSdemo();
				mainDto.setSdemo(null);
				mainDto.setScommitdate(null);
			}
			wherestr = CommonUtil.getFuncCodeSql(wherestr, mainDto, expfunccode,payamt);
			if(null == wherestr){
				return CommonFacade.getODB().findRsByDtoPaging(mainDto,
						pageRequest,null," S_ORGCODE,S_TRECODE ");
			}else{
				return CommonFacade.getODB().findRsByDtoPaging(mainDto,
						pageRequest,wherestr," S_ORGCODE,S_TRECODE ");
			}
		} catch (JAFDatabaseException e) {
			log.error("分页查询直接支付历史主信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付历史主信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询直接支付历史主信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付历史主信息时错误!", e);
		}
	}

	/**
	 * 分页查询直接支付额度子信息
	 * 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findSubByPage(TvDirectpaymsgmainDto mainDto,
			PageRequest pageRequest,String expfunccode,String payamt) throws ITFEBizException {
		if (null == mainDto || null == mainDto.getSorgcode()
				|| "".equals(mainDto.getSorgcode().trim())
				|| null == mainDto.getSofyear()
				|| "".equals(mainDto.getSofyear().trim())
				|| null == mainDto.getStaxticketno()
				|| "".equals(mainDto.getStaxticketno().trim())
				|| null == mainDto.getSpackageno()
				|| "".equals(mainDto.getSpackageno().trim())) {
			return null;
		}
		   
		TvDirectpaymsgsubDto subdto = new TvDirectpaymsgsubDto();
		subdto.setIvousrlno(mainDto.getIvousrlno());
		if(expfunccode!=null && !"".equals(expfunccode)){
			subdto.setSfunsubjectcode(expfunccode);
		}
		if(payamt!=null && !"".equals(payamt)){
			subdto.setNmoney(BigDecimal.valueOf(Long.valueOf(payamt)));
		}
//		subdto.setSorgcode(mainDto.getSorgcode());
//		subdto.setSofyear(mainDto.getSofyear());
//		subdto.setStaxticketno(mainDto.getStaxticketno());
//		subdto.setSpackageno(mainDto.getSpackageno());

		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest,null," S_FUNSUBJECTCODE ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询直接支付子信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付子信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询直接支付子信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付子信息时错误!", e);
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
	public String reSendMsg(String scommitdate, String spackageno,
			String sorgcode, String sfilename) throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		
		if(!orgcode.equals(sorgcode)){
			log.error("没有权限操作[" + sorgcode +"]机构的数据!");
			throw new ITFEBizException("没有权限操作[" + sorgcode +"]机构的数据!");
		}
		
		String smsgno = MsgConstant.MSG_NO_5102;
		
		// 取得对应的报文处理类
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
				MsgConstant.SPRING_SERVICE_SERVER + smsgno);

		iservice.sendMsg(sfilename, sorgcode, spackageno, smsgno, scommitdate, null,true);
		
		return null;
	}
	public PageResponse findSubByPageForHis(HtvDirectpaymsgmainDto mainDto,
			PageRequest pageRequest,String expfunccode,String payamt) throws ITFEBizException {
		if (null == mainDto || null == mainDto.getSorgcode()
				|| "".equals(mainDto.getSorgcode().trim())
				|| null == mainDto.getSofyear()
				|| "".equals(mainDto.getSofyear().trim())
				|| null == mainDto.getStaxticketno()
				|| "".equals(mainDto.getStaxticketno().trim())
				|| null == mainDto.getSpackageno()
				|| "".equals(mainDto.getSpackageno().trim())) {
			return null;
		}
		   
		HtvDirectpaymsgsubDto subdto = new HtvDirectpaymsgsubDto();
		subdto.setSorgcode(mainDto.getSorgcode());
		subdto.setSofyear(mainDto.getSofyear());
		subdto.setStaxticketno(mainDto.getStaxticketno());
		subdto.setSpackageno(mainDto.getSpackageno());
		if(expfunccode!=null && !"".equals(expfunccode)){
			subdto.setSfunsubjectcode(expfunccode);
		}
		if(payamt!=null && !"".equals(payamt)){
			subdto.setNmoney(BigDecimal.valueOf(Long.valueOf(payamt)));
		}
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest,null," S_FUNSUBJECTCODE ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询直接支付历史子信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付历史子信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询直接支付历史子信息时错误!", e);
			throw new ITFEBizException("分页查询直接支付历史子信息时错误!", e);
		}
	}
	
	/**
	 * 导出txt	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public String dataexport(IDto mainDto, String selectedtable) throws ITFEBizException{
	   
	// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		String filename="";
		
		try {
//			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			startdate = TimeFacade.getCurrentStringTime();
			if(startdate.equals(enddate))
				pici++;
			else
				enddate = TimeFacade.getCurrentStringTime();
			if(selectedtable.equals("0")){
				TvDirectpaymsgmainDto mdto=(TvDirectpaymsgmainDto)mainDto;
				mdto.setSorgcode(orgcode);//				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				list=CommonFacade.getODB().findRsByDto(mdto);
			}else if(selectedtable.equals("1")){
				TvDirectpaymsgmainDto mdto=(TvDirectpaymsgmainDto)mainDto;
				HtvDirectpaymsgmainDto htvdto=new HtvDirectpaymsgmainDto();
				htvdto.setStrecode(mdto.getStrecode());
				htvdto.setScommitdate(mdto.getScommitdate());
				htvdto.setSpayunit(mdto.getSpayunit());
				htvdto.setStransbankcode(mdto.getStransbankcode());
				htvdto.setNmoney(mdto.getNmoney());
				htvdto.setStaxticketno(mdto.getStaxticketno());
				htvdto.setSofyear(mdto.getSofyear());
				htvdto.setSpackageno(mdto.getSpackageno());
				htvdto.setSdealno(mdto.getSdealno());
				htvdto.setSfilename(mdto.getSfilename());
				htvdto.setSstatus(mdto.getSstatus());//				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				htvdto.setSorgcode(orgcode);
				list=CommonFacade.getODB().findRsByDto(htvdto);
			}
			filename=TimeFacade.getCurrentStringTime()+"01"+(pici<10?"0"+pici:pici)+"hd.txt";
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = ",";//"\t"; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
//				List sublist = new ArrayList();
//				int index=1;
				if(selectedtable.equals("0")){
					filebuf.append("文件名称,国库代码,银行代码,凭证编号,交易流水号,付款人账号,付款人全称,付款人开户银行,收款人账号,收款人全称,收款人开户行,预算种类,金额,分包凭证编号,凭证日期,处理结果,说明\r\n");
					for (TvDirectpaymsgmainDto _dto :(List<TvDirectpaymsgmainDto>) list) {
						TvDirectpaymsgsubDto subPara = new TvDirectpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
//						subPara.setSofyear(_dto.getSofyear());
//						subPara.setStaxticketno(_dto.getStaxticketno());
//						subPara.setSpackageno(_dto.getSpackageno());
//						sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						filebuf.append(_dto.getSfilename());//文件名称
						filebuf.append(splitSign);
						filebuf.append(_dto.getStrecode());//国库代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransactunit());//银行代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdealno());//交易流水号
						filebuf.append(splitSign);
						filebuf.append("");//付款人帐号
						filebuf.append(splitSign);
						filebuf.append("");//付款人全称
						filebuf.append(splitSign);
						filebuf.append("");//付款人开户银行
						filebuf.append(splitSign);
						filebuf.append("");//收款人帐号
						filebuf.append(splitSign);
						filebuf.append("");;//收款人全称
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransbankcode());//收款人开户行
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//合计金额
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpackageticketno()); //分包对应凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSgenticketdate());//凭证日期
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//处理结果1成功，0失败
						else
							filebuf.append("0");//处理结果1成功，0失败
						filebuf.append(splitSign);
						if("80000".equals(_dto.getSstatus()))
							filebuf.append("成功");//说明
						else if("80001".equals(_dto.getSstatus()))
							filebuf.append("失败");//说明
						else if("80002".equals(_dto.getSstatus()))
							filebuf.append("处理中");//说明
						else if("80008".equals(_dto.getSstatus()))
							filebuf.append("未发送");//说明
						else if("80009".equals(_dto.getSstatus()))
							filebuf.append("待处理");//说明
						else
							filebuf.append(_dto.getSdemo());//说明
						filebuf.append("\r\n");
//						//子表信息
//						for(TvDirectpaymsgsubDto sub : (List<TvDirectpaymsgsubDto>)sublist){
//							filebuf.append(sub.getSfunsubjectcode());//功能科目代码
//							filebuf.append(splitSign);
//							filebuf.append(sub.getSecosubjectcode());//经济科目代码
//							filebuf.append(splitSign);
//							filebuf.append(sub.getSbudgetunitcode());//预算单位代码
//							filebuf.append(splitSign);
//							filebuf.append(sub.getNmoney());//发生额
//							filebuf.append("\r\n");
//						}
					}
				}else if(selectedtable.equals("1")){
					for (HtvDirectpaymsgmainDto _dto :(List<HtvDirectpaymsgmainDto>) list) {
						HtvDirectpaymsgsubDto subPara = new HtvDirectpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
//						subPara.setSofyear(_dto.getSofyear());
//						subPara.setStaxticketno(_dto.getStaxticketno());
//						subPara.setSpackageno(_dto.getSpackageno());
//						sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						filebuf.append(_dto.getSfilename());//文件名称
						filebuf.append(splitSign);
						filebuf.append(_dto.getStrecode());//国库代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransactunit());//银行代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdealno());//交易流水号
						filebuf.append(splitSign);
						filebuf.append("");//付款人帐号
						filebuf.append(splitSign);
						filebuf.append("");//付款人全称
						filebuf.append(splitSign);
						filebuf.append("");//付款人开户银行
						filebuf.append(splitSign);
						filebuf.append("");//收款人帐号
						filebuf.append(splitSign);
						filebuf.append("");;//收款人全称
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransbankcode());//收款人开户行
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//合计金额
						filebuf.append(splitSign);
						filebuf.append(_dto.getSgenticketdate());//凭证日期
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//处理结果1成功，0失败
						else
							filebuf.append("0");//处理结果1成功，0失败
						filebuf.append(splitSign);
						if("80000".equals(_dto.getSstatus()))
							filebuf.append("成功");//说明
						else if("80001".equals(_dto.getSstatus()))
							filebuf.append("失败");//说明
						else if("80002".equals(_dto.getSstatus()))
							filebuf.append("处理中");//说明
						else if("80008".equals(_dto.getSstatus()))
							filebuf.append("未发送");//说明
						else if("80009".equals(_dto.getSstatus()))
							filebuf.append("待处理");//说明
						else
							filebuf.append(_dto.getSdemo());//说明
						filebuf.append("\r\n");
						//子表信息
//						for(TvDirectpaymsgsubDto sub : (List<TvDirectpaymsgsubDto>)sublist){
//							filebuf.append(sub.getSfunsubjectcode());//功能科目代码
//							filebuf.append(splitSign);
//							filebuf.append(sub.getSecosubjectcode());//经济科目代码
//							filebuf.append(splitSign);
//							filebuf.append(sub.getSbudgetunitcode());//预算单位代码
//							filebuf.append(splitSign);
//							filebuf.append(sub.getNmoney());//发生额
//							filebuf.append("\r\n");
//						}
					}
				}
			
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
			    return fullpath.replaceAll(root, "");
				
			} else {
				throw new ITFEBizException("查询无数据");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	   
   }
   
   
   /**
	 * 导出CSV	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
	public String exportFile(IDto mainDto, String selectedtable)
			throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		try {
			if(selectedtable.equals("0")){
				TvDirectpaymsgmainDto mdto=(TvDirectpaymsgmainDto)mainDto;
				mdto.setSorgcode(orgcode);
				String where = null;
				if(mdto.getSdemo()!=null)
				{
					where=" and "+mdto.getSdemo();
					mdto.setSdemo(null);
					mdto.setScommitdate(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto, where);
			}else if(selectedtable.equals("1")){
				TvDirectpaymsgmainDto mdto=(TvDirectpaymsgmainDto)mainDto;
				HtvDirectpaymsgmainDto htvdto=new HtvDirectpaymsgmainDto();
				htvdto.setStrecode(mdto.getStrecode());
				htvdto.setScommitdate(mdto.getScommitdate());
				htvdto.setSpayunit(mdto.getSpayunit());
				htvdto.setStransbankcode(mdto.getStransbankcode());
				htvdto.setNmoney(mdto.getNmoney());
				htvdto.setStaxticketno(mdto.getStaxticketno());
				htvdto.setSofyear(mdto.getSofyear());
				htvdto.setSpackageno(mdto.getSpackageno());
				htvdto.setSdealno(mdto.getSdealno());
				htvdto.setSfilename(mdto.getSfilename());
				htvdto.setSstatus(mdto.getSstatus());
				htvdto.setSorgcode(orgcode);
				String where = null;
				if(mdto.getSdemo()!=null)
				{
					where=" and "+mdto.getSdemo();
					htvdto.setSdemo(null);
					htvdto.setScommitdate(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(htvdto,where);
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+"01.csv";
			String splitSign = ","; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){//当前主表
					for (TvDirectpaymsgmainDto _dto :(List<TvDirectpaymsgmainDto>) list) {
						filebuf.append(_dto.getStrecode());//国库代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayacctno()==null?"":_dto.getSpayacctno());//付款人帐号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayacctname()==null?"":_dto.getSpayacctname());//付款人全称
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpaybankno()==null?"":_dto.getSpaybankno());//付款人开户银行
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacctno()==null?"":_dto.getSpayeeacctno());//收款人帐号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacctname()==null?"":_dto.getSpayeeacctname());;//收款人全称
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransbankcode()==null?"":_dto.getStransbankcode());//收款人开户行
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//合计金额
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpackageticketno()); //分包对应凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSgenticketdate());//凭证日期
						filebuf.append("\r\n");
						
						//根据主表获取当前子表信息
						List sublist = new ArrayList();
						TvDirectpaymsgsubDto subPara = new TvDirectpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
						subPara.setIvousrlno(_dto.getIvousrlno());
						sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						for(TvDirectpaymsgsubDto sub : (List<TvDirectpaymsgsubDto>)sublist){
							filebuf.append(sub.getSfunsubjectcode());//功能科目代码
							filebuf.append(splitSign);
							filebuf.append(sub.getSecosubjectcode()==null?"":sub.getSecosubjectcode());//经济科目代码
							filebuf.append(splitSign);
							filebuf.append(sub.getSbudgetunitcode());//预算单位代码
							filebuf.append(splitSign);
							filebuf.append(sub.getNmoney());//发生额
							filebuf.append("\r\n");
						}
					}
				}else if(selectedtable.equals("1")){//历史主表
					for (HtvDirectpaymsgmainDto _dto :(List<HtvDirectpaymsgmainDto>) list) {
						filebuf.append(_dto.getStrecode());//国库代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayacctno()==null?"":_dto.getSpayacctno());//付款人帐号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayacctname()==null?"":_dto.getSpayacctname());//付款人全称
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpaybankno()==null?"":_dto.getSpaybankno());//付款人开户银行
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacctno()==null?"":_dto.getSpayeeacctno());//收款人帐号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacctname()==null?"":_dto.getSpayeeacctname());;//收款人全称
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransbankcode()==null?"":_dto.getStransbankcode());//收款人开户行
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//合计金额
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpackageticketno()); //分包对应凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSgenticketdate());//凭证日期
						filebuf.append("\r\n");
						
						//根据主表获取历史子表信息
						HtvDirectpaymsgsubDto subPara = new HtvDirectpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
						subPara.setIvousrlno(_dto.getIvousrlno());
						List<HtvDirectpaymsgsubDto> sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						for(HtvDirectpaymsgsubDto sub : (List<HtvDirectpaymsgsubDto>)sublist){
							filebuf.append(sub.getSfunsubjectcode());//功能科目代码
							filebuf.append(splitSign);
							filebuf.append(sub.getSecosubjectcode()==null?"":sub.getSecosubjectcode());//经济科目代码
							filebuf.append(splitSign);
							filebuf.append(sub.getSbudgetunitcode());//预算单位代码
							filebuf.append(splitSign);
							filebuf.append(sub.getNmoney());//发生额
							filebuf.append("\r\n");
						}
					}
				}
			
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
			    return fullpath.replaceAll(root, "");
				
			} else {
				throw new ITFEBizException("查询无数据");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}
   
   
   
}