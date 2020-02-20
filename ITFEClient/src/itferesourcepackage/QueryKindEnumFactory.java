package itferesourcepackage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * 1-��˰
2-�걨��
3-����
 * @author caoyg
 * @time   09-11-12 15:51:12
 */
public class QueryKindEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("1","��˰���ݲ�ѯ"));
		list.add(new Mapper("2","�걨�����ݲ�ѯ"));
		list.add(new Mapper("3","TBS��������"));
		return list;
		
	}

}
