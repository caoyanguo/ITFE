package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * 用于导出TBS功能定义的业务类型
 * 
 * @author win7
 * @time 13-10-29 09:46:42
 */
public class SBIZTYPETBSEnumFactory implements IEnumFactory {

	/**
	 * TV_PAYOUTMSGMAIN(SUB)导出实拨资金 TV_DIRECTPAYMSGMAIN(SUB)直接支付额度
	 * TV_GRANTPAYMSGMAIN(SUB)授权支付额度 TV_PAYRECK_BANK 商行办理支付（划款申请）
	 */
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maps = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0", "实拨资金");
		Mapper m1 = new Mapper("1", "直接支付额度");
		Mapper m2 = new Mapper("2", "授权支付额度");
		Mapper m3 = new Mapper("3", "集中支付划款申请");
		maps.add(m0);
		maps.add(m1);
		maps.add(m2);
		maps.add(m3);
		return maps;
	}

}
