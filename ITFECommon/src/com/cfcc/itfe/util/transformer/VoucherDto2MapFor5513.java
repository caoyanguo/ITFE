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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositSubDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * ���п���˻������������3513��ִ5513ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5513 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5513.class);
											
	/**
	 * ƾ֤����
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	*/
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		if(vDto==null)
			return null;
		return getVoucher(vDto);	
	}
	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List returnList = new ArrayList();
		try {
			MainAndSubDto datadto =  get3513Data(vDto);
			List<IDto> alldata = getDataList(datadto);
			Map<String,Object> getMap = getSplitPack(alldata);
			if(getMap!=null&&getMap.size()>0&&datadto!=null&&datadto.getMainDto()!=null)
			{
				String FileName=null;
				String dirsep = File.separator; 
				String mainvou=VoucherUtil.getGrantSequence();
				FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
				TvVoucherinfoDto dto=new TvVoucherinfoDto();			
				dto.setSorgcode(vDto.getSorgcode());
				dto.setSadmdivcode(vDto.getSadmdivcode());
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5513);
				dto.setScreatdate(TimeFacade.getCurrentStringTime());
				dto.setStrecode(vDto.getStrecode());
				dto.setSfilename(FileName);
				dto.setSdealno(mainvou);		
				dto.setSstyear(dto.getScreatdate().substring(0, 4));				
				dto.setScheckdate(vDto.getScheckdate());
				dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
				dto.setShold3(vDto.getShold3());
				dto.setShold4(vDto.getShold4());
				dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				dto.setSdemo("����ɹ�");
				dto.setSvoucherflag("1");
				dto.setSvoucherno(mainvou);	
				dto.setIcount(getMap.size());
				dto.setSext1(vDto.getSdealno());
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map=tranfor(datadto,getMap,dto);
				List vouList=new ArrayList();
				vouList.add(map);
				vouList.add(dto);
				vouList.add(idtoList);
				returnList.add(vouList);
			}
		} catch (Exception e2) {		
			logger.error(e2);
			throw new ITFEBizException("��Ϣ�쳣��",e2);
		}
		return returnList;
	}
	private Map tranfor(MainAndSubDto datadto,Map<String,Object> getMap,TvVoucherinfoDto dto) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TfReportDepositMainDto main3513dto = (TfReportDepositMainDto)datadto.getMainDto();
			TfReportDepositSubDto sub3513dto = null;
			map.put("Voucher", vouchermap);// ���ñ��Ľڵ� Voucher
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",main3513dto.getSadmdivcode());//������������
			vouchermap.put("StYear",main3513dto.getSstyear());//ҵ�����
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5513);//ƾ֤���ͱ��
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//ƾ֤����
			vouchermap.put("VoucherNo",dto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",main3513dto.getSvouchercheckno());//���˵���
			vouchermap.put("TreCode",main3513dto.getStrecode());//�����������
			vouchermap.put("TreName",main3513dto.getStrename());//������������
			vouchermap.put("AcctNo",main3513dto.getSacctno());//����˺�
			vouchermap.put("AcctName",main3513dto.getSacctname());//����˻�����
			vouchermap.put("BeginDate",main3513dto.getSbegindate());//������ʼ����
			vouchermap.put("EndDate",main3513dto.getSenddate());//������ֹ����
			vouchermap.put("AllNum",getMap.get("allcount"));//�ܱ���
			vouchermap.put("AcctAmt",String.valueOf(main3513dto.getNacctamt()));//�������
			vouchermap.put("XAcctAmt",String.valueOf(getMap.get("allamt")));//�����������
			vouchermap.put("XDiffMoney",String.valueOf(main3513dto.getNacctamt().subtract(new BigDecimal(String.valueOf(getMap.get("allamt")))).abs()));//���
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			Map<String,String> tempMap = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3513dto = (TfReportDepositSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3513dto.getSid());//���
				Detailmap.put("AcctDate",sub3513dto.getSacctdate());//��ϸ����
				Detailmap.put("IncomeAmt",String.valueOf(sub3513dto.getNincomeamt()));//������
				Detailmap.put("PayAmt",String.valueOf(sub3513dto.getNpayamt()));//֧�����
				tempdto = getObject(getMap,sub3513dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
					Detailmap.put("XCheckReason","�޴�ƾ֤");//����ԭ��
				}else if(tempdto instanceof Map)
				{
					tempMap = (Map<String,String>)tempdto;
					if(sub3513dto.getNpayamt()!=null&&sub3513dto.getNincomeamt()!=null&&String.valueOf(sub3513dto.getNpayamt()).equals(tempMap.get("PayAmt"))&&String.valueOf(sub3513dto.getNincomeamt()).equals(tempMap.get("IncomeAmt")))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//���˽��
						Detailmap.put("XCheckReason","");//����ԭ��
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//���˽��
						if(!String.valueOf(sub3513dto.getNpayamt()).equals(tempMap.get("PayAmt")))
							Detailmap.put("XCheckReason","֧������");//����ԭ��
						if(!String.valueOf(sub3513dto.getNincomeamt()).equals(tempMap.get("IncomeAmt")))
							Detailmap.put("XCheckReason","�������");//����ԭ��
					}
					
				}
				Detailmap.put("Hold1",sub3513dto.getShold1());//Ԥ���ֶ�1
				Detailmap.put("Hold2",sub3513dto.getShold2());//Ԥ���ֶ�2
				Detailmap.put("Hold3",sub3513dto.getShold3());//Ԥ���ֶ�3
				Detailmap.put("Hold4",sub3513dto.getShold4());//Ԥ���ֶ�4
				Detail.add(Detailmap);
			}
			vouchermap.put("XDiffNum",error);//��������
			vouchermap.put("Hold1",main3513dto.getShold1());//Ԥ���ֶ�1
			vouchermap.put("Hold2",main3513dto.getShold2());//Ԥ���ֶ�2
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<IDto> getDataList(MainAndSubDto dto3513) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReportDepositMainDto vDto = (TfReportDepositMainDto)dto3513.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sql = new StringBuffer("SELECT * FROM HTR_STOCKDAYRPT WHERE S_RPTDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode());
			if(vDto.getSacctno()!=null&&!"".equals(vDto.getSacctno().trim()))
				sql.append(" and S_ACCNO='"+vDto.getSacctno()+"' ");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TrStockdayrptDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTR_STOCKDAYRPT","TR_STOCKDAYRPT"),TrStockdayrptDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e.getMessage(),e);
			throw new ITFEBizException("��ѯ"+sql==null?"":sql.toString()+"5513�����ϸ��Ϣ�쳣��",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return getList;
	}
	private Map<String,Object> getSplitPack(List<IDto> dataList)
	{
		Map<String,Object> getMap = null;
		BigDecimal allamt=new BigDecimal("0.00");
		if(dataList!=null&&dataList.size()>0)
		{
			getMap = new HashMap<String,Object>();
			Map<String,String> dataMap = null;
			TrStockdayrptDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (TrStockdayrptDto)idto;
				dataMap = new HashMap<String,String>();
				dataMap.put("AcctDate",dto.getSaccdate());//��ϸ����
				dataMap.put("IncomeAmt",String.valueOf(dto.getNmoneyin()));//������
				dataMap.put("PayAmt",String.valueOf(dto.getNmoneyout()));//֧�����
				getMap.put(dto.getSaccdate(),dataMap);
				allamt=dto.getNmoneytoday();
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", getMap.size());
		}
		return getMap;
	}
	private MainAndSubDto get3513Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_REPORT_DEPOSIT_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReportDepositMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_REPORT_DEPOSIT_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReportDepositSubDto.class).getDtoCollection());
				}
				
			} catch (JAFDatabaseException e) {
				logger.error(e.getMessage(),e);
				throw new ITFEBizException("��ִ2507ʱ��ѯ3507������Ϣ�쳣��",e);
			}finally
			{
				if(execDetail!=null)
					execDetail.closeConnection();
			}
		}
		return dataDto;
	}
	private Object getObject(Map map,TfReportDepositSubDto sub3513dto)
	{
		Object getObject = null;
		if(map!=null&&sub3513dto!=null)
		{
			getObject = map.get(sub3513dto.getSacctdate());
		}
		return getObject;
	}
}
