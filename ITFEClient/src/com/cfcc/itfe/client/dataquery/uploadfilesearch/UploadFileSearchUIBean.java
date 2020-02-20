package com.cfcc.itfe.client.dataquery.uploadfilesearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dataquery.bizdatacollect.BizDataCountBean;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-01-04 15:20:24
 * ��ϵͳ: DataQuery
 * ģ��:UploadFileSearch
 * ���:UploadFileSearchUI
 */
public class UploadFileSearchUIBean extends AbstractUploadFileSearchUIBean implements IPageDataProvider {

	private TvRecvlogDto finddto = null; // ��ѯ����DTO����
	private PagingContext msgtablepage; // ��ҳ�ؼ�
	private TvRecvlogDto oneRecorddto = null; // ��ѯ����DTO����
	private ITFELoginInfo loginfo = null;
	private List selectRs=null;
	private String orgcode;
	private List orglist;
	public UploadFileSearchUIBean() {
		super();
		selectRs=new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		finddto = new TvRecvlogDto();
		String sdate;
		try {
			sdate = commonDataAccessService.getSysDBDate();
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		finddto.setSsenddate(sdate);
		finddto.setSdemo("2");// Ĭ��Ϊ������־
		finddto.setSifsend("3");// Ĭ��Ϊ������־
		finddto.setSoperationtypecode("0000");
		msgtablepage = new PagingContext(this);
       init();
	}
	private void init(){
		List bizlist = getEnums("0407");
		orglist = BizDataCountBean.getOrgLists(loginfo.getSorgcode(),
				commonDataAccessService);
		if(null != orglist && orglist.size() > 0){
			finddto.setSrecvorgcode(((Mapper)orglist.get(0)).getUnderlyValue().toString());
		}

	}
	/**
	 * Direction: �����շ���־��ѯ ename: searchMsgLog ���÷���: viewers: ��־��ѯ��� messages:
	 */
	public String searchMsgLog(Object o) {
		// ���Ļ�������
		String centerorg = null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		if (!loginfo.getSorgcode().equals(centerorg)) {
			// finddto.setStrecode(loginfo.getTrecode()); //��ǰ��½������Ӧ�������
			// finddto.setSbillorg(loginfo.getSorgcode()); //��Ʊ��λ���ǵ�½��������
		}

		if (StringUtils.isBlank(finddto.getSdemo())) {
			MessageDialog.openMessageDialog(null, "�������ͱ���ѡ��!");
			return super.backSearch(o);
		}
		if (!StringUtils.isBlank(finddto.getSoperationtypecode()) && finddto.getSoperationtypecode().startsWith("000")) {
			finddto.setSoperationtypecode(null);
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		msgtablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return "";
		}

		editor.fireModelChanged();
        return "������־��ѯ���";

		
	}
	public String selectAll(Object o){
		List curlist=msgtablepage.getPage().getData();
		if(selectRs!=null&&selectRs.size()==curlist.size()){
			selectRs=new ArrayList();
			this.editor.fireModelChanged();
			return "";
		}
		selectRs=new ArrayList();
		selectRs.addAll(curlist);
		this.editor.fireModelChanged();
		return "";
	}

	/**
	 * Direction: ������־��ѯ���� ename: backSearch ���÷���: viewers: �����շ���־��ѯ messages:
	 */
	public String backSearch(Object o) {
		return super.backSearch(o);
	}

	/**
	 * Direction: ��־�����¼� ename: singleClickLog ���÷���: viewers: ��־��ѯ��� messages:
	 */
	public String singleClickLog(Object o) {
		oneRecorddto = (TvRecvlogDto) o;
		return super.singleClickLog(o);
	}

	/**
	 * Direction: ������־���� ename: download ���÷���: viewers: * messages:
	 */
	public String download(Object o) {
		if (oneRecorddto == null) {
			MessageDialog.openMessageDialog(null, "��ѡ��ĳһ����¼!");
			return super.download(o);
		}

		// �ͻ������ر��ĵ�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + "msg" + dirsep
				+ oneRecorddto.getSsenddate() + dirsep + loginfo.getSorgcode()
				+ dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			int lastIndex = oneRecorddto.getStitle().lastIndexOf(dirsep);
			String clientfile = clientpath
					+ oneRecorddto.getStitle().substring(lastIndex + 1);
			ClientFileTransferUtil.downloadFile(oneRecorddto.getStitle()
					.replace(queryLogsService.getFileRootPath(), ""),
					clientfile);
			MessageDialog.openMessageDialog(null, "�������سɹ���\n" + clientfile);
		} catch (Exception e) {
			log.error("�������ع����г��ִ���", e);
			MessageDialog.openErrorDialog(null, e);
			return super.download(o);
		}
		return super.download(o);
	}
	/**
	 * Direction: ���ձ�����־���� ename: download ���÷���: viewers: * messages:
	 */
	public String downloadAll(Object o) {
		if (selectRs == null||selectRs.size()<=0) {
			MessageDialog.openMessageDialog(null, "��ѡ��ĳһ����¼!");
			return null;
		}
		// �ͻ������ر��ĵ�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		dirsep = "/";
		try {
			for(TvRecvlogDto recvDto:(List<TvRecvlogDto>)selectRs){
				String clientpath = "c:" + dirsep + "client" + dirsep + "msg" + dirsep
				+ recvDto.getSsenddate() + dirsep + loginfo.getSorgcode()
				+ dirsep;
				File dir = new File(clientpath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				int lastIndex = recvDto.getStitle().lastIndexOf(dirsep);
				String clientfile = clientpath
						+ recvDto.getStitle().substring(lastIndex + 1);
				ClientFileTransferUtil.downloadFile(recvDto.getStitle()
						.replace(queryLogsService.getFileRootPath(), ""),
						clientfile);
			}
			MessageDialog.openMessageDialog(null, "�����������سɹ���\n"+"���ش��·����c:" + dirsep + "client" + dirsep + "msg" );
		} catch (Exception e) {
			log.error("�������ع����г��ִ���", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		
		selectRs=new ArrayList();
		return super.downloadAll(o);
	}

	/**
	 * Direction: �����ط� ename: msgresend ���÷���: viewers: * messages:
	 */
	public String msgresend(Object o) {
		if (oneRecorddto == null) {
			MessageDialog.openMessageDialog(null, "��ѡ��ĳһ����¼!");
			return super.msgresend(o);
		}
		if ( !(finddto instanceof TvRecvlogDto))  {
			MessageDialog.openMessageDialog(null, "ֻ�ܶԡ�������־���ļ�¼�����ط�!!");
			return super.msgresend(o);
		}
		String retcode=oneRecorddto.getSretcode();
		
		
		try {
			String ls_OperationType  = oneRecorddto.getSoperationtypecode();
			if(queryLogsService.isBizMsgNo(ls_OperationType)||ls_OperationType.equals("3139")||ls_OperationType.equals("3129")||ls_OperationType.equals("3128")){
				if(queryLogsService.isBizMsgNo(ls_OperationType)){
					if(retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIPT)||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CHECK)
							||retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIVER)|| retcode.equals(DealCodeConstants.DEALCODE_ITFE_SEND)
							||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CANCELLATION)){
						MessageDialog.openMessageDialog(null, "��״̬��ҵ������ܽ����ط�������ҵ���״̬!");
						return super.msgresend(o);
					}
				}
			}else{
				MessageDialog.openMessageDialog(null, "��ҵ���ģ����ܽ����ط�!!");
				return super.msgresend(o);
			}
			queryLogsService.resendMsg(oneRecorddto.getSsendno());

			PageRequest pageRequest = new PageRequest();
			PageResponse pageResponse = retrieve(pageRequest);
			msgtablepage.setPage(pageResponse);
			editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "�ط��ɹ�!");
		} catch (ITFEBizException e) {
			log.error("�����ط������г��ִ���", e);
			MessageDialog.openErrorDialog(null, e);
			return super.msgresend(o);
		}
		return super.msgresend(o);
	}
	
	/**
	 * Direction: ����ʧ��
	 */
	public String updateFail(Object o) {
		if(oneRecorddto==null){
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼!!");
			return super.updateFail(o);
		}
		//����Ȩ����
		String msg = "��Ҫ������Ȩ�������ñ���״̬Ϊʧ�ܣ�";
		if(!AdminConfirmDialogFacade.open(msg)){
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
		String ls_OperationType  = oneRecorddto.getSoperationtypecode();
		try {
			if(!queryLogsService.isBizMsgNo(ls_OperationType)){
				MessageDialog.openMessageDialog(null, "��ҵ���ģ���������ʧ��״̬!!");
				return super.updateFail(o);
			}
			queryLogsService.updateFail(oneRecorddto.getSsendno(),oneRecorddto.getSpackno(),oneRecorddto.getSoperationtypecode(),oneRecorddto.getSdate());
			PageRequest pageRequest = new PageRequest();
			PageResponse pageResponse = retrieve(pageRequest);
			msgtablepage.setPage(pageResponse);
			MessageDialog.openMessageDialog(null, "����״ִ̬�гɹ�!");
			editor.fireModelChanged();
		}  catch (ITFEBizException e) {
			log.error("����״̬�����г��ִ���", e);
			MessageDialog.openErrorDialog(null, e);
			return super.msgresend(o);
		}
		return super.updateFail(o);
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
			/*PageResponse pr=queryLogsService.queryMsgLog(finddto, pageRequest);
			List<>*/
			return queryLogsService.queryMsgLog(finddto, pageRequest,finddto.getSsenddate(),null);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.retrieve(pageRequest);
	}

	public PagingContext getMsgtablepage() {
		return msgtablepage;
	}

	public void setMsgtablepage(PagingContext msgtablepage) {
		this.msgtablepage = msgtablepage;
	}

	public TvRecvlogDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvRecvlogDto finddto) {
		this.finddto = finddto;
	}

	public TvRecvlogDto getOneRecorddto() {
		return oneRecorddto;
	}
	

	public List getSelectRs() {
		return selectRs;
	}

	public void setSelectRs(List selectRs) {
		this.selectRs = selectRs;
	}

	public void setOneRecorddto(TvRecvlogDto oneRecorddto) {
		this.oneRecorddto = oneRecorddto;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
	
	public List getOrglist() {
		return orglist;
	}
	public void setOrglist(List orglist) {
		this.orglist = orglist;
	}
	
	public String getOrgcode() {
		return orgcode;
	}
	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		IItfeCacheService cacheService = (IItfeCacheService) ServiceFactory
				.getService(IItfeCacheService.class);
		List<TdEnumvalueDto> enumList = new ArrayList<TdEnumvalueDto>();
		try {
			enumList = cacheService.cacheEnumValueByCode(o.toString());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		maplist.add(new Mapper("0", "ȫ��ҵ������"));
		for (TdEnumvalueDto emuDto : enumList) {
			Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
			maplist.add(map);
		}

		return maplist;
	}

	public static List<Mapper> getOrgLists(String orgcode,
			ICommonDataAccessService common) {
		// ��������� ��ʾ���еĻ���
		try {
			List<Mapper> list = new ArrayList<Mapper>();
			TsOrganDto organDto = new TsOrganDto();
			if (!StateConstant.ORG_CENTER_CODE.equals(orgcode.trim()) && !StateConstant.STAT_CENTER_CODE.equals(orgcode.trim())) {
				organDto.setSorgcode(orgcode);
			}
			List<TsOrganDto> organList;
			organList = common.findRsByDto(organDto);
			Mapper mapper = null;
			for (TsOrganDto tmp : organList) {
				if(StateConstant.ORG_CENTER_CODE.equals(tmp.getSorgcode())|| StateConstant.STAT_CENTER_CODE.equals(tmp.getSorgcode())){
					list.add(new Mapper("000000000000", "ȫϽ"));
					continue;
				}
				mapper = new Mapper();
				mapper.setDisplayValue(tmp.getSorgname());
				mapper.setUnderlyValue(tmp.getSorgcode());
				list.add(mapper);
			}
			return list;
		} catch (ITFEBizException e) {
		
			MessageDialog.openMessageDialog(null, "��ȡ������������ʧ�ܣ�");
			return null;
		}
	}


}