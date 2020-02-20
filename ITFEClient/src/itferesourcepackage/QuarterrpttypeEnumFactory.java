package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-08-30 10:58:32
 */
public class QuarterrpttypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("1","��һ����");
		Mapper m1 = new Mapper("2","�ڶ�����");
		Mapper m2 = new Mapper("3","��������");
		Mapper m3 = new Mapper("4","���ļ���");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		return ms;
	}

}
