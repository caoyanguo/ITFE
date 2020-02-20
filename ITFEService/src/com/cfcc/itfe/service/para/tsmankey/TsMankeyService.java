package com.cfcc.itfe.service.para.tsmankey;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.datamove.DataMoveUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutorFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * @time 12-06-25 16:34:52 codecomment:
 */

public class TsMankeyService extends AbstractTsMankeyService {
	private static Log log = LogFactory.getLog(TsMankeyService.class);

	/**
	 * 密钥参数列表
	 * 
	 * @generated
	 * @param dto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse keyList(IDto dto, PageRequest pageRequest)
			throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDtoPaging(dto, pageRequest);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("分页查询出错", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("分页查询出错", e);
		}
	}

	/**
	 * 密钥参数删除
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void keyDelete(IDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().delete(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
	}

	/**
	 * 密钥参数录入
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void keySave(IDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段密钥维护模式，密钥机构代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}

	}

	/**
	 * 密钥参数修改
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void keyModify(IDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段密钥维护模式，密钥机构代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}

	}

	public List autoGetBillOrg() throws ITFEBizException {

		try {
			List<IDto> crelist = new ArrayList<IDto>();
			// 获取已有的机构信息
			TsMankeyDto keydto = new TsMankeyDto();
			keydto.setSorgcode(getLoginInfo().getSorgcode());
			List<TsMankeyDto> keylist = CommonFacade.getODB().findRsByDto(
					keydto);
			HashMap<String, TsMankeyDto> map = new HashMap<String, TsMankeyDto>();
			if (null != keylist && keylist.size() > 0) {
				for (TsMankeyDto alldto : keylist) {
					map.put(alldto.getSkeyorgcode(), alldto);
				}
			}

			// 自动获取财政代码
			TsConvertfinorgDto _dto = new TsConvertfinorgDto();
			_dto.setSorgcode(getLoginInfo().getSorgcode());
			List<TsConvertfinorgDto> list = CommonFacade.getODB().findRsByDto(
					_dto);
			if (null != list && list.size() > 0) {
				for (TsConvertfinorgDto tmp : list) {// 生成密钥
					if (!map.containsKey(tmp.getSfinorgcode())) {
						TsMankeyDto creDto = new TsMankeyDto();
						creDto.setSorgcode(getLoginInfo().getSorgcode());
						creDto.setSkeymode(StateConstant.KEY_BILLORG);
						creDto.setSkeyorgcode(tmp.getSfinorgcode());
						creDto.setSkey("no");
						creDto.setSencryptkey("no");
						creDto.setImodicount(0);
						crelist.add(creDto);
					} else if (map.containsKey(tmp.getSfinorgcode())) {
						crelist.add(map.get(tmp.getSfinorgcode()));
					}
				}
			}
			// 自动获取征收机关代码
			String querySql="SELECT DISTINCT(S_TCBSTAXORGCODE) FROM TS_CONVERTTAXORG WHERE S_ORGCODE= ?  AND S_TCBSTAXORGCODE " +
					" not in (select S_FINORGCODE from TS_CONVERTFINORG where s_orgcode = ? )  " ;
			SQLExecutor sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlexec.addParam(getLoginInfo().getSorgcode());
			sqlexec.addParam(getLoginInfo().getSorgcode());
			SQLResults sqlResult=sqlexec.runQueryCloseCon(querySql,TsConverttaxorgDto.class);
			List<TsConverttaxorgDto> tcbstaxorgcodelist=new ArrayList<TsConverttaxorgDto>();
			if(sqlResult!=null&&sqlResult.getRowCount()>0){
				tcbstaxorgcodelist=(List<TsConverttaxorgDto>) sqlResult.getDtoCollection();
			}
			if (null != tcbstaxorgcodelist && tcbstaxorgcodelist.size() > 0) {
				for (TsConverttaxorgDto tmptsct : tcbstaxorgcodelist) {// 生成密钥
					String tcbstaxorgcode=tmptsct.getStcbstaxorgcode();
					if (!map.containsKey(tcbstaxorgcode)){
						TsMankeyDto creDto = new TsMankeyDto();
						creDto.setSorgcode(getLoginInfo().getSorgcode());
						creDto.setSkeymode(StateConstant.KEY_TAXORG);
						creDto.setSkeyorgcode(tcbstaxorgcode);
						creDto.setSkey("no");
						creDto.setSencryptkey("no");
						creDto.setImodicount(0);
						crelist.add(creDto);
					} else if (map.containsKey(tcbstaxorgcode)) {
						crelist.add(map.get(tcbstaxorgcode));
					}

			    }
			}
				// 自动获取代理银行代码
				TsGenbankandreckbankDto bankdto = new TsGenbankandreckbankDto();
				bankdto.setSbookorgcode(getLoginInfo().getSorgcode());
				List<TsGenbankandreckbankDto> banklist = CommonFacade.getODB()
						.findRsByDto(bankdto);
				if (null != banklist && banklist.size() > 0) {
					HashMap<String,TsMankeyDto> jyMap = new HashMap<String,TsMankeyDto>();//解决广西一个银行代理多个国库，导致一个银行插入多遍报主键冲突
					for (TsGenbankandreckbankDto tmp : banklist) {// 生成密钥
						if (!map.containsKey(tmp.getSreckbankcode())&&!jyMap.containsKey(tmp.getSreckbankcode())) {
							TsMankeyDto creDto = new TsMankeyDto();
							creDto.setSorgcode(getLoginInfo().getSorgcode());
							creDto.setSkeymode(StateConstant.KEY_GENBANK);
							creDto.setSkeyorgcode(tmp.getSreckbankcode());
							creDto.setSkey("no");
							creDto.setSencryptkey("no");
							creDto.setImodicount(0);
							crelist.add(creDto);
							jyMap.put(tmp.getSreckbankcode(), creDto);
						} else if (map.containsKey(tmp.getSreckbankcode())&&!jyMap.containsKey(tmp.getSreckbankcode())) {
							crelist.add(map.get(tmp.getSreckbankcode()));
							jyMap.put(tmp.getSreckbankcode(), map.get(tmp.getSreckbankcode()));
						}
					}
				}
				SQLExecutor sqlExce = DatabaseFacade.getDb()
				.getSqlExecutorFactory().getSQLExecutor();
				String sql = "delete from TS_MANKEY where s_orgcode = ? ";
				sqlExce.addParam(getLoginInfo().getSorgcode());
				sqlExce.runQueryCloseCon(sql);
			if (crelist.size() > 0) {
				for(IDto d:crelist){
					TsMankeyDto m=(TsMankeyDto)d;
					System.out.println(m.getSorgcode()+"\t"+m.getSkeymode()+"\t"+m.getSkeyorgcode());
				}
				DatabaseFacade.getODB().create(CommonUtil.listTArray(crelist));
			}
			return crelist;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("自动获取出票单位信息出错！", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("自动获取出票单位信息出错！", e);
		}
	}

	public List updateAndExport(List list, java.sql.Date daffdate)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		List<String> strList = new ArrayList<String>();
		List<TsMankeyDto> billlist = list;
		List<IDto> updList = new ArrayList<IDto>();
		String root = ITFECommonConstant.FILE_ROOT_PATH; // 取得根路径
		String dirsep = File.separator; // 取得系统分割符
		String strdate = DateUtil.currentDate().toString(); // 当前系统年月日
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep;
		try {
			for (TsMankeyDto dto : billlist) {
				Random random = new Random(System.currentTimeMillis());
				Long count = random.nextLong();
				String title = dto.getSorgcode() + "_" + dto.getSkeyorgcode()
						+ "_" + daffdate;
				String key = SM3Process.calculateSign(title + count, title);
				key = key.substring(0, 48);
				title = fullpath + title + ".KEY";
				dto.setSnewkey(key);
				dto.setSnewencryptkey(key);
				dto.setDaffdate((java.sql.Date) daffdate);
				// 密钥已更新 //密钥定时生效，放在了定时任务中
				dto.setImodicount(StateConstant.KEYORG_STATUS_UPD);
				updList.add(dto);
				FileUtil.getInstance().writeFile(title, key);
				strList.add(title.replace(root, ""));
				
				//测试阶段使用一下语句//测试阶段密钥及时生效：
//				dto.setSkey(key);
//				dto.setSencryptkey(key);
//				dto.setImodicount(StateConstant.KEYORG_STATUS_AFF);
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(updList));
			//测试阶段密钥及时生效：
//			updateSecrKeyByDate(getLoginInfo().getSorgcode(),daffdate);
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("生成密钥文件失败！", e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("更新密钥出错", e);
		}

		return strList;
	}
	
	
	/**
	 * 测试阶段密钥按生效日期自动生效
	 * @throws ITFEBizException 
	 * @throws JAFDatabaseException 
	 */
	private static void updateSecrKeyByDate(String sbookorgcode,Date currentDate) throws  JAFDatabaseException { 
		
		SQLExecutor sqlExec=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
		String sql="update ts_mankey set s_key = s_newkey,S_ENCRYPTKEY=S_NEWENCRYPTKEY,I_MODICOUNT= 1,d_affdate= ? where s_orgcode = ?" ;
		sqlExec.addParam(currentDate);
		sqlExec.addParam(sbookorgcode);
		sqlExec.runQueryCloseCon(sql);
	}

}