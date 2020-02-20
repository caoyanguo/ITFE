package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForGZTest extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());
	private static final int MAX_NUM = 300000; // ÿ��ȡ������¼��

	public String makeReportByBiz(TrIncomedayrptDto idto, String bizType,
			String sbookorgcode) throws ITFEBizException {

		// �����������
		String strecode = idto.getStrecode();
		// Ԥ������
		String bugtype = idto.getSbudgettype();
		// Ͻ����־
		String sbelong = idto.getSbelongflag();
		// �����ڱ�־
		String strimflag = idto.getStrimflag();
		// ����
		String srptdate = idto.getSrptdate();
		// �Ƿ񺬿�ϼ�
		String slesumitem = idto.getSdividegroup();
		// �����ѯ����
		String sqlWhere = CommonMakeReport.makesqlwhere(idto);

		String filename = CommonMakeReport.getExpFileNameByBillType(idto,
				bizType);// 
		String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep
				+ filename;
		SQLExecutor sqlExec = null;
		try {
			int count = 0;
			String sql = " where S_TRECODE = ? and s_intredate= ? with ur";
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.setMaxRows(MAX_NUM);
			List<TvFinIncomeDetailDto> resVouList = (List<TvFinIncomeDetailDto>) sqlExec
					.runQueryCloseCon(sql, TvFinIncomeDetailDto.class)
					.getDtoCollection();
			StringBuffer filebuf = new StringBuffer(
					"skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
			HashMap<String, String> taxMap = super.converTaxCode(sbookorgcode);
			if (resVouList.size() > 0) {
				// �õ�����ļ�����
				String splitSign = ","; // �ļ���¼�ָ�����
				for (TvFinIncomeDetailDto _dto : resVouList) {
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbdglevel());
					filebuf.append(splitSign);
					String staxorgcode = "";
					if (taxMap.containsKey(_dto.getStaxorgcode())) {
						staxorgcode = taxMap.get(_dto.getStaxorgcode());
					} else {
						throw new ITFEBizException("�����������" + sbookorgcode
								+ "��TCBS���ջ��ش��루" + _dto.getStaxorgcode()
								+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
					}
					filebuf.append(_dto.getStaxorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(srptdate);
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbdgkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSexpvouno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append("1");
					filebuf.append(splitSign);
					filebuf.append("");
					filebuf.append(splitSign);
					filebuf.append(_dto.getFamt());
					filebuf.append("\r\n");
				}

			} else {
				return null;
			}
			FileUtil.getInstance().writeFile(fullpath, filebuf.toString());

			return fullpath;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ������ƾ֤����" + filename, e);
		} finally {
			if (sqlExec != null) {
				try {
					sqlExec.closeConnection();
				} catch (Exception e) {
					log.error("�ر����ӳ���", e);
				}
			}
		}
	}
}
