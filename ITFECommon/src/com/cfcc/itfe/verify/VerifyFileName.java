package com.cfcc.itfe.verify;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 校验导入的文件名称是否符合规范
 * @author zhouchuan
 *
 */
public class VerifyFileName {
	
	private static Log logger = LogFactory.getLog(VerifyFileName.class);
	
	/**
	 * 校验文件名称是否符合税票收入的名称规范
	 * @param String filename 文件名称 
	 * @throws ITFEBizException
	 */
	public static void verifyIncomeFile(String filename) throws ITFEBizException{
		
	}
	
	/**
	 * 校验是否重复导入
	 * @param sorgcode
	 * @param sfilename
	 * @throws ITFEBizException
	 */
	public static boolean verifyImportRepeat(String sorgcode,String sfilename) throws ITFEBizException{
		TvFilepackagerefDto finddto = new TvFilepackagerefDto();
		finddto.setSorgcode(sorgcode);
		finddto.setSfilename(sfilename);
		
		try {
			List list = CommonFacade.getODB().findRsByDto(finddto);
			if(null == list || list.size() == 0){
				return false;
			}
			
			return true;
		} catch (JAFDatabaseException e) {
			logger.error("校验导入文件的重复性时出现数据库异常!", e);
			throw new ITFEBizException("校验导入文件的重复性时出现数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("校验导入文件的重复性时出现数据库异常!", e);
			throw new ITFEBizException("校验导入文件的重复性时出现数据库异常!", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
