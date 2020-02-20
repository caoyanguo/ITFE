package com.cfcc.itfe.service.dataquery.trincomereport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TnLedgerdataDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * @author db2admin
 * @time   14-06-17 16:33:50
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class TrIncomeReportService extends AbstractTrIncomeReportService {
	private static Log log = LogFactory.getLog(TrIncomeReportService.class);	


	/**
	 * �����౨���ļ�����
	 	 
	 * @generated
	 * @param fileList
	 * @param dto
	 * @throws ITFEBizException	 
	 */
	public void importFile(List fileList, IDto dto ,String reportStyle) throws ITFEBizException {
      String errorInfo="";
      for (Object object : fileList) {
			String filePath=ITFECommonConstant.FILE_ROOT_PATH+(String) object;
			try {
				//���տո�" "��ȡ�ļ���������ȡ��ÿ�м�¼�洢���б���
				List<String[]> fileContent = FileUtil.getInstance().readFileWithLine(filePath, " ");
				List<IDto> list = new ArrayList<IDto>();
				if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_INCOME)){
					errorInfo="TCBS�����ձ��ļ�";
					list = analysisIncomeData(fileContent,dto);
			  	}else if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_STOCK)){
			  		errorInfo="TCBS����ձ��ļ�";
			  		list = analysisStockData(fileContent,dto);
			  	}else
			  	{
			  		errorInfo="TCBS�ֻ����ļ�";
			  		list = analysisLedgerData(fileContent,dto);
			  	}
				if(list!=null&&list.size()>0){
					DatabaseFacade.getODB().create(CommonUtil.listTArray(list));
				}
			} catch (FileOperateException e) {
				log.error(e);
				throw new ITFEBizException("��ȡ"+errorInfo+"����"+e.getMessage());
			} catch (JAFDatabaseException e) {
				log.error(e);
				throw new ITFEBizException("��ȡ"+errorInfo+"����"+e.getMessage());
			}
		}
      
    }
    
    
    public List<IDto> analysisIncomeData(List<String[]> fileContent,IDto dto) throws ITFEBizException{
    	List<IDto> dtos = new ArrayList<IDto>();
    	try {
			TrIncomedayrptDto _dto = (TrIncomedayrptDto) dto;
			for(int i=0;i<fileContent.size();i++){
				String[] title = getColumnsContent(fileContent.get(0));
				if(!title[0].contains("�����ձ���")){
					throw new ITFEBizException("������ļ����������ձ���" );
				}
				String[] cols  = getColumnsContent(fileContent.get(i));
				if(cols[0].matches("[0-9]+")){
					TrIncomedayrptDto incomedayrptDto = new TrIncomedayrptDto();
					incomedayrptDto=(TrIncomedayrptDto) _dto.clone();
					incomedayrptDto.setSbudgetsubcode(cols[0]);
					incomedayrptDto.setSbudgetsubname(cols[1]);
					incomedayrptDto.setNmoneyday(new BigDecimal(cols[2]));
					incomedayrptDto.setNmoneytenday(new BigDecimal("0.00"));
					incomedayrptDto.setNmoneymonth(new BigDecimal(cols[3]));
					incomedayrptDto.setNmoneyquarter(new BigDecimal("0.00"));
					incomedayrptDto.setNmoneyyear(new BigDecimal(cols[4]));
					deleteRepeateData(incomedayrptDto);//ɾ���ظ�����
					dtos.add(incomedayrptDto);
				}
			}
    	} catch (ITFEBizException e) {
			log.error(e);
			throw new ITFEBizException("����TCBS�����ձ��ļ�����"+e.getMessage());
		}
		return dtos;
		
	}
    
    public List<IDto> analysisStockData(List<String[]> fileContent,IDto dto) throws ITFEBizException{
		List<IDto> dtos = new ArrayList<IDto>();
		try {
			TrStockdayrptDto _dto = (TrStockdayrptDto) dto;
			for (int i = 0; i < fileContent.size(); i++) {
				String[] title = getColumnsContent(fileContent.get(0));
				if(!title[0].contains("��������ձ���")){
					throw new ITFEBizException("������ļ����ǲ�������ձ���" );
				}
				String[] cols = getColumnsContent(fileContent.get(i));
				if(cols[0].matches("[0-9]+")){
					TrStockdayrptDto stockdayrptDto = new TrStockdayrptDto();
					stockdayrptDto = (TrStockdayrptDto) _dto.clone();
					if(cols.length!=5){
						throw new ITFEBizException("����Ŀ���ձ��ļ���ʽ����ȷ��" );
					}
					stockdayrptDto.setSaccno(cols[0]);
					stockdayrptDto.setSaccname(stockdayrptDto.getSaccname()==null?"":stockdayrptDto.getSaccname());
					stockdayrptDto.setNmoneyyesterday(new BigDecimal(cols[1]));
					stockdayrptDto.setNmoneyout(new BigDecimal(cols[2]));
					stockdayrptDto.setNmoneyin(new BigDecimal(cols[3]));
					stockdayrptDto.setNmoneytoday(new BigDecimal(cols[4]));
					deleteRepeateData(stockdayrptDto);//ɾ���ظ�����
					dtos.add(stockdayrptDto);
				}
			}	
		} catch (ITFEBizException e) {
			log.error(e);
			throw new ITFEBizException("����TCBS����ձ��ļ�����"+e.getMessage());
		}
		return dtos;
		
	}
    
    public List<IDto> analysisLedgerData(List<String[]> fileContent,IDto dto) throws ITFEBizException{
    	List<IDto> dtos = new ArrayList<IDto>();
    	SQLExecutor execDetail = null;
    	try {
    		TnLedgerdataDto basedto = (TnLedgerdataDto) dto;
    		Map<String,TsInfoconnorgaccDto> tempMap = getAcctCache(basedto.getSorgcode());
    		TnLedgerdataDto insertdto = null;
			Pattern patt = Pattern.compile("[0-9]{1,2}");// ƥ��С��3λ����
			for(int i=0;i<fileContent.size();i++){
				
				String[] cols  = getColumnsContent(fileContent.get(i));
				List<String> templist = new ArrayList<String>();
				for(String temp:cols)
				{
					if(temp!=null&&!"".equals(temp))
						templist.add(temp);
				}
				cols = new String[templist.size()];
				cols = templist.toArray(cols);
				if(i==0&&!cols[1].contains("�ֻ���")){
					throw new ITFEBizException("������ļ����Ƿֻ������ݣ�" );
				}else if(cols[0].contains("��")&&cols[1].contains("�ţ�"))
				{
					basedto.setSacctno(cols[2]);
				}else if(cols[0].contains("��")&&cols[1].contains("����"))
				{
					basedto.setSext4(cols[2]);//����
				}else if(i==3&&cols[0].length()==4)
				{
					basedto.setSext1(cols[0]);//��
				}else if(patt.matcher(cols[0]).matches()==true&&patt.matcher(cols[1]).matches()==true)
				{
					if(tempMap.get(basedto.getSacctno())==null)
						continue;
					insertdto = new TnLedgerdataDto();
					insertdto.setSdealno(VoucherUtil.getGrantSequence());
					insertdto.setSorgcode(basedto.getSorgcode());
					insertdto.setStrecode(basedto.getStrecode());
					insertdto.setSacctno(basedto.getSacctno());
					insertdto.setSacctname(basedto.getSext2());
					insertdto.setSext4(basedto.getSext4());
					insertdto.setSacctdate(basedto.getSext1()+(cols[0].length()==1?"0"+cols[0]:cols[0])+(cols[1].length()==1?"0"+cols[1]:cols[1]));
					insertdto.setSdemo(cols[2]);
					if(insertdto.getSdemo().contains("��ת")||insertdto.getSdemo().contains("�н�"))
					{
						if(cols.length==5)
						{
							insertdto.setSdebitmark(cols[3]);
							insertdto.setNamt(new BigDecimal(cols[4]));
						}else
							continue;
					}else
					{
						insertdto.setSvoucherno(cols[3]);
						insertdto.setSdebitmark(cols[5]);
						insertdto.setNamt(new BigDecimal(cols[6]));
						if(dtos!=null&&dtos.size()>0)
						{
							if(insertdto.getNamt().compareTo(((TnLedgerdataDto)dtos.get(dtos.size()-1)).getNamt())<0)
								insertdto.setNdebitamt(new BigDecimal(cols[4]));
							else
								insertdto.setNcreditamt(new BigDecimal(cols[4]));
						}else if(dtos!=null&&dtos.size()==0)
						{
							String sql = "select * from TN_LEDGERDATA where S_TRECODE=? and S_ACCTNO=?  order by S_DEALNO desc fetch first 1 rows only";
							if(execDetail==null)
								execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
							execDetail.addParam(basedto.getStrecode());
							execDetail.addParam(basedto.getSacctno());
							List<TnLedgerdataDto> selist = (List<TnLedgerdataDto>)execDetail.runQuery(sql,TnLedgerdataDto.class).getDtoCollection();
							if(selist!=null&&selist.size()>0)
							{
								if(insertdto.getNamt().compareTo(((TnLedgerdataDto)dtos.get(dtos.size()-1)).getNamt())<0)
									insertdto.setNdebitamt(new BigDecimal(cols[4]));
								else
									insertdto.setNcreditamt(new BigDecimal(cols[4]));
							}else
							{
								insertdto.setNcreditamt(new BigDecimal(cols[4]));
							}
						}
					}
					
					dtos.add(insertdto);
				}
			}
			if(dtos!=null&&dtos.size()>0)
			{
				String sql = "delete from TN_LEDGERDATA where S_TRECODE=? and S_ACCTNO=? and S_ACCTDATE>=? and S_ACCTDATE<=?";
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				execDetail.addParam(basedto.getStrecode());
				execDetail.addParam(basedto.getSacctno());
				execDetail.addParam(((TnLedgerdataDto)dtos.get(0)).getSacctdate());
				execDetail.addParam(((TnLedgerdataDto)dtos.get(dtos.size()-1)).getSacctdate());
				execDetail.runQuery(sql);
			}else
			{
				throw new ITFEBizException("�ļ�Ϊ���ļ������ļ���û�кϷ������ݣ�" );
			}
    	} catch (ITFEBizException e) {
			log.error(e);
			throw new ITFEBizException("����TCBS�ֻ����ļ�����"+e.getMessage());
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("����TCBS�ֻ����ļ�����"+e.getMessage());
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return dtos;
		
	}
    
	/**
	 * ��ȡ������
	 * @param cols
	 * @return
	 */
	public String[] getColumnsContent(String[] cols){
		StringBuffer rows = new StringBuffer();
		for(int i = 0 ; i < cols.length ; i ++){
			if(cols[i].trim().length() != 0)
				rows.append(cols[i].trim().replaceAll("\"", "").replaceAll(",", "").trim()).append(",");			
		}return rows.toString().trim().split(",");
	}

	public static Map<String,TsInfoconnorgaccDto> getAcctCache(String orgcode)
	{
		Map<String,TsInfoconnorgaccDto> getMap = null;
		String sql = "SELECT * FROM TS_INFOCONNORGACC WHERE S_ORGCODE=?";
		SQLExecutor sqlExe = null;
		try {
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe.addParam(orgcode);
			List<TsInfoconnorgaccDto> templist = (List<TsInfoconnorgaccDto>)sqlExe.runQueryCloseCon(sql,TsInfoconnorgaccDto.class).getDtoCollection();
			if(templist!=null&&templist.size()>0)
			{
				TsInfoconnorgaccDto dto = null;
				getMap = new HashMap<String,TsInfoconnorgaccDto>();
				for(int i=0;i<templist.size();i++)
				{
					dto = templist.get(i);
					getMap.put(dto.getSpayeraccount(), dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("���ɿ���˻����ʵ��������ձ����п���˻�ʧ�ܣ�", e);
		}finally{
			if(sqlExe!=null)
				sqlExe.closeConnection();
		}
		
		return getMap;
	}
	/**
	 * ɾ���ظ�����
	 * @param dto
	 * @throws ITFEBizException
	 */
	private void deleteRepeateData(IDto _dto) throws ITFEBizException{
		IDto delDto=null;
		if(_dto instanceof TrIncomedayrptDto){
			TrIncomedayrptDto dto = (TrIncomedayrptDto) _dto;
			TrIncomedayrptDto deleteDto=new TrIncomedayrptDto();
		    deleteDto.setSfinorgcode(dto.getSfinorgcode());
		    deleteDto.setStaxorgcode(dto.getStaxorgcode());
		    deleteDto.setStrecode(dto.getStrecode());
		    deleteDto.setSrptdate(dto.getSrptdate());
		    deleteDto.setSbudgettype(dto.getSbudgettype());
			deleteDto.setSbudgetlevelcode(dto.getSbudgetlevelcode());
			deleteDto.setSbudgetsubcode(dto.getSbudgetsubcode());
			deleteDto.setStrimflag(dto.getStrimflag());
			deleteDto.setSdividegroup(dto.getSdividegroup());
			deleteDto.setSbillkind(dto.getSbillkind());
			delDto = deleteDto;
		}else if(_dto instanceof TrStockdayrptDto){
			TrStockdayrptDto dto = (TrStockdayrptDto) _dto;
			TrStockdayrptDto deleteDto=new TrStockdayrptDto();
			deleteDto.setSorgcode(dto.getSorgcode());
			deleteDto.setStrecode(dto.getStrecode());
			deleteDto.setSrptdate(dto.getSrptdate());
			deleteDto.setSaccno(dto.getSaccno());
			deleteDto.setSaccdate(dto.getSaccdate());
			delDto = deleteDto;
		}
		try {
			DatabaseFacade.getODB().delete(delDto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("ɾ���ظ������쳣",e);
		}
	}

}