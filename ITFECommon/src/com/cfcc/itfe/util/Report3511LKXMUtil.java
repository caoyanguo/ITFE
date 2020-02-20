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
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;
@SuppressWarnings("unchecked")
public class Report3511LKXMUtil {
	/**
	 * 根据传入的报表数据，根据Flag计算类款项合计,Flag 0不含款合计，1含款合计
	 * 
	 * @param list
	 * @param flag
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 * @throws ValidateException 
	 */
	public List computeLKM(String sOrgcode, List reportList, String flag)
			throws JAFDatabaseException, ITFEBizException, ValidateException {

		List<TrIncomedayrptDto> repList = new ArrayList<TrIncomedayrptDto>();
		repList.addAll(reportList);
		// 计算类款项的容器
		HashMap<String, TrIncomedayrptDto> Lmap = new HashMap<String, TrIncomedayrptDto>();
		HashMap<String, TsBudgetsubjectDto> subMap = SrvCacheFacade.cacheTsBdgsbtInfo(sOrgcode);
		//获取全部不可录入的科目代码
		TsBudgetsubjectDto tdto = new TsBudgetsubjectDto();
		tdto.setSorgcode(sOrgcode);
		tdto.setSwriteflag(StateConstant.COMMON_NO);
		CommonQto qto = SqlUtil.IDto2CommonQto(tdto);
		List <TsBudgetsubjectDto> tlist =  DatabaseFacade.getODB().findWithUR(tdto.getClass(), qto);
		for (TsBudgetsubjectDto t :tlist) {
			subMap.put(t.getSsubjectcode(), t);
		}
		 
		// 循环计算每一条记录的类款项
		for (TrIncomedayrptDto _dto : repList) {
			String subCode = _dto.getSbudgetsubcode();
			Lmap = realComputeLKXM(subCode, subMap, Lmap, _dto, flag);
		}
		//统计合计数
		List<TrIncomedayrptDto> countList = new ArrayList<TrIncomedayrptDto>();
		if(!Lmap.containsKey(StateConstant.BUGGET_IN_FUND+StateConstant.MOVE_INCOME))
		{
			TrIncomedayrptDto cindto = Lmap.get(StateConstant.COMMON_BUGGET_IN);//国有资本
			TrIncomedayrptDto oindto = Lmap.get(StateConstant.BUGGET_IN_FUND);//预算内基金
			TrIncomedayrptDto indto = Lmap.get(StateConstant.BUGGET_IN_FUND+StateConstant.COMMON_BUGGET_IN);//一般预算内
			TrIncomedayrptDto movedto = Lmap.get(StateConstant.MOVE_INCOME);//转移性收入
			TrIncomedayrptDto outdto = Lmap.get(StateConstant.COMMON_BUGGET_IN+StateConstant.MOVE_INCOME);//预算内债务
			if(oindto!=null||indto!=null||movedto!=null||outdto!=null||cindto!=null)
			{
				Lmap.remove(StateConstant.COMMON_BUGGET_IN);
				Lmap.remove(StateConstant.BUGGET_IN_FUND);
				Lmap.remove(StateConstant.MOVE_INCOME);
				Lmap.remove(StateConstant.COMMON_BUGGET_IN+StateConstant.MOVE_INCOME);
				TrIncomedayrptDto countdto = new TrIncomedayrptDto();
				countdto.setSbudgetsubcode(StateConstant.BUGGET_IN_FUND+StateConstant.MOVE_INCOME);
				countdto.setSbudgetsubname("合计数");
				countdto.setSbudgettype(StateConstant.BUGGET_IN_FUND);
				countdto.setSbudgetlevelcode(StateConstant.BUGGET_IN_FUND);
				countdto.setNmoneyday(new BigDecimal(0));
				countdto.setNmoneymonth(new BigDecimal(0));
				countdto.setNmoneyyear(new BigDecimal(0));
				countdto.setNmoneyquarter(new BigDecimal(0));
				countdto.setNmoneytenday(new BigDecimal(0));
				
				if(cindto!=null&&cindto.getNmoneyday()!=null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(cindto.getNmoneyday()));
				if(oindto!=null&&oindto.getNmoneyday()!=null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(oindto.getNmoneyday()));
				if(indto!=null&&indto.getNmoneyday()!=null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(indto.getNmoneyday()));
				if(movedto!=null&&movedto.getNmoneyday()!=null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(movedto.getNmoneyday()));
				if(outdto!=null&&outdto.getNmoneyday()!=null)
					countdto.setNmoneyday(countdto.getNmoneyday().add(outdto.getNmoneyday()));
				
				if(cindto!=null&&cindto.getNmoneymonth()!=null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(cindto.getNmoneymonth()));
				if(oindto!=null&&oindto.getNmoneymonth()!=null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(oindto.getNmoneymonth()));
				if(indto!=null&&indto.getNmoneymonth()!=null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(indto.getNmoneymonth()));
				if(movedto!=null&&movedto.getNmoneymonth()!=null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(movedto.getNmoneymonth()));
				if(outdto!=null&&outdto.getNmoneymonth()!=null)
					countdto.setNmoneymonth(countdto.getNmoneymonth().add(outdto.getNmoneymonth()));
				
				if(cindto!=null&&cindto.getNmoneyquarter()!=null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(cindto.getNmoneyquarter()));
				if(oindto!=null&&oindto.getNmoneyquarter()!=null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(oindto.getNmoneyquarter()));
				if(indto!=null&&indto.getNmoneyquarter()!=null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(indto.getNmoneyquarter()));
				if(movedto!=null&&movedto.getNmoneyquarter()!=null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(movedto.getNmoneyquarter()));
				if(outdto!=null&&outdto.getNmoneyquarter()!=null)
					countdto.setNmoneyquarter(countdto.getNmoneyquarter().add(outdto.getNmoneyquarter()));
				
				if(cindto!=null&&cindto.getNmoneytenday()!=null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(cindto.getNmoneytenday()));
				if(oindto!=null&&oindto.getNmoneytenday()!=null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(oindto.getNmoneytenday()));
				if(indto!=null&&indto.getNmoneytenday()!=null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(indto.getNmoneytenday()));
				if(movedto!=null&&movedto.getNmoneytenday()!=null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(movedto.getNmoneytenday()));
				if(outdto!=null&&outdto.getNmoneytenday()!=null)
					countdto.setNmoneytenday(countdto.getNmoneytenday().add(outdto.getNmoneytenday()));
				
				if(cindto!=null&&cindto.getNmoneyyear()!=null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(cindto.getNmoneyyear()));
				if(oindto!=null&&oindto.getNmoneyyear()!=null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(oindto.getNmoneyyear()));
				if(indto!=null&&indto.getNmoneyyear()!=null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(indto.getNmoneyyear()));
				if(movedto!=null&&movedto.getNmoneyyear()!=null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(movedto.getNmoneyyear()));
				if(outdto!=null&&outdto.getNmoneyyear()!=null)
					countdto.setNmoneyyear(countdto.getNmoneyyear().add(outdto.getNmoneyyear()));
				
				if(cindto!=null)
					countList.add(cindto);
				if(oindto!=null)
					countList.add(oindto);
				if(indto!=null)
					countList.add(indto);
				if(movedto!=null)
					countList.add(movedto);
				if(outdto!=null)
					countList.add(outdto);
				if(countdto!=null)
					countList.add(countdto);
			}
		}
		
		// 加入计算结果到报表统计结果中
		Set<String> set = Lmap.keySet();
		repList = new ArrayList<TrIncomedayrptDto>();
		for (String key : set) {
			repList.add( Lmap.get(key));
		}
		// 对统计结果整体上进行排序,显示类款项效果
		Collections.sort(repList, new SortByDayReport());
		if(countList!=null&&countList.size()>0)
			repList.addAll(countList);
		return repList;
	}

	/**
	 * 对报表List按照科目进行排序
	 * 
	 * @author db2admin
	 * 
	 */
	class SortByDayReport implements Comparator {
		public int compare(Object o1, Object o2) {
			TrIncomedayrptDto dto1 = (TrIncomedayrptDto) o1;
			TrIncomedayrptDto dto2 = (TrIncomedayrptDto) o2;

			String c1 = dto1.getSbudgetsubcode();
			String c2 = dto2.getSbudgetsubcode();
			return c1.compareTo(c2);
		}
	}

	/**
	 * 实际计算计算类款项目
	 * 
	 * @param subCode
	 * @param subMap
	 * @param Lmap
	 * @param _dto
	 * @param flag
	 */
	private HashMap<String, TrIncomedayrptDto> realComputeLKXM(String subCode,
			HashMap<String, TsBudgetsubjectDto> subMap,
			HashMap<String, TrIncomedayrptDto> lmap, TrIncomedayrptDto _dto,
			String flag) {
		// 3位以上科目计算类合计
		if (subCode.length() >= 3) {
			String l = subCode.substring(0, 3);
			TrIncomedayrptDto tmpDto = (TrIncomedayrptDto) _dto.clone();
			tmpDto.setSbudgetsubcode(l);
			tmpDto.setSbudgetsubname(subMap.get(l)==null?_dto.getSbudgetsubname():subMap.get(l).getSsubjectname());
			if (!lmap.containsKey(l)) {
				lmap.put(l, tmpDto);
			} else {
				TrIncomedayrptDto ldto = lmap.get(l);
				ldto.setNmoneyday(ldto.getNmoneyday().add(_dto.getNmoneyday()));
				if(String.valueOf(ldto.getSrptdate()).equals(_dto.getSrptdate()))
				{
					ldto.setNmoneytenday(ldto.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					ldto.setNmoneymonth(ldto.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					ldto.setNmoneyquarter(ldto.getNmoneyquarter().add(
							_dto.getNmoneyquarter()));
					ldto.setNmoneyyear(ldto.getNmoneyyear()
							.add(_dto.getNmoneyyear()));
				}else
				{
					ldto.setSrptdate(_dto.getSrptdate());
					ldto.setNmoneytenday(_dto.getNmoneytenday());
					ldto.setNmoneymonth(_dto.getNmoneymonth());
					ldto.setNmoneyquarter(_dto.getNmoneyquarter());
					ldto.setNmoneyyear(_dto.getNmoneyyear());
				}
				lmap.put(l, ldto);
			}
			//科目位数>3<5放入原始科目信息，科目长度等于3，则不再放入科目信息,一般没有这种情况
			if (subCode.length()>3 && subCode.length()<5) {
				lmap.put(subCode, _dto);
			}
			
		}
		// 5位以上的科目计算款合计
		if (StateConstant.COMMON_YES.equals(flag)) {
			if (subCode.length() >= 5) {
				String k = subCode.substring(0, 5);
				TrIncomedayrptDto tmpDto = (TrIncomedayrptDto) _dto.clone();
				tmpDto.setSbudgetsubcode(k);
				tmpDto.setSbudgetsubname(subMap.get(k)==null?_dto.getSbudgetsubname():subMap.get(k).getSsubjectname());
				if (!lmap.containsKey(k)) {
					lmap.put(k, tmpDto);
				} else {
					TrIncomedayrptDto kdto = lmap.get(k);
					kdto.setNmoneyday(kdto.getNmoneyday().add(
							_dto.getNmoneyday()));
					if(String.valueOf(kdto.getSrptdate()).equals(_dto.getSrptdate()))
					{
						kdto.setNmoneytenday(kdto.getNmoneytenday().add(
								_dto.getNmoneytenday()));
						kdto.setNmoneymonth(kdto.getNmoneymonth().add(
								_dto.getNmoneymonth()));
						kdto.setNmoneyquarter(kdto.getNmoneyquarter().add(
								_dto.getNmoneyquarter()));
						kdto.setNmoneyyear(kdto.getNmoneyyear().add(
								_dto.getNmoneyyear()));
					}else
					{
						kdto.setSrptdate(_dto.getSrptdate());
						kdto.setNmoneytenday(_dto.getNmoneytenday());
						kdto.setNmoneymonth(_dto.getNmoneymonth());
						kdto.setNmoneyquarter(_dto.getNmoneyquarter());
						kdto.setNmoneyyear(_dto.getNmoneyyear());
					}
					lmap.put(k, kdto);
				}
	
				//科目位数>5<7放入原始科目信息，科目长度等于5，则不再放入科目信息,一般没有这种情况
				if (subCode.length()>5 && subCode.length()<7) {
					lmap.put(subCode, _dto);
				}
			}
		}
		// 7位以上的科目计算项合计
		if (subCode.length() >= 7) {
			String x = subCode.substring(0, 7);
			TrIncomedayrptDto tmpDto = (TrIncomedayrptDto) _dto.clone();
			tmpDto.setSbudgetsubcode(x);
			tmpDto.setSbudgetsubname(subMap.get(x)==null?_dto.getSbudgetsubname():subMap.get(x).getSsubjectname());
			if (!lmap.containsKey(x)) {
				lmap.put(x, tmpDto);
			} else {
				TrIncomedayrptDto xdto = lmap.get(x);
				xdto.setNmoneyday(xdto.getNmoneyday().add(_dto.getNmoneyday()));
				if(String.valueOf(xdto.getSrptdate()).equals(_dto.getSrptdate()))
				{
					xdto.setNmoneytenday(xdto.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					xdto.setNmoneymonth(xdto.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					xdto.setNmoneyquarter(xdto.getNmoneyquarter().add(
							_dto.getNmoneyquarter()));
					xdto.setNmoneyyear(xdto.getNmoneyyear()
							.add(_dto.getNmoneyyear()));
				}else
				{
					xdto.setSrptdate(_dto.getSrptdate());
					xdto.setNmoneytenday(_dto.getNmoneytenday());
					xdto.setNmoneymonth(_dto.getNmoneymonth());
					xdto.setNmoneyquarter(_dto.getNmoneyquarter());
					xdto.setNmoneyyear(_dto.getNmoneyyear());
				}
				lmap.put(x, xdto);
			}
			//科目长度大于7为的加入
			if (subCode.length()>7) {
				if (!lmap.containsKey(subCode)) {
					_dto.setSbudgetsubname(subMap.get(subCode)==null?_dto.getSbudgetsubname():subMap.get(subCode).getSsubjectname());
					lmap.put(subCode, _dto);
				} else {
					TrIncomedayrptDto xdto = lmap.get(subCode);
					xdto.setNmoneyday(xdto.getNmoneyday().add(_dto.getNmoneyday()));
					if(String.valueOf(xdto.getSrptdate()).equals(_dto.getSrptdate()))
					{
						xdto.setNmoneytenday(xdto.getNmoneytenday().add(
								_dto.getNmoneytenday()));
						xdto.setNmoneymonth(xdto.getNmoneymonth().add(
								_dto.getNmoneymonth()));
						xdto.setNmoneyquarter(xdto.getNmoneyquarter().add(
								_dto.getNmoneyquarter()));
						xdto.setNmoneyyear(xdto.getNmoneyyear()
								.add(_dto.getNmoneyyear()));
					}else
					{
						xdto.setSrptdate(_dto.getSrptdate());
						xdto.setNmoneytenday(_dto.getNmoneytenday());
						xdto.setNmoneymonth(_dto.getNmoneymonth());
						xdto.setNmoneyquarter(_dto.getNmoneyquarter());
						xdto.setNmoneyyear(_dto.getNmoneyyear());
					}
					lmap.put(subCode, xdto);
				}
			}
		}
		lmap = addStatInfo(lmap, subMap, subCode, _dto);
		return lmap;
	}

	/**
	 * 增加部分计算的统计信息,供主体方法调用
	 * 
	 * @return
	 */
	private HashMap<String, TrIncomedayrptDto> addStatInfo(
			HashMap<String, TrIncomedayrptDto> lmap,
			HashMap<String, TsBudgetsubjectDto> subMap, String subCode,
			TrIncomedayrptDto _dto) {
		// 科目类型：s_SubjectType
		// 01一般预算内02一般预算外03预算内基金04预算内债务05预算外基金06共用07转移性收入08国有资本经营预算收入---sleSubjectType
		// 科目分类 s_ClassFlag 01 税收收入 02 社保基金收入 03 非税收入 04 贷款转贷回收本金 05 债务收入 06
		// 转移性收入 07支出功能 08 支出经济
		TsBudgetsubjectDto subDto = subMap.get(subCode);
		String subType = subDto==null?"-":subDto.getSsubjecttype();
//		String subClass = subDto==null?"-":subDto.getSsubjectclass();
		//按科目类型统计国有资本经营预算收入
		if (Integer.valueOf(StateConstant.SBT_TYPE_STATE_OWNED).toString().equals(subType)) {
			if (!lmap.containsKey(StateConstant.COMMON_BUGGET_IN)) {
				TrIncomedayrptDto BUDGET_IN_DTO = (TrIncomedayrptDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.COMMON_BUGGET_IN);
				BUDGET_IN_DTO.setSbudgetsubname("国有资本经营预算收入");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.COMMON_BUGGET_IN, BUDGET_IN_DTO);
			} else {
				TrIncomedayrptDto BUDGET_IN_DTO = lmap
						.get(StateConstant.COMMON_BUGGET_IN);
				BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
						_dto.getNmoneyday()));
				BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday().add(
						_dto.getNmoneytenday()));
				BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth().add(
						_dto.getNmoneymonth()));
				BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
						.add(_dto.getNmoneyquarter()));
				BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				lmap.put(StateConstant.COMMON_BUGGET_IN, BUDGET_IN_DTO);
			}
		}
		// 按科目类型 统计一般预算内
		if (Integer.valueOf(StateConstant.SBT_TYPE_BUDGET_IN).toString().equals(subType)) {
			if (!lmap.containsKey(StateConstant.BUGGET_IN_FUND)) {
				TrIncomedayrptDto BUDGET_IN_DTO = (TrIncomedayrptDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.BUGGET_IN_FUND);
				BUDGET_IN_DTO.setSbudgetsubname("一般预算内");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.BUGGET_IN_FUND, BUDGET_IN_DTO);
			} else {
				TrIncomedayrptDto BUDGET_IN_DTO = lmap
						.get(StateConstant.BUGGET_IN_FUND);
				if(String.valueOf(BUDGET_IN_DTO.getSrptdate()).equals(_dto.getSrptdate()))
				{
					BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
							_dto.getNmoneyday()));
					BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
							.add(_dto.getNmoneyquarter()));
					BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
							_dto.getNmoneyyear()));
				}else
				{
					BUDGET_IN_DTO.setSrptdate(_dto.getSrptdate());
					BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
					BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
					BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
					BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				}
				lmap.put(StateConstant.BUGGET_IN_FUND, BUDGET_IN_DTO);
			}
		}
		// 按科目类型 统计预算内基金
		if (Integer.valueOf(StateConstant.SBT_TYPE_FUND_IN).toString().equals(subType)) {
			if (!lmap.containsKey(StateConstant.BUGGET_IN_FUND+StateConstant.COMMON_BUGGET_IN)) {
				TrIncomedayrptDto BUDGET_IN_DTO = (TrIncomedayrptDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.BUGGET_IN_FUND+StateConstant.COMMON_BUGGET_IN);
				BUDGET_IN_DTO.setSbudgetsubname("预算内基金");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.BUGGET_IN_FUND+StateConstant.COMMON_BUGGET_IN, BUDGET_IN_DTO);
			} else {
				TrIncomedayrptDto BUDGET_IN_DTO = lmap
						.get(StateConstant.BUGGET_IN_FUND+StateConstant.COMMON_BUGGET_IN);
				if(String.valueOf(BUDGET_IN_DTO.getSrptdate()).equals(_dto.getSrptdate()))
				{
					BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
							_dto.getNmoneyday()));
					BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
							.add(_dto.getNmoneyquarter()));
					BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
							_dto.getNmoneyyear()));
				}else
				{
					BUDGET_IN_DTO.setSrptdate(_dto.getSrptdate());
					BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
					BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
					BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
					BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				}
				lmap.put(StateConstant.BUGGET_IN_FUND+StateConstant.COMMON_BUGGET_IN, BUDGET_IN_DTO);
			}
		}
		// 按科目类型 统计债务收入
		if (Integer.valueOf(StateConstant.SBT_CLASS_CAPINCOME).toString().equals(subType)) {
			if (!lmap.containsKey(StateConstant.COMMON_BUGGET_IN+StateConstant.MOVE_INCOME)) {
				TrIncomedayrptDto BUDGET_IN_DTO = (TrIncomedayrptDto) _dto.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.COMMON_BUGGET_IN+StateConstant.MOVE_INCOME);
				BUDGET_IN_DTO.setSbudgetsubname("预算内债务");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.COMMON_BUGGET_IN+StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			} else {
				TrIncomedayrptDto BUDGET_IN_DTO = lmap.get(StateConstant.COMMON_BUGGET_IN+StateConstant.MOVE_INCOME);
				if(String.valueOf(BUDGET_IN_DTO.getSrptdate()).equals(_dto.getSrptdate()))
				{
					BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
							_dto.getNmoneyday()));
					BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
							.add(_dto.getNmoneyquarter()));
					BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
							_dto.getNmoneyyear()));
				}else
				{
					BUDGET_IN_DTO.setSrptdate(_dto.getSrptdate());
					BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
					BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
					BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
					BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				}
				lmap.put(StateConstant.COMMON_BUGGET_IN+StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			}
		}
		// 按科目分类转移性收入
		if (Integer.valueOf(StateConstant.SBT_CLASS_FUNCPAY).toString().equals(subType)) {
			if (!lmap.containsKey(StateConstant.MOVE_INCOME)) {
				TrIncomedayrptDto BUDGET_IN_DTO = (TrIncomedayrptDto) _dto
						.clone();
				BUDGET_IN_DTO.setSbudgetsubcode(StateConstant.MOVE_INCOME);
				BUDGET_IN_DTO.setSbudgetsubname("转移性收入");
				BUDGET_IN_DTO.setNmoneyday(_dto.getNmoneyday());
				BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
				BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
				BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
				BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				lmap.put(StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			} else {
				TrIncomedayrptDto BUDGET_IN_DTO = lmap
						.get(StateConstant.MOVE_INCOME);
				if(String.valueOf(BUDGET_IN_DTO.getSrptdate()).equals(_dto.getSrptdate()))
				{
					BUDGET_IN_DTO.setNmoneyday(BUDGET_IN_DTO.getNmoneyday().add(
							_dto.getNmoneyday()));
					BUDGET_IN_DTO.setNmoneytenday(BUDGET_IN_DTO.getNmoneytenday().add(
							_dto.getNmoneytenday()));
					BUDGET_IN_DTO.setNmoneymonth(BUDGET_IN_DTO.getNmoneymonth().add(
							_dto.getNmoneymonth()));
					BUDGET_IN_DTO.setNmoneyquarter(BUDGET_IN_DTO.getNmoneyquarter()
							.add(_dto.getNmoneyquarter()));
					BUDGET_IN_DTO.setNmoneyyear(BUDGET_IN_DTO.getNmoneyyear().add(
							_dto.getNmoneyyear()));
				}else
				{
					BUDGET_IN_DTO.setSrptdate(_dto.getSrptdate());
					BUDGET_IN_DTO.setNmoneytenday(_dto.getNmoneytenday());
					BUDGET_IN_DTO.setNmoneymonth(_dto.getNmoneymonth());
					BUDGET_IN_DTO.setNmoneyquarter(_dto.getNmoneyquarter());
					BUDGET_IN_DTO.setNmoneyyear(_dto.getNmoneyyear());
				}
				lmap.put(StateConstant.MOVE_INCOME, BUDGET_IN_DTO);
			}
		}
		return lmap;
	}
	
	
}