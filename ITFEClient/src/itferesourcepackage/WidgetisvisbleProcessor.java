package itferesourcepackage;

import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.ui.metadata.ButtonMetaData;
import com.cfcc.jaf.ui.metadata.ComboMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;

/**
 * @author db2admin
 * @time 12-06-25 10:39:11
 */
public class WidgetisvisbleProcessor implements ICompositeMetadataProcessor {
	/**
	 * ����һ�����ɵ�Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public ContainerMetaData process(ContainerMetaData metadata) {
		List controls = metadata.controlMetadatas;
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		if ("���������¶�Ӧ�Ĺ����б�".equals(metadata.caption)) {
			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ComboMetaData) {
					ComboMetaData combo = (ComboMetaData) controls.get(i);
					if (combo.caption.equals("�����б���")) {
						combo.visible = false;
					}
				}
			}
		} else if ("button".equals(metadata.type)) {

			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ButtonMetaData) {
					ButtonMetaData button = (ButtonMetaData) controls.get(i);
					if (button.caption.equals("��������")) {
						if (!loginfo.isSystemUser()) {
							button.visible = false;
						}
					} else if (button.caption.equals("¼��")
							|| (button.caption.equals("ɾ��") && !"itfe_recbiz_uploadmodule_uploadui_delerrordata"
									.equals(button.messageNamePrix))
							|| button.caption.equals("�޸�")) {
						if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
								.getArea())) {
							button.visible = false;
						} else {
							button.visible = true;
						}
					} else if (button.caption.equals("�Զ���ȡ��Կ����")
							|| button.caption.equals("��Կ���¼�����")
							|| button.caption.equals("����Ч��Կ����")) {
						if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
								.getArea())) {
							button.visible = true;
						} else {
							button.visible = false;
						}
					} else if (button.caption.equals("������Ŀ¼����")
							|| button.caption.equals("��������������")|| button.caption.equals("����ҵ������")) {
						if ("200000000002".equals(loginfo.getSorgcode()))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("�������"))
					{
						if(loginfo.getSorgcode().endsWith("02"))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("��������")&&loginfo.getSorgcode().endsWith("02"))
					{
						if(loginfo.getPublicparam().contains(",fileswapcz,"))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("������")&&loginfo.getSorgcode().endsWith("02"))
					{
						if(loginfo.getPublicparam().contains(",fileswapcz,"))
							button.visible = true;
						else
							button.visible = false;
					}

				}
			}
		} else if ("����Ҫ��Ϊ����ҵ����ר��".equals(metadata.name)) {
			if ("38".equals(loginfo.getSorgcode().substring(0, 2))) {
				for (int i = 0; i < controls.size(); i++) {
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textMeta = (TextMetaData) controls.get(i);
						if ("�ʽ�������ˮ��".equals(textMeta.caption)) {
							textMeta.visible = false;
						}
					}
				}
			}
		} else if ("��������˰Ʊ����".equals(metadata.name)
				|| "�������ؽ���".equals(metadata.name)
				|| "�������".equals(metadata.name)) {
			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ComboMetaData) {
					ComboMetaData textMeta = (ComboMetaData) controls.get(i);
					if ("���뷶Χ".equals(textMeta.caption)) {
						if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
								.getArea())) {
							if(loginfo.getSorgcode().startsWith("20"))
								textMeta.visible = true;
							else
								textMeta.visible = false;
						} else {
							textMeta.visible = true;
						}

					}
				}else if(controls.get(i) instanceof TextMetaData && loginfo.getSorgcode().startsWith("11"))//�㽭�������忪ͷ11
				{
					//���ջ�������-����Χ
					TextMetaData textMeta = (TextMetaData) controls.get(i);
					if ("���ջ�������".equals(textMeta.caption)) {
						textMeta.visible = true;
						textMeta.enable = true;
					}else if ("����Χ".equals(textMeta.caption)) {
						textMeta.visible = true;
						textMeta.enable = true;
					}
				}
			}
		}
		return metadata;
	}
}
