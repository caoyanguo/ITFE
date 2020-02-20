package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@SuppressWarnings("unchecked")
public class TrTaxorgPayoutFileOper extends AbstractTipsFileOper{
	private static Log log = LogFactory.getLog(TrTaxorgPayoutFileOper.class);
	@SuppressWarnings("deprecation")
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {		
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// ҵ������
		mulitDto.setSbookorgcode(sbookorgcode);// �����������
		List<IDto> freedtos = new ArrayList<IDto>();
		TrTaxorgPayoutReportDto inputDto = (TrTaxorgPayoutReportDto)paramdto;
		if(file.contains("������")){
			inputDto.setStrimflag(MsgConstant.TIME_FLAG_TRIM);
		}else{
			inputDto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL);
		}
		if(file!=null&&file.endsWith(".xml"))
			return fileParse(file, freedtos, mulitDto, inputDto);
		if(match(file.substring(file.lastIndexOf(File.separator)+1, file.length())))
			return fileParser(file, mulitDto, freedtos, inputDto);
		try {
			String sbillType = null ;
			String sbillDate = null;
			List<String[]> fileContent = super.readFile(file, " ");
			for (int i = 0; i < fileContent.size(); i++) {
				if (i==0) {//��0��ȡ��������
					String[] cols = fileContent.get(i);
					String titleStr = StringUtils.concatenate(cols);
					// �����������뻺�� keyΪ�������
					HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade.cacheFincInfo(null);
					if (titleStr.contains(("һ��Ԥ��֧���ձ���"))  ) {//һ��Ԥ��֧������ ��ֵ 1
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_1;
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					}else if(titleStr.contains("ʵ���ʽ��ձ���")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_2;
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					}else if(titleStr.contains("����֧���ձ���")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_3;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					}else if(titleStr.contains("ֱ��֧���ձ���")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_4;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else if(titleStr.contains("��Ȩ֧���ձ���")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_5;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else if(titleStr.contains("ֱ��֧���˻��ձ���")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_6;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else if(titleStr.contains("��Ȩ֧���˻��ձ���")){
						sbillType = StateConstant.REPORT_PAYOUT_TYPE_7;						
						inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());						
					}else{
						throw new ITFEBizException("ѡ��ĵ����ļ��������֤��");
					}									
					inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					if(titleStr.contains("���ܷ���")){
						inputDto.setSpaytype("0");
					}else if(titleStr.contains("���÷���")){
						inputDto.setSpaytype("1");
					}
					//��1��ȡϽ����־��Ԥ������
					String[] cols1 = fileContent.get(i+1);
					String titleStr1 = StringUtils.concatenate(cols1);
					if(titleStr1.contains("����")){
						inputDto.setSbelongflag(MsgConstant.RULE_SIGN_SELF);
					}else if(titleStr1.contains("ȫϽ")){
						inputDto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
					}
					
					if(titleStr1.contains("Ԥ����")){
						inputDto.setSbudgettype(MsgConstant.BDG_KIND_IN);
					}else if(titleStr1.contains("Ԥ����")){
						inputDto.setSbudgettype(MsgConstant.BDG_KIND_OUT);
					}
					
					String[] cols2 = fileContent.get(i+2);
					for (int j = 0; j < cols2.length; j++) {
						if (cols2[j].contains("��")) {
							String year = cols2[j].substring(0,cols2[j].indexOf("��"));
							String month = cols2[j].substring(cols2[j].indexOf("��")+1,cols2[j].indexOf("��"));
							String day = cols2[j].substring(cols2[j].indexOf("��")+1,cols2[j].indexOf("��"));
							if(month!=null&&month.length()!=2)
								month = "0"+month;
							if(day!=null&&day.length()!=2)
								day = "0"+day;
							sbillDate = year+month+day;
							if (!inputDto.getSrptdate().equals(sbillDate)) {
								throw new ITFEBizException("����ı�������"+inputDto.getSrptdate()+"���ļ������ı�������"+sbillDate+"������");
							}
						}
					}
					do{
						++i;//i += 4;	//��λ�����һ��            ���磺 ��Ŀ����    ��Ŀ����    ���շ�����    �����ۼ�   �����ۼ�
					}while(fileContent.get(i)!=null&&!fileContent.get(i)[0].contains("��Ŀ����"));
					++i;	
				}
				String[] cols = fileContent.get(i);
				if (StringUtils.concatenate(cols).contains("�ձ���")) {
					do{
						++i;//i += 4;	//��λ�����һ��            ���磺 ��Ŀ����    ��Ŀ����    ���շ�����    �����ۼ�   �����ۼ�
					}while(fileContent.get(i)!=null&&!fileContent.get(i)[0].contains("��Ŀ����"));
				    cols = fileContent.get(++i);
				}
				if(StringUtils.concatenate(cols).contains("�ϼ���")){//����ҵ��ϼ������� �������     ���磺�ϼ���    "286,666.25"    "299,666.25"   "299,666.25"
					i = fileContent.size();		//����ѭ��
					continue;
				}
				StringBuffer rows = new StringBuffer();
				for(int x = 0 ; x < cols.length ; x ++){
					if(cols[x].trim().length() != 0){
						rows.append(cols[x].replaceAll("\"", "").replaceAll(",", "").trim()).append(",");
					}
				}
				String[] row = rows.toString().split(",");
				if(row.length == 5){
					BigDecimal yearAmt = null;
					BigDecimal monAmt = null;
					BigDecimal dayAmt = null;
					yearAmt = new BigDecimal(row[row.length -1].replaceAll("\"", "").replaceAll(",", ""));
					monAmt = new BigDecimal(row[row.length -2].replaceAll("\"", "").replaceAll(",", ""));
					dayAmt = new BigDecimal(row[row.length -3].replaceAll("\"", "").replaceAll(",", ""));
					TrTaxorgPayoutReportDto dto = new TrTaxorgPayoutReportDto();
					dto.setStrecode(inputDto.getStrecode());
					dto.setSfinorgcode(inputDto.getSfinorgcode());
					dto.setStaxorgcode(sbillType);
					dto.setSbudgettype(inputDto.getSbudgettype());
					dto.setSrptdate(inputDto.getSrptdate());
					dto.setSbudgetlevelcode(inputDto.getSbudgetlevelcode());
					dto.setSbudgetsubcode(row[0]);
					dto.setSbudgetsubname(row[1]);
					dto.setStrimflag(inputDto.getStrimflag());
					dto.setSpaytype(inputDto.getSpaytype());
					dto.setSbelongflag(inputDto.getSbelongflag());
					dto.setNmoneyday(dayAmt);
					dto.setNmoneymonth(monAmt);
					dto.setNmoneyyear(yearAmt);
					dto.setNmoneytenday(new BigDecimal("0.00"));
					dto.setNmoneyquarter(new BigDecimal("0.00"));
					freedtos.add(dto);
				//����ʽ���������ͳ�������ݣ�������������Ϊ5ʱ��	
				}else if(i > 3 && row.length != 5){
					continue;
				}
				else{	
					throw new ITFEBizException("�����ļ���ʽ��׼ȷ��ϵͳ��֧������ʽ����ĵ���,����!");
				}					
			}
			TrTaxorgPayoutReportDto  tmpdto = new TrTaxorgPayoutReportDto();
			tmpdto.setStrecode(inputDto.getStrecode());
			tmpdto.setSrptdate(inputDto.getSrptdate());
			tmpdto.setStaxorgcode(sbillType);//
			CommonFacade.getODB().deleteRsByDto(tmpdto);//ɾ���ظ�����
		} catch (FileOperateException e) {
			throw new ITFEBizException("��������쳣",e);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��ѯ��������쳣",e);
		}catch(ITFEBizException e){
			throw new ITFEBizException(e.getMessage(),e);
		}catch(Exception e){
			throw new ITFEBizException("ѡ��ĵ����ļ���ʽ�������֤��");
		}
		mulitDto.setFatherDtos(freedtos);
		saveDownloadReportCheck(inputDto.getSrptdate(),inputDto.getStrecode());//�������ر���������
		return mulitDto;		
	}
	
	/**
	 * У�鼯��֧���ձ������ļ��������� 
	 * [���д���(12λ����)_֧������(1-ֱ��֧�� 2��Ȩ֧�� 3ֱ��֧���˻� 4��Ȩ֧���� ��)+��.CSV��]
	 * @param fileName
	 * @return
	 */
	public boolean match(String fileName){
		if(fileName==null||fileName.length()!=18||!fileName.endsWith(".csv"))
			return false;			
		if(!(fileName.substring(13, 14).equals("1")||fileName.substring(13, 14).equals("2")||
				fileName.substring(13, 14).equals("3")||fileName.substring(13, 14).equals("4")))
			return false;
		Pattern pattern=Pattern.compile("[0-9]{12}");//ƥ��12λ����
		Matcher match=pattern.matcher(fileName.substring(0, 12));
		return match.matches()&&fileName.indexOf("_")!=-1;	
	}
	
	/**
	 * ��������֧��������
	 * @param file
	 * @param mulitDto
	 * @param freedtos
	 * @param inputDto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParser(String file, MulitTableDto mulitDto ,
			List<IDto> freedtos,TrTaxorgPayoutReportDto inputDto) throws ITFEBizException{
			String sbillType=(Integer.parseInt(file.substring(file.lastIndexOf(File.separator)+1, file.length()).substring(13, 14)+"")+3)+"";
			inputDto.setSfinorgcode(file.substring(file.lastIndexOf(File.separator)+1, file.length()).substring(0, 12));			
			try {
				List<String[]> fileContent = super.readFile(file, " ");
				for (int i = 0; i < fileContent.size(); ) {
					if(i+3==fileContent.size())
						break;
					String year = getColumnsContent(fileContent.get(i+2))[1].substring(0,getColumnsContent(fileContent.get(i+2))[1].indexOf("��"));
					String month = getColumnsContent(fileContent.get(i+2))[1].substring(getColumnsContent(fileContent.get(i+2))[1].indexOf("��")+1,getColumnsContent(fileContent.get(i+2))[1].indexOf("��"));
					String day = getColumnsContent(fileContent.get(i+2))[1].substring(getColumnsContent(fileContent.get(i+2))[1].indexOf("��")+1,getColumnsContent(fileContent.get(i+2))[1].indexOf("��"));
					if(month!=null&&month.length()!=2)
						month = "0"+month;
					if(day!=null&&day.length()!=2)
						day = "0"+day;
					String rptdate=year+month+day;//(getColumnsContent(fileContent.get(i+2)))[1].replace("��", "").replace("��", "").replace("��", "");//��4�У� ��������
					if(!rptdate.equals(inputDto.getSrptdate()))
						throw new ITFEBizException("����ı�������"+inputDto.getSrptdate()+"���ļ������ı�������"+rptdate+"������");
					String[] cols= getColumnsContent(fileContent.get(i += 4));//��6�У�   ��Ŀ����   ��Ŀ����   ���շ�����  �����ۼ�  �����ۼ� 
					while(cols.length==5){
						TrTaxorgPayoutReportDto dto = new TrTaxorgPayoutReportDto();
						dto.setStrecode(inputDto.getStrecode());
						dto.setSfinorgcode(inputDto.getSfinorgcode());
						dto.setStaxorgcode(sbillType);
						dto.setSbudgettype(inputDto.getSbudgettype());
						dto.setSrptdate(inputDto.getSrptdate());
						dto.setSbudgetlevelcode(inputDto.getSbudgetlevelcode());						
						dto.setSbudgetsubcode(cols[0]);//��Ŀ����
						dto.setSbudgetsubname(cols[1]);//��Ŀ����
						dto.setNmoneyday(new BigDecimal(cols[2]));//���շ�����
						dto.setNmoneymonth(new BigDecimal(cols[3]));//�����ۼ�
						dto.setNmoneyyear(new BigDecimal(cols[4]));//�����ۼ�												
						freedtos.add(dto);
						if(++i==fileContent.size())
							break;
						cols=getColumnsContent(fileContent.get(i));
					}
				}
				//��ѯ�����Ƿ��Ѿ����룬����Ѿ�������ʾ�û����������ظ�����
				TrTaxorgPayoutReportDto  tmpdto = new TrTaxorgPayoutReportDto();
			
				tmpdto.setStrecode(inputDto.getStrecode());
				tmpdto.setSrptdate(inputDto.getSrptdate());
				tmpdto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);//�����е�����xml�ļ�����һ��Ԥ��֧������
				List list = CommonFacade.getODB().findRsByDtoWithUR(tmpdto);
				//����������ݴ���0��˵�����ձ����Ѿ����룬��Ҫ��ɾ���� ���� ���롣
				if (list.size()>0) {
					CommonFacade.getODB().deleteRsByDto(tmpdto);
				}
			} catch (FileOperateException e) {			
				throw new ITFEBizException("�ļ������쳣",e);
			} catch (JAFDatabaseException e) {			
				throw new ITFEBizException("ɾ���ظ������쳣",e);
			}catch (ITFEBizException e) {			
				throw new ITFEBizException(e.getMessage(),e);
			} catch (Exception e) {
				throw new ITFEBizException("ѡ��ĵ����ļ���ʽ�������֤��",e);	
			}	
			mulitDto.setFatherDtos(freedtos);
			saveDownloadReportCheckjzzf(inputDto.getSrptdate(),inputDto.getStrecode());//�������ر���������
			return mulitDto;	
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
		}return rows.toString().trim().split(",");
	}
	
	/**
	 * ����XML�ļ�
	 * @param file
	 * @param freedtos
	 * @param mulitDto
	 * @param inputDto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParse(String file, 
			List<IDto> freedtos, MulitTableDto mulitDto,
			TrTaxorgPayoutReportDto inputDto) throws ITFEBizException{
		File inputXml = new File(file);
		SAXReader saxReader = new SAXReader();// SAXReader ����xml��
		try {
			// 2. ����SAXReader�����ȡ�ļ�����Ϣ��������Document����
			Document doc = saxReader.read(inputXml);
			Element cfx = doc.getRootElement();
			if(cfx!=null)
			{
				Element msg = cfx.element("MSG");
				if(msg==null)
					throw new ITFEBizException("xml�ļ����ݸ�ʽ���ԣ�");
				Element payBody6104Element = msg.element("PayBody6104");
				if(payBody6104Element==null)
					throw new ITFEBizException("xml�ļ����ݸ�ʽ���ԣ�");
				Element paybill6104Element = payBody6104Element.element("PayBill6104");
				if(paybill6104Element==null)
					throw new ITFEBizException("xml�ļ����ݸ�ʽ���ԣ�");
				List<Element> payDetail6104ElementList = paybill6104Element.elements("PayDetail6104");
				if(payDetail6104ElementList!=null&&payDetail6104ElementList.size()>0)
				{
					// �����������뻺�� keyΪ�������
					HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade.cacheFincInfo(null);
					inputDto.setSfinorgcode(mapFincInfo.get(inputDto.getStrecode()).getSfinorgcode());
					//��ѯ�����Ƿ��Ѿ����룬����Ѿ�������ʾ�û����������ظ�����
					TrTaxorgPayoutReportDto  tmpdto = new TrTaxorgPayoutReportDto();
				
					tmpdto.setStrecode(inputDto.getStrecode());
					tmpdto.setSrptdate(inputDto.getSrptdate());
					tmpdto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);//�����е�����xml�ļ�����һ��Ԥ��֧������
					List list = CommonFacade.getODB().findRsByDtoWithUR(tmpdto);
					//����������ݴ���0��˵�����ձ����Ѿ����룬��Ҫ��ɾ���� ���� ���롣
					if (list.size()>0) {
						CommonFacade.getODB().deleteRsByDto(tmpdto);
					}
					for(Element payDetail6104Element:payDetail6104ElementList)
					{
						tmpdto = new TrTaxorgPayoutReportDto();
						tmpdto.setStrecode(inputDto.getStrecode());//�������
						tmpdto.setSrptdate(inputDto.getSrptdate());//��������
						tmpdto.setSfinorgcode(inputDto.getSfinorgcode());//�������ش���
						tmpdto.setSbudgetlevelcode(inputDto.getSbudgetlevelcode());//Ԥ�㼶��
						tmpdto.setSbudgettype(payDetail6104Element.elementText("BudgetType"));//Ԥ������
						tmpdto.setSbudgetsubcode(payDetail6104Element.elementText("BudgetSubjectCode"));//Ԥ���Ŀ����
						tmpdto.setSbudgetsubname(payDetail6104Element.elementText("BudgetSubjectName"));//Ԥ���Ŀ����
						tmpdto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);//�����е�����xml�ļ�����һ��Ԥ��֧������
						tmpdto.setSeconmicsubcode(payDetail6104Element.elementText("EcnomicSubjectCode"));//���ÿ�Ŀ����
						tmpdto.setSeconmicsubname(payDetail6104Element.elementText("EcnomicSubjectName"));//���ÿ�Ŀ����
						tmpdto.setNmoneyday(new BigDecimal(payDetail6104Element.elementText("DayAmt")));//�ս��
						tmpdto.setNmoneytenday(new BigDecimal(payDetail6104Element.elementText("TenDayAmt")));//Ѯ���
						tmpdto.setNmoneymonth(new BigDecimal(payDetail6104Element.elementText("MonthAmt")));//�½��
						tmpdto.setNmoneyquarter(new BigDecimal(payDetail6104Element.elementText("QuarterAmt")));//�����
						tmpdto.setNmoneyyear(new BigDecimal(payDetail6104Element.elementText("YearAmt")));//����
						freedtos.add(tmpdto);
					}
					mulitDto.setFatherDtos(freedtos);
					saveDownloadReportCheck(inputDto.getSrptdate(),inputDto.getStrecode());//�������ر���������
					return mulitDto;
				}else
					throw new ITFEBizException("xml�ļ�Ϊ�������ļ���");
			}
		}catch (DocumentException e) {
			throw new ITFEBizException("����xml�ļ������쳣��", e);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("����xml��ѯ���ݿ����Ƿ��Ѿ���������쳣��", e);
		} catch (ValidateException e) {
			throw new ITFEBizException("����xml��ѯ���ݿ����Ƿ��Ѿ���������쳣��", e);
		}
		return mulitDto;	
	}
	private void saveDownloadReportCheck(String date,String trecode)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setSzhichu("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSzhichu())||null==dto.getSzhichu())
				{
					dto.setSzhichu("1");
					DatabaseFacade.getODB().update(dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("�������ر����������ʧ��:"+e.toString());
		}catch(Exception e)
		{
			log.error("�������ر����������ʧ��:"+e.toString());
		}
	}
	private void saveDownloadReportCheckjzzf(String date,String trecode)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setSext1("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSext1())||null==dto.getSext1())
				{
					dto.setSext1("1");
					DatabaseFacade.getODB().update(dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("�������ر����������ʧ��:"+e.toString());
		}catch(Exception e)
		{
			log.error("�������ر����������ʧ��:"+e.toString());
		}
	}
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}

}
