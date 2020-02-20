package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author zl
 * @time   13-04-19 15:15:30
 */
public class CommonApplyTypeEnumFactory extends ITFEEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0125");
	}

}
