package com.cfcc.itfe.client.dataquery.pbcpayquery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.page.HisMainPbcPayBean;
import com.cfcc.itfe.client.common.page.HisSubPbcPayBean;
import com.cfcc.itfe.client.common.page.MainPbcPayBean;
import com.cfcc.itfe.client.common.page.SubPbcPayBean;
import com.cfcc.itfe.client.dialog.BursarAffirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.HtvPbcpaySubDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.pbcpayquery.IPbcpayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author think
 * @time   12-06-28 14:19:53
 * ��ϵͳ: DataQuery
 * ģ��:pbcPayQuery
 * ���:PbcPayQuery
 */
public class PbcPayQueryBean extends AbstractPbcPayQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(PbcPayQueryBean.class);
    
    private IPbcpayService pbcpayService = (IPbcpayService)getService(IPbcpayService.class);
    private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	
    private TvPbcpayMainDto finddto;
    private TvPbcpayMainDto operdto;
    private HtvPbcpayMainDto hisoperdto;
    private TvPbcpayMainDto checkdto;
    private ITFELoginInfo loginfo ;
    //��ǰ��&��ʷ������
    private String selectedtable;
    //��ǰ��&��ʷ��List
    private List statelist2 ;
    private List<TvPbcpayMainDto> selectedlist = new ArrayList<TvPbcpayMainDto>();
    private List<HtvPbcpayMainDto> hselectedlist = new ArrayList<HtvPbcpayMainDto>();
    MainPbcPayBean mainpbcpaybean = null;
    HisMainPbcPayBean hismainpbcpaybean = null;
    SubPbcPayBean subpbcpaybean = null;
    HisSubPbcPayBean hissubpbcpaybean = null;
	

	public PbcPayQueryBean() {
      super();
      finddto = new TvPbcpayMainDto();
      operdto = new TvPbcpayMainDto();
      hisoperdto = new HtvPbcpayMainDto();
      checkdto = new TvPbcpayMainDto();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      initStatelist();
      selectedtable = "0";
      setMainpbcpaybean(new MainPbcPayBean(pbcpayService,finddto));
      setHismainpbcpaybean(new HisMainPbcPayBean(pbcpayService,new HtvPbcpayMainDto()));
      setSubpbcpaybean(new SubPbcPayBean(pbcpayService,finddto));
      setHissubpbcpaybean(new HisSubPbcPayBean(pbcpayService,new HtvPbcpayMainDto()));
      finddto.setSentrustdate(TimeFacade.getCurrentStringTime());
    }
    
	

	



	/**
	 * Direction: ��ѯ�б��¼�
	 * ename: searchList
	 * ���÷���: 
	 * viewers: ���а�����Ȩ֧��������Ϣ�б�
	 * messages: 
	 */
    public String searchList(Object o){
    	
    	PageRequest mainpageRequest = new PageRequest();
		PageResponse response=null;
		if(selectedtable.equals("0")){
			this.getMainpbcpaybean().setMaindto(finddto);
			response = this.getMainpbcpaybean().retrieve(mainpageRequest);
		}else if(selectedtable.equals("1")){
			HtvPbcpayMainDto htvDto = new HtvPbcpayMainDto();
			htvDto.setStrecode(finddto.getStrecode());
			htvDto.setSentrustdate(finddto.getSentrustdate());
			htvDto.setSpackno(finddto.getSpackno());
			htvDto.setSbillorg(finddto.getSbillorg());
			htvDto.setSrcvbnkno(finddto.getSrcvbnkno());
			htvDto.setSoritrano(finddto.getSoritrano());
			htvDto.setSvouno(finddto.getSvouno());
			htvDto.setDvoucher(finddto.getDvoucher());
			htvDto.setFamt(finddto.getFamt());
			htvDto.setSpayeracct(finddto.getSpayeracct());
			htvDto.setSpayeeacct(finddto.getSpayeeacct());
			htvDto.setSpayeeopnbnkno(finddto.getSpayeeopnbnkno());
			htvDto.setIofyear(finddto.getIofyear());
			htvDto.setSstatus(finddto.getSstatus());
			htvDto.setCtrimflag(finddto.getCtrimflag());
			htvDto.setSbackflag(finddto.getSbackflag());
			this.getHismainpbcpaybean().setMaindto(htvDto);
			response = this.getHismainpbcpaybean().retrieve(mainpageRequest);
			if (response.getTotalCount() <= 0) {
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
				return this.rebackSearch(o);
			}
			editor.fireModelChanged();
			return "���а���ֱ��֧��������Ϣ�б�(��ʷ��)";
		}
		
		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return this.rebackSearch(o);
		}
    	
		editor.fireModelChanged();
		operdto = new TvPbcpayMainDto();
        return super.searchList(o);
    }

	/**
	 * Direction: ���ز�ѯ����
	 * ename: rebackSearch
	 * ���÷���: 
	 * viewers: ���а�����Ȩ֧�������ѯ����
	 * messages: 
	 */
    public String rebackSearch(Object o){
    	return super.rebackSearch(o);
    }

	/**
	 * Direction: ȫѡ/��ѡ
	 * ename: selectAllOrNone
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
    	if (this.mainpbcpaybean.getMaintablepage() == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.mainpbcpaybean.getMaintablepage().getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvPbcpayMainDto> templist = page.getData();
		if (templist != null && this.selectedlist != null) {
			if (selectedlist.size() != 0 && selectedlist.containsAll(templist)) {
				selectedlist.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (selectedlist.contains(templist.get(i))) {
						selectedlist.set(selectedlist.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						selectedlist.add(i, templist.get(i));
					}
				}
			}
		}
		this.editor.fireModelChanged();
		return super.selectAllOrNone(o);
    }

	/**
	 * Direction: ���³ɹ�
	 * ename: updateSuccess
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String updateSuccess(Object o){
    	if(selectedlist.size()==0 || selectedlist == null){
			MessageDialog.openMessageDialog(null, "��ѡ�и���״̬�ļ�¼��");
			return null;
    	}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ����ѡ�еļ�¼Ϊ�ɹ���")) {
			return "";
		}
		//����Ȩ����
		String msg = "��Ҫ������Ȩ���ܸ���״̬Ϊ�ɹ���";
		if(!BursarAffirmDialogFacade.open(msg)){
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		};
    	for (int i = 0; i < selectedlist.size(); i++) {
    		checkdto = selectedlist.get(i);
    		checkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
    		try {
    			commonDataAccessService.updateData(checkdto);
    		} catch (ITFEBizException e) {
    			MessageDialog.openErrorDialog(null, e);
    		}
    	}
    	editor.fireModelChanged();
    	selectedlist.clear();
    	MessageDialog.openMessageDialog(null, "����״̬�ɹ���"); 
          return super.updateSuccess(o);
    }

	/**
	 * Direction: ����ʧ��
	 * ename: updateFail
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String updateFail(Object o){
    	if(selectedlist.size()==0 || selectedlist == null){
			MessageDialog.openMessageDialog(null, "��ѡ�и���״̬�ļ�¼��");
			return null;
    	}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ����ѡ�еļ�¼Ϊʧ����")) {
			return "";
		}
		//����Ȩ����
		String msg = "��Ҫ������Ȩ���ܸ���״̬Ϊʧ�ܣ�";
		if(!BursarAffirmDialogFacade.open(msg)){
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		};
    	for (int i = 0; i < selectedlist.size(); i++) {
    		checkdto = selectedlist.get(i);
    		checkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
    		try {
    			commonDataAccessService.updateData(checkdto);
    		} catch (ITFEBizException e) {
    			MessageDialog.openErrorDialog(null, e);
    		}
    	}
    	editor.fireModelChanged();
    	selectedlist.clear();
    	MessageDialog.openMessageDialog(null, "����״̬�ɹ���");  
          return super.updateFail(o);
    }
    
    
    

    /**
     * Direction: ȷ�ϻ���
     * ename: goBackSure
     */
    public String goBackSure(Object o){
    	//��ʼ��SQL
    	String where = "";
    	Long oid = 0l;
    	Long nid = 0l;
    	//��ʼ��Sub����
    	TvPbcpaySubDto subdto;
    	TvPbcpaySubDto findsubdto;
    	HtvPbcpaySubDto hissubdto;
    	HtvPbcpaySubDto findhissubdto;
    	List list ;
    	if(selectedtable.length()==0 || selectedtable == null){
			MessageDialog.openMessageDialog(null, "�޷�ȷ��Ҫ��ѯ�ı�,�뷵�����ԣ�");
			return null;
    	}
    	
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ����ѡ�еļ�¼��ȷ���˻���")) {
			return "";
		}
		
		if(selectedtable.equals("0")){
			if(selectedlist.size()==0 || selectedlist == null){
				MessageDialog.openMessageDialog(null, "��ѡ�и���״̬�ļ�¼��");
				return null;
	    	}
			for (int i = 0; i < selectedlist.size(); i++) {
				try {
					//Main
					checkdto = selectedlist.get(i);
					oid = checkdto.getIvousrlno();
					nid = commonDataAccessService.getSequenceNo("seqforpbcpay");
//					checkdto.setIvousrlno(nid);
					//���˱�ʾ
					checkdto.setSbackflag("1");
					checkdto.setSentrustdate(TimeFacade.getCurrentStringTime());
					pbcpayService.updateInfo(checkdto);
//					where = " where I_VOUSRLNO = " + checkdto.getIvousrlno();
					//Sub
//					findsubdto = new TvPbcpaySubDto();
//					findsubdto.setIvousrlno(oid);
//					list = commonDataAccessService.findRsByDto(findsubdto);
//					for(int j = 0 ; j < list.size() ; j ++){
//						subdto = (TvPbcpaySubDto)list.get(j);
//						subdto.setIvousrlno(nid);
//						pbcpayService.addSubInfo(subdto);
//					}
				} catch (ITFEBizException e1) {
					MessageDialog.openErrorDialog(null, e1);
				} 
				
			}
		}else if(selectedtable.equals("1")){
			if( hselectedlist.size() ==0 || hselectedlist == null){
				MessageDialog.openMessageDialog(null, "��ѡ�и���״̬�ļ�¼��");
				return null;
	    	}
			for (int i = 0; i < selectedlist.size(); i++) {
				try {
					//Main
					hisoperdto = hselectedlist.get(i);
					where = " where I_VOUSRLNO = " + hisoperdto.getIvousrlno();
					oid = hisoperdto.getIvousrlno();
					nid = commonDataAccessService.getSequenceNo("seqforpbcpay");
					checkdto.setIvousrlno(nid);
					checkdto.setSorgcode(hisoperdto.getSorgcode());
					checkdto.setStrecode(hisoperdto.getStrecode());
					checkdto.setSbillorg(hisoperdto.getSbillorg());
					checkdto.setSentrustdate(hisoperdto.getSentrustdate());
					checkdto.setSpackno(hisoperdto.getSpackno());
					checkdto.setSpayoutvoutypeno(hisoperdto.getSpayoutvoutypeno());
					checkdto.setSpaymode(hisoperdto.getSpaymode());
					checkdto.setSvouno(hisoperdto.getSvouno());
					checkdto.setDvoucher(hisoperdto.getDvoucher());
					checkdto.setSpayeracct(hisoperdto.getSpayeracct());
					checkdto.setSpayername(hisoperdto.getSpayername());
					checkdto.setSpayeraddr(hisoperdto.getSpayeraddr());
					checkdto.setSpayeeacct(hisoperdto.getSpayeeacct());
					checkdto.setSpayeename(hisoperdto.getSpayeename());
					checkdto.setSpayeeaddr(hisoperdto.getSpayeeaddr());
					checkdto.setSrcvbnkno(hisoperdto.getSrcvbnkno());
					checkdto.setSpayeeopnbnkno(hisoperdto.getSpayeeopnbnkno());
					checkdto.setSaddword(hisoperdto.getSaddword());
					checkdto.setCbdgkind(hisoperdto.getCbdgkind());
					checkdto.setIofyear(hisoperdto.getIofyear());
					checkdto.setSbdgadmtype(hisoperdto.getSbdgadmtype());
					checkdto.setFamt(hisoperdto.getFamt());
					checkdto.setStrastate(hisoperdto.getStrastate());
					checkdto.setSdescription(hisoperdto.getSdescription());
					checkdto.setDacct(hisoperdto.getDacct());
					checkdto.setCtrimflag(hisoperdto.getCtrimflag());
					checkdto.setIdetailnio(hisoperdto.getIdetailnio());
					checkdto.setSstatus(hisoperdto.getSstatus());
					checkdto.setSbiztype(hisoperdto.getSbiztype());
					//���˱�ʾ
					checkdto.setSbackflag("1");
					checkdto.setDorientrustdate(hisoperdto.getDorientrustdate());
					checkdto.setSoritrano(hisoperdto.getSoritrano());
					checkdto.setSorivouno(hisoperdto.getSorivouno());
					checkdto.setDorivoudate(hisoperdto.getDorivoudate());
					checkdto.setIchgnum(hisoperdto.getIchgnum());
					checkdto.setSinputerid(hisoperdto.getSinputerid());
					checkdto.setTssysupdate(hisoperdto.getTssysupdate());
					checkdto.setSentrustdate(TimeFacade.getCurrentStringTime());
					checkdto.setStrano(hisoperdto.getStrano());
					pbcpayService.addInfo(checkdto);
					//Sub
					findhissubdto = new HtvPbcpaySubDto();
					findhissubdto.setIvousrlno(oid);
					list = commonDataAccessService.findRsByDto(findhissubdto);
					for(int j = 0 ; i < list.size() ; j ++){
						subdto = new TvPbcpaySubDto();
						hissubdto = (HtvPbcpaySubDto)list.get(j);
						subdto.setIvousrlno(nid);
						subdto.setIseqno(hissubdto.getIseqno());
						subdto.setSbdgorgcode(hissubdto.getSbdgorgcode());
						subdto.setSfuncsbtcode(hissubdto.getSfuncsbtcode());
						subdto.setSecosbtcode(hissubdto.getSecosbtcode());
						subdto.setCacctprop(hissubdto.getCacctprop());
						subdto.setFamt(hissubdto.getFamt());
						subdto.setTssysupdate(hissubdto.getTssysupdate());
						pbcpayService.addInfo(subdto);
					}
				} catch (ITFEBizException e1) {
					MessageDialog.openErrorDialog(null, e1);
				} 
			}
		}
		
		
		
    	return super.goBackSure(o);
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
			serverFilePath = pbcpayService.dataExport(finddto,selectedtable).replace("\\",
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
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.dataExport(o);
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
			operdto = (TvPbcpayMainDto) o;
			this.getSubpbcpaybean().setMaindto(operdto);
			this.getSubpbcpaybean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvPbcpayMainDto)o;
			this.getHissubpbcpaybean().setMaindto(hisoperdto);
			this.getHissubpbcpaybean().retrieve(subpageRequest);
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
			operdto = (TvPbcpayMainDto) o;
			this.getSubpbcpaybean().setMaindto(operdto);
			this.getSubpbcpaybean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvPbcpayMainDto)o;
			this.getHissubpbcpaybean().setMaindto(hisoperdto);
			this.getHissubpbcpaybean().retrieve(subpageRequest);
		}
		
		editor.fireModelChanged();
          return super.doubleclickMain(o);
    }
    
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
    
    
    /**
     * init selectedtable's value
     * 
     */
    private void initStatelist(){
		
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
    
    
    
    public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		PbcPayQueryBean.log = log;
	}

	public TvPbcpayMainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvPbcpayMainDto finddto) {
		this.finddto = finddto;
	}

	public TvPbcpayMainDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvPbcpayMainDto operdto) {
		this.operdto = operdto;
	}

	public HtvPbcpayMainDto getHisoperdto() {
		return hisoperdto;
	}

	public void setHisoperdto(HtvPbcpayMainDto hisoperdto) {
		this.hisoperdto = hisoperdto;
	}

	public TvPbcpayMainDto getCheckdto() {
		return checkdto;
	}

	public void setCheckdto(TvPbcpayMainDto checkdto) {
		this.checkdto = checkdto;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
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

	public List<TvPbcpayMainDto> getSelectedlist() {
		return selectedlist;
	}

	public void setSelectedlist(List<TvPbcpayMainDto> selectedlist) {
		this.selectedlist = selectedlist;
	}

	public List<HtvPbcpayMainDto> getHselectedlist() {
		return hselectedlist;
	}

	public void setHselectedlist(List<HtvPbcpayMainDto> hselectedlist) {
		this.hselectedlist = hselectedlist;
	}

	public MainPbcPayBean getMainpbcpaybean() {
		return mainpbcpaybean;
	}

	public void setMainpbcpaybean(MainPbcPayBean mainpbcpaybean) {
		this.mainpbcpaybean = mainpbcpaybean;
	}

	public HisMainPbcPayBean getHismainpbcpaybean() {
		return hismainpbcpaybean;
	}

	public void setHismainpbcpaybean(HisMainPbcPayBean hismainpbcpaybean) {
		this.hismainpbcpaybean = hismainpbcpaybean;
	}
	
	public SubPbcPayBean getSubpbcpaybean() {
		return subpbcpaybean;
	}

	public void setSubpbcpaybean(SubPbcPayBean subpbcpaybean) {
		this.subpbcpaybean = subpbcpaybean;
	}
	
	public HisSubPbcPayBean getHissubpbcpaybean() {
		return hissubpbcpaybean;
	}



	public void setHissubpbcpaybean(HisSubPbcPayBean hissubpbcpaybean) {
		this.hissubpbcpaybean = hissubpbcpaybean;
	}
}