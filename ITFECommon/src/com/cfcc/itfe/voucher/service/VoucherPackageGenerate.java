package com.cfcc.itfe.voucher.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class VoucherPackageGenerate {

	private static Log logger = LogFactory.getLog(VoucherPackageGenerate.class);

	/*
	 * �ְ�ģ�� �˴�����Ҫ��������һ�������������
	 */
	public static int packageGenerateByVtcode(
			List<TvVoucherinfoDto> voucherDtoList, String svtcode)
			throws ITFEBizException {
		int count = 0;
		String tmpPackNo = "";
		StringBuffer ls_SQLBf = new StringBuffer(" where ");
		List<String> params = new ArrayList<String>();
		for (int i = 0; i < voucherDtoList.size(); i++) {
			if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)||svtcode.equals(MsgConstant.VOUCHER_NO_5267)) {
				ls_SQLBf.append(" ( S_BIZNO = ? ) or");
			}else if (svtcode.equals(MsgConstant.VOUCHER_NO_5671)||svtcode.equals(MsgConstant.VOUCHER_NO_5408)) {
				ls_SQLBf.append(" ( S_DEALNO = ? ) or");
			} else {
				ls_SQLBf.append(" ( I_VOUSRLNO = ? ) or");
			}
			params.add(voucherDtoList.get(i).getSdealno());
		}
		String ls_SQL = ls_SQLBf.toString();
		ls_SQL = ls_SQL.substring(0, ls_SQL.length() - 2);
		try {
			// ����ƾ֤���Ͳ���������Ϣ
			if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)||svtcode.equals(MsgConstant.VOUCHER_NO_5267)) {
				List mainDtoList = new ArrayList<TvPayoutmsgmainDto>();

				mainDtoList = DatabaseFacade.getDb().find(
						TvPayoutmsgmainDto.class, ls_SQL, params);
				List list = VoucherUtil.getListBySpayeebankno(mainDtoList);
				for (List<TvPayoutmsgmainDto> mainList : (List<List>) list) {

					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					tmpPackNo = VoucherPackageGenerate.packageGenerate(vList,
							mainList.get(0).getSpayunit(), VoucherUtil
									.getOperationType(svtcode));
					// ����ҵ��������Ϣ
					for (TvPayoutmsgmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setScommitdate(TimeFacade.getCurrentStringTime());
					}
					TvPayoutmsgmainDto[] mainDtos = new TvPayoutmsgmainDto[mainList
							.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {

						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");

						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);

					count += vList.size();
				}
			} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5106)) {
				List<TvGrantpaymsgmainDto> mainDtoList = new ArrayList<TvGrantpaymsgmainDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TvGrantpaymsgmainDto.class, ls_SQL, params);
				if (mainDtoList.size() > 1000) {
					throw new ITFEBizException("��Ȩ֧��ƾ֤�ְ���ƾ֤����1000����");
				}
				// ƾ֤�ְ�
				tmpPackNo = VoucherPackageGenerate.packageGenerate(
						voucherDtoList, mainDtoList.get(0).getSpayunit(),
						VoucherUtil.getOperationType(svtcode));
				// ����ҵ��������Ϣ
				for (TvGrantpaymsgmainDto dto : mainDtoList) {
					dto.setSpackageno(tmpPackNo);
					dto.setScommitdate(TimeFacade.getCurrentStringTime());
				}
				TvGrantpaymsgmainDto[] mainDtos = new TvGrantpaymsgmainDto[mainDtoList
						.size()];
				mainDtos = mainDtoList.toArray(mainDtos);
				DatabaseFacade.getODB().update(mainDtos);
				voucherDtoList.get(0).setSpackno(tmpPackNo);
				VoucherUtil.sendTips(voucherDtoList.get(0));
				// ����ƾ֤�б�״̬
				for (TvVoucherinfoDto dto : voucherDtoList) {

					dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
					dto.setSdemo("�ѷ���");

					dto.setSpackno(tmpPackNo);
				}
				TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[voucherDtoList
						.size()];
				vDtos = voucherDtoList.toArray(vDtos);
				DatabaseFacade.getODB().update(vDtos);
				count += voucherDtoList.size();
			} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5108)) {
				List<TvDirectpaymsgmainDto> mainDtoList = new ArrayList<TvDirectpaymsgmainDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TvDirectpaymsgmainDto.class, ls_SQL, params);
				// ƾ֤�ְ�
				tmpPackNo = VoucherPackageGenerate.packageGenerate(
						voucherDtoList, mainDtoList.get(0).getSpayunit(),
						VoucherUtil.getOperationType(svtcode));
				// ����ҵ��������Ϣ
				for (TvDirectpaymsgmainDto dto : mainDtoList) {
					dto.setSpackageno(tmpPackNo);
					dto.setScommitdate(TimeFacade.getCurrentStringTime());
				}
				TvDirectpaymsgmainDto[] mainDtos = new TvDirectpaymsgmainDto[mainDtoList
						.size()];
				mainDtos = mainDtoList.toArray(mainDtos);
				DatabaseFacade.getODB().update(mainDtos);
				voucherDtoList.get(0).setSpackno(tmpPackNo);
				VoucherUtil.sendTips(voucherDtoList.get(0));
				// ����ƾ֤�б�״̬
				for (TvVoucherinfoDto dto : voucherDtoList) {
					dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
					dto.setSdemo("�ѷ���");
					dto.setSpackno(tmpPackNo);
				}
				TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[voucherDtoList
						.size()];
				vDtos = voucherDtoList.toArray(vDtos);
				DatabaseFacade.getODB().update(vDtos);
				count += voucherDtoList.size();
			} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5209)) {

				List mainDtoList = new ArrayList<TvDwbkDto>();
				mainDtoList = DatabaseFacade.getDb().find(TvDwbkDto.class,
						ls_SQL, params);
				List list = VoucherUtil.getListByStaxorgcode(mainDtoList);
				for (List<TvDwbkDto> mainList : (List<List>) list) {

					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					tmpPackNo = VoucherPackageGenerate.packageGenerate(vList,
							mainList.get(0).getStaxorgcode(), VoucherUtil
									.getOperationType(svtcode));
					// ����ҵ��������Ϣ
					for (TvDwbkDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
					}
					TvDwbkDto[] mainDtos = new TvDwbkDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {

						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");

						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);
					count += vList.size();
				}

			} else if (svtcode.equals(MsgConstant.VOUCHER_NO_2301)) {

				List mainDtoList = new ArrayList<TvPayreckBankDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TvPayreckBankDto.class, ls_SQL, params);
				List list = VoucherUtil.getListBySagentbnkcode(mainDtoList);
				for (List<TvPayreckBankDto> mainList : (List<List>) list) {

					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					tmpPackNo = VoucherPackageGenerate.packageGenerate(vList,
							mainList.get(0).getSagentbnkcode(), VoucherUtil
									.getOperationType(svtcode));
					// ����ҵ��������Ϣ
					StringBuffer updatesqlbf = new StringBuffer("update TV_PAYRECK_BANK set S_PACKNO='"+tmpPackNo+"' where ");
					for (TvPayreckBankDto dto : mainList) {
						updatesqlbf.append(" ( I_VOUSRLNO = '"+dto.getIvousrlno().toString()+"' ) or");
					}
					String updatesql = updatesqlbf.toString();
					updatesql = updatesql.substring(0, updatesql.length() - 2);
					DatabaseFacade.getODB().execSql(updatesql);
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");
						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);
					count += vList.size();
				}

			} else if (svtcode.equals(MsgConstant.VOUCHER_NO_2302)) {
				List mainDtoList = new ArrayList<TvPayreckBankBackDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TvPayreckBankBackDto.class, ls_SQL, params);
				List list = VoucherUtil.getListBySagentbnkcodes(mainDtoList);
				for (List<TvPayreckBankBackDto> mainList : (List<List>) list) {

					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					tmpPackNo = VoucherPackageGenerate.packageGenerate(vList,
							mainList.get(0).getSagentbnkcode(), VoucherUtil
									.getOperationType(svtcode));
					StringBuffer updatesqlbf = new StringBuffer("update TV_PAYRECK_BANK_BACK set S_PACKNO='"+tmpPackNo+"' where ");
					for (TvPayreckBankBackDto dto : mainList) {
						updatesqlbf.append(" ( I_VOUSRLNO = '"+dto.getIvousrlno().toString()+"' ) or");
					}
					String updatesql = updatesqlbf.toString();
					updatesql = updatesql.substring(0, updatesql.length() - 2);
					DatabaseFacade.getODB().execSql(updatesql);
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");
						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);
					count += vList.size();
				}

			}else if (svtcode.equals(MsgConstant.VOUCHER_NO_5201)) {
				List mainDtoList = new ArrayList<TfDirectpaymsgmainDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TfDirectpaymsgmainDto.class, ls_SQL, params);
				//���մ������кŽ��зְ�
				List list = VoucherUtil.getListBySpaybankcode(mainDtoList);
				for (List<TfDirectpaymsgmainDto> mainList : (List<List>) list) {
					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					TfDirectpaymsgmainDto mainDto=mainList.get(0);
					//��Ʊ��λ������ҵ��ȡ���������кţ�����ҵ��ȡ������������
					String Spayunit=mainDto.getSbusinesstypecode().equals(StateConstant.BIZTYPE_CODE_BATCH)?
							mainDto.getSpayeeacctbankno():mainDto.getSfinorgcode();
					//��ȡ����		
					tmpPackNo = packageGenerate(vList,Spayunit, MsgConstant.VOUCHER_NO_5201);
					// ����ҵ��������Ϣ
					for (TfDirectpaymsgmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setScommitdate(TimeFacade.getCurrentStringTime());
					}
					TfDirectpaymsgmainDto[] mainDtos = new TfDirectpaymsgmainDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");
						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);
					count += vList.size();
				}
			}else if (svtcode.equals(MsgConstant.VOUCHER_NO_2252)) {
				List mainDtoList = new ArrayList<TfPaybankRefundmainDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TfPaybankRefundmainDto.class, ls_SQL, params);
				//����֧���������кŽ��зְ�
				List list = VoucherUtil.getListBySpaysndbnkno(mainDtoList);
				for (List<TfPaybankRefundmainDto> mainList : (List<List>) list) {
					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					TfPaybankRefundmainDto mainDto=mainList.get(0);
					//��ȡ����		
					tmpPackNo = packageGenerate(vList,mainDto.getSpaysndbnkno(), svtcode);
					// ����ҵ��������Ϣ
					for (TfPaybankRefundmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setScommitdate(TimeFacade.getCurrentStringTime());
					}
					TfPaybankRefundmainDto[] mainDtos = new TfPaybankRefundmainDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					copyTable(vList.get(0), mainList);
					if(ITFECommonConstant.PUBLICPARAM.indexOf("sh,") > 0 && !("91".equals(vList.get(0).getShold2().trim()))){
						
					
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");
						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);
					}
					count += vList.size();
				}
			}else if (svtcode.equals(MsgConstant.VOUCHER_NO_5407)) {
				List mainDtoList = new ArrayList<TvInCorrhandbookDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TvInCorrhandbookDto.class, ls_SQL, params);
				//����֧���������кŽ��зְ�
				List list = VoucherUtil.getListByCorrhandbook(mainDtoList);
				for (List<TvInCorrhandbookDto> mainList : (List<List>) list) {
					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					TvInCorrhandbookDto mainDto=mainList.get(0);
					//��ȡ����		
					tmpPackNo = packageGenerate(vList,mainDto.getSfinorgcode(), svtcode);
					// ����ҵ��������Ϣ
					for (TvInCorrhandbookDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
					}
					TvInCorrhandbookDto[] mainDtos = new TvInCorrhandbookDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");
						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);
					count += vList.size();
				}
			}else if (svtcode.equals(MsgConstant.VOUCHER_NO_5671)||svtcode.equals(MsgConstant.VOUCHER_NO_5408)) {
				List mainDtoList = new ArrayList<TvNontaxmainDto>();
				mainDtoList = DatabaseFacade.getDb().find(
						TvNontaxmainDto.class, ls_SQL, params);
				//����֧���������кŽ��зְ�
				List list = VoucherUtil.getListByNontaxIncome(mainDtoList);
				for (List<TvNontaxmainDto> mainList : (List<List>) list) {
					// �����������ƾ֤�б�
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// ƾ֤�ְ�
					TvNontaxmainDto mainDto=mainList.get(0);
					//��ȡ����		
					tmpPackNo = packageGenerate(vList,mainDto.getSfinorgcode(), svtcode);
					// ����ҵ��������Ϣ
					for (TvNontaxmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
					}
					TvNontaxmainDto[] mainDtos = new TvNontaxmainDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// ���ͱ���
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// ����ƾ֤�б�״̬
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("�ѷ���");
						dto.setSpackno(tmpPackNo);
					}
					TvVoucherinfoDto[] vDtos = new TvVoucherinfoDto[vList
							.size()];
					vDtos = vList.toArray(vDtos);
					DatabaseFacade.getODB().update(vDtos);
					count += vList.size();
				}
			}
		} catch (ITFEBizException e) {
			logger.error("ƾ֤�ְ������쳣��" + e.getMessage(), e);
			throw new ITFEBizException("ƾ֤�ְ������쳣��", e);
		} catch (JAFDatabaseException e) {
			logger.error("����������Ϣ�����ƾ֤״̬�쳣��", e);
			throw new ITFEBizException("����������Ϣ�����ƾ֤״̬�쳣��", e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("ƾ֤�ְ������쳣��", e);
		}
		return count;
	}

	// ƾ֤�ְ�
	public static String packageGenerate(List list, String Spayunit,
			String operationtypecode) throws ITFEBizException {

		int count = 0;
		String tmpPackNo = "";
		try {
			List<TvFilepackagerefDto> filepackagerefDtoList = new ArrayList();
			String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ����
			TvVoucherinfoDto dto = (TvVoucherinfoDto) list.get(0);
			String ls_TreCode = dto.getStrecode();

			tmpPackNo = SequenceGenerator
					.changePackNoForLocal(SequenceGenerator.getNextByDb2(
							SequenceName.FILENAME_PACKNO_REF_SEQ,
							SequenceName.TRAID_SEQ_CACHE,
							SequenceName.TRAID_SEQ_STARTWITH,
							MsgConstant.SEQUENCE_MAX_DEF_VALUE));

			BigDecimal famtAll = new BigDecimal("0.00");
			for (TvVoucherinfoDto vDto : (List<TvVoucherinfoDto>) list) {
				famtAll = famtAll.add(vDto.getNmoney());
			}
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSorgcode(dto.getSorgcode());// ���ݹ������ó�
			// �����������
			packdto.setStrecode(ls_TreCode);
			packdto.setSfilename(dto.getSfilename());
			// ���ջ��ش���
			packdto.setStaxorgcode(Spayunit);
			packdto.setScommitdate(currentDate);
			packdto.setSaccdate(currentDate);
			packdto.setSpackageno(tmpPackNo);
			packdto.setSoperationtypecode(operationtypecode);
			packdto.setIcount(list.size());
			packdto.setNmoney(famtAll);
			packdto.setSretcode(DealCodeConstants.DEALCODE_VOUCHER_ITFE_WAIT_DEALING);
			packdto.setSchkstate(StateConstant.CONFIRMSTATE_YES);
			packdto.setSusercode("000");
			packdto.setImodicount(0);
			packdto.setSpackageno(tmpPackNo);
			filepackagerefDtoList.add(packdto);

			TvFilepackagerefDto[] fileDto = new TvFilepackagerefDto[filepackagerefDtoList
					.size()];
			fileDto = filepackagerefDtoList.toArray(fileDto);
			DatabaseFacade.getDb().create(fileDto);
		} catch (JAFDatabaseException e) {
			logger.error("��Ӵ����ְ�ʱ�����쳣��" + e);
			throw new ITFEBizException("��Ӵ����ְ�ʱ�����쳣��", e);
		} catch (Exception e) {
			logger.error("��Ӵ����ְ�ʱ�����쳣��" + e);
			throw new ITFEBizException("��Ӵ����ְ�ʱ�����쳣��", e);
		}
		return tmpPackNo;
	}
	
	
	
	
	
	/**
	 * ���Ʊ�����
	 * @param dto
	 * @param maindtoList
	 * @throws ITFEBizException
	 */
	private static void copyTable(TvVoucherinfoDto dto,List maindtoList) throws ITFEBizException{
		List copyMaindtoList=new ArrayList();
		List copySubdtoList=new ArrayList();
		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252) && !("91".equals(dto.getShold2().trim()))){
			//ֱ��֧���˿�
			for(TfPaybankRefundmainDto maindto2252:(List<TfPaybankRefundmainDto>) maindtoList){
				if(voucherVerifyIsCopy(dto))
					continue;
				List list = VoucherUtil.findVoucherDto(maindto2252.getStrecode(),MsgConstant.VOUCHER_NO_5201 , 
						maindto2252.getSoriginalvoucherno(), DealCodeConstants.VOUCHER_SUCCESS);
				if(list==null||list.size()==0){
					throw new ITFEBizException("δ�ҵ���Ӧ��"+MsgConstant.VOUCHER_NO_5201+"ƾ֤��");
				}
				TfDirectpaymsgmainDto maindto5201=(TfDirectpaymsgmainDto) VoucherUtil.findMainDtoByVoucher(
						(TvVoucherinfoDto) list.get(0)).get(0);
				copyMaindtoList.add(copyMaindto2252(dto, maindto2252, maindto5201));
				List subdtoList2252=PublicSearchFacade.findSubDtoByMain(maindto2252);
				HashMap subdtoMap5201=VoucherUtil.convertListToMap(PublicSearchFacade.findSubDtoByMain(maindto5201));
				for(TfPaybankRefundsubDto subdto2252:(List<TfPaybankRefundsubDto>)subdtoList2252){
					copySubdtoList.add(copySubdto2252(subdto2252, (TfDirectpaymsgsubDto)subdtoMap5201.get(subdto2252.getSid()), 
							maindto5201));
				}				
			}
		}else{
			//ʵ���ʽ��˿�
			for(TfPaybankRefundmainDto maindto2252:(List<TfPaybankRefundmainDto>) maindtoList){
				//����ԭ����
				List list = VoucherUtil.findVoucherDto(maindto2252.getStrecode(),MsgConstant.VOUCHER_NO_5207 , 
						maindto2252.getSoriginalvoucherno(), DealCodeConstants.VOUCHER_SUCCESS);
				if(list==null||list.size()==0){
					throw new ITFEBizException("δ�ҵ���Ӧ��" + MsgConstant.VOUCHER_NO_5207 + "ƾ֤��");
				}
				TvVoucherinfoDto tmpInfoDto = (TvVoucherinfoDto) ((TvVoucherinfoDto) list.get(0)).clone();
				tmpInfoDto.setSvtcode(MsgConstant.VOUCHER_NO_3208);
				List payOutList=VoucherUtil.findMainDtoByVoucher(tmpInfoDto);
				//�ж��Ƿ��Ѿ�����
				if(null != payOutList && payOutList.size() > 0)
					continue;
				TvPayoutmsgmainDto mainDto5207 = (TvPayoutmsgmainDto) VoucherUtil.findMainDtoByVoucher((TvVoucherinfoDto) list.get(0)).get(0);
				copyMaindtoList.add(copyPayOutMaindto2252(dto, maindto2252, mainDto5207));
				//�����ӱ�
				List<IDto> subdtoList2252=PublicSearchFacade.findSubDtoByMain(maindto2252);
				HashMap subdtoMap5207 =VoucherUtil.convertListToMap(PublicSearchFacade.findSubDtoByMain(mainDto5207));
				for(IDto tmpDto : subdtoList2252){
					TfPaybankRefundsubDto tmp = (TfPaybankRefundsubDto) tmpDto;
					copySubdtoList.add(copyPayOutSubdto2252(tmp, (TvPayoutmsgsubDto)subdtoMap5207.get(tmp.getSid()), mainDto5207));
				}
			}
		}
		try {
			DatabaseFacade.getODB().create(CommonUtil.listTArray(copyMaindtoList));
			DatabaseFacade.getODB().create(CommonUtil.listTArray(copySubdtoList));
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("д��������Ϣ�쳣!");
		}
	}
	
	/**
	 * ����2252����
	 * @param dto
	 * @param maindto2252
	 * @param maindto5201
	 * @return
	 */
	private static TvPayreckBankBackDto copyMaindto2252(TvVoucherinfoDto dto,
			TfPaybankRefundmainDto maindto2252,TfDirectpaymsgmainDto maindto5201){
		TvPayreckBankBackDto maindto=new TvPayreckBankBackDto();
		maindto.setStrano(maindto2252.getSdealno());// ���뻮��ƾ֤Id
		maindto.setIvousrlno(maindto2252.getIvousrlno());// ƾ֤��ˮ��
		maindto.setSid(maindto2252.getSid());// ���뻮��ƾ֤Id
		maindto.setSadmdivcode(maindto2252.getSadmdivcode());//������������
		maindto.setSofyear(maindto2252.getSstyear());// ҵ�����
		maindto.setSvtcode(maindto2252.getSvtcode());//ƾ֤���ͱ��
		maindto.setDvoudate(CommonUtil.strToDate(maindto2252.getSvoudate())); // ƾ֤����
		maindto.setSvouno(maindto2252.getSvoucherno());// ƾ֤��
		maindto.setSbookorgcode(maindto2252.getSorgcode());//�����������
		maindto.setStrecode(maindto2252.getStrecode()); // �����������
		maindto.setSfinorgcode(maindto2252.getSfinorgcode());// �������ش���
		maindto.setSbgttypecode(maindto5201.getSbgttypecode());// Ԥ�����ͱ���
		maindto.setSbgttypename(maindto5201.getSbusinesstypename());// Ԥ����������
		maindto.setSfundtypecode(maindto5201.getSfundtypecode());// �ʽ����ʱ���
		maindto.setSfundtypename(maindto5201.getSfundtypename());// �ʽ���������
		maindto.setSpaymode(maindto2252.getSpaytypecode());
		maindto.setSpaytypecode(maindto2252.getSpaytypecode());// ֧����ʽ����
		maindto.setSpaytypename(maindto2252.getSpaytypename());// ֧����ʽ����
		maindto.setSpayeeacct(maindto5201.getSpayeeacctno());// �տ����˺�
		maindto.setSpayeename(maindto5201.getSpayeeacctname());// �տ����˻�����
		maindto.setSagentacctbankname(maindto5201.getSpayeeacctbankname());// �տ�����
		maindto.setSpayeracct(maindto5201.getSpayacctno());// �����˺�
		maindto.setSpayername(maindto5201.getSpayacctname());// �����˻�����
		maindto.setSclearacctbankname(maindto5201.getSpayacctbankname());// ��������					
		maindto.setFamt(maindto2252.getNpayamt());// ����������
		maindto.setSpaybankname(maindto5201.getSpayacctbankname());// ������������
		maindto.setSagentbnkcode(maindto5201.getSpayeeacctbankno());// ���������к�
		maindto.setSremark(maindto2252.getSremark());// ժҪ
		maindto.setSmoneycorpcode("");// ���ڻ�������			
		maindto.setShold1(maindto2252.getShold1());// Ԥ���ֶ�1
		maindto.setShold2(maindto2252.getShold2());// Ԥ���ֶ�2
		maindto.setDentrustdate(DateUtil.currentDate());//ί������
		maindto.setDorientrustdate(CommonUtil.strToDate(maindto5201.getScommitdate()));//ԭί������
		maindto.setSpackno(maindto2252.getSpackageno());//����ˮ��
		maindto.setSoritrano(maindto5201.getSdealno());//ԭ������ˮ��
		maindto.setDacceptdate(CommonUtil.strToDate(dto.getScreatdate()));//��������
		maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);//�����ڱ�־
		maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//Ԥ������(Ĭ��Ԥ����)
		maindto.setDorivoudate(CommonUtil.strToDate(maindto5201.getSvoudate()));//ԭƾ֤����
		maindto.setSorivouno(maindto5201.getSvoucherno());//ԭƾ֤���
		maindto.setSpaydictateno(maindto2252.getSpaydictateno());// ���֧���˿�����
		maindto.setSpaymsgno(maindto2252.getSpaymsgno());// ֧�����ı��
		maindto.setDpayentrustdate(CommonUtil.strToDate(maindto2252.getSpayentrustdate()));// ֧��ί������
		maindto.setSpaysndbnkno(maindto2252.getSpaysndbnkno());// ֧���������к�
		maindto.setSfilename(maindto2252.getSfilename());
		maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬ ������
		maindto.setIstatinfnum(Integer.parseInt(maindto2252.getSext1()));
		maindto.setSxpaysndbnkno("");// ֧���������к�
		maindto.setSaddword(maindto2252.getSdemo());// ����
		maindto.setSbackflag(maindto2252.getSbackflag());
		maindto.setSrefundtype(maindto2252.getSrefundtype());
		maindto.setNbackmoney(maindto2252.getNpayamt());
		maindto.setSbckreason(maindto2252.getSdemo());
		maindto.setSxcleardate(CommonUtil.strToDate(maindto2252.getSpaydate()));// ��������
		maindto.setSxpayamt(null);
		maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳʱ��	
		return maindto;
	}
	
	/**
	 * ����ʵ���ʽ��˿�
	 * @param dto
	 * @param maindto2252
	 * @param main5207
	 * @return
	 */
	private static TvPayoutbackmsgMainDto copyPayOutMaindto2252(TvVoucherinfoDto dto,TfPaybankRefundmainDto maindto2252,TvPayoutmsgmainDto main5207){
		TvPayoutbackmsgMainDto mainDto = new TvPayoutbackmsgMainDto();
		mainDto.setSbizno(main5207.getSbizno());	//������ˮ��
		mainDto.setSorgcode(maindto2252.getSorgcode());	//��������
		mainDto.setScommitdate(maindto2252.getScommitdate());	//ί������
		mainDto.setSaccdate(maindto2252.getSpayentrustdate());	//��������
		mainDto.setSfilename(maindto2252.getSfilename());	//�����ļ���
		mainDto.setStrecode(maindto2252.getStrecode());	//�����������
		mainDto.setSpackageno(main5207.getSpackageno());	//����ˮ��
		mainDto.setSpayunit(main5207.getSpayunit());	//��Ʊ��λ
		mainDto.setSvouno(maindto2252.getSvoucherno());	//ƾ֤���
		mainDto.setSvoudate(maindto2252.getSvoudate());	//ƾ֤����
		mainDto.setSoritrano(main5207.getSdealno());	//������ˮ��
		mainDto.setSorientrustdate(maindto2252.getSpayentrustdate());	//ԭί������
		mainDto.setSorivouno(maindto2252.getSoriginalvoucherno());	//ԭ֧��ƾ֤���
		mainDto.setSorivoudate(main5207.getSgenticketdate());	//ԭ֧��ƾ֤����
		mainDto.setSpayeracct(main5207.getSrecacct());	//�������ʺ�
		mainDto.setSpayername(main5207.getSrecname());	//����������
		mainDto.setSpayeraddr("");	//
		mainDto.setNmoney(maindto2252.getNpayamt());	//���׽��
		mainDto.setSpayeebankno(main5207.getSinputrecbankno());	//�տ��˿���������
		mainDto.setSpayeeopbkno(main5207.getSinputrecbankno());	//�տ��˿���������
//		mainDto.setSpayeeopbkno(main5207.getSpayerbankname());	//�տ��˿���������
//		mainDto.setSpayeebankno(_spayeebankno);	//�տ��˿���������
//		mainDto.setSpayeebankno(_spayeebankno);	//�տ����к�
		mainDto.setSpayeeacct(main5207.getSpayeracct());	//�տ����ʺ�
		mainDto.setSpayeename(main5207.getSpayername());	//�տ�������
		mainDto.setSaddword(maindto2252.getSremark());	//����
		mainDto.setSbudgetunitcode(main5207.getSbudgetunitcode());	//Ԥ�㵥λ����
		mainDto.setSunitcodename(main5207.getSunitcodename());	//Ԥ�㵥λ����
		mainDto.setSofyear(maindto2252.getSstyear());	//ҵ�����
		mainDto.setStrimflag(main5207.getStrimflag());	//�����ڱ�־
		mainDto.setSbudgettype(main5207.getSbudgettype());	//Ԥ������
		mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬ ������
		mainDto.setSbackflag(maindto2252.getSbackflag());	//�˻ر�־
		mainDto.setSisreturn(maindto2252.getSbackflag());	//�Ƿ�������ƾ֤
		mainDto.setSbckreason(maindto2252.getSbckreason());	//�˻�ԭ��
		mainDto.setSdemo(maindto2252.getSremark());
		return mainDto;
	}
	
	private static TvPayoutbackmsgSubDto copyPayOutSubdto2252(TfPaybankRefundsubDto subdto2252,TvPayoutmsgsubDto sub5207,TvPayoutmsgmainDto main5207){
		TvPayoutbackmsgSubDto subDto = new TvPayoutbackmsgSubDto();
		subDto.setSbizno(sub5207.getSbizno());	//������ˮ��
		subDto.setSseqno(sub5207.getSseqno());	
		subDto.setSfunsubjectcode(sub5207.getSfunsubjectcode());	//���ܿ�Ŀ����
		subDto.setSfunsubjectname(sub5207.getSexpfuncname());	//���ܿ�Ŀ����
		subDto.setSbudgetprjcode(sub5207.getSbudgetprjcode());	//Ԥ����Ŀ����
		subDto.setNmoney(sub5207.getNmoney());	//���׽��
		subDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//����ʱ��
		subDto.setShold1(sub5207.getShold1());
		return subDto;
	}
	
	
	/**
	 * ����2252����
	 * @param subdto2252
	 * @param subdto5201
	 * @param maindto5201
	 * @return
	 */
	private static TvPayreckBankBackListDto copySubdto2252(TfPaybankRefundsubDto subdto2252,
			TfDirectpaymsgsubDto subdto5201,TfDirectpaymsgmainDto maindto5201){
		TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
		subdto.setIseqno(Integer.parseInt(subdto2252.getIseqno()+""));
		subdto.setSid(subdto2252.getSid());
		subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);//�˻�����
		subdto.setIvousrlno(subdto2252.getIvousrlno());// ƾ֤��ˮ�� 
		subdto.setSvoucherno(subdto5201.getSvoucherbillno());// �ӱ���ϸ���
		subdto.setSbdgorgcode(subdto5201.getSagencycode());// һ��Ԥ�㵥λ����
		subdto.setSsupdepname(subdto5201.getSagencyname());// һ��Ԥ�㵥λ����
		subdto.setSfuncbdgsbtcode(subdto5201.getSexpfunccode());// ֧�����ܷ����Ŀ����
		subdto.setSexpfuncname(subdto5201.getSexpfuncname());// ֧�����ܷ����Ŀ����
		subdto.setSecnomicsubjectcode(subdto5201.getSexpecocode());//���ÿ�Ŀ����
		subdto.setFamt(subdto2252.getNpayamt());// ֧�����
		subdto.setSpaysummaryname(subdto2252.getSremark());// ժҪ����
		subdto.setShold1(subdto2252.getShold1());// Ԥ���ֶ�1
		subdto.setShold2(subdto2252.getShold2());// Ԥ���ֶ�2
		subdto.setShold3(subdto2252.getShold3());// Ԥ���ֶ�3
		subdto.setShold4(subdto2252.getShold4());// Ԥ���ֶ�4
		subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));//����ʱ��
		subdto.setSorivouno(maindto5201.getSvoucherno());// ԭ֧��ƾ֤����
		subdto.setSorivoudetailno(subdto5201.getSvoucherbillno());// ԭ֧��ƾ֤��ϸ����
		subdto.setDorivoudate(CommonUtil.strToDate(maindto5201.getSvoudate()));
		subdto.setShold1(subdto5201.getSvoucherno());	//�洢5201��ϸ��voucherno  ����������2203ʱ����
		return subdto;
	}
	
	/**
	 * У���Ƿ��Ѳ�������
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	private static boolean voucherVerifyIsCopy(TvVoucherinfoDto dto) throws ITFEBizException{
		TvVoucherinfoDto tempdto=(TvVoucherinfoDto) dto.clone();
		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)){
			tempdto.setSvtcode(MsgConstant.VOUCHER_NO_2302);			
		}
		List list=VoucherUtil.findMainDtoByVoucher(tempdto);
		if(list==null||list.size()==0)
			return false;
		TvPayreckBankBackDto maindto=(TvPayreckBankBackDto) list.get(0);
		maindto.setSpackno(dto.getSpackno());
		try {
			DatabaseFacade.getODB().update(maindto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����ҵ��������Ϣ�쳣!",e);
		}
		return true;
	}

}
