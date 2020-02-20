package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author zhangliang
 * @time   14-04-03 00:45:48
 */
public class ReporttaxorgcodeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m2 = new Mapper("0","��������");
		Mapper m0 = new Mapper("1","����");
		Mapper m1 = new Mapper("2","�����������ջ���");
		Mapper m3 = new Mapper("3","��˰����");//111111111111
		Mapper m4 = new Mapper("4","��˰����");//222222222222
		Mapper m5 = new Mapper("5","���ش���");//333333333333
		Mapper m6 = new Mapper("6","��������");//555555555555
//		Mapper m3 = new Mapper("4","444444444444");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		ms.add(m4);
		ms.add(m5);
		ms.add(m6);
		return ms;
	}

}
