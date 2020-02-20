package com.cfcc.itfe.service.dataquery.tvfinincomeonlinedetailblend;

import java.util.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.persistence.dto.SummaryReportDto;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto;
import com.cfcc.itfe.service.dataquery.tvfinincomeonlinedetailblend.AbstractTvFinIncomeonlineDetailBlendService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
/**
 * @author zhangliang
 * @time   14-11-14 10:11:57
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class TvFinIncomeonlineDetailBlendService extends AbstractTvFinIncomeonlineDetailBlendService {
	private static Log log = LogFactory.getLog(TvFinIncomeonlineDetailBlendService.class);

	public String adddata(TvIncomeonlineIncomeondetailBlendDto idtodata)
			throws ITFEBizException{
		String sseq = "";
		try {
			sseq = StampFacade.getStampSendSeq("JS"); // ȡ�������������ˮ��Ϣҵ����ˮ
			idtodata.setSseq(sseq);
			idtodata.setSremark3("�˹����");
			DatabaseFacade.getDb().create(idtodata);
		}catch (SequenceException e) {
			log.error(e);
			throw new ITFEBizException(
					"ȡ�����������˰Ʊ��Ϣҵ����ˮSEQ����:"+e.toString());
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(
					"����˰Ʊ�����ˮ�����˹����ʧ��:"+e.toString());
		}
		return null;
	}
	public Map searchBlend(TvIncomeonlineIncomeondetailBlendDto finddto,Map parammap) throws ITFEBizException {
		SQLExecutor execDetail = null;
		Map getMap = null;
		try
		{
			if(finddto!=null)
			{
				getMap = new HashMap();
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				StringBuffer sql = new StringBuffer("select count(*) as count from TV_FIN_INCOME_DETAIL where  S_INTREDATE='"+finddto.getSintredate()+"' ");//S_TRASTATE='20' and
//				if(isNotNull(finddto.getStaxvouno()))//˰Ʊ����
//					sql.append(" and S_TAXVOUNO='"+finddto.getStaxvouno()+"' ");
//				if(isNotNull(finddto.getSpackno()))//����ˮ��
//					sql.append(" and S_PACKNO='"+finddto.getSpackno()+"' ");
//				if(isNotNull(finddto.getSpaybnkno()))//�������к�
//					sql.append(" and S_PAYBNKNO='"+finddto.getSpaybnkno()+"' ");
				StringBuffer yh = new StringBuffer("");
				if(isNotNull(finddto.getStrecode()))//�������
				{
					sql.append(" and S_TRECODE='"+finddto.getStrecode()+"' ");
					yh.append(" S_TRECODE='"+finddto.getStrecode()+"' ");
				}else
				{
					List<TsTreasuryDto> tList=(List)execDetail.runQuery("select * from TS_TREASURY where S_ORGCODE='"+this.getLoginInfo().getSorgcode()+"'",TsTreasuryDto.class).getDtoCollection();
					if(tList!=null&&tList.size()>0)
					{
						
						TsTreasuryDto tDto = null;
						for(int i=0;i<tList.size();i++)
						{
							tDto = (TsTreasuryDto)tList.get(i);
							if(i==0)
							{
								sql.append(" and (S_TRECODE='"+tDto.getStrecode()+"' ");
								yh.append(" (S_TRECODE='"+tDto.getStrecode()+"' ");
							}else
							{
								sql.append(" or S_TRECODE='"+tDto.getStrecode()+"' ");
								yh.append(" or S_TRECODE='"+tDto.getStrecode()+"' ");
							}
						}
						sql.append(")");
						yh.append(")");
					}
				}
//				if(isNotNull(finddto.getStaxorgcode()))//���ջ��ش���
//					sql.append(" and S_TAXORGCODE='"+finddto.getStaxorgcode()+"' ");
//				if(isNotNull(finddto.getSbudgettype()))//Ԥ������
//					sql.append(" and S_BUDGETTYPE='"+finddto.getSbudgettype()+"' ");
//				if(isNotNull(finddto.getSbdglevel()))//Ԥ�㼶��
//					sql.append(" and S_BDGLEVEL='"+finddto.getSbdglevel()+"' ");
//				if(isNotNull(finddto.getStrimflag()))//�����ڱ�־
//					sql.append(" and S_TRIMFLAG='"+finddto.getStrimflag()+"' ");
//				if(isNotNull(finddto.getSbdgsbtcode()))//Ԥ���Ŀ����
//					sql.append(" and S_BDGSBTCODE='"+finddto.getSbdgsbtcode()+"' ");
//				if(isNotNull(finddto.getStrano()))//������ˮ��
//					sql.append(" and S_TRANO='"+finddto.getStrano()+"' ");
//				if(isNotNull(finddto.getStaxpaycode()))//��˰�˱��
//					sql.append(" and S_TAXPAYCODE='"+finddto.getStaxpaycode()+"' ");
//				if(isNotNull(finddto.getSpackno()))//��˰������
//					sql.append(" and S_TAXPAYNAME like '%"+finddto.getSpackno()+"%' ");
//				if(isNotNull(finddto.getSpayacct()))//�������˺�
//					sql.append(" and S_PAYACCT='"+finddto.getSpayacct()+"' ");
				getMap.put("allcount",execDetail.runQuery(sql.toString()).getString(0, 0));//�����ˮ�����������
				getMap.put("allcountwhere", StringUtil.replace(sql.toString(), "select count(*) as count from TV_FIN_INCOME_DETAIL where ", ""));
				StringBuffer blendsql = new StringBuffer("and S_SEQ in(select S_SEQ from TV_INCOMEONLINE_INCOMEONDETAIL_BLEND where S_INTREDATE='"+finddto.getSintredate()+"' and "+yh.toString()+") ");
				getMap.put("blendcount",execDetail.runQuery(sql.toString()+blendsql.toString()).getString(0, 0));//����˰Ʊ�����ˮ�����������
				getMap.put("blendcountwhere", StringUtil.replace(sql.toString()+blendsql.toString(), "select count(*) as count from TV_FIN_INCOME_DETAIL where ", ""));
				sql.append("and S_EXPVOUNO in(select S_TAXVOUNO from TV_FIN_INCOMEONLINE where S_TRASTATE='20' and "+yh.toString()+") ");
//				if(finddto.getSintredate()!=null&&!"".equals(finddto.getSintredate()))
//					sql.append(" where S_INTREDATE='"+finddto.getSintredate()+"'");
//				sql.append(")");
				getMap.put("equalscount",execDetail.runQuery(sql.toString()).getString(0, 0));//����˰Ʊ�����ˮ�����������
				getMap.put("equalswhere", StringUtil.replace(sql.toString(), "select count(*) as count from TV_FIN_INCOME_DETAIL where ", ""));
				String notequalssql = StringUtil.replace(sql.toString(), "in(", "not in(");
				getMap.put("notequalscount", execDetail.runQuery(notequalssql).getString(0, 0));//����˰Ʊ�����ˮ���Ҳ�������
				getMap.put("notequalswhere", StringUtil.replace(notequalssql, "select count(*) as count from TV_FIN_INCOME_DETAIL where ", ""));
			}
		}catch (Exception e2) {
			log.error(e2.getMessage(),e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		
		return getMap;
	}	

	private boolean isNotNull(String valiString)
	{
		if(valiString==null||"".equals(valiString)||"null".equals(valiString.toLowerCase()))
			return false;
		else
			return true;
	}

	public String blendStorage(TvIncomeonlineIncomeondetailBlendDto finddto,Map paramMap) throws ITFEBizException {
		SQLExecutor execDetail = null;
		try
		{
			if(finddto!=null)
			{
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				StringBuffer insertsql = new StringBuffer("insert into TV_INCOMEONLINE_INCOMEONDETAIL_BLEND(");
				insertsql.append("S_SEQ,S_FINORGCODE,S_APPLYDATE,S_INTREDATE,S_BLEND,S_PACKNO,S_TRECODE,S_TRENAME,S_TAXORGCODE,S_PAYBNKNO,S_TRANO,S_ORIMSGNO,F_TRAAMT,");
				insertsql.append("F_AMT,S_PAYEROPNBNKNO,S_PAYEROPBKNAME,S_HANDORGNAME,S_PAYACCT,S_TAXVOUNO,S_BILLDATE,S_TAXPAYCODE,S_TAXPAYNAME,S_BUDGETTYPE,S_TRIMFLAG,");
				insertsql.append("S_ETPCODE,S_ETPNAME,S_ETPTYPE,S_BDGSBTCODE,S_BDGSBTNAME,S_LIMIT,S_TAXTYPECODE,S_TAXKINDNAME,S_BDGLEVEL,S_BDGLEVELNAME,S_TAXSTARTDATE,");
				insertsql.append("S_TAXENDDATE,S_ASTFLAG,S_TAXTYPE,S_ACCT,S_TRASTATE,S_INPUTERID,TS_SYSUPDATE,S_VOUCHANNEL,S_STATUS,S_REMARK,S_REMARK1,S_REMARK2,S_REMARK3");
				if(isNotNull(String.valueOf(paramMap.get("remark"))))
					insertsql.append(",S_REMARK4)");
				else
					insertsql.append(")");
				insertsql.append(" (select ");//year(CURRENT date)||month(CURRENT date)||day(CURRENT date)
				insertsql.append(" b.S_SEQ,a.S_ORGCODE,a.S_APPLYDATE,b.S_INTREDATE,b.S_EXPVOUTYPE,a.S_PACKNO,a.S_TRECODE,a.S_TRENAME,a.S_TAXORGCODE,a.S_PAYBNKNO,a.S_TRANO,a.S_ORIMSGNO,a.F_TRAAMT,");
				insertsql.append("b.F_AMT,a.S_PAYEROPNBNKNO,a.PAYEROPBKNAME,a.S_HANDORGNAME,a.S_PAYACCT,a.S_TAXVOUNO,a.S_BILLDATE,a.S_TAXPAYCODE,a.S_TAXPAYNAME,a.c_BUDGETTYPE,a.c_TRIMFLAG,");
				insertsql.append("a.S_ETPCODE,a.S_ETPNAME,a.S_ETPTYPE,a.S_BDGSBTCODE,a.S_BDGSBTNAME,a.S_LIMIT,a.S_TAXTYPECODE,a.S_TAXKINDNAME,a.c_BDGLEVEL,a.c_BDGLEVELNAME,a.S_TAXSTARTDATE,");
				insertsql.append("a.S_TAXENDDATE,a.S_ASTFLAG,a.c_TAXTYPE,a.S_ACCT,a.S_TRASTATE,a.S_INPUTERID,CURRENT TIMESTAMP,b.C_VOUCHANNEL,a.S_TRASTATE,a.S_REMARK,a.S_REMARK1,a.S_REMARK2,'�������'");
				if(isNotNull(String.valueOf(paramMap.get("remark"))))
					insertsql.append(",'"+String.valueOf(paramMap.get("remark"))+"'");
				insertsql.append(" from TV_FIN_INCOMEONLINE a,TV_FIN_INCOME_DETAIL b where a.S_TAXVOUNO=b.S_EXPVOUNO and a.S_ORGCODE=b.S_ORGCODE and a.S_TAXORGCODE=b.S_TAXORGCODE and a.S_BDGSBTCODE=b.S_BDGSBTCODE ");//and a.S_TRASTATE='20'
				insertsql.append(" and b.S_INTREDATE='"+finddto.getSintredate()+"' and a.S_TRASTATE='20' and a.S_TRECODE=b.S_TRECODE ");
				StringBuffer wheresql = new StringBuffer("");
				if(isNotNull(finddto.getStrecode()))//�������
					wheresql.append(" and a.S_TRECODE='"+finddto.getStrecode()+"' ");
				else
				{
					List<TsTreasuryDto> tList=(List)execDetail.runQuery("select * from TS_TREASURY where S_ORGCODE='"+this.getLoginInfo().getSorgcode()+"'",TsTreasuryDto.class).getDtoCollection();
					if(tList!=null&&tList.size()>0)
					{
						
						TsTreasuryDto tDto = null;
						for(int i=0;i<tList.size();i++)
						{
							tDto = (TsTreasuryDto)tList.get(i);
							if(i==0)
							{
								wheresql.append(" and (a.S_TRECODE='"+tDto.getStrecode()+"' ");
							}else
							{
								wheresql.append(" or a.S_TRECODE='"+tDto.getStrecode()+"' ");
							}
						}
						wheresql.append(")");
					}
				}
				StringBuffer blendsql = new StringBuffer(" and b.S_SEQ not in(select S_SEQ from TV_INCOMEONLINE_INCOMEONDETAIL_BLEND where S_INTREDATE='"+finddto.getSintredate()+"' "+wheresql.toString().replace("a.", "")+") ");
				execDetail.runQuery(insertsql.toString()+wheresql.toString()+blendsql+")");
				insertsql = new StringBuffer("insert into TV_INCOMEONLINE_INCOMEONDETAIL_BLEND(");
				insertsql.append("S_SEQ,S_FINORGCODE,S_APPLYDATE,S_INTREDATE,S_BLEND,S_PACKNO,S_TRECODE,S_TRENAME,S_TAXORGCODE,S_PAYBNKNO,S_TRANO,S_ORIMSGNO,F_TRAAMT,");
				insertsql.append("F_AMT,S_PAYEROPNBNKNO,S_PAYEROPBKNAME,S_HANDORGNAME,S_PAYACCT,S_TAXVOUNO,S_BILLDATE,S_TAXPAYCODE,S_TAXPAYNAME,S_BUDGETTYPE,S_TRIMFLAG,");
				insertsql.append("S_ETPCODE,S_ETPNAME,S_ETPTYPE,S_BDGSBTCODE,S_BDGSBTNAME,S_LIMIT,S_TAXTYPECODE,S_TAXKINDNAME,S_BDGLEVEL,S_BDGLEVELNAME,S_TAXSTARTDATE,");
				insertsql.append("S_TAXENDDATE,S_ASTFLAG,S_TAXTYPE,S_ACCT,S_TRASTATE,S_INPUTERID,TS_SYSUPDATE,S_VOUCHANNEL,S_STATUS,S_REMARK,S_REMARK1,S_REMARK2,S_REMARK3");
				if(isNotNull(String.valueOf(paramMap.get("remark"))))
					insertsql.append(",S_REMARK4)");
				else
					insertsql.append(")");
				insertsql.append(" (select ");//year(CURRENT date)||month(CURRENT date)||day(CURRENT date)
				insertsql.append(" b.S_SEQ,b.S_ORGCODE,'',b.S_INTREDATE,b.S_EXPVOUTYPE,b.I_PKGSEQNO,b.S_TRECODE,'',b.S_TAXORGCODE,'','','',0,");
				insertsql.append("b.F_AMT,'','','','',b.S_EXPVOUNO,'','','',b.C_BDGKIND,'',");
				insertsql.append("'','','',b.S_BDGSBTCODE,'','','','',b.c_BDGLEVEL,'','',");
				insertsql.append("'','','',year(CURRENT date)||month(CURRENT date)||day(CURRENT date),'','',CURRENT TIMESTAMP,b.C_VOUCHANNEL,'','','','','�ֹ�Ʊ���'");
				if(isNotNull(String.valueOf(paramMap.get("remark"))))
					insertsql.append(",'"+String.valueOf(paramMap.get("remark"))+"'");
				wheresql = new StringBuffer("");
				List<TsTreasuryDto> tList = null;
				if(isNotNull(finddto.getStrecode()))//�������
					wheresql.append(" S_TRECODE='"+finddto.getStrecode()+"' ");
				else
				{
					tList=(List)execDetail.runQuery("select * from TS_TREASURY where S_ORGCODE='"+this.getLoginInfo().getSorgcode()+"'",TsTreasuryDto.class).getDtoCollection();
					if(tList!=null&&tList.size()>0)
					{
						
						TsTreasuryDto tDto = null;
						for(int i=0;i<tList.size();i++)
						{
							tDto = (TsTreasuryDto)tList.get(i);
							if(i==0)
							{
								wheresql.append(" (S_TRECODE='"+tDto.getStrecode()+"' ");
							}else
							{
								wheresql.append(" or S_TRECODE='"+tDto.getStrecode()+"' ");
							}
						}
						wheresql.append(")");
					}
				}
				insertsql.append(" from TV_FIN_INCOME_DETAIL b where b.S_INTREDATE='"+finddto.getSintredate()+"' and "+wheresql.toString());//and a.S_TRASTATE='20'
				blendsql = new StringBuffer(" and b.S_SEQ not in(select S_SEQ from TV_INCOMEONLINE_INCOMEONDETAIL_BLEND where S_INTREDATE='"+finddto.getSintredate()+"' and "+wheresql.toString().replace("a.", "")+") ");
				execDetail.runQuery(insertsql.toString()+blendsql+")");
				if(isNotNull(finddto.getStrecode()))
				{
					tList = new ArrayList<TsTreasuryDto>();
					TsTreasuryDto tDto = new TsTreasuryDto();
					tDto.setStrecode(finddto.getStrecode());
					tList.add(tDto);
				}
				saveDownloadReportCheck(finddto.getSintredate(),tList);
			}
		}catch (Exception e2) {
			log.error(e2.getMessage(),e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return null;
	}
	
	/**
	 * ���Ի�����ⱨ���ѯ
	 */
	public List findSummaryReport(IDto idto, String strecode) throws ITFEBizException {
		TvIncomeonlineIncomeondetailBlendDto reportDto = (TvIncomeonlineIncomeondetailBlendDto)idto;
		List list = new ArrayList();
		try {
			SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			CommonQto qto = SqlUtil.IDto2CommonQto(reportDto);
			String sql = "SELECT s_intredate squerydate,S_TRECODE sbdgsbtcode,count(*) icount,sum(F_AMT) famt FROM TV_INCOMEONLINE_INCOMEONDETAIL_BLEND "+(qto==null?"where 1=1 ":qto.getSWhereClause())+strecode+" GROUP BY S_TRECODE,s_intredate";
			if(qto!=null)
				sqlExe.addParam(qto.getLParams());
			SQLResults res = sqlExe.runQueryCloseCon(sql, SummaryReportDto.class);
			if(res!=null){
    			list.addAll(res.getDtoCollection());
    		}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			throw new ITFEBizException(e);
		}
		return list;
	}
	private void saveDownloadReportCheck(String date,List<TsTreasuryDto> tList)
	{
		if(date==null||tList==null||"".equals(date)||tList.size()<=0)
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		for(TsTreasuryDto tempdto:tList)
		{
			finddto.setSdates(date);
			finddto.setStrecode(tempdto.getStrecode());
			try {
				TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
				if(dto==null)
				{
					finddto.setSext1("1");
					DatabaseFacade.getODB().create(finddto);
				}else
				{
					if("0".equals(dto.getSext1())||null==dto.getSext1())
					{
						dto.setSext1("1");
						DatabaseFacade.getODB().update(dto);
					}
				}
			} catch (JAFDatabaseException e) {
				log.error("�������ر����������ʧ��:"+e.toString());
			}catch(Exception e)
			{
				log.error("�������ر����������ʧ��:"+e.toString());
			}
		}
	}
}