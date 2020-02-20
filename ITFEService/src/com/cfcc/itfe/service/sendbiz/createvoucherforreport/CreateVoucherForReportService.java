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
		//�׳��쳣ʱ�Ķ������� author:zhanghuibin
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
				//���ݲ�ͬ��ƾ֤���ͻ�ȡ��ͬ�Ķ�������
				String type="";
				//�����������VoucherTypeΪnull������ݾ����ƾ֤����ls_VtCode�����Ӧ�Ķ�������
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
				List<Object> mianDtos=new ArrayList<Object>();//�洢������Ϣ author:zhanghuibin
				TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
				voucherDto.setSdealno(ls_DealNo);
				voucherDto.setNmoney(lbig_Money);
				voucherDto.setSadmdivcode(ls_AdmDivCode);
				voucherDto.setScreatdate(currentDate);
				voucherDto.setSfilename(FileName);
				voucherDto.setSorgcode(orgCode);
				voucherDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
				voucherDto.setSdemo(ls_PayBankCode);//ʹ�ø��ֶδ�Ŵ������б���
				voucherDto.setSstyear(ls_StYear);
				voucherDto.setStrecode(ls_TreCode);
				voucherDto.setSvoucherflag("1");
				voucherDto.setSvoucherno(ls_DealNo);
				voucherDto.setSvtcode(type);
				voucherDto.setScheckvouchertype(ls_VtCode);
				voucherDto.setSattach(ls_VtCode);//ʹ�ø��ֶδ���ö��˵�ƾ֤����
				voucherDto.setIcount(li_Count);
				voucherDto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));
				voucherDto.setScheckdate(createVoucherDate);//�������ڣ�ȡƾ֤�ص�/�˻�����
				voucherDto.setSverifyusercode(createVoucherDate);//�����������
				
				/**
				 * ��ѯ��ϸ��Ϣ
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
					voucherDetailDto.setSdealno(detailId);//��Sdealno������ϸƾ֤ID
					voucherDetailDto.setNmoney(Money);//��ϸ���
					voucherDetailDto.setSvoucherno(voucherno);//��ϸƾ֤��ƾ֤���
					voucherDetailDto.setShold1(status);//��shold1������ϸƾ֤�Ľ���״̬
					detailDtos.add(voucherDetailDto);
				}
				mianDtos.add(voucherDto);
				mianDtos.add(detailDtos);
				Map map = null;
				IVoucherDto2Map voucher=(IVoucherDto2Map) ContextFactory.getApplicationContext().getBean(
						MsgConstant.VOUCHER_DTO2MAP+type);	
//				map = voucher.voucherTranfor(voucherDto);//�������ϸǰ��ִ�з���
				map = voucher.tranfor(mianDtos);//��������Ϣת����ƾ֤���� author:zhanghuibin�������ϸ���ִ�з�����
				VoucherUtil.sendTips(voucherDto,map);
				voucherDto.setSdemo("����ɹ�");
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
	 * ���ղ�ͬ��ƾ֤��������sql���
	 */
	private String getSelectConditon(String VoucherType,
			String SubVoucherType, String treCode,
			String createVoucherDate,String orgCode){
		String ls_SelectVoucher ="";
		//�Ͳ������ˣ����ù���ҵ����ѯ
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
			//�����ж���Ҫ����ҵ���
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
			//�����ƾ֤����Ϊ�գ���ô����¼���ϲ�
			if(SubVoucherType==null||SubVoucherType.equals("")){
				ls_Voucher3502+=" UNION "+ls_Voucher3502.replace(TvPayreckBankDto.tableName(), TvPayreckBankBackDto.tableName());
			}
			ls_SelectVoucher = ls_Voucher3502;
		}else{
			String ls_Voucher3501 = "";
			//'x' as x ��Ϊ�˲�������ж���sql�����еĸ���
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
	 * ���ղ�ͬ��ƾ֤��������ƾ֤��ϸsql��䣨ֻ��ѯ��Ӧ����ϸ��
	 * AdmDivCode        ��������
	 * StYear            ���
	 * VoucherType       ��������
	 * SubVoucherType    ƾ֤����
	 * treCode           ����
	 * createVoucherDate ƾ֤����
	 * PayBankCode       �������д���
	 */
	private String getDetailSelectConditon(String AdmDivCode,String StYear,String VoucherType,
			String SubVoucherType, String treCode,
			String createVoucherDate,String orgCode,String PayBankCode){
		//��װ��ѯ��ϸ��Ϣ��SQL
		String ls_DetailSelectVoucher ="";
		//��ò�ͬƾ֤��������
		String ls_TableName = "";
		if(MsgConstant.VOUCHER_NO_2301.equals(SubVoucherType)){//���뻮��ƾ֤�ص�2301
			ls_TableName = TvPayreckBankDto.tableName();
		}else if(MsgConstant.VOUCHER_NO_2302.equals(SubVoucherType)){//�����˿�ƾ֤�ص�2302
			ls_TableName = TvPayreckBankBackDto.tableName();
		}
		//��ѯ��ϸ��Ϣ��SQL
		String DetailSelectVoucherForTable ="";
		if(ls_TableName.equals("")){
			DetailSelectVoucherForTable="select S_VOUCHERNO,N_MONEY,S_STATUS from tv_voucherinfo where S_ADMDIVCODE = '"+AdmDivCode+"' and S_STYEAR = '"+StYear+"' and S_CONFIRUSERCODE = '"+createVoucherDate+"' and ( S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"' or S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and S_ORGCODE = '"+orgCode+"' and S_VTCODE = '"+SubVoucherType+"' and S_TRECODE = '"+treCode+"' ";
		}else{
			DetailSelectVoucherForTable ="select a.S_VOUCHERNO,a.N_MONEY,a.S_STATUS from tv_voucherinfo a,"+ls_TableName+" b where a.S_DEALNO = b.I_VOUSRLNO  and a.S_ADMDIVCODE = '"+AdmDivCode+"' and a.S_STYEAR = '"+StYear+"' and a.S_CONFIRUSERCODE = '"+createVoucherDate+"' and ( a.S_STATUS = '"+DealCodeConstants.VOUCHER_SUCCESS+"' or a.S_STATUS = '"+DealCodeConstants.VOUCHER_FAIL+"' ) and a.S_ORGCODE = '"+orgCode+"' and a.S_VTCODE = '"+SubVoucherType+"' and a.S_TRECODE = '"+treCode+"' ";
		}
		if(MsgConstant.VOUCHER_NO_3502.equals(VoucherType)){//�����ж���
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