package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author wangtuo
 * @time 10-04-09 08:47:02
 */
public class SubjectTypeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		// ��Ŀ����  - Ԥ���Ŀ����(��֧��־)
		return super.getEnums("0403");
	}

}
