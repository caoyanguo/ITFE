package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * ҵ�����ͱ���ö��ֵ
 * @author hejianrong
 * @time   14-10-31 17:03:05
 */
public class BusinessTypeCodeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("1","��ͨҵ��");
		Mapper m1 = new Mapper("3","����ҵ��");
		Mapper m2 = new Mapper("4","��������ҵ��");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		return ms;
	}

}
