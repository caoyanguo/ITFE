package com.cfcc.itfe.client.dataquery.findatastatdown;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 12-02-21 15:37:44 ��ϵͳ: DataQuery ģ��:FinDataStatDown ���:FinDataStatDown
 */
public class FinDataStatDownBean extends AbstractFinDataStatDownBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(FinDataStatDownBean.class);
	ITFELoginInfo loginfo;
	String sleTrimFlag;
	String sleBudKind;
	String sleTreCode;
	String sleTaxOrgCode;
	String sleOfFlag;
	String sleSumItem;
	Date sdate;
	// ��������б�
	private List<TsTreasuryDto> treList;
	// ���ջ��ش����б�
	private List<TsTaxorgDto> taxorgList;
	// ѡ�񱨱�list
	private List<TdEnumvalueDto> checklist = null;
	// ��������list
	private List<TdEnumvalueDto> reportlist = null;

	public FinDataStatDownBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		treList = new ArrayList<TsTreasuryDto>();
		checklist = new ArrayList<TdEnumvalueDto>();
		reportlist = new ArrayList<TdEnumvalueDto>();
		taxorgList = new ArrayList<TsTaxorgDto>();
		init();

	}

	/**
	 * Direction: ����excel�ļ� ename: exportFile ���÷���: viewers: * messages:
	 */
	public String exportFile(Object o) {
		if (checklist.size() <= 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�����ı���!");
			return "";
		}
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		StringBuffer sbf = new StringBuffer("");
		if(null!=sleTreCode&&!sleTreCode.equals("")&&sleTreCode.equals("0")){
			for (TsTreasuryDto trecodeDto : treList) {
				if(trecodeDto.getStrecode().equals("0")){
					continue;
				}
				sbf.append(exportFileForEachTrecode(filePath, trecodeDto.getStrecode()));
			}
		}else{
			sbf.append(exportFileForEachTrecode(filePath, sleTreCode));
		}
		
		if(!sbf.toString().equals("")){
			log.debug(sbf.toString());
			MessageDialog.openMessageDialog(null, sbf.toString());
			return "";
		}
		return super.exportFile(o);
	}
	
	
	public String exportFileForEachTrecode(String filePath,String sleTreCode){
		//����У����Ϣ
		String returnInfo="";
		TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
		// �����������
		incomedto.setStrecode(sleTreCode);
		// ���ݹ��������ͬ�����ش���
		/**
		 * ��һ�� ���ݹ�������ҵ���Ӧ�Ĳ�������
		 */
		TsConvertfinorgDto finadto = new TsConvertfinorgDto();
		finadto.setStrecode(sleTreCode);
		finadto.setSorgcode(loginfo.getSorgcode());

		List finorgList = null;
		try {
			finorgList = commonDataAccessService.findRsByDto(finadto);
		} catch (ITFEBizException e1) {
			MessageDialog.openErrorDialog(null, e1);
		}
		if (finorgList == null || finorgList.size() == 0) {
			returnInfo="����������Ϣ��δά������["+sleTreCode+"]�Ͳ�������Ķ�Ӧ��ϵ��";
		} else if (finorgList.size() > 1) {
			returnInfo="����["+sleTreCode+"]ֻ�ܶ�Ӧһ��ͬ��������";
		} else {
			finadto = (TsConvertfinorgDto) finorgList.get(0);
			incomedto.setSfinorgcode(finadto.getSfinorgcode());
		}

		// ���ջ��ش���
		incomedto.setStaxorgcode(sleTaxOrgCode);
		// Ԥ������
		incomedto.setSbudgettype(sleBudKind);
		// Ͻ����־
		incomedto.setSbelongflag(sleOfFlag);
		// �����ڱ�־
		incomedto.setStrimflag(sleTrimFlag);
		// ����
		incomedto.setSrptdate(sdate.toString().replaceAll("-", ""));
		// �Ƿ񺬿�ϼ�
		incomedto.setSdividegroup(sleSumItem);
		
		List<String> filelist = new ArrayList<String>();
		String serverFilePath;
		String clientFilePath;
		String partfilepath = "";
		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		String dirsep = File.separator;
		try {
			filelist = finDataStatDownService.makeRptFile(incomedto,
					checklist);
			for (int i = 0; i < filelist.size(); i++) {
				serverFilePath = filelist.get(i).replace("\\", "/");
				int j = serverFilePath.lastIndexOf("/");
				partfilepath = serverFilePath.replaceAll(serverFilePath
						.substring(0, j + 1), "");
				clientFilePath = filePath + dirsep + strdate + dirsep
						+ partfilepath;
				File f = new File(clientFilePath);
				File dir = new File(f.getParent());
				if (!dir.exists()) {
					dir.mkdirs();
				} else {
					/**
					 * �޸�Ϊ��Ŀ¼���ڣ�����Ŀ¼������ͬ�ļ����ļ�ʱ������ɾ�������ļ� by hua 2013-06-18
					 */
					if(f.exists()) {
						f.delete();
					}
				}
				
				ClientFileTransferUtil.downloadFile(serverFilePath,
						clientFilePath);

			}
			if (filelist.size() > 0) {
				returnInfo="����["+sleTreCode+"]�µı������سɹ���������"	+ filelist.size() + "���ļ���\r\n";
			} else {
				returnInfo="����["+sleTreCode+"]�µı���û�������ļ���\r\n";
			}

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";

		} catch (FileTransferException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		return returnInfo;
	}

	/**
	 * Direction: ȫѡ ename: allSelect ���÷���: viewers: * messages:
	 */
	public String allSelect(Object o) {
		if (checklist.size() > 0) {
			checklist = new ArrayList<TdEnumvalueDto>();
		} else {
			checklist.addAll(reportlist);
		}
		this.editor.fireModelChanged();
		return "";
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

	/**
	 * ���ݹ�������ѯ���ջ��ش��� ���������ջ��ض�Ӧ��ϵ��
	 */
	private List<TsTaxorgDto> querytaxoeglist(String sleTreCode) {
		try {
			taxorgList.clear();
			TsTaxorgDto orgdto_1 = new TsTaxorgDto();
			orgdto_1.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto_1.setStrecode(sleTreCode);// �������
			orgdto_1.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS);
			orgdto_1.setStaxorgname("�������ջ���(TBS)");
			orgdto_1.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorgList.add(orgdto_1);

			TsTaxorgDto orgdto0 = new TsTaxorgDto();
			orgdto0.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto0.setStrecode(sleTreCode);// �������
			orgdto0.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			orgdto0.setStaxorgname("�������ջ���");
			orgdto0.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorgList.add(orgdto0);

			TsTaxorgDto orgdto1 = new TsTaxorgDto();
			orgdto1.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto1.setStrecode(sleTreCode);// �������
			orgdto1.setStaxorgcode(MsgConstant.MSG_TAXORG_NATION_CLASS);
			orgdto1.setStaxorgname("��˰");
			orgdto1.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
			taxorgList.add(orgdto1);

			TsTaxorgDto orgdto2 = new TsTaxorgDto();
			orgdto2.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto2.setStrecode(sleTreCode);// �������
			orgdto2.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			orgdto2.setStaxorgname("��˰");
			orgdto2.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
			taxorgList.add(orgdto2);

			TsTaxorgDto orgdto3 = new TsTaxorgDto();
			orgdto3.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto3.setStrecode(sleTreCode);// �������
			orgdto3.setStaxorgcode(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			orgdto3.setStaxorgname("����");
			orgdto3.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
			taxorgList.add(orgdto3);

			TsTaxorgDto orgdto4 = new TsTaxorgDto();
			orgdto4.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto4.setStrecode(sleTreCode);// �������
			orgdto4.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			orgdto4.setStaxorgname("����");
			orgdto4.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
			taxorgList.add(orgdto4);

			TsTaxorgDto orgdto5 = new TsTaxorgDto();
			orgdto5.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto5.setStrecode(sleTreCode);// �������
			orgdto5.setStaxorgcode(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			orgdto5.setStaxorgname("����");
			orgdto5.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
			taxorgList.add(orgdto5);
			if(null!=sleTreCode&&!sleTreCode.equals("")&&!sleTreCode.equals("0")){
				// ��ѯ�����Ӧ���ջ��ش�����dto
				TsTaxorgDto taxorgdto = new TsTaxorgDto();
				taxorgdto.setStrecode(sleTreCode);// �������
				taxorgList.addAll(commonDataAccessService.findRsByDto(taxorgdto));
				//���Ӻϲ�������ջ���
				TdTaxorgMergerDto  taxorg = new TdTaxorgMergerDto();
				taxorg.setSbookorgcode(loginfo.getSorgcode());
				List <TdTaxorgMergerDto> taxMergeorgList = commonDataAccessService.findRsByDto(taxorg);
				if (null!=taxMergeorgList && taxMergeorgList.size()>0) {
					HashMap<String,String> map = new HashMap<String, String>();
					for (TdTaxorgMergerDto _dto : taxMergeorgList) {
						 if (!map.containsKey(_dto.getSaftertaxorgcode())) {
							 TsTaxorgDto adddto = new TsTaxorgDto();
							 adddto.setSorgcode(_dto.getSbookorgcode());
							 adddto.setStaxorgcode(_dto.getSaftertaxorgcode());
							 adddto.setStaxorgname(_dto.getStaxorgname());
							 map.put(_dto.getSaftertaxorgcode(), _dto.getStaxorgname());
							 taxorgList.add(adddto);
						}
						
						
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		// ��ʼ�����ջ��ش���Ĭ��ֵ
		if (taxorgList.size() > 0) {
			if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo.getArea())) {
				sleTaxOrgCode = MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS;
			} else {
				sleTaxOrgCode = MsgConstant.MSG_TAXORG_SHARE_CLASS;
			}
		}
		return taxorgList;
	}

	private void init() {
		TsTreasuryDto trecodeDto = new TsTreasuryDto();
		trecodeDto.setSorgcode(loginfo.getSorgcode());
		trecodeDto.setStrecode("0");
		trecodeDto.setStrename("���й���");
		treList.add(trecodeDto);
		// ��ѯ���������dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// ���Ļ�������
		String centerorg = null;

		try {
			centerorg = StateConstant.ORG_CENTER_CODE;
			// ���Ļ�����ȡ�����й����б�
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList.addAll(commonDataAccessService.findRsByDto(tredto));
			}
			// �����Ļ�����ȡ�õ�¼������Ӧ����
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList.addAll(commonDataAccessService.findRsByDto(tredto));
			}
			// ��������
			TdEnumvalueDto _dto = new TdEnumvalueDto();
			_dto.setStypecode(StateConstant.BILL_TYPE);
			reportlist = commonDataAccessService.findRsByDto(_dto," ORDER BY integer(S_value)");
			if (reportlist.size() > 0) {
				checklist.addAll(reportlist);
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		// ��ʼ���������Ĭ��ֵ
		if (treList.size() > 0) {
			sleTreCode = treList.get(0).getStrecode();
		}
		// ��ʼ�����ջ��ش���
		querytaxoeglist(this.getSleTreCode());
		// �����ڱ�־
		sleTrimFlag = StateConstant.TRIMSIGN_FLAG_NORMAL;
		// Ͻ����־
		sleOfFlag = MsgConstant.RULE_SIGN_SELF;
		// Ԥ������
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// ��ϼ�
		sleSumItem = MsgConstant.INPUT_SIGN_NO;
		// ��ѯ����
		sdate = TimeFacade.getCurrentDateTime();
	}

	public String getSleTrimFlag() {
		return sleTrimFlag;
	}

	public void setSleTrimFlag(String sleTrimFlag) {
		this.sleTrimFlag = sleTrimFlag;
	}

	public String getSleBudKind() {
		return sleBudKind;
	}

	public void setSleBudKind(String sleBudKind) {
		this.sleBudKind = sleBudKind;
	}

	public String getSleTreCode() {
		return sleTreCode;
	}

	public void setSleTreCode(String sleTreCode) {
		this.sleTreCode = sleTreCode;
		List<TsTaxorgDto> list = querytaxoeglist(sleTreCode);
		if (list != null && list.size() > 0) {
			taxorgList = list;
		} else {
			taxorgList.clear();
		}
		editor.fireModelChanged();
	}

	public String getSleTaxOrgCode() {
		return sleTaxOrgCode;
	}

	public void setSleTaxOrgCode(String sleTaxOrgCode) {
		this.sleTaxOrgCode = sleTaxOrgCode;
	}

	public String getSleOfFlag() {
		return sleOfFlag;
	}

	public void setSleOfFlag(String sleOfFlag) {
		this.sleOfFlag = sleOfFlag;
	}

	public String getSleSumItem() {
		return sleSumItem;
	}

	public void setSleSumItem(String sleSumItem) {
		this.sleSumItem = sleSumItem;
	}

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
	}

	public List<TsTaxorgDto> getTaxorgList() {
		return taxorgList;
	}

	public void setTaxorgList(List<TsTaxorgDto> taxorgList) {
		this.taxorgList = taxorgList;
	}

	public Date getSdate() {
		return sdate;
	}

	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}

	public List<TdEnumvalueDto> getReportlist() {
		return reportlist;
	}

	public void setReportlist(List<TdEnumvalueDto> reportlist) {
		this.reportlist = reportlist;
	}

	public List<TdEnumvalueDto> getChecklist() {
		return checklist;
	}

	public void setChecklist(List<TdEnumvalueDto> checklist) {
		this.checklist = checklist;
	}

}