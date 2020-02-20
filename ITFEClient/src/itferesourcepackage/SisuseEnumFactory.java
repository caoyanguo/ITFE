package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   09-11-12 14:47:16
 */
public class SisuseEnumFactory extends ITFEEnumFactory {

		public List<Mapper> getEnums(Object o) {
			return super.getEnums("0109");
	}

}
