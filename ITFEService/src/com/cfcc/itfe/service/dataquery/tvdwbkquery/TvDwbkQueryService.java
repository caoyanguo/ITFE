package com.cfcc.itfe.service.dataquery.tvdwbkquery;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.HtvDwbkDto;
import com.cfcc.itfe.persistence.dto.HtvDwbkDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvDwbkReportDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author t60
 * @time   12-02-22 14:06:01
 * codecomment: 
 */

public class TvDwbkQueryService extends AbstractTvDwbkQueryService {
	private static Log log = LogFactory.getLog(TvDwbkQueryService.class);	


	/**
	 * ��ȡ��ѯ���	 
	 * @generated
	 * @param dtoInfo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List getSearchList(TvDwbkDto dtoInfo) throws ITFEBizException {
      return null;
    }


	public void updateFail(List dtoInfos) throws ITFEBizException {
		if(ITFECommonConstant.IFNEWINTERFACE.equals("1")){
    		throw new ITFEBizException("�°�TIPS�ӿ��Զ����´�״̬���������ֶ����£�");
    		
    	}
		try {
			DatabaseFacade.getDb().update(CommonUtil.listTArray(dtoInfos));
		} catch (JAFDatabaseException e1) {
			log.error(e1);
			throw new ITFEBizException("������������ʧ��", e1);
		}
		
	}


	public List searchForReport(String selectedtable, IDto dtoInfo)
			throws ITFEBizException {
		SQLExecutor sqlExecutor=null;
		SQLResults rs = null;
		List list = new ArrayList();
		try{
			TvDwbkDto dto=(TvDwbkDto)dtoInfo;
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT I_VOUSRLNO,S_DWBKVOUCODE,dwbk.S_TAXORGCODE,dwbk.S_PAYEECODE,S_PAYERTRECODE,S_AIMTRECODE,ev.S_VALUECMT as C_BDGKIND,S_BDGSBTCODE,evalue2.S_VALUECMT as S_STATUS,";
			sql+="dbr.S_TCBSDRAWNAME as S_DWBKREASONCODE,evalue.S_VALUECMT as C_BCKFLAG,S_PAYEEACCT,S_PAYEENAME,S_PAYEEOPNBNKNO,F_AMT,S_FILENAME,S_TAXORGNAME ";
			if(selectedtable.equals("0")){
				sql+=" FROM TV_DWBK dwbk LEFT JOIN TD_ENUMVALUE ev on dwbk.C_BDGKIND=ev.S_VALUE AND ev.S_TYPECODE='0122'" +
						" LEFT JOIN TS_DRAWBACKREASON dbr ON dbr.S_TCBSDRAWCODE=dwbk.S_DWBKREASONCODE AND dbr.S_BOOKORGCODE='"+dto.getSbookorgcode()+"' " +
						" LEFT JOIN TD_ENUMVALUE evalue on dwbk.C_BCKFLAG=evalue.S_VALUE AND evalue.S_TYPECODE='0117'" +
						" LEFT JOIN TD_ENUMVALUE evalue2 on dwbk.S_STATUS=evalue2.S_VALUE AND evalue2.S_TYPECODE='0412'"+
						" LEFT JOIN TS_TAXORG tao on dwbk.S_TAXORGCODE=tao.S_TAXORGNAME  WHERE ";
				if(dto.getSstatus()!=null&&!dto.getSstatus().equals("")){
					sql+="dwbk.S_STATUS='"+dto.getSstatus()+"' AND  ";
				}
			}else if(selectedtable.equals("1")){
				sql+=" FROM HTV_DWBK dwbk LEFT JOIN TD_ENUMVALUE ev on dwbk.C_BDGKIND=ev.S_VALUE AND ev.S_TYPECODE='0122'" +
				" LEFT JOIN TS_DRAWBACKREASON dbr ON dbr.S_TCBSDRAWCODE=dwbk.S_DWBKREASONCODE AND dbr.S_BOOKORGCODE='"+dto.getSbookorgcode()+"' " +
				" LEFT JOIN TD_ENUMVALUE evalue on dwbk.C_BCKFLAG=evalue.S_VALUE AND evalue.S_TYPECODE='0117'" +
				" LEFT JOIN TD_ENUMVALUE evalue2 on dwbk.S_STATUS=evalue2.S_VALUE AND evalue2.S_TYPECODE='0412'"+
				" LEFT JOIN TS_TAXORG tao on dwbk.S_TAXORGCODE=tao.S_TAXORGNAME  WHERE ";
			}
			if(dto.getIvousrlno()!=null&&!dto.getIvousrlno().equals("")){
				sql+="  dwbk.I_VOUSRLNO='"+dto.getIvousrlno()+"' AND ";
			}
			if(dto.getSdwbkvoucode()!=null&&!dto.getSdwbkvoucode().equals("")){
				sql+="dwbk.S_DWBKVOUCODE='"+dto.getSdwbkvoucode()+"' AND ";
			}
			if(dto.getStaxorgcode()!=null&&!dto.getStaxorgcode().equals("")){
				sql+="dwbk.S_TAXORGCODE='"+dto.getStaxorgcode()+"' AND ";
			}
			if(dto.getDaccept()!=null&&!dto.getDaccept().equals("")){
				sql+="dwbk.D_ACCEPT='"+dto.getDaccept()+"' AND ";
			}
			if(dto.getCbdgkind()!=null&&!dto.getCbdgkind().equals("")){
				sql+="dwbk.C_BDGKIND='"+dto.getCbdgkind()+"' AND ";
			}
			if(dto.getSdwbkreasoncode()!=null&&!dto.getSdwbkreasoncode().equals("")){
				sql+="dwbk.S_DWBKREASONCODE='"+dto.getSdwbkreasoncode()+"' AND ";
			}
			if(dto.getSbiztype()!=null&&!dto.getSbiztype().equals("")){
				sql+="dwbk.S_BIZTYPE='"+dto.getSbiztype()+"' AND ";
			}
			if(dto.getSpackageno()!=null&&!dto.getSpackageno().equals("")){
				sql+="dwbk.S_PACKAGENO='"+dto.getSpackageno()+"' AND ";
			}
			
			if(dto.getSfilename()!=null&&!dto.getSfilename().equals("")){
				sql+="dwbk.S_FILENAME='"+dto.getSfilename()+"' AND ";
			}
			if(null != dto.getFamt() && !"".equals(dto.getFamt())) {
				sql+="dwbk.F_AMT="+dto.getFamt()+" AND ";
			}
			if(dto.getSbookorgcode()!=null&&!dto.getSbookorgcode().equals("")){
				sql+="dwbk.S_BOOKORGCODE='"+dto.getSbookorgcode()+"' ";
			}
			
			rs = sqlExecutor.runQueryCloseCon(sql,TvDwbkReportDto.class);
		}catch(JAFDatabaseException ex){
			log.error("��ѯ�˿�ҵ������ʱ�����쳣!",ex);
			throw new ITFEBizException("��ѯ�˿�ҵ������ʱ�����쳣!",ex);
		}
		list.addAll(rs.getDtoCollection());
		return list;
	}


	public void updateSuccess(List dtoInfos) throws ITFEBizException {
		if(ITFECommonConstant.IFNEWINTERFACE.equals("1")&&!"000001900000".equals(ITFECommonConstant.SRC_NODE)){
    		throw new ITFEBizException("�°�TIPS�ӿ��Զ����´�״̬���������ֶ����£�");
    	}
		try {
			DatabaseFacade.getDb().update(CommonUtil.listTArray(dtoInfos));
		} catch (JAFDatabaseException e1) {
			log.error(e1);
			throw new ITFEBizException("������������ʧ��", e1);
		}
		
	}


	public void setback(List dtolist) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().update(CommonUtil.listTArray(dtolist));
		} catch (JAFDatabaseException e1) {
			log.error(e1);
			throw new ITFEBizException("������������ʧ��", e1);
		}
	}


	public String exportfile(IDto finddto, String selectedtable) throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		try {
			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			TvDwbkDto mdto=(TvDwbkDto)finddto;
			if(selectedtable.equals("0")){
				mdto.setSbookorgcode(orgcode);
				list=CommonFacade.getODB().findRsByDto(mdto);
			}else if(selectedtable.equals("1")){
				HtvDwbkDto hdto= new HtvDwbkDto();
				hdto.setSdealno(mdto.getSdealno());
				hdto.setSpackageno(mdto.getSpackageno());
				hdto.setStaxorgcode(mdto.getStaxorgcode());
				hdto.setDaccept(mdto.getDaccept());
				hdto.setSbgttypecode(mdto.getSbgttypecode());
				hdto.setSdwbkvoucode(mdto.getSdwbkvoucode());
				hdto.setSstatus(mdto.getSstatus());
				hdto.setFamt(mdto.getFamt());
				hdto.setSbookorgcode(mdto.getSbookorgcode());
				list=CommonFacade.getODB().findRsByDto(hdto);
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep +StringUtil.replace(String.valueOf(mdto.getDaccept()), "-", "")+"_3140_"+ new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+"13.txt";
			String splitSign = ",";//"\t"; // �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					for (TvDwbkDto _dto :(List<TvDwbkDto>) list) {
						filebuf.append(_dto.getDacct());//ί������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdwbkvoucode());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxorgcode());//���ջ��ش���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayertrecode());//�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getCbdglevel());//Ԥ�㼶��
						filebuf.append(splitSign);
						filebuf.append(_dto.getCbdgkind());//Ԥ������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbdgsbtcode());//��Ŀ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getStbsastflag()==null ? "" : _dto.getStbsastflag());//������־
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdwbkreasoncode());//�˿�ԭ�����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSetpcode());//��ҵ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacct());//�տ��˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeename());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeecode());//�տλ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeopnbnkno());//�տ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getFamt());//���
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdemo()==null ? "" : _dto.getSdemo());//ԭ��
						filebuf.append("\r\n");
					}
				}else if(selectedtable.equals("1")){
					for (HtvDwbkDto _dto :(List<HtvDwbkDto>) list) {
						filebuf.append(_dto.getDacct());//ί������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdwbkvoucode());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxorgcode());//���ջ��ش���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayertrecode());//�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getCbdglevel());//Ԥ�㼶��
						filebuf.append(splitSign);
						filebuf.append(_dto.getCbdgkind());//Ԥ������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbdgsbtcode());//��Ŀ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getStbsastflag()==null ? "" : _dto.getStbsastflag());//������־
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdwbkreasoncode());//�˿�ԭ�����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSetpcode());//��ҵ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeacct());//�տ��˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeename());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeecode());//�տλ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeeopnbnkno());//�տ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getFamt());//���
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdemo()==null ? "" : _dto.getSdemo());//ԭ��
						filebuf.append("\r\n");
					}
				}
			
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
			    return fullpath.replaceAll(root, "");
				
			} else {
				throw new ITFEBizException("��ѯ������");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

}