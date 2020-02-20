package com.cfcc.itfe.tbs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TfReconciliationDto;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;
import com.cfcc.itfe.persistence.dto.TfResultReconciDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsTbsorgstatusDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvExceptionmanDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * 
 */

@WebService(endpointInterface = "com.cfcc.itfe.tbs.ITbs", serviceName = "TbsImpl")
public class TbsImpl implements ITbs {
	private static Log logger = LogFactory.getLog(TbsImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.itfe.tbs.ITbs#sayHi(java.lang.String)
	 */
	public String sayHi(String text) {
		try {
			// System.out.println("say :" + text);
			logger.info("����������:" + text);
			// return VoucherUtil.base64Encode("say:" + text);
			return VoucherUtil.base64Encode(text + "null");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
			// throw new ITFEBizException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * ƾ֤ǩ��
	 * 
	 * @param admDivCode
	 *            ��������
	 * @param vtCode
	 *            ƾ֤���
	 * @param treCode
	 *            �������
	 * @param vouDate
	 *            ƾ֤����
	 * @param vouNo
	 *            ƾ֤���
	 * @return ���ز������
	 * @throws ITFEBizException
	 */
	public String confirmVoucher(String admDivCode, String vtCode,
			String treCode, String vouDate, String vouNo) {
		logger.info("����(confirmVoucher)����admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate
				+ "vouNo:" + vouNo);
		try {
			// У�������Ϣ
			String str = TBSVerifyUtils.verifyConfirmVoucherParam(admDivCode,
					vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			// ��ѯ�������Ӧ����Ϣ
			TvVoucherinfoDto tvVoucherinfoDto = TBSVerifyUtils
					.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,
							admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			// �ж�ƾ֤״̬�Ƿ�����
			str = TBSVerifyUtils.verifyInfoLocked(tvVoucherinfoDto);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			// S_EXT1Ϊ���Ǹն�ȡ��ƾ֤ ��Ϊ���Ѿ��������������ٴ�ǩ��
			if (!TbsDealCodeConstants.VOUCHER_ACCEPT.equals(tvVoucherinfoDto
					.getSext1())) {
				logger.error("�������ظ�ǩ��");
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�������ظ�ǩ��");
			}
			// 5106��5108��Ҫ��¼X�ֶ�
			updateMainDto(tvVoucherinfoDto);
			// tbs�ӿڶ�ȡƾ֤��״̬������S_EXT1�ֶ��� ����TBSУ��ɹ�
			tvVoucherinfoDto
					.setSext1(TbsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
			tvVoucherinfoDto.setSdemo("TBSǩ�ճɹ�");
			DatabaseFacade.getODB().update(tvVoucherinfoDto);
			return VoucherUtil
					.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
		} catch (ITFEBizException e) {
			logger.error("TbsImpl.confirmVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.confirmVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}

	}

	/**
	 * ����5106��5108 X�ֶ�
	 * 
	 * @param vDto
	 * @throws ITFEBizException
	 */
	private static void updateMainDto(TvVoucherinfoDto vDto)
			throws ITFEBizException {
		try {
			String status = DealCodeConstants.DEALCODE_ITFE_SUCCESS;
			String xpaydate = TimeFacade.getCurrentStringTime();
			if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
				TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
				mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
				TvGrantpaymsgsubDto subDto = new TvGrantpaymsgsubDto();
				subDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
				mainDto = (TvGrantpaymsgmainDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
				List<TvGrantpaymsgsubDto> subDtoList = CommonFacade.getODB().findRsByDto(subDto);
				if (subDtoList == null || subDtoList.size() == 0)
					throw new ITFEBizException("��ѯ�ӱ���Ϣ�쳣����ƾ֤��"+ vDto.getSvoucherno() + "��Ӧ���ӱ��¼�����ڣ�");
				mainDto.setSxacctdate(xpaydate);
				mainDto.setSstatus(status);
				DatabaseFacade.getODB().update(mainDto);
				for (TvGrantpaymsgsubDto subdto : subDtoList) {
					subdto.setSstatus(status);
					DatabaseFacade.getODB().update(subdto);
				}
			} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
				TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
				mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
				mainDto = (TvDirectpaymsgmainDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
				mainDto.setSxacctdate(xpaydate);
				mainDto.setSstatus(status);
				DatabaseFacade.getODB().update(mainDto);
			}
		} catch (java.lang.IndexOutOfBoundsException e) {
			logger.error("��ѯ������Ϣ�쳣����ƾ֤��" + vDto.getSvoucherno()+ "��Ӧ�������¼�����ڣ�   ");
			throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��" + vDto.getSvoucherno()+ "��Ӧ�������¼�����ڣ�", e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * ��ȡƾ֤
	 * 
	 * @param admDivCode��������
	 * @param vtCode
	 *            ƾ֤���
	 * @param treCode
	 *            �������
	 * @param vouDate
	 *            ƾ֤����
	 * @return ����ƾ֤���ݣ���Base64����
	 * @throws ITFEBizException
	 */
	public String readVoucher(String admDivCode, String vtCode, String treCode,
			String vouDate) {
		logger.info("TBS���ýӿ�(readVoucher)����admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate);
		SQLExecutor sqlExecutor = null;
		try {
			// У�������Ϣ
			String str = TBSVerifyUtils.verifyReadVoucherParam(admDivCode,
					vtCode, treCode, vouDate);
			if (StringUtils.isNotBlank(str)) {
				throw new ITFEBizException(TbsDealCodeConstants.OPERATION_FAIL
						+ VoucherUtil.base64Encode(str));
			}
			List params = new ArrayList();
			// params.add(admDivCode);
			params.add(vtCode);
			params.add(treCode);
			params.add(vouDate);
			// ����ȡ״̬Ϊ���Ѷ�ȡ��ǩ�ճɹ���ǩ��ʧ�ܡ�
			// ��ѯ S_EXT1Ϊ��(��ƾ֤���ȡ����ʱ�Ѿ���ֵΪ���ַ�) ƾ֤״̬Ϊ У��ɹ�������(5207Ϊ��˳ɹ�--��¼�տ��˿������к�)
			String whereSql=" WHERE  length(S_EXT1) != 2  AND S_VTCODE = ? AND S_TRECODE = ? AND S_CREATDATE <= ? AND (S_STATUS = '20' OR S_STATUS = '50') AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			if(vtCode.equals(MsgConstant.VOUCHER_NO_5207) || vtCode.equals(MsgConstant.VOUCHER_NO_5209)){ //δ��¼�����кŵĲ���ȡ
//				whereSql = " WHERE  length(S_EXT1) != 2  AND S_VTCODE = ? AND S_TRECODE = ? AND S_CREATDATE = ? " +
//						"AND S_DEALNO IN (SELECT S_BIZNO FROM TV_PAYOUTMSGMAIN WHERE S_TRECODE = ? " +
//						"AND S_COMMITDATE = ? AND (TRIM(S_PAYEEBANKNO) != '' AND S_PAYEEBANKNO IS NOT NULL))" +
//						" AND S_STATUS = '50' AND S_PAYBANKCODE IS NOT NULL ";
//				params.add(treCode);
//				params.add(vouDate);
				
				whereSql = " WHERE  length(S_EXT1) != 2  AND S_VTCODE = ? AND S_TRECODE = ? AND S_CREATDATE <= ? AND S_STATUS = '50' AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			}
			List<TvVoucherinfoDto> result = DatabaseFacade.getODB().find(TvVoucherinfoDto.class, whereSql, params);
			if (null == result || result.size() == 0) {
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL);
			}
			List bizDatas = TBSVerifyUtils.getBizDatas(result);
			if (null == bizDatas || bizDatas.size() == 0) {
				logger.error("δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
			}
			// tbs��ȡ״̬������S_EXT1��ͬʱ����status�ֶ�Ϊ"������"
			List<IDto> infoList = (List<IDto>) bizDatas.get(0);
			for (IDto tmpDto : infoList) {
				TvVoucherinfoDto voucherDto = (TvVoucherinfoDto)tmpDto;
				voucherDto.setSext1(TbsDealCodeConstants.VOUCHER_ACCEPT);
				voucherDto.setSstatus(DealCodeConstants.VOUCHER_RECIPE);
				voucherDto.setSdemo("������");
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(infoList));
			logger.info("�����" + (String) bizDatas.get(1));
			return VoucherUtil.base64Encode((String) bizDatas.get(1));
		} catch (Exception e) {
			logger.error("TbsImpl.readVoucher[" + e.getMessage() + "]", e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} finally {
			if (null != sqlExecutor) {
				sqlExecutor.closeConnection();
			}
		}

	}

	/**
	 * ƾ֤�˻�
	 * 
	 * @param admDivCode
	 *            ��������
	 * @param vtCode
	 *            ƾ֤���
	 * @param treCode
	 *            �������
	 * @param vouDate
	 *            ��������
	 * @param vouNo
	 *            ƾ֤���
	 * @param errMsg
	 *            ʧ��ԭ��
	 * @return ���ز������ ����(0:�ɹ���1:ʧ��)
	 * @throws ITFEBizException
	 */
	public String returnVoucherBack(String admDivCode, String vtCode,
			String treCode, String vouDate, String vouNo, String errMsg) {
		logger.info("TBS���ýӿ�(returnVoucherBack)����admDivCode:" + admDivCode
				+ "vtCode:" + vtCode + "treCode:" + treCode + "vouDate:"
				+ vouDate + "vouNo:" + vouNo);
		try {
			// У�������Ϣ
			String str = TBSVerifyUtils.verifyReturnVoucherBackParam(admDivCode, vtCode, treCode, vouDate, vouNo, errMsg);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			// ��ѯ�������Ӧ����Ϣ
			TvVoucherinfoDto tvVoucherinfoDto = TBSVerifyUtils.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error(VoucherUtil.base64Encode("δ�ҵ���Ӧ��ƾ֤��Ϣ"));
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			// �ж�ƾ֤״̬�Ƿ�����
			/*str = TBSVerifyUtils.verifyInfoLocked(tvVoucherinfoDto);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}*/
			// �ȴ������������ʽ���ƾ֤ �������˻�
			/*if (!(TbsDealCodeConstants.VOUCHER_ACCEPT.equals(tvVoucherinfoDto.getSext1().trim())
					|| TbsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(tvVoucherinfoDto.getSext1().trim())
					|| TbsDealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(tvVoucherinfoDto.getSext1().trim()) 
					|| TbsDealCodeConstants.VOUCHER_LIQUIDATION_FAILE.equals(tvVoucherinfoDto.getSext1().trim()))) {
				logger.error("״̬Ϊ�����С�����ɹ����ص��ɹ����˻سɹ����������");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "״̬Ϊ�����С�����ɹ����ص��ɹ����˻سɹ����������");
			}*/
			if(TbsDealCodeConstants.VOUCHER_LIQUIDATION_ING.equals(tvVoucherinfoDto.getSext1().trim()) 
					|| TbsDealCodeConstants.VOUCHER_LIQUIDATION_SUCCEED.equals(tvVoucherinfoDto.getSext1().trim())
					|| TbsDealCodeConstants.VOUCHER_SUCCESS.equals(tvVoucherinfoDto.getSext1().trim())
					|| TbsDealCodeConstants.VOUCHER_FAIL.equals(tvVoucherinfoDto.getSext1().trim())){
				logger.error("״̬Ϊ�����С�����ɹ����ص��ɹ����˻سɹ��������˻ز���");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "״̬Ϊ�����С�����ɹ����ص��ɹ����˻سɹ��������˻ز���");
			}
			//�������⴦���20190903
			if (treCode.startsWith("40")) { //���ŵ���Ʊ��Ҫ���³ɴ���ʧ�ܣ�Ȼ��ǩ��Ʊ�£���ǰ���Զ��˻�
				TBSVerifyUtils.updateVoucherInfoStatus(tvVoucherinfoDto, DealCodeConstants.VOUCHER_FAIL_TCBS, "tbs����ʧ��");
				TBSVerifyUtils.updateBizInfoStatus(tvVoucherinfoDto, "80001"); //����ҵ���Ϊ����ʧ��
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
			}else{ //�����Զ���Ϊ��Ʊ�ɹ�
				
			// ����ƾ֤��ִ�� ƾ֤�˻�
			VoucherService voucherService = new VoucherService();
			String err = voucherService.returnVoucherBack(null, tvVoucherinfoDto.getSadmdivcode(),
					Integer.parseInt(tvVoucherinfoDto.getSstyear()), vtCode,
					new String[] { vouNo },
					new String[] { errMsg.length() > 50 ? errMsg.substring(0, 50) : errMsg });
			if (StringUtils.isNotBlank(err)) {
				// ����ƾ֤��ʧ�ܵģ���¼״̬��ԭ�� �����������
				TBSVerifyUtils.updateVoucherInfoStatus(tvVoucherinfoDto, TbsDealCodeConstants.VOUCHER_APP_FAIL, errMsg);
				logger.error("[�������:" + treCode + "ƾ֤���:" + vouNo + "ƾ֤����:" + vouDate + "]����ƾ֤��returnVoucherʧ��");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + err);
			}
			TBSVerifyUtils.updateVoucherInfoStatus(tvVoucherinfoDto, TbsDealCodeConstants.VOUCHER_FAIL, "�˻سɹ�");
			TBSVerifyUtils.updateBizInfoStatus(tvVoucherinfoDto, "80001"); //����ҵ���Ϊ����ʧ��
			return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
			}
		} catch (ITFEBizException e) {
			logger.error("TbsImpl.returnVoucherBack[" + e.getMessage() + "]",
							e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.returnVoucherBack[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
	}

	/**
	 * ����ƾ֤
	 * 
	 * @param admDivCode
	 *            ��������
	 * @param vtCode
	 *            ƾ֤���
	 * @param treCode
	 *            �������
	 * @param vouDate
	 *            ƾ֤����
	 * @param vouNo
	 *            ƾ֤���
	 * @return ���ز������ ����(0:�ɹ���1:ʧ��)
	 * @throws ITFEBizException
	 */
	public String sendVoucher(String admDivCode, String vtCode, String treCode,
			String vouDate, String vouNo) {
		logger.info("TBS���ýӿ�(sendVoucher)����admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate
				+ "vouNo:" + vouNo);
		// У�������Ϣ
		try {
			String str = TBSVerifyUtils.verifySendVoucherParam(admDivCode,
					vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			// ��ѯ�������Ӧ����Ϣ
			TvVoucherinfoDto tvVoucherinfoDto = TBSVerifyUtils
					.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,
							admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			/**
			 * ���״̬��80(�ѻص�)����ôֱ�ӷ��ز����ɹ�
			 * ����������Ĵ���ƾ֤���Ѿ��յ��ص��������ص��ǲ���ʧ�ܣ���������������״̬Ҳ���³�ʧ�ܣ����ظ��������̵Ľ��
			 * Ҳ�Ǵ���ʧ�ܣ��������Ľ��̲���Բ���ʧ�ܵ�ƾ֤״̬���и���(Ϊ�˻����Զ����ͻص�)��ǰ�ú�ƾ֤�⾭����ͨ���ֹ�
			 * �����ݿ��޸��˶�Ӧƾ֤��״̬������ֱ�Ӹ��������̷��سɹ����Ա�������̸��´������״̬
			 */
//			if(tvVoucherinfoDto.getSstatus().equals(TbsDealCodeConstants.VOUCHER_SUCCESS)){
//				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
//			}
			if(!tvVoucherinfoDto.getSstatus().equals(TbsDealCodeConstants.VOUCHER_READ_SUCESS)){ //ֻ�Զ��ص�һ�Σ��ڶ���Ĭ�ϳɹ�
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
			}
			// �ж�ƾ֤״̬�Ƿ�����
//			str = TBSVerifyUtils.verifyInfoLocked(tvVoucherinfoDto);
//			if (StringUtils.isNotBlank(str)) {
//				logger.error(str);
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
//			}

			if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0) { // ����ƾ֤������ģʽ
				if (!(TbsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS
						.equals(tvVoucherinfoDto.getSext1().trim()))) {
					logger.error("ֻ�ܷ���У��ɹ���ƾ֤");
					return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "ֻ�ܷ���У��ɹ���ƾ֤");
				}
			} else if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode02,") >= 0) { // ��������ģʽ
//				if (!(TbsDealCodeConstants.VOUCHER_LIQUIDATION_SUCCEED.equals(tvVoucherinfoDto.getSext1().trim()))) {
//					logger.error("ֻ�ܷ�������ɹ���ƾ֤");
//					return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "ֻ�ܷ�������ɹ���ƾ֤");
//				}
				if((tvVoucherinfoDto.getSvtcode().equals("2301") || tvVoucherinfoDto.getSvtcode().equals("5207") 
						|| tvVoucherinfoDto.getSvtcode().equals("5209")) 
						&& !TbsDealCodeConstants.VOUCHER_LIQUIDATION_SUCCEED.equals(tvVoucherinfoDto.getSext1().trim())){
					logger.error("ֻ�ܷ�������ɹ���ƾ֤");
					return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "ֻ�ܷ�������ɹ���ƾ֤");
				}
			} else {
				logger.error("TBS�ʽ�����ģʽ��������");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "TBS�ʽ�����ģʽ��������");
			}

			// �Ƿ�ϵͳ�Զ�����ƾ֤��Ϣ
			boolean isSysGenVoucherFlag = Boolean.FALSE;
			if (MsgConstant.VOUCHER_NO_3208.equals(tvVoucherinfoDto.getSvtcode())) {
				isSysGenVoucherFlag = true;
			}
			// ǩ����ǩ�º�ת������SYSConfig.xml����
			if (ITFECommonConstant.TBS_011.containsKey(tvVoucherinfoDto.getSvtcode())) {
				TBSVerifyUtils.sendVoucherUtil(ITFECommonConstant.TBS_011,
						tvVoucherinfoDto, StateConstant.ORGCODE_FIN,
						isSysGenVoucherFlag);
			}
			if (ITFECommonConstant.TBS_111.containsKey(tvVoucherinfoDto.getSvtcode())) {
				if(null == ITFECommonConstant.TBS_TREANDBANK || ITFECommonConstant.TBS_TREANDBANK.size() == 0){
					throw new ITFEBizException("���ݹ�������ȡ��������Ϣʧ�ܣ�");
				}
				TBSVerifyUtils.sendVoucherUtil(ITFECommonConstant.TBS_111,
						tvVoucherinfoDto, ITFECommonConstant.TBS_TREANDBANK.get(tvVoucherinfoDto.getStrecode()),
						isSysGenVoucherFlag);
			}
			return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error("TbsImpl.sendVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.sendVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
	}

	/**
	 * ƾ֤״̬ͬ��
	 * 
	 * @param admDivCode
	 *            ��������
	 * @param vtCode
	 *            ƾ֤���
	 * @param treCode
	 *            �������
	 * @param vouDate
	 *            ƾ֤����
	 * @param vouNo
	 *            ƾ֤���
	 * @return ƾ֤״̬
	 * @throws ITFEBizException
	 */
	public String synchronousVoucherStatus(String admDivCode, String vtCode,
			String treCode, String vouDate, String vouNo) {
		logger.info("TBS���ýӿ�(synchronousVoucherStatus)����admDivCode:" + admDivCode
				+ "vtCode:" + vtCode + "treCode:" + treCode + "vouDate:"
				+ vouDate + "vouNo:" + vouNo);
		try {
			// У�������Ϣ
			String str = TBSVerifyUtils.verifySynchronousVoucherStatusParam(
					admDivCode, vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			TvVoucherinfoDto tvVoucherinfoDto = TBSVerifyUtils
					.getTvVoucherinfoDto(treCode, (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0 && vtCode.equals(MsgConstant.VOUCHER_NO_5209))? MsgConstant.VOUCHER_NO_3209:vtCode, vouDate, vouNo,
							admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			if (StringUtils.isNotBlank(tvVoucherinfoDto.getSext3())) {
				tvVoucherinfoDto.setSext3(null);
				DatabaseFacade.getODB().update(tvVoucherinfoDto);
			}
			// ����TBS��ȡ��ƾ֤״̬
			return VoucherUtil.base64Encode(tvVoucherinfoDto.getSext1().trim());
		} catch (ITFEBizException e) {
			logger.error("TbsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * ���¶�ȡƾ֤
	 * 
	 * @param admDivCode��������
	 * @param vtCode
	 *            ƾ֤���
	 * @param treCode
	 *            �������
	 * @param vouDate
	 *            ƾ֤����
	 * @return ����ƾ֤���ݣ���Base64����
	 * @throws ITFEBizException
	 */
	public String readVoucherAgain(String admDivCode, String vtCode,
			String treCode, String vouDate) {
		logger.info("TBS���ýӿ�(readVoucherAgain)����admDivCode:" + admDivCode
				+ "vtCode:" + vtCode + "treCode:" + treCode + "vouDate:"
				+ vouDate + "vouNo:");
		SQLExecutor sqlExecutor = null;
		try {
			// У�������Ϣ
			String str = TBSVerifyUtils.verifyReadVoucherParam(admDivCode,
					vtCode, treCode, vouDate);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			List params = new ArrayList();
			// params.add(admDivCode);
			params.add(vtCode);
			params.add(treCode);
			params.add(vouDate);
			// ����ȡƾ֤״̬S_STATUSΪ �Ѷ�ȡ ǩ�ճɹ� ǩ��ʧ������
			List<TvVoucherinfoDto> result = DatabaseFacade
					.getODB()
					.find(
							TvVoucherinfoDto.class,
							" WHERE length(S_EXT1) = 2  AND S_VTCODE = ? AND S_TRECODE = ? AND S_CREATDATE = ? AND (S_STATUS != '10' OR S_STATUS != '15' OR S_STATUS != '16') AND S_PAYBANKCODE IS NOT NULL ",
							params);
			if (null == result || result.size() == 0) {
				logger.error("δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
			}
			List bizDatas = TBSVerifyUtils.getBizDatas(result);
			if (null == bizDatas || bizDatas.size() == 0) {
				logger.error("δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
			}
			// �����������¼Ϊ ����״̬
			List<IDto> infoList = (List<IDto>) bizDatas.get(0);
			for (IDto tmpDto : infoList) {
				((TvVoucherinfoDto) tmpDto)
						.setSext3(TbsDealCodeConstants.VOUCHER_LOCKING);
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(infoList));
			return VoucherUtil.base64Encode((String) bizDatas.get(1));
		} catch (JAFDatabaseException e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} finally {
			if (null != sqlExecutor) {
				sqlExecutor.closeConnection();
			}
		}
	}

	/**
	 * �ʽ�����ӿ�
	 * 
	 * @param vtCode
	 *            ƾ֤���
	 * @param treCode
	 *            �������
	 * @param vouDate
	 *            ƾ֤����
	 * @param vouNo
	 *            ƾ֤���
	 * @throws ITFEBizException
	 */
	public String fundLiquidation(String admDivCode, String vtCode,
			String treCode, String vouDate, String vouNo) {
		logger.info("TBS���ýӿ�(fundLiquidation)����admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate
				+ "vouNo:" + vouNo);
		SQLExecutor sqlExecutor = null;
		try {
			if (MsgConstant.VOUCHER_NO_5106.equals(vtCode) || MsgConstant.VOUCHER_NO_5108.equals(vtCode)) {
				logger.error("��������Ϣ����������ʽ��������");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "��������Ϣ����������ʽ��������");
			}
			// �������ò���
			if (!(ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode02,") >= 0)) {
				logger.error("������ʽ�����ģʽ��������");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "������ʽ�����ģʽ��������");
			}
			// У�������Ϣ
			String str = TBSVerifyUtils.verifyConfirmVoucherParam(admDivCode, vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			// ��ѯ��������Ϣ
			TvVoucherinfoDto vDto = TBSVerifyUtils.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo, admDivCode);
			if (null == vDto) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			// У��ڵ�״̬
//			str = TBSVerifyUtils.verifyNodeStatus(vDto.getSpaybankcode());
//			if (StringUtils.isNotBlank(str)) {
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
//			}
//			if (!TbsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(vDto
//					.getSext1())) {
//				logger.error("ֻ��״̬ΪУ��ɹ��ſ�������");
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
//								+ "ֻ��״̬ΪУ��ɹ��ſ�������");
//			}
			if("52095230".contains(vDto.getSvtcode())&&ITFECommonConstant.PUBLICPARAM.contains(",dlkysw=no,"))
			{
				try
				{
					TvDwbkDto sdto = new TvDwbkDto();
					sdto.setSagencyname(vDto.getSorgcode());
					sdto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					sdto = (TvDwbkDto)DatabaseFacade.getODB().find(sdto);
					HashMap<String, TsBudgetsubjectDto> subMap = SrvCacheFacade.cacheTsBdgsbtInfo(vDto.getSorgcode());
//					//��ȡȫ������¼��Ŀ�Ŀ����
//					TsBudgetsubjectDto tdto = new TsBudgetsubjectDto();
//					tdto.setSorgcode(vDto.getSorgcode());
//					tdto.setSwriteflag(StateConstant.COMMON_NO);
//					CommonQto qto = SqlUtil.IDto2CommonQto(tdto);
//					List <TsBudgetsubjectDto> tlist =  DatabaseFacade.getODB().findWithUR(tdto.getClass(), qto);
//					for (TsBudgetsubjectDto t :tlist) {
//						subMap.put(t.getSsubjectcode(), t);
//					}
					if(subMap.get(sdto.getSbdgsbtcode())!=null)
					{
						TsBudgetsubjectDto bdgdto = subMap.get(sdto.getSbdgsbtcode());
						if(bdgdto.getSbudgettype()!=null&&bdgdto.getSbudgettype().equals("2"))
						{
							return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "�������㱨��ʧ��");
						}
					}
				}catch(Exception e)
				{
					logger.error(e);
				}
			}
			MuleMessage message = new DefaultMuleMessage("");
			MuleClient client = new MuleClient();
			message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY, "TBS_1000");
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			// message.setProperty("orgCode", vDto.getSorgcode());
			message = client.send("vm://ManagerMsgWithCommBank", message);
			vDto = TBSVerifyUtils.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo, admDivCode);
			String sql = "SELECT count(*) FROM TF_FUND_APPROPRIATION WHERE S_PACKNO = ? AND S_TRECODE = ? AND S_VOUNO = ? AND S_VOUDATE = ?";
			sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(vDto.getSpackno());
			sqlExecutor.addParam(vDto.getStrecode());
			sqlExecutor.addParam(vDto.getSvoucherno());
			sqlExecutor.addParam(vDto.getSext4());
			SQLResults sqlResults = sqlExecutor.runQuery(sql);
			if (sqlResults.getInt(0, 0) == 0) {
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "�������㱨��ʧ��");
			} else {
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
			}
		} catch (Exception e) {
			logger.error(e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		} finally {
			if (null != sqlExecutor) {
				sqlExecutor.closeConnection();
			}
		}
	}
	
	/**
	 * ����������
	 * 
	 * @throws ITFEBizException
	 */
	public void testConnect(String desOrgCode, String vouDate, String orgCode) {
		// TODO Auto-generated method stub
		try {
			logger.info("TBS���ýӿ�(testConnect)����desOrgCode:" + desOrgCode
					+ "vouDate:" + vouDate + "orgCode:" + orgCode);
			if (StringUtils.isBlank(desOrgCode) || StringUtils.isBlank(vouDate)
					|| StringUtils.isBlank(orgCode)) {
				throw new ITFEBizException(VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "���ջ������������ڲ���Ϊ��"));
			}
			HeadDto headdto = new HeadDto();
			headdto.set_VER(MsgConstant.MSG_HEAD_VER);
			headdto.set_SRC("111111111111");
			headdto.set_DES(desOrgCode);
			headdto.set_APP("TCQS");
			headdto.set_msgNo(MsgConstant.MSG_TBS_NO_3000);
			headdto.set_workDate(vouDate);
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSpaybankcode(desOrgCode);
			// ���ͱ���
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_TBS_NO_3000 + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, orgCode);
			message.setPayload(map);
			message = client.send("vm://ManagerMsgWithCommBank", message);
		} catch (Exception e) {
			logger.error(e);
			// return
			// TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL +
			// e.getMessage());
		}
	}

	public String userLoginOrOut(String orgCode, String password, String operate) {
		try {
			logger.info("����(userLoginOrOut)����orgCode:" + orgCode + "password:"
					+ password + "operate:" + operate);
			if (StringUtils.isBlank(orgCode)) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�������벻��Ϊ��");
			}
			Pattern p = Pattern.compile("[0-9]{12}");
			Matcher m = p.matcher(orgCode);
			if (!m.matches()) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�����������Ϊ12Ϊ����");
			}
			if (StringUtils.isBlank(password)) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "���벻��Ϊ��");
			} else if (!("00000000".equals(password))) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�������");
			}
			if (StringUtils.isBlank(operate)) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�������Ͳ���Ϊ��");
			} else if (operate.length() != 1) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�������ͳ���Ϊ1Ϊ��0��½1�˳�");
			} else if (!("0".equals(operate) || "1".equals(operate))) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�������ͳ���Ϊ1Ϊ��0��½1�˳�");
			}
			TsTbsorgstatusDto tsTbsorgstatusDto = new TsTbsorgstatusDto();
			tsTbsorgstatusDto.setSorgcode(orgCode);
			tsTbsorgstatusDto.setSpassword(password);
			List resultList = CommonFacade.getODB().findRsByDto(
					tsTbsorgstatusDto);
			if (null == resultList || resultList.size() == 0) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�û���δ�ҵ�");
			}
			tsTbsorgstatusDto = (TsTbsorgstatusDto) resultList.get(0);
			tsTbsorgstatusDto.setSstatus(operate);
			DatabaseFacade.getODB().update(tsTbsorgstatusDto);
			return VoucherUtil
					.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * 
	 * @param voucherType
	 *            �������ͱ��
	 * @param fileContent
	 *            �ļ�����
	 * @return
	 * @throws ITFEBizException
	 */
	public String genVoucherTKXml(String voucherType, String fileContent) {
		// TODO Auto-generated method stub
		logger.info("TBS���ýӿ�(genVoucherXml)����voucherType:" + voucherType
				+ "fileContent:" + fileContent);
		try {
			String dirsep = File.separator;
			if (StringUtils.isBlank(voucherType)
					|| StringUtils.isBlank(fileContent)) {
				return VoucherUtil
						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
								+ "�������ͱ�Ż����ݲ���Ϊ��");
			}
//			String FileName = ITFECommonConstant.FILE_ROOT_PATH
//					+ dirsep
//					+ "Voucher"
//					+ dirsep
//					+ TimeFacade.getCurrentStringTime()
//					+ dirsep
//					+ "rev"
//					+ voucherType
//					+ "_"
//					+ new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System
//							.currentTimeMillis()) + ".msg";
//			FileUtil.getInstance().writeFile(FileName, fileContent);
//			if (!(voucherType.equals(MsgConstant.VOUCHER_NO_3208) || MsgConstant.VOUCHER_NO_5209
//					.equals(voucherType))) {
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
//								+ "�������ͱ�Ŵ���");
//			}
			// �����ӿ� ��ȡ�ܼ�¼��
			// int count = Integer.valueOf(fileContent.substring(2, fileContent
			// .indexOf("**")));
			// ��ȡ��������ϸ��Ϣ
			// String[] fileContents = fileContent.substring(
			// fileContent.indexOf("**")).split("\\*\\*");
			String[] fileContents = new String[1];
			fileContents[0] = fileContent;
			// if (count != (fileContents.length - 1)) {
			// return VoucherUtil
			// .base64Encode(TbsDealCodeConstants.OPERATION_FAIL
			// + "��¼����ƥ������");
			// }
			return TBSVerifyUtils.jxVoucherByType(voucherType, fileContents);
			// if (StringUtils.isNotBlank(result)) {
			// return VoucherUtil
			// .base64Encode(TbsDealCodeConstants.OPERATION_FAIL
			// + result);
			// }
			//
			// return VoucherUtil
			// .base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils
					.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * tbs�Ĳ���ͬ����ITFE(��Ŀ���룬���˴���)
	 * @param paraType �����ǿ�Ŀ���뻹�Ƿ��˴��� 0--��Ŀ����   1--���˴���
	 * @param paramContent ͬ��������
	 * @return �Ƿ�ɹ� 0:�����ɹ� -1:����ʧ��
	 */
	public String synchronousParam(String orgCode, String paraType, String paramContent) {
		logger.info("TBS���ýӿ�(synchronousParam)����orgCode:" + orgCode + "paraType:" + paraType
				+ "paramContent:" + paramContent);
		if(paraType == null || paraType.trim().equals("")){
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"-��������paraType����Ϊ��");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"��������paramContent����Ϊ��");
		}
		String[] datas = paramContent.substring(paramContent.indexOf("**") + 2).split("\\*\\*");
		TsBudgetsubjectDto bsDto = null;
		TdCorpDto  corpDto = null;
		TnConpaycheckbillDto concheckdto =null;
		Date startdate =null;
		Date endDate = null;
		List paramList = new ArrayList();
		StringBuffer sberror = new StringBuffer();
		if(paraType.equals("0")){ // 0--��Ŀ����
			for(String str:datas){
				bsDto = new TsBudgetsubjectDto();
				String[] columns = str.split(",");
				bsDto.setSorgcode(columns[0]);
				bsDto.setSsubjectcode(columns[1]);
				bsDto.setSsubjectname(columns[2]);
				bsDto.setSsubjectclass(columns[3]);
				bsDto.setSsubjecttype(columns[4]);
				bsDto.setSsubjectattr(convertNull(columns[5]));
				bsDto.setSinoutflag(columns[6]);
				bsDto.setSwriteflag(convertNull(columns[7]));
				bsDto.setSmoveflag(convertNull(columns[8]));
				bsDto.setSbudgettype(columns[9]);
				bsDto.setSclassflag(columns[10]);
				bsDto.setSdrawbacktype(columns[11]);
				paramList.add(bsDto);
			}
		}else if(paraType.equals("1")){ // 1--���˴���
			Calendar cal = Calendar.getInstance();
			for(String str:datas){
				corpDto = new TdCorpDto();
				String[] columns = str.split(",");
				corpDto.setSbookorgcode(columns[0]);
				corpDto.setStrecode(columns[1]);
				corpDto.setScorpcode(columns[2]);
				corpDto.setCtrimflag(columns[3]);
				corpDto.setScorpname(columns[4]);
				corpDto.setScorpsht(columns[5]);
				corpDto.setCmayaprtfund(columns[6]);
				corpDto.setCpayoutprop("N");
				corpDto.setCtaxpayerprop(columns[7]);
				corpDto.setCtradeprop("N");
				corpDto.setTssysupdate(new Timestamp(cal.getTimeInMillis()));
				paramList.add(corpDto);
			}
		}else if (paraType.equals("5555")){
			Calendar cal = Calendar.getInstance();
			for(String str:datas){
				concheckdto = new TnConpaycheckbillDto();
				String[] columns = str.split(",");
				concheckdto.setSbookorgcode(orgCode);
				concheckdto.setStrecode(columns[0]);
				concheckdto.setSbdgorgcode(columns[1]);
				concheckdto.setSbnkno(columns[2]);
				concheckdto.setSfuncsbtcode(columns[3]);
				concheckdto.setSecosbtcode(columns[4]);
				concheckdto.setCamtkind(columns[13]);
				concheckdto.setFlastmonthzeroamt(new BigDecimal(columns[5]));
				concheckdto.setFcurzeroamt(new BigDecimal(columns[6]));
				concheckdto.setFcurreckzeroamt(new BigDecimal(columns[7]));
				concheckdto.setFlastmonthsmallamt(new BigDecimal(columns[9]));
				concheckdto.setFcursmallamt(new BigDecimal(columns[10]));
				concheckdto.setFcurrecksmallamt(new BigDecimal(columns[11]));
				startdate = java.sql.Date.valueOf(columns[14].substring(0,4)+"-"+columns[14].substring(4,6)+"-"+columns[14].substring(6));
				endDate = java.sql.Date.valueOf(columns[15].substring(0,4)+"-"+columns[15].substring(4,6)+"-"+columns[15].substring(6));
				concheckdto.setDstartdate(startdate);
				concheckdto.setDenddate(endDate);
				concheckdto.setTssysupdate(new Timestamp(cal.getTimeInMillis()));
				paramList.add(concheckdto);
			}
		}else
		{
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"�޷�ͬ��δ֪�Ĳ�����");
		}
		String sql = "";
		if(paraType.equals("0")){ // 0--��Ŀ����
			sql = "DELETE FROM TS_BUDGETSUBJECT WHERE S_ORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql);
				DatabaseFacade.getODB().create((TsBudgetsubjectDto[])paramList.toArray(new TsBudgetsubjectDto[paramList.size()]));
				SrvCacheFacade.reloadBuffer(orgCode,StateConstant.CacheBdgSbt);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		if(paraType.equals("1")){ // 1--���˴���
			sql = "DELETE FROM TD_CORP WHERE S_BOOKORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql);
				DatabaseFacade.getODB().create((TdCorpDto[])paramList.toArray(new TdCorpDto[paramList.size()]));
				SrvCacheFacade.reloadBuffer(orgCode,StateConstant.CacheTDCrop);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		if(paraType.equals("5555")){ //5555--����֧����ȶ��˵�
			sql = "DELETE FROM TN_CONPAYCHECKBILL WHERE S_BOOKORGCODE = ? and D_ENDDATE >=?  AND D_STARTDATE <= ? ";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.addParam(orgCode);
				sqlEx.addParam(startdate);
				sqlEx.addParam(endDate);
				sqlEx.runQueryCloseCon(sql);
				DatabaseFacade.getODB().create((TnConpaycheckbillDto[])paramList.toArray(new TnConpaycheckbillDto[paramList.size()]));
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		
		return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
	}
	
	/**
	 * tbsΪNULL���ֶΣ�jdbc��ȡ������Ϊ�ַ���null,ת��Ϊ���ַ���
	 * @param oralStr
	 * @return
	 */
	public String convertNull(String oralStr){
		if(oralStr.equals("null")){
			return "";
		}
		return oralStr;
	}

	/**
	 * tbs�ı�������ͬ����ITFE(���룬֧��)
	 * @param orgCode �����������
	 * @param paraType ����һ��ռλ��
	 * @param paramContent ͬ��������
	 * @return �Ƿ�ɹ� 0:�����ɹ� -1:����ʧ��
	 */
	public String synchronousIncomeDayRpt(String orgCode, String paraType,
			String paramContent) {
		logger.info("TBS���ýӿ�(synchronousIncomeDayRpt)orgCode:" + orgCode + "paraType:" + paraType
				+ "paramContent:" + paramContent);
		if(paraType == null || paraType.trim().equals("")){
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"-������������paraType����Ϊ��");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"��������paramContent����Ϊ��");
		}
		String[] strArry = paramContent.split("@");
		String incomecontent = strArry[0];
		String payoutcontent = strArry[1];
		String[] datas1 = incomecontent.substring(incomecontent.indexOf("**") + 2).split("\\*\\*");
		String[] datas2 = payoutcontent.substring(payoutcontent.indexOf("**") + 2).split("\\*\\*");
		TrIncomedayrptDto bsDto = null;//�����dto
		TrTaxorgPayoutReportDto  corpDto = null;//֧����dto
		List paramList1 = new ArrayList();
		List paramList2 = new ArrayList();
		StringBuffer sberror = new StringBuffer();
		if(paraType.equals("0")){
			// ����
			for(String str:datas1){
				bsDto = new TrIncomedayrptDto();
				String[] columns = str.split(",");
				bsDto.setSfinorgcode(orgCode);//�������ش���
				bsDto.setStaxorgcode(columns[3]);//���ջ��ش���
				bsDto.setStrecode(columns[2]);//�������
				bsDto.setSrptdate(columns[1]);//srptdate
				bsDto.setSbudgettype(columns[4]);//Ԥ������
				bsDto.setSbudgetlevelcode(columns[5]);//Ԥ�㼶�δ��� 
				bsDto.setSbudgetsubcode(columns[6]);//Ԥ���Ŀ
				bsDto.setSbudgetsubname(columns[7]);//Ԥ���Ŀ����
				bsDto.setNmoneyday(new BigDecimal(columns[8]));//���ۼƽ��
				bsDto.setNmoneytenday(new BigDecimal(columns[9]));//Ѯ�ۼƽ��
				bsDto.setNmoneymonth(new BigDecimal(columns[10]));//���ۼƽ��
				bsDto.setNmoneyquarter(new BigDecimal(columns[11]));//���ۼƽ��
				bsDto.setNmoneyyear(new BigDecimal(columns[12]));//���ۼƽ��
				bsDto.setSbelongflag(columns[13]);//Ͻ����־
				bsDto.setStrimflag(columns[14]);//�����ڱ�־
				bsDto.setSdividegroup(columns[15]);//�ֳ����־ 
				bsDto.setSbillkind(columns[16]);//�������� 
				paramList1.add(bsDto);
			}
			// ֧��
			Calendar cal = Calendar.getInstance();
			for(String str:datas2){
				corpDto = new TrTaxorgPayoutReportDto();
				String[] columns = str.split(",");
				corpDto.setSfinorgcode(orgCode);//�������ش���
				corpDto.setStaxorgcode(columns[3]);//���ջ��ش���
				corpDto.setStrecode(columns[2]);//�������
				corpDto.setSrptdate(columns[1]);//srptdate
				corpDto.setSbudgettype(columns[4]);//Ԥ������
				corpDto.setSbudgetlevelcode(columns[5]);//Ԥ�㼶�δ��� 
				corpDto.setSbudgetsubcode(columns[6]);//Ԥ���Ŀ
				corpDto.setSbudgetsubname(columns[7]);//Ԥ���Ŀ����
				corpDto.setNmoneyday(new BigDecimal(columns[8]));//���ۼƽ��
				corpDto.setNmoneytenday(new BigDecimal(columns[9]));//Ѯ�ۼƽ��
				corpDto.setNmoneymonth(new BigDecimal(columns[10]));//���ۼƽ��
				corpDto.setNmoneyquarter(new BigDecimal(columns[11]));//���ۼƽ��
				corpDto.setNmoneyyear(new BigDecimal(columns[12]));//���ۼƽ��
				paramList2.add(corpDto);
			}
		}else{
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"�޷�ͬ��δ֪�����ݣ�");
		}
		String sql1 = "";
		String sql2 = "";
		if(paraType.equals("0")){
			sql1 = "DELETE FROM TR_INCOMEDAYRPT WHERE S_FINORGCODE = '" + orgCode + "'";
			sql2 = "DELETE FROM TR_TAXORG_PAYOUT_REPORT WHERE S_FINORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql1);
				sqlEx.runQueryCloseCon(sql2);
				DatabaseFacade.getODB().create((TrIncomedayrptDto[])paramList1.toArray(new TrIncomedayrptDto[paramList1.size()]));
				DatabaseFacade.getODB().create((TrTaxorgPayoutReportDto[])paramList2.toArray(new TrTaxorgPayoutReportDto[paramList2.size()]));
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
	}

	/**
	 * tbs�Ŀ��ͬ����ITFE(���)
	 * @param orgCode �����������
	 * @param paraType ����һ��ռλ��Ϊ0
	 * @param paramContent ͬ��������
	 * @return �Ƿ�ɹ� 0:�����ɹ� -1:����ʧ��
	 */
	public String synchronousStockRpt(String orgCode, String paraType,
			String paramContent) {
		logger.info("TBS���ýӿ�(synchronousStockRpt)orgCode:" + orgCode + "paraType:" + paraType
				+ "paramContent:" + paramContent);
		if(paraType == null || paraType.trim().equals("")){
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"-��������paraType����Ϊ��");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"��汨������paramContent����Ϊ��");
		}
		String[] datas = paramContent.substring(paramContent.indexOf("**") + 2).split("\\*\\*");
		TrStockdayrptDto bsDto = null;
		List paramList = new ArrayList();
		StringBuffer sberror = new StringBuffer();
		if(paraType.equals("0")){ // 0
			for(String str:datas){
				bsDto = new TrStockdayrptDto();
				String[] columns = str.split(",");
				bsDto.setSorgcode(orgCode);//������������ S_ORGCODE
				bsDto.setStrecode(columns[1]);//������� S_TRECODE
				bsDto.setSrptdate(columns[0]);//S_RPTDATE
				bsDto.setSaccno(columns[2]);//�ʻ����� S_ACCNO
				bsDto.setSaccname(columns[3]);//�ʻ����� S_ACCNAME
				bsDto.setSaccdate("");//�ʻ����� S_ACCDATE
				bsDto.setNmoneyyesterday(new BigDecimal(columns[4]));//������� N_MONEYYESTERDAY
				bsDto.setNmoneyin(new BigDecimal(columns[5]));//�������� N_MONEYIN
				bsDto.setNmoneyout(new BigDecimal(columns[6]));//����֧�� N_MONEYOUT
				bsDto.setNmoneytoday(new BigDecimal(columns[7]));//������� N_MONEYTODAY

				paramList.add(bsDto);
			}
		
		}else{
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL+"�޷�ͬ��δ֪�����ݣ�");
		}
		String sql = "";
		if(paraType.equals("0")){ // 0
			sql = "DELETE FROM TR_STOCKDAYRPT WHERE S_ORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql);
				DatabaseFacade.getODB().create((TrStockdayrptDto[])paramList.toArray(new TrStockdayrptDto[paramList.size()]));
				
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
	}

	/**
	 * ��������ϸͬ����tbs��
	 * @param strecode �������
	 * @param checkDate ��������
	 * @param checkNum ���˴���
	 */
	public String synchronousAcctBalance(String strecode, String checkDate, String checkNum) {
		logger.info("TBS���ýӿ�(synchronousAcctBalance)strecode:" + strecode + "checkDate:" + checkDate
				+ "checkNum:" + checkNum);
		StringBuffer sbuf = new StringBuffer();
		try {
			List<TfReconciliationDto> recList = null;
			TfReconciliationDto recDto = new TfReconciliationDto();
			recDto.setStrecode(strecode);
			recDto.setSchkdate(checkDate);
			recList = CommonFacade.getODB().findRsByDtoWithUR(recDto);
			if(recList==null || recList.size()==0){
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL);
			}
			
			//�ж϶��˴�����tbs��ͬ����ȥ�Ķ������ݵĶ��˴����Ƿ���ͬ�������ͬ������Ҫ��ͬ������(ǰ�õĶ�����Ϣ����ȡ���Ķ��˴�����tbs����ͬ����ȥ�Ķ��˴������)
			Integer chkNo = recList.get(0).getIpackno();
			for(int i=1;i<recList.size();i++){
				if(recList.get(i).getIpackno()>chkNo){
					chkNo = recList.get(i).getIpackno();
				}
			}
			if(checkNum==null||"".equals(checkNum)||"null".equals(checkNum.toLowerCase())){
				checkNum="";
			}
			if(checkNum != null && !checkNum.trim().equals("") && Integer.parseInt(checkNum)==chkNo){
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL);
			}
			
			//������ǰ��ȱ�ٵ��˿�֪ͨ��Ϣ��ֻ�н��յĶ�����Ϣ�����м�¼��Ҫ�ѽ��ͬ����tbs
			String sql = "select S_TRECODE,S_PACKNO,N_CURPACKVOUAMT,S_PAYOUTVOUTYPE,S_EXT1 " +
					"from TF_RECONCILIATION where S_TRECODE = ? and S_CHKDATE = ? and S_EXT1 = '3' " +
					"and I_PACKNO = (SELECT max(I_PACKNO) FROM TF_RECONCILIATION)";
			SQLExecutor sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe.addParam(strecode);
			sqlExe.addParam(checkDate);
			SQLResults rs = sqlExe.runQueryCloseCon(sql);
			if(rs != null && rs.getRowCount()>0){
				for(int i=0;i<=rs.getRowCount();i++){
					sbuf.append("**");
					sbuf.append(rs.getString(i, "S_TRECODE") + ","); //�������
					sbuf.append(rs.getString(i, "S_PACKNO") + ","); //������Ϊƾ֤���
					sbuf.append(rs.getDouble(i, "N_CURPACKVOUAMT") + ","); //������
//					sbuf.append(rs.getString(i, "S_PAYOUTVOUTYPE") + ","); //ҵ������
					sbuf.append("35,"); //ҵ������--�˿�֪ͨ��ҵ������д��35
					sbuf.append(rs.getString(i, "S_TRECODE") + ","); //�������д���
					sbuf.append(checkDate + ","); //��������
					sbuf.append(rs.getString(i, "S_EXT1") + ","); //���˽��
					sbuf.append(recList.get(0).getIpackno()); //���˴���
				}
			}
			
			TfFundAppropriationDto appropriationDto = new TfFundAppropriationDto();
			appropriationDto.setSentrustdate(checkDate);
			appropriationDto.setStrecode(strecode);
			List<TfFundAppropriationDto> fundList = null;
			fundList = CommonFacade.getODB().findRsByDtoWithUR(appropriationDto);
			if (null == fundList || fundList.size() == 0) {//�������û���ʽ𲦸�����û�кʹ����з���ҵ��
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL);
			}
			
			for(int i=0;i<fundList.size();i++){
				appropriationDto =  fundList.get(i);
				sbuf.append("**");
				sbuf.append(appropriationDto.getStrecode() + ","); //�������
				sbuf.append(appropriationDto.getSvouno() + ","); //ƾ֤���
				sbuf.append(appropriationDto.getNallamt() + ","); //������
				if(appropriationDto.getSpayoutvoutype().equals("1")){//ʵ�����˿�
					sbuf.append("17,"); //ҵ������
					sbuf.append(appropriationDto.getStrecode() + ","); //���д��� ��������
				}else if(appropriationDto.getSpayoutvoutype().equals("2")){
					sbuf.append("13,"); //ҵ������
					sbuf.append(appropriationDto.getStrecode() + ","); //���д��� ��������
				}else if(appropriationDto.getSpayoutvoutype().equals("3")){//����֧��
					sbuf.append(appropriationDto.getSext2().equals("1") ? "27" : "25"); //ҵ������       25-ֱ��֧��   27-��Ȩ֧��
					sbuf.append(",");
					sbuf.append(appropriationDto.getSpayeeopbkno() + ",");
				}else{
					sbuf.append("N,"); //ҵ������
					sbuf.append("N,"); //�������д���
				}
				sbuf.append(checkDate + ","); //��������
				sbuf.append(((appropriationDto.getSext1()==null || appropriationDto.getSext1().equals(""))?"0":appropriationDto.getSext1()) + ","); //���˽��
				sbuf.append(recList.get(0).getIpackno()); //���˴���
			}
			
			List<TfRefundNoticeDto> refundList = null;
			TfRefundNoticeDto refundDto = new TfRefundNoticeDto();
			refundDto.setStrecode(strecode);
			refundDto.setSentrustdate(checkDate);
			refundList = CommonFacade.getODB().findRsByDtoWithUR(refundDto);
			if(refundList != null && refundList.size()>0){
				for(int i=0;i<refundList.size();i++){
					refundDto = refundList.get(i);
					sbuf.append("**");
					sbuf.append(refundDto.getStrecode() + ","); //�������
					sbuf.append(refundDto.getSvouno() + ","); //ƾ֤���
					sbuf.append(refundDto.getNallamt() + ","); //������
					sbuf.append("35,"); //ҵ������
					sbuf.append(refundDto.getSpayerbankno() + ","); //�������д���
					sbuf.append(checkDate + ","); //��������
					sbuf.append(((refundDto.getSext1()==null || refundDto.getSext1().equals(""))?"0":refundDto.getSext1()) + ","); //���˽��
					sbuf.append(recList.get(0).getIpackno()); //���˴���
				}
			}
			
			//���˽��ͬ����ɺ󣬽���ϸ��״̬�ָ����ٴζ���ʹ��
			String fundSql = "update TF_FUND_APPROPRIATION set S_EXT1 = '' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
			String checkSql = "update TF_RECONCILIATION set S_EXT1 = '' where S_TRECODE = ? and S_CHKDATE = ?";
			String refundSql = "update TF_REFUND_NOTICE set S_EXT1 = '' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
			SQLExecutor sqlExe1 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe1.addParam(strecode);
			sqlExe1.addParam(checkDate);
			sqlExe1.runQuery(fundSql);
			
			sqlExe1.clearParams();
			sqlExe1.addParam(strecode);
			sqlExe1.addParam(checkDate);
			sqlExe1.runQuery(checkSql);
			
			sqlExe1.clearParams();
			sqlExe1.addParam(strecode);
			sqlExe1.addParam(checkDate);
			sqlExe1.runQueryCloseCon(refundSql);
		} catch (Exception e) {
			logger.error(e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
		logger.info("TBS���ýӿ�(synchronousAcctBalance)strecode:" + strecode + "checkDate:" + checkDate+ "checkNum:" + checkNum+"+��������:"+sbuf.toString());
		return TBSVerifyUtils.base64Encode(sbuf.toString());
	}

	public void tbsErrorLog(String msgNo, String errorMsg) {
		Exception e = new Exception(errorMsg);
		logger.error(msgNo, e);
		if(msgNo==null||msgNo.equals("")){
			msgNo=MsgConstant.VOUCHER+"_"+MsgConstant.MSG_NO_0000;;
		}
		if(msgNo.indexOf(MsgConstant.VOUCHER)<0){
			msgNo=MsgConstant.VOUCHER+"_"+msgNo;
		}
		
		TvExceptionmanDto tDto=new TvExceptionmanDto();
		tDto.setSexceptioninfo(e.getMessage()==null?(getErrStr(e.toString())):(getErrStr(e.getMessage())));
		tDto.setSofbizkind(msgNo);
		tDto.setTsupdate(new Timestamp(new java.util.Date().getTime()));
		tDto.setDworkdate(TimeFacade.getCurrentStringTime());
		try {
			DatabaseFacade.getODB().create(tDto);
		} catch (JAFDatabaseException e1) {
			logger.error("��¼ƾ֤������־�����쳣", e1);
		}
	}
	
	
	public String getErrStr(String errStr){
		if(errStr != null){
			if(errStr.length()>1024){
				return errStr.substring(0, 1000);
			}else{
				return errStr;
			}
		}
		return errStr;
	}

	public String updateVoucherStatus(String admDivCode, String vtCode, String treCode, String vouDate, String vouNo) {
		logger.info("TBS���ýӿ�(updateVoucherStatus)����admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate + "vouNo:" + vouNo);
		try {
			// У�������Ϣ
			String str = TBSVerifyUtils.verifyConfirmVoucherParam(admDivCode,vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
			}
			// ��ѯ�������Ӧ����Ϣ
			TvVoucherinfoDto tvVoucherinfoDto = TBSVerifyUtils.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			// 5106��5108��Ҫ��¼X�ֶ�
			updateMainDto(tvVoucherinfoDto);
			// tbs�ӿڶ�ȡƾ֤��״̬������S_EXT1�ֶ��� ����TBSУ��ɹ�
			if(!"71727380".contains(tvVoucherinfoDto.getSstatus()))
			{
				tvVoucherinfoDto.setSstatus(TbsDealCodeConstants.VOUCHER_DEALWITH_SUCESS);//����ɹ�
			}
			tvVoucherinfoDto.setSext1(TbsDealCodeConstants.VOUCHER_DEALWITH_SUCESS);
			tvVoucherinfoDto.setSdemo("TBS����ɹ�");
			DatabaseFacade.getODB().update(tvVoucherinfoDto);
			return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
		} catch (ITFEBizException e) {
			logger.error("TbsImpl.updateVoucherStatus[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.updateVoucherStatus[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
	}
	
}
