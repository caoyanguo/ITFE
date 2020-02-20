package com.cfcc.itfe.client.sendbiz.exporttbsfiletxt;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author win7
 * @time 13-10-29 06:18:53 子系统: SendBiz 模块:exportTBSfiletxt 组件:ExportTBSfiletxt
 */
public class ExportTBSfiletxtBean extends AbstractExportTBSfiletxtBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(ExportTBSfiletxtBean.class);
	private ITFELoginInfo loginfo;
	private String sbiztypetbs;
	private String strecode;
	private Date dacctdate;
	private String ctrimflag;

	public Date getDacctdate() {
		return dacctdate;
	}

	public void setDacctdate(Date dacctdate) {
		this.dacctdate = dacctdate;
	}

	public String getCtrimflag() {
		return ctrimflag;
	}

	public void setCtrimflag(String ctrimflag) {
		this.ctrimflag = ctrimflag;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
	}

	public ExportTBSfiletxtBean() {
		super();
		tabList = new ArrayList();
		checkList = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
	}
	/**
	 * Direction: 导出 ename: exportTBS 引用方法: viewers: * messages:
	 */
	public String exportTBS(Object o) {
		try {
			List<IDto> result = this.exportTBSfiletxtService.exportTBSdata(
					sbiztypetbs, strecode, dacctdate, ctrimflag);
			if (null == result || result.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return null;
			}
			// 选择保存路径
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
				return "";
			}
			String fileName = filePath + File.separator;
			// + getfilename(getSbiztypetbs());

			expdata(result, fileName);
			this.exportTBSfiletxtService.updateVdtoStatus(result);
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.exportTBS(o);
	}
	
	// 获取文件名称
	private String getfilename(String typeTBS) {
		// 文件名称：yyyyMMddxxxxxxttm.txt
		StringBuffer fileName = new StringBuffer();
		if (null == getDacctdate()) {
			fileName.append(TimeFacade.formatDate(TimeFacade.getCurrentDate(),
					"yyyyMMdd"));// 生成文件的账务日期
		} else {
			fileName.append(TimeFacade.formatDate(getDacctdate(), "yyyyMMdd"));// 生成文件的账务日期
		}
		try {
			fileName.append(this.exportTBSfiletxtService.getTBSNum());
		} catch (ITFEBizException e) {
			e.printStackTrace();
			log.info("生成批次号异常");
			MessageDialog.openMessageDialog(null, e.getMessage());
			return null;
		}// 批次号2或4位生成文件的批次号，用于在国库系统唯一标识导入文件，没有国库业务含义
		// 业务类型
		if (typeTBS.equalsIgnoreCase(StateConstant.TBS_SBZJ)) {
			fileName.append("11");

		} else if (typeTBS.equalsIgnoreCase(StateConstant.TBS_ZJZFED)) {
			fileName.append("01");
		} else if (typeTBS.equalsIgnoreCase(StateConstant.TBS_SQZFED)) {
			fileName.append("02");
		} else if (typeTBS.equalsIgnoreCase(StateConstant.TBS_JZZFHKSQ)) {
			fileName.append("01");
		}
		// M为调整期标志（1位）―― 0－正常期；1－调整期
		if (null == getCtrimflag()) {
			fileName.append("0");// 正常期
		} else {
			fileName.append(getCtrimflag());
		}
		fileName.append(".txt");
		return fileName.toString();
	}

	private void expdata(List<IDto> result, String fileName)
			throws FileOperateException, ITFEBizException {
		StringBuffer resultStr = new StringBuffer();

		if (result.get(0) instanceof TvPayreckBankDto) {// 集中支付划款明细岗文件
			String directpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"25"+ctrimflag+".txt";
			String grantpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"27"+ctrimflag+".txt";	
			StringBuffer directpayresultStr = new StringBuffer();
			StringBuffer grantpayresultStr = new StringBuffer();
			for (IDto tmp : result) {
				TvPayreckBankDto dto = (TvPayreckBankDto) tmp;
				if(dto.getSpaytypecode().equals("11")){//直接支付划款申请
					directpayresultStr.append("**" + dto.getStrecode().toString() + ","); // 国库代码
					directpayresultStr.append(","); // 付款人全称
					directpayresultStr.append(dto.getSpayeracct() + ","); // 付款人帐号
					directpayresultStr.append("" + ","); // 付款人开户行
					directpayresultStr.append("" + ","); // 付款人开户行行号
					//directpayresultStr.append(dto.getSpayeename() + ","); // 收款人全称
					directpayresultStr.append(dto.getSagentbnkcode() + ","); // 甘肃测试，改为代理银行行号
					directpayresultStr.append(dto.getSpayeeacct() + ","); // 收款人账号
					directpayresultStr.append("" + ","); // 收款人开户行
					directpayresultStr.append("" + ","); // 收款人开户行行号
					directpayresultStr.append(dto.getSbudgettype() + ","); // 预算种类代码
					directpayresultStr.append(dto.getFamt() + ","); // 零余额发生额
					directpayresultStr.append("0.00" + ","); // 小额现金发生额
					directpayresultStr.append("" + ","); // 摘要代码
					String voucherNo = dto.getSvouno();//凭证编号截取后八位数据
					if(voucherNo.length()>8)
						voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
					directpayresultStr.append(voucherNo + ","); // 凭证编号
					directpayresultStr.append(TimeFacade.formatDate(dto.getDvoudate(),
							"yyyyMMdd")); // 凭证日期
					directpayresultStr.append("\n");

					List<TvPayreckBankListDto> resultsub = this.exportTBSfiletxtService
							.getsubinfo(dto);
					for (TvPayreckBankListDto subdto : resultsub) {
						directpayresultStr.append(subdto.getSbdgorgcode() + ",");// 预算单位代码
						directpayresultStr.append(subdto.getSfuncbdgsbtcode() + ",");// 功能科目代码
						if (null==subdto.getSecnomicsubjectcode()|| subdto.getSecnomicsubjectcode().trim().length()<1) {
							directpayresultStr.append("" + ",");// 经济科目代码
						}else{
							directpayresultStr.append(subdto.getSecnomicsubjectcode().trim() + ",");// 经济科目代码
						}
						directpayresultStr.append(subdto.getSacctprop() + ",");// 帐户性质
						directpayresultStr.append(subdto.getFamt());// 支付金额
						directpayresultStr.append("\n");// 换行
					}
				}else{//授权支付划款申请
					grantpayresultStr.append("**" + dto.getStrecode().toString() + ","); // 国库代码
					grantpayresultStr.append(","); // 付款人全称
					grantpayresultStr.append(dto.getSpayeracct() + ","); // 付款人帐号
					grantpayresultStr.append("" + ","); // 付款人开户行
					grantpayresultStr.append("" + ","); // 付款人开户行行号
					//grantpayresultStr.append(dto.getSpayeename() + ","); // 收款人全称
					grantpayresultStr.append(dto.getSagentbnkcode() + ","); // 甘肃测试，改为代理银行行号
					grantpayresultStr.append(dto.getSpayeeacct() + ","); // 收款人账号
					grantpayresultStr.append("" + ","); // 收款人开户行
					grantpayresultStr.append("" + ","); // 收款人开户行行号
					grantpayresultStr.append(dto.getSbudgettype() + ","); // 预算种类代码
					grantpayresultStr.append(dto.getFamt() + ","); // 零余额发生额
					grantpayresultStr.append("0.00" + ","); // 小额现金发生额
					grantpayresultStr.append("" + ","); // 摘要代码
					String voucherNo = dto.getSvouno();
					if(voucherNo.length()>8)
						voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
					grantpayresultStr.append(voucherNo + ","); // 凭证编号
					grantpayresultStr.append(TimeFacade.formatDate(dto.getDvoudate(),
							"yyyyMMdd")); // 凭证日期
					grantpayresultStr.append("\n");

					List<TvPayreckBankListDto> resultsub = this.exportTBSfiletxtService
							.getsubinfo(dto);
					for (TvPayreckBankListDto subdto : resultsub) {
						grantpayresultStr.append(subdto.getSbdgorgcode() + ",");// 预算单位代码
						grantpayresultStr.append(subdto.getSfuncbdgsbtcode() + ",");// 功能科目代码
						if (null==subdto.getSecnomicsubjectcode()|| subdto.getSecnomicsubjectcode().trim().length()<1) {
							grantpayresultStr.append("" + ",");// 经济科目代码
						}else{
							grantpayresultStr.append(subdto.getSecnomicsubjectcode().trim() + ",");// 经济科目代码
						}
						grantpayresultStr.append(subdto.getSacctprop() + ",");// 帐户性质
						grantpayresultStr.append(subdto.getFamt());// 支付金额
						grantpayresultStr.append("\n");// 换行
					}
				}
			}
			if(!directpayresultStr.toString().equals("")){
				FileUtil.getInstance().writeFile(directpayfileName, directpayresultStr.toString());
			}
			if(!grantpayresultStr.toString().equals("")){
				FileUtil.getInstance().writeFile(grantpayfileName, grantpayresultStr.toString());
			}
			
		} else if (result.get(0) instanceof TvDirectpaymsgmainDto) {// 直接支付          直接支付额度明细岗文件
			for (IDto tmp : result) {
				resultStr = new StringBuffer();
				String directpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"01"+ctrimflag+".txt";	
				TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) tmp;
				resultStr.append("" + dto.getStrecode() + ","); // 国库代码
				resultStr.append(dto.getSpayacctno() + ","); // 付款人帐号
				resultStr.append(","); // 付款人全称
				resultStr.append("" + ","); // 付款人开户银行
				resultStr.append(dto.getSpayeeacctno() + ","); // 收款人帐号
				resultStr.append(","); // 收款人全称
				resultStr.append(dto.getSpaybankno() + ","); // 收款人开户行
				resultStr.append(dto.getSbudgettype() + ","); // 预算种类代码
				resultStr.append(dto.getNmoney() + ","); // 合计金额
				String voucherNo = dto.getIvousrlno()+"";
				if(voucherNo.length()>8)
					voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
				resultStr.append(voucherNo + ","); // 凭证编号
				resultStr.append("" + ","); // 对应凭证编号
				resultStr.append(dto.getSgenticketdate()); // 凭证日期
				resultStr.append("\n");

				List<TvDirectpaymsgsubDto> resultsub = this.exportTBSfiletxtService
						.getsubinfo(dto);
				for (TvDirectpaymsgsubDto subdto : resultsub) {
					resultStr.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码
					if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
						resultStr.append("" + ",");// 经济科目代码
					}else{
						resultStr.append(subdto.getSecosubjectcode().trim() + ",");// 经济科目代码
					}
					resultStr.append(subdto.getSagencycode() + ",");// 预算单位代码
					resultStr.append(subdto.getNmoney());// 发生额
					resultStr.append("\n");// 换行
				}
				FileUtil.getInstance().writeFile(directpayfileName, resultStr.toString());
			}
			
		} else if (result.get(0) instanceof TvGrantpaymsgmainDto) {// TV_GRANTPAYMSGMAIN  授权支付
			for (IDto tmp : result) {
				resultStr = new StringBuffer();
				String grantpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"02"+ctrimflag+".txt";	
				TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) tmp;
				resultStr.append(dto.getStrecode().toString() + ","); // 国库代码
				String voucherNo = dto.getIvousrlno()+"";
				if(voucherNo.length()>8)
					voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
				resultStr.append(voucherNo + ","); // 凭证编号
				resultStr.append(dto.getSpaybankno() + ","); // 代理银行代码
				resultStr.append(dto.getSbudgetunitcode() + ","); // 预算单位代码
				resultStr.append(dto.getSofmonth() + ","); // 所属月份
				resultStr.append(dto.getSbudgettype() + ","); // 预算种类
				resultStr.append(dto.getNmoney() + ","); // 零余额发生额
				resultStr.append("0.00"); // 小额现金发生额
				resultStr.append("\n");

				List<TvGrantpaymsgsubDto> resultsub = this.exportTBSfiletxtService
						.getsubinfo(dto);
				for (TvGrantpaymsgsubDto subdto : resultsub) {
					resultStr.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码
					if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
						resultStr.append("" + ",");// 经济科目代码
					}else{
						resultStr.append(subdto.getSecosubjectcode().trim() + ",");// 经济科目代码
					}
					resultStr.append(subdto.getNmoney() + ",");// 零余额发生额
					resultStr.append("0.00");// 小额现金发生额
					resultStr.append("\n");// 换行
				}
				FileUtil.getInstance().writeFile(grantpayfileName, resultStr.toString());
			}
			
		} else if (result.get(0) instanceof TvPayoutmsgmainDto) {// TV_PAYOUTMSGMAIN  实拨资金
			//划分 一般支出和调拨
			StringBuffer diaobo = new StringBuffer();
			StringBuffer payout = new StringBuffer();
			String diaobofileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"17"+ctrimflag+".txt";
			String payoutfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"23"+ctrimflag+".txt";
			for (IDto tmp : result) {
				TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) tmp;
				List<TvPayoutmsgsubDto> resultsub = this.exportTBSfiletxtService
				.getsubinfo(dto);
				for(TvPayoutmsgsubDto subdto : resultsub){
					String moveflag=this.exportTBSfiletxtService.getmoveflag(dto,subdto);
					if (null==moveflag) {
						MessageDialog.openMessageDialog(null, "未查询到对应的支出种类");
					}else {
						if(moveflag.equals("1")){
							payout.append(dto.getStrecode() + ","); // 国库代码
							payout.append(dto.getSpayeracct() + ",");// 付款账号
							payout.append(dto.getSpaysummarycode() + ",");// 支出原因代码
							payout.append(dto.getSbudgetunitcode() + ",");// 预算单位代码
							payout.append(dto.getSrecacct() + ",");// 收款单位账号
							payout.append(dto.getSbudgettype() + ",");// 预算种类
							payout.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码 ？？？
							payout.append(subdto.getSecnomicsubjectcode() + ",");// 经济科目代码
							payout.append("" + ",");// 会计科目代码
							payout.append(moveflag + ",");// 支出种类 ？？？
							payout.append(subdto.getNmoney() + ",");// 发生额
							String seqno = String.valueOf(subdto.getSseqno());
							if(seqno.length()==1){
								seqno = "00"+seqno;
							}else if(seqno.length()==2){
								seqno = "0"+seqno;
							}
							payout.append(dto.getStaxticketno()+seqno);// 凭证号码
							payout.append("\n");
						}else{
							diaobo.append(dto.getStrecode() + ","); // 国库代码
							diaobo.append(dto.getSpayeracct() + ",");// 付款账号
							diaobo.append(dto.getSpaysummarycode() + ",");// 支出原因代码
							diaobo.append(dto.getSbudgetunitcode() + ",");// 预算单位代码
							diaobo.append(dto.getSrecacct() + ",");// 收款单位账号
							diaobo.append(dto.getSbudgettype() + ",");// 预算种类
							diaobo.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码 ？？？
							diaobo.append(subdto.getSecnomicsubjectcode() + ",");// 经济科目代码
							diaobo.append("" + ",");// 会计科目代码
							diaobo.append(moveflag + ",");// 支出种类 ？？？
							diaobo.append(subdto.getNmoney() + ",");// 发生额
							String seqno = String.valueOf(subdto.getSseqno());
							if(seqno.length()==1){
								seqno = "00"+seqno;
							}else if(seqno.length()==2){
								seqno = "0"+seqno;
							}
							payout.append(dto.getStaxticketno()+seqno);// 凭证号码
							diaobo.append("\n");
						}
					}
				}
				
//				resultStr.append(dto.getSclearbankcode() + ",");// 发起行行号
//				
//				TvPayoutmsgmainDto tmpdto=new TvPayoutmsgmainDto();
//				tmpdto.setSrecbankname(dto.getSrecbankname());
//				resultStr.append(dto.getSrecbankno() + ",");// 接收行行号
//				resultStr.append(dto.getSxpayamt() + ",");// 金额
//				resultStr.append( dto.getSclearbankcode()+ ",");// 付款人开户行行号
//				resultStr.append(dto.getSpayeracct() + ",");// 付款人账号
//				resultStr.append(dto.getSpayername() + ",");// 付款人名称
//				resultStr.append(dto.getSpayeraddr() + ",");// 付款人地址
//				resultStr.append(dto.getSrecbankno() + ",");// 收款人开户行行号
//				resultStr.append(dto.getSrecacct() + ",");// 收款人账号
//				resultStr.append(dto.getSrecname() + ",");// 收款人名称
//				resultStr.append("" + ",");// 收款人地址
//				resultStr.append("50" + ",");// 支付业务种类
//				resultStr.append("00100" + ",");// 业务类型号
//				resultStr.append(dto.getSpaysummaryname());// 附言
//				resultStr.append("\n");// 换行
				
//				List<TvPayoutmsgsubDto> resultsub = this.exportTBSfiletxtService
//						.getsubinfo(dto);
//				String moveflag=this.exportTBSfiletxtService.getmoveflag(dto);
//				for (TvPayoutmsgsubDto subdto : resultsub) {
//					resultStr.append(dto.getStrecode() + ","); // 国库代码
//					resultStr.append(dto.getSpayeracct() + ",");// 付款账号
//					resultStr.append(dto.getSpaysummarycode() + ",");// 支出原因代码
//					resultStr.append(subdto.getSagencycode() + ",");// 预算单位代码
//					resultStr.append(dto.getSrecacct() + ",");// 收款单位账号
//					resultStr.append(dto.getSbudgettype() + ",");// 预算种类
//					resultStr.append(subdto.getSexpfunccode1() + ",");// 功能科目代码 ？？？
//					resultStr.append(subdto.getSecnomicsubjectcode() + ",");// 经济科目代码
//					resultStr.append("" + ",");// 会计科目代码
//					if (null==moveflag) {
//						MessageDialog.openMessageDialog(null, "未查询到对应的支出种类");
//					}else {
//						resultStr.append(moveflag + ",");// 支出种类 ？？？
//					}
//					
//					resultStr.append(subdto.getNmoney() + ",");// 发生额
//					resultStr.append(subdto.getStaxticketno());// 凭证号码
//					resultStr.append("\n");
//
//				}
			}
			if(!diaobo.toString().equals("")){
				FileUtil.getInstance().writeFile(diaobofileName, diaobo.toString());
			}
			if(!payout.toString().equals("")){
				FileUtil.getInstance().writeFile(payoutfileName, payout.toString());
			}
		}

	}

	/**
	 * Direction: 全选 ename: selectedAll 引用方法: viewers: * messages:
	 */
	public String selectedAll(Object o) {
		return super.selectedAll(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
	public void setSbiztypetbs(String sbiztypetbs) {
		this.sbiztypetbs = sbiztypetbs;
	}

	public String getSbiztypetbs() {
		return sbiztypetbs;
	}

}