package com.cfcc.itfe.client.dataquery.querymsglog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.BursarAffirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 09-12-11 11:55:00 ��ϵͳ: DataQuery ģ��:queryMsgLog ���:QueryMsgLog
 */
public class QueryMsgLogBean extends AbstractQueryMsgLogBean implements
		IPageDataProvider {

	private TvRecvlogDto finddto = null; // ��ѯ����DTO����
	private PagingContext msgtablepage; // ��ҳ�ؼ�
	private TvRecvlogDto oneRecorddto = null; // ��ѯ����DTO����
	private ITFELoginInfo loginfo = null;
	private List selectRs=null;
	private String startdate;
	private String enddate;
	private List trelist;
	private List finorglist;
	public QueryMsgLogBean() {
		super();
		selectRs=new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		finddto = new TvRecvlogDto();
		finddto.setSsenddate(TimeFacade.getCurrentStringTime());
		finddto.setSdemo("1");// Ĭ��Ϊ������־
		//finddto.setSifsend("1");
		startdate=TimeFacade.getCurrentStringTime();
		enddate=startdate;
		msgtablepage = new PagingContext(this);
		init();

	}
	private void init(){
		TsTreasuryDto tredto=new TsTreasuryDto();
		TsConvertfinorgDto findto=new TsConvertfinorgDto();
		if(!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode()) && !StateConstant.STAT_CENTER_CODE.equals(loginfo.getSorgcode())){
			findto.setSorgcode(loginfo.getSorgcode());
			tredto.setSorgcode(loginfo.getSorgcode());
		}
		try {
			trelist=commonDataAccessService.findRsByDto(tredto);
			finorglist=commonDataAccessService.findRsByDto(findto);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
	}
	public String refreshrs(Object o){
		return this.searchMsgLog(o);
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
		if(null!=startdate&&!"".equals(startdate)&&null!=enddate&&!"".equals(enddate)){
			if (Integer.parseInt(startdate)>Integer.parseInt(enddate)) {
				MessageDialog.openMessageDialog(null, "��ѯ��ʼ����ӦС�ڲ�ѯ�������ڣ�");
				return null;
			}
		}
		
		if (StringUtils.isBlank(finddto.getSdemo())) {
			MessageDialog.openMessageDialog(null, "�������ͱ���ѡ��!");
			return super.backSearch(o);
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		msgtablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.backSearch(o);
		}

		editor.fireModelChanged();
		String opertype = finddto.getSdemo(); // ȡ�ò�������
		if (StateConstant.LOG_SEND.equals(opertype)) {
			return super.searchMsgLog(o);
		}else{
			return "������־��ѯ���";
		}
		
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
			int lastIndex = oneRecorddto.getStitle().replace("\\", "/").lastIndexOf(dirsep);
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
		
		String strecode = oneRecorddto.getStrecode();
		if(strecode == null || strecode.trim().equals("")){
			MessageDialog.openMessageDialog(null, "��ѡ��¼�������Ϊ�գ��������ط�");
			return null;
		}
		TsTreasuryDto trecodeDto = new TsTreasuryDto();
		trecodeDto.setStrecode(strecode);
		try {
			List<TsTreasuryDto> treDtoList = commonDataAccessService.findRsByDto(trecodeDto);
			if(treDtoList == null || treDtoList.size()==0){
				MessageDialog.openMessageDialog(null, "��ѡ��¼��������ڹ�����������δά����");
				return null;
			}
			String ls_OperationType  = oneRecorddto.getSoperationtypecode();
			if(treDtoList.get(0).getStreattrib().equals("2")){ //�����
				if(ls_OperationType.equals(MsgConstant.MSG_TBS_NO_1000) || ls_OperationType.equals(MsgConstant.MSG_TBS_NO_3001)){
					if(retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIPT)||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CHECK)
							||retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIVER)|| retcode.equals(DealCodeConstants.DEALCODE_ITFE_SEND)
							||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CANCELLATION)){
						MessageDialog.openMessageDialog(null, "��״̬��ҵ��������ǻ�ִ�����ܽ����ط�������ҵ���״̬!");
						return super.msgresend(o);
					}
				}else{
					MessageDialog.openMessageDialog(null, "ֻ���ʽ���1000�ͷ�����ͨ�û�ִ3001�����ط�!");
					return super.msgresend(o);
				}
			}else{
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
		String strecode = oneRecorddto.getStrecode();
		if(strecode == null || strecode.trim().equals("")){
			MessageDialog.openMessageDialog(null, "��ѡ��¼�������Ϊ�գ�����������ʧ��");
			return null;
		}
		TsTreasuryDto trecodeDto = new TsTreasuryDto();
		trecodeDto.setStrecode(strecode);
		try {
			List<TsTreasuryDto> treDtoList = commonDataAccessService.findRsByDto(trecodeDto);
			if(treDtoList == null || treDtoList.size()==0){
				MessageDialog.openMessageDialog(null, "��ѡ��¼��������ڹ�����������δά����");
				return null;
			}
			if(treDtoList.get(0).getStreattrib().equals("2")){ //�����
				if ( !(finddto instanceof TvRecvlogDto))  {
					MessageDialog.openMessageDialog(null, "ֻ�ܶԡ�������־���ļ�¼����ʧ��!!");
					return null;
				}
				if(!(MsgConstant.MSG_TBS_NO_1000.equals(ls_OperationType) || MsgConstant.MSG_TBS_NO_3001.equals(ls_OperationType))){
					MessageDialog.openMessageDialog(null, "ֻ�ܶԷ������ʽ���(1000)�����Ƿ�����ͨ�û�ִ��־����ʧ��!!");
					return null;
				}
			}else{
				if(!queryLogsService.isBizMsgNo(ls_OperationType)){
					MessageDialog.openMessageDialog(null, "��ҵ���ģ���������ʧ��״̬!!");
					return super.updateFail(o);
				}
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
			 if(StateConstant.LOG_RECV.equals(finddto.getSdemo().trim())){
					pageRequest.setPageSize(50);
				}
			return queryLogsService.queryMsgLog(finddto, pageRequest,startdate,enddate);
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

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

	public List getFinorglist() {
		return finorglist;
	}

	public void setFinorglist(List finorglist) {
		this.finorglist = finorglist;
	}
	
}