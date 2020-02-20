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
 * 将Csv、Xml文件与Java对象转换处理
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
				Exception e=new Exception("没有找到要转化报文[" + (String)message.getProperty(msgNoKey) + "]对应的模版java2xml!");
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
			String msg = new String("写文件失败,IO错误." + fileName);
			logger.error(msg, e);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
			logger.error(msg, e);
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					logger.error("关闭文件出错！", ex);
					throw new FileOperateException("关闭文件出错！", ex);
				}
			}
		}		
	}

}
