package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-06-04 14:42:35
 */
public class PrintvousignEnumFactory extends ITFEEnumFactory {

	// ��ӡ����ƾ֤��־
	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0307");
	}

}
