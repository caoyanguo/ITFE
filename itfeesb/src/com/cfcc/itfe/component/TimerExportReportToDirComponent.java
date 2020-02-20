package com.cfcc.itfe.component;

//import java.io.File;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.StatusOfReportDownLoad;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 
 * ���ķ��ͽ���
 * 
 */
@SuppressWarnings("unchecked")
public class TimerExportReportToDirComponent implements Callable {

	private static Log logger = LogFactory.getLog(TimerExportReportToDirComponent.class);
	private static String datadate = null;
//	private static String datatype = null;
	private static int count = 1;
	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.info("����ʱ���񵼳������ļ�start=================================="+datadate);
		logger.info("����ʱ���񵼳������ļ�count=================================="+count);
		if(ITFECommonConstant.PUBLICPARAM.contains(",exportReportToCsv=true,"))
		{
			if(count>=4)
				count = 1;
			exportReportToCsv();
			count++;
		}
		if(count==0)
			searchStatusOfReportDownLoad();
		logger.info("����ʱ���񵼳������ļ�end=================================="+TimeFacade.getCurrentStringTimebefor());
		return eventContext;
	}
	/**
	 * ���ɵ���Tips�ļ�
	 * 
	 * @generated
	 * @param list
	 * @param dto
	 * @param str
	 * @return java.util.Map
	 * @throws ITFEBizException
	 */
	private void exportReportToCsv() throws ITFEBizException
	{
		SQLExecutor selectsqlExec = null;
		try {
			List<String> filelist = new ArrayList<String>(); //����ļ��б�
			String beforDate = TimeFacade.getCurrentStringTimebefor();
			StringBuffer expcontent =new StringBuffer("");
			expcontent.append(PublicSearchFacade.getSqlConnectByProp());
			SQLResults rs = null;
			String abTipsRoot = null;
			if(isWin())
				abTipsRoot = "D:/itfe/kbftp/";
			else
				abTipsRoot = "/itfe/kbftp/";
			String shellpath = abTipsRoot+"tipsexport.sql";
			/*
			 * ��ǰ�����ļ�Ŀ¼������LINUX�ᱨ��
			 */
			FileUtil.getInstance().createDir(abTipsRoot);
			//���ڵõ�ĳ������һ��
			String fname = abTipsRoot+beforDate+"_3128_shoururibao_1.csv"; // 3128�����ձ���
			expcontent.append("export to "+fname+" of del select * from TR_INCOMEDAYRPT where S_RPTDATE='"+beforDate+"';\n");//select * from HTR_INCOMEDAYRPT where S_RPTDATE='"+beforDate+"' union
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3130_kucunribao_1.csv"; // 3130����ձ���
			expcontent.append("export to "+fname+" of del select * from TR_STOCKDAYRPT where S_RPTDATE='"+beforDate+"';\n");//select * from HTR_STOCKDAYRPT where S_RPTDATE='"+beforDate+"' union
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3139_rukuliushui_1.csv";//3139�����ˮ
			expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOME_DETAIL where S_INTREDATE='"+beforDate+"';\n");//select * from HTV_FIN_INCOME_DETAIL where S_INTREDATE='"+beforDate+"' union 
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3127_yusuanzhichu_1.csv";//3127֧������
			expcontent.append("export to "+fname+" of del select * from TR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"';\n");//select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"' union 
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3129_dianzishuipiao_1.csv"; // 3129����˰Ʊ
			expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"';\n");//select * from HTV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"' union 
			filelist.add(fname);
			if(datadate!=null&&!"".equals(datadate)&&!beforDate.equals(datadate))
			{
				if(selectsqlExec==null)
					selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"'",TvFinIncomeonlineDto.class);//select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+datadate+"' union 
				if(rs.getDtoCollection()!=null&&rs.getDtoCollection().size()>0)
				{
					fname = abTipsRoot+datadate+"_3128_shoururibao_1.csv"; // 3128�����ձ���
					expcontent.append("export to "+fname+" of del select * from TR_INCOMEDAYRPT where S_RPTDATE='"+datadate+"';\n");//select * from HTR_INCOMEDAYRPT where S_RPTDATE='"+datadate+"' union
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3130_kucunribao_1.csv"; // 3130����ձ���
					expcontent.append("export to "+fname+" of del select * from TR_STOCKDAYRPT where S_RPTDATE='"+datadate+"';\n");//select * from HTR_STOCKDAYRPT where S_RPTDATE='"+datadate+"' union 
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3139_rukuliushui_1.csv"; //3139�����ˮ
					expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOME_DETAIL where S_INTREDATE='"+datadate+"';\n");//select * from HTV_FIN_INCOME_DETAIL where S_INTREDATE='"+datadate+"' union 
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3127_yusuanzhichu_1.csv"; // 3127֧������
					expcontent.append("export to "+fname+" of del select * from TR_TAXORG_PAYOUT_REPORT where s_rptdate='"+datadate+"';\n");//select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+datadate+"' union 
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3129_dianzishuipiao_1.csv"; // 3129����˰Ʊ
					expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"';\n");//select * from HTV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"' union 
					filelist.add(fname);
				}
			}
			if(count==3)
			{
				if(selectsqlExec==null)
					selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"'",TvFinIncomeonlineDto.class);//����ĵ���˰Ʊ����һ�·���3129����˰Ʊ�ж�Ϊ׼select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"' union 
				if(rs.getDtoCollection()==null||rs.getDtoCollection().size()<=0)
				{
					if(datadate==null||"".equals(datadate))
						datadate = beforDate;//�õ�û�����ݵ�����
					else
					{
						selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"'",TvFinIncomeonlineDto.class);//����ĵ���˰Ʊ����һ�·���3129����˰Ʊ�ж�Ϊ׼ 
						if(rs!=null&&rs.getDtoCollection().size()>0)
							datadate = null;
					}
				}
				else if(datadate!=null&&!"".equals(datadate))
				{
					if(selectsqlExec==null)
						selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"'",TvFinIncomeonlineDto.class);//����ĵ���˰Ʊ����һ�·���3129����˰Ʊ�ж�Ϊ׼ 
					if(rs!=null&&rs.getDtoCollection().size()>0)
						datadate = null;
					else if(daysOfTwo(datadate,beforDate)>7)
						datadate = null;
						
				}
			}
			try{
				FileUtil.getInstance().deleteFileForExists(shellpath);
			}catch(Exception e)
			{
			}
			FileUtil.getInstance().writeFile(shellpath, expcontent.toString());
			CallShellUtil.callShellWithRes("chmod -R 777 "+shellpath);
			byte[] bytes = null;
			String results = null;
			bytes = DB2CallShell.dbCallShellWithRes(shellpath);
			FileUtil.getInstance().writeFile(abTipsRoot+TimeFacade.getCurrentStringTime()+".end", "");
			CallShellUtil.callShellWithRes("chmod -R 777 "+abTipsRoot+beforDate);
//			CallShellUtil.callShellWithRes("chmod 777 "+abTipsRoot+TimeFacade.getCurrentStringTime()+".end");
			if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
				results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
			} else {
				results = new String(bytes);
			}
			logger.error("����ʱ���񵼳������ļ�����SHELL���:" + results);			
		} catch (FileOperateException e) {
			logger.error("����ʱ���񵼳������ļ��ӱ���ͷ����",e);
			throw new ITFEBizException("����ʱ���񵼳������ļ��ӱ���ͷ����",e);
		} catch (FileNotFoundException e) {
			logger.error("����ʱ���񵼳������ļ�û���ҵ�",e);
			throw new ITFEBizException("����ʱ���񵼳������ļ�û���ҵ�",e);
		} catch (Exception e) {
			logger.error("����ʱ���񵼳������ļ�����",e);
			throw new ITFEBizException("����ʱ���񵼳������ļ�����",e);
		}finally
		{
			if(selectsqlExec!=null)
				selectsqlExec.closeConnection();
		}
	}
//	private List<String> process3129CSV(List<String> flist) throws FileOperateException, FileNotFoundException {
//		List<String> filel = new ArrayList<String>();
//		for(String file : flist) {
//			if(new File(file).exists()) {
//				if(file.indexOf("_3128_shoururibao") != -1) { //����3128sr���ַ���
//					String newContent = StateConstant.RecvTips_3128_SR_ColName+"\r\n"+FileUtil.getInstance().readFile(file); //���ɼ������Ƶ��ļ�����
//					FileUtil.getInstance().deleteFile(file); //ɾ��ԭ�ļ�
//					FileUtil.getInstance().writeFile(file, newContent); //�������ļ�
//					filel.add(file);
//				}else if(file.indexOf("_3128_kucunribao") != -1) { //����3128kc���ַ���
//					String newContent = StateConstant.RecvTips_3128_KC_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);
//					filel.add(file);
//				}else if(file.indexOf("_3129_dianzishuipiao") != -1) { //����3129���ַ���
//					// �����ÿ���ʽ�������ֶ�����
//					String newContent = StateConstant.RecvTips_3129_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);
//					filel.add(file);	
//				}else if(file.indexOf("_3139_rukuliushui") != -1) { //����3139���ַ���
//					String newContent = StateConstant.RecvTips_3139_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);	
//					filel.add(file);
//				}else if(file.indexOf("_3127_yusuanzhichu") != -1)
//				{
//					String newContent = StateConstant.RecvTips_3127_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);	
//					filel.add(file);
//				}
//			}		
//		}		
//		return filel;		
//	}
	private void searchStatusOfReportDownLoad() throws ITFEBizException
	{
		List<StatusOfReportDownLoad> list = new ArrayList<StatusOfReportDownLoad>();
		String beforDate = TimeFacade.getCurrentStringTimebefor();
		int downloadcount = 0;
		String stringdate = null;
		String tresql = "";
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(beforDate);
		List<TdDownloadReportCheckDto> checkList=null;
		try {
			checkList = CommonFacade.getODB().findRsByDtoForWhere(finddto,tresql);
			TsTreasuryDto tredto = new TsTreasuryDto();
			List<TsTreasuryDto> trelist =CommonFacade.getODB().findRsByDtoForWhere(tredto, tresql);
			Map<String, TsTreasuryDto> treNameMap = BusinessFacade.findTreasuryInfo("");
			Set<String> tmpTreSet = new HashSet<String>();
			StatusOfReportDownLoad dto =null;
			if(checkList!=null && checkList.size()>0){
				for(TdDownloadReportCheckDto checkdto: checkList){
					dto = new StatusOfReportDownLoad();
					dto.setSdates(checkdto.getSdates());
					dto.setStrecode(checkdto.getStrecode());
					dto.setStrename(treNameMap.get(dto.getStrecode()).getStrename());
					dto.setSkucun(checkdto.getSkucun()==null?"0":checkdto.getSkucun());
					dto.setSliushui(checkdto.getSliushui()==null?"0":checkdto.getSliushui());
					dto.setSribao(checkdto.getSribao()==null?"0":checkdto.getSribao());
					dto.setSshuipiao(checkdto.getSshuipiao()==null?"0":checkdto.getSshuipiao());
					dto.setSzhichu(checkdto.getSzhichu()==null?"0":checkdto.getSzhichu());
					list.add(dto);
					tmpTreSet.add(dto.getStrecode());
				}
			}
			for(TsTreasuryDto tre : trelist){
				if(tmpTreSet.add(tre.getStrecode())){
					StatusOfReportDownLoad srdldto = new StatusOfReportDownLoad();
					srdldto.setSdates(stringdate);
					srdldto.setStrecode(tre.getStrecode());
					srdldto.setStrename(tre.getStrename());
					srdldto.setSribao("0");
					srdldto.setSliushui("0");
					srdldto.setSshuipiao("0");
					srdldto.setSkucun("0");
					srdldto.setSzhichu("0");
					downloadcount++;
					list.add(srdldto);
				}
			}
			for(StatusOfReportDownLoad senddto:list)
			{
				if(("0".equals(senddto.getSribao())||"0".equals(senddto.getSkucun()))&&downloadcount!=trelist.size())
					sendMsg(dto.getStrecode(),beforDate,MsgConstant.MSG_NO_5001);
				if("0".equals(senddto.getSshuipiao())&&downloadcount!=trelist.size())
					sendMsg(senddto.getStrecode(),beforDate,MsgConstant.MSG_NO_5003);
				if("0".equals(senddto.getSliushui())&&downloadcount!=trelist.size())
					sendMsg(senddto.getStrecode(),beforDate,MsgConstant.MSG_NO_5002);
			}
		} catch (JAFDatabaseException e) {
			logger.error("����ʱ���񵼳������ļ������Щ����û��������������ʧ��!", e);
			throw new ITFEBizException("����ʱ���񵼳������ļ������Щ����û��������������ʧ��!", e);
		} catch (ValidateException e) {
			logger.error("����ʱ���񵼳������ļ������Щ����û��������������ʧ��!", e);
			throw new ITFEBizException("����ʱ���񵼳������ļ������Щ����û��������������ʧ��!", e);
		}		
	}
	private void sendMsg(String trecode, String inTreDate, String msgno)throws ITFEBizException {
		String sfinorg = null;
		try {
			TsConvertfinorgDto finadto = new TsConvertfinorgDto();
			finadto.setStrecode(trecode);
			List<TsConvertfinorgDto> list;
			list = CommonFacade.getODB().findRsByDto(finadto);
			if (null == list || list.size() == 0) 
				logger.error("���ڲ���������Ϣ��ά������Ͳ�������Ķ�Ӧ��ϵ!");
			sfinorg = list.get(0).getSfinorgcode();
			HeadDto headdto = new HeadDto();
			headdto.set_VER(MsgConstant.MSG_HEAD_VER);
			headdto.set_SRC(sfinorg);
			headdto.set_APP(MsgConstant.MSG_HEAD_APP);
			headdto.set_DES(inTreDate);
			headdto.set_msgNo(msgno);
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
			headdto.set_workDate(TimeFacade.getCurrentStringTime());
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgno + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, "000000000000");// �����������
			message.setProperty(MessagePropertyKeys.MSG_DATE, inTreDate);// �������
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, sfinorg);// ���ͻ��ش���
		
			message.setPayload(map);
			if(trecode!=null&&trecode.startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			logger.error("���ú�̨���Ĵ����ʱ������쳣!", e);
			throw new ITFEBizException("���ú�̨���Ĵ����ʱ������쳣!", e);
		} catch (JAFDatabaseException e) {
			logger.error("���ú�̨����������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("���ú�̨����������ݿ��쳣!", e);
		} catch (com.cfcc.itfe.exception.SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
		}
	}
//	private void requestRpt(String strecode, String srptdate)throws ITFEBizException {
//		try {
//			/**
//			 * ��һ�� ���ݹ�������ҵ���Ӧ�Ĳ�������
//			 */
//			TsConvertfinorgDto finadto = new TsConvertfinorgDto();
//			finadto.setStrecode(strecode);
//			List<TsConvertfinorgDto> list = CommonFacade.getODB().findRsByDto(finadto);
//			if (null == list || list.size() == 0) {
//				logger.error("���ڲ���������Ϣ��ά������Ͳ�������Ķ�Ӧ��ϵ!�������:"+strecode);
//				throw new ITFEBizException("���ڲ���������Ϣ��ά������Ͳ�������Ķ�Ӧ��ϵ!�������:"+strecode);
//			}
//		
//			String finorgcode = list.get(0).getSfinorgcode();
//		
//			/**
//			 * �ڶ��� ���ݲ�����������뱨�����ڵ��ú�̨����
//			 */
//			MuleClient client = new MuleClient();
//			Map map = new HashMap();
//			MuleMessage message = new DefaultMuleMessage(map);
//			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,MsgConstant.MSG_NO_5001 + "_OUT");
//			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorgcode);
//			message.setProperty(MessagePropertyKeys.MSG_DATE, srptdate);
//			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, list.get(0).getSorgcode());
//			message.setPayload(map);
//			message = client.send("vm://ManagerMsgToPbc", message);
//			ServiceUtil.checkResult(message);
//		} catch (MuleException e) {
//			logger.error("����YAK��̨���Ĵ�������쳣!", e);
//			throw new ITFEBizException("����YAK��̨���Ĵ�������쳣!", e);
//		} catch (JAFDatabaseException e) {
//			logger.error("���ú�̨����������ݿ��쳣!", e);
//			throw new ITFEBizException("���ú�̨����������ݿ��쳣!", e);
//		} catch (ValidateException e) {
//			logger.error("���ú�̨����������ݿ��쳣!", e);
//			throw new ITFEBizException("���ú�̨����������ݿ��쳣!", e);
//		}
//	}
	/**
	 * �����Ƚ���������֮�����������
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	private int daysOfTwo(String startDate, String endDate) {
		if(startDate==null||"".equals(startDate)||endDate==null||"".equals(endDate))
			return 0;
	     Calendar aCalendar = Calendar.getInstance();
	       aCalendar.setTime(TimeFacade.parseDate(startDate));
	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       aCalendar.setTime(TimeFacade.parseDate(endDate));
	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       return Math.abs(day2-day1);
	}
	private boolean isWin()
	 {
			String osName = System.getProperty("os.name");
			if (osName.indexOf("Windows") >= 0) {
				return true;
			} else {
				return false;
			}
	}
	
	private void exportBusinessData() throws ITFEBizException {
		String splitSign = ",";// "\t"; // �ļ���¼�ָ�����
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String strdate = TimeFacade.getCurrentStringTimebefor(); // ��ǰϵͳ������
		String path = root + dirsep + "businessFile" + dirsep + strdate
				+ dirsep;
		String strdate1 = strdate.substring(0, 4)+"-"+strdate.substring(4, 6)+"-"+strdate.substring(6, 8);
		try {
			List<TsTreasuryDto> trelist = CommonFacade.getODB().findRsByDto(
					new TsTreasuryDto());
			if (trelist != null && trelist.size() > 0) {
				for (TsTreasuryDto tredto : trelist) {

					String sql = " and S_COMMITDATE='"
							+ strdate
							+ "' and S_TRECODE='" + tredto.getStrecode()+"'";
					directPlan(splitSign, path, sql);
					grantPlan(splitSign, path, sql);
					payout(splitSign, path, sql);
					payreckbank(splitSign, path, " and D_VOUDATE='"
							+ strdate1
							+ "' and S_TRECODE='" + tredto.getStrecode()+"'");
					payreckbankback(splitSign, path, " and D_VOUDATE='"
							+ strdate1
							+ "' and S_TRECODE='" + tredto.getStrecode()+"'");
				}
			}

		} catch (Exception e) {
			logger.error("���ú�̨���Ĵ����ʱ������쳣!", e);
			throw new ITFEBizException("���ú�̨���Ĵ����ʱ������쳣!", e);
		}
	}

	/**
	 * 
	 * @param splitSign
	 * @param path
	 * @param string
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void payreckbankback(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvPayreckBankBackDto> list = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvPayreckBankBackDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (list != null && list.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ list.get(0).getStrecode() + "280hd.txt";
			path = path + filename;
			for (TvPayreckBankBackDto _dto : (List<TvPayreckBankBackDto>) list) {
				filebuf.append("**"+_dto.getSbookorgcode()+splitSign);//�����������
				filebuf.append(_dto.getDentrustdate()+splitSign);//ί������
				filebuf.append(_dto.getSpackno()+splitSign);//����ˮ��
				filebuf.append(_dto.getSagentbnkcode()+splitSign);//���������к�
				filebuf.append(_dto.getStrano()+splitSign);//������ˮ��
				filebuf.append(_dto.getIvousrlno()+splitSign);//ƾ֤��ˮ��
				filebuf.append(_dto.getSvouno()+splitSign);//ƾ֤���
				filebuf.append(_dto.getDvoudate()+splitSign);//ƾ֤����
				filebuf.append(_dto.getSfinorgcode()+splitSign);//�������ش���
				filebuf.append(_dto.getStrecode()+splitSign);//�������
				filebuf.append(_dto.getSoritrano()+splitSign);//ԭ������ˮ��
				filebuf.append(_dto.getDorientrustdate()+splitSign);//ԭί������
				filebuf.append(_dto.getSorivouno()+splitSign);//ԭƾ֤���
				filebuf.append(_dto.getDorivoudate()+splitSign);//ԭƾ֤����
				filebuf.append(_dto.getSpayeracct()+splitSign);//�������˺�
				filebuf.append(_dto.getSpayername()+splitSign);//����������
				filebuf.append(_dto.getSpayeeacct()+splitSign);//�տ����˺�
				filebuf.append(_dto.getSpayeename()+splitSign);//�տ�������
				filebuf.append(_dto.getSpaydictateno()+splitSign);//֧���������
				filebuf.append(_dto.getSpaymsgno()+splitSign);//֧�����ı��
				filebuf.append(_dto.getDpayentrustdate()+splitSign);//֧��ί������
				filebuf.append(_dto.getSpaysndbnkno()+splitSign);//֧���������к�
				filebuf.append(_dto.getSbudgettype()+splitSign);//Ԥ������
				filebuf.append(_dto.getStrimsign()+splitSign);//�����ڱ�־
				filebuf.append(_dto.getSofyear()+splitSign);//�������
				filebuf.append(_dto.getFamt()+splitSign);//���
				filebuf.append(_dto.getIstatinfnum()+splitSign);//��ϸ��Ϣ����
				filebuf.append(_dto.getSstatus()+splitSign);//����״̬
				filebuf.append(_dto.getSaddword()+splitSign);//����
				filebuf.append(_dto.getDacceptdate()+splitSign);//��������
				filebuf.append(_dto.getTsupdate()+splitSign);//����ʱ��
				filebuf.append(_dto.getSpaymode()+splitSign);//֧����ʽ
				filebuf.append(_dto.getSfilename()+splitSign);//�ļ���
				filebuf.append(_dto.getSid()+splitSign);//���뻮��ƾ֤Id
				filebuf.append(_dto.getSbgttypecode()+splitSign);//Ԥ�����ͱ���
				filebuf.append(_dto.getSbgttypename()+splitSign);//Ԥ����������
				filebuf.append(_dto.getSfundtypecode()+splitSign);//�ʽ����ʱ���
				filebuf.append(_dto.getSfundtypename()+splitSign);//�ʽ���������
				filebuf.append(_dto.getSpaytypecode()+splitSign);//֧����ʽ����
				filebuf.append(_dto.getSpaytypename()+splitSign);//֧����ʽ����
				filebuf.append(_dto.getSpaybankname()+splitSign);//������������
				filebuf.append(_dto.getSxpaysndbnkno()+splitSign);//֧���������к�
				filebuf.append(_dto.getSxpayamt()+splitSign);//����������
				filebuf.append(_dto.getShold1()+splitSign);//Ԥ���ֶ�1
				filebuf.append(_dto.getShold2()+splitSign);//Ԥ���ֶ�2
				filebuf.append(_dto.getSxcleardate()+splitSign);//��������
				filebuf.append(_dto.getSagentacctbankname()+splitSign);//ԭ�տ�����
				filebuf.append(_dto.getSclearacctbankname()+splitSign);//ԭ��������
				filebuf.append(_dto.getSremark()+splitSign);//ժҪ
				filebuf.append(_dto.getSmoneycorpcode()+splitSign);//���ڻ�������
				filebuf.append(_dto.getSadmdivcode()+splitSign);//������������
				filebuf.append(_dto.getSvtcode()+splitSign);//ƾ֤���ͱ��
				filebuf.append(_dto.getSbackflag()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSisreturn()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSbckreason()+splitSign);//֧�����ı��
				filebuf.append(_dto.getNbackmoney()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSrefundtype()+splitSign);//֧�����ı��
				filebuf.append(_dto.getShold3()+splitSign);//֧�����ı��
				filebuf.append(_dto.getShold4()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSext1()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSext2()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSext3()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSext4()+splitSign);//֧�����ı��
				filebuf.append(_dto.getSext5()+splitSign);//֧�����ı��
				filebuf.append("\r\n");
				TvPayreckBankBackListDto subpara = new TvPayreckBankBackListDto();
				subpara.setIvousrlno(_dto.getIvousrlno());
				List<TvPayreckBankBackListDto> sublist = CommonFacade.getODB().findRsByDto(subpara);
				for(TvPayreckBankBackListDto dto : sublist){
					filebuf.append(dto.getIvousrlno()+splitSign);//ƾ֤��ˮ��
					filebuf.append(dto.getIseqno()+splitSign);//���
					filebuf.append(dto.getSorivouno()+splitSign);//ԭƾ֤���
					filebuf.append(dto.getDorivoudate()+splitSign);//ԭƾ֤����
					filebuf.append(dto.getSbdgorgcode()+splitSign);//Ԥ�㵥λ����
					filebuf.append(dto.getSfuncbdgsbtcode()+splitSign);//�������Ŀ����
					filebuf.append(dto.getSecnomicsubjectcode()+splitSign);//�������Ŀ����
					filebuf.append(dto.getFamt()+splitSign);//���
					filebuf.append(dto.getSacctprop()+splitSign);//�˻�����
					filebuf.append(dto.getTsupdate()+splitSign);//����ʱ��
					filebuf.append(dto.getSsupdepname()+splitSign);//Ԥ�㵥λ����
					filebuf.append(dto.getSexpfuncname()+splitSign);//֧�����ܷ����Ŀ����
					filebuf.append(dto.getSpaysummaryname()+splitSign);//ժҪ����
					filebuf.append(dto.getShold1()+splitSign);//Ԥ���ֶ�1
					filebuf.append(dto.getShold2()+splitSign);//Ԥ���ֶ�2
					filebuf.append(dto.getShold3()+splitSign);//Ԥ���ֶ�3
					filebuf.append(dto.getShold4()+splitSign);//Ԥ���ֶ�4
					filebuf.append(dto.getSvoucherno()+splitSign);
					filebuf.append(dto.getSorivoudetailno()+splitSign);
					filebuf.append(dto.getSid()+splitSign);
					filebuf.append(dto.getSpaydate()+splitSign);
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * 
	 * @param splitSign
	 * @param path
	 * @param string
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void payreckbank(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvPayreckBankDto> list = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvPayreckBankDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (list != null && list.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ list.get(0).getStrecode() + "260hd.txt";
			path = path + filename;
			for (TvPayreckBankDto _dto : (List<TvPayreckBankDto>) list) {
				TvPayreckBankListDto subPara = new TvPayreckBankListDto();
				subPara.setIvousrlno(_dto.getIvousrlno());
				filebuf.append("**"+_dto.getSbookorgcode()+splitSign);//�����������
				filebuf.append(_dto.getDentrustdate()+splitSign);//ί������
				filebuf.append(_dto.getSpackno()+splitSign);//����ˮ��
				filebuf.append(_dto.getSagentbnkcode()+splitSign);//���������к�
				filebuf.append(_dto.getStrano()+splitSign);//������ˮ��
				filebuf.append(_dto.getIvousrlno()+splitSign);//ƾ֤��ˮ��
				filebuf.append(_dto.getSfinorgcode()+splitSign);//�������ش���
				filebuf.append(_dto.getStrecode()+splitSign);//�������
				filebuf.append(_dto.getSvouno()+splitSign);//ƾ֤���
				filebuf.append(_dto.getDvoudate()+splitSign);//ƾ֤����
				filebuf.append(_dto.getSpayeracct()+splitSign);//�������˺�
				filebuf.append(_dto.getSpayername()+splitSign);//����������
				filebuf.append(_dto.getSpayeraddr()+splitSign);//�����˵�ַ
				filebuf.append(_dto.getSpayeeacct()+splitSign);//�տ����˺�
				filebuf.append(_dto.getSpayeename()+splitSign);//�տ�������
				filebuf.append(_dto.getSpayeeaddr()+splitSign);//�տ��˵�ַ
				filebuf.append(_dto.getSpayeeopbkno()+splitSign);//�տ��˿������к�
				filebuf.append(_dto.getSaddword()+splitSign);//����
				filebuf.append(_dto.getSbudgettype()+splitSign);//Ԥ������
				filebuf.append(_dto.getStrimsign()+splitSign);//�����ڱ�־
				filebuf.append(_dto.getSofyear()+splitSign);//�������
				filebuf.append(_dto.getFamt()+splitSign);//���
				filebuf.append(_dto.getIstatinfnum()+splitSign);//��ϸ��Ϣ����
				filebuf.append(_dto.getDacctdate()+splitSign);//��������
				filebuf.append(_dto.getSresult()+splitSign);//������
				filebuf.append(_dto.getSdescription()+splitSign);//˵��
				filebuf.append(_dto.getSprocstat()+splitSign);//����״̬
				filebuf.append(_dto.getDacceptdate()+splitSign);//��������
				filebuf.append(_dto.getTsupdate()+splitSign);//����ʱ��
				filebuf.append(_dto.getSpaymode()+splitSign);//֧����ʽ
				filebuf.append(_dto.getSfilename()+splitSign);//�ļ���
				filebuf.append(_dto.getSid()+splitSign);//���뻮��ƾ֤Id
				filebuf.append(_dto.getSbgttypecode()+splitSign);//Ԥ�����ͱ���
				filebuf.append(_dto.getSbgttypename()+splitSign);//Ԥ����������
				filebuf.append(_dto.getSfundtypecode()+splitSign);//�ʽ����ʱ���
				filebuf.append(_dto.getSfundtypename()+splitSign);//�ʽ���������
				filebuf.append(_dto.getSpaytypecode()+splitSign);//֧����ʽ����
				filebuf.append(_dto.getSpaytypename()+splitSign);//֧����ʽ����
				filebuf.append(_dto.getSpaybankname()+splitSign);//������������
				filebuf.append(_dto.getSxpaysndbnkno()+splitSign);//֧���������к�
				filebuf.append(_dto.getSxpayamt()+splitSign);//����������
				filebuf.append(_dto.getShold1()+splitSign);//Ԥ���ֶ�1
				filebuf.append(_dto.getShold2()+splitSign);//Ԥ���ֶ�2
				filebuf.append(_dto.getSxcleardate()+splitSign);//��������
				filebuf.append(_dto.getSagentacctbankname()+splitSign);//�տ�����
				filebuf.append(_dto.getSclearacctbankname()+splitSign);//��������
				filebuf.append(_dto.getSmoneycorpcode()+splitSign);//���ڻ�������
				filebuf.append(_dto.getSadmdivcode()+splitSign);//������������
				filebuf.append(_dto.getSvtcode()+splitSign);//ƾ֤���ͱ��
				filebuf.append("\r\n");
				List<TvPayreckBankListDto> sublist = CommonFacade.getODB().findRsByDto(subPara);
				for(TvPayreckBankListDto dto : sublist){
					filebuf.append(dto.getIvousrlno()+splitSign);//ƾ֤��ˮ��
					filebuf.append(dto.getIseqno()+splitSign);//���
					filebuf.append(dto.getSbdgorgcode()+splitSign);//Ԥ�㵥λ����
					filebuf.append(dto.getSfuncbdgsbtcode()+splitSign);//�������Ŀ����
					filebuf.append(dto.getSecnomicsubjectcode()+splitSign);//�������Ŀ����
					filebuf.append(dto.getFamt()+splitSign);//���
					filebuf.append(dto.getSacctprop()+splitSign);//�˻�����
					filebuf.append(dto.getTsupdate()+splitSign);//����ʱ��
					filebuf.append(dto.getSsupdepname()+splitSign);//Ԥ�㵥λ����
					filebuf.append(dto.getSexpfuncname()+splitSign);//֧�����ܷ����Ŀ����
					filebuf.append(dto.getSpaysummaryname()+splitSign);//ժҪ����
					filebuf.append(dto.getShold1()+splitSign);//Ԥ���ֶ�1
					filebuf.append(dto.getShold2()+splitSign);//Ԥ���ֶ�2
					filebuf.append(dto.getShold3()+splitSign);//Ԥ���ֶ�3
					filebuf.append(dto.getShold4()+splitSign);//Ԥ���ֶ�4
					filebuf.append(dto.getSid()+splitSign);
					filebuf.append(dto.getSpaydate()+splitSign);
					filebuf.append(dto.getSvouchern0()+splitSign);//֧��ƾ֤����
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * ʵ���ʽ����ݵ���
	 * 
	 * @param splitSign
	 * @param path
	 * @param sql
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void payout(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvPayoutmsgmainDto> list = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvPayoutmsgmainDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (list != null && list.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ list.get(0).getStrecode() + "170hd.txt";
			path = path + filename;
			for (TvPayoutmsgmainDto _dto : (List<TvPayoutmsgmainDto>) list) {
				TvPayoutmsgsubDto subPara = new TvPayoutmsgsubDto();
				subPara.setSbizno(_dto.getSbizno());
				List<TvPayoutmsgsubDto> sublist = CommonFacade.getODB()
						.findRsByDtoWithUR(subPara);

				filebuf.append("**"+_dto.getSbizno());// ������ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorgcode());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getScommitdate());// ί������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaccdate());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfilename());// �����ļ���
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrecode());// �����������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageno());// ����ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayunit());// ��Ʊ��λ
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeebankno());// ת������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdealno());// ������ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getStaxticketno());// ˰Ʊ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSgenticketdate());// ��Ʊ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeracct());// �������ʺ�
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayername());// ����������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeraddr());// ����������
				filebuf.append(splitSign);
				filebuf.append(_dto.getNmoney());// ���׽��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecbankno());// �տ��˿���������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecacct());// �տ����ʺ�
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecname());// �տ�������
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrimflag());// �����ڱ�־
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgetunitcode());// Ԥ�㵥λ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSunitcodename());// Ԥ�㵥λ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofyear());// �������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgettype());// Ԥ������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSstatus());// ����״̬
				filebuf.append(splitSign);
				filebuf.append(_dto.getSusercode());// ����Ա����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaddword());// ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorivouno());// ԭ֧��ƾ֤���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorivoudate());// ԭ֧��ƾ֤����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbackflag());// �˻ر�־
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo());// ��ע
				filebuf.append(splitSign);
				filebuf.append(_dto.getTssysupdate());// ϵͳ����ʱ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSid());// ʵ������ƾ֤Id
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecbankname());// �տ���������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayerbankname());// ��������������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypecode());// �ʽ����ʱ���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypename());// �ʽ���������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaytypecode());// ֧����ʽ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaytypename());// ֧����ʽ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankcode());// �������б���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankname());// ������������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaysummarycode());// ��;����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaysummaryname());// ��;����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxagentbusinessno());// ���н�����ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxpayamt());// ʵ��֧�����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxpaydate());// ֧������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSifmatch());// �Ƿ���Ҫ��¼�к�(0--����¼��1--��¼)
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold1());// Ԥ���ֶ�1
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold2());// Ԥ���ֶ�2
				filebuf.append(splitSign);
				filebuf.append(_dto.getSinputrecbankno());// ��¼���к�
				filebuf.append(splitSign);
				filebuf.append(_dto.getSinputusercode());// ��¼��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSchecksercode());// ������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSinputrecbankname());// ��¼����
				filebuf.append(splitSign);
				filebuf.append(_dto.getScheckstatus());// ��¼����״̬
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbusinesstypecode());
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbusinesstypename());
				filebuf.append(splitSign);

				if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
						.getSstatus()))
					filebuf.append("1");// ������1�ɹ���0ʧ��
				else
					filebuf.append("0");// ������1�ɹ���0ʧ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo() + splitSign);// ԭ��
				if ("1".equals(_dto.getSbackflag())) {
					filebuf.append("ʵ���ʽ��˿�" + splitSign);
					filebuf.append(_dto.getShold2());
				} else {
					filebuf.append("ʵ���ʽ�");
				}
				filebuf.append("\r\n");
				for (TvPayoutmsgsubDto subdto : sublist) {
					filebuf.append(subdto.getSbizno());// ������ˮ��
					filebuf.append(splitSign);
					filebuf.append(subdto.getSseqno());//
					filebuf.append(splitSign);
					filebuf.append(subdto.getSaccdate());// ��������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSecnomicsubjectcode());// ���ÿ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSbudgetprjcode());// Ԥ����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getNmoney());// ���׽��
					filebuf.append(splitSign);
					filebuf.append(subdto.getSfunsubjectcode());// ���ܿ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getTssysupdate());// ϵͳ����ʱ��
					filebuf.append(splitSign);
					filebuf.append(subdto.getStaxticketno());// ����ƾ֤Id
					filebuf.append(splitSign);
					filebuf.append(subdto.getSbgttypecode());// Ԥ�����ͱ���
					filebuf.append(splitSign);
					filebuf.append(subdto.getSbgttypename());// Ԥ����������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSprocatcode());// ��֧�������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSprocatname());// ��֧��������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSagencycode());// Ԥ�㵥λ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSagencyname());// Ԥ�㵥λ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname());// ֧�����ܷ����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfunccode1());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname1());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfunccode2());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname2());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfunccode3());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname3());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpecocode());// ֧�����÷����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpeconame());// ֧�����÷����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpecocode1());// ֧�����÷����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpeconame1());// ֧�����÷����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpecocode2());// ֧�����÷����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpeconame2());// ֧�����÷����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold1());// Ԥ���ֶ�1
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold2());// Ԥ���ֶ�2
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold3());// Ԥ���ֶ�3
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold4());// Ԥ���ֶ�4
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpayeeacctno());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpayeeacctname());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpayeeacctbankname());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpaysummarycode());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpaysummaryname());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxpayamt());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxpaydate());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxagentbusinessno());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxaddword());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSid());
					filebuf.append(splitSign);
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * ��Ȩ֧��������ݵ���
	 * 
	 * @param splitSign
	 * @param path
	 * @param sql
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void grantPlan(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvGrantpaymsgmainDto> listgrant = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvGrantpaymsgmainDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (listgrant != null && listgrant.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ listgrant.get(0).getStrecode() + "020hd.txt";
			path = path + filename;
			for (TvGrantpaymsgmainDto _dto : (List<TvGrantpaymsgmainDto>) listgrant) {
				TvGrantpaymsgsubDto subPara = new TvGrantpaymsgsubDto();
				subPara.setSorgcode(_dto.getSorgcode());
				subPara.setSlimitid(_dto.getSlimitid());
				subPara.setSofyear(_dto.getSofyear());
				subPara.setSfilename(_dto.getSfilename());
				subPara.setSpackageno(_dto.getSpackageno());
				List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
				filebuf.append("**"+_dto.getIvousrlno());// ƾ֤��ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorgcode());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSlimitid());// ���κ�
				filebuf.append(splitSign);
				filebuf.append(_dto.getScommitdate());// ί������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaccdate());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfilename());// �����ļ���
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrecode());// �����������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageno());// ����ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayunit());// ��Ʊ��λ
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransbankcode());// ת������
				filebuf.append(splitSign);
				filebuf.append(_dto.getNmoney());// ���׽��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageticketno());// �ְ�˰Ʊ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSgenticketdate());// ��Ʊ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofyear());// �������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofmonth());// �����·�
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransactunit());// ���쵥λ
				filebuf.append(splitSign);
				filebuf.append(_dto.getSamttype());// �������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSusercode());// ����Ա����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSstatus());// ����״̬
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo());// ��ע
				filebuf.append(splitSign);
				filebuf.append(_dto.getTssysupdate());// ϵͳ����ʱ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdealno());// ������ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgetunitcode());// Ԥ���ڵ�λ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgettype());// Ԥ������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSid());// ������֪ͨ��Id
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdeptnum());// һ��Ԥ�㵥λ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypecode());// �ʽ����ʱ���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypename());// �ʽ���������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankcode());// �������б���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankname());// ������������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankcode());// �������б���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankname());// ������������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankno());// ���������к�
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxacctdate());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold1());// Ԥ���ֶ�1
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold2());// Ԥ���ֶ�2
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearvoucherno());
				filebuf.append(splitSign);
				filebuf.append(_dto.getNxallamt());
				filebuf.append(splitSign);
				if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
						.getSstatus()))
					filebuf.append("1");// ������1�ɹ���0ʧ��
				else
					filebuf.append("0");// ������1�ɹ���0ʧ��
				filebuf.append(splitSign);
				if ("80000".equals(_dto.getSstatus()))
					filebuf.append("�ɹ�");// ˵��
				else if ("80001".equals(_dto.getSstatus()))
					filebuf.append("ʧ��");// ˵��
				else if ("80002".equals(_dto.getSstatus()))
					filebuf.append("������");// ˵��
				else if ("80008".equals(_dto.getSstatus()))
					filebuf.append("δ����");// ˵��
				else if ("80009".equals(_dto.getSstatus()))
					filebuf.append("������");// ˵��
				else
					filebuf.append(_dto.getSdemo());// ˵��
				filebuf.append("\r\n");
				// �ӱ���Ϣ
				for (TvGrantpaymsgsubDto sub : (List<TvGrantpaymsgsubDto>) sublist) {
					filebuf.append(sub.getIvousrlno());// ƾ֤��ˮ��
					filebuf.append(splitSign);
					filebuf.append(sub.getIdetailseqno());// ��ϸ���
					filebuf.append(splitSign);
					filebuf.append(sub.getSorgcode());// ��������
					filebuf.append(splitSign);
					filebuf.append(sub.getSfilename());// �����ļ���
					filebuf.append(splitSign);
					filebuf.append(sub.getSlimitid());// ���κ�
					filebuf.append(splitSign);
					filebuf.append(sub.getSofyear());// �������
					filebuf.append(splitSign);
					filebuf.append(sub.getSline());// ��ϸ��
					filebuf.append(splitSign);
					filebuf.append(sub.getSpackageno());// ����ˮ��
					filebuf.append(splitSign);
					filebuf.append(sub.getSdealno());// ������ˮ��
					filebuf.append(splitSign);
					filebuf.append(sub.getSpackageticketno());// �ְ�˰Ʊ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSbudgetunitcode());// Ԥ���ڵ�λ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSbudgettype());// Ԥ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSfunsubjectcode());// ���ܿ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSecosubjectcode());// ���ÿ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(sub.getNmoney());// ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSaccattrib());// �ʻ�����
					filebuf.append(splitSign);
					filebuf.append(sub.getSaccdate());// ��������
					filebuf.append(splitSign);
					filebuf.append(sub.getSstatus());// ����״̬
					filebuf.append(splitSign);
					filebuf.append(sub.getSusercode());// ����Ա����
					filebuf.append(splitSign);
					filebuf.append(sub.getTssysupdate());// ϵͳ����ʱ��
					filebuf.append(splitSign);
					filebuf.append(sub.getSdemo());// ��ע
					filebuf.append(splitSign);
					filebuf.append(sub.getSsupdepname());// һ��Ԥ�㵥λ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname());// ֧�����ܷ����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode1());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname1());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode2());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname2());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode3());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname3());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSprocatcode());// ��֧�������
					filebuf.append(splitSign);
					filebuf.append(sub.getShold1());// Ԥ���ֶ�1
					filebuf.append(splitSign);
					filebuf.append(sub.getShold2());// Ԥ���ֶ�2
					filebuf.append(splitSign);
					filebuf.append(sub.getShold3());// Ԥ���ֶ�3
					filebuf.append(splitSign);
					filebuf.append(sub.getShold4());// Ԥ���ֶ�4
					filebuf.append(splitSign);
					filebuf.append(sub.getSid());
					filebuf.append(splitSign);
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * ֱ��֧��������ݵ���
	 * 
	 * @param splitSign
	 * @param path
	 * @param sql
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void directPlan(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvDirectpaymsgmainDto> listdirect = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvDirectpaymsgmainDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (listdirect != null && listdirect.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ listdirect.get(0).getStrecode() + "010hd.txt";
			path = path + filename;
			for (int i = 0; i < listdirect.size(); i++) {
				TvDirectpaymsgmainDto _dto = listdirect.get(i);
				TvDirectpaymsgsubDto subPara = new TvDirectpaymsgsubDto();
				subPara.setSorgcode(_dto.getSorgcode());
				subPara.setSofyear(_dto.getSofyear());
				subPara.setStaxticketno(_dto.getStaxticketno());
				subPara.setSpackageno(_dto.getSpackageno());
				List<TvDirectpaymsgsubDto> sublist = CommonFacade.getODB()
						.findRsByDtoWithUR(subPara);
				filebuf.append("**"+_dto.getIvousrlno());// ƾ֤��ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorgcode());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getScommitdate());// ί������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaccdate());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfilename());// �����ļ���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageno());// ����ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrecode());// �����������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayunit());// ��Ʊ��λ
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransbankcode());// ת������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdealno());// ������ˮ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getNmoney());// ���׽��
				filebuf.append(splitSign);
				filebuf.append(_dto.getStaxticketno());// ˰Ʊ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageticketno());// �ְ�˰Ʊ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSgenticketdate());// ��Ʊ����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgettype());// Ԥ������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofyear());// �������
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransactunit());// ���쵥λ
				filebuf.append(splitSign);
				filebuf.append(_dto.getSamttype());// �������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSstatus());// ����״̬
				filebuf.append(splitSign);
				filebuf.append(_dto.getSusercode());// ����Ա����
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo());// ��ע
				filebuf.append(splitSign);
				filebuf.append(_dto.getTssysupdate());// ϵͳ����ʱ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSid());// ֱ��֧������������֪ͨ��
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypecode());// �ʽ����ʱ���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypename());// �ʽ���������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeeacctno());// �տ����˺�
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeeacctname());// �տ�������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeeacctbankname());// �տ�����������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayacctno());// �������˺�
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayacctname());// ����������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayacctbankname());// ��������������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankcode());// �������б���
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankname());// ������������
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankno());// ���������к�
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxacctdate());// ��������
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold1());// Ԥ���ֶ�1
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold2());// Ԥ���ֶ�2
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayvoucherno());
				filebuf.append(splitSign);
				filebuf.append(_dto.getNxallamt());
				filebuf.append(splitSign);

				if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
						.getSstatus()))
					filebuf.append("1");// ������1�ɹ���0ʧ��
				else
					filebuf.append("0");// ������1�ɹ���0ʧ��
				filebuf.append(splitSign);
				if ("80000".equals(_dto.getSstatus()))
					filebuf.append("�ɹ�");// ˵��
				else if ("80001".equals(_dto.getSstatus()))
					filebuf.append("ʧ��");// ˵��
				else if ("80002".equals(_dto.getSstatus()))
					filebuf.append("������");// ˵��
				else if ("80008".equals(_dto.getSstatus()))
					filebuf.append("δ����");// ˵��
				else if ("80009".equals(_dto.getSstatus()))
					filebuf.append("������");// ˵��
				else
					filebuf.append(_dto.getSdemo());// ˵��
				filebuf.append("\r\n");
				// �ӱ���Ϣ
				for (TvDirectpaymsgsubDto sub : (List<TvDirectpaymsgsubDto>) sublist) {
					// filebuf.append(sub.getSfunsubjectcode());//���ܿ�Ŀ����
					// filebuf.append(splitSign);
					// filebuf.append(sub.getSecosubjectcode());//���ÿ�Ŀ����
					// filebuf.append(splitSign);
					// filebuf.append(sub.getSbudgetunitcode());//Ԥ�㵥λ����
					// filebuf.append(splitSign);
					// filebuf.append(sub.getNmoney());//������
					// filebuf.append(splitSign);
					filebuf.append(sub.getIvousrlno());// ƾ֤��ˮ��
					filebuf.append(splitSign);
					filebuf.append(sub.getIdetailseqno());// ��ϸ���
					filebuf.append(splitSign);
					filebuf.append(sub.getSorgcode());// ��������
					filebuf.append(splitSign);
					filebuf.append(sub.getSpackageno());// ����ˮ��
					filebuf.append(splitSign);
					filebuf.append(sub.getSbudgetunitcode());// Ԥ���ڵ�λ����
					filebuf.append(splitSign);
					filebuf.append(sub.getStaxticketno());// ˰Ʊ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSline());// ֧����ϸ
					filebuf.append(splitSign);
					filebuf.append(sub.getSofyear());// �������
					filebuf.append(splitSign);
					filebuf.append(sub.getInoafterpackage());// �ְ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSfunsubjectcode());// ���ܿ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSecosubjectcode());// ���ÿ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(sub.getNmoney());// ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSaccdate());// ��������
					filebuf.append(splitSign);
					filebuf.append(sub.getSusercode());// ����Ա����
					filebuf.append(splitSign);
					filebuf.append(sub.getSdemo());// ��ע
					filebuf.append(splitSign);
					filebuf.append(sub.getTssysupdate());// ϵͳ����ʱ��
					filebuf.append(splitSign);
					filebuf.append(sub.getSagencycode());// Ԥ�㵥λ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSagencyname());// Ԥ�㵥λ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname());// ֧�����ܷ����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode1());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname1());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode2());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname2());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode3());// ֧�����ܷ����Ŀ�����
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname3());// ֧�����ܷ����Ŀ������
					filebuf.append(splitSign);
					filebuf.append(sub.getSprocatcode());// ��֧�������
					filebuf.append(splitSign);
					filebuf.append(sub.getSprocatname());// ��֧��������
					filebuf.append(splitSign);
					filebuf.append(sub.getShold1());// Ԥ���ֶ�1
					filebuf.append(splitSign);
					filebuf.append(sub.getShold2());// Ԥ���ֶ�2
					filebuf.append(splitSign);
					filebuf.append(sub.getShold3());// Ԥ���ֶ�3
					filebuf.append(splitSign);
					filebuf.append(sub.getShold4());// Ԥ���ֶ�4
					filebuf.append(splitSign);
					filebuf.append(sub.getSid());//
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}
	
	public static void main(String[] a)
	{
		System.out.println(count++);
		System.out.println(count++);
		System.out.println(count++);
		System.out.println(count++);
		System.out.println(count++);
		
	}
}
