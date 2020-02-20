package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-03-08 10:52:35
 */
public class PayMsgNOEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("111","客户发起汇兑业务报文111(二代)"));
		list.add(new Mapper("112","金融机构发起汇兑业务报文112(二代)"));
		list.add(new Mapper("121","客户发起普通贷记业务报文(二代)121"));
		list.add(new Mapper("122","金融机构发起普通贷记业务报文122(二代)"));
		list.add(new Mapper("103","大额支付汇划CMT103(一代)"));
		list.add(new Mapper("100","大额支付汇兑CMT100(一代)"));
		list.add(new Mapper("001","小额普通贷记PKG001(一代)"));
		return list;
	}

}
