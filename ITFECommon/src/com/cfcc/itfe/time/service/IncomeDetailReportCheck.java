package com.cfcc.itfe.time.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvIncomeDetailReportCheckDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * �����ˮ�뱨��˶�
 * @author �ν���
 * @time   14-02-27 15:49:44
 * codecomment: 
 */
public class IncomeDetailReportCheck {
	private static Log log = LogFactory.getLog(IncomeDetailReportCheck.class);	
	
	/**
	 * �˶������ˮ�ܽ���뱨���ܽ���Ƿ����	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
    public String incomeDetailReportCheck(TvIncomeDetailReportCheckDto dto) throws ITFEBizException {
    	try {
			SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();			
			SQLResults manageStrecodeRs = sqlExec.runQueryCloseCon(getSQL(dto, "1"),TvIncomeDetailReportCheckDto.class);
			sqlExec= DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();			
			return create(manageStrecodeRs, sqlExec.runQueryCloseCon(getSQL(dto, "2"),TvIncomeDetailReportCheckDto.class));
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
    }
    
    /**
     * �˶������ˮ�ܽ���뱨���ܽ���Ƿ����
     * @return
     * @throws ITFEBizException
     */
    public void incomeDetailReportCheck() throws ITFEBizException{
    	TvIncomeDetailReportCheckDto dto=new TvIncomeDetailReportCheckDto();
    	dto.setScreatdate(TimeFacade.getCurrentStringTimebefor());    	
    	incomeDetailReportCheck(dto);
    }
    
    /**
     * ������ѯ�������ˮ�뱨��˶�����д�����ݿ�
     * @param manageStrecodeRs �����������
     * @param proxyStrecodeRs  �����������
     * @return
     * @throws JAFDatabaseException
     */
    private String create(SQLResults manageStrecodeRs,SQLResults proxyStrecodeRs) throws JAFDatabaseException{
    	if(manageStrecodeRs.getDtoCollection().size()==0&&proxyStrecodeRs.getDtoCollection().size()==0)
    		return "";
    	List<TvIncomeDetailReportCheckDto> list=new ArrayList<TvIncomeDetailReportCheckDto>();		
    	list.addAll(manageStrecodeRs.getDtoCollection());
    	list.addAll(proxyStrecodeRs.getDtoCollection());    						
		TvIncomeDetailReportCheckDto[] dtos=new TvIncomeDetailReportCheckDto[list.size()];
		dtos=(TvIncomeDetailReportCheckDto[]) list.toArray(dtos);
		DatabaseFacade.getODB().delete(dtos);	
		DatabaseFacade.getODB().create(dtos);		
		return dealResult(list);
    }
    
    /**
     * ������
     * @param list
     * @return
     */
    private String dealResult(List<TvIncomeDetailReportCheckDto> list){
    	String result="";
    	for(TvIncomeDetailReportCheckDto dto:list){
    		if(dto.getSstatus().equals("0"))
    			result+=dto.getShold1()+" , ";
    	}return result.length()==0?"���ⱨ���������ˮ�˶�һ��":((result.trim().substring(0,result.lastIndexOf(","))+ "�����������ˮ�˶Բ�һ�£���鿴��"));
    }
    
    /**
     * �����ˮ�뱨��˶�SQL���
     * @param dto
     * @param S_TREATTRIB �������� 1 ������� 2 �������
     * @return
     */
    private String getSQL(TvIncomeDetailReportCheckDto dto ,String S_TREATTRIB){
    	String sql="select S_ORGCODE ,ta.S_TRECODE S_TRECODE,S_TRENAME S_HOLD1 , '"+dto.getScreatdate()+"' S_CREATDATE, F_AMT, N_MONEYDAY,";
    	sql+=" (CASE WHEN (N_MONEYDAY-F_AMT) = 0 THEN '1' ELSE '0' END ) S_STATUS ,";
    	sql+=" (CASE WHEN (N_MONEYDAY-F_AMT) = 0 THEN ''  WHEN N_MONEYDAY IS NULL THEN '�ޱ�������' WHEN F_AMT IS NULL THEN '�������ˮ����'";
    	sql+=" ELSE '�����������ˮ������ '||(N_MONEYDAY-F_AMT) END ) S_DEMO ,'"+S_TREATTRIB+"' S_TREATTRIB ";
    	sql+=" from(select S_TRECODE , S_ORGCODE ,S_TRENAME from TS_TREASURY WHERE S_TREATTRIB = '"+S_TREATTRIB+"' ";
    	sql+= dto.getSorgcode()!=null?"AND S_ORGCODE = '"+dto.getSorgcode() +"' ":"";
    	sql+= dto.getStrecode()!=null?"AND S_TRECODE = '"+dto.getStrecode() +"' ":"";
    	sql+=" group by S_TRECODE,S_ORGCODE, S_TRENAME ) ta "; 
    	sql+=" left join (select S_TRECODE,sum( ";
    	sql+= S_TREATTRIB.equals("1")?"F_AMT":"CASE WHEN S_EXPVOUTYPE  = '1' THEN (0-F_AMT) ELSE F_AMT END";
    	sql+=" ) F_AMT,S_INTREDATE from TV_FIN_INCOME_DETAIL WHERE S_INTREDATE='"+dto.getScreatdate()+"' ";
    	sql+=" AND (S_EXPVOUTYPE ='0' or S_EXPVOUTYPE ='1' or S_EXPVOUTYPE ='2') ";    	
    	sql+= dto.getStrecode()!=null?"AND S_TRECODE = '"+dto.getStrecode() +"' ":"";
    	sql+=" group by S_TRECODE ,S_INTREDATE)t1 on ta.S_TRECODE = t1.S_TRECODE ";
    	sql+=" left join (select S_TRECODE,sum(N_MONEYDAY) N_MONEYDAY ,S_RPTDATE from TR_INCOMEDAYRPT WHERE S_BILLKIND = '1' ";
    	sql+=" AND S_RPTDATE='"+dto.getScreatdate()+"'";
    	sql+= S_TREATTRIB.equals("1")?" AND S_BELONGFLAG = '0'":"";    
    	sql+= dto.getStrecode()!=null?"AND S_TRECODE = '"+dto.getStrecode() +"' ":"";
    	sql+=" group by S_TRECODE ,S_RPTDATE)t2 on ta.S_TRECODE = t2.S_TRECODE ";
    	sql+=" left join (select S_TRECODE,S_STATUS ,S_CREATDATE from TV_INCOME_DETAIL_REPORT_CHECK ";
    	sql+=" WHERE TV_INCOME_DETAIL_REPORT_CHECK.S_STATUS = '1' AND S_CREATDATE = '"+dto.getScreatdate()+"'";
    	sql+= dto.getSorgcode()!=null?"AND S_ORGCODE = '"+dto.getSorgcode() +"' ":"";
    	sql+= dto.getStrecode()!=null?"AND S_TRECODE = '"+dto.getStrecode() +"' ":"";
    	sql+=" group by S_TRECODE,S_STATUS,S_CREATDATE)t3 on ta.S_TRECODE = t3.S_TRECODE";
    	return sql+=" WHERE (S_INTREDATE IS NOT NULL or S_RPTDATE IS NOT NULL ) AND S_CREATDATE IS NULL";  	  	
    }    
}
