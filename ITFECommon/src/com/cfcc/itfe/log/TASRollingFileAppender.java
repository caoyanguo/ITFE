/*
 * �������� 2006-1-16
 *
 * TODO Ҫ���Ĵ����ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package com.cfcc.itfe.log;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.RollingFileAppender;

/**
 * @author zhxinpeng
 *
 * �Զ����FileAppender,��Ӧ�÷������м�Ⱥ�����»�Ϊÿ��ʵ��дһ���ļ�,
 * �ļ���ΪWAS_CELL_WAS_NODE_WAS_SERVER_FileName,����was.serverstart.cell,
 * was.serverstart.node,was.serverstart.server�����ڻ���������ָ��,
 * FileName�������ļ���ָ��.��Ҫ�޸�java������������,
 * ��һ�в�����������"-Dwas.serverstart.cell=%WAS_CELL%" "-Dwas.serverstart.node=%WAS_NODE%" "-Dwas.serverstart.server=%1"
 * windows��Ҫ����,AIX��HPUx����Ҫ���� 
 */
public class TASRollingFileAppender extends RollingFileAppender {
	
	
	  public void setFile(String file) {
	  	Properties props=System.getProperties();
		Set set=props.keySet();
		Iterator it= set.iterator();		
		while (it.hasNext()) {
			String key = (String) it.next();
			String value=props.getProperty(key);
		}
	  	String wasCell=System.getProperty("was.serverstart.cell");
	  	String wasNode=System.getProperty("was.serverstart.node");
	  	String wasServer=System.getProperty("was.serverstart.server");
	  	if(wasCell==null||wasCell.trim().length()==0){
	  		wasCell="";
	  	}else{
	  		wasCell=wasCell+"_";
	  	}
	  	if(wasNode==null||wasNode.trim().length()==0){
	  		wasNode="";
	  	}else{
	  		wasNode=wasNode+"_";
	  	}
	  	if(wasServer==null||wasServer.trim().length()==0){
	  		wasServer="";
	  	}else{
	  		wasServer=wasServer+"_";
	  	}
	    String val = file.trim();
	    int index= val.lastIndexOf(File.separator);
	    if(index==-1){
	    	index= val.lastIndexOf("/");
	    }
	    if(index!=-1){
	    	val= val.substring(0,index+1)+wasCell+wasNode+wasServer+val.substring(index+1);
	    }else{
	    	val= wasCell+wasNode+wasServer+val;
	    }
	    
	    fileName = val;
	  }
}
