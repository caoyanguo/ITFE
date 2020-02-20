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
 * 客户端定时提醒凭证处理情况
 * @author hua
 *
 */
@SuppressWarnings("unchecked")
public class TimerVoucherInfoDialog extends TitleAreaDialog {
	
	private static Log log = LogFactory.getLog(TimerVoucherInfoDialog.class);
	
	private static final String TIMER_VOUCHER_RESULTMAP_KEY = "timerResult";
	private static final String TIMER_VOUCHER_EDITOR_ID = "99d5a4d2-d6b4-4f44-8007-961c083cab09";
	public static final String TIMER_VOUCHER_INFO_ERROR = "error";
	
	public static final String TIMER_VOUCHER_COMBO_TODAY = "查看当天";
	public static final String TIMER_VOUCHER_COMBO_THREEDAY = "最近三天";
	public static final String TIMER_VOUCHER_COMBO_SEVENDAY = "最近七天";
	
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
		newShell.setText("凭证处理情况定时提示:");
	}

	protected Point getInitialSize() {

		return new Point(600, 450);
	}

	protected int getShellStyle() {
		return SWT.MODELESS | SWT.RESIZE | SWT.SHELL_TRIM;
	}

	/**
	 * 创建主体窗口
	 */
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FormLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		String message = "本列表显示凭证处理情况信息, 如需人工查看,请打开菜单 [系统管理->系统监控信息->凭证处理情况信息] <红色表示存在【处理失败】的记录>:";
		setTitle("凭证处理进度一览表");
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
		tf.addColumn("国库代码", 80, "strecode");
		tf.addColumn("业务类型", 120, "sbizname");
		tf.addColumn("校验成功", 80, "count1");
		tf.addColumn("处理中", 80, "count2");
        tf.addColumn("处理失败", 80, "count3");
		tf.addColumn("已回单", 80, "count4");
		
		/* 查询下拉框*/
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
		
		/* 查询label 间隔时间选择 */
		Label timerLabel = new Label(container, SWT.SHADOW_IN | SWT.BOTTOM | SWT.PUSH);
		FormData labFormData = new FormData();
		labFormData.left = new FormAttachment(0, 5);
		labFormData.right = new FormAttachment(100, -5);
		labFormData.bottom = new FormAttachment(100, 0);
		labFormData.top = new FormAttachment(table, 10);
		timerLabel.setLayoutData(labFormData);
		timerLabel.setText("统计时间间隔：");
		
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
						log.error("在响应双击事件时出现异常(只记录).");
						if(editor != null) { //在异常出现的时候重试一次.
							try {
								AbstractMetaDataEditorPart newEditor = MVCUtils.openEditor(TIMER_VOUCHER_EDITOR_ID);
								initModel(newEditor);
							} catch (Exception e2) {
								log.error("在重新响应双击事件时继续出错.", e2);
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
			log.error("客户端定时提醒任务获取数据时出现异常(fetchVoucherInfoForClientTimer)", e);
			return genReturnInfo();
		}
		return timerVoucherInfoList;
	}
	
	private List<TimerVoucherInfoDto> genReturnInfo() {
		List<TimerVoucherInfoDto> timerVoucherInfoList = new ArrayList<TimerVoucherInfoDto>();;
		TimerVoucherInfoDto infoDto = new TimerVoucherInfoDto();
		infoDto.setSvtcode(TIMER_VOUCHER_INFO_ERROR);
		infoDto.setSbizname("获取数据出现异常,请检查网络或者联系系统管理员.!");
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
					createButton(parent, IDialogConstants.OPEN_ID, "前往处理>>", false);
			} catch (ITFEBizException e) {
			}
		}
		createButton(parent, IDialogConstants.OK_ID, "刷新", false);
		createButton(parent, IDialogConstants.CANCEL_ID, "关闭", true);
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
				log.error("在响应双击事件时出现异常(只记录).");
				if(editor != null) { //在异常出现的时候重试一次.
					try {
						MVCUtils.openEditor(TIMER_VOUCHER_EDITOR_ID);
//						initModel(newEditor);
					} catch (Exception e2) {
						log.error("在重新响应双击事件时继续出错.", e2);
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
