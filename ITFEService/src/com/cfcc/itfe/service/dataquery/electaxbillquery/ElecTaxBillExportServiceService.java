package com.cfcc.itfe.service.dataquery.electaxbillquery;

import java.util.*;
import java.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;
import com.cfcc.itfe.service.dataquery.electaxbillquery.AbstractElecTaxBillExportServiceService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author Administrator
 * @time 13-09-23 10:26:46 codecomment:
 */
@SuppressWarnings("unchecked")
public class ElecTaxBillExportServiceService extends
		AbstractElecTaxBillExportServiceService {
	private static Log log = LogFactory
			.getLog(ElecTaxBillExportServiceService.class);

	/**
	 * ������������˰Ʊ
	 * 
	 */
	public String exportfile(IDto idto) throws ITFEBizException {
		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		TvFinIncomeDetailDto dto = (TvFinIncomeDetailDto) idto;
		String trecode =  dto.getStrecode();
		String filename = "";
		if(StringUtils.isNotBlank(trecode)){
			filename = dto.getSintredate() + "_" + trecode + "_elecTaxBill.del";
		}else{
			filename = dto.getSintredate() + "_0000000000_elecTaxBill.del";
		}
		
		try {
			String filepath = ITFECommonConstant.FILE_ROOT_PATH + currentDate
					+ dirsep + getLoginInfo().getSorgcode() + dirsep
					+ filename;
			StringBuffer wheresql = new StringBuffer();
			if(StringUtils.isNotBlank(dto.getSintredate())){
				wheresql.append(" S_INTREDATE >= '" + dto.getSintredate() + "'");//������
			}
			if(StringUtils.isNotBlank(dto.getIpkgseqno())){
				wheresql.append(" AND S_INTREDATE <= '" + dto.getIpkgseqno() + "'");//����ֹ
			}
			if(StringUtils.isNotBlank(dto.getStrecode())&&!dto.getStrecode().contains("00000000"))
			{
				if("1".equals(dto.getSorgcode()))//ȫϽ
				{
					CommonDataAccessService ser = new CommonDataAccessService();
					TsTreasuryDto tredto = new TsTreasuryDto();
					tredto.setStrecode(dto.getStrecode());
					List<TsTreasuryDto> findList = ser.getSubTreCode(tredto);
					if(findList!=null&&findList.size()>0)
					{
						wheresql.append(" AND (");
						for(int i=0;i<findList.size();i++)
						{
							if(i==0)
								wheresql.append("S_TRECODE = '"+findList.get(i).getStrecode()+"'");
							else
								wheresql.append(" OR S_TRECODE = '"+findList.get(i).getStrecode()+"'");
						}
						wheresql.append(")");
					}
				}else
				{
					wheresql.append(" AND S_TRECODE = '" + dto.getStrecode() + "'");//�������
				}
			}
			if(StringUtils.isNotBlank(dto.getCvouchannel())){
				wheresql.append(" AND C_VOUCHANNEL = '" + dto.getCvouchannel() + "'");//ƾ֤��Դ
			}
			if(StringUtils.isNotBlank(dto.getCbdgkind())){
				wheresql.append(" AND C_BDGKIND = '" + dto.getCbdgkind() + "'");//Ԥ������
			}
			if(StringUtils.isNotBlank(dto.getSexpvoutype())){
				wheresql.append(" AND S_EXPVOUTYPE = '" + dto.getSexpvoutype() + "'");//ƾ֤����
			}
			if(StringUtils.isNotBlank(dto.getStaxorgcode())){
				wheresql.append(" AND S_TAXORGCODE like '" + dto.getStaxorgcode() + "%'");//���ջ��ش���
			}
			if(StringUtils.isNotBlank(dto.getSexpvouno())){
				wheresql.append(" AND S_EXPVOUNO like '" + dto.getSexpvouno() + "%'");//ƾ֤���
			}
			if(StringUtils.isNotBlank(dto.getSbdgsbtcode())){
				wheresql.append(" AND S_BDGSBTCODE like '" + dto.getSbdgsbtcode() + "%'");//Ԥ���Ŀ����
			}
			StringBuffer sql = new StringBuffer();
			sql.append(PublicSearchFacade.getSqlConnectByProp());
			sql.append("export to " + filepath + " of del ");
			sql
					.append("SELECT *  FROM TV_FIN_INCOMEONLINE a INNER JOIN (SELECT DISTINCT S_EXPVOUNO FROM TV_FIN_INCOME_DETAIL WHERE " + wheresql.toString());
			sql
					.append(" UNION  SELECT DISTINCT  S_EXPVOUNO FROM HTV_FIN_INCOME_DETAIL  WHERE " + wheresql.toString() +" ) b ON a.S_TAXVOUNO = b.S_EXPVOUNO UNION ( ");
			sql
					.append("SELECT *  FROM HTV_FIN_INCOMEONLINE a INNER JOIN (SELECT DISTINCT  S_EXPVOUNO FROM TV_FIN_INCOME_DETAIL WHERE " + wheresql.toString() +" UNION  SELECT DISTINCT  S_EXPVOUNO FROM HTV_FIN_INCOME_DETAIL WHERE " + wheresql.toString() +" ) b ON a.S_TAXVOUNO = b.S_EXPVOUNO );\n");
			sql.append("connect reset;");
			FileUtil.getInstance().writeFile(
					filepath.replaceAll(".del", ".sql"), sql.toString());

			long start = System.currentTimeMillis();
			byte[] bytes = DB2CallShell.dbCallShellWithRes(filepath.replaceAll(
					".del", ".sql"));
			String results;
			if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
				results = new String(bytes, 0,
						MsgConstant.MAX_CALLSHELL_RS * 1024);
			} else {
				results = new String(bytes);
			}
			log.info(results + "\n��ʱ��" + (System.currentTimeMillis() - start)/1000 + "S\n");
			File file = new File(filepath);
			if(!file.exists()){
				return null;
			}else{
				if(file.length() == 0){
					return null;
				}else{
					return filepath.replaceAll(ITFECommonConstant.FILE_ROOT_PATH, "");
				}
				
			}
			
		} catch (Throwable e) {
			log.error("����������˰Ʊ��Ϣʧ�ܣ�", e);
			throw new ITFEBizException("����������˰Ʊ��Ϣʧ�ܣ�", e);
		} 

	}

	public List busworkcount(Map paramMap) throws ITFEBizException {
		List getList = null;
		if(paramMap!=null&&paramMap.get("dto")!=null)
		{
			SQLExecutor selectsqlExec = null;
			SQLResults rs = null;
			try{
				TvFinIncomeDetailDto dto = (TvFinIncomeDetailDto)paramMap.get("dto");
				StringBuffer wheresql = new StringBuffer();
				List paramlist = new ArrayList();
				if(StringUtils.isNotBlank(dto.getSintredate())){
					wheresql.append(" S_INTREDATE >= ?");//������
					paramlist.add(dto.getSintredate());
				}
				if(StringUtils.isNotBlank(dto.getIpkgseqno())){
					wheresql.append(" AND S_INTREDATE <= ?");//����ֹ
					paramlist.add(dto.getIpkgseqno());
				}
				if(StringUtils.isNotBlank(dto.getStrecode()))
				{
					if("1".equals(dto.getSorgcode())&&!dto.getStrecode().contains("00000000"))//����ΧȫϽ
					{
						CommonDataAccessService ser = new CommonDataAccessService();
						TsTreasuryDto tredto = new TsTreasuryDto();
						tredto.setStrecode(dto.getStrecode());
						List<TsTreasuryDto> findList = ser.getSubTreCode(tredto);
						if(findList!=null&&findList.size()>0)
						{
							wheresql.append(" AND (");
							for(int i=0;i<findList.size();i++)
							{
								if(i==0)
								{
									wheresql.append("S_TRECODE = ?");
									paramlist.add(findList.get(i).getStrecode());
								}
								else
								{
									wheresql.append(" OR S_TRECODE = ?");
									paramlist.add(findList.get(i).getStrecode());
								}
							}
							wheresql.append(")");
						}
					}else if("0".equals(dto.getSorgcode()))
					{
						wheresql.append(" AND S_TRECODE = ?");//�������
						paramlist.add(dto.getStrecode());
					}
				}
				if(StringUtils.isNotBlank(dto.getCvouchannel())){
					wheresql.append(" AND C_VOUCHANNEL = ?");//ƾ֤��Դ
					paramlist.add(dto.getCvouchannel());
				}
				if(StringUtils.isNotBlank(dto.getCbdgkind())){
					wheresql.append(" AND C_BDGKIND = ?");//Ԥ������
					paramlist.add(dto.getCbdgkind());
				}
				if(StringUtils.isNotBlank(dto.getSexpvoutype())){
					wheresql.append(" AND S_EXPVOUTYPE = ?");//ƾ֤����
					paramlist.add(dto.getSexpvoutype());
				}
				if(StringUtils.isNotBlank(dto.getStaxorgcode())){
					wheresql.append(" AND S_TAXORGCODE like ?");//���ջ��ش���
					paramlist.add(dto.getStaxorgcode()+"%");
				}
				if(StringUtils.isNotBlank(dto.getSexpvouno())){
					wheresql.append(" AND S_EXPVOUNO like ?");//ƾ֤���
					paramlist.add(dto.getSexpvouno()+"%");
				}
				if(StringUtils.isNotBlank(dto.getSbdgsbtcode())){
					wheresql.append(" AND S_BDGSBTCODE like ?");//Ԥ���Ŀ����
					paramlist.add(dto.getSbdgsbtcode()+"%");
				}
				StringBuffer sql = new StringBuffer("SELECT C_VOUCHANNEL,sum(scount),sum(F_AMT) FROM(");
				sql.append("SELECT C_VOUCHANNEL,count(*) AS scount,sum(F_AMT) AS F_AMT FROM TV_FIN_INCOME_DETAIL WHERE " + wheresql.toString()+" GROUP BY C_VOUCHANNEL");
				sql.append(" UNION SELECT C_VOUCHANNEL,count(*),sum(F_AMT) FROM HTV_FIN_INCOME_DETAIL WHERE " + wheresql.toString()+" GROUP BY C_VOUCHANNEL");
				sql.append(")GROUP BY C_VOUCHANNEL");
				selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				for(int i=0;i<paramlist.size();i++)
				{
					selectsqlExec.addParam(paramlist.get(i));
				}
				for(int i=0;i<paramlist.size();i++)
				{
					selectsqlExec.addParam(paramlist.get(i));
				}
				rs = selectsqlExec.runQueryCloseCon(sql.toString());
				if(rs!=null&&rs.getRowCount()>0)
				{
					Map getMap = null;
					getList = new ArrayList();
					for(int i=0;i<rs.getRowCount();i++)
					{
						getMap = new HashMap();
						getMap.put("type", rs.getObject(i, 0));
						getMap.put("allcount", rs.getObject(i, 1));
						getMap.put("allamt", rs.getObject(i, 2));
						getList.add(getMap);
					}
				}
			} catch (Exception e) {
				if (null != selectsqlExec) {
					selectsqlExec.closeConnection();
				}
				log.error("�����ˮҵ����ͳ�Ƴ����쳣!", e);
				throw new ITFEBizException("�����ˮҵ����ͳ�Ƴ����쳣!", e);
			}finally
			{
				if (null != selectsqlExec) {
					selectsqlExec.closeConnection();
				}
			}
			
		}
		return getList;
	}

}