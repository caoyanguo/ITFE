package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   14-04-02 09:39:38
 */
public class BookAccountEnumFactory implements IEnumFactory {

	ICommonDataAccessService commondataService = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
	ITFELoginInfo loginfo=null;
	@SuppressWarnings("unchecked")
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    	List<TdBookacctMainDto> bookAccountList=new ArrayList<TdBookacctMainDto>();
    	TdBookacctMainDto idto=new TdBookacctMainDto();
    	idto.setSbookorgcode(loginfo.getSorgcode());
    	try {
    		bookAccountList=commondataService.findRsByDtoUR(idto);
    		if(bookAccountList==null||bookAccountList.size()==0){
    			return null;
    		}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "获取库款账户枚举值失败："+e.getMessage());
			return null;
		}
    	
		for (TdBookacctMainDto bookAccountDto : bookAccountList) {
			Mapper map = new Mapper(bookAccountDto.getSbookacct(),bookAccountDto.getSbookacctname());
			maplist.add(map);
		}
		return maplist;
	}
}
