package com.cfcc.itfe.service.recbiz.backvoucher;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.IVoucherDto2Map;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3208;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3210;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3268;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3251;
import com.cfcc.itfe.voucher.service.VoucherFactory;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Tyler
 * @time 14-04-01 16:47:31 codecomment:
 */

public class BackVoucherService extends AbstractBackVoucherService {
	private static Log log = LogFactory.getLog(BackVoucherService.class);

	/**
	 * ����ƾ֤
	 */
	@SuppressWarnings("unchecked")
	public void voucherGenerate(List params) throws ITFEBizException {
		String type = (String) params.get(0);
		TvVoucherinfoDto vdto = (TvVoucherinfoDto) params.get(2);
		if (type.equals(MsgConstant.VOUCHER_NO_3208)) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = (TvPayoutmsgmainDto) params
					.get(1);
			tvPayoutmsgmainDto.setSbackflag("1");
			// ��װ����
			Map map = new VoucherDto2MapFor3208().tranfor(params);
			// �����ļ�
			VoucherUtil.sendTips(vdto, map);
			try {
				// ����ҵ����־�ֶ�
				String sqlStr = "UPDATE TV_PAYOUTMSGMAIN SET S_BACKFLAG = '1' where S_BIZNO = '"
						+ tvPayoutmsgmainDto.getSbizno() + "'";
				DatabaseFacade.getDb().execSql(sqlStr);
				DatabaseFacade.getDb().execSql(sqlStr.replace("TV_PAYOUTMSGMAIN", "HTV_PAYOUTMSGMAIN"));
				// ����������
				vdto.setSext4(tvPayoutmsgmainDto.getSbizno());
				DatabaseFacade.getDb().create(vdto);
			} catch (JAFDatabaseException e) {
				log.error(e);
				throw new ITFEBizException("����ҵ���������򱣴����������");
			}
		}else if(type.equals(MsgConstant.VOUCHER_NO_3268)) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = (TvPayoutmsgmainDto) params
			.get(1);
			tvPayoutmsgmainDto.setSbackflag("1");
			// ��װ����
			Map map = new VoucherDto2MapFor3268().tranfor(params);
			// �����ļ�
			VoucherUtil.sendTips(vdto, map);
			try {
				// ����ҵ����־�ֶ�
				String sqlStr = "UPDATE TV_PAYOUTMSGMAIN SET S_BACKFLAG = '1' where S_BIZNO = '"
						+ tvPayoutmsgmainDto.getSbizno() + "'";
				DatabaseFacade.getDb().execSql(sqlStr);
				DatabaseFacade.getDb().execSql(sqlStr.replace("TV_PAYOUTMSGMAIN", "HTV_PAYOUTMSGMAIN"));
				// ����������
				vdto.setSext4(tvPayoutmsgmainDto.getSbizno());
				DatabaseFacade.getDb().create(vdto);
			} catch (JAFDatabaseException e) {
				log.error(e);
				throw new ITFEBizException("����ҵ���������򱣴����������");
			}
		} else if (type.equals(MsgConstant.VOUCHER_NO_3210)|| type.equals(MsgConstant.VOUCHER_NO_3251)) {
			TvDwbkDto tvDwbkDto = (TvDwbkDto) params.get(1);
			tvDwbkDto.setCbckflag("1");
			// ��װ����
			Map map ;
				if(ITFECommonConstant.PUBLICPARAM.contains(",jilin,")){
					map = new VoucherDto2MapFor3251().tranfor(params);
				}else{
					map = new VoucherDto2MapFor3210().tranfor(params);
				}
			// �����ļ�
			VoucherUtil.sendTips(vdto, map);
			try {
				// ����ҵ����־�ֶ�
				String sqlStr = "UPDATE TV_DWBK SET C_BCKFLAG = '1' where I_VOUSRLNO = "
						+ tvDwbkDto.getIvousrlno()
						+ " and S_BOOKORGCODE = '"
						+ tvDwbkDto.getSbookorgcode() + "'";
				DatabaseFacade.getDb().execSql(sqlStr);
				// ����������
				vdto.setSext4(tvDwbkDto.getSbizno());
				DatabaseFacade.getDb().create(vdto);
			} catch (JAFDatabaseException e) {
				log.error(e);
				throw new ITFEBizException("����ҵ���������򱣴����������");
			}
		}else if (type.equals(MsgConstant.VOUCHER_NO_2203)) {
			IVoucherDto2Map voucher = (IVoucherDto2Map) ContextFactory
					.getApplicationContext().getBean(
							MsgConstant.VOUCHER_DTO2MAP + type);
			Map map = voucher.tranfor(params);
			// ǰ�÷���ģ���Ҫ��5201���ݸ��Ƶ� ����ֱ��֧���˿���У������������2302
			IDto idto = (IDto) params.get(1);
			TvVoucherinfoDto _dto = (TvVoucherinfoDto) map.get("voucherDto");
			if (idto instanceof TfDirectpaymsgmainDto) {
				try {
					copyMain5201to2202(_dto, (TfDirectpaymsgmainDto) idto);
					copySub5201to2202((TfDirectpaymsgmainDto) idto);
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("ǰ������ֱ��֧���˿�ʱ ����5201�����", e);
				}

			}
		}
	}

	public TvVoucherinfoDto getTvVoucherinfoDto(List params)
			throws ITFEBizException {
		String type = (String) params.get(0);
		TsConvertfinorgDto cforgDto = (TsConvertfinorgDto) params.get(1);
		BigDecimal nMoney = (BigDecimal) params.get(2);

		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		String dirsep = File.separator;
		String currentDate = TimeFacade.getCurrentStringTime();
		Timestamp now = new Timestamp(TimeFacade.getCurrentDateTime().getTime());
		String number1 = VoucherUtil.getGrantSequence();
		String FileName = ITFECommonConstant.FILE_ROOT_PATH + File.separator
				+ "Voucher" + File.separator + currentDate + File.separator
				+ "send" + type + "_" + number1 + ".msg";

		vDto.setSdealno(number1);
		vDto.setSorgcode(cforgDto.getSorgcode());
		vDto.setStrecode(cforgDto.getStrecode());
		vDto.setSfilename(FileName);
		vDto.setSadmdivcode(cforgDto.getSadmdivcode());
		vDto.setSstyear(currentDate.substring(0, 4));
		vDto.setSvtcode(type);
		vDto.setSvoucherno(number1);
		vDto.setSvoucherflag("0");
		if (type.equals(MsgConstant.VOUCHER_NO_3208)) {
			vDto.setSattach(MsgConstant.VOUCHER_NO_5207);
		} else if (type.equals(MsgConstant.VOUCHER_NO_3268)) {
			vDto.setSattach(MsgConstant.VOUCHER_NO_5267);
		}else if (type.equals(MsgConstant.VOUCHER_NO_3210)) {
			vDto.setSattach(MsgConstant.VOUCHER_NO_5209);
		}
		vDto.setScreatdate(currentDate);
		vDto.setNmoney(nMoney);
		vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		vDto.setSdemo("����ɹ�");
		vDto.setTssysupdate(now);
		vDto.setSrecvtime(now);
		//�������TCBS��ִ�����ʵ���ʽ��˿�,��Ҫ��ע���ʻ�������ҵ��(�����Ϻ��������ط�Ĭ�϶��ǵ���ҵ��--1)
		if(params.size()>=5&&params.get(4).toString().trim().equals("TCBS")){
			TvPayoutbackmsgMainDto dto = (TvPayoutbackmsgMainDto) params.get(3);
			//����������־������ - 1 �������˿� - 4
			if(StringUtils.isNotBlank(dto.getShold4())&&dto.getShold4().trim().equals(StateConstant.BIZTYPE_CODE_BATCH)){
				vDto.setShold4(StateConstant.BIZTYPE_CODE_BATCH);// ����Ϊ����ҵ��
			}else{
				vDto.setShold4(StateConstant.BIZTYPE_CODE_SINGLE);// ����Ϊ����ҵ��
			}
		}
		
		return vDto;
	}

	/**
	 * voucherGenerate
	 * 
	 * @generated
	 * @param list
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException
	 */
	public MulitTableDto voucherGenerateForTCBS(List list)
			throws ITFEBizException {
		int successcount = 0;
		int failcount = 0;
		MulitTableDto mulitTableDto = new MulitTableDto();
		List<String> errorList = new ArrayList<String>();
		String type = (String) list.get(0);// ҵ������
		TvPayoutbackmsgMainDto querydto = (TvPayoutbackmsgMainDto) list.get(1);// ��ѯdto
		// У������Ӧ�Ĳ���������Ϣ�����������Ƿ�ά��
		TsConvertfinorgDto cDto = new TsConvertfinorgDto();
		cDto.setSorgcode(querydto.getSorgcode());
		cDto.setStrecode(querydto.getStrecode());
		List finorgDtoList = null;
		try {
			finorgDtoList = CommonFacade.getODB().findRsByDto(cDto);
		} catch (JAFDatabaseException e2) {
			log.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��", e2);
		} catch (ValidateException e2) {
			log.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��", e2);
		}
		if (finorgDtoList == null || finorgDtoList.size() == 0) {
			throw new ITFEBizException("���⣺" + cDto.getStrecode()
					+ " ��Ӧ�Ĳ�������δά����");
		}
		cDto = (TsConvertfinorgDto) finorgDtoList.get(0);
		if (cDto.getSadmdivcode() == null || cDto.getSadmdivcode().equals("")) {
			throw new ITFEBizException("���⣺" + cDto.getStrecode()
					+ " ��Ӧ����������δά����ƾ֤δ���ɣ�");
		}
		// ����ƾ֤
		if (type.equals(MsgConstant.VOUCHER_NO_3208)||type.equals(MsgConstant.VOUCHER_NO_3268)) {
			List<TvPayoutbackmsgMainDto> voucherlist = (List<TvPayoutbackmsgMainDto>) list
					.get(2);// ����ƾ֤list
			for (int i = 0; i < voucherlist.size(); i++) {
				TvPayoutbackmsgMainDto dto = voucherlist.get(i);
				try {
					// ��װƾ֤������dto
					List param = new ArrayList();
					param.add(type);
					param.add(cDto);
					param.add(dto.getNmoney());
					param.add(dto);
					param.add("TCBS");
					TvVoucherinfoDto voucherdto = getTvVoucherinfoDto(param);
					voucherdto.setSvoucherno(dto.getSvouno());
					// ��װ����
					Map map = null;
					if(type.equals(MsgConstant.VOUCHER_NO_3208))
						map = new VoucherDto2MapFor3208().tranforForTCBS(dto,voucherdto);
					else
						map = new VoucherDto2MapFor3268().tranforForTCBS(dto,voucherdto);
					// �����ļ�
					VoucherUtil.sendTips(voucherdto, map);
					dto.setSisreturn(StateConstant.COMMON_YES);
					DatabaseFacade.getODB().update(dto);
					DatabaseFacade.getODB().create(voucherdto);
					successcount++;
				} catch (JAFDatabaseException e2) {
					log.error(e2);
					throw new ITFEBizException("��ѯ��Ϣ�쳣��", e2);
				} catch (Exception e2) {
					log.error(e2);
					throw new ITFEBizException(e2.getMessage(), e2);
				}
			}
		} else if (type.equals(MsgConstant.MSG_NO_2202)) {
			type = MsgConstant.VOUCHER_NO_2203;
			IVoucherDto2Map voucher = (IVoucherDto2Map) ContextFactory
					.getApplicationContext().getBean(
							MsgConstant.VOUCHER_DTO2MAP + type);
			List<TvPayreckBankBackDto> list2202 = (List) list.get(2);
			for (TvPayreckBankBackDto maindto : list2202) {
				List newList2202 = new ArrayList();
				newList2202.add(type);
				newList2202.add(maindto);
				voucher.tranfor(newList2202);
			}
			successcount = list2202.size();
		}
		mulitTableDto.setTotalCount(successcount);
		mulitTableDto.setErrorCount(failcount);
		mulitTableDto.setErrorList(errorList);
		return mulitTableDto;
	}

	/**
	 * ʵ���ʽ��˿�(TCBS�ʽ��ļ�����)voucherGenerateForPayoutBack
	 * 
	 * @generated
	 * @param list
	 * @return list
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherGenerateForPayoutBack(List list) throws ITFEBizException {

		return VoucherFactory.voucherGenerateForPayoutBack(list);
	}

	/**
	 * �����˿���Ϣ
	 */
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {
		try {
			SQLExecutor sqlExec = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			SQLResults rs = sqlExec.runQueryCloseCon(getSQL(dto),
					TfDirectpaymsgmainDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	private String getSQL(TvVoucherinfoDto dto) {
		return " SELECT * FROM TF_DIRECTPAYMSGMAIN WHERE S_TRECODE='"
				+ dto.getStrecode()
				+ "' AND "
				+ " S_VOUCHERNO='"
				+ dto.getSvoucherno()
				+ "' "
				+ (StringUtils.isBlank(dto.getScreatdate()) ? ""
						: "AND S_VOUDATE='" + dto.getScreatdate() + "' ")
				+ " AND S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' "
				+ (dto.getNmoney() == null ? "" : " AND N_PAYAMT="
						+ dto.getNmoney())
				+ " AND EXISTS ( SELECT 1 FROM "
				+ " TV_VOUCHERINFO WHERE TV_VOUCHERINFO.S_DEALNO=TF_DIRECTPAYMSGMAIN.I_VOUSRLNO AND TV_VOUCHERINFO.S_VTCODE"
				+ " ='"
				+ MsgConstant.VOUCHER_NO_5201
				+ "' AND TV_VOUCHERINFO.S_STATUS='"
				+ DealCodeConstants.VOUCHER_SUCCESS
				+ "')"
				+ " UNION ALL "
				+ " SELECT * FROM TF_DIRECTPAYMSGMAIN WHERE S_TRECODE='"
				+ dto.getStrecode()
				+ "' AND "
				+ " S_VOUCHERNO='"
				+ dto.getSvoucherno()
				+ "' "
				+ (StringUtils.isBlank(dto.getScreatdate()) ? ""
						: "AND S_VOUDATE='" + dto.getScreatdate() + "' ")
				+ " AND S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' "
				+ (dto.getNmoney() == null ? "" : " AND N_PAYAMT="
						+ dto.getNmoney())
				+ " AND NOT EXISTS ( SELECT 1 FROM "
				+ " TV_VOUCHERINFO WHERE TV_VOUCHERINFO.S_DEALNO=TF_DIRECTPAYMSGMAIN.I_VOUSRLNO )";
	}

	/**
	 * ����5201��������,����ǰ�������˿�
	 * 
	 * @param dto
	 * @param maindto5201
	 * @return
	 * @throws JAFDatabaseException
	 */
	private static TvPayreckBankBackDto copyMain5201to2202(
			TvVoucherinfoDto dto, TfDirectpaymsgmainDto maindto5201)
			throws JAFDatabaseException {
		TvPayreckBankBackDto maindto = new TvPayreckBankBackDto();
		maindto.setStrano(maindto5201.getSdealno());// ���뻮��ƾ֤Id
		maindto.setIvousrlno(maindto5201.getIvousrlno());// ƾ֤��ˮ��
		maindto.setSid(maindto5201.getSid());// ���뻮��ƾ֤Id
		maindto.setSadmdivcode(maindto5201.getSadmdivcode());// ������������
		maindto.setSofyear(maindto5201.getSstyear());// ҵ�����
		maindto.setSvtcode(dto.getSvtcode());// ƾ֤���ͱ��
		maindto.setDvoudate(CommonUtil.strToDate(dto.getScreatdate())); // ƾ֤����
		maindto.setSvouno(dto.getSvoucherno());// ƾ֤��
		maindto.setSbookorgcode(maindto5201.getSorgcode());// �����������
		maindto.setStrecode(maindto5201.getStrecode()); // �����������
		maindto.setSfinorgcode(maindto5201.getSfinorgcode());// �������ش���
		maindto.setSbgttypecode(maindto5201.getSbgttypecode());// Ԥ�����ͱ���
		maindto.setSbgttypename(maindto5201.getSbusinesstypename());// Ԥ����������
		maindto.setSfundtypecode(maindto5201.getSfundtypecode());// �ʽ����ʱ���
		maindto.setSfundtypename(maindto5201.getSfundtypename());// �ʽ���������
		maindto.setSpaymode(maindto5201.getSpaytypecode());
		maindto.setSpaytypecode(maindto5201.getSpaytypecode());// ֧����ʽ����
		maindto.setSpaytypename(maindto5201.getSpaytypename());// ֧����ʽ����
		maindto.setSpayeeacct(maindto5201.getSpayeeacctno());// �տ����˺�
		maindto.setSpayeename(maindto5201.getSpayeeacctname());// �տ����˻�����
		maindto.setSagentacctbankname(maindto5201.getSpayeeacctbankname());// �տ�����
		maindto.setSpayeracct(maindto5201.getSpayacctno());// �����˺�
		maindto.setSpayername(maindto5201.getSpayacctname());// �����˻�����
		maindto.setSclearacctbankname(maindto5201.getSpayacctbankname());// ��������
		maindto.setFamt(maindto5201.getNpayamt());// ����������
		maindto.setSpaybankname(maindto5201.getSpayacctbankname());// ������������
		maindto.setSagentbnkcode(maindto5201.getSpayeeacctbankno());// ���������к�
		maindto.setSremark(maindto5201.getSdemo());// ժҪ
		maindto.setSmoneycorpcode("");// ���ڻ�������
		maindto.setShold1(maindto5201.getShold1());// Ԥ���ֶ�1
		maindto.setShold2(maindto5201.getShold2());// Ԥ���ֶ�2
		maindto.setDentrustdate(DateUtil.currentDate());// ί������
		maindto.setDorientrustdate(CommonUtil.strToDate(maindto5201
				.getScommitdate()));// ԭί������
		maindto.setSpackno(maindto5201.getSdealno());// ����ˮ��
		maindto.setSoritrano(maindto5201.getSdealno());// ԭ������ˮ��
		maindto.setDacceptdate(CommonUtil.strToDate(dto.getScreatdate()));// ��������
		maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// �����ڱ�־
		maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// Ԥ������(Ĭ��Ԥ����)
		maindto.setDorivoudate(CommonUtil.strToDate(maindto5201.getSvoudate()));// ԭƾ֤����
		maindto.setSorivouno(maindto5201.getSvoucherno());// ԭƾ֤���
		maindto.setSpaydictateno("no");// ���֧���˿�����
		maindto.setSpaymsgno("111");// ֧�����ı��
		maindto.setDpayentrustdate(CommonUtil
				.strToDate(maindto5201.getShold1()));// ֧��ί������
		maindto.setSpaysndbnkno(maindto5201.getSpayeeacctbankno());// ֧���������к�
		maindto.setSfilename(maindto5201.getSfilename());
		maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// ״̬ ������
		maindto.setIstatinfnum(0);
		maindto.setSxpaysndbnkno("");// ֧���������к�
		maindto.setSaddword(maindto5201.getSdemo());// ����
		maindto.setSbackflag(maindto5201.getSbackflag());
		maindto.setSrefundtype(maindto5201.getSrefundtype());
		maindto.setNbackmoney(maindto5201.getNpayamt());
		maindto.setSbckreason(maindto5201.getSdemo());
//		maindto.setSxcleardate(CommonUtil.strToDate(maindto5201.getShold1()));// ��������
		maindto.setSxcleardate(CommonUtil.strToDate(maindto5201.getSxpaydate()));// ��������
		maindto.setSxpayamt(maindto5201.getNbackmoney());
		maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳʱ��
		DatabaseFacade.getODB().create(maindto);
		return maindto;
	}

	/**
	 * * ����5201��ϸ����
	 * 
	 * @param maindto5201
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private static TvPayreckBankBackListDto copySub5201to2202(
			TfDirectpaymsgmainDto maindto5201) throws ITFEBizException,
			JAFDatabaseException {
		List<IDto> subdtoLis = PublicSearchFacade.findSubDtoByMain(maindto5201);
		List<IDto> list = new ArrayList<IDto>();
		for (IDto tmpdto : subdtoLis) {
			TfDirectpaymsgsubDto subdto5201 = (TfDirectpaymsgsubDto) tmpdto;
			TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
			subdto.setIseqno(Integer.parseInt(subdto5201.getIseqno() + ""));
			subdto.setSid(subdto5201.getSid());
			subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// �˻�����
			subdto.setIvousrlno(subdto5201.getIvousrlno());// ƾ֤��ˮ��
			subdto.setSvoucherno(subdto5201.getSvoucherbillno());// �ӱ���ϸ���
			subdto.setSbdgorgcode(subdto5201.getSagencycode());// һ��Ԥ�㵥λ����
			subdto.setSsupdepname(subdto5201.getSagencyname());// һ��Ԥ�㵥λ����
			subdto.setSfuncbdgsbtcode(subdto5201.getSexpfunccode());// ֧�����ܷ����Ŀ����
			subdto.setSexpfuncname(subdto5201.getSexpfuncname());// ֧�����ܷ����Ŀ����
			subdto.setSecnomicsubjectcode(subdto5201.getSexpecocode());// ���ÿ�Ŀ����
			subdto.setFamt(subdto5201.getNpayamt());// ֧�����
			subdto.setSpaysummaryname(subdto5201.getSremark());// ժҪ����
			subdto.setShold1(subdto5201.getShold1());// Ԥ���ֶ�1
			subdto.setShold2(subdto5201.getShold2());// Ԥ���ֶ�2
			subdto.setShold3(subdto5201.getShold3());// Ԥ���ֶ�3
			subdto.setShold4(subdto5201.getShold4());// Ԥ���ֶ�4
			subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
			subdto.setSorivouno(maindto5201.getSvoucherno());// ԭ֧��ƾ֤����
			subdto.setSorivoudetailno(subdto5201.getSvoucherbillno());// ԭ֧��ƾ֤��ϸ����
			subdto.setDorivoudate(CommonUtil.strToDate(maindto5201
					.getSvoudate()));
			subdto.setShold1(subdto5201.getSvoucherno());
			list.add(subdto);
		}
		if (list.size() > 0) {
			DatabaseFacade.getODB().create(CommonUtil.listTArray(list));
		}
		return null;

	}

}