package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-04-09 08:47:02
 */
public class SubjectTypeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		// 科目代码  - 预算科目种类(收支标志)
		return super.getEnums("0403");
	}

}
