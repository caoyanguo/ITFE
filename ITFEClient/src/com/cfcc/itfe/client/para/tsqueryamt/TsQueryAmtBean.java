package com.cfcc.itfe.client.para.tsqueryamt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsQueryAmtDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author caoyg
 * @time   15-03-26 16:46:52
 * ��ϵͳ: Para
 * ģ��:tsQueryAmt
 * ���:TsQueryAmt
 */
@SuppressWarnings("unchecked")
public class TsQueryAmtBean extends AbstractTsQueryAmtBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsQueryAmtBean.class);
    private ITFELoginInfo loginfo = null;
    private List<TdEnumvalueDto> biztypelist;// ҵ�������б�
    public TsQueryAmtBean() {
      super();
      dto = new TsQueryAmtDto();
	  loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
			.getLoginInfo();
	  dto.setSorgcode(loginfo.getSorgcode());
	  pagingcontext = new PagingContext(this); 
	  moneyUnit = StateConstant.MoneyUnit_BW;
	  init();
    }
    
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TsQueryAmtDto();
		dto.setSorgcode(loginfo.getSorgcode());
        return super.goInput(o);
    }

	/**
	 * Direction: ¼�뱣��
	 * ename: insertSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String insertSave(Object o){
    	int i=dto.getFendamt().compareTo(dto.getFstatamt());
    	if (i<1) {
    		MessageDialog.openMessageDialog(null, "������ޱ�����ڽ������");
    		return "";
		}else{
			try {
				if (moneyUnit.equals(StateConstant.MoneyUnit_WAN)) {
					dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(4));
					dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(4));
				}else if(moneyUnit.equals(StateConstant.MoneyUnit_BW)){
					dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(6));
					dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(6));
				}else if(moneyUnit.equals(StateConstant.MoneyUnit_QW)){
					dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(7));
					dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(7));
				}else if(moneyUnit.equals(StateConstant.MoneyUnit_YI)){
					dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(8));
					dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(8));
				}
	    		tsQueryAmtService.addInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.insertSave(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
			dto = new TsQueryAmtDto();
			init();
	        return super.goBackMaintenance(o);
		}
    	
    }

	/**
	 * Direction: ���ص�ά������
	 * ename: goBackMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String goBackMaintenance(Object o){
    	dto = new TsQueryAmtDto();
		init();
        return super.goBackMaintenance(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TsQueryAmtDto) o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (null == dto || null == dto.getStrecode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "ɾ������ȷ��!", "�Ƿ�ȷ��Ҫɾ��������¼��");
		if (flag) {
			try {
				tsQueryAmtService.delInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			dto = new TsQueryAmtDto();
		}else{
			dto = new TsQueryAmtDto();
			return "";
		}

		init();
		return "";
    }

	/**
	 * Direction: ��ת�޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
    	if (null == dto || null == dto.getStrecode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
          return super.goModify(o);
    }

	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	int i=dto.getFendamt().compareTo(dto.getFstatamt());
    	if (i<1) {
    		MessageDialog.openMessageDialog(null, "������ޱ�����ڽ������");
    		return "";
		}else{
		try {
			if (moneyUnit.equals(StateConstant.MoneyUnit_WAN)) {
				dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(4));
				dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(4));
			}else if(moneyUnit.equals(StateConstant.MoneyUnit_BW)){
				dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(6));
				dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(6));
			}else if(moneyUnit.equals(StateConstant.MoneyUnit_QW)){
				dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(7));
				dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(7));
			}else if(moneyUnit.equals(StateConstant.MoneyUnit_YI)){
				dto.setFendamt(dto.getFendamt().scaleByPowerOfTen(8));
				dto.setFstatamt(dto.getFstatamt().scaleByPowerOfTen(8));
			}
	    		tsQueryAmtService.modifyInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
			dto = new TsQueryAmtDto();
			return super.goBackMaintenance(o);
		}
    	
    }
    
	private void init() {
    	biztypelist = new ArrayList();
    	TdEnumvalueDto value1 = new TdEnumvalueDto();
		value1.setStypecode(BizTypeConstant.VOR_TYPE_PAYOUT);
		value1.setSvaluecmt("ʵ���ʽ�5207");
		biztypelist.add(value1);
		TdEnumvalueDto value2 = new TdEnumvalueDto();
		value2.setStypecode(BizTypeConstant.VOR_TYPE_DWBK);
		value2.setSvaluecmt("�����˸�5209");
		biztypelist.add(value2);
		if(loginfo.getPublicparam().indexOf(",sh,")>=0){
			TdEnumvalueDto value3 = new TdEnumvalueDto();
			value3.setStypecode(MsgConstant.VOUCHER_NO_5201);
			value3.setSvaluecmt("ֱ��֧��5201");
			biztypelist.add(value3);
		}
		if(loginfo.getPublicparam().indexOf(",xm5207,")>=0){
			TdEnumvalueDto value3 = new TdEnumvalueDto();
			value3.setStypecode(MsgConstant.VOUCHER_NO_5267);
			value3.setSvaluecmt("ʵ���ʽ�ר��5267");
			biztypelist.add(value3);
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	TsQueryAmtDto amtDto= new TsQueryAmtDto();
    	amtDto.setSorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(amtDto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public List<TdEnumvalueDto> getBiztypelist() {
		return biztypelist;
	}

	public void setBiztypelist(List<TdEnumvalueDto> biztypelist) {
		this.biztypelist = biztypelist;
	}

}