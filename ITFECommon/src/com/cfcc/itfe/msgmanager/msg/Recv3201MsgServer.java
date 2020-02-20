package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvTips3201Dto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;

/**
 * 本工作日TIPS向某外联机构转发的所有相关报文信息（报文编号涉及：3131、3133、3134） 主要功能：核对从TIPS系统发送给ITFE系统包的信息
 * 
 * @author zhouchuan
 * 
 */
@SuppressWarnings("unchecked")
public class Recv3201MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3201MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		/**
		 * 解析信息头
		 */
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3201");
		String chkDate = (String) batchheadMap.get("ChkDate"); // 核对日期
		String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
		String billOrg = (String) batchheadMap.get("BillOrg");// 核对机构代码
		String orgType = (String) batchheadMap.get("OrgType");// 机构类型
//		int allRcvNum = Integer.valueOf((String) (batchheadMap
//				.get("AllSendNum")));// 发送包数
		int childPackNum = Integer.valueOf((String) batchheadMap
				.get("ChildPackNum")); // 子包总数
//		String curPackNo = (String) batchheadMap.get("CurPackNo");// 本包序号
//		String curPackNum = (String) batchheadMap.get("CurPackNum"); // 本包笔数
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("CurPackAmt"));// 本包金额
		List<TvRecvlogDto> finddtolist;
		HashMap<String, TsConvertfinorgDto> map;
		String sbookorgcode;
		try {
			map = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (null != map.get(billOrg)) {
				sbookorgcode = map.get(billOrg).getSorgcode();
			} else {
				sbookorgcode = ITFECommonConstant.SRC_NODE;
			}
			TvRecvlogDto logDto = new TvRecvlogDto();
			logDto.setSrecvorgcode(sbookorgcode);
			logDto.setSdate(chkDate);
			logDto.setSpackno(packNo);
			logDto.setSoperationtypecode(MsgConstant.MSG_NO_3201);
			finddtolist = CommonFacade.getODB().findRsByDto(logDto);
			if (finddtolist.size() == 0) {
				List returnList = (List) msgMap.get("CompDeduct3201");
				if (null == returnList || returnList.size() == 0) {
				} else {
					int count = returnList.size();
					for (int i = 0; i < count; i++) {
						HashMap returnmap = (HashMap) returnList.get(i);

						String orimsgno = (String) returnmap.get("OriMsgNo"); // 原报文编号
						String orisendorgcode = (String) returnmap
								.get("OriSendOrgCode"); // 原发起机构代码
						String orientrustdate = (String) returnmap
								.get("OriEntrustDate");// 原委托日期
						String oripackno = (String) returnmap.get("OriPackNo");// 原包流水号
						int allnum = Integer.valueOf((String) (returnmap
								.get("AllNum")));// 总笔数
						BigDecimal allAmt = MtoCodeTrans
								.transformBigDecimal(returnmap.get("AllAmt"));// 总金额
//
//						String biztype = PublicSearchFacade
//								.getBizTypeByMsgNo(orimsgno); // 业务类型
//						String sdemo = allnum + "_" + allAmt; // 备注：笔数+金额

						// 保存3201信息
						TvTips3201Dto tv3201dto = new TvTips3201Dto();
						// 所属机构代码
						tv3201dto.setSorgcode(sbookorgcode);
						// 核对日期
						tv3201dto.setSchkdate(chkDate);
						// 包流水号
						tv3201dto.setSpackno(packNo);
						// 核对机构代码
						tv3201dto.setSbillorg(orgType);
						// 机构类型
						tv3201dto.setSorgtype(orgType);
						// 原报文编号
						tv3201dto.setSorimsgno(orimsgno);
						// 原发起机构代码
						tv3201dto.setSorisendorgcode(orisendorgcode);
						// 原委托日期
						tv3201dto.setSorientrustdate(orientrustdate);
						// 原包流水号
						tv3201dto.setSoripackno(oripackno);
						// 总笔数
						tv3201dto.setIallnum(allnum);
						// 总金额
						tv3201dto.setFallamt(allAmt);
						// 是否核对
						tv3201dto.setScheck(StateConstant.CANCEL_FLAG_NOCHECK);
						// 保存数据
						DatabaseFacade.getDb().create(tv3201dto);

						// 更新包对应关系表
						MsgRecvFacade.updateMsgHeadByMsgNofor3201(
								orisendorgcode, oripackno, orientrustdate,
								DealCodeConstants.DEALCODE_ITFE_CHECK);
						// 更新发送日志表
						MsgRecvFacade.updateRecvLogByPackageNo(orimsgno,
								orisendorgcode, oripackno, orientrustdate,
								DealCodeConstants.DEALCODE_ITFE_CHECK);
					}
				}
			}
			/**
			 * 保存接收日志
			 */

			TvRecvlogDto recvlogdto = new TvRecvlogDto();
			recvlogdto.setSrecvno(StampFacade.getStampSendSeq("JS")); // 流水号
			recvlogdto.setSpackno(packNo); // 原包流水号
			recvlogdto.setSrecvorgcode(sbookorgcode); // 发送机构代码默认
			recvlogdto.setSsendorgcode((String) headMap.get("SRC")); // 接收机构代码默认
			recvlogdto.setSdate(chkDate);// 所属日期-委托日期
			recvlogdto.setSoperationtypecode(MsgConstant.MSG_NO_3201);// 业务凭证类型
			recvlogdto.setSrecvtime(new Timestamp(new java.util.Date()
					.getTime()));// 发送时间
			recvlogdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIVER); // 处理结果
			recvlogdto.setSretcodedesc(""); // 处理说明- 附言
			recvlogdto.setIcount(childPackNum);
			recvlogdto.setNmoney(curPackAmt);
			recvlogdto.setStitle((String) eventContext.getMessage()
					.getProperty("XML_MSG_FILE_PATH"));
			recvlogdto.setSsenddate(TimeFacade.getCurrentStringTime());
			recvlogdto.setSseq((String) headMap.get("MsgID"));
			recvlogdto.setSifsend((String) eventContext.getMessage()
					.getProperty(MessagePropertyKeys.MSG_SENDER));
			recvlogdto.setSdemo(MsgConstant.LOG_ADDWORD_RECV_TIPS
					+ MsgConstant.MSG_NO_3201);
			DatabaseFacade.getDb().create(recvlogdto);

		} catch (Exception e) {
			logger.error("接收3201报文出现异常!", e);
			throw new ITFEBizException("接收3201报文出现异常!", e);
		}
		eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		return;
	}
}
