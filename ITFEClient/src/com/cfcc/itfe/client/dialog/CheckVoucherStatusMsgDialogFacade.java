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
	 * ����Ȩ����
	 * 
	 * @param checkfail
	 * @param message
	 *            ��Ȩ�����е���ʾ��Ϣ
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
				MessageDialog.openMessageDialog(null, "��ѡ�����ƾ֤״̬��");
				open(checklist, checkfail,commonDataAccessService);
			}else if(selectVoucherStatus.equals("����ɹ�")){
				if(verifyXpaydate(inputxpaydate))
					open(checklist, checkfail,commonDataAccessService);
				updateVoucherStatus(checklist, selectVoucherStatus, inputxpaydate);	
				return true;
			}else if(selectVoucherStatus.equals("��Ʊ�ɹ�")){
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
				MessageDialog.openMessageDialog(null, "��ѡ�������ʧ��ԭ��");
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
			if(selectVoucherStatus.equals("����ɹ�")){
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			}else{
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);
			}
		}
	}
	
	public static void updateVoucherStatus(List<TvVoucherinfoDto> checklist,String selectVoucherStatus,String inputxpaydate){
		for(int i = 0; i < checklist.size(); i++){
			checklist.get(i).setSdemo(selectVoucherStatus);
			if(selectVoucherStatus.equals("����ɹ�")){
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				checklist.get(i).setSext5(inputxpaydate);
			}else if(selectVoucherStatus.equals("��Ʊ�ɹ�")){
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_SUCCESS_BACK);
				checklist.get(i).setSext5(inputxpaydate);
			}else{
				checklist.get(i).setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);
			}
		}
	}

	
	public static void insertTsCheckReason(List<TvVoucherinfoDto> checklist,String failcode,String inputmsg,ICommonDataAccessService commonDataAccessService,List<TsCheckfailreasonDto> checkfail) throws ITFEBizException{
		
		TsCheckfailreasonDto checkdouble = new TsCheckfailreasonDto();
		checkdouble.setSorgcode(checklist.get(0).getSorgcode());// �����������
		checkdouble.setScheckfailcode(failcode);
		List<TsCheckfailreasonDto> list = commonDataAccessService.findRsByDto(checkdouble);
		if(list!=null && list.size()>0){
			MessageDialog.openMessageDialog(null, "ʧ��ԭ������ظ������������룡");
			open(checklist, checkfail,commonDataAccessService);
		}else{
			checkdouble.setSorgcode(checklist.get(0).getSorgcode());// �����������
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
			MessageDialog.openMessageDialog(null, "�����ʵ��֧�����ڸ�ʽ����Ϊ�գ�");
			return flag;
		}
		if(xpaydate.length()<8){
			MessageDialog.openMessageDialog(null, "�����ʵ��֧�����ڸ�ʽ���淶��");
			return flag;
		}
		if(Integer.parseInt(xpaydate)>Integer.parseInt(TimeFacade.getCurrentStringTime())){
			MessageDialog.openMessageDialog(null, "�����ʵ��֧�����ڲ��ܴ��ڵ�ǰ���ڣ�");
			return flag;
		}		
		return false;	
	}
}
