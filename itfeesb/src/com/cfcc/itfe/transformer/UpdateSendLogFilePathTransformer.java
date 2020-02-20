package com.cfcc.itfe.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

public class UpdateSendLogFilePathTransformer extends AbstractMessageAwareTransformer {

	private static Log logger = LogFactory.getLog(UpdateSendLogFilePathTransformer.class);

	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {
		TvSendlogDto tvsendlogdto =(TvSendlogDto) message.getProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO); 
		if(tvsendlogdto!=null){
			tvsendlogdto.setStitle((String)message.getProperty("XML_MSG_FILE_PATH"));
			try {
				DatabaseFacade.getDb().update(tvsendlogdto);
			} catch (JAFDatabaseException e) {
				logger.error("更新发送日志报文保存路径时出错", e);
				throw new YakTransformerException(YakMessages.msgTransformError(e.getMessage()), e);
			}
		}else{
			logger.warn(message+"没有记发送日志");
		}
		return message.getPayload();
	}
}
