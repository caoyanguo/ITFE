package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-04-09 08:47:02
 */
public class SubjectAttrEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		// 科目代码  - 科目属性 
		return super.getEnums("0402");
	}

}
