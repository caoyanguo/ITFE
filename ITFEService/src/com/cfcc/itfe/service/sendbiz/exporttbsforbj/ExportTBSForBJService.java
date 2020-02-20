package com.cfcc.itfe.service.sendbiz.exporttbsforbj;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TimerVoucherInfoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
/**
 * @author hua
 * @time   14-09-10 10:11:07
 * ���ַ����Ǵ�ϵͳԭ���� '����TBS�ļ�' ����������������޸�
 */

@SuppressWarnings("unchecked")
public class ExportTBSForBJService extends AbstractExportTBSForBJService {
	private static Log log = LogFactory.getLog(ExportTBSForBJService.class);

	/**Ϊ�ͻ����ṩ��ѯ���ݷ���**/
	public List exportTBS(String bizType, String treCode, Date acctDate,
			String trimFlag, Object backParam) throws ITFEBizException {
		List<IDto> list = new ArrayList<IDto>();
		try {
			if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAY_OUT)) {// ʵ���ʽ�
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);
				if (null != acctDate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(acctDate, "yyyyMMdd"));
				}
				if (null != trimFlag) {
					where += " AND  S_TRIMFLAG = ? ";
					params.add(trimFlag);
				}
				where += " AND S_BIZNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5207);
				params.add(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayoutmsgmainDto.class, where, params);

			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN)) {// ����ֱ��֧�����
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(acctDate, "yyyyMMdd"));
				}
				
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5108);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvDirectpaymsgmainDto.class, where, params);

			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN)) {//��Ȩ֧�����
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(acctDate, "yyyyMMdd"));
				}
				
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5106);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvGrantpaymsgmainDto.class, where, params);

			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					|| bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {// ����֧����������
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  D_VOUDATE = ? ";
					params.add(acctDate);
				}
				if (null != trimFlag) {
					where += " AND  S_TRIMSIGN = ? ";
					params.add(trimFlag);
				}
				where += " AND  S_PAYTYPECODE = ? ";
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)){
					params.add("11");
				}
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
					params.add("12");
				}
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_2301);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayreckBankDto.class, where, params);
			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
					|| bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// ����֧�����������˻�
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  D_VOUDATE = ? ";
					params.add(acctDate);
				}
				if (null != trimFlag) {
					where += " AND  S_TRIMSIGN = ? ";
					params.add(trimFlag);
				}
				where += " AND  S_PAYTYPECODE = ? ";
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)){
					params.add("11");
				}
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
					params.add("12");
				}
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_2302);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayreckBankBackDto.class, where, params);
			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_RET_TREASURY)){ //�˿�
				ArrayList params = new ArrayList();
				String where = " where  S_PAYERTRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  D_ACCEPT = ? ";
					params.add(acctDate);
				}
				if (null != trimFlag) {
					where += " AND  C_TRIMFLAG = ? ";
					params.add(trimFlag);
				}
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5209);
				params.add(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvDwbkDto.class, where, params);
			} else {
				throw new RuntimeException("���빦���ݲ�֧�ָ�ҵ���������ݵ���["+bizType+"]!");
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ�����쳣(����TBS�ļ�-��������)\n" + e.getMessage());
			throw new ITFEBizException("ҵ�����쳣:" + e.getCause().getMessage(), e);
		}
		return list;
	}
	
	/**����TBS��ִ**/
	public MulitTableDto importTBS(List fileList, Object param) throws ITFEBizException {
		if (fileList == null || fileList.size() == 0) {
			throw new RuntimeException("�ϴ�������˵��ļ��б�Ϊ��!");
		}
		List<String> filePathList = fileList;
		MulitTableDto resultDto = new MulitTableDto();
		resultDto.setTotalCount(fileList.size());
		try {
			for (String fileName : filePathList) {
				
				/** 1.�����ļ�·�����õ��ļ�ҵ������ **/
				String fileType = BasicTBSFileProcesser.resolveFileType(fileName);
				
				/** 2.�����ļ�ҵ������ʵ������Ӧ�Ĵ�������д��� **/
				IProcessHandler processer = BasicTBSFileProcesser.generateProcesser(fileType);
				
				/** 3.��ʼ��������������� **/
				MulitTableDto processResult = null;
				
				try {
					/** 4.�ô�������ʼ�����ļ� **/
					processResult = processer.process(BasicTBSFileProcesser.getServerFilePath(fileName));
					
					/** 5.û�г���exception�쳣�������,����ҵ����صĴ��� **/
					BasicTBSFileProcesser.copyProcessRes2Result(fileName,processResult,resultDto);
					
				} catch (Exception e) {
					/** 6.�����쳣,���¼������Ϣ,����������һ���ļ� **/
					if(processResult!=null && processResult.getErrorList().size() > 0){
						resultDto.getErrorList().addAll(processResult.getErrorList());
					}
					resultDto.getErrNameList().add(new File(fileName).getName());
					resultDto.setErrorCount(resultDto.getErrorCount() + 1);
					log.error(e);
					continue;
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException("����TBS��ִ�����쳣!",e);
		} 
		return resultDto;
	}

	/**����������Ϣ�����ֱ�**/
	public List findSubInfoByMain(IDto mainDto) throws ITFEBizException {
		List list = new ArrayList();
		try {
			if (mainDto instanceof TvPayreckBankDto) {// ��������
				TvPayreckBankDto dto = (TvPayreckBankDto) mainDto;
				list = DatabaseFacade.getDb().find(TvPayreckBankListDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (mainDto instanceof TvPayreckBankBackDto) {// ���������˻�
				TvPayreckBankBackDto dto = (TvPayreckBankBackDto) mainDto;
				list = DatabaseFacade.getDb().find(TvPayreckBankBackListDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (mainDto instanceof TvGrantpaymsgmainDto) {// ��Ȩ֧�����
				TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) mainDto;
				list = DatabaseFacade.getDb().find(TvGrantpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno() + " and S_PACKAGETICKETNO = '"+dto.getSpackageticketno()+"' ");//���������
			} else if (mainDto instanceof TvDirectpaymsgmainDto) {// ֱ��֧�����
				TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) mainDto;
				list = DatabaseFacade.getDb().find(TvDirectpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (mainDto instanceof TvPayoutmsgmainDto) {// ʵ���ʽ�
				TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) mainDto;
				list = DatabaseFacade.getDb().find(TvPayoutmsgsubDto.class,
						" where  S_BIZNO = '" + dto.getSbizno() + "'");
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ�����쳣(����TBS�ļ�-��������)\n" + e.getMessage());
			throw new ITFEBizException("��ѯ�����쳣:" + e.getCause().getMessage(), e);
		}
		return list;
	}
	
	/**����TBS��ʽ�ļ�֮�����ƾ֤״̬**/
	public void updateVouStatus(List list) throws ITFEBizException {
		String updateStatus = DealCodeConstants.VOUCHER_SENDED;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			List<IDto> updateList = list;
			String sql = "UPDATE TV_VOUCHERINFO SET S_STATUS=?,S_DEMO=? WHERE S_DEALNO = ? ";
			conn = BasicTBSFileProcesser.getConnection();
			conn.setAutoCommit(false); //�Լ���������,Ҫôȫ�����³ɹ�,Ҫôȫ��������
			st = conn.prepareStatement(sql);
			for(int i=0; updateList!=null && updateList.size()>0 && i<updateList.size(); i++) {
				IDto dto = updateList.get(i);
				st.setString(1, updateStatus);
				st.setString(2, "�ѷ���");
				if(dto instanceof TvPayoutmsgmainDto) {
					st.setString(3, BeanUtils.getProperty(dto, "sbizno")+"");
				} else {
					st.setString(3, BeanUtils.getProperty(dto, "ivousrlno")+"");
				}
				st.addBatch();
			}
			st.executeBatch();
			conn.commit();
		} catch (Exception e) {
			if(conn!=null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.error("���ݻع�ʧ��(TBS�ļ�����)!",e);
				}
			}
			log.error(e);
			throw new RuntimeException("����ƾ֤״̬�쳣(TBS�ļ�����)!",e);
		} finally {
			BasicTBSFileProcesser.releaseConnection(conn, st, rs);
		}
	}

	public void deleteServerFile(String fileName) throws ITFEBizException {
		//ɾ�������ļ�
		try {
			if(new File(fileName).exists()) {
				FileUtil.getInstance().deleteFile(fileName);
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������������ļ������쳣", e);
		} catch (FileNotFoundException e) {
			log.error(e);
		} 
	}

	/**�����ļ����뵼����־��Ϣ*/
	public void writeTbsFileLog(List logList) throws ITFEBizException {
		if(isNotBlankList(logList)){
			try {
				for(Object obj : logList) {
					TvSendlogDto dto = (TvSendlogDto) obj;
					// ȡ������־��ˮ
					dto.setSsendno(StampFacade.getStampSendSeq("FS"));
					// ���ͻ�������
					dto.setSsendorgcode(getLoginInfo().getSorgcode());
					// ���սṹ����
					dto.setSrecvorgcode(getLoginInfo().getSorgcode());
					// �ϴ�����
					dto.setSdate(DateUtil.date2String2(TSystemFacade.findDBSystemDate()));
					// ����ʱ��
					dto.setSsendtime(TSystemFacade.getDBSystemTime());
					// ����ʱ��
					dto.setSproctime(TSystemFacade.getDBSystemTime());
					// ��������
					dto.setSsenddate(TimeFacade.getCurrentStringTime());
					// ������˵��
					dto.setSretcodedesc("����TBS��ʽ�ļ��ɹ�");
					dto.setSifsend(StateConstant.MSG_SENDER_FLAG_3);// �ļ���ʽ
					dto.setSturnsendflag(StateConstant.SendFinNo);// ת����־
					dto.setSdemo("����TBS��ʽ�ļ��ɹ�");
				}
				DatabaseFacade.getODB().create(CommonUtil.listTArray(logList));
			} catch (Exception e) {
				log.error("��¼�ļ�������־�쳣(��������)!",e);
			}
		}
	}
	
	private static final String TIMER_INFO_TYPE1 = "'20'"; //У��ɹ�
	private static final String TIMER_INFO_TYPE2 = "'17','40','45','50','60','61','71','73'"; //������
	private static final String TIMER_INFO_TYPE3 = "'10','15','16','30','62','72'"; //����ʧ��
	private static final String TIMER_INFO_TYPE4 = "'80','90','100'"; //�ѻص�
	
	/**
	 * Ϊ�ͻ��� ��ʱ����ƾ֤������� �ṩ���ݷ��ʷ���(����д����ֻ��Ϊ���Ҹ��ط������)
	 */
	public Map fetchVoucherInfoForClientTimer(String startDate, String endDate, IDto paramDto, String backParamString) throws ITFEBizException {
		
		if(startDate == null || "".equals(startDate)) {
			startDate = TimeFacade.getCurrentStringTime();
		}
		if(endDate == null || "".equals(endDate)) {
			endDate = TimeFacade.getCurrentStringTime();
		}
		
		//sql�����˺ܶ�case����ҪĿ����Ϊ����ת��
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  S_VTCODE AS SVTCODE,s_trecode as strecode,SBIZNAME,COALESCE(COUNT1,0) AS COUNT1,COALESCE(COUNT2,0) AS COUNT2,COALESCE(COUNT3,0) AS COUNT3,COALESCE(COUNT4,0) AS COUNT4 FROM (");
		sql.append("SELECT  S_VTCODE,s_trecode,SBIZNAME,");
		sql.append("		MAX(CASE SSTATUS WHEN '1' THEN SCOUNT END) AS COUNT1,");
		sql.append("		MAX(CASE SSTATUS WHEN '2' THEN SCOUNT END) AS COUNT2,");
		sql.append("		MAX(CASE SSTATUS WHEN '3' THEN SCOUNT END) AS COUNT3,");
		sql.append("		MAX(CASE SSTATUS WHEN '4' THEN SCOUNT END) AS COUNT4 ");
		sql.append("FROM ( ");
		sql.append("		SELECT  S_VTCODE ,s_trecode,");
		sql.append("                (SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_VTCODE) SBIZNAME,");
		sql.append("			    SSTATUS, ");
		sql.append("			    COUNT(*) SCOUNT  ");
		sql.append("		FROM ( ");
		sql.append("			  SELECT S_VTCODE,s_trecode,  ");
		sql.append("					CASE WHEN S_STATUS IN ("+TIMER_INFO_TYPE1+") THEN '1' ");
		sql.append("						 WHEN S_STATUS IN ("+TIMER_INFO_TYPE2+") THEN '2' ");
		sql.append("						 WHEN S_STATUS IN ("+TIMER_INFO_TYPE3+") THEN '3' ");
		sql.append("						 WHEN S_STATUS IN ("+TIMER_INFO_TYPE4+") THEN '4' ");
		sql.append("						 ELSE '2' ");
		sql.append("					END AS SSTATUS  ");
		sql.append("			  FROM TV_VOUCHERINFO  ");
		sql.append("			  WHERE S_CREATDATE BETWEEN ? AND ? AND S_ORGCODE=? AND S_STATUS IS NOT NULL AND S_VTCODE IS NOT NULL");
		sql.append("		) A WHERE EXISTS (SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE='0419' AND S_VALUE=A.S_VTCODE) ");
		sql.append("	        GROUP BY A.S_VTCODE,A.SSTATUS,s_trecode ");
		sql.append("            ORDER BY A.S_VTCODE,A.SSTATUS ");
		sql.append(")");
		sql.append("GROUP BY SBIZNAME,S_VTCODE,s_trecode )");
		
		SQLExecutor sqlExec = null;
		SQLResults rs = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			if("sendvoucherinfocount".equals(startDate)&&paramDto instanceof TvVoucherinfoDto)
			{
				TvVoucherinfoDto sdto = (TvVoucherinfoDto)paramDto;
				String csql = "select s_vtcode AS svtcode,s_trecode as strecode,s_status as sbizname,sum(N_MONEY) as totalamt, count(*) as COUNT1,'' as COUNT2,'' as COUNT3,'' as COUNT4 from"
				+"(select * from tv_voucherinfo union all select * from htv_voucherinfo)"
				+" where s_trecode=? and s_creatdate >=? and s_creatdate<=? and s_vtcode "
				+((sdto.getSvtcode()==null||"".equals(sdto.getSvtcode()))?"in(SELECT S_VTCODE FROM TS_VOUCHERCOMMITAUTO WHERE S_ORGCODE=? AND S_TRECODE=? AND S_RETURBACKNAUTO=?)":"=?")
				+" group by s_vtcode,s_status,s_trecode";
				sqlExec.addParam(sdto.getStrecode());
				sqlExec.addParam(sdto.getShold1());
				sqlExec.addParam(sdto.getShold2());
				if(sdto.getSvtcode()==null||"".equals(sdto.getSvtcode()))
				{
					sqlExec.addParam(sdto.getSorgcode());
					sqlExec.addParam(sdto.getStrecode());
					sqlExec.addParam("0");
				}else
				{
					sqlExec.addParam(sdto.getSvtcode());
				}
				rs = sqlExec.runQueryCloseCon(csql, TimerVoucherInfoDto.class);
				if(rs != null && rs.getRowCount() > 0) {
					List<TimerVoucherInfoDto> list = (List<TimerVoucherInfoDto>) rs.getDtoCollection();
					Map<String, List<TimerVoucherInfoDto>> map = new HashMap<String, List<TimerVoucherInfoDto>>();
					map.put("timerResult", list);
					return map;
				} else {
					return Collections.emptyMap();
				}
			}else
			{
				sqlExec.addParam("0419");
				sqlExec.addParam(startDate);
				sqlExec.addParam(endDate);
				sqlExec.addParam(getLoginInfo().getSorgcode());
				rs = sqlExec.runQueryCloseCon(sql.toString(), TimerVoucherInfoDto.class);
				if(rs != null && rs.getRowCount() > 0) {
					List<TimerVoucherInfoDto> list = (List<TimerVoucherInfoDto>) rs.getDtoCollection();
					Map<String, List<TimerVoucherInfoDto>> map = new HashMap<String, List<TimerVoucherInfoDto>>();
					map.put("timerResult", list);
					return map;
				} else {
					return Collections.emptyMap();
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("�ڻ�ȡ�ͻ��˶�ʱ���ѹ�����������ʱ�����쳣",e);
			throw new RuntimeException("���ݻ�ȡ�쳣!",e);
		}
	}
	
	private boolean isNotBlankList(List list) {
		return (list!=null)&&(list.size()>0);
	}
}