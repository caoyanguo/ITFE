package com.cfcc.itfe.client.dataquery.organdmoneydetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.deptone.common.core.config.SystemConfig;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.persistence.dto.OrgAndMoneyDetail;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-05-07 21:48:04 子系统: DataQuery 模块:orgAndMoneyDetail
 *       组件:OrgAndMoneyDetail
 */
public class OrgAndMoneyDetailBean extends AbstractOrgAndMoneyDetailBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(OrgAndMoneyDetailBean.class);
	private String reportpath = "com/cfcc/itfe/client/ireport/OrgAndMoneyDetailReport.jasper";
	private List reportRs = null;
	private Map reportmap = new HashMap();
	private String flag;
	private int moneyposition = 0;
	private String path = "C:\\预算收入报表解付";
	private List<OrgAndMoneyDetail> filelist = new ArrayList<OrgAndMoneyDetail>();

	public OrgAndMoneyDetailBean() {
		super();
		filepath = new ArrayList<File>();

	}

	/**
	 * Direction: 省核算明细 ename: provinceDetail 引用方法: viewers: 核算明细报表页 messages:
	 */
	public String provinceDetail(Object o) {
		flag = "10";
		moneyposition=2;
		choseFile();
		filelist = getFileList(filepath, flag);
		reportRs = filelist;
		return super.provinceDetail(o);
	}
	
	/**
	 * Direction: 市核算明细 ename: cityDetail 引用方法: viewers: 核算明细报表页 messages:
	 */
	public String cityDetail(Object o) {
		flag = "11";
		moneyposition = 2;
		choseFile();
		filelist = getFileList(filepath, flag);
		reportRs = filelist;
		return super.cityDetail(o);
	}

	/**
	 * Direction: 区县核算明细 ename: townDetail 引用方法: viewers: 核算明细报表页 messages:
	 */
	public String townDetail(Object o) {
		flag = "63";
		moneyposition=1;
		choseFile();
		filelist = getFileList(filepath, flag);
		reportRs = filelist;
		return super.townDetail(o);
	}

	/**
	 * 获取文件列表
	 * 
	 * @param filepath
	 * @param flag
	 * @return
	 */
	/**
	 * @param filepath
	 * @param flag
	 * @return
	 */
	private List<OrgAndMoneyDetail> getFileList(List filepath, String flag) {
		try {
			filelist = new ArrayList<OrgAndMoneyDetail>();
			int filenum = 0;
			String pathflag="";

			TsTreasuryDto orgdto = new TsTreasuryDto();

			String name = "";
			// 根据查看类型添加报表标题
			if (flag.equals("11")) {
				name = "上海市省级预算收入报表发生额清单";
				orgdto.setStreattrib("2");
				pathflag = "\\省";
			} else if (flag.equals("10")) {
				name = "上海市中央级预算收入报表发生额清单";
				orgdto.setStreattrib("2");
				pathflag = "\\中央";
			} else {
				name = "上海市区县级预算收入报表发生额清单";
				orgdto.setStrelevel("4");
				pathflag = "\\区县";
			}
			boolean flags = false;
			// 查询固定类型的国库信息
			List<TsTreasuryDto> listOrg = commonDataAccessService
					.findRsByDto(orgdto);
			// 获取首个文件日期
			String times = ((File) filepath.get(0)).getName().substring(0, 6);
			// 遍历文件
			for (int j = 0; j < filepath.size(); j++) {
				// 获取读取的文件的日期
				String timese = ((File) filepath.get(j)).getName().substring(0,
						6);
				if (j > 0 && timese.equals(times)) {
					continue;
				} else {
					times = timese;
				}
				// 遍历国库
				for (int i = 0; i < listOrg.size(); i++) {
					// 遍历文件
					for (int k = 0; k < filepath.size(); k++) {
						// 获取文件
						File file = (File) filepath.get(k);
						String timeses = file.getName().substring(0, 6);
						// 文件所属国库与遍历的国库信息对应且与上一个文件同日期的则添加
						if (file.getName().substring(6, 16).equals(
								listOrg.get(i).getStrecode())
								&& timeses.equals(times)) {

							BufferedReader reader = new BufferedReader(
									new FileReader(file));
							String tempString = reader.readLine();
							String[] arr = tempString.split(",");
							String orgcode = file.getName().substring(10, 12);

							OrgAndMoneyDetail dto = new OrgAndMoneyDetail();
							dto.setFilename(file.getName());
							dto.setMoney(BigDecimal.valueOf(Double
									.valueOf(arr[moneyposition])));
							dto
									.setOrgname(orgcode
											+ listOrg.get(i).getStrename()
													.substring(7));

							dto.setDates(times);

							filelist.add(dto);
							filenum++;

							flags = true;
							break;
						} else {
							flags = false;
						}
					}
					// 添加文件中未对应的国库信息
					if (!flags) {
						OrgAndMoneyDetail dto = new OrgAndMoneyDetail();
						dto.setOrgname(listOrg.get(i).getStrecode().substring(
								4, 6)
								+ listOrg.get(i).getStrename().substring(7));
						dto.setDates(times);
						filelist.add(dto);
					}
				}

			}

			// 分类文件到指定文件夹
			
			for (int j = 0; j < filepath.size(); j++) {
				File file = (File) filepath.get(j);
				String timeses = file.getName().substring(0, 6);
				String trecode = file.getName().substring(10, 12);
				String paths = path + "\\" + timeses + "\\" + pathflag;
				File files = new File(paths);
				if (!files.exists()) {
					files.mkdirs();
				}
				
				
				String pathss = "";
				if (Integer.parseInt(trecode) < 12) {
					pathss = paths + "\\" + "工行带资金报表";
				} else if (Integer.parseInt(trecode) < 22) {
					pathss = paths + "\\" + "农行带资金报表";
				} else {
					pathss = paths + "\\" + "不带资金报表";
				}
				File copyfiles = new File(pathss);
				if(!copyfiles.exists()){
					copyfiles.mkdirs();
				}
				FileUtil.getInstance().copyFile(file.getPath(), pathss + "\\" + file.getName());
			}

			reportmap.put("dates", times.substring(0, 2) + "年"
					+ times.substring(2, 4) + "月" + times.substring(4) + "日");
			reportmap.put("title", name);
			reportmap.put("filenum", filenum);
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日")
					.format(new java.util.Date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filelist;
	}
	
	/**
	 * 筛选文件
	 * @param filepath
	 * @param flag
	 */
	private void choseFile() {
		
		if(flag.equals("63")){//区县
			for(int i=0;i<filepath.size();i++){
				File file = (File) filepath.get(i);
				String[] filename = file.getName().split(",");
				if(filename[1].trim().equals("01004.pas")){
					filepath.remove(i);
					i--;
				}
			}
		}else if(flag.equals("10")){//中央
			for(int i=0;i<filepath.size();i++){
				File file = (File) filepath.get(i);
				String[] filename = file.getName().split(",");
				if(filename[1].trim().equals("00504.pas")){
					filepath.remove(i);
					i--;
					continue;
				}
				if(filename[0].substring(16, 18).equals("11")){
					filepath.remove(i);
					i--;
					continue;
				}
			}
		}else if(flag.equals("11")){//省
			for(int i=0;i<filepath.size();i++){
				File file = (File) filepath.get(i);
				String[] filename = file.getName().split(",");
				if(filename[1].trim().equals("00504.pas")){
					filepath.remove(i);
					i--;
					continue;
				}
				if(filename[0].substring(16, 18).equals("10")){
					filepath.remove(i);
					i--;
					continue;
				}
			}
		}
	}
	
	/**
	 * Direction: 返回 ename: reback 引用方法: viewers: 文件导入界面 messages:
	 */
	public String reback(Object o) {
		return super.reback(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public String getReportpath() {
		return reportpath;
	}

	public void setReportpath(String reportpath) {
		this.reportpath = reportpath;
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

}