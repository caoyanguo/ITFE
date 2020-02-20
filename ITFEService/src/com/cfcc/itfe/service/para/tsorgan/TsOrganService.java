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
	 * ����
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
			paradto.setSdescript("�˿�");
			paradto.setSorgcode(organdto.getSorgcode());
			paradto.setSvalue("0");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_RET_TREASURY);
			paradto.setSdescript("�˿�");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PAY_OUT);
			paradto.setSdescript("ʵ���ʽ�");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PAY_OUT);
			paradto.setSdescript("ʵ���ʽ�");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_CORRECT);
			paradto.setSdescript("����");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_CORRECT);
			paradto.setSdescript("����");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);
			paradto.setSdescript("ֱ��֧�����");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);
			paradto.setSdescript("ֱ��֧�����");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
			paradto.setSdescript("��Ȩ֧�����");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
			paradto.setSdescript("��Ȩ֧�����");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_BATCH_OUT);
			paradto.setSdescript("��������");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
			paradto.setSdescript("���а���ֱ��֧��");
			paradto.setSopertype("10");
			DatabaseFacade.getDb().create(paradto);
			paradto.setScode(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
			paradto.setSdescript("���а���ֱ��֧��");
			paradto.setSopertype("20");
			DatabaseFacade.getDb().create(paradto);
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ú�����������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * ɾ������
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 * @throws
	 */
	public void delInfo(IDto dtoInfo) throws ITFEBizException {
		TsOrganDto dto = (TsOrganDto) dtoInfo;

		if (this.getLoginInfo().getSorgcode().equals(dto.getSorgcode())) {
			log.error("����ɾ���Լ���������!");
			throw new ITFEBizException("����ɾ���Լ���������!");
		}
		try {

			// ����ɾ������ʱ�жϻ�������������Ϣ�Ƿ��Ѿ�ɾ��
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
				throw new ITFEBizException("�ú�����������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * ɾ������ʱ���ж������������塢�û������ջ�����������־�� ת�����С�������־��������������Ϣ�Ƿ��Ѿ�ɾ��
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public String checkList(IDto dtoInfo) throws ITFEBizException {

		TsOrganDto dto = (TsOrganDto) dtoInfo;

		// �û���Ϣ
		TsUsersDto userdto = new TsUsersDto();
		userdto.setSorgcode(dto.getSorgcode());

		// ����������Ϣ
		TsTreasuryDto treasuryDto = new TsTreasuryDto();
		treasuryDto.setSorgcode(dto.getSorgcode());

		// ���ջ���
		TsConverttaxorgDto converttaxorgDto = new TsConverttaxorgDto();
		converttaxorgDto.setSorgcode(dto.getSorgcode());

		// ������־
		TsConvertassitsignDto convertassitsignDto = new TsConvertassitsignDto();
		convertassitsignDto.setSorgcode(dto.getSorgcode());

		// ת������
		TsConverttobankDto converttobankDto = new TsConverttobankDto();
		converttobankDto.setSorgcode(dto.getSorgcode());

		// ������־
		TsConvertfinorgDto convertfinorgDto = new TsConvertfinorgDto();
		convertfinorgDto.setSorgcode(dto.getSorgcode());

		// ��������
		TsInfoconnorgDto infoconnorgDto = new TsInfoconnorgDto();
		infoconnorgDto.setSorgcode(dto.getSorgcode());

		String check = null;

		List list = null;

		try {

			// �������û���Ϣ
			list = CommonFacade.getODB().findRsByDto(userdto);

			if (null != list && list.size() > 0) {
				check = "����ɾ���û�������ȷ�ϸû�����Ӧ���û���Ϣ�Ѿ�ɾ����";
			}
			// list.clear();

			// �����¹���������Ϣ
			list = CommonFacade.getODB().findRsByDto(treasuryDto);

			if (null != list && list.size() > 0) {
				check = "����ɾ���û�������ȷ�ϸû�����Ӧ�Ĺ���������Ϣ�Ѿ�ɾ����";
			}

			// ���ջ���
			list = CommonFacade.getODB().findRsByDto(converttaxorgDto);

			if (null != list && list.size() > 0) {
				check = "����ɾ���û�������ȷ�ϸû�����Ӧ�����ջ�����Ϣ�Ѿ�ɾ����";
			}

			// ������־
			list = CommonFacade.getODB().findRsByDto(convertassitsignDto);

			if (null != list && list.size() > 0) {
				check = "����ɾ���û�������ȷ�ϸû�����Ӧ�ĸ�����־��Ϣ�Ѿ�ɾ����";
			}

			// ת������
			list = CommonFacade.getODB().findRsByDto(converttobankDto);

			if (null != list && list.size() > 0) {
				check = "����ɾ���û�������ȷ�ϸû�����Ӧ��ת��������Ϣ�Ѿ�ɾ����";
			}

			// ������־
			list = CommonFacade.getODB().findRsByDto(convertfinorgDto);

			if (null != list && list.size() > 0) {
				check = "����ɾ���û�������ȷ�ϸû�����Ӧ�Ĳ�����־��Ϣ�Ѿ�ɾ����";
			}

			// ��������
			list = CommonFacade.getODB().findRsByDto(infoconnorgDto);

			if (null != list && list.size() > 0) {
				check = "����ɾ���û�������ȷ�ϸû�����Ӧ������������Ϣ�Ѿ�ɾ����";
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
