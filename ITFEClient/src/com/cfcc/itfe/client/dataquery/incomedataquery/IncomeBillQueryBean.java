package com.cfcc.itfe.client.dataquery.incomedataquery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 09-11-06 18:18:43 子系统: DataQuery 模块:incomeDataQuery 组件:IncomeBillQuery
 */
public class IncomeBillQueryBean extends AbstractIncomeBillQueryBean implements IPageDataProvider {

	private ITFELoginInfo loginInfo = null; // 登陆信息

	private TvInfileDto finddto = null; // 查询条件DTO对象

	private TvInfileDto operdto = null; // 操作DTO对象

	private PagingContext incometablepage; // 分页控件
	
	private List<TdEnumvalueDto> typelist = new ArrayList<TdEnumvalueDto>(); //枚举查询类型
	private List<IDto> checklist = null;
	private String ifdetail ; //查询类型
	
	private TvInfileDetailDto paramDetail = new TvInfileDetailDto();
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	public IncomeBillQueryBean() {
		super();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();

		incometablepage = new PagingContext(this);
		finddto = new TvInfileDto();
		operdto = new TvInfileDto();
		finddto.setScommitdate(TimeFacade.getCurrentStringTime());
		checklist = new ArrayList<IDto>();
		init();
		ifdetail = "0";
		selectedIncomeList=new ArrayList<TvInfileDto>();
	}

	public void init() {
		TdEnumvalueDto value1 = new TdEnumvalueDto();
		value1.setStypecode("0");
		value1.setSvaluecmt("汇总税票信息");
		
		TdEnumvalueDto value2 = new TdEnumvalueDto();
		value2.setStypecode("1");
		value2.setSvaluecmt("明细税票信息");
		
		typelist.add(value1);
		typelist.add(value2);
		
	}
	
	public TvInfileDetailDto createDetailInfo() {
		TvInfileDetailDto detail = new TvInfileDetailDto();
		detail.setSrecvtrecode(finddto.getSrecvtrecode());
		detail.setScommitdate(finddto.getScommitdate());
		detail.setNmoney(finddto.getNmoney());
		detail.setSfilename(finddto.getSfilename());
		detail.setSpackageno(finddto.getSpackageno());
		detail.setSdealno(finddto.getSdealno());
		detail.setStbstaxorgcode(finddto.getStbstaxorgcode());
		detail.setStaxorgcode(finddto.getStaxorgcode());
		detail.setStbsassitsign(finddto.getStbsassitsign());
		detail.setSassitsign(finddto.getSassitsign());
		detail.setSbudgettype(finddto.getSbudgettype());
		detail.setSbudgetlevelcode(finddto.getSbudgetlevelcode());
		detail.setSbudgetsubcode(finddto.getSbudgetsubcode());
		detail.setSpaybookkind(finddto.getSpaybookkind());
		detail.setStrasrlno(finddto.getStrasrlno());
		return detail;
	}
	
	/**
	 * Direction: 查询列表事件 ename: searchList 引用方法: viewers: 收入税票列表界面 messages:
	 */
	public String searchList(Object o) {		
		if(null == ifdetail || "".equals(ifdetail)) {
			ifdetail = "0";
		}
		if("1".equals(ifdetail)) {  //查看明细信息
			this.setParamDetail(createDetailInfo());
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		incometablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return super.rebackSearch(o);
		}

		editor.fireModelChanged();

		return super.searchList(o);

	}
	
	/**
	 * Direction: 导出税票事件 ename: exportBillData 引用方法: viewers: * messages:
	 */
	public String exportBillData(Object o) {

		if (null == operdto || null == operdto.getSpackageno() || "".equals(operdto.getSpackageno().trim())) {
			MessageDialog.openMessageDialog(null, "导出数据前必须先选择包流水号！");
			return super.exportBillData(o);
		}

		// 取得当前系统时间
		String date = TimeFacade.getCurrentStringTime();

		try {
			String exportStr = incomeBillService.exportIncomeData(ifdetail, operdto);
			if (null == exportStr) {
				MessageDialog.openMessageDialog(null, "没有找到要导出的记录！");
			} else {
				// 选择保存路径
				DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
						.getActiveShell());
				String filePath = path.open();
				List<String> filelist = new ArrayList<String>();
				if ((null == filePath) || (filePath.length() == 0)) {
					MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
					return "";
				}
				/*String clientFilePath;
				String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
				String dirsep = File.separator;
				String serverFilePath;*/
				
				String dirsep = File.separator; // 取得系统分割符
				/*String fileName = "c:" + dirsep + "client" + dirsep + "income" + dirsep + date + dirsep + "tips" + loginInfo.getSorgcode()
						+ operdto.getSpackageno() + "00.txt" + dirsep;*/
				String fileName = "tips" + loginInfo.getSorgcode() + operdto.getSdealno() + "00.txt";
				String fullName = filePath + dirsep + fileName;
				File file = new File(fullName);
				File dir = new File(file.getParent());

				FileOutputStream output = null;
				try {
					if (!dir.exists()) {
						dir.mkdirs();
					}
					output = new FileOutputStream(fullName, false);
					output.write(exportStr.getBytes("GBK"));

					MessageDialog.openMessageDialog(null, "导出记录成功:记录存放路径[" + fullName + "]");
				} catch (IOException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} catch (RuntimeException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (Exception ex) {
							return super.exportBillData(o);
						}
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.exportBillData(o);
	}
	
	
	/**
	 * Direction: 导出查询出的所有税票事件 ename: exportAllBillData 引用方法: viewers: * messages:
	 */
	public String exportAllBillData(Object o) {
		List list=new ArrayList();
		try {
			if("1".equals(ifdetail)) {
				list =  incomeBillService.findIncomeByDto(this.getParamDetail(),ifdetail);
			}else if("0".equals(ifdetail)){
				list =  incomeBillService.findIncomeByDto(finddto,ifdetail);
			}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		// 取得当前系统时间
		String date = TimeFacade.getCurrentStringTime();

		try {
			
			String exportStr = incomeBillService.exportAllIncomeData(list,ifdetail);
			if (null == exportStr||"".equals(exportStr)) {
				MessageDialog.openMessageDialog(null, "没有找到要导出的记录！");
				return "";
			} else {
				// 选择保存路径
				DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
						.getActiveShell());
				String filePath = path.open();
				List<String> filelist = new ArrayList<String>();
				if ((null == filePath) || (filePath.length() == 0)) {
					MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
					return "";
				}
				
				String dirsep = File.separator; // 取得系统分割符
				String fileName="";
				Random random = new Random();
				int count = random.nextInt(100000000);
				fileName = "tips" + loginInfo.getSorgcode() + count+ "00.txt";
				String fullName = filePath + dirsep + fileName;
				File file = new File(fullName);
				File dir = new File(file.getParent());

				FileOutputStream output = null;
				try {
					if (!dir.exists()) {
						dir.mkdirs();
					}
					output = new FileOutputStream(fullName, false);
					output.write(exportStr.getBytes("GBK"));

					MessageDialog.openMessageDialog(null, "导出记录成功:记录存放路径[" + fullName + "]");
				} catch (IOException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} catch (RuntimeException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (Exception ex) {
							return super.exportBillData(o);
						}
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.exportAllBillData(o);
	}

	/**
	 * Direction: 导出查询出的选择税票事件 ename: exportSelectedBillData 引用方法: viewers: * messages:
	 */
	public String exportSelectedBillData(Object o) {
		if (null == selectedIncomeList || selectedIncomeList.size()<=0) {
			MessageDialog.openMessageDialog(null, "导出数据前必须先选择所要导出的记录！");
			return super.exportBillData(o);
		}

		// 取得当前系统时间
		String date = TimeFacade.getCurrentStringTime();

		try {
			String exportStr = incomeBillService.exportSelectedIncomeData(selectedIncomeList,ifdetail);
			if (null == exportStr||"".equals(exportStr)) {
				MessageDialog.openMessageDialog(null, "没有找到要导出的记录！");
				selectedIncomeList.clear();
				selectedIncomeList=new ArrayList<TvInfileDto>();
				this.editor.fireModelChanged();
				return "";
			} else {
				// 选择保存路径
				DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
						.getActiveShell());
				String filePath = path.open();
				List<String> filelist = new ArrayList<String>();
				if ((null == filePath) || (filePath.length() == 0)) {
					MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
					return "";
				}
				
				String dirsep = File.separator; // 取得系统分割符
				String fileName="";
				if(selectedIncomeList.size()==1){
					String dealno="";
					if("1".equals(ifdetail)) {
						dealno =  ((TvInfileDetailDto)selectedIncomeList.get(0)).getSdealno();
					}else if("0".equals(ifdetail)){
						dealno =  ((TvInfileDto)selectedIncomeList.get(0)).getSdealno();
					}
					 fileName = "tips" + loginInfo.getSorgcode() + dealno + "00.txt";
				}else{
					Random random = new Random();
					int count = random.nextInt(100000000);
					fileName = "tips" + loginInfo.getSorgcode() + count+ "00.txt";
				}
				String fullName = filePath + dirsep + fileName;
				File file = new File(fullName);
				File dir = new File(file.getParent());

				FileOutputStream output = null;
				try {
					if (!dir.exists()) {
						dir.mkdirs();
					}
					output = new FileOutputStream(fullName, false);
					output.write(exportStr.getBytes("GBK"));

					MessageDialog.openMessageDialog(null, "导出记录成功:记录存放路径[" + fullName + "]");
				} catch (IOException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} catch (RuntimeException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (Exception ex) {
							return super.exportBillData(o);
						}
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		selectedIncomeList.clear();
		selectedIncomeList=new ArrayList<TvInfileDto>();
		this.editor.fireModelChanged();
		return super.exportSelectedBillData(o);
	}

	/**
	 * Direction: 补发报文事件 ename: reSendMsg 引用方法: viewers: * messages:
	 */
	public String reSendMsg(Object o) {

		if (null == operdto || null == operdto.getSpackageno() || "".equals(operdto.getSpackageno().trim())) {
			MessageDialog.openMessageDialog(null, "请选择要重发报文的记录！");
			return super.reSendMsg(o);
		}

		try {
			incomeBillService.reSendMsg(operdto.getScommitdate(), operdto.getSpackageno(), operdto.getSorgcode(), operdto.getSfilename());
			MessageDialog.openMessageDialog(null, "重发报文成功！[包流水号：" + operdto.getSpackageno() + "]");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.reSendMsg(o);
	}

	/**
	 * Direction: 返回查询界面 ename: rebackSearch 引用方法: viewers: 收入税票查询界面 messages:
	 */
	public String rebackSearch(Object o) {
		return super.rebackSearch(o);
	}
	/**
	 * Direction: 数据列表双击事件
	 * ename: doubleclickIncome
	 * 引用方法: 
	 * viewers: 收入税票编辑页面
	 * messages: 
	 */
    public String doubleclickIncome(Object o){
    	if(o instanceof TvInfileDto) {
			operdto = (TvInfileDto) o;
		}else if(o instanceof TvInfileDetailDto) {
			operdto = new TvInfileDto();
			TvInfileDetailDto tmp = (TvInfileDetailDto)o;
			operdto.setSrecvtrecode(tmp.getSrecvtrecode());
			operdto.setScommitdate(tmp.getScommitdate());
			operdto.setNmoney(tmp.getNmoney());
			operdto.setSfilename(tmp.getSfilename());
			operdto.setSpackageno(tmp.getSpackageno());
			operdto.setSdealno(tmp.getSdealno());
			operdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
			operdto.setStaxorgcode(tmp.getStaxorgcode());
			operdto.setStbsassitsign(tmp.getStbsassitsign());
			operdto.setSassitsign(tmp.getSassitsign());
			operdto.setSbudgettype(tmp.getSbudgettype());
			operdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
			operdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
			operdto.setSpaybookkind(tmp.getSpaybookkind());
			operdto.setStrasrlno(tmp.getStrasrlno());
		}
        return super.doubleclickIncome(o);
    }
	/**
	 * Direction: 收入税票明细单击事件 ename: singleclickIncome 引用方法: viewers: * messages:
	 */
	public String singleclickIncome(Object o) {
		if(o instanceof TvInfileDto) {
			operdto = (TvInfileDto) o;
		}else if(o instanceof TvInfileDetailDto) {
			operdto = new TvInfileDto();
			TvInfileDetailDto tmp = (TvInfileDetailDto)o;
			operdto.setSrecvtrecode(tmp.getSrecvtrecode());
			operdto.setScommitdate(tmp.getScommitdate());
			operdto.setNmoney(tmp.getNmoney());
			operdto.setSfilename(tmp.getSfilename());
			operdto.setSpackageno(tmp.getSpackageno());
			operdto.setSdealno(tmp.getSdealno());
			operdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
			operdto.setStaxorgcode(tmp.getStaxorgcode());
			operdto.setStbsassitsign(tmp.getStbsassitsign());
			operdto.setSassitsign(tmp.getSassitsign());
			operdto.setSbudgettype(tmp.getSbudgettype());
			operdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
			operdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
			operdto.setSpaybookkind(tmp.getSpaybookkind());
			operdto.setStrasrlno(tmp.getStrasrlno());
		}
		
		return super.singleclickIncome(o);
	}
	private IDto swipInfileOrInfileDetail(IDto dto) throws ITFEBizException
	{
		IDto returndto = null;
		if(dto instanceof TvInfileDto)
		{
			TvInfileDetailDto datadto = new TvInfileDetailDto();
			TvInfileDto temp = (TvInfileDto)dto;
			datadto.setSdealno(temp.getSdealno());
			List<TvInfileDetailDto> list = commonDataAccessService.findRsByDto(datadto);
			if(list!=null&&list.size()==1)
				datadto = list.get(0);
			else
				return null;
			datadto.setSrecvtrecode(temp.getSrecvtrecode());
			datadto.setScommitdate(temp.getScommitdate());
			datadto.setNmoney(temp.getNmoney());
			datadto.setSfilename(temp.getSfilename());
			datadto.setSpackageno(temp.getSpackageno());
			datadto.setSdealno(temp.getSdealno());
			datadto.setStbstaxorgcode(temp.getStbstaxorgcode());
			datadto.setStaxorgcode(temp.getStaxorgcode());
			datadto.setStbsassitsign(temp.getStbsassitsign());
			datadto.setSassitsign(temp.getSassitsign());
			datadto.setSbudgettype(temp.getSbudgettype());
			datadto.setSbudgetlevelcode(temp.getSbudgetlevelcode());
			datadto.setSbudgetsubcode(temp.getSbudgetsubcode());
			datadto.setSpaybookkind(temp.getSpaybookkind());
			datadto.setStrasrlno(temp.getStrasrlno());
			returndto = datadto;
		}else if(dto instanceof TvInfileDetailDto)
		{
			TvInfileDto datadto = new TvInfileDto();
			TvInfileDetailDto temp = (TvInfileDetailDto)dto;
			datadto.setSdealno(temp.getSdealno());
			List<TvInfileDto> list = commonDataAccessService.findRsByDto(datadto);
			if(list!=null&&list.size()==1)
				datadto = list.get(0);
			else
				return null;
			datadto.setSrecvtrecode(temp.getSrecvtrecode());
			datadto.setScommitdate(temp.getScommitdate());
			datadto.setNmoney(temp.getNmoney());
			datadto.setSfilename(temp.getSfilename());
			datadto.setSpackageno(temp.getSpackageno());
			datadto.setSdealno(temp.getSdealno());
			datadto.setStbstaxorgcode(temp.getStbstaxorgcode());
			datadto.setStaxorgcode(temp.getStaxorgcode());
			datadto.setStbsassitsign(temp.getStbsassitsign());
			datadto.setSassitsign(temp.getSassitsign());
			datadto.setSbudgettype(temp.getSbudgettype());
			datadto.setSbudgetlevelcode(temp.getSbudgetlevelcode());
			datadto.setSbudgetsubcode(temp.getSbudgetsubcode());
			datadto.setSpaybookkind(temp.getSpaybookkind());
			datadto.setStrasrlno(temp.getStrasrlno());
			returndto = datadto;
		}
		return returndto;
	}
    /**
	 * Direction: 保存数据
	 * ename: savedata
	 * 引用方法: 
	 * viewers: 收入税票列表界面
	 * messages: 
	 */
    public String savedata(Object o){
    	if(operdto!=null&&operdto.getSrecvtrecode()!=null&&!"".equals(operdto.getSrecvtrecode()))
        {
        	try
        	{
        		IDto swipdto = this.swipInfileOrInfileDetail(operdto);
        		if(swipdto!=null)
        		{
        			commonDataAccessService.updateData(operdto);
        			commonDataAccessService.updateData(swipdto);
        			MessageDialog.openMessageDialog(null, "保存数据成功!");
        		}
        		
        	} catch (Throwable e) {
    			MessageDialog.openMessageDialog(null, "保存数据失败："+e.toString());
    			return "";
    		}
        	operdto = new TvInfileDto();
        }
        return super.savedata(o);
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
			PageResponse respone = null ;
			if("1".equals(ifdetail)) {
				respone =  incomeBillService.findIncomeByPage(this.getParamDetail(), pageRequest);
			}else if("0".equals(ifdetail)){
				respone =  incomeBillService.findIncomeByPage(finddto, pageRequest);
			}
			return respone;
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.retrieve(pageRequest);
	}
	

	/**
	 * Direction: 全选/反选 ename: selectAllOrNone 引用方法: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String selectAllOrNone(Object o) {
		if (this.incometablepage == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.incometablepage.getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvInfileDto> templist = page.getData();
		if (templist != null && this.selectedIncomeList != null) {
			if (selectedIncomeList.size() != 0 && selectedIncomeList.containsAll(templist)) {
				selectedIncomeList.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (selectedIncomeList.contains(templist.get(i))) {
						selectedIncomeList.set(selectedIncomeList.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						selectedIncomeList.add(i, templist.get(i));
					}
				}
			}
		}
		this.editor.fireModelChanged();
		return super.selectAllOrNone(o);
	}

	public TvInfileDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvInfileDto finddto) {
		this.finddto = finddto;
	}

	public PagingContext getIncometablepage() {
		return incometablepage;
	}

	public void setIncometablepage(PagingContext incometablepage) {
		this.incometablepage = incometablepage;
	}

	public TvInfileDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvInfileDto operdto) {
		this.operdto = operdto;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public List<TdEnumvalueDto> getTypelist() {
		return typelist;
	}

	public void setTypelist(List<TdEnumvalueDto> typelist) {
		this.typelist = typelist;
	}

	public String getIfdetail() {
		return ifdetail;
	}

	public void setIfdetail(String ifdetail) {
		this.ifdetail = ifdetail;
	}

	public TvInfileDetailDto getParamDetail() {
		return paramDetail;
	}

	public void setParamDetail(TvInfileDetailDto paramDetail) {
		this.paramDetail = paramDetail;
	}

}