package com.cfcc.itfe.client.para.tsbankcodeconverttype;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author sunyan
 * @time   13-10-16 17:35:08
 * ��ϵͳ: Para
 * ģ��:TsBankcodeConverttype
 * ���:TsConvertbanktype
 */
public class TsConvertbanktypeBean extends AbstractTsConvertbanktypeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsConvertbanktypeBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    private PagingContext pagingcontext = new PagingContext(this);
    
    /**
	 * TsConvertbanktypeDto
	 */
    private TsConvertbanktypeDto dto = null;
    // ��ѯdto
	private TsConvertbanktypeDto querydto = null;
	
	
    public TsConvertbanktypeBean() {
      super();
      querydto = new TsConvertbanktypeDto();
      querydto.setSorgcode(loginfo.getSorgcode());
      dto = new TsConvertbanktypeDto();
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: queryConvert
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String queryConvert(Object o){
	      PageRequest pageRequest = new PageRequest();
		  PageResponse pageResponse = retrieve(pageRequest);
		  pagingcontext.setPage(pageResponse);
          return super.queryConvert(o);
    }
    
    /**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TsConvertbanktypeDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
    }
    
    /**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
		if(datacheck(dto)){
			return null;
		}
    	try {
    		  tsConvertbanktypeService.addInfo(dto);
		    } catch (Exception e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			this.queryConvert(o);
			return super.inputSave(o);
		}
		dto = new TsConvertbanktypeDto();
		this.queryConvert(o);
		return super.backMaintenance(o);
    }
    
    /**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TsConvertbanktypeDto();
    	this.queryConvert(o);
		return super.backMaintenance(o);
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	  dto = (TsConvertbanktypeDto) o;
          return super.singleSelect(o);
    }
    
    /**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
        
    	if (null == dto.getStrecode() || "".equals(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// ��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ��ļ�¼��")) {
			dto=new TsConvertbanktypeDto();
			return "";
		}
		try {
			tsConvertbanktypeService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		queryConvert(null);
		editor.fireModelChanged();
		this.queryConvert(o);
		return super.delete(o);
    }    
    
    /**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getStrecode() || "".equals(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}
    
	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if(datacheck(dto)){
			return null;
		}
		try {
    			tsConvertbanktypeService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConvertbanktypeDto();
		this.queryConvert(o);
		return super.backMaintenance(o);
	}
	
	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ��Ϣ��ѯ messages:
	 */
	public String goBack(Object o) {
		dto = new TsConvertbanktypeDto();
		this.queryConvert(o);
		return super.goBack(o);
	}
	
	@Override
	public String expfile(Object o) {
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_�к��б���չ�ϵ.csv";
		try {
			List<TsConvertbanktypeDto> list = commonDataAccessService.findRsByDto(querydto);
			if(null == list || list.size() == 0 ){
				MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ�");
				return null;
			}
			StringBuffer result = new StringBuffer();
			result.append("��������_VARCHAR_NOT NULL,�������_VARCHAR_NOT NULL," +
					"������������_VARCHAR,�������д���_CHARACTER_NOT NULL,�����б����_VARCHAR_NOT NULL\n");
			for(TsConvertbanktypeDto tmp : list ){
				result.append(tmp.getSorgcode() + ",");		//��������
				result.append(tmp.getStrecode() + ",");		//�������
				result.append(tmp.getSbankname() + ",");	//������������
				result.append(tmp.getSbankcode() + ",");    //�������д���
				result.append(tmp.getSbanktype() + ",\n");	//�����б���루ƾ֤�ⶨ��3λ���֣�
			}
			FileUtil.getInstance().writeFile(fileName, result.toString());
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.expfile(o);
	}
	
	private boolean datacheck(TsConvertbanktypeDto dto) {
		if(!isNumber(dto.getSbankcode())){
			MessageDialog.openMessageDialog(null, "���������кű���Ϊ���֡�");
			return true;
		}
		if(!isNumber(dto.getSbanktype())){
			MessageDialog.openMessageDialog(null, "�����б����Ϊ���֡�");
			return true;
		}

		return false;
	}
	
	/**
	 * �ж��ַ����Ƿ����������
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
           return false;
        }
        else
        {
           return true;
        }
    }
	
	
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
			querydto.setSorgcode(loginfo.getSorgcode());
			return commonDataAccessService.findRsByDtoWithWherePaging(querydto,
					pageRequest, "1=1");

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TsConvertbanktypeBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TsConvertbanktypeDto getDto() {
		return dto;
	}

	public void setDto(TsConvertbanktypeDto dto) {
		this.dto = dto;
	}

	public TsConvertbanktypeDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TsConvertbanktypeDto querydto) {
		this.querydto = querydto;
	}

    
}