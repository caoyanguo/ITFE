package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * ֱ��֧�����ת��
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5102 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5102.class);
	
	/**
	 * DTOת��XML����(ֱ��֧��ҵ��)
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
	public static Map tranfor(List<TvDirectpaymsgmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5102);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}

		// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> head5102Map = new HashMap<String, Object>();
		head5102Map.put("TreCode", list.get(0).getStrecode()); // �������
		head5102Map.put("BillOrg", list.get(0).getSpayunit()); // ��Ʊ��λ
		head5102Map.put("TransBankNo", list.get(0).getStransbankcode()); // ת������
		head5102Map.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", "")); // ί������
		head5102Map.put("PackNo", list.get(0).getSpackageno()); // �����
		head5102Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head5102Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����
		
		List<Object> bill5102List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill5102Map = new HashMap<String, Object>();
			List<Object> detail5102List = new ArrayList<Object>();

			TvDirectpaymsgmainDto maindto = list.get(i);
			List sublist = new ArrayList();
		    if (maindto.getStrecode().startsWith("19") && !maindto.getStrecode().startsWith("1906")) {
		        SQLExecutor execDetail = null;
		        String sql = "SELECT ROW_NUMBER() OVER() AS I_DETAILSEQNO, SUM(N_MONEY) AS N_MONEY,S_BUDGETUNITCODE,S_FUNSUBJECTCODE FROM TV_DIRECTPAYMSGSUB WHERE I_VOUSRLNO='" + maindto.getIvousrlno() + 
		          "' GROUP BY S_BUDGETUNITCODE,S_FUNSUBJECTCODE order by S_BUDGETUNITCODE,S_FUNSUBJECTCODE";
		        try {
		          execDetail = DatabaseFacade.getODB()
		            .getSqlExecutorFactory().getSQLExecutor();
		          sublist = (List)execDetail.runQuery(sql.toString(), 
		            TvDirectpaymsgsubDto.class).getDtoCollection();
		        } catch (Exception e) {
		          if (execDetail != null)
		            execDetail.closeConnection();
		          logger.error(e.getMessage(), e);
		          throw new ITFEBizException(sql.toString() + "��ѯ����֧���˿���ϸ��Ϣ�쳣��", e);
		        } finally {
		          if (execDetail != null)
		            execDetail.closeConnection();
		        }
		      } else {
		         sublist = PublicSearchFacade.findSubDtoByMain(maindto);
		      }
		
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��![" + list.get(i).toString() + "]");
			}
			//���������ҵ���ӱ����Ϻ���չ��
			sublist = MtoCodeTrans.transformZeroAmtSubDto(maindto, sublist);
			allamt = allamt.add(maindto.getNmoney());

			bill5102Map.put("TraNo", maindto.getSdealno()); // ������ˮ��
//			if(orgcode.substring(0, 2).equals("11")){
//				if(maindto.getNmoney().compareTo(BigDecimal.ZERO)<0)
//					bill5102Map.put("VouNo", "-"+maindto.getSpackageticketno());
//				else
//					bill5102Map.put("VouNo", maindto.getSpackageticketno()); 
//			}else
//			{
				bill5102Map.put("VouNo", maindto.getSpackageticketno()); // ƾ֤���
//			}
			bill5102Map.put("VouDate", maindto.getSgenticketdate().replaceAll("-", "")); // ƾ֤����[��Ʊ����]
			bill5102Map.put("SumAmt", MtoCodeTrans.transformString(maindto.getNmoney())); // �ϼƽ��
			bill5102Map.put("BudgetType", maindto.getSbudgettype()); // Ԥ������
			bill5102Map.put("OfYear", maindto.getSofyear()); // �������
			bill5102Map.put("RransactOrg", MtoCodeTrans.transformString(maindto.getStransactunit())); // ���쵥λ
			bill5102Map.put("AmtKind", maindto.getSamttype()); // �������
			bill5102Map.put("StatInfNum", sublist.size()); // ͳ����Ϣ����
			String s = "0";
			 try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("���ҹ�������쳣!", e);
				throw new ITFEBizException("���ҹ�������쳣��", e);
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,")&&(filename!=null&&filename.endsWith(".msg")))
				sublist = tranList(sublist);
			// ѭ������Ϣ
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail5102Map = new HashMap<String, Object>();
				TvDirectpaymsgsubDto subdto = (TvDirectpaymsgsubDto) sublist.get(j);
				detail5102Map.put("SeqNo", j+1);
				detail5102Map.put("BdgOrgCode", subdto.getSbudgetunitcode());
				detail5102Map.put("FuncBdgSbtCode", subdto.getSfunsubjectcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5102Map.put("EcnomicSubjectCode", subdto.getSecosubjectcode());
				}else{
					detail5102Map.put("EcnomicSubjectCode", "");
				}
				detail5102Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney()));
				detail5102List.add(detail5102Map);
			}

			bill5102Map.put("Detail5102", detail5102List);
			bill5102List.add(bill5102Map);

		}

		head5102Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5102", head5102Map);
		msgMap.put("Bill5102", bill5102List);

		/*if(!isRepeat){
			// �����ط�
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSorgcode(orgcode); // ��������
			packdto.setStrecode(list.get(0).getStrecode()); // �������
			packdto.setSfilename(filename); // �����ļ���
			packdto.setStaxorgcode(list.get(0).getSpayunit()); // ���ش���
			packdto.setScommitdate(list.get(0).getScommitdate()); // ί������
			packdto.setSaccdate(TimeFacade.getCurrentStringTime()); // ��������
			packdto.setSpackageno(packno); // ����ˮ��
			packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN); // ҵ������
			packdto.setIcount(list.size()); // �ܱ���
			packdto.setNmoney(allamt); // �ܽ��
			packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
			packdto.setSusercode(list.get(0).getSusercode()); // ����Ա����
			packdto.setImodicount(1);

			try {
				DatabaseFacade.getDb().create(packdto);
			} catch (JAFDatabaseException e) {
				logger.error("���汨��ͷ��ʱ������쳣��", e);
				throw new ITFEBizException("���汨��ͷ��ʱ������쳣��", e);
			}
		}*/
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	public static List<IDto> tranList(List<IDto> sublist)
	{
		List<IDto> returnlist = new ArrayList<IDto>();
		if(sublist!=null&&sublist.size()>0)
		{
			TvDirectpaymsgsubDto subdto = null;
			Map<String,TvDirectpaymsgsubDto> packMap = new HashMap<String,TvDirectpaymsgsubDto>();
			for(int i=0;i<sublist.size();i++)
			{
				subdto = (TvDirectpaymsgsubDto)sublist.get(i);
				subdto.setSbudgetunitcode(subdto.getSbudgetunitcode().substring(0, 7));
				if(packMap.get(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode())==null)
				{
					packMap.put(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode(), subdto);
				}else
				{
					packMap.get(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode()).setNmoney(packMap.get(subdto.getSbudgetunitcode()+subdto.getSbudgetunitcode()).getNmoney().add(subdto.getNmoney()));
				}
			}
			Set<String> set = packMap.keySet();
			for(String key:set)
				returnlist.add(packMap.get(key));
		}
		return returnlist;
	}
}
