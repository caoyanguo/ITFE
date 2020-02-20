package com.cfcc.itfe.voucher.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutDetailMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;
import com.ibm.db2.jcc.am.t;

/**
 * ƾ֤У��
 * 
 * @author Yuan
 * 
 */
public class VoucherVerify {
	private static Log logger = LogFactory.getLog(VoucherVerify.class);

	private String tmpFailReason;// У��ʧ��ԭ��
	
	public VoucherVerify() {
		tmpFailReason = "";
	}

	/**
	 * ƾ֤У��
	 * 
	 * @param Lists
	 *            ƾ֤����
	 * @param vtcode
	 *            ƾ֤����
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public int verify(List lists, String vtCode) throws ITFEBizException {
		// ����У��ɹ���ƾ֤List
		List<TvVoucherinfoDto> successList = new ArrayList<TvVoucherinfoDto>();
		// �Ϻ���ֽ��У��ҵ��Ҫ�سɹ���ȶԵ�ƾ֤List
		List succList;
		int count = 0;
		VoucherCompare voucherCompare = new VoucherCompare();
		try {
			if (vtCode.equals(MsgConstant.VOUCHER_NO_5267)) {
				succList = verifyFor5207(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5207)) {
				succList = verifyFor5207(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_2301)) {
				succList = verifyFor2301(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_2302)) {
				successList = verifyFor2302(lists, vtCode);
				count = successList.size();
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5108)) {
				succList = verifyFor5108(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5106)) {
				succList = verifyFor5106(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5209)) {
				count = verifyFor5209(lists, vtCode);
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5201)) {
				succList = verifyFor5201(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_2252)) {
				count = verifyFor2252(lists, vtCode);
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5351)) {
				succList = verifyFor5351(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5253)) {
				succList = verifyFor5253(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// �ȶ�ƾ֤
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5257)) {
				successList = verifyFor5257(lists, vtCode);
				count = successList.size();
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_3507)) {
				count = verifyFor3507(lists, vtCode);
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_3508)) {
				count = verifyFor3508(lists, vtCode);
			}else if (vtCode.equals(MsgConstant.VOUCHER_NO_5407)) {
				count = verifyFor5407(lists, vtCode);
			}else if (vtCode.equals(MsgConstant.VOUCHER_NO_5671)||vtCode.equals(MsgConstant.VOUCHER_NO_5408)) {
				count = verifyFor5671(lists, vtCode);
			}
			
			//�Ϻ����ӳ���ĿУ��ʧ��ʱ������У�����ȫ���˻�
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") > 0){
				TvVoucherinfoDto tvVoucherinfoDto = null;
				TvVoucherinfoPK tvVoucherinfoPK = null;
				List returnBackVoucherList = new ArrayList();
				for(int i = 0 ; i < lists.size() ; i ++ ){
					tvVoucherinfoDto = (TvVoucherinfoDto) ((List)lists.get(i)).get(1);
					tvVoucherinfoPK = new TvVoucherinfoPK();
					tvVoucherinfoPK.setSdealno(tvVoucherinfoDto.getSdealno());
					tvVoucherinfoDto = (TvVoucherinfoDto) DatabaseFacade.getODB().find(tvVoucherinfoPK);
					//ITFE_001�����ĿУ������
//					System.out.println("״̬��" + tvVoucherinfoDto.getSstatus() + "ƾ֤���:" + tvVoucherinfoDto.getSvoucherno() + "���ԣ�" + tvVoucherinfoDto.getSdemo());
					if(DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(tvVoucherinfoDto.getSstatus().trim()) && !(tvVoucherinfoDto.getSdemo().startsWith("ITFE_001"))){
						returnBackVoucherList.add(tvVoucherinfoDto);
					}
				}
				if(null != returnBackVoucherList && returnBackVoucherList.size() > 0 ){
					Voucher voucher = (Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
					voucher.voucherReturnBack(returnBackVoucherList);
				}
				
			}
			
			
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("�鿴ҵ��������Ϣ�����ƾ֤״̬�쳣��", e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("ƾ֤У������쳣��", e);
		}
		return count;
	}
	/**
	 * У��  ��˰��������5671
	 * @param lists
	 * @param vtCode
	 * @return
	 */
	private int verifyFor5671(List lists, String vtCode) throws JAFDatabaseException, ValidateException, ITFEBizException,
	Exception{
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvNontaxmainDto mainDto = (TvNontaxmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);

			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())&& 
			verifySfinOrgCode(mainDto.getSfinorgcode(),vDto.getSorgcode(), vDto.getStrecode())
					&& verifySubject(vDto.getSorgcode(),expFuncCodeList, vtCode)
			) {
				
				VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				count++;
				succList.add(new Integer(count));
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * У�� ʵ������ƾ֤�嵥��Ϣ5257
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	private List<TvVoucherinfoDto> verifyFor5257(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		for (List list : (List<List>) lists) {
			TvPayoutDetailMainDto mainDto = (TvPayoutDetailMainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// У��
			if (verifySfinOrgCode(mainDto.getSfinorgcode(), vDto.getSorgcode(),mainDto
					.getStrecode())) {
				returnList.add(vDto);
				// ����ƾ֤״̬
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);

			}
		}
		return returnList;
	}

	/**
	 * �����˿�ƾ֤�ص�
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public List<TvVoucherinfoDto> verifyFor2302(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		for (List list : (List<List>) lists) {
			TvPayreckBankBackDto mainDto = (TvPayreckBankBackDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);

			// У��
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getSfinorgcode(), vDto.getSorgcode(),mainDto
							.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(),
							expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSbookorgcode(), tdcorpList)
					&& verifySpayInfo(mainDto)// У���ո�������Ϣ��֤ ���� �տ��˿������к� ���ո�������Ϣ
					&& verifyPaybankName(
							mainDto.getSbookorgcode(),
							mainDto.getStrecode(),
							mainDto.getSagentbnkcode(),
							"201053100013".equals(ITFECommonConstant.SRC_NODE) ? mainDto
									.getSagentacctbankname()
									: mainDto.getSpaybankname())// У�������������201053100013��ɽ���ڵ�У�������ֶκ����������ֶβ�ͬ
			) {
				// ����˿�����Ϊ�����˿���ҪУ��2301�Ƿ���ڣ���ƾ֤ ��У��2301�Ƿ����
				boolean checkflag = true;
				if (ITFECommonConstant.PUBLICPARAM.indexOf("verify2302")>0 && StateConstant.TKLX_1.equals(mainDto.getShold1())) {
					// У�黮������ԭƾ֤��Ϣ�Ƿ����
					List<IDto> payreckList = verifyVoucherExists(mainDto,
							vtCode);
					if (null != payreckList && payreckList.size() > 0) {
						returnList.add(vDto);
						checkflag = true;
					} else {
						checkflag = false;
					}
				} else {
					returnList.add(vDto);
				}
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), checkflag ? null
								: this.tmpFailReason, checkflag);
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return returnList;
	}

	/**
	 * ���뻮��ƾ֤�ص�
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public List<TvVoucherinfoDto> verifyFor2301(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvPayreckBankDto mainDto = (TvPayreckBankDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);

			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// У��
			if (verifyInfoAndPay(vDto)&&verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getSfinorgcode(), vDto.getSorgcode(),mainDto
							.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(),
							expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSbookorgcode(), tdcorpList)
					&& verifySpayInfo(mainDto)// У���ո�������Ϣ��֤ ���� �տ��˿������к� ���ո�������Ϣ
					&& verifyPaybankName(
							mainDto.getSbookorgcode(),
							mainDto.getStrecode(),
							mainDto.getSpayeeopbkno(),
							"201053100013".equals(ITFECommonConstant.SRC_NODE) ? mainDto
									.getSagentacctbankname()
									: mainDto.getSpaybankname())// У�������������201053100013��ɽ���ڵ�У�������ֶκ����������ֶβ�ͬ
			) {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					vDto.setShold3(mainDto.getSvouno());
					// ����״̬Ϊ"У����"
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5106);
					succList.add(list);
				} else {
					// ��Ȩ֧��2301���������״̬Ϊ"У��ɹ�"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					count++;
					succList.add(new Integer(count));
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * ����ֱ��֧��ƾ֤5201
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */

	@SuppressWarnings( { "static-access", "unchecked" })
	public List verifyFor5201(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TfDirectpaymsgmainDto mainDto = (TfDirectpaymsgmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// ���ܿ�Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// У��
			if (verifySubject(mainDto.getSorgcode(), expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& (mainDto.getSbusinesstypecode().equals(
							StateConstant.BIZTYPE_CODE_BATCH) ? (verifySpayInfo(mainDto) && verifyPaybankName(
							mainDto.getSorgcode(), mainDto.getStrecode(),
							mainDto.getSpayeeacctbankno(), mainDto
									.getSpayeeacctbankname()))
							: verifyPayeeBankNo(mainDto.getSpayeeacctbankno(),
									vtCode))) {
				// ����ҵ����Ҫ�ȶ�ƾ֤
				// if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
				// && mainDto.getSbusinesstypecode().equals(
				// StateConstant.BIZTYPE_CODE_BATCH)) {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					// ����ҵ��ֱ�Ӹ���״̬Ϊ"У����"
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5108);
				} else {
					// ����ҵ��ֱ�Ӹ���״̬Ϊ"У��ɹ�"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				}
				succList.add(list);
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * ֱ��֧������ƾ֤5253
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	public List verifyFor5253(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TfDirectpayAdjustmainDto mainDto = (TfDirectpayAdjustmainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// ���ܿ�Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// У��
			if (verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getSpayeeacctbankno(), vtCode)// ��������У��
			) {
				if ((ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)) {
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5108);
					succList.add(list);
				} else {
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * �տ������˿�֪ͨƾ֤2252
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public int verifyFor2252(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TfPaybankRefundmainDto mainDto = (TfPaybankRefundmainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			List<TfPaybankRefundsubDto> subList = (List<TfPaybankRefundsubDto>) list
					.get(2);// ��ϸ����Ϣ��������ϸ����ո�������ϢУ��
			if (verifyPayeeBankNo(mainDto.getSpaysndbnkno(), vtCode)
					&& verifyOriVoucher(mainDto, subList)) {
				// ����ƾ֤״̬
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * ʵ���ʽ�У����
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List verifyFor5207(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		List succList = new ArrayList();
		for (List list : (List<List>) lists) {
			TvPayoutmsgmainDto mainDto = (TvPayoutmsgmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			List<TvPayoutmsgsubDto> subdtos = list.size()>=4?(List<TvPayoutmsgsubDto>)list.get(3):null;
			// У��
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifySfinOrgCode(mainDto.getSpayunit(), mainDto.getSorgcode(),mainDto.getStrecode())
					&& verifyAccName(mainDto.getSorgcode(), mainDto
							.getStrecode(), mainDto.getSpayeracct())
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyCorpcodeList(mainDto.getSorgcode(), mainDto
							.getStrecode(), mainDto.getSbudgetunitcode(),
							vtCode)
					&& verifyPayeeBankNo(mainDto.getSrecbankno(), vtCode,mainDto.getStrecode(),subdtos)
					&& verifyPayOutMoveFunSubject(mainDto, expFuncCodeList)) {
				/**
				 * ʵ���ʽ�5207��������ϸ8207�ȶ�У��(�Ϻ�)
				 * 
				 * @author �Ż��
				 */
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& mainDto.getSbusinesstypecode().equals(
								StateConstant.BIZTYPE_CODE_BATCH)) {
					// ҵ��Ҫ��ȫ��У��ɹ������״̬Ϊ"У����"
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_8207);
					succList.add(list);
				} else {
					// ����ƾ֤״̬Ϊ"У��ɹ�"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					//�����������������кſ���Ϊ�գ�����Ҫ��¼��ֱ�Ӹ�Ϊ���˳ɹ� 20190507
					HashMap  tremap = SrvCacheFacade.cacheTreasuryInfo(null);
					if (tremap.containsKey(vDto.getStrecode())) {
						TsTreasuryDto t = (TsTreasuryDto) tremap.get(vDto.getStrecode());
						if (StateConstant.COMMON_YES.equals(t.getSpayunitname()) ){
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("���˳ɹ�");
					        DatabaseFacade.getDb().update(vDto);
						}
					}
					if (ITFECommonConstant.PUBLICPARAM
							.indexOf(",verify=false,") >= 0) {
						if (StateConstant.CHECKSTATUS_4.equals(mainDto
								.getScheckstatus())) {
							vDto
									.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("���˳ɹ�");
							DatabaseFacade.getDb().update(vDto);
						}
					}
					count++;
					succList.add(new Integer(count));
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * ֱ��֧�����ҵ��У����
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List<TvVoucherinfoDto> verifyFor5108(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvDirectpaymsgmainDto mainDto = (TvDirectpaymsgmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// У��
			if (verifyInfoAndPay(vDto)&&verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifySfinOrgCode((ITFECommonConstant.PUBLICPARAM
							.indexOf(",sh,") < 0 ? mainDto.getSpayunit()
							: mainDto.getShold2()), mainDto.getSorgcode(),vDto.getStrecode())
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)// ��������У��
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,")?verifySpayInfo(mainDto):true)
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapaycodeverify,")?verifyPaybankNo(vDto.getSorgcode(),vDto.getStrecode(),mainDto.getSpaybankno(),mainDto.getSpaybankname()):true)
			) {
				/**
				 * 1�����=0.00 ֱ��֧�����5108��ֱ��֧������5253�ȶ�У��(�Ϻ�) 2�����<>0.00
				 * ֱ��֧�����5108��ֱ��֧��ҵ��5201�ȶ�У�飬
				 * Ȼ��ֱ��֧��5201��������ϸ8207�ȶ�У�飬�ȶԽ�����ظ���5108(�Ϻ�)
				 */
				// if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
				// && mainDto.getShold1().equals("1")
				// && (mainDto.getNmoney().compareTo(BigDecimal.ZERO) >= 0)) {
				// // 5108����Ȳ���Ҫ����5201
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& (mainDto.getNmoney().compareTo(BigDecimal.ZERO) >= 0)) { // 5108����Ȳ���Ҫ����5201
					// ҵ��Ҫ��ȫ��У��ɹ������״̬Ϊ"У����"
					vDto.setShold3(mainDto.getSpayvoucherno());
					if (vDto.getNmoney().compareTo(BigDecimal.ZERO) == 0)
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_5253);
					else
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_5201);
					succList.add(list);
				} else {
					// ����ƾ֤״̬Ϊ"У��ɹ�"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					count++;
					succList.add(new Integer(count));
				}

			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * ��Ȩ֧�����ҵ��У����
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List<TvVoucherinfoDto> verifyFor5106(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			// ��Ȩ֧���������
			IDto idto = (IDto) list.get(0);
			TvGrantpaymsgmainDto mainDto = (TvGrantpaymsgmainDto) idto;
			// ������
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// Ԥ�㵥λ�����б�
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// У��
			if (verifyInfoAndPay(vDto)&&verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifySfinOrgCode(mainDto.getSpayunit(), mainDto
							.getSorgcode(),vDto.getStrecode())
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)// ��������У��
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,")?verifySpayInfo(mainDto):true)
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapaycodeverify,")?verifyPaybankNo(vDto.getSorgcode(),vDto.getStrecode(),mainDto.getSpaybankno(),mainDto.getSpaybankname()):true)
			) {
				/**
				 * ��Ȩ֧���������Ȩ֧���������5351����Ȩ֧��ҵ��2301�ȶ�У��(�Ϻ�)
				 * 
				 * @author �Ż��
				 */
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& mainDto.getNmoney().compareTo(BigDecimal.ZERO) >= 0) {
					if (mainDto.getNmoney().compareTo(BigDecimal.ZERO) == 0) {
						vDto.setShold3(mainDto.getSclearvoucherno());// 5106����.ClearVoucherNo=5351����.VoucherNo
						// �������Ȩ����������Ϣ5351У�飬����Ȩ֧��������������״̬Ϊ"У����"
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_5351);
					} else {
						vDto.setShold3(mainDto.getSclearvoucherno());// 5106����.ClearVoucherNo=2301����.VoucherNo
						// �������Ȩ֧����Ϣ2301У�飬�������Ȩ֧�����������״̬Ϊ"У����"
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_2301);
					}
					succList.add(list);
				} else {
					// ����ƾ֤״̬Ϊ"У��ɹ�"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					count++;
					succList.add(new Integer(count));
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * ��Ȩ֧������ҵ��У����
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List<TvVoucherinfoDto> verifyFor5351(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		for (List list : (List<List>) lists) {
			IDto idto = (IDto) list.get(0);
			TfGrantpayAdjustmainDto mainDto = (TfGrantpayAdjustmainDto) idto;
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);

			// У��
			if (verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getSpaybankcode(), vtCode)// ��������У��
			) {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5106);
					succList.add(list);
				} else {
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);

			}
		}
		return succList;
	}

	/**
	 * �����˸�У����
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public int verifyFor5209(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TvDwbkDto mainDto = (TvDwbkDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);

			// У��
			if (verifyTreasury(mainDto.getSaimtrecode(), mainDto
					.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getStaxorgcode(), mainDto
							.getSbookorgcode(),vDto.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(),
							expFuncCodeList, vtCode)
					&& verifyPayeeBankNo(mainDto.getSpayeeopnbnkno(), vtCode)
					&& verifyStrelevel(mainDto)) {

				// ����ƾ֤״̬
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,") >= 0) {
					if (StateConstant.CHECKSTATUS_4.equals(mainDto
							.getScheckstatus())) {
						vDto
								.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
						vDto.setSdemo("���˳ɹ�");
						DatabaseFacade.getDb().update(vDto);
					}
				}
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);

			}

		}
		return count;
	}

	/**
	 * ������Ϣ����У����
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public int verifyFor3507(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TfReconcilePayinfoMainDto mainDto = (TfReconcilePayinfoMainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// У��
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)) {
				// ����ƾ֤״̬
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * ʵ����Ϣ����У����
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public int verifyFor3508(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TfReconcilePayinfoMainDto mainDto = (TfReconcilePayinfoMainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// У��
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())) {
				// ����ƾ֤״̬
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * У���ո�������Ϣ��֤ ���� �տ��˿������к� ���ո�������Ϣ
	 * 
	 * @param mainDto
	 * @return
	 * @throws ITFEBizException
	 */
	private boolean verifySpayInfo(IDto dto) throws ITFEBizException {
		if (dto instanceof TvPayreckBankDto) {
			TvPayreckBankDto mainDto = (TvPayreckBankDto) dto;
			// ���ڵ�һ�˻����ո����˵�У��
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // ��ȡ�ո�������Ϣ
			if (StringUtils.isBlank(mainDto.getSpayeeopbkno())) {
				this.tmpFailReason = "���Ĳ��淶, û���ҵ��տ��˿������к���Ϣ!";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode() + mainDto.getSpayeeopbkno()
								+ mainDto.getSpayeracct() + mainDto
								.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "���ݹ�����룺" + mainDto.getStrecode()
							+ ", �տ��˿������кţ�" + mainDto.getSpayeeopbkno()
							+ ", �������˺ţ�" + mainDto.getSpayeracct() + ", �տ����˺ţ�"
							+ mainDto.getSpayeeacct() + " û���ҵ��ո�������Ϣ!";
					return false;
				} else {
					if (!mainDto.getSpayeracct().equals(
							tmppayacctinfoDto.getSpayeracct())) { // �������ʻ�
						this.tmpFailReason = "�������ʻ�:" + mainDto.getSpayeracct()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayername().equals(
							tmppayacctinfoDto.getSpayername())) { // ����������
						this.tmpFailReason = "����������:" + mainDto.getSpayername()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayeeacct().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // �տ����˻�
						this.tmpFailReason = "�տ����˻�:" + mainDto.getSpayeeacct()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayeename().equals(
							tmppayacctinfoDto.getSpayeename())) { // �տ�������
						this.tmpFailReason = "�տ�������:" + mainDto.getSpayeename()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
				}
			}
		} else if (dto instanceof TvPayreckBankBackDto) {
			TvPayreckBankBackDto mainDto = (TvPayreckBankBackDto) dto;
			// ���ڵ�һ�˻����ո����˵�У��
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // ��ȡ�ո�������Ϣ
			if (StringUtils.isBlank(mainDto.getSpaysndbnkno())) {
				this.tmpFailReason = "���Ĳ��淶, û���ҵ��տ��˿������к���Ϣ";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode() + mainDto.getSpaysndbnkno()
								+ mainDto.getSpayeracct() + mainDto
								.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "���ݹ�����룺" + mainDto.getStrecode()
							+ ", �տ��˿������кţ�" + mainDto.getSpaysndbnkno()
							+ ", �������˺ţ�" + mainDto.getSpayeracct() + ", �տ����˺ţ�"
							+ mainDto.getSpayeeacct() + " û���ҵ��ո�������Ϣ!";
					return false;
				} else {
					if (!mainDto.getSpayeracct().equals(
							tmppayacctinfoDto.getSpayeracct())) { // �������ʻ�
						this.tmpFailReason = "�������ʻ�:" + mainDto.getSpayeracct()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayername().equals(
							tmppayacctinfoDto.getSpayername())) { // ����������
						this.tmpFailReason = "����������:" + mainDto.getSpayername()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayeeacct().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // �տ����˻�
						this.tmpFailReason = "�տ����˻�:" + mainDto.getSpayeeacct()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayeename().equals(
							tmppayacctinfoDto.getSpayeename())) { // �տ�������
						this.tmpFailReason = "�տ�������:" + mainDto.getSpayeename()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
				}
			}
		} else if (dto instanceof TfDirectpaymsgmainDto) {
			TfDirectpaymsgmainDto mainDto = (TfDirectpaymsgmainDto) dto;
			// ���ڵ�һ�˻����ո����˵�У��
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSorgcode()); // ��ȡ�ո�������Ϣ
			if (StringUtils.isBlank(mainDto.getSpayeeacctbankno())) {
				this.tmpFailReason = "���Ĳ��淶, û���ҵ��տ��˿������к���Ϣ";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode()
								+ mainDto.getSpayeeacctbankno()
								+ mainDto.getSpayacctno() + mainDto
								.getSpayeeacctno()));
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "���ݹ�����룺" + mainDto.getStrecode()
							+ ", �տ��˿������кţ�" + mainDto.getSpayeeacctbankno()
							+ ", �������˺ţ�" + mainDto.getSpayacctno() + ", �տ����˺ţ�"
							+ mainDto.getSpayeeacctno() + " û���ҵ��ո�������Ϣ!";
					return false;
				} else {
					if (!mainDto.getSpayacctno().equals(
							tmppayacctinfoDto.getSpayeracct())) { // �������ʻ�
						this.tmpFailReason = "�������ʻ�:" + mainDto.getSpayacctno()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayacctname().equals(
							tmppayacctinfoDto.getSpayername())) { // ����������
						this.tmpFailReason = "����������:"
								+ mainDto.getSpayacctname() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayeeacctno().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // �տ����˻�
						this.tmpFailReason = "�տ����˻�:"
								+ mainDto.getSpayeeacctno() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSpayeeacctname().equals(
							tmppayacctinfoDto.getSpayeename())) { // �տ�������
						this.tmpFailReason = "�տ�������:"
								+ mainDto.getSpayeeacctname()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
				}
			}
		}else if (dto instanceof TvGrantpaymsgmainDto) {
			TvGrantpaymsgmainDto mainDto = (TvGrantpaymsgmainDto) dto;
			if(StringUtils.isBlank(mainDto.getSpaybankno())||StringUtils.isBlank(mainDto.getShold1())||StringUtils.isBlank(mainDto.getSclearbankcode()))
				return true;
			// ���ڵ�һ�˻����ո����˵�У��
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSorgcode()); // ��ȡ�ո�������Ϣ
			if (StringUtils.isBlank(mainDto.getSpaybankno())) {
				this.tmpFailReason = "���Ĳ��淶, û���ҵ��տ��˿������к���Ϣ!";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode() + mainDto.getSpaybankno()
								+ mainDto.getSclearbankcode() + mainDto
								.getShold1()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "���ݹ�����룺" + mainDto.getStrecode()
							+ ", �տ��˿������кţ�" + mainDto.getSpaybankno()
							+ ", �������˺ţ�" + mainDto.getSclearbankcode() + ", �տ����˺ţ�"
							+ mainDto.getShold1() + " û���ҵ��ո�������Ϣ!";
					return false;
				} else {
					if (!mainDto.getSclearbankcode().equals(
							tmppayacctinfoDto.getSpayeracct())) { // �������ʻ�
						this.tmpFailReason = "�������ʻ�:" + mainDto.getSclearbankcode()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getSclearbankname().equals(
							tmppayacctinfoDto.getSpayername())) { // ����������
						this.tmpFailReason = "����������:" + mainDto.getSclearbankname()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getShold1().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // �տ����˻�
						this.tmpFailReason = "�տ����˻�:" + mainDto.getShold1()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if (!mainDto.getShold2().equals(
							tmppayacctinfoDto.getSpayeename())) { // �տ�������
						this.tmpFailReason = "�տ�������:" + mainDto.getShold2()
								+ "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * У����Ȩ����ƾ֤��ϸ�Ƿ����
	 * 
	 * @param mainIDto
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List<IDto> verifyVoucherExists(IDto mainIDto, String vtCode)
			throws JAFDatabaseException, ValidateException {
		if (vtCode.equals(MsgConstant.VOUCHER_NO_2302)) {
			TvPayreckBankBackDto mainDto = (TvPayreckBankBackDto) mainIDto;
			TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
			subdto.setIvousrlno(mainDto.getIvousrlno());
			List<IDto> PayBankBackList = CommonFacade.getODB().findRsByDto(
					subdto);
			StringBuffer sbf = new StringBuffer("");
			if (null != PayBankBackList && PayBankBackList.size() > 0) {
				TvPayreckBankBackListDto backListDto = null;
				String sql = "SELECT * FROM TV_PAYRECK_BANK_LIST a WHERE I_VOUSRLNO =(SELECT I_VOUSRLNO FROM TV_PAYRECK_BANK WHERE S_BOOKORGCODE = ?  AND S_TRECODE = ? AND S_VOUNO =  ? AND S_RESULT = ?) AND S_ID = ? ";
				SQLExecutor sqlExecutor = DatabaseFacade.getODB()
						.getSqlExecutorFactory().getSQLExecutor();
				SQLResults sqlResults = null;
				TvPayreckBankListDto tmpSubDto = null;
				List updateSubList = new ArrayList();
				for (IDto dto : PayBankBackList) {
					backListDto = (TvPayreckBankBackListDto) dto;
					sqlExecutor.addParam(mainDto.getSbookorgcode());
					sqlExecutor.addParam(mainDto.getStrecode());
					sqlExecutor.addParam(backListDto.getSorivouno());
					sqlExecutor.addParam("80000");
					sqlExecutor.addParam(backListDto.getSorivoudetailno());
					// TvPayreckBankDto PayreckBankDto = new TvPayreckBankDto();
					// PayreckBankDto.setSbookorgcode(mainDto.getSbookorgcode());
					// PayreckBankDto.setStrecode(mainDto.getStrecode());
					// PayreckBankDto.setSvouno(backListDto.getSorivouno());
					// List<TvPayreckBankDto> PayreckBankDtoList = CommonFacade
					// .getODB().findRsByDto(PayreckBankDto);
					sqlResults = sqlExecutor.runQuery(sql,
							TvPayreckBankListDto.class);
					if (null == sqlResults || sqlResults.getRowCount() == 0) {
						this.tmpFailReason = "���ݻ��������˿��е�ԭƾ֤����["
								+ backListDto.getSorivouno() + "]δ���ҵ�ԭ��������ƾ֤!";
						return null;
					}
					tmpSubDto = (TvPayreckBankListDto) ((List) sqlResults
							.getDtoCollection()).get(0);
					if (StringUtils.isNotBlank(tmpSubDto.getShold1())) {
						this.tmpFailReason = "ƾ֤���Ϊ��" + mainDto.getSvouno() +  "��,��ϸID��" + backListDto.getSid() + "�������ظ��˿";
						return null;
					}
					if (backListDto.getFamt().compareTo(tmpSubDto.getFamt()) != 0) {
						this.tmpFailReason = "ƾ֤���Ϊ��" + mainDto.getSvouno() +  "��,��ϸID��" + backListDto.getSid() + "�˿��" + backListDto.getFamt() +"��ԭ����ƾ֤���:" + tmpSubDto.getFamt() + "������";
						return null;
					}
					tmpSubDto.setShold1("1"); // ����ñ���ϸ�Ѿ��˿�������ظ���
					updateSubList.add(tmpSubDto);
					sqlExecutor.clearParams();
				}
				DatabaseFacade.getODB().update(CommonUtil.listTArray(updateSubList));
				return PayBankBackList;
			}
		}
		return null;

	}

	/**
	 * �ṩ���������������֤
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyTreasury(String trecode, String orgcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade
				.cacheTreasuryInfo(orgcode);
		HashMap<String, TsTreasuryDto> newMap = new HashMap<String, TsTreasuryDto>();

		Set<Map.Entry<String, TsTreasuryDto>> set = map.entrySet();
		if(orgcode!=null&&!orgcode.equals("")){
			for (Iterator<Map.Entry<String, TsTreasuryDto>> it = set.iterator(); it.hasNext();) {
				Map.Entry<String, TsTreasuryDto> entry = (Map.Entry<String, TsTreasuryDto>) it.next();
				newMap.put(entry.getKey() + entry.getValue().getSorgcode(), entry.getValue());
			}
		}
		if (newMap != null && newMap.containsKey(trecode + orgcode)) {
			return true;
		} else {
			this.tmpFailReason = "�����������[" + trecode + "]�ڹ���������Ϣ�����в�����!";
			return false;
		}
	}

	/**
	 * У�����Ԥ�㼶��
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyStrelevel(TvDwbkDto dto) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		if (ITFECommonConstant.SRC_NODE.equals("000057400006")||ITFECommonConstant.SRC_NODE.equals("201057100006")) {// ���Ϊ��������ҪУ��
			return true;
		}
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade
				.cacheTreasuryInfo(dto.getSbookorgcode());
		TsTreasuryDto tDto = map.get(dto.getSaimtrecode());
		if (tDto.getStrelevel() == null || tDto.getStrelevel().equals("")) {
			this.tmpFailReason = "����[" + tDto.getStrecode() + "]Ԥ�㼶��δά����";
			return false;
		}
		dto.setCbdglevel(tDto.getStrelevel());
		DatabaseFacade.getODB().update(dto);
		return true;

	}

	/**
	 * У�鹦�ܿ�Ŀ����
	 * 
	 * @param orgcode
	 *            ��������
	 * @param funccode
	 *            ���ܿ�Ŀ�б�
	 * @param vtCode
	 *            ҵ������
	 * 
	 * @return ture/false
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public boolean verifySubject(String orgcode, List<String> funccodeList,
			String vtcode) throws ITFEBizException, JAFDatabaseException {
		Map<String, TsBudgetsubjectDto> smap = SrvCacheFacade
				.cacheTsBdgsbtInfo(orgcode);
		for (String funccode : funccodeList) {
			TsBudgetsubjectDto dto = smap.get(funccode);
			if (null == funccode || "".equals(funccode)) {
				this.tmpFailReason = "ITFE_001��ϸ��Ϣ�д��ڹ��ܿ�Ŀ����Ϊ�յļ�¼!";
				return false;
			}
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				this.tmpFailReason = "ITFE_001���ܿ�Ŀ����[" + funccode + "]������!";
				return false;
			} else {
				if(vtcode.equals(MsgConstant.VOUCHER_NO_5407))
					return true;
				if (vtcode.equals(MsgConstant.VOUCHER_NO_5209) || vtcode.equals(MsgConstant.VOUCHER_NO_5671)|| vtcode.equals(MsgConstant.VOUCHER_NO_5408)) {
					if (!"1".equals(dto.getSsubjectclass())) {
						this.tmpFailReason = "ITFE_001���ܿ�Ŀ����[" + funccode + "]���������Ŀ!";
						return false;
					}
				} else {
					if (!"2".equals(dto.getSsubjectclass())) {
						this.tmpFailReason = "ITFE_001���ܿ�Ŀ����[" + funccode
								+ "]����֧�����ܿ�Ŀ!";
						return false;
					}
				}
				if (!"1".equals(dto.getSwriteflag())) {
					this.tmpFailReason = "ITFE_001���ܿ�Ŀ����[" + funccode + "]��¼���־Ϊ����¼��!";
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * У��ʵ���ʽ��Ŀ�����Ƿ��ǵ�����Ŀ
	 * 
	 * @param funccode
	 *            ���ܿ�Ŀ�б�
	 * @param maindto
	 *            ʵ���ʽ�����dto
	 * 
	 * @return ture/false
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public boolean verifyPayOutMoveFunSubject(TvPayoutmsgmainDto mainDto,
			List<String> funccodeList) throws ITFEBizException,
			JAFDatabaseException {
		Map<String, TsBudgetsubjectDto> smap = SrvCacheFacade
				.cacheTsBdgsbtInfo(mainDto.getSorgcode());
		String ischeck = ITFECommonConstant.CHECKPAYOUTSUBJECT;
		if (ischeck != null
				&& ischeck.equals(MsgConstant.VOUCHER_CHECKPAYOUTSUBJECT)
				&& mainDto != null && mainDto.getSrecbankno().startsWith("011")) {
			for (String funccode : funccodeList) {
				TsBudgetsubjectDto dto = smap.get(funccode);
				if (null == funccode || "".equals(funccode)) {
					this.tmpFailReason = "ITFE_001��ϸ��Ϣ�д��ڹ��ܿ�Ŀ����Ϊ�յļ�¼!";
					return false;
				}
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					this.tmpFailReason = "ITFE_001���ܿ�Ŀ����[" + funccode + "]������!";
					return false;
				} else {
					if (MsgConstant.MOVE_FUND_SIGN_NO
							.equals(dto.getSmoveflag())) {
						this.tmpFailReason = "ITFE_001���ܿ�Ŀ����[" + funccode + "]Ϊ�ǵ���!";
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * ����Ԥ�㵥λ�����б�У��
	 * 
	 * @param orgcode
	 *            ��������
	 * @param tdCorpList
	 *            Ԥ�㵥λ�����б�
	 * @param vtCode
	 *            ҵ������
	 * 
	 * @return true/false
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyCorpcodeList(String orgcode, String trecode,
			String tdCorp, String vtCode) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		Map<String, TdCorpDto> map = SrvCacheFacade.cacheTdCorpInfo(orgcode);
		if (map == null) {
			this.tmpFailReason = "����[" + orgcode + "]û��ά��Ԥ�㵥λ�������!";
			return false;
		}
		if ("".equals(tdCorp) || tdCorp == null) {
			this.tmpFailReason = "Ԥ�㵥λ����Ϊ��!";
			return false;
		}
		if (vtCode.equals(MsgConstant.VOUCHER_NO_5207)) {
			TdCorpDto cDto = map.get(trecode + tdCorp);
			if (cDto == null || !cDto.getScorpcode().equals(tdCorp)) {
				this.tmpFailReason = "Ԥ�㵥λ����[" + tdCorp + "]��Ԥ�㵥λ��������в�����!";
				return false;
			} else {
				if (!"1".equals(cDto.getCmayaprtfund().trim())) {
					this.tmpFailReason = "Ԥ�㵥λ����[" + tdCorp + "]���ܽ���ʵ���ʽ�!";
					return false;
				}
			}

		} else {
			if (!map.containsKey(trecode + tdCorp)) {
				this.tmpFailReason = "Ԥ�㵥λ����[" + tdCorp + "]��Ԥ�㵥λ��������в�����!";
				return false;
			}

		}
		return true;
	}

	/**
	 * У�鸶�����˺�
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */

	public boolean verifyAccName(String bookorgCode, String strecode,
			String spayeraccount) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		Map<String, TsInfoconnorgaccDto> map = SrvCacheFacade
				.cacheFinTreAcctInfo();
		if (map.get(bookorgCode + spayeraccount) == null) {
			this.tmpFailReason = "�������˺�[" + spayeraccount
					+ "]û����'��������˻�����ά����ά��!";
			return false;
		}
		return true;
	}

	/**
	 * У���տ��˿�����
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyPayeeBankNo(String PayeeAcctBankNo, String vtcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {
			if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {// ����û����д�кţ�������У��
				return true;
			}
		} else {
			if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {
				this.tmpFailReason = "�տ����д���Ϊ��";
				return false;
			}
		}
		HashMap<String, TsPaybankDto> bankmap = SrvCacheFacade
				.cachePayBankInfo();
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
				&& PayeeAcctBankNo.trim().length() == 10) { // �Ϻ����Ϊ10������벻����У��
			return true;
		} else if (bankmap.get(PayeeAcctBankNo) == null) {
			this.tmpFailReason = "�տ��к�[" + PayeeAcctBankNo + "]��֧��ϵͳ�к��в�����!";
			return false;
		}
		return true;
	}
	/**
	 * У���տ��˿�����
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyPayeeBankNo(String PayeeAcctBankNo, String vtcode,String strecode,List<TvPayoutmsgsubDto> subdtos)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		HashMap<String, TsPaybankDto> bankmap = SrvCacheFacade
				.cachePayBankInfo();
		if (strecode != null && !"".equals(strecode) && subdtos != null
				&& subdtos.size() > 0) {
			TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(strecode);
			if (StateConstant.COMMON_YES.equals(String
					.valueOf(tredto == null ? "" : tredto.getSpayunitname()))) {
				for (int i = 0; i < subdtos.size(); i++) {
					if (bankmap.get(String.valueOf(subdtos.get(i)
							.getSpayeebankno())) == null) {
						this.tmpFailReason = "�տ��к�["
								+ String.valueOf(subdtos.get(i)
										.getSpayeebankno()) + "]��֧��ϵͳ�к��в�����!";
						return false;
					}
				}
			} else {
				if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
						|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
						|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {
					if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {// ����û����д�кţ�������У��
						return true;
					} else {
						TsPaybankDto paybankdto = bankmap.get(PayeeAcctBankNo);
						if (paybankdto == null) {
							this.tmpFailReason = PayeeAcctBankNo + " ֧��ϵͳ�޴��к�";
							return false;
						}
					}
				} else {
					if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {
						this.tmpFailReason = "�տ����д���Ϊ��";
						return false;
					}
				}
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& PayeeAcctBankNo.trim().length() == 10) { // �Ϻ����Ϊ10������벻����У��
					return true;
				} else if (bankmap.get(PayeeAcctBankNo) == null) {
					this.tmpFailReason = "�տ��к�[" + PayeeAcctBankNo
							+ "]��֧��ϵͳ�к��в�����!";
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * У�������������
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifySfinOrgCode(String SfinOrgCode, String sbookorgcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		if ("".equals(SfinOrgCode) || SfinOrgCode == null) {
			this.tmpFailReason = "���Ĳ��淶��������������Ϊ��";
			return false;
		}
		HashMap<String, TsConvertfinorgDto> bankmap = SrvCacheFacade
				.cacheFincInfoByFinc(sbookorgcode);
		if (bankmap.get(SfinOrgCode) == null) {
			this.tmpFailReason = "��������[" + SfinOrgCode + "]�ڲ��������в�����!";
			return false;
		}
		return true;
	}
	/**
	 * У�������������
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifySfinOrgCode(String SfinOrgCode, String sbookorgcode,String trecode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		if ("".equals(SfinOrgCode) || SfinOrgCode == null) {
			this.tmpFailReason = "���Ĳ��淶��������������Ϊ��";
			return false;
		}
		HashMap<String, TsConvertfinorgDto> bankmap = SrvCacheFacade
				.cacheFincInfoByFinc(sbookorgcode);
		if (bankmap.get(SfinOrgCode) == null) {
			this.tmpFailReason = "��������[" + SfinOrgCode + "]�ڲ��������в�����!";
			return false;
		}else
		{
			TsConvertfinorgDto temp = bankmap.get(SfinOrgCode);
			if(!temp.getStrecode().equals(trecode))
			{
				this.tmpFailReason = "��������[" + SfinOrgCode + "]����ά���Ĺ������"+temp.getStrecode()+"��ƾ֤�еĹ�����벻һ��!";
				return false;
			}
		}
		return true;
	}
	public String getTmpFailReason() {
		return tmpFailReason;
	}

	public void setTmpFailReason(String tmpFailReason) {
		this.tmpFailReason = tmpFailReason;
	}

	/**
	 * ���з��˴���У��
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyCorpcodeList(String orgcode, List<String> tdCorpList)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		Map<String, TdCorpDto> map = SrvCacheFacade.cacheTdCorpInfo(orgcode);
		if (map != null) {
			for (String tdCorp : tdCorpList) {
				if (!map.containsKey(tdCorp)) {
					// ȥ��Ԥ�㵥λУ���еĹ������
					tdCorp = tdCorp.substring(10);
					this.tmpFailReason = "Ԥ�㵥λ����[" + tdCorp + "]��Ԥ�㵥λ��������в�����";
					return false;
				}
			}
		} else {
			this.tmpFailReason = "����[" + orgcode + "]�ڷ��˴�������в�����";
			return false;
		}
		return true;
	}

	/**
	 * ������������У�飨����֧�������ã�
	 */
	public boolean verifyPaybankName(String orgcode, String treCode,
			String bankno, String bankname) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		if ("".equals(bankname) || bankname == null) {
			this.tmpFailReason = "�����������Ʋ���Ϊ�ա�";
			return false;
		}
		Map<String, TsConvertbanktypeDto> map = SrvCacheFacade
				.cacheTsconvertBankType(orgcode);
		if (map != null && !map.isEmpty()) {
			if (map.containsKey(treCode + bankno)) {
				String inputBankname = map.get(treCode + bankno).getSbankname();
				if (!bankname.equals(inputBankname)) {
					this.tmpFailReason = "������������[" + bankname
							+ "]�롾����������Ϣά���������в�һ�¡�";
					return false;
				}
			} else {
				this.tmpFailReason = "�������д���û���ڡ�����������Ϣά������ά����";
				return false;
			}
		} else {
			this.tmpFailReason = "������������Ϣά����û��ά����";
			return false;
		}
		return true;
	}
	/**
	 * ������������У�飨����֧�������ã�
	 */
	public boolean verifyPaybankNo(String orgcode, String treCode,
			String bankno, String bankname) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		if ("".equals(bankname) || bankname == null) {
			this.tmpFailReason = "�����������Ʋ���Ϊ�ա�";
			return false;
		}
		Map<String, TsConvertbanktypeDto> map = SrvCacheFacade
				.cacheTsconvertBankType(orgcode);
		if (map != null && !map.isEmpty()) {
			if (!map.containsKey(treCode + bankno)) {
				this.tmpFailReason = "�������д���û���ڡ�����������Ϣά������ά����";
				return false;
			}
		} else {
			this.tmpFailReason = "������������Ϣά����û��ά����";
			return false;
		}
		return true;
	}
	/**
	 * У��ԭƾ֤�Ƿ����
	 * 
	 * @author �Ż��
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public boolean verifyOriVoucher(TfPaybankRefundmainDto mainDto,
			List<TfPaybankRefundsubDto> subList) throws ITFEBizException,
			JAFDatabaseException {
		String svtcode = "";
		// ����֧����ʽ����ȷ������ҵ�� 11-ֱ��֧�� 12-��Ȩ֧�� 91-ʵ���ʽ�
		if (mainDto.getSpaytypecode().equals(StateConstant.PAYOUT_PAY_CODE)) {
			svtcode = MsgConstant.VOUCHER_NO_5207;
		} else if (mainDto.getSpaytypecode().equals(MsgConstant.directPay)) {
			svtcode = MsgConstant.VOUCHER_NO_5201;
		}
		List list = VoucherUtil.findVoucherDto(mainDto.getStrecode(), svtcode,
				mainDto.getSoriginalvoucherno(),
				DealCodeConstants.VOUCHER_SUCCESS);
		if (list == null || list.size() == 0) {
			this.tmpFailReason = "����ƾ֤��ţ�" + mainDto.getSoriginalvoucherno()
					+ "δ�ҵ�����Ϊ" + svtcode + "����ƾ֤";
			return false;
		}
		TvVoucherinfoDto dto = (TvVoucherinfoDto) list.get(0);
		if (mainDto.getNpayamt().compareTo(dto.getNmoney()) > 0) {
			this.tmpFailReason = MsgConstant.VOUCHER_NO_2252 + "�˿���[-"
					+ mainDto.getNpayamt() + "]�ľ���ֵ���ܴ���" + svtcode + "ƾ֤���["
					+ dto.getNmoney() + "]��";
			return false;
		} else if (mainDto.getNpayamt().compareTo(dto.getNmoney()) == 0)
			mainDto.setSrefundtype("2");
		else
			mainDto.setSrefundtype("1");
		try {
			DatabaseFacade.getODB().update(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����ҵ�������˿������쳣!", e);
		}
		// ��У��2252��ϸId�Ƿ������ֱ��֧��5201��Ӧ����ϸId
		List updateSubList = new ArrayList();
		if (svtcode.equals(MsgConstant.VOUCHER_NO_5201)) {
			TfDirectpaymsgmainDto maindto5201 = (TfDirectpaymsgmainDto) VoucherUtil
					.findMainDtoByVoucher(dto).get(0);
			HashMap subdtoMap = VoucherUtil.convertListToMap(PublicSearchFacade
					.findSubDtoByMain(maindto5201));
			TfDirectpaymsgsubDto tempsudto = null;
			for (TfPaybankRefundsubDto subdto : subList) {
				tempsudto = (TfDirectpaymsgsubDto) subdtoMap.get(subdto
						.getSid());
				if (null == tempsudto) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "��ϸId��"
							+ subdto.getSid() + "��" + svtcode + "��ϸ�в����ڡ�";
					break;
				} else if (StringUtils.isNotBlank(tempsudto.getSext3())) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "��ϸId��"
							+ subdto.getSid() + "�������ظ��˿�";
					break;
				} else if (subdto.getNpayamt()
						.compareTo(tempsudto.getNpayamt()) != 0) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "��ϸId��"
							+ subdto.getSid() + "�˿���[" + subdto.getNpayamt()
							+ "]��ԭ������[" + tempsudto.getNpayamt() + "]����";
					break;
				}
				tempsudto.setSext3("1"); // �����Ѿ������˿�ļ�¼
				updateSubList.add(tempsudto);
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = (TvPayoutmsgmainDto) VoucherUtil
					.findMainDtoByVoucher(dto).get(0);
			HashMap subdtoMap = VoucherUtil.convertListToMap(PublicSearchFacade
					.findSubDtoByMain(tvPayoutmsgmainDto));
			TvPayoutmsgsubDto tmpSubDto = null;
			for (TfPaybankRefundsubDto subdto : subList) {
				tmpSubDto = (TvPayoutmsgsubDto) subdtoMap.get(subdto.getSid());
				if (null == tmpSubDto) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "��ϸId��"
							+ subdto.getSid() + "��" + svtcode + "��ϸ�в����ڡ�";
					break;
				} else if (StringUtils.isNotBlank(tmpSubDto.getShold4())) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "��ϸId��"
							+ subdto.getSid() + "�������ظ��˿�";
					break;
				} else if (subdto.getNpayamt().compareTo(tmpSubDto.getNmoney()) != 0) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "��ϸId��"
							+ subdto.getSid() + "�˿���[" + subdto.getNpayamt()
							+ "]��ԭ������[" + tmpSubDto.getNmoney() + "]����";
					break;

				}
				tmpSubDto.setShold4("1");
				updateSubList.add(tmpSubDto);
			}
		}
		if (StringUtils.isNotBlank(tmpFailReason)) {
			return false;
		}
		DatabaseFacade.getODB().update(CommonUtil.listTArray(updateSubList));
		return true;
	}

	/**
	 * У���Ϻ���ֽ��ҵ��ؼ��ֶΣ�ֻ���и�ʽУ�飩
	 * 
	 * @author �Ż��
	 * @param allList
	 * @return String
	 */
	public String checkVerify(List<IDto> subDtoList, String vtcode) {
		StringBuffer sb = new StringBuffer("");
		if (vtcode.equals(MsgConstant.VOUCHER_NO_5207)) {
			for (IDto dto : subDtoList) {
				sb.append(checkValidDetailFor5207(dto));
			}
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_2252)) {
			for (IDto dto : subDtoList) {
				sb.append(checkValidDetailFor2252(dto));
			}
		}
		return sb.toString();
	}

	/**
	 * У����ϸID�Ƿ���ԭ֧����ϸ��
	 * 
	 * @param subDtoList
	 * @param idto
	 * @return
	 */
	public String checkVerify(List<IDto> subDtoList, IDto idto) {
		StringBuffer sb = new StringBuffer("");
		try {
			if (idto instanceof TfPaybankRefundmainDto) {
				TfPaybankRefundmainDto tmpDto = (TfPaybankRefundmainDto) idto;
				TvVoucherinfoDto tmpInfo = new TvVoucherinfoDto();
				tmpInfo.setSvoucherno(tmpDto.getSoriginalvoucherno());
				tmpInfo.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
				List searchInfosList = CommonFacade.getODB().findRsByDto(
						tmpInfo);
				if (null == searchInfosList || searchInfosList.size() == 0) {
					return "����ƾ֤���" + tmpDto.getSoriginalvoucherno()
							+ "û�в�ѯ����Ӧ��֧��������Ϣ��";
				}
				tmpInfo = (TvVoucherinfoDto) searchInfosList.get(0);
				List<IDto> list = null;
				// У��ʵ���ʽ��˿���ϸID
				if (StateConstant.PAYOUT_PAY_CODE.equals(tmpDto
						.getSpaytypecode().trim())) {
					TvPayoutmsgmainDto tmpMaindto = new TvPayoutmsgmainDto();
					tmpMaindto.setSbizno(tmpInfo.getSdealno());
					list = PublicSearchFacade.findSubDtoByMain(tmpMaindto);
				} else if (MsgConstant.directPay.equals(tmpDto
						.getSpaytypecode().trim())) { // У��ֱ��֧����ϸID
					TfDirectpaymsgmainDto tmpMainDto = new TfDirectpaymsgmainDto();
					tmpMainDto.setIvousrlno(Long.valueOf(tmpInfo.getSdealno()));
					list = PublicSearchFacade.findSubDtoByMain(tmpMainDto);
				}
				if (null == list || list.size() == 0) {
					sb.append("��ѯ��ϸ����Ϣʧ�ܣ�");
					return sb.toString();
				} else {
					Map<String, IDto> map = VoucherUtil.convertListToMap(list);
					for (IDto dto : subDtoList) {
						if (null == map.get(((TfPaybankRefundsubDto) dto)
								.getSid())) {
							sb.append("��ϸID��"
									+ ((TfPaybankRefundsubDto) dto).getSid()
									+ "��֧�����в����ڣ�");
							return sb.toString();
						}
					}
				}
			}
		} catch (ITFEBizException e) {
			logger.error(e);
			sb.append("��ѯ��ϸ����Ϣʧ�ܣ�");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			sb.append("��ѯ��ϸ����Ϣʧ�ܣ�");
		} catch (ValidateException e) {
			logger.error(e);
			sb.append("��ѯ��ϸ����Ϣʧ�ܣ�");
		}
		return sb.toString();
	}

	/**
	 * У���Ϻ���ֽ��ҵ��ؼ��ֶΣ�ʵ���ʽ�5207��
	 * 
	 * @author �Ż��
	 * @param list
	 * @return String
	 */
	public String checkValidDetailFor5207(IDto dto) {
		// ���ڴ洢У����Ϣ
		StringBuffer sb = new StringBuffer("");
		Matcher match = null;

		TvPayoutmsgsubDto subDto = (TvPayoutmsgsubDto) dto;

		// �տ������˺�У��
		Pattern subPayeeAcctNoPattern = Pattern.compile("[0-9]{1,42}");// ƥ��С��32λ����
		if (StringUtils.isBlank(subDto.getSpayeeacctno())) {
			sb.append("��ϸ�е��տ��˺Ų���Ϊ�ա�");
		} else {
			match = subPayeeAcctNoPattern.matcher(subDto.getSpayeeacctno());
			if (match.matches() == false) {
				sb.append("��ϸ�е��տ��˺�[" + subDto.getSpayeeacctno()
						+ "]��ʽ���󣬱���ΪС��42λ���֡�");
			}
		}

		// �տ������˻�����
		if (StringUtils.isBlank(subDto.getSpayeeacctname())) {
			sb.append("��ϸ�е��տ��˺����Ʋ���Ϊ�ա�");
		} else if (subDto.getSpayeeacctname().getBytes().length > 120) {
			sb.append("��ϸ�е��տ��˺�����[" + subDto.getSpayeeacctname()
					+ "]��ʽ���󣬱���ΪС��120���ַ���60�����֣���");
		}

		// �տ���������
		if (StringUtils.isBlank(subDto.getSpayeeacctbankname())) {
			sb.append("��ϸ�е��տ��������Ʋ���Ϊ�ա�");
		} else if (subDto.getSpayeeacctbankname().getBytes().length > 60) {
			sb.append("��ϸ�е��տ���������[" + subDto.getSpayeeacctbankname()
					+ "]��ʽ���󣬱���ΪС��60���ַ���30�����֣���");
		}

		// ��;����
		if (StringUtils.isBlank(subDto.getSpaysummaryname())) {
			sb.append("��ϸ�е���;���Ʋ���Ϊ�ա�");
		} else if (subDto.getSpaysummaryname().getBytes().length > 200) {
			sb.append("��ϸ�е���;����[" + subDto.getSpaysummaryname()
					+ "]��ʽ���󣬱���ΪС��200���ַ���100�����֣���");
		}

		return sb.toString();
	}

	/**
	 * У���Ϻ���ֽ��ҵ��ؼ��ֶΣ��տ������˿�֪ͨƾ֤2252��
	 * 
	 * @author �Ż��
	 * @param list
	 * @return String
	 */
	public String checkValidDetailFor2252(IDto dto) {
		// ���ڴ洢У����Ϣ
		StringBuffer sb = new StringBuffer("");
		Matcher match = null;
		TfPaybankRefundsubDto subDto = (TfPaybankRefundsubDto) dto;

		// ԭ�������˺�У��
		Pattern subPayAcctNoPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// ƥ��С��42λ����
		if (StringUtils.isBlank(subDto.getSpayacctno())) {
			sb.append("��ϸ�е�ԭ�������˺Ų���Ϊ�ա�");
		} else {
			match = subPayAcctNoPattern.matcher(subDto.getSpayacctno());
			if (match.matches() == false) {
				sb.append("��ϸ�е�ԭ�������˺�[" + subDto.getSpayacctno()
						+ "]��ʽ���󣬱���ΪС��42λ���֡�");
			}
		}

		// ԭ�������˻�����
		if (StringUtils.isBlank(subDto.getSpayacctname())) {
			sb.append("��ϸ�е�ԭ�������˻����Ʋ���Ϊ�ա�");
		} else if (subDto.getSpayacctname().getBytes().length > 120) {
			sb.append("��ϸ�е�ԭ�������˻�����[" + subDto.getSpayacctname()
					+ "]��ʽ���󣬱���ΪС��120���ַ���60�����֣���");
		}

		// ԭ��������������
		if (StringUtils.isBlank(subDto.getSpayacctbankname())) {
			sb.append("��ϸ�е�ԭ�������������Ʋ���Ϊ�ա�");
		} else if (subDto.getSpayacctbankname().getBytes().length > 60) {
			sb.append("��ϸ�е�ԭ��������������[" + subDto.getSpayacctbankname()
					+ "]��ʽ���󣬱���ΪС��60���ַ���30�����֣���");
		}

		// �տ������˺�У��
		Pattern subPayeeAcctNoPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// ƥ��С��42λ����
		if (StringUtils.isBlank(subDto.getSpayeeacctno())) {
			sb.append("��ϸ�е�ԭ�տ��˺Ų���Ϊ�ա�");
		} else {
			match = subPayeeAcctNoPattern.matcher(subDto.getSpayeeacctno());
			if (match.matches() == false) {
				sb.append("��ϸ�е�ԭ�տ��˺�[" + subDto.getSpayeeacctno()
						+ "]��ʽ���󣬱���ΪС��42λ���֡�");
			}
		}

		// �տ������˻�����
		if (StringUtils.isBlank(subDto.getSpayeeacctname())) {
			sb.append("��ϸ�е�ԭ�տ��˺����Ʋ���Ϊ�ա�");
		} else if (subDto.getSpayeeacctname().getBytes().length > 120) {
			sb.append("��ϸ�е�ԭ�տ��˺�����[" + subDto.getSpayeeacctname()
					+ "]��ʽ���󣬱���ΪС��120���ַ���60�����֣���");
		}

		// �տ���������
		if (StringUtils.isBlank(subDto.getSpayeeacctbankname())) {
			sb.append("��ϸ�е�ԭ�տ��������Ʋ���Ϊ�ա�");
		} else if (subDto.getSpayeeacctbankname().getBytes().length > 60) {
			sb.append("��ϸ�е�ԭ�տ���������[" + subDto.getSpayeeacctbankname()
					+ "]��ʽ���󣬱���ΪС��60���ַ���30�����֣���");
		}

		Pattern bankcodePattern = Pattern.compile("[0-9]{12}");// ƥ��12λ����
		// ԭ�տ��������к�
		if (StringUtils.isNotBlank(subDto.getSpayeeacctbankno())
				&& subDto.getSpayeeacctbankno().getBytes().length > 200) {
			match = bankcodePattern.matcher(subDto.getSpayeeacctbankno());
			if (match.matches() == false) {
				sb.append("��ϸ�е�ԭ�տ��������к�[" + subDto.getSpayeeacctbankno()
						+ "]��ʽ���󣬱���Ϊ12λ�����֡�");
			}
		}
		return sb.toString();
	}

	/**
	 * У�鱨�Ĺؼ��ֶι����ֻࣨ���и�ʽУ�飩
	 * 
	 * @param verifyDto
	 * @param vtcode
	 * @return
	 */
	public String checkValid(VoucherVerifyDto dto, String vtcode) {
		Pattern trecodePattern = Pattern.compile("[0-9]{10}");// ƥ��10λ����
		Pattern finorgcodePattern = Pattern.compile("[0-9]{1,12}");// ƥ��С��12λ����
		Pattern bankcodePattern = Pattern.compile("[0-9]{12}");// ƥ��12λ����
		Pattern yearPattern = Pattern.compile("[0-9]{4}");// ƥ��4λ����
		Pattern monthPattern = Pattern.compile("[0-9]{2}");// ƥ��2λ����
		Matcher match = null;
		StringBuffer sb = new StringBuffer();
		if (vtcode.equalsIgnoreCase(MsgConstant.VOUCHER_NO_5407)) {
			return null;
		}
		// �������
		if (StringUtils.isBlank(dto.getTrecode())) {
			sb.append("������벻��Ϊ�ա�");
		} else {
			match = trecodePattern.matcher(dto.getTrecode());
			if (match.matches() == false) {
				sb.append("�������[" + dto.getTrecode() + "]��ʽ���󣬱���Ϊ10λ���֡�");
			}
		}
		// ������������
		if (StringUtils.isBlank(dto.getFinorgcode())) {
			sb.append("�����������벻��Ϊ�ա�");
		} else {
			match = finorgcodePattern.matcher(dto.getFinorgcode());
			if (match.matches() == false) {
				sb.append("������������[" + dto.getFinorgcode()
						+ "]��ʽ���󣬱���ΪС��12λ�����֡�");
			}
		}

		// �������
		if (StringUtils.isBlank(dto.getOfyear())) {
			sb.append("������Ȳ���Ϊ�ա�");
		} else {
			match = yearPattern.matcher(dto.getOfyear().trim());
			if (match.matches() == false) {
				sb.append("�������[" + dto.getOfyear() + "]��ʽ���󣬱���Ϊ4λ���֡�");
			}
			String currentyear = TimeFacade.getCurrentStringTime().substring(0,
					4);
			if (Integer.valueOf(dto.getOfyear().trim()) > Integer
					.valueOf(currentyear)) {
				sb.append("�������[" + dto.getOfyear() + "]���ܳ�����ǰ��ȡ�");
			}
		}
		// �����·�
		if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)) {
			if (StringUtils.isBlank(dto.getOfmonth())) {
				sb.append("�����·ݲ���Ϊ�ա�");
			} else {
				match = monthPattern.matcher(dto.getOfmonth().trim());
				if (match.matches() == false) {
					sb.append("�����·�[" + dto.getOfmonth() + "]��ʽ���󣬱���Ϊ2λ���֡�");
				}
				if (Integer.valueOf(dto.getOfmonth()) > 12
						|| Integer.valueOf(dto.getOfmonth()) < 1) {
					sb.append("�����·�[" + dto.getOfmonth() + "]��ʽ���󣺲���С��1����12��");
				}
			}
		}

		// ������
		if (!MsgConstant.VOUCHER_NO_5951.equals(vtcode)
				&& !isCurrency(dto.getFamt())) {
			sb.append("֧�����[" + dto.getFamt()
					+ "]��ʽ������λ��ѡ����λΪԪ�����������15λ��С�����̶ֹ���λ�����ܰ������ŵȷָ�����");
		} else {
			if (MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2252.equals(vtcode)) {// �˿�������Ϊ��
				if (Double.valueOf(dto.getFamt()) >= 0) {
					sb.append("֧�����[" + dto.getFamt() + "]��ʽ������Ϊ������");
				}
			}
			// else if ((MsgConstant.VOUCHER_NO_5108.equals(vtcode) ||
			// MsgConstant.VOUCHER_NO_5106
			// .equals(vtcode))
			// && ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
			// if (Double.valueOf(dto.getFamt()) < 0) {
			// sb.append("֧�����[" + dto.getFamt() + "]��ʽ������Ϊ������");
			// }
			// }
			else if (MsgConstant.VOUCHER_NO_5351.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode)) {// ��Ȩ֧��������Ⱥ�ֱ��֧���������ֻ��Ϊ��
				if (!dto.getFamt().equals("0.00")) {
					sb.append("֧�����[" + dto.getFamt() + "]��ʽ������Ϊ�㡣");
				}
			} else if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)) {
				if (Double.valueOf(dto.getFamt()) <= 0) {
					sb.append("֧�����[" + dto.getFamt() + "]��ʽ������Ϊ������");
				}
			} else if ((MsgConstant.VOUCHER_NO_5108.equals(vtcode) || MsgConstant.VOUCHER_NO_5106
					.equals(vtcode))
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0) {
				if (Double.valueOf(dto.getFamt()) == 0)
					sb.append("֧�����[" + dto.getFamt() + "]��ʽ����Ƚ�����0��");
			}
		}

		// ֧����ʽ����
		if (StringUtils.isBlank(dto.getPaytypecode())) {
			// 2301��2302��5201��8207��5253��5351��2252��5207��У��֧����ʽ����
			if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2252.equals(vtcode))
				sb.append("֧����ʽ���벻��Ϊ�ա�");
		} else {
			Pattern payTypePattern = Pattern.compile("[0-9]{2,6}");// ƥ��2-6λ����
			match = payTypePattern.matcher(dto.getPaytypecode());
			if (match.matches() == false) {
				sb.append("֧����ʽ�����ʽ���󣬱���Ϊ2λ��6λ�����֡�");
			} else {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					if ((MsgConstant.VOUCHER_NO_2301.equals(vtcode) || MsgConstant.VOUCHER_NO_2302
							.equals(vtcode))
							&& !"12".equals(dto.getPaytypecode())) {
						sb.append("֧����ʽ����[" + dto.getPaytypecode()
								+ "]���ʹ�ֻ��Ϊ 12-��Ȩ֧��");
					} else if ((MsgConstant.VOUCHER_NO_5201.equals(vtcode) || MsgConstant.VOUCHER_NO_5253
							.equals(vtcode))
							&& !"11".equals(dto.getPaytypecode())) {
						sb.append("֧����ʽ����[" + dto.getPaytypecode()
								+ "]���ʹ�ֻ��Ϊ  11-ֱ��֧��");
					} else if (MsgConstant.VOUCHER_NO_2252.equals(vtcode)
							&& !("11".equals(dto.getPaytypecode()) || "91"
									.equals(dto.getPaytypecode()))) {
						sb.append("֧����ʽ����[" + dto.getPaytypecode()
								+ "]���ʹ�ֻ��Ϊ  11-ֱ��֧��  91-ʵ��");
					} else if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
							.equals(vtcode))
							&& !"91".equals(dto.getPaytypecode())) {
						sb.append("֧����ʽ����[" + dto.getPaytypecode()
								+ "]���ʹ�ֻ��Ϊ  91-ʵ��");
					} else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode)) {
						if (StringUtils.isNotBlank(dto.getOriginalVtCode())
								&& dto.getOriginalVtCode().equals(
										MsgConstant.VOUCHER_NO_5201)
								&& !"11".equals(dto.getPaytypecode()))
							sb.append("֧����ʽ����[" + dto.getPaytypecode()
									+ "]���ʹ���ƾ֤���ͱ��Ϊ" + dto.getOriginalVtCode()
									+ "��֧����ʽ����ֻ��Ϊ 11-ֱ��֧��");
						else if (StringUtils
								.isNotBlank(dto.getOriginalVtCode())
								&& dto.getOriginalVtCode().equals(
										MsgConstant.VOUCHER_NO_5207)
								&& !"91".equals(dto.getPaytypecode()))
							sb.append("֧����ʽ����[" + dto.getPaytypecode()
									+ "]���ʹ���ƾ֤���ͱ��Ϊ" + dto.getOriginalVtCode()
									+ "��֧����ʽ����ֻ��Ϊ 91-ʵ��");
						else if (!"11".equals(dto.getPaytypecode())
								&& !"12".equals(dto.getPaytypecode())
								&& !"91".equals(dto.getPaytypecode()))
							sb.append("֧����ʽ����[" + dto.getPaytypecode()
									+ "]���ʹ�ֻ��Ϊ  11-ֱ��֧�� 12-��Ȩ֧�� 91-ʵ��");
					}
				} else {
					if ((MsgConstant.VOUCHER_NO_2301.equals(vtcode) || MsgConstant.VOUCHER_NO_2302
							.equals(vtcode))
							&& !("11".equals(dto.getPaytypecode())
									|| "001001".equals(dto.getPaytypecode())
									|| "001002".equals(dto.getPaytypecode()) || "12"
									.equals(dto.getPaytypecode()))) {
						sb.append("֧����ʽ����[" + dto.getPaytypecode()
								+ "]���ʹ�11(001001)-ֱ��֧����12(001002)-��Ȩ֧��");
					} else if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
							&& !"91".equals(dto.getPaytypecode())
							&& !"002".equals(dto.getPaytypecode())
							&& !dto.getPaytypecode().startsWith("92")) {
						if(dto.getTrecode().startsWith("27") && !"21".equals(dto.getPaytypecode())){//����
							sb.append("֧����ʽ����[" + dto.getPaytypecode()
									+ "]���ʹ�ʵ��ֻ��Ϊ  21 ");
						}else if(dto.getTrecode().startsWith("01")&& !dto.getPaytypecode().startsWith("91")){
							sb.append("֧����ʽ����[" + dto.getPaytypecode()
									+ "]���ʹ�ʵ��ֻ��Ϊ  91 ��002������91��ͷ");
						}else if(!dto.getTrecode().startsWith("27")&&!dto.getTrecode().startsWith("01")){
							sb.append("֧����ʽ����[" + dto.getPaytypecode()
									+ "]���ʹ�ʵ��ֻ��Ϊ  91 ����002");
						}
						
					}
				}
			}
		}

		// ҵ�����ͱ���
		if (StringUtils.isBlank(dto.getBusinessTypeCode())) {
			if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
					.indexOf(",sh,") >= 0)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("ҵ�����ͱ��벻��Ϊ�ա�");
		} else {
			if (match.matches() == false) {
				sb.append("ҵ�����ͱ����ʽ���󣬱���Ϊ1λ�����֡�");
			} else if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				if (!dto.getBusinessTypeCode().equals(
						StateConstant.BIZTYPE_CODE_SINGLE)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("ҵ�����ͱ���[" + dto.getBusinessTypeCode()
							+ "]ֻ��Ϊ1-����ҵ��    4-����ҵ��!");
			} else if (MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {
				if (!dto.getBusinessTypeCode().equals(
						StateConstant.BIZTYPE_CODE_SINGLE)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_SALARY)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("ҵ�����ͱ���[" + dto.getBusinessTypeCode()
							+ "]ֻ��Ϊ1-����ҵ�� 3-����ҵ��    4-����ҵ��!");
			} else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode)) {
				if (StringUtils.isNotBlank(dto.getOriginalVtCode())
						&& (dto.getOriginalVtCode().equals(
								MsgConstant.VOUCHER_NO_5207) || dto
								.getOriginalVtCode().equals(
										MsgConstant.VOUCHER_NO_5201))
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("ҵ�����ͱ���[" + dto.getBusinessTypeCode()
							+ "]���ʹ���ƾ֤���ͱ��Ϊ" + dto.getOriginalVtCode()
							+ "��ҵ�����ͱ���ֻ��Ϊ 4-����ҵ��!");
				else if (!dto.getBusinessTypeCode().equals(
						StateConstant.BIZTYPE_CODE_SINGLE)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_SALARY)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("ҵ�����ͱ���[" + dto.getBusinessTypeCode()
							+ "]ֻ��Ϊ1-����ҵ�� 3-����ҵ��  4-����ҵ��!");
			}
		}

		// ҵ����������
		if (StringUtils.isBlank(dto.getBusinessTypeName())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
				sb.append("ҵ���������Ʋ���Ϊ�ա�");
		} else {
			if (dto.getBusinessTypeName().getBytes().length > 60)
				sb.append("ҵ����������[" + dto.getBusinessTypeName()
						+ "]��ʽ���󣬱���ΪС��60���ַ���30�����֣���");
		}

		// �ʽ����ʱ���
		if (StringUtils.isBlank(dto.getFundTypeCode())) {

			if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
					.equals(vtcode))
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("�ʽ����ʱ��벻��Ϊ��");
		} else {
			if (!dto.getFundTypeCode().startsWith("1")
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
				sb.append("�ʽ����ʱ���[" + dto.getFundTypeCode() + "]��ֻ��Ϊ1��");
			else if (!dto.getFundTypeCode().startsWith("1")
					&& !dto.getFundTypeCode().startsWith("2")
					&& !dto.getFundTypeCode().startsWith("3")
					&& !dto.getFundTypeCode().startsWith("8")
					&& !dto.getFundTypeCode().startsWith("9")
					&& !dto.getFundTypeCode().endsWith("2")
					&& !dto.getFundTypeCode().endsWith("1")) {
				sb.append("�ʽ����ʱ���[" + dto.getFundTypeCode() + "]��ֻ����1��2��8��9��ͷ��");
			}
		}

		// ԭƾ֤����
		if (StringUtils.isBlank(dto.getOriginalVtCode())) {
			if (MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("��ƾ֤���ͱ�Ų���Ϊ��");
		} else {
			if (!(dto.getOriginalVtCode().equals(MsgConstant.VOUCHER_NO_5207)
					|| dto.getOriginalVtCode().equals(
							MsgConstant.VOUCHER_NO_5201) || dto
					.getOriginalVtCode().equals(MsgConstant.VOUCHER_NO_8202)))
				sb.append("��ƾ֤���ͱ��[" + dto.getOriginalVtCode()
						+ "]��ֻ��Ϊ5207��5201��8202");
		}

		// ƾ֤���
		if (StringUtils.isBlank(dto.getVoucherno())) {
			sb.append("ƾ֤��Ų���Ϊ�ա�");
		} else {
			if (MsgConstant.VOUCHER_NO_5209.equals(vtcode)) {// �����˸�
				Pattern oldVouPattern = Pattern.compile("[0-9]{8}");// ƥ��8λ����
				if (ITFECommonConstant.IFNEWINTERFACE.equals("1")) {// �½ӿ�
					if (dto.getVoucherno().getBytes().length > 20) {
						sb.append("ƾ֤��ű���С��20λ��");
					}
				} else {// �ɽӿ�
					match = oldVouPattern.matcher(dto.getVoucherno());
					if (match.matches() == false) {
						sb.append("ƾ֤��Ÿ�ʽ���󣬱���Ϊ8λ�����֡�");
					}
				}
			} else if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)) {
				if (dto.getVoucherno().getBytes().length > 17) {
					sb.append("ƾ֤��ű���С��17λ��");
				}
			} else {
				if (dto.getVoucherno().getBytes().length > 20) {
					sb.append("ƾ֤��ű���С��20λ��");
				}
			}
		}

		// �ȶ�ƾ֤��ţ��Ϻ���ɫ��
		if (StringUtils.isBlank(dto.getPayVoucherNo())) {
			if ((MsgConstant.VOUCHER_NO_5106.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
					.indexOf(",sh,") >= 0)
					|| (MsgConstant.VOUCHER_NO_5108.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
							.indexOf(",sh,") >= 0))
				sb.append("֧��ƾ֤�ţ�����ƾ֤�ţ�����Ϊ�ա�");
			else if (MsgConstant.VOUCHER_NO_2252.equals(vtcode))
				sb.append("ԭҵ�񵥾ݺŲ���Ϊ�ա�");
			else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("��֧��ƾ֤��Ų���Ϊ�ա�");
		} else {
			if (dto.getVoucherno().getBytes().length > 20) {
				if ((MsgConstant.VOUCHER_NO_5106.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
						.indexOf(",sh,") >= 0)
						|| (MsgConstant.VOUCHER_NO_5108.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
								.indexOf(",sh,") >= 0))
					sb.append("֧��ƾ֤�ţ�����ƾ֤�ţ�����С��20λ��");
				else if (MsgConstant.VOUCHER_NO_2252.equals(vtcode))
					sb.append("ԭҵ�񵥾ݺ� ����С��20λ��");
				else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode))
					sb.append("��֧��ƾ֤��� ����С��20λ��");
			}
		}

		// ƾ֤����
		if (StringUtils.isBlank(dto.getVoudate())) {
			sb.append("ƾ֤���ڲ���Ϊ�ա�");
		} else {// ����ƾ֤���ڲ��ܴ��ڵ�ǰ���ڵ�У��
			String voucherdate = dto.getVoudate();
			Date comparedate = TimeFacade.parseDate(voucherdate);
			Date currentdate = TimeFacade.parseDate(TimeFacade
					.getCurrentStringTime());
			if (comparedate.after(currentdate)) {
				sb.append("ƾ֤���ڲ��ܴ��ڵ�ǰ���ڡ�");
			}
		}

		// �տ����˺�
		if (StringUtils.isBlank(dto.getAgentAcctNo())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode))
				sb.append("�տ��˺Ų���Ϊ�ա�");
		} else {
			Pattern acctPattern;
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				acctPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// ƥ��С��32λ����
			} else {
				acctPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// ƥ��С��32λ����
			}
			match = acctPattern.matcher(dto.getAgentAcctNo());
			if (match.matches() == false) {
				sb.append("�տ��˺�[" + dto.getAgentAcctNo()
						+ "]��ʽ���󣬱���ΪС��32λ�����֡��ַ�[0-9A-Z]��");
			}
		}

		// �տ����˺�����
		if (StringUtils.isBlank(dto.getAgentAcctName())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode))
				sb.append("�տ����˺����Ʋ���Ϊ�ա�");
		} else {
			if (dto.getAgentAcctName().getBytes().length > 60) {
				sb.append("�տ����˺�����[" + dto.getAgentAcctName()
						+ "]��ʽ���󣬱���ΪС��60���ַ���30�����֣���");
			} else if (MsgConstant.VOUCHER_NO_5209.equals(vtcode)) {// �����˸�У�տ�������У����Ƨ��
				String errChi_9 = "";
				try {
					errChi_9 = VerifyParamTrans.verifyNotUsableChinese(dto
							.getAgentAcctName());
				} catch (Exception e) {
					logger.error(e);
				}
				if (null != errChi_9 && !"".equals(errChi_9)) {
					sb.append("�տ����˺�����[" + dto.getAgentAcctName() + "]�д��ڷǷ��ַ���"
							+ errChi_9 + "��");
				}
			}
		}

		// �տ�����������
		if (StringUtils.isBlank(dto.getPaybankname())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5209.equals(vtcode))
				sb.append("�տ����������Ʋ���Ϊ�ա�");
		}

		// ���������к�
		if (StringUtils.isBlank(dto.getPaybankno())) {
			if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5108.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5351.equals(vtcode))
				sb.append("���������кŲ���Ϊ�ա�");
		} else {
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
					&& (MsgConstant.VOUCHER_NO_5108.equals(vtcode)
							|| MsgConstant.VOUCHER_NO_5253.equals(vtcode) || MsgConstant.VOUCHER_NO_5201
							.equals(vtcode))) {
				match = finorgcodePattern.matcher(dto.getPaybankno());
			} else {
				match = bankcodePattern.matcher(dto.getPaybankno());
			}
			if (match.matches() == false) {
				sb.append("���������кŸ�ʽ���󣬱���Ϊ12λ�����֡�");
			}
		}

		// �������˺�
		if (StringUtils.isBlank(dto.getClearAcctNo())) {
			if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode))
				sb.append("�������˺Ų���Ϊ�ա�");
		} else {
			if (dto.getClearAcctNo().getBytes().length > 32) {
				sb.append("�������˺�[" + dto.getClearAcctNo() + "]��ʽ���󣬱���С��32λ��");
			}
		}

		// �������˺�����
		if (StringUtils.isBlank(dto.getClearAcctName())) {
			if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode))
				sb.append("�������˺����Ʋ���Ϊ�ա�");
		} else {
			if (dto.getClearAcctName().getBytes().length > 60)
				sb.append("�������˺�����[" + dto.getClearAcctName()
						+ "]��ʽ���󣬱���ΪС��60���ַ���30�����֣���");
		}

		// ʵ���ʽ�Ԥ�㵥λ����
		if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
				.equals(vtcode))
				&& StringUtils.isBlank(dto.getAgencyName())) {
			sb.append("Ԥ�㵥λ���Ʋ���Ϊ�ա�");
		} else {
			if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
					.equals(vtcode))
					&& dto.getAgencyName().getBytes().length > 120)
				sb.append("Ԥ�㵥λ����[" + dto.getAgencyName()
						+ "]��ʽ���󣬱���ΪС��120���ַ���60�����֣���");
		}

		/**
		 * �Ϻ����з������е��տ������˿�֪ͨƾ֤2252�����ֶθ�ʽУ��
		 * 
		 * @author �Ż��
		 */
		if (MsgConstant.VOUCHER_NO_2252.equals(vtcode)) {

			// ֧���������
			Pattern pattern = Pattern.compile("[0-9]{8}");// ƥ��8λ����
			// �����д���paybankno����֧���������payDictateNo
			if (StringUtils.isBlank(dto.getPaybankname())) {
				sb.append("֧��������Ų���Ϊ�ա�");
			} else {
				match = pattern.matcher(dto.getPaybankname());
				if (match.matches() == false) {
					sb.append("֧���������[" + dto.getPaybankname()
							+ "]��ʽ���󣬱���Ϊ8λ�����֡�");
				}
			}

			// ֧�����ı��
			Pattern acctPattern = Pattern.compile("[0-9]{3}");// ƥ��3λ����
			// ���տ������˺Ŵ���֧�����ı��
			if (StringUtils.isBlank(dto.getAgentAcctNo())) {
				sb.append("֧�����ı�Ų���Ϊ�ա�");
			} else {
				match = acctPattern.matcher(dto.getAgentAcctNo());
				if (match.matches() == false) {
					sb.append("֧�����ı��[" + dto.getAgentAcctNo()
							+ "]��ʽ���󣬱���Ϊ3λ���֡�");
				} else if (!"100".equals(dto.getAgentAcctNo())
						&& !"103".equals(dto.getAgentAcctNo())
						&& !"001".equals(dto.getAgentAcctNo())
						&& !"111".equals(dto.getAgentAcctNo())
						&& !"112".equals(dto.getAgentAcctNo())
						&& !"121".equals(dto.getAgentAcctNo())
						&& !"122".equals(dto.getAgentAcctNo())) {
					sb
							.append("֧�����ı��["
									+ dto.getAgentAcctNo()
									+ "]���ʹ��󣬱���Ϊһ����ţ�100�� 103��001 ���߶�����ţ� 111��112��121��122��");
				}
			}

			// ���տ������˻����ƴ���֧��ί������
			if (StringUtils.isBlank(dto.getAgentAcctName().trim())) {
				sb.append("֧��ί�����ڲ���Ϊ�ա�");
			} else {// ֧��ί�����ڲ��ܴ��ڵ�ǰ���ڵ�У��
				String payEntrustDate = dto.getAgentAcctName();
				Date comparedate = TimeFacade.parseDate(payEntrustDate);
				Date currentdate = TimeFacade.parseDate(TimeFacade
						.getCurrentStringTime());
				if (comparedate.after(currentdate)) {
					sb.append("֧��ί�����ڲ��ܴ��ڵ�ǰ���ڡ�");
				}
			}

			// ֧���������к�
			Pattern paySendBankPattern = Pattern.compile("[0-9]{1,14}");// ƥ��С��14λ����
			// �ø����˺Ŵ���֧���������к�
			if (StringUtils.isBlank(dto.getClearAcctNo())) {
				sb.append("֧���������кŲ���Ϊ�ա�");
			} else {
				match = paySendBankPattern.matcher(dto.getClearAcctNo());
				if (match.matches() == false) {
					sb.append("֧���������к�[" + dto.getClearAcctNo()
							+ "]��ʽ���󣬱���С��14λ���֡�");
				}
			}

			// �ø����˺����ƴ���ʵ���˿�����
			if (StringUtils.isBlank(dto.getClearAcctName())) {
				sb.append("ʵ���˿����ڲ���Ϊ�ա�");
			} else {// ʵ���˿����ڲ��ܴ��ڵ�ǰ���ڵ�У��
				if (Integer.parseInt(TimeFacade.getCurrentStringTime()) < Integer
						.parseInt(dto.getClearAcctName().substring(0, 8)))
					sb.append("ʵ���˿����ڲ��ܴ��ڵ�ǰ���ڡ�");
			}
		}

		// Ԥ�㵥λ����У��
		/**
		 * У���˺���Ԥ�������Ƿ�һ�� ��������Ԥ���ڣ��⣩����Ĭ��Ԥ���� У�鷽ʽ((S_ORGCODE, S_TRECODE,
		 * S_PAYERACCOUNT)
		 */
		if ("000073100012".equals(ITFECommonConstant.SRC_NODE)&&(MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5207.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5267.equals(vtcode))) {
			TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
			tmppk.setSorgcode(dto.getOrgcode()); // ��������
			tmppk.setStrecode(dto.getTrecode()); // TreCode��������
			tmppk.setSpayeraccount(dto.getClearAcctNo());// ClearAcctNo �����˺�
			TsInfoconnorgaccDto resultdto = null;
			try {
				resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(
						tmppk);
			} catch (JAFDatabaseException e) {
				sb.append("��ѯ����˻��쳣:" + e.getMessage());
			}
			if (null == resultdto) {
				sb.append("�����������˻���Ϣ�ڲ�������˻��в�����!");
			} else {
				if (StringUtils.isBlank(resultdto.getSbiztype())) {
					resultdto.setSbiztype("1");
				}
				if (!resultdto.getSbiztype().equalsIgnoreCase(
						dto.getBudgettype())) {
					sb.append("Ԥ���������������˻���Ϣ��ά���Ĳ�һ��");
				}
			}
		}

		String msg = sb.toString();
		if (msg.length() == 0) {
			return null;
		}
		return msg;
	}

	/**
	 * У����ҽ�����λ��ѡ����λΪԪ�����������15λ��С�����̶ֹ���λ�����ܰ������ŵȷָ������磺8979.05
	 * 
	 * @param str
	 * @return
	 */
	private boolean isCurrency(String str) {
		Pattern pattern = Pattern.compile("^[+-]?[0-9]{1,15}(\\.\\d{2}){1}");
		Matcher match = pattern.matcher(str.trim());
		return match.matches();
	}

	/**
	 * У����ϸId�Ƿ�Ϊ�ջ��ظ�
	 * 
	 * @param subDtoIdList
	 * @return
	 */
	public static String checkValidSudDtoId(List<String> subDtoIdList) {
		if (subDtoIdList.size() == 0)
			return "��ϸId���ڿ�ֵ";
		StringBuffer sb = new StringBuffer();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String id : subDtoIdList) {
			if (StringUtils.isBlank(id))
				sb.append("��ϸId���ڿ�ֵ��");
			if (id.equals("�ڵ㲻����"))
				return null;
			Integer count = map.get(id);
			if (count == null)
				map.put(id, 1);
			else
				map.put(id, ++count);
		}
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String id = (String) it.next();
			Integer count = map.get(id);
			if (count > 1) {
				sb.append(id + "����" + count + "����");
			}
		}
		return StringUtils.isBlank(sb.toString()) ? null : "��ϸId�ظ���"
				+ sb.toString().substring(0, sb.toString().length() - 1) + "��";
	}
	
	private int verifyFor5407(List lists, String vtCode) throws JAFDatabaseException, ValidateException, ITFEBizException {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvInCorrhandbookDto mainDto = (TvInCorrhandbookDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);

			// Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = new ArrayList<String>();
			expFuncCodeList.add(mainDto.getSoribdgsbtcode());
			expFuncCodeList.add(mainDto.getScurbdgsbtcode());
			// У���㽭Ҫ��У��������verifyTreasury(mainDto.getSoripayeetrecode(), "")&&verifyTreasury(mainDto.getScurpayeetrecode(), "")&& 
			if (verifySfinOrgCode(mainDto.getSfinorgcode(),vDto.getSorgcode(), vDto.getStrecode())
					&& verifySubject(vDto.getSorgcode(),expFuncCodeList, vtCode)
			) {
				
				VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				count++;
				succList.add(new Integer(count));
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}
	/**
	 * У�����Ԥ�㼶��
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public boolean verifyInfoAndPay(TvVoucherinfoDto vDto) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		try{
			Map<String,TsOrganDto> orgmap = SrvCacheFacade.cacheOrgMap();
			Voucher ver = new Voucher();
			if("hold1".equals(orgmap.get(vDto.getSorgcode()).getSofprovorgcode()))
			{
				TvVoucherinfoDto vsdto = new TvVoucherinfoDto();
				if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301))
					vsdto.setShold1(vDto.getSvoucherno());
				else if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108))
					vsdto.setSvoucherno(vDto.getShold1());
				vsdto.setScreatdate(vDto.getScreatdate());
				vsdto.setSstyear(vDto.getSstyear());
				vsdto.setSorgcode(vDto.getSorgcode());
				vsdto.setStrecode(vDto.getStrecode());
				List<TvVoucherinfoDto> questlist = CommonFacade.getODB().findRsByDto(vsdto);
				TvVoucherinfoDto seardto = null;
				if(questlist!=null&&questlist.size()==1)
					seardto = questlist.get(0);
				logger.info(vDto.getSvtcode()+"��Ⱥ�����ƥ���ѯ"+(seardto==null?"��ѯΪ��!":seardto.getSvtcode()));
				logger.info(vDto.getSvoucherno()+"��Ⱥ�����ƥ���ѯ"+(seardto==null?"��ѯΪ��!":seardto.getSvoucherno()));
				logger.info(vDto.getSstatus()+"��Ⱥ�����ƥ���ѯ"+(seardto==null?"��ѯΪ��!":seardto.getSstatus()));
				if(seardto==null||"16627290".contains(seardto.getSstatus()))
				{
					this.tmpFailReason = "�ȶԶ����Ϣ��������Ϣʧ��!";
					return false;
				}else if(DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(seardto.getSstatus())&&"10151720".contains(vDto.getSstatus()))
				{
					if((vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
					{
						List<TvVoucherinfoDto> verlist = new ArrayList<TvVoucherinfoDto>();
						verlist.add(seardto);
						ver.voucherVerify(verlist);
					}
				}
			}
		}catch(Exception e)
		{
			logger.error(e);
		}
		return true;
	}
}
