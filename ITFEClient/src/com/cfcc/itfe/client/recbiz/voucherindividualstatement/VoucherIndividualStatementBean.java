package com.cfcc.itfe.client.recbiz.voucherindividualstatement;

import itferesourcepackage.VoucherIndividualStatementEnumFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositevoucherIndividualStatementOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ComboMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * 功能：每日业务对账单(广西)(个性化对账单)
 * @author hejianrong
 * @time   14-06-16 15:00:11
 * 子系统: RecBiz
 * 模块:voucherIndividualStatement
 * 组件:VoucherIndividualStatement
 */
public class VoucherIndividualStatementBean extends AbstractVoucherIndividualStatementBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherIndividualStatementBean.class);
    List<TvVoucherinfoDto> checkList;   
	private ITFELoginInfo loginInfo;
	private String stamp;
	private List stampList=null;
	//凭证类型
	private String voucherType=null;
    public VoucherIndividualStatementBean() {
      super();
      dto = new TvVoucherinfoDto();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      checkList=new ArrayList();
      stampList=new ArrayList();
      dto.setScheckdate(TimeFacade.getCurrentStringTime());
      dto.setScreatdate(TimeFacade.getCurrentStringTime());
      dto.setSorgcode(loginInfo.getSorgcode());
      pagingcontext = new PagingContext(this);
    }
    
    /**
	 * Direction: 生成凭证
	 * ename: voucherGenerator
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherGenerator(Object o){
    	if(StringUtils.isBlank(dto.getScheckdate())){
    		MessageDialog.openMessageDialog(null, "请输入对账日期！");
    		return "";
    	}    	
    	if(Integer.parseInt(dto.getScheckdate())>Integer.parseInt(TimeFacade.getCurrentStringTime())){
    		MessageDialog.openMessageDialog(null, "对账日期不能大于当前系统日期！");
    		return "";
    	}   	
    	int count=0;
    	try{
    		StringBuffer sbuf=new StringBuffer();
        	TvVoucherinfoDto voucherDto=new TvVoucherinfoDto();
        	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
        		List voucherMapperList = new VoucherIndividualStatementEnumFactory().getEnums(null);
            	for(Mapper mapper:(List<Mapper>)voucherMapperList){
            		voucherDto.setSvtcode(mapper.getUnderlyValue()+"");
            		
                	//判断是否已经生成凭证
                	sbuf.append(voucherIsRepeat(voucherDto));
            	}
        	}else{        		
        		voucherDto.setSvtcode(dto.getSvtcode());
        		if(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_4006)&&
        				!dto.getScheckdate().equals(getLastDayOfMonth(dto.getScheckdate()))){            		
        	    	MessageDialog.openMessageDialog(null, 
        	    			"对账日期不是该月最后一天，不能生成人民银行与代理银行集中支付对账明细表凭证！");
        			return null;            	    	
        		}
            	//判断是否已经生成凭证
            	sbuf.append(voucherIsRepeat(voucherDto));
        	}
        	if(sbuf.toString()!=null&&!sbuf.toString().equals("")){
        		if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "信息提示", sbuf.toString()+"\r\n确定继续生成吗？")){
            		return "";
            	}
        	}
        	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
        		List voucherMapperList = new VoucherIndividualStatementEnumFactory().getEnums(null);
            	for(Mapper mapper:(List<Mapper>)voucherMapperList){
            		voucherDto.setSvtcode(mapper.getUnderlyValue()+"");
            		if(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_4006)&&
            				!dto.getScheckdate().equals(getLastDayOfMonth(dto.getScheckdate())))            		
            	    	continue;
            		String result=voucherGererate(voucherDto);
            		if(result==null||result.equals("")){
            			continue;           			
            		}count+=Integer.parseInt(result);
            	}
        	}else{
        		voucherDto.setSvtcode(dto.getSvtcode());
        		String result=voucherGererate(voucherDto);
        		if(result!=null&&!result.equals("")){
        			count+=Integer.parseInt(result);
        		}
        	}
        	if(count==0){
        		MessageDialog.openMessageDialog(null, "没有该日期的凭证生成！");
        		if(StringUtils.isBlank(dto.getSvtcode())&&
        				!dto.getScheckdate().equals(getLastDayOfMonth(dto.getScheckdate())))            		
        	    	MessageDialog.openMessageDialog(null, 
        	    			"对账日期不是该月最后一天，不能生成人民银行与代理银行集中支付对账明细表凭证！");
        		return "";
        	}
        	MessageDialog.openMessageDialog(null, "对账凭证生成操作成功，成功条数为："+count+" ！");
        	if(StringUtils.isBlank(dto.getSvtcode())&&
    				!dto.getScheckdate().equals(getLastDayOfMonth(dto.getScheckdate())))            		
    	    	MessageDialog.openMessageDialog(null, 
    	    			"对账日期不是该月最后一天，不能生成人民银行与代理银行集中支付对账明细表凭证！");
    		refreshTable();   		
    	} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception(e.getMessage(),e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);						
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){			
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");			
		}catch(Exception e){
			log.error(e);			
			Exception e1=new Exception("生成凭证操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}		
        return super.voucherGenerator(o);
    }    
    
    public String voucherGererate(TvVoucherinfoDto voucherDto) throws ITFEBizException{
		List<TvVoucherinfoDto> list=getVoucherList(voucherDto);   
    	if(list==null||list.size()==0){
			return "";
		}
		List resultList=voucherLoadService.voucherGenerate(list);
		if(resultList!=null&&resultList.size()>0){
			return resultList.get(0)+"";
		}					
		return "";				
    }
    
    /**
     * 获取当月最后一天
     * @param day
     * @return
     */
    public String getLastDayOfMonth(String day){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(TimeFacade.parseDate(day));		
		return day.substring(0, 6)+calendar.getActualMaximum(Calendar.DAY_OF_MONTH);	
    }
    
    /**
     * 对帐凭证凭证判重
     * @return
     * @throws ITFEBizException
     */
	@SuppressWarnings("unchecked")
	private String voucherIsRepeat(TvVoucherinfoDto voucherDto) throws ITFEBizException{
		List<TvVoucherinfoDto> list=getVoucherList(voucherDto);   
    	if(list==null||list.size()==0){
			return "";
		}
    	String vtcodeType=voucherDto.getSvtcode();
    	//存放具体国库下的重复凭证
    	List<TvVoucherinfoDto> lists=new ArrayList<TvVoucherinfoDto>();
    	//遍历凭证列表
    	for (TvVoucherinfoDto tvVoucherinfoDto : list) {
    		TvVoucherinfoDto idto=new TvVoucherinfoDto();
			idto.setSorgcode(tvVoucherinfoDto.getSorgcode());
			idto.setStrecode(tvVoucherinfoDto.getStrecode());
			idto.setSvtcode(tvVoucherinfoDto.getSvtcode());
			idto.setScheckdate(dto.getScheckdate());
    		if(vtcodeType!=null&&!vtcodeType.equals(""))
    			if(vtcodeType.equals(MsgConstant.VOUCHER_NO_4005))
        			idto.setSpaybankcode(tvVoucherinfoDto.getSpaybankcode());
    			else if(vtcodeType.equals(MsgConstant.VOUCHER_NO_4006)){
    				idto.setSpaybankcode(tvVoucherinfoDto.getSpaybankcode());
    				idto.setShold1(tvVoucherinfoDto.getShold1());
    			}
    		List<TvVoucherinfoDto> voucherList = commonDataAccessService.findRsByDto(idto);
			if(voucherList!=null&&voucherList.size()>0){
				lists.add(tvVoucherinfoDto);
			}
		}
    	StringBuffer sbf=new StringBuffer();
    	if(lists!=null&&lists.size()>0){
    		for (TvVoucherinfoDto tvVoucherinfoDto : lists) {
    			sbf.append("国库为：")
        		.append(tvVoucherinfoDto.getStrecode())
        		.append(", 业务类型为：")
        		.append(getVtcodeTypeCmt(vtcodeType))
        		.append(", 对账日期为：")
        		.append(tvVoucherinfoDto.getScheckdate())
        		.append(" 对账凭证已生成,")
        		.append("\r\n");
			}
    	}
		return sbf.toString();
    	
    }
	
	public String getVtcodeTypeCmt(String vtcode){
    	String cmt="";
		if(vtcode.equals(MsgConstant.VOUCHER_NO_4004)){
			cmt="人民银行与财政部门集中支付业务对账单4004";
		}else if(vtcode.equals(MsgConstant.VOUCHER_NO_4005)){
			cmt="人民银行与代理银行集中支付业务对账单4005";
		}else if(vtcode.equals(MsgConstant.VOUCHER_NO_4006)){
			cmt="人民银行与代理银行集中支付对账明细表4006";
		}
		return cmt;
    	
    }
	
	public List<TvVoucherinfoDto> getVoucherList(TvVoucherinfoDto voucherDto) throws ITFEBizException{
    	List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();    	
			List<TsTreasuryDto> tList=new ArrayList<TsTreasuryDto>();
			TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			if(dto.getStrecode()==null||dto.getStrecode().equals("")){				
				tList=commonDataAccessService.findRsByDto(tDto);
			}else{
				tDto.setStrecode(dto.getStrecode());				
				tList.add(tDto);
			}
			if(tList==null||tList.size()==0)
				throw new ITFEBizException("国库参数未维护！",new Exception(""));							
			for(TsTreasuryDto tsDto:tList){
				TsConvertfinorgDto tsConvertfinorgDto=new TsConvertfinorgDto();
				tsConvertfinorgDto.setSorgcode(dto.getSorgcode());
				tsConvertfinorgDto.setStrecode(tsDto.getStrecode());
				List<TsConvertfinorgDto> tsConvertfinorgDtoList=commonDataAccessService.findRsByDto(tsConvertfinorgDto);
				if(tsConvertfinorgDtoList==null||tsConvertfinorgDtoList.size()==0)
					throw new ITFEBizException("国库代码："+tsDto.getStrecode()+" 对应的财政代码参数未维护！",new Exception(""));
				tsConvertfinorgDto=tsConvertfinorgDtoList.get(0);
				if(tsConvertfinorgDto.getSadmdivcode()==null||tsConvertfinorgDto.getSadmdivcode().equals(""))
					throw new ITFEBizException("财政代码："+tsConvertfinorgDto.getSfinorgcode()+" 对应的区划代码参数未维护！",new Exception(""));
		    	TvVoucherinfoDto vDto=(TvVoucherinfoDto) dto.clone();
		    	if(StringUtils.isBlank(vDto.getSvtcode()))
		    		vDto.setSvtcode(voucherDto.getSvtcode());
		    	if(StringUtils.isBlank(vDto.getStrecode()))
		    		vDto.setStrecode(tsDto.getStrecode());				
				vDto.setSadmdivcode(tsConvertfinorgDtoList.get(0).getSadmdivcode());				
				list.add(vDto);				
			}	
		return list;
    }
	
    /**
     * 查询国库代码
     * @return
     * @throws ITFEBizException
     */
    private List findStrecode() throws ITFEBizException{
    	TsTreasuryDto dto=new TsTreasuryDto();
		dto.setSorgcode(loginInfo.getSorgcode());
		return commonDataAccessService.findRsByDto(dto);
    }
    
    /**
     * 获取OCX控件url
     * @return
     */
    public String getOcxVoucherServerURL(){
    	try {
    		return voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);	
			MessageDialog.openErrorDialog(Display.
					getDefault().getActiveShell(),new Exception("获取OCX控件URL地址操作出现异常！",e));
		}return null;
    }
    
    /**
     * 获取签章服务地址
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取OCX控件签章服务URL地址操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
	/**
	 * Direction: 查询 
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String search(Object o){   	
    	refreshTable();
        return super.search(o);
    }
    
    /**
     * 查询凭证信息
     */
    private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先生成凭证！");
		}
		pagingcontext.setPage(pageResponse);
	}
    
    /**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
    	 if(checkList==null||checkList.size()==0){
         	checkList = new ArrayList();
         	checkList.addAll(pagingcontext.getPage().getData());
         }
         else
         	checkList.clear();
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
         return super.selectAll(o);
    }
    
    /**
	 * Direction: 签章
	 * ename: transferCompletedStamp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){    	
    	boolean ocxflag=false;
    	List<TvVoucherinfoDto> checkList=new ArrayList<TvVoucherinfoDto>();
    	String stamp=null;
    	TvVoucherinfoDto dto=new TvVoucherinfoDto();    
    	if(o instanceof List){
			List  ocxStampList=(List) o;
    		String stampname=(String) ocxStampList.get(0);       		
    		dto=(TvVoucherinfoDto) ocxStampList.get(1);    		
    		TsStamppositionDto stampPostionDto=new TsStamppositionDto();
    		stampPostionDto.setSorgcode(dto.getSorgcode());
    		stampPostionDto.setStrecode(dto.getStrecode());
    		stampPostionDto.setSvtcode(dto.getSvtcode());
    		stampPostionDto.setSstampname(stampname);
				try {
					stampPostionDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampPostionDto).get(0);
				} catch (ITFEBizException e) {
					log.error(e); 
					Exception e1=new Exception("签章出现异常！",e);			
					MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
					return "";
				}			
			stamp=stampPostionDto.getSstamptype();
			this.stamp = stampPostionDto.getSstamptype();
			checkList.add(dto);
			ocxflag=true;			
    	}
    	if(!ocxflag){
    		stamp=this.stamp;
    		checkList=this.checkList;
    		dto=this.dto;
    	}
    	int count=0;
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择签章类型！");
			return null;
		}
    	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要签章的记录！");
    		return "";
    	}
    	
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行签章操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim()))){
	    			MessageDialog.openMessageDialog(null, "请选择凭证状态为处理成功的记录！");	    			
	        		return "";
	    		}
	    	}		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");    			
    			return "";       		
    		}    		
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}       			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}        			
        		}
    		}    		
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "当前用户无  \""+stampDto.getSstampname()+"\"  签章权限！");    			
    			return "";
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
    		String stampuser="";
    		String stampid="";
    		for(TvVoucherinfoDto vDto:checkList){
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			if(stampid!=null&&!stampid.equals("")){
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 已签  \""+stampDto.getSstampname()+"\" ，同一凭证不能重复签章！");			    			
			    			return "";
						}
					}
    			}
    			if(!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)&&!stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
    				stampuser=vDto.getSstampuser();
    				if(stampuser!=null&&!stampuser.equals("")){
						String[] stampusers=stampuser.split(",");
						for(int i=0;i<stampusers.length;i++){							
							if(usercode.equals(stampusers[i])){
								TsStamppositionDto tstampDto=new TsStamppositionDto();
								tstampDto.setSorgcode(loginInfo.getSorgcode());
								tstampDto.setSvtcode(dto.getSvtcode());
								String[]  stampids=vDto.getSstampid().split(",");
								for(int j=0;j<stampids.length;j++){
									if(!stampids[i].equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
										tstampDto.setSstamptype(stampids[i]);
										break;
									}
								}
								tstampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(tstampDto).get(0);
								MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 当前用户已签  \""+tstampDto.getSstampname()+"\" ，同一用户只能签一次私章，请选择其他用户！");    			    			
    			    			return "";
							}
						}
					
    				}
        		}
    		}    		
    		Iterator it=map.keySet().iterator();    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		String strecode="";
    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中不存在！");
    				
    				return "";
    			}    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码："+strecode+" "+stampDto.getSstampname()+" 参数未维护！ ");
    				
    				return "";
    			}    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			
    			}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户  "+stampDto.getSstampname()+"  证书ID参数未维护！ ");        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户   "+stampDto.getSstampname()+"  印章ID参数未维护！ ");        				
        				return "";
        			}    		    			
    			}
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositevoucherIndividualStatementOcx.sortStringArray(seqs);    							
    							String temp="";
    							for(int i=seqs.length-1;i>-1;i--){
    								if(Integer.parseInt(seqs[i])<Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			        			for(TsStamppositionDto tstampDto:tsList){
    			        				err=tstampDto.getSstampname()+" , "+err;
    			        			}
    			        			err=err.substring(0,err.lastIndexOf(","));
    			        			if(stampid==null||stampid.equals("")){
        								MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+vDto.getSvoucherno()+" \""+stampDto.getSstampname()+"\"签章前请先 \""+err+"\"签章！");
            			    			
            			    			return "";
        							
    			        			}else{
    			        				err="";
    			        				String[] stampids=stampid.split(",");
			        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
			        					for(int j=0;j<tsList.size();j++){
			        						for(int i=0;i<stampids.length;i++){
    			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
    			        							tsList1.add(tsList.get(j));
    			        						}
    			        					}
		        						}
			        					tsList.removeAll(tsList1);
			        					if(tsList.size()>0){
			        						for(TsStamppositionDto tstampDto:tsList){
		    			        				err=tstampDto.getSstampname()+" , "+err;
		    			        			}
			        						err=err.substring(0,err.lastIndexOf(","));
			        						MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"签章前请先 \""+err+"\"签章！");
	            			    			
	            			    			return "";
			        					}
    			        			}
    			        		
    							}
    						}
						}
						if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_OFFICIALSTAMP)){								
								sinList.add(ActiveXCompositevoucherIndividualStatementOcx.getVoucherStamp(vDto, tDto.getScertid(), sDto.getSstampposition(), tDto.getSstampid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP)){								
								sinList.add(ActiveXCompositevoucherIndividualStatementOcx.getVoucherStamp(vDto, tDto.getSrotarycertid(), sDto.getSstampposition(), tDto.getSrotaryid()));   	    												
							}
						}else{
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositevoucherIndividualStatementOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
							}
						}
    					vList.add(sinList);
    				}
    			}
    			list.add(uDto);
    			list.add(tDto);
    			list.add(sDto);
    			list.add(sList.size());
    			list.add(vList);    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStamp(lists);
    		if(ocxflag){
    			
    			return count+"";
    		}
    		MessageDialog.openMessageDialog(null, "凭证签章   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);    		
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("签章操作出现异常！",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}		
        return super.voucherStamp(o);
    }
    
    /**
	 * Direction:签章撤销
	 * ename: transferCompletedStampCancle
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStampCancle(Object o){
    	int count=0;        	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要签章撤销的记录！");
    		return "";
    	}
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择签章类型！");
			return null;
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行签章撤销操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus()))&&!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()))){
	    			MessageDialog.openMessageDialog(null, "请选择凭证状态为处理成功、签章成功的记录！");
	    			
	        		return "";
	    		}
	    	}
		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");
    			
    			return "";
        		
    		}
   
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}
        			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}
        			
        		}
    		}
    		boolean managerPermission=false;
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "当前用户无  \""+stampDto.getSstampname()+"\"  签章权限！，可通过主管授权撤销签章");
				String msg = "需要主管授权才能才能撤销签章！";
				if(!AdminConfirmDialogFacade.open("B_265", "国库往来票据生成", "授权用户"+loginInfo.getSuserName()+"撤销签章", msg)){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				}else{
					managerPermission=true;
				}
				
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
    		String stampuser="";
    		String stampid="";    		
    		for(TvVoucherinfoDto vDto:checkList){
    			usercode=uDto.getSusercode();
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			int j=-1;
    			if(stampid==null||stampid.equals("")){
    				flag=false;
    			}else{
    				flag=false;
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							flag=true;
							j=i;
							break;
							
						}
					}
    			
    			}if(flag==false){
					MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 未签  \""+stampDto.getSstampname()+"\" ！");	    			
	    			return "";
				}
    			if(managerPermission==false){
    				if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
						usercode=usercode+"!";
					}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
						usercode=usercode+"#";
					}
    				String stampuserboolean=vDto.getSstampuser().split(",")[j];
    				if(!stampuserboolean.equals(usercode)){
    					MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+"   \""+stampDto.getSstampname()+"\" 不是当前用户所签！可通过主管授权撤销签章");
    					String msg = "需要主管授权才能才能撤销签章！";
    					if(!AdminConfirmDialogFacade.open("B_265", "国库往来票据生成", "授权用户"+loginInfo.getSuserName()+"撤销签章", msg)){
    						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    						return null;
    					}else{
    						managerPermission=true;
    					}    				
    				}   				
    			}
    		}
    		
    		Iterator it=map.keySet().iterator();
    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		
    		String strecode="";
    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList<TvVoucherinfoDto>();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中不存在！");
    				
    				return "";
    			}
    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码："+strecode+" "+stampDto.getSstampname()+" 参数未维护！ ");
    				
    				return "";
    			}
    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			
    			}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户  "+stampDto.getSstampname()+"  证书ID参数未维护！ ");
        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户   "+stampDto.getSstampname()+"  印章ID参数未维护！ ");
        				
        				return "";
        			}
    		    			
    			}
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){
    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositevoucherIndividualStatementOcx.sortStringArray(seqs);
    							
    							String temp="";
    							for(int i=0;i<seqs.length;i++){
    								if(Integer.parseInt(seqs[i])>Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			      
			        				String[] stampids=stampid.split(",");
		        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
		        					for(int j=0;j<tsList.size();j++){
		        						for(int i=0;i<stampids.length;i++){
			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
			        							err=tsList.get(j).getSstampname()+" "+err;
			        						}
			        					}
	        						}
		        					if(!err.trim().equals("")){
		        						for(TsStamppositionDto tstampDto:tsList){
	    			        				err=tstampDto.getSstampname()+" , "+err;
	    			        			}
		        						err=err.substring(0,err.lastIndexOf(","));
		        						MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"撤销签章前请先撤销 \""+err+"\"签章！");
            			    			
            			    			return "";
		        					}    			        		
    							}
    						}
						}
						int j=-1;
						String[] stampids=stampid.split(",");
						for(int i=0;i<stampids.length;i++){
							if(stamp.equals(stampids[i])){								
								j=i;
								break;
								
							}
						}
						TsUsersDto userDto=new TsUsersDto();
						userDto.setSorgcode(loginInfo.getSorgcode());
						String user=vDto.getSstampuser().split(",")[j];
						
						userDto.setSusercode(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)?user.substring(0, (user.length()-1)):(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)?user.substring(0, (user.length()-1)):user));
						userDto=(TsUsersDto) commonDataAccessService.findRsByDto(userDto).get(0);
						sinList.add(userDto);
						vList.add(sinList);
    				}
    			}
    			
    			list.add(tDto);
    			list.add(sDto);
    			
    			list.add(vList);
    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStampCancle(lists);
    		MessageDialog.openMessageDialog(null, "凭证撤销签章   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("签章操作撤销出现异常！",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}
        return super.voucherStampCancle(o);
    }
    
    /**
	 * Direction: 发送电子凭证
	 * ename: sendReturnVoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要发送电子凭证的记录！");
    		return "";
    	}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行发送电子凭证操作吗？")) 
			return "";		
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()))){
				MessageDialog.openMessageDialog(null, "请选择凭证状态为 \"处理成功\" 的记录！");
        		return "";
			}
	    }
    	//对选中的列表进行操作时，重新查询数据库取得最新数据状态
    	List<TvVoucherinfoDto> checkList=new ArrayList<TvVoucherinfoDto>();
    	for(TvVoucherinfoDto vDto:this.checkList){
    		TvVoucherinfoDto newvDto = new TvVoucherinfoDto();
    		try {
				newvDto = getDto(vDto);
			} catch (ITFEBizException e) {
				Exception e1=new Exception("重新查询数据出现错误！",e);			
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			}
			checkList.add(newvDto);
    	}
    	try {   		
    		 int count=voucherLoadService.voucherReturnSuccess(checkList);
    		 MessageDialog.openMessageDialog(null, "发送电子凭证   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		 refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);		
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
			log.error(e);		
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),new Exception("发送电子凭证库操作出现异常！",e));
		}return super.voucherSend(o);         
    }

	/**
	 * Direction: 凭证查看
	 * ename: voucherView
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
    		return "";
    	}
    	try{
    		ActiveXCompositevoucherIndividualStatementOcx.init(0);
    		return super.voucherView(o);
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
    		log.error(e);	
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),new Exception("凭证查看异常！",e));			
    	}return "";        
    }
    
    /**
     * 刷新签章类型
     * 
     */
    public void refreshStampType(){
    	TsStamppositionDto tsDto=new TsStamppositionDto();
        tsDto.setSorgcode(dto.getSorgcode());
      	tsDto.setSvtcode(dto.getSvtcode());
      	tsDto.setStrecode(dto.getStrecode());
      	Set set=new HashSet();
      	TsStamppositionDto sDto=new TsStamppositionDto();
      	List<TsStamppositionDto> tList=null;
      	stampList=new ArrayList();
      	List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
      	try{
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		for(TsStamppositionDto sdto:tList){
          			set.add(sdto.getSstamptype());
          		}
          		for(String stamptype:(Set<String>)set){
          			sDto=new TsStamppositionDto();
          			sDto.setSorgcode(dto.getSorgcode());
          			sDto.setSvtcode(dto.getSvtcode());
          			sDto.setSstamptype(stamptype);
          			sDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
          			tsList.add(sDto);
          		}
          		stampList.addAll(tsList);
          		if(stampList.size()==1){
          			stamp = ((TsStamppositionDto)stampList.get(0)).getSstamptype();     
          		}
          	}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("凭证刷新操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);			   			
      	}
    }
    
    /**
	 * Direction: 查询凭证打印次数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryVoucherPrintCount(TvVoucherinfoDto vDto){
    	try {
    		return voucherLoadService.queryVoucherPrintCount(vDto);			
		} catch (ITFEBizException e) {			
			log.error(e);
			return "查询异常";
		}
    }
    
    /**
	 * Direction: 查询凭证联数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public int queryVoucherJOintCount(TvVoucherinfoDto vDto){
    	TsVouchercommitautoDto tDto=new TsVouchercommitautoDto();
    	tDto.setSorgcode(vDto.getSorgcode());
    	tDto.setStrecode(vDto.getStrecode());
    	tDto.setSvtcode(vDto.getSvtcode());
    	try {
			List<TsVouchercommitautoDto> list= (List) commonDataAccessService.findRsByDto(tDto);
			if(list==null||list.size()==0)
				return -1;
			tDto=list.get(0);
			if(StringUtils.isBlank(tDto.getSjointcount()+"")){
				return -1;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		}catch(Exception e){
			log.error(e);
			return -1;
		}return tDto.getSjointcount();
    }
     
    /**
     * 查找凭证对应的凭证报文
     * @param vDto
     * @return
     * @throws ITFEBizException
     */
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{    	
    	return voucherLoadService.voucherStampXml(vDto);
    }
    
    /**
     * 刷新页面
     */
    public void refreshTable(){
    	init();
		checkList.clear();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
    }
       
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	StringBuffer wheresql=new StringBuffer();
    	wheresql.append(" 1=1 ");
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
        	List voucherList = new VoucherIndividualStatementEnumFactory().getEnums(null);
        	wheresql.append("AND ( ");
        	for(int i=0;i<voucherList.size();i++){
        		Mapper mapper=(Mapper)voucherList.get(i);
        		if(i==voucherList.size()-1){
            		wheresql.append(" S_VTCODE = '"+ mapper.getUnderlyValue()+"' )");
            		break;
        		}            		
        		wheresql.append(" S_VTCODE = '"+ mapper.getUnderlyValue()+"' OR ");
        	}       	
    	}
    	try {
    		page =  commonDataAccessService.findRsByDtoPaging(dto, pageRequest, wheresql.toString(), " TS_SYSUPDATE desc");   	
    		return page;    				
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");			
		}catch (Throwable e) {
			log.error(e);
			Exception e1=new Exception("凭证查询出现异常！",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			
		}
		return super.retrieve(pageRequest);
	}
    
    public List<Mapper> getStampEnums(TvVoucherinfoDto vDto){
		List<Mapper> maplist = new ArrayList<Mapper>();
		
		List<TsStamppositionDto> enumList = new ArrayList<TsStamppositionDto>();
		TsStamppositionDto tDto=new TsStamppositionDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			enumList =commonDataAccessService.findRsByDto(tDto);
			if(enumList!=null&&enumList.size()>0){
				for (TsStamppositionDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSstamptype(), emuDto.getSstampname());
					maplist.add(map);
				}
			}
			
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取凭证签章列表出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}		
		return maplist;
	}
    
    /**
     * 通过主键查找凭证
     * @param dto
     * @return
     * @throws ITFEBizException
     */
    public TvVoucherinfoDto getDto(TvVoucherinfoDto dto) throws ITFEBizException{
    	TvVoucherinfoPK pk=new TvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (TvVoucherinfoDto) commonDataAccessService.find(pk);
    }
    
    /**
     * 根据组织机构代码、国库代码查找相应的区划代码
     * @return
     */
    private Boolean findAdmDivCodeByStrecode(){
    	TsConvertfinorgDto finorgDto=new TsConvertfinorgDto();
    	finorgDto.setSorgcode(dto.getSorgcode());
    	finorgDto.setStrecode(dto.getStrecode());
    	try {
			List list=commonDataAccessService.findRsByDto(finorgDto);
			if(list==null||list.size()==0){
				MessageDialog.openMessageDialog(null, "国库代码"+dto.getStrecode()+"对应的财政代码参数未维护！");
				return true;
			}				
			finorgDto=(TsConvertfinorgDto) list.get(0);
			if(StringUtils.isBlank(finorgDto.getSadmdivcode()))
				MessageDialog.openMessageDialog(null, "财政代码"+finorgDto.getSfinorgcode()+"对应的区划代码参数未维护！");
			else{
				dto.setSadmdivcode(finorgDto.getSadmdivcode());
				dto.setShold3(finorgDto.getSfinorgcode());
				return false;
			}			
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询财政代码参数异常！");			
		}return true;   	
    }
   
    public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		this.dto.setSvtcode(voucherType);
		//根据不同的凭证显示不同的控件
		setContentVisible();
		pagingcontext.setPage(new PageResponse());
		refreshStampType();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	
	
	@SuppressWarnings("unchecked")
	public void setContentVisible(){
		List<String> visContentAreaName=new ArrayList<String>();
		visContentAreaName.add("信息查询");
		visContentAreaName.add("凭证查询一览表");
	    List<ContainerMetaData> containerMetaData=MVCUtils.setContentAreasToVisible(editor, visContentAreaName);
	    for (ContainerMetaData metadata : containerMetaData) {
	    	List controls = metadata.controlMetadatas;
			if ("信息查询".equals(metadata.name)) {
				for (int i = 0; i < controls.size(); i++) {
					if(voucherType.equals(MsgConstant.VOUCHER_NO_4004)){
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textmetadata = (TextMetaData) controls.get(i);
							if (textmetadata.caption.equals("银行代码")) {
								textmetadata.visible=false;
							}
						}
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combo = (ComboMetaData) controls.get(i);
							if (combo.caption.equals("支付方式")) {
								combo.visible=false;
							}
						}
					}else if(voucherType.equals(MsgConstant.VOUCHER_NO_4005)){
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textmetadata = (TextMetaData) controls.get(i);
							if (textmetadata.caption.equals("银行代码")) {
								textmetadata.visible=true;
							}
						}
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combo = (ComboMetaData) controls.get(i);
							if (combo.caption.equals("支付方式")) {
								combo.visible=false;
							}
						}
					}else if(voucherType.equals(MsgConstant.VOUCHER_NO_4006)){
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textmetadata = (TextMetaData) controls.get(i);
							if (textmetadata.caption.equals("银行代码")) {
								textmetadata.visible=true;
							}
						}
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combo = (ComboMetaData) controls.get(i);
							if (combo.caption.equals("支付方式")) {
								combo.visible=true;
							}
						}
					}
				}
			}else if ("凭证查询一览表".equals(metadata.name)) {
				TableMetaData tablemd = (TableMetaData) controls.get(0);
				for (int i = 0; i < tablemd.columnList.size(); i++) {
					if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
						ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
						if(voucherType.equals(MsgConstant.VOUCHER_NO_4004)){
							if (coletadata.title.equals("银行代码")) {
								coletadata.visible = false;
							}else if(coletadata.title.equals("支付方式")){
								coletadata.visible = false;
							}
						}else if(voucherType.equals(MsgConstant.VOUCHER_NO_4005)){
							if (coletadata.title.equals("银行代码")) {
								coletadata.visible = true;
							}else if(coletadata.title.equals("支付方式")){
								coletadata.visible = false;
							}
						}else if(voucherType.equals(MsgConstant.VOUCHER_NO_4006)){
							if (coletadata.title.equals("银行代码")) {
								coletadata.visible = true;
							}else if(coletadata.title.equals("支付方式")){
								coletadata.visible = true;
							}
						}
					}
				}
			}
		}
	    MVCUtils.reopenCurrentComposite(editor);
	}
    
	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public List getStampList() {
		return stampList;
	}

	public void setStampList(List stampList) {
		this.stampList = stampList;
	}

	public String getVoucherType() {
		return voucherType;
	}
}