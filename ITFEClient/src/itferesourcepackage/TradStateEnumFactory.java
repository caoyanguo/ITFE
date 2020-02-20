package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author t60
 * @time   12-03-05 10:41:58
 */
public class TradStateEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		/*// TODO Auto-generated method stub
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
		return list;*/
		return super.getEnums("0300");
	}

}
