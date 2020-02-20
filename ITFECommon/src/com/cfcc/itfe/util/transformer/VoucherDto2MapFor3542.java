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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ���뼰ʡ������Ʊ��(��������ƾ֤)��3542��
 * 
 * @author hejianrong
 * @time  2014-04-02
 * 
 */
public class VoucherDto2MapFor3542 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3542.class);

	/**
	 * ƾ֤����
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=findMainDto(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TvVoucherinfoAllocateIncomeDto mainDto:(List<TvVoucherinfoAllocateIncomeDto>)list){
			lists.add(voucherTranfor(dto, mainDto));
		}return lists;
	}
		
	/**
	 * ����ƾ֤
	 * ����ƾ֤����
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	private List voucherTranfor(TvVoucherinfoDto vDto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));		
		dto.setSvoucherno(mainDto.getSdealno());
		dto.setSadmdivcode(mainDto.getSadmdivcode());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");		
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map=tranfor(lists);
		lists.clear();		
		lists.add(map);
		lists.add(dto);
		return lists;
	}
	
	/**	
	 * ��ѯ���뼰ʡ������Ʊ��(��������ƾ֤)ҵ�����Ϣ
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {		
		TvVoucherinfoAllocateIncomeDto mainDto=new TvVoucherinfoAllocateIncomeDto();
		mainDto.setSorgcode(dto.getSorgcode());
		mainDto.setStrecode(dto.getStrecode());
		mainDto.setScommitdate(dto.getScreatdate());
		try {
			return CommonFacade.getODB().findRsByDto(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ���뼰ʡ������Ʊ��ҵ�����Ϣ����",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ���뼰ʡ������Ʊ��ҵ�����Ϣ����",e);
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
			TvVoucherinfoAllocateIncomeDto mainDto=(TvVoucherinfoAllocateIncomeDto) lists.get(1);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("Id", vDto.getSdealno());//���뼰ʡ������Ʊ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//������������
			vouchermap.put("StYear", vDto.getSstyear());//ҵ�����		
			vouchermap.put("VtCode", vDto.getSvtcode());//ƾ֤���ͱ��			
			vouchermap.put("VouDate", vDto.getScreatdate());//ƾ֤����		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//ƾ֤��		
			vouchermap.put("PayeeAcctNo", mainDto.getSpayeeacctno());//�տ����˺�
			vouchermap.put("PayeeAcctName", mainDto.getSpayeeacctname());//�տ�������
			vouchermap.put("PayeeAcctBankName", mainDto.getSpayeeacctbankname());//�տ�������
			vouchermap.put("PayAcctNo", mainDto.getSpayacctno());//�������˺�
			vouchermap.put("PayAcctName", mainDto.getSpayacctname());//����������
			vouchermap.put("PayAcctBankName", mainDto.getSpayacctbankname());//����������
			vouchermap.put("PaySummaryCode", "");//��;����
			vouchermap.put("PaySummaryName", "");//��;����
			vouchermap.put("PayAmt",  MtoCodeTrans.transformString(mainDto.getNmoney()));//������
			vouchermap.put("AgencyCode", "");//����Ԥ�㵥λ����
			vouchermap.put("AGencyName", "");//����Ԥ�㵥λ����			
			vouchermap.put("Hold1", "");//Ԥ���ֶ�1		
			vouchermap.put("Hold2", "");//Ԥ���ֶ�2					
			return map;	
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��",e);
		}
	}
		
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}	
}
