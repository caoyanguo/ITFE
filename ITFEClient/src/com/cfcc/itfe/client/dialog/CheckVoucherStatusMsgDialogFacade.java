package com.cfcc.itfe.client.dialog;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.rcp.util.MessageDialog;
public class CheckVoucherStatusMsgDialogFacade {
	private static Log log = LogFactory.getLog(BursarAffirmDialogFacade.class);

	/**
	 * 打开授权界面
	 * 
	 * @param checkfail
	 * @param message
	 *            授权见面中的提示信息
	 * @return
	 * @throws ITFEBizException
	 */
	public static boolean open(List<TvVoucherinfoDto> checklist,
			List<TsCheckfailreasonDto> checkfail,
			ICommonDataAccessService commonDataAccessService)
			throws ITFEBizException {
		final CheckVoucherStatusMsgDialog dialog = new CheckVoucherStatusMsgDialog(null,
				checkfail, checklist);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			String selectVoucherStatus=dialog.getSelectVoucherStatus();
			String selectmsg = dialog.getSelectmsg();
			String failcode = dialog.getInputfailcode();
			String inputmsg = dialog.getInputmessage();
			String inputxpaydate = dialog.getInputxpaydate();
			if (!isNotNull(selectVoucherStatus)) {
				MessageDialog.openMessageDialog(null, "请选择更改凭证状态！");
				open(checklist, checkfail,commonDataAccessService);
			}else if(selectVoucherStatus.equals("处理成功")){
				if(verifyXpaydate(inputxpaydate))
					open(checklist, checkfail,commonDataAccessService);
				updateVoucherStatus(checklist, selectVoucherStatus, inputxpaydate);	
				return true;
			}else if(selectVoucherStatus.equals("退票成功")){
				if(verifyXpaydate(inputxpaydate))
					open(checklist, checkfail,commonDataAccessService);
				updateVoucherStatus(checklist, selectVoucherStatus, inputxpaydate);	
				return true;
			}else if(isNotNull(selectmsg)){
				updateVoucherStatus(checklist,selectmsg.substring(selectmsg.indexOf("_")+1));
				return true;
			}else if(isNotNull(inputmsg)&&isNotNull(failcode)){
				insertTsCheckReason(checklist, failcode, inputmsg, commonDataAccessService, checkfail);
				updateVoucherStatus(checklist,inputmsg);
				return true;
			}else {
				MessageDialog.openMessageDialog(null, "请选择或输入失败原因！");
				open(checklist, checkfail,commonDataAccessService);
			}
		}
		return false;
	}
	
	public static boolean isNotNull(String obj){
		if(obj!=null&&!obj.equals("")){
			return true;
		}return false;
	}
	
	public static void updateVoucherStatus(List<TvVoucherinfoDto> checklist,String selectVoucherStatus){
		for(int i = 0; i < checklist.size(); i++){
			checklist.get(i).setSdemo(selectVoucherStatus);
			if(selectVoucherStatus.equals("处理成功")){
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			}else{
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);
			}
		}
	}
	
	public static void updateVoucherStatus(List<TvVoucherinfoDto> checklist,String selectVoucherStatus,String inputxpaydate){
		for(int i = 0; i < checklist.size(); i++){
			checklist.get(i).setSdemo(selectVoucherStatus);
			if(selectVoucherStatus.equals("处理成功")){
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				checklist.get(i).setSext5(inputxpaydate);
			}else if(selectVoucherStatus.equals("退票成功")){
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_SUCCESS_BACK);
				checklist.get(i).setSext5(inputxpaydate);
			}else{
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);
			}
		}
	}

	
	public static void insertTsCheckReason(List<TvVoucherinfoDto> checklist,String failcode,String inputmsg,ICommonDataAccessService commonDataAccessService,List<TsCheckfailreasonDto> checkfail) throws ITFEBizException{
		
		TsCheckfailreasonDto checkdouble = new TsCheckfailreasonDto();
		checkdouble.setSorgcode(checklist.get(0).getSorgcode());// 核算主体代码
		checkdouble.setScheckfailcode(failcode);
		List<TsCheckfailreasonDto> list = commonDataAccessService.findRsByDto(checkdouble);
		if(list!=null && list.size()>0){
			MessageDialog.openMessageDialog(null, "失败原因代码重复，请重新输入！");
			open(checklist, checkfail,commonDataAccessService);
		}else{
			checkdouble.setSorgcode(checklist.get(0).getSorgcode());// 核算主体代码
			checkdouble.setScheckfailcode(failcode);
			checkdouble.setScheckfaildsp(inputmsg);
			checkdouble.setTssysupdate(DateUtil
					.currentTimestamp());
			commonDataAccessService.create(checkdouble);
		}
	}
	
	public static boolean verifyXpaydate(String xpaydate){
		boolean flag=true;
		if(StringUtils.isBlank(xpaydate)){
			MessageDialog.openMessageDialog(null, "输入的实际支付日期格式不能为空！");
			return flag;
		}
		if(xpaydate.length()<8){
			MessageDialog.openMessageDialog(null, "输入的实际支付日期格式不规范！");
			return flag;
		}
		if(Integer.parseInt(xpaydate)>Integer.parseInt(TimeFacade.getCurrentStringTime())){
			MessageDialog.openMessageDialog(null, "输入的实际支付日期不能大于当前日期！");
			return flag;
		}		
		return false;	
	}
}
