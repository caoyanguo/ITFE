package com.cfcc.itfe.tipsfileparser;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public abstract class AbstractTipsFileOper implements ITipsFileOper {
	final Log log = LogFactory.getLog(this.getClass());

	/**
	 * TBS文件解析
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String[]> readFile(String file, String split)
			throws FileOperateException {
		return FileUtil.getInstance().readFileWithLine(file, split);

	}

	/**
	 * 批量拨付签名文件解密，解析
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String[]> readSignFile(String file, String split)
			throws FileOperateException {
		try {
			// 存储解密的临时文件
			String tmp = file + ".tmp";
			decryptFile(file, tmp);
			List<String[]> l = FileUtil.getInstance().readFileWithLine(tmp,
					split);
			// 删除临时文件
			FileUtil.getInstance().deleteFile(tmp);
			return l;
		} catch (Exception e) {
			log.error(e);
			throw new FileOperateException("文件解密出现异常", e);
		} finally {
			// 删除临时文件
			try {
				FileUtil.getInstance().deleteFile(file + ".tmp");
			} catch (FileNotFoundException e) {
				log.error(e);
				throw new FileOperateException("文件解密删除临时文件时出现异常", e);
			}
		}
	}

	/**
	 * 批量拨付PAS文件解密，解析
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String[]> readPassFile(String file, String split)
			throws FileOperateException {
		try {
			// 存储解密的临时文件
			String tmp = file + ".tmp";
			decryptPassFile(file, tmp);
			List<String[]> l = FileUtil.getInstance().readFileWithLine(tmp,
					split);
			// 删除临时文件
			FileUtil.getInstance().deleteFile(tmp);
			return l;
		} catch (Exception e) {
			log.error(e);
			throw new FileOperateException("文件解密出现异常", e);
		} finally {
			// 删除临时文件
			try {
				FileUtil.getInstance().deleteFile(file + ".tmp");
			} catch (FileNotFoundException e) {
				log.error(e);
				throw new FileOperateException("文件解密删除临时文件时出现异常", e);
			}
		}
	}

	/**
	 * 获取财政调拨支出预算科目表信息
	 */
	public static Map<String, TsFinmovepaysubDto> getMovepaysub(String orgcode)
	throws ITFEBizException {
		try {
			TsFinmovepaysubDto searchdto = new TsFinmovepaysubDto();
			searchdto.setSorgcode(orgcode);
			List<TsFinmovepaysubDto> list = CommonFacade.getODB().findRsByDto(
					searchdto);
			Map<String, TsFinmovepaysubDto> map = new HashMap<String, TsFinmovepaysubDto>();
			for (TsFinmovepaysubDto tmp : list) {
				map.put(tmp.getSsubjectcode(), tmp);
			}
			return map;
		} catch (Throwable e) {
			throw new ITFEBizException("获取财政调拨支出预算科目表信息失败", e);
		}
	}
	/**
	 * 获得文件校验密钥(山东)
	 * 
	 * @param sorgcode
	 * @param sconnorgcode
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public String findKeyForValidate(String sorgcode, String sconnorgcode)
			throws ITFEBizException, JAFDatabaseException, ValidateException {
		TsInfoconnorgDto keydto = new TsInfoconnorgDto();
		keydto.setSorgcode(StateConstant.INFOCONNORG_DECRYPT_SD);
		keydto.setSconnorgcode(StateConstant.INFOCONNORG_DECRYPT_SD);
		List<IDto> l = CommonFacade.getODB().findRsByDtoWithUR(keydto);
		if (l == null || l.size() == 0) {
			throw new ITFEBizException("文件进行校验码验证时没有找到对应密钥，请查证");
		}
		return ((TsInfoconnorgDto) l.get(0)).getSkey();
	}

	/**
	 * 山东退库和实拨资金校验码
	 * 
	 * @param vouchNo
	 *            凭证号码
	 * @param subCode
	 *            科目代码
	 * @param rcvAccount
	 *            收款账号
	 * @param amt
	 *            金额
	 * @param key
	 *            密钥
	 * @return
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	@SuppressWarnings("unchecked")
	public boolean importValidator(String vouchNo, String subCode,
			String rcvAccount, BigDecimal amt, String waitVerify, String key)
			throws ITFEBizException, JAFDatabaseException, ValidateException {

		String rightKey = TipsFileDecrypt.getMD5(vouchNo, subCode, rcvAccount,
				amt, key);
		if (rightKey.equals(waitVerify)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 文件校验方法
	 */
	public void importValidator() throws Exception {

	}

	/**
	 * 解密文件
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void decryptFile(String srcFile, String dstFile) throws Exception {
		TreasuryEncrypt.Decrypt(srcFile, dstFile, false);
	}

	/**
	 * pass文件解密文件
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void decryptPassFile(String srcFile, String dstFile)
			throws Exception {
		ImplGKEncryptKeyJNI jni = new ImplGKEncryptKeyJNI();

	}

	/**
	 * 提供对所属国库进行验证
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean checkTreasury(String trecode, String orgcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade
				.cacheTreasuryInfo(orgcode);
		if (map.containsKey(trecode)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 校验预算科目 功能--1,经济--2 subcd 需要校验的科目代码 msgtype 所属业务类型(以报文类型) type 功能科目代码 1
	 * 经济科目代码 2
	 * 
	 * @throws ITFEBizException
	 */
	public String verifySubject(Map<String, TsBudgetsubjectDto> smap,
			String subcd, String msgtype, String type, String filename,
			String svouno) throws ITFEBizException {
		String subcode = subcd;
		if (null != subcode && !"".equals(subcode.trim())) {
			subcode = subcd.trim();
		} else {
			return "";
		}
		TsBudgetsubjectDto dto = smap.get(subcode);

		StringBuffer sb = new StringBuffer("");
		if (msgtype.equals(MsgConstant.MSG_NO_7211)) { // 税票收入
		} else if (msgtype.equals(MsgConstant.MSG_NO_1104)) { // 退库
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				sb.append("退库文件[" + filename + "] 凭证编号为[" + svouno
						+ "]的记录中科目代码 " + subcode + " 没有在'预算科目参数'中找到!");
			} else {
				if (!"1".equals(dto.getSsubjectclass())) {
					sb.append("退库文件[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中科目代码 " + subcode + " 不是收入类科目代码\n");
				}
				if (!"1".equals(dto.getSwriteflag())) {
					sb.append("退库文件[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中科目代码 " + subcode + " 的录入标志为不可录入\n");
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_5101)) { // 实拨资金
			String bizflag = filename.substring(filename.length() - 7, filename.length() - 5); // 一般预算还是调拨
			String moveflag ;
			if ("1".equals(type)) { // 功能科目
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中功能科目代码 " + subcode + " 没有在'预算科目参数'中找到!");
				} else {
					moveflag = dto.getSmoveflag(); //调拨标志0：非调拨 1：调拨
					if (!"2".equals(dto.getSsubjectclass())) {
						sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
								+ "]的记录中功能科目代码 " + subcode + " 不是支出功能科目\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
								+ "]的记录中功能科目代码 " + subcode + " 的录入标志为不可录入\n");
					}
					if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizflag)){//一般预算支出
						if("1".equals(moveflag)){
							sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
									+ "]的记录中功能科目代码 " + subcode + " 为调拨科目\n");
						}
					}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizflag)){//调拨支出
						if("0".equals(moveflag)){
							sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
									+ "]的记录中功能科目代码 " + subcode + " 为非调拨科目\n");
						}
					}
				}
			} else if ("2".equals(type) && !"".equals(subcode)
					&& null != subcode) { // 经济科目
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中经济科目代码 " + subcode + " 没有在'预算科目参数'中找到!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
								+ "]的记录中经济科目代码 " + subcode + " 不是支出经济科目\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
								+ "]的记录中经济科目代码 " + subcode + " 的录入标志为不可录入\n");
					}
				}
				moveflag = dto.getSmoveflag(); //调拨标志0：非调拨 1：调拨
				if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizflag)){//一般预算支出
					if("1".equals(moveflag)){
						sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
								+ "]的记录中功能科目代码 " + subcode + " 为调拨科目\n");
					}
				}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizflag)){//调拨支出
					if("0".equals(moveflag)){
						sb.append("实拨资金文件[" + filename + "] 凭证编号为[" + svouno
								+ "]的记录中功能科目代码 " + subcode + " 为非调拨科目\n");
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_5102)) { // 直接支付额度
			if ("1".equals(type)) { // 功能科目
				if (null == subcode || "".equals(subcode)) {
					sb.append("直接支付额度文件[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中功能科目代码为空!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb
								.append("直接支付额度文件[" + filename + "] 凭证编号为["
										+ svouno + "]的记录中功能科目代码 " + subcode
										+ " 没有在'预算科目参数'中找到!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("直接支付额度文件[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 不是支出功能科目\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("直接支付额度文件[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 的录入标志为不可录入\n");
						}
					}
				}
			} else if ("2".equals(type)) { // 经济科目
				if (null == subcode || "".equals(subcode)) {
					sb.append("启用经济代码的情况下,[" + filename + "]中经济科目代码不能为空！");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("直接支付额度文件[" + filename + "]中经济科目代码 "
								+ subcode + " 没有在'预算科目参数'中找到!");
					} else {
						if (!"3".equals(dto.getSsubjectclass())) {
							sb.append("直接支付额度文件[" + filename + "]中经济科目代码 "
									+ subcode + " 不是支出经济科目\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("直接支付额度文件[" + filename + "]中经济科目代码 "
									+ subcode + " 的录入标志为不可录入\n");
						}
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_5103)) { // 授权支付额度
			if ("1".equals(type)) { // 功能科目
				if (null == subcode || "".equals(subcode)) {
					sb.append("授权支付额度文件[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中功能科目代码为空!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb
								.append("授权支付额度文件[" + filename + "] 凭证编号为["
										+ svouno + "]的记录中功能科目代码 " + subcode
										+ " 没有在'预算科目参数'中找到!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("授权支付额度文件[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 不是支出功能科目\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("授权支付额度文件[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 的录入标志为不可录入\n");
						}
					}
				}
			} else if ("2".equals(type)) { // 经济科目
				if (null == subcode || "".equals(subcode)) {
					sb.append("启用经济代码的情况下,[" + filename + "]中经济科目代码不能为空！");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("授权支付额度文件[" + filename + "]中经济科目代码 "
								+ subcode + " 没有在'预算科目参数'中找到!");
					} else {
						if (!"3".equals(dto.getSsubjectclass())) {
							sb.append("授权支付额度文件[" + filename + "]中经济科目代码 "
									+ subcode + " 不是支出经济科目\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("授权支付额度文件[" + filename + "]中经济科目代码 "
									+ subcode + " 的录入标志为不可录入\n");
						}
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_1105)) { // 更正
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				sb.append("更正文件[" + filename + "]中科目代码 " + subcode
						+ " 没有在'预算科目参数'中找到!");
			}

		} else if (msgtype.equals(MsgConstant.MSG_NO_5104)) { // 人行办理直接支付
			if ("1".equals(type)) { // 功能科目
				if (null == subcode || "".equals(subcode)) {
					sb.append("人行办理直接支付文件[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中功能科目代码为空!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("人行办理直接支付文件[" + filename + "] 凭证编号为["
								+ svouno + "]的记录中功能科目代码 " + subcode
								+ " 没有在'预算科目参数'中找到!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("人行办理直接支付文件[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 不是支出功能科目\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("人行办理直接支付文件[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 的录入标志为不可录入\n");
						}
					}
				}
			} else if ("2".equals(type)) { // 经济科目
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("人行办理直接支付文件[" + filename + "]中经济科目代码 " + subcode
							+ " 没有在'预算科目参数'中找到!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("人行办理直接支付文件[" + filename + "]中经济科目代码 "
								+ subcode + " 不是支出经济科目\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("人行办理直接支付文件[" + filename + "]中经济科目代码 "
								+ subcode + " 的录入标志为不可录入\n");
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.APPLYPAY_DAORU)) { // 商行办理支付划款申请
			if ("1".equals(type)) { // 功能科目
				if (null == subcode || "".equals(subcode)) {
					sb.append("商行办理支付划款申请[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中功能科目代码为空!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("商行办理支付划款申请[" + filename + "] 凭证编号为["
								+ svouno + "]的记录中功能科目代码 " + subcode
								+ " 没有在'预算科目参数'中找到!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("商行办理支付划款申请[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 不是支出功能科目\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("商行办理支付划款申请[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 的录入标志为不可录入\n");
						}
					}
				}
			} else if ("2".equals(type)) { // 经济科目
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("商行办理支付划款申请[" + filename + "]中经济科目代码 " + subcode
							+ " 没有在'预算科目参数'中找到!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("商行办理支付划款申请[" + filename + "]中经济科目代码 "
								+ subcode + " 不是支出经济科目\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("商行办理支付划款申请[" + filename + "]中经济科目代码 "
								+ subcode + " 的录入标志为不可录入\n");
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.APPLYPAY_BACK_DAORU)) { // 商行办理支付划款申请退回
			if ("1".equals(type)) { // 功能科目
				if (null == subcode || "".equals(subcode)) {
					sb.append("商行办理支付划款申请退回[" + filename + "] 凭证编号为[" + svouno
							+ "]的记录中功能科目代码为空!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("商行办理支付划款申请退回[" + filename + "] 凭证编号为["
								+ svouno + "]的记录中功能科目代码 " + subcode
								+ " 没有在'预算科目参数'中找到!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("商行办理支付划款申请退回[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 不是支出功能科目\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("商行办理支付划款申请退回[" + filename + "] 凭证编号为["
									+ svouno + "]的记录中功能科目代码 " + subcode
									+ " 的录入标志为不可录入\n");
						}
					}
				}
			} else if ("2".equals(type)) { // 经济科目
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("商行办理支付划款申请退回[" + filename + "]中经济科目代码 "
							+ subcode + " 没有在'预算科目参数'中找到!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("商行办理支付划款申请退回[" + filename + "]中经济科目代码 "
								+ subcode + " 不是支出经济科目\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("商行办理支付划款申请退回[" + filename + "]中经济科目代码 "
								+ subcode + " 的录入标志为不可录入\n");
					}
				}
			}
		}
		if (sb != null && !"".equals(sb.toString())) {
			return sb.toString();
		}
		return "";
	}

	/**
	 * 进行法人代码校验
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public Map<String, TdCorpDto> verifyCorpcode(String sorgcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		return SrvCacheFacade.cacheTdCorpInfo(sorgcode);
	}

	/**
	 * 得到支付行号缓存Map
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsPaybankDto> makeBankMap()
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		return SrvCacheFacade.cachePayBankInfo();
	}

	/**
	 * 得到财政代码缓存Map
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertfinorgDto> makeFincMap(
			String sbookorgcode) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		return SrvCacheFacade.cacheFincInfo(sbookorgcode);

	}

	/**
	 * 得到征收机关缓存Map
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConverttaxorgDto> makeTaxMap(
			String sbookorgcode) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		return SrvCacheFacade.cacheTaxInfo(sbookorgcode);

	}

	/**
	 * 按照国库来校验文件名重复
	 * 
	 * @param orgcode
	 * @param trecode
	 * @param filename
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	public String checkFileExsit(String orgcode, String trecode,
			String filename, String type) throws JAFDatabaseException,
			ValidateException {
		TvFilepackagerefDto dto = new TvFilepackagerefDto();
		dto.setSorgcode(orgcode);
		dto.setStrecode(trecode);
		dto.setSfilename(filename);
		StringBuffer exceptionInfo = new StringBuffer("");
		List list = CommonFacade.getODB().findRsByDtoWithUR(dto);
		if (list != null && list.size() > 0) {
			if (MsgConstant.MSG_NO_5102.equals(type)) {
				exceptionInfo.append("直接支付额度文件[" + filename + "]中根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.MSG_NO_5103.equals(type)) {
				exceptionInfo.append("授权支付额度文件[" + filename + "]中 根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.MSG_NO_5104.equals(type)) {
				exceptionInfo.append("人行办理直接支付文件[" + filename + "]中 根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.MSG_NO_5101.equals(type)) {
				exceptionInfo.append("实拨资金文件[" + filename + "]中 根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.MSG_NO_1104.equals(type)) {
				exceptionInfo.append("退库文件[" + filename + "]中 根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.MSG_NO_1105.equals(type)) {
				exceptionInfo.append("更正文件[" + filename + "]中 根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.MSG_NO_1106.equals(type)) {
				exceptionInfo.append("免抵调文件[" + filename + "]中 根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.APPLYPAY_DAORU.equals(type)) {
				exceptionInfo.append("商行办理支付划款申请文件[" + filename + "]中 根据国库代码["
						+ trecode + "] + 文件名[" + filename + "] 校验为重复导入!");
			} else if (MsgConstant.APPLYPAY_BACK_DAORU.equals(type)) {
				exceptionInfo.append("商行办理支付划款申请退回文件[" + filename
						+ "]中 根据国库代码[" + trecode + "] + 文件名[" + filename
						+ "] 校验为重复导入!");
			}
		}
		return exceptionInfo.toString();
	}

	/**
	 * 取得支付系统行号
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public static HashMap<String, TsPaybankDto> getBankMap()
			throws JAFDatabaseException, ValidateException {
		HashMap<String, TsPaybankDto> bankmap = new HashMap<String, TsPaybankDto>();
		TsPaybankDto bank = new TsPaybankDto();
		String sql = "select * from ts_paybank where s_orgcode=?";
		bank.setSorgcode(StateConstant.ORG_CENTER_CODE);
		SQLExecutor sqlExec = null;
		sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		sqlExec.addParam(StateConstant.ORG_CENTER_CODE);
		sqlExec.setMaxRows(150000);
		SQLResults payRs = sqlExec.runQueryCloseCon(sql, TsPaybankDto.class);
		List banklist = (List) payRs.getDtoCollection();
		for (Object obj : banklist) {
			TsPaybankDto bankdto = (TsPaybankDto) obj;
			bankmap.put(bankdto.getSbankno(), bankdto);
		}
		return bankmap;
	}

	/**
	 * 取得付款人账户信息
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public static HashMap<String, TsInfoconnorgaccDto> getBookAcctMap(
			String bookorgCode) throws JAFDatabaseException, ValidateException {
		HashMap<String, TsInfoconnorgaccDto> acctmap = new HashMap<String, TsInfoconnorgaccDto>();
		// 通过国库代码和付款人账号 找到对应付款人名称
		TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
		accdto.setSorgcode(bookorgCode);
		List<TsInfoconnorgaccDto> itoList = CommonFacade.getODB().findRsByDto(
				accdto);
		if (null != itoList && itoList.size() > 0) {
			for (TsInfoconnorgaccDto obj : itoList) {
				acctmap.put(obj.getStrecode() + obj.getSpayeraccount(), obj);
			}
		}
		return acctmap;
	}
	
	/**
	 * 取得行名行号信息
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public static HashMap<String, String> getBankInfo(String bookorgCode) throws JAFDatabaseException, ValidateException {
		HashMap<String, String> acctmap = new HashMap<String, String>();
		TsConvertbanknameDto convertbanknamedto = new TsConvertbanknameDto();
		convertbanknamedto.setSorgcode(bookorgCode);
		List<TsConvertbanknameDto> itoList = CommonFacade.getODB().findRsByDto(
				convertbanknamedto);
		if (null != itoList && itoList.size() > 0) {
			for (TsConvertbanknameDto obj : itoList) {
				acctmap.put(obj.getSbankname(), obj.getSbankcode());
			}
		}
		return acctmap;
	}

	/**
	 * 从缓存中获取所有付款人账户信息
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsInfoconnorgaccDto> getFinTreAcctInfo()
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		return SrvCacheFacade.cacheFinTreAcctInfo();
	}
	
	/**
	 * 从缓存中获取所有付款人账户信息会计账户表中
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TdBookacctMainDto> getFinTreAcctFromBookAcctInfo()
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		return SrvCacheFacade.cacheFinTreAcctFromBookAcctInfo();
	}

	/**
	 * 从缓存中获取核算主体信息
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsOrganDto> getOrganInfo()
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		return SrvCacheFacade.cacheOrgInfo();
	}

}
