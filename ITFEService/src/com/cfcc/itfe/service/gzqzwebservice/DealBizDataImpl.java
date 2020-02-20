package com.cfcc.itfe.service.gzqzwebservice;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;
import com.cfcc.itfe.service.expreport.IMakeReport;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

@WebService(endpointInterface = "com.cfcc.itfe.service.gzqzwebservice.IDealBizData", serviceName = "dealbizdata")
public class DealBizDataImpl implements IDealBizData {

	private static Log log = LogFactory.getLog(DealBizDataImpl.class);
	ICommonDataAccessService commonDataAccessService = new CommonDataAccessService();

	/**
	 * ������ȡ�ط�����ϵͳ˰Ʊ����˰��֧����ʵ��������֧����ȡ���������ݵ������ۺ�ǰ�á�
	 * @param fileStr   ҵ������(ǰ���ϴ����ļ�����)
	 * @param biztype   ҵ������
	 * @param paramStr ԭǰ�ý�����Ҫ¼��Ҫ�أ��ַ������룬�Զ��ŷָ�
	 * @return          �Ƿ�ɹ� 0-ʧ��  1-�ɹ�
	 * @throws ITFEBizException
	 */
	public String readCommBizData(String fileStr, String biztype, String paramStr,String fileName)
			throws ITFEBizException {
		log.info("==========��ȡ�ط�����ϵͳ˰Ʊ����˰��֧����ʵ��������֧����ȡ���������ݿ�ʼ==========");
		if(fileStr==null || fileStr.trim().equals("")){
			throw new ITFEBizException("˰Ʊ��Ϣ���ݲ�����Ϊ�գ�");
		}
		if(biztype==null || biztype.trim().equals("")){
			throw new ITFEBizException("ҵ�����Ͳ�����Ϊ�գ�");
		}
		if(fileName==null || fileName.trim().equals("")){
			throw new ITFEBizException("�ļ����Ʋ�����Ϊ�գ�");
		}
		
		if(biztype.trim().equals("11")){ //����˰Ʊ���ݵ���
			/*FileResultDto fileResultDto = new FileResultDto();
			if (StateConstant.TRIMSIGN_FLAG_TRIM.equals(fileName.substring(12, 13))) {
				Date adjustdate = commonDataAccessService.getAdjustDate();
				String str = commonDataAccessService.getSysDBDate();
				Date systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
				if(com.cfcc.jaf.common.util.DateUtil.after(systemDate,adjustdate)){
					throw new ITFEBizException("������" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "�ѹ������ܴ��������ҵ��");
				}
			}
			fileResultDto = dealFile(fPath, fName, loginfo.getSorgcode(), sequenceHelperService);
			if (BizTypeConstant.BIZ_TYPE_INCOME.equals(fileResultDto.getSbiztype())) {
				if(fileResultDto.getFileColumnLen() != 13) {
					// ����ҵ��ҪУ���ʽ�������ˮ���Ƿ���д
					if(null == inputsrlnodto.getStrasrlno() || "".equals(inputsrlnodto.getStrasrlno().trim())
							|| inputsrlnodto.getStrasrlno().trim().length() != 18){
						MessageDialog.openMessageDialog(null, "����ҵ���ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
						return super.upload(o);
					}
					fileResultDto.setIsError(false);
					fileResultDto.setStrasrlno(inputsrlnodto.getStrasrlno().trim());
				}else {
					fileResultDto.setIsError(false);
				}
			}else{
				if(null == inputsrlnodto.getNmoney()){
					MessageDialog.openMessageDialog(null, "֧����ҵ���ļ��ܽ�������д��");
					return super.upload(o);
				}
				fileResultDto.setFsumamt(inputsrlnodto.getNmoney());
			}*/
		}else if(biztype.trim().equals("17") || biztype.trim().equals("23")){ //ʵ���ʽ����ݵ���
			
		}else if(biztype.trim().equals("01")){ //ֱ��֧����ȵ���
			
		}else if(biztype.trim().equals("02")){ //��Ȩ֧����ȵ���
			
		}else if(biztype.trim().equals("13")){ //�˿�ƾ֤���ݵ���
			
		}else if(biztype.trim().equals("15")){ //����ƾ֤���ݵ���
			
		}
		return "1";
	}

	public String sendBizSeriData(String params) throws ITFEBizException {
		// TODO Auto-generated method stub
		try {
			TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
			Map<String, String> paramMap = JOSNUtils.getJOSNContent(params);
			if (null == paramMap || paramMap.size() == 0) {
				throw new ITFEBizException("������Ϣ����Ϊ�գ�");
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.TRECODE))) {
				throw new ITFEBizException("������벻��Ϊ�գ�");
			} else {
				// �������
				incomedto.setStrecode(paramMap.get(JOSNUtils.TRECODE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.ACCTDATE))) {
				throw new ITFEBizException("�������ڲ���Ϊ�գ�");
			} else {
				// ����
				incomedto.setSrptdate(paramMap.get(JOSNUtils.ACCTDATE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.TRIMFLAG))) {
				throw new ITFEBizException("�����ڱ�ʶ����Ϊ�գ�");
			} else {
				// �����ڱ�־
				incomedto.setStrimflag(paramMap.get(JOSNUtils.TRIMFLAG));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BUDGETTYPE))) {
				throw new ITFEBizException("Ԥ�����಻��Ϊ�գ�");
			} else {
				// Ԥ������
				incomedto.setSbudgettype(paramMap.get(JOSNUtils.BUDGETTYPE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.TAXORGCODE))) {
				throw new ITFEBizException("���ջ��ش��벻��Ϊ�գ�");
			} else {
				// ���ջ��ش���
				incomedto.setStaxorgcode(paramMap.get(JOSNUtils.TAXORGCODE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BELONGFLAG))) {
				throw new ITFEBizException("Ͻ����ʶ����Ϊ�գ�");
			} else {
				// Ͻ����־
				incomedto.setSbelongflag(paramMap.get(JOSNUtils.BELONGFLAG));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BELONGFLAG))) {
				throw new ITFEBizException("����ϼƱ�ʶ����Ϊ�գ�");
			} else {
				// �Ƿ񺬿�ϼ�
				incomedto.setSdividegroup(paramMap.get(JOSNUtils.BELONGFLAG));
			}
			List<TdEnumvalueDto> enumvalueList = null;
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BILLTYPE))) {
				throw new ITFEBizException("�����ʽ����Ϊ�գ�");
			} else {
				TdEnumvalueDto tdEnumvalueDto = new TdEnumvalueDto();
				tdEnumvalueDto.setStypecode(StateConstant.BILL_TYPE);
				tdEnumvalueDto.setSvalue(paramMap.get(JOSNUtils.BILLTYPE));
				enumvalueList = CommonFacade.getODB().findRsByDto(
						tdEnumvalueDto);
				if (null == enumvalueList || enumvalueList.size() == 0) {
					throw new ITFEBizException("���������б��������");
				}
			}

			// FinDataStatDownService finDataStatDownService = new
			// FinDataStatDownService();
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			HashMap<String, String> resultMap = new HashMap<String, String>();
			TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
			tsTreasuryDto.setStrecode(incomedto.getStrecode());
			tsTreasuryDto = (TsTreasuryDto) CommonFacade.getODB().findRsByDto(
					tsTreasuryDto).get(0);
			TsOrganDto tsOrganDto = new TsOrganDto();
			tsOrganDto.setSorgcode(tsTreasuryDto.getSorgcode());
			tsOrganDto = (TsOrganDto) CommonFacade.getODB().findRsByDto(
					tsOrganDto).get(0);
			String bizType = enumvalueList.get(0).getSvalue();
			String areaBizType = bizType;
			if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(AreaSpecUtil
					.getInstance().getArea())) {
				areaBizType = areaBizType + MsgConstant.PLACE_GZ;
			}
			if ((StateConstant.IS_COLLECT_YES).equals(tsOrganDto
					.getSiscollect())
					&& StateConstant.REPORTTYPE_9.equals(bizType)) {
				areaBizType = bizType + MsgConstant.PLACE_GZ
						+ StateConstant.IS_COLLECT_YES;
			}
			IMakeReport makereport = (IMakeReport) ContextFactory
					.getApplicationContext().getBean(
							MsgConstant.SPRING_EXP_REPORT + areaBizType);
			String filepath = makereport.makeReportByBiz(incomedto, bizType,
					tsOrganDto.getSorgcode());
			if(StringUtils.isBlank(filepath)){
				return null;
			}else{
				return FileUtil.getInstance().readFile(root + filepath);
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ���ݿ�ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		} catch (FileOperateException e) {
			log.error("��ȡ�ļ�ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			log.error("��ѯ���ݿ�ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
	}
	
	
	/**
	 * ������Ϣ����
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @param ISequenceHelperService
	 *            sequenceHelperService SEQ����SERVER
	 * @return
	 * @throws ITFEBizException
	 */
	public FileResultDto dealFile(String filepath, String filename, String orgcode,
			ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		
		// �����ļ�����ȡ���ļ�DTO���� - Ȼ��ȡҵ������
		/*FileObjDto fileobj = getFileObjByFileName(filename);
		String sbiztype = fileobj.getSbiztype();
		String csourceflag = fileobj.getCsourceflag();
		
		if(MsgConstant.MSG_SOURCE_PLACE.equals(csourceflag)){
			// ��˰�ӿ����ݸ�ʽ
			FileResultDto rtndto = new FileResultDto();
			rtndto.setCsourceflag(csourceflag);
			rtndto.setSbiztype(sbiztype);
			rtndto.setSmsgno(MsgConstant.MSG_NO_7211);
			return rtndto;
		}
		if(MsgConstant.MSG_SOURCE_NATION.equals(csourceflag)){
			// ��˰�ӿ����ݸ�ʽ
			return 	FileOperFacade.dealIncomeFileForNation(filepath, filename, orgcode);
		}
		if(MsgConstant.MSG_SOURCE_TIPS.equals(csourceflag)){
			// TIPS�ӿ����ݸ�ʽ
			return  FileOperFacade.dealIncomeFileTips(filepath, filename, orgcode);
		}
		
		if(BizTypeConstant.BIZ_TYPE_INCOME.equals(sbiztype)){
			// ����ҵ��
			return 	FileOperFacade.dealIncomeFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(sbiztype)){
			// ֧��ҵ��
			return FileOperFacade.dealPayOutFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(sbiztype)){
			// ֱ��֧�����ҵ��
			return FileOperFacade.dealDirectPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else if(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(sbiztype)){
			// ��Ȩ֧�����ҵ��
			return FileOperFacade.dealGrantPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else{
			throw new ITFEBizException("ҵ������[" + sbiztype + "]����!");
		}*/
		return null;
	}
	
	
	/**
	 * �����ļ�����ת��DTO
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename) throws ITFEBizException{
		return PublicSearchFacade.getFileObjByFileName(filename);
	}

	/**
	 * ���ϴ��������ļ�(��˰)�����õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealIncomeFileForNation(String filepath, String filename,
			String orgcode) throws ITFEBizException{
		
		/*InputStream inputStream = null ;
	
		try {
			inputStream = new FileInputStream(filepath); 
			DBFReader reader = new DBFReader(inputStream);
			reader.setCharactersetName("GBK");

			List<TvInfileTmpCountryDto> rtnlist = new ArrayList<TvInfileTmpCountryDto>();
			
			Object[] rowObjects;
			int j = 0 ;	
			while ((rowObjects = reader.nextRecord()) != null) {
				TvInfileTmpCountryDto tmpdto = new TvInfileTmpCountryDto();
				
				j = 0 ;
				tmpdto.setStaxticketno((String)change(rowObjects[j++]));//˰Ʊ����
				j++;//������ϵ����
				tmpdto.setScorptypename((String)change(rowObjects[j++])); //�Ǽ�ע����������
				tmpdto.setSbilldate((String)change(rowObjects[j++]));// ��Ʊ����
				tmpdto.setStaxorgcode(splitString((String)change(rowObjects[j++]), "|", null));//���ջ��ش���
				tmpdto.setScorpcode((String)change(rowObjects[j++]));//��˰��ʶ���
				tmpdto.setScorpname((String)change(rowObjects[j++]));//��˰������
				tmpdto.setSpaybnkno(splitString((String)change(rowObjects[j++]), "|", null));//���д���
				tmpdto.setSpayacct((String)change(rowObjects[j++]));//�����˺�
				tmpdto.setSbudgetsubcode((String)change(rowObjects[j++]));//Ԥ���Ŀ����
				j++;//Ԥ���Ŀ����
				tmpdto.setSbudgetlevelcode(splitString((String)change(rowObjects[j++]), "|", null));//Ԥ�㼶�δ���
				tmpdto.setSpayeetrecode(splitString((String)change(rowObjects[j++]), "(", ")"));//�տ��������
				tmpdto.setStaxstartdate((String)change(rowObjects[j++]));// ����ʱ��-��
				tmpdto.setStaxenddate((String)change(rowObjects[j++]));// ����ʱ��-ֹ
				tmpdto.setSlimitdate((String)change(rowObjects[j++]));// ˰���޽�����
				tmpdto.setStaxtypename((String)change(rowObjects[j++]));//����ƷĿ����
				j++;//��˰����
				j++;//�������� 
				j++;//˰��
				tmpdto.setNfacttaxamt(new BigDecimal((String)change(rowObjects[j++])));//ʵ�ɽ��
				tmpdto.setSfilename(filename);
				tmpdto.setSorgcode(orgcode);
				
				rtnlist.add(tmpdto);
			}

			inputStream.close();
			
			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_7211);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setMainlist(rtnlist);
			fileRstDto.setCsourceflag(MsgConstant.MSG_SOURCE_NATION);
			
			return fileRstDto;
		} catch (DBFException e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} catch (IOException e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} finally{
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
					throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
				}
			}
		}*/
		return null;
		
	}
}
