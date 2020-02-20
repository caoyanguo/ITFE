package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.PayreckCountDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherDto2MapFor5904 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5904.class);

	/**
	 * ƾ֤����
	 * 
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		if (vDto == null)
			return null;
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		return getVoucher(vDto);
	}

	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException {
		List returnList = new ArrayList();
		TsConvertfinorgDto cDto = new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());
			cDto.setStrecode(vDto.getStrecode());
			List<TsConvertfinorgDto> tsConvertfinorgList = (List<TsConvertfinorgDto>) CommonFacade
					.getODB().findRsByDto(cDto);
			if (tsConvertfinorgList == null || tsConvertfinorgList.size() == 0) {
				throw new ITFEBizException("���⣺" + vDto.getStrecode()
						+ "��Ӧ�Ĳ������ش������δά����");
			}
			cDto = (TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if (cDto.getSadmdivcode() == null
					|| cDto.getSadmdivcode().equals("")) {
				throw new ITFEBizException("���⣺" + cDto.getStrecode()
						+ "��Ӧ����������δά����");
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			List listall = getDataList(vDto, execDetail);
			if (listall != null && listall.size() > 0) {
				createVoucher(vDto, returnList, cDto, listall);
			}

		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��", e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��", e2);
		} finally {
			if (execDetail != null)
				execDetail.closeConnection();
		}
		return returnList;
	}

	private void createVoucher(TvVoucherinfoDto vDto, List returnList,
			TsConvertfinorgDto cDto, List listall) throws ITFEBizException {
		List<List> sendList = this.getSubLists(listall, 1000);
		for (int i = 0; i < sendList.size(); i++) {
			List<IDto> tempList = sendList.get(i);
			
			IDto idto = (IDto) tempList.get(i);
			String danhao = null;
			String FileName = null;
			String dirsep = File.separator;
			String mainvou = "";
			TvVoucherinfoDto dto = new TvVoucherinfoDto();
			dto.setSorgcode(vDto.getSorgcode());
			dto.setSadmdivcode(vDto.getSadmdivcode());
			dto.setSvtcode(vDto.getSvtcode());
			dto.setScreatdate(TimeFacade.getCurrentStringTime());
			dto.setStrecode(vDto.getStrecode());
			dto.setSstyear(dto.getScreatdate().substring(0, 4));
			dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			dto.setSdemo("����ɹ�");
			dto.setSvoucherflag("1");
			dto.setNmoney(new BigDecimal("0.00"));
			dto.setSext1("1");// ����ʽ1:���з���,2:��������,3:���з���
			if (idto instanceof TsBudgetsubjectDto) {
				dto.setSext4("1");
				dto.setSext5("Ԥ���Ŀ");
			} else if (idto instanceof TsPaybankDto) {
				dto.setSext4("2");
				dto.setSext5("֧���к�");
			}
			mainvou = VoucherUtil.getGrantSequence();
			vDto.setSdealno(mainvou);
			vDto.setSvoucherno(mainvou);
			dto.setSvoucherno(mainvou);
			if (danhao == null)
				danhao = mainvou;
			FileName = ITFECommonConstant.FILE_ROOT_PATH + dirsep + "Voucher"
					+ dirsep + vDto.getScreatdate() + dirsep + "send"
					+ vDto.getSvtcode() + "_" + mainvou + ".msg";
			dto.setSfilename(FileName);
			List<IDto> idtoList = new ArrayList<IDto>();
			Map map = tranfor(dto, tempList);
			dto.setSpaybankcode("011");
			dto.setSdealno(mainvou);
			dto.setSvoucherno(mainvou);
			List vouList = new ArrayList();
			vouList.add(map);
			vouList.add(dto);
			vouList.add(idtoList);
			returnList.add(vouList);
		}

	}

	private Map tranfor(TvVoucherinfoDto vDto, List<IDto> idtolist)
			throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSvoucherno());// ��ˮ��
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			vouchermap.put("DataEle", vDto.getSext4());// �������ݴ���
			vouchermap.put("DataEleName", vDto.getSext5());//����������������
			vouchermap.put("Hold1", "");// Ԥ���ֶ�1
			vouchermap.put("Hold2", "");// Ԥ���ֶ�2
			vouchermap.put("Hold3", "");// Ԥ���ֶ�3
			vouchermap.put("Hold4", "");// Ԥ���ֶ�4
			List<Object> Detail= new ArrayList<Object>();
			if (vDto.getSext4().equals("1")) {//Ԥ���Ŀ
				for(int i = 0; i<idtolist.size();i++){
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					TsBudgetsubjectDto idto = (TsBudgetsubjectDto) idtolist.get(i);
					Detailmap.put("Id", i+1);// ��ˮ��
					Detailmap.put("Subjectcode", idto.getSsubjectcode());// ��Ŀ����
					Detailmap.put("Subjectname", idto.getSsubjectname());// ��Ŀ����
					Detailmap.put("Subjecttype", idto.getSsubjecttype());// ��Ŀ����
					Detailmap.put("Subjectclassification",idto.getSsubjectclass());// ��Ŀ����
					Detailmap.put("IEsign", idto.getSinoutflag());// ��֧��־
					Detailmap.put("Entrymark", idto.getSwriteflag());// ¼���־
					Detailmap.put("Subjectattribute", idto.getSsubjectattr());// ��Ŀ����
					Detailmap.put("Allocationmark", idto.getSmoveflag());// ������־
					Detailmap.put("SubjectcodeSimplify", "");// ��Ŀ����
					Detailmap.put("FiscalCode", "");// ��������
					Detailmap.put("TaxCode", "");// ˰�����
					Detailmap.put("SuperiorsCode", idto.getSsubjectcode());// �ϼ�����
					Detailmap.put("UniformCode", "");// ͳһ����
					Detailmap.put("Statisticalcode", "");// ͳ�ƴ���
					
					Detailmap.put("Paylinenumber", "");// ֧���к�
					Detailmap.put("Status", "");// ״̬
					Detailmap.put("Liquidationlinenumber", "");// �������к�
					Detailmap.put("City", "");// ���ڳ���
					Detailmap.put("Participantsfullname", "");// ������ȫ��
					Detailmap.put("effectivedate", "");// ��Ч����
					Detailmap.put("Expirationdate", "");// ʧЧ����
					Detailmap.put("Remark", "");// ��ע
					Detailmap.put("ZeroAccount", "");// ������˺�
					Detailmap.put("ZBAsstate", "");// ������˻�״̬
					Detailmap.put("Zerotime", "");// ��������ʱ��
					Detailmap.put("ZeroBanknumber", "");// �������к�
					Detailmap.put("ZeroBankname", "");// ������������
					Detailmap.put("Zerocodingunit", "");// ��������λ����
					Detailmap.put("ZeroAccountName", "");// ��������λ����
					Detailmap.put("SupDepName", "");// ��λ����
					Detailmap.put("SupDepCode", "");// ��λ����
					Detailmap.put("UnitChangeStatus", "");// ��λ���״̬
					Detailmap.put("IssuedDate", "");// ��������
					Detailmap.put("EffectiveDate", "");// ��Ч���� 
					Detail.add(Detailmap);
				}
			}
			
			if (vDto.getSext4().equals("2")) {
				
				for(int i = 0; i<idtolist.size();i++){
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					TsPaybankDto idto = (TsPaybankDto) idtolist.get(i);
					Detailmap.put("Id", i);// ��ˮ��
					Detailmap.put("Subjectcode", "");// ��Ŀ����
					Detailmap.put("Subjectname", "");// ��Ŀ����
					Detailmap.put("Subjecttype", "");// ��Ŀ����
					Detailmap.put("Subjectclassification","");// ��Ŀ����
					Detailmap.put("IEsign", "");// ��֧��־
					Detailmap.put("Entrymark", "");// ¼���־
					Detailmap.put("Subjectattribute", "");// ��Ŀ����
					Detailmap.put("Allocationmark", "");// ������־
					Detailmap.put("SubjectcodeSimplify", "");// ��Ŀ����
					Detailmap.put("FiscalCode", "");// ��������
					Detailmap.put("TaxCode", "");// ˰�����
					Detailmap.put("SuperiorsCode", "");// �ϼ�����
					Detailmap.put("UniformCode", "");// ͳһ����
					Detailmap.put("Statisticalcode", "");// ͳ�ƴ���
					
					Detailmap.put("Paylinenumber",idto.getSbankno());// ֧���к�
					String state = idto.getSstate();// 0��Чǰ��1��Ч��2ע��
					if (state.endsWith("0")) {
						Detailmap.put("Status", "1");// ״̬
					} else if (state.endsWith("2")) {
						Detailmap.put("Status", "2");// ״̬
					}
					Detailmap.put("Liquidationlinenumber", idto.getSpaybankno());// �������к�
					Detailmap.put("City", idto.getSofcity());// ���ڳ���
					Detailmap.put("Participantsfullname", idto.getSbankname());// ������ȫ��
					Detailmap.put("effectivedate", idto.getDaffdate());// ��Ч����
					Detailmap.put("Expirationdate", "");// ʧЧ����
					Detailmap.put("Remark", "");// ��ע
					
					Detailmap.put("ZeroAccount", "");// ������˺�
					Detailmap.put("ZBAsstate", "");// ������˻�״̬
					Detailmap.put("Zerotime", "");// ��������ʱ��
					Detailmap.put("ZeroBanknumber", "");// �������к�
					Detailmap.put("ZeroBankname", "");// ������������
					Detailmap.put("Zerocodingunit", "");// ��������λ����
					Detailmap.put("ZeroAccountName", "");// ��������λ����
					Detailmap.put("SupDepName", "");// ��λ����
					Detailmap.put("SupDepCode", "");// ��λ����
					Detailmap.put("UnitChangeStatus", "");// ��λ���״̬
					Detailmap.put("IssuedDate", "");// ��������
					Detailmap.put("EffectiveDate", "");// ��Ч���� 
					Detail.add(Detailmap);
				}
			}
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	private String getString(Map datamap, String key) {
		if (datamap == null || key == null)
			return "";
		else
			return String.valueOf(datamap.get(key));
	}

	public Map tranfor(List list) throws ITFEBizException {
		Map map = new HashMap<String, PayreckCountDto>();
		for (PayreckCountDto dto : (List<PayreckCountDto>) list) {
			if (map.get(dto.getFundtypecode() + "-" + dto.getPaytypecode()) == null) {
				map.put(dto.getFundtypecode() + "-"+ dto.getPaytypecode(), dto);
			}
		}
		return map;
	}

	private List getDataList(TvVoucherinfoDto vDto, SQLExecutor execDetail)
			throws ITFEBizException {
		List listall = new ArrayList();
		StringBuffer sql = null;
		try {
			// Ԥ���Ŀ
			if (vDto.getSext4() == null || vDto.getSext4().equals("")
					|| vDto.getSext4().equals("1")) {
				TsBudgetsubjectDto dto = new TsBudgetsubjectDto();
				dto.setSorgcode(vDto.getSorgcode());
				dto.setScreatstatus("1");// û�����ɹ�5904
				List<TsBudgetsubjectDto> list = (List<TsBudgetsubjectDto>) CommonFacade
						.getQDB().findRsByDto(dto);
				if (list != null && list.size() > 0)
					listall.addAll(list);
			}
			// ֧��ϵͳ�к�
			if (vDto.getSext4() == null || vDto.getSext4().equals("")
					|| vDto.getSext4().equals("2")) {
				TsPaybankDto dto = new TsPaybankDto();
				dto.setScreatstatus("1");// û�����ɹ�5904
				List<TsPaybankDto> list = (List<TsPaybankDto>) CommonFacade
						.getQDB().findRsByDto(dto);
				if (list != null && list.size() > 0)
					listall.addAll(list);
			}

		} catch (Exception e) {
			if (execDetail != null)
				execDetail.closeConnection();
			logger.error(e.getMessage(), e);
			throw new ITFEBizException("��ѯ" + sql == null ? "" : sql.toString()
					+ "��������Ԫ������Ϣ�쳣��", e);
		} finally {
			if (execDetail != null)
				execDetail.closeConnection();
		}
		return listall;
	}
	
	private List getSubLists(List list,int subsize)
	{
		List getList = null;
		if(list!=null&&list.size()>0)
		{
			if(subsize<1)
				subsize=500;
			int count = list.size()/subsize;
			int yu = list.size()%subsize;
			getList = new ArrayList();
			for(int i=0;i<count;i++)
				getList.add(list.subList(i*subsize, subsize*(i+1)));
			if(yu>0)
				getList.add(list.subList(count*subsize, (count*subsize)+yu));
		}
		return getList;
	}
	
	private String getString(String key) {
		if (key == null)
			key = "";
		return key;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

}
