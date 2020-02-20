package com.cfcc.itfe.client.recbiz.bankvouchervalidate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.BankValidateDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IExportTBSForBJService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * �Ե��������ƾ֤��Ϣ����У��<8202|5201 -> 2301>
 * 
 * @author hua
 * @time 15-04-03 10:29:55
 */
public class BankVoucherValidateBean extends AbstractBankVoucherValidateBean implements IPageDataProvider {
	private static Log log = LogFactory.getLog(BankVoucherValidateBean.class);
	private IExportTBSForBJService exportTBSForBJService = (IExportTBSForBJService) getService(IExportTBSForBJService.class);
	private String startDate; // ��ʼ�ȶ�����
	private String endDate; // �����ȶ�����
	private List<File> fileList = new ArrayList<File>(); // �����ļ��б�
	private List<Mapper> compareEnumList = new ArrayList<Mapper>(); // �ȶ�״̬ö��
	private String compareStatus; // �ȶ�״̬
	private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();

	public BankVoucherValidateBean() {
		super();
		pagingContext = new PagingContext(this);
		init();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		startDate = TimeFacade.getCurrentStringTime();
		endDate = TimeFacade.getCurrentStringTime();
		compareEnumList.add(new Mapper(StateConstant.MERGE_VALIDATE_SUCCESS, "�ȶԳɹ�"));
		compareEnumList.add(new Mapper(StateConstant.MERGE_VALIDATE_FAILURE, "�ȶ�ʧ��"));
		compareEnumList.add(new Mapper(StateConstant.MERGE_VALIDATE_NOTCOMPARE, "δ�ض�"));
	}

	/**
	 * Direction: �����ļ�
	 */
	public String voucherImport(Object o) {
		long startTime = System.currentTimeMillis();
		System.out.println("Start to update file : "+ startTime);
		if (fileList.size() == 0) {
			MessageDialog.openMessageDialog(null, "����ѡ���ļ�!");
			return super.voucherImport(o);
		}
		try {
			/** У���ļ����� */
			String validateInfo = validate(fileList);
			if (null != validateInfo && !"".equals(validateInfo)) {
				MessageDialog.openMessageDialog(null, validateInfo);
				return super.voucherImport(o);
			}

			/** �������������ļ��ϴ��������� */
			List<String> serverPathList = new ArrayList<String>();
			
//			ListUtils.sub
//			ExecutorService es = Executors.newFixedThreadPool(4);
//			es.execute(new Runnable() {
//				public void run() {
//					
//				}
//			});
			for (int i = 0; i < fileList.size(); i++) {
				File file = fileList.get(i);
				String absFilePath = file.getAbsolutePath();
				String serverFilePath = commonDataAccessService.getServerRootPath(absFilePath, loginfo.getSorgcode());
				exportTBSForBJService.deleteServerFile(serverFilePath);
				serverPathList.add(ClientFileTransferUtil.uploadFile(absFilePath));
			}
			long endUploadFileTime = System.currentTimeMillis();
			System.out.println("update file cost time : "+(endUploadFileTime-startTime));
			/** ��ʼ���е��� */
			MulitTableDto resultDto = bankVoucherValidateService.voucherImport(serverPathList, null);
			System.out.println("import server cost time : "+(System.currentTimeMillis()-endUploadFileTime));

			MessageDialog.openMessageDialog(null, generateMessage(resultDto, serverPathList));
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			refresh();
		} catch (Exception e) {
			log.error(e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return super.voucherImport(o);
		}
		return super.voucherImport(o);
	}

	/**
	 * Direction: ��ʼ�ȶ� ename: compare ���÷���: viewers: * messages:
	 */
	public String compare(Object o) {
		/** У������ */
		String validateInfo = validate4Compare();
		if (StringUtils.isNotBlank(validateInfo)) {
			MessageDialog.openMessageDialog(null, validateInfo);
			return "";
		}
		try {
			/** ��ʼ���бȶ� */
			bankVoucherValidateService.voucherCompare(startDate, endDate, null);
			MessageDialog.openMessageDialog(null, "���ݱȶ����!");
			queryResult(o);
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
	}

	/**
	 * Direction: ��ѯ���� ename: queryResult ���÷���: viewers: ���ݱȶԽ��� messages:
	 */
	public String queryResult(Object o) {
		/** У������ */
		String validateInfo = validate4Compare();
		if (StringUtils.isNotBlank(validateInfo)) {
			MessageDialog.openMessageDialog(null, validateInfo);
			return "";
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if (pageResponse == null || pageResponse.getData().size() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ����������������");
		}
		pagingContext.setPage(pageResponse);
		editor.fireModelChanged();
		return super.queryResult(o);
	}

	/**
	 * У������
	 */
	private String validate4Compare() {
		try {
			Date dateStart = new SimpleDateFormat("yyyyMMdd").parse(startDate);
			Date dateEnd = new SimpleDateFormat("yyyyMMdd").parse(endDate);
			boolean before = DateUtil.before(new java.sql.Date(dateStart.getTime()), new java.sql.Date(dateEnd.getTime()));
			int diffDate = DateUtil.difference(new java.sql.Date(dateStart.getTime()), new java.sql.Date(dateEnd.getTime()));
			if (!before && dateStart.compareTo(dateEnd) != 0) {
				return "��ʼ���ڲ���С�ڽ�������!";
			}
			if (Math.abs(diffDate) > 7) {
				return "��ʼ���ںͽ������ڼ�����ܴ���7��!";
			}
		} catch (ParseException e) {
			return "���ڸ�ʽ�Ƿ�!(��ȷ��ʽ:yyyyMMdd)";
		}
		return "";
	}

	/** ���ѡ����ļ�,��ˢ�½��� **/
	private void refresh() {
		fileList = new ArrayList<File>();
		editor.fireModelChanged();
	}

	/**
	 * У���ļ��б�
	 * 
	 * @param fileList
	 * @return
	 */
	private String validate(List<File> fileList) {
		StringBuilder sb = new StringBuilder("");
		/* �ļ�����Ϊxml�ļ� */
		for (File file : fileList) {
			if (!file.getName().toUpperCase().endsWith(".XML")) {
				sb.append(file.getName() + " �� �ļ����ʹ���!\n");
			}
		}
		return sb.toString();
	}

	public PageResponse retrieve(PageRequest request) {
		try {
			BankValidateDto paramDto = null;
			if (compareStatus != null && !"".equals(compareStatus)) {
				paramDto = new BankValidateDto();
				paramDto.setResult(compareStatus);
			}
			return bankVoucherValidateService.findValidateResult(startDate, endDate, paramDto, request);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(request);
	}

	/**
	 * ���ݽ������������ʾ��Ϣ(����ƾ֤У����Ϣ)
	 * 
	 * @param resultDto
	 * @param serverFileList
	 *            TODO ���ǰ������־������ȡ��һ�����÷���
	 */
	public String generateMessage(MulitTableDto resultDto, List<String> serverFileList) {
		List<String> errorInfoList = resultDto.getErrorList(); // ������Ϣ�б�
		List<String> errorFileList = resultDto.getErrNameList(); // �����ļ��б�
		if (errorFileList == null || errorFileList.size() == 0) {
			/** ��������ļ����б�Ϊ��,��˵��ȫ������ɹ� **/
			return "�ļ����سɹ�,���ι����سɹ�" + serverFileList.size() + "���ļ�!";

		} else {
			/** ���������־��Ϣ **/
			String errorLogName = StateConstant.Import_Errinfo_DIR + "����ƾ֤У�������Ϣ(" + new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new java.util.Date()) + ").txt";
			StringBuffer errorLog = new StringBuffer(""); // ���ȫ���Ĵ�����־��Ϣ
			StringBuffer errorMessage = new StringBuffer(""); // ���Ҫ��ʾ���û��Ĵ�����־��Ϣ���������ݽϳ�������ֻ��ʾǰ15��
			for (int i = 0; errorInfoList.size() > 0 && i < errorInfoList.size(); i++) {
				String errorInfo = errorInfoList.get(i);
				errorLog.append(errorInfo + "\r\n");
				if (i < 15) {
					errorMessage.append(errorInfo + "\r\n");
				}
			}
			if (!"".equals(errorLog.toString())) {
				try {
					/** �����10��֮ǰ�Ĵ�����־��Ϣ **/
					FileUtil.getInstance().deleteFileWithDays(StateConstant.Import_Errinfo_DIR, Integer.parseInt(StateConstant.Import_Errinfo_BackDays));
					/** �������Ĵ�����Ϣ���浽���� **/
					FileUtil.getInstance().writeFile(errorLogName, errorLog.toString());
				} catch (Exception e) {
					/** ���������־�����쳣,��������� **/
					e.printStackTrace();// ���ڵ���
				}
			}

			/** �����ļ��б�Ϊ��,˵�����ڴ����ļ� **/
			StringBuffer sb = new StringBuffer("���μ����ļ���" + serverFileList.size() + "��,����" + (serverFileList.size() - errorFileList.size()) + "���ɹ�," + errorFileList.size() + "��ʧ��,������Ϣ���¡���ϸ������Ϣ��鿴" + errorLogName + "����\r\n");

			sb.append(errorMessage.toString());
			return sb.toString();
		}
	}

	public List<File> getFileList() {
		return fileList;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<Mapper> getCompareEnumList() {
		return compareEnumList;
	}

	public void setCompareEnumList(List<Mapper> compareEnumList) {
		this.compareEnumList = compareEnumList;
	}

	public String getCompareStatus() {
		return compareStatus;
	}

	public void setCompareStatus(String compareStatus) {
		this.compareStatus = compareStatus;
	}
}