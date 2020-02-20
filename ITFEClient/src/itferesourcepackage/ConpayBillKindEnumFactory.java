package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-04-16 21:41:56
 */
public class ConpayBillKindEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list=new ArrayList<Mapper>();
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_4,"ֱ��֧������"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_5,"��Ȩ֧������"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_6,"ֱ��֧���˻ر���"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_7,"��Ȩ֧���˻ر���"));
		return list;
	}

}
