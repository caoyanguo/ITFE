package com.cfcc.itfe.service.dataquery.commapplypayquery;

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
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time   13-02-20 14:59:00
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class CommApplyPayQueryService extends AbstractCommApplyPayQueryService {
	private static Log log = LogFactory.getLog(CommApplyPayQueryService.class);	
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;
	/**
	 * 商行办理支付划款申请主体信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findMainByPage(TvPayreckBankDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSbookorgcode(orgcode);
			String where = null;
			if(mainDto.getSdescription()!=null)
			{
				where = mainDto.getSdescription();
				mainDto.setSdescription(null);
			}
			where = CommonUtil.getFuncCodeSql(where, mainDto, expfunccode, payamt);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, where, " S_BOOKORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询商行办理支付划款申请主体信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请主体信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询商行办理支付划款申请主体信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请主体信息时错误!", e);
		}
    }

	/**
	 * 商行办理支付划款申请明细信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findSubByPage(TvPayreckBankDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	if (null == mainDto  || null == mainDto.getSpackno()) {
			return null;
		}
    	TvPayreckBankListDto subdto = new TvPayreckBankListDto();
    	subdto.setIvousrlno(mainDto.getIvousrlno());//凭证流水号
    	if(expfunccode!=null && !"".equals(expfunccode)){
    		subdto.setSfuncbdgsbtcode(expfunccode);
    	}
    	if(payamt!=null && !"".equals(payamt)){
    		subdto.setFamt(BigDecimal.valueOf(Long.valueOf(payamt)));
    	}
    	/*subdto.setDentrustdate(mainDto.getDacceptdate());
    	subdto.setStrano(mainDto.getStrano());
    	subdto.setSagentbnkcode(mainDto.getSagentbnkcode());
    	subdto.setSpackno(mainDto.getSpackno());*/
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, " 1=1", null);
		} catch (JAFDatabaseException e) {
			log.error("分页查询商行办理支付划款申请明细信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请明细信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询商行办理支付划款申请明细信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请明细信息时错误!", e);
		}
    }

	/**
	 * 商行办理支付划款申请主体信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findMainByPageForHis(HtvPayreckBankDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSbookorgcode(orgcode);
			String where = null;
			if(mainDto.getSdescription()!=null)
			{
				where = mainDto.getSdescription();
				mainDto.setSdescription(null);
			}
			where = CommonUtil.getFuncCodeSql(where, mainDto, expfunccode, payamt);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, where, " S_BOOKORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询商行办理支付划款申请主体信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请历史主体信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询商行办理支付划款申请主体信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请历史主体信息时错误!", e);
		}
    }

	/**
	 * 商行办理支付划款申请明细信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findSubByPageForHis(HtvPayreckBankDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	if (null == mainDto || null == mainDto.getSpackno()) {
			return null;
		}
    	HtvPayreckBankListDto subdto = new HtvPayreckBankListDto();
    	subdto.setIvousrlno(mainDto.getIvousrlno());//凭证流水号
    	if(expfunccode!=null && !"".equals(expfunccode)){
    		subdto.setSfuncbdgsbtcode(expfunccode);
    	}
    	if(payamt!=null && !"".equals(payamt)){
    		subdto.setFamt(BigDecimal.valueOf(Long.valueOf(payamt)));
    	}
    	/*subdto.setDentrustdate(mainDto.getDacceptdate());
    	subdto.setStrano(mainDto.getStrano());
    	subdto.setSagentbnkcode(mainDto.getSagentbnkcode());
    	subdto.setSpackno(mainDto.getSpackno());*/
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, " 1=1", null);
		} catch (JAFDatabaseException e) {
			log.error("分页查询商行办理支付划款申请历史明细信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请历史明细信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询商行办理支付划款申请历史明细信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请历史明细信息时错误!", e);
		}
    }
	public String exportCommApplyPay(IDto finddto, String selectedtable) throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		String filename="";
		
		try {
//			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			if(selectedtable.equals("0")){
				TvPayreckBankDto mdto=(TvPayreckBankDto)finddto;
				mdto.setSbookorgcode(orgcode);
				list=CommonFacade.getODB().findRsByDto(mdto);
				startdate = TimeFacade.getCurrentStringTime();
				if(startdate.equals(enddate))
					pici++;
				else
					enddate = TimeFacade.getCurrentStringTime();
				if("0".equals(mdto.getSpaymode()))//0直接1授权
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+(pici<10?"0"+pici:pici)+"hd.txt";
				else
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+(pici<10?"0"+pici:pici)+"hd.txt";
			}else if(selectedtable.equals("1")){
				HtvPayreckBankDto mdto=(HtvPayreckBankDto)finddto;
				mdto.setSbookorgcode(orgcode);
				list=CommonFacade.getODB().findRsByDto(mdto);
				if("0".equals(mdto.getSpaymode()))//0直接1授权
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+"hd.txt";
				else
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+"hd.txt";
			}
			
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = ",";//"\t"; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					TvPayreckBankListDto subPara = null;
					for (TvPayreckBankDto _dto :(List<TvPayreckBankDto>) list) {
						subPara = new TvPayreckBankListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
//						if(list.size()>1)
//							filebuf.append("**");
						filebuf.append(_dto.getStrecode());//付款国库代码
						filebuf.append(splitSign+_dto.getSpayername());//付款人名称
						filebuf.append(splitSign+_dto.getSpayeracct());//付款人账号
						filebuf.append(splitSign+"");//付款人开户行代码
						filebuf.append(splitSign+"");//付款人开户行行号
//						String bankname = bankMap.get(_dto.getSrecbankno()).getSbankname();//接收行名
						filebuf.append(splitSign+_dto.getSpayeename());//收款人名称
						filebuf.append(splitSign+_dto.getSpayeeacct());//收款人账号
						filebuf.append(splitSign+_dto.getSagentbnkcode());//代理银行代码
						filebuf.append(splitSign+_dto.getSpayeeopbkno());//收款人开户行行号
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign+_dto.getFamt());//零余额发生额
						filebuf.append(splitSign+"0.00");//小额现金发生额
						filebuf.append(splitSign);//摘要代码
						filebuf.append(splitSign+_dto.getSvouno());//凭证编号
						String dates = _dto.getDvoudate().toString();
						filebuf.append(splitSign+dates.replaceAll("-", ""));//凭证日期
						filebuf.append(splitSign+_dto.getSaddword());//附言
						filebuf.append(splitSign);//是否成功
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSresult()))
							filebuf.append("1");//处理结果1成功，0失败
						else
							filebuf.append("0");//处理结果1成功，0失败
						filebuf.append(splitSign);//原因
						filebuf.append("\r\n");
//						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
//						for (TvPayreckBankListDto sub :(List<TvPayreckBankListDto>) sublist) {
//							filebuf.append(sub.getSbdgorgcode());//预算单位代码
//							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//功能类科目代码
//							filebuf.append(splitSign+sub.getSecnomicsubjectcode());//经济类科目代码
//							filebuf.append(splitSign+sub.getSacctprop());//账户性质
//							filebuf.append(splitSign+sub.getFamt());//支付金额
//							filebuf.append("\r\n");
//						}
					}
				}else if(selectedtable.equals("1")){
					HtvPayreckBankListDto subPara = null;
					for (HtvPayreckBankDto _dto :(List<HtvPayreckBankDto>) list) {
						subPara = new HtvPayreckBankListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
						if(list.size()>1)
							filebuf.append("**");
						filebuf.append(_dto.getStrecode());//付款国库代码
						filebuf.append(splitSign+_dto.getSpayername());//付款人名称
						filebuf.append(splitSign+_dto.getSpayeracct());//付款人账号
						filebuf.append(splitSign+"");//付款人开户行代码
						filebuf.append(splitSign+"");//付款人开户行行号
						filebuf.append(splitSign+_dto.getSpayeename());//收款人名称
						filebuf.append(splitSign+_dto.getSpayeeacct());//收款人账号
						filebuf.append(splitSign+_dto.getSagentbnkcode());//代理银行代码
						filebuf.append(splitSign+_dto.getSpayeeopbkno());//收款人开户行行号
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign+_dto.getFamt());//零余额发生额
						filebuf.append(splitSign+"0.00");//小额现金发生额
						filebuf.append(splitSign);//摘要代码
						filebuf.append(splitSign+_dto.getSvouno());//凭证编号
						filebuf.append(splitSign+_dto.getDvoudate().toString().replaceAll("-", ""));//凭证日期
						filebuf.append(splitSign+_dto.getSaddword());//附言
						filebuf.append(splitSign);//是否成功
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSresult()))
							filebuf.append("1");//处理结果1成功，0失败
						else
							filebuf.append("0");//处理结果1成功，0失败
						filebuf.append(splitSign+_dto.getSresult()+_dto.getSdescription());//原因
						filebuf.append("\r\n");
//						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
//						for (HtvPayreckBankListDto sub :(List<HtvPayreckBankListDto>) sublist) {
//							filebuf.append(sub.getSbdgorgcode());//预算单位代码
//							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//功能类科目代码
//							filebuf.append(splitSign+sub.getSecnomicsubjectcode());//经济类科目代码
//							filebuf.append(splitSign+sub.getSacctprop());//账户性质
//							filebuf.append(splitSign+sub.getFamt());//支付金额
//							filebuf.append("\r\n");
//						}
					}
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
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
	 * 导出主子表信息 
	 * @generated
	 * @param findidto
	 * @param selecttable
	 * @return String
	 * @throws ITFEBizException	 
	 */
	public String exportFile(IDto finddto, String selectedtable)
			throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		String filename="";
		try {
			if(selectedtable.equals("0")){
				TvPayreckBankDto mdto=(TvPayreckBankDto)finddto;
				mdto.setSbookorgcode(orgcode);
				String where = null;
				if(mdto.getSdescription()!=null)
				{
					where = " and "+mdto.getSdescription();
					mdto.setSdescription(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto, where);
				if("0".equals(mdto.getSpaymode()))//0直接1授权
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+".csv";
				else
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+".csv";
			}else if(selectedtable.equals("1")){
				TvPayreckBankDto mdto=(TvPayreckBankDto)finddto;
				HtvPayreckBankDto hmdto=new HtvPayreckBankDto();
				hmdto.setSbookorgcode(orgcode);
				hmdto.setStrecode(mdto.getStrecode());
				hmdto.setDentrustdate(mdto.getDentrustdate());
				hmdto.setSpackno(mdto.getSpackno());
				hmdto.setSpaymode(mdto.getSpaymode());
				hmdto.setSfinorgcode(mdto.getSfinorgcode());
				hmdto.setSagentbnkcode(mdto.getSagentbnkcode());
				hmdto.setStrano(mdto.getStrano());
				hmdto.setSvouno(mdto.getSvouno());
				hmdto.setDvoudate(mdto.getDvoudate());
				hmdto.setFamt(mdto.getFamt());
				hmdto.setSpayeracct(mdto.getSpayeracct());
				hmdto.setSpayeeacct(mdto.getSpayeeacct());
				hmdto.setSpayeeopbkno(mdto.getSpayeeopbkno());
				hmdto.setSofyear(mdto.getSofyear());
				hmdto.setStrimsign(mdto.getStrimsign());
				hmdto.setSbudgettype(mdto.getSbudgettype());
				String where = null;
				if(mdto.getSdescription()!=null)
				{
					where = " and "+mdto.getSdescription();
					mdto.setSdescription(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(hmdto,where);
				if("0".equals(mdto.getSpaymode()))//0直接1授权
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+".csv";
				else
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+".csv";
			}
			
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+filename;
			String splitSign = ",";// 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					for (TvPayreckBankDto _dto :(List<TvPayreckBankDto>) list) {
						filebuf.append(_dto.getStrecode());//付款国库代码
						filebuf.append(splitSign+_dto.getSpayername());//付款人名称
						filebuf.append(splitSign+_dto.getSpayeracct());//付款人账号
						filebuf.append(splitSign+"");//付款人开户行代码
						filebuf.append(splitSign+"");//付款人开户行行号
						filebuf.append(splitSign+_dto.getSpayeename());//收款人名称
						filebuf.append(splitSign+_dto.getSpayeeacct());//收款人账号
						filebuf.append(splitSign+_dto.getSagentbnkcode());//代理银行代码
						filebuf.append(splitSign+_dto.getSpayeeopbkno());//收款人开户行行号
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign+_dto.getFamt());//零余额发生额
						filebuf.append(splitSign+"0.00");//小额现金发生额
						filebuf.append(splitSign);//摘要代码
						filebuf.append(splitSign+_dto.getSvouno());//凭证编号
						String dates = _dto.getDvoudate().toString();
						filebuf.append(splitSign+dates.replaceAll("-", ""));//凭证日期
						filebuf.append("\r\n");
						TvPayreckBankListDto subPara  = new TvPayreckBankListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
						for (TvPayreckBankListDto sub :(List<TvPayreckBankListDto>) sublist) {
							filebuf.append(sub.getSbdgorgcode());//预算单位代码
							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//功能类科目代码
							filebuf.append(splitSign+((sub.getSecnomicsubjectcode()==null)?"":sub.getSecnomicsubjectcode()));//经济类科目代码
							filebuf.append(splitSign+sub.getSacctprop());//账户性质
							filebuf.append(splitSign+sub.getFamt());//支付金额
							filebuf.append(splitSign+sub.getSvouchern0());//支付凭证单号
							filebuf.append("\r\n");
						}
					}
				}else if(selectedtable.equals("1")){
					for (HtvPayreckBankDto _dto :(List<HtvPayreckBankDto>) list) {
						filebuf.append(_dto.getStrecode());//付款国库代码
						filebuf.append(splitSign+_dto.getSpayername());//付款人名称
						filebuf.append(splitSign+_dto.getSpayeracct());//付款人账号
						filebuf.append(splitSign+"");//付款人开户行代码
						filebuf.append(splitSign+"");//付款人开户行行号
						filebuf.append(splitSign+_dto.getSpayeename());//收款人名称
						filebuf.append(splitSign+_dto.getSpayeeacct());//收款人账号
						filebuf.append(splitSign+_dto.getSagentbnkcode());//代理银行代码
						filebuf.append(splitSign+_dto.getSpayeeopbkno());//收款人开户行行号
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign+_dto.getFamt());//零余额发生额
						filebuf.append(splitSign+"0.00");//小额现金发生额
						filebuf.append(splitSign);//摘要代码
						filebuf.append(splitSign+_dto.getSvouno());//凭证编号
						filebuf.append(splitSign+_dto.getDvoudate().toString().replaceAll("-", ""));//凭证日期
						filebuf.append("\r\n");
						HtvPayreckBankListDto subPara  = new HtvPayreckBankListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
						for (HtvPayreckBankListDto sub :(List<HtvPayreckBankListDto>) sublist) {
							filebuf.append(sub.getSbdgorgcode());//预算单位代码
							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//功能类科目代码
							filebuf.append(splitSign+((sub.getSecnomicsubjectcode()==null)?"":sub.getSecnomicsubjectcode()));//经济类科目代码
							filebuf.append(splitSign+sub.getSacctprop());//账户性质
							filebuf.append(splitSign+sub.getFamt());//支付金额
							filebuf.append(splitSign+sub.getSvouchern0());//支付凭证单号
							filebuf.append("\r\n");
						}
					}
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
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