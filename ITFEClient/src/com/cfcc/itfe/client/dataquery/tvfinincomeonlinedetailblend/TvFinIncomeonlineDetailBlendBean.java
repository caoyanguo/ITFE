package com.cfcc.itfe.client.dataquery.tvfinincomeonlinedetailblend;

import java.util.*;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.SummaryReportDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author zhangliang
 * @time   14-11-13 14:33:12
 * ��ϵͳ: DataQuery
 * ģ��:TvFinIncomeonlineDetailBlend
 * ���:TvFinIncomeonlineDetailBlend
 */
@SuppressWarnings("unchecked")
public class TvFinIncomeonlineDetailBlendBean extends AbstractTvFinIncomeonlineDetailBlendBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvFinIncomeonlineDetailBlendBean.class);
    private TvIncomeonlineIncomeondetailBlendDto dto = null;
    private TvIncomeonlineIncomeondetailBlendDto editdto = null;
    private TvIncomeonlineIncomeondetailBlendDto savedto = null;
    private List selecteddatalist=null;
    private String equalscount = null;
    private String notequalscount = null;
    private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    private String sfinorgcode = "";
    private String strecode = null;
    private List treCodeList = null;
    private List<TsConvertfinorgDto> finorgList = null;
    private List<TsTaxorgDto> taxorgList = null;
    private PagingContext pagingcontext = null;
    private PagingContext pagingcontextblend = null;
	private String display=null;
	private String allcount=null;
	private String blendcount=null;
	private String remark=null;
    private Map map = null;
    private Map<String,String> ysjcmap = null;
    private Map<String,String> mapysjc = null;
    private Map<String,String> taxtype = null;
    private Map<String,String> typetax = null;
    private Map<String,String> yslx = null;
    private Map<String,String> lxys = null;
    private String sintredateStart = TimeFacade.getCurrentStringTimebefor();
    private String sintredateEnd = TimeFacade.getCurrentStringTimebefor();
    private String sapplydateStart = null;
    private String sapplydateEnd = null;
    private Map<String,String> tremap = null;
    private Map<String,String> trxmap = null;
    private List<TdEnumvalueDto> statelist = null;
    public TvFinIncomeonlineDetailBlendBean() {
      super();
      pagingcontext = new PagingContext(this);
      pagingcontextblend = new PagingContext(this);
      init();
    }
	private void init()
    {
		dto = new TvIncomeonlineIncomeondetailBlendDto();
		editdto = new TvIncomeonlineIncomeondetailBlendDto();
//		dto.setSapplydate(TimeFacade.getCurrentStringTime());
//		dto.setSintredate(TimeFacade.getCurrentStringTimebefor());
//		dto.setSblend(TimeFacade.getCurrentStringTime());
		selecteddatalist = new ArrayList<TvIncomeonlineIncomeondetailBlendDto>();
    	// ��ѯ�����Ӧ���ջ��ش�����dto
		taxorgList = new ArrayList<TsTaxorgDto>();
		TsConvertfinorgDto finorgdto = new TsConvertfinorgDto();
		finorgdto.setSorgcode(loginfo.getSorgcode());
		try {
//			TsTaxorgDto orgdto0 = new TsTaxorgDto();
//			orgdto0.setSorgcode(loginfo.getSorgcode());// ��������
//			orgdto0.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
//			orgdto0.setStaxorgname("�������ջ���");
//			orgdto0.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
//			taxorgList.add(orgdto0);
//
//			TsTaxorgDto orgdto1 = new TsTaxorgDto();
//			orgdto1.setSorgcode(loginfo.getSorgcode());// ��������
//			orgdto1.setStaxorgcode(MsgConstant.MSG_TAXORG_NATION_CLASS);
//			orgdto1.setStaxorgname("��˰");
//			orgdto1.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
//			taxorgList.add(orgdto1);
//
//			TsTaxorgDto orgdto2 = new TsTaxorgDto();
//			orgdto2.setSorgcode(loginfo.getSorgcode());// ��������
//			orgdto2.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
//			orgdto2.setStaxorgname("��˰");
//			orgdto2.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
//			taxorgList.add(orgdto2);
//
//			TsTaxorgDto orgdto3 = new TsTaxorgDto();
//			orgdto3.setSorgcode(loginfo.getSorgcode());// ��������
//			orgdto3.setStaxorgcode(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
//			orgdto3.setStaxorgname("����");
//			orgdto3.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
//			taxorgList.add(orgdto3);
//
//			TsTaxorgDto orgdto4 = new TsTaxorgDto();
//			orgdto4.setSorgcode(loginfo.getSorgcode());// ��������
//			orgdto4.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
//			orgdto4.setStaxorgname("����");
//			orgdto4.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
//			taxorgList.add(orgdto4);
//
//			TsTaxorgDto orgdto5 = new TsTaxorgDto();
//			orgdto5.setSorgcode(loginfo.getSorgcode());// ��������
//			orgdto5.setStaxorgcode(MsgConstant.MSG_TAXORG_OTHER_CLASS);
//			orgdto5.setStaxorgname("����");
//			orgdto5.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
//			taxorgList.add(orgdto5);
			TsTaxorgDto taxorgdto = new TsTaxorgDto();
			taxorgdto.setSorgcode(loginfo.getSorgcode());
			taxorgList.addAll(commonDataAccessService.findRsByDto(taxorgdto));
			if(taxorgList!=null&&taxorgList.size()>0)
			{
				trxmap = new HashMap<String,String>();
				for(TsTaxorgDto tmp:taxorgList)
					trxmap.put(tmp.getStaxorgcode(), tmp.getStaxorgname());
			}
			finorgList = commonDataAccessService.findRsByDto(finorgdto);
			treCodeList=commonDataAccessService.getSubTreCode(loginfo.getSorgcode());
		} catch (Throwable e) {
			log.error(e.getMessage());
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		reportPath = "/com/cfcc/itfe/client/ireport/summaryReport.jasper";
	    reportRs = new ArrayList();
	    reportMap = new HashMap();
	    reportDetailPath = "/com/cfcc/itfe/client/ireport/detailReport.jasper";
	    reportDetailRs = new ArrayList();
	    reportDetailMap = new HashMap();
	    searchArea = "0";
	    detailDto = new TvIncomeonlineIncomeondetailBlendDto();
    }
	/**
	 * Direction: ��ѯ
	 * ename: searchBlend
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchBlend(Object o){
    	display = "blendsearch";
    	selecteddatalist = new ArrayList<TvIncomeonlineIncomeondetailBlendDto>();
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		pagingcontextblend.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.searchBlend(o);
    }
    /**
	 * Direction: ȫѡ��ѡ
	 * ename: selectall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectall(Object o){
    	if(pagingcontextblend.getPage().getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
        if(selecteddatalist!=null&&selecteddatalist.size()>0)
        	selecteddatalist = new ArrayList<TvIncomeonlineIncomeondetailBlendDto>();
        else
        	selecteddatalist.addAll(pagingcontextblend.getPage().getData());
        editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.selectall(o);
    }
	/**
	 * Direction: ����
	 * ename: searchIncomeLineDetail
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchIncomeLineDetail(Object o){
        try {
        	pagingcontext.setPage(new PageResponse());
			
			if(!(sintredateStart!=null&&sintredateStart.equals(sintredateEnd)))
			{
				MessageDialog.openMessageDialog(null, "��⿪ʼ���ں����������ڱ�����ͬ��");
				return null;
			}
			TvIncomeonlineIncomeondetailBlendDto querydto = (TvIncomeonlineIncomeondetailBlendDto)dto.clone();
			querydto.setSintredate(sintredateStart);
			map = tvFinIncomeonlineDetailBlendService.searchBlend(querydto, null);
			equalscount = String.valueOf(map.get("equalscount"));
			notequalscount = String.valueOf(map.get("notequalscount"));
			allcount = String.valueOf(map.get("allcount"));
			blendcount = String.valueOf(map.get("blendcount"));
		} catch (ITFEBizException e) {
			log.error(e.getMessage());
			MessageDialog.openMessageDialog(null, "��ѯ����˰Ʊ�������ˮ��������ʧ�ܣ�"+e.getMessage());
		}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.searchIncomeLineDetail(o);
    }
    /**
	 * Direction: �������ݲ鿴
	 * ename: notequalsdetail
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String notequalsdetail(Object o){
    	display = "notequals";
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.notequalsdetail(o);
    }
    
	/**
	 * Direction: ������ݲ鿴
	 * ename: equalsdetail
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String equalsdetail(Object o){
    	display = "equals";
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.equalsdetail(o);
    }
	/**
	 * Direction: �޸ı�ע
	 * ename: editdemo
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String editdemo(Object o){
        if(selecteddatalist!=null&&selecteddatalist.size()>0)
        {
        	try{
	        	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
	    				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ�Ͻ��˿����ڡ��˿�ƾ֤�š��˿���������ڡ�����ƾ֤�š���������ע���ݱ��浽ѡ�е������б�?"))
	        	{
	        		TvIncomeonlineIncomeondetailBlendDto tempdto = null;
	        		for(int i=0;i<selecteddatalist.size();i++)
	        		{
	        			tempdto = (TvIncomeonlineIncomeondetailBlendDto)selecteddatalist.get(i);
	        			if(tempdto.getSapplydate()==null||"".equals(tempdto.getSapplydate()))
	        				tempdto.setSapplydate(tempdto.getSintredate());
	        			tempdto.setSetpcode(dto.getSetpcode());
	        			tempdto.setSetpname(dto.getSetpname());
	        			tempdto.setSbdgsbtname(dto.getSbdgsbtname());
	        			tempdto.setSremark1(dto.getSremark1());
	        			tempdto.setSremark2(dto.getSremark2());
	        			tempdto.setSremark5(dto.getSremark5());
	        			tempdto.setSremark4(dto.getSremark4());
	        		}
	        		commonDataAccessService.updateDtos(selecteddatalist);
	        	}
        	} catch (ITFEBizException e) {
    			log.error(e.getMessage());
    			MessageDialog.openMessageDialog(null, "���汸עʧ�ܣ�"+e.getMessage());
    			return null;
    		}
        	
        }else
        {
        	MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵ����ݣ�");
        }
        searchBlend(o);
        return super.editdemo(o);
    }
    
	/**
	 * Direction: ɾ��
	 * ename: deleteadddata
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deleteadddata(Object o){
    	if(selecteddatalist!=null&&selecteddatalist.size()>0)
        {
    		TvIncomeonlineIncomeondetailBlendDto tempdto = null;
    		for(int i=0;i<selecteddatalist.size();i++)
    		{
    			tempdto = (TvIncomeonlineIncomeondetailBlendDto)selecteddatalist;
    			if("�˹����".equals(tempdto.getSremark3()))
    			{
    				MessageDialog.openMessageDialog(null, "ѡ�е������а���������������,ֻ���˹��������ݿ�ɾ����");
    				return null;
    			}
    		}
    		try
    		{
	    		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
	    				.getCurrentComposite().getShell(), "��ʾ", "ȷ��ɾ��ѡ�е�����?")) {
	        		for(int i=0;i<selecteddatalist.size();i++)
	        		{
	        			tempdto = (TvIncomeonlineIncomeondetailBlendDto)selecteddatalist;
	        			commonDataAccessService.delete(tempdto);
	        		}
	    		}
    		} catch (ITFEBizException e) {
    			log.error(e.getMessage());
    			MessageDialog.openMessageDialog(null, "ɾ������ʧ�ܣ�"+e.getMessage());
    			return null;
    		}
        }else
        {
        	MessageDialog.openMessageDialog(null, "��ѡ����Ҫɾ�������ݣ�");
        }
    	selecteddatalist = new ArrayList<TvIncomeonlineIncomeondetailBlendDto>();
    	searchBlend(o);
        return super.deleteadddata(o);
    }
    
	/**
	 * Direction: �޸ı���
	 * ename: editdata
	 * ���÷���: 
	 * viewers: ��������ѯ
	 * messages: 
	 */
    public String editdata(Object o){
    	if(editdto!=null&&editdto.getSseq()!=null&&!"".equals(editdto.getSseq()))
    	{
    		try {
    			if(savedto.getSapplydate()==null||"".equals(savedto.getSapplydate()))
    				savedto.setSapplydate(savedto.getSintredate());
    			savedto.setSetpcode(editdto.getSetpcode());
    			savedto.setSetpname(editdto.getSetpname());
    			savedto.setSbdgsbtname(editdto.getSbdgsbtname());
    			savedto.setSremark1(editdto.getSremark1());
    			savedto.setSremark2(editdto.getSremark2());
    			savedto.setSremark5(editdto.getSremark5());
    			savedto.setSremark4(editdto.getSremark4());
    			commonDataAccessService.updateData(savedto);
    		} catch (ITFEBizException e) {
    			log.error(e.getMessage());
    			MessageDialog.openMessageDialog(null, "�޸ı���ʧ�ܣ�"+e.getMessage());
    			return null;
    		}
    	}
        return super.editdata(o);
    }
    
	/**
	 * Direction: �������
	 * ename: blendStorage
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String blendStorage(Object o){
    	try {
    		if(blendcount!=null&&!"".equals(blendcount)&&blendcount.equals(allcount))
    		{
    			MessageDialog.openMessageDialog(null, "������ȫ����⣬�����ظ���⣡");
    			return null;
    		}
    		map.put("remark", remark);//�˹���ע
    		TvIncomeonlineIncomeondetailBlendDto querydto = (TvIncomeonlineIncomeondetailBlendDto)dto.clone();
			querydto.setSintredate(sintredateStart);
			tvFinIncomeonlineDetailBlendService.blendStorage(querydto, map);
		} catch (ITFEBizException e) {
			log.error(e.getMessage());
			MessageDialog.openMessageDialog(null, "�������ʧ�ܣ�"+e.getMessage());
			return null;
		}
		MessageDialog.openMessageDialog(null, "�������ɹ���");
		searchIncomeLineDetail(o);
        return super.blendStorage(o);
    }
    
	/**
	 * Direction: ����
	 * ename: returndefault
	 * ���÷���: 
	 * viewers: ��������ѯ
	 * messages: 
	 */
    public String returndefault(Object o){
    	pagingcontext.setPage(new PageResponse());
    	pagingcontextblend.setPage(new PageResponse());
        return super.returndefault(o);
    }
    
    
	/**
	 * Direction: �޸�ҳ��
	 * ename: goedit
	 * ���÷���: 
	 * viewers: �ֹ�¼����Ϣ
	 * messages: 
	 */
    public String goedit(Object o){
    	 editdto = (TvIncomeonlineIncomeondetailBlendDto)o;
    	 savedto = (TvIncomeonlineIncomeondetailBlendDto)editdto.clone();
//         if("�˹����".equals(editdto.getSremark3()))
         	return super.goedit(o);
//         else
//         	return null; 
    }
    /**
	 * Direction: ¼�����
	 * ename: goadd
	 * ���÷���: 
	 * viewers: ��������¼�����
	 * messages: 
	 */
    public String goadd(Object o){
    	editdto = new TvIncomeonlineIncomeondetailBlendDto();
    	editdto.setSremark3("�˹����");
        return super.goadd(o);
    }
    
	/**
	 * Direction: ���������ܱ���
	 * ename: toSummaryReport
	 * ���÷���: 
	 * viewers: ���������ܱ���
	 * messages: 
	 */
    public String toSummaryReport(Object o){
       
        reportRs.clear();
        reportMap.clear();
        try {
        	TvIncomeonlineIncomeondetailBlendDto  findto = (TvIncomeonlineIncomeondetailBlendDto)dto.clone();
        	findto.setSseq(null);
        	findto.setStrename(null);
        	if(findto.getStrecode()==null||"".equals(findto.getStrecode().trim())){
				List<TsTreasuryDto> findTreCodeList = null;
				if("0".equals(searchArea)){
					TsTreasuryDto tr = new TsTreasuryDto();
					tr.setStrecode(findto.getStrecode());
					findTreCodeList = new ArrayList<TsTreasuryDto>();
					findTreCodeList.add(tr);
				}else if("1".equals(searchArea)){
					TsTreasuryDto tr = new TsTreasuryDto();
					tr.setStrecode(findto.getStrecode());
					findTreCodeList = commonDataAccessService.getSubTreCode(tr);
				}
				StringBuffer temp = new StringBuffer("");
				for(int i=0;i<findTreCodeList.size();i++){
					if(i==(findTreCodeList.size()-1)){
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"'");
					}else{
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"',");
					}
				}
				strecode = temp.toString();
			}else{
				List<TsTreasuryDto> findTreCodeList = null;
				if("0".equals(searchArea)){
					TsTreasuryDto tr = new TsTreasuryDto();
					tr.setStrecode(findto.getStrecode());
					findTreCodeList = new ArrayList<TsTreasuryDto>();
					findTreCodeList.add(tr);
				}else if("1".equals(searchArea)){
					TsTreasuryDto tr = new TsTreasuryDto();
					tr.setStrecode(findto.getStrecode());
					findTreCodeList = commonDataAccessService.getSubTreCode(tr);
				}
				StringBuffer temp = new StringBuffer("");
				for(int i=0;i<findTreCodeList.size();i++){
					if(i==(findTreCodeList.size()-1)){
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"'");
					}else{
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"',");
					}
				}
				strecode = temp.toString();
			}
        	StringBuffer where = new StringBuffer("");
			if(findto.getStaxvouno()!=null&&!"".equals(findto.getStaxvouno()))
			{
				where.append(" and S_TAXVOUNO like '"+findto.getStaxvouno()+"%' ");
				findto.setStaxvouno(null);
			}
			if(sintredateStart!=null&&!"".equals(sintredateStart))
				where.append(" and s_intredate>='"+sintredateStart+"' ");
			if(sintredateEnd!=null&&!"".equals(sintredateEnd))
				where.append(" and s_intredate<='"+sintredateEnd+"' ");
			if(sapplydateStart!=null&&!"".equals(sapplydateStart))
				where.append(" and s_applydate>='"+sapplydateStart+"' ");
			if(sapplydateEnd!=null&&!"".equals(sapplydateEnd))
				where.append(" and s_applydate<='"+sapplydateEnd+"' ");
			where.append(" and S_TRECODE in("+strecode+") ");
			TvIncomeonlineIncomeondetailBlendDto qdto = (TvIncomeonlineIncomeondetailBlendDto)findto.clone();
			qdto.setStrecode(null);
        	reportRs = tvFinIncomeonlineDetailBlendService.findSummaryReport(qdto,where.toString());
        	if(reportRs!=null&&reportRs.size()<=0)
        	{
        		MessageDialog.openMessageDialog(null, "��ѯ����Ϊ�գ�");
        		return null;
        	}else
        	{
        		if(tremap==null)
        			tremap = new HashMap<String,String>();
        		if(treCodeList!=null&&treCodeList.size()>0)
        		{
        			TsTreasuryDto tr = null;
        			for(int i=0;i<treCodeList.size();i++)
        			{
        				tr = (TsTreasuryDto)treCodeList.get(i);
        				tremap.put(tr.getStrecode(), tr.getStrename());
        			}
        			SummaryReportDto sdto = null;
        			for(int i=0;i<reportRs.size();i++)
        			{
        				sdto = (SummaryReportDto)reportRs.get(i);
        				sdto.setSbdgsbtcode(tremap.get(sdto.getSbdgsbtcode()));
        				if(sdto.getSbdgsbtcode()!=null&&!"".equals(sdto.getSbdgsbtcode()))
        					sdto.setSbdgsbtcode(sdto.getSbdgsbtcode().replace("���ҽ��", ""));
        			}
        				
        		}
        	}
        	if(trxmap!=null&&dto.getStaxorgcode()!=null&&!"".equals(dto.getStaxorgcode()))
        		reportMap.put("taxorgname", trxmap.get(dto.getStaxorgcode()));
        	else if(trxmap!=null)
        		reportMap.put("taxorgname", " ȫ��");
        	reportMap.put("reportTitle", tremap.get(dto.getStrecode())+("0".equals(searchArea)?"����":"ȫϽ")+"ͳ����Ϣ");
        	reportMap.put("sorgcode", loginfo.getSuserName());
        	reportMap.put("printDate", loginfo.getCurrentDate());
        	strecode = dto.getStrecode();
		} catch (ITFEBizException e) {
			log.error("��ѯ���������ܱ���ʧ��", e);
			if(e.getCause() == null){
				MessageDialog.openMessageDialog(null, "��ѯ���������ܱ���ʧ�� "+e.getMessage());
			}else{
				MessageDialog.openMessageDialog(null, "��ѯ���������ܱ���ʧ�� "+e.getCause().getMessage());
			}
			return null;
		}
        return super.toSummaryReport(o);
    }
    
	/**
	 * Direction: ���������ϸ����
	 * ename: toDetailReport
	 * ���÷���: 
	 * viewers: ���������ϸ����
	 * messages: 
	 */
    public String toDetailReport(Object o){
    	if(selecteddatalist==null || selecteddatalist.size() == 0){
    		MessageDialog.openMessageDialog(null, "��ѡ�����ݴ�ӡ��ϸ��");
        	return null;
    	}
    	if(yslx==null)
    	{
    		yslx = new HashMap<String,String>();
    		yslx.put("1", "Ԥ����");
    		yslx.put("2", "Ԥ����");
    	}
    	if(lxys==null)
    	{
    		lxys = new HashMap<String,String>();
    		lxys.put("Ԥ����","1");
    		lxys.put("Ԥ����","2");
    	}
    	if(ysjcmap==null)
    	{
    		ysjcmap = new HashMap<String,String>();
    		ysjcmap.put("0", "����");
    		ysjcmap.put("1", "����");
    		ysjcmap.put("2", "ʡ");
    		ysjcmap.put("3", "��");
    		ysjcmap.put("4", "��");
    		ysjcmap.put("5", "��");
    		ysjcmap.put("6", "�ط�");
    		
    	}
    	if(mapysjc==null)
    	{
    		mapysjc = new HashMap<String,String>();
    		mapysjc.put("����","0");
    		mapysjc.put("����","1");
    		mapysjc.put("ʡ","2");
    		mapysjc.put("��","3");
    		mapysjc.put("��","4");
    		mapysjc.put("��","5");
    		mapysjc.put("�ط�","6");
    	}
    	if(taxtype==null)
    	{
    		taxtype = new HashMap<String,String>();
    		taxtype.put("1", "��������");
    		taxtype.put("2", "����Ƿ˰");
    		taxtype.put("3", "�������");
    		taxtype.put("4", "��鲹˰");
    	}
    	if(typetax==null)
    	{
    		typetax = new HashMap<String,String>();
    		typetax.put("��������","1");
    		typetax.put("����Ƿ˰","2");
    		typetax.put("�������","3");
    		typetax.put("��鲹˰","4");
    	}
    	TvIncomeonlineIncomeondetailBlendDto tempdto = null;
    	for(int i=0;i<selecteddatalist.size();i++)
    	{
    		tempdto = (TvIncomeonlineIncomeondetailBlendDto)selecteddatalist.get(i);
	    	tempdto.setSbudgettype(yslx.get(tempdto.getSbudgettype()));
	    	tempdto.setSbdglevel(ysjcmap.get(tempdto.getSbdglevel()));
	    	tempdto.setStaxtype(taxtype.get(tempdto.getStaxtype()));
    	}
        reportDetailRs.clear();
        reportDetailMap.clear();
        reportDetailRs.addAll(selecteddatalist) ;
        reportDetailMap.put("printDate", TimeFacade.getCurrentStringTime());
        reportDetailMap.put("printer", loginfo.getSuserName());
        return super.toDetailReport(o);
    }
    
	/**
	 * Direction: ����
	 * ename: backToSearch
	 * ���÷���: 
	 * viewers: ��������ѯ
	 * messages: 
	 */
    public String backToSearch(Object o){
    	TvIncomeonlineIncomeondetailBlendDto tempdto = null;
    	for(int i=0;i<selecteddatalist.size();i++)
    	{
    		tempdto = (TvIncomeonlineIncomeondetailBlendDto)selecteddatalist.get(i);
    		if(lxys.get(tempdto.getSbudgettype())!=null)
    			tempdto.setSbudgettype(lxys.get(tempdto.getSbudgettype()));
    		if(mapysjc.get(tempdto.getSbdglevel())!=null)
    			tempdto.setSbdglevel(mapysjc.get(tempdto.getSbdglevel()));
    		if(typetax.get(tempdto.getStaxtype())!=null)
    			tempdto.setStaxtype(typetax.get(tempdto.getStaxtype()));
    	}
        return "��������ѯ";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	if (o instanceof TvIncomeonlineIncomeondetailBlendDto) {
			detailDto = (TvIncomeonlineIncomeondetailBlendDto) o;
		}
        return "";
    }
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
    		PageResponse page = new PageResponse(pageRequest);
    		if("notequals".equals(display))
    			page =  commonDataAccessService.findRsByDtoWithWherePaging(new TvFinIncomeDetailDto(),pageRequest, String.valueOf(map.get("notequalswhere")));
    		else if("equals".equals(display))
    			page =  commonDataAccessService.findRsByDtoWithWherePaging(new TvFinIncomeDetailDto(),pageRequest, String.valueOf(map.get("equalswhere")));
    		else if("blendsearch".equals(display))
    		{
    			if(dto.getStrecode()==null||"".equals(dto.getStrecode()))
    				dto.setStrecode(loginfo.getSorgcode().substring(0,10));
    			TvIncomeonlineIncomeondetailBlendDto  findto = (TvIncomeonlineIncomeondetailBlendDto)dto.clone();
    			StringBuffer where = new StringBuffer("");
    			if(findto.getStaxvouno()!=null&&!"".equals(findto.getStaxvouno()))
    			{
    				where.append(" and S_TAXVOUNO like '"+findto.getStaxvouno()+"%' ");
    				findto.setStaxvouno(null);
    			}
    			if(sintredateStart!=null&&!"".equals(sintredateStart))
    				where.append(" and s_intredate>='"+sintredateStart+"' ");
    			if(sintredateEnd!=null&&!"".equals(sintredateEnd))
    				where.append(" and s_intredate<='"+sintredateEnd+"' ");
    			if(sapplydateStart!=null&&!"".equals(sapplydateStart))
    				where.append(" and s_applydate>='"+sapplydateStart+"' ");
    			if(sapplydateEnd!=null&&!"".equals(sapplydateEnd))
    				where.append(" and s_applydate<='"+sapplydateEnd+"' ");
    			if(findto.getStrecode()==null||"".equals(findto.getStrecode()))
    			{
	    			TsTreasuryDto tDto = new TsTreasuryDto();
	    			tDto.setSorgcode(loginfo.getSorgcode());
	    			List<TsTreasuryDto> list = null;
	    			if("0".equals(searchArea)){
    					TsTreasuryDto tr = new TsTreasuryDto();
    					tr.setSorgcode(loginfo.getSorgcode());
    					tr.setStrecode(findto.getStrecode());
    					list = new ArrayList<TsTreasuryDto>();
    					list.add(tr);
    				}else if("1".equals(searchArea)){
    					TsTreasuryDto tr = new TsTreasuryDto();
    					tr.setStrecode(findto.getStrecode());
    					if(!loginfo.getSorgcode().contains("00000002"))
    						list = commonDataAccessService.getSubTreCode(tr);
    				}
	    			if(list!=null && list.size() > 0)
	    			{
	    				where.append(" and (");
	    				for(int i=0;i<list.size();i++)
	    				{
	    					tDto = (TsTreasuryDto)list.get(i);
	    					if(i==0)
	    						where.append(" S_TRECODE='"+tDto.getStrecode()+"' ");
	    					else
	    						where.append(" or S_TRECODE='"+tDto.getStrecode()+"' ");
	    				}
	    				where.append(" ) "); 
	    			}
    			}else
    			{
    				List<TsTreasuryDto> findTreCodeList = null;
    				if("0".equals(searchArea)){
    					TsTreasuryDto tr = new TsTreasuryDto();
    					tr.setSorgcode(loginfo.getSorgcode());
    					tr.setStrecode(findto.getStrecode());
    					findTreCodeList = new ArrayList<TsTreasuryDto>();
    					findTreCodeList.add(tr);
    				}else if("1".equals(searchArea)){
    					TsTreasuryDto tr = new TsTreasuryDto();
    					tr.setStrecode(findto.getStrecode());
    					if(!loginfo.getSorgcode().contains("00000002"))
    						findTreCodeList = commonDataAccessService.getSubTreCode(tr);
    				}
    				if(findTreCodeList!=null && findTreCodeList.size() > 0)
	    			{
	    				where.append(" and (");
	    				TsTreasuryDto tDto = null;
	    				for(int i=0;i<findTreCodeList.size();i++)
	    				{
	    					tDto = (TsTreasuryDto)findTreCodeList.get(i);
	    					if(i==0)
	    						where.append(" S_TRECODE='"+tDto.getStrecode()+"' ");
	    					else
	    						where.append(" or S_TRECODE='"+tDto.getStrecode()+"' ");
	    				}
	    				where.append(" ) "); 
	    			}
    			}
    			TvIncomeonlineIncomeondetailBlendDto qdto = (TvIncomeonlineIncomeondetailBlendDto)findto.clone();
    			qdto.setStrecode(null);
    			qdto.setSseq(sintredateStart);
    			page = commonDataAccessService.findRsByDtoWithWherePaging(qdto,pageRequest, "1=1 "+where);
    		}
    		return page;
    				
		}catch(ITFEBizException e)
		{
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
			return null;
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch (Throwable e) {
			log.error(e);
			Exception e1=new Exception("��ѯ�����쳣��",e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return super.retrieve(pageRequest);
	}
	public TvIncomeonlineIncomeondetailBlendDto getDto() {
		return dto;
	}
	public void setDto(TvIncomeonlineIncomeondetailBlendDto dto) {
		this.dto = dto;
	}
	public List<TsConvertfinorgDto> getFinorgList() {
		return finorgList;
	}
	public void setFinorgList(List<TsConvertfinorgDto> finorgList) {
		this.finorgList = finorgList;
	}
	public List<TsTaxorgDto> getTaxorgList() {
		return taxorgList;
	}
	public void setTaxorgList(List<TsTaxorgDto> taxorgList) {
		this.taxorgList = taxorgList;
	}
	public PagingContext getPagingcontext() {
		return pagingcontext;
	}
	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}
	public String getSfinorgcode() {
		return sfinorgcode;
	}
	public void setSfinorgcode(String sfinorgcode) {
		this.sfinorgcode = sfinorgcode;
		if(sfinorgcode==null||"".equals(sfinorgcode))
		{
			this.strecode = null;
			this.sfinorgcode=null;
			dto.setStrecode(null);
			dto.setSfinorgcode(null);
		}else if(finorgList!=null&&finorgList.size()>0)
		{
			for(TsConvertfinorgDto dto:finorgList)
			{
				if(dto.getSfinorgcode().equals(sfinorgcode))
				{
					this.strecode=dto.getStrecode();
					dto.setStrecode(dto.getStrecode());
					dto.setSfinorgcode(dto.getSfinorgcode());
					break;
				}
			}
		}
	}
	public String getStrecode() {
		return strecode;
	}
	public void setStrecode(String strecode) throws ITFEBizException {
		this.strecode = strecode;
		if(strecode==null||"".equals(strecode))
		{
			this.strecode = null;
			this.sfinorgcode=null;
			dto.setStrecode(null);
			dto.setSfinorgcode(null);
		}
		dto.setStrecode(strecode);
		TsTreasuryDto tr = new TsTreasuryDto();
		tr.setStrecode(dto.getStrecode());
		List slist = commonDataAccessService.findRsByDto(tr);
		if(slist!=null&&slist.size()>0)
		{
			tr = (TsTreasuryDto)slist.get(0);
			TsTaxorgDto taxorgdto = new TsTaxorgDto();
			taxorgdto.setSorgcode(tr.getSorgcode());
			taxorgList=commonDataAccessService.findRsByDto(taxorgdto);
			if(taxorgList!=null&&taxorgList.size()>0)
			{
				trxmap = new HashMap<String,String>();
				for(TsTaxorgDto tmp:taxorgList)
					trxmap.put(tmp.getStaxorgcode(), tmp.getStaxorgname());
			}
		}
//		else if(finorgList!=null&&finorgList.size()>0)
//		{
//			for(TsConvertfinorgDto fdto:finorgList)
//			{
//				if(fdto.getStrecode().equals(strecode))
//				{
//					this.sfinorgcode=fdto.getSfinorgcode();
//					dto.setStrecode(fdto.getStrecode());
//					dto.setSfinorgcode(fdto.getSfinorgcode());
//					break;
//				}
//			}
//		}
		this.editor.fireModelChanged();
	}
	public TvIncomeonlineIncomeondetailBlendDto getEditdto() {
		return editdto;
	}
	public void setEditdto(TvIncomeonlineIncomeondetailBlendDto editdto) {
		this.editdto = editdto;
	}
	public List<TvIncomeonlineIncomeondetailBlendDto> getSelecteddatalist() {
		return selecteddatalist;
	}
	public void setSelecteddatalist(
			List<TvIncomeonlineIncomeondetailBlendDto> selecteddatalist) {
		this.selecteddatalist = selecteddatalist;
	}
	public String getEqualscount() {
		return equalscount;
	}
	public void setEqualscount(String equalscount) {
		this.equalscount = equalscount;
	}
	public String getNotequalscount() {
		return notequalscount;
	}
	public void setNotequalscount(String notequalscount) {
		this.notequalscount = notequalscount;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		this.map = map;
	}
	public String getAllcount() {
		return allcount;
	}
	public void setAllcount(String allcount) {
		this.allcount = allcount;
	}
	public String getBlendcount() {
		return blendcount;
	}
	public void setBlendcount(String blendcount) {
		this.blendcount = blendcount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public PagingContext getPagingcontextblend() {
		return pagingcontextblend;
	}
	public void setPagingcontextblend(PagingContext pagingcontextblend) {
		this.pagingcontextblend = pagingcontextblend;
	}
	public void setSearchArea(String area) {
		super.setSearchArea(area);
//		if(searchArea!=null&&!"".equals(searchArea.trim())){
//    		strecode = "";
//		}
//		editor.fireModelChanged();
	}
	public String getSintredateStart() {
		return sintredateStart;
	}
	public void setSintredateStart(String sintredateStart) {
		this.sintredateStart = sintredateStart;
	}
	public String getSintredateEnd() {
		return sintredateEnd;
	}
	public void setSintredateEnd(String sintredateEnd) {
		this.sintredateEnd = sintredateEnd;
	}
	public String getSapplydateStart() {
		return sapplydateStart;
	}
	public void setSapplydateStart(String sapplydateStart) {
		this.sapplydateStart = sapplydateStart;
	}
	public String getSapplydateEnd() {
		return sapplydateEnd;
	}
	public void setSapplydateEnd(String sapplydateEnd) {
		this.sapplydateEnd = sapplydateEnd;
	}
	public List getTreCodeList() {
		return treCodeList;
	}
	public void setTreCodeList(List treCodeList) {
		this.treCodeList = treCodeList;
	}
	public Map<String, String> getTrxmap() {
		return trxmap;
	}
	public void setTrxmap(Map<String, String> trxmap) {
		this.trxmap = trxmap;
	}
	public List<TdEnumvalueDto> getStatelist() {
		if(statelist==null||statelist.size()<=0)
		{
			 statelist = new ArrayList<TdEnumvalueDto>();
			 TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
			 valuedtoa.setStypecode("��ǰ��");
			 valuedtoa.setSvalue("0");
			 statelist.add(valuedtoa);
			 TdEnumvalueDto valuedtob = new TdEnumvalueDto();
			 valuedtob.setStypecode("��ʷ��");
			 valuedtob.setSvalue("1");
			 statelist.add(valuedtob);
		}
		return statelist;
	}
	public void setStatelist(List<TdEnumvalueDto> statelist) {
		this.statelist = statelist;
	}
}