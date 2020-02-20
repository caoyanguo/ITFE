package com.cfcc.itfe.client.para.tsmtofaccpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;
import com.cfcc.deptone.common.core.exception.FileOperateException;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsSpecacctinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author lixh
 * @time   15-05-25 16:32:26
 * 子系统: Para
 * 模块:tsMtofAccpt
 * 组件:TsMtofAccpt
 */
@SuppressWarnings("unchecked")
public class TsMtofAccptBean extends AbstractTsMtofAccptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsMtofAccptBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private TsSpecacctinfoDto searchdto;
    private TsSpecacctinfoDto updatedto;
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    private PagingContext paging ;
	private List<Mapper> sacctnaturelist;//账户性质
    private List<Mapper> sbnktypelist;//开户行类别
    private List<Mapper> sstateslist;//状态
    private List<TsSpecacctinfoDto> checklist;//复选
    private String reportPath;
	private List reportRs;
	private Map reportmap;
	private List uploadfile;
	private boolean backresult;
	private Map<String,String> tranMap = new HashMap<String,String>();
	private String tips;
    public TsMtofAccptBean() {
      super();
      searchdto = new TsSpecacctinfoDto();
      updatedto = new TsSpecacctinfoDto();
      reportPath = "/com/cfcc/itfe/client/ireport/tsmtofaccpt.jasper";
	  reportRs = new ArrayList();
	  reportmap = new HashMap();
	  uploadfile = new ArrayList();
      paging= new PagingContext(this);
      checklist = new ArrayList<TsSpecacctinfoDto>();
      tips="文件格式:只支持*.csv或者*.txt文件\n";
      tips+="字段格式:归属地,专户账号,专户名称,开户行号,开户行名称,开户行,账户性质,开户日期,销户日期,用途,备注,状态\n";
      
    }
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	backresult = true;
    	updatedto = new TsSpecacctinfoDto();
    	updatedto.setSorgcode(loginfo.getSorgcode());
        return super.goInput(o);
    }
    /**
	 * Direction: 数据导入
	 * ename: uploadimport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String uploadimport(Object o){
        if(uploadfile==null||uploadfile.size()==0)
        {
        	MessageDialog.openMessageDialog(null, "请选择文件!");
			return "";
        }else
        {
        	File filepath;
        	List<String> fileList;
        	List<TsSpecacctinfoDto> dataList = null;
        	for(int i=0;i<uploadfile.size();i++)
        	{
        		filepath = (File)uploadfile.get(i);
        		if(!filepath.getName().endsWith(".csv")&&!filepath.getName().endsWith(".txt"))
        		{
        			MessageDialog.openMessageDialog(null, "文件格式不对,只支持csv文件或者txt文件!");
        			return "";
        		}else
        		{
        			try {
						fileList = readFileWithLine(filepath.getAbsolutePath());
						String[] fields;
						if(fileList!=null&&fileList.size()>0)
						{
							Set<String> tmpTreSet = new HashSet<String>();
							dataList = new ArrayList<TsSpecacctinfoDto>();
							TsSpecacctinfoDto tempdto = null;
							for(int j=0;j<fileList.size();j++)
							{
								fields = fileList.get(j).split(",");
								if(fields.length!=12)
								{
									MessageDialog.openMessageDialog(null, "行号:"+(j+1)+fileList.get(j)+"数据格式不对,每行应有12列数据！");
									return null;
								}
								tempdto = new TsSpecacctinfoDto();
								tempdto.setSorgcode(loginfo.getSorgcode());//核算主体代码
								tempdto.setStrecode(fields[0]);//归属国库代码
								tempdto.setSpayeeacct(fields[1]);//专户账号
								tempdto.setSpayeename(fields[2]);//专户名称
								tempdto.setSopnbankcode(fields[3]);//开户行行号
								tempdto.setSopnbankname(fields[4]);//开户行名称
								if(fields[5]!=null&&(new String(fields[5].getBytes("GBK"),"iso-8859-1").length()>32))
									fields[5] = "其它银行";
								tempdto.setSext1(fields[5]);//开户行类别
//								if(tranMap.get(fields[5])==null)
//								{
//									MessageDialog.openMessageDialog(null, "行号:"+(j+1)+fields[5]+"数据格式不对,开户行类别:中国工商银行,中国农业银行,中国银行,中国建设银行,交通银行,兴业银行,厦门银行,厦门农村商业银行,中国光大银行,中信银行,招商银行,中国民生银行,平安银行,中国邮政储蓄银行,厦门国际银行,其他银行!");
//									return null;
//								}else
//								{
//									tempdto.setSbnktype(tranMap.get(fields[5]));//开户行类别
//								}
								if(tranMap.get(fields[6])==null)
								{
									MessageDialog.openMessageDialog(null, "行号:"+(j+1)+fields[6]+"数据格式不对,账户性质:一般户,专用户,基本户");
									return null;
								}else
								{
									tempdto.setSacctnature(tranMap.get(fields[6]));//账户性质
								}
								if(fields[7]!=null&&fields[7].indexOf("-")>0)
								{
									String[] dates = fields[7].split("-");
									if(dates.length==3)
									{
										fields[7] = dates[0]+(dates[1].length()==1?"0"+dates[1]:dates[1])+(dates[2].length()==1?"0"+dates[2]:dates[2]);
									}
								}
								if(fields[7]==null||fields[7].equals("")||fields[7].length()<=8)
									tempdto.setSopendate(fields[7]);//开户日期
								else
								{
									MessageDialog.openMessageDialog(null, "行号:"+(j+1)+fields[7]+"数据格式不对,开户日期数据为8位或为空!");
									return null;
								}
								if(fields[8]!=null&&fields[8].indexOf("-")>0)
								{
									String[] dates = fields[7].split("-");
									if(dates.length==3)
									{
										fields[8] = dates[0]+(dates[1].length()==1?"0"+dates[1]:dates[1])+(dates[2].length()==1?"0"+dates[2]:dates[2]);
									}
								}
								if(fields[8]==null||fields[8].trim().equals("")||fields[8].length()<=8)
									tempdto.setSclosedate(fields[8]);//销户日期
								else
								{
									MessageDialog.openMessageDialog(null, "行号:"+(j+1)+fields[8]+"数据格式不对,销户日期数据为8位或为空!");
									return null;
								}
								tempdto.setSacctused(fields[9]);//账户用途
								tempdto.setSremark(fields[10]);//备注
								if(tranMap.get(fields[11])==null)
								{
									MessageDialog.openMessageDialog(null, "行号:"+(j+1)+fields[11]+"数据格式不对,状态:正常,销户");
									return null;
								}else
									tempdto.setSstates(tranMap.get(fields[11]));//状态
								if(tmpTreSet.add(tempdto.getSorgcode()+tempdto.getStrecode()+tempdto.getSpayeeacct()))
									dataList.add(tempdto);
							}
							if(dataList!=null&&dataList.size()>0)
							{
								List<TsSpecacctinfoDto> updateList = new ArrayList<TsSpecacctinfoDto>();
								List<TsSpecacctinfoDto> createList = new ArrayList<TsSpecacctinfoDto>();
								tempdto = new TsSpecacctinfoDto();
								tempdto.setSorgcode(loginfo.getSorgcode());
								List<TsSpecacctinfoDto> equalsList = commonDataAccessService.findRsByDto(tempdto);
								Map map = new HashMap();
								if(equalsList!=null&&equalsList.size()>0)
								{
									for(int eq=0;eq<equalsList.size();eq++)
									{
										tempdto = equalsList.get(eq);
										map.put(tempdto.getSorgcode()+tempdto.getStrecode()+tempdto.getSpayeeacct(), tempdto);
									}
									for(int eq=0;eq<dataList.size();eq++)
									{
										tempdto = dataList.get(eq);
										if(map.get(tempdto.getSorgcode()+tempdto.getStrecode()+tempdto.getSpayeeacct())==null)
											createList.add(tempdto);
										else
											updateList.add(tempdto);
									}
									if(updateList.size()>0)
										commonDataAccessService.updateDtos(updateList);
									if(createList.size()>0)
										commonDataAccessService.createdtos(createList);
								}else
								{
									commonDataAccessService.createdtos(dataList);
								}
							}
						}
					} catch (FileOperateException e) {
						MessageDialog.openMessageDialog(null, "文件解析失败："+e.toString());
						return null;
					} catch (ITFEBizException e) {
						MessageDialog.openMessageDialog(null, "保存数据失败："+e.toString());
						return null;
					} catch (UnsupportedEncodingException e) {
						MessageDialog.openMessageDialog(null, "保存数据失败："+e.toString());
						return null;
					}
        		}
        	}
        }
        MessageDialog.openMessageDialog(null, "导入数据成功！");
        uploadfile.clear();
        return super.goBack(o);
    }
    
	/**
	 * Direction: 报表打印
	 * ename: reportprint
	 * 引用方法: 
	 * viewers: 报表打印
	 * messages: 
	 */
    public String reportprint(Object o){
    	editor.fireModelChanged();
		reportRs.clear();
		reportmap.clear();
		reportPath = "/com/cfcc/itfe/client/ireport/tsmtofaccpt.jasper";
		try {
			reportRs =commonDataAccessService.findRsByDto(searchdto);
			if (null == reportRs || reportRs.size() <= 0) {
				MessageDialog.openMessageDialog(null, "查询无数据!");
				return "";
			}else
			{
				TsSpecacctinfoDto tempdto = null;
				for(int i=0;i<reportRs.size();i++)
				{
					tempdto = (TsSpecacctinfoDto)reportRs.get(i);
					tempdto.setSbnktype(tempdto.getSext1());
					tempdto.setSacctnature(tranMap.get("sa"+tempdto.getSacctnature()));
					tempdto.setSstates(tranMap.get("ss"+tempdto.getSstates()));
				}
			}
			reportmap.put("auditClerk",loginfo.getSuserName());
			reportmap.put("printDate", new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), new Exception(
							"查询凭证业务量操作出现异常！", e));
		}
		backresult = true;
        return super.reportprint(o);
    }
    
	/**
	 * Direction: 到数据导入界面
	 * ename: gouploadimport
	 * 引用方法: 
	 * viewers: 数据导入
	 * messages: 
	 */
    public String gouploadimport(Object o){
        
        return super.gouploadimport(o);
    }
    
	/**
	 * Direction: 到录入界面
	 * ename: searchgoinput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String searchgoinput(Object o){
    	backresult = false;
    	updatedto = new TsSpecacctinfoDto();
    	updatedto.setSorgcode(loginfo.getSorgcode());
        return super.searchgoinput(o);
    }
	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if(checklist==null||checklist.size()<=0)
    	{
        	MessageDialog.openMessageDialog(null, "请选择需要修改的记录！");
        	return "";
    	}
    	// 提示用户确定删除
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否要删除选择的记录？")) {
			return "";
		}
		try {
			for(int i=0;i<checklist.size();i++)
				commonDataAccessService.delete(checklist.get(i));
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.DELETEFAIL);
		}
		search(o);
		editor.fireModelChanged();
        return super.delete(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
    	backresult = true;
    	if(checklist==null||checklist.size()<=0)
    	{
        	MessageDialog.openMessageDialog(null, "请选择需要修改的记录！");
        	return "";
    	}else if(checklist.size()==1)
    	{
    		updatedto = checklist.get(0);
    		updatedto.setSacctnature(tranMap.get(updatedto.getSacctnature()));
    		updatedto.setSstates(tranMap.get(updatedto.getSstates()));
    	}
    	else
    	{
    		MessageDialog.openMessageDialog(null, "只能选择一条记录！");
    		return "";
    	}
    	if(updatedto==null||updatedto.getSorgcode()==null)
    	{
    		MessageDialog.openMessageDialog(null, "请选择需要修改的记录！");
    		return "";
    	}
    	
    	editor.fireModelChanged();
        return super.goModify(o);
    }

	/**
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	try {
			commonDataAccessService.updateData(updatedto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYFAIL);
		}
        return super.modifySave(o);
    }

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	updatedto = new TsSpecacctinfoDto();
    	checklist = new ArrayList<TsSpecacctinfoDto>();
    	if(backresult)
    		return super.backMaintenance(o);
    	else
    		return goBack(o);
    }

	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	try {
			commonDataAccessService.create(updatedto);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "保存失败："+e.toString());
		}
    	search(o);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	updatedto = (TsSpecacctinfoDto) o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String search(Object o){
    	  updatedto = new TsSpecacctinfoDto();
    	  PageRequest pageRequest = new PageRequest();
		  PageResponse pageResponse = retrieve(pageRequest);
		  paging.setPage(pageResponse);
		  if(pageResponse==null||pageResponse.getData()==null||pageResponse.getData().size()<=0)
		  {
			  MessageDialog.openMessageDialog(null, "查询数据结果为空！");
			  return "";
		  }else
		  {
			  checklist = new ArrayList<TsSpecacctinfoDto>();
		  }
		  editor.fireModelChanged();
          return super.search(o);
    }

	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 信息查询
	 * messages: 
	 */
    public String goBack(Object o){
    	updatedto = new TsSpecacctinfoDto();
        return super.goBack(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	try
    	{
    		PageResponse pageResponse = commonDataAccessService.findRsByDtoPaging(searchdto, arg0,"","");
    		TsSpecacctinfoDto tempdto = null;
    		if(pageResponse!=null&&pageResponse.getData()!=null&&pageResponse.getData().size()>0)
  		  	{
	    		for(int i=0;i<pageResponse.getData().size();i++)
				{
					tempdto = (TsSpecacctinfoDto)pageResponse.getData().get(i);
					tempdto.setSbnktype(tempdto.getSext1());
					tempdto.setSacctnature(tranMap.get("sa"+tempdto.getSacctnature()));
					tempdto.setSstates(tranMap.get("ss"+tempdto.getSstates()));
				}
  		  	}
    		return pageResponse;
    	} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}
	public TsSpecacctinfoDto getSearchdto() {
		return searchdto;
	}

	public void setSearchdto(TsSpecacctinfoDto searchdto) {
		this.searchdto = searchdto;
	}

	public TsSpecacctinfoDto getUpdatedto() {
		return updatedto;
	}

	public void setUpdatedto(TsSpecacctinfoDto updatedto) {
		this.updatedto = updatedto;
	}

	public List getSacctnaturelist() {
		if(sacctnaturelist==null)
		{
			sacctnaturelist = new ArrayList<Mapper>();
			Mapper mapper = new Mapper("1","一般户");
			tranMap.put(String.valueOf("sa"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sacctnaturelist.add(mapper);
			mapper = new Mapper("2","专用户");
			tranMap.put(String.valueOf("sa"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sacctnaturelist.add(mapper);
			mapper = new Mapper("3","基本户");
			tranMap.put(String.valueOf("sa"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sacctnaturelist.add(mapper);
		}
		return sacctnaturelist;
	}

	public void setSacctnaturelist(List sacctnaturelist) {
		this.sacctnaturelist = sacctnaturelist;
	}

	public List getSbnktypelist() {
		if(sbnktypelist==null)
		{
			sbnktypelist = new ArrayList<Mapper>();
			Mapper mapper = new Mapper("1","中国工商银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("2","中国农业银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("3","中国银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("4","中国建设银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("5","交通银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("6","兴业银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("7","厦门银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("8","厦门农村商业银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			tranMap.put( String.valueOf("厦门市农村商业银行"),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("9","中国光大银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("10","中信银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("11","招商银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("12","中国民生银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("13","平安银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("14","中国邮政储蓄银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("15","厦门国际银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			tranMap.put("厦门市国际银行",String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("16","中国农业发展银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("17","其他银行");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
		}
		return sbnktypelist;
	}

	public void setSbnktypelist(List sbnktypelist) {
		this.sbnktypelist = sbnktypelist;
	}

	public List getSstateslist() {
		if(sstateslist==null)
		{
			sstateslist = new ArrayList<Mapper>();
			Mapper mapper = new Mapper("1","正常");
			tranMap.put(String.valueOf("ss"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sstateslist.add(mapper);
			mapper = new Mapper("2","销户");
			tranMap.put(String.valueOf("ss"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sstateslist.add(mapper);
		}
		return sstateslist;
	}

	public void setSstateslist(List sstateslist) {
		this.sstateslist = sstateslist;
	}

	public PagingContext getPaging() {
		return paging;
	}

	public void setPaging(PagingContext paging) {
		this.paging = paging;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
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

	public List getUploadfile() {
		return uploadfile;
	}

	public void setUploadfile(List uploadfile) {
		this.uploadfile = uploadfile;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TsMtofAccptBean.log = log;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}
	/**
	 * 按照行读取文件，每行根据split进行分割成字符串数组
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String> readFileWithLine(String fileName)
			throws FileOperateException {
		List<String> listStr = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("") || data.startsWith("#")) {
					continue;
				}
				listStr.add(data);
			}
			br.close();
			return listStr;
		}catch(FileNotFoundException e1){ 
			log.error(e1);
			throw new FileOperateException("找不到文件！", e1);
		}catch (Exception e) {
			log.error(e);
			throw new FileOperateException("读取文件出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("读取文件出现异常", e);
				}

			}
		}

	}

	public List<TsSpecacctinfoDto> getChecklist() {
		return checklist;
	}

	public void setChecklist(List<TsSpecacctinfoDto> checklist) {
		this.checklist = checklist;
	}
}