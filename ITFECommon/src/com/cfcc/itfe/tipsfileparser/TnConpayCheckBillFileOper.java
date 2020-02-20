package com.cfcc.itfe.tipsfileparser;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckpaybankDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * ������ȶ��˵������ļ�
 * @author hjr
 * 20131226
 */
public class TnConpayCheckBillFileOper extends AbstractTipsFileOper{
	private Log logger=LogFactory.getLog(TnConpayCheckBillFileOper.class);
	
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {		
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// ҵ������
		mulitDto.setSbookorgcode(sbookorgcode);// �����������
		TnConpaycheckpaybankDto _dto = (TnConpaycheckpaybankDto) paramdto;
		_dto.setSorgcode(sbookorgcode);
		List<IDto> freedtos = new ArrayList<IDto>();
		if(_dto!=null&&"2".equals(_dto.getSext1()))
			return fileParserPaybank(file, mulitDto, freedtos, _dto);
		if(file.endsWith(".csv"))
			return fileParser(file, mulitDto, freedtos, _dto);
		try {
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(sbookorgcode); //���˴��뻺��
			List<String[]> fileContent = super.readFile(file, ",");
			for (int i = 1; i < fileContent.size(); i++) {
				String[] cols = fileContent.get(i);				
				BigDecimal curBalance  = new BigDecimal(cols[8]);//���ڶ�����
			    BigDecimal curPayAmt  = new BigDecimal(cols[6]);//����֧�����
			    BigDecimal curBackAmt  = new BigDecimal(cols[7]);//�����˻ض��
			    BigDecimal curAddAmt  = new BigDecimal(cols[5]);//�����´��� 
				TnConpaycheckbillDto dto = new TnConpaycheckbillDto();
				dto.setSbookorgcode(sbookorgcode);
				dto.setStrecode(_dto.getStrecode());
				dto.setSfuncsbtcode(cols[2]);
				String payKind ="";
				if (StateConstant.COMMON_YES.equals(cols[3])) {
					payKind =StateConstant.Conpay_Grant;
				}else{
					payKind =StateConstant.Conpay_Direct;
				}
				dto.setCamtkind(payKind);
				dto.setSbnkno(cols[4]);
				dto.setFlastmonthzeroamt(curBalance.add(curAddAmt.negate()).add(curPayAmt.add(curBackAmt.negate())));//���ڶ����ϵͳ������ + ���������� - ¼���ȷ�����
				dto.setFcurzeroamt(curBalance);//���ڶ�����
				dto.setFcurreckzeroamt(curPayAmt.add(curBackAmt.negate()));//���������ȣ�֧����ȷ�����-�˻ض��
				dto.setFcursmallamt(curAddAmt);//�������Ӷ��(С���ֽ������ֶ���ʱ���������洢���������Ӷ)
				dto.setFlastmonthsmallamt(new BigDecimal(0));//���¶��С���
				dto.setFcurrecksmallamt(new BigDecimal(0));//��������С���֧�����
				dto.setDstartdate(CommonUtil.strToDate(cols[9]));
				dto.setDenddate(CommonUtil.strToDate(cols[10]));
				if (i==1) {
					TnConpaycheckbillDto tmp = new TnConpaycheckbillDto();
				    tmp.setDstartdate(CommonUtil.strToDate(cols[9]));
					tmp.setDenddate(CommonUtil.strToDate(cols[10]));
					tmp.setStrecode(_dto.getStrecode());
					tmp.setCamtkind(payKind);
					CommonFacade.getODB().deleteRsByDto(tmp);
					
				}
				dto.setSbdgorgcode(cols[1]);//Ԥ�㵥λ����
				dto.setSbdgorgname(rpmap.get(cols[1])==null?"":rpmap.get(cols[1]).getSbookorgcode());//Ԥ�㵥λ����
				freedtos.add(dto);				    
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("ɾ���ظ������쳣",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("ɾ���ظ������쳣",e);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("��ȶ��˵��ļ�����ʧ�ܣ�",e);
		} 
		mulitDto.setFatherDtos(freedtos);
		return mulitDto;
	}
	
	/**
	 * ����.csv�ļ�
	 * @param file
	 * @param mulitDto
	 * @param freedtos
	 * @param _dto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParser(String file, MulitTableDto mulitDto ,
			List<IDto> freedtos,TnConpaycheckpaybankDto _dto) throws ITFEBizException{		
		try {
			List<String[]> fileContent = super.readFile(file, " ");
			String[] cols2=null;
			String startdate="";
			String enddate="";
			String bgttype=null;
			for (int i = 5; i < fileContent.size();i++) {		//��6�У�
				if(i==5)
				{
					cols2 = getColumnsContent(fileContent.get(i-4));//��3�У� Ԥ������
					if(cols2[0]!=null&&cols2[0].contains("Ԥ����"))
						bgttype = MsgConstant.BDG_KIND_IN;
					else if(cols2[0]!=null&&cols2[0].contains("Ԥ����"))
						bgttype = MsgConstant.BDG_KIND_OUT;
					cols2 = getColumnsContent(fileContent.get(i-3));//��3�У�   ������������	 ��ʼ����		��ֹ����
					if(cols2.length>=3&&cols2[2].contains("��"))
					{
						startdate=cols2[2].replace("��", "").replace("��", "").replace("��", "");	//��ʼ����
						if(cols2.length>=4&&cols2[3].contains("��"))
							enddate=cols2[3].replace("��", "").replace("��", "").replace("��", "");	//��ֹ����
					}
					else if(cols2.length>=4&&cols2[3].contains("��"))
					{
						startdate=cols2[3].replace("��", "").replace("��", "").replace("��", "");	//��ʼ����
						if(cols2.length>=5&&cols2[4].contains("��"))
							enddate=cols2[4].replace("��", "").replace("��", "").replace("��", "");	//��ֹ����
					}
					//TCBS�����ļ��仯����ֹ���ڱ�Ϊ������
//					enddate=cols2[4].replace("��", "").replace("��", "").replace("��", "");	//��ֹ����	
//					enddate=cols2[3].replace("��", "").replace("��", "").replace("��", "");	//��ֹ����	
				}
				String[] cols= getColumnsContent(fileContent.get(i));//��6�У�   ���  ֧������  Ԥ�㵥λ����  Ԥ�㵥λ���� Ԥ���Ŀ���� .....  
				String payKind = null;
				
				if(cols!=null&&cols.length>7){
					if(cols[1] != null && !"".equals(cols[1])){
						if(!"ֱ��֧��".equals(cols[1]) && !"��Ȩ֧��".equals(cols[1])){
							logger.error(cols[1]);
							continue;
						}
					}
					payKind = getAmtkind(cols[1]);
				}
				if("1".equals(payKind)||"2".equals(payKind))
				{
					TnConpaycheckbillDto dto = new TnConpaycheckbillDto();							
					dto.setDstartdate(CommonUtil.strToDate(startdate));//��ʼ����
					dto.setDenddate(CommonUtil.strToDate(enddate));//��ֹ����		
					dto.setSbookorgcode(mulitDto.getSbookorgcode());//�����������
					dto.setStrecode(_dto.getStrecode());//�����������
					dto.setSbdgorgcode(cols[2]);//Ԥ�㵥λ����
					if(cols.length==9)
					{
						dto.setSbdgorgname(cols[3]);//Ԥ�㵥λ����					
						dto.setSbnkno(_dto.getSbankcode());//�������д���
						dto.setSfuncsbtcode(cols[4]);//�������Ŀ���� 
						dto.setSecosbtcode(bgttype);//Ԥ������			
						dto.setCamtkind(getAmtkind(cols[1]));//�������
						dto.setFlastmonthzeroamt(new BigDecimal(cols[5]));//���ڶ����� 
						dto.setFcursmallamt(new BigDecimal(cols[6]));//�������Ӷ��
						dto.setFcurreckzeroamt(new BigDecimal(cols[7]));//�������������� 
						dto.setFcurzeroamt(new BigDecimal(cols[8]));//���ڶ�����
					}else if(cols.length==8)
					{
						dto.setSbdgorgname("δ֪");//Ԥ�㵥λ����					
						dto.setSbnkno(_dto.getSbankcode());//�������д���
						dto.setSfuncsbtcode(cols[3]);//�������Ŀ���� 
						dto.setSecosbtcode(bgttype);//Ԥ������			
						dto.setCamtkind(getAmtkind(cols[1]));//�������
						dto.setFlastmonthzeroamt(new BigDecimal(cols[4]));//���ڶ����� 
						dto.setFcursmallamt(new BigDecimal(cols[5]));//�������Ӷ��
						dto.setFcurreckzeroamt(new BigDecimal(cols[6]));//�������������� 
						dto.setFcurzeroamt(new BigDecimal(cols[7]));//���ڶ�����
					}
					dto.setFlastmonthsmallamt(null);//����С���ֽ������
					dto.setFcurrecksmallamt(null);//����С���ֽ���������
					dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));						
					freedtos.add(dto);
					if(i==5)
					{
						TnConpaycheckbillDto tmp = new TnConpaycheckbillDto();
						tmp.setDstartdate(CommonUtil.strToDate(startdate));
						tmp.setDenddate(CommonUtil.strToDate(enddate));
						tmp.setStrecode(_dto.getStrecode());
						tmp.setSbnkno(_dto.getSbankcode());
						tmp.setCamtkind(payKind);
						tmp.setSecosbtcode(bgttype);//Ԥ������
						CommonFacade.getODB().deleteRsByDto(tmp);
					}
				}
			}	
		} catch (FileOperateException e) {
			logger.error(e);
			throw new ITFEBizException("�ļ������쳣",e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("ɾ���ظ������쳣",e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("ѡ��ĵ����ļ���ʽ�������֤��",e);	
		}	
		mulitDto.setFatherDtos(freedtos);
		return mulitDto;	
	}
	/**
	 * ����.csv�ļ�
	 * @param file
	 * @param mulitDto
	 * @param freedtos
	 * @param _dto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParserPaybank(String file, MulitTableDto mulitDto ,
			List<IDto> freedtos,TnConpaycheckpaybankDto dto) throws ITFEBizException{	
		SQLExecutor execDetail = null;
		try {
			List<String[]> fileContent = super.readFile(file, " ");
			String[] cols2=null;
			String startdate="";
			Matcher match = null;
			Pattern patt = Pattern.compile("[0-9]{1,60}");// ƥ��С��32λ����
			for (int i = 0; i < fileContent.size();i++) {		//��6�У�
				cols2 = fileContent.get(i);
				List<String> templist = new ArrayList<String>();
				for(String temp:cols2)
				{
					if(temp!=null&&!"".equals(temp))
						templist.add(temp);
				}
				cols2 = new String[templist.size()];
				cols2 = templist.toArray(cols2);
				if(cols2[0].contains("��������"))
				{
					startdate = cols2[1];
					startdate = startdate.replace("��", "");
					startdate = startdate.replace("��", "");
					startdate = startdate.replace("��", "");
					dto.setSacctdate(startdate);
				}else
				{
					match = patt.matcher(cols2[0]);
					if(match.matches()==true&&cols2.length==4)
					{
						TnConpaycheckpaybankDto tmpdto = new TnConpaycheckpaybankDto();
						tmpdto.setSdealno(VoucherUtil.getGrantSequence());
						tmpdto.setSorgcode(dto.getSorgcode());
						tmpdto.setStrecode(dto.getStrecode());
						tmpdto.setSacctdate(dto.getSacctdate());
						tmpdto.setSbankcode(dto.getSbankcode());
						tmpdto.setSbgtlevel(dto.getSbgtlevel());
						tmpdto.setSext2(dto.getSext2());//���˵��·�
						tmpdto.setSbgttypecode(dto.getSbgttypecode());
						tmpdto.setSpaytypecode(dto.getSpaytypecode());
						tmpdto.setSsubjectcode(cols2[0]);
						tmpdto.setSsubjectname(cols2[1]);
						tmpdto.setNmonthamt(new BigDecimal(StringUtil.replace(cols2[2], "\"", "").replace(",", "")));
						tmpdto.setNyearamt(new BigDecimal(StringUtil.replace(cols2[3],"\"","").replace(",", "")));
						freedtos.add(tmpdto);
					}else
					{
						continue;
					}
					
				}
			}
			if(freedtos!=null&&freedtos.size()>0)
			{
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = "delete from TN_CONPAYCHECKPAYBANK where s_orgcode=? and s_trecode=? and s_bankcode=? and s_bgtlevel=? and s_bgttypecode=? and s_paytypecode=? and s_ext2=?";
				execDetail.addParam(dto.getSorgcode());
				execDetail.addParam(dto.getStrecode());
				execDetail.addParam(dto.getSbankcode());
				execDetail.addParam(dto.getSbgtlevel());
				execDetail.addParam(dto.getSbgttypecode());
				execDetail.addParam(dto.getSpaytypecode());
				execDetail.addParam(dto.getSext2());
				execDetail.runQuery(sql);
			}
				
		} catch (FileOperateException e) {
			logger.error(e);
			throw new ITFEBizException("�ļ������쳣",e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("ɾ���ظ������쳣",e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("ѡ��ĵ����ļ���ʽ�������֤��",e);	
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}	
		mulitDto.setFatherDtos(freedtos);
		return mulitDto;	
	}
	/**
	 * ��ȡ֧����ʽ
	 * @param amtkind
	 * @return
	 * @throws ITFEBizException
	 */
	public String getAmtkind(String amtkind) throws ITFEBizException{
		if(amtkind.equals("��Ȩ֧��")){
			return "2";
		}else if(amtkind.equals("ֱ��֧��")){
			return "1";
		}else if(amtkind.equals("֧������")){
			return "0";
		}else{
			throw new ITFEBizException("ѡ��ĵ����ļ���ʽ����֧�������ֵ���� \"��Ȩ֧��\" �� \"ֱ��֧��\" ��");
		}		
	}
	
	/**
	 * �����������Ʋ������д���
	 * @param sbankname
	 * @param ls_OrgCode
	 * @return
	 */
	public String getSbanknoBybankname(String sbankname,String ls_OrgCode){
		Map<String, TsConvertbanknameDto> bankInfo=new HashMap<String, TsConvertbanknameDto>();
	//	Map<String, TsConvertbanknameDto> bankInfo=BusinessFacade.getBankInfo(ls_OrgCode);
		if(bankInfo!=null&&bankInfo.size()>0){
			TsConvertbanknameDto dto = bankInfo.get(sbankname);
			if(dto!=null)
				return dto.getSbankcode();
		}return null;
	}
	/**
	 * ��ȡ������
	 * @param cols
	 * @return
	 */
	public String[] getColumnsContent(String[] cols){
		StringBuffer rows = new StringBuffer();
		for(int x = 0 ; x < cols.length ; x ++){
			if(cols[x].trim().length() != 0){
				rows.append(cols[x].trim().replaceAll("\"", "").replaceAll(",", "").trim()).append(",");
			}
		}
		return rows.toString().trim().split(",");
	}
	
	
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}

}
