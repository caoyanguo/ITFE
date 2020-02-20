package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * ÕÀªÿ±Í÷æ = 0117
 * @author win7
 * @time   13-11-04 09:37:37
 */
public class BackflagEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0117");
	}


}
