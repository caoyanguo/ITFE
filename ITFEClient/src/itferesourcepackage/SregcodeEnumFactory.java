package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-04-09 12:35:36
 */
public class SregcodeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0360");
	}

}
