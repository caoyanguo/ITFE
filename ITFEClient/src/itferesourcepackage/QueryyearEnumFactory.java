package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-09-16 16:20:25
 */
public class QueryyearEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		String date=TimeFacade.getCurrentStringTime();
		String year=date.substring(0, 4);
		String lastyear=(Integer.parseInt(year)-1+"").trim();
		Mapper m0 = new Mapper(year,year+"定業");
		Mapper m1 = new Mapper(lastyear,lastyear+"定業");
		ms.add(m0);
		ms.add(m1);
		return ms;
	}

}
