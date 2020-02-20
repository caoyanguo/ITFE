package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-08-16 14:57:43
 */
public class DirectGrantBiztypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("25","ֱ��֧������[25]");
		Mapper m1 = new Mapper("26","ֱ��֧���˿�[26]");
		Mapper m2 = new Mapper("27","��Ȩ֧������[27]");
		Mapper m3 = new Mapper("28","��Ȩ֧���˿�[28]");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		return ms;
	}

}
