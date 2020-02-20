package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
/**
 * 是否自动读取 0 自动 1 本地';是否自动提交 0 自动 1 本地';是否自动签章 0 自动 1 本地 ';(签章成功)是否自动发回执 0 自动 1 本地;'失败）是否自动发回执 0 自动 1 本地'
 * @author db2admin
 * @time   13-07-31 14:30:14
 */
public class VoucherAutoEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		// TODO Auto-generated method stub
		return super.getEnums("0421");
	}

}
