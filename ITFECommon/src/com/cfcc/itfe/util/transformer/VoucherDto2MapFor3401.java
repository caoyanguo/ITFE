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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
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
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ƾ֤�����ձ���(3401)ת��
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3401 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3401.class);
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
//		vDto.getSorgcode();//��������
//		vDto.getScreatdate();//ƾ֤����
//		vDto.getSvtcode();//ƾ֤����3208-ʵ���ʽ��˻أ�3401-�����ձ���3402-����ձ���
//		vDto.getStrecode();//�������
//		vDto.getSstyear();//��������1-�ձ� 2-�±� 3-�걨
//		vDto.getSpaybankcode();//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
//		vDto.getShold1();//Ԥ������1-Ԥ����2-Ԥ����
//		vDto.getShold3();//Ͻ����־0-������1-ȫϽ
//		vDto.getShold2();//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��
//		vDto.getShold4();//��������1-������Ԥ�����뱨��2-�������˿ⱨ����3-�����ڵ������뱨��4-�ܶ�ֳɱ�����5-������Ԥ�����뱨����6-�������˿ⱨ����7-�����ڵ������뱨����
		//S_TRIMFLAG IS '�����ڱ�־0 �����(������)1 �����(������)';S_BUDGETTYPE IS 'Ԥ������1-Ԥ����2-Ԥ����';S_BUDGETLEVELCODE IS 'Ԥ�㼶�δ���';.S_BELONGFLAG IS 'Ͻ����־0-������1-ȫϽ';
		//S_FINORGCODE IS '�������ش���';S_TAXORGCODE IS '���ջ��ش���';S_BILLKIND IS '��������1-������Ԥ�����뱨���壬2-�������˿ⱨ���壬3-�����ڵ������뱨���壬4-�ܶ�ֳɱ����壬5-�����ڹ���ֳɱ����壬6-������Ԥ�����뱨���壬7-�������˿ⱨ���壬8-�����ڵ������뱨���壬9-�����ڹ���ֳɱ�����';
		List<String> ysjc = new ArrayList<String>();
		List<String> bbzl = new ArrayList<String>();
		ysjc.add("1");ysjc.add("2");ysjc.add("3");ysjc.add("4");ysjc.add("5");//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��
		bbzl.add("1");bbzl.add("2");bbzl.add("3");bbzl.add("4");bbzl.add("6");bbzl.add("7");bbzl.add("8");//1-������Ԥ�����뱨���壬2-�������˿ⱨ���壬3-�����ڵ������뱨���壬4-�ܶ�ֳɱ����壬6-������Ԥ�����뱨���壬7-�������˿ⱨ���壬8-�����ڵ������뱨����
		String shold2 = vDto.getShold2();
		String shold4 = vDto.getScheckvouchertype();
		List getList = new ArrayList();
		List tempList = null;
		TrIncomedayrptDto dto=new TrIncomedayrptDto();
		dto.setStrecode(vDto.getStrecode());//�������
		dto.setStaxorgcode(vDto.getSpaybankcode());//���ջ��ش���
		dto.setSrptdate(vDto.getScheckdate());//��������
		dto.setSbelongflag(vDto.getShold3());//Ͻ����־
		dto.setSbudgetlevelcode(vDto.getShold2());//Ԥ�㼶��
		dto.setSbudgettype(vDto.getShold1());//Ԥ������
		dto.setSbillkind(vDto.getScheckvouchertype());//��������
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
		return getList;//if(vDto.getShold2()!=null&&"6".equals(vDto.getShold2()))//return tranfordf(dto,vDto);//else//return tranforList(dto,vDto);
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
			StringBuffer sql = new StringBuffer("SELECT * FROM TR_INCOMEDAYRPT where S_FINORGCODE=? and S_TRECODE = ? and S_RPTDATE = ?");
			execDetail.addParam(cDto.getSfinorgcode());
			execDetail.addParam(dto.getStrecode());
			execDetail.addParam(dto.getSrptdate());
			//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444),3-��˰����(111111111111),4-��˰����(222222222222),5-���ش���(333333333333),6-��������(555555555555),
			if(vDto.getSpaybankcode()!=null&&"0".equals(vDto.getSpaybankcode()))//���ջ��ش���
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"1".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"2".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? ");
				execDetail.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				execDetail.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"3".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"4".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"5".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			}else if(vDto.getSpaybankcode()!=null&&"6".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else
			{
				sql.append(" and S_TAXORGCODE = ?");
				execDetail.addParam(vDto.getSpaybankcode());
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
			if(dto.getSbillkind()!=null)
			{
				sql.append(" and S_BILLKIND = ?");
				execDetail.addParam(dto.getSbillkind());
			}
			
			sql.append(" and ((N_MONEYDAY<>? or N_MONEYMONTH<>? or N_MONEYYEAR<>?)");
			execDetail.addParam(0);
			execDetail.addParam(0);
			execDetail.addParam(0);
			if(vDto.getSorgcode().startsWith("06")){
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")>=0)
				{
					sql.append(" or (N_MONEYDAY=?)");
					execDetail.addParam(0);
				}
			}else{
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")<0)
				{
					sql.append(" and (N_MONEYDAY<>?)");
					execDetail.addParam(0);
				}
			}
			sql.append(") order by S_BUDGETSUBCODE asc ");
			list = (List)execDetail.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();
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
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		if(list==null||list.size()==0){
			return null;
		}
		TvVoucherinfoDto voucherdto = makeVouInfoDto(list, vDto, cDto);
		List lists=new ArrayList();
		lists.add(list);
		lists.add(voucherdto);
		lists.add(cDto);
		Map map= tranfor(lists);				
		List voucherList=new ArrayList();
		voucherList.add(map);
		voucherList.add(voucherdto);
		return voucherList;
	}
	
	/**
	 * �������������Զ����ͱ�����һ������
	 * @param list
	 * @param vDto
	 * @param cDto
	 * @return
	 * @throws ITFEBizException 
	 */
	private  TvVoucherinfoDto  makeVouInfoDto(List list,TvVoucherinfoDto vDto,TsConvertfinorgDto cDto) throws ITFEBizException{
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
		return voucherdto;
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
			List<TsTreasuryDto> treCodeList = PublicSearchFacade.getSubTreCode(treDto);
			if(treCodeList!=null&&treCodeList.size()>0)
			{
				for(int i=0;i<treCodeList.size();i++)
				{
					treDto = treCodeList.get(i);
					if(i==0)
						sql.append(" (((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					else if(i==(treCodeList.size()-1))
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"')))");
					else
						sql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
					if(treCodeList.size()==1)
						sql.append(")");
				}
			}
			sql.append(" AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ");
			//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444),3-��˰����(111111111111),4-��˰����(222222222222),5-���ش���(333333333333),6-��������(555555555555),
			if(vDto.getSpaybankcode()!=null&&"0".equals(vDto.getSpaybankcode()))//���ջ��ش���
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"'");
			}else if(vDto.getSpaybankcode()!=null&&"1".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"2".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_SHARE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"' and S_TAXORGCODE <>'"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"3".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_NATION_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"4".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"5".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"' ");
			}else if(vDto.getSpaybankcode()!=null&&"6".equals(vDto.getSpaybankcode()))
			{
				sql.append(" and S_TAXORGCODE='"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"' ");
			}else
			{
				sql.append(" and S_TAXORGCODE='"+vDto.getSpaybankcode()+"' ");
			}
//			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//Ͻ����־����
//				sql.append(" and S_BELONGFLAG='"+MsgConstant.RULE_SIGN_SELF+"'");
//			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//ȫϽ
//				sql.append(" and S_BELONGFLAG='"+MsgConstant.RULE_SIGN_ALL+"'");
			sql.append(" and ((N_MONEYDAY<>0 or N_MONEYMONTH<>0 or N_MONEYYEAR<>0)");
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",ribao=0,")<0)
				sql.append(" or (N_MONEYDAY<>0)");
				sql.append(")  order by S_BUDGETSUBCODE");
			dto.setStrecode(null);
			dto.setSrptdate(null);
			dto.setStaxorgcode(null);
			dto.setSbudgetlevelcode(null);
			list = CommonFacade.getODB().findRsByDtoForWhere(dto, sql.toString());
			ReportLKXMUtil reportUtil = new ReportLKXMUtil();
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")<0&&ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=null,")<0)
				list = reportUtil.computeLKM(vDto.getSorgcode(), list, "1");
			else if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")>=0)
				list = reportUtil.computeLKM(vDto.getSorgcode(), list, "0");
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
		TvVoucherinfoDto voucherdto = makeVouInfoDto(list, vDto, cDto);
		List lists=new ArrayList();
		lists.add(list);
		lists.add(voucherdto);
		lists.add(cDto);
		Map map= tranfor(lists);				
		List voucherList=new ArrayList();
		voucherList.add(map);
		voucherList.add(voucherdto);
		return voucherList;
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
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));		
			vouchermap.put("VtCode", vDto.getSvtcode());		
			vouchermap.put("VouDate", vDto.getScreatdate());		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("FundTypeCode", "1");//�ʽ�����
			vouchermap.put("BudgetType", getString(vDto.getShold1()));//Ԥ������
			vouchermap.put("BelongFlag", getString(vDto.getShold3()));//Ͻ����־
			vouchermap.put("BudgetLevelCode", getString(vDto.getShold2()));//Ԥ�㼶��
			if("6".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "5");
			else if("7".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "6");
			else if("8".equals(vDto.getScheckvouchertype()))
				vouchermap.put("BillKind", "7");
			else
				vouchermap.put("BillKind", getString(vDto.getScheckvouchertype()));//��������
			vouchermap.put("ReportDate", vDto.getScheckdate());		
			vouchermap.put("FinOrgCode", cDto.getSfinorgcode());
			vouchermap.put("TreCode", vDto.getStrecode());//�������
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//��������
			vouchermap.put("SumMoney", null);//�����ձ��ۼƽ��
			vouchermap.put("Hold1", "");		
			vouchermap.put("Hold2", "");	
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			Map<String,TdTaxorgParamDto> taxorgcodeMap = getTdTaxorgParam(vDto.getSorgcode());
			for (TrIncomedayrptDto dto:list) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
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
				vouchermap.put("FundTypeCode", "1");//�ʽ�����
				Detailmap.put("BudgetType", dto.getSbudgettype()==null?"":dto.getSbudgettype()); 
				Detailmap.put("BudgetLevelCode",dto.getSbudgetlevelcode()==null?"":dto.getSbudgetlevelcode()); 
				Detailmap.put("BudgetSubjectCode", dto.getSbudgetsubcode()==null?"":dto.getSbudgetsubcode()); 
				Detailmap.put("BudgetSubjectName", dto.getSbudgetsubname()==null?"":dto.getSbudgetsubname()); 
				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
				Detailmap.put("TenDayAmt", MtoCodeTrans.transformString(dto.getNmoneytenday())); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("QuarterAmt", MtoCodeTrans.transformString(dto.getNmoneyquarter())); 
				Detailmap.put("YearAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", "");
				if(dto.getSbudgetsubcode()!=null&&dto.getSbudgetsubcode().equals(StateConstant.BUGGET_IN_FUND+StateConstant.MOVE_INCOME))
					allamt=dto.getNmoneyday();										
				Detail.add(Detailmap);
			}					
			vouchermap.put("SumMoney",MtoCodeTrans.transformString(allamt));	
			vDto.setNmoney(allamt);
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
	private String getString(String value)
	{
		if(value==null)
			value="";
		return value;
	}
}
