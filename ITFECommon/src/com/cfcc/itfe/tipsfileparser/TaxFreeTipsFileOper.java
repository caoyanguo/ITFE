package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpaySubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
/**
 * author leilianjie  免抵调
 */
public class TaxFreeTipsFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		int recordNum = 0; // 记录记录器
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// 业务类型
		mulitDto.setSbookorgcode(sbookorgcode);// 核算主体代码
		BigDecimal famt = new BigDecimal("0.00");
		List<IDto> freedtos = new ArrayList<IDto>();
		Map<String, List<TbsTvFreeDto>> pkgs = new HashMap<String, List<TbsTvFreeDto>>();
		Set<String> taxcodes = new HashSet<String>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<String> voulist = new ArrayList<String>(); //凭证编号集
		
		try {
			HashMap<String, TsTreasuryDto> tremap =SrvCacheFacade.cacheTreasuryInfo(sbookorgcode);
			// 取得辅助标志对照表
			DwbkTipsFileOperForSD sd = new DwbkTipsFileOperForSD();
			HashMap viceMap = sd.getViceSignMap(sbookorgcode);
			File f = new File(file);
			List<String[]> fileContent = super.readFile(file, ",");
			for (int i = 0; i < fileContent.size(); i++) {
				String[] cols = fileContent.get(i);
				if(i == 0) {
					//校验文件重复,国库代码+文件名
					String errorInfo = this.checkFileExsit(sbookorgcode, cols[8], f.getName(), MsgConstant.MSG_NO_1106);
					if(null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
						return mulitDto;
					}
				}
				
				recordNum++;
				TbsTvFreeDto freedto = new TbsTvFreeDto();
				
				//freedto.setIvousrlno(Long.parseLong(SequenceGenerator.getNextByDb2(SequenceName.TAXFREE_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH)));
				
				String taxorgcode = cols[0];//征收机关代码
				TsConverttaxorgDto convertTaxOrgDto = new TsConverttaxorgDto();
				// 核算主体代码
				convertTaxOrgDto.setSorgcode(sbookorgcode);
				// 国库主体代码
				convertTaxOrgDto.setStrecode(cols[8]);
				// TBS征收机关代码
				convertTaxOrgDto.setStbstaxorgcode(cols[0]);
				List dtoL = CommonFacade.getODB().findRsByDtoWithUR(
						convertTaxOrgDto);
				String strTipsTaxOrg = "";
				if (dtoL == null || dtoL.size() == 0) {
					mulitDto.getErrorList().add(
							"免抵调文件[" + f.getName() + "]中国库主体代码：" + cols[8]
									+ ",征收机关代码：" + cols[0]
									+ " 没有维护'征收机关对照'!");
				} else {
					TsConverttaxorgDto taxogdto = (TsConverttaxorgDto) dtoL
							.get(0);
					// Tips征收机关代码
					strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
					// 征收机关代码
					freedto.setStaxorgcode(strTipsTaxOrg);
				}
				
				taxcodes.add(cols[0].trim());
				
				//免抵调电子凭证号需要进行截取后8位
				if(null != cols[3] && !"".equals(cols[3]) && cols[3].length()>8) {
					freedto.setStrano(cols[3].substring(cols[3].length()-8));
				}else {
					freedto.setStrano(cols[3]);
				}
				
				try{
					freedto.setDbilldate(new java.sql.Date(TimeFacade.parseDate(cols[1], "yyyyMMdd").getTime()));
				}catch(Exception e){
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"] 凭证编号为["+cols[2].trim()+"]的记录中开票日期格式非法");
				}
				freedto.setSfreevouno(cols[2]);
				voulist.add(cols[2]);
				
				freedto.setCfreeplutype(cols[4]);//免抵调增预算种类
				String psubject = cols[5];
				if(!bmap.containsKey(psubject)){
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"] 凭证编号为["+cols[2].trim()+"]的记录中免抵调增预算科目代码"+psubject+"未维护");;
				}
				freedto.setSfreeplusubjectcode(psubject);
				freedto.setCfreeplulevel(cols[6]);
				
				if (null != cols[7] && !"".equals(cols[7])) { 
					if (!viceMap.containsKey(cols[8] + cols[5] + cols[7])) {
						if (!viceMap.containsKey(cols[5] + cols[7])) {
							if (!viceMap.containsKey(cols[8] + cols[7])) {
								if (!viceMap.containsKey(cols[7])) {
									mulitDto.getErrorList().add(
											"免抵调文件[" + f.getName() + "] 凭证编号为["+cols[2].trim()+"]中免抵调增辅助标志： "+ cols[7]+ " 没有在'辅助标志对照维护'参数中维护!");
								}
							}
						}
					}
				}
				freedto.setSfreeplusign(cols[7]); // 免抵调增辅助标志
				String trecode = cols[8];
				if(!this.checkTreasury(trecode, sbookorgcode)) {
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"] 凭证编号为["+cols[2].trim()+"]的记录中免抵调增收款国库代码："+trecode+" 没有在'国库主体信息参数'中查找到!");
				}
				freedto.setSfreepluptrecode(trecode);
				try{
					BigDecimal pamt = new BigDecimal(cols[9]);
					freedto.setFfreepluamt(pamt);
					famt.add(pamt);
				}catch(Exception e){
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"] 凭证编号为["+cols[2].trim()+"]的记录中免抵调增交易金额非法");
				}
				freedto.setCfreemikind(cols[10]);
				String msubject = cols[11];
				if(!bmap.containsKey(msubject)){
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"] 凭证编号为["+cols[2].trim()+"]的记录中免抵调减预算科目代码"+msubject+"未维护");;
				}
				freedto.setSfreemisubject(msubject);
				freedto.setCfreemilevel(cols[12]);
				
				if (null != cols[13] && !"".equals(cols[13])) { 
					if (!viceMap.containsKey(cols[14] + cols[11] + cols[13])) {
						if (!viceMap.containsKey(cols[11] + cols[13])) {
							if (!viceMap.containsKey(cols[14] + cols[13])) {
								if (!viceMap.containsKey(cols[13])) {
									mulitDto.getErrorList().add(
											"免抵调文件[" + f.getName() + "] 凭证编号为["+cols[2].trim()+"]中免抵调减辅助标志： "+ cols[7]+ " 没有在'辅助标志对照维护'参数中维护!");
								}
							}
						}
					}
				}
				freedto.setSfreemisign(cols[13]); // 免抵调减辅助标志
				String mtrecode = cols[14];
				if(!this.checkTreasury(mtrecode, sbookorgcode)) {
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"]凭证编号为["+cols[2].trim()+"]的记录中免抵调减收款国库代码："+mtrecode+" 没有在'国库主体信息参数'中查找到!");
				}
				freedto.setSfreemiptre(mtrecode);
				try{
					BigDecimal mamt = new BigDecimal(cols[15]);
					freedto.setFfreemiamt(mamt);
					famt.add(mamt);
				}catch(Exception e){
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"] 凭证编号为["+cols[2].trim()+"]的记录中免抵调减交易金额非法");
				}
				freedto.setScorpcode(cols[17]);
				freedto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				freedto.setCtrimflag(cols[16]);
				freedto.setSbiztype(biztype);
				freedto.setSfilename(f.getName());
				freedto.setSbookorgcode(sbookorgcode);
				freedto.setDacceptdate(TimeFacade.getCurrentDateTime());
				freedto.setTssysupdate(TSystemFacade.getDBSystemTime());
				List<TbsTvFreeDto> pkg = pkgs.get(taxorgcode);
				if(null == pkg || pkg.size() == 0){
					pkg = new ArrayList<TbsTvFreeDto>();
					pkg.add(freedto);
					pkgs.put(taxorgcode, pkg);
				}else {
					pkg.add(freedto);
				}
				
			}
			
			for(String taxcode : taxcodes){
				List<TbsTvFreeDto> pkg = pkgs.get(taxcode);
				int li_Detail = (pkg.size()-1) / 1000;
				for (int k = 0; k <= li_Detail; k++) {
					int li_TempCount = 0;
					if (li_Detail == k) {
						li_TempCount = pkg.size();
					} else {
						li_TempCount = (k + 1) * 1000;
					}
					String pkgno = SequenceGenerator.changePackNoForLocal(SequenceGenerator
							.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
									SequenceName.TRAID_SEQ_CACHE,
									SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					BigDecimal pkgFamt = new BigDecimal("0.00");
					String trecode =  pkg.get(0).getSfreepluptrecode();
					
					int count = 0;
					for (int j = k * 1000; j < li_TempCount; j++) {
						TbsTvFreeDto bkdto = (TbsTvFreeDto) pkg.get(j);
						//设置包流水号
						bkdto.setSpackno(pkgno);
						//进行数量的统计
						count ++;
						//统计金额
						pkgFamt = pkgFamt.add(bkdto.getFfreemiamt()).add(bkdto.getFfreepluamt());
						//将所有填充数据完成的DTO保存
						freedtos.add(bkdto);
					}
					
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(sbookorgcode);
					packdto.setStrecode(trecode);
					packdto.setSfilename(f.getName());
					packdto.setStaxorgcode(taxcode);
					packdto.setScommitdate(TimeFacade.getCurrentStringTime());
					packdto.setSaccdate(TimeFacade.getCurrentStringTime());
					packdto.setSpackageno(pkgno);
					packdto.setSoperationtypecode(biztype);
					packdto.setIcount(count);
					packdto.setNmoney(pkgFamt);
					packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdto.setSusercode(userid);
					packdto.setImodicount(0);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdtos.add(packdto);
				}
			}
			
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					mulitDto.getErrorList().add("免抵调文件["+f.getName()+"] 中存在凭证编号 ["+item.trim()+"] 重复");
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("免抵调文件解析出错：\n"+e.getMessage(), e);
		}

		
		mulitDto.setFatherDtos(freedtos);
		mulitDto.setPackDtos(packdtos);
		mulitDto.setVoulist(voulist);
		mulitDto.setFamt(famt);
		mulitDto.setTotalCount(recordNum); 
		
		return mulitDto;
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}
