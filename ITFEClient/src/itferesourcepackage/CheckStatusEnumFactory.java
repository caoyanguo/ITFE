package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author hjr
 * @time   14-03-10 10:04:21
 */
public class CheckStatusEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0438");
	}

}
