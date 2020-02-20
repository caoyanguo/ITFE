package com.cfcc.itfe.facade;

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

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConnectionDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsSysbatchDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 通用业务信息访问
 * 
 */
public class BusinessFacade {
	private static Log log = LogFactory.getLog(BusinessFacade.class);

	/**
	 * 支付系统参与者信息
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsPaybankDto> findTsPayBankInfo()
			throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsPaybankDto> map = new HashMap<String, TsPaybankDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsPaybankDto.class);
			String sql = "  with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsPaybankDto dto = (TsPaybankDto) iter.next();
					map.put(dto.getSbankno(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查询支付系统行名行号出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 缓存库款账户表信息
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsInfoconnorgaccDto> findTsFinTreAcctInfo()
			throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsInfoconnorgaccDto> map = new HashMap<String, TsInfoconnorgaccDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsInfoconnorgaccDto.class);
			String sql = "  with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsInfoconnorgaccDto dto = (TsInfoconnorgaccDto) iter.next();
					map.put(dto.getSorgcode() + dto.getSpayeraccount(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查询财政库款账户出错！";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 缓存库款账户信息
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TdBookacctMainDto> findAllFinTreAcctFromBookAcctInfo()
			throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TdBookacctMainDto> map = new HashMap<String, TdBookacctMainDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TdBookacctMainDto.class);
			String sql = "  with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TdBookacctMainDto dto = (TdBookacctMainDto) iter.next();
					if ("1".equals(dto.getCacctprop())) {
						map
								.put(
										dto.getSbookorgcode()
												+ dto.getSbookacct(), dto);
					}

				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查询财政库款账户出错！";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 核算主体代码表
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsOrganDto> findTsOrganInfo()
			throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsOrganDto> map = new HashMap<String, TsOrganDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsOrganDto.class);
			String sql = "  with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsOrganDto dto = (TsOrganDto) iter.next();
					map.put(dto.getSpaybankno(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查询核算主体代码表出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}
	/**
	 * 核算主体代码表
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsOrganDto> organInfo()
			throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsOrganDto> map = new HashMap<String, TsOrganDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsOrganDto.class);
			String sql = "  with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsOrganDto dto = (TsOrganDto) iter.next();
					map.put(dto.getSorgcode(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查询核算主体代码表出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}
	/**
	 * 查询预算科目信息
	 * 
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public static HashMap<String, HashMap<String, TsBudgetsubjectDto>> findTsBdgsbtInfo(
			String bookorgcode) throws ITFEBizException, JAFDatabaseException {
		HashMap<String, HashMap<String, TsBudgetsubjectDto>> mapmap = new HashMap<String, HashMap<String, TsBudgetsubjectDto>>();
		List<TsOrganDto> list = getbookorg();
		HashMap<String, TsBudgetsubjectDto> map = null;
		for (TsOrganDto _dto : list) {
			String sorgcode = _dto.getSorgcode();
			if("000000000000".equals(sorgcode))
				continue;
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",Budgetsubject=public,")>=0&&sorgcode!=null&&sorgcode.length()>2)
				sorgcode = sorgcode.substring(0,2)+"0000000002";
			else
				map = null;
			BatchRetriever retriever = null;
			int count = 0;
			
			try {
				if(map==null)
				{
					map = new HashMap<String, TsBudgetsubjectDto>();
					retriever = DatabaseFacade.getODB().getBatchRetriever(
							TsBudgetsubjectDto.class);
					String sql = null;
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",Budgetsubject=public,")>=0)
						sql = " where (s_orgcode='"+(sorgcode.substring(0,2)+"0000000003'")+" or s_orgcode = ?) and  S_WRITEFLAG = '1' with ur";
					else
						sql = " where s_orgcode = ? and  S_WRITEFLAG = '1' with ur";
					List<Object> params = new ArrayList<Object>();
					params.add(sorgcode);
					retriever.runQuery(sql, params);
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",Budgetsubject=public,")<0)
						retriever.setMaxRows(10000);// 每次从数据库中取出10000笔记录
					while (retriever.hasMore()) {
						List dtos = retriever.getResults();
						count = count + dtos.size();
						for (Iterator iter = dtos.iterator(); iter.hasNext();) {
							TsBudgetsubjectDto dto = (TsBudgetsubjectDto) iter
									.next();
							map.put(dto.getSsubjectcode(), dto);
						}
					}
				}
				mapmap.put(_dto.getSorgcode(), map);
			} catch (JAFDatabaseException e) {
				String error = "查找预算科目信息出错";
				log.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (retriever != null) {
					try {
						retriever.clearConnecton();
					} catch (Exception e) {
						log.error("关闭连接出错", e);
					}
				}
			}
		}
		return mapmap;
	}

	/**
	 * 查询预算单位信息
	 * 
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public static HashMap<String, HashMap<String, TdCorpDto>> findTdCorp(
			String sbookorgcode) throws ITFEBizException, JAFDatabaseException {
		HashMap<String, HashMap<String, TdCorpDto>> mapmap = new HashMap<String, HashMap<String, TdCorpDto>>();
		List<TsOrganDto> list = getbookorg();
		for (TsOrganDto _dto : list) {
			String sorgcode = _dto.getSorgcode();
			BatchRetriever retriever = null;
			int count = 0;
			HashMap<String, TdCorpDto> map = new HashMap<String, TdCorpDto>();
			try {
				retriever = DatabaseFacade.getODB().getBatchRetriever(
						TdCorpDto.class);
				String sql = " where s_bookorgcode = ? with ur";
				List<Object> params = new ArrayList<Object>();
				params.add(sorgcode);
				retriever.runQuery(sql, params);
				// 每次从数据库中取出1000笔记录
				retriever.setMaxRows(10000);
				while (retriever.hasMore()) {
					List dtos = retriever.getResults();
					count = count + dtos.size();
					for (Iterator iter = dtos.iterator(); iter.hasNext();) {
						TdCorpDto dto = (TdCorpDto) iter.next();
						map.put(dto.getStrecode() + dto.getScorpcode(), dto);
					}
				}

				mapmap.put(sorgcode, map);
			} catch (JAFDatabaseException e) {
				String error = "查找预算单位信息出错";
				log.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (retriever != null) {
					try {
						retriever.clearConnecton();
					} catch (Exception e) {
						log.error("关闭连接出错", e);
					}
				}
			}
		}
		return mapmap;
	}

	/**
	 * 查询国库代码
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsTreasuryDto> findTreasuryInfo(
			String sbookorgcode) throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsTreasuryDto> map = new HashMap<String, TsTreasuryDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsTreasuryDto.class);
			String sql = " where 1=1 with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsTreasuryDto dto = (TsTreasuryDto) iter.next();
					map.put(dto.getStrecode(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查找国库参数信息出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 查询财政机构代码
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertfinorgDto> findFincInfo(
			String sbookorgcode) throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsConvertfinorgDto> map = new HashMap<String, TsConvertfinorgDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsConvertfinorgDto.class);
			String sql = " where 1=1 with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsConvertfinorgDto dto = (TsConvertfinorgDto) iter.next();
					map.put(dto.getStrecode(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查找财政机关信息参数信息出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 查询银行代码与行号的对应关系
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsGenbankandreckbankDto> findGenBankInfo(
			String sbookorgcode) throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsGenbankandreckbankDto> map = new HashMap<String, TsGenbankandreckbankDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsGenbankandreckbankDto.class);
			String sql = " where 1=1 with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsGenbankandreckbankDto dto = (TsGenbankandreckbankDto) iter.next();
					if(dto.getStrecode()!=null&&!"".equals(dto.getStrecode().trim()))
						map.put(dto.getSbookorgcode()+dto.getStrecode()+dto.getSgenbankcode(), dto);
					else
						map.put(dto.getSbookorgcode()+dto.getSgenbankcode(), dto);

				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查找银行对照关系代码表信息参数信息出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 查询财政机构代码
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertfinorgDto> findFincInfoByFinc(
			String sbookorgcode) throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsConvertfinorgDto> map = new HashMap<String, TsConvertfinorgDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsConvertfinorgDto.class);
			String sql = " where 1=1 with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsConvertfinorgDto dto = (TsConvertfinorgDto) iter.next();
					map.put(dto.getSfinorgcode(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查找财政机关信息参数信息出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 查询征收机关对照表代码
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConverttaxorgDto> findTaxInfo(
			String sbookorgcode) throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsConverttaxorgDto> map = new HashMap<String, TsConverttaxorgDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsConverttaxorgDto.class);
			String sql = " where 1=1 with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsConverttaxorgDto dto = (TsConverttaxorgDto) iter.next();
					map.put(dto.getStrecode() + dto.getStbstaxorgcode(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查找财政机关信息参数信息出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}

	/**
	 * 获取核算主体代码
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static List<TsOrganDto> getbookorg() throws JAFDatabaseException {
		List<TsOrganDto> list = DatabaseFacade.getODB().find(TsOrganDto.class);
		return list;
	}

	/**
	 * 获取收付款人信息
	 * @throws ITFEBizException 
	 * 
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public static List<TsPayacctinfoDto> findPayacctInfos(String sorgcode) throws ITFEBizException {
		try {
			TsPayacctinfoDto tsPayacctinfoDto = new TsPayacctinfoDto();
			if (!StringUtils.isBlank(sorgcode)) {
				tsPayacctinfoDto.setSorgcode(sorgcode);
				return (List<TsPayacctinfoDto>) CommonFacade.getODB()
						.findRsByDto(tsPayacctinfoDto);
			} else {
				return DatabaseFacade.getODB().find(TsPayacctinfoDto.class);
			}
		} catch (JAFDatabaseException e) {
			log.error("查找收付款人信息出错", e);
			throw new ITFEBizException("查找收付款人信息出错", e);
		} catch (ValidateException e) {
			log.error("查找收付款人信息出错", e);
			throw new ITFEBizException("查找收付款人信息出错", e);
		}
	}
	
	
	/**
	 * 返回凭证自动提交控制参数
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsVouchercommitautoDto> findTsVoucherAutoCommit()
			throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsVouchercommitautoDto> map = new HashMap<String, TsVouchercommitautoDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsVouchercommitautoDto.class);
			String sql = "  with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsVouchercommitautoDto dto = (TsVouchercommitautoDto) iter.next();
					map.put(dto.getStrecode(), dto);
				}
			}
			return map;

		} catch (JAFDatabaseException e) {
			String error = "查询凭证自动提交控制参数出错！";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}
	
	
	/**
	 * 取得行名行号信息
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public static HashMap<String, TsConvertbanknameDto> getBankInfo(String bookorgCode) throws JAFDatabaseException, ValidateException {
		HashMap<String, TsConvertbanknameDto> acctmap = new HashMap<String, TsConvertbanknameDto>();
		TsConvertbanknameDto convertbanknamedto = new TsConvertbanknameDto();
		convertbanknamedto.setSorgcode(bookorgCode);
		List<TsConvertbanknameDto> itoList = CommonFacade.getODB().findRsByDto(
				convertbanknamedto);
		if (null != itoList && itoList.size() > 0) {
			for (TsConvertbanknameDto obj : itoList) {
				acctmap.put(obj.getSbankname(), obj);
			}
		}
		return acctmap;
	}
	
	/**
	 * 获取凭证自动控制参数 
	 * 
	 * @return
	 * @throws JAFDatabaseException 
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, List> findTsVoucherAuto(String column) throws JAFDatabaseException {
		HashMap<String, List> acctmap = new HashMap<String, List>();
		SQLExecutor sqlExecutor=null;
		String S_ORGCODE="";
		String S_FINORGCODE="";
		String S_ADMDIVCODE="";
		String S_VTCODE="";
		sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
		String sql="SELECT c.S_ORGCODE,c.S_FINORGCODE,c.S_ADMDIVCODE,v.S_VTCODE ,c.S_TRECODE FROM TS_CONVERTFINORG c JOIN TS_VOUCHERCOMMITAUTO v ON c.S_TRECODE=v.S_TRECODE WHERE v.";
		sql=sql+column+"  = ? ";
		sql = sql + " ORDER BY  S_ORGCODE , S_VTCODE DESC";
		sqlExecutor.clearParams();
		sqlExecutor.addParam(DealCodeConstants.VOUCHER_AUTO);
		SQLResults rs = sqlExecutor.runQueryCloseCon(sql);
		List list=new ArrayList();
		List<TsOrganDto> organList = getbookorg();
		Map<String,TsOrganDto> orgMap = new HashMap<String,TsOrganDto>();
		String key = "itfe@icfcc.com";
		Md5App des =  new Md5App();
		if(organList!=null&&organList.size()>0)
		{
			String miyao = null;
			List<IDto> uporganlist = new ArrayList<IDto>();
			for(TsOrganDto tempdto:organList)
			{
				
				miyao = des.makeMd5(tempdto.getSorgcode()+key);
				if(ITFECommonConstant.PUBLICPARAM.contains(",orgcodeautoverify=fales,"))
					orgMap.put(tempdto.getSorgcode(), tempdto);
				else
				{
					if(ITFECommonConstant.CLIENTEDITION!=null&&ITFECommonConstant.CLIENTEDITION.contains("cs"))
					{
						if(tempdto.getSofcityorgcode()!=null&&"1".equals(tempdto.getSofcityorgcode()))
						{
							orgMap.put(tempdto.getSorgcode(), tempdto);
							tempdto.setSofcityorgcode((miyao!=null&&miyao.length()>=12)?miyao.substring(miyao.length()-12):null);
							uporganlist.add(tempdto);
						}
					}
					if(miyao!=null&&miyao.length()>=12&&miyao.substring(miyao.length()-12).equals(tempdto.getSofcityorgcode()))
						orgMap.put(tempdto.getSorgcode(), tempdto);
				}
			}
			if(uporganlist!=null&&uporganlist.size()>0)
				DatabaseFacade.getDb().update(uporganlist.toArray(new IDto[uporganlist.size()]));
		}
		Set<String> tmpTreSet = new HashSet<String>(); 
		if(rs!=null&&rs.getRowCount()>0){
			for(int i=0;i<rs.getRowCount();i++){
				List autoList=new ArrayList();
				S_ORGCODE=rs.getString(i, 0);
				S_FINORGCODE=rs.getString(i, 1);
				S_ADMDIVCODE=rs.getString(i, 2);
				S_VTCODE=rs.getString(i, 3);
				if(S_ADMDIVCODE==null||S_ADMDIVCODE.equals("")){
					log.error("财政代码: "+S_FINORGCODE+" 对应的区划代码 参数未维护！");
					VoucherException.saveErrInfo(S_VTCODE,"财政代码: "+S_FINORGCODE+" 对应的区划代码 参数未维护！");
					continue;
				}
				if(orgMap.get(S_ORGCODE)!=null)
				{
					autoList.add(S_ORGCODE);
					autoList.add(S_FINORGCODE);
					autoList.add(S_ADMDIVCODE);
					autoList.add(S_VTCODE);
					autoList.add(rs.getString(i, 4));//添加国库代码
					list.add(autoList);
				}else if(tmpTreSet.add(S_ORGCODE))
				{
					log.error("核算主体代码: "+"核算主体代码: "+S_ORGCODE+"没有权限处理无纸化业务！"+des.makeMd5(S_ORGCODE+key));
					VoucherException.saveErrInfo(S_ORGCODE,"核算主体代码: "+S_ORGCODE+"没有权限处理无纸化业务！");
					continue;
				}
			}
			acctmap.put("VOUCHER", list);				
		}	
		if(sqlExecutor!=null){
			sqlExecutor.closeConnection();
		}
		return acctmap;
	}
	
	/**
	 * 查询银行代码与行别对应关系
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertbanktypeDto> findTsconvertBankType(
			String sbookorgcode) throws ITFEBizException {
		BatchRetriever retriever = null;
		int count = 0;
		HashMap<String, TsConvertbanktypeDto> map = new HashMap<String, TsConvertbanktypeDto>();
		try {
			retriever = DatabaseFacade.getODB().getBatchRetriever(
					TsConvertbanktypeDto.class);
			String sql = " where 1=1 with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsConvertbanktypeDto dto = (TsConvertbanktypeDto) iter.next();
					map.put(dto.getStrecode()+dto.getSbankcode(), dto);
				}
			}
			return map;
		} catch (JAFDatabaseException e) {
			String error = "查询银行代码与行别对应关系参数信息出错";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (retriever != null) {
				try {
					retriever.clearConnecton();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}
	/**
	 * 查询收入退付原因
	 * @param sbookorgcode
	 * @return
	 */
	public static HashMap<String, TsDwbkReasonDto> findTsDwbkReason(
			String sbookorgcode) throws JAFDatabaseException, ValidateException {
		HashMap<String, TsDwbkReasonDto> acctmap = new HashMap<String, TsDwbkReasonDto>();
		TsDwbkReasonDto dwbkReasonDto = new TsDwbkReasonDto();
		dwbkReasonDto.setSorgcode(sbookorgcode);
		List<TsDwbkReasonDto> itoList = CommonFacade.getODB().findRsByDto(
				dwbkReasonDto);
		if (null != itoList && itoList.size() > 0) {
			for (TsDwbkReasonDto obj : itoList) {
				acctmap.put(obj.getSdrawbackreacmt(), obj);
			}
		}
		return acctmap;
	}
	/**
	 * 判断财政报文是否转发到TIPS
	 * 用于上线无纸化的国库，不能再通过此渠道转发。
	 * @param trecode 国库代码
	 * @param biztype 业务类型
	 * @return true 转发tips  
	 * 		   false 不转发tips
	 * @throws ITFEBizException 
	 */
	public static boolean isRelayMsg(String trecode, String biztype) throws ITFEBizException{
		SQLExecutor sqlExecutor = null;
		String pzkbiztype = transferBiztype(biztype);
		try {
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql = "SELECT * FROM TS_VOUCHERCOMMITAUTO WHERE S_TRECODE = ? AND S_VTCODE= ? AND S_READAUTO = ?";
			sqlExecutor.addParam(trecode);
			sqlExecutor.addParam(pzkbiztype);
			sqlExecutor.addParam(StateConstant.COMMON_NO);//自动读取
			SQLResults result = sqlExecutor.runQueryCloseCon(sql);
			if(result.getRowCount() > 0){
				return false;
			}else{
				return true;
			}
		} catch (JAFDatabaseException e) {
			String error = "查询电子凭证参数表出错！";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		}finally{
			if (sqlExecutor  != null){
				sqlExecutor.closeConnection();
			}
		}
	}
	
	/**
	 * 根据TIPS业务类型转换为凭证库业务类型
	 * @return
	 */
	public static String transferBiztype(String tipsBizType){
		if(MsgConstant.MSG_NO_5101.equals(tipsBizType)){
			return MsgConstant.VOUCHER_NO_5207;
		}else if(MsgConstant.MSG_NO_5102.equals(tipsBizType)){
			return MsgConstant.VOUCHER_NO_5108;
		}else if(MsgConstant.MSG_NO_5103.equals(tipsBizType)){
			return MsgConstant.VOUCHER_NO_5106;
		}else if(MsgConstant.MSG_NO_2201.equals(tipsBizType)){
			return MsgConstant.VOUCHER_NO_2301;
		}else if(MsgConstant.MSG_NO_2202.equals(tipsBizType)){
			return MsgConstant.VOUCHER_NO_2302;
		}else if(MsgConstant.MSG_NO_1104.equals(tipsBizType)){
			return MsgConstant.VOUCHER_NO_5209;
		}
		return tipsBizType;
	}
	
	/**
	 * 缓存生僻字用于校验
	 */
	public static HashMap<String, String> findNotUsableChinese() throws ITFEBizException {
		BatchRetriever retriever = null;
		try {
			int count = 0;
			HashMap<String, String> map = new HashMap<String, String>();
			retriever = DatabaseFacade.getODB().getBatchRetriever(TsConnectionDto.class);
			String sql = " where 1=1 with ur";
			List<Object> params = new ArrayList<Object>();
			retriever.runQuery(sql, params);
			// 每次从数据库中取出1000笔记录
			retriever.setMaxRows(10000);
			while (retriever.hasMore()) {
				List dtos = retriever.getResults();
				count = count + dtos.size();
				for (Iterator iter = dtos.iterator(); iter.hasNext();) {
					TsConnectionDto dto = (TsConnectionDto) iter.next();
					map.put(dto.getSconnorgcodea(), dto.getSconnorgcodeb());
				}
			}
			return map;
		} catch (JAFDatabaseException e) {
			log.error("查找生僻字信息出错", e);
			throw new ITFEBizException("查找生僻字信息出错", e);
		} finally {
			releaseConnection(retriever, null);
		}
	}
	
	/**
	 * 用于在通过WebService向财政传输数据之前校验数据完整性
	 * @param finOrgcode
	 * @param reportDate
	 * @param bizType
	 * @return 
	 * @throws ITFEBizException 
	 */
	public static String[] checkIfComplete(String finOrgcode,String reportDate ,String bizType) throws ITFEBizException {
		SQLExecutor sqlExecutor = null;
		try {
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql = "SELECT DISTINCT S_SEQ,S_PAYEEBANKNO FROM TV_RECVLOG WHERE S_BILLORG = ? AND S_DATE= ? AND S_OPERATIONTYPECODE = ?";
			sqlExecutor.addParam(finOrgcode);
			sqlExecutor.addParam(reportDate);
			sqlExecutor.addParam(bizType);
			SQLResults result = sqlExecutor.runQueryCloseCon(sql);
			int rowCount = result.getRowCount();
			if(rowCount >0 && ((null != result.getString(0, 1) && rowCount == Integer.valueOf(result.getString(0, 1))) || MsgConstant.MSG_NO_3128.equals(bizType))) {
				String[] msgidArray = new String[rowCount];
				for(int i = 0 ; i < rowCount ; i ++) {
					msgidArray[i] = result.getString(i, 0);
				}
				return msgidArray;
			}
		} catch (JAFDatabaseException e) {
			String error = "校验数据完整性异常(WebService)！";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		}finally{
			if (sqlExecutor  != null){
				sqlExecutor.closeConnection();
			}
		}
		return null;
	}
	
	/**
	 * 根据boo来判断是检查是否已经发送财政数据还是要保存发送'财政数据发送日志'
	 * COMMON_NO_0--检查 COMMON_YES_1--保存
	 * @param finOrgcode
	 * @param reportDate
	 * @param bizType
	 * @param boo
	 * @throws ITFEBizException
	 */
	public static boolean checkAndSaveRecvlog(String finOrgcode,String reportDate ,String bizType,String boo) throws ITFEBizException {
		TsSysbatchDto batchDto = new TsSysbatchDto();
		batchDto.setSorgcode(finOrgcode);
		batchDto.setSdate(reportDate);
		batchDto.setSoperationtypecode(bizType);
		try {
			if(StateConstant.COMMON_NO.equals(boo)) {
				List l = CommonFacade.getODB().findRsByDtoWithUR(batchDto);
				if(null != l && l.size() > 0) {
					return true;
				}
				return false;
			}else if (StateConstant.COMMON_YES.equals(boo)) {
				DatabaseFacade.getODB().create(batchDto);
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询Ts_sysbatch数据异常(WebService)",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("查询Ts_sysbatch数据异常(WebService)",e);
		}
		return false;
		
	}
	
	
	
	/**
	 * 判断财政报文是否转发到TIPS
	 * 用于上线无纸化的国库，代理行，不能再通过此渠道转发。
	 * @param orgcode 核算主体
	 * @param trecode 国库代码
	 * @param biztype 业务类型
	 * @param bankcode 代理行号
	 * @return String  info
	 * @throws ITFEBizException 
	 */
	public static String isRelayMsgToTips(String orgcode,String trecode, String biztype,String bankcode) throws ITFEBizException{
		String flag="";
		SQLExecutor sqlExecutor = null;
		String pzkbiztype = transferBiztype(biztype);
		try {
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql = "SELECT S_ISNOPAPER FROM TS_ISNOPAPER WHERE S_ORGCODE = ? AND S_TRECODE = ? AND S_BANKCODE = ? AND S_VTCODE= ? ";
			sqlExecutor.addParam(orgcode);
			sqlExecutor.addParam(trecode);
			sqlExecutor.addParam(bankcode);
			sqlExecutor.addParam(pzkbiztype);
			SQLResults result = sqlExecutor.runQueryCloseCon(sql);
			if(result.getRowCount() > 0){
				String isrelay=result.getString(0, 0);
				if(isrelay.equals(StateConstant.MSG_NORELAY_TIPS)){
					flag = MsgConstant.LOG_ADDWORD_RUN_NOPAPER;
				}
			}
		} catch (JAFDatabaseException e) {
			String error = "查询商行无纸化业务参数出错！";
			log.error(error, e);
			throw new ITFEBizException(error, e);
		}finally{
			if (sqlExecutor  != null){
				sqlExecutor.closeConnection();
			}
		}
		return flag;
	}

	/**释放数据库资源*/
	private static void releaseConnection(BatchRetriever retriever, SQLExecutor executor){
		if (retriever != null) {
			try {
				retriever.clearConnecton();
			} catch (Exception e) {
				log.error("关闭连接出错", e);
			}
			retriever = null;
		}
		
		if (executor != null) {
			try {
				executor.closeConnection();
			} catch (Exception e) {
				log.error("关闭连接出错", e);
			}
			executor = null;
		}
	}
}
