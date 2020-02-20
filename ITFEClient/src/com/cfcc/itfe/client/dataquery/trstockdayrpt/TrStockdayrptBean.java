package com.cfcc.itfe.client.dataquery.trstockdayrpt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-24 10:44:47
 * ��ϵͳ: DataQuery
 * ģ��:TrStockdayrpt
 * ���:TrStockdayrpt
 */
public class TrStockdayrptBean extends AbstractTrStockdayrptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TrStockdayrptBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    List<TsConvertfinorgDto> finddtolist=null;
	
	private Boolean flag=true; 
	
	public TrStockdayrptBean() {
		super();
		dto = new TrStockdayrptDto();
		String dateString = TimeFacade.getCurrentStringTime();
//		dto.setSrptdate(dateString);		
		pagingcontext = new PagingContext(this);
		startDate = dateString;
		endDate = dateString;
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
		if(!startDate.toString().trim().matches("[0-9]{8}")){
			MessageDialog.openMessageDialog(null, "���ڸ�ʽ����,��ʼ���ڱ���Ϊ8λ���֣�");
			return "";
		}
		if(!endDate.toString().trim().matches("[0-9]{8}")){
			MessageDialog.openMessageDialog(null, "���ڸ�ʽ����,��ֹ���ڱ���Ϊ8λ���֣�");
			return "";
		}

		// ����ҳ��
		String returnpage = null;
		
		//���Ļ�������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			//�жϵ�ǰ��¼��ѯ������롢���������Ƿ��ں���������
			TsConvertfinorgDto finddto=new TsConvertfinorgDto();
			
			finddto.setSorgcode(loginfo.getSorgcode()); //�����������
			if(dto.getSorgcode()!=null && !"".equals(dto.getSorgcode())){
				finddto.setSfinorgcode(dto.getSorgcode()); //��������
			}
			if(dto.getStrecode()!=null && !"".equals(dto.getStrecode())){
				finddto.setStrecode(dto.getStrecode()); //�����������
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
	 * Direction: ��������
	 * ename: exportTable
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportTable(Object o){
    	HtrStockdayrptDto hdto = new HtrStockdayrptDto();
		hdto.setSorgcode(dto.getSorgcode());
		hdto.setStrecode(dto.getStrecode());
		hdto.setSrptdate(dto.getSrptdate());
		String where="";
		//��������Ϊ�գ��������Ϊ�գ���ȫ��
		if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSorgcode()==null || "".equals(dto.getSorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
			
			where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
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
		String fileName = filePath+ File.separator+"3128��������ձ�("+startDate+"-"+endDate+").txt";
		List<TrStockdayrptDto> templist =null;
		try {
			templist = commonDataAccessService.findRsByDto(dto, where);
			templist.addAll(commonDataAccessService.findRsByDto(hdto, where));
		} catch (ITFEBizException e1) {
			MessageDialog.openMessageDialog(null, "��ѯ���ݳ���");
			return null;
		}
		
		if (templist == null || templist.size() == 0) {
			MessageDialog.openMessageDialog(null, "�޷���ѯ�����ݣ������²�ѯ��");
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
    public String exportTableForWhere(List <TrStockdayrptDto> templist ,String fileName,	String splitSign ) throws FileOperateException{
    	String sql="�������ش���,�������,��������,�˻�����,�˻�����,�˻�����,������� ,��������,����֧��,�������";
		StringBuffer filebuf = new StringBuffer(sql+"\r\n");
		TrStockdayrptDto _dto = null;
		HtrStockdayrptDto hdto = null;
        for (IDto idto:templist) {
        	if(idto instanceof TrStockdayrptDto)
        	{
        		_dto = (TrStockdayrptDto)idto;
	        	filebuf.append(_dto.getSorgcode()+",");
	        	filebuf.append(_dto.getStrecode()+",");
	        	filebuf.append(_dto.getSrptdate()+",");
	        	filebuf.append(_dto.getSaccno()+",");
	        	filebuf.append(_dto.getSaccname()+",");
	        	filebuf.append(_dto.getSaccdate()+",");	        	
	        	filebuf.append(_dto.getNmoneyyesterday()+","); 
	        	filebuf.append(_dto.getNmoneyin()+","); 
	        	filebuf.append(_dto.getNmoneyout()+",");
	        	filebuf.append(_dto.getNmoneytoday());
	        	filebuf.append("\r\n");
        	}
        	else if(idto instanceof HtrStockdayrptDto)
        	{
        		hdto = (HtrStockdayrptDto)idto;
        		filebuf.append(hdto.getSorgcode()+",");
            	filebuf.append(hdto.getStrecode()+",");
            	filebuf.append(hdto.getSrptdate()+",");
            	filebuf.append(hdto.getSaccno()+",");
            	filebuf.append(hdto.getSaccname()+",");
            	filebuf.append(hdto.getSaccdate()+",");
            	filebuf.append(hdto.getSrptdate()+",");
            	filebuf.append(hdto.getNmoneyyesterday()+","); 
            	filebuf.append(hdto.getNmoneyin()+","); 
            	filebuf.append(hdto.getNmoneyout()+",");
            	filebuf.append(hdto.getNmoneytoday());
            	filebuf.append("\r\n");
        	}
		}
        filebuf.delete(filebuf.length()-2, filebuf.length());
    	FileUtil.getInstance().writeFile(fileName, filebuf.toString());
		return "";
    	
    	
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
		
		String rtmsg ="";
		
		try {
			List listFile = new ArrayList();
			String copyFilename=dto.getSrptdate()+"_kucun_"+finddto.getSorgcode()+"_"+(new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()))+".txt";
			for(TsConvertfinorgDto idto :finddtolist){
				
				//ֻ���п��������
//				if(idto.getStrelevel().equals("3") || idto.getStrelevel().equals("4")){
					
					List list=null;
					String strdate=(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis()));
					
					
					//�������
					fdto.setStrecode(idto.getStrecode());
					//��������
					fdto.setSrptdate(dto.getSrptdate());
					
					//�������
					hfdto.setStrecode(idto.getStrecode());
					//��������
					hfdto.setSrptdate(dto.getSrptdate());

//					if(TimeFacade.getCurrentStringTime().equals(dto.getSrptdate())){
						list =commonDataAccessService.findRsByDto(fdto);
//					}
//					else{
//						list =commonDataAccessService.findRsByDto(hfdto);
//					}
					
					if (list != null && list.size() > 0) {
						listFile.addAll(list);
					} 
//				}
			}
			if(listFile!=null&&listFile.size()>0){
				/**
				 * �ļ�����
				 */
				filecreat(filepath+copyFilename,listFile,dto.getSrptdate(),dto.getSrptdate(),null);
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
    
    private void filecreat(String filepathname,List list,String srptdate,String strdate,TsConvertfinorgDto idto) throws FileOperateException{
    	//�����ļ�
    	try {
    		File file = new File(filepathname);
    		File dir = new File(file.getParent());
    		if (!dir.exists()) {
    			dir.mkdirs();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		
		StringBuffer sb = new StringBuffer("");
		
		sb.append("czjgdm,gkdm,rq,zhdm,zhmc,zhrq,srye,brsr,brzc,brye \r\n");
		List<TrStockdayrptDto> dlist=(List<TrStockdayrptDto>)list;
		
		for(TrStockdayrptDto trdto : dlist){
			sb.append(trdto.getSorgcode()+","); //skgkdm
			sb.append(trdto.getStrecode()+","); //mdgkdm
			sb.append(trdto.getSrptdate()+","); //ssgkdm
			sb.append(trdto.getSaccno()+","); //ysjc
			sb.append(trdto.getSaccname()+","); //jgdm
			sb.append(trdto.getSaccdate()+","); //kmdm
			sb.append(trdto.getSrptdate()+","); //zwrq
			sb.append(trdto.getNmoneyyesterday()+","); //yszl
			sb.append(trdto.getNmoneyin()+","); //rlj
			sb.append(trdto.getNmoneyout()+","); //ylj
			sb.append(trdto.getNmoneytoday()); //nlj
			sb.append(" \r\n");
		}
		
	/*	
		StringBuffer sb = new StringBuffer("");
		sb.append("                                  "+"��������ձ� \r\n");
		sb.append("                                  "+"�������ڣ�"+dto.getSrptdate().substring(0, 4) + "��"+ dto.getSrptdate().substring(4, 6) + "��"+ dto.getSrptdate().substring(6, 8) + "��  \r\n");
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
//		if(TimeFacade.getCurrentStringTime().equals(srptdate)){
			List<TrStockdayrptDto> dlist=(List<TrStockdayrptDto>)list;
			
			int maxSaccno=0;
			int maxSaccname=0;
			int maxNmoneyyesterday=0;
			int maxNmoneyin=0;
			int maxNmoneyout=0;
			int maxNmoneytoday=0;
			
			for(TrStockdayrptDto trdto:dlist){
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
			sb.append("�������");
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
//		}
//		else{
//			List<HtrStockdayrptDto> hlist=(List<HtrStockdayrptDto>)list;
//			for(HtrStockdayrptDto trdto : hlist){
//				sb.append(trdto.getSaccno()+" , "); //�˺�/��Ŀ
//				sb.append(trdto.getSaccname()+" , "); //�ʻ�����
//				sb.append(trdto.getNmoneyyesterday()+" , "); //�������
//				sb.append(trdto.getNmoneyin()+" , "); //���շ�����跽
//				sb.append(trdto.getNmoneyout()+" , "); //���շ��������
//				sb.append(trdto.getNmoneytoday()); //�������
//				sb.append("  \r\n");
//			}
//		}
		
		sb.append("------------------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n");
		//yyyyMMddhhmmssSSS
		String strfomatdate=strdate.substring(0, 4) + "��"+ strdate.substring(4, 6) + "��"+ strdate.substring(6, 8) + "�� ";
		sb.append("�����ˣ�"+loginfo.getSuserName()+"                  �������ڣ�"+strfomatdate+" \r\n");*/
		
		FileUtil.getInstance().writeFile(filepathname, sb.toString());
    }
    
    private String getspaceforsum(int length){
    	StringBuffer sb = new StringBuffer("");
    	for(int i=0; i<length;i++){
    		sb.append(" ");
    	}
    	return sb.toString();
    }
    
    /**
	 * Direction: ����ͬ�˻�ͬ���ۼ��������
	 * ename: exportBalData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportBalData(Object o){
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+"3128��������ձ���������ۼ�����("+startDate+"-"+endDate+").txt";
		List templist =null;
		try {
			templist = trStockdayrptService.findTotalBalForExport(dto, finddtolist, startDate, endDate);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "��ѯ���ݳ���");
			return null;
		}
		try {
			if(templist == null || templist.size() == 0){
				MessageDialog.openMessageDialog(null, "���ݲ�����");
				return null;
			}
			exportBalDataForWhere(templist, fileName, ",");
			MessageDialog.openMessageDialog(null, "���ݵ����ɹ�\n"+fileName);
		} catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
    
    /**
     * ����ָ��ʱ����˻���������ۼ�
     * @param templist
     * @param fileName
     * @param splitSign
     * @return
     * @throws FileOperateException
     */
    public String exportBalDataForWhere(List templist ,String fileName,String splitSign) throws FileOperateException{
    	StringBuffer filebuf = new StringBuffer("�˻�����,�˻�����,��ʼ����,��ֹ����,��������ۼ�\r\n");
    	for(int i=0;i<templist.size();i++){
    		TrStockdayrptReportDto dto = (TrStockdayrptReportDto)templist.get(i);
    		filebuf.append(dto.getSaccno()+",");
    		filebuf.append(fetchAcctName(dto.getSaccno(),startDate,endDate)+",");
    		filebuf.append(startDate+",");
    		filebuf.append(endDate+",");
    		filebuf.append(dto.getNtotalmoneytoday());
    		filebuf.append("\r\n");
    	}
    	filebuf.delete(filebuf.length()-2, filebuf.length());
    	FileUtil.getInstance().writeFile(fileName, filebuf.toString());
		return "";
    }
    
    public String fetchAcctName(String acctNo,String startDate,String endDate){
    	String acctName = "";
    	try {
    		acctName = trStockdayrptService.findAcctName(acctNo, startDate, endDate);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "��ѯ��������");
			return null;
		}
    	return acctName;
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
			if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSorgcode()==null || "".equals(dto.getSorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
				
				where+=" and ( S_TRECODE='"+finddtolist.get(0).getStrecode()+"' ";
				for(int i = 1 ;i<finddtolist.size();i++){
					where += " or S_TRECODE= '"+finddtolist.get(i).getStrecode()+"' ";
				}
				where+=" ) ";
			}
			if(startDate != null && !startDate.trim().equals("")){
				where+= " and S_RPTDATE >= '"+startDate+"'" ;
			}
			if(endDate != null && !endDate.trim().equals("")){
				where+= " and S_RPTDATE <= '"+endDate+"'" ;
			}
			
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, where, "", dto.tableName());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TrStockdayrptBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List<TsConvertfinorgDto> getFinddtolist() {
		return finddtolist;
	}

	public void setFinddtolist(List<TsConvertfinorgDto> finddtolist) {
		this.finddtolist = finddtolist;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
}