package com.cfcc.itfe.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor1000_1104;
import com.cfcc.itfe.util.transformer.Dto2MapFor1000_2201;
import com.cfcc.itfe.util.transformer.Dto2MapFor1000_5101;
import com.cfcc.itfe.util.transformer.Dto2MapFor1104;
import com.cfcc.itfe.util.transformer.Dto2MapFor11042;
import com.cfcc.itfe.util.transformer.Dto2MapFor1105;
import com.cfcc.itfe.util.transformer.Dto2MapFor11052;
import com.cfcc.itfe.util.transformer.Dto2MapFor2201;
import com.cfcc.itfe.util.transformer.Dto2MapFor2202;
import com.cfcc.itfe.util.transformer.Dto2MapFor5101;
import com.cfcc.itfe.util.transformer.Dto2MapFor5101ForBatch;
import com.cfcc.itfe.util.transformer.Dto2MapFor5102;
import com.cfcc.itfe.util.transformer.Dto2MapFor5103;
import com.cfcc.itfe.util.transformer.Dto2MapFor5112;
import com.cfcc.itfe.util.transformer.Dto2MapFor5408;
import com.cfcc.itfe.util.transformer.Dto2MapFor5408_JL;
import com.cfcc.itfe.util.transformer.Dto2MapFor5671;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor2201;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor2202;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor5104;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;
@SuppressWarnings("unchecked")
public class VoucherSendTipsMessageComponent implements Callable {
	private static Log logger = LogFactory
			.getLog(VoucherSendTipsMessageComponent.class);
	private Map splitXmlMap = null;

	public Object onCall(MuleEventContext eventContext) throws Exception {
		eventContext.getMessage().setStringProperty(
				MuleProperties.MULE_REPLY_TO_STOP_PROPERTY, "true");
		eventContext.getMessage().setReplyTo(null);
		// 接收报文时调用配置的插件进行转化
		eventContext.transformMessage();
		String packageno = (String) eventContext.getMessage().getProperty(
				"packno");
		String commitdate = TimeFacade.getCurrentStringTime(); // 当前系统日期
		String trasrlsql = "select * from TV_FILEPACKAGEREF where S_RETCODE = ? And S_PACKAGENO = ? and S_COMMITDATE = ? ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec
					.addParam(DealCodeConstants.DEALCODE_VOUCHER_ITFE_WAIT_DEALING);
			sqlExec.addParam(packageno);
			sqlExec.addParam(commitdate);
			SQLResults fileSrlnoRs = sqlExec.runQueryCloseCon(trasrlsql,
					TvFilepackagerefDto.class);
			List<TvFilepackagerefDto> dtoList = (List<TvFilepackagerefDto>) fileSrlnoRs
					.getDtoCollection();
			if (dtoList != null && dtoList.size() > 0) {
				for (int i = 0; i < dtoList.size(); i++) {
					TvFilepackagerefDto tmppackdto = dtoList.get(i);
					Map map = new HashMap();
					MuleMessage message = new DefaultMuleMessage(map);
					String orgcode = tmppackdto.getSorgcode();
					String trecode = tmppackdto.getStrecode();
					String filename = tmppackdto.getSfilename();
					String packno = tmppackdto.getSpackageno();
					commitdate = tmppackdto.getScommitdate();
					String operationType = tmppackdto.getSoperationtypecode();
					String ls_TreCode = tmppackdto.getStrecode();
					String ls_BillOrg = tmppackdto.getStaxorgcode();// 此处出票单位是从包对应关系表的征收机关代码字段中取出
					Map xmlMap = null;
					Boolean MsgNo5207Batch =Boolean.FALSE;

					// 根据ls_TreCode判断是不是为代理库
					HashMap<String, TsTreasuryDto> mapTreCode = SrvCacheFacade
							.cacheTreasuryInfo(orgcode);
					if (mapTreCode.containsKey(ls_TreCode)) {
						TsTreasuryDto treDto = mapTreCode.get(ls_TreCode);
						String ls_treattrib = treDto.getStreattrib();
						String ls_PbcCode = treDto.getSofcountrytrecode();// 所属县国库代码，设置为代理银行行别代码
						String isbatch = String.valueOf(treDto==null?"":treDto.getSpayunitname());
						
						if (StateConstant.COMMON_YES.equals(isbatch) && BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(operationType) ) {
							   MsgNo5207Batch = Boolean.TRUE;
							  List msgMapList = detailSendMsg(eventContext, orgcode, ls_TreCode, filename, packno, commitdate);
							  for (int j = 0; j < msgMapList.size(); j++) {
								  xmlMap = (Map) msgMapList.get(j);
									String ls_MsgId = (String) ((Map) ((Map) xmlMap.get("cfx"))
											.get("HEAD")).get("MsgID");
									Map msg = (Map) xmlMap.get("cfx");
									Map batchHead5101 = (Map) ((Map) msg.get("MSG"))
											.get("BatchHead5101");
									BigDecimal amt = new BigDecimal(batchHead5101.get("AllAmt")
											.toString());
									String subpackno = batchHead5101.get("PackNo").toString();
									String payunit = batchHead5101.get("BillOrg").toString();
									int num = Integer.valueOf(batchHead5101.get("AllNum")
											.toString());
									// 写发送日志
									message.setProperty(
											MessagePropertyKeys.MSG_SEND_LOG_DTO,
											MsgLogFacade.writeSendLogWithResult(StampFacade
													.getStampSendSeq("FS"), null, orgcode,
													ITFECommonConstant.DES_NODE, commitdate
															.replaceAll("-", ""),
													MsgConstant.MSG_NO_5101, (String) eventContext
															.getMessage().getProperty(
																	"XML_MSG_FILE_PATH"), num, amt,
																	subpackno, ls_TreCode, null,payunit, null, ls_MsgId,
													DealCodeConstants.DEALCODE_ITFE_SEND, null,
													null,
													(String) eventContext.getMessage().getProperty(
															MessagePropertyKeys.MSG_SENDER), null,
													""));
								  message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY,"5101");
								  message.setPayload(xmlMap);
								  eventContext.dispatchEvent(message, "Dispatchjms");
							   } 
							   SQLExecutor updateExce = null;
								try {
									updateExce = DatabaseFacade.getDb()
											.getSqlExecutorFactory().getSQLExecutor();
									String updateSql = "update "
											+ TvFilepackagerefDto.tableName()
											+ " set S_RETCODE = ?, S_MSGID = ?,T_SENDTIME =CURRENT TIMESTAMP where  S_PACKAGENO = ? and  S_ORGCODE = ?  and S_COMMITDATE = ?";
									updateExce.clearParams();
									updateExce
											.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
									updateExce.addParam("0");
									updateExce.addParam(packno);
									updateExce.addParam(orgcode);// 核算主体代码
									updateExce.addParam(commitdate);// //委托日期
									updateExce.runQuery(updateSql);
									updateExce.closeConnection();
								} catch (JAFDatabaseException e) {
									String error = "更新包处理回执状态时出现数据库异常！";
									logger.error(error, e);
									throw new YakTransformerException(YakMessages
											.msgTransformError(e.getMessage()), e);
								} finally {
									if (null != updateExce) {
										updateExce.closeConnection();
									}
								}
								continue;
						}

						if ("2".equals(ls_treattrib)) {// 2为代理国库
							xmlMap = getMsgMapByOperationSameBank(orgcode,
									filename, packno, commitdate, operationType);
							message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
									ls_PbcCode
											+ "-"
											+ (String) ((Map) ((Map) xmlMap
													.get("cfx")).get("HEAD"))
													.get("MsgNo"));
						} else {
							xmlMap = getMsgMapByOperation(orgcode,trecode, filename,
									packno, commitdate, operationType);
							if(MsgConstant.VOUCHER_NO_5407.equals(operationType)&&ITFECommonConstant.IFNEWINTERFACE.equals("1"))
							{
								message.setProperty(MessagePropertyKeys.MSG_NO_KEY,"11052");
							}else
							{
								message.setProperty(MessagePropertyKeys.MSG_NO_KEY,(String)((Map)((Map)xmlMap.get("cfx")).get("HEAD")).get("MsgNo"));
							}
						}
					}
					if (MsgNo5207Batch) { //如果5207拨款清单模式,后续代码都不执行
						continue;
					}
					if (xmlMap == null) {
						Exception e = new Exception("组装凭证 报文异常，报文为空");
						logger.error(e.getMessage(), e);
						throw new YakTransformerException(YakMessages
								.msgTransformError(e.getMessage()), e);
					}
					String ls_MsgId = (String) ((Map) ((Map) xmlMap.get("cfx"))
							.get("HEAD")).get("MsgID");
					String ls_MsgNo = (String) ((Map) ((Map) xmlMap.get("cfx"))
							.get("HEAD")).get("MsgNo");
					BigDecimal amt = new BigDecimal(0);
					int num = 0;

					if (ls_MsgNo
							.equals(MsgConstant.MSG_NO_5102)
							|| ls_MsgNo
									.equals(MsgConstant.MSG_NO_5103)) {
						packno = (String) ((Map) ((Map) ((Map) xmlMap
								.get("cfx")).get("MSG")).get("BatchHead"
								+ ls_MsgNo)).get("PackNo");
						amt = new BigDecimal((String) ((Map) ((Map) ((Map) xmlMap
								.get("cfx")).get("MSG")).get("BatchHead"
								+ ls_MsgNo)).get("AllAmt"));
						num = Integer
								.valueOf((String) ((Map) ((Map) ((Map) xmlMap
										.get("cfx")).get("MSG"))
										.get("BatchHead"
												+ ls_MsgNo))
										.get("AllNum"));

					} else {
//						Map msg = (Map) xmlMap.get("cfx");
						amt = tmppackdto.getNmoney();
						num = tmppackdto.getIcount();
					}
					// 更新包状态
					SQLExecutor updateExce = null;
					try {
						updateExce = DatabaseFacade.getDb()
								.getSqlExecutorFactory().getSQLExecutor();
						String updateSql = "update "
								+ TvFilepackagerefDto.tableName()
								+ " set S_RETCODE = ?, S_MSGID = ?,T_SENDTIME =CURRENT TIMESTAMP where  S_PACKAGENO = ? and  S_ORGCODE = ?  and S_COMMITDATE = ?";
						updateExce.clearParams();
						updateExce
								.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
						updateExce.addParam(ls_MsgId);
						updateExce.addParam(packno);
						updateExce.addParam(orgcode);// 核算主体代码
						updateExce.addParam(commitdate);// //委托日期
						updateExce.runQuery(updateSql);
						updateExce.closeConnection();
					} catch (JAFDatabaseException e) {
						String error = "更新包处理回执状态时出现数据库异常！";
						logger.error(error, e);
						throw new YakTransformerException(YakMessages
								.msgTransformError(e.getMessage()), e);
					} finally {
						if (null != updateExce) {
							updateExce.closeConnection();
						}
					}
					// 设置消息体
					message
					.setProperty(
							MessagePropertyKeys.MSG_SEND_LOG_DTO,
							MsgLogFacade
									.writeSendLogWithResult(
											StampFacade
													.getStampSendSeq("FS"),
											null,
											orgcode,
											ITFECommonConstant.DES_NODE,
											TimeFacade
													.getCurrentStringTime(),
											ls_MsgNo,
											(String) eventContext
													.getMessage()
													.getProperty(
															"XML_MSG_FILE_PATH"),
											num,
											amt,
											packno,
											ls_TreCode,
											null,
											ls_BillOrg,
											null,
											ls_MsgId,
											DealCodeConstants.DEALCODE_ITFE_SEND,
											null,
											null,
											StateConstant.MSG_SENDER_FLAG_0,
											null,
											MsgConstant.ITFE_AUTO_SEND
													+ ls_MsgNo));
					message.setPayload(xmlMap);
					
					logger.debug("======================== 转发tips ========================");
					eventContext.dispatchEvent(message, "Dispatchjms");
					
					// 分包情况下分别记录在发送完成后记录发送日志
					
					if (splitXmlMap != null) {
						if (ls_MsgNo
								.equals(MsgConstant.MSG_NO_5102)
								|| ls_MsgNo
										.equals(MsgConstant.MSG_NO_5103)) {
						ls_MsgId = (String) ((Map) ((Map) splitXmlMap
								.get("cfx")).get("HEAD")).get("MsgID");
						ls_MsgNo = (String) ((Map) ((Map) splitXmlMap
								.get("cfx")).get("HEAD")).get("MsgNo");
						packno = (String) ((Map) ((Map) ((Map) splitXmlMap
								.get("cfx")).get("MSG")).get("BatchHead"
								+ ls_MsgNo)).get("PackNo");
						amt = new BigDecimal((String) ((Map) ((Map) ((Map) splitXmlMap
								.get("cfx")).get("MSG")).get("BatchHead"
								+ ls_MsgNo)).get("AllAmt"));
						num = Integer
								.valueOf((String) ((Map) ((Map) ((Map) splitXmlMap
										.get("cfx")).get("MSG"))
										.get("BatchHead"
												+ ls_MsgNo))
										.get("AllNum"));
						}
						message.setPayload(splitXmlMap);
						message.setProperty(
								MessagePropertyKeys.MSG_SEND_LOG_DTO,
								MsgLogFacade.writeSendLogWithResult(StampFacade
										.getStampSendSeq("FS"), null, orgcode,
										ITFECommonConstant.DES_NODE, TimeFacade
												.getCurrentStringTime(),
										ls_MsgNo, (String) eventContext
												.getMessage().getProperty(
														"XML_MSG_FILE_PATH"),
										num, amt, packno, ls_TreCode, null,
										ls_BillOrg, null, ls_MsgId,
										DealCodeConstants.DEALCODE_ITFE_SEND,
										null, null,
										StateConstant.MSG_SENDER_FLAG_0, null,
										MsgConstant.ITFE_AUTO_SEND + ls_MsgNo));
						
						eventContext.dispatchEvent(message, "Dispatchjms");
					}
					} 
				}
			

			return eventContext.getMessage().getPayload();
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new YakTransformerException(YakMessages.msgTransformError(e
					.getMessage()), e);
		} catch (Exception e) {
			logger.error("生成5407异常：",e);
			throw new YakTransformerException(YakMessages.msgTransformError(e
					.getMessage()), e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/*
	 * 根据业务类型生成xmlMap对象
	 */
	private Map getMsgMapByOperation(String orgCode,String trecode, String fileName,
			String packNo, String commitDate, String operationType)
			throws JAFDatabaseException, ITFEBizException {
		Map xmlMap = null;
		List<String> params = new ArrayList<String>();
		String orderbysql = "where S_PACKAGENO = ? and S_TRECODE = ? ";
		params.clear();
		params.add(packNo);
		params.add(trecode);
		if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(operationType)) {
			// 查找数据库

			List<TvGrantpaymsgmainDto> list = DatabaseFacade.getDb().find(
					TvGrantpaymsgmainDto.class, orderbysql, params);
			List<List> lists = MtoCodeTrans.transformZeroAmtMainDto(list);
			xmlMap = Dto2MapFor5103.tranfor(lists.get(0), orgCode, fileName,
					packNo, false);
			if (lists.size() == 2)
				splitXmlMap = Dto2MapFor5103.tranfor(lists.get(1), orgCode,
						fileName, packNo, false);

		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(operationType)) {
			// 查找数据库

			List<TvPayoutfinanceMainDto> list = DatabaseFacade.getDb().find(
					TvPayoutfinanceMainDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor5112.tranfor(list, orgCode, fileName,
						packNo, false);
			}
		} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN
				.equals(operationType)) {
			// 查找数据库

			List<TvDirectpaymsgmainDto> list = DatabaseFacade.getDb().find(
					TvDirectpaymsgmainDto.class, orderbysql, params);
			List<List> lists = MtoCodeTrans.transformZeroAmtMainDto(list);
			xmlMap = Dto2MapFor5102.tranfor(lists.get(0), orgCode, fileName,
					packNo, false);
			if (lists.size() == 2)
				splitXmlMap = Dto2MapFor5102.tranfor(lists.get(1), orgCode,
						fileName, packNo, false);

		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(operationType)) {
			// 查找数据库

			List<TvPayoutmsgmainDto> list = DatabaseFacade.getDb().find(
					TvPayoutmsgmainDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor5101.tranfor(list, orgCode, fileName,
						packNo, false);	
			}
			
			
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKAGENO = ? and S_AIMTRECODE = ? ";
			List<TvDwbkDto> list = DatabaseFacade.getDb().find(TvDwbkDto.class,
					orderbysql, params);
			if (null != list && list.size() > 0) {
				if (ITFECommonConstant.IFNEWINTERFACE.equals("1")) {// 新接口
					xmlMap = Dto2MapFor11042.tranfor(list, orgCode, fileName,
							packNo, false);
				} else {
					xmlMap = Dto2MapFor1104.tranfor(list, orgCode, fileName,
							packNo, false);
				}
			}
		} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY
				.equals(operationType)
				|| BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY
						.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKNO = ? and S_TRECODE = ? ";
			List<TvPayreckBankDto> list = DatabaseFacade.getDb().find(
					TvPayreckBankDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor2201.tranfor(list, orgCode, fileName,
						packNo, false);
			}
		} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK
				.equals(operationType)
				|| BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK
						.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKNO = ? and S_TRECODE = ? ";
			List<TvPayreckBankBackDto> list = DatabaseFacade.getDb().find(
					TvPayreckBankBackDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor2202.tranfor(list, orgCode, fileName,
						packNo, false);
			}
		} else if (MsgConstant.VOUCHER_NO_5201.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKAGENO = ? and S_TRECODE = ? ";
			List<TfDirectpaymsgmainDto> list = DatabaseFacade.getDb().find(
					TfDirectpaymsgmainDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				if (list.get(0).getSbusinesstypecode().equals(
						StateConstant.BIZTYPE_CODE_BATCH))
					// 批量业务生成TIPS商行划款申请报文（2201）(上海扩展)
					xmlMap = VoucherDto2MapFor2201.tranfor(list, orgCode,
							fileName, packNo, false);
				else
					// 单笔业务生成TIPS人民银行办理直接支付报文（5104）(上海扩展)
					xmlMap = VoucherDto2MapFor5104.tranfor(list, orgCode,
							fileName, packNo, false);
			}
		} else if (MsgConstant.VOUCHER_NO_2252.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKAGENO = ? and S_TRECODE = ? ";
			List<TfPaybankRefundmainDto> list = DatabaseFacade.getDb().find(
					TfPaybankRefundmainDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				// 收款银行退款通知2252(上海扩展)
				xmlMap = VoucherDto2MapFor2202.tranfor(list, orgCode, fileName,
						packNo, false);
			}
		}else if (MsgConstant.VOUCHER_NO_5407.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKAGENO = ? and (S_CURPAYEETRECODE = ? or S_ORIPAYEETRECODE = ?) ";
			params.add(trecode);
			List<TvInCorrhandbookDto> list = DatabaseFacade.getDb().find(
					TvInCorrhandbookDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				// 更正5407
				if(ITFECommonConstant.IFNEWINTERFACE.equals("1")){
					xmlMap = Dto2MapFor11052.tranfor(list, orgCode, fileName, packNo,false);
				}else{
					xmlMap = Dto2MapFor1105.tranfor(list, orgCode, fileName,packNo, false);
				}
			}
		}else if (MsgConstant.VOUCHER_NO_5671.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKAGENO = ? and S_TRECODE = ? ";
			List<TvNontaxmainDto> list = DatabaseFacade.getDb().find(
					TvNontaxmainDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor5671.tranfor(list, orgCode, fileName,packNo, false);
				
			}
		}else if (MsgConstant.VOUCHER_NO_5408.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKAGENO = ? and S_TRECODE = ? ";
			List<TvNontaxmainDto> list = DatabaseFacade.getDb().find(
					TvNontaxmainDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				if (list.get(0).getStrecode().startsWith("06")) {
				  xmlMap = Dto2MapFor5408_JL.tranfor(list, orgCode, fileName,packNo, false);
				}else{
				  xmlMap = Dto2MapFor5408.tranfor(list, orgCode, fileName,packNo, false);
				}
				
			}
		}
		return xmlMap;
	}

	/*
	 * 根据业务类型生成xmlMap对象(同城清算接口)
	 */
	private Map getMsgMapByOperationSameBank(String orgCode, String fileName,
			String packNo, String commitDate, String operationType)
			throws JAFDatabaseException, ITFEBizException {
		Map xmlMap = null;
		List<String> params = new ArrayList<String>();
		String orderbysql = "where S_PACKAGENO = ?";
		params.clear();
		params.add(packNo);
		if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(operationType)) {
			// 查找数据库
			List<TvPayoutmsgmainDto> list = DatabaseFacade.getDb().find(
					TvPayoutmsgmainDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor1000_5101.tranfor(list, orgCode, fileName,
						packNo, false);
			}
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(operationType)) {
			// 查找数据库

			List<TvDwbkDto> list = DatabaseFacade.getDb().find(TvDwbkDto.class,
					orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor1000_1104.tranfor(list, orgCode, fileName,
						packNo, false);
			}
		} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY
				.equals(operationType)) {
			// 查找数据库
			orderbysql = "where S_PACKNO = ?";
			List<TvPayreckBankDto> list = DatabaseFacade.getDb().find(
					TvPayreckBankDto.class, orderbysql, params);
			if (null != list && list.size() > 0) {
				xmlMap = Dto2MapFor1000_2201.tranfor(list, orgCode, fileName,
						packNo, false);
			}
		}
		return xmlMap;
	}
	
	
	/**
	 * 明细账户模式
	 * 拨款清单模式
	 * @throws SequenceException 
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws MuleException 
	 * 
	 */
	private List detailSendMsg(MuleEventContext eventContext,String orgcode,String trecode, String filename,
			String packno, String  commitdate) throws ITFEBizException, SequenceException, JAFDatabaseException, ValidateException, MuleException{
		
		String wheresql =  "where S_PACKAGENO = ? and S_TRECODE = ? ";
		List params = new ArrayList();
		params.add(packno);
		params.add(trecode);
		List<TvPayoutmsgmainDto> list = DatabaseFacade.getODB().find(
				TvPayoutmsgmainDto.class, wheresql, params);
		List  MsgMapList = new ArrayList() ;
		for (TvPayoutmsgmainDto tmpdto : list) {
			TvPayoutmsgsubDto qdto = new TvPayoutmsgsubDto();
			qdto.setSbizno(tmpdto.getSbizno());
			List <TvPayoutmsgsubDto> subList = CommonFacade.getODB().findRsByDto(qdto) ;
			if (subList.size()>0) {
				List bankList = getListByDetailBank(subList);//明细按照银行进行分包
				
				List <TvPayoutmsgsubDto> subpackList = new ArrayList<TvPayoutmsgsubDto>();
				
				for (List<TvPayoutmsgsubDto> subBankList : (List <List>) bankList) {
					int i =0;
				for (TvPayoutmsgsubDto subdto : subBankList) {
					subpackList.add(subdto);
					i++;
					if ((i % MsgConstant.TIPS_MAX_OF_PACK==0  && i!=0) ||  i==subBankList.size()) {
						Map xmlMap = Dto2MapFor5101ForBatch.tranfor(tmpdto, subpackList,orgcode, filename,
								packno, Boolean.FALSE);
						
						subpackList.clear();
						// 设置消息体
						MsgMapList.add(xmlMap);
						logger.debug("======================== 拨款清单发送TIPStips ========================");
						
					}
				}
			}
			}else{
				logger.error("实拨资金子表查询记录为空！");
				return null;
			}					
		}
		return MsgMapList;
	}
	
	
	/**
	 * 凭证列表根据开户行行号分包
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	private static List getListByDetailBank(List<TvPayoutmsgsubDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TvPayoutmsgsubDto dto : list) {
			map.put(dto.getSpayeebankno(), "");
		}
		if (map.size() == 0) {
			return null;
		}
		List<List> lists = new ArrayList<List>();
		try { 

			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvPayoutmsgsubDto> newList = new ArrayList<TvPayoutmsgsubDto>();
				for (TvPayoutmsgsubDto dto : list) {
					if (dto.getSpayeebankno().equals(key)) {
						newList.add(dto);
					}
				}
				lists.add(newList);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("实拨资金根据明细记录开户行行号分包出现异常！", e);
		}

		return lists;
	}
	
	
	
	
	
	
	
	
}
