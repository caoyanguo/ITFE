package com.cfcc.itfe.client.recbiz.overdrawpayinto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-02-21 15:15:51 ��ϵͳ: RecBiz ģ��:overdrawPayInto ���:OverdrawPayInto
 */
@SuppressWarnings("unchecked")
public class OverdrawPayIntoBean extends AbstractOverdrawPayIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(OverdrawPayIntoBean.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// ƾ֤�ܱ���
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	private BigDecimal totalmoney_rel = new BigDecimal("0.00");
	private GrantPaySubBean subBean = null;
	private static String filemainpath;
	private List filelist;
	private List checklist;
	private List ftpchecklist;
	private List ftpfilelist;
	private List ftpfilepath;
	private boolean isselect=true;
	public OverdrawPayIntoBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvGrantpayplanMainDto();
		setSubBean(new GrantPaySubBean(commonDataAccessService, searchdto));
		// searchdto.setDaccept(DateUtil.currentDate());
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		filemainpath="/itfe/czftp/";
		checklist = new ArrayList();
		init();
	}
	private void init()
	{
		try {
			filelist = uploadConfirmService.getDirectGrantFileList(filemainpath, "020");
		} catch (Throwable e) {
			log.error(e);
			if(e.toString().contains("HttpInvokerException")){
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧ��\r\n�����µ�¼��");
			}else{
				MessageDialog.openMessageDialog(null, "��ȡ�ļ��б�ʧ�ܣ�");
			}
		}
	}
	/**
	 * Direction: ������Ŀ¼ˢ��
	 * ename: servicedirRefresh
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String servicedirRefresh(Object o){
        init();
        editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.servicedirRefresh(o);
    }
	/**
	 * Direction: ������Ŀ¼����
	 * ename: serviceFileAdd
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String serviceFileAdd(Object o){
    	String errName = "";
		int errCount = 0;
		int wrongFileSum = 0;
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		List<String> servicefilelist = null;
		List<String> oldfilelist = null;
		if (checklist == null||checklist.size()<=0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���صļ�¼!");
			return super.serviceFileAdd(o);
		}
		try
		{
			
			if (checklist != null && checklist.size() > 0) {
				servicefilelist = new ArrayList<String>();
				oldfilelist = new ArrayList<String>();
				Map fileMap = null;
				for(int i=0;i<checklist.size();i++)
				{
					fileMap = (Map)checklist.get(i);
					servicefilelist.add(String.valueOf(fileMap.get("newfilepath")));
					oldfilelist.add(String.valueOf(fileMap.get("oldfilepath")));
				}
				MulitTableDto bizDto = fileResolveCommonService.loadFile(servicefilelist, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN,MsgConstant.SHOUQUAN_DAORU, null);
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
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil.wipeFileOut(servicefilelist, bizDto.getErrNameList()),BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
				errName = StateConstant.Import_Errinfo_DIR+ "ֱ��֧���ļ����������Ϣ("+ new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��").format(new java.util.Date()) + ").txt";
				if (!"".equals(errorStr.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorStr.toString());
				}
			}
			if (problemStr.toString().trim().length() > 0) {
				String noteInfo = "";
				if (servicefilelist != null && servicefilelist.size() > 0) {
					noteInfo = "��������" + servicefilelist.size() + "���ļ���������" + wrongFileSum
							+ "�������ļ�����Ϣ���£�\r\n";
				} else {
					noteInfo = "��������" + checklist.size() + "���ļ���������" + errCount
							+ "�������ļ���������Ϣ���¡���ϸ������Ϣ��鿴" + errName + "����\r\n";
				}
				MessageDialog.openMessageDialog(null, noteInfo+ problemStr.toString());
				if(oldfilelist!=null&&oldfilelist.size()>0)
				{
					List<String> dellist = new ArrayList<String>();
					for(String temfile:oldfilelist)
					{
						if(problemStr.toString().indexOf(temfile.substring(temfile.lastIndexOf("/")+1))<0)
							dellist.add(temfile);
					}
					if(dellist!=null&&dellist.size()>0)
						uploadConfirmService.delfilelist(dellist,"0");
				}
			} else {
				if(oldfilelist!=null&&oldfilelist.size()>0)
					uploadConfirmService.delfilelist(oldfilelist,"0");
				MessageDialog.openMessageDialog(null, "�ļ����سɹ�,���ι����سɹ� "+ checklist.size() + " ���ļ���");
			}
	    } catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	    init();
		checklist= new ArrayList();
		editor.fireModelChanged();
        return super.serviceFileAdd(o);
    }
    /**
	 * Direction: ��ת������Ŀ¼����
	 * ename: goServiceDirAdd
	 * ���÷���: 
	 * viewers: ������Ŀ¼��������
	 * messages: 
	 */
    public String goServiceDirAdd(Object o){
        init();
        return super.goServiceDirAdd(o);
    }
	/**
	 * Direction: ȫѡ��ѡ
	 * ename: selectAllOrOne
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllOrOne(Object o){
    	if(filelist==null||filelist.size()<=0)
    		return super.ftpselectall(o);
        if(checklist!=null&&checklist.size()>0)
        	checklist = new ArrayList();
        else
        {
        	if(filelist!=null&&filelist.size()>=100)
        		checklist = filelist.subList(0, 100);
        	else
        		checklist = filelist;
        	if(isselect)
        	{
        		MessageDialog.openMessageDialog(null, "Ϊ��ϵͳ����,ȫѡֻ��ѡ100����¼!");
        		isselect=false;
        	}
        }
    	editor.fireModelChanged();
        return super.selectAllOrOne(o);
    }
    /**
	 * Direction: ��������
	 * ename: pldownload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String pldownload(Object o){
    	if (checklist == null||checklist.size()<=0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���صļ�¼!");
			return super.pldownload(o);
		}

		// �ͻ������ر��ĵ�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + "02" + dirsep+TimeFacade.getCurrentStringTime() + dirsep + loginfo.getSorgcode()
				+ dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		List<String> servicefilelist = null;
		List<String> oldfilelist = null;
		if (checklist != null && checklist.size() > 0) {
			servicefilelist = new ArrayList<String>();
			oldfilelist = new ArrayList<String>();
			Map fileMap = null;
			for(int i=0;i<checklist.size();i++)
			{
				fileMap = (Map)checklist.get(i);
				servicefilelist.add(String.valueOf(fileMap.get("newfilepath")));
				oldfilelist.add(String.valueOf(fileMap.get("oldfilepath")));
			}
			try {
				for(int i=0;i<oldfilelist.size();i++)
				{
					String clientfile = clientpath+ servicefilelist.get(i).substring(servicefilelist.get(i).lastIndexOf(dirsep)+1);
					ClientFileTransferUtil.downloadFile(servicefilelist.get(i),clientfile);
				}
				
				MessageDialog.openMessageDialog(null, "������Ȩ֧���������سɹ���\n·��:"+clientpath );
			} catch (Exception e) {
				log.error("������Ȩ֧���������سɹ�", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
        return super.pldownload(o);
    }
	/**
	 * Direction: ɾ��������Ŀ¼�ļ�
	 * ename: deleteServiceDirFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deleteServiceDirFile(Object o){
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��ѡ���ļ�?")) {
	    	List<String> servicefilelist = null;
			List<String> oldfilelist = null;
			try {
				if (checklist != null && checklist.size() > 0) {
					servicefilelist = new ArrayList<String>();
					oldfilelist = new ArrayList<String>();
					Map fileMap = null;
					for(int i=0;i<checklist.size();i++)
					{
						fileMap = (Map)checklist.get(i);
						servicefilelist.add(String.valueOf(fileMap.get("newfilepath")));
						oldfilelist.add(String.valueOf(fileMap.get("oldfilepath")));
					}
					uploadConfirmService.delfilelist(oldfilelist,"1");
					MessageDialog.openMessageDialog(null, "ɾ���ļ��ɹ���");
					init();
					checklist = new ArrayList();
					editor.fireModelChanged();
				}else
					MessageDialog.openMessageDialog(null, "��ѡ��Ҫɾ�����ļ���");
			} catch (ITFEBizException e) {
				e.printStackTrace();
			}
    	}
        return super.deleteServiceDirFile(o);
    }
	public String selectEvent(Object o) {
		List<TbsTvGrantpayplanMainDto> mlist = (List<TbsTvGrantpayplanMainDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvGrantpayplanMainDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFzerosumamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
		return super.selectEvent(o);
	}

	/**
	 * Direction: ���ݼ��� ename: dateload ���÷���: viewers: * messages:
	 */
	public String dateload(Object o) {
		String interfacetype = ""; // �ӿ�����
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		int sumFile = 0; // ͳ�����е����txt�ļ�
		int wrongFileSum = 0; // ͳ�ƴ����ļ��ĸ������������20��������׷�Ӵ�����Ϣ
		String resultType = "";
		// �ж��Ƿ�ѡ���ļ�
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
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
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName(); // �ļ�������
				String tmpfilepath = tmpfile.getAbsolutePath(); // ��ȡ�ļ���·��
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
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, tmpfilename);
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
				//�ж��ļ��Ƿ���ڣ�����Ѿ����ڣ���ɾ��
				String serverFilePath=commonDataAccessService.getServerRootPath(tmpfilepath, loginfo.getSorgcode());
				FileUtil.getInstance().deleteFileForExists(serverFilePath);
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
						fileList, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN,
						resultType, null);
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
				// ��¼������־
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
				errName = StateConstant.Import_Errinfo_DIR
						+ "��Ȩ֧���ļ����������Ϣ("
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
		}catch (FileTransferException e) {
			log.error("ɾ���������ļ�ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		} catch (FileOperateException e) {
			log.error("���ɴ�����Ϣ�ļ�ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		} catch (Throwable e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			// ɾ���������ϴ�ʧ���ļ�
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e1);
			}
			return null;
		} 
		filepath = new ArrayList();
		this.editor.fireModelChanged();
		return super.dateload(o);
	}

	/**
	 * Direction: ��ת�������Ų�ѯ ename: topiliangxh ���÷���: viewers: �������� messages:
	 */
	public String topiliangxh(Object o) {
		return super.topiliangxh(o);
	}

	/**
	 * Direction: ��ת������Ų�ѯ ename: tozhubixh ���÷���: viewers: ������� messages:
	 */
	public String tozhubixh(Object o) {
		return super.tozhubixh(o);
	}

	/**
	 * Direction: ֱ���ύ ename: submit ���÷���: viewers: * messages:
	 */
	public String submit(Object o) {
		try {
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, loginfo
							.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
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
	 * Direction: ����Ĭ�Ͻ��� ename: goback ���÷���: viewers: ֱ��֧����ȵ��� messages:
	 */
	public String goback(Object o) {
		return super.goback(o);
	}

	/**
	 * Direction: ����ȷ�� ename: plsubmit ���÷���: viewers: * messages:
	 */
	public String plsubmit(Object o) {
		try {
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, loginfo
							.getSorgcode())) {
				if (null == selectedfilelist || selectedfilelist.size() == 0) {
					MessageDialog.openMessageDialog(null, "��ѡ��һ����¼��");
					return null;
				}

				String area = AreaSpecUtil.getInstance().getArea();
				if ("FUZHOU".equals(area)) {
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
			} else {
				MessageDialog.openMessageDialog(null, "û����������Ȩ�ޣ��������Ա��ϵ��");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("�����ύʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.plsubmit(o);
	}
	/**
	 * Direction: ȫѡ ename: selectall ���÷���: viewers: * messages:
	 */
	public String pldselectall(Object o) {
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			selectedfilelist = new ArrayList();
			selectedfilelist.addAll(showfilelist);
		} else {
			selectedfilelist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.pldselectall(o);
	}
	/**
	 * Direction: ����ɾ�� ename: pldel ���÷���: viewers: * messages:
	 */
	public String pldel(Object o) {
		// ȷ���Ѿ���ѡ��ļ�¼
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫɾ���ļ�¼��");
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
							BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, dto);
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
	 * Direction: ����ύ ename: zbsubmit ���÷���: viewers: * messages:
	 */
	public String zbsubmit(Object o) {
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
					TbsTvGrantpayplanMainDto dto = (TbsTvGrantpayplanMainDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, dto);
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

	// /**
	// * Direction: �������Ų�ѯ ename: plsearch ���÷���: viewers: * messages:
	// */
	// public String plsearch(Object o) {
	// // ���������ӿڻ�ȡshowfilelist�����showfilelist��������¼����ѡ�У�����ֱ��ѡ��
	// try {
	// showfilelist = new ArrayList();
	// selectedfilelist = new ArrayList();
	// showfilelist = uploadConfirmService.batchQuery(
	// BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, sumamt);
	// if (null != showfilelist) {
	// if (1 == showfilelist.size()) {
	// selectedfilelist.addAll(showfilelist);
	// }
	// }
	// this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	// } catch (ITFEBizException e) {
	// log.error("�������Ų�ѯ����", e);
	// MessageDialog.openErrorDialog(null, e);
	// return null;
	// }
	//
	// return super.plsearch(o);
	// }

	/**
	 * Direction: ������Ų�ѯ ename: zbsearch ���÷���: viewers: * messages:
	 */
	public String zbsearch(Object o) {
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		try {
			// �����������
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// δ����
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, searchdto);
			// ͳ�Ʊ������
			List<TbsTvGrantpayplanMainDto> mlist = (List<TbsTvGrantpayplanMainDto>) showdatalist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvGrantpayplanMainDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFzerosumamt());
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
	 * Direction: ȫѡ ename: selectall ���÷���: viewers: * messages:
	 */
	public String selectall(Object o) {
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
	 * Direction: ��Ȩ˫���¼� ename: doubleClickEvent ���÷���: viewers: ����Ϣ��¼��ʾ
	 * messages:
	 */
	public String doubleClickEvent(Object o) {
		if (null != o) {
			TbsTvGrantpayplanMainDto main = (TbsTvGrantpayplanMainDto) o;
			PageRequest subRequest = new PageRequest();
			this.getSubBean().setMainDto(main);
			this.getSubBean().retrieve(subRequest);
		}
		editor.fireModelChanged();
		return super.doubleClickEvent(o);
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

	// �����ļ�����
	// /////////////////////////////////////////////////////////////////////////////////////////////
	private String getFileObjByFileNameBiz(String biztype, String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();
		// �����˿� ���������ţ�����ļ���
		if (tmpfilename_new.indexOf(".txt") > 0) {
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			// ʮ��λ˵�����п����ǵط�������tbs boolΪtrue ��Ϊtbs�ӿڷ���Ϊ�ط�����
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
				interficetype = MsgConstant.SHOUQUAN_DAORU; // tbs�ӿ�
				if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(fileobj
						.getSbiztype())) {
				} else {
					return "�ļ�" + filename + "ҵ�����Ͳ����ϣ�\r\n";
					// throw new ITFEBizException("ҵ�����Ͳ�����!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					return "�ļ�" + filename + "�����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1�������ڣ�\r\n";
					// throw new
					// ITFEBizException("�����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1��������!");
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
							// throw new ITFEBizException("������" +
							// com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate)
							// + "�ѹ������ܴ��������ҵ��");
						}
					}
				}

			} else {
				return "�ļ�" + filename + "�ļ�����ʽ������\r\n";
				// throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else {
			return "�ļ�" + filename + "�ļ�����ʽ������\r\n";
			// throw new ITFEBizException("�ļ�����ʽ����!");
		}
		return interficetype;

		// ҵ��������Ҫ�Ķ�
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
					.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
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
					MessageDialog.openMessageDialog(null, "��Ȩ֧����Ȳ�����δ��������!");
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
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, sumamt);
				if (null != showfilelist) {
					if (1 == showfilelist.size()) {
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						// massdispose();
					}
					this.editor
							.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
					if (showfilelist.size() <= 0) {
						MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
					}
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
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		}

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

	public GrantPaySubBean getSubBean() {
		return subBean;
	}

	public void setSubBean(GrantPaySubBean subBean) {
		this.subBean = subBean;
	}

	public String getFilemainpath() {
		return filemainpath;
	}

	public void setFilemainpath(String filemainpath) {
		if(filemainpath.indexOf("/itfe/czftp/")>=0||filemainpath.indexOf("/itfe/nnczftp/")>=0)
			OverdrawPayIntoBean.filemainpath = filemainpath;
		else
			OverdrawPayIntoBean.filemainpath="/itfe/czftp/";
	}

	public List getFilelist() {
		return filelist;
	}

	public void setFilelist(List filelist) {
		this.filelist = filelist;
	}

	public List getChecklist() {
		return checklist;
	}

	public void setChecklist(List checklist) {
		this.checklist = checklist;
	}
	public List getFtpchecklist() {
		return ftpchecklist;
	}
	public void setFtpchecklist(List ftpchecklist) {
		this.ftpchecklist = ftpchecklist;
	}
	public List getFtpfilelist() {
		return ftpfilelist;
	}
	public void setFtpfilelist(List ftpfilelist) {
		this.ftpfilelist = ftpfilelist;
	}
	public List getFtpfilepath() {
		return ftpfilepath;
	}
	public void setFtpfilepath(List ftpfilepath) {
		this.ftpfilepath = ftpfilepath;
	}
	/**
	 * Direction: ��תftp�ļ�����
	 * ename: goftpfilemanager
	 * ���÷���: 
	 * viewers: ftp�ļ�����
	 * messages: 
	 */
    public String goftpfilemanager(Object o){
        this.ftpchecklist = new ArrayList();
        this.ftpfilepath = new ArrayList();
        isselect = true;
        try {
			ftpfilelist = uploadConfirmService.getDirectGrantFileList(filemainpath+"swap/", "ftp");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ȡ�ļ��б�ʧ�ܣ�");
		}
        return super.goftpfilemanager(o);
    }
    
	/**
	 * Direction: ftpȫѡ��ѡ
	 * ename: ftpselectall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftpselectall(Object o){
    	if(ftpfilelist==null||ftpfilelist.size()<=0)
    		return super.ftpselectall(o);
        if(ftpchecklist!=null&&ftpchecklist.size()>0)
        	ftpchecklist = new ArrayList();
        else
        {
        	if(ftpfilelist!=null&&ftpfilelist.size()>=100)
        		ftpchecklist = ftpfilelist.subList(0, 100);
        	else
        		ftpchecklist = ftpfilelist;
        	if(isselect)
        	{
        		MessageDialog.openMessageDialog(null, "Ϊ��ϵͳ����,ȫѡֻ��ѡ100����¼!");
        		isselect=false;
        	}
        }
        this.editor.fireModelChanged();
        return super.ftpselectall(o);
    }
    
	/**
	 * Direction: ftp����
	 * ename: ftpdownload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftpdownload(Object o){
    	if (ftpchecklist == null||ftpchecklist.size()<=0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���صļ�¼!");
			return super.ftpdownload(o);
		}

		// �ͻ������ر��ĵ�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + filemainpath +TimeFacade.getCurrentStringTimebefor() + dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		List<String> servicefilelist = null;
		if (ftpchecklist != null && ftpchecklist.size() > 0) {
			servicefilelist = new ArrayList<String>();
			Map fileMap = null;
			try {
				for(int i=0;i<ftpchecklist.size();i++)
				{
					fileMap = (Map)ftpchecklist.get(i);
					servicefilelist.addAll(uploadConfirmService.getDirectGrantFileList(String.valueOf(fileMap.get("filepath")),"ftpdownload"));
				}
				for(int i=0;i<servicefilelist.size();i++)
				{
					String clientfile = clientpath+ servicefilelist.get(i).substring(servicefilelist.get(i).lastIndexOf(dirsep)+1);
					ClientFileTransferUtil.downloadFile(servicefilelist.get(i),clientfile);
				}
				DeleteServerFileUtil.delFile(commonDataAccessService,servicefilelist);
				MessageDialog.openMessageDialog(null, "Ftp�ļ����سɹ���\n·��:"+clientpath );
			} catch (Exception e) {
				log.error("Ftp�ļ�����ʧ��:", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
        return super.ftpdownload(o);
    }
    
	/**
	 * Direction: ftpɾ��
	 * ename: ftpdeletefile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftpdeletefile(Object o){
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��ѡ���ļ�?")) {
	    	List<String> servicefilelist = null;
			try {
				if (ftpchecklist != null && ftpchecklist.size() > 0) {
					servicefilelist = new ArrayList<String>();
					Map fileMap = null;
					for(int i=0;i<ftpchecklist.size();i++)
					{
						fileMap = (Map)ftpchecklist.get(i);
						servicefilelist.add(String.valueOf(fileMap.get("filepath")));
					}
					uploadConfirmService.delfilelist(servicefilelist,"2");
					MessageDialog.openMessageDialog(null, "ɾ���ļ��ɹ���");
					ftprefresh(o);
					ftpchecklist = new ArrayList();
					editor.fireModelChanged();
				}else
					MessageDialog.openMessageDialog(null, "��ѡ��Ҫɾ�����ļ���");
			} catch (ITFEBizException e) {
				e.printStackTrace();
			}
    	}
        return super.ftpdeletefile(o);
    }
    /**
	 * Direction: ftp�ϴ��ļ�
	 * ename: ftpupload
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftpupload(Object o){
    	// �ж��Ƿ�ѡ���ļ�
		if (null == ftpfilepath || ftpfilepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		if(ftpfilepath!=null&&ftpfilepath.size()>0)
		{
			File tmpfile = null;
			String serverpath = null;
			try
			{
				for(int i=0;i<ftpfilepath.size();i++)
				{
					tmpfile = (File) ftpfilepath.get(i);
					// �ļ��ϴ�����¼��־
					serverpath = ClientFileTransferUtil.uploadFile(tmpfile.getAbsolutePath());
					uploadConfirmService.getDirectGrantFileList(serverpath, "ftpupload"+filemainpath);
				}
			} catch (Exception e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}
		MessageDialog.openMessageDialog(null, "�ļ��ϴ��ɹ���");
		goftpfilemanager(o);
		editor.fireModelChanged();
        return super.ftpupload(o);
    }
	/**
	 * Direction: ftpˢ��
	 * ename: ftprefresh
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ftprefresh(Object o){
    	this.ftpchecklist = new ArrayList();
        this.ftpfilepath = new ArrayList();
        try {
			ftpfilelist = uploadConfirmService.getDirectGrantFileList(filemainpath+"swap/", "ftp");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ȡ�ļ��б�ʧ�ܣ�");
		}
		editor.fireModelChanged();
        return super.ftprefresh(o);
    }
}