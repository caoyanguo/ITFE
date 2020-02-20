package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto;
import com.cfcc.itfe.persistence.dto.TfReconciliationDto;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Dto2MapFor1002 {
	private static Log logger = LogFactory.getLog(Dto2MapFor1002.class);
	
	@SuppressWarnings("unchecked")
	public static Map tranfor(List<TfReconciliationDto> list, TfReconciliationDto tfRecDto,String revNode)throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		List<TvVoucherinfoDto> voucherDtoList = new ArrayList<TvVoucherinfoDto>(); //用于存放补发代理行未收到的资金拨付信息索引dto

		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// 报文头 HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		headMap.put("SRC", "111111111111");
		headMap.put("DES", revNode);
		headMap.put("APP", "TCQS");
		headMap.put("MsgNo", MsgConstant.MSG_TBS_NO_1002);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("Reserve", "");
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", tfRecDto.getSmsgid());
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		
		//报文体--批量头
		HashMap<String, Object> head1002map = new HashMap<String, Object>();
		List<Object> billCheck1002list = new ArrayList<Object>();
		if(null==list || list.size()==0){//对账报文不带明细，说明当天没有业务发生
			if(tfRecDto.getIrecvpacknum()!=0 || tfRecDto.getIsendpacknum()!=0){
				throw new ITFEBizException("无对账明细，但接收或发送包个数不为零(应该为零)！");
			}else{
				head1002map.put("ChkDate", tfRecDto.getSchkdate());
				head1002map.put("TreCode", tfRecDto.getStrecode());
				head1002map.put("PackNo", tfRecDto.getIpackno());
				head1002map.put("CheckResult", "0");
				head1002map.put("SendPackNum", "0");
				head1002map.put("RecvPackNum", "0");
			}
		}else{
			//通过查询数据库，判断代理行本日接收包总个数和代理库本日发起包总个数是否相等，
			//代理行本日发起包总个数和代理库本日接收包总个数是否相等，如果全部相等，则对账成功，否则对账失败，
			//并且找出导致对账失败的对账不符的数据，作为对账结果的明细返回。对账失败有三种可能：第一：本日接收包个数不符；
			//第二：本日发起包个数不符；第三：本日接收和发送包都不符。
			
			head1002map.put("ChkDate", tfRecDto.getSchkdate());
			head1002map.put("TreCode", tfRecDto.getStrecode());
			head1002map.put("PackNo", tfRecDto.getIpackno());
			
			try {
				//统计当日同一国库资金拨付总包数及总金额
				String sql = "select count(S_PACKNO) as count_packno, sum(N_AMT) as sum_amt from TF_FUND_APPROPRIATION where S_TRECODE = ? and S_ENTRUSTDATE = ?";
				SQLExecutor sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlExe.addParam(tfRecDto.getStrecode());
				sqlExe.addParam(tfRecDto.getSchkdate());
				SQLResults rs = sqlExe.runQuery(sql);
				Integer count_packno1 = rs.getInt(0, 0);
				BigDecimal sum_amt1 = rs.getBigDecimal(0, 1);
				
				//统计当日同一国库清算回执总包数及总金额
				sql = "select count(S_PACKNO) as count_packno, sum(N_AMT) as sum_amt from TF_FUND_CLEAR_RECEIPT where S_TRECODE = ? and S_ENTRUSTDATE = ?";
				sqlExe.clearParams();
				sqlExe.addParam(tfRecDto.getStrecode());
				sqlExe.addParam(tfRecDto.getSchkdate());
				rs = sqlExe.runQuery(sql);
				Integer count_packno2 = rs.getInt(0, 0);
				BigDecimal sum_amt2 = rs.getBigDecimal(0, 1);
				
				//统计当日同一国库退款通知总包数及总金额
				sql = "select count(S_PACKNO) as count_packno, sum(N_AMT) as sum_amt from TF_REFUND_NOTICE where S_TRECODE = ? and S_ENTRUSTDATE = ?";
				sqlExe.clearParams();
				sqlExe.addParam(tfRecDto.getStrecode());
				sqlExe.addParam(tfRecDto.getSchkdate());
				rs = sqlExe.runQueryCloseCon(sql);
				Integer count_packno3 = rs.getInt(0, 0);
				BigDecimal sum_amt3 = rs.getBigDecimal(0, 1);
				
				if(count_packno1 == null){
					count_packno1 = 0;
				}
				if(count_packno2 == null){
					count_packno2 = 0;
				}
				if(count_packno3 == null){
					count_packno3 = 0;
				}
				if(sum_amt1 == null){
					sum_amt1 = new BigDecimal(0.0);
				}
				if(sum_amt2 == null){
					sum_amt2 = new BigDecimal(0.0);
				}
				if(sum_amt3 == null){
					sum_amt3 = new BigDecimal(0.0);
				}
				
				//对账相符
				if(count_packno1==tfRecDto.getIrecvpacknum() && count_packno2+count_packno3==tfRecDto.getIsendpacknum() 
						&& tfRecDto.getNpackamt().compareTo(sum_amt1)==0 && tfRecDto.getNsendpackamt().compareTo(sum_amt2.add(sum_amt3))==0){
					head1002map.put("CheckResult", "0");
					head1002map.put("SendPackNum", "0");
					head1002map.put("RecvPackNum", "0");
					
					//为同步对账数据做准备--S_EXT1用于表示对账状态    0表示未对账  1表示对账相符    2代理行少  3 国库前置少
					String sql1 = "update TF_FUND_APPROPRIATION set S_EXT1 = '1' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
//					String sql2 = "update TF_FUND_CLEAR_RECEIPT set S_EXT1 = '1' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
					String sql3 = "update TF_REFUND_NOTICE set S_EXT1 = '1' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
					
					SQLExecutor sqlExe1 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					sqlExe1.addParam(tfRecDto.getStrecode());
					sqlExe1.addParam(tfRecDto.getSchkdate());
					sqlExe1.runQuery(sql1);
//					sqlExe1.clearParams();
//					sqlExe1.addParam(tfRecDto.getStrecode());
//					sqlExe1.addParam(tfRecDto.getSchkdate());
//					sqlExe1.runQuery(sql2);
					sqlExe1.clearParams();
					sqlExe1.addParam(tfRecDto.getStrecode());
					sqlExe1.addParam(tfRecDto.getSchkdate());
					sqlExe1.runQueryCloseCon(sql3);
					
				}else{//对账不符
					head1002map.put("CheckResult", "1");
					head1002map.put("SendPackNum", count_packno1-tfRecDto.getIrecvpacknum()+"");
					head1002map.put("RecvPackNum", tfRecDto.getIsendpacknum()-(count_packno2+count_packno3)+"");
					
					List fundApprList = new ArrayList();   //存放资金拨付包流水号
					List<TfReconciliationDto> fundClearList = new ArrayList<TfReconciliationDto>();  //存放资金清算回执信息
					List<TfReconciliationDto> refundList = new ArrayList<TfReconciliationDto>();     //存放退款通知信息
					for(int i=0;i<list.size();i++){
						//将收到的对账报文中资金拨付、资金清算回执和退款通知对应的包流水号分别放到不同的list中，
						//
						String svoutype = list.get(i).getSpayoutvoutype();
						if(svoutype.equals("1") || svoutype.equals("2") || svoutype.equals("3") || svoutype.equals("6")){ //资金拨付
							fundApprList.add(list.get(i).getSpackno());
						}else if(svoutype.equals("4")){ //清算回执
							fundClearList.add(list.get(i));
						}else if(svoutype.equals("5")){ //退款通知
							refundList.add(list.get(i));
						}
					}
					//对账结果报文中，将代理行资金拨付信息与代理库不符的，转发给代理行(代理行缺少的数据)
					TfFundAppropriationDto fundApprDto = new TfFundAppropriationDto();
					fundApprDto.setStrecode(tfRecDto.getStrecode());
					fundApprDto.setSentrustdate(tfRecDto.getSchkdate());
					List<TfFundAppropriationDto> list1 = CommonFacade.getODB().findRsByDto(fundApprDto);
					StringBuffer strBuffer = new StringBuffer(); //存放代理行缺少的资金拨付信息的流水号
					StringBuffer StrBufferO = new StringBuffer(); //存放代理行不存在的资金拨付信息的包流水号
					StringBuffer strBufferT = new StringBuffer(); //存放代理行存在的资金拨付信息的包流水号
					if(list1!=null && list1.size()>0){
						for(int i=0;i<list1.size();i++){
							if(!fundApprList.contains(list1.get(i).getSpackno())){
								HashMap<String, Object> billCheck1002Map = new HashMap<String, Object>();
								billCheck1002Map.put("PayoutVouType", list1.get(i).getSpayoutvoutype());
								billCheck1002Map.put("PackNo", list1.get(i).getSpackno());
								billCheck1002Map.put("CurPackVouNum", MtoCodeTrans.transformString(list1.get(i).getIallnum()));
								billCheck1002Map.put("CurPackVouAmt", MtoCodeTrans.transformString(list1.get(i).getNallamt()));
								billCheck1002list.add(billCheck1002Map);
								strBuffer.append("'"+list1.get(i).getSid()+"',");
								
								StrBufferO.append("'"+list1.get(i).getSpackno()+"',"); //
							}else{ //用于更新状态的条件，为同步对账数据做准备--S_EXT1用于表示对账状态    0表示未对账  1表示对账相符    2代理行少  3 国库前置少
								strBufferT.append("'"+list1.get(i).getSpackno()+"',");
							}
						}
						/**
						 * 用于补发代理行未收到资金拨付报文
						 */
						if(strBuffer.toString().length()>0){
							TvVoucherinfoDto infoDto = new TvVoucherinfoDto();
							infoDto.setStrecode(tfRecDto.getStrecode());
//							infoDto.setScreatdate(TimeFacade.getCurrentStringTime());
							String wheresql = "AND S_DEALNO IN (" + strBuffer.toString().substring(0,strBuffer.toString().lastIndexOf(","))+")";
							voucherDtoList = CommonFacade.getODB().findRsByDtoForWhere(infoDto, wheresql);
							
							//更新状态用于将对账结果同步到TBS
							String sql4 = "update TF_FUND_APPROPRIATION set S_EXT1 = '2' where S_TRECODE = ? and S_ENTRUSTDATE = ? and S_PACKNO in " +
									"("+StrBufferO.toString().substring(0,StrBufferO.toString().lastIndexOf(","))+")";
							SQLExecutor sqlExe2 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
							sqlExe2.addParam(tfRecDto.getStrecode());
							sqlExe2.addParam(tfRecDto.getSchkdate());
							sqlExe2.runQueryCloseCon(sql4);
						}
						
						//更新状态用于将对账结果同步到TBS
						if(strBufferT.toString().length()>0){
							String sql5 = "update TF_FUND_APPROPRIATION set S_EXT1 = '1' where S_TRECODE = ? and S_ENTRUSTDATE = ? and S_PACKNO in " +
									"("+strBufferT.toString().substring(0,strBufferT.toString().lastIndexOf(","))+")";
							SQLExecutor sqlExe3 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
							sqlExe3.addParam(tfRecDto.getStrecode());
							sqlExe3.addParam(tfRecDto.getSchkdate());
							sqlExe3.runQueryCloseCon(sql5);
						}
					}
					
					//对账结果报文中，将代理行资金清算回执与代理库不符的，转发给代理行(代理库缺少的数据)
					TfFundClearReceiptDto cRectDto = new TfFundClearReceiptDto();
					cRectDto.setStrecode(tfRecDto.getStrecode());
					cRectDto.setSentrustdate(tfRecDto.getSchkdate());
					List<TfFundClearReceiptDto> list2 = CommonFacade.getODB().findRsByDto(cRectDto);
					List cRectList = new ArrayList();
					for(int i=0;i<list2.size();i++){
						cRectList.add(list2.get(i).getSpackno());
					}
					List<TfReconciliationDto> lessClearList = new ArrayList<TfReconciliationDto>();
//					List lessClearList = new ArrayList();
					for(int i=0;i<fundClearList.size();i++){
						if(!cRectList.contains(fundClearList.get(i).getSpackno())){
							HashMap<String, Object> billCheck1002Map = new HashMap<String, Object>();
							billCheck1002Map.put("PayoutVouType", fundClearList.get(i).getSpayoutvoutype());
							billCheck1002Map.put("PackNo", fundClearList.get(i).getSpackno());
							billCheck1002Map.put("CurPackVouNum", MtoCodeTrans.transformString(fundClearList.get(i).getIcurpackvounum()));
							billCheck1002Map.put("CurPackVouAmt", MtoCodeTrans.transformString(fundClearList.get(i).getNcurpackvouamt()));
							billCheck1002list.add(billCheck1002Map);
							
							//用于标记前置缺少的清算回执信息，为同步对账结果到TBS做准备
//							TfReconciliationDto recDto = fundClearList.get(i);
//							recDto.setSext1("3");
//							lessClearList.add(recDto);//对账信息中前置缺少的资金清算回执
						}
					}
					
					//更新状态用于将对账结果同步到TBS
//					if(list2 != null && list2.size()>0){
//						String sql6 = "update TF_FUND_CLEAR_RECEIPT set S_EXT1 = '1' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
//						SQLExecutor sqlExe4 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//						sqlExe4.addParam(tfRecDto.getStrecode());
//						sqlExe4.addParam(tfRecDto.getSchkdate());
//						sqlExe4.runQueryCloseCon(sql6);
//					}
//					if(lessClearList.size()>0){ //标记前置缺少的清算回执信息，为同步对账结果到TBS做准备
//						DatabaseFacade.getODB().update(lessClearList.toArray(new TfReconciliationDto[lessClearList.size()]));
//					}
					lessClearList.clear();
					
					//对账结果报文中，将代理行退款通知与代理库不符的，转发给代理行(代理库缺少的数据)
					TfRefundNoticeDto rnDto = new TfRefundNoticeDto();
					rnDto.setStrecode(tfRecDto.getStrecode());
					rnDto.setSentrustdate(tfRecDto.getSchkdate());
					List<TfRefundNoticeDto> list3 = CommonFacade.getODB().findRsByDto(rnDto);
					List rnList = new ArrayList();
					for(int i=0;i<list3.size();i++){
						rnList.add(list3.get(i).getSpackno());
					}
					for(int i=0;i<refundList.size();i++){
						if(!rnList.contains(refundList.get(i).getSpackno())){
							HashMap<String, Object> billCheck1002Map = new HashMap<String, Object>();
							billCheck1002Map.put("PayoutVouType", refundList.get(i).getSpayoutvoutype());
							billCheck1002Map.put("PackNo", refundList.get(i).getSpackno());
							billCheck1002Map.put("CurPackVouNum", MtoCodeTrans.transformString(refundList.get(i).getIcurpackvounum()));
							billCheck1002Map.put("CurPackVouAmt", MtoCodeTrans.transformString(refundList.get(i).getNcurpackvouamt()));
							billCheck1002list.add(billCheck1002Map);
							
							//用于标记前置缺少的退款通知信息，为同步对账结果到TBS做准备
							TfReconciliationDto recDto = refundList.get(i);
							recDto.setSext1("3");
							lessClearList.add(recDto);//对账信息中前置缺少的退款通知
						}
					}
					
					//更新状态用于将对账结果同步到TBS
					if(list3 != null && list3.size()>0){
						String sql7 = "update TF_REFUND_NOTICE set S_EXT1 = '1' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
						SQLExecutor sqlExe5 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
						sqlExe5.addParam(tfRecDto.getStrecode());
						sqlExe5.addParam(tfRecDto.getSchkdate());
						sqlExe5.runQueryCloseCon(sql7);
					}
					if(lessClearList.size()>0){ //标记前置缺少的退款通知信息，为同步对账结果到TBS做准备
						String checkSql = "UPDATE TF_RECONCILIATION SET S_EXT1 = ? WHERE S_MSGID = ? AND S_CHKDATE = ? AND S_TRECODE = ? AND S_PACKNO = ? AND S_PAYOUTVOUTYPE = ?";
						for(TfReconciliationDto tmpDto:lessClearList){
							SQLExecutor sqlExrst= DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
							sqlExrst.addParam(tmpDto.getSext1());
							sqlExrst.addParam(tmpDto.getSmsgid());
							sqlExrst.addParam(tmpDto.getSchkdate());
							sqlExrst.addParam(tmpDto.getStrecode());
							sqlExrst.addParam(tmpDto.getSpackno());
							sqlExrst.addParam(tmpDto.getSpayoutvoutype());
							sqlExrst.runQueryCloseCon(checkSql);
						}
					}
				}
				
			} catch (JAFDatabaseException e) {
				logger.error("查询资金拨付、资金清算回执或退款通知异常！", e);
				throw new ITFEBizException("查询资金拨付、资金清算回执或退款通知异常！", e);
			} catch (ValidateException e) {
				logger.error("查询资金拨付、资金清算回执或退款通知异常！", e);
				throw new ITFEBizException("查询资金拨付、资金清算回执或退款通知异常！", e);
			} catch (Exception e) {
				logger.error("其它异常！", e);
				throw new ITFEBizException("其它异常！", e);
			}
			
			//补发缺少的业务报文
			for(TvVoucherinfoDto indexDTo:voucherDtoList){
				sendMessage(indexDTo);
				logger.info("=========补发代理行缺少的业务报文完成=========");
			}
		}
		msgMap.put("BatchHead1002", head1002map);
		msgMap.put("BillCheck1002", billCheck1002list);
		return map;
	}
	
	private static void sendMessage(TvVoucherinfoDto infoDto) {
		try {
			MuleMessage message = new DefaultMuleMessage("");
			MuleClient client = new MuleClient();
			message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY,
					"TBS_1000_REISSUE");
			message.setProperty(MessagePropertyKeys.MSG_DTO, infoDto);
			// message.setProperty("orgCode", vDto.getSorgcode());
			message = client.send("vm://ManagerMsgWithCommBank", message);
		} catch (MuleException e) {
			e.printStackTrace();
		}
	}
}
