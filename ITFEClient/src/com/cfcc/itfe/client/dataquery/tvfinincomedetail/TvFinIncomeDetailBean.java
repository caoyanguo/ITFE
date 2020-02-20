package com.cfcc.itfe.client.dataquery.tvfinincomedetail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-24 10:44:46
 * ��ϵͳ: DataQuery
 * ģ��:TvFinIncomeDetail
 * ���:TvFinIncomeDetail
 */
public class TvFinIncomeDetailBean extends AbstractTvFinIncomeDetailBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvFinIncomeDetailBean.class);
    private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    private List<TsConvertfinorgDto> finddtolist = null;
    private String staxpayname = null;
	private List<TsTaxorgDto> querydtolist = null;
	private List<TvFinIncomeDetailDto> excheckList;// ѡ�м�¼

	public TvFinIncomeDetailBean() {
		super();
		dto = new TvFinIncomeDetailDto();
		dto.setSintredate(TimeFacade.getCurrentStringTime());
		excheckList = new ArrayList<TvFinIncomeDetailDto>();
		staxpayname = "";
		pagingcontext = new PagingContext(this);

	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: �����ˮ��ѯ��� messages:
	 */
	public String query(Object o) {
		excheckList = new ArrayList<TvFinIncomeDetailDto>();
		// ����ҳ��
		String returnpage = null;

		// ���Ļ�������
		String centerorg = null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}

		if (!loginfo.getSorgcode().equals(centerorg)) {

			// �жϵ�ǰ��¼��ѯ������롢���������Ƿ��ں���������
			TsConvertfinorgDto finddto = new TsConvertfinorgDto();

			finddto.setSorgcode(loginfo.getSorgcode()); // �����������
			if (dto.getSorgcode() != null && !"".equals(dto.getSorgcode())) {
				finddto.setSfinorgcode(dto.getSorgcode()); // ��������
			}
			if (dto.getStrecode() != null && !"".equals(dto.getStrecode())) {
				finddto.setStrecode(dto.getStrecode()); // �����������
			}

			try {
				finddtolist = commonDataAccessService.findRsByDtocheckUR(
						finddto, "1");
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
//			if (null == finddtolist || finddtolist.size() == 0) {
//				MessageDialog
//						.openMessageDialog(null, " Ȩ�޲��㣡����д��ȷ�Ĳ���������߹�����룡");
//				return null;
//			}

		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			returnpage = "�����ˮ��ѯ����";
		} 
		else {
			//excheckList.addAll(this.pagingcontext.getPage().getData());
			returnpage = super.query(o);
		}
		return returnpage;
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: �����ˮ��ѯ���� messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}
	
	/**
	 * Direction: �ֶ�������ֳ� ename: makeDivide ���÷���: viewers: * messages:
	 */
	public String makeDivide(Object o) {

		if (null == this.excheckList || excheckList.size() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ�����ļ�¼��");
			return null;
		}

		String msg = null;
		try {
			msg = tvFinIncomeDetailService.makeDivide(excheckList,dto.getSintredate());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if ("".equals(msg)) {
			MessageDialog.openMessageDialog(null, " �ֹ�����ɹ���");
		} else {
			MessageDialog.openMessageDialog(null, " �ֹ�������δִ����Ϣ��ԭ��" + msg);
		}

		return super.makeDivide(o);
	}
	
	/**
	 * Direction: �������� ename: export3139 ���÷���: viewers: * messages:
	 */
    public String export3139(Object o){
    	PageResponse page = this.pagingcontext.getPage();
		if (page == null || page.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "�б�������,�޷�ִ�е�������������ѯ��");
			return null;
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
			List<TvFinIncomeDetailDto> tmpList = new ArrayList<TvFinIncomeDetailDto>();
			tmpList.add(dto);
			String serverFilePath = tvFinIncomeDetailService.makeDivide(tmpList,where);
			int j = serverFilePath.lastIndexOf("/");
			if(j < 0 ){
				j = serverFilePath.lastIndexOf("\\");
			}
			String dirsep = File.separator;
			String clientFilePath;
			String partfilepath  = serverFilePath.replace(serverFilePath.substring(
					0, j + 1), "");
			clientFilePath = filePath + dirsep
					+ partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"
					+ clientFilePath);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		} catch (FileTransferException e) {
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
    public String exportTableForWhere(List <TvFinIncomeDetailDto> templist ,String fileName,String splitSign ) throws FileOperateException{
    	String sql="�������ش���,�������,����ί������,����ˮ��,ƾ֤���,����ƾ֤����, Ԥ�㼶��,Ԥ���Ŀ����,Ԥ������,���,ƾ֤��Դ,���ջ��ش���,ҵ����ˮ��";
		StringBuffer filebuf = new StringBuffer(sql+"\r\n");
        for (TvFinIncomeDetailDto _dto :templist) {
        	filebuf.append(_dto.getSorgcode().trim());filebuf.append(splitSign);
        	filebuf.append(_dto.getStrecode());filebuf.append(splitSign);
        	filebuf.append(_dto.getSintredate());filebuf.append(splitSign);
        	filebuf.append(_dto.getIpkgseqno());filebuf.append(splitSign);
        	filebuf.append(_dto.getSexpvouno());filebuf.append(splitSign);
        	filebuf.append(_dto.getSexpvoutype());filebuf.append(splitSign);
        	filebuf.append(_dto.getCbdglevel());filebuf.append(splitSign);
        	filebuf.append(_dto.getSbdgsbtcode());filebuf.append(splitSign);
        	filebuf.append(_dto.getCbdgkind());filebuf.append(splitSign);
        	filebuf.append(_dto.getFamt());filebuf.append(splitSign);
        	filebuf.append(_dto.getCvouchannel());filebuf.append(splitSign);
        	filebuf.append(_dto.getStaxorgcode());filebuf.append(splitSign);
        	filebuf.append(_dto.getSseq());filebuf.append(splitSign);
        	filebuf.append("\r\n");
		}
    	FileUtil.getInstance().writeFile(fileName, filebuf.toString());
		return staxpayname;
    	
    	
    }
    
	/**
	 * Direction: ȫѡ/��ѡ ename: selectAllOrNone ���÷���: viewers: * messages:
	 */
	public String selectAllOrNone(Object o) {
		/*if (this.pagingcontext == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.pagingcontext.getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvFinIncomeDetailDto> templist = page.getData();
		if (templist != null && this.excheckList != null) {
			if (excheckList.size() != 0 && excheckList.containsAll(templist)) {
				excheckList.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (excheckList.contains(templist.get(i))) {
						excheckList.set(excheckList.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						excheckList.add(i, templist.get(i));
					}
				}
			}
		}*/
		if(excheckList.size() > 0){
			excheckList=new ArrayList<TvFinIncomeDetailDto>();
			editor.fireModelChanged();
			return super.selectAllOrNone(o);
    	}
		excheckList=new ArrayList<TvFinIncomeDetailDto>();
		excheckList.addAll(queryforSelect().getData());
		editor.fireModelChanged();
		return super.selectAllOrNone(o);
	}

	 public PageResponse queryforSelect() {
	    	List list = new ArrayList();
	    	PageRequest pageRequest = new PageRequest();
	    	if(this.pagingcontext != null) {
	    		pageRequest.setPageSize(this.getPagingcontext().getPage().getTotalCount());
	    	}else {
	    		pageRequest.setPageSize(20);
	    	}  	    	
	    	PageResponse resp = null ;
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
	    		resp = commonDataAccessService.findRsByDtoWithWherePaging(dto, pageRequest, where);	
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
			return resp;
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
		TvFinIncomeDetailBean.log = log;
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

	public List<TsTaxorgDto> getQuerydtolist() {
		return querydtolist;
	}

	public void setQuerydtolist(List<TsTaxorgDto> querydtolist) {
		this.querydtolist = querydtolist;
	}

	public List<TvFinIncomeDetailDto> getExcheckList() {
		return excheckList;
	}

	public void setExcheckList(List<TvFinIncomeDetailDto> excheckList) {
		this.excheckList = excheckList;
	}

	public String getStaxpayname() {
		return staxpayname;
	}

	public void setStaxpayname(String staxpayname) {
		this.staxpayname = staxpayname;
	}


}