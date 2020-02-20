package com.cfcc.itfe.component;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsAutogenervoucherDto;
import com.cfcc.itfe.persistence.dto.TsBillautosendDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * ���ܣ���ʱ������ֽ���ճ����� ���һ��Ԥ�����뱨��̶����α���
 * @author Administrator
 */
public class TimerAutoSendReport implements Callable {
	private static Log logger = LogFactory.getLog(TimerAutoSendReport.class);

	@SuppressWarnings("unchecked")
	public Object onCall(MuleEventContext eventContext) throws Exception {
		Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
		TsBillautosendDto _dto = new TsBillautosendDto();
		_dto.setSisauto(StateConstant.COMMON_NO);
		List<TsBillautosendDto> list = CommonFacade.getODB().findRsByDto(_dto);
		TvVoucherinfoDto vDto = null;
		List<TvVoucherinfoDto> report3401 = new ArrayList<TvVoucherinfoDto>();
		List<TvVoucherinfoDto> report3402 = new ArrayList<TvVoucherinfoDto>();
		TrStockdayrptDto kcdto = new TrStockdayrptDto();
		String getdate = null;
		Calendar calendar = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
		List datalist = null;
		for(int i=1;i<15;i++)
		{
			calendar = Calendar.getInstance();
			calendar.setTime(TimeFacade.getCurrentDate());
			calendar.add(Calendar.DATE, -i);
			getdate = dateFormat.format(calendar.getTime());
			kcdto.setSaccdate(getdate);
			kcdto.setSrptdate(getdate);
			datalist = CommonFacade.getODB().findRsByDto(kcdto);
			if(datalist!=null&&datalist.size()>0)
				break;
		}
		if(list==null||list.size()<=0)
			return eventContext;
		Set<String> tmpTreSet = new HashSet<String>();
		logger.debug("======================== ǰ�ö�ʱ��ƾ֤�ⷢ�𱨱�========================3401����"+list.size());
		for (TsBillautosendDto tmp : list) {
			vDto = new TvVoucherinfoDto();
			vDto.setSorgcode(tmp.getSorgcode());//��������
			vDto.setSvtcode(MsgConstant.VOUCHER_NO_3401);//����
			vDto.setStrecode(tmp.getStrecode());//�������
			vDto.setScheckdate(getdate);//��������
			vDto.setShold3(tmp.getSbelongflag());//Ͻ����־
			vDto.setSpaybankcode(tmp.getStaxorgcode());//���ջ���
			vDto.setShold1(tmp.getSbudgettype());//Ԥ������
			vDto.setScheckvouchertype(tmp.getSbillkind());//��������
			vDto.setShold2(tmp.getSbudgetlevelcode());//Ԥ�㼶��
			vDto.setScreatdate(TimeFacade.getCurrentStringTime());//����ʱ��
			if(report3401!=null&&report3401.size()==0)
			{
				TvVoucherinfoDto searchdto = (TvVoucherinfoDto)vDto.clone();
				searchdto.setScreatdate(null);
				List searchList = CommonFacade.getODB().findRsByDto(searchdto);
				if(searchList!=null&&searchList.size()>0)
					break;
			}
			report3401.add(vDto);
			logger.debug("��"+report3401.size()+"��==================����3401=================="+vDto.toString());
			if(tmpTreSet.add(vDto.getSorgcode()+vDto.getStrecode()))
			{
				vDto = new TvVoucherinfoDto();
				vDto.setSorgcode(tmp.getSorgcode());//��������
				vDto.setSvtcode(MsgConstant.VOUCHER_NO_3402);//����
				vDto.setStrecode(tmp.getStrecode());//�������
				vDto.setScheckdate(getdate);//��������
				vDto.setScreatdate(TimeFacade.getCurrentStringTime());//����ʱ��
				report3402.add(vDto);
			}
		}
		if(report3401!=null&&report3401.size()>0)
		{
			try
			{
				voucher.voucherGenerate(report3401);
			}catch(Exception e){
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
			voucher.voucherGenerate(report3402);
		}
		List<TvVoucherinfoDto> report3405 = new ArrayList<TvVoucherinfoDto>();
		List<TvVoucherinfoDto> report3406 = new ArrayList<TvVoucherinfoDto>();
		TsAutogenervoucherDto searchdto = new TsAutogenervoucherDto();
		searchdto.setSisauto(StateConstant.COMMON_NO);
		List<TsAutogenervoucherDto> findlist = CommonFacade.getODB().findRsByDto(searchdto);
		if(findlist!=null&&findlist.size()>0)
		{
			TvVoucherinfoDto zevou = null;
			for(TsAutogenervoucherDto temp:findlist)
			{
				zevou = new TvVoucherinfoDto();
				zevou.setScheckdate(getdate);//��������
				zevou.setSvtcode(temp.getSvtcode());//ƾ֤����
				zevou.setStrecode(temp.getStrecode());
				zevou.setSorgcode(temp.getSorgcode());
				if(MsgConstant.VOUCHER_NO_3405.equals(temp.getSvtcode()))
				{
					if(temp.getShold2()!=null&&!"".equals(temp.getShold2()))
						zevou.setShold3(temp.getShold3());//����Χ
					if(temp.getSext1()!=null&&!"".equals(temp.getSext1()))
						zevou.setSext1(temp.getSext1());//Ԥ�㼶��
					if(temp.getShold4()!=null&&!"".equals(temp.getShold4()))
						zevou.setShold4(temp.getShold4());//��Ŀ����
					if(report3405!=null&&report3405.size()==0)
					{
						TvVoucherinfoDto svdto = (TvVoucherinfoDto)zevou.clone();
						svdto.setScreatdate(null);
						List searchList = CommonFacade.getODB().findRsByDto(svdto);
						if(searchList!=null&&searchList.size()>0)
							break;
					}
					report3405.add(zevou);
				}
				else if(MsgConstant.VOUCHER_NO_3406.equals(temp.getSvtcode()))
				{
					zevou.setShold3(temp.getShold3());//����Χ
					if(temp.getShold2()!=null&&!"".equals(temp.getShold2()))
						zevou.setShold2(temp.getShold2());//�����ڱ�־
					if(temp.getShold1()!=null&&!"".equals(temp.getShold1()))
						zevou.setShold1(temp.getShold1());//Ԥ������
					if(temp.getSext1()!=null&&!"".equals(temp.getSext1()))
						zevou.setSext1(temp.getSext1());//Ԥ�㼶��
					if(temp.getShold4()!=null&&!"".equals(temp.getShold4()))
						zevou.setShold4(temp.getShold4());//��Ŀ����
					zevou.setSpaybankcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);//���ջ��ش���
					zevou.setSattach(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
					if(report3406!=null&&report3406.size()==0)
					{
						TvVoucherinfoDto svdto = (TvVoucherinfoDto)zevou.clone();
						svdto.setScreatdate(null);
						List searchList = CommonFacade.getODB().findRsByDto(svdto);
						if(searchList!=null&&searchList.size()>0)
							break;
					}
					report3406.add(zevou);
				}
			}
		}
		if(report3405!=null&&report3405.size()>0)
		{
			try
			{
				voucher.voucherGenerate(report3405);
			}catch(Exception e){
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
		}
		if(report3406!=null&&report3406.size()>0)
		{
			try
			{
				voucher.voucherGenerate(report3406);
			}catch(Exception e){
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
		}
		logger
				.debug("========================ǰ�ö�ʱ��ƾ֤�ⷢ�𱨱� ========================");
		return eventContext;
	}
	
	
}
