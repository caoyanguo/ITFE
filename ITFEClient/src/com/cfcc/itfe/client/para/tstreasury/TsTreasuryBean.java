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
 * @time 09-10-20 08:42:02 子系统: Para 模块:TsTreasury 组件:TsTreasury
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
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsTreasuryDto();
		dto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL);
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSisuniontre(sisuniontre);//是否包含经济代码
		payoutamt =new BigDecimal(0);
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		String verifyRs = verifyPorp(dto);
		if(payoutamt!=null&&payoutamt.compareTo(new BigDecimal(0))>0)
			dto.setSfinancename(String.valueOf(payoutamt));//实拨资金最大拨款额控制
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
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsTreasuryDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsTreasuryDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto || null == dto.getStrecode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "删除数据确认!", "是否确认要删除这条记录！");
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
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
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
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
        dto.setSisuniontre(sisuniontre);
        if(payoutamt!=null&&payoutamt.compareTo(new BigDecimal(0))>0)
        	dto.setSfinancename(String.valueOf(payoutamt));//实拨资金最大拨款额控制
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
	 * 从Usb-Key中获取签章信息
	 */
	public String getStampID(Object o) {
		AsspOcx asspOcx = new AsspOcx(new Shell());
		dto.setScertid(asspOcx.DlgSelectCertId());
		dto.setSstampid(asspOcx.DlgSelectStampId());
		editor.fireModelChanged();
		return super.getStampID(o);
	}
	/**
	 * 从Usb-Key中获取转讫章信息
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
	 * 校验字段属性
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsTreasuryDto idto) {
		if (null == idto) {
			return "要操作的记录为空，请确认！";
		}
		String regex ="^[0-9]*$";
		Matcher matcher;
		Pattern pattern;
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(idto.getStrecode());
		if (null == idto.getStrecode() || "".equals(idto.getStrecode().trim())||!matcher.matches()
				|| idto.getStrecode().trim().length() != 10 ) {
			return "国库代码不能为空，且必须为10位数字字符！";
		}

		if (null == idto.getStrename() || "".equals(idto.getStrename())) {
			return "国库主体名称为空！";
		}

		if (null == idto.getSisuniontre() || "".equals(idto.getSisuniontre())) {
			return "是否启用经济科目不能为空，请选择！";
		}
		matcher = pattern.matcher(idto.getSgoverntrecode());
		if (null == idto.getSgoverntrecode() || "".equals(idto.getSgoverntrecode()) ||!matcher.matches()||idto.getSgoverntrecode().trim().length() != 10) {
			return "管辖国库代码不能为空，且必须为10位数字字符！";
		}
        
//		if ("2".equals(idto.getStreattrib())&&(null==idto.getSofcountrytrecode()||"".equals(idto.getSofcountrytrecode()))) {
//			return "代理国库对应的行别代码不能为空！";
//		}

		/*if (null == idto.getStrimflag() || "".equals(idto.getStrimflag())) {
			return "调整期标志必须填写！";
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