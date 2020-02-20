package com.cfcc.itfe.client.recbiz.returnkuinto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.ViewMetaData;

public class DwbkMainListener extends KeyAdapter{
	private static Log log = LogFactory.getLog(DwbkMainListener.class);
	private Composite composite;
	private ContainerMetaData metadata;
	AbstractMetaDataEditorPart editor;
	private String mename;
	private ReturnKuIntoBean bean;
	private boolean isWithSubTable;

	public DwbkMainListener(Composite composite, ContainerMetaData metadata,BasicModel bean,String metaname) {
		this.composite = composite;
		this.metadata = metadata;
		this.editor = MVCUtils.getEditor(metadata.editorid);
		this.bean = (ReturnKuIntoBean) bean;
		this.mename = metaname;
	}

	public void keyPressed(KeyEvent e) {
		if (e.keyCode != SWT.Selection && e.keyCode != SWT.KEYPAD_CR) {
			return;
		}
		if (e.getSource() instanceof Text) {
			composite.setFocus();
			
			ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			IUploadConfirmService uploadConfirmService = (IUploadConfirmService) bean
			.getService(IUploadConfirmService.class);
			if("逐笔销号".equals(metadata.name)) {
				TbsTvDwbkDto dwdto = bean.getDwbkdto();		
				if("".equals(bean.getDefvou()) || null == bean.getDefvou()) {
					bean.setDefvou("");
				}
				if("".equals(bean.getEndvou()) || null == bean.getEndvou()) {
					bean.setEndvou("");
				}
				String ssvou = bean.getDefvou().trim() + bean.getEndvou().trim();
				StringBuffer sb = new StringBuffer("");
				if("".equals(bean.getTrecode()) || null == bean.getTrecode()) { 
					sb.append("国库代码不能为空\n");
				}
				if("".equals(ssvou) || null == ssvou) {
					sb.append("退库凭证编号不能为空\n");
				}
				if("".equals(dwdto.getFamt()) || null == dwdto.getFamt()) {
					sb.append("金额不能为空");
				}
				if(!"".equals(sb.toString())) {
					MessageDialog.openMessageDialog(null, sb.toString());
					return ;
				}
				dwdto.setSpayertrecode(bean.getTrecode());
				dwdto.setSdwbkvoucode(bean.getDefvou().trim()+bean.getEndvou().trim());
				List showdatalist ;
				TbsTvDwbkDto bkdto = new TbsTvDwbkDto();
				//核算主体代码
				dwdto.setSbookorgcode(loginfo.getSorgcode());
				//未销号
				dwdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				try {
					showdatalist = uploadConfirmService.eachQuery(
							BizTypeConstant.BIZ_TYPE_RET_TREASURY, dwdto);
					if(null == showdatalist || showdatalist.size() == 0) {
						MessageDialog.openMessageDialog(null, "没有找到对应记录");
						return ;
					}else if (null != showdatalist && showdatalist.size() > 1) { 
						bkdto = (TbsTvDwbkDto)showdatalist.get(0);
					}else if (null != showdatalist && showdatalist.size() == 1) { 
						bkdto = (TbsTvDwbkDto)showdatalist.get(0);
					}	
					List<ResultDesDto> rlist = createPaylist(bkdto);				
					bean.setDwbklist(rlist);
					dwdto.setSpayeeacct(bkdto.getSpayeeacct());
					dwdto.setSpayeename(bkdto.getSpayeename());
					bean.setDwbkdto(dwdto);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
					org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
							null, "销号提示", null, "查找到对应记录，是否确认销号",
							org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
									"确认", "取消" }, 0);
					if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
						int result = uploadConfirmService.eachConfirm(
								BizTypeConstant.BIZ_TYPE_RET_TREASURY, bkdto);
						if (StateConstant.SUBMITSTATE_DONE == result
								|| StateConstant.SUBMITSTATE_SUCCESS == result){
							MessageDialog.openMessageDialog(null, "操作成功！");
						}						
					} else {
						return;
					}
					
					//销号后除国库代码外清空
					dwdto.setFamt(null);
					dwdto.setSpayeeacct(null);
					dwdto.setSpayeename(null);
					bean.setDwbkdto(dwdto);
					bean.setDwbklist(new ArrayList());
					bean.setEndvou("");
				} catch (ITFEBizException e1) {
					log.error(e);
					MessageDialog.openErrorDialog(null, e1);
					return ;
				}
				this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				
			}else if ("所属国库代码".equals(metadata.name)) {
				MVCUtils.setContentAreaFocus(editor, "逐笔销号");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
			}
		}
	}
	
	public List<ResultDesDto> createPaylist(TbsTvDwbkDto ddto)  {
		List<ResultDesDto> reslist = new ArrayList<ResultDesDto>();
		reslist.add(this.getResult("退库国库代码", ddto.getSpayertrecode()));
		reslist.add(this.getResult("退库凭证编号", ddto.getSdwbkvoucode()));
		reslist.add(this.getResult("收款人账号", ddto.getSpayeeacct()));
		reslist.add(this.getResult("收款人名称", ddto.getSpayeename()));
		reslist.add(this.getResult("退库金额", ddto.getFamt().toString()));
		reslist.add(this.getResult("征收机关代码", ddto.getStaxorgcode()));
		reslist.add(this.getResult("收款单位", ddto.getSpayeecode()));
		if(null != ddto.getSagenttaxorgcode() && !"".equals(ddto.getSagenttaxorgcode())) {
			reslist.add(this.getResult("代征征收机关代码", ddto.getSagenttaxorgcode()));
		}		
		if(null != ddto.getSecocode() && !"".equals(ddto.getSecocode())) {
			reslist.add(this.getResult("退库人经济类型代码", ddto.getSecocode()));
		}
		if(null != ddto.getSetpcode() && !"".equals(ddto.getSetpcode())) {
			reslist.add(this.getResult("退库人行业类型代码", ddto.getSetpcode()));
		}		
		if(null != ddto.getDbill() && !"".equals(ddto.getDbill())) {
			reslist.add(this.getResult("填发日期", ddto.getDbill().toString()));
		}	
		reslist.add(this.getResult("收款单位开户行", ddto.getSpayeeopnbnkno()));
		reslist.add(this.getResult("退库级次", ddto.getCbdglevel()));
		reslist.add(this.getResult("科目代码", ddto.getSbdgsbtcode()));
		reslist.add(this.getResult("退库原因", ddto.getSdwbkreasoncode()));
		if(null != ddto.getSexamorg() && !"".equals(ddto.getSexamorg())) {
			reslist.add(this.getResult("审批机关", ddto.getSexamorg()));
		}
		if(null != ddto.getSdwbkby() && !"".equals(ddto.getSdwbkby())) {
			reslist.add(this.getResult("退库依据", ddto.getSdwbkby()));
		}
		if(null != ddto.getFdwbkratio() && !"".equals(ddto.getFdwbkratio().toString())) {
			reslist.add(this.getResult("退库比例", ddto.getFdwbkratio().toString()));
		}
		if(null != ddto.getFdwbkamt() && !"".equals(ddto.getFdwbkamt())) {
			reslist.add(this.getResult("退库总额", ddto.getFdwbkamt().toString()));
		}		
		if(null != ddto.getSastflag() && !"".equals(ddto.getSastflag())) {
			reslist.add(this.getResult("辅助标志", ddto.getSastflag()));
		}		
		
		return reslist;
	}
	
	public ResultDesDto getResult(String colkey,String colvalue) {
		ResultDesDto desdto = new ResultDesDto();
		desdto.setColName(colkey);
		desdto.setColValue(colvalue.toString());
		return desdto;
	}
	
}
