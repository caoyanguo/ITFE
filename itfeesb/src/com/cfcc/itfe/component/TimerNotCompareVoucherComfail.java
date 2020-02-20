package com.cfcc.itfe.component;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 功能：前置定时将数据库中未比对的报文做签收失败
 * @author Administrator
 */
@SuppressWarnings("unchecked")
public class TimerNotCompareVoucherComfail implements Callable {
	private static Log logger = LogFactory.getLog(TimerNotCompareVoucherComfail.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.debug("========================前置定时将数据库中未比对的报文做退回任务开始========================");
		VoucherService voucherService = new VoucherService();
		if(SrvCacheFacade.getMapVoucherCompare()!=null&&SrvCacheFacade.getMapVoucherCompare().size()>0)
		{
			List tempList = null;
			for(String mapkey:SrvCacheFacade.getMapVoucherCompare().keySet())
			{
				tempList = SrvCacheFacade.getMapVoucherCompare().get(mapkey);
				if(tempList!=null&&tempList.size()>0)
					dataHandle(tempList,voucherService);
			}
		}
		returnNotComparevoucher(voucherService);
		SrvCacheFacade.setMapVoucherCompare(new HashMap<String,List>());
		logger.debug("========================前置定时将数据库中未比对的报文做退回任务结束========================");
		return eventContext;
	}

	private void dataHandle(List tempList,VoucherService voucherService) {
		TvVoucherinfoDto vDto = null;
		IDto maindto = null;
		vDto = (TvVoucherinfoDto)tempList.get(0);
		maindto = (IDto)tempList.get(1);
		vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL);
		vDto.setSdemo("退回原因：当天未比对数据,定时任务退回处理" );
		vDto.setSreturnerrmsg(null);
		vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// 设置凭证退回日期
		if(voucherService==null)
			try {
				voucherService = new VoucherService();
			} catch (ITFEBizException e1) {
				logger.error("前置定时将数据库中未比对的报文退回初始化对象时出现异常："+vDto.getSvoucherno() + e1.toString());
			}
		String err = voucherService.returnVoucherBack(null, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),vDto.getSvtcode(), new String[]{vDto.getSvoucherno()}, new String[]{"退回原因：当天未比对数据,定时任务退回处理"});
		if(err!=null)
		{
			logger.debug(err);
			return;
		}
		if(MsgConstant.VOUCHER_NO_5201.equals(vDto.getSvtcode()))
		{
			((TfDirectpaymsgmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}else if(MsgConstant.VOUCHER_NO_5108.equals(vDto.getSvtcode()))
		{
			((TvDirectpaymsgmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}else if(MsgConstant.VOUCHER_NO_5253.equals(vDto.getSvtcode()))
		{
			((TfDirectpayAdjustmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}else if(MsgConstant.VOUCHER_NO_5106.equals(vDto.getSvtcode()))
		{
			((TvGrantpaymsgmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}else if(MsgConstant.VOUCHER_NO_5351.equals(vDto.getSvtcode()))
		{
			((TfGrantpayAdjustmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}else if(MsgConstant.VOUCHER_NO_2301.equals(vDto.getSvtcode()))
		{
			((TvPayreckBankDto)maindto).setSresult(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}else if(MsgConstant.VOUCHER_NO_2302.equals(vDto.getSvtcode()))
		{
			((TvPayreckBankBackDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}
		else if(MsgConstant.VOUCHER_NO_5209.equals(vDto.getSvtcode()))
		{
			((TvDwbkDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}
		else if(MsgConstant.VOUCHER_NO_8207.equals(vDto.getSvtcode()))
		{
			((TfPaymentDetailsmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}
		else if(MsgConstant.VOUCHER_NO_2252.equals(vDto.getSvtcode()))
		{
			((TfPaybankRefundmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}
		else if(MsgConstant.VOUCHER_NO_5951.equals(vDto.getSvtcode()))
		{
			((TfUnitrecordmainDto)maindto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
		}
		try {
//				IDto[] dtos = new IDto[updateList.size()];
//				dtos = updateList.toArray(dtos);
				DatabaseFacade.getDb().update(vDto);
				DatabaseFacade.getODB().update(maindto);
		} catch (JAFDatabaseException e) {
			logger.error("前置定时将内存中未比对的报文更新凭证信息时出现异常："+vDto.getSvoucherno() + e.toString());
		}
	}
	private void returnNotComparevoucher(VoucherService voucherService)
	{
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
//		List<IDto> updateList = new ArrayList<IDto>();
		try {
			vDto.setScreatdate(TimeFacade.getCurrentStringTime());
			vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT);
			List<TvVoucherinfoDto> list = CommonFacade.getODB().findRsByDto(vDto);
			if(list!=null&&list.size()>0)
			{
				IDto tempdto = null;
				List<IDto> tempList = null;
				for(int i=0;i<list.size();i++)
				{
					vDto = list.get(i);
					vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL);
					vDto.setSdemo("退回原因：当天未比对数据,定时任务退回处理" );
					vDto.setSreturnerrmsg(null);
					vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// 设置凭证退回日期
//					updateList.add(vDto);
					String err = voucherService.returnVoucherBack(null, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),vDto.getSvtcode(), new String[]{vDto.getSvoucherno()}, new String[]{"退回原因：当天未比对数据,定时任务退回处理"});
					if(err!=null)
					{
						logger.debug(err);
						continue;
					}
					tempList = VoucherUtil.findMainDtoByVoucher(vDto);
					if(tempList!=null&&tempList.size()>0)
					{
						tempdto = tempList.get(0);
						if(MsgConstant.VOUCHER_NO_5201.equals(vDto.getSvtcode()))
						{
							((TfDirectpaymsgmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}else if(MsgConstant.VOUCHER_NO_5108.equals(vDto.getSvtcode()))
						{
							((TvDirectpaymsgmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}else if(MsgConstant.VOUCHER_NO_5253.equals(vDto.getSvtcode()))
						{
							((TfDirectpayAdjustmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}else if(MsgConstant.VOUCHER_NO_5106.equals(vDto.getSvtcode()))
						{
							((TvGrantpaymsgmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}else if(MsgConstant.VOUCHER_NO_5351.equals(vDto.getSvtcode()))
						{
							((TfGrantpayAdjustmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}else if(MsgConstant.VOUCHER_NO_2301.equals(vDto.getSvtcode()))
						{
							((TvPayreckBankDto)tempdto).setSresult(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}else if(MsgConstant.VOUCHER_NO_2302.equals(vDto.getSvtcode()))
						{
							((TvPayreckBankBackDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}
						else if(MsgConstant.VOUCHER_NO_5209.equals(vDto.getSvtcode()))
						{
							((TvDwbkDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}
						else if(MsgConstant.VOUCHER_NO_8207.equals(vDto.getSvtcode()))
						{
							((TfPaymentDetailsmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}
						else if(MsgConstant.VOUCHER_NO_2252.equals(vDto.getSvtcode()))
						{
							((TfPaybankRefundmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}
						else if(MsgConstant.VOUCHER_NO_5951.equals(vDto.getSvtcode()))
						{
							((TfUnitrecordmainDto)tempdto).setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
						}
					}
					DatabaseFacade.getODB().update(vDto);
					DatabaseFacade.getODB().update(tempdto);
				}
			}
//			if(updateList!=null&&updateList.size()>0)
//			{
//				DatabaseFacade.getDb().update(CommonUtil.listTArray(updateList));
//			}
		} catch (JAFDatabaseException e) {
			logger.error("前置定时将数据库中未比对的报文更新凭证信息时出现异常："+vDto.getSvoucherno() + e.toString());
		} catch (ValidateException e) {
			logger.error("前置定时将数据库中未比对的报文更新凭证信息时出现异常："+vDto.getSvoucherno() + e.toString());
		} catch (ITFEBizException e) {
			logger.error("前置定时将数据库中未比对的报文更新凭证信息时出现异常："+vDto.getSvoucherno() + e.toString());
		}
	}
}
