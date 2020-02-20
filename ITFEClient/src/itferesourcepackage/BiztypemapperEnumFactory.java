package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * 0直接支付91实拨资金退款
 * @author Administrator
 * @time   15-04-15 19:56:07
 */
public class BiztypemapperEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> bizTypeMapperList = new ArrayList<Mapper>();
		Mapper mapper0 = new Mapper();
		mapper0.setDisplayValue("直接支付");
		mapper0.setUnderlyValue("0");
		Mapper mapper1 = new Mapper();
		mapper1.setDisplayValue("授权支付");
		mapper1.setUnderlyValue("1");
		Mapper mapper91 = new Mapper();
		mapper91.setDisplayValue("实拨退款");
		mapper91.setUnderlyValue("91");
		Mapper mapper5207 = new Mapper();
		mapper5207.setDisplayValue("预算拨款");
		mapper5207.setUnderlyValue("5207");
		Mapper mapper5201 = new Mapper();
		mapper5201.setDisplayValue("直接支付");
		mapper5201.setUnderlyValue("5201");
		bizTypeMapperList.add(mapper0);
		bizTypeMapperList.add(mapper1);
		bizTypeMapperList.add(mapper91);
		bizTypeMapperList.add(mapper5207);
		bizTypeMapperList.add(mapper5201);
		return bizTypeMapperList;
	}

}
