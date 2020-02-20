package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface ICommonDataAccessService extends IService {



	/**
	 * findAllDtos
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findAllDtos(Class dto) throws ITFEBizException;

	/**
	 * findAllDtosUR
	 	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findAllDtosUR(Class dto) throws ITFEBizException;

	/**
	 * findRsByDto
	 	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDto(IDto idto) throws ITFEBizException;

	/**
	 * findRsByDtoUR
	 	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDtoUR(IDto idto) throws ITFEBizException;

	/**
	 * findRsByDtoWithBookOrgUR
	 	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDtoWithBookOrgUR(IDto idto) throws ITFEBizException;

	/**
	 * findRsByDtoWithBookOrg
	 	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDtoWithBookOrg(IDto idto) throws ITFEBizException;

	/**
	 * 生成Sequence序号	 
	 * @generated
	 * @param seqname
	 * @return java.lang.Long
	 * @throws ITFEBizException	 
	 */
   public abstract Long getSequenceNo(String seqname) throws ITFEBizException;

	/**
	 * 更新数据	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void updateData(IDto dto) throws ITFEBizException;

	/**
	 * 带条件的分页查询	 
	 * @generated
	 * @param dto
	 * @param request
	 * @param wheresql
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findRsByDtoWithWherePaging(IDto dto, PageRequest request, String wheresql) throws ITFEBizException;

	/**
	 * 获得服务器时间	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getSysDate() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param orgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findOrgList(String orgcode) throws ITFEBizException;

	/**
	 * 获得接收或者发送日志中所有日期的列表	 
	 * @generated
	 * @param strTag
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findLogDateList(String strTag) throws ITFEBizException;

	/**
	 * 获得系统数据库时间	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getSysDBDate() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param orgcode
	 * @return java.util.HashMap
	 * @throws ITFEBizException	 
	 */
   public abstract HashMap getMiYao(String orgcode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @param flag
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDtocheckUR(IDto idto, String flag) throws ITFEBizException;

	/**
	 * 带条件的分页查询	 
	 * @generated
	 * @param dto
	 * @param request
	 * @param wheresql
	 * @param sort
	 * @param tablename
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findRsByDtoWithWherePaging(IDto dto, PageRequest request, String wheresql, String sort, String tablename) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param filename
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean verifyImportRepeat(String filename) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param biztype
	 * @param orgcode
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean judgeDirectSubmit(String biztype, String orgcode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param pathlist
	 * @param biztype
	 * @throws ITFEBizException	 
	 */
   public abstract void saveTvrecvlog(List pathlist, String biztype) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param str
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String decrypt(String str) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param bankcode
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean auditBankCode(String bankcode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @return java.sql.Date
	 * @throws ITFEBizException	 
	 */
   public abstract Date getAdjustDate() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param filelist
	 * @throws ITFEBizException	 
	 */
   public abstract void delServerWrongFile(List filelist) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @param orderstr
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDto(IDto idto, String orderstr) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @param sort
	 * @param tablename
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDto(IDto idto, String sort, String tablename) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param maps
	 * @param biztype
	 * @throws ITFEBizException	 
	 */
   public abstract void saveMassTvrecvlog(Map maps, String biztype) throws ITFEBizException;

	/**
	 * 通过主键查找一条记录	 
	 * @generated
	 * @param pk
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto find(IPK pk) throws ITFEBizException;

	/**
	 * 判断用户密码是否失效	 
	 * @generated
	 * @param susercode
	 * @param sorgcode
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean userPasswordisValid(String susercode, String sorgcode) throws ITFEBizException;

	/**
	 * 按机构获取服务器端密钥	 
	 * @generated
	 * @param keyMode
	 * @param sorgcode
	 * @param strecode
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
   public abstract IDto getSecrKeyByOrg(String keyMode, String sorgcode, String strecode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param biztype
	 * @param orgcode
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
   public abstract Boolean judgeBatchConfirm(String biztype, String orgcode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param desStr
	 * @param str
	 * @param keydto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String decryptForIncome(String desStr, String str, TsMankeyDto keydto) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param cacheKey
	 * @return java.lang.Object
	 * @throws ITFEBizException	 
	 */
   public abstract Object getCacheInfo(String cacheKey) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param orgCode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getSubTreCode(String orgCode) throws ITFEBizException;

	/**
	 * 获取核算主体key	 
	 * @generated
	 * @param sorgcode
	 * @param skeymode
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getModeKey(String sorgcode, String skeymode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List selHtableBydto(IDto idto) throws ITFEBizException;

	/**
	 * 带条件的分页查询	 
	 * @generated
	 * @param dto
	 * @param request
	 * @param wheresql
	 * @param sort
	 * @param tablename
	 * @param rptdate
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findRsByDtoWithConditionPaging(IDto dto, PageRequest request, String wheresql, String sort, String tablename, String rptdate) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void delete(IDto dto) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void create(IDto dto) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @param request
	 * @param wherestr
	 * @param orderbyProp
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findRsByDtoPaging(IDto idto, PageRequest request, String wherestr, String orderbyProp) throws ITFEBizException;

	/**
	 * 获取加载文件的服务器根路径	 
	 * @generated
	 * @param clientPath
	 * @param orgCode
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getServerRootPath(String clientPath, String orgCode) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param mapkey
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map getmapforkey(String mapkey) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param getmapkey
	 * @param mapkey
	 * @param mapvalue
	 * @throws ITFEBizException	 
	 */
   public abstract void setmapforkey(String getmapkey, String mapkey, String mapvalue) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getFileRootPath() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param idto
	 * @param wheresql
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findRsByDtoWithWhere(IDto idto, String wheresql) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getIsClientProxy() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getSrcNode() throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoList
	 * @throws ITFEBizException	 
	 */
   public abstract void createdtos(List dtoList) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param updateDtos
	 * @throws ITFEBizException	 
	 */
   public abstract void updateDtos(List updateDtos) throws ITFEBizException;

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getSubTreCode(TsTreasuryDto dto) throws ITFEBizException;

}