package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * 这是跟TBS保持一致的业务类型，如划款申请分直接和授权
 * @author hua
 * @time   14-09-10 10:31:02
 */
public class TBSBizTypeForBJEnumFactory implements IEnumFactory {
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maps = new ArrayList<Mapper>();
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_RET_TREASURY ,"退库"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAY_OUT, "实拨资金"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN, "直接支付额度"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, "授权支付额度"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY, "直接支付清算"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY, "授权支付清算"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK, "直接支付清算退回"));
		maps.add(new Mapper(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK, "授权支付清算退回"));
		return maps;
	}

}
