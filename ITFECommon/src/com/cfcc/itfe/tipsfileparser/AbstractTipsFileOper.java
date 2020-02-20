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
	 * TBS�ļ�����
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
	 * ��������ǩ���ļ����ܣ�����
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String[]> readSignFile(String file, String split)
			throws FileOperateException {
		try {
			// �洢���ܵ���ʱ�ļ�
			String tmp = file + ".tmp";
			decryptFile(file, tmp);
			List<String[]> l = FileUtil.getInstance().readFileWithLine(tmp,
					split);
			// ɾ����ʱ�ļ�
			FileUtil.getInstance().deleteFile(tmp);
			return l;
		} catch (Exception e) {
			log.error(e);
			throw new FileOperateException("�ļ����ܳ����쳣", e);
		} finally {
			// ɾ����ʱ�ļ�
			try {
				FileUtil.getInstance().deleteFile(file + ".tmp");
			} catch (FileNotFoundException e) {
				log.error(e);
				throw new FileOperateException("�ļ�����ɾ����ʱ�ļ�ʱ�����쳣", e);
			}
		}
	}

	/**
	 * ��������PAS�ļ����ܣ�����
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String[]> readPassFile(String file, String split)
			throws FileOperateException {
		try {
			// �洢���ܵ���ʱ�ļ�
			String tmp = file + ".tmp";
			decryptPassFile(file, tmp);
			List<String[]> l = FileUtil.getInstance().readFileWithLine(tmp,
					split);
			// ɾ����ʱ�ļ�
			FileUtil.getInstance().deleteFile(tmp);
			return l;
		} catch (Exception e) {
			log.error(e);
			throw new FileOperateException("�ļ����ܳ����쳣", e);
		} finally {
			// ɾ����ʱ�ļ�
			try {
				FileUtil.getInstance().deleteFile(file + ".tmp");
			} catch (FileNotFoundException e) {
				log.error(e);
				throw new FileOperateException("�ļ�����ɾ����ʱ�ļ�ʱ�����쳣", e);
			}
		}
	}

	/**
	 * ��ȡ��������֧��Ԥ���Ŀ����Ϣ
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
			throw new ITFEBizException("��ȡ��������֧��Ԥ���Ŀ����Ϣʧ��", e);
		}
	}
	/**
	 * ����ļ�У����Կ(ɽ��)
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
			throw new ITFEBizException("�ļ�����У������֤ʱû���ҵ���Ӧ��Կ�����֤");
		}
		return ((TsInfoconnorgDto) l.get(0)).getSkey();
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
	 * �ļ�У�鷽��
	 */
	public void importValidator() throws Exception {

	}

	/**
	 * �����ļ�
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void decryptFile(String srcFile, String dstFile) throws Exception {
		TreasuryEncrypt.Decrypt(srcFile, dstFile, false);
	}

	/**
	 * pass�ļ������ļ�
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void decryptPassFile(String srcFile, String dstFile)
			throws Exception {
		ImplGKEncryptKeyJNI jni = new ImplGKEncryptKeyJNI();

	}

	/**
	 * �ṩ���������������֤
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
	 * У��Ԥ���Ŀ ����--1,����--2 subcd ��ҪУ��Ŀ�Ŀ���� msgtype ����ҵ������(�Ա�������) type ���ܿ�Ŀ���� 1
	 * ���ÿ�Ŀ���� 2
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
		if (msgtype.equals(MsgConstant.MSG_NO_7211)) { // ˰Ʊ����
		} else if (msgtype.equals(MsgConstant.MSG_NO_1104)) { // �˿�
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				sb.append("�˿��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
						+ "]�ļ�¼�п�Ŀ���� " + subcode + " û����'Ԥ���Ŀ����'���ҵ�!");
			} else {
				if (!"1".equals(dto.getSsubjectclass())) {
					sb.append("�˿��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�п�Ŀ���� " + subcode + " �����������Ŀ����\n");
				}
				if (!"1".equals(dto.getSwriteflag())) {
					sb.append("�˿��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�п�Ŀ���� " + subcode + " ��¼���־Ϊ����¼��\n");
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_5101)) { // ʵ���ʽ�
			String bizflag = filename.substring(filename.length() - 7, filename.length() - 5); // һ��Ԥ�㻹�ǵ���
			String moveflag ;
			if ("1".equals(type)) { // ���ܿ�Ŀ
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�й��ܿ�Ŀ���� " + subcode + " û����'Ԥ���Ŀ����'���ҵ�!");
				} else {
					moveflag = dto.getSmoveflag(); //������־0���ǵ��� 1������
					if (!"2".equals(dto.getSsubjectclass())) {
						sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
								+ "]�ļ�¼�й��ܿ�Ŀ���� " + subcode + " ����֧�����ܿ�Ŀ\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
								+ "]�ļ�¼�й��ܿ�Ŀ���� " + subcode + " ��¼���־Ϊ����¼��\n");
					}
					if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizflag)){//һ��Ԥ��֧��
						if("1".equals(moveflag)){
							sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
									+ "]�ļ�¼�й��ܿ�Ŀ���� " + subcode + " Ϊ������Ŀ\n");
						}
					}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizflag)){//����֧��
						if("0".equals(moveflag)){
							sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
									+ "]�ļ�¼�й��ܿ�Ŀ���� " + subcode + " Ϊ�ǵ�����Ŀ\n");
						}
					}
				}
			} else if ("2".equals(type) && !"".equals(subcode)
					&& null != subcode) { // ���ÿ�Ŀ
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�о��ÿ�Ŀ���� " + subcode + " û����'Ԥ���Ŀ����'���ҵ�!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
								+ "]�ļ�¼�о��ÿ�Ŀ���� " + subcode + " ����֧�����ÿ�Ŀ\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
								+ "]�ļ�¼�о��ÿ�Ŀ���� " + subcode + " ��¼���־Ϊ����¼��\n");
					}
				}
				moveflag = dto.getSmoveflag(); //������־0���ǵ��� 1������
				if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizflag)){//һ��Ԥ��֧��
					if("1".equals(moveflag)){
						sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
								+ "]�ļ�¼�й��ܿ�Ŀ���� " + subcode + " Ϊ������Ŀ\n");
					}
				}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizflag)){//����֧��
					if("0".equals(moveflag)){
						sb.append("ʵ���ʽ��ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
								+ "]�ļ�¼�й��ܿ�Ŀ���� " + subcode + " Ϊ�ǵ�����Ŀ\n");
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_5102)) { // ֱ��֧�����
			if ("1".equals(type)) { // ���ܿ�Ŀ
				if (null == subcode || "".equals(subcode)) {
					sb.append("ֱ��֧������ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�й��ܿ�Ŀ����Ϊ��!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb
								.append("ֱ��֧������ļ�[" + filename + "] ƾ֤���Ϊ["
										+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
										+ " û����'Ԥ���Ŀ����'���ҵ�!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("ֱ��֧������ļ�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ����֧�����ܿ�Ŀ\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("ֱ��֧������ļ�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ��¼���־Ϊ����¼��\n");
						}
					}
				}
			} else if ("2".equals(type)) { // ���ÿ�Ŀ
				if (null == subcode || "".equals(subcode)) {
					sb.append("���þ��ô���������,[" + filename + "]�о��ÿ�Ŀ���벻��Ϊ�գ�");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("ֱ��֧������ļ�[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " û����'Ԥ���Ŀ����'���ҵ�!");
					} else {
						if (!"3".equals(dto.getSsubjectclass())) {
							sb.append("ֱ��֧������ļ�[" + filename + "]�о��ÿ�Ŀ���� "
									+ subcode + " ����֧�����ÿ�Ŀ\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("ֱ��֧������ļ�[" + filename + "]�о��ÿ�Ŀ���� "
									+ subcode + " ��¼���־Ϊ����¼��\n");
						}
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_5103)) { // ��Ȩ֧�����
			if ("1".equals(type)) { // ���ܿ�Ŀ
				if (null == subcode || "".equals(subcode)) {
					sb.append("��Ȩ֧������ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�й��ܿ�Ŀ����Ϊ��!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb
								.append("��Ȩ֧������ļ�[" + filename + "] ƾ֤���Ϊ["
										+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
										+ " û����'Ԥ���Ŀ����'���ҵ�!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("��Ȩ֧������ļ�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ����֧�����ܿ�Ŀ\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("��Ȩ֧������ļ�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ��¼���־Ϊ����¼��\n");
						}
					}
				}
			} else if ("2".equals(type)) { // ���ÿ�Ŀ
				if (null == subcode || "".equals(subcode)) {
					sb.append("���þ��ô���������,[" + filename + "]�о��ÿ�Ŀ���벻��Ϊ�գ�");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("��Ȩ֧������ļ�[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " û����'Ԥ���Ŀ����'���ҵ�!");
					} else {
						if (!"3".equals(dto.getSsubjectclass())) {
							sb.append("��Ȩ֧������ļ�[" + filename + "]�о��ÿ�Ŀ���� "
									+ subcode + " ����֧�����ÿ�Ŀ\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("��Ȩ֧������ļ�[" + filename + "]�о��ÿ�Ŀ���� "
									+ subcode + " ��¼���־Ϊ����¼��\n");
						}
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.MSG_NO_1105)) { // ����
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				sb.append("�����ļ�[" + filename + "]�п�Ŀ���� " + subcode
						+ " û����'Ԥ���Ŀ����'���ҵ�!");
			}

		} else if (msgtype.equals(MsgConstant.MSG_NO_5104)) { // ���а���ֱ��֧��
			if ("1".equals(type)) { // ���ܿ�Ŀ
				if (null == subcode || "".equals(subcode)) {
					sb.append("���а���ֱ��֧���ļ�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�й��ܿ�Ŀ����Ϊ��!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("���а���ֱ��֧���ļ�[" + filename + "] ƾ֤���Ϊ["
								+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
								+ " û����'Ԥ���Ŀ����'���ҵ�!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("���а���ֱ��֧���ļ�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ����֧�����ܿ�Ŀ\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("���а���ֱ��֧���ļ�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ��¼���־Ϊ����¼��\n");
						}
					}
				}
			} else if ("2".equals(type)) { // ���ÿ�Ŀ
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("���а���ֱ��֧���ļ�[" + filename + "]�о��ÿ�Ŀ���� " + subcode
							+ " û����'Ԥ���Ŀ����'���ҵ�!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("���а���ֱ��֧���ļ�[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " ����֧�����ÿ�Ŀ\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("���а���ֱ��֧���ļ�[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " ��¼���־Ϊ����¼��\n");
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.APPLYPAY_DAORU)) { // ���а���֧����������
			if ("1".equals(type)) { // ���ܿ�Ŀ
				if (null == subcode || "".equals(subcode)) {
					sb.append("���а���֧����������[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�й��ܿ�Ŀ����Ϊ��!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("���а���֧����������[" + filename + "] ƾ֤���Ϊ["
								+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
								+ " û����'Ԥ���Ŀ����'���ҵ�!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("���а���֧����������[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ����֧�����ܿ�Ŀ\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("���а���֧����������[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ��¼���־Ϊ����¼��\n");
						}
					}
				}
			} else if ("2".equals(type)) { // ���ÿ�Ŀ
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("���а���֧����������[" + filename + "]�о��ÿ�Ŀ���� " + subcode
							+ " û����'Ԥ���Ŀ����'���ҵ�!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("���а���֧����������[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " ����֧�����ÿ�Ŀ\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("���а���֧����������[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " ��¼���־Ϊ����¼��\n");
					}
				}
			}
		} else if (msgtype.equals(MsgConstant.APPLYPAY_BACK_DAORU)) { // ���а���֧�����������˻�
			if ("1".equals(type)) { // ���ܿ�Ŀ
				if (null == subcode || "".equals(subcode)) {
					sb.append("���а���֧�����������˻�[" + filename + "] ƾ֤���Ϊ[" + svouno
							+ "]�ļ�¼�й��ܿ�Ŀ����Ϊ��!");
				} else {
					if (null == dto || "".equals(dto.getSsubjectcode())) {
						sb.append("���а���֧�����������˻�[" + filename + "] ƾ֤���Ϊ["
								+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
								+ " û����'Ԥ���Ŀ����'���ҵ�!");
					} else {
						if (!"2".equals(dto.getSsubjectclass())) {
							sb.append("���а���֧�����������˻�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ����֧�����ܿ�Ŀ\n");
						}
						if (!"1".equals(dto.getSwriteflag())) {
							sb.append("���а���֧�����������˻�[" + filename + "] ƾ֤���Ϊ["
									+ svouno + "]�ļ�¼�й��ܿ�Ŀ���� " + subcode
									+ " ��¼���־Ϊ����¼��\n");
						}
					}
				}
			} else if ("2".equals(type)) { // ���ÿ�Ŀ
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					sb.append("���а���֧�����������˻�[" + filename + "]�о��ÿ�Ŀ���� "
							+ subcode + " û����'Ԥ���Ŀ����'���ҵ�!");
				} else {
					if (!"3".equals(dto.getSsubjectclass())) {
						sb.append("���а���֧�����������˻�[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " ����֧�����ÿ�Ŀ\n");
					}
					if (!"1".equals(dto.getSwriteflag())) {
						sb.append("���а���֧�����������˻�[" + filename + "]�о��ÿ�Ŀ���� "
								+ subcode + " ��¼���־Ϊ����¼��\n");
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
	 * ���з��˴���У��
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
	 * �õ�֧���кŻ���Map
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
	 * �õ��������뻺��Map
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
	 * �õ����ջ��ػ���Map
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
	 * ���չ�����У���ļ����ظ�
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
				exceptionInfo.append("ֱ��֧������ļ�[" + filename + "]�и��ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.MSG_NO_5103.equals(type)) {
				exceptionInfo.append("��Ȩ֧������ļ�[" + filename + "]�� ���ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.MSG_NO_5104.equals(type)) {
				exceptionInfo.append("���а���ֱ��֧���ļ�[" + filename + "]�� ���ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.MSG_NO_5101.equals(type)) {
				exceptionInfo.append("ʵ���ʽ��ļ�[" + filename + "]�� ���ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.MSG_NO_1104.equals(type)) {
				exceptionInfo.append("�˿��ļ�[" + filename + "]�� ���ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.MSG_NO_1105.equals(type)) {
				exceptionInfo.append("�����ļ�[" + filename + "]�� ���ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.MSG_NO_1106.equals(type)) {
				exceptionInfo.append("��ֵ��ļ�[" + filename + "]�� ���ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.APPLYPAY_DAORU.equals(type)) {
				exceptionInfo.append("���а���֧�����������ļ�[" + filename + "]�� ���ݹ������["
						+ trecode + "] + �ļ���[" + filename + "] У��Ϊ�ظ�����!");
			} else if (MsgConstant.APPLYPAY_BACK_DAORU.equals(type)) {
				exceptionInfo.append("���а���֧�����������˻��ļ�[" + filename
						+ "]�� ���ݹ������[" + trecode + "] + �ļ���[" + filename
						+ "] У��Ϊ�ظ�����!");
			}
		}
		return exceptionInfo.toString();
	}

	/**
	 * ȡ��֧��ϵͳ�к�
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
	 * ȡ�ø������˻���Ϣ
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public static HashMap<String, TsInfoconnorgaccDto> getBookAcctMap(
			String bookorgCode) throws JAFDatabaseException, ValidateException {
		HashMap<String, TsInfoconnorgaccDto> acctmap = new HashMap<String, TsInfoconnorgaccDto>();
		// ͨ���������͸������˺� �ҵ���Ӧ����������
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
	 * ȡ�������к���Ϣ
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
	 * �ӻ����л�ȡ���и������˻���Ϣ
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
	 * �ӻ����л�ȡ���и������˻���Ϣ����˻�����
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
	 * �ӻ����л�ȡ����������Ϣ
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
