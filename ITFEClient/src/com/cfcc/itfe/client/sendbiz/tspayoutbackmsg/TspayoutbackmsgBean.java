package com.cfcc.itfe.client.sendbiz.tspayoutbackmsg;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;

/**
 * codecomment: 
 * @author db2itfe
 * @time   13-09-05 17:32:47
 * 子系统: SendBiz
 * 模块:tspayoutbackmsg
 * 组件:Tspayoutbackmsg
 */
public class TspayoutbackmsgBean extends AbstractTspayoutbackmsgBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TspayoutbackmsgBean.class);
    private ITFELoginInfo loginfo= (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    private PageRequest request = new PageRequest();
    public TspayoutbackmsgBean() {
      super();
      dto = new TvVoucherinfoDto();
      pagingcontext = new PagingContext(null);
      request = new PageRequest();            
    }
    
	/**
	 * Direction: 生成凭证
	 * ename: voucherGenerator
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherGenerator(Object o){
    	try{
    		TvPayoutmsgmainDto _dto = new TvPayoutmsgmainDto();
        	if(dto.getStrecode()!=null){
        		_dto.setStrecode(dto.getStrecode());
        	}
        	if(dto.getScreatdate()!=null && !dto.getScreatdate().equals("")){
        		_dto.setScommitdate(dto.getScreatdate());
        	}
        	_dto.setSbackflag(StateConstant.MSG_BACK_FLAG_YES);
        	List<TvPayoutmsgmainDto> payoutlist = commonDataAccessService.findRsByDto(_dto);
        	for(TvPayoutmsgmainDto payoutdto : payoutlist){
        		TvPayoutmsgmainDto odto = new TvPayoutmsgmainDto();
        		odto.setStaxticketno(payoutdto.getSorivouno());
        		odto.setSgenticketdate(payoutdto.getSorivoudate());  
        		TvPayoutmsgmainDto opayoutdto = (TvPayoutmsgmainDto) commonDataAccessService.findRsByDto(odto).get(0);
        		
        		TvVoucherinfoPK ovdto = new TvVoucherinfoPK();
        		ovdto.setSdealno(opayoutdto.getSdealno());
        		TvVoucherinfoDto dvdto = (TvVoucherinfoDto) commonDataAccessService.find(ovdto);
        		
        		TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
        		voucherDto.setSdealno(payoutdto.getSbizno());
				voucherDto.setNmoney(payoutdto.getNmoney());
				voucherDto.setSadmdivcode(dvdto.getSadmdivcode());
				voucherDto.setScreatdate(TimeFacade.getCurrentStringTime());
				voucherDto.setSfilename(payoutdto.getSfilename());
				voucherDto.setSorgcode(payoutdto.getSorgcode());
				voucherDto.setSattach(payoutdto.getSaddword());

				voucherDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				voucherDto.setSdemo("处理成功");
				voucherDto.setSstyear(payoutdto.getSofyear());
				voucherDto.setStrecode(payoutdto.getStrecode());
				voucherDto.setSvoucherflag("1");
				voucherDto.setSvoucherno(payoutdto.getStaxticketno());
				voucherDto.setSvtcode(MsgConstant.VOUCHER_NO_3208);
        	}
        	
    	}catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
    	
          return super.voucherGenerator(o);
    }

	/**
	 * Direction: 签章
	 * ename: voucherStamp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){
          return super.voucherStamp(o);
    }

	/**
	 * Direction: 签章撤销
	 * ename: voucherStampCancle
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStampCancle(Object o){
          return super.voucherStampCancle(o);
    }

	/**
	 * Direction: 凭证查看
	 * ename: voucherView
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherView(Object o){
          return super.voucherView(o);
    }

	/**
	 * Direction: 发生凭证
	 * ename: voucherSend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
          return super.voucherSend(o);
    }

	/**
	 * Direction: 查询
	 * ename: voucherSearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSearch(Object o){
          return super.voucherSearch(o);
    }

	/**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
          return super.selectAll(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}