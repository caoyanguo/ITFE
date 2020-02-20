package com.cfcc.itfe.client.recbiz.fundpay;

import java.math.BigDecimal;
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
import com.cfcc.itfe.facade.data.BizDataCountDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsCheckbalanceDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.service.recbiz.fundinto.IFundIntoService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.ViewMetaData;

public class PayoutMainForGroupListener extends KeyAdapter {
	private static Log log = LogFactory.getLog(PayoutMainForGroupListener.class);
	private Composite composite;
	private ContainerMetaData metadata;
	AbstractMetaDataEditorPart editor;
	private String maname ;
	private FundPayBean bean;

	public PayoutMainForGroupListener(Composite composite, ContainerMetaData metadata,BasicModel bean,String metaname) {
		this.composite = composite;
		this.metadata = metadata;
		this.editor = MVCUtils.getEditor(metadata.editorid);
		this.bean = (FundPayBean) bean;
		this.maname = metaname;
	}

	public void keyPressed(KeyEvent e) {
		if (e.keyCode != SWT.Selection && e.keyCode != SWT.KEYPAD_CR) {
			return;
		}
		if (e.getSource() instanceof Text) {
			composite.setFocus();
			
			/**
			 * ׼���������õ���Ҫ�õ��ķ������ݿ⡢ʵ�����ύ��
			 */
			ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			IUploadConfirmService uploadConfirmService = (IUploadConfirmService) bean.getService(IUploadConfirmService.class);
			ICommonDataAccessService commonservice = (ICommonDataAccessService) bean.getService(ICommonDataAccessService.class);
			IFundIntoService funservice = (IFundIntoService) bean.getService(IFundIntoService.class);
			String operModel = "0"; //��־��ת���ĸ�����   0--��ת�������������
													 // 1--��ת�����������������																
			if("�������".equals(metadata.name) || "��Ҫ��Ϣ��ʾ".equals(metadata.name)) {
				try {					
					/**
					 * a���õ���Ŀ���뻺��
					 */
					Map<String, TsBudgetsubjectDto> codeMap =  funservice.makeBudgCode();
					/**
					 * b��У��ǰ̨¼������
					 */
					TbsTvPayoutDto paydto = bean.getPaysdto();
					String spayee = bean.getSpayeecode(); //�õ�ǰ̨¼����տ����˺� 5λ����
					Integer totalcount = bean.getTotalcount(); //�ܱ���
					BigDecimal totalFamt = bean.getTotalfamt(); //�ܽ��
					String grpno = bean.getGrpno(); //���
					
					if("".equals(bean.getDefvou()) || null == bean.getDefvou()) {
						bean.setDefvou("");
					}
					if("".equals(bean.getEndvou()) || null == bean.getEndvou()) {
						bean.setEndvou("");
					}
					String ssvou = bean.getDefvou().trim() + bean.getEndvou().trim(); //ƾ֤���ǰ׺+ƾ֤���
					StringBuffer sb = new StringBuffer("");
					if("".equals(bean.getTrecode()) || null == bean.getTrecode()) { 
						sb.append("������벻��Ϊ��\n");
					}
					if(null == grpno || "".equals(grpno)) {
						sb.append("��Ų���Ϊ��\n");
					}
					if(null == totalcount || totalcount == 0) {
						sb.append("�ܱ����������0\n");
					}
					if(null == totalFamt) {
						sb.append("�ܽ���Ϊ��\n");
					}
					if("".equals(ssvou) || null == ssvou) {
						sb.append("ƾ֤��Ų���Ϊ��\n");
					}
					if("".equals(spayee) || null == spayee || spayee.length() < 5) {
						sb.append("�տ����˺ű�����ڵ���5λ\n");
					}					
					if(!"".equals(sb.toString())) {
						MessageDialog.openMessageDialog(null, sb.toString());
						return ;
					}
					/**
					 * c�������޸Ŀ�Ŀ����
					 */
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
					/**
					 * d���õ�ǰ̨��ѯ�����������ɲ�ѯDto
					 */
					paydto.setStrecode(bean.getTrecode());
					paydto.setSvouno(bean.getDefvou().trim()+bean.getEndvou().trim());
					List showdatalist ;					
					
					//�����������
					paydto.setSbookorgcode(loginfo.getSorgcode());
					//δ����
					paydto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					
					/**
					 * e�����Ҽ�¼ʱ���տ����˺Ű��պ�5λ������ƥ���ѯ
					 */
					showdatalist = commonservice.findRsByDto(paydto, " AND S_GROUPID IS NULL AND S_PAYEEACCT LIKE '%"+spayee+"' ");
					
					TbsTvPayoutDto payoutdto = new TbsTvPayoutDto();
					
					if(null == showdatalist || showdatalist.size() == 0) {
						MessageDialog.openMessageDialog(null, "û���ҵ���Ӧ��¼");
						return ;
					}else if (null != showdatalist && showdatalist.size() > 1) {
						payoutdto = (TbsTvPayoutDto)showdatalist.get(0);
					}else if (null != showdatalist && showdatalist.size() == 1) { 
						payoutdto = (TbsTvPayoutDto)showdatalist.get(0);
					}
					
					/**
					 * �ҵ���¼����Ը���Ž���create_update����
					 */
					TsCheckbalanceDto balance = new TsCheckbalanceDto();
					balance.setSorgcode(loginfo.getSorgcode());
					balance.setSgroupid(grpno);
					balance.setSusercode(loginfo.getSuserCode());
					balance.setItotalnum(totalcount);
					balance.setNtotalmoney(totalFamt);
					funservice.createOrUpdate(balance);
					/*
					 * TODO �����Ƿ�Ҫ����ҵ���������֣���Ҫ��concern���˿�
					 */
					/**
					 * f����������ʾҪ��
					 */
					List<ResultDesDto> rlist = createPaylist(payoutdto);
					bean.setPaylist(rlist);
					paydto.setSpayeename(payoutdto.getSpayeename());
					paydto.setFamt(payoutdto.getFamt());
					bean.setPaysdto(paydto);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);					
					
					/**
					 * g���õ�����Ŷ�Ӧ��¼
					 */
					TsCheckbalanceDto bal = new TsCheckbalanceDto();
					bal.setSorgcode(loginfo.getSorgcode());
					bal.setSusercode(loginfo.getSuserCode());
					bal.setSgroupid(grpno);
					List blist = commonservice.findRsByDto(bal);
					TsCheckbalanceDto resCheck = (TsCheckbalanceDto)blist.get(0);
					
					//�ж���������Ƿ������һ�ʣ��������У��ƽ��
					String checkState = checkBalance(resCheck,payoutdto.getFamt());
					if("3".equals(checkState)) { //���ܽ�������
						bean.setPaylist(new ArrayList());
						MessageDialog.openMessageDialog(null, "���["+grpno+"]�������ű����Ѵﵽ�ܱ���,����ƽ��,���ܼ�������!\n" +
								"["+grpno+"], �����ű���:"+resCheck.getIcheckednum()+", �����Ž��:"+resCheck.getNcheckmoney());
						//���ź��������������
						paydto.setFamt(null);
						paydto.setSpayeename(null);
						bean.setPaysdto(paydto);						
						bean.setEndvou("");
						bean.setSpayeecode("");
						bean.setNewbudgcode(null);
						MVCUtils.setContentAreaFocus(editor, "�������");
						editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
								.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
						return ;
					}
					/**
					 * h����ʾ�û��Ƿ�����
					 */
					org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
							null, "������ʾ", null, "���ҵ���Ӧ��¼���Ƿ�ȷ������\n"+sff.toString(),
							org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
									"ȷ��", "ȡ��" }, 1);
					int returnType = msg.open();
					if (returnType == org.eclipse.jface.dialogs.MessageDialog.OK) {
						if(!"".equals(sff.toString())) {
							payoutdto.setSfuncsbtcode(bean.getNewbudgcode());
							commonservice.updateData(payoutdto); //�û����ȷ������ʱ������Ŀ������½�ȥ
						}		
						
						/**
						 * �ĳɲ��������ݣ�ֻ����š��û������µ�����֮��
						 * modefy date:2012-09-17
						 */
						payoutdto.setSgroupid(grpno);
						payoutdto.setSusercode(loginfo.getSuserCode());
						commonservice.updateData(payoutdto);
					
						BizDataCountDto datacount = new BizDataCountDto();
						datacount.setBizname(grpno);
						datacount.setTotalfamt(payoutdto.getFamt());
						String grpresult = funservice.updateGroup(datacount, "", "", null,"add");
						/**
						 * g�����µõ�����Ŷ�Ӧ��¼
						 */
						bal.setSorgcode(loginfo.getSorgcode());
						bal.setSusercode(loginfo.getSuserCode());
						bal.setSgroupid(grpno);
						blist = commonservice.findRsByDto(bal);
						resCheck = (TsCheckbalanceDto)blist.get(0);
						if("1".equals(checkState)) { //������������ƽ��
							//��ƽ������,ϵͳ���Զ��ύ
							MessageDialog.openMessageDialog(null, "�����ɹ����Ѿ��ﵽ���ű�������У��ƽ����ύ��\n" +
									"["+grpno+"] �����ű���:"+(resCheck.getIcheckednum())+" �����Ž��:"+resCheck.getNcheckmoney());
						}else if("2".equals(checkState)){ //���������ɵ���ƽ��
							MessageDialog.openMessageDialog(null, "�����ɹ�,�Ѿ��ﵽ���ű�������У�鲻ƽ��,���֤��\n" +
									"["+grpno+"] �����ű���:"+(resCheck.getIcheckednum())+" �����Ž��:"+resCheck.getNcheckmoney());
						}else { 
							MessageDialog.openMessageDialog(null, "�����ɹ���");
						}							
					} else {
						return;
					}
					
					//���ź��������������
					paydto.setFamt(null);
					paydto.setSpayeename(null);
					bean.setPaysdto(paydto);
					bean.setPaylist(new ArrayList());
					bean.setEndvou("");
					bean.setSpayeecode("");
					bean.setNewbudgcode(null);
				} catch (ITFEBizException e1) {
					log.error(e);
					MessageDialog.openErrorDialog(null, e1);
					return ;
				}
				MVCUtils.setContentAreaFocus(editor, "�������");
				if("1".equals(operModel)) {
					editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
							.getData(MetaDataConstants.META_DATA_KEY)).id, true,true);
				}else {
					editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
							.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
				}
				
				
			}else if ("�����������".equals(metadata.name)) {
				MVCUtils.setContentAreaFocus(editor, "�������");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
			}
		}
	}
	
	public List<ResultDesDto> createPaylist(TbsTvPayoutDto pdto)  {
		List<ResultDesDto> reslist = new ArrayList<ResultDesDto>();		
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
			
		reslist.add(this.getResult("֧�����ܿ�Ŀ����", pdto.getSfuncsbtcode()));
		if(null != pdto.getSecosbtcode() && !"".equals(pdto.getSecosbtcode())) {
			reslist.add(this.getResult("֧�����ÿ�Ŀ����", pdto.getSecosbtcode()));
		}
		reslist.add(this.getResult("ƾ֤���", pdto.getSvouno()));
		reslist.add(this.getResult("���", pdto.getFamt().toString()));
		reslist.add(this.getResult("Ԥ�㵥λ����", pdto.getSbdgorgname()));
		if(null != pdto.getSbdgorgcode() && !"".equals(pdto.getSbdgorgcode())) {
			reslist.add(this.getResult("Ԥ�㵥λ����", pdto.getSbdgorgcode()));		
		}
		reslist.add(this.getResult("���������к�", pdto.getSpayeebankno()));
		reslist.add(this.getResult("�������", pdto.getStrecode()));
		if(null != pdto.getSbillorg() && !"".equals(pdto.getSbillorg())) {
			reslist.add(this.getResult("��Ʊ��λ", pdto.getSbillorg()));
		}
		if(null != pdto.getSpaylevel() && !"".equals(pdto.getSpaylevel())) {
			reslist.add(this.getResult("�����", pdto.getSpaylevel()));
		}		
		reslist.add(this.getResult("ƾ֤����", pdto.getDvoucher().toString()));
		if(null != pdto.getSpaybizkind() && !"".equals(pdto.getSpaybizkind())) {
			reslist.add(this.getResult("��������", pdto.getSpaybizkind()));
		}		
		if(null != pdto.getSmovefundreason() && !"".equals(pdto.getSmovefundreason())) {
			reslist.add(this.getResult("֧��ԭ��", pdto.getSmovefundreason()));
		}
		
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
	
	/**
	 * �����������ɶ���
	 * @param colkey
	 * @param colvalue
	 * @return
	 */
	public ResultDesDto getResult(String colkey,String colvalue) {
		ResultDesDto desdto = new ResultDesDto();
		desdto.setColName(colkey);
		desdto.setColValue(colvalue.toString());
		return desdto;
	}
	
	/**
	 * ������� ֱ���ύ����
	 * @param confirmService
	 * @param commonService
	 * @param grpno
	 * @param loginfo
	 * @return
	 * @throws ITFEBizException
	 */
	public boolean confirmGroup(IUploadConfirmService confirmService,ICommonDataAccessService commonService,String grpno,ITFELoginInfo loginfo) throws ITFEBizException {
		TbsTvPayoutDto paramdto = new TbsTvPayoutDto();
		paramdto.setSbookorgcode(loginfo.getSorgcode()); //��������
		paramdto.setSgroupid(grpno); //���
		paramdto.setSusercode(loginfo.getSuserCode()); //�û���
		paramdto.setSstatus(StateConstant.CONFIRMSTATE_NO); //δ����
		List l = commonService.findRsByDtoUR(paramdto);
		if(null == l || l.size() == 0) {
			return Boolean.FALSE;
		}else {
			for(Object obj : l) {
				IDto idto = (IDto)obj;
				int result = confirmService.eachConfirm(BizTypeConstant.BIZ_TYPE_PAY_OUT, idto);
				if (StateConstant.SUBMITSTATE_DONE != result && StateConstant.SUBMITSTATE_SUCCESS != result) {
					return Boolean.FALSE;					
				}
			}
		}
		return Boolean.TRUE;		
	}
	
	/**
	 * У������״̬
	 * 	0--�������δ��ɣ���������״̬
	 *  1--���������ɣ���У��ƽ��
	 *  2--���������ɣ���У�鲻ƽ��
	 *  3--���������ɣ��������������֮�⣬��ֹͣ������(����״̬'2'�Ļ���)
	 * @param dto
	 * @param readyFamt
	 * @return
	 */
	public String checkBalance(TsCheckbalanceDto dto ,BigDecimal readyFamt) {
		//�ж���������Ƿ������һ�ʣ��������У��ƽ��
		Integer checknum = dto.getIcheckednum();
		Integer totalunm = dto.getItotalnum();
		BigDecimal checkfamt = dto.getNcheckmoney();
		BigDecimal totalfamt = dto.getNtotalmoney();
		
		if(null == checknum || checknum.intValue() == 0) {
			checknum = 0;
		}
		if(null == totalunm || totalunm.intValue() == 0) {
			totalunm = 0;
		}
		if(null == checkfamt) {
			checkfamt = new BigDecimal("0.00");
		}
		if(null == totalfamt) {
			totalfamt = new BigDecimal("0.00");
		}
		if((checknum+1)==totalunm) { //������㣬��˵�������һ��
			if(totalfamt.compareTo(checkfamt.add(readyFamt)) == 0) { //˵������������֮��У��ƽ��
				return "1";
			}else { //��ƽ��
				return "2";
			}
		}else if((checknum+1)>totalunm) { //˵���������������֮�⣬��Ҫ��ֹ
			return "3";
		}else {
			return "0";
		}
	}
}
