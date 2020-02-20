package com.cfcc.itfe.client.dataquery.bizdatasearch;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.bizdatasearch.IBizdatasearchService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author Administrator
 * @time   12-05-30 15:23:35
 * ��ϵͳ: DataQuery
 * ģ��:bizdatasearch
 * ���:Bizdatasearch
 */
public class BizdatasearchBean extends AbstractBizdatasearchBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(BizdatasearchBean.class);
    private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private IBizdatasearchService bizdatasearchService = (IBizdatasearchService)getService(IBizdatasearchService.class);
    private ITFELoginInfo loginfo;
    public BizdatasearchBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      trecode = "";
      biztype = "HTV_INFILE";
      starttime = loginfo.getCurrentDate();
      endtime = loginfo.getCurrentDate();
      trecodelist = new ArrayList();
      bizlist = new ArrayList();
      reportpath = "/com/cfcc/itfe/client/ireport/bizdatasearch.jasper";
      init();
                  
    }
    
    private void init(){
    	TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			trecodelist = commonDataAccessService.findRsByDto(tredto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		

		
		TdEnumvalueDto valuedto0 = new TdEnumvalueDto();
		valuedto0.setStypecode("����˰Ʊ");
		valuedto0.setSvalue("HTV_INFILE");
		this.bizlist.add(valuedto0);
		
		TdEnumvalueDto valuedto1 = new TdEnumvalueDto();
		valuedto1.setStypecode("ֱ��֧�����");
		valuedto1.setSvalue("HTV_DIRECTPAYMSGMAIN");
		this.bizlist.add(valuedto1);
		
		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("��Ȩ֧�����");
		valuedto2.setSvalue("HTV_GRANTPAYMSGMAIN");
		this.bizlist.add(valuedto2);
		
		TdEnumvalueDto valuedto3= new TdEnumvalueDto();
		valuedto3.setStypecode("�˿�ҵ��");
		valuedto3.setSvalue("HTV_DWBK");
		this.bizlist.add(valuedto3);
		
		TdEnumvalueDto valuedto4 = new TdEnumvalueDto();
		valuedto4.setStypecode("��������");
		valuedto4.setSvalue("HTV_PAYOUTFINANCE_MAIN");
		this.bizlist.add(valuedto4);
    }
    
    /**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	reportlist = new ArrayList();
    	reportmap = new HashMap();
    	resultlist = new ArrayList();
    	try {
			resultlist = bizdatasearchService.getResult(createsqlwhere(biztype));
			if(null == resultlist || resultlist.size() == 0 ){
				resultlist = new ArrayList();
				MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
			}
		} catch (ITFEBizException e) {
			log.error("��ѯ���ݿ�ʧ�ܣ�");
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
        return super.search(o);
    }
    
    
    
    /**
	 * Direction: ��ӡ
	 * ename: print
	 * ���÷���: 
	 * viewers: ��ӡ���
	 * messages: 
	 */
    public String print(Object o){
        if(null != resultlist && resultlist.size() > 0){
        	reportlist.addAll(resultlist);
        }else{
        	MessageDialog.openMessageDialog(null, "û����Ҫ��ӡ�����ݣ�");
        	return null;
        }
        return super.print(o);
    }
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    
    
    private String  createsqlwhere(String biztype){
    	StringBuffer sql = new StringBuffer();
    	if("HTV_INFILE".equals(biztype)){
    		sql.append("SELECT S_RECVTRECODE AS strecode,sum(N_MONEY) AS summoney,count(1) AS conuts,S_TAXORGCODE AS taxcode from " + biztype + " where (1=1) ");
    		sql.append("  and (S_ACCDATE between '" + starttime.replaceAll("-", "") + "' and  '" + endtime.replaceAll("-", "") +"')");
    		if(null != trecode && trecode.length() > 0){
    			sql.append(" and (S_RECVTRECODE = '" + trecode + "') ");
    		}else{
    			sql.append(" and (S_RECVTRECODE in " + createTrecodeWherer() + ") ");
    		}
    		sql.append(" GROUP BY S_RECVTRECODE,S_TAXORGCODE");
    		reportmap.put("title", "����˰Ʊͳ�ƽ��");
    		return sql.toString();
    	}else if("HTV_DIRECTPAYMSGMAIN".equals(biztype) || "HTV_GRANTPAYMSGMAIN".equals(biztype)){
    		sql.append("SELECT S_TRECODE AS strecode,sum(N_MONEY) AS summoney,count(1) AS conuts,S_PAYUNIT AS taxcode FROM " + biztype + " where (1=1) ");
    		sql.append("  and (S_ACCDATE between '" + starttime.replaceAll("-", "") + "' and  '" + endtime.replaceAll("-", "") +"')");
    		if(null != trecode && trecode.length() > 0){
    			sql.append(" and (S_TRECODE = '" + trecode +"')");
    		}else{
    			sql.append(" and (S_TRECODE in " + createTrecodeWherer() + ") ");
    		}
    		sql.append(" GROUP BY S_TRECODE,S_PAYUNIT");
    		if("HTV_DIRECTPAYMSGMAIN".equals(biztype))
    			reportmap.put("title", "ֱ��֧�����ͳ�ƽ��");
    		else{
    			reportmap.put("title", "��Ȩ֧�����ͳ�ƽ��");
    		}
    		return sql.toString();
    	}else if("HTV_DWBK".equals(biztype)){
    		sql.append("SELECT S_PAYERTRECODE AS strecode,sum(F_AMT) AS summoney,count(1) AS conuts, S_TAXORGCODE AS taxcode from " + biztype + " where (1=1) ");
    		sql.append("  and (D_ACCEPT between '" + starttime + "' and  '" + endtime +"')");
    		if(null != trecode && trecode.length() > 0){
    			sql.append(" and (S_PAYERTRECODE = '" + trecode + "') ");
    		}else{
    			sql.append(" and (S_PAYERTRECODE in " + createTrecodeWherer() + ")");
    		}
    		sql.append(" GROUP BY S_PAYERTRECODE,S_TAXORGCODE");
    		reportmap.put("title", "�˿�ƾ֤ͳ�ƽ��");
    		return sql.toString();
    	}else if("HTV_PAYOUTFINANCE_MAIN".equals(biztype)){
    		sql.append("SELECT S_TRECODE AS strecode,sum(N_AMT) AS summoney,count(1) AS conuts,S_BILLORG AS taxcode from " + biztype + " where (1=1) ");
    		sql.append("  and (S_ENTRUSTDATE between '" + starttime.replaceAll("-", "") + "' and  '" + endtime.replaceAll("-", "") +"')");
    		if(null != trecode && trecode.length() > 0){
    			sql.append(" and (S_TRECODE = '" + trecode + "') ");
    		}else{
    			sql.append(" and (S_TRECODE in " + createTrecodeWherer() + ")");
    		}
    		sql.append(" GROUP BY S_TRECODE,S_BILLORG");
    		reportmap.put("title", "��������ͳ�ƽ��");
    		return sql.toString();
    	}else{
    		return null;
    	}
    }
    
    private String createTrecodeWherer(){
    	StringBuffer treBuffer = new StringBuffer();
    	treBuffer.append("(");
    	for(int i = 0; i < trecodelist.size() ; i ++ ){
    		TsTreasuryDto tmp = (TsTreasuryDto) trecodelist.get(i);
    		treBuffer.append("'" + tmp.getStrecode() + "',");
    	}
    	return treBuffer.toString().substring(0, treBuffer.toString().length() -1 ) + ")";
    }
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}
	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
    
    

}