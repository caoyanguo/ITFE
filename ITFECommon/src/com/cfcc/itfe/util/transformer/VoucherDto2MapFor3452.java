package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ��������Ʊ�ݣ�3452��
 * 
 * @author hejianrong
 * @time  2014-04-02
 * 
 */
public class VoucherDto2MapFor3452 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3452.class);

	/**
	 * ƾ֤����
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=findMainDto(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TvVoucherinfoAllocateIncomeDto mainDto:(List<TvVoucherinfoAllocateIncomeDto>)list){
			List<TvVoucherinfoDto> voutherList=voucherIsRepeat(dto, mainDto);
			if(voutherList!=null&&voutherList.size()>0){
				continue;
			}
			lists.add(voucherTranfor(dto, mainDto));
		}
		return lists;
	}
		
	/**
	 * ����ƾ֤
	 * ����ƾ֤����
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	private List voucherTranfor(TvVoucherinfoDto vDto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));		
		dto.setSadmdivcode(mainDto.getSadmdivcode());
		dto.setSvoucherno(mainDto.getSdealno());
		mainDto.setShold2(dto.getSvoucherno());
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
		lists.add(mainDto);
		return lists;
	}
	
	/**	
	 * ��ѯ���뼰ʡ������Ʊ��(��������ƾ֤)ҵ�����Ϣ
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {		
		TvVoucherinfoAllocateIncomeDto mainDto=new TvVoucherinfoAllocateIncomeDto();
		mainDto.setSorgcode(dto.getSorgcode());
		mainDto.setStrecode(dto.getStrecode());
		mainDto.setScommitdate(dto.getScreatdate());
		mainDto.setSvtcode(dto.getSvtcode());
		try {
			return CommonFacade.getODB().findRsByDtoForWhere(mainDto, " AND  S_HOLD2 IS NULL");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
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
	@SuppressWarnings("unchecked")
	public Map tranfor(List lists) throws ITFEBizException{
		try{
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
			vouchermap.put("MsgType", mainDto.getSreportkind());//��������
			vouchermap.put("PayTraseqNo", mainDto.getSpaydealno());//֧���������
			vouchermap.put("TrasType", "");//��������
			vouchermap.put("BizType", mainDto.getSvtcodekind());//ҵ������
			vouchermap.put("SndBnkNo", mainDto.getSforwardbankno());//�������к�
			vouchermap.put("PayerOpnBnkNo", "");//�����˿������к�
			vouchermap.put("RcvBnkNo", mainDto.getSreceivebankno());//�������к�
			vouchermap.put("PayeeOpnbnkNo", "");//�տ��˿������к�
			vouchermap.put("DebitAcct", mainDto.getSborrow());//�����˻��跽
			vouchermap.put("LoanAcct", mainDto.getSlend());//�����˻�����
			vouchermap.put("ChkSignFlag", mainDto.getSescortmarks());//��Ѻ��־
			vouchermap.put("ChkSign", mainDto.getSsecretsign());//��Ѻ
			vouchermap.put("RecvMsgDate", mainDto.getSreceivedate());//�ձ�����
			vouchermap.put("EntrustDate", mainDto.getScommitdate());//ί������
			vouchermap.put("AddWord", mainDto.getSdemo());//����
			vouchermap.put("TrasrlNo", mainDto.getSdealno());//������ˮ��
			vouchermap.put("PrintTime", "");//��ӡʱ��
			vouchermap.put("PrintNum", "");//��ӡ����
			vouchermap.put("PrintOper", "");//��ӡ����Ա
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
	
	
	/**
	 * ƾ֤����
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherIsRepeat(TvVoucherinfoDto dto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException{
		TvVoucherinfoDto vDto=(TvVoucherinfoDto) dto.clone();
		vDto.setSadmdivcode(mainDto.getSadmdivcode());
		vDto.setSvoucherno(mainDto.getSdealno());
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
		}		
	}
}
