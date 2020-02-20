package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �������������֧����������˵���3551��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3551 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3551.class);

	/**
	 * ƾ֤����
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=findCamtkindDAndPayBankCode(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TnConpaycheckbillDto mainDto:(List<TnConpaycheckbillDto>)list){		
			lists.add(voucherTranfor(dto, findMainDto(dto, mainDto)));			
		}return voucherGenerate(lists);
	}
	
	/**
	 * ƾ֤���ķ���MQ��̨����
	 * ƾ֤д�����ݿ�
	 * @param lists
	 * @return
	 * @throws ITFEBizException
	 */
	private List voucherGenerate(List lists) throws ITFEBizException{
		for(List list:(List<List>)lists){
			VoucherUtil.sendTips((TvVoucherinfoDto)list.get(0), (Map)list.get(2));
			try {
				DatabaseFacade.getODB().create((TvVoucherinfoDto)list.get(0));
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("ƾ֤д�����ݿ��쳣��",e);
			}
		}int count=lists.size();
		lists.clear();
		lists.add(count);
		return lists;
	}
	
	/**
	 * ����ƾ֤
	 * ����ƾ֤����
	 * @param vDto
	 * @param list
	 * @throws ITFEBizException
	 */
	private List voucherTranfor(TvVoucherinfoDto vDto,List list) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();	
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");		
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));
		dto.setSvoucherno(dto.getSdealno());
		dto.setSpaybankcode(((TnConpaycheckbillDto)list.get(0)).getSbnkno());
		dto.setShold2((Integer.parseInt(((TnConpaycheckbillDto)list.get(0)).getCamtkind())-1)+"");
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");		
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(list);		
		lists.add(tranfor(lists));	
		return lists;
	}
	
	/**
	 * ������֯�������롢������롢���д��롢֧����ʽ����ʼ���ڡ���������
	 * ��ѯ����֧����ȶ��˵�ҵ�����Ϣ
	 * @param dto
	 * @param tnConpaycheckbillDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findMainDto(TvVoucherinfoDto dto,TnConpaycheckbillDto tnConpaycheckbillDto) throws ITFEBizException {		
		TnConpaycheckbillDto mainDto=new TnConpaycheckbillDto();
		mainDto.setSbookorgcode(dto.getSorgcode());
		mainDto.setStrecode(tnConpaycheckbillDto.getStrecode());
		mainDto.setSbnkno(tnConpaycheckbillDto.getSbnkno());
		mainDto.setCamtkind(tnConpaycheckbillDto.getCamtkind());
		mainDto.setDstartdate(CommonUtil.strToDate(dto.getShold3()));
		mainDto.setDenddate(CommonUtil.strToDate(dto.getShold4()));
		try {
			return CommonFacade.getODB().findRsByDto(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ����֧����ȶ��˵�ҵ�����Ϣ����",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ����֧����ȶ��˵�ҵ�����Ϣ����",e);
		}
	}
	
	/**
	 * ���Ҹ��ݹ�����롢֧����ʽ�����д������SQL���
	 * @param dto
	 * @return
	 */
	private String findCamtkindDAndPayBankCodeSQL(TvVoucherinfoDto dto){
		String sql= "SELECT S_TRECODE,C_AMTKIND,S_BNKNO FROM TN_CONPAYCHECKBILL ";
		sql+= "WHERE S_BOOKORGCODE='"+dto.getSorgcode()+"' ";
		sql+= dto.getStrecode()==null?"":"AND S_TRECODE='"+dto.getStrecode()+"' ";
		sql+= dto.getShold2()==null?"":"AND C_AMTKIND='"+(Integer.parseInt(dto.getShold2())+1)+"' ";
		sql+= dto.getSpaybankcode()==null?"":"AND S_BNKNO='"+dto.getSpaybankcode()+"' ";
		sql+= "AND D_STARTDATE='"+CommonUtil.strToDate(dto.getShold3())+"' AND D_ENDDATE='"+CommonUtil.strToDate(dto.getShold4())+"' ";
		return sql+= "GROUP BY S_TRECODE,C_AMTKIND,S_BNKNO ";
	}
	
	/**
	 * ���ҹ�����롢֧����ʽ�����д���
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findCamtkindDAndPayBankCode(TvVoucherinfoDto dto) throws ITFEBizException{
		try {
			SQLExecutor sqlExec= DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			SQLResults rs = sqlExec.runQueryCloseCon(findCamtkindDAndPayBankCodeSQL(dto),TnConpaycheckbillDto.class);			
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ����֧����ȶ��˵�ҵ�����Ϣ����",e);
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
			BigDecimal Total=new BigDecimal("0.00");
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);
			List<TnConpaycheckbillDto> resultList=(List) lists.get(1);
			//ȡ�ù�������
			String treName = BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()) == null ? "" : BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()).getStrename();
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("Id", vDto.getSdealno());//������˵�Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//������������
			vouchermap.put("StYear", vDto.getSstyear());//ҵ�����		
			vouchermap.put("VtCode", vDto.getSvtcode());//ƾ֤���ͱ��
			vouchermap.put("EVoucherType", ((TnConpaycheckbillDto) resultList.get(0)).getCamtkind());//���˵�����  1ֱ��֧������ 2��Ȩ֧������
			vouchermap.put("VouDate", vDto.getScreatdate());//ƾ֤����		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//ƾ֤��
			vouchermap.put("TreCode", vDto.getStrecode());//�������
			vouchermap.put("TreName", treName);//��������
			vouchermap.put("BeginDate", vDto.getShold3());//������ʼ����
			vouchermap.put("EndDate", vDto.getShold4());//������ֹ����
			vouchermap.put("AllNum", resultList.size());//��ϸ�ܱ���			
			vouchermap.put("PayBankCode", vDto.getSpaybankcode());//�������б���
			vouchermap.put("PayBankName", PublicSearchFacade.findPayBankNameByPayBankCode((TnConpaycheckbillDto) resultList.get(0)));//������������
			vouchermap.put("PayBankNo", vDto.getSpaybankcode());//���������к�
			vouchermap.put("Hold1", "");//Ԥ���ֶ�1		
			vouchermap.put("Hold2", "");//Ԥ���ֶ�2	
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();			
			for(TnConpaycheckbillDto mainDto:resultList){
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("SupDepCode", mainDto.getSbdgorgcode());//һ��Ԥ�㵥λ����
				Detailmap.put("SupDepName", mainDto.getSbdgorgname());//һ��Ԥ�㵥λ����
				Detailmap.put("ExpFuncCode",mainDto.getSfuncsbtcode());//֧�����ܷ����Ŀ���� 
				Detailmap.put("ExpFuncName", PublicSearchFacade.findExpFuncNameByExpFuncCode(mainDto)); //֧�����ܷ����Ŀ����
				Detailmap.put("PreDateMoney", MtoCodeTrans.transformString(mainDto.getFlastmonthzeroamt()));//���ڶ�����
				Detailmap.put("CurAddMoney", MtoCodeTrans.transformString(mainDto.getFcursmallamt()));//�������Ӷ��
				Detailmap.put("CurReckMoney", MtoCodeTrans.transformString(mainDto.getFcurreckzeroamt()));//������������
				Detailmap.put("CurDateMoney", MtoCodeTrans.transformString(mainDto.getFcurzeroamt()));//���ڶ�����
				Detailmap.put("XCheckResult", "");//���˽��
				Detailmap.put("XCurFinReckMoney", "");//���ڲ���������
				Detailmap.put("XCurFinAddMoney", "");//���ڲ������Ӷ�� 
				Detailmap.put("Remark", "");//��ע				
				Detailmap.put("Hold1", "");//Ԥ���ֶ�1 
				Detailmap.put("Hold2", "");//Ԥ���ֶ�2
				Detailmap.put("Hold3", "");//Ԥ���ֶ�3 
				Detailmap.put("Hold4", "");//Ԥ���ֶ�4 							
				Detail.add(Detailmap);
				Total=Total.add((BigDecimal)mainDto.getFcurzeroamt());
			}
			vouchermap.put("AllAmt", MtoCodeTrans.transformString(Total));//��ϸ�ܱ���
			vDto.setNmoney(Total);
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
	

}
