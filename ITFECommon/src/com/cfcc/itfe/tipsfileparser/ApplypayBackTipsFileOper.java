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
import org.eclipse.swt.internal.LONG;

import com.cfcc.deptone.common.util.DateUtil;
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
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author zhang 商行办理支付划款申请退回文件导入解析类
 */
public class ApplypayBackTipsFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		int recordNum = 0; // 记录记录器
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// 业务类型
		mulitDto.setSbookorgcode(sbookorgcode);// 核算主体代码
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonFamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();

		List<String> voulist = new ArrayList<String>(); // 凭证编号集
		List<String> oriVouList = new ArrayList<String>(); // 原2凭证编号集
		File fi = new File(file);
		HashMap<String, TbsTvBnkpayMainDto> mainMap = new HashMap<String, TbsTvBnkpayMainDto>();
		HashMap<String, String> vouMap = null;
		int j = 0;
		String strecode = null;
		String mainvou = null;
		String svo = null;
		String key = null;
		TvPayreckBankBackDto paydto = (TvPayreckBankBackDto) idto;
		try {
			Map<String, TdCorpDto> rpMap = this.verifyCorpcode(sbookorgcode); // 法人代码缓存
			HashMap<String, TsTreasuryDto> treMap = SrvCacheFacade
					.cacheTreasuryInfo(sbookorgcode);
			List<String[]> fileContent = super.readFile(file, ",");
			CommonParamDto _dto = (CommonParamDto) paramdto;
			String encyptMode = _dto.getEncryptMode();
			int record = fileContent.size();
			if (StateConstant.SM3_ENCRYPT.equals(encyptMode) && record > 1) {
				record = record - 1;
			}
			HashMap<String, TsGenbankandreckbankDto> mapGenBankByBankCode = SrvCacheFacade
					.cacheGenBankInfo(null);
			Map<String, TsInfoconnorgaccDto> bookacctMap = this
					.getBookAcctMap(sbookorgcode);// 收款人账户信息
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(sbookorgcode); // 获取收付款人信息
			Map<String, String> bankInfo = this.getBankInfo(sbookorgcode);// 从行名对照表获取银行名（财政）与行号信息
			TbsTvBnkpayMainDto mainDto = null;
			for (int i = 0; i < record; i++) {

				String[] strs = fileContent.get(i);
				if (i == 0) {
					if (!strs[0].contains("**")) {
						strs[0] = "**" + strs[0];
					}
					// 按照代理银行代码取对应的支付行号，用于取对应的密钥
					String reckbankcode = "aaaaaaaaaaaa";
					if (mapGenBankByBankCode
							.containsKey(sbookorgcode + strs[3])) {
						reckbankcode = mapGenBankByBankCode.get(
								sbookorgcode + strs[3]).getSreckbankcode();
					}else
					{
						String mytrecode = "";
						if(strs[0].contains("**"))
							mytrecode = strs[0].replace("**", "").trim();
						else
							mytrecode = strs[0];
						if (mapGenBankByBankCode.containsKey(sbookorgcode+mytrecode+strs[3]))
							reckbankcode =mapGenBankByBankCode.get(sbookorgcode+mytrecode+strs[3]).getSreckbankcode();
					}
					TsMankeyDto keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
							.getKeyMode(), sbookorgcode, reckbankcode);
					if (null != keydto) {
						key = keydto.getSkey();
						if (StateConstant.SM3_ENCRYPT.equals(encyptMode)) {
							if (!SM3Process.verifySM3SignFile(file, key)) {
								mulitDto.getErrorList().add(
										"商行申请退款[" + fi.getName()
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
				if (strs[0].contains("**")) { // 包含型号的为文件的主体信息
					j = 0;
					mainDto = new TbsTvBnkpayMainDto();
					strecode = strs[0].replace("**", "").trim();
					if (!this.checkTreasury(strecode, sbookorgcode)) {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["
										+ strs[13].trim() + "]的记录中国库主体代码："
										+ strs[0] + " 在'国库主体信息参数'中不存在!");
					}
					mainDto.setStrecode(strecode); // 国库代码
					String errorInfo = this.checkFileExsit(sbookorgcode,
							strecode, fi.getName(),
							MsgConstant.APPLYPAY_BACK_DAORU);
					if (null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					mainDto.setSbiztype(biztype);// 业务类型
					// 付款人全称(单位代码)不能为空
					if (StringUtils.isNotBlank(strs[1])) {
						mainDto.setSpayername(strs[1]);// 付款人全称(单位代码)
					} else {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["
										+ strs[13].trim() + "]的记录付款人全称不能为空!");
					}
					mainDto.setSpayeracct(strs[2]); // 付款人帐号
					mainDto.setSsndbnkno(strs[3]);// 付款人开户银行

					/**
					 * 增加判断是否需要补录行号
					 */
					if (ITFECommonConstant.ISMATCHBANKNAME
							.equals(StateConstant.IF_MATCHBNKNAME_YES)) {
						if (strs.length > 15 && null != strs[15]
								&& !"".equals(strs[15])) {
							mainDto.setSpayeeaddr(strs[15]); // 将收款人开户行行名填入收款人地址中
							if (null != strs[4] && !"".equals(strs[4])) {
								mainDto.setSpayeropnbnkno(strs[4]);
								mainDto
										.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);
							} else {
								if (null != bankInfo.get(strs[15])
										&& !"".equals(bankInfo.get(strs[15]))) {
									mainDto.setSpayeropnbnkno(bankInfo
											.get(strs[15]));
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								} else {
									mainDto.setSpayeropnbnkno("");
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}
							}
						} else {
							mulitDto.getErrorList().add(
									"商行划款退回申请文件[" + fi.getName() + "] 凭证编号为["
											+ strs[13].trim()
											+ "]的记录中 收款人开户行行名 为空，请查证!");
						}
					}else {
						mainDto.setSpayeropnbnkno(strs[4]); // 付款人开户银行行号	
						//收款人开户行代理行也就是代理行需要转换成支付行号
						TsGenbankandreckbankDto tmpDto = mapGenBankByBankCode.get(sbookorgcode+strs[3]);
						if(tmpDto==null)
							tmpDto = mapGenBankByBankCode.get(sbookorgcode+strecode+strs[3]);
						if(null != tmpDto) {
							String tcbsbnk =tmpDto.getSreckbankcode();

							if (!tcbsbnk.equals(strs[4])) {
								mulitDto.getErrorList().add(
										"商行划款退回申请文件[" + fi.getName()
												+ "] 凭证编号为[" + strs[13].trim()
												+ "]的记录中收款人开户行行号：" + strs[4]
												+ " 与银行代码与支付行号对应关系中获取的支付行号"
												+ tcbsbnk + "不一致!");
							}
						}

						if ((ITFECommonConstant.SRC_NODE.equals("000001900000")&&!paydto.getSpaysndbnkno().substring(0, 3).equals(strs[4].substring(0, 3)))||!paydto.getSpaysndbnkno().equals(strs[4])) {
							mulitDto.getErrorList()
									.add(
											"商行划款退回申请文件[" + fi.getName()
													+ "] 凭证编号为["
													+ strs[13].trim()
													+ "]的记录中收款人开户行行号："
													+ strs[4] + " 与界面选择的支付行号"
													+ paydto.getSpaysndbnkno()
													+ "不一致!");
						}
					}

					// 收款人全称 不能为空
					if (StringUtils.isNotBlank(strs[5])) {
						mainDto.setSpayeename(strs[5]);
					} else {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["
										+ strs[13].trim() + "]的记录收款人全称不能为空!");
					}
					if (!bookacctMap.containsKey(strecode + strs[6])) {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请文件[" + fi.getName() + "] 凭证编号为["
										+ strs[13].trim() + "]的记录中收款人账号："
										+ strs[6].trim()
										+ "错误或没有在'资金拨付付款人账户维护'参数中维护!");
					}
					mainDto.setSpayeeacct(strs[6]); // 收款人帐号

					mainDto.setSrcvbnkno(strs[7]);// 收款人开户行
					mainDto.setSpayeeopnbnkno(strs[8]); // 收款人开户行行号

					
					//收付款人信息验证	根据 付款人开户行行号 找收付款人信息
					if (StringUtils.isBlank(mainDto.getSpayeropnbnkno())) {
						mulitDto.getErrorList().add("商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim()	+ "]的记录中没有找到付款人开户行行号信息!");
					} else {
						TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpayeropnbnkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
						if (null == tmppayacctinfoDto) {
							mulitDto.getErrorList().add("商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中根据 国库代码："+mainDto.getStrecode()+",付款人开户行行号："+ mainDto.getSpayeropnbnkno()+ ", 付款人账号："+mainDto.getSpayeracct()+", 收款人账号："+mainDto.getSpayeeacct()+ " 没有找到收付款人信息!");
						}else{
							if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// 付款人帐户
								mulitDto.getErrorList().add("商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,付款人帐户:"  + mainDto.getSpayeracct() + "与维护的收付款人信息中不一致!");
							}
							if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// 付款人名称
								mulitDto.getErrorList().add("商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,付款人名称:"  + mainDto.getSpayername() + "与维护的收付款人信息中不一致!");
							}
							if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// 收款人账户
								mulitDto.getErrorList().add("商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,收款人账户:"  + mainDto.getSpayeeacct() + "与维护的收付款人信息中不一致!");
							}
							if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// 收款人名称
								mulitDto.getErrorList().add("商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["+ strs[13].trim() + "]的记录中,收款人名称:"  + mainDto.getSpayeename() + "与维护的收付款人信息中不一致!");
							}
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
					voulist.add(strecode.trim() + "," + strs[13].trim());
					mainDto.setDvoucher(CommonUtil.strToDate(strs[14]));// 凭证日期
					mainDto.setIofyear(Integer.parseInt(fi.getName().substring(
							0, 4))); // 所属年度不为空
					mainDto.setSagentbnkcode(strs[4]);// 代理银行行号不能为空
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); // 受理日期不为空(默认当前日期)
					mainDto.setSbookorgcode(sbookorgcode);// 核算主体代码
					mainDto.setSfilename(fi.getName());
					mainDto.setDpayentrustdate(paydto.getDpayentrustdate());
					mainDto.setSpaydictateno(paydto.getSpaydictateno());
					mainDto.setSpaymsgno(strs[8]);
					mainDto.setSpaymsgno(paydto.getSpaymsgno());
					mainDto.setSpaysndbnkno(paydto.getSpaysndbnkno());
					mainDto.setCtrimflag(fi.getName().substring(
							fi.getName().indexOf(".") - 1,
							fi.getName().indexOf(".")));
					// 汇总记录中清除上一个凭证信息
					vouMap = new HashMap<String, String>();
				} else {// 明细中的信息需要按照原凭证编号进行分包
					TbsTvBnkpaySubDto subDto = new TbsTvBnkpaySubDto();
					List<IDto> sondtolist = new ArrayList<IDto>();
					// 原凭证编号、原凭证日期 赋一个固定的值，为了国库的处理的方便性，造成找不到原凭证信息的情况，不在分包
					if (StateConstant.COMMON_YES
							.equals(ITFECommonConstant.IFAUTOGENORIVOUNO)) {
						strs[0] = svo;
						strs[1] = TimeFacade.getCurrentStringTimebefor();
					}
					subDto.setSorivouno(strs[0]);// 原凭证编号
					subDto.setDorivoudate(CommonUtil.strToDate(strs[1]));// 原凭证日期
					subDto.setSbdgorgcode(strs[2]); // 预算单位代码
					// 修改为按照国库代码+法人代码进行维护
					if (!rpMap.containsKey(strecode
							+ subDto.getSbdgorgcode().trim())) {
						mulitDto.getErrorList().add(
								"商行办理支付划款申请退回文件[" + fi.getName() + "] 凭证编号为["
										+ svo + "]的记录中预算单位代码 '"
										+ subDto.getSbdgorgcode()
										+ "' 在法人代码表中不存在!");
					}
					subDto.setSfuncsbtcode(strs[3]); // 功能科目代码
					String err = this.verifySubject(bmap, strs[3],
							MsgConstant.APPLYPAY_BACK_DAORU, "1", fi.getName(),
							svo);
					if (!"".equals(err)) {
						mulitDto.getErrorList().add(err);
					}
					if (null != treMap.get(strecode)) {
						if (StateConstant.COMMON_YES.equals(treMap
								.get(strecode).getSisuniontre())) { // 启用的情况下判断经济代码不能为空
							err = this.verifySubject(bmap, strs[4],
									MsgConstant.APPLYPAY_BACK_DAORU, "2", fi
											.getName(), svo);
							if (!"".equals(err)) {
								mulitDto.getErrorList().add(err);
							}
							subDto.setSecosbtcode(strs[4]); // 经济科目代码
						} else { // 不启用的情况下，不管文件中是否有经济代码，都填写空值
							subDto.setSecosbtcode(null);
						}
					}
					subDto.setCacctprop(strs[5]);// 账户性质
					subDto.setFamt(new BigDecimal(strs[6].trim()));// 发生额
					sonFamt = sonFamt.add(new BigDecimal(strs[6].trim()));// 计算明细合计金额
					subDto.setIgrpinnerseqno(recordNum + 1);// 组内序号
					subDto.setSbookorgcode(sbookorgcode); // 核算主体代码
					// 说明明细中已包含原凭证编号+原凭证日期相同的记录
					String keyWord = strs[0] + strs[1];
					if (vouMap.containsKey(keyWord)) {
						if (subDto.getCacctprop().equals(
								StateConstant.CONPAY_ZEROBALANCE)) {
							mainMap.get(keyWord).setFzerosumamt(
									mainMap.get(keyWord).getFzerosumamt().add(
											subDto.getFamt()));
						} else {
							mainMap.get(keyWord).setFsmallsumamt(
									mainMap.get(keyWord).getFsmallsumamt().add(
											subDto.getFamt()));
						}
						subDto
								.setIvousrlno(Long.parseLong(vouMap
										.get(keyWord)));
					} else {// 就是一个新包,相当于主表的一条明细
						j++;
						TbsTvBnkpayMainDto cremainDto = (TbsTvBnkpayMainDto) mainDto
								.clone();
						// 生成一个新的包流水号,同时生成一个新的主凭证流水号作为主键信息
						mainvou = SequenceGenerator.getNextByDb2(
								SequenceName.HKSQ_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH);
						String tmpPackNo = SequenceGenerator
								.changePackNoForLocal(SequenceGenerator
										.getNextByDb2(
												SequenceName.FILENAME_PACKNO_REF_SEQ,
												SequenceName.TRAID_SEQ_CACHE,
												SequenceName.TRAID_SEQ_STARTWITH,
												MsgConstant.SEQUENCE_MAX_DEF_VALUE));
						vouMap.put(keyWord, mainvou);
						cremainDto.setSpackno(tmpPackNo);
						// 凭证流水号需要在原凭证流水号的基础上增加末尾数字，代表分包，凭证编号重新赋值 需要特殊处理
						String snewvouno;
						if (StateConstant.COMMON_YES
								.equals(ITFECommonConstant.IFAUTOGENORIVOUNO)) {
							snewvouno = mainDto.getSvouno();
						} else {
							snewvouno = mainDto.getSvouno() + j;
							if (snewvouno.length() > 20) {
								snewvouno = snewvouno.substring(snewvouno
										.length() - 20, snewvouno.length());
							}
						}
						cremainDto.setSvouno(snewvouno);
						// 分包后给新的主表信息赋凭证流水号
						cremainDto.setIvousrlno(Long.parseLong(mainvou));
						// 主表中原凭证编赋值
						cremainDto.setSorivouno(strs[0]);
						cremainDto
								.setDorivoudate(CommonUtil.strToDate(strs[1]));
						if (subDto.getCacctprop().equals(
								StateConstant.CONPAY_ZEROBALANCE)) {
							cremainDto.setFzerosumamt(subDto.getFamt());
						} else {
							cremainDto.setFsmallsumamt(subDto.getFamt());
						}
						// 放在MAP中
						mainMap.put(keyWord, cremainDto);
						subDto.setIvousrlno(Long.parseLong(mainvou)); // 凭证流水号
						fatherdtos.add(cremainDto);
					}

					recordNum++;
					sondtos.add(subDto);
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(
					"商行办理支付划款申请退回文件解析出错：\n" + e.getMessage(), e);
		}
		if (famt.compareTo(new BigDecimal("0.00")) == 0) {
			mulitDto.getErrorList().add(
					"商行办理支付划款申请退回文件[" + fi.getName() + "]发生额不能为0");
		}
		if (famt.compareTo(paydto.getFamt()) != 0) {
			mulitDto.getErrorList().add(
					"录入的来账资金总额[" + paydto.getFamt() + "]与导入文件[" + fi.getName()
							+ "]中的总金额[" + famt + "]不一致！");
		}
		if (famt.compareTo(sonFamt) != 0) {
			mulitDto.getErrorList().add(
					"商行办理支付划款申请退回文件[" + fi.getName() + "]中汇总金额[" + famt
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
						"商行办理支付划款申请退回文件[" + fi.getName() + "]中存在凭证编号重复");
			}
		}

		mulitDto.setFatherDtos(fatherdtos);
		mulitDto.setSonDtos(sondtos);

		mulitDto.setVoulist(voulist);

		return mulitDto;
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}
