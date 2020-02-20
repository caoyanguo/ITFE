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
 * 入库流水与报表核对
 * @author 何健荣
 * @time   14-02-27 15:49:44
 * codecomment: 
 */
public class IncomeDetailReportCheck {
	private static Log log = LogFactory.getLog(IncomeDetailReportCheck.class);	
	
	/**
	 * 核对入库流水总金额与报表总金额是否相等	 	 
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
     * 核对入库流水总金额与报表总金额是否相等
     * @return
     * @throws ITFEBizException
     */
    public void incomeDetailReportCheck() throws ITFEBizException{
    	TvIncomeDetailReportCheckDto dto=new TvIncomeDetailReportCheckDto();
    	dto.setScreatdate(TimeFacade.getCurrentStringTimebefor());    	
    	incomeDetailReportCheck(dto);
    }
    
    /**
     * 将所查询的入库流水与报表核对数据写入数据库
     * @param manageStrecodeRs 经理国库结果集
     * @param proxyStrecodeRs  代理国库结果集
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
     * 处理结果
     * @param list
     * @return
     */
    private String dealResult(List<TvIncomeDetailReportCheckDto> list){
    	String result="";
    	for(TvIncomeDetailReportCheckDto dto:list){
    		if(dto.getSstatus().equals("0"))
    			result+=dto.getShold1()+" , ";
    	}return result.length()==0?"国库报表与入库流水核对一致":((result.trim().substring(0,result.lastIndexOf(","))+ "报表与入库流水核对不一致，请查看！"));
    }
    
    /**
     * 入库流水与报表核对SQL语句
     * @param dto
     * @param S_TREATTRIB 国库属性 1 经理国库 2 代理国库
     * @return
     */
    private String getSQL(TvIncomeDetailReportCheckDto dto ,String S_TREATTRIB){
    	String sql="select S_ORGCODE ,ta.S_TRECODE S_TRECODE,S_TRENAME S_HOLD1 , '"+dto.getScreatdate()+"' S_CREATDATE, F_AMT, N_MONEYDAY,";
    	sql+=" (CASE WHEN (N_MONEYDAY-F_AMT) = 0 THEN '1' ELSE '0' END ) S_STATUS ,";
    	sql+=" (CASE WHEN (N_MONEYDAY-F_AMT) = 0 THEN ''  WHEN N_MONEYDAY IS NULL THEN '无报表数据' WHEN F_AMT IS NULL THEN '无入库流水数据'";
    	sql+=" ELSE '报表与入库流水金额相差 '||(N_MONEYDAY-F_AMT) END ) S_DEMO ,'"+S_TREATTRIB+"' S_TREATTRIB ";
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
