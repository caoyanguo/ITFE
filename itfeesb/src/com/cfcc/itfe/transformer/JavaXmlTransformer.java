package com.cfcc.itfe.transformer;

import java.io.StringWriter;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

import freemarker.template.Template;

/**
 * 将Csv、Xml文件与Java对象转换处理
 * 
 * @author db2admin
 * 
 */
public class JavaXmlTransformer extends AbstractMessageAwareTransformer {
	
	private static Log logger = LogFactory.getLog(JavaXmlTransformer.class);
	
	final static String defencoding = "GB18030";
//	HashMap<String, Smooks> map = new HashMap<String, Smooks>();
	// 报文编号
	private String msgNoKey;
	// 转换方向
	private String tradDirection;
	//
	private String domain;

	private String encoding;
	// 解析的标签头
	private String beanid;

	public String getBeanid() {
		return beanid;
	}

	public void setBeanid(String beanid) {
		this.beanid = beanid;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void initialise() throws InitialisationException {
		super.initialise();
		if (msgNoKey == null || "".equals(msgNoKey)) {
			throw new InitialisationException(YakMessages
					.fileNameGeneratorNotSet(), this);
		}
		if (tradDirection == null || "".equals(tradDirection)) {
			throw new InitialisationException(YakMessages
					.fileNameGeneratorNotSet(), this);
		}
		if (domain == null || "".equals(domain)) {
			throw new InitialisationException(YakMessages
					.fileNameGeneratorNotSet(), this);
		}

		
	}

	@Override
	public Object transform(MuleMessage message, String outputEncoding)
			throws TransformerException {

		Smooks smooks = null;
		try {
			String returnType = tradDirection.substring(tradDirection
					.indexOf("2") + 1);
			if (returnType.equals("java")) {
				String msgno = (String) message.getProperty(msgNoKey);
				String fromtips = (String) message.getProperty(MessagePropertyKeys.MSG_SENDER);
				if (StateConstant.MSG_SENDER_FLAG_1.equals(fromtips) && ITFECommonConstant.timerBizMsgNoList.containsKey(msgno)) {
				    return message;
				} 
				smooks = ConfigLoader.initXml2JavaConfig().get(msgno);
				
				if(null == smooks){
					logger.debug("没有找到要转化报文[" + msgno + "]对应的模版xml2java!");
					return null;
				}

				ExecutionContext executionContext = smooks.createExecutionContext();
				JavaResult result = new JavaResult();
				Object payload = message.getPayload();
				if (payload instanceof String) {
					smooks.filterSource(executionContext, new StringSource(
							(String) payload), result);
				} else if (payload instanceof byte[]) {
					String res = null;
					if (encoding == null) {
						res = new String((byte[]) payload, defencoding);
					} else {
						res = new String((byte[]) payload, encoding);
					}

					smooks.filterSource(executionContext,new StringSource(res), result);
				}

				return result.getBean(beanid);
			} else {
				StringWriter writer = new StringWriter();
//				Map root = new HashMap();
				HashMap cfx = (HashMap) message.getPayload();
//				root.put("cfx", cfx);
				Template template=ConfigLoader.initJava2XmlConfig().get((String)message.getProperty(msgNoKey));
				
				if(null == template){
					logger.debug("没有找到要转化报文[" + (String)message.getProperty(msgNoKey) + "]对应的模版java2xml!");
					return null;
				}
				
				template.process(cfx, writer);
				return writer.toString();
			}
		} catch (Exception e) {
			logger.error(e, e);
			throw new YakTransformerException(YakMessages.msgTransformError(e
					.getMessage()), e);
		} finally {
			if (smooks != null) {
				smooks.close();
			}
		}
	}

	public String getMsgNoKey() {
		return msgNoKey;
	}

	public void setMsgNoKey(String msgNoKey) {
		this.msgNoKey = msgNoKey;
	}

	public String getTradDirection() {
		return tradDirection;
	}

	public void setTradDirection(String tradDirection) {
		this.tradDirection = tradDirection;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
