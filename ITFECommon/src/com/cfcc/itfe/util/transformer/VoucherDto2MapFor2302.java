package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ���������˿�ת��
 * 
 * @author wangyunbin
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor2302 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2301.class);
	private Map<String, TsInfoconnorgaccDto> accMap = null;

	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	/**
	 * ƾ֤����
	 * 
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		if (vDto == null)
			return null;
		List returnList = new ArrayList();
		vDto.setShold1("0");
		searchBankBackInfo(vDto, returnList);
		return returnList;
	}

	/**
	 * ��װ�˻���Ϣmap��Ϣ
	 * 
	 * @param vDto
	 * @param returnList
	 * @throws ITFEBizException
	 */
	private void searchBankBackInfo(TvVoucherinfoDto vDto, List returnList)
			throws ITFEBizException {
		TvVoucherinfoDto directVoucherDto;
		try {
			// ͨ��2252���ɵ�ֱ��֧���˿ͨ��ǰ�����ɵ�ֱ��֧���˿�Ҳ�������˿����
			List<TvPayreckBankBackDto> directBankBackList = getPayreckBankBack(vDto);
			if (null != directBankBackList && directBankBackList.size() > 0) {

				List<TsInfoconnorgaccDto> accList = null;
				TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
				accdto.setStrecode(vDto.getStrecode());
				accdto.setSorgcode(vDto.getSorgcode());
				accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB()
						.findRsByDto(accdto);
				if (accList != null && accList.size() > 0) {
					accMap = new HashMap<String, TsInfoconnorgaccDto>();
					for (TsInfoconnorgaccDto tempdto : accList) {
						if (tempdto.getSpayeraccount().indexOf(
								vDto.getSorgcode() + "271") >= 0)
							accMap.put("271", tempdto); // �Ϻ��в����ֹ����
						else if (tempdto.getSpayeraccount().indexOf(vDto.getSorgcode() + "371") >= 0 && !(tempdto.getSpayername().indexOf("��Ȩ") >= 0))
							accMap.put("371", tempdto);
					}
				}

				// �������ں͹�������ѯҵ����ϸ��
				TvPayreckBankBackListDto tvPayreckBankBackListDto;
				List<IDto> directBankBackDetailList;
				for (TvPayreckBankBackDto tvPayreckBankBackDto : directBankBackList) {
					tvPayreckBankBackListDto = new TvPayreckBankBackListDto();
					tvPayreckBankBackListDto.setIvousrlno(tvPayreckBankBackDto
							.getIvousrlno());
					directBankBackDetailList = CommonFacade.getODB()
							.findRsByDto(tvPayreckBankBackListDto);
					if (null == directBankBackDetailList
							|| directBankBackDetailList.size() == 0) {
						continue;
					}
					// ����TvVoucherinfoDto
					directVoucherDto = getVouhcerinfoDto(tvPayreckBankBackDto,
							vDto);
					tvPayreckBankBackDto.setSpayeeacct(accMap.get("271")
							.getSpayeraccount());
					tvPayreckBankBackDto.setSpayeename(accMap.get("271")
							.getSpayername());
					tvPayreckBankBackDto.setSpayeracct(accMap.get("371").getSpayeraccount());
					tvPayreckBankBackDto.setSpayername(accMap.get("371").getSpayername());
					// ����ҵ�����װMap
					Map xmlMapDirect = Dto2XmlMap(tvPayreckBankBackDto,
							directBankBackDetailList, directVoucherDto);
					tvPayreckBankBackDto.setSisreturn("1");		//�Ѿ����ɹ�2302�ļ�¼������������
					DatabaseFacade.getODB().update(tvPayreckBankBackDto);
					List directList = new ArrayList();
					directList.add(xmlMapDirect);
					directList.add(directVoucherDto);
					returnList.add(directList);
					
				}

			}
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ�����쳣��", e);
			throw new ITFEBizException("��ѯ�����쳣��", e);
		} catch (ValidateException e) {
			logger.error("��ѯ�����쳣��", e);
			throw new ITFEBizException("��ѯ�����쳣��", e);
		} catch (ITFEBizException e) {
			logger.error(e.getMessage(), e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}

	/**
	 * @throws ITFEBizException
	 * 
	 */
	private TvVoucherinfoDto getVouhcerinfoDto(
			TvPayreckBankBackDto bankBackDto, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		String dirsep = File.separator;
		TvVoucherinfoDto vDtoReturn = new TvVoucherinfoDto();
		vDtoReturn.setSorgcode(bankBackDto.getSbookorgcode());
		vDtoReturn.setSadmdivcode(bankBackDto.getSadmdivcode());
		vDtoReturn.setSvtcode(MsgConstant.VOUCHER_NO_2302);
		vDtoReturn.setScreatdate(TimeFacade.getCurrentStringTime());
		vDtoReturn.setStrecode(bankBackDto.getStrecode());
		String mainvou = null;
		try {
			mainvou = VoucherUtil.getGrantSequence();
		} catch (ITFEBizException e) {
			logger.debug(e);
		}
		String FileName = ITFECommonConstant.FILE_ROOT_PATH + dirsep
				+ "Voucher" + dirsep + vDtoReturn.getScreatdate() + dirsep
				+ "send" + vDtoReturn.getSvtcode() + "_" + mainvou + ".msg";
		vDtoReturn.setSfilename(FileName);
		vDtoReturn.setSdealno(mainvou);
		vDtoReturn.setSstyear(vDtoReturn.getScreatdate().substring(0, 4));
		vDtoReturn.setScheckdate(vDto.getScheckdate());
		// vDtoReturn.setSpaybankcode(bankBackDto.getSxpaysndbnkno());
		vDtoReturn.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		vDtoReturn.setSdemo("����ɹ�");
		vDtoReturn.setSvoucherflag("1");
		vDtoReturn.setSvoucherno(mainvou);
		vDtoReturn.setIcount(1);
		vDtoReturn
				.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
		BigDecimal moneySum = new BigDecimal(0.00);
		vDtoReturn.setNmoney(bankBackDto.getFamt());
		return vDtoReturn;
	}

	/**
	 * ���ݸ������ں͹�������ѯҵ��� ���������ɵ�2203ֱ��֧���˿����ҵ����У�Ϊ������2302�ȽϷ���
	 * 
	 * @throws ITFEBizException
	 */
	private List<TvPayreckBankBackDto> getPayreckBankBack(TvVoucherinfoDto vDto)
			throws ITFEBizException {
		List<TvPayreckBankBackDto> dtoList = new ArrayList();
		String ls_SQL = "  AND s_paymode ='"
				+ vDto.getShold1()
				+ "' AND S_BACKFLAG = '1' AND S_ISRETURN IS NULL "
				+ " AND S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' "
				+ (StringUtils.isBlank(vDto.getScheckdate()) ? ""
						: " AND D_VOUDATE ='"
								+ CommonUtil.strToDate(vDto.getScheckdate())
								+ "'");
		try {
			TvPayreckBankBackDto _dto = new TvPayreckBankBackDto();
			_dto.setSbookorgcode(vDto.getSorgcode());
			_dto.setStrecode(vDto.getStrecode());
			dtoList = CommonFacade.getODB().findRsByDtoForWhere(_dto, ls_SQL);
		} catch (JAFDatabaseException e) {
			logger.debug(e);
			throw new ITFEBizException("��ѯֱ��֧���˿����ݳ����쳣��", e);
		} catch (ValidateException e) {
			logger.debug(e);
			throw new ITFEBizException("��ѯֱ��֧���˿����ݳ����쳣��", e);
		}
		return dtoList;
	}

	private void TvPayreckBankBackDto() {
		// TODO Auto-generated method stub

	}

	/**
	 * ���ݸ������ں͹�������ѯҵ����ϸ��
	 */
	private List<TvPayreckBankBackListDto> getPayreckBankBackDetail(
			TvVoucherinfoDto vDto) {
		String ls_SQL = " SELECT b.* FROM TV_PAYRECK_BANK_BACK a,TV_PAYRECK_BANK_BACK_LIST b "
				+ "WHERE a.I_VOUSRLNO = b.I_VOUSRLNO "
				+ "AND a.S_TRECODE = '"
				+ vDto.getStrecode()
				+ "' "
				+ "AND replace(CHAR(S_XCLEARDATE,iso),'-','') = '"
				+ vDto.getScheckdate() + "' " + "and a.S_PAYTYPECODE = ? ";
		List<TvPayreckBankBackListDto> bankBackDetailList = new ArrayList();
		try {
			SQLExecutor execDetail = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			execDetail.addParam(vDto.getShold1());// ʹ�� �� �ֶ���ʱ�洢֧����ʽ
			SQLResults result = execDetail.runQueryCloseCon(ls_SQL,
					TvPayreckBankBackListDto.class);
			for (int i = 0; i < result.getRowCount(); i++) {
				TvPayreckBankBackListDto bankBackDetailDto = new TvPayreckBankBackListDto();
				bankBackDetailDto.setIvousrlno(result.getLong(i, "I_VOUSRLNO"));
				bankBackDetailDto.setSid(result.getString(i, "S_ID"));
				bankBackDetailDto.setSorivouno(result
						.getString(i, "S_ORIVOUNO"));
				bankBackDetailDto.setSvoucherno(result.getString(i,
						"S_VOUCHERNO"));
				bankBackDetailDto.setSbdgorgcode(result.getString(i,
						"S_BDGORGCODE"));
				bankBackDetailDto.setSsupdepname(result.getString(i,
						"S_SUPDEPNAME"));
				bankBackDetailDto.setSfuncbdgsbtcode(result.getString(i,
						"S_FUNCBDGSBTCODE"));
				bankBackDetailDto.setSexpfuncname(result.getString(i,
						"S_EXPFUNCNAME"));
				bankBackDetailDto.setFamt(result.getBigDecimal(i, "F_AMT"));
				bankBackDetailList.add(bankBackDetailDto);
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
		}
		return bankBackDetailList;
	}

	private Map Dto2XmlMap(TvPayreckBankBackDto bankBackDto,
			List<IDto> bankBackDetailList, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		// ������������� �����б����ά�� �б����Ϊ 001
		String PayBankName, PayBankNo;
		// ��ȡά���Ĳ���������Ϣ
		TsConvertfinorgDto cDto = new TsConvertfinorgDto();
		cDto.setSorgcode(vDto.getSorgcode());
		cDto.setStrecode(vDto.getStrecode());
		List<TsConvertfinorgDto> tsConvertfinorgList;
		try {
			tsConvertfinorgList = (List<TsConvertfinorgDto>) CommonFacade
					.getODB().findRsByDto(cDto);

			if (tsConvertfinorgList == null || tsConvertfinorgList.size() == 0) {
				throw new ITFEBizException("���⣺" + vDto.getStrecode()
						+ "��Ӧ�Ĳ������ش������δά����");
			}
			cDto = (TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if (cDto.getSadmdivcode() == null
					|| cDto.getSadmdivcode().equals("")) {
				throw new ITFEBizException("���⣺" + cDto.getStrecode()
						+ "��Ӧ����������δά����");
			}
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��", e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��", e2);
		}
		Map map = new HashMap();
		HashMap<String, Object> vouchermap = new HashMap<String, Object>();
		// ���ñ��Ľڵ� Voucher
		map.put("Voucher", vouchermap);
		// ���ñ�����Ϣ��
		vouchermap.put("Id", vDto.getSvoucherno());// �����˿�ƾ֤Id
		vouchermap.put("AdmDivCode", bankBackDto.getSadmdivcode());// ������������
		vouchermap.put("StYear", getString(bankBackDto.getSofyear()));// ҵ�����
		vouchermap.put("VtCode", getString(vDto.getSvtcode()));// ƾ֤���ͱ��
		vouchermap.put("VouDate", DateUtil.date2String2(bankBackDto
				.getDvoudate()));// ƾ֤����
		vouchermap.put("VoucherNo", getString(vDto.getSvoucherno()));// ƾ֤��
		vouchermap.put("TreCode", getString(bankBackDto.getStrecode()));// �����������
		vouchermap.put("FinOrgCode", getString(bankBackDto.getSfinorgcode()));// �������ش���
		vouchermap.put("BgtTypeCode", getString(bankBackDto.getSbgttypecode()));// Ԥ�����ͱ���
		vouchermap.put("BgtTypeName", getString(bankBackDto.getSbgttypename()));// Ԥ����������
		vouchermap.put("FundTypeCode",
				getString(bankBackDto.getSfundtypecode()));// �ʽ����ʱ���
		vouchermap.put("FundTypeName",
				getString(bankBackDto.getSfundtypename()));// �ʽ���������
		if ("1".equals(bankBackDto.getSpaytypecode())) {
			vouchermap.put("PayTypeCode", "12");// ֧����ʽ����
		} else if ("0".equals(bankBackDto.getSpaytypecode())) {
			vouchermap.put("PayTypeCode", "11");// ֧����ʽ����
		}
		// vouchermap.put("PayTypeCode",bankBackDto.getSpaytypecode());//֧����ʽ����
		vouchermap.put("PayTypeName", getString(bankBackDto.getSpaytypename()));// ֧����ʽ����
		vouchermap.put("AgentAcctNo", getString(bankBackDto.getSpayeeacct()));// ԭ�տ������˺�
		vouchermap.put("AgentAcctName", getString(bankBackDto.getSpayeename()));// ԭ�տ������˻�����
		vouchermap.put("AgentAcctBankName", getString(bankBackDto
				.getSclearacctbankname()));// ԭ�տ�����
		vouchermap.put("ClearAcctNo", getString(bankBackDto.getSpayeracct()));// ԭ�����˺�
		vouchermap.put("ClearAcctName", getString(bankBackDto.getSpayername()));// ԭ�����˻�����
		vouchermap.put("ClearAcctBankName", getString(bankBackDto
				.getSclearacctbankname()));// ԭ��������
		vouchermap.put("PayDictateNo",
				getString(bankBackDto.getSpaydictateno()));// ֧���������
		vouchermap.put("PayMsgNo", getString(bankBackDto.getSpaymsgno()));// ֧�����ı��
		vouchermap.put("PayEntrustDate", DateUtil.date2String2(bankBackDto
				.getDorientrustdate()));// ֧��ί������
		vouchermap.put("PayBankName", getString(bankBackDto.getSpaybankname()));// ������������
		vouchermap.put("PayBankNo", getString(bankBackDto.getSagentbnkcode()));// ���������к�
		vouchermap.put("Remark", "");// ժҪ
		vouchermap.put("MoneyCorpCode", "");// ���ڻ�������
		vouchermap.put("TKLX", StateConstant.TKLX_1); // �˿�����
		vouchermap
				.put("XPaySndBnkNo", getString(bankBackDto.getSpaysndbnkno()));// ֧���������к�
		vouchermap.put("XAddWord", "");// ����
		vouchermap.put("XClearDate", DateUtil.date2String2(bankBackDto
				.getSxcleardate()));// ��������
		vouchermap.put("Hold1", "");// Ԥ���ֶ�1
		vouchermap.put("Hold2", "");// Ԥ���ֶ�2
		HashMap<String, Object> DetailList = new HashMap<String, Object>();
		List detailList = new ArrayList();
		BigDecimal sumamt = new BigDecimal("0.00");
		HashMap<String, Object> Detailmap = new HashMap<String, Object>();
		Detailmap.put("Id", "01");// ���
		// ���ҷ���2203�ı��ı��
		TvVoucherinfoDto tmpDto = new TvVoucherinfoDto();
		tmpDto.setSorgcode(vDto.getSorgcode());
		tmpDto.setStrecode(vDto.getStrecode());
		tmpDto.setSadmdivcode(vDto.getSadmdivcode());
		tmpDto.setShold2(String.valueOf(bankBackDto.getIvousrlno()) + "01");
		List<TvVoucherinfoDto> lists;
		try {
			lists = CommonFacade.getODB().findRsByDto(tmpDto);
			if (null == lists || lists.size() == 0) {
				throw new ITFEBizException(
						"û����Ҫ���ɶ�Ӧ��ֱ��֧��ƾ֤��Ϣ�����֤�Ƿ�����ѷ��͵�ֱ��֧���˿�֪ͨ�飡");
			} else {
				tmpDto = lists.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(
					"û����Ҫ���ɶ�Ӧ��ֱ��֧��ƾ֤��Ϣ�����֤�Ƿ�����ѷ��͵�ֱ��֧���˿�֪ͨ�飡", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException(
					"û����Ҫ���ɶ�Ӧ��ֱ��֧��ƾ֤��Ϣ�����֤�Ƿ�����ѷ��͵�ֱ��֧���˿�֪ͨ�飡", e);
		}
		Detailmap.put("VoucherNo", getString(tmpDto.getSdealno()));
		Detailmap.put("SupDepCode", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSbdgorgcode());// һ��Ԥ�㵥λ����
		Detailmap.put("SupDepName", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSsupdepname());// һ��Ԥ�㵥λ����
		Detailmap.put("ExpFuncCode", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSfuncbdgsbtcode());// ֧�����ܷ����Ŀ����
		Detailmap.put("ExpFuncName", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSexpfuncname());// ֧�����ܷ����Ŀ����
		Detailmap.put("PaySummaryName", "");
		Detailmap.put("Hold1", getString(""));// Ԥ���ֶ�1
		Detailmap.put("Hold2", getString(""));// Ԥ���ֶ�2
		Detailmap.put("Hold3", getString(""));// Ԥ���ֶ�3
		Detailmap.put("Hold4", getString(""));// Ԥ���ֶ�4
		for (int i = 0; i < bankBackDetailList.size(); i++) {
			TvPayreckBankBackListDto detailDto = (TvPayreckBankBackListDto) bankBackDetailList
					.get(i);
			// DetailList.put("Detail", Detailmap);
			sumamt = sumamt.add(detailDto.getFamt());

		}
		Detailmap.put("PayAmt", sumamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00"
				: "-" + MtoCodeTrans.transformString(sumamt));// ֧�����
		detailList.add(Detailmap);
		vouchermap.put("PayAmt",
				sumamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : "-"
						+ MtoCodeTrans.transformString(sumamt));// ����������
		vouchermap.put("XPayAmt",
				sumamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : "-"
						+ MtoCodeTrans.transformString(sumamt));// ����������
		DetailList.put("Detail", detailList);
		vouchermap.put("DetailList", DetailList);
		return map;
	}

	private String getString(String key) {
		if (key == null)
			key = "";
		return key;
	}

	public Map<String, TsInfoconnorgaccDto> getAccMap() {
		return accMap;
	}

	public void setAccMap(Map<String, TsInfoconnorgaccDto> accMap) {
		this.accMap = accMap;
	}
}
