package itferesourcepackage;

import java.util.List;

import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author VAIO
 * @time 10-04-09 08:47:02
 */
public class WriteFlagEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		// ��Ŀ����  - ¼���־
		return super.getEnums("0404");
	}

}
