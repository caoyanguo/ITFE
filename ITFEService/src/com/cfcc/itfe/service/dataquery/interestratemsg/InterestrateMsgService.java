package com.cfcc.itfe.service.dataquery.interestratemsg;

import java.util.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.itfe.service.dataquery.interestratemsg.AbstractInterestrateMsgService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.TfInterestParamDto;
import com.cfcc.itfe.persistence.dto.TfInterestrateMsgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * @time   14-09-23 17:40:57
 * codecomment: 
 */

public class InterestrateMsgService extends AbstractInterestrateMsgService {
	private static Log log = LogFactory.getLog(InterestrateMsgService.class);	

	/**
	 * interestRate	 
	 * @generated
	 * @param mainDto计息
	 * @return java.lang.Void
	 * @throws ITFEBizException	 
	 */
    public Void interestRate(TfInterestrateMsgDto interestrateMsgDto) throws ITFEBizException {
    	VoucherUtil.putVoucherDataToInteresDetail(this.getLoginInfo().getSorgcode());
    	SQLExecutor sqlExecutor = null;
    	try {
    		String sql = "";
    		String ls_DelSQL = "delete from TF_INTERESTRATE_MSG where S_ORGCODE = ? and S_YEAR = ? and S_QUARTER = ?  ";
			if(interestrateMsgDto!=null){
				List msgDtoList = new ArrayList();
				//生成利息数据 , ，b.S_INTERESTDATE <> S_LIQUIDATIONDATE日期需要在插入表TF_INTEREST_PARAM ，TF_INTEREST_DETAIL 时格式化成8位日期格式
//				sql="select a.S_QUARTER,a.S_STARTDATE,a.S_ENDDATE,a.N_INTEREST_RATES, b.S_ORGCODE,b.S_BANKTYPE ," +
//					"sum((DAYS(to_DATE(b.S_LIQUIDATIONDATE,'yyyymmdd'))-DAYS(to_DATE(b.S_INTERESTDATE,'yyyymmdd')))* b.N_MONEY)  AS N_MONEY,b.S_EXT2  " +
//					"from TF_INTEREST_PARAM a LEFT JOIN TF_INTEREST_DETAIL b  " +
//					"on a.S_QUARTER = ?  " +
//					"and a.S_ORGCODE = ?  " +
//					"AND b.S_LIQUIDATIONDATE <= a.S_ENDDATE  " +
//					"and  b.S_LIQUIDATIONDATE >= a.S_STARTDATE " +
//					"GROUP BY a.S_QUARTER,a.S_STARTDATE,a.S_ENDDATE,a.N_INTEREST_RATES, b.S_ORGCODE,b.S_BANKTYPE,b.S_EXT2";
//				sql = "SELECT b.S_QUARTER,b.S_STARTDATE,b.S_ENDDATE,a.S_ORGCODE,a.S_EXT2,a.S_BANKTYPE, sum((DAYS(to_DATE(a.S_LIQUIDATIONDATE,'yyyymmdd'))-DAYS(to_DATE(a.S_INTERESTDATE,'yyyymmdd')))* a.N_MONEY)  AS N_MONEY,a.S_VOUCHERTYPE   FROM TF_INTEREST_DETAIL a LEFT JOIN TF_INTEREST_PARAM b  ON a.S_EXT2 = b.S_EXT3  AND a.S_LIQUIDATIONDATE <= b.S_ENDDATE and  a.S_LIQUIDATIONDATE >= b.S_STARTDATE WHERE b.S_QUARTER = ? AND a.S_ORGCODE = ? GROUP BY b.S_QUARTER,b.S_STARTDATE,b.S_ENDDATE,a.S_BANKTYPE,b.S_EXT3,a.S_ORGCODE,a.S_EXT2,a.S_VOUCHERTYPE";
				sql = "SELECT S_QUARTER,S_STARTDATE,S_ENDDATE,S_ORGCODE,S_EXT2 AS s_trecode,S_BANKTYPE,sum(N_MONEY) AS  n_money ,S_VOUCHERTYPE FROM (SELECT DISTINCT  a.I_VOUSRLNO,b.S_QUARTER,b.S_STARTDATE,b.S_ENDDATE,a.S_ORGCODE,a.S_EXT2,a.S_BANKTYPE,a.N_MONEY ,a.S_VOUCHERTYPE FROM TF_INTEREST_DETAIL a LEFT JOIN TF_INTEREST_PARAM b  ON a.S_EXT2 = b.S_EXT3  AND a.S_LIQUIDATIONDATE <= b.S_ENDDATE and  a.S_LIQUIDATIONDATE >= b.S_STARTDATE WHERE b.S_QUARTER = ? AND a.S_ORGCODE = ? ) c GROUP BY S_QUARTER,S_STARTDATE,S_ENDDATE,S_ORGCODE,S_EXT2,S_BANKTYPE,S_VOUCHERTYPE";
				sqlExecutor  = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlExecutor.addParam(interestrateMsgDto.getSquarter());
				sqlExecutor.addParam(this.getLoginInfo().getSorgcode());
				SQLResults result = sqlExecutor.runQuery(sql);
				Map<String, Double> interestrateMap = getInterestrateMap();
				for(int i=0;i<result.getRowCount();i++){
					if(interestrateMsgDto.getSquarter().equals(result.getString(i, 0))){
						TfInterestrateMsgDto msgDto = new TfInterestrateMsgDto();
						//获取凭证序列号
						String vouno = SequenceGenerator.getNextByDb2(SequenceName.GRANTPAY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
						msgDto.setIvousrlno(Long.parseLong(vouno));
						msgDto.setSyear(interestrateMsgDto.getSyear());
						msgDto.setSquarter(result.getString(i, "S_QUARTER"));
						msgDto.setSstartdate(result.getString(i, "S_STARTDATE"));
						msgDto.setSenddate(result.getString(i, "S_ENDDATE"));
						msgDto.setSbanktype(result.getString(i, "S_BANKTYPE"));
						
						
						Double rate = interestrateMap.get(result.getString(i,"s_trecode").trim() + msgDto.getSbanktype() + msgDto.getSquarter());
//						Double rate = result.getDouble(i, 3);
						if(rate==null)
							rate = interestrateMap.get(result.getString(i, "s_trecode").trim() + "default" + msgDto.getSquarter().trim());
						
						
						
						Double money = result.getDouble(i, "n_money");
						if(money==null)
							money = Double.valueOf(0.0);
						msgDto.setNinterestratecount(BigDecimal.valueOf(money));
						msgDto.setNinterestrates(BigDecimal.valueOf(rate));
						Double rateValue  = money*rate/36000;
						msgDto.setNinterestvalue(new BigDecimal(rateValue).setScale(2, BigDecimal.ROUND_HALF_UP));
						msgDto.setSorgcode(this.getLoginInfo().getSorgcode());
						msgDto.setSuesrcode(this.getLoginInfo().getSuserCode());
						msgDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
						msgDto.setSext3(result.getString(i, "s_trecode").trim());
						msgDto.setSstatus(result.getString(i, "S_VOUCHERTYPE"));
						msgDtoList.add(msgDto);
					}
				}
				if(msgDtoList.size()>0){
					sqlExecutor.clearParams();
					sqlExecutor.addParam(this.getLoginInfo().getSorgcode());
					sqlExecutor.addParam(interestrateMsgDto.getSyear());
					sqlExecutor.addParam(interestrateMsgDto.getSquarter());
					sqlExecutor.runQuery(ls_DelSQL);
					//查询出的记录插入计息统计信息表TF_INTERESTRATE_MSG
					DatabaseFacade.getODB().create(CommonUtil.listTArray(msgDtoList));		
				}
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			 throw new ITFEBizException("查询利息参数表异常！",e);
		} catch (SequenceException e) {
			log.error(e);
			 throw new ITFEBizException("获取序列号出现异常！",e);
		}finally{
			if(sqlExecutor!=null)
				sqlExecutor.closeConnection();
		}
      return null;
    }
    
    /**
     * 按国库+行别+季度  获取利率
     * 默认利率由 国库+default
     * @return
     * @throws ITFEBizException 
     */
    private Map<String, Double> getInterestrateMap() throws ITFEBizException{
    	try {
			List<TfInterestParamDto> result = CommonFacade.getODB().findRsByDto(new TfInterestParamDto());
			if(null == result || result.size() == 0){
				throw new ITFEBizException("利率参数未维护，请先维护利率参数！");
			}
			Map<String, Double> interestrateMap = new HashMap<String, Double>();
			for(TfInterestParamDto param : result){
				if(StringUtils.isNotBlank(param.getSext2())){
					interestrateMap.put(param.getSext3().trim() + param.getSext2().trim() + param.getSquarter().trim(), param.getNinterestrates().doubleValue());
				}else{
					interestrateMap.put(param.getSext3().trim() + "default" + param.getSquarter(), param.getNinterestrates().doubleValue());
				}
			}
			return interestrateMap;
		} catch (JAFDatabaseException e) {
			log.error("获取利率缓存失败！",e);
			throw new ITFEBizException("获取利率缓存失败！",e);
		} catch (ValidateException e) {
			log.error("获取利率缓存失败！",e);
			throw new ITFEBizException("获取利率缓存失败！",e);
		}
    }

}