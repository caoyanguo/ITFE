package com.cfcc.itfe.client.dialog;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class BuLUBankNoDialogFacade {
	private static Log log = LogFactory.getLog(BursarAffirmDialogFacade.class);
	
	private static ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	/** service */
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
   /**
	 * ��ҵ��Ҫ�ز�¼����,����ҵ����
	 * 
	 * @param dto
	 * @param banklist
	 * @param bankname
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public static IDto openDialogProc(IDto dto, List<TsPaybankDto> banklist,
			String bankname, String biztype,ICommonDataAccessService iservice,List<TsDwbkReasonDto> listdto) throws ITFEBizException {
		final BuLUBankNoDialog dialog = new BuLUBankNoDialog(null, dto,
				banklist, bankname, biztype,iservice,listdto);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			String bankno = dialog.getBanknnoB();
			String banknames = dialog.getBanknameB();
			if(bankno == null || bankno.equals("")||banknames == null || banknames.equals("")){
				MessageDialog.openMessageDialog(null, "��¼���տ�����к�����Ϊ�գ������²�¼!");
				return null;
			}
				if (biztype.equals(BizTypeConstant.VOR_TYPE_PAYOUT)) {
					TvPayoutmsgmainDto _dto = (TvPayoutmsgmainDto) dto;
					if(bankno != null && !bankno.equals("")){
						_dto.setSinputrecbankno(bankno);
						_dto.setSrecbankno(bankno);// �տ�����к�
						_dto.setSpayeebankno(bankno);// �տ�����к�
					}
					if(banknames != null && !banknames.equals("")){
						_dto.setSinputrecbankname(banknames);
					}
					_dto.setSinputusercode(loginfo.getSuserCode());// ��¼��
					_dto.setScheckstatus(StateConstant.CHECKSTATUS_1);// ��¼����״̬Ϊ�Ѳ�¼
					if(loginfo.getPublicparam().contains(",buluverify=false,"))
					{
						_dto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
						_dto.setSinputusercode(loginfo.getSuserCode());
						// ����ƾ֤����Ϣ
						TvVoucherinfoPK pk = new TvVoucherinfoPK();
						pk.setSdealno(_dto.getSbizno());
						TvVoucherinfoDto vDto = (TvVoucherinfoDto) iservice.find(pk);
						if(vDto!=null)
						{
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
							vDto.setSdemo("��˳ɹ�");
							iservice.updateData(vDto);
						}
					}
					iservice.updateData(_dto);
					changebankcode(_dto,iservice);
				} else if (biztype.equals(BizTypeConstant.VOR_TYPE_DWBK)) {
					String backreason = dialog.getSelectbackreason();
					TvDwbkDto _dto = (TvDwbkDto) dto;
					if(bankno != null && !bankno.equals("")){
						_dto.setSinputrecbankno(bankno);
						_dto.setSpayeeopnbnkno(bankno);// �տ�����к�
					}
					if(banknames != null && !banknames.equals("")){
						_dto.setSinputrecbankname(banknames);
					}
					_dto.setSinputusercode(loginfo.getSuserCode());// ��¼��
					_dto.setScheckstatus(StateConstant.CHECKSTATUS_1);// ��¼����״̬Ϊ�Ѳ�¼
					if(backreason!=null && !backreason.equals("")){
						String[] backreasonarr = backreason.split("_");
						_dto.setSdwbkreasoncode(backreasonarr[0]);
					}
//					if(!_dto.getSdwbkreasoncode().equals("") && !_dto.getSpayeeopnbnkno().equals("")){
//						_dto.setScheckstatus(StateConstant.CHECKSTATUS_1);// ��¼����״̬Ϊ�Ѳ�¼
//					}else{
//						_dto.setScheckstatus(StateConstant.CHECKSTATUS_0);// ��¼����״̬Ϊδ��¼
//					}
					if(loginfo.getPublicparam().contains(",buluverify=false,"))
					{
						_dto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
						_dto.setSinputusercode(loginfo.getSuserCode());
						// ����ƾ֤����Ϣ
						TvVoucherinfoPK pk = new TvVoucherinfoPK();
						pk.setSdealno(_dto.getSbizno());
						TvVoucherinfoDto vDto = (TvVoucherinfoDto) iservice.find(pk);
						if(vDto!=null)
						{
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
							vDto.setSdemo("��˳ɹ�");
							iservice.updateData(vDto);
						}
					}
					iservice.updateData(_dto);
					changebankcode(_dto,iservice);
				} else if (biztype.equals(MsgConstant.VOUCHER_NO_5201)) {
					TfDirectpaymsgmainDto _dto = (TfDirectpaymsgmainDto) dto;
					if(bankno != null && !bankno.equals("")){
						_dto.setSpayeeacctbankno(bankno);// �տ�����к�
					}
					if(banknames != null && !banknames.equals("")){
						_dto.setSinputrecbankname(banknames);
					}
					_dto.setSinputusercode(loginfo.getSuserCode());// ��¼��
					_dto.setScheckstatus(StateConstant.CHECKSTATUS_1);// ��¼����״̬Ϊ�Ѳ�¼
					if(loginfo.getPublicparam().contains(",buluverify=false,"))
					{
						_dto.setScheckstatus(StateConstant.CHECKSTATUS_3);// ����״̬Ϊ���ͨ��
						_dto.setSinputusercode(loginfo.getSuserCode());
						// ����ƾ֤����Ϣ
						TvVoucherinfoPK pk = new TvVoucherinfoPK();
						pk.setSdealno(String.valueOf(_dto.getIvousrlno()));
						TvVoucherinfoDto vDto = (TvVoucherinfoDto) iservice.find(pk);
						if(vDto!=null)
						{
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECKSUCCESS);
							vDto.setSdemo("��˳ɹ�");
							iservice.updateData(vDto);
						}
					}
					iservice.updateData(_dto);
					changebankcode(_dto,iservice);
				}			
		} else {
			return null;
		}
		return dto;
	}
	
	public static void changebankcode(IDto idto,ICommonDataAccessService iservice){
		TvVoucherinfoDto queryDto = null;
		try {
			if (idto instanceof TvPayoutmsgmainDto) {
				TvPayoutmsgmainDto _dto = (TvPayoutmsgmainDto) idto;
				TvPayoutmsgmainDto dto = new TvPayoutmsgmainDto();
				dto.setSorgcode(loginfo.getSorgcode());
				// ��¼
				dto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
				// ��¼����״̬
				dto.setScheckstatus(StateConstant.CHECKSTATUS_0);
				//����
				dto.setSrecbankname(_dto.getSrecbankname());
				String wheresql = " and S_BIZNO in (select S_DEALNO from TV_VOUCHERINFO WHERE S_VTCODE='5207' AND S_STATUS='20' )";
				
				List<TvPayoutmsgmainDto> list = iservice.findRsByDtoWithWhere(dto,wheresql);
				
				for(int i=0;i<list.size();i++){
					TvPayoutmsgmainDto payoutdtos = list.get(i);
					if(payoutdtos.getScheckstatus().equals(StateConstant.CHECKSTATUS_0)&&payoutdtos.getSrecbankname().equals(_dto.getSrecbankname())){
						payoutdtos.setSinputrecbankno(_dto.getSinputrecbankno());
						payoutdtos.setSrecbankno(_dto.getSrecbankno());// �տ�����к�
						payoutdtos.setSpayeebankno(_dto.getSpayeebankno());// �տ�����к�
						payoutdtos.setSinputrecbankname(_dto.getSinputrecbankname());
						payoutdtos.setSinputusercode(loginfo.getSuserCode());// ��¼��
						payoutdtos.setScheckstatus(StateConstant.CHECKSTATUS_1);// ��¼����״̬Ϊ�Ѳ�¼
					}
					// �����к�
					iservice.updateDtos(list);
				}
			} else if (idto instanceof TvDwbkDto) {
				TvDwbkDto _dto = (TvDwbkDto) idto;
				TvDwbkDto dto = new TvDwbkDto();
				dto.setSbookorgcode(loginfo.getSorgcode());
				// ��¼
				dto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
				// ��¼����״̬
				dto.setScheckstatus(StateConstant.CHECKSTATUS_0);
				//����
				dto.setSrecbankname(_dto.getSrecbankname());
				String wheresql = " and S_BIZNO in (select S_DEALNO from TV_VOUCHERINFO WHERE S_VTCODE='"
					+ BizTypeConstant.VOR_TYPE_DWBK
					+ "' AND (S_STATUS='"
					+ DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"' ))";
				List<TvDwbkDto> list = iservice.findRsByDtoWithWhere(dto,wheresql);
				
				for(int i=0;i<list.size();i++){
					TvDwbkDto dwbkdtos = list.get(i);
					if(dwbkdtos.getSrecbankname().equals(_dto.getSrecbankname())&&dwbkdtos.getScheckstatus().equals(StateConstant.CHECKSTATUS_0) ){
						// �����к�
						dwbkdtos.setSpayeeopnbnkno(_dto.getSpayeeopnbnkno());// �տ�����к�
						dwbkdtos.setSinputrecbankname(_dto.getSinputrecbankname());// ��¼���տ��������
						dwbkdtos.setSinputusercode(loginfo.getSuserCode());// ��¼��
						dwbkdtos.setScheckstatus(StateConstant.CHECKSTATUS_1);// �Ѳ�¼
						iservice.updateData(dwbkdtos);
					}
				}
				
			} else if (idto instanceof TfDirectpaymsgmainDto) {
				TfDirectpaymsgmainDto _dto = (TfDirectpaymsgmainDto) idto;
				TfDirectpaymsgmainDto dto = new TfDirectpaymsgmainDto();
				dto.setSorgcode(loginfo.getSorgcode());
				// ��¼
				dto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
				// ��¼����״̬
				dto.setScheckstatus(StateConstant.CHECKSTATUS_0);
				//����
				dto.setSinputrecbankname(_dto.getSinputrecbankname());
				String wheresql = " I_VOUSRLNO in (select S_DEALNO from TV_VOUCHERINFO WHERE S_VTCODE='"
					+ MsgConstant.VOUCHER_NO_5201
					+ "' AND (S_STATUS='"
					+ DealCodeConstants.VOUCHER_VALIDAT_SUCCESS+"' ) " +
				    " AND (S_HOLD4 = '"+StateConstant.BIZTYPE_CODE_SINGLE+"' OR S_HOLD4 = '"+StateConstant.BIZTYPE_CODE_SALARY+"' )";
				
				List<TfDirectpaymsgmainDto> list = iservice.findRsByDtoWithWhere(dto,wheresql);
				
				for(int i=0;i<list.size();i++){
					TfDirectpaymsgmainDto directdtos = list.get(i);
					if(directdtos.getScheckstatus().equals(StateConstant.CHECKSTATUS_0)&&directdtos.getSinputrecbankname().equals(_dto.getSinputrecbankname())){
						directdtos.setSpayeeacctbankno(_dto.getSpayeeacctbankno());// �տ�����к�
						directdtos.setSinputrecbankname(_dto.getSinputrecbankname());// ��¼���տ��������
						directdtos.setSinputusercode(loginfo.getSuserCode());// ��¼��
						directdtos.setScheckstatus(StateConstant.CHECKSTATUS_1);// �Ѳ�¼
						// �����к�
						iservice.updateData(directdtos);
					}
				}
			}
		} catch (ITFEBizException e) {
			log.error(e);
		}
	}
	
	public IService getService(Class clazz) {
		return ServiceFactory.getService(clazz);
	}
}
