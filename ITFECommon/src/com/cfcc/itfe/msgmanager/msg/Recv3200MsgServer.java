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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * 本工作日TIPS从某外联机构接收并转发的所有相关报文信息（报文编号涉及：5101、5102、5103）
 * 主要功能：核对从ITFE系统发送给TIPS包的信息
 * @author zhouchuan
 * 
 */
@SuppressWarnings("unchecked")
public class Recv3200MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3200MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		String msgRef  = (String)headMap.get("MsgRef");

		/**
		 * 解析信息头
		 */
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3200");
		String chkDate = (String) batchheadMap.get("ChkDate"); // 核对日期
		String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
		String billOrg = (String) batchheadMap.get("BillOrg");// 核对机构代码
//		String orgType = (String) batchheadMap.get("OrgType");// 机构类型
//		int allRcvNum = Integer.valueOf((String) (batchheadMap.get("AllRcvNum")));// 接收包数
//		int childPackNum = Integer.valueOf((String) batchheadMap.get("ChildPackNum")); // 子包总数
//		String curPackNo = (String) batchheadMap.get("CurPackNo");// 本包序号
		int curPackNum = Integer.valueOf((String) batchheadMap.get("CurPackNum")); // 本包笔数
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("CurPackAmt"));// 本包金额

		List returnList = (List) msgMap.get("CompDeduct3200");
		
		if (null == returnList || returnList.size() == 0) {
			
		} else {
				int count = returnList.size();
				for (int i = 0; i < count; i++) {
					HashMap returnmap = (HashMap) returnList.get(i);

					String orimsgno = (String) returnmap.get("OriMsgNo"); // 原报文编号
					String orisendorgcode = (String) returnmap.get("OriSendOrgCode"); // 原发起机构代码
					String orientrustdate = (String) returnmap.get("OriEntrustDate");// 原委托日期
					String oripackno = (String) returnmap.get("OriPackNo");// 原包流水号
//					int allnum = Integer.valueOf((String) (returnmap.get("AllNum")));// 总笔数
//					BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(returnmap.get("AllAmt"));// 总金额

//					String biztype = PublicSearchFacade.getBizTypeByMsgNo(orimsgno); // 业务类型
//					String sdemo = allnum + "_" + allAmt; // 备注：笔数+金额 
					//更新包对应关系表
					MsgRecvFacade.updateMsgHeadByMsgNo(orimsgno, orisendorgcode, oripackno, orientrustdate, DealCodeConstants.DEALCODE_ITFE_CHECK);
					//更新发送日志表
					MsgRecvFacade.updateSendLogByPackageNo(orimsgno,orisendorgcode,oripackno,orientrustdate,DealCodeConstants.DEALCODE_ITFE_CHECK);
				}
				
				/**
				 * 保存接收日志
				 */
				try {
					HashMap<String, TsConvertfinorgDto> map = SrvCacheFacade.cacheFincInfoByFinc(null);
					String sbookorgcode;
					if (null != map.get(billOrg)) {
						sbookorgcode = map.get(billOrg).getSorgcode();
					} else {
						sbookorgcode = ITFECommonConstant.SRC_NODE;
					}
					TvRecvlogDto recvlogdto = new TvRecvlogDto();
					recvlogdto.setSrecvno(StampFacade.getStampSendSeq("JS")); // 流水号
					recvlogdto.setSpackno(packNo); // 原包流水号
					recvlogdto.setSrecvorgcode(sbookorgcode); // 发送机构代码默认
					recvlogdto.setSsendorgcode((String) headMap.get("SRC ")); // 接收机构代码默认
					recvlogdto.setSdate(chkDate);// 所属日期-委托日期
					recvlogdto.setSoperationtypecode(MsgConstant.MSG_NO_3200);// 业务凭证类型
					recvlogdto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));// 发送时间
					recvlogdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIVER); // 处理结果
					recvlogdto.setSretcodedesc("接收对账包"); // 处理说明- 附言
					recvlogdto.setIcount(curPackNum);
					recvlogdto.setNmoney(curPackAmt);
					recvlogdto.setStitle((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
					recvlogdto.setSsenddate(TimeFacade.getCurrentStringTime());
					recvlogdto.setSseq((String) headMap.get("MsgID"));
					recvlogdto.setSifsend((String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER));
					recvlogdto.setSdemo(MsgConstant.LOG_ADDWORD_RECV_TIPS);
					DatabaseFacade.getDb().create(recvlogdto);
					// 查找原发送日志
					TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,MsgConstant.MSG_NO_9117);
					if (null!=senddto){
						//更新原发送日志流水号
						MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIVER, recvlogdto.getSrecvno(), "");
					}
					
				} catch (SequenceException e) {
					logger.error("取接收流水号失败!", e);
					throw new ITFEBizException("取接收流水号失败", e);
				} catch (JAFDatabaseException e) {
					logger.error("创建接收日志出现数据库异常!", e);
					throw new ITFEBizException("创建接收日志出现数据库异常!", e);
				}
		}
		eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		return;
	}

}
