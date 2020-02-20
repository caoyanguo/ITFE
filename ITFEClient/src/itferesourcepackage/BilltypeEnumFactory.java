package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author caoyg
 * @time   09-12-10 16:54:10
 */
public class BilltypeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0201");
	}

}
