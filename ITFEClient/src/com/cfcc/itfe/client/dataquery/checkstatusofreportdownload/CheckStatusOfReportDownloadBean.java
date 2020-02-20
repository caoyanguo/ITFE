package com.cfcc.itfe.client.dataquery.checkstatusofreportdownload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.StatusOfReportDownLoad;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.querylogs.IQueryLogsService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-04-19 14:21:47
 * ��ϵͳ: DataQuery
 * ģ��:checkStatusOfReportDownload
 * ���:CheckStatusOfReportDownload
 */
@SuppressWarnings("unchecked")
public class CheckStatusOfReportDownloadBean extends AbstractCheckStatusOfReportDownloadBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(CheckStatusOfReportDownloadBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    protected IQueryLogsService queryLogsService = (IQueryLogsService)getService(IQueryLogsService.class);
    private String reportpath = "com/cfcc/itfe/client/ireport/StatusOfDownLoadReport.jasper";
	private List reportRs = null;
	private List treCodeList = null;
	private String searchArea = null;
	private Map reportmap = new HashMap();
	private List<StatusOfReportDownLoad> pagingList = null;
	private ITFELoginInfo loginfo = null;
	private List voucherTypeList;
	private int gkcount=0;
	private int srcount=0;
	private int lscount=0;
	private int spcount=0;
	private int kccount=0;
	private int zccount=0;
	private int gdcount=0;
	private int hgcount=0;
	private int tkcount=0;
	private String busType;
    public CheckStatusOfReportDownloadBean() {
      super();
      pagingContext = new PagingContext(this);
      searchdate = java.sql.Date.valueOf(TimeFacade.getCurrent2StringTime()); 
      reportRs = new ArrayList();
      searchArea = "0";
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
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
	 * Direction: ��ѯ���ر�����������
	 * ename: search
	 * ���÷���: 
	 * viewers: ���ر������������
	 * messages: 
	 */
    public String search(Object o){
    	PageRequest request = new PageRequest();
    	if(searchArea!=null&&!"".equals(searchArea.trim()))
    		strecode = "";
    	PageResponse response = retrieve(request);
    	if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			 return "";
		}
    	pagingList = response.getData();
    	if(pagingList!=null&&pagingList.size()>0)
		{
    		gkcount = pagingList.size();
    		lscount=0;srcount=0;spcount=0;kccount=0;zccount=0;gdcount=0;hgcount=0;tkcount=0;
			for(int i=0;i<pagingList.size();i++)
			{
				if("1".equals(pagingList.get(i).getSliushui()))
					lscount++;
				if("1".equals(pagingList.get(i).getSribao()))
					srcount++;
				if("1".equals(pagingList.get(i).getSshuipiao()))
					spcount++;
				if("1".equals(pagingList.get(i).getSkucun()))
					kccount++;
				if("1".equals(pagingList.get(i).getSzhichu()))
					zccount++;
				if("1".equals(pagingList.get(i).getSext1()))
					gdcount++;
				if("1".equals(pagingList.get(i).getSishaiguan()))
					hgcount++;
				if(pagingList.get(i).getItuikucount()!=null&&pagingList.get(i).getItuikucount()>0)
					tkcount+=pagingList.get(i).getItuikucount();
			}
		}
		editor.fireModelChanged();
        return super.search(o);
    }
    /**
	 * Direction: ����
	 * ename: exportcheckresult
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportcheckresult(Object o){
    	if(pagingList==null||pagingList.size()<=0)
    	{
    		MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ����Ȳ�ѯ��Ҫ���������ݣ�");
    		return "";
    	}
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+ "�·����������" +loginfo.getCurrentDate().replaceAll("-", "") + ".txt";
		try {
			StringBuffer resultStr = new StringBuffer();
			resultStr.append("����,�������,��������,�����ձ�,�����ˮ,����˰Ʊ,����ձ�,֧������"+System.getProperty("line.separator"));
			for(StatusOfReportDownLoad tmp : pagingList){
				resultStr.append(tmp.getSdates()+","); //����
				resultStr.append(tmp.getStrecode()+",");	//�������
				resultStr.append(tmp.getStrename()+",");	//��������
				resultStr.append(tmp.getSribao()+",");//�ձ��·����
				resultStr.append(tmp.getSliushui()+",");//��ˮ�·����
				resultStr.append(tmp.getSshuipiao()+",");//˰Ʊ�·����
				resultStr.append(tmp.getSkucun()+",");//����·����
				resultStr.append(tmp.getSzhichu()+System.getProperty("line.separator"));//֧���������
			}
			FileUtil.getInstance().writeFile(fileName, resultStr.toString());
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.exportcheckresult(o);
    }
	/**
	 * Direction: ��ӡԤ��
	 * ename: queryprint
	 * ���÷���: 
	 * viewers: ���ر������������������
	 * messages: 
	 */
    public String queryprint(Object o){
		try {
			PageRequest request = new PageRequest();
			PageResponse  response = checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, strecode, request);
			List<StatusOfReportDownLoad> list = new ArrayList<StatusOfReportDownLoad>();
			list.addAll(response.getData());
			if(null == list || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "û���ҵ���Ӧ��¼!");
				return null;
			}
			if(list!=null&&list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					if("0".equals(list.get(i).getSliushui()))
						list.get(i).setSliushui("��");
					else
						list.get(i).setSliushui("��");
					if("0".equals(list.get(i).getSribao()))
						list.get(i).setSribao("��");
					else
						list.get(i).setSribao("��");
					if("0".equals(list.get(i).getSshuipiao()))
						list.get(i).setSshuipiao("��");
					else
						list.get(i).setSshuipiao("��");
					if("0".equals(list.get(i).getSkucun()))
						list.get(i).setSkucun("��");
					else
						list.get(i).setSkucun("��");
					if("0".equals(list.get(i).getSzhichu()))
						list.get(i).setSzhichu("��");
					else
						list.get(i).setSzhichu("��");
				}
			}
			reportRs.clear();
			reportRs.addAll(list);
			reportmap.put("printDate",  new SimpleDateFormat("yyyy��MM��dd��").format(new java.util.Date()));
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
          return super.queryprint(o);
    }
    /**
	 * Direction: ��������������
	 * ename: exportToServer
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportToServer(Object o){
    	String exportdate = searchdate==null?TimeFacade.getCurrentStringTimebefor():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ�ϵ���"+exportdate+"�����ļ���������Ŀ¼?")) {
    		try
    		{
    			checkStatusOfReportDownloadService.exportToServer(exportdate);
    			MessageDialog.openMessageDialog(null,"���������ļ����������ɹ���");
    		} catch (ITFEBizException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"���������ļ���������ʧ�ܣ�");
    		}
    	}
        return super.exportToServer(o);
    }
	/**
	 * Direction: ����
	 * ename: reback
	 * ���÷���: 
	 * viewers: ���ر������������
	 * messages: 
	 */
    public String reback(Object o){
//    	PageRequest request = new PageRequest();
//    	retrieve(request);
        return super.reback(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
		try {
			if(strecode==null||"".equals(strecode.trim()))
			{
				List<TsTreasuryDto> findTreCodeList = null;
				if("0".equals(searchArea))
				{
					TsTreasuryDto findto = new TsTreasuryDto();
					findto.setSorgcode(loginfo.getSorgcode());
					findTreCodeList = commonDataAccessService.findRsByDto(findto);
				}else if("1".equals(searchArea))
				{
					findTreCodeList = commonDataAccessService.getSubTreCode(loginfo.getSorgcode());
				}
				StringBuffer temp = new StringBuffer("");
				for(int i=0;i<findTreCodeList.size();i++)
				{
					if(i==(findTreCodeList.size()-1))
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"'");
					else
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"',");
				}
				strecode = temp.toString();
			}else
			{
				if(strecode.indexOf("'")<0)
					strecode = "'"+strecode+"'";
			}
			
			return checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, strecode, arg0);
		} catch (ITFEBizException e) {
			e.printStackTrace();
			return null;
		}
	}
    /**
	 * Direction: ����ҵ�����ݵ�������
	 * ename: exportBusData
	 * ���÷���: 
	 * viewers: ����ҵ�����ݵ�������
	 * messages: 
	 */
    public String exportBusData(Object o){
    	String exportdate = searchdate==null?TimeFacade.getCurrentStringTime():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ�ϵ���"+exportdate+"ҵ�����ݵ�������Ŀ¼?")) {
    		try
    		{
    			String result = checkStatusOfReportDownloadService.exportBusData(exportdate, busType);
    			MessageDialog.openMessageDialog(null,result);
    		} catch (ITFEBizException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"����ҵ�����ݵ�������Ŀ¼ʧ�ܣ�");
    		}
    	}
       
        return super.exportBusData(o);
    }
    /**
	 * Direction: ����tips�·�����
	 * ename: exporttipsreport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exporttipsreport(Object o){
		String exportdate = searchdate==null?TimeFacade.getCurrentStringTimebefor():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ����������"+exportdate+"��tips�·��ı�������?")) {
    		// �ͻ������ر��ĵ�·��
    		String dirsep = "/"; // ȡ��ϵͳ�ָ��
    		// ѡ�񱣴�·��
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
    				.getActiveShell());
    		String filePath = path.open();
    		if ((null == filePath) || (filePath.length() == 0)) {
    			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
    			return "";
    		}
    		filePath = filePath+dirsep+"downloadTipsReport"+dirsep;
    		File dir = new File(filePath);
    		if (!dir.exists()) {
    			dir.mkdirs();
    		}
    		TvRecvlogDto findto = new TvRecvlogDto();
    		findto.setSdate(exportdate);
    		if(loginfo.getPublicparam().indexOf(",tipsDownLoadReport=clientall,")<0)
    			findto.setSrecvorgcode(loginfo.getSorgcode());
    		try {
				List tempList = commonDataAccessService.findRsByDtoWithWhere(findto," and (S_OPERATIONTYPECODE='"+MsgConstant.MSG_NO_3128+"' or S_OPERATIONTYPECODE='"+MsgConstant.MSG_NO_3129+"' or S_OPERATIONTYPECODE='"+MsgConstant.MSG_NO_3139+"')");
				if(tempList!=null&&tempList.size()>0)
				{
					List<TvRecvlogDto> dataList = new ArrayList<TvRecvlogDto>();
					TvRecvlogDto tempdto = null;
					Set<String> tmpSet = new HashSet<String>(); 
					for(int i=0;i<tempList.size();i++)
					{
						tempdto = (TvRecvlogDto)tempList.get(i);
						if(MsgConstant.MSG_NO_3128.equals(tempdto.getSoperationtypecode())&&StringUtils.isNotBlank(tempdto.getStrecode()))
						{
							if(tmpSet.add(tempdto.getSoperationtypecode()+tempdto.getStrecode()))
								dataList.add(tempdto);
						}else if(MsgConstant.MSG_NO_3129.equals(tempdto.getSoperationtypecode())||MsgConstant.MSG_NO_3139.equals(tempdto.getSoperationtypecode()))
						{
							if(tmpSet.add(tempdto.getSoperationtypecode()+tempdto.getSseq()))
								dataList.add(tempdto);
						}else
						{
							dataList.add(tempdto);
						}
					}
					if(dataList!=null&&dataList.size()>0)
					{
						int lastIndex = 0;
						String clientfile = null;
						for(TvRecvlogDto downdto:dataList)
						{
							lastIndex = downdto.getStitle().replace("\\", "/").lastIndexOf(dirsep);
							clientfile = filePath+downdto.getStitle().substring(lastIndex + 1);
							ClientFileTransferUtil.downloadFile(downdto.getStitle().replace(queryLogsService.getFileRootPath(),""), clientfile);
						}
					}else
					{
						MessageDialog.openMessageDialog(null, "��ѯ�������ݣ�");
						return "";
					}	
					
				}else
				{
					MessageDialog.openMessageDialog(null, "��ѯ�������ݣ�");
					return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "����ʧ�ܣ�"+e.toString());
			} catch (FileTransferException e) {
				MessageDialog.openMessageDialog(null, "����ʧ�ܣ�"+e.toString());
			}
    	}
        return super.exporttipsreport(o);
    }
    /**
	 * Direction: ����ҵ������
	 * ename: downloadBus
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String downloadBus(Object o){
    	// �ͻ������ر��ĵ�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + "01" + dirsep+TimeFacade.getCurrentStringTime() + dirsep + loginfo.getSorgcode()
				+ dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String exportdate = searchdate==null?TimeFacade.getCurrentStringTime():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ������"+exportdate+"��ҵ������?")) {
    		try
    		{
    			String result = null;
    			Map getMap = checkStatusOfReportDownloadService.downloadbus(exportdate, busType);
    			result = String.valueOf(getMap.get("result"));
    			List<String> fileList = (List<String>)getMap.get("fileList");
    			if(fileList!=null&&fileList.size()>0)
    			{
    				File file = null;
    				for(int i=0;i<fileList.size();i++)
    				{
    					String clientfile = null;
    					if(fileList.get(i).indexOf(File.separator)>0)
    						clientfile=clientpath+fileList.get(i).substring(fileList.get(i).lastIndexOf(File.separator)+1);
    					else
    						clientfile=clientpath+fileList.get(i).substring(fileList.get(i).lastIndexOf("/")+1);
    					file = new File(clientfile);
    					if(!new File(file.getParent()).exists()){
            				new File(file.getParent()).mkdirs();
            			}
            			if(file.exists())
            				file.delete();
    					ClientFileTransferUtil.downloadFile(fileList.get(i),clientfile);
    				}
    			}
    			String line = System.getProperty("line.separator");
    			MessageDialog.openMessageDialog(null,"�����ļ��ɹ�,������Ŀ¼��"+clientpath+line+result);
    		} catch (ITFEBizException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"����ҵ������ʧ�ܣ�");
    		} catch (FileTransferException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"����ҵ������ʧ�ܣ�");
			}
    	}
		
        return super.downloadBus(o);
    }
    /**
	 * Direction: �������
	 * ename: blend
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String blend(Object o){
    	try {
    		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
    				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ�Ϲ���ȫʡ�������ݣ�")) {
			checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, "blend", null);
			MessageDialog.openMessageDialog(null,"�ѳɹ����ù��ҽӿ�,�����ʱ��鿴���ҽ����");
    		}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.blend(o);
    }
    /**
	 * Direction: ��������
	 * ename: exportParam
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportParam(Object o){
    	try {
    		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
    				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���������͵����ݽ���ƽ̨��?")) {
			checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, "exportparam", null);
			MessageDialog.openMessageDialog(null,"�ѳɹ����ò������ͽӿ�,�����ʱ��鿴���ͽ����");
    		}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.exportParam(o);
    }
    /**
	 * Direction: ������
	 * ename: exportReport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportReport(Object o){
    	try {
    		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
    				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ�������͵����ݽ���ƽ̨��?")) {
			checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, "exportreport", null);
			MessageDialog.openMessageDialog(null,"�ѳɹ����ñ����ͽӿ�,�����ʱ��鿴���ͽ����");
    		}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.exportReport(o);
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

	public List getTreCodeList() {
		return treCodeList;
	}

	public void setTreCodeList(List treCodeList) {
		this.treCodeList = treCodeList;
	}

	public String getSearchArea() {
		return searchArea;
	}
	 public void setStrecode(java.lang.String _strecode) {
	        strecode = _strecode;
	        if(strecode!=null&&!"".equals(strecode.trim()))
	        	searchArea = "";
	    }
	public void setSearchArea(String searchArea) {
		this.searchArea = searchArea;
		if(searchArea!=null&&!"".equals(searchArea.trim()))
    		strecode = "";
		editor.fireModelChanged();
	}
	public List getPagingList() {
		return pagingList;
	}
	public void setPagingList(List pagingList) {
		this.pagingList = pagingList;
	}
	public int getGkcount() {
		return gkcount;
	}
	public void setGkcount(int gkcount) {
		this.gkcount = gkcount;
	}
	public int getSrcount() {
		return srcount;
	}
	public void setSrcount(int srcount) {
		this.srcount = srcount;
	}
	public int getLscount() {
		return lscount;
	}
	public void setLscount(int lscount) {
		this.lscount = lscount;
	}
	public int getSpcount() {
		return spcount;
	}
	public void setSpcount(int spcount) {
		this.spcount = spcount;
	}
	public int getKccount() {
		return kccount;
	}
	public void setKccount(int kccount) {
		this.kccount = kccount;
	}
	public int getZccount() {
		return zccount;
	}
	public void setZccount(int zccount) {
		this.zccount = zccount;
	}
	public List getVoucherTypeList() {
		if(voucherTypeList==null)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.MSG_NO_5101, "ʵ���ʽ�ҵ��");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3208, "ʵ���ʽ��˿�");
			Mapper map2 = new Mapper(MsgConstant.MSG_NO_5102, "ֱ��֧�����");
			Mapper map3 = new Mapper(MsgConstant.MSG_NO_5103, "��Ȩ֧�����");
			Mapper map4 = new Mapper(MsgConstant.VOUCHER_NO_2301, "���뻮��ƾ֤");
			Mapper map5 = new Mapper(MsgConstant.VOUCHER_NO_2302, "�����˿�ƾ֤");
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
			voucherTypeList.add(map3);
			voucherTypeList.add(map4);
			voucherTypeList.add(map5);
		}
		return voucherTypeList;
	}
	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}
	public String getBusType() {
		return busType;
	}
	public void setBusType(String busType) {
		this.busType = busType;
	}
	public int getGdcount() {
		return gdcount;
	}
	public void setGdcount(int gdcount) {
		this.gdcount = gdcount;
	}
	public int getHgcount() {
		return hgcount;
	}
	public void setHgcount(int hgcount) {
		this.hgcount = hgcount;
	}
	public int getTkcount() {
		return tkcount;
	}
	public void setTkcount(int tkcount) {
		this.tkcount = tkcount;
	}

}