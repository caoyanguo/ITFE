package com.cfcc.itfe.client.para.tsfinmovepaysub;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   12-06-26 14:44:55
 * ��ϵͳ: Para
 * ģ��:tsfinmovepaysub
 * ���:TsFinmovepaysub
 */
public class TsFinmovepaysubBean extends AbstractTsFinmovepaysubBean implements IPageDataProvider {


	private static Log log = LogFactory.getLog(TsFinmovepaysubBean.class);
    private ITFELoginInfo loginfo;
    private TsFinmovepaysubDto updto;
    public TsFinmovepaysubBean() {
      super();
      dto = new TsFinmovepaysubDto();
      updto = new TsFinmovepaysubDto();
      loginfo=(ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      pagingContext = new PagingContext(this);
      init();           
    }
    
	/**
	 * Direction: ת����������֧��¼��
	 * ename: toFinmovepaysave
	 * ���÷���: 
	 * viewers: ��������֧��¼��
	 * messages: 
	 */
    public String toFinmovepaysave(Object o){
    	updto = new TsFinmovepaysubDto();
    	updto.setSorgcode(loginfo.getSorgcode());
          return super.toFinmovepaysave(o);
    }

	/**
	 * Direction: ��������֧��ɾ��
	 * ename:finmovepayDelete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String finmovepayDelete(Object o){
    	if(null==updto||null==updto.getSorgcode())
    	{
    		MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
    		return null;
    	}else if(!loginfo.getSorgcode().equals(updto.getSorgcode())){
    		if(loginfo.getSorgcode().equals("000000000000"))
    		{
    			try {
    				tsFinmovepaysubService.finmovepayDelete(updto);
    			} catch (ITFEBizException e) {
    				log.error(e);
    				MessageDialog.openErrorDialog(null, e);
    			}
    			updto=new TsFinmovepaysubDto();
    	    	init();
    	    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    	        return super.finmovepayDelete(o);
    		}
    		MessageDialog.openMessageDialog(null, "��Ա�����¼û�в���Ȩ�ޣ�");
    		return null;
    	}else if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��ʾ��Ϣ", "��ȷ��ɾ��������¼��")){
    		return null;
    	}
    	try {
			tsFinmovepaysubService.finmovepayDelete(updto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		updto=new TsFinmovepaysubDto();
    	init();
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
          return super.finmovepayDelete(o);
    }

	/**
	 * Direction: ת����������֧���޸�
	 * ename: toFinmovepaymodify
	 * ���÷���: 
	 * viewers: ��������֧���޸�
	 * messages: 
	 */
    public String toFinmovepaymodify(Object o){
    	if(null==updto||null==updto.getSorgcode())
    	{
    		MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
    		return null;
    	}else if(!loginfo.getSorgcode().equals(updto.getSorgcode())){
    		if(loginfo.getSorgcode().equals("000000000000"))
    		{
    			 return super.toFinmovepaymodify(o);
    		}
    		MessageDialog.openMessageDialog(null, "��Ա�����¼û�в���Ȩ�ޣ�");
    		return null;
    	}
          return super.toFinmovepaymodify(o);
    }

	/**
	 * Direction: ��������֧��¼��
	 * ename: finmovepaySave
	 * ���÷���: 
	 * viewers: ��������ָ���б�
	 * messages: 
	 */
    public String finmovepaySave(Object o){
    	if(null==updto.getSsubjectcode()||updto.getSsubjectcode().equals("")
    	  ||null==updto.getSsubjectname()||updto.getSsubjectname().equals(""))
    	{
    		MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
    		return null;
    	}
    	else if(isExists())
    	{
    		MessageDialog.openMessageDialog(null, StateConstant.PRIMAYKEY);
    		return null;
    	}
    	try {
			tsFinmovepaysubService.finmovepaySave(updto);
			MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		updto=new TsFinmovepaysubDto();
    	init();
          return super.finmovepaySave(o);
    }
    
    /**
     * 
     * �ж�¼��������Ƿ��Ѿ�����
     * @return true����false
     */
    @SuppressWarnings("unchecked")
	public boolean isExists(){
    	TsFinmovepaysubDto tempdto=new TsFinmovepaysubDto();
    	tempdto.setSorgcode(loginfo.getSorgcode());
    	tempdto.setSsubjectcode(updto.getSsubjectcode());
    	try {
			List list=commonDataAccessService.findRsByDto(tempdto);
			if(list!=null&&list.size()>0){
				return true;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
    	return false;
    }
    
    

	/**
	 * Direction: ת����������ָ���б�
	 * ename: toFinmovepaylist
	 * ���÷���: 
	 * viewers: ��������ָ���б�
	 * messages: 
	 */
    public String toFinmovepaylist(Object o){
    	updto=new TsFinmovepaysubDto();
    	init();
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
          return super.toFinmovepaylist(o);
    }

	/**
	 * Direction: ��������֧���޸�
	 * ename: finmovepayModify
	 * ���÷���: 
	 * viewers: ��������ָ���б�
	 * messages: 
	 */
    public String finmovepayModify(Object o){
    	if(null==updto.getSsubjectname()||updto.getSsubjectname().equals(""))
    	{
    		MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
    		return null;
    	}
    	try {
			tsFinmovepaysubService.finmovepayModify(updto);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		updto=new TsFinmovepaysubDto();
    	init();
          return super.finmovepayModify(o);
    }

	/**
	 * Direction: ����ѡ�ж���
	 * ename: clickSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String clickSelect(Object o){
    	updto=(TsFinmovepaysubDto) o;
          return super.clickSelect(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
    		dto.setSorgcode(loginfo.getSorgcode());
			return tsFinmovepaysubService.finmovepayList(pageRequest, dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

    
    public void init()
    {
    	PageRequest pageRequest=new PageRequest();
    	PageResponse pageResponse=retrieve(pageRequest);
    	pagingContext.setPage(pageResponse);
    }

	public TsFinmovepaysubDto getUpdto() {
		return updto;
	}

	public void setUpdto(TsFinmovepaysubDto updto) {
		this.updto = updto;
	}
}