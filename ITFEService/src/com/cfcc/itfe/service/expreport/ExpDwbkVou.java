package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpDwbkVou extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());

	public String makeReportByBiz(TrIncomedayrptDto idto, String bizType,String sbookorgcode)
			throws ITFEBizException {
		// �����������
		String strecode = idto.getStrecode();
		// Ԥ������
		String bugtype = idto.getSbudgettype();
		// Ͻ����־
		String sbelong = idto.getSbelongflag();
		// �����ڱ�־
		String strimflag = idto.getStrimflag();
		// ����
		String srptdate = idto.getSrptdate();
		// �Ƿ񺬿�ϼ�
		String slesumitem = idto.getSdividegroup();
		// �����ѯ����
		String sqlWhere = CommonMakeReport.makesqlwhere(idto);
		// ���ݱ��еı�������
		String rptType = CommonMakeReport
				.getReportTypeByBillType(idto, bizType);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
		String filename = CommonMakeReport.getExpFileNameByBillType(idto, bizType);// 
		List<TvDwbkDto> resVouList = null; // ����˰Ʊ
		String sqlwhere= "";
		if (ITFECommonConstant.IFNEWINTERFACE.equals(StateConstant.IFNEWINTERFACE_TRUE)) {
			sqlwhere = " AND s_Status = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"'";
		} else {
			sqlwhere  = " AND (s_Status = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' OR s_Status <> '"+DealCodeConstants.DEALCODE_ITFE_FAIL+"')";
		}
	   
		try {
			TvDwbkDto dwbkdto = new TvDwbkDto();
			dwbkdto.setSbookorgcode(sbookorgcode);
			dwbkdto.setSpayertrecode(strecode);
			dwbkdto.setDacct(CommonUtil.strToDate(srptdate));
			
			resVouList = CommonFacade.getODB().findRsByDtoForWhere(dwbkdto,sqlwhere);
			
			if (resVouList.size() > 0) {
				// �õ�����ļ�����
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf = new StringBuffer(
						"skgkdm,mdgkdm,pzbh,ysjc,jgdm,kmdm,zwrq,tkyydm,yszl,thbz,skzh,ysdw,fse\r\n");
				for (TvDwbkDto _dto : resVouList) {
					filebuf.append(_dto.getSpayertrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSaimtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSdwbkvoucode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getDacct());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSdwbkreasoncode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbdgkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbckflag());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpayeeacct());
					filebuf.append(splitSign);
					filebuf.append(splitSign);
					filebuf.append(splitSign);
					filebuf.append(_dto.getFamt());
					filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ�������˿�ƾ֤����" + filename, e);
		}

	}
}
