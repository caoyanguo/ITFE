package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-08-27 09:09:54
 */
public class SbilltypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("1","�ձ�");
		Mapper m1 = new Mapper("2","Ѯ��");
		Mapper m2 = new Mapper("3","�±�");
		Mapper m3 = new Mapper("4","����");
		Mapper m4 = new Mapper("5","���걨");
		Mapper m5 = new Mapper("6","�걨");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		ms.add(m4);
		ms.add(m5);
		return ms;
	}

}
