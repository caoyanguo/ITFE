package com.cfcc.itfe.service.para.tcbsimport;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.ParamConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.param.IParamInOut;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectAdjustDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangtuo
 * @time 10-04-12 09:12:18 codecomment:
 */

public class TCBSimportService extends AbstractTCBSimportService {
	private static Log log = LogFactory.getLog(TCBSimportService.class);

	/**
	 * Ԥ���Ŀά�����ݵ���
	 */
	public void fileImport(String filePath) throws ITFEBizException {
		try {
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			List<String[]> listStr = FileUtil.getInstance().readFileWithLine(
					fileFullName, ",");
			FileUtil.getInstance().deleteFile(fileFullName);
			if (filePath.toUpperCase().endsWith("Ԥ���Ŀ_TBS.CSV")
					&& listStr.get(0).length == 15) {
				fileImportTBS(listStr);
				return ;
			}
			if (listStr != null && listStr.size() > 0) {
				if (fileFullName.contains("������")) {
					TsBudgetsubjectDto[] dtos = new TsBudgetsubjectDto[listStr
							.size() - 1];
					for (int i = 1; i < listStr.size(); i++) {
						TsBudgetsubjectDto dto = new TsBudgetsubjectDto();
						String[] ss = listStr.get(i);

						// ����Ԥ���Ŀά������
						dto.setSorgcode(getLoginInfo().getSorgcode());
						dto.setSsubjectcode(ss[1]);// ��Ŀ����
						dto.setSsubjectname(ss[2]);// ��Ŀ����
						dto.setSsubjectclass(change(ss[3]));// ��Ŀ����
						dto.setSclassflag(change(ss[4]));// ��Ŀ����
						dto.setSbudgettype(change(ss[5]));// Ԥ������
						dto.setSsubjecttype(change(ss[6]));// ��Ŀ����
						dto.setSwriteflag(change(ss[8]));// ¼���־
						dto.setSsubjectattr(change(ss[9]));// ��Ŀ����
						dto.setSmoveflag(change(ss[10]));// ������־
						dto.setSinoutflag(change(ss[3]));// ��֧��־
						dto.setSdrawbacktype("");// ��˰���
						dto.setImodicount(1);// �޸Ĵ���
						dtos[i - 1] = dto;
					}
					delete();
					DatabaseFacade.getDb().create(dtos);
				} else if (fileFullName.contains("������")) {
					TsBudgetsubjectAdjustDto[] dtos = new TsBudgetsubjectAdjustDto[listStr
							.size() - 1];
					for (int i = 1; i < listStr.size(); i++) {
						TsBudgetsubjectAdjustDto dto = new TsBudgetsubjectAdjustDto();
						String[] ss = listStr.get(i);

						// ����Ԥ���Ŀά������
						dto.setSorgcode(getLoginInfo().getSorgcode());
						dto.setSsubjectcode(ss[1]);// ��Ŀ����
						dto.setSsubjectname(ss[2]);// ��Ŀ����
						dto.setSsubjectclass(change(ss[3]));// ��Ŀ����
						dto.setSclassflag(change(ss[4]));// ��Ŀ����
						dto.setSbudgettype(change(ss[5]));// Ԥ������
						dto.setSsubjecttype(change(ss[6]));// ��Ŀ����
						dto.setSwriteflag(change(ss[8]));// ¼���־
						dto.setSsubjectattr(change(ss[9]));// ��Ŀ����
						dto.setSmoveflag(change(ss[10]));// ������־
						dto.setSinoutflag(change(ss[3]));// ��֧��־
						dto.setSdrawbacktype("");// ��˰���
						dto.setImodicount(1);// �޸Ĵ���
						dtos[i - 1] = dto;
					}
					deleteadjust();
					DatabaseFacade.getDb().create(dtos);
				}else if(fileFullName.length()>15&&fileFullName.endsWith("Ԥ���Ŀ����.txt"))
				{
					String trecode = fileFullName.substring(0,10);
					if(!verifyTreasury(trecode,getLoginInfo().getSorgcode()))
						throw new ITFEBizException("TBS���˴����ļ����Ƹ�ʽ����ӦΪ�������+���˴���.txt!");
					List<String[]> listStrtbs = FileUtil.getInstance().readFileWithLine(fileFullName, "\t");
					TsBudgetsubjectAdjustDto[] dtos = new TsBudgetsubjectAdjustDto[listStrtbs.size() - 1];
					for (int i = 1; i < listStrtbs.size(); i++) {
						TsBudgetsubjectAdjustDto dto = new TsBudgetsubjectAdjustDto();
						String[] ss = listStrtbs.get(i);

						// ����Ԥ���Ŀά������
						dto.setSorgcode(getLoginInfo().getSorgcode());
						dto.setSsubjectcode(ss[1]);// ��Ŀ����
						dto.setSsubjectname(ss[2]);// ��Ŀ����
						dto.setSsubjecttype(change(ss[3]));// ��Ŀ����
						dto.setSclassflag(change(ss[4]));// ��Ŀ����
						dto.setSinoutflag(change(ss[5]));// ��֧��־
						dto.setSwriteflag(change(ss[6]));// ¼���־
						dto.setSsubjectattr(change(ss[7]));// ��Ŀ����
						dto.setSmoveflag(change(ss[8]));// ������־
						
						dto.setSsubjectclass("1");// ��Ŀ����
						dto.setSbudgettype(change("1"));// Ԥ������
						dto.setSdrawbacktype("1");// ��˰���
						dto.setImodicount(1);// �޸Ĵ���
						dtos[i - 1] = dto;
					}
					deleteadjust();
					DatabaseFacade.getDb().create(dtos);
				}

			}

			SrvCacheFacade.reloadBuffer(getLoginInfo().getSorgcode(),
					StateConstant.CacheBdgSbt);
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("��ȡ�ļ�����", e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ���Ŀά������ʱ��������", e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������ļ�ʱ�Ҳ���ָ�����ļ�", e);
		}

	}

	private void fileImportTBS(List<String[]> listStr) throws ITFEBizException {
		try {
		if (null != listStr && listStr.size() > 0) {
			TsBudgetsubjectDto[] dtos = new TsBudgetsubjectDto[listStr.size() - 1];
			TsBudgetsubjectDto dto = null;
			String[] ss = null;
			for (int i = 1; i < listStr.size(); i++) {
				dto = new TsBudgetsubjectDto();
				ss = listStr.get(i);
				// ����Ԥ���Ŀά������
				dto.setSorgcode(getLoginInfo().getSorgcode());
				dto.setSsubjectcode(ss[0]);// ��Ŀ����
				dto.setSsubjectname(ss[1]);// ��Ŀ����
				dto.setSsubjectclass("1");// ��Ŀ����
				dto.setSclassflag(change(ss[3]));// ��Ŀ����
				dto.setSbudgettype("1");// Ԥ������ ��
				dto.setSsubjecttype(change(ss[2]));// ��Ŀ����
				dto.setSwriteflag(change(ss[5]));// ¼���־
				dto.setSsubjectattr(change(ss[6]));// ��Ŀ����
				dto.setSmoveflag(change(ss[7]));// ������־
				dto.setSinoutflag(change(ss[4]));// ��֧��־
				dto.setSdrawbacktype("");// ��˰���
				dto.setImodicount(1);// �޸Ĵ���
				dtos[i - 1] = dto;
			}
			
				delete();
			DatabaseFacade.getDb().create(dtos);
		}
		} catch (ITFEBizException e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ���Ŀά������ʱ��������", e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ���Ŀά������ʱ��������", e);
		}
	}

	/**
	 * ���ջ��ع����Ӧ��ϵά�����ݵ���
	 */
	public void taxFileImport(String filePath) throws ITFEBizException {
		try {
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			List<String[]> listStr = FileUtil.getInstance().readFileWithLine(
					fileFullName, ",");
			if (null == listStr || listStr.size() == 0) {
				return;
			}
			FileUtil.getInstance().deleteFile(fileFullName);
			// ��ȡ���ջ��ش������Ϣ
			List<TdTaxorgParamDto> tdtaxorglist = DatabaseFacade.getODB().find(
					TdTaxorgParamDto.class,
					" WHERE S_BOOKORGCODE = '" + getLoginInfo().getSorgcode()
							+ "'");
			HashMap<String, TdTaxorgParamDto> tdtaxorgmap = new HashMap<String, TdTaxorgParamDto>();
			if (tdtaxorglist != null && tdtaxorglist.size() > 0) {
				for (TdTaxorgParamDto tdtaxorg : tdtaxorglist) {
					tdtaxorgmap.put(tdtaxorg.getSbookorgcode()
							+ tdtaxorg.getStaxorgcode(), tdtaxorg);
				}
			}
			if (filePath.toUpperCase().endsWith("���ջ��ع����Ӧ��ϵ_TBS.CSV")
					&& listStr.get(0).length == 7) {
				taxTbsFileImport(listStr, tdtaxorgmap);
				return;
			}
			if (listStr != null && listStr.size() > 0) {
				TsTaxorgDto[] dtos = new TsTaxorgDto[listStr.size() - 1];
				for (int i = 1; i < listStr.size(); i++) {
					TsTaxorgDto dto = new TsTaxorgDto();
					String[] ss = listStr.get(i);

					// �������ջ��ع����Ӧ��ϵά������
					dto.setSorgcode(getLoginInfo().getSorgcode());
					dto.setStaxorgcode(ss[1]);// ���ջ��ش���
					dto.setStaxorgname("");// ���ջ�������
					dto.setStrecode(ss[2]);// �������

					// �������ջ��ش����
					if (tdtaxorglist != null && tdtaxorglist.size() > 0) {
						TdTaxorgParamDto tmptdtaxorg = tdtaxorgmap.get(dto
								.getSorgcode()
								+ dto.getStaxorgcode());
						if (tmptdtaxorg != null) {
							if(tmptdtaxorg.getStaxorgname()!=null)
								dto.setStaxorgname(tmptdtaxorg.getStaxorgname().length()>60 ? CommonUtil.subString(tmptdtaxorg.getStaxorgname(), 60) : tmptdtaxorg.getStaxorgname());// ���ջ�������
							dto.setStaxprop(tmptdtaxorg.getCtaxorgprop());// ���ջ�������
						}
					}

					dtos[i - 1] = dto;
				}
				taxDelete();
				DatabaseFacade.getDb().create(dtos);
			}

		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("��ȡ�ļ�����", e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("�������ջ��ع����Ӧ��ϵά������ʱ��������", e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������ļ�ʱ�Ҳ���ָ�����ļ�", e);
		}

	}

	private void taxTbsFileImport(List<String[]> listStr,
			HashMap<String, TdTaxorgParamDto> tdtaxorgmap)
			throws ITFEBizException {
		try {
			if (listStr != null && listStr.size() > 0) {
				TsTaxorgDto[] dtos = new TsTaxorgDto[listStr.size() - 1];
				for (int i = 1; i < listStr.size(); i++) {
					TsTaxorgDto dto = new TsTaxorgDto();
					String[] ss = listStr.get(i);

					// �������ջ��ع����Ӧ��ϵά������
					dto.setSorgcode(getLoginInfo().getSorgcode());
					dto.setStaxorgcode(ss[0]);// ���ջ��ش���
					dto.setStaxorgname("");// ���ջ�������
					dto.setStrecode(ss[1]);// �������

					// �������ջ��ش����
					if (tdtaxorgmap != null && tdtaxorgmap.size() > 0) {
						TdTaxorgParamDto tmptdtaxorg = tdtaxorgmap.get(dto
								.getSorgcode()
								+ dto.getStaxorgcode());
						if (tmptdtaxorg != null) {
							dto.setStaxorgname(tmptdtaxorg.getStaxorgname());// ���ջ�������
							dto.setStaxprop(tmptdtaxorg.getCtaxorgprop());// ���ջ�������
						}
					}

					dtos[i - 1] = dto;
				}
				taxDelete();
				DatabaseFacade.getDb().create(dtos);
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("�������ջ��ع����Ӧ��ϵά������ʱ��������", e);
		}
	}

	/**
	 * �����˺����ݵ���
	 */
	public void banknoImport(String filePath) throws ITFEBizException {
		try {
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			List<String> list = FileUtil.getInstance().readFileWithLine(
					fileFullName);
			bankDataImport(list);
			FileUtil.getInstance().deleteFile(fileFullName);
			SrvCacheFacade.reloadBuffer(getLoginInfo().getSorgcode(),
					StateConstant.CachePayBank);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e.toString());
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException(e.toString());
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException(e.toString());
		}

	}

	/**
	 * ɾ��-Ԥ���Ŀ
	 */
	public void delete() throws ITFEBizException {
		String delSQL = "delete from TS_BUDGETSUBJECT where S_ORGCODE = ? ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.runQueryCloseCon(delSQL);
			TsBudgetsubjectDto deldto = new TsBudgetsubjectDto();
			deldto.setSorgcode(this.getLoginInfo().getSorgcode());
			CommonFacade.getODB().deleteRsByDto(deldto);
		} catch (JAFDatabaseException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} catch (ValidateException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * ɾ��-Ԥ���Ŀ������
	 */
	public void deleteadjust() throws ITFEBizException {
		String delSQL = "delete from TS_BUDGETSUBJECT_ADJUST where S_ORGCODE = ? ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.runQueryCloseCon(delSQL);
			TsBudgetsubjectAdjustDto deldto = new TsBudgetsubjectAdjustDto();
			deldto.setSorgcode(this.getLoginInfo().getSorgcode());
			CommonFacade.getODB().deleteRsByDto(deldto);
		} catch (JAFDatabaseException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} catch (ValidateException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * ɾ��-���ջ��ع����Ӧ��ϵά��
	 */
	public void taxDelete() throws ITFEBizException {
		String delSQL = "delete from TS_TAXORG where S_ORGCODE = ? ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			sqlExec.runQueryCloseCon(delSQL);
			TsTaxorgDto deldto = new TsTaxorgDto();
			deldto.setSorgcode(this.getLoginInfo().getSorgcode());
			CommonFacade.getODB().deleteRsByDto(deldto);
		} catch (JAFDatabaseException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} catch (ValidateException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * ɾ��-�����˺�
	 */
	public void banknoDelete() throws ITFEBizException {
		String delSQL = "delete from TS_PAYBANK";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.runQueryCloseCon(delSQL);
		} catch (JAFDatabaseException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} catch (Exception e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * ���˴�����Ϣ����
	 * 
	 * @generated
	 * @param filePath
	 * @throws ITFEBizException
	 */
	public void tdCorpImport(String filePath) throws ITFEBizException {
		int rownum = 0;
		String scorpcode = "";
		try {
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			List<String[]> listStr = FileUtil.getInstance().readFileWithLine(
					fileFullName, ",");
			FileUtil.getInstance().deleteFile(fileFullName);
			if (filePath.toUpperCase().endsWith("_TBS.CSV")
					&& listStr.get(0).length == 4) {
				tdTbsCrop(listStr);
				return;
			}else if(fileFullName.length()>15&&fileFullName.endsWith("���˴���.txt"))
			{
				String trecode = fileFullName.substring(0,10);
				if(!verifyTreasury(trecode,getLoginInfo().getSorgcode()))
					throw new ITFEBizException("TBS���˴����ļ����Ƹ�ʽ����ӦΪ�������+���˴���.txt!");
				List<String[]> listStrtbs = FileUtil.getInstance().readFileWithLine(fileFullName, "\t");
				tdTbsCrop(listStrtbs);
				return;
			}
			if (listStr != null && listStr.size() > 0) {
				String ctrimflag = null;
				if (fileFullName.contains("������")) {
					ctrimflag = "0";
				} else if (fileFullName.contains("������")) {
					ctrimflag = "1";
				}
				HashMap<String, String> map = new HashMap<String, String>();
				List<IDto> list = new ArrayList<IDto>();
				TdCorpDto dto;
				for (int i = 1; i < listStr.size(); i++) {
					rownum = i;
					dto = new TdCorpDto();
					String[] ss = listStr.get(i);
					if (ss == null || ss.length == 0) {
						break;
					}
					if (ss[0].trim().length() != 12) {
						throw new ITFEBizException("�����������Ƿ�������ӦΪ12λ��"
								+ ss[0].trim());
					}
					if (i == 1) {
						tdCorpDelete(ss[0].trim());
					}
					// ���˴�����Ϣ
					dto.setCtrimflag(ctrimflag);// �����ڱ�־
					dto.setSbookorgcode(ss[0].trim());// �����������

					dto.setStrecode(ss[1].trim());// �����������
					if (ss[1].trim().length() != 10) {
						throw new ITFEBizException("�����������Ƿ�������ӦΪ10λ��"
								+ ss[1].trim());
					}
					scorpcode = ss[2].trim();
					if (map.containsKey(ss[1].trim() + "||" + scorpcode)) {
						throw new ITFEBizException("�������+Ԥ�㵥λ�����ظ���"
								+ ss[1].trim() + "||" + scorpcode);
					} else {
						map.put(ss[1].trim() + "||" + scorpcode, "");
					}
					dto.setScorpcode(ss[2].trim());// ���˴���_VARCHAR_NOT NULL
					dto.setScorpname(ss[3].trim());// ��������_VARCHAR_NOT NULL
					dto.setScorpsht(ss[4].trim());// ���˼���_VARCHAR
					dto.setCmayaprtfund(ss[5].trim());// �ܷ�ʵ���ʽ�_CHARACTER_NOT
					// NULL
					dto.setCtaxpayerprop(ss[6].trim());// ��λ����_CHARACTER_NOT
					// NULL
					dto.setCpayoutprop(ss[7].trim());// ע������_CHARACTER_NOT NULL
					dto.setCtradeprop(ss[8].trim());// ��ҵ����_CHARACTER_NOT NULL
					if (ss.length == 10) {

						if (ss[9] == null || ss[9].trim().equals("")) {
							// dto.setIacctnum(0);// �˻���_INTEGER_NOT NULL
						} else {
							dto.setIacctnum(Integer.parseInt(ss[9].trim()));// �˻���_INTEGER_NOT
							// NULL
						}
					}
					dto.setTssysupdate(new Timestamp(new java.util.Date()
							.getTime()));// ϵͳ����ʱ��
					list.add(dto);
					if (list.size() > 0 && list.size() % 1000 == 0) {
						DatabaseFacade.getODB().create(
								CommonUtil.listTArray(list));
						list = new ArrayList<IDto>();
					}
				}
				if (list.size() > 0) {
					DatabaseFacade.getODB().create(CommonUtil.listTArray(list));
				}
				SrvCacheFacade.reloadBuffer(getLoginInfo().getSorgcode(),
						StateConstant.CacheTDCrop);
			}
		} catch (Exception e) {
			log.error(e);
			String errMsg = e.getMessage();
			if (errMsg.contains("23505")) {
				errMsg = "��¼�ظ���";
			}
			throw new ITFEBizException("���淨�˴�����Ϣ����,�����У�" + rownum + ",���˴���"
					+ scorpcode + "\n ����ԭ��" + errMsg, e);
		}
	}

	private void tdTbsCrop(List<String[]> listStr) throws ITFEBizException {
		int rownum = 0;
		String scorpcode = "";
		HashMap<String, String> map = new HashMap<String, String>();
		List<IDto> list = new ArrayList<IDto>();
		try {
			TdCorpDto dto = null;
			TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
			tsTreasuryDto.setSorgcode(getLoginInfo().getSorgcode());
			tsTreasuryDto = (TsTreasuryDto) CommonFacade.getODB().findRsByDto(
					tsTreasuryDto).get(0);
			for (int i = 1; i < listStr.size(); i++) {
				rownum = i;
				dto = new TdCorpDto();
				String[] ss = listStr.get(i);
				if (ss == null || ss.length == 0) {
					break;
				}
				// if (ss[0].trim().length() != 12) {
				// throw new ITFEBizException("�����������Ƿ�������ӦΪ12λ��"
				// + ss[0].trim());
				// }
				if (i == 1) {
					tdCorpDelete(getLoginInfo().getSorgcode());
				}
				// ���˴�����Ϣ
				dto.setCtrimflag("0");// �����ڱ�־
				dto.setSbookorgcode(tsTreasuryDto.getSorgcode());// �����������

				dto.setStrecode(tsTreasuryDto.getStrecode());// �����������
				// if (ss[1].trim().length() != 10) {
				// throw new ITFEBizException("�����������Ƿ�������ӦΪ10λ��"
				// + ss[1].trim());
				// }
				scorpcode = ss[0].trim();
				if (map.containsKey(tsTreasuryDto.getStrecode() + "||"
						+ scorpcode)) {
					throw new ITFEBizException("�������+Ԥ�㵥λ�����ظ���"
							+ tsTreasuryDto.getStrecode() + "||" + scorpcode);
				} else {
					map.put(tsTreasuryDto.getStrecode() + "||" + scorpcode, "");
				}
				dto.setScorpcode(scorpcode);// ���˴���_VARCHAR_NOT NULL
				dto.setScorpname(ss[1].trim());// ��������_VARCHAR_NOT NULL
				dto.setScorpsht(ss[2].trim());// ���˼���_VARCHAR
				dto.setCmayaprtfund("1");// �ܷ�ʵ���ʽ�_CHARACTER_NOT
				// NULL
				dto.setCtaxpayerprop(ss[3].trim());// ��λ����_CHARACTER_NOT
				// NULL
				dto.setCpayoutprop("1");// ע������_CHARACTER_NOT NULL
				dto.setCtradeprop("1");// ��ҵ����_CHARACTER_NOT NULL
				// if (ss.length == 10) {
				//
				// if (ss[9] == null || ss[9].trim().equals("")) {
				// // dto.setIacctnum(0);// �˻���_INTEGER_NOT NULL
				// } else {
				// dto.setIacctnum(Integer.parseInt(ss[9].trim()));//
				// �˻���_INTEGER_NOT
				// // NULL
				// }
				// }
				// dto
				// .setTssysupdate(new Timestamp(new java.util.Date()
				// .getTime()));// ϵͳ����ʱ��
				list.add(dto);
				if (list.size() > 0 && list.size() % 1000 == 0) {
					DatabaseFacade.getODB().create(CommonUtil.listTArray(list));
					list = new ArrayList<IDto>();
				}
			}
			if (list.size() > 0) {
				DatabaseFacade.getODB().create(CommonUtil.listTArray(list));
			}
			SrvCacheFacade.reloadBuffer(getLoginInfo().getSorgcode(),
					StateConstant.CacheTDCrop);
		} catch (JAFDatabaseException e) {
			log.error(e);
			String errMsg = e.getMessage();
			if (errMsg.contains("23505")) {
				errMsg = "��¼�ظ���";
			}
			throw new ITFEBizException("���淨�˴�����Ϣ����,�����У�" + rownum + ",���˴���"
					+ scorpcode + "\n ����ԭ��" + errMsg, e);
		} catch (ValidateException e) {
			log.error(e);
			String errMsg = e.getMessage();
			if (errMsg.contains("23505")) {
				errMsg = "��¼�ظ���";
			}
			throw new ITFEBizException("���淨�˴�����Ϣ����,�����У�" + rownum + ",���˴���"
					+ scorpcode + "\n ����ԭ��" + errMsg, e);
		}
	}

	/**
	 * ɾ��-���˴�����Ϣ
	 */
	public void tdCorpDelete(String sbookorgcode) throws ITFEBizException {
		String delSQL = "delete from TD_CORP where S_BOOKORGCODE = ? ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.addParam(sbookorgcode);
			sqlExec.runQueryCloseCon(delSQL);
		} catch (JAFDatabaseException e) {
			log.error("ɾ�����ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("ɾ�����ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	private String change(String ss) {
		if (null == ss || "".equals(ss) || "".equals(ss.trim())) {
			return "";
		}

		return Integer.valueOf(ss).toString();
	}

	/**
	 * �����ؽ������CallShell
	 * 
	 * @param command
	 *            ���õ�shell����
	 * @return ������Ϣ
	 * @throws Exception
	 */
	private byte[] callShellWithRes(String command) throws Exception {

		try {

			log.debug("Shell:" + command);

			Process child = Runtime.getRuntime().exec(command);

			String line = null;
			StringBuffer result = new StringBuffer();
			BufferedReader brIn = new BufferedReader(new InputStreamReader(
					child.getInputStream()));
			InputStream in = child.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((line = brIn.readLine()) != null) {
				result.append(line + "\n");
			}
			brIn.close();

			BufferedReader brErr = new BufferedReader(new InputStreamReader(
					child.getErrorStream()));
			while ((line = brErr.readLine()) != null) {
				result.append(line + "\n");
			}
			brErr.close();
			return result.toString().getBytes();
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.itfe.service.para.tcbsimport.ITCBSimportService#bankInfoImport
	 * (java.util.List)
	 */
	public void bankInfoImport(List list) throws ITFEBizException {
		try {
			banknoDelete();
			bankDataImport(list);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e.toString());
		}

	}

	/**
	 * �к����ݵ���
	 * 
	 * @param listStr
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public void bankDataImport(List<String> listStr)
			throws JAFDatabaseException, ITFEBizException {
		List<IDto> bankcodeList = new ArrayList<IDto>();
		TsPaybankDto dto = null;

		String sbankno = null;
		String sbankname = null;
		String spaybankno = null;
		String spaybankname = null;
		int imodicount = 0;
		String sstate = null;
		String seffdate = null;
		String cbnkclass = null;
		String scitycode = null;
		String cbnkclsno = null;
		String cbnkacctsta = null;
		String cbnkpostcode = null;
		String sbnkaddr = null;
		String copttype = null;
		String sbnktel = null;

		String sorgcode = StateConstant.ORG_CENTER_CODE;
		for (int i = 0; i < listStr.size(); i++) {
			String stri = listStr.get(i);
			if (stri.contains("<CNAPS_BANK>")) {
				if (i == 0)
					banknoDelete();
				dto = new TsPaybankDto();
				dto.setImodicount(imodicount);
			} else if (stri.contains("<CNAPS_BANK_BNKCODE>")) {
				sbankno = stri.replaceAll("<CNAPS_BANK_BNKCODE>", "")
						.replaceAll("</CNAPS_BANK_BNKCODE>", "").trim();
				dto.setSbankno(sbankno);
			} else if (stri.contains("<CNAPS_BANK_LNAME>")) {
				sbankname = stri.replaceAll("<CNAPS_BANK_LNAME>", "")
						.replaceAll("</CNAPS_BANK_LNAME>", "").replaceAll(
								"&nbsp", "").trim();
				dto.setSbankname(sbankname);
			} else if (stri.contains("<CNAPS_BANK_DRECCODE>")) {
				spaybankno = stri.replaceAll("<CNAPS_BANK_DRECCODE>", "")
						.replaceAll("</CNAPS_BANK_DRECCODE>", "").trim();
				dto.setSpaybankno(spaybankno);
			} else if (stri.contains("<CNAPS_BANK_SNAME>")) {
				spaybankname = stri.replaceAll("<CNAPS_BANK_SNAME>", "")
						.replaceAll("</CNAPS_BANK_SNAME>", "").trim();
				dto.setSpaybankname(spaybankname);
			} else if (stri.contains("<CNAPS_BANK_TEL>")) {
				sbnktel = stri.replaceAll("<CNAPS_BANK_TEL>", "").replaceAll(
						"</CNAPS_BANK_TEL>", "").trim();
				dto.setSbnktel(sbnktel);
			} else if (stri.contains("<CNAPS_BANK_ADDR>")) {
				sbnkaddr = stri.replaceAll("<CNAPS_BANK_ADDR>", "").replaceAll(
						"</CNAPS_BANK_ADDR>", "").trim();
				dto.setSbnkaddr(sbnkaddr);
			} else if (stri.contains("<CNAPS_BANK_POSTCODE>")) {
				cbnkpostcode = stri.replaceAll("<CNAPS_BANK_POSTCODE>", "")
						.replaceAll("</CNAPS_BANK_POSTCODE>", "").trim();
				dto.setCbnkpostcode(cbnkpostcode);
			} else if (stri.contains("<CNAPS_BANK_ACCTSTATUS>")) {
				cbnkacctsta = stri.replaceAll("<CNAPS_BANK_ACCTSTATUS>", "")
						.replaceAll("</CNAPS_BANK_ACCTSTATUS>", "").replace(
								"\t", "").trim();
				dto.setCbnkacctsta(cbnkacctsta);
			}

			else if (stri.contains("<CNAPS_BANK_STATUS>")) {
				sstate = stri.replaceAll("<CNAPS_BANK_STATUS>", "").replaceAll(
						"</CNAPS_BANK_STATUS>", "").trim();
				dto.setSstate(sstate);
			} else if (stri.contains("<CNAPS_BANK_CITYCODE>")) {
				scitycode = stri.replaceAll("<CNAPS_BANK_CITYCODE>", "")
						.replaceAll("</CNAPS_BANK_CITYCODE>", "").trim();
				dto.setSofcity(scitycode);

			} else if (stri.contains("<CNAPS_BANK_EFFDATE>")) {
				seffdate = stri.replaceAll("<CNAPS_BANK_EFFDATE>", "")
						.replaceAll("</CNAPS_BANK_EFFDATE>", "").trim();
				if (null != seffdate && seffdate.trim().length() > 0) {
					String _saffdate = seffdate.substring(0, 4) + "-"
							+ seffdate.substring(4, 6) + "-"
							+ seffdate.substring(6, 8);
					Date _daffdate = java.sql.Date.valueOf(_saffdate);
					dto.setDaffdate(_daffdate);
				}
				dto.setSorgcode(sorgcode);
				bankcodeList.add(dto);
				if (bankcodeList.size() > 0 && bankcodeList.size() % 1000 == 0) {
					DatabaseFacade.getODB().create(
							CommonUtil.listTArray(bankcodeList));
					bankcodeList = new ArrayList<IDto>();
				}

			} else {
				log.error("�����к��ļ������ݸ�ʽ����ȷ��");
				throw new ITFEBizException("�����к��ļ����ݸ�ʽ����ȷ��");
			}
		}
		if (bankcodeList.size() > 0) {
			DatabaseFacade.getODB().create(CommonUtil.listTArray(bankcodeList));
		}
	}

	/**
	 * �����˺���Ϣ����
	 * 
	 * @generated
	 * @param filepath
	 * @throws ITFEBizException
	 */
	public void tbsBankImport(String filepath) throws ITFEBizException {
		try {
			/**
			 * ɾ����������
			 */
			banknoDelete();

			/**
			 * ��ʼ����
			 */
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filepath;
			List<String[]> listStr = FileUtil.getInstance().readFileWithLine(
					fileFullName, "@#$%");
			FileUtil.getInstance().deleteFile(fileFullName);
			List<IDto> bankcodeList = new ArrayList<IDto>();
			if (listStr != null && listStr.size() > 0) {
				for (int i = 1; i < listStr.size(); i++) {
					TsPaybankDto dto = new TsPaybankDto();
					String[] tt = listStr.get(i);
					String str = tt[0];
					if (str.contains("\"")) {
						for (int j = 0; j < 5; j++) {
							int k = str.indexOf(",\"");
							int m = str.indexOf("\",");
							if (m > k) {
								str = str.substring(0, k + 1)
										+ str.substring(k + 1, m + 1).replace(
												",", "").replace("\"", "")
										+ str.substring(m + 1, str.length());
							} else {
								str.replace("\"", "");
							}

						}

					}
					String[] ss = str.split(",");

					dto.setSorgcode(getLoginInfo().getSorgcode());
					dto.setSbankno(ss[0]); // �к�
					dto.setSbankname(ss[12]); // ��������
					dto.setSpaybankno(ss[4]); // �������к�
					dto.setSpaybankname(ss[13]);// ����������
					dto.setImodicount(Integer.parseInt(ss[8])); // ���д���
					dto.setSstate(ss[1]); // ״̬
					dto.setDaffdate(CommonUtil.strToDate(ss[18])); // ��Ч����
					// dto.setImodicount(0);
					dto.setCbnkclass(ss[2]);
					dto.setSofcity(ss[8]);
					dto.setCbnkclsno(ss[3]);
					dto.setCbnkacctsta(ss[9]);
					dto.setCbnkpostcode(ss[15]);
					String saddr = "";
					if (null != ss[14]) {
						saddr = ss[14].replace("\t", "").replace(" ", "");
					} else {
						saddr = "";
					}
					dto.setSbnkaddr(saddr);
					dto.setSbnktel(ss[16]);
					bankcodeList.add(dto);
					if (bankcodeList.size() > 0
							&& bankcodeList.size() % 1000 == 0) {
						DatabaseFacade.getODB().create(
								CommonUtil.listTArray(bankcodeList));
						bankcodeList = new ArrayList<IDto>();
					}
				}
				if (bankcodeList.size() > 0) {
					DatabaseFacade.getODB().create(
							CommonUtil.listTArray(bankcodeList));
				}
			}

			SrvCacheFacade.reloadBuffer(getLoginInfo().getSorgcode(),
					StateConstant.CacheBdgSbt);
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("��ȡ�ļ�����", e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ���Ŀά������ʱ��������", e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������ļ�ʱ�Ҳ���ָ�����ļ�", e);
		}
	}

	public static void main(String[] args) throws Exception {
		TCBSimportService importserver = new TCBSimportService();
		String filePath = "F:/20090611���.XML";
		importserver.banknoImport(filePath);
	}

	public void paramImport(String filepath, String tabcode)
			throws ITFEBizException {
		IParamInOut paramImporter = getIParamInOutImpl(tabcode);
		try {
			Map param = new HashMap();
			param
					.put(ParamConstant.ORGCODE, super.getLoginInfo()
							.getSorgcode());
			paramdelte(tabcode);
			paramImporter.importParam(filepath, param);
			paramgl(tabcode);
		} catch (Exception e) {
			log.error(filepath, e);
			throw new ITFEBizException(filepath, e);
		}
	}

	/**
	 * ɾ��ԭ�л����²���
	 * 
	 * @param tabcode
	 * @throws ITFEBizException
	 */
	private void paramdelte(String tabcode) throws ITFEBizException {
		try {
			String[][] pkarray = { { "TS_TREASURY", "S_ORGCODE" },
					{ "TS_TAXORG", "S_ORGCODE" },
					{ "TD_TAXORG_PARAM", "S_BOOKORGCODE" },
					{ "TS_BUDGETSUBJECT", "S_ORGCODE" },
					{ "TP_SHARE_DIVIDE", "S_BOOKORGCODE" },
					{ "TD_BANK", "S_BOOKORGCODE" },
					{ "TD_BOOKACCT_MAIN", "S_BOOKORGCODE" },
					{ "TD_CORP", "S_BOOKORGCODE" },
					{ "TD_CORPACCT", "S_BOOKORGCODE" },
					{ "TD_BOOKSBT", "S_BOOKORGCODE" },
					{ "TS_PAYBANK", "S_ORGCODE" },
					{ "TN_CONPAYCHECKBILL", "S_BOOKORGCODE" },
					{ "TS_CORRREASON", "S_BOOKORGCODE" },
					{ "TS_DRAWBACKREASON", "S_BOOKORGCODE" },
					{ "TS_CONVERTASSITSIGN", "S_ORGCODE" } };
			Map<String, String> pkmap = ArrayUtils.toMap(pkarray);
			Class cls = CommonUtil.tableConverDto(tabcode);
			DatabaseFacade.getODB().delete(
					cls,
					" " + pkmap.get(tabcode) + " = '"
							+ getLoginInfo().getSorgcode() + "' ", null);
			/*
			 * if(tabcode.equals("TD_TAXORG_PARAM")){
			 * DatabaseFacade.getODB().delete(TdTaxorgParamDto.class,
			 * " S_BOOKORGCODE = '"+getLoginInfo().getSorgcode()+"' ", null);
			 * }else if(tabcode.equals("TD_CORPACCT")){
			 * DatabaseFacade.getODB().delete(TdCorpacctDto.class,
			 * " S_BOOKORGCODE = '"+getLoginInfo().getSorgcode()+"' ", null);
			 * }else if(tabcode.equals("TS_CONVERTASSITSIGN")){
			 * DatabaseFacade.getODB().delete(TsConvertassitsignDto.class,
			 * " S_ORGCODE = '"+getLoginInfo().getSorgcode()+"' ", null); }else
			 * if(tabcode.equals("TD_BOOKACCT_MAIN")){
			 * DatabaseFacade.getODB().delete(TdBookacctMainDto.class,
			 * " S_BOOKORGCODE = '"+getLoginInfo().getSorgcode()+"' ", null); }
			 */
		} catch (JAFDatabaseException ex) {
			log.error("ɾ��" + tabcode + "����ʱ�����쳣:" + ex);
			throw new ITFEBizException("ɾ��" + tabcode + "����ʱ�����쳣:" + ex);
		} catch (ClassNotFoundException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}

	}

	private void paramgl(String tabcode) throws ITFEBizException {
		try {
			if (tabcode.equals("TS_TAXORG")) {
				HashMap<String, TdTaxorgParamDto> tdtaxorgmap = new HashMap<String, TdTaxorgParamDto>();
				List<TdTaxorgParamDto> tdTaxorgParamlist = DatabaseFacade.getODB()
						.find(
								TdTaxorgParamDto.class,
								" WHERE S_BOOKORGCODE = '"
										+ getLoginInfo().getSorgcode() + "'");
				HashMap<String, TsTaxorgDto> tstaxorgmap = new HashMap<String, TsTaxorgDto>();
				List<TsTaxorgDto> tstaxorglist = DatabaseFacade.getODB().find(
						TsTaxorgDto.class,
						" WHERE S_ORGCODE = '" + getLoginInfo().getSorgcode()
								+ "'");
				if (tstaxorglist != null && tstaxorglist.size() > 0
						&& tdTaxorgParamlist != null && tdTaxorgParamlist.size() > 0) {
					for (TdTaxorgParamDto dto : tdTaxorgParamlist) {
						tdtaxorgmap.put(dto.getStaxorgcode(), dto);
					}
					for (TsTaxorgDto dto : tstaxorglist) {
						TdTaxorgParamDto tmptdtx = tdtaxorgmap.get(dto
								.getStaxorgcode());
						if (tmptdtx != null) {
							dto.setStaxorgname(tmptdtx.getStaxorgname());
							dto.setStaxprop(tmptdtx.getCtaxorgprop());
						}
					}
					TsTaxorgDto[] utstaxorgs = new TsTaxorgDto[tstaxorglist
							.size()];
					utstaxorgs = tstaxorglist.toArray(utstaxorgs);
					DatabaseFacade.getODB().update(utstaxorgs);
				}

			}
		} catch (JAFDatabaseException ex) {
			log.error("ɾ��" + tabcode + "����ʱ�����쳣:" + ex);
			throw new ITFEBizException("ɾ��" + tabcode + "����ʱ�����쳣:" + ex);
		}

	}

	private IParamInOut getIParamInOutImpl(String tabcode)
			throws ITFEBizException {
		tabcode = tabcode.replaceAll("_", "").toUpperCase();
		try {
			return (IParamInOut) Class.forName(
					ParamConstant.DEFAULTPARAMPREFIX + tabcode).newInstance();
		} catch (Exception e) {
			log.error("�޷���ʼ��ҵ������", e);
			throw new ITFEBizException("�޷���ʼ��ҵ������", e);
		}
	}

	/**
	 * ���������кŵ���
	 */
	public void bankCodeImport(String filePath) throws ITFEBizException {
		try {
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			// ͨ���ַ����밴�ж�ȡ����Ķ���֧���к��ļ�
			List<String> list = FileUtil.getInstance()
					.readFileWithLineForEncoding(fileFullName);
			// ����������к��ļ������������
			List<IDto> bankCodeList = bankDataImportForSecond(list);
			if (bankCodeList.size() == 1) {
				TsPaybankDto dto = (TsPaybankDto) bankCodeList.get(0);
				if (dto.getSbankno() == null) {
					log.error("�����ļ��д����к�Ϊ�յ��ֶΣ�");
					throw new ITFEBizException("�����ļ��д����к�Ϊ�յ��ֶΣ�");
				} else if (dto.getSbankno().equals("")) {
					log.error("�����ļ��д����кŲ���12λ���ֵ��ֶΣ�");
					throw new ITFEBizException("�����ļ��д����кŲ���12λ���ֵ��ֶΣ�");
				}
			}
			// ����Ķ����к��벹¼���к���ȶ�,����ɾ�������ڵ��Ѿ���¼���к�
			compareBnakCode(bankCodeList);
			// ɾ���ϴ��Ķ���֧���к��ļ�
			FileUtil.getInstance().deleteFile(fileFullName);
			// ˢ�»���
			SrvCacheFacade.reloadBuffer(getLoginInfo().getSorgcode(),
					StateConstant.CachePayBank);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e.toString());
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException(e.toString());
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException(e.toString());
		}

	}

	/**
	 * ����Ķ����к��벹¼���к���ȶ�, �������Ķ����к��ﲻ�����Ѿ���¼���к�,��ɾ��������¼���к�
	 * 
	 * @param bankCodeList
	 * @throws JAFDatabaseException
	 */
	@SuppressWarnings("unchecked")
	public void compareBnakCode(List<IDto> bankCodeList)
			throws JAFDatabaseException {
		if(bankCodeList==null||bankCodeList.size()<=0)
			return;
		// ��ò�¼�������к�
		TsConvertbanknameDto bankNameDto = new TsConvertbanknameDto();
		List<TsConvertbanknameDto> bankNameList = DatabaseFacade.getODB().find(
				bankNameDto.getClass());
		// ��ò����ڵ��Ѿ���¼���к��б�
		List<IDto> deleteBankNameList = new ArrayList<IDto>();
		// ��õ���Ķ����к��б�
		List<String> tsPaybankList = new ArrayList<String>();
		// ����Ķ����к��벹¼���к���ȶ�
		for (IDto dto : bankCodeList) {
			TsPaybankDto payBankDto = (TsPaybankDto) dto;
			tsPaybankList.add(payBankDto.getSbankno());
		}
		for (TsConvertbanknameDto tsConvertbanknameDto : bankNameList) {
			if (!tsPaybankList.contains(tsConvertbanknameDto.getSbankcode())) {
				deleteBankNameList.add(tsConvertbanknameDto);
			}
		}
		// ɾ�������ڵ��Ѿ���¼���к�
		DatabaseFacade.getODB().delete(
				CommonUtil.listTArray(deleteBankNameList));
	}

	/**
	 * ���������к����ݵ���
	 * 
	 * @param listStr
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException 
	 */
	public List<IDto> bankDataImportForSecond(List<String> listStr)
			throws JAFDatabaseException, ITFEBizException {
		List<IDto> bankcodeList = new ArrayList<IDto>();
		List<IDto> tsPaybankDtos = new ArrayList<IDto>();
		TsPaybankDto dto = null;

		/**
		 * �к� *
		 */
		String sbankno = null;
		/**
		 * ���� *
		 */
		String sbankname = null;
		/**
		 * �������к� *
		 */
		String spaybankno = null;
		/**
		 * �޸Ĵ��� *
		 */
		int imodicount = 0;
		/**
		 * ��Ч���� *
		 */
		String seffdate = null;
		/**
		 * ��� *
		 */
		String cbnkclass = null;
		/**
		 * ���ڳ��� *
		 */
		String scitycode = null;
		/**
		 * �б�״̬ *
		 */
		String cbnkclsno = null;
		/**
		 * �б���Ч״̬ *
		 */
		String cbnkstate = null;

		String sorgcode = StateConstant.ORG_CENTER_CODE;
		// ���Ƚ�����������к������
		banknoDelete();
		for (int i = 0; i < listStr.size(); i++) {
			String stri = listStr.get(i);
			// ֧��TCBS�������кű�
			if (stri.contains("<ROW>") || stri.contains("<CNAPS_BANK>")) {
				dto = new TsPaybankDto();
				dto.setImodicount(imodicount);
			}
			if (stri.contains("<BANKCODE>")) {
				sbankno = stri.replaceAll("<BANKCODE>", "").replaceAll(
						"</BANKCODE>", "").trim();
				dto.setSbankno(sbankno);
			} else if (stri.contains("<CNAPS_BANK_BNKCODE>")) {
				sbankno = stri.replaceAll("<CNAPS_BANK_BNKCODE>", "")
						.replaceAll("</CNAPS_BANK_BNKCODE>", "").trim();
				dto.setSbankno(sbankno);
			}
			if ("314307100196".equals(sbankno)) {
				System.out.println("aaaaa");
			}
			if (stri.contains("<BANKCODE>")
					|| stri.contains("<CNAPS_BANK_BNKCODE>")) {
				// �ж��к��Ƿ�Ϊ��
				if (sbankno == null || sbankno.equals("")) {
					TsPaybankDto checkDto = new TsPaybankDto();
					checkDto.setSbankno(null);
//					bankcodeList.add(checkDto);
//					return bankcodeList;
					continue;
				} else if (!match(sbankno)) {// У���к�ֻ��Ϊ12λ����
					TsPaybankDto checkDto = new TsPaybankDto();
					checkDto.setSbankno("");
//					bankcodeList.add(checkDto);
//					return bankcodeList;
					continue;
				}
			}
			if (stri.contains("<BANKNAME>")) {
				sbankname = stri.replaceAll("<BANKNAME>", "").replaceAll(
						"</BANKNAME>", "").replaceAll("&nbsp", "").trim();
				dto.setSbankname(sbankname);

			} else if (stri.contains("<CNAPS_BANK_LNAME>")) {
				sbankname = stri.replaceAll("<CNAPS_BANK_LNAME>", "")
						.replaceAll("</CNAPS_BANK_LNAME>", "").replaceAll(
								"&nbsp", "").trim();
				dto.setSbankname(sbankname);
			}

			if (stri.contains("<CNAPS_BANK_DRECCODE>")) {
				spaybankno = stri.replaceAll("<CNAPS_BANK_DRECCODE>", "")
						.replaceAll("</CNAPS_BANK_DRECCODE>", "").trim();
				dto.setSpaybankno(spaybankno);
			} else if (stri.contains("<DRECCODE>")) {
				spaybankno = stri.replaceAll("<DRECCODE>", "").replaceAll(
						"</DRECCODE>", "").trim();
				dto.setSpaybankno(spaybankno);
			}

			if (stri.contains("<BANKCATALOG>")) {
				cbnkclass = stri.replaceAll("<BANKCATALOG>", "").replaceAll(
						"</BANKCATALOG>", "").trim();
				dto.setCbnkclass(cbnkclass);
			} else if (stri.contains("<CNAPS_BANK_CATEGORY>")) {
				cbnkclass = stri.replaceAll("<CNAPS_BANK_CATEGORY>", "")
						.replaceAll("</CNAPS_BANK_CATEGORY>", "").trim();
				dto.setCbnkclass(cbnkclass);
			}

			if (stri.contains("<BANKTYPE>")) {
				cbnkclsno = stri.replaceAll("<BANKTYPE>", "").replaceAll(
						"</BANKTYPE>", "").trim();
				dto.setCbnkclsno(cbnkclsno);
			} else if (stri.contains("<CNAPS_BANK_CLSCODE>")) {
				cbnkclsno = stri.replaceAll("<CNAPS_BANK_CLSCODE>", "")
						.replaceAll("</CNAPS_BANK_CLSCODE>", "").trim();
				dto.setCbnkclsno(cbnkclsno);
			}

			if (stri.contains("<DEBTORCITY>")) {
				scitycode = stri.replaceAll("<DEBTORCITY>", "").replaceAll(
						"</DEBTORCITY>", "").trim();
				dto.setSofcity(scitycode);
			} else if (stri.contains("<CNAPS_BANK_CITYCODE>")) {
				scitycode = stri.replaceAll("<CNAPS_BANK_CITYCODE>", "")
						.replaceAll("</CNAPS_BANK_CITYCODE>", "").trim();
				dto.setSofcity(scitycode);
			}

			if (stri.contains("<CNAPS_BANK_STATUS>")) {
				cbnkstate = stri.replaceAll("<CNAPS_BANK_STATUS>", "")
						.replaceAll("</CNAPS_BANK_STATUS>", "").trim();
				dto.setSstate(cbnkstate);// ״̬(0��Чǰ��1��Ч��2ע��)

			} else {
				if (null != dto) {
					dto.setSstate(StateConstant.COMMON_YES);// ״̬(0��Чǰ��1��Ч��2ע��)
				}
			}
			if (stri.contains("<EFFECTDATE>")) {
				seffdate = stri.replaceAll("<EFFECTDATE>", "").replaceAll(
						"</EFFECTDATE>", "").trim();
				if (null != seffdate && seffdate.trim().length() > 0) {
					String _saffdate = seffdate.substring(0, 4) + "-"
							+ seffdate.substring(4, 6) + "-"
							+ seffdate.substring(6, 8);
					Date _daffdate = java.sql.Date.valueOf(_saffdate);
					dto.setDaffdate(_daffdate);
					dto.setSorgcode(sorgcode);
					if (sbankno != null && !sbankno.equals("")) {
						bankcodeList.add(dto);
						tsPaybankDtos.add(dto);
					}
				}
			} else if (stri.contains("<CNAPS_BANK_EFFDATE>")) {
				seffdate = stri.replaceAll("<CNAPS_BANK_EFFDATE>", "")
						.replaceAll("</CNAPS_BANK_EFFDATE>", "").trim();
				if (null != seffdate && seffdate.trim().length() > 0) {
					String _saffdate = seffdate.substring(0, 4) + "-"
							+ seffdate.substring(4, 6) + "-"
							+ seffdate.substring(6, 8);
					Date _daffdate = java.sql.Date.valueOf(_saffdate);
					dto.setDaffdate(_daffdate);
					dto.setSorgcode(sorgcode);
					if (sbankno != null && !sbankno.equals("")) {
						bankcodeList.add(dto);
						tsPaybankDtos.add(dto);
					}
				}
			}

			if (bankcodeList.size() > 0 && bankcodeList.size() % 1000 == 0) {
				DatabaseFacade.getODB().create(
						CommonUtil.listTArray(bankcodeList));
				bankcodeList = new ArrayList<IDto>();
			}

		}

		if (bankcodeList.size() > 0) {
			DatabaseFacade.getODB().create(CommonUtil.listTArray(bankcodeList));
		}
		return tsPaybankDtos;

	}

	public boolean match(String str) {
		Pattern pattern = Pattern.compile("[0-9]{12}");// ƥ��12λ����
		Matcher match = pattern.matcher(str);
		return match.matches();
	}
	/**
	 * �ṩ���������������֤
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyTreasury(String trecode, String orgcode)
			throws ITFEBizException {
		HashMap<String, TsTreasuryDto> map ;
		HashMap<String, TsTreasuryDto> newMap = new HashMap<String, TsTreasuryDto>();
		try {
			map = SrvCacheFacade.cacheTreasuryInfo(orgcode);
			Set<Map.Entry<String, TsTreasuryDto>> set = map.entrySet();
			if(orgcode!=null&&!orgcode.equals("")){
				for (Iterator<Map.Entry<String, TsTreasuryDto>> it = set.iterator(); it.hasNext();) {
					Map.Entry<String, TsTreasuryDto> entry = (Map.Entry<String, TsTreasuryDto>) it.next();
					newMap.put(entry.getKey() + entry.getValue().getSorgcode(), entry.getValue());
				}
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		}
		
		if (newMap != null && newMap.containsKey(trecode + orgcode)) {
			return true;
		} else {
			return false;
		}
	}
}