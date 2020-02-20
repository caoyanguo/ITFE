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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundSubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �����˸����ˣ�3509��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3509 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3509.class);
	/**
	 * ƾ֤����
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	*/
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		if(vDto==null)
			return null;						
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		return getVoucher(vDto);	
	}
	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List returnList = new ArrayList();
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List<TsConvertfinorgDto> tsConvertfinorgList=(List<TsConvertfinorgDto>) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("���⣺"+vDto.getStrecode()+"��Ӧ�Ĳ������ش������δά����");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("���⣺"+cDto.getStrecode()+"��Ӧ����������δά����");
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			List<List<IDto>> dataList = getSplitPack(getDataList(vDto,execDetail));
			if(dataList!=null&&dataList.size()>0)
			{
				List<List> sendList = null;
				List mapList=null;
				for(int k=0;k<dataList.size();k++)
				{
					sendList = this.getSubLists(dataList.get(k), 500);
					if(sendList!=null&&sendList.size()>0)
					{
						List<IDto> tempList = null;
						String danhao=null;
						for(int i=0;i<sendList.size();i++)
						{
							mapList=new ArrayList();
							tempList = sendList.get(i);
							String FileName=null;
							String dirsep = File.separator; 
							String mainvou=VoucherUtil.getGrantSequence();
							vDto.setSdealno(mainvou);
							vDto.setSvoucherno(mainvou);
							if(danhao==null)
								danhao=mainvou;
							FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
							TvVoucherinfoDto dto=new TvVoucherinfoDto();			
							dto.setSorgcode(vDto.getSorgcode());
							dto.setSadmdivcode(vDto.getSadmdivcode());
							dto.setSvtcode(vDto.getSvtcode());
							dto.setScreatdate(TimeFacade.getCurrentStringTime());
							dto.setStrecode(vDto.getStrecode());
							dto.setSfilename(FileName);
							dto.setSdealno(mainvou);		
							dto.setSstyear(dto.getScreatdate().substring(0, 4));				
							dto.setScheckdate(vDto.getScheckdate());
							dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
							dto.setShold3(vDto.getShold3());
							dto.setShold4(vDto.getShold4());
//							if(ITFECommonConstant.PUBLICPARAM.indexOf(",stampmode=sign,")<0)//�Ƿ����ǩ��
//							{
							dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
							dto.setSdemo("����ɹ�");
							dto.setSvoucherflag("1");
							dto.setSvoucherno(mainvou);
							dto.setSattach(danhao);//���˵���
							dto.setShold1(String.valueOf(sendList.size()));//������
							dto.setShold2(String.valueOf(i+1));//�����
							dto.setIcount(tempList.size());
							dto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
							dto.setShold3("");
							dto.setShold4("");
							dto.setSext2("");
							dto.setSext3("");
							dto.setSext4("");
							dto.setSext5("");
							mapList.add(vDto);
							mapList.add(cDto);
							mapList.add(tempList);
							List<IDto> idtoList = new ArrayList<IDto>();
							Map map=tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
							dto.setNmoney(MtoCodeTrans.transformBigDecimal(map.get("Voucher")==null?null:((Map)map.get("Voucher")).get("AllAmt")));
							dto.setIcount(Integer.valueOf(String.valueOf((map.get("Voucher")==null?null:((Map)map.get("Voucher")).get("AllNum")))));
							List vouList=new ArrayList();
							vouList.add(map);
							vouList.add(dto);
							vouList.add(idtoList);
							returnList.add(vouList);
						}
					}
				}
			}
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return returnList;
	}
	private Map tranfor(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			List<IDto> detailList=(List<IDto>) mapList.get(2);
			TvDwbkDto pdto = null;
			TvVoucherinfoAllocateIncomeDto tadto = null;
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//������������
			vouchermap.put("StYear",vDto.getShold3().substring(0,4));//ҵ�����
			vouchermap.put("VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
			vouchermap.put("VouDate",vDto.getScreatdate());//ƾ֤����
			vouchermap.put("VoucherNo",vDto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",danhao);//���˵���
			vouchermap.put("ChildPackNum",count);//�Ӱ�����
			vouchermap.put("CurPackNo",xuhao);//�������
			vouchermap.put("TreCode",vDto.getStrecode());//�����������
			vouchermap.put("BeginDate",vDto.getShold3());//������ʼ����
			vouchermap.put("EndDate",vDto.getShold4());//������ֹ����
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for(IDto idto:detailList)
			{
				setIdtoToMap(vouchermap,idto);
				if(idto instanceof TvDwbkDto)
				{
					pdto = (TvDwbkDto)idto;
					id++;
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id",vDto.getSvoucherno()+id);//�˸���ϸId
					if("000057400006".equals(ITFECommonConstant.SRC_NODE))//����ר��
						Detailmap.put("PayDetailId",(pdto.getSdetailhold2()==null?(vDto.getSvoucherno()+id):pdto.getSdetailhold2()));//���;
					else
						Detailmap.put("PayDetailId",(pdto.getSdetailhold4()==null?(vDto.getSvoucherno()+id):pdto.getSdetailhold4()));//���
					Detailmap.put("FundTypeCode",getString(pdto.getSfundtypecode()));//�ʽ����ʱ���
					Detailmap.put("FundTypeName",getString(pdto.getSfundtypename()));//�ʽ���������
					Detailmap.put("PayeeAcctNo",getString(pdto.getSpayeeacct()));//�տ����˺�
					Detailmap.put("PayeeAcctName",getString(pdto.getSpayeename()));//�տ�������
					Detailmap.put("PayeeAcctBankName",getString(pdto.getSpayacctbankname()));//�տ�������
					Detailmap.put("PayAcctNo",getString(pdto.getSpayacctno()));//�����˻��˺�
					Detailmap.put("PayAcctName",getString(pdto.getSpayacctname()));//�����˻�����
					Detailmap.put("PayAcctBankName",getString(pdto.getSpayacctbankname()));//�����˻�����
					Detailmap.put("AgencyCode",getString(pdto.getSpayeecode()));//Ԥ�㵥λ����
					Detailmap.put("AgencyName",getString(pdto.getSagencyname()));//Ԥ�㵥λ����
					Detailmap.put("IncomeSortCode",getString(""));//��������Ŀ����
					Detailmap.put("IncomeSortName",getString(""));//��������Ŀ����
					Detailmap.put("PayAmt",String.valueOf(pdto.getFamt()));//�˸����
					Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
					Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
					Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
					Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
					if(!"1".equals(pdto.getCbckflag()))
						allamt=allamt.add(pdto.getFamt());
//					else
//						allamt=allamt.subtract(subdto.getNmoney());
					Detail.add(Detailmap);
					subdtolist.add(getSubDto(Detailmap,vouchermap));
				}else if(idto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					tadto = (TvVoucherinfoAllocateIncomeDto)idto;
					id++;
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id",vDto.getSvoucherno()+String.valueOf(tadto.getSdealno())+id);//���
					Detailmap.put("PayDetailId",vDto.getSvoucherno()+String.valueOf(tadto.getSdealno())+id);//�˸���ϸId
					Detailmap.put("FundTypeCode",getString("9"));//�ʽ����ʱ���
					Detailmap.put("FundTypeName",getString("�����ʽ�"));//�ʽ���������
					Detailmap.put("PayeeAcctNo",getString(tadto.getSpayeeacctno()));//�տ����˺�
					Detailmap.put("PayeeAcctName",getString(tadto.getSpayeeacctname()));//�տ�������
					Detailmap.put("PayeeAcctBankName",getString(tadto.getSpayeeacctbankname()));//�տ�������
					Detailmap.put("PayAcctNo",getString(tadto.getSpayacctno()));//�����˻��˺�
					Detailmap.put("PayAcctName",getString(tadto.getSpayacctname()));//�����˻�����
					Detailmap.put("PayAcctBankName",getString(tadto.getSpayacctbankname()));//�����˻�����
					Detailmap.put("AgencyCode",getString(""));//Ԥ�㵥λ����
					Detailmap.put("AgencyName",getString(""));//Ԥ�㵥λ����
					Detailmap.put("IncomeSortCode",getString(""));//��������Ŀ����
					Detailmap.put("IncomeSortName",getString(""));//��������Ŀ����
					Detailmap.put("PayAmt",String.valueOf(tadto.getNmoney()));//�˸����
					Detailmap.put("Hold1",getString(""));//Ԥ���ֶ�1
					Detailmap.put("Hold2",getString(""));//Ԥ���ֶ�2
					Detailmap.put("Hold3",getString(""));//Ԥ���ֶ�3
					Detailmap.put("Hold4",getString(""));//Ԥ���ֶ�4
					allamt=allamt.subtract(tadto.getNmoney());
					Detail.add(Detailmap);
					subdtolist.add(getSubDto(Detailmap,vouchermap));
				}
			}
			vouchermap.put("AllNum",id);//�ܱ���
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));//�ܽ��
			vouchermap.put("Hold1","");//Ԥ���ֶ�1
			vouchermap.put("Hold2","");//Ԥ���ֶ�2
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
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
	private TfReconcileRefundMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReconcileRefundMainDto mainDto = new TfReconcileRefundMainDto();
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		mainDto.setSdemo("����ɹ�");
		mainDto.setSorgcode(vDto.getSorgcode());
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		mainDto.setIvousrlno(Long.valueOf(voucherno));
		mainDto.setSadmdivcode(getString(mainMap,"AdmDivCode"));//AdmDivCode",vDto.getSadmdivcode());//������������
		mainDto.setSstyear(getString(mainMap,"StYear"));//StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
		mainDto.setSvtcode(getString(mainMap,"VtCode"));//VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
		mainDto.setSvoudate(getString(mainMap,"VouDate"));//VouDate",vDto.getScreatdate());//ƾ֤����
		mainDto.setSvoucherno(getString(mainMap,"VoucherNo"));//VoucherNo",vDto.getSvoucherno());//ƾ֤��
		mainDto.setSvouchercheckno(getString(mainMap,"VoucherCheckNo"));//VoucherCheckNo",danhao);//���˵���
		mainDto.setSchildpacknum(getString(mainMap,"ChildPackNum"));//ChildPackNum",count);//�Ӱ�����
		mainDto.setScurpackno(getString(mainMap,"CurPackNo"));//CurPackNo",xuhao);//�������
//		mainDto.setSbillkind(getString(mainMap,"BillKind"));//BillKind","1".equals(vDto.getScheckvouchertype())?"1":"2");//��������1,��Ӧ1-һ��Ԥ��֧������3��Ӧ2-����֧��
//		mainDto.setSfinorgcode(getString(mainMap,"FinOrgCode"));//FinOrgCode",cDto.getSfinorgcode());//�������ش���
		mainDto.setStrecode(getString(mainMap,"TreCode"));//TreCode",vDto.getStrecode());//�����������
//		mainDto.setStrename(getString(mainMap,"TreName"));//TreName"
//		mainDto.setSbgttypecode(getString(mainMap,"BgtTypeCode"));//BgtTypeCode",vDto.getShold1());//Ԥ�����ͱ���
//		mainDto.setSbgttypename(getString(mainMap,"BgtTypeName"));//BgtTypeName","1".equals(vDto.getShold1())?"Ԥ����":"Ԥ����");//Ԥ����������
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//������ʼ����
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//������ֹ����
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//�ܱ���
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//�ܽ��
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
		mainDto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
		return mainDto;
	}
	private TfReconcileRefundSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReconcileRefundSubDto subDto = new TfReconcileRefundSubDto();
		String voucherno = null;
		if("000057400006".equals(ITFECommonConstant.SRC_NODE))//����ר��
			voucherno = getString(subMap,"PayDetailId");
		else
			voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//���
		subDto.setSid(getString(subMap,"Id"));
//		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//ExpFuncCode",dto.getSbudgetsubcode());//֧�����ܷ����Ŀ����
//		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//ExpFuncName",dto.getSbudgetsubname());//֧�����ܷ����Ŀ����
//		subDto.setNcurpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"CurPayAmt")));//CurPayAmt",dto.getNmoneyday());//���ڽ��
//		subDto.setNsumpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"SumPayAmt")));//SumPayAmt",dto.getNmoneymonth());//�ۼƽ��
		subDto.setShold1(getString(subMap,"Hold1"));//Hold1","");//Ԥ���ֶ�1
		subDto.setShold2(getString(subMap,"Hold2"));//Hold2","");//Ԥ���ֶ�2
		subDto.setShold3(getString(subMap,"Hold3"));//Hold3","");//Ԥ���ֶ�3
		subDto.setShold4(getString(subMap,"Hold4"));//Hold4","");//Ԥ���ֶ�4
		subDto.setSxcheckresult("0");//���˽��Ĭ�ϳɹ�
		return subDto;
	}
	private String getString(Map datamap,String key)
	{
		if(datamap==null||key==null)
			return "";
		else
			return String.valueOf(datamap.get(key));
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
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<IDto> getDataList(TvVoucherinfoDto vDto,SQLExecutor execDetail) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sql = new StringBuffer("SELECT * FROM HTV_DWBK WHERE S_BIZNO in(");//��ѯʵ���ʽ���ʷ���Ѿ��ص�������
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE (S_VTCODE= ? OR S_VTCODE= ?)  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?) ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_5209);
			execDetail.addParam(MsgConstant.VOUCHER_NO_3210);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvDwbkDto.class).getDtoCollection();//��ʷ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			//��ѯʵ���ʽ���ʽ���Ѿ��ص�������
			execDetail.addParam(MsgConstant.VOUCHER_NO_5209);
			execDetail.addParam(MsgConstant.VOUCHER_NO_3210);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_DWBK","TV_DWBK"),TvDwbkDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			sql = new StringBuffer("SELECT * FROM TV_VOUCHERINFO_ALLOCATE_INCOME WHERE S_DEALNO IN(");//��ѯʵ���ʽ���ʷ���Ѿ��ص�������
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ?  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?) ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_3210);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvVoucherinfoAllocateIncomeDto.class).getDtoCollection();//��ʽ������
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e);
			throw new ITFEBizException("��ѯ"+sql==null?"":sql.toString()+"3509��ϸ��Ϣ�쳣��",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return getList;
	}
	private List<List<IDto>> getSplitPack(List<IDto> dataList)
	{
		List<List<IDto>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,List<IDto>> tempMap = new HashMap<String,List<IDto>>();
			List<IDto> tempList = null;
			for(IDto idto:dataList)
			{
				if(idto instanceof TvDwbkDto)
				{
					TvDwbkDto pdto = (TvDwbkDto)idto;
//					if(tempMap.get(pdto.getSpayeeacct())==null)
//					{
//						tempList = new ArrayList<IDto>();
//						tempList.add(idto);
//						tempMap.put(pdto.getSpayeeacct(), tempList);
//					}else
//					{
//						tempList = tempMap.get(pdto.getSpayeeacct());
//						tempList.add(idto);
//						tempMap.put(pdto.getSpayeeacct(), tempList);
//					}
					//����Ҫ�󲻰����зְ�
					if(tempList == null){
						tempList = new ArrayList<IDto>();
					}
					tempList.add(idto);
					tempMap.put("000000", tempList);
				}else if(idto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					TvVoucherinfoAllocateIncomeDto adto = (TvVoucherinfoAllocateIncomeDto)idto;
					if(tempMap.get(adto.getSpayacctno())==null)
					{
						tempList = new ArrayList<IDto>();
						tempList.add(idto);
						tempMap.put(adto.getSpayacctno(), tempList);
					}else
					{
						tempList = tempMap.get(adto.getSpayacctno());
						tempList.add(idto);
						tempMap.put(adto.getSpayacctno(), tempList);
					}
				}
			}
			if(tempMap!=null&&tempMap.size()>0)
			{
				getList = new ArrayList<List<IDto>>();
				for(String mapkey:tempMap.keySet())
					getList.add(tempMap.get(mapkey));
			}
		}
		return getList;
	}
	private String getString(String key)
	{
		if(key==null||"null".equals(key)||"NULL".equals(key))
			key="";
		return key;
	}
	private void setIdtoToMap(Map vouchermap,IDto idto)
	{
		if(vouchermap!=null&&idto!=null)
		{
			if(vouchermap.get("ClearBankCode")==null)
				vouchermap.put("ClearBankCode","");
			if(vouchermap.get("ClearBankName")==null)
				vouchermap.put("ClearBankName","");
			if(idto instanceof TvDwbkDto)
			{
				TvDwbkDto pdto = (TvDwbkDto)idto;
				if(vouchermap.get("ClearBankCode")==null||"".equals(vouchermap.get("ClearBankCode")))
				{
					if(!"".equals(getString(pdto.getSclearbankcode())))
						vouchermap.put("ClearBankCode",pdto.getSclearbankcode());//�������б���
				}
				if(vouchermap.get("ClearBankName")==null||"".equals(vouchermap.get("ClearBankName")))
				{
					if(!"".equals(getString(pdto.getSclearbankname())))
						vouchermap.put("ClearBankName",pdto.getSclearbankname());//������������
				}
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayeeacct())))
						vouchermap.put("ClearAccNo",pdto.getSpayeeacct());//�����˺�
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayeename())))
						vouchermap.put("ClearAccNanme",pdto.getSpayeename());//�����˻�����
				}
				//2015.10.20����Ҫ���Ϊ��
				vouchermap.put("ClearAccNo","");//�����˺�
				vouchermap.put("ClearAccNanme","");//�����˻�����
				
			}else if(idto instanceof TvVoucherinfoAllocateIncomeDto)
			{
				TvVoucherinfoAllocateIncomeDto pdto = (TvVoucherinfoAllocateIncomeDto)idto;
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayacctno())))
						vouchermap.put("ClearAccNo",pdto.getSpayacctno());//�����˺�
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayacctname())))
						vouchermap.put("ClearAccNanme",pdto.getSpayacctname());//�����˻�����
				}
			}
		}	
	}
}
