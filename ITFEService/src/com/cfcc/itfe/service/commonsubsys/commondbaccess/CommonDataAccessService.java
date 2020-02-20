package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ComboListDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsSysparaDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.security.DESPlus;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileRootPathUtil;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.itfe.util.encrypt.Hex;
import com.cfcc.itfe.util.encrypt.MD5Sign;
import com.cfcc.itfe.util.encrypt.TripleDES;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangwz
 * @time 08-11-18 08:28:09
 * @codecomponent
 */

@SuppressWarnings("unchecked")
public class CommonDataAccessService extends AbstractCommonDataAccessService {
	private static Log log = LogFactory.getLog(CommonDataAccessService.class);
	private static Map<String,Map> getMap = new HashMap<String,Map>();
	/**
	 * �������ݿ�������Ҹñ����������
	 * 
	 * @param dto
	 *            IDTo.Class
	 * @return
	 * @throws TASBizException
	 */
	public List<IDto> findAllDtos(Class dto) throws ITFEBizException {
		try {
			return DatabaseFacade.getODB().find(dto);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	/**
	 * ʹ������ķ�ʽ���������ݿ�������Ҹñ����������
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List<IDto> findAllDtosUR(Class dto) throws ITFEBizException {
		try {
			return DatabaseFacade.getODB().findWithUR(dto);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��ѯ�����쳣", e);
		}

	}

	/**
	 * ����Dto�����õ�����������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��, (�����ѯ) Լ����ֻ����Ե�ֵ��ȵ��������в�ѯ
	 * 
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> findRsByDto(IDto idto) throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDto(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	/**
	 * ����Dto�����õ�����������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��, (�����ѯ) Լ����ֻ����Ե�ֵ��ȵ��������в�ѯ
	 * 
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> findRsByDtoUR(IDto idto) throws ITFEBizException {
		try {
			// if (idto instanceof TmUserDto) {
			// TmUserDto tmUserDto = (TmUserDto)
			// CommonFacade.getODB().findRsByDtoWithUR(idto);
			//
			// TASLoginInfo loginfo = new TASLoginInfo();
			// loginfo.setUserID(tmUserDto.getSusername());
			// loginfo.setBookOrgCode(tmUserDto.getSbookorgcode());
			// loginfo.setPassword(tmUserDto.getSpassword());
			// setLoginInfo(loginfo);
			// }

			return CommonFacade.getODB().findRsByDtoWithUR(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.tas.service.commonsubsys.commondbaccess.ICommonDataAccessService
	 * #findByDto(com.cfcc.jaf.persistence.jaform.parent.IDto)
	 */
	public List<IDto> findByDto(IDto idto) throws ITFEBizException {
		try {
			CommonQto qto = SqlUtil.IDto2CommonQto(idto);
			return DatabaseFacade.getODB().find(idto.getClass(), qto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.tas.service.commonsubsys.commondbaccess.ICommonDataAccessService
	 * #findByDtoWithUR(com.cfcc.jaf.persistence.jaform.parent.IDto)
	 */
	public List<IDto> findByDtoWithUR(IDto idto) throws ITFEBizException {
		try {
			CommonQto qto = SqlUtil.IDto2CommonQto(idto);
			return DatabaseFacade.getODB().findWithUR(idto.getClass(), qto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	public List findRsByDtoWithBookOrg(IDto idto) throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDtoWithUR(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	public List findRsByDtoWithBookOrgUR(IDto idto) throws ITFEBizException {
		try {

			// ITFELoginInfo loginfo = getLoginInfo();
			// if (loginfo == null || loginfo.getBookOrgCode() == null
			// || "".equals(loginfo.getBookOrgCode())) {
			// return CommonFacade.getODB().findRsByDtoWithUR(idto);
			// }
			// BeanUtils.setProperty(idto, "sbookorgcode", getLoginInfo()
			// .getBookOrgCode());
			return CommonFacade.getODB().findRsByDtoWithUR(idto);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	/**
	 * ����Sequence���* @generated
	 * 
	 * @param seqname
	 * @return java.lang.Long
	 * @throws ITFEBizException
	 */
	public Long getSequenceNo(String seqname) throws ITFEBizException {
		Long ivousrlno;
		try {
			ivousrlno = new Long(SequenceGenerator.getNextByDb2(seqname, 1000,
					1));
		} catch (NumberFormatException e) {
			log.error(e);
			throw new ITFEBizException("��ȡSequence����", e);
		} catch (SequenceException e) {
			log.error(e);
			throw new ITFEBizException("��ȡSequence����", e);
		}
		return ivousrlno;
	}

	/**
	 * �������ķ�ҳ��ѯ
	 * 
	 * @param dto
	 * @param request
	 * @param where
	 * @return
	 * @throws ITFEBizException
	 */
	public PageResponse findRsByDtoWithWherePaging(IDto dto,
			PageRequest request, String where) throws ITFEBizException {
		try {
			if(dto.getClass().getName().equals("com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto"))
			{
				return findRsByDtoWithWhereP(dto,request, where);
			}else
			{
				return CommonFacade.getODB().findRsByDtoWithWherePaging(dto,request, where);
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��ѯ�쳣", e);
		} catch (ValidateException e) {
			throw new ITFEBizException("��ѯ�쳣", e);
		}
	}
	
	/**
	 * ��Where��������DTO�����ѯ������ҳ���ͻ��˲�ѯʹ��
	 * 
	 * @param dto
	 * @param request
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public PageResponse findRsByDtoWithWhereP(IDto dto,
			PageRequest request, String where) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		PageResponse response = new PageResponse(request);

		// ��ҳ��ѯ
		Method m;
		String tableName=null;
		try {
			m = dto.getClass().getMethod("tableName", new Class[] {});
			tableName = (String) m.invoke(null, new Object[] {});
		} catch (Exception e) {
			
		}
		TvIncomeonlineIncomeondetailBlendDto sdto = (TvIncomeonlineIncomeondetailBlendDto)dto;
		if(sdto.getSseq()!=null&&sdto.getSseq().length()==8&&sdto.getStrename()!=null&&"1".equals(sdto.getStrename()))
		{
			try {
				String dateyear = sdto.getSseq();
				String currdate = TimeFacade.getCurrentStringTime();
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//				java.util.Date start = sdf.parse(currdate);java.util.Date end = sdf.parse(dateyear);(start.getTime()-end.getTime())/(24*60*60*1000)>100&&
				if(!(dateyear.substring(0,4).equals(currdate.substring(0,4))))
					tableName = tableName +"_"+dateyear.substring(0,4);
			}catch (Exception e) {
				
			}
		}
		sdto.setSseq(null);
		sdto.setStrename(null);
		String sqlwhere = "select * from " + tableName;
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			if (where != null) {
				sqlwhere += qto.getSWhereClause() + " and " + where
						+ " with ur";
			} else {
				sqlwhere += qto.getSWhereClause() + " with ur";
			}
		} else {
			if (where != null) {
				sqlwhere += " where " + where;
			}
		}

		if (response.getTotalCount() == 0) {
			SQLExecutor sqlExe = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			if (qto != null) {
				sqlExe.addParam(qto.getLParams());
			}
			// ��ʼ���ܼ�¼��
			String countsqlwhere = sqlwhere.replace("*", "count(*)");
			int count = sqlExe.runQueryCloseCon(countsqlwhere, dto.getClass())
					.getInt(0, 0);
			response.setTotalCount(count);
		}

		SQLExecutor sqlExe2 = DatabaseFacade.getDb()
				.getSqlExecutorFactory().getSQLExecutor();
		if (qto != null) {
			sqlExe2.addParam(qto.getLParams());
		}

		SQLResults res = sqlExe2.runQueryCloseCon(sqlwhere, dto.getClass(),
				request.getStartPosition(), request.getPageSize(), false);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		response.getData().clear();
		response.setData(list);
		return response;
	}
	/**
	 * 
	 * @param idto
	 * @throws ITFEBizException
	 */
	public void updateData(IDto idto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().update(idto);
			if(idto.getClass().getName().equals("com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto"))
			{
				SQLExecutor execDetail = null;
				try{
					execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();;
					TvIncomeonlineIncomeondetailBlendDto tmpdto = null;
					tmpdto = (TvIncomeonlineIncomeondetailBlendDto)idto;
					String sql = "update TV_INCOMEONLINE_INCOMEONDETAIL_BLEND_"+tmpdto.getSintredate().substring(0,4)+" set S_ETPCODE=?,S_ETPNAME=?, S_BDGSBTNAME=?, S_REMARK1=?,S_REMARK2=?,S_REMARK5=?,S_REMARK4=? where S_SEQ=?";
					execDetail.clearParams();
					if(tmpdto.getSetpcode()==null)
						tmpdto.setSetpcode("-");
					if(tmpdto.getSetpname()==null)
						tmpdto.setSetpname("-");
					if(tmpdto.getSbdgsbtname()==null)
						tmpdto.setSbdgsbtname("-");
					if(tmpdto.getSremark1()==null)
						tmpdto.setSremark1("-");
					if(tmpdto.getSremark2()==null)
						tmpdto.setSremark2("-");
					if(tmpdto.getSremark5()==null)
						tmpdto.setSremark5("-");
					if(tmpdto.getSremark4()==null)
						tmpdto.setSremark4("-");
					execDetail.addParam(tmpdto.getSetpcode());
					execDetail.addParam(tmpdto.getSetpname());
					execDetail.addParam(tmpdto.getSbdgsbtname());
					execDetail.addParam(tmpdto.getSremark1());
					execDetail.addParam(tmpdto.getSremark2());
					execDetail.addParam(tmpdto.getSremark5());
					execDetail.addParam(tmpdto.getSremark4());
					execDetail.addParam(tmpdto.getSseq());
					execDetail.runQueryCloseCon(sql);
				}catch(Exception e)
				{
					log.error(e);
				}finally
				{
					if(execDetail!=null)
						execDetail.closeConnection();
				}
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("���³���", e);
		}
	}

	public void create(IDto idto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().create(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("���������쳣", e);
		}
	}
	
	/**
	 * ͨ����Ҫɾ���Ķ���ɾ�����ݿ��е�һ����¼
	 * 
	 * @param _dto
	 * @throws ITFEBizException 
	 * @throws JAFDatabaseException
	 */
	public void delete(IDto _dto) throws ITFEBizException  {
		try {
			DatabaseFacade.getODB().delete(_dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������쳣", e);
		}

	}
	
	public List findListbyDto(String idto) throws ITFEBizException {
		return null;
	}

	public void updatesvouno(IDto idto) throws ITFEBizException {

	}

	/**
	 * ���ط��������ڣ���ʽΪyyyy-MM-dd
	 */
	public String getSysDate() throws ITFEBizException {
		return TimeFacade.getCurrent2StringTime();
	}

	public List findOrgList(String orgcode) throws ITFEBizException {
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "select S_ORGCODE,S_ORGNAME from ts_organ where S_ORGCODE =? or S_GOVERNORGCODE=?";
			exec.addParam(orgcode);
			exec.addParam(orgcode);
			List<TsOrganDto> list = (List<TsOrganDto>) exec.runQueryCloseCon(
					sql, TsOrganDto.class).getDtoCollection();
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ��������������", e);
		}
	}

	/**
	 * ��ȡ���ͻ��߽�����־�е������б�
	 * 
	 * @param strTag
	 *            ���ͽ��ձ�־
	 */
	public List<ComboListDto> findLogDateList(String strTag)
			throws ITFEBizException {
		try {
			// �û���Ϣ
			ITFELoginInfo user = getLoginInfo();
			// ֻ��ѯ�����������ݵ���־�б�
			String sql = "select distinct s_date from ";
			if (StateConstant.LOG_RECV.equals(strTag)) {
				// ������־
				sql += "tv_recvlog where S_RECVORGCODE='";
			} else {
				// ������־
				sql += "tv_sendlog where S_SENDORGCODE='";
			}
			sql += user.getSorgcode() + "' order by s_date desc";
			SQLResults res = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor().runQueryCloseCon(sql);
			List<ComboListDto> dateList = new ArrayList<ComboListDto>();
			int rowCount = res.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				ComboListDto one = new ComboListDto();
				one.setData(res.getString(i, 0));
				one.setDisplay(res.getString(i, 0));
				dateList.add(one);
			}
			return dateList;
		} catch (Exception e) {
			throw new ITFEBizException("��ѯ�쳣", e);
		}
	}

	/**
	 * ���ط��������ݿ�ʱ�� ��ʱ���ϵͳʱ�䲻�ԣ�����ȡ���ݿ�ʱ��
	 */
	public String getSysDBDate() throws ITFEBizException {
		try {
			// ��ȡ��ǰʱ��
			Date now = TSystemFacade.findDBSystemDate();
			// ���ļ����浽����Ŀ¼��
			return DateUtil.date2String2(now);
		} catch (Exception e) {
			throw new ITFEBizException("��ȡϵͳʱ���쳣", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService
	 * #getMiYao(java.lang.String)
	 */
	public HashMap getMiYao(String orgcode) throws ITFEBizException {

		return PublicSearchFacade.findOrgKey(orgcode);
	}

	public PageResponse findRsByDtoWithWherePaging(IDto dto,
			PageRequest request, String wheresql, String sort, String tablename)
			throws ITFEBizException {

		try {
			return CommonFacade.getODB().findRsByDtoWithWherePaging(dto,
					request, wheresql, sort, tablename);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��ѯ�쳣", e);
		} catch (ValidateException e) {
			throw new ITFEBizException("��ѯ�쳣", e);
		}
	}

	public List findRsByDtocheckUR(IDto idto, String flag)
			throws ITFEBizException {
		List list = null;
		try {
			// У���ѯ���⡢�����Ƿ��ڵ�ǰ������¼��
			if (flag.equals("1")) {
				CommonQto qto = SqlUtil.IDto2CommonQto(idto);
				List<TsConvertfinorgDto> tstreasurylist = DatabaseFacade
						.getODB().findWithUR(idto.getClass(), qto);

				return tstreasurylist;
			}
			// У���ѯ���ջ����Ƿ������ջ���������Ӧ��ϵ��
			else if (flag.equals("2")) {
				CommonQto qto = SqlUtil.IDto2CommonQto(idto);
				List<TsTaxorgDto> tstaxlist = DatabaseFacade.getODB()
						.findWithUR(idto.getClass(), qto);
				return tstaxlist;
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
		return list;
	}

	/**
	 * 
	 * �ж��ļ����Ƿ��ظ�
	 */

	public Boolean verifyImportRepeat(String filename) throws ITFEBizException {
		return VerifyFileName.verifyImportRepeat(getLoginInfo().getSorgcode(),
				filename);

	}

	/**
	 * �ж�ֱ���ύȨ��
	 */
	public Boolean judgeDirectSubmit(String biztype, String orgcode)
			throws ITFEBizException {
		String sqlWhere="";
		if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
			sqlWhere=" and ( s_code= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+"' or s_code='"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+"')";
		}else if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
			sqlWhere=" and ( s_code= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+"' or s_code='"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+"')";
		}else{
			sqlWhere=" and s_code= '"+biztype+"'";
		}
		try {
			TsSysparaDto dto = new TsSysparaDto();
			dto.setSorgcode(orgcode);
//			dto.setScode(biztype);
			dto.setSopertype("10");
			List<TsSysparaDto> list = CommonFacade.getODB().findRsByDtoForWhere(dto, sqlWhere);
			if (null == list || list.size() == 0) {
				throw new ITFEBizException("�������������ύȨ�޲���");
			} else {
				if ("1".equals(list.get(0).getSvalue())) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			log.error("��ѯ���ݿ�ʧ�ܣ�", e);
			throw new ITFEBizException("У��ֱ���ύȨ��ʧ�ܣ�" + "\n" + e.getMessage(), e);
		}
	}

	/**
	 * �ж���������Ȩ��
	 * 
	 * @param biztype
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public Boolean judgeBatchConfirm(String biztype, String orgcode)
			throws ITFEBizException {
		String sqlWhere="";
		if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY) ||biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
			sqlWhere=" and s_code= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY +"'";
		}else if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK )|| biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
			sqlWhere=" and  s_code= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+"'";
		}else{
			sqlWhere=" and s_code= '"+biztype+"'";
		}
		try {
			TsSysparaDto dto = new TsSysparaDto();
			dto.setSorgcode(orgcode);
			dto.setSopertype("20");
			List<TsSysparaDto> list = CommonFacade.getODB().findRsByDtoForWhere(dto, sqlWhere);
			if (null == list || list.size() == 0) {
				throw new ITFEBizException("�������������ύȨ�޲���");
			} else {
				if ("1".equals(list.get(0).getSvalue())) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			log.error("��ѯ���ݿ�ʧ�ܣ�", e);
			throw new ITFEBizException("У����������Ȩ��ʧ�ܣ�" + "\n" + e.getMessage(), e);
		}
	}

	/**
	 * ���ݼ���ʱ�ǽ�����־
	 */
	public void saveTvrecvlog(List pathlist, String biztype)
			throws ITFEBizException {

		// �ļ��ϴ���·��
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		List dtos = new ArrayList();
		try {
			for (int i = 0; i < pathlist.size(); i++) {
				TvRecvlogDto dto = new TvRecvlogDto();
				// ȡ������־��ˮ
				dto.setSrecvno(StampFacade.getStampSendSeq("JS"));
				// ���ͻ�������
				dto.setSrecvorgcode(getLoginInfo().getSorgcode());
				// �ϴ�����
				dto.setSdate(DateUtil.date2String2(TSystemFacade
						.findDBSystemDate()));
				// ҵ������
				dto.setSoperationtypecode(biztype);
				// �ļ��ڷ������ĵ�ַ
				dto.setStitle(root + pathlist.get(i).toString());
				// ����ʱ��
				dto.setSrecvtime(TSystemFacade.getDBSystemTime());
				// ��������
				dto.setSsenddate(TimeFacade.getCurrentStringTime());
				// ������˵��
				dto.setSretcodedesc("�ϴ��ɹ�");
				dto.setSifsend(StateConstant.MSG_SENDER_FLAG_3);// �ļ���ʽ
				dto.setSturnsendflag(StateConstant.SendFinNo);// ת����־
				dto.setSdemo("�ļ��ϴ��ɹ�");
				dtos.add(dto);
			}
			DatabaseFacade.getODB().create(CommonUtil.listTArray(dtos));
		} catch (Exception e) {
			log.error("���������־ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
	}

	// ����Կ����
	public String decrypt(String str) throws ITFEBizException {
		try {
			DESPlus dPlus = new DESPlus();
			return dPlus.decrypt(str);
		} catch (Exception e) {
			log.error("��Կ����ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}

	}

	public Boolean auditBankCode(String bankcode) throws ITFEBizException {
		try {
			TsPaybankDto dto = new TsPaybankDto();
			dto.setSpaybankno(bankcode);
			dto.setSstate("1");
			List list = CommonFacade.getODB().findRsByDto(dto);
			if (null == list || list.size() == 0) {
				dto.setSbankno(bankcode);
				dto.setSpaybankno(null);
				List listbank = CommonFacade.getODB().findRsByDto(dto);
				if (null == listbank || listbank.size() == 0) {
					throw new ITFEBizException("�кŲ����ڣ�");
				}
				return true;
			} else {
				return true;
			}
		} catch (Exception e) {
			log.error("�����к�ʧ�ܣ�", e);
			throw new ITFEBizException("�����к�ʧ�ܣ�", e);
		}
	}

	public Date getAdjustDate() throws ITFEBizException {
		try {
			List<TsSystemDto> list = DatabaseFacade.getODB().find(
					TsSystemDto.class);
			if (list != null && list.size() > 0) {
				String str = list.get(0).getSendadjustday();
				return Date.valueOf(str.substring(0, 4) + "-"
						+ str.substring(4, 6) + "-" + str.substring(6, 8));
			} else {
				throw new ITFEBizException("��ȡ������ʱ��ʧ��");
			}
		} catch (Exception e) {
			log.error("��ȡ����������ʧ�ܣ�", e);
			throw new ITFEBizException("��ȡ����������ʧ�ܣ�", e);
		}

	}

	public void delServerWrongFile(List filelist) throws ITFEBizException {
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
				.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");

		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();
		try {
			for (int i = 0; i < filelist.size(); i++) {
				File tmpfile = new File(root + filelist.get(i));
				if (tmpfile.exists()) {
					tmpfile.delete();
					if (((String) filelist.get(i)).endsWith(".pas")) {
						File pasfile = new File(root
								+ ((String) filelist.get(i)).replaceAll(".pas",
										".tmp"));
						if (pasfile.exists())
							pasfile.delete();

					} else {
						File pasfile = new File(root
								+ ((String) filelist.get(i)).replaceAll(".txt",
										".tmp"));
						if (pasfile.exists())
							pasfile.delete();
					}
				}
			}
		} catch (Exception e) {
			log.error("ɾ���������ļ�ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
	}

	public List findRsByDto(IDto idto, String orderstr) throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDtoForWhere(idto, orderstr);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}

	public List findRsByDto(IDto idto, String sort, String tablename)
			throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDto(idto, sort, tablename);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}
	
	/**
	 * ��ȡ�ļ���·��
	 */
	public String getFileRootPath(){
		return ITFECommonConstant.FILE_ROOT_PATH;
	}

	public void saveMassTvrecvlog(Map maps, String biztype)
			throws ITFEBizException {
		// �ļ��ϴ���·��
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		List dtos = new ArrayList();
		try {
			Iterator<String> iterators = maps.keySet().iterator();
			while (iterators.hasNext()) {
				String str = iterators.next(); // ��֤��
				TvRecvlogDto dto = new TvRecvlogDto();
				// ȡ������־��ˮ
				dto.setSrecvno(StampFacade.getStampSendSeq("JS"));
				// ���ͻ�������
				dto.setSrecvorgcode(getLoginInfo().getSorgcode());
				// �ϴ�����
				dto.setSdate(DateUtil.date2String2(TSystemFacade
						.findDBSystemDate()));
				// ҵ������
				dto.setSoperationtypecode(biztype);
				// �ļ��ڷ������ĵ�ַ
				dto.setStitle(root + maps.get(str));
				dto.setSbatch(str); // ��¼��֤��
				// ����ʱ��
				dto.setSrecvtime(TSystemFacade.getDBSystemTime());
				// ��������
				dto.setSsenddate(TimeFacade.getCurrentStringTime());
				// ������˵��
				dto.setSretcodedesc("�ϴ��ɹ�");
				dto.setSifsend(StateConstant.MSG_SENDER_FLAG_3);// �ļ���ʽ
				dto.setSturnsendflag(StateConstant.SendFinNo);// ת����־
				dto.setSdemo("�ļ��ϴ��ɹ�");
				dtos.add(dto);
			}
			DatabaseFacade.getODB().create(CommonUtil.listTArray(dtos));
		} catch (Exception e) {
			log.error("���������־ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}

	}

	public IDto find(IPK pk) throws ITFEBizException {
		try {
			return DatabaseFacade.getDb().find(pk);
		} catch (JAFDatabaseException e) {
			log.error("����find(IPK pk)ʱ�����쳣!", e);
			throw new ITFEBizException("����find(IPK pk)ʱ�����쳣!", e);
		}
	}

	public Boolean userPasswordisValid(String susercode, String sorgcode)
			throws ITFEBizException {
		TsUsersPK pk = new TsUsersPK();
		pk.setSusercode(susercode);
		pk.setSorgcode(sorgcode);
		TsUsersDto userDto = (TsUsersDto) find(pk);
		if (userDto == null || StringUtils.isBlank(userDto.getSusercode())) {
			return true;
		} else {
			java.util.Date curdate = TimeFacade.getCurrentDate();
			java.util.Date pasdate = TimeFacade.parseDate(userDto
					.getSpassmoddate(), "yyyyMMdd");
			int days = (int) ((curdate.getTime() - pasdate.getTime()) / 1000 / 60 / 60 / 24);
			if (days > userDto.getSpassmodcycle()) {
				return false;
			} else {
				return true;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService
	 * #getSecrKeyByOrg(java.lang.String, java.lang.String, java.lang.String)
	 */
	public IDto getSecrKeyByOrg(String keyMode, String sorgcode, String strecode)
			throws ITFEBizException {
		try {
			return TipsFileDecrypt
					.findKeyByKeyMode(keyMode, sorgcode, strecode);
		} catch (JAFDatabaseException e) {
			log.error("�������˸�����Կ����ģʽ��ѯ��Ӧ����Կʧ��!", e);
			throw new ITFEBizException("�������˸�����Կ����ģʽ��ѯ��Ӧ����Կʧ�ܣ�", e);
		} catch (ValidateException e) {
			log.error("�������˸�����Կ����ģʽ��ѯ��Ӧ����Կʧ��!", e);
			throw new ITFEBizException("�������˸�����Կ����ģʽ��ѯ��Ӧ����Կʧ��!", e);
		}
	}
	
	/**
	 * ����(ɽ��)
	 */
	public String decryptForIncome(String encStr, String str,TsMankeyDto _dto)
			throws ITFEBizException {
		try {
			String encKey = _dto.getSencryptkey();
			String strKey = _dto.getSkey();
			// ��ȡ�����ַ���
			String noEncStr = TripleDES.decrypt(encStr, encKey, null);
			// ��֤ǩ��������ԭ�ĺ�MD5ǩ��,int�᲻�����
			int index = noEncStr.lastIndexOf("[[[");
			// ԭ��
			String msg = noEncStr.substring(0, index);
			// ǩ����Ϣ
			String signResult = noEncStr.substring(index + 3,
					noEncStr.length() - 3);
			// ����ǩ����Ϣ����ǩ��
			String Comsign = new String(Hex.encode(MD5Sign.encryptHMAC(msg,
					strKey)));
			// �Ƚ�ǩ��
			if (Comsign.equals(signResult)) {
				return msg;
			} else {
				return "";
			}
		} catch (Exception e) {
			log.error("�ļ����ܴ���!", e);
		}
		return "";
	}
	
	//�õ�ĳϵϵͳ�����ö��ֵ
	public Object getCacheInfo(String cacheKey) throws ITFEBizException {
		HashMap<String, String> map = ITFECommonConstant.sCONSTANTCODEMap;
		return map.get(cacheKey);
	}

	public List findRsByDtoWithWhere(IDto idto, String sqlwhere)
			throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDtoForWhere(idto, sqlwhere);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣", e);
		}
	}
	public List<TsTreasuryDto> getSubTreCode(TsTreasuryDto dto) throws ITFEBizException 
	{
		List<TsTreasuryDto> trelist = null;
		try{
			if(dto.getStrecode()==null||"".equals(dto.getStrecode().trim()))
				return trelist;
			else
			{
				trelist = new ArrayList<TsTreasuryDto>();
				TsTreasuryDto finddto = new TsTreasuryDto();
				finddto.setSgoverntrecode(dto.getStrecode());
				trelist.add(dto);
				List<TsTreasuryDto> templist = CommonFacade.getODB().findRsByDto(finddto);
				if(templist!=null&&templist.size()>0)
				{
					trelist.addAll(templist);
					TsTreasuryDto tempdto = null;
					for(int i=0;i<templist.size();i++)
					{
						tempdto = templist.get(i);
						if(!tempdto.getStrecode().equals(tempdto.getSgoverntrecode()))
							trelist.addAll(getSubTreCode(tempdto));
					}
				}	
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		} catch (ValidateException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		}
		return trelist;
	}
	public List<TsTreasuryDto> getSubTreCode(String orgCode) throws ITFEBizException 
	{
		List<TsTreasuryDto> returnList = null;
		try{
			if(orgCode==null||"".equals(orgCode.trim()))
				return returnList;
			else
			{
				List<TsTreasuryDto> trelist = new ArrayList<TsTreasuryDto>();
				returnList = new ArrayList<TsTreasuryDto>();
				TsTreasuryDto finddto = new TsTreasuryDto();
				finddto.setSorgcode(orgCode);
				List<TsTreasuryDto> templist = CommonFacade.getODB().findRsByDto(finddto);
				if(templist!=null&&templist.size()>0)
				{
					trelist.addAll(templist);
					for(int i=0;i<templist.size();i++)
						trelist.addAll(getSubTreCode(templist.get(i)));
				}
				Set<String> tmpTreSet = new HashSet<String>();
				if(trelist!=null&&trelist.size()>0)
				{
					for(int i=0;i<trelist.size();i++)
					{
						if(tmpTreSet.add(trelist.get(i).getStrecode()))
							returnList.add(trelist.get(i));
					}
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		} catch (ValidateException e) {
			log.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
		}
		return returnList;
	}
	
	/**
	 * ��ȡ��������key	 
	 * @generated
	 * @param sorgcode
	 * @param skeymode
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public String getModeKey(String sorgcode, String skeymode) throws ITFEBizException{
	   String key = "";
	   TsMankeyDto _dto = new TsMankeyDto();
		_dto.setSorgcode(sorgcode);
		_dto.setSkeymode(skeymode);
		try {
			List<TsMankeyDto> l = CommonFacade.getODB().findRsByDtoWithUR(_dto);
			if(l!=null && l.size()>0){
				key = l.get(0).getSkey();
			}
		} catch (Exception e) {
			log.error("��ȡ��������key�������ݿ��쳣!", e);
			throw new ITFEBizException("��ȡ��������key�������ݿ��쳣", e);
		} 
	   return key;
   }

	public PageResponse findRsByDtoWithConditionPaging(IDto dto,
			PageRequest request, String wheresql, String sort,
			String tablename, String rptdate) throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDtoWithConditionPaging(dto,
					request, wheresql,sort,tablename,rptdate);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��ѯ�쳣", e);
		} catch (ValidateException e) {
			throw new ITFEBizException("��ѯ�쳣", e);
		}
	}

	public List selHtableBydto(IDto idto) throws ITFEBizException {
		try {
			return CommonFacade.getODB().selHtableBydto(idto);
	} catch (JAFDatabaseException e) {
		throw new ITFEBizException("��ѯ�쳣", e);
	}
	}
	
	/**
	 * ����DTO�����ѯ������ҳ���ͻ��˲�ѯʹ��
	 * 
	 * @param dto
	 * @param request
	 * @param String
	 *            wherestr where����,���ܴ�����
	 * @param String
	 *            orderbyProp ��������
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws TPPFSBizException
	 * @author hjr
	 */
	public PageResponse findRsByDtoPaging(IDto idto, PageRequest request,
			String wherestr, String orderbyProp) throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDtoPaging(idto, request, wherestr, orderbyProp);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�쳣", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�쳣", e);
		}

	}
	
	
	/**
	    * ��ȡ�������ļ�·��
	    */
		public String getServerRootPath(String clientPath, String orgCode)
				throws ITFEBizException {
			String rootPath=FileRootPathUtil.getInstance().getRoot();
			String fileName = null;

			if (clientPath == null) {
				return null;
			}

			int i = clientPath.lastIndexOf('\\');
			if (i == -1){
				//���û���ҵ���б�ߣ���ô��Ѱ��б��
				i = clientPath.lastIndexOf('/');
			}
			if (i != -1) {
				fileName = clientPath.substring(i + 1);
			}
			String srvFile = rootPath + ITFECommonConstant.FILE_UPLOAD + orgCode + "/" + DateUtil.date2String2(TimeFacade.getCurrentDate()) + "/" + fileName;

			return srvFile;
		}

	public Map getmapforkey(String mapkey) throws ITFEBizException {
		if(getMap==null)
			getMap = new HashMap<String,Map>();
		if(mapkey!=null&&mapkey.startsWith("SELECT DISTINCT(S_FILENAME) FROM"))
		{
			SQLExecutor sqlExecutor = null;
		    SQLResults rs = null;
		    List fileList = new ArrayList();
		    Map dataMap = new HashMap();
		    try {
		      sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
		      rs = sqlExecutor.runQueryCloseCon(mapkey);
		      if ((rs != null) && (rs.getRowCount() > 0))
		      {
		        for (int i = 0; i < rs.getRowCount(); i++) {
		          TvFilepackagerefDto dto = new TvFilepackagerefDto();
		          dto.setSfilename(rs.getString(i, 0));
		          fileList.add(dto);
		        }
		        dataMap.put("data",fileList);
		      }
		    }
		    catch (JAFDatabaseException e) {
		      log.debug(e);
		    }
		    return dataMap;
		}else if(mapkey!=null&&mapkey.indexOf("acctnolist")>=0)
		{
			Map<String,TsInfoconnorgaccDto> tempMap = ServiceUtil.getAcctCache(this.getLoginInfo().getSorgcode());
			if(tempMap!=null&&tempMap.size()>0)
			{
				Object object[] = tempMap.keySet().toArray();
				Map<String,TsInfoconnorgaccDto> resultMap = new HashMap<String,TsInfoconnorgaccDto>();
				TsInfoconnorgaccDto tempdto = null;
				for(int i=0;i<tempMap.size();i++)
				{
					if(String.valueOf(object[i]).indexOf(this.getLoginInfo().getSorgcode())==0)
					{
						tempdto = tempMap.get(String.valueOf(object[i]));
						resultMap.put(tempdto.getSorgcode()+tempdto.getStrecode(), tempdto);
					}
				}
				return resultMap;
			}else
				return null;
			
		}else if(mapkey!=null&&(mapkey.indexOf("getEndDateOfMonth")>=0||mapkey.indexOf("getEndDateOfYear")>=0))
		{
			String[] params = mapkey.split("-");
			Map dateMap = null;
			if(params!=null&&params.length==4)
			{
				TsConvertfinorgDto cDto=new TsConvertfinorgDto();
				SQLExecutor sqlExe = null;
				try {
					String sql = null;
					sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					if(mapkey.indexOf("getEndDateOfMonth")>=0)
					{
						cDto.setSorgcode(params[1]);		
						cDto.setStrecode(params[2]);
						List<TsConvertfinorgDto> TsConvertfinorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
						if(TsConvertfinorgDtoList!=null&&TsConvertfinorgDtoList.size()>0)
							cDto = TsConvertfinorgDtoList.get(0);
						else
							return dateMap;
//						sql="SELECT max(s_rptdate)  FROM Tr_Incomedayrpt WHERE S_FINORGCODE='"+cDto.getSfinorgcode()+"' AND s_trecode='"+cDto.getStrecode()+"' and S_RPTDATE like '"+params[3].substring(0,6)+"%'";
						sql="SELECT max(s_rptdate)  FROM (SELECT max(s_rptdate) AS s_rptdate  from Tr_Incomedayrpt WHERE S_FINORGCODE=? AND s_trecode=? and S_RPTDATE like ? union  SELECT max(s_rptdate) AS s_rptdate  from HTr_Incomedayrpt WHERE S_FINORGCODE=? AND s_trecode=? and S_RPTDATE like ?)";
						sqlExe.addParam(cDto.getSfinorgcode());
						sqlExe.addParam(cDto.getStrecode());
						sqlExe.addParam(params[3].substring(0,6)+"%");
						sqlExe.addParam(cDto.getSfinorgcode());
						sqlExe.addParam(cDto.getStrecode());
						sqlExe.addParam(params[3].substring(0,6)+"%");
					}
					else
					{
						sql="SELECT max(s_rptdate)  FROM (SELECT max(s_rptdate) AS s_rptdate  from Tr_Incomedayrpt WHERE S_RPTDATE like ? union  SELECT max(s_rptdate) AS s_rptdate  from HTr_Incomedayrpt WHERE S_RPTDATE like ?)";
						sqlExe.addParam(params[3].substring(0,4)+"%");
						sqlExe.addParam(params[3].substring(0,4)+"%");
					}
					
					SQLResults res = sqlExe.runQueryCloseCon(sql);
					if(res!=null)
					{
						dateMap = new HashMap();
						if(mapkey.indexOf("getEndDateOfMonth")>=0)
							dateMap.put("getEndDateOfMonth", res.getString(0, 0));
						else
							dateMap.put("getEndDateOfYear", res.getString(0, 0));
					}
				}catch(Exception e2 ){
					if(sqlExe!=null)
						sqlExe.closeConnection();
					return dateMap;
				}finally
				{
					if(sqlExe!=null)
						sqlExe.closeConnection();
				}
			}
			return dateMap;
		}else if(mapkey!=null&&!"".equals(mapkey))
			return getMap.get(mapkey);
		else
			return null;
	}

	public void setmapforkey(String getmapkey, String mapkey, String mapvalue)
			throws ITFEBizException {
		if(getmapkey!=null&&mapkey!=null&&mapvalue!=null&&!"".equals(getmapkey)&&!"".equals(mapkey)&&!"".equals(mapvalue))
		{
			if(getMap==null)
				getMap = new HashMap<String,Map>();
			Map keymap = getMap.get(getmapkey);
			if(keymap==null)
			{
				keymap = new HashMap<String,String>();
				keymap.put(mapkey, mapvalue);
			}else
			{
				keymap.put(mapkey, mapvalue);
			}
		}
		
	}

	/**��ȡ'�ͻ��˷���ocx�����Ƿ�ʹ�ô���'����**/
	public String getIsClientProxy() throws ITFEBizException {
		return ITFECommonConstant.OCXSERVICE_ISCLIENTPROXY;
	}

	
	
	/**
	 * ��ȡTIPS�ڵ����
	 * @author db2admin
	 * @throws ITFEBizException
	 */
	public String getSrcNode() throws ITFEBizException {
		return ITFECommonConstant.SRC_NODE;
	}

	public void createdtos(List dtoList) throws ITFEBizException {
		if(dtoList==null||dtoList.size()<=0)
			return;
		try
		{
			DatabaseFacade.getODB().create(CommonUtil.listTArray(dtoList));
		} catch (Exception e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
	}

	public void updateDtos(List updateDtos) throws ITFEBizException {
		if(updateDtos==null||updateDtos.size()<=0)
			return;
		try
		{
			DatabaseFacade.getODB().update(CommonUtil.listTArray(updateDtos));
			if(updateDtos.get(0).getClass().getName().equals("com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto"))
			{
				SQLExecutor execDetail = null;
				try{
					execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();;
					String sql = "update TV_INCOMEONLINE_INCOMEONDETAIL_BLEND_XXX set S_ETPCODE=?,S_ETPNAME=?, S_BDGSBTNAME=?, S_REMARK1=?,S_REMARK2=?,S_REMARK5=?,S_REMARK4=? where S_SEQ=?";
					TvIncomeonlineIncomeondetailBlendDto tmpdto = null;
					for(Object temp:updateDtos)
					{
						tmpdto = (TvIncomeonlineIncomeondetailBlendDto)temp;
						sql = sql.replace("XXX", tmpdto.getSintredate().substring(0,4));
						execDetail.clearParams();
						if(tmpdto.getSetpcode()==null)
							tmpdto.setSetpcode("-");
						if(tmpdto.getSetpname()==null)
							tmpdto.setSetpname("-");
						if(tmpdto.getSbdgsbtname()==null)
							tmpdto.setSbdgsbtname("-");
						if(tmpdto.getSremark1()==null)
							tmpdto.setSremark1("-");
						if(tmpdto.getSremark2()==null)
							tmpdto.setSremark2("-");
						if(tmpdto.getSremark5()==null)
							tmpdto.setSremark5("-");
						if(tmpdto.getSremark4()==null)
							tmpdto.setSremark4("-");
						execDetail.addParam(tmpdto.getSetpcode());
						execDetail.addParam(tmpdto.getSetpname());
						execDetail.addParam(tmpdto.getSbdgsbtname());
						execDetail.addParam(tmpdto.getSremark1());
						execDetail.addParam(tmpdto.getSremark2());
						execDetail.addParam(tmpdto.getSremark5());
						execDetail.addParam(tmpdto.getSremark4());
						execDetail.addParam(tmpdto.getSseq());
						execDetail.runQuery(sql);
					}
					execDetail.closeConnection();
				}catch(Exception e)
				{
					log.error(e);
				}finally
				{
					if(execDetail!=null)
						execDetail.closeConnection();
				}
			}
		} catch (Exception e) {
			log.error("��������ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
		
	}
}