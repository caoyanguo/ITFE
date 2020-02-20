package com.cfcc.itfe.client.dataquery.organdmoneydetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.deptone.common.core.config.SystemConfig;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.persistence.dto.OrgAndMoneyDetail;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-05-07 21:48:04 ��ϵͳ: DataQuery ģ��:orgAndMoneyDetail
 *       ���:OrgAndMoneyDetail
 */
public class OrgAndMoneyDetailBean extends AbstractOrgAndMoneyDetailBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(OrgAndMoneyDetailBean.class);
	private String reportpath = "com/cfcc/itfe/client/ireport/OrgAndMoneyDetailReport.jasper";
	private List reportRs = null;
	private Map reportmap = new HashMap();
	private String flag;
	private int moneyposition = 0;
	private String path = "C:\\Ԥ�����뱨��⸶";
	private List<OrgAndMoneyDetail> filelist = new ArrayList<OrgAndMoneyDetail>();

	public OrgAndMoneyDetailBean() {
		super();
		filepath = new ArrayList<File>();

	}

	/**
	 * Direction: ʡ������ϸ ename: provinceDetail ���÷���: viewers: ������ϸ����ҳ messages:
	 */
	public String provinceDetail(Object o) {
		flag = "10";
		moneyposition=2;
		choseFile();
		filelist = getFileList(filepath, flag);
		reportRs = filelist;
		return super.provinceDetail(o);
	}
	
	/**
	 * Direction: �к�����ϸ ename: cityDetail ���÷���: viewers: ������ϸ����ҳ messages:
	 */
	public String cityDetail(Object o) {
		flag = "11";
		moneyposition = 2;
		choseFile();
		filelist = getFileList(filepath, flag);
		reportRs = filelist;
		return super.cityDetail(o);
	}

	/**
	 * Direction: ���غ�����ϸ ename: townDetail ���÷���: viewers: ������ϸ����ҳ messages:
	 */
	public String townDetail(Object o) {
		flag = "63";
		moneyposition=1;
		choseFile();
		filelist = getFileList(filepath, flag);
		reportRs = filelist;
		return super.townDetail(o);
	}

	/**
	 * ��ȡ�ļ��б�
	 * 
	 * @param filepath
	 * @param flag
	 * @return
	 */
	/**
	 * @param filepath
	 * @param flag
	 * @return
	 */
	private List<OrgAndMoneyDetail> getFileList(List filepath, String flag) {
		try {
			filelist = new ArrayList<OrgAndMoneyDetail>();
			int filenum = 0;
			String pathflag="";

			TsTreasuryDto orgdto = new TsTreasuryDto();

			String name = "";
			// ���ݲ鿴������ӱ������
			if (flag.equals("11")) {
				name = "�Ϻ���ʡ��Ԥ�����뱨�������嵥";
				orgdto.setStreattrib("2");
				pathflag = "\\ʡ";
			} else if (flag.equals("10")) {
				name = "�Ϻ������뼶Ԥ�����뱨�������嵥";
				orgdto.setStreattrib("2");
				pathflag = "\\����";
			} else {
				name = "�Ϻ������ؼ�Ԥ�����뱨�������嵥";
				orgdto.setStrelevel("4");
				pathflag = "\\����";
			}
			boolean flags = false;
			// ��ѯ�̶����͵Ĺ�����Ϣ
			List<TsTreasuryDto> listOrg = commonDataAccessService
					.findRsByDto(orgdto);
			// ��ȡ�׸��ļ�����
			String times = ((File) filepath.get(0)).getName().substring(0, 6);
			// �����ļ�
			for (int j = 0; j < filepath.size(); j++) {
				// ��ȡ��ȡ���ļ�������
				String timese = ((File) filepath.get(j)).getName().substring(0,
						6);
				if (j > 0 && timese.equals(times)) {
					continue;
				} else {
					times = timese;
				}
				// ��������
				for (int i = 0; i < listOrg.size(); i++) {
					// �����ļ�
					for (int k = 0; k < filepath.size(); k++) {
						// ��ȡ�ļ�
						File file = (File) filepath.get(k);
						String timeses = file.getName().substring(0, 6);
						// �ļ���������������Ĺ�����Ϣ��Ӧ������һ���ļ�ͬ���ڵ������
						if (file.getName().substring(6, 16).equals(
								listOrg.get(i).getStrecode())
								&& timeses.equals(times)) {

							BufferedReader reader = new BufferedReader(
									new FileReader(file));
							String tempString = reader.readLine();
							String[] arr = tempString.split(",");
							String orgcode = file.getName().substring(10, 12);

							OrgAndMoneyDetail dto = new OrgAndMoneyDetail();
							dto.setFilename(file.getName());
							dto.setMoney(BigDecimal.valueOf(Double
									.valueOf(arr[moneyposition])));
							dto
									.setOrgname(orgcode
											+ listOrg.get(i).getStrename()
													.substring(7));

							dto.setDates(times);

							filelist.add(dto);
							filenum++;

							flags = true;
							break;
						} else {
							flags = false;
						}
					}
					// ����ļ���δ��Ӧ�Ĺ�����Ϣ
					if (!flags) {
						OrgAndMoneyDetail dto = new OrgAndMoneyDetail();
						dto.setOrgname(listOrg.get(i).getStrecode().substring(
								4, 6)
								+ listOrg.get(i).getStrename().substring(7));
						dto.setDates(times);
						filelist.add(dto);
					}
				}

			}

			// �����ļ���ָ���ļ���
			
			for (int j = 0; j < filepath.size(); j++) {
				File file = (File) filepath.get(j);
				String timeses = file.getName().substring(0, 6);
				String trecode = file.getName().substring(10, 12);
				String paths = path + "\\" + timeses + "\\" + pathflag;
				File files = new File(paths);
				if (!files.exists()) {
					files.mkdirs();
				}
				
				
				String pathss = "";
				if (Integer.parseInt(trecode) < 12) {
					pathss = paths + "\\" + "���д��ʽ𱨱�";
				} else if (Integer.parseInt(trecode) < 22) {
					pathss = paths + "\\" + "ũ�д��ʽ𱨱�";
				} else {
					pathss = paths + "\\" + "�����ʽ𱨱�";
				}
				File copyfiles = new File(pathss);
				if(!copyfiles.exists()){
					copyfiles.mkdirs();
				}
				FileUtil.getInstance().copyFile(file.getPath(), pathss + "\\" + file.getName());
			}

			reportmap.put("dates", times.substring(0, 2) + "��"
					+ times.substring(2, 4) + "��" + times.substring(4) + "��");
			reportmap.put("title", name);
			reportmap.put("filenum", filenum);
			reportmap.put("printDate", new SimpleDateFormat("yyyy��MM��dd��")
					.format(new java.util.Date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filelist;
	}
	
	/**
	 * ɸѡ�ļ�
	 * @param filepath
	 * @param flag
	 */
	private void choseFile() {
		
		if(flag.equals("63")){//����
			for(int i=0;i<filepath.size();i++){
				File file = (File) filepath.get(i);
				String[] filename = file.getName().split(",");
				if(filename[1].trim().equals("01004.pas")){
					filepath.remove(i);
					i--;
				}
			}
		}else if(flag.equals("10")){//����
			for(int i=0;i<filepath.size();i++){
				File file = (File) filepath.get(i);
				String[] filename = file.getName().split(",");
				if(filename[1].trim().equals("00504.pas")){
					filepath.remove(i);
					i--;
					continue;
				}
				if(filename[0].substring(16, 18).equals("11")){
					filepath.remove(i);
					i--;
					continue;
				}
			}
		}else if(flag.equals("11")){//ʡ
			for(int i=0;i<filepath.size();i++){
				File file = (File) filepath.get(i);
				String[] filename = file.getName().split(",");
				if(filename[1].trim().equals("00504.pas")){
					filepath.remove(i);
					i--;
					continue;
				}
				if(filename[0].substring(16, 18).equals("10")){
					filepath.remove(i);
					i--;
					continue;
				}
			}
		}
	}
	
	/**
	 * Direction: ���� ename: reback ���÷���: viewers: �ļ�������� messages:
	 */
	public String reback(Object o) {
		return super.reback(o);
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