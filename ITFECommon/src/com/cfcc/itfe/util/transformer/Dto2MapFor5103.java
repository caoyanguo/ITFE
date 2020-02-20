package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 直接支付额度转化
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5103 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5103.class);

	/**
	 * DTO转化XML报文(授权支付业务)
	 * 
	 * @param List
	 *            <TvGrantpaymsgmainDto> list 发送报文对象集合
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            packno 包流水号
	 * @param boolean isRepeat 是否重发报文
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public static Map tranfor(List<TvGrantpaymsgmainDto> list, String orgcode,
			String filename, String packno, boolean isRepeat)
			throws ITFEBizException {
		if (null == list || list.size() == 0) {
			throw new ITFEBizException("要转化的对象为空,请确认!");
		}

		// if (list.size() != 1) {
		// throw new ITFEBizException("要转化的对象重复,请确认!");
		// }

		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// 报文头 HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_5103);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}


		
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		int allcount = 0; // 总比数
		HashMap<String, Object> head5103Map = new HashMap<String, Object>();
		List<Object> bill5103List = new ArrayList<Object>();

		for (int i = 0; i < list.size(); i++) {
			allcount++;
			TvGrantpaymsgmainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("要转化的对象对应的子记录为空,请确认!["
						+ maindto.getSlimitid() + "_" + maindto.getSofyear()
						+ "]");
			}
			
			// 设置报文消息体 MSG
			head5103Map.put("TreCode", maindto.getStrecode()); // 国库代码
			head5103Map.put("BillOrg", maindto.getSpayunit()); // 出票单位
			head5103Map.put("TransBankNo", maindto.getStransbankcode()); // 转发银行
			head5103Map.put("EntrustDate", maindto.getScommitdate().replaceAll(
					"-", "")); // 委托日期
			head5103Map.put("PackNo", maindto.getSpackageno()); // 包序号
			
			// 支出凭证类型 0无纸凭证1有纸凭证
			head5103Map.put("PayoutVouType",
					MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES);
			
			/**
			 *  设置Bill5103
			 */
			HashMap<String, Object> bill5103Map = new HashMap<String, Object>();
			// 交易流水号
			bill5103Map.put("TraNo", maindto.getSdealno()); 
			// 凭证编号
			if(!orgcode.substring(0, 2).equals("11")){
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")<0){
					bill5103Map.put("VouNo", maindto.getSpackageticketno()); 
				}else{
					bill5103Map.put("VouNo", !(maindto.getShold2()!=null&&maindto.getShold2().equals("已拆分业务主表标志"))?maindto.getSpackageticketno():maindto.getShold1()); 
				} 
			}
			else
			{
				if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("rev5106_")&&maindto.getSfilename().contains(".msg"))
					bill5103Map.put("VouNo", maindto.getSpackageticketno());
				else
				{
					TvGrantpaymsgsubDto sub = (TvGrantpaymsgsubDto) sublist.get(0);
					bill5103Map.put("VouNo", sub.getSpackageticketno() + sub.getSline());
				}
			}
			// 凭证日期
			bill5103Map.put("VouDate", maindto.getSgenticketdate()
					.replaceAll("-", "")); 
			// 预算单位代码
			if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,")&&(filename!=null&&filename.endsWith(".msg")))
				bill5103Map.put("BdgOrgCode", maindto.getSbudgetunitcode().substring(0,7)); 
			else
				bill5103Map.put("BdgOrgCode", maindto.getSbudgetunitcode()); 
			// 预算种类
			bill5103Map.put("BudgetType", maindto.getSbudgettype()); 
			// 所属年度
			bill5103Map.put("OfYear", maindto.getSofyear()); 
			// 所属月份
			String ofmonth = DateUtil.getFormatMonth(Integer
					.parseInt(maindto.getSofmonth()));
			bill5103Map.put("OfMonth", ofmonth); 
			// 经办单位
			bill5103Map.put("RransactOrg", MtoCodeTrans
					.transformString(maindto.getStransactunit()));
			// 额度种类
			bill5103Map.put("AmtKind", maindto.getSamttype()); 
			//票据金额合计
			BigDecimal billamt = new BigDecimal("0.00"); 
			
			String s = "0";
			try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("查找国库代码异常!", e);
				throw new ITFEBizException("查找国库代码异常！", e);
			}
			
			//拆分零余额的业务子表（上海扩展）
			sublist = MtoCodeTrans.transformZeroAmtSubDto(maindto, sublist);
			
			List<Object> detail5103List = new ArrayList<Object>();
			for (int j = 0; j < sublist.size(); j++) {
				TvGrantpaymsgsubDto subdto = (TvGrantpaymsgsubDto) sublist
						.get(j);
				
				allamt = allamt.add(subdto.getNmoney());
				billamt = billamt.add(subdto.getNmoney());

				// 循环子信息
				HashMap<String, Object> detail5103Map = new HashMap<String, Object>();

				detail5103Map.put("SeqNo", j+1);
				detail5103Map.put("FuncBdgSbtCode", subdto.getSfunsubjectcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5103Map.put("EcnomicSubjectCode", MtoCodeTrans.transformString(subdto.getSecosubjectcode()));
				}else{
					detail5103Map.put("EcnomicSubjectCode", "");
				}
				detail5103Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney()));
				detail5103Map.put("AcctProp", subdto.getSaccattrib());
				detail5103List.add(detail5103Map);

				bill5103Map.put("Detail5103", detail5103List);
			}
			// 统计信息条数
			bill5103Map.put("StatInfNum", sublist.size());
			// 合计金额
			bill5103Map.put("SumAmt", MtoCodeTrans.transformString(billamt)); 
			bill5103List.add(bill5103Map);
		}
		head5103Map.put("AllNum", String.valueOf(allcount)); // 总笔数
		head5103Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5103", head5103Map);
		msgMap.put("Bill5103", bill5103List);

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
