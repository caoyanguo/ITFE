package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author zhangliang
 * @time   14-10-30 15:46:02
 */
public class ReportBussReadReturnEnumFactory extends ITFEEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		if(o!=null&& o instanceof String&&o.toString().length()==4)
			return super.getEnums(o.toString());
		else
			return super.getEnums("0440");
	}

}
