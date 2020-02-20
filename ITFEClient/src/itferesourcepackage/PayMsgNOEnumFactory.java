package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-03-08 10:52:35
 */
public class PayMsgNOEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("111","�ͻ�������ҵ����111(����)"));
		list.add(new Mapper("112","���ڻ���������ҵ����112(����)"));
		list.add(new Mapper("121","�ͻ�������ͨ����ҵ����(����)121"));
		list.add(new Mapper("122","���ڻ���������ͨ����ҵ����122(����)"));
		list.add(new Mapper("103","���֧���㻮CMT103(һ��)"));
		list.add(new Mapper("100","���֧�����CMT100(һ��)"));
		list.add(new Mapper("001","С����ͨ����PKG001(һ��)"));
		return list;
	}

}
