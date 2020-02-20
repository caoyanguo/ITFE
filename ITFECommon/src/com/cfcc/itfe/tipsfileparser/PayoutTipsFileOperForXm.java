package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua 实拨资金
 */
public class PayoutTipsFileOperForXm extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		try {
			int fbcount = 0; // 分包记数器
			int recordNum = 1; // 记录记录器
			
			String packno = ""; 
			String tmpPackNo = "";
			String trecode = "";
			
			String fname = new File(file).getName();
			String ctrimflag = fname.substring(fname.length()-5,fname.length()-4);  //调整期标志
			
			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);//业务类型
			multi.setSbookorgcode(bookorgCode);//核算主体代码
			BigDecimal famt = new BigDecimal("0.00");
			BigDecimal famtPack = new BigDecimal("0.00");
			
			List<IDto> listdto = new ArrayList<IDto>();
			List<IDto> packdtos = new ArrayList<IDto>();
			List<IDto> famtdtos = new ArrayList<IDto>();
			
			HashMap<String, String> map = new HashMap<String, String>();
			File fi = new File(file);
			
			List<String> voulist = new ArrayList<String>();
			Set<String> bnkset = new HashSet<String>();
			
			List<String[]> fileContent = super.readFile(file, ",");
			//文件解析
			if(file.endsWith("txt")) {  //明细岗接口
				Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //法人代码缓存
				for (String[] singDto : fileContent) {
					TbsTvPayoutDto payoutDto = new TbsTvPayoutDto();
					payoutDto.setStrecode(singDto[0]); // 国库代码
					trecode = singDto[0];
					if(!this.checkTreasury(trecode, bookorgCode)) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[11].trim()+"]的记录中国库主体代码："+trecode+" 没有在'国库主体信息参数'中查找到!");
					}
					payoutDto.setSpayeracct(singDto[1]); // 付款人账号
					payoutDto.setSmovefundreason(singDto[2]); // 支出原因代码或者调拨原因代码 (可能为空)
					//预算单位代码
					if(singDto[3]!=null && singDto[3].length() > 15) {
						payoutDto.setSbdgorgcode(singDto[3].substring(0, 15));//收款单位代码
					}else {
						payoutDto.setSbdgorgcode(singDto[3]);//收款单位代码
					}
					//预算单位代码校验
					if(!rpmap.containsKey(singDto[0]+payoutDto.getSbdgorgcode().trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[11].trim()+"]的记录中预算单位代码 '"+payoutDto.getSbdgorgcode()+"' 没有在法人代码表中找到!");
					}else {
						if(!"1".equals(rpmap.get(singDto[0]+payoutDto.getSbdgorgcode()).getCmayaprtfund().trim())) {
							multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[11].trim()+"]的记录中预算单位代码 '"+payoutDto.getSbdgorgcode()+"' 对应预算单位不能进行实拨资金!");
						}
					}
//					payoutDto.setSbdgorgcode(singDto[3]); // 预算单位代码
					payoutDto.setSpayeeacct(singDto[4]); // 收款单位账号(可能为空)
					payoutDto.setCbdgkind(singDto[5]); // 预算种类
					payoutDto.setSfuncsbtcode(singDto[6]); // 功能科目代码
					payoutDto.setSecosbtcode(singDto[7]); // 经济科目代码
					payoutDto.setSbooksbtcode(singDto[8]); // 会计科目代码 (可为空)
					payoutDto.setSpaybizkind(singDto[9]); // 支出种类
					payoutDto.setFamt(new BigDecimal(singDto[10])); // 发生额 数值转换方法
					famt = famt.add(new BigDecimal(singDto[10]));	
					payoutDto.setSvouno(singDto[11]); // 凭证编号
					if(VerifyParamTrans.isNull(singDto[11])){//凭证编号不能为空
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号不能为空!");
					}
					voulist.add(trecode.trim()+","+singDto[11].trim());
					
					//通过国库代码和付款人账号 找到对应付款人名称
					TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
					accdto.setSorgcode(bookorgCode);
					accdto.setStrecode(trecode);
					accdto.setSpayeraccount(singDto[1].trim());
					IDto ito = DatabaseFacade.getODB().find(accdto);
					if(ito == null) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[11].trim()+"]的记录根据国库代码："+trecode+" , 付款人账号："+singDto[1].trim()+" 没有在'付款人账户信息维护参数'中找到对应付款人名称，请先维护");
					}else {
						TsInfoconnorgaccDto tndto = (TsInfoconnorgaccDto)ito;
						payoutDto.setSpayername(tndto.getSpayername());
					}
					payoutDto.setSbookorgcode(bookorgCode);// 核算主体代码
					payoutDto.setDaccept(TimeFacade.getCurrentDateTime());//受理日期
					payoutDto.setIofyear(Integer.parseInt(fi.getName().substring(0, 4))); //所属年度
					payoutDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0, 8))); //凭证日期
					
					if(biztype != null && !"".equals(biztype)) {
						payoutDto.setSbiztype(biztype);// 业务类型 不能为空
					}else {
						payoutDto.setSbiztype("");// 业务类型
					}
					payoutDto.setCtrimflag(ctrimflag); //调整期标志不能为空
					
					payoutDto.setSpackageno(tmpPackNo);  //包流水号
					payoutDto.setSfilename(fi.getName());
					payoutDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					fbcount++;
					recordNum++;
					listdto.add(payoutDto);
				}
			}else if(file.endsWith("tmp")){ //资金岗接口
				String srcvreno = fi.getName().substring(0, 12);
				for (int i = 0; i < fileContent.size(); i++) {
					TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
					dto.setSrcvreckbnkno(srcvreno);
					String[] strs = fileContent.get(i);
					if("<CA>".equals(strs[0].toUpperCase())) {
						break ;
					}
					dto.setSsndbnkno(strs[0]);   //发起行行号
					dto.setSrcvbnkno(strs[1]);   //接受行行号
					dto.setFamt(new BigDecimal(strs[2]));  //金额
					famt = famt.add(new BigDecimal(strs[2]));
					famtPack = famtPack.add(new BigDecimal(strs[2]));
					dto.setSrcvreckbnkno(srcvreno); //清算行
					dto.setSpayeropnbnkno(strs[3]);    //付款人开户行行号
					dto.setSpayeracct(strs[4]);   //付款人账号
					dto.setSpayername(strs[5]);   //付款人名称
					dto.setSpayeraddr(strs[6]);   //付款人地址
					dto.setSpayeeopnbnkno(strs[7]);   //收款人开户行行号
					dto.setSpayeeacct(strs[8]);     //收款人账号	
					dto.setSpayeename(strs[9]);		//收款人名称
					dto.setSpayeeaddr(strs[10]);	//收款人地址
					dto.setSpaybizkind(strs[11]);   //支付业务种类
					dto.setSbiztype(strs[12]);   //业务类型号
					dto.setSaddword(strs[13]);      //附言
					
					famtdtos.add(dto);
				}
			}
			
			//校验文件中凭证编号是否重复
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"]中存在凭证编号重复");
				}
			}
			
			multi.setFatherDtos(listdto);
			multi.setSonDtos(famtdtos);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt);//总金额
			multi.setTotalCount(recordNum); //总笔数
			return multi;
			
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("实拨文件解析出错 \n"+e.getMessage(), e);
		}
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}
