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
 * 组装退库报文，给财政发送通知
 * 
 */
public class TimerReadAndImportDwbkComponent implements Callable {

	private static Log logger = LogFactory
			.getLog(TimerReadAndImportDwbkComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception  {
		/**
		 * 计划定时任务每隔一小时调用一次 读取磁盘上传过来的数据文件，判断是否已经传输完毕，传输完毕的，执行加载
		 * 路径配置：localpathstring=
		 * 定时任务：${exportreporttodir}
		 * 对fileUtil 进行了修改，升级时注意一并升级
		 * @throws Exception
		 */
		// 退库文件路径
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
			
			// 脚本执行结果无误
//			if ("导入成功".equals(result)) {
                //备份文件
				File fromDir = new File(filepath);
				String bakpath = ITFECommonConstant.FILE_TRANSFER_CONFIG_ROOT+ "Backup"+ File.separator+TimeFacade.getCurrentStringTime();
				File toDir = new File(bakpath);
				if (!toDir.exists()) {
					toDir.mkdirs();
				}
				//备份文件路径 //删除文件夹下的文件
				for (int i = 0; i <listFile.size(); i++) {
					String file = listFile.get(i);
					File f = new File(file);
					String toDirFile = bakpath+File.separator +f.getName();
					FileUtil.copyFile(file, toDirFile);
					logger.debug("源文件："+file);
					logger.debug("目标文件："+toDirFile);
					FileUtil.getInstance().deleteFile(file);
				}

			} catch (FileOperateException e) {
				logger.error("文件操作异常！",e);
			} catch (FileNotFoundException e) {
				logger.error("文件没有找到！",e);
			} catch (Exception e) {
				logger.error("出现异常！",e);			}
		}

		return eventContext;
	}
	
	/**
	 * 获取路径下所有文件的绝对文件名
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
		// 根据绝对路径进行删除操作
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
