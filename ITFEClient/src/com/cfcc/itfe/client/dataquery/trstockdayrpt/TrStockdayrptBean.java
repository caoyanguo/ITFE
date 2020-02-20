package com.cfcc.itfe.client.dataquery.trstockdayrpt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-24 10:44:47
 * 子系统: DataQuery
 * 模块:TrStockdayrpt
 * 组件:TrStockdayrpt
 */
public class TrStockdayrptBean extends AbstractTrStockdayrptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TrStockdayrptBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    List<TsConvertfinorgDto> finddtolist=null;
	
	private Boolean flag=true; 
	
	public TrStockdayrptBean() {
		super();
		dto = new TrStockdayrptDto();
		String dateString = TimeFacade.getCurrentStringTime();
//		dto.setSrptdate(dateString);		
		pagingcontext = new PagingContext(this);
		startDate = dateString;
		endDate = dateString;
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 库存日报表查询结果 messages:
	 */
	public String query(Object o) {
		if(!startDate.toString().trim().matches("[0-9]{8}")){
			MessageDialog.openMessageDialog(null, "日期格式不对,起始日期必须为8位数字！");
			return "";
		}
		if(!endDate.toString().trim().matches("[0-9]{8}")){
			MessageDialog.openMessageDialog(null, "日期格式不对,终止日期必须为8位数字！");
			return "";
		}

		// 返回页面
		String returnpage = null;
		
		//中心机构代码
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//判断当前登录查询国库代码、财政代码是否在核算主体中
			TsConvertfinorgDto finddto=new TsConvertfinorgDto();
			
			finddto.setSorgcode(loginfo.getSorgcode()); //核算主体代码
			if(dto.getSorgcode()!=null && !"".equals(dto.getSorgcode())){
				finddto.setSfinorgcode(dto.getSorgcode()); //财政代码
			}
			if(dto.getStrecode()!=null && !"".equals(dto.getStrecode())){
				finddto.setStrecode(dto.getStrecode()); //国库主体代码
			}
			
			try {
				finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,"1");
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
			if(null==finddtolist || finddtolist.size() == 0){
				MessageDialog.openMessageDialog(null, " 权限不足！请填写正确的财政代码或者国库代码！");
				return null;
			}
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			returnpage = "库存日报表查询条件";
		} else {
			returnpage = super.query(o);
		}
		return returnpage;
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 库存日报表查询条件 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}
	
	
	/**
	 * Direction: 导出数据
	 * ename: exportTable
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportTable(Object o){
    	HtrStockdayrptDto hdto = new HtrStockdayrptDto();
		hdto.setSorgcode(dto.getSorgcode());
		hdto.setStrecode(dto.getStrecode());
		hdto.setSrptdate(dto.getSrptdate());
		String where="";
		//财政代码为空，国库代码为空，查全局
		if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSorgcode()==null || "".equals(dto.getSorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
			
			where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
			for(int i = 1 ;i<finddtolist.size();i++){
				where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
			}
			where+=" ) ";
		}
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+"3128财政库存日报("+startDate+"-"+endDate+").txt";
		List<TrStockdayrptDto> templist =null;
		try {
			templist = commonDataAccessService.findRsByDto(dto, where);
			templist.addAll(commonDataAccessService.findRsByDto(hdto, where));
		} catch (ITFEBizException e1) {
			MessageDialog.openMessageDialog(null, "查询数据出错！");
			return null;
		}
		
		if (templist == null || templist.size() == 0) {
			MessageDialog.openMessageDialog(null, "无法查询出数据，请重新查询！");
			return null;
		}
		try {
			exportTableForWhere(templist, fileName, ",");
			MessageDialog.openMessageDialog(null, "数据导出成功\n"+fileName);
		} catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}

        return "";
    }
    /**
     * 按条件导出数据
     * @param list
     * @param filename
     * @return
     * @throws FileOperateException 
     */
    public String exportTableForWhere(List <TrStockdayrptDto> templist ,String fileName,	String splitSign ) throws FileOperateException{
    	String sql="财政机关代码,国库代码,申请日期,账户代码,账户名称,账户日期,上日余额 ,本日收入,本日支出,本日余额";
		StringBuffer filebuf = new StringBuffer(sql+"\r\n");
		TrStockdayrptDto _dto = null;
		HtrStockdayrptDto hdto = null;
        for (IDto idto:templist) {
        	if(idto instanceof TrStockdayrptDto)
        	{
        		_dto = (TrStockdayrptDto)idto;
	        	filebuf.append(_dto.getSorgcode()+",");
	        	filebuf.append(_dto.getStrecode()+",");
	        	filebuf.append(_dto.getSrptdate()+",");
	        	filebuf.append(_dto.getSaccno()+",");
	        	filebuf.append(_dto.getSaccname()+",");
	        	filebuf.append(_dto.getSaccdate()+",");	        	
	        	filebuf.append(_dto.getNmoneyyesterday()+","); 
	        	filebuf.append(_dto.getNmoneyin()+","); 
	        	filebuf.append(_dto.getNmoneyout()+",");
	        	filebuf.append(_dto.getNmoneytoday());
	        	filebuf.append("\r\n");
        	}
        	else if(idto instanceof HtrStockdayrptDto)
        	{
        		hdto = (HtrStockdayrptDto)idto;
        		filebuf.append(hdto.getSorgcode()+",");
            	filebuf.append(hdto.getStrecode()+",");
            	filebuf.append(hdto.getSrptdate()+",");
            	filebuf.append(hdto.getSaccno()+",");
            	filebuf.append(hdto.getSaccname()+",");
            	filebuf.append(hdto.getSaccdate()+",");
            	filebuf.append(hdto.getSrptdate()+",");
            	filebuf.append(hdto.getNmoneyyesterday()+","); 
            	filebuf.append(hdto.getNmoneyin()+","); 
            	filebuf.append(hdto.getNmoneyout()+",");
            	filebuf.append(hdto.getNmoneytoday());
            	filebuf.append("\r\n");
        	}
		}
        filebuf.delete(filebuf.length()-2, filebuf.length());
    	FileUtil.getInstance().writeFile(fileName, filebuf.toString());
		return "";
    	
    	
    }
	
	/**
	 * Direction: 导出库存txt
	 * ename: exporttxt
	 * 引用方法: 
	 * viewers: 库存日报表查询条件
	 * messages: 
	 */
    public String exporttxt(Object o){
    	
    	//查询用dto
    	TrStockdayrptDto fdto = new TrStockdayrptDto();
    	HtrStockdayrptDto hfdto = new HtrStockdayrptDto();
    	
    	String dirsep = File.separator; // 取得系统分割符
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	
		//中心机构代码
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		TsConvertfinorgDto finddto=new TsConvertfinorgDto();
		//非中心机构
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//判断当前登录查询国库代码、财政代码是否在国库主体中
			finddto.setSorgcode(loginfo.getSorgcode()); //核算主体代码
		}
		
		try {
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,"1");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if(null==finddtolist || finddtolist.size() == 0){
			MessageDialog.openMessageDialog(null, " 权限不足！请填写正确的财政代码或者国库代码！");
			return null;
		}
		
		String rtmsg ="";
		
		try {
			List listFile = new ArrayList();
			String copyFilename=dto.getSrptdate()+"_kucun_"+finddto.getSorgcode()+"_"+(new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()))+".txt";
			for(TsConvertfinorgDto idto :finddtolist){
				
				//只出市库和县区库
//				if(idto.getStrelevel().equals("3") || idto.getStrelevel().equals("4")){
					
					List list=null;
					String strdate=(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis()));
					
					
					//国库代码
					fdto.setStrecode(idto.getStrecode());
					//报表日期
					fdto.setSrptdate(dto.getSrptdate());
					
					//国库代码
					hfdto.setStrecode(idto.getStrecode());
					//报表日期
					hfdto.setSrptdate(dto.getSrptdate());

//					if(TimeFacade.getCurrentStringTime().equals(dto.getSrptdate())){
						list =commonDataAccessService.findRsByDto(fdto);
//					}
//					else{
//						list =commonDataAccessService.findRsByDto(hfdto);
//					}
					
					if (list != null && list.size() > 0) {
						listFile.addAll(list);
					} 
//				}
			}
			if(listFile!=null&&listFile.size()>0){
				/**
				 * 文件生成
				 */
				filecreat(filepath+copyFilename,listFile,dto.getSrptdate(),dto.getSrptdate(),null);
				rtmsg +=filepath+copyFilename+"; \r\n";
			}
			
		
		} catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		
		MessageDialog.openMessageDialog(null, " 文件已生成！已保存到：" + rtmsg);
		if(flag) editor.fireModelChanged();
		
        return super.exporttxt(o);
    }
    
    private void filecreat(String filepathname,List list,String srptdate,String strdate,TsConvertfinorgDto idto) throws FileOperateException{
    	//生成文件
    	try {
    		File file = new File(filepathname);
    		File dir = new File(file.getParent());
    		if (!dir.exists()) {
    			dir.mkdirs();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		
		StringBuffer sb = new StringBuffer("");
		
		sb.append("czjgdm,gkdm,rq,zhdm,zhmc,zhrq,srye,brsr,brzc,brye \r\n");
		List<TrStockdayrptDto> dlist=(List<TrStockdayrptDto>)list;
		
		for(TrStockdayrptDto trdto : dlist){
			sb.append(trdto.getSorgcode()+","); //skgkdm
			sb.append(trdto.getStrecode()+","); //mdgkdm
			sb.append(trdto.getSrptdate()+","); //ssgkdm
			sb.append(trdto.getSaccno()+","); //ysjc
			sb.append(trdto.getSaccname()+","); //jgdm
			sb.append(trdto.getSaccdate()+","); //kmdm
			sb.append(trdto.getSrptdate()+","); //zwrq
			sb.append(trdto.getNmoneyyesterday()+","); //yszl
			sb.append(trdto.getNmoneyin()+","); //rlj
			sb.append(trdto.getNmoneyout()+","); //ylj
			sb.append(trdto.getNmoneytoday()); //nlj
			sb.append(" \r\n");
		}
		
	/*	
		StringBuffer sb = new StringBuffer("");
		sb.append("                                  "+"财政库存日报 \r\n");
		sb.append("                                  "+"编制日期："+dto.getSrptdate().substring(0, 4) + "年"+ dto.getSrptdate().substring(4, 6) + "月"+ dto.getSrptdate().substring(6, 8) + "日  \r\n");
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
//		if(TimeFacade.getCurrentStringTime().equals(srptdate)){
			List<TrStockdayrptDto> dlist=(List<TrStockdayrptDto>)list;
			
			int maxSaccno=0;
			int maxSaccname=0;
			int maxNmoneyyesterday=0;
			int maxNmoneyin=0;
			int maxNmoneyout=0;
			int maxNmoneytoday=0;
			
			for(TrStockdayrptDto trdto:dlist){
				if(trdto.getSaccno().trim().getBytes().length>maxSaccno){
					maxSaccno=trdto.getSaccno().trim().getBytes().length;
				}
				if(trdto.getSaccname().trim().getBytes().length>maxSaccname){
					maxSaccname=trdto.getSaccname().trim().getBytes().length;
				}
				if(trdto.getNmoneyyesterday().toString().getBytes().length>maxNmoneyyesterday){
					maxNmoneyyesterday=trdto.getNmoneyyesterday().toString().getBytes().length;
				}
				if(trdto.getNmoneyin().toString().getBytes().length>maxNmoneyin){
					maxNmoneyin=trdto.getNmoneyin().toString().getBytes().length;
				}
				if(trdto.getNmoneyout().toString().getBytes().length>maxNmoneyout){
					maxNmoneyout=trdto.getNmoneyout().toString().getBytes().length;
				}
				if(trdto.getNmoneytoday().toString().getBytes().length>maxNmoneytoday){
					maxNmoneytoday=trdto.getNmoneytoday().toString().getBytes().length;
				}
			}
			maxSaccno=maxSaccno+10;
			maxSaccname=maxSaccname+10;
			maxNmoneyyesterday=maxNmoneyyesterday+10;
			maxNmoneyin=maxNmoneyin+10;
			maxNmoneyout=maxNmoneyout+10;
			maxNmoneytoday=maxNmoneytoday+10;
			
			sb.append("账号/科目"+getspaceforsum(maxSaccno-"账号/科目".getBytes().length));
			sb.append("帐户名称"+getspaceforsum(maxSaccname-"帐户名称".getBytes().length));
			sb.append("上日余额 "+getspaceforsum(maxNmoneyyesterday-"上日余额 ".getBytes().length));
			sb.append("本日发生额借方"+getspaceforsum(maxNmoneyin-"本日发生额借方".getBytes().length));
			sb.append("本日发生额贷方"+getspaceforsum(maxNmoneyout-"本日发生额贷方".getBytes().length));
			sb.append("本日余额");
			sb.append("  \r\n");
			
			for(TrStockdayrptDto trdto : dlist){
				sb.append(trdto.getSaccno()+getspaceforsum(maxSaccno-trdto.getSaccno().trim().getBytes().length)); //账号/科目
				sb.append(trdto.getSaccname()+getspaceforsum(maxSaccname-trdto.getSaccname().trim().getBytes().length)); //帐户名称
				sb.append(trdto.getNmoneyyesterday().toEngineeringString()+getspaceforsum(maxNmoneyyesterday-trdto.getNmoneyyesterday().toString().getBytes().length)); //上日余额
				sb.append(trdto.getNmoneyin().toPlainString()+getspaceforsum(maxNmoneyin-trdto.getNmoneyin().toString().getBytes().length)); //本日发生额借方
				sb.append(trdto.getNmoneyout().toPlainString()+getspaceforsum(maxNmoneyout-trdto.getNmoneyout().toString().getBytes().length)); //本日发生额贷方
				sb.append(trdto.getNmoneytoday().toPlainString()); //本日余额
				sb.append("  \r\n");
			}
//		}
//		else{
//			List<HtrStockdayrptDto> hlist=(List<HtrStockdayrptDto>)list;
//			for(HtrStockdayrptDto trdto : hlist){
//				sb.append(trdto.getSaccno()+" , "); //账号/科目
//				sb.append(trdto.getSaccname()+" , "); //帐户名称
//				sb.append(trdto.getNmoneyyesterday()+" , "); //上日余额
//				sb.append(trdto.getNmoneyin()+" , "); //本日发生额借方
//				sb.append(trdto.getNmoneyout()+" , "); //本日发生额贷方
//				sb.append(trdto.getNmoneytoday()); //本日余额
//				sb.append("  \r\n");
//			}
//		}
		
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
		//yyyyMMddhhmmssSSS
		String strfomatdate=strdate.substring(0, 4) + "年"+ strdate.substring(4, 6) + "月"+ strdate.substring(6, 8) + "日 ";
		sb.append("生成人："+loginfo.getSuserName()+"                  生成日期："+strfomatdate+" \r\n");*/
		
		FileUtil.getInstance().writeFile(filepathname, sb.toString());
    }
    
    private String getspaceforsum(int length){
    	StringBuffer sb = new StringBuffer("");
    	for(int i=0; i<length;i++){
    		sb.append(" ");
    	}
    	return sb.toString();
    }
    
    /**
	 * Direction: 导出同账户同日累计余额数据
	 * ename: exportBalData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportBalData(Object o){
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+"3128财政库存日报本日余额累计数据("+startDate+"-"+endDate+").txt";
		List templist =null;
		try {
			templist = trStockdayrptService.findTotalBalForExport(dto, finddtolist, startDate, endDate);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询数据出错！");
			return null;
		}
		try {
			if(templist == null || templist.size() == 0){
				MessageDialog.openMessageDialog(null, "数据不存在");
				return null;
			}
			exportBalDataForWhere(templist, fileName, ",");
			MessageDialog.openMessageDialog(null, "数据导出成功\n"+fileName);
		} catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
    
    /**
     * 导出指定时间段账户本日余额累计
     * @param templist
     * @param fileName
     * @param splitSign
     * @return
     * @throws FileOperateException
     */
    public String exportBalDataForWhere(List templist ,String fileName,String splitSign) throws FileOperateException{
    	StringBuffer filebuf = new StringBuffer("账户代码,账户名称,起始日期,终止日期,本日余额累计\r\n");
    	for(int i=0;i<templist.size();i++){
    		TrStockdayrptReportDto dto = (TrStockdayrptReportDto)templist.get(i);
    		filebuf.append(dto.getSaccno()+",");
    		filebuf.append(fetchAcctName(dto.getSaccno(),startDate,endDate)+",");
    		filebuf.append(startDate+",");
    		filebuf.append(endDate+",");
    		filebuf.append(dto.getNtotalmoneytoday());
    		filebuf.append("\r\n");
    	}
    	filebuf.delete(filebuf.length()-2, filebuf.length());
    	FileUtil.getInstance().writeFile(fileName, filebuf.toString());
		return "";
    }
    
    public String fetchAcctName(String acctNo,String startDate,String endDate){
    	String acctName = "";
    	try {
    		acctName = trStockdayrptService.findAcctName(acctNo, startDate, endDate);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询记名出错！");
			return null;
		}
    	return acctName;
    }
    

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {

		try {
			
			String where=" 1=1 ";
			//财政代码为空，国库代码为空，查全局
			if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSorgcode()==null || "".equals(dto.getSorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
				
				where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
				for(int i = 1 ;i<finddtolist.size();i++){
					where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
				}
				where+=" ) ";
			}
			if(startDate != null && !startDate.trim().equals("")){
				where+= " and S_RPTDATE >= '"+startDate+"'" ;
			}
			if(endDate != null && !endDate.trim().equals("")){
				where+= " and S_RPTDATE <= '"+endDate+"'" ;
			}
			
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, where, "", dto.tableName());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TrStockdayrptBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List<TsConvertfinorgDto> getFinddtolist() {
		return finddtolist;
	}

	public void setFinddtolist(List<TsConvertfinorgDto> finddtolist) {
		this.finddtolist = finddtolist;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
}