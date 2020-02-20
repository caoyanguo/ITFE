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
import com.cfcc.jaf.persistence.jdbc.sql.SQLBatchRetriever;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForGZ extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());
	private static final int MAX_NUM = 10000; // ÿ��ȡ������¼��

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
		String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		String filename ="";
		SQLBatchRetriever batchRetriever = null;
		try {
			HashMap<String, String> taxMap = super.converTaxCode(sbookorgcode);
			filename = CommonMakeReport.getExpFileNameByBillTypeForGZ(idto,
					bizType,taxMap);// 
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep+ filename;
			File f = new File(fullpath);
			if (f.exists()) {
				f.delete();
			}
			String sql = " Select * from TV_FIN_INCOME_DETAIL where S_TRECODE = ? and s_intredate= ? with ur";
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			batchRetriever.addParam(strecode); // �������
			batchRetriever.addParam(srptdate); // ��������
			batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������

			batchRetriever.runQuery(sql);
			StringBuffer filebuf = new StringBuffer();
			filebuf.append("skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
			while (batchRetriever.hasMore()) {
				SQLResults result = batchRetriever.RetrieveNextBatch(); 
				int count = result.getRowCount();
				if (count > 0) {
					// �õ�����ļ�����
					String splitSign = ","; // �ļ���¼�ָ�����
					for (int i=0;i< count ;i++) {
						filebuf.append(result.getString(i, 1));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 1));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 6));
						filebuf.append(splitSign);
						String staxorgcode = "";
						if (taxMap.containsKey(result.getString(i, 11))) {
							staxorgcode = taxMap.get(result.getString(i, 11));
						} else {
							throw new ITFEBizException("�����������" + sbookorgcode
									+ "��TCBS���ջ��ش��루" + result.getString(i, 11)
									+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
						}
						filebuf.append(staxorgcode);
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 7));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 2));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 8));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 4));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 1));
						filebuf.append(splitSign);
						filebuf.append("1");
						filebuf.append(splitSign);
						filebuf.append("");
						filebuf.append(splitSign);
						filebuf.append(result.getBigDecimal(i, 9));
						filebuf.append("\r\n");
					}

					FileUtil.getInstance().writeFile(fullpath,
							filebuf.toString(),true);
					filebuf = new StringBuffer();
				} else {
					return null;
				}
			}

			return fullpath;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ������ƾ֤����" + filename, e);
		} finally {
			if (batchRetriever != null) {
				try {
					batchRetriever.closeConnection();
				} catch (Exception e) {
					log.error("�ر����ӳ���", e);
				}
			}
		}
	}
}
