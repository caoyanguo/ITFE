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
 * ��ϵͳ: Para
 * ģ��:tsMtofAccpt
 * ���:TsMtofAccpt
 */
@SuppressWarnings("unchecked")
public class TsMtofAccptBean extends AbstractTsMtofAccptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsMtofAccptBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private TsSpecacctinfoDto searchdto;
    private TsSpecacctinfoDto updatedto;
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    private PagingContext paging ;
	private List<Mapper> sacctnaturelist;//�˻�����
    private List<Mapper> sbnktypelist;//���������
    private List<Mapper> sstateslist;//״̬
    private List<TsSpecacctinfoDto> checklist;//��ѡ
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
      tips="�ļ���ʽ:ֻ֧��*.csv����*.txt�ļ�\n";
      tips+="�ֶθ�ʽ:������,ר���˺�,ר������,�����к�,����������,������,�˻�����,��������,��������,��;,��ע,״̬\n";
      
    }
    
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	backresult = true;
    	updatedto = new TsSpecacctinfoDto();
    	updatedto.setSorgcode(loginfo.getSorgcode());
        return super.goInput(o);
    }
    /**
	 * Direction: ���ݵ���
	 * ename: uploadimport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String uploadimport(Object o){
        if(uploadfile==null||uploadfile.size()==0)
        {
        	MessageDialog.openMessageDialog(null, "��ѡ���ļ�!");
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
        			MessageDialog.openMessageDialog(null, "�ļ���ʽ����,ֻ֧��csv�ļ�����txt�ļ�!");
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
									MessageDialog.openMessageDialog(null, "�к�:"+(j+1)+fileList.get(j)+"���ݸ�ʽ����,ÿ��Ӧ��12�����ݣ�");
									return null;
								}
								tempdto = new TsSpecacctinfoDto();
								tempdto.setSorgcode(loginfo.getSorgcode());//�����������
								tempdto.setStrecode(fields[0]);//�����������
								tempdto.setSpayeeacct(fields[1]);//ר���˺�
								tempdto.setSpayeename(fields[2]);//ר������
								tempdto.setSopnbankcode(fields[3]);//�������к�
								tempdto.setSopnbankname(fields[4]);//����������
								if(fields[5]!=null&&(new String(fields[5].getBytes("GBK"),"iso-8859-1").length()>32))
									fields[5] = "��������";
								tempdto.setSext1(fields[5]);//���������
//								if(tranMap.get(fields[5])==null)
//								{
//									MessageDialog.openMessageDialog(null, "�к�:"+(j+1)+fields[5]+"���ݸ�ʽ����,���������:�й���������,�й�ũҵ����,�й�����,�й���������,��ͨ����,��ҵ����,��������,����ũ����ҵ����,�й��������,��������,��������,�й���������,ƽ������,�й�������������,���Ź�������,��������!");
//									return null;
//								}else
//								{
//									tempdto.setSbnktype(tranMap.get(fields[5]));//���������
//								}
								if(tranMap.get(fields[6])==null)
								{
									MessageDialog.openMessageDialog(null, "�к�:"+(j+1)+fields[6]+"���ݸ�ʽ����,�˻�����:һ�㻧,ר�û�,������");
									return null;
								}else
								{
									tempdto.setSacctnature(tranMap.get(fields[6]));//�˻�����
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
									tempdto.setSopendate(fields[7]);//��������
								else
								{
									MessageDialog.openMessageDialog(null, "�к�:"+(j+1)+fields[7]+"���ݸ�ʽ����,������������Ϊ8λ��Ϊ��!");
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
									tempdto.setSclosedate(fields[8]);//��������
								else
								{
									MessageDialog.openMessageDialog(null, "�к�:"+(j+1)+fields[8]+"���ݸ�ʽ����,������������Ϊ8λ��Ϊ��!");
									return null;
								}
								tempdto.setSacctused(fields[9]);//�˻���;
								tempdto.setSremark(fields[10]);//��ע
								if(tranMap.get(fields[11])==null)
								{
									MessageDialog.openMessageDialog(null, "�к�:"+(j+1)+fields[11]+"���ݸ�ʽ����,״̬:����,����");
									return null;
								}else
									tempdto.setSstates(tranMap.get(fields[11]));//״̬
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
						MessageDialog.openMessageDialog(null, "�ļ�����ʧ�ܣ�"+e.toString());
						return null;
					} catch (ITFEBizException e) {
						MessageDialog.openMessageDialog(null, "��������ʧ�ܣ�"+e.toString());
						return null;
					} catch (UnsupportedEncodingException e) {
						MessageDialog.openMessageDialog(null, "��������ʧ�ܣ�"+e.toString());
						return null;
					}
        		}
        	}
        }
        MessageDialog.openMessageDialog(null, "�������ݳɹ���");
        uploadfile.clear();
        return super.goBack(o);
    }
    
	/**
	 * Direction: �����ӡ
	 * ename: reportprint
	 * ���÷���: 
	 * viewers: �����ӡ
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
				MessageDialog.openMessageDialog(null, "��ѯ������!");
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
			reportmap.put("printDate", new SimpleDateFormat("yyyy��MM��dd��").format(new java.util.Date()));
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), new Exception(
							"��ѯƾ֤ҵ�������������쳣��", e));
		}
		backresult = true;
        return super.reportprint(o);
    }
    
	/**
	 * Direction: �����ݵ������
	 * ename: gouploadimport
	 * ���÷���: 
	 * viewers: ���ݵ���
	 * messages: 
	 */
    public String gouploadimport(Object o){
        
        return super.gouploadimport(o);
    }
    
	/**
	 * Direction: ��¼�����
	 * ename: searchgoinput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String searchgoinput(Object o){
    	backresult = false;
    	updatedto = new TsSpecacctinfoDto();
    	updatedto.setSorgcode(loginfo.getSorgcode());
        return super.searchgoinput(o);
    }
	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if(checklist==null||checklist.size()<=0)
    	{
        	MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵļ�¼��");
        	return "";
    	}
    	// ��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ��ļ�¼��")) {
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
	 * Direction: ���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
    	backresult = true;
    	if(checklist==null||checklist.size()<=0)
    	{
        	MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵļ�¼��");
        	return "";
    	}else if(checklist.size()==1)
    	{
    		updatedto = checklist.get(0);
    		updatedto.setSacctnature(tranMap.get(updatedto.getSacctnature()));
    		updatedto.setSstates(tranMap.get(updatedto.getSstates()));
    	}
    	else
    	{
    		MessageDialog.openMessageDialog(null, "ֻ��ѡ��һ����¼��");
    		return "";
    	}
    	if(updatedto==null||updatedto.getSorgcode()==null)
    	{
    		MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵļ�¼��");
    		return "";
    	}
    	
    	editor.fireModelChanged();
        return super.goModify(o);
    }

	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
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
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ��ѯ���
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
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	try {
			commonDataAccessService.create(updatedto);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "����ʧ�ܣ�"+e.toString());
		}
    	search(o);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	updatedto = (TsSpecacctinfoDto) o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String search(Object o){
    	  updatedto = new TsSpecacctinfoDto();
    	  PageRequest pageRequest = new PageRequest();
		  PageResponse pageResponse = retrieve(pageRequest);
		  paging.setPage(pageResponse);
		  if(pageResponse==null||pageResponse.getData()==null||pageResponse.getData().size()<=0)
		  {
			  MessageDialog.openMessageDialog(null, "��ѯ���ݽ��Ϊ�գ�");
			  return "";
		  }else
		  {
			  checklist = new ArrayList<TsSpecacctinfoDto>();
		  }
		  editor.fireModelChanged();
          return super.search(o);
    }

	/**
	 * Direction: ����
	 * ename: goBack
	 * ���÷���: 
	 * viewers: ��Ϣ��ѯ
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
			Mapper mapper = new Mapper("1","һ�㻧");
			tranMap.put(String.valueOf("sa"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sacctnaturelist.add(mapper);
			mapper = new Mapper("2","ר�û�");
			tranMap.put(String.valueOf("sa"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sacctnaturelist.add(mapper);
			mapper = new Mapper("3","������");
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
			Mapper mapper = new Mapper("1","�й���������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("2","�й�ũҵ����");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("3","�й�����");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("4","�й���������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("5","��ͨ����");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("6","��ҵ����");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("7","��������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("8","����ũ����ҵ����");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			tranMap.put( String.valueOf("������ũ����ҵ����"),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("9","�й��������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("10","��������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("11","��������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("12","�й���������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("13","ƽ������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("14","�й�������������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("15","���Ź�������");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			tranMap.put("�����й�������",String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("16","�й�ũҵ��չ����");
			tranMap.put(String.valueOf("sb"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sbnktypelist.add(mapper);
			mapper = new Mapper("17","��������");
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
			Mapper mapper = new Mapper("1","����");
			tranMap.put(String.valueOf("ss"+mapper.getUnderlyValue()), String.valueOf(mapper.getDisplayValue()));
			tranMap.put( String.valueOf(mapper.getDisplayValue()),String.valueOf(mapper.getUnderlyValue()));
			sstateslist.add(mapper);
			mapper = new Mapper("2","����");
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
	 * �����ж�ȡ�ļ���ÿ�и���split���зָ���ַ�������
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
			throw new FileOperateException("�Ҳ����ļ���", e1);
		}catch (Exception e) {
			log.error(e);
			throw new FileOperateException("��ȡ�ļ������쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("��ȡ�ļ������쳣", e);
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