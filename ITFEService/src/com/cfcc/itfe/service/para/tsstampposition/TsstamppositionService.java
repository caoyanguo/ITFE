package com.cfcc.itfe.service.para.tsstampposition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time   11-08-01 14:49:22
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class TsstamppositionService extends AbstractTsstamppositionService {
	private static Log log = LogFactory.getLog(TsstamppositionService.class);	


	/**
	 * ����
	 	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().delete(dtoInfo);
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶ�ƾ֤���ͣ����������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
    }

	/**
	 * ɾ��
	 	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
    }

	/**
	 * �޸�
	 	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶ�ƾ֤���ͣ��������룬ǩ��λ���Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
    }

    /*
     * (non-Javadoc)��ѯǩ��λ����Ϣ
     * @see com.cfcc.itfe.service.para.tsstampposition.ITsstamppositionService#queryStampPosition(java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
     */
	public List queryStampPosition(String certID, String treCode,
			Integer stYear, String vtCode) throws ITFEBizException {
		//���ݹ������ȡ��orgcode�������������
		String admDivCode =null;
		String orgCode = null;
		Map<String,TsConvertfinorgDto> TsConvertfinorgMap = BusinessFacade.findFincInfo("");
		TsConvertfinorgDto tsConvertfinorgDto =  TsConvertfinorgMap.get(treCode);
		admDivCode =tsConvertfinorgDto.getSadmdivcode();
		orgCode = tsConvertfinorgDto.getSorgcode();
		//����webservice����ȡ��ǩ��λ����Ϣ
		VoucherService voucherService=new VoucherService();
		List<TsStamppositionDto> stampPostionList = new ArrayList();
		if(admDivCode==null||"".equals(admDivCode))
		{
			if(tsConvertfinorgDto.getSfinorgcode()!=null&&tsConvertfinorgDto.getSfinorgcode().length()>6)
				admDivCode = tsConvertfinorgDto.getSfinorgcode().substring(0, 6);
		}
		Map stampPosttionMap  = voucherService.queryStampPositionWithName(certID, admDivCode, stYear, vtCode);
		
		//�����ص�ǩ��λ����Ϣmap��������stampPostionList����
		if(stampPosttionMap!=null&&stampPosttionMap.size()>0){
			Set<Map.Entry<String, String>> set = stampPosttionMap.entrySet();
			int sequence = 0;
			for(Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();){
				TsStamppositionDto stamppositionDto = new TsStamppositionDto();
				Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();   
				String ls_StampPosition = (String) entry.getKey();
				String ls_StampPositionName = entry.getValue();
				//��Ҫ��������͹���
				if(ls_StampPosition.indexOf("rh")==0){
					String[] stampPositions = ls_StampPosition.split("_");
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",xm5207,")>=0) {
						sequence = 0;
					}
					sequence++;
					stamppositionDto.setSadmdivcode(admDivCode);
					stamppositionDto.setSorgcode(orgCode);
					stamppositionDto.setSstampname(ls_StampPositionName);
					stamppositionDto.setSstampposition(ls_StampPosition);
					stamppositionDto.setSstampsequence(String.valueOf(sequence));
					//����ǩ��λ��������������ǩ������
	        		if(stampPositions[stampPositions.length-1].contains(MsgConstant.VOUCHERSAMP_ROTARY)){
	        			stamppositionDto.setSstamptype(MsgConstant.VOUCHERSAMP_ROTARY);
	        		}else if(stampPositions[stampPositions.length-1].contains(MsgConstant.VOUCHERSAMP_OFFICIAL)){
	        			stamppositionDto.setSstamptype(MsgConstant.VOUCHERSAMP_OFFICIAL);
	        		}else if(stampPositions[stampPositions.length-1].contains(MsgConstant.VOUCHERSAMP_ATTACH)){
	        			stamppositionDto.setSstamptype(MsgConstant.VOUCHERSAMP_ATTACH);
	        		}else if(stampPositions[stampPositions.length-1].contains(MsgConstant.VOUCHERSAMP_BUSS)){
	        			stamppositionDto.setSstamptype(MsgConstant.VOUCHERSAMP_BUSS);
	        		}else{
	        			stamppositionDto.setSstamptype(stampPositions[stampPositions.length-1]);
	        		}
					stamppositionDto.setStrecode(treCode);
					stamppositionDto.setSvtcode(vtCode);
					stampPostionList.add(stamppositionDto);
				}
			}
		}
		return stampPostionList;
	}

}