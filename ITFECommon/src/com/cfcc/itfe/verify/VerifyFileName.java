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
 * У�鵼����ļ������Ƿ���Ϲ淶
 * @author zhouchuan
 *
 */
public class VerifyFileName {
	
	private static Log logger = LogFactory.getLog(VerifyFileName.class);
	
	/**
	 * У���ļ������Ƿ����˰Ʊ��������ƹ淶
	 * @param String filename �ļ����� 
	 * @throws ITFEBizException
	 */
	public static void verifyIncomeFile(String filename) throws ITFEBizException{
		
	}
	
	/**
	 * У���Ƿ��ظ�����
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
			logger.error("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
