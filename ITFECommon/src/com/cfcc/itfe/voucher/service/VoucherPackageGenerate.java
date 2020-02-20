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
	 * 分包模块 此处代码要整理做成一个公共处理的类
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
			// 根据凭证类型查找主表信息
			if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)||svtcode.equals(MsgConstant.VOUCHER_NO_5267)) {
				List mainDtoList = new ArrayList<TvPayoutmsgmainDto>();

				mainDtoList = DatabaseFacade.getDb().find(
						TvPayoutmsgmainDto.class, ls_SQL, params);
				List list = VoucherUtil.getListBySpayeebankno(mainDtoList);
				for (List<TvPayoutmsgmainDto> mainList : (List<List>) list) {

					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
					tmpPackNo = VoucherPackageGenerate.packageGenerate(vList,
							mainList.get(0).getSpayunit(), VoucherUtil
									.getOperationType(svtcode));
					// 更新业务主表信息
					for (TvPayoutmsgmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setScommitdate(TimeFacade.getCurrentStringTime());
					}
					TvPayoutmsgmainDto[] mainDtos = new TvPayoutmsgmainDto[mainList
							.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {

						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");

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
					throw new ITFEBizException("授权支付凭证分包后凭证大于1000条！");
				}
				// 凭证分包
				tmpPackNo = VoucherPackageGenerate.packageGenerate(
						voucherDtoList, mainDtoList.get(0).getSpayunit(),
						VoucherUtil.getOperationType(svtcode));
				// 更新业务主表信息
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
				// 更新凭证列表状态
				for (TvVoucherinfoDto dto : voucherDtoList) {

					dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
					dto.setSdemo("已发送");

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
				// 凭证分包
				tmpPackNo = VoucherPackageGenerate.packageGenerate(
						voucherDtoList, mainDtoList.get(0).getSpayunit(),
						VoucherUtil.getOperationType(svtcode));
				// 更新业务主表信息
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
				// 更新凭证列表状态
				for (TvVoucherinfoDto dto : voucherDtoList) {
					dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
					dto.setSdemo("已发送");
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

					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
					tmpPackNo = VoucherPackageGenerate.packageGenerate(vList,
							mainList.get(0).getStaxorgcode(), VoucherUtil
									.getOperationType(svtcode));
					// 更新业务主表信息
					for (TvDwbkDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
					}
					TvDwbkDto[] mainDtos = new TvDwbkDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {

						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");

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

					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
					tmpPackNo = VoucherPackageGenerate.packageGenerate(vList,
							mainList.get(0).getSagentbnkcode(), VoucherUtil
									.getOperationType(svtcode));
					// 更新业务主表信息
					StringBuffer updatesqlbf = new StringBuffer("update TV_PAYRECK_BANK set S_PACKNO='"+tmpPackNo+"' where ");
					for (TvPayreckBankDto dto : mainList) {
						updatesqlbf.append(" ( I_VOUSRLNO = '"+dto.getIvousrlno().toString()+"' ) or");
					}
					String updatesql = updatesqlbf.toString();
					updatesql = updatesql.substring(0, updatesql.length() - 2);
					DatabaseFacade.getODB().execSql(updatesql);
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");
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

					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
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
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");
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
				//按照代理银行号进行分包
				List list = VoucherUtil.getListBySpaybankcode(mainDtoList);
				for (List<TfDirectpaymsgmainDto> mainList : (List<List>) list) {
					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
					TfDirectpaymsgmainDto mainDto=mainList.get(0);
					//出票单位：批量业务取代理银行行号，单笔业务取财政机构代码
					String Spayunit=mainDto.getSbusinesstypecode().equals(StateConstant.BIZTYPE_CODE_BATCH)?
							mainDto.getSpayeeacctbankno():mainDto.getSfinorgcode();
					//获取包号		
					tmpPackNo = packageGenerate(vList,Spayunit, MsgConstant.VOUCHER_NO_5201);
					// 更新业务主表信息
					for (TfDirectpaymsgmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setScommitdate(TimeFacade.getCurrentStringTime());
					}
					TfDirectpaymsgmainDto[] mainDtos = new TfDirectpaymsgmainDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");
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
				//按照支付发起行行号进行分包
				List list = VoucherUtil.getListBySpaysndbnkno(mainDtoList);
				for (List<TfPaybankRefundmainDto> mainList : (List<List>) list) {
					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
					TfPaybankRefundmainDto mainDto=mainList.get(0);
					//获取包号		
					tmpPackNo = packageGenerate(vList,mainDto.getSpaysndbnkno(), svtcode);
					// 更新业务主表信息
					for (TfPaybankRefundmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setScommitdate(TimeFacade.getCurrentStringTime());
					}
					TfPaybankRefundmainDto[] mainDtos = new TfPaybankRefundmainDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					copyTable(vList.get(0), mainList);
					if(ITFECommonConstant.PUBLICPARAM.indexOf("sh,") > 0 && !("91".equals(vList.get(0).getShold2().trim()))){
						
					
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");
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
				//按照支付发起行行号进行分包
				List list = VoucherUtil.getListByCorrhandbook(mainDtoList);
				for (List<TvInCorrhandbookDto> mainList : (List<List>) list) {
					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
					TvInCorrhandbookDto mainDto=mainList.get(0);
					//获取包号		
					tmpPackNo = packageGenerate(vList,mainDto.getSfinorgcode(), svtcode);
					// 更新业务主表信息
					for (TvInCorrhandbookDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
					}
					TvInCorrhandbookDto[] mainDtos = new TvInCorrhandbookDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");
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
				//按照支付发起行行号进行分包
				List list = VoucherUtil.getListByNontaxIncome(mainDtoList);
				for (List<TvNontaxmainDto> mainList : (List<List>) list) {
					// 根据主表查找凭证列表
					List<TvVoucherinfoDto> vList = VoucherUtil
							.getVoucherByMainDtoList(mainList, svtcode);
					// 凭证分包
					TvNontaxmainDto mainDto=mainList.get(0);
					//获取包号		
					tmpPackNo = packageGenerate(vList,mainDto.getSfinorgcode(), svtcode);
					// 更新业务主表信息
					for (TvNontaxmainDto dto : mainList) {
						dto.setSpackageno(tmpPackNo);
						dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
					}
					TvNontaxmainDto[] mainDtos = new TvNontaxmainDto[mainList.size()];
					mainDtos = mainList.toArray(mainDtos);
					DatabaseFacade.getODB().update(mainDtos);
					// 发送报文
					vList.get(0).setSpackno(tmpPackNo);
					VoucherUtil.sendTips(vList.get(0));
					// 更新凭证列表状态
					for (TvVoucherinfoDto dto : vList) {
						dto.setSstatus(DealCodeConstants.VOUCHER_SENDED);
						dto.setSdemo("已发送");
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
			logger.error("凭证分包出现异常：" + e.getMessage(), e);
			throw new ITFEBizException("凭证分包出现异常！", e);
		} catch (JAFDatabaseException e) {
			logger.error("查找主表信息或更新凭证状态异常！", e);
			throw new ITFEBizException("查找主表信息或更新凭证状态异常！", e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("凭证分包出现异常！", e);
		}
		return count;
	}

	// 凭证分包
	public static String packageGenerate(List list, String Spayunit,
			String operationtypecode) throws ITFEBizException {

		int count = 0;
		String tmpPackNo = "";
		try {
			List<TvFilepackagerefDto> filepackagerefDtoList = new ArrayList();
			String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统日期
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
			packdto.setSorgcode(dto.getSorgcode());// 根据国库代码得出
			// 国库主体代码
			packdto.setStrecode(ls_TreCode);
			packdto.setSfilename(dto.getSfilename());
			// 征收机关代码
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
			logger.error("添加创建分包时出现异常：" + e);
			throw new ITFEBizException("添加创建分包时出现异常！", e);
		} catch (Exception e) {
			logger.error("添加创建分包时出现异常：" + e);
			throw new ITFEBizException("添加创建分包时出现异常！", e);
		}
		return tmpPackNo;
	}
	
	
	
	
	
	/**
	 * 复制表数据
	 * @param dto
	 * @param maindtoList
	 * @throws ITFEBizException
	 */
	private static void copyTable(TvVoucherinfoDto dto,List maindtoList) throws ITFEBizException{
		List copyMaindtoList=new ArrayList();
		List copySubdtoList=new ArrayList();
		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252) && !("91".equals(dto.getShold2().trim()))){
			//直接支付退款
			for(TfPaybankRefundmainDto maindto2252:(List<TfPaybankRefundmainDto>) maindtoList){
				if(voucherVerifyIsCopy(dto))
					continue;
				List list = VoucherUtil.findVoucherDto(maindto2252.getStrecode(),MsgConstant.VOUCHER_NO_5201 , 
						maindto2252.getSoriginalvoucherno(), DealCodeConstants.VOUCHER_SUCCESS);
				if(list==null||list.size()==0){
					throw new ITFEBizException("未找到对应的"+MsgConstant.VOUCHER_NO_5201+"凭证。");
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
			//实拨资金退款
			for(TfPaybankRefundmainDto maindto2252:(List<TfPaybankRefundmainDto>) maindtoList){
				//查找原交易
				List list = VoucherUtil.findVoucherDto(maindto2252.getStrecode(),MsgConstant.VOUCHER_NO_5207 , 
						maindto2252.getSoriginalvoucherno(), DealCodeConstants.VOUCHER_SUCCESS);
				if(list==null||list.size()==0){
					throw new ITFEBizException("未找到对应的" + MsgConstant.VOUCHER_NO_5207 + "凭证。");
				}
				TvVoucherinfoDto tmpInfoDto = (TvVoucherinfoDto) ((TvVoucherinfoDto) list.get(0)).clone();
				tmpInfoDto.setSvtcode(MsgConstant.VOUCHER_NO_3208);
				List payOutList=VoucherUtil.findMainDtoByVoucher(tmpInfoDto);
				//判断是否已经复制
				if(null != payOutList && payOutList.size() > 0)
					continue;
				TvPayoutmsgmainDto mainDto5207 = (TvPayoutmsgmainDto) VoucherUtil.findMainDtoByVoucher((TvVoucherinfoDto) list.get(0)).get(0);
				copyMaindtoList.add(copyPayOutMaindto2252(dto, maindto2252, mainDto5207));
				//处理子表
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
			throw new ITFEBizException("写入主表信息异常!");
		}
	}
	
	/**
	 * 复制2252数据
	 * @param dto
	 * @param maindto2252
	 * @param maindto5201
	 * @return
	 */
	private static TvPayreckBankBackDto copyMaindto2252(TvVoucherinfoDto dto,
			TfPaybankRefundmainDto maindto2252,TfDirectpaymsgmainDto maindto5201){
		TvPayreckBankBackDto maindto=new TvPayreckBankBackDto();
		maindto.setStrano(maindto2252.getSdealno());// 申请划款凭证Id
		maindto.setIvousrlno(maindto2252.getIvousrlno());// 凭证流水号
		maindto.setSid(maindto2252.getSid());// 申请划款凭证Id
		maindto.setSadmdivcode(maindto2252.getSadmdivcode());//行政区划代码
		maindto.setSofyear(maindto2252.getSstyear());// 业务年度
		maindto.setSvtcode(maindto2252.getSvtcode());//凭证类型编号
		maindto.setDvoudate(CommonUtil.strToDate(maindto2252.getSvoudate())); // 凭证日期
		maindto.setSvouno(maindto2252.getSvoucherno());// 凭证号
		maindto.setSbookorgcode(maindto2252.getSorgcode());//核算主体代码
		maindto.setStrecode(maindto2252.getStrecode()); // 国库主体代码
		maindto.setSfinorgcode(maindto2252.getSfinorgcode());// 财政机关代码
		maindto.setSbgttypecode(maindto5201.getSbgttypecode());// 预算类型编码
		maindto.setSbgttypename(maindto5201.getSbusinesstypename());// 预算类型名称
		maindto.setSfundtypecode(maindto5201.getSfundtypecode());// 资金性质编码
		maindto.setSfundtypename(maindto5201.getSfundtypename());// 资金性质名称
		maindto.setSpaymode(maindto2252.getSpaytypecode());
		maindto.setSpaytypecode(maindto2252.getSpaytypecode());// 支付方式编码
		maindto.setSpaytypename(maindto2252.getSpaytypename());// 支付方式名称
		maindto.setSpayeeacct(maindto5201.getSpayeeacctno());// 收款人账号
		maindto.setSpayeename(maindto5201.getSpayeeacctname());// 收款人账户名称
		maindto.setSagentacctbankname(maindto5201.getSpayeeacctbankname());// 收款银行
		maindto.setSpayeracct(maindto5201.getSpayacctno());// 付款账号
		maindto.setSpayername(maindto5201.getSpayacctname());// 付款账户名称
		maindto.setSclearacctbankname(maindto5201.getSpayacctbankname());// 付款银行					
		maindto.setFamt(maindto2252.getNpayamt());// 汇总清算金额
		maindto.setSpaybankname(maindto5201.getSpayacctbankname());// 代理银行名称
		maindto.setSagentbnkcode(maindto5201.getSpayeeacctbankno());// 代理银行行号
		maindto.setSremark(maindto2252.getSremark());// 摘要
		maindto.setSmoneycorpcode("");// 金融机构编码			
		maindto.setShold1(maindto2252.getShold1());// 预留字段1
		maindto.setShold2(maindto2252.getShold2());// 预留字段2
		maindto.setDentrustdate(DateUtil.currentDate());//委托日期
		maindto.setDorientrustdate(CommonUtil.strToDate(maindto5201.getScommitdate()));//原委托日期
		maindto.setSpackno(maindto2252.getSpackageno());//包流水号
		maindto.setSoritrano(maindto5201.getSdealno());//原交易流水号
		maindto.setDacceptdate(CommonUtil.strToDate(dto.getScreatdate()));//接收日期
		maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);//调整期标志
		maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//预算种类(默认预算内)
		maindto.setDorivoudate(CommonUtil.strToDate(maindto5201.getSvoudate()));//原凭证日期
		maindto.setSorivouno(maindto5201.getSvoucherno());//原凭证编号
		maindto.setSpaydictateno(maindto2252.getSpaydictateno());// 大额支付退款交易序号
		maindto.setSpaymsgno(maindto2252.getSpaymsgno());// 支付报文编号
		maindto.setDpayentrustdate(CommonUtil.strToDate(maindto2252.getSpayentrustdate()));// 支付委托日期
		maindto.setSpaysndbnkno(maindto2252.getSpaysndbnkno());// 支付发起行行号
		maindto.setSfilename(maindto2252.getSfilename());
		maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态 处理中
		maindto.setIstatinfnum(Integer.parseInt(maindto2252.getSext1()));
		maindto.setSxpaysndbnkno("");// 支付发起行行号
		maindto.setSaddword(maindto2252.getSdemo());// 附言
		maindto.setSbackflag(maindto2252.getSbackflag());
		maindto.setSrefundtype(maindto2252.getSrefundtype());
		maindto.setNbackmoney(maindto2252.getNpayamt());
		maindto.setSbckreason(maindto2252.getSdemo());
		maindto.setSxcleardate(CommonUtil.strToDate(maindto2252.getSpaydate()));// 清算日期
		maindto.setSxpayamt(null);
		maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 系统时间	
		return maindto;
	}
	
	/**
	 * 复制实拨资金退款
	 * @param dto
	 * @param maindto2252
	 * @param main5207
	 * @return
	 */
	private static TvPayoutbackmsgMainDto copyPayOutMaindto2252(TvVoucherinfoDto dto,TfPaybankRefundmainDto maindto2252,TvPayoutmsgmainDto main5207){
		TvPayoutbackmsgMainDto mainDto = new TvPayoutbackmsgMainDto();
		mainDto.setSbizno(main5207.getSbizno());	//发送流水号
		mainDto.setSorgcode(maindto2252.getSorgcode());	//机构代码
		mainDto.setScommitdate(maindto2252.getScommitdate());	//委托日期
		mainDto.setSaccdate(maindto2252.getSpayentrustdate());	//帐务日期
		mainDto.setSfilename(maindto2252.getSfilename());	//导入文件名
		mainDto.setStrecode(maindto2252.getStrecode());	//国库主体代码
		mainDto.setSpackageno(main5207.getSpackageno());	//包流水号
		mainDto.setSpayunit(main5207.getSpayunit());	//出票单位
		mainDto.setSvouno(maindto2252.getSvoucherno());	//凭证编号
		mainDto.setSvoudate(maindto2252.getSvoudate());	//凭证日期
		mainDto.setSoritrano(main5207.getSdealno());	//交易流水号
		mainDto.setSorientrustdate(maindto2252.getSpayentrustdate());	//原委托日期
		mainDto.setSorivouno(maindto2252.getSoriginalvoucherno());	//原支出凭证编号
		mainDto.setSorivoudate(main5207.getSgenticketdate());	//原支出凭证日期
		mainDto.setSpayeracct(main5207.getSrecacct());	//付款人帐号
		mainDto.setSpayername(main5207.getSrecname());	//付款人名称
		mainDto.setSpayeraddr("");	//
		mainDto.setNmoney(maindto2252.getNpayamt());	//交易金额
		mainDto.setSpayeebankno(main5207.getSinputrecbankno());	//收款人开户行名称
		mainDto.setSpayeeopbkno(main5207.getSinputrecbankno());	//收款人开户行名称
//		mainDto.setSpayeeopbkno(main5207.getSpayerbankname());	//收款人开户行名称
//		mainDto.setSpayeebankno(_spayeebankno);	//收款人开户行名称
//		mainDto.setSpayeebankno(_spayeebankno);	//收款行行号
		mainDto.setSpayeeacct(main5207.getSpayeracct());	//收款人帐号
		mainDto.setSpayeename(main5207.getSpayername());	//收款人名称
		mainDto.setSaddword(maindto2252.getSremark());	//附言
		mainDto.setSbudgetunitcode(main5207.getSbudgetunitcode());	//预算单位代码
		mainDto.setSunitcodename(main5207.getSunitcodename());	//预算单位名称
		mainDto.setSofyear(maindto2252.getSstyear());	//业务年度
		mainDto.setStrimflag(main5207.getStrimflag());	//调整期标志
		mainDto.setSbudgettype(main5207.getSbudgettype());	//预算种类
		mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态 处理中
		mainDto.setSbackflag(maindto2252.getSbackflag());	//退回标志
		mainDto.setSisreturn(maindto2252.getSbackflag());	//是否生成了凭证
		mainDto.setSbckreason(maindto2252.getSbckreason());	//退回原因
		mainDto.setSdemo(maindto2252.getSremark());
		return mainDto;
	}
	
	private static TvPayoutbackmsgSubDto copyPayOutSubdto2252(TfPaybankRefundsubDto subdto2252,TvPayoutmsgsubDto sub5207,TvPayoutmsgmainDto main5207){
		TvPayoutbackmsgSubDto subDto = new TvPayoutbackmsgSubDto();
		subDto.setSbizno(sub5207.getSbizno());	//发送流水号
		subDto.setSseqno(sub5207.getSseqno());	
		subDto.setSfunsubjectcode(sub5207.getSfunsubjectcode());	//功能科目代码
		subDto.setSfunsubjectname(sub5207.getSexpfuncname());	//功能科目名称
		subDto.setSbudgetprjcode(sub5207.getSbudgetprjcode());	//预算项目代码
		subDto.setNmoney(sub5207.getNmoney());	//交易金额
		subDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//更新时间
		subDto.setShold1(sub5207.getShold1());
		return subDto;
	}
	
	
	/**
	 * 复制2252数据
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
		subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);//账户性质
		subdto.setIvousrlno(subdto2252.getIvousrlno());// 凭证流水号 
		subdto.setSvoucherno(subdto5201.getSvoucherbillno());// 子表明细序号
		subdto.setSbdgorgcode(subdto5201.getSagencycode());// 一级预算单位编码
		subdto.setSsupdepname(subdto5201.getSagencyname());// 一级预算单位名称
		subdto.setSfuncbdgsbtcode(subdto5201.getSexpfunccode());// 支出功能分类科目编码
		subdto.setSexpfuncname(subdto5201.getSexpfuncname());// 支出功能分类科目名称
		subdto.setSecnomicsubjectcode(subdto5201.getSexpecocode());//经济科目代码
		subdto.setFamt(subdto2252.getNpayamt());// 支付金额
		subdto.setSpaysummaryname(subdto2252.getSremark());// 摘要名称
		subdto.setShold1(subdto2252.getShold1());// 预留字段1
		subdto.setShold2(subdto2252.getShold2());// 预留字段2
		subdto.setShold3(subdto2252.getShold3());// 预留字段3
		subdto.setShold4(subdto2252.getShold4());// 预留字段4
		subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));//更新时间
		subdto.setSorivouno(maindto5201.getSvoucherno());// 原支付凭证单号
		subdto.setSorivoudetailno(subdto5201.getSvoucherbillno());// 原支付凭证明细单号
		subdto.setDorivoudate(CommonUtil.strToDate(maindto5201.getSvoudate()));
		subdto.setShold1(subdto5201.getSvoucherno());	//存储5201明细的voucherno  给财政发送2203时必填
		return subdto;
	}
	
	/**
	 * 校验是否已插入主表
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
			throw new ITFEBizException("更新业务主表信息异常!",e);
		}
		return true;
	}

}
