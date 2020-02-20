package com.cfcc.itfe.component;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TipsTbDrawbackDto;
import com.cfcc.itfe.util.FileRootPathUtil;
import com.cfcc.itfe.util.transformer.Dto2MapForBigData1104;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 
 * 组装退库报文，给财政发送通知
 * 
 */
public class TimerSendDwbkComponent implements Callable {

	private static Log logger = LogFactory.getLog(TimerSendDwbkComponent.class);

	@SuppressWarnings("unchecked")
	public Object onCall(MuleEventContext eventContext) throws Exception {
        
		//1.组装退库报文 TIPS_TB_DRAWBACK
		//String sql = "SELECT DISTINCT S_TAXORGCODE FROM TIPS_TB_DRAWBACK WHERE D_ENTRUST = ? AND S_EXPSTATE = ?";
		String sql = "SELECT DISTINCT S_TAXORGCODE,D_ENTRUST FROM TIPS_TB_DRAWBACK WHERE S_EXPSTATE = ?";
		SQLExecutor sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
		//sqlExe.addParam(yesterday());
		sqlExe.addParam("1");
		SQLResults rs = sqlExe.runQueryCloseCon(sql, TipsTbDrawbackDto.class);
		
		if (rs.getDtoCollection() != null && rs.getDtoCollection().size() > 0) {
			for (TipsTbDrawbackDto tempDto : (ArrayList<TipsTbDrawbackDto>)rs.getDtoCollection()){
				List<String> msgidStrList = new ArrayList<String>();
				List<TipsTbDrawbackDto> list = new ArrayList<TipsTbDrawbackDto>();
				//tempDto.setDentrust(yesterday());
				tempDto.setSexpstate("1");
				list = CommonFacade.getODB().findRsByDtoWithUR(tempDto);
				
				List<List<TipsTbDrawbackDto>> listdto = getSubLists(list,1000);
				for(int i=0;i<listdto.size();i++){
					Map map = Dto2MapForBigData1104.tranfor(listdto.get(i), null, null, null, true);
					/**
					 * 更新状态
					 */
					for(TipsTbDrawbackDto updateDto : listdto.get(i)){
						updateDto.setSexpstate("0"); //0--未生成报文       1--生成报文
					}
					TipsTbDrawbackDto[] mainDtos=new TipsTbDrawbackDto[listdto.get(i).size()];
					mainDtos = (TipsTbDrawbackDto[])listdto.get(i).toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					/**
					 *  保存文件
					 */
					String filename = "";
					try {
						String ls = "";
						MuleMessage message = new DefaultMuleMessage(ls);
						MuleClient client = new MuleClient();
						message.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_1104+"2");
						filename = FileRootPathUtil.getInstance().getRoot()+"MsgLog"+File.separator+"recvMsgLog"+File.separator+TimeFacade.getCurrentStringTime()+File.separator
							+"1104_"+UUID.randomUUID().toString().replace("-","")+".msg";
						
						message.setProperty("fileName", filename);
						message.setProperty("xml", map);
						message = client.send("vm://VoucherXmlTransformer", message);
					} catch (Exception e) {
						logger.error(e);
						//throw new ITFEBizException("生成报文异常！", e);
					}
					String _srecvno = StampFacade.getStampSendSeq("JS");
					HashMap cfxMap = (HashMap) map.get("cfx");
					HashMap headMap = (HashMap)cfxMap.get("HEAD");
					HashMap msgMap = (HashMap)cfxMap.get("MSG");
					HashMap BatchHead1104 = (HashMap)msgMap.get("BatchHead1104");
					String spackno = (String) BatchHead1104.get("PackNo"); // 包流水号
					BigDecimal sumamt = new BigDecimal((String) BatchHead1104.get("AllAmt")); // 总金额
					String msgid = (String)headMap.get("MsgID");
					msgidStrList.add(msgid);
					//2.记录接收日志表
					MsgLogFacade.writeRcvLog(_srecvno, null, listdto.get(i).get(0).getSdwbktrecode(), TimeFacade.getCurrentStringTimebefor(), MsgConstant.MSG_NO_1104,
							ITFECommonConstant.SRC_NODE, filename,listdto.get(i).size(), sumamt, spackno, listdto.get(i).get(0).getSdwbktrecode(), null,null, null, 
							(String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,"1", null, "");
					
				}
				
			/*	if(msgidStrList.size()>0){
					FinReportService finReportService = new FinReportService();
					try {
						finReportService.readReportNotice(tempDto.getStaxorgcode(), TimeFacade.getCurrentStringTime(), MsgConstant.MSG_NO_1104,"1",msgidStrList.toArray(new String[msgidStrList.size()]));
					} catch (UnsupportedEncodingException e) {
						String error = "向财政发送报文时出现异常！";
						logger.error(error, e);
						throw new ITFEBizException(error, e);
					}
				}*/
				
			}
		}
		
		return eventContext;
	}

	/**
	 * 
	 * @param list 
	 * @param subsize
	 * @return
	 */
	private List getSubLists(List list,int subsize)
	{
		List getList = null;
		if(list!=null&&list.size()>0)
		{
			if(subsize<1)
				subsize=1000;
			int count = list.size()/subsize;
			int yu = list.size()%subsize;
			getList = new ArrayList();
			for(int i=0;i<count;i++)
				getList.add(list.subList(i*subsize, subsize*(i+1)));
			if(yu>0)
				getList.add(list.subList(count*subsize, (count*subsize)+yu));
		}
		return getList;
	}

	/**
	 * 指定日期的上一日
	 * 
	 * @param date
	 *            指定日期
	 * @return 上一日
	 */
	public static java.sql.Date yesterday() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
	    return new java.sql.Date(calendar.getTimeInMillis());
	}
}
