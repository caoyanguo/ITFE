package com.cfcc.itfe.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
@SuppressWarnings("unchecked")
public class ExportBusinessDataCsv {
	private String filepath;
	String split = ",";// �ļ���¼�ָ�����
	String line = System.getProperty("line.separator");//����
	public int exportPayoutData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//ʵ���ʽ𵼳�
	{
		TvPayoutmsgmainDto finddto = (TvPayoutmsgmainDto)dto;
		finddto.setSbackflag(StateConstant.MSG_BACK_FLAG_NO);
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getScommitdate()+"_5101_shibozijin_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getScommitdate()+"_5101_shibozijin_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from Tv_Payoutmsgmain where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTv_Payoutmsgmain where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				filecontent = new StringBuffer("");
				TvPayoutmsgmainDto maindto = null;
				String sql = " select * from Tv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvPayoutmsgsubDto> subList = new ArrayList<TvPayoutmsgsubDto>();
				List<TvPayoutmsgsubDto> templist = null;
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayoutmsgsubDto>> subMap = new HashMap<String,List<TvPayoutmsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayoutmsgsubDto> tempList = null;
					for(TvPayoutmsgsubDto subdto:subList)
					{
						tempList = subMap.get(subdto.getSbizno());
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayoutmsgsubDto>();
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}
					}
				}
				filecontent.append("����Ϣ:�ļ�����,ί������,��������,�������,��Ʊ��λ,ƾ֤���,���,�տ��˿������к�,�տ����˺�,�տ�������,�������˺�,����������,Ԥ�㵥λ����,Ԥ�㵥λ����,");
				filecontent.append("Ԥ������,��������־,�������,����,����״̬,ԭ��"+line);
				filecontent.append("��ϸ��Ϣ:��������,���ÿ�Ŀ����,Ԥ����Ŀ����,���׽��,���ܿ�Ŀ����,Ԥ�����ͱ���,Ԥ����������,��֧�������,��֧��������"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayoutmsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//�ļ�����
					else
						filecontent.append(getString(maindto.getSfilename())+split);//�ļ�����
					filecontent.append(getString(maindto.getScommitdate())+split);//ί������
					filecontent.append(getString(maindto.getSaccdate())+split);//��������
					filecontent.append(getString(maindto.getStrecode())+split);//�������
					filecontent.append(getString(maindto.getSpayunit())+split);//��Ʊ��λ
					filecontent.append(getString(maindto.getStaxticketno())+split);//ƾ֤���
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//���
					String bankno = maindto.getSrecbankno();//�����к�//					String bankname = "";//					if(bankMap != null){//						bankname = bankMap.get(bankno)==null ? "": bankMap.get(bankno).getSbankname();//��������//					}
					filecontent.append(getString(bankno)+split);//�տ��˿������к�//					filecontent.append(bankname+split);//�տ��˿���������
					filecontent.append(getString(maindto.getSrecacct())+split);//�տ����˺�
					filecontent.append(getString(maindto.getSrecname())+split);//�տ�������
					filecontent.append(getString(maindto.getSpayeracct())+split);//�������˺�
					filecontent.append(getString(maindto.getSpayername())+split);//����������
					filecontent.append(getString(maindto.getSbudgetunitcode())+split); //Ԥ�㵥λ����
					filecontent.append(getString(maindto.getSunitcodename())+split); //Ԥ�㵥λ����
					filecontent.append(getString(maindto.getSbudgettype())+split); //Ԥ������
					filecontent.append(getString(maindto.getStrimflag())+split); //��������־
					filecontent.append(getString(maindto.getSofyear())+split); //�������
					filecontent.append(getString(maindto.getSaddword())+split);//����
					filecontent.append(getString(maindto.getSstatus())+split);//����״̬
					filecontent.append(maindto.getSdemo()==null ? ""+line : maindto.getSdemo()+line);//ԭ��
					subList = subMap.get(maindto.getSbizno());
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayoutmsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//��������
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//���ÿ�Ŀ����
							filecontent.append(getString(subdto.getSbudgetprjcode())+split);//Ԥ����Ŀ����
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//���׽��
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//���ܿ�Ŀ����
							filecontent.append(getString(subdto.getSbgttypecode())+split);//Ԥ�����ͱ���
							filecontent.append(getString(subdto.getSbgttypename())+split);//Ԥ����������
							filecontent.append(getString(subdto.getSprocatcode())+split);//��֧�������
							filecontent.append(getString(subdto.getSprocatname())+line);//��֧��������
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("��ѯ����:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("�ļ���Ȩ����:chmod 777"+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportPayoutBack(IDto dto,Map filepathnameMap) throws ITFEBizException//ʵ���ʽ��˻ص���
	{
		TvPayoutmsgmainDto finddto = (TvPayoutmsgmainDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		fname = getFilepath()+finddto.getScommitdate()+"_3145_shibotuihui_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from Tv_Payoutmsgmain where S_COMMITDATE=? and S_BACKFLAG=? ");
			sqlExec.addParam(finddto.getScommitdate());
			sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
			mainsql.append(" union select * from HTv_Payoutmsgmain where S_COMMITDATE=? and S_BACKFLAG=? ");
			sqlExec.addParam(finddto.getScommitdate());
			sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				TvPayoutmsgmainDto maindto = null;
				String sql = " select * from Tv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				List<TvPayoutmsgsubDto> subList = new ArrayList<TvPayoutmsgsubDto>();
				List<TvPayoutmsgsubDto> templist = null;
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayoutmsgsubDto>> subMap = new HashMap<String,List<TvPayoutmsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayoutmsgsubDto> tempList = null;
					for(TvPayoutmsgsubDto subdto:subList)
					{
						tempList = subMap.get(subdto.getSbizno());
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayoutmsgsubDto>();
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}
					}
				}
				filecontent = new StringBuffer("");
				filecontent.append("����Ϣ:�ļ�����,ί������,��������,�������,��Ʊ��λ,ƾ֤���,���׽��,�˻ؽ��,ԭ�տ��˿������к�,ԭ�տ����˺�,ԭ�տ�������,ԭ�������˺�,ԭ����������,Ԥ�㵥λ����,Ԥ�㵥λ����,");
				filecontent.append("Ԥ������,�����ڱ�־,�������,����,����״̬,ԭ��"+line);
				filecontent.append("��ϸ��Ϣ:��������,���ÿ�Ŀ����,Ԥ����Ŀ����,���׽��,���ܿ�Ŀ����,Ԥ�����ͱ���,Ԥ����������,��֧�������,��֧��������"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayoutmsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//�ļ�����
					else
						filecontent.append(getString(maindto.getSfilename())+split);//�ļ�����
					filecontent.append(getString(maindto.getScommitdate())+split);//ί������
					filecontent.append(getString(maindto.getSaccdate())+split);//��������
					filecontent.append(getString(maindto.getStrecode())+split);//�������
					filecontent.append(getString(maindto.getSpayunit())+split);//��Ʊ��λ
					filecontent.append(getString(maindto.getStaxticketno())+split);//ƾ֤���
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//���׽��
					filecontent.append(getString(maindto.getShold2())+split);//�˻ؽ��
					String bankno = maindto.getSrecbankno();//�����к�//					String bankname = "";//					if(bankMap != null){//						bankname = bankMap.get(bankno)==null ? "": bankMap.get(bankno).getSbankname();//��������//					}
					filecontent.append(getString(bankno)+split);//�տ��˿������к�//					filecontent.append(bankname+split);//�տ��˿���������
					filecontent.append(getString(maindto.getSrecacct())+split);//�տ����˺�
					filecontent.append(getString(maindto.getSrecname())+split);//�տ�������
					filecontent.append(getString(maindto.getSpayeracct())+split);//�������˺�
					filecontent.append(getString(maindto.getSpayername())+split);//����������
					filecontent.append(getString(maindto.getSbudgetunitcode())+split); //Ԥ�㵥λ����
					filecontent.append(getString(maindto.getSunitcodename())+split); //Ԥ�㵥λ����
					filecontent.append(getString(maindto.getSbudgettype())+split); //Ԥ������
					filecontent.append(getString(maindto.getStrimflag())+split); //��������־
					filecontent.append(getString(maindto.getSofyear())+split); //�������
					filecontent.append(getString(maindto.getSaddword())+split);//����
					filecontent.append(getString(maindto.getSstatus())+split);//����״̬
					filecontent.append(maindto.getSdemo()==null ? ""+line : maindto.getSdemo()+line);//ԭ��
					subList = subMap.get(maindto.getSbizno());
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayoutmsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//��������
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//���ÿ�Ŀ����
							filecontent.append(getString(subdto.getSbudgetprjcode())+split);//Ԥ����Ŀ����
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//���׽��
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//���ܿ�Ŀ����
							filecontent.append(getString(subdto.getSbgttypecode())+split);//Ԥ�����ͱ���
							filecontent.append(getString(subdto.getSbgttypename())+split);//Ԥ����������
							filecontent.append(getString(subdto.getSprocatcode())+split);//��֧�������
							filecontent.append(getString(subdto.getSprocatname())+line);//��֧��������
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("��ѯ����:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("�ļ���Ȩ����:chmod 777"+fname+e.toString(),e);
		} finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportDirectPayData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//ֱ��֧����ȵ���
	{
		TvDirectpaymsgmainDto finddto = (TvDirectpaymsgmainDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getScommitdate()+"_5102_zhijiezhifu_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getScommitdate()+"_5102_zhijiezhifu_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_DIRECTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_DIRECTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvDirectpaymsgmainDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				filecontent = new StringBuffer("");
				TvDirectpaymsgmainDto maindto = null;
				String sql = " select * from Tv_Directpaymsgsub where I_VOUSRLNO in("+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvDirectpaymsgsubDto> subList = new ArrayList<TvDirectpaymsgsubDto>();
				List<TvDirectpaymsgsubDto> templist = null;
				templist = (List<TvDirectpaymsgsubDto>)sqlExec.runQuery(sql, TvDirectpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Directpaymsgsub where I_VOUSRLNO in("+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvDirectpaymsgsubDto>)sqlExec.runQuery(sql, TvDirectpaymsgsubDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvDirectpaymsgsubDto>> subMap = new HashMap<String,List<TvDirectpaymsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvDirectpaymsgsubDto> tempList = null;
					for(TvDirectpaymsgsubDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvDirectpaymsgsubDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				filecontent.append("����Ϣ:�ļ�����,ί������,��������,�������,��Ʊ��λ,ƾ֤���,���,����ˮ��,ת������,���쵥λ,Ԥ������,�������,�տ����˺�,�տ�������,");
				filecontent.append("�տ��˿���������,�������˺�,����������,������������,�������б���,���������к�,����״̬,������������"+line);
				filecontent.append("��ϸ��Ϣ:��������,Ԥ���ڵ�λ����,���ܿ�Ŀ����,���׽��,���ÿ�Ŀ����,Ԥ�㵥λ����,Ԥ�㵥λ����,֧����ϸ,�������"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvDirectpaymsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//�ļ�����
					else
						filecontent.append(getString(maindto.getSfilename())+split);//�ļ�����
					filecontent.append(getString(maindto.getScommitdate())+split);//ί������
					filecontent.append(getString(maindto.getSaccdate())+split);//��������
					filecontent.append(getString(maindto.getStrecode())+split);//�������
					filecontent.append(getString(maindto.getSpayunit())+split);//��Ʊ��λ
					filecontent.append(getString(maindto.getStaxticketno())+split);//ƾ֤���
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//���
					filecontent.append(getString(maindto.getSpackageno())+split);//����ˮ��
					filecontent.append(getString(maindto.getStransbankcode())+split);//ת������
					filecontent.append(getString(maindto.getStransactunit())+split);//���쵥λ
					filecontent.append(getString(maindto.getSbudgettype())+split);//Ԥ������
					filecontent.append(getString(maindto.getSamttype())+split);//�������
					filecontent.append(getString(maindto.getSpayeeacctno())+split);//�տ����˺�
					filecontent.append(getString(maindto.getSpayeeacctname())+split); //�տ�������
					filecontent.append(getString(maindto.getSpayeeacctbankname())+split); //�տ��˿���������
					filecontent.append(getString(maindto.getSpayacctno())+split); //�������˺�
					filecontent.append(getString(maindto.getSpayacctname())+split); //����������
					filecontent.append(getString(maindto.getSpayacctbankname())+split); //������������
					filecontent.append(getString(maindto.getSpaybankcode())+split);//�������б���
					filecontent.append(getString(maindto.getSpaybankno())+split);//���������к�
					filecontent.append(getString(maindto.getSstatus())+split);//����״̬
					filecontent.append(getString(maindto.getSpaybankname())+line);//������������
					subList = subMap.get(String.valueOf(maindto.getIvousrlno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvDirectpaymsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//��������
							filecontent.append(getString(subdto.getSbudgetunitcode())+split);//Ԥ���ڵ�λ����
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//���ܿ�Ŀ����
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//���׽��
							filecontent.append(getString(subdto.getSecosubjectcode())+split);//���ÿ�Ŀ����
							filecontent.append(getString(subdto.getSagencycode())+split);//Ԥ�㵥λ����
							filecontent.append(getString(subdto.getSagencyname())+split);//Ԥ�㵥λ����
							filecontent.append(getString(subdto.getSline())+split);//֧����ϸ
							filecontent.append(getString(subdto.getSofyear())+line);//�������
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("��ѯ����:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("�ļ���Ȩ����:chmod 777"+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportGrantPayData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//��Ȩ֧����ȵ���
	{
		TvGrantpaymsgmainDto finddto = (TvGrantpaymsgmainDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getScommitdate()+"_5103_shouquanzhifu_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getScommitdate()+"_5103_shouquanzhifu_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_GRANTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_GRANTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvGrantpaymsgmainDto.class).getDtoCollection();	
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				TvGrantpaymsgmainDto maindto = null;
				String sql = " select b.* from TV_GRANTPAYMSGSUB b,TV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				List<TvGrantpaymsgsubDto> subList = new ArrayList<TvGrantpaymsgsubDto>();
				List<TvGrantpaymsgsubDto> templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select b.* from TV_GRANTPAYMSGSUB b,HTV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select b.* from HTV_GRANTPAYMSGSUB b,TV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select b.* from HTV_GRANTPAYMSGSUB b,HTV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvGrantpaymsgsubDto>> subMap = new HashMap<String,List<TvGrantpaymsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvGrantpaymsgsubDto> tempList = null;
					for(TvGrantpaymsgsubDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvGrantpaymsgsubDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()), tempList);
						}
					}
				}
				filecontent = new StringBuffer("");
				filecontent.append("����Ϣ:�ļ�����,ί������,��������,�������,��Ʊ��λ,���κ�,���,����ˮ��,ת������,���쵥λ,Ԥ������,�������,��Ʊ����,�������,�����·�,");
				filecontent.append("��ע,������֪ͨ��,Ԥ�㵥λ����,�ʽ����ʱ���,�������б���,���������к�,����״̬,������������"+line);
				filecontent.append("��ϸ��Ϣ:��������,Ԥ���ڵ�λ����,���ܿ�Ŀ����,���׽��,���ÿ�Ŀ����,�˻�����,��ע,֧����ϸ,�������"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvGrantpaymsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//�ļ�����
					else
						filecontent.append(getString(maindto.getSfilename())+split);//�ļ�����
					filecontent.append(getString(maindto.getScommitdate())+split);//ί������
					filecontent.append(getString(maindto.getSaccdate())+split);//��������
					filecontent.append(getString(maindto.getStrecode())+split);//�������
					filecontent.append(getString(maindto.getSpayunit())+split);//��Ʊ��λ
					filecontent.append(getString(maindto.getSlimitid())+split);//���κ�
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//���
					filecontent.append(getString(maindto.getSpackageno())+split);//����ˮ��
					filecontent.append(getString(maindto.getStransbankcode())+split);//ת������
					filecontent.append(getString(maindto.getStransactunit())+split);//���쵥λ
					filecontent.append(getString(maindto.getSbudgettype())+split);//Ԥ������
					filecontent.append(getString(maindto.getSamttype())+split);//�������
					filecontent.append(getString(maindto.getSgenticketdate())+split);//��Ʊ����
					filecontent.append(getString(maindto.getSofyear())+split); //�������
					filecontent.append(getString(maindto.getSofmonth())+split); //�����·�
					filecontent.append(getString(maindto.getSdemo())+split); //��ע
					filecontent.append(getString(maindto.getSid())+split); //������֪ͨ��
					filecontent.append(getString(maindto.getSdeptnum())+split); //Ԥ�㵥λ����
					filecontent.append(getString(maindto.getSfundtypecode())+split); //�ʽ����ʱ���
					filecontent.append(getString(maindto.getSpaybankcode())+split);//�������б���
					filecontent.append(getString(maindto.getSpaybankno())+split);//���������к�
					filecontent.append(getString(maindto.getSstatus())+split);//����״̬
					filecontent.append(getString(maindto.getSpaybankname())+line);//������������
					subList = subMap.get(String.valueOf(maindto.getIvousrlno())+String.valueOf(maindto.getSpackageticketno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvGrantpaymsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//��������
							filecontent.append(getString(subdto.getSbudgetunitcode())+split);//Ԥ���ڵ�λ����
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//���ܿ�Ŀ����
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//���׽��
							filecontent.append(getString(subdto.getSecosubjectcode())+split);//���ÿ�Ŀ����
							filecontent.append(getString(subdto.getSaccattrib())+split);//�˻�����
							filecontent.append(getString(subdto.getSdemo())+split);//��ע
							filecontent.append(getString(subdto.getSline())+split);//֧����ϸ
							filecontent.append(getString(subdto.getSofyear())+line);//�������
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("��ѯ����:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("�ļ���Ȩ����:chmod 777"+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportCommApplyPayData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//���л����
	{
		TvPayreckBankDto finddto = (TvPayreckBankDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_RESULT")>=0)
			fname = getFilepath()+finddto.getDentrustdate()+"_2301_shanghenghuakuan_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getDentrustdate()+"_2301_shanghenghuakuan_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_PAYRECK_BANK where D_ENTRUSTDATE=? ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_RESULT"))
			{
				mainsql.append("AND (S_RESULT=? or S_RESULT=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_PAYRECK_BANK where D_ENTRUSTDATE=?  ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_RESULT"))
			{
				mainsql.append("AND (S_RESULT=? or S_RESULT=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayreckBankDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				TvPayreckBankDto maindto = null;
				filecontent = new StringBuffer("");
				String sql = " select * from Tv_Payreck_Bank_List where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvPayreckBankListDto> subList = new ArrayList<TvPayreckBankListDto>();
				List<TvPayreckBankListDto> templist = null;
				templist = (List<TvPayreckBankListDto>)sqlExec.runQuery(sql, TvPayreckBankListDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Payreck_Bank_List where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvPayreckBankListDto>)sqlExec.runQuery(sql, TvPayreckBankListDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayreckBankListDto>> subMap = new HashMap<String,List<TvPayreckBankListDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayreckBankListDto> tempList = null;
					for(TvPayreckBankListDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayreckBankListDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				filecontent.append("����Ϣ:�ļ�����,ί������,��������,ƾ֤����,��������,����������,�������˺�,����������,�����˵�ַ,�տ����˺�,�տ�������,�տ��˵�ַ,�տ��˿������к�,");
				filecontent.append("�������д���,������������,����ˮ��,������ˮ��,�������,�������ش���,����,Ԥ������,�����ڱ�־,���,");
				filecontent.append("������,��ϸ����,˵��,Ԥ�����ʹ���,Ԥ����������,�ʽ����ʴ���,�ʽ���������,֧����ʽ����,֧����ʽ����,֧���������к�,���뻮��ƾ֤,����״̬,������������"+line);
				filecontent.append("��ϸ��Ϣ:Ԥ�㵥λ����,Ԥ�㵥λ����,�������Ŀ����,�������Ŀ����,���׽��,֧�����ܿ�Ŀ����,ժҪ����"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayreckBankDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//�ļ�����
					else
						filecontent.append(getString(maindto.getSfilename())+split);//�ļ�����
					filecontent.append(getString(String.valueOf(maindto.getDentrustdate()))+split);//ί������
					filecontent.append(getString(String.valueOf(maindto.getDacctdate()))+split);//��������
					filecontent.append(getString(String.valueOf(maindto.getDvoudate()))+split);//ƾ֤����
					filecontent.append(getString(String.valueOf(maindto.getDacceptdate()))+split);//��������
					filecontent.append(getString(maindto.getStrecode())+split);//����������
					filecontent.append(getString(maindto.getSpayeracct())+split);//�������˺�
					filecontent.append(getString(maindto.getSpayername())+split);//����������
					filecontent.append(getString(maindto.getSpayeraddr())+split);//�����˵�ַ
					filecontent.append(getString(maindto.getSpayeeacct())+split);//�տ����˺�
					filecontent.append(getString(maindto.getSpayeename())+split);//�տ�������
					filecontent.append(getString(maindto.getSpayeeaddr())+split);//�տ��˵�ַ
					filecontent.append(getString(maindto.getSpayeeopbkno())+split);//�տ��˿������к�
					filecontent.append(getString(maindto.getSagentbnkcode())+split);//�������д���
					filecontent.append(getString(maindto.getSagentacctbankname())+split);//������������
					filecontent.append(getString(maindto.getSpackno())+split);//����ˮ��
					filecontent.append(getString(maindto.getStrano())+split);//������ˮ��
					filecontent.append(getString(maindto.getSofyear())+split); //�������
					filecontent.append(getString(maindto.getSfinorgcode())+split); //�������ش���
					filecontent.append(getString(maindto.getSaddword())+split); //����
					filecontent.append(getString(maindto.getSbudgettype())+split); //Ԥ������
					filecontent.append(getString(maindto.getStrimsign())+split); //�����ڱ�־
					filecontent.append(getString(String.valueOf(maindto.getFamt()))+split); //���
					filecontent.append(getString(maindto.getSresult())+split); //������
					filecontent.append(getString(String.valueOf(maindto.getIstatinfnum()))+split);//��ϸ����
					filecontent.append(getString(maindto.getSdescription())+split);//˵��
					filecontent.append(getString(maindto.getSbgttypecode())+split);//Ԥ�����ʹ���
					filecontent.append(getString(maindto.getSbgttypename())+split);//Ԥ����������
					filecontent.append(getString(maindto.getSfundtypecode())+split);//�ʽ����ʴ���
					filecontent.append(getString(maindto.getSfundtypename())+split);//�ʽ���������
					filecontent.append(getString(maindto.getSpaytypecode())+split);//֧����ʽ����
					filecontent.append(getString(maindto.getSpaytypename())+split);//֧����ʽ����
					filecontent.append(getString(maindto.getSxpaysndbnkno())+split);//֧���������к�
					filecontent.append(getString(maindto.getSid())+split);//���뻮��ƾ֤
					filecontent.append(getString(maindto.getSresult())+split);//����״̬
					filecontent.append(getString(maindto.getSpaybankname())+line);//������������
					subList = subMap.get(String.valueOf(maindto.getIvousrlno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayreckBankListDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSbdgorgcode())+split);//Ԥ�㵥λ����
							filecontent.append(getString(subdto.getSsupdepname())+split);//Ԥ�㵥λ����
							filecontent.append(getString(subdto.getSfuncbdgsbtcode())+split);//�������Ŀ����
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//�������Ŀ����
							filecontent.append(getString(String.valueOf(subdto.getFamt()))+split);//���׽��
							filecontent.append(getString(subdto.getSacctprop())+split);//�˻�����
							filecontent.append(getString(subdto.getSexpfuncname())+split);//֧�����ܿ�Ŀ����
							filecontent.append(getString(subdto.getSpaysummaryname())+line);//ժҪ����
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("��ѯ����:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("�ļ���Ȩ����:chmod 777 "+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportCommApplyPayBackData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//�����˿��
	{
		TvPayreckBankBackDto finddto = (TvPayreckBankBackDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getDentrustdate()+"_2302_shanghengtuikuan_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getDentrustdate()+"_2302_shanghengtuikuan_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_PAYRECK_BANK_BACK where D_ENTRUSTDATE=? ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_PAYRECK_BANK_BACK where D_ENTRUSTDATE=? ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayreckBankBackDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				filecontent = new StringBuffer("");
				TvPayreckBankBackDto maindto = null;
				String sql = " select * from TV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvPayreckBankBackListDto> subList = new ArrayList<TvPayreckBankBackListDto>();
				List<TvPayreckBankBackListDto> templist = null;
				templist = (List<TvPayreckBankBackListDto>)sqlExec.runQuery(sql, TvPayreckBankBackListDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvPayreckBankBackListDto>)sqlExec.runQuery(sql, TvPayreckBankBackListDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayreckBankBackListDto>> subMap = new HashMap<String,List<TvPayreckBankBackListDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayreckBankBackListDto> tempList = null;
					for(TvPayreckBankBackListDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayreckBankBackListDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				filecontent.append("����Ϣ:�ļ�����,ί������,����ˮ��,���������к�,������������,ƾ֤���,ƾ֤����,�������ش���,�������,�տ����˺�,�տ�������,ԭί������,ԭƾ֤����,ԭƾ֤���,");
				filecontent.append("�������˺�,����������,�������д���,������������,ԭ������ˮ��,�������,�������ش���,����,Ԥ������,�����ڱ�־,���,֧���������,��ϸ����,֧�����ı��,");
				filecontent.append("֧��ί������,Ԥ�����ʹ���,Ԥ����������,�ʽ����ʴ���,�ʽ���������,֧����ʽ����,֧����ʽ����,֧���������к�,���뻮��ƾ֤,����״̬,������������"+line);
				filecontent.append("��ϸ��Ϣ:ԭƾ֤����,ԭƾ֤���,Ԥ�㵥λ����,Ԥ�㵥λ����,���ܿ�Ŀ����,���ܿ�Ŀ����,�������Ŀ����,���׽��,�˻�����,ժҪ����"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayreckBankBackDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//�ļ�����
					else
						filecontent.append(getString(maindto.getSfilename())+split);//�ļ�����
					filecontent.append(getString(String.valueOf(maindto.getDentrustdate()))+split);//ί������
					filecontent.append(getString(maindto.getSpackno())+split);//����ˮ��
					filecontent.append(getString(maindto.getSagentbnkcode())+split);//���������к�
					filecontent.append(getString(maindto.getSagentacctbankname())+split);//������������
					filecontent.append(getString(maindto.getSvouno())+split);//ƾ֤���
					filecontent.append(getString(String.valueOf(maindto.getDvoudate()))+split);//ƾ֤����
					filecontent.append(getString(maindto.getSfinorgcode())+split);//�������ش���
					filecontent.append(getString(maindto.getStrecode())+split);//�������
					filecontent.append(getString(maindto.getSpayeeacct())+split);//�տ����˺�
					filecontent.append(getString(maindto.getSpayeename())+split);//�տ�������
					filecontent.append(getString(String.valueOf(maindto.getDorientrustdate()))+split);//ԭί������
					filecontent.append(getString(String.valueOf(maindto.getDorivoudate()))+split);//ԭƾ֤����
					filecontent.append(getString(maindto.getSorivouno())+split);//ԭƾ֤���
					filecontent.append(getString(maindto.getSpayeracct())+split);//�������˺�
					filecontent.append(getString(maindto.getSpayername())+split);//����������
					filecontent.append(getString(maindto.getSagentbnkcode())+split);//�������д���
					filecontent.append(getString(maindto.getSagentacctbankname())+split);//������������
					filecontent.append(getString(maindto.getSoritrano())+split);//ԭ������ˮ��
					filecontent.append(getString(maindto.getSofyear())+split); //�������
					filecontent.append(getString(maindto.getSfinorgcode())+split); //�������ش���
					filecontent.append(getString(maindto.getSaddword())+split); //����
					filecontent.append(getString(maindto.getSbudgettype())+split); //Ԥ������
					filecontent.append(getString(maindto.getStrimsign())+split); //�����ڱ�־
					filecontent.append(getString(String.valueOf(maindto.getFamt()))+split); //���
					filecontent.append(getString(maindto.getSpaydictateno())+split); //֧���������
					filecontent.append(getString(String.valueOf(maindto.getIstatinfnum()))+split);//��ϸ����
					filecontent.append(getString(maindto.getSpaymsgno())+split);//֧�����ı��
					filecontent.append(getString(String.valueOf(maindto.getDpayentrustdate()))+split);//֧��ί������
					filecontent.append(getString(maindto.getSbgttypecode())+split);//Ԥ�����ʹ���
					filecontent.append(getString(maindto.getSbgttypename())+split);//Ԥ����������
					filecontent.append(getString(maindto.getSfundtypecode())+split);//�ʽ����ʴ���
					filecontent.append(getString(maindto.getSfundtypename())+split);//�ʽ���������
					filecontent.append(getString(maindto.getSpaytypecode())+split);//֧����ʽ����
					filecontent.append(getString(maindto.getSpaytypename())+split);//֧����ʽ����
					filecontent.append(getString(maindto.getSxpaysndbnkno())+split);//֧���������к�
					filecontent.append(getString(maindto.getSid())+split);//���뻮��ƾ֤
					filecontent.append(getString(maindto.getSstatus())+split);//����״̬
					filecontent.append(getString(maindto.getSpaybankname())+line);//������������
					subList = subMap.get(String.valueOf(maindto.getIvousrlno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayreckBankBackListDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(String.valueOf(subdto.getDorivoudate()))+split);//ԭƾ֤����
							filecontent.append(getString(subdto.getSorivouno())+split);//ԭƾ֤���
							filecontent.append(getString(subdto.getSbdgorgcode())+split);//Ԥ�㵥λ����
							filecontent.append(getString(subdto.getSsupdepname())+split);//Ԥ�㵥λ����
							filecontent.append(getString(subdto.getSfuncbdgsbtcode())+split);//���ܿ�Ŀ����
							filecontent.append(getString(subdto.getSexpfuncname())+split);//���ܿ�Ŀ����
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//�������Ŀ����
							filecontent.append(getString(String.valueOf(subdto.getFamt()))+split);//���׽��
							filecontent.append(getString(subdto.getSacctprop())+split);//�˻�����
							filecontent.append(getString(subdto.getSpaysummaryname())+line);//ժҪ����
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("��ѯ����:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("�ļ���Ȩ����:chmod 777"+fname+e.toString(),e);
		} finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
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
	public  List<String> getFileList(String filePath){
		File tmp1 = new File(filePath);
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		List<String> filesStr = new ArrayList<String>(); 
		if (file.isFile()) {
			filesStr.add(str);
		} else {
			File[] f = file.listFiles();
			if (f == null || f.length <= 0) {
				filesStr.add(str);
			}else
			{
				for (int i = 0; i < f.length; i++) {
					File tmp = f[i];
					String strF = tmp.getAbsolutePath();
					if (tmp.isFile()&&(tmp.getName().startsWith("3143_")||tmp.getName().startsWith("3144_")))
						filesStr.add(strF);
				}
			}
		}
		return filesStr;
	}
	// �����ļ�
    private void copyFile(String sourceFile, String targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
        	File souFile = new File(sourceFile);
        	File tarFile = new File(targetFile);
        	if(!tarFile.getParentFile().exists())
        		tarFile.getParentFile().mkdirs();
            // �½��ļ����������������л���
            inBuff = new BufferedInputStream(new FileInputStream(souFile));
            // �½��ļ���������������л���
            outBuff = new BufferedOutputStream(new FileOutputStream(tarFile));
            // ��������
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // ˢ�´˻���������
            outBuff.flush();
        } finally {
            // �ر���
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
	public String getFilepath() {
		if(filepath==null||"".equals(filepath))
		{
			if(isWin())
				filepath = "D:/itfe/kbftp/";
			else
				filepath = "/itfe/kbftp/";
		}
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	private void writeFile(String fname,String fileContent,Map filepathnameMap) throws FileOperateException
	{
		if(fname!=null&&!"".equals(fname)&&fileContent!=null&&!"".equals(fileContent))
		{
			if(filepathnameMap!=null&&filepathnameMap.get("fileList")!=null)
			{
				String newfilename = TimeFacade.getCurrentStringTime()+File.separator+StringUtil.replace(fname, getFilepath(), "");//"ITFEDATA"+File.separator
				File f = new File(ITFECommonConstant.FILE_ROOT_PATH+newfilename);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH+newfilename);
				}
				FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH+newfilename,fileContent);
				((List)filepathnameMap.get("fileList")).add(newfilename);
			}else
			{
				File f = new File(fname);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fname);
				}
				FileUtil.getInstance().writeFile(fname,fileContent);
			}
		}
	}
	private String getString(String getString)
	{
		if(getString==null||"".equals(getString)||"null".equals(getString)||"NULL".equals(getString))
			return "";
		else
			return getString;
	}
	public String recvLogCopyFile(String copydate,String msgno) throws ITFEBizException
	{
		TvRecvlogDto recvlog = new TvRecvlogDto();
		recvlog.setSdate(copydate);
		recvlog.setSoperationtypecode(msgno);
		List<TvRecvlogDto> fileList = null;
		String filepath = null;
		try {
			fileList = CommonFacade.getODB().findRsByDto(recvlog);
			if(fileList!=null&&fileList.size()>0)
			{
				for(TvRecvlogDto tempdto:fileList)
				{
					filepath = tempdto.getStitle();
					if(filepath!=null&&!"".equals(filepath))
					{
						copyFile(filepath,getFilepath()+copydate+"/"+filepath.substring(filepath.indexOf(msgno+"_"),filepath.length()));
					}
				}
				CallShellUtil.callShellWithRes("chmod -R 777 "+getFilepath()+copydate+"/");
				return String.valueOf(fileList.size());
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��������ҵ�����ݵ�ftpĿ¼�²�ѯ������־�б��ĳ���-"+copydate+"-"+msgno,e);
		} catch (ValidateException e) {
			throw new ITFEBizException("��������ҵ�����ݵ�ftpĿ¼�²�ѯ������־�б��ĳ���-"+copydate+"-"+msgno,e);
		} catch (IOException e) {
			throw new ITFEBizException("��������ҵ�����ݵ�ftpĿ¼�¿����ļ�����-"+copydate+"-"+msgno,e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
	public static void main(String args[]) throws IOException
	{
		String s="�������ﰡds";
		String ls =  new String(s.getBytes("GBK"),"iso-8859-1");
		System.out.println("================"+s.length());
		System.out.println("================"+ls.length());
//		List<String> fileList = getFileList("E:/fujian/2014082873071713030700000");
//		if(fileList!=null)
//		{
//			for(int i=0;i<fileList.size();i++)
//			{
//				String tempString = fileList.get(i);
//				System.out.println(tempString.indexOf("3143_")>0?tempString.substring(tempString.indexOf("3143_"),tempString.length()):tempString.substring(tempString.indexOf("3144_"),tempString.length()));
//				copyFile(tempString,"E:/fujian/20140829/"+(tempString.indexOf("3143_")>0?tempString.substring(tempString.indexOf("3143_"),tempString.length()):tempString.substring(tempString.indexOf("3144_"),tempString.length())));
//			}
//		}
	}
}
