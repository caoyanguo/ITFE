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
 * ��ϵͳ: SubSysManage
 * ģ��:DataQuery
 * ���:DataQuery
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
	 * @param sql ��ѯsql
	 * @param effectNum Ӱ������
	 * @param dbType ���ݿ�
	 * @return
	 */
	public boolean search(String sql,String effectNum){
		if(sql == null || sql.trim().length() == 0){
			MessageDialog.openMessageDialog(null, "д����ѯ���!");
			return false;
		}
		
		sql = sql.replaceAll("\"", "").trim();
		String tmpSql = sql.trim();//ȥ��toLowerCase()����
		
		if(tmpSql.indexOf("drop ") == 0){
			MessageDialog.openMessageDialog(null, "���������drop����!");
			return false;
		}
		
		if(tmpSql.indexOf("insert ") == 0){
			//insert���û��where���
		}else if(tmpSql.indexOf("where")<0){
			MessageDialog.openMessageDialog(null, "ִ����������where�Ӿ䣡");
			return false;
		}
		
		if(	tmpSql.indexOf("alter ") == 0
			   || tmpSql.indexOf("create ") == 0
			   || tmpSql.indexOf("insert ") == 0
			   || tmpSql.indexOf("delete ") == 0
			   || tmpSql.indexOf("update ") == 0){
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "ȷ��", "��Ĳ�����Ӱ�����ݿ���е�����,�Ƿ������");
			if(!flag)
				return false;
			
			//�����update, delete ��Ҫ��effectNumƥ���Ƿ�һ�¡�
			if( tmpSql.indexOf("delete ") == 0 || tmpSql.indexOf("update ") == 0){
				
				if(!isDigital(effectNum)){
					MessageDialog.openMessageDialog(null, "Ӱ��������������ֵ��");
					return false;
				}
				//��������Ӱ�������Ƿ������һ�¡�
				int elines = forcastEffectNum(tmpSql);
				if(elines != Integer.parseInt(effectNum)){
					MessageDialog.openMessageDialog(null, "��дӰ��������ʵ�ʲ�������������д��");
					return false;
				}
			}
			
		}
		//���ô洢���̵�������
		else if (tmpSql.indexOf("call ") == 0){
			
			//ȡ�ò����б�
			String procParam = sql.substring(sql.indexOf("(")+1, sql.indexOf(")"));
			String[] params = procParam.split(",");
			List procParams = new ArrayList();
			
			for(int i =0 ; i< params.length; i++){
				String param = params[i].trim().replaceAll("'", "");
				procParams.add(param);
			}
			
			//ȡ�ô洢��������
			String procName =sql.substring(tmpSql.indexOf("call")+4, tmpSql.indexOf("(")).trim();
			
			//�洢�����а����ո�
			if(procName.contains(" ")){
				MessageDialog.openMessageDialog(null, "�洢��������:"+ procName +"����!");
				return false;
			}
			
			//���ô洢���̷���ֵ
			String result ="";
			
			try {
				//����
				result = iSrv.callProc(procName, procParams);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, e.getMessage());
				return false;
			}
			
			//����ֵ�ж�
			if("1".equals(result)){
				MessageDialog.openMessageDialog(null, "�洢���̵ķ���ֵΪ:'1'");
			} else {
				MessageDialog.openMessageDialog(null, "�洢���̵ķ���ֵΪ:" + result);
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
					MessageDialog.openMessageDialog(null, "��ѯ���Ϊ��!");
					return false;
				}
				else{
					MessageDialog.openMessageDialog(null, "ִ�гɹ�!");
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
	 * Direction: ִ��
	 * ename: execText
	 * ���÷���: 
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