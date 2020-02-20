package com.cfcc.itfe.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

@SuppressWarnings("unchecked")
public class Report3553LKXMUtil {
	/**
	 * ���ݴ���ı������ݣ�����Flag���������ϼ�,Flag 0������ϼƣ�1����ϼ�
	 * 
	 * @param list
	 * @param flag
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List computePayLKM(String sOrgcode, List reportList, String flag)
			throws JAFDatabaseException, ITFEBizException, ValidateException {

		List<TrTaxorgPayoutReportDto> repList = new ArrayList<TrTaxorgPayoutReportDto>();
		repList.addAll(reportList);
		// ��������������
		HashMap<String, TrTaxorgPayoutReportDto> Lmap = new HashMap<String, TrTaxorgPayoutReportDto>();
		HashMap<String, TsBudgetsubjectDto> subMap = SrvCacheFacade
				.cacheTsBdgsbtInfo(sOrgcode);
		// ��ȡȫ������¼��Ŀ�Ŀ����
		TsBudgetsubjectDto tdto = new TsBudgetsubjectDto();
		tdto.setSorgcode(sOrgcode);
		tdto.setSwriteflag(StateConstant.COMMON_NO);
		CommonQto qto = SqlUtil.IDto2CommonQto(tdto);
		List<TsBudgetsubjectDto> tlist = DatabaseFacade.getODB().findWithUR(
				tdto.getClass(), qto);
		for (TsBudgetsubjectDto t : tlist) {
			subMap.put(t.getSsubjectcode(), t);
		}

		// ѭ������ÿһ����¼�������
		for (TrTaxorgPayoutReportDto _dto : repList) {
			String subCode = _dto.getSbudgetsubcode();
			Lmap = realComputePayLKXM(subCode, subMap, Lmap, _dto, flag);
		}
		// ͳ�ƺϼ���
		List<TrTaxorgPayoutReportDto> countList = new ArrayList<TrTaxorgPayoutReportDto>();
		if (!Lmap.containsKey(StateConstant.BUGGET_IN_FUND
				+ StateConstant.MOVE_INCOME)) {
			TrTaxorgPayoutReportDto oindto = Lmap
					.get(StateConstant.BUGGET_IN_FUND);// Ԥ���ڻ���
			TrTaxorgPayoutReportDto indto = Lmap
					.get(StateConstant.COMMON_BUGGET_IN);// һ��Ԥ����
			TrTaxorgPayoutReportDto statedto = Lmap
					.get(StateConstant.STATE_OWNED);// ת��������
			TrTaxorgPayoutReportDto movedto = Lmap
					.get(StateConstant.MOVE_INCOME);// ת��������
			TrTaxorgPayoutReportDto outdto = Lmap
					.get(StateConstant.COMMON_BUGGET_IN
							+ StateConstant.MOVE_INCOME);// Ԥ����ծ��
			if (oindto != null || indto != null || movedto != null
					|| outdto != null || statedto != null) {
				Lmap.remove(StateConstant.COMMON_BUGGET_IN);
				Lmap.remove(StateConstant.BUGGET_IN_FUND);
				Lmap.remove(StateConstant.STATE_OWNED);
				Lmap.remove(StateConstant.MOVE_INCOME);
				Lmap.remove(StateConstant.COMMON_BUGGET_IN
						+ StateConstant.MOVE_INCOME);
				TrTaxorgPayoutReportDto countdto = new TrTaxorgPayoutReportDto();
				countdto.setSbudgetsubcode(StateConstant.BUGGET_IN_FUND
						+ StateConstant.MOVE_INCOME);
				countdto.setSbudgetsubname("�ϼ���");
				countdto.setSbudgettype(StateConstant.BUGGET_IN_FUND);
				countdto.setSbudgetlevelcode(StateConstant.BUGGET_IN_FUND);
				countdto.setNmoneyday(new BigDecimal(0));
				countdto.setNmoneymonth(new BigDecimal(0));
				countdto.setNmoneyyear(new BigDecimal(0));
				countdto.setNmoneyquarter(new BigDecimal(0));
				countdto.setNmoneytenday(new BigDecimal(0));
				if (oindto != null && oindto.getNmoneyday() != null)
					countdto.setNmoneyday(oindto.getNmoneyday());
				if (indto != null && indto.getNmoneyday() != null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(
							indto.getNmoneyday()));
				if (movedto != null && movedto.getNmoneyday() != null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(
							movedto.getNmoneyday()));
				if (outdto != null && outdto.getNmoneyday() != null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(
							outdto.getNmoneyday()));
				if (statedto != null && statedto.getNmoneyday() != null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(
							statedto.getNmoneyday()));

				if (oindto != null && oindto.getNmoneymonth() != null)
					countdto.setNmoneymonth(oindto.getNmoneymonth());
				if (indto != null && indto.getNmoneymonth() != null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(
							indto.getNmoneymonth()));
				if (movedto != null && movedto.getNmoneymonth() != null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(
							movedto.getNmoneymonth()));
				if (outdto != null && outdto.getNmoneymonth() != null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(
							outdto.getNmoneymonth()));
				if (statedto != null && statedto.getNmoneymonth() != null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(
							statedto.getNmoneymonth()));

				if (oindto != null && oindto.getNmoneyquarter() != null)
					countdto.setNmoneyquarter(oindto.getNmoneyquarter());
				if (indto != null && indto.getNmoneyquarter() != null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(
							indto.getNmoneyquarter()));
				if (movedto != null && movedto.getNmoneyquarter() != null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(
							movedto.getNmoneyquarter()));
				if (outdto != null && outdto.getNmoneyquarter() != null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(
							outdto.getNmoneyquarter()));
				if (statedto != null && statedto.getNmoneyquarter() != null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(
							statedto.getNmoneyquarter()));

				if (oindto != null && oindto.getNmoneytenday() != null)
					countdto.setNmoneytenday(oindto.getNmoneytenday());
				if (indto != null && indto.getNmoneytenday() != null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(
							indto.getNmoneytenday()));
				if (movedto != null && movedto.getNmoneytenday() != null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(
							movedto.getNmoneytenday()));
				if (outdto != null && outdto.getNmoneytenday() != null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(
							outdto.getNmoneytenday()));
				if (statedto != null && statedto.getNmoneytenday() != null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(
							statedto.getNmoneytenday()));

				if (oindto != null && oindto.getNmoneyyear() != null)
					countdto.setNmoneyyear(oindto.getNmoneyyear());
				if (indto != null && indto.getNmoneyyear() != null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(
							indto.getNmoneyyear()));
				if (movedto != null && movedto.getNmoneyyear() != null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(
							movedto.getNmoneyyear()));
				if (outdto != null && outdto.getNmoneyyear() != null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(
							outdto.getNmoneyyear()));
				if (statedto != null && statedto.getNmoneyyear() != null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(
							statedto.getNmoneyyear()));

				if (indto != null)
					countList.add(indto);
				if (oindto != null)
					countList.add(oindto);
				if (movedto != null)
					countList.add(movedto);
				if (outdto != null)
					countList.add(outdto);
				if (statedto != null)
					countList.add(statedto);
				if (countdto != null)
					countList.add(countdto);
			}
		}

		// ���������������ͳ�ƽ����
		Set<String> set = Lmap.keySet();
		repList = new ArrayList<TrTaxorgPayoutReportDto>();
		for (String key : set) {
			repList.add(Lmap.get(key));
		}
		// ��ͳ�ƽ�������Ͻ�������,��ʾ�����Ч��
		Collections.sort(repList, new SortByPayReport());
		if (countList != null && countList.size() > 0)
			repList.addAll(countList);
		return repList;
	}

	/**
	 * �Ա���List���տ�Ŀ��������
	 * 
	 * @author db2admin
	 * 
	 */
	class SortByPayReport implements Comparator {
		public int compare(Object o1, Object o2) {
			TrTaxorgPayoutReportDto dto1 = (TrTaxorgPayoutReportDto) o1;
			TrTaxorgPayoutReportDto dto2 = (TrTaxorgPayoutReportDto) o2;

			String c1 = dto1.getSbudgetsubcode();
			String c2 = dto2.getSbudgetsubcode();
			return c1.compareTo(c2);
		}
	}

	/**
	 * ʵ�ʼ�����������Ŀ
	 * 
	 * @param subCode
	 * @param subMap
	 * @param Lmap
	 * @param _dto
	 * @param flag
	 */
	private HashMap<String, TrTaxorgPayoutReportDto> realComputePayLKXM(
			String subCode, HashMap<String, TsBudgetsubjectDto> subMap,
			HashMap<String, TrTaxorgPayoutReportDto> lmap,
			TrTaxorgPayoutReportDto _dto, String flag) {
		// 3λ���Ͽ�Ŀ������ϼ�
		if (subCode.length() == 3) {
			String l = subCode.substring(0, 3);
			TrTaxorgPayoutReportDto tmpDto = (TrTaxorgPayoutReportDto) _dto
					.clone();
			tmpDto.setSbudgetsubcode(l);
			tmpDto.setSbudgetsubname(subMap.get(l) == null ? _dto
					.getSbudgetsubname() : subMap.get(l).getSsubjectname());
			if (!lmap.containsKey(l)) {
				lmap.put(l, tmpDto);
			} else {
				TrTaxorgPayoutReportDto ldto = lmap.get(l);
				ldto.setNmoneyday(ldto.getNmoneyday().add(_dto.getNmoneyday()));
				ldto.setNmoneytenday(ldto.getNmoneytenday().add(
						_dto.getNmoneytenday()));
				ldto.setNmoneymonth(ldto.getNmoneymonth().add(
						_dto.getNmoneymonth()));
				ldto.setNmoneyquarter(ldto.getNmoneyquarter().add(
						_dto.getNmoneyquarter()));
				ldto.setNmoneyyear(ldto.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(l, ldto);
			}
			// ��Ŀλ��>3<5����ԭʼ��Ŀ��Ϣ����Ŀ���ȵ���3�����ٷ����Ŀ��Ϣ,һ��û���������
			if (subCode.length() > 3 && subCode.length() < 5) {
				lmap.put(subCode, _dto);
			}

		}
		// 5λ���ϵĿ�Ŀ�����ϼ�
		if (StateConstant.COMMON_YES.equals(flag)) {
			if (subCode.length() >= 5) {
				String k = subCode.substring(0, 5);
				TrTaxorgPayoutReportDto tmpDto = (TrTaxorgPayoutReportDto) _dto
						.clone();
				tmpDto.setSbudgetsubcode(k);
				tmpDto.setSbudgetsubname(subMap.get(k) == null ? _dto
						.getSbudgetsubname() : subMap.get(k).getSsubjectname());
				if (!lmap.containsKey(k)) {
					lmap.put(k, tmpDto);
				} else {
					TrTaxorgPayoutReportDto kdto = lmap.get(k);
					kdto.setNmoneyday(kdto.getNmoneyday().add(
							_dto.getNmoneyday()));
					kdto.setNmoneytenday(kdto.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					kdto.setNmoneymonth(kdto.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					kdto.setNmoneyquarter(kdto.getNmoneyquarter().add(
							_dto.getNmoneyquarter()));
					kdto.setNmoneyyear(kdto.getNmoneyyear().add(
							_dto.getNmoneyyear()));
					lmap.put(k, kdto);
				}

				// ��Ŀλ��>5<7����ԭʼ��Ŀ��Ϣ����Ŀ���ȵ���5�����ٷ����Ŀ��Ϣ,һ��û���������
				if (subCode.length() > 5 && subCode.length() < 7) {
					lmap.put(subCode, _dto);
				}
			}
		}
		// 7λ���ϵĿ�Ŀ������ϼ�
		if (subCode.length() >= 7) {
			String x = subCode.substring(0, 7);
			TrTaxorgPayoutReportDto tmpDto = (TrTaxorgPayoutReportDto) _dto
					.clone();
			tmpDto.setSbudgetsubcode(x);
			tmpDto.setSbudgetsubname(subMap.get(x) == null ? _dto
					.getSbudgetsubname() : subMap.get(x).getSsubjectname());
			if (!lmap.containsKey(x)) {
				lmap.put(x, tmpDto);
			} else {
				TrTaxorgPayoutReportDto xdto = lmap.get(x);
				xdto.setNmoneyday(xdto.getNmoneyday().add(_dto.getNmoneyday()));
				xdto.setNmoneytenday(xdto.getNmoneytenday().add(
						_dto.getNmoneytenday()));
				xdto.setNmoneymonth(xdto.getNmoneymonth().add(
						_dto.getNmoneymonth()));
				xdto.setNmoneyquarter(xdto.getNmoneyquarter().add(
						_dto.getNmoneyquarter()));
				xdto.setNmoneyyear(xdto.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(x, xdto);
			}
			// ��Ŀ���ȴ���7Ϊ�ļ���
			if (subCode.length() > 7) {
				if (!lmap.containsKey(subCode)) {
					_dto.setSbudgetsubname(subMap.get(subCode) == null ? _dto
							.getSbudgetsubname() : subMap.get(subCode)
							.getSsubjectname());
					lmap.put(subCode, _dto);
				} else {
					TrTaxorgPayoutReportDto xdto = lmap.get(subCode);
					xdto.setNmoneyday(xdto.getNmoneyday().add(
							_dto.getNmoneyday()));
					xdto.setNmoneytenday(xdto.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					xdto.setNmoneymonth(xdto.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					xdto.setNmoneyquarter(xdto.getNmoneyquarter().add(
							_dto.getNmoneyquarter()));
					xdto.setNmoneyyear(xdto.getNmoneyyear().add(
							_dto.getNmoneyyear()));
					lmap.put(subCode, xdto);
				}
			}
		}
		if(subCode.length() > 3){
			lmap = addStatPayInfo(lmap, subMap, subCode, _dto);
		}
		return lmap;
	}

	/**
	 * ���Ӳ��ּ����ͳ����Ϣ,�����巽������
	 * 
	 * @return
	 */
	private HashMap<String, TrTaxorgPayoutReportDto> addStatPayInfo(
			HashMap<String, TrTaxorgPayoutReportDto> lmap,
			HashMap<String, TsBudgetsubjectDto> subMap, String subCode,
			TrTaxorgPayoutReportDto _dto) {
		// ��Ŀ���ͣ�s_SubjectType
		// 00����֧��01һ��Ԥ����02һ��Ԥ����03Ԥ���ڻ���04Ԥ����ծ��05Ԥ�������06����07ת��������08�����ʱ���ӪԤ��֧��---sleSubjectType
		// ��Ŀ���� s_ClassFlag 01 ˰������ 02 �籣�������� 03 ��˰���� 04 ����ת�����ձ��� 05 ծ������ 06
		// ת�������� 07֧������ 08 ֧������
		TsBudgetsubjectDto subDto = subMap.get(subCode);
		String subType = subDto == null ? "-" : subDto.getSsubjecttype();
		// String subClass = subDto==null?"-":subDto.getSsubjectclass();
		// ����Ŀ���� ͳ��Ԥ���ڻ���
		if (Integer.valueOf(StateConstant.SBT_TYPE_FUND_IN).toString().equals(
				subType)||Integer.valueOf(StateConstant.SBT_TYPE_DEBT_OUT).toString().equals(
						subType)) {
			if (!lmap.containsKey(StateConstant.COMMON_BUGGET_IN)) {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = (TrTaxorgPayoutReportDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.COMMON_BUGGET_IN);
				BUDGET_IN_DTO.setSbudgetsubname("Ԥ���ڻ���");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.COMMON_BUGGET_IN, BUDGET_IN_DTO);
			} else {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = lmap
						.get(StateConstant.COMMON_BUGGET_IN);
				BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
						_dto.getNmoneyday()));
				BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday()
						.add(_dto.getNmoneytenday()));
				BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth()
						.add(_dto.getNmoneymonth()));
				BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
						.add(_dto.getNmoneyquarter()));
				BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(StateConstant.COMMON_BUGGET_IN, BUDGET_IN_DTO);
			}
		}
		// ����Ŀ���� ͳ��һ��Ԥ����
		if (Integer.valueOf(StateConstant.SBT_TYPE_BUDGET_IN).toString()
				.equals(subType)) {
			if (!lmap.containsKey(StateConstant.BUGGET_IN_FUND)) {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = (TrTaxorgPayoutReportDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.BUGGET_IN_FUND);
				BUDGET_IN_DTO.setSbudgetsubname("һ��Ԥ����");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.BUGGET_IN_FUND, BUDGET_IN_DTO);
			} else {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = lmap
						.get(StateConstant.BUGGET_IN_FUND);
				BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
						_dto.getNmoneyday()));
				BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday()
						.add(_dto.getNmoneytenday()));
				BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth()
						.add(_dto.getNmoneymonth()));
				BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
						.add(_dto.getNmoneyquarter()));
				BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(StateConstant.BUGGET_IN_FUND, BUDGET_IN_DTO);
			}
		}
		// ����Ŀ���� ͳ��ծ������
		if (Integer.valueOf(StateConstant.SBT_CLASS_CAPINCOME).toString()
				.equals(subType)) {
			if (!lmap.containsKey(StateConstant.COMMON_BUGGET_IN
					+ StateConstant.MOVE_INCOME)) {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = (TrTaxorgPayoutReportDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.COMMON_BUGGET_IN
						+ StateConstant.MOVE_INCOME);
				BUDGET_IN_DTO.setSbudgetsubname("Ԥ����ծ��");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.COMMON_BUGGET_IN
						+ StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			} else {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = lmap
						.get(StateConstant.COMMON_BUGGET_IN
								+ StateConstant.MOVE_INCOME);
				BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
						_dto.getNmoneyday()));
				BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday()
						.add(_dto.getNmoneytenday()));
				BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth()
						.add(_dto.getNmoneymonth()));
				BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
						.add(_dto.getNmoneyquarter()));
				BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(StateConstant.COMMON_BUGGET_IN
						+ StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			}
		}
		// ����Ŀ����ת��������
		if (Integer.valueOf(StateConstant.SBT_CLASS_FUNCPAY).toString().equals(
				subType)) {
			if (!lmap.containsKey(StateConstant.MOVE_INCOME)) {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = (TrTaxorgPayoutReportDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.MOVE_INCOME);
				BUDGET_IN_DTO.setSbudgetsubname("ת��������");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			} else {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = lmap
						.get(StateConstant.MOVE_INCOME);
				BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
						_dto.getNmoneyday()));
				BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday()
						.add(_dto.getNmoneytenday()));
				BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth()
						.add(_dto.getNmoneymonth()));
				BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
						.add(_dto.getNmoneyquarter()));
				BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			}
		}
		// ����Ŀ����08�����ʱ���ӪԤ��֧��
		if (Integer.valueOf(StateConstant.SBT_TYPE_STATE_OWNED).toString()
				.equals(subType)) {
			if (!lmap.containsKey(StateConstant.STATE_OWNED)) {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = (TrTaxorgPayoutReportDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.STATE_OWNED);
				BUDGET_IN_DTO.setSbudgetsubname("�����ʱ���ӪԤ��֧��");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.STATE_OWNED, BUDGET_IN_DTO);
			} else {
				TrTaxorgPayoutReportDto BUDGET_IN_DTO = lmap
						.get(StateConstant.STATE_OWNED);
				BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
						_dto.getNmoneyday()));
				BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday()
						.add(_dto.getNmoneytenday()));
				BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth()
						.add(_dto.getNmoneymonth()));
				BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
						.add(_dto.getNmoneyquarter()));
				BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(StateConstant.STATE_OWNED, BUDGET_IN_DTO);
			}
		}
		return lmap;
	}

}