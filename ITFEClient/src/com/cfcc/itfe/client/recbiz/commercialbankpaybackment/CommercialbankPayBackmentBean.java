package com.cfcc.itfe.client.recbiz.commercialbankpaybackment;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.recbiz.commercialbankpayment.PayreckBankSubBean;
import com.cfcc.itfe.constant.AreaConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
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
import com.cfcc.jaf.ui.validator.ValidationError;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-01-26 13:24:26
 * ��ϵͳ: RecBiz
 * ģ��:commercialbankPayBackment
 * ���:CommercialbankPayBackment
 */
public class CommercialbankPayBackmentBean extends AbstractCommercialbankPayBackmentBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(CommercialbankPayBackmentBean.class);
    private ITFELoginInfo loginfo;
    private BigDecimal sumamt;
	// ƾ֤�ܱ���
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	private BigDecimal totalmoney_rel = new BigDecimal("0.00");
	private PayreckBankBackSubBean subBean = null;
	private List<String> biztypeList=new ArrayList<String>();

	
	public CommercialbankPayBackmentBean() {
      super();
      filepath = new ArrayList();
      selectedfilelist = new ArrayList();
      showfilelist = new ArrayList();
      selecteddatalist = new ArrayList();
      showdatalist = new ArrayList();
      searchdto = new TbsTvBnkpayMainDto();
      payreckbackdto = new TvPayreckBankBackDto();
      payreckbackdto.setDpayentrustdate(DateUtil.currentDate());
      payreckbackdto.setSpaymsgno(StateConstant.CMT100);
//      setSubBean(new PayreckBankBackSubBean(commonDataAccessService, searchdto));
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
    	
    	// �ӿ�����
		String interfacetype = "";
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		String resultType = "";
		int sumFile = 0; // ͳ�����е����txt�ļ�
		int wrongFileSum = 0; // ͳ�ƴ����ļ��ĸ������������20��������׷�Ӵ�����Ϣ
		// �ж��Ƿ�ѡ���ļ�
		if (null == getFilepath() || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		String info =checkRequired();
		if (null!=info && info.length()>0) {
			MessageDialog.openMessageDialog(null, "�벹¼�����ʽ���Ϣ\n"+info);
			return null;
		}
		try {
			// �ж���ѡ�ļ��Ƿ����1000��
			if (filepath.size() > 1000) {
				MessageDialog.openMessageDialog(null, "��ѡ�����ļ����ܴ���1000����");
				return null;
			}
			adjustdate = commonDataAccessService.getAdjustDate();
			String str = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(str.substring(0, 4) + "-"
					+ str.substring(4, 6) + "-" + str.substring(6, 8));
			// ���ݼ���
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			biztypeList.clear();
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				// �ļ�������
				String tmpfilename = tmpfile.getName();
				// ��ȡ�ļ���·��
				String tmpfilepath = tmpfile.getAbsolutePath();
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				sumFile++; // ͳ�����е�txt�ļ�
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// �����ļ�����
				interfacetype = getFileObjByFileNameBiz(tmpfilename);
				if (interfacetype.trim().length() != 4) {
					if (wrongFileSum < 15) {
						problemStr.append(interfacetype);
					}
					errorStr.append(interfacetype);
					wrongFileSum++;
					continue;
				} else {
					resultType = interfacetype;
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
			if (fileList != null && fileList.size() > 0 && resultType != null
					&& resultType.trim().length() > 0) {
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						fileList,"",resultType, payreckbackdto);
				
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
				String flag=getBiztype();
				// �ǽ�����־
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),flag);
				errName = StateConstant.Import_Errinfo_DIR
						+ "���д���֧�����������ļ����������Ϣ("
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
			 payreckbackdto = new TvPayreckBankBackDto();
		     payreckbackdto.setDpayentrustdate(DateUtil.currentDate());
		     payreckbackdto.setSpaymsgno(StateConstant.CMT103);   
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Throwable e) {
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
		
		filepath = new ArrayList();
		this.editor.fireModelChanged();
        return super.dateload(o);
    }

	/**
	 * Direction: ��ת�������Ų�ѯ
	 * ename: topiliangxh
	 * ���÷���: 
	 * viewers: ��������
	 * messages: 
	 */
    public String topiliangxh(Object o){
          return super.topiliangxh(o);
    }

	/**
	 * Direction: ��ת������Ų�ѯ
	 * ename: tozhubixh
	 * ���÷���: 
	 * viewers: �������
	 * messages: 
	 */
    public String tozhubixh(Object o){
          return super.tozhubixh(o);
    }

	/**
	 * Direction: ֱ���ύ
	 * ename: submit
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String submit(Object o){
    	//��ȡ�����ļ���ҵ������
    	String flag=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK;
    	try {
			if (commonDataAccessService.judgeDirectSubmit(flag, loginfo.getSorgcode())) {
				int result = uploadConfirmService.applyBackDirectSubmit(flag, payreckbackdto);
				if (StateConstant.SUBMITSTATE_SUCCESS == result) {
					MessageDialog.openMessageDialog(null, "�����ɹ���");
				} else if (StateConstant.SUBMITSTATE_DONE == result) {
					MessageDialog.openMessageDialog(null, "�뵼�����ݣ�");
				} else {
					MessageDialog.openMessageDialog(null, "����ʧ�ܣ�");
				}
			} else {
				MessageDialog.openMessageDialog(null, "û��ֱ���ύȨ�ޣ��������Ա��ϵ��");
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
	 * viewers: ���а���֧�������������ݵ���
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
    	//��ȡ�����ļ���ҵ������
    	String flag=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK;
    	try {
			/**
			 * �ж��ܷ���������
			 */
			if (commonDataAccessService.judgeBatchConfirm(flag, loginfo.getSorgcode())) {

				if (null == selectedfilelist || selectedfilelist.size() == 0) {
					MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
					return null;
				}

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
						// �������Ŵ���
						DisplayCursor.setCursor(SWT.CURSOR_WAIT);
						massdispose(flag);
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);

					}
			
			} else {
				MessageDialog.openMessageDialog(null, "û����������Ȩ�ޣ��������Ա��ϵ��");
			}

		} catch (ITFEBizException e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
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
//    	String flag=getBiztype();
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
							dto.getSoperationtypecode(), dto);
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
    	
    	//��ȡ�����ļ���ҵ������
//    	String flag=getBiztype();
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
					TbsTvBnkpayMainDto dto = (TbsTvBnkpayMainDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.applyBackEachConfirm(dto.getSbiztype(), dto, payreckbackdto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				selecteddatalist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
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
		String flag=getBiztype();
		try {
			// �����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
//			searchdto.setSstate(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					flag, searchdto);
			selecteddatalist.addAll(showdatalist);
			// ͳ�Ʊ������
			List<TbsTvBnkpayMainDto> mlist = (List<TbsTvBnkpayMainDto>) showdatalist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvBnkpayMainDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFsmallsumamt().add(mdto.getFzerosumamt()));
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
    	List<TbsTvBnkpayMainDto> mlist = (List<TbsTvBnkpayMainDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvBnkpayMainDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFsmallsumamt().add(mdto.getFzerosumamt()));
			totalnum++;
		}
		this.editor.fireModelChanged();
          return super.selectEvent(o);
    }

	/**
	 * Direction: ˫����ʾ��ϸ
	 * ename: doubleClickEvent
	 * ���÷���: 
	 * viewers: ��������¼��ʾ����
	 * messages: 
	 */
    public String doubleClickEvent(Object o){
    	if (null != o) {
			TbsTvBnkpayMainDto main = (TbsTvBnkpayMainDto) o;
			PageRequest subRequest = new PageRequest();
			this.getSubBean().setMaindto(main);
			this.getSubBean().retrieve(subRequest);
		}
		editor.fireModelChanged();
          return super.doubleClickEvent(o);
    }
    
    
    /**
	 * Direction: ��תֱ���ύ��¼��Ϣҳ��
	 * ename: toDirectBulu
	 * ���÷���: 
	 * viewers: 
	 * messages: 
	 */
	public String toDirectBulu(Object o) {
		List<String> list=getBiztypeList();
		if (null == list || list.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		MessageDialog.openMessageDialog(null, "������д��¼��Ϣ��");
		payreckbackdto = new TvPayreckBankBackDto();
		return super.toDirectBulu(o);
	}

	/**
	 * Direction: ��ת�����ύ��¼��Ϣҳ��
	 * ename: toPilBulu
	 * ���÷���: 
	 * viewers: 
	 * messages: 
	 */
	public String toPilBulu(Object o) {
		MessageDialog.openMessageDialog(null, "������д��¼��Ϣ��");
		payreckbackdto = new TvPayreckBankBackDto();
		return super.toPilBulu(o);
	}

	/**
	 * Direction: ��ת����ύ��¼��Ϣҳ��
	 * ename: toZhubiBulu
	 * ���÷���: 
	 * viewers:
	 * messages: 
	 */
	public String toZhubiBulu(Object o) {
		MessageDialog.openMessageDialog(null, "������д��¼��Ϣ��");
		payreckbackdto = new TvPayreckBankBackDto();
		return super.toZhubiBulu(o);
	}
    
    
	public String checkRequired(){
		StringBuffer demo=new StringBuffer();
 		if(null==payreckbackdto.getSpaydictateno()||payreckbackdto.getSpaydictateno().equals("")){
			demo.append("֧��������Ų���Ϊ�գ�\r");
		}else {
			String regex = "\\d{8}";
			Matcher matcher;
			Pattern pattern;
			String tempStr = payreckbackdto.getSpaydictateno().toString();
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(tempStr);
			if (!matcher.matches()) {
				demo.append("���벻�Ϸ���֧���������Ӧ������8λ���֣�\r\n");
				payreckbackdto.setSpaydictateno(null);
			}
			
		
		}
 		if(null==payreckbackdto.getSpaymsgno()||payreckbackdto.getSpaymsgno().equals("")){
			demo.append("֧�����ı�Ų���Ϊ�գ�\r");
		}
 		if(null==payreckbackdto.getDpayentrustdate()||payreckbackdto.getDpayentrustdate().equals("")){
			demo.append("֧��ί�����ڲ���Ϊ�գ�\r");
		}
 		if(null==payreckbackdto.getSpaysndbnkno()||payreckbackdto.getSpaysndbnkno().equals("")){
			demo.append("֧���������кŲ���Ϊ�գ�\r");
		}
 		if(null==payreckbackdto.getFamt()||payreckbackdto.getFamt().equals("")){
			demo.append("�����ʽ��ܶ��Ϊ�գ�");
		}
		
		return demo.toString();
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
	 * �����ļ����õ��ļ��ӿ�����
	 * 
	 * @param biztype
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	private String getFileObjByFileNameBiz(String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();
		if (tmpfilename_new.indexOf(".txt") > 0) {
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			// ʮ��λ˵�����п����ǵط�������tbs�� boolΪtrue ��Ϊtbs�ӿڷ���Ϊ�ط�����
			if (tmpfilename.length() == 13 || tmpfilename.length() == 15) {
				// 8λ���ڣ�2λ���κţ�2λҵ�����ͣ�1λ�����ڱ�־���
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				if (tmpfilename.length() == 13) {
					fileobj.setSbatch(tmpfilename.substring(8, 10)); // ���κ�
					fileobj.setSbiztype(tmpfilename.substring(10, 12)); // ҵ������
					fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // �����ڱ�־
				} else {
					fileobj.setSbatch(tmpfilename.substring(8, 12)); // ���κ�
					fileobj.setSbiztype(tmpfilename.substring(12, 14)); // ҵ������
					fileobj.setCtrimflag(tmpfilename.substring(14, 15)); // ҵ������
				}

				biztypeList.add(fileobj.getSbiztype());
				interficetype = MsgConstant.APPLYPAY_BACK_DAORU; // tbs�ӿ�
				if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK.equals(fileobj
						.getSbiztype())||BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK.equals(fileobj
								.getSbiztype())) {
				} else {
					return "�ļ�" + filename + "ҵ�����Ͳ����ϣ�\r\n";
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					return "�ļ�" + filename + "�����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1�������ڣ�\r\n";
				} else {
					if (MsgConstant.TIME_FLAG_TRIM.equals(fileobj
							.getCtrimflag())) {
						if (DateUtil.after(systemDate, adjustdate)) {
							return "�ļ�"
									+ filename
									+ "������"
									+ com.cfcc.deptone.common.util.DateUtil
											.date2String(adjustdate)
									+ "�ѹ������ܴ��������ҵ��";
						}
					}
				}

			} else {
				return "�ļ�" + filename + "�ļ�����ʽ������\r\n";
			}
		} else {
			return "�ļ�" + filename + "�ļ�����ʽ������\r\n";
		}
		return interficetype;
	}
    
	
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
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
		String flag=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK;
		String area = AreaSpecUtil.getInstance().getArea();
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
			 MessageDialog.openMessageDialog(null, "����Ϊ�ջ�0��");
		} else  {
			BigDecimal totalfamt = new BigDecimal("0.00");
			showfilelist = new ArrayList();
			selectedfilelist = new ArrayList();
			
			String sqlWhere="";
			if(flag.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlWhere=" and ( S_operationtypecode= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+"' or S_operationtypecode='"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+"')";
			}else if(flag.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)){
				sqlWhere=" and S_operationtypecode= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+"'";
			}else if(flag.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlWhere=" and S_operationtypecode= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+"'";
			}
			
			TvFilepackagerefDto refdto = new TvFilepackagerefDto();
			refdto.setSorgcode(loginfo.getSorgcode());
			refdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
//			refdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
			try {
				Set<String> strset = new HashSet<String>();
				showfilelist = commonDataAccessService.findRsByDto(refdto, sqlWhere);
				if (showfilelist != null && showfilelist.size() > 0) {
					for (Object obj : showfilelist) {
						TvFilepackagerefDto packdto = (TvFilepackagerefDto) obj;
						totalfamt = totalfamt.add(packdto.getNmoney());
						strset.add(packdto.getSpackageno());
					}
					this.setTotalmoney_rel(totalfamt);
					if (totalfamt.compareTo(sumamt) == 0) {
						selectedfilelist.clear();
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
						// massdispose();
					} else {
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
									+ sumamt + "]��δ�����ļ��ĺϼƽ�����ݲ�һ��\n"
									+ "��ϸ��ʾ:����δ�����ļ��ܽ��Ϊ[" + totalfamt
									+ "],���� [" + showfilelist.size() + "]��");
						} else {
							MessageDialog.openMessageDialog(null, "������ܽ��[["
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
					MessageDialog.openMessageDialog(null, "���������˻ز�����δ��������!");
				}
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}

		} 
	}

	private void massdispose(String flag) throws ITFEBizException {

		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ���ύ��")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.applyBackBatchConfirm(flag, dto, payreckbackdto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);
			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		}

	}
	
	public String getBiztype(){
		StringBuffer flag=new StringBuffer();
    	List<String> list=getBiztypeList();
    	if(null!=list&&list.size()>0){
    		if(list.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)){
        		flag.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
        	}
        	if(list.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
        		flag.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
        	}
    	}else{
    		flag.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
    		.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
    	}
		return flag.toString();
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

	public Integer getVouCount() {
		return vouCount;
	}

	public void setVouCount(Integer vouCount) {
		this.vouCount = vouCount;
	}

	public PayreckBankBackSubBean getSubBean() {
		return subBean;
	}

	public void setSubBean(PayreckBankBackSubBean subBean) {
		this.subBean = subBean;
	}
	
	public List<String> getBiztypeList() {
		return biztypeList;
	}

	public void setBiztypeList(List<String> biztypeList) {
		this.biztypeList = biztypeList;
	}

}