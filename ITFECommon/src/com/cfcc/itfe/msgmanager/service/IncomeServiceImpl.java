package com.cfcc.itfe.msgmanager.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractServiceManagerServer;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpCountryDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpPlaceDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpTipsDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.verify.VerifyCallShellRs;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLBatchRetriever;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class IncomeServiceImpl extends AbstractServiceManagerServer {

	private static Log logger = LogFactory.getLog(IncomeServiceImpl.class);

	/**
	 * ����ӿͻ��˴��͹������ļ�����(����˰Ʊҵ����)
	 * 
	 * @param FileResultDto
	 *            fileResultDto �ļ����������DTO
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public void dealFileDto(FileResultDto fileResultDto, String sorgcode,
			String susercode) throws ITFEBizException {

		try {
			// ��һ��У�鵼���ļ����ظ���
			boolean repeatflag = VerifyFileName.verifyImportRepeat(sorgcode,fileResultDto.getSfilename());
			if (repeatflag) {
				// ���ظ�����,��ʾ�û�������Ϣ.
				logger.error("���ļ�[" + fileResultDto.getSfilename() + "]�Ѿ�����ɹ�,�����ظ�����!");
				throw new ITFEBizException("���ļ�["	 + fileResultDto.getSfilename() + "]�Ѿ�����ɹ�,�����ظ�����!");
			}
			Integer iscollect = 0;
			if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(fileResultDto.getArea())) {
				iscollect = Integer.valueOf(fileResultDto.getIscollect());
			} else {
				TsSystemDto sysdto = new TsSystemDto();
				List<TsSystemDto> syslist = CommonFacade.getODB().findRsByDto(sysdto);
				if (null == syslist || syslist.size() == 0) {
					logger.error("��ѯϵͳ���������û���ҵ���Ӧ�Ļ�������!");
					throw new ITFEBizException("��ѯϵͳ���������û���ҵ���Ӧ�Ļ�������!");
				}
				iscollect = syslist.get(0).getImodicount();
			}

			if (MsgConstant.MSG_SOURCE_PLACE.equals(fileResultDto.getCsourceflag())) {
				dealPlaceData(fileResultDto, sorgcode, susercode, iscollect);
			} else if (MsgConstant.MSG_SOURCE_NATION.equals(fileResultDto.getCsourceflag())) {
				dealNationData(fileResultDto, sorgcode, susercode, iscollect);
			} else if (MsgConstant.MSG_SOURCE_TBS.equals(fileResultDto.getCsourceflag())) {
				if(sorgcode.startsWith("11")){
					dealTBSDataForZJ(fileResultDto, sorgcode, susercode, iscollect);
				}else{
					dealTBSData(fileResultDto, sorgcode, susercode, iscollect);
				}
			} else if (MsgConstant.MSG_SOURCE_TIPS.equals(fileResultDto.getCsourceflag())) {
				dealTIPSData(fileResultDto, sorgcode, susercode, iscollect);
			} else {
				// ���ظ�����,��ʾ�û�������Ϣ.
				logger.error("�ļ���Դ��־[" + fileResultDto.getCsourceflag() + "]����!");
				throw new ITFEBizException("�ļ���Դ��־[" + fileResultDto.getCsourceflag() + "]����!");
			}
		} catch (JAFDatabaseException e) {
			logger.error("���ݰ��˵�ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("���ݰ��˵�ʱ��������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
		}
	}

	/**
	 * ����TBS�ӿ�����
	 * 
	 * @param FileResultDto
	 *            fileResultDto �����ļ��ӿڶ���
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	public void dealTBSData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {
		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��
		FileObjDto fileobj = PublicSearchFacade
				.getFileObjByFileName(fileResultDto.getSfilename());
		String filetime = fileobj.getSdate(); // �ļ�����
		String ctrimflag = fileobj.getCtrimflag(); // �����ڱ�־

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + currentDate
				+ dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
		String absoluteSql = absolutePath + ".sql";

		SQLBatchRetriever batchRetriever = null; // ������ѯҪ�õ�

		// ��һ��У��ƾ֤����Ƿ��ظ�
		List<String> voulist = new ArrayList<String>(); // ��ʼ��ƾ֤���LIST
		String mainInfo = fileResultDto.getSmaininfo();
		String[] lineInfo = mainInfo.split("\r\n");
		for (int i = 0; i < lineInfo.length; i++) {
			String[] arr = lineInfo[i].split(StateConstant.INCOME_SPLIT);
			if (arr[4] != null && !"".equals(arr[4])) {
				voulist.add(arr[3] + "," + arr[4]);
			}
		}
		if (!StateConstant.SPECIAL_AREA_GUANGDONG.equals(fileResultDto
				.getArea())) {
			// �ǻ��ܷ�ʽУ�� add by zhuguozhen 20120509
			if (null != iscollect && iscollect != 0) {
				checkIncomeVouRepeat(sorgcode, voulist, fileResultDto
						.getSfilename());
			}
		}

		try {
			// �ڶ��� �洢�ļ�
			FileUtil.getInstance().writeFile(absolutePath,
					fileResultDto.getSmaininfo());

			// ������ ����SHELL���ļ�IMPORT�����ݿ� ��ʱ��TV_INFILE_TMP (��ɾ��Ȼ�����)
			String importcontent = PublicSearchFacade.getSqlConnectByProp()
					+ "delete from TV_INFILE_TMP where S_ORGCODE = '"
					+ sorgcode
					+ "' and S_FILENAME = '"
					+ fileResultDto.getSfilename()
					+ "';\n"
					+ "import from "
					+ absolutePath
					+ " of del modified by compound=100 insert into TV_INFILE_TMP;";
			FileUtil.getInstance().writeFile(absoluteSql, importcontent);
			String results = null;
			try {
				byte[] bytes = null;
				// if(!isWin()){
				// String command =
				// "su - db2inst1 -c \"/home/db2inst1/sqllib/bin/db2 -tvf ";
				// command = command + absoluteSql + "\"";
				// bytes = CallShellUtil.callShellWithRes(command);
				// }else{
				// bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				// }
				bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0,
							MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				logger.debug("����SHELL���:" + results);

				bytes = null;
			} catch (Exception e) {
				logger.error("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
				throw new ITFEBizException("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
			}

			// ���Ĳ� �жϵ���SHELL�Ĵ�����,��֤ȫ�����ɹ�.���ܳ��ֵĴ���:����ת������,���ݱ��ض�
			VerifyCallShellRs.verifyShellForIncomeRs(results);

			// ���岽 ������У�� 1 ���ջ��ش��� 2 ������־
			if (!ITFECommonConstant.ISCONVERT.equals("0")) {
				VerifyParamTrans.verifyTBSIncomeTrans(sorgcode, fileResultDto
						.getSfilename());
			} else {
				VerifyParamTrans.SetTCBSIncomeTrans(sorgcode, fileResultDto
						.getSfilename());
			}
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_TBS, sorgcode,
					fileResultDto.getSfilename(), null);
			// ���⼶����Ԥ�㼶�ε�У��
			VerifyParamTrans.verifyTreasuryLevel(sorgcode, fileResultDto
					.getSfilename(), ITFECommonConstant.IFVERIFYTRELEVEL);

			// ������ �����ļ�����,��������,�����������ջ��ش������(ע��Ҫȥ������ҵ��)
			List<FileObjDto> taxlist = new ArrayList<FileObjDto>();

			// String selectSQL =
			// "select S_RECVTRECODE,S_TAXORGCODE from TV_INFILE_TMP where S_RECVTRECODE = S_DESCTRECODE "
			// +
			// "and S_FILENAME = ? and S_ORGCODE = ? group by S_RECVTRECODE,S_TAXORGCODE";
			/**
			 * ���Ӷ��տ�����Ŀ�Ĺ��ⲻһ�µ����У�飬�������׳��쳣 20130609
			 */
			String tmpSql = "select count(*) from TV_INFILE_TMP "
					+ "	where "
					+ " (S_DESCTRECODE IS NOT NULL) AND (S_RECVTRECODE <> S_DESCTRECODE) "
					+ " AND S_FILENAME = ? and S_ORGCODE = ? ";
			SQLExecutor sqlExec_ = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			sqlExec_.addParam(fileResultDto.getSfilename());
			sqlExec_.addParam(sorgcode);
			SQLResults taxorgRs_ = sqlExec_.runQueryCloseCon(tmpSql);
			if (taxorgRs_.getInt(0, 0) > 0) {
				logger.error("�����ļ�[" + fileResultDto.getSfilename()
						+ "]�д����տ������Ŀ�Ĺ��ⲻһ�¼�¼!");
				throw new ITFEBizException("�����ļ�["
						+ fileResultDto.getSfilename() + "]�д����տ������Ŀ�Ĺ��ⲻһ�¼�¼!");
			}

			/**
			 * �޸�Ϊ���Ŀ�Ĺ���Ϊ����У����տ������� �޸����ڣ�20120906
			 */
			String selectSQL = "select S_RECVTRECODE,S_TAXORGCODE from TV_INFILE_TMP "
					+ "	where "
					+ " ((S_DESCTRECODE IS NOT NULL AND S_RECVTRECODE = S_DESCTRECODE) OR (S_DESCTRECODE IS NULL)) and S_FILENAME = ? and S_ORGCODE = ? "
					+ " group by S_RECVTRECODE,S_TAXORGCODE";
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults taxorgRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = taxorgRs.getRowCount();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = new FileObjDto();
				tmpdto.setStrecode(taxorgRs.getString(i, 0));
				tmpdto.setStaxorgcode(taxorgRs.getString(i, 1));

				taxlist.add(tmpdto);
			}

			if (count == 0) {
				logger.error("û���ҵ����������ļ�¼(�������տ������Ŀ�Ĺ��ⲻһ��)!");
				throw new ITFEBizException("û���ҵ����������ļ�¼(�������տ������Ŀ�Ĺ��ⲻһ��)!");
			}

			// ����һ�� ������쳣������ҪУ���ļ����ƥ��
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_MONEY) as N_MONEY from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("�쳣�������ļ��ܽ��[" + famt + "]��¼�����ȣ�");
						throw new ITFEBizException("�쳣�������ļ��ܽ��[" + famt
								+ "]��¼�����ȣ�");
					}
				} catch (JAFDatabaseException e) {
					logger.error("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
					throw new ITFEBizException("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// ���߲� ���ݷ�������ȥ���.

			List<String> packList = new ArrayList<String>();
			String tmpfilesql = null;
			String tmpDetailsql = null;

			// ����ʱȥ������λ������ܣ���λ������7211������û��ʹ�ø��ֶΣ�������߻�������
			if (null == iscollect || iscollect == 0) {
				// �ǻ��ܴ���
				// tmpfilesql =
				// "select S_TBS_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND "
				// +
				// " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
				// +
				// " group by S_TBS_TAXORGCODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND";
				tmpfilesql = "select S_TBS_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_OPENACCBANKCODE,S_PAYBOOKKIND "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
						+ " group by S_TBS_TAXORGCODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_OPENACCBANKCODE,S_PAYBOOKKIND";

				// ��ϸ��Ϣ
				tmpDetailsql = "select N_MONEY,S_ASSITSIGN,S_TBS_ASSITSIGN,S_DEALNO,S_TAXTICKETNO,S_DESCTRECODE "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
						+ " and S_TBS_TAXORGCODE = ? and S_BUDGETTYPE = ? and S_BUDGETSUBCODE = ? and S_BUDGETLEVELCODE = ? ";
			} else {
				// ������ϸ����
				tmpfilesql = "select S_TBS_TAXORGCODE,N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_OPENACCBANKCODE,S_PAYBOOKKIND,S_DEALNO,S_TAXTICKETNO,S_DESCTRECODE,S_UNITCODE "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? ";
			}
			// //
			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			SQLExecutor sqlDetail = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = taxlist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // �������
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // ���ջ��ش���
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPSÿ����������

				batchRetriever.runQuery(tmpfilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						List<TvInfileDetailDto> listDetail = new ArrayList<TvInfileDetailDto>();
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							TvInfileDetailDto mainDto = new TvInfileDetailDto();
							dtos[j] = new TvInfileDto();

							String stbstaxorgcode = result.getString(j, k++);
							dtos[j].setStbstaxorgcode(stbstaxorgcode); // TBS���ջ��ش���
							BigDecimal nmoney = result.getBigDecimal(j, k++);
							dtos[j].setNmoney(nmoney); // ���׽��
							String sbudgettype = result.getString(j, k++);
							dtos[j].setSbudgettype(sbudgettype); // Ԥ������
							String sbudgetsubcode = result.getString(j, k++);
							dtos[j].setSbudgetsubcode(sbudgetsubcode); // Ԥ���Ŀ����
							String sbudgetlevelcode = result.getString(j, k++);
							dtos[j].setSbudgetlevelcode(sbudgetlevelcode); // Ԥ�㼶��
							String sassitsign = result.getString(j, k++);
							String stbsassitsig = result.getString(j, k++);
							if (ITFECommonConstant.ISCONVERT.equals("0")) {
								dtos[j].setSassitsign(stbsassitsig); // ������־
							} else {
								dtos[j].setSassitsign(sassitsign); // ������־
							}
							dtos[j].setStbsassitsign(stbsassitsig); // TBS������־
							// String sunitcode = result.getString(j, k++);
							// dtos[j].setSunitcode(sunitcode); // ��λ����
							dtos[j].setSunitcode(null); // ��λ����
							String sopenaccbankcode = result.getString(j, k++);
							dtos[j].setSopenaccbankcode(sopenaccbankcode); // ��������
							String spaybookkind = result.getString(j, k++);
							dtos[j].setSpaybookkind(spaybookkind); // �ɿ�������

							dtos[j].setSorgcode(sorgcode); // ��������
							// dtos[j].setScommitdate(filetime); // ί������
							dtos[j].setScommitdate(currentDate); // ί������
							dtos[j].setSaccdate(currentDate); // ��������
							dtos[j].setSpackageno(tmpPackNo); // ����ˮ��
							if (ITFECommonConstant.ISCONVERT.equals("0")) {
								dtos[j].setStaxorgcode(stbstaxorgcode); // ���ջ��ش���
							} else {
								dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // ���ջ��ش���
							}
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // �ļ�����
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // �տ�������
							String sdealno = SequenceGenerator.changeTraSrlNo(
									packno, j);
							dtos[j].setSdealno(sdealno); // ������ˮ��
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
							String staxticketno = dtos[j].getSdealno();
							dtos[j].setStaxticketno(staxticketno); // ˰Ʊ����
							dtos[j].setSgenticketdate(filetime); // ��Ʊ����
							dtos[j].setStrimflag(ctrimflag); // ���� �ڱ�־
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δ����
							dtos[j].setSusercode(susercode);

							// ��ϸ��Ϣ
							// mainDto.setSunitcode(sunitcode); // ��λ����
							mainDto.setSunitcode(null); // ��λ����
							mainDto.setSrecvtrecode(tmpdto.getStrecode()); // �տ�������
							mainDto.setStbstaxorgcode(stbstaxorgcode); // TBS���ջ��ش���
							mainDto.setSpaybookkind(spaybookkind); // �ɿ�������
							mainDto.setSopenaccbankcode(sopenaccbankcode); // ��������
							mainDto.setSbudgetlevelcode(sbudgetlevelcode); // Ԥ�㼶��
							mainDto.setSbudgettype(sbudgettype); // Ԥ������
							mainDto.setSbudgetsubcode(sbudgetsubcode); // Ԥ���Ŀ����
							mainDto.setStbsassitsign(stbsassitsig); // TBS������־
							mainDto.setStrasrlno(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
							mainDto
									.setSfilename((fileResultDto.getSfilename())); // �ļ�����
							mainDto.setSorgcode(sorgcode); // ��������
							mainDto.setSdealno(sdealno); // ������ˮ��
							mainDto.setSpackageno(tmpPackNo); // ����ˮ��
							mainDto.setStaxorgcode(tmpdto.getStaxorgcode()); // ���ջ��ش���
							mainDto.setSsumtaxticketno(staxticketno); // ����ƾ֤���
							mainDto.setScommitdate(currentDate); // ��������
							mainDto
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING); // ��ϸ״̬Ĭ��Ϊ
							// ������

							// ���ܷ�ʽ
							if (null == iscollect || iscollect == 0) {
								// SQLExecutor sqlDetail =
								// DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();

								sqlDetail.addParam(sorgcode);
								sqlDetail
										.addParam(fileResultDto.getSfilename());
								sqlDetail.addParam(tmpdto.getStrecode());
								sqlDetail.addParam(tmpdto.getStaxorgcode());
								sqlDetail.addParam(stbstaxorgcode);
								sqlDetail.addParam(sbudgettype);
								sqlDetail.addParam(sbudgetsubcode);
								sqlDetail.addParam(sbudgetlevelcode);
								String detailSql = tmpDetailsql;
								if (spaybookkind != null
										&& spaybookkind.length() > 0) {
									detailSql = detailSql
											+ " and S_PAYBOOKKIND = ?  ";
									sqlDetail.addParam(spaybookkind);
								} else {
									detailSql = detailSql
											+ " and (S_PAYBOOKKIND is null or S_PAYBOOKKIND ='') ";
								}

								if (sopenaccbankcode != null
										&& sopenaccbankcode.length() > 0) {
									detailSql = detailSql
											+ " and S_OPENACCBANKCODE = ? ";
									sqlDetail.addParam(sopenaccbankcode);
								} else {
									detailSql = detailSql
											+ " and (S_OPENACCBANKCODE is null or S_OPENACCBANKCODE ='') ";
								}
								// if(sunitcode != null && sunitcode.length() >
								// 0){
								// detailSql = detailSql +
								// " and S_UNITCODE = ? ";
								// sqlDetail.addParam(sunitcode);
								// }else{
								// detailSql = detailSql +
								// " and (S_UNITCODE is null or S_UNITCODE ='') ";
								// }
								if (sassitsign != null
										&& sassitsign.length() > 0) {
									detailSql = detailSql
											+ " and S_ASSITSIGN = ? ";
									sqlDetail.addParam(sassitsign);
								} else {
									detailSql = detailSql
											+ " and (S_ASSITSIGN is null  or S_ASSITSIGN ='') ";
								}
								if (stbsassitsig != null
										&& stbsassitsig.length() > 0) {
									detailSql = detailSql
											+ " and S_TBS_ASSITSIGN = ? ";
									sqlDetail.addParam(stbsassitsig);
								} else {
									detailSql = detailSql
											+ " and (S_TBS_ASSITSIGN is null or S_TBS_ASSITSIGN ='') ";
								}
								SQLResults detailRs = sqlDetail
										.runQuery(detailSql);

								int detailcount = detailRs.getRowCount();
								for (int n = 0; n < detailcount; n++) {
									TvInfileDetailDto detailDto = new TvInfileDetailDto();
									detailDto = (TvInfileDetailDto) mainDto
											.clone();
									detailDto.setInum(Integer.parseInt(detailRs
											.getString(n, 3))); // ˳���
									detailDto.setNmoney(detailRs.getBigDecimal(
											n, 0)); // ���׽��
									detailDto.setStaxticketno(detailRs
											.getString(n, 4)); // ƾ֤���
									detailDto.setSdesctrecode(detailRs
											.getString(n, 5)); // Ŀ�Ĺ���

									listDetail.add(detailDto);
								}
							} else {
								mainDto.setNmoney(nmoney); // ���׽��
								int iNum = Integer.parseInt(result.getString(j,
										k++));
								mainDto.setInum(iNum); // ˳���
								String taxticketno = result.getString(j, k++);
								if (null == taxticketno
										|| taxticketno.trim().length() == 0) {
									taxticketno = mainDto.getSsumtaxticketno(); // ƾ֤���
								}
								mainDto.setStaxticketno(taxticketno); // ƾ֤���
								String desctrecode = result.getString(j, k++);
								mainDto.setSdesctrecode(desctrecode);
								String sunitcode = result.getString(j, k++); // ��λ����
								listDetail.add(mainDto);

								dtos[j].setStaxticketno(taxticketno); // ƾ֤���
								dtos[j].setStaxpaycode(sunitcode); // ��˰�˱���
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
							// sqlDetail.clearParams();
						}

						// ������������
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// ��ϸ���ݱ���
						TvInfileDetailDto[] detail = new TvInfileDetailDto[listDetail
								.size()];
						detail = listDetail.toArray(detail);
						DatabaseFacade.getDb().create(detail);
						listDetail = new ArrayList<TvInfileDetailDto>();

						// ������ͷ��Ϣ
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // ��������
						packdto.setStrecode(tmpdto.getStrecode()); // �������
						packdto.setSfilename(fileResultDto.getSfilename()); // �����ļ���
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // ���ش���
						// packdto.setScommitdate(filetime); // ί������
						packdto.setScommitdate(currentDate); // ί������
						packdto.setSaccdate(currentDate); // ��������
						packdto.setSpackageno(tmpPackNo); // ����ˮ��
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
						packdto.setIcount(icount); // �ܱ���
						packdto.setNmoney(sumamt); // �ܽ��

						if (fileResultDto.getIsError()) {
							// ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // ����״̬δ������
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δδ����
						}

						packdto.setSusercode(susercode); // ����Ա����
						packdto.setSdemo(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("���汨��ͷ��ʱ������쳣��", e);
				throw new ITFEBizException("���汨��ͷ��ʱ������쳣��", e);
			}

			// �ر�����
			sqlDetail.closeConnection();
			batchRetriever.closeConnection();

			// if (fileResultDto.getIsError()) {
			// // ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
			// // �����������ͱ���
			// fileResultDto.setPacknos(packList);
			// for (int i = 0; i < packList.size(); i++) {
			// this.sendMsg(fileResultDto.getSfilename(), sorgcode,
			// packList.get(i), MsgConstant.MSG_NO_7211, filetime, null, false);
			// }
			// }

			// ɾ����ʱ���¼
			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			CommonFacade.getODB().deleteRsByDto(tmpdto);
		} catch (FileOperateException e) {
			logger.error("�ļ����ص�ʱ������쳣!", e);
			throw new ITFEBizException("�ļ����ص�ʱ������쳣!", e);
		} catch (JAFDatabaseException e) {
			logger.error("���ݰ��˵�ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("���ݰ��˵�ʱ��������ݿ��쳣!", e);
		} catch (SequenceException e) {
			logger.error("������ˮ��ʱ�����쳣!", e);
			throw new ITFEBizException("������ˮ��ʱ�����쳣!", e);
		} catch (ValidateException e) {
			logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			} catch (JAFDatabaseException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			}
		}
	}

	
	/**
	 * ����TBS�ӿ�����
	 * 
	 * @param FileResultDto
	 *            fileResultDto �����ļ��ӿڶ���
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	public void dealTBSDataForZJ(FileResultDto fileResultDto, String sorgcode, String susercode , Integer iscollect) throws ITFEBizException {
		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��
		FileObjDto fileobj = PublicSearchFacade.getFileObjByFileName(fileResultDto.getSfilename());
		String filetime = fileobj.getSdate(); // �ļ�����
		String ctrimflag = fileobj.getCtrimflag(); // �����ڱ�־

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
		String absoluteSql = absolutePath + ".sql";

		SQLBatchRetriever batchRetriever = null; // ������ѯҪ�õ�

		try {
			// �ڶ��� �洢�ļ�
			String os = System.getProperties().getProperty("os.name");
			if (os.indexOf("Win") >= 0) {
				FileUtil.getInstance().writeFile(absolutePath, fileResultDto.getSmaininfo());
			} else {
				FileUtil.getInstance().writeFile(absolutePath, fileResultDto.getSmaininfo().replaceAll("\\r", ""));
			}
			
			// ������ ����SHELL���ļ�IMPORT�����ݿ� ��ʱ��TV_INFILE_TMP (��ɾ��Ȼ�����)
			String importcontent = PublicSearchFacade.getSqlConnectByProp() + "delete from TV_INFILE_TMP where S_ORGCODE = '" + sorgcode
					+ "' and S_FILENAME = '" + fileResultDto.getSfilename() + "';\n" + "import from " + absolutePath
					+ " of del modified by compound=100 insert into TV_INFILE_TMP;";
			FileUtil.getInstance().writeFile(absoluteSql, importcontent);
			String results = null;
			try {
				byte[] bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}

				bytes = null;
			} catch (Exception e) {
				logger.error("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
				throw new ITFEBizException("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
			}

			// ���Ĳ� �жϵ���SHELL�Ĵ�����,��֤ȫ�����ɹ�.���ܳ��ֵĴ���:����ת������,���ݱ��ض�
			VerifyCallShellRs.verifyShellForIncomeRs(results);

			// ���岽 ������У�� 1 ���ջ��ش��� 2 ������־
			VerifyParamTrans.verifyTBSIncomeTransForZJ(sorgcode, fileResultDto.getSfilename());
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_TBS, sorgcode, fileResultDto.getSfilename(), null);

			// ������ �����ļ�����,��������,�����������ջ��ش������(ע��Ҫȥ������ҵ��)
			List<FileObjDto> taxlist = new ArrayList<FileObjDto>();
			String selectSQL = "select S_RECVTRECODE,S_TAXORGCODE from TV_INFILE_TMP where S_RECVTRECODE = S_DESCTRECODE "
					+ "and S_FILENAME = ? and S_ORGCODE = ? group by S_RECVTRECODE,S_TAXORGCODE";
			SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults taxorgRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = taxorgRs.getRowCount();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = new FileObjDto();
				tmpdto.setStrecode(taxorgRs.getString(i, 0));
				tmpdto.setStaxorgcode(taxorgRs.getString(i, 1));

				taxlist.add(tmpdto);
			}

			if (count == 0) {
				logger.error("û���ҵ����������ļ�¼(�������տ������Ŀ�Ĺ��ⲻһ��)!");
				throw new ITFEBizException("û���ҵ����������ļ�¼(�������տ������Ŀ�Ĺ��ⲻһ��)!");
			}

			// ����һ�� ������쳣������ҪУ���ļ����ƥ��
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_MONEY) as N_MONEY from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0 || famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("�쳣�������ļ��ܽ��[" + famt + "]��¼�����ȣ�");
						throw new ITFEBizException("�쳣�������ļ��ܽ��[" + famt + "]��¼�����ȣ�");
					}
				} catch (JAFDatabaseException e) {
					logger.error("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
					throw new ITFEBizException("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// ���߲� ���ݷ�������ȥ���.

			List<String> packList = new ArrayList<String>();
			String tmpfilesql = null;

			if (null == iscollect || iscollect == 0) {
				// �ǻ��ܴ���
				tmpfilesql = "select S_TBS_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
						+ " group by S_TBS_TAXORGCODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND";
			} else {
				// ������ϸ����
				tmpfilesql = "select S_TBS_TAXORGCODE,N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND"
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? ";
			}

			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = taxlist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // �������
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // ���ջ��ش���
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPSÿ����������

				batchRetriever.runQuery(tmpfilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStbstaxorgcode((result.getString(j, k++))); // TBS���ջ��ش���
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // ���׽��
							dtos[j].setSbudgettype((result.getString(j, k++))); // Ԥ������
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // Ԥ���Ŀ����
							dtos[j].setSbudgetlevelcode(result.getString(j, k++)); // Ԥ�㼶��
							dtos[j].setSassitsign(result.getString(j, k++)); // ������־
							dtos[j].setStbsassitsign(result.getString(j, k++)); // TBS������־
							dtos[j].setSunitcode(result.getString(j, k++)); // ��λ����
							dtos[j].setSopenaccbankcode(result.getString(j, k++)); // ��������
							dtos[j].setSpaybookkind(result.getString(j, k++)); // �ɿ�������

							dtos[j].setSorgcode(sorgcode); // ��������
							dtos[j].setScommitdate(filetime); // ί������
							dtos[j].setSaccdate(currentDate); // ��������
							dtos[j].setSpackageno(tmpPackNo); // ����ˮ��
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // ���ջ��ش���
							dtos[j].setSfilename((fileResultDto.getSfilename())); // �ļ�����
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // �տ�������
							dtos[j].setSdealno(SequenceGenerator.changeTraSrlNo(packno, j)); // ������ˮ��
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // ˰Ʊ����
							dtos[j].setSgenticketdate(filetime); // ��Ʊ����
							dtos[j].setStrimflag(ctrimflag); // ���� �ڱ�־
							dtos[j].setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δ����
							dtos[j].setSusercode(susercode);

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// ������������
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// ������ͷ��Ϣ
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // ��������
						packdto.setStrecode(tmpdto.getStrecode()); // �������
						packdto.setSfilename(fileResultDto.getSfilename()); // �����ļ���
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // ���ش���
						packdto.setScommitdate(filetime); // ί������
						packdto.setSaccdate(currentDate); // ��������
						packdto.setSpackageno(tmpPackNo); // ����ˮ��
						packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
						packdto.setIcount(icount); // �ܱ���
						packdto.setNmoney(sumamt); // �ܽ��	
						
						if (fileResultDto.getIsError()) {
							// ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
							packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // ����״̬δ������
						}else{
							packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δδ����
						}

						packdto.setSusercode(susercode); // ����Ա����
						packdto.setSdemo(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("���汨��ͷ��ʱ������쳣��", e);
				throw new ITFEBizException("���汨��ͷ��ʱ������쳣��", e);
			}

			// �ر�����
			batchRetriever.closeConnection();

//			if (fileResultDto.getIsError()) {
//				// ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
//				// �����������ͱ���
//				fileResultDto.setPacknos(packList);
//				for (int i = 0; i < packList.size(); i++) {
//					this.sendMsg(fileResultDto.getSfilename(), sorgcode, packList.get(i), MsgConstant.MSG_NO_7211, filetime, null, false);
//				}
//			}

			// ɾ����ʱ���¼
			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			CommonFacade.getODB().deleteRsByDto(tmpdto);
		} catch (FileOperateException e) {
			logger.error("�ļ����ص�ʱ������쳣!", e);
			throw new ITFEBizException("�ļ����ص�ʱ������쳣!", e);
		} catch (JAFDatabaseException e) {
			logger.error("���ݰ��˵�ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("���ݰ��˵�ʱ��������ݿ��쳣!", e);
		} catch (SequenceException e) {
			logger.error("������ˮ��ʱ�����쳣!", e);
			throw new ITFEBizException("������ˮ��ʱ�����쳣!", e);
		} catch (ValidateException e) {
			logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			} catch (JAFDatabaseException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			}
		}
	}
	
	
	
	
	/**
	 * �����˰�ӿ�����
	 * 
	 * @param FileResultDto
	 *            fileResultDto �����ļ��ӿڶ���
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	public void dealNationData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {

		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��
		SQLBatchRetriever batchRetriever = null; // ������ѯҪ�õ�

		try {
			List<TvInfileTmpCountryDto> dtolist = fileResultDto.getMainlist();
			if (null == dtolist || dtolist.size() == 0) {
				logger.error("������˰�ļ����ִ���û���ҵ���Ӧ�����ݣ�");
				throw new ITFEBizException("������˰�ļ����ִ���û���ҵ���Ӧ�����ݣ���");
			}

			TvInfileTmpCountryDto[] countrydtos = new TvInfileTmpCountryDto[dtolist
					.size()];
			countrydtos = dtolist.toArray(countrydtos);
			DatabaseFacade.getDb().create(countrydtos);

			// ����һ�� ������쳣������ҪУ���ļ����ƥ��
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_FACTTAXAMT) as N_FACTTAXAMT from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("�쳣�������ļ��ܽ��[" + famt + "]��¼�����ȣ�");
						throw new ITFEBizException("�쳣�������ļ��ܽ��[" + famt
								+ "]��¼�����ȣ�");
					}
				} catch (JAFDatabaseException e) {
					logger.error("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
					throw new ITFEBizException("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// 1.2������У��.
			List<FileObjDto> taxtrereflist = VerifyParamTrans
					.verifyNationIncomeTrans(sorgcode, fileResultDto
							.getSfilename());
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_NATION,
					sorgcode, fileResultDto.getSfilename(), null);
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
				VerifyParamTrans.verifyTreasury(sorgcode, fileResultDto.getSfilename());//��֤�տ�����Ŀ������Ƿ�һ��
			// 1.3�����ļ�����,��������,�����������ջ��ش������.
			String tmpplacefilesql = "";

			if (null != iscollect && iscollect != 0) {
				// ������ϸ����
				tmpplacefilesql = "select S_TCBSTAXORGCODE,N_FACTTAXAMT,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE,"
						+ "S_PAYBNKNO,S_OPENACCBANKCODE,S_CORPNAME,S_PAYACCT,S_CORPCODE,S_LIMITDATE,"
						+ "S_TAXSTARTDATE,S_TAXENDDATE,S_TAXTYPENAME"
						+ " from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? ";
			} else {
				// �ǻ��ܴ���
				tmpplacefilesql = "select S_TCBSTAXORGCODE,sum(N_FACTTAXAMT) as N_FACTTAXAMT,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN"
						+ " from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? "
						+ " group by S_TCBSTAXORGCODE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN";
			}

			List<String> packList = new ArrayList<String>();
			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			for (int i = 0; i < taxtrereflist.size(); i++) {
				FileObjDto tmpdto = taxtrereflist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // �������
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // ���ջ��ش���
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPSÿ����������

				batchRetriever.runQuery(tmpplacefilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStaxorgcode((result.getString(j, k++))); // TCBS���ջ��ش���
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // ���׽��
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // Ԥ���Ŀ����
							dtos[j].setSbudgetlevelcode(result
									.getString(j, k++)); // Ԥ�㼶��
							dtos[j].setSassitsign(result.getString(j, k++)); // ������־

							dtos[j].setSgenticketdate(currentDate); // ��Ʊ����
							dtos[j].setSbudgettype(MsgConstant.BDG_KIND_IN); // Ԥ������
							dtos[j].setSorgcode(sorgcode); // ��������
							dtos[j].setSaccdate(currentDate); // ��������
							dtos[j].setSpackageno(tmpPackNo); // ����ˮ��
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // ���ջ��ش���
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // �ļ�����
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // �տ�������
							dtos[j].setSdealno(SequenceGenerator
									.changeTraSrlNo(packno, j)); // ������ˮ��
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // ˰Ʊ����
							dtos[j].setScommitdate(currentDate); // ί������
							dtos[j].setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // ������Ϊ������
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δ����
							dtos[j].setSusercode(susercode);
							dtos[j]
									.setSsourceflag(MsgConstant.MSG_SOURCE_NATION);// ������Դ

							// 1-��˰
							if (null != iscollect && iscollect != 0) {
								// ������ϸ����
								dtos[j].setSgenticketdate(result.getString(j,
										k++)); // ��Ʊ����
								dtos[j].setSpaybnkno(result.getString(j, k++));// �����к�
								dtos[j].setSopenaccbankcode(result.getString(j,
										k++));// ��������к�
								dtos[j]
										.setStaxpayname(result
												.getString(j, k++));// ��˰������
								dtos[j].setSpayacct(result.getString(j, k++));// �����ʻ�
								dtos[j]
										.setStaxpaycode(result
												.getString(j, k++));// ��˰�˱���
								dtos[j].setSlimitdate(result.getString(j, k++));// �޽�����

								dtos[j].setStaxstartdate(result.getString(j,
										k++));// ������ʼ����
								dtos[j]
										.setStaxenddate(result
												.getString(j, k++));// ������ֹ����
								dtos[j].setStaxtypename(result
										.getString(j, k++));// ˰������
								dtos[j].setStaxtypecode(dtos[j]
										.getSbudgetsubcode());// ˰�ִ���
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// ������������
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// ������ͷ��Ϣ
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // ��������
						packdto.setStrecode(tmpdto.getStrecode()); // �������
						packdto.setSfilename(fileResultDto.getSfilename()); // �����ļ���
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // ���ش���
						packdto.setScommitdate(currentDate); // ί������
						packdto.setSaccdate(currentDate); // ��������
						packdto.setSpackageno(tmpPackNo); // ����ˮ��
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
						packdto.setIcount(icount); // �ܱ���
						packdto.setNmoney(sumamt); // �ܽ��

						if (fileResultDto.getIsError()) {
							// ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // ����״̬δ������
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δδ����
						}

						packdto.setSusercode(susercode); // ����Ա����
						packdto.setSdemo(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("���汨��ͷ��ʱ������쳣��", e);
				throw new ITFEBizException("���汨��ͷ��ʱ������쳣��", e);
			}

			// �ر�����
			batchRetriever.closeConnection();

			TvInfileTmpCountryDto tmpdto = new TvInfileTmpCountryDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			} catch (JAFDatabaseException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			}
		} catch (JAFDatabaseException e) {
			logger.error("���ݰ��˵�ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("���ݰ��˵�ʱ��������ݿ��쳣!", e);
		} catch (SequenceException e) {
			logger.error("������ˮ��ʱ�����쳣!", e);
			throw new ITFEBizException("������ˮ��ʱ�����쳣!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpCountryDto tmpdto = new TvInfileTmpCountryDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			} catch (JAFDatabaseException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			}
		}
	}

	/**
	 * �����˰�ӿ�����
	 * 
	 * @param FileResultDto
	 *            fileResultDto �����ļ��ӿڶ���
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	public void dealPlaceData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {

		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��

		SQLBatchRetriever batchRetriever = null; // ������ѯҪ�õ�

		String fileflag = ""; // �����ļ�����

		try {
			fileflag = SequenceGenerator.changeTraSrlNo(18, SequenceGenerator
					.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY));

			// ��˰�ӿڴ���ʽ
			// 1.1 ���ݵ���
			String serverpath = ITFECommonConstant.FILE_ROOT_PATH + dirsep
					+ fileResultDto.getSmaininfo(); // ������·��
			String serverdelpath = changeFileName(serverpath, ".txt", ".del");
			String absoluteSql = serverdelpath + ".sql";
			FileUtil.getInstance().writeFile(serverdelpath, "aaaaaaaaaaaaaaaaaaaaaaa");
			try {
				// �ڶ��� �洢�ļ�
				String os = System.getProperties().getProperty("os.name");
				byte[] bytes;
				if (os.indexOf("Win") >= 0) {
				     bytes = CallShellUtil
					.callShellWithRes("cmd.exe /c C:\\WINDOWS\\sed\\bin\\sed.exe -e \"s | , g\" "
							+ serverpath + ">" + serverdelpath);
				} else {

					 bytes = CallShellUtil
							.callShellWithRes("sh /itfe/script/init/sed " +serverpath.replaceAll("//", "/") +" "+serverdelpath.replaceAll("//", "/"));
				}
				
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					logger.error(new String(bytes, 0,
							MsgConstant.MAX_CALLSHELL_RS * 1024));
				} else {
					logger.error(new String(bytes));
				}

				// SQLExecutor proexcutor =
				// DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				// proexcutor.addParam(fileflag);

				String tmptablename = "tv_itfe_model_" + fileflag;

				// ����SHELL�������ε�λ����ʱ��a
				// ����SHELL���ļ�IMPORT�� ��ʱ��a
				// ����SHELL�� ��ʱ��a���ݵ����˰��ʱ��
				// ɾ����ʱ��a
				String importcontent = PublicSearchFacade.getSqlConnectByProp()
						+ "create table "
						+ tmptablename
						+ " like tv_infile_tmp_place_model in TS_BD_16K index in TS_BX_8K compress no;\n"
//						+ " like tv_infile_tmp_place_model compress no;\n"
						+ "import from " + serverdelpath
						+ " of del replace into " + tmptablename
						+ ";\n" + "insert into tv_infile_tmp_place select "
						+ "'" + fileflag + "',a.*,'','','','','' from "
						+ tmptablename + " a;\n" + "drop table " + tmptablename
						+ ";";

				// String importcontent =
				// PublicSearchFacade.getSqlConnectByProp() +
				// "call PRC_INFILE_PLACE('" + serverdelpath
				// + "' , '" + fileflag + "');";
				FileUtil.getInstance().writeFile(absoluteSql, importcontent);
				String results = null;
				try {
					byte[] bytesimport = DB2CallShell
							.dbCallShellWithRes(absoluteSql);
					if (bytesimport.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
						results = new String(bytesimport, 0,
								MsgConstant.MAX_CALLSHELL_RS * 1024);
					} else {
						results = new String(bytesimport);
					}

					bytes = null;
				} catch (Exception e) {
					logger.error("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
					throw new ITFEBizException("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
				}

				// �жϵ���SHELL�Ĵ�����,��֤ȫ�����ɹ�.���ܳ��ֵĴ���:����ת������,���ݱ��ض�
				VerifyCallShellRs.verifyShellForIncomeRs(results);

				// ����ģ���ɾ����ʱ��
				// proexcutor.runStoredProcExecCloseCon("PRC_INFILE_PLACE_2");
			} catch (Throwable e) {
				logger.error("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
				throw new ITFEBizException("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
			}

			// ����һ�� ������쳣������ҪУ���ļ����ƥ��
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_FACTTAXAMT) as N_FACTTAXAMT from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(fileflag);
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("�쳣�������ļ��ܽ��[" + famt + "]��¼�����ȣ�");
						throw new ITFEBizException("�쳣�������ļ��ܽ��[" + famt
								+ "]��¼�����ȣ�");
					}
				} catch (JAFDatabaseException e) {
					logger.error("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
					throw new ITFEBizException("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// 1.2������У�飨���ջ��ش��룬������룬������־����������кţ�.
			List<FileObjDto> taxtrereflist = VerifyParamTrans
					.verifyPlaceIncomeTrans(sorgcode, fileflag);
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_PLACE,
					sorgcode, fileResultDto.getSfilename(), fileflag);
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
				VerifyParamTrans.verifyTreasury(sorgcode, fileResultDto.getSfilename());//��֤�տ�����Ŀ������Ƿ�һ��
			// 1.3�����ļ�����,��������,�����������ջ��ش������.
			String tmpplacefilesql = "";

			if (null != iscollect && iscollect != 0) {
				// ������ϸ����
				tmpplacefilesql = "select S_TCBSTAXORGCODE,N_FACTTAXAMT,S_BUDGETSUBJECTCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE,"
						+ "S_PAYBNKNO,S_PAYOPBNKNO,S_CORPNAME,S_PAYACCT,S_CORPCODE,S_LIMITDATE,S_TAXTYPECODE,S_TAXTYPENAME,"
						+ "S_TAXSTARTDATE,S_TAXENDDATE,S_TAXSUBJECTNAME,I_TAXNUMBER,N_TAXAMT,N_TAXRATE,N_FACTTAXAMT,N_DISCOUNTTAXAMT"
						+ " from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? ";
			} else {
				// �ǻ��ܴ���
				tmpplacefilesql = "select S_TCBSTAXORGCODE,sum(N_FACTTAXAMT) as N_FACTTAXAMT,S_BUDGETSUBJECTCODE,S_BUDGETLEVELCODE,S_ASSITSIGN"
						+ " from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? "
						+ " group by S_TCBSTAXORGCODE,S_BUDGETSUBJECTCODE,S_BUDGETLEVELCODE,S_ASSITSIGN";
			}

			List<String> packList = new ArrayList<String>();
			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			for (int i = 0; i < taxtrereflist.size(); i++) {
				FileObjDto tmpdto = taxtrereflist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(fileflag);
				batchRetriever.addParam(tmpdto.getStrecode()); // �������
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // ���ջ��ش���
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPSÿ����������

				batchRetriever.runQuery(tmpplacefilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStaxorgcode((result.getString(j, k++))); // TCBS���ջ��ش���
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // ���׽��
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // Ԥ���Ŀ����
							dtos[j].setSbudgetlevelcode(result
									.getString(j, k++)); // Ԥ�㼶��
							dtos[j].setSassitsign(result.getString(j, k++)); // ������־
							dtos[j].setSgenticketdate(currentDate); // ��Ʊ����

							dtos[j].setSbudgettype(MsgConstant.BDG_KIND_IN); // Ԥ������
							dtos[j].setSorgcode(sorgcode); // ��������
							dtos[j].setSaccdate(currentDate); // ��������
							dtos[j].setSpackageno(tmpPackNo); // ����ˮ��
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // ���ջ��ش���
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // �ļ�����
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // �տ�������
							dtos[j].setSdealno(SequenceGenerator
									.changeTraSrlNo(packno, j)); // ������ˮ��
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // ˰Ʊ����
							dtos[j].setScommitdate(currentDate); // ί������
							dtos[j].setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // ������Ϊ������
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δ����
							dtos[j].setSusercode(susercode);
							dtos[j]
									.setSsourceflag(MsgConstant.MSG_SOURCE_PLACE);// ������Դ

							// 2-��˰
							if (null != iscollect && iscollect != 0) {
								// ������ϸ����
								dtos[j].setSgenticketdate(result.getString(j,
										k++)); // ��Ʊ����
								dtos[j].setSpaybnkno(result.getString(j, k++));// �����к�
								dtos[j].setSopenaccbankcode(result.getString(j,
										k++));// ��������к�
								dtos[j]
										.setStaxpayname(result
												.getString(j, k++));// ��˰������
								dtos[j].setSpayacct(result.getString(j, k++));// �����ʻ�
								dtos[j]
										.setStaxpaycode(result
												.getString(j, k++));// ��˰�˱���
								dtos[j].setSlimitdate(result.getString(j, k++));// �޽�����
								dtos[j].setStaxtypecode(result
										.getString(j, k++));// ˰�ִ���
								dtos[j].setStaxtypename(result
										.getString(j, k++));// ˰������
								dtos[j].setStaxstartdate(result.getString(j,
										k++));// ������ʼ����
								dtos[j]
										.setStaxenddate(result
												.getString(j, k++));// ������ֹ����
								dtos[j].setStaxsubjectname(result.getString(j,
										k++));// ˰Ŀ����
								dtos[j].setStaxnumber(result.getString(j, k++));// ��˰����
								dtos[j]
										.setNtaxamt(result
												.getBigDecimal(j, k++));// ��˰���
								dtos[j].setNtaxrate(result
										.getBigDecimal(j, k++));// ˰��
								dtos[j].setNfacttaxamt(result.getBigDecimal(j,
										k++));// ʵ��˰��
								dtos[j].setNdiscounttaxamt(result
										.getBigDecimal(j, k++));// ��˰��
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// ������������
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// ������ͷ��Ϣ
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // ��������
						packdto.setStrecode(tmpdto.getStrecode()); // �������
						packdto.setSfilename(fileResultDto.getSfilename()); // �����ļ���
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // ���ش���
						packdto.setScommitdate(currentDate); // ί������
						packdto.setSaccdate(currentDate); // ��������
						packdto.setSpackageno(tmpPackNo); // ����ˮ��
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
						packdto.setIcount(icount); // �ܱ���
						packdto.setNmoney(sumamt); // �ܽ��

						if (fileResultDto.getIsError()) {
							// ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // ����״̬δ������
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δδ����
						}

						packdto.setSusercode(susercode); // ����Ա����
						packdto.setSdemo(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("���汨��ͷ��ʱ������쳣��", e);
				throw new ITFEBizException("���汨��ͷ��ʱ������쳣��", e);
			}

			// �ر�����
			batchRetriever.closeConnection();

			TvInfileTmpPlaceDto tmpdto = new TvInfileTmpPlaceDto();
			tmpdto.setSoperbatch(fileflag);

			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			} catch (JAFDatabaseException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			}

		} catch (FileOperateException e) {
			logger.error("�ļ����ص�ʱ������쳣!", e);
			throw new ITFEBizException("�ļ����ص�ʱ������쳣!", e);
		} catch (JAFDatabaseException e) {
			logger.error("���ݰ��˵�ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("���ݰ��˵�ʱ��������ݿ��쳣!", e);
		} catch (SequenceException e) {
			logger.error("������ˮ��ʱ�����쳣!", e);
			throw new ITFEBizException("������ˮ��ʱ�����쳣!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpPlaceDto tmpdto = new TvInfileTmpPlaceDto();
			tmpdto.setSoperbatch(fileflag);

			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			} catch (JAFDatabaseException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			}
		}
	}

	/**
	 * ����TIPS�ӿ�����
	 * 
	 * @param FileResultDto
	 *            fileResultDto �����ļ��ӿڶ���
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	public void dealTIPSData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {

		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + currentDate
				+ dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
		String absoluteSql = absolutePath + ".sql";

		SQLBatchRetriever batchRetriever = null; // ������ѯҪ�õ�

		try {
			// �ڶ��� �洢�ļ�
			String os = System.getProperties().getProperty("os.name");
			if (os.indexOf("Win") >= 0) {
				FileUtil.getInstance().writeFile(absolutePath,
						fileResultDto.getSmaininfo());
			} else {
				FileUtil.getInstance().writeFile(absolutePath,
						fileResultDto.getSmaininfo().replaceAll("\\r", ""));
			}
			

			// ������ ����SHELL���ļ�IMPORT�����ݿ� ��ʱ��tv_infile_tmp (��ɾ��Ȼ�����)
			String importcontent = PublicSearchFacade.getSqlConnectByProp()
					+ "delete from TV_INFILE_TMP_TIPS where S_ORGCODE = '"
					+ sorgcode
					+ "' and S_FILENAME = '"
					+ fileResultDto.getSfilename()
					+ "';\n"
					+ "import from "
					+ absolutePath
					+ " of del modified by compound=100 insert into TV_INFILE_TMP_TIPS;";
			FileUtil.getInstance().writeFile(absoluteSql, importcontent);
			String results = null;
			try {
				byte[] bytes = null;
				// if(!isWin()){
				// String command =
				// "su - db2inst1 -c \"/home/db2inst1/sqllib/bin/db2 -tvf ";
				// command = command + absoluteSql + "\"";
				// bytes = CallShellUtil.callShellWithRes(command);
				// }else{
				// bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				// }

				bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0,
							MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				logger.debug("����SHELL���:" + results);
				bytes = null;
			} catch (Exception e) {
				logger.error("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
				throw new ITFEBizException("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
			}

			// ���Ĳ� �жϵ���SHELL�Ĵ�����,��֤ȫ�����ɹ�.���ܳ��ֵĴ���:����ת������,���ݱ��ض�
			VerifyCallShellRs.verifyShellForIncomeRs(results);

			// // ���岽 ������У�� 1 ���ջ��ش��� 2 ������־(ȡ����������ת��,ֱ����ҪУ��)
			// VerifyParamTrans.verifyIncomeTrans(sorgcode,
			// fileResultDto.getSfilename());
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_TIPS,
					sorgcode, fileResultDto.getSfilename(), null);
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
				VerifyParamTrans.verifyTreasury(sorgcode, fileResultDto.getSfilename());//��֤�տ�����Ŀ������Ƿ�һ��
			/**
			 * �����ַ���ʽ���⣬����import�����ݿ��еĺ������������һ����г�12λ������������ַ����������µ��ж϶�ʧ�ܣ�
			 * ��������һ��SQL�����ڽ�����������һ�л�ԭ 2013-04-25
			 */
			String reviewSQL = "update TV_INFILE_TMP_TIPS set s_orgcode=substr(s_orgcode,1,12) where S_FILENAME = ?";
			SQLExecutor reviewExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			reviewExec.addParam(fileResultDto.getSfilename());
			reviewExec.runQueryCloseCon(reviewSQL);

			// ������ �����ļ�����,��������,�����������ջ��ش������(ע��Ҫȥ������ҵ��)
			List<FileObjDto> taxlist = new ArrayList<FileObjDto>();
			String selectSQL = "select S_TRECODE,S_TAXORGCODE from TV_INFILE_TMP_TIPS where S_FILENAME = ? and S_ORGCODE = ? group by S_TRECODE,S_TAXORGCODE";
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults taxorgRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = taxorgRs.getRowCount();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = new FileObjDto();
				tmpdto.setStrecode(taxorgRs.getString(i, 0));
				tmpdto.setStaxorgcode(taxorgRs.getString(i, 1));

				taxlist.add(tmpdto);
			}

			if (count == 0) {
				logger.error("û���ҵ����������ļ�¼!");
				throw new ITFEBizException("û���ҵ����������ļ�¼");
			}

			// ����һ�� ������쳣������ҪУ���ļ����ƥ��
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_MONEY) as N_MONEY from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("�쳣�������ļ��ܽ��[" + famt + "]��¼�����ȣ�");
						throw new ITFEBizException("�쳣�������ļ��ܽ��[" + famt
								+ "]��¼�����ȣ�");
					}
				} catch (JAFDatabaseException e) {
					logger.error("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
					throw new ITFEBizException("��ѯ�ļ����ܽ��ʱ�����쳣!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// ���߲� ���ݷ�������ȥ���.
			List<String> packList = new ArrayList<String>();
			String tmpfilesql = null;

			if (null == iscollect || iscollect == 0) {
				// �ǻ��ܴ���
				tmpfilesql = "select S_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE "
						+ " from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ? and S_TRECODE =? and S_TAXORGCODE = ? "
						+ " group by S_TAXORGCODE,S_TRECODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE";
			} else {
				// ������ϸ���� S_BILLDATE
				tmpfilesql = " select S_TAXORGCODE,N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE,"
						+ " S_TAXPAYCODE,S_TAXPAYNAME,S_TAXSTARTDATE,S_TAXENDDATE,S_PAYBNKNO,S_PAYOPBNKNO,S_PAYACCT"
						+ " from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ? and S_TRECODE =? and S_TAXORGCODE = ? ";
			}

			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = taxlist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // �������
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // ���ջ��ش���
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPSÿ����������

				batchRetriever.runQuery(tmpfilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStaxorgcode(result.getString(j, k++)); // ���ջ��ش���
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // ���׽��
							dtos[j].setSbudgettype((result.getString(j, k++))); // Ԥ������
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // Ԥ���Ŀ����
							dtos[j].setSbudgetlevelcode(result
									.getString(j, k++)); // Ԥ�㼶��
							dtos[j].setSassitsign(result.getString(j, k++)); // ������־
							dtos[j].setSgenticketdate(result.getString(j, k++)); // ��Ʊ����

							dtos[j].setSorgcode(sorgcode); // ��������
							dtos[j].setScommitdate(currentDate); // ί������
							dtos[j].setSaccdate(currentDate); // ��������
							dtos[j].setSpackageno(tmpPackNo); // ����ˮ��
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // ���ջ��ش���
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // �ļ�����
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // �տ�������
							dtos[j].setSdealno(SequenceGenerator
									.changeTraSrlNo(packno, j)); // ������ˮ��
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // ˰Ʊ����
							dtos[j].setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // ������Ϊ������
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δ����
							dtos[j].setSusercode(susercode);
							dtos[j].setSsourceflag(MsgConstant.MSG_SOURCE_TIPS);// ������Դ

							// 2-TIPS
							if (null != iscollect && iscollect != 0) {
								// ������ϸ����
								dtos[j].setSgenticketdate(result.getString(j,
										k++)); // ��Ʊ����
								dtos[j]
										.setStaxpaycode(result
												.getString(j, k++));// ��˰�˱���
								dtos[j]
										.setStaxpayname(result
												.getString(j, k++));// ��˰������
								dtos[j].setStaxstartdate(result.getString(j,
										k++));// ������ʼ����
								dtos[j]
										.setStaxenddate(result
												.getString(j, k++));// ������ֹ����
								dtos[j].setSpaybnkno(result.getString(j, k++));// �����к�
								dtos[j].setSopenaccbankcode(result.getString(j,
										k++));// ��������к�
								dtos[j].setSpayacct(result.getString(j, k++));// �����ʻ�
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// ������������
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// ������ͷ��Ϣ
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // ��������
						packdto.setStrecode(tmpdto.getStrecode()); // �������
						packdto.setSfilename(fileResultDto.getSfilename()); // �����ļ���
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // ���ش���
						packdto.setScommitdate(currentDate); // ί������
						packdto.setSaccdate(currentDate); // ��������
						packdto.setSpackageno(tmpPackNo); // ����ˮ��
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
						packdto.setIcount(icount); // �ܱ���
						packdto.setNmoney(sumamt); // �ܽ��

						if (fileResultDto.getIsError()) {
							// ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // ����״̬δ������
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // ����״̬δδ����
						}

						packdto.setSusercode(susercode); // ����Ա����
						packdto.setSdemo(fileResultDto.getStrasrlno()); // �ʽ�������ˮ��
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("���汨��ͷ��ʱ������쳣��", e);
				throw new ITFEBizException("���汨��ͷ��ʱ������쳣��", e);
			}

			// �ر�����
			batchRetriever.closeConnection();

			// if (fileResultDto.getIsError()) {
			// // ������쳣����ֱ�ӷ��ͱ��ģ�������Ҫȷ���ύ
			// // �����������ͱ���
			// fileResultDto.setPacknos(packList);
			// for (int i = 0; i < packList.size(); i++) {
			// this.sendMsg(fileResultDto.getSfilename(), sorgcode,
			// packList.get(i), MsgConstant.MSG_NO_7211, filetime, null, false);
			// }
			// }

			// ɾ����ʱ���¼
			TvInfileTmpTipsDto tmpdto = new TvInfileTmpTipsDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			CommonFacade.getODB().deleteRsByDto(tmpdto);
		} catch (FileOperateException e) {
			logger.error("�ļ����ص�ʱ������쳣!", e);
			throw new ITFEBizException("�ļ����ص�ʱ������쳣!", e);
		} catch (JAFDatabaseException e) {
			logger.error("���ݰ��˵�ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("���ݰ��˵�ʱ��������ݿ��쳣!", e);
		} catch (SequenceException e) {
			logger.error("������ˮ��ʱ�����쳣!", e);
			throw new ITFEBizException("������ˮ��ʱ�����쳣!", e);
		} catch (ValidateException e) {
			logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpTipsDto tmpdto = new TvInfileTmpTipsDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			} catch (JAFDatabaseException e) {
				logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			}
		}

	}

	/**
	 * У�����˰Ʊƾ֤����Ƿ��ظ�
	 * 
	 * @param voulist
	 * @param filename
	 * @throws ITFEBizException
	 */
	public static void checkIncomeVouRepeat(String orgcode,
			List<String> voulist, String filename) throws ITFEBizException {

		String today = TimeFacade.getCurrentStringTime(); // ��ǰ����

		int oldSize = 0;

		int newSize = 0;

		Set<String> sets = new HashSet<String>();

		if (voulist == null || voulist.size() == 0) {
			return;
		}

		// ����˰ƱDTO
		TvInfileDto dto = new TvInfileDto();
		dto.setScommitdate(today);
		dto.setSorgcode(orgcode);
		List<TvInfileDto> incomelist = null;
		try {
			incomelist = (List<TvInfileDto>) CommonFacade.getODB()
					.findRsByDtoWithUR(dto);
		} catch (JAFDatabaseException e) {
			logger.error("��������˰Ʊ�쳣��", e);
			throw new ITFEBizException("��������˰Ʊ�쳣��", e);
		} catch (ValidateException e) {
			logger.error("��������˰Ʊ�쳣��", e);
			throw new ITFEBizException("��������˰Ʊ�쳣��", e);
		}
		if (incomelist != null && incomelist.size() > 0) {
			for (TvInfileDto dt : incomelist) {
				voulist.add(dt.getStbstaxorgcode().trim() + ","
						+ dt.getStaxticketno().trim());
			}
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					throw new ITFEBizException("����˰Ʊ�ļ�[" + filename + "]��ƾ֤���["
							+ item.split(",")[1] + "]�͵����ѵ���ƾ֤����ظ�,���֤");
				}
			}
		}
	}

	/**
	 * �ļ�����ת��
	 * 
	 * @param String
	 *            srcFile ԭ�ļ�
	 * @param String
	 *            srcFlag ԭ��־
	 * @param String
	 *            desFlag Ŀ���־
	 * @return
	 */
	private String changeFileName(String srcFile, String srcFlag, String desFlag) {
		String filename = srcFile.replace(srcFlag, desFlag);
		if (filename.indexOf(desFlag) <= 0) {
			filename = filename + desFlag;
		}

		return filename;
	}

	/**
	 * �ļ�·��ת��
	 * 
	 * @param String
	 *            srcFile Դ�ļ�·��
	 * @param String
	 *            dirsep ϵͳ�ļ��ָ�����
	 * @return
	 */
	private String changeFilePath(String srcFile, String dirsep) {

		srcFile = srcFile.replace("///", dirsep).replace("//", dirsep).replace(
				"/", dirsep);
		srcFile = srcFile.replace("\\\\\\", dirsep).replace("\\\\", dirsep)
				.replace("\\", dirsep);
		srcFile = srcFile.replace(dirsep + dirsep + dirsep, dirsep).replace(
				dirsep + dirsep, dirsep);

		return srcFile;
	}

	/**
	 * @return
	 */
	private static boolean isWin() {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") >= 0) {
			return true;
		} else {
			return false;
		}
	}

}
