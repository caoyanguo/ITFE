package com.cfcc.itfe.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * ��ȡ���ʱ��
 * @author t60
 *
 */
public class PublishTime {
	private static Log log = LogFactory.getLog(PublishTime.class);
	public String getTime(){
		log.info("�û�����Ŀ¼��"+new File(System.getProperty("user.dir")).getAbsoluteFile());
		long lt=new File(System.getProperty("user.dir")+"/plugins/itfe_1.0.0.jar").lastModified();
		return new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss").format(new Date(lt));
		
	}
}
