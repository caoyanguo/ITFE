package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author VAIO
 * @time 10-04-09 08:47:02
 */
public class WriteFlagEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		// 科目代码  - 录入标志
		return super.getEnums("0404");
	}

}
