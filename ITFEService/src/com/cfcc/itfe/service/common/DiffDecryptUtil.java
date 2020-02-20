/**
 * 
 */
package com.cfcc.itfe.service.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.security.TipsFileDecryptUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.interfaces.ILoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 服务器端调用解密方法
 * 
 * @author cyg
 * 
 */
public class DiffDecryptUtil {

	public static String commonDecrypt(ITFELoginInfo loginfo, String fPath,
			String biztype) throws ITFEBizException, JAFDatabaseException,
			ValidateException, FileOperateException, FileNotFoundException {
		// 根据登陆信息中的密钥设置模式，加密方式、地方特色模式等要素对需要解密的文件进行解密
		String encryptMode = loginfo.getEncryptMode().get(biztype);
		String keyMode = loginfo.getMankeyMode();
		String area = loginfo.getArea();
		String sorgcode = loginfo.getSorgcode();
		if(!ITFECommonConstant.SRC_NODE.equals("000035100009")&&(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(biztype)||BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(biztype)))
			return newCommonDecrypt(loginfo,fPath,biztype);//直接支付额度、授权支付额度采用新接口sm3验签
		try {
			// 根据加密方式调用不同的解密方法
			if (StateConstant.DES3_ENCRYPT.equals(encryptMode)) {
				// 根据密钥维护模式设置密钥去，// 仅山西的文件需要解密，仅支持按核算主体维护密钥
				TsMankeyDto keydto = (TsMankeyDto) TipsFileDecrypt
						.findKeyByKeyMode(keyMode, sorgcode, null);
				if (null == keydto) {
					return StateConstant.ENCRYPT_FIAL_INFO_NOKEY;
				}
				return TipsFileDecryptUtil.fileProcInfo(keydto, area,
						encryptMode, fPath);
			} else {// 其他签名模式在解析时进行判断，不用解密直接返回文件名
				return fPath;
			}
		} catch (ITFEBizException e) {
			throw new ITFEBizException("解密失败", e);
		}
	}
	public static String newCommonDecrypt(ITFELoginInfo loginfo, String fPath,
			String biztype) throws ITFEBizException, JAFDatabaseException,
			ValidateException, FileOperateException, FileNotFoundException {
		String encryptMode = loginfo.getEncryptMode().get(biztype);//配置文件读取加密方式
		String keyMode = loginfo.getMankeyMode();//系统维护模式方式按全省统一或按核算主体
		String area = loginfo.getArea();
		String sorgcode = loginfo.getSorgcode();
		try {
			// 根据加密方式调用不同的解密方法
			if (StateConstant.SM3_ENCRYPT.equals(encryptMode)) {// 根据密钥维护模式设置密钥去，// 仅山西的文件需要解密，仅支持按核算主体维护密钥
				List<String[]> fileContent = FileUtil.getInstance().readFileWithLine(fPath, ",");
				String treCode = null;
				if(fileContent!=null&&fileContent.size()>0&&(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(biztype)||BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(biztype)))
				{
					String[] strs = fileContent.get(0);
					treCode = strs[0];
				}
				TsMankeyDto keydto = (TsMankeyDto) TipsFileDecrypt.newFindKeyByKeyMode(keyMode, sorgcode, treCode);
				if (null == keydto) {
					throw new ITFEBizException("没有找到该机构的密钥维护TsMankeyDto-sorgcode="+sorgcode);
				}
				return TipsFileDecryptUtil.newFileProcInfo(keydto, area,encryptMode, fPath);
			} else {// 其他签名模式在解析时进行判断，不用解密直接返回文件名
				return fPath;
			}
		} catch (ITFEBizException e) {
			throw new ITFEBizException("解密失败", e);
		}
	}
}
