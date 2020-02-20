package itferesourcepackage;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ButtonMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.sun.security.sasl.ServerFactoryImpl;

/**
 * ����ҵ��ƾ֤������水ť��ʾ
 * 
 * @author Administrator
 * @time 15-06-09 11:24:39
 */
public class ConlBizProcButtonVisibleProcessor implements
		ICompositeMetadataProcessor {
	/**
	 * ����һ�����ɵ�Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public ContainerMetaData process(ContainerMetaData metadata) {
		ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory
				.getService(ICommonDataAccessService.class);
		List controls = metadata.controlMetadatas;
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		TsUsersDto idto = new TsUsersDto();
		idto.setSorgcode(loginfo.getSorgcode());
		idto.setSusercode(loginfo.getSuserCode());
		List<TsUsersDto> list;
		try {
			list = (List<TsUsersDto>) commonDataAccessService.findRsByDto(idto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		String ss = list.get(0).getShold2();
		if (null == ss || "".equals(ss.trim())) {
			return null;
		}
		String[] arg = ss.split(",");
		if ("button".equals(metadata.type)) {
			for (int i = 0; i < controls.size(); i++) {
				if (controls.get(i) instanceof ButtonMetaData) {
					ButtonMetaData button = (ButtonMetaData) controls.get(i);
					if ((button.caption.equals("����У��") && StateConstant.COMMON_YES
							.equals(arg[0]))
							|| (button.caption.equals("�ύ") && StateConstant.COMMON_YES
									.equals(arg[1]))
							|| (button.caption.equals("����״̬") && StateConstant.COMMON_YES
									.equals(arg[2]))
							|| (button.caption.equals("ǩ��") && StateConstant.COMMON_YES
									.equals(arg[3]))
							|| (button.caption.equals("ǩ�³���") && StateConstant.COMMON_YES
									.equals(arg[4]))
							|| (button.caption.equals("���ͻص�") && StateConstant.COMMON_YES
									.equals(arg[5]))
							|| (button.caption.equals("�˻�") && StateConstant.COMMON_YES
									.equals(arg[6])&&arg.length>=7)
							|| (button.caption.equals("�ص�״̬��ѯ") && StateConstant.COMMON_YES
									.equals(arg[7])&&arg.length>=8)
							|| (button.caption.equals("����ƾ֤��ԭչʾ") && StateConstant.COMMON_YES
									.equals(arg[8])&&arg.length>=9)) {
						button.visible = true;
					}else if(button.caption.equals("����PDF�ļ�"))
					{
						if(arg.length>9&&StateConstant.COMMON_YES.equals(arg[9]))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("��ϸУ��"))
					{
						if(loginfo.getPublicparam().contains(",bigdatavalid,"))
							button.visible = true;
						else
							button.visible = false;
					}else if(button.caption.equals("����"))
					{
						if(loginfo.getPublicparam().contains(",vou=export,"))
							button.visible = true;
						else
							button.visible = false;
					}
				}
			}
		}
		return metadata;
	}
}
