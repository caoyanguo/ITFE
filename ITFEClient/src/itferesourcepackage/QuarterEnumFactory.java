package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   14-09-28 11:01:00
 */
public class QuarterEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
//		Mapper m0 = new Mapper("","");
		Mapper m1 = new Mapper("1","��һ����");
		Mapper m2 = new Mapper("2","�ڶ�����");
		Mapper m3 = new Mapper("3","��������");
		Mapper m4 = new Mapper("4","���ļ���");
//		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		ms.add(m4);
		return ms;
	}

}
