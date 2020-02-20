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
 * ��Ҫ���ܣ�6.4.10�����������ˮ��ϸ
 * 
 * @author wangyunbin
 * 
 */
public class Proc3139MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3139MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * �����������ͷ MSG->BillHead3139
		 */
		HashMap billhead3139 = (HashMap) msgMap.get("BillHead3139");

		String finorgcode = (String) billhead3139.get("FinOrgCode");// �������ش���
		String trecode = (String) billhead3139.get("TreCode");// �������
		String intredate = (String) billhead3139.get("InTreDate");// ���ƾ֤����
		String packno = (String) billhead3139.get("PackNo");// ����ˮ��
		int childpacknum = Integer.valueOf(billhead3139.get("ChildPackNum")
				.toString());// �Ӱ�����
		String curpackno = (String) billhead3139.get("CurPackNo");// �������
		String taxallnum = (String) billhead3139.get("TaxAllNum");// ����˰Ʊ�ܱ���
		String drawbackallnum = (String) billhead3139.get("DrawBackAllNum");// �����˿��ܱ���
		String corrallnum = (String) billhead3139.get("CorrAllNum");// ���������ܱ���
		int freeallnum = Integer.valueOf(billhead3139.get("FreeAllNum")
				.toString());// ������ֵ��ܱ���
		String sbookorgcode = (String) headMap.get("DES");
		String sqladddetail = "(";
		String sqladdsum = "(";
		List <String> deatilList = new ArrayList<String>();
		List <String> sumList = new ArrayList<String>();
		
		

		List<IDto> list = new ArrayList<IDto>();
		// ��ѯ������־
		TvRecvlogDto logDto = new TvRecvlogDto();
		logDto.setSseq((String) headMap.get("MsgID"));
		try {
			//�ж��Ƿ����ǹ���ά���Ĳ����������룬��ֹά����������������������ˮ�ظ���
			// �����������뻺��keyΪ��������
			HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (!mapFincInfoByFinc.containsKey(finorgcode)) {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			// ���ݹ����ѯ��������
			TsTreasuryDto _dto = SrvCacheFacade.cacheTreasuryInfo(null).get(
					trecode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			List<TvRecvlogDto> finddtolist = CommonFacade.getODB().findRsByDto(
					logDto);
			// �Ѿ�������־��¼
			if (null == finddtolist || finddtolist.size() == 0) {
				List returnList = (List) msgMap.get("Bill3139");
				if (null == returnList || returnList.size() == 0) {
					return;
				} else {

					int count = returnList.size();
					for (int i = 0; i < count; i++) {
						HashMap bill3139map = (HashMap) returnList.get(i);
						String TaxOrgCode = (String) bill3139map
								.get("TaxOrgCode"); // ���ջ��ش���
						String ExportVouType = (String) bill3139map
								.get("ExportVouType"); // ����ƾ֤����
						String ExpTaxVouNo = (String) bill3139map
								.get("ExpTaxVouNo"); // ƾ֤����
						String BudgetType = (String) bill3139map
								.get("BudgetType"); // Ԥ������
						String BudgetLevelCode = (String) bill3139map
								.get("BudgetLevelCode"); // Ԥ�㼶��
						String BudgetSubjectCode = (String) bill3139map
								.get("BudgetSubjectCode"); // Ԥ���Ŀ
						String TraAmt = (String) bill3139map.get("TraAmt"); // ���׽��
						String Origin = (String) bill3139map.get("Origin"); // ƾ֤��Դ
						TvFinIncomeDetailDto dto = new TvFinIncomeDetailDto();
						/**
						 * ��֯DTO׼����������*****************************
						 */
						String _sseq = "";
						try {
							_sseq = StampFacade.getStampSendSeq("JS"); // ȡ�������������ˮ��Ϣҵ����ˮ
						} catch (SequenceException e) {
							logger.error(e);
							throw new ITFEBizException("ȡ�������������ˮ��Ϣҵ����ˮSEQ����");
						}
						dto.setSseq(_sseq);// ҵ����ˮ��
						dto.setCbdgkind(BudgetType.trim());// Ԥ������
						dto.setCbdglevel(BudgetLevelCode.trim());// Ԥ�㼶��
						if (Origin == null || Origin.trim().equals("")) {
							dto.setCvouchannel("");// ƾ֤��Դ
						} else {
							dto.setCvouchannel(Origin.trim());// ƾ֤��Դ
						}
						dto.setFamt(MtoCodeTrans.transformBigDecimal(TraAmt));// ���
						dto.setIpkgseqno(packno.trim());// ����ˮ��
						dto.setSbdgsbtcode(BudgetSubjectCode.trim());// Ԥ���Ŀ
						dto.setSexpvouno(ExpTaxVouNo.trim());// ƾ֤����
						dto.setSexpvoutype(ExportVouType.trim());// ����ƾ֤����
						dto.setStaxorgcode(TaxOrgCode.trim());// ���ջ��ش���
						dto.setStrecode(trecode.trim());// �������
						dto.setSintredate(intredate.trim());// ���ƾ֤����
						dto.setSorgcode(finorgcode.trim());// �������ش���

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
			 * ����/������־
			 */
			String recvseqno;// ������־��ˮ��
			String sendseqno;// ������־��ˮ��
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ

			String path = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_FILE_NAME);
			
			// �ǽ�����־
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
			
			//�����ж����Ӳ��������Ƿ��Ѿ�������������������WebService֪ͨ����ȡ��    --20131230 by hua
			if("030010011118".equals((String) headMap.get("DES"))) { //Ŀǰֻ֧�ֱ���
				//1�����ȿ��Ƿ��Ѿ�����������֪ͨ�����û�з��������
				boolean boo = BusinessFacade.checkAndSaveRecvlog(finorgcode, intredate, MsgConstant.MSG_NO_3139, StateConstant.COMMON_NO);
				if(!boo) {
					//2��У�����������Բ��õ�msgid�ļ���
					String[] msgidArray = BusinessFacade.checkIfComplete(finorgcode, intredate, MsgConstant.MSG_NO_3139);
					//3������֪ͨ�ӿ�
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
			logger.error("��������3139���ĳ����쳣!", e1);
			throw new ITFEBizException("��������3139���ĳ����쳣!", e1);
		} catch (SequenceException e) {
			logger.error("��������3139���ĳ����쳣!", e);
			throw new ITFEBizException("��������3139���ĳ����쳣!", e);
		} catch (ValidateException e) {
			logger.error("��������3139���ĳ����쳣!", e);
			throw new ITFEBizException("��������3139���ĳ����쳣!", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("��������3139���ĳ����쳣!", e);
			throw new ITFEBizException("��������3139���ĳ����쳣!", e);
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
			log.error("�������ر����������ʧ��:"+e.toString());
		}catch(Exception e)
		{
			log.error("�������ر����������ʧ��:"+e.toString());
		}
	}
}
