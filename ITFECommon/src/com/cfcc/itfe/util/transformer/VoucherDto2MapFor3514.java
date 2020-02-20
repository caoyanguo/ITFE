package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.StringUtils;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnLedgerdataDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 分户账对帐3514
 * 
 * @author zhangliang
 * @time  2017-03-31
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3514 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3514.class);
	private static Map<String,TsInfoconnorgaccDto> getMap = null;
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());//始终为当天的凭证日期String startDate = vDto.getScheckdate();//开始日期String endDate = vDto.getSpaybankcode();//结束日期
		List<TsTreasuryDto> treList = getTreList(vDto.getSorgcode(),vDto.getStrecode());//国库代码
		List<TsInfoconnorgaccDto> acctList = getAcctList(vDto.getSorgcode(),vDto.getStrecode(),vDto.getShold1());//库款账户
		List<List> lists=new ArrayList<List>();
		if(treList!=null&&treList.size()>0)
		{
			List vouList = null;
			if(treList.size()==1&&acctList!=null&&acctList.size()==1)
			{
				vouList = getVoucher(vDto,treList.get(0),acctList.get(0));
				if(vouList!=null&&vouList.size()>0)
					lists.add(vouList);
			}else
			{
				for(TsTreasuryDto tredto:treList)
				{
					acctList = getAcctList(tredto.getSorgcode(),tredto.getStrecode(),vDto.getShold1());
					for(TsInfoconnorgaccDto sDto:acctList)
					{
						vouList = getVoucher(vDto,tredto,sDto);
						if(vouList!=null&&vouList.size()>0)
							lists.add(vouList);
					}
				}
			}
		}
		getMap = null;
		return lists;
	}
	private List getVoucher(TvVoucherinfoDto vDto,TsTreasuryDto tDto,TsInfoconnorgaccDto sDto) throws ITFEBizException
	{
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List<TsConvertfinorgDto> tsConvertfinorgList=(List<TsConvertfinorgDto>) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("国库："+vDto.getStrecode()+"对应的财政机关代码参数未维护！");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("国库："+cDto.getStrecode()+"对应的区划代码未维护！");
			}
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}		
		String FileName=null;
		String dirsep = File.separator; 
		String mainvou=VoucherUtil.getGrantSequence();
		FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+mainvou+ ".msg";
		TvVoucherinfoDto dto=new TvVoucherinfoDto();			
		dto.setSorgcode(vDto.getSorgcode());
		dto.setSadmdivcode(vDto.getSadmdivcode());
		dto.setSvtcode(vDto.getSvtcode());
		dto.setScreatdate(vDto.getScreatdate());
		dto.setStrecode(vDto.getStrecode());
		dto.setSfilename(FileName);
		dto.setSdealno(mainvou);		
		dto.setSadmdivcode(cDto.getSadmdivcode());
		dto.setSstyear(dto.getScreatdate().substring(0, 4));				
		dto.setShold1(sDto.getSpayeraccount());//库款账号
		dto.setShold2(sDto.getSpayername());//库款账户名称
//		if(ITFECommonConstant.PUBLICPARAM.indexOf(",stampmode=sign,")<0)//是否采用签名
//		{
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");
		dto.setSvoucherflag("1");
		dto.setSvoucherno(mainvou);
		dto.setScheckdate(vDto.getScheckdate());
		dto.setSpaybankcode(vDto.getSpaybankcode());
		dto.setShold3(vDto.getShold3());
		dto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
		String stockDayRptDetailSql="SELECT * FROM TN_LEDGERDATA WHERE S_TRECODE = ? AND S_ACCTNO = ? AND S_ACCTDATE >= ? AND S_ACCTDATE <= ? ORDER BY S_DEALNO";
		List<TnLedgerdataDto> detailList=new ArrayList<TnLedgerdataDto>();;
		try {
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			execDetail.addParam(tDto.getStrecode());
			execDetail.addParam(sDto.getSpayeraccount());
			execDetail.addParam(vDto.getScheckdate());
			execDetail.addParam(vDto.getSpaybankcode());
			detailList=  (List<TnLedgerdataDto>) execDetail.runQuery(stockDayRptDetailSql,TnLedgerdataDto.class).getDtoCollection();
			if(detailList==null||detailList.size()==0){
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("查询"+stockDayRptDetailSql+"分户账信息异常！",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		dto.setNmoney(detailList.get(detailList.size()-1).getNamt());
		dto.setIcount(detailList.size());
		List mapList=new ArrayList();
		mapList.add(sDto);
		mapList.add(dto);
		mapList.add(detailList);
		Map map=tranfor(mapList,null);		
		List vouList=new ArrayList();
		vouList.add(map);
		vouList.add(dto);
		return vouList;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	public Map tranfor(List lists,List<IDto> idtoList) throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> vouchermap = new HashMap<String, Object>();
		TsInfoconnorgaccDto sDto = (TsInfoconnorgaccDto)lists.get(0);
		TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
		List<TnLedgerdataDto> detailList=(List<TnLedgerdataDto>) lists.get(2);
		// 设置报文节点 Voucher
		map.put("Voucher", vouchermap);
		// 设置报文消息体 
		vouchermap.put("Id",vDto.getSdealno());//凭证号
		vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//行政区划代码
		vouchermap.put("StYear",vDto.getScheckdate().substring(0,4));//年度
		vouchermap.put("VtCode",vDto.getSvtcode());//凭证类型
		vouchermap.put("VouDate",vDto.getScreatdate());//凭证日期
		vouchermap.put("VoucherNo",vDto.getSdealno());//凭证号
		vouchermap.put("AcctNo",sDto.getSpayeraccount());//库款账户
		vouchermap.put("AcctName",sDto.getSpayername());//库款名称
		vouchermap.put("Hold1","");
		vouchermap.put("Hold2","");
		List<Object> Detail= new ArrayList<Object>();
		int id=0;
		for (TnLedgerdataDto tl : detailList) {
			Map<String, Object> detailmap = new HashMap<String, Object>();
			++id;
			detailmap.put("Id",tl.getSdealno());
			detailmap.put("VoucherBillId",vDto.getSdealno());//分户账账单id
			detailmap.put("VoucherNo",tl.getSvoucherno()==null?"":tl.getSvoucherno());//凭证号 
			detailmap.put("VouDate",tl.getSacctdate()==null?"":tl.getSacctdate());//账户日期 
			detailmap.put("DebitAcctAmt",tl.getNdebitamt()==null?"":tl.getNdebitamt());//借方
			detailmap.put("LoanAcctAmt",tl.getNcreditamt()==null?"":tl.getNcreditamt());//贷方
			detailmap.put("CurDateMoney",tl.getNamt()==null?"":tl.getNamt());//余额
			detailmap.put("TypeName",tl.getSdebitmark()==null?"":tl.getSdebitmark());//借贷标识 
			detailmap.put("Remark",tl.getSdemo()==null?"":tl.getSdemo());//摘要 
			detailmap.put("Hold1",""); 
			detailmap.put("Hold2",""); 
			detailmap.put("Hold3",""); 
			detailmap.put("Hold4",""); 
			Detail.add(detailmap);
		}
		HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
		DetailListmap.put("Detail",Detail);
		vouchermap.put("DetailList", DetailListmap);
		return map;
	}
	private List<TsTreasuryDto> getTreList(String sorgCode,String sTrecode)  throws ITFEBizException 
	{
		List<TsTreasuryDto> treList = null;//国库代码
		try {
			if(StringUtils.isBlank(sTrecode)||StringUtils.isEmpty(sTrecode))
			{
				TsTreasuryDto findto = new TsTreasuryDto();
				findto.setSorgcode(sorgCode);
				treList =  CommonFacade.getODB().findRsByDto(findto);
			}else
			{
				TsTreasuryDto dto = new TsTreasuryDto();
				dto.setStrecode(sTrecode);
				dto.setSorgcode(sorgCode);
				treList =  CommonFacade.getODB().findRsByDto(dto);
				if(treList==null||treList.size()<=0)
					throw new ITFEBizException("国库代码参数未维护！");
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			throw new ITFEBizException(e);
		}
		return treList;
	}
	private List<TsInfoconnorgaccDto> getAcctList(String sorgCode,String sTrecode,String acctno) throws ITFEBizException 
	{
		List<TsInfoconnorgaccDto> acctList = null;//库款账户
		try {
			Map<String,TsInfoconnorgaccDto> getMap = getAcctCache(sorgCode);
			if(StringUtils.isBlank(acctno)||StringUtils.isEmpty(acctno))
			{
				if(getMap!=null&&getMap.size()>0)
				{
					acctList = new ArrayList();
					Object object[] = getMap.keySet().toArray();
					if(StringUtils.isBlank(sTrecode)||StringUtils.isEmpty(sTrecode))
					{
						for(int i=0;i<getMap.size();i++)
						{
							if(String.valueOf(object[i]).indexOf(sorgCode)==0)
								acctList.add(getMap.get(String.valueOf(object[i])));
						}
					}else
					{
						for(int i=0;i<getMap.size();i++)
						{
							if(String.valueOf(object[i]).indexOf(sorgCode+sTrecode)==0)
								acctList.add(getMap.get(String.valueOf(object[i])));
						}
					}
				}				
			}else{
				if(getMap!=null&&getMap.size()>0)
				{
					acctList = new ArrayList();
					Object object[] = getMap.keySet().toArray();
					if(StringUtils.isBlank(sTrecode)||StringUtils.isEmpty(sTrecode))
					{
						for(int i=0;i<getMap.size();i++)
						{
							if(String.valueOf(object[i]).indexOf(sorgCode)==0&&String.valueOf(object[i]).indexOf(acctno)>0)
							{
								acctList.add(getMap.get(String.valueOf(object[i])));
								break;
							}else
								continue;
						}
					}else
					{
						if(getMap.get(sorgCode+sTrecode+acctno)!=null)
							acctList.add(getMap.get(sorgCode+sTrecode+acctno));
						else
							throw new ITFEBizException("国库代码和库款账号对应关系错！"); 
					}
				}		
			}
		} catch (Exception e) {
			throw new ITFEBizException(e);
		}
		return acctList;
	}
	public  Map<String,TsInfoconnorgaccDto> getAcctCache(String orgCode)
	{
		if(getMap!=null)
			return getMap;
		getMap = ServiceUtil.getAcctCache(orgCode);//缓存库款账户
		return getMap;
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
}
