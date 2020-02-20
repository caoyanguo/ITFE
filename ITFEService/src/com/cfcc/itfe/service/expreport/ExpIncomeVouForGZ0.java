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
import com.cfcc.itfe.constant.MsgConstant;
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
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForGZ0 extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());
	private static final int MAX_NUM = 150000; // ÿ��ȡ������¼��

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
		//���ջ���
		String staxorg = idto.getStaxorgcode();
		// �����ѯ����
		String sqlWhere = CommonMakeReport.makesqlwhere(idto);
		// ���ݱ��еı�������
		String rptType = CommonMakeReport
				.getReportTypeByBillType(idto, bizType);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);

		String filename = null;// 

		try {
			HashMap<String, String> taxMap = super.converTaxCode(sbookorgcode);
			filename = CommonMakeReport.getExpFileNameByBillTypeForGZ(idto,
					bizType, taxMap);// 			
			String sql = "select * from TV_INFILE_DETAIL where S_RECVTRECODE = ? and s_orgcode = ? and s_commitdate= ? and s_status = ? ";
			
			/**
			 * ����ƾ֤ʱ�������ջ������� by hua 2013-06-18
			 */
			sql = new CommonMakeReport().alertSqlStringWithTaxorg(strecode,staxorg,sql);
			
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(MAX_NUM);
			sqlExec.addParam(strecode);
			sqlExec.addParam(sbookorgcode);
			sqlExec.addParam(srptdate);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			List<TvInfileDetailDto> resVouList = (List<TvInfileDetailDto>) sqlExec
					.runQueryCloseCon(sql, TvInfileDetailDto.class)
					.getDtoCollection();
			
			// ��ȡ�ֹ�˰Ʊ��һ������
			List <TvInfileDetailDto> handbookList = getHandBookDetail(strecode, srptdate,staxorg);
			if (null!=handbookList && handbookList.size()>0) {
				resVouList.addAll(handbookList)	;
			}
			if (resVouList.size() > 0) {
				// �õ�����ļ�����
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf = new StringBuffer(
						"skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
				for (TvInfileDetailDto _dto : resVouList) {
					filebuf.append(_dto.getSrecvtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrecvtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetlevelcode());
					filebuf.append(splitSign);
					String staxorgcode = "";
					if (taxMap.containsKey(_dto.getStaxorgcode())) {
						staxorgcode = taxMap.get(_dto.getStaxorgcode());
					} else {
						throw new ITFEBizException("�����������" + sbookorgcode
								+ "��TCBS���ջ��ش��루" + _dto.getStaxorgcode()
								+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
					}
					filebuf.append(staxorgcode);
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetsubcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScommitdate());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());
					filebuf.append(splitSign);
					if (null == _dto.getStaxticketno()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getStaxticketno());
					}
					filebuf.append(splitSign);
					if (null == _dto.getSunitcode()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getSunitcode());
					}
					filebuf.append(splitSign);
					if (null == _dto.getSpaybookkind()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getSpaybookkind());
					}
					filebuf.append(splitSign);
					if (null == _dto.getSopenaccbankcode()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getSopenaccbankcode());
					}

					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());
					filebuf.append("\r\n");
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ������ƾ֤����" + filename, e);
		}

	}

	
	/**
	 * ��ȡ�ֹ�˰Ʊ��������˰Ʊ��ϸDto�У��Ա�����׵�һ������
	 * @param strecode
	 * @param sintredate
	 * @return
	 * @throws JAFDatabaseException
	 */
	private List<TvInfileDetailDto> getHandBookDetail(String strecode,String sintredate,String staxorg) throws JAFDatabaseException {
		String sql = " Select * from TV_FIN_INCOME_DETAIL where S_TRECODE = ? and s_intredate= ? AND ( S_EXPVOUNO NOT IN " +
				" (SELECT S_TAXTICKETNO FROM TV_INFILE WHERE S_RECVTRECODE = ? AND S_COMMITDATE <=? ) OR S_EXPVOUNO is null) ";
		sql = new CommonMakeReport().alertSqlStringWithTaxorg(strecode, staxorg, sql);
		SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
		sqlExec.addParam(strecode);
		sqlExec.addParam(sintredate);
		sqlExec.addParam(strecode);
		sqlExec.addParam(sintredate);
		List <TvFinIncomeDetailDto> handBookList= (List<TvFinIncomeDetailDto>) sqlExec.runQueryCloseCon(sql,TvFinIncomeDetailDto.class).getDtoCollection();
		//ת��Dto
		List <TvInfileDetailDto> incomeList = null ;
		if (handBookList.size()>0) {
			incomeList= new ArrayList<TvInfileDetailDto>();
			for (TvFinIncomeDetailDto _dto : handBookList) {
				TvInfileDetailDto tmpdto = new TvInfileDetailDto();
				tmpdto.setSrecvtrecode(_dto.getStrecode());
				tmpdto.setSbudgetlevelcode(_dto.getCbdglevel());
				tmpdto.setStaxorgcode(_dto.getStaxorgcode());
				tmpdto.setSbudgetsubcode(_dto.getSbdgsbtcode());
				tmpdto.setScommitdate(_dto.getSintredate());
				tmpdto.setSbudgettype(_dto.getCbdgkind());
				tmpdto.setStaxticketno(_dto.getSexpvouno());
				tmpdto.setSpaybookkind(_dto.getCvouchannel());
				tmpdto.setNmoney(_dto.getFamt());
				incomeList.add(tmpdto);
			}

		}
		return incomeList;

	}
}
