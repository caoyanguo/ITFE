package com.cfcc.itfe.transformer;

import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class getMsgInfoWithMsg extends AbstractMessageAwareTransformer implements Initialisable{

	@Override
	public Object transform(MuleMessage mulemessage, String s)
			throws TransformerException {
		return mulemessage.getPayload();
	}

}
