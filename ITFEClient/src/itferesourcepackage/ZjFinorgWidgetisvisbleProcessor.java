package itferesourcepackage;

import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;

/**
 * 功能：浙江财政机构隐藏控件
 * @author hejianrong
 * @time   14-07-31 14:07:30
 */
public class ZjFinorgWidgetisvisbleProcessor implements ICompositeMetadataProcessor {
	
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
		//浙江财政机构隐藏控件
		if(!loginfo.getSorgcode().startsWith("11")){
			if ("信息录入".equals(metadata.caption) || "信息修改".equals(metadata.caption)) {
				for (int i = 0; i < controls.size(); i++) {
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textmetadata = (TextMetaData) controls.get(i);
						if (textmetadata.caption.equals("财政局标识")) {
							textmetadata.visible = false;
						}
					}				
				}
			} 
			if ("参数查询一览表".equals(metadata.caption)){
				TableMetaData tablemd = (TableMetaData) controls.get(0);
				for (int i = 0; i < tablemd.columnList.size(); i++) {
					if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
						ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
						if (coletadata.title.equals("财政局标识")) {
							coletadata.visible = false;
						}
					}
				}
			}			
		}			
		return metadata;
	}

}
