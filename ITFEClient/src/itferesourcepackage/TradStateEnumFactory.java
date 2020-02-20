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
		return list;*/
		return super.getEnums("0300");
	}

}
