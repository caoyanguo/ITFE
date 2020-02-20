package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * ���ڵ���TBS���ܶ����ҵ������
 * 
 * @author win7
 * @time 13-10-29 09:46:42
 */
public class SBIZTYPETBSEnumFactory implements IEnumFactory {

	/**
	 * TV_PAYOUTMSGMAIN(SUB)����ʵ���ʽ� TV_DIRECTPAYMSGMAIN(SUB)ֱ��֧�����
	 * TV_GRANTPAYMSGMAIN(SUB)��Ȩ֧����� TV_PAYRECK_BANK ���а���֧�����������룩
	 */
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maps = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0", "ʵ���ʽ�");
		Mapper m1 = new Mapper("1", "ֱ��֧�����");
		Mapper m2 = new Mapper("2", "��Ȩ֧�����");
		Mapper m3 = new Mapper("3", "����֧����������");
		maps.add(m0);
		maps.add(m1);
		maps.add(m2);
		maps.add(m3);
		return maps;
	}

}
