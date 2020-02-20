package com.cfcc.itfe.param.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.ParamConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.param.IParamInOut;
import com.cfcc.itfe.param.service.ParamInOutTPSHAREDIVIDE;
import com.cfcc.itfe.param.service.ParamInOutTSTAXORG;
import com.cfcc.itfe.persistence.dto.TpShareDivideDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectAdjustDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreAcctNoBnkCodeDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public abstract class AbstractParamInOut implements IParamInOut {
	private static Log log = LogFactory.getLog(AbstractParamInOut.class);
	public String export(Map<String,String> param) throws Exception {
		String tabcode = (String)param.get(ParamConstant.TAB_CODE);
		String tabname = (String)param.get(ParamConstant.TAB_NAME);
		String separator = (String)param.get(ParamConstant.SEPARATOR);
		String orgcode = (String)param.get(ParamConstant.ORGCODE);
		File file = createParamFile(tabname);
		BufferedWriter bw = null;
		try {
			List<IDto> dtos = getDtos(tabcode,orgcode);
			if(dtos == null || dtos.size()==0){
				return "NODATA:"+tabname;
			}
			bw = new BufferedWriter(new FileWriter(file));
			if(tabcode.equals("TP_SHARE_DIVIDE")){
				ParamInOutTPSHAREDIVIDE.seq=1;
				ParamInOutTPSHAREDIVIDE.groupseq=1;
				ParamInOutTPSHAREDIVIDE.amt100=new BigDecimal(0);
				ParamInOutTPSHAREDIVIDE.counter =0;
			}
			
			if(tabcode.equals("TD_TAXORG_PARAM")){
				ParamInOutTSTAXORG.isfirst = true;
			}
			for (int i = 0; i < dtos.size(); i++) {
				StringBuffer sb = new StringBuffer();
				createLine(dtos.get(i),separator,sb);
				if(sb.toString().startsWith("error:")){
					throw new Exception(sb.substring(5,sb.length()-1));
				}
				bw.write(sb.toString().replaceAll("null", ""));
			}
			bw.flush();
		} catch (Exception e) {
			log.error("业务处理异常", e);
			throw new ITFEBizException("File not found exception",e);
		}finally{
			if(bw != null){
				try{
					bw.close();
				}catch(Exception e){}
			}
		}
		return file.getName();
	}

	private File createParamFile(String tabname) throws IOException {
		String path = ITFECommonConstant.FILE_ROOT_PATH+System.getProperty("file.separator");
		File tmp = new File(path+tabname+".txt");
		if(!tmp.exists()) tmp.createNewFile();
		return tmp;
	}
	protected abstract void createLine(IDto dto,String separator,StringBuffer sb);

	protected List<IDto> getDtos(String tabcode,String orgcode) throws Exception{
		if (tabcode.equals("TS_PAYBANK")) {
			HashMap<String, TsPaybankDto> mapTsPayBank = SrvCacheFacade.cachePayBankInfo();
			List <IDto> list = new ArrayList<IDto>();
			for (String key : mapTsPayBank.keySet()) {
				TsPaybankDto _dto = mapTsPayBank.get(key);
			    list.add(_dto);	
			}
			return list;
		}else if(tabcode.equals("TP_SHARE_DIVIDE")){
			List<IDto> result = new ArrayList<IDto>();
			String sql = "SELECT S_ROOTTRECODE,S_ROOTTAXORGCODE,S_ROOTBDGSBTCODE,S_ROOTASTFLAG FROM  TP_SHARE_DIVIDE WHERE S_BOOKORGCODE = ?  GROUP BY S_ROOTTRECODE,S_ROOTTAXORGCODE,S_ROOTBDGSBTCODE,S_ROOTASTFLAG";
			SQLExecutor sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(orgcode);
			SQLResults sqlresult = sqlExecutor.runQueryCloseCon(sql);
			if(null != sqlresult || sqlresult.getRowCount() != 0){
				for(int i = 0 ; i < sqlresult.getRowCount() ; i ++ ){
					TpShareDivideDto tmpDivideDto = new TpShareDivideDto();
					tmpDivideDto.setSbookorgcode(orgcode);
					tmpDivideDto.setSroottrecode(sqlresult.getString(i, "S_ROOTTRECODE"));
					tmpDivideDto.setSroottaxorgcode(sqlresult.getString(i, "S_ROOTTAXORGCODE"));
					tmpDivideDto.setSrootbdgsbtcode(sqlresult.getString(i, "S_ROOTBDGSBTCODE"));
					tmpDivideDto.setSrootastflag(sqlresult.getString(i, "S_ROOTASTFLAG"));
					result.add(tmpDivideDto);
				}
				return result;
			}else{
				return result;
			}
//			return DatabaseFacade.getODB().find(TpShareDivideDto.class, " WHERE S_BOOKORGCODE = '"+orgcode+"' ORDER BY S_ROOTTRECODE,S_ROOTTAXORGCODE,S_ROOTBDGSBTCODE,S_ROOTASTFLAG");
		}else if(tabcode.equals("TS_BUDGETSUBJECT")){
			List<IDto> dtolist=new ArrayList<IDto>();
			dtolist=DatabaseFacade.getODB().find(TsBudgetsubjectDto.class, " WHERE S_ORGCODE = '"+orgcode+"'");
			dtolist.addAll(DatabaseFacade.getODB().find(TsBudgetsubjectAdjustDto.class, " WHERE S_ORGCODE = '"+orgcode+"'"));
			return dtolist;
		}else if(tabcode.equals("TS_TREACCTNOBNKCODE")){
			String sql = "select ts.S_ORGCODE,ts.S_PAYBANKNO,td.S_BOOKACCT,td.S_BOOKACCTNAME from TS_ORGAN ts,TD_BOOKACCT_MAIN td where ts.S_ORGCODE = td.S_BOOKORGCODE and td.C_ACCTPROP = '1'";
			List<IDto> list = new ArrayList<IDto>();
			SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			SQLResults rs = sqlExec.runQueryCloseCon(sql, TsTreAcctNoBnkCodeDto.class);
			if(rs != null){
				list.addAll(rs.getDtoCollection());
			}
			return list;
		}else{
			IDto _dto = CommonUtil.createDtoByTable(tabcode);
			if (!ParamConstant.CENTERORG.equals(orgcode)) {
				setConds(_dto,orgcode);
			}
			return CommonFacade.getODB().findRsByDto(_dto);
		}
		
	}
	
	protected abstract void setConds(IDto dto,String cond);

	public void importParam(String filename,Map param) throws Exception {
		String path = ITFECommonConstant.FILE_ROOT_PATH+System.getProperty("file.separator");
		try {
			List<String[]> conts = FileUtil.getInstance().readFileWithLine(path+filename, ",");
			verify(conts,param);
			List<IDto> _dtos = new ArrayList<IDto>();
			if(conts==null || conts.size()==0){
				throw new ITFEBizException("空文件");
			}
			for(int i=1; i<conts.size(); i++){
				String[] cols = conts.get(i);
				_dtos.add(createDto(cols,filename));
			}
			IDto[] dtos = new IDto[_dtos.size()];
			_dtos.toArray(dtos);
			DatabaseFacade.getODB().create(dtos);
			FileUtil.getInstance().deleteFile(path+filename);
		} catch (FileOperateException e) {
			log.error("文件读取失败", e);
			throw new ITFEBizException("File not found exception",e);
		}
	}

	private void verify(List<String[]> conts,Map param) throws Exception {
		String orgcode = (String)param.get(ParamConstant.ORGCODE);
		for(int i=1; i<conts.size(); i++){
			if(!orgcode.equals(conts.get(i)[0])){
				throw new ITFEBizException("文件中核算主体不是登录核算主体");
			}
		}
	}
	protected abstract IDto createDto(String[] cols,String filename) throws Exception ;

	protected String trim(String s){
		String tmp = null;
		if(s!=null){
			tmp = s.trim();
		}
		return "".equals(tmp)?null:tmp;
	}
}
