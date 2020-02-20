package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpaySubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author zhang 商行办理支付划款申请文件导入解析类
 */
public class ApplypayTipsFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		int recordNum = 0; // 记录记录器
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// 业务类型
		mulitDto.setSbookorgcode(sbookorgcode);// 核算主体代码
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonFamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();
		List<IDto> sondtos_tmp = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<String> voulist = new ArrayList<String>(); // 凭证编号集
		File fi = new File(file);
		Map<String, BigDecimal> diremap = new HashMap<String, BigDecimal>();
		String strecode = "";
		String mainvou = "";
		String mainvou_tmp = "";
		String svo = "";
		try {
			Map<String, TdCorpDto> rpmap = this.verifyCorpcode(sbookorgcode); // 法人代码缓存
			HashMap<String, TsTreasuryDto> tremap = SrvCacheFacade
					.cacheTreasuryInfo(sbookorgcode);
			List<String[]> fileContent = super.readFile(file, ",");
			String trecode = "";
			CommonParamDto _dto = (CommonParamDto) paramdto;
			String encyptMode = _dto.getEncryptMode();
			int record = fileContent.size();
			if (StateConstant.SM3_ENCRYPT.equals(encyptMode) && record > 1) {
				record = record - 1;
			}
			HashMap<String, TsGenbankandreckbankDto> mapGenBankByBankCode = SrvCacheFacade
					.cacheGenBankInfo(null);
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(sbookorgcode); // 获取收付款人信息
			Map<String, TsInfoconnorgaccDto> bookacctMap = this
					.getBookAcctMap(sbookorgcode);// 付款人账户信息
			Map<String, String> bankInfo = this.getBankInfo(sbookorgcode);// 从行名对照表获取银行名（财政）与行号信息
			String key = "";
			for (int i = 0; i < record; i++) {

				String[] strs = fileContent.get(i);
				String reckbankcode = "aaaaaaaaaaaa";
				if (i == 0) {
					if (!strs[0].contains("**")) {
						strs[0] = "**" + strs[0];
					}

					// 按照代理银行代码取对应的支付行号，用于取对应的密钥

					
					if (mapGenBankByBankCode
							.containsKey(sbookorgcode + strs[7])) {
						reckbankcode = mapGenBankByBankCode.get(
								sbookorgcode + strs[7]).getSreckbankcode();
					}else
					{
						String mytrecode = "";
						if(strs[0].contains("**"))
							mytrecode = strs[0].replace("**", "").trim();
						else
							mytrecode = strs[0];
						if (mapGenBankByBankCode.containsKey(sbookorgcode+mytrecode+strs[7]))
							reckbankcode =mapGenBankByBankCode.get(sbookorgcode+mytrecode+strs[7]).getSreckbankcode();
					}

					TsMankeyDto keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
							.getKeyMode(), sbookorgcode, reckbankcode);
					if (null != keydto) {
						key = keydto.getSkey();
						if (StateConstant.SM3_ENCRYPT.equals(encyptMode)) {
							if (!SM3Process.verifySM3SignFile(file, key)) {
								mulitDto.getErrorList().add(
										"划款申请资金文件[" + fi.getName()
												+ "]SM3签名验证失败!");
								return mulitDto;
							}
						}
					} else {
						mulitDto.getErrorList().add(
								"实拨资金文件[" + fi.getName() + "没有查找到对应的解密密钥！");
						return mulitDto;
					}
				}

				if (strs[0].contains("**")) {
					// 包流水号
					String tmpPackNo = SequenceGenerator
							.changePackNoForLocal(SequenceGenerator
									.getNextByDb2(
											SequenceName.FILENAME_PACKNO_REF_SEQ,
											SequenceName.TRAID_SEQ_CACHE,
											SequenceName.TRAID_SEQ_STARTWITH,
											MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					;
					/**
					 * s 文件第一行为主体信息
					 */
					strs[0] = strs[0].replace("**", "").trim();
					mainvou = SequenceGenerator.getNextByDb2(
							SequenceName.HKSQ_SEQ,
							SequenceName.TRAID_SEQ_CACHE,
							SequenceName.TRAID_SEQ_STARTWITH);
					mainvou_tmp = mainvou;

					TbsTvBnkpayMainDto mainDto = new TbsTvBnkpayMainDto();
					mainDto.setIvousrlno(Long.parseLong(mainvou));
					strecode = strs[0];
					if (!this.checkTreasury(strs[0], sbookorgcode)) {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请文件[" + fi.getName() + "] 凭证编号为["
										+ strs[13].trim() + "]的记录中国库主体代码："
										+ strs[0] + " 在'国库主体信息参数'中不存在!");
					}
					trecode = strs[0];
					mainDto.setStrecode(strs[0]); // 国库代码
					String errorInfo = this.checkFileExsit(sbookorgcode,
							trecode, fi.getName(), MsgConstant.APPLYPAY_DAORU);
					if (null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					mainDto.setSbiztype(biztype);// 业务类型
					mainDto.setSpayername(strs[1]);// 付款人全称(单位代码)
					mainDto.setSpayeracct(strs[2]); // 付款人帐号
					if (!bookacctMap.containsKey(strs[0] + strs[2])) {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请文件[" + fi.getName() + "] 凭证编号为["
										+ strs[13].trim() + "]的记录中付款人账号："
										+ strs[2].trim()
										+ "错误或没有在'资金拨付付款人账户维护'参数中维护!");
					}
					mainDto.setSsndbnkno(strs[3]);// 付款人开户银行
					mainDto.setSpayeropnbnkno(strs[4]); // 付款人开户银行行号
					mainDto.setSpayeename(strs[5]); // 收款人全称
					mainDto.setSpayeeacct(strs[6]); // 收款人帐号
					mainDto.setSrcvbnkno(strs[7]);// 收款人开户行
					/**
					 * 增加判断是否需要补录行号
					 */
					if (ITFECommonConstant.ISMATCHBANKNAME
							.equals(StateConstant.IF_MATCHBNKNAME_YES)) {
						if (strs.length > 16 && null != strs[16]
								&& !"".equals(strs[16])) {
							mainDto.setSpayeeaddr(strs[16]); // 将收款人开户行行名填入收款人地址中
							if (null != strs[8] && !"".equals(strs[8])) {
								mainDto.setSpayeeopnbnkno(strs[8]);
								mainDto
										.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);
							} else {
								if (null != bankInfo.get(strs[16])
										&& !"".equals(bankInfo.get(strs[16]))) {
									mainDto.setSpayeeopnbnkno(bankInfo
											.get(strs[16]));
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								} else {
									mainDto.setSpayeeopnbnkno("");
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}
							}
						} else {
							mulitDto.getErrorList().add(
									"商行划款申请文件[" + fi.getName() + "] 凭证编号为["
											+ strs[13].trim()
											+ "]的记录中 收款人开户行行名 为空，请查证!");
						}
					} else {
						// 收款人开户行代理行也就是代理行需要转换成支付行号
						if (null != reckbankcode && !"aaaaaaaaaaaa".equals(reckbankcode)) {
							if (!reckbankcode.equals(strs[8])) {
								mulitDto.getErrorList().add(
										"商行划款申请文件[" + fi.getName() + "] 凭证编号为["
												+ strs[13].trim()
												+ "]的记录中收款人开户行行号：" + strs[8]
												+ " 与银行代码与支付行号对应关系中获取的支付行号"
												+ reckbankcode + "不一致!");
							}
						} else {
							mulitDto.getErrorList().add(
									"商行划款申请文件[" + fi.getName() + "] 凭证编号为["
											+ strs[13].trim()
											+ "]的记录中收款人开户行行号：" + strs[8]
											+ " 没有在银行代码与支付行号对应关系维护!");
						}

						mainDto.setSpayeeopnbnkno(strs[8]); // 收款人开户行行号
					}

					//收付款人信息验证    根据 收款人开户行行号 找收付款人信息
					if (StringUtils.isBlank(mainDto.getSpayeeopnbnkno())) {
						mulitDto.getErrorList().add("商行划款申请文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim()	+ "]的记录中没有找到收款人开户行行号信息!");
					} else {
						TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpayeeopnbnkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
						if (null == tmppayacctinfoDto) {
							mulitDto.getErrorList().add("商行划款申请文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中根据国库代码："+mainDto.getStrecode()+", 收款人开户行行号："+ mainDto.getSpayeeopnbnkno()+", 付款人账号："+mainDto.getSpayeracct()+", 收款人账号："+mainDto.getSpayeeacct()+ " 没有找到收付款人信息!");
						}else{
							if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// 付款人帐户
								mulitDto.getErrorList().add("商行划款申请文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,付款人帐户:"  + mainDto.getSpayeracct() + "与维护的收付款人信息中不一致!");
							}
							if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// 付款人名称
								mulitDto.getErrorList().add("商行划款申请文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,付款人名称:"  + mainDto.getSpayername() + "与维护的收付款人信息中不一致!");
							}
							if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// 收款人账户
								mulitDto.getErrorList().add("商行划款申请文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,收款人账户:"  + mainDto.getSpayeeacct() + "与维护的收付款人信息中不一致!");
							}
							if(strs[5].equals(tmppayacctinfoDto.getSpayeename())) {
								System.out.println("一样啊！！！");
							}
							if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// 收款人名称
								mulitDto.getErrorList().add("商行划款申请文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,收款人名称:"  + mainDto.getSpayeename() + "与维护的收付款人信息中不一致!");
							}
							System.out.println("strs[5] = "+strs[5]);
							System.out.println("mainDto.getSpayeename() = "+mainDto.getSpayeename());
							System.out.println("tmppayacctinfoDto.getSpayeename() = "+tmppayacctinfoDto.getSpayeename());
						}
						
					}
					mainDto.setCbdgkind(strs[9]);// 预算种类代码
					if (strs[10] != null && !strs[10].trim().equals("")) {
						mainDto.setFzerosumamt(new BigDecimal(strs[10].trim())); // 零余额发生额
					} else {
						mainDto.setFzerosumamt(new BigDecimal("0.00")); // 零余额发生额
					}
					if (strs[11] != null && !strs[11].trim().equals("")) { // 小额现金发生额
						mainDto
								.setFsmallsumamt(new BigDecimal(strs[11].trim()));
					} else {
						mainDto.setFsmallsumamt(new BigDecimal("0.00"));
					}
					famt = famt.add(mainDto.getFzerosumamt()).add(
							mainDto.getFsmallsumamt());
					mainDto.setSsrlacctbriefcode(strs[12]);
					mainDto.setSvouno(strs[13]); // 凭证编号
					svo = strs[13];
					voulist.add(trecode.trim() + "," + strs[13].trim());
					mainDto.setDvoucher(CommonUtil.strToDate(strs[14]));// 凭证日期
					mainDto.setIofyear(Integer.parseInt(fi.getName().substring(
							0, 4))); // 所属年度不为空
					mainDto.setSagentbnkcode(strs[8]);// 代理银行行号不能为空
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); // 受理日期不为空(默认当前日期)
					mainDto.setSbookorgcode(sbookorgcode);// 核算主体代码
					mainDto.setSfilename(fi.getName());
					mainDto.setSpackno(tmpPackNo);
					mainDto.setCtrimflag(fi.getName().substring(
							fi.getName().indexOf(".") - 1,
							fi.getName().indexOf(".")));
					if (strs.length > 15) {
						mainDto.setSaddword(strs[15]);
					}
					fatherdtos.add(mainDto);
				} else {
					// sub信息
					TbsTvBnkpaySubDto subDto = new TbsTvBnkpaySubDto();
					String err = this.verifySubject(bmap, strs[1],
							MsgConstant.APPLYPAY_DAORU, "1", fi.getName(), svo);
					if (!"".equals(err)) {
						mulitDto.getErrorList().add(err);
					}
					subDto.setSfuncsbtcode(strs[1]); // 功能科目代码

					if (null != tremap.get(trecode)) {
						if (StateConstant.COMMON_YES.equals(tremap.get(trecode)
								.getSisuniontre())) { // 启用的情况下判断经济代码不能为空
							err = this.verifySubject(bmap, strs[2],
									MsgConstant.APPLYPAY_DAORU, "2", fi
											.getName(), svo);
							if (!"".equals(err)) {
								mulitDto.getErrorList().add(err);
							}
							subDto.setSecosbtcode(strs[2]); // 经济科目代码
						} else { // 不启用的情况下，不管文件中是否有经济代码，都填写空值
							subDto.setSecosbtcode("");
						}
					}

					subDto.setSbdgorgcode(strs[0]); // 预算单位代码
					// 修改为按照国库代码+法人代码进行维护
					if (!rpmap.containsKey(trecode
							+ subDto.getSbdgorgcode().trim())) {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请文件[" + fi.getName() + "] 凭证编号为["
										+ svo + "]的记录中预算单位代码 '"
										+ subDto.getSbdgorgcode()
										+ "' 在法人代码表中不存在!");
					}
					subDto.setCacctprop(strs[3]);// 账户性质
					subDto.setFamt(new BigDecimal(strs[4]));// 发生额
					sonFamt = sonFamt.add(new BigDecimal(strs[4]));
					subDto.setSbookorgcode(sbookorgcode); // 核算主体代码
					subDto.setIvousrlno(Long.parseLong(mainvou)); // 凭证流水号
					subDto.setIgrpinnerseqno(recordNum + 1); // 凭证流水号
					if (diremap.containsKey(strs[0]+","+strs[1]+","+strs[2]+","+strs[3])) { // 对相同预算科目、功能代码、经济代码进行汇总
						diremap.put(strs[0] + "," + strs[1] + "," + strs[2]+","+strs[3],diremap.get(strs[0] + "," + strs[1] + ","+ strs[2]+","+strs[3]).add(new BigDecimal(strs[4])));
					} else {
						diremap.put(strs[0] + "," + strs[1] + "," + strs[2]+","+strs[3],new BigDecimal(strs[4]));
					}
					sondtos_tmp.add(subDto);
					recordNum++;
				}

			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("商行办理支付划款申请文件解析出错：\n" + e.getMessage(),
					e);
		}
		if (famt.compareTo(new BigDecimal("0.00")) == 0) {
			mulitDto.getErrorList().add(
					"商行办理支付划款申请文件[" + fi.getName() + "]发生额不能为0");
		}
		if (famt.compareTo(sonFamt) != 0) {
			mulitDto.getErrorList().add(
					"商行办理支付划款申请文件[" + fi.getName() + "]中汇总金额[" + famt
							+ "]与明细总金额[" + sonFamt + "]不符");
		}

		// 文件中凭证编号校验
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		for (String item : voulist) {
			oldSize = sets.size();
			sets.add(item);
			newSize = sets.size();
			if (newSize == oldSize) {
				mulitDto.getErrorList().add(
						"商行办理支付划款申请文件[" + fi.getName() + "]中存在凭证编号重复");
			}
		}
		// 按照功能、经济、预算单位汇总
		if (recordNum > 499) {
			int Igrpinnerseqno = 0;
			for (String single : diremap.keySet()) {
				String[] strs = single.split(",");
				TbsTvBnkpaySubDto diredto = new TbsTvBnkpaySubDto();
				diredto.setIvousrlno(Long.parseLong(mainvou_tmp));
				diredto.setSfuncsbtcode(strs[1]);
				diredto.setSecosbtcode(strs[2]);
				diredto.setIgrpinnerseqno(++Igrpinnerseqno);
				diredto.setCacctprop(strs[3]);
				diredto.setSbdgorgcode(strs[0]);
				diredto.setFamt(diremap.get(single));
				diredto.setSbookorgcode(sbookorgcode);
				sondtos.add(diredto);
			}
		} else {
			sondtos.addAll(sondtos_tmp);
		}
		if (sondtos.size() > 499) {
			mulitDto.getErrorList().add(
					"商行办理支付划款申请文件[" + fi.getName()
							+ "]中明细子体信息按照功能类科目代码、经济类科目代码、预算单位代码进行汇总后，总笔数为["
							+ sondtos.size() + "],超过499笔，请手工拆包后导入!");
		}
		mulitDto.setFatherDtos(fatherdtos);
		mulitDto.setSonDtos(sondtos);
		mulitDto.setPackDtos(packdtos);
		mulitDto.setVoulist(voulist);
		mulitDto.setFamt(famt);

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
