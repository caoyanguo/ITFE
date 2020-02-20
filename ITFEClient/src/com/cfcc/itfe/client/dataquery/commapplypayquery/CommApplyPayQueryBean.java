package com.cfcc.itfe.client.dataquery.commapplypayquery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.page.HisMainCommApplyPayBean;
import com.cfcc.itfe.client.common.page.HisSubCommApplyPayBean;
import com.cfcc.itfe.client.common.page.MainCommApplyPayBean;
import com.cfcc.itfe.client.common.page.SubCommApplyPayBean;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-03-04 21:08:15
 * ��ϵͳ: DataQuery
 * ģ��:commApplyPayQuery
 * ���:CommApplyPayQuery
 */
@SuppressWarnings("unchecked")
public class CommApplyPayQueryBean extends AbstractCommApplyPayQueryBean implements IPageDataProvider {
	@SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(CommApplyPayQueryBean.class);
    
    private TvPayreckBankDto finddto;
    private TvPayreckBankDto operdto;
    private HtvPayreckBankDto hisoperdto;
	private List statelist ;
	private String selectedtable;
	private List statelist2 ;
	private ITFELoginInfo loginfo;
	private String dentrustdate=null;
	private String dvoudate=null;
	private java.sql.Date dentrustdatestart=null;
	private java.sql.Date dentrustdateend = null;
	private String expfunccode;
	private String payamt;
	String startdate = TimeFacade.getCurrentStringTime();
	String enddate = TimeFacade.getCurrentStringTime();
	int pici = 0;
	MainCommApplyPayBean mainCommApplyPayBean = null;
	SubCommApplyPayBean subCommApplyPayBean = null;
	HisMainCommApplyPayBean hisMainCommApplyPayBean = null;
	HisSubCommApplyPayBean hisSubCommApplyPayBean = null;
	
	
    public CommApplyPayQueryBean() {
      super();
      selecteddatalist = new ArrayList();
      selectedtable="0";
		finddto = new TvPayreckBankDto();
		operdto = new TvPayreckBankDto();
		hisoperdto=new HtvPayreckBankDto();
		finddto.setSpaymode("0");		
		initStatelist();
		setMainCommApplyPayBean(new MainCommApplyPayBean(commApplyPayQueryService, finddto));

		setSubCommApplyPayBean(new SubCommApplyPayBean(commApplyPayQueryService, finddto));
		setHisMainCommApplyPayBean(new HisMainCommApplyPayBean(commApplyPayQueryService,new HtvPayreckBankDto()));
		setHisSubCommApplyPayBean(new HisSubCommApplyPayBean(commApplyPayQueryService,new HtvPayreckBankDto()));
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
		dentrustdate=TimeFacade.getCurrentStringTime();
		dentrustdatestart=CommonUtil.strToDate(dentrustdate);
		dentrustdateend=CommonUtil.strToDate(dentrustdate);
    }
    
	/**
	 * Direction: ��ѯ�б��¼�
	 * ename: searchList
	 * ���÷���: 
	 * viewers: ���а���֧����������������Ϣ�б�
	 * messages: 
	 */
    public String searchList(Object o){
    	if(finddto.getSpaymode()==null||"".equals(finddto.getSpaymode()))
    	{
    		MessageDialog.openMessageDialog(null, "֧����ʽ����Ϊ��!");
    		return "";
    	}
    	StringBuffer where = null;
		if(dentrustdatestart!=null&&!"".equals(dentrustdatestart))
		{
			where = new StringBuffer("");
			where.append(" D_ENTRUSTDATE >='"+dentrustdatestart+"'");
		}
		if(dentrustdateend!=null&&!"".equals(dentrustdateend))
		{
			if(where==null)
			{
				where = new StringBuffer();
				where.append(" D_ENTRUSTDATE <='"+dentrustdateend+"'");
			}else
			{
				where.append(" and D_ENTRUSTDATE <='"+dentrustdateend+"'");
			}
		}
		if(where!=null)
			finddto.setSdescription(where.toString());
		else
			finddto.setSdescription(null);
    	PageRequest mainpageRequest = new PageRequest();
		PageResponse response=null;
		if(selectedtable.equals("0")){
			this.getMainCommApplyPayBean().setMaindto(finddto);
			this.getMainCommApplyPayBean().setExpfunccode(expfunccode);
			this.getMainCommApplyPayBean().setPayamt(payamt);
			response = this.getMainCommApplyPayBean().retrieve(mainpageRequest);
		}else if(selectedtable.equals("1")){
			HtvPayreckBankDto htvdto=new HtvPayreckBankDto();
			htvdto.setStrecode(finddto.getStrecode());
			htvdto.setDentrustdate(finddto.getDentrustdate());
			htvdto.setSpackno(finddto.getSpackno());
			htvdto.setSfinorgcode(finddto.getSfinorgcode());
			htvdto.setSagentbnkcode(finddto.getSagentbnkcode());
			htvdto.setStrano(finddto.getStrano());
			htvdto.setSvouno(finddto.getSvouno());
			htvdto.setDvoudate(finddto.getDvoudate());
			htvdto.setFamt(finddto.getFamt());
			htvdto.setSpayeracct(finddto.getSpayeracct());
			htvdto.setSpayeeacct(finddto.getSpayeeacct());
			htvdto.setSpayeeopbkno(finddto.getSpayeeopbkno());
			htvdto.setSofyear(finddto.getSofyear());
			htvdto.setSprocstat(finddto.getSprocstat());
			htvdto.setStrimsign(finddto.getStrimsign());
			htvdto.setSbudgettype(finddto.getSbudgettype());
			htvdto.setSpaymode(finddto.getSpaymode());
			htvdto.setSdescription(finddto.getSdescription());
			this.getHisMainCommApplyPayBean().setMaindto(htvdto);
			this.getHisMainCommApplyPayBean().setExpfunccode(expfunccode);
			this.getHisMainCommApplyPayBean().setPayamt(payamt);
			response=this.getHisMainCommApplyPayBean().retrieve(mainpageRequest);
			if (response.getTotalCount() <= 0) {
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");				
				return super.rebackSearch(o);
			}
			editor.fireModelChanged();
			return "���а���֧����������������Ϣ�б�(��ʷ��)";
		}
		
		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.rebackSearch(o);
		}
		editor.fireModelChanged();
		operdto=new TvPayreckBankDto();
        return super.searchList(o);
    }

	/**
	 * Direction: ���ز�ѯ����
	 * ename: rebackSearch
	 * ���÷���: 
	 * viewers: ���а���֧�����������ѯ����
	 * messages: 
	 */
    public String rebackSearch(Object o){
    	if(this.getSubCommApplyPayBean().retrieve(new PageRequest())!=null)
    		this.getSubCommApplyPayBean().retrieve(new PageRequest()).setData(new ArrayList());
    	if(this.getHisSubCommApplyPayBean().retrieve(new PageRequest())!=null)
    		this.getHisSubCommApplyPayBean().retrieve(new PageRequest()).setData(new ArrayList());
        return super.rebackSearch(o);
    }


	/**
	 * Direction: ����Ϣ�����¼�
	 * ename: singleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
    	PageRequest subpageRequest = new PageRequest();
		
		if(selectedtable.equals("0")){
			operdto = (TvPayreckBankDto) o;
			this.getSubCommApplyPayBean().setMaindto(operdto);
			this.getSubCommApplyPayBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvPayreckBankDto)o;
			this.getHisSubCommApplyPayBean().setMaindto(hisoperdto);
			this.getHisSubCommApplyPayBean().retrieve(subpageRequest);
		}
		
		editor.fireModelChanged();
          return super.singleclickMain(o);
    }
    
    /**
	 * Direction: ����Ϣ˫���¼�
	 * ename: doubleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
    	PageRequest subpageRequest = new PageRequest();
		
		if(selectedtable.equals("0")){
			operdto = (TvPayreckBankDto) o;
			this.getSubCommApplyPayBean().setMaindto(operdto);
			this.getSubCommApplyPayBean().setExpfunccode(expfunccode);
			this.getSubCommApplyPayBean().setPayamt(payamt);
			this.getSubCommApplyPayBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvPayreckBankDto)o;
			this.getHisSubCommApplyPayBean().setMaindto(hisoperdto);
			this.getHisSubCommApplyPayBean().setExpfunccode(expfunccode);
			this.getHisSubCommApplyPayBean().setPayamt(payamt);
			this.getHisSubCommApplyPayBean().retrieve(subpageRequest);
		}
		
		editor.fireModelChanged();
        return super.doubleclickMain(o);
    }
    
    private void initStatelist(){
		this.statelist = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("������");
		valuedto3.setSvalue(DealCodeConstants.DEALCODE_ITFE_DEALING);
		this.statelist.add(valuedto3);
		
		TdEnumvalueDto valuedto4 = new TdEnumvalueDto();
		valuedto4.setStypecode("������");
		valuedto4.setSvalue(DealCodeConstants.DEALCODE_ITFE_RECEIVER);
		this.statelist.add(valuedto4);
		    
		TdEnumvalueDto valuedto1 = new TdEnumvalueDto();
		valuedto1.setStypecode("�ɹ�");
		valuedto1.setSvalue(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
		this.statelist.add(valuedto1);
		
		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("ʧ��");
		valuedto2.setSvalue(DealCodeConstants.DEALCODE_ITFE_FAIL);
		this.statelist.add(valuedto2);
		
		TdEnumvalueDto valuedto5 = new TdEnumvalueDto();
		valuedto5.setStypecode("���ط�");
		valuedto5.setSvalue(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND);
		this.statelist.add(valuedto5);
		
		this.statelist2 = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
		valuedtoa.setStypecode("��ǰ��");
		valuedtoa.setSvalue("0");
		this.statelist2.add(valuedtoa);
		
		TdEnumvalueDto valuedtob = new TdEnumvalueDto();
		valuedtob.setStypecode("��ʷ��");
		valuedtob.setSvalue("1");
		this.statelist2.add(valuedtob);
	}
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public TvPayreckBankDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvPayreckBankDto finddto) {
		this.finddto = finddto;
	}

	public TvPayreckBankDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvPayreckBankDto operdto) {
		this.operdto = operdto;
	}

	public HtvPayreckBankDto getHisoperdto() {
		return hisoperdto;
	}

	public void setHisoperdto(HtvPayreckBankDto hisoperdto) {
		this.hisoperdto = hisoperdto;
	}

	public List getStatelist() {
		return statelist;
	}

	public void setStatelist(List statelist) {
		this.statelist = statelist;
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List getStatelist2() {
		return statelist2;
	}

	public void setStatelist2(List statelist2) {
		this.statelist2 = statelist2;
	}

	public MainCommApplyPayBean getMainCommApplyPayBean() {
		return mainCommApplyPayBean;
	}

	public void setMainCommApplyPayBean(MainCommApplyPayBean mainCommApplyPayBean) {
		this.mainCommApplyPayBean = mainCommApplyPayBean;
	}

	public SubCommApplyPayBean getSubCommApplyPayBean() {
		return subCommApplyPayBean;
	}

	public void setSubCommApplyPayBean(SubCommApplyPayBean subCommApplyPayBean) {
		this.subCommApplyPayBean = subCommApplyPayBean;
	}

	public HisMainCommApplyPayBean getHisMainCommApplyPayBean() {
		return hisMainCommApplyPayBean;
	}

	public void setHisMainCommApplyPayBean(
			HisMainCommApplyPayBean hisMainCommApplyPayBean) {
		this.hisMainCommApplyPayBean = hisMainCommApplyPayBean;
	}

	public HisSubCommApplyPayBean getHisSubCommApplyPayBean() {
		return hisSubCommApplyPayBean;
	}

	public void setHisSubCommApplyPayBean(
			HisSubCommApplyPayBean hisSubCommApplyPayBean) {
		this.hisSubCommApplyPayBean = hisSubCommApplyPayBean;
	}
	/**
	 * Direction: ����txt
	 * ename: exportCommApplyPay
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportCommApplyPay(Object o){
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
//		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String clientFilePath;
//		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		String dirsep = File.separator;
		String serverFilePath;
		try {
			StringBuffer where = null;
			if(dentrustdatestart!=null&&!"".equals(dentrustdatestart))
			{
				where = new StringBuffer("");
				where.append(" D_ENTRUSTDATE >='"+dentrustdatestart+"'");
			}
			if(dentrustdateend!=null&&!"".equals(dentrustdateend))
			{
				if(where==null)
				{
					where = new StringBuffer();
					where.append(" D_ENTRUSTDATE <='"+dentrustdateend+"'");
				}else
				{
					where.append(" and D_ENTRUSTDATE <='"+dentrustdateend+"'");
				}
			}
			if(where!=null)
				finddto.setSdescription(where.toString());
			else
				finddto.setSdescription(null);
			serverFilePath = commApplyPayQueryService.exportCommApplyPay(finddto,selectedtable).replace("\\",
					"/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(
					0, j + 1), "");
			clientFilePath = filePath + dirsep
					+ partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			String key = commonDataAccessService.getModeKey(loginfo.getSorgcode(), "1");
			SM3Process.addSM3Sign(clientFilePath, key);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return "";
    }
    /**
	 * Direction: ����ѡ�лص�
	 * ename: exportPartCommApplyPay
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportPartCommApplyPay(Object o){
    	if(selecteddatalist.size()==0){
    		MessageDialog.openMessageDialog(null, "δѡ��ص�����ѡ��Ҫ�����Ļص���");
			return "";
    	}
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
//		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String clientFilePath;
//		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		String dirsep = File.separator;
		String serverFilePath;
		try {
			serverFilePath = exportPartCommApplyPay(selecteddatalist, selectedtable, finddto,filePath).replace("\\",
					"/");
			String key = commonDataAccessService.getModeKey(loginfo.getSorgcode(), "1");
			SM3Process.addSM3Sign(serverFilePath, key);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"
					+ serverFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return "";
    }
    
    /**
	 * ����ѡ�лص�	 
	 * @generated
	 * @param selecteddatalist
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
	public String exportPartCommApplyPay(List selecteddatalist,String selectedtable,IDto finddto,String filePath)
			throws ITFEBizException {
		String filename="";
		
		try {
//			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			if(selectedtable.equals("0")){
				TvPayreckBankDto mdto=(TvPayreckBankDto)finddto;
				startdate = TimeFacade.getCurrentStringTime();
				if(startdate.equals(enddate))
					pici++;
				else
					enddate = TimeFacade.getCurrentStringTime();
				if("0".equals(mdto.getSpaymode()))//0ֱ��1��Ȩ
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+(pici<10?"0"+pici:pici)+"hd.txt";
				else
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+(pici<10?"0"+pici:pici)+"hd.txt";
			}else if(selectedtable.equals("1")){
				HtvPayreckBankDto mdto=(HtvPayreckBankDto)finddto;
				if("0".equals(mdto.getSpaymode()))//0ֱ��1��Ȩ
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+"hd.txt";
				else
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+"hd.txt";
			}
			
//			String root = "/itfe/root/temp/";//ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = filePath + dirsep + filename;
			String splitSign = ",";//"\t"; // �ļ���¼�ָ�����
			if (selecteddatalist.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					TvPayreckBankListDto subPara = null;
					for (TvPayreckBankDto _dto :(List<TvPayreckBankDto>) selecteddatalist) {
						subPara = new TvPayreckBankListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
//						if(list.size()>1)
//							filebuf.append("**");
						filebuf.append(_dto.getStrecode());//����������
						filebuf.append(splitSign+_dto.getSpayername());//����������
						filebuf.append(splitSign+_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign+"");//�����˿����д���
						filebuf.append(splitSign+"");//�����˿������к�
//						String bankname = bankMap.get(_dto.getSrecbankno()).getSbankname();//��������
						filebuf.append(splitSign+_dto.getSpayeename());//�տ�������
						filebuf.append(splitSign+_dto.getSpayeeacct());//�տ����˺�
						filebuf.append(splitSign+_dto.getSagentbnkcode());//�������д���
						filebuf.append(splitSign+_dto.getSpayeeopbkno());//�տ��˿������к�
						filebuf.append(splitSign+_dto.getSbudgettype());//Ԥ���������
						filebuf.append(splitSign+_dto.getFamt());//��������
						filebuf.append(splitSign+"0.00");//С���ֽ�����
						filebuf.append(splitSign);//ժҪ����
						filebuf.append(splitSign+_dto.getSvouno());//ƾ֤���
						String dates = _dto.getDvoudate().toString();
						filebuf.append(splitSign+dates.replaceAll("-", ""));//ƾ֤����
						filebuf.append(splitSign+_dto.getSaddword());//����
						filebuf.append(splitSign);//�Ƿ�ɹ�
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSresult()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);//ԭ��
						filebuf.append("\r\n");
					}
				}else if(selectedtable.equals("1")){
					HtvPayreckBankListDto subPara = null;
					for (HtvPayreckBankDto _dto :(List<HtvPayreckBankDto>) selecteddatalist) {
						subPara = new HtvPayreckBankListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
						if(selecteddatalist.size()>1)
							filebuf.append("**");
						filebuf.append(_dto.getStrecode());//����������
						filebuf.append(splitSign+_dto.getSpayername());//����������
						filebuf.append(splitSign+_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign+"");//�����˿����д���
						filebuf.append(splitSign+"");//�����˿������к�
						filebuf.append(splitSign+_dto.getSpayeename());//�տ�������
						filebuf.append(splitSign+_dto.getSpayeeacct());//�տ����˺�
						filebuf.append(splitSign+_dto.getSagentbnkcode());//�������д���
						filebuf.append(splitSign+_dto.getSpayeeopbkno());//�տ��˿������к�
						filebuf.append(splitSign+_dto.getSbudgettype());//Ԥ���������
						filebuf.append(splitSign+_dto.getFamt());//��������
						filebuf.append(splitSign+"0.00");//С���ֽ�����
						filebuf.append(splitSign);//ժҪ����
						filebuf.append(splitSign+_dto.getSvouno());//ƾ֤���
						filebuf.append(splitSign+_dto.getDvoudate().toString().replaceAll("-", ""));//ƾ֤����
						filebuf.append(splitSign+_dto.getSaddword());//����
						filebuf.append(splitSign);//�Ƿ�ɹ�
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSresult()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign+_dto.getSresult()+_dto.getSdescription());//ԭ��
						filebuf.append("\r\n");
					}
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
			    return fullpath;
				
			} else {
				throw new ITFEBizException("��ѯ������");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����",e);
		}
	}

	public String getDentrustdate() {
		return dentrustdate;
	}

	public void setDentrustdate(String dentrustdate) {
		this.dentrustdate = dentrustdate;
		if(finddto!=null)
			finddto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dentrustdate));
		if(operdto!=null)
			operdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dentrustdate));		
		if(hisoperdto!=null)
			hisoperdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dentrustdate));		
	}

	public String getDvoudate() {
		return dvoudate;
	}

	public void setDvoudate(String dvoudate) {
		this.dvoudate = dvoudate;
		if(finddto!=null)
			finddto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dvoudate));
		if(operdto!=null)
			operdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dvoudate));		
		if(hisoperdto!=null)
			hisoperdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dvoudate));	
	}

	/**
	 * Direction: ����CSV
	 * ename: exportFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
	public String exportFile(Object o) {
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
		try {
			serverFilePath = commApplyPayQueryService.exportFile(finddto,selectedtable).replace("\\","/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"+ clientFilePath);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
	}

	public java.sql.Date getDentrustdatestart() {
		return dentrustdatestart;
	}

	public void setDentrustdatestart(java.sql.Date dentrustdatestart) {
		this.dentrustdatestart = dentrustdatestart;
	}

	public java.sql.Date getDentrustdateend() {
		return dentrustdateend;
	}

	public void setDentrustdateend(java.sql.Date dentrustdateend) {
		this.dentrustdateend = dentrustdateend;
	}

	public String getExpfunccode() {
		return expfunccode;
	}

	public void setExpfunccode(String expfunccode) {
		this.expfunccode = expfunccode;
	}

	public String getPayamt() {
		return payamt;
	}

	public void setPayamt(String payamt) {
		this.payamt = payamt;
	}
	
	
	
}