package com.cfcc.itfe.service.dataquery.tvfinincomedetail;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDivideDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.datamove.DataMoveUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;

/**
 * @author t60
 * @time   12-02-24 10:44:46
 * codecomment: 
 */

public class TvFinIncomeDetailService extends AbstractTvFinIncomeDetailService {
	private static Log log = LogFactory.getLog(TvFinIncomeDetailService.class);	

	/**
	 * ����	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
      return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
      
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
      
    }
    
    /**
     * ���������ˮ
     */
    public String makeDivide(List excheckList, String where) throws ITFEBizException {
    	SQLExecutor exec = null;
    	try {
	    	if(null != excheckList && excheckList.size() >0) {
	    		TvFinIncomeDetailDto finddto = (TvFinIncomeDetailDto)excheckList.get(0);
	    		String sql = "select t.* from (select * from TV_FIN_INCOME_DETAIL union all select * from HTV_FIN_INCOME_DETAIL) as t ";
	    		exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
	    		exec.setMaxRows(1000000);
	    		CommonQto qto = SqlUtil.IDto2CommonQto(finddto);
				if (qto != null) {
					if (where != null) {
						sql += qto.getSWhereClause() + " and " + where;
					} else {
						sql += qto.getSWhereClause();
					}
					exec.addParam(qto.getLParams());
				} else {
					if (where != null) {
						sql += " where " + where;
					}
				}
	    		
	    		SQLResults res = exec.runQueryCloseCon(sql, TvFinIncomeDetailDto.class);
	    		List<TvFinIncomeDetailDto> reslist = (List<TvFinIncomeDetailDto>)res.getDtoCollection();
	    		
	    		String root = ITFECommonConstant.FILE_ROOT_PATH;
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String dateStr = "";
				if(StringUtils.isBlank(finddto.getSintredate())){
					dateStr = TimeFacade.getCurrentStringTime();
				}else{
					dateStr = finddto.getSintredate();
				}
				String fileName = "3139�����ˮ("+dateStr+").txt";
				String fullpath = root + "exportFile" + dirsep + strdate + dirsep + fileName;
	    		
	    		if(null != reslist && reslist.size() > 0) {
	    			String title="�������ش���,�������,����ί������,����ˮ��,ƾ֤���,����ƾ֤����, Ԥ�㼶��,Ԥ���Ŀ����,Ԥ������,���,ƾ֤��Դ,���ջ��ش���,ҵ����ˮ��";
					StringBuffer filebuf = new StringBuffer(title+"\r\n");
					String splitSign = ",";
			        for (TvFinIncomeDetailDto _dto :reslist) {
			        	filebuf.append(_dto.getSorgcode().trim());filebuf.append(splitSign);
			        	filebuf.append(_dto.getStrecode());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSintredate());filebuf.append(splitSign);
			        	filebuf.append(_dto.getIpkgseqno());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSexpvouno());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSexpvoutype());filebuf.append(splitSign);
			        	filebuf.append(_dto.getCbdglevel());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSbdgsbtcode());filebuf.append(splitSign);
			        	filebuf.append(_dto.getCbdgkind());filebuf.append(splitSign);
			        	filebuf.append(_dto.getFamt());filebuf.append(splitSign);
			        	filebuf.append(_dto.getCvouchannel());filebuf.append(splitSign);
			        	filebuf.append(_dto.getStaxorgcode());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSseq());
			        	filebuf.append("\r\n");
					}
				
					File f = new File(fullpath);
					if (f.exists()) {
						FileUtil.getInstance().deleteFiles(fullpath);
					}
					FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
					
				    return fullpath.replaceAll(root, "");
	    		}
	    	}
    	} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����",e);
		} finally {
			if(null != exec) {
				exec.closeConnection();
			}
		}
		return null;
    }

	/*public String makeDivide(List excheckList, String reportdate)
			throws ITFEBizException {
		
		List<TvFinIncomeonlineDto> list = new ArrayList<TvFinIncomeonlineDto>();
		
		try {
			//��ѯѡ�е������ˮ�����Ƿ��е���˰Ʊ��Ϣ
			for(TvFinIncomeDetailDto dto: (List<TvFinIncomeDetailDto>)excheckList){
				
				String where = " WHERE F_TRAAMT="+dto.getFamt().toString().trim()+" and S_TAXVOUNO='"+dto.getSexpvouno().trim()+"' and S_TRECODE='"+dto.getStrecode().trim()+
				"' and S_APPLYDATE='"+dto.getSintredate().trim()+"' and S_TAXORGCODE='"+dto.getStaxorgcode().trim()+"' ";
				
				List detaildtolist = DatabaseFacade.getDb().find(TvFinIncomeonlineDto.class, where);
				
				if(null == detaildtolist || 0 ==detaildtolist.size() ){
					continue;
				}
				else{
					TvFinIncomeonlineDto o= (TvFinIncomeonlineDto)detaildtolist.get(0);
					list.add(o);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("�ֶ�������ֳ�����ת��ʱ����!", e);
			throw new ITFEBizException("�ֶ�������ֳ�����ת��ʱ����!", e);
		}
		return null;
//		return DataMoveUtil.makeDivide_(list,1,reportdate,this.getLoginInfo().getSorgcode(),null);
	}*/

}