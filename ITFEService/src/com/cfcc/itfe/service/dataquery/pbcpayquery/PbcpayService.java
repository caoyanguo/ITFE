package com.cfcc.itfe.service.dataquery.pbcpayquery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.HtvPbcpaySubDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author think
 * @time   12-06-26 15:25:34
 * codecomment: 
 */

public class PbcpayService extends AbstractPbcpayService {
	private static Log log = LogFactory.getLog(PbcpayService.class);	



	public String dataExport(IDto finddto, String selectedtable)
			throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		String filename="";
		
		try {
			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			if(selectedtable.equals("0")){
				TvPbcpayMainDto mdto=(TvPbcpayMainDto)finddto;
				mdto.setSorgcode(orgcode);
				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				list=CommonFacade.getODB().findRsByDto(mdto);
				filename=TimeFacade.getCurrentStringTime()+"_PbcPayout.txt";
			}else if(selectedtable.equals("1")){
				HtvPbcpayMainDto mdto=(HtvPbcpayMainDto)finddto;
				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				mdto.setSorgcode(orgcode);
				list=CommonFacade.getODB().findRsByDto(mdto);
				filename=TimeFacade.getCurrentStringTime()+"_PbcPayout.txt";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = ",";//"\t"; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				int index=1;
				if(selectedtable.equals("0")){
					for (TvPbcpayMainDto _dto :(List<TvPbcpayMainDto>) list) {
//						TvPbcpaySubDto subPara = new TvPbcpaySubDto();
//						subPara.setSbizno(_dto.getSbizno());
//						List l = CommonFacade.getODB().findRsByDtoWithUR(subPara);
//						TvPayoutmsgsubDto sub = (TvPayoutmsgsubDto)l.get(0);						
						filebuf.append(_dto.getStrecode()); //国库代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getSentrustdate());//委托日期
						filebuf.append(splitSign);
//						filebuf.append(index++);//支付交易序号
//						filebuf.append(splitSign);
						filebuf.append(_dto.getSvouno());//凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getFamt());//金额
						filebuf.append(splitSign);
						String bankno = _dto.getSrcvbnkno();//接收行号
						String bankname = bankMap.get(bankno).getSbankname();//接收行名
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacct());//收款人账号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeename());//收款人名称
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//付款人账号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//付款人名称
//						filebuf.append(splitSign);
//						filebuf.append(sub.getSfunsubjectcode()); //功能科目代码
//						filebuf.append(splitSign);
//						filebuf.append(_dto.getSaddword());//附言
//						filebuf.append(splitSign);
//						filebuf.append(_dto.getSstatus()); //状态
						filebuf.append("\r\n");
					}
				}else if(selectedtable.equals("1")){
					for (HtvPbcpayMainDto _dto :(List<HtvPbcpayMainDto>) list) {
//						HtvPayoutmsgsubDto subPara = new HtvPayoutmsgsubDto();
//						subPara.setSbizno(_dto.getSbizno());
//						List l = CommonFacade.getODB().findRsByDtoWithUR(subPara);
//						TvPayoutmsgsubDto sub = (TvPayoutmsgsubDto)l.get(0);
						filebuf.append(_dto.getStrecode()); //国库代码
						filebuf.append(splitSign);
						filebuf.append(_dto.getSentrustdate());//委托日期
						filebuf.append(splitSign);
//						filebuf.append(index++);//支付交易序号
//						filebuf.append(splitSign);
						filebuf.append(_dto.getSvouno());//凭证编号
						filebuf.append(splitSign);
						filebuf.append(_dto.getFamt());//金额
						filebuf.append(splitSign);
						String bankno = _dto.getSrcvbnkno();//接收行号
						String bankname = bankMap.get(bankno).getSbankname();//接收行名
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacct());//收款人账号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeename());//收款人名称
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//付款人账号
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//付款人名称
//						filebuf.append(splitSign);
//						filebuf.append(sub.getSfunsubjectcode()); //功能科目代码
//						filebuf.append(splitSign);
//						filebuf.append(_dto.getSaddword());//附言
//						filebuf.append(splitSign);
//						filebuf.append(_dto.getSstatus()); //状态
						filebuf.append("\r\n");
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

	public PageResponse findMainByPage(TvPbcpayMainDto mainDto,
			PageRequest pageRequest) throws ITFEBizException {
		
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, null, " S_ORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询人行办理授权支付主体信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付主体信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询人行办理授权支付主体信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付主体信息时错误!", e);
		}
		
		
	}

	public PageResponse findMainByPageForHis(HtvPbcpayMainDto mainDto,
			PageRequest pageRequest) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, null, " S_ORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询人行办理授权支付主体信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付历史主体信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询人行办理授权支付主体信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付历史主体信息时错误!", e);
		}
		
	}

	public PageResponse findSubByPage(TvPbcpayMainDto mainDto,
			PageRequest pageRequest) throws ITFEBizException {
		
		if (null == mainDto || null == mainDto.getSorgcode() || null == mainDto.getIvousrlno()) {
			return null;
		}
		TvPbcpaySubDto subdto = new TvPbcpaySubDto();
		subdto.setIvousrlno(mainDto.getIvousrlno());
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " I_VOUSRLNO");
		} catch (JAFDatabaseException e) {
			log.error("分页查询人行办理授权支付明细信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付明细信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询人行办理授权支付明细信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付明细信息时错误!", e);
		}
	}

	public PageResponse findSubByPageForHis(HtvPbcpayMainDto mainDto,
			PageRequest pageRequest) throws ITFEBizException {
		if (null == mainDto || null == mainDto.getSorgcode() || null == mainDto.getIvousrlno()) {
			return null;
		}
		HtvPbcpaySubDto subdto = new HtvPbcpaySubDto();
		subdto.setIvousrlno(mainDto.getIvousrlno());
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " I_VOUSRLNO");
		} catch (JAFDatabaseException e) {
			log.error("分页查询人行办理授权支付历史明细信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付历史明细信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询人行办理授权支付历史明细信息时错误!", e);
			throw new ITFEBizException("分页查询人行办理授权支付历史明细信息时错误!", e);
		}
	}
	
	
	
	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			return DatabaseFacade.getDb().createWithResult(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
    }

	public void addSubInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
	}

	public void updateInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
	}
    
   

    
    
    
    
    

}