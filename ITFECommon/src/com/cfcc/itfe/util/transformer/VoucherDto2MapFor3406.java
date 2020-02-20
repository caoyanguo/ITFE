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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 ��˰�����ձ���3406��
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3406 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3406.class);
	private  BigDecimal Total;
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		Total=new BigDecimal("0.00");
//		vDto.setScheckdate(TimeFacade.getEndDateOfMonth(vDto.getScheckdate()));
		vDto.setSdealno(VoucherUtil.getGrantSequence());
		//vDto.svtcode��������
		//vDto.shold1Ԥ������
		//vDto.shold2�����ڱ�־
		//vDto.shold3����Χ
		//vDto.shold4��Ŀ����
		//vDto.strecode�����������
		//vDto.scheckdate��������
		//vDto.sadmdivcode ��������
		//vDto.getSattachϽ����־
		TrIncomedayrptDto dto=new TrIncomedayrptDto();
		dto.setStrecode(vDto.getStrecode());//�������
		dto.setStaxorgcode(vDto.getSpaybankcode());//���ջ��ش���
		dto.setSbudgetsubcode(vDto.getShold4());//Ԥ���Ŀ����
		dto.setSrptdate(vDto.getScheckdate());//��������
		dto.setSbelongflag(vDto.getSattach());//Ͻ����־
		dto.setStrimflag(vDto.getShold2());//�����ڱ�־
		dto.setSbudgettype(vDto.getShold1());//Ԥ������
		dto.setSbudgetlevelcode(vDto.getSext1());//Ԥ�㼶��
		return tranforList(dto,vDto);
	}
	private List tranforList(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List<TrIncomedayrptDto> list=null;
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List TsConvertfinorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
			if(TsConvertfinorgDtoList==null||TsConvertfinorgDtoList.size()==0){
				throw new ITFEBizException("���⣺"+cDto.getStrecode()+" ��Ӧ�Ĳ�������δά����");
			}
			cDto=(TsConvertfinorgDto) TsConvertfinorgDtoList.get(0);
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){			
				throw new ITFEBizException("���⣺"+cDto.getStrecode()+" ��Ӧ����������δά����");
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			StringBuffer sql = new StringBuffer("SELECT * FROM TR_INCOMEDAYRPT where ");
			if("0".equals(vDto.getShold3()))//�������¼�
			{
				sql.append(" S_FINORGCODE=? and S_TRECODE = ?");
				execDetail.addParam(cDto.getSfinorgcode());
				execDetail.addParam(dto.getStrecode());
			}else
			{
				sql.append(" (S_TRECODE = ?");
				execDetail.addParam(dto.getStrecode());
				TsTreasuryDto tDto=new TsTreasuryDto();
				tDto.setSgoverntrecode(dto.getStrecode());
				List<TsTreasuryDto> trelist = CommonFacade.getODB().findRsByDto(tDto);
				if(trelist!=null&&trelist.size()>0)
				{
					for(TsTreasuryDto tsDto:trelist)
					{
						if(!tsDto.getStrecode().equals(dto.getStrecode()))
						{
							sql.append(" or S_TRECODE = ? ");
							execDetail.addParam(tsDto.getStrecode());
						}
					}
				}
				sql.append(")");
			}
			if(dto.getSrptdate()!=null)
			{
				sql.append(" and S_RPTDATE = ?");
				execDetail.addParam(dto.getSrptdate());
			}
			if(dto.getStaxorgcode()!=null)
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(dto.getStaxorgcode());
			}
			if(dto.getSbudgetsubcode()!=null)
			{
				sql.append(" and S_BUDGETSUBCODE = ?");
				execDetail.addParam(dto.getSbudgetsubcode());
			}
			if(dto.getSbelongflag()!=null)
			{
				sql.append(" and S_BELONGFLAG = ?");
				execDetail.addParam(dto.getSbelongflag());
			}
			if(dto.getStrimflag()!=null)
			{
				sql.append(" and S_TRIMFLAG = ?");
				execDetail.addParam(dto.getStrimflag());
			}
			if(dto.getSbudgettype()!=null)
			{
				sql.append(" and S_BUDGETTYPE = ?");
				execDetail.addParam(dto.getSbudgettype());
			}
			if(dto.getSbudgetlevelcode()!=null)
			{
				sql.append(" and S_BUDGETLEVELCODE = ?");
				execDetail.addParam(dto.getSbudgetlevelcode());
			}
			sql.append(" order by S_TRECODE,S_BUDGETSUBCODE ");
			list = (List)execDetail.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}catch(Exception e2 ){
			logger.error(e2);
			throw new ITFEBizException(e2.getMessage(),e2);
		}finally{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		if(list==null||list.size()==0){
			return null;
		}
		String dirsep = File.separator;
		String mainvou=VoucherUtil.getGrantSequence();
		String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ TimeFacade.getCurrentStringTime()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
		TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
		voucherdto.setSdealno(mainvou);
		voucherdto.setSorgcode(vDto.getSorgcode());
		voucherdto.setSadmdivcode(vDto.getSadmdivcode());
		voucherdto.setSvtcode(vDto.getSvtcode());
		voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//ƾ֤����
		voucherdto.setScheckdate(vDto.getScheckdate());//��������
		voucherdto.setStrecode(vDto.getStrecode());
		voucherdto.setSadmdivcode(cDto.getSadmdivcode());
		voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
		voucherdto.setSattach(getString(vDto.getSattach()));//Ͻ����־
		voucherdto.setSpaybankcode(getString(vDto.getSpaybankcode()));//���ջ��ش���
		voucherdto.setShold1(getString(vDto.getShold1()));//Ԥ������
		voucherdto.setShold2(getString(vDto.getShold2()));//�����ڱ�־
		voucherdto.setShold3(getString(vDto.getShold3()));//����Χ
		voucherdto.setShold4(getString(vDto.getShold4()));//��Ŀ����
		voucherdto.setSext1(getString(vDto.getSext1()));//Ԥ�㼶��
		voucherdto.setSfilename(FileName);
		voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		voucherdto.setSdemo("����ɹ�");				
		voucherdto.setSvoucherflag("1");
		voucherdto.setSvoucherno(mainvou);		
		voucherdto.setSvoucherflag("1");
		voucherdto.setSvoucherno(mainvou);
		voucherdto.setIcount(list.size());
		List lists=new ArrayList();
		lists.add(list);
		lists.add(voucherdto);
		lists.add(cDto);
		Map map= tranfor(lists);				
		List voucherList=new ArrayList();
		voucherList.add(map);
		voucherList.add(voucherdto);
		List listss=new ArrayList();
		listss.add(voucherList);
		return listss;
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
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//������������
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));	//ҵ�����	
			vouchermap.put("VtCode", vDto.getSvtcode());//ƾ֤���ͱ��		
			vouchermap.put("VouDate", vDto.getScreatdate());//ƾ֤����		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//ƾ֤��
			vouchermap.put("BillKind", vDto.getShold3());//�������ࡢ��Χ
			vouchermap.put("BudgetType", vDto.getShold1());//Ԥ������
			vouchermap.put("ReportDate", vDto.getScheckdate());//��������
			vouchermap.put("FinOrgCode", cDto.getSfinorgcode());//�������ش���
			vouchermap.put("FundTypeCode", "1");//�ʽ�����
			vouchermap.put("TreCode", vDto.getStrecode());//�������
			vouchermap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//��������
			vouchermap.put("Hold1", vDto.getShold4());//Ԥ���ֶ�1-Ԥ���Ŀ����ռ��		
			vouchermap.put("Hold2", "");//Ԥ���ֶ�2	
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			for (TrIncomedayrptDto dto:list) {

				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", VoucherUtil.getDetailSequence(vDto.getSdealno(), id+1));
				Detailmap.put("TreCode", dto.getStrecode()); //�¼������������
				Detailmap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(dto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(dto.getStrecode()).getStrename()); //�¼�������������
				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("YearAmt", MtoCodeTrans.transformString(dto.getNmoneyyear())); 
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
				allamt=allamt.add(dto.getNmoneyday());										
				Detail.add(Detailmap);
			}		
			Total=Total.add(allamt);			
			vouchermap.put("SumMoney",MtoCodeTrans.transformString(allamt));	
			vDto.setNmoney(Total);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
		
	}private String getString(String key)
	{
		if(key==null||"null".equals(key)||"NULL".equals(key))
			key="";
		return key;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}
}
