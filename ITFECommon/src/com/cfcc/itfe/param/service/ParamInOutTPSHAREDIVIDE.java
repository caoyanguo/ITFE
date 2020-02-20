package com.cfcc.itfe.param.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TpShareDivideDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ParamInOutTPSHAREDIVIDE extends AbstractParamInOut {
	public static int seq = 1;
	public static int groupseq = 1;
	public static BigDecimal amt100 = new BigDecimal(0);
	public static int counter = 0;
	private static Log logger = LogFactory.getLog(ParamInOutTPSHAREDIVIDE.class);
	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		// if(amt100.intValue()==1){
		// amt100=new BigDecimal(0);
		// groupseq++;
		// }
		counter += 1;
		List<TpShareDivideDto> listtpshare = new ArrayList<TpShareDivideDto>();
		try {
//			TpShareDivideDto tpdto = (TpShareDivideDto) dto;
//			String sql = "SELECT * FROM Tp_Share_Divide Where  (S_BOOKORGCODE = ? ) AND (S_ROOTTRECODE = ? ) AND (S_ROOTTAXORGCODE = ? ) AND (S_ROOTBDGSBTCODE = ? ) AND (S_ROOTASTFLAG = ? ) ";
//			SQLExecutor sqlexec = DatabaseFacade.getODB()
//					.getSqlExecutorFactory().getSQLExecutor();
//			sqlexec.addParam(tpdto.getSbookorgcode());
//			sqlexec.addParam(tpdto.getSroottrecode());
//			sqlexec.addParam(tpdto.getSroottaxorgcode());
//			sqlexec.addParam(tpdto.getSrootbdgsbtcode());
//			if (StringUtils.isNotBlank(tpdto.getSrootastflag())) {
//				sqlexec.addParam(tpdto.getSrootastflag());
//			} else {
//				sqlexec.addParam("");
//			}
			listtpshare = (List<TpShareDivideDto>) CommonFacade.getODB().findRsByDto(dto);
//			listtpshare = (List<TpShareDivideDto>) sqlexec.runQueryCloseCon(
//					sql, TpShareDivideDto.class).getDtoCollection();
		} catch (Exception e) {
			logger.error("查询共享分成参数异常"+e);
//			throw new ITFEBizException("查看业务主表信息或更新凭证状态异常！",e);
		}
		if(listtpshare!=null && listtpshare.size()>0){
			for (TpShareDivideDto tsdto : listtpshare) {
				// TpShareDivideDto tsdto = (TpShareDivideDto) dto;
				// sb.append(groupseq + separator);
				sb.append(counter + separator);
				sb.append(tsdto.getSrootbdgsbtcode() + separator);
				sb.append(tsdto.getSrootastflag() + separator);
				if (tsdto.getSroottaxorgcode().equals("000000000000")
						|| tsdto.getSroottaxorgcode().equals("111111111111")
						|| tsdto.getSroottaxorgcode().equals("222222222222")
						|| tsdto.getSroottaxorgcode().equals("333333333333")
						|| tsdto.getSroottaxorgcode().equals("444444444444")
						|| tsdto.getSroottaxorgcode().equals("555555555555")) {
					sb.append(tsdto.getSroottaxorgcode().substring(0, 10)
							+ separator);
				} else {
					sb.append(tsdto.getSroottaxorgcode() + separator);
				}
				// sb.append(tsdto.getSroottaxorgcode() + separator);
				sb.append(tsdto.getSroottrecode() + separator);
				sb.append(tsdto.getSpayeetrecode() + separator);
				sb.append(tsdto.getCrootbdgkind() + separator);
				sb.append(tsdto.getCaftbdglevel() + separator);
				sb.append(tsdto.getSaftbdgsbtcode() + separator);
				sb.append(tsdto.getSaftastflag() + separator);
				if (tsdto.getSafttaxorgcode().equals("000000000000")
						|| tsdto.getSafttaxorgcode().equals("111111111111")
						|| tsdto.getSafttaxorgcode().equals("222222222222")
						|| tsdto.getSafttaxorgcode().equals("333333333333")
						|| tsdto.getSafttaxorgcode().equals("444444444444")
						|| tsdto.getSafttaxorgcode().equals("555555555555")) {
					sb.append(tsdto.getSafttaxorgcode().substring(0, 10)
							+ separator);
				} else {
					sb.append(tsdto.getSafttaxorgcode() + separator);
				}

				sb.append(tsdto.getSafttrecode() + separator);
				sb.append("" + separator);
				sb.append(tsdto.getFjoindividerate() + separator);
				sb.append("g" + separator);
				sb.append(seq++);
				// amt100=amt100.add(tsdto.getFjoindividerate());
				sb.append(System.getProperty("line.separator"));
			}
		}
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TpShareDivideDto tsdto = (TpShareDivideDto) dto;
		tsdto.setSbookorgcode(cond);
	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TpShareDivideDto tsdto = new TpShareDivideDto();
		tsdto.setSbookorgcode(cols[0]);
		if (filename.contains("正常期")) {
			tsdto.setCtrimflag("0");
		}else{
			tsdto.setCtrimflag("1");
		}
		tsdto.setIdividegrpid(1);

		tsdto.setSroottrecode(cols[1]);
		tsdto.setStratrecode(cols[2]);
		tsdto.setSpayeetrecode(cols[3]);
		tsdto.setSroottaxorgcode(cols[4]);
		tsdto.setSrootbdgsbtcode(cols[5]);
		tsdto.setCrootbdgkind(cols[6]);
		tsdto.setSrootastflag(cols[7]);
		tsdto.setSafttrecode(cols[8]);
		tsdto.setSafttaxorgcode(cols[9]);
		tsdto.setCaftbdglevel(cols[10]);
		tsdto.setSaftbdgsbtcode(cols[11]);
		tsdto.setCaftbdgtype(cols[12]);
		tsdto.setSaftastflag(cols[13]);
		tsdto.setFjoindividerate(new BigDecimal(cols[14]));
		tsdto.setCgovernralation(cols[15]);

		tsdto.setTssysupdate(TSystemFacade.getDBSystemTime());
		return tsdto;
	}

}
