package com.cfcc.itfe.service.para.paramtransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.ParamConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.param.IParamInOut;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;

/**
 * @author db2admin
 * @time 13-01-30 09:50:49 codecomment:
 */

public class ParamTransformService extends AbstractParamTransformService {
	private static Log log = LogFactory.getLog(ParamTransformService.class);

	public List export(List tabs, String separator,String orgcode) throws ITFEBizException {
		List<String> paths = new ArrayList<String>();
		for(int i=0; i<tabs.size(); i++){
			TdEnumvalueDto dto = (TdEnumvalueDto)tabs.get(i);
			String tabcode =  dto.getSvalue();
			if("TD_TAXORG_PARAM".equals(tabcode)) { // �������ջ��������Ѿ���'���ջ��ش����'�ĳ�'���ջ���������Ӧ��ϵ��'(��Ҫ�����ֶ�) - 20160907
				tabcode = "TS_TAXORG";
			}
			
			String tabname = dto.getSvaluecmt();
			Map map = new HashMap();
			map.put(ParamConstant.TAB_CODE, tabcode);
			map.put(ParamConstant.TAB_NAME, tabname);
			map.put(ParamConstant.SEPARATOR,separator);
			map.put(ParamConstant.ORGCODE,orgcode);
			IParamInOut paramSv = getIParamInOutImpl(tabcode);
			try {
				String path =  paramSv.export(map);
				paths.add(path);
			} catch (Exception e) {
				log.error("ҵ�����쳣:"+e.getCause().getMessage(), e);
				throw new ITFEBizException("ҵ�����쳣:"+e.getCause().getMessage(), e);
			}
		}
		return paths;
	}

	private IParamInOut getIParamInOutImpl(String tabcode) throws ITFEBizException {
		tabcode = tabcode.replaceAll("_", "").toUpperCase();
        try {
			return (IParamInOut) Class.forName(ParamConstant.DEFAULTPARAMPREFIX+tabcode).newInstance();
		} catch (Exception e) {
			log.error("�޷���ʼ��ҵ������", e);
			throw new ITFEBizException("�޷���ʼ��ҵ������",e);
		} 
	}

}