package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 国库往来票据（3452）
 * 
 * @author hejianrong
 * @time  2014-04-02
 * 
 */
public class VoucherDto2MapFor3452 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3452.class);

	/**
	 * 凭证生成
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=findMainDto(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TvVoucherinfoAllocateIncomeDto mainDto:(List<TvVoucherinfoAllocateIncomeDto>)list){
			List<TvVoucherinfoDto> voutherList=voucherIsRepeat(dto, mainDto);
			if(voutherList!=null&&voutherList.size()>0){
				continue;
			}
			lists.add(voucherTranfor(dto, mainDto));
		}
		return lists;
	}
		
	/**
	 * 生成凭证
	 * 生成凭证报文
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	private List voucherTranfor(TvVoucherinfoDto vDto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));		
		dto.setSadmdivcode(mainDto.getSadmdivcode());
		dto.setSvoucherno(mainDto.getSdealno());
		mainDto.setShold2(dto.getSvoucherno());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");		
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map=tranfor(lists);
		lists.clear();		
		lists.add(map);
		lists.add(dto);
		lists.add(mainDto);
		return lists;
	}
	
	/**	
	 * 查询中央及省市往来票据(调拨收入凭证)业务表信息
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {		
		TvVoucherinfoAllocateIncomeDto mainDto=new TvVoucherinfoAllocateIncomeDto();
		mainDto.setSorgcode(dto.getSorgcode());
		mainDto.setStrecode(dto.getStrecode());
		mainDto.setScommitdate(dto.getScreatdate());
		mainDto.setSvtcode(dto.getSvtcode());
		try {
			return CommonFacade.getODB().findRsByDtoForWhere(mainDto, " AND  S_HOLD2 IS NULL");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
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
	@SuppressWarnings("unchecked")
	public Map tranfor(List lists) throws ITFEBizException{
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto mainDto=(TvVoucherinfoAllocateIncomeDto) lists.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());//中央及省市往来票据Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());//业务年度		
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号			
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号		
			vouchermap.put("PayeeAcctNo", mainDto.getSpayeeacctno());//收款人账号
			vouchermap.put("PayeeAcctName", mainDto.getSpayeeacctname());//收款人名称
			vouchermap.put("PayeeAcctBankName", mainDto.getSpayeeacctbankname());//收款人银行
			vouchermap.put("PayAcctNo", mainDto.getSpayacctno());//付款人账号
			vouchermap.put("PayAcctName", mainDto.getSpayacctname());//付款人名称
			vouchermap.put("PayAcctBankName", mainDto.getSpayacctbankname());//付款人银行
			vouchermap.put("PaySummaryCode", "");//用途编码
			vouchermap.put("PaySummaryName", "");//用途名称
			vouchermap.put("PayAmt",  MtoCodeTrans.transformString(mainDto.getNmoney()));//拨款金额
			vouchermap.put("AgencyCode", "");//基层预算单位编码
			vouchermap.put("AGencyName", "");//基层预算单位名称	
			vouchermap.put("MsgType", mainDto.getSreportkind());//报文种类
			vouchermap.put("PayTraseqNo", mainDto.getSpaydealno());//支付交易序号
			vouchermap.put("TrasType", "");//交易种类
			vouchermap.put("BizType", mainDto.getSvtcodekind());//业务类型
			vouchermap.put("SndBnkNo", mainDto.getSforwardbankno());//发起行行号
			vouchermap.put("PayerOpnBnkNo", "");//付款人开户行行号
			vouchermap.put("RcvBnkNo", mainDto.getSreceivebankno());//接收行行号
			vouchermap.put("PayeeOpnbnkNo", "");//收款人开户行行号
			vouchermap.put("DebitAcct", mainDto.getSborrow());//记账账户借方
			vouchermap.put("LoanAcct", mainDto.getSlend());//记账账户贷方
			vouchermap.put("ChkSignFlag", mainDto.getSescortmarks());//核押标志
			vouchermap.put("ChkSign", mainDto.getSsecretsign());//密押
			vouchermap.put("RecvMsgDate", mainDto.getSreceivedate());//收报日期
			vouchermap.put("EntrustDate", mainDto.getScommitdate());//委托日期
			vouchermap.put("AddWord", mainDto.getSdemo());//附言
			vouchermap.put("TrasrlNo", mainDto.getSdealno());//交易流水号
			vouchermap.put("PrintTime", "");//打印时间
			vouchermap.put("PrintNum", "");//打印次数
			vouchermap.put("PrintOper", "");//打印操作员
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2					
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
	 * 凭证判重
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherIsRepeat(TvVoucherinfoDto dto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException{
		TvVoucherinfoDto vDto=(TvVoucherinfoDto) dto.clone();
		vDto.setSadmdivcode(mainDto.getSadmdivcode());
		vDto.setSvoucherno(mainDto.getSdealno());
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
		}		
	}
}
