package com.cfcc.itfe.service.dataquery.trecodelargetaxandpaytable;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.core.exception.FileOperateException;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author db2admin
 * @time   14-06-05 16:56:38
 * codecomment: 
 */

public class TrecodeLargeTaxAndPayTableService extends AbstractTrecodeLargeTaxAndPayTableService {
	private static Log log = LogFactory.getLog(TrecodeLargeTaxAndPayTableService.class);	


	/**
	 * ���˰Դ֧��ͳ�Ʊ��ѯ��ҳ
	 	 
	 * @generated
	 * @param queryStyle
	 * @param request
	 * @param IDto
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    @SuppressWarnings("unchecked")
	public PageResponse queryByPage(String queryStyle, PageRequest request, IDto IDto) throws ITFEBizException {
    	PageResponse response = new PageResponse(request);
    	StringBuffer sqlwhere = new StringBuffer("");
    	String columns="";
    	Class clz=null;
    	if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
    		clz=TvFinIncomeonlineDto.class;
    		TvFinIncomeonlineDto finIncomeOnlineDto=(TvFinIncomeonlineDto) IDto;
    		BigDecimal setMoney = finIncomeOnlineDto.getFtraamt();//�趨���
    		String trecode = finIncomeOnlineDto.getStrecode();//�������
    		String inDate=finIncomeOnlineDto.getSapplydate();//�������
    		String bdgsubCode=finIncomeOnlineDto.getSbdgsbtcode();//˰�տ�Ŀ����
    		String ofDate=finIncomeOnlineDto.getSbilldate();//˰����������
    	    columns="A.S_TRECODE AS strecode , B.S_INTREDATE AS sapplydate , A.S_TAXPAYNAME AS staxpayname , A.S_BDGSBTCODE AS sbdgsbtcode , A.S_BDGSBTNAME AS sbdgsbtname , A.C_BDGLEVEL AS cbdglevel , A.F_TRAAMT AS ftraamt , A.S_BILLDATE AS sbilldate , A.S_REMARK AS sremark";
    		sqlwhere.append("SELECT ")
    		.append(columns)
    		.append(" FROM TV_FIN_INCOMEONLINE A , TV_FIN_INCOME_DETAIL B WHERE ");
    		if(!StringUtils.isBlank(trecode)){
    			sqlwhere.append(" A.S_TRECODE = '")
        		.append(trecode)
        		.append("' ")
        		.append(" AND B.S_TRECODE = '")
        		.append(trecode)
        		.append("' ");
    		}
    		if(!StringUtils.isBlank(bdgsubCode)){
    			sqlwhere.append(" AND A.S_BDGSBTCODE = '")
        		.append(bdgsubCode)
        		.append("' ")
        		.append(" AND B.S_BDGSBTCODE = '")
        		.append(bdgsubCode)
        		.append("' ");
    		}
    		if(!StringUtils.isBlank(ofDate)){
    			sqlwhere.append(" AND A.S_BILLDATE = '")
        		.append(ofDate)
        		.append("' ");
    		}
    		if(!StringUtils.isBlank(inDate)){
    			sqlwhere.append(" AND B.S_INTREDATE = '")
        		.append(inDate)
        		.append("' ");
    		}
    		if(setMoney!=null){
    			sqlwhere.append(" AND A.F_TRAAMT >= ")
        		.append(setMoney)
        		.append(" AND B.F_AMT >= ")
        		.append(setMoney);
    		}
    		sqlwhere.append(" AND A.S_TRECODE = B.S_TRECODE")
    		.append(" AND A.S_BDGSBTCODE = B.S_BDGSBTCODE")
    		.append(" AND A.S_TAXVOUNO = B.S_EXPVOUNO")
    		.append(" AND A.F_TRAAMT = B.F_AMT")
    		.append(" WITH UR");
    		
    	}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
    		return response;
    	}else{
    		return response;
    	}

    	try {
			if (response.getTotalCount() == 0) {
				SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				// ��ʼ���ܼ�¼��
				String countsqlwhere = sqlwhere.toString().replace(columns, "count(*)");
				int count = sqlExe.runQueryCloseCon(countsqlwhere, clz).getInt(0, 0);
				response.setTotalCount(count);
			}
			SQLExecutor sqlExe2 = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
	
			SQLResults res = sqlExe2.runQueryCloseCon(sqlwhere.toString(), clz,
					request.getStartPosition(), request.getPageSize(), false);
			List list = new ArrayList();
			list.addAll(res.getDtoCollection());
			response.getData().clear();
			response.setData(list);
    	} catch (JAFDatabaseException e) {
    		throw new ITFEBizException("��ѯ���˰Դ֧��ͳ�Ʊ����"+e.getMessage());
		}
		return response;
    }

	/**
	 * ����
	 	 
	 * @generated
	 * @param queryStype
	 * @param list
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
    @SuppressWarnings("unchecked")
	public String exportFile(String queryStyle, List list) throws ITFEBizException {
    	String root = ITFECommonConstant.FILE_ROOT_PATH;
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String strdate = DateUtil.date2String2(new java.util.Date()); // ��ǰϵͳ������
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+".txt";
		String splitSign = ",";// �ļ���¼�ָ�����
		if (list.size() > 0) {
			StringBuffer filebuf = new StringBuffer();
			if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
				for (Object object : list) {
					TvFinIncomeonlineDto _dto=(TvFinIncomeonlineDto) object;
					filebuf.append(_dto.getStrecode());//�������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSapplydate());//�������
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxpayname());//��˰������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbdgsbtcode());//˰�տ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbdgsbtname());//˰�տ�Ŀ����
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbdglevelname());//Ԥ�㼶��
					filebuf.append(splitSign);
					filebuf.append(_dto.getFtraamt());//���
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbilldate());//��˰����������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSremark());//��ע
					filebuf.append("\r\n");
				}
			}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
				for (Object object : list) {
					TvPayoutmsgmainDto _dto=(TvPayoutmsgmainDto) object;
					filebuf.append(_dto.getStrecode());//�������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSgenticketdate());//֧������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetunitcode());//֧����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(_dto.getSunitcodename());//֧����Ŀ����
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrecacct());//�տ����˺�
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrecname());//�տ�������
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrecbankno());//�տ��˿���������
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());//���
					filebuf.append(splitSign);
					filebuf.append(_dto.getSaddword());//ժҪ
					filebuf.append("\r\n");
				}
			}
			File f = new File(fullpath);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(fullpath);
			}
			try {
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
			} catch (com.cfcc.itfe.exception.FileOperateException e) {
				log.error(e);
				throw new ITFEBizException("д�ļ�����",e);
			}
		    return fullpath.replaceAll(root, "");
		}else{
			throw new ITFEBizException("��ѯ������");
		}
    }

    
    /**
	 * ���˰Դ֧��ͳ�Ʊ��ѯ
	 	 
	 * @generated
	 * @param queryStyle
	 * @param IDto
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
	@SuppressWarnings("unchecked")
	public List queryByList(String queryStyle, IDto IDto)
			throws ITFEBizException {
		List<IDto> list = new ArrayList<IDto>();
    	StringBuffer sqlwhere = new StringBuffer("");
    	String columns="";
    	Class clz=null;
    	String orgCode="";
    	if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
    		clz=TvFinIncomeonlineDto.class;
    		TvFinIncomeonlineDto finIncomeOnlineDto=(TvFinIncomeonlineDto) IDto;
    		orgCode = finIncomeOnlineDto.getSorgcode();//�����������
    		BigDecimal setMoney = finIncomeOnlineDto.getFtraamt();//�趨���
    		String trecode = finIncomeOnlineDto.getStrecode();//�������
//    		String inDate=finIncomeOnlineDto.getSapplydate();//�������
    		String bdgsubCode=finIncomeOnlineDto.getSbdgsbtcode();//˰�տ�Ŀ����
//    		String ofDate=finIncomeOnlineDto.getSbilldate();//˰����������
    	    columns="A.S_TRECODE AS strecode , B.S_INTREDATE AS sapplydate , A.S_TAXPAYNAME AS staxpayname , A.S_BDGSBTCODE AS sbdgsbtcode , A.S_BDGSBTCODE AS sbdgsbtname , A.C_BDGLEVEL AS cbdglevel , A.F_TRAAMT AS ftraamt , A.S_BILLDATE AS sbilldate , A.S_REMARK AS sremark";
    		sqlwhere.append("SELECT DISTINCT ")
    		.append(columns)
    		.append(" FROM TV_FIN_INCOMEONLINE A , TV_FIN_INCOME_DETAIL B WHERE ");
    		if(!StringUtils.isBlank(trecode)){
    			sqlwhere.append(" A.S_TRECODE = '")
        		.append(trecode)
        		.append("' ");
    		}else
    		{
    			sqlwhere.append(" 1=1 ");
    		}
    		if(!StringUtils.isBlank(bdgsubCode)){
    			sqlwhere.append(" AND A.S_BDGSBTCODE = '")
        		.append(bdgsubCode)
        		.append("' ");
    		}
    		if(!StringUtils.isBlank(finIncomeOnlineDto.getSremark())){
    			sqlwhere.append(finIncomeOnlineDto.getSremark());
    		}
    		if(setMoney!=null){
    			sqlwhere.append(" AND A.F_TRAAMT >= ")
        		.append(setMoney);
    		}
    		sqlwhere.append(" AND A.S_TRECODE = B.S_TRECODE")
    		.append(" AND A.S_BDGSBTCODE = B.S_BDGSBTCODE")
    		.append(" AND A.S_TAXVOUNO = B.S_EXPVOUNO")
    		.append(" AND A.F_TRAAMT = B.F_AMT")
    		.append(" WITH UR");
    		
    	}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
    		clz=TvPayoutmsgmainDto.class;
    		TvPayoutmsgmainDto tvPayoutmsgmainDto=(TvPayoutmsgmainDto) IDto;
    		orgCode = tvPayoutmsgmainDto.getSorgcode();//�����������
    		BigDecimal setMoney = tvPayoutmsgmainDto.getNmoney();//�趨���
    		String trecode = tvPayoutmsgmainDto.getStrecode();//�������
//    		String payDate=tvPayoutmsgmainDto.getSgenticketdate();//֧������
    		String bdgsubCode=tvPayoutmsgmainDto.getSbudgetunitcode();//֧����Ŀ����
    		String recvAcct=tvPayoutmsgmainDto.getSrecacct();//�տ����˺�
    	    columns="A.S_TRECODE AS strecode , A.S_GENTICKETDATE AS sgenticketdate , B.S_FUNSUBJECTCODE AS sbudgetunitcode , B.S_FUNSUBJECTCODE AS sunitcodename , A.S_RECACCT AS srecacct , A.S_RECNAME AS srecname , A.S_RECBANKNO AS srecbankno , A.N_MONEY AS nmoney , A.S_ADDWORD AS saddword";
    		sqlwhere.append("SELECT ")
    		.append(columns)
    		.append(" FROM TV_PAYOUTMSGMAIN A , TV_PAYOUTMSGSUB B WHERE ");
    		if(!StringUtils.isBlank(orgCode)){
    			sqlwhere.append(" A.S_ORGCODE = '")
        		.append(orgCode)
        		.append("' ");
    		}
    		if(!StringUtils.isBlank(trecode)){
    			sqlwhere.append(" AND A.S_TRECODE = '")
        		.append(trecode)
        		.append("' ");
    		}
    		if(!StringUtils.isBlank(bdgsubCode)){
    			sqlwhere.append(" AND B.S_FUNSUBJECTCODE = '")
        		.append(bdgsubCode)
        		.append("' ");
    		}
    		if(!StringUtils.isBlank(tvPayoutmsgmainDto.getSaddword())){
    			sqlwhere.append(tvPayoutmsgmainDto.getSaddword());
    		}
    		if(!StringUtils.isBlank(recvAcct)){
    			sqlwhere.append(" AND A.S_RECACCT = '")
        		.append(recvAcct)
        		.append("' ");
    		}
    		if(setMoney!=null){
    			sqlwhere.append(" AND A.N_MONEY >= ")
        		.append(setMoney);
    		}
    		sqlwhere.append(" AND A.S_BIZNO = B.S_BIZNO")
    		.append(" WITH UR");
    	}else{
    		return list;
    	}

    	try {
			SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			SQLResults res = sqlExe.runQueryCloseCon(sqlwhere.toString(), clz);
			List<IDto> resList = new ArrayList<IDto>();
			resList.addAll(res.getDtoCollection());
			list.addAll(getSubjectName(queryStyle, resList, orgCode));
    	} catch (JAFDatabaseException e) {
    		throw new ITFEBizException("��ѯ���˰Դ֧��ͳ�Ʊ����"+e.getMessage());
		}
    	
		return list;
	}
	
	
	
	public List<IDto> getSubjectName(String queryStyle ,List<IDto> list,String orgCode) throws ITFEBizException, JAFDatabaseException{
		List<IDto> resultList = new ArrayList<IDto>();
		HashMap<String, TsBudgetsubjectDto> map=SrvCacheFacade.cacheTsBdgsbtInfo(orgCode);
		if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
			for (IDto dto : list) {
				TvFinIncomeonlineDto incomeDto = (TvFinIncomeonlineDto) dto;
				incomeDto.setSbdgsbtname(map.get(incomeDto.getSbdgsbtname()).getSsubjectname());
				resultList.add(incomeDto);
			}
		}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
			for (IDto dto : list) {
				TvPayoutmsgmainDto payoutDto = (TvPayoutmsgmainDto) dto;
				payoutDto.setSunitcodename(map.get(payoutDto.getSunitcodename()).getSsubjectname());
				resultList.add(payoutDto);
			}
		}
		return resultList;
		
	}

}