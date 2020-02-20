package com.cfcc.itfe.client.dialog;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class ModifySubcodeDialogFacade {

	private static ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory
			.getService(ICommonDataAccessService.class);

	/**
	 * ���޸Ŀ�Ŀ�������
	 * @param dto Ҫ�޸ĵ���ϸ��Ϣdto
	 * @return
	 */
	public static boolean open(IDto dto,String info) {
		final ModifySubcodeDialog dialog = new ModifySubcodeDialog(null, dto,info);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			String subjectcode = dialog.getFunsubjectcode();
			boolean flag=org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��Ϣ��ʾ", "��ȷ��Ҫ�޸ĸÿ�Ŀ������");
			if(flag){
				boolean bol=modifySubcode(dto, subjectcode, info);
				if(!bol){
					return false;
				}
			}else{
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * �޸Ŀ�Ŀ����
	 * 
	 * @return
	 * @throws Exception
	 */
	private static boolean modifySubcode(IDto dto, String subjectcode,String info) {
		try {
			if(info.equals("0")){
				TvPayoutmsgsubDto subdto=(TvPayoutmsgsubDto) dto;
				subdto.setSfunsubjectcode(subjectcode);
				commonDataAccessService.updateData(subdto);
			}else if(info.equals("1")){
				HtvPayoutmsgsubDto hsubdto=(HtvPayoutmsgsubDto) dto;
				hsubdto.setSfunsubjectcode(subjectcode);
				commonDataAccessService.updateData(hsubdto);
			}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "�޸Ŀ�Ŀ����ʧ�ܣ�");
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
