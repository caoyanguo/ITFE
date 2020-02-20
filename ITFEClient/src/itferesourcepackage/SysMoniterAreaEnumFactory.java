package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-01-15 11:29:37
 */
public class SysMoniterAreaEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0","操作系统");
		Mapper m1 = new Mapper("1","数据库");
		Mapper m2 = new Mapper("2","MQ");
		Mapper m3 = new Mapper("3","应用服务器");
		Mapper m4 = new Mapper("4","CA证书");
		Mapper m5 = new Mapper("5","应用日志");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		ms.add(m3);
		ms.add(m4);
		ms.add(m5);
		return ms;
	}

}