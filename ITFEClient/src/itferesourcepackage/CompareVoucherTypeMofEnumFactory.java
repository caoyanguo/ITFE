package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   11-09-06 13:18:37
 */
public class CompareVoucherTypeMofEnumFactory  extends ITFEEnumFactory  {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0431");
	}

}
