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
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua 实拨资金
 */
public class PayoutTipsFileOperForFj extends AbstractTipsFileOper {

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
			ShiboDto sbdto = (ShiboDto)paramdto;
			int fbcount = 0; // 分包记数器
			int recordNum = 1; // 记录记录器
			
			String tmpPackNo = "";			
			String trecode = "";
			
			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);//业务类型
			multi.setSbookorgcode(bookorgCode);//核算主体代码
			
			BigDecimal famt = new BigDecimal("0.00");
			BigDecimal famtPack = new BigDecimal("0.00");
			BigDecimal famthead = new BigDecimal("0.00");
			int recordhead = 0; 

			List<String[]> fileContent = super.readFile(file, ",");
			List<IDto> listdto = new ArrayList<IDto>();
			List<IDto> packdtos = new ArrayList<IDto>();
			
			List<IDto> fadtos = new ArrayList<IDto>();
			
			File fi = new File(file);
			List<String> voulist = new ArrayList<String>();
			Set<String> bnkset = new HashSet<String>();
			Map<String, TsFinmovepaysubDto> movesubs = this.getMovepaysub(bookorgCode);	//财政调拨支出预算科目表信息
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //法人代码缓存
			Map<String,TsPaybankDto> paymap = this.makeBankMap(); //支付行号缓存
			for (int i = 0; i < fileContent.size(); i++) {
				String[] singDto = fileContent.get(i);
				if(i == 0) {
					//属于文件头
					recordhead = Integer.parseInt(singDto[2]);
					famthead = new BigDecimal(singDto[3]);
				}else if( i > 0 && i<fileContent.size() -1){
					TbsTvPayoutDto payoutDto = new TbsTvPayoutDto();
					//序号
					if(singDto[1] == null || "".equals(singDto[1].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'国库代码'为空!");
					}
					payoutDto.setStrecode(singDto[1]+"00");//国库代码
					trecode = singDto[1]+"00";
					if(i == 0) {
						//校验文件重复,国库代码+文件名
						String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5101);
						if(null != errorInfo && errorInfo.length() > 0) {
							multi.getErrorList().add(errorInfo);
							return multi;
						}
					}
					if(!this.checkTreasury(trecode, bookorgCode)) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中国库主体代码："+trecode+" 没有在'国库主体信息参数'中查找到!");
					}
					if(singDto[2] == null || "".equals(singDto[2].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'拨款机关'为空!");
					}
					payoutDto.setSbillorg(singDto[2]);//拨款机关
					
					if(singDto[3] == null || "".equals(singDto[3].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'收款单位代码'为空!");
					}
					if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
						if(singDto[3]!=null && singDto[3].length() > 15) {
							payoutDto.setSbdgorgcode(singDto[3].substring(0, 15));//收款单位代码
						}else {
							payoutDto.setSbdgorgcode(singDto[3]);//收款单位代码
						}
					}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
						payoutDto.setSbdgorgcode(singDto[3]);//收款单位代码
					}
					//修改为按照国库代码+预算单位唯一
					if(!rpmap.containsKey(singDto[1]+"00"+payoutDto.getSbdgorgcode().trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中预算单位代码 '"+payoutDto.getSbdgorgcode()+"' 没有在法人代码表中找到!");
					}else {
						if(!"1".equals(rpmap.get(singDto[1]+"00"+payoutDto.getSbdgorgcode()).getCmayaprtfund().trim())) {
							multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中预算单位代码 '"+payoutDto.getSbdgorgcode()+"' 对应预算单位不能进行实拨资金!");
						}
					}
					if(singDto[4] == null || "".equals(singDto[4].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'收款单位名称'为空!");
					}
					
					if(singDto[4].getBytes().length > 60) {
						payoutDto.setSbdgorgname(singDto[4].substring(0,30)); //预算单位名称
						payoutDto.setSpayeename(singDto[4].substring(0,30));  //收款人名称
					}else {
						payoutDto.setSbdgorgname(singDto[4]);//预算单位名称
						payoutDto.setSpayeename(singDto[4]);//收款人名称
					}
					
					if(singDto[5] == null || "".equals(singDto[5].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'收款单位账号'为空!");
					}
					
					if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
						//不采用新接口，就需要进行字符截取
						if(singDto[5].length() > 32) {
							payoutDto.setSpayeeacct(singDto[5].substring(0,32));//收款单位账号
						}else {
							payoutDto.setSpayeeacct(singDto[5]);//收款单位账号
						}
					}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
						payoutDto.setSpayeeacct(singDto[5]);//收款单位账号
					}	
					
					if(singDto[6] == null || "".equals(singDto[6].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'收款单位开户行'为空!");
					}
					
					TsPaybankDto bnkpaydto = paymap.get(singDto[6].trim());
					if(null == bnkpaydto) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录根据收款单位开户行：'"+singDto[6].trim()+"'没有在'支付行号查询参数' 中找到相应清算行行号!");
					}else {
						if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中收款单位开户行：'"+singDto[6].trim()+"' 处于'生效前'状态!");
						}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中收款单位开户行：'"+singDto[6].trim()+"' 处于'注销'状态!");
						}	
					}									
					payoutDto.setSpayeeopnbnkno(singDto[6].trim());//收款单位开户行
					/*
					 * 分包，此代码5.3修改成按开户行分包
					payoutDto.setSpayeebankno(bnkpaydto.getSpaybankno());
					bnkset.add(bnkpaydto.getSpaybankno()); // 根据清算行分包
					*/
					payoutDto.setSpayeebankno(singDto[6].trim()); //填写成开户行
					bnkset.add(payoutDto.getSpayeeopnbnkno()); // 根据开户行分包
					if(singDto[7] == null || "".equals(singDto[7].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'拨款凭证号'为空!");
					}
					payoutDto.setSvouno(singDto[7]);//拨款凭证号
					voulist.add(trecode.trim()+","+singDto[7].trim());
					payoutDto.setSpaylevel(singDto[8]);//级次
					if(singDto[9] == null || "".equals(singDto[9].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'支出功能科目代码'为空!");
					}
					String err = this.verifySubject(dmap, singDto[9], MsgConstant.MSG_NO_5101, "1", fi.getName(),singDto[7]);
					if (!"".equals(err)) {
						if (null != dmap.get(singDto[9])) {
							multi.getErrorList().add(err);
						}else{
							if(null == movesubs.get(singDto[9])){
								multi.getErrorList().add("实拨资金文件[" + fi.getName() + "]中功能科目代码 " + singDto[9] 	+ "没有在'预算科目参数'或'财政调拨支出预算科目表'中找到!");
							}
						} 
					}
					payoutDto.setSfuncsbtcode(singDto[9]);//支出功能科目代码
					if (singDto[10].getBytes().length>20) {
						singDto[10] = singDto[10].substring(0,10);
					}
					payoutDto.setSmovefundreason(singDto[10]);//支出原因
					if(singDto[11] == null || "".equals(singDto[11].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'支出金额'为空!");
					}
					payoutDto.setFamt(new BigDecimal(singDto[11])); //支出金额
					if(singDto[12] == null || "".equals(singDto[12].trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[7].trim()+"]的记录中存在'拨款日期'为空!");
					}
//					payoutDto.setDacct(CommonUtil.strToDate(singDto[12]));//拨款日期
					payoutDto.setDvoucher(CommonUtil.strToDate(singDto[12])); //拨款日期(凭证日期)
					payoutDto.setSpaybizkind(singDto[13].substring(0,1)); //拨款种类
					err = this.verifySubject(dmap, singDto[14], MsgConstant.MSG_NO_5101, "2", fi.getName(),singDto[7]);
					if(!"".equals(err)) {
						multi.getErrorList().add(err);
					}
					if(singDto.length == 14) {
						payoutDto.setSecosbtcode(""); //支出经济科目代码
					}else {
						payoutDto.setSecosbtcode(singDto[14]);//支出经济科目代码
					}
					
					payoutDto.setIofyear(Integer.parseInt(fi.getName().substring(0, 4))); // 所属年度
					payoutDto.setSpayeracct(sbdto.getPayeracct()); //付款人账号不能为空
					payoutDto.setSpayername(sbdto.getPayername()); //付款人名称
					payoutDto.setCbdgkind(MsgConstant.BDG_KIND_IN); //预算种类不能为空，默认预算内
					payoutDto.setSbookorgcode(bookorgCode);// 核算主体代码
					payoutDto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期
					famt = famt.add(new BigDecimal(singDto[11]));
					if(biztype != null && !"".equals(biztype)) {
						payoutDto.setSbiztype(biztype);// 业务类型
					}else {
						payoutDto.setSbiztype(""); //业务类型
					}
					payoutDto.setCtrimflag("0"); //调整期标志 默认为0
					
					payoutDto.setSpackageno(tmpPackNo);  //包流水号
					payoutDto.setSfilename(fi.getName());
					payoutDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					recordNum++;
					listdto.add(payoutDto);
				}else {
					break;
				}
				
			}	
			Map<String, List<IDto>> bnkli = new HashMap<String, List<IDto>>();
			//按照清算行行号进行分包
			  //1.首先得到清算行的数量
			for (String st : bnkset) {
				List<IDto> nlist = new ArrayList<IDto>();
				for (IDto dto : listdto) {
					TbsTvPayoutDto bkdto = (TbsTvPayoutDto) dto;
					if (st.equals(bkdto.getSpayeeopnbnkno())) {
						nlist.add(bkdto);
					}
				}
				bnkli.put(st, nlist);
			}
			
			//2.如果某清算行下的明细DTO的数量大于TIPS分包限制,则进行再分包
			for (String st : bnkset) {
				List<IDto> detlist = bnkli.get(st);
				int li_Detail = (detlist.size()-1) / 1000;
				for (int k = 0; k <= li_Detail; k++) {
					int li_TempCount = 0;
					if (li_Detail == k) {
						li_TempCount = detlist.size();
					} else {
						li_TempCount = (k + 1) * 1000;
					}
					//生成包流水号
					tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
							.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
									SequenceName.TRAID_SEQ_CACHE,
									SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					for (int j = k * 1000; j < li_TempCount; j++) {
						TbsTvPayoutDto bkdto = (TbsTvPayoutDto) detlist.get(j);
						//设置包流水号
						bkdto.setSpackageno(tmpPackNo);
						//进行金额统计，用于分包
						famtPack = famtPack.add(bkdto.getFamt());
						//进行数量的统计
						fbcount ++;
						//将所有填充数据完成的DTO保存
						fadtos.add(bkdto);
					}
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgCode);
					packdto.setStrecode(trecode);
					packdto.setSfilename(fi.getName());
					
					TsConvertfinorgDto condto = new TsConvertfinorgDto();
					condto.setSorgcode(bookorgCode);
					condto.setStrecode(trecode);
					List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
					if(conlist == null || conlist.size() == 0 ){
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 中根据核算主体代码："+bookorgCode+"、 国库主体代码："+trecode+" 没有在 '财政机构信息维护参数'中找到对应财政机构代码!");
					} else {
						TsConvertfinorgDto gd = (TsConvertfinorgDto)conlist.get(0);
						packdto.setStaxorgcode(gd.getSfinorgcode());
					}					
					packdto.setScommitdate(TimeFacade.getCurrentStringTime());
					packdto.setSaccdate(TimeFacade.getCurrentStringTime());
					packdto.setSpackageno(tmpPackNo);
					packdto.setSoperationtypecode(biztype);
					packdto.setIcount(fbcount);
					packdto.setNmoney(famtPack);
					packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdto.setSusercode(userid);
					packdto.setImodicount(0);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					famtPack = new BigDecimal("0.00");
					fbcount = 0 ;
					packdtos.add(packdto);
				}
			}
			if (famt.compareTo(famthead) != 0) {
				multi.getErrorList().add("汇总金额[" + famt + "]与文件头总金额[" + famthead
						+ "]不符");
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
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+item.trim()+"]的记录中存在凭证编号重复");
				}
			}
			
			multi.setFatherDtos(fadtos);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt);//总金额
			multi.setTotalCount(recordNum); //总笔数
			return multi;

		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("实拨资金文件解析出错 \n"+e.getMessage(), e);
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
