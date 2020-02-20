package com.cfcc.itfe.service.sendbiz.createvoucherforreport;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.IVoucherDto2Map;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
/**
 * @author db2admin
 * @time   13-09-03 11:12:27
 * codecomment: 
 */

public class CreateVoucherForReportService extends AbstractCreateVoucherForReportService {
	private static Log log = LogFactory.getLog(CreateVoucherForReportService.class);

	@SuppressWarnings("unchecked")
	public Integer createVoucherAndSend(String VoucherType,
			String SubVoucherType, String treCode,
			String createVoucherDate,String orgCode) throws ITFEBizException {
		String currentDate = TimeFacade.getCurrentStringTime(); 
		String dirsep = File.separator;
		String ls_SelectVoucher = getSelectConditon(VoucherType,SubVoucherType, treCode,createVoucherDate,orgCode);  
		SQLExecutor exec = null;
		int count=0;
		//抛出异常时的对账类型 author:zhanghuibin
		String errorType="";
		try {
			exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			SQLResults result =  exec.runQueryCloseCon(ls_SelectVoucher);
			if(result.getRowCount()<=0)
				return count;
			for(int i=0;i<result.getRowCount();i++){
				String ls_AdmDivCode = result.getString(i, 0);
				String ls_StYear = result.getString(i, 1);
				String ls_VtCode = result.getString(i, 2);
				String ls_TreCode = result.getString(i, 3);
				int li_Count = result.getInt(i, 4);
				BigDecimal lbig_Money = result.getBigDecimal(i, 5);
				//根据不同的凭证类型获取不同的对账类型
				String type="";
				//如果对账类型VoucherType为null，则根据具体的凭证类型ls_VtCode获得相应的对账类型
				if(null==VoucherType||"".equals(VoucherType)){
					type = getVoucherTypeBySubVoucherType(ls_VtCode);
				}else{
					type=VoucherType;
				}
				errorType=type;
				String ls_PayBankCode = "";
				if(MsgConstant.VOUCHER_NO_3502.equals(type)){
					ls_PayBankCode = result.getString(i, 6);
				}
				String ls_DealNo  = VoucherUtil.getGrantSequence();
				String FileName = ITFECommonConstant.FILE_ROOT_PATH
				+ dirsep
				+ "Voucher"
				+ dirsep
				+ currentDate
				+ dirsep
				+ "send"
				+type+"_"				
				+ ls_VtCode
				+ "_"
				+ ls_DealNo
				+ ".msg";
				List<Object> mianDtos=new ArrayList<Object>();//存储主子信息 author:zhanghuibin
				TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
				voucherDto.setSdealno(ls_DealNo);
				voucherDto.setNmoney(lbig_Money);
				voucherDto.setSadmdivcode(ls_AdmDivCode);
				voucherDto.setScreatdate(currentDate);
				voucherDto.setSfilename(FileName);
				voucherDto.setSorgcode(orgCode);
				voucherDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
				voucherDto.setSdemo(ls_PayBankCode);//使用该字段存放代理银行编码
				voucherDto.setSstyear(ls_StYear);
				voucherDto.setStrecode(ls_TreCode);
				voucherDto.setSvoucherflag("1");
				voucherDto.setSvoucherno(ls_DealNo);
				voucherDto.setSvtcode(type);
				voucherDto.setScheckvouchertype(ls_VtCode);
				voucherDto.setSattach(ls_VtCode);//使用该字段存放用对账的凭证类型
				voucherDto.setIcount(li_Count);
				voucherDto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));
				voucherDto.setScheckdate(createVoucherDate);//对账日期：取凭证回单/退回日期
				voucherDto.setSverifyusercode(createVoucherDate);//存入对账日期
				
				/**
				 * 查询明细信息
				 * author:zhanghuibin
				 */
				String ls_DetailSelectVoucher = getDetailSelectConditon(ls_AdmDivCode,ls_StYear,type,ls_VtCode, ls_TreCode,createVoucherDate,orgCode,ls_PayBankCode);
				int detailCount=0;
				SQLExecutor execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				SQLResults resultDetail=null;
				if(!ls_DetailSelectVoucher.equals("")){
					resultDetail =  execDetail.runQueryCloseCon(ls_DetailSelectVoucher);
				}else{
					return detailCount;
				}
				int rows=resultDetail.getRowCount();
				if(rows<=0){
					return detailCount;
				}
				List<TvVoucherinfoDto> detailDtos=new ArrayList<TvVoucherinfoDto>();
				for(int j=0;j<rows;j++){
					String voucherno=resultDetail.getString(j, 0);
					BigDecimal Money = resultDetail.getBigDecimal(j, 1);
					String status=resultDetail.getString(j, 2);
					TvVoucherinfoDto voucherDetailDto = new TvVoucherinfoDto();
					String detailId  = VoucherUtil.getGrantSequence();
					voucherDetailDto.setSdealno(detailId);//用Sdealno代传明细凭证ID
					voucherDetailDto.setNmoney(Money);//明细金额
					voucherDetailDto.setSvoucherno(voucherno);//明细凭证的凭证编号
					voucherDetailDto.setShold1(status);//用shold1代传明细凭证的交易状态
					detailDtos.add(voucherDetailDto);
				}
				mianDtos.add(voucherDto);
				mianDtos.add(detailDtos);
				Map map = null;
				IVoucherDto2Map voucher=(IVoucherDto2Map) ContextFactory.getApplicationContext().getBean(
						MsgConstant.VOUCHER_DTO2MAP+type);	
//				map = voucher.voucherTranfor(voucherDto);//不添加明细前的执行方法
				map = voucher.tranfor(mianDtos);//将主子信息转换成凭证报文 author:zhanghuibin（添加明细后的执行方法）
				VoucherUtil.sendTips(voucherDto,map);
				voucherDto.setSdemo("处理成功");
				DatabaseFacade.getODB().create(voucherDto);
				count++;
			}
		} catch (JAFDatabaseException e) {
			log.debug(e);
			VoucherException.saveErrInfo(errorType, e);
		}
		return count;
	}	

	/*
	 * 按照不同的凭证类型生成sql语句
	 */
	private String getSelectConditon(String VoucherType,
			String SubVoucherType, String treCode,
			String createVoucherDate,String orgCode){
		String ls_SelectVoucher ="";
		//和财政对账，不用关联业务表查询
		if(MsgConstant.VOUCHER_NO_3501.equals(VoucherType)){
			String ls_Voucher3501 = "";
			ls_Voucher3501 = "select S_ADMDIVCODE,S_STYEAR,S_VTCODE,S_TRECODE,count(S_TRECODE),sum(N_MONEY) " +
			"from tv_voucherinfo where S_CONFIRUSERCODE = '"+createVoucherDate+"' and ( S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"' or S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and S_ORGCODE = '"+orgCode+"' ";
			if(SubVoucherType!=null&&!SubVoucherType.equals("")){
				ls_Voucher3501+=" and S_VTCODE = '"+SubVoucherType+"' ";
			}else{
				ls_Voucher3501+=" and S_VTCODE in ('"
				+MsgConstant.VOUCHER_NO_5106+"','"
				+MsgConstant.VOUCHER_NO_5108+"','"
				+MsgConstant.VOUCHER_NO_5207+"','"
				+MsgConstant.VOUCHER_NO_5209+"','"
				+MsgConstant.VOUCHER_NO_3208+"' )";
			}
			if(treCode!=null&&!treCode.equals("")){
				ls_Voucher3501+=" and S_TRECODE = '"+treCode+"' ";
			}
			ls_Voucher3501+=" group by  S_ADMDIVCODE,S_STYEAR,S_VTCODE,S_TRECODE ";
			ls_SelectVoucher = ls_Voucher3501;
		}else if(MsgConstant.VOUCHER_NO_3502.equals(VoucherType)){
			//和商行对账要关联业务表
			String ls_Voucher3502 = "";
			String ls_TableName = "";
			if(MsgConstant.VOUCHER_NO_2301.equals(SubVoucherType)){
				ls_TableName = TvPayreckBankDto.tableName();
			}else if(MsgConstant.VOUCHER_NO_2302.equals(SubVoucherType)){
				ls_TableName = TvPayreckBankBackDto.tableName();
			}else{
				ls_TableName = TvPayreckBankDto.tableName();
			}
			ls_Voucher3502 = "select a.S_ADMDIVCODE,a.S_STYEAR,a.S_VTCODE,a.S_TRECODE,count(a.S_TRECODE),sum(a.N_MONEY),b.S_AGENTBNKCODE " +
			"from tv_voucherinfo a,"+ls_TableName+" b where a.S_DEALNO = b.I_VOUSRLNO and a.S_CONFIRUSERCODE = '"+createVoucherDate+"' and ( a.S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"' or a.S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and S_ORGCODE = '"+orgCode+"' ";
			if(SubVoucherType!=null&&!SubVoucherType.equals("")){
				ls_Voucher3502+=" and a.S_VTCODE = '"+SubVoucherType+"' ";
			}else{
				ls_Voucher3502+=" and (a.S_VTCODE = '"+MsgConstant.VOUCHER_NO_2301+"' or a.S_VTCODE = '"+MsgConstant.VOUCHER_NO_2302+"') ";
			}
			if(treCode!=null&&!treCode.equals("")){
				ls_Voucher3502+=" and a.S_TRECODE = '"+treCode+"' ";
			}
			ls_Voucher3502+=" group by  a.S_ADMDIVCODE,a.S_STYEAR,a.S_VTCODE,a.S_TRECODE,b.S_AGENTBNKCODE ";
			//如果子凭证类型为空，那么将记录集合并
			if(SubVoucherType==null||SubVoucherType.equals("")){
				ls_Voucher3502+=" UNION "+ls_Voucher3502.replace(TvPayreckBankDto.tableName(), TvPayreckBankBackDto.tableName());
			}
			ls_SelectVoucher = ls_Voucher3502;
		}else{
			String ls_Voucher3501 = "";
			//'x' as x 是为了补齐和商行对账sql返回列的个数
			ls_Voucher3501 = "select S_ADMDIVCODE,S_STYEAR,S_VTCODE,S_TRECODE,count(S_TRECODE),sum(N_MONEY),'x' as x " +
			"from tv_voucherinfo where S_CONFIRUSERCODE = '"+createVoucherDate+"' and ( S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"'  or S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and S_ORGCODE = '"+orgCode+"' ";
			ls_Voucher3501+=" and S_VTCODE in ('"
				+MsgConstant.VOUCHER_NO_5106+"','"
				+MsgConstant.VOUCHER_NO_5108+"','"
				+MsgConstant.VOUCHER_NO_5207+"','"
				+MsgConstant.VOUCHER_NO_5209+"','"
				+MsgConstant.VOUCHER_NO_3208+"' )";
			if(treCode!=null&&!treCode.equals("")){
				ls_Voucher3501+=" and S_TRECODE = '"+treCode+"' ";
			}
			ls_Voucher3501+=" group by  S_ADMDIVCODE,S_STYEAR,S_VTCODE,S_TRECODE ";
			
			String ls_Voucher3502 = "";
			String ls_TableName = TvPayreckBankDto.tableName();
			ls_Voucher3502 = "select a.S_ADMDIVCODE,a.S_STYEAR,a.S_VTCODE,a.S_TRECODE,count(a.S_TRECODE),sum(a.N_MONEY),b.S_AGENTBNKCODE " +
							"from tv_voucherinfo a,"+ls_TableName+" b where a.S_DEALNO = b.I_VOUSRLNO and a.S_CONFIRUSERCODE = '"
							+createVoucherDate+"' and ( a.S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"'  or a.S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and S_ORGCODE = '"+orgCode+"' ";
			ls_Voucher3502+=" and (a.S_VTCODE = '"+MsgConstant.VOUCHER_NO_2301+"' or a.S_VTCODE = '"+MsgConstant.VOUCHER_NO_2302+"') ";
			if(treCode!=null&&!treCode.equals("")){
				ls_Voucher3502+=" and a.S_TRECODE = '"+treCode+"' ";
			}
			ls_Voucher3502+=" group by  a.S_ADMDIVCODE,a.S_STYEAR,a.S_VTCODE,a.S_TRECODE,b.S_AGENTBNKCODE ";
			ls_Voucher3502+=" UNION "+ls_Voucher3502.replace(TvPayreckBankDto.tableName(), TvPayreckBankBackDto.tableName());
			ls_SelectVoucher = ls_Voucher3501 +" UNION "+ ls_Voucher3502;
		}
		return ls_SelectVoucher;
	}
	
	
	/**
	 * @author zhanghuibin
	 * 按照不同的凭证类型生成凭证明细sql语句（只查询相应的明细金额）
	 * AdmDivCode        区划代码
	 * StYear            年度
	 * VoucherType       对账类型
	 * SubVoucherType    凭证类型
	 * treCode           国库
	 * createVoucherDate 凭证日期
	 * PayBankCode       代理银行代码
	 */
	private String getDetailSelectConditon(String AdmDivCode,String StYear,String VoucherType,
			String SubVoucherType, String treCode,
			String createVoucherDate,String orgCode,String PayBankCode){
		//组装查询明细信息的SQL
		String ls_DetailSelectVoucher ="";
		//获得不同凭证的主表名
		String ls_TableName = "";
		if(MsgConstant.VOUCHER_NO_2301.equals(SubVoucherType)){//申请划款凭证回单2301
			ls_TableName = TvPayreckBankDto.tableName();
		}else if(MsgConstant.VOUCHER_NO_2302.equals(SubVoucherType)){//申请退款凭证回单2302
			ls_TableName = TvPayreckBankBackDto.tableName();
		}
		//查询明细信息的SQL
		String DetailSelectVoucherForTable ="";
		if(ls_TableName.equals("")){
			DetailSelectVoucherForTable="select S_VOUCHERNO,N_MONEY,S_STATUS from tv_voucherinfo where S_ADMDIVCODE = '"+AdmDivCode+"' and S_STYEAR = '"+StYear+"' and S_CONFIRUSERCODE = '"+createVoucherDate+"' and ( S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"' or S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and S_ORGCODE = '"+orgCode+"' and S_VTCODE = '"+SubVoucherType+"' and S_TRECODE = '"+treCode+"' ";
		}else{
			DetailSelectVoucherForTable ="select a.S_VOUCHERNO,a.N_MONEY,a.S_STATUS from tv_voucherinfo a,"+ls_TableName+" b where a.S_DEALNO = b.I_VOUSRLNO  and a.S_ADMDIVCODE = '"+AdmDivCode+"' and a.S_STYEAR = '"+StYear+"' and a.S_CONFIRUSERCODE = '"+createVoucherDate+"' and ( a.S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"' or a.S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and a.S_ORGCODE = '"+orgCode+"' and a.S_VTCODE = '"+SubVoucherType+"' and a.S_TRECODE = '"+treCode+"' ";
		}
		if(MsgConstant.VOUCHER_NO_3502.equals(VoucherType)){//与商行对账
			DetailSelectVoucherForTable += " and b.S_AGENTBNKCODE = '"+PayBankCode+"' ";
		}
		ls_DetailSelectVoucher=DetailSelectVoucherForTable;
		return ls_DetailSelectVoucher;
	}
	
	
	private String getVoucherTypeBySubVoucherType(String subVoucherType){
		String VoucherType = null;
		if(MsgConstant.VOUCHER_NO_5106.equals(subVoucherType)
				||MsgConstant.VOUCHER_NO_5108.equals(subVoucherType)
				||MsgConstant.VOUCHER_NO_5207.equals(subVoucherType)
				||MsgConstant.VOUCHER_NO_5209.equals(subVoucherType)
				||MsgConstant.VOUCHER_NO_3208.equals(subVoucherType)){
			VoucherType = MsgConstant.VOUCHER_NO_3501;
		}else if(MsgConstant.VOUCHER_NO_2301.equals(subVoucherType)
				||MsgConstant.VOUCHER_NO_2302.equals(subVoucherType)){
			VoucherType = MsgConstant.VOUCHER_NO_3502;
		}
		return VoucherType;
		
	}
}