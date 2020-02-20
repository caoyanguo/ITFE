package com.cfcc.itfe.client.sendbiz.directpaydayreport;

import itferesourcepackage.VoucherRegularCheckAccountSvtcodeEnumFactory;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeDirectPayDayreportOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
/**
 * codecomment: 
 * @author zhangliang
 * @time   15-02-04 18:02:06
 * ��ϵͳ: SendBiz
 * ģ��:directPayDayreport
 * ���:DirectPayDayreport
 */
@SuppressWarnings("unchecked")
public class DirectPayDayreportBean extends AbstractDirectPayDayreportBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DirectPayDayreportBean.class);
    List<TvVoucherinfoDto> checkList = null;
	List subVoucherList = null;
	// �û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	// ǩ������
	private String stamp;
	// ǩ���б�
	private List<TsStamppositionDto> stampList = null;
	// ƾ֤����
	private String voucherType = "";

    public DirectPayDayreportBean() {
      super();
        loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		dto = new TvVoucherinfoDto();
		dto.setScheckdate(TimeFacade.getCurrentStringTime());
		dto.setSorgcode(loginInfo.getSorgcode());
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		subVoucherList = new ArrayList<Mapper>();
		getSubVoucherList();
		checkList = new ArrayList<TvVoucherinfoDto>();
		TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
		tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
		List<TsTreasuryDto> list = null;
		try {
			list = (List<TsTreasuryDto>) commonDataAccessService
					.findRsByDto(tsTreasuryDto);
			if (list != null && list.size() > 0) {
				dto.setStrecode(list.get(0).getStrecode());// �������
			}
		} catch (ITFEBizException e) {
			log.error(e);
		}
		pagingcontext = new PagingContext(this);             
    }
    /**
	 * Direction: ����ƾ֤ ename: voucherGenerator ���÷���: viewers: * messages:
	 */
	public String voucherGenerator(Object o) {
		if (dto.getStrecode() == null || "".equals(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
			return "";
		}
		if (dto.getScheckdate() == null || "".equals(dto.getScheckdate())) {
			MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
			return "";
		}
		int count = 0;
		try {
			StringBuffer sbuf = new StringBuffer();
			TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
			if (null ==dto.getSvtcode() || dto.getSvtcode().equals("")) {
				List voucherMapperList = new VoucherRegularCheckAccountSvtcodeEnumFactory()
						.getEnums(null);
				for (Mapper mapper : (List<Mapper>) voucherMapperList) {
					voucherDto.setSvtcode(mapper.getUnderlyValue() + "");
					// �ж��Ƿ��Ѿ�����ƾ֤
					sbuf.append(voucherIsRepeat(voucherDto));
				}
			} else {
				voucherDto.setSvtcode(dto.getSvtcode());
				// �ж��Ƿ��Ѿ�����ƾ֤
				sbuf.append(voucherIsRepeat(voucherDto));
			}
			if (sbuf.toString() != null && !sbuf.toString().equals("")) {

				 if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��Ϣ��ʾ", "�������µĻص�ƾ֤�Ѿ����ɣ��Ƿ��������ɣ�")){
				    return "";
				 }
			}
			if (null== dto.getSvtcode()  || dto.getSvtcode().equals("")) {
				List voucherMapperList = new VoucherRegularCheckAccountSvtcodeEnumFactory()
						.getEnums(null);
				for (Mapper mapper : (List<Mapper>) voucherMapperList) {
					voucherDto.setSvtcode(mapper.getUnderlyValue() + "");
					String result = voucherGererate(voucherDto);
					if (result == null || result.equals("")) {
						continue;
					}
					count += Integer.parseInt(result);
				}
			} else {
				voucherDto.setSvtcode(dto.getSvtcode());
				String result = voucherGererate(voucherDto);
				if (result != null && !result.equals("")) {
					count += Integer.parseInt(result);
				}
			}
			if (count == 0) {
				MessageDialog.openMessageDialog(null, "û�и����ڵ�ƾ֤���ɣ�");
				return "";
			}
			MessageDialog.openMessageDialog(null, "�ص�ƾ֤���ɲ����ɹ����ɹ�����Ϊ��" + count
					+ " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception(e.getMessage(), e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("����ƾ֤���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherGenerator(o);
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
	 * Direction: ��ѯ ename: search ���÷���: viewers: * messages:
	 */
	public String search(Object o) {
		refreshTable();
		return super.search(o);
	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = new PageResponse();
		pageResponse = retrieve(pageRequest);
		if (pageResponse.getTotalCount() == 0
				&& (StringUtils.isBlank(dto.getSstatus()) || dto.getSstatus()
						.equals(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK)))
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼����������ƾ֤��");
		else if (pageResponse.getTotalCount() == 0
				&& (dto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)))
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼������ǩ�£�");
		else if (pageResponse.getTotalCount() == 0
				&& dto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS))
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼");
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: ȫѡ ename: selectAll ���÷���: viewers: * messages:
	 */
	public String selectAll(Object o) {
		if (checkList == null || checkList.size() == 0) {
			checkList = new ArrayList<TvVoucherinfoDto>();
			checkList.addAll(pagingcontext.getPage().getData());
		} else
			checkList.clear();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.selectAll(o);
	}

	/**
	 * Direction: ���͵���ƾ֤ ename: sendReturnVoucher ���÷���: viewers: * messages:
	 */
	public String voucherSend(Object o) {
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���͵���ƾ֤�ļ�¼��");
			return "";
		}
		int count = 0;
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ",
				"��ȷ��Ҫ��ѡ�еļ�¼ִ�з��͵���ƾ֤������")) {
			return "";
		}
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
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()
					.trim()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ \"ǩ�³ɹ�\" �ļ�¼��");
				return "";
			}
		}
		try {
			count = voucherLoadService.voucherReturnSuccess(checkList);
			MessageDialog.openMessageDialog(null, "���͵���ƾ֤   "
					+ checkList.size() + " �����ɹ�����Ϊ��" + count + " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("���͵���ƾ֤����������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);

			return "";
		}
		return super.voucherSend(o);

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
			ActiveXCompositeDirectPayDayreportOcx.init(0);

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�鿴�쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.voucherView(o);
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
	 * Direction: �ص�״̬��ѯ ename: queryStatusReturnVoucher ���÷���: viewers: *
	 * messages:
	 */
	public String queryStatusReturnVoucher(Object o) {
		int count=0;
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴���͵���ƾ֤��ɹ�״̬�ļ�¼��");
    		return "";
    	}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim()))){
	    			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ���͵���ƾ֤��ɹ��ɹ��ļ�¼��");
	        		return "";
	    		}
	    }
    	try {
			count=voucherLoadService.queryStatusReturnVoucher(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤�鿴�ص�״̬"+checkList.size()+" �����ɹ�����Ϊ��"+count+" , ��鿴��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);						
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("ƾ֤�鿴�ص�״̬���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}		
        return super.queryStatusReturnVoucher(o);
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

	public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException {
		return voucherLoadService.voucherStampXml(vDto);
	}

	public void refreshTable() {
		init();
		if (checkList != null)
			checkList.clear();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
	}

	public String voucherGererate(TvVoucherinfoDto voucherDto)
			throws ITFEBizException {
		List<TvVoucherinfoDto> list = getVoucherList(voucherDto);
		if (list == null || list.size() == 0) {
			return "";
		}

		List resultList = voucherLoadService.voucherGenerate(list);
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0) + "";
		}
		return "";
	}

	/**
	 * ����ƾ֤ƾ֤����
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	private String voucherIsRepeat(TvVoucherinfoDto voucherDto)
			throws ITFEBizException {
		List<TvVoucherinfoDto> list = getVoucherList(voucherDto);
		if (list == null || list.size() == 0) {
			return "";
		}
		String vtcodeType = voucherDto.getSvtcode();
		// ��ž�������µ��ظ�ƾ֤
		List<TvVoucherinfoDto> lists = new ArrayList<TvVoucherinfoDto>();
		// ����ƾ֤�б�
		for (TvVoucherinfoDto tvVoucherinfoDto : list) {
			TvVoucherinfoDto idto = new TvVoucherinfoDto();
			idto.setSorgcode(tvVoucherinfoDto.getSorgcode());
			idto.setStrecode(tvVoucherinfoDto.getStrecode());
			idto.setSvtcode(tvVoucherinfoDto.getSvtcode());
			idto.setScheckdate(tvVoucherinfoDto.getScheckdate());
			idto.setScreatdate(tvVoucherinfoDto.getScreatdate());
			idto.setShold1(tvVoucherinfoDto.getShold1());
			List<TvVoucherinfoDto> voucherList = commonDataAccessService
					.findRsByDto(idto);
			if (voucherList != null && voucherList.size() > 0) {
				lists.add(tvVoucherinfoDto);
			}
		}
		StringBuffer sbf = new StringBuffer();
		if (lists != null && lists.size() > 0) {
			for (TvVoucherinfoDto tvVoucherinfoDto : lists) {
				sbf.append("����Ϊ��").append(tvVoucherinfoDto.getStrecode())
						.append(", ҵ������Ϊ��")
						.append(vtcodeType).append(", �� ")
						.append(tvVoucherinfoDto.getScheckdate()).append(
								" �����ڵ�����ص�������,").append("\r\n");
			}
		}
		return sbf.toString();
	}


	public List<TvVoucherinfoDto> getVoucherList(TvVoucherinfoDto voucherDto)
			throws ITFEBizException {
		List<TvVoucherinfoDto> list = new ArrayList<TvVoucherinfoDto>();
		List<TsTreasuryDto> tList = new ArrayList<TsTreasuryDto>();
		TsTreasuryDto tDto = new TsTreasuryDto();
		tDto.setSorgcode(loginInfo.getSorgcode());
		if (dto.getStrecode() == null || dto.getStrecode().equals("")) {
			tList = commonDataAccessService.findRsByDto(tDto);
		} else {
			tDto.setStrecode(dto.getStrecode());
			tList.add(tDto);
		}
		if (tList == null || tList.size() == 0)
			throw new ITFEBizException("�������δά����", new Exception(""));
		TvVoucherinfoDto vDto = null;
		for (TsTreasuryDto tsDto : tList) {
			vDto = (TvVoucherinfoDto) dto.clone();
			vDto.setStrecode(tsDto.getStrecode());
			vDto.setScreatdate(TimeFacade.getCurrentStringTime());
			list.add(vDto);
		}
		return list;
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
		StringBuffer wheresql = new StringBuffer();
		wheresql.append(" 1=1 ");
		if (dto.getSvtcode() != null&&dto.getSvtcode().length()>=5)
		{
			dto.setShold1(dto.getSvtcode().substring(4));
			dto.setSvtcode(MsgConstant.VOUCHER_NO_2206);
		}
		else
		{
			dto.setSvtcode(MsgConstant.VOUCHER_NO_2206);
			dto.setShold1(null);
		}
		try {
			page = commonDataAccessService.findRsByDtoPaging(dto, pageRequest,
					wheresql.toString(), " TS_SYSUPDATE desc");
			return page;
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤��ѯ�����쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * Direction: ǩ�� ename: voucherStamp ���÷���: viewers: * messages:
	 */
	public String voucherStamp(Object o) {
		boolean ocxflag = false;
		List<TvVoucherinfoDto> checkList = new ArrayList<TvVoucherinfoDto>();
		String stamp = null;
		TvVoucherinfoDto dto = new TvVoucherinfoDto();
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

		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�²�����")) {
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
					.getSstatus().trim()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ��ļ�¼��");
				return "";
			}
		}
		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
				return "";
			}
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
				if (!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)
						&& !stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
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
				if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
					if (tDto.getSrotarycertid() == null
							|| tDto.getSrotarycertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");

						return "";
					} else if (tDto.getSrotaryid() == null
							|| tDto.getSrotaryid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");

						return "";
					}
				} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

					if (tDto.getScertid() == null
							|| tDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");

						return "";
					} else if (tDto.getSstampid() == null
							|| tDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");

						return "";
					}

				} else {
					if (uDto.getScertid() == null
							|| uDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�  "
								+ stampDto.getSstampname() + "  ֤��ID����δά���� ");
						return "";
					} else if (uDto.getSstampid() == null
							|| uDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�   "
								+ stampDto.getSstampname() + "  ӡ��ID����δά���� ");
						return "";
					}
				}
				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {
						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
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
								ActiveXCompositeDirectPayDayreportOcx
										.sortStringArray(seqs);
								String temp = "";
								for (int i = seqs.length - 1; i > -1; i--) {
									if (Integer.parseInt(seqs[i]) < Integer
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
									for (TsStamppositionDto tstampDto : tsList) {
										err = tstampDto.getSstampname() + " , "
												+ err;
									}
									err = err
											.substring(0, err.lastIndexOf(","));
									if (stampid == null || stampid.equals("")) {
										MessageDialog
												.openMessageDialog(
														null,
														"������룺"
																+ vDto
																		.getStrecode()
																+ " ƾ֤����: "
																+ vDto
																		.getSvtcode()
																+ vDto
																		.getSvoucherno()
																+ " \""
																+ stampDto
																		.getSstampname()
																+ "\"ǩ��ǰ���� \""
																+ err + "\"ǩ�£�");

										return "";

									} else {
										err = "";
										String[] stampids = stampid.split(",");
										List<TsStamppositionDto> tsList1 = new ArrayList<TsStamppositionDto>();
										for (int j = 0; j < tsList.size(); j++) {
											for (int i = 0; i < stampids.length; i++) {
												if (stampids[i]
														.equals(tsList
																.get(j)
																.getSstamptype())) {
													tsList1.add(tsList.get(j));
												}
											}
										}
										tsList.removeAll(tsList1);
										if (tsList.size() > 0) {
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
																	+ "\"ǩ��ǰ���� \""
																	+ err
																	+ "\"ǩ�£�");

											return "";
										}
									}

								}
							}
						}
						if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {
							if (!voucherLoadService.getOfficialStamp().equals(
									MsgConstant.VOUCHER_OFFICIALSTAMP)) {
								sinList.add(ActiveXCompositeDirectPayDayreportOcx
										.getVoucherStamp(vDto, tDto
												.getScertid(), sDto
												.getSstampposition(), tDto
												.getSstampid()));
							}
						} else if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
							if (!voucherLoadService.getOfficialStamp().equals(
									MsgConstant.VOUCHER_ROTARYSTAMP)) {
								sinList.add(ActiveXCompositeDirectPayDayreportOcx
										.getVoucherStamp(vDto, tDto
												.getSrotarycertid(), sDto
												.getSstampposition(), tDto
												.getSrotaryid()));
							}
						} else {
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeDirectPayDayreportOcx
										.getVoucherStamp(vDto, uDto.getScertid(),
												sDto.getSstampposition(), uDto
														.getSstampid()));
							}
						}
						vList.add(sinList);
					}
				}
				list.add(uDto);
				list.add(tDto);
				list.add(sDto);
				list.add(sList.size());
				list.add(vList);
				lists.add(list);
			}
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
			return "";

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ǩ�²��������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return "";

		}
		return super.voucherStamp(o);
	}

	/**
	 * Direction:ǩ�³��� ename: voucherStampCancle ���÷���: viewers: * messages:
	 */
	public String voucherStampCancle(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�³����ļ�¼��");
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
			if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
					.getSstatus()))
					&& !(DealCodeConstants.VOUCHER_STAMP.equals(infoDto
							.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ���ǩ�³ɹ��ļ�¼��");
				return "";
			}
		}

		try {
			if (!((TvVoucherinfoDto) checkList.get(0)).getSvtcode().equals(
					dto.getSvtcode())) {
				MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
				return "";

			}

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
			boolean managerPermission = false;
			if (flag == false) {
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""
						+ stampDto.getSstampname() + "\"  ǩ��Ȩ�ޣ�����ͨ��������Ȩ����ǩ��");
				String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
				if (!AdminConfirmDialogFacade.open("B_247", "ҵ�����ƾ֤", "��Ȩ�û�"
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
					}
					String stampuserboolean = vDto.getSstampuser().split(",")[j];
					if (!stampuserboolean.equals(usercode)) {
						MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"
								+ vDto.getSvoucherno() + "   \""
								+ stampDto.getSstampname()
								+ "\" ���ǵ�ǰ�û���ǩ����ͨ��������Ȩ����ǩ��");
						String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
						if (!AdminConfirmDialogFacade
								.open("B_247", "���뱨��ƾ֤", "��Ȩ�û�"
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

				if (stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)) {
					if (tDto.getSrotarycertid() == null
							|| tDto.getSrotarycertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");

						return "";
					} else if (tDto.getSrotaryid() == null
							|| tDto.getSrotaryid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");
						return "";
					}
				} else if (stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)) {

					if (tDto.getScertid() == null
							|| tDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "֤��ID ����δά���� ");
						return "";
					} else if (tDto.getSstampid() == null
							|| tDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "�����������"
								+ strecode + "�ڹ���������Ϣ������ "
								+ stampDto.getSstampname() + "ӡ��ID ����δά���� ");
						return "";
					}

				} else {
					if (uDto.getScertid() == null
							|| uDto.getScertid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�  "
								+ stampDto.getSstampname() + "  ֤��ID����δά���� ");
						return "";
					} else if (uDto.getSstampid() == null
							|| uDto.getSstampid().equals("")) {
						MessageDialog.openMessageDialog(null, "��ǰ�û�   "
								+ stampDto.getSstampname() + "  ӡ��ID����δά���� ");
						return "";
					}

				}

				for (TvVoucherinfoDto vDto : checkList) {
					if (vDto.getStrecode().equals(strecode)) {

						sinList = new ArrayList();
						sinList.add(vDto);
						stampid = vDto.getSstampid();
						String seq = sDto.getSstampsequence();
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
								ActiveXCompositeDirectPayDayreportOcx
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
												: user));
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
			count = voucherLoadService.voucherStampCancle(lists);
			MessageDialog.openMessageDialog(null, "ƾ֤����ǩ��   "
					+ checkList.size() + " �����ɹ�����Ϊ��" + count + " ��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			return "";

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ǩ�²������������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
			return "";

		}
		return super.voucherStampCancle(o);
	}

	/**
	 * ˢ��ǩ������
	 * 
	 */
	public void refreshStampType(String type) {
		TsStamppositionDto tsDto = new TsStamppositionDto();
		tsDto.setSorgcode(dto.getSorgcode());
		tsDto.setSvtcode(dto.getSvtcode());
		tsDto.setStrecode(dto.getStrecode());
		Set<String> set = new HashSet<String>();
		TsStamppositionDto sDto = new TsStamppositionDto();
		List<TsStamppositionDto> tList = null;
		stampList = new ArrayList<TsStamppositionDto>();
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
		if (type == null) {
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
	}

	public TvVoucherinfoDto getDto(TvVoucherinfoDto dto)
			throws ITFEBizException {
		TvVoucherinfoPK pk = new TvVoucherinfoPK();
		pk.setSdealno(dto.getSdealno());
		return (TvVoucherinfoDto) commonDataAccessService.find(pk);
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		DirectPayDayreportBean.log = log;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public List<TsStamppositionDto> getStampList() {
		return stampList;
	}

	public void setStampList(List<TsStamppositionDto> stampList) {
		this.stampList = stampList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		if(voucherType!=null&&voucherType.length()>=4)
		{
			dto.setSvtcode(voucherType.substring(0, 4));
			dto.setShold1(voucherType.length()>= 5 ? voucherType.substring(4): null);
			pagingcontext.setPage(new PageResponse());
			refreshStampType(null);
		}
	}

	public List getSubVoucherList() {
		if (subVoucherList == null || subVoucherList.size() <= 0) {
			subVoucherList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_2206,
					"ֱ��֧���ձ������˿");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_2206+"1",
					"ֱ��֧���ձ��������˿");
			Mapper map3 = new Mapper(MsgConstant.VOUCHER_NO_2206+"2",
					"ֱ��֧���˿��ձ���");
			subVoucherList.add(map);
			subVoucherList.add(map1);
			subVoucherList.add(map3);
		}
		return subVoucherList;
	}

	public void setSubVoucherList(List subVoucherList) {
		this.subVoucherList = subVoucherList;
	}
}