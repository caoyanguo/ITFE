package com.cfcc.itfe.client.dataquery.directpayquery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.page.HisMainDirectPayBean;
import com.cfcc.itfe.client.common.page.HisSubDirectPayBean;
import com.cfcc.itfe.client.common.page.MainDirectPayBean;
import com.cfcc.itfe.client.common.page.SubDirectPayBean;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.SM3Util;
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
 * @time 09-11-07 14:32:31 ��ϵͳ: DataQuery ģ��:directPayQuery ���:DirectPayQuery
 */
public class DirectPayQueryBean extends AbstractDirectPayQueryBean implements
		IPageDataProvider {
	String startdate = TimeFacade.getCurrentStringTime();
	String enddate = TimeFacade.getCurrentStringTime();
	int pici = 0;
	private TvDirectpaymsgmainDto finddto;
	private String scommitdatestart;
	private String scommitdateend;
	private TvDirectpaymsgmainDto operdto;
	private HtvDirectpaymsgmainDto hisoperdto;
	private List statelist ;
	private String selectedtable;
	private List statelist2 ;
	private ITFELoginInfo loginfo;
	private String expfunccode;
	private String payamt;

	/** �����б� */
	MainDirectPayBean mainDirectPayBean = null;
	SubDirectPayBean subDirectPayBean = null;
	HisMainDirectPayBean hisMainDirectPayBean=null;
	HisSubDirectPayBean hisSubDirectPayBean=null;

	public DirectPayQueryBean() {
		super();
		selectedtable="0";
		selectDataList = new ArrayList();
		finddto = new TvDirectpaymsgmainDto();
		operdto = new TvDirectpaymsgmainDto();
		hisoperdto=new HtvDirectpaymsgmainDto();
		initStatelist();
		scommitdatestart =TimeFacade.getCurrentStringTime();
		scommitdateend =TimeFacade.getCurrentStringTime();
		setMainDirectPayBean(new MainDirectPayBean(directPayService, finddto));

		setSubDirectPayBean(new SubDirectPayBean(directPayService, finddto));
		setHisMainDirectPayBean(new HisMainDirectPayBean(directPayService,new HtvDirectpaymsgmainDto()));
		setHisSubDirectPayBean(new HisSubDirectPayBean(directPayService,new HtvDirectpaymsgmainDto()));
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
	}

	/**
	 * Direction: ��ѯ�б��¼� ename: searchList ���÷���: viewers: ֱ��֧���������Ϣ�б� messages:
	 */
	public String searchList(Object o) {

		PageRequest mainpageRequest = new PageRequest();
		PageResponse response=null;
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
			this.getMainDirectPayBean().setMaindto(finddto);
			this.getMainDirectPayBean().setExpfunccode(expfunccode);
			this.getMainDirectPayBean().setPayamt(payamt);
			response = this.getMainDirectPayBean().retrieve(mainpageRequest);
		}else if(selectedtable.equals("1")){
			HtvDirectpaymsgmainDto htvdto=new HtvDirectpaymsgmainDto();
			htvdto.setStrecode(finddto.getStrecode());
			htvdto.setScommitdate(finddto.getScommitdate());
			htvdto.setSaccdate(finddto.getSaccdate());
			htvdto.setSpayunit(finddto.getSpayunit());
			htvdto.setStransbankcode(finddto.getStransbankcode());
			htvdto.setNmoney(finddto.getNmoney());
			htvdto.setStaxticketno(finddto.getStaxticketno());
			htvdto.setSofyear(finddto.getSofyear());
			htvdto.setSpackageno(finddto.getSpackageno());
			htvdto.setSdealno(finddto.getSdealno());
			htvdto.setSfilename(finddto.getSfilename());
			htvdto.setSstatus(finddto.getSstatus());
			htvdto.setSdemo(finddto.getSdemo());
			this.getHisMainDirectPayBean().setMaindto(htvdto);
			this.getHisMainDirectPayBean().setExpfunccode(expfunccode);
			this.getHisMainDirectPayBean().setPayamt(payamt);
			response=this.getHisMainDirectPayBean().retrieve(mainpageRequest);
			if (response.getTotalCount() <= 0) {
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
				this.finddto=new TvDirectpaymsgmainDto();
				return super.rebackSearch(o);
			}
			editor.fireModelChanged();
			return "ֱ��֧���������Ϣ�б�(��ʷ��)";
		}
		
		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.rebackSearch(o);
		}
		editor.fireModelChanged();
		operdto=new TvDirectpaymsgmainDto();
		return super.searchList(o);
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
			directPayService.reSendMsg(operdto.getScommitdate(), operdto
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
	 * Direction: ���ز�ѯ���� ename: rebackSearch ���÷���: viewers: ֱ��֧����Ȳ�ѯ���� messages:
	 */
	public String rebackSearch(Object o) {
		this.operdto=new TvDirectpaymsgmainDto();
		this.hisoperdto=new HtvDirectpaymsgmainDto();
		setSubDirectPayBean(new SubDirectPayBean(directPayService, new TvDirectpaymsgmainDto()));
		setHisSubDirectPayBean(new HisSubDirectPayBean(directPayService,new HtvDirectpaymsgmainDto()));
		return super.rebackSearch(o);
	}

	/**
	 * Direction: ����Ϣ�����¼� ename: singleclickMain ���÷���: viewers: * messages:
	 */
	public String singleclickMain(Object o) {
		PageRequest subpageRequest = new PageRequest();
		
		if(selectedtable.equals("0")){
			operdto = (TvDirectpaymsgmainDto) o;
			this.getSubDirectPayBean().setMaindto(operdto);
			this.getSubDirectPayBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvDirectpaymsgmainDto)o;
			this.getHisSubDirectPayBean().setMaindto(hisoperdto);
			this.getHisSubDirectPayBean().retrieve(subpageRequest);
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
			operdto = (TvDirectpaymsgmainDto) o;
			this.getSubDirectPayBean().setMaindto(operdto);
			this.getSubDirectPayBean().setExpfunccode(expfunccode);
			this.getSubDirectPayBean().setPayamt(payamt);
			this.getSubDirectPayBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvDirectpaymsgmainDto)o;
			this.getHisSubDirectPayBean().setMaindto(hisoperdto);
			this.getHisSubDirectPayBean().setExpfunccode(expfunccode);
			this.getHisSubDirectPayBean().setPayamt(payamt);
			this.getHisSubDirectPayBean().retrieve(subpageRequest);
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
			serverFilePath = directPayService.dataexport(finddto,selectedtable).replace("\\",
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
			serverFilePath = dataexport(finddto,selectedtable, selectDataList, filePath).replace("\\",
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
			List selectDataList,String filePath) throws ITFEBizException {
		
		String filename = "";

		try {
			startdate = TimeFacade.getCurrentStringTime();
			if (startdate.equals(enddate))
				pici++;
			else
				enddate = TimeFacade.getCurrentStringTime();
			if (selectedtable.equals("0")) {
				filename = TimeFacade.getCurrentStringTime() + "01"
						+ (pici < 10 ? "0" + pici : pici) + "hd.txt";
			} else if (selectedtable.equals("1")) {
				filename = TimeFacade.getCurrentStringTime() + "01"
						+ (pici < 10 ? "0" + pici : pici) + "hd.txt";
			}
//			String root = "/itfe/root/temp/";// ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = filePath + dirsep + filename;
			String splitSign = ",";// "\t"; // �ļ���¼�ָ�����

			StringBuffer filebuf = new StringBuffer();
			if (selectedtable.equals("0")) {
				for (TvDirectpaymsgmainDto _dto : (List<TvDirectpaymsgmainDto>) selectDataList) {
					filebuf.append(_dto.getStrecode());// �������
					filebuf.append(splitSign);
					filebuf.append("");// �������ʺ�
					filebuf.append(splitSign);
					filebuf.append("");// ������ȫ��
					filebuf.append(splitSign);
					filebuf.append("");// �����˿�������
					filebuf.append(splitSign);
					filebuf.append("");// �տ����ʺ�
					filebuf.append(splitSign);
					filebuf.append("");
					;// �տ���ȫ��
					filebuf.append(splitSign);
					filebuf.append(_dto.getStransactunit());// �տ��˿�����
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());// Ԥ���������
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());// �ϼƽ��
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxticketno());// ƾ֤���
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpackageticketno()); // ��Ӧƾ֤���
					filebuf.append(splitSign);
					filebuf.append(_dto.getSgenticketdate());// ƾ֤����
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
				for (HtvDirectpaymsgmainDto _dto : (List<HtvDirectpaymsgmainDto>) selectDataList) {
					filebuf.append(_dto.getStrecode());// �������
					filebuf.append(splitSign);
					filebuf.append("");// �������ʺ�
					filebuf.append(splitSign);
					filebuf.append("");// ������ȫ��
					filebuf.append(splitSign);
					filebuf.append("");// �����˿�������
					filebuf.append(splitSign);
					filebuf.append("");// �տ����ʺ�
					filebuf.append(splitSign);
					filebuf.append("");
					;// �տ���ȫ��
					filebuf.append(splitSign);
					filebuf.append(_dto.getStransbankcode());// �տ��˿�����
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());// Ԥ���������
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());// �ϼƽ��
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxticketno());// ƾ֤���
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpackageticketno()); // ��Ӧƾ֤���
					filebuf.append(splitSign);
					filebuf.append(_dto.getSgenticketdate());// ƾ֤����
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
	 * Direction: ������ѯ����������Ϣ
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
		String clientFilePath="";//�ͻ����ļ�����·��
		String dirsep = File.separator;
		String serverFilePath;//������ļ�·��
		try {
			serverFilePath = directPayService.exportFile(finddto,selectedtable).replace("\\","/");
			int j = serverFilePath.lastIndexOf("/");
			//��ȡ�����ļ���
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"	+ clientFilePath);
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

	public TvDirectpaymsgmainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvDirectpaymsgmainDto finddto) {
		this.finddto = finddto;
	}

	public TvDirectpaymsgmainDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvDirectpaymsgmainDto operdto) {
		this.operdto = operdto;
	}

	public MainDirectPayBean getMainDirectPayBean() {
		return mainDirectPayBean;
	}

	public void setMainDirectPayBean(MainDirectPayBean mainDirectPayBean) {
		this.mainDirectPayBean = mainDirectPayBean;
	}

	public SubDirectPayBean getSubDirectPayBean() {
		return subDirectPayBean;
	}

	public void setSubDirectPayBean(SubDirectPayBean subDirectPayBean) {
		this.subDirectPayBean = subDirectPayBean;
	}

	public List getStatelist() {
		return statelist;
	}

	public void setStatelist(List statelist) {
		this.statelist = statelist;
	}

	public HtvDirectpaymsgmainDto getHisoperdto() {
		return hisoperdto;
	}

	public void setHisoperdto(HtvDirectpaymsgmainDto hisoperdto) {
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

	public HisMainDirectPayBean getHisMainDirectPayBean() {
		return hisMainDirectPayBean;
	}

	public void setHisMainDirectPayBean(HisMainDirectPayBean hisMainDirectPayBean) {
		this.hisMainDirectPayBean = hisMainDirectPayBean;
	}

	public HisSubDirectPayBean getHisSubDirectPayBean() {
		return hisSubDirectPayBean;
	}

	public void setHisSubDirectPayBean(HisSubDirectPayBean hisSubDirectPayBean) {
		this.hisSubDirectPayBean = hisSubDirectPayBean;
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