package com.cfcc.itfe.transformer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.util.FileUtils;

import com.cfcc.yak.i18n.YakMessages;


/**
 * 报文中为文件路径，将路径加载到String中
 * @author newroc
 *
 */
public class FileToStringTransformer extends AbstractMessageAwareTransformer {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(FileToStringTransformer.class);
     
	@Override
	public Object transform(MuleMessage p_message, String p_outputEncoding)
			throws TransformerException {
	    String filePath=(String) p_message.getPayload();
	    String msg=null;
		try {
			msg = FileUtils.readFileToString(new File(filePath), p_outputEncoding);
		} catch (IOException e) {
			logger.error("读取文件异常", e);			
			throw new TransformerException(YakMessages.fileOperationError(filePath),e);
		}
	    DefaultMuleMessage muleMessage=new DefaultMuleMessage(msg, p_message);
	    if(logger.isDebugEnabled()){
	    	logger.debug("加载文件"+filePath+"成功。文件内容为:");
	    }
	    return muleMessage;
	}

}
