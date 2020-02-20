package com.cfcc.itfe.client.dataquery.tipsmsgproc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 09-12-10 13:27:07 ��ϵͳ: DataQuery ģ��:TipsMsgProc ���:DealSendMsg
 */
public class DealSendMsgBean extends AbstractDealSendMsgBean implements IPageDataProvider {

	private TvFilepackagerefDto finddto = null; // ��ѯ��������
	private TvFilepackagerefDto operdto = null; // ���ݲ�������
	private PagingContext sendmsgtablepage; // ��ҳ�ؼ�
	
	private List bizlist ;
	private List statelist ;
	private ITFELoginInfo loginfo = null;
	private List reportRs = null;
	private Map reportmap = new HashMap();
	private String startdate;
	private String enddate;
	private List trelist;
	private List finorglist;
	private String reportPath = "com/cfcc/itfe/client/ireport/SendMsgView.jasper";
	private List<TvFilepackagerefDto> filelist;
	private List<TvFilepackagerefDto> checkList = null;
	private String operationtypecode;
	public DealSendMsgBean() {
		super();
		reportRs = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
		initBizlist();
		initStatelist();
		startdate=TimeFacade.getCurrentStringTime();
		enddate=startdate;
		finddto = new TvFilepackagerefDto();
		operdto = new TvFilepackagerefDto();
		sendmsgtablepage = new PagingContext(this);
		init();
	}
	private void init(){
		TsTreasuryDto tredto=new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		TsConvertfinorgDto findto=new TsConvertfinorgDto();
		findto.setSorgcode(loginfo.getSorgcode());
		try {
			filelist = new ArrayList<TvFilepackagerefDto>();
			checkList = new ArrayList<TvFilepackagerefDto>();
			trelist=commonDataAccessService.findRsByDto(tredto);
			finorglist=commonDataAccessService.findRsByDto(findto);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}
	public String refreshrs(Object o){
		return this.queryMsg(o);
	}
	/**
	 * Direction: ���ͱ��Ĳ�ѯ��ӡ ename: queryPrint ���÷���: viewers: ���Ĳ�ѯ��ӡ messages:
	 */
	public String queryPrint(Object o) {
		String biztype = finddto.getSoperationtypecode();
		if (null == biztype || "".equals(biztype.trim())) {
			MessageDialog.openMessageDialog(null, "��ѯ��ӡʱҵ�����ͱ�����д!");
			return super.backSearch(o);
		}
		if("0000".equals(biztype))
		{
			MessageDialog.openMessageDialog(null, "��ѯ��ӡʱҵ�����Ͳ���Ϊȫ��ҵ������!");
			return super.backSearch(o);
		}
		if(BizTypeConstant.BIZ_TYPE_INCOME.equals(finddto.getSoperationtypecode()))
		{
			reportPath = "com/cfcc/itfe/client/ireport/SendMsgView_xiamen.jasper";
		}
		//������������ʱ��β�ѯ����
		StringBuffer s_where = new StringBuffer();
		s_where.append(" and 1=1 ");
		if(!StringUtils.isBlank(startdate)){
			s_where.append("  AND S_COMMITDATE >= '"+startdate+"' " ) ;
		}
		if(!StringUtils.isBlank(enddate)){
			s_where.append("  AND S_COMMITDATE <= '"+enddate+"' " ) ;
		}
		 if ((this.checkList != null) && (this.checkList.size() > 0))
		      s_where.append("  AND ( substr(s_filename,1,posstr(s_filename,'.')-1) IN( " + getcheckListToString(this.checkList) + "))");
		try {
			finddto.setSorgcode(loginfo.getSorgcode());
			String ls_tempDemo = this.finddto.getSdemo();
		    this.finddto.setSdemo(null);
			List<TvFilepackagerefDto> tmplist = commonDataAccessService.findRsByDto(finddto, s_where.toString());
			this.finddto.setSdemo(ls_tempDemo);
			if(tmplist!=null&&tmplist.size()>0)
			{
				for(TvFilepackagerefDto tmpdto:tmplist)
				{
					if(tmpdto.getSfilename().indexOf("/")>=0)
						tmpdto.setSfilename(tmpdto.getSfilename().substring(tmpdto.getSfilename().lastIndexOf("/")+1));
					else if(tmpdto.getSfilename().indexOf("\\")>=0)
						tmpdto.setSfilename(tmpdto.getSfilename().substring(tmpdto.getSfilename().lastIndexOf("\\")+1));
				}
			}
			if(null == tmplist || tmplist.size() == 0){
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
				return super.backSearch(o);
			}
			TdEnumvalueDto findto = new TdEnumvalueDto();
			findto.setStypecode("0407");
			List<TdEnumvalueDto> tdenumList = commonDataAccessService.findRsByDto(findto);
			if(tdenumList!=null&&tdenumList.size()>0)
			{
				for(int i=0;i<tdenumList.size();i++)
				{
					findto = tdenumList.get(i);
					if(biztype.equals(findto.getSvalue()))
					{
						biztype = findto.getSvaluecmt();
						break;
					}
				}
			}
			List countlist = changeDtoList(tmplist);
			reportmap.put("REPORT_TYPE", biztype);
			reportmap.put("REPORT_SUMAMT", countlist.get(0));
			reportmap.put("REPORT_SUMCOUNT", countlist.get(1));
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.queryPrint(o);
	}

	/**
	 * Direction: ���ͱ��Ĳ�ѯ���� ename: queryMsg ���÷���: viewers: ���Ĳ�ѯ���� messages:
	 */
	public String queryMsg(Object o) {
		if(null!=startdate&&!"".equals(startdate)&&null!=enddate&&!"".equals(enddate)){
			if (Integer.parseInt(startdate)>Integer.parseInt(enddate)) {
				MessageDialog.openMessageDialog(null, "��ѯ��ʼ����ӦС�ڲ�ѯ�������ڣ�");
				return null;
			}
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		sendmsgtablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.backSearch(o);
		}

		editor.fireModelChanged();

		return super.queryMsg(o);
	}

	/**
	 * Direction: ������־��ѯ ename: backSearch ���÷���: viewers: ���ͱ��Ĳ�ѯ messages:
	 */
	public String backSearch(Object o) {
		return super.backSearch(o);
	}

	/**
	 * Direction: ��ѡһ����¼ ename: selOneRecode ���÷���: viewers: * messages:
	 */
	public String selOneRecode(Object o) {
		operdto = (TvFilepackagerefDto) o;
		return super.selOneRecode(o);
	}

	/**
	 * Direction: �����ط� ename: sendMsgRepeat ���÷���: viewers: * messages:
	 */
	public String sendMsgRepeat(Object o) {
		if (null == operdto || null == operdto.getSpackageno() || "".equals(operdto.getSpackageno().trim())) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�ط����ĵļ�¼��");
			return super.sendMsgRepeat(o);
		}

		try {
			dealSendMsgService.sendMsgRepeat(operdto);
			MessageDialog.openMessageDialog(null, "�ط����ĳɹ���[����ˮ�ţ�" + operdto.getSpackageno() + "]");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.sendMsgRepeat(o);
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
			if("0000".equals(finddto.getSoperationtypecode()))
			{
				finddto.setSoperationtypecode(null);
			}
			StringBuffer s_where = new StringBuffer();
			s_where.append(" 1=1 ");
			if(!StringUtils.isBlank(startdate)){
				s_where.append("  AND S_COMMITDATE >= '"+startdate+"' " ) ;
			}
			if(!StringUtils.isBlank(enddate)){
				s_where.append("  AND S_COMMITDATE <= '"+enddate+"' " ) ;
			}
			 if ((this.checkList != null) && (this.checkList.size() > 0))
			      s_where.append("  AND ( substr(s_filename,1,posstr(s_filename,'.')-1) IN( " + getcheckListToString(this.checkList) + "))");
			String ls_tempDemo = this.finddto.getSdemo();
		    this.finddto.setSdemo(null);
		    PageResponse pageResponse = this.commonDataAccessService.findRsByDtoWithWherePaging(finddto, pageRequest,s_where.toString());
	        this.finddto.setSdemo(ls_tempDemo);
	        return pageResponse;
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.retrieve(pageRequest);
	}

	public TvFilepackagerefDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvFilepackagerefDto finddto) {
		this.finddto = finddto;
	}

	public TvFilepackagerefDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvFilepackagerefDto operdto) {
		this.operdto = operdto;
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

	public PagingContext getSendmsgtablepage() {
		return sendmsgtablepage;
	}

	public void setSendmsgtablepage(PagingContext sendmsgtablepage) {
		this.sendmsgtablepage = sendmsgtablepage;
	}
	
	/**
	 * ת��������״̬������
	 * @param tmplist
	 * @throws ITFEBizException 
	 */
	private List changeDtoList(List<TvFilepackagerefDto> tmplist) throws ITFEBizException{
		List countlist = new ArrayList();
		BigDecimal sumamt = new BigDecimal("0.00");
		int icount = 0 ;
		TdEnumvalueDto findto = new TdEnumvalueDto();
		findto.setStypecode("0300");
		List<TdEnumvalueDto> tdenumList = commonDataAccessService.findRsByDto(findto);
		Map<String,String> cacheMap = new HashMap<String,String>();
		if(tdenumList!=null&&tdenumList.size()>0)
		{
			for(int i=0;i<tdenumList.size();i++)
			{
				findto = tdenumList.get(i);
				cacheMap.put(findto.getSvalue(), findto.getSvaluecmt());
			}
		}
		reportRs = new ArrayList<TvFilepackagerefDto>();
		for(int i = 0 ;i < tmplist.size() ; i++){
			TvFilepackagerefDto tmpdto = tmplist.get(i);
			tmpdto.setSretcode(cacheMap.get(tmpdto.getSretcode()));
			if(tmpdto.getSdemo()==null)
				tmpdto.setSdemo("");
			sumamt = sumamt.add(tmpdto.getNmoney());
			icount = icount + tmpdto.getIcount();
			reportRs.add(tmpdto);
		}
		
		countlist.add(0,sumamt);
		countlist.add(1,icount);
		
		return countlist;
	}
	
	/**
	 * ����״̬������ת���������ӡר�ã�
	 * @param dealcode
	 * @return
	 */
	private String changeDealCode(String dealcode){
		if(DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(dealcode)
				|| DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(dealcode)){
			return "�ɹ�";
		}else if(DealCodeConstants.DEALCODE_ITFE_FAIL.equals(dealcode)){
			return "ʧ��";
		}else if(DealCodeConstants.DEALCODE_ITFE_DEALING.equals(dealcode)){
			return "������";
		}else if(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND.equals(dealcode)){
			return "���ط�";
		}else if(DealCodeConstants.DEALCODE_ITFE_RECEIVER.equals(dealcode)){
			return "������";
		}else if(DealCodeConstants.DEALCODE_ITFE_NO_CHECK.equals(dealcode)){
			return "δ����";
		}else if(DealCodeConstants.DEALCODE_ITFE_NO_SEND.equals(dealcode)){
			return "δ����";
		}else if(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING.equals(dealcode)){
			return "������";
		}
	
		return dealcode;
	}
	
	private void initBizlist(){
		this.bizlist = new ArrayList<TdEnumvalueDto>();
		    
		TdEnumvalueDto valuedto1 = new TdEnumvalueDto();
		valuedto1.setStypecode("����˰Ʊ");
		valuedto1.setSvalue(BizTypeConstant.BIZ_TYPE_INCOME);
		this.bizlist.add(valuedto1);
		
		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("ֱ��֧��");
		valuedto2.setSvalue(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);
		this.bizlist.add(valuedto2);
		
		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("��Ȩ֧��");
		valuedto3.setSvalue(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
		this.bizlist.add(valuedto3);
		
		TdEnumvalueDto valuedto4 = new TdEnumvalueDto();
		valuedto4.setStypecode("ʵ��");
		valuedto4.setSvalue(BizTypeConstant.BIZ_TYPE_PAY_OUT);
		this.bizlist.add(valuedto4);
	}
	
	private void initStatelist(){
		this.statelist = new ArrayList<TdEnumvalueDto>();
		
		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("������");
		valuedto3.setSvalue(DealCodeConstants.DEALCODE_ITFE_DEALING);
		this.statelist.add(valuedto3);
		
		TdEnumvalueDto valuedto4 = new TdEnumvalueDto();
		valuedto4.setStypecode("������");
		valuedto4.setSvalue(DealCodeConstants.DEALCODE_ITFE_RECEIVER);
		this.statelist.add(valuedto4);
		    
		TdEnumvalueDto valuedto1 = new TdEnumvalueDto();
		valuedto1.setStypecode("�ɹ�");
		valuedto1.setSvalue(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
		this.statelist.add(valuedto1);
		
		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("ʧ��");
		valuedto2.setSvalue(DealCodeConstants.DEALCODE_ITFE_FAIL);
		this.statelist.add(valuedto2);
		
		TdEnumvalueDto valuedto5 = new TdEnumvalueDto();
		valuedto5.setStypecode("δ����");
		valuedto5.setSvalue(DealCodeConstants.DEALCODE_ITFE_NO_SEND);
		this.statelist.add(valuedto5);
		
		TdEnumvalueDto valuedto6 = new TdEnumvalueDto();
		valuedto6.setStypecode("������");
		valuedto6.setSvalue(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
		this.statelist.add(valuedto6);
	}

	public List getBizlist() {
		return bizlist;
	}

	public void setBizlist(List bizlist) {
		this.bizlist = bizlist;
	}

	public List getStatelist() {
		return statelist;
	}

	public void setStatelist(List statelist) {
		this.statelist = statelist;
	}
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}
	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public List getTrelist() {
		return trelist;
	}
	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}
	public List getFinorglist() {
		return finorglist;
	}
	public void setFinorglist(List finorglist) {
		this.finorglist = finorglist;
	}
	public List<TvFilepackagerefDto> getFilelist() {
		return filelist;
	}
	public void setFilelist(List<TvFilepackagerefDto> filelist) {
		this.filelist = filelist;
	}
	public List<TvFilepackagerefDto> getCheckList() {
		return checkList;
	}
	public void setCheckList(List<TvFilepackagerefDto> checkList) {
		this.checkList = checkList;
	}
	public String getOperationtypecode() {
		return operationtypecode;
	}
	public void setOperationtypecode(String operationtypecode) {
	    this.operationtypecode = operationtypecode;
	    this.finddto.setSoperationtypecode(operationtypecode);

	    if ("11".equals(operationtypecode))
	    {
	      if ((this.startdate != null) && (!"".equals(this.startdate)) && (this.enddate != null) && (!"".equals(this.enddate)) && 
	        (Integer.parseInt(this.startdate) > Integer.parseInt(this.enddate))) {
	        MessageDialog.openMessageDialog(null, "��ѯ��ʼ����ӦС�ڲ�ѯ�������ڣ�");
	        return;
	      }

	      this.filelist = findFileListByTrasrlNo(this.finddto, this.startdate, this.enddate);
	      this.checkList.clear();
	      this.checkList.addAll(this.filelist);
	      List contreaNames1 = new ArrayList();
	      contreaNames1.add("���ͱ��Ĳ�ѯ����");
	      contreaNames1.add("����˰Ʊ�ļ���ӡ�б�");
	      MVCUtils.setContentAreaVisible(this.editor, contreaNames1, true);
	      MVCUtils.reopenCurrentComposite(this.editor);
	      this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	    } else {
	      if ((this.checkList != null) && (this.checkList.size() > 0))
	        this.checkList.clear();
	      if ((this.filelist != null) && (this.filelist.size() > 0))
	        this.filelist.clear();
	      List contreaNames1 = new ArrayList();
	      contreaNames1.add("����˰Ʊ�ļ���ӡ�б�");
	      MVCUtils.setContentAreaVisible(this.editor, contreaNames1, false);
	      MVCUtils.reopenCurrentComposite(this.editor);
	      this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	    }
	  }
	private String getcheckListToString(List<TvFilepackagerefDto> filelist)
	  {
	    String ls_Return = "";
	    if ((filelist != null) && (filelist.size() > 0)) {
	      for (int i = 0; i < filelist.size(); i++) {
	        String filename = ((TvFilepackagerefDto)filelist.get(i)).getSfilename();
	        filename = filename.substring(0, filename.indexOf("."));
	        ls_Return = ls_Return + "'" + filename + "',";
	      }
	    }
	    ls_Return = ls_Return.substring(0, ls_Return.length() - 1);
	    return ls_Return;
	  }

	  public String chooseAllFile(Object o)
	  {
	    if ((this.checkList == null) || (this.checkList.size() == 0)) {
	      this.checkList = new ArrayList();
	      this.checkList.addAll(this.filelist);
	    }
	    else {
	      this.checkList.clear();
	    }this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
	    return super.chooseAllFile(o);
	  }
	  public List findFileListByTrasrlNo(TvFilepackagerefDto finddto, String startDate, String endDate)
	  {
	    List fileList = new ArrayList();
	    StringBuffer ls_SQL = new StringBuffer("SELECT DISTINCT(S_FILENAME) FROM TV_INFILE where 1=1 ");
	    if (!StringUtils.isBlank(finddto.getSorgcode())) {
	      ls_SQL.append("  AND S_ORGCODE = '" + finddto.getSorgcode() + "'");
	    }
	    if (!StringUtils.isBlank(finddto.getStaxorgcode())) {
	      ls_SQL.append("  AND S_TAXORGCODE = '" + finddto.getStaxorgcode() + "'");
	    }
	    if (!StringUtils.isBlank(finddto.getStrecode())) {
	      ls_SQL.append("  AND S_RECVTRECODE = '" + finddto.getStrecode() + "'");
	    }
	    if (!StringUtils.isBlank(finddto.getSpackageno())) {
	      ls_SQL.append("  AND S_PACKAGENO = '" + finddto.getSpackageno() + "'");
	    }
	    if (!StringUtils.isBlank(finddto.getSdemo())) {
	      ls_SQL.append("  AND S_TRASRLNO = '" + finddto.getSdemo() + "'");
	    }
	    if (!StringUtils.isBlank(finddto.getSfilename())) {
		      ls_SQL.append("  AND S_FILENAME >= '" + finddto.getSfilename() + "' ");
		}
		if (!StringUtils.isBlank(finddto.getScommitdate())) {
		      ls_SQL.append("  AND S_COMMITDATE = '" + finddto.getScommitdate() + "' ");
		}
	    if (!StringUtils.isBlank(startDate)) {
	      ls_SQL.append("  AND S_COMMITDATE >= '" + startDate + "' ");
	    }
	    if (!StringUtils.isBlank(endDate)) {
	      ls_SQL.append("  AND S_COMMITDATE <= '" + endDate + "' ");
	    }
	    try
	    {
	    	Map getMap = this.commonDataAccessService.getmapforkey(ls_SQL.toString());
	      fileList = getMap==null?null:(List)getMap.get("data");
	    }
	    catch (Throwable e)
	    {
	      MessageDialog.openErrorDialog(null, e);
	    }
	    return fileList;
	  }

}