package com.cfcc.itfe.client.dataquery.findatastatdownfortj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
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
 * @author Administrator
 * @time   12-10-15 15:43:33
 * ��ϵͳ: DataQuery
 * ģ��:FinDataStatDownForTJ
 * ���:FinDataStatDownForTJ
 */
public class FinDataStatDownForTJBean extends AbstractFinDataStatDownForTJBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(FinDataStatDownForTJBean.class);
	ITFELoginInfo loginfo;
	String sleTrimFlag;
	String sleBudKind;
	String sleTreCode;
	String sleTaxOrgCode;
	String sleOfFlag;
	String sleSumItem;

	Date sdate;
	Date agentDate;
	// ��������б�
	private List<TsTreasuryDto> treList;
	// ���ջ��ش����б�
	private List<TsTaxorgDto> taxorgList;
	// ѡ�񱨱�list
	private List<TdEnumvalueDto> checklist = null;
	// ��������list
	private List<TdEnumvalueDto> reportlist = null;

	// ��Ӧ��Ͻ����������list
	private List<String> treCodeList = new ArrayList<String>();
	// ѡ���Ӧ��Ͻ������������
	private List<TsTreasuryDto> chkcentralTreList = new ArrayList<TsTreasuryDto>();
	// ��Ӧ��Ͻ���������������
	private List<TsTreasuryDto> centralTreList = new ArrayList<TsTreasuryDto>();
	// ѡ���Ӧ��Ͻ����ʡ����
	private List<TsTreasuryDto> chkprovinceTreList = new ArrayList<TsTreasuryDto>();
	// ��Ӧ��Ͻ����ʡ����
	private List<TsTreasuryDto> provinceTreList = new ArrayList<TsTreasuryDto>();
	// ѡ���Ӧ��Ͻ����������
	private List<TsTreasuryDto> chkcityTreList = new ArrayList<TsTreasuryDto>();
	// ��Ӧ��Ͻ����������
	private List<TsTreasuryDto> cityTreList = new ArrayList<TsTreasuryDto>();
	// ѡ���Ӧ��Ͻ����������
	private List<TsTreasuryDto> chkcountyTreList = new ArrayList<TsTreasuryDto>();
	// ��Ӧ��Ͻ����������
	private List<TsTreasuryDto> countyTreList = new ArrayList<TsTreasuryDto>();
	
	//�ֹ�����List
	List inputList = new ArrayList();
 	//�ֹ�������1
	boolean bisselect1 = false;
	String srptdate1;
	String strecode1;
	boolean bcentral1 = false;
	boolean bprovince1 = false;
	boolean bcity1 = false;
	boolean bcountry1 = false;
	//�ֹ�������2
	boolean bisselect2 = false;
	String srptdate2;
	String strecode2;
	boolean bcentral2 = false;
	boolean bprovince2 = false;
	boolean bcity2 = false;
	boolean bcountry2 = false;
	//�ֹ�������3
	boolean bisselect3 = false;
	String srptdate3;
	String strecode3;
	boolean bcentral3 = false;
	boolean bprovince3 = false;
	boolean bcity3 = false;
	boolean bcountry3 = false;
	//�ֹ�������4
	boolean bisselect4 = false;
	String srptdate4;
	String strecode4;
	boolean bcentral4 = false;
	boolean bprovince4 = false;
	boolean bcity4 = false;
	boolean bcountry4 = false;
	//�ֹ�������5
	boolean bisselect5 = false;
	String srptdate5;
	String strecode5;
	boolean bcentral5 = false;
	boolean bprovince5 = false;
	boolean bcity5 = false;
	boolean bcountry5 = false;
	
	//�Ƿ���ĩ
	boolean bisEndMonth = false;
	//��ĩ�����������
	Date agentEndDate;

	public FinDataStatDownForTJBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		checklist = new ArrayList();
		init();

	}

	/**
	 * Direction: ����excel�ļ� ename: exportFile ���÷���: viewers: * messages:
	 */
	public String exportFile(Object o) {
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
			log.error("���ڲ���������Ϣ��ά������Ͳ�������Ķ�Ӧ��ϵ!");
			MessageDialog.openMessageDialog(null, "���ڲ���������Ϣ��ά������Ͳ�������Ķ�Ӧ��ϵ!");
			return "";
		} else if (finorgList.size() > 1) {
			log.error("һ������ֻ�ܶ�Ӧһ��ͬ������");
			MessageDialog.openMessageDialog(null, "һ������ֻ�ܶ�Ӧһ��ͬ������");
			return "";
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
		//����ⱨ������
		// �Ƿ񺬿�ϼ�
		incomedto.setSdividegroup(sleSumItem);
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<HashMap> filelist = new ArrayList<HashMap>();
		if ((null == filePath) || (filePath.length() == 0)) {
			return "";
		}
		String serverFilePath;
		String clientFilePath;
		String partfilepath = "";
		String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
		String dirsep = File.separator;
		//�����Ͻ����ѡ�и������ε�MAP<String,List> �磺<2100000000,(1,2,3,4)> 1:���� 2:ʡ3:��4:��
		HashMap map = createTreandLevelMap();
		if (checklist.size() > 0) {
			try {
				//��Ӵ���ⱨ�����ڵ�Map��
				HashMap params = new HashMap();
				params.put("agentDate", agentDate.toString().replaceAll("-", ""));
				params.put("treLevels", map);
				params.put("inputList", inputList);
				if(bisEndMonth){
					params.put("agentEndDate", agentEndDate.toString().replaceAll("-", ""));
				}else{
					params.put("agentEndDate", "");
				}
				filelist = finDataStatDownForTJService.makeRptFile(incomedto, checklist, params);
				for (int i = 0; i < filelist.size(); i++) {
					HashMap fileMap = filelist.get(i);
					Set set = fileMap.keySet();
					for(Object key : set){
						String filename = (String)key;
						HashMap levelMapMoney = (HashMap) fileMap.get(filename);
						serverFilePath = filename.replace("\\", "/");
						int j = serverFilePath.lastIndexOf("/");
						partfilepath = serverFilePath.replaceAll(serverFilePath
								.substring(0, j + 1), "");
						clientFilePath = filePath + dirsep + strdate + dirsep
								+ partfilepath;
						File f = new File(clientFilePath);
						File dir = new File(f.getParent());
						if(f.exists()){//����ļ�������ɾ���ļ�
							System.gc();//����jvm��������
							f.delete();
						}
						if (!dir.exists()) {
							dir.mkdirs();
						}
						ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
						//Ԥ�����뱨����ʾû�����ݵĹ������
						if("1".equals(partfilepath.substring(12, 13))){
							//����Set
							Set treSet = new HashSet();
							//�ӵڶ��п�ʼ�����ļ���¼����
							ArrayList<String> record = null;
							List<ArrayList<String>> recordList = file2List(f);
							//ȡ���ļ������ݵĹ������
							 for(int k = 1; k < recordList.size(); k++){
								 record = recordList.get(k);//��ȡ��ǰ��¼
								 String trecode = record.get(0);//�������
								 treSet.add(trecode);
							 }
							 //�жϹ�������Ƿ����ļ��д���
							 String msg = "";
							 String outmsg = "";
							 for(int l = 1; l < treCodeList.size(); l++){
								 String trecode = treCodeList.get(l);
								 if(!treSet.contains(trecode)){
									 msg += "\t"+ trecode;
								 }
							 }
							if("".equals(msg)){
								outmsg = "";
							}else{
								outmsg = "�������" + msg + "���������ļ��в����ڣ�����";
							}
							if(levelMapMoney != null){
								BigDecimal centralsumamt = (BigDecimal) levelMapMoney.get(MsgConstant.BUDGET_LEVEL_CENTER);
								BigDecimal provincesumamt = (BigDecimal) levelMapMoney.get(MsgConstant.BUDGET_LEVEL_PROVINCE);
								BigDecimal citysumamt = (BigDecimal) levelMapMoney.get(MsgConstant.BUDGET_LEVEL_DISTRICT);
								BigDecimal countysumamt = (BigDecimal) levelMapMoney.get(MsgConstant.BUDGET_LEVEL_PREFECTURE);
								MessageDialog.openMessageDialog(null, "Ԥ�����뱨����\r\n���뼶�ϼƣ�"
										 +centralsumamt+"\r\nʡ���ϼƣ�"+provincesumamt+"\r\n�м��ϼƣ�"+citysumamt+"\r\n�ؼ��ϼƣ�"+countysumamt
										 +"\r\n"+outmsg);
							}
						}
					}
				}
				if (filelist.size() > 0) {
					MessageDialog.openMessageDialog(null, "�������سɹ���������"
							+ filelist.size() + "���ļ�");
				} else {
					MessageDialog.openMessageDialog(null, "û�������ļ���");
				}

			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);

			} catch (FileTransferException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
		} else {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�����ı���!");
			return null;
		}
		return super.exportFile(o);
	}

	/**
	 * Direction: ȫѡ ename: allSelect ���÷���: viewers: * messages:
	 */
	public String allSelect(Object o) {
		TdEnumvalueDto _dto = new TdEnumvalueDto();
		_dto.setStypecode(StateConstant.BILL_TYPE);
		try {
			reportlist = commonDataAccessService.findRsByDtoUR(_dto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		if (checklist.size() > 0) {
			checklist = new ArrayList<TdEnumvalueDto>();
		} else {
			checklist = reportlist;
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
	 * ���ݹ�������ѯ��Ͻ����
	 * 
	 */
	private void queryLowerTreasuryList(String selTreCode) {
		treCodeList.clear();
		chkcentralTreList.clear();
		centralTreList.clear();
		provinceTreList.clear();
		chkprovinceTreList.clear();
		cityTreList.clear();
		chkcityTreList.clear();
		countyTreList.clear();
		chkcountyTreList.clear();
		HashMap tremap = createTreMap();
		TsTreasuryDto treasurydto = new TsTreasuryDto();
		treasurydto.setSgoverntrecode(selTreCode);
		// ��Ӧ��Ͻ�������list
		 List<TsTreasuryDto> proxyTreList = null;
		try {
			// ��Ӧ��Ͻ������������
			proxyTreList = commonDataAccessService.findRsByDto(treasurydto);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		for(TsTreasuryDto dto : proxyTreList){
			treCodeList.add(dto.getStrecode());
		}
		for(TsTreasuryDto dto : countyTreList){
			if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(tremap.get(sleTreCode))){
				treCodeList.add(dto.getStrecode());
			}
		}
		// ��Ͻ������������
		for(TsTreasuryDto dto : proxyTreList){
			chkcentralTreList.add(dto);
			centralTreList.add(dto);
		}
		for(TsTreasuryDto dto : countyTreList){
			if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(tremap.get(sleTreCode))){
				chkcentralTreList.add(dto);
				centralTreList.add(dto);
			}
		}
		// ��Ͻ����ʡ����
		for(TsTreasuryDto dto : proxyTreList){
			provinceTreList.add(dto);
			chkprovinceTreList.add(dto);
		}
		for(TsTreasuryDto dto : countyTreList){
			if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(tremap.get(sleTreCode))){
				provinceTreList.add(dto);
				chkprovinceTreList.add(dto);
			}
		}
		// ��Ͻ����������
		for(TsTreasuryDto dto : proxyTreList){
			cityTreList.add(dto);
			chkcityTreList.add(dto);
		}
		for(TsTreasuryDto dto : countyTreList){
			if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(tremap.get(sleTreCode))){
				cityTreList.add(dto);
				chkcityTreList.add(dto);
			}
		}
		// ��Ͻ����������
		for(TsTreasuryDto dto : proxyTreList){
			countyTreList.add(dto);
			chkcountyTreList.add(dto);
		}
	}
	
	private HashMap<String, List<String>> createTreandLevelMap(){
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> centralTreCodeList = new ArrayList<String>();
		List<String> provinceTreCodeList = new ArrayList<String>();
		List<String> cityTreCodeList = new ArrayList<String>();
		List<String> countyTreCodeList = new ArrayList<String>();
		// ��Ͻ��������
		for(TsTreasuryDto centralDto : chkcentralTreList){
			centralTreCodeList.add(centralDto.getStrecode());
		}
		// ��Ͻ����ʡ����
		for(TsTreasuryDto provinceDto : chkprovinceTreList){
			provinceTreCodeList.add(provinceDto.getStrecode());
		}
		// ��Ͻ����������
		for(TsTreasuryDto cityDto : chkcityTreList){
			cityTreCodeList.add(cityDto.getStrecode());
		}
		// ��Ͻ����������
		for(TsTreasuryDto countyDto : chkcountyTreList){
			countyTreCodeList.add(countyDto.getStrecode());
		}
		for(String treCode : treCodeList){
			List<String> levelList = new ArrayList<String>();
			if(!centralTreCodeList.contains(treCode)){
				levelList.add("1");
			}
			if(!provinceTreCodeList.contains(treCode)){
				levelList.add("2");
			}
			if(!cityTreCodeList.contains(treCode)){
				levelList.add("3");
			}
			if(!countyTreCodeList.contains(treCode)){
				levelList.add("4");
			}
			map.put(treCode, levelList);
		}
		return map;
	}
	
	private HashMap<String, String> createTreMap(){
		TsTreasuryDto treasurydto = new TsTreasuryDto();
		List<TsTreasuryDto> treList = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			//ȡ�����й����뼶��map
			treList = commonDataAccessService.findRsByDto(treasurydto);
		}catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		for(TsTreasuryDto countyDto : treList){
			map.put(countyDto.getStrecode(), countyDto.getStrelevel());
		}
		return map;
	}

	/**
	 * ���ݹ�������ѯ���ջ��ش��� ���������ջ��ض�Ӧ��ϵ��
	 */
	private List querytaxoeglist(String sleTreCode) {
		// ��ѯ�����Ӧ���ջ��ش�����dto
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		taxorgdto.setStrecode(sleTreCode);// �������
		try {
			taxorgList = commonDataAccessService.findRsByDto(taxorgdto);

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

		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		// ��ʼ�����ջ��ش���Ĭ��ֵ
		if (taxorgList.size() > 0) {
			taxorgdto.setStrecode(sleTreCode);// �������
			sleTaxOrgCode = MsgConstant.MSG_TAXORG_SHARE_CLASS;
		}
		return taxorgList;
	}

	private void init() {
		// ��ѯ���������dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// ���Ļ�������
		String centerorg = null;

		try {
			centerorg = StateConstant.ORG_CENTER_CODE;
			// ���Ļ�����ȡ�����й����б�
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// �����Ļ�����ȡ�õ�¼������Ӧ����
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// ��������
			TdEnumvalueDto _dto = new TdEnumvalueDto();
			_dto.setStypecode(StateConstant.BILL_TYPE);
			reportlist = commonDataAccessService.findRsByDtoUR(_dto);
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
		sleOfFlag = MsgConstant.RULE_SIGN_ALL;
		// Ԥ������
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// ����ϼ�
		sleSumItem = MsgConstant.INPUT_SIGN_YES;
		// ��ѯ����
		sdate = TimeFacade.getCurrentDateTime();
		
		agentDate = TimeFacade.getCurrentDateTime();
		//��ĩ�����������
		agentEndDate = TimeFacade.getCurrentDateTime();
		//��Ͻ��
		queryLowerTreasuryList(this.getSleTreCode());
	}
	
	private String createSpecailInput(){
		inputList.clear();
		ArrayList inputList1;
		ArrayList<String> treList;
		if ((bisselect1) == true){
			inputList1 = new ArrayList();
			treList = new ArrayList<String>();
			String errorMsg = "";
			if("".equals(srptdate1)||srptdate1==null){
				errorMsg+="�����뵥ѡ������һ�ı�������!\r\n";
			}
			if("".equals(strecode1)||strecode1==null){
				errorMsg+="�����뵥ѡ������һ�Ĺ������!\r\n";
			}
			if(bcentral1==false && bprovince1 == false && bcity1 == false && bcountry1 == false){
				errorMsg+="�����뵥ѡ������һ�ļ���!\r\n";
			}
			if(!"".equals(errorMsg)){
				MessageDialog.openMessageDialog(null, errorMsg);
				return errorMsg;
			}else {
				inputList1.add(srptdate1);
				inputList1.add(strecode1);
				if(bcentral1 == true) treList.add("1");
				if(bprovince1 == true) treList.add("2");
				if(bcity1 == true) treList.add("3");
				if(bcountry1 == true) treList.add("4");
				inputList1.add(treList);
			}
			inputList.add(inputList1);
		}
		if ((bisselect2) == true){
			inputList1 = new ArrayList<String>();
			treList = new ArrayList<String>();
			String errorMsg = "";
			if("".equals(srptdate2)||srptdate2==null){
				errorMsg+="�����뵥ѡ��������ı�������!\r\n";
			}
			if("".equals(strecode2)||strecode2==null){
				errorMsg+="�����뵥ѡ��������Ĺ������!\r\n";
			}
			if(bcentral2==false && bprovince2 == false && bcity2 == false && bcountry2 == false){
				errorMsg+="�����뵥ѡ��������ļ���!\r\n";
			}
			if(!"".equals(errorMsg)){
				MessageDialog.openMessageDialog(null, errorMsg);
				return errorMsg;
			}else {
				inputList1.add(srptdate2);
				inputList1.add(strecode2);
				if(bcentral2 == true) treList.add("1");
				if(bprovince2 == true) treList.add("2");
				if(bcity2 == true) treList.add("3");
				if(bcountry2 == true) treList.add("4");
				inputList1.add(treList);
			}
			inputList.add(inputList1);
		}
		if ((bisselect3) == true){
			inputList1 = new ArrayList<String>();
			treList = new ArrayList<String>();
			String errorMsg = "";
			if("".equals(srptdate3)||srptdate3==null){
				errorMsg+="�����뵥ѡ���������ı�������!\r\n";
			}
			if("".equals(strecode3)||strecode3==null){
				errorMsg+="�����뵥ѡ���������Ĺ������!\r\n";
			}
			if(bcentral3==false && bprovince3 == false && bcity3 == false && bcountry3 == false){
				errorMsg+="�����뵥ѡ���������ļ���!\r\n";
			}
			if(!"".equals(errorMsg)){
				MessageDialog.openMessageDialog(null, errorMsg);
				return errorMsg;
			}else {
				inputList1.add(srptdate3);
				inputList1.add(strecode3);
				if(bcentral3 == true) treList.add("1");
				if(bprovince3 == true) treList.add("2");
				if(bcity3 == true) treList.add("3");
				if(bcountry3 == true) treList.add("4");
				inputList1.add(treList);
			}
			inputList.add(inputList1);
		}
		if ((bisselect4) == true){
			inputList1 = new ArrayList<String>();
			treList = new ArrayList<String>();
			String errorMsg = "";
			if("".equals(srptdate4)||srptdate4==null){
				errorMsg+="�����뵥ѡ�������ĵı�������!\r\n";
			}
			if("".equals(strecode4)||strecode4==null){
				errorMsg+="�����뵥ѡ�������ĵĹ������!\r\n";
			}
			if(bcentral4==false && bprovince4 == false && bcity4 == false && bcountry4 == false){
				errorMsg+="�����뵥ѡ�������ĵļ���!\r\n";
			}
			if(!"".equals(errorMsg)){
				MessageDialog.openMessageDialog(null, errorMsg);
				return errorMsg;
			}else {
				inputList1.add(srptdate4);
				inputList1.add(strecode4);
				if(bcentral4 == true) treList.add("1");
				if(bprovince4 == true) treList.add("2");
				if(bcity4 == true) treList.add("3");
				if(bcountry4 == true) treList.add("4");
				inputList1.add(treList);
			}
			inputList.add(inputList1);
		}
		if ((bisselect5) == true){
			inputList1 = new ArrayList<String>();
			treList = new ArrayList<String>();
			String errorMsg = "";
			if("".equals(srptdate5)||srptdate5==null){
				errorMsg+="�����뵥ѡ��������ı�������!\r\n";
			}
			if("".equals(strecode5)||strecode5==null){
				errorMsg+="�����뵥ѡ��������Ĺ������!\r\n";
			}
			if(bcentral5==false && bprovince5 == false && bcity5 == false && bcountry5 == false){
				errorMsg+="�����뵥ѡ��������ļ���!\r\n";
			}
			if(!"".equals(errorMsg)){
				MessageDialog.openMessageDialog(null, errorMsg);
				return errorMsg;
			}else {
				inputList1.add(srptdate5);
				inputList1.add(strecode5);
				if(bcentral5 == true) treList.add("1");
				if(bprovince5 == true) treList.add("2");
				if(bcity5 == true) treList.add("3");
				if(bcountry5 == true) treList.add("4");
				inputList1.add(treList);
			}
			inputList.add(inputList1);
		}
		return "";
	}
	
	/**
	 * Direction: ��ѡ�����뷵��
	 * ename: inputBack
	 * ���÷���: 
	 * viewers: �������ݵ��������
	 * messages: 
	 */
    public String inputBack(Object o){
    	if(!"".equals(createSpecailInput())){
    		return "";
    	}else{
    		return super.inputBack(o);
    	}
    }
    
	/**
	 *  ���ļ����ݰ��ж�ȡת��ΪLIST
	 * @return
	 * @throws FileOperateException 
	 */
	private List<ArrayList<String>> file2List(File f){
		try{
			BufferedReader bufferReader = new BufferedReader(new FileReader(f));
			//�ļ���һ���ļ�����������
			String  data;
			List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			while(( data  = bufferReader.readLine()) != null){
				String[] tokens = data.split(",");
				ArrayList<String> alList = new ArrayList<String>();
				for(int k = 0; k < tokens.length; k++){
				    alList.add(tokens[k]);
				}
				list.add(alList);
			}
			return list;
		}catch(IOException e){
			MessageDialog.openMessageDialog(null, "��ȡ�ļ�ʧ��,IO����.");
			return null;
		}
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
		List list = querytaxoeglist(sleTreCode);
		queryLowerTreasuryList(sleTreCode);
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

	public Date getAgentDate() {
		return agentDate;
	}

	public void setAgentDate(Date agentDate) {
		this.agentDate = agentDate;
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

	public List<String> getTreCodeList() {
		return treCodeList;
	}

	public void setTreCodeList(List<String> treCodeList) {
		this.treCodeList = treCodeList;
	}

	public List<TsTreasuryDto> getChkcentralTreList() {
		return chkcentralTreList;
	}

	public void setChkcentralTreList(List<TsTreasuryDto> chkcentralTreList) {
		this.chkcentralTreList = chkcentralTreList;
	}

	public List<TsTreasuryDto> getCentralTreList() {
		return centralTreList;
	}

	public void setCentralTreList(List<TsTreasuryDto> centralTreList) {
		this.centralTreList = centralTreList;
	}

	public List<TsTreasuryDto> getChkprovinceTreList() {
		return chkprovinceTreList;
	}

	public void setChkprovinceTreList(List<TsTreasuryDto> chkprovinceTreList) {
		this.chkprovinceTreList = chkprovinceTreList;
	}

	public List<TsTreasuryDto> getProvinceTreList() {
		return provinceTreList;
	}

	public void setProvinceTreList(List<TsTreasuryDto> provinceTreList) {
		this.provinceTreList = provinceTreList;
	}

	public List<TsTreasuryDto> getChkcityTreList() {
		return chkcityTreList;
	}

	public void setChkcityTreList(List<TsTreasuryDto> chkcityTreList) {
		this.chkcityTreList = chkcityTreList;
	}

	public List<TsTreasuryDto> getCityTreList() {
		return cityTreList;
	}

	public void setCityTreList(List<TsTreasuryDto> cityTreList) {
		this.cityTreList = cityTreList;
	}

	public List<TsTreasuryDto> getChkcountyTreList() {
		return chkcountyTreList;
	}

	public void setChkcountyTreList(List<TsTreasuryDto> chkcountyTreList) {
		this.chkcountyTreList = chkcountyTreList;
	}

	public List<TsTreasuryDto> getCountyTreList() {
		return countyTreList;
	}

	public void setCountyTreList(List<TsTreasuryDto> countyTreList) {
		this.countyTreList = countyTreList;
	}

	public String getSrptdate1() {
		return srptdate1;
	}

	public void setSrptdate1(String srptdate1) {
		this.srptdate1 = srptdate1;
	}

	public String getStrecode1() {
		return strecode1;
	}

	public void setStrecode1(String strecode1) {
		this.strecode1 = strecode1;
	}

	public boolean isBcentral1() {
		return bcentral1;
	}

	public void setBcentral1(boolean bcentral1) {
		this.bcentral1 = bcentral1;
	}

	public boolean isBprovince1() {
		return bprovince1;
	}

	public void setBprovince1(boolean bprovince1) {
		this.bprovince1 = bprovince1;
	}

	public boolean isBcity1() {
		return bcity1;
	}

	public void setBcity1(boolean bcity1) {
		this.bcity1 = bcity1;
	}

	public boolean isBisselect1() {
		return bisselect1;
	}

	public void setBisselect1(boolean bisselect1) {
		this.bisselect1 = bisselect1;
	}

	public boolean isBisselect2() {
		return bisselect2;
	}

	public void setBisselect2(boolean bisselect2) {
		this.bisselect2 = bisselect2;
	}

	public String getSrptdate2() {
		return srptdate2;
	}

	public void setSrptdate2(String srptdate2) {
		this.srptdate2 = srptdate2;
	}

	public String getStrecode2() {
		return strecode2;
	}

	public void setStrecode2(String strecode2) {
		this.strecode2 = strecode2;
	}

	public boolean isBcentral2() {
		return bcentral2;
	}

	public void setBcentral2(boolean bcentral2) {
		this.bcentral2 = bcentral2;
	}

	public boolean isBprovince2() {
		return bprovince2;
	}

	public void setBprovince2(boolean bprovince2) {
		this.bprovince2 = bprovince2;
	}

	public boolean isBcity2() {
		return bcity2;
	}

	public void setBcity2(boolean bcity2) {
		this.bcity2 = bcity2;
	}

	public boolean isBisselect3() {
		return bisselect3;
	}

	public void setBisselect3(boolean bisselect3) {
		this.bisselect3 = bisselect3;
	}

	public String getSrptdate3() {
		return srptdate3;
	}

	public void setSrptdate3(String srptdate3) {
		this.srptdate3 = srptdate3;
	}

	public String getStrecode3() {
		return strecode3;
	}

	public void setStrecode3(String strecode3) {
		this.strecode3 = strecode3;
	}

	public boolean isBcentral3() {
		return bcentral3;
	}

	public void setBcentral3(boolean bcentral3) {
		this.bcentral3 = bcentral3;
	}

	public boolean isBprovince3() {
		return bprovince3;
	}

	public void setBprovince3(boolean bprovince3) {
		this.bprovince3 = bprovince3;
	}

	public boolean isBcity3() {
		return bcity3;
	}

	public void setBcity3(boolean bcity3) {
		this.bcity3 = bcity3;
	}

	public boolean isBisselect4() {
		return bisselect4;
	}

	public void setBisselect4(boolean bisselect4) {
		this.bisselect4 = bisselect4;
	}

	public String getSrptdate4() {
		return srptdate4;
	}

	public void setSrptdate4(String srptdate4) {
		this.srptdate4 = srptdate4;
	}

	public String getStrecode4() {
		return strecode4;
	}

	public void setStrecode4(String strecode4) {
		this.strecode4 = strecode4;
	}

	public boolean isBcentral4() {
		return bcentral4;
	}

	public void setBcentral4(boolean bcentral4) {
		this.bcentral4 = bcentral4;
	}

	public boolean isBprovince4() {
		return bprovince4;
	}

	public void setBprovince4(boolean bprovince4) {
		this.bprovince4 = bprovince4;
	}

	public boolean isBcity4() {
		return bcity4;
	}

	public void setBcity4(boolean bcity4) {
		this.bcity4 = bcity4;
	}

	public boolean isBisselect5() {
		return bisselect5;
	}

	public void setBisselect5(boolean bisselect5) {
		this.bisselect5 = bisselect5;
	}

	public String getSrptdate5() {
		return srptdate5;
	}

	public void setSrptdate5(String srptdate5) {
		this.srptdate5 = srptdate5;
	}

	public String getStrecode5() {
		return strecode5;
	}

	public void setStrecode5(String strecode5) {
		this.strecode5 = strecode5;
	}

	public boolean isBcentral5() {
		return bcentral5;
	}

	public void setBcentral5(boolean bcentral5) {
		this.bcentral5 = bcentral5;
	}

	public boolean isBprovince5() {
		return bprovince5;
	}

	public void setBprovince5(boolean bprovince5) {
		this.bprovince5 = bprovince5;
	}

	public boolean isBcity5() {
		return bcity5;
	}

	public void setBcity5(boolean bcity5) {
		this.bcity5 = bcity5;
	}

	public boolean isBcountry1() {
		return bcountry1;
	}

	public void setBcountry1(boolean bcountry1) {
		this.bcountry1 = bcountry1;
	}

	public boolean isBcountry2() {
		return bcountry2;
	}

	public void setBcountry2(boolean bcountry2) {
		this.bcountry2 = bcountry2;
	}

	public boolean isBcountry3() {
		return bcountry3;
	}

	public void setBcountry3(boolean bcountry3) {
		this.bcountry3 = bcountry3;
	}

	public boolean isBcountry4() {
		return bcountry4;
	}

	public void setBcountry4(boolean bcountry4) {
		this.bcountry4 = bcountry4;
	}

	public boolean isBcountry5() {
		return bcountry5;
	}

	public void setBcountry5(boolean bcountry5) {
		this.bcountry5 = bcountry5;
	}

	public boolean isBisEndMonth() {
		return bisEndMonth;
	}

	public void setBisEndMonth(boolean bisEndMonth) {
		this.bisEndMonth = bisEndMonth;
	}

	public Date getAgentEndDate() {
		return agentEndDate;
	}

	public void setAgentEndDate(Date agentEndDate) {
		this.agentEndDate = agentEndDate;
	}
}
