package com.cfcc.itfe.twcs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsTbsorgstatusDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
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
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * 
 */

@WebService(endpointInterface = "com.cfcc.itfe.twcs.ITwcs", serviceName = "TwcsImpl")
public class TwcsImpl implements ITwcs {
	private static Log logger = LogFactory.getLog(TwcsImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.itfe.tbs.ITbs#sayHi(java.lang.String)
	 */
	public String sayHi(String text) {
		try {
			logger.info("����������:" + text);
			return VoucherUtil.base64Encode(text + "hello World!");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
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
		logger.info("TWCS���ýӿ�(readVoucher)����admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate);
		SQLExecutor sqlExecutor = null;
		try {
			List params = new ArrayList();
			params.add(vtCode);
			params.add(treCode);
			params.add(vouDate);
			// ��ѯ S_EXT1Ϊ��(��ƾ֤���ȡ����ʱ�Ѿ���ֵΪ���ַ�) ƾ֤״̬Ϊ20 У��ɹ�,50���˳ɹ�������(5207Ϊ��˳ɹ�--��¼�տ��˿������к�)
			String whereSql=" WHERE  length(S_EXT1) != 2  AND  S_VTCODE = ?  AND S_TRECODE  = ? AND S_CREATDATE <= ? AND (S_STATUS = '20' OR S_STATUS = '50') AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			if (treCode.startsWith("22") && (MsgConstant.VOUCHER_NO_5207.equals(vtCode))) {
				whereSql=" WHERE  length(S_EXT1) != 2  AND  S_VTCODE = ?  AND S_TRECODE  = ? AND S_CREATDATE <= ? AND (S_STATUS = '20') AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			}
			//������������мල����ȡ�ѻص�״̬�ļ�¼���ڻ��ܶ���
			if (treCode.startsWith("22") && (MsgConstant.VOUCHER_NO_2301.equals(vtCode)|| MsgConstant.VOUCHER_NO_2302.equals(vtCode))) {
				 whereSql=" WHERE  (S_EXT3 IS NULL OR length(S_EXT3) != 2)  AND  S_VTCODE = ?  AND S_TRECODE  = ? AND S_CREATDATE <= ? AND (S_STATUS = '80') AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			}
			List<TvVoucherinfoDto> result = DatabaseFacade.getODB().find(TvVoucherinfoDto.class, whereSql, params);
			if (null == result || result.size() == 0) {
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
			}
			List bizDatas = TWCSVerifyUtils.getBizDatas(result);
			if (null == bizDatas || bizDatas.size() == 0) {
				logger.error("δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
			}
			// Twcs��ȡ״̬������S_EXT1��ͬʱ����status�ֶ�Ϊ"������"
			List<IDto> infoList = (List<IDto>) bizDatas.get(0);
			for (IDto tmpDto : infoList) {
				TvVoucherinfoDto voucherDto = (TvVoucherinfoDto)tmpDto;
				//������⣬����֧��ҵ���ȡ��,S_EXT3״̬�޸�85 : ��ע���ϵͳ�Ѷ�ȡ
				if (treCode.startsWith("22") && (MsgConstant.VOUCHER_NO_2301.equals(vtCode)|| MsgConstant.VOUCHER_NO_2302.equals(vtCode))) {
					voucherDto.setSext3(TwcsDealCodeConstants.VOUCHER_ACCEPT_RETURNVOUCHER);
					voucherDto.setSdemo(TwcsDealCodeConstants.VOUCHER_ACCEPT_INFO);
					voucherDto.setSext1(TwcsDealCodeConstants.VOUCHER_ACCEPT_INFO);
				}else{//�Ĵ���ȡ�� �޸�  status Ϊ �����57  ������ʾ�����
					voucherDto.setSext1(DealCodeConstants.VOUCHER_REGULATORY_AUDITING);
					voucherDto.setSstatus(DealCodeConstants.VOUCHER_REGULATORY_AUDITING);
					voucherDto.setSdemo(TwcsDealCodeConstants.VOUCHER_ACCEPT_AUDITING);
				}
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(infoList));
			logger.info("�����" + (String) bizDatas.get(1));
			return VoucherUtil.base64Encode((String) bizDatas.get(1));
		} catch (Exception e) {
			logger.error("TwcsImpl.readVoucher[" + e.getMessage() + "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} finally {
			if (null != sqlExecutor) {
				sqlExecutor.closeConnection();
			}
		}
	}
	/**
	 * �����ȡ����֧������ɹ��󣬵��øýӿڸ��¼���֧����ȡ״̬Ϊ��ȡ�ɹ�
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
	public String updateConPayState( String vtCode,
			String treCode, String vouDate) {
		logger.info("CQ����֧��ҵ��ǩ��" + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate);
		try {
			// ��ѯ�������Ӧ����Ϣ
			List <TvVoucherinfoDto> list  = TWCSVerifyUtils
					.getTvVoucherinfoList(treCode, vtCode, vouDate,
							null);
			if (null == list || list.size()>0) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			// ����TWCS��ȡ���ݳɹ�
			List <IDto> updateList = new ArrayList<IDto>();
			for (TvVoucherinfoDto tvd : list) {
				tvd.setSext3(TwcsDealCodeConstants.VOUCHER_ACCEPT_SUCCESS);
				tvd.setSdemo(TwcsDealCodeConstants.TWCS_ACCEPT_SUCCESS);
				updateList.add(tvd);
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(updateList));
			return VoucherUtil
					.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (ITFEBizException e) {
			logger.error("TbsImpl.confirmVoucher[" + e.getMessage() + "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.confirmVoucher[" + e.getMessage() + "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
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
				mainDto = (TvGrantpaymsgmainDto) CommonFacade.getODB()
						.findRsByDto(mainDto).get(0);
				List<TvGrantpaymsgsubDto> subDtoList = CommonFacade.getODB()
						.findRsByDto(subDto);
				if (subDtoList == null || subDtoList.size() == 0)
					throw new ITFEBizException("��ѯ�ӱ���Ϣ�쳣����ƾ֤��"
							+ vDto.getSvoucherno() + "��Ӧ���ӱ��¼�����ڣ�");
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
				mainDto = (TvDirectpaymsgmainDto) CommonFacade.getODB()
						.findRsByDto(mainDto).get(0);
				mainDto.setSxacctdate(xpaydate);
				mainDto.setSstatus(status);
				DatabaseFacade.getODB().update(mainDto);
			}
		} catch (java.lang.IndexOutOfBoundsException e) {
			logger.error("��ѯ������Ϣ�쳣����ƾ֤��" + vDto.getSvoucherno()
					+ "��Ӧ�������¼�����ڣ�   ");
			throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��" + vDto.getSvoucherno()
					+ "��Ӧ�������¼�����ڣ�", e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
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
	 *            ƾ֤����
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
			String str = TWCSVerifyUtils.verifyReturnVoucherBackParam(
					admDivCode, vtCode, treCode, vouDate, vouNo, errMsg);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
			}
			// ��ѯ�������Ӧ����Ϣ
			TvVoucherinfoDto tvVoucherinfoDto = TWCSVerifyUtils
					.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,
							admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error(VoucherUtil.base64Encode("δ�ҵ���Ӧ��ƾ֤��Ϣ"));
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			// �ж�ƾ֤״̬�Ƿ�����
			str = TWCSVerifyUtils.verifyInfoLocked(tvVoucherinfoDto);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
			}
			// �ȴ������������ʽ���ƾ֤ �������˻�
			if (!(TwcsDealCodeConstants.VOUCHER_ACCEPT.equals(tvVoucherinfoDto
					.getSext1().trim())
					|| TwcsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS
							.equals(tvVoucherinfoDto.getSext1().trim())
					|| TwcsDealCodeConstants.VOUCHER_VALIDAT_FAIL
							.equals(tvVoucherinfoDto.getSext1().trim()) || TwcsDealCodeConstants.VOUCHER_LIQUIDATION_FAILE
					.equals(tvVoucherinfoDto.getSext1().trim()))) {
				logger.error("״̬Ϊ�����С�����ɹ����ص��ɹ����˻سɹ����������");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "״̬Ϊ�����С�����ɹ����ص��ɹ����˻سɹ����������");
			}
			// ����ƾ֤��ִ�� ƾ֤�˻�
			VoucherService voucherService = new VoucherService();
			String err = voucherService.returnVoucherBack(null, admDivCode,
					Integer.parseInt(tvVoucherinfoDto.getSstyear()), vtCode,
					new String[] { vouNo },
					new String[] { errMsg.length() > 50 ? errMsg.substring(0,
							50) : errMsg });
			if (StringUtils.isNotBlank(err)) {
				// ����ƾ֤��ʧ�ܵģ���¼״̬��ԭ�� �����������
				TWCSVerifyUtils.updateVoucherInfoStatus(tvVoucherinfoDto,
						TwcsDealCodeConstants.VOUCHER_APP_FAIL, errMsg);
				logger.error("[�������:" + treCode + "ƾ֤���:" + vouNo + "ƾ֤����:"
						+ vouDate + "]����ƾ֤��returnVoucherʧ��");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + err);
			}
			// ��¼ƾ֤״̬���˻�ԭ��
			TWCSVerifyUtils.updateVoucherInfoStatus(tvVoucherinfoDto,
					TwcsDealCodeConstants.VOUCHER_FAIL, "�˻سɹ�");
			return VoucherUtil
					.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (ITFEBizException e) {
			logger
					.error("TbsImpl.returnVoucherBack[" + e.getMessage() + "]",
							e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger
					.error("TbsImpl.returnVoucherBack[" + e.getMessage() + "]",
							e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
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
			String str = TWCSVerifyUtils.verifySendVoucherParam(admDivCode,
					vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
			}
			// ��ѯ�������Ӧ����Ϣ
			TvVoucherinfoDto tvVoucherinfoDto = TWCSVerifyUtils
					.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,
							admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ӧ��ƾ֤��Ϣ");
			}
			// �ж�ƾ֤״̬�Ƿ�����
//			str = TBSVerifyUtils.verifyInfoLocked(tvVoucherinfoDto);
//			if (StringUtils.isNotBlank(str)) {
//				logger.error(str);
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
//			}

			if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0) { // ����ƾ֤������ģʽ
				if (!(TwcsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS
						.equals(tvVoucherinfoDto.getSext1().trim()))) {
					logger.error("ֻ�ܷ���У��ɹ���ƾ֤");
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "ֻ�ܷ���У��ɹ���ƾ֤");
				}
			} else if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode02,") >= 0) { // ��������ģʽ
//				if (!(TbsDealCodeConstants.VOUCHER_LIQUIDATION_SUCCEED.equals(tvVoucherinfoDto.getSext1().trim()))) {
//					logger.error("ֻ�ܷ�������ɹ���ƾ֤");
//					return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "ֻ�ܷ�������ɹ���ƾ֤");
//				}
				if((tvVoucherinfoDto.getSvtcode().equals("2301") || tvVoucherinfoDto.getSvtcode().equals("5207") 
						|| tvVoucherinfoDto.getSvtcode().equals("5209")) 
						&& !TwcsDealCodeConstants.VOUCHER_LIQUIDATION_SUCCEED.equals(tvVoucherinfoDto.getSext1().trim())){
					logger.error("ֻ�ܷ�������ɹ���ƾ֤");
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "ֻ�ܷ�������ɹ���ƾ֤");
				}
			} else {
				logger.error("TBS�ʽ�����ģʽ��������");
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "TBS�ʽ�����ģʽ��������");
			}

			// �Ƿ�ϵͳ�Զ�����ƾ֤��Ϣ
			boolean isSysGenVoucherFlag = Boolean.FALSE;
			if (MsgConstant.VOUCHER_NO_3208.equals(tvVoucherinfoDto.getSvtcode())) {
				isSysGenVoucherFlag = true;
			}
			// ǩ����ǩ�º�ת������SYSConfig.xml����
			if (ITFECommonConstant.TBS_011.containsKey(tvVoucherinfoDto.getSvtcode())) {
				TWCSVerifyUtils.sendVoucherUtil(ITFECommonConstant.TBS_011,
						tvVoucherinfoDto, StateConstant.ORGCODE_FIN,
						isSysGenVoucherFlag);
			}
			if (ITFECommonConstant.TBS_111.containsKey(tvVoucherinfoDto.getSvtcode())) {
				if(null == ITFECommonConstant.TBS_TREANDBANK || ITFECommonConstant.TBS_TREANDBANK.size() == 0){
					throw new ITFEBizException("���ݹ�������ȡ��������Ϣʧ�ܣ�");
				}
				TWCSVerifyUtils.sendVoucherUtil(ITFECommonConstant.TBS_111,
						tvVoucherinfoDto, ITFECommonConstant.TBS_TREANDBANK.get(tvVoucherinfoDto.getStrecode()),
						isSysGenVoucherFlag);
			}
			return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error("TbsImpl.sendVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.sendVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
	}

	/**
	 * ƾ֤״̬ͬ��
	 * @param ƾ֤��ˮ�ż���
	 * @throws ITFEBizException
	 */
	public String synchronousVoucherStatus(String vtCode,String dealnos) {
		logger.info("TWCS���ýӿ�(synchronousVoucherStatus)����:dealnos" +dealnos );
		try {
			String str = VoucherUtil.base64Decode(dealnos);
			if (StringUtils.isBlank(str)) {
				logger.error("��ִ����ʧ��"+str);
				return null;
			}
			String retMsg = TWCSVerifyUtils
					.updateVoucherStatus(vtCode,str);
			// ����TWCS��ȡ��ƾ֤״̬
			return VoucherUtil.base64Encode(retMsg);
		} catch (ITFEBizException e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
		
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}
	
	
	/**
	 * ͬ���Ĵ�����ҵ��ƾ֤������״̬
	 * @param ƾ֤��ˮ�ż���
	 * @throws ITFEBizException
	 */
	public String updateVoucherReckStatus(String vtCode,String dealnos) {
		logger.info("TWCS���ýӿ�(updateVoucherReckStatus)����:dealnos" +dealnos );
		try {
			String str = VoucherUtil.base64Encode(dealnos);
			if (StringUtils.isBlank(str)) {
				logger.error("��ִ����ʧ��"+str);
				return null;
			}
			String retMsg = TWCSVerifyUtils
					.updateVoucherReckStatus(vtCode,str);
			// ����TWCS��ȡ��ƾ֤״̬
			return VoucherUtil.base64Encode(retMsg);
		} catch (ITFEBizException e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
		
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
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
			String str = TWCSVerifyUtils.verifyReadVoucherParam(admDivCode,
					vtCode, treCode, vouDate);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
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
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
			}
			List bizDatas = TWCSVerifyUtils.getBizDatas(result);
			if (null == bizDatas || bizDatas.size() == 0) {
				logger.error("δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ҫ��ȡ��ƾ֤��Ϣ");
			}
			// �����������¼Ϊ ����״̬
			List<IDto> infoList = (List<IDto>) bizDatas.get(0);
			for (IDto tmpDto : infoList) {
				((TvVoucherinfoDto) tmpDto)
						.setSext3(TwcsDealCodeConstants.VOUCHER_LOCKING);
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(infoList));
			return VoucherUtil.base64Encode((String) bizDatas.get(1));
		} catch (JAFDatabaseException e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
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
				+ "vouNo:");
		SQLExecutor sqlExecutor = null;
		try {
			if (MsgConstant.VOUCHER_NO_5106.equals(vtCode)
					|| MsgConstant.VOUCHER_NO_5108.equals(vtCode)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "��������Ϣ����������ʽ��������");
			}
			// �������ò���
			if (!(ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode02,") >= 0)) {
				logger.error("������ʽ�����ģʽ��������");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "������ʽ�����ģʽ��������");
			}
			// У�������Ϣ
			String str = TWCSVerifyUtils.verifyConfirmVoucherParam(admDivCode,
					vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);

			}
			// ��ѯ��������Ϣ
			TvVoucherinfoDto vDto = TWCSVerifyUtils.getTvVoucherinfoDto(treCode,
					vtCode, vouDate, vouNo, admDivCode);
			if (null == vDto) {
				logger.error("δ�ҵ���Ӧ��ƾ֤��Ϣ");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "δ�ҵ���Ӧ��ƾ֤��Ϣ");
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
			MuleMessage message = new DefaultMuleMessage("");
			MuleClient client = new MuleClient();
			message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY,
					"TBS_1000");
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			// message.setProperty("orgCode", vDto.getSorgcode());
			message = client.send("vm://ManagerMsgWithCommBank", message);
			vDto = TWCSVerifyUtils.getTvVoucherinfoDto(treCode, vtCode, vouDate,
					vouNo, admDivCode);
			String sql = "SELECT count(*) FROM TF_FUND_APPROPRIATION WHERE S_PACKNO = ? AND S_TRECODE = ? AND S_VOUNO = ? AND S_VOUDATE = ?";
			sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExecutor.addParam(vDto.getSpackno());
			sqlExecutor.addParam(vDto.getStrecode());
			sqlExecutor.addParam(vDto.getSvoucherno());
			sqlExecutor.addParam(vDto.getSext4());
			SQLResults sqlResults = sqlExecutor.runQuery(sql);
			if (sqlResults.getInt(0, 0) == 0) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�������㱨��ʧ��");
			} else {

				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
			}
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
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
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
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
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�������벻��Ϊ��");
			}
			Pattern p = Pattern.compile("[0-9]{12}");
			Matcher m = p.matcher(orgCode);
			if (!m.matches()) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�����������Ϊ12Ϊ����");
			}
			if (StringUtils.isBlank(password)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "���벻��Ϊ��");
			} else if (!("00000000".equals(password))) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�������");
			}
			if (StringUtils.isBlank(operate)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�������Ͳ���Ϊ��");
			} else if (operate.length() != 1) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�������ͳ���Ϊ1Ϊ��0��½1�˳�");
			} else if (!("0".equals(operate) || "1".equals(operate))) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�������ͳ���Ϊ1Ϊ��0��½1�˳�");
			}
			TsTbsorgstatusDto tsTbsorgstatusDto = new TsTbsorgstatusDto();
			tsTbsorgstatusDto.setSorgcode(orgCode);
			tsTbsorgstatusDto.setSpassword(password);
			List resultList = CommonFacade.getODB().findRsByDto(
					tsTbsorgstatusDto);
			if (null == resultList || resultList.size() == 0) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "�û���δ�ҵ�");
			}
			tsTbsorgstatusDto = (TsTbsorgstatusDto) resultList.get(0);
			tsTbsorgstatusDto.setSstatus(operate);
			DatabaseFacade.getODB().update(tsTbsorgstatusDto);
			return VoucherUtil
					.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
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
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
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
			return TWCSVerifyUtils.jxVoucherByType(voucherType, fileContents);
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
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
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
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"-��������paraType����Ϊ��");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"��������paramContent����Ϊ��");
		}
		String[] datas = paramContent.substring(paramContent.indexOf("**") + 2).split("\\*\\*");
		TsBudgetsubjectDto bsDto = null;
		TdCorpDto  corpDto = null;
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
				bsDto.setSsubjectattr(columns[5]);
				bsDto.setSinoutflag(columns[6]);
				bsDto.setSwriteflag(columns[7]);
				bsDto.setSmoveflag(columns[8].equals("null")?"":columns[8]);
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
		}else{
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"�޷�ͬ��δ֪�Ĳ�����");
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
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
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
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
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
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"-������������paraType����Ϊ��");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"��������paramContent����Ϊ��");
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
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"�޷�ͬ��δ֪�����ݣ�");
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
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
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
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"-��������paraType����Ϊ��");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"��汨������paramContent����Ϊ��");
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
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"�޷�ͬ��δ֪�����ݣ�");
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
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
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
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
			}
			
			//�ж϶��˴�����tbs��ͬ����ȥ�Ķ������ݵĶ��˴����Ƿ���ͬ�������ͬ������Ҫ��ͬ������(ǰ�õĶ�����Ϣ����ȡ���Ķ��˴�����tbs����ͬ����ȥ�Ķ��˴������)
			Integer chkNo = recList.get(0).getIpackno();
			for(int i=1;i<recList.size();i++){
				if(recList.get(i).getIpackno()>chkNo){
					chkNo = recList.get(i).getIpackno();
				}
			}
			if(checkNum != null && !checkNum.trim().equals("") && Integer.parseInt(checkNum)==chkNo){
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
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
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
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
					sbuf.append("25,"); //ҵ������       25-ֱ��֧��   27-��Ȩ֧��     ��д25
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
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
		
		return TWCSVerifyUtils.base64Encode(sbuf.toString());
	}
	
	/**
	 * ������ֽ��ƾ֤�������������״̬
	 * @param strecode �������
	 * @param checkDate ��������
	 * @param checkNum ���˴���
	 */
	public String updateItfeVoucherInfo(String vtCode, String msgInfo) {
		SQLExecutor  updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String[] str  = msgInfo.split("####\\*\\*\\*\\*");
			if (str.length> 0) {
				for (String str1 : str) {
					String[] strcol  = str1.split("\\|\\|");
					updateExce.clearParams();
					//�ַ�����ϣ�ƾ֤���� +ƾ֤��ˮ��+����״̬+����+������+��������+֧���������к�
					if (MsgConstant.VOUCHER_NO_2301.equals(vtCode)) {
						String updateSql = "update TV_PAYRECK_BANK  set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ?  where I_VOUSRLNO = ?  and S_RESULT = ? ";
						updateExce.addParam(strcol[2]);//����״̬
						updateExce.addParam(strcol[3]);//����
						updateExce.addParam(java.sql.Date.valueOf(strcol[5]));//��������
						updateExce.addParam(new BigDecimal(strcol[4]));//������
						updateExce.addParam(java.sql.Date.valueOf(strcol[5]));//��������
						updateExce.addParam(strcol[6]);//֧���������к�
						updateExce.addParam(Long.valueOf(strcol[1]));//ƾ֤��ˮ��
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
						updateExce.runQuery(updateSql);
					}
					//�ַ�����ϣ�ƾ֤���� +ƾ֤��ˮ��+����״̬+����+������+��������+֧���������к�
					if (MsgConstant.VOUCHER_NO_2302.equals(vtCode)) {
						String updateSql = "update TV_PAYRECK_BANK_BACK  set S_STATUS = ? , S_ADDWORD = ? ,S_XPAYAMT = ? ,S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ?  where I_VOUSRLNO = ?  and S_STATUS = ? ";
						updateExce.addParam(strcol[2]);//����״̬
						updateExce.addParam(strcol[3]);//����
						updateExce.addParam(new BigDecimal(strcol[4]));//������
						updateExce.addParam(java.sql.Date.valueOf(strcol[5]));//��������
						updateExce.addParam(strcol[6]);//֧���������к�
						updateExce.addParam(Long.valueOf(strcol[1]));//ƾ֤��ˮ��
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
						updateExce.runQuery(updateSql);
					}
					if (MsgConstant.VOUCHER_NO_5108.equals(vtCode)) {
					 //�ַ�����ϣ�ƾ֤���� +ƾ֤��ˮ��+����״̬+����+��������
					  String updateSql =	"update TV_DIRECTPAYMSGMAIN set S_STATUS = ? , S_DEMO = ? ,S_ACCDATE = ?   where I_VOUSRLNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//����״̬
						updateExce.addParam(strcol[3]);//����
						updateExce.addParam(strcol[4]);//��������
						updateExce.addParam(Long.valueOf(strcol[1]));//ƾ֤��ˮ��
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
						updateExce.runQuery(updateSql);
					}
					//�ַ�����ϣ�ƾ֤���� +ƾ֤��ˮ��+����״̬+����+��������+֧�����+������ˮ��+ί������
					if (MsgConstant.VOUCHER_NO_5106.equals(vtCode)) {
						String updateSql =	"update TV_GRANTPAYMSGMAIN set S_STATUS = ? , S_DEMO = ? ,S_ACCDATE = ? ,N_XALLAMT=?  where I_VOUSRLNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//����״̬
						updateExce.addParam(strcol[3]);//����
						updateExce.addParam(strcol[4]);//��������
						updateExce.addParam(new BigDecimal(strcol[5]));//֧�����
						updateExce.addParam(Long.valueOf(strcol[1]));//ƾ֤��ˮ��
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
						if(VoucherUtil.updateVoucherSplitReceiveTCBS(strcol[6], strcol[2], new BigDecimal(strcol[5]), strcol[4], strcol[7], strcol[3])){
							continue;
						}
						updateExce.runQuery(updateSql);	
					}
					if (MsgConstant.VOUCHER_NO_5207.equals(vtCode)) {
						//�ַ�����ϣ�ƾ֤���� +ƾ֤��ˮ��+����״̬+����+������+������ˮ��
						String updateSql =	"update TV_PAYOUTMSGMAIN set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?   where S_BIZNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//����״̬
						updateExce.addParam(strcol[3]);//����
						updateExce.addParam(new BigDecimal(strcol[4]));//������
						updateExce.addParam(strcol[5]);//��������
						updateExce.addParam(strcol[6]);//������ˮ��
						updateExce.addParam(Long.valueOf(strcol[1]));//ƾ֤��ˮ��
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
						updateExce.runQuery(updateSql);
					}
					if (MsgConstant.VOUCHER_NO_5209.equals(vtCode)) {
						//�ַ�����ϣ�ƾ֤���� +ƾ֤��ˮ��+����״̬+����+������
						String updateSql =	"update TV_DWBK set S_STATUS = ? , S_DEMO = ? ,XPAYAMT=?   where I_VOUSRLNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//����״̬
						updateExce.addParam(strcol[3]);//����
						updateExce.addParam(new BigDecimal(strcol[4]));//������
						updateExce.addParam(Long.valueOf(strcol[1]));//ƾ֤��ˮ��
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// ������״̬
						updateExce.runQuery(updateSql);
					}
					//����������״̬
					updateInfoFacade.VoucherReceiveTWCS(strcol[1], strcol[2], strcol[3]);
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e, e);
			TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		} catch (Exception e) {
			logger.error(e, e);
			TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}finally{
			updateExce.closeConnection();
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
	}
	
}
