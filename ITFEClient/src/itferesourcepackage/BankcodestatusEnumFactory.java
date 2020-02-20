package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author Administrator
 * @time   13-11-02 14:19:48
 */
public class BankcodestatusEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0437");
	}

}
