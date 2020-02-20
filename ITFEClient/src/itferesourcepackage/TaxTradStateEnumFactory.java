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
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_DEALING,"处理中"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_RECEIVER,"已收妥"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_SUCCESS,"成功"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_FAIL,"失败"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND,"已重发"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_NO_CHECK,"未对账"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_SEND,"已发送"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_NO_SEND,"未发送"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING,"待处理"));
		list.add(new Mapper(DealCodeConstants.DEALCODE_ITFE_CANCELLATION,"已作废"));
		return list;
	}

}
