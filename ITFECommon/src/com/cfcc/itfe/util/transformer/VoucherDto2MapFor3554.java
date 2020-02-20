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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ��ҵ������˵���3554��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3554 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3554.class);

	/**
	 * ƾ֤����
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=new VoucherDto2MapFor3551().findCamtkindDAndPayBankCode(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TnConpaycheckbillDto mainDto:(List<TnConpaycheckbillDto>)list){		
			lists.add(voucherTranfor(dto, new VoucherDto2MapFor3551().findMainDto(dto, mainDto)));			
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
		dto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
		dto.setSdemo("����ɹ�");		
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(list);		
		lists.add(tranfor(lists));	
		return lists;
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
			vouchermap.put("BeginDate", vDto.getShold3());
			vouchermap.put("EndDate", vDto.getShold4());
			vouchermap.put("AllNum", resultList.size());//��ϸ�ܱ���			
			vouchermap.put("PayBankCode", vDto.getSpaybankcode());//�������б���
			vouchermap.put("PayBankName", PublicSearchFacade.findPayBankNameByPayBankCode((TnConpaycheckbillDto) resultList.get(0)));//������������
			vouchermap.put("PayBankNo", vDto.getSpaybankcode());//���������к�
			vouchermap.put("PayTypeCode", "");//֧����ʽ����
			vouchermap.put("PayTypeName", "");//֧����ʽ����
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
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(mainDto.getFcurreckzeroamt()));//�����ۼƽ��
				Detailmap.put("XCheckResult", "");//���˽��
				Detailmap.put("XCurFinReckMoney", "");//��������������
				Detailmap.put("XDiffMoney", "");//��� 
				Detailmap.put("Remark", "");//��ע				
				Detailmap.put("Hold1", "");//Ԥ���ֶ�1 
				Detailmap.put("Hold2", "");//Ԥ���ֶ�2 							
				Detail.add(Detailmap);
				Total=Total.add((BigDecimal)mainDto.getFcurreckzeroamt());
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
