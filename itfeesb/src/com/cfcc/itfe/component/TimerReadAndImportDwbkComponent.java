package com.cfcc.itfe.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;
import org.mule.transport.bpm.jbpm.actions.Continue;
import org.springframework.scheduling.timer.TimerFactoryBean;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.envoisolutions.sxc.builder.impl.JIfElseBlock.IfCondition;
import com.sun.org.apache.bcel.internal.generic.IFNULL;

/**
 * 
 * ��װ�˿ⱨ�ģ�����������֪ͨ
 * 
 */
public class TimerReadAndImportDwbkComponent implements Callable {

	private static Log logger = LogFactory
			.getLog(TimerReadAndImportDwbkComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception  {
		/**
		 * �ƻ���ʱ����ÿ��һСʱ����һ�� ��ȡ�����ϴ������������ļ����ж��Ƿ��Ѿ�������ϣ�������ϵģ�ִ�м���
		 * ·�����ã�localpathstring=
		 * ��ʱ����${exportreporttodir}
		 * ��fileUtil �������޸ģ�����ʱע��һ������
		 * @throws Exception
		 */
		// �˿��ļ�·��
	   String filepath = ITFECommonConstant.LOCALPATHSTRING +File.separator;
		List<String> listFile = listFileAbspath(filepath);
		if (listFile.size() > 0) {
			StringBuffer shellBuffer = new StringBuffer("");
			String dbalias = ITFECommonConstant.DB_ALIAS;
			String username = ITFECommonConstant.DB_USERNAME;
			String password = ITFECommonConstant.DB_PASSWORD;
			shellBuffer.append("connect to  ").append(dbalias).append(" user ")
					.append(username).append(" using ").append(password).append(
							";\n");

			StringBuffer sqlBuffer = new StringBuffer();
			for (int i = 0; i < listFile.size(); i++) {
				String file  = listFile.get(i);
				File f = new File(file);
				String filename = f.getName().toLowerCase();
				if (filename.endsWith(".ready")) {
					sqlBuffer
							.append("import from "
									+ filepath
									+ filename.replace(".ready", ".del")
									+ " of del commitcount 10000 insert into TIPS_TB_DRAWBACK ;\n");
				}
			}
			shellBuffer.append(sqlBuffer.toString());
			shellBuffer.append("connect reset;\n");
			logger.debug("*********************"+shellBuffer.toString()+"********************");
			try {
				FileUtil.getInstance().writeFile(
						ITFECommonConstant.FILE_ROOT_PATH
								+ "importTipsDwbkData.sql", shellBuffer.toString());
		
			String filename = DBOperUtil
					.callShell(ITFECommonConstant.FILE_ROOT_PATH
							+ "importTipsDwbkData.sql");
			//String result = DBOperUtil.callShellResultProcess(filename);
			
			// �ű�ִ�н������
//			if ("����ɹ�".equals(result)) {
                //�����ļ�
				File fromDir = new File(filepath);
				String bakpath = ITFECommonConstant.FILE_TRANSFER_CONFIG_ROOT+ "Backup"+ File.separator+TimeFacade.getCurrentStringTime();
				File toDir = new File(bakpath);
				if (!toDir.exists()) {
					toDir.mkdirs();
				}
				//�����ļ�·�� //ɾ���ļ����µ��ļ�
				for (int i = 0; i <listFile.size(); i++) {
					String file = listFile.get(i);
					File f = new File(file);
					String toDirFile = bakpath+File.separator +f.getName();
					FileUtil.copyFile(file, toDirFile);
					logger.debug("Դ�ļ���"+file);
					logger.debug("Ŀ���ļ���"+toDirFile);
					FileUtil.getInstance().deleteFile(file);
				}

			} catch (FileOperateException e) {
				logger.error("�ļ������쳣��",e);
			} catch (FileNotFoundException e) {
				logger.error("�ļ�û���ҵ���",e);
			} catch (Exception e) {
				logger.error("�����쳣��",e);			}
		}

		return eventContext;
	}
	
	/**
	 * ��ȡ·���������ļ��ľ����ļ���
	 * 
	 * @param filePath
	 * @return
	 */
	private List<String> listFileAbspath(String filePath) {
		List<String> listFile = new ArrayList<String>();
		listAbsPath(filePath, listFile);
		return listFile;
	}

	private void listAbsPath(String filePath, List filesStr) {
		File tmp1 = new File(filePath);
		// ���ݾ���·������ɾ������
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		if (file.isFile()) 
			filesStr.add(str);
		else {
			File[] f = file.listFiles();
			if (f == null || f.length == 0) 
				return;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
			Map<String,String> map;
			for (int i = 0; i < f.length; i++) {
				File tmp = f[i];
				map = new HashMap<String,String>();
				map.put("filepath", tmp.getAbsolutePath());
				map.put("edittime", df.format(tmp.lastModified()));
				
				if (tmp.isDirectory()) 
					listAbsPath(map.get("filepath"), filesStr);
				 else 
					filesStr.add(map);			
			}
		}
	}

}
