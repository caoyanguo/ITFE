package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author PBCNN
 * @time   13-06-18 16:20:22
 */
public class TvinfilestateEnumFactory extends ITFEEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0333");
	}

}
