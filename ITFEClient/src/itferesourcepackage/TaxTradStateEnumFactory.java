package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-04-15 02:50:40
 */
public class TaxTradStateEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list=new ArrayList<Mapper>();
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_DEALING,"������"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_RECEIVER,"������"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_SUCCESS,"�ɹ�"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_FAIL,"ʧ��"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND,"���ط�"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_NO_CHECK,"δ����"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_SEND,"�ѷ���"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_NO_SEND,"δ����"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING,"������"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_CANCELLATION,"������"));
		return list;
	}

}
