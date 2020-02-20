package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectforqueryDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.service.dataquery.trecodelargetaxandpaytable.TrecodeLargeTaxAndPayTableService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpReportIncomeForTJ extends AbstractExpReportForTJ {
	final Log log = LogFactory.getLog(this.getClass());

	public HashMap makeReportByBiz(TrIncomedayrptDto idto, String bizType,
			String sbookorgcode, HashMap map) throws ITFEBizException {
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
		// ��ȡ�������list
		List<String> proxyTreList = null;
		try {
			proxyTreList = findProxyList(sbookorgcode, strecode);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
		// �����ѯ����
		String sqlWhere = CommonMakeReportForTJ.makesqlwhere(idto, map,
				proxyTreList);
		// ���ݱ��еı�������
		String rptType = CommonMakeReportForTJ.getReportTypeByBillType(idto,
				bizType);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		// queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
		// �����ܶ�ֳɱ���ʱ���õ��������ջ��ش���������� add by zgz 20120913
		if (StateConstant.REPORTTYPE_FLAG_AMOUNTBILL.equals(rptType)) {
			queryDto.setSfinorgcode(idto.getSfinorgcode());
		}
		List<TrIncomedayrptDto> resList = new ArrayList<TrIncomedayrptDto>();// ���뱨��
		String filename = CommonMakeReportForTJ.getExpFileNameByBillType(idto,
				bizType);
		try {
			// ��ȡ���ջ�������map
			HashMap<String, String> taxorgmap = findTaxorgMap(sbookorgcode);
			List<TrIncomedayrptDto> exceptList = new ArrayList<TrIncomedayrptDto>();
			if (MsgConstant.MSG_TAXORG_SHARE_CLASS
					.equals(idto.getStaxorgcode())
					|| MsgConstant.RULE_SIGN_SELF.equals(sbelong)) {// ֻ�в������ջ���ʱ��ȡ�����Ŀ������
				List<TrIncomedayrptDto> specList = procSpecBudsubjectForTJ(
						idto, rptType, sbookorgcode, map);
				resList.addAll(specList);
				// �������������а���������֧�������Ŀ����
				if ("028800000003".equals(sbookorgcode)) {
					List<TrIncomedayrptDto> kfList = new ArrayList<TrIncomedayrptDto>();
					TrIncomedayrptDto specaildto = new TrIncomedayrptDto();
					TsTreasuryDto _dto = new TsTreasuryDto();
					_dto.setSorgcode(sbookorgcode);
					_dto.setStreattrib(StateConstant.COMMON_YES);// �����
					_dto.setSgoverntrecode("0288000000");// ��Ͻ�����������֧��
					List<TsTreasuryDto> list = CommonFacade.getODB()
							.findRsByDto(_dto);
					if (list.size() > 0) {
						if (strecode.equals(sbookorgcode.substring(0, 10))) {
							for (TsTreasuryDto tmp : list) {
								specaildto.setStrecode(tmp.getStrecode());
								specaildto.setSrptdate(srptdate);
								specaildto.setSbillkind(rptType);
								specaildto.setStrimflag(strimflag);
								specaildto.setSbudgettype(bugtype);
								specaildto
										.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
								specaildto
										.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
								String exceptsql = " AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
										+ sbookorgcode + "')";
								kfList = CommonFacade.getODB()
										.findRsByDtoForWhere(specaildto,
												exceptsql);
								resList.addAll(kfList);
							}
							//���˵������������ջ��ص������Ŀ�����Ŀ����
							for (TsTreasuryDto tmp : list) {
								specaildto = new TrIncomedayrptDto();
								specaildto.setStrecode(tmp.getStrecode());
								specaildto.setSrptdate(srptdate);
								specaildto.setSbillkind(rptType);
								specaildto.setStrimflag(strimflag);
								specaildto.setSbudgettype(bugtype);

								specaildto
										.setSbelongflag(MsgConstant.RULE_SIGN_SELF);
								String exceptsql = " AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
										+ sbookorgcode + "')";
								kfList = CommonFacade.getODB()
										.findRsByDtoForWhere(specaildto,
												exceptsql);
								exceptList.addAll(kfList);
							}
						}else{
//							specaildto.setStrecode(strecode);
//							specaildto.setSrptdate(srptdate);
//							specaildto.setSbillkind(rptType);
//							specaildto.setStrimflag(strimflag);
//							specaildto.setSbudgettype(bugtype);
//							specaildto
//									.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
//							specaildto
//									.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
//							String exceptsql = " AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
//									+ sbookorgcode + "')";
//							kfList = CommonFacade.getODB()
//									.findRsByDtoForWhere(specaildto,
//											exceptsql);
//							resList.addAll(kfList);
						}
					}

				}
				// ���˵������������ջ��ص�����Ԥ���Ŀ
				TrIncomedayrptDto tempdto = new TrIncomedayrptDto();
				tempdto.setStrecode(strecode);
				tempdto.setSrptdate(srptdate);
				tempdto.setSbillkind(rptType);
				tempdto.setStrimflag(strimflag);
				tempdto.setSbudgettype(bugtype);
				tempdto.setSbelongflag(MsgConstant.RULE_SIGN_SELF);
				String exceptsql = " AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
						+ sbookorgcode + "')";
				exceptList.addAll(CommonFacade.getODB().findRsByDtoForWhere(tempdto,exceptsql));
			}

			// �����ܶ�ֳɱ���ʱ,����Ҫ��ѯsql add by zgz add by zgz 20120913
			if (StateConstant.REPORTTYPE_FLAG_AMOUNTBILL.equals(rptType)) {
				sqlWhere = "";
			}
			sqlWhere += " ORDER BY S_TRECODE, S_TAXORGCODE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE";

			resList.addAll(CommonFacade.getODB().findRsByDtoForWhere(queryDto,
					sqlWhere));
			// ���˵������������ջ��ص�����Ԥ���Ŀ
			resList.removeAll(exceptList);

			// //ȡ�õ�ѡ��List
			// List inputList = (List) map.get("inputList");
			// //����е�ѡ����չ�����룬���ջ��أ�Ԥ���Ŀ��Ԥ�㼶��������������кϲ�����ɾ���ϲ��������
			// if(inputList.size()>0){
			// mergeSameData(resList);
			// }

			// ���ֿ⵼��ʱ��ѯ������������ȫϽ
			if ("0200000000".equals(strecode)) {
				if (MsgConstant.MSG_TAXORG_SHARE_CLASS.equals(idto
						.getStaxorgcode())
						&& MsgConstant.RULE_SIGN_ALL.equals(sbelong)) {
					String bhclause = " and S_TAXORGCODE in ('"
							+ MsgConstant.MSG_TAXORG_NATION_CLASS
							+ "','"
							+ MsgConstant.MSG_TAXORG_PLACE_CLASS
							+ "','"
							+ MsgConstant.MSG_TAXORG_CUSTOM_CLASS
							+ "','"
							+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
							+ "','"
							+ MsgConstant.MSG_TAXORG_OTHER_CLASS
							+ "') "
							+ "and S_TRECODE = '0288000000' and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL
							+ "' ORDER BY S_TRECODE, S_TAXORGCODE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE";
					queryDto.setSrptdate(srptdate);
					List<TrIncomedayrptDto> bhList = CommonFacade.getODB()
							.findRsByDtoForWhere(queryDto, bhclause);
					mergeBySubjects(bhList);
					resList.addAll(bhList);
				} else if ((MsgConstant.MSG_TAXORG_NATION_CLASS.equals(idto
						.getStaxorgcode()) || MsgConstant.MSG_TAXORG_PLACE_CLASS
						.equals(idto.getStaxorgcode()))
						&& MsgConstant.RULE_SIGN_ALL.equals(sbelong)) {
					String bhclause = " ORDER BY S_TRECODE, S_TAXORGCODE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE";
					queryDto.setSrptdate(srptdate);
					queryDto.setStaxorgcode(idto.getStaxorgcode());
					queryDto.setStrecode("0288000000");
					queryDto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
					List<TrIncomedayrptDto> bhList = CommonFacade.getODB()
							.findRsByDtoForWhere(queryDto, bhclause);
					mergeBySubjects(bhList);
					resList.addAll(bhList);
				}
			}
			// ��������ϲ�
			mergeProxyTreSameData(resList, taxorgmap, proxyTreList, map);

			if (resList.size() > 0) {
				// �õ�����ļ�����
				Map<String, String> taxorg = this.converTaxCode(sbookorgcode);
				HashMap<String, TsBudgetsubjectDto> subMap = SrvCacheFacade.cacheTsBdgsbtInfo(sbookorgcode);
				//��ȡȫ������¼��Ŀ�Ŀ����
				TsBudgetsubjectDto tdto = new TsBudgetsubjectDto();
				tdto.setSorgcode(sbookorgcode);
				tdto.setSwriteflag(StateConstant.COMMON_NO);
				CommonQto qto = SqlUtil.IDto2CommonQto(tdto);
				List <TsBudgetsubjectDto> tlist =  DatabaseFacade.getODB().findWithUR(tdto.getClass(), qto);
				for (TsBudgetsubjectDto t :tlist) {
					subMap.put(t.getSsubjectcode(), t);
				}
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf;
				if (StateConstant.REPORTTYPE_FLAG_AMOUNTBILL.equals(rptType)) {// �ܶ�ֳɱ���ȥ����jgdm���С�
					filebuf = new StringBuffer(
							"skgkdm,mdgkdm,ssgkdm,ysjc,kmdm,zwrq,yszl,rlj,ylj,nlj\r\n");
				} else {
					filebuf = new StringBuffer(
							"skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj\r\n");
				}

				// �õ��������ļ���
				TsTreasuryPK trepk = new TsTreasuryPK();
				trepk.setSorgcode(sbookorgcode);
				trepk.setStrecode(strecode);
				TsTreasuryDto tredto = (TsTreasuryDto) DatabaseFacade.getODB()
						.find(trepk);
				String tredtolevel = tredto.getStrelevel();
				// ���շ�����ϼƣ����룩
				BigDecimal centralsumamt = new BigDecimal("0.00");
				// ���շ�����ϼƣ�ʡ��
				BigDecimal provincesumamt = new BigDecimal("0.00");
				// ���շ�����ϼƣ��У�
				BigDecimal citysumamt = new BigDecimal("0.00");
				// ���շ�����ϼƣ��أ�
				BigDecimal countysumamt = new BigDecimal("0.00");
				// ����ϼ����ۼ�
				BigDecimal daysumamt = new BigDecimal("0.00");
				// ����ϼ����ۼ�
				BigDecimal mounthsumamt = new BigDecimal("0.00");
				// ����ϼ����ۼ�
				BigDecimal yearsumamt = new BigDecimal("0.00");
				// ��������Map
				HashMap<String, BigDecimal> levelMapMoney = new HashMap<String, BigDecimal>();
				// ����Map
				HashMap<String, HashMap> returnMap = new HashMap<String, HashMap>();
				for (int i = 0; i < resList.size(); i++) {
					TrIncomedayrptDto _dto = resList.get(i);
					TrIncomedayrptDto _dtonext = new TrIncomedayrptDto();
					if (i < resList.size() - 1) {
						_dtonext = resList.get(i + 1);
					}
					if (!StateConstant.REPORTTYPE_FLAG_AMOUNTBILL
							.equals(rptType)
							&& taxorg.get(_dto.getStaxorgcode()) == null) {
						// ����¼��ⲻά�����ջ��ض��չ�ϵ����д��Ӧ�Ĵ���
						if ("1".equals(_dto.getStaxorgcode().substring(0, 1))) {// ��˰
							taxorg.put(_dto.getStaxorgcode(),
									MsgConstant.MSG_TAXORG_NATION_CLASS
											.substring(0, 10));
						} else if ("2".equals(_dto.getStaxorgcode().substring(
								0, 1))) {// ��˰
							taxorg.put(_dto.getStaxorgcode(),
									MsgConstant.MSG_TAXORG_NATION_CLASS
											.substring(0, 10));
						} else if ("3".equals(_dto.getStaxorgcode().substring(
								0, 1))) {// ����
							taxorg.put(_dto.getStaxorgcode(),
									MsgConstant.MSG_TAXORG_CUSTOM_CLASS
											.substring(0, 10));
						} else if ("4".equals(_dto.getStaxorgcode().substring(
								0, 1))) {// ����
							taxorg.put(_dto.getStaxorgcode(),
									MsgConstant.MSG_TAXORG_FINANCE_CLASS
											.substring(0, 10));
						} else if ("5".equals(_dto.getStaxorgcode().substring(
								0, 1))) {// ����
							taxorg.put(_dto.getStaxorgcode(),
									MsgConstant.MSG_TAXORG_OTHER_CLASS
											.substring(0, 10));
						}
						// throw new ITFEBizException("��������" + sbookorgcode
						// + "��TCBS���ջ��ش��루" + _dto.getStaxorgcode()
						// + "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
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
					String level = _dto.getSbudgetlevelcode();
					filebuf.append(_dto.getSbudgetlevelcode());
					if (MsgConstant.BUDGET_LEVEL_CENTER.equals(level)) {
						centralsumamt = centralsumamt.add(_dto.getNmoneyday());
					} else if (MsgConstant.BUDGET_LEVEL_PROVINCE.equals(level)) {
						provincesumamt = provincesumamt
								.add(_dto.getNmoneyday());
					} else if (MsgConstant.BUDGET_LEVEL_DISTRICT.equals(level)) {
						citysumamt = citysumamt.add(_dto.getNmoneyday());
					} else if (MsgConstant.BUDGET_LEVEL_PREFECTURE
							.equals(level)) {
						countysumamt = countysumamt.add(_dto.getNmoneyday());
					}
					filebuf.append(splitSign);
					if (!StateConstant.REPORTTYPE_FLAG_AMOUNTBILL
							.equals(rptType)) {// �ܶ�ֳɱ���ȥ����jgdm���С�
						filebuf.append(taxorg.get(_dto.getStaxorgcode()));
						filebuf.append(splitSign);
					}
					filebuf.append(_dto.getSbudgetsubcode());
					filebuf.append(splitSign);
					filebuf.append(srptdate);
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
					if(i==resList.size()-1)
						log.error(i);
					if (MsgConstant.INPUT_SIGN_YES.equals(slesumitem)) {
						if (i == resList.size() - 1&&MsgConstant.INPUT_SIGN_NO.equals(subMap.get(_dto.getSbudgetsubcode().substring(0, 5)).getSwriteflag())) {
							daysumamt = daysumamt.add(_dto.getNmoneyday());
							mounthsumamt = mounthsumamt.add(_dto
									.getNmoneymonth());
							yearsumamt = yearsumamt.add(_dto.getNmoneyyear());
							filebuf.append("\r\n");
							filebuf.append(_dto.getStrecode());
							filebuf.append(splitSign);
							filebuf.append(_dto.getStrecode());
							filebuf.append(splitSign);
							filebuf.append(strecode);
							filebuf.append(splitSign);
							filebuf.append(_dto.getSbudgetlevelcode());
							filebuf.append(splitSign);
							if (!StateConstant.REPORTTYPE_FLAG_AMOUNTBILL
									.equals(rptType)) {// �ܶ�ֳɱ���ȥ����jgdm���С�
								filebuf.append(taxorg
										.get(_dto.getStaxorgcode()));
								filebuf.append(splitSign);
							}
							filebuf.append(_dto.getSbudgetsubcode().substring(
									0, 5));
							filebuf.append(splitSign);
							filebuf.append(srptdate);
							filebuf.append(splitSign);
							filebuf.append(_dto.getSbudgettype());
							filebuf.append(splitSign);
							filebuf.append(daysumamt);
							filebuf.append(splitSign);
							filebuf.append(mounthsumamt);
							filebuf.append(splitSign);
							filebuf.append(yearsumamt);
							daysumamt = new BigDecimal("0.00");
							mounthsumamt = new BigDecimal("0.00");
							yearsumamt = new BigDecimal("0.00");
						} else{
							if (_dto.getStrecode().equals(
									_dtonext.getStrecode())
									&& taxorg.get(_dto.getStaxorgcode())
											.equals(
													taxorg.get(_dtonext
															.getStaxorgcode()))
									&& _dto.getSbudgetlevelcode().equals(
											_dtonext.getSbudgetlevelcode())
									&& _dto
											.getSbudgetsubcode()
											.substring(0, 5)
											.equals(
													_dtonext
															.getSbudgetsubcode()
															.substring(0, 5))&&MsgConstant.INPUT_SIGN_NO.equals(subMap.get(_dto.getSbudgetsubcode().substring(0, 5)).getSwriteflag())) {
								daysumamt = daysumamt.add(_dto.getNmoneyday());
								mounthsumamt = mounthsumamt.add(_dto
										.getNmoneymonth());
								yearsumamt = yearsumamt.add(_dto
										.getNmoneyyear());
							} else if(MsgConstant.INPUT_SIGN_NO.equals(subMap.get(_dto.getSbudgetsubcode().substring(0, 5)).getSwriteflag())){
								daysumamt = daysumamt.add(_dto.getNmoneyday());
								mounthsumamt = mounthsumamt.add(_dto
										.getNmoneymonth());
								yearsumamt = yearsumamt.add(_dto
										.getNmoneyyear());
								filebuf.append(_dto.getStrecode());
								filebuf.append(splitSign);
								filebuf.append(_dto.getStrecode());
								filebuf.append(splitSign);
								filebuf.append(strecode);
								filebuf.append(splitSign);
								filebuf.append(_dto.getSbudgetlevelcode());
								filebuf.append(splitSign);
								if (!StateConstant.REPORTTYPE_FLAG_AMOUNTBILL
										.equals(rptType)) {// �ܶ�ֳɱ���ȥ����jgdm���С�
									filebuf.append(taxorg.get(_dto
											.getStaxorgcode()));
									filebuf.append(splitSign);
								}
								filebuf.append(_dto.getSbudgetsubcode()
										.substring(0, 5));
								filebuf.append(splitSign);
								filebuf.append(srptdate);
								filebuf.append(splitSign);
								filebuf.append(_dto.getSbudgettype());
								filebuf.append(splitSign);
								filebuf.append(daysumamt);
								filebuf.append(splitSign);
								filebuf.append(mounthsumamt);
								filebuf.append(splitSign);
								filebuf.append(yearsumamt);
								filebuf.append("\r\n");
								daysumamt = new BigDecimal("0.00");
								mounthsumamt = new BigDecimal("0.00");
								yearsumamt = new BigDecimal("0.00");
							}
						}
					}
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				levelMapMoney.put(MsgConstant.BUDGET_LEVEL_CENTER,
						centralsumamt);
				levelMapMoney.put(MsgConstant.BUDGET_LEVEL_PROVINCE,
						provincesumamt);
				levelMapMoney
						.put(MsgConstant.BUDGET_LEVEL_DISTRICT, citysumamt);
				levelMapMoney.put(MsgConstant.BUDGET_LEVEL_PREFECTURE,
						countysumamt);
				returnMap.put(fullpath.replaceAll(root, ""), levelMapMoney);
				return returnMap;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}

	/**
	 * ���չ�����룬���ջ��أ�Ԥ���Ŀ��Ԥ�㼶��������������кϲ�����ɾ���ϲ��������
	 * 
	 * @param resList
	 */
	private void mergeSameData(List<TrIncomedayrptDto> resList) {

		List removelist = new ArrayList();
		for (int i = 0; i < resList.size(); i++) {// �ҵ�һ������
			TrIncomedayrptDto indto = resList.get(i);
			if (!"R".equals(indto.getSbudgetlevelcode())) {
				String treCode = indto.getStrecode();
				String taxcode = indto.getStaxorgcode();
				String subcode = indto.getSbudgetsubcode();
				String level = indto.getSbudgetlevelcode();

				for (int j = i + 1; j < resList.size(); j++) {// �ҵ���һ������
					TrIncomedayrptDto another = resList.get(j);
					if (treCode.equals(another.getStrecode())
							&& level.equals(another.getSbudgetlevelcode())
							&& taxcode.equals(another.getStaxorgcode())
							&& subcode.equals(another.getSbudgetsubcode())) {
						resList.get(j).setSbudgetlevelcode("R");// ����ɾ�����
						resList.get(i).setNmoneyday(
								another.getNmoneyday()
										.add(indto.getNmoneyday()));
						// resList.get(i).setNmoneymonth(another.getNmoneymonth().add(indto.getNmoneymonth()));
						// resList.get(i).setNmoneyyear(another.getNmoneyyear().add(indto.getNmoneyyear()));
						removelist.add(another);
						break;
					}

				}
			}
		}
		resList.removeAll(removelist);
	}

	/**
	 * �����չ�����룬���ջ��ش��࣬Ԥ���Ŀ��Ԥ�㼶��������������кϲ�����ɾ���ϲ��������
	 * 
	 * @param resList
	 */
	private void mergeProxyTreSameData(List<TrIncomedayrptDto> resList,
			HashMap<String, String> taxorgmap, List<String> proxyTreList,
			HashMap map) {
		// ���������
		String agentDate = (String) map.get("agentDate");
		List removelist = new ArrayList();
		for (int i = 0; i < resList.size(); i++) {// �ҵ�һ������
			TrIncomedayrptDto indto = resList.get(i);
			if (!proxyTreList.contains(indto.getStrecode())) {
				continue;
			}
			if (!"R".equals(indto.getSbudgetlevelcode())) {
				String treCode = indto.getStrecode();
				String taxcodeprop = taxorgmap.get(indto.getStaxorgcode());
				String subcode = indto.getSbudgetsubcode();
				String level = indto.getSbudgetlevelcode();
				Date thedate = TimeFacade.parseDate(indto.getSrptdate());
				BigDecimal theamtmonth = indto.getNmoneymonth();
				BigDecimal theamtyear = indto.getNmoneyyear();
				for (int j = i + 1; j < resList.size(); j++) {// �ҵ���һ������
					TrIncomedayrptDto another = resList.get(j);
					Date anotherdate = TimeFacade.parseDate(another
							.getSrptdate());
					BigDecimal anotheramtmonth = another.getNmoneymonth();
					BigDecimal anotheramtyear = another.getNmoneyyear();
					if (treCode.equals(another.getStrecode())
							&& level.equals(another.getSbudgetlevelcode())
							&& taxcodeprop.equals(taxorgmap.get(another
									.getStaxorgcode()))
							&& subcode.equals(another.getSbudgetsubcode())) {
						resList.get(j).setSbudgetlevelcode("R");// ����ɾ�����
						if("59".contains(resList.get(j).getSbillkind()))//ȥ�����������
						{
							removelist.add(another);
							continue;
						}
						resList.get(i).setNmoneyday(
								another.getNmoneyday()
										.add(indto.getNmoneyday()));
						if (thedate.compareTo(anotherdate) == 0) {
							resList.get(i).setNmoneymonth(
									another.getNmoneymonth().add(
											indto.getNmoneymonth()));
							resList.get(i).setNmoneyyear(
									another.getNmoneyyear().add(
											indto.getNmoneyyear()));
						} else if (thedate.compareTo(anotherdate) > 0) {
							resList.get(i).setNmoneymonth(theamtmonth);
							resList.get(i).setNmoneyyear(theamtyear);
						} else if (thedate.compareTo(anotherdate) < 0) {
							resList.get(i).setNmoneymonth(anotheramtmonth);
							resList.get(i).setNmoneyyear(anotheramtyear);
						}
						removelist.add(another);
						break;
					}

				}
			}
		}
		resList.removeAll(removelist);
	}

	/**
	 * ���չ�����룬���ջ��أ�Ԥ���Ŀ���ؼ�����ϲ����м����룬�ϲ�ǰ������ɾ����û�кϲ����ؼ������޸�Ϊ�м�
	 * 
	 * @param resList
	 */
	private void mergeBySubjects(List<TrIncomedayrptDto> resList) {

		List inDays = new ArrayList();
		for (int i = 0; i < resList.size(); i++) {// �ҵ�һ������
			TrIncomedayrptDto indto = resList.get(i);

			if (MsgConstant.BUDGET_LEVEL_DISTRICT.equals(indto
					.getSbudgetlevelcode())) {
				String treCode = indto.getStrecode();
				String taxcode = indto.getStaxorgcode();
				String subcode = indto.getSbudgetsubcode();

				for (int j = 0; j < resList.size(); j++) {// �ҵ���һ������
					TrIncomedayrptDto another = resList.get(j);
					if (treCode.equals(another.getStrecode())
							&& MsgConstant.BUDGET_LEVEL_PREFECTURE
									.equals(another.getSbudgetlevelcode())
							&& taxcode.equals(another.getStaxorgcode())
							&& subcode.equals(another.getSbudgetsubcode())) {
						resList.get(i).setNmoneyday(
								another.getNmoneyday()
										.add(indto.getNmoneyday()));
						resList.get(i).setNmoneymonth(
								another.getNmoneymonth().add(
										indto.getNmoneymonth()));
						resList.get(i).setNmoneyyear(
								another.getNmoneyyear().add(
										indto.getNmoneyyear()));
						inDays.add(another);
						break;
					}

				}
			}
		}
		resList.removeAll(inDays);
		for (int i = 0; i < resList.size(); i++) {
			TrIncomedayrptDto indto = resList.get(i);
			if (MsgConstant.BUDGET_LEVEL_PREFECTURE.equals(resList.get(i)
					.getSbudgetlevelcode())) {
				resList.get(i).setSbudgetlevelcode(
						MsgConstant.BUDGET_LEVEL_DISTRICT);
			}
		}

	}

	private Map<String, String> converTaxCode(String orgCode)
			throws JAFDatabaseException, ValidateException {
		TsConverttaxorgDto dto = new TsConverttaxorgDto();
		dto.setSorgcode(orgCode);
		List<TsConverttaxorgDto> list = CommonFacade.getODB().findRsByDto(dto);
		Map<String, String> taxMap = new HashMap<String, String>();
		for (TsConverttaxorgDto tmpdto : list) {
			taxMap.put(tmpdto.getStcbstaxorgcode(), tmpdto.getStbstaxorgcode());
		}

		// ��˰
		taxMap.put(MsgConstant.MSG_TAXORG_NATION_CLASS,
				MsgConstant.MSG_TAXORG_NATION_CLASS.substring(0, 10));
		// ��˰
		taxMap.put(MsgConstant.MSG_TAXORG_PLACE_CLASS,
				MsgConstant.MSG_TAXORG_PLACE_CLASS.substring(0, 10));
		// ����
		taxMap.put(MsgConstant.MSG_TAXORG_CUSTOM_CLASS,
				MsgConstant.MSG_TAXORG_CUSTOM_CLASS.substring(0, 10));
		// ����
		taxMap.put(MsgConstant.MSG_TAXORG_FINANCE_CLASS,
				MsgConstant.MSG_TAXORG_FINANCE_CLASS.substring(0, 10));
		// ����
		taxMap.put(MsgConstant.MSG_TAXORG_OTHER_CLASS,
				MsgConstant.MSG_TAXORG_OTHER_CLASS.substring(0, 10));
		// �������ջ���
		taxMap.put(MsgConstant.MSG_TAXORG_SHARE_CLASS,
				MsgConstant.MSG_TAXORG_SHARE_CLASS.substring(0, 10));

		return taxMap;

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
				_dto.setStaxorgcode(idto.getSfinorgcode());
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
	 * ���Ӷ������Ŀ����Ҫ���������Ƴ�Ʒ��˰�յ�û�о������ջ��أ���ͳ�Ʊ����α���ʱ�޷�ͳ�Ƶ� �Ŀ�Ŀ�����
	 * 
	 * @param idto
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	private List<TrIncomedayrptDto> procSpecBudsubjectForTJ(
			TrIncomedayrptDto idto, String rptType, String sbookorgcode,
			HashMap map) throws JAFDatabaseException, ValidateException {

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

		TrIncomedayrptDto queryDto2 = new TrIncomedayrptDto();
		String agentDate = (String) map.get("agentDate");
		String agentEndDate = (String) map.get("agentEndDate");// ��ĩ���������
		// queryDto2.setSrptdate(agentDate);
		queryDto2.setSbillkind(rptType);
		queryDto2.setStrimflag(strimflag);
		queryDto2.setSbudgettype(bugtype);
		queryDto2.setSbelongflag(MsgConstant.RULE_SIGN_SELF);
		// queryDto2.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
		// ȡ��Ͻ�������ͳ�ƹ���������Ŀ��ȫϽ��
		sqlwhere = "";
		if ("0200000000".equals(strecode)) {
			sqlwhere += " AND  s_trecode <> '0288000000'";
		}
		if ("".equals(agentEndDate)) {
			sqlwhere += " AND S_RPTDATE ='" + agentDate + "'";
		} else {
			sqlwhere += " AND (S_RPTDATE ='" + agentDate + "' OR S_RPTDATE ='"
					+ agentEndDate + "')";
		}
		sqlwhere += " AND s_trecode  IN ( select S_TRECODE from TS_TREASURY where S_GOVERNTRECODE = '"
				+ strecode
				+ "' AND S_TREATTRIB ='2') "
				+ " AND S_TAXORGCODE LIKE '4%' "
				+ " AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where s_orgcode = '"
				+ sbookorgcode + "' order by S_BUDGETSUBCODE) ";
		List<TrIncomedayrptDto> listdown = CommonFacade.getODB()
				.findRsByDtoForWhere(queryDto2, sqlwhere);

		// ��������������������Ŀlist
		if ("0288000000".equals(strecode)) {
			TrIncomedayrptDto queryDto3 = new TrIncomedayrptDto();
			queryDto3.setSrptdate(srptdate);
			queryDto3.setSbillkind(rptType);
			queryDto3.setStrimflag(strimflag);
			queryDto3.setSbudgettype(bugtype);
			queryDto3.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
			queryDto3.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			// ȡ��Ͻ�������ͳ�ƹ���������Ŀ��ȫϽ��
			sqlwhere = "";
			sqlwhere += " AND  s_trecode  IN ( select S_TRECODE from TS_TREASURY where S_GOVERNTRECODE = '"
					+ strecode
					+ "' AND S_TREATTRIB ='1') "
					+ " AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where s_orgcode = '"
					+ sbookorgcode + "' order by S_BUDGETSUBCODE) ";
			List<TrIncomedayrptDto> kflist = CommonFacade.getODB()
					.findRsByDtoForWhere(queryDto3, sqlwhere);
			listdown.addAll(kflist);
		}

		// ɾ��û��ѡ�еĹ�������
		List<TrIncomedayrptDto> rmList = new ArrayList<TrIncomedayrptDto>();
		HashMap treLevels = (HashMap) map.get("treLevels");
		treLevels.remove("0288000000");
		Set set = treLevels.keySet();
		for (Object key : set) {
			String skey = (String) key;
			ArrayList list = (ArrayList) treLevels.get(skey);
			if (list.size() != 0) {
				for (int j = 0; j < listdown.size(); j++) {
					TrIncomedayrptDto another = listdown.get(j);
					if (another.getStrecode().equals(skey)
							&& list.contains(another.getSbudgetlevelcode())) {
						rmList.add(another);
					}
				}
			}
		}
		listdown.removeAll(rmList);
		// ���ֿ⵼��ʱ��ѯ����������������ȫϽ
		if ("0200000000".equals(strecode)) {
			if (MsgConstant.MSG_TAXORG_SHARE_CLASS
					.equals(idto.getStaxorgcode())
					&& MsgConstant.RULE_SIGN_ALL.equals(sbelong)) {
				String bhclause = " and S_TAXORGCODE in ('"
						+ MsgConstant.MSG_TAXORG_FINANCE_CLASS
						+ "') "
						+ "and S_TRECODE = '0288000000' AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
						+ sbookorgcode + "' )";
				queryDto.setSrptdate(srptdate);
				List<TrIncomedayrptDto> bhList = CommonFacade.getODB()
						.findRsByDtoForWhere(queryDto, bhclause);
				listdown.addAll(bhList);
			}
		}

		// ��Ŀʵ�ʷ����� = ����ȫϽ�� --��Ͻ�����ȫϽ���ݣ�����Ŀѭ��
		if (null != listup && listup.size() > 0) {
			for (TrIncomedayrptDto _dto : listup) {
				String sbtcode = _dto.getSbudgetsubcode();
				String budlevel = _dto.getSbudgetlevelcode();
				_dto.setStaxorgcode(idto.getSfinorgcode());
				Date thedate = TimeFacade.parseDate(_dto.getSrptdate());
				for (TrIncomedayrptDto _dto2 : listdown) {
					Date anotherdate = TimeFacade
							.parseDate(_dto2.getSrptdate());
					String sbtcode2 = _dto2.getSbudgetsubcode();
					String budlevel2 = _dto2.getSbudgetlevelcode();
					if (sbtcode.equals(sbtcode2) && budlevel.equals(budlevel2)) {
						_dto.setNmoneyday(_dto.getNmoneyday().add(
								_dto2.getNmoneyday().negate()));
						if ("".equals(agentEndDate)) {// ������ĩ����������
							_dto.setNmoneymonth(_dto.getNmoneymonth().add(
									_dto2.getNmoneymonth().negate()));
							_dto.setNmoneyyear(_dto.getNmoneyyear().add(
									_dto2.getNmoneyyear().negate()));
						} else {
							if (thedate.compareTo(anotherdate) == 0) {// ��ĩֻ��ȥ���һ����������
								_dto.setNmoneymonth(_dto.getNmoneymonth().add(
										_dto2.getNmoneymonth().negate()));
								_dto.setNmoneyyear(_dto.getNmoneyyear().add(
										_dto2.getNmoneyyear().negate()));
							}
						}
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

	/**
	 * ��ȡ���ջ������ʵ�map
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	private HashMap<String, String> findTaxorgMap(String sbookorgcode)
			throws JAFDatabaseException, ValidateException {
		// ������ջ��ص�list
		TsTaxorgDto qto = new TsTaxorgDto();
		qto.setSorgcode(sbookorgcode);
		List<TsTaxorgDto> bslist = bslist = CommonFacade.getODB().findRsByDto(
				qto);
		HashMap<String, String> map = new HashMap<String, String>();
		for (TsTaxorgDto dto : bslist) {
			map.put(dto.getStaxorgcode(), dto.getStaxprop());
		}
		return map;
	}

	/**
	 * ��ȡ�¼��Ĵ������list
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	private List<String> findProxyList(String sbookorgcode, String sgoventrecode)
			throws JAFDatabaseException, ValidateException {
		// ��ô�������list
		TsTreasuryDto qto = new TsTreasuryDto();
		qto.setSorgcode(sbookorgcode);
		qto.setStreattrib("2");// �������
		qto.setSgoverntrecode(sgoventrecode);
		List<TsTreasuryDto> bslist = CommonFacade.getODB().findRsByDto(qto);
		List<String> proxyTreList = new ArrayList<String>();
		for (TsTreasuryDto dto : bslist) {
			proxyTreList.add(dto.getStrecode());
		}
		return proxyTreList;
	}

}
