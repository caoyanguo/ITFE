package com.cfcc.itfe.service.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author 曹艳国 实拨资金
 * @time 12-02-21 08:45:49 批量销号，逐笔销号，直接提交，一些判断等
 */

public class EachPayOutSalaryCommitUtil {
	private static Log logger = LogFactory
			.getLog(EachPayOutSalaryCommitUtil.class);

	/**
	 * 实拨资金(福建工资文件)：逐笔销号-确认提交
	 * 
	 * @param bizType
	 * @param fileName
	 * @throws ITFEBizException
	 */
	public static boolean confirmPayout(String bizType, IDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult;
		SQLExecutor sqlExec = null;
		try {
			TbsTvPayoutDto tmpdto = (TbsTvPayoutDto) idto;
			tmpdto.setFamt(null);
			List<TbsTvPayoutDto> list = CommonFacade.getODB().findRsByDto(tmpdto);
			if(null == list || list.size() == 0){
				return false;
			}
			/**
			 * 判断是否维护财政机构信息
			 */
			bResult = CheckBizParam.checkConvertFinOrg(bizType, list.get(0));
			if (!bResult) {
				return false;
			}
			/**
			 * 判断收款行行号是否为空
			 */
			bResult = CheckBizParam.checkPayeeBanknoForEach(bizType, list.get(0), loginfo);
			if (!bResult) {
				return false;
			}
			TvPayoutmsgmainDto maindto ;
			TvPayoutmsgsubDto subdto;
			Map<String, ArrayList<TvPayoutmsgsubDto>> subdtomap = new HashMap<String, ArrayList<TvPayoutmsgsubDto>>();
			Map<String, TvPayoutmsgmainDto> mainmap = new HashMap<String, TvPayoutmsgmainDto>();
			Map<String, TbsTvPayoutDto> tbspayoutdtomap = new HashMap<String, TbsTvPayoutDto>();
			String sql ;
			SQLResults sqlResults ;
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			List<String> keys = new ArrayList<String>();
			for(TbsTvPayoutDto dto : list){
				if(!mainmap.containsKey(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason())){
					keys.add(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason());
					tbspayoutdtomap.put(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason(), dto);
					maindto = new TvPayoutmsgmainDto();
					maindto.setSbizno(String.valueOf(dto.getIvousrlno()).replaceAll(",", ""));
					maindto.setSorgcode(dto.getSbookorgcode());
					maindto.setScommitdate(DateUtil.date2String2(dto.getDaccept()));
					maindto.setSaccdate(DateUtil.date2String2(dto.getDaccept()));
					maindto.setSfilename(dto.getSfilename());
					maindto.setStrecode(dto.getStrecode());
					maindto.setSpackageno(dto.getSpackageno());
					//出票单位
					sql = " SELECT Max(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=? AND S_TRECODE=? ";
					sqlExec.clearParams();
					sqlExec.addParam(dto.getSbookorgcode());
					sqlExec.addParam(dto.getStrecode());
					sqlResults = sqlExec.runQuery(sql);
					if(null == sqlResults || sqlResults.getRowCount() == 0){
						return false;
					}
					maindto.setSpayunit(sqlResults.getString(0, 0));	//出票单位
					maindto.setSpayeebankno(dto.getSpayeebankno());	//转发银行
					sqlExec.clearParams();
					sql = "values substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8)";
					sqlResults = sqlExec.runQuery(sql);
					maindto.setSdealno(sqlResults.getString(0, 0));	//交易流水号
					maindto.setStaxticketno(dto.getSvouno());	//税票号码
					maindto.setSgenticketdate(DateUtil.date2String2(dto.getDvoucher()));	//开票日期
					maindto.setSpayeracct(dto.getSpayeracct());	//付款人帐号
					maindto.setSpayername(dto.getSpayername());	//付款人名称	
					maindto.setNmoney(dto.getFamt());	//交易金额
					maindto.setSrecbankno(dto.getSpayeeopnbnkno());	//收款人开户行名称
					maindto.setSrecacct(dto.getSpayeeacct());	//收款人帐号
					maindto.setSrecname(dto.getSpayeename());	//收款人名称
					maindto.setStrimflag(dto.getCtrimflag());	//调整期标志
					maindto.setSbudgetunitcode(dto.getSbdgorgcode());
					maindto.setSunitcodename(dto.getSbdgorgname());
					maindto.setSofyear(String.valueOf(dto.getIofyear()).trim().replaceAll(",", ""));
					maindto.setSbudgettype(dto.getCbdgkind());
					maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
					maindto.setSusercode(loginfo.getSuserCode());
					maindto.setSaddword(dto.getSmovefundreason());
					maindto.setSdemo(dto.getSaddword());
					maindto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
					mainmap.put(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason(), maindto);
					
					subdto = new TvPayoutmsgsubDto();
					subdto.setSbizno(maindto.getSbizno());
//					sqlExec.clearParams();
//					sql = "values nextval FOR ITFE_TRAID_SEQ";
//					sqlResults = sqlExec.runQuery(sql);
					subdto.setSaccdate(DateUtil.date2String2(dto.getDaccept()));
					subdto.setSecnomicsubjectcode(dto.getSecosbtcode());
					subdto.setNmoney(dto.getFamt());
					subdto.setSfunsubjectcode(dto.getSfuncsbtcode());
					subdto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
					ArrayList<TvPayoutmsgsubDto> tmplist = new ArrayList<TvPayoutmsgsubDto>();
					tmplist.add(subdto);
					subdto.setSseqno(1);
					subdtomap.put(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason(), tmplist);
				}else{
					maindto = mainmap.get(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason());
					maindto.setNmoney(maindto.getNmoney().add(dto.getFamt()));
					subdto = new TvPayoutmsgsubDto();
					subdto.setSbizno(maindto.getSbizno());
//					sql = "values nextval FOR ITFE_TRAID_SEQ";
//					sqlResults = sqlExec.runQuery(sql);
//					subdto.setSseqno(sqlResults.getInt(0, 0));
					subdto.setSaccdate(DateUtil.date2String2(dto.getDaccept()));
					subdto.setSecnomicsubjectcode(dto.getSecosbtcode());
					subdto.setNmoney(dto.getFamt());
					subdto.setSfunsubjectcode(dto.getSfuncsbtcode());
					subdto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
					subdto.setSseqno(subdtomap.get(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason()).size() + 1);
					subdtomap.get(dto.getSvouno() + dto.getSpayeeopnbnkno() + dto.getSbdgorgcode() + dto.getSpayeeacct() + dto.getSmovefundreason()).add(subdto);
					
				}
			}
			String movedataSql = "UPDATE TBS_TV_PAYOUT SET S_STATUS=?"
							+ " WHERE  S_BOOKORGCODE = ? AND S_VOUNO = ?  AND S_PAYEEOPNBNKNO = ?  AND S_BDGORGCODE = ? AND S_PAYEEACCT = ? AND S_MOVEFUNDREASON = ?  ";
			
			for(String key : keys){
				maindto = mainmap.get(key);
				//取得同一凭证编号下最小序号的科目代码最为汇总后主信息的科目代码（福建工资导入要求）
				String querySubCodeSql = "SELECT S_FUNCSBTCODE FROM TBS_TV_PAYOUT WHERE S_BOOKORGCODE = ? AND S_VOUNO = ?  AND S_PAYEEOPNBNKNO = ?  AND S_BDGORGCODE = ? AND S_PAYEEACCT = ? AND S_MOVEFUNDREASON = ?  " 
					+ "AND S_GROUPID = ( SELECT MIN(S_GROUPID) FROM TBS_TV_PAYOUT WHERE S_BOOKORGCODE = ? AND S_VOUNO = ?  AND S_PAYEEOPNBNKNO = ?  AND S_BDGORGCODE = ? AND S_PAYEEACCT = ? AND S_MOVEFUNDREASON = ? )";
				sqlExec.clearParams();
				sqlExec.addParam(maindto.getSorgcode());
				sqlExec.addParam(maindto.getStaxticketno());
				sqlExec.addParam(maindto.getSrecbankno());
				sqlExec.addParam(maindto.getSbudgetunitcode());
				sqlExec.addParam(maindto.getSrecacct());
				sqlExec.addParam(maindto.getSaddword());
				sqlExec.addParam(maindto.getSorgcode());
				sqlExec.addParam(maindto.getStaxticketno());
				sqlExec.addParam(maindto.getSrecbankno());
				sqlExec.addParam(maindto.getSbudgetunitcode());
				sqlExec.addParam(maindto.getSrecacct());
				sqlExec.addParam(maindto.getSaddword());
				String funcstcode = sqlExec.runQuery(querySubCodeSql).getString(0, 0);
				//将查询出的科目代码设置为主信息的科目代码
				maindto.setSdemo(funcstcode);
				//设置退款标致为"0"，即未退款，否则地方横练文件导入后报空指针异常
				maindto.setSbackflag("0");
				//添加进数据库
				DatabaseFacade.getODB().create(maindto);
				List<TvPayoutmsgsubDto> subList=subdtomap.get(key);
				List<IDto> lists=new ArrayList<IDto>();
				if(subList.size()!=0&&subList.size()>499){
					//汇总统计每条凭证下的明细数量（不能大于499笔）
					Map<String,BigDecimal> grantmap = new HashMap<String, BigDecimal>();
					for (IDto dto2 : subList) {
						TvPayoutmsgsubDto dto=(TvPayoutmsgsubDto) dto2;
						if(grantmap.containsKey(dto.getSfunsubjectcode()+","+dto.getSecnomicsubjectcode())) { //对相同功能代码、经济代码进行汇总
							grantmap.put(dto.getSfunsubjectcode()+","+dto.getSecnomicsubjectcode(), grantmap.get(dto.getSfunsubjectcode()+","+dto.getSecnomicsubjectcode()).add(dto.getNmoney()));
						}else {
							grantmap.put(dto.getSfunsubjectcode()+","+dto.getSecnomicsubjectcode(), dto.getNmoney());
						}
					}
					if(grantmap.size()!=0&&grantmap.size()>499){
						logger.debug("工资拨付文件["+maindto.getSfilename()+"]中，凭证编号为["+maindto.getStaxticketno().toString()+"]的明细信息汇总后，总笔数为["+grantmap.size()+"],超过499笔，请手工拆包后导入!");
						return false;
					}
					int count=1;
					for (String son : grantmap.keySet()) {
						String[] str=son.split(",");
						TvPayoutmsgsubDto subdto1 = new TvPayoutmsgsubDto();
						subdto1.setSbizno(maindto.getSbizno());
						subdto1.setSaccdate(maindto.getSaccdate());
						if(str.length==1){
							subdto1.setSecnomicsubjectcode("");
						}else{
							subdto1.setSecnomicsubjectcode(str[1]);
						}
						subdto1.setNmoney(grantmap.get(son));
						subdto1.setSfunsubjectcode(str[0]);
						subdto1.setTssysupdate(new Timestamp(System.currentTimeMillis()));
						subdto1.setSseqno(count);
						count++;
						lists.add(subdto1);
					}
					DatabaseFacade.getODB().create(CommonUtil.listTArray(lists));
				}else{
					DatabaseFacade.getODB().create(subdtomap.get(key).toArray(new IDto[subdtomap.get(key).size()]));
				}
				sqlExec.clearParams();
				sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
				sqlExec.addParam(maindto.getSorgcode());
				sqlExec.addParam(maindto.getStaxticketno());
				sqlExec.addParam(maindto.getSrecbankno());
				sqlExec.addParam(maindto.getSbudgetunitcode());
				sqlExec.addParam(maindto.getSrecacct());
				sqlExec.addParam(maindto.getSaddword());
				sqlExec.runQuery(movedataSql);
				// 调用发送报文
				sendMsgUtil.checkAndSendMsg(tbspayoutdtomap.get(key),
						MsgConstant.MSG_NO_5101, TbsTvPayoutDto.tableName(), null,loginfo);
			}
		} catch (Throwable e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}
}