package com.cfcc.itfe.client.dataquery.trincomedayrpt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-24 10:44:46
 * 子系统: DataQuery
 * 模块:TrIncomedayrpt
 * 组件:TrIncomedayrpt
 */
public class TrIncomedayrptBean extends AbstractTrIncomedayrptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TrIncomedayrptBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    List<TsConvertfinorgDto> finddtolist=null;
    private List reportRs = null;
    private Map reportmap = new HashMap();
    private String reportPath = "";
	

	private Boolean flag=true; 

	public TrIncomedayrptBean() {
		super();
		dto = new TrIncomedayrptDto();
		dto.setSrptdate(TimeFacade.getCurrentStringTime());
		pagingcontext = new PagingContext(this);
		reportRs = new ArrayList();
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 财政申请收入日报表查询结果 messages:
	 */
	public String query(Object o) {
		// 返回页面
		String returnpage = null;
		
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

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			returnpage = "财政申请收入日报表查询";
		} else {
			returnpage = super.query(o);
		}
		return returnpage;
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 财政申请收入日报表查询 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
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
			if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSfinorgcode()==null || "".equals(dto.getSfinorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
				
				where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
				for(int i = 1 ;i<finddtolist.size();i++){
					where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
				}
				where+=" ) ";
			}
			
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, where, "", dto.tableName());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
	
	/**
	 * Direction: 导出数据
	 * ename: exportTable
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportTable(Object o){
    	String where="";
		//财政代码为空，国库代码为空，查全局
		if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSfinorgcode()==null || "".equals(dto.getSfinorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
			
			where+=" ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
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
		String fileName = filePath+ File.separator+"3128财政申请收入日报("+TimeFacade.getCurrentStringTime()+").txt";
		List<TrIncomedayrptDto> templist=null;
		try {
			templist=trIncomedayrptService.exportQuery(dto, where);
		} catch (ITFEBizException e1) {
			MessageDialog.openMessageDialog(null, "查询数据出错！");
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
    public String exportTableForWhere(List <TrIncomedayrptDto> templist ,String fileName,	String splitSign ) throws FileOperateException{
    	String sql="财政机关代码,征收机关代码,国库主体代码,申请日期,预算种类,预算级次代码,预算科目,预算科目名称,日累计金额,月累计金额,年累计金额,辖属标志,调整期标志,分成组标志,报表种类";
		StringBuffer filebuf = new StringBuffer(sql+"\r\n");
        for (TrIncomedayrptDto _dto :templist) {
        	filebuf.append(_dto.getSfinorgcode()+",");
        	filebuf.append(_dto.getStaxorgcode()+",");
        	filebuf.append(_dto.getStrecode()+",");
        	filebuf.append(_dto.getSrptdate()+",");
        	filebuf.append(_dto.getSbudgettype()+",");
        	filebuf.append(_dto.getSbudgetlevelcode()+",");
        	filebuf.append(_dto.getSbudgetsubcode()+",");
        	filebuf.append(_dto.getSbudgetsubname()+",");
        	filebuf.append(_dto.getNmoneyday()+",");
        	filebuf.append(_dto.getNmoneymonth()+",");
        	filebuf.append(_dto.getNmoneyyear()+",");
        	filebuf.append(_dto.getSbelongflag()+",");
        	filebuf.append(_dto.getStrimflag()+",");
        	filebuf.append(_dto.getSdividegroup()+",");
        	filebuf.append(_dto.getSbillkind());
        	filebuf.append("\r\n");
		}
        filebuf.delete(filebuf.length()-2, filebuf.length());
    	FileUtil.getInstance().writeFile(fileName, filebuf.toString());
		return "";
    	
    	
    }
	/**
	 * Direction: 导出文档
	 * ename: exporttxt
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exporttxt(Object o){
        
    	//查询用dto
    	TrIncomedayrptDto fdto = new TrIncomedayrptDto();
    	HtrIncomedayrptDto hdto = new HtrIncomedayrptDto();
    	
    	String dirsep = File.separator; // 取得系统分割符
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
		
		String rtmsg ="";
		
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
			return null;
		}
		if(null==finddtolist || finddtolist.size() == 0){
			MessageDialog.openMessageDialog(null, "权限不足！请填写正确的财政代码或者国库代码！");
			return null;
		}
		
		try {
			List<TrIncomedayrptDto> listFile= new ArrayList();
			String copyFilename=dto.getSrptdate()+"_shouru_"+finddto.getSorgcode()+"_"+(new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()))+".txt";
			for(TsConvertfinorgDto idto :finddtolist){
					List<TrIncomedayrptDto> list1=null;
					//国库代码
					fdto.setStrecode(idto.getStrecode());
					//报表日期
					fdto.setSrptdate(dto.getSrptdate());
					list1 =(List<TrIncomedayrptDto>)commonDataAccessService.findRsByDto(fdto);
					if (list1 != null && list1.size() != 0) {
						listFile.addAll(list1);
					} 
//				}
			}
			if (listFile != null && listFile.size() != 0) {
				//创建文件
				filecreat(filepath+copyFilename,listFile,dto.getSrptdate(),"0");
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
		if(flag) this.editor.fireModelChanged();
        return super.exporttxt(o);
    }
    
    private void filecreat(String filepathname,List<TrIncomedayrptDto> list,String srptdate,String flag) throws FileOperateException{
    	
    	//生成文件
    	File file = new File(filepathname);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		StringBuffer sb = new StringBuffer("");
		sb.append("czjgdm,zsjgdm,gkztdm,rq,yszl,ysjcdm,yskm,yskmmc,rljje,xljje,yljje,jljje,nljje,xsbz,tzqbz,fczbz,bbzl \r\n");
		List<TrIncomedayrptDto> dlist=(List<TrIncomedayrptDto>)list;
		
		if(flag.equals("0")){//all
			for(TrIncomedayrptDto trdto : dlist){
				
				//如果为不分征收机关，不导出
//				if(trdto.getStaxorgcode().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)){
//					continue;
//				}
				
				sb.append(trdto.getSfinorgcode()+","); //skgkdm
				sb.append(trdto.getStaxorgcode()+","); //mdgkdm
				sb.append(trdto.getStrecode()+","); //ssgkdm
				sb.append(trdto.getSrptdate()+","); //ysjc
				sb.append(trdto.getSbudgettype()+","); //jgdm
				sb.append(trdto.getSbudgetlevelcode()+","); //kmdm
				sb.append(trdto.getSbudgetsubcode()+","); //zwrq
				sb.append(trdto.getSbudgetsubname()+","); //yszl
				sb.append(trdto.getNmoneyday()+","); //rlj
				sb.append(trdto.getNmoneytenday()+","); //ylj
				sb.append(trdto.getNmoneymonth()+","); //nlj
				sb.append(trdto.getNmoneyquarter()+","); //aaaa
				sb.append(trdto.getNmoneyyear()+","); //aaaa
				sb.append(trdto.getSbelongflag()+","); //aaaa
				sb.append(trdto.getStrimflag()+","); //aaaa
				sb.append(trdto.getSdividegroup()+","); //aaaa
				sb.append(trdto.getSbillkind()); //aaaa
				sb.append(" \r\n");
			}
		}
		else{//000000000000
			for(TrIncomedayrptDto trdto : dlist){
				
				//如果为不分征收机关，导出
				if(trdto.getStaxorgcode().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)){
					
					sb.append(trdto.getStrecode()+","); //skgkdm
					sb.append(trdto.getStrecode()+","); //mdgkdm
					sb.append(trdto.getStrecode()+","); //ssgkdm
					sb.append(trdto.getSbudgetlevelcode()+","); //ysjc
					sb.append(trdto.getStaxorgcode()+","); //jgdm
					sb.append(trdto.getSbudgetsubcode()+","); //kmdm
					sb.append(trdto.getSrptdate()+","); //zwrq
					sb.append(trdto.getSbudgettype()+","); //yszl
					sb.append(trdto.getNmoneyday()+","); //rlj
					sb.append(trdto.getNmoneymonth()+","); //ylj
					sb.append(trdto.getNmoneyyear()); //nlj
					sb.append(" \r\n");
				}
			}
		}

		FileUtil.getInstance().writeFile(filepathname, sb.toString());
    }
    
	/**
	 * Direction: 导出文档不分征收机关
	 * ename: exporttxt0
	 * 引用方法: 
	 * viewers: 财政申请收入日报表查询
	 * messages: 
	 */
    public String exporttxt0(Object o){
        
    	//查询用dto
    	TrIncomedayrptDto fdto = new TrIncomedayrptDto();
    	HtrIncomedayrptDto hdto = new HtrIncomedayrptDto();
    	
    	String dirsep = File.separator; // 取得系统分割符
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
		
		String rtmsg ="";
		
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
			return null;
		}
		if(null==finddtolist || finddtolist.size() == 0){
			MessageDialog.openMessageDialog(null, "权限不足！请填写正确的财政代码或者国库代码！");
			return null;
		}
		
		try {
			
			for(TsConvertfinorgDto idto :finddtolist){
				
				//只出市库和县区库
//				if(idto.getStrelevel().equals("3") || idto.getStrelevel().equals("4")){
					
					List<TrIncomedayrptDto> list1=null;
					String copyFilename=idto.getStrecode()+"_"+dto.getSrptdate()+"_000000000000_"+(new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()))+".txt";
					
					//国库代码
					fdto.setStrecode(idto.getStrecode());
					//报表日期
					fdto.setSrptdate(dto.getSrptdate());
					
					list1 =(List<TrIncomedayrptDto>)commonDataAccessService.findRsByDto(fdto);

					if (list1 == null || list1.size() == 0) {
						MessageDialog.openMessageDialog(null, idto.getStrecode()+","+idto.getStrename()+": 查询无记录！");
						continue;
					} 
					
					//创建文件
					filecreat(filepath+copyFilename,list1,dto.getSrptdate(),"1");
					rtmsg +=filepath+copyFilename+"; \r\n";
//				}
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
		if(flag) this.editor.fireModelChanged();
        return super.exporttxt0(o);
    }
    
    /**
	 * Direction: 返回到查询结果
	 * ename: rebackToSelectResult
	 * 引用方法: 
	 * viewers: 财政申请收入日报表查询结果
	 * messages: 
	 */
    public String rebackToSelectResult(Object o){
        
        return super.rebackToSelectResult(o);
    }
    
	/**
	 * Direction: 报表预览
	 * ename: printReport
	 * 引用方法: 
	 * viewers: 财政申请收入日报表打印
	 * messages: 
	 */
    public String printReport(Object o){
    	reportPath = "com/cfcc/itfe/client/ireport/trIncomedayrpt.jasper";
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
    	List<TrIncomedayrptDto>	list = new ArrayList<TrIncomedayrptDto>();
    	String where=" 1=1 ";
		//财政代码为空，国库代码为空，查全局
		if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSfinorgcode()==null || "".equals(dto.getSfinorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
			
			where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
			for(int i = 1 ;i<finddtolist.size();i++){
				where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
			}
			where+=" ) ";
		}
		TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
		tsTreasuryDto.setStrecode(dto.getStrecode());
		String trename = "";
		try {
			list.addAll(trIncomedayrptService.exportQuery(dto, where));
	    	reportRs.clear();
			reportRs.addAll(list);
			List<TsTreasuryDto> list1 =(List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
			if(list1!=null && list1.size()>0){
				trename = list1.get(0).getStrename();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if (null == reportRs || reportRs.size() == 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return super.goBack(o);
		}
		//预算级次
		if(dto.getSbudgetlevelcode()==null || dto.getSbudgetlevelcode().equals("")){
			MessageDialog.openMessageDialog(null, "请选择预算级次!");
			return super.goBack(o);
		}else {
			if(dto.getSbudgetlevelcode().equals("0")){
				reportmap.put("title", trename+"共享级预算收入日报表");
			}else if(dto.getSbudgetlevelcode().equals("1")){
				reportmap.put("title", trename+"中央级预算收入日报表");
			}else if(dto.getSbudgetlevelcode().equals("2")){
				reportmap.put("title", trename+"省级预算收入日报表");
			}else if(dto.getSbudgetlevelcode().equals("3")){
				reportmap.put("title", trename+"市级预算收入日报表");
			}else if(dto.getSbudgetlevelcode().equals("4")){
				reportmap.put("title", trename+"县级预算收入日报表");
			}else if(dto.getSbudgetlevelcode().equals("5")){
				reportmap.put("title", trename+"乡级预算收入日报表");
			}else if(dto.getSbudgetlevelcode().equals("6")){
				reportmap.put("title", trename+"地方级预算收入日报表");
			}
		}
		//辖属标志
		if(dto.getSbelongflag()==null || dto.getSbelongflag().equals("")){
			MessageDialog.openMessageDialog(null, "请选择辖属标志!");
			return super.goBack(o);
		}else{
			if(dto.getSbelongflag().equals("0")){
				reportmap.put("sbelongflag", "【本级】");
			}else if(dto.getSbelongflag().equals("1")){
				reportmap.put("sbelongflag", "【全辖】");
			}else if(dto.getSbelongflag().equals("3")){
				reportmap.put("sbelongflag", "【全辖非本级】");
			}
		}
		//预算种类
		if(dto.getSbudgettype()==null || dto.getSbudgettype().equals("")){
			MessageDialog.openMessageDialog(null, "请选择预算种类!");
			return super.goBack(o);
		}else{
			if(dto.getSbudgettype().equals("1")){
				reportmap.put("sbudgettype", "【预算内】");
			}else if(dto.getSbudgettype().equals("2")){
				reportmap.put("sbudgettype", "【预算外】");
			}
		}
		//报表日期
		if(dto.getSrptdate()==null || dto.getSrptdate().equals("")){
			MessageDialog.openMessageDialog(null, "请选择报表日期!");
			return super.goBack(o);
		}else{
			String date = dto.getSrptdate().substring(0, 4)+"年"+dto.getSrptdate().substring(4, 6)+"月"+dto.getSrptdate().substring(6, 8)+"日";
			reportmap.put("reportDate", date);
		}
		editor.fireModelChanged();
        return super.printReport(o);
    }
    
	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TrIncomedayrptBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getFinddtolist() {
		return finddtolist;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public void setFinddtolist(List<TsConvertfinorgDto> finddtolist) {
		this.finddtolist = finddtolist;
	}
	
	public List getReportRs() {
		return reportRs;
	}

	public void setReportRs(List reportRs) {
		this.reportRs = reportRs;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	
}