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
	 * 操作一个生成的Composite
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
					if(button.caption.equals("签章")||button.caption.equals("签章撤销")||button.caption.equals("回单签章"))
					{
						if(!loginfo.getPublicparam().contains(",stampmode=sign,"))//是否采用签名
						{
							button.visible = true;
						}else
						{
							button.visible = false;
						}
					}else if(button.caption.equals("导入")||button.caption.equals("录入")||button.caption.equals("修改")||button.caption.equals("删除"))
					{
						if(loginfo.getPublicparam().contains(",xm5207,"))
						{
							if("2".equals(loginfo.getSuserType()))//厦门要求业务主管可维护，操作员只能查看
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
		}else if("凭证退回业务信息查询".equals(metadata.caption))
		{
			for (int i = 0; i < controls.size(); i++)
			{
				 if(controls.get(i) instanceof ComboMetaData)
					{
						ComboMetaData combometadata = (ComboMetaData) controls.get(i);
						if (combometadata.caption.equals("接收方")&&(loginfo.getPublicparam().contains(",send3208=more,")||loginfo.getPublicparam().contains(",send3508=more,")||loginfo.getPublicparam().contains(",send3510=more,"))) {
							combometadata.visible = true;
						}
					}
			}
		}
		return metadata;
	}

}
