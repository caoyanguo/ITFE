package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * ���Ǹ�TBS����һ�µ�ҵ�����ͣ��绮�������ֱ�Ӻ���Ȩ
 * @author hua
 * @time   14-09-10 10:31:02
 */
public class TBSBizTypeForBJEnumFactory implements IEnumFactory {
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maps = new ArrayList<Mapper>();
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_RET_TREASURY ,"�˿�"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAY_OUT, "ʵ���ʽ�"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN, "ֱ��֧�����"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, "��Ȩ֧�����"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY, "ֱ��֧������"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY, "��Ȩ֧������"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK, "ֱ��֧�������˻�"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK, "��Ȩ֧�������˻�"));
		return maps;
	}

}
