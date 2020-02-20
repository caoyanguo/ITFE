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
 * �������˵��ý��ܷ���
 * 
 * @author cyg
 * 
 */
public class DiffDecryptUtil {

	public static String commonDecrypt(ITFELoginInfo loginfo, String fPath,
			String biztype) throws ITFEBizException, JAFDatabaseException,
			ValidateException, FileOperateException, FileNotFoundException {
		// ���ݵ�½��Ϣ�е���Կ����ģʽ�����ܷ�ʽ���ط���ɫģʽ��Ҫ�ض���Ҫ���ܵ��ļ����н���
		String encryptMode = loginfo.getEncryptMode().get(biztype);
		String keyMode = loginfo.getMankeyMode();
		String area = loginfo.getArea();
		String sorgcode = loginfo.getSorgcode();
		if(!ITFECommonConstant.SRC_NODE.equals("000035100009")&&(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(biztype)||BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(biztype)))
			return newCommonDecrypt(loginfo,fPath,biztype);//ֱ��֧����ȡ���Ȩ֧����Ȳ����½ӿ�sm3��ǩ
		try {
			// ���ݼ��ܷ�ʽ���ò�ͬ�Ľ��ܷ���
			if (StateConstant.DES3_ENCRYPT.equals(encryptMode)) {
				// ������Կά��ģʽ������Կȥ��// ��ɽ�����ļ���Ҫ���ܣ���֧�ְ���������ά����Կ
				TsMankeyDto keydto = (TsMankeyDto) TipsFileDecrypt
						.findKeyByKeyMode(keyMode, sorgcode, null);
				if (null == keydto) {
					return StateConstant.ENCRYPT_FIAL_INFO_NOKEY;
				}
				return TipsFileDecryptUtil.fileProcInfo(keydto, area,
						encryptMode, fPath);
			} else {// ����ǩ��ģʽ�ڽ���ʱ�����жϣ����ý���ֱ�ӷ����ļ���
				return fPath;
			}
		} catch (ITFEBizException e) {
			throw new ITFEBizException("����ʧ��", e);
		}
	}
	public static String newCommonDecrypt(ITFELoginInfo loginfo, String fPath,
			String biztype) throws ITFEBizException, JAFDatabaseException,
			ValidateException, FileOperateException, FileNotFoundException {
		String encryptMode = loginfo.getEncryptMode().get(biztype);//�����ļ���ȡ���ܷ�ʽ
		String keyMode = loginfo.getMankeyMode();//ϵͳά��ģʽ��ʽ��ȫʡͳһ�򰴺�������
		String area = loginfo.getArea();
		String sorgcode = loginfo.getSorgcode();
		try {
			// ���ݼ��ܷ�ʽ���ò�ͬ�Ľ��ܷ���
			if (StateConstant.SM3_ENCRYPT.equals(encryptMode)) {// ������Կά��ģʽ������Կȥ��// ��ɽ�����ļ���Ҫ���ܣ���֧�ְ���������ά����Կ
				List<String[]> fileContent = FileUtil.getInstance().readFileWithLine(fPath, ",");
				String treCode = null;
				if(fileContent!=null&&fileContent.size()>0&&(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(biztype)||BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(biztype)))
				{
					String[] strs = fileContent.get(0);
					treCode = strs[0];
				}
				TsMankeyDto keydto = (TsMankeyDto) TipsFileDecrypt.newFindKeyByKeyMode(keyMode, sorgcode, treCode);
				if (null == keydto) {
					throw new ITFEBizException("û���ҵ��û�������Կά��TsMankeyDto-sorgcode="+sorgcode);
				}
				return TipsFileDecryptUtil.newFileProcInfo(keydto, area,encryptMode, fPath);
			} else {// ����ǩ��ģʽ�ڽ���ʱ�����жϣ����ý���ֱ�ӷ����ļ���
				return fPath;
			}
		} catch (ITFEBizException e) {
			throw new ITFEBizException("����ʧ��", e);
		}
	}
}
