package com.cfcc.itfe.client.recbiz.taxfree;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.AreaConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-02-20 16:37:03
 * ��ϵͳ: RecBiz
 * ģ��:TaxFree
 * ���:TaxFreeUI
 */
public class TaxFreeUIBean extends AbstractTaxFreeUIBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TaxFreeUIBean.class);
    
    private ITFELoginInfo loginfo;
    private Date adjustdate;
	private Date systemDate;

	private StringBuffer tipErrsb = new StringBuffer();
	private StringBuffer errsb = new StringBuffer();
	private boolean err = false;
	private int errCount = 0;
	private int errFileCount = 0;
	private int sumFile = 0; 
	private static final int maxErrCount = 15;
	private String errFileName ;
	
	private BigDecimal totalmoney_rel = new BigDecimal("0.00");
	
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	
	private BigDecimal sumamt;
	// ƾ֤�ܱ���
	private Integer vouCount;
	
    public TaxFreeUIBean() {
      super();
      filepath = new ArrayList();
      selectedfilelist = new ArrayList();
      showfilelist = new ArrayList();
      selecteddatalist = new ArrayList();
      showdatalist = new ArrayList();
      filedata = new FileResultDto();
      searchdto = new TbsTvFreeDto();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
                  
    }
    
	/**
	 * Direction: ���ݼ���
	 * ename: dateload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dateload(Object o){
    	if (filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		// �ж���ѡ�ļ��Ƿ����1000��
		if (filepath.size() > 1000) {
			MessageDialog.openMessageDialog(null, "��ѡ�����ļ����ܴ���1000����");
			return null;
		}
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		String resultType = null;//
		// ͳ�����е����txt�ļ�
		// �ж��Ƿ�ѡ���ļ�
		try {
			adjustdate = commonDataAccessService.getAdjustDate();
			String str = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(str.substring(0, 4) + "-"
					+ str.substring(4, 6) + "-" + str.substring(6, 8));
			for (int i = 0; i < filepath.size(); i++) {
				err = false;
				File file = (File) filepath.get(i);
				// �ļ�������
				String filename = file.getName();
				if (!filename.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				sumFile++; // ͳ�����е�txt�ļ�

				// У���ļ���
				validateFileName(filename);
				if(err){
					errCount++;
					continue;
				}
				//�ӿ�������չ����
				resultType = getFileObjByFileNameBiz(filename);
				// �ļ��ϴ�����¼��־
				String serverpath = ClientFileTransferUtil.uploadFile(file
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}

			// ����������
			if (fileList.size() > 0) {
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						fileList, BizTypeConstant.BIZ_TYPE_TAX_FREE,
						resultType, null);
				errFileCount = bizDto.getErrorCount() + errCount;
				if (bizDto.getErrorList().size() > 0) {
					for (int m = 0; m < bizDto.getErrorList().size(); m++) {
						addServerErrMsg(bizDto.getErrorList().get(m)+ "\r\n");
					}
				}
				// �ǽ�����־
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_TAX_FREE);
				if (!"".equals(errorStr.toString())) {
					FileUtil.getInstance().writeFile(getErrFileName(),
							errorStr.toString());
				}
				FileUtil.getInstance().deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer.parseInt(StateConstant.Import_Errinfo_BackDays));
			}

			if (tipErrsb.toString().trim().length() > 0) {
				String noteInfo = "";
				if (fileList == null || fileList.size() == 0) {
					noteInfo = "��������" + sumFile + "���ļ���������" + errCount
							+ "�������ļ�����Ϣ���£�\r\n";
				} else {
					noteInfo = "��������" + sumFile + "���ļ���������" + errFileCount
							+ "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴" + getErrFileName() + "����\r\n";
				}
				MessageDialog.openMessageDialog(null, noteInfo
						+ tipErrsb.toString());
			} else {
				MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "
						+ fileList.size() + " ���ļ���");
			}

		} catch (Exception e) {
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
		reset();
		this.editor.fireModelChanged();
		return super.dateload(o);
    }
	private void reset() {
		this.err = false;
		this.errCount = 0;
		this.errFileCount = 0;
		this.errsb = new StringBuffer();
		this.tipErrsb = new StringBuffer();
		this.sumFile = 0;
		filepath = new ArrayList();
	}

	private String getFileObjByFileNameBiz(String filename) {
		return MsgConstant.TAX_FREE_DAORU;
	}

	private void validateFileName(String filename) {
		String simpleName = filename.substring(0, filename.length() - 4);
		if (simpleName.length() == 17) {
			String bizType = simpleName.substring(12, 16);
			if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {
			} else {
				addErrMsg("�ļ�" + filename + "ҵ�����Ͳ����ϣ�\r\n");
			}
			
			String ctrimflag = simpleName.substring(16);
			if (!MsgConstant.TIME_FLAG_NORMAL.equals(ctrimflag)
					&& !MsgConstant.TIME_FLAG_TRIM.equals(ctrimflag)) {
				addErrMsg("�ļ�" + filename + "�����ڱ�־�����ϣ�����Ϊ0-������ �� 1-�����ڣ�\r\n");
			} else {
				if (MsgConstant.TIME_FLAG_TRIM.equals(ctrimflag)) {
					if (DateUtil.after(systemDate, adjustdate)) {
						addErrMsg("�ļ�"+ filename+ "������"+ com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate)+ "�ѹ������ܴ��������ҵ��");
					}
				}
			}
		} else {
			addErrMsg("�ļ�" + filename + "�ļ�����ʽ������\r\n");
		}
	}

	private void addErrMsg(String msg) {
		if(errCount < maxErrCount){
			errCount++;
			getTipErrsb().append(msg);
			err = true;
		}
		getErrsb().append(msg);
	}
	private void addServerErrMsg(String msg) {
		if(errCount < maxErrCount){
			errCount++;
			getTipErrsb().append(msg);
		}
		getErrsb().append(msg);
	}

	/**
	 * Direction: ֱ���ύ
	 * ename: submit
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String submit(Object o){
    	if(true) {
    		MessageDialog.openMessageDialog(null, "�Ĺ�����δ����!");
        	return super.submit(o);
    	}
    	try {
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_TAX_FREE, loginfo
							.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_TAX_FREE);
				if (StateConstant.SUBMITSTATE_SUCCESS == result) {
					MessageDialog.openMessageDialog(null, "�����ɹ���");
				} else if (StateConstant.SUBMITSTATE_DONE == result) {
					MessageDialog.openMessageDialog(null, "�뵼�����ݣ�");
				} else {
					MessageDialog.openMessageDialog(null, "����ʧ�ܣ�");
				}
			} else {
				MessageDialog.openMessageDialog(null, "û��ֱ���ύȨ�ޣ��������Ա��ϵ��");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("ֱ���ύʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		}
          return super.submit(o);
    }

	/**
	 * Direction: ����Ĭ�Ͻ���
	 * ename: goback
	 * ���÷���: 
	 * viewers: ��ֵ�����
	 * messages: 
	 */
    public String goback(Object o){
          return super.goback(o);
    }

	/**
	 * Direction: ����ȷ��
	 * ename: plsubmit
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String plsubmit(Object o){
    	try {
//			if (commonDataAccessService.judgeBatchConfirm(
//					BizTypeConstant.BIZ_TYPE_TAX_FREE, loginfo
//							.getSorgcode())) {
				if (null == selectedfilelist || selectedfilelist.size() == 0) {
					MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
					return null;
				}

				String area = AreaSpecUtil.getInstance().getArea();
				if ("FUZHOU".equals(area) || "SHANDONG".equals(area)) {
					// ѡ���ļ����ܽ��
					BigDecimal selSumamt = new BigDecimal(0.00);
					// ѡ���ļ���ƾ֤�ܱ���
					Integer selvouCount = 0;
					if (sumamt == null) {
						MessageDialog.openMessageDialog(null, "����д�ܽ�");
						return null;
					}
					if (vouCount == null) {
						MessageDialog.openMessageDialog(null, "����дƾ֤�ܱ�����");
						return null;
					}
					for (int i = 0; i < selectedfilelist.size(); i++) {
						TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
								.get(i);
						selSumamt = selSumamt.add(dto.getNmoney());
						selvouCount = selvouCount + dto.getIcount();
					}
					if (selSumamt.compareTo(sumamt) != 0
							|| selvouCount.compareTo(vouCount) != 0) {
						MessageDialog.openMessageDialog(null, "������ܽ�� ["
								+ sumamt.toString() + "]��ƾ֤�ܱ���["
								+ vouCount.toString()
								+ "]�������ļ����ܽ�ƾ֤�ܱ�������,���֤!");
						return null;
					} else {
						DisplayCursor.setCursor(SWT.CURSOR_WAIT);
						massdispose();
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					}
				} else {
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					massdispose();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				}
//			} else {
//				MessageDialog.openMessageDialog(null, "û����������Ȩ�ޣ��������Ա��ϵ��");
//				return null;
//			}

		} catch (ITFEBizException e) {
			log.error("�����ύʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
          return super.plsubmit(o);
    }

	/**
	 * Direction: ����ɾ��
	 * ename: pldel
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String pldel(Object o){
    	// ȷ���Ѿ���ѡ��ļ�¼
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		}
		// ��ʾ�û�ȷ��ɾ��
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��?")) {
			try {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_TAX_FREE, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showfilelist.remove(dto);

				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				selectedfilelist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
			} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				log.error("����ɾ��ʧ�ܣ�", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			return null;
		}
          return super.pldel(o);
    }

	/**
	 * Direction: ����ύ
	 * ename: zbsubmit
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String zbsubmit(Object o){
    	// ȷ���Ѿ���ѡ��ļ�¼
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			try {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TbsTvFreeDto dto = (TbsTvFreeDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_TAX_FREE, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				selecteddatalist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				log.error("����ύʧ�ܣ�", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}
          return super.zbsubmit(o);
    }

	/**
	 * Direction: �������Ų�ѯ
	 * ename: plsearch
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String plsearch(Object o){
    	
          return super.plsearch(o);
    }

	/**
	 * Direction: ������Ų�ѯ
	 * ename: zbsearch
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String zbsearch(Object o){
    	selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		try {
			// �����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_TAX_FREE, searchdto);
			// ͳ�Ʊ������
			List<TbsTvFreeDto> mlist = (List<TbsTvFreeDto>) showdatalist;
			
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvFreeDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFfreepluamt());
				totalnum++;
			}
			if (showdatalist.size() == 0) {
				MessageDialog.openMessageDialog(null, "û�в�ѯ���������������ݣ�");
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				return null;
			}
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("������Ų�ѯʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.zbsearch(o);
    }

	/**
	 * Direction: ȫѡ
	 * ename: selectall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectall(Object o){
    	if (null == selecteddatalist || selecteddatalist.size() == 0) {
			selecteddatalist = new ArrayList();
			selecteddatalist.addAll(showdatalist);
		} else {
			selecteddatalist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
          return super.selectall(o);
    }

	/**
	 * Direction: ѡ���¼�
	 * ename: selectEvent
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectEvent(Object o){
    	List<TbsTvFreeDto> mlist = (List<TbsTvFreeDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvFreeDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFfreepluamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
          return super.selectEvent(o);
    }

	/**
	 * Direction: ˫����ʾ��ϸ
	 * ename: doubleClickEvent
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleClickEvent(Object o){
          return super.doubleClickEvent(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public Date getAdjustdate() {
		return adjustdate;
	}

	public void setAdjustdate(Date adjustdate) {
		this.adjustdate = adjustdate;
	}

	public Date getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(Date systemDate) {
		this.systemDate = systemDate;
	}

	public StringBuffer getTipErrsb() {
		return tipErrsb;
	}

	public void setTipErrsb(StringBuffer tipErrsb) {
		this.tipErrsb = tipErrsb;
	}

	public StringBuffer getErrsb() {
		return errsb;
	}

	public void setErrsb(StringBuffer errsb) {
		this.errsb = errsb;
	}

	public boolean isErr() {
		return err;
	}

	public void setErr(boolean err) {
		this.err = err;
	}

	public int getErrCount() {
		return errCount;
	}

	public void setErrCount(int errCount) {
		this.errCount = errCount;
	}

	public int getErrFileCount() {
		return errFileCount;
	}

	public void setErrFileCount(int errFileCount) {
		this.errFileCount = errFileCount;
	}

	public int getSumFile() {
		return sumFile;
	}

	public void setSumFile(int sumFile) {
		this.sumFile = sumFile;
	}

	public String getErrFileName() {
		if(errFileName == null){
			errFileName =  StateConstant.Import_Errinfo_DIR
			+ "��ֵ��ļ����������Ϣ("
			+ new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��")
					.format(new java.util.Date()) + ").txt";
		}
		return errFileName;
	}

	public BigDecimal getSumamt() {
		return sumamt;
	}

	public void setSumamt(BigDecimal sumamt) {
		if(null == vouCount || null == sumamt){
			MessageDialog.openMessageDialog(null, "ƾ֤�������ܽ���Ϊ�գ�");
			return ;
		}
		this.sumamt = sumamt;
		String area = AreaSpecUtil.getInstance().getArea();
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
		} else if ("FUZHOU".equals(area)
				|| AreaConstant.AREA_SHANDONG.equals(area)
				|| AreaConstant.AREA_SICHUAN.equals(area)
				|| "XIAMEN".equals(area)) {
			BigDecimal totalfamt = new BigDecimal("0.00");
			Set<String> strset = new HashSet<String>();
			showfilelist = new ArrayList();
			selectedfilelist = new ArrayList();
			TvFilepackagerefDto refdto = new TvFilepackagerefDto();
			refdto.setSorgcode(loginfo.getSorgcode());
			refdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
			refdto
					.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_TAX_FREE);
			try {
				showfilelist = commonDataAccessService.findRsByDtoUR(refdto);
				if (showfilelist != null && showfilelist.size() > 0) {
					for (Object obj : showfilelist) {
						TvFilepackagerefDto packdto = (TvFilepackagerefDto) obj;
						totalfamt = totalfamt.add(packdto.getNmoney());
						strset.add(packdto.getSpackageno()); // ����ˮ��
					}
					this.setTotalmoney_rel(totalfamt);
					if (totalfamt.compareTo(sumamt) == 0) {
						selectedfilelist.clear();
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
						// massdispose();
					} else {
						Map<BigDecimal, Integer> inmap = new HashMap<BigDecimal, Integer>();
						List<String> inlist = new ArrayList<String>();
						for (String pkno : strset) {
							BigDecimal treamt = new BigDecimal("0.00");
							int sum = 0;
							List<TvFilepackagerefDto> filist = new ArrayList<TvFilepackagerefDto>();
							for (Object obj : showfilelist) {
								TvFilepackagerefDto packdto = (TvFilepackagerefDto) obj;
								if (null != packdto.getSpackageno()
										&& pkno.equals(packdto.getSpackageno())) {
									treamt = treamt.add(packdto.getNmoney());
									sum++;
									filist.add(packdto);
								}
							}
							inlist.add(treamt + "--" + sum);
						}
						selectedfilelist.clear();
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
						if (strset.size() == 1) {
							MessageDialog.openMessageDialog(null, "������ܽ��["
									+ sumamt + "]]��δ�����ļ��ĺϼƽ�����ݲ�һ��\n"
									+ "��ϸ��ʾ:����δ�����ļ��ܽ��Ϊ[" + totalfamt
									+ "],���� [" + showfilelist.size() + "]��");
						} else {
							MessageDialog.openMessageDialog(null, "������ܽ��["
									+ sumamt + "]��δ�����ļ��ĺϼƽ�����ݲ�һ��\n"
									+ "��ϸ��ʾ:����δ�����ļ��ܽ��Ϊ[" + totalfamt
									+ "],���� [" + showfilelist.size()
									+ "]�� ,����Ϊ[" + strset.size()
									+ "]����,'���ܽ��-����' ��Ӧ��ϵ�ֱ�����\n" + ""
									+ inlist.toString() + "");
						}
					}
					this.editor
							.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				} else {
					MessageDialog.openMessageDialog(null, "��ֵ�������δ��������!");
				}
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}

		} else {
			try {
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_TAX_FREE, sumamt);
				if (null != showfilelist) {
					if (1 == showfilelist.size()) {
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						// massdispose();
					}
					this.editor
							.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
					if(showfilelist.size() <= 0){
						MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
						return;
					}
					
				}else{
					MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
					return;
				}

			} catch (ITFEBizException e) {
				log.error("����ʧ�ܣ�", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}

	private void massdispose() throws ITFEBizException {

		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_TAX_FREE, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		}

	}
	
	public Integer getVouCount() {
		return vouCount;
	}

	public void setVouCount(Integer vouCount) {
		this.vouCount = vouCount;
	}

	public BigDecimal getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(BigDecimal totalmoney) {
		this.totalmoney = totalmoney;
	}

	public int getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}

	public BigDecimal getTotalmoney_rel() {
		return totalmoney_rel;
	}

	public void setTotalmoney_rel(BigDecimal totalmoney_rel) {
		this.totalmoney_rel = totalmoney_rel;
	}


}