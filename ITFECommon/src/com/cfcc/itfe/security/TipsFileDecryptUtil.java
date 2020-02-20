package com.cfcc.itfe.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.constants.DeptoneGlobalBeanId;
import com.cfcc.deptone.common.core.exception.DecryptSecurityException;
import com.cfcc.deptone.common.core.exception.EncryptSecurityException;
import com.cfcc.deptone.common.core.security.ca.support.CASecurityHandler;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileProcInfoDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.interfaces.ILoginInfo;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * @author db2admin
 *
 */
public class TipsFileDecryptUtil {

	private static Log logger = LogFactory.getLog(TipsFileDecryptUtil.class);

	/**
	 * 
	 * @param <ITFELoginInfo>
	 * @param loginfo ��loginfo�еõ�����Ҫ�ļ����ļ�����Ϣ
	 * @param filename �����ļ����ļ���
	 * @return FileProcInfoDto
	 * @throws ITFEBizException
	 */
	/**
	 * ���ļ����н���,���ߴ���Ĳ������ļ����н���
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
	public static String newFileProcInfo(TsMankeyDto manKeyDto,String area,String encryptMode,String sourceFile) throws ITFEBizException, FileOperateException, FileNotFoundException {
		//���ܷ�ʽΪ�����ܵģ����߲���ɽ����ʽ���м��ܵģ�ֱ�ӷ����ļ��� ��
		if (StateConstant.NO_ENCRYPT.equals(encryptMode) ) {
			return sourceFile;
		} else if (StateConstant.DES3_ENCRYPT.equals(encryptMode)){ //ɽ������MD5ǩ�� ��3DES����
			File tmpfile = new File(sourceFile);
			String tmpfilename = tmpfile.getName();// ȡ�ļ�������
			String tmpfilepath = tmpfile.getParent();// ��ȡ�ļ���·��
			String targetFile =tmpfilepath +"/TMP/"+tmpfilename; //Ŀ���ļ�
			File tmpfile1 = new File(targetFile);
			if (tmpfile1.exists()) {
				FileUtil.getInstance().deleteFile(targetFile);
			}
			Boolean b=TipsFileDecrypt.checkDesAndMd5(sourceFile, targetFile, manKeyDto);
			if (b) {
				return targetFile;
			}else{
				return StateConstant.ENCRYPT_FAIL_INFO+encryptMode;
//				throw new ITFEBizException("�����ļ�+sourceFile+ʧ�ܣ�");
			}
			
		} else if (StateConstant.SIGN_ENCRYPT.equals(encryptMode)){ //SM3ͨ����ǩ
			File tmpfile = new File(sourceFile);
			String tmpfilename = tmpfile.getName();// ȡ�ļ�������
			String tmpfilepath = tmpfile.getParent();// ��ȡ�ļ���·��
			String targetFile =tmpfilepath +"/TMP/"+tmpfilename; //Ŀ���ļ�
			File tmpfile1 = new File(targetFile);
			if (tmpfile1.exists()) {
				FileUtil.getInstance().deleteFile(targetFile);
			}
			Boolean b = TipsFileDecrypt.checkSM3Sign(sourceFile, targetFile, manKeyDto);
			if (b) {
				return targetFile;
			}else{
				return StateConstant.ENCRYPT_FAIL_INFO;
			}
		}else if(StateConstant.SM3_ENCRYPT.equals(encryptMode))
		{
			List<String> fileContent = FileUtil.getInstance().readFileWithLine(sourceFile);
			SM3Util sm = new SM3Util();
			boolean b= sm.verifySM3Sign(sourceFile, manKeyDto.getSnewkey());
			if (b) {
				StringBuffer content = new StringBuffer();
				File tmpfile = new File(sourceFile);
				String tmpfilename = tmpfile.getName();// ȡ�ļ�������
				String tmpfilepath = tmpfile.getParent();// ��ȡ�ļ���·��
				String targetFile =tmpfilepath +"/TMP/"+tmpfilename; //Ŀ���ļ�
				File tmpfile1 = new File(targetFile);
				if (tmpfile1.exists()) {
					FileUtil.getInstance().deleteFile(targetFile);
				}
				for(int fi=0;fi<fileContent.size();fi++)
				{
					if(!"".equals(fileContent.get(fi)) && !fileContent.get(fi).startsWith("<CA>")){
						content.append(fileContent.get(fi)+System.getProperty("line.separator"));
					}
				}
				FileUtil.getInstance().writeFile(targetFile, content.toString());
				return targetFile;
			}else{
				return StateConstant.ENCRYPT_FAIL_INFO+encryptMode;
			}
		}else{
			return "Ŀǰ�в�֧�ָ��ֽ��ܷ�ʽ="+encryptMode;
		}
		
	
	}
	public static String fileProcInfo(TsMankeyDto manKeyDto,String area,String encryptMode,String sourceFile) throws ITFEBizException, FileOperateException, FileNotFoundException {
		//���ܷ�ʽΪ�����ܵģ����߲���ɽ����ʽ���м��ܵģ�ֱ�ӷ����ļ��� ��
		if (StateConstant.NO_ENCRYPT.equals(encryptMode) ) {
			return sourceFile;
		} else if (StateConstant.DES3_ENCRYPT.equals(encryptMode)){ //ɽ������MD5ǩ�� ��3DES����
			File tmpfile = new File(sourceFile);
			String tmpfilename = tmpfile.getName();// ȡ�ļ�������
			String tmpfilepath = tmpfile.getParent();// ��ȡ�ļ���·��
			String targetFile =tmpfilepath +"/TMP/"+tmpfilename; //Ŀ���ļ�
			File tmpfile1 = new File(targetFile);
			if (tmpfile1.exists()) {
				FileUtil.getInstance().deleteFile(targetFile);
			}
			Boolean b=TipsFileDecrypt.checkDesAndMd5(sourceFile, targetFile, manKeyDto);
			if (b) {
				return targetFile;
			}else{
				//����������쳣 ������β������ļ�����ʱ�ļ�
				if(new File(sourceFile).exists()){
					FileUtil.getInstance().deleteFile(sourceFile);
				}
				if(new File(targetFile).exists()){
					FileUtil.getInstance().deleteFile(targetFile);
				}
				return StateConstant.ENCRYPT_FAIL_INFO;
			}
			
		} else if (StateConstant.SIGN_ENCRYPT.equals(encryptMode)){ //SM3ͨ����ǩ
			File tmpfile = new File(sourceFile);
			String tmpfilename = tmpfile.getName();// ȡ�ļ�������
			String tmpfilepath = tmpfile.getParent();// ��ȡ�ļ���·��
			String targetFile =tmpfilepath +"/TMP/"+tmpfilename; //Ŀ���ļ�
			File tmpfile1 = new File(targetFile);
			if (tmpfile1.exists()) {
				FileUtil.getInstance().deleteFile(targetFile);
			}
			Boolean b=TipsFileDecrypt.checkSM3Sign(sourceFile, targetFile, manKeyDto);
			if (b) {
				return targetFile;
			}else{
				//����������쳣 ������β������ļ�����ʱ�ļ�
				if(new File(sourceFile).exists()){
					FileUtil.getInstance().deleteFile(sourceFile);
				}
				if(new File(targetFile).exists()){
					FileUtil.getInstance().deleteFile(targetFile);
				}
				return StateConstant.ENCRYPT_FAIL_INFO;
			}
		}else{
			throw new ITFEBizException("Ŀǰ�в�֧�ָ��ֽ��ܷ�ʽ");
		}
		
	
	}
	
}
