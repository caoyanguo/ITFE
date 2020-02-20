package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeSubDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.Report3511LKXMUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * 收入报表对账（3511）
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3511 implements IVoucherDto2Map{
	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3507.class);
	private Map<String,TdTaxorgParamDto> taxorgcodeMap = null;
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
//		dto.sattach();//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
//		dto.shold1();//预算种类1-预算内2-预算外
//		dto.shold3();//辖属标志0-本级，1-全辖
//		dto.setShold2();//预算级次1-中央，2-省，3-市，4-县，5-乡
//		dto.scheckvouchertype();//报表种类1-正常期预算收入报表2-正常期退库报表体3-正常期调拨收入报表4-总额分成报表体5-调整期预算收入报表体6-调整期退库报表体7-调整期调拨收入报表体
		//S_TRIMFLAG IS '调整期标志0 本年度(正常期)1 上年度(调整期)';S_BUDGETTYPE IS '预算种类1-预算内2-预算外';S_BUDGETLEVELCODE IS '预算级次代码';.S_BELONGFLAG IS '辖属标志0-本级，1-全辖';
		//S_FINORGCODE IS '财政机关代码';S_TAXORGCODE IS '征收机关代码';S_BILLKIND IS '报表种类1-正常期预算收入报表体，2-正常期退库报表体，3-正常期调拨收入报表体，4-总额分成报表体，5-正常期共享分成报表体，6-调整期预算收入报表体，7-调整期退库报表体，8-调整期调拨收入报表体，9-调整期共享分成报表体';
//		TrIncomedayrptDto dto=new TrIncomedayrptDto();
//		dto.setStrecode(vDto.getStrecode());//国库代码
//		dto.setStaxorgcode(vDto.getSattach());//征收机关代码
//		dto.setSbelongflag(vDto.getShold3());//辖属标志
//		dto.setSbudgetlevelcode(vDto.getShold2());//预算级次
//		dto.setSbudgettype(vDto.getShold1());//预算种类
//		dto.setSbillkind(vDto.getScheckvouchertype());//报表种类
		if(taxorgcodeMap==null)
			taxorgcodeMap = getTdTaxorgParam(vDto.getSorgcode());
		if(vDto.getShold2()!=null&&"6".equals(vDto.getShold2()))
			return tranfordf(vDto);
		else
			return tranforList(vDto);
	}
	private List tranforList(TvVoucherinfoDto vDto) throws ITFEBizException
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
			}else
				vDto.setSadmdivcode(cDto.getSadmdivcode());
//			dto.sattach();//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
//			dto.shold1();//预算种类1-预算内2-预算外//			dto.shold3();//辖属标志0-本级，1-全辖
//			dto.setShold2();//预算级次1-中央，2-省，3-市，4-县，5-乡//			dto.scheckvouchertype();//报表种类
			StringBuffer sql = new StringBuffer("SELECT *  FROM hTr_Incomedayrpt WHERE "+" S_FINORGCODE=? and S_TRECODE=? AND S_RPTDATE>=? and S_RPTDATE<=?");//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444),3-国税大类(111111111111),4-地税大类(222222222222),5-海关大类(333333333333),6-其它大类(555555555555),
			
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//征收机关代码
				sql.append(" and S_TAXORGCODE=?");
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? ");
			else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else
				sql.append(" and S_TAXORGCODE=? ");
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//辖属标志本级
				sql.append(" and S_BELONGFLAG=?");
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//全辖
				sql.append(" and S_BELONGFLAG=?");
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//预算种类
				sql.append(" and S_BUDGETTYPE=?");
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2())&&!"A".equals(vDto.getShold2().trim()))//预算级次
				sql.append(" and S_BUDGETLEVELCODE=?");
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//报表种类
				sql.append(" and S_BILLKIND=?");
			sql.append(" and ((N_MONEYDAY<>? or N_MONEYMONTH<>? or N_MONEYYEAR<>?)");
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sql.append(" and (N_MONEYDAY>?)");
			sql.append(") order by S_RPTDATE,S_BUDGETSUBCODE asc");
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe.addParam(cDto.getSfinorgcode());
			sqlExe.addParam(vDto.getStrecode());
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//征收机关代码
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//辖属标志本级
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//全辖
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//预算种类
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2())&&!"A".equals(vDto.getShold2().trim()))//预算级次
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//报表种类
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list = (List<TrIncomedayrptDto>) sqlExe.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();
			sqlExe.addParam(cDto.getSfinorgcode());
			sqlExe.addParam(vDto.getStrecode());
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//征收机关代码
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//辖属标志本级
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//全辖
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//预算种类
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2())&&!"A".equals(vDto.getShold2().trim()))//预算级次
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//报表种类
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list.addAll((List<TrIncomedayrptDto>) sqlExe.runQueryCloseCon(StringUtil.replace(sql.toString(), "hTr_Incomedayrpt", "Tr_Incomedayrpt"),TrIncomedayrptDto.class).getDtoCollection());
			Report3511LKXMUtil reportUtil = new Report3511LKXMUtil();
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
				if(vDto.getSdealno()==null||"".equals(vDto.getSdealno()))
					vDto.setSdealno(mainvou);
				String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
		        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
				TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
				voucherdto.setSdealno(mainvou);
				voucherdto.setSorgcode(vDto.getSorgcode());
				voucherdto.setSadmdivcode(vDto.getSadmdivcode());
				voucherdto.setSvtcode(vDto.getSvtcode());
				voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
				voucherdto.setScheckdate(vDto.getScheckdate());//对帐起始日期
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//对帐结束日期
				voucherdto.setStrecode(vDto.getStrecode());
				voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
				voucherdto.setSattach(vDto.getSattach()==null?"":vDto.getSattach());//征收机关代码
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//报表种类
				voucherdto.setShold1(vDto.getShold1()==null?"":vDto.getShold1());//预算种类
				voucherdto.setShold2(vDto.getShold2()==null?"":vDto.getShold2());//预算级次
				voucherdto.setShold3(vDto.getShold3()==null?"":vDto.getShold3());//辖属标志
				voucherdto.setSvoucherflag("1");
				voucherdto.setSfilename(FileName);
//				if(ITFECommonConstant.PUBLICPARAM.indexOf(",stampmode=sign,")<0)//是否采用签名
//				{
				voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				voucherdto.setSdemo("处理成功");
				voucherdto.setSvoucherno(mainvou);
				voucherdto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
				voucherdto.setSext4(vDto.getSdealno());//对帐单号
				voucherdto.setSext2(String.valueOf(sendList.size()));//包总数
				voucherdto.setSext3(String.valueOf(i+1));//包序号
				voucherdto.setIcount(list.size());
				List lists=new ArrayList();
				lists.add(list);
				lists.add(voucherdto);
				lists.add(cDto);
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map= tranfor(lists,sendList.size(),(i+1),vDto,idtoList);				
				List voucherList=new ArrayList();
				voucherList.add(map);
				voucherList.add(voucherdto);
				voucherList.add(idtoList);
				listss.add(voucherList);
			}
		}
		
		return listss;
	}
	private List tranfordf(TvVoucherinfoDto vDto) throws ITFEBizException
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
			}else
				vDto.setSadmdivcode(cDto.getSadmdivcode());
			TsTreasuryDto treDto = new TsTreasuryDto();
			treDto.setStrecode(vDto.getStrecode());
//			List<TsTreasuryDto> treCodeList = PublicSearchFacade.getSubTreCode(treDto);
//			if(treCodeList!=null&&treCodeList.size()>0)
//			{
//				for(int i=0;i<treCodeList.size();i++)
//				{
//					treDto = treCodeList.get(i);
//					if(i==0&&treDto.getStrelevel()!=null)
//						tresql.append(" (((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
//					else if(i==(treCodeList.size()-1)&&treDto.getStrelevel()!=null)
//						tresql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"')))");
//					else if(treDto.getStrelevel()!=null)
//						tresql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
//					else
//						tresql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='error'))");
//				}
//			}
			StringBuffer sql = new StringBuffer("SELECT *  FROM hTr_Incomedayrpt WHERE S_RPTDATE>=? and S_RPTDATE<=? ");//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444),3-国税大类(111111111111),4-地税大类(222222222222),5-海关大类(333333333333),6-其它大类(555555555555),
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//征收机关代码
				sql.append(" and S_TAXORGCODE=?");
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? ");
			else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else
				sql.append(" and S_TAXORGCODE=? ");
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//辖属标志本级
				sql.append(" and S_BELONGFLAG=?");
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//全辖
				sql.append(" and S_BELONGFLAG=?");
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//预算种类
				sql.append(" and S_BUDGETTYPE=?");
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2()))//预算级次
				sql.append(" and S_BUDGETLEVELCODE=?");
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//报表种类
				sql.append(" and S_BILLKIND=?");
			sql.append(" and ((N_MONEYDAY<>? or N_MONEYMONTH<>? or N_MONEYYEAR<>?)");
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sql.append(" and (N_MONEYDAY>?)");
			sql.append(") order by S_RPTDATE,S_BUDGETSUBCODE asc");
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//征收机关代码
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//辖属标志本级
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//全辖
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//预算种类
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2()))//预算级次
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//报表种类
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list = (List<TrIncomedayrptDto>) sqlExe.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//征收机关代码
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//辖属标志本级
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//全辖
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//预算种类
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2()))//预算级次
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//报表种类
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list.addAll((List<TrIncomedayrptDto>) sqlExe.runQueryCloseCon(StringUtil.replace(sql.toString(), "hTr_Incomedayrpt", "Tr_Incomedayrpt"),TrIncomedayrptDto.class).getDtoCollection());
			if(list!=null&&list.size()>0)
			{
				Report3511LKXMUtil reportUtil = new Report3511LKXMUtil();
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
				voucherdto.setScheckdate(vDto.getScheckdate());//对帐起始日期
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//对帐截止日期
				voucherdto.setStrecode(vDto.getStrecode());
				voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//报表种类
				voucherdto.setSattach(vDto.getSattach()==null?"":vDto.getSattach());//征收机关代码
				voucherdto.setShold1(vDto.getShold1()==null?"":vDto.getShold1());//预算种类
				voucherdto.setShold2(vDto.getShold2()==null?"":vDto.getShold2());//预算级次
				voucherdto.setShold3(vDto.getShold3()==null?"":vDto.getShold3());//辖属标志
				voucherdto.setSfilename(FileName);
				voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				voucherdto.setSdemo("处理成功");				
				voucherdto.setSvoucherno(mainvou);
				voucherdto.setIcount(list.size());
				List lists=new ArrayList();
				lists.add(list);
				lists.add(voucherdto);
				lists.add(cDto);
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map= tranfor(lists,sendList.size(),(i+1),vDto,idtoList);				
				List voucherList=new ArrayList();
				voucherList.add(map);
				voucherList.add(voucherdto);
				voucherList.add(idtoList);
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
	public Map tranfor(List lists,int packnum,int packno,TvVoucherinfoDto infodto,List idtoList) throws ITFEBizException{
		try{	
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",vDto.getScheckdate().substring(0,4));//业务年度
			vouchermap.put("VtCode",vDto.getSvtcode());//凭证类型编号
			vouchermap.put("VouDate",vDto.getScreatdate());//凭证日期
			vouchermap.put("VoucherNo",vDto.getSdealno());//凭证号
			vouchermap.put("VoucherCheckNo",infodto.getSdealno());//对账单号
			vouchermap.put("ChildPackNum",packnum);//子包总数
			vouchermap.put("CurPackNo",packno);//本包序号
			vouchermap.put("FundTypeCode","1");//资金性质-tips下发报表无此数据，凭证库需要，写死1-预算管理资金
			vouchermap.put("BelongFlag",vDto.getShold3()==null?"":vDto.getShold3());//辖属标志//dto.sattach();//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)dto.shold1();//预算种类1-预算内2-预算外dto.shold3();//辖属标志0-本级，1-全辖dto.setShold2();//预算级次1-中央，2-省，3-市，4-县，5-乡dto.scheckvouchertype();//报表种类
			vouchermap.put("BudgetLevelCode",vDto.getShold2()==null?"":vDto.getShold2());//预算级次
			vouchermap.put("BillKind",vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//报表种类
			vouchermap.put("FinOrgCode",cDto.getSfinorgcode()==null?"":cDto.getSfinorgcode());//财政机关代码
			vouchermap.put("TreCode",vDto.getStrecode());//国库主体代码
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//国库主体名称
			vouchermap.put("BeginDate",vDto.getScheckdate());//对账起始日期
			vouchermap.put("EndDate",vDto.getSpaybankcode());//对账终止日期
			vouchermap.put("AllNum",list==null?0:list.size());//总笔数
			vouchermap.put("Hold1","");//预留字段1
			vouchermap.put("Hold2",vDto.getSverifyusercode()==null?"":vDto.getSverifyusercode());//预留字段2
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for (TrIncomedayrptDto dto:list) {

				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",vDto.getSdealno()+(++id));//序号
				Detailmap.put("AdmDivCode",vDto.getSadmdivcode());//行政区划代码
				Detailmap.put("StYear",vDto.getScreatdate().substring(0,4));//业务年度
				Detailmap.put("TaxOrgCode",dto.getStaxorgcode()==null?"":dto.getStaxorgcode());
				if(dto.getStaxorgcode()==null)
					Detailmap.put("TaxOrgName","");
				else if(taxorgcodeMap.get(dto.getStaxorgcode())==null){//征收机关名称
					Detailmap.put("TaxOrgName", VoucherUtil.getStaxorgname(dto.getStaxorgcode()));
				}else{
					Detailmap.put("TaxOrgName", taxorgcodeMap.get(dto.getStaxorgcode()).getStaxorgname());
				}
				Detailmap.put("BudgetType","1");//资金性质-tips下发报表无此数据，凭证库需要，写死1-预算管理资金
				Detailmap.put("BudgetLevelCode",dto.getSbudgetlevelcode()==null?"":dto.getSbudgetlevelcode());//预算级次
				Detailmap.put("BudgetSubjectCode",dto.getSbudgetsubcode()==null?"":dto.getSbudgetsubcode());//收入分类科目编码
				Detailmap.put("BudgetSubjectName",dto.getSbudgetsubname()==null?"":dto.getSbudgetsubname());//收入分类科目名称
				Detailmap.put("CurIncomeAmt",String.valueOf(dto.getNmoneymonth()));//本期金额
				Detailmap.put("SumIncomeAmt",String.valueOf(dto.getNmoneyyear()));//累计金额
				Detailmap.put("Hold1",String.valueOf(dto.getNmoneyday()));//预留字段1
				Detailmap.put("Hold2",String.valueOf(dto.getNmoneyquarter()));//预留字段2
				Detailmap.put("Hold3",String.valueOf(dto.getNmoneytenday()));//预留字段3
				Detailmap.put("Hold4","");//预留字段4
				if(dto.getSbudgetsubcode()!=null&&dto.getSbudgetsubcode().length()==3)
					allamt=allamt.add(dto.getNmoneyday());										
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}					
			vDto.setNmoney(allamt);
			vouchermap.put("AllAmt",String.valueOf(allamt));//总金额
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
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
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private TfReportIncomeMainDto getMainDto(HashMap<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReportIncomeMainDto mainDto = new TfReportIncomeMainDto();
		mainDto.setSorgcode(vDto.getSorgcode());//S_ORGCODE IS '机构代码'; 
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		mainDto.setIvousrlno(Long.valueOf(voucherno));   
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		mainDto.setSdemo("处理成功");
		mainDto.setStrecode(getString(mainMap,"TreCode"));//S_TRECODE IS '国库代码'; 
		mainDto.setStrename(getString(mainMap,"TreName"));//S_TRENAME IS '国库主体名称';
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);//S_STATUS IS '状态';      
		mainDto.setSdemo("处理成功");//S_DEMO IS '描述';        
		mainDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//TS_SYSUPDATE IS '系统时间';
		;//S_PACKAGENO IS '包流水号';
		mainDto.setSadmdivcode(getString(mainMap,"AdmDivCode"));//S_ADMDIVCODE IS '行政区划代码';
		mainDto.setSstyear(getString(mainMap,"StYear"));//S_STYEAR IS '业务年度';
		mainDto.setSvtcode(getString(mainMap,"VtCode"));//S_VTCODE IS '凭证类型编号';
		mainDto.setSvoudate(getString(mainMap,"VouDate"));//S_VOUDATE IS '凭证日期';
		mainDto.setSvoucherno(getString(mainMap,"VoucherNo"));//S_VOUCHERNO IS '凭证号';
		mainDto.setSvouchercheckno(getString(mainMap,"VoucherCheckNo"));//S_VOUCHERCHECKNO IS '对账单号';
		mainDto.setSchildpacknum(getString(mainMap,"ChildPackNum"));//S_CHILDPACKNUM IS '子包总数';
		mainDto.setScurpackno(getString(mainMap,"CurPackNo"));//S_CURPACKNO IS '本包序号';
		mainDto.setSfundtypecode(getString(mainMap,"FundTypeCode"));//S_FUNDTYPECODE IS '资金性质';
		mainDto.setSbelongflag(getString(mainMap,"BelongFlag"));//S_BELONGFLAG IS '辖属标志';
		mainDto.setSbudgetlevelcode(getString(mainMap,"BudgetLevelCode").length()>1?"1":getString(mainMap,"BudgetLevelCode"));//S_BUDGETLEVELCODE IS '预算级次';
		mainDto.setSbillkind(getString(mainMap,"BillKind"));//S_BILLKIND IS '报表种类';
		mainDto.setSfinorgcode(getString(mainMap,"FinOrgCode"));//S_FINORGCODE IS '财政机关代码';
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//S_BEGINDATE IS '对账起始日期';
		mainDto.setSenddate(getString(mainMap,"EndDate"));//S_ENDDATE IS '对账终止日期';
		mainDto.setSallnum(getString(mainMap,"AllNum"));//S_ALLNUM IS '总笔数';
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//N_ALLAMT IS '总金额';
		;//S_XCHECKRESULT IS '对帐结果';
		;//S_XDIFFNUM IS '不符笔数';
		mainDto.setShold1(getString(mainMap,"Hold1"));//S_HOLD1 IS '预留字段1';
		mainDto.setShold2(getString(mainMap,"Hold2"));//S_HOLD2 IS '预留字段2';
		mainDto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起        
		mainDto.setSext2(getString(mainMap,"ext2"));//S_EXT2 IS '扩展';        
		mainDto.setSext3(getString(mainMap,"ext3"));//S_EXT3 IS '扩展';        
		mainDto.setSext4(getString(mainMap,"ext4"));//S_EXT4 IS '扩展';        
		mainDto.setSext5(getString(mainMap,"ext5"));//S_EXT5 IS '扩展'; 
		return mainDto;
	}
	private TfReportIncomeSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReportIncomeSubDto subDto = new TfReportIncomeSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//序号
		subDto.setSid(String.valueOf(subDto.getIseqno()));
		subDto.setSadmdivcode(getString(subMap,"AdmDivCode"));//AdmDivCode",vDto.getSadmdivcode());//行政区划代码
		subDto.setSstyear(getString(subMap,"StYear"));//StYear",vDto.getScreatdate().substring(0,4));//业务年度
		subDto.setStaxorgcode(getString(subMap,"TaxOrgCode"));//TaxOrgCode",cDto.getSfinorgcode());//征收机关代码
		subDto.setStaxorgname(getString(subMap,"TaxOrgName"));//TaxOrgName",cDto.getSfinorgname());//征收机关名称
		subDto.setSbudgettype(getString(subMap,"BudgetType"));//BudgetType","1");//资金性质-tips下发报表无此数据，凭证库需要，写死1-预算管理资金
		subDto.setSbudgetlevelcode(getString(mainMap,"BudgetLevelCode").length()>1?"1":getString(mainMap,"BudgetLevelCode"));//BudgetLevelCode",dto.getSbudgetlevelcode());//预算级次
		subDto.setSbudgetsubjectcode(getString(subMap,"BudgetSubjectCode"));//BudgetSubjectCode",dto.getSbudgetsubcode());//收入分类科目编码
		subDto.setSbudgetsubjectname(getString(subMap,"BudgetSubjectName"));//BudgetSubjectName",dto.getSbudgetsubname());//收入分类科目名称
		subDto.setNcurincomeamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"CurIncomeAmt")));//CurIncomeAmt",dto.getNmoneyday());//本期金额
		subDto.setNsumincomeamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"SumIncomeAmt")));//SumIncomeAmt",dto.getNmoneyyear());//累计金额
		subDto.setShold1(getString(subMap,"Hold1"));//S_HOLD1 IS '预留字段1';
		subDto.setShold2(getString(subMap,"Hold2"));//S_HOLD2 IS '预留字段2';
		subDto.setShold3(getString(subMap,"Hold3"));//S_HOLD3 IS '预留字段3';
		subDto.setShold4(getString(subMap,"Hold4"));//S_HOLD4 IS '预留字段4';
		subDto.setSext1(getString(subMap,"ext1"));//S_EXT1 IS '扩展';        
		subDto.setSext2(getString(subMap,"ext2"));//S_EXT2 IS '扩展';        
		subDto.setSext3(getString(subMap,"ext3"));//S_EXT3 IS '扩展';        
		subDto.setSext4(getString(subMap,"ext4"));//S_EXT4 IS '扩展';        
		subDto.setSext5(getString(subMap,"ext5"));//S_EXT5 IS '扩展';
		subDto.setSxcheckresult("0");//对账结果默认成功
		return subDto;
	}
	private String getString(Map datamap,String key)
	{
		if(datamap==null||key==null)
			return "";
		else
			return String.valueOf(datamap.get(key));
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
}
