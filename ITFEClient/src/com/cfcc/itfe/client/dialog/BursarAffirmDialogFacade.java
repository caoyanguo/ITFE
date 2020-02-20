package com.cfcc.itfe.client.dialog;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class BursarAffirmDialogFacade {

	private static Log log = LogFactory.getLog(BursarAffirmDialogFacade.class);

	private static ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	private static ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory
			.getService(ICommonDataAccessService.class);

	/**
	 * ����Ȩ����
	 * @param message ��Ȩ�����е���ʾ��Ϣ
	 * @return
	 */
	public static boolean open(String message) {
		final BursarAffirmDialog dialog = new BursarAffirmDialog(null, message);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			String password = dialog.getLoginPassword();
			String usercode = dialog.getLoginUserCode();
			StringBuffer msg = new StringBuffer();

			if (StringUtils.isBlank(usercode)) {
				msg.append("�������û����\n");
			} else if (usercode.trim().getBytes().length > 30) {
				msg.append("�û���Ų��ܳ���30λ��\n");
			}
			if (StringUtils.isBlank(password)) {
				msg.append("����������\n");
			}
			if (!msg.toString().equals("")) {
				MessageDialog.openMessageDialog(Display.getDefault()
						.getActiveShell(), msg.toString());
				return open(message);
			}
			// �û���У��ʧ�����µ���
			if (!checkin(usercode, password, loginfo.getSorgcode())) {
				return open(message);
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * �û�У��
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static boolean checkin(String usercode, String password,
			String sorgcode) {

		TsUsersDto dto = new TsUsersDto();
		dto.setSusercode(usercode);
		dto.setSpassword(new Md5App().makeMd5(password.trim()));
		dto.setSorgcode(sorgcode);
		dto.setSusertype("2");//����
		try {
			// ȡ����־��Ϣ
			List<TsUsersDto> dtolist = commonDataAccessService.findRsByDto(dto);
			if (null == dtolist || dtolist.size() != 1) {
				Exception ex = new Exception("�û�id�����������");
				log.error(ex);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), ex);
				return false;
			}
		} catch (Exception e) {
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			log.error(e);
			return false;
		}
		return true;
	}

}
