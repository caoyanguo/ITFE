package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author tyler
 * @time   13-11-18 16:24:10
 */
public class SVoucherTypeSXEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list=new ArrayList<Mapper>();
		list.add(new Mapper(MsgConstant.VOUCHER_NO_2301,"���л�������"));
		list.add(new Mapper(MsgConstant.VOUCHER_NO_2302,"�����˿�����"));
		list.add(new Mapper(MsgConstant.VOUCHER_NO_5106,"��Ȩ֧�����"));
		list.add(new Mapper(MsgConstant.VOUCHER_NO_5108,"ֱ��֧�����"));
		list.add(new Mapper(MsgConstant.VOUCHER_NO_5207,"ʵ���ʽ�"));
		return list;
	}

}
