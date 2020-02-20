package com.cfcc.itfe.client.dialog;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tscheckfailreason.ITsCheckFailReasonService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.util.MessageDialog;
public class CheckFaultMsgDialogFacade {
	private static Log log = LogFactory.getLog(BursarAffirmDialogFacade.class);

	private static ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	/**
	 * ����Ȩ����
	 * 
	 * @param checkfail
	 * @param message
	 *            ��Ȩ�����е���ʾ��Ϣ
	 * @return
	 * @throws ITFEBizException
	 */
	public static boolean open(TvVoucherinfoDto dto,
			List<TsCheckfailreasonDto> checkfail,
			ICommonDataAccessService commonDataAccessService)
			throws ITFEBizException {
		final CheckFaultMsgDialog dialog = new CheckFaultMsgDialog(null,
				checkfail);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			String selectmsg = dialog.getSelectmsg();
			String failcode = dialog.getInputfailcode();
			String inputmsg = dialog.getInputmessage();
			StringBuffer msg = new StringBuffer();
			if ((selectmsg != null && !selectmsg.equals(""))
					|| (inputmsg != null && !inputmsg.equals("")
							&& failcode != null && !failcode.equals(""))) {
				if (selectmsg != null && !selectmsg.equals("")) {
					dto.setSdemo(selectmsg.substring(
							selectmsg.indexOf("_") + 1, selectmsg.length()));
				} else {
					dto.setSdemo(inputmsg);
					TsCheckfailreasonDto checkfailreasondto = new TsCheckfailreasonDto();
					
					TsCheckfailreasonDto checkdouble = new TsCheckfailreasonDto();
					checkdouble.setSorgcode(dto.getSorgcode());// �����������
					checkdouble.setScheckfailcode(failcode);
					List<TsCheckfailreasonDto> list = commonDataAccessService.findRsByDto(checkdouble);
					if(list!=null && list.size()>0){
						MessageDialog.openMessageDialog(null, "ʧ��ԭ������ظ������������룡");
						CheckFaultMsgDialogFacade.open(dto, checkfail,
								commonDataAccessService);
					}else{
						checkfailreasondto.setSorgcode(dto.getSorgcode());// �����������
						checkfailreasondto.setScheckfailcode(failcode);
						checkfailreasondto.setScheckfaildsp(inputmsg);
						checkfailreasondto.setTssysupdate(DateUtil
								.currentTimestamp());
						commonDataAccessService.create(checkfailreasondto);
					}
				}
				return true;

			} else {
				MessageDialog.openMessageDialog(null, "��ѡ�������ʧ��ԭ��");
				CheckFaultMsgDialogFacade.open(dto, checkfail,
						commonDataAccessService);
			}
		}
		return false;
	}

}
