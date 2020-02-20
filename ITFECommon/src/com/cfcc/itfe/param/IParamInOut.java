package com.cfcc.itfe.param;

import java.util.Map;


public interface IParamInOut {

	
	public String export(Map<String,String> param) throws Exception ;
	
	public void importParam (String filename,Map param)throws Exception ;

}
