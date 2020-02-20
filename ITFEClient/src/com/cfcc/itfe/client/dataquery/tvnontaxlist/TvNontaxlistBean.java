package com.cfcc.itfe.client.dataquery.tvnontaxlist;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.NontaxDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   18-04-27 15:05:00
 * ��ϵͳ: DataQuery
 * ģ��:TvNontaxlist
 * ���:TvNontaxlist
 */
public class TvNontaxlistBean extends AbstractTvNontaxlistBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvNontaxlistBean.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    public TvNontaxlistBean() {
      super();
      searchDto = new NontaxDto();
      searchDto.setSstartdate(TimeFacade.getCurrentStringTime());
      searchDto.setSenddate(TimeFacade.getCurrentStringTime());
      searchDto.setStrecode(logInfo.getSorgcode().substring(0, 10));
      reportPath = "/com/cfcc/itfe/client/ireport/itfe_TV_NONTAXSUB.jasper";
      rsList = new ArrayList();
      paramMap = new HashMap();
    }
    
	/**
	 * Direction: ��ѯ���������
	 * ename: queryResult
	 * ���÷���: 
	 * viewers: ����
	 * messages: 
	 */
    public String queryResult(Object o){
    	rsList.clear();
    	paramMap.clear();
    	try {
			rsList = tvNontaxlistService.findInfo(searchDto);
			if(rsList!=null&&rsList.size()>0)
			{
				if(searchDto.getSfinorgcode()==null||"".equals(searchDto.getSfinorgcode()))
				{
					NontaxDto nTaxDto = (NontaxDto)rsList.get(0);
					searchDto.setSfinorgcode(nTaxDto.getSfinorgcode());
				}
			}else
			{
				MessageDialog.openMessageDialog(null, "��ѯ����Ϊ��!");
				return "";
			}				
			paramMap.put("printDate", TimeFacade.getCurrent2StringTime());
			paramMap.put("username", logInfo.getSuserName());
			String tmpbnkname = "";
			if(searchDto.getSpaybankno()!=null && !searchDto.getSpaybankno().equals("")){
				TsPaybankDto pbdto = new TsPaybankDto();
				pbdto.setSbankno(searchDto.getSpaybankno());
				List<TsPaybankDto> list = commonDataAccessService.findRsByDto(pbdto);
				if(list != null && list.size() > 0){
					tmpbnkname = list.get(0).getSbankname();
				}
			}else{
				tmpbnkname = "ȫ��";
			}
			if(searchDto.getStrecode()!=null)
			{
				TsTreasuryDto trdto = new TsTreasuryDto();
				trdto.setStrecode(searchDto.getStrecode());
				trdto.setSorgcode(logInfo.getSorgcode());
				List<TsTreasuryDto> list = commonDataAccessService.findRsByDto(trdto);
				if(list != null && list.size() > 0){
					paramMap.put("strename",list.get(0).getStrename());
				}else
				{
					paramMap.put("strename", "δ֪");
				}
			}else
			{
				paramMap.put("strename", "ȫ��");
			}
			if(searchDto.getSfinorgcode()!=null)
			{
				TsTaxorgDto trdto = new TsTaxorgDto();
				trdto.setStrecode(searchDto.getStrecode());
				trdto.setSorgcode(logInfo.getSorgcode());
				trdto.setStaxorgcode(searchDto.getSfinorgcode());
				List<TsTaxorgDto> list = commonDataAccessService.findRsByDto(trdto);
				if(list != null && list.size() > 0){
					paramMap.put("staxorgname",list.get(0).getStaxorgname());
				}else
				{
					paramMap.put("staxorgname", "δ֪");
				}
			}else
			{
				paramMap.put("staxorgname", "ȫ��");
			}
			paramMap.put("spaybankname", tmpbnkname);
			for(int i = 0;i<rsList.size();i++){
				NontaxDto nTaxDto = (NontaxDto)rsList.get(i);
				if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("1")){
					nTaxDto.setSxaddword("����");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("2")){
					nTaxDto.setSxaddword("ʡ��");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("3")){
					nTaxDto.setSxaddword("����");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("4")){
					nTaxDto.setSxaddword("����");
					nTaxDto.setSvicesign("");
				}else if(nTaxDto.getSxaddword() != null && nTaxDto.getSxaddword().equals("0")){
					nTaxDto.setSxaddword("����");
					nTaxDto.setSvicesign(getSharingRatio(nTaxDto.getSvicesign()));
				}
			}
		} catch (ITFEBizException e) {
			log.error("��ѯ���ݴ���", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.queryResult(o);
    }
    /**
	 * Direction: ����txt
	 * ename: exporttxt
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exporttxt(Object o){
    	if(rsList==null||rsList.size()<=0)
    	{
    		MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ����Ȳ�ѯ��Ҫ���������ݣ�");
    		return "";
    	}
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+ searchDto.getStrecode()+"_"+searchDto.getSstartdate()+"_" +searchDto.getSenddate()+ ".txt";
		try {
			StringBuffer resultStr = new StringBuffer();
			resultStr.append("�տ����:"+paramMap.get("strename")+"���ջ���:"+paramMap.get("staxorgname")+"����:"+paramMap.get("spaybankname")+System.getProperty("line.separator"));
			resultStr.append("�ɿ�ʶ����,ִ�յ�λ����,�ɿ�������,Ԥ���Ŀ����,Ԥ�㼶��,������־,���"+System.getProperty("line.separator"));
			NontaxDto temp = null;
			for(int i=0;i<rsList.size();i++){
				temp = (NontaxDto)rsList.get(i);
				resultStr.append(temp.getShold1()+","); //�ɿ�ʶ����
				resultStr.append(temp.getShold4()+",");	//ִ�յ�λ����
				resultStr.append(temp.getShold2()+",");	//�ɿ�������
				resultStr.append(temp.getSitemcode()+",");//Ԥ���Ŀ����
				resultStr.append(temp.getSxaddword()+",");//Ԥ�㼶��
				resultStr.append(temp.getSvicesign()+",");//������־
				resultStr.append(temp.getNtraamt()+System.getProperty("line.separator"));//���
			}
			FileUtil.getInstance().writeFile(fileName, resultStr.toString());
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.exporttxt(o);
    }
    /**
     * ������־35λ��ÿ7λ��������Ӧ�Ĳ㼶��ռ��
     * @param ratioCode
     * @return
     */
    public String getSharingRatio(String ratioCode){
    	StringBuffer sb = new StringBuffer();
    	String tmp = ratioCode.substring(0, 7);
    	StringBuffer ratStrSb = new StringBuffer("");
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	DecimalFormat df = new DecimalFormat("0.00%");
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append("����:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	tmp = ratioCode.substring(7, 14);
    	sb = new StringBuffer();
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append(" ʡ��:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	tmp = ratioCode.substring(14, 21);
    	sb = new StringBuffer();
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append(" ����:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	tmp = ratioCode.substring(21, 28);
    	sb = new StringBuffer();
    	sb.append(tmp.charAt(0)+".");
    	for(int i=1;i<tmp.length();i++){
    		sb.append(tmp.charAt(i));
    	}
    	if(sb.toString().equals("0.000000")){
//    		ratStrSb.append("0.00%");
    	}else{
    		ratStrSb.append(" ����:");
    		ratStrSb.append(df.format(new BigDecimal(sb.toString())));
    	}
    	
    	return ratStrSb.toString();
    }

	/**
	 * Direction: ���ص���ѯ����
	 * ename: backQuery
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String backQuery(Object o){
          return super.backQuery(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}