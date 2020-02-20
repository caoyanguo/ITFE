package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author hejianrong
 * @time   14-06-13 17:11:58
 */
public class VoucherRegularStatusEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("71","����ɹ�");
		Mapper m1 = new Mapper("73","ǩ�³ɹ�");
		Mapper m2 = new Mapper("80","���͵���ƾ֤��ɹ�");
		Mapper m3 = new Mapper("5","�ѻص�");		
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		return ms;
	}

}
