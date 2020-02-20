/**
 * 
 */
package com.cfcc.itfe.facade.stamp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.TsOperationformDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOperationplaceDto;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.pk.TsOperationtypePK;
import com.cfcc.itfe.persistence.pk.TsOrganPK;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 电子印鉴参数使用的Facade
 * @author sjz
 *
 */
public class StampFacade {
	//总库与代理银行联网中使用的Sequence的名字
	private static String StampSendSeqName = "STAMP_SEND_SEQUENCE";
	private static String StampRecvSeqName = "STAMP_RECV_SEQUENCE";
	private static String StampFileSeqName = "STAMP_FILE_SEQUENCE";
	private static String BIZSeqName = "BIZ_SEQUENCE";
	
	/**
	 * 根据文件种类获得业务凭证类型信息	 
	 * @generated
	 * @param fileType
	 * @return com.cfcc.itfe.persistence.dao.TsOperationtypeDao
	 * @throws ITFEBizException	 
	 */
	public static TsOperationtypeDto getOperationTypeByFileType(String fileType) throws JAFDatabaseException{
		TsOperationtypePK pk = new TsOperationtypePK();
		pk.setSoperationtypecode(fileType);
		return (TsOperationtypeDto)DatabaseFacade.getDb().find(pk);
	}

	/**
	 * 根据业务凭证种类获得Model信息	 
	 * @generated
	 * @param modelId
	 * @return com.cfcc.itfe.persistence.dto.TsOperationmodelDto
	 * @throws ITFEBizException	 
	 */
	public static TsOperationmodelDto getModelByOperationType(String opertionType) throws ValidateException,JAFDatabaseException,ITFEBizException{
		TsOperationmodelDto model = new TsOperationmodelDto();
		model.setSoperationtypecode(opertionType);
		List list = CommonFacade.getODB().findRsByDto(model);
		if ((list == null) || (list.size() == 0)){
			throw new ITFEBizException("找不到业务凭证种类" + opertionType + "对应的模版信息");
		}
		return (TsOperationmodelDto)list.get(0);
	}

	/**
	 * 根据业务凭证的模版Id获得业务凭证联信息	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public static List<IDto> getFormByModelId(String modelId) throws ValidateException,JAFDatabaseException,ITFEBizException{
		TsOperationformDto form = new TsOperationformDto();
		form.setSmodelid(modelId);
		List<IDto> list = CommonFacade.getODB().findRsByDto(form);
		if ((list == null) || (list.size() == 0)){
			throw new ITFEBizException("找不到业务凭证模版Id" + modelId + "对应的联信息");
		}
		return list;
	}

	/**
	 * 根据业务凭证模版Id获得业务凭证中所有盖章和验章位置	 
	 * @generated
	 * @param modelId
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public static List<IDto> getPlaceByModelId(String modelId) throws ValidateException,JAFDatabaseException,ITFEBizException{
		TsOperationplaceDto place = new TsOperationplaceDto();
		place.setSmodelid(modelId);
		List<IDto> list = CommonFacade.getODB().findRsByDto(place);
		if ((list == null) || (list.size() == 0)){
			throw new ITFEBizException("找不到业务凭证模版Id" + modelId + "对应的盖章位置信息");
		}
		return list;
	}

	/**
	 * 根据文件存放的相对路径，获取文件内容，并保存在字符串中返回
	 */
	public static String getFileByPath(String path) throws ITFEBizException,FileOperateException {
		String srvFile = ITFECommonConstant.FILE_ROOT_PATH + path;
		File file = new File(srvFile);
		if (!file.exists()){
			throw new ITFEBizException("找不到指定的文件" + path);
		}
		return FileUtil.getInstance().readFile(srvFile);
	}
	
	/**
	 * 根据文件存放的相对路径，获取UTF8格式的文件内容，并保存在字符串中返回
	 */
	public static String getFileByPathUTF8(String path) throws ITFEBizException,FileOperateException,IOException {
		String srvFile = ITFECommonConstant.FILE_ROOT_PATH + path;
		File file = new File(srvFile);
		if (!file.exists()){
			throw new ITFEBizException("找不到指定的文件" + path);
		}
		return FileUtil.getInstance().readFileUtf8(srvFile);
	}
	
	/**
	 * 返回20位长的发送或接收流水号
	 * @param flag 收发标志，接收-JS，发送-FS
	 * @return
	 * @throws SequenceException
	 */
	public static String getStampSendSeq(String flag) throws SequenceException{
		String seqName;
		
		if ("JS".equals(flag)){
			seqName = StampRecvSeqName;
		}else if ("FS".equals(flag)){
			seqName = StampSendSeqName;
		}else{
			seqName = StampFileSeqName;
		}
		String seq = SequenceGenerator.getNextByDb2(seqName, SequenceName.TRAID_SEQ_CACHE, SequenceName.TRAID_SEQ_STARTWITH);
		//流水号为18位长
		seq = "00000000000000000000" + seq;
		seq = seq.substring(seq.length() - 18, seq.length());
		return flag + seq;
	}

	/**
	 * 获得传入机构代码的所有可连通性机构
	 * @param orgCode 机构代码
	 * @return 所有可以连通的机构
	 * @throws JAFDatabaseException
	 */
	public static List<TsOrganDto> getAllConnOrgs(String orgCode) throws JAFDatabaseException{
		String sql = "select s_OrgCode,s_OrgName from ts_organ where s_OrgCode in "
			+ "(select s_ConnOrgCodeA from ts_connection where s_ConnOrgCodeB='" + orgCode + "' union "
			+ "select s_ConnOrgCodeB from ts_connection where s_ConnOrgCodeA='" + orgCode + "') order by s_OrgCode";
		SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
		SQLResults res = exec.runQueryCloseCon(sql, TsOrganDto.class);
		return (List<TsOrganDto>)res.getDtoCollection();
	}
	
	/**
	 * 获得缺省的连通机构，只在业务凭证发送的时候使用
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static TsOrganDto getDefaultConnOrgs() throws JAFDatabaseException{
		TsOrganPK pk = new TsOrganPK();
		pk.setSorgcode("999");
		return (TsOrganDto)DatabaseFacade.getDb().find(pk);
	}
	
	/**
	 * 返回20位长的发送或接收流水号
	 * @param flag 收发标志，接收-JS，发送-FS
	 * @return
	 * @throws SequenceException
	 */
	public static String getBizSeq(String biz) throws SequenceException{
		String seq = SequenceGenerator.getNextByDb2(BIZSeqName+"_"+biz, SequenceName.TRAID_SEQ_CACHE, SequenceName.TRAID_SEQ_STARTWITH);
		//流水号为20位长
		seq = "00000000000000000000" + seq;
		seq = seq.substring(seq.length() - 16, seq.length());
		return biz+seq;
	}

}
