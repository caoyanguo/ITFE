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
 * @time 13-10-29 06:18:53 ��ϵͳ: SendBiz ģ��:exportTBSfiletxt ���:ExportTBSfiletxt
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
	 * Direction: ���� ename: exportTBS ���÷���: viewers: * messages:
	 */
	public String exportTBS(Object o) {
		try {
			List<IDto> result = this.exportTBSfiletxtService.exportTBSdata(
					sbiztypetbs, strecode, dacctdate, ctrimflag);
			if (null == result || result.size() == 0) {
				MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ�");
				return null;
			}
			// ѡ�񱣴�·��
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String filePath = path.open();
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
				return "";
			}
			String fileName = filePath + File.separator;
			// + getfilename(getSbiztypetbs());

			expdata(result, fileName);
			this.exportTBSfiletxtService.updateVdtoStatus(result);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.exportTBS(o);
	}
	
	// ��ȡ�ļ�����
	private String getfilename(String typeTBS) {
		// �ļ����ƣ�yyyyMMddxxxxxxttm.txt
		StringBuffer fileName = new StringBuffer();
		if (null == getDacctdate()) {
			fileName.append(TimeFacade.formatDate(TimeFacade.getCurrentDate(),
					"yyyyMMdd"));// �����ļ�����������
		} else {
			fileName.append(TimeFacade.formatDate(getDacctdate(), "yyyyMMdd"));// �����ļ�����������
		}
		try {
			fileName.append(this.exportTBSfiletxtService.getTBSNum());
		} catch (ITFEBizException e) {
			e.printStackTrace();
			log.info("�������κ��쳣");
			MessageDialog.openMessageDialog(null, e.getMessage());
			return null;
		}// ���κ�2��4λ�����ļ������κţ������ڹ���ϵͳΨһ��ʶ�����ļ���û�й���ҵ����
		// ҵ������
		if (typeTBS.equalsIgnoreCase(StateConstant.TBS_SBZJ)) {
			fileName.append("11");

		} else if (typeTBS.equalsIgnoreCase(StateConstant.TBS_ZJZFED)) {
			fileName.append("01");
		} else if (typeTBS.equalsIgnoreCase(StateConstant.TBS_SQZFED)) {
			fileName.append("02");
		} else if (typeTBS.equalsIgnoreCase(StateConstant.TBS_JZZFHKSQ)) {
			fileName.append("01");
		}
		// MΪ�����ڱ�־��1λ���D�D 0�������ڣ�1��������
		if (null == getCtrimflag()) {
			fileName.append("0");// ������
		} else {
			fileName.append(getCtrimflag());
		}
		fileName.append(".txt");
		return fileName.toString();
	}

	private void expdata(List<IDto> result, String fileName)
			throws FileOperateException, ITFEBizException {
		StringBuffer resultStr = new StringBuffer();

		if (result.get(0) instanceof TvPayreckBankDto) {// ����֧��������ϸ���ļ�
			String directpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"25"+ctrimflag+".txt";
			String grantpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"27"+ctrimflag+".txt";	
			StringBuffer directpayresultStr = new StringBuffer();
			StringBuffer grantpayresultStr = new StringBuffer();
			for (IDto tmp : result) {
				TvPayreckBankDto dto = (TvPayreckBankDto) tmp;
				if(dto.getSpaytypecode().equals("11")){//ֱ��֧����������
					directpayresultStr.append("**" + dto.getStrecode().toString() + ","); // �������
					directpayresultStr.append(","); // ������ȫ��
					directpayresultStr.append(dto.getSpayeracct() + ","); // �������ʺ�
					directpayresultStr.append("" + ","); // �����˿�����
					directpayresultStr.append("" + ","); // �����˿������к�
					//directpayresultStr.append(dto.getSpayeename() + ","); // �տ���ȫ��
					directpayresultStr.append(dto.getSagentbnkcode() + ","); // ������ԣ���Ϊ���������к�
					directpayresultStr.append(dto.getSpayeeacct() + ","); // �տ����˺�
					directpayresultStr.append("" + ","); // �տ��˿�����
					directpayresultStr.append("" + ","); // �տ��˿������к�
					directpayresultStr.append(dto.getSbudgettype() + ","); // Ԥ���������
					directpayresultStr.append(dto.getFamt() + ","); // ��������
					directpayresultStr.append("0.00" + ","); // С���ֽ�����
					directpayresultStr.append("" + ","); // ժҪ����
					String voucherNo = dto.getSvouno();//ƾ֤��Ž�ȡ���λ����
					if(voucherNo.length()>8)
						voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
					directpayresultStr.append(voucherNo + ","); // ƾ֤���
					directpayresultStr.append(TimeFacade.formatDate(dto.getDvoudate(),
							"yyyyMMdd")); // ƾ֤����
					directpayresultStr.append("\n");

					List<TvPayreckBankListDto> resultsub = this.exportTBSfiletxtService
							.getsubinfo(dto);
					for (TvPayreckBankListDto subdto : resultsub) {
						directpayresultStr.append(subdto.getSbdgorgcode() + ",");// Ԥ�㵥λ����
						directpayresultStr.append(subdto.getSfuncbdgsbtcode() + ",");// ���ܿ�Ŀ����
						if (null==subdto.getSecnomicsubjectcode()|| subdto.getSecnomicsubjectcode().trim().length()<1) {
							directpayresultStr.append("" + ",");// ���ÿ�Ŀ����
						}else{
							directpayresultStr.append(subdto.getSecnomicsubjectcode().trim() + ",");// ���ÿ�Ŀ����
						}
						directpayresultStr.append(subdto.getSacctprop() + ",");// �ʻ�����
						directpayresultStr.append(subdto.getFamt());// ֧�����
						directpayresultStr.append("\n");// ����
					}
				}else{//��Ȩ֧����������
					grantpayresultStr.append("**" + dto.getStrecode().toString() + ","); // �������
					grantpayresultStr.append(","); // ������ȫ��
					grantpayresultStr.append(dto.getSpayeracct() + ","); // �������ʺ�
					grantpayresultStr.append("" + ","); // �����˿�����
					grantpayresultStr.append("" + ","); // �����˿������к�
					//grantpayresultStr.append(dto.getSpayeename() + ","); // �տ���ȫ��
					grantpayresultStr.append(dto.getSagentbnkcode() + ","); // ������ԣ���Ϊ���������к�
					grantpayresultStr.append(dto.getSpayeeacct() + ","); // �տ����˺�
					grantpayresultStr.append("" + ","); // �տ��˿�����
					grantpayresultStr.append("" + ","); // �տ��˿������к�
					grantpayresultStr.append(dto.getSbudgettype() + ","); // Ԥ���������
					grantpayresultStr.append(dto.getFamt() + ","); // ��������
					grantpayresultStr.append("0.00" + ","); // С���ֽ�����
					grantpayresultStr.append("" + ","); // ժҪ����
					String voucherNo = dto.getSvouno();
					if(voucherNo.length()>8)
						voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
					grantpayresultStr.append(voucherNo + ","); // ƾ֤���
					grantpayresultStr.append(TimeFacade.formatDate(dto.getDvoudate(),
							"yyyyMMdd")); // ƾ֤����
					grantpayresultStr.append("\n");

					List<TvPayreckBankListDto> resultsub = this.exportTBSfiletxtService
							.getsubinfo(dto);
					for (TvPayreckBankListDto subdto : resultsub) {
						grantpayresultStr.append(subdto.getSbdgorgcode() + ",");// Ԥ�㵥λ����
						grantpayresultStr.append(subdto.getSfuncbdgsbtcode() + ",");// ���ܿ�Ŀ����
						if (null==subdto.getSecnomicsubjectcode()|| subdto.getSecnomicsubjectcode().trim().length()<1) {
							grantpayresultStr.append("" + ",");// ���ÿ�Ŀ����
						}else{
							grantpayresultStr.append(subdto.getSecnomicsubjectcode().trim() + ",");// ���ÿ�Ŀ����
						}
						grantpayresultStr.append(subdto.getSacctprop() + ",");// �ʻ�����
						grantpayresultStr.append(subdto.getFamt());// ֧�����
						grantpayresultStr.append("\n");// ����
					}
				}
			}
			if(!directpayresultStr.toString().equals("")){
				FileUtil.getInstance().writeFile(directpayfileName, directpayresultStr.toString());
			}
			if(!grantpayresultStr.toString().equals("")){
				FileUtil.getInstance().writeFile(grantpayfileName, grantpayresultStr.toString());
			}
			
		} else if (result.get(0) instanceof TvDirectpaymsgmainDto) {// ֱ��֧��          ֱ��֧�������ϸ���ļ�
			for (IDto tmp : result) {
				resultStr = new StringBuffer();
				String directpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"01"+ctrimflag+".txt";	
				TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) tmp;
				resultStr.append("" + dto.getStrecode() + ","); // �������
				resultStr.append(dto.getSpayacctno() + ","); // �������ʺ�
				resultStr.append(","); // ������ȫ��
				resultStr.append("" + ","); // �����˿�������
				resultStr.append(dto.getSpayeeacctno() + ","); // �տ����ʺ�
				resultStr.append(","); // �տ���ȫ��
				resultStr.append(dto.getSpaybankno() + ","); // �տ��˿�����
				resultStr.append(dto.getSbudgettype() + ","); // Ԥ���������
				resultStr.append(dto.getNmoney() + ","); // �ϼƽ��
				String voucherNo = dto.getIvousrlno()+"";
				if(voucherNo.length()>8)
					voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
				resultStr.append(voucherNo + ","); // ƾ֤���
				resultStr.append("" + ","); // ��Ӧƾ֤���
				resultStr.append(dto.getSgenticketdate()); // ƾ֤����
				resultStr.append("\n");

				List<TvDirectpaymsgsubDto> resultsub = this.exportTBSfiletxtService
						.getsubinfo(dto);
				for (TvDirectpaymsgsubDto subdto : resultsub) {
					resultStr.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ����
					if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
						resultStr.append("" + ",");// ���ÿ�Ŀ����
					}else{
						resultStr.append(subdto.getSecosubjectcode().trim() + ",");// ���ÿ�Ŀ����
					}
					resultStr.append(subdto.getSagencycode() + ",");// Ԥ�㵥λ����
					resultStr.append(subdto.getNmoney());// ������
					resultStr.append("\n");// ����
				}
				FileUtil.getInstance().writeFile(directpayfileName, resultStr.toString());
			}
			
		} else if (result.get(0) instanceof TvGrantpaymsgmainDto) {// TV_GRANTPAYMSGMAIN  ��Ȩ֧��
			for (IDto tmp : result) {
				resultStr = new StringBuffer();
				String grantpayfileName = fileName+dacctdate.toString().replaceAll("-", "")+this.exportTBSfiletxtService.getTBSNum()+"02"+ctrimflag+".txt";	
				TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) tmp;
				resultStr.append(dto.getStrecode().toString() + ","); // �������
				String voucherNo = dto.getIvousrlno()+"";
				if(voucherNo.length()>8)
					voucherNo = voucherNo.substring(voucherNo.length()-8, voucherNo.length());
				resultStr.append(voucherNo + ","); // ƾ֤���
				resultStr.append(dto.getSpaybankno() + ","); // �������д���
				resultStr.append(dto.getSbudgetunitcode() + ","); // Ԥ�㵥λ����
				resultStr.append(dto.getSofmonth() + ","); // �����·�
				resultStr.append(dto.getSbudgettype() + ","); // Ԥ������
				resultStr.append(dto.getNmoney() + ","); // ��������
				resultStr.append("0.00"); // С���ֽ�����
				resultStr.append("\n");

				List<TvGrantpaymsgsubDto> resultsub = this.exportTBSfiletxtService
						.getsubinfo(dto);
				for (TvGrantpaymsgsubDto subdto : resultsub) {
					resultStr.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ����
					if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
						resultStr.append("" + ",");// ���ÿ�Ŀ����
					}else{
						resultStr.append(subdto.getSecosubjectcode().trim() + ",");// ���ÿ�Ŀ����
					}
					resultStr.append(subdto.getNmoney() + ",");// ��������
					resultStr.append("0.00");// С���ֽ�����
					resultStr.append("\n");// ����
				}
				FileUtil.getInstance().writeFile(grantpayfileName, resultStr.toString());
			}
			
		} else if (result.get(0) instanceof TvPayoutmsgmainDto) {// TV_PAYOUTMSGMAIN  ʵ���ʽ�
			//���� һ��֧���͵���
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
						MessageDialog.openMessageDialog(null, "δ��ѯ����Ӧ��֧������");
					}else {
						if(moveflag.equals("1")){
							payout.append(dto.getStrecode() + ","); // �������
							payout.append(dto.getSpayeracct() + ",");// �����˺�
							payout.append(dto.getSpaysummarycode() + ",");// ֧��ԭ�����
							payout.append(dto.getSbudgetunitcode() + ",");// Ԥ�㵥λ����
							payout.append(dto.getSrecacct() + ",");// �տλ�˺�
							payout.append(dto.getSbudgettype() + ",");// Ԥ������
							payout.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ���� ������
							payout.append(subdto.getSecnomicsubjectcode() + ",");// ���ÿ�Ŀ����
							payout.append("" + ",");// ��ƿ�Ŀ����
							payout.append(moveflag + ",");// ֧������ ������
							payout.append(subdto.getNmoney() + ",");// ������
							String seqno = String.valueOf(subdto.getSseqno());
							if(seqno.length()==1){
								seqno = "00"+seqno;
							}else if(seqno.length()==2){
								seqno = "0"+seqno;
							}
							payout.append(dto.getStaxticketno()+seqno);// ƾ֤����
							payout.append("\n");
						}else{
							diaobo.append(dto.getStrecode() + ","); // �������
							diaobo.append(dto.getSpayeracct() + ",");// �����˺�
							diaobo.append(dto.getSpaysummarycode() + ",");// ֧��ԭ�����
							diaobo.append(dto.getSbudgetunitcode() + ",");// Ԥ�㵥λ����
							diaobo.append(dto.getSrecacct() + ",");// �տλ�˺�
							diaobo.append(dto.getSbudgettype() + ",");// Ԥ������
							diaobo.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ���� ������
							diaobo.append(subdto.getSecnomicsubjectcode() + ",");// ���ÿ�Ŀ����
							diaobo.append("" + ",");// ��ƿ�Ŀ����
							diaobo.append(moveflag + ",");// ֧������ ������
							diaobo.append(subdto.getNmoney() + ",");// ������
							String seqno = String.valueOf(subdto.getSseqno());
							if(seqno.length()==1){
								seqno = "00"+seqno;
							}else if(seqno.length()==2){
								seqno = "0"+seqno;
							}
							payout.append(dto.getStaxticketno()+seqno);// ƾ֤����
							diaobo.append("\n");
						}
					}
				}
				
//				resultStr.append(dto.getSclearbankcode() + ",");// �������к�
//				
//				TvPayoutmsgmainDto tmpdto=new TvPayoutmsgmainDto();
//				tmpdto.setSrecbankname(dto.getSrecbankname());
//				resultStr.append(dto.getSrecbankno() + ",");// �������к�
//				resultStr.append(dto.getSxpayamt() + ",");// ���
//				resultStr.append( dto.getSclearbankcode()+ ",");// �����˿������к�
//				resultStr.append(dto.getSpayeracct() + ",");// �������˺�
//				resultStr.append(dto.getSpayername() + ",");// ����������
//				resultStr.append(dto.getSpayeraddr() + ",");// �����˵�ַ
//				resultStr.append(dto.getSrecbankno() + ",");// �տ��˿������к�
//				resultStr.append(dto.getSrecacct() + ",");// �տ����˺�
//				resultStr.append(dto.getSrecname() + ",");// �տ�������
//				resultStr.append("" + ",");// �տ��˵�ַ
//				resultStr.append("50" + ",");// ֧��ҵ������
//				resultStr.append("00100" + ",");// ҵ�����ͺ�
//				resultStr.append(dto.getSpaysummaryname());// ����
//				resultStr.append("\n");// ����
				
//				List<TvPayoutmsgsubDto> resultsub = this.exportTBSfiletxtService
//						.getsubinfo(dto);
//				String moveflag=this.exportTBSfiletxtService.getmoveflag(dto);
//				for (TvPayoutmsgsubDto subdto : resultsub) {
//					resultStr.append(dto.getStrecode() + ","); // �������
//					resultStr.append(dto.getSpayeracct() + ",");// �����˺�
//					resultStr.append(dto.getSpaysummarycode() + ",");// ֧��ԭ�����
//					resultStr.append(subdto.getSagencycode() + ",");// Ԥ�㵥λ����
//					resultStr.append(dto.getSrecacct() + ",");// �տλ�˺�
//					resultStr.append(dto.getSbudgettype() + ",");// Ԥ������
//					resultStr.append(subdto.getSexpfunccode1() + ",");// ���ܿ�Ŀ���� ������
//					resultStr.append(subdto.getSecnomicsubjectcode() + ",");// ���ÿ�Ŀ����
//					resultStr.append("" + ",");// ��ƿ�Ŀ����
//					if (null==moveflag) {
//						MessageDialog.openMessageDialog(null, "δ��ѯ����Ӧ��֧������");
//					}else {
//						resultStr.append(moveflag + ",");// ֧������ ������
//					}
//					
//					resultStr.append(subdto.getNmoney() + ",");// ������
//					resultStr.append(subdto.getStaxticketno());// ƾ֤����
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
	 * Direction: ȫѡ ename: selectedAll ���÷���: viewers: * messages:
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