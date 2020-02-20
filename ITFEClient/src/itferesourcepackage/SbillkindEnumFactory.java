package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author t60
 * @time   12-02-28 15:52:40
 */
public class SbillkindEnumFactory extends ITFEEnumFactory {
	// 报表种类
	public List<Mapper> getEnums(Object o) {
		// TODO Auto-generated method stub
		return super.getEnums("0303");
	}

}
