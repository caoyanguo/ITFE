package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author whj
 * @time   13-09-04 16:01:26
 */
public class DztypeEnumFactory implements IEnumFactory {

	/*
	 * 
	 * 0:��������1:�������ж���
	 * */
	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0","��������");
		Mapper m1 = new Mapper("1","�������ж���");
		ms.add(m0);
		ms.add(m1);
		return ms;
	}

}
