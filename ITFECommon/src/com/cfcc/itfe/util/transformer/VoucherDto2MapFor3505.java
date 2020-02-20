package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 实拨信息对账（3505）
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3505 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3401.class);
	private  BigDecimal Total;
	private  String voucher_Sql="SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? "+ 
								"AND S_TRECODE= ?  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' " +
								"AND S_CREATDATE BETWEEN ? AND ?";	
	private  String MainDto_Sql_5207="SELECT * FROM TV_PAYOUTMSGMAIN WHERE S_BIZNO IN("+voucher_Sql+")";
	private String CheckAccount_Finance_Sql_5207="SELECT * FROM (SELECT S_BUDGETUNITCODE , S_FUNSUBJECTCODE,sum(N_MONEY) N_MONEY FROM("+
										 "SELECT a.S_BUDGETUNITCODE S_BUDGETUNITCODE,b.S_FUNSUBJECTCODE S_FUNSUBJECTCODE,b.N_MONEY N_MONEY" +
										 " FROM ("+MainDto_Sql_5207+
										 ") a,TV_PAYOUTMSGSUB b WHERE a.S_BIZNO=b.S_BIZNO)GROUP BY S_BUDGETUNITCODE , S_FUNSUBJECTCODE"+
										 ") ORDER BY S_BUDGETUNITCODE , S_FUNSUBJECTCODE";
	private String VoucherCheckNo;//对账单号
	private int DetailChildPackNum=500;//明细分包数
	private int ChildPackNum;//子包总数
	private int CurPackNo=0;//本包序号
	private int count=0;//生成凭证数量
	
	/**
	 * 凭证生成
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		String result=verifyVoucherCount(vDto);
		if(result==null||result.equals(""))
			return null;
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		String[] results=result.split("#");
		List lists=new ArrayList();
		List list=new ArrayList();
		list.addAll(getCheckAccountFinanceResult(vDto, results[0]));
		lists.add(list);
		voucherTranfor(lists,vDto);		
		lists.clear();
		lists.add(count);
		return lists;	
	}
	
	/**
	 * 根据凭证明细分组生成凭证
	 * @param lists
	 * @param vDto
	 * @throws ITFEBizException
	 */
	public void voucherTranfor(List lists,TvVoucherinfoDto vDto) throws ITFEBizException{
		for(List list:(List<List>)lists){			
			List fatherPackList=getListByChildPackNum(list);
			VoucherCheckNo=VoucherUtil.getGrantSequence();
			for(List chilPackList:(List<List>)fatherPackList){
				CurPackNo++;
				voucherTranfor(vDto, chilPackList);
				count++;
				if(CurPackNo==fatherPackList.size())
					CurPackNo=0;
			}				
		}
	}
	
	/**
	 * 根据凭证明细分组
	 * @param list
	 * @return
	 */
	public List getListByChildPackNum(List<List> list){
		List lists=new ArrayList();				
		int count=list.size()/DetailChildPackNum;
		int mod=list.size()%DetailChildPackNum;		
		List detailList;
		for(int i=0;i<count+1;i++){
			if(mod==0&&i==count)
				break;
			if(mod!=0&&i==count)
				lists.add(list.subList(DetailChildPackNum*i, list.size()));				
			else				
				lists.add(list.subList(DetailChildPackNum*i, count==0?list.size():DetailChildPackNum*(i+1)));
		}ChildPackNum=lists.size();
		return lists;
	}
	
	/**
	 * 生成凭证
	 * 生成凭证报文
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	public void voucherTranfor(TvVoucherinfoDto vDto,List list) throws ITFEBizException {
		List lists=new ArrayList();
		lists=(List) list.get(0);
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");	
		dto.setShold2("");
		String mainvou=VoucherUtil.getGrantSequence();
		dto.setSdealno(mainvou);		
		dto.setSvoucherno(mainvou+"_"+VoucherCheckNo.substring(12, VoucherCheckNo.length()));	
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");		
		lists=new ArrayList();
		lists.add(dto);
		lists.add(list);
		Total=new BigDecimal("0.00");
		Map map=tranfor(lists);
		dto.setNmoney(Total);
		VoucherUtil.sendTips(dto, map);
		try {
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("凭证写入数据库异常！",e);
		}
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
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);
			List<List> resultList=(List) lists.get(1);		
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 			
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getSstyear());		
			vouchermap.put("VtCode", vDto.getSvtcode());		
			vouchermap.put("VouDate", vDto.getScreatdate());		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("VoucherCheckNo", VoucherCheckNo);
			vouchermap.put("ChildPackNum", ChildPackNum);
			vouchermap.put("CurPackNo", CurPackNo);
			vouchermap.put("TreCode", vDto.getStrecode());		
			vouchermap.put("ClearBankCode", "");
			vouchermap.put("ClearBankName", "");
			vouchermap.put("ClearAccNo", "");
			vouchermap.put("ClearAccNanme", "");
			vouchermap.put("BeginDate", vDto.getShold3());
			vouchermap.put("EndDate", vDto.getShold4());
			vouchermap.put("XCheckResult", ""); 	
			vouchermap.put("XCheckReason", "");
			vouchermap.put("Hold1", "");		
			vouchermap.put("Hold2", "");					
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();
			for(int i=0;i<resultList.size();i++){
				List list=resultList.get(i);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", VoucherUtil.getDetailSequence(vDto.getSdealno(), i+1));
				Detailmap.put("BgtTypeCode", "");
				Detailmap.put("BgtTypeName", ""); 
				Detailmap.put("FundTypeCode",""); 
				Detailmap.put("FundTypeName", ""); 
				Detailmap.put("PayTypeCode", ""); 
				Detailmap.put("PayTypeName", ""); 
				Detailmap.put("PayeeAcctNo", ""); 
				Detailmap.put("PayeeAcctName", ""); 
				Detailmap.put("PayeeAcctBankName", ""); 
				Detailmap.put("PayAcctNo", ""); 
				Detailmap.put("PayAcctName", ""); 
				Detailmap.put("PayAcctBankName", ""); 
				Detailmap.put("AgencyCode", list.get(0)); 
				Detailmap.put("AgencyName", ""); 
				Detailmap.put("ExpFuncCode", list.get(1)); 
				Detailmap.put("ExpFuncName", ""); 
				Detailmap.put("ExpEcoCode", ""); 
				Detailmap.put("ExpEcoName", ""); 
				Detailmap.put("PayAmt", MtoCodeTrans.transformString(list.get(2)));
				Detailmap.put("XCheckResult", ""); 	
				Detailmap.put("XCheckReason", "");
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 							
				Detail.add(Detailmap);
				Total=Total.add((BigDecimal) list.get(2));
			}
			vouchermap.put("AllAmt", MtoCodeTrans.transformString(Total));
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！",e);
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	
	/**
	 * 校验凭证索引表与业务主表记录是否相等
	 * 
	 * @param List
	 * @return
	 * @throws ITFEBizException
	 */
	public String verifyVoucherCount(TvVoucherinfoDto dto) throws ITFEBizException{		
		SQLExecutor sqlExecutor=null;
		String svtcode=null;
		String result="";
		try {			
			for(int i=0;i<2;i++){
				sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				sqlExecutor.clearParams();
				sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5207);
				svtcode=MsgConstant.VOUCHER_NO_5207;			
				sqlExecutor.addParam(dto.getSorgcode());
				sqlExecutor.addParam(dto.getStrecode());
				sqlExecutor.addParam(dto.getShold3());
				sqlExecutor.addParam(dto.getShold4());
				SQLResults rs = sqlExecutor.runQueryCloseCon(voucher_Sql);
				if(rs!=null&&rs.getRowCount()>0){
					if(verifyMainDtoCount(rs.getRowCount(), dto,svtcode)){
						result=result+svtcode+"#";
					}
				}if(sqlExecutor!=null){
					sqlExecutor.closeConnection();
				}
			}
			
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据索引表记录查询业务表信息出错！",e);
		}return result;
	}
	
	/**
	 * 校验业务主表记录是否已清理到历史表
	 * @param count
	 * @param dto
	 * @param svtcode
	 * @return
	 * @throws ITFEBizException
	 */
	public boolean verifyMainDtoCount(int count,TvVoucherinfoDto dto,String svtcode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String sql=null;
		String err=null;
		try {			
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.clearParams();			
			sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5207);
			sql=MainDto_Sql_5207;
			err="根据凭证索引表记录查询 \"实拨资金业务主表\" 信息出错！\t\n索引表记录与业务主表记录不相等！\t\n\"实拨资金业务主表\" 部分记录可能已清理到历史表！";		
			sqlExecutor.addParam(dto.getSorgcode());
			sqlExecutor.addParam(dto.getStrecode());
			sqlExecutor.addParam(dto.getShold3());
			sqlExecutor.addParam(dto.getShold4());
			SQLResults rs = sqlExecutor.runQueryCloseCon(sql);
			if(rs!=null&&rs.getRowCount()>0){
				if(count!=rs.getRowCount()){
					throw new ITFEBizException(err);
				}return true;
			}					
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据索引表记录查询业务表信息出错！",e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}finally{
			if(sqlExecutor!=null){
				sqlExecutor.closeConnection();
			}
		}
		return false;
	}
	
	/**
	 * 查询生成凭证信息
	 * @param dto
	 * @param svtcode
	 * @return
	 * @throws ITFEBizException
	 */
	public List getCheckAccountFinanceResult(TvVoucherinfoDto dto,String svtcode) throws ITFEBizException {
		SQLExecutor sqlExecutor=null;
		SQLResults rs =null;
		List lists=new ArrayList();		
		try {
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.clearParams();					
			sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5207);
			sqlExecutor.addParam(dto.getSorgcode());
			sqlExecutor.addParam(dto.getStrecode());
			sqlExecutor.addParam(dto.getShold3());
			sqlExecutor.addParam(dto.getShold4());
			rs = sqlExecutor.runQueryCloseCon(CheckAccount_Finance_Sql_5207);					
			if(rs!=null&&rs.getRowCount()>0){
				for(int i=0;i<rs.getRowCount();i++){
					List list=new ArrayList();
					list.add(rs.getString(i, 0));
					list.add(rs.getString(i, 1));
					list.add(rs.getBigDecimal(i, 2));
					lists.add(list);
				}
			}		
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}finally{
			if(sqlExecutor!=null){
				sqlExecutor.closeConnection();
			}
		}		
		return lists;
	}
	
}
