package com.cfcc.itfe.client.para.tstreasury;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Shell;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.AsspOcx;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsTreasury ���:TsTreasury
 */
@SuppressWarnings("unchecked")
public class TsTreasuryBean extends AbstractTsTreasuryBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsTreasuryBean.class);
	private List<TsOrganDto> list2 = new ArrayList<TsOrganDto>();
	private ITFELoginInfo loginfo = null;
	private String sisuniontre;
	private BigDecimal	payoutamt;
	public TsTreasuryBean() {
		super();
		dto = new TsTreasuryDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		sisuniontre =StateConstant.COMMON_NO ;
		init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsTreasuryDto();
		dto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL);
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSisuniontre(sisuniontre);//�Ƿ�������ô���
		payoutamt =new BigDecimal(0);
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		String verifyRs = verifyPorp(dto);
		if(payoutamt!=null&&payoutamt.compareTo(new BigDecimal(0))>0)
			dto.setSfinancename(String.valueOf(payoutamt));//ʵ���ʽ���󲦿�����
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}

		try {
			tsTreasuryService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsTreasuryDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsTreasuryDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsTreasuryDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto || null == dto.getStrecode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "ɾ������ȷ��!", "�Ƿ�ȷ��Ҫɾ��������¼��");
		if (flag) {
			try {
				tsTreasuryService.delInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			dto = new TsTreasuryDto();
		}else{
			dto = new TsTreasuryDto();
			return "";
		}

		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
        
		if (null == dto || null == dto.getStrecode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if(dto.getSfinancename()!=null&&!"".equals(dto.getSfinancename())&&!"null".equals(dto.getSfinancename()))
			payoutamt = MtoCodeTrans.transformBigDecimal(dto.getSfinancename());
		else
			payoutamt =new BigDecimal(0);
		sisuniontre = dto.getSisuniontre() ;
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
        dto.setSisuniontre(sisuniontre);
        if(payoutamt!=null&&payoutamt.compareTo(new BigDecimal(0))>0)
        	dto.setSfinancename(String.valueOf(payoutamt));//ʵ���ʽ���󲦿�����
		try {
			tsTreasuryService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsTreasuryDto();
		return super.backMaintenance(o);
	}

	/**
	 * ��Usb-Key�л�ȡǩ����Ϣ
	 */
	public String getStampID(Object o) {
		AsspOcx asspOcx = new AsspOcx(new Shell());
		dto.setScertid(asspOcx.DlgSelectCertId());
		dto.setSstampid(asspOcx.DlgSelectStampId());
		editor.fireModelChanged();
		return super.getStampID(o);
	}
	/**
	 * ��Usb-Key�л�ȡת������Ϣ
	 */
	public String getRotarycertID(Object o) {
		AsspOcx asspOcx = new AsspOcx(new Shell());
		dto.setSrotarycertid(asspOcx.DlgSelectCertId());
		dto.setSrotaryid(asspOcx.DlgSelectStampId());
		editor.fireModelChanged();
		return super.getStampID(o);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		TsTreasuryDto treasuryDto= new TsTreasuryDto();
		treasuryDto.setSorgcode(this.getLoginfo().getSorgcode());

		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(treasuryDto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private void init() {
		TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorgcode(this.getLoginfo().getSorgcode());
		try {
			list2 = commonDataAccessService.findRsByDto(orgdto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * У���ֶ�����
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsTreasuryDto idto) {
		if (null == idto) {
			return "Ҫ�����ļ�¼Ϊ�գ���ȷ�ϣ�";
		}
		String regex ="^[0-9]*$";
		Matcher matcher;
		Pattern pattern;
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(idto.getStrecode());
		if (null == idto.getStrecode() || "".equals(idto.getStrecode().trim())||!matcher.matches()
				|| idto.getStrecode().trim().length() != 10 ) {
			return "������벻��Ϊ�գ��ұ���Ϊ10λ�����ַ���";
		}

		if (null == idto.getStrename() || "".equals(idto.getStrename())) {
			return "������������Ϊ�գ�";
		}

		if (null == idto.getSisuniontre() || "".equals(idto.getSisuniontre())) {
			return "�Ƿ����þ��ÿ�Ŀ����Ϊ�գ���ѡ��";
		}
		matcher = pattern.matcher(idto.getSgoverntrecode());
		if (null == idto.getSgoverntrecode() || "".equals(idto.getSgoverntrecode()) ||!matcher.matches()||idto.getSgoverntrecode().trim().length() != 10) {
			return "��Ͻ������벻��Ϊ�գ��ұ���Ϊ10λ�����ַ���";
		}
        
//		if ("2".equals(idto.getStreattrib())&&(null==idto.getSofcountrytrecode()||"".equals(idto.getSofcountrytrecode()))) {
//			return "��������Ӧ���б���벻��Ϊ�գ�";
//		}

		/*if (null == idto.getStrimflag() || "".equals(idto.getStrimflag())) {
			return "�����ڱ�־������д��";
		}*/

		return null;
	}

	

	public String getSisuniontre() {
		return sisuniontre;
	}

	public void setSisuniontre(String sisuniontre) {
		this.sisuniontre = sisuniontre;
	}

	public List<TsOrganDto> getList2() {
		return list2;
	}

	public void setList2(List<TsOrganDto> list2) {
		this.list2 = list2;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public BigDecimal getPayoutamt() {
		return payoutamt;
	}

	public void setPayoutamt(BigDecimal payoutamt) {
		this.payoutamt = payoutamt;
	}
}