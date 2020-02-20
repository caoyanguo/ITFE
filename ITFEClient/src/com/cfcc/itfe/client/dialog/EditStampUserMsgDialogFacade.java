package com.cfcc.itfe.client.dialog;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.util.StringUtil;
public class EditStampUserMsgDialogFacade {
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
	public static boolean open(List<TsStamppositionDto> dtolist,ICommonDataAccessService commonDataAccessService)
			throws ITFEBizException {
		final EditStampUserMsgDialog dialog = new EditStampUserMsgDialog(null,"ǩ���û�");
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			String selectmsg = dialog.getSelectmsg();
			if(selectmsg!=null&&selectmsg.contains("***"))
			{
				String userid = StringUtil.split(selectmsg, "***")[0];
				for(TsStamppositionDto dto:dtolist)
					dto.setSstampuser(userid);
				commonDataAccessService.updateDtos(dtolist);
			}
		}
		return false;
	}

}
