package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author zl
 * @time   14-03-03 13:21:56
 */
public class FtpfilestateEnumFactory extends ITFEEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list=null;
		list = super.getEnums("0461");
		if(list==null||list.size()<=0)
		{
			list = new ArrayList<Mapper>();
			list.add(new Mapper(StateConstant.FTPFILESTATE_DOWNLOAD,"已读取"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_ADDLOAD,"已加载"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_RETURN,"已回执"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_SUMMARY,"汇总文件"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_UNKNOWN,"非法文件"));
		}
		return list;
	}

}
