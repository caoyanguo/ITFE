package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author zgz
 * @time   13-02-16 14:30:30
 */
public class ColSeparatorEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {

		List ms = new ArrayList();
		ms.add(new Mapper("	","TabÖÆ±í·û"));
		ms.add(new Mapper(",","¶ººÅ"));
		
		return ms;

	}

}
