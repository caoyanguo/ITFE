package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author Administrator
 * @time   13-11-08 15:22:02
 */
public class PayoutbackflagEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		//ʵ���ʽ��˿��־
		list.add(new Mapper("0","��"));
		list.add(new Mapper("1","��"));
		return list;
	}

}
