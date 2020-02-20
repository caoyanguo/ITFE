package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author win7
 * @time   13-09-17 07:01:58
 * Ö§¸¶·½Ê½ spaytypecode = 0127
 */
public class SpaytypecodeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0127");
	}
}
