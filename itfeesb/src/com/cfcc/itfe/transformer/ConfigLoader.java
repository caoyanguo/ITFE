package com.cfcc.itfe.transformer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import com.cfcc.itfe.config.ITFECommonConstant;
import org.milyn.Smooks;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ConfigLoader {
	
	public static Log logger = LogFactory.getLog(ConfigLoader.class);

	public static Integer lock = new Integer(0);

	private static volatile HashMap<String, Smooks> configMap = null;
	
	public static HashMap<String, Smooks> initXml2JavaConfig() {
		if(configMap == null || (configMap != null && configMap.size() == 0)){
			synchronized(lock){
				if(configMap == null || (configMap != null && configMap.size() == 0)){
					try {
						configMap = new HashMap<String, Smooks>();
						HashMap<String, String> xmltojavaMsgConf = ITFECommonConstant.getXml2javaMsgConfig();
						Iterator<String> keys = xmltojavaMsgConf.keySet().iterator();
						while (keys.hasNext()) {
							String key = keys.next();
							configMap.put(key, new Smooks(xmltojavaMsgConf.get(key)));
						}
					} catch (IOException e) {
						logger.error("初始化配置文件失败", e);
					} catch (SAXException e) {
						logger.error("初始化配置文件失败", e);
					}
				}
			}
		}

		return configMap;
	}
	
//	public static HashMap<String, Smooks> initXml2JavaConfig() {
//		HashMap<String, Smooks> configMap = new HashMap<String, Smooks>();
//
//		HashMap<String, String> xmltojavaMsgConf = ITFECommonConstant.getXml2javaMsgConfig();
//		try {
//			Iterator<String> keys = xmltojavaMsgConf.keySet().iterator();
//			while (keys.hasNext()) {
//				String key = keys.next();
//				// logger.error("key, new Smooks(xmltojavaMsgConf.get(key))" +
//				// key + "," + xmltojavaMsgConf.get(key));
//				configMap.put(key, new Smooks(xmltojavaMsgConf.get(key)));
//			}
//		} catch (IOException e) {
//
//			logger.error("初始化配置文件失败", e);
//		} catch (SAXException e) {
//			logger.error("初始化配置文件失败", e);
//		}
//		return configMap;
//	}

	public static HashMap<String, Template> initJava2XmlConfig() {
		ClassTemplateLoader loader = new ClassTemplateLoader(ConfigLoader.class, "/transconfig/");
		Configuration cfg = new Configuration();
		cfg.setNumberFormat("0.##");
		cfg.setTemplateLoader(loader);
		HashMap<String, String> javatoxmlMsgConf = ITFECommonConstant.getJava2xmlMsgConfig();

		HashMap<String, Template> configMap = new HashMap<String, Template>();
		Iterator<String> keys = javatoxmlMsgConf.keySet().iterator();
		try {
			while (keys.hasNext()) {
				String key = keys.next();
				Template template = cfg.getTemplate(javatoxmlMsgConf.get(key));
				configMap.put(key, template);
			}

		} catch (Exception e) {
			logger.error("初始化配置文件失败", e);
		}
		return configMap;
	}
}
