package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author Administrator
 * @time   17-04-04 13:58:09
 */
public class FundClearVouTypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		Mapper mp0 = new Mapper("1","ʵ��");
		Mapper mp1 = new Mapper("2","�˿�");
		Mapper mp2 = new Mapper("3","���л���");
		Mapper mp3 = new Mapper("4","����");
		list.add(mp0);
		list.add(mp1);
		list.add(mp2);
		list.add(mp3);
		return list;
	}

}
