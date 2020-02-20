package com.cfcc.itfe.service.dataquery.payoutfinancequery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.dataquery.payoutfinancequery.AbstractPayOutFinanceQueryService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceSubDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayOutFinanceReportDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceSubDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto;

/**
 * @author t60
 * @time   12-02-23 15:31:47
 * codecomment: 
 */

public class PayOutFinanceQueryService extends AbstractPayOutFinanceQueryService {
	private static Log log = LogFactory.getLog(PayOutFinanceQueryService.class);	


	/**
	 * 分页查询批量拨付主信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findMainByPage(TvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException {
    	try {
    		// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSbookorgcode(orgcode);
			// 取得票据编号 - 支持模糊查询
			String wherestr = PublicSearchFacade.changeFileName(null, mainDto.getSfilename());
			mainDto.setSfilename(null);

			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, null, " S_TRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, wherestr, "S_TRECODE ");
			}
		} catch (JAFDatabaseException e) {
			log.error("分页查询批量拨付主信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付主信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询批量拨付主信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付主信息时错误!", e);
		}
    }

	/**
	 * 分页查询批量拨付子信息	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findSubByPage(TvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException {

    	TvPayoutfinanceSubDto subdto = new TvPayoutfinanceSubDto();
    	subdto.setIvousrlno(mainDto.getIvousrlno());
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " S_SEQNO");
		} catch (JAFDatabaseException e) {
			log.error("分页查询批量拨付子信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付子信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询批量拨付子信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付子信息时错误!", e);
		}
    }

	/**
	 * 分页查询批量拨付主信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findMainByPageForHis(HtvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException {
    	try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSbookorgcode(orgcode);
			
			// 取得票据编号 - 支持模糊查询
			String wherestr = PublicSearchFacade.changeFileName(null, mainDto.getSfilename());
			mainDto.setSfilename(null);

			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, null, " S_BOOKORGCODE,S_TRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, wherestr, " S_BOOKORGCODE,S_TRECODE ");
			}
		} catch (JAFDatabaseException e) {
			log.error("分页查询批量拨付历史主信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付历史主信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询批量拨付历史主信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付历史主信息时错误!", e);
		}
    }

	/**
	 * 分页查询批量拨付子信息(历史表)	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findSubByPageForHis(HtvPayoutfinanceMainDto mainDto, PageRequest pageRequest) throws ITFEBizException {
    	if (null == mainDto || null == mainDto.getSbookorgcode() || null == mainDto.getIvousrlno()) {
			return null;
		}

    	HtvPayoutfinanceSubDto subdto = new HtvPayoutfinanceSubDto();
		subdto.setIvousrlno(mainDto.getIvousrlno());

		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " S_SEQNO");
		} catch (JAFDatabaseException e) {
			log.error("分页查询批量拨付历史子信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付历史子信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询批量拨付历史子信息时错误!", e);
			throw new ITFEBizException("分页查询批量拨付历史子信息时错误!", e);
		}
    }

	public List searchForReport(String selectTable, IDto dtoInfo)
			throws ITFEBizException {
		SQLExecutor sqlExecutor=null;
		SQLResults rs = null;
		List list = new ArrayList();
		try{
			TvPayoutfinanceMainDto dto=(TvPayoutfinanceMainDto)dtoInfo;
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT S_FILENAME,N_AMT,S_PAYERACCT,S_PAYERNAME,I_VOUSRLNO ";
			sql+=",tvmain.S_TRECODE,treasury.S_TRENAME,organ.S_ORGNAME,S_BDGORGCODE,S_SUBJECTNAME,  ";
			if(selectTable.equals("0")){
				sql+="tvmain.S_BOOKORGCODE,S_VALUECMT AS S_RESULT FROM TV_PAYOUTFINANCE_MAIN tvmain ";
				sql+=" LEFT JOIN TD_ENUMVALUE tdenum ON tdenum.S_TYPECODE='0412' AND tdenum.S_VALUE=tvmain.S_STATUS ";
				sql+=" LEFT JOIN TS_ORGAN organ ON organ.S_ORGCODE=tvmain.S_BOOKORGCODE ";
			}else if(selectTable.equals("1")){
				sql+="tvmain.S_ORGCODE FROM HTV_PAYOUTFINANCE_MAIN tvmain ";
				sql+=" LEFT JOIN TS_ORGAN organ ON organ.S_ORGCODE=tvmain.S_ORGCODE ";
			}
			sql+=" LEFT JOIN TS_TREASURY treasury ON treasury.S_TRECODE=tvmain.S_TRECODE AND treasury.S_ORGCODE='"+this.getLoginInfo().getSorgcode()+"' ";
			
			sql+=" LEFT JOIN TS_BUDGETSUBJECT bject ON bject.S_SUBJECTCODE=tvmain.S_FUNCSBTCODE AND bject.S_ORGCODE='"+this.getLoginInfo().getSorgcode()+"' ";
			sql+=" WHERE 1=1 ";
			
			if(dto.getStrecode()!=null && !dto.getStrecode().equals("")){
				sql+=" AND tvmain.S_TRECODE='"+dto.getStrecode()+"'";
			}
			if(dto.getIvousrlno()!=null && !dto.getIvousrlno().equals("")){
				sql+=" AND tvmain.I_VOUSRLNO="+dto.getIvousrlno()+" ";
			}
			if(dto.getSbillorg()!=null && !dto.getSbillorg().equals("")){
				sql+=" AND tvmain.S_BILLORG='"+dto.getSbillorg()+"'";
			}
			if(dto.getSpayeebankno()!=null && !dto.getSpayeebankno().equals("")){
				sql+=" AND tvmain.S_PAYEEBANKNO='"+dto.getSpayeebankno()+"'";
			}
			if(dto.getSpayeracct()!=null && !dto.getSpayeracct().equals("")){
				sql+=" AND tvmain.S_PAYERACCT='"+dto.getSpayeracct()+"'";
			}
			if(dto.getSpayername()!=null && !dto.getSpayername().equals("")){
				sql+=" AND tvmain.S_PAYERNAME='"+dto.getSpayername()+"'";
			}
			if(dto.getSentrustdate()!=null && !dto.getSentrustdate().equals("")){
				sql+=" AND tvmain.S_ENTRUSTDATE='"+dto.getSentrustdate()+"'";
			}
			if(dto.getSpackageno()!=null && !dto.getSpackageno().equals("")){
				sql+=" AND tvmain.S_PACKAGENO='"+dto.getSpackageno()+"'";
			}
			if(dto.getSstatus()!=null && !dto.getSstatus().equals("")&&selectTable.equals("0")){
				sql+=" AND tvmain.S_STATUS='"+dto.getSstatus()+"'";
			}
			if(dto.getSvouno()!=null && !dto.getSvouno().equals("")){
				sql+=" AND tvmain.S_VOUNO='"+dto.getSvouno()+"'";
			}
			if(dto.getSvoudate()!=null && !dto.getSvoudate().equals("")){
				sql+=" AND tvmain.S_VOUDATE='"+dto.getSvoudate()+"'";
			}
			if(dto.getSpayoutvoutype()!=null && !dto.getSpayoutvoutype().equals("")){
				sql+=" AND tvmain.S_PAYOUTVOUTYPE='"+dto.getSpayoutvoutype()+"'";
			}
			if(dto.getSfilename()!=null && !dto.getSfilename().equals("")){
				sql+=" AND tvmain.S_FILENAME='"+dto.getSfilename()+"'";
			}
			if(dto.getSbookorgcode()!=null && !dto.getSbookorgcode().equals("")&&selectTable.equals("0")){
				sql+=" AND tvmain.S_BOOKORGCODE='"+dto.getSbookorgcode()+"'";
			}
			if(dto.getSbookorgcode()!=null && !dto.getSbookorgcode().equals("")&&selectTable.equals("1")){
				sql+=" AND tvmain.S_ORGCODE='"+dto.getSbookorgcode()+"'";
			}
			sql+=" ORDER BY S_FILENAME ";
			rs = sqlExecutor.runQueryCloseCon(sql,TvPayOutFinanceReportDto.class);
		}catch(JAFDatabaseException ex){
			log.error("查询批量拨付业务数据时出现异常!",ex);
			throw new ITFEBizException("查询批量拨付业务数据时出现异常!",ex);
		}
		list.addAll(rs.getDtoCollection());
		return list;
	}

	public String dataexport(IDto finddto, String selectedtable)
			throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		String filename="";
		
		try {
			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			if(selectedtable.equals("0")){
				TvPayoutfinanceMainDto mdto=(TvPayoutfinanceMainDto)finddto;
				mdto.setSbookorgcode(orgcode);
				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				list=CommonFacade.getODB().findRsByDto(mdto);
				filename=TimeFacade.getCurrentStringTime()+"_BathPayout.txt";
			}else if(selectedtable.equals("1")){
				HtvPayoutfinanceMainDto mdto=(HtvPayoutfinanceMainDto)finddto;
				mdto.setSbookorgcode(orgcode);
				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				list=CommonFacade.getODB().findRsByDto(mdto);
				filename=TimeFacade.getCurrentStringTime()+"_BathPayout.txt";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new java.util.Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = "\t"; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				int index=1;
				if(selectedtable.equals("0")){
					for (TvPayoutfinanceMainDto _dto :(List<TvPayoutfinanceMainDto>) list) {
						TvPayoutfinanceSubDto subdto = new TvPayoutfinanceSubDto();
				    	subdto.setIvousrlno(_dto.getIvousrlno());
				    	List<TvPayoutfinanceSubDto> slist=CommonFacade.getODB().findRsByDto(subdto);
				    	for(TvPayoutfinanceSubDto sdto:slist){
				    		filebuf.append(_dto.getSentrustdate());
							filebuf.append(splitSign);
							filebuf.append(index++);
							filebuf.append(splitSign);
							filebuf.append(_dto.getSvouno());
							filebuf.append(splitSign);
							filebuf.append(sdto.getNsubamt());
							filebuf.append(splitSign);
							String bankno = sdto.getSpayeeopnbnkno();//接收行号
							String bankname = bankMap.get(bankno)==null?"":bankMap.get(bankno).getSbankname();//接收行名
							filebuf.append(bankno);
							filebuf.append(splitSign);
							filebuf.append(bankname);
							filebuf.append(splitSign);
							filebuf.append(sdto.getSpayeeacct());
							filebuf.append(splitSign);
							filebuf.append(sdto.getSpayeename());
							filebuf.append(splitSign);
							filebuf.append(_dto.getSpayeracct());
							filebuf.append(splitSign);
							filebuf.append(_dto.getSpayername());
							filebuf.append("\r\n");
				    	}
						
					}
				}else if(selectedtable.equals("1")){
					for (HtvPayoutfinanceMainDto _dto :(List<HtvPayoutfinanceMainDto>) list) {
						HtvPayoutfinanceSubDto subdto = new HtvPayoutfinanceSubDto();
				    	subdto.setIvousrlno(_dto.getIvousrlno());
				    	List<HtvPayoutfinanceSubDto> slist=CommonFacade.getODB().findRsByDto(subdto);
				    	for(HtvPayoutfinanceSubDto sdto:slist){
				    		filebuf.append(_dto.getSentrustdate());
							filebuf.append(splitSign);
							filebuf.append(index++);
							filebuf.append(splitSign);
							filebuf.append(_dto.getSvouno());
							filebuf.append(splitSign);
							filebuf.append(sdto.getNsubamt());
							filebuf.append(splitSign);
							String bankno = sdto.getSpayeeopnbnkno();//接收行号
							String bankname = bankMap.get(bankno)==null?"":bankMap.get(bankno).getSbankname();//接收行名
							filebuf.append(bankno);
							filebuf.append(splitSign);
							filebuf.append(bankname);
							filebuf.append(splitSign);
							filebuf.append(sdto.getSpayeeacct());
							filebuf.append(splitSign);
							filebuf.append(sdto.getSpayeename());
							filebuf.append(splitSign);
							filebuf.append(_dto.getSpayeracct());
							filebuf.append(splitSign);
							filebuf.append(_dto.getSpayername());
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