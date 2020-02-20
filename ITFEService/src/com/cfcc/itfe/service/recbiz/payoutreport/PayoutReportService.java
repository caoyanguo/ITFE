package com.cfcc.itfe.service.recbiz.payoutreport;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3508;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3553;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author renqingbin
 * @time   14-03-28 09:04:23
 * codecomment: 
 */

public class PayoutReportService extends AbstractPayoutReportService {
	private static Log log = LogFactory.getLog(PayoutReportService.class);	

	/**
	 * ֧����������
	 	 
	 * @generated
	 * @param list<vocherlist[ƾ֤������Ϣlist],reportTypeList[��������list]>
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
    @SuppressWarnings("unchecked")
	public MulitTableDto payoutVoucherGenerate(List list) throws ITFEBizException {
    	int count=0;
    	MulitTableDto mulitTableDto = new MulitTableDto();
    	String currentDate = TimeFacade.getCurrentStringTime(); 
    	String dirsep = File.separator;
    	List<TvVoucherinfoDto> vocherlist = (List<TvVoucherinfoDto>) list.get(0);
    	List<String> reportTypeList = (List<String>) list.get(1);
    	List<TrTaxorgPayoutReportDto> payoutReportList = null;
    	List<String> errorList = new ArrayList<String>();
    	for(TvVoucherinfoDto voucherdto : vocherlist){
    		//У������Ӧ�Ĳ���������Ϣ�����������Ƿ�ά��
    		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
    		try {
    			cDto.setSorgcode(voucherdto.getSorgcode());		
    			cDto.setStrecode(voucherdto.getStrecode());
    			List finorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
    			if(finorgDtoList==null||finorgDtoList.size()==0){
    				errorList.add("���⣺"+cDto.getStrecode()+" ��Ӧ�Ĳ�������δά�����ù���ı���δ���ɣ�");
					continue;
    				//throw new ITFEBizException("���⣺"+cDto.getStrecode()+" ��Ӧ�Ĳ�������δά����");
    			}
    			cDto=(TsConvertfinorgDto) finorgDtoList.get(0);
    			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){
    				errorList.add("���⣺"+cDto.getStrecode()+" ��Ӧ����������δά�����ù���ı���δ���ɣ�");
					continue;
    				//throw new ITFEBizException("���⣺"+cDto.getStrecode()+" ��Ӧ����������δά����");
    			}
				for(String voucherTpe : reportTypeList) {
					//У��ƾ֤�Ƿ��Ѿ�����
					TvVoucherinfoDto tempdto = new TvVoucherinfoDto();
					tempdto.setSorgcode(voucherdto.getSorgcode());
					tempdto.setStrecode(voucherdto.getStrecode());
					tempdto.setScheckdate(voucherdto.getScheckdate());
					tempdto.setScheckvouchertype(voucherTpe);
					tempdto.setSattach(voucherdto.getSattach());
					if(StateConstant.REPORT_DAY.equals(tempdto.getSattach())){//�ձ�
						tempdto.setSvtcode(MsgConstant.VOUCHER_NO_3553);
					}else if(StateConstant.REPORT_MONTH.equals(tempdto.getSattach())){//�±�
						tempdto.setSvtcode(MsgConstant.VOUCHER_NO_3508);
					}
					tempdto.setShold1(voucherdto.getShold1());
					tempdto.setShold2(voucherdto.getShold2());
					tempdto.setShold3(voucherdto.getShold3());
					tempdto.setShold4(voucherdto.getShold4());
					List templist = CommonFacade.getODB().findRsByDto(tempdto);
					if(templist.size()>0){
						errorList.add("�������["+voucherdto.getStrecode()+"]��["+getReportName(voucherTpe)+"]�Ѿ����ɣ������ظ����ɣ�");
						continue;
					}
					//��װƾ֤������dto
		    		String mainvou=VoucherUtil.getGrantSequence();
		    		voucherdto.setSdealno(mainvou);
		    		voucherdto.setSstyear(voucherdto.getScheckdate().substring(0, 4));
		    		voucherdto.setScreatdate(currentDate);
		    		voucherdto.setSvoucherno(mainvou);
		    		voucherdto.setSadmdivcode(cDto.getSadmdivcode());
					voucherdto.setSpaybankcode(cDto.getSfinorgcode());
		    		//�ļ���
		    		String fileName = ITFECommonConstant.FILE_ROOT_PATH + dirsep + "Voucher" + dirsep + currentDate + dirsep
						+ "send" + voucherdto.getSvtcode() + "_" + voucherdto.getSattach() + "_" + voucherTpe + "_"
						+ mainvou + ".msg";
		    		voucherdto.setSfilename(fileName);
		    		voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		    		voucherdto.setSdemo("����ɹ�");				
		    		voucherdto.setSvoucherflag("1");
		    		voucherdto.setScheckvouchertype(voucherTpe);
		    		//����������ѯ��������
					payoutReportList = queryPayoutReport(voucherTpe,voucherdto);
					if(payoutReportList == null || payoutReportList.size()==0){
						errorList.add("�������["+voucherdto.getStrecode()+"]��["+getReportName(voucherTpe)+"]û�����ݣ���ȷ���Ƿ��Ѿ����룡");
						continue;
					}
					Map map = null;
					if(StateConstant.REPORT_DAY.equals(voucherdto.getSattach())){//�ձ�
						map = VoucherDto2MapFor3553.tranfor(payoutReportList, voucherdto);
					}else{
						map = VoucherDto2MapFor3508.tranfor(payoutReportList, voucherdto);
					}
	    			Map xmlmap = (Map) map.get("Map");
	    			BigDecimal sumamt = (BigDecimal) map.get("SumMoney");
	    			voucherdto.setNmoney(sumamt);
	    			VoucherUtil.sendTips(voucherdto,xmlmap);
	    			try {
						DatabaseFacade.getODB().create(voucherdto);
					} catch (JAFDatabaseException e) {
						log.error(e);					
						throw new ITFEBizException(e.getMessage(),e);
					}
					count++;
				}
    		} catch (JAFDatabaseException e2) {
    			log.error(e2);
    			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
    		} catch (ValidateException e2) {
    			log.error(e2);
    			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
    		}catch(Exception e2 ){
    			log.error(e2);
    			throw new ITFEBizException(e2.getMessage(),e2);
    		}
    	}
    	mulitTableDto.setTotalCount(count);
    	mulitTableDto.setErrorList(errorList);
    	return mulitTableDto;
    }
    /**
	 * ����������ѯ֧����������
	 * 
	 * @param subVoucherType ��������
	 * @param voucherdto     ����dto
	 * @throws ITFEBizException	 
	 */
	private List<TrTaxorgPayoutReportDto> queryPayoutReport(String voucherTpe,
			TvVoucherinfoDto voucherdto) throws ITFEBizException{
		List<TrTaxorgPayoutReportDto> payoutReportList = null;
	    TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
	    payoutdto.setStrecode(voucherdto.getStrecode());
	    payoutdto.setStaxorgcode(voucherTpe);//��������
	    payoutdto.setSrptdate(voucherdto.getScheckdate());
	    payoutdto.setSbudgettype(voucherdto.getShold1());//Ԥ������
	    payoutdto.setSbelongflag(voucherdto.getShold3());//Ͻ����־
	    payoutdto.setSpaytype(voucherdto.getShold4());//֧������
	    payoutdto.setStrimflag(voucherdto.getShold2());//������
	    try {
			payoutReportList = CommonFacade.getODB().findRsByDtoForWhere(payoutdto, " order by S_BUDGETSUBCODE");//RsByDto(payoutdto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("֧�������ѯ�쳣��",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("֧�������ѯ�쳣��",e);
		}
		return payoutReportList;
	}

	/**
	 * ����������ѯ֧����������
	 * 
	 * @param subVoucherType ��������
	 * @param reportDate     ��������
	 * @param strecode       �������
	 * @return String
	 * @throws ITFEBizException	 
	 */
	private List<TrTaxorgPayoutReportDto> queryPayoutReport(String subVoucherType,String reportDate,String strecode) throws ITFEBizException{
		List<TrTaxorgPayoutReportDto> payoutReportList = null;
	    TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
	    payoutdto.setStrecode(strecode);
	    payoutdto.setStaxorgcode(subVoucherType);//��������
	    payoutdto.setSrptdate(reportDate);
	    try {
			payoutReportList = CommonFacade.getODB().findRsByDto(payoutdto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("֧�������ѯ�쳣��",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("֧�������ѯ�쳣��",e);
		}
		return payoutReportList;
	}
	
	/**
	 * 
	 * @return
	 */
	private String getReportName(String voucherType){
		if(StateConstant.REPORT_PAYOUT_TYPE_1.equals(voucherType)){
			return "һ��Ԥ��֧������";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_2.equals(voucherType)){
			return "ʵ���ʽ�֧������";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_3.equals(voucherType)){
			return "����Ԥ��֧������";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_4.equals(voucherType)){
			return "ֱ��֧���ձ���";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_5.equals(voucherType)){
			return "��Ȩ֧���ձ���";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_6.equals(voucherType)){
			return "ֱ��֧���˻��ձ���";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_7.equals(voucherType)){
			return "��Ȩ֧���˻��ձ���";					
		}
		return voucherType;
	}

}