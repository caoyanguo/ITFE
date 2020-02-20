package com.cfcc.itfe.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.util.FileOprUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.itfe.util.encrypt.Hex;
import com.cfcc.itfe.util.encrypt.MD5Sign;
import com.cfcc.itfe.util.encrypt.TripleDES;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;
import com.cfcc.jaf.rcp.mvc.ICommand;

public class TipsFileDecrypt {
	private static Log log = LogFactory.getLog(TipsFileDecrypt.class);

	/**
	 * 批量拨付解签名为TXT格式的签名文件，山东最初用的为MD5签名的文件没有加密，只需要验证签名即可。
	 * 
	 * @param srcFile
	 * @param dstFile
	 * @param srckey
	 * @return 0 success -1 fail
	 * @throws Exception
	 */
	public static String checkSignFile(String srcFile, String dstFile,
			String srckey) throws Exception {
		FileOprUtil util = new FileOprUtil();
		Md5App md5XOr = new Md5App(true, srckey);// 需要带密钥和异或功能
		List<String> list = util.readFileWithLine(srcFile);
		StringBuffer filebuf = new StringBuffer("");
		String fileSign = "";
		StringBuffer strCA = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if ("<CA>".equals(list.get(i).toUpperCase())) {
				fileSign = list.get(i + 1);
				break;
			} else {
				// 对每一行进行带异或方式的MD5加签
				String linestr = list.get(i);
				String linestrmd5 = md5XOr.makeMd5(linestr);
				strCA.append(linestrmd5);
				filebuf.append(linestr + "\r\n");
			}
		}
		// 不需要带密钥和异或的MD5加签
		Md5App md5 = new Md5App();
		String checkSign = md5.makeMd5(strCA.toString());
		if (!fileSign.equals(checkSign)) {
			return "-1";
		}
		util.deleteFile(dstFile);
		util.writeFile(dstFile, filebuf.toString());
		return fileSign;

	}

	/**
	 * 批量拨付pass文件解密文件,适用于批量拨付的加密的pass文件
	 * 
	 * @param passFile
	 * @param txtFile
	 * @param key
	 * @return 0 success -1 fail
	 * @throws Exception
	 */
	public static String decryptPassFile(String passFile, String txtFile,
			String srckey) throws Exception {
		ImplGKEncryptKeyJNI jni = new ImplGKEncryptKeyJNI();
		long ret = jni.gkUnEncryptKey(passFile, txtFile, srckey);
		if (ret == 0) { // 解密成功调用 验证签名方法
			return checkSignFile(txtFile, txtFile, srckey);
		} else { // 解密失败
			return "-1";
		}
	}

	/**
	 * 计算效验码
	 * 
	 * @param filename
	 * @param CAByte
	 * @return 校验码
	 */
	public static int calculate_XYM(String filename, int CAByte) {
		File file = new File(filename);
		int asc = 0; // asc码
		int cal = 0; // 2^n
		int wasc = 0; // 权
		int xym = 0; // 验证码
		try {
			// 读文件
			FileInputStream fin = new FileInputStream(file);
			// 文件长度
			int size = fin.available();
			byte[] temp = new byte[size]; // 字节数组

			if (fin.read(temp) != size) {
			}
			String s2 = ""; // 二进制
			String s10 = ""; // 十进制

			for (int i = 0; i < temp.length - CAByte; i++) {
				char cchar = (char) temp[i];
				asc = (int) cchar;
				if (asc > 255) {
					s2 = Integer.toBinaryString(asc); // 二进制
					s10 = Integer.valueOf(s2.substring(s2.length() - 8), 2)
							.toString(); // 单字节10进制
					asc = Integer.parseInt(s10); // 单字节二进制asc码
				}
				cal = (i + 1) % 11;
				wasc = (int) Math.pow(2, cal); // 算加权
				xym = (xym + asc * wasc); // 校验码计算
			}
			xym = xym % 1000000; // 校验码计算
			// 校验码< 100000时，xym = xym + 100000
			if (xym < 100000) {
				xym = xym + 100000;
			}
		} catch (IOException e) {
			log.error(e);
			return 0;
		}
		return xym;
	}

	/**
	 * 验证文件是否正确
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean verifyCA(String fileName) {
		boolean isCorrect = false;
		File file = new File(fileName);
		// 读文件
		FileInputStream fin;
		try {
			fin = new FileInputStream(file);
			// 文件长度
			int size;
			size = fin.available();
			byte[] temp = new byte[size]; // 字节数组

			if (fin.read(temp) != size) {
				log.error("读不了文件");
			}
			// 计算验证码
			int xym = calculate_XYM(fileName, 8);
			// 取文件验证码
			String CAValue = "";
			for (int i = size - 8; i < size - 2; i++) {
				char ctemp = (char) temp[i];
				CAValue = CAValue + (char) temp[i];
			}
			log.debug(CAValue);

			// 文件验证码和计算出的验证码如果相等返回TRUE
			if (Integer.parseInt(CAValue) == xym) {
				isCorrect = true;
				log.debug("验证正确");
			}
		} catch (FileNotFoundException e) {
			log.error("验证签名出错!", e);
		} catch (IOException e) {
			log.error("验证签名出错!", e);
		}
		return isCorrect;
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
	 */
	public static String getMD5(String vouchNo, String subCode,
			String rcvAccount, BigDecimal amt, String key) {
		MD5ForSD md5 = new MD5ForSD();
		StringBuffer buffer = new StringBuffer();
		String value = buffer.append(vouchNo).append(subCode)
				.append(rcvAccount).toString();
		long damt = Math.round(amt.doubleValue() * 100);
		return md5.getMD5ofStr(value + damt + key);
	}

	/**
	 * 解密解签名MD5——DES
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @param strKey
	 * @param encKey
	 *            加密密钥
	 * @return 未测试
	 */
	public static Boolean checkDesAndMd5(String sourceFile, String targetFile,
			TsMankeyDto _dto) {
		try {
			String encKey = _dto.getSencryptkey();
			String strKey = _dto.getSkey();
			// 读取加密字符串
			String encStr = FileUtil.getInstance().readFile(sourceFile);
			// 解密得到明文解密密钥
			String noEncStr = TripleDES.decrypt(encStr, encKey, null);
			// 验证签名，分离原文和MD5签名,int会不会溢出
			int index = noEncStr.lastIndexOf("[[[");
			// 原文
			String msg = noEncStr.substring(0, index);
			// 签名信息
			String signResult = noEncStr.substring(index + 3,
					noEncStr.length() - 3);
			// 调用签名信息计算签名
			String Comsign = new String(Hex.encode(MD5Sign.encryptHMAC(msg,
					strKey)));
			// 比较签名
			if (Comsign.equals(signResult)) {
				FileUtil.getInstance().writeFile(targetFile, msg);
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			log.error("文件解密错误!", e);
		}
		return Boolean.FALSE;

	}

	/**
	 * 验证SM3签名
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @param strKey
	 * @param encKey
	 *            加密密钥
	 * @return 未测试
	 */
	public static Boolean checkSM3Sign(String sourceFile, String targetFile,
			TsMankeyDto _dto) {
		try {
			String encKey = _dto.getSencryptkey();
			String strKey = _dto.getSkey();
			// 读取加签文件符串
			String signStr = FileUtil.getInstance().readFile(sourceFile);
			// 验证签名，分离原文和MD5签名,int会不会溢出
			int index = signStr.lastIndexOf("<CA>");
			// 原文
			String msg = signStr.substring(0, index);
			// 签名信息
			String signInfo = signStr.substring(signStr.indexOf("<CA>") + 4,
					signStr.indexOf("</CA>"));
			// 调用签名信息计算签名
			String Comsign = SM3Process.calculateSign(msg, strKey);
			// 比较签名
			if (Comsign.equals(signInfo)) {
				FileUtil.getInstance().writeFile(targetFile, msg);
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			log.error("文件解密错误!", e);
		}
		return Boolean.FALSE;

	}

	/**
	 * 根据设置的密钥模式获取对应的密钥，此处不考虑文件是否加密选项
	 * 
	 * @param sorgcode
	 * @param sconnorgcode
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static TsMankeyDto findKeyByKeyMode(String keyMode, String sorgcode,
			String strecode) throws JAFDatabaseException, ValidateException,
			ITFEBizException {
		TsMankeyDto _dto = new TsMankeyDto();
		if (StateConstant.KEY_ALL.equals(keyMode)) {
			_dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
		} else if (StateConstant.KEY_BOOK.equals(keyMode)) {
			_dto.setSorgcode(sorgcode);
		}
		List<TsMankeyDto> l = CommonFacade.getODB().findRsByDtoWithUR(_dto);
		if (l == null || l.size() == 0) {
			return null;
		} else {
			TsMankeyDto tmpdto = l.get(0);
			
			// 按国库代码设置密钥
			if (StateConstant.KEY_TRECODE.equals(tmpdto.getSkeymode())) {
				for (TsMankeyDto dto : l) {
					if (strecode.equals(dto.getSkeyorgcode())) {
						return dto;
					}
				}
				// 按出票单位设置密钥分别是财政代码，代理银行、征收机关
			} else if (StateConstant.KEY_BILLORG.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_GENBANK.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_TAXORG.equals(tmpdto.getSkeymode())) {
				// 根据国库代码获取财政机构代码
				HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade
						.cacheFincInfo(sorgcode);
				for (TsMankeyDto dto : l) {
					// 说明传过来的是国库代码，需要转换成财政代码进行查找,只有实拨资金传入国库代码
					if (mapFincInfo.containsKey(strecode)) {
						if (mapFincInfo.get(strecode).getSfinorgcode().equals(
								dto.getSkeyorgcode())) {
							return dto;
						}
						// 如果传过来的是不是国库代码，说明是征收机关或代理银行,直接进行比较
					} else {
						if (strecode.equals(dto.getSkeyorgcode())) {
							return dto;
						}
					}
				}

			} else {// 按核算主体和其他进行设置
				return tmpdto;
			}
		}
		return null;

	}
	/**
	 * 根据设置的密钥模式获取对应的密钥，此处不考虑文件是否加密选项
	 * 
	 * @param sorgcode
	 * @param sconnorgcode
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static TsMankeyDto newFindKeyByKeyMode(String keyMode, String sorgcode,
			String strecode) throws JAFDatabaseException, ValidateException,
			ITFEBizException {
		TsMankeyDto _dto = new TsMankeyDto();
		if (StateConstant.KEY_ALL.equals(keyMode)) {
			_dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
		} else if (StateConstant.KEY_BOOK.equals(keyMode)) {
			_dto.setSorgcode(sorgcode);
		}
		List<TsMankeyDto> l = CommonFacade.getODB().findRsByDtoWithUR(_dto);
		if (l == null || l.size() == 0) {
			return null;
		} else {
			TsMankeyDto tmpdto = l.get(0);
			
			// 按国库代码设置密钥
			if (StateConstant.KEY_TRECODE.equals(tmpdto.getSkeymode())) {
				for (TsMankeyDto dto : l) {
					if (strecode.equals(dto.getSkeyorgcode())) {
						return dto;
					}
				}
				// 按出票单位设置密钥分别是财政代码，代理银行、征收机关
			} else if (StateConstant.KEY_BILLORG.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_GENBANK.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_TAXORG.equals(tmpdto.getSkeymode())) {
				// 根据国库代码获取财政机构代码
				HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade.cacheFincInfo(sorgcode);
				for (TsMankeyDto dto : l) {
					// 说明传过来的是国库代码，需要转换成财政代码进行查找,只有实拨资金传入国库代码
					if (mapFincInfo.containsKey(strecode)) {
						if (mapFincInfo.get(strecode).getSfinorgcode().equals(
								dto.getSkeyorgcode())) {
							return dto;
						}
						// 如果传过来的是不是国库代码，说明是征收机关或代理银行,直接进行比较
					} else {
						if (strecode!=null&&strecode.equals(dto.getSkeyorgcode())) {
							return dto;
						}
					}
				}

			} else {// 按核算主体和其他进行设置
				return tmpdto;
			}
		}
		return null;

	}
	/**
	 * 根据设置的密钥模式获取对应的密钥，此处不考虑文件是否加密选项
	 * 
	 * @param sorgcode
	 * @param sconnorgcode
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static TsMankeyDto findKeyByKeyMode(String keyMode, String sorgcode,
			String strecode,String keyControl) throws JAFDatabaseException, ValidateException,
			ITFEBizException {
		TsMankeyDto _dto = new TsMankeyDto();
		if (StateConstant.KEY_ALL.equals(keyMode)) {
			_dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
		} else if (StateConstant.KEY_BOOK.equals(keyMode)) {
			_dto.setSorgcode(sorgcode);
		}
		List<TsMankeyDto> l = CommonFacade.getODB().findRsByDtoWithUR(_dto);
		if (l == null || l.size() == 0) {
			return null;
		} else {
			TsMankeyDto tmpdto = l.get(0);
			
			// 按国库代码设置密钥
			if (StateConstant.KEY_TRECODE.equals(tmpdto.getSkeymode())) {
				for (TsMankeyDto dto : l) {
					if (strecode.equals(dto.getSkeyorgcode())) {
						return dto;
					}
				}
				// 按出票单位设置密钥分别是财政代码，代理银行、征收机关
			} else if (StateConstant.KEY_BILLORG.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_GENBANK.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_TAXORG.equals(tmpdto.getSkeymode())) {
				// 根据国库代码获取财政机构代码
				HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade
						.cacheFincInfo(sorgcode);
				for (TsMankeyDto dto : l) {
					// 说明传过来的是国库代码，需要转换成财政代码进行查找,只有实拨资金传入国库代码
					if (mapFincInfo.containsKey(strecode)) {
						if (mapFincInfo.get(strecode).getSfinorgcode().equals(
								dto.getSkeyorgcode())) {
							return dto;
						}
						// 如果传过来的是不是国库代码，说明是征收机关或代理银行,直接进行比较
					} else {
						if (strecode.equals(dto.getSkeyorgcode())) {
							return dto;
						}
					}
				}

			} else {// 按核算主体和其他进行设置
				return tmpdto;
			}
		}
		return null;

	}
	public static void main(String[] args) {
		// ContextFactory.setContextFile("/config/ContextLoader_01.xml");
		// String fileName = "E:\\20120209cz434100000q020.txt";
		// Boolean b = verifyCA(fileName);
		// checkDesAndMd5("D:/1.txt", "D:/2.txt",
		// "123456789012345678901234567890123456789012345678");

		// ImplGKEncryptKeyJNI jni = new ImplGKEncryptKeyJNI();
		// String decFileName =
		// "D:\\011306006007200805160000100001040300000000.pas";
		// String key = "aaaaaaaaaaaaaaaa";
		// String srcFilename1 =
		// "D:\\011306006007200805160000100001040300000000.txt";
		// TipsFileDecrypt tips = new TipsFileDecrypt();
		// try {
		// tips.decryptPassFile(decFileName, srcFilename1, key);
		// long value = jni.gkUnEncryptKey(decFileName, srcFilename1, key);
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// String srcfile =
		// "D:\\011306006007200805160000100001040300000000.txt";
		// TipsFileDecrypt tips = new TipsFileDecrypt();
		// try {
		// tips.checkSignFile(srcfile, srcfile, key);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
