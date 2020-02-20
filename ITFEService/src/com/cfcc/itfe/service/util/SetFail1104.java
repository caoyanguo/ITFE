package com.cfcc.itfe.service.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class SetFail1104 {
	
	private static Log logger = LogFactory.getLog(SetFail5101.class);
	public void setFail(IDto dto, String orgcode)throws ITFEBizException{
		if(dto instanceof TbsTvDwbkDto){
			TbsTvDwbkDto paydto = (TbsTvDwbkDto)dto;
		SQLExecutor sqlExec = null;
		try {
			TvFilepackagerefDto ref = new TvFilepackagerefDto();
			ref.setSorgcode(orgcode);
			ref.setSpackageno(paydto.getSpackageno());
			List refl = CommonFacade.getODB().findRsByDtoWithUR(ref);
			if (refl != null && refl.size() >0) { // 只有一条记录 则只更新记录状态为待定
				TvFilepackagerefDto newRef = new TvFilepackagerefDto();
				newRef = (TvFilepackagerefDto) refl.get(0);
				if (newRef.getIcount() == 1) {
					String newName = paydto.getSfilename() + "_" + paydto.getSpackageno();
					paydto.setSfilename(newName);
					paydto.setSstatus("0");
					DatabaseFacade.getODB().update(paydto);
					DatabaseFacade.getODB().delete(newRef);//删除是因为文件名是主键无法修改(update)，只好先删除后创建
					newRef.setSfilename(newName); //将只有一笔记录的包流水对应关系的文件名同时修改，这样就销号单独组报文了而不受原文件名限制
					DatabaseFacade.getODB().create(newRef);
					return;
				}
				// 1.更改包流水对应关系
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();

				String movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
				// 凭证金额
				sqlExec.addParam(paydto.getFamt());
				// 核算主体代码
				sqlExec.addParam(orgcode);
				// 文件名
				sqlExec.addParam(paydto.getSfilename());
				// 包流水号
				sqlExec.addParam(paydto.getSpackageno());
				sqlExec.runQueryCloseCon(movedataSql);
				String tmpPackNo = SequenceGenerator
						.changePackNoForLocal(SequenceGenerator.getNextByDb2(
								SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,
								MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				String newFileName = paydto.getSfilename() + "_" + tmpPackNo;
				paydto.setSpackageno(tmpPackNo); // 更新新流水号
				paydto.setSfilename(newFileName);
				paydto.setSstatus("0");
				newRef.setSorgcode(orgcode);
				newRef.setSpackageno(tmpPackNo);
				newRef.setIcount(1);
				newRef.setSfilename(newFileName);
				newRef.setNmoney(paydto.getFamt());
				DatabaseFacade.getODB().create(newRef); // 保存新的对应关系
				DatabaseFacade.getODB().update(paydto); // 更新信息
			}else{
				throw new ITFEBizException("无法找到对应包信息");
			}
		} catch (Exception e) {
			logger.error("退库1104业务异常!", e);
			throw new ITFEBizException("退库1104业务异常!", e);
		}
	}
	}

}
