package com.cfcc.itfe.client.dataquery.electaxbillquery;

import java.util.*;
import java.io.*;
import java.math.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;

/**
 * codecomment: 
 * @author Administrator
 * @time   13-09-23 10:26:46
 * 子系统: DataQuery
 * 模块:elecTaxBillQuery
 * 组件:ElecTaxBillExport
 */
@SuppressWarnings("unchecked")
public class ElecTaxBillExportBean extends AbstractElecTaxBillExportBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(ElecTaxBillExportBean.class);
    private TvFinIncomeDetailDto dto;	//入库流水
    private ITFELoginInfo loginfo;
    private List treCodeList = null;
	private List buslist = null;
    public ElecTaxBillExportBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TvFinIncomeDetailDto();
      dto.setSintredate(loginfo.getCurrentDate().replaceAll("-", ""));
      dto.setIpkgseqno(dto.getSintredate());
      dto.setSorgcode("0");
      init();
    }
    
    private void init()
    {
    	try
    	{
    		treCodeList=commonDataAccessService.getSubTreCode(loginfo.getSorgcode());
    	} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
    }
	/**
	 * Direction: 数据导出TXT
	 * ename: exportToTxt
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportToTxt(Object o){
    	try {
			// 选择保存路径
			DirectoryDialog choosePath = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String path = choosePath.open();
			if ((null == path) || (path.length() == 0)) {
				MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
				return "";
			}
    		String filename="";
    		String trecode = dto.getStrecode();
    		if(StringUtils.isNotBlank(trecode)){
    			filename = dto.getSintredate() + "_" + trecode + "_elecTaxBill.txt";
    		}else{
    			filename = dto.getSintredate() + "_0000000000_elecTaxBill.txt";
    		}
    		String filepath = elecTaxBillExportServiceService.exportfile(dto);
    		if(StringUtils.isBlank(filepath)){
    			MessageDialog.openMessageDialog(null, "没有符合条件的数据信息！");
    			return null;
    		}else{
//    			String clientpath = "C:" + File.separator + "Report" + File.separator + TimeFacade.getCurrentStringTime() + File.separator + filename;
    			String clientpath = path + File.separator + filename;
    			File file = new File(clientpath);
    			if(file.exists()){
    				file.delete();
    			}else{
    				new File(file.getParent()).mkdirs();
    			}
    			
    			ClientFileTransferUtil.downloadFile(filepath,clientpath);
    			MessageDialog.openMessageDialog(null, "报表路径:" + clientpath);
    		}
		} catch (Throwable e) {
			log.error("导出入库电子税票信息失败!", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		} 
          return super.exportToTxt(o);
    }
    /**
	 * Direction: 业务量统计
	 * ename: busworkcount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String busworkcount(Object o){
    	Map paramMap = new HashMap();
    	try {
    		paramMap.put("dto", dto);
			List list =	elecTaxBillExportServiceService.busworkcount(paramMap);
			if(list!=null&&list.size()>0)
			{
				buslist = new ArrayList();
				Map datamap = new HashMap();
				Map tempmap = null;
				BigDecimal allamt = new BigDecimal(0);
				int allcount = 0;
				for(int i=0;i<list.size();i++)
				{
					tempmap = (Map)list.get(i);
					if("0".equals(String.valueOf(tempmap.get("type"))))//无法区分
					{
						datamap.put("nocount", tempmap.get("allcount"));
						datamap.put("noamt", tempmap.get("allamt"));
					}else if("1".equals(String.valueOf(tempmap.get("type"))))//电子
					{
						datamap.put("autocount", tempmap.get("allcount"));
						datamap.put("autoamt", tempmap.get("allamt"));
					}else if("2".equals(String.valueOf(tempmap.get("type"))))//手工
					{
						datamap.put("workcount", tempmap.get("allcount"));
						datamap.put("workamt", tempmap.get("allamt"));
					}
					allamt = allamt.add(new BigDecimal(String.valueOf(tempmap.get("allamt"))));
					allcount+=Integer.parseInt(String.valueOf(tempmap.get("allcount")));
				}
				datamap.put("allamt", allamt);
				datamap.put("allcount", allcount);
				datamap.put("strecode", dto.getStrecode());//	    		dto.strecode国库代码
				datamap.put("sorgcode", "0".equals(dto.getSorgcode())?"本级":"全辖");//	    		dto.sorgcode报表范围				datamap.put("cvouchannel", value);//	    		dto.cvouchannel凭证来源
				if(dto.getCbdgkind()==null||"".equals(dto.getCbdgkind()))
					datamap.put("cbdgkind", "全部");//	    		dto.cbdgkind预算种类;
				else
					datamap.put("cbdgkind", "1".equals(dto.getCbdgkind())?"预算内":"预算外");//	    		dto.cbdgkind预算种类
				if("0".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "税票");//dto.sexpvoutype凭证类型;
				else if("1".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "退库");//dto.sexpvoutype凭证类型;
				else if("2".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "更正");//dto.sexpvoutype凭证类型;
				else if("3".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "免抵调");//dto.sexpvoutype凭证类型;
				else if("4".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "免抵调调减");//dto.sexpvoutype凭证类型;
				else if("5".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "免抵调调增");//dto.sexpvoutype凭证类型;
				else if("6".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "退库退回");//dto.sexpvoutype凭证类型;
				else if("7".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "退库补税");//dto.sexpvoutype凭证类型;
				else if("8".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "退抵欠退项");//dto.sexpvoutype凭证类型;
				else if("9".equals(dto.getSexpvoutype()))
					datamap.put("sexpvoutype", "退抵欠欠项");//dto.sexpvoutype凭证类型;
				else
					datamap.put("sexpvoutype", "");//dto.sexpvoutype凭证类型;
				datamap.put("staxorgcode", dto.getStaxorgcode()==null?"":dto.getStaxorgcode());//	    		dto.staxorgcode征收机关代码
				datamap.put("sintredate", dto.getSintredate());//	    		dto.sintredate日期起
				datamap.put("ipkgseqno", dto.getIpkgseqno());//	    		dto.ipkgseqno日期止
				datamap.put("sexpvouno", dto.getSexpvouno());//	    		dto.sexpvouno凭证编号
				datamap.put("sbdgsbtcode", dto.getSbdgsbtcode());//	    		dto.sbdgsbtcode预算科目代码
				buslist.add(datamap);
			}
		} catch (ITFEBizException e) {
			log.error("导出入库电子税票信息失败!", e);
			MessageDialog.openErrorDialog(null, e);
			return super.busworkcount(o);
		}
		editor.fireModelChanged();
        return super.busworkcount(o);
    }
    /**
	 * Direction: 业务量统计
	 * ename: busworkcount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String busexport(Object o){
    	if(buslist==null||buslist.size()<=0)
    	{
    		MessageDialog.openMessageDialog(null, "没有需要导出的数据，请先进行业务量统计！");
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
		String fileName = filePath+ File.separator+ "业务量统计" +loginfo.getCurrentDate().replaceAll("-", "") + ".txt";
		try {
			StringBuffer resultStr = new StringBuffer();
			resultStr.append("国库代码,报表范围,预算种类,征收机关,日期起,日期止,凭证类型,总笔数,电子,手工,无法区分,总金额,电子,手工,无法区分"+System.getProperty("line.separator"));
			Map tmpmap = null;
			for(int i=0;i< buslist.size();i++){
				tmpmap = (Map)buslist.get(i);
				resultStr.append(tmpmap.get("strecode")+",");
				resultStr.append(tmpmap.get("sorgcode")+",");
				resultStr.append(tmpmap.get("cbdgkind")+",");
				resultStr.append(tmpmap.get("staxorgcode")+",");
				resultStr.append(tmpmap.get("sintredate")+",");
				resultStr.append(tmpmap.get("ipkgseqno")+",");
				resultStr.append(tmpmap.get("sexpvoutype")+",");
				resultStr.append(tmpmap.get("allcount")+",");
				resultStr.append(tmpmap.get("autocount")+",");
				resultStr.append(tmpmap.get("workcount")+",");
				resultStr.append(tmpmap.get("nocount")+",");
				resultStr.append(tmpmap.get("allamt")+",");
				resultStr.append(tmpmap.get("autoamt")+",");
				resultStr.append(tmpmap.get("workamt")+",");
				resultStr.append(tmpmap.get("noamt")+System.getProperty("line.separator"));
			}
			FileUtil.getInstance().writeFile(fileName, resultStr.toString());
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.busexport(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public TvFinIncomeDetailDto getDto() {
		return dto;
	}

	public void setDto(TvFinIncomeDetailDto dto) {
		this.dto = dto;
	}


	public List getTreCodeList() {
		return treCodeList;
	}


	public void setTreCodeList(List treCodeList) {
		this.treCodeList = treCodeList;
	}

	public List getBuslist() {
		return buslist;
	}

	public void setBuslist(List buslist) {
		this.buslist = buslist;
	}


    
}