package itferesourcepackage;

import java.util.List;

//import org.eclipse.swt.widgets.Composite;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.ui.metadata.ComboMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.ControlMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;
import com.cfcc.jaf.ui.metadata.TextareaMetaData;
//import com.ibm.security.pkcs7.Content;

/**
 * @author db2admin
 * @time 13-03-14 14:20:41
 */
public class ControlTextVisibleProcessor implements ICompositeMetadataProcessor {
	/**
	 * ����һ�����ɵ�Composite
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
			if (m instanceof TextMetaData) {
				m = (TextMetaData) m;
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
						.getArea())) {
					if (m.caption.equals("��Կ��Ч����")) {
						m.visible = true;
					}
					if (m.caption.equals("�������1")||m.caption.equals("Ԥ���Ŀ����1")) {
						m.visible = false;
					}
				}
				if (loginfo.getPublicparam().contains(",fjz,")){
					if (m.caption.equals("������ID")) {
						m.visible = true;
					}
				}
				if(loginfo.getPublicparam().contains(",ywzyz,"))
				{
					if (m.caption.equals("ҵ��ר����ID")) {
						m.visible = true;
					}
				}
			}
			if (m instanceof TextareaMetaData) {
				m = (TextareaMetaData) m;
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
						.getArea())) {
					if (m.caption.equals("��ť����˵��")) {
						m.visible = true;
					}
				} else {
					m.visible = false;
				}
			}

			if (m instanceof TableMetaData) {
				m = (TableMetaData) m;
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
						.getArea())) {
				} else {
					((TableMetaData) m).columnList.remove(6);
				}
				if (!loginfo.getPublicparam().contains(",sh,") ){
					((TableMetaData) m).columnList.remove(14);
					((TableMetaData) m).columnList.remove(13);
				}

			}
			if (m instanceof ComboMetaData) {
				m = (ComboMetaData) m;
			   if (StateConstant.COMMON_NO.equals(loginfo
						.getSysflag())) {
					if (m.caption.equals("���������к� ")) {
						m.visible = false;
					}
				} else {
					if (m.caption.equals("���������к� ")) {
						m.visible = true;
					}
				}
			}
			
			if (m instanceof TextMetaData) {
				m = (TextMetaData) m;
			   if (StateConstant.COMMON_NO.equals(loginfo
						.getSysflag())) {
					if (m.caption.equals("���������к� ")) {
						m.visible = true;
					}
				} else {
					if (m.caption.equals("���������к� ")) {
						m.visible = false;
					}
				}
			}
		}
		return metadata;
	}

}
