package com.cfcc.itfe.service.subsysmanage.sysmoniter;

import java.io.File;
import java.text.NumberFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.util.DateUtil;

/**
 * @author db2admin
 * @time 13-07-03 16:28:30 codecomment:
 */

public class SysmonitorService extends AbstractSysmonitorService {
	private static Log log = LogFactory.getLog(SysmonitorService.class);

	/**
	 * ϵͳ���
	 * 
	 * @generated
	 * @param monitorarea
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String sysMonitor(String monitorarea) throws ITFEBizException {
    	String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + dirsep + "monitor";
		String results = null;
		//ϵͳ��ط�Χ 0--����ϵͳ��1--���ݿ⣬2--MQ��3--Ӧ�÷�������4--CA֤�飬5--Ӧ����־
		try {
			byte[] bytes = null;
	    	if(monitorarea.equals(StateConstant.MONITOR_SYSTEM)){
	    		if(!isWin()){
	    			String absoluteShell = absolutePath +dirsep+ "osmonitor.sh";
	    			File file=new File(absoluteShell);
	    			if(file.exists()){
	    				file.delete();
	    			}
	    			String importcontent = "#!/bin/sh\n"+"df -lh";
	    			FileUtil.getInstance().writeFile(absoluteShell, importcontent);
					bytes = CallShellUtil.callShellWithRes("sh "+absoluteShell);
					if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
						results = new String(bytes, 0,
								MsgConstant.MAX_CALLSHELL_RS * 1024);
					} else {
						results = new String(bytes);
					}
	    		}
	    	}else if(monitorarea.equals(StateConstant.MONITOR_DB)){
	    		String absoluteShell = absolutePath +dirsep+ "dbmonitor.sh";
	    		File file=new File(absoluteShell);
    			if(file.exists()){
    				file.delete();
    			}
				if(isWin()){
					String content ="connect to " + ITFECommonConstant.DB_ALIAS + " user "
					+ ITFECommonConstant.DB_USERNAME + " using "
					+ ITFECommonConstant.DB_PASSWORD + ";\n"
	    			+"list tablespaces show detail;\n"+"connect reset;";
					FileUtil.getInstance().writeFile(absoluteShell, content);
					bytes = CallShellUtil.callShellWithRes("db2cmd.exe -c -w -i db2 -tvf "+absoluteShell);
				}else {
					String importcontent ="#!/bin/sh\n"+"db2 connect to " + ITFECommonConstant.DB_ALIAS + " user "
					+ ITFECommonConstant.DB_USERNAME + " using "
					+ ITFECommonConstant.DB_PASSWORD + ";\n"
	    			+"db2 list tablespaces show detail;\n"+"db2 connect reset;";
	    			FileUtil.getInstance().writeFile(absoluteShell, importcontent);
					bytes = CallShellUtil.callShellWithRes("sh "+absoluteShell);
				}
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0,
							MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				results=infoTransfer(results,monitorarea);
	    	}else if(monitorarea.equals(StateConstant.MONITOR_MQ)){
	    		if(!isWin()){
		    		String absoluteShell = absolutePath +dirsep+ "mqmonitor.sh";
		    		File file=new File(absoluteShell);
	    			if(file.exists()){
	    				file.delete();
	    			}
	    			String importcontent ="#!/bin/sh\n"+"su - mqm -c \"/opt/mqm/bin/dspmq -m "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY CHSTATUS("+ ITFECommonConstant.SRC_NODE+"."+ ITFECommonConstant.PBCNODECODE+".B1)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY CHSTATUS("+ ITFECommonConstant.SRC_NODE+"."+ ITFECommonConstant.PBCNODECODE+".O1)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY CHSTATUS("+ ITFECommonConstant.PBCNODECODE+"."+ ITFECommonConstant.SRC_NODE+".B1)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY CHSTATUS("+ ITFECommonConstant.PBCNODECODE+"."+ ITFECommonConstant.SRC_NODE+".O1)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY QUEUE(PBC."+ ITFECommonConstant.PBCNODECODE+".BATCH.TRAN)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY QUEUE(PBC."+ ITFECommonConstant.PBCNODECODE+".ONLINE.TRAN)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY QUEUE(PBC."+ ITFECommonConstant.SRC_NODE+".BATCH.OUT)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY QUEUE(PBC."+ ITFECommonConstant.SRC_NODE+".ONLINE.OUT)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY QUEUE(PBC.EXT.BATCH.IN)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY QUEUE(PBC.EXT.ONLINE.IN)\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";\n"
	    			+"echo \"DISPLAY LSSTATUS(LSR."+ ITFECommonConstant.SRC_NODE+")\" | su - mqm -c \"/opt/mqm/bin/runmqsc "+ITFECommonConstant.MGRNAME+"\";";
	    			FileUtil.getInstance().writeFile(absoluteShell, importcontent);
					bytes = CallShellUtil.callShellWithRes("sh "+absoluteShell);
					if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
						results = new String(bytes, 0,
								MsgConstant.MAX_CALLSHELL_RS * 1024);
					} else {
						results = new String(bytes);
					}
					//��ִ�н�����з�������
					String [] line = results.split("\n");
					StringBuffer buf = new StringBuffer("");
					for (int i = 0; i < line.length; i++) {
						if (null==line[i] || line[i].trim().length() ==0) {
							continue;
						}
						if (line[i].startsWith("QMNAME(QM_TIPS_")) {
							int m = line[i].indexOf("STATUS");
							String ret = line[i].substring(m -1);
							buf.append("1�����й���������״̬��ʾ:\n");
							buf.append("   ���й�����:"+ITFECommonConstant.MGRNAME+"����״̬:"+ret +"\n");
				            buf.append("2��ͨ������״̬��ʾ��\n");
						}
						if (line[i].contains("1 : DISPLAY CHSTATUS")) {
							int m =line[i].indexOf("CHSTATUS");
							String chsname = line[i].substring(m-1);
							if (line[i+1].contains("AMQ8417")) {
								m = line[i+2].indexOf("CHLTYPE");
								String chstype = line[i+2].substring(m -1);
								m =line[i+4].indexOf("STATUS");
								String chsstatus = line[i+4].substring(m -1);
								buf.append("   ͨ������: "+chsname+" ͨ�����ͣ�  "+chstype+"   ͨ��״̬:"+chsstatus +"\n");	
								i+=10;
							}else if(line[i+1].contains("AMQ8420")){
								buf.append("   ͨ������:"+chsname+"  ͨ��״̬δ�ҵ���������ͨ�������\n");	
							}
							
						 }
						if (line[i].contains("1 : DISPLAY QUEUE")) {
							if (!buf.toString().contains("3����������״̬��ʾ��\n")) {
								buf.append("3����������״̬��ʾ��\n");
							}
							int m =line[i].indexOf("QUEUE");
							String quename = line[i].substring(m -1);
							m = line[i+2].indexOf("TYPE");
							String qutype = line[i+2].substring(m -1);
							if (qutype.equals("QLOCAL")) {
								m =line[i+9].indexOf("CURDEPTH");
								if (m>0) {
									String qudepth = line[i+9].substring(m -1);
									buf.append("   ��������:"+quename+" �������� "+qutype+"�������:"+qudepth +"\n");
								}else{
									buf.append("   ��������:"+quename+" ��������: "+qutype+"   �������: 0\n");
								}
									
							}else{
									buf.append("   ��������:"+quename+" ��������: "+qutype+"   �������: 0\n");
							}
						}
						if (line[i].contains("1 : DISPLAY LSSTATUS")) {
							buf.append("4������������״̬��ʾ��\n");
							int m =line[i].indexOf("LSSTATUS");
							String lsrname = line[i].substring(m -1);
							m = line[i+2].indexOf("STATUS");
							String state = line[i+2].substring(m -1);
							buf.append("   MQ������:  "+lsrname+"  ����״̬:   "+state+" \n"); 
						}
					}
					 results =buf.toString();
	    		}
					 
	    	}else if(monitorarea.equals(StateConstant.MONITOR_SERVER)){
	    		if(!isWin()){
	    			String absoluteShell = absolutePath +dirsep+ "wasmonitor.sh";
	    			File file=new File(absoluteShell);
	    			if(file.exists()){
	    				file.delete();
	    			}
	    			String importcontent = "#!/bin/sh\n"
	    				+"/opt/IBM/WebSphere/AppServer/profiles/AppSrv01/bin/serverStatus.sh -all -username wasadmin -password wasadmin";
	    			FileUtil.getInstance().writeFile(absoluteShell, importcontent);
					bytes = CallShellUtil.callShellWithRes("sh "+absoluteShell);
					if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
						results = new String(bytes, 0,
								MsgConstant.MAX_CALLSHELL_RS * 1024);
					} else {
						results = new String(bytes);
					}
	    		}
	    	}else if(monitorarea.equals(StateConstant.MONITOR_CA)){
	    		String validdate =ITFECommonConstant.CAVALIDDATE;
	    		results = "CA֤�鵽�����ڣ�"+validdate;
	    		if (DateUtil.difference(java.sql.Date.valueOf(validdate),DateUtil.currentDate())<30) {
	    			results+="\n"+"֤�鼴�����ڣ��뼰ʱ������";
				}
	    	}else if(monitorarea.equals(StateConstant.MONITOR_LOG)){
	    		results = "Ӧ����־·��:  /itfe/logs/Server.log* ";
	    	}
	    	log.debug("����SHELL���:" + results);
			} catch (Throwable e) {
				log.error("����SHELL:�鿴ϵͳ�����Ϣʱ�����쳣!", e);
				throw new ITFEBizException("����SHELL:�鿴ϵͳ�����Ϣʱ�����쳣!", e);
			}
		
			return results;
    }

	/**
	 * @author db2admin
	 * @param null
	 * @return boolean
	 */
	private static boolean isWin() {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public String infoTransfer(String info, String monitorarea) {
		String[] str = info.split("\n");
		StringBuffer buf = new StringBuffer();
		if (monitorarea.equals(StateConstant.MONITOR_SYSTEM)) {

		} else if (monitorarea.equals(StateConstant.MONITOR_DB)) {
			buf.append("��ռ��ʶ\t\t").append("��ռ�����\t\t").append("�ܴ�С\t\t")
					.append("���ô�С\t\t").append("���ô�С\t\t").append("����%\r\n");
			int m = 0;
			double x = 0, y = 0;
			for (int i = 0; i < str.length; i++) {
				if (str[i].contains("Tablespace ID")
						|| str[i].contains("��ռ��ʶ") || str[i].contains("Name")
						|| str[i].contains("����")) {
					buf.append(str[i].split("=")[1] + "\t\t");
				} else if (str[i].contains("Total pages")
						|| str[i].contains("�ܼ�ҳ��")) {
					m = i;
					if (str[i].split("=")[1].trim().getBytes().length == str[i]
							.split("=")[1].trim().length()) {
						x = Integer.parseInt(str[i].split("=")[1].trim()) * 16 / 1024;
						buf.append(String.valueOf(Integer.parseInt(str[i]
								.split("=")[1].trim()) * 16 / 1024)
								+ "M\t\t");
					} else {
						buf.append(String.valueOf("") + "\t\t");
					}
				}
				if ((m > 0) && (i == m + 2)) {
					if (str[i].split("=")[1].trim().getBytes().length == str[i]
							.split("=")[1].trim().length()) {
						y = Integer.parseInt(str[i].split("=")[1].trim()) * 16 / 1024;
						buf.append(String.valueOf(Integer.parseInt(str[i]
								.split("=")[1].trim()) * 16 / 1024)
								+ "M\t\t");
					} else {
						buf.append(String.valueOf("") + "\t\t");
					}
				} else if ((m > 0) && (i == m + 3)) {
					NumberFormat nf = NumberFormat.getPercentInstance();
					nf.setMaximumFractionDigits(1);
					if ((str[i].split("=")[1].trim().getBytes().length == str[i]
							.split("=")[1].trim().length())
							&& (x != 0)) {
						buf.append(
								String.valueOf((Integer.parseInt(str[i]
										.split("=")[1].trim()) * 16 / 1024))
										+ "M\t\t").append(
								String
										.valueOf(nf.format(Double
												.valueOf(y / x)))
										+ "\r\n");
					} else {
						buf.append(String.valueOf("") + "\t\t").append(
								String.valueOf("") + "\r\n");
					}
				}
				System.out.println(str[i]);
			}
		} else if (monitorarea.equals(StateConstant.MONITOR_MQ)) {

		} else if (monitorarea.equals(StateConstant.MONITOR_SERVER)) {

		} else if (monitorarea.equals(StateConstant.MONITOR_CA)) {

		} else if (monitorarea.equals(StateConstant.MONITOR_LOG)) {

		}
		return buf.toString();
	}
}