package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author caoyg
 * @time   09-11-12 20:07:55
 */
public class ProcFlagEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("1","Ԥ����"));
		list.add(new Mapper("2","��ʽ����"));
		list.add(new Mapper("3","�Ѹ���"));
		list.add(new Mapper("4","У��ʧ��"));
		list.add(new Mapper("5","�˿��˻�"));
		list.add(new Mapper("6","����ʧ��"));
		return list;
	}

}
