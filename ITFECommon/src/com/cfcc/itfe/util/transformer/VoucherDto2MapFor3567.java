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
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.ReportLKXMUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �����±���3507ת��-->3567���������±�
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3567 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3567.class);
	private  BigDecimal Total;
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		Total=new BigDecimal("0.00");
		vDto.setScheckdate(TimeFacade.getEndDateOfMonth(vDto.getScheckdate()));
		vDto.setSdealno(VoucherUtil.getGrantSequence());
//		vDto.getSorgcode();//��������
//		vDto.getScreatdate();//ƾ֤����
//		vDto.getSvtcode();//ƾ֤����3453-ȫʡ����ձ���3401-�����ձ���3402-����ձ���,3567-�����±���
//		vDto.getStrecode();//�������
//		vDto.getSstyear();//��������1-�ձ� 2-�±� 3-�걨
//		vDto.getSpaybankcode();//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
//		vDto.getShold1();//Ԥ������1-Ԥ����2-Ԥ����
//		vDto.getShold2();//Ͻ����־0-������1-ȫϽ
//		vDto.getShold3();//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��
//		vDto.getShold4();//��������1-������Ԥ�����뱨��2-�������˿ⱨ����3-�����ڵ������뱨��4-�ܶ�ֳɱ�����5-������Ԥ�����뱨����6-�������˿ⱨ����7-�����ڵ������뱨����
		//S_TRIMFLAG IS '�����ڱ�־0 �����(������)1 �����(������)';S_BUDGETTYPE IS 'Ԥ������1-Ԥ����2-Ԥ����';S_BUDGETLEVELCODE IS 'Ԥ�㼶�δ���';.S_BELONGFLAG IS 'Ͻ����־0-������1-ȫϽ';
		//S_FINORGCODE IS '�������ش���';S_TAXORGCODE IS '���ջ��ش���';S_BILLKIND IS '��������1-������Ԥ�����뱨���壬2-�������˿ⱨ���壬3-�����ڵ������뱨���壬4-�ܶ�ֳɱ����壬5-�����ڹ���ֳɱ����壬6-������Ԥ�����뱨���壬7-�������˿ⱨ���壬8-�����ڵ������뱨���壬9-�����ڹ���ֳɱ�����';
		TrIncomedayrptDto dto=new TrIncomedayrptDto();
		dto.setStrecode(vDto.getStrecode());//�������
		dto.setStaxorgcode(vDto.getSpaybankcode());//���ջ��ش���
		dto.setSrptdate(vDto.getScheckdate());//��������
		dto.setSbelongflag(vDto.getShold3());//Ͻ����־
		dto.setSbudgetlevelcode(vDto.getShold2());//Ԥ�㼶��
		dto.setSbudgettype(vDto.getShold1());//Ԥ������
		dto.setSbillkind(vDto.getScheckvouchertype());//��������
//		if(vDto.getShold2()!=null&&"6".equals(vDto.getShold2()))
//			return tranfordf(dto,vDto);
//		else
//			return tranforList(dto,vDto);
		List getList = new ArrayList();
		List tempList = null;
		String shold2 = vDto.getShold2();
		List<String> ysjc = new ArrayList<String>();
		List<String> bbzl = new ArrayList<String>();
		ysjc.add("1");ysjc.add("2");ysjc.add("3");ysjc.add("4");ysjc.add("5");//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��
		bbzl.add("1");bbzl.add("2");bbzl.add("3");bbzl.add("4");bbzl.add("6");bbzl.add("7");bbzl.add("8");//1-������Ԥ�����뱨���壬2-�������˿ⱨ���壬3-�����ڵ������뱨���壬4-�ܶ�ֳɱ����壬6-������Ԥ�����뱨���壬7-�������˿ⱨ���壬8-�����ڵ������뱨����
		String shold4 = vDto.getScheckvouchertype();
		if(shold2!=null&&"6".equals(shold2))//�ط�������
		{
			tempList = tranfordf(dto,vDto);
			if(tempList!=null&&tempList.size()>0)
				getList.add(tempList);
		}else if(shold2==null||"".equals(shold2))
		{
			for(int ys=0;ys<ysjc.size();ys++)
			{
				dto.setStrecode(vDto.getStrecode());//�������
				dto.setStaxorgcode(vDto.getSpaybankcode());//���ջ��ش���
				dto.setSrptdate(vDto.getScheckdate());//��������
				vDto.setShold2(ysjc.get(ys));
				dto.setSbudgetlevelcode(ysjc.get(ys));
				if(shold4==null||"".equals(shold4))
				{
					for(int bb=0;bb<bbzl.size();bb++)
					{
						vDto.setScheckvouchertype(bbzl.get(bb));
						dto.setSbillkind(bbzl.get(bb));
						tempList = tranforList(dto,vDto);
						if(tempList!=null&&tempList.size()>0)
							getList.add(tempList);
					}
				}else
				{
					tempList = tranforList(dto,vDto);
					if(tempList!=null&&tempList.size()>0)
						getList.add(tempList);
				}
			}	
		}else if(shold4==null||"".equals(shold4))
		{
			for(int bb=0;bb<bbzl.size();bb++)
			{
				vDto.setScheckvouchertype(bbzl.get(bb));
				dto.setSbillkind(bbzl.get(bb));
				tempList = tranforList(dto,vDto);
				if(tempList!=null&&tempList.size()>0)
					getList.add(tempList);
			}
		}else
		{
			tempList = tranforList(dto,vDto);
			if(tempList!=null&&tempList.size()>0)
				getList.add(tempList);
		}
		return getList;
	}
	private List tranforList(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List<TrIncomedayrptDto> list=null;
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor sqlExe = null;
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
			String sql = "SELECT max(s_rptdate)  FROM Tr_Incomedayrpt WHERE S_FINORGCODE='"+cDto.getSfinorgcode()+"' AND s_trecode='"+dto.getStrecode()+"' and S_RPTDATE like '"+dto.getSrptdate().substring(0,6)+"%'";
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			SQLResults res = sqlExe.runQueryCloseCon(sql);
			if(res!=null)
				dto.setSrptdate(res.getString(0, 0));
			//Ϊ���������ĳ�SQL��䣬����Dto
			StringBuffer where = new StringBuffer(" and S_FINORGCODE='"+cDto.getSfinorgcode()+"' and (S_TRECODE = '"+dto.getStrecode()+"') AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ");
			//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444),3-��˰����(111111111111),4-��˰����(222222222222),5-���ش���(333333333333),6-��������(555555555555),
			if(vDto.getSpaybankcode()!=null&&"0".equals(vDto.getSpaybankcode()))//���ջ��ش���
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"'");
			}else if(vDto.getSpaybankcode()!=null&&"1".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"2".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"3".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"4".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"5".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"6".equals(vDto.getSpaybankcode()))
			{
				where.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}
			where.append(" and ((N_MONEYDAY<>0 or N_MONEYMONTH<>0 or N_MONEYYEAR<>0)");
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")<0)
				where.append(" or (N_MONEYDAY<>0)");
			where.append(") order by S_BUDGETSUBCODE");
			dto.setStrecode(null);
			dto.setSrptdate(null);
			dto.setStaxorgcode(null);
			list = CommonFacade.getODB().findRsByDtoForWhere(dto, where.toString());
			if(list!=null&&list.size()>0)
			{
				ReportLKXMUtil reportUtil = new ReportLKXMUtil();
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")<0&&ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=null,")<0)
					list = reportUtil.computeLKM(vDto.getSorgcode(), list, "1");
				else if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")>=0)
					list = reportUtil.computeLKM(vDto.getSorgcode(), list, "0");
			}
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}catch(Exception e2 ){
			logger.error(e2);
			throw new ITFEBizException(e2.getMessage(),e2);
		}finally
		{
			if(sqlExe!=null)
				sqlExe.closeConnection();
		}
		if(list==null||list.size()==0){
			return null;
		}
		List sendList = getSubLists(list,500);
		List listss=new ArrayList();
		if(sendList!=null&&sendList.size()>0)
		{
			for(int i=0;i<sendList.size();i++)
			{
				list = (List)sendList.get(i);
				String dirsep = File.separator;
				String mainvou=VoucherUtil.getGrantSequence();
				String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
		        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
				TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
				voucherdto.setSdealno(mainvou);
				voucherdto.setSorgcode(vDto.getSorgcode());
				voucherdto.setSadmdivcode(vDto.getSadmdivcode());
				voucherdto.setSvtcode(vDto.getSvtcode());
				voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//ƾ֤����
				voucherdto.setScheckdate(vDto.getScheckdate().substring(0,6));//��������
				voucherdto.setStrecode(vDto.getStrecode());
				voucherdto.setSadmdivcode(cDto.getSadmdivcode());
				voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
//				voucherdto.setSattach(vDto.getSattach());//���������ձ��±��걨
				voucherdto.setSattach("2");//���������ձ��±��걨
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype());//��������
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//���ջ��ش���
				voucherdto.setShold1(vDto.getShold1());//Ԥ������
				
				if (null!=vDto.getShold2()&&vDto.getShold2().trim().length()>1) {
					voucherdto.setShold2(vDto.getShold2());//Ԥ�㼶��
				} else {
					voucherdto.setShold2(list.get(0).getSbudgetlevelcode());//Ԥ�㼶��
				}
				
				voucherdto.setShold3(vDto.getShold3());//Ͻ����־
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
				Map map= tranfor(lists,sendList.size(),(i+1),vDto);				
//				List voucherList=new ArrayList();
				listss.add(map);
				listss.add(voucherdto);
//				listss.add(voucherList);
			}
		}
		
		return listss;
	}
	private List tranfordf(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List<TrIncomedayrptDto> list=null;
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		StringBuffer sql = new StringBuffer(" and ");
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
			TsTreasuryDto treDto = new TsTreasuryDto();
			treDto.setStrecode(vDto.getStrecode());
			treDto.setSorgcode(vDto.getSorgcode());
			List<TsTreasuryDto> trel = CommonFacade.getODB().findRsByDto(treDto);
			if(trel!=null&&trel.size()>0)
				treDto = trel.get(0);
			List<TsTreasuryDto> treCodeList = PublicSearchFacade.getSubTreCode(treDto);
			if(treCodeList!=null&&treCodeList.size()>0)
			{
				for(int i=0;i<treCodeList.size();i++)
				{
					treDto = treCodeList.get(i);
					if(i==0&&treDto.getStrelevel()!=null)
						sql.append(" (((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					else if(i==(treCodeList.size()-1)&&treDto.getStrelevel()!=null)
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"')))");
					else if(treDto.getStrelevel()!=null)
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					else
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='error'))");
				}
			}
			sql.append(" AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ");
			if(dto.getStaxorgcode()!=null)
				sql.append("AND (S_TAXORGCODE = '"+dto.getStaxorgcode()+"' ) ");
			dto.setStrecode(null);
			dto.setSrptdate(null);
			dto.setStaxorgcode(null);
			dto.setSbudgetlevelcode(null);
			list = CommonFacade.getODB().findRsByDtoForWhere(dto, sql.toString());
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("��ѯ��Ϣ�쳣��",e2);
		}catch(Exception e2 ){
			logger.error(e2);
			throw new ITFEBizException(e2.getMessage(),e2);
		}
		if(list==null||list.size()==0){
			return null;
		}
		List sendList = getSubLists(list,500);
		List listss=new ArrayList();
		if(sendList!=null&&sendList.size()>0)
		{
			for(int i=0;i<sendList.size();i++)
			{
				list = (List)sendList.get(i);
				String dirsep = File.separator;
				String mainvou=VoucherUtil.getGrantSequence();
				String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
		        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
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
				voucherdto.setSattach(vDto.getSattach());//���������ձ��±��걨
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype());//��������
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//���ջ��ش���
				voucherdto.setShold1(vDto.getShold1());//Ԥ������
				voucherdto.setShold2(vDto.getShold2());//Ԥ�㼶��
				voucherdto.setShold3(vDto.getShold3());//Ͻ����־
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
				Map map= tranfor(lists,sendList.size(),(i+1),vDto);				
				List voucherList=new ArrayList();
				voucherList.add(map);
				voucherList.add(voucherdto);
				listss.add(voucherList);
			}
		}
		
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
	public Map tranfor(List lists,int packnum,int packno,TvVoucherinfoDto infodto) throws ITFEBizException{
		try{	
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
//			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//������������
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));	//ҵ�����	
			vouchermap.put("VtCode", vDto.getSvtcode());//ƾ֤���ͱ��		
			vouchermap.put("VouDate", vDto.getScreatdate());//ƾ֤����		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//ƾ֤��
			vouchermap.put("VoucherCheckNo", infodto.getSdealno());//���˵���
			vouchermap.put("ChildPackNum", String.valueOf(packnum));//�Ӱ�����
			vouchermap.put("CurPackNo", String.valueOf(packno));//�������
			vouchermap.put("BudgetType", vDto.getShold1());//Ԥ������
			vouchermap.put("BelongFlag", vDto.getShold3());//Ͻ����־
			vouchermap.put("BudgetLevelCode", vDto.getShold2());//Ԥ�㼶��
			if("6".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "5");
			else if("7".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "6");
			else if("8".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "7");
			else
				vouchermap.put("BillKind", vDto.getScheckvouchertype());//��������
			vouchermap.put("ChkMonth", vDto.getScheckdate()!=null&&vDto.getScheckdate().length()>6?vDto.getScheckdate().substring(0,6):"");		
			vouchermap.put("FinOrgCode", cDto.getSfinorgcode());//�������ش���
			vouchermap.put("TreCode", vDto.getStrecode());//�������
			vouchermap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//��������
			vouchermap.put("XCheckResult", "");//���˽��
			vouchermap.put("XCheckReason", "");//����ԭ��
			Map<String,TdTaxorgParamDto> taxorgcodeMap = getTdTaxorgParam(vDto.getSorgcode());
			if(list.get(0).getStaxorgcode()==null){
				vouchermap.put("Hold1", "");//Ԥ���ֶ�1
			}else{
				if(taxorgcodeMap.get(list.get(0).getStaxorgcode())!=null){
					vouchermap.put("Hold1", taxorgcodeMap.get(list.get(0).getStaxorgcode()).getStaxorgname());	
				}else{
					vouchermap.put("Hold1", VoucherUtil.getStaxorgname(list.get(0).getStaxorgcode()));	
				}
			}
			vouchermap.put("Hold2", "");//Ԥ���ֶ�2	
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			for (TrIncomedayrptDto dto:list) {

				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", "3567"+StampFacade.getStampSendSeq("3567")); // ��ˮ��
				Detailmap.put("AdmDivCode", vDto.getSadmdivcode()); // ��������
				Detailmap.put("StYear", vDto.getScreatdate().substring(0, 4)); 
				Detailmap.put("TaxOrgCode",dto.getStaxorgcode()==null?"":dto.getStaxorgcode());			
				if(dto.getStaxorgcode()==null)
					Detailmap.put("TaxOrgName","");
				else if(taxorgcodeMap.get(dto.getStaxorgcode())==null){
					Detailmap.put("TaxOrgName", VoucherUtil.getStaxorgname(dto.getStaxorgcode()));
				}else{
					Detailmap.put("TaxOrgName", taxorgcodeMap.get(dto.getStaxorgcode()).getStaxorgname());
				}	 
				Detailmap.put("BudgetType", dto.getSbudgettype()==null?"":dto.getSbudgettype()); 
				Detailmap.put("BudgetLevelCode",dto.getSbudgetlevelcode()==null?"":dto.getSbudgetlevelcode()); 
				Detailmap.put("BudgetSubjectCode", dto.getSbudgetsubcode()==null?"":dto.getSbudgetsubcode()); 
				Detailmap.put("BudgetSubjectName", dto.getSbudgetsubname()==null?"":dto.getSbudgetsubname()); 
				
//				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
//				Detailmap.put("TenDayAmt", MtoCodeTrans.transformString(dto.getNmoneytenday())); 
//				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("CurIncomeAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("SumIncomeAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("XCheckResult", "");//���˽��
				Detailmap.put("XCheckReason", "");//����ԭ��
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
				allamt=allamt.add(dto.getNmoneymonth());										
				Detail.add(Detailmap);
			}		
			Total=Total.add(allamt);			
			vDto.setNmoney(allamt);
			vDto.setNmoney(Total);
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
	private Map<String,TdTaxorgParamDto> getTdTaxorgParam(String sorgcode) throws ITFEBizException 
	{
		Map<String,TdTaxorgParamDto> getmap = new HashMap<String,TdTaxorgParamDto>();
		TdTaxorgParamDto tDto=new TdTaxorgParamDto();			
		tDto.setSbookorgcode(sorgcode);
		try {
			List<TdTaxorgParamDto> tdTaxorgParamDtoList=CommonFacade.getODB().findRsByDto(tDto);
			if(tdTaxorgParamDtoList!=null&&tdTaxorgParamDtoList.size()>0)
			{
				for(TdTaxorgParamDto dto:tdTaxorgParamDtoList)
					getmap.put(dto.getStaxorgcode(), dto);
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			throw new ITFEBizException(e);
		}
		return getmap;
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}

}
