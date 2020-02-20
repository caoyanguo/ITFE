package com.cfcc.itfe.client.para.conpaychecksearchui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckpaybankDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.para.tsuserstampfunction.ITsUserstampfunctionService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-03-05 20:05:36 ��ϵͳ: Para ģ��:ConPayCheckSearchUI
 *       ���:ConPayCheckBillSearchUI
 */
@SuppressWarnings("unchecked")
public class ConPayCheckBillSearchUIBean extends
		AbstractConPayCheckBillSearchUIBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(ConPayCheckBillSearchUIBean.class);
	protected ITsUserstampfunctionService tsUserstampfunctionService = (ITsUserstampfunctionService)getService(ITsUserstampfunctionService.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private PagingContext pagingcontext = new PagingContext(this);
	private PagingContext qscontext = new PagingContext(this);
	private TnConpaycheckbillDto querydto = null;
	private TnConpaycheckbillDto editdto = null;
	private TnConpaycheckbillDto savedto = null;
	private TnConpaycheckpaybankDto searchdto = null;
	private boolean searchtype = false;
	private List checktypelist = null;
	private String strecode;
	private String bankcode;
	private List bankcodelist;
	private String funcinfo;
	private List monthlist;
	public ConPayCheckBillSearchUIBean() {
		super();
		querydto= new TnConpaycheckbillDto();
		editdto = new TnConpaycheckbillDto();
		searchdto = new TnConpaycheckpaybankDto();
		searchdto.setSbgtlevel("0");
		searchdto.setSbgttypecode("1");
		searchdto.setSpaytypecode("0");
		filePath = new ArrayList();
		bankcodelist = new ArrayList();
		checktypelist = new ArrayList();
		querydto.setDstartdate(TimeFacade.getCurrentDateTime());
		querydto.setDenddate(TimeFacade.getCurrentDateTime());
		setFuncinfo("���˵�֧�ֵ���*.csv��ʽ���ļ�������csv��ʽʱ���밴��TC����ʱѡ����������롣");
	}

	/**
	 * Direction: ��ѯ ename: queryBudget ���÷���: viewers: ��ѯ��� messages:
	 */
	public String queryBudget(Object o) {
		searchtype = false;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if (null==pageResponse ||pageResponse.getTotalCount()==0 ) {
			MessageDialog.openMessageDialog(null, "��ѯ�޼�¼��");
			return null;
		}
		pagingcontext.setPage(pageResponse);
		return super.queryBudget(o);
	}
	/**
	 * Direction: ��ѯ ename: queryBudget ���÷���: viewers: ��ѯ��� messages:
	 */
	public String queryQs(Object o) {
		searchtype = true;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if (null==pageResponse ||pageResponse.getTotalCount()==0 ) {
			MessageDialog.openMessageDialog(null, "��ѯ�޼�¼��");
			return null;
		}
		qscontext.setPage(pageResponse);
		this.editor.fireModelChanged();
		return super.queryQs(o);
	}
	/**
	 * Direction: ��ȶ��˵�ɾ��
	 * ename: eddelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String eddelete(Object o){
    	StringBuffer ts = new StringBuffer("ȷ��ɾ��:");
        if(querydto.getStrecode()!=null&&!"".equals(querydto.getStrecode()))
        {
        	ts.append("�������["+querydto.getStrecode()+"]");
        }
        if(querydto.getSbnkno()!=null&&!"".equals(querydto.getSbnkno()))
        {
        	ts.append("��������["+querydto.getSbnkno()+"]");
        }
        if(querydto.getDstartdate()!=null&&!"".equals(querydto.getDstartdate()))
        {
        	ts.append("��ʼ����["+querydto.getDstartdate()+"]");
        }
        if(querydto.getDenddate()!=null&&!"".equals(querydto.getDenddate()))
        {
        	ts.append("��������["+querydto.getDenddate()+"]");
        }
        ts.append("��¼��?");
        if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null,
				"��Ϣ��ʾ", ts.toString())) {
			return "";
		}
        try{
        	tsUserstampfunctionService.delInfo(querydto);
        	MessageDialog.openMessageDialog(null, "ɾ���ɹ�!");
        }catch(Exception e)
        {
        	MessageDialog.openMessageDialog(null, "ɾ��ʧ��:"+e.getMessage());
        }        
        return super.eddelete(o);
    }
    
	/**
	 * Direction: ������˵�ɾ��
	 * ename: qsdelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String qsdelete(Object o){
    	StringBuffer ts = new StringBuffer("ȷ��ɾ��:");
        if(searchdto.getStrecode()!=null&&!"".equals(searchdto.getStrecode()))
        {
        	ts.append("�������["+searchdto.getStrecode()+"]");
        }
        if(searchdto.getSbankcode()!=null&&!"".equals(searchdto.getSbankcode()))
        {
        	ts.append("��������["+searchdto.getSbankcode()+"]");
        }
        if(searchdto.getSext2()!=null&&!"".equals(searchdto.getSext2()))
        {
        	ts.append("�·�["+searchdto.getSext2()+"]");
        }
        ts.append("��¼��?");
        if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null,
				"��Ϣ��ʾ", ts.toString())) {
			return "";
		}
        try{
        	tsUserstampfunctionService.delInfo(searchdto);
        	MessageDialog.openMessageDialog(null, "ɾ���ɹ�!");
        }catch(Exception e)
        {
        	MessageDialog.openMessageDialog(null, "ɾ��ʧ��:"+e.getMessage());
        }        
        return super.qsdelete(o);
    }
	public String dataexport(Object o){
		List list = new ArrayList();
		String fsp = System.getProperty("file.separator");
		DirectoryDialog directory = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String clientPath = directory.open();
		String fileName =  "TN_CONPAYCHECKBILL.txt";
		if(clientPath==null){
			return null;
		}
		BufferedWriter w = null;
		try {
			list = commonDataAccessService.findRsByDtoWithBookOrgUR(querydto);
			if(list.size() > 0){
				w = new BufferedWriter(new FileWriter(clientPath + fsp + fileName));
				StringBuffer sb = new StringBuffer("I_ENROLSRLNO, D_ENDDATE, D_STARTDATE, S_BOOKORGCODE,S_TRECODE, S_BDGORGCODE, S_BDGORGNAME, S_BNKNO, S_FUNCSBTCODE,S_ECOSBTCODE, C_AMTKIND, F_LASTMONTHZEROAMT, F_CURZEROAMT,F_CURRECKZEROAMT, F_LASTMONTHSMALLAMT, F_CURSMALLAMT,F_CURRECKSMALLAMT, TS_SYSUPDATE" + "\r\n");
				for(int i = 0 ; i < list.size() ; i ++){
					TnConpaycheckbillDto tnConpaycheckbillDto = (TnConpaycheckbillDto)list.get(i);
					sb.append(tnConpaycheckbillDto.getIenrolsrlno()).append(",");
					sb.append(tnConpaycheckbillDto.getDenddate()).append(",");
					sb.append(tnConpaycheckbillDto.getDstartdate()).append(",");
					sb.append(tnConpaycheckbillDto.getSbookorgcode()).append(",");
					sb.append(tnConpaycheckbillDto.getStrecode()).append(",");
					sb.append(tnConpaycheckbillDto.getSbdgorgcode()).append(",");
					sb.append(tnConpaycheckbillDto.getSbdgorgname()).append(",");
					sb.append(tnConpaycheckbillDto.getSbnkno()).append(",");
					sb.append(tnConpaycheckbillDto.getSfuncsbtcode()).append(",");
					sb.append(tnConpaycheckbillDto.getSecosbtcode()).append(",");
					sb.append(tnConpaycheckbillDto.getCamtkind()).append(",");
					sb.append(tnConpaycheckbillDto.getFlastmonthzeroamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcurzeroamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcurreckzeroamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFlastmonthsmallamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcursmallamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcurrecksmallamt()).append(",");
					sb.append(tnConpaycheckbillDto.getTssysupdate()).append("\r\n");	
				}
				w.write(sb.toString());
				w.flush();
				MessageDialog.openMessageDialog(null, "�������ݳɹ�!");
			}else{
				MessageDialog.openMessageDialog(null, "������!");
			}
			
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != w){
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return super.dataexport(o);
	}
	
	/**
	 * ��������
	 */
	public String dataimport(Object o){
		if(strecode==null||"".equals(strecode))
		{
			MessageDialog.openMessageDialog(null, "����������벻��Ϊ�գ�");
			return null;
		}
		searchdto.setStrecode(strecode);
		searchdto.setSbankcode(bankcode);
		if(searchdto.getSbankcode()==null||"".equals(searchdto.getSbankcode()))
		{
			MessageDialog.openMessageDialog(null, "���д��벻��Ϊ�գ�");
			return null;
		}
		if(searchdto.getSext1()==null||"".equals(searchdto.getSext1()))
		{
			MessageDialog.openMessageDialog(null, "���˵����Ͳ���Ϊ�գ�");
			return null;
		}
		if("2".equals(searchdto.getSext1()))
		{
			if(searchdto.getSpaytypecode()==null||"".equals(searchdto.getSpaytypecode()))
			{
				MessageDialog.openMessageDialog(null, "֧����ʽ����Ϊ�գ�");
				return null;
			}else if(searchdto.getSbgtlevel()==null||"".equals(searchdto.getSbgtlevel()))
			{
				MessageDialog.openMessageDialog(null, "Ͻ����־����Ϊ�գ�");
				return null;
			}else if(searchdto.getSbgttypecode()==null||"".equals(searchdto.getSbgttypecode()))
			{
				MessageDialog.openMessageDialog(null, "Ԥ�����಻��Ϊ�գ�");
				return null;
			}else if(searchdto.getSext2()==null||"".equals(searchdto.getSext2()))
			{
				MessageDialog.openMessageDialog(null, "���˵��·ݲ���Ϊ�գ�");
				return null;
			}
		}
		// �ӿ�����
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		String resultType = "JZZF";
		int sumFile = 0; // ͳ�����е����txt�ļ�
		int wrongFileSum = 0; // ͳ�ƴ����ļ��ĸ������������20��������׷�Ӵ�����Ϣ
		// �ж��Ƿ�ѡ���ļ�
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}

	
		try {
			// �ж���ѡ�ļ��Ƿ����1000��
			if (filePath.size() > 1000) {
				MessageDialog.openMessageDialog(null, "��ѡ�����ļ����ܴ���1000����");
				return null;
			}
			// ���ݼ���
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < filePath.size(); i++) {
				File tmpfile = (File) filePath.get(i);
				// �ļ�������
				String tmpfilename = tmpfile.getName();
				// ��ȡ�ļ���·��
				String tmpfilepath = tmpfile.getAbsolutePath();
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")&&
						!tmpfilename.trim().toLowerCase().endsWith(".csv")) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ѡ����ļ���ֻ�ܰ���txt����csv��ʽ���ļ���");
					return super.dataimport(o);
				}
				if(tmpfilename.trim().toLowerCase().endsWith(".csv") && StringUtils.isBlank(bankcode)){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "����csv��ʽ���ļ�ʱ���밴�����е��룬��ѡ��������У�");
					return super.dataimport(o);
				}
				sumFile++; // ͳ�����е�csv�ļ�
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// �ļ��ϴ�����¼��־
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			// ����������
			String errName = "";
			int errCount = 0;
			
			if (fileList != null && fileList.size() > 0) {
			
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						fileList, BizTypeConstant.BIZ_TYPE_JZZF,
						resultType, searchdto);
				errCount = bizDto.getErrorCount() + wrongFileSum;
				if (null != bizDto.getErrorList()
						&& bizDto.getErrorList().size() > 0) {
					for (int m = 0; m < bizDto.getErrorList().size(); m++) {
						wrongFileSum++;
						if (wrongFileSum < 15) {
							problemStr.append(bizDto.getErrorList().get(m)
									.substring(6)
									+ "\r\n");
							errorStr.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						} else {
							errorStr.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						}
					}
				}
				// �ǽ�����־
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_JZZF);
				errName = StateConstant.Import_Errinfo_DIR
						+ "ֱ��֧���ļ����������Ϣ("
						+ new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��")
								.format(new java.util.Date()) + ").txt";
				if (!"".equals(errorStr.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorStr.toString());
				}
				FileUtil
						.getInstance()
						.deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer
										.parseInt(StateConstant.Import_Errinfo_BackDays));
			}
			if (problemStr.toString().trim().length() > 0) {
				String noteInfo = "";
				if (fileList == null || fileList.size() == 0) {
					noteInfo = "��������" + sumFile + "���ļ���������" + wrongFileSum
							+ "�������ļ�����Ϣ���£�\r\n";
				} else {
					noteInfo = "��������" + sumFile + "���ļ���������" + errCount
							+ "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴" + errName + "����\r\n";
				}
				MessageDialog.openMessageDialog(null, noteInfo
						+ problemStr.toString());
			} else {
				MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "
						+ fileList.size() + " ���ļ���");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			// ɾ���������ϴ�ʧ���ļ�
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
		filePath = new ArrayList();
		this.editor.fireModelChanged();
		return super.dataimport(o);
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ��Ϣ��ѯ messages:
	 */
	public String goBack(Object o) {
//		querydto = new TnConpaycheckbillDto();
		filePath = new ArrayList();
		return super.goBack(o);
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
			PageResponse response = null;
			if(searchtype)
			{
				response = commonDataAccessService.findRsByDtoWithWherePaging(searchdto,pageRequest, "1=1");
			}else
			{
				querydto.setSbookorgcode(loginfo.getSorgcode());
				String where = "D_STARTDATE>='"+querydto.getDstartdate()+"' and D_STARTDATE<='"+querydto.getDenddate()+"'";
				TnConpaycheckbillDto newquerydto = (TnConpaycheckbillDto)querydto.clone();
				newquerydto.setDstartdate(null);
				newquerydto.setDenddate(null);
				response = commonDataAccessService.findRsByDtoWithWherePaging(newquerydto,pageRequest, where);
			}
			
			return response;

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
	/**
	 * Direction: ��������
	 * ename: savedate
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String savedate(Object o){
    	if (!AdminConfirmDialogFacade.open("B_247", "ҵ�����ƾ֤", "��Ȩ�û�"
				+ loginfo.getSuserName() + "�޸ļ���֧���������", "������Ȩ")) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
        savedto.setFcurrecksmallamt(editdto.getFcurrecksmallamt());
        savedto.setFcurreckzeroamt(editdto.getFcurreckzeroamt());
        savedto.setFcursmallamt(editdto.getFcursmallamt());
        savedto.setFcurzeroamt(editdto.getFcurzeroamt());
        savedto.setFlastmonthsmallamt(editdto.getFlastmonthsmallamt());
        savedto.setFlastmonthzeroamt(editdto.getFlastmonthzeroamt());
        try {
			commonDataAccessService.updateData(savedto);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "��������ʧ��!"+e.toString());
		}
        return super.savedate(o);
    }
    
	/**
	 * Direction: ����
	 * ename: returnqueryresult
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String returnqueryresult(Object o){
        
        return super.returnqueryresult(o);
    }
    
	/**
	 * Direction: ��ת��ѯ���
	 * ename: goqueryresult
	 * ���÷���: 
	 * viewers: ��Ϣ�޸�
	 * messages: 
	 */
    public String goqueryresult(Object o){
    	if(loginfo.getPublicparam().contains("paymentCalculation=true"))
    	{
	        editdto = (TnConpaycheckbillDto)o;
	        savedto = (TnConpaycheckbillDto)o;
	        return super.goqueryresult(o);
    	}else
    	{
    		return "";
    	}
    }
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public TnConpaycheckbillDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TnConpaycheckbillDto querydto) {
		this.querydto = querydto;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
		bankcodelist = new ArrayList();
		bankcodelist.add(new Mapper("",""));
		TsConvertbanktypeDto dto = new TsConvertbanktypeDto();
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setStrecode(strecode);
		try {
			List<TsConvertbanktypeDto> banklist =  commonDataAccessService.findRsByDto(dto);
			for (TsConvertbanktypeDto emuDto : banklist) {
				Mapper map = new Mapper(emuDto.getSbankcode(), emuDto.getSbankname());
				bankcodelist.add(map);
			}
			querydto.setStrecode(strecode);
			searchdto.setStrecode(strecode);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public List getBankcodelist() {
		return bankcodelist;
	}

	public void setBankcodelist(List bankcodelist) {
		this.bankcodelist = bankcodelist;
	}

	public String getFuncinfo() {
		return funcinfo;
	}

	public void setFuncinfo(String funcinfo) {
		this.funcinfo = funcinfo;
	}

	public TnConpaycheckpaybankDto getSearchdto() {
		return searchdto;
	}

	public void setSearchdto(TnConpaycheckpaybankDto searchdto) {
		this.searchdto = searchdto;
	}

	public List getChecktypelist() {
		if(checktypelist==null)
			checktypelist = new ArrayList();
		else if(checktypelist.size()==0)
		{
			Mapper map = new Mapper("1", "����֧����ȶ��˵�");
			Mapper map1 = new Mapper("2", "����֧��������˵�");
			checktypelist.add(map);
			checktypelist.add(map1);
		}
		return checktypelist;
	}

	public void setChecktypelist(List checktypelist) {
		this.checktypelist = checktypelist;
	}

	public List getMonthlist() {
		if(monthlist==null)
		{
			monthlist = new ArrayList();
			Mapper map = new Mapper("01","1��");
			monthlist.add(map);
			map = new Mapper("02","2��");
			monthlist.add(map);
			map = new Mapper("03","3��");
			monthlist.add(map);
			map = new Mapper("04","4��");
			monthlist.add(map);
			map = new Mapper("05","5��");
			monthlist.add(map);
			map = new Mapper("06","6��");
			monthlist.add(map);
			map = new Mapper("07","7��");
			monthlist.add(map);
			map = new Mapper("08","8��");
			monthlist.add(map);
			map = new Mapper("09","9��");
			monthlist.add(map);
			map = new Mapper("10","10��");
			monthlist.add(map);
			map = new Mapper("11","11��");
			monthlist.add(map);
			map = new Mapper("12","12��");
			monthlist.add(map);
		}
		return monthlist;
	}

	public void setMonthlist(List monthlist) {
		this.monthlist = monthlist;
	}

	public PagingContext getQscontext() {
		return qscontext;
	}

	public void setQscontext(PagingContext qscontext) {
		this.qscontext = qscontext;
	}

	public TnConpaycheckbillDto getEditdto() {
		return editdto;
	}

	public void setEditdto(TnConpaycheckbillDto editdto) {
		this.editdto = editdto;
	}

}