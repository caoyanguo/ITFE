package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author Administrator
 * @time   12-10-27 22:45:10
 */
public class ConfirmStatusEnumFactory extends ITFEEnumFactory{

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0415");
	}

}
