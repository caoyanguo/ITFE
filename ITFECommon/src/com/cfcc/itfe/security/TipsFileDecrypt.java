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
	 * ����������ǩ��ΪTXT��ʽ��ǩ���ļ���ɽ������õ�ΪMD5ǩ�����ļ�û�м��ܣ�ֻ��Ҫ��֤ǩ�����ɡ�
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
		Md5App md5XOr = new Md5App(true, srckey);// ��Ҫ����Կ�������
		List<String> list = util.readFileWithLine(srcFile);
		StringBuffer filebuf = new StringBuffer("");
		String fileSign = "";
		StringBuffer strCA = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if ("<CA>".equals(list.get(i).toUpperCase())) {
				fileSign = list.get(i + 1);
				break;
			} else {
				// ��ÿһ�н��д����ʽ��MD5��ǩ
				String linestr = list.get(i);
				String linestrmd5 = md5XOr.makeMd5(linestr);
				strCA.append(linestrmd5);
				filebuf.append(linestr + "\r\n");
			}
		}
		// ����Ҫ����Կ������MD5��ǩ
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
	 * ��������pass�ļ������ļ�,���������������ļ��ܵ�pass�ļ�
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
		if (ret == 0) { // ���ܳɹ����� ��֤ǩ������
			return checkSignFile(txtFile, txtFile, srckey);
		} else { // ����ʧ��
			return "-1";
		}
	}

	/**
	 * ����Ч����
	 * 
	 * @param filename
	 * @param CAByte
	 * @return У����
	 */
	public static int calculate_XYM(String filename, int CAByte) {
		File file = new File(filename);
		int asc = 0; // asc��
		int cal = 0; // 2^n
		int wasc = 0; // Ȩ
		int xym = 0; // ��֤��
		try {
			// ���ļ�
			FileInputStream fin = new FileInputStream(file);
			// �ļ�����
			int size = fin.available();
			byte[] temp = new byte[size]; // �ֽ�����

			if (fin.read(temp) != size) {
			}
			String s2 = ""; // ������
			String s10 = ""; // ʮ����

			for (int i = 0; i < temp.length - CAByte; i++) {
				char cchar = (char) temp[i];
				asc = (int) cchar;
				if (asc > 255) {
					s2 = Integer.toBinaryString(asc); // ������
					s10 = Integer.valueOf(s2.substring(s2.length() - 8), 2)
							.toString(); // ���ֽ�10����
					asc = Integer.parseInt(s10); // ���ֽڶ�����asc��
				}
				cal = (i + 1) % 11;
				wasc = (int) Math.pow(2, cal); // ���Ȩ
				xym = (xym + asc * wasc); // У�������
			}
			xym = xym % 1000000; // У�������
			// У����< 100000ʱ��xym = xym + 100000
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
	 * ��֤�ļ��Ƿ���ȷ
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean verifyCA(String fileName) {
		boolean isCorrect = false;
		File file = new File(fileName);
		// ���ļ�
		FileInputStream fin;
		try {
			fin = new FileInputStream(file);
			// �ļ�����
			int size;
			size = fin.available();
			byte[] temp = new byte[size]; // �ֽ�����

			if (fin.read(temp) != size) {
				log.error("�������ļ�");
			}
			// ������֤��
			int xym = calculate_XYM(fileName, 8);
			// ȡ�ļ���֤��
			String CAValue = "";
			for (int i = size - 8; i < size - 2; i++) {
				char ctemp = (char) temp[i];
				CAValue = CAValue + (char) temp[i];
			}
			log.debug(CAValue);

			// �ļ���֤��ͼ��������֤�������ȷ���TRUE
			if (Integer.parseInt(CAValue) == xym) {
				isCorrect = true;
				log.debug("��֤��ȷ");
			}
		} catch (FileNotFoundException e) {
			log.error("��֤ǩ������!", e);
		} catch (IOException e) {
			log.error("��֤ǩ������!", e);
		}
		return isCorrect;
	}

	/**
	 * ɽ���˿��ʵ���ʽ�У����
	 * 
	 * @param vouchNo
	 *            ƾ֤����
	 * @param subCode
	 *            ��Ŀ����
	 * @param rcvAccount
	 *            �տ��˺�
	 * @param amt
	 *            ���
	 * @param key
	 *            ��Կ
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

	/**
	 * ��֤SM3ǩ��
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @param strKey
	 * @param encKey
	 *            ������Կ
	 * @return δ����
	 */
	public static Boolean checkSM3Sign(String sourceFile, String targetFile,
			TsMankeyDto _dto) {
		try {
			String encKey = _dto.getSencryptkey();
			String strKey = _dto.getSkey();
			// ��ȡ��ǩ�ļ�����
			String signStr = FileUtil.getInstance().readFile(sourceFile);
			// ��֤ǩ��������ԭ�ĺ�MD5ǩ��,int�᲻�����
			int index = signStr.lastIndexOf("<CA>");
			// ԭ��
			String msg = signStr.substring(0, index);
			// ǩ����Ϣ
			String signInfo = signStr.substring(signStr.indexOf("<CA>") + 4,
					signStr.indexOf("</CA>"));
			// ����ǩ����Ϣ����ǩ��
			String Comsign = SM3Process.calculateSign(msg, strKey);
			// �Ƚ�ǩ��
			if (Comsign.equals(signInfo)) {
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

	/**
	 * �������õ���Կģʽ��ȡ��Ӧ����Կ���˴��������ļ��Ƿ����ѡ��
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
			
			// ���������������Կ
			if (StateConstant.KEY_TRECODE.equals(tmpdto.getSkeymode())) {
				for (TsMankeyDto dto : l) {
					if (strecode.equals(dto.getSkeyorgcode())) {
						return dto;
					}
				}
				// ����Ʊ��λ������Կ�ֱ��ǲ������룬�������С����ջ���
			} else if (StateConstant.KEY_BILLORG.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_GENBANK.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_TAXORG.equals(tmpdto.getSkeymode())) {
				// ���ݹ�������ȡ������������
				HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade
						.cacheFincInfo(sorgcode);
				for (TsMankeyDto dto : l) {
					// ˵�����������ǹ�����룬��Ҫת���ɲ���������в���,ֻ��ʵ���ʽ���������
					if (mapFincInfo.containsKey(strecode)) {
						if (mapFincInfo.get(strecode).getSfinorgcode().equals(
								dto.getSkeyorgcode())) {
							return dto;
						}
						// ������������ǲ��ǹ�����룬˵�������ջ��ػ��������,ֱ�ӽ��бȽ�
					} else {
						if (strecode.equals(dto.getSkeyorgcode())) {
							return dto;
						}
					}
				}

			} else {// �����������������������
				return tmpdto;
			}
		}
		return null;

	}
	/**
	 * �������õ���Կģʽ��ȡ��Ӧ����Կ���˴��������ļ��Ƿ����ѡ��
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
			
			// ���������������Կ
			if (StateConstant.KEY_TRECODE.equals(tmpdto.getSkeymode())) {
				for (TsMankeyDto dto : l) {
					if (strecode.equals(dto.getSkeyorgcode())) {
						return dto;
					}
				}
				// ����Ʊ��λ������Կ�ֱ��ǲ������룬�������С����ջ���
			} else if (StateConstant.KEY_BILLORG.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_GENBANK.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_TAXORG.equals(tmpdto.getSkeymode())) {
				// ���ݹ�������ȡ������������
				HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade.cacheFincInfo(sorgcode);
				for (TsMankeyDto dto : l) {
					// ˵�����������ǹ�����룬��Ҫת���ɲ���������в���,ֻ��ʵ���ʽ���������
					if (mapFincInfo.containsKey(strecode)) {
						if (mapFincInfo.get(strecode).getSfinorgcode().equals(
								dto.getSkeyorgcode())) {
							return dto;
						}
						// ������������ǲ��ǹ�����룬˵�������ջ��ػ��������,ֱ�ӽ��бȽ�
					} else {
						if (strecode!=null&&strecode.equals(dto.getSkeyorgcode())) {
							return dto;
						}
					}
				}

			} else {// �����������������������
				return tmpdto;
			}
		}
		return null;

	}
	/**
	 * �������õ���Կģʽ��ȡ��Ӧ����Կ���˴��������ļ��Ƿ����ѡ��
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
			
			// ���������������Կ
			if (StateConstant.KEY_TRECODE.equals(tmpdto.getSkeymode())) {
				for (TsMankeyDto dto : l) {
					if (strecode.equals(dto.getSkeyorgcode())) {
						return dto;
					}
				}
				// ����Ʊ��λ������Կ�ֱ��ǲ������룬�������С����ջ���
			} else if (StateConstant.KEY_BILLORG.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_GENBANK.equals(tmpdto.getSkeymode())
					|| StateConstant.KEY_TAXORG.equals(tmpdto.getSkeymode())) {
				// ���ݹ�������ȡ������������
				HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade
						.cacheFincInfo(sorgcode);
				for (TsMankeyDto dto : l) {
					// ˵�����������ǹ�����룬��Ҫת���ɲ���������в���,ֻ��ʵ���ʽ���������
					if (mapFincInfo.containsKey(strecode)) {
						if (mapFincInfo.get(strecode).getSfinorgcode().equals(
								dto.getSkeyorgcode())) {
							return dto;
						}
						// ������������ǲ��ǹ�����룬˵�������ջ��ػ��������,ֱ�ӽ��бȽ�
					} else {
						if (strecode.equals(dto.getSkeyorgcode())) {
							return dto;
						}
					}
				}

			} else {// �����������������������
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
