package com.cfcc.itfe.service.sendbiz.exporttbsfiletxt;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author win7
 * @time 13-10-29 06:18:53 codecomment:
 */

public class ExportTBSfiletxtService extends AbstractExportTBSfiletxtService {
	private static Log log = LogFactory.getLog(ExportTBSfiletxtService.class);

	/**
	 * ���������ļ� Mapper m0 = new Mapper("0", "ʵ���ʽ�"); Mapper m1 = new Mapper("1",
	 * "ֱ��֧�����"); Mapper m2 = new Mapper("2", "��Ȩ֧�����"); Mapper m3 = new
	 * Mapper("3", "����֧����������"); TV_PAYOUTMSGMAIN(SUB)����ʵ���ʽ�
	 * TV_DIRECTPAYMSGMAIN(SUB)ֱ��֧����� TV_GRANTPAYMSGMAIN(SUB)��Ȩ֧�����
	 * TV_PAYRECK_BANK ���а���֧�����������룩
	 */

	public List exportTBSdata(String sbiztypetbs, String orgcode,
			Date dacctdate, String ctrimflag) throws ITFEBizException {
		List<IDto> list = new ArrayList<IDto>();
		StringBuffer resultStr = new StringBuffer();
		try {
			if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_SBZJ)) {// "0", "ʵ���ʽ�" --//
													// ʵ���ʽ���ϸ���ļ����ʽ���ļ�
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(orgcode);

				if (null != dacctdate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(dacctdate, "yyyyMMdd"));
				}
				if (null != ctrimflag) {
					where += " AND  S_TRIMFLAG = ? ";
					params.add(ctrimflag);
				}
				
				where += " AND S_BIZNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5207);
				params.add(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayoutmsgmainDto.class, where, params);

			} else if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_ZJZFED)) {// "1", "ֱ��֧�����" --
															// ����ֱ��֧�������ϸ���ļ�
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(orgcode);

				if (null != dacctdate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(dacctdate, "yyyyMMdd"));
				}
				// if (null != ctrimflag) {
				// where += " AND  S_TRIMSIGN = ? ";
				// params.add(ctrimflag);
				// }
				
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5108);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvDirectpaymsgmainDto.class, where, params);

			} else if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_SQZFED)) {// "2", "��Ȩ֧�����" --
															// ��Ȩ֧�������ϸ���ļ�
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(orgcode);

				if (null != dacctdate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(dacctdate, "yyyyMMdd"));
				}
				// if (null != ctrimflag) {
				// where += " AND  S_TRIMSIGN = ? ";
				// params.add(ctrimflag);
				// }
				
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5106);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvGrantpaymsgmainDto.class, where, params);

			} else if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_JZZFHKSQ)) {// "3", "����֧����������"
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(orgcode);

				if (null != dacctdate) {
//					where += " AND  D_ACCTDATE = ? ";
					where += " AND  D_VOUDATE = ? ";
					params.add(dacctdate);
				}
				if (null != ctrimflag) {
					where += " AND  S_TRIMSIGN = ? ";
					params.add(ctrimflag);
				}
				
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_2301);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayreckBankDto.class, where, params);
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			log.info("��ѯ�����쳣" + e.getMessage());
			throw new ITFEBizException("ҵ�����쳣:" + e.getCause().getMessage(), e);
		}
		return list;
	}

	/**
	 * ��ȡ��ϸ��Ϣ
	 * 
	 */
	public List getsubinfo(IDto idto) throws ITFEBizException {
		List list = new ArrayList();
		try {
			if (idto instanceof TvPayreckBankDto) {// ��������
				TvPayreckBankDto dto = (TvPayreckBankDto) idto;

				list = DatabaseFacade.getDb().find(TvPayreckBankListDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());

			} else if (idto instanceof TvGrantpaymsgmainDto) {// ��Ȩ֧��
				TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) idto;
				list = DatabaseFacade.getDb().find(TvGrantpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno()+" and S_PACKAGETICKETNO='"+dto.getSpackageticketno()+"' ");
			} else if (idto instanceof TvDirectpaymsgmainDto) {// ֱ��֧��
				TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) idto;
				list = DatabaseFacade.getDb().find(TvDirectpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (idto instanceof TvPayoutmsgmainDto) {// ʵ���ʽ�
				TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) idto;
				list = DatabaseFacade.getDb().find(TvPayoutmsgsubDto.class,
						" where  S_BIZNO = '" + dto.getSbizno() + "'");
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			log.info("��ѯ�����쳣" + e.getMessage());
			throw new ITFEBizException("��ѯ�����쳣:" + e.getCause().getMessage(), e);
		}
		return list;
	}

	public String getTBSNum() throws ITFEBizException {
		String tmpPackNo="";
			 try {
				tmpPackNo =  SequenceGenerator.getNextByDb2(SequenceName.TBS_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
			} catch (SequenceException e) {
				e.printStackTrace();
				log.info("�������κ��쳣");
				throw new ITFEBizException("�������κ��쳣:" + e.getCause().getMessage(), e);
			}
			
			if (tmpPackNo.length()>4) {
				tmpPackNo=tmpPackNo.substring(tmpPackNo.length()-4,tmpPackNo.length());
			}else {
				tmpPackNo=String.format("%04d",Integer.valueOf(tmpPackNo));
			}
			
		
		return tmpPackNo;
	}

	
	public TsPayacctinfoDto gettspayacctinfo(TvPayoutmsgmainDto paramdto)
			throws ITFEBizException {
		return null;
	}

	public String getmoveflag(TvPayoutmsgmainDto dto, TvPayoutmsgsubDto subdto) throws ITFEBizException {
		try {
			Map<String, TsBudgetsubjectDto> smap=SrvCacheFacade.cacheTsBdgsbtInfo(dto.getSorgcode());
			
			TsBudgetsubjectDto tsbugdto = smap.get(subdto.getSfunsubjectcode());
			String tmpstr=tsbugdto.getSmoveflag();
			if (tmpstr.equalsIgnoreCase("0")) {
				return "1";
			} else {
				return "2";
			}
		} catch (JAFDatabaseException e1) {
			log.error("��ȡ������־�쳣");
			e1.printStackTrace();
		}
		return null;
	}

	public void updateVdtoStatus(List list) throws ITFEBizException {
		IDto idto = (IDto) list.get(0);
		String sql = new String();
		try {
			if (idto instanceof TvPayreckBankDto) {// ��������
				List<TvPayreckBankDto> dtolist = list;
				for(TvPayreckBankDto dto : dtolist){
					sql +="'" + dto.getIvousrlno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			} else if (idto instanceof TvGrantpaymsgmainDto) {// ��Ȩ֧��
				List<TvGrantpaymsgmainDto> dtolist = list;
				for(TvGrantpaymsgmainDto dto : dtolist){
					sql +="'" + dto.getIvousrlno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			} else if (idto instanceof TvDirectpaymsgmainDto) {// ֱ��֧��
				List<TvDirectpaymsgmainDto> dtolist = list;
				for(TvDirectpaymsgmainDto dto : dtolist){
					sql +="'" + dto.getIvousrlno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			} else if (idto instanceof TvPayoutmsgmainDto) {// ʵ���ʽ�
				List<TvPayoutmsgmainDto> dtolist = list;
				for(TvPayoutmsgmainDto dto : dtolist){
					sql +="'" + dto.getSbizno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			log.info("����ƾ֤����������״̬����");
			throw new ITFEBizException("����ƾ֤����������״̬����:" + e.getCause().getMessage(), e);
		}
		
	}

}