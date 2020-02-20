package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   14-06-12 15:50:46
 */
public class FundBizTypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("3403","国库往来票据");
		Mapper m1 = new Mapper("3208","实拨资金退款");
		ms.add(m0);
		ms.add(m1);
		return ms;
	}

}
