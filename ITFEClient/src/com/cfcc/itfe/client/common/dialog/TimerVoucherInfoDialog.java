package com.cfcc.itfe.client.common.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.recbiz.voucherload.VoucherLoadBean;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TimerVoucherInfoDto;
import com.cfcc.itfe.persistence.dto.TsUsersfuncDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IExportTBSForBJService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.rcp.mvc.IModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;

/**
 * �ͻ��˶�ʱ����ƾ֤�������
 * @author hua
 *
 */
@SuppressWarnings("unchecked")
public class TimerVoucherInfoDialog extends TitleAreaDialog {
	
	private static Log log = LogFactory.getLog(TimerVoucherInfoDialog.class);
	
	private static final String TIMER_VOUCHER_RESULTMAP_KEY = "timerResult";
	private static final String TIMER_VOUCHER_EDITOR_ID = "99d5a4d2-d6b4-4f44-8007-961c083cab09";
	public static final String TIMER_VOUCHER_INFO_ERROR = "error";
	
	public static final String TIMER_VOUCHER_COMBO_TODAY = "�鿴����";
	public static final String TIMER_VOUCHER_COMBO_THREEDAY = "�������";
	public static final String TIMER_VOUCHER_COMBO_SEVENDAY = "�������";
	
	private IExportTBSForBJService timerService = (IExportTBSForBJService) ServiceFactory.getService(IExportTBSForBJService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
	private Table table;
	private TableViewer tableViewer;
	private IDto selectdto;
	protected Label imageLabel;
	private List<TimerVoucherInfoDto> timerVoucherInfoList = null;
	private String startDate = "";
	private String endDate = "";
	private Combo timerCombo;

	public TimerVoucherInfoDialog(Shell parentShell) {
		super(parentShell);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		startDate = simpleDateFormat.format(new Date());
		endDate = simpleDateFormat.format(new Date());
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("ƾ֤���������ʱ��ʾ:");
	}

	protected Point getInitialSize() {

		return new Point(600, 450);
	}

	protected int getShellStyle() {
		return SWT.MODELESS | SWT.RESIZE | SWT.SHELL_TRIM;
	}

	/**
	 * �������崰��
	 */
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FormLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		String message = "���б���ʾƾ֤���������Ϣ, �����˹��鿴,��򿪲˵� [ϵͳ����->ϵͳ�����Ϣ->ƾ֤���������Ϣ] <��ɫ��ʾ���ڡ�����ʧ�ܡ��ļ�¼>:";
		setTitle("ƾ֤�������һ����");
		this.setMessage(message, IMessageProvider.INFORMATION);

		/* table */
		table = new Table(container, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);

		final FormData formData = new FormData();
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -35);
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		table.setLayoutData(formData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		tableViewer = new TableViewer(table);

		TableFacadeX tf = new TableFacadeX(tableViewer);
		tf.addColumn("�������", 80, "strecode");
		tf.addColumn("ҵ������", 120, "sbizname");
		tf.addColumn("У��ɹ�", 80, "count1");
		tf.addColumn("������", 80, "count2");
        tf.addColumn("����ʧ��", 80, "count3");
		tf.addColumn("�ѻص�", 80, "count4");
		
		/* ��ѯ������*/
		timerCombo = new Combo(container, SWT.PUSH | SWT.READ_ONLY);
		FormData comboFormData = new FormData();
		comboFormData.width = 70;
		comboFormData.left = new FormAttachment(0, 100);
		comboFormData.bottom = new FormAttachment(100, 0);
		comboFormData.top = new FormAttachment(table, 10);
		
		timerCombo.setLayoutData(comboFormData);
		
		timerCombo.add(TIMER_VOUCHER_COMBO_TODAY);
		timerCombo.add(TIMER_VOUCHER_COMBO_THREEDAY);
		timerCombo.add(TIMER_VOUCHER_COMBO_SEVENDAY);
		timerCombo.select(0);
		
		timerCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				Combo c = (Combo) e.getSource();
				if (c != null) {
					if (c.getText() != null && !c.getText().equals("")&&c.getText().equals(TIMER_VOUCHER_COMBO_TODAY) ){
						startDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
					} else if (c.getText() != null && !"".equals(c.getText()) && c.getText().equals(TIMER_VOUCHER_COMBO_THREEDAY) ){
						startDate = DateUtil.dateBefore(new Date(), 2, "").replaceAll("-", "");
					} else if (c.getText() != null && !"".equals(c.getText()) && c.getText().equals(TIMER_VOUCHER_COMBO_SEVENDAY) ){
						startDate = DateUtil.dateBefore(new Date(), 6, "").replaceAll("-", "");
					}
					refreshTableInfo();
				}				
			}
		});
		
		/* ��ѯlabel ���ʱ��ѡ�� */
		Label timerLabel = new Label(container, SWT.SHADOW_IN | SWT.BOTTOM | SWT.PUSH);
		FormData labFormData = new FormData();
		labFormData.left = new FormAttachment(0, 5);
		labFormData.right = new FormAttachment(100, -5);
		labFormData.bottom = new FormAttachment(100, 0);
		labFormData.top = new FormAttachment(table, 10);
		timerLabel.setLayoutData(labFormData);
		timerLabel.setText("ͳ��ʱ������");
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection iss = (IStructuredSelection) event.getSelection();
				if (iss != null && iss.size() > 0) {
					selectdto = (IDto) iss.getFirstElement();
				}
			}
		});

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection iss = (IStructuredSelection) event.getSelection();
				if (iss != null && iss.size() > 0) {
					AbstractMetaDataEditorPart editor = null;
					try {
						selectdto = (IDto) iss.getFirstElement();
						cancelPressed();
						editor = MVCUtils.getEditor(TIMER_VOUCHER_EDITOR_ID);
						initModel(editor);
						MVCUtils.reopenCurrentComposite(editor);
					} catch (Exception e) {
						log.error("����Ӧ˫���¼�ʱ�����쳣(ֻ��¼).");
						if(editor != null) { //���쳣���ֵ�ʱ������һ��.
							try {
								AbstractMetaDataEditorPart newEditor = MVCUtils.openEditor(TIMER_VOUCHER_EDITOR_ID);
								initModel(newEditor);
							} catch (Exception e2) {
								log.error("��������Ӧ˫���¼�ʱ��������.", e2);
							}
						}
					}
				}
				//okPressed();
			}

		});
		
		tableViewer.getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					if (null == selectdto) {
						selectdto = (IDto) tableViewer.getElementAt(0);
					}
					//okPressed();
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		
		bind();
		table.pack();
		return area;
	}

	private void initModel(AbstractMetaDataEditorPart editor) {
		IModel model = editor.getModel();
		if(model != null && selectdto !=null && selectdto instanceof TimerVoucherInfoDto &&  model instanceof VoucherLoadBean) {
			VoucherLoadBean beanInstance = (VoucherLoadBean) model;
			TimerVoucherInfoDto timerInfoDto = (TimerVoucherInfoDto) selectdto;
			beanInstance.setVoucherType(timerInfoDto.getSvtcode());
		}
	}
	
	public void bind() {
		
		tableViewer.setContentProvider(new TimerVoucherInfoContentProvider());
		tableViewer.setLabelProvider(new TimerVoucherInfoLabelProvider());
		
		tableViewer.setInput(retriveTimerInfo());
		tableViewer.refresh();
	}

	public List<TimerVoucherInfoDto> retriveTimerInfo(){
		Map<String, List<TimerVoucherInfoDto>> resMap = null;
		try {
			resMap =  timerService.fetchVoucherInfoForClientTimer(startDate, endDate, null, null);
			if(resMap != null && resMap.size() > 0 && resMap.containsKey(TIMER_VOUCHER_RESULTMAP_KEY)) {
				timerVoucherInfoList =  resMap.get(TIMER_VOUCHER_RESULTMAP_KEY);
			} else {
				timerVoucherInfoList = new ArrayList<TimerVoucherInfoDto>(0);//genReturnInfo();
			}
		}  catch (Exception e) {
			log.error("�ͻ��˶�ʱ���������ȡ����ʱ�����쳣(fetchVoucherInfoForClientTimer)", e);
			return genReturnInfo();
		}
		return timerVoucherInfoList;
	}
	
	private List<TimerVoucherInfoDto> genReturnInfo() {
		List<TimerVoucherInfoDto> timerVoucherInfoList = new ArrayList<TimerVoucherInfoDto>();;
		TimerVoucherInfoDto infoDto = new TimerVoucherInfoDto();
		infoDto.setSvtcode(TIMER_VOUCHER_INFO_ERROR);
		infoDto.setSbizname("��ȡ���ݳ����쳣,�������������ϵϵͳ����Ա.!");
		timerVoucherInfoList.add(infoDto);
		return timerVoucherInfoList;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		TsUsersfuncDto sysuser = new TsUsersfuncDto();
		ITFELoginInfo userinfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		if(userinfo!=null)
		{
			sysuser.setSorgcode(userinfo.getSorgcode());
			sysuser.setSusercode(userinfo.getSuserCode());
			sysuser.setSfunccode("B_018");
			try {
				List dtolist = (List)commonDataAccessService.findRsByDto(sysuser);
				if(dtolist!=null&&dtolist.size()>0)
					createButton(parent, IDialogConstants.OPEN_ID, "ǰ������>>", false);
			} catch (ITFEBizException e) {
			}
		}
		createButton(parent, IDialogConstants.OK_ID, "ˢ��", false);
		createButton(parent, IDialogConstants.CANCEL_ID, "�ر�", true);
	}
	
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if (null == selectdto) {
				refreshTableInfo();
				return;
			}
//			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		} else if (IDialogConstants.OPEN_ID == buttonId) {
			cancelPressed();
			AbstractMetaDataEditorPart editor = null;
			try{	
				editor = MVCUtils.getEditor(TIMER_VOUCHER_EDITOR_ID);
				MVCUtils.reopenCurrentComposite(editor);
			} catch (Exception e) {
				log.error("����Ӧ˫���¼�ʱ�����쳣(ֻ��¼).");
				if(editor != null) { //���쳣���ֵ�ʱ������һ��.
					try {
						MVCUtils.openEditor(TIMER_VOUCHER_EDITOR_ID);
//						initModel(newEditor);
					} catch (Exception e2) {
						log.error("��������Ӧ˫���¼�ʱ��������.", e2);
					}
				}
			}
		}
	}
	
	private void refreshTableInfo(){
		this.timerVoucherInfoList = retriveTimerInfo();
		this.tableViewer.setInput(this.timerVoucherInfoList);
	}

	/****************** Getter Setter ******************/
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public IDto getSelectdto() {
		return selectdto;
	}

	public void setSelectdto(IDto selectdto) {
		this.selectdto = selectdto;
	}

	public IExportTBSForBJService getTimerService() {
		return timerService;
	}

	public void setTimerService(IExportTBSForBJService timerService) {
		this.timerService = timerService;
	}

	public List<TimerVoucherInfoDto> getTimerVoucherInfoList() {
		return timerVoucherInfoList;
	}

	public void setTimerVoucherInfoList(List<TimerVoucherInfoDto> timerVoucherInfoList) {
		this.timerVoucherInfoList = timerVoucherInfoList;
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

	public Combo getTimerCombo() {
		return timerCombo;
	}

	public void setTimerCombo(Combo timerCombo) {
		this.timerCombo = timerCombo;
	}

	public static void main(String[] args) {
		TimerVoucherInfoDialog dialog = new TimerVoucherInfoDialog(null);
		dialog.open();
	}
}
