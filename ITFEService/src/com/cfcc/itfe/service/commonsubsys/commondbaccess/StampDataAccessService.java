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
 * ����ӡ���������ʹ�������
 * @author sjz
 * @time   09-11-10 02:33:51
 * codecomment: 
 */

public class StampDataAccessService extends AbstractStampDataAccessService {
	private static Log log = LogFactory.getLog(StampDataAccessService.class);

	/**
	 * �����ļ�������ҵ��ƾ֤������Ϣ	 
	 * @generated
	 * @param fileType
	 * @return com.cfcc.itfe.persistence.dao.TsOperationtypeDao
	 * @throws ITFEBizException	 
	 */
	public TsOperationtypeDto getOperationTypeByFileType(String fileType) throws ITFEBizException {
		try{
			log.debug("�����ļ�����" + fileType + "���ҵ��ƾ֤����");
			return StampFacade.getOperationTypeByFileType(fileType);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * ����ҵ��ƾ֤������Model��Ϣ	 
	 * @generated
	 * @param modelId
	 * @return com.cfcc.itfe.persistence.dto.TsOperationmodelDto
	 * @throws ITFEBizException	 
	 */
	public TsOperationmodelDto getModelByOperationType(String opertionType) throws ITFEBizException {
		try{
			log.debug("����ҵ��ƾ֤����" + opertionType + "���ҵ��ƾ֤ģ����Ϣ");
			return StampFacade.getModelByOperationType(opertionType);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * ����ҵ��ƾ֤��ģ��Id���ҵ��ƾ֤����Ϣ	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List<IDto> getFormByModelId(String modelId) throws ITFEBizException {
		try{
			log.debug("����ҵ��ƾ֤ģ��Id" + modelId + "����ҵ��ƾ֤����Ϣ");
			return StampFacade.getFormByModelId(modelId);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * ����ҵ��ƾ֤ģ��Id���ҵ��ƾ֤�����и��º�����λ��	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List<IDto> getPlaceByModelId(String modelId) throws ITFEBizException {
		try{
			log.debug("����ҵ��ƾ֤ģ��Id" + modelId + "����ҵ��ƾ֤����λ����Ϣ");
			return StampFacade.getPlaceByModelId(modelId);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * �����ļ���ŵ����·������ȡ�ļ����ݣ����������ַ����з���
	 */
	public String getFileByPath(String path) throws ITFEBizException {
		try{
			log.debug("��ȡ�������ļ�:" + path);
			return StampFacade.getFileByPath(path);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}	

	/**
	 * ����ҵ��ƾ֤ģ����Ϣ���ҵ��ƾ֤������������λ�á�ģ�����ݵ���Ϣ	 
	 * @generated
	 * @param model
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List<Object> getStampInfoByModel(IDto model) throws ITFEBizException {
		try{
			TsOperationmodelDto modelDto = (TsOperationmodelDto)model;
			List<Object> list = new ArrayList<Object>();
			//ҵ��ƾ֤����ϢΪ���ϵĵ�һ��Ԫ��
			list.add(StampFacade.getFormByModelId(modelDto.getSmodelid()));
			//ҵ��ƾ֤����λ����ϢΪ���ϵĵڶ���Ԫ��
			list.add(StampFacade.getPlaceByModelId(modelDto.getSmodelid()));
			//���ҵ��ƾ֤ģ�������
			list.add(StampFacade.getFileByPathUTF8(modelDto.getSmodelsavepath()));
			return list;
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * ����û�ָ��ҵ��ƾ֤�ĸ���Ȩ��
	 */
	public Map<String,UserStampFuncDto> getUserStampFuction(String modelId) throws ITFEBizException {
		ITFELoginInfo user = getLoginInfo();
		try{
			Map<String,UserStampFuncDto> result = new HashMap<String, UserStampFuncDto>();
			log.debug("��ȡ�û�����Ȩ�ޣ��û�" + user.getSuserCode() + "��ҵ��ƾ֤" + modelId);
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