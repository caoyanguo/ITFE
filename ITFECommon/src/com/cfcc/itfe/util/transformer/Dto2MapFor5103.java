package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ֱ��֧�����ת��
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5103 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5103.class);

	/**
	 * DTOת��XML����(��Ȩ֧��ҵ��)
	 * 
	 * @param List
	 *            <TvGrantpaymsgmainDto> list ���ͱ��Ķ��󼯺�
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
	@SuppressWarnings("unchecked")
	public static Map tranfor(List<TvGrantpaymsgmainDto> list, String orgcode,
			String filename, String packno, boolean isRepeat)
			throws ITFEBizException {
		if (null == list || list.size() == 0) {
			throw new ITFEBizException("Ҫת���Ķ���Ϊ��,��ȷ��!");
		}

		// if (list.size() != 1) {
		// throw new ITFEBizException("Ҫת���Ķ����ظ�,��ȷ��!");
		// }

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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5103);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}


		
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		int allcount = 0; // �ܱ���
		HashMap<String, Object> head5103Map = new HashMap<String, Object>();
		List<Object> bill5103List = new ArrayList<Object>();

		for (int i = 0; i < list.size(); i++) {
			allcount++;
			TvGrantpaymsgmainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��!["
						+ maindto.getSlimitid() + "_" + maindto.getSofyear()
						+ "]");
			}
			
			// ���ñ�����Ϣ�� MSG
			head5103Map.put("TreCode", maindto.getStrecode()); // �������
			head5103Map.put("BillOrg", maindto.getSpayunit()); // ��Ʊ��λ
			head5103Map.put("TransBankNo", maindto.getStransbankcode()); // ת������
			head5103Map.put("EntrustDate", maindto.getScommitdate().replaceAll(
					"-", "")); // ί������
			head5103Map.put("PackNo", maindto.getSpackageno()); // �����
			
			// ֧��ƾ֤���� 0��ֽƾ֤1��ֽƾ֤
			head5103Map.put("PayoutVouType",
					MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES);
			
			/**
			 *  ����Bill5103
			 */
			HashMap<String, Object> bill5103Map = new HashMap<String, Object>();
			// ������ˮ��
			bill5103Map.put("TraNo", maindto.getSdealno()); 
			// ƾ֤���
			if(!orgcode.substring(0, 2).equals("11")){
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")<0){
					bill5103Map.put("VouNo", maindto.getSpackageticketno()); 
				}else{
					bill5103Map.put("VouNo", !(maindto.getShold2()!=null&&maindto.getShold2().equals("�Ѳ��ҵ�������־"))?maindto.getSpackageticketno():maindto.getShold1()); 
				} 
			}
			else
			{
				if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("rev5106_")&&maindto.getSfilename().contains(".msg"))
					bill5103Map.put("VouNo", maindto.getSpackageticketno());
				else
				{
					TvGrantpaymsgsubDto sub = (TvGrantpaymsgsubDto) sublist.get(0);
					bill5103Map.put("VouNo", sub.getSpackageticketno() + sub.getSline());
				}
			}
			// ƾ֤����
			bill5103Map.put("VouDate", maindto.getSgenticketdate()
					.replaceAll("-", "")); 
			// Ԥ�㵥λ����
			if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,")&&(filename!=null&&filename.endsWith(".msg")))
				bill5103Map.put("BdgOrgCode", maindto.getSbudgetunitcode().substring(0,7)); 
			else
				bill5103Map.put("BdgOrgCode", maindto.getSbudgetunitcode()); 
			// Ԥ������
			bill5103Map.put("BudgetType", maindto.getSbudgettype()); 
			// �������
			bill5103Map.put("OfYear", maindto.getSofyear()); 
			// �����·�
			String ofmonth = DateUtil.getFormatMonth(Integer
					.parseInt(maindto.getSofmonth()));
			bill5103Map.put("OfMonth", ofmonth); 
			// ���쵥λ
			bill5103Map.put("RransactOrg", MtoCodeTrans
					.transformString(maindto.getStransactunit()));
			// �������
			bill5103Map.put("AmtKind", maindto.getSamttype()); 
			//Ʊ�ݽ��ϼ�
			BigDecimal billamt = new BigDecimal("0.00"); 
			
			String s = "0";
			try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("���ҹ�������쳣!", e);
				throw new ITFEBizException("���ҹ�������쳣��", e);
			}
			
			//���������ҵ���ӱ��Ϻ���չ��
			sublist = MtoCodeTrans.transformZeroAmtSubDto(maindto, sublist);
			
			List<Object> detail5103List = new ArrayList<Object>();
			for (int j = 0; j < sublist.size(); j++) {
				TvGrantpaymsgsubDto subdto = (TvGrantpaymsgsubDto) sublist
						.get(j);
				
				allamt = allamt.add(subdto.getNmoney());
				billamt = billamt.add(subdto.getNmoney());

				// ѭ������Ϣ
				HashMap<String, Object> detail5103Map = new HashMap<String, Object>();

				detail5103Map.put("SeqNo", j+1);
				detail5103Map.put("FuncBdgSbtCode", subdto.getSfunsubjectcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5103Map.put("EcnomicSubjectCode", MtoCodeTrans.transformString(subdto.getSecosubjectcode()));
				}else{
					detail5103Map.put("EcnomicSubjectCode", "");
				}
				detail5103Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney()));
				detail5103Map.put("AcctProp", subdto.getSaccattrib());
				detail5103List.add(detail5103Map);

				bill5103Map.put("Detail5103", detail5103List);
			}
			// ͳ����Ϣ����
			bill5103Map.put("StatInfNum", sublist.size());
			// �ϼƽ��
			bill5103Map.put("SumAmt", MtoCodeTrans.transformString(billamt)); 
			bill5103List.add(bill5103Map);
		}
		head5103Map.put("AllNum", String.valueOf(allcount)); // �ܱ���
		head5103Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5103", head5103Map);
		msgMap.put("Bill5103", bill5103List);

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
