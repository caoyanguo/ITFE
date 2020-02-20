package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-08-16 14:57:43
 */
public class DirectGrantBiztypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("25","直接支付清算[25]");
		Mapper m1 = new Mapper("26","直接支付退款[26]");
		Mapper m2 = new Mapper("27","授权支付清算[27]");
		Mapper m3 = new Mapper("28","授权支付退款[28]");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		return ms;
	}

}
