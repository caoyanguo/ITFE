package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * ��λ���
��λ���
 * @author Administrator
 * @time   14-10-10 14:54:49
 */
public class AllflagEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		Mapper m1 = new Mapper("1","ȫ��");
		Mapper m2 = new Mapper("2","����");
		list.add(m1);
		list.add(m2);
		return list;
	}

}
