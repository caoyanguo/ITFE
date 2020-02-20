package com.cfcc.itfe.client.recbiz.fundinto;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.persistence.pk.TsFinmovepaysubPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.service.recbiz.fundinto.IFundIntoService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.ViewMetaData;

public class PayoutMainListener extends KeyAdapter {
	private static Log log = LogFactory.getLog(PayoutMainListener.class);
	private Composite composite;
	private ContainerMetaData metadata;
	AbstractMetaDataEditorPart editor;
	private String maname ;
	private FundIntoBean bean;

	public PayoutMainListener(Composite composite, ContainerMetaData metadata,BasicModel bean,String metaname) {
		this.composite = composite;
		this.metadata = metadata;
		this.editor = MVCUtils.getEditor(metadata.editorid);
		this.bean = (FundIntoBean) bean;
		this.maname = metaname;
	}

	public void keyPressed(KeyEvent e) {
		if (e.keyCode != SWT.Selection && e.keyCode != SWT.KEYPAD_CR) {
			return;
		}
		if (e.getSource() instanceof Text) {
			composite.setFocus();
			
			ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			IUploadConfirmService uploadConfirmService = (IUploadConfirmService) bean.getService(IUploadConfirmService.class);
			ICommonDataAccessService commonservice = (ICommonDataAccessService) bean.getService(ICommonDataAccessService.class);
			IFundIntoService funservice = (IFundIntoService) bean.getService(IFundIntoService.class);
			
			if("�������".equals(metadata.name) || "�տ����˺���Ϣ��ʾ".equals(metadata.name)) {
				try {
					Map<String, TsBudgetsubjectDto> codeMap =  funservice.makeBudgCode();
					TbsTvPayoutDto paydto = bean.getPaysdto();
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
					paydto.setStrecode(bean.getTrecode());
					paydto.setSvouno(bean.getDefvou().trim()+bean.getEndvou().trim());
					List showdatalist ;
					TbsTvPayoutDto payoutdto = new TbsTvPayoutDto();
					
					//�����������
					paydto.setSbookorgcode(loginfo.getSorgcode());
					//δ����
					paydto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				
					showdatalist = uploadConfirmService.eachQuery(
							BizTypeConstant.BIZ_TYPE_PAY_OUT, paydto);
					if(null == showdatalist || showdatalist.size() == 0) {
						MessageDialog.openMessageDialog(null, "û���ҵ���Ӧ��¼");
						return ;
					}else if (null != showdatalist && showdatalist.size() > 1) {
						payoutdto = (TbsTvPayoutDto)showdatalist.get(0);
					}else if (null != showdatalist && showdatalist.size() == 1) { 
						payoutdto = (TbsTvPayoutDto)showdatalist.get(0);
					}	
					
					/*//���ݵ����ļ���Ԥ�㵥λ������Ҷ�Ӧ��Ԥ�㵥λ���ƣ���Ҫ�ǽ�����������ļ����տ���������Ԥ�㵥λ���Ʋ�һ�µ������
					if(loginfo.getArea().equals("FUZHOU")){
						TdCorpDto corpDto = new TdCorpDto();
						corpDto.setSbookorgcode(loginfo.getSorgcode());
						corpDto.setStrecode(payoutdto.getStrecode());
						corpDto.setScorpcode(payoutdto.getSbdgorgcode());
						List<TdCorpDto> cropDtoList = commonservice.findRsByDto(corpDto);
						if(null!=cropDtoList&&cropDtoList.size()>0){
							payoutdto.setSbdgorgname(cropDtoList.get(0).getScorpname());
						}
					}*/
//					payoutdto.setSfuncsbtcode(bean.getNewbudgcode());
					List<ResultDesDto> rlist = createPaylist(payoutdto);				
					bean.setPaylist(rlist);
					paydto.setSpayeeacct(payoutdto.getSpayeeacct());
					paydto.setSpayeename(payoutdto.getSpayeename());
					bean.setPaysdto(paydto);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
					
					StringBuffer sff = new StringBuffer("");
					if(!"".equals(bean.getNewbudgcode())&&  null != bean.getNewbudgcode()) {
						if(!checkFunCode(payoutdto.getSfuncsbtcode(), commonservice, loginfo)) {
							MessageDialog.openMessageDialog(null, "�ü�¼��Ŀ���벻�ǲ�������֧����Ŀ���룬���ܽ����޸Ĳ���!");
							return ;
						}
						if(codeMap != null) {
							TsBudgetsubjectDto sudto = codeMap.get(bean.getNewbudgcode());
							if (null == sudto || "".equals(sudto.getSsubjectcode())) {
								if (!checkFunCode(bean.getNewbudgcode(), commonservice, loginfo)) {
									MessageDialog.openMessageDialog(null, "����Ŀ�Ŀ���� '" + bean.getNewbudgcode()+ "' û����'Ԥ���Ŀ����'���ҵ�,���֤");
									return;
								}
							} else {
								if (!"2".equals(sudto.getSsubjectclass())) {
									MessageDialog.openMessageDialog(null, "����Ŀ�Ŀ���� '" + bean.getNewbudgcode()+ "' ����֧�����ܿ�Ŀ");
									return ;
								}
							}
							sff.append("��ʾ���������Ž��޸ĸü�¼��Ŀ����Ϊ '"+bean.getNewbudgcode()+"'");
						}else {
							MessageDialog.openMessageDialog(null, "����ά��Ԥ���Ŀ�������!");
							return ;
						}
						
					}
					
					org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
							null, "������ʾ", null, "���ҵ���Ӧ��¼���Ƿ�ȷ������\n"+sff.toString(),
							org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
									"ȷ��", "ȡ��" }, 0);
					if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
						if(!"".equals(sff.toString())) {
							payoutdto.setSfuncsbtcode(bean.getNewbudgcode());
							commonservice.updateData(payoutdto); //�û����ȷ������ʱ������Ŀ������½�ȥ
						}						
						int result = uploadConfirmService.eachConfirm(BizTypeConstant.BIZ_TYPE_PAY_OUT, payoutdto);
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
					bean.setPaysdto(paydto);
					bean.setPaylist(new ArrayList());
					bean.setEndvou("");
					bean.setNewbudgcode(null);
				} catch (ITFEBizException e1) {
					log.error(e1);
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
	
	public List<ResultDesDto> createPaylist(TbsTvPayoutDto pdto)  {
		List<ResultDesDto> reslist = new ArrayList<ResultDesDto>();
		reslist.add(this.getResult("�������", pdto.getStrecode()));
		reslist.add(this.getResult("ƾ֤���", pdto.getSvouno()));
		reslist.add(this.getResult("���", pdto.getFamt().toString()));
		reslist.add(this.getResult("�������˺�", pdto.getSpayeracct()));	
		if(null != pdto.getSpayername() && !"".equals(pdto.getSpayername())) {
			reslist.add(this.getResult("����������", pdto.getSpayername()));
		}				
		reslist.add(this.getResult("�տ����˺�", pdto.getSpayeeacct()));
		if(null != pdto.getSpayeeopnbnkno() && !"".equals(pdto.getSpayeeopnbnkno())) {
			reslist.add(this.getResult("�տλ�������к�", pdto.getSpayeeopnbnkno()));
		}		
		if(null != pdto.getSbillorg() && !"".equals(pdto.getSbillorg())) {
			reslist.add(this.getResult("��Ʊ��λ", pdto.getSbillorg()));
		}
		if(null != pdto.getSbdgorgcode() && !"".equals(pdto.getSbdgorgcode())) {
			reslist.add(this.getResult("Ԥ�㵥λ����", pdto.getSbdgorgcode()));		}
		
		reslist.add(this.getResult("Ԥ�㵥λ����", pdto.getSbdgorgname()));
		if(null != pdto.getSpaylevel() && !"".equals(pdto.getSpaylevel())) {
			reslist.add(this.getResult("�����", pdto.getSpaylevel()));
		}		
		reslist.add(this.getResult("֧�����ܿ�Ŀ����", pdto.getSfuncsbtcode()));
		if(null != pdto.getSecosbtcode() && !"".equals(pdto.getSecosbtcode())) {
			reslist.add(this.getResult("֧�����ÿ�Ŀ����", pdto.getSecosbtcode()));
		}		
		if(null != pdto.getSmovefundreason() && !"".equals(pdto.getSmovefundreason())) {
			reslist.add(this.getResult("֧��ԭ��", pdto.getSmovefundreason()));
		}		
		reslist.add(this.getResult("ƾ֤����", pdto.getDvoucher().toString()));
		if(null != pdto.getSpaybizkind() && !"".equals(pdto.getSpaybizkind())) {
			reslist.add(this.getResult("��������", pdto.getSpaybizkind()));
		}
		reslist.add(this.getResult("���������к�", pdto.getSpayeebankno()));
		
		
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
		if(null == list || list.size() == 0) {
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
