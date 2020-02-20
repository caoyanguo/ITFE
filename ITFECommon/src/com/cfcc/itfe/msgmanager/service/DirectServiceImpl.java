package com.cfcc.itfe.msgmanager.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractServiceManagerServer;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttobankDto;
import com.cfcc.itfe.persistence.dto.TvDirectpayfilemainTmpDto;
import com.cfcc.itfe.persistence.dto.TvDirectpayfilesubTmpDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.verify.VerifyCallShellRs;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ֱ��֧����ȱ��ķ���Ԥ����
 * 
 * @author zhouchuan modify by zhouchuan 20091201 ����Ʊ��λ�޸�Ϊ���д���
 */

public class DirectServiceImpl extends AbstractServiceManagerServer {

	private static Log logger = LogFactory.getLog(DirectServiceImpl.class);

	/**
	 * ����ӿͻ��˴��͹������ļ�����(ֱ��֧�����ҵ��ҵ����)
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
	public void dealFileDto(FileResultDto fileResultDto, String sorgcode, String susercode) throws ITFEBizException {
		String filetime = PublicSearchFacade.getFileObjByFileName(fileResultDto.getSfilename()).getSdate(); // �ļ�����
		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ���� 

		try {
			// ��һ�� ȡ���ؼ��ֶ�
			HashMap<String, Object> mapkey = fileResultDto.getMap();
			String czjname = (String) mapkey.get(MsgConstant.MSG_FLAG_CZJNAME); // �����ֱ�־
			String reveiver = (String) mapkey.get(MsgConstant.MSG_FLAG_RECEIVER); // ���յ�λ

			// �ڶ��� ���йؼ��ֶε�ת��
			if (null == czjname || "".equals(czjname.trim()) || null == reveiver || "".equals(reveiver.trim())) {
				logger.error("�����ֱ�־[" + czjname + "]����յ�λ��־[" + reveiver + "]Ϊ��,��ȷ��!");
				throw new ITFEBizException("�����ֱ�־[" + czjname + "]����յ�λ��־[" + reveiver + "]Ϊ��,��ȷ��!");
			}
			TsConvertfinorgDto finflagdto = PublicSearchFacade.findFinOrgDto(sorgcode, czjname);
			if (null == finflagdto) {
				logger.error("û���ҵ���Ӧ�����ֱ�־ת��������[" + czjname + "]");
				throw new ITFEBizException("û���ҵ���Ӧ�����ֱ�־ת��������[" + czjname + "]");
			}
			TsConverttobankDto bankdto = PublicSearchFacade.findBankDto(sorgcode, reveiver, finflagdto.getStrecode());
			if (null == bankdto) {
				logger.error("û���ҵ���Ӧ���յ�λ�͹�������ת�����е�ת��������[" + reveiver + "-" + finflagdto.getStrecode() +  "]");
				throw new ITFEBizException("û���ҵ���Ӧ���յ�λ�͹�������ת�����е�ת��������[" + reveiver + "-" + finflagdto.getStrecode() +  "]");
			}
			String strecode = finflagdto.getStrecode(); // �������
			String sfincode = finflagdto.getSfinorgcode(); // ������������
			String sbankno = bankdto.getStobank(); // ת�����д���
			String sagentbank = bankdto.getSagentbank(); // ��������

			// ������ �洢�ļ�
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			// ����Ϣ��¼��ŵ�·��
			String mainPath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + "main" + fileResultDto.getSfilename();
			// ��ϸ��Ϣ��¼��ŵ�·��
			String detailPath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + "detail"
					+ fileResultDto.getSfilename();
			// ����SQL����ŵ�·��
			String callshellSql = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + fileResultDto.getSfilename()
					+ ".sql";
            
			String os = System.getProperties().getProperty("os.name");
			if (os.indexOf("Win") >= 0) {
				FileUtil.getInstance().writeFile(mainPath, fileResultDto.getSmaininfo());
				FileUtil.getInstance().writeFile(detailPath, fileResultDto.getSdetailinfo());
			} else {
				FileUtil.getInstance().writeFile(mainPath, fileResultDto.getSmaininfo().replaceAll("\\r", ""));
				FileUtil.getInstance().writeFile(detailPath, fileResultDto.getSdetailinfo().replaceAll("\\r", ""));
			}
			// ���Ĳ� ����SHELL���ļ�IMPORT�����ݿ� ��ʱ��TV_DIRECTPAYFILEMAIN_TMP ��
			// TV_DIRECTPAYFILESUB_TMP(��ɾ��Ȼ�����)
			String importcontent = PublicSearchFacade.getSqlConnectByProp() + "delete from " + TvDirectpayfilemainTmpDto.tableName()
					+ " where S_ORGCODE = '" + sorgcode + "' and S_FILENAME = '" + fileResultDto.getSfilename() + "';\n" + "delete from "
					+ TvDirectpayfilesubTmpDto.tableName() + " where S_ORGCODE = '" + sorgcode + "' and S_FILENAME = '"
					+ fileResultDto.getSfilename() + "';\n" + "import from " + mainPath + " of del modified by delprioritychar insert into "
					+ TvDirectpayfilemainTmpDto.tableName() + ";\n" + "import from " + detailPath
					+ " of del modified by delprioritychar insert into " + TvDirectpayfilesubTmpDto.tableName() + ";\n";

			FileUtil.getInstance().writeFile(callshellSql, importcontent);
			String results = null;
			try {
				byte[] bytes = DB2CallShell.dbCallShellWithRes(callshellSql);
				if(bytes.length > MsgConstant.MAX_CALLSHELL_RS*1024){
					results = new String(bytes,0,MsgConstant.MAX_CALLSHELL_RS*1024);
				}else{
					results = new String(bytes);
				}
				
				bytes = null;
			} catch (Exception e) {
				logger.error("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
				throw new ITFEBizException("����SHELL:���ݵ����ʱ��������ݿ��쳣!", e);
			}

			// �жϵ���SHELL�Ĵ�����,��֤ȫ�����ɹ�.���ܳ��ֵĴ���:����ת������,���ݱ��ض�
			VerifyCallShellRs.verifyShellForPayoutRs(results);
			
			// У���ܽ��
			VerifyParamTrans.verifySumAmt(sorgcode, fileResultDto.getSfilename(), TvDirectpayfilemainTmpDto.tableName(), fileResultDto.getFsumamt());

			// ���岽 ȡ��Ԥ������
			String sbdgkind = VerifyParamTrans.verifyPayOutBdgKind(sorgcode, fileResultDto.getSfilename(), TvDirectpayfilesubTmpDto.tableName());

			// ������У�鵼���ļ����ظ���
			boolean repeatflag = VerifyFileName.verifyImportRepeat(sorgcode, fileResultDto.getSfilename());
			TvDirectpaymsgmainDto directdto = new TvDirectpaymsgmainDto();
			directdto.setSorgcode(sorgcode);
			directdto.setSfilename(fileResultDto.getSfilename());
			
			List directlist = CommonFacade.getODB().findRsByDto(directdto);
			if(directlist != null && directlist.size() > 0){
				repeatflag = true;
			}else{
				repeatflag = false;
			}
			
			if (repeatflag) {
				// ���ظ�����,�����Ƿ���ʧ�ܵļ�¼.�����,����ʧ�ܵ�ҵ��,���û��,��ʾ�û�������Ϣ.
				boolean failflag = PublicSearchFacade.findFailedDirectRecord(sorgcode, fileResultDto.getSfilename());
				if (!failflag) {
					// û��ʧ�ܼ�¼,��ʾ�û�������Ϣ.
					logger.error("���ļ�[" + fileResultDto.getSfilename() + "]�Ѿ�����ɹ�,�����ظ�����!");
					throw new ITFEBizException("���ļ�[" + fileResultDto.getSfilename() + "]�Ѿ�����ɹ�,�����ظ�����!");
				}
				// �� ��.һ �� ����ʱ��ļ�¼ȡ��Ȼ��洢����ʽ��
				SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String movedataSql = "insert into "
						+ TvDirectpaymsgmainDto.tableName()
						+ " (I_VOUSRLNO,S_ORGCODE,S_COMMITDATE,S_ACCDATE,S_FILENAME,S_PACKAGENO,S_TRECODE,S_PAYUNIT,S_TRANSBANKCODE,S_DEALNO,"
						+ "	N_MONEY,S_TAXTICKETNO,S_PACKAGETICKETNO,S_GENTICKETDATE,S_BUDGETTYPE,S_OFYEAR,S_TRANSACTUNIT,S_AMTTYPE,S_STATUS,S_USERCODE,TS_SYSUPDATE,S_HOLD2) "
						+ " select next value for ITFE_DIRECTPAYSEQ,file.S_ORGCODE,'"
						+ filetime
						+ "','"
						+ currentDate
						+ "',file.S_FILENAME,file.S_PACKAGENO,'"
						+ strecode
						+ "','"
						+ sfincode
						+ "','"
						+ sbankno
						+ "',file.S_DEALNO,file.N_MONEY,file.S_TAXTICKETNO,file.S_BUDGETUNITCODE||file.S_TAXTICKETNO,file.S_GENTICKETDATE,'"
						+ sbdgkind
						+ "',file.S_OFYEAR,'"
						+ sagentbank
						+ "','"
						+ MsgConstant.AMT_KIND_BANK
						+ "','"
						+ DealCodeConstants.DEALCODE_ITFE_DEALING
						+ "','"
						+ susercode
						+ "',CURRENT TIMESTAMP,file.S_BUDGETUNITCODE "
						+ " from "
						+ TvDirectpayfilemainTmpDto.tableName()
						+ " file , "
						+ TvDirectpaymsgmainDto.tableName()
						+ " msg "
						+ " where file.S_ORGCODE = ? and file.S_FILENAME = ? and file.S_ORGCODE = msg.S_ORGCODE and file.S_FILENAME = msg.S_FILENAME and "
						+ " file.S_BUDGETUNITCODE =msg.S_HOLD2  AND file.S_TAXTICKETNO = msg.S_TAXTICKETNO and  file.S_OFYEAR = msg.S_OFYEAR and "
						+ " msg.S_STATUS = ?";
				sqlExec.addParam(sorgcode);
				sqlExec.addParam(fileResultDto.getSfilename());
				sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
				sqlExec.runQueryCloseCon(movedataSql);

				// ��������ˮ��,���ɰ���ˮ������
				StringBuffer packbuf = new StringBuffer();
				for (int i = 0; i < fileResultDto.getPacknos().size(); i++) {
					if (i == fileResultDto.getPacknos().size() - 1) {
						packbuf.append("'" + fileResultDto.getPacknos().get(i) + "'");
					} else {
						packbuf.append("'" + fileResultDto.getPacknos().get(i) + "',");
					}
				}
				
				TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
				mainDto.setSorgcode(sorgcode);
				mainDto.setScommitdate(filetime);
				mainDto.setSaccdate(currentDate);
				mainDto.setStrecode(strecode);
				mainDto.setSfilename(fileResultDto.getSfilename());
				List<TvDirectpaymsgmainDto> mainDtoList = CommonFacade.getODB().findRsByDto(mainDto);
				for (TvDirectpaymsgmainDto main : mainDtoList) {
					SQLExecutor subsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					String movesubdataSql = "insert into "
							+ TvDirectpaymsgsubDto.tableName()
							+ " (I_VOUSRLNO,I_DETAILSEQNO,S_ORGCODE,S_PACKAGENO,S_OFYEAR,S_BUDGETUNITCODE,S_TAXTICKETNO,S_LINE,S_FUNSUBJECTCODE,N_MONEY,S_USERCODE,S_ACCDATE,TS_SYSUPDATE) "
							+ "  select main.I_VOUSRLNO,row_number() over(),sub.S_ORGCODE,main.S_PACKAGENO,sub.S_OFYEAR,sub.S_BUDGETUNITCODE,sub.S_TAXTICKETNO,sub.S_LINE,sub.S_FUNSUBJECTCODE,sub.N_MONEY,'"
							+ susercode
							+ "','"
							+ currentDate
							+ "',CURRENT TIMESTAMP from "
							+ TvDirectpayfilesubTmpDto.tableName()
							+ " sub , "
							+ TvDirectpaymsgmainDto.tableName()
							+ " main "
							+ " where main.S_ORGCODE = sub.S_ORGCODE and main.S_FILENAME = sub.S_FILENAME "
							+ " and main.S_OFYEAR = sub.S_OFYEAR and main.S_TAXTICKETNO = sub.S_TAXTICKETNO  AND main.S_HOLD2 = sub.S_BUDGETUNITCODE "
							+ " and sub.S_ORGCODE = ? and sub.S_FILENAME = ? and main.I_VOUSRLNO = ? and main.S_PACKAGENO in (";
					subsqlExec.addParam(sorgcode);
					subsqlExec.addParam(fileResultDto.getSfilename());
					subsqlExec.addParam(main.getIvousrlno());
					subsqlExec.runQueryCloseCon(movesubdataSql + packbuf + ")");
				}
				
				// �� ��.�� �� ���Ѿ��ط��ļ�¼�޸�״̬Ϊ���ط�
				SQLExecutor updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String updatesql = " update "
						+ TvDirectpaymsgmainDto.tableName()
						+ " msg set msg.S_STATUS = ? "
						+ " where exists(select 1 from "
						+ TvDirectpaymsgmainDto.tableName()
						+ " tmp where msg.S_ORGCODE = ? and msg.S_FILENAME = ? and "
						+ " msg.S_ORGCODE = tmp.S_ORGCODE and msg.S_FILENAME = tmp.S_FILENAME and msg.S_TAXTICKETNO = tmp.S_TAXTICKETNO AND msg.S_HOLD2 = tmp.S_HOLD2 and "
						+ " msg.S_OFYEAR = tmp.S_OFYEAR and msg.S_STATUS = ? and tmp.S_PACKAGENO in (" + packbuf + ") )";
				updatesqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND);
				updatesqlExec.addParam(sorgcode);
				updatesqlExec.addParam(fileResultDto.getSfilename());
				updatesqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
				updatesqlExec.runQueryCloseCon(updatesql);

				// ����Ҫ�ط����ݵİ�
				SQLExecutor packsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String packsql = "select distinct S_PACKAGENO from " + TvDirectpaymsgmainDto.tableName()
						+ " where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO in (";
				packsqlExec.addParam(sorgcode);
				packsqlExec.addParam(fileResultDto.getSfilename());
				SQLResults rs = packsqlExec.runQueryCloseCon(packsql + packbuf + ")");
				int packcount = rs.getRowCount();
				if (packcount <= 0) {
					logger.error("û���ҵ�Ҫ���͵ı��ļ�¼,��ȷ��!");
					throw new ITFEBizException("û���ҵ�Ҫ���͵ı��ļ�¼,��ȷ��!");
				}
				List<String> packlist = new ArrayList<String>();
				for (int i = 0; i < packcount; i++) {
					packlist.add(rs.getString(i, 0));
				}

				fileResultDto.setPacknos(packlist);
			} else {
				// �ļ��ǵ�һ�ε���.ֱ�ӽ����ݲ������ݿ⴦��.
				// ���߲� ����ʱ��ļ�¼ȡ��Ȼ��洢����ʽ��
				SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String movedataSql = "insert into "
						+ TvDirectpaymsgmainDto.tableName()
						+ " (I_VOUSRLNO,S_ORGCODE,S_COMMITDATE,S_ACCDATE,S_FILENAME,S_PACKAGENO,S_TRECODE,S_PAYUNIT,S_TRANSBANKCODE,S_DEALNO,"
						+ "	 N_MONEY,S_TAXTICKETNO,S_PACKAGETICKETNO,S_GENTICKETDATE,S_BUDGETTYPE,S_OFYEAR,S_TRANSACTUNIT,S_AMTTYPE,S_STATUS,S_USERCODE,TS_SYSUPDATE,S_HOLD2) "
						+ "  select next value for ITFE_DIRECTPAYSEQ,S_ORGCODE,'" 
						+ filetime 
						+ "','" 
						+ currentDate 
						+ "',S_FILENAME,S_PACKAGENO,'" 
						+ strecode 
						+ "','" 
						+ sfincode
						+ "','" 
						+ sbankno 
						+ "',S_DEALNO,N_MONEY,S_TAXTICKETNO,S_BUDGETUNITCODE||S_TAXTICKETNO,S_GENTICKETDATE,'"
						+ sbdgkind 
						+ "',S_OFYEAR,' " 
						+ sagentbank 
						+ "','" 
						+ MsgConstant.AMT_KIND_BANK 
						+ "','" 
						+ DealCodeConstants.DEALCODE_ITFE_DEALING
						+ "','" 
						+ susercode 
						+ "'" 
						+ ",CURRENT TIMESTAMP,S_BUDGETUNITCODE from " 
						+ TvDirectpayfilemainTmpDto.tableName() 
						+ "  where S_ORGCODE = ? and S_FILENAME = ? ";
				sqlExec.addParam(sorgcode);
				sqlExec.addParam(fileResultDto.getSfilename());
				sqlExec.runQueryCloseCon(movedataSql);

				TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
				mainDto.setSorgcode(sorgcode);
				mainDto.setScommitdate(filetime);
				mainDto.setSaccdate(currentDate);
				mainDto.setStrecode(strecode);
				mainDto.setSfilename(fileResultDto.getSfilename());
				List<TvDirectpaymsgmainDto> mainDtoList = CommonFacade.getODB().findRsByDto(mainDto);
				for (TvDirectpaymsgmainDto main : mainDtoList) {
					SQLExecutor subsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					String movesubdataSql = "insert into "
							+ TvDirectpaymsgsubDto.tableName()
							+ " (I_VOUSRLNO,I_DETAILSEQNO,S_ORGCODE,S_PACKAGENO,S_OFYEAR,S_BUDGETUNITCODE,S_TAXTICKETNO,S_LINE,S_FUNSUBJECTCODE,N_MONEY,S_USERCODE,S_ACCDATE,TS_SYSUPDATE) "
							+ "  select main.I_VOUSRLNO,row_number() over(),sub.S_ORGCODE,main.S_PACKAGENO,sub.S_OFYEAR,sub.S_BUDGETUNITCODE,sub.S_TAXTICKETNO,sub.S_LINE,sub.S_FUNSUBJECTCODE,sub.N_MONEY,'"
							+ susercode
							+ "','"
							+ currentDate
							+ "',CURRENT TIMESTAMP from "
							+ TvDirectpayfilesubTmpDto.tableName()
							+ " sub , "
							+ TvDirectpaymsgmainDto.tableName()
							+ " main "
							+ " where main.S_ORGCODE = sub.S_ORGCODE and main.S_FILENAME = sub.S_FILENAME "
							+ " and main.S_OFYEAR = sub.S_OFYEAR and main.S_TAXTICKETNO = sub.S_TAXTICKETNO AND main.S_HOLD2 =sub.S_BUDGETUNITCODE"
							+ " and sub.S_ORGCODE = ? and sub.S_FILENAME = ? and main.I_VOUSRLNO = ?";
					subsqlExec.addParam(sorgcode);
					subsqlExec.addParam(fileResultDto.getSfilename());
					subsqlExec.addParam(main.getIvousrlno());
					subsqlExec.runQueryCloseCon(movesubdataSql);
				}
			}
			
			// �����������ͱ���
			for (int i = 0; i < fileResultDto.getPacknos().size(); i++) {
				this.sendMsg(fileResultDto.getSfilename(), sorgcode, (String) (fileResultDto.getPacknos().get(i)), MsgConstant.MSG_NO_5102+"_OUT",
						filetime, null ,false);
			}
			
			// ɾ����ʱ���¼
			TvDirectpayfilesubTmpDto submpdto = new TvDirectpayfilesubTmpDto();
			submpdto.setSorgcode(sorgcode);
			submpdto.setSfilename(fileResultDto.getSfilename());
			
			TvDirectpayfilemainTmpDto maintmpdto = new TvDirectpayfilemainTmpDto();
			maintmpdto.setSorgcode(sorgcode);
			maintmpdto.setSfilename(fileResultDto.getSfilename());
			
			CommonFacade.getODB().deleteRsByDto(submpdto);
			CommonFacade.getODB().deleteRsByDto(maintmpdto);
		} catch (FileOperateException e) {
			logger.error("�ļ����ص�ʱ������쳣!", e);
			throw new ITFEBizException("�ļ����ص�ʱ������쳣!", e);
		} catch (JAFDatabaseException e) {
			logger.error("���ݰ��˵�ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("���ݰ��˵�ʱ��������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
			throw new ITFEBizException("ɾ����ʱ���¼��ʱ��������ݿ��쳣!", e);
		}
	}
}
