package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author hjr
 * @time   14-11-13 14:41:25
 */
public class RefundtypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		Mapper m1=new Mapper("1","�����˿�");
		Mapper m2=new Mapper("2","ȫ���˿�");
		Mapper m3=new Mapper("5","��ȷ���˿�");
		List<Mapper> list=new ArrayList<Mapper>();
		list.add(m1);
		list.add(m2);
		list.add(m3);
		return list;
	}

}
