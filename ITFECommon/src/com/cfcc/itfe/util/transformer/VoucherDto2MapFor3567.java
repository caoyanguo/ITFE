package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.ReportLKXMUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 收入月报表3507转化-->3567长春收入月报
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3567 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3567.class);
	private  BigDecimal Total;
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		Total=new BigDecimal("0.00");
		vDto.setScheckdate(TimeFacade.getEndDateOfMonth(vDto.getScheckdate()));
		vDto.setSdealno(VoucherUtil.getGrantSequence());
//		vDto.getSorgcode();//机构代码
//		vDto.getScreatdate();//凭证日期
//		vDto.getSvtcode();//凭证类型3453-全省库存日报表，3401-收入日报表，3402-库存日报表,3567-收入月报表
//		vDto.getStrecode();//国库代码
//		vDto.getSstyear();//报表类型1-日报 2-月报 3-年报
//		vDto.getSpaybankcode();//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
//		vDto.getShold1();//预算种类1-预算内2-预算外
//		vDto.getShold2();//辖属标志0-本级，1-全辖
//		vDto.getShold3();//预算级次1-中央，2-省，3-市，4-县，5-乡
//		vDto.getShold4();//报表种类1-正常期预算收入报表2-正常期退库报表体3-正常期调拨收入报表4-总额分成报表体5-调整期预算收入报表体6-调整期退库报表体7-调整期调拨收入报表体
		//S_TRIMFLAG IS '调整期标志0 本年度(正常期)1 上年度(调整期)';S_BUDGETTYPE IS '预算种类1-预算内2-预算外';S_BUDGETLEVELCODE IS '预算级次代码';.S_BELONGFLAG IS '辖属标志0-本级，1-全辖';
		//S_FINORGCODE IS '财政机关代码';S_TAXORGCODE IS '征收机关代码';S_BILLKIND IS '报表种类1-正常期预算收入报表体，2-正常期退库报表体，3-正常期调拨收入报表体，4-总额分成报表体，5-正常期共享分成报表体，6-调整期预算收入报表体，7-调整期退库报表体，8-调整期调拨收入报表体，9-调整期共享分成报表体';
		TrIncomedayrptDto dto=new TrIncomedayrptDto();
		dto.setStrecode(vDto.getStrecode());//国库代码
		dto.setStaxorgcode(vDto.getSpaybankcode());//征收机关代码
		dto.setSrptdate(vDto.getScheckdate());//报表日期
		dto.setSbelongflag(vDto.getShold3());//辖属标志
		dto.setSbudgetlevelcode(vDto.getShold2());//预算级次
		dto.setSbudgettype(vDto.getShold1());//预算种类
		dto.setSbillkind(vDto.getScheckvouchertype());//报表种类
//		if(vDto.getShold2()!=null&&"6".equals(vDto.getShold2()))
//			return tranfordf(dto,vDto);
//		else
//			return tranforList(dto,vDto);
		List getList = new ArrayList();
		List tempList = null;
		String shold2 = vDto.getShold2();
		List<String> ysjc = new ArrayList<String>();
		List<String> bbzl = new ArrayList<String>();
		ysjc.add("1");ysjc.add("2");ysjc.add("3");ysjc.add("4");ysjc.add("5");//预算级次1-中央，2-省，3-市，4-县，5-乡
		bbzl.add("1");bbzl.add("2");bbzl.add("3");bbzl.add("4");bbzl.add("6");bbzl.add("7");bbzl.add("8");//1-正常期预算收入报表体，2-正常期退库报表体，3-正常期调拨收入报表体，4-总额分成报表体，6-调整期预算收入报表体，7-调整期退库报表体，8-调整期调拨收入报表体
		String shold4 = vDto.getScheckvouchertype();
		if(shold2!=null&&"6".equals(shold2))//地方级报表
		{
			tempList = tranfordf(dto,vDto);
			if(tempList!=null&&tempList.size()>0)
				getList.add(tempList);
		}else if(shold2==null||"".equals(shold2))
		{
			for(int ys=0;ys<ysjc.size();ys++)
			{
				dto.setStrecode(vDto.getStrecode());//国库代码
				dto.setStaxorgcode(vDto.getSpaybankcode());//征收机关代码
				dto.setSrptdate(vDto.getScheckdate());//报表日期
				vDto.setShold2(ysjc.get(ys));
				dto.setSbudgetlevelcode(ysjc.get(ys));
				if(shold4==null||"".equals(shold4))
				{
					for(int bb=0;bb<bbzl.size();bb++)
					{
						vDto.setScheckvouchertype(bbzl.get(bb));
						dto.setSbillkind(bbzl.get(bb));
						tempList = tranforList(dto,vDto);
						if(tempList!=null&&tempList.size()>0)
							getList.add(tempList);
					}
				}else
				{
					tempList = tranforList(dto,vDto);
					if(tempList!=null&&tempList.size()>0)
						getList.add(tempList);
				}
			}	
		}else if(shold4==null||"".equals(shold4))
		{
			for(int bb=0;bb<bbzl.size();bb++)
			{
				vDto.setScheckvouchertype(bbzl.get(bb));
				dto.setSbillkind(bbzl.get(bb));
				tempList = tranforList(dto,vDto);
				if(tempList!=null&&tempList.size()>0)
					getList.add(tempList);
			}
		}else
		{
			tempList = tranforList(dto,vDto);
			if(tempList!=null&&tempList.size()>0)
				getList.add(tempList);
		}
		return getList;
	}
	private List tranforList(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List<TrIncomedayrptDto> list=null;
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor sqlExe = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List TsConvertfinorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
			if(TsConvertfinorgDtoList==null||TsConvertfinorgDtoList.size()==0){
				throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的财政代码未维护！");
			}
			cDto=(TsConvertfinorgDto) TsConvertfinorgDtoList.get(0);
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){			
				throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的区划代码未维护！");
			}
			String sql = "SELECT max(s_rptdate)  FROM Tr_Incomedayrpt WHERE S_FINORGCODE='"+cDto.getSfinorgcode()+"' AND s_trecode='"+dto.getStrecode()+"' and S_RPTDATE like '"+dto.getSrptdate().substring(0,6)+"%'";
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			SQLResults res = sqlExe.runQueryCloseCon(sql);
			if(res!=null)
				dto.setSrptdate(res.getString(0, 0));
			//为了走索引改成SQL语句，不用Dto
			StringBuffer where = new StringBuffer(" and S_FINORGCODE='"+cDto.getSfinorgcode()+"' and (S_TRECODE = '"+dto.getStrecode()+"') AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ");
			//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444),3-国税大类(111111111111),4-地税大类(222222222222),5-海关大类(333333333333),6-其它大类(555555555555),
			if(vDto.getSpaybankcode()!=null&&"0".equals(vDto.getSpaybankcode()))//征收机关代码
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"'");
			}else if(vDto.getSpaybankcode()!=null&&"1".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"2".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"3".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"4".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"5".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"6".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}
			where.append(" and ((N_MONEYDAY<>0 or N_MONEYMONTH<>0 or N_MONEYYEAR<>0)");
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")<0)
				where.append(" or (N_MONEYDAY<>0)");
			where.append(") order by S_BUDGETSUBCODE");
			dto.setStrecode(null);
			dto.setSrptdate(null);
			dto.setStaxorgcode(null);
			list = CommonFacade.getODB().findRsByDtoForWhere(dto, where.toString());
			if(list!=null&&list.size()>0)
			{
				ReportLKXMUtil reportUtil = new ReportLKXMUtil();
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")<0&&ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=null,")<0)
					list = reportUtil.computeLKM(vDto.getSorgcode(), list, "1");
				else if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")>=0)
					list = reportUtil.computeLKM(vDto.getSorgcode(), list, "0");
			}
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}catch(Exception e2 ){
			logger.error(e2);
			throw new ITFEBizException(e2.getMessage(),e2);
		}finally
		{
			if(sqlExe!=null)
				sqlExe.closeConnection();
		}
		if(list==null||list.size()==0){
			return null;
		}
		List sendList = getSubLists(list,500);
		List listss=new ArrayList();
		if(sendList!=null&&sendList.size()>0)
		{
			for(int i=0;i<sendList.size();i++)
			{
				list = (List)sendList.get(i);
				String dirsep = File.separator;
				String mainvou=VoucherUtil.getGrantSequence();
				String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
		        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
				TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
				voucherdto.setSdealno(mainvou);
				voucherdto.setSorgcode(vDto.getSorgcode());
				voucherdto.setSadmdivcode(vDto.getSadmdivcode());
				voucherdto.setSvtcode(vDto.getSvtcode());
				voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
				voucherdto.setScheckdate(vDto.getScheckdate().substring(0,6));//报表日期
				voucherdto.setStrecode(vDto.getStrecode());
				voucherdto.setSadmdivcode(cDto.getSadmdivcode());
				voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
//				voucherdto.setSattach(vDto.getSattach());//报表类型日报月报年报
				voucherdto.setSattach("2");//报表类型日报月报年报
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype());//报表种类
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//征收机关代码
				voucherdto.setShold1(vDto.getShold1());//预算种类
				
				if (null!=vDto.getShold2()&&vDto.getShold2().trim().length()>1) {
					voucherdto.setShold2(vDto.getShold2());//预算级次
				} else {
					voucherdto.setShold2(list.get(0).getSbudgetlevelcode());//预算级次
				}
				
				voucherdto.setShold3(vDto.getShold3());//辖属标志
				voucherdto.setSfilename(FileName);
				voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				voucherdto.setSdemo("处理成功");				
				voucherdto.setSvoucherflag("1");
				voucherdto.setSvoucherno(mainvou);		
				voucherdto.setSvoucherflag("1");
				voucherdto.setSvoucherno(mainvou);
				voucherdto.setIcount(list.size());
				
				List lists=new ArrayList();
				lists.add(list);
				lists.add(voucherdto);
				lists.add(cDto);
				Map map= tranfor(lists,sendList.size(),(i+1),vDto);				
//				List voucherList=new ArrayList();
				listss.add(map);
				listss.add(voucherdto);
//				listss.add(voucherList);
			}
		}
		
		return listss;
	}
	private List tranfordf(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List<TrIncomedayrptDto> list=null;
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		StringBuffer sql = new StringBuffer(" and ");
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List TsConvertfinorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
			if(TsConvertfinorgDtoList==null||TsConvertfinorgDtoList.size()==0){
				throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的财政代码未维护！");
			}
			cDto=(TsConvertfinorgDto) TsConvertfinorgDtoList.get(0);
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){			
				throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的区划代码未维护！");
			}
			TsTreasuryDto treDto = new TsTreasuryDto();
			treDto.setStrecode(vDto.getStrecode());
			treDto.setSorgcode(vDto.getSorgcode());
			List<TsTreasuryDto> trel = CommonFacade.getODB().findRsByDto(treDto);
			if(trel!=null&&trel.size()>0)
				treDto = trel.get(0);
			List<TsTreasuryDto> treCodeList = PublicSearchFacade.getSubTreCode(treDto);
			if(treCodeList!=null&&treCodeList.size()>0)
			{
				for(int i=0;i<treCodeList.size();i++)
				{
					treDto = treCodeList.get(i);
					if(i==0&&treDto.getStrelevel()!=null)
						sql.append(" (((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					else if(i==(treCodeList.size()-1)&&treDto.getStrelevel()!=null)
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"')))");
					else if(treDto.getStrelevel()!=null)
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					else
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='error'))");
				}
			}
			sql.append(" AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ");
			if(dto.getStaxorgcode()!=null)
				sql.append("AND (S_TAXORGCODE = '"+dto.getStaxorgcode()+"' ) ");
			dto.setStrecode(null);
			dto.setSrptdate(null);
			dto.setStaxorgcode(null);
			dto.setSbudgetlevelcode(null);
			list = CommonFacade.getODB().findRsByDtoForWhere(dto, sql.toString());
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}catch(Exception e2 ){
			logger.error(e2);
			throw new ITFEBizException(e2.getMessage(),e2);
		}
		if(list==null||list.size()==0){
			return null;
		}
		List sendList = getSubLists(list,500);
		List listss=new ArrayList();
		if(sendList!=null&&sendList.size()>0)
		{
			for(int i=0;i<sendList.size();i++)
			{
				list = (List)sendList.get(i);
				String dirsep = File.separator;
				String mainvou=VoucherUtil.getGrantSequence();
				String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
		        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
				TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
				voucherdto.setSdealno(mainvou);
				voucherdto.setSorgcode(vDto.getSorgcode());
				voucherdto.setSadmdivcode(vDto.getSadmdivcode());
				voucherdto.setSvtcode(vDto.getSvtcode());
				voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
				voucherdto.setScheckdate(vDto.getScheckdate());//报表日期
				voucherdto.setStrecode(vDto.getStrecode());
				voucherdto.setSadmdivcode(cDto.getSadmdivcode());
				voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
				voucherdto.setSattach(vDto.getSattach());//报表类型日报月报年报
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype());//报表种类
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//征收机关代码
				voucherdto.setShold1(vDto.getShold1());//预算种类
				voucherdto.setShold2(vDto.getShold2());//预算级次
				voucherdto.setShold3(vDto.getShold3());//辖属标志
				voucherdto.setSfilename(FileName);
				voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				voucherdto.setSdemo("处理成功");				
				voucherdto.setSvoucherflag("1");
				voucherdto.setSvoucherno(mainvou);		
				voucherdto.setSvoucherflag("1");
				voucherdto.setSvoucherno(mainvou);
				voucherdto.setIcount(list.size());
				List lists=new ArrayList();
				lists.add(list);
				lists.add(voucherdto);
				lists.add(cDto);
				Map map= tranfor(lists,sendList.size(),(i+1),vDto);				
				List voucherList=new ArrayList();
				voucherList.add(map);
				voucherList.add(voucherdto);
				listss.add(voucherList);
			}
		}
		
		return listss;
	}
	/**
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            	生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor(List lists,int packnum,int packno,TvVoucherinfoDto infodto) throws ITFEBizException{
		try{	
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
//			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));	//业务年度	
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号		
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo", infodto.getSdealno());//对账单号
			vouchermap.put("ChildPackNum", String.valueOf(packnum));//子包总数
			vouchermap.put("CurPackNo", String.valueOf(packno));//本包序号
			vouchermap.put("BudgetType", vDto.getShold1());//预算种类
			vouchermap.put("BelongFlag", vDto.getShold3());//辖属标志
			vouchermap.put("BudgetLevelCode", vDto.getShold2());//预算级次
			if("6".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "5");
			else if("7".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "6");
			else if("8".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "7");
			else
				vouchermap.put("BillKind", vDto.getScheckvouchertype());//报表种类
			vouchermap.put("ChkMonth", vDto.getScheckdate()!=null&&vDto.getScheckdate().length()>6?vDto.getScheckdate().substring(0,6):"");		
			vouchermap.put("FinOrgCode", cDto.getSfinorgcode());//财政机关代码
			vouchermap.put("TreCode", vDto.getStrecode());//国库代码
			vouchermap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//国库名称
			vouchermap.put("XCheckResult", "");//对账结果
			vouchermap.put("XCheckReason", "");//不符原因
			Map<String,TdTaxorgParamDto> taxorgcodeMap = getTdTaxorgParam(vDto.getSorgcode());
			if(list.get(0).getStaxorgcode()==null){
				vouchermap.put("Hold1", "");//预留字段1
			}else{
				if(taxorgcodeMap.get(list.get(0).getStaxorgcode())!=null){
					vouchermap.put("Hold1", taxorgcodeMap.get(list.get(0).getStaxorgcode()).getStaxorgname());	
				}else{
					vouchermap.put("Hold1", VoucherUtil.getStaxorgname(list.get(0).getStaxorgcode()));	
				}
			}
			vouchermap.put("Hold2", "");//预留字段2	
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			for (TrIncomedayrptDto dto:list) {

				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", "3567"+StampFacade.getStampSendSeq("3567")); // 流水号
				Detailmap.put("AdmDivCode", vDto.getSadmdivcode()); // 区划代码
				Detailmap.put("StYear", vDto.getScreatdate().substring(0, 4)); 
				Detailmap.put("TaxOrgCode",dto.getStaxorgcode()==null?"":dto.getStaxorgcode());			
				if(dto.getStaxorgcode()==null)
					Detailmap.put("TaxOrgName","");
				else if(taxorgcodeMap.get(dto.getStaxorgcode())==null){
					Detailmap.put("TaxOrgName", VoucherUtil.getStaxorgname(dto.getStaxorgcode()));
				}else{
					Detailmap.put("TaxOrgName", taxorgcodeMap.get(dto.getStaxorgcode()).getStaxorgname());
				}	 
				Detailmap.put("BudgetType", dto.getSbudgettype()==null?"":dto.getSbudgettype()); 
				Detailmap.put("BudgetLevelCode",dto.getSbudgetlevelcode()==null?"":dto.getSbudgetlevelcode()); 
				Detailmap.put("BudgetSubjectCode", dto.getSbudgetsubcode()==null?"":dto.getSbudgetsubcode()); 
				Detailmap.put("BudgetSubjectName", dto.getSbudgetsubname()==null?"":dto.getSbudgetsubname()); 
				
//				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
//				Detailmap.put("TenDayAmt", MtoCodeTrans.transformString(dto.getNmoneytenday())); 
//				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("CurIncomeAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("SumIncomeAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("XCheckResult", "");//对账结果
				Detailmap.put("XCheckReason", "");//不符原因
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
				allamt=allamt.add(dto.getNmoneymonth());										
				Detail.add(Detailmap);
			}		
			Total=Total.add(allamt);			
			vDto.setNmoney(allamt);
			vDto.setNmoney(Total);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
		
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}
	private List getSubLists(List list,int subsize)
	{
		List getList = null;
		if(list!=null&&list.size()>0)
		{
			if(subsize<1)
				subsize=500;
			int count = list.size()/subsize;
			int yu = list.size()%subsize;
			getList = new ArrayList();
			for(int i=0;i<count;i++)
				getList.add(list.subList(i*subsize, subsize*(i+1)));
			if(yu>0)
				getList.add(list.subList(count*subsize, (count*subsize)+yu));
		}
		return getList;
	}
	private Map<String,TdTaxorgParamDto> getTdTaxorgParam(String sorgcode) throws ITFEBizException 
	{
		Map<String,TdTaxorgParamDto> getmap = new HashMap<String,TdTaxorgParamDto>();
		TdTaxorgParamDto tDto=new TdTaxorgParamDto();			
		tDto.setSbookorgcode(sorgcode);
		try {
			List<TdTaxorgParamDto> tdTaxorgParamDtoList=CommonFacade.getODB().findRsByDto(tDto);
			if(tdTaxorgParamDtoList!=null&&tdTaxorgParamDtoList.size()>0)
			{
				for(TdTaxorgParamDto dto:tdTaxorgParamDtoList)
					getmap.put(dto.getStaxorgcode(), dto);
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			throw new ITFEBizException(e);
		}
		return getmap;
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}

}
