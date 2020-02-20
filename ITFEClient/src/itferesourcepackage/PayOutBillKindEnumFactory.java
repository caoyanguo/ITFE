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
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_1,"һ��Ԥ��֧������"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_2,"ʵ���ʽ�֧������"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_3,"����Ԥ��֧������"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_4,"ֱ��֧���ձ���"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_5,"��Ȩ֧���ձ���"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_6,"ֱ��֧���˻��ձ���"));
		list.add(new Mapper(StateConstant.REPORT_PAYOUT_TYPE_7,"��Ȩ֧���˻��ձ���"));
		return list;
	}

}
