package itferesourcepackage;

import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.ControlMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;
import com.cfcc.jaf.ui.metadata.TextareaMetaData;
//import com.ibm.security.pkcs7.Content;

/**
 * @author db2admin
 * @time   13-04-17 03:33:49
 */
public class ControlTableColumnVisibleProcessor implements ICompositeMetadataProcessor {
	/**
	 * 操作一个生成的Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public ContainerMetaData process(ContainerMetaData metadata) {
		List controlMetadatas = metadata.getControlMetadatas();
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		for (int i = 0; i < controlMetadatas.size(); i++) {
			ControlMetaData m = (ControlMetaData) controlMetadatas.get(i);
			
			if (m instanceof TableMetaData) {
				m = (TableMetaData) m;
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
						.getArea())) {
					((TableMetaData) m).columnList.remove(0);
				} else {
					((TableMetaData) m).columnList.remove(1);
				}
			}
		}
		return metadata;
	}
}

