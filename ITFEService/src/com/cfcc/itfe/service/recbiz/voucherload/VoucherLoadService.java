package com.cfcc.itfe.service.recbiz.voucherload;

import java.io.InputStream;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherAmtUtil;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author hjr
 * @time 13-07-03 14:03:42 codecomment:
 */

public class VoucherLoadService extends AbstractVoucherLoadService {
	private static Log log = LogFactory.getLog(VoucherLoadService.class);
	private Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
			.getBean(MsgConstant.VOUCHER);

	/**
	 * ƾ֤��ȡ
	 * 
	 * @generated
	 * @param svtcode
	 * @return java.lang.Integer
	 * @throws ITFEBizException
	 */
	public Integer voucherLoad(String svtcode, String sorgcode)
			throws ITFEBizException {
		try{
			return voucher.voucherRead(svtcode, sorgcode);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public String getOCXServerURL() throws ITFEBizException {
		return ITFECommonConstant.OCXSERVICE_URL;
	}

	public Integer voucherCommit(List checkList) throws ITFEBizException {
		try{
			return voucher.voucherCommit(checkList);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * �鿴�ѻص�ƾ֤״̬
	 * 
	 */
	public Integer queryStatusReturnVoucher(List checkList)
			throws ITFEBizException {
		try{
			return voucher.voucherReturnQueryStatus(checkList);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}
	
	public Map queryVoucherReturnStatus(Map parammap) throws ITFEBizException {
		List datalist = null;
		TvVoucherinfoDto vdto = null;
		if(parammap!=null)
			vdto=(TvVoucherinfoDto)parammap.get("searchdto");
		try {
			datalist = getQueryDataList(vdto);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException(e);
		}
		if(datalist!=null&&datalist.size()>0)
			return voucher.queryVoucherReturnStatus(datalist);
		else
			return null;
	}
	@SuppressWarnings("unchecked")
	private List getQueryDataList(TvVoucherinfoDto vdto) throws JAFDatabaseException
	{
		List datalist = new ArrayList();
		StringBuffer sql = null;
		SQLExecutor execDetail = null;
		try
		{
			if(vdto!=null)
			{
				sql = new StringBuffer("");
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				if(MsgConstant.VOUCHER_NO_3507.equals(vdto.getSvtcode()))
				{
					sql.append("SELECT * FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_VTCODE= ? AND S_ORGCODE= ? ");
					sql.append(vdto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
					sql.append(vdto.getSext1()==null?"":" AND S_EXT1=? ");
					sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
					sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ? ");//S_CONFIRUSERCODE
					execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
					execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
					execDetail.addParam(vdto.getSorgcode());
					if(vdto.getSpaybankcode()!=null)
						execDetail.addParam(vdto.getSpaybankcode());
					if(vdto.getSext1()!=null)
						execDetail.addParam(vdto.getSext1());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					List querylist=  (List<IDto>)execDetail.runQuery(sql.toString(),TvVoucherinfoDto.class).getDtoCollection();//��ʷ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
					execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
					execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
					execDetail.addParam(vdto.getSorgcode());
					if(vdto.getSpaybankcode()!=null)
						execDetail.addParam(vdto.getSpaybankcode());
					if(vdto.getSext1()!=null)
						execDetail.addParam(vdto.getSext1());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					querylist=  (List<IDto>)execDetail.runQueryCloseCon(sql.toString().replace("TV_VOUCHERINFO", "HTV_VOUCHERINFO"),TvVoucherinfoDto.class).getDtoCollection();//��ǰ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
				}else if(MsgConstant.VOUCHER_NO_3508.equals(vdto.getSvtcode()))
				{
					sql.append("SELECT * FROM TV_VOUCHERINFO WHERE (S_VTCODE= ? OR S_VTCODE= ?"+(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,")?" OR S_VTCODE= ? OR S_VTCODE= ?)":")")+"  AND S_ORGCODE= ?");
					sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
					sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ? ");
					if(vdto.getSext3()!=null&&"011".equals(vdto.getSext3()))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
							sql.append(" and S_ATTACH<>?");
					else if(vdto.getSext3()!=null&&"012".equals(vdto.getSext3()))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
						sql.append(" and S_ATTACH=?");
					execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
					execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
					if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
					{
						execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
						execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
					}
					execDetail.addParam(vdto.getSorgcode());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					if(vdto.getSext3()!=null&&("011".equals(vdto.getSext3())||"012".equals(vdto.getSext3())))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
						execDetail.addParam("012");
					List querylist=  (List<IDto>)execDetail.runQuery(sql.toString(),TvVoucherinfoDto.class).getDtoCollection();//��ʷ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
					execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
					execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
					if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
					{
						execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
						execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
					}
					execDetail.addParam(vdto.getSorgcode());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					if(vdto.getSext3()!=null&&("011".equals(vdto.getSext3())||"012".equals(vdto.getSext3())))//���ϵ�˰Ҳ����ʵ���ʽ𡢲���Ҳ����ʵ���ʽ�011����012��˰
						execDetail.addParam("012");
					querylist=  (List<IDto>)execDetail.runQueryCloseCon(sql.toString().replace("TV_VOUCHERINFO", "HTV_VOUCHERINFO"),TvVoucherinfoDto.class).getDtoCollection();//��ǰ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
					
				}else if(MsgConstant.VOUCHER_NO_3509.equals(vdto.getSvtcode()))
				{
					sql.append("SELECT * FROM TV_VOUCHERINFO WHERE (S_VTCODE= ? OR S_VTCODE= ?)  AND S_ORGCODE= ? ");
					sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
					sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ? ");
					execDetail.addParam(MsgConstant.VOUCHER_NO_5209);
					execDetail.addParam(MsgConstant.VOUCHER_NO_3210);
					execDetail.addParam(vdto.getSorgcode());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					List querylist=  (List<IDto>)execDetail.runQuery(sql.toString(),TvVoucherinfoDto.class).getDtoCollection();//��ʷ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
					execDetail.addParam(MsgConstant.VOUCHER_NO_5209);
					execDetail.addParam(MsgConstant.VOUCHER_NO_3210);
					execDetail.addParam(vdto.getSorgcode());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					querylist=  (List<IDto>)execDetail.runQueryCloseCon(sql.toString().replace("TV_VOUCHERINFO", "HTV_VOUCHERINFO"),TvVoucherinfoDto.class).getDtoCollection();//��ǰ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
				}else if(MsgConstant.VOUCHER_NO_3510.equals(vdto.getSvtcode()))
				{
					sql.append("SELECT * FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
					sql.append(vdto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
					sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
					sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?");
					execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
					execDetail.addParam(vdto.getSorgcode());
					if(vdto.getSpaybankcode()!=null)
						execDetail.addParam(vdto.getSpaybankcode());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					List querylist=  (List<IDto>)execDetail.runQuery(sql.toString(),TvVoucherinfoDto.class).getDtoCollection();//��ʷ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
					execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
					execDetail.addParam(vdto.getSorgcode());
					if(vdto.getSpaybankcode()!=null)
						execDetail.addParam(vdto.getSpaybankcode());
					execDetail.addParam(vdto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vdto.getShold3());
					execDetail.addParam(vdto.getShold4());
					querylist=  (List<IDto>)execDetail.runQueryCloseCon(sql.toString().replace("TV_VOUCHERINFO", "HTV_VOUCHERINFO"),TvVoucherinfoDto.class).getDtoCollection();//��ǰ��
					if(querylist!=null&&querylist.size()>0)
						datalist.addAll(querylist);
				}
	
			}
		}catch(JAFDatabaseException e)
		{
			throw e;
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return datalist;
	}
	
	public Integer voucherReturnBack(List list) throws ITFEBizException {
		for (TvVoucherinfoDto dto : (List<TvVoucherinfoDto>) list) {
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
				break;
			SrvCacheFacade.removeCacheVoucherCompare(dto);
		}
		try{
			return voucher.voucherReturnBack(list);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public Integer voucherReturnSuccess(List succList) throws ITFEBizException {
		try {
			return voucher.voucherReturnSuccess(succList);
		} catch (Exception e) {
			log.error(e);
			VoucherException.saveErrInfo(((TvVoucherinfoDto) succList.get(0))
					.getSvtcode(), e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}

	public Integer voucherStamp(List lists) throws ITFEBizException {
		try{
			return voucher.voucherStamp(lists);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public Integer voucherStampCancle(List lists) throws ITFEBizException {
		try{
			return voucher.voucherStampCancle(lists);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public String queryVoucherPrintCount(TvVoucherinfoDto dto) {
		String err = null;
		VoucherService voucherService;
		try {
			voucherService = new VoucherService();
			err = voucherService.queryVoucherPrintCount(null, dto
					.getSadmdivcode(), Integer.parseInt(dto.getSstyear()), dto
					.getSvtcode(), dto.getSvoucherno());
		} catch (ITFEBizException e) {
			log.error(e);
			return err;
		}
		return err;
	}

	public Integer voucherVerify(List checkList) throws ITFEBizException {
		try{
			return voucher.voucherVerify(checkList);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public List voucherGenerate(List list) throws ITFEBizException {
		try{
			return voucher.voucherGenerate(list);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public String voucherStampXml(TvVoucherinfoDto dto) throws ITFEBizException {
		try{
			return VoucherUtil.getVoucherStampXML(dto);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public MulitTableDto voucherCheckRetBack(List list) throws ITFEBizException {
		List<TvVoucherinfoDto> allList = new ArrayList<TvVoucherinfoDto>();
		for (int i = 0; i < list.size(); i++) {
			TvVoucherinfoDto dto = (TvVoucherinfoDto) ((TvVoucherinfoDto) list
					.get(i)).clone();
			// if(dto.getSdemo().length()>50){
			// dto.setSdemo(dto.getSdemo().substring(0, 50));
			// }
			allList.add(dto);
		}
		String svtcode = allList.get(0).getSvtcode();
		String retFlag = VoucherUtil.returnTableNameByVtcode(svtcode);
		MulitTableDto _dto = new MulitTableDto();
		int count = 0;
		// �ж��Ƿ���ƶ�����
		if (ITFECommonConstant.ISCHECKPAYPLAN.equals(StateConstant.COMMON_YES)
				&& !"".equals(retFlag)) {
			List<TvVoucherinfoDto> checkerrList = VoucherAmtUtil
					.updateAmtByVoucher(list, svtcode,
							DealCodeConstants.VOUCHER_FAIL);
			_dto.setErrCheckList(checkerrList);
			allList.removeAll(checkerrList);
		}

		if (allList.size() > 0) {
			count = voucher.voucherReturnBack(allList);
		}
		_dto.setTotalCount(count);
		return _dto;
	}

	public String getOfficialStamp() throws ITFEBizException {
		return ITFECommonConstant.OFFICIALSTAMP;
	}

	public String getRotaryStamp() throws ITFEBizException {
		return ITFECommonConstant.ROTARYSTAMP;
	}

	/**
	 * ȡ���Ƿ���ж�ȿ��Ʊ�־
	 * 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String getIscheckpayplan() throws ITFEBizException {
		return ITFECommonConstant.ISCHECKPAYPLAN;
	}

	/**
	 * ��ȿ���У��
	 * 
	 * @generated
	 * @param list
	 * @return java.lang.Integer
	 * @throws ITFEBizException
	 */
	public MulitTableDto amtControlVerify(List list) throws ITFEBizException {
		List<TvVoucherinfoDto> voucherFailList = new ArrayList<TvVoucherinfoDto>();
		MulitTableDto _dto = new MulitTableDto();
		if (!"".equals(VoucherUtil
				.returnTableNameByVtcode(((TvVoucherinfoDto) list.get(0))
						.getSvtcode()))) {
			voucherFailList = VoucherAmtUtil.updateAmtByVoucher(list,
					((TvVoucherinfoDto) list.get(0)).getSvtcode(),
					DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			list.removeAll(voucherFailList);
		}
		_dto.setErrCheckList(voucherFailList);
		_dto.setTotalCount(list.size());
		try {
			// ����ƾ֤�������ҵ���״̬(�ɹ���¼)
			for (int i = 0; i < list.size(); i++) {
				VoucherUtil.voucherVerifyBatchUpdateStatus(
						(TvVoucherinfoDto) (list.get(i)), true);
			}
			// ����ƾ֤�������ҵ���״̬(ʧ�ܼ�¼)
			for (int i = 0; i < voucherFailList.size(); i++) {
				VoucherUtil.voucherVerifyBatchUpdateStatus(
						(TvVoucherinfoDto) (voucherFailList.get(i)), false);
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ȿ��Ƽ������", e);
		}
		return _dto;
	}

	/**
	 * ȡ�û��������Ƿ�ͨ��ǰ���ύ
	 * 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String getIsitfecommit() throws ITFEBizException {
		return ITFECommonConstant.ISITFECOMMIT;
	}

	/**
	 * ȡ��ocxǩ�·����ַ
	 * 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String getOCXStampServerURL() throws ITFEBizException {
		return ITFECommonConstant.OCXSERVICE_STAMPURL;
	}

	/**
	 * ����ƾ֤����(��ɫͨ��)
	 * 
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void sendData(TvVoucherinfoDto dto) throws ITFEBizException {
		try{
			voucher.sendData(dto);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * ����ƾ֤����(��ɫͨ��)
	 * 
	 * @param dto
	 * @throws ITFEBizException
	 */
	public Integer getData(TvVoucherinfoDto dto) throws ITFEBizException {
		try{
			return VoucherUtil.recvVoucherAttach(dto);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * У��ʵ���ʽ�ҵ�����Ƿ�Ϊ������Ŀ
	 * 
	 * @param voucher
	 * @throws ITFEBizException
	 */
	public String verifyPayOutMoveFunSubject(TvVoucherinfoDto voucher)
			throws ITFEBizException {
		TvPayoutmsgmainDto mainDto = new TvPayoutmsgmainDto();
		mainDto.setSbizno(voucher.getSdealno());// ������ˮ��
		mainDto.setStaxticketno(voucher.getSvoucherno());// ƾ֤���
		mainDto.setSorgcode(voucher.getSorgcode());// �����������
		mainDto.setStrecode(voucher.getStrecode());// �������
		try {
			String ischeck = ITFECommonConstant.CHECKPAYOUTSUBJECT;
			if (ischeck != null
					&& ischeck.equals(MsgConstant.VOUCHER_CHECKPAYOUTSUBJECT)) {
				// �����������ѯʵ���ʽ�����
				List<TvPayoutmsgmainDto> mainDtoList = CommonFacade.getODB()
						.findRsByDto(mainDto);
				if (null != mainDtoList && mainDtoList.size() > 0) {
					TvPayoutmsgmainDto payoutMainDto = mainDtoList.get(0);
					if (payoutMainDto.getSrecbankno().startsWith("011")) {
						// ���ݽ�����ˮ�Ż�����б�
						TvPayoutmsgsubDto subDto = new TvPayoutmsgsubDto();
						subDto.setSbizno(payoutMainDto.getSbizno());
						List<TvPayoutmsgsubDto> subDtoList = CommonFacade
								.getODB().findRsByDto(subDto);
						if (null != subDtoList && subDtoList.size() > 0) {
							Map<String, TsBudgetsubjectDto> smap = SrvCacheFacade
									.cacheTsBdgsbtInfo(payoutMainDto
											.getSorgcode());
							for (TvPayoutmsgsubDto tvPayoutmsgsubDto : subDtoList) {
								String funccode = tvPayoutmsgsubDto
										.getSfunsubjectcode();
								TsBudgetsubjectDto dto = smap.get(funccode);
								if (null == funccode || "".equals(funccode)) {
									return "��ϸ��Ϣ�д��ڹ��ܿ�Ŀ����Ϊ�յļ�¼!";
								}
								if (null == dto
										|| "".equals(dto.getSsubjectcode())) {
									return "���ܿ�Ŀ����[" + funccode + "]������!";
								} else {
									if (MsgConstant.MOVE_FUND_SIGN_NO
											.equals(dto.getSmoveflag())) {
										return "���ܿ�Ŀ����[" + funccode + "]Ϊ�ǵ���!";
									}
								}
							}
						}
					}
				}
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("У��ʵ���ʽ�ҵ�����Ƿ�Ϊ������Ŀ����", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("У��ʵ���ʽ�ҵ�����Ƿ�Ϊ������Ŀ����", e);
		}
		return "";
	}

	public List exportPDF(TvVoucherinfoDto dto) throws ITFEBizException {
		byte[] pdfbyte = voucher.exportPDF(dto);
		List list = new ArrayList<byte[]>();
		list.add(pdfbyte);
		return list;
	}

	public StringBuffer exportData(List voucherList) throws ITFEBizException {
		StringBuffer returnString = new StringBuffer("");
		try{
			if(voucherList!=null&&voucherList.size()>0)
			{
				for(int i=0;i<voucherList.size();i++)
					returnString.append(VoucherUtil.getVoucherStampXML((TvVoucherinfoDto)voucherList.get(i))+System.getProperty("line.separator"));
			}
		}catch(Exception e)
		{
			log.error(e);
			throw new ITFEBizException("�������ݳ���", e);
		}
		return returnString;
	}
}