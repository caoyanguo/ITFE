/*
 * 创建日期 2006-1-16
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
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
 * 自定义的FileAppender,在应用服务器中集群环境下会为每个实例写一个文件,
 * 文件名为WAS_CELL_WAS_NODE_WAS_SERVER_FileName,其中was.serverstart.cell,
 * was.serverstart.node,was.serverstart.server可以在环境变量中指定,
 * FileName在配置文件中指定.需要修改java进程启动参数,
 * 后一行插入以下内容"-Dwas.serverstart.cell=%WAS_CELL%" "-Dwas.serverstart.node=%WAS_NODE%" "-Dwas.serverstart.server=%1"
 * windows需要设置,AIX和HPUx不需要设置 
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
