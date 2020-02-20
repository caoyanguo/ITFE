package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 直接支付额度转化
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5102 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5102.class);
	
	/**
	 * DTO转化XML报文(直接支付业务)
	 * 
	 * @param List
	 *            <TvDirectpaymsgmainDto> list 发送报文对象集合
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
	public static Map tranfor(List<TvDirectpaymsgmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
		if (null == list || list.size() == 0) {
			throw new ITFEBizException("要转化的对象为空,请确认!");
		}

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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5102);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

		// 设置报文消息体 MSG
		HashMap<String, Object> head5102Map = new HashMap<String, Object>();
		head5102Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head5102Map.put("BillOrg", list.get(0).getSpayunit()); // 出票单位
		head5102Map.put("TransBankNo", list.get(0).getStransbankcode()); // 转发银行
		head5102Map.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", "")); // 委托日期
		head5102Map.put("PackNo", list.get(0).getSpackageno()); // 包序号
		head5102Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head5102Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型
		
		List<Object> bill5102List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill5102Map = new HashMap<String, Object>();
			List<Object> detail5102List = new ArrayList<Object>();

			TvDirectpaymsgmainDto maindto = list.get(i);
			List sublist = new ArrayList();
		    if (maindto.getStrecode().startsWith("19") && !maindto.getStrecode().startsWith("1906")) {
		        SQLExecutor execDetail = null;
		        String sql = "SELECT ROW_NUMBER() OVER() AS I_DETAILSEQNO, SUM(N_MONEY) AS N_MONEY,S_BUDGETUNITCODE,S_FUNSUBJECTCODE FROM TV_DIRECTPAYMSGSUB WHERE I_VOUSRLNO='" + maindto.getIvousrlno() + 
		          "' GROUP BY S_BUDGETUNITCODE,S_FUNSUBJECTCODE order by S_BUDGETUNITCODE,S_FUNSUBJECTCODE";
		        try {
		          execDetail = DatabaseFacade.getODB()
		            .getSqlExecutorFactory().getSQLExecutor();
		          sublist = (List)execDetail.runQuery(sql.toString(), 
		            TvDirectpaymsgsubDto.class).getDtoCollection();
		        } catch (Exception e) {
		          if (execDetail != null)
		            execDetail.closeConnection();
		          logger.error(e.getMessage(), e);
		          throw new ITFEBizException(sql.toString() + "查询集中支付退款明细信息异常！", e);
		        } finally {
		          if (execDetail != null)
		            execDetail.closeConnection();
		        }
		      } else {
		         sublist = PublicSearchFacade.findSubDtoByMain(maindto);
		      }
		
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("要转化的对象对应的子记录为空,请确认![" + list.get(i).toString() + "]");
			}
			//拆分零余额的业务子表（上海扩展）
			sublist = MtoCodeTrans.transformZeroAmtSubDto(maindto, sublist);
			allamt = allamt.add(maindto.getNmoney());

			bill5102Map.put("TraNo", maindto.getSdealno()); // 交易流水号
//			if(orgcode.substring(0, 2).equals("11")){
//				if(maindto.getNmoney().compareTo(BigDecimal.ZERO)<0)
//					bill5102Map.put("VouNo", "-"+maindto.getSpackageticketno());
//				else
//					bill5102Map.put("VouNo", maindto.getSpackageticketno()); 
//			}else
//			{
				bill5102Map.put("VouNo", maindto.getSpackageticketno()); // 凭证编号
//			}
			bill5102Map.put("VouDate", maindto.getSgenticketdate().replaceAll("-", "")); // 凭证日期[开票日期]
			bill5102Map.put("SumAmt", MtoCodeTrans.transformString(maindto.getNmoney())); // 合计金额
			bill5102Map.put("BudgetType", maindto.getSbudgettype()); // 预算种类
			bill5102Map.put("OfYear", maindto.getSofyear()); // 所属年度
			bill5102Map.put("RransactOrg", MtoCodeTrans.transformString(maindto.getStransactunit())); // 经办单位
			bill5102Map.put("AmtKind", maindto.getSamttype()); // 额度种类
			bill5102Map.put("StatInfNum", sublist.size()); // 统计信息条数
			String s = "0";
			 try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("查找国库代码异常!", e);
				throw new ITFEBizException("查找国库代码异常！", e);
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,")&&(filename!=null&&filename.endsWith(".msg")))
				sublist = tranList(sublist);
			// 循环子信息
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail5102Map = new HashMap<String, Object>();
				TvDirectpaymsgsubDto subdto = (TvDirectpaymsgsubDto) sublist.get(j);
				detail5102Map.put("SeqNo", j+1);
				detail5102Map.put("BdgOrgCode", subdto.getSbudgetunitcode());
				detail5102Map.put("FuncBdgSbtCode", subdto.getSfunsubjectcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5102Map.put("EcnomicSubjectCode", subdto.getSecosubjectcode());
				}else{
					detail5102Map.put("EcnomicSubjectCode", "");
				}
				detail5102Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney()));
				detail5102List.add(detail5102Map);
			}

			bill5102Map.put("Detail5102", detail5102List);
			bill5102List.add(bill5102Map);

		}

		head5102Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5102", head5102Map);
		msgMap.put("Bill5102", bill5102List);

		/*if(!isRepeat){
			// 不是重发
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSorgcode(orgcode); // 机构代码
			packdto.setStrecode(list.get(0).getStrecode()); // 国库代码
			packdto.setSfilename(filename); // 导入文件名
			packdto.setStaxorgcode(list.get(0).getSpayunit()); // 机关代码
			packdto.setScommitdate(list.get(0).getScommitdate()); // 委托日期
			packdto.setSaccdate(TimeFacade.getCurrentStringTime()); // 账务日期
			packdto.setSpackageno(packno); // 包流水号
			packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN); // 业务类型
			packdto.setIcount(list.size()); // 总笔数
			packdto.setNmoney(allamt); // 总金额
			packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
			packdto.setSusercode(list.get(0).getSusercode()); // 操作员代码
			packdto.setImodicount(1);

			try {
				DatabaseFacade.getDb().create(packdto);
			} catch (JAFDatabaseException e) {
				logger.error("保存报文头的时候出现异常！", e);
				throw new ITFEBizException("保存报文头的时候出现异常！", e);
			}
		}*/
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	public static List<IDto> tranList(List<IDto> sublist)
	{
		List<IDto> returnlist = new ArrayList<IDto>();
		if(sublist!=null&&sublist.size()>0)
		{
			TvDirectpaymsgsubDto subdto = null;
			Map<String,TvDirectpaymsgsubDto> packMap = new HashMap<String,TvDirectpaymsgsubDto>();
			for(int i=0;i<sublist.size();i++)
			{
				subdto = (TvDirectpaymsgsubDto)sublist.get(i);
				subdto.setSbudgetunitcode(subdto.getSbudgetunitcode().substring(0, 7));
				if(packMap.get(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode())==null)
				{
					packMap.put(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode(), subdto);
				}else
				{
					packMap.get(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode()).setNmoney(packMap.get(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode()).getNmoney().add(subdto.getNmoney()));
				}
			}
			Set<String> set = packMap.keySet();
			for(String key:set)
				returnlist.add(packMap.get(key));
		}
		return returnlist;
	}
}

