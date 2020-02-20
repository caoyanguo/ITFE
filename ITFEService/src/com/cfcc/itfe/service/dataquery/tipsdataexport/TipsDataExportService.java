package com.cfcc.itfe.service.dataquery.tipsdataexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author hua
 * @time 12-05-16 18:43:18 codecomment:
 */

public class TipsDataExportService extends AbstractTipsDataExportService {
	private static Log log = LogFactory.getLog(TipsDataExportService.class);

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
	public Map generateTipsToFile(List list, IDto dto, String str)
			throws ITFEBizException {
		List<String> errorlist = new ArrayList<String>();
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		String shellpath = root+"tipsexport.sql";
		String abTipsRoot = root+"tipstemp/";
		List<String> filelist = new ArrayList<String>();
		TipsParamDto paramdto = null;
		Map<String, List<String>> resmap = new HashMap<String, List<String>>();
		if (null != dto) {
			paramdto = (TipsParamDto) dto; // �õ�ǰ̨�����Ĳ�ѯ����
			if("sendreport".equals(paramdto.getSbankcode()))
			{
				byte[] bytes = null;
				String results = null;
				String commad = "sh /itfe/script/timer/exportbyparam "+paramdto.getStartdate().toString().replace("-", "");
				try {
					bytes = CallShellUtil.callShellWithRes(commad);
				} catch (Exception e) {
					throw new ITFEBizException("���·��ͱ���ʧ��:",e);
				}
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				log.error("����ʱ���񵼳������ļ�����SHELL���:" + results);	
				return null;
			}
		}
		SQLExecutor sqlExec = null;
		boolean boo = false;
		StringBuffer expBuffer = new StringBuffer("");
		expBuffer.append(PublicSearchFacade.getSqlConnectByProp()); //���ȸ��ű�������������ݿ�����		
		try {
			//�õ�������־�������ձ��¼���������жϷ�����ʱ��ѯ��������ʹ��
			TsConvertfinorgDto finddto = new TsConvertfinorgDto();
			List<TsConvertfinorgDto> finddtolist = null;
			finddto.setSorgcode(getLoginInfo().getSorgcode()); // �����������
			finddtolist = (List<TsConvertfinorgDto>)CommonFacade.getODB().findRsByDtoWithUR(finddto);
			for (Object obj : list) { // ѭ����Ҫ��������������
				TdEnumvalueDto typedto = (TdEnumvalueDto) obj;
				if (StateConstant.RecvTips_3128_SR.equals(typedto.getSremark())) { // 3128�����ձ���
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_rptdate from HTR_INCOMEDAYRPT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_INCOMEDAYRPT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {
						errorlist.add("3128�������������ձ����ѯ������,δ����");
						continue;
					}else {
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3128_SR, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
						
					}
				} else if (StateConstant.RecvTips_3128_KC.equals(typedto.getSremark())) { // 3128����ձ���
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_rptdate from HTR_STOCKDAYRPT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_STOCKDAYRPT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3128�����������ձ����ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3128_KC, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				} else if (StateConstant.RecvTips_3129.equals(typedto.getSremark())) { // 3129����˰Ʊ
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_applydate from HTV_FIN_INCOMEONLINE where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_APPLYDATE >= ? and S_APPLYDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_applydate from TV_FIN_INCOMEONLINE where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_APPLYDATE >= ? and S_APPLYDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3129�����������˰Ʊ��ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3129, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				} else if (StateConstant.RecvTips_3139.equals(typedto.getSremark())) { // 3139�����ˮ
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_intredate from HTV_FIN_INCOME_DETAIL where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_INTREDATE >= ? and S_INTREDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_intredate from TV_FIN_INCOME_DETAIL where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_INTREDATE >= ? and S_INTREDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3139�������������ˮ��ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3139, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				}else if(StateConstant.RecvTips_3127.equals(typedto.getSremark()))
				{
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='1' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='1' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3127һ��Ԥ��֧�������ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"ybyszc", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='2' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='2' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3127ʵ���ʽ�֧�������ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"sbzjzc", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='3' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='3' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3127����Ԥ��֧�������ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"dbyszc", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='4' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='4' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3127ֱ��֧�������ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"zjzf", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='5' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='5' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//���û�����ݣ���������ȥ
						errorlist.add("3127��Ȩ֧�������ѯ������,δ����");
						continue;
					}else { //��������������ɽű�
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"sqzf", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				}
			}
			if(filelist.size() > 0){
				FileUtil.getInstance().writeFile(shellpath, expBuffer.toString());
				byte[] bytes = null;
				String results = null;
				bytes = DB2CallShell.dbCallShellWithRes(shellpath);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				log.debug("����SHELL���:" + results);
			}			
			resmap.put("error", errorlist);
			/*�������ļ������ֶ�����*/
			resmap.put("files", this.process3129CSV(filelist, paramdto));
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("�����ļ��쳣!\n"+e.getMessage(),e);			
		} finally{
			if(sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
		return resmap;
	}
	
	/**
	 * �����ڲ���ȫ�����֮��ɾ�����������ļ�
	 */
	public void deleteTheFiles(List filelist) throws ITFEBizException {		
		try {
			File dir = new File(ITFECommonConstant.FILE_ROOT_PATH+"tipstemp");
			if(dir.exists()) {
				FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH+"tipstemp"); //ɾ��Ŀ¼
			}
			File sqlfile = new File(ITFECommonConstant.FILE_ROOT_PATH+"tipsexport.sql");
			if(sqlfile.exists()) {
				FileUtil.getInstance().deleteFile(ITFECommonConstant.FILE_ROOT_PATH+"tipsexport.sql"); //ɾ���ű�
			}			
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}
	}
	/**
	 * ������������linux��shell�ű�
	 * @throws FileOperateException 
	 */
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		public Map<String, List<String>> createShellForExp(List<String> ls ,TipsParamDto paramdto,String paramsql ,String paramsql2,String paramsql3,String type ,Date sdate,Date edate) throws FileOperateException {
		Map<String, List<String>> rtmap = new HashMap<String, List<String>>(); //��Žű���Ϣ
		List<String> filelist = new ArrayList<String>(); //����ļ��б�
		//�ļ�������·��,Ŀǰ��
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		//��ʼ���ںͽ��������������
		int count = this.daysOfTwo(sdate, edate);
		String shellpath = root+"tipsexport.sql";
		StringBuffer expcontent =new StringBuffer("");
		String abTipsRoot = root+"tipstemp/";
		String sr_3128 = abTipsRoot+StateConstant.sr_3128;
		String kc_3128 = abTipsRoot+StateConstant.kc_3128;
		String on_3129 = abTipsRoot+StateConstant.on_3129;
		String in_3139 = abTipsRoot+StateConstant.in_3139;
		String in_3127 = abTipsRoot+StateConstant.in_3127;
		/*
		 * ��ǰ�����ļ�Ŀ¼������LINUX�ᱨ��
		 */
		FileUtil.getInstance().createDir(sr_3128);
		FileUtil.getInstance().createDir(kc_3128);
		FileUtil.getInstance().createDir(on_3129);
		FileUtil.getInstance().createDir(in_3139);
		FileUtil.getInstance().createDir(in_3127);
		//���ڵõ�ĳ������һ��
		String time = TimeFacade.getCurrentStringTime("yyyyMMddHHmmss");
//
		for(int i = 1 ; i <= ls.size() ; i++) {
			String fileEnd = time+"_��������-"+ls.get(i-1)+"_"+i+".csv"; //�ļ����󲿷֣���ȡ�����������޸�
			if (StateConstant.RecvTips_3128_SR.equals(type)) { // 3128�����ձ���
				String fname = getLoginInfo().getSorgcode()+"_3128�����ձ���_"+fileEnd;
				expcontent.append("export to "+sr_3128+fname+" of del select * from HTR_INCOMEDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"' union select * from TR_INCOMEDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"';\n");
				filelist.add(sr_3128+fname);
			} else if (StateConstant.RecvTips_3128_KC.equals(type)) { // 3128����ձ���
				String fname = getLoginInfo().getSorgcode()+"_3128����ձ���_"+fileEnd;
				expcontent.append("export to "+kc_3128+fname+" of del select * from HTR_STOCKDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"' union select * from TR_STOCKDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"';\n");
				filelist.add(kc_3128+fname);
			} else if (StateConstant.RecvTips_3129.equals(type)) { // 3129����˰Ʊ
				String fname = getLoginInfo().getSorgcode()+"_3129����˰Ʊ��Ϣ_"+fileEnd;
				if(null != paramdto && null != paramdto.getExptype() && !"".equals(paramdto.getExptype())) {
					if("0".equals(paramdto.getExptype())) { //���ÿ���ʽ
						expcontent.append("export to "+on_3129+fname+" of del select * from HTV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"' union select * from TV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"';\n");
						filelist.add(on_3129+fname);
					}
				}else { //δѡ��Ĭ�ϲ��ÿ���ʽ
					expcontent.append("export to "+on_3129+fname+" of del select * from HTV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"' union select * from TV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"';\n");
					filelist.add(on_3129+fname);
				}			
				
			} else if (StateConstant.RecvTips_3139.equals(type)) { // 3139�����ˮ
				String fname = getLoginInfo().getSorgcode()+"_3139�����ˮ��Ϣ_"+fileEnd;
				expcontent.append("export to "+in_3139+fname+" of del select * from HTV_FIN_INCOME_DETAIL where "+paramsql2+" and S_INTREDATE='"+ls.get(i-1)+"' union select * from TV_FIN_INCOME_DETAIL where "+paramsql2+" and S_INTREDATE='"+ls.get(i-1)+"';\n");
				filelist.add(in_3139+fname);
			}else if((StateConstant.RecvTips_3127+"ybyszc").equals(type))//Ԥ��֧������
			{
				String fname = getLoginInfo().getSorgcode()+"_3127һ��Ԥ��֧��������Ϣ_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='1' and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='1' and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"sbzjzc").equals(type))//Ԥ��֧������
			{
				String fname = getLoginInfo().getSorgcode()+"_3127ʵ���ʽ�֧��������Ϣ_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='2' and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='2' and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"dbyszc").equals(type))//Ԥ��֧������
			{
				String fname = getLoginInfo().getSorgcode()+"_3127����Ԥ��֧��������Ϣ_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='3'  and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='3'  and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"zjzf").equals(type))//Ԥ��֧������
			{
				String fname = getLoginInfo().getSorgcode()+"_3127ֱ��֧��������Ϣ_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='4'  and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='4'  and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"sqzf").equals(type))//Ԥ��֧������
			{
				String fname = getLoginInfo().getSorgcode()+"_3127��Ȩ֧��������Ϣ_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='5'  and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='5'  and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}
		}
		//�޸ĳɸ���ͳ�Ƹ�ʽ��������
		if (StateConstant.RecvTips_3129.equals(type) && null != paramdto.getExptype() && "1".equals(paramdto.getExptype())) { //���ø���ͳ�Ƹ�ʽ
			String f_name = getLoginInfo().getSorgcode()+"_3129����˰Ʊ��Ϣ_"+time+"_TEMP.csv";
			expcontent.append("export to "+on_3129+f_name+" of del select substr(S_TAXORGCODE,1,1),S_TAXPAYCODE,S_TAXPAYNAME,'','',C_BDGLEVEL,S_BDGSBTCODE,S_TAXTYPECODE,S_ASTFLAG,F_TRAAMT from HTV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE >= '"+paramdto.getStartdate().toString().replaceAll("-", "")+"' and S_APPLYDATE <='"+paramdto.getEnddate().toString().replaceAll("-", "")+"' union select substr(S_TAXORGCODE,1,1),S_TAXPAYCODE,S_TAXPAYNAME,'','',C_BDGLEVEL,S_BDGSBTCODE,S_TAXTYPECODE,S_ASTFLAG,F_TRAAMT from TV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE >= '"+paramdto.getStartdate().toString().replaceAll("-", "")+"' and S_APPLYDATE <='"+paramdto.getEnddate().toString().replaceAll("-", "")+"';\n");
			filelist.add(on_3129+f_name);
		}
		List<String> l = new ArrayList<String>();
		l.add(expcontent.toString());
		rtmap.put("shell", l);
		rtmap.put("files", filelist);
		return rtmap;				
	}
	
	/**
	 * ����DTO����sql��ѯ����
	 * @param dto
	 * @param type
	 * @param finlist
	 */
	public String createParamStr(TipsParamDto dto ,String type,List<TsConvertfinorgDto> finlist) {
		String trecode = dto.getStrecode();
		String trestr = "";
		int point = 0;
		if(null != dto.getIfsub() && "1".equals(dto.getIfsub()) && null != trecode && !"".equals(trecode)) { //���ڰ����¼������⴦��					
			for(int i = trecode.length() ;i > 0 ; i--) {
				char c = trecode.charAt(i-1);
				if(c != '0') {
					trestr = trecode.substring(0, i);
					point = i;
					break;
				}
			}
		}
		StringBuffer sb = new StringBuffer("");
		sb.append(" 1=1 ");
		if(StateConstant.RecvTips_3129.equals(type)&&!(this.getLoginInfo().getSorgcode().startsWith("20")||this.getLoginInfo().getSorgcode().startsWith("24"))) { //����˰ƱĬ��ˢѡ״̬
			sb.append(" and S_TRASTATE in ("+StateConstant.STATE_OF_3129+") ");
		}
		//�����Ļ�������ѯ������������
		if (!getLoginInfo().getSorgcode().equals(StateConstant.ORG_CENTER_CODE)&&!(this.getLoginInfo().getSorgcode().startsWith("20")||this.getLoginInfo().getSorgcode().startsWith("24"))) {
			if((null == dto.getSorgcode() || "".equals(dto.getSorgcode())) && (null == dto.getStrecode() || "".equals(dto.getStrecode()))) {
				//�������Ͳ�������ͬʱΪ�գ����ѯ�������������й���
				sb.append(" and ( S_TRECODE='"+finlist.get(0).getStrecode()+"' ");
				for(int i = 1 ;i<finlist.size();i++){
					sb.append(" or S_TRECODE= '"+finlist.get(i).getStrecode()+"' ");
				}
				sb.append(" ) ");
			}			
		}		
		if(null != dto.getSorgcode() && !"".equals(dto.getSorgcode())) { //������������
			if(StateConstant.RecvTips_3128_SR.equals(type)) {
				sb.append(" and S_FINORGCODE='"+dto.getSorgcode()+"' ");
			}else {
				sb.append(" and S_ORGCODE='"+dto.getSorgcode()+"' ");
			}			
		}
		if(null != dto.getStaxorgcode() && !"".equals(dto.getStaxorgcode())) { //���ջ��ش���
			if(!StateConstant.RecvTips_3128_KC.equals(type)) {
				sb.append(" and S_TAXORGCODE='"+dto.getStaxorgcode()+"' ");
			}
		}
		if(null != dto.getStrecode() && !"".equals(dto.getStrecode())) { //�������
			if(this.getLoginInfo().getSorgcode().startsWith("20")||this.getLoginInfo().getSorgcode().startsWith("24"))
			{
				List<TsTreasuryDto> treasuryList =null;//��ѯ�����б�
				try
				{
					if("0".equals(dto.getIfsub()))
					{
						TsTreasuryDto findto = new TsTreasuryDto();
						findto.setSorgcode(this.getLoginInfo().getSorgcode());
						findto.setStrecode(dto.getStrecode());
						treasuryList = CommonFacade.getODB().findRsByDto(findto);
					}else if("1".equals(dto.getIfsub()))
					{
						TsTreasuryDto findto = new TsTreasuryDto();
						TsTreasuryDto resultdto = null;
						findto.setSorgcode(this.getLoginInfo().getSorgcode());
						findto.setStrecode(dto.getStrecode());
						List<TsTreasuryDto> resultList = CommonFacade.getODB().findRsByDto(findto);
						resultdto = resultList!=null&&resultList.size()==1?resultList.get(0):null;
						if(resultdto!=null)
							treasuryList = PublicSearchFacade.getSubTreCode(resultdto);
						else
							treasuryList = PublicSearchFacade.getSubTreCode(this.getLoginInfo().getSorgcode());
					}else
						sb.append(" and S_TRECODE='"+dto.getStrecode()+"' ");
				}catch (Exception e) {
					log.error(e);			
				}
				if(treasuryList!=null&&treasuryList.size()>0)
				{
					for(int i=0;i<treasuryList.size();i++)
					{
						if(i==0)
							sb.append(" and S_TRECODE in('"+treasuryList.get(i).getStrecode()+"'");
						else
							sb.append(",'"+treasuryList.get(i).getStrecode()+"'");
					}
					sb.append(") ");
				}
			}else
			{
				if(null == dto.getIfsub() || "".equals(dto.getIfsub()) || "0".equals(dto.getIfsub())) { //�������¼�
					sb.append(" and S_TRECODE='"+dto.getStrecode()+"' ");
				}else {
					sb.append(" and substr(S_TRECODE,1,"+point+")='"+trestr+"' ");
				}
			}
		}else
		{
			List<TsTreasuryDto> treasuryList =null;//��ѯ�����б�
			try
			{
				if("0".equals(dto.getIfsub()))
				{
					TsTreasuryDto findto = new TsTreasuryDto();
					findto.setSorgcode(this.getLoginInfo().getSorgcode());
					treasuryList = CommonFacade.getODB().findRsByDto(findto);
				}else if("1".equals(dto.getIfsub()))
				{
					treasuryList = PublicSearchFacade.getSubTreCode(this.getLoginInfo().getSorgcode());
				}
			}catch (Exception e) {
				log.error(e);			
			}
			if(treasuryList!=null&&treasuryList.size()>0)
			{
				for(int i=0;i<treasuryList.size();i++)
				{
					if(i==0)
						sb.append(" and S_TRECODE in('"+treasuryList.get(i).getStrecode()+"'");
					else
						sb.append(",'"+treasuryList.get(i).getStrecode()+"'");
				}
				sb.append(") ");
			}
		}
		if(null != dto.getSbeflag() && !"".equals(dto.getSbeflag())) {  //Ͻ����־
			if(StateConstant.RecvTips_3128_SR.equals(type)) {
				sb.append(" and S_BELONGFLAG='"+dto.getSbeflag()+"' ");
			}			
		}		
		return sb.toString();
	}
	
	/**
	 * �����ɵ�3129�ļ�ת���ɸ���Ҫ���ʽ,���������ļ������ֶ�����
	 * @param flist
	 * @param tipsdto
	 * @throws FileOperateException 
	 * @throws FileNotFoundException 
	 */
	public List<String> process3129CSV(List<String> flist,TipsParamDto tipsdto) throws FileOperateException, FileNotFoundException {
		List<String> filel = new ArrayList<String>();
		for(String file : flist) {
			if(new File(file).exists()) {
				if(file.indexOf(StateConstant.sr_3128) != -1) { //����3128sr���ַ���
					String newContent = StateConstant.RecvTips_3128_SR_ColName+"\r\n"+FileUtil.getInstance().readFile(file); //���ɼ������Ƶ��ļ�����
					FileUtil.getInstance().deleteFile(file); //ɾ��ԭ�ļ�
					FileUtil.getInstance().writeFile(file, newContent); //�������ļ�
					filel.add(file);
				}else if(file.indexOf(StateConstant.kc_3128) != -1) { //����3128kc���ַ���
					String newContent = StateConstant.RecvTips_3128_KC_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
					FileUtil.getInstance().deleteFile(file);
					FileUtil.getInstance().writeFile(file, newContent);
					filel.add(file);
				}else if(file.indexOf(StateConstant.on_3129) != -1) { //����3129���ַ���
					if(null != tipsdto.getExptype() && "1".equals(tipsdto.getExptype())) { //�����ص�˰Դͳ�Ƹ�ʽ
						String split = ","; //del�ļ�Ĭ�Ϸָ���
						String newFileName = tipsdto.getStaxorgcode()+"_"+tipsdto.getStartdate().toString().replaceAll("-", "").substring(0,6)+"_8.txt"; //Ҫ���ɵ��ļ�����
						List<String[]> temps = FileUtil.getInstance().readFileWithLine(file, ","); //���ļ����ݰ��ж�ȡ
						StringBuffer sb = new StringBuffer(""); //����ļ���һ��
						BigDecimal afamt = new BigDecimal("0.00"); //����ܽ��
						for(String[] strs : temps) {
							afamt = afamt.add(new BigDecimal(strs[9])); //������ļ����ܽ��
						}
						sb.append(newFileName+split+"0"+split+"0"+split+afamt+"\r\n"); //�ļ���һ��
						sb.append(FileUtil.getInstance().readFile(file).replaceAll("\"", "").replaceAll(" ", ""));  //�ļ���Ҫ����,��Ҫȥ�����źͿո�
						String newFileDir = file.substring(0,file.lastIndexOf("/")+1); //�ļ����·��
						FileUtil.getInstance().writeFile(newFileDir+newFileName, sb.toString()); //�����ص�˰Դ��ʽ�ļ�
						filel.add(newFileDir+newFileName);
					}else { // �����ÿ���ʽ�������ֶ�����
						String newContent = StateConstant.RecvTips_3129_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
						FileUtil.getInstance().deleteFile(file);
						FileUtil.getInstance().writeFile(file, newContent);
						filel.add(file);
					}	
				}else if(file.indexOf(StateConstant.in_3139) != -1) { //����3139���ַ���
					String newContent = StateConstant.RecvTips_3139_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
					FileUtil.getInstance().deleteFile(file);
					FileUtil.getInstance().writeFile(file, newContent);	
					filel.add(file);
				}else if(file.indexOf(StateConstant.in_3127) != -1)
				{
					String newContent = StateConstant.RecvTips_3127_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
					FileUtil.getInstance().deleteFile(file);
					FileUtil.getInstance().writeFile(file, newContent);	
					filel.add(file);
				}
			}else {
				continue;
			}			
		}		
		return filel;		
	}
	
	/**
	 * ���ڽ�������е��������ݴ浽List��
	 * @param rs
	 * @return
	 */
	public List<String> acheiveCollFromRS(SQLResults rs) {
		List<String> datelist = new ArrayList<String>();
		for(int i = 0 ; i < rs.getRowCount() ; i++) {
			datelist.add(rs.getString(0, i));
		}
		return datelist;
	}
	/**
	 * �����Ƚ���������֮�����������
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	public int daysOfTwo(Date fDate, Date oDate) {
	       Calendar aCalendar = Calendar.getInstance();
	       aCalendar.setTime(fDate);
	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       aCalendar.setTime(oDate);
	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       return day2-day1;
	}
}