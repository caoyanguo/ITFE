package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-04-15 03:22:19
 */
public class PayOutBillKindEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list=new ArrayList<Mapper>();
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_1,"一般预算支出报表"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_2,"实拨资金支出报表"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_3,"调拨预算支出报表"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_4,"直接支付日报表"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_5,"授权支付日报表"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_6,"直接支付退回日报表"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_7,"授权支付退回日报表"));
		return list;
	}

}
