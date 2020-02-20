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
 * �ɸ����˾�ṩ��ʹ��Usb-Key�ڿͻ��˽��м��ܵ���
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
		// ���ݵ�½��Ϣ�е���Կ����ģʽ�����ܷ�ʽ���ط���ɫģʽ��Ҫ�ض���Ҫ���ܵ��ļ����н���
		String encryptMode = loginfo.getEncryptMode().get(biztype);
		String keyMode = loginfo.getMankeyMode();
		String area = loginfo.getArea();
		// ��ɽ�����ļ���Ҫ���ܣ���֧�ְ���������ά����Կ
		if (!StateConstant.NO_ENCRYPT.equals(encryptMode)) {
			try {
				// ������Կά��ģʽ������Կȥ
				TsMankeyDto keydto = (TsMankeyDto) commonDataAccessService
						.getSecrKeyByOrg(keyMode, loginfo.getSorgcode(), null);
				if (null == keydto) {
					MessageDialog.openMessageDialog(null,
							StateConstant.ENCRYPT_FIAL_INFO_NOKEY);
					return null;
				}
				// ���ݼ��ܷ�ʽ���ò�ͬ�Ľ��ܷ���
				if (StateConstant.DES3_ENCRYPT.equals(encryptMode)) {
					return fileProcInfo(keydto, area, encryptMode, fPath,commonDataAccessService);
				} else {
					return null;
				}
			} catch (ITFEBizException e) {
				throw new ITFEBizException("����ʧ��", e);
			}
		} else {
			return fPath;
		}
	}

	/**
	 * ���ļ����н���,���ߴ���Ĳ������ļ����н���
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
		// ���ܷ�ʽΪ�����ܵģ����߲���ɽ����ʽ���м��ܵģ�ֱ�ӷ����ļ��� ��
		if (StateConstant.NO_ENCRYPT.equals(encryptMode)) {
			return sourceFile;
		} else if (StateConstant.DES3_ENCRYPT.equals(encryptMode)) { // ɽ������MD5ǩ��
																		// ��3DES����
			File tmpfile = new File(sourceFile);
			String tmpfilename = tmpfile.getName();// ȡ�ļ�������
			String tmpfilepath = tmpfile.getParent();// ��ȡ�ļ���·��
			String targetFile = tmpfilepath + "/TMP/" + tmpfilename; // Ŀ���ļ�
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
			throw new ITFEBizException("Ŀǰ�в�֧�ָ��ֽ��ܷ�ʽ");
		}

	}

	/**
	 * ���ܽ�ǩ��MD5����DES
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @param strKey
	 * @param encKey
	 *            ������Կ
	 * @return δ����
	 */
	public static Boolean checkDesAndMd5(String sourceFile, String targetFile,
			TsMankeyDto _dto) {
		try {
			String encKey = _dto.getSencryptkey();
			String strKey = _dto.getSkey();
			// ��ȡ�����ַ���
			String encStr = FileUtil.getInstance().readFile(sourceFile);
			// ���ܵõ����Ľ�����Կ
			String noEncStr = TripleDES.decrypt(encStr, encKey, null);
			// ��֤ǩ��������ԭ�ĺ�MD5ǩ��,int�᲻�����
			int index = noEncStr.lastIndexOf("[[[");
			// ԭ��
			String msg = noEncStr.substring(0, index);
			// ǩ����Ϣ
			String signResult = noEncStr.substring(index + 3,
					noEncStr.length() - 3);
			// ����ǩ����Ϣ����ǩ��
			String Comsign = new String(Hex.encode(MD5Sign.encryptHMAC(msg,
					strKey)));
			// �Ƚ�ǩ��
			if (Comsign.equals(signResult)) {
				FileUtil.getInstance().writeFile(targetFile, msg);
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			log.error("�ļ����ܴ���!", e);
		}
		return Boolean.FALSE;

	}
}
