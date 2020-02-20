package com.cfcc.itfe.client.sendbiz.taxorgsosecry;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-01-10 10:23:36 ��ϵͳ: SendBiz ģ��:TaxOrgSoSecry ���:TaxOrgSoSecry
 */
public class TaxOrgSoSecryBean extends AbstractTaxOrgSoSecryBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TaxOrgSoSecryBean.class);
	private ITFELoginInfo loginfo = null;
	private String sendOrgCode;
	private String inTreDate;;
	List<TsTaxorgDto> finddtolist = null;

	public TaxOrgSoSecryBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		inTreDate =TimeFacade.getCurrentStringTime();
	}

	/**
	 * Direction: ���� ename: applyReport ���÷���: viewers: * messages:
	 */
	public String applyReport(Object o) {

		StringBuilder sb = new StringBuilder();
		if (StringUtils.isBlank(sendOrgCode)) {
			sb.append("����������벻��Ϊ��\n");
		}
		Date tmp = null ;
		try{
		 tmp = TimeFacade.parseDate(inTreDate, "yyyyMMdd");
		}catch(Exception e){
			sb.append("������ڸ�ʽ����\n");
		}
		if (tmp != null && tmp.after(TimeFacade.getCurrentDate())) {
			sb.append("������ڲ��ܳ�����ǰ����\n");
		}
		if(!"".equals(sb.toString())){
			MessageDialog.openMessageDialog(null,sb.toString());
			return null;
		}
		
		try {
			// ���Ļ�������
			String centerorg = itfeCacheService.cacheGetCenterOrg();
			// �жϵ�ǰ���ջ��ش����Ƿ����
			TsTaxorgDto finddto = new TsTaxorgDto();
			finddto.setStaxorgcode(sendOrgCode); // ���ջ��ش���
			if (!loginfo.getSorgcode().equals(centerorg)) {
				finddto.setSorgcode(loginfo.getSorgcode()); // �����������
			}
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,
					"2");
			if (null == finddtolist || finddtolist.size() == 0) {
				MessageDialog.openMessageDialog(null,
						"����������벻���ڻ�Ȩ�޲���,����д��ȷ�ķ���������룡");
				return null;
			}
			this.taxOrgSoSecryService.sendMsg(sendOrgCode.trim(),inTreDate, MsgConstant.MSG_NO_1024);
			MessageDialog.openMessageDialog(null, "���뱨�ķ��ͳɹ�");
		} catch (Exception e) {
			log.error("1024���ķ���ʧ��!", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.applyReport(o);
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

	public String getSendOrgCode() {
		return sendOrgCode;
	}

	public void setSendOrgCode(String sendOrgCode) {
		this.sendOrgCode = sendOrgCode;
	}

	public String getInTreDate() {
		return inTreDate;
	}

	public void setInTreDate(String inTreDate) {
		this.inTreDate = inTreDate;
	}

}