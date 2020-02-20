package com.cfcc.itfe.client.dataquery.incomedataquery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 09-11-06 18:18:43 ��ϵͳ: DataQuery ģ��:incomeDataQuery ���:IncomeBillQuery
 */
public class IncomeBillQueryBean extends AbstractIncomeBillQueryBean implements IPageDataProvider {

	private ITFELoginInfo loginInfo = null; // ��½��Ϣ

	private TvInfileDto finddto = null; // ��ѯ����DTO����

	private TvInfileDto operdto = null; // ����DTO����

	private PagingContext incometablepage; // ��ҳ�ؼ�
	
	private List<TdEnumvalueDto> typelist = new ArrayList<TdEnumvalueDto>(); //ö�ٲ�ѯ����
	private List<IDto> checklist = null;
	private String ifdetail ; //��ѯ����
	
	private TvInfileDetailDto paramDetail = new TvInfileDetailDto();
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	public IncomeBillQueryBean() {
		super();
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();

		incometablepage = new PagingContext(this);
		finddto = new TvInfileDto();
		operdto = new TvInfileDto();
		finddto.setScommitdate(TimeFacade.getCurrentStringTime());
		checklist = new ArrayList<IDto>();
		init();
		ifdetail = "0";
		selectedIncomeList=new ArrayList<TvInfileDto>();
	}

	public void init() {
		TdEnumvalueDto value1 = new TdEnumvalueDto();
		value1.setStypecode("0");
		value1.setSvaluecmt("����˰Ʊ��Ϣ");
		
		TdEnumvalueDto value2 = new TdEnumvalueDto();
		value2.setStypecode("1");
		value2.setSvaluecmt("��ϸ˰Ʊ��Ϣ");
		
		typelist.add(value1);
		typelist.add(value2);
		
	}
	
	public TvInfileDetailDto createDetailInfo() {
		TvInfileDetailDto detail = new TvInfileDetailDto();
		detail.setSrecvtrecode(finddto.getSrecvtrecode());
		detail.setScommitdate(finddto.getScommitdate());
		detail.setNmoney(finddto.getNmoney());
		detail.setSfilename(finddto.getSfilename());
		detail.setSpackageno(finddto.getSpackageno());
		detail.setSdealno(finddto.getSdealno());
		detail.setStbstaxorgcode(finddto.getStbstaxorgcode());
		detail.setStaxorgcode(finddto.getStaxorgcode());
		detail.setStbsassitsign(finddto.getStbsassitsign());
		detail.setSassitsign(finddto.getSassitsign());
		detail.setSbudgettype(finddto.getSbudgettype());
		detail.setSbudgetlevelcode(finddto.getSbudgetlevelcode());
		detail.setSbudgetsubcode(finddto.getSbudgetsubcode());
		detail.setSpaybookkind(finddto.getSpaybookkind());
		detail.setStrasrlno(finddto.getStrasrlno());
		return detail;
	}
	
	/**
	 * Direction: ��ѯ�б��¼� ename: searchList ���÷���: viewers: ����˰Ʊ�б���� messages:
	 */
	public String searchList(Object o) {		
		if(null == ifdetail || "".equals(ifdetail)) {
			ifdetail = "0";
		}
		if("1".equals(ifdetail)) {  //�鿴��ϸ��Ϣ
			this.setParamDetail(createDetailInfo());
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		incometablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.rebackSearch(o);
		}

		editor.fireModelChanged();

		return super.searchList(o);

	}
	
	/**
	 * Direction: ����˰Ʊ�¼� ename: exportBillData ���÷���: viewers: * messages:
	 */
	public String exportBillData(Object o) {

		if (null == operdto || null == operdto.getSpackageno() || "".equals(operdto.getSpackageno().trim())) {
			MessageDialog.openMessageDialog(null, "��������ǰ������ѡ�����ˮ�ţ�");
			return super.exportBillData(o);
		}

		// ȡ�õ�ǰϵͳʱ��
		String date = TimeFacade.getCurrentStringTime();

		try {
			String exportStr = incomeBillService.exportIncomeData(ifdetail, operdto);
			if (null == exportStr) {
				MessageDialog.openMessageDialog(null, "û���ҵ�Ҫ�����ļ�¼��");
			} else {
				// ѡ�񱣴�·��
				DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
						.getActiveShell());
				String filePath = path.open();
				List<String> filelist = new ArrayList<String>();
				if ((null == filePath) || (filePath.length() == 0)) {
					MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
					return "";
				}
				/*String clientFilePath;
				String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
				String dirsep = File.separator;
				String serverFilePath;*/
				
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				/*String fileName = "c:" + dirsep + "client" + dirsep + "income" + dirsep + date + dirsep + "tips" + loginInfo.getSorgcode()
						+ operdto.getSpackageno() + "00.txt" + dirsep;*/
				String fileName = "tips" + loginInfo.getSorgcode() + operdto.getSdealno() + "00.txt";
				String fullName = filePath + dirsep + fileName;
				File file = new File(fullName);
				File dir = new File(file.getParent());

				FileOutputStream output = null;
				try {
					if (!dir.exists()) {
						dir.mkdirs();
					}
					output = new FileOutputStream(fullName, false);
					output.write(exportStr.getBytes("GBK"));

					MessageDialog.openMessageDialog(null, "������¼�ɹ�:��¼���·��[" + fullName + "]");
				} catch (IOException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} catch (RuntimeException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (Exception ex) {
							return super.exportBillData(o);
						}
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.exportBillData(o);
	}
	
	
	/**
	 * Direction: ������ѯ��������˰Ʊ�¼� ename: exportAllBillData ���÷���: viewers: * messages:
	 */
	public String exportAllBillData(Object o) {
		List list=new ArrayList();
		try {
			if("1".equals(ifdetail)) {
				list =  incomeBillService.findIncomeByDto(this.getParamDetail(),ifdetail);
			}else if("0".equals(ifdetail)){
				list =  incomeBillService.findIncomeByDto(finddto,ifdetail);
			}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, e.getMessage());
			return "";
		}

		// ȡ�õ�ǰϵͳʱ��
		String date = TimeFacade.getCurrentStringTime();

		try {
			
			String exportStr = incomeBillService.exportAllIncomeData(list,ifdetail);
			if (null == exportStr||"".equals(exportStr)) {
				MessageDialog.openMessageDialog(null, "û���ҵ�Ҫ�����ļ�¼��");
				return "";
			} else {
				// ѡ�񱣴�·��
				DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
						.getActiveShell());
				String filePath = path.open();
				List<String> filelist = new ArrayList<String>();
				if ((null == filePath) || (filePath.length() == 0)) {
					MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
					return "";
				}
				
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String fileName="";
				Random random = new Random();
				int count = random.nextInt(100000000);
				fileName = "tips" + loginInfo.getSorgcode() + count+ "00.txt";
				String fullName = filePath + dirsep + fileName;
				File file = new File(fullName);
				File dir = new File(file.getParent());

				FileOutputStream output = null;
				try {
					if (!dir.exists()) {
						dir.mkdirs();
					}
					output = new FileOutputStream(fullName, false);
					output.write(exportStr.getBytes("GBK"));

					MessageDialog.openMessageDialog(null, "������¼�ɹ�:��¼���·��[" + fullName + "]");
				} catch (IOException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} catch (RuntimeException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (Exception ex) {
							return super.exportBillData(o);
						}
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.exportAllBillData(o);
	}

	/**
	 * Direction: ������ѯ����ѡ��˰Ʊ�¼� ename: exportSelectedBillData ���÷���: viewers: * messages:
	 */
	public String exportSelectedBillData(Object o) {
		if (null == selectedIncomeList || selectedIncomeList.size()<=0) {
			MessageDialog.openMessageDialog(null, "��������ǰ������ѡ����Ҫ�����ļ�¼��");
			return super.exportBillData(o);
		}

		// ȡ�õ�ǰϵͳʱ��
		String date = TimeFacade.getCurrentStringTime();

		try {
			String exportStr = incomeBillService.exportSelectedIncomeData(selectedIncomeList,ifdetail);
			if (null == exportStr||"".equals(exportStr)) {
				MessageDialog.openMessageDialog(null, "û���ҵ�Ҫ�����ļ�¼��");
				selectedIncomeList.clear();
				selectedIncomeList=new ArrayList<TvInfileDto>();
				this.editor.fireModelChanged();
				return "";
			} else {
				// ѡ�񱣴�·��
				DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
						.getActiveShell());
				String filePath = path.open();
				List<String> filelist = new ArrayList<String>();
				if ((null == filePath) || (filePath.length() == 0)) {
					MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
					return "";
				}
				
				String dirsep = File.separator; // ȡ��ϵͳ�ָ��
				String fileName="";
				if(selectedIncomeList.size()==1){
					String dealno="";
					if("1".equals(ifdetail)) {
						dealno =  ((TvInfileDetailDto)selectedIncomeList.get(0)).getSdealno();
					}else if("0".equals(ifdetail)){
						dealno =  ((TvInfileDto)selectedIncomeList.get(0)).getSdealno();
					}
					 fileName = "tips" + loginInfo.getSorgcode() + dealno + "00.txt";
				}else{
					Random random = new Random();
					int count = random.nextInt(100000000);
					fileName = "tips" + loginInfo.getSorgcode() + count+ "00.txt";
				}
				String fullName = filePath + dirsep + fileName;
				File file = new File(fullName);
				File dir = new File(file.getParent());

				FileOutputStream output = null;
				try {
					if (!dir.exists()) {
						dir.mkdirs();
					}
					output = new FileOutputStream(fullName, false);
					output.write(exportStr.getBytes("GBK"));

					MessageDialog.openMessageDialog(null, "������¼�ɹ�:��¼���·��[" + fullName + "]");
				} catch (IOException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} catch (RuntimeException e) {
					MessageDialog.openErrorDialog(null, e);
					return super.exportBillData(o);
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (Exception ex) {
							return super.exportBillData(o);
						}
					}
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		selectedIncomeList.clear();
		selectedIncomeList=new ArrayList<TvInfileDto>();
		this.editor.fireModelChanged();
		return super.exportSelectedBillData(o);
	}

	/**
	 * Direction: ���������¼� ename: reSendMsg ���÷���: viewers: * messages:
	 */
	public String reSendMsg(Object o) {

		if (null == operdto || null == operdto.getSpackageno() || "".equals(operdto.getSpackageno().trim())) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�ط����ĵļ�¼��");
			return super.reSendMsg(o);
		}

		try {
			incomeBillService.reSendMsg(operdto.getScommitdate(), operdto.getSpackageno(), operdto.getSorgcode(), operdto.getSfilename());
			MessageDialog.openMessageDialog(null, "�ط����ĳɹ���[����ˮ�ţ�" + operdto.getSpackageno() + "]");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.reSendMsg(o);
	}

	/**
	 * Direction: ���ز�ѯ���� ename: rebackSearch ���÷���: viewers: ����˰Ʊ��ѯ���� messages:
	 */
	public String rebackSearch(Object o) {
		return super.rebackSearch(o);
	}
	/**
	 * Direction: �����б�˫���¼�
	 * ename: doubleclickIncome
	 * ���÷���: 
	 * viewers: ����˰Ʊ�༭ҳ��
	 * messages: 
	 */
    public String doubleclickIncome(Object o){
    	if(o instanceof TvInfileDto) {
			operdto = (TvInfileDto) o;
		}else if(o instanceof TvInfileDetailDto) {
			operdto = new TvInfileDto();
			TvInfileDetailDto tmp = (TvInfileDetailDto)o;
			operdto.setSrecvtrecode(tmp.getSrecvtrecode());
			operdto.setScommitdate(tmp.getScommitdate());
			operdto.setNmoney(tmp.getNmoney());
			operdto.setSfilename(tmp.getSfilename());
			operdto.setSpackageno(tmp.getSpackageno());
			operdto.setSdealno(tmp.getSdealno());
			operdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
			operdto.setStaxorgcode(tmp.getStaxorgcode());
			operdto.setStbsassitsign(tmp.getStbsassitsign());
			operdto.setSassitsign(tmp.getSassitsign());
			operdto.setSbudgettype(tmp.getSbudgettype());
			operdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
			operdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
			operdto.setSpaybookkind(tmp.getSpaybookkind());
			operdto.setStrasrlno(tmp.getStrasrlno());
		}
        return super.doubleclickIncome(o);
    }
	/**
	 * Direction: ����˰Ʊ��ϸ�����¼� ename: singleclickIncome ���÷���: viewers: * messages:
	 */
	public String singleclickIncome(Object o) {
		if(o instanceof TvInfileDto) {
			operdto = (TvInfileDto) o;
		}else if(o instanceof TvInfileDetailDto) {
			operdto = new TvInfileDto();
			TvInfileDetailDto tmp = (TvInfileDetailDto)o;
			operdto.setSrecvtrecode(tmp.getSrecvtrecode());
			operdto.setScommitdate(tmp.getScommitdate());
			operdto.setNmoney(tmp.getNmoney());
			operdto.setSfilename(tmp.getSfilename());
			operdto.setSpackageno(tmp.getSpackageno());
			operdto.setSdealno(tmp.getSdealno());
			operdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
			operdto.setStaxorgcode(tmp.getStaxorgcode());
			operdto.setStbsassitsign(tmp.getStbsassitsign());
			operdto.setSassitsign(tmp.getSassitsign());
			operdto.setSbudgettype(tmp.getSbudgettype());
			operdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
			operdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
			operdto.setSpaybookkind(tmp.getSpaybookkind());
			operdto.setStrasrlno(tmp.getStrasrlno());
		}
		
		return super.singleclickIncome(o);
	}
	private IDto swipInfileOrInfileDetail(IDto dto) throws ITFEBizException
	{
		IDto returndto = null;
		if(dto instanceof TvInfileDto)
		{
			TvInfileDetailDto datadto = new TvInfileDetailDto();
			TvInfileDto temp = (TvInfileDto)dto;
			datadto.setSdealno(temp.getSdealno());
			List<TvInfileDetailDto> list = commonDataAccessService.findRsByDto(datadto);
			if(list!=null&&list.size()==1)
				datadto = list.get(0);
			else
				return null;
			datadto.setSrecvtrecode(temp.getSrecvtrecode());
			datadto.setScommitdate(temp.getScommitdate());
			datadto.setNmoney(temp.getNmoney());
			datadto.setSfilename(temp.getSfilename());
			datadto.setSpackageno(temp.getSpackageno());
			datadto.setSdealno(temp.getSdealno());
			datadto.setStbstaxorgcode(temp.getStbstaxorgcode());
			datadto.setStaxorgcode(temp.getStaxorgcode());
			datadto.setStbsassitsign(temp.getStbsassitsign());
			datadto.setSassitsign(temp.getSassitsign());
			datadto.setSbudgettype(temp.getSbudgettype());
			datadto.setSbudgetlevelcode(temp.getSbudgetlevelcode());
			datadto.setSbudgetsubcode(temp.getSbudgetsubcode());
			datadto.setSpaybookkind(temp.getSpaybookkind());
			datadto.setStrasrlno(temp.getStrasrlno());
			returndto = datadto;
		}else if(dto instanceof TvInfileDetailDto)
		{
			TvInfileDto datadto = new TvInfileDto();
			TvInfileDetailDto temp = (TvInfileDetailDto)dto;
			datadto.setSdealno(temp.getSdealno());
			List<TvInfileDto> list = commonDataAccessService.findRsByDto(datadto);
			if(list!=null&&list.size()==1)
				datadto = list.get(0);
			else
				return null;
			datadto.setSrecvtrecode(temp.getSrecvtrecode());
			datadto.setScommitdate(temp.getScommitdate());
			datadto.setNmoney(temp.getNmoney());
			datadto.setSfilename(temp.getSfilename());
			datadto.setSpackageno(temp.getSpackageno());
			datadto.setSdealno(temp.getSdealno());
			datadto.setStbstaxorgcode(temp.getStbstaxorgcode());
			datadto.setStaxorgcode(temp.getStaxorgcode());
			datadto.setStbsassitsign(temp.getStbsassitsign());
			datadto.setSassitsign(temp.getSassitsign());
			datadto.setSbudgettype(temp.getSbudgettype());
			datadto.setSbudgetlevelcode(temp.getSbudgetlevelcode());
			datadto.setSbudgetsubcode(temp.getSbudgetsubcode());
			datadto.setSpaybookkind(temp.getSpaybookkind());
			datadto.setStrasrlno(temp.getStrasrlno());
			returndto = datadto;
		}
		return returndto;
	}
    /**
	 * Direction: ��������
	 * ename: savedata
	 * ���÷���: 
	 * viewers: ����˰Ʊ�б����
	 * messages: 
	 */
    public String savedata(Object o){
    	if(operdto!=null&&operdto.getSrecvtrecode()!=null&&!"".equals(operdto.getSrecvtrecode()))
        {
        	try
        	{
        		IDto swipdto = this.swipInfileOrInfileDetail(operdto);
        		if(swipdto!=null)
        		{
        			commonDataAccessService.updateData(operdto);
        			commonDataAccessService.updateData(swipdto);
        			MessageDialog.openMessageDialog(null, "�������ݳɹ�!");
        		}
        		
        	} catch (Throwable e) {
    			MessageDialog.openMessageDialog(null, "��������ʧ�ܣ�"+e.toString());
    			return "";
    		}
        	operdto = new TvInfileDto();
        }
        return super.savedata(o);
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			PageResponse respone = null ;
			if("1".equals(ifdetail)) {
				respone =  incomeBillService.findIncomeByPage(this.getParamDetail(), pageRequest);
			}else if("0".equals(ifdetail)){
				respone =  incomeBillService.findIncomeByPage(finddto, pageRequest);
			}
			return respone;
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.retrieve(pageRequest);
	}
	

	/**
	 * Direction: ȫѡ/��ѡ ename: selectAllOrNone ���÷���: viewers: * messages:
	 */
	@SuppressWarnings("unchecked")
	public String selectAllOrNone(Object o) {
		if (this.incometablepage == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.incometablepage.getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvInfileDto> templist = page.getData();
		if (templist != null && this.selectedIncomeList != null) {
			if (selectedIncomeList.size() != 0 && selectedIncomeList.containsAll(templist)) {
				selectedIncomeList.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (selectedIncomeList.contains(templist.get(i))) {
						selectedIncomeList.set(selectedIncomeList.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						selectedIncomeList.add(i, templist.get(i));
					}
				}
			}
		}
		this.editor.fireModelChanged();
		return super.selectAllOrNone(o);
	}

	public TvInfileDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvInfileDto finddto) {
		this.finddto = finddto;
	}

	public PagingContext getIncometablepage() {
		return incometablepage;
	}

	public void setIncometablepage(PagingContext incometablepage) {
		this.incometablepage = incometablepage;
	}

	public TvInfileDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvInfileDto operdto) {
		this.operdto = operdto;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public List<TdEnumvalueDto> getTypelist() {
		return typelist;
	}

	public void setTypelist(List<TdEnumvalueDto> typelist) {
		this.typelist = typelist;
	}

	public String getIfdetail() {
		return ifdetail;
	}

	public void setIfdetail(String ifdetail) {
		this.ifdetail = ifdetail;
	}

	public TvInfileDetailDto getParamDetail() {
		return paramDetail;
	}

	public void setParamDetail(TvInfileDetailDto paramDetail) {
		this.paramDetail = paramDetail;
	}

}