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
			list.add(new Mapper(StateConstant.FTPFILESTATE_DOWNLOAD,"�Ѷ�ȡ"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_ADDLOAD,"�Ѽ���"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_RETURN,"�ѻ�ִ"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_SUMMARY,"�����ļ�"));
			list.add(new Mapper(StateConstant.FTPFILESTATE_UNKNOWN,"�Ƿ��ļ�"));
		}
		return list;
	}

}
