package com.cfcc.itfe.client.dataquery.sendvoucherinfocount;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TimerVoucherInfoDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IExportTBSForBJService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;

/**
 * codecomment: 
 * @author zhangliang
 * @time   17-09-25 15:12:48
 * ��ϵͳ: DataQuery
 * ģ��:SendVoucherInfoCount
 * ���:SendVoucherInfoCount
 */
@SuppressWarnings("unchecked")
public class SendVoucherInfoCountBean extends AbstractSendVoucherInfoCountBean implements IPageDataProvider {
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	private IExportTBSForBJService timerService = (IExportTBSForBJService) ServiceFactory.getService(IExportTBSForBJService.class);
    private static Log log = LogFactory.getLog(SendVoucherInfoCountBean.class);
    private TvVoucherinfoDto searchdto = null;
    private static Map<String,TdEnumvalueDto> enumMap = null;
    private ITFELoginInfo loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    private String strecode = null;
    private List vtcodelist = null;
    private List<Map<String,String>> datalist = null;
    public SendVoucherInfoCountBean() {
      super();
      searchdto = new TvVoucherinfoDto();  
      searchdto.setSorgcode(loginInfo.getSorgcode());
      searchdto.setShold1(TimeFacade.getCurrentStringTime());
      searchdto.setShold2(TimeFacade.getCurrentStringTime());
    }
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
	public TvVoucherinfoDto getSearchdto() {
		return searchdto;
	}
	public void setSearchdto(TvVoucherinfoDto searchdto) {
		this.searchdto = searchdto;
	}
	public String getStrecode() {
		return strecode;
	}
	
	public void setStrecode(String strecode) throws ITFEBizException {
		if(strecode!=null&&!"".equals(strecode))
		{
			TsVouchercommitautoDto findto = new TsVouchercommitautoDto();
			findto.setStrecode(strecode);
			findto.setSreadauto("1");
			findto.setSorgcode(loginInfo.getSorgcode());
			List<TsVouchercommitautoDto> searchlist = commonDataAccessService.findRsByDto(findto);
			if(vtcodelist==null)
				vtcodelist = new ArrayList();
			vtcodelist.clear();
			searchdto.setSvtcode(null);
			if(searchlist!=null&&searchlist.size()>0)
			{
				if(enumMap==null||enumMap.isEmpty())
				{
					TdEnumvalueDto efindto = new TdEnumvalueDto();
					efindto.setStypecode("0433");
					enumMap = new HashMap<String,TdEnumvalueDto>();
					List<TdEnumvalueDto> findlist = commonDataAccessService.findRsByDto(efindto);
					if(findlist!=null&&findlist.size()>0)
					{
						for(TdEnumvalueDto temp:findlist)
							enumMap.put(temp.getSvalue(), temp);
					}
				}
				for(TsVouchercommitautoDto temp:searchlist)
				{
					if(enumMap.get(temp.getSvtcode())!=null)
						vtcodelist.add(enumMap.get(temp.getSvtcode()));
				}
			}
		}
		this.strecode = strecode;
		searchdto.setStrecode(strecode);
		this.editor.fireModelChanged();
	}
	/**
	 * Direction: ����
	 * ename: exportfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportfile(Object o){
    	if(datalist==null||datalist.size()<=0)
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
		String fileName = filePath+ File.separator+ searchdto.getStrecode() +"-"+searchdto.getShold1()+"-"+searchdto.getShold2() + ".txt";
		try {
			StringBuffer resultStr = new StringBuffer();
			resultStr.append("�������,ƾ֤����,��������,���ɽ��,ǩ������,ǩ�½��,��������,���ͽ��,�ϼ�����,�ϼƽ��"+System.getProperty("line.separator"));
			for(Map<String,String> tmp : datalist){
				resultStr.append(tmp.get("strecode")+","); //�������
				resultStr.append(tmp.get("svtcode")+",");	//ƾ֤����
				resultStr.append((tmp.get("buildcount")==null?"0":tmp.get("buildcount"))+",");	//��������
				resultStr.append((tmp.get("buildamt")==null?"0":tmp.get("buildamt"))+",");//���ɽ��
				resultStr.append((tmp.get("stampcount")==null?"0":tmp.get("stampcount"))+",");//ǩ������
				resultStr.append((tmp.get("stampamt")==null?"0":tmp.get("stampamt"))+",");//ǩ�½��
				resultStr.append((tmp.get("sendcount")==null?"0":tmp.get("sendcount"))+",");//��������
				resultStr.append((tmp.get("sendamt")==null?"0":tmp.get("sendamt"))+",");//���ͽ��
				resultStr.append((tmp.get("allcount")==null?"0":tmp.get("allcount"))+",");//�ϼ�����
				resultStr.append(tmp.get("allamt")+System.getProperty("line.separator"));//�ϼƽ��
			}
			FileUtil.getInstance().writeFile(fileName, resultStr.toString());
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.exportfile(o);
    }
	/**
	 * Direction: ��ѯ
	 * ename: searchvoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchvoucher(Object o){
        
    	
    	try {
			Map<String,List<TimerVoucherInfoDto>> datamap = timerService.fetchVoucherInfoForClientTimer("sendvoucherinfocount", "sendvoucherinfocount", searchdto, "");
			if(datamap!=null&&datamap.get("timerResult")!=null)
			{
				List<TimerVoucherInfoDto> getlist  = datamap.get("timerResult");
				if(getlist!=null&&getlist.size()>0)
				{
					Map<String,Map<String,String>> getmap = new HashMap<String,Map<String,String>>();
					Map<String,String> tempmap = null;
					int buildcount = 0;//��������
					int stampcount = 0;//ǩ������
					int sendcount = 0;//��������
					int allcount = 0;//�ϼ�����
					BigDecimal buildamt = new BigDecimal(0);
					BigDecimal stampamt = new BigDecimal(0);
					BigDecimal sendamt = new BigDecimal(0);
					BigDecimal allamt = new BigDecimal(0);
					for(TimerVoucherInfoDto temp:getlist)
					{
						if(getmap.get(temp.getSvtcode())==null)
						{
							tempmap = new HashMap<String,String>();
							tempmap.put("svtcode", temp.getSvtcode());
							tempmap.put("strecode",strecode);
							getmap.put(temp.getSvtcode(), tempmap);
						}else
						{
							tempmap = getmap.get(temp.getSvtcode());
						}
						allcount = Integer.parseInt(tempmap.get("allcount")==null?"0":tempmap.get("allcount"));
						allamt = new BigDecimal((tempmap.get("allamt")==null?"0":tempmap.get("allamt")));
						allamt = allamt.add(temp.getTotalamt());
						tempmap.put("allamt", allamt.toString());
						if(DealCodeConstants.VOUCHER_STAMP.equals(temp.getSbizname()))
						{
							stampcount = Integer.parseInt(tempmap.get("stampcount")==null?"0":tempmap.get("stampcount"));
							stampamt = new BigDecimal((tempmap.get("stampamt")==null?"0":tempmap.get("stampamt")));
							stampamt = stampamt.add(temp.getTotalamt());
							stampcount+=temp.getCount1();
							tempmap.put("stampcount", String.valueOf(stampcount));
							tempmap.put("stampamt", stampamt.toString());
							tempmap.put("allcount", String.valueOf(allcount+temp.getCount1()));
						}else if(DealCodeConstants.VOUCHER_FAIL.equals(temp.getSbizname())|| DealCodeConstants.VOUCHER_SUCCESS.equals(temp.getSbizname())||DealCodeConstants.VOUCHER_READFIN.equals(temp.getSbizname())||DealCodeConstants.VOUCHER_READBANK.equals(temp.getSbizname())||DealCodeConstants.VOUCHER_READRETURN.equals(temp.getSbizname()))
						{
							sendcount = Integer.parseInt(tempmap.get("sendcount")==null?"0":tempmap.get("sendcount"));
							sendamt = new BigDecimal((tempmap.get("sendamt")==null?"0":tempmap.get("sendamt")));
							sendamt = sendamt.add(temp.getTotalamt());
							sendcount+=temp.getCount1();
							tempmap.put("sendcount", String.valueOf(sendcount));
							tempmap.put("sendamt", sendamt.toString());
							tempmap.put("allcount", String.valueOf(allcount+temp.getCount1()));
						}else
						{
							buildcount = Integer.parseInt(tempmap.get("buildcount")==null?"0":tempmap.get("buildcount"));
							buildamt = new BigDecimal((tempmap.get("buildamt")==null?"0":tempmap.get("buildamt")));
							buildamt = buildamt.add(temp.getTotalamt());
							buildcount+=temp.getCount1();
							tempmap.put("buildamt", buildamt.toString());
							tempmap.put("buildcount", String.valueOf(buildcount));
							tempmap.put("allcount", String.valueOf(allcount+temp.getCount1()));
						}
					}
					if(!getmap.isEmpty())
					{
						if(datalist!=null)
							datalist.clear();
						else
							datalist = new ArrayList();
						datalist.addAll(getmap.values());
					}else
					{
						MessageDialog.openMessageDialog(null, "��ѯ����Ϊ��!");
						return super.searchvoucher(o);
					}
				}else
				{
					MessageDialog.openMessageDialog(null, "��ѯ����Ϊ��!");
					return super.searchvoucher(o);
				}
			}else
			{
				MessageDialog.openMessageDialog(null, "��ѯ����Ϊ��!");
				return super.searchvoucher(o);
			}
		} catch (Exception e) {
			MessageDialog.openMessageDialog(null, "��ѯͳ���쳣:"+e.toString());
		}
		this.editor.fireModelChanged();
        return super.searchvoucher(o);
    }
	public List getVtcodelist() {
		return vtcodelist;
	}
	public void setVtcodelist(List vtcodelist) {
		this.vtcodelist = vtcodelist;
	}
	public static Log getLog() {
		return log;
	}
	public static void setLog(Log log) {
		SendVoucherInfoCountBean.log = log;
	}
	public List getDatalist() {
		return datalist;
	}
	public void setDatalist(List<Map<String,String>> datalist) {
		this.datalist = datalist;
	}

}