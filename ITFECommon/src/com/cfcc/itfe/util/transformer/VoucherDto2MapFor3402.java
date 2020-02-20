package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;



/**
 * ƾ֤����ձ���(3402)ת��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3402 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3402.class);
	private  BigDecimal Total;
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		cDto.setSorgcode(vDto.getSorgcode());		
		cDto.setStrecode(vDto.getStrecode());
		List<TrStockdayrptDto> sList=new ArrayList<TrStockdayrptDto>();
		TrStockdayrptDto sDto=new TrStockdayrptDto();
		sDto.setStrecode(vDto.getStrecode());
		sDto.setSrptdate(vDto.getScheckdate());
		try {
			List tsConvertfinorgList=(List) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("���⣺"+vDto.getStrecode()+"��Ӧ�Ĳ������ش������δά����");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("���⣺"+cDto.getStrecode()+"��Ӧ����������δά����");
			}
			sList= CommonFacade.getODB().findRsByDto(sDto);		
			if(sList==null||sList.size()==0){
				return null;
			}
			vDto.setSadmdivcode(cDto.getSadmdivcode());
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}		
		List<List> lists=new ArrayList<List>();
		String FileName=null;
		String dirsep = File.separator;
		if(sList!=null&&sList.size()>0){
			
			String mainvou=VoucherUtil.getGrantSequence();
			FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
	        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
			TvVoucherinfoDto dto=new TvVoucherinfoDto();			
			dto.setSorgcode(vDto.getSorgcode());
			dto.setSadmdivcode(vDto.getSadmdivcode());
			dto.setSvtcode(vDto.getSvtcode());
			dto.setScreatdate(TimeFacade.getCurrentStringTime());//ƾ֤����
			dto.setScheckdate(vDto.getScheckdate());//��������
			dto.setStrecode(vDto.getStrecode());
			dto.setSfilename(FileName);
			dto.setSdealno(mainvou);		
			dto.setSadmdivcode(cDto.getSadmdivcode());
			dto.setSstyear(dto.getScreatdate().substring(0, 4));				
			dto.setSattach("");
			dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			dto.setSdemo("����ɹ�");				
			dto.setSvoucherflag("1");
			dto.setSvoucherno(mainvou);
			dto.setIcount(sList.size());
			Map map=tranfor(sList,dto);					
			List vouList=new ArrayList();
			vouList.add(map);
			vouList.add(dto);			
			lists.add(vouList);			
		}
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
	private Map tranfor(List<TrStockdayrptDto> dtoList,TvVoucherinfoDto vDto) throws ITFEBizException{
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> vouchermap = new HashMap<String, Object>();
		
		// ���ñ��Ľڵ� Voucher
		map.put("Voucher", vouchermap);
		// ���ñ�����Ϣ�� 
		vouchermap.put("Id", vDto.getSdealno());//����ձ���Id
		vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//������������
		vouchermap.put("StYear", vDto.getScheckdate().substring(0,4));//ҵ�����		
		vouchermap.put("VtCode", vDto.getSvtcode());//ƾ֤���ͱ��		
		vouchermap.put("VouDate", vDto.getScreatdate());//ƾ֤����	
		vouchermap.put("AcctDate", vDto.getScheckdate());//��������
		vouchermap.put("VoucherNo", vDto.getSvoucherno());//ƾ֤��
		vouchermap.put("BankCode", vDto.getStrecode());//�����������
		vouchermap.put("TreName", BusinessFacade.findTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":BusinessFacade.findTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//������������
		vouchermap.put("Hold1", "");//Ԥ���ֶ�1	
		vouchermap.put("Hold2", "");//Ԥ���ֶ�2	
		Map detailListMap = new HashMap();
		List detailList = new ArrayList();
		Map detailMap = null;//Voucher/DetailList/Detail/
		Total=new BigDecimal("0.00");
		int id=0;
		for(TrStockdayrptDto sdto:dtoList)
		{
			if(!ITFECommonConstant.PUBLICPARAM.contains(",3402acct371=true,")&&sdto.getSaccno().contains(vDto.getSorgcode()+"371"))//������������371��ͷ���˻����벻����
			{
				continue;
			}
			detailMap = new HashMap();
			detailMap.put("Id", vDto.getSdealno()+(id++));//id
			detailMap.put("AcctCode", sdto.getSaccno());//�˻�����
			detailMap.put("AcctName", sdto.getSaccname());//�˻�����
			detailMap.put("YesterdayBalance", MtoCodeTrans.transformString(sdto.getNmoneyyesterday()));//�������
			detailMap.put("TodayReceipt", MtoCodeTrans.transformString(sdto.getNmoneyin()));//��������
			detailMap.put("TodayPay", MtoCodeTrans.transformString(sdto.getNmoneyout()));//����֧��
			detailMap.put("TodayBalance", MtoCodeTrans.transformString(sdto.getNmoneytoday()));//�������
			detailMap.put("Hold1", "");//Ԥ���ֶ�1
			detailMap.put("Hold2", "");//Ԥ���ֶ�2
			detailMap.put("Hold3", "");//Ԥ���ֶ�3
			detailMap.put("Hold4", "");//Ԥ���ֶ�4
			Total =Total.add(sdto.getNmoneytoday());
			detailList.add(detailMap);
		}
		vDto.setNmoney(Total);
		vouchermap.put("DetailList", detailListMap);
		detailListMap.put("Detail", detailList);
		return map;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	

}
