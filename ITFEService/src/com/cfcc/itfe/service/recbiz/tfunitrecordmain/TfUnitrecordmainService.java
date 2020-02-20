package com.cfcc.itfe.service.recbiz.tfunitrecordmain;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TfUnitrecordCustomDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time   14-10-09 11:17:45
 * codecomment: 
 */

public class TfUnitrecordmainService extends AbstractTfUnitrecordmainService {
	private static Log log = LogFactory.getLog(TfUnitrecordmainService.class);	
	private static final int MAX_NUM = 150000; // ÿ��ȡ������¼��


	/**
	 * �������˴�����Ϣ
	 	 
	 * @generated
	 * @param dtoInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String legalPersonCodeExport(IDto dtoInfo) throws ITFEBizException {
    	TfUnitrecordmainDto mainDto = (TfUnitrecordmainDto)dtoInfo;
    	String sqlWhere = genWhere(mainDto);
    	
    	String root = ITFECommonConstant.FILE_ROOT_PATH;
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String strdate = DateUtil.date2String2(new java.util.Date()); // ��ǰϵͳ������
		String filename = getLoginInfo().getSorgcode()+"_���˴���ά��_������.CSV";
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep + filename;
		String sql = "SELECT S_ORGCODE ,S_TRECODE ,S_AGENCYCODE ,S_AGENCYNAME FROM TF_UNITRECORDMAIN a,TF_UNITRECORDSUB b where a.I_VOUSRLNO = b.I_VOUSRLNO AND b.S_FUNDTYPECODE = '1' "+sqlWhere;
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(MAX_NUM);
			List<TfUnitrecordCustomDto> list = (List<TfUnitrecordCustomDto>) sqlExec.runQueryCloseCon(sql, TfUnitrecordCustomDto.class).getDtoCollection();
			String splitSign = ","; // �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer("�����������,�����������,���˴���,��������,���˼���,�ܷ�ʵ���ʽ�,��λ����,ע������,��ҵ����,�˻���\r\n");
				for (TfUnitrecordCustomDto _dto : list) {
					filebuf.append(_dto.getSorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSagencycode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSagencyname());
					filebuf.append(splitSign);
					filebuf.append("");
					filebuf.append(splitSign);
					filebuf.append("1");
					filebuf.append(splitSign);
					filebuf.append("N");
					filebuf.append(splitSign);
					filebuf.append("N");
					filebuf.append(splitSign);
					filebuf.append("N");
					filebuf.append(splitSign);
					filebuf.append(" ");
					filebuf.append("\r\n");
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
			    return fullpath.replaceAll(root, "");
				
			} else {
				throw new ITFEBizException("��ѯ������");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����",e);
		}
    }

    /**
	 * ���ɲ�ѯ����
	 * @return
	 */
	private String genWhere(TfUnitrecordmainDto dto){
		String sqlwhere = "";
		if (null!=dto.getSadmdivcode()) {
			sqlwhere += " AND  a.S_ADMDIVCODE = '"+dto.getSadmdivcode()+"'";
		}
		if (null!=dto.getSstyear()) {
			sqlwhere += " AND a.S_STYEAR  = '"+dto.getSstyear()+"'";
		}
		if (null!=dto.getSvtcode()) {
			sqlwhere += " AND  a.S_VTCODE = '"+dto.getSvtcode()+"'";
		}
		if (null!=dto.getSvoudate()) {
			sqlwhere += " AND  a.S_VOUDATE = '"+dto.getSvoudate()+"'";
		}
		if (null!=dto.getSvoucherno()) {
			sqlwhere += " AND  a.S_VOUCHERNO = "+dto.getSvoucherno()+"";
		}
		if (null!=dto.getStrecode()) {
			sqlwhere += " AND  a.S_TRECODE = '"+dto.getStrecode()+"'";
		}
		if (null!=dto.getSfinorgcode()) {
			sqlwhere += " AND  a.S_FINORGCODE = '"+dto.getSfinorgcode()+"'";
		}
		if (null!=dto.getSpaybankcode()) {
			sqlwhere += " AND  a.S_PAYBANKCODE = '"+dto.getSpaybankcode()+"'";
		}
		if (null!=dto.getSpaybankname()) {
			sqlwhere += " AND  a.S_PAYBANKNAME = '"+dto.getSpaybankname()+"'";
		}
		if (null!=dto.getSallflag()) {
			sqlwhere += " AND  a.S_ALLFLAG = '"+dto.getSallflag()+"'";
		}
		return sqlwhere;
		
	}
}