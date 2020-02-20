package com.cfcc.itfe.client.recbiz.reportdownload;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 09-10-23 13:30:18 ��ϵͳ: RecBiz ģ��:ReportDownLoad ���:ReportDownload
 */
@SuppressWarnings("unchecked")
public class ReportDownloadBean extends AbstractReportDownloadBean implements
		IPageDataProvider {

	private ITFELoginInfo loginfo = null;
	
	private String strecode = null; // �������
	private List trelist = null;
	private String rptdate = null; // ��������
	private String taxprop = null ; // ���ջ�������
	private String rptrange = null ; // ����Χ
	private String searchArea = null;//Ͻ����־
	public ReportDownloadBean() {
		super();
		rptdate=TimeFacade.getCurrentStringTimebefor();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		setTaxprop("4"); // Ĭ�����ջ���Ϊ����
		setRptrange("1"); // Ĭ�ϱ���ΧΪȫϽ

		TsTreasuryDto tredto = new TsTreasuryDto();
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			tredto.setSorgcode(loginfo.getSorgcode());
		}else{
			tredto.setStreattrib(StateConstant.COMMON_YES);
		}
		try {
			//trelist = commonDataAccessService.findRsByDto(tredto," order by S_TRELEVEL");
			trelist = commonDataAccessService.getSubTreCode(loginfo.getSorgcode());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	/**
	 * Direction: �������� ename: downLoad ���÷���: viewers: * messages:
	 */
	public String downLoad(Object o) {
		if (null == strecode || "".equals(strecode.trim())) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ر���Ĺ�����룡");
			return super.downLoad(o);
		}
		if (null == rptdate || "".equals(rptdate.trim())) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ر���ı������ڣ�");
			return super.downLoad(o);
		}
		if (null == taxprop || "".equals(taxprop.trim())
				|| !("1".equals(taxprop) || "2".equals(taxprop) || "3".equals(taxprop) || "4".equals(taxprop) || "5".equals(taxprop))) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ر�������ջ�������[1����˰ 2����˰ 3������ 4������ 5������ ]��");
			return super.downLoad(o);
		}
		if (null == rptrange || "".equals(rptrange.trim())
				|| !("0".equals(rptrange) || "1".equals(rptrange))) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ر���ķ�Χ[0-����  1-ȫϽ ]��");
			return super.downLoad(o);
		}
		
		String taxname = "";
		if("1".equals(taxprop)){
			taxname = "gs";
		}else if("2".equals(taxprop)){
			taxname = "ds";
		}else if("3".equals(taxprop)){
			taxname = "hg";
		}else if("4".equals(taxprop)){
			taxname = "cz";
		}else if("5".equals(taxprop)){
			taxname = "qt";
		}

		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String clientpath = "c:" + dirsep + "client" + dirsep + "report" + dirsep + rptdate + dirsep + loginfo.getSorgcode() + dirsep + taxname + dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		try {
			String fileinfo = "";
			
			List<String> filepathList = rptDownloadService.downloadRpt(strecode, rptdate, taxprop,rptrange);
			if(null == filepathList || filepathList.size() == 0){
				MessageDialog.openMessageDialog(null, "û���ҵ�Ҫ���صı������ݣ�");
				return super.downLoad(o);
			}
			
			for(int i = 0 ; i < filepathList.size(); i++){
				String serverfile = filepathList.get(i);
				String filename = getFileNameByPath(serverfile);
				String clientfile = clientpath + filename;
				
				ClientFileTransferUtil.downloadFile(filepathList.get(i),clientfile);
				
				fileinfo = fileinfo + clientfile + "\n";
			}
			
			MessageDialog.openMessageDialog(null, "���±������سɹ���\n" + fileinfo);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.downLoad(o);
	}
	
	private String getFileNameByPath(String serverfile){
		if(serverfile.indexOf("report") >=0){
			return serverfile.substring(serverfile.indexOf("report") + 7, serverfile.length());
		}
		
		if(serverfile.indexOf(File.separator) >=0){
			int count = serverfile.lastIndexOf(File.separator);
			return serverfile.substring(count + 2, serverfile.length());
		}

		if(serverfile.indexOf("/") >=0){
			int count = serverfile.lastIndexOf("/");
			return serverfile.substring(count + 1, serverfile.length());
		}
		
		return serverfile;
	}

	/**
	 * Direction: �������� ename: requestRpt ���÷���: viewers: * messages:
	 */
	public String requestRpt(Object o) {
		try {
			if ((null == strecode || "".equals(strecode.trim()))&&(this.searchArea==null||"".equals(this.searchArea))) {
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
						.getArea())) {
					MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ͱ�������Ĺ�����룡");
				} else {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ͱ�������Ĺ�������ѡ�����뷶Χ��");
				}
				return super.requestRpt(o);
			}
			if (null == rptdate || "".equals(rptdate.trim())) {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ͱ�������ı������ڣ�");
				return super.requestRpt(o);
			}
			if(strecode==null||"".equals(strecode.trim()))
			{
				List<TsTreasuryDto> findTreCodeList = null;
				if("0".equals(searchArea))
				{
					TsTreasuryDto findto = new TsTreasuryDto();
					findto.setSorgcode(loginfo.getSorgcode());
					findTreCodeList = commonDataAccessService.findRsByDto(findto);
				}else if("1".equals(searchArea))
				{
					findTreCodeList = commonDataAccessService.getSubTreCode(loginfo.getSorgcode());
				}
				for(int i=0;i<findTreCodeList.size();i++)
					rptDownloadService.requestRpt(findTreCodeList.get(i).getStrecode(), rptdate);
			}else
			{
				rptDownloadService.requestRpt(strecode, rptdate);
			}
			
			MessageDialog.openMessageDialog(null, "���ͱ�������ɹ���");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			MessageDialog.openMessageDialog(null, "���ͱ�������ʧ�ܣ�"+e.getMessage());
		}
		return super.requestRpt(o);
	}
	
	

	@Override
	public String sendAll(Object o) {
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			MessageDialog.openMessageDialog(null, "�������û�,��Ȩ�ޣ�");
			return null;
		}
		if (null == rptdate || "".equals(rptdate.trim())) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ͱ�������ı������ڣ�");
			return super.sendAll(o);
		}
		StringBuffer result = new StringBuffer();
		try {
			List<TsTreasuryDto> listtre = commonDataAccessService.findAllDtos(TsTreasuryDto.class);
			for(TsTreasuryDto tmpTre : listtre){
				TsConvertfinorgDto finorg = new TsConvertfinorgDto();
				finorg.setStrecode(tmpTre.getStrecode());
				finorg.setSorgcode(tmpTre.getSorgcode());
				List<TsConvertfinorgDto> listfin = commonDataAccessService.findRsByDto(finorg);
				if(null == listfin || listfin.size() == 0){
					result.append(tmpTre.getStrename() + "û��ά�������Ӧ�Ĳ����������룬�޷���������!\r");
				}else{
					rptDownloadService.sendApplyInfo(rptdate, MsgConstant.MSG_NO_5001 , listfin.get(0).getSfinorgcode(),tmpTre.getSorgcode());
				}
			}
			if(StringUtils.isBlank(result.toString())){
				MessageDialog.openMessageDialog(null, "���ͱ�������ɹ���");
			}else{
				MessageDialog.openMessageDialog(null, result.toString());
			}
		} catch (Exception e) {
			MessageDialog.openMessageDialog(null, e.toString());
			return null;
		}
		return super.sendAll(o);
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

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
		if(this.strecode!=null&&!"".equals(this.strecode))
			this.searchArea="";
		editor.fireModelChanged();
	}

	public String getRptdate() {
		return rptdate;
	}

	public void setRptdate(String rptdate) {
		this.rptdate = rptdate;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

	public String getTaxprop() {
		return taxprop;
	}

	public void setTaxprop(String taxprop) {
		this.taxprop = taxprop;
	}

	public String getRptrange() {
		return rptrange;
	}

	public void setRptrange(String rptrange) {
		this.rptrange = rptrange;
	}

	public String getSearchArea() {
		return searchArea;
	}

	public void setSearchArea(String searchArea) {
		this.searchArea = searchArea;
		if(this.searchArea!=null&&!"".equals(this.searchArea))
			strecode = "";
		editor.fireModelChanged();
	}

}