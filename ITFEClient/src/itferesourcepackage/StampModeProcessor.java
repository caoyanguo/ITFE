package itferesourcepackage;

import java.util.List;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.ui.metadata.ButtonMetaData;
import com.cfcc.jaf.ui.metadata.ComboMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * @author zhangliang
 * @time   14-11-13 15:09:52
 */
@SuppressWarnings({ "unchecked", "deprecation" })
public class StampModeProcessor implements ICompositeMetadataProcessor {
	/**
	 * ����һ�����ɵ�Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public ContainerMetaData process(ContainerMetaData metadata){
		List controls = metadata.controlMetadatas;
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		if ("button".equals(metadata.type)) {

			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ButtonMetaData) {
					ButtonMetaData button = (ButtonMetaData) controls.get(i);
					if(button.caption.equals("ǩ��")||button.caption.equals("ǩ�³���")||button.caption.equals("�ص�ǩ��"))
					{
						if(!loginfo.getPublicparam().contains(",stampmode=sign,"))//�Ƿ����ǩ��
						{
							button.visible = true;
						}else
						{
							button.visible = false;
						}
					}else if(button.caption.equals("����")||button.caption.equals("¼��")||button.caption.equals("�޸�")||button.caption.equals("ɾ��"))
					{
						if(loginfo.getPublicparam().contains(",xm5207,"))
						{
							if("2".equals(loginfo.getSuserType()))//����Ҫ��ҵ�����ܿ�ά��������Աֻ�ܲ鿴
							{
								button.visible = true;
							}else
							{
								button.visible = false;
							}
						}
					}
				}
			}
		}else if("ƾ֤�˻�ҵ����Ϣ��ѯ".equals(metadata.caption))
		{
			for (int i = 0; i < controls.size(); i++)
			{
				 if(controls.get(i) instanceof ComboMetaData)
					{
						ComboMetaData combometadata = (ComboMetaData) controls.get(i);
						if (combometadata.caption.equals("���շ�")&&(loginfo.getPublicparam().contains(",send3208=more,")||loginfo.getPublicparam().contains(",send3508=more,")||loginfo.getPublicparam().contains(",send3510=more,"))) {
							combometadata.visible = true;
						}
					}
			}
		}
		return metadata;
	}

}
