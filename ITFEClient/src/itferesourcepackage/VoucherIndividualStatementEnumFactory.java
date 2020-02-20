package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author hejianrong
 * @time   14-06-17 08:58:45
 */
public class VoucherIndividualStatementEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("4004","人民银行与财政部门集中支付业务对账单");
		Mapper m1 = new Mapper("4005","人民银行与代理银行集中支付业务对账单");
		Mapper m2 = new Mapper("4006","人民银行与代理银行集中支付对账明细表");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		return ms;
	}

}
