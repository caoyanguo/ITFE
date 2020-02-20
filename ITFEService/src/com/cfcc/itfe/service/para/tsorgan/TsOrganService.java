package com.cfcc.itfe.service.para.tsorgan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttobankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsSysparaDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time 09-10-20 08:42:02 codecomment:
 */

public class TsOrganService extends AbstractTsOrganService {
	private static Log log = LogFactory.getLog(TsOrganService.class);

	/**
	 * 增加
	 * 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException
	 */
	public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			TsOrganDto organdto=(TsOrganDto)dtoInfo;
			DatabaseFacade.getDb().create(dtoInfo);
			List<TsSysparaDto> dts=new ArrayList<TsSysparaDto>();
			TsSysparaDto paradto=new TsSysparaDto();
			paradto.setScode(BizTypeConstant.BIZ_TYPE_RET_TREASURY);
			paradto.setSdescript("退库");
			paradto.setSorgcode(organdto.getSorgcode());
			paradto.setSvalue("0");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_RET_TREASURY);
			paradto.setSdescript("退库");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PAY_OUT);
			paradto.setSdescript("实拨资金");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PAY_OUT);
			paradto.setSdescript("实拨资金");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_CORRECT);
			paradto.setSdescript("更正");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_CORRECT);
			paradto.setSdescript("更正");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);
			paradto.setSdescript("直接支付额度");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);
			paradto.setSdescript("直接支付额度");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
			paradto.setSdescript("授权支付额度");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
			paradto.setSdescript("授权支付额度");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_BATCH_OUT);
			paradto.setSdescript("批量拨付");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
			paradto.setSdescript("人行办理直接支付");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
			paradto.setSdescript("人行办理直接支付");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("该核算主体代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * 删除机构
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 * @throws
	 */
	public void delInfo(IDto dtoInfo) throws ITFEBizException {
		TsOrganDto dto = (TsOrganDto) dtoInfo;

		if (this.getLoginInfo().getSorgcode().equals(dto.getSorgcode())) {
			log.error("不能删除自己所属机构!");
			throw new ITFEBizException("不能删除自己所属机构!");
		}
		try {

			// 调用删除机构时判断机构所含下属信息是否已经删除
			String delCheck = checkList(dtoInfo);

			if (null != delCheck) {
				throw new ITFEBizException(delCheck);
			}

			DatabaseFacade.getODB().delete(dtoInfo);
			SQLExecutor sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.runQueryCloseCon("delete from TS_SYSPARA where S_ORGCODE='"+dto.getSorgcode()+"'");
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void modInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("该核算主体代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * 删除机构时，判断下属国库主体、用户、征收机构、辅助标志、 转发银行、财政标志、联网机构等信息是否已经删除
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public String checkList(IDto dtoInfo) throws ITFEBizException {

		TsOrganDto dto = (TsOrganDto) dtoInfo;

		// 用户信息
		TsUsersDto userdto = new TsUsersDto();
		userdto.setSorgcode(dto.getSorgcode());

		// 国库主体信息
		TsTreasuryDto treasuryDto = new TsTreasuryDto();
		treasuryDto.setSorgcode(dto.getSorgcode());

		// 征收机构
		TsConverttaxorgDto converttaxorgDto = new TsConverttaxorgDto();
		converttaxorgDto.setSorgcode(dto.getSorgcode());

		// 辅助标志
		TsConvertassitsignDto convertassitsignDto = new TsConvertassitsignDto();
		convertassitsignDto.setSorgcode(dto.getSorgcode());

		// 转发银行
		TsConverttobankDto converttobankDto = new TsConverttobankDto();
		converttobankDto.setSorgcode(dto.getSorgcode());

		// 财政标志
		TsConvertfinorgDto convertfinorgDto = new TsConvertfinorgDto();
		convertfinorgDto.setSorgcode(dto.getSorgcode());

		// 联网机构
		TsInfoconnorgDto infoconnorgDto = new TsInfoconnorgDto();
		infoconnorgDto.setSorgcode(dto.getSorgcode());

		String check = null;

		List list = null;

		try {

			// 机构下用户信息
			list = CommonFacade.getODB().findRsByDto(userdto);

			if (null != list && list.size() > 0) {
				check = "不能删除该机构，请确认该机构对应的用户信息已经删除！";
			}
			// list.clear();

			// 机构下国库主体信息
			list = CommonFacade.getODB().findRsByDto(treasuryDto);

			if (null != list && list.size() > 0) {
				check = "不能删除该机构，请确认该机构对应的国库主体信息已经删除！";
			}

			// 征收机构
			list = CommonFacade.getODB().findRsByDto(converttaxorgDto);

			if (null != list && list.size() > 0) {
				check = "不能删除该机构，请确认该机构对应的征收机构信息已经删除！";
			}

			// 辅助标志
			list = CommonFacade.getODB().findRsByDto(convertassitsignDto);

			if (null != list && list.size() > 0) {
				check = "不能删除该机构，请确认该机构对应的辅助标志信息已经删除！";
			}

			// 转发银行
			list = CommonFacade.getODB().findRsByDto(converttobankDto);

			if (null != list && list.size() > 0) {
				check = "不能删除该机构，请确认该机构对应的转发银行信息已经删除！";
			}

			// 财政标志
			list = CommonFacade.getODB().findRsByDto(convertfinorgDto);

			if (null != list && list.size() > 0) {
				check = "不能删除该机构，请确认该机构对应的财政标志信息已经删除！";
			}

			// 联网机构
			list = CommonFacade.getODB().findRsByDto(infoconnorgDto);

			if (null != list && list.size() > 0) {
				check = "不能删除该机构，请确认该机构对应的联网机构信息已经删除！";
			}

		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);

		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}

		return check;

	}
}
