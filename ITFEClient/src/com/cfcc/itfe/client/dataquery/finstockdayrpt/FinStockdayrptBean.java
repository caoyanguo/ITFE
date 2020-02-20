/**
 * 库存日报表查询
 */
package com.cfcc.itfe.client.dataquery.finstockdayrpt;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:库存日报表查询
 * 
 * @author wangtuo
 * @time 10-06-02 15:02:38 子系统: DataQuery 模块:TrStockdayrpt 组件:TrStockdayrpt
 */
public class FinStockdayrptBean extends AbstractFinStockdayrptBean implements
		IPageDataProvider {

	private List trelist;
	private static Log log = LogFactory.getLog(FinStockdayrptBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	List<TsTreasuryDto> finddtolist=null;
	
	private Boolean flag=true; 
	List list=new ArrayList();
	List templist=new ArrayList();
	public FinStockdayrptBean() {
		super();
		dto = new TrStockdayrptDto();
		dto.setSrptdate(TimeFacade.getCurrentStringTime());		
		pagingcontext = new PagingContext(this);
		
		TsTreasuryDto tredto = new TsTreasuryDto();
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			tredto.setSorgcode(loginfo.getSorgcode());
		} else {
			tredto.setStreattrib(StateConstant.COMMON_YES);
		}
		try {
			trelist = commonDataAccessService.getSubTreCode(loginfo
					.getSorgcode());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		dto.setStrecode("");
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

		// 返回页面
		String returnpage = null;
		
		//中心机构代码
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if(null==dto.getSrptdate()||dto.getSrptdate().equals("")){
			MessageDialog.openMessageDialog(null, "报表日期不能为空！");
			return "";
		}
		
		
		TsTreasuryDto finddto=new TsTreasuryDto();
		if (!loginfo.getSorgcode().equals(centerorg) ) {
			
			//判断当前登录查询国库代码、财政代码是否在核算主体中
			finddto.setSorgcode(loginfo.getSorgcode()); //核算主体代码
			if(dto.getSorgcode()!=null && !"".equals(dto.getSorgcode())){
				finddto.setSpayunitname(dto.getSorgcode()); //财政代码
			}
			if(dto.getStrecode()!=null && !"".equals(dto.getStrecode())){
				finddto.setStrecode(dto.getStrecode()); //国库主体代码
			}
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
		
		TsTreasuryDto finddto=new TsTreasuryDto();
		//非中心机构
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//判断当前登录查询国库代码、财政代码是否在国库主体中
//			finddto.setSbookorgcode(loginfo.getSorgcode()); //核算主体代码
		}
		
		try {
			finddtolist = commonDataAccessService.findRsByDto(finddto);
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
			TrStockdayrptDto trstockdayrptdto=new TrStockdayrptDto();
			trstockdayrptdto.setSrptdate(dto.getSrptdate());
			templist=commonDataAccessService.selHtableBydto(trstockdayrptdto);
			for(TsTreasuryDto idto :finddtolist){
				List list=null;
				String strdate=(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis()));
				String copyFilename=idto.getStrecode()+"_"+dto.getSrptdate()+"_"+idto.getStrename()+"财政库存日报_"+strdate+".txt";
				
				//国库代码
				fdto.setStrecode(idto.getStrecode());
				//报表日期
				fdto.setSrptdate(dto.getSrptdate());
				
				//国库代码
				hfdto.setStrecode(idto.getStrecode());
				//报表日期
				hfdto.setSrptdate(dto.getSrptdate());

				if(null!=templist&&templist.size()>0){
					list =commonDataAccessService.findRsByDto(fdto);
				}
				else{
					list =commonDataAccessService.findRsByDto(hfdto);
				}
				
				if (list == null || list.size() == 0) {
					MessageDialog.openMessageDialog(null, idto.getStrecode()+","+idto.getStrename()+" 查询无记录！");
					continue;
				} 
				
				/**
				 * 文件生成
				 */
				filecreat(filepath+copyFilename,list,dto.getSrptdate(),strdate,idto);
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
    
    private void filecreat(String filepathname,List list,String srptdate,String strdate,TsTreasuryDto idto) throws FileOperateException{
    	//生成文件
    	File file = new File(filepathname);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		StringBuffer sb = new StringBuffer("");
		sb.append("                                  "+idto.getStrename()+"财政库存日报 \r\n");
		sb.append("                                  "+"编制日期："+dto.getSrptdate().substring(0, 4) + "年"+ dto.getSrptdate().substring(4, 6) + "月"+ dto.getSrptdate().substring(6, 8) + "日  \r\n");
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
		if(null!=templist&&templist.size()>0){
			List<TrStockdayrptDto> dlist=(List<TrStockdayrptDto>)list;
			
			int maxSaccno=0;
			int maxSaccname=0;
			int maxNmoneyyesterday=0;
			int maxNmoneyin=0;
			int maxNmoneyout=0;
			int maxNmoneytoday=0;
			
			for(TrStockdayrptDto trdto : dlist){
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
			sb.append("本日余额"+getspaceforsum(maxNmoneyyesterday-"本日余额 ".getBytes().length));
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
		}
		else{
			List<HtrStockdayrptDto> hlist=(List<HtrStockdayrptDto>)list;
			int maxSaccno=0;
			int maxSaccname=0;
			int maxNmoneyyesterday=0;
			int maxNmoneyin=0;
			int maxNmoneyout=0;
			int maxNmoneytoday=0;
			
			for(HtrStockdayrptDto trdto : hlist){
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
			sb.append("本日余额"+getspaceforsum(maxNmoneyyesterday-"本日余额 ".getBytes().length));
			sb.append("  \r\n");
			
			for(HtrStockdayrptDto trdto : hlist){
				sb.append(trdto.getSaccno()+getspaceforsum(maxSaccno-trdto.getSaccno().trim().getBytes().length)); //账号/科目
				sb.append(trdto.getSaccname()+getspaceforsum(maxSaccname-trdto.getSaccname().trim().getBytes().length)); //帐户名称
				sb.append(trdto.getNmoneyyesterday().toEngineeringString()+getspaceforsum(maxNmoneyyesterday-trdto.getNmoneyyesterday().toString().getBytes().length)); //上日余额
				sb.append(trdto.getNmoneyin().toPlainString()+getspaceforsum(maxNmoneyin-trdto.getNmoneyin().toString().getBytes().length)); //本日发生额借方
				sb.append(trdto.getNmoneyout().toPlainString()+getspaceforsum(maxNmoneyout-trdto.getNmoneyout().toString().getBytes().length)); //本日发生额贷方
				sb.append(trdto.getNmoneytoday().toPlainString()); //本日余额
				sb.append("  \r\n");
			}
		}
		
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
		//yyyyMMddhhmmssSSS
		String strfomatdate=strdate.substring(0, 4) + "年"+ strdate.substring(4, 6) + "月"+ strdate.substring(6, 8) + "日 "+strdate.substring(8, 10)+":"+strdate.substring(10, 12)+":"+strdate.substring(12, 14);
		sb.append("生成人："+loginfo.getSuserName()+"                  生成日期："+strfomatdate+" \r\n");
		
		FileUtil.getInstance().writeFile(filepathname, sb.toString());
    }
    
    private String getspaceforsum(int length){
    	StringBuffer sb = new StringBuffer("");
    	for(int i=0; i<length;i++){
    		sb.append(" ");
    	}
    	return sb.toString();
    }
    
    //导出库存统计信息
	public String exportexcel(Object o) {

		//查询用dto
    	TrStockdayrptDto fdto = new TrStockdayrptDto();
    	HtrStockdayrptDto hfdto = new HtrStockdayrptDto();
    	
		String dirsep = File.separator; // 取得系统分割符
		String msg = "";
		HashMap<String, String> financeName = new HashMap<String, String>(); // 财政名称
		HashMap<String, String> accountname = new HashMap<String, String>(); // 账户
		HashMap<String, BigDecimal> fmt = new HashMap<String, BigDecimal>(); // 金额
		
		//中心机构代码
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if (!loginfo.getSorgcode().equals(centerorg)) {
			MessageDialog.openMessageDialog(null, " 没有当前功能权限！");
			return "";
		}

		//取得库存财政名称
		TsTreasuryDto finddto=new TsTreasuryDto();
		try {
			
			finddtolist = commonDataAccessService.findRsByDto(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		
		try {
			TrStockdayrptDto trstockdayrptdto=new TrStockdayrptDto();
			trstockdayrptdto.setSrptdate(dto.getSrptdate());
			
			templist=commonDataAccessService.selHtableBydto(trstockdayrptdto);
			List list=null;
			BigDecimal bigtemp=new BigDecimal(10000.00);
			List<Map.Entry<String, String>> infoIds =null;
			List<Map.Entry<String, String>> infoIdsaccountname =null;
			
			for(TsTreasuryDto idto :finddtolist){
				/**
				 * 第一步 设置财政名称map
				 */
				String name =idto.getStrename();
				//'国家金库鞍山市中心支库'
				//'国家金库鞍山市铁东区支库'
				//'国家金库岫岩县支库'
				//'国家金库台安县新开河镇金库'
				name = name.replaceAll("国家", "").replaceAll("金库", "").replaceAll("中心", "").replaceAll("支库", "").replaceAll("鞍山", "").replaceAll("市", "").trim()+"财政";
				financeName.put(idto.getStrecode(), name);
				
				/**
				 * 第二步 取得库存数据，设置户名map和金额map
				 */
				//国库代码
				fdto.setStrecode(idto.getStrecode());
				//报表日期
				fdto.setSrptdate(dto.getSrptdate());
				
				//国库代码
				hfdto.setStrecode(idto.getStrecode());
				//报表日期
				hfdto.setSrptdate(dto.getSrptdate());

				if(null!=templist&&templist.size()>0){
					list =commonDataAccessService.findRsByDto(fdto);
				}
				else{
					list =commonDataAccessService.findRsByDto(hfdto);
				}
				if (list == null || list.size() == 0) {
					continue;
				}
				else{
					if(null!=templist&&templist.size()>0){
						List<TrStockdayrptDto> flist=(List<TrStockdayrptDto>)list;
						for(TrStockdayrptDto trdto : flist){ //N_MONEYTODAY
							accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
							fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
						}
					}else{
						List<HtrStockdayrptDto> hflist=(List<HtrStockdayrptDto>)list;
						for(HtrStockdayrptDto trdto : hflist){ //N_MONEYTODAY
							accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
							fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
						}
					}
				}
			}
			
			//排序
			infoIds =
			    new ArrayList<Map.Entry<String, String>>(financeName.entrySet());
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
			
			infoIdsaccountname =
			    new ArrayList<Map.Entry<String, String>>(accountname.entrySet());
			Collections.sort(infoIdsaccountname, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
		

			String datename=dto.getSrptdate().substring(0, 4) + "年"+ dto.getSrptdate().substring(4, 6) + "月"+ dto.getSrptdate().substring(6, 8) + "日";
			ReportExcel.init();
			String copyFilename = "全市"+datename+"全辖库存统计表.xls";

			// 报表模块文件路径
			String filerootpath = "C:\\" + "Report" + dirsep;
			ReportExcel.setFilepath(filerootpath);
			// ReportExcel.setFilepath("com/cfcc/itfe/client/ireport/");
			// 文件路径
			String rportpath = TimeFacade.getCurrentStringTime() + dirsep;
			ReportExcel.setNewfilepath(filerootpath + rportpath);
			// 新建报表名称
			ReportExcel.setCopyFilename(copyFilename);
			// 报表标题
			ReportExcel.setReporttitle("全市"+datename+"全辖库存统计表");
			msg=filerootpath+copyFilename;
			
			// 出表单位
			ReportExcel.setUnit("");
			// 报表数据
			HashMap<String, Object> datamap = new HashMap<String, Object>();
			datamap.put("infoIds" ,infoIds);
			datamap.put("infoIdsaccountname" ,infoIdsaccountname);
			datamap.put("fmt" ,fmt);
			ReportExcel.setDatamap(datamap);

			// 编制年月日
			String date = TimeFacade.getCurrentStringTime();
			date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
					+ date.substring(6, 8) + "日";
			ReportExcel.setDate(date);

			// 报表模板名称
			ReportExcel.setFilename("model2.xls");
			ReportExcel.setInputstream(FinStockdayrptBean.class
					.getResourceAsStream("model2.xls"));
			// 报表sheet名称
			ReportExcel.setSheetname(datename);

			ReportExcel.getreportbyStockdayrpt("", "");

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		
		MessageDialog.openMessageDialog(null, " 文件已生成！已保存到：" + msg);
		if(flag) editor.fireModelChanged();
		
        return super.exporttxt(o);
	}
	
    public String exportexcelbyfamt(Object o){
        
		//查询用dto
    	TrStockdayrptDto fdto = new TrStockdayrptDto();
    	HtrStockdayrptDto hfdto = new HtrStockdayrptDto();
    	
		String dirsep = File.separator; // 取得系统分割符
		String msg = "";
		HashMap<String, String> financeName = new HashMap<String, String>(); // 财政名称
		HashMap<String, String> accountname = new HashMap<String, String>(); // 账户
		HashMap<String, BigDecimal> fmt = new HashMap<String, BigDecimal>(); // 金额
		
		//中心机构代码
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if (!loginfo.getSorgcode().equals(centerorg)) {
			MessageDialog.openMessageDialog(null, " 没有当前功能权限！");
			return "";
		}

		//取得库存财政名称
		TsTreasuryDto finddto=new TsTreasuryDto();
		
		try {
			finddtolist = commonDataAccessService.findRsByDto(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		
		try {
			TrStockdayrptDto trstockdayrptdto=new TrStockdayrptDto();
			trstockdayrptdto.setSrptdate(dto.getSrptdate());
			templist=commonDataAccessService.selHtableBydto(trstockdayrptdto);
			List list=null;
			BigDecimal bigtemp=new BigDecimal(10000.00);
			List<Map.Entry<String, String>> infoIds =null;
			List<Map.Entry<String, String>> infoIdsaccountname =null;
			
			for(TsTreasuryDto idto :finddtolist){
				
				/**
				 * 第一步 设置财政名称map
				 */
				String name =idto.getStrename();
				//'国家金库鞍山市中心支库'
				//'国家金库鞍山市铁东区支库'
				//'国家金库岫岩县支库'
				//'国家金库台安县新开河镇金库'
				name = name.replaceAll("国家", "").replaceAll("金库", "").replaceAll("中心", "").replaceAll("支库", "").replaceAll("鞍山", "").replaceAll("市", "").trim()+"财政";
				financeName.put(idto.getStrecode(), name);
				
				/**
				 * 第二步 取得库存数据，设置户名map和金额map
				 */
				//国库代码
				fdto.setStrecode(idto.getStrecode());
				//报表日期
				fdto.setSrptdate(dto.getSrptdate());
				
				//国库代码
				hfdto.setStrecode(idto.getStrecode());
				//报表日期
				hfdto.setSrptdate(dto.getSrptdate());
				if(null!=templist&&templist.size()>0){
					list =commonDataAccessService.findRsByDto(fdto);
				}
				else{
					list =commonDataAccessService.findRsByDto(hfdto);
				}
				if (list == null || list.size() == 0) {
					continue;
				}
				else{
					if(null!=templist&&templist.size()>0){
						List<TrStockdayrptDto> flist=(List<TrStockdayrptDto>)list;
						for(TrStockdayrptDto trdto : flist){ //N_MONEYTODAY
							//小于1万元不统计
							if(trdto.getNmoneytoday().compareTo(bigtemp) >= 0){
								accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
								fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
							}
						}
					}else{
						List<HtrStockdayrptDto> hflist=(List<HtrStockdayrptDto>)list;
						for(HtrStockdayrptDto trdto : hflist){ //N_MONEYTODAY
							//小于1万元不统计
							if(trdto.getNmoneytoday().compareTo(bigtemp) >= 0){
								accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
								fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
							}
						}
					}
				}
			}
			
			    new ArrayList<Map.Entry<String, String>>(financeName.entrySet());
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
			
			infoIdsaccountname =
			    new ArrayList<Map.Entry<String, String>>(accountname.entrySet());
			Collections.sort(infoIdsaccountname, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
		

			String datename=dto.getSrptdate().substring(0, 4) + "年"+ dto.getSrptdate().substring(4, 6) + "月"+ dto.getSrptdate().substring(6, 8) + "日";
			ReportExcel.init();
			String copyFilename = "全市"+datename+"全辖库存统计表.xls";

			// 报表模块文件路径
			String filerootpath = "C:\\" + "Report" + dirsep;
			ReportExcel.setFilepath(filerootpath);
			// ReportExcel.setFilepath("com/cfcc/itfe/client/ireport/");
			// 文件路径
			String rportpath = TimeFacade.getCurrentStringTime() + dirsep;
			ReportExcel.setNewfilepath(filerootpath + rportpath);
			// 新建报表名称
			ReportExcel.setCopyFilename(copyFilename);
			// 报表标题
			ReportExcel.setReporttitle("全市"+datename+"全辖库存统计表");
			msg=filerootpath+copyFilename;
			
			// 出表单位
			ReportExcel.setUnit("");
			// 报表数据
			HashMap<String, Object> datamap = new HashMap<String, Object>();
			datamap.put("infoIds" ,infoIds);
			datamap.put("infoIdsaccountname" ,infoIdsaccountname);
			datamap.put("fmt" ,fmt);
			ReportExcel.setDatamap(datamap);

			// 编制年月日
			String date = TimeFacade.getCurrentStringTime();
			date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
					+ date.substring(6, 8) + "日";
			ReportExcel.setDate(date);

			// 报表模板名称
			ReportExcel.setFilename("model2.xls");
			ReportExcel.setInputstream(FinStockdayrptBean.class
					.getResourceAsStream("model2.xls"));
			// 报表sheet名称
			ReportExcel.setSheetname(datename);

			ReportExcel.getreportbyStockdayrpt("", "");

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		
		MessageDialog.openMessageDialog(null, " 文件已生成！已保存到：" + msg);
		if(flag) editor.fireModelChanged();
		
        return super.exporttxt(o);
    }
    
    public String makeSaccname(String saccname){
    	int index = saccname.indexOf("-");
    	if(index>0){
    		return saccname.substring(index+1, saccname.length());
    	}
    	else{
    		return saccname;
    	}
    }
    
    /**
	 * Direction: 导出上海报表
	 * ename: exportReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportReport(Object o){
    	String dirsep = File.separator; // 取得系统分割符
        TrStockdayrptDto nowStockday = new TrStockdayrptDto();
        nowStockday.setStrecode(dto.getStrecode());
        nowStockday.setSrptdate(dto.getSrptdate());
        List result = new ArrayList();
        try {
        	result = commonDataAccessService.findRsByDto(nowStockday, "", nowStockday.tableName());
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		TsTreasuryPK treasuryPK = new TsTreasuryPK();
		treasuryPK.setSorgcode(loginfo.getSorgcode());
		treasuryPK.setStrecode(dto.getStrecode());
		
		TsTreasuryDto tsTreasuryDto = (TsTreasuryDto) commonDataAccessService.find(treasuryPK);
		
		
        String datename=dto.getSrptdate().substring(0, 4) + "年"+ dto.getSrptdate().substring(4, 6) + "月"+ dto.getSrptdate().substring(6, 8) + "日";
		ReportExcel.init();
//		国家金库上海市闵行支库（代理）
		String copyFilename = tsTreasuryDto.getStrename().replaceAll("（代理）", "") + "财政库存日报表.xls";

		// 报表模块文件路径
		String filerootpath = "C:\\" + "Report" + dirsep;
		ReportExcel.setFilepath(filerootpath);
		// ReportExcel.setFilepath("com/cfcc/itfe/client/ireport/");
		// 文件路径
		String rportpath = TimeFacade.getCurrentStringTime() + dirsep;
		ReportExcel.setNewfilepath(filerootpath + rportpath);
		// 新建报表名称
		ReportExcel.setCopyFilename(copyFilename);
		// 报表标题
		ReportExcel.setReporttitle(tsTreasuryDto.getStrename().replaceAll("（代理）", "") + "财政库存日报表");
//		msg=filerootpath+copyFilename;
		// 出表单位
		ReportExcel.setUnit("");
		// 报表数据
		HashMap<String, Object> datamap = new HashMap<String, Object>();
		datamap.put("infoIds" ,result);
//		datamap.put("infoIdsaccountname" ,infoIdsaccountname);
//		datamap.put("fmt" ,fmt);
		ReportExcel.setDatamap(datamap);

		// 编制年月日
		String date = TimeFacade.getCurrentStringTime();
		date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月"
				+ date.substring(6, 8) + "日";
		ReportExcel.setDate(date);

		// 报表模板名称
		ReportExcel.setFilename("model.xls");
		ReportExcel.setInputstream(FinStockdayrptBean.class
				.getResourceAsStream("model.xls"));
		// 报表sheet名称
		ReportExcel.setSheetname(datename);
		ReportExcel.bzunit = tsTreasuryDto.getStrename().replaceAll("（代理）", "");
		ReportExcel.bzuser = loginfo.getSuserName();
		
			String msg = ReportExcel.getreportbyStockdayrpt("", "");
			MessageDialog.openMessageDialog(null, msg);
		} catch (ITFEBizException e) {
			log.error("导出报表失败！",e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.exportReport(o);
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
//			//财政代码为空，国库代码为空，查全局
//			if(list.size()==0&&(dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSorgcode()==null || "".equals(dto.getSorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
//				
//				where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
//				for(int i = 1 ;i<finddtolist.size();i++){
//					where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
//				}
//				where+=" ) ";
//			}
			
			return commonDataAccessService.findRsByDtoWithConditionPaging(dto,
					pageRequest, where, "", dto.tableName()," where S_RPTDATE = '"+dto.getSrptdate()+"' ");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	
	public static Log getLog() {
		return log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List<TsTreasuryDto> getFinddtolist() {
		return finddtolist;
	}

	public void setFinddtolist(List<TsTreasuryDto> finddtolist) {
		this.finddtolist = finddtolist;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}
	
	
	
	
}