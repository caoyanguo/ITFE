package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author lenovo
 * @time   13-09-04 15:57:37
 */
public class VoucherRegularCheckAccountSvtcodeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		if(o!=null&& o instanceof String&&o.toString().length()==4)
			return super.getEnums(o.toString());
		else
			return super.getEnums("0427");
	}

}
