package com.cfcc.itfe.transformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.jaf.common.statistics.ServiceMethodStatistics;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

import freemarker.template.Template;

/**
 * ��Csv��Xml�ļ���Java����ת������
 * 
 * @author hjr
 * 
 */
public class VoucherXmlTransformer extends AbstractMessageAwareTransformer implements Callable{
	
	private static Log logger = LogFactory.getLog(VoucherXmlTransformer.class);
	

	@Override
	public Object transform(MuleMessage message, String outputEncoding)
			throws TransformerException {
		try{
			StringWriter writer = new StringWriter();
			String msgNoKey=MessagePropertyKeys.MSG_NO_KEY;
			Map map =  (Map) message.getProperty("xml");
			Template template=ConfigLoader.initJava2XmlConfig().get((String)message.getProperty(msgNoKey));
			
			if(null == template){				
				Exception e=new Exception("û���ҵ�Ҫת������[" + (String)message.getProperty(msgNoKey) + "]��Ӧ��ģ��java2xml!");
				logger.error(e);
				throw new YakTransformerException(YakMessages.msgTransformError(e
						.getMessage()), e);	
			}
			template.process(map, writer);
			logger.debug(writer.toString());
			writeFile((String)message.getProperty("fileName"), writer.toString());			
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new YakTransformerException(YakMessages.msgTransformError(e
					.getMessage()), e);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new YakTransformerException(YakMessages.msgTransformError(e
					.getMessage()), e);					
		}

		return null;
	}


	public Object onCall(MuleEventContext eventContext)throws Exception {
		transform(eventContext.getMessage(),"GBK");
		return null;
	}


	public void writeFile(String fileName, String fileContent)
			throws FileOperateException {
			
		File file = new File(fileName);
		File dir = new File(file.getParent());

		FileOutputStream output = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(fileName, false);
			output.write(fileContent.getBytes("GBK"));

		} catch (IOException e) {
			String msg = new String("д�ļ�ʧ��,IO����." + fileName);
			logger.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
			logger.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					logger.error("�ر��ļ�����", ex);
					throw new FileOperateException("�ر��ļ�����", ex);
				}
			}
		}		
	}

}
