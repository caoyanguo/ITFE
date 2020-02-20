package com.cfcc.itfe.service.dataquery.totalreportsearchandexport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.*;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.service.dataquery.totalreportsearchandexport.AbstractTotalReportSearchAndExportService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zl
 * @time   13-04-18 08:49:22
 * codecomment: 
 */

public class TotalReportSearchAndExportService extends AbstractTotalReportSearchAndExportService {
	private static Log log = LogFactory.getLog(TotalReportSearchAndExportService.class);	
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;

	/**
	 * �����ļ�
	 	 
	 * @generated
	 * @param findto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    @SuppressWarnings("unchecked")
	public String dataExport(IDto findto,String whereSql) throws ITFEBizException {
		List list=new ArrayList();
		String filename="";
		try {
			startdate = TimeFacade.getCurrentStringTime();
			if(startdate.equals(enddate))
				pici++;
			else
				enddate = TimeFacade.getCurrentStringTime();
			if(findto instanceof TrIncomedayrptDto ){
				TrIncomedayrptDto mdto=(TrIncomedayrptDto)findto;
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto, whereSql);
				filename=TimeFacade.getCurrentStringTime()+"_4"+(pici<10?"0"+pici:pici)+"hd.txt";
			}else if(findto instanceof HtrIncomedayrptDto){
				HtrIncomedayrptDto mdto=(HtrIncomedayrptDto)findto;
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,whereSql);
				filename=TimeFacade.getCurrentStringTime()+"_4"+(pici<10?"0"+pici:pici)+"hd.txt";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = ",";//"\t"; // �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
//				int index=1;
				if(findto instanceof TrIncomedayrptDto){
					for (TrIncomedayrptDto _dto :(List<TrIncomedayrptDto>) list) {
						filebuf.append(_dto.getStrecode());//�������
						filebuf.append(splitSign+_dto.getSbudgetlevelcode());//Ԥ�㼶��
						filebuf.append(splitSign+_dto.getSbudgetsubcode());//��Ŀ����
						filebuf.append(splitSign+_dto.getSrptdate());//��������
						filebuf.append(splitSign+_dto.getSbudgettype());//Ԥ������
						filebuf.append(splitSign+_dto.getNmoneyday());//���ۼ�
						filebuf.append(splitSign+_dto.getNmoneymonth());//���ۼ�
						filebuf.append(splitSign+_dto.getNmoneyyear());//���ۼ�
						filebuf.append("\r\n");
					}
				}else if(findto instanceof HtrIncomedayrptDto){
					for (HtrIncomedayrptDto _dto :(List<HtrIncomedayrptDto>) list) {
						filebuf.append(_dto.getStrecode());//�������
						filebuf.append(splitSign+_dto.getSbudgetlevelcode());//Ԥ�㼶��
						filebuf.append(splitSign+_dto.getSbudgetsubcode());//��Ŀ����
						filebuf.append(splitSign+_dto.getSrptdate());//��������
						filebuf.append(splitSign+_dto.getSbudgettype());//Ԥ������
						filebuf.append(splitSign+_dto.getNmoneyday());//���ۼ�
						filebuf.append(splitSign+_dto.getNmoneymonth());//���ۼ�
						filebuf.append(splitSign+_dto.getNmoneyyear());//���ۼ�
						filebuf.append("\r\n");
					}
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
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
    }


	public static Log getLog() {
		return log;
	}


	public static void setLog(Log log) {
		TotalReportSearchAndExportService.log = log;
	}

}