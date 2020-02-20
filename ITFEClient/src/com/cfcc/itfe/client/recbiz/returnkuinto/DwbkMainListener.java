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
			if("�������".equals(metadata.name)) {
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
					sb.append("������벻��Ϊ��\n");
				}
				if("".equals(ssvou) || null == ssvou) {
					sb.append("�˿�ƾ֤��Ų���Ϊ��\n");
				}
				if("".equals(dwdto.getFamt()) || null == dwdto.getFamt()) {
					sb.append("����Ϊ��");
				}
				if(!"".equals(sb.toString())) {
					MessageDialog.openMessageDialog(null, sb.toString());
					return ;
				}
				dwdto.setSpayertrecode(bean.getTrecode());
				dwdto.setSdwbkvoucode(bean.getDefvou().trim()+bean.getEndvou().trim());
				List showdatalist ;
				TbsTvDwbkDto bkdto = new TbsTvDwbkDto();
				//�����������
				dwdto.setSbookorgcode(loginfo.getSorgcode());
				//δ����
				dwdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				try {
					showdatalist = uploadConfirmService.eachQuery(
							BizTypeConstant.BIZ_TYPE_RET_TREASURY, dwdto);
					if(null == showdatalist || showdatalist.size() == 0) {
						MessageDialog.openMessageDialog(null, "û���ҵ���Ӧ��¼");
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
							null, "������ʾ", null, "���ҵ���Ӧ��¼���Ƿ�ȷ������",
							org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
									"ȷ��", "ȡ��" }, 0);
					if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
						int result = uploadConfirmService.eachConfirm(
								BizTypeConstant.BIZ_TYPE_RET_TREASURY, bkdto);
						if (StateConstant.SUBMITSTATE_DONE == result
								|| StateConstant.SUBMITSTATE_SUCCESS == result){
							MessageDialog.openMessageDialog(null, "�����ɹ���");
						}						
					} else {
						return;
					}
					
					//���ź��������������
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
				
			}else if ("�����������".equals(metadata.name)) {
				MVCUtils.setContentAreaFocus(editor, "�������");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
			}
		}
	}
	
	public List<ResultDesDto> createPaylist(TbsTvDwbkDto ddto)  {
		List<ResultDesDto> reslist = new ArrayList<ResultDesDto>();
		reslist.add(this.getResult("�˿�������", ddto.getSpayertrecode()));
		reslist.add(this.getResult("�˿�ƾ֤���", ddto.getSdwbkvoucode()));
		reslist.add(this.getResult("�տ����˺�", ddto.getSpayeeacct()));
		reslist.add(this.getResult("�տ�������", ddto.getSpayeename()));
		reslist.add(this.getResult("�˿���", ddto.getFamt().toString()));
		reslist.add(this.getResult("���ջ��ش���", ddto.getStaxorgcode()));
		reslist.add(this.getResult("�տλ", ddto.getSpayeecode()));
		if(null != ddto.getSagenttaxorgcode() && !"".equals(ddto.getSagenttaxorgcode())) {
			reslist.add(this.getResult("�������ջ��ش���", ddto.getSagenttaxorgcode()));
		}		
		if(null != ddto.getSecocode() && !"".equals(ddto.getSecocode())) {
			reslist.add(this.getResult("�˿��˾������ʹ���", ddto.getSecocode()));
		}
		if(null != ddto.getSetpcode() && !"".equals(ddto.getSetpcode())) {
			reslist.add(this.getResult("�˿�����ҵ���ʹ���", ddto.getSetpcode()));
		}		
		if(null != ddto.getDbill() && !"".equals(ddto.getDbill())) {
			reslist.add(this.getResult("�����", ddto.getDbill().toString()));
		}	
		reslist.add(this.getResult("�տλ������", ddto.getSpayeeopnbnkno()));
		reslist.add(this.getResult("�˿⼶��", ddto.getCbdglevel()));
		reslist.add(this.getResult("��Ŀ����", ddto.getSbdgsbtcode()));
		reslist.add(this.getResult("�˿�ԭ��", ddto.getSdwbkreasoncode()));
		if(null != ddto.getSexamorg() && !"".equals(ddto.getSexamorg())) {
			reslist.add(this.getResult("��������", ddto.getSexamorg()));
		}
		if(null != ddto.getSdwbkby() && !"".equals(ddto.getSdwbkby())) {
			reslist.add(this.getResult("�˿�����", ddto.getSdwbkby()));
		}
		if(null != ddto.getFdwbkratio() && !"".equals(ddto.getFdwbkratio().toString())) {
			reslist.add(this.getResult("�˿����", ddto.getFdwbkratio().toString()));
		}
		if(null != ddto.getFdwbkamt() && !"".equals(ddto.getFdwbkamt())) {
			reslist.add(this.getResult("�˿��ܶ�", ddto.getFdwbkamt().toString()));
		}		
		if(null != ddto.getSastflag() && !"".equals(ddto.getSastflag())) {
			reslist.add(this.getResult("������־", ddto.getSastflag()));
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
