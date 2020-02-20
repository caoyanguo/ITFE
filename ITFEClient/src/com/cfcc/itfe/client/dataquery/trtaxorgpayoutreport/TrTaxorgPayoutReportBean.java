package com.cfcc.itfe.client.dataquery.trtaxorgpayoutreport;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;

/**
 * codecomment:
 * 
 * @author ZZD
 * @time 13-03-20 08:59:05 ��ϵͳ: DataQuery ģ��:trTaxorgPayoutReport
 *       ���:TrTaxorgPayoutReport
 */
public class TrTaxorgPayoutReportBean extends AbstractTrTaxorgPayoutReportBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TrTaxorgPayoutReportBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	String conPayBillType;
	QueryPayOutReportBean querypayoutreportbean;
	QueryConPayOutReportBean queryconpayoutreportbean;
	private BigDecimal nmoneyday, nmoneymonth, nmoneyyear;

	public TrTaxorgPayoutReportBean() {
		super();
		nmoneyday = new BigDecimal("0.00");
		nmoneymonth = new BigDecimal("0.00");
		nmoneyyear = new BigDecimal("0.00");
		dto = new TrTaxorgPayoutReportDto();
		pagingcontext = new PagingContext(this);
		filePath = new ArrayList();
		querypayoutreportbean = new QueryPayOutReportBean(
				commonDataAccessService, dto);
		queryconpayoutreportbean = new QueryConPayOutReportBean(
				commonDataAccessService, dto);
		init();
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: Ԥ��֧���ձ����ѯ��� messages:
	 */
	public String query(Object o) {	
		if(!query(dto))
			return null;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = querypayoutreportbean.retrieve(pageRequest);		
		return super.query(o);
	}

	/**
	 * Direction: ���� ename: exportToFile ���÷���: viewers: * messages:
	 */
	public String exptofile(Object o) {
		exportReport(dto.getStrecode()+dto.getSrptdate(), true);
		return super.exptofile(o);
	}

	/**
	 * Direction: ����֧������ ename: conexpfile ���÷���: viewers: * messages:
	 */
	public String conexpfile(Object o) {
		exportReport(dto.getStrecode()+dto.getSrptdate(), true);
		return super.conexpfile(o);
	}
	
	/**
	 * Direction: ����֧�������±�
	 * ename: contexpmonthfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String contexpmonthfile(Object o){
    	if(!dto.getSrptdate().equals(getLastDayOfMonth(dto.getSrptdate()))){
    		MessageDialog.openMessageDialog(null, "��ǰ���ڲ��Ǹ������һ�죬���ܵ����±���");
			return null;
    	}   	
    	exportReport(dto.getStrecode()+dto.getSrptdate(), false);
        return super.contexpmonthfile(o);
    }
    
	/**
	 * Direction: �����±�
	 * ename: exptomonthfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exptomonthfile(Object o){
    	if(!dto.getSrptdate().equals(getLastDayOfMonth(dto.getSrptdate()))){
    		MessageDialog.openMessageDialog(null, "��ǰ���ڲ��Ǹ������һ�죬���ܵ����±���");
			return null;
    	}   	
    	exportReport(dto.getStrecode()+dto.getSrptdate(), false);
        return super.exptomonthfile(o);
    }
    
    /**
     * ��ȡ�������һ��
     * @param day
     * @return
     */
    public String getLastDayOfMonth(String day){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(TimeFacade.parseDate(day));		
		return day.substring(0, 6)+calendar.getActualMaximum(Calendar.DAY_OF_MONTH);	
    }
    
    /**
     * ��������
     * @param fileName
     * @param flag true �ձ� false �±�
     */
    private void exportReport(String fileName,Boolean flag){    	
		String path = new DirectoryDialog(Display.getCurrent().getActiveShell()).open();		
		if (StringUtils.isBlank(path)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return ;
		}
		try {
			List<TrTaxorgPayoutReportDto> result = commonDataAccessService
					.findRsByDto(querypayoutreportbean.getDto()," ",querypayoutreportbean.getDto().tableName());
			if (null == result || result.size() == 0) {
				MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ�");
				return ;
			}
			expdata(result, path+File.separator+fileName+".txt",flag);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);			
		}
    }
    
    /**
     * ��������
     * @param result
     * @param fileName
     * @param flag true �ձ� false �±�
     * @throws FileOperateException
     */
	private void expdata(List<TrTaxorgPayoutReportDto> result, String fileName,boolean flag)
			throws FileOperateException {
		StringBuffer resultStr = new StringBuffer();
		resultStr.append("�������,��������,Ԥ���Ŀ,");
		if(flag)
			resultStr.append("���ۼ�,");
		resultStr.append("���ۼ�,���ۼ�\n");
		for (TrTaxorgPayoutReportDto tmp : result) {
			resultStr.append(tmp.getStrecode() + ","); // �������
			resultStr.append(tmp.getSrptdate() + ","); // ��������
			resultStr.append(tmp.getSbudgetsubcode() + ","); // Ԥ���Ŀ����
			if(flag)
				resultStr.append(tmp.getNmoneyday().toString() + ","); // ���ۼ�
			resultStr.append(tmp.getNmoneymonth().toString() + ","); // ���ۼ�
			resultStr.append(tmp.getNmoneyyear() + "\n"); // ���ۼ�
		}FileUtil.getInstance().writeFile(fileName, resultStr.toString());
	}

	/**
	 * Direction: ����֧����ѯ ename: conPayoutQuery ���÷���: viewers: ����֧���ձ����ѯ���
	 * messages:
	 */
	public String conPayoutQuery(Object o) {
		dto.setStaxorgcode(conPayBillType);		
		if(!query(dto))
			return null;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = queryconpayoutreportbean.retrieve(pageRequest);		
		return super.conPayoutQuery(o);
	}
	
	/**
	 * ��ѯ
	 * @param dto
	 * @return
	 */
	private boolean query(TrTaxorgPayoutReportDto dto){
		if (StringUtils.isBlank(dto.getSrptdate())) {
			MessageDialog.openMessageDialog(null, "�����뱨�����ڣ�");
			return false;
		}
		if (StringUtils.isBlank(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "��ѡ�������룡");
			return false;
		}				
		if (StringUtils.isBlank(dto.getStaxorgcode())) {
			MessageDialog.openMessageDialog(null, "��ѡ�񱨱����࣡");
			return false;
		}
		try {
			List<TrTaxorgPayoutReportDto> list = commonDataAccessService
				.findRsByDto(dto," ",dto.tableName());
			if(list==null||list.size()==0){
				MessageDialog.openMessageDialog(null, "��ѯ�����ݣ�");
				return false;	
			}
			if(StringUtils.isBlank(dto.getSfinorgcode())){
				nmoneyday = new BigDecimal("0.00");
				nmoneymonth = new BigDecimal("0.00");
				nmoneyyear = new BigDecimal("0.00");
				for (TrTaxorgPayoutReportDto tmpDto : list) {
					if(tmpDto.getSbudgetsubcode().length()==3){//�����ֻ�����
						nmoneyday = nmoneyday.add(tmpDto.getNmoneyday());
						nmoneymonth = nmoneymonth.add(tmpDto.getNmoneymonth());
						nmoneyyear = nmoneyyear.add(tmpDto.getNmoneyyear());
					}		
				}
			}
			return true;							
		} catch (ITFEBizException e) {
			log.error("��ѯ����ʧ�ܣ�");
			MessageDialog.openMessageDialog(null, "��ѯ����ʧ�ܣ�");			
		}return false;
	}

	/**
	 * Direction: ��ѯ ename: conPayquery ���÷���: viewers: ����֧���ձ����ѯ��� messages:
	 */
	public String goConpayQuery(Object o) {

		return super.goConpayQuery(o);

	}

	/**
	 * Direction: ���ز�ѯҳ�� ename: goQuery ���÷���: viewers: Ԥ��֧���ձ����ѯ messages:
	 */
	public String goQuery(Object o) {
		// init();
		filePath = new ArrayList();
		return super.goQuery(o);
	}

	/**
	 * Direction: �������� ename: upLoad ���÷���: viewers: * messages:
	 */
	public String upLoad(Object o) {

		// �ӿ�����
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		String resultType = "3127";
		int sumFile = 0; // ͳ�����е����csv�ļ�
		int wrongFileSum = 0; // ͳ�ƴ����ļ��ĸ������������20��������׷�Ӵ�����Ϣ
		// �ж��Ƿ�ѡ���ļ�
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}

		if (this.check(inputDto, null)) {
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
				if (!tmpfilename.trim().toLowerCase().endsWith(".csv")&&!tmpfilename.trim().toLowerCase().endsWith(".xml")) {
					MessageDialog.openMessageDialog(null, " ��ѡ����ȷ���ļ���ʽ,ֻ֧��csv��xml��ʽ�ļ���");
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
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
						fileList, BizTypeConstant.BIZ_TYPE_TAXORG_PAYOUT,
						resultType, inputDto);
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
						BizTypeConstant.BIZ_TYPE_TAXORG_PAYOUT);
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
		// inputDto = new TrTaxorgPayoutReportDto();
		this.editor.fireModelChanged();
		return super.upLoad(o);
	}

	/**
	 * Direction: ���뵼������ҳ�� ename: goUpload ���÷���: viewers: �������� messages:
	 */
	public String goUpload(Object o) {
		return super.goUpload(o);
	}

	public boolean check(TrTaxorgPayoutReportDto checkDto, String billType) {
		
		if (null == checkDto.getSrptdate() || "".equals(checkDto.getSrptdate())) {
			MessageDialog.openMessageDialog(null, "����д�������ڣ�");
			return true;
		}
		if ((billType==null||"".equals(billType))&&(null == checkDto.getStrecode() || "".equals(checkDto.getStrecode()))) {
			MessageDialog.openMessageDialog(null, "��ѡ�������룡");
			return true;
		}
		if ((billType==null||"".equals(billType))&&(null == checkDto.getSbudgettype()|| "".equals(checkDto.getSbudgettype()))) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ�����࣡");
			return true;
		}
		return false;
	}

	private void init() {
		inputDto = new TrTaxorgPayoutReportDto();
		inputDto.setSrptdate(TimeFacade.getCurrentStringTime());
		inputDto.setSbudgettype(StateConstant.BudgetType_IN);
		TsTreasuryDto _dto = new TsTreasuryDto();
		_dto.setSorgcode(loginfo.getSorgcode());
		TsTreasuryDto tmpdto = null;
		try {
			tmpdto = (TsTreasuryDto) commonDataAccessService.findRsByDto(_dto)
					.get(0);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		inputDto.setStrecode(tmpdto.getStrecode());
		inputDto.setSbudgetlevelcode(tmpdto.getStrelevel());
		dto.setSrptdate(TimeFacade.getCurrentStringTime());
		dto.setSbudgettype(StateConstant.BudgetType_IN);
		dto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);
		dto.setStrecode(tmpdto.getStrecode());
		conPayBillType = StateConstant.REPORT_PAYOUT_TYPE_4;
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

			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, " 1=1 ", "", dto.tableName());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public String getConPayBillType() {
		return conPayBillType;
	}

	public void setConPayBillType(String conPayBillType) {
		this.conPayBillType = conPayBillType;
	}

	public QueryPayOutReportBean getQuerypayoutreportbean() {
		return querypayoutreportbean;
	}

	public void setQuerypayoutreportbean(
			QueryPayOutReportBean querypayoutreportbean) {
		this.querypayoutreportbean = querypayoutreportbean;
	}

	public QueryConPayOutReportBean getQueryconpayoutreportbean() {
		return queryconpayoutreportbean;
	}

	public void setQueryconpayoutreportbean(
			QueryConPayOutReportBean queryconpayoutreportbean) {
		this.queryconpayoutreportbean = queryconpayoutreportbean;
	}

	public BigDecimal getNmoneyday() {
		return nmoneyday;
	}

	public void setNmoneyday(BigDecimal nmoneyday) {
		this.nmoneyday = nmoneyday;
	}

	public BigDecimal getNmoneymonth() {
		return nmoneymonth;
	}

	public void setNmoneymonth(BigDecimal nmoneymonth) {
		this.nmoneymonth = nmoneymonth;
	}

	public BigDecimal getNmoneyyear() {
		return nmoneyyear;
	}

	public void setNmoneyyear(BigDecimal nmoneyyear) {
		this.nmoneyyear = nmoneyyear;
	}

}