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
 * ���ܣ��㽭�����������ؿؼ�
 * @author hejianrong
 * @time   14-07-31 14:07:30
 */
public class ZjFinorgWidgetisvisbleProcessor implements ICompositeMetadataProcessor {
	
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
		//�㽭�����������ؿؼ�
		if(!loginfo.getSorgcode().startsWith("11")){
			if ("��Ϣ¼��".equals(metadata.caption) || "��Ϣ�޸�".equals(metadata.caption)) {
				for (int i = 0; i < controls.size(); i++) {
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textmetadata = (TextMetaData) controls.get(i);
						if (textmetadata.caption.equals("�����ֱ�ʶ")) {
							textmetadata.visible = false;
						}
					}				
				}
			} 
			if ("������ѯһ����".equals(metadata.caption)){
				TableMetaData tablemd = (TableMetaData) controls.get(0);
				for (int i = 0; i < tablemd.columnList.size(); i++) {
					if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
						ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
						if (coletadata.title.equals("�����ֱ�ʶ")) {
							coletadata.visible = false;
						}
					}
				}
			}			
		}			
		return metadata;
	}

}
