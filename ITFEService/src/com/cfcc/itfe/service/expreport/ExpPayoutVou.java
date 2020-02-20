package com.cfcc.itfe.service.expreport;
import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.AddColumnPayOutDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpPayoutVou extends AbstractExpReport {
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
		String filename = "s"+strecode+"_z0"+srptdate;// 
		List<AddColumnPayOutDto> paylist = null;// ֧��ƾ֤
		try {
			String sql = " select A.S_TRECODE as S_TRECODE,B.S_FUNSUBJECTCODE As S_FUNSUBJECTCODE ,B.S_ECNOMICSUBJECTCODE As S_ECNOMICSUBJECTCODE,A.S_ACCDATE as S_ACCDATE,A.S_TAXTICKETNO as S_TAXTICKETNO,A.S_PAYERACCT as S_PAYERACCT,A.S_BUDGETUNITCODE as S_BUDGETUNITCODE,A.S_RECACCT as  S_RECACCT,Case substr(A.S_FILENAME,length(S_FILENAME)-7,2)  when '23' then '2' else '1' end as s_biztype ,A.S_BUDGETTYPE as S_BUDGETTYPE,A.N_MONEY  as N_MONEY from TV_PAYOUTMSGMAIN A,TV_PAYOUTMSGSUB B where A.S_BIZNO = B.S_BIZNO AND A.S_ORGCODE = ?  AND  A.S_TRECODE = ? AND A.S_ACCDATE = ? and A.S_STATUS =? ";
			SQLExecutor exec;
				exec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				exec.addParam(sbookorgcode);
				exec.addParam(strecode);
				exec.addParam(srptdate);
				exec.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				paylist = (List<AddColumnPayOutDto>) exec.runQueryCloseCon(
						sql, AddColumnPayOutDto.class).getDtoCollection();
			
			if (paylist.size() > 0) {
				// �õ�����ļ�����
				String root = ITFECommonConstant.FILE_ROOT_PATH; // ȡ�ø�·��
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // �ļ���¼�ָ�����
				StringBuffer filebuf = new StringBuffer(
						"gkdm,kmdm,jjdm,zwrq,pzbh,fkzh,ysdw,skzh,zczl,yszl,fse\r\n");
				for (AddColumnPayOutDto _dto : paylist) {
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetunitcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSecnomicsubjectcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSaccdate());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxticketno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpayeracct());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetunitcode());
					filebuf.append(splitSign);
					filebuf.append(splitSign);
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbiztype());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());
					filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("����Ԥ��֧��ƾ֤����" + filename, e);
		}

	}
}
