package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author hejianrong
 * @time   14-11-18 10:04:11
 */
public class PaymodeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list=new ArrayList<Mapper>();
		Mapper m1=new Mapper("0","直接支付");
		Mapper m2=new Mapper("91","实拨");
		list.add(m1);
		list.add(m2);
		return list;
	}

}
