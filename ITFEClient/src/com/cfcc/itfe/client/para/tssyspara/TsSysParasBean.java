package com.cfcc.itfe.client.para.tssyspara;

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
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tssyspara.ITsSysParasService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsSysparaDto;

/**
 * codecomment: 
 * @author t60
 * @time   12-03-02 09:11:15
 * 子系统: Para
 * 模块:tsSysPara
 * 组件:TsSysParas
 */
public class TsSysParasBean extends AbstractTsSysParasBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsSysParasBean.class);
    private ITFELoginInfo loginfo ;
    private boolean twbk_dirc;
    private boolean twbk_batch;
    private boolean payout_dirc;
    private boolean payout_batch;
    private boolean corr_dirc;
    private boolean corr_batch;
    private boolean directpay_dirc;
    private boolean directpay_batch;
    private boolean grantpay_dirc;
    private boolean grantpay_batch;
    private boolean pbcdirectpay_dirc;
    private boolean pbcdirectpay_batch;
    private boolean batch_dirc;
    private boolean conpay_dirc;
    private boolean conpay_batch;
    private boolean conpayback_dirc;
    private boolean conpayback_batch;
    public TsSysParasBean() {
      super();
      dto = new TsSysparaDto();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      init();
    }
    
	/**
	 * Direction: 保存
	 * ename: saveInfo
	 * 引用方法: 
	 * viewers: 参数维护
	 * messages: 
	 */
    public String saveInfo(Object o){
    	List<TsSysparaDto> dtoList=new ArrayList();
    	TsSysparaDto paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_RET_TREASURY);
		//说明
		paradto.setSdescript("退库");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.twbk_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);
		
    	paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_RET_TREASURY);
		//说明
		paradto.setSdescript("退库");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.twbk_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PAY_OUT);
		//说明
		paradto.setSdescript("实拨资金");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.payout_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);

		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PAY_OUT);
		//说明
		paradto.setSdescript("实拨资金");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.payout_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_CORRECT);
		//说明
		paradto.setSdescript("更正");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.corr_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_CORRECT);
		//说明
		paradto.setSdescript("更正");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.corr_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);
		//说明
		paradto.setSdescript("直接支付额度");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.directpay_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);
		//说明
		paradto.setSdescript("直接支付额度");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.directpay_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
		//说明
		paradto.setSdescript("授权支付额度");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.grantpay_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
		//说明
		paradto.setSdescript("授权支付额度");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.grantpay_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_BATCH_OUT);
		//说明
		paradto.setSdescript("代发财政款项");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.batch_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
		//说明
		paradto.setSdescript("人行办理支付");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.pbcdirectpay_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);	
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
		//说明
		paradto.setSdescript("人行办理支付");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.pbcdirectpay_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
		//说明
		paradto.setSdescript("商行划款申请");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.conpay_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);	
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
		//说明
		paradto.setSdescript("商行划款申请");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.conpay_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
		//说明
		paradto.setSdescript("商行退款申请");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.conpayback_dirc));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("10");
		dtoList.add(paradto);	
		
		paradto=new TsSysparaDto();
    	//业务类型
		paradto.setScode(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
		//说明
		paradto.setSdescript("商行退款申请");
		//机构代码
		paradto.setSorgcode(loginfo.getSorgcode());
		//参数值
		paradto.setSvalue(changeBoolToStr(this.conpayback_batch));
		//操作类型(10直接提交,20批量销号)
		paradto.setSopertype("20");
		dtoList.add(paradto);
		
		try {
			tsSysParasService.modify(dtoList);
			
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "销号提交权限设置时出现错误！");
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
          return super.saveInfo(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
    private void init(){
		
		TsSysparaDto paradto=new TsSysparaDto();
    	paradto.setSorgcode(loginfo.getSorgcode());
		try {
			List<TsSysparaDto> dtoList=commonDataAccessService.findRsByDto(paradto);
			for(TsSysparaDto pdto:dtoList){
				//业务类型
				String scode=pdto.getScode();
				//参数值(0否,1是)
				String svalue=pdto.getSvalue();
				//操作类型(10直接提交,20批量销号)
				String soperType=pdto.getSopertype();
				if(scode.equals(BizTypeConstant.BIZ_TYPE_RET_TREASURY)&&svalue.equals("1")&&soperType.equals("10")){
					this.twbk_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_RET_TREASURY)&&svalue.equals("1")&&soperType.equals("20")){
					this.twbk_batch=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_BATCH_OUT)&&svalue.equals("1")&&soperType.equals("10")){
					this.batch_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_CORRECT)&&svalue.equals("1")&&soperType.equals("10")){
					this.corr_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_CORRECT)&&svalue.equals("1")&&soperType.equals("20")){
					this.corr_batch=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN)&&svalue.equals("1")&&soperType.equals("10")){
					this.directpay_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN)&&svalue.equals("1")&&soperType.equals("20")){
					this.directpay_batch=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN)&&svalue.equals("1")&&soperType.equals("10")){
					this.grantpay_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN)&&svalue.equals("1")&&soperType.equals("20")){
					this.grantpay_batch=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_PAY_OUT)&&svalue.equals("1")&&soperType.equals("10")){
					this.payout_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_PAY_OUT)&&svalue.equals("1")&&soperType.equals("20")){
					this.payout_batch=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY)&&svalue.equals("1")&&soperType.equals("10")){
					this.pbcdirectpay_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY)&&svalue.equals("1")&&soperType.equals("20")){
					this.pbcdirectpay_batch=true;
				}else if(scode.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)&&svalue.equals("1")&&soperType.equals("10")){
					this.conpay_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)&&svalue.equals("1")&&soperType.equals("20")){
					this.conpay_batch=true;
				}else if(scode.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)&&svalue.equals("1")&&soperType.equals("10")){
					this.conpayback_dirc=true;
				}
				else if(scode.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)&&svalue.equals("1")&&soperType.equals("20")){
					this.conpayback_batch=true;
				}
			}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "获取销号提交权限数据时出现错误！");
		}
    }


	
    public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public boolean isTwbk_dirc() {
		return twbk_dirc;
	}

	public void setTwbk_dirc(boolean twbk_dirc) {
		this.twbk_dirc = twbk_dirc;
	}

	public boolean isTwbk_batch() {
		return twbk_batch;
	}

	public void setTwbk_batch(boolean twbk_batch) {
		this.twbk_batch = twbk_batch;
	}

	public boolean isPayout_dirc() {
		return payout_dirc;
	}

	public void setPayout_dirc(boolean payout_dirc) {
		this.payout_dirc = payout_dirc;
	}

	public boolean isPayout_batch() {
		return payout_batch;
	}

	public void setPayout_batch(boolean payout_batch) {
		this.payout_batch = payout_batch;
	}

	public boolean isCorr_dirc() {
		return corr_dirc;
	}

	public void setCorr_dirc(boolean corr_dirc) {
		this.corr_dirc = corr_dirc;
	}

	public boolean isCorr_batch() {
		return corr_batch;
	}

	public void setCorr_batch(boolean corr_batch) {
		this.corr_batch = corr_batch;
	}

	public boolean isDirectpay_dirc() {
		return directpay_dirc;
	}

	public void setDirectpay_dirc(boolean directpay_dirc) {
		this.directpay_dirc = directpay_dirc;
	}

	public boolean isDirectpay_batch() {
		return directpay_batch;
	}

	public void setDirectpay_batch(boolean directpay_batch) {
		this.directpay_batch = directpay_batch;
	}

	public boolean isGrantpay_dirc() {
		return grantpay_dirc;
	}

	public void setGrantpay_dirc(boolean grantpay_dirc) {
		this.grantpay_dirc = grantpay_dirc;
	}

	public boolean isGrantpay_batch() {
		return grantpay_batch;
	}

	public void setGrantpay_batch(boolean grantpay_batch) {
		this.grantpay_batch = grantpay_batch;
	}

	public boolean isPbcdirectpay_dirc() {
		return pbcdirectpay_dirc;
	}

	public void setPbcdirectpay_dirc(boolean pbcdirectpay_dirc) {
		this.pbcdirectpay_dirc = pbcdirectpay_dirc;
	}

	public boolean isPbcdirectpay_batch() {
		return pbcdirectpay_batch;
	}

	public void setPbcdirectpay_batch(boolean pbcdirectpay_batch) {
		this.pbcdirectpay_batch = pbcdirectpay_batch;
	}

	public boolean isBatch_dirc() {
		return batch_dirc;
	}

	public void setBatch_dirc(boolean batch_dirc) {
		this.batch_dirc = batch_dirc;
	}

	public boolean isConpay_dirc() {
		return conpay_dirc;
	}

	public void setConpay_dirc(boolean conpay_dirc) {
		this.conpay_dirc = conpay_dirc;
	}

	public boolean isConpay_batch() {
		return conpay_batch;
	}

	public void setConpay_batch(boolean conpay_batch) {
		this.conpay_batch = conpay_batch;
	}

	public boolean isConpayback_dirc() {
		return conpayback_dirc;
	}

	public void setConpayback_dirc(boolean conpayback_dirc) {
		this.conpayback_dirc = conpayback_dirc;
	}

	public boolean isConpayback_batch() {
		return conpayback_batch;
	}

	public void setConpayback_batch(boolean conpayback_batch) {
		this.conpayback_batch = conpayback_batch;
	}

	/**
     * 转换BOOLEAN值成String类型
     * true -- 1 
     * false -- 0
     */
	public String changeBoolToStr(boolean bool) {
		if(bool) {
			return "1";
		}else {
			return "0";
		}
	}

}