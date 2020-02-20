package com.cfcc.itfe.client.dataquery.grantpayquery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.jce.provider.JDKGOST3410Signer.gost3410;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.page.HisMainGrantPayBean;
import com.cfcc.itfe.client.common.page.HisSubGrantPayBean;
import com.cfcc.itfe.client.common.page.MainGrantPayBean;
import com.cfcc.itfe.client.common.page.SubGrantPayBean;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 09-11-07 17:12:15 ��ϵͳ: DataQuery ģ��:grantPayQuery ���:GrantPayQuery
 */
public class GrantPayQueryBean extends AbstractGrantPayQueryBean implements
		IPageDataProvider {
	String startdate = TimeFacade.getCurrentStringTime();
	String enddate = TimeFacade.getCurrentStringTime();
	int pici = 0;
	private TvGrantpaymsgmainDto finddto;
	private String scommitdatestart;
	private String scommitdateend;
	private TvGrantpaymsgmainDto operdto;
	private HtvGrantpaymsgmainDto hisoperdto;
	private String sstatus;
	private List statelist ;
	private String selectedtable;
    private List statelist2;
    private ITFELoginInfo loginfo;
    private String expfunccode;
    private String payamt;

	/** �����б� */
	MainGrantPayBean mainGrantPayBean = null;
	SubGrantPayBean subGrantPayBean = null;
	HisMainGrantPayBean hisMainGrantPayBean=null;
	HisSubGrantPayBean hisSubGrantPayBean=null;

	public GrantPayQueryBean() {
		super();
		selectDataList = new ArrayList();
		finddto = new TvGrantpaymsgmainDto();
		operdto = new TvGrantpaymsgmainDto();
		hisoperdto=new HtvGrantpaymsgmainDto();
		 selectedtable="0";
		sstatus = null;
		
		initStatelist();
		scommitdatestart = TimeFacade.getCurrentStringTime();
		scommitdateend = TimeFacade.getCurrentStringTime();
		setMainGrantPayBean(new MainGrantPayBean(grantPayService, finddto));

		setSubGrantPayBean(new SubGrantPayBean(grantPayService, finddto , sstatus));
		setHisMainGrantPayBean(new HisMainGrantPayBean(grantPayService,new HtvGrantpaymsgmainDto()));
		setHisSubGrantPayBean(new HisSubGrantPayBean(grantPayService,new HtvGrantpaymsgmainDto(),sstatus));
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();

	}

	/**
	 * Direction: ��ѯ�б��¼� ename: searchList ���÷���: viewers: ��Ȩ֧���������Ϣ�б� messages:
	 */
	public String searchList(Object o) {
		PageRequest mainpageRequest = new PageRequest();
		PageResponse response=null;
		finddto.setSstatus(sstatus);
		StringBuffer where = null;
		if(scommitdatestart!=null&&!"".equals(scommitdatestart))
		{
			where = new StringBuffer("");
			where.append(" S_COMMITDATE >='"+scommitdatestart+"'");
		}
		if(scommitdateend!=null&&!"".equals(scommitdateend))
		{
			if(where==null)
			{
				where = new StringBuffer();
				where.append(" S_COMMITDATE <='"+scommitdateend+"'");
			}else
			{
				where.append(" and S_COMMITDATE <='"+scommitdateend+"'");
			}
		}
		if(where!=null)
			finddto.setSdemo(where.toString());
		else
			finddto.setSdemo(null);
		if(selectedtable.equals("0")){
			this.getMainGrantPayBean().setMaindto(finddto);
			this.getMainGrantPayBean().setExpfunccode(expfunccode);
			this.getMainGrantPayBean().setPayamt(payamt);
			response = this.getMainGrantPayBean().retrieve(mainpageRequest);
		}else if(selectedtable.equals("1")){
			HtvGrantpaymsgmainDto htvdto=new HtvGrantpaymsgmainDto();
			htvdto.setStrecode(finddto.getStrecode());
			htvdto.setSpackageticketno(finddto.getSpackageticketno());
			htvdto.setSlimitid(finddto.getSlimitid());
			htvdto.setScommitdate(finddto.getScommitdate());
			htvdto.setSpayunit(finddto.getSpayunit());
			htvdto.setNmoney(finddto.getNmoney());
			htvdto.setSpackageno(finddto.getSpackageno());
			htvdto.setSofyear(finddto.getSofyear());
			htvdto.setSfilename(finddto.getSfilename());
			htvdto.setSstatus(finddto.getSstatus());
			htvdto.setSdemo(finddto.getSdemo());
			this.getHisMainGrantPayBean().setMaindto(htvdto);
			this.getHisMainGrantPayBean().setExpfunccode(expfunccode);
			this.getHisMainGrantPayBean().setPayamt(payamt);
			response=this.getHisMainGrantPayBean().retrieve(mainpageRequest);
			if (response.getTotalCount() <= 0) {
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
				return super.rebackSearch(o);
			}
			
			editor.fireModelChanged();
			return "��Ȩ֧���������Ϣ�б�(��ʷ��)";
		}
		
		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.rebackSearch(o);
		}
		
		editor.fireModelChanged();
		operdto = new TvGrantpaymsgmainDto();
		return "��Ȩ֧���������Ϣ�б�";
	}

	/**
	 * Direction: ���������¼� ename: reSendMsg ���÷���: viewers: * messages:
	 */
	public String reSendMsg(Object o) {
		if (null == operdto || null == operdto.getSpackageno()
				|| "".equals(operdto.getSpackageno().trim())) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�ط����ĵļ�¼��");
			return super.reSendMsg(o);
		}

		try {
			grantPayService.reSendMsg(operdto.getScommitdate(), operdto
					.getSpackageno(), operdto.getSorgcode(), operdto
					.getSfilename());
			MessageDialog.openMessageDialog(null, "�ط����ĳɹ���[����ˮ�ţ�"
					+ operdto.getSpackageno() + "]");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.reSendMsg(o);
	}

	/**
	 * Direction: ���ز�ѯ���� ename: rebackSearch ���÷���: viewers: ��Ȩ֧����Ȳ�ѯ���� messages:
	 */
	public String rebackSearch(Object o) {
		this.operdto=new TvGrantpaymsgmainDto()	;
		this.hisoperdto=new HtvGrantpaymsgmainDto();
		setSubGrantPayBean(new SubGrantPayBean(grantPayService, new TvGrantpaymsgmainDto() , sstatus));
		setHisSubGrantPayBean(new HisSubGrantPayBean(grantPayService,new HtvGrantpaymsgmainDto(),sstatus));
		return super.rebackSearch(o);
	}

	/**
	 * Direction: ����Ϣ�����¼� ename: singleclickMain ���÷���: viewers: * messages:
	 */
	public String singleclickMain(Object o) {
		PageRequest subpageRequest = new PageRequest();
		
		if(selectedtable.equals("0")){
			operdto = (TvGrantpaymsgmainDto) o;
			this.getSubGrantPayBean().setMaindto(operdto);
			this.getSubGrantPayBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvGrantpaymsgmainDto)o;
			this.getHisSubGrantPayBean().setMaindto(hisoperdto);
			this.getHisSubGrantPayBean().retrieve(subpageRequest);
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
			operdto = (TvGrantpaymsgmainDto) o;
			this.getSubGrantPayBean().setMaindto(operdto);
			this.getSubGrantPayBean().setExpfunccode(expfunccode);
			this.getSubGrantPayBean().setPayamt(payamt);
			this.getSubGrantPayBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvGrantpaymsgmainDto)o;
			this.getHisSubGrantPayBean().setMaindto(hisoperdto);
			this.getHisSubGrantPayBean().setExpfunccode(expfunccode);
			this.getHisSubGrantPayBean().setPayamt(payamt);
			this.getHisSubGrantPayBean().retrieve(subpageRequest);
		}
		
		editor.fireModelChanged();
        return super.doubleclickMain(o);
    }
	
	/**
	 * Direction: ����
	 * ename: dataExport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String clientFilePath;
		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		String dirsep = File.separator;
		String serverFilePath;
		try {
			StringBuffer where = null;
			if(scommitdatestart!=null&&!"".equals(scommitdatestart))
			{
				where = new StringBuffer("");
				where.append(" S_COMMITDATE >='"+scommitdatestart+"'");
			}
			if(scommitdateend!=null&&!"".equals(scommitdateend))
			{
				if(where==null)
				{
					where = new StringBuffer();
					where.append(" S_COMMITDATE <='"+scommitdateend+"'");
				}else
				{
					where.append(" and S_COMMITDATE <='"+scommitdateend+"'");
				}
			}
			if(where!=null)
				finddto.setSdemo(where.toString());
			else
				finddto.setSdemo(null);
			serverFilePath = grantPayService.dataexport(finddto,selectedtable).replace("\\",
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
	 * ename: exportSelectData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportSelectData(Object o){
    	if(selectDataList.size()==0){
    		MessageDialog.openMessageDialog(null, "δѡ��ص�����ѡ��Ҫ�����Ļص���");
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
		String serverFilePath;
		try {
			serverFilePath = dataexport(finddto,selectedtable, selectDataList,filePath).replace("\\",
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
    
    public String dataexport(IDto mainDto, String selectedtable,
			List selectDataList,String filepath) throws ITFEBizException {
		
		String filename = "";

		try {
			startdate = TimeFacade.getCurrentStringTime();
			if (startdate.equals(enddate))
				pici++;
			else
				enddate = TimeFacade.getCurrentStringTime();
			if (selectedtable.equals("0")) {
				filename = TimeFacade.getCurrentStringTime() + "02"
						+ (pici < 10 ? "0" + pici : pici) + "hd.txt";
			} else if (selectedtable.equals("1")) {
				filename = TimeFacade.getCurrentStringTime() + "02"
						+ (pici < 10 ? "0" + pici : pici) + "hd.txt";
			}
//			String root = "/itfe/root/temp/";// ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = filepath + dirsep + filename;
			String splitSign = ",";// "\t"; // �ļ���¼�ָ�����
			StringBuffer filebuf = new StringBuffer();
			if (selectedtable.equals("0")) {
				for (TvGrantpaymsgmainDto _dto : (List<TvGrantpaymsgmainDto>) selectDataList) {
					filebuf.append(_dto.getStrecode());// �������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpackageticketno());// ƾ֤���
					filebuf.append(splitSign);
					filebuf.append(_dto.getStransactunit());// �������д��� ���쵥λ
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetunitcode());// Ԥ�㵥λ����
					filebuf.append(splitSign);
					String months = _dto.getSofmonth();
					if (months.trim().length() == 1) {
						months = "0" + months.trim();
					}
					filebuf.append(months);// �����·�
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());// Ԥ������
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());// ��������
					filebuf.append(splitSign);
					filebuf.append("0.00");// С���ֽ�����
					filebuf.append(splitSign);
					if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
							.getSstatus()))
						filebuf.append("1");// ������1�ɹ���0ʧ��
					else
						filebuf.append("0");// ������1�ɹ���0ʧ��
					filebuf.append(splitSign);
					filebuf.append(_dto.getSdemo());// ˵��
					filebuf.append("\r\n");
				}
			} else if (selectedtable.equals("1")) {
				for (HtvGrantpaymsgmainDto _dto : (List<HtvGrantpaymsgmainDto>) selectDataList) {
					filebuf.append(_dto.getStrecode());// �������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpackageticketno());// ƾ֤���
					filebuf.append(splitSign);
					filebuf.append(_dto.getStransactunit());// �������д���
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetunitcode());// Ԥ�㵥λ����
					filebuf.append(splitSign);
					String months = _dto.getSofmonth();
					if (months.length() == 1) {
						months = "0" + months;
					}
					filebuf.append(months);// �����·�
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());// Ԥ������
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());// ��������
					filebuf.append(splitSign);
					filebuf.append("0.00");// С���ֽ�����
					filebuf.append(splitSign);
					if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
							.getSstatus()))
						filebuf.append("1");// ������1�ɹ���0ʧ��
					else
						filebuf.append("0");// ������1�ɹ���0ʧ��
					filebuf.append(splitSign);
					filebuf.append(_dto.getSdemo());// ˵��
					filebuf.append("\r\n");
				}
			}
			File f = new File(fullpath);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(fullpath);
			}
			FileUtil.getInstance().writeFile(
					fullpath,
					filebuf.toString().substring(0,
							filebuf.toString().length() - 2));
			return fullpath;

		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����", e);
		}
	}
    
    
    
    
    /**
	 * Direction: �������ӱ���Ϣ
	 * ename: exportFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
	public String exportFile(Object o) {
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·��!");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
		try {
			serverFilePath = grantPayService.exportFile(finddto,selectedtable).replace("\\","/");
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

	public TvGrantpaymsgmainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvGrantpaymsgmainDto finddto) {
		this.finddto = finddto;
	}

	public TvGrantpaymsgmainDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvGrantpaymsgmainDto operdto) {
		this.operdto = operdto;
	}

	public MainGrantPayBean getMainGrantPayBean() {
		return mainGrantPayBean;
	}

	public void setMainGrantPayBean(MainGrantPayBean mainGrantPayBean) {
		this.mainGrantPayBean = mainGrantPayBean;
	}

	public SubGrantPayBean getSubGrantPayBean() {
		return subGrantPayBean;
	}

	public void setSubGrantPayBean(SubGrantPayBean subGrantPayBean) {
		this.subGrantPayBean = subGrantPayBean;
	}

	public String getSstatus() {
		return sstatus;
	}

	public void setSstatus(String sstatus) {
		this.sstatus = sstatus;
	}

	public List getStatelist() {
		return statelist;
	}

	public void setStatelist(List statelist) {
		this.statelist = statelist;
	}

	public HtvGrantpaymsgmainDto getHisoperdto() {
		return hisoperdto;
	}

	public void setHisoperdto(HtvGrantpaymsgmainDto hisoperdto) {
		this.hisoperdto = hisoperdto;
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

	public HisMainGrantPayBean getHisMainGrantPayBean() {
		return hisMainGrantPayBean;
	}

	public void setHisMainGrantPayBean(HisMainGrantPayBean hisMainGrantPayBean) {
		this.hisMainGrantPayBean = hisMainGrantPayBean;
	}

	public HisSubGrantPayBean getHisSubGrantPayBean() {
		return hisSubGrantPayBean;
	}

	public void setHisSubGrantPayBean(HisSubGrantPayBean hisSubGrantPayBean) {
		this.hisSubGrantPayBean = hisSubGrantPayBean;
	}

	public String getScommitdatestart() {
		return scommitdatestart;
	}

	public void setScommitdatestart(String scommitdatestart) {
		this.scommitdatestart = scommitdatestart;
	}

	public String getScommitdateend() {
		return scommitdateend;
	}

	public void setScommitdateend(String scommitdateend) {
		this.scommitdateend = scommitdateend;
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