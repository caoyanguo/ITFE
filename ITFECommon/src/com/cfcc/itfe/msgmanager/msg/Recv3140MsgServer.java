package com.cfcc.itfe.msgmanager.msg;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;


/**
 *�����ı���漰��3140��
 * 
 * ֻת�������������κδ���
 * 
 */
public class Recv3140MsgServer extends AbstractMsgManagerServer {

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try{
			// ����Tips����ҵ��--����MQ��Ϣ�����ʼ��
			eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
//			HashMap headMap = (HashMap) cfxMap.get("HEAD");
	
			// ����ͷ��ϢCFX->HEAD
//			String sorgcode = (String) headMap.get("SRC"); // �����������
//			String sdescode = (String) headMap.get("DES");// ���սڵ����
//			String msgNo = (String) headMap.get("MsgNo");// ���ı��
//			String msgID = (String) headMap.get("MsgID");// ���ı�ʶ��
			// String msgRef = (String) headMap.get("MsgRef");// ���Ĳο���
			// String sdate = (String) headMap.get("WorkDate");// ��������
	
			/**
			 * ȡ�û�ִͷ��Ϣ
			 */
			HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3140");
	
			String strecode = (String) batchHeadMap.get("DrawBackTreCode"); //�˿�������
			String sfinorgcode = (String) batchHeadMap.get("FinOrgCode");// �������ش���
			String entrustDate = (String) batchHeadMap.get("EntrustDate");// ί������
//			String sentrustDate = (String) batchHeadMap.get("EntrustDate");// ί������
			String spackno = (String) batchHeadMap.get("PackNo"); // ����ˮ��
			
			List retTreasury3140 = (List) msgMap.get("RetTreasury3140");// ˰����ϸ
			if(retTreasury3140!=null&&retTreasury3140.size()>0)
			{
				HashMap retTreasury3140Map = null;
				List<IDto> dtolist = new ArrayList<IDto>();
				List<IDto> vdtolist = new ArrayList<IDto>();
				TvDwbkDto dwbkdto = null;
				TvVoucherinfoDto vdto = null;
				TsConvertfinorgDto cDto=new TsConvertfinorgDto();
				cDto.setSfinorgcode(sfinorgcode);
				cDto.setStrecode(strecode);
				List<TsConvertfinorgDto> cdtoList=CommonFacade.getODB().findRsByDto(cDto);
				for(int i=0;i<retTreasury3140.size();i++)
				{
					retTreasury3140Map = (HashMap) retTreasury3140.get(i);
					String traNo = (String) retTreasury3140Map.get("TraNo");// ������ˮ��
					String taxOrgCode = (String) retTreasury3140Map.get("TaxOrgCode");// ���ջ��ش���
//					String payeeBankNo = (String) retTreasury3140Map.get("PayeeBankNo");// �տ����к�
					String payeeOpBkCode = (String) retTreasury3140Map.get("PayeeOpBkCode");//�տ�����к�
					String PayeeAcct = (String) retTreasury3140Map.get("PayeeAcct");// �տ����˺�
					String payeeName = (String) retTreasury3140Map.get("PayeeName");// �տ�������
					String taxPayCode = (String) retTreasury3140Map.get("TaxPayCode");// ��˰�˱���
//					String taxPayName = (String) retTreasury3140Map.get("TaxPayName");// ��˰������
					String ElectroTaxVouNo = (String) retTreasury3140Map.get("ElectroTaxVouNo");//�˿�ƾ֤���
//					String electroTaxVouNo = (String) retTreasury3140Map.get("ElectroTaxVouNo");// ����ƾ֤����
//					String oriTaxVouNo = (String) retTreasury3140Map.get("OriTaxVouNo");// ԭ˰Ʊ����
					String traAmt = (String) retTreasury3140Map.get("TraAmt");// ���׽��
					String billDate = (String) retTreasury3140Map.get("BillDate");// ��Ʊ����
//					String corpCode = (String) retTreasury3140Map.get("CorpCode");//��ҵ����
					String budgetType = (String) retTreasury3140Map.get("BudgetType");// Ԥ������
					String budgetSubjectCode = (String) retTreasury3140Map.get("BudgetSubjectCode");// Ԥ���Ŀ����
					String budgetLevelCode = (String) retTreasury3140Map.get("BudgetLevelCode");// Ԥ�㼶�δ���
					String trimSign = (String) retTreasury3140Map.get("TrimSign");// �����ڱ�־
					String viceSign = (String) retTreasury3140Map.get("ViceSign");//������־
					String drawBackReasonCode = (String) retTreasury3140Map.get("DrawBackReasonCode");// �˿�ԭ�����
					String drawBackVou = (String) retTreasury3140Map.get("DrawBackVou");// �˿�����
//					String examOrg = (String) retTreasury3140Map.get("ExamOrg");// ��������
//					String authNo = (String) retTreasury3140Map.get("AuthNo");// ��׼�ĺ�
//					String transType = (String) retTreasury3140Map.get("TransType");//ת������
//					String opStat = (String) retTreasury3140Map.get("OpStat");// ����״̬
					
					dwbkdto = new TvDwbkDto();
					vdto = new TvVoucherinfoDto();
					String mainvou = VoucherUtil.getGrantSequence();
					vdto.setSdealno(mainvou);
					dwbkdto.setIvousrlno(Long.valueOf(mainvou));
					dwbkdto.setSastflag(viceSign);
					
					dwbkdto.setSbizno(mainvou);
					dwbkdto.setSdealno(mainvou.substring(8, 16));
					dwbkdto.setSelecvouno(traNo);
					dwbkdto.setSdwbkvoucode(traNo);
					dwbkdto.setSpayertrecode(strecode);
					dwbkdto.setSaimtrecode(strecode);
					dwbkdto.setCbckflag(StateConstant.MSG_BACK_FLAG_NO);
					dwbkdto.setCbdgkind(MsgConstant.BDG_KIND_IN);
					dwbkdto.setCtrimflag(trimSign);
					dwbkdto.setSpayeeopnbnkno(payeeOpBkCode);
					dwbkdto.setCbdglevel(budgetLevelCode);
					dwbkdto.setSpackageno(spackno);
					dwbkdto.setSpayeeacct(PayeeAcct);
					dwbkdto.setSpayeecode(taxPayCode);
					dwbkdto.setSbdgsbtcode(budgetSubjectCode);//��Ŀ����
					dwbkdto.setSpayeename(payeeName);
					dwbkdto.setDaccept(TimeFacade.getCurrentDateTime());
					dwbkdto.setDvoucher(CommonUtil.strToDate(billDate));
					dwbkdto.setDacct(TimeFacade.getCurrentDateTime());
					dwbkdto.setDbill(TimeFacade.getCurrentDateTime());
					dwbkdto.setSbookorgcode(cdtoList.get(0).getSorgcode());
					dwbkdto.setSfilename((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
					dwbkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//״̬
					dwbkdto.setSfundtypecode(budgetType);//�ʽ����ʱ���
					dwbkdto.setSfundtypename(MsgConstant.BDG_KIND_IN.equals(budgetType)?"Ԥ����":"Ԥ����");//�ʽ���������
					dwbkdto.setSclearbankcode(strecode);
					dwbkdto.setSclearbankname("");
					dwbkdto.setSdwbkreasoncode(drawBackReasonCode);//�˻�ԭ��
					dwbkdto.setSdemo("Tips�·��˿ⱨ��3140");
					dwbkdto.setSdwbkby(drawBackVou);
					dwbkdto.setXagentbusinessno("");
					dwbkdto.setSpayacctno("");//�������˺�
					dwbkdto.setSpayacctname("");//����������
					dwbkdto.setSpayacctbankname("");//����������
					dwbkdto.setSbgttypecode(budgetType);//Ԥ�����ͱ���
					dwbkdto.setSbgttypename(MsgConstant.BDG_KIND_IN.equals(budgetType)?"Ԥ����":"Ԥ����");//Ԥ�����ͱ���
					dwbkdto.setSprocatcode("");//��֧�������
					dwbkdto.setSprocatname("");//��֧��������
					dwbkdto.setSagencyname("");//Ԥ�㵥λ����
					dwbkdto.setSincomesortname("");//��������Ŀ����
					dwbkdto.setSincomesortname1("");//��������Ŀ����
					dwbkdto.setSincomesortname2("");//��������Ŀ����
					dwbkdto.setSincomesortname3("");//��������Ŀ����
					dwbkdto.setSincomesortname4("");//��������Ŀ����
					dwbkdto.setSincomesortcode1("");//��������Ŀ�����
					dwbkdto.setSincomesortcode2("");//��������Ŀ�����
					dwbkdto.setSincomesortcode3("");//��������Ŀ�����
					dwbkdto.setSincomesortcode4("");//��������Ŀ�����
					dwbkdto.setSbiztype("5230");
					dwbkdto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
					dwbkdto.setFamt(BigDecimal.valueOf(Double.valueOf(traAmt)));//���
					dwbkdto.setFdwbkamt(dwbkdto.getFamt());
					dwbkdto.setShold2(String.valueOf(dwbkdto.getFamt()));
					dwbkdto.setStaxorgcode(taxOrgCode);
					vdto.setNmoney(MtoCodeTrans.transformBigDecimal(traAmt));
					vdto.setSadmdivcode(cdtoList.get(0).getSadmdivcode());
					vdto.setScreatdate(TimeFacade.getCurrentStringTime());
					vdto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));
					vdto.setSfilename((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
					vdto.setSorgcode(cdtoList.get(0).getSorgcode());
					vdto.setShold1("");
					vdto.setShold2("");
					vdto.setShold3("");
					vdto.setShold4("");
					vdto.setSext1("");// ֧����ʽ
					vdto.setSext3("");// ҵ������
					vdto.setSext2("");// 
					vdto.setSext4("");// Ԥ������
					vdto.setSext5("");//
					vdto.setSstyear(vdto.getScreatdate().substring(0,4));
					vdto.setStrecode(strecode);
					vdto.setSvoucherflag("1");
					vdto.setSvoucherno(ElectroTaxVouNo);
					vdto.setSvtcode("5230");
					vdto.setSpaybankcode(sfinorgcode); // ��Ʊ��λ
					vdto.setSext1(StateConstant.PAYOUT_PAY_CODE);
					vdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					vdto.setSdemo("Tips�·��˿�3140����");
					vdto.setSattach(drawBackReasonCode);
					dtolist.add(dwbkdto);
					vdtolist.add(vdto);
				}
				if(dtolist!=null&&dtolist.size()>0)
				{
					DatabaseFacade.getDb().create(CommonUtil.listTArray(dtolist));
					saveDownloadReportCheck(entrustDate,strecode,dtolist.size());
				}
				if(vdtolist!=null&&vdtolist.size()>0)
					DatabaseFacade.getDb().create(CommonUtil.listTArray(vdtolist));
			}
		}catch(Exception e)
		{
			
		}
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
	private void saveDownloadReportCheck(String date,String trecode,int size)
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
				finddto.setItuikucount(new Integer(size));
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if(dto.getItuikucount()!=null&&dto.getItuikucount()>=0)
				{
					dto.setItuikucount(dto.getItuikucount()+new Integer(size));
				}else
				{
					dto.setItuikucount(new Integer(size));
				}
				DatabaseFacade.getODB().update(dto);
			}
		} catch (JAFDatabaseException e) {
			log.error("�������ر����������ʧ��:"+e.toString());
		}catch(Exception e)
		{
			log.error("�������ر����������ʧ��:"+e.toString());
		}
	}
}	

