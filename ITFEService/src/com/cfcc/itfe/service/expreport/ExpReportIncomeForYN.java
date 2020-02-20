package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class ExpReportIncomeForYN extends AbstractExpReport{
		private final Log log = LogFactory.getLog(ExpReportIncomeForYN.class);

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
			String srptdateend = idto.getSbudgetsubname();
			// �Ƿ񺬿�ϼ�
			String slesumitem = idto.getSdividegroup();
			//Ԥ���Ŀ���룬���ݴ����Ԥ���Ŀ�������ò�ѯ����
			String sfuncbdgsbtcode = idto.getSbudgetsubcode();
			// �����ѯ����
			StringBuffer sqlWhere = new StringBuffer("select * from TR_INCOMEDAYRPT where 1=1 ");
			//String sqlWhere="select * from TR_INCOMEDAYRPT where 1=1 ";
			
			// ���ݱ��еı�������
			String rptType = CommonMakeReport
					.getReportTypeByBillType(idto, bizType);
			/*TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
			queryDto.setSrptdate(srptdate);
			queryDto.setSbillkind(rptType);
			queryDto.setStrimflag(strimflag);
			queryDto.setSbudgettype(bugtype);
			queryDto.setSfinorgcode(idto.getSfinorgcode());*/
			sqlWhere.append(CommonMakeReport.makesqlwherebyarea(idto,sbookorgcode));
			List<TrIncomedayrptDto> resList = new ArrayList<TrIncomedayrptDto>();// ���뱨��
			String filename = CommonMakeReport.getExpFileNameByBillType(idto,
					bizType);
			SQLExecutor sqlExec = null;
			try {
				sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				if(srptdate!=null&&!srptdate.equals("")){
					sqlWhere.append(" and S_RPTDATE >= ? ");
					sqlExec.addParam(srptdate);
				}
				if(srptdateend!=null&&!srptdateend.equals("")){
					sqlWhere.append(" and S_RPTDATE <= ? ");
					sqlExec.addParam(srptdateend);
				}
				if(rptType!=null&&!rptType.equals("")){
					sqlWhere.append(" and S_BILLKIND = ? ");
					sqlExec.addParam(rptType);
				}
				if(strimflag!=null&&!strimflag.equals("")){
					sqlWhere.append(" and S_TRIMFLAG = ? ");
					sqlExec.addParam(strimflag);
				}
				if(bugtype!=null&&!bugtype.equals("")){
					sqlWhere.append(" and S_BUDGETTYPE = ? ");
					sqlExec.addParam(bugtype);
				}
				if(idto.getSfinorgcode()!=null&&!idto.getSfinorgcode().equals("")){
					sqlWhere.append(" and S_FINORGCODE = ? ");
					sqlExec.addParam(idto.getSfinorgcode());
				}
	            /*if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)&&idto.getSbudgetlevelcode().equals(StateConstant.SELF_AREA)) {
		            List <TrIncomedayrptDto>  specList = procSpecBudsubject(idto, rptType, sbookorgcode);
		            resList.addAll(specList);
		            //���˵�����Ԥ���Ŀ
		            sqlWhere += " AND S_BUDGETSUBCODE NOT IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
						+ sbookorgcode + "')";
				}*/
	            
	            if(null!=sfuncbdgsbtcode&&""!=sfuncbdgsbtcode.trim()){
	            	sfuncbdgsbtcode = sfuncbdgsbtcode.replaceAll(",", "','");
	            	sqlWhere.append(" AND S_BUDGETSUBCODE IN ('"+sfuncbdgsbtcode+"')");
	            }
	            StringBuffer hsql = new StringBuffer(" union ");
	            hsql.append(sqlWhere.toString().replace("TR_INCOMEDAYRPT", "HTR_INCOMEDAYRPT"));
	            if(srptdate!=null&&!srptdate.equals("")){
					sqlExec.addParam(srptdate);
				}
				if(srptdateend!=null&&!srptdateend.equals("")){
					sqlExec.addParam(srptdateend);
				}
				if(rptType!=null&&!rptType.equals("")){
					sqlExec.addParam(rptType);
				}
				if(strimflag!=null&&!strimflag.equals("")){
					sqlExec.addParam(strimflag);
				}
				if(bugtype!=null&&!bugtype.equals("")){
					sqlExec.addParam(bugtype);
				}
				if(idto.getSfinorgcode()!=null&&!idto.getSfinorgcode().equals("")){
					sqlExec.addParam(idto.getSfinorgcode());
				}
	            sqlExec.setMaxRows(500000);
	            SQLResults rs = sqlExec.runQueryCloseCon(sqlWhere.toString()+hsql.toString());
	           
				if (rs.getRowCount() > 0) {
					// �õ�����ļ�����
					Map<String, String> taxorg = this.converTaxCode(sbookorgcode);
					String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
					String dirsep = File.separator; // ȡ��ϵͳ�ָ��
					String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
					String fullpath = root + "exportFile" + dirsep + strdate
							+ dirsep + filename;
					String splitSign = ","; // �ļ���¼�ָ�����
					StringBuffer filebuf = new StringBuffer(
							"skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj\r\n");
					
					//�õ��������ļ���
					TsTreasuryPK trepk = new TsTreasuryPK();
					trepk.setSorgcode(sbookorgcode);
					trepk.setStrecode(strecode);
					TsTreasuryDto tredto = (TsTreasuryDto)DatabaseFacade.getODB().find(trepk);
					String tredtolevel = tredto.getStrelevel();
					
					String ls_TaxOrgCode,ls_TreCode,ls_BudGetLevelCode,ls_BudGetSubCode,ls_RptDate,ls_BudGetType,ls_Nmoneyday,ls_Nmoneymonth,ls_Nmoneyyear;
					for (int i = 0; i < rs.getRowCount(); i++) {
						//TrIncomedayrptDto _dto = resList.get(i);
						ls_TaxOrgCode= rs.getString(i, 1);
						ls_TreCode= rs.getString(i, 2);
						ls_BudGetLevelCode= rs.getString(i, 5);
						ls_BudGetSubCode= rs.getString(i, 6);
						ls_RptDate= rs.getString(i, 3);
						ls_BudGetType= rs.getString(i, 4);
						ls_Nmoneyday= rs.getBigDecimal(i, 8).toPlainString();
						ls_Nmoneymonth= rs.getBigDecimal(i, 10).toPlainString();
						ls_Nmoneyyear= rs.getBigDecimal(i, 12).toPlainString();
						if (taxorg.get(ls_TaxOrgCode) == null) {
							throw new ITFEBizException("�����������" + sbookorgcode
									+ "��TCBS���ջ��ش��루" + ls_TaxOrgCode
									+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
						}
						//���ѡ���������ұ���ļ��δ��ڵ�ǰ����ļ����򲻵���
						if(MsgConstant.RULE_SIGN_SELF.equals(sbelong)&&ls_BudGetLevelCode.compareTo(tredtolevel)>0){
							continue;
						}
						
						filebuf.append(ls_TreCode);
						filebuf.append(splitSign);
						filebuf.append(ls_TreCode);
						filebuf.append(splitSign);
						filebuf.append(strecode);
						filebuf.append(splitSign);
						filebuf.append(ls_BudGetLevelCode);
						filebuf.append(splitSign);
						filebuf.append(taxorg.get(ls_TaxOrgCode));
						filebuf.append(splitSign);
						filebuf.append(ls_BudGetSubCode);
						filebuf.append(splitSign);
						filebuf.append(ls_RptDate);
						filebuf.append(splitSign);
						filebuf.append(ls_BudGetType);
						filebuf.append(splitSign);
						filebuf.append(ls_Nmoneyday);
						filebuf.append(splitSign);
						filebuf.append(ls_Nmoneymonth);
						filebuf.append(splitSign);
						filebuf.append(ls_Nmoneyyear);
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
			}finally{
				if(sqlExec!=null)
					sqlExec.closeConnection();
			}

		}
}
