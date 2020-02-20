package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.persistence.dto.UserStampFuncDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 电子印鉴参数访问公共服务
 * @author sjz
 * @time   09-11-10 02:33:51
 * codecomment: 
 */

public class StampDataAccessService extends AbstractStampDataAccessService {
	private static Log log = LogFactory.getLog(StampDataAccessService.class);

	/**
	 * 根据文件种类获得业务凭证类型信息	 
	 * @generated
	 * @param fileType
	 * @return com.cfcc.itfe.persistence.dao.TsOperationtypeDao
	 * @throws ITFEBizException	 
	 */
	public TsOperationtypeDto getOperationTypeByFileType(String fileType) throws ITFEBizException {
		try{
			log.debug("根据文件类型" + fileType + "获得业务凭证种类");
			return StampFacade.getOperationTypeByFileType(fileType);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 根据业务凭证种类获得Model信息	 
	 * @generated
	 * @param modelId
	 * @return com.cfcc.itfe.persistence.dto.TsOperationmodelDto
	 * @throws ITFEBizException	 
	 */
	public TsOperationmodelDto getModelByOperationType(String opertionType) throws ITFEBizException {
		try{
			log.debug("根据业务凭证种类" + opertionType + "获得业务凭证模版信息");
			return StampFacade.getModelByOperationType(opertionType);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 根据业务凭证的模版Id获得业务凭证联信息	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List<IDto> getFormByModelId(String modelId) throws ITFEBizException {
		try{
			log.debug("根据业务凭证模版Id" + modelId + "查找业务凭证联信息");
			return StampFacade.getFormByModelId(modelId);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 根据业务凭证模版Id获得业务凭证中所有盖章和验章位置	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List<IDto> getPlaceByModelId(String modelId) throws ITFEBizException {
		try{
			log.debug("根据业务凭证模版Id" + modelId + "查找业务凭证盖章位置信息");
			return StampFacade.getPlaceByModelId(modelId);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 根据文件存放的相对路径，获取文件内容，并保存在字符串中返回
	 */
	public String getFileByPath(String path) throws ITFEBizException {
		try{
			log.debug("获取服务器文件:" + path);
			return StampFacade.getFileByPath(path);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}	

	/**
	 * 根据业务凭证模版信息获得业务凭证盖章联、盖章位置、模版内容等信息	 
	 * @generated
	 * @param model
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List<Object> getStampInfoByModel(IDto model) throws ITFEBizException {
		try{
			TsOperationmodelDto modelDto = (TsOperationmodelDto)model;
			List<Object> list = new ArrayList<Object>();
			//业务凭证联信息为集合的第一个元素
			list.add(StampFacade.getFormByModelId(modelDto.getSmodelid()));
			//业务凭证盖章位置信息为集合的第二个元素
			list.add(StampFacade.getPlaceByModelId(modelDto.getSmodelid()));
			//获得业务凭证模版的内容
			list.add(StampFacade.getFileByPathUTF8(modelDto.getSmodelsavepath()));
			return list;
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 获得用户指定业务凭证的盖章权限
	 */
	public Map<String,UserStampFuncDto> getUserStampFuction(String modelId) throws ITFEBizException {
		ITFELoginInfo user = getLoginInfo();
		try{
			Map<String,UserStampFuncDto> result = new HashMap<String, UserStampFuncDto>();
			log.debug("获取用户盖章权限，用户" + user.getSuserCode() + "，业务凭证" + modelId);
			String sql = "select a.s_orgcode,a.s_usercode,a.s_stamptypecode,a.s_stampid,a.s_isvalid,b.s_modelid,b.s_placeid "
				+ "from ts_userstamp a,ts_userstampfunction b "
				+ "where a.s_orgcode=b.s_orgcode and a.s_usercode=b.s_usercode and a.s_stamptypecode=b.s_stamptypecode "
				+ " and a.s_isvalid=? and a.s_orgcode=? and a.s_usercode=? and b.s_modelid=?";
			SQLExecutor executor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			executor.addParam("1");
			executor.addParam(user.getSorgcode());
			executor.addParam(user.getSuserCode());
			executor.addParam(modelId);
			List<UserStampFuncDto> list = (List<UserStampFuncDto>)executor.runQueryCloseCon(sql,UserStampFuncDto.class).getDtoCollection();
			if (list != null){
				for (UserStampFuncDto one : list){
					result.put(one.getSplaceid(), one);
				}
			}
			return result;
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}
	
}