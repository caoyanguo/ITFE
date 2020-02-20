package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpReportStock extends AbstractExpReport {
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
		queryDto.setSfinorgcode(idto.getSfinorgcode());
		List<TrIncomedayrptDto> resList = null;// ���뱨��
		String filename = "s" + strecode + "_c0" + srptdate+".txt"; // ����ļ�����
		List<TrStockdayrptDto> stockList = null;// ����ձ�
		try {
			TrStockdayrptDto stockdto = new TrStockdayrptDto();
			stockdto.setSrptdate(srptdate);
			stockdto.setStrecode(strecode);
			stockdto.setSorgcode(idto.getSfinorgcode());
			stockList = CommonFacade.getODB().findRsByDtoWithUR(stockdto);
			if (stockList.size() > 0) {
				// �õ�����ļ�����
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf = new StringBuffer(
						"kjzh,kjzhm,zwrq,srye,brdfsr,brjfzc,brye\r\n");
				for (TrStockdayrptDto _dto : stockList) {
					filebuf.append(_dto.getSaccno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSaccname());
					filebuf.append(splitSign);
					filebuf.append(srptdate);
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneyyesterday());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneyin());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneyout());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneytoday());
					filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("���ɿ�汨�����" + filename, e);
		}

	}
}
