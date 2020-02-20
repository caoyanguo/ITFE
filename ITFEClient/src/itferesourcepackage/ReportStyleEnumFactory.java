package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   14-06-12 15:50:46
 */
public class ReportStyleEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0","�����ձ�");
		Mapper m1 = new Mapper("1","����ձ�");
		Mapper m2 = new Mapper("2","�ֻ���");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		return ms;
	}

}
