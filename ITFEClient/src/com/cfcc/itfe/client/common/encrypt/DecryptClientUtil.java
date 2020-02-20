/**
 * 
 */
package com.cfcc.itfe.client.common.encrypt;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.encrypt.Hex;
import com.cfcc.itfe.util.encrypt.MD5Sign;
import com.cfcc.itfe.util.encrypt.TripleDES;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * 由格尔公司提供，使用Usb-Key在客户端进行加密的类
 * 
 * @author sjz
 * 
 */
public class DecryptClientUtil {
	private static Log log = LogFactory.getLog(DecryptClientUtil.class);

	public static String commonDecrypt(ITFELoginInfo loginfo,
			ICommonDataAccessService commonDataAccessService, String fPath,
			String biztype) throws ITFEBizException, FileOperateException,
			FileNotFoundException {
		// 根据登陆信息中的密钥设置模式，加密方式、地方特色模式等要素对需要解密的文件进行解密
		String encryptMode = loginfo.getEncryptMode().get(biztype);
		String keyMode = loginfo.getMankeyMode();
		String area = loginfo.getArea();
		// 仅山西的文件需要解密，仅支持按核算主体维护密钥
		if (!StateConstant.NO_ENCRYPT.equals(encryptMode)) {
			try {
				// 根据密钥维护模式设置密钥去
				TsMankeyDto keydto = (TsMankeyDto) commonDataAccessService
						.getSecrKeyByOrg(keyMode, loginfo.getSorgcode(), null);
				if (null == keydto) {
					MessageDialog.openMessageDialog(null,
							StateConstant.ENCRYPT_FIAL_INFO_NOKEY);
					return null;
				}
				// 根据加密方式调用不同的解密方法
				if (StateConstant.DES3_ENCRYPT.equals(encryptMode)) {
					return fileProcInfo(keydto, area, encryptMode, fPath,commonDataAccessService);
				} else {
					return null;
				}
			} catch (ITFEBizException e) {
				throw new ITFEBizException("解密失败", e);
			}
		} else {
			return fPath;
		}
	}

	/**
	 * 对文件进行解密,更具传入的参数对文件进行解密
	 * 
	 * @param KeyMode
	 * @param area
	 * @param encryptMode
	 * @param filename
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 * @throws FileNotFoundException
	 * @throws FileOperateException
	 */
	public static String fileProcInfo(TsMankeyDto manKeyDto, String area,
			String encryptMode, String sourceFile,ICommonDataAccessService commonDataAccessService) throws ITFEBizException,
			FileOperateException, FileNotFoundException {
		// 加密方式为不加密的，或者采用山东方式逐行加密的，直接返回文件名 ：
		if (StateConstant.NO_ENCRYPT.equals(encryptMode)) {
			return sourceFile;
		} else if (StateConstant.DES3_ENCRYPT.equals(encryptMode)) { // 山西的先MD5签名
																		// 在3DES加密
			File tmpfile = new File(sourceFile);
			String tmpfilename = tmpfile.getName();// 取文件的名字
			String tmpfilepath = tmpfile.getParent();// 获取文件的路径
			String targetFile = tmpfilepath + "/TMP/" + tmpfilename; // 目标文件
			File tmpfile1 = new File(targetFile);
			if (tmpfile1.exists()) {
				FileUtil.getInstance().deleteFile(targetFile);
			}
			String encStr = commonDataAccessService.decryptForIncome(FileUtil.getInstance().readFile(sourceFile), "", manKeyDto);
			
			if (!"".equals(encStr)) {
				FileUtil.getInstance().writeFile(targetFile, encStr);
				return targetFile;
			} else {
				return StateConstant.ENCRYPT_FAIL_INFO;
			}

		} else {
			throw new ITFEBizException("目前尚不支持该种解密方式");
		}

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
}
