package com.cfcc.itfe.transformer;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.yak.transformer.FileNameGenerator;

public class MsgFileNameGenerator implements FileNameGenerator {

	public String generateFileName(MuleMessage p_message, String p_rootDir) {
       DateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		StringBuilder sb=new StringBuilder();
       sb.append(p_rootDir);
       sb.append(File.separator);
       sb.append(sdf.format(new Date()));
       sb.append(File.separator);
       sb.append(p_message.getStringProperty(MessagePropertyKeys.MSG_NO_KEY, "NA"));
       sb.append("_");
       sb.append(UUID.randomUUID().toString().replace("-",""));
       sb.append(".msg");
		return sb.toString();
	}

}
