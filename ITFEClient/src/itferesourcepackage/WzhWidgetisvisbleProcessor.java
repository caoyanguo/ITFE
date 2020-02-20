package itferesourcepackage;

import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.ui.metadata.ButtonMetaData;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ComboMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;

/**
 * @author db2itfe
 * @time   13-09-12 09:59:18
 */
public class WzhWidgetisvisbleProcessor implements ICompositeMetadataProcessor {
	/**
	 * ����һ�����ɵ�Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public ContainerMetaData process(ContainerMetaData metadata){
		List controls = metadata.controlMetadatas;
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		//��ֽ��ϵͳ���ؿؼ�
		if(loginfo.getSysflag().equals("0")){
			if(loginfo.getOrgKind().equals("000000000000")){
				if ("��Ϣ¼��".equals(metadata.caption) || "��Ϣ�޸�".equals(metadata.caption)) {
					for (int i = 0; i < controls.size(); i++) {
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textmetadata = (TextMetaData) controls.get(i);
							if (textmetadata.caption.equals("��Ӧ֧���к�")) {
								textmetadata.visible = false;
							}
						}
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combo = (ComboMetaData) controls.get(i);
							if (combo.caption.equals("��������") || combo.caption.equals("�����Ƿ����")) {
								combo.visible = false;
							}
						}
					}
				} 
				if ("ά����ѯ���".equals(metadata.caption)){
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if (coletadata.title.equals("��Ӧ֧���к�")||coletadata.title.equals("��������") || coletadata.title.equals("�����Ƿ����")) {
								coletadata.visible = false;
							}
						}
					}
				}
				
			}else{
				for (int i = 0; i < controls.size(); i++) {
					if (controls.get(i) instanceof ButtonMetaData) {
						ButtonMetaData btn = (ButtonMetaData) controls.get(i);
						if(loginfo.getSuserType().equals(StateConstant.User_Type_MainBiz)){//������ܿ��Ա༭����
							if (btn.caption.equals("¼��")||btn.caption.equals("ɾ��")||btn.caption.equals("�޸�")
									|| btn.caption.equals("����ȫ���ص�") || btn.caption.equals("����ѡ�лص�") ) {
								btn.visible = true;
							}
						}else{
							if (btn.caption.equals("¼��")||btn.caption.equals("ɾ��")||btn.caption.equals("�޸�")
									|| btn.caption.equals("����ȫ���ص�") || btn.caption.equals("����ѡ�лص�") ) {
								btn.visible = false;
							}
						}
						
						if(loginfo.getSorgcode().startsWith("11")){//�㽭ʵ���ʽ��ѯ����������ؿؼ�
							if (btn.caption.equals("����")||btn.caption.equals("����")||btn.caption.equals("�޸�")) {
								btn.visible = true;
							}
						}
						if(loginfo.getPublicparam().indexOf(",sh,")>=0){
							if (btn.id.equals("bc51c53b-be20-4530-a39d-33184c5aa61c")) {
								btn.visible = true;
							}
						}
						if (btn.caption.equals("�����ӡ")&& btn.id.equals("565bf6e8-4aa2-4ebe-886b-60caccb250d0")) {
							if(loginfo.getSorgcode().startsWith("3302")){
								btn.visible = true;
							}else{
								btn.visible = false;
							}
							
						}
						
					}
					if (controls.get(i) instanceof ComboMetaData) {
						ComboMetaData combo = (ComboMetaData) controls.get(i);
						if(loginfo.getPublicparam().indexOf(",sh,")>=0){
							if (combo.id.equals("b716ab51-c1ad-4e65-80cf-22e9d6682a46")) {
								combo.visible = true;
							}
							if (combo.id.equals("f914967f-8d9f-4558-bf86-1218e9c55007")) {
								combo.visible = false;
							}
						}
					}
				}
			}
		}
		
		return metadata;
	}

}
