package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-06-04 15:38:38
 */
public class CreckontypeEnumFactory extends ITFEEnumFactory {

	// ÇåËãÇşµÀ
	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0311");
	}

}
