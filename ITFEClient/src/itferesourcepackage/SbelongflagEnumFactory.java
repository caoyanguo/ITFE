package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-06-03 10:07:13
 */
public class SbelongflagEnumFactory extends ITFEEnumFactory {

	// Ͻ����־
	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0302");
	}

}
