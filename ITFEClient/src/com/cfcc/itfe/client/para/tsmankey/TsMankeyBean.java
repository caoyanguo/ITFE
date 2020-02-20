package com.cfcc.itfe.client.para.tsmankey;

import itferesourcepackage.TskeyorgnameEnumFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-06-25 16:34:52 ��ϵͳ: Para ģ��:tsmankey ���:TsMankey
 */
public class TsMankeyBean extends AbstractTsMankeyBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsMankeyBean.class);
	private ITFELoginInfo loginfo;
	private String repeatencryptkey;
	private List<TsMankeyDto> checkList = new ArrayList<TsMankeyDto>();
	List<TdEnumvalueDto> list = new ArrayList<TdEnumvalueDto>();
	java.sql.Date daffdate;
	List<Mapper> maplist;
	String funcinfo;
	String skeymode;
	String skeyorgcode;
	Date current;
	public TsMankeyBean() {
		super();
		dto = new TsMankeyDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		pagingContext = new PagingContext(this);
		repeatkey = "";
		repeatencryptkey = "";
		skeymode = loginfo.getMankeyMode();
		TskeyorgnameEnumFactory  list2 = new TskeyorgnameEnumFactory();
		maplist = list2.getEnums(null);
		current = com.cfcc.jaf.common.util.DateUtil.currentDate();
		daffdate =com.cfcc.jaf.common.util.DateUtil.difDate((java.sql.Date) current, 1);
		funcinfo="�Զ���ȡ��Կ���� �����ǴӲ���������Ϣά�������ջ��ض���ά��������������֧���кŶ�Ӧ��ϵ���л�ȡTCBS����ش���������Կ���ɡ���Կ���¼�����  �Ǹ���ѡ���������Կ����������Կ�ļ�";
		list = getorglist(skeymode);
		init();
	
	}

	/**
	 * Direction: ת����Կ����¼�� ename: toKeysave ���÷���: viewers: ��Կ����¼�� messages:
	 */
	public String toKeysave(Object o) {
		if(StateConstant.KEY_ALL.equals(loginfo.getMankeyMode()) && !StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) { //�����ȫʡͳһ������Ҫ������������ά���ò���
			MessageDialog.openMessageDialog(null, "Ŀǰ��Կά��ģʽΪȫʡͳһ�������Ĺ���Ա���ü���!");
			return "";
		}
		dto = new TsMankeyDto();
		repeatkey = "";
		repeatencryptkey = "";
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSkeymode(skeymode);
		dto.setImodicount(StateConstant.KEYORG_STATUS_AFF);
		return super.toKeysave(o);
	}

	/**
	 * Direction: ��Կ����ɾ�� ename: keyDelete ���÷���: viewers: * messages:
	 */
	public String keyDelete(Object o) {
		if (null == checkList || checkList.size() != 1) {
			MessageDialog.openMessageDialog(null, "ɾ����Կʱһ��ֻ��ѡ��һ����¼��");
			checkList = new ArrayList<TsMankeyDto>();
			return null;
		} else {
			dto = checkList.get(0);
		}
		if (!dto.getSorgcode().equals(loginfo.getSorgcode())) {
			MessageDialog.openMessageDialog(null, "��û�б�����¼�Ĳ���Ȩ�ޣ�");
			return null;
		}
		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openQuestion(
				null, "��ʾ��Ϣ", "��ȷ��ɾ��������¼��");
		if (!flag) {
			return null;
		}
		try {
			tsMankeyService.keyDelete(dto);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.DELETEFAIL);
			return null;
		}
		dto = new TsMankeyDto();
		init();
		checkList = new ArrayList<TsMankeyDto>();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.keyDelete(o);
	}

	/**
	 * Direction: ת����Կ�����޸� ename: toKeymodify ���÷���: viewers: ��Կ�����޸� messages:
	 */
	public String toKeymodify(Object o) {

		if (null == checkList || checkList.size() != 1) {
			MessageDialog.openMessageDialog(null, "�޸���Կʱһ��ֻ��ѡ��һ����¼��");
			checkList =new ArrayList<TsMankeyDto>();
			return null;
		} else {
			dto = checkList.get(0);
		}
		if (!dto.getSorgcode().equals(loginfo.getSorgcode())) {
			if (loginfo.getSorgcode().equals("000000000000")) {
				skeymode = dto.getSkeymode();
				return super.toKeymodify(o);
			}
			MessageDialog.openMessageDialog(null, "��û�б�����¼�Ĳ���Ȩ�ޣ�");
			return null;
		}
		
		skeymode =dto.getSkeymode();
		
		repeatkey = dto.getSkey();
		repeatencryptkey = dto.getSencryptkey();
		dto.setSorgcode(loginfo.getSorgcode());
		if (!AdminConfirmDialogFacade.open("�޸���Կ��Ҫ���������Ȩ��")) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
		return super.toKeymodify(o);
	}

	/**
	 * Direction: ��Կ����¼�� ename: keySave ���÷���: viewers: ��Կ�����б� messages:
	 */
	public String keySave(Object o) {
		if (null == dto || dto.getSkeyorgcode()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
			return null;
		} else if (isExists()) {
			MessageDialog.openMessageDialog(null, StateConstant.PRIMAYKEY);
			return null;
		} else if (!repeatkey.equals(dto.getSkey())) {
			MessageDialog.openMessageDialog(null, "ȷ��ǩ����Կ�����ǩ����Կ��һ�£�");
			return null;
		} else if ((repeatencryptkey != null && dto.getSencryptkey() != null)
				&& !repeatencryptkey.equals(dto.getSencryptkey())) {
			MessageDialog.openMessageDialog(null, "ȷ�ϼ�����Կ�����������Կ��һ�£�");
			return null;
		}
		dto.setDaffdate((java.sql.Date) current);
		dto.setSnewkey(dto.getSkey());
		dto.setSnewencryptkey(dto.getSkey());
		try {
			tsMankeyService.keySave(dto);
			MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.INPUTFAIL);
			return null;
		}
		dto = new TsMankeyDto();
		init();
		return super.keySave(o);
	}

	@SuppressWarnings("unchecked")
	public boolean isExists() {
		TsMankeyDto tempdto = new TsMankeyDto();
		try {
			tempdto.setSorgcode(dto.getSorgcode());
			tempdto.setSkeyorgcode(dto.getSkeyorgcode());
			tempdto.setSkeymode(dto.getSkeymode());
			List list = commonDataAccessService.findRsByDto(tempdto);
			if (list != null && list.size() > 0) {
				return true;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return false;
	}

	/**
	 * Direction: ת����Կ�����б� ename: toKeylist ���÷���: viewers: ��Կ�����б� messages:
	 */
	public String toKeylist(Object o) {
		dto = new TsMankeyDto();
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.toKeylist(o);
	}

	/**
	 * Direction: ��Կ�����޸� ename: keyModify ���÷���: viewers: ��Կ�����б� messages:
	 */
	public String keyModify(Object o) {
//		if (null == dto || null == dto.getSkeymode()
//				|| dto.getSkeymode().equals("") || dto.getSkey().equals("")
//				|| null == dto.getSkey()) {
//			MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
//			return null;
//		} else 
		if (dto.getSnewkey()==null||"".equals(dto.getSnewkey())) {
			MessageDialog.openMessageDialog(null, "������Կ����Ϊ�գ�");
			return null;
		}else if ((dto.getSnewkey() != null && dto.getSnewencryptkey() != null)
				&& !dto.getSnewencryptkey().equals(dto.getSnewkey())) {
			MessageDialog.openMessageDialog(null, "ȷ�ϼ�����Կ�����������Կ��һ�£�");
			return null;
		}
		try {
//			dto.setDaffdate((java.sql.Date) current);
//			dto.setSnewkey(dto.getSkey());
//			dto.setSnewencryptkey(dto.getSkey());
			tsMankeyService.keyModify(dto);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYFAIL);
			return null;
		}
		checkList = new ArrayList<TsMankeyDto>();
		dto = new TsMankeyDto();
		init();
		return super.keyModify(o);
	}

	/**
	 * Direction: ����ѡ�ж��� ename: clickSelect ���÷���: viewers: * messages:
	 */
	public String clickSelect(Object o) {
		dto = (TsMankeyDto) o;
		return super.clickSelect(o);
	}

	/**
	 * Direction: ������Կ������ ename: updateAndExport ���÷���: viewers: * messages:
	 */
	public String updateAndExport(Object o) {
		if (checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ������Կ�Ļ�����¼��");
			return null;
		}
//		MessageDialog.openMessageDialog(null, "���Խ׶���Կ������Ч���ڵ����ƣ���������Կ��ʱ��Ч");
		org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
				editor.getContainer().getShell(), "���»�����Կ��ʾ", null,
				"�Ƿ�ȷ��Ҫ����ѡ���������Կ,������Կ���뼰ʱ����Կ�ļ��·���ص�λ\n"
						+ " ��������˫��ά����Կ��һ�£�Ӱ��ҵ���������У������ش���",
				org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
						"��", "��" }, 0);
		if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
			if (null == daffdate) {
				MessageDialog.openMessageDialog(null, "��¼������Կ����Ч���ڣ�");
				return null;
			} else if (daffdate.compareTo(com.cfcc.jaf.common.util.DateUtil
					.currentDate()) <= 0) {
				MessageDialog.openMessageDialog(null, "��Ч������Ҫ���ڵ�ǰ����");
				return null;
			}
			String filePath="C:";
			String dirsep = File.separator;
			String clientFilePath = null;
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			try {
				List<String> filelist = tsMankeyService.updateAndExport(checkList,
						daffdate);
				for (int i = 0; i < filelist.size(); i++) {
					String serverFilePath = filelist.get(i).replace("\\", "/");
					int j = serverFilePath.lastIndexOf("/");
					String partfilepath = serverFilePath.replaceAll(
							serverFilePath.substring(0, j + 1), "");
					clientFilePath = filePath + dirsep + strdate
							+ dirsep + partfilepath;
					File f = new File(clientFilePath);
					File dir = new File(f.getParent());
					if (!dir.exists()) {
						dir.mkdirs();
					}
					if (f.exists()) {
						f.delete();
					}
					ClientFileTransferUtil.downloadFile(serverFilePath,
							clientFilePath);

				}
				if (filelist.size() > 0) {
					init();
					editor.fireModelChanged();
					MessageDialog.openMessageDialog(null, "��Կ�Ѹ���,������"+ filelist.size() + "���ļ�,"+
							"������"+filePath+ dirsep + strdate+ dirsep+"Ŀ¼��\n����Կ��Ч���� [" + daffdate+" ],�뼰ʱ�·���ص�λ");
					
				} else {
					MessageDialog.openMessageDialog(null, "��Կû�и��£�");
				}

			} catch (Exception e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
		}else{
			MessageDialog.openMessageDialog(null, "��Կδ���£�");
		}
		checkList = new ArrayList<TsMankeyDto>();
		editor.fireModelChanged();
		return null;
	}

	/**
	 * Direction: �Զ���ȡ��Ʊ��λ ename: autoGetBillOrg ���÷���: viewers: * messages:
	 */
	public String autoGetBillOrg(Object o) {
		try {
			List crelist = tsMankeyService.autoGetBillOrg();
			init();
			editor.fireModelChanged();
			checkList =new ArrayList<TsMankeyDto>();
			MessageDialog.openMessageDialog(null, "��ȡ��Ʊ��λ��Ϣ�ɹ�!,����ȡ"
					+ crelist.size() + "��¼");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
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
			dto.setSorgcode(loginfo.getSorgcode());
			return tsMankeyService.keyList(dto, pageRequest);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingContext.setPage(pageResponse);
		
	}
	public java.sql.Date getDaffdate() {
		return daffdate;
	}

	public void setDaffdate(java.sql.Date daffdate) {
		this.daffdate = daffdate;
	}

	public String getSkeyorgcode() {
		return skeyorgcode;
	}

	public void setSkeyorgcode(String skeyorgcode) {
		this.skeyorgcode = skeyorgcode;
	}

	public String getRepeatencryptkey() {
		return repeatencryptkey;
	}

	public void setRepeatencryptkey(String repeatencryptkey) {
		this.repeatencryptkey = repeatencryptkey;
	}



	public String getSkeymode() {
		return skeymode;
	}

	public void setSkeymode(String skeymode) {
		this.skeymode = skeymode;
		list = getorglist(skeymode);
		editor.fireModelChanged();

	}
	/**
	 * Direction: ȫѡ
	 * ename: allSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String allSelect(Object o){
        if (checkList.size()>0) {
			checkList = new ArrayList<TsMankeyDto>();
		}else{
			checkList = pagingContext.getPage().getData();
		}
    	editor.fireModelChanged();
        return "";
    }
    
    /**
	 * Direction: ����Ч��Կ����
	 * ename: affKeyExport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String affKeyExport(Object o){
    	if (checkList.size()>0) {
    		String filePath="C:";
			String dirsep = File.separator;
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String msg = "";
			int i=0,j=0;
    		for (TsMankeyDto _dto :checkList) {
    			if (_dto.getImodicount().compareTo(StateConstant.KEYORG_STATUS_NO)==0) {
    				i++;
    				continue;
				}
    			String stitle =_dto.getSorgcode()+"_"+_dto.getSkeyorgcode()+".KEY";
    			String fileName =filePath+dirsep+strdate+dirsep+stitle;
    			File file = new File(fileName);
    			File dir = new File(file.getParent());
    			FileOutputStream output = null;
    				if (!dir.exists()) {
    					dir.mkdirs();
    				}
    				try {
						output = new FileOutputStream(fileName, false);
						output.write(_dto.getSkey().getBytes("GBK"));
						output.close();
					} catch (Exception e) {
						MessageDialog.openErrorDialog(null, e);
						return null;
					} 
				j++;	
			}
    		if (j>0) {
    			msg+="������"+j+"����Կ�ļ�������"+filePath+dirsep+strdate;
    		}	
    		if (i>0) {
    			msg+="��"+i+"��������Կ��δ���ɣ����ܵ���";
			}
			MessageDialog.openMessageDialog(null, msg);
			checkList = new ArrayList<TsMankeyDto>();
		}else{
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ������ ��Կ����");
			checkList = new ArrayList<TsMankeyDto>();
		}
    	editor.fireModelChanged();
        return "";
    }

	public List<TdEnumvalueDto> getList() {
		return list;
	}

	public void setList(List<TdEnumvalueDto> list) {
		this.list = list;
	}

	public List<TsMankeyDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TsMankeyDto> checkList) {
		this.checkList = checkList;
	}
    
	public List<Mapper> getMaplist() {
		return maplist;
	}

	public String getFuncinfo() {
		return funcinfo;
	}

	public void setFuncinfo(String funcinfo) {
		this.funcinfo = funcinfo;
	}

	public void setMaplist(List<Mapper> maplist) {
		this.maplist = maplist;
	}
	
	

	public List<TdEnumvalueDto> getorglist(String keymode) {
		List<TdEnumvalueDto> list = new ArrayList<TdEnumvalueDto>();
		TdEnumvalueDto _dto = new TdEnumvalueDto();
		IItfeCacheService cacheServer = (IItfeCacheService) ServiceFactory
				.getService(IItfeCacheService.class);
		if (StateConstant.KEY_BOOK.equals(keymode)) {
			_dto.setSvalue(loginfo.getSorgcode());
			_dto.setSvaluecmt(loginfo.getSorgName());
			list.add(_dto);

		} else if (StateConstant.KEY_TRECODE.equals(keymode)) {
			TsTreasuryDto dto = new TsTreasuryDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsTreasuryDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsTreasuryDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getStrecode());
						_dto.setSvaluecmt(emuDto.getStrename());
						list.add(_dto);
					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (StateConstant.KEY_BILLORG.equals(keymode)) {
			TsConvertfinorgDto dto = new TsConvertfinorgDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsConvertfinorgDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsConvertfinorgDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getSfinorgcode());
						_dto.setSvaluecmt(emuDto.getSfinorgname());
						list.add(_dto);
					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (StateConstant.KEY_GENBANK.equals(toString())) {
			TsGenbankandreckbankDto dto = new TsGenbankandreckbankDto();
			dto.setSbookorgcode(loginfo.getSorgcode());
			try {
				List<TsGenbankandreckbankDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsGenbankandreckbankDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getSreckbankcode());
						_dto.setSvaluecmt(emuDto.getSreckbankname());
						list.add(_dto);

					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (StateConstant.KEY_TAXORG.equals(toString())) {
			TsTaxorgDto dto = new TsTaxorgDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsTaxorgDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {

					for (TsTaxorgDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getStaxorgcode());
						_dto.setSvaluecmt(emuDto.getStaxorgname());
						list.add(_dto);
					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if(StateConstant.KEY_ALL.equals(keymode)) {
			_dto.setSvalue(StateConstant.ORG_CENTER_CODE);
			_dto.setSvaluecmt("���Ĺ������");
			list.add(_dto);
		}
		return list;

	}
}