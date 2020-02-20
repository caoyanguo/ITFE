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
	 * 操作一个生成的Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public ContainerMetaData process(ContainerMetaData metadata) {
		List controls = metadata.controlMetadatas;
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		if ("核算主体下对应的国库列表".equals(metadata.caption)) {
			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ComboMetaData) {
					ComboMetaData combo = (ComboMetaData) controls.get(i);
					if (combo.caption.equals("国库列表项")) {
						combo.visible = false;
					}
				}
			}
		} else if ("button".equals(metadata.type)) {

			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ButtonMetaData) {
					ButtonMetaData button = (ButtonMetaData) controls.get(i);
					if (button.caption.equals("重置密码")) {
						if (!loginfo.isSystemUser()) {
							button.visible = false;
						}
					} else if (button.caption.equals("录入")
							|| (button.caption.equals("删除") && !"itfe_recbiz_uploadmodule_uploadui_delerrordata"
									.equals(button.messageNamePrix))
							|| button.caption.equals("修改")) {
						if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
								.getArea())) {
							button.visible = false;
						} else {
							button.visible = true;
						}
					} else if (button.caption.equals("自动获取密钥机构")
							|| button.caption.equals("密钥更新及导出")
							|| button.caption.equals("已生效密钥导出")) {
						if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
								.getArea())) {
							button.visible = true;
						} else {
							button.visible = false;
						}
					} else if (button.caption.equals("服务器目录加载")
							|| button.caption.equals("导出报表到服务器")|| button.caption.equals("导出业务数据")) {
						if ("200000000002".equals(loginfo.getSorgcode()))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("勾兑入库"))
					{
						if(loginfo.getSorgcode().endsWith("02"))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("参数传送")&&loginfo.getSorgcode().endsWith("02"))
					{
						if(loginfo.getPublicparam().contains(",fileswapcz,"))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("报表传送")&&loginfo.getSorgcode().endsWith("02"))
					{
						if(loginfo.getPublicparam().contains(",fileswapcz,"))
							button.visible = true;
						else
							button.visible = false;
					}

				}
			}
		} else if ("下面要素为收入业务处理专用".equals(metadata.name)) {
			if ("38".equals(loginfo.getSorgcode().substring(0, 2))) {
				for (int i = 0; i < controls.size(); i++) {
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textMeta = (TextMetaData) controls.get(i);
						if ("资金收纳流水号".equals(textMeta.caption)) {
							textMeta.visible = false;
						}
					}
				}
			}
		} else if ("财政电子税票申请".equals(metadata.name)
				|| "报表下载界面".equals(metadata.name)
				|| "申请界面".equals(metadata.name)) {
			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ComboMetaData) {
					ComboMetaData textMeta = (ComboMetaData) controls.get(i);
					if ("申请范围".equals(textMeta.caption)) {
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
				}else if(controls.get(i) instanceof TextMetaData && loginfo.getSorgcode().startsWith("11"))//浙江核算主体开头11
				{
					//征收机关性质-报表范围
					TextMetaData textMeta = (TextMetaData) controls.get(i);
					if ("征收机关性质".equals(textMeta.caption)) {
						textMeta.visible = true;
						textMeta.enable = true;
					}else if ("报表范围".equals(textMeta.caption)) {
						textMeta.visible = true;
						textMeta.enable = true;
					}
				}
			}
		}
		return metadata;
	}
}
