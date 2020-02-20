package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeSubDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.Report3511LKXMUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * ���뱨����ˣ�3511��
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3511 implements IVoucherDto2Map{
	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3507.class);
	private Map<String,TdTaxorgParamDto> taxorgcodeMap = null;
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
//		dto.sattach();//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
//		dto.shold1();//Ԥ������1-Ԥ����2-Ԥ����
//		dto.shold3();//Ͻ����־0-������1-ȫϽ
//		dto.setShold2();//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��
//		dto.scheckvouchertype();//��������1-������Ԥ�����뱨��2-�������˿ⱨ����3-�����ڵ������뱨��4-�ܶ�ֳɱ�����5-������Ԥ�����뱨����6-�������˿ⱨ����7-�����ڵ������뱨����
		//S_TRIMFLAG IS '�����ڱ�־0 �����(������)1 �����(������)';S_BUDGETTYPE IS 'Ԥ������1-Ԥ����2-Ԥ����';S_BUDGETLEVELCODE IS 'Ԥ�㼶�δ���';.S_BELONGFLAG IS 'Ͻ����־0-������1-ȫϽ';
		//S_FINORGCODE IS '�������ش���';S_TAXORGCODE IS '���ջ��ش���';S_BILLKIND IS '��������1-������Ԥ�����뱨���壬2-�������˿ⱨ���壬3-�����ڵ������뱨���壬4-�ܶ�ֳɱ����壬5-�����ڹ���ֳɱ����壬6-������Ԥ�����뱨���壬7-�������˿ⱨ���壬8-�����ڵ������뱨���壬9-�����ڹ���ֳɱ�����';
//		TrIncomedayrptDto dto=new TrIncomedayrptDto();
//		dto.setStrecode(vDto.getStrecode());//�������
//		dto.setStaxorgcode(vDto.getSattach());//���ջ��ش���
//		dto.setSbelongflag(vDto.getShold3());//Ͻ����־
//		dto.setSbudgetlevelcode(vDto.getShold2());//Ԥ�㼶��
//		dto.setSbudgettype(vDto.getShold1());//Ԥ������
//		dto.setSbillkind(vDto.getScheckvouchertype());//��������
		if(taxorgcodeMap==null)
			taxorgcodeMap = getTdTaxorgParam(vDto.getSorgcode());
		if(vDto.getShold2()!=null&&"6".equals(vDto.getShold2()))
			return tranfordf(vDto);
		else
			return tranforList(vDto);
	}
	private List tranforList(TvVoucherinfoDto vDto) throws ITFEBizException
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
			}else
				vDto.setSadmdivcode(cDto.getSadmdivcode());
//			dto.sattach();//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
//			dto.shold1();//Ԥ������1-Ԥ����2-Ԥ����//			dto.shold3();//Ͻ����־0-������1-ȫϽ
//			dto.setShold2();//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��//			dto.scheckvouchertype();//��������
			StringBuffer sql = new StringBuffer("SELECT *  FROM hTr_Incomedayrpt WHERE "+" S_FINORGCODE=? and S_TRECODE=? AND S_RPTDATE>=? and S_RPTDATE<=?");//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444),3-��˰����(111111111111),4-��˰����(222222222222),5-���ش���(333333333333),6-��������(555555555555),
			
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//���ջ��ش���
				sql.append(" and S_TAXORGCODE=?");
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? ");
			else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else
				sql.append(" and S_TAXORGCODE=? ");
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//Ͻ����־����
				sql.append(" and S_BELONGFLAG=?");
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//ȫϽ
				sql.append(" and S_BELONGFLAG=?");
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//Ԥ������
				sql.append(" and S_BUDGETTYPE=?");
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2())&&!"A".equals(vDto.getShold2().trim()))//Ԥ�㼶��
				sql.append(" and S_BUDGETLEVELCODE=?");
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//��������
				sql.append(" and S_BILLKIND=?");
			sql.append(" and ((N_MONEYDAY<>? or N_MONEYMONTH<>? or N_MONEYYEAR<>?)");
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sql.append(" and (N_MONEYDAY>?)");
			sql.append(") order by S_RPTDATE,S_BUDGETSUBCODE asc");
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe.addParam(cDto.getSfinorgcode());
			sqlExe.addParam(vDto.getStrecode());
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//���ջ��ش���
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//Ͻ����־����
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//ȫϽ
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//Ԥ������
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2())&&!"A".equals(vDto.getShold2().trim()))//Ԥ�㼶��
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//��������
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list = (List<TrIncomedayrptDto>) sqlExe.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();
			sqlExe.addParam(cDto.getSfinorgcode());
			sqlExe.addParam(vDto.getStrecode());
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//���ջ��ش���
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//Ͻ����־����
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//ȫϽ
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//Ԥ������
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2())&&!"A".equals(vDto.getShold2().trim()))//Ԥ�㼶��
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//��������
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list.addAll((List<TrIncomedayrptDto>) sqlExe.runQueryCloseCon(StringUtil.replace(sql.toString(), "hTr_Incomedayrpt", "Tr_Incomedayrpt"),TrIncomedayrptDto.class).getDtoCollection());
			Report3511LKXMUtil reportUtil = new Report3511LKXMUtil();
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
				if(vDto.getSdealno()==null||"".equals(vDto.getSdealno()))
					vDto.setSdealno(mainvou);
				String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
		        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
				TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
				voucherdto.setSdealno(mainvou);
				voucherdto.setSorgcode(vDto.getSorgcode());
				voucherdto.setSadmdivcode(vDto.getSadmdivcode());
				voucherdto.setSvtcode(vDto.getSvtcode());
				voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//ƾ֤����
				voucherdto.setScheckdate(vDto.getScheckdate());//������ʼ����
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//���ʽ�������
				voucherdto.setStrecode(vDto.getStrecode());
				voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
				voucherdto.setSattach(vDto.getSattach()==null?"":vDto.getSattach());//���ջ��ش���
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//��������
				voucherdto.setShold1(vDto.getShold1()==null?"":vDto.getShold1());//Ԥ������
				voucherdto.setShold2(vDto.getShold2()==null?"":vDto.getShold2());//Ԥ�㼶��
				voucherdto.setShold3(vDto.getShold3()==null?"":vDto.getShold3());//Ͻ����־
				voucherdto.setSvoucherflag("1");
				voucherdto.setSfilename(FileName);
//				if(ITFECommonConstant.PUBLICPARAM.indexOf(",stampmode=sign,")<0)//�Ƿ����ǩ��
//				{
				voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				voucherdto.setSdemo("����ɹ�");
				voucherdto.setSvoucherno(mainvou);
				voucherdto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���
				voucherdto.setSext4(vDto.getSdealno());//���ʵ���
				voucherdto.setSext2(String.valueOf(sendList.size()));//������
				voucherdto.setSext3(String.valueOf(i+1));//�����
				voucherdto.setIcount(list.size());
				List lists=new ArrayList();
				lists.add(list);
				lists.add(voucherdto);
				lists.add(cDto);
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map= tranfor(lists,sendList.size(),(i+1),vDto,idtoList);				
				List voucherList=new ArrayList();
				voucherList.add(map);
				voucherList.add(voucherdto);
				voucherList.add(idtoList);
				listss.add(voucherList);
			}
		}
		
		return listss;
	}
	private List tranfordf(TvVoucherinfoDto vDto) throws ITFEBizException
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
			}else
				vDto.setSadmdivcode(cDto.getSadmdivcode());
			TsTreasuryDto treDto = new TsTreasuryDto();
			treDto.setStrecode(vDto.getStrecode());
//			List<TsTreasuryDto> treCodeList = PublicSearchFacade.getSubTreCode(treDto);
//			if(treCodeList!=null&&treCodeList.size()>0)
//			{
//				for(int i=0;i<treCodeList.size();i++)
//				{
//					treDto = treCodeList.get(i);
//					if(i==0&&treDto.getStrelevel()!=null)
//						tresql.append(" (((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
//					else if(i==(treCodeList.size()-1)&&treDto.getStrelevel()!=null)
//						tresql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"')))");
//					else if(treDto.getStrelevel()!=null)
//						tresql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='"+treDto.getStrelevel()+"'))");
//					else
//						tresql.append(" or ((S_TRECODE = '"+treDto.getStrecode()+"') and (S_BUDGETLEVELCODE='error'))");
//				}
//			}
			StringBuffer sql = new StringBuffer("SELECT *  FROM hTr_Incomedayrpt WHERE S_RPTDATE>=? and S_RPTDATE<=? ");//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444),3-��˰����(111111111111),4-��˰����(222222222222),5-���ش���(333333333333),6-��������(555555555555),
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//���ջ��ش���
				sql.append(" and S_TAXORGCODE=?");
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? and S_TAXORGCODE <>? ");
			else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sql.append(" and S_TAXORGCODE=? ");
			else
				sql.append(" and S_TAXORGCODE=? ");
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//Ͻ����־����
				sql.append(" and S_BELONGFLAG=?");
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//ȫϽ
				sql.append(" and S_BELONGFLAG=?");
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//Ԥ������
				sql.append(" and S_BUDGETTYPE=?");
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2()))//Ԥ�㼶��
				sql.append(" and S_BUDGETLEVELCODE=?");
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//��������
				sql.append(" and S_BILLKIND=?");
			sql.append(" and ((N_MONEYDAY<>? or N_MONEYMONTH<>? or N_MONEYYEAR<>?)");
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sql.append(" and (N_MONEYDAY>?)");
			sql.append(") order by S_RPTDATE,S_BUDGETSUBCODE asc");
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//���ջ��ش���
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//Ͻ����־����
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//ȫϽ
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//Ԥ������
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2()))//Ԥ�㼶��
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//��������
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list = (List<TrIncomedayrptDto>) sqlExe.runQuery(sql.toString(),TrIncomedayrptDto.class).getDtoCollection();
			sqlExe.addParam(vDto.getScheckdate());
			sqlExe.addParam(vDto.getSpaybankcode());
			if(vDto.getSattach()!=null&&"0".equals(vDto.getSattach()))//���ջ��ش���
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			else if(vDto.getSattach()!=null&&"1".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			else if(vDto.getSattach()!=null&&"2".equals(vDto.getSattach()))
			{
				sqlExe.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			}else if(vDto.getSattach()!=null&&"3".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_NATION_CLASS);
			else if(vDto.getSattach()!=null&&"4".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			else if(vDto.getSattach()!=null&&"5".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			else if(vDto.getSattach()!=null&&"6".equals(vDto.getSattach()))
				sqlExe.addParam(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			else
				sqlExe.addParam(vDto.getSattach());
			if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_SELF.equals(vDto.getShold3()))//Ͻ����־����
				sqlExe.addParam(MsgConstant.RULE_SIGN_SELF);
			else if(vDto.getShold3()!=null&&MsgConstant.RULE_SIGN_ALL.equals(vDto.getShold3()))//ȫϽ
				sqlExe.addParam(MsgConstant.RULE_SIGN_ALL);
			if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))//Ԥ������
				sqlExe.addParam(vDto.getShold1());
			if(vDto.getShold2()!=null&&!"".equals(vDto.getShold2()))//Ԥ�㼶��
				sqlExe.addParam(vDto.getShold2());
			if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))//��������
				sqlExe.addParam(vDto.getScheckvouchertype());
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			sqlExe.addParam(0);
			if(ITFECommonConstant.PUBLICPARAM.contains(",ribao=0,"))
				sqlExe.addParam(0);
			list.addAll((List<TrIncomedayrptDto>) sqlExe.runQueryCloseCon(StringUtil.replace(sql.toString(), "hTr_Incomedayrpt", "Tr_Incomedayrpt"),TrIncomedayrptDto.class).getDtoCollection());
			if(list!=null&&list.size()>0)
			{
				Report3511LKXMUtil reportUtil = new Report3511LKXMUtil();
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
				voucherdto.setScheckdate(vDto.getScheckdate());//������ʼ����
				voucherdto.setSpaybankcode(vDto.getSpaybankcode());//���ʽ�ֹ����
				voucherdto.setStrecode(vDto.getStrecode());
				voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
				voucherdto.setScheckvouchertype(vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//��������
				voucherdto.setSattach(vDto.getSattach()==null?"":vDto.getSattach());//���ջ��ش���
				voucherdto.setShold1(vDto.getShold1()==null?"":vDto.getShold1());//Ԥ������
				voucherdto.setShold2(vDto.getShold2()==null?"":vDto.getShold2());//Ԥ�㼶��
				voucherdto.setShold3(vDto.getShold3()==null?"":vDto.getShold3());//Ͻ����־
				voucherdto.setSfilename(FileName);
				voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				voucherdto.setSdemo("����ɹ�");				
				voucherdto.setSvoucherno(mainvou);
				voucherdto.setIcount(list.size());
				List lists=new ArrayList();
				lists.add(list);
				lists.add(voucherdto);
				lists.add(cDto);
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map= tranfor(lists,sendList.size(),(i+1),vDto,idtoList);				
				List voucherList=new ArrayList();
				voucherList.add(map);
				voucherList.add(voucherdto);
				voucherList.add(idtoList);
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
	public Map tranfor(List lists,int packnum,int packno,TvVoucherinfoDto infodto,List idtoList) throws ITFEBizException{
		try{	
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ�� 
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//������������
			vouchermap.put("StYear",vDto.getScheckdate().substring(0,4));//ҵ�����
			vouchermap.put("VtCode",vDto.getSvtcode());//ƾ֤���ͱ��
			vouchermap.put("VouDate",vDto.getScreatdate());//ƾ֤����
			vouchermap.put("VoucherNo",vDto.getSdealno());//ƾ֤��
			vouchermap.put("VoucherCheckNo",infodto.getSdealno());//���˵���
			vouchermap.put("ChildPackNum",packnum);//�Ӱ�����
			vouchermap.put("CurPackNo",packno);//�������
			vouchermap.put("FundTypeCode","1");//�ʽ�����-tips�·������޴����ݣ�ƾ֤����Ҫ��д��1-Ԥ������ʽ�
			vouchermap.put("BelongFlag",vDto.getShold3()==null?"":vDto.getShold3());//Ͻ����־//dto.sattach();//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)dto.shold1();//Ԥ������1-Ԥ����2-Ԥ����dto.shold3();//Ͻ����־0-������1-ȫϽdto.setShold2();//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��dto.scheckvouchertype();//��������
			vouchermap.put("BudgetLevelCode",vDto.getShold2()==null?"":vDto.getShold2());//Ԥ�㼶��
			vouchermap.put("BillKind",vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//��������
			vouchermap.put("FinOrgCode",cDto.getSfinorgcode()==null?"":cDto.getSfinorgcode());//�������ش���
			vouchermap.put("TreCode",vDto.getStrecode());//�����������
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//������������
			vouchermap.put("BeginDate",vDto.getScheckdate());//������ʼ����
			vouchermap.put("EndDate",vDto.getSpaybankcode());//������ֹ����
			vouchermap.put("AllNum",list==null?0:list.size());//�ܱ���
			vouchermap.put("Hold1","");//Ԥ���ֶ�1
			vouchermap.put("Hold2",vDto.getSverifyusercode()==null?"":vDto.getSverifyusercode());//Ԥ���ֶ�2
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for (TrIncomedayrptDto dto:list) {

				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",vDto.getSdealno()+(++id));//���
				Detailmap.put("AdmDivCode",vDto.getSadmdivcode());//������������
				Detailmap.put("StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
				Detailmap.put("TaxOrgCode",dto.getStaxorgcode()==null?"":dto.getStaxorgcode());
				if(dto.getStaxorgcode()==null)
					Detailmap.put("TaxOrgName","");
				else if(taxorgcodeMap.get(dto.getStaxorgcode())==null){//���ջ�������
					Detailmap.put("TaxOrgName", VoucherUtil.getStaxorgname(dto.getStaxorgcode()));
				}else{
					Detailmap.put("TaxOrgName", taxorgcodeMap.get(dto.getStaxorgcode()).getStaxorgname());
				}
				Detailmap.put("BudgetType","1");//�ʽ�����-tips�·������޴����ݣ�ƾ֤����Ҫ��д��1-Ԥ������ʽ�
				Detailmap.put("BudgetLevelCode",dto.getSbudgetlevelcode()==null?"":dto.getSbudgetlevelcode());//Ԥ�㼶��
				Detailmap.put("BudgetSubjectCode",dto.getSbudgetsubcode()==null?"":dto.getSbudgetsubcode());//��������Ŀ����
				Detailmap.put("BudgetSubjectName",dto.getSbudgetsubname()==null?"":dto.getSbudgetsubname());//��������Ŀ����
				Detailmap.put("CurIncomeAmt",String.valueOf(dto.getNmoneymonth()));//���ڽ��
				Detailmap.put("SumIncomeAmt",String.valueOf(dto.getNmoneyyear()));//�ۼƽ��
				Detailmap.put("Hold1",String.valueOf(dto.getNmoneyday()));//Ԥ���ֶ�1
				Detailmap.put("Hold2",String.valueOf(dto.getNmoneyquarter()));//Ԥ���ֶ�2
				Detailmap.put("Hold3",String.valueOf(dto.getNmoneytenday()));//Ԥ���ֶ�3
				Detailmap.put("Hold4","");//Ԥ���ֶ�4
				if(dto.getSbudgetsubcode()!=null&&dto.getSbudgetsubcode().length()==3)
					allamt=allamt.add(dto.getNmoneyday());										
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}					
			vDto.setNmoney(allamt);
			vouchermap.put("AllAmt",String.valueOf(allamt));//�ܽ��
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
	private TfReportIncomeMainDto getMainDto(HashMap<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReportIncomeMainDto mainDto = new TfReportIncomeMainDto();
		mainDto.setSorgcode(vDto.getSorgcode());//S_ORGCODE IS '��������'; 
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		mainDto.setIvousrlno(Long.valueOf(voucherno));   
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		mainDto.setSdemo("����ɹ�");
		mainDto.setStrecode(getString(mainMap,"TreCode"));//S_TRECODE IS '�������'; 
		mainDto.setStrename(getString(mainMap,"TreName"));//S_TRENAME IS '������������';
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);//S_STATUS IS '״̬';      
		mainDto.setSdemo("����ɹ�");//S_DEMO IS '����';        
		mainDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//TS_SYSUPDATE IS 'ϵͳʱ��';
		;//S_PACKAGENO IS '����ˮ��';
		mainDto.setSadmdivcode(getString(mainMap,"AdmDivCode"));//S_ADMDIVCODE IS '������������';
		mainDto.setSstyear(getString(mainMap,"StYear"));//S_STYEAR IS 'ҵ�����';
		mainDto.setSvtcode(getString(mainMap,"VtCode"));//S_VTCODE IS 'ƾ֤���ͱ��';
		mainDto.setSvoudate(getString(mainMap,"VouDate"));//S_VOUDATE IS 'ƾ֤����';
		mainDto.setSvoucherno(getString(mainMap,"VoucherNo"));//S_VOUCHERNO IS 'ƾ֤��';
		mainDto.setSvouchercheckno(getString(mainMap,"VoucherCheckNo"));//S_VOUCHERCHECKNO IS '���˵���';
		mainDto.setSchildpacknum(getString(mainMap,"ChildPackNum"));//S_CHILDPACKNUM IS '�Ӱ�����';
		mainDto.setScurpackno(getString(mainMap,"CurPackNo"));//S_CURPACKNO IS '�������';
		mainDto.setSfundtypecode(getString(mainMap,"FundTypeCode"));//S_FUNDTYPECODE IS '�ʽ�����';
		mainDto.setSbelongflag(getString(mainMap,"BelongFlag"));//S_BELONGFLAG IS 'Ͻ����־';
		mainDto.setSbudgetlevelcode(getString(mainMap,"BudgetLevelCode").length()>1?"1":getString(mainMap,"BudgetLevelCode"));//S_BUDGETLEVELCODE IS 'Ԥ�㼶��';
		mainDto.setSbillkind(getString(mainMap,"BillKind"));//S_BILLKIND IS '��������';
		mainDto.setSfinorgcode(getString(mainMap,"FinOrgCode"));//S_FINORGCODE IS '�������ش���';
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//S_BEGINDATE IS '������ʼ����';
		mainDto.setSenddate(getString(mainMap,"EndDate"));//S_ENDDATE IS '������ֹ����';
		mainDto.setSallnum(getString(mainMap,"AllNum"));//S_ALLNUM IS '�ܱ���';
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//N_ALLAMT IS '�ܽ��';
		;//S_XCHECKRESULT IS '���ʽ��';
		;//S_XDIFFNUM IS '��������';
		mainDto.setShold1(getString(mainMap,"Hold1"));//S_HOLD1 IS 'Ԥ���ֶ�1';
		mainDto.setShold2(getString(mainMap,"Hold2"));//S_HOLD2 IS 'Ԥ���ֶ�2';
		mainDto.setSext1("1");//����ʽ1:���з���,2:��������,3:���з���        
		mainDto.setSext2(getString(mainMap,"ext2"));//S_EXT2 IS '��չ';        
		mainDto.setSext3(getString(mainMap,"ext3"));//S_EXT3 IS '��չ';        
		mainDto.setSext4(getString(mainMap,"ext4"));//S_EXT4 IS '��չ';        
		mainDto.setSext5(getString(mainMap,"ext5"));//S_EXT5 IS '��չ'; 
		return mainDto;
	}
	private TfReportIncomeSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReportIncomeSubDto subDto = new TfReportIncomeSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//���
		subDto.setSid(String.valueOf(subDto.getIseqno()));
		subDto.setSadmdivcode(getString(subMap,"AdmDivCode"));//AdmDivCode",vDto.getSadmdivcode());//������������
		subDto.setSstyear(getString(subMap,"StYear"));//StYear",vDto.getScreatdate().substring(0,4));//ҵ�����
		subDto.setStaxorgcode(getString(subMap,"TaxOrgCode"));//TaxOrgCode",cDto.getSfinorgcode());//���ջ��ش���
		subDto.setStaxorgname(getString(subMap,"TaxOrgName"));//TaxOrgName",cDto.getSfinorgname());//���ջ�������
		subDto.setSbudgettype(getString(subMap,"BudgetType"));//BudgetType","1");//�ʽ�����-tips�·������޴����ݣ�ƾ֤����Ҫ��д��1-Ԥ������ʽ�
		subDto.setSbudgetlevelcode(getString(mainMap,"BudgetLevelCode").length()>1?"1":getString(mainMap,"BudgetLevelCode"));//BudgetLevelCode",dto.getSbudgetlevelcode());//Ԥ�㼶��
		subDto.setSbudgetsubjectcode(getString(subMap,"BudgetSubjectCode"));//BudgetSubjectCode",dto.getSbudgetsubcode());//��������Ŀ����
		subDto.setSbudgetsubjectname(getString(subMap,"BudgetSubjectName"));//BudgetSubjectName",dto.getSbudgetsubname());//��������Ŀ����
		subDto.setNcurincomeamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"CurIncomeAmt")));//CurIncomeAmt",dto.getNmoneyday());//���ڽ��
		subDto.setNsumincomeamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"SumIncomeAmt")));//SumIncomeAmt",dto.getNmoneyyear());//�ۼƽ��
		subDto.setShold1(getString(subMap,"Hold1"));//S_HOLD1 IS 'Ԥ���ֶ�1';
		subDto.setShold2(getString(subMap,"Hold2"));//S_HOLD2 IS 'Ԥ���ֶ�2';
		subDto.setShold3(getString(subMap,"Hold3"));//S_HOLD3 IS 'Ԥ���ֶ�3';
		subDto.setShold4(getString(subMap,"Hold4"));//S_HOLD4 IS 'Ԥ���ֶ�4';
		subDto.setSext1(getString(subMap,"ext1"));//S_EXT1 IS '��չ';        
		subDto.setSext2(getString(subMap,"ext2"));//S_EXT2 IS '��չ';        
		subDto.setSext3(getString(subMap,"ext3"));//S_EXT3 IS '��չ';        
		subDto.setSext4(getString(subMap,"ext4"));//S_EXT4 IS '��չ';        
		subDto.setSext5(getString(subMap,"ext5"));//S_EXT5 IS '��չ';
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
}
