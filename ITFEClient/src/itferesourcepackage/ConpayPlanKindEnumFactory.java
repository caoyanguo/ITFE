package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-04-17 23:39:44
 */
public class ConpayPlanKindEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list=new ArrayList<Mapper>();
		list.add(new Mapper(StateConstant.Conpay_Direct,"直接支付"));
		list.add(new Mapper(StateConstant.Conpay_Grant,"授权支付"));
		return list;
	}

}
