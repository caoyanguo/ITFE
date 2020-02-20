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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpCorrVouForGZ extends AbstractExpReport {
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
		String filename = null ;
		List<TvInCorrhandbookDto> resVouList = null; // �������˰Ʊ
		String sqlwhere= "";
		if (ITFECommonConstant.IFNEWINTERFACE.equals(StateConstant.IFNEWINTERFACE_TRUE)) {
			sqlwhere = " AND s_Status = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"'";
		} else {
			sqlwhere  = "  AND (s_Status = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' OR s_Status <> '"+DealCodeConstants.DEALCODE_ITFE_FAIL+"')";
		}
		try {
			HashMap<String, String> taxMap = super.converTaxCode(sbookorgcode);
			filename = CommonMakeReport.getExpFileNameByBillTypeForGZ(idto,
					bizType,taxMap);
			
			TvInCorrhandbookDto corrdto = new TvInCorrhandbookDto();
			corrdto.setSbookorgcode(sbookorgcode);
			corrdto.setSoripayeetrecode(strecode);
//			if(ITFECommonConstant.IFNEWINTERFACE.equals(StateConstant.IFNEWINTERFACE_TRUE)){//�½ӿ�
				corrdto.setDaccept(CommonUtil.strToDate(srptdate));
//	    	}else{//�ɽӿڰ�����������
//	    		corrdto.setDaccept(CommonUtil.strToDate(srptdate));
//	    	}
			resVouList = CommonFacade.getODB().findRsByDtoForWhere(corrdto, sqlwhere);
		
			if (resVouList.size() > 0) {
				// �õ�����ļ�����
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf = new StringBuffer(
						"pzbh,yskgkdm,ymdgkdm,yjgdm,yysjc,ykmdm,yfzbz,yfse,gzyydm,xskgkdm,xmdgkdm,xjgdm,xysjc,xkmdm,xfzbz,xfse,ssyf,yszl,tzqbz,ztbz,yssgkdm,xssgkdm,zwrq\r\n");
				for (TvInCorrhandbookDto _dto : resVouList) {
					filebuf.append(_dto.getScorrvouno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoripayeetrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoriaimtrecode());
					filebuf.append(splitSign);
					String oristaxorgcode = "";
					if (taxMap.containsKey(_dto.getSoritaxorgcode())) {
						oristaxorgcode = taxMap.get(_dto.getSoritaxorgcode());
					} else {
						throw new ITFEBizException("�����������" + sbookorgcode
								+ "��TCBS���ջ��ش��루" + _dto.getSoritaxorgcode()
								+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
					}
					filebuf.append(oristaxorgcode);
					filebuf.append(splitSign);
					filebuf.append(_dto.getCoribdglevel());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoribdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoriastflag());
					filebuf.append(splitSign);
					filebuf.append(_dto.getForicorramt());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSreasoncode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScurpayeetrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScuraimtrecode());
					filebuf.append(splitSign);
					String curstaxorgcode = "";
					if (taxMap.containsKey(_dto.getScurtaxorgcode())) {
						curstaxorgcode = taxMap.get(_dto.getScurtaxorgcode());
					} else {
						throw new ITFEBizException("�����������" + sbookorgcode
								+ "��TCBS���ջ��ش��루" + _dto.getScurtaxorgcode()
								+ "����û���ҵ���Ӧ�ĵط��������ջ��ش��룡");
					}
					filebuf.append(curstaxorgcode);
					filebuf.append(splitSign);
					filebuf.append(_dto.getCcurbdglevel());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScurbdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScurastflag());
					filebuf.append(splitSign);
					filebuf.append(_dto.getFcurcorramt());
					filebuf.append(splitSign);
					if (_dto.getCtrimflag().equals(StateConstant.TRIMSIGN_FLAG_NORMAL)) {
						filebuf.append(_dto.getDaccept().toString().replace("-", "").substring(4,6));//�����·�
					} else {
						filebuf.append("12");
					}
				
					filebuf.append(splitSign);
					filebuf.append(_dto.getCcurbdgkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCtrimflag());
					filebuf.append(splitSign);
					filebuf.append("0");
					filebuf.append(splitSign);
					filebuf.append(strecode);
					filebuf.append(splitSign);
					filebuf.append(strecode);
					filebuf.append(splitSign);
					if (null==_dto.getDacct()) {
						filebuf.append(_dto.getDaccept().toString().replace("-", ""));
					}else{
						filebuf.append(_dto.getDacct().toString().replace("-", ""));
					}
					
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
