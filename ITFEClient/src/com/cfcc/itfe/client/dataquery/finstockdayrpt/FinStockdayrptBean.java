/**
 * ����ձ����ѯ
 */
package com.cfcc.itfe.client.dataquery.finstockdayrpt;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ReportExcel;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:����ձ����ѯ
 * 
 * @author wangtuo
 * @time 10-06-02 15:02:38 ��ϵͳ: DataQuery ģ��:TrStockdayrpt ���:TrStockdayrpt
 */
public class FinStockdayrptBean extends AbstractFinStockdayrptBean implements
		IPageDataProvider {

	private List trelist;
	private static Log log = LogFactory.getLog(FinStockdayrptBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	List<TsTreasuryDto> finddtolist=null;
	
	private Boolean flag=true; 
	List list=new ArrayList();
	List templist=new ArrayList();
	public FinStockdayrptBean() {
		super();
		dto = new TrStockdayrptDto();
		dto.setSrptdate(TimeFacade.getCurrentStringTime());		
		pagingcontext = new PagingContext(this);
		
		TsTreasuryDto tredto = new TsTreasuryDto();
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			tredto.setSorgcode(loginfo.getSorgcode());
		} else {
			tredto.setStreattrib(StateConstant.COMMON_YES);
		}
		try {
			trelist = commonDataAccessService.getSubTreCode(loginfo
					.getSorgcode());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		dto.setStrecode("");
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: ����ձ����ѯ��� messages:
	 */
	public String query(Object o) {

		// ����ҳ��
		String returnpage = null;
		
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if(null==dto.getSrptdate()||dto.getSrptdate().equals("")){
			MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
			return "";
		}
		
		
		TsTreasuryDto finddto=new TsTreasuryDto();
		if (!loginfo.getSorgcode().equals(centerorg) ) {
			
			//�жϵ�ǰ��¼��ѯ������롢���������Ƿ��ں���������
			finddto.setSorgcode(loginfo.getSorgcode()); //�����������
			if(dto.getSorgcode()!=null && !"".equals(dto.getSorgcode())){
				finddto.setSpayunitname(dto.getSorgcode()); //��������
			}
			if(dto.getStrecode()!=null && !"".equals(dto.getStrecode())){
				finddto.setStrecode(dto.getStrecode()); //�����������
			}
		}
		try {
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,"1");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if(null==finddtolist || finddtolist.size() == 0){
			MessageDialog.openMessageDialog(null, " Ȩ�޲��㣡����д��ȷ�Ĳ���������߹�����룡");
			return null;
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			returnpage = "����ձ����ѯ����";
		} else {
			returnpage = super.query(o);
		}
		return returnpage;
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ����ձ����ѯ���� messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}
	
	
	/**
	 * Direction: �������txt
	 * ename: exporttxt
	 * ���÷���: 
	 * viewers: ����ձ����ѯ����
	 * messages: 
	 */
    public String exporttxt(Object o){
    	
    	//��ѯ��dto
    	TrStockdayrptDto fdto = new TrStockdayrptDto();
    	HtrStockdayrptDto hfdto = new HtrStockdayrptDto();
    	
    	String dirsep = File.separator; // ȡ��ϵͳ�ָ��
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
    	
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		TsTreasuryDto finddto=new TsTreasuryDto();
		//�����Ļ���
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//�жϵ�ǰ��¼��ѯ������롢���������Ƿ��ڹ���������
//			finddto.setSbookorgcode(loginfo.getSorgcode()); //�����������
		}
		
		try {
			finddtolist = commonDataAccessService.findRsByDto(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if(null==finddtolist || finddtolist.size() == 0){
			MessageDialog.openMessageDialog(null, " Ȩ�޲��㣡����д��ȷ�Ĳ���������߹�����룡");
			return null;
		}
		
		String rtmsg ="";
		
		try {
			TrStockdayrptDto trstockdayrptdto=new TrStockdayrptDto();
			trstockdayrptdto.setSrptdate(dto.getSrptdate());
			templist=commonDataAccessService.selHtableBydto(trstockdayrptdto);
			for(TsTreasuryDto idto :finddtolist){
				List list=null;
				String strdate=(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis()));
				String copyFilename=idto.getStrecode()+"_"+dto.getSrptdate()+"_"+idto.getStrename()+"��������ձ�_"+strdate+".txt";
				
				//�������
				fdto.setStrecode(idto.getStrecode());
				//��������
				fdto.setSrptdate(dto.getSrptdate());
				
				//�������
				hfdto.setStrecode(idto.getStrecode());
				//��������
				hfdto.setSrptdate(dto.getSrptdate());

				if(null!=templist&&templist.size()>0){
					list =commonDataAccessService.findRsByDto(fdto);
				}
				else{
					list =commonDataAccessService.findRsByDto(hfdto);
				}
				
				if (list == null || list.size() == 0) {
					MessageDialog.openMessageDialog(null, idto.getStrecode()+","+idto.getStrename()+" ��ѯ�޼�¼��");
					continue;
				} 
				
				/**
				 * �ļ�����
				 */
				filecreat(filepath+copyFilename,list,dto.getSrptdate(),strdate,idto);
				rtmsg +=filepath+copyFilename+"; \r\n";
			}
		
		} catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		
		MessageDialog.openMessageDialog(null, " �ļ������ɣ��ѱ��浽��" + rtmsg);
		if(flag) editor.fireModelChanged();
		
        return super.exporttxt(o);
    }
    
    private void filecreat(String filepathname,List list,String srptdate,String strdate,TsTreasuryDto idto) throws FileOperateException{
    	//�����ļ�
    	File file = new File(filepathname);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		StringBuffer sb = new StringBuffer("");
		sb.append("                                  "+idto.getStrename()+"��������ձ� \r\n");
		sb.append("                                  "+"�������ڣ�"+dto.getSrptdate().substring(0, 4) + "��"+ dto.getSrptdate().substring(4, 6) + "��"+ dto.getSrptdate().substring(6, 8) + "��  \r\n");
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
		if(null!=templist&&templist.size()>0){
			List<TrStockdayrptDto> dlist=(List<TrStockdayrptDto>)list;
			
			int maxSaccno=0;
			int maxSaccname=0;
			int maxNmoneyyesterday=0;
			int maxNmoneyin=0;
			int maxNmoneyout=0;
			int maxNmoneytoday=0;
			
			for(TrStockdayrptDto trdto : dlist){
				if(trdto.getSaccno().trim().getBytes().length>maxSaccno){
					maxSaccno=trdto.getSaccno().trim().getBytes().length;
				}
				if(trdto.getSaccname().trim().getBytes().length>maxSaccname){
					maxSaccname=trdto.getSaccname().trim().getBytes().length;
				}
				if(trdto.getNmoneyyesterday().toString().getBytes().length>maxNmoneyyesterday){
					maxNmoneyyesterday=trdto.getNmoneyyesterday().toString().getBytes().length;
				}
				if(trdto.getNmoneyin().toString().getBytes().length>maxNmoneyin){
					maxNmoneyin=trdto.getNmoneyin().toString().getBytes().length;
				}
				if(trdto.getNmoneyout().toString().getBytes().length>maxNmoneyout){
					maxNmoneyout=trdto.getNmoneyout().toString().getBytes().length;
				}
				if(trdto.getNmoneytoday().toString().getBytes().length>maxNmoneytoday){
					maxNmoneytoday=trdto.getNmoneytoday().toString().getBytes().length;
				}
			}
			maxSaccno=maxSaccno+10;
			maxSaccname=maxSaccname+10;
			maxNmoneyyesterday=maxNmoneyyesterday+10;
			maxNmoneyin=maxNmoneyin+10;
			maxNmoneyout=maxNmoneyout+10;
			maxNmoneytoday=maxNmoneytoday+10;
			
			sb.append("�˺�/��Ŀ"+getspaceforsum(maxSaccno-"�˺�/��Ŀ".getBytes().length));
			sb.append("�ʻ�����"+getspaceforsum(maxSaccname-"�ʻ�����".getBytes().length));
			sb.append("������� "+getspaceforsum(maxNmoneyyesterday-"������� ".getBytes().length));
			sb.append("���շ�����跽"+getspaceforsum(maxNmoneyin-"���շ�����跽".getBytes().length));
			sb.append("���շ��������"+getspaceforsum(maxNmoneyout-"���շ��������".getBytes().length));
			sb.append("�������"+getspaceforsum(maxNmoneyyesterday-"������� ".getBytes().length));
			sb.append("  \r\n");
			
			for(TrStockdayrptDto trdto : dlist){
				sb.append(trdto.getSaccno()+getspaceforsum(maxSaccno-trdto.getSaccno().trim().getBytes().length)); //�˺�/��Ŀ
				sb.append(trdto.getSaccname()+getspaceforsum(maxSaccname-trdto.getSaccname().trim().getBytes().length)); //�ʻ�����
				sb.append(trdto.getNmoneyyesterday().toEngineeringString()+getspaceforsum(maxNmoneyyesterday-trdto.getNmoneyyesterday().toString().getBytes().length)); //�������
				sb.append(trdto.getNmoneyin().toPlainString()+getspaceforsum(maxNmoneyin-trdto.getNmoneyin().toString().getBytes().length)); //���շ�����跽
				sb.append(trdto.getNmoneyout().toPlainString()+getspaceforsum(maxNmoneyout-trdto.getNmoneyout().toString().getBytes().length)); //���շ��������
				sb.append(trdto.getNmoneytoday().toPlainString()); //�������
				sb.append("  \r\n");
			}
		}
		else{
			List<HtrStockdayrptDto> hlist=(List<HtrStockdayrptDto>)list;
			int maxSaccno=0;
			int maxSaccname=0;
			int maxNmoneyyesterday=0;
			int maxNmoneyin=0;
			int maxNmoneyout=0;
			int maxNmoneytoday=0;
			
			for(HtrStockdayrptDto trdto : hlist){
				if(trdto.getSaccno().trim().getBytes().length>maxSaccno){
					maxSaccno=trdto.getSaccno().trim().getBytes().length;
				}
				if(trdto.getSaccname().trim().getBytes().length>maxSaccname){
					maxSaccname=trdto.getSaccname().trim().getBytes().length;
				}
				if(trdto.getNmoneyyesterday().toString().getBytes().length>maxNmoneyyesterday){
					maxNmoneyyesterday=trdto.getNmoneyyesterday().toString().getBytes().length;
				}
				if(trdto.getNmoneyin().toString().getBytes().length>maxNmoneyin){
					maxNmoneyin=trdto.getNmoneyin().toString().getBytes().length;
				}
				if(trdto.getNmoneyout().toString().getBytes().length>maxNmoneyout){
					maxNmoneyout=trdto.getNmoneyout().toString().getBytes().length;
				}
				if(trdto.getNmoneytoday().toString().getBytes().length>maxNmoneytoday){
					maxNmoneytoday=trdto.getNmoneytoday().toString().getBytes().length;
				}
			}
			maxSaccno=maxSaccno+10;
			maxSaccname=maxSaccname+10;
			maxNmoneyyesterday=maxNmoneyyesterday+10;
			maxNmoneyin=maxNmoneyin+10;
			maxNmoneyout=maxNmoneyout+10;
			maxNmoneytoday=maxNmoneytoday+10;
			
			sb.append("�˺�/��Ŀ"+getspaceforsum(maxSaccno-"�˺�/��Ŀ".getBytes().length));
			sb.append("�ʻ�����"+getspaceforsum(maxSaccname-"�ʻ�����".getBytes().length));
			sb.append("������� "+getspaceforsum(maxNmoneyyesterday-"������� ".getBytes().length));
			sb.append("���շ�����跽"+getspaceforsum(maxNmoneyin-"���շ�����跽".getBytes().length));
			sb.append("���շ��������"+getspaceforsum(maxNmoneyout-"���շ��������".getBytes().length));
			sb.append("�������"+getspaceforsum(maxNmoneyyesterday-"������� ".getBytes().length));
			sb.append("  \r\n");
			
			for(HtrStockdayrptDto trdto : hlist){
				sb.append(trdto.getSaccno()+getspaceforsum(maxSaccno-trdto.getSaccno().trim().getBytes().length)); //�˺�/��Ŀ
				sb.append(trdto.getSaccname()+getspaceforsum(maxSaccname-trdto.getSaccname().trim().getBytes().length)); //�ʻ�����
				sb.append(trdto.getNmoneyyesterday().toEngineeringString()+getspaceforsum(maxNmoneyyesterday-trdto.getNmoneyyesterday().toString().getBytes().length)); //�������
				sb.append(trdto.getNmoneyin().toPlainString()+getspaceforsum(maxNmoneyin-trdto.getNmoneyin().toString().getBytes().length)); //���շ�����跽
				sb.append(trdto.getNmoneyout().toPlainString()+getspaceforsum(maxNmoneyout-trdto.getNmoneyout().toString().getBytes().length)); //���շ��������
				sb.append(trdto.getNmoneytoday().toPlainString()); //�������
				sb.append("  \r\n");
			}
		}
		
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
		//yyyyMMddhhmmssSSS
		String strfomatdate=strdate.substring(0, 4) + "��"+ strdate.substring(4, 6) + "��"+ strdate.substring(6, 8) + "�� "+strdate.substring(8, 10)+":"+strdate.substring(10, 12)+":"+strdate.substring(12, 14);
		sb.append("�����ˣ�"+loginfo.getSuserName()+"                  �������ڣ�"+strfomatdate+" \r\n");
		
		FileUtil.getInstance().writeFile(filepathname, sb.toString());
    }
    
    private String getspaceforsum(int length){
    	StringBuffer sb = new StringBuffer("");
    	for(int i=0; i<length;i++){
    		sb.append(" ");
    	}
    	return sb.toString();
    }
    
    //�������ͳ����Ϣ
	public String exportexcel(Object o) {

		//��ѯ��dto
    	TrStockdayrptDto fdto = new TrStockdayrptDto();
    	HtrStockdayrptDto hfdto = new HtrStockdayrptDto();
    	
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String msg = "";
		HashMap<String, String> financeName = new HashMap<String, String>(); // ��������
		HashMap<String, String> accountname = new HashMap<String, String>(); // �˻�
		HashMap<String, BigDecimal> fmt = new HashMap<String, BigDecimal>(); // ���
		
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if (!loginfo.getSorgcode().equals(centerorg)) {
			MessageDialog.openMessageDialog(null, " û�е�ǰ����Ȩ�ޣ�");
			return "";
		}

		//ȡ�ÿ���������
		TsTreasuryDto finddto=new TsTreasuryDto();
		try {
			
			finddtolist = commonDataAccessService.findRsByDto(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		
		try {
			TrStockdayrptDto trstockdayrptdto=new TrStockdayrptDto();
			trstockdayrptdto.setSrptdate(dto.getSrptdate());
			
			templist=commonDataAccessService.selHtableBydto(trstockdayrptdto);
			List list=null;
			BigDecimal bigtemp=new BigDecimal(10000.00);
			List<Map.Entry<String, String>> infoIds =null;
			List<Map.Entry<String, String>> infoIdsaccountname =null;
			
			for(TsTreasuryDto idto :finddtolist){
				/**
				 * ��һ�� ���ò�������map
				 */
				String name =idto.getStrename();
				//'���ҽ�ⰰɽ������֧��'
				//'���ҽ�ⰰɽ��������֧��'
				//'���ҽ�������֧��'
				//'���ҽ��̨�����¿�������'
				name = name.replaceAll("����", "").replaceAll("���", "").replaceAll("����", "").replaceAll("֧��", "").replaceAll("��ɽ", "").replaceAll("��", "").trim()+"����";
				financeName.put(idto.getStrecode(), name);
				
				/**
				 * �ڶ��� ȡ�ÿ�����ݣ����û���map�ͽ��map
				 */
				//�������
				fdto.setStrecode(idto.getStrecode());
				//��������
				fdto.setSrptdate(dto.getSrptdate());
				
				//�������
				hfdto.setStrecode(idto.getStrecode());
				//��������
				hfdto.setSrptdate(dto.getSrptdate());

				if(null!=templist&&templist.size()>0){
					list =commonDataAccessService.findRsByDto(fdto);
				}
				else{
					list =commonDataAccessService.findRsByDto(hfdto);
				}
				if (list == null || list.size() == 0) {
					continue;
				}
				else{
					if(null!=templist&&templist.size()>0){
						List<TrStockdayrptDto> flist=(List<TrStockdayrptDto>)list;
						for(TrStockdayrptDto trdto : flist){ //N_MONEYTODAY
							accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
							fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
						}
					}else{
						List<HtrStockdayrptDto> hflist=(List<HtrStockdayrptDto>)list;
						for(HtrStockdayrptDto trdto : hflist){ //N_MONEYTODAY
							accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
							fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
						}
					}
				}
			}
			
			//����
			infoIds =
			    new ArrayList<Map.Entry<String, String>>(financeName.entrySet());
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
			
			infoIdsaccountname =
			    new ArrayList<Map.Entry<String, String>>(accountname.entrySet());
			Collections.sort(infoIdsaccountname, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
		

			String datename=dto.getSrptdate().substring(0, 4) + "��"+ dto.getSrptdate().substring(4, 6) + "��"+ dto.getSrptdate().substring(6, 8) + "��";
			ReportExcel.init();
			String copyFilename = "ȫ��"+datename+"ȫϽ���ͳ�Ʊ�.xls";

			// ����ģ���ļ�·��
			String filerootpath = "C:\\" + "Report" + dirsep;
			ReportExcel.setFilepath(filerootpath);
			// ReportExcel.setFilepath("com/cfcc/itfe/client/ireport/");
			// �ļ�·��
			String rportpath = TimeFacade.getCurrentStringTime() + dirsep;
			ReportExcel.setNewfilepath(filerootpath + rportpath);
			// �½���������
			ReportExcel.setCopyFilename(copyFilename);
			// �������
			ReportExcel.setReporttitle("ȫ��"+datename+"ȫϽ���ͳ�Ʊ�");
			msg=filerootpath+copyFilename;
			
			// ����λ
			ReportExcel.setUnit("");
			// ��������
			HashMap<String, Object> datamap = new HashMap<String, Object>();
			datamap.put("infoIds" ,infoIds);
			datamap.put("infoIdsaccountname" ,infoIdsaccountname);
			datamap.put("fmt" ,fmt);
			ReportExcel.setDatamap(datamap);

			// ����������
			String date = TimeFacade.getCurrentStringTime();
			date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
					+ date.substring(6, 8) + "��";
			ReportExcel.setDate(date);

			// ����ģ������
			ReportExcel.setFilename("model2.xls");
			ReportExcel.setInputstream(FinStockdayrptBean.class
					.getResourceAsStream("model2.xls"));
			// ����sheet����
			ReportExcel.setSheetname(datename);

			ReportExcel.getreportbyStockdayrpt("", "");

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		
		MessageDialog.openMessageDialog(null, " �ļ������ɣ��ѱ��浽��" + msg);
		if(flag) editor.fireModelChanged();
		
        return super.exporttxt(o);
	}
	
    public String exportexcelbyfamt(Object o){
        
		//��ѯ��dto
    	TrStockdayrptDto fdto = new TrStockdayrptDto();
    	HtrStockdayrptDto hfdto = new HtrStockdayrptDto();
    	
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String msg = "";
		HashMap<String, String> financeName = new HashMap<String, String>(); // ��������
		HashMap<String, String> accountname = new HashMap<String, String>(); // �˻�
		HashMap<String, BigDecimal> fmt = new HashMap<String, BigDecimal>(); // ���
		
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if (!loginfo.getSorgcode().equals(centerorg)) {
			MessageDialog.openMessageDialog(null, " û�е�ǰ����Ȩ�ޣ�");
			return "";
		}

		//ȡ�ÿ���������
		TsTreasuryDto finddto=new TsTreasuryDto();
		
		try {
			finddtolist = commonDataAccessService.findRsByDto(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		
		try {
			TrStockdayrptDto trstockdayrptdto=new TrStockdayrptDto();
			trstockdayrptdto.setSrptdate(dto.getSrptdate());
			templist=commonDataAccessService.selHtableBydto(trstockdayrptdto);
			List list=null;
			BigDecimal bigtemp=new BigDecimal(10000.00);
			List<Map.Entry<String, String>> infoIds =null;
			List<Map.Entry<String, String>> infoIdsaccountname =null;
			
			for(TsTreasuryDto idto :finddtolist){
				
				/**
				 * ��һ�� ���ò�������map
				 */
				String name =idto.getStrename();
				//'���ҽ�ⰰɽ������֧��'
				//'���ҽ�ⰰɽ��������֧��'
				//'���ҽ�������֧��'
				//'���ҽ��̨�����¿�������'
				name = name.replaceAll("����", "").replaceAll("���", "").replaceAll("����", "").replaceAll("֧��", "").replaceAll("��ɽ", "").replaceAll("��", "").trim()+"����";
				financeName.put(idto.getStrecode(), name);
				
				/**
				 * �ڶ��� ȡ�ÿ�����ݣ����û���map�ͽ��map
				 */
				//�������
				fdto.setStrecode(idto.getStrecode());
				//��������
				fdto.setSrptdate(dto.getSrptdate());
				
				//�������
				hfdto.setStrecode(idto.getStrecode());
				//��������
				hfdto.setSrptdate(dto.getSrptdate());
				if(null!=templist&&templist.size()>0){
					list =commonDataAccessService.findRsByDto(fdto);
				}
				else{
					list =commonDataAccessService.findRsByDto(hfdto);
				}
				if (list == null || list.size() == 0) {
					continue;
				}
				else{
					if(null!=templist&&templist.size()>0){
						List<TrStockdayrptDto> flist=(List<TrStockdayrptDto>)list;
						for(TrStockdayrptDto trdto : flist){ //N_MONEYTODAY
							//С��1��Ԫ��ͳ��
							if(trdto.getNmoneytoday().compareTo(bigtemp) >= 0){
								accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
								fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
							}
						}
					}else{
						List<HtrStockdayrptDto> hflist=(List<HtrStockdayrptDto>)list;
						for(HtrStockdayrptDto trdto : hflist){ //N_MONEYTODAY
							//С��1��Ԫ��ͳ��
							if(trdto.getNmoneytoday().compareTo(bigtemp) >= 0){
								accountname.put(idto.getStrecode()+trdto.getSaccno(), makeSaccname(trdto.getSaccname()));
								fmt.put(idto.getStrecode()+trdto.getSaccno(), trdto.getNmoneytoday().divide(bigtemp));
							}
						}
					}
				}
			}
			
			    new ArrayList<Map.Entry<String, String>>(financeName.entrySet());
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
			
			infoIdsaccountname =
			    new ArrayList<Map.Entry<String, String>>(accountname.entrySet());
			Collections.sort(infoIdsaccountname, new Comparator<Map.Entry<String, String>>() {   
			    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return (o1.getKey()).toString().compareTo(o2.getKey());
			    }
			});
		

			String datename=dto.getSrptdate().substring(0, 4) + "��"+ dto.getSrptdate().substring(4, 6) + "��"+ dto.getSrptdate().substring(6, 8) + "��";
			ReportExcel.init();
			String copyFilename = "ȫ��"+datename+"ȫϽ���ͳ�Ʊ�.xls";

			// ����ģ���ļ�·��
			String filerootpath = "C:\\" + "Report" + dirsep;
			ReportExcel.setFilepath(filerootpath);
			// ReportExcel.setFilepath("com/cfcc/itfe/client/ireport/");
			// �ļ�·��
			String rportpath = TimeFacade.getCurrentStringTime() + dirsep;
			ReportExcel.setNewfilepath(filerootpath + rportpath);
			// �½���������
			ReportExcel.setCopyFilename(copyFilename);
			// �������
			ReportExcel.setReporttitle("ȫ��"+datename+"ȫϽ���ͳ�Ʊ�");
			msg=filerootpath+copyFilename;
			
			// ����λ
			ReportExcel.setUnit("");
			// ��������
			HashMap<String, Object> datamap = new HashMap<String, Object>();
			datamap.put("infoIds" ,infoIds);
			datamap.put("infoIdsaccountname" ,infoIdsaccountname);
			datamap.put("fmt" ,fmt);
			ReportExcel.setDatamap(datamap);

			// ����������
			String date = TimeFacade.getCurrentStringTime();
			date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
					+ date.substring(6, 8) + "��";
			ReportExcel.setDate(date);

			// ����ģ������
			ReportExcel.setFilename("model2.xls");
			ReportExcel.setInputstream(FinStockdayrptBean.class
					.getResourceAsStream("model2.xls"));
			// ����sheet����
			ReportExcel.setSheetname(datename);

			ReportExcel.getreportbyStockdayrpt("", "");

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		
		MessageDialog.openMessageDialog(null, " �ļ������ɣ��ѱ��浽��" + msg);
		if(flag) editor.fireModelChanged();
		
        return super.exporttxt(o);
    }
    
    public String makeSaccname(String saccname){
    	int index = saccname.indexOf("-");
    	if(index>0){
    		return saccname.substring(index+1, saccname.length());
    	}
    	else{
    		return saccname;
    	}
    }
    
    /**
	 * Direction: �����Ϻ�����
	 * ename: exportReport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportReport(Object o){
    	String dirsep = File.separator; // ȡ��ϵͳ�ָ��
        TrStockdayrptDto nowStockday = new TrStockdayrptDto();
        nowStockday.setStrecode(dto.getStrecode());
        nowStockday.setSrptdate(dto.getSrptdate());
        List result = new ArrayList();
        try {
        	result = commonDataAccessService.findRsByDto(nowStockday, "", nowStockday.tableName());
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		TsTreasuryPK treasuryPK = new TsTreasuryPK();
		treasuryPK.setSorgcode(loginfo.getSorgcode());
		treasuryPK.setStrecode(dto.getStrecode());
		
		TsTreasuryDto tsTreasuryDto = (TsTreasuryDto) commonDataAccessService.find(treasuryPK);
		
		
        String datename=dto.getSrptdate().substring(0, 4) + "��"+ dto.getSrptdate().substring(4, 6) + "��"+ dto.getSrptdate().substring(6, 8) + "��";
		ReportExcel.init();
//		���ҽ���Ϻ�������֧�⣨����
		String copyFilename = tsTreasuryDto.getStrename().replaceAll("������", "") + "��������ձ���.xls";

		// ����ģ���ļ�·��
		String filerootpath = "C:\\" + "Report" + dirsep;
		ReportExcel.setFilepath(filerootpath);
		// ReportExcel.setFilepath("com/cfcc/itfe/client/ireport/");
		// �ļ�·��
		String rportpath = TimeFacade.getCurrentStringTime() + dirsep;
		ReportExcel.setNewfilepath(filerootpath + rportpath);
		// �½���������
		ReportExcel.setCopyFilename(copyFilename);
		// �������
		ReportExcel.setReporttitle(tsTreasuryDto.getStrename().replaceAll("������", "") + "��������ձ���");
//		msg=filerootpath+copyFilename;
		// ����λ
		ReportExcel.setUnit("");
		// ��������
		HashMap<String, Object> datamap = new HashMap<String, Object>();
		datamap.put("infoIds" ,result);
//		datamap.put("infoIdsaccountname" ,infoIdsaccountname);
//		datamap.put("fmt" ,fmt);
		ReportExcel.setDatamap(datamap);

		// ����������
		String date = TimeFacade.getCurrentStringTime();
		date = date.substring(0, 4) + "��" + date.substring(4, 6) + "��"
				+ date.substring(6, 8) + "��";
		ReportExcel.setDate(date);

		// ����ģ������
		ReportExcel.setFilename("model.xls");
		ReportExcel.setInputstream(FinStockdayrptBean.class
				.getResourceAsStream("model.xls"));
		// ����sheet����
		ReportExcel.setSheetname(datename);
		ReportExcel.bzunit = tsTreasuryDto.getStrename().replaceAll("������", "");
		ReportExcel.bzuser = loginfo.getSuserName();
		
			String msg = ReportExcel.getreportbyStockdayrpt("", "");
			MessageDialog.openMessageDialog(null, msg);
		} catch (ITFEBizException e) {
			log.error("��������ʧ�ܣ�",e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.exportReport(o);
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
			
			String where=" 1=1 ";
//			//��������Ϊ�գ��������Ϊ�գ���ȫ��
//			if(list.size()==0&&(dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSorgcode()==null || "".equals(dto.getSorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
//				
//				where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
//				for(int i = 1 ;i<finddtolist.size();i++){
//					where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
//				}
//				where+=" ) ";
//			}
			
			return commonDataAccessService.findRsByDtoWithConditionPaging(dto,
					pageRequest, where, "", dto.tableName()," where S_RPTDATE = '"+dto.getSrptdate()+"' ");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	
	public static Log getLog() {
		return log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List<TsTreasuryDto> getFinddtolist() {
		return finddtolist;
	}

	public void setFinddtolist(List<TsTreasuryDto> finddtolist) {
		this.finddtolist = finddtolist;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}
	
	
	
	
}