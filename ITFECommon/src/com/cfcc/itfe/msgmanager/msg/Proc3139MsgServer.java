package com.cfcc.itfe.msgmanager.msg;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 
 * 主要功能：6.4.10　财政入库流水明细
 * 
 * @author wangyunbin
 * 
 */
public class Proc3139MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3139MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * 解析入库批量头 MSG->BillHead3139
		 */
		HashMap billhead3139 = (HashMap) msgMap.get("BillHead3139");

		String finorgcode = (String) billhead3139.get("FinOrgCode");// 财政机关代码
		String trecode = (String) billhead3139.get("TreCode");// 国库代码
		String intredate = (String) billhead3139.get("InTreDate");// 入库凭证日期
		String packno = (String) billhead3139.get("PackNo");// 包流水号
		int childpacknum = Integer.valueOf(billhead3139.get("ChildPackNum")
				.toString());// 子包总数
		String curpackno = (String) billhead3139.get("CurPackNo");// 本包序号
		String taxallnum = (String) billhead3139.get("TaxAllNum");// 本包税票总笔数
		String drawbackallnum = (String) billhead3139.get("DrawBackAllNum");// 本包退库总笔数
		String corrallnum = (String) billhead3139.get("CorrAllNum");// 本包更正总笔数
		int freeallnum = Integer.valueOf(billhead3139.get("FreeAllNum")
				.toString());// 本包免抵调总笔数
		String sbookorgcode = (String) headMap.get("DES");
		String sqladddetail = "(";
		String sqladdsum = "(";
		List <String> deatilList = new ArrayList<String>();
		List <String> sumList = new ArrayList<String>();
		
		

		List<IDto> list = new ArrayList<IDto>();
		// 查询接收日志
		TvRecvlogDto logDto = new TvRecvlogDto();
		logDto.setSseq((String) headMap.get("MsgID"));
		try {
			//判断是否是是国库维护的财政机构代码，防止维护了两个财政，造成入库流水重复。
			// 财政机构代码缓存key为财政代码
			HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (!mapFincInfoByFinc.containsKey(finorgcode)) {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			// 根据国库查询核算主体
			TsTreasuryDto _dto = SrvCacheFacade.cacheTreasuryInfo(null).get(
					trecode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			List<TvRecvlogDto> finddtolist = CommonFacade.getODB().findRsByDto(
					logDto);
			// 已经接收日志记录
			if (null == finddtolist || finddtolist.size() == 0) {
				List returnList = (List) msgMap.get("Bill3139");
				if (null == returnList || returnList.size() == 0) {
					return;
				} else {

					int count = returnList.size();
					for (int i = 0; i < count; i++) {
						HashMap bill3139map = (HashMap) returnList.get(i);
						String TaxOrgCode = (String) bill3139map
								.get("TaxOrgCode"); // 征收机关代码
						String ExportVouType = (String) bill3139map
								.get("ExportVouType"); // 导出凭证类型
						String ExpTaxVouNo = (String) bill3139map
								.get("ExpTaxVouNo"); // 凭证号码
						String BudgetType = (String) bill3139map
								.get("BudgetType"); // 预算种类
						String BudgetLevelCode = (String) bill3139map
								.get("BudgetLevelCode"); // 预算级次
						String BudgetSubjectCode = (String) bill3139map
								.get("BudgetSubjectCode"); // 预算科目
						String TraAmt = (String) bill3139map.get("TraAmt"); // 交易金额
						String Origin = (String) bill3139map.get("Origin"); // 凭证来源
						TvFinIncomeDetailDto dto = new TvFinIncomeDetailDto();
						/**
						 * 组织DTO准备保存数据*****************************
						 */
						String _sseq = "";
						try {
							_sseq = StampFacade.getStampSendSeq("JS"); // 取财政申请入库流水信息业务流水
						} catch (SequenceException e) {
							logger.error(e);
							throw new ITFEBizException("取财政申请入库流水信息业务流水SEQ出错");
						}
						dto.setSseq(_sseq);// 业务流水号
						dto.setCbdgkind(BudgetType.trim());// 预算种类
						dto.setCbdglevel(BudgetLevelCode.trim());// 预算级次
						if (Origin == null || Origin.trim().equals("")) {
							dto.setCvouchannel("");// 凭证来源
						} else {
							dto.setCvouchannel(Origin.trim());// 凭证来源
						}
						dto.setFamt(MtoCodeTrans.transformBigDecimal(TraAmt));// 金额
						dto.setIpkgseqno(packno.trim());// 包流水号
						dto.setSbdgsbtcode(BudgetSubjectCode.trim());// 预算科目
						dto.setSexpvouno(ExpTaxVouNo.trim());// 凭证号码
						dto.setSexpvoutype(ExportVouType.trim());// 导出凭证类型
						dto.setStaxorgcode(TaxOrgCode.trim());// 征收机关代码
						dto.setStrecode(trecode.trim());// 国库代码
						dto.setSintredate(intredate.trim());// 入库凭证日期
						dto.setSorgcode(finorgcode.trim());// 财政机关代码

						list.add(dto);
						
						if ((list.size() > 0 && list.size() % 1000 == 0)
								|| (i + 1) == count) {
							DatabaseFacade.getDb().create(
									CommonUtil.listTArray(list));
							saveDownloadReportCheck(intredate,trecode);
						}
					}
					
				}
			}
			/*
			 * 接收/发送日志
			 */
			String recvseqno;// 接收日志流水号
			String sendseqno;// 发送日志流水号
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水

			String path = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_FILE_NAME);
			
			// 记接收日志
			MsgLogFacade.writeRcvLog(recvseqno, recvseqno, sbookorgcode,
					intredate, (String) headMap.get("MsgNo"), (String) headMap
							.get("SRC"), path, 0, new BigDecimal(0), packno,
							trecode, String.valueOf(childpacknum), finorgcode, null,
					(String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null,
					MsgConstant.LOG_ADDWORD_RECV_TIPS
							+ (String) headMap.get("MsgNo"));
			
			//用于判断所接财政数据是否已经完整，如果完整则调用WebService通知财政取数    --20131230 by hua
			if("030010011118".equals((String) headMap.get("DES"))) { //目前只支持北京
				//1、首先看是否已经给财政发过通知，如果没有发过则继续
				boolean boo = BusinessFacade.checkAndSaveRecvlog(finorgcode, intredate, MsgConstant.MSG_NO_3139, StateConstant.COMMON_NO);
				if(!boo) {
					//2、校验数据完整性并得到msgid的集合
					String[] msgidArray = BusinessFacade.checkIfComplete(finorgcode, intredate, MsgConstant.MSG_NO_3139);
					//3、调用通知接口
					if(null != msgidArray && msgidArray.length > 0) {
						FinReportService finReportService = new FinReportService();
						finReportService.readReportNotice(finorgcode, intredate, MsgConstant.MSG_NO_3139,String.valueOf(childpacknum),msgidArray);
						BusinessFacade.checkAndSaveRecvlog(finorgcode, intredate, MsgConstant.MSG_NO_3139, StateConstant.COMMON_YES);
					}
				}
			}
			
			eventContext.setStopFurtherProcessing(true);
			return;

		} catch (JAFDatabaseException e1) {
			logger.error("解析保存3139报文出现异常!", e1);
			throw new ITFEBizException("解析保存3139报文出现异常!", e1);
		} catch (SequenceException e) {
			logger.error("解析保存3139报文出现异常!", e);
			throw new ITFEBizException("解析保存3139报文出现异常!", e);
		} catch (ValidateException e) {
			logger.error("解析保存3139报文出现异常!", e);
			throw new ITFEBizException("解析保存3139报文出现异常!", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("解析保存3139报文出现异常!", e);
			throw new ITFEBizException("解析保存3139报文出现异常!", e);
		}
	}
	private void saveDownloadReportCheck(String date,String trecode)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setSliushui("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSliushui())||null==dto.getSliushui())
				{
					dto.setSliushui("1");
					DatabaseFacade.getODB().update(dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}catch(Exception e)
		{
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}
	}
}
