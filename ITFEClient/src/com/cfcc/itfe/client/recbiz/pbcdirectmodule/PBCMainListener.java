package com.cfcc.itfe.client.recbiz.pbcdirectmodule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.service.recbiz.fundinto.IFundIntoService;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.ViewMetaData;

public class PBCMainListener extends KeyAdapter{
	private static Log log = LogFactory.getLog(PBCMainListener.class);
	private Composite composite;
	private ContainerMetaData metadata;
	AbstractMetaDataEditorPart editor;
	private String maname ;
	private PbcDirectImportBean bean;

	public PBCMainListener(Composite composite, ContainerMetaData metadata,BasicModel bean,String metaname) {
		this.composite = composite;
		this.metadata = metadata;
		this.editor = MVCUtils.getEditor(metadata.editorid);
		this.bean = (PbcDirectImportBean) bean;
		this.maname = metaname;
	}

	public void keyPressed(KeyEvent e) {
		if (e.keyCode != SWT.Selection && e.keyCode != SWT.KEYPAD_CR) {
			return;
		}
		if (e.getSource() instanceof Text) {
			composite.setFocus();
			//�õ���ط������
			ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			IUploadConfirmService uploadConfirmService = (IUploadConfirmService) bean.getService(IUploadConfirmService.class);
			ICommonDataAccessService commonservice = (ICommonDataAccessService) bean.getService(ICommonDataAccessService.class);
			IFundIntoService funservice = (IFundIntoService) bean.getService(IFundIntoService.class);
			if("�������".equals(metadata.name) || "�տ����˺���Ϣ��ʾ".equals(metadata.name)) {
				try {
					Map<String, TsBudgetsubjectDto> codeMap =  funservice.makeBudgCode();
					TbsTvPbcpayDto paydto = bean.getPbcdto();
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
						sb.append("ƾ֤��Ų���Ϊ��\n");
					}
					if("".equals(paydto.getFamt()) || null == paydto.getFamt()) {
						sb.append("����Ϊ��");
					}
					if(!"".equals(sb.toString())) {
						MessageDialog.openMessageDialog(null, sb.toString());
						return ;
					}
					StringBuffer sff = new StringBuffer("");
					if(!"".equals(bean.getNewbudgcode())&&  null != bean.getNewbudgcode()) {
						if(!checkFunCode(paydto.getSfuncsbtcode(), commonservice, loginfo)) {
							MessageDialog.openMessageDialog(null, "�ü�¼��Ŀ���벻�ǲ�������֧����Ŀ���룬���ܽ����޸Ĳ���!");
							return ;
						}
						if(codeMap != null) {
							TsBudgetsubjectDto sudto = codeMap.get(bean.getNewbudgcode());
							if (null == sudto || "".equals(sudto.getSsubjectcode())) {
								MessageDialog.openMessageDialog(null, "����Ŀ�Ŀ���� '" + bean.getNewbudgcode()+ "' û����'Ԥ���Ŀ����'���ҵ�,���֤");
								return ;
							} else {
								if (!"2".equals(sudto.getSsubjectclass())) {
									MessageDialog.openMessageDialog(null, "����Ŀ�Ŀ���� '" + bean.getNewbudgcode()+ "' ����֧�����ܿ�Ŀ");
									return ;
								}
							}
						}
						sff.append("��ʾ���������Ž��޸ĸü�¼��Ŀ����Ϊ '"+bean.getNewbudgcode()+"'");
					}
					paydto.setStrecode(bean.getTrecode());
					paydto.setSvouno(bean.getDefvou().trim()+bean.getEndvou().trim());
					List showdatalist ;
					TbsTvPbcpayDto payoutdto = new TbsTvPbcpayDto();
					
					//�����������
					paydto.setSbookorgcode(loginfo.getSorgcode());
					//δ����
					paydto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				
					showdatalist = uploadConfirmService.eachQuery(
							BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, paydto);
					if(null == showdatalist || showdatalist.size() == 0) {
						MessageDialog.openMessageDialog(null, "û���ҵ���Ӧ��¼");
						return ;
					}else if (null != showdatalist && showdatalist.size() > 1) {
						payoutdto = (TbsTvPbcpayDto)showdatalist.get(0);
					}else if (null != showdatalist && showdatalist.size() == 1) { 
						payoutdto = (TbsTvPbcpayDto)showdatalist.get(0);
					}	
//					payoutdto.setSfuncsbtcode(bean.getNewbudgcode());
					List<ResultDesDto> rlist = createPBClist(payoutdto);				
					bean.setPbclist(rlist);
					paydto.setSpayeeacct(payoutdto.getSpayeeacct());
					paydto.setSpayeename(payoutdto.getSpayeename());
					bean.setPbcdto(paydto);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
					
					org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
							null, "������ʾ", null, "���ҵ���Ӧ��¼���Ƿ�ȷ������\n"+sff.toString(),
							org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
									"ȷ��", "ȡ��" }, 0);
					if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
						if(!"".equals(sff.toString())) {
							payoutdto.setSfuncsbtcode(bean.getNewbudgcode());
							commonservice.updateData(payoutdto); //�û����ȷ������ʱ������Ŀ������½�ȥ
						}						
						int result = uploadConfirmService.eachConfirm(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, payoutdto);
						if (StateConstant.SUBMITSTATE_DONE == result
								|| StateConstant.SUBMITSTATE_SUCCESS == result) {
							MessageDialog.openMessageDialog(null, "�����ɹ���");
						}
					} else {
						return;
					}
					
					//���ź��������������
					paydto.setFamt(null);
					paydto.setSpayeeacct(null);
					paydto.setSpayeename(null);
					bean.setPbcdto(paydto);
					bean.setPbclist(new ArrayList());
					bean.setEndvou("");
					bean.setNewbudgcode(null);
				} catch (ITFEBizException e1) {
					log.error(e);
					MessageDialog.openErrorDialog(null, e1);
					return ;
				}
				MVCUtils.setContentAreaFocus(editor, "�������");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
				
			}else if ("�����������".equals(metadata.name)) {
				MVCUtils.setContentAreaFocus(editor, "�������");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
			}
		}
	}
	
	public List<ResultDesDto> createPBClist(TbsTvPbcpayDto pdto)  {
		List<ResultDesDto> reslist = new ArrayList<ResultDesDto>();
		reslist.add(this.getResult("�������", pdto.getStrecode()));
		reslist.add(this.getResult("ƾ֤���", pdto.getSvouno()));
		reslist.add(this.getResult("���", pdto.getFamt().toString()));
		reslist.add(this.getResult("�������˺�", pdto.getSpayeracct()));	
		if(null != pdto.getSpayername() && !"".equals(pdto.getSpayername())) {
			reslist.add(this.getResult("����������", pdto.getSpayername()));
		}				
		reslist.add(this.getResult("�տ����˺�", pdto.getSpayeeacct()));
		if(null != pdto.getSpayeename() && !"".equals(pdto.getSpayeename())) {
			reslist.add(this.getResult("�տ�������", pdto.getSpayeename()));
		}
		if(null != pdto.getSpayeeopnbnkno() && !"".equals(pdto.getSpayeeopnbnkno())) {
			reslist.add(this.getResult("�տλ�������к�", pdto.getSpayeeopnbnkno()));
		}		
		if(null != pdto.getSbillorg() && !"".equals(pdto.getSbillorg())) {
			reslist.add(this.getResult("��Ʊ��λ", pdto.getSbillorg()));
		}
		if(null != pdto.getSbdgorgcode() && !"".equals(pdto.getSbdgorgcode())) {
			reslist.add(this.getResult("Ԥ�㵥λ����", pdto.getSbdgorgcode()));		
		}
		if(null != pdto.getSbdgorgname() && !"".equals(pdto.getSbdgorgname())) {
			reslist.add(this.getResult("Ԥ�㵥λ����", pdto.getSbdgorgname()));
		}		
		reslist.add(this.getResult("֧�����ܿ�Ŀ����", pdto.getSfuncsbtcode()));
		if(null != pdto.getSecosbtcode() && !"".equals(pdto.getSecosbtcode())) {
			reslist.add(this.getResult("֧�����ÿ�Ŀ����", pdto.getSecosbtcode()));
		}
		reslist.add(this.getResult("ƾ֤����", pdto.getDvoucher().toString()));
		reslist.add(this.getResult("�����ļ���", pdto.getSfilename()));
		
		
		return reslist;
	}
	
	/**
	 * ����Ҫ�޸Ŀ�Ŀʱ���ж��ļ��п�Ŀ�Ƿ��ǵ���֧����Ŀ
	 * @param funcode
	 * @param service
	 * @param loginfo
	 * @return
	 * @throws ITFEBizException 
	 */
	private boolean checkFunCode(String funcode,ICommonDataAccessService service,ITFELoginInfo loginfo) throws ITFEBizException {
		TsFinmovepaysubDto dto = new TsFinmovepaysubDto();
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSsubjectcode(funcode);
		List list = service.findRsByDtoUR(dto);
		if(null == null || list.size() == 0) {
			return false;
		}
		return true;
	}
	
	public ResultDesDto getResult(String colkey,String colvalue) {
		ResultDesDto desdto = new ResultDesDto();
		desdto.setColName(colkey);
		desdto.setColValue(colvalue.toString());
		return desdto;
	}
}
