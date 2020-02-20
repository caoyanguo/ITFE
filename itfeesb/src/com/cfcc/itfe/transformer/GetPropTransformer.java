package com.cfcc.itfe.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

public class GetPropTransformer extends AbstractMessageAwareTransformer {
	
	private static Log logger = LogFactory.getLog(GetPropTransformer.class);
	
	private String prop;
	
	private String label;
	
	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {
		// 由于时间关系,暂时只增加特殊情况处理
		String xmlcontent = (String) message.getPayload();
		String lowcontent = xmlcontent.toLowerCase();
		String lowlabel = label.toLowerCase();
		
		String label1 = "<" + lowlabel +">";
		String label2 = "</" + lowlabel +">";
		
		if(lowcontent.indexOf(label1) < 0 || lowcontent.indexOf(label2) < 0){
			logger.error("找不到匹配[" + label + "]的标签!");
			throw new YakTransformerException(YakMessages.msgTransformError("找不到匹配[" + label + "]的标签!"));
		}
		
		int start1 = lowcontent.indexOf(label1);
		int start2 = lowcontent.indexOf(label2);
		
		if(start1 >= start2){
			logger.error("格式不匹配 start1 >= start2!");
			throw new YakTransformerException(YakMessages.msgTransformError("格式不匹配 start1 >= start2!"));
		}
		
		message.setProperty(prop, xmlcontent.substring(start1 + label1.length() , start2));
		logger.debug("key,value=" + prop + "," + xmlcontent.substring(start1 + label1.length() , start2));
		
		return message.getPayload();
	}


	public String getProp() {
		return prop;
	}


	public void setProp(String prop) {
		this.prop = prop;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}

}
