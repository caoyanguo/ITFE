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
	 * 导出数据文件 Mapper m0 = new Mapper("0", "实拨资金"); Mapper m1 = new Mapper("1",
	 * "直接支付额度"); Mapper m2 = new Mapper("2", "授权支付额度"); Mapper m3 = new
	 * Mapper("3", "集中支付划款申请"); TV_PAYOUTMSGMAIN(SUB)导出实拨资金
	 * TV_DIRECTPAYMSGMAIN(SUB)直接支付额度 TV_GRANTPAYMSGMAIN(SUB)授权支付额度
	 * TV_PAYRECK_BANK 商行办理支付（划款申请）
	 */

	public List exportTBSdata(String sbiztypetbs, String orgcode,
			Date dacctdate, String ctrimflag) throws ITFEBizException {
		List<IDto> list = new ArrayList<IDto>();
		StringBuffer resultStr = new StringBuffer();
		try {
			if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_SBZJ)) {// "0", "实拨资金" --//
													// 实拨资金明细岗文件和资金岗文件
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

			} else if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_ZJZFED)) {// "1", "直接支付额度" --
															// 生成直接支付额度明细岗文件
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

			} else if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_SQZFED)) {// "2", "授权支付额度" --
															// 授权支付额度明细岗文件
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

			} else if (sbiztypetbs.equalsIgnoreCase(StateConstant.TBS_JZZFHKSQ)) {// "3", "集中支付划款申请"
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
			log.info("查询数据异常" + e.getMessage());
			throw new ITFEBizException("业务处理异常:" + e.getCause().getMessage(), e);
		}
		return list;
	}

	/**
	 * 获取明细信息
	 * 
	 */
	public List getsubinfo(IDto idto) throws ITFEBizException {
		List list = new ArrayList();
		try {
			if (idto instanceof TvPayreckBankDto) {// 划款申请
				TvPayreckBankDto dto = (TvPayreckBankDto) idto;

				list = DatabaseFacade.getDb().find(TvPayreckBankListDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());

			} else if (idto instanceof TvGrantpaymsgmainDto) {// 授权支付
				TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) idto;
				list = DatabaseFacade.getDb().find(TvGrantpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno()+" and S_PACKAGETICKETNO='"+dto.getSpackageticketno()+"' ");
			} else if (idto instanceof TvDirectpaymsgmainDto) {// 直接支付
				TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) idto;
				list = DatabaseFacade.getDb().find(TvDirectpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (idto instanceof TvPayoutmsgmainDto) {// 实拨资金
				TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) idto;
				list = DatabaseFacade.getDb().find(TvPayoutmsgsubDto.class,
						" where  S_BIZNO = '" + dto.getSbizno() + "'");
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			log.info("查询数据异常" + e.getMessage());
			throw new ITFEBizException("查询数据异常:" + e.getCause().getMessage(), e);
		}
		return list;
	}

	public String getTBSNum() throws ITFEBizException {
		String tmpPackNo="";
			 try {
				tmpPackNo =  SequenceGenerator.getNextByDb2(SequenceName.TBS_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
			} catch (SequenceException e) {
				e.printStackTrace();
				log.info("生成批次号异常");
				throw new ITFEBizException("生成批次号异常:" + e.getCause().getMessage(), e);
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
			log.error("获取调拨标志异常");
			e1.printStackTrace();
		}
		return null;
	}

	public void updateVdtoStatus(List list) throws ITFEBizException {
		IDto idto = (IDto) list.get(0);
		String sql = new String();
		try {
			if (idto instanceof TvPayreckBankDto) {// 划款申请
				List<TvPayreckBankDto> dtolist = list;
				for(TvPayreckBankDto dto : dtolist){
					sql +="'" + dto.getIvousrlno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			} else if (idto instanceof TvGrantpaymsgmainDto) {// 授权支付
				List<TvGrantpaymsgmainDto> dtolist = list;
				for(TvGrantpaymsgmainDto dto : dtolist){
					sql +="'" + dto.getIvousrlno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			} else if (idto instanceof TvDirectpaymsgmainDto) {// 直接支付
				List<TvDirectpaymsgmainDto> dtolist = list;
				for(TvDirectpaymsgmainDto dto : dtolist){
					sql +="'" + dto.getIvousrlno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			} else if (idto instanceof TvPayoutmsgmainDto) {// 实拨资金
				List<TvPayoutmsgmainDto> dtolist = list;
				for(TvPayoutmsgmainDto dto : dtolist){
					sql +="'" + dto.getSbizno()+"'"+",";
				}
				DatabaseFacade.getDb().execSql("UPDATE TV_VOUCHERINFO SET S_STATUS='"+DealCodeConstants.VOUCHER_SENDED+"' WHERE S_DEALNO IN ("+sql.substring(0, sql.length()-1)+")");
				
			}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			log.info("更新凭证索引表数据状态错误！");
			throw new ITFEBizException("更新凭证索引表数据状态错误:" + e.getCause().getMessage(), e);
		}
		
	}

}