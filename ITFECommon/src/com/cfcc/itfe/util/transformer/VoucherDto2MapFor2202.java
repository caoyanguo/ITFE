package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ���������˿�(�Ϻ���չ)	
 * �տ������˿�֪ͨ2252����
 * @author hejianrong
 * time 2014-10-16
 * 
 */
public class VoucherDto2MapFor2202 {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2202.class);
	
	/**
	 * DTOת��XML����(���������˿�ҵ��)
	 * 
	 * @param List
	 *            <TvDirectpaymsgmainDto> list ���ͱ��Ķ��󼯺�
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            packno ����ˮ��
	 * @param boolean isRepeat �Ƿ��ط�����
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TfPaybankRefundmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
		if (null == list || list.size() == 0) {
			throw new ITFEBizException("Ҫת���Ķ���Ϊ��,��ȷ��!");
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// ���ñ��Ľڵ� CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// ����ͷ HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.APPLYPAY_BACK_DAORU);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("Reserve", MsgConstant.CFCCHL);
		 
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}

		// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> head2202Map = new HashMap<String, Object>();
		
		head2202Map.put("FinOrgCode", list.get(0).getSfinorgcode());
		head2202Map.put("TreCode", list.get(0).getStrecode()); // �������
		head2202Map.put("AgentBnkCode", list.get(0).getSpaysndbnkno());
		head2202Map.put("EntrustDate", list.get(0).getScommitdate());
		head2202Map.put("PackNo", list.get(0).getSpackageno()); // �����
		head2202Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head2202Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����: 0����ֽƾ֤1����ֽƾ֤
		head2202Map.put("PayMode",list.get(0).getSpaytypecode());
		
		List<Object> bill2202List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill2202Map = new HashMap<String, Object>();
			List<Object> detail2202List = new ArrayList<Object>();

			TfPaybankRefundmainDto maindto = list.get(i);
			//��2252�����Ӧ��5201����
			TvVoucherinfoDto dto5201=(TvVoucherinfoDto) VoucherUtil.findVoucherDto(
					maindto.getStrecode(),MsgConstant.VOUCHER_NO_5201 , 
					maindto.getSoriginalvoucherno(), DealCodeConstants.VOUCHER_SUCCESS).get(0);
			TfDirectpaymsgmainDto maindto5201 = new TfDirectpaymsgmainDto();
			maindto5201.setIvousrlno(Long.parseLong(dto5201.getSdealno()));
			try {
				maindto5201=(TfDirectpaymsgmainDto) DatabaseFacade.getODB().find(maindto5201);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException(e);
			}
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			//��2252�ӱ��Ӧ��5201�ӱ���ϸ
			HashMap	subdtoMap5201=VoucherUtil.convertListToMap(PublicSearchFacade.findSubDtoByMain(maindto5201));
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��![" + list.get(i).toString() + "]");
			}

			allamt = allamt.add(maindto.getNpayamt());

			bill2202Map.put("TraNo", maindto.getSdealno()); // ������ˮ��
			bill2202Map.put("VouNo", maindto.getSvoucherno()); // ƾ֤���
			bill2202Map.put("VouDate", maindto.getSvoudate()); // ƾ֤����[��Ʊ����]
			bill2202Map.put("OriTraNo", maindto.getSpaydictateno());
			bill2202Map.put("OriEntrustDate", maindto.getSpayentrustdate());
			bill2202Map.put("OriVouNo", maindto.getSoriginalvoucherno());
			bill2202Map.put("OriVouDate", maindto5201.getSvoudate());
			bill2202Map.put("OriPayerAcct", maindto5201.getSpayacctno());//ԭ�������˺�
			bill2202Map.put("OriPayerName",  maindto5201.getSpayacctname());//ԭ����������
			bill2202Map.put("OriPayeeAcct", maindto5201.getSpayeeacctno());//ԭ�տ����˺�
			bill2202Map.put("OriPayeeName", maindto5201.getSpayeeacctname());//ԭ�տ�������
			bill2202Map.put("PayDictateNo", maindto.getSpaydictateno());
			bill2202Map.put("PayMsgNo",maindto.getSpaymsgno());
			bill2202Map.put("PayEntrustDate",maindto.getSpayentrustdate() );
			bill2202Map.put("PaySndBnkNo", maindto.getSpaysndbnkno());
			//���maindto5201.getSbgttypecode()Ϊ�գ�Ĭ��ΪԤ����
			if(StringUtils.isBlank(maindto5201.getSbgttypecode())){
				bill2202Map.put("BudgetType", MsgConstant.BDG_KIND_IN);
			}else{
				bill2202Map.put("BudgetType", maindto5201.getSbgttypecode());
			}
			bill2202Map.put("TrimSign", MsgConstant.TIME_FLAG_NORMAL);
			bill2202Map.put("OfYear", maindto.getSstyear());
			bill2202Map.put("Amt",  MtoCodeTrans.transformString(maindto.getNpayamt()));
			bill2202Map.put("StatInfNum", sublist.size());
			String s = "0";
			 try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("���ҹ�������쳣!", e);
				throw new ITFEBizException("���ҹ�������쳣��", e);
			}
			
			// ѭ������Ϣ
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail2202Map = new HashMap<String, Object>();
				TfPaybankRefundsubDto subdto = (TfPaybankRefundsubDto) sublist.get(j);
				TfDirectpaymsgsubDto subdto5201=(TfDirectpaymsgsubDto) subdtoMap5201.get(subdto.getSid());
				detail2202Map.put("SeqNo", (j+1));
				detail2202Map.put("BdgOrgCode", subdto5201.getSagencycode());
				detail2202Map.put("FuncSbtCode", subdto5201.getSexpfunccode());
				if (StateConstant.COMMON_YES.equals(s)) {//�������Ŀ����
					detail2202Map.put("EcnomicSubjectCode", MtoCodeTrans.transformString(subdto5201.getSexpecocode()));
				}else{
					detail2202Map.put("EcnomicSubjectCode", "");
				}
				detail2202Map.put("Amt", MtoCodeTrans.transformString(subdto.getNpayamt()) );
				detail2202Map.put("AcctProp", MsgConstant.ACCT_PROP_ZERO);//�˻�����	     Ĭ�� 1 ����� 
				detail2202List.add(detail2202Map);
			}

			bill2202Map.put("Detail2202", detail2202List);
			bill2202List.add(bill2202Map);
		}

		head2202Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("Head2202", head2202Map);
		msgMap.put("Bill2202", bill2202List);
		return map;
	}

}
