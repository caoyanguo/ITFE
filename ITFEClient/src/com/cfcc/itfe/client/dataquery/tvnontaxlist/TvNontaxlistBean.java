package com.cfcc.itfe.client.dataquery.tvnontaxlist;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.NontaxDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   18-04-27 15:05:00
 * 子系统: DataQuery
 * 模块:TvNontaxlist
 * 组件:TvNontaxlist
 */
public class TvNontaxlistBean extends AbstractTvNontaxlistBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvNontaxlistBean.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    public TvNontaxlistBean() {
      super();
      searchDto = new NontaxDto();
      searchDto.setSstartdate(TimeFacade.getCurrentStringTime());
      searchDto.setSenddate(TimeFacade.getCurrentStringTime());
      searchDto.setStrecode(logInfo.getSorgcode().substring(0, 10));
      reportPath = "/com/cfcc/itfe/client/ireport/itfe_TV_NONTAXSUB.jasper";
      rsList = new ArrayList();
      paramMap = new HashMap();
    }
    
	/**
	 * Direction: 查询到结果界面
	 * ename: queryResult
	 * 引用方法: 
	 * viewers: 报表
	 * messages: 
	 */
    public String queryResult(Object o){
    	rsList.clear();
    	paramMap.clear();
    	try {
			rsList = tvNontaxlistService.findInfo(searchDto);
			if(rsList!=null&&rsList.size()>0)
			{
				if(searchDto.getSfinorgcode()==null||"".equals(searchDto.getSfinorgcode()))
				{
					NontaxDto nTaxDto = (NontaxDto)rsList.get(0);
					searchDto.setSfinorgcode(nTaxDto.getSfinorgcode());
				}
			}else
			{
				MessageDialog.openMessageDialog(null, "查询数据为空!");
				return "";
			}				
			paramMap.put("printDate", TimeFacade.getCurrent2StringTime());
			paramMap.put("username", logInfo.getSuserName());
			String tmpbnkname = "";
			if(searchDto.getSpaybankno()!=null && !searchDto.getSpaybankno().equals("")){
				TsPaybankDto pbdto = new TsPaybankDto();
				pbdto.setSbankno(searchDto.getSpaybankno());
				List<TsPaybankDto> list = commonDataAccessService.findRsByDto(pbdto);
				if(list != null && list.size() > 0){
					tmpbnkname = list.get(0).getSbankname();
				}
			}else{
				tmpbnkname = "全部";
			}
			if(searchDto.getStrecode()!=null)
			{
				TsTreasuryDto trdto = new TsTreasuryDto();
				trdto.setStrecode(searchDto.getStrecode());
				trdto.setSorgcode(logInfo.getSorgcode());
				List<TsTreasuryDto> list = commonDataAccessService.findRsByDto(trdto);
				if(list != null && list.size() > 0){
					paramMap.put("strename",list.get(0).getStrename());
				}else
				{
					paramMap.put("strename", "未知");
				}
			}else
			{
				paramMap.put("strename", "全部");
			}
			if(searchDto.getSfinorgcode()!=null)
			{
				TsTaxorgDto trdto = new TsTaxorgDto();
				trdto.setStrecode(searchDto.getStrecode());
				trdto.setSorgcode(logInfo.getSorgcode());
				trdto.setStaxorgcode(searchDto.getSfinorgcode());
				List<TsTaxorgDto> list = commonDataAccessService.findRsByDto(trdto);
				if(list != null && list.size() > 0){
					paramMap.put("staxorgname",list.get(0).getStaxorgname());
				}else
				{
					paramMap.put("staxorgname", "未知");
				}
			}else
			{
				paramMap.put("staxorgname", "全部");
			}
			paramMap.put("spaybankname", tmpbnkname);
			for(int i = 0;i<rsList.size();i++){
				NontaxDto nTaxDto = (NontaxDto)rsList.get(i);
				if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("1")){
					nTaxDto.setSxaddword("中央");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("2")){
					nTaxDto.setSxaddword("省市");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("3")){
					nTaxDto.setSxaddword("地市");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("4")){
					nTaxDto.setSxaddword("区县");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("0")){
					nTaxDto.setSxaddword("共享");
					nTaxDto.setSvicesign(getSharingRatio(nTaxDto.getSvicesign()));
				}
			}
		} catch (ITFEBizException e) {
			log.error("查询数据错误！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.queryResult(o);
    }
    /**
	 * Direction: 导出txt
	 * ename: exporttxt
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exporttxt(Object o){
    	if(rsList==null||rsList.size()<=0)
    	{
    		MessageDialog.openMessageDialog(null, "没有需要导出的数据，请先查询需要导出的数据！");
    		return "";
    	}
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+ searchDto.getStrecode()+"_"+searchDto.getSstartdate()+"_" +searchDto.getSenddate()+ ".txt";
		try {
			StringBuffer resultStr = new StringBuffer();
			resultStr.append("收款国库:"+paramMap.get("strename")+"征收机关:"+paramMap.get("staxorgname")+"银行:"+paramMap.get("spaybankname")+System.getProperty("line.separator"));
			resultStr.append("缴款识别码,执收单位名称,缴款人名称,预算科目代码,预算级次,辅助标志,金额"+System.getProperty("line.separator"));
			NontaxDto temp = null;
			for(int i=0;i<rsList.size();i++){
				temp = (NontaxDto)rsList.get(i);
				resultStr.append(temp.getShold1()+","); //缴款识别码
				resultStr.append(temp.getShold4()+",");	//执收单位名称
				resultStr.append(temp.getShold2()+",");	//缴款人名称
				resultStr.append(temp.getSitemcode()+",");//预算科目代码
				resultStr.append(temp.getSxaddword()+",");//预算级次
				resultStr.append(temp.getSvicesign()+",");//辅助标志
				resultStr.append(temp.getNtraamt()+System.getProperty("line.separator"));//金额
			}
			FileUtil.getInstance().writeFile(fileName, resultStr.toString());
			MessageDialog.openMessageDialog(null, "导出成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.exporttxt(o);
    }
    /**
     * 辅助标志35位，每7位代表所对应的层级的占比
     * @param ratioCode
     * @return
     */
    public String getSharingRatio(String ratioCode){
    	StringBuffer sb = new StringBuffer();
    	String tmp = ratioCode.substring(0, 7);
    	StringBuffer ratStrSb = new StringBuffer("");
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	DecimalFormat df = new DecimalFormat("0.00%");
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append("中央:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	tmp = ratioCode.substring(7, 14);
    	sb = new StringBuffer();
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append(" 省市:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	tmp = ratioCode.substring(14, 21);
    	sb = new StringBuffer();
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append(" 地市:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	tmp = ratioCode.substring(21, 28);
    	sb = new StringBuffer();
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append(" 区县:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	return ratStrSb.toString();
    }

	/**
	 * Direction: 返回到查询界面
	 * ename: backQuery
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String backQuery(Object o){
          return super.backQuery(o);
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