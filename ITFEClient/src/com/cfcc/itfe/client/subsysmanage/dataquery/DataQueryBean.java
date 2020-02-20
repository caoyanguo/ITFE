package com.cfcc.itfe.client.subsysmanage.dataquery;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.subsysmanage.dataquery.IDataQueryService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author admin
 * @time   17-02-20 09:29:52
 * 子系统: SubSysManage
 * 模块:DataQuery
 * 组件:DataQuery
 */
public class DataQueryBean extends AbstractDataQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DataQueryBean.class);
    private IDataQueryService iSrv = (IDataQueryService)getService(IDataQueryService.class); 
    public DataQueryBean() {
      super();
      resultTable = new ArrayList();
      pagingcontext = new PagingContext(null);
                  
    }
    /**
	 * 
	 * @param sql 查询sql
	 * @param effectNum 影响行数
	 * @param dbType 数据库
	 * @return
	 */
	public boolean search(String sql,String effectNum){
		if(sql == null || sql.trim().length() == 0){
			MessageDialog.openMessageDialog(null, "写个查询语句!");
			return false;
		}
		
		sql = sql.replaceAll("\"", "").trim();
		String tmpSql = sql.trim();//去掉toLowerCase()调用
		
		if(tmpSql.indexOf("drop ") == 0){
			MessageDialog.openMessageDialog(null, "不允许操作drop命令!");
			return false;
		}
		
		if(tmpSql.indexOf("insert ") == 0){
			//insert语句没有where语句
		}else if(tmpSql.indexOf("where")<0){
			MessageDialog.openMessageDialog(null, "执行语句必须有where子句！");
			return false;
		}
		
		if(	tmpSql.indexOf("alter ") == 0
			   || tmpSql.indexOf("create ") == 0
			   || tmpSql.indexOf("insert ") == 0
			   || tmpSql.indexOf("delete ") == 0
			   || tmpSql.indexOf("update ") == 0){
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "确认", "你的操作会影响数据库表中的数据,是否继续？");
			if(!flag)
				return false;
			
			//如果是update, delete 需要和effectNum匹配是否一致。
			if( tmpSql.indexOf("delete ") == 0 || tmpSql.indexOf("update ") == 0){
				
				if(!isDigital(effectNum)){
					MessageDialog.openMessageDialog(null, "影响行数必须是数值！");
					return false;
				}
				//测试语句的影响行数是否与参数一致。
				int elines = forcastEffectNum(tmpSql);
				if(elines != Integer.parseInt(effectNum)){
					MessageDialog.openMessageDialog(null, "填写影响行数与实际不符，请重新填写！");
					return false;
				}
			}
			
		}
		//调用存储过程单独处理
		else if (tmpSql.indexOf("call ") == 0){
			
			//取得参数列表
			String procParam = sql.substring(sql.indexOf("(")+1, sql.indexOf(")"));
			String[] params = procParam.split(",");
			List procParams = new ArrayList();
			
			for(int i =0 ; i< params.length; i++){
				String param = params[i].trim().replaceAll("'", "");
				procParams.add(param);
			}
			
			//取得存储过程名称
			String procName =sql.substring(tmpSql.indexOf("call")+4, tmpSql.indexOf("(")).trim();
			
			//存储过程中包括空格
			if(procName.contains(" ")){
				MessageDialog.openMessageDialog(null, "存储过程名称:"+ procName +"错误!");
				return false;
			}
			
			//调用存储过程返回值
			String result ="";
			
			try {
				//调用
				result = iSrv.callProc(procName, procParams);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, e.getMessage());
				return false;
			}
			
			//返回值判断
			if("1".equals(result)){
				MessageDialog.openMessageDialog(null, "存储过程的返回值为:'1'");
			} else {
				MessageDialog.openMessageDialog(null, "存储过程的返回值为:" + result);
			}
			return true;
		}
		
		if(sql.indexOf(" ur") == -1){
			if(sql.endsWith(";")){
				sql = sql.substring(0, sql.length() -1);
			}
			
			if(sql.indexOf("select")>=0){
				sql += " with ur";
			}
		}
		
		try {
			resultTable = iSrv.find(sql);
			if((resultTable == null || resultTable.size() <= 2)){
				if(sql.indexOf("select")>=0){
					MessageDialog.openMessageDialog(null, "查询结果为空!");
					return false;
				}
				else{
					MessageDialog.openMessageDialog(null, "执行成功!");
					return false;
				}
			}
			
			return true;
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return false;
		}
	}
	private int forcastEffectNum(String tmpSql) {
		StringBuilder excuteSql = new StringBuilder();
		int whereIndex = 0, fromIndex = 0, setIndex = 0;
		
		fromIndex = tmpSql.indexOf("from");

		excuteSql.append("select count(*) ");
		if(tmpSql.startsWith("delete")){
			excuteSql.append(tmpSql.substring(fromIndex));
		}else{
			whereIndex = tmpSql.indexOf("where");
			setIndex = tmpSql.indexOf("set");
			excuteSql.append(" from ");
			excuteSql.append(tmpSql.substring(6, setIndex));
			excuteSql.append(tmpSql.substring(whereIndex));
		}
		
		log.info(excuteSql);
		
		try {
			resultTable = iSrv.find(excuteSql.toString());
			
			List result = (List)resultTable.get(2);
			return (Integer)(result.get(0));
		}
		catch (ITFEBizException e) {
			log.error(e,e);
		}
		return -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public List getColNames(){
		if(resultTable == null && resultTable.size() <= 0) return null;
		
		List list = (List)resultTable.get(0);
		if(list == null || list.size() <= 0) return null;
		
		return list;
	}

	public int getRowCount(){
		if(resultTable == null && resultTable.size() <= 0) return 0;
		
		Integer integer = (Integer)resultTable.get(1);
		if(integer == null) return 0;
		
		return integer;
	}	
	
	/**
	 * 
	 * @return
	 */
	public List getRows(){
		if(resultTable == null && resultTable.size() <= 0) return null;
		
		List list = (List)resultTable.get(2);
		if(list == null || list.size() <= 0) return null;
		
		return list;
	}
	
	/**
	 * Direction: 执行
	 * ename: execText
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String execText(Object o){
          return super.execText(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
    public Boolean isDigital(String str){
		StringBuffer reg = new StringBuffer("^([0-9]{1,").append(str).append("}+)");
		Pattern p = Pattern.compile(reg.toString());
		Matcher m = p.matcher(str);
		return m.matches();
    }

}