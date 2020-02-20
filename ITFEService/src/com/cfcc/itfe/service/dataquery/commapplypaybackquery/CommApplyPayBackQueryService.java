package com.cfcc.itfe.service.dataquery.commapplypaybackquery;

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
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time   13-02-21 14:34:21
 * codecomment: 
 */

public class CommApplyPayBackQueryService extends AbstractCommApplyPayBackQueryService {
	private static Log log = LogFactory.getLog(CommApplyPayBackQueryService.class);	
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;

	/**
	 * 退回商行办理支付划款申请主体信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findMainByPage(TvPayreckBankBackDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSbookorgcode(orgcode);
			String where = null;
			if(mainDto.getSaddword()!=null)
			{
				where = mainDto.getSaddword();
				mainDto.setSaddword(null);
			}
			where = CommonUtil.getFuncCodeSql(where, mainDto, expfunccode, payamt);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, where, " S_BOOKORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询商行办理支付划款申请退回主体信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请退回主体信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询商行办理支付划款申请退回主体信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请退回主体信息时错误!", e);
		}
    }

	/**
	 * 商行办理支付划款申请退回明细信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findSubByPage(TvPayreckBankBackDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	if (null == mainDto  || null == mainDto.getSpackno()) {
			return null;
		}
    	TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
    	subdto.setIvousrlno(mainDto.getIvousrlno());//凭证流水号
    	if(expfunccode!=null && !"".equals(expfunccode)){
    		subdto.setSfuncbdgsbtcode(expfunccode);
    	}
    	if(payamt!=null && !"".equals(payamt)){
    		subdto.setFamt(BigDecimal.valueOf(Long.valueOf(payamt)));
    	}
    	/*subdto.setDentrustdate(mainDto.getDentrustdate());
    	subdto.setSpackno(mainDto.getSpackno());
    	subdto.setSagentbnkcode(mainDto.getSagentbnkcode());
    	subdto.setStrano(mainDto.getStrano());*/
    	
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " I_SEQNO");
		} catch (JAFDatabaseException e) {
			log.error("分页查询商行办理支付划款申请退回明细信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请退回明细信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询商行办理支付划款申请退回明细信息时错误!", e);
			throw new ITFEBizException("分页查询商行办理支付划款申请退回明细信息时错误!", e);
		}
    }

	@SuppressWarnings("unchecked")
	public String exportCommApplyPayBack(IDto finddto, String selectedtable)throws ITFEBizException {
		
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
				TvPayreckBankBackDto mdto=(TvPayreckBankBackDto)finddto;
				mdto.setSbookorgcode(orgcode);
				String where = null;
				if(mdto.getSaddword()!=null)
				{
					where = " and "+mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,where);
				if("0".equals(mdto.getSpaymode()))//0直接1授权
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+(pici<10?"0"+pici:pici)+"hd.txt";
				else
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+(pici<10?"0"+pici:pici)+"hd.txt";
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
					TvPayreckBankBackListDto subPara = null;
					for (TvPayreckBankBackDto _dto :(List<TvPayreckBankBackDto>) list) {
						subPara = new TvPayreckBankBackListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
//						if(list.size()>1)
//							filebuf.append("**");
						filebuf.append(_dto.getStrecode());//收款国库代码
						filebuf.append(splitSign+_dto.getSpayername());//代理行名称-付款人名称
						filebuf.append(splitSign+_dto.getSpayeracct());//代理行收款账户-付款人账号
						filebuf.append(splitSign+_dto.getSagentbnkcode());//代理银行代码
						filebuf.append(splitSign+"");//代理银行开户行行号-付款人开户行代码
						filebuf.append(splitSign+_dto.getSpayeename());//国库收款人名称-收款人名称
						filebuf.append(splitSign+_dto.getSpayeeacct());//国库收款人账户-收款人账号
						filebuf.append(splitSign+"");//国库所在银行代码-收款人开户行代码
						filebuf.append(splitSign+"");//国库所在银行行号-收款人开户行行号
//						String bankname = bankMap.get(_dto.getSrecbankno()).getSbankname();//接收行名
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign+_dto.getFamt());//零余额发生额
						filebuf.append(splitSign+"0.00");//小额现金发生额
						filebuf.append(splitSign);//摘要代码
						filebuf.append(splitSign+_dto.getSvouno());//凭证编号
						filebuf.append(splitSign+_dto.getDvoudate().toString().replaceAll("-", ""));//凭证日期
						filebuf.append(splitSign+_dto.getSorivouno());//原凭证编号
						filebuf.append(splitSign+_dto.getDorivoudate().toString().replaceAll("-", ""));//原凭证日期
						filebuf.append(splitSign);//是否成功
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//处理结果1成功，0失败
						else
							filebuf.append("0");//处理结果1成功，0失败
						if(_dto.getSaddword()!=null){
							filebuf.append(splitSign+_dto.getSaddword());//原因
						}else{
							filebuf.append(splitSign+"");//原因
						}
						
						filebuf.append("\r\n");
//						List<TvPayreckBankBackListDto> sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
//						for (TvPayreckBankBackListDto sub : sublist) {
//							filebuf.append(sub.getSfuncbdgsbtcode());//原凭证编号
//							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//原凭证日期
//							filebuf.append(splitSign+sub.getSbdgorgcode());//预算单位代码
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
	 * @param finddto
	 * @param selectedtable
	 * @return String
	 * @throws ITFEBizException	 
	 */
	@SuppressWarnings("unchecked")
	public String exportFile(IDto finddto, String selectedtable)
			throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		List<TvPayreckBankBackDto> list=new ArrayList<TvPayreckBankBackDto>();
		String filename="";
		try {
			if(selectedtable.equals("0")){
				TvPayreckBankBackDto mdto=(TvPayreckBankBackDto)finddto;
				mdto.setSbookorgcode(orgcode);
				String where = null;
				if(mdto.getSaddword()!=null)
				{
					where = " and "+mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,where);
				if("0".equals(mdto.getSpaymode()))//0直接1授权
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+".csv";
				else
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+".csv";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep +new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())	+ filename;
			String splitSign = ",";// 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					for (TvPayreckBankBackDto _dto :(List<TvPayreckBankBackDto>) list) {
						filebuf.append(_dto.getStrecode());//收款国库代码
						filebuf.append(splitSign+_dto.getSpayername());//代理行名称-付款人名称
						filebuf.append(splitSign+_dto.getSpayeracct());//代理行收款账户-付款人账号
						filebuf.append(splitSign+_dto.getSagentbnkcode());//代理银行代码
						filebuf.append(splitSign+"");//代理银行开户行行号-付款人开户行代码
						filebuf.append(splitSign+_dto.getSpayeename());//国库收款人名称-收款人名称
						filebuf.append(splitSign+_dto.getSpayeeacct());//国库收款人账户-收款人账号
						filebuf.append(splitSign+"");//国库所在银行代码-收款人开户行代码
						filebuf.append(splitSign+"");//国库所在银行行号-收款人开户行行号
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类代码
						filebuf.append(splitSign+_dto.getFamt());//零余额发生额
						filebuf.append(splitSign+"0.00");//小额现金发生额
						filebuf.append(splitSign);//摘要代码
						filebuf.append(splitSign+_dto.getSvouno());//凭证编号
						filebuf.append(splitSign+_dto.getDvoudate().toString().replaceAll("-", ""));//凭证日期
						filebuf.append(splitSign+_dto.getSorivouno());//原凭证编号
						filebuf.append(splitSign+_dto.getDorivoudate().toString().replaceAll("-", ""));//原凭证日期
						filebuf.append("\r\n");
						TvPayreckBankBackListDto subPara =  new TvPayreckBankBackListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
						List<TvPayreckBankBackListDto> sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
						for (TvPayreckBankBackListDto sub : sublist) {
							filebuf.append(sub.getSorivouno());//原凭证编号
							filebuf.append(splitSign+(sub.getDorivoudate()==null?"":sub.getDorivoudate().toString().replaceAll("-", "")));//原凭证日期
							filebuf.append(splitSign+sub.getSbdgorgcode());//预算单位代码
							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//功能类科目代码
							filebuf.append(splitSign+((sub.getSecnomicsubjectcode()==null)?"":sub.getSecnomicsubjectcode()));//经济类科目代码
							filebuf.append(splitSign+sub.getSacctprop());//账户性质
							filebuf.append(splitSign+sub.getFamt());//支付金额
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