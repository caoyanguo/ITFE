package com.cfcc.itfe.service.para.tsconvertassitsign;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.service.para.tsconvertassitsign.AbstractTsConvertassitsignService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time   09-10-20 08:42:02
 * codecomment: 
 */

public class TsConvertassitsignService extends AbstractTsConvertassitsignService {
	private static Log log = LogFactory.getLog(TsConvertassitsignService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsConvertassitsignDto dto = (TsConvertassitsignDto)dtoInfo;
    		if(null == dto.getSbudgetsubcode() || "".equals(dto.getSbudgetsubcode().trim())){
    			dto.setSbudgetsubcode("N");
    		}
    		
    		if(null == dto.getStrecode() || "".equals(dto.getStrecode().trim())){
    			dto.setStrecode("N");
    		}
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
		if (e.getSqlState().equals("23505")) { 
			throw new ITFEBizException("字段核算主体代码，国库代码，撗联辅助标志，预算科目代码已存在，不能重复录入！", e);
	}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
      return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().delete(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} 
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
      
    	try {
    		TsConvertassitsignDto dto = (TsConvertassitsignDto)dtoInfo;
    		if(null == dto.getSbudgetsubcode() || "".equals(dto.getSbudgetsubcode().trim())){
    			dto.setSbudgetsubcode("N");
    		}

    		if(null == dto.getStrecode() || "".equals(dto.getStrecode().trim())){
    			dto.setStrecode("N");
    		}
   		DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段核算主体代码，国库代码，撗联辅助标志，预算科目代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

	public void dataimport(String filepath, String importtype)
			throws ITFEBizException {
		try{
			// 得到文件上传配置
			FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
			String root = sysconfig.getRoot();
			String filerootpath = (root+filepath).replace("/", File.separator).replace("\\",File.separator);
			List<String[]> fileContent = FileUtil.getInstance().readFileWithLine(filerootpath, ",");
			if(fileContent!=null&&fileContent.size()>0)
			{
				String[] temp = null;
				HashMap<String,TsConvertassitsignDto> taxorgMap = getViceSignMap(this.getLoginInfo().getSorgcode());
				List<IDto> dtoList = new ArrayList<IDto>();
				TsConvertassitsignDto dto = null;
				for(int i=0;i<fileContent.size();i++)
				{
					temp = fileContent.get(i);
					if(!taxorgMap.containsKey(temp[0]+temp[1]+temp[2]+temp[3])&&"addimport".equals(importtype))
					{
						dto =new TsConvertassitsignDto();
						dto.setSorgcode(temp[0]);//机构代码
						if(dto.getSorgcode()==null||dto.getSorgcode().length()>12)
							throw new ITFEBizException("导入数据失败:核算主体代码必须小于等于12位",null);
						dto.setStrecode(temp[1]);//国库代码
						if(dto.getStrecode()==null||dto.getStrecode().length()>10)
							throw new ITFEBizException("导入数据失败:国库主体代码必须小于等于10位",null);
						dto.setSbudgetsubcode(temp[2]);//预算科目代码
						if(dto.getSbudgetsubcode()==null||dto.getSbudgetsubcode().length()>30)
							throw new ITFEBizException("导入数据失败:预算科目代码必须小于等于30位",null);
						dto.setStbsassitsign(temp[3]);//TBS辅助标志
						if(dto.getStbsassitsign()==null||dto.getStbsassitsign().length()>30)
							throw new ITFEBizException("导入数据失败:TBS辅助标志必须小于等于30位",null);
						dto.setStipsassistsign(temp[4]);//Tips辅助标志
						if(dto.getStipsassistsign()==null||dto.getStipsassistsign().length()>35)
							throw new ITFEBizException("导入数据失败:TIPS辅助标志必须小于等于35位",null);
						dtoList.add(dto);
					}else if("deleteimport".equals(importtype))
					{
						dto =new TsConvertassitsignDto();
						dto.setSorgcode(temp[0]);//机构代码
						if(dto.getSorgcode()==null||dto.getSorgcode().length()>12)
							throw new ITFEBizException("导入数据失败:核算主体代码必须小于等于12位",null);
						dto.setStrecode(temp[1]);//国库代码
						if(dto.getStrecode()==null||dto.getStrecode().length()>10)
							throw new ITFEBizException("导入数据失败:国库主体代码必须小于等于10位",null);
						dto.setSbudgetsubcode(temp[2]);//预算科目代码
						if(dto.getSbudgetsubcode()==null||dto.getSbudgetsubcode().length()>30)
							throw new ITFEBizException("导入数据失败:预算科目代码必须小于等于30位",null);
						dto.setStbsassitsign(temp[3]);//TBS辅助标志
						if(dto.getStbsassitsign()==null||dto.getStbsassitsign().length()>30)
							throw new ITFEBizException("导入数据失败:TBS辅助标志必须小于等于30位",null);
						dto.setStipsassistsign(temp[4]);//Tips辅助标志
						if(dto.getStipsassistsign()==null||dto.getStipsassistsign().length()>35)
							throw new ITFEBizException("导入数据失败:TIPS辅助标志必须小于等于35位",null);
						dtoList.add(dto);
					}
				}
				if("deleteimport".equals(importtype))
				{
					String delSQL = "DELETE FROM TS_CONVERTASSITSIGN where S_ORGCODE='"+this.getLoginInfo().getSorgcode()+"'";
					SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					sqlExec.runQueryCloseCon(delSQL);
				}
				DatabaseFacade.getDb().create(CommonUtil.listTArray(dtoList));
				FileUtil.getInstance().deleteFile(filerootpath);
			}
		}catch(Exception e)
		{
			log.error(e);
			throw new ITFEBizException("导入数据失败:",e);
		}		
	}
	/**
	 * 辅助标志对照关系表
	 * 
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, TsConvertassitsignDto> getViceSignMap(String sbookorgcode)
			throws ITFEBizException {
		HashMap<String, TsConvertassitsignDto> vicemap = new HashMap<String, TsConvertassitsignDto>();
		TsConvertassitsignDto viceDto = new TsConvertassitsignDto();
		if(sbookorgcode!=null&&!"".equals(sbookorgcode)&&!"000000000000".equals(sbookorgcode))
			viceDto.setSorgcode(sbookorgcode);
		try {
			List<TsConvertassitsignDto> list = CommonFacade.getODB().findRsByDto(viceDto);
			for (int i = 0; i < list.size(); i++) {
				TsConvertassitsignDto dto = list.get(i);
				vicemap.put(dto.getSorgcode()+dto.getStrecode() + dto.getSbudgetsubcode()+ dto.getStbsassitsign(),dto);
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("取得辅助标志对照关系表异常 \n" + e.getMessage(), e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("取得辅助标志对照关系表异常 \n" + e.getMessage(), e);
		}
		return vicemap;
	}
}