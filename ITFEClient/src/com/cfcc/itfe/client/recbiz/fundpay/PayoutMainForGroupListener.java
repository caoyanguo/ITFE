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
			 * 准备工作：得到需要用到的服务，数据库、实拨、提交等
			 */
			ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			IUploadConfirmService uploadConfirmService = (IUploadConfirmService) bean.getService(IUploadConfirmService.class);
			ICommonDataAccessService commonservice = (ICommonDataAccessService) bean.getService(ICommonDataAccessService.class);
			IFundIntoService funservice = (IFundIntoService) bean.getService(IFundIntoService.class);
			String operModel = "0"; //标志跳转到哪个区域   0--跳转到逐笔销号区域
													 // 1--跳转到所属国库代码区域																
			if("逐笔销号".equals(metadata.name) || "重要信息显示".equals(metadata.name)) {
				try {					
					/**
					 * a、得到科目代码缓存
					 */
					Map<String, TsBudgetsubjectDto> codeMap =  funservice.makeBudgCode();
					/**
					 * b、校验前台录入数据
					 */
					TbsTvPayoutDto paydto = bean.getPaysdto();
					String spayee = bean.getSpayeecode(); //得到前台录入的收款人账号 5位以上
					Integer totalcount = bean.getTotalcount(); //总笔数
					BigDecimal totalFamt = bean.getTotalfamt(); //总金额
					String grpno = bean.getGrpno(); //组号
					
					if("".equals(bean.getDefvou()) || null == bean.getDefvou()) {
						bean.setDefvou("");
					}
					if("".equals(bean.getEndvou()) || null == bean.getEndvou()) {
						bean.setEndvou("");
					}
					String ssvou = bean.getDefvou().trim() + bean.getEndvou().trim(); //凭证编号前缀+凭证编号
					StringBuffer sb = new StringBuffer("");
					if("".equals(bean.getTrecode()) || null == bean.getTrecode()) { 
						sb.append("国库代码不能为空\n");
					}
					if(null == grpno || "".equals(grpno)) {
						sb.append("组号不能为空\n");
					}
					if(null == totalcount || totalcount == 0) {
						sb.append("总笔数必须大于0\n");
					}
					if(null == totalFamt) {
						sb.append("总金额不能为空\n");
					}
					if("".equals(ssvou) || null == ssvou) {
						sb.append("凭证编号不能为空\n");
					}
					if("".equals(spayee) || null == spayee || spayee.length() < 5) {
						sb.append("收款人账号必须大于等于5位\n");
					}					
					if(!"".equals(sb.toString())) {
						MessageDialog.openMessageDialog(null, sb.toString());
						return ;
					}
					/**
					 * c、处理修改科目代码
					 */
					StringBuffer sff = new StringBuffer("");
					if(!"".equals(bean.getNewbudgcode())&&  null != bean.getNewbudgcode()) {
						if(!checkFunCode(paydto.getSfuncsbtcode(), commonservice, loginfo)) {
							MessageDialog.openMessageDialog(null, "该记录科目代码不是财政调拨支出科目代码，不能进行修改操作!");
							return ;
						}
						if(codeMap != null) {
							TsBudgetsubjectDto sudto = codeMap.get(bean.getNewbudgcode());
							if (null == sudto || "".equals(sudto.getSsubjectcode())) {
								MessageDialog.openMessageDialog(null, "所填的科目代码 '" + bean.getNewbudgcode()+ "' 没有在'预算科目参数'中找到,请查证");
								return ;
							} else {
								if (!"2".equals(sudto.getSsubjectclass())) {
									MessageDialog.openMessageDialog(null, "所填的科目代码 '" + bean.getNewbudgcode()+ "' 不是支出功能科目");
									return ;
								}
							}
						}
						sff.append("提示：本次销号将修改该记录科目代码为 '"+bean.getNewbudgcode()+"'");
					}
					/**
					 * d、得到前台查询条件，并生成查询Dto
					 */
					paydto.setStrecode(bean.getTrecode());
					paydto.setSvouno(bean.getDefvou().trim()+bean.getEndvou().trim());
					List showdatalist ;					
					
					//核算主体代码
					paydto.setSbookorgcode(loginfo.getSorgcode());
					//未销号
					paydto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					
					/**
					 * e、查找记录时，收款人账号按照后5位或以上匹配查询
					 */
					showdatalist = commonservice.findRsByDto(paydto, " AND S_GROUPID IS NULL AND S_PAYEEACCT LIKE '%"+spayee+"' ");
					
					TbsTvPayoutDto payoutdto = new TbsTvPayoutDto();
					
					if(null == showdatalist || showdatalist.size() == 0) {
						MessageDialog.openMessageDialog(null, "没有找到对应记录");
						return ;
					}else if (null != showdatalist && showdatalist.size() > 1) {
						payoutdto = (TbsTvPayoutDto)showdatalist.get(0);
					}else if (null != showdatalist && showdatalist.size() == 1) { 
						payoutdto = (TbsTvPayoutDto)showdatalist.get(0);
					}
					
					/**
					 * 找到记录，则对该组号进行create_update操作
					 */
					TsCheckbalanceDto balance = new TsCheckbalanceDto();
					balance.setSorgcode(loginfo.getSorgcode());
					balance.setSgroupid(grpno);
					balance.setSusercode(loginfo.getSuserCode());
					balance.setItotalnum(totalcount);
					balance.setNtotalmoney(totalFamt);
					funservice.createOrUpdate(balance);
					/*
					 * TODO 考虑是否要加上业务类型区分，主要是concern到退库
					 */
					/**
					 * f、填充界面显示要素
					 */
					List<ResultDesDto> rlist = createPaylist(payoutdto);
					bean.setPaylist(rlist);
					paydto.setSpayeename(payoutdto.getSpayeename());
					paydto.setFamt(payoutdto.getFamt());
					bean.setPaysdto(paydto);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);					
					
					/**
					 * g、得到该组号对应记录
					 */
					TsCheckbalanceDto bal = new TsCheckbalanceDto();
					bal.setSorgcode(loginfo.getSorgcode());
					bal.setSusercode(loginfo.getSuserCode());
					bal.setSgroupid(grpno);
					List blist = commonservice.findRsByDto(bal);
					TsCheckbalanceDto resCheck = (TsCheckbalanceDto)blist.get(0);
					
					//判断这次销号是否是最后一笔，如果是则校验平衡
					String checkState = checkBalance(resCheck,payoutdto.getFamt());
					if("3".equals(checkState)) { //不能进行销号
						bean.setPaylist(new ArrayList());
						MessageDialog.openMessageDialog(null, "组号["+grpno+"]的已销号笔数已达到总笔数,但金额不平衡,不能继续销号!\n" +
								"["+grpno+"], 已销号笔数:"+resCheck.getIcheckednum()+", 已销号金额:"+resCheck.getNcheckmoney());
						//销号后除国库代码外清空
						paydto.setFamt(null);
						paydto.setSpayeename(null);
						bean.setPaysdto(paydto);						
						bean.setEndvou("");
						bean.setSpayeecode("");
						bean.setNewbudgcode(null);
						MVCUtils.setContentAreaFocus(editor, "逐笔销号");
						editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
								.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
						return ;
					}
					/**
					 * h、提示用户是否销号
					 */
					org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
							null, "销号提示", null, "查找到对应记录，是否确认销号\n"+sff.toString(),
							org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
									"确认", "取消" }, 1);
					int returnType = msg.open();
					if (returnType == org.eclipse.jface.dialogs.MessageDialog.OK) {
						if(!"".equals(sff.toString())) {
							payoutdto.setSfuncsbtcode(bean.getNewbudgcode());
							commonservice.updateData(payoutdto); //用户点击确认销号时，将科目代码更新进去
						}		
						
						/**
						 * 改成不销号数据，只将组号、用户名更新到数据之中
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
						 * g、重新得到该组号对应记录
						 */
						bal.setSorgcode(loginfo.getSorgcode());
						bal.setSusercode(loginfo.getSuserCode());
						bal.setSgroupid(grpno);
						blist = commonservice.findRsByDto(bal);
						resCheck = (TsCheckbalanceDto)blist.get(0);
						if("1".equals(checkState)) { //组号销号完成且平衡
							//已平衡的组号,系统不自动提交
							MessageDialog.openMessageDialog(null, "操作成功，已经达到销号笔数，请校验平衡后提交！\n" +
									"["+grpno+"] 已销号笔数:"+(resCheck.getIcheckednum())+" 已销号金额:"+resCheck.getNcheckmoney());
						}else if("2".equals(checkState)){ //组号销号完成但不平衡
							MessageDialog.openMessageDialog(null, "操作成功,已经达到销号笔数，组校验不平衡,请查证！\n" +
									"["+grpno+"] 已销号笔数:"+(resCheck.getIcheckednum())+" 已销号金额:"+resCheck.getNcheckmoney());
						}else { 
							MessageDialog.openMessageDialog(null, "操作成功！");
						}							
					} else {
						return;
					}
					
					//销号后除国库代码外清空
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
				MVCUtils.setContentAreaFocus(editor, "逐笔销号");
				if("1".equals(operModel)) {
					editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
							.getData(MetaDataConstants.META_DATA_KEY)).id, true,true);
				}else {
					editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
							.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
				}
				
				
			}else if ("所属国库代码".equals(metadata.name)) {
				MVCUtils.setContentAreaFocus(editor, "逐笔销号");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,false);
			}
		}
	}
	
	public List<ResultDesDto> createPaylist(TbsTvPayoutDto pdto)  {
		List<ResultDesDto> reslist = new ArrayList<ResultDesDto>();		
		reslist.add(this.getResult("付款人账号", pdto.getSpayeracct()));	
		if(null != pdto.getSpayername() && !"".equals(pdto.getSpayername())) {
			reslist.add(this.getResult("付款人名称", pdto.getSpayername()));
		}				
		reslist.add(this.getResult("收款人账号", pdto.getSpayeeacct()));
		if(null != pdto.getSpayeename() && !"".equals(pdto.getSpayeename())) {
			reslist.add(this.getResult("收款人名称", pdto.getSpayeename()));
		}
		if(null != pdto.getSpayeeopnbnkno() && !"".equals(pdto.getSpayeeopnbnkno())) {
			reslist.add(this.getResult("收款单位开户行行号", pdto.getSpayeeopnbnkno()));
		}		
			
		reslist.add(this.getResult("支出功能科目代码", pdto.getSfuncsbtcode()));
		if(null != pdto.getSecosbtcode() && !"".equals(pdto.getSecosbtcode())) {
			reslist.add(this.getResult("支出经济科目代码", pdto.getSecosbtcode()));
		}
		reslist.add(this.getResult("凭证编号", pdto.getSvouno()));
		reslist.add(this.getResult("金额", pdto.getFamt().toString()));
		reslist.add(this.getResult("预算单位名称", pdto.getSbdgorgname()));
		if(null != pdto.getSbdgorgcode() && !"".equals(pdto.getSbdgorgcode())) {
			reslist.add(this.getResult("预算单位代码", pdto.getSbdgorgcode()));		
		}
		reslist.add(this.getResult("清算银行行号", pdto.getSpayeebankno()));
		reslist.add(this.getResult("国库代码", pdto.getStrecode()));
		if(null != pdto.getSbillorg() && !"".equals(pdto.getSbillorg())) {
			reslist.add(this.getResult("出票单位", pdto.getSbillorg()));
		}
		if(null != pdto.getSpaylevel() && !"".equals(pdto.getSpaylevel())) {
			reslist.add(this.getResult("拨款级次", pdto.getSpaylevel()));
		}		
		reslist.add(this.getResult("凭证日期", pdto.getDvoucher().toString()));
		if(null != pdto.getSpaybizkind() && !"".equals(pdto.getSpaybizkind())) {
			reslist.add(this.getResult("拨款种类", pdto.getSpaybizkind()));
		}		
		if(null != pdto.getSmovefundreason() && !"".equals(pdto.getSmovefundreason())) {
			reslist.add(this.getResult("支出原因", pdto.getSmovefundreason()));
		}
		
		return reslist;
	}
	
	/**
	 * 在需要修改科目时，判断文件中科目是否是调拨支出科目
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
	 * 根据条件生成对象
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
	 * 根据组号 直接提交整组
	 * @param confirmService
	 * @param commonService
	 * @param grpno
	 * @param loginfo
	 * @return
	 * @throws ITFEBizException
	 */
	public boolean confirmGroup(IUploadConfirmService confirmService,ICommonDataAccessService commonService,String grpno,ITFELoginInfo loginfo) throws ITFEBizException {
		TbsTvPayoutDto paramdto = new TbsTvPayoutDto();
		paramdto.setSbookorgcode(loginfo.getSorgcode()); //核算主体
		paramdto.setSgroupid(grpno); //组号
		paramdto.setSusercode(loginfo.getSuserCode()); //用户名
		paramdto.setSstatus(StateConstant.CONFIRMSTATE_NO); //未销号
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
	 * 校验销号状态
	 * 	0--组号销号未完成，正常工作状态
	 *  1--组号销号完成，且校验平衡
	 *  2--组号销号完成，但校验不平衡
	 *  3--组号销号完成，销号数据在组号之外，需停止该销号(基于状态'2'的基础)
	 * @param dto
	 * @param readyFamt
	 * @return
	 */
	public String checkBalance(TsCheckbalanceDto dto ,BigDecimal readyFamt) {
		//判断这次销号是否是最后一笔，如果是则校验平衡
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
		if((checknum+1)==totalunm) { //如果满足，则说明是最后一笔
			if(totalfamt.compareTo(checkfamt.add(readyFamt)) == 0) { //说明本次销号完之后校验平衡
				return "1";
			}else { //不平衡
				return "2";
			}
		}else if((checknum+1)>totalunm) { //说明本次销号在组号之外，需要阻止
			return "3";
		}else {
			return "0";
		}
	}
}
