package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author adminabc
 * @time   16-08-24 09:02:12
 */
public class BillAutoTaxEnumFactory implements IEnumFactory {
	ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		Mapper taxmap = null;
		maplist=new ArrayList();
		taxmap = new Mapper("1","����");
		maplist.add(taxmap);
		taxmap = new Mapper("0","��������");
		maplist.add(taxmap);
		taxmap = new Mapper("2","�����������ջ���");
		maplist.add(taxmap);
		taxmap = new Mapper("3","��˰����");//111111111111
		maplist.add(taxmap);
		taxmap = new Mapper("4","��˰����");//222222222222
		maplist.add(taxmap);
		taxmap = new Mapper("5","���ش���");//333333333333
		maplist.add(taxmap);
		taxmap = new Mapper("6","��������");//555555555555
		maplist.add(taxmap);
		TsTaxorgDto taxdto = new TsTaxorgDto();
		taxdto.setSorgcode(loginfo.getSorgcode());
		try {
				List<TsTaxorgDto> queryList = commonDataAccessService.findRsByDto(taxdto," order by s_taxorgcode ");
				if(queryList!=null&&queryList.size()>0)
				{
					for(TsTaxorgDto temp:queryList)
					{
						if(temp.getStaxorgname()!=null&&!"".equals(temp.getStaxorgname()))
							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgcode()+temp.getStaxorgname());
						else
							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgcode());
						maplist.add(taxmap);
					}
				}
		} catch (ITFEBizException e) {
				e.printStackTrace();
		}
		return maplist;
	}

}
