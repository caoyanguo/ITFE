package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-06-04 14:34:36
 */
public class HandletypeEnumFactory extends ITFEEnumFactory {

	// �������
	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0306");
	}

}
