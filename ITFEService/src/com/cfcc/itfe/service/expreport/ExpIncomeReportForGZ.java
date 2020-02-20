package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectforqueryDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeReportForGZ extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());

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
		// ���ݱ��еı�������
		String rptType = CommonMakeReport.getReportTypeByBillType(idto, bizType);
		idto.setSbillkind(rptType);
		// �����ѯ����
		String sqlWhere = CommonMakeReport.makesqlwhere(idto);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
		// queryDto.setSfinorgcode(idto.getSfinorgcode());
		List<TrIncomedayrptDto> resList = new ArrayList<TrIncomedayrptDto>();// ���뱨��
		String filename = "";

		try {
			Map<String, String> taxorg = this.converTaxCode(sbookorgcode);
			filename = CommonMakeReport.getExpFileNameByBillTypeForGZ(idto,
					bizType, taxorg);
			if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)||(MsgConstant.RULE_SIGN_ALL.equals(sbelong) && idto.getStaxorgcode().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS) )) {
				List<TrIncomedayrptDto> specList = procSpecBudsubject(idto,
						rptType, sbookorgcode);
				resList.addAll(specList);
				// ���˵�����Ԥ���Ŀ
				sqlWhere += " AND S_BUDGETSUBCODE NOT IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
						+ sbookorgcode + "')";
			}
			sqlWhere = sqlWhere
					+ " AND s_taxorgcode not in(select S_PRETAXORGCODE from  TD_TAXORG_MERGER where s_bookorgcode = '"
					+ sbookorgcode + "')";
			resList.addAll(CommonFacade.getODB().findRsByDtoForWhere(queryDto,
					sqlWhere));
			
			if (null!=idto.getStaxorgcode() && MsgConstant.RULE_SIGN_ALL.equals(idto.getSbelongflag()) && getBigKindTaxorg().containsKey(idto.getStaxorgcode())) {
				//ѡ�� ȫϽ���ջ��ش���ʱ����ϲ����ջ��ز���
			}else if(null==idto.getStaxorgcode() && MsgConstant.RULE_SIGN_ALL.equals(idto.getSbelongflag())){
				//ѡ�� ȫϽ���ջ��ش���ʱ����ϲ����ջ��ز���
			}else{
				// ��ȡ�ϲ���ı���
				List <TrIncomedayrptDto> mergelist = mergeTaxOrgBill(idto, sbookorgcode,rptType);
				if (null!=mergelist && mergelist.size()>0) {
					resList.addAll(mergelist);
				}
			}
		
			//�ж��Ƿ��ϼ�
			if (StateConstant.COMMON_YES.equals(slesumitem)) {
				resList.addAll( super.getSumKuanItem(resList, strecode));
			}
			if (resList.size() > 0) {
				//����
				Collections.sort(resList, new SortByDayReport());
				// �õ�����ļ�����
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf = new StringBuffer(
						"skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj\r\n");
				// �õ��������ļ���
				TsTreasuryPK trepk = new TsTreasuryPK();
				trepk.setSorgcode(sbookorgcode);
				trepk.setStrecode(strecode);
				TsTreasuryDto tredto = (TsTreasuryDto) DatabaseFacade.getODB()
						.find(trepk);
				String tredtolevel = tredto.getStrelevel();
                 
				for (int i = 0; i < resList.size(); i++) {
					TrIncomedayrptDto _dto = resList.get(i);
					if (taxorg.containsKey(_dto.getStaxorgcode())&&taxorg.get(_dto.getStaxorgcode()) == null) {
						throw new ITFEBizException("�����������" + sbookorgcode
								+ "��TCBS���ջ��ش��루" + _dto.getStaxorgcode()
								+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
					}
					// ���ѡ���������ұ���ļ��δ��ڵ�ǰ����ļ����򲻵���
					if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)
							&& _dto.getSbudgetlevelcode()
									.compareTo(tredtolevel) > 0) {
						continue;
					}
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(strecode);
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetlevelcode());
					filebuf.append(splitSign);
					if (null==taxorg.get(_dto.getStaxorgcode())) {
						filebuf.append(_dto.getStaxorgcode());
					}else{
						filebuf.append(taxorg.get(_dto.getStaxorgcode()));
					}
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetsubcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrptdate());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneyday());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneymonth());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneyyear());
					if ((i + 1) != resList.size())
						filebuf.append("\r\n");
				}
				
				
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}

	/**
	 * ���Ӷ������Ŀ����Ҫ���������Ƴ�Ʒ��˰�յ�û�о������ջ��أ���ͳ�Ʊ����α���ʱ�޷�ͳ�Ƶ� �Ŀ�Ŀ
	 * 
	 * @param idto
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	private List<TrIncomedayrptDto> procSpecBudsubject(TrIncomedayrptDto idto,
			String rptType, String sbookorgcode) throws JAFDatabaseException,
			ValidateException {

		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
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
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
		// queryDto.setSfinorgcode(idto.getSfinorgcode());
		queryDto.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
		queryDto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
		// ȡͳ�ƹ���������Ŀ��ȫϽ����
		String sqlwhere = " AND s_trecode = '"
				+ strecode
				+ "' AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
				+ sbookorgcode + "' )  order by S_BUDGETSUBCODE";
		List<TrIncomedayrptDto> listup = CommonFacade.getODB()
				.findRsByDtoForWhere(queryDto, sqlwhere);

		// ȡ��Ͻ�������ͳ�ƹ���������Ŀ��ȫϽ��
		sqlwhere = " AND  s_trecode  IN ( select S_TRECODE from TS_TREASURY where S_GOVERNTRECODE = '"
				+ strecode
				+ "' )  AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where s_orgcode = '"
				+ sbookorgcode + "' ) order by S_BUDGETSUBCODE";
		List<TrIncomedayrptDto> listdown = CommonFacade.getODB()
				.findRsByDtoForWhere(queryDto, sqlwhere);

		// ��Ŀʵ�ʷ����� = ����ȫϽ�� --��Ͻ�����ȫϽ���ݣ�����Ŀѭ��
		if (null != listup && listup.size() > 0) {
			for (TrIncomedayrptDto _dto : listup) {
				String sbtcode = _dto.getSbudgetsubcode();
				String budlevel = _dto.getSbudgetlevelcode();
//				_dto.setStaxorgcode(idto.getSfinorgcode());
				for (TrIncomedayrptDto _dto2 : listdown) {
					String sbtcode2 = _dto2.getSbudgetsubcode();
					String budlevel2 = _dto2.getSbudgetlevelcode();
					if (sbtcode.equals(sbtcode2) && budlevel.equals(budlevel2)) {
						_dto.setNmoneyday(_dto.getNmoneyday().add(
								_dto2.getNmoneyday().negate()));
						_dto.setNmoneymonth(_dto.getNmoneymonth().add(
								_dto2.getNmoneymonth().negate()));
						_dto.setNmoneyquarter(_dto.getNmoneyquarter().add(
								_dto2.getNmoneyquarter().negate()));
						_dto.setNmoneytenday(_dto.getNmoneytenday().add(
								_dto2.getNmoneytenday().negate()));
						_dto.setNmoneyyear(_dto.getNmoneyyear().add(
								_dto2.getNmoneyyear().negate()));
					}
				}
			}
		}

		return listup;

	}

	/**
	 * ��ȡ����Ԥ���Ŀ��map
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	private HashMap findSpecialBudjectSubjectMap(String sbookorgcode)
			throws JAFDatabaseException, ValidateException {
		// ��������Ŀ��list
		TsBudgetsubjectforqueryDto qto = new TsBudgetsubjectforqueryDto();
		qto.setSorgcode(sbookorgcode);
		List<TsBudgetsubjectforqueryDto> bslist = CommonFacade.getODB()
				.findRsByDto(qto);

		HashMap<String, String> map = new HashMap<String, String>();
		for (TsBudgetsubjectforqueryDto dto : bslist) {
			map.put(dto.getSsubjectcode(), dto.getSsubjectname());
		}
		return map;
	}

	
}
