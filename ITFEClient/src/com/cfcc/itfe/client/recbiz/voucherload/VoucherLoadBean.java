package com.cfcc.itfe.client.recbiz.voucherload;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.CheckFaultMsgDialogFacade;
import com.cfcc.itfe.client.dialog.CheckVoucherStatusMsgDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsQueryAmtDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataListDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TsOrganPK;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.persistence.pk.TvPayoutmsgmainPK;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author hjr
 * @time 13-07-05 00:26:06 ��ϵͳ: RecBiz ģ��:voucherLoad ���:VoucherLoad
 */
@SuppressWarnings("unchecked")
public class VoucherLoadBean extends AbstractVoucherLoadBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(VoucherLoadBean.class);
	List<TvVoucherinfoDto> checkList = null;

	// �û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	private String stamp;
	private List stampList = null;
	private String voucherType = null;
	private List voucherTypeList = null;
	private List bizTypeMapperList = null;
	private TvPayreckBigdataDto bigdatadto = null;
	private TvPayreckBigdataBackDto bigdatabackdto = null;
	private PagingContext bigdatapage;
	private PagingContext bigdatabackpage;
	private Map<String, TsQueryAmtDto> amtMap;
	private String searchtype = "";
	private List mainlist;
	public VoucherLoadBean() {
		super();
		dto = new TvVoucherinfoDto();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		checkList = new ArrayList();
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSorgcode(loginInfo.getSorgcode());
		bigdatadto = new TvPayreckBigdataDto();
		bigdatabackdto = new TvPayreckBigdataBackDto();
		mainlist = new ArrayList();
		pagingcontext = new PagingContext(this);
		bigdatapage = new PagingContext(this);
		bigdatabackpage = new PagingContext(this);
		voucherTypeList = new itferesourcepackage.SVoucherTypeEnumFactory()
				.getEnums(null);

	}

	/**
	 * ��ȡOCX�ؼ�url
	 * 
	 * @return
	 */
	public String getOcxVoucherServerURL() {
		String ls_URL = "";
		try {
			ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("��ȡOCX�ؼ�URL��ַ���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return ls_URL;
	}

	/**
	 * ��ȡǩ�·����ַ
	 * 
	 * @return
	 */
	public String getOCXStampServerURL() {
		String ls_URL = "";
		try {
			ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("��ȡOCX�ؼ�ǩ�·���URL��ַ���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return ls_URL;
	}
	/**
	 * Direction: ��������
	 * ename: exportData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportData(Object o){
    	try {
    		if(checkList==null||checkList.size()<=0)
    		{
    			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���������ݣ�");
				return "";
    		}
			// ѡ�񱣴�·��
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
			String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
				return "";
			}
			StringBuffer vouchercontext = voucherLoadService.exportData(checkList); 
			String fileName = filePath+ File.separator+ checkList.get(0).getSvtcode() +checkList.get(0).getScreatdate() + ".xml";
			try {
				FileUtil.getInstance().writeFile(fileName, vouchercontext.toString());
				MessageDialog.openMessageDialog(null, "�����ɹ���");
			} catch (Exception e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null,e.toString());
		}
        return super.exportData(o);
    }
	/**
	 * Direction: ��ѯ ename: search ���÷���: viewers: * messages:
	 */
	public String search(Object o) {
		if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			MessageDialog.openMessageDialog(null, "����ѡ��ƾ֤���ͣ�");
			return "";
		}
		if(loginInfo.getPublicparam().contains(",voucherload=trecode,"))
		{
			if(dto.getStrecode() == null|| dto.getStrecode().equals(""))
			{
				List<TsTreasuryDto> findTreCodeList = null;
				TsTreasuryDto findto = new TsTreasuryDto();
				findto.setSorgcode(loginInfo.getSorgcode());
				try {
					findTreCodeList = commonDataAccessService.findRsByDto(findto);
					dto.setStrecode(findTreCodeList.get(0).getStrecode());
				} catch (ITFEBizException e) {
				}
			}
		}
		refreshTable();
		return super.search(o);
	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = new PageResponse();
		pageResponse = retrieve(pageRequest);
		if (pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: ��ȡƾ֤ ename: voucherLoad ���÷���: viewers: * messages:
	 */
	public String voucherLoad(Object o) {
		int count = 0;
		try {
			count = voucherLoadService.voucherLoad(dto.getSvtcode(), loginInfo
					.getSorgcode());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "���÷��������ȷ�������ӵ���������");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}
		if (count == -1) {
			MessageDialog.openMessageDialog(null, "ƾ֤����������ӳ�ʱ�������ԣ�");
		} else {
			MessageDialog.openMessageDialog(null, "�ɹ���ȡ��" + count + "����¼�����ѯ��");
		}
		refreshTable();
		return super.voucherLoad(o);
	}

	/**
	 * Direction: ȫѡ ename: selectAll ���÷���: viewers: * messages:
	 */
	public String selectAll(Object o) {
		if (checkList == null || checkList.size() == 0) {
			checkList = new ArrayList();
			checkList.addAll(pagingcontext.getPage().getData());
		} else
			checkList.clear();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.selectAll(o);
	}

	/**
	 * Direction: У�� ename: voucherVerify ���÷���: viewers: * messages:
	 */
	public String voucherVerify(Object o) {
		int count = 0;

		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��ҪУ��ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		for (int i = 0; i < checkList.size(); i++) {
			String ls_Status = checkList.get(i).getSstatus().trim();
			if (!ls_Status.equals(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS)
					&& !ls_Status
							.equals(DealCodeConstants.VOUCHER_VALIDAT_FAIL)
					&& !ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT)) {
				MessageDialog
						.openMessageDialog(null,
								"ֻ��״̬Ϊ\"ǩ�ճɹ�\" , \"У��ʧ��\" "
										+ (loginInfo.getPublicparam().indexOf(
												",sh,") >= 0 ? ",\"У����\"" : "")
										+ " ��ƾ֤��ִ�д˲���!");
				return "";
			}
			if (ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_FAIL)
					&& checkList.get(i).getSdemo().startsWith("ҵ��Ҫ�ع���ʧ��")) {
				MessageDialog.openMessageDialog(null, "ҵ��Ҫ�ع���ʧ�ܵ�ƾ֤��������У��!");
				return "";
			}
		}

		try {
			count = voucherLoadService.voucherVerify(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤У��" + checkList.size()
					+ "�����ɹ�����Ϊ��" + count);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤У����������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		refreshTable();
		return super.voucherCommit(o);
	}

	/**
	 * Direction: 
�ɹ� ename: voucherUpdateSuccess ���÷���: viewers: * messages:
	 */
	public String voucherUpdateSuccess(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ����״̬�ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
				dto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
			return "";
		}
		boolean flag = false;
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		for (TvVoucherinfoDto vDto : this.checkList) {
			TvVoucherinfoDto voucherDto = null;
			try {
				voucherDto = getDto(vDto);
			} catch (ITFEBizException e) {
				log.error("����״̬�����г��ִ���", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e);
				return "";
			} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null,
						"�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
				return "";
			} catch (Exception e) {
				log.error(e);
				Exception e1 = new Exception("����״̬�����г����쳣��", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
				return "";
			}
    		String ls_Status =  voucherDto.getSstatus();
			if((ls_Status.equals(DealCodeConstants.VOUCHER_SUCCESS) &&(vDto.getSstampid()!=null &&
					vDto.getSstampid().getBytes().length>0))){
				MessageDialog.openMessageDialog(null, "ƾ֤"+vDto.getSvoucherno()+"�ص��ɹ����ܸ���״̬��");    			
        		return "";			
    		}else if((ls_Status.equals(DealCodeConstants.VOUCHER_FAIL) &&(vDto.getSstampid()!=null &&
					vDto.getSstampid().getBytes().length>0))){
				MessageDialog.openMessageDialog(null, "ƾ֤"+vDto.getSvoucherno()+"�˻سɹ����ܸ���״̬��");    			
        		return "";			
    		}else
				try {
					if(!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TCBS)||ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS))&&
							!((loginInfo.getPublicparam().contains(",updatestatus=all,"))&&(DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(ls_Status)||ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)||ls_Status.equals(DealCodeConstants.VOUCHER_CHECKSUCCESS)||ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS)||ls_Status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)))
							&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)&&
							((StateConstant.COMMON_NO.equals(voucherLoadService.getIsitfecommit())&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)))||
							vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)||
							vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207) || vDto.getSvtcode().equals(
									MsgConstant.VOUCHER_NO_2252) ||
							(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)&&vDto.getShold2().equals(StateConstant.PAYOUT_PAY_CODE))))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_RECIPE))&&
							!(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5407))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_FAULT))&&
							!(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))){
						MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ\"TCBS����ʧ��\" ,  \"�ѷ���\" , \"������\" ,\t\n��״̬Ϊ \"У��ɹ�\" ƾ֤����Ϊ 2301 ��2302 " +
								(loginInfo.getPublicparam().indexOf(",sh,")>=0?"��8207�� 5253�� 5351":"")+
								(loginInfo.getPublicparam().indexOf(",sh,")>=0?"\t\n��״̬Ϊ \"У��ɹ�\"��֧����ʽΪʵ����ƾ֤����Ϊ 2252":"")+
								",\t\n��״̬Ϊ  \"����ɹ�\" δǩ�� \t\n��ƾ֤��ִ�д˲�����");    			
						return "";
					}else if(!voucherCompare(voucherDto, checkList)){
						return "";
					}
				} catch (ITFEBizException e) {
					MessageDialog.openMessageDialog(null, "ƾ֤����״̬ʧ�ܣ�"+e.toString());    			
	        		return "";
				}
			checkList.add(voucherDto);
    		if(loginInfo.getPublicparam().contains(",display=true,"))
    		{
    			if((!"display".equals(voucherDto.getSext4()))&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)))
    			{
    				MessageDialog.openMessageDialog(null, "ƾ֤"+voucherDto.getSvoucherno()+"����ƾ֤��ԭչʾ��ſɼ���������");    			
            		return "";
    			}
    		}
    		if(loginInfo.getPublicparam().contains(",display=all,"))
    		{
    			if((!"display".equals(voucherDto.getSext4()))&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
    			{
    				MessageDialog.openMessageDialog(null, "ƾ֤"+voucherDto.getSvoucherno()+"����ƾ֤��ԭչʾ��ſɼ���������");    			
            		return "";
    			}
    		}
    	}
		String msg = "��Ҫ������Ȩ���ܸ���ƾ֤״̬��";
		try {
			int i = 0;
			int j = 0;
			int k = 0;
			if (!flag) {
				if(!loginInfo.getPublicparam().contains(",adminconfrim=false,"))
				{
					if (!AdminConfirmDialogFacade.open(msg)) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						return null;
					}
				}
				TsCheckfailreasonDto checkDto = new TsCheckfailreasonDto();
				checkDto.setSorgcode(loginInfo.getSorgcode());
				List<TsCheckfailreasonDto> checkfail = commonDataAccessService
						.findRsByDto(checkDto);
				if (CheckVoucherStatusMsgDialogFacade.open(checkList,
						checkfail, commonDataAccessService)) {
					for (TvVoucherinfoDto vDto : checkList) {
						updateMainDto(vDto);
						commonDataAccessService.updateData(vDto);
						// if (vDto.getSvtcode().equals(
						// MsgConstant.VOUCHER_NO_2252)) {
						// vDto.setSvtcode(MsgConstant.VOUCHER_NO_2302);
						// updateMainDto(vDto);
						// }
						if (vDto.getSstatus().equals(
								DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
							i++;
						} else if (vDto.getSstatus().equals(
								DealCodeConstants.VOUCHER_SUCCESS_BACK)){
							k++;
						}else {
							j++;
						}
					}
				}
			} else {
				for (TvVoucherinfoDto vDto : checkList) {
					vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					vDto.setSdemo("����ɹ�");
					updateMainDto(vDto);
					commonDataAccessService.updateData(vDto);
					i++;
				}
			}
			if (i > 0 || j > 0 || k > 0) {
				String updateMsg = "����״̬����ִ�гɹ���\t\n";
				if (i > 0) {
					updateMsg = updateMsg + "�ɹ�����ƾ֤״̬Ϊ \"����ɹ�\" ������" + i
							+ "\t\n";
				}
				if (j > 0) {
					updateMsg = updateMsg + "�ɹ�����ƾ֤״̬Ϊ \"TCBS����ʧ��\" ������" + j;
				}
				if (k > 0) {
					updateMsg = updateMsg + "�ɹ�����ƾ֤״̬Ϊ \"��Ʊ�ɹ�\" ������" + k
							+ "\t\n";
				}
				MessageDialog.openMessageDialog(null, updateMsg);
				refreshTable();
				AdminConfirmDialogFacade.createSysLog("B_018", "ҵ��ƾ֤����", "��Ȩ�û�"
						+ loginInfo.getSuserName() + "����ƾ֤״̬Ϊ"
						+ (i > 0 ? "����ɹ�" : "TCBS����ʧ��"));
			}
		} catch (ITFEBizException e) {
			log.error("����״̬�����г��ִ���", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"����״̬�����г����쳣��", e));
		}
		return super.voucherUpdateSuccess(o);
	}

	/**
	 * Direction: �ύ ename: voucherCommit ���÷���: viewers: * messages:
	 */
	public String voucherCommit(Object o) {
		int count = 0;

		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�ύ�ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ���ύ������")) {
			return "";
		}
		if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
				dto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
			return "";
		}
		TsTreasuryPK trecodeDto = new TsTreasuryPK();
		if(dto.getStrecode()==null||"".equals(dto.getStrecode()))
			trecodeDto.setStrecode(checkList.get(0).getStrecode());
		else
			trecodeDto.setStrecode(dto.getStrecode());
		trecodeDto.setSorgcode(loginInfo.getSorgcode());
		TsTreasuryDto tredto = null;
		try {
			tredto = (TsTreasuryDto)commonDataAccessService.find(trecodeDto);
			if(tredto.getStreattrib().equals("2"))
			{
				
				for(int i=0;i<checkList.size();i++)
				{
					if(!DealCodeConstants.VOUCHER_RECIPE.equals(checkList.get(i).getSstatus()) && !DealCodeConstants.VOUCHER_SUCCESS.equals(checkList.get(i).getSstatus()))
					{
						MessageDialog.openMessageDialog(null, "���������״ֻ̬��Ϊ�����ס��ѻص��ſ������ύTBS!");
						return "";
					}
				}
				if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
						.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ�������ύTBS������")) {
					return "";
				}else
				{
					for(int i=0;i<checkList.size();i++)
					{
						//������Ѿ��ص����ļ�¼�������ǣ��Ա�������ٻص�
						if(DealCodeConstants.VOUCHER_SUCCESS.equals(checkList.get(i).getSstatus())){
							checkList.get(i).setShold4(StateConstant.COMMON_NO);
						}
						if(MsgConstant.VOUCHER_NO_5207.equals(checkList.get(i).getSvtcode()))
						{
							checkList.get(i).setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							checkList.get(i).setSext1("");
						}else
						{
							checkList.get(i).setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
							checkList.get(i).setSext1("");
						}
						
					}
					commonDataAccessService.updateDtos(checkList);
				}
				MessageDialog.openMessageDialog(null, "���������ύTBS�ɹ�!");
				return "";
			}
		} catch (ITFEBizException e2) {
		}
			
		
		// ������ֻ��У��ɹ����ұȶԳɹ������ݲſ�ִ���ύ���� -- start
		if ("260000000002".equals(loginInfo.getSorgcode())) {
			for (int i = 0; i < checkList.size(); i++) {
				try {
					String svtcode = getDto(checkList.get(i)).getSvtcode();
					String status = getDto(checkList.get(i)).getSstatus();
					String sdemo = getDto(checkList.get(i)).getSdemo();

					if (MsgConstant.VOUCHER_NO_2301.equals(svtcode)
							|| MsgConstant.VOUCHER_NO_2302.equals(svtcode)
							|| MsgConstant.VOUCHER_NO_5106.equals(svtcode)
							|| MsgConstant.VOUCHER_NO_5108.equals(svtcode)) {
						if (DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
								.equals(status)
								&& !"���ݱȶԳɹ�".equals(sdemo)) {
							String msg = "����������δ�ȶԳɹ����Ƿ����";
							boolean flag = org.eclipse.jface.dialogs.MessageDialog
									.openConfirm(null, "��ʾ", msg);
							if (flag == false) {
								return null;
							} else {
								if (AdminConfirmDialogFacade
										.open("��Ҫ������Ȩ���ܼ���ִ�в���")) {
									DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								}
							}
						}

					}
				} catch (ITFEBizException e) {
					log.error("���ݲ�ѯ�쳣��" + e);
					MessageDialog.openMessageDialog(null, "���ݲ�ѯ�쳣��");
					return "";
				}
			}
		}
		// ������ֻ��У��ɹ����ұȶԳɹ������ݲſ�ִ���ύ���� -- end

		try {
			if (StateConstant.COMMON_YES.equals(voucherLoadService
					.getIscheckpayplan())) {// �ӱ�ͨ��ǰ�ý��ж�ȿ��ƣ����ύTIPS�����ж��У��
				for (int i = 0; i < checkList.size(); i++) {
					String ls_Status = getDto(checkList.get(i)).getSstatus();
					if (!(ls_Status
							.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS))
							&& !(ls_Status
									.equals(DealCodeConstants.VOUCHER_FAIL_TCBS))) {
						MessageDialog.openMessageDialog(null,
								"ֻ��״̬Ϊ\"У��ɹ�\" , \"TCBS����ʧ��\"��ƾ֤��ִ���ύ����!");
						return "";
					}
				}
				MulitTableDto _dto = voucherLoadService
						.amtControlVerify(checkList);
				count = _dto.getTotalCount();
				List<TvVoucherinfoDto> chkerrList = _dto.getErrCheckList();
				String errText = "";
				for (TvVoucherinfoDto tmp : chkerrList) {
					errText = "ƾ֤��ţ�" + tmp.getSvoucherno() + tmp.getSdemo()
							+ "\n";
					if (errText.length() > 100) {
						break;
					}
				}
				String info = "ƾ֤�ύ   " + checkList.size() + " �����ɹ�����Ϊ��"
						+ count + "!";
				if (errText.length() > 0) {
					info += " ʧ��������" + (checkList.size() - count)
							+ "  ������Ϣ���£�\n" + errText;
				}
				MessageDialog.openMessageDialog(null, info);
				refreshTable();

        	}else{//�ύTIPS�ߴ��߼�
	    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||
	    				dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)){	
	        		if(voucherLoadService.getIsitfecommit().equals(StateConstant.COMMON_YES)){
	        			for(int i=0;i<checkList.size();i++){
			    			String ls_Status =  getDto(checkList.get(i)).getSstatus();
			    			if((loginInfo.getPublicparam().contains(",jgtr"+dto.getStrecode()+",")||loginInfo.getPublicparam().contains(",jgall,")||loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"jg"+dto.getStrecode()+","))&&!ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_SUCCESS)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
			    			{
			    				MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ \"��ܳɹ�\"��ƾ֤��ִ�д˲���!");
			            		return "";
			    			}else if(!(ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS) )&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED)){
			        			MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ\"У��ɹ�\" , \"TIPS����ʧ��\"��ƾ֤��ִ�д˲���!");
			            		return "";
			        		}
			        		if(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
	        				{
	        					Calendar calendar = Calendar.getInstance();
	        					calendar.setTime(TimeFacade.getCurrentDate());
	        					calendar.add(Calendar.DATE, -15);
	        					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
	        					String getdate = dateFormat.format(calendar.getTime());
	        					TvSendlogDto sdto = new TvSendlogDto();
	        					sdto.setSpackno(checkList.get(i).getSpackno());
	        					sdto.setStrecode(checkList.get(i).getStrecode());
	        					sdto.setSsendorgcode(checkList.get(i).getSorgcode());
	        					List<TvSendlogDto> slist = commonDataAccessService.findRsByDtoWithWhere(sdto, " and S_DATE>'"+getdate+"'");
	        					if(slist!=null&&slist.size()==1)
	        					{
	        						sdto = slist.get(0);
	        						if(!(sdto.getStitle()==null||"".equals(sdto.getStitle())))
	        						{
	        							MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"����ҵ���ֱ��֧��ҵ�񡢵���ҵ���":"")+
			        					"ʵ���ʽ�ҵ�������˸�ҵ��ֻ��״̬Ϊ\"���˳ɹ�\" , \"TIPS����ʧ��\"��ƾ֤��ִ�д˲���!");
	        							return "";
	        						}
	        					}
	        				}
			        	}
	        		}else{
	        			MessageDialog.openMessageDialog(null, "����֧������ƾ֤�����ύTIPS!");
	        			return "";
	        		}
	        	}else if((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207))
	        			||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)
	        			|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)){
		    		for(int i=0;i<checkList.size();i++){
		    			TvVoucherinfoDto tempdto= getDto(checkList.get(i));
		        		String ls_Status = tempdto.getSstatus();
		        		if(((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))&&loginInfo.getPublicparam().indexOf(",sh,")<0)
			        			||((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)&&loginInfo.getPublicparam().indexOf(",sh,")>=0)
			        					&&!tempdto.getShold4().equals(StateConstant.BIZTYPE_CODE_BATCH))
			        			||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)){
		        			if((loginInfo.getPublicparam().contains(",jgtr"+dto.getStrecode()+",")||loginInfo.getPublicparam().contains(",jgall,")||loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"jg"+dto.getStrecode()+","))&&!ls_Status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
			    			{
			    				MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ \"���˳ɹ�\"��ƾ֤��ִ�д˲���!");
			            		return "";
			    			}else if(!(ls_Status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS) )&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED)){
		        				MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"����ҵ���ֱ��֧��ҵ�񡢵���ҵ���":"")+
				        					"ʵ���ʽ�ҵ�������˸�ҵ��ֻ��״̬Ϊ\"���˳ɹ�\" , \"TIPS����ʧ��\"��ƾ֤��ִ�д˲���!");
				            		return "";
			        		}else
			        		{
			        			if(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
		        				{
		        					Calendar calendar = Calendar.getInstance();
		        					calendar.setTime(TimeFacade.getCurrentDate());
		        					calendar.add(Calendar.DATE, -15);
		        					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
		        					String getdate = dateFormat.format(calendar.getTime());
		        					TvSendlogDto sdto = new TvSendlogDto();
		        					sdto.setSpackno(tempdto.getSpackno());
		        					sdto.setStrecode(tempdto.getStrecode());
		        					sdto.setSsendorgcode(tempdto.getSorgcode());
		        					List<TvSendlogDto> slist = commonDataAccessService.findRsByDtoWithWhere(sdto, " and S_DATE>'"+getdate+"'");
		        					if(slist!=null&&slist.size()==1)
		        					{
		        						sdto = slist.get(0);
		        						if(!(sdto.getStitle()==null||"".equals(sdto.getStitle())))
		        						{
		        							MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"����ҵ���ֱ��֧��ҵ�񡢵���ҵ���":"")+
				        					"ʵ���ʽ�ҵ�������˸�ҵ��ֻ��״̬Ϊ\"���˳ɹ�\" , \"TIPS����ʧ��\"��ƾ֤��ִ�д˲���!");
		        							return "";
		        						}
		        					}
		        				}
			        		}
		        			/**
		        			 * ������ҵ��ʱ���������ƾ֤�ǵ�һ�η��������������к�δ��д������֮ǰ��δ��¼����
		        			 * ��ô�ڵ�һ��У��ʱ����У�鲻������ƾ֤�Ƿ��ǵ�����Ŀ�����û��У�������
		        			 * ��ô���͵�TCBS�ͻ��޷����������ڴ˴��ٴ��ж�
		        			 * @author zhanghuibin
		        			 */
		        			String verifyInfo = voucherLoadService.verifyPayOutMoveFunSubject(tempdto);
		        			if(StringUtils.isNotBlank(verifyInfo)){
		        				MessageDialog.openMessageDialog(null, "ƾ֤���Ϊ["+tempdto.getSvoucherno()+"]��"+verifyInfo);
		        				return "";
		        			}
		        		}		        		
		        	}
		    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)||
		    			dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)){
		    		MessageDialog.openMessageDialog(null, "��������ҵ��֧����ϸƾ֤��ֱ��֧������ƾ֤����Ȩ֧������ƾ֤�����ύTIPS!");
        			return "";
		    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8202))
		    	{
		    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8202))
		    		{
		    			MessageDialog.openMessageDialog(null, "������Ȩ֧��ƾ֤8202�����ύTIPS!");
		    		}else if(!loginInfo.getPublicparam().contains(",5201=pbcpay,"))
		    		{
		    			MessageDialog.openMessageDialog(null, "����ֱ��֧��ƾ֤5201�����ύTIPS!");
		    		}
		    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)){
		    		for(int i=0;i<checkList.size();i++){
		    			String payTypeCode = checkList.get(i).getShold2().trim();//֧����ʽ����
		        		if(StringUtils.isNotBlank(payTypeCode)&&!payTypeCode.equals(MsgConstant.directPay)){
		        			MessageDialog.openMessageDialog(null, "ֻ��֧����ʽΪ��ֱ��֧�������տ������˿�֪ͨƾ֤���ύTIPS!");
		        			return "";
		        		}
		        	}
		    	}else{
		    		for(int i=0;i<checkList.size();i++){
		    			String ls_Status =  getDto(checkList.get(i)).getSstatus();
		    			if((loginInfo.getPublicparam().contains(",jgtr"+dto.getStrecode()+",")||loginInfo.getPublicparam().contains(",jgall,")||loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"jg"+dto.getStrecode()+","))&&!ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_SUCCESS)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS) )&&!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
		    			{
		    				MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ \"��ܳɹ�\"��ƾ֤��ִ�д˲���!");
		            		return "";
		    			}else if((!ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS))&&(!ls_Status.equals(DealCodeConstants.VOUCHER_FAIL_TIPS))&&(!ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))){
		        			MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ\"У��ɹ�\" , \"TIPS����ʧ��\"��ƾ֤��ִ�д˲���!");
		            		return "";
		        		}
		        		if(ls_Status.equals(DealCodeConstants.VOUCHER_SENDED))
        				{
        					Calendar calendar = Calendar.getInstance();
        					calendar.setTime(TimeFacade.getCurrentDate());
        					calendar.add(Calendar.DATE, -15);
        					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
        					String getdate = dateFormat.format(calendar.getTime());
        					TvSendlogDto sdto = new TvSendlogDto();
        					sdto.setSpackno(checkList.get(i).getSpackno());
        					sdto.setStrecode(checkList.get(i).getStrecode());
        					sdto.setSsendorgcode(checkList.get(i).getSorgcode());
        					List<TvSendlogDto> slist = commonDataAccessService.findRsByDtoWithWhere(sdto, " and S_DATE>'"+getdate+"'");
        					if(slist!=null&&slist.size()==1)
        					{
        						sdto = slist.get(0);
        						if(!(sdto.getStitle()==null||"".equals(sdto.getStitle())))
        						{
        							MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"����ҵ���ֱ��֧��ҵ�񡢵���ҵ���":"")+
		        					"ʵ���ʽ�ҵ�������˸�ҵ��ֻ��״̬Ϊ\"���˳ɹ�\" , \"TIPS����ʧ��\"��ƾ֤��ִ�д˲���!");
        							return "";
        						}
        					}
        				}
		        	}
		    	}
	    		if(loginInfo.getPublicparam().contains(",display=true,"))
	    		{
	    			TvVoucherinfoDto voucherDto = null;
	    			for(int i=0;i<checkList.size();i++)
	    			{
	    				voucherDto = getDto(checkList.get(i));
		    			if((!"display".equals(voucherDto.getSext4()))&&(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)))
		    			{
		    				MessageDialog.openMessageDialog(null, "ƾ֤"+voucherDto.getSvoucherno()+"����ƾ֤��ԭչʾ��ſɼ���������");    			
		            		return "";
		    			}
		    			if(loginInfo.getPublicparam().contains(",display=all,"))
		        		{
		        			if((!"display".equals(voucherDto.getSext4()))&&(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
		        			{
		        				MessageDialog.openMessageDialog(null, "ƾ֤"+voucherDto.getSvoucherno()+"����ƾ֤��ԭչʾ��ſɼ���������");    			
		                		return "";
		        			}
		        		}
		    			if(loginInfo.getPublicparam().contains("bigamt=true,")&&(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)))
			    		{
			    			TsQueryAmtDto queryAmtDto =  getAmtMap().get(voucherDto.getSorgcode()+voucherDto.getStrecode()+voucherDto.getSvtcode());
							if (null!=queryAmtDto)
							{
								BigDecimal moneyfilter = queryAmtDto.getFendamt();
								if (moneyfilter.compareTo(voucherDto.getNmoney())<=0&&(!"1".equals(voucherDto.getSext2()))) 
								{
									if (!AdminConfirmDialogFacade.open("����ʽ���Ҫ���������Ȩ��")) {
										DisplayCursor.setCursor(SWT.CURSOR_ARROW);
										return null;
									}
								}
							}else
							{
								BigDecimal moneyfilter = new BigDecimal(100000000.00);
								if (moneyfilter.compareTo(voucherDto.getNmoney())<=0&&(!"1".equals(voucherDto.getSext2()))) 
								{
									if (!AdminConfirmDialogFacade.open("����ʽ���Ҫ���������Ȩ��")) {
										DisplayCursor.setCursor(SWT.CURSOR_ARROW);
										return null;
									}
								}
							}
			    		}
	    			}
	    		}
	    		if(loginInfo.getPublicparam().contains(",5207verifykc=true,"))
        		{
	    			BigDecimal kced = new BigDecimal(0);
	        		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))
		    		{
	        			TvVoucherinfoDto findto = new TvVoucherinfoDto();
	        			findto.setSvtcode(dto.getSvtcode());
	        			findto.setScreatdate(dto.getScreatdate());
	        			findto.setStrecode(dto.getStrecode());
	        			findto.setSorgcode(dto.getSorgcode());
	        			List findlist = commonDataAccessService.findRsByDtoWithWhere(findto, " and S_STATUS in('"+DealCodeConstants.VOUCHER_SENDED+"','"
	        					+DealCodeConstants.VOUCHER_RECIPE+"','"
	        					+DealCodeConstants.VOUCHER_SUCCESS_NO_BACK+"','"
	        					+DealCodeConstants.VOUCHER_STAMP+"','"
	        					+DealCodeConstants.VOUCHER_SUCCESS+"')");
	        			if(findlist!=null&&findlist.size()>0)
	        			{
	        				for(int i=0;i<findlist.size();i++)
	        				{
	        					findto = (TvVoucherinfoDto)findlist.get(i);
	        					kced.add(findto.getNmoney());
	        				}
	        			}
		    		}
	        		TvVoucherinfoDto voucherDto = null;
	        		TrStockdayrptDto kc = new TrStockdayrptDto();
	        		TrStockdayrptDto kcdto = new TrStockdayrptDto();
	        		String getdate = null;
	        		Calendar calendar = null;
	        		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
	        		List datalist = null;
	        		for(int i=1;i<15;i++)
	        		{
	        			calendar = Calendar.getInstance();
	        			calendar.setTime(TimeFacade.getCurrentDate());
	        			calendar.add(Calendar.DATE, -i);
	        			getdate = dateFormat.format(calendar.getTime());
	        			kcdto.setSaccdate(getdate);
	        			kcdto.setSrptdate(getdate);
	        			datalist = commonDataAccessService.findRsByDto(kcdto);
	        			if(datalist!=null&&datalist.size()>0)
	        				break;
	        		}
	        		kc.setSaccdate(getdate);
	        		kc.setSrptdate(getdate);
	        		TvPayoutmsgmainPK pk = new TvPayoutmsgmainPK();
	        		TvPayoutmsgmainDto maindto = new TvPayoutmsgmainDto();
	        		List kcList = null;
	        		for(int i=0;i<checkList.size();i++)
	    			{
	    				voucherDto = checkList.get(i);
	    				pk.setSbizno(voucherDto.getSdealno());
	    				maindto = (TvPayoutmsgmainDto) commonDataAccessService.find(pk);
	    				if(voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||voucherDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))
	    				{
	    					kced.add(voucherDto.getNmoney());
	    					kc.setStrecode(voucherDto.getStrecode());
	    					kc.setSaccno(maindto.getSpayeracct());
	    					kcList = commonDataAccessService.findRsByDto(kc);
	    					if(kcList!=null&&kcList.size()>0)
	    						kcdto = (TrStockdayrptDto)kcList.get(0);
	    					if(kcdto==null)
	    					{
	    						if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
	    								.getCurrentComposite().getShell(), "��ʾ", "�˻�"+kc.getSaccno()+"��汨������,ȷ����У������ύ��?")) {
	    							return "";
	    						}
	    					}else if(kced.compareTo(kcdto.getNmoneytoday()==null?new BigDecimal(0):kcdto.getNmoneytoday())>0)
	    					{
	    						if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
	    								.getCurrentComposite().getShell(), "��ʾ", "�˻�"+kcdto.getSaccno()+"�������ѯTCBS���,ȷ�������ύ��?")) {
	    							return "";
	    						}
	    					}
	    				}
	    			}
        		}
	    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)){
	    			Integer fcount = new Integer(0);
		    		for(int i=0;i<checkList.size();i++){
		    			if(checkList.get(i).getIcount()!=null)
		    			{
		    				fcount+=checkList.get(i).getIcount();
		    			}
		        		if(fcount.intValue()>1000){
		        			MessageDialog.openMessageDialog(null, "ѡ�еļ�¼��ϸ����̫��,Tips�涨��ϸ�������ܳ�1000��!");
		        			return "";
		        		}
		        	}
	    		}
				count=voucherLoadService.voucherCommit(checkList);
				MessageDialog.openMessageDialog(null, "ƾ֤�ύ"+checkList.size()+"�����ɹ�����Ϊ��"+count);
				refreshTable();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return null;
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return null;
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�ύ���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return null;
		}
		return super.voucherCommit(o);
	}

	/**
	 * Direction: ���ͻص� ename: sendReturnVoucher ���÷���: viewers: * messages:
	 */
	public String sendReturnVoucher(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�ص��ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		int count = 0;
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ�лص�������")) {
			return "";
		}
		boolean flag = true;
		// ��ѡ�е��б���в���ʱ�����²�ѯ���ݿ�ȡ����������״̬
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		for (TvVoucherinfoDto vDto : this.checkList) {
			TvVoucherinfoDto newvDto = new TvVoucherinfoDto();
			//�Ϻ�2252ֱ��֧���˿��Ҫ�ص�����
			if(MsgConstant.VOUCHER_NO_2252.equals(vDto.getSvtcode().trim())){
				MessageDialog.openMessageDialog(null, "�տ������˿�֪ͨ2252����Ҫ���ص�������");
				return "";
			}
			
			try {
				newvDto = getDto(vDto);
			} catch (ITFEBizException e) {
				Exception e1 = new Exception("���²�ѯ���ݳ��ִ���", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
			}
			checkList.add(newvDto);
		}
		// У��ɷ��͵���ƾ֤���ƾ֤״̬
		for (TvVoucherinfoDto vDto : checkList) {
//			if(vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)
//			{
//				MessageDialog.openMessageDialog(null, "ƾ֤"	+ vDto.getSvoucherno() + "��ǩ��Ʊ�²��ܻص�ֻ���˻أ�");
//				return "";
//			}
			if (!vDto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)
					&& !vDto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SUCCESS))
				flag = false;
			else if (vDto.getSstatus()
					.equals(DealCodeConstants.VOUCHER_SUCCESS)) {
				String repeatVoucherFlag = getRepeatVoucherFlag(vDto);// ��ȡ���·���ƾ֤������־
				if (!repeatVoucherFlag.equals("0")) {
					vDto.setSreturnerrmsg(vDto.getSreturnerrmsg() + "$"
							+ repeatVoucherFlag);
				} else
					flag = false;
			}
			if(loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"qm=true,")&&(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_RECIPE)||vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)))
			{
				flag = true;
			}
			if (!flag)
				break;
			if (loginInfo.getPublicparam().indexOf(",display=true,") >= 0) {
				if ((!"display".equals(vDto.getSext4()))
						&& (vDto.getSvtcode().equals(
								MsgConstant.VOUCHER_NO_2301)
								|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)|| vDto
								.getSvtcode().equals(
										MsgConstant.VOUCHER_NO_5207))) {
					MessageDialog.openMessageDialog(null, "ƾ֤"
							+ vDto.getSvoucherno() + "����ƾ֤��ԭչʾ��ſɼ���������");
					return "";
				}
			}
			if(loginInfo.getPublicparam().contains(",display=all,"))
    		{
    			if((!"display".equals(vDto.getSext4()))&&(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
    			{
    				MessageDialog.openMessageDialog(null, "ƾ֤"+vDto.getSvoucherno()+"����ƾ֤��ԭչʾ��ſɼ���������");    			
            		return "";
    			}
    		}
		}
		if (!flag) {
			MessageDialog
					.openMessageDialog(
							null,
							"��ѡ��ƾ֤״̬Ϊ\"ǩ�³ɹ�\" ����ƾ֤״̬Ϊ\"�ѻص�\""
									+ "��ϸ��ϢΪ  \"�ص�״̬:�Է�δ����\" ��\"�ص�״̬:�Է�����ʧ��\"��\"�ص�״̬:�Է�ǩ��ʧ��\"��"
									+ "\"�ص�״̬:����δ���� \"��\"�ص�״̬:�Է����˻�\"�ļ�¼��");
			return "";
		}
		try {
			count = voucherLoadService.voucherReturnSuccess(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤���ͻص�   "
					+ checkList.size() + " �����ɹ�����Ϊ��" + count + " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤���ͻص����������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.sendReturnVoucher(o);
	}

	/**
	 * 
	 *���·���ƾ֤������־�� 0 ���ط� 1 �ص�״̬Ϊʧ�ܵ�״̬��ƾ֤ 2 �ص�״̬Ϊ���˻ص�ƾ֤ 3 [��������]�ص�״̬Ϊʧ�ܵ�״̬��ƾ֤ 4
	 * [��������]�ص�״̬Ϊʧ�ܵ�״̬��ƾ֤ 5 [��������]��[��������]�ص�״̬��Ϊʧ�ܵ�״̬��ƾ֤
	 * 
	 * @param dto
	 * @return
	 */
	public String getRepeatVoucherFlag(TvVoucherinfoDto dto) {
		String repeatVoucherFlag = "0";
		String[] errArr = { "�ص�״̬:����δ����","�ص�״̬:�Է�δ����", "�ص�״̬:�Է�����ʧ��", "�ص�״̬:�Է�ǩ��ʧ��",
				 };// �ص�״̬������ʧ�ܵ�״̬
		for (int i = 0; i < errArr.length; i++) {
			if (dto.getSdemo().indexOf(errArr[i]) > -1)
				repeatVoucherFlag = "1";
			if (dto.getSdemo().indexOf("[��������]" + errArr[i]) > -1)
				repeatVoucherFlag = "3";
			if (dto.getSdemo().indexOf("[��������]" + errArr[i]) > -1)
				repeatVoucherFlag = "4";
			if (dto.getSdemo().indexOf("[��������]" + errArr[i]) > -1
					&& dto.getSdemo().indexOf("[��������]" + errArr[i]) > -1)
				repeatVoucherFlag = "5";
			if (!repeatVoucherFlag.equals("0"))
				break;
		}
		if (dto.getSdemo().indexOf("�ص�״̬:�Է����˻�") > -1)
			repeatVoucherFlag = "2";
		return repeatVoucherFlag;
	}

	/**
	 * Direction: ƾ֤�鿴 ename: voucherView ���÷���: viewers: * messages:
	 */
	public String voucherView(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
			return "";
		}
		try {
			ActiveXCompositeVoucherOcx.init(0,true);

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�鿴���������쳣��"+e.toString(), e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		} catch (Error e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�鿴����OCX���ó���ϵͳ��������ϵ������Ա��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherView(o);
	}

	/**
	 * Direction: ƾ֤�˻� ename: voucherBack ���÷���: viewers: * messages:
	 */
	public String voucherBack(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�˻صļ�¼��");
			return "";
		}else
		{
			for(TvVoucherinfoDto temdto:checkList)
			{
				if(!(temdto.getSstampid()==null||"".equals(temdto.getSstampid())))
				{
					MessageDialog.openMessageDialog(null, "ѡ�еļ�¼�д�����ǩ�µ����ݣ��볷��ǩ�º����˻أ�");
					return "";
				}
			}
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ�˻�ѡ�еļ�¼��")) {
			return "";
		}
		if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
				dto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");

			return "";

		}
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		checkList.addAll(this.checkList);
//<<<<<<< .mine
//		try {
//	  		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)
//    				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)){
//				for (int i = 0; i < checkList.size(); i++) {
//					String ls_Status = getDto(checkList.get(i)).getSstatus();
//					if (!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
//							.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_RECEIVE_SUCCESS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TIPS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_VALIDAT_FAIL
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TCBS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_CHECKFAULT
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_CHECKSUCCESS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_CHECK_SUCCESS
//									.equals(ls_Status)
//							&& (loginInfo.getPublicparam().indexOf(",sh,") >= 0 ? !DealCodeConstants.VOUCHER_VALIDAT
//									.equals(ls_Status)
//									: true)) {
//						MessageDialog
//								.openMessageDialog(
//										null,
//										(loginInfo.getPublicparam().indexOf(
//												",sh,") >= 0 ? "ֱ��֧��ҵ��" : "")
//												+ "ʵ���ʽ�ҵ��������˸�ҵ��ֻ��״̬Ϊ  \"ǩ�ճɹ�\" ,\"У��ɹ�\" , "
//												+ (loginInfo.getPublicparam()
//														.indexOf(",sh,") >= 0 ? ",\"У����\" , "
//														: "")
//												+ "\"У��ʧ��\",\"���ʧ��\" ,\"��˳ɹ�\",\"���˳ɹ�\" \"TIPS����ʧ��\" , \"TCBS����ʧ��\" ��ƾ֤��ִ�д˲���!");
//						refreshTable();
//						return "";
//					}
//				}
//			} else {
//				for (int i = 0; i < checkList.size(); i++) {
//					String ls_Status = getDto(checkList.get(i)).getSstatus();
//					if (!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS
//							.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_RECEIVE_SUCCESS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TIPS
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_VALIDAT_FAIL
//									.equals(ls_Status)
//							&& !DealCodeConstants.VOUCHER_FAIL_TCBS
//									.equals(ls_Status)
//							&& (loginInfo.getPublicparam().indexOf(",sh,") >= 0 ? !DealCodeConstants.VOUCHER_VALIDAT
//									.equals(ls_Status)
//									: true)) {
//						MessageDialog
//								.openMessageDialog(
//										null,
//										"��ѡ��ƾ֤״̬Ϊ  \"ǩ�ճɹ�\" ,\"У��ɹ�\" , "
//												+ (loginInfo.getPublicparam()
//														.indexOf(",sh,") >= 0 ? ",\"У����\" , "
//														: "")
//												+ "\"У��ʧ��\" \"TIPS����ʧ��\" , \"TCBS����ʧ��\"  �ļ�¼�� ");
//						refreshTable();
//						return "";
//					}
//				}
//			}
//			boolean flag = false;
//			String status = checkList.get(0).getSstatus();
//			if (status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_CHECKSUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)
//					|| status.equals(DealCodeConstants.VOUCHER_VALIDAT)) {
//				flag = true;
//			}
//			if (flag) {
//				// ��������Ȩ���棬�ֶ������˻�ʧ��ԭ��
//				TsCheckfailreasonDto checkfailDto = new TsCheckfailreasonDto();
//=======
    	try {
    		if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0)
    		{
    			MessageDialog.openMessageDialog(null, "ҵ����Ҫ�����˻�,ֻ��ǩ��Ʊ�·��ͻص���");
            			refreshTable();
                		return "";
    		}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)
    				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)){
        		for(int i=0;i<checkList.size();i++){
            		String ls_Status =  getDto(checkList.get(i)).getSstatus();
            		if(!DealCodeConstants.VOUCHER_ACCEPT.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TIPS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TCBS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_CHECKFAULT.equals(ls_Status)&&
            				!(ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_FAULT))&&
            				!DealCodeConstants.VOUCHER_CHECKSUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_CHECK_SUCCESS.equals(ls_Status)&&
            				(loginInfo.getPublicparam().indexOf(",sh,")>=0?!DealCodeConstants.VOUCHER_VALIDAT.equals(ls_Status):true)&&
            				(!loginInfo.getPublicparam().contains(",backtype=wh,"))){
            			MessageDialog.openMessageDialog(null, (loginInfo.getPublicparam().indexOf(",sh,")>=0?"ֱ��֧��ҵ��":"")+
            					"ʵ���ʽ�ҵ��������˸�ҵ��ֻ��״̬Ϊ  \"ǩ�ճɹ�\" ,\"У��ɹ�\" , " +
            					(loginInfo.getPublicparam().indexOf(",sh,")>=0?",\"У����\" , ":"")+
            					"\"У��ʧ��\",\"���ʧ��\" ,\"��˳ɹ�\",\"���˳ɹ�\" \"TIPS����ʧ��\" , \"TCBS����ʧ��\" ��ƾ֤��ִ�д˲���!");
            			refreshTable();
                		return "";
            		}
            	}
        	}else{
        		for(int i=0;i<checkList.size();i++){
        			String ls_Status =  getDto(checkList.get(i)).getSstatus();
            		if((!(DealCodeConstants.VOUCHER_VALIDAT.equals(ls_Status)&&MsgConstant.VOUCHER_NO_5408.equals(checkList.get(i).getSvtcode())))&&!DealCodeConstants.VOUCHER_ACCEPT.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TIPS.equals(ls_Status)&&!DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(ls_Status)&&
            				!DealCodeConstants.VOUCHER_FAIL_TCBS.equals(ls_Status)&&!(ls_Status.equals(DealCodeConstants.VOUCHER_REGULATORY_FAULT))&&
            				(loginInfo.getPublicparam().indexOf(",sh,")>=0?!DealCodeConstants.VOUCHER_VALIDAT.equals(ls_Status):true)){
            			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ  \"ǩ�ճɹ�\" ,\"У��ɹ�\" , " +
            					(loginInfo.getPublicparam().indexOf(",sh,")>=0?",\"У����\" , ":"")+
            					"\"У��ʧ��\" \"TIPS����ʧ��\" , \"TCBS����ʧ��\"  �ļ�¼�� ");
            			refreshTable();
                		return "";
            		}
            	}
        	}    		
    		boolean flag=false;
			String status=checkList.get(0).getSstatus();
			if(status.equals(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)||status.equals(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS)
					||status.equals(DealCodeConstants.VOUCHER_CHECKSUCCESS)||status.equals(DealCodeConstants.VOUCHER_CHECK_SUCCESS)
					||status.equals(DealCodeConstants.VOUCHER_VALIDAT)){
				if(!loginInfo.getPublicparam().contains(",backtype=wh,")&&!dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207))
					flag=true;
				else if(!loginInfo.getPublicparam().contains(",backtype=wh,"))
					flag=true;
			}
			if(DealCodeConstants.VOUCHER_ACCEPT.equals(status)||DealCodeConstants.VOUCHER_RECEIVE_FAIL.equals(status))
			{
				if(loginInfo.getPublicparam().contains(",backverify,"))
				{
					if (!AdminConfirmDialogFacade.open("�Ѷ�ȡ��ǩ��ʧ�������˻���Ҫ���������Ȩ��")) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						return null;
					}else
					{
						flag=true;
					}
				}
			}
			if(flag){
				//��������Ȩ���棬�ֶ������˻�ʧ��ԭ��
				TsCheckfailreasonDto checkfailDto=new TsCheckfailreasonDto();
				checkfailDto.setSorgcode(dto.getSorgcode());
				List<TsCheckfailreasonDto> checkfail = commonDataAccessService
						.findRsByDto(checkfailDto);
				List<TvVoucherinfoDto> returnBackList = new ArrayList<TvVoucherinfoDto>();
				for (TvVoucherinfoDto vdto : checkList) {
					if (!CheckFaultMsgDialogFacade.open(vdto, checkfail,
							commonDataAccessService)) {
						returnBackList.add(vdto);
					}
				}
				checkList.removeAll(returnBackList);
			}
			String errText = "";
			if (checkList.size() > 0) {
				MulitTableDto _dto = voucherLoadService
						.voucherCheckRetBack(checkList);
				count = _dto.getTotalCount();
				List<TvVoucherinfoDto> chkerrList = _dto.getErrCheckList();
				for (TvVoucherinfoDto tmp : chkerrList) {
					errText = "ƾ֤��ţ�" + tmp.getSvoucherno() + tmp.getSdemo()
							+ "\n";
					if (errText.length() > 100) {
						break;
					}
				}
			} else {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�˻صļ�¼��");
				return "";
			}
			String info = "ƾ֤�˻�   " + checkList.size() + " �����ɹ�����Ϊ��" + count
					+ "!";
			if (errText.length() > 0) {
				info += " ʧ��������" + (checkList.size() - count) + "  ������Ϣ���£�\n"
						+ errText;
			}
			MessageDialog.openMessageDialog(null, info);
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return null;
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return "";
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�˻ز��������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}

		return "";

	}

	/**
	 * Direction: ǩ�� ename: transferCompletedStamp ���÷���: viewers: * messages:
	 */
	public String voucherStamp(Object o) {
		boolean ocxflag = false;
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		String stamp = null;
		TvVoucherinfoDto dto = new TvVoucherinfoDto();
		/*
		 * ƾ֤��ԭչʾҳ��ƾ֤ǩ��
		 */
		if (o instanceof List) {
			List ocxStampList = (List) o;
			String stampname = (String) ocxStampList.get(0);
			dto = (TvVoucherinfoDto) ocxStampList.get(1);
			TsStamppositionDto stampPostionDto = new TsStamppositionDto();
			stampPostionDto.setSorgcode(dto.getSorgcode());
			stampPostionDto.setStrecode(dto.getStrecode());
			stampPostionDto.setSvtcode(dto.getSvtcode());
			stampPostionDto.setSstampname(stampname);
			try {
				stampPostionDto = (TsStamppositionDto) commonDataAccessService
						.findRsByDto(stampPostionDto).get(0);
			} catch (ITFEBizException e) {
				log.error(e);
				Exception e1 = new Exception("ǩ�³����쳣��", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
				return "";
			}
			stamp = stampPostionDto.getSstamptype();
			this.stamp = stampPostionDto.getSstamptype();
			checkList.add(dto);
			ocxflag = true;
		}
		/*
		 * ҵ����ҳ��ƾ֤ǩ��
		 */
		if (!ocxflag) {
			stamp = this.stamp;
			checkList = this.checkList;
			dto = this.dto;
		}
		int count = 0;
		if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}

		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�µļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}

		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�²�����")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if((loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0)&&stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
			{
				if (!(DealCodeConstants.VOUCHER_FAIL_TCBS.equals(infoDto.getSstatus().trim()))) {
					MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ʧ�ܵļ�¼��");
					return "";
				}
			}else
			{
				if(loginInfo.getPublicparam().contains(","+dto.getSvtcode()+"qm=true,"))
				{
					MessageDialog.openMessageDialog(null, dto.getSvtcode()+"ҵ����ǩ��ֻ��ǩ����ֱ�ӷ��ͻص���");
					return "";
				}
				if(infoDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408))
				{
					if(!(DealCodeConstants.VOUCHER_RECIPE.equals(infoDto
							.getSstatus().trim()))) {
							MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ�����׵ļ�¼��");
							return "";
						}
				}else
				{
					if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
						.getSstatus().trim()))) {
						MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ��ļ�¼��");
						return "";
					}
				}
			}
			
		}
		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
				return "";
			}
			// ��ѯ��ǰǩ���û���Ϣ
			TsUsersDto uDto = new TsUsersDto();
			uDto.setSorgcode(loginInfo.getSorgcode());
			uDto.setSusercode(loginInfo.getSuserCode());
			uDto = (TsUsersDto) commonDataAccessService.findRsByDto(uDto)
					.get(0);
			TsStamppositionDto stampDto = new TsStamppositionDto();
			stampDto.setSvtcode(dto.getSvtcode());
			stampDto.setSorgcode(loginInfo.getSorgcode());
			stampDto.setSstamptype(stamp);
			stampDto = (TsStamppositionDto) commonDataAccessService
					.findRsByDto(stampDto).get(0);
			String permission = uDto.getSstamppermission();
			boolean flag = true;
			/*
			 * У���û��Ƿ��е�ǰѡ��ӡ��ǩ��Ȩ��
			 */
			if (permission == null || permission.equals("")) {
				flag = false;
			} else {
				if (permission.indexOf(",") < 0) {
					if (!permission.equals(stamp)) {
						flag = false;
					}
				} else {
					flag = false;
					String[] permissions = permission.split(",");
					for (int i = 0; i < permissions.length; i++) {
						if (permissions[i].equals(stamp)) {
							flag = true;
							break;
						}
					}
				}
			}
			if (flag == false) {
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""
						+ stampDto.getSstampname() + "\"  ǩ��Ȩ�ޣ�");
				return "";
			}
			TsTreasuryDto tDto = new TsTreasuryDto();
			TsStamppositionDto sDto = new TsStamppositionDto();
			Map map = new HashMap();
			String usercode = uDto.getSusercode();
			String stampuser = "";
			String stampid = "";
			for (TvVoucherinfoDto vDto : checkList) {
				map.put(vDto.getStrecode(), "");
				stampid = vDto.getSstampid();
				/*
				 * У��ƾ֤�Ƿ���ǩ��ǰѡ��ӡ�£�ͬһƾ֤�����ظ�ǩ��
				 */
				if (stampid != null && !stampid.equals("")) {
					String[] stampids = stampid.split(",");
					for (int i = 0; i < stampids.length; i++) {
						if (stamp.equals(stampids[i])) {
							MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
									+ vDto.getSvoucherno() + " ��ǩ  \""
									+ stampDto.getSstampname()
									+ "\" ��ͬһƾ֤�����ظ�ǩ�£�");
							return "";
						}
					}
				}
				/*
				 * У���û��Ƿ���ǩ˽�£�ͬһ�û�ֻ��ǩһ��˽��
				 */
				if (!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {
					stampuser = vDto.getSstampuser();
					if (stampuser != null && !stampuser.equals("")) {
						String[] stampusers = stampuser.split(",");
						for (int i = 0; i < stampusers.length; i++) {
							if (usercode.equals(stampusers[i])) {
								TsStamppositionDto tstampDto = new TsStamppositionDto();
								tstampDto.setSorgcode(loginInfo.getSorgcode());
								tstampDto.setSvtcode(dto.getSvtcode());
								String[] stampids = vDto.getSstampid().split(
										",");
								for (int j = 0; j < stampids.length; j++) {
									if (!stampids[i]
											.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
										tstampDto.setSstamptype(stampids[i]);
										break;
									}
								}
								tstampDto = (TsStamppositionDto) commonDataAccessService
										.findRsByDto(tstampDto).get(0);
								MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
										+ vDto.getSvoucherno() + " ��ǰ�û���ǩ  \""
										+ tstampDto.getSstampname()
										+ "\" ��ͬһ�û�ֻ��ǩһ��˽�£���ѡ�������û���");
								return "";
							}
						}

					}
				}
			}
			Iterator it = map.keySet().iterator();
			List lists = new ArrayList();
			List list = null;
			List sinList = null;
			List<TsStamppositionDto> sList = null;
			List vList = null;
			String strecode = "";
			while (it.hasNext()) {
				strecode = it.next() + "";
				vList = new ArrayList();
				tDto = new TsTreasuryDto();
				sDto = new TsStamppositionDto();
				sList = new ArrayList<TsStamppositionDto>();
				list = new ArrayList();
				// ��ѯ��������������
				try {
					tDto.setSorgcode(loginInfo.getSorgcode());
					tDto.setStrecode(strecode);
					tDto = (TsTreasuryDto) commonDataAccessService.findRsByDto(
							tDto).get(0);
				} catch (Exception e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "�����������" + strecode
							+ "�ڹ���������Ϣ�����в����ڣ�");

					return "";
				}
				// ��ѯǩ��λ����Ϣ
				try {
					sDto.setSorgcode(loginInfo.getSorgcode());
					sDto.setStrecode(strecode);
					sDto.setSvtcode(dto.getSvtcode());
					sList = (List<TsStamppositionDto>) commonDataAccessService
							.findRsByDto(sDto);
					sDto.setSstamptype(stamp);
					sDto = (TsStamppositionDto) commonDataAccessService
							.findRsByDto(sDto).get(0);

				} catch (Exception e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "����������룺" + strecode
							+ " " + stampDto.getSstampname() + " ����δά���� ");
					return "";
				}

				// У��ӡ�¶�Ӧ��֤��ID��ӡ��ID�Ƿ��Ѿ�ά��
				Boolean b = checkStampIDAndCertID(tDto, uDto, strecode,
						stampDto);
				if (!b) {
					return "";
				}
				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {
						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
						/*
						 * ƾ֤��ǩ��˳��ǩ�£�ǩ��˳���С������ǩ��
						 */
						if (seq != null && !seq.equals("")) {
							Boolean bo = checkSignStampSeq(vDto, seq, sList,
									strecode, stampDto, stampid);
							if (!bo) {
								return "";
							}
						}
						/**
						 * �ж�ǩ�����ͣ�����ǩ��
						 */
						SignStamp(vDto, seq, sList, tDto, sinList, sDto, uDto);
						vList.add(sinList);
					}
				}
				list.add(uDto);
				list.add(tDto);
				list.add(sDto);
				boolean tpz = false;
				for(TsStamppositionDto tedto:sList)
				{
					if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&MsgConstant.VOUCHERSAMP_ATTACH.equals(tedto.getSstamptype()))
						tpz = true;
				}
				if(tpz)
					list.add((sList.size()-1));
				else
					list.add(sList.size());
				list.add(vList);
				lists.add(list);
			}
			// ƾ֤ǩ��
			count = voucherLoadService.voucherStamp(lists);
			if (ocxflag) {

				return count + "";
			}
			MessageDialog.openMessageDialog(null, "ƾ֤ǩ��   " + checkList.size()
					+ " �����ɹ�����Ϊ��" + count + " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ǩ�²��������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherStamp(o);
	}

	/**
	 * Direction:ǩ�³��� ename: transferCompletedStampCancle ���÷���: viewers: *
	 * messages:
	 */
	public String stampCancle(Object o) {
		int count = 0;
		// ��ѡ�е��б���в���ʱ�����²�ѯ���ݿ�ȡ����������״̬
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		for (TvVoucherinfoDto vDto : this.checkList) {
			TvVoucherinfoDto newvDto = new TvVoucherinfoDto();
			try {
				newvDto = getDto(vDto);
			} catch (ITFEBizException e) {
				Exception e1 = new Exception("���²�ѯ���ݳ��ִ���", e);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), e1);
			}
			checkList.add(newvDto);
		}
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�³����ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog
				.openConfirm(this.editor.getCurrentComposite().getShell(),
						"��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�³���������")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus()))
				&&!((DealCodeConstants.VOUCHER_CHECKSUCCESS.equals(infoDto.getSstatus())||DealCodeConstants.VOUCHER_CHECKFAULT.equals(infoDto.getSstatus())||DealCodeConstants.VOUCHER_CHECK_SUCCESS.equals(infoDto.getSstatus())))
				&&!(infoDto.getSreturnerrmsg()!=null&&infoDto.getSreturnerrmsg().contains("ǩ��λ����ǩ��"))
				&&!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()))
				&&!(DealCodeConstants.VOUCHER_FAIL_TCBS.equals(infoDto.getSstatus()))
				&&!(DealCodeConstants.VOUCHER_FAIL_TIPS.equals(infoDto.getSstatus()))
				&&(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus())||infoDto.getSstampuser()==null||"".equals(infoDto.getSstampuser())||infoDto.getSstampid()==null||"".equals(infoDto.getSstampid()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬ΪTCBS����ʧ�ܡ�����ɹ���ǩ�³ɹ��ļ�¼��");

				return "";
			}
		}

		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");

				return "";

			}
			// ��ѯ��ǰ����ǩ���û���Ϣ
			TsUsersDto uDto = new TsUsersDto();
			uDto.setSorgcode(loginInfo.getSorgcode());
			uDto.setSusercode(loginInfo.getSuserCode());
			uDto = (TsUsersDto) commonDataAccessService.findRsByDto(uDto)
					.get(0);
			TsStamppositionDto stampDto = new TsStamppositionDto();
			stampDto.setSvtcode(dto.getSvtcode());
			stampDto.setSorgcode(loginInfo.getSorgcode());
			stampDto.setSstamptype(stamp);
			stampDto = (TsStamppositionDto) commonDataAccessService
					.findRsByDto(stampDto).get(0);
			String permission = uDto.getSstamppermission();
			boolean flag = true;
			/*
			 * У�鵱ǰ�û��Ƿ��е�ǰѡ��ӡ��ǩ��Ȩ��
			 */
			if (permission == null || permission.equals("")) {
				flag = false;
			} else {
				if (permission.indexOf(",") < 0) {
					if (!permission.equals(stamp)) {
						flag = false;
					}

				} else {
					flag = false;
					String[] permissions = permission.split(",");
					for (int i = 0; i < permissions.length; i++) {
						if (permissions[i].equals(stamp)) {
							flag = true;
							break;
						}
					}

				}
			}
			/*
			 * ����ǰ�û��޵�ǰѡ��ӡ��ǩ��Ȩ�ޣ���ͨ��������Ȩ����ǩ��
			 */
			boolean managerPermission = false;
			if (flag == false) {
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""
						+ stampDto.getSstampname() + "\"  ǩ��Ȩ�ޣ�����ͨ��������Ȩ����ǩ��");
				String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
				if (!AdminConfirmDialogFacade.open("B_018", "ҵ��ƾ֤����", "��Ȩ�û�"
						+ loginInfo.getSuserName() + "����ǩ��", msg)) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				} else {
					managerPermission = true;
				}

			}
			TsTreasuryDto tDto = new TsTreasuryDto();
			TsStamppositionDto sDto = new TsStamppositionDto();
			Map map = new HashMap();
			String usercode = uDto.getSusercode();
			String stampid = "";
			for (TvVoucherinfoDto vDto : checkList) {
				usercode = uDto.getSusercode();
				map.put(vDto.getStrecode(), "");
				stampid = vDto.getSstampid();
				int j = -1;
				/*
				 * У��ƾ֤�Ƿ���ǩ��ǰ�û���������ǩ��
				 */
				if((vDto.getSreturnerrmsg()!=null&&vDto.getSreturnerrmsg().contains("ǩ��λ����ǩ��")))
				{
					stampid = stamp+",";
					vDto.setSstampuser(loginInfo.getSuserCode());
					vDto.setSstampid(stamp+",");
				}else
				{
					if (stampid == null || stampid.equals("")) {
						flag = false;
					} else {
						flag = false;
						String[] stampids = stampid.split(",");
						for (int i = 0; i < stampids.length; i++) {
							if (stamp.equals(stampids[i])) {
								flag = true;
								j = i;
								break;
	
							}
						}
	
					}
				}
				if (flag == false) {
					MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
							+ vDto.getSvoucherno() + " δǩ  \""
							+ stampDto.getSstampname() + "\" ��");
					return "";
				}
				if (managerPermission == false) {
					if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
						usercode = usercode + "!";
					} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
						usercode = usercode + "#";
					} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)) {
						usercode = usercode + "^";
					}else if (stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {
						usercode = usercode + "&";
					}
					/*
					 * 1������ǩ�°���˭ǩ��˭������ԭ�� 2 ������ǰѡ��ӡ�²��ǵ�ǰ�û���ǩ����ͨ��������Ȩ����ǩ��
					 */
					String stampuserboolean = vDto.getSstampuser().split(",")[j];
					if (!stampuserboolean.equals(usercode)) {
						MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
								+ vDto.getSvoucherno() + "   \""
								+ stampDto.getSstampname()
								+ "\" ���ǵ�ǰ�û���ǩ����ͨ��������Ȩ����ǩ��");
						String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
						if (!AdminConfirmDialogFacade
								.open("B_018", "ҵ��ƾ֤����", "��Ȩ�û�"
										+ loginInfo.getSuserName() + "����ǩ��",
										msg)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							return null;
						} else {
							managerPermission = true;
						}
					}
				}
			}

			Iterator it = map.keySet().iterator();
			List lists = new ArrayList();
			List list = null;
			List sinList = null;
			List<TsStamppositionDto> sList = null;
			List vList = null;

			String strecode = "";
			while (it.hasNext()) {
				strecode = it.next() + "";
				vList = new ArrayList<TvVoucherinfoDto>();
				tDto = new TsTreasuryDto();
				sDto = new TsStamppositionDto();
				sList = new ArrayList<TsStamppositionDto>();
				list = new ArrayList();
				// ��ѯ��������������
				try {
					tDto.setSorgcode(loginInfo.getSorgcode());
					tDto.setStrecode(strecode);
					tDto = (TsTreasuryDto) commonDataAccessService.findRsByDto(
							tDto).get(0);
				} catch (Exception e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "�����������" + strecode
							+ "�ڹ���������Ϣ�����в����ڣ�");

					return "";
				}
				// ��ѯǩ��λ����Ϣ
				try {
					sDto.setSorgcode(loginInfo.getSorgcode());
					sDto.setStrecode(strecode);
					sDto.setSvtcode(dto.getSvtcode());
					sList = (List<TsStamppositionDto>) commonDataAccessService
							.findRsByDto(sDto);
					sDto.setSstamptype(stamp);
					sDto = (TsStamppositionDto) commonDataAccessService
							.findRsByDto(sDto).get(0);

				} catch (Exception e) {

					log.error(e);
					MessageDialog.openMessageDialog(null, "����������룺" + strecode
							+ " " + stampDto.getSstampname() + " ����δά���� ");

					return "";
				}
				// У��ӡ�¶�Ӧ��֤��ID��ӡ��ID�Ƿ��Ѿ�ά��
				Boolean b = checkStampIDAndCertID(tDto, uDto, strecode,
						stampDto);
				if (!b) {
					return "";
				}

				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {

						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
						/*
						 * ����ǩ��˳�����γ���ǩ��
						 */
						if (seq != null && !seq.equals("")) {
							List<String> seqList = new ArrayList<String>();
							for (int i = 0; i < sList.size(); i++) {
								TsStamppositionDto tsDto = (TsStamppositionDto) sList
										.get(i);
								if (tsDto.getSstampsequence() != null
										&& !tsDto.getSstampsequence()
												.equals("")) {
									seqList.add(tsDto.getSstampsequence());
								}
							}
							if (seqList != null && seqList.size() > 0) {
								String[] seqs = seqList
										.toArray(new String[seqList.size()]);
								ActiveXCompositeVoucherOcx
										.sortStringArray(seqs);

								String temp = "";
								for (int i = 0; i < seqs.length; i++) {
									if (Integer.parseInt(seqs[i]) > Integer
											.parseInt(seq)) {
										temp = seqs[i];
										break;
									}
								}
								if (!temp.equals("")) {
									List<TsStamppositionDto> tsList = new ArrayList<TsStamppositionDto>();
									TsStamppositionDto tsDto = new TsStamppositionDto();
									tsDto.setSorgcode(loginInfo.getSorgcode());
									tsDto.setStrecode(strecode);
									tsDto.setSvtcode(vDto.getSvtcode());
									tsDto.setSstampsequence(temp);
									tsList = (List<TsStamppositionDto>) commonDataAccessService
											.findRsByDto(tsDto);
									String err = "";

									String[] stampids = stampid.split(",");
									for (int j = 0; j < tsList.size(); j++) {
										for (int i = 0; i < stampids.length; i++) {
											if (stampids[i].equals(tsList
													.get(j).getSstamptype())) {
												err = tsList.get(j)
														.getSstampname()
														+ " " + err;
											}
										}
									}

									if (!err.trim().equals("")) {
										for (TsStamppositionDto tstampDto : tsList) {
											err = tstampDto.getSstampname()
													+ " , " + err;
										}
										err = err.substring(0, err
												.lastIndexOf(","));
										MessageDialog
												.openMessageDialog(
														null,
														"������룺"
																+ vDto
																		.getStrecode()
																+ " ƾ֤����: "
																+ vDto
																		.getSvtcode()
																+ " \""
																+ stampDto
																		.getSstampname()
																+ "\"����ǩ��ǰ���ȳ��� \""
																+ err + "\"ǩ�£�");

										return "";
									}
								}
							}
						}
						int j = -1;
						String[] stampids = stampid.split(",");
						for (int i = 0; i < stampids.length; i++) {
							if (stamp.equals(stampids[i])) {
								j = i;
								break;
							}
						}
						// ��ѯƾ֤ǩ�ºۼ����û���Ϣ�ۼ�
						TsUsersDto userDto = new TsUsersDto();
						userDto.setSorgcode(loginInfo.getSorgcode());
						String user = vDto.getSstampuser().split(",")[j];
						userDto
								.setSusercode(stamp
										.equals(MsgConstant.VOUCHERSAMP_ROTARY) ? user
										.substring(0, (user.length() - 1))
										: (stamp
												.equals(MsgConstant.VOUCHERSAMP_OFFICIAL) ? user
												.substring(0,
														(user.length() - 1))
												: (stamp
														.equals(MsgConstant.VOUCHERSAMP_ATTACH) ? user
														.substring(0, (user
																.length() - 1))
														: (stamp
																.equals(MsgConstant.VOUCHERSAMP_BUSS) ? user
																		.substring(0, (user
																				.length() - 1))
																		: user))));
						userDto = (TsUsersDto) commonDataAccessService
								.findRsByDto(userDto).get(0);
						sinList.add(userDto);
						vList.add(sinList);
					}
				}
				list.add(tDto);
				list.add(sDto);
				list.add(vList);
				lists.add(list);
			}
			// ƾ֤����ǩ��
			count = voucherLoadService.voucherStampCancle(lists);
			MessageDialog.openMessageDialog(null, "ƾ֤����ǩ��   "
					+ checkList.size() + " �����ɹ�����Ϊ��" + count + " ��");
			if(checkList.size()!=count&&loginInfo.getPublicparam().contains(",canclestamp=verify,"))
			{
				if(!loginInfo.getPublicparam().contains(",canclestamp=notverify,"))
				{
					if (!AdminConfirmDialogFacade.open("ѡ�е������ͳɹ�������һ�£��Ƿ�������ǿ�Ƴ���ǩ�ºۼ���")) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						return null;
					}
				}
				List upList = new ArrayList();
				for (TvVoucherinfoDto vDto : checkList) {
					vDto = getDto(vDto);
					int j = -1;
					String[] stampids = vDto.getSstampid().split(",");
					for (int i = 0; i < stampids.length; i++) {
						if (stamp.equals(stampids[i])) {
							j = i;
							break;
						}
					}
					if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)){
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					}
					// ��ѯƾ֤ǩ�ºۼ����û���Ϣ�ۼ�
					TsUsersDto userDto = new TsUsersDto();
					userDto.setSorgcode(loginInfo.getSorgcode());
					String user = vDto.getSstampuser().split(",")[j];
					userDto
							.setSusercode(stamp
									.equals(MsgConstant.VOUCHERSAMP_ROTARY) ? user
									.substring(0, (user.length() - 1))
									: (stamp
											.equals(MsgConstant.VOUCHERSAMP_OFFICIAL) ? user
											.substring(0,
													(user.length() - 1))
											: (stamp
													.equals(MsgConstant.VOUCHERSAMP_ATTACH) ? user
													.substring(0, (user
															.length() - 1))
													: (stamp
															.equals(MsgConstant.VOUCHERSAMP_BUSS) ? user
																	.substring(0, (user
																			.length() - 1))
																	: user))));
					userDto = (TsUsersDto) commonDataAccessService
							.findRsByDto(userDto).get(0);
					// ����ǩ�³ɹ�����ӡ�ºۼ�
					vDto.setSstampid(vDto.getSstampid().substring(
							0,
							vDto.getSstampid().indexOf(
									sDto.getSstamptype() + ","))
							+ vDto.getSstampid().substring(
									vDto.getSstampid().indexOf(
											sDto.getSstamptype() + ",")
											+ (sDto.getSstamptype() + ",")
													.length(),
									vDto.getSstampid().length()));
					// ����ǩ�³ɹ�����ǩ���û��ۼ�
					if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "#,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "#,")
												+ (userDto.getSusercode() + "#,")
														.length(),
										vDto.getSstampuser().length()));
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ROTARY))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "!,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "!,")
												+ (userDto.getSusercode() + "!,")
														.length(),
										vDto.getSstampuser().length()));
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ATTACH))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "^,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "^,")
												+ (userDto.getSusercode() + "^,")
														.length(),
										vDto.getSstampuser().length()));
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_BUSS))
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + "&,"))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + "&,")
												+ (userDto.getSusercode() + "&,")
														.length(),
										vDto.getSstampuser().length()));
					else
						vDto.setSstampuser(vDto.getSstampuser().substring(
								0,
								vDto.getSstampuser().indexOf(
										userDto.getSusercode() + ","))
								+ vDto.getSstampuser().substring(
										vDto.getSstampuser().indexOf(
												userDto.getSusercode() + ",")
												+ (userDto.getSusercode() + ",")
														.length(),
										vDto.getSstampuser().length()));
					upList.add(vDto);
				}
				commonDataAccessService.updateDtos(upList);
			}
			refreshTable();
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤����ǩ�²��������쳣��", e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e1);
		}
		return super.stampCancle(o);
	}

	/**
	 * Direction: �ص�״̬��ѯ ename: queryStatusReturnVoucher ���÷���: viewers: *
	 * messages:
	 */
	public String queryStatusReturnVoucher(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ص�״̬�ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_FAIL.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS_BACK.equals(infoDto.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ�ص��ɹ�������Ʊ�ɹ��ļ�¼��");
				return "";
			}
		}
		try {
			count = voucherLoadService.queryStatusReturnVoucher(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤�鿴�ص�״̬" + checkList.size()
					+ " �����ɹ�����Ϊ��" + count + " , ��鿴��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�鿴�ص�״̬���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.queryStatusReturnVoucher(o);
	}

	/**
	 * Direction: ��ѯƾ֤��ӡ���� ename: queryVoucherPrintCount ���÷���: viewers: *
	 * messages:
	 */
	public String queryVoucherPrintCount(TvVoucherinfoDto vDto) {
		String err = null;
		try {
			err = voucherLoadService.queryVoucherPrintCount(vDto);

		} catch (ITFEBizException e) {
			log.error(e);
			return "��ѯ�쳣";
		}
		return err;
	}

	/**
	 * Direction: ��ѯƾ֤���� ename: queryVoucherPrintCount ���÷���: viewers: *
	 * messages:
	 */
	public int queryVoucherJOintCount(TvVoucherinfoDto vDto) {
		TsVouchercommitautoDto tDto = new TsVouchercommitautoDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			List<TsVouchercommitautoDto> list = (List) commonDataAccessService
					.findRsByDto(tDto);
			if (list == null || list.size() == 0)
				return -1;
			tDto = list.get(0);
			if (tDto.getSjointcount() == null) {
				return -1;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		} catch (Exception e) {
			log.error(e);
			return -1;
		}
		return tDto.getSjointcount();
	}

	public List<Mapper> getStampEnums(TvVoucherinfoDto vDto) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		List<TsStamppositionDto> enumList = new ArrayList<TsStamppositionDto>();
		TsStamppositionDto tDto = new TsStamppositionDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			enumList = commonDataAccessService.findRsByDto(tDto);
			if (enumList != null && enumList.size() > 0) {
				for (TsStamppositionDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSstamptype(), emuDto
							.getSstampname());
					maplist.add(map);
				}
			}

		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception("��ȡƾ֤ǩ���б�����쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}

		return maplist;
	}

	public void refreshTable() {
		init();
		checkList.clear();
		refreshStampType();
	}

	public void updateMainDto(TvVoucherinfoDto vDto) throws ITFEBizException {
		String status = "";
		String xpaydate = "";
		if (vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
			status = DealCodeConstants.DEALCODE_ITFE_SUCCESS;
			xpaydate = StringUtils.isBlank(vDto.getSext5()) ? TimeFacade
					.getCurrentStringTime() : vDto.getSext5();
		} else {
			status = DealCodeConstants.DEALCODE_ITFE_FAIL;
		}
		if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)) {
			TvPayoutmsgmainDto mainDto = new TvPayoutmsgmainDto();
			mainDto.setSbizno(vDto.getSdealno());
			try {
				mainDto = (TvPayoutmsgmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			mainDto.setSstatus(status);
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//���Ŵ���ʧ�ܲ�������������������
			{
				mainDto.setSxpayamt(new BigDecimal(0));
				mainDto.setSxpaydate("");
			}else
			{
				mainDto.setSxpayamt(vDto.getNmoney());
				mainDto.setSxpaydate(xpaydate);
			}
			//�Բ����嵥�Ĵ���2.0ʱ����ʵ���ʽ�ʽ
			//���ݹ�������ж�
			TsTreasuryDto d = new TsTreasuryDto();
			IItfeCacheService cacheServer = (IItfeCacheService) ServiceFactory.getService(IItfeCacheService.class);
	        d.setStrecode(vDto.getStrecode());
	        List  list = cacheServer.cacheGetValueByDto(d);
			if (null!=list && list.size()>0) {
				TsTreasuryDto resdto = (TsTreasuryDto) list.get(0);
				if (StateConstant.COMMON_YES.equals(resdto.getSpayunitname())){
					mainDto.setSxagentbusinessno("0");
					String saddword;
					if (vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
						saddword = DealCodeConstants.PROCESS_SUCCESS;
						status = DealCodeConstants.DEALCODE_ITFE_SUCCESS;
						xpaydate = StringUtils.isBlank(vDto.getSext5()) ? TimeFacade
								.getCurrentStringTime() : vDto.getSext5();
					} else {
						saddword = DealCodeConstants.PROCESS_FAIL;
						status = DealCodeConstants.DEALCODE_ITFE_FAIL;
					}
					mainDto.setSaddword(saddword);
					//������ϸ
					TvPayoutmsgsubDto sub = new TvPayoutmsgsubDto();
					sub.setSbizno(vDto.getSdealno());
					List <TvPayoutmsgsubDto> sublist = commonDataAccessService.findRsByDto(sub);
					List updateDtos = new ArrayList();
					for (TvPayoutmsgsubDto t : sublist ) {
						t.setSxaddword(saddword);
						t.setSxagentbusinessno(t.getStrasno());
						t.setSxpaydate(xpaydate);
						t.setSstatus(status);
						if (vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)) {
							  t.setSxpayamt(t.getNmoney());
						}else{
							  t.setSxpayamt(BigDecimal.ZERO);
						}
						updateDtos.add(t);
					}
					if (updateDtos.size()>0) {
						commonDataAccessService.updateDtos(updateDtos);
					}
				
				}
			}
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)) {
			TvDwbkDto mainDto = new TvDwbkDto();
			mainDto.setSbizno(vDto.getSdealno());
			try {
				mainDto = (TvDwbkDto) commonDataAccessService.findRsByDto(
						mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			mainDto.setSstatus(status);
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//���Ŵ���ʧ�ܲ�������������������
			{
				mainDto.setXpayamt(new BigDecimal(0));
			}else
			{
				mainDto.setXpayamt(vDto.getNmoney());
			}
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
			TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
			mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			TvGrantpaymsgsubDto subDto = new TvGrantpaymsgsubDto();
			subDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			List<TvGrantpaymsgmainDto> mainDtoList = commonDataAccessService
					.findRsByDto(mainDto);
			if (mainDtoList == null || mainDtoList.size() == 0)
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�");
			List<TvGrantpaymsgsubDto> subDtoList = commonDataAccessService
					.findRsByDto(subDto);
			if (subDtoList == null || subDtoList.size() == 0)
				throw new ITFEBizException("��ѯ�ӱ���Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ���ӱ��¼�����ڣ�");
			for (TvGrantpaymsgmainDto maindto : mainDtoList) {
				if(!(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0))//���Ŵ���ʧ�ܲ�������������������
				{
					maindto.setSxacctdate(xpaydate);
				}
				maindto.setSstatus(status);
				commonDataAccessService.updateData(maindto);
			}
			for (TvGrantpaymsgsubDto subdto : subDtoList) {
				subdto.setSstatus(status);
				commonDataAccessService.updateData(subdto);
			}
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
			TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
			mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			try {
				mainDto = (TvDirectpaymsgmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			if(!(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0))//���Ŵ���ʧ�ܲ�������������������
			{
				mainDto.setSxacctdate(xpaydate);
			}
			mainDto.setSstatus(status);
			commonDataAccessService.updateData(mainDto);
		}else if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5671)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)){
			TvNontaxmainDto maindto = new TvNontaxmainDto();
			maindto.setSdealno(vDto.getSdealno());
			try {
				maindto = (TvNontaxmainDto) commonDataAccessService.findRsByDto(maindto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			maindto.setSstatus(status);
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//���Ŵ���ʧ�ܲ�������������������
			{
				maindto.setNxmoney(new BigDecimal(0));
				maindto.setSxtradate("");
			}else
			{
				maindto.setNxmoney(vDto.getNmoney());
				maindto.setSxtradate(xpaydate);
			}
			commonDataAccessService.updateData(maindto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)) {
			TvPayreckBankDto mainDto = new TvPayreckBankDto();
			mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
			try {
				mainDto = (TvPayreckBankDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			/**
			 * TS_CONVERTBANKTYPE ��XPaySndBnkNo֧���������кš� ��д3144����д���������к�
			 * �ֹ�����û�ж�Ӧ��ֵ ��XAddWord���ԡ� ��д ԭ������ˮ��
			 */

			if (mainDto.getSbookorgcode().indexOf("09") >= 0
					&& mainDto.getSpaymode().equals("1")) {
				mainDto.setSxpaysndbnkno(mainDto.getSagentbnkcode());
				mainDto.setSaddword("011�޷���ȡ");
			}
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//���Ŵ���ʧ�ܲ�������������������
			{
				mainDto.setSxpayamt(new BigDecimal(0));
			}else
			{
				mainDto.setSxcleardate(CommonUtil.strToDate(xpaydate));
				mainDto.setSxpayamt(vDto.getNmoney());
			}
			mainDto.setSresult(status);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)) {
			// if (loginInfo.getPublicparam().indexOf("sh,") > 0
			// && "91".equals(vDto.getShold2()) && null != checkList
			// && checkList.size() > 0) {
			// List<TvVoucherinfoDto> voucher2252 = new
			// ArrayList<TvVoucherinfoDto>();
			// TvVoucherinfoDto tmp = ((TvVoucherinfoDto) vDto.clone());
			// tmp.setSvtcode(MsgConstant.VOUCHER_NO_2252);
			// voucher2252.add(tmp);
			// voucherLoadService.voucherCommit(voucher2252);
			// }
			TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TvPayreckBankBackDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			/**
			 * ����������09��ͷ��Ĭ��Ϊ�Ϻ� ��XPaySndBnkNo֧���������кš� ��д3144����д���������к�
			 * ��XAddWord���ԡ� ��д ԭ������ˮ��
			 */
			if (mainDto.getSbookorgcode().indexOf("09") >= 0
					&& mainDto.getSpaymode().equals("1")) {
				mainDto.setSxpaysndbnkno(mainDto.getSpaysndbnkno());
				mainDto.setSaddword("011�޷���ȡ");
			}
			if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//���Ŵ���ʧ�ܲ�������������������
			{
				mainDto.setSxpayamt(new BigDecimal(0));
			}else
			{
				mainDto.setSxcleardate(CommonUtil.strToDate(xpaydate));
				mainDto.setSxpayamt(vDto.getNmoney().negate());
			}
			mainDto.setSstatus(status);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)) {
			TfGrantpayAdjustmainDto mainDto = new TfGrantpayAdjustmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfGrantpayAdjustmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			mainDto.setSxaccdate(xpaydate);
			mainDto.setSstatus(status);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)) {
			TfDirectpayAdjustmainDto mainDto = new TfDirectpayAdjustmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfDirectpayAdjustmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			mainDto.setSxpaydate(xpaydate);
			mainDto.setSstatus(status);
			mainDto.setNxpayamt(mainDto.getNxpayamt());
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
			TfDirectpaymsgmainDto mainDto = new TfDirectpaymsgmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfDirectpaymsgmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			mainDto.setSxpaydate(xpaydate);
			mainDto.setSstatus(status);
			mainDto.setNxpayamt(mainDto.getNpayamt());
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {
			TfPaymentDetailsmainDto mainDto = new TfPaymentDetailsmainDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (TfPaymentDetailsmainDto) commonDataAccessService
						.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			mainDto.setSxpaydate(xpaydate);
			mainDto.setSstatus(status);
			mainDto.setNxsumamt(mainDto.getNsumamt());
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5407)) {
			TvInCorrhandbookDto mainDto = new TvInCorrhandbookDto();
			mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			try {
				mainDto = (commonDataAccessService.findRsByDto(mainDto)==null||commonDataAccessService.findRsByDto(mainDto).size()<=0)?null:(TvInCorrhandbookDto) commonDataAccessService.findRsByDto(mainDto).get(0);
			} catch (java.lang.IndexOutOfBoundsException e) {
				log.error(e);
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
						+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
			}
			if(mainDto==null)
				return;
			mainDto.setSstatus(status);
			mainDto.setDxacctdate(xpaydate);
			commonDataAccessService.updateData(mainDto);
		} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)) {
			if (loginInfo.getPublicparam().indexOf("sh,") > 0
					&& "91".equals(vDto.getShold2()) && null != checkList
					&& checkList.size() > 0) {
				//�������ʧ�ܲ����ú�̨���Ʊ�
				if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(vDto.getSstatus().trim()) || DealCodeConstants.VOUCHER_FAIL_TCBS.equals(vDto.getSstatus().trim())) {
					TfPaybankRefundmainDto mainDto = new TfPaybankRefundmainDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					try {
						mainDto = (TfPaybankRefundmainDto) commonDataAccessService
								.findRsByDto(mainDto).get(0);
					} catch (java.lang.IndexOutOfBoundsException e) {
						log.error(e);
						throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
								+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
					}
					mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
					mainDto.setNbackmoney(BigDecimal.ZERO);
					commonDataAccessService.updateData(mainDto);
				} else {
					List<TvVoucherinfoDto> voucher2252 = new ArrayList<TvVoucherinfoDto>();
					TvVoucherinfoDto tmp = ((TvVoucherinfoDto) vDto.clone());
					tmp.setSvtcode(MsgConstant.VOUCHER_NO_2252);
					voucher2252.add(tmp);
					voucherLoadService.voucherCommit(voucher2252);
				}
			} else {
				TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
				mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
				try {
					mainDto = (TvPayreckBankBackDto) commonDataAccessService
							.findRsByDto(mainDto).get(0);
				} catch (java.lang.IndexOutOfBoundsException e) {
					log.error(e);
					throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��"
							+ vDto.getSvoucherno() + "��Ӧ�������¼�����ڣ�", e);
				}
				/**
				 * ����������09��ͷ��Ĭ��Ϊ�Ϻ� ��XPaySndBnkNo֧���������кš� ��д3144����д���������к�
				 * ��XAddWord���ԡ� ��д ԭ������ˮ��
				 */
				if (mainDto.getSbookorgcode().indexOf("09") >= 0
						&& mainDto.getSpaymode().equals("1")) {
					mainDto.setSxpaysndbnkno(mainDto.getSpaysndbnkno());
					mainDto.setSaddword("011�޷���ȡ");
				}
				if(loginInfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)//���Ŵ���ʧ�ܲ�������������������
				{
					mainDto.setSxpayamt(new BigDecimal(0));
				}else
				{
					mainDto.setSxcleardate(CommonUtil.strToDate(xpaydate));
					mainDto.setSxpayamt(vDto.getNmoney().negate());
				}
				mainDto.setSstatus(status);
				commonDataAccessService.updateData(mainDto);
			}
		}
	}

	public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException {
		return voucherLoadService.voucherStampXml(vDto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		pageRequest.setPageSize(50);
		PageResponse page = null;
		try {
			if(searchtype!=null&&!"".equals(searchtype))
			{
				if(searchtype!=null&&MsgConstant.VOUCHER_NO_2301.equals(searchtype))
				{
					TvPayreckBigdataListDto bigdatalistdto = new TvPayreckBigdataListDto();
					bigdatalistdto.setIvousrlno(bigdatadto.getIvousrlno());
					page = commonDataAccessService.findRsByDtoPaging(bigdatalistdto, pageRequest,null,null);
				}else
				{
					TvPayreckBigdataBackListDto bigdatabacklistdto = new TvPayreckBigdataBackListDto();
					bigdatabacklistdto.setIvousrlno(bigdatabackdto.getIvousrlno());
					page = commonDataAccessService.findRsByDtoPaging(bigdatabacklistdto, pageRequest,null,null);
				}
			}else
			{
				if(loginInfo.getPublicparam().contains((",vouchersearchhold=5,")))
				{
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(TimeFacade.getCurrentDate());
					calendar.add(Calendar.DATE, -5);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
					String getdate = dateFormat.format(calendar.getTime());
					page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
							"1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS_BACK+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_FAIL+"')", "S_RECVTIME DESC");
				}else if(loginInfo.getPublicparam().contains((",vouchersearchhold=15,")))
				{
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(TimeFacade.getCurrentDate());
					calendar.add(Calendar.DATE, -15);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
					String getdate = dateFormat.format(calendar.getTime());
					page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
							"1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS_BACK+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_SUCCESS+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_FAIL+"')", "S_RECVTIME DESC");
				}else
				{
					page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
							"1=1 AND S_PAYBANKCODE IS NOT NULL ", "S_RECVTIME DESC");
				}
			}
			return page;

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("��ѯ�����쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.retrieve(pageRequest);
	}

	public TvVoucherinfoDto getDto(TvVoucherinfoDto dto)
			throws ITFEBizException {
		TvVoucherinfoPK pk = new TvVoucherinfoPK();
		pk.setSdealno(dto.getSdealno());
		return (TvVoucherinfoDto) commonDataAccessService.find(pk);
	}

	/**
	 * ˢ��ǩ������
	 * 
	 */
	public void refreshStampType() {
		TsStamppositionDto tsDto = new TsStamppositionDto();
		tsDto.setSorgcode(dto.getSorgcode());
		tsDto.setSvtcode(dto.getSvtcode());
		tsDto.setStrecode(dto.getStrecode());
		Set set = new HashSet();
		TsStamppositionDto sDto = new TsStamppositionDto();
		List<TsStamppositionDto> tList = null;
		stampList = new ArrayList();
		stamp = null;
		List<TsStamppositionDto> tsList = new ArrayList<TsStamppositionDto>();
		try {
			tList = commonDataAccessService.findRsByDto(tsDto);
			if (tList.size() > 0) {
				for (TsStamppositionDto sdto : tList) {
					set.add(sdto.getSstamptype());
				}
				for (String stamptype : (Set<String>) set) {
					sDto = new TsStamppositionDto();
					sDto.setSorgcode(dto.getSorgcode());
					sDto.setSvtcode(dto.getSvtcode());
					sDto.setSstamptype(stamptype);
					sDto = (TsStamppositionDto) commonDataAccessService
							.findRsByDto(sDto).get(0);
					tsList.add(sDto);
				}
				stampList.addAll(tsList);
				if (stampList.size() == 1) {
					stamp = ((TsStamppositionDto) stampList.get(0))
							.getSstamptype();
				}
			}
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤ˢ�²��������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List checkList) {
		this.checkList = checkList;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public List getStampList() {
		return stampList;
	}

	public void setStampList(List stampList) {
		this.stampList = stampList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		// ���л���������˿�������ֱ��֧��������Ȩ
		if (voucherType.equals("230101") || voucherType.equals("230201")) {
			this.dto.setSext1("0");// ֧����ʽΪֱ��֧��
			this.dto.setSvtcode(voucherType.substring(0, 4));
		} else if (voucherType.equals("230102") || voucherType.equals("230202")) {
			this.dto.setSvtcode(voucherType.substring(0, 4));
			this.dto.setSext1("1");// ֧����ʽΪ��Ȩ֧��
		} else {
			this.dto.setSvtcode(voucherType);
		}
		refreshStampType();
		List contreaNamesVisibleFalse = new ArrayList();//�ر���ʾƾ֤һ����
		List contreaNamesVisibbleTrue = new ArrayList();//��ʾƾ֤һ����
		//��̬��ʾ
		if (((MsgConstant.VOUCHER_NO_5207.equals(voucherType)|| MsgConstant.VOUCHER_NO_5267.equals(voucherType)
				||MsgConstant.VOUCHER_NO_5209.equals(voucherType)||MsgConstant.VOUCHER_NO_5407.equals(voucherType)) 
				&&loginInfo.getPublicparam().indexOf(",sh,")<0)) {
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ���������");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����˰����");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ�����Ʊ��λ");
			List<ContainerMetaData> containerMetaData = MVCUtils.setContentAreasToVisible(editor, contreaNamesVisibbleTrue);
			for (ContainerMetaData metadata : containerMetaData) {
				if ("ƾ֤��ѯһ�����Ʊ��λ".equals(metadata.name))
				{
					List controls = metadata.controlMetadatas;
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if(coletadata.title.equals("�տ����˺�")||coletadata.title.equals("�տ�������")||coletadata.title.equals("�տ���������")||coletadata.title.equals("�������˺�")||coletadata.title.equals("����������"))
							{
								if(loginInfo.getPublicparam().contains(",voucherdisplaymain=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
							if(coletadata.title.equals("��ӡ����"))
							{
								if(loginInfo.getPublicparam().contains(",printcount=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
						}
					}
				}
			}
		} else if (((MsgConstant.VOUCHER_NO_2301.equals(voucherType)
				|| "230101".equals(voucherType) || "230102".equals(voucherType)) && loginInfo
				.getPublicparam().indexOf(",sh,") < 0)
				|| MsgConstant.VOUCHER_NO_2302.equals(voucherType)
				|| "230201".equals(voucherType)
				|| "230202".equals(voucherType)
				|| (MsgConstant.VOUCHER_NO_5106.equals(voucherType) && loginInfo
						.getPublicparam().indexOf(",sh,") < 0)
				|| (MsgConstant.VOUCHER_NO_5108.equals(voucherType) && loginInfo
						.getPublicparam().indexOf(",sh,") < 0)
				|| MsgConstant.VOUCHER_NO_2252.equals(voucherType)) {
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����Ʊ��λ");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����˰����");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ���������");
			List<ContainerMetaData> containerMetaData = MVCUtils.setContentAreasToVisible(editor, contreaNamesVisibbleTrue);
			for (ContainerMetaData metadata : containerMetaData) {
				if ("ƾ֤��ѯһ���������".equals(metadata.name))
				{
					List controls = metadata.controlMetadatas;
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if(coletadata.title.equals("��ӡ����"))
							{
								if(loginInfo.getPublicparam().contains(",printcount=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
							if(coletadata.title.equals("Ԥ������"))
							{
								if(loginInfo.getPublicparam().contains(",quotabudgettype,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
							}
							if(coletadata.title.equals("ƥ��ƾ֤���"))
							{
								if((MsgConstant.VOUCHER_NO_5106.equals(voucherType)||MsgConstant.VOUCHER_NO_5108.equals(voucherType)))
								{
									TsOrganPK ipk = new TsOrganPK();
									ipk.setSorgcode(loginInfo.getSorgcode());
									TsOrganDto dto=null;
									try {
										dto = (TsOrganDto)commonDataAccessService.find(ipk);
									} catch (ITFEBizException e) {
									}
									if(dto!=null&&dto.getSofprovorgcode()!=null&&"hold1".contains(dto.getSofprovorgcode()))
										coletadata.visible = true;
									else
										coletadata.visible = false;
								}else
									coletadata.visible = false;
							}
						}
					}
				}
			}
		} else if (((MsgConstant.VOUCHER_NO_2301.equals(voucherType)
				|| "230101".equals(voucherType) || "230102".equals(voucherType)) && loginInfo
				.getPublicparam().indexOf(",sh,") >= 0)
				|| MsgConstant.VOUCHER_NO_5253.equals(voucherType)
				|| MsgConstant.VOUCHER_NO_5351.equals(voucherType)) {
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����Ʊ��λ");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ���������");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����˰����");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ������ƾ֤����");
		} else if ((MsgConstant.VOUCHER_NO_5106.equals(voucherType) && loginInfo
				.getPublicparam().indexOf(",sh,") >= 0)
				|| (MsgConstant.VOUCHER_NO_5108.equals(voucherType) && loginInfo
						.getPublicparam().indexOf(",sh,") >= 0)) {
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����Ʊ��λ");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ���������");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����˰����");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
		} else if (MsgConstant.VOUCHER_NO_8207.equals(voucherType)) {
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����Ʊ��λ");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ���������");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����˰����");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
		} else if ((MsgConstant.VOUCHER_NO_5207.equals(voucherType) && loginInfo
				.getPublicparam().indexOf(",sh,") >= 0)
				|| MsgConstant.VOUCHER_NO_5201.equals(voucherType)) {
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����Ʊ��λ");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ���������");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����˰����");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
		}else if (MsgConstant.VOUCHER_NO_5671.equals(voucherType)||MsgConstant.VOUCHER_NO_5408.equals(voucherType))
		{
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����Ʊ��λ");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ���������");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ�����˰����");
		}else
		{
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ���������");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤���͹��ұ��ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ������ƾ֤����ҵ�����ͱ���");
			contreaNamesVisibleFalse.add("ƾ֤��ѯһ�����˰����");
			contreaNamesVisibbleTrue.add("ƾ֤��ѯһ�����Ʊ��λ");
			List<ContainerMetaData> containerMetaData = MVCUtils.setContentAreasToVisible(editor, contreaNamesVisibbleTrue);
			for (ContainerMetaData metadata : containerMetaData) {
				if ("ƾ֤��ѯһ�����Ʊ��λ".equals(metadata.name))
				{
					List controls = metadata.controlMetadatas;
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if(coletadata.title.equals("��ӡ����"))
							{
								if(loginInfo.getPublicparam().contains(",printcount=true,"))
									coletadata.visible = true;
								else
									coletadata.visible = false;
								break;
							}
						}
					}
					break;
				}
			}
		}
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibleFalse, false);
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibbleTrue, true);
		pagingcontext.setPage(new PageResponse());
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();

	}

	public boolean voucherCompare(TvVoucherinfoDto dto, List checkList) {
		if (loginInfo.getPublicparam().indexOf(",sh,") < 0)
			return true;
		if (!(((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201) || dto
				.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)) && dto
				.getShold4().equals(StateConstant.BIZTYPE_CODE_BATCH)) || dto
				.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)))
			return true;
		TvVoucherinfoDto tempdto = new TvVoucherinfoDto();
		tempdto.setStrecode(dto.getStrecode());
		tempdto.setSvtcode(dto.getScheckvouchertype());
		tempdto.setShold4(StateConstant.BIZTYPE_CODE_BATCH);
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
			tempdto.setSvoucherno(dto.getShold3());
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)) {
			tempdto.setShold3(dto.getSvoucherno());
		}
		List list = null;
		try {
			list = commonDataAccessService.findRsByDto(tempdto,
					" ORDER BY S_STATUS DESC");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), new Exception(
							"��ѯƾ֤��Ϣ�쳣!", e));
			return false;
		}
		if (list == null || list.size() == 0) {
			MessageDialog.openMessageDialog(null, "��Ӧ��"
					+ dto.getScheckvouchertype() + "ƾ֤�����ڡ�");
			return false;
		}
		tempdto = (TvVoucherinfoDto) list.get(0);
		if (tempdto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {
			if (tempdto.getSstatus().equals(
					DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)
					|| tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_FAIL_TCBS)
					|| (tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SUCCESS_NO_BACK) && StringUtils
							.isBlank(tempdto.getSstampid()))) {
				checkList.add(tempdto);
				return true;
			}
		} else if (tempdto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
			if (tempdto.getSstatus().equals(
					DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)) {
				MessageDialog.openMessageDialog(null, "��Ӧ��"
						+ dto.getScheckvouchertype()
						+ "ƾ֤״̬Ϊ\"У��ɹ�\"��δ�ύTIPS�����ܸ���״̬!");
				return false;
			}
			if (tempdto.getSstatus()
					.equals(DealCodeConstants.VOUCHER_FAIL_TCBS)
					|| (tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SUCCESS_NO_BACK) && StringUtils
							.isBlank(tempdto.getSstampid()))
					|| tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_RECIPE)
					|| tempdto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SENDED)) {
				checkList.add(tempdto);
				return true;
			}
		}
		return true;

	}

	public List getVoucherTypeList() {
		return voucherTypeList;
	}

	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	// �ж��û�ѡ��ļ�¼�Ƿ������ݿ���״̬һ�£������һ�£�����ʾ�û����²�ѯ���ٽ��в���
	// �����û�ѡ����������²�ѯ���ݿ⣬�����ȶ��û�״̬
	public int checkdbstatus() {
		if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			dto.setSvtcode(checkList.get(0).getSvtcode());
		}
		int i = 0;
		try {
			List<TvVoucherinfoDto> queryList = commonDataAccessService
					.findRsByDto(dto);
			if (queryList.size() > 0) {
				HashMap<String, String> map = new HashMap<String, String>();
				for (TvVoucherinfoDto tmp : queryList) {
					if (!map.containsKey(tmp.getSdealno())) {
						map.put(tmp.getSdealno(), tmp.getSstatus());
					}
				}
				for (TvVoucherinfoDto tmp : checkList) {
					if (map.containsKey(tmp.getSdealno())
							&& tmp.getSstatus().equals(
									map.get(tmp.getSdealno()))) {
						continue;
					} else {
						i++;
						break;
					}
				}
			} else {
				return 1;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return 1;
		}
		return i;
	}

	/**
	 * �ж�ӡ�¶�Ӧ��֤���Ƿ��Ѿ�ά��
	 * 
	 * @return
	 */
	public Boolean checkStampIDAndCertID(TsTreasuryDto tDto, TsUsersDto uDto,
			String strecode, TsStamppositionDto stampDto) {
		/*
		 * У������������ת����֤��ID��ӡ��ID�����Ƿ���ά��
		 */
		if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
			if (tDto.getSrotarycertid() == null
					|| tDto.getSrotarycertid().equals("")) {
				MessageDialog.openMessageDialog(null, "�����������" + strecode
						+ "�ڹ���������Ϣ������ " + stampDto.getSstampname()
						+ "֤��ID ����δά���� ");
				return Boolean.FALSE;
			} else if (tDto.getSrotaryid() == null
					|| tDto.getSrotaryid().equals("")) {
				MessageDialog.openMessageDialog(null, "�����������" + strecode
						+ "�ڹ���������Ϣ������ " + stampDto.getSstampname()
						+ "ӡ��ID ����δά���� ");
				return Boolean.FALSE;
			}
		}
		/*
		 * У�����������빫��֤��ID��ӡ��ID�����Ƿ���ά��
		 */
		else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

			if (tDto.getScertid() == null || tDto.getScertid().equals("")) {
				MessageDialog.openMessageDialog(null, "�����������" + strecode
						+ "�ڹ���������Ϣ������ " + stampDto.getSstampname()
						+ "֤��ID ����δά���� ");

				return Boolean.FALSE;
			} else if (tDto.getSstampid() == null
					|| tDto.getSstampid().equals("")) {
				MessageDialog.openMessageDialog(null, "�����������" + strecode
						+ "�ڹ���������Ϣ������ " + stampDto.getSstampname()
						+ "ӡ��ID ����δά���� ");

				return Boolean.FALSE;
			}
		}
		/*
		 * У�����������빫��֤��ID��ӡ��ID�����Ƿ���ά��
		 */
		else if (stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)) {

			if (tDto.getSattachid() == null
					|| tDto.getSattachid().equals("")) {
				MessageDialog.openMessageDialog(null, "�����������" + strecode
						+ "�ڹ���������Ϣ������ " + stampDto.getSstampname()
						+ "ӡ��ID ����δά���� ");

				return Boolean.FALSE;
			}
		}
		else if (stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {

			if (tDto.getSattachcertid() == null
					|| tDto.getSattachcertid().equals("")) {
				MessageDialog.openMessageDialog(null, "�����������" + strecode
						+ "�ڹ���������Ϣ������ " + stampDto.getSstampname()
						+ "ӡ��ID ����δά���� ");

				return Boolean.FALSE;
			}
		}
		/*
		 * У�鵱ǰ�û�˽��֤��ID��ӡ��ID�����Ƿ���ά��
		 */
		else {
			if (uDto.getScertid() == null || uDto.getScertid().equals("")) {
				MessageDialog.openMessageDialog(null, "��ǰ�û�  "
						+ stampDto.getSstampname() + "  ֤��ID����δά���� ");
				return Boolean.FALSE;
			} else if (uDto.getSstampid() == null
					|| uDto.getSstampid().equals("")) {
				MessageDialog.openMessageDialog(null, "��ǰ�û�   "
						+ stampDto.getSstampname() + "  ӡ��ID����δά���� ");
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * У��ǩ��˳�򣬰���ǩ��˳��ǩ��
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public Boolean checkSignStampSeq(TvVoucherinfoDto vDto, String seq,
			List sList, String strecode, TsStamppositionDto stampDto,
			String stampid) throws ITFEBizException {

		List<String> seqList = new ArrayList<String>();
		for (int i = 0; i < sList.size(); i++) {
			TsStamppositionDto tsDto = (TsStamppositionDto) sList.get(i);
			if (tsDto.getSstampsequence() != null
					&& !tsDto.getSstampsequence().equals("")) {
				seqList.add(tsDto.getSstampsequence());
			}
		}
		if (seqList != null && seqList.size() > 0) {
			String[] seqs = seqList.toArray(new String[seqList.size()]);
			ActiveXCompositeVoucherOcx.sortStringArray(seqs);

			String temp = "";
			for (int i = seqs.length - 1; i > -1; i--) {
				if (Integer.parseInt(seqs[i]) < Integer.parseInt(seq)) {
					temp = seqs[i];
					break;
				}
			}
			if (!temp.equals("")) {
				List<TsStamppositionDto> tsList = new ArrayList<TsStamppositionDto>();
				TsStamppositionDto tsDto = new TsStamppositionDto();
				tsDto.setSorgcode(loginInfo.getSorgcode());
				tsDto.setStrecode(strecode);
				tsDto.setSvtcode(vDto.getSvtcode());
				tsDto.setSstampsequence(temp);
				tsList = (List<TsStamppositionDto>) commonDataAccessService
						.findRsByDto(tsDto);
				String err = "";
				for (TsStamppositionDto tstampDto : tsList) {
					err = tstampDto.getSstampname() + " , " + err;
				}
				err = err.substring(0, err.lastIndexOf(","));
				if (stampid == null || stampid.equals("")) {
					MessageDialog.openMessageDialog(null, "������룺"
							+ vDto.getStrecode() + " ƾ֤����: "
							+ vDto.getSvtcode() + vDto.getSvoucherno() + " \""
							+ stampDto.getSstampname() + "\"ǩ��ǰ���� \"" + err
							+ "\"ǩ�£�");
					return Boolean.FALSE;
				} else {
					err = "";
					String[] stampids = stampid.split(",");
					List<TsStamppositionDto> tsList1 = new ArrayList<TsStamppositionDto>();
					for (int j = 0; j < tsList.size(); j++) {
						for (int i = 0; i < stampids.length; i++) {
							if (stampids[i].equals(tsList.get(j)
									.getSstamptype())) {
								tsList1.add(tsList.get(j));
							}
						}
					}
					tsList.removeAll(tsList1);
					if (tsList.size() > 0) {
						for (TsStamppositionDto tstampDto : tsList) {
							err = tstampDto.getSstampname() + " , " + err;
						}
						err = err.substring(0, err.lastIndexOf(","));
						MessageDialog.openMessageDialog(null, "������룺"
								+ vDto.getStrecode() + " ƾ֤����: "
								+ vDto.getSvtcode() + " \""
								+ stampDto.getSstampname() + "\"ǩ��ǰ���� \"" + err
								+ "\"ǩ�£�");
						return Boolean.FALSE;
					}
				}

			}
		}
		return Boolean.TRUE;
	}

	/**
	 * ���ݲ�ͬ��ӡ�����ͽ��и��£��ڸ��¹��������ֿͻ��˸��ºͷ������˸���
	 * 
	 * @throws ITFEBizException
	 * 
	 */
	private void SignStamp(TvVoucherinfoDto vDto, String seq, List sList,
			TsTreasuryDto tDto, List sinList, TsStamppositionDto sDto,
			TsUsersDto uDto) throws ITFEBizException {
		if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
			/*
			 * ����ǩ�·�ʽ 1-�ͻ���ǩ�� ���ͻ���ǩ��
			 */
			if (!voucherLoadService.getOfficialStamp().equals(
					MsgConstant.VOUCHER_OFFICIALSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						tDto.getScertid(), sDto.getSstampposition(), tDto
								.getSstampid()));
			}
		} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
			/*
			 * ת����ǩ�·�ʽ 1-�ͻ���ǩ�� ���ͻ���ǩ��
			 */
			if (!voucherLoadService.getRotaryStamp().equals(
					MsgConstant.VOUCHER_ROTARYSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						tDto.getSrotarycertid(), sDto.getSstampposition(), tDto
								.getSrotaryid()));
			}
		} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)) {
			/*
			 * ������ǩ�·�ʽ 1-�ͻ���ǩ�� ���ͻ���ǩ��
			 */
			if (!voucherLoadService.getRotaryStamp().equals(
					MsgConstant.VOUCHER_ATTACHSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						vDto.getStrecode(), sDto.getSstampposition(), tDto
								.getSattachid()));
			}
		} else if (stamp.equals(MsgConstant.VOUCHERSAMP_BUSS)) {
			/*
			 * ������ǩ�·�ʽ 1-�ͻ���ǩ�� ���ͻ���ǩ��
			 */
			if (!voucherLoadService.getRotaryStamp().equals(
					MsgConstant.VOUCHER_ATTACHSTAMP)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
						vDto.getStrecode(), sDto.getSstampposition(), tDto
								.getSattachcertid()));
			}
		} else {
			/*
			 * ˽��ǩ�·�ʽ���ͻ��˽�ǩ��
			 */
			if (stamp.endsWith(MsgConstant.VOUCHERSIGN_PRIVATE)) {
				sinList.add(ActiveXCompositeVoucherOcx.getVoucherSign(vDto,
						uDto.getScertid(), sDto.getSstampposition()));
			} else {// �ͻ���ǩ˽��ӡ��
				if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
				{
					sinList.add(ActiveXCompositeVoucherOcx.getVoucherStamp(vDto,
							uDto.getScertid(), sDto.getSstampposition(), uDto
									.getSstampid()));
				}
			}

		}
	}

	/**
	 * Direction: ����PDF�ļ�
	 * ename: exportPDF
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportPDF(Object o){
    	try {
    		// ѡ�񱣴�·��
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
    				.getActiveShell());
    		String filePath = path.open();
    		if ((null == filePath) || (filePath.length() == 0)) {
    			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
    			return "";
    		}
	    	for(TvVoucherinfoDto dto : checkList){
	    		List<byte[]> list = voucherLoadService.exportPDF(dto);
	    		byte[] pdfbyte = list.get(0);
				InputStream pdfStream = new java.io.ByteArrayInputStream(pdfbyte);
				java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath+"\\"+dto.getSvoucherno()+".pdf");
				byte[] bytes = new byte[1024];
				int len =-1;
				while((len=pdfStream.read(bytes))!=-1){
					fos.write(bytes, 0, len);
				}
				fos.flush();
				fos.close();
	    	}
    	} catch (Exception e) {
    		log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		}
        return "";
    }
	
//	public List getBizTypeMapperList() {
//		bizTypeMapperList = new ArrayList<Mapper>();
//		Mapper mapper0 = new Mapper();
//		mapper0.setDisplayValue("ֱ��֧��");
//		mapper0.setUnderlyValue("0");
//		Mapper mapper91 = new Mapper();
//		mapper91.setDisplayValue("ʵ���˿�");
//		mapper91.setUnderlyValue("91");
//		bizTypeMapperList.add(mapper0);
//		bizTypeMapperList.add(mapper91);
//		return bizTypeMapperList;
//	}

	public void setBizTypeMapperList(List bizTypeMapperList) {
		this.bizTypeMapperList = bizTypeMapperList;
	}


	/**
	 * Direction: ��������ϸУ��
	 * ename: bigdatavalid
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String bigdatavalid(Object o){
    	TvVoucherinfoDto selectdto = null;
    	if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��ҪУ��ļ�¼��");
			return "";
		}else
		{
			selectdto = checkList.get(0);
		}
    	if(MsgConstant.VOUCHER_NO_2301.equals(selectdto.getSvtcode()))
    	{
    		TvPayreckBigdataDto seardto = new TvPayreckBigdataDto();
    		seardto.setSid(selectdto.getSvoucherno());
    		try {
    			mainlist = commonDataAccessService.findRsByDto(seardto);
				if(mainlist==null||mainlist.size()>0)
				{
					bigdatadto = (TvPayreckBigdataDto)mainlist.get(mainlist.size()-1);
					PageRequest pageRequest = new PageRequest();
					searchtype = MsgConstant.VOUCHER_NO_2301;
					PageResponse pageResponse = retrieve(pageRequest);
					bigdatapage.setPage(pageResponse);
					editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}else
				{
					MessageDialog.openMessageDialog(null, "��ѯ����ƥ������ݣ�");
					return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "��ѯ�쳣��");
				return "";
			}
    		return "������2305���ݲ鿴";
    	}
    	else
    	{
    		TvPayreckBigdataBackDto seardto = new TvPayreckBigdataBackDto();
    		seardto.setSid(selectdto.getSvoucherno());
    		try {
    			mainlist = commonDataAccessService.findRsByDto(seardto);
				if(mainlist==null||mainlist.size()>0)
				{
					bigdatabackdto = (TvPayreckBigdataBackDto)mainlist.get(0);
					PageRequest pageRequest = new PageRequest();
					searchtype = MsgConstant.VOUCHER_NO_2302;
					PageResponse pageResponse = retrieve(pageRequest);
					bigdatabackpage.setPage(pageResponse);
					editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}else
				{
					MessageDialog.openMessageDialog(null, "��ѯ����ƥ������ݣ�");
					return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "��ѯ�쳣��");
				return "";
			}
    		return "������2306���ݲ鿴";
    	}
    }
    /**
	 * Direction: �������б�˫��
	 * ename: bigdatadouble
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String bigdatadouble(Object o){
    	PageRequest pageRequest = new PageRequest();
    	if(o instanceof TvPayreckBigdataBackDto)
    	{
    		bigdatabackdto = (TvPayreckBigdataBackDto)o;
    		searchtype = MsgConstant.VOUCHER_NO_2302;
    		PageResponse pageResponse = retrieve(pageRequest);
			bigdatabackpage.setPage(pageResponse);
    	}else if(o instanceof TvPayreckBigdataDto)
    	{
    		bigdatadto = (TvPayreckBigdataDto)o;
    		searchtype = MsgConstant.VOUCHER_NO_2301;
    		PageResponse pageResponse = retrieve(pageRequest);
			bigdatapage.setPage(pageResponse);
    	}
    	editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.bigdatadouble(o);
    }
    /**
	 * Direction: ����
	 * ename: returnvoucherload
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String returnvoucherload(Object o){
        searchtype="";
        return super.returnvoucherload(o);
    }
	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public List getBizTypeMapperList() {
		return bizTypeMapperList;
	}
	public Map<String, TsQueryAmtDto> getAmtMap() throws ITFEBizException{
		if(amtMap==null)
		{
			amtMap = new HashMap<String, TsQueryAmtDto>();
			List<TsQueryAmtDto> queryAmtDtoList =  commonDataAccessService.findRsByDto(new TsQueryAmtDto());
			if(queryAmtDtoList!=null&&queryAmtDtoList.size()>0)
			{
				for(TsQueryAmtDto tempdto:queryAmtDtoList)
					amtMap.put(tempdto.getSorgcode()+tempdto.getStrecode()+tempdto.getSbiztype(), tempdto);
				
			}			
		}
		return amtMap;
	}

	public void setAmtMap(Map<String, TsQueryAmtDto> amtMap) {
		this.amtMap = amtMap;
	}
	public TvPayreckBigdataDto getBigdatadto() {
		return bigdatadto;
	}

	public void setBigdatadto(TvPayreckBigdataDto bigdatadto) {
		this.bigdatadto = bigdatadto;
	}

	public TvPayreckBigdataBackDto getBigdatabackdto() {
		return bigdatabackdto;
	}

	public void setBigdatabackdto(TvPayreckBigdataBackDto bigdatabackdto) {
		this.bigdatabackdto = bigdatabackdto;
	}

	public PagingContext getBigdatapage() {
		return bigdatapage;
	}

	public void setBigdatapage(PagingContext bigdatapage) {
		this.bigdatapage = bigdatapage;
	}

	public PagingContext getBigdatabackpage() {
		return bigdatabackpage;
	}

	public void setBigdatabackpage(PagingContext bigdatabackpage) {
		this.bigdatabackpage = bigdatabackpage;
	}

	public String getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
	}

	public List getMainlist() {
		return mainlist;
	}

	public void setMainlist(List mainlist) {
		this.mainlist = mainlist;
	}
}