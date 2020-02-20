package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * �����ȶ��ˣ�3503��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3503 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3401.class);
	private  BigDecimal Total;
	private  String voucher_Sql="SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? "+ 
								"AND S_TRECODE= ?  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' " +
								"AND S_CREATDATE BETWEEN ? AND ?";		
	private  String MainDto_Sql_5106="SELECT * FROM TV_GRANTPAYMSGMAIN WHERE I_VOUSRLNO IN("+voucher_Sql+")";
	private  String MainDto_Sql_5108="SELECT * FROM TV_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO IN("+voucher_Sql+")";
	private String CheckAccount_Finance_Sql_5106="SELECT * FROM (SELECT S_BUDGETUNITCODE,S_TRANSACTUNIT,S_FUNSUBJECTCODE,sum(N_MONEY),'5106' FROM("+
											"SELECT a.S_TRANSACTUNIT S_TRANSACTUNIT,b.S_BUDGETUNITCODE S_BUDGETUNITCODE ," +
											"b.S_FUNSUBJECTCODE S_FUNSUBJECTCODE,b.N_MONEY N_MONEY FROM ("+MainDto_Sql_5106+
											") a,TV_GRANTPAYMSGSUB b WHERE a.I_VOUSRLNO=b.I_VOUSRLNO AND a.S_PACKAGETICKETNO = b.S_PACKAGETICKETNO)GROUP BY S_BUDGETUNITCODE , S_TRANSACTUNIT , S_FUNSUBJECTCODE "+
											") ORDER BY S_BUDGETUNITCODE , S_TRANSACTUNIT , S_FUNSUBJECTCODE";	
	private String CheckAccount_Finance_Sql_5108="SELECT * FROM (SELECT S_BUDGETUNITCODE,S_TRANSACTUNIT,S_FUNSUBJECTCODE,sum(N_MONEY),'5108' FROM("+
											"SELECT a.S_TRANSACTUNIT S_TRANSACTUNIT,b.S_BUDGETUNITCODE S_BUDGETUNITCODE ," +
											"b.S_FUNSUBJECTCODE S_FUNSUBJECTCODE,b.N_MONEY N_MONEY FROM ("+MainDto_Sql_5108+
											") a,TV_DIRECTPAYMSGSUB b WHERE a.I_VOUSRLNO=b.I_VOUSRLNO)GROUP BY S_BUDGETUNITCODE , S_TRANSACTUNIT , S_FUNSUBJECTCODE "+
											") ORDER BY S_BUDGETUNITCODE , S_TRANSACTUNIT , S_FUNSUBJECTCODE";	
	private String VoucherCheckNo;//���˵���
	private int DetailChildPackNum=500;//��ϸ�ְ���
	private int ChildPackNum;//�Ӱ�����
	private int CurPackNo=0;//�������
	private int count=0;//����ƾ֤����
	
	/**
	 * ƾ֤����
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		String result=verifyVoucherCount(vDto);
		if(result==null||result.equals(""))
			return null;		
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		String[] results=result.split("#");
		List lists=new ArrayList();
		List list=new ArrayList();	
		if(results.length==1){				
			list.addAll(getCheckAccountFinanceResult(vDto, results[0]));
			lists.add(list);		
		}else{	
			list.addAll(getCheckAccountFinanceResult(vDto, MsgConstant.VOUCHER_NO_5106));			
			list.addAll(getCheckAccountFinanceResult(vDto, MsgConstant.VOUCHER_NO_5108));
			lists.add(list);			
		}voucherTranfor(lists, vDto);
		lists.clear();
		lists.add(count);
		return lists;	
	}
	
	/**
	 * ����ƾ֤��ϸ��������ƾ֤
	 * @param lists
	 * @param vDto
	 * @throws ITFEBizException
	 */
	public void voucherTranfor(List lists,TvVoucherinfoDto vDto) throws ITFEBizException{
		for(List list:(List<List>)lists){			
			List fatherPackList=getListByChildPackNum(list);
			VoucherCheckNo=VoucherUtil.getGrantSequence();
			for(List chilPackList:(List<List>)fatherPackList){
				CurPackNo++;
				voucherTranfor(vDto, chilPackList);
				count++;
				if(CurPackNo==fatherPackList.size())
					CurPackNo=0;
			}				
		}
	}
	
	/**
	 * ����ƾ֤��ϸ����
	 * @param list
	 * @return
	 */
	public List getListByChildPackNum(List<List> list){
		List lists=new ArrayList();				
		int count=list.size()/DetailChildPackNum;
		int mod=list.size()%DetailChildPackNum;		
		List detailList;
		for(int i=0;i<count+1;i++){
			if(mod==0&&i==count)
				break;
			if(mod!=0&&i==count)
				lists.add(list.subList(DetailChildPackNum*i, list.size()));				
			else				
				lists.add(list.subList(DetailChildPackNum*i, count==0?list.size():DetailChildPackNum*(i+1)));
		}ChildPackNum=lists.size();
		return lists;
	}
	
	/**
	 * ����ƾ֤
	 * ����ƾ֤����
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	public void voucherTranfor(TvVoucherinfoDto vDto,List list) throws ITFEBizException {
		List lists=new ArrayList();
		lists=(List) list.get(0);
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();		
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");		
		dto.setShold2("");
		String mainvou=VoucherUtil.getGrantSequence();
		dto.setSdealno(mainvou);		
		dto.setSvoucherno(mainvou+"_"+VoucherCheckNo.substring(12, VoucherCheckNo.length()));
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));	
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");		
		lists=new ArrayList();
		lists.add(dto);
		lists.add(list);
		Total=new BigDecimal("0.00");
		Map map=tranfor(lists);
		dto.setNmoney(Total);
		try {
			VoucherUtil.sendTips(dto, map);
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("ƾ֤д�����ݿ��쳣��",e);
		}
	}

	/**
	 * DTOת��XML����
	 * 
	 * @param List
	 *            	���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor(List lists) throws ITFEBizException{
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);
			List<List> resultList=(List) lists.get(1);	
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getSstyear());		
			vouchermap.put("VtCode", vDto.getSvtcode());		
			vouchermap.put("VouDate", vDto.getScreatdate());		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("VoucherCheckNo", VoucherCheckNo);
			vouchermap.put("ChildPackNum", ChildPackNum);
			vouchermap.put("CurPackNo", CurPackNo);
			vouchermap.put("TreCode", vDto.getStrecode());		
			vouchermap.put("ClearBankCode", "");
			vouchermap.put("ClearBankName", "");
			vouchermap.put("ClearAccNo", "");
			vouchermap.put("ClearAccNanme", "");
			vouchermap.put("BeginDate", vDto.getShold3());
			vouchermap.put("EndDate", vDto.getShold4());
			vouchermap.put("XCheckResult", ""); 	
			vouchermap.put("XCheckReason", "");
			vouchermap.put("Hold1", "");		
			vouchermap.put("Hold2", "");					
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();
			for(int i=0;i<resultList.size();i++){
				List list=resultList.get(i);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", VoucherUtil.getDetailSequence(vDto.getSdealno(), i+1));				
				Detailmap.put("SupDepCode", list.get(0));
				Detailmap.put("SupDepName", PublicSearchFacade.findSupDepCodeBySupDepName(vDto.getSorgcode(),vDto.getStrecode(),list.get(0)+""));
				Detailmap.put("FundTypeCode",""); 
				Detailmap.put("FundTypeName", ""); 
				Detailmap.put("PayBankCode", ""); 
				Detailmap.put("PayBankName", ""); 
				Detailmap.put("PayBankNo", list.get(1)); 	
				Detailmap.put("ExpFuncCode", list.get(2));
				Detailmap.put("ExpFuncName", PublicSearchFacade.findExpFuncNameByExpFuncCode(vDto.getSorgcode(),list.get(2)+""));
				Detailmap.put("ProCatCode", ""); 
				Detailmap.put("ProCatName", "");
				if(((list.get(4)+"").equals(MsgConstant.VOUCHER_NO_5106))){
					Detailmap.put("PayTypeCode", "12"); 
					Detailmap.put("PayTypeName", "��Ȩ֧��"); 
				}else{
					Detailmap.put("PayTypeCode", "11"); 
					Detailmap.put("PayTypeName", "ֱ��֧��"); 
				}
				Detailmap.put("ClearAmt", list.get(3));
				Detailmap.put("XCheckResult", "");
				Detailmap.put("XCheckReason", "");
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", "");					
				Detail.add(Detailmap);
				Total=Total.add((BigDecimal) list.get(3));
			}
			vouchermap.put("AllAmt", MtoCodeTrans.transformString(Total));
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;		
		}catch(ITFEBizException e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage());
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��",e);
		}
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	
	/**
	 * У��ƾ֤��������ҵ�������¼�Ƿ����
	 * 
	 * @param List
	 * @return
	 * @throws ITFEBizException
	 */
	public String verifyVoucherCount(TvVoucherinfoDto dto) throws ITFEBizException{		
		SQLExecutor sqlExecutor=null;
		String svtcode=null;
		String result="";		
		try {			
			for(int i=0;i<2;i++){
				sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				sqlExecutor.clearParams();
				if(i==0){
					sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5106);
					svtcode=MsgConstant.VOUCHER_NO_5106;
					
				}else{
					sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5108);
					svtcode=MsgConstant.VOUCHER_NO_5108;					
				}
				sqlExecutor.addParam(dto.getSorgcode());
				sqlExecutor.addParam(dto.getStrecode());
				sqlExecutor.addParam(dto.getShold3());
				sqlExecutor.addParam(dto.getShold4());
				SQLResults rs = sqlExecutor.runQueryCloseCon(voucher_Sql);
				if(rs!=null&&rs.getRowCount()>0){
					if(verifyMainDtoCount(rs.getRowCount(), dto,svtcode)){
						result=result+svtcode+"#";
					}
				}if(sqlExecutor!=null){
					sqlExecutor.closeConnection();
				}
			}
			
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����",e);
		}return result;
	}
	
	/**
	 * У��ҵ�������¼�Ƿ���������ʷ��
	 * @param count
	 * @param dto
	 * @param svtcode
	 * @return
	 * @throws ITFEBizException
	 */
	public boolean verifyMainDtoCount(int count,TvVoucherinfoDto dto,String svtcode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String sql=null;
		String err=null;
		try {			
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.clearParams();
			if(svtcode.equals(MsgConstant.VOUCHER_NO_5106)){				
				sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5106);
				sql=MainDto_Sql_5106;
				err="����ƾ֤�������¼��ѯ \"��Ȩ֧�����ҵ������\" ��Ϣ����\t\n�������¼��ҵ�������¼����ȣ�\t\n\"��Ȩ֧�����ҵ������\" ���ּ�¼������������ʷ��";
			}else{				
				sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5108);
				sql=MainDto_Sql_5108;
				err="����ƾ֤�������¼��ѯ \"ֱ��֧�����ҵ������\" ��Ϣ����\t\n�������¼��ҵ�������¼����ȣ�\t\n\"ֱ��֧�����ҵ������\" ���ּ�¼������������ʷ��";
			}
			sqlExecutor.addParam(dto.getSorgcode());
			sqlExecutor.addParam(dto.getStrecode());
			sqlExecutor.addParam(dto.getShold3());
			sqlExecutor.addParam(dto.getShold4());
			SQLResults rs = sqlExecutor.runQueryCloseCon(sql);
			if(rs!=null&&rs.getRowCount()>0){
				if(svtcode.equals(MsgConstant.VOUCHER_NO_5106)){
					HashSet set=new HashSet();
					for(int i=0;i<rs.getRowCount();i++){
						set.add(rs.getString(i, 0));
					}if(set.size()!=count){
						throw new ITFEBizException(err,new Exception(err));
					}
				}else{
					if(count!=rs.getRowCount()){
						throw new ITFEBizException(err,new Exception(err));
					}
				}
				return true;
			}					
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����",e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}finally{
			if(sqlExecutor!=null){
				sqlExecutor.closeConnection();
			}
		}
		return false;
	}
		
	/**
	 * ��ѯ����ƾ֤��Ϣ
	 * @param dto
	 * @param svtcode
	 * @return
	 * @throws ITFEBizException
	 */
	public List getCheckAccountFinanceResult(TvVoucherinfoDto dto,String svtcode) throws ITFEBizException {
		SQLExecutor sqlExecutor=null;
		SQLResults rs =null;
		List lists=new ArrayList();		
		try {
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.clearParams();		
			if(svtcode.equals(MsgConstant.VOUCHER_NO_5106)){						
				sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5106);
				sqlExecutor.addParam(dto.getSorgcode());
				sqlExecutor.addParam(dto.getStrecode());
				sqlExecutor.addParam(dto.getShold3());
				sqlExecutor.addParam(dto.getShold4());
				rs = sqlExecutor.runQueryCloseCon(CheckAccount_Finance_Sql_5106);
			}else{				
				sqlExecutor.addParam(MsgConstant.VOUCHER_NO_5108);
				sqlExecutor.addParam(dto.getSorgcode());
				sqlExecutor.addParam(dto.getStrecode());
				sqlExecutor.addParam(dto.getShold3());
				sqlExecutor.addParam(dto.getShold4());
				rs = sqlExecutor.runQueryCloseCon(CheckAccount_Finance_Sql_5108);
			}			
			if(rs!=null&&rs.getRowCount()>0){
				for(int i=0;i<rs.getRowCount();i++){
					List list=new ArrayList();
					list.add(rs.getString(i, 0));
					list.add(rs.getString(i, 1));
					list.add(rs.getString(i, 2));				
					list.add(rs.getBigDecimal(i, 3));
					list.add(rs.getString(i, 4));
					lists.add(list);
				}
			}		
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}finally{
			if(sqlExecutor!=null){
				sqlExecutor.closeConnection();
			}
		}		
		return lists;
	}
	

}
