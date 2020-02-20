package com.cfcc.itfe.client.dataquery.deletedataquery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsCheckdeloptlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author hua
 * @time 12-07-20 00:03:57 ��ϵͳ: DataQuery ģ��:deleteDataQuery ���:DeleteDataQuery
 */
public class DeleteDataQueryBean extends AbstractDeleteDataQueryBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(DeleteDataQueryBean.class);
	private String reportpath = "com/cfcc/itfe/client/ireport/deleteDataReport.jasper";
	private List reportRs = null;
	private Map reportmap = new HashMap();
	private ITFELoginInfo loginfo;

	public DeleteDataQueryBean() {
		super();
		sbiztype = "";
		svouno = "";
		startdate = TimeFacade.getCurrentDateTime();
		enddate = TimeFacade.getCurrentDateTime();
		reportRs = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
	}

	/**
	 * Direction: ��ѯ��ӡ ename: queryService ���÷���: viewers: ������־���� messages:
	 */
	public String queryService(Object o) {
		TsCheckdeloptlogDto paramdto = new TsCheckdeloptlogDto();
		String sqlW = "";
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE) && !loginfo.getSorgcode().equals(StateConstant.STAT_CENTER_CODE)) {
			paramdto.setSorgcode(loginfo.getSorgcode());
		}
		
		if(sbiztype != null && !"".equals(sbiztype)) {
			paramdto.setSbiztype(sbiztype);
		}
		if(svouno != null && !"".equals(svouno)) {
			paramdto.setSvouno(svouno);
		}
		if(null != startdate) {
			sqlW = sqlW + " and date(t_opetime) >= '"+startdate+"'";
		}
		if(null != startdate) {
			sqlW = sqlW + " and date(t_opetime) <= '"+enddate+"'";
		}
		try {
			List resList = commonDataAccessService.findRsByDto(paramdto, sqlW);
			if(null == resList || resList.size() == 0) {
				MessageDialog.openMessageDialog(null, "û���ҵ���Ӧ��¼!");
				return null;
			}
			List<TsCheckdeloptlogDto> delList = new ArrayList<TsCheckdeloptlogDto>();
			for(Object obj : resList) {
				TsCheckdeloptlogDto logDto = (TsCheckdeloptlogDto)obj;
				logDto.setSbiztype(getBizname(logDto.getSbiztype()));
				delList.add(logDto);
			}
			reportRs.clear();
			reportRs.addAll(delList);
			reportmap.put("printDate",  new SimpleDateFormat("yyyy��MM��dd��").format(new java.util.Date()));
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.queryService(o);
	}
	
	/**
	 * ����ҵ�����ͻ�ȡ��Ӧ��ҵ������
	 */
	public static String getBizname(String bizType){
		
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
		    return "ֱ��֧�����";
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			return "��Ȩ֧�����";
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// ʵ���ʽ�
			return "ʵ���ʽ�";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// ����
			return "����";
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// �˿�
			return "�˿�";
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// ��������
			return "��������";
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// ���а���֧��
			return "���а���ֱ��֧��";
		} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY.equals(bizType)) {
			return "���а���ֱ��֧����������";
		} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK.equals(bizType)) {
			return "���а���ֱ��֧�����������˻�";
		} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY.equals(bizType)) {
			return "���а�����Ȩ֧����������";
		} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK.equals(bizType)) {
			return "���а�����Ȩ֧�����������˻�";
		}
		
		return bizType;
		
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

	public String getReportpath() {
		return reportpath;
	}

	public void setReportpath(String reportpath) {
		this.reportpath = reportpath;
	}

	public List getReportRs() {
		return reportRs;
	}

	public void setReportRs(List reportRs) {
		this.reportRs = reportRs;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}

}