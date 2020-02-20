package com.cfcc.itfe.client.dataquery.trincomedayrpt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-24 10:44:46
 * ��ϵͳ: DataQuery
 * ģ��:TrIncomedayrpt
 * ���:TrIncomedayrpt
 */
public class TrIncomedayrptBean extends AbstractTrIncomedayrptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TrIncomedayrptBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    List<TsConvertfinorgDto> finddtolist=null;
    private List reportRs = null;
    private Map reportmap = new HashMap();
    private String reportPath = "";
	

	private Boolean flag=true; 

	public TrIncomedayrptBean() {
		super();
		dto = new TrIncomedayrptDto();
		dto.setSrptdate(TimeFacade.getCurrentStringTime());
		pagingcontext = new PagingContext(this);
		reportRs = new ArrayList();
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: �������������ձ����ѯ��� messages:
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
		
		TsConvertfinorgDto finddto=new TsConvertfinorgDto();
		
		//�����Ļ���
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//�жϵ�ǰ��¼��ѯ������롢���������Ƿ��ڹ���������
			finddto.setSorgcode(loginfo.getSorgcode()); //�����������
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
			returnpage = "�������������ձ����ѯ";
		} else {
			returnpage = super.query(o);
		}
		return returnpage;
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: �������������ձ����ѯ messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
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
			//��������Ϊ�գ��������Ϊ�գ���ȫ��
			if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSfinorgcode()==null || "".equals(dto.getSfinorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
				
				where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
				for(int i = 1 ;i<finddtolist.size();i++){
					where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
				}
				where+=" ) ";
			}
			
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, where, "", dto.tableName());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
	
	/**
	 * Direction: ��������
	 * ename: exportTable
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportTable(Object o){
    	String where="";
		//��������Ϊ�գ��������Ϊ�գ���ȫ��
		if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSfinorgcode()==null || "".equals(dto.getSfinorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
			
			where+=" ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
			for(int i = 1 ;i<finddtolist.size();i++){
				where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
			}
			where+=" ) ";
		}
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+"3128�������������ձ�("+TimeFacade.getCurrentStringTime()+").txt";
		List<TrIncomedayrptDto> templist=null;
		try {
			templist=trIncomedayrptService.exportQuery(dto, where);
		} catch (ITFEBizException e1) {
			MessageDialog.openMessageDialog(null, "��ѯ���ݳ���");
			return null;
		}
		try {
			exportTableForWhere(templist, fileName, ",");
			MessageDialog.openMessageDialog(null, "���ݵ����ɹ�\n"+fileName);
		} catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}

        return "";
    }
    /**
     * ��������������
     * @param list
     * @param filename
     * @return
     * @throws FileOperateException 
     */
    public String exportTableForWhere(List <TrIncomedayrptDto> templist ,String fileName,	String splitSign ) throws FileOperateException{
    	String sql="�������ش���,���ջ��ش���,�����������,��������,Ԥ������,Ԥ�㼶�δ���,Ԥ���Ŀ,Ԥ���Ŀ����,���ۼƽ��,���ۼƽ��,���ۼƽ��,Ͻ����־,�����ڱ�־,�ֳ����־,��������";
		StringBuffer filebuf = new StringBuffer(sql+"\r\n");
        for (TrIncomedayrptDto _dto :templist) {
        	filebuf.append(_dto.getSfinorgcode()+",");
        	filebuf.append(_dto.getStaxorgcode()+",");
        	filebuf.append(_dto.getStrecode()+",");
        	filebuf.append(_dto.getSrptdate()+",");
        	filebuf.append(_dto.getSbudgettype()+",");
        	filebuf.append(_dto.getSbudgetlevelcode()+",");
        	filebuf.append(_dto.getSbudgetsubcode()+",");
        	filebuf.append(_dto.getSbudgetsubname()+",");
        	filebuf.append(_dto.getNmoneyday()+",");
        	filebuf.append(_dto.getNmoneymonth()+",");
        	filebuf.append(_dto.getNmoneyyear()+",");
        	filebuf.append(_dto.getSbelongflag()+",");
        	filebuf.append(_dto.getStrimflag()+",");
        	filebuf.append(_dto.getSdividegroup()+",");
        	filebuf.append(_dto.getSbillkind());
        	filebuf.append("\r\n");
		}
        filebuf.delete(filebuf.length()-2, filebuf.length());
    	FileUtil.getInstance().writeFile(fileName, filebuf.toString());
		return "";
    	
    	
    }
	/**
	 * Direction: �����ĵ�
	 * ename: exporttxt
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exporttxt(Object o){
        
    	//��ѯ��dto
    	TrIncomedayrptDto fdto = new TrIncomedayrptDto();
    	HtrIncomedayrptDto hdto = new HtrIncomedayrptDto();
    	
    	String dirsep = File.separator; // ȡ��ϵͳ�ָ��
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
		
		String rtmsg ="";
		
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		TsConvertfinorgDto finddto=new TsConvertfinorgDto();
		//�����Ļ���
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//�жϵ�ǰ��¼��ѯ������롢���������Ƿ��ڹ���������
			finddto.setSorgcode(loginfo.getSorgcode()); //�����������
		}
		
		try {
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,"1");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		if(null==finddtolist || finddtolist.size() == 0){
			MessageDialog.openMessageDialog(null, "Ȩ�޲��㣡����д��ȷ�Ĳ���������߹�����룡");
			return null;
		}
		
		try {
			List<TrIncomedayrptDto> listFile= new ArrayList();
			String copyFilename=dto.getSrptdate()+"_shouru_"+finddto.getSorgcode()+"_"+(new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()))+".txt";
			for(TsConvertfinorgDto idto :finddtolist){
					List<TrIncomedayrptDto> list1=null;
					//�������
					fdto.setStrecode(idto.getStrecode());
					//��������
					fdto.setSrptdate(dto.getSrptdate());
					list1 =(List<TrIncomedayrptDto>)commonDataAccessService.findRsByDto(fdto);
					if (list1 != null && list1.size() != 0) {
						listFile.addAll(list1);
					} 
//				}
			}
			if (listFile != null && listFile.size() != 0) {
				//�����ļ�
				filecreat(filepath+copyFilename,listFile,dto.getSrptdate(),"0");
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
		if(flag) this.editor.fireModelChanged();
        return super.exporttxt(o);
    }
    
    private void filecreat(String filepathname,List<TrIncomedayrptDto> list,String srptdate,String flag) throws FileOperateException{
    	
    	//�����ļ�
    	File file = new File(filepathname);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		StringBuffer sb = new StringBuffer("");
		sb.append("czjgdm,zsjgdm,gkztdm,rq,yszl,ysjcdm,yskm,yskmmc,rljje,xljje,yljje,jljje,nljje,xsbz,tzqbz,fczbz,bbzl \r\n");
		List<TrIncomedayrptDto> dlist=(List<TrIncomedayrptDto>)list;
		
		if(flag.equals("0")){//all
			for(TrIncomedayrptDto trdto : dlist){
				
				//���Ϊ�������ջ��أ�������
//				if(trdto.getStaxorgcode().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)){
//					continue;
//				}
				
				sb.append(trdto.getSfinorgcode()+","); //skgkdm
				sb.append(trdto.getStaxorgcode()+","); //mdgkdm
				sb.append(trdto.getStrecode()+","); //ssgkdm
				sb.append(trdto.getSrptdate()+","); //ysjc
				sb.append(trdto.getSbudgettype()+","); //jgdm
				sb.append(trdto.getSbudgetlevelcode()+","); //kmdm
				sb.append(trdto.getSbudgetsubcode()+","); //zwrq
				sb.append(trdto.getSbudgetsubname()+","); //yszl
				sb.append(trdto.getNmoneyday()+","); //rlj
				sb.append(trdto.getNmoneytenday()+","); //ylj
				sb.append(trdto.getNmoneymonth()+","); //nlj
				sb.append(trdto.getNmoneyquarter()+","); //aaaa
				sb.append(trdto.getNmoneyyear()+","); //aaaa
				sb.append(trdto.getSbelongflag()+","); //aaaa
				sb.append(trdto.getStrimflag()+","); //aaaa
				sb.append(trdto.getSdividegroup()+","); //aaaa
				sb.append(trdto.getSbillkind()); //aaaa
				sb.append(" \r\n");
			}
		}
		else{//000000000000
			for(TrIncomedayrptDto trdto : dlist){
				
				//���Ϊ�������ջ��أ�����
				if(trdto.getStaxorgcode().equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)){
					
					sb.append(trdto.getStrecode()+","); //skgkdm
					sb.append(trdto.getStrecode()+","); //mdgkdm
					sb.append(trdto.getStrecode()+","); //ssgkdm
					sb.append(trdto.getSbudgetlevelcode()+","); //ysjc
					sb.append(trdto.getStaxorgcode()+","); //jgdm
					sb.append(trdto.getSbudgetsubcode()+","); //kmdm
					sb.append(trdto.getSrptdate()+","); //zwrq
					sb.append(trdto.getSbudgettype()+","); //yszl
					sb.append(trdto.getNmoneyday()+","); //rlj
					sb.append(trdto.getNmoneymonth()+","); //ylj
					sb.append(trdto.getNmoneyyear()); //nlj
					sb.append(" \r\n");
				}
			}
		}

		FileUtil.getInstance().writeFile(filepathname, sb.toString());
    }
    
	/**
	 * Direction: �����ĵ��������ջ���
	 * ename: exporttxt0
	 * ���÷���: 
	 * viewers: �������������ձ����ѯ
	 * messages: 
	 */
    public String exporttxt0(Object o){
        
    	//��ѯ��dto
    	TrIncomedayrptDto fdto = new TrIncomedayrptDto();
    	HtrIncomedayrptDto hdto = new HtrIncomedayrptDto();
    	
    	String dirsep = File.separator; // ȡ��ϵͳ�ָ��
    	String filepath="C:\\"+"Report"+dirsep+TimeFacade.getCurrentStringTime()+dirsep;
		
		String rtmsg ="";
		
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		TsConvertfinorgDto finddto=new TsConvertfinorgDto();
		//�����Ļ���
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//�жϵ�ǰ��¼��ѯ������롢���������Ƿ��ڹ���������
			finddto.setSorgcode(loginfo.getSorgcode()); //�����������
		}
		try {
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,"1");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		if(null==finddtolist || finddtolist.size() == 0){
			MessageDialog.openMessageDialog(null, "Ȩ�޲��㣡����д��ȷ�Ĳ���������߹�����룡");
			return null;
		}
		
		try {
			
			for(TsConvertfinorgDto idto :finddtolist){
				
				//ֻ���п��������
//				if(idto.getStrelevel().equals("3") || idto.getStrelevel().equals("4")){
					
					List<TrIncomedayrptDto> list1=null;
					String copyFilename=idto.getStrecode()+"_"+dto.getSrptdate()+"_000000000000_"+(new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()))+".txt";
					
					//�������
					fdto.setStrecode(idto.getStrecode());
					//��������
					fdto.setSrptdate(dto.getSrptdate());
					
					list1 =(List<TrIncomedayrptDto>)commonDataAccessService.findRsByDto(fdto);

					if (list1 == null || list1.size() == 0) {
						MessageDialog.openMessageDialog(null, idto.getStrecode()+","+idto.getStrename()+": ��ѯ�޼�¼��");
						continue;
					} 
					
					//�����ļ�
					filecreat(filepath+copyFilename,list1,dto.getSrptdate(),"1");
					rtmsg +=filepath+copyFilename+"; \r\n";
//				}
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
		if(flag) this.editor.fireModelChanged();
        return super.exporttxt0(o);
    }
    
    /**
	 * Direction: ���ص���ѯ���
	 * ename: rebackToSelectResult
	 * ���÷���: 
	 * viewers: �������������ձ����ѯ���
	 * messages: 
	 */
    public String rebackToSelectResult(Object o){
        
        return super.rebackToSelectResult(o);
    }
    
	/**
	 * Direction: ����Ԥ��
	 * ename: printReport
	 * ���÷���: 
	 * viewers: �������������ձ����ӡ
	 * messages: 
	 */
    public String printReport(Object o){
    	reportPath = "com/cfcc/itfe/client/ireport/trIncomedayrpt.jasper";
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
    	List<TrIncomedayrptDto>	list = new ArrayList<TrIncomedayrptDto>();
    	String where=" 1=1 ";
		//��������Ϊ�գ��������Ϊ�գ���ȫ��
		if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSfinorgcode()==null || "".equals(dto.getSfinorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
			
			where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
			for(int i = 1 ;i<finddtolist.size();i++){
				where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
			}
			where+=" ) ";
		}
		TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
		tsTreasuryDto.setStrecode(dto.getStrecode());
		String trename = "";
		try {
			list.addAll(trIncomedayrptService.exportQuery(dto, where));
	    	reportRs.clear();
			reportRs.addAll(list);
			List<TsTreasuryDto> list1 =(List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
			if(list1!=null && list1.size()>0){
				trename = list1.get(0).getStrename();
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if (null == reportRs || reportRs.size() == 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.goBack(o);
		}
		//Ԥ�㼶��
		if(dto.getSbudgetlevelcode()==null || dto.getSbudgetlevelcode().equals("")){
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ�㼶��!");
			return super.goBack(o);
		}else {
			if(dto.getSbudgetlevelcode().equals("0")){
				reportmap.put("title", trename+"����Ԥ�������ձ���");
			}else if(dto.getSbudgetlevelcode().equals("1")){
				reportmap.put("title", trename+"���뼶Ԥ�������ձ���");
			}else if(dto.getSbudgetlevelcode().equals("2")){
				reportmap.put("title", trename+"ʡ��Ԥ�������ձ���");
			}else if(dto.getSbudgetlevelcode().equals("3")){
				reportmap.put("title", trename+"�м�Ԥ�������ձ���");
			}else if(dto.getSbudgetlevelcode().equals("4")){
				reportmap.put("title", trename+"�ؼ�Ԥ�������ձ���");
			}else if(dto.getSbudgetlevelcode().equals("5")){
				reportmap.put("title", trename+"�缶Ԥ�������ձ���");
			}else if(dto.getSbudgetlevelcode().equals("6")){
				reportmap.put("title", trename+"�ط���Ԥ�������ձ���");
			}
		}
		//Ͻ����־
		if(dto.getSbelongflag()==null || dto.getSbelongflag().equals("")){
			MessageDialog.openMessageDialog(null, "��ѡ��Ͻ����־!");
			return super.goBack(o);
		}else{
			if(dto.getSbelongflag().equals("0")){
				reportmap.put("sbelongflag", "��������");
			}else if(dto.getSbelongflag().equals("1")){
				reportmap.put("sbelongflag", "��ȫϽ��");
			}else if(dto.getSbelongflag().equals("3")){
				reportmap.put("sbelongflag", "��ȫϽ�Ǳ�����");
			}
		}
		//Ԥ������
		if(dto.getSbudgettype()==null || dto.getSbudgettype().equals("")){
			MessageDialog.openMessageDialog(null, "��ѡ��Ԥ������!");
			return super.goBack(o);
		}else{
			if(dto.getSbudgettype().equals("1")){
				reportmap.put("sbudgettype", "��Ԥ���ڡ�");
			}else if(dto.getSbudgettype().equals("2")){
				reportmap.put("sbudgettype", "��Ԥ���⡿");
			}
		}
		//��������
		if(dto.getSrptdate()==null || dto.getSrptdate().equals("")){
			MessageDialog.openMessageDialog(null, "��ѡ�񱨱�����!");
			return super.goBack(o);
		}else{
			String date = dto.getSrptdate().substring(0, 4)+"��"+dto.getSrptdate().substring(4, 6)+"��"+dto.getSrptdate().substring(6, 8)+"��";
			reportmap.put("reportDate", date);
		}
		editor.fireModelChanged();
        return super.printReport(o);
    }
    
	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TrIncomedayrptBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getFinddtolist() {
		return finddtolist;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public void setFinddtolist(List<TsConvertfinorgDto> finddtolist) {
		this.finddtolist = finddtolist;
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

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	
}