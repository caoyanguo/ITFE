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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
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
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 凭证收入日报表(3401)转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3401 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3401.class);
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
//		vDto.getSorgcode();//机构代码
//		vDto.getScreatdate();//凭证日期
//		vDto.getSvtcode();//凭证类型3208-实拨资金退回，3401-收入日报表，3402-库存日报表
//		vDto.getStrecode();//国库代码
//		vDto.getSstyear();//报表类型1-日报 2-月报 3-年报
//		vDto.getSpaybankcode();//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
//		vDto.getShold1();//预算种类1-预算内2-预算外
//		vDto.getShold3();//辖属标志0-本级，1-全辖
//		vDto.getShold2();//预算级次1-中央，2-省，3-市，4-县，5-乡
//		vDto.getShold4();//报表种类1-正常期预算收入报表2-正常期退库报表体3-正常期调拨收入报表4-总额分成报表体5-调整期预算收入报表体6-调整期退库报表体7-调整期调拨收入报表体
		//S_TRIMFLAG IS '调整期标志0 本年度(正常期)1 上年度(调整期)';S_BUDGETTYPE IS '预算种类1-预算内2-预算外';S_BUDGETLEVELCODE IS '预算级次代码';.S_BELONGFLAG IS '辖属标志0-本级，1-全辖';
		//S_FINORGCODE IS '财政机关代码';S_TAXORGCODE IS '征收机关代码';S_BILLKIND IS '报表种类1-正常期预算收入报表体，2-正常期退库报表体，3-正常期调拨收入报表体，4-总额分成报表体，5-正常期共享分成报表体，6-调整期预算收入报表体，7-调整期退库报表体，8-调整期调拨收入报表体，9-调整期共享分成报表体';
		List<String> ysjc = new ArrayList<String>();
		List<String> bbzl = new ArrayList<String>();
		ysjc.add("1");ysjc.add("2");ysjc.add("3");ysjc.add("4");ysjc.add("5");//预算级次1-中央，2-省，3-市，4-县，5-乡
		bbzl.add("1");bbzl.add("2");bbzl.add("3");bbzl.add("4");bbzl.add("6");bbzl.add("7");bbzl.add("8");//1-正常期预算收入报表体，2-正常期退库报表体，3-正常期调拨收入报表体，4-总额分成报表体，6-调整期预算收入报表体，7-调整期退库报表体，8-调整期调拨收入报表体
		String shold2 = vDto.getShold2();
		String shold4 = vDto.getScheckvouchertype();
		List getList = new ArrayList();
		List tempList = null;
		TrIncomedayrptDto dto=new TrIncomedayrptDto();
		dto.setStrecode(vDto.getStrecode());//国库代码
		dto.setStaxorgcode(vDto.getSpaybankcode());//征收机关代码
		dto.setSrptdate(vDto.getScheckdate());//报表日期
		dto.setSbelongflag(vDto.getShold3());//辖属标志
		dto.setSbudgetlevelcode(vDto.getShold2());//预算级次
		dto.setSbudgettype(vDto.getShold1());//预算种类
		dto.setSbillkind(vDto.getScheckvouchertype());//报表种类
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
		return getList;//if(vDto.getShold2()!=null&&"6".equals(vDto.getShold2()))//return tranfordf(dto,vDto);//else//return tranforList(dto,vDto);
	}
	private List tranforList(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List<TrIncomedayrptDto> list=null;
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
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
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			StringBuffer sql = new StringBuffer("SELECT * FROM TR_INCOMEDAYRPT where S_FINORGCODE=? and S_TRECODE = ? and S_RPTDATE = ?");
			execDetail.addParam(cDto.getSfinorgcode());
			execDetail.addParam(dto.getStrecode());
			execDetail.addParam(dto.getSrptdate());
			//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444),3-国税大类(111111111111),4-地税大类(222222222222),5-海关大类(333333333333),6-其它大类(555555555555),
			if(vDto.getSpaybankcode()!=null&&"0".equals(vDto.getSpaybankcode()))//征收机关代码
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"1".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"2".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? ");
				execDetail.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"3".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"4".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"5".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"6".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(vDto.getSpaybankcode());
			}
			if(dto.getSbelongflag()!=null)
			{
				sql.append(" and S_BELONGFLAG = ?");
				execDetail.addParam(dto.getSbelongflag());
			}
			if(dto.getStrimflag()!=null)
			{
				sql.append(" and S_TRIMFLAG = ?");
				execDetail.addParam(dto.getStrimflag());
			}
			if(dto.getSbudgettype()!=null)
			{
				sql.append(" and S_BUDGETTYPE = ?");
				execDetail.addParam(dto.getSbudgettype());
			}
			if(dto.getSbudgetlevelcode()!=null)
			{
				sql.append(" and S_BUDGETLEVELCODE = ?");
				execDetail.addParam(dto.getSbudgetlevelcode());
			}
			if(dto.getSbillkind()!=null)
			{
				sql.append(" and S_BILLKIND = ?");
				execDetail.addParam(dto.getSbillkind());
			}
			
			sql.append(" and ((N_MONEYDAY<>? or N_MONEYMONTH<>? or N_MONEYYEAR<>?)");
			execDetail.addParam(0);
			execDetail.addParam(0);
			execDetail.addParam(0);
			if(vDto.getSorgcode().startsWith("06")){
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")>=0)
				{
					sql.append(" or (N_MONEYDAY=?)");
					execDetail.addParam(0);
				}
			}else{
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")<0)
				{
					sql.append(" and (N_MONEYDAY<>?)");
					execDetail.addParam(0);
				}
			}
			sql.append(") order by S_BUDGETSUBCODE asc ");
			list = (List)execDetail.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();
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
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		if(list==null||list.size()==0){
			return null;
		}
		TvVoucherinfoDto voucherdto = makeVouInfoDto(list, vDto, cDto);
		List lists=new ArrayList();
		lists.add(list);
		lists.add(voucherdto);
		lists.add(cDto);
		Map map= tranfor(lists);				
		List voucherList=new ArrayList();
		voucherList.add(map);
		voucherList.add(voucherdto);
		return voucherList;
	}
	
	/**
	 * 生成索引表，与自动发送报表公用一个方法
	 * @param list
	 * @param vDto
	 * @param cDto
	 * @return
	 * @throws ITFEBizException 
	 */
	private  TvVoucherinfoDto  makeVouInfoDto(List list,TvVoucherinfoDto vDto,TsConvertfinorgDto cDto) throws ITFEBizException{
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
		return voucherdto;
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
			List<TsTreasuryDto> treCodeList = PublicSearchFacade.getSubTreCode(treDto);
			if(treCodeList!=null&&treCodeList.size()>0)
			{
				for(int i=0;i<treCodeList.size();i++)
				{
					treDto = treCodeList.get(i);
					if(i==0)
						sql.append(" (((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					else if(i==(treCodeList.size()-1))
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"')))");
					else
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					if(treCodeList.size()==1)
						sql.append(")");
				}
			}
			sql.append(" AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ");
			//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444),3-国税大类(111111111111),4-地税大类(222222222222),5-海关大类(333333333333),6-其它大类(555555555555),
			if(vDto.getSpaybankcode()!=null&&"0".equals(vDto.getSpaybankcode()))//征收机关代码
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"'");
			}else if(vDto.getSpaybankcode()!=null&&"1".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"2".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"3".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"4".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"5".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"6".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}else
			{
				sql.append(" and S_TAXORGCODE='"+vDto.getSpaybankcode()+"' ");
			}
//			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//辖属标志本级
//				sql.append(" and S_BELONGFLAG='"+MsgConstant.RULE_SIGN_SELF+"'");
//			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//全辖
//				sql.append(" and S_BELONGFLAG='"+MsgConstant.RULE_SIGN_ALL+"'");
			sql.append(" and ((N_MONEYDAY<>0 or N_MONEYMONTH<>0 or N_MONEYYEAR<>0)");
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")<0)
				sql.append(" or (N_MONEYDAY<>0)");
				sql.append(")  order by S_BUDGETSUBCODE");
			dto.setStrecode(null);
			dto.setSrptdate(null);
			dto.setStaxorgcode(null);
			dto.setSbudgetlevelcode(null);
			list = CommonFacade.getODB().findRsByDtoForWhere(dto, sql.toString());
			ReportLKXMUtil reportUtil = new ReportLKXMUtil();
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")<0&&ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=null,")<0)
				list = reportUtil.computeLKM(vDto.getSorgcode(), list, "1");
			else if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")>=0)
				list = reportUtil.computeLKM(vDto.getSorgcode(), list, "0");
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
		TvVoucherinfoDto voucherdto = makeVouInfoDto(list, vDto, cDto);
		List lists=new ArrayList();
		lists.add(list);
		lists.add(voucherdto);
		lists.add(cDto);
		Map map= tranfor(lists);				
		List voucherList=new ArrayList();
		voucherList.add(map);
		voucherList.add(voucherdto);
		return voucherList;
	}

	/**
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            	生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor(List lists) throws ITFEBizException{
		try{	
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));		
			vouchermap.put("VtCode", vDto.getSvtcode());		
			vouchermap.put("VouDate", vDto.getScreatdate());		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("FundTypeCode", "1");//资金性质
			vouchermap.put("BudgetType", getString(vDto.getShold1()));//预算种类
			vouchermap.put("BelongFlag", getString(vDto.getShold3()));//辖属标志
			vouchermap.put("BudgetLevelCode", getString(vDto.getShold2()));//预算级次
			if("6".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "5");
			else if("7".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "6");
			else if("8".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "7");
			else
				vouchermap.put("BillKind", getString(vDto.getScheckvouchertype()));//报表种类
			vouchermap.put("ReportDate", vDto.getScheckdate());		
			vouchermap.put("FinOrgCode", cDto.getSfinorgcode());
			vouchermap.put("TreCode", vDto.getStrecode());//国库代码
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//国库名称
			vouchermap.put("SumMoney", null);//本日日报累计金额
			vouchermap.put("Hold1", "");		
			vouchermap.put("Hold2", "");	
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			Map<String,TdTaxorgParamDto> taxorgcodeMap = getTdTaxorgParam(vDto.getSorgcode());
			for (TrIncomedayrptDto dto:list) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
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
				vouchermap.put("FundTypeCode", "1");//资金性质
				Detailmap.put("BudgetType", dto.getSbudgettype()==null?"":dto.getSbudgettype()); 
				Detailmap.put("BudgetLevelCode",dto.getSbudgetlevelcode()==null?"":dto.getSbudgetlevelcode()); 
				Detailmap.put("BudgetSubjectCode", dto.getSbudgetsubcode()==null?"":dto.getSbudgetsubcode()); 
				Detailmap.put("BudgetSubjectName", dto.getSbudgetsubname()==null?"":dto.getSbudgetsubname()); 
				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
				Detailmap.put("TenDayAmt", MtoCodeTrans.transformString(dto.getNmoneytenday())); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("QuarterAmt", MtoCodeTrans.transformString(dto.getNmoneyquarter())); 
				Detailmap.put("YearAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", "");
				if(dto.getSbudgetsubcode()!=null&&dto.getSbudgetsubcode().equals(StateConstant.BUGGET_IN_FUND+StateConstant.MOVE_INCOME))
					allamt=dto.getNmoneyday();										
				Detail.add(Detailmap);
			}					
			vouchermap.put("SumMoney",MtoCodeTrans.transformString(allamt));	
			vDto.setNmoney(allamt);
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
	private String getString(String value)
	{
		if(value==null)
			value="";
		return value;
	}
}
