package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author caoyg
 * @time   09-12-11 09:59:04
 */
public class BdglevelEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0121");
	}

}
