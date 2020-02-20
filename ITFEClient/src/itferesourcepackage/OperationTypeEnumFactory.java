package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   09-12-15 15:30:15
 */
public class OperationTypeEnumFactory extends ITFEEnumFactory  {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0201");
	}

}
