package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForTJ extends AbstractExpReportForTJ {
	final Log log = LogFactory.getLog(this.getClass());

	public HashMap makeReportByBiz(TrIncomedayrptDto idto, String bizType,String sbookorgcode,HashMap map)
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
		List<TvInfileDetailDto> resVouList = null; // ����˰Ʊ
		//����Map
		HashMap<String, HashMap> returnMap = new HashMap<String, HashMap>();
		try {
			TvInfileDetailDto voudto = new TvInfileDetailDto();
			voudto.setSorgcode(sbookorgcode);
			voudto.setScommitdate(srptdate);
			voudto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			
			resVouList = CommonFacade.getODB().findRsByDtoWithUR(voudto);
		
			if (resVouList.size() > 0) {
				// �õ�����ļ�����
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf = new StringBuffer(
						"skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
				for (TvInfileDetailDto _dto : resVouList) {
					filebuf.append(_dto.getSrecvtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrecvtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetlevelcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetsubcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxticketno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSunitcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpaybookkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSopenaccbankcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());
					filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				returnMap.put(fullpath.replaceAll(root, ""), null);
				return returnMap;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ������ƾ֤����" + filename, e);
		}

	}
}
