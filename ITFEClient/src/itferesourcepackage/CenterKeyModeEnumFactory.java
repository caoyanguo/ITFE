package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * 中心密钥设置模式
 * @author db2admin
 * @time   12-06-21 16:49:21
 */
public class CenterKeyModeEnumFactory extends ITFEEnumFactory{

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0413");
	}

}
