package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * 业务类型编码枚举值
 * @author hejianrong
 * @time   14-10-31 17:03:05
 */
public class BusinessTypeCodeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("1","普通业务");
		Mapper m1 = new Mapper("3","工资业务");
		Mapper m2 = new Mapper("4","其他批量业务");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		return ms;
	}

}
