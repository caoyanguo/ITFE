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
	 * 联机读取地方横联系统税票、退税、支出、实拨、集中支付额度、清算等数据到国库综合前置。
	 * @param fileStr   业务内容(前置上传的文件内容)
	 * @param biztype   业务类型
	 * @param paramStr 原前置界面需要录入要素，字符串传入，以逗号分隔
	 * @return          是否成功 0-失败  1-成功
	 * @throws ITFEBizException
	 */
	public String readCommBizData(String fileStr, String biztype, String paramStr,String fileName)
			throws ITFEBizException {
		log.info("==========读取地方横联系统税票、退税、支出、实拨、集中支付额度、清算等数据开始==========");
		if(fileStr==null || fileStr.trim().equals("")){
			throw new ITFEBizException("税票信息内容不允许为空！");
		}
		if(biztype==null || biztype.trim().equals("")){
			throw new ITFEBizException("业务类型不允许为空！");
		}
		if(fileName==null || fileName.trim().equals("")){
			throw new ITFEBizException("文件名称不允许为空！");
		}
		
		if(biztype.trim().equals("11")){ //电子税票数据导入
			/*FileResultDto fileResultDto = new FileResultDto();
			if (StateConstant.TRIMSIGN_FLAG_TRIM.equals(fileName.substring(12, 13))) {
				Date adjustdate = commonDataAccessService.getAdjustDate();
				String str = commonDataAccessService.getSysDBDate();
				Date systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
				if(com.cfcc.jaf.common.util.DateUtil.after(systemDate,adjustdate)){
					throw new ITFEBizException("调整期" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "已过，不能处理调整期业务！");
				}
			}
			fileResultDto = dealFile(fPath, fName, loginfo.getSorgcode(), sequenceHelperService);
			if (BizTypeConstant.BIZ_TYPE_INCOME.equals(fileResultDto.getSbiztype())) {
				if(fileResultDto.getFileColumnLen() != 13) {
					// 收入业务，要校验资金收纳流水号是否填写
					if(null == inputsrlnodto.getStrasrlno() || "".equals(inputsrlnodto.getStrasrlno().trim())
							|| inputsrlnodto.getStrasrlno().trim().length() != 18){
						MessageDialog.openMessageDialog(null, "收入业务：资金收纳流水号必须填写，且必须为18位！");
						return super.upload(o);
					}
					fileResultDto.setIsError(false);
					fileResultDto.setStrasrlno(inputsrlnodto.getStrasrlno().trim());
				}else {
					fileResultDto.setIsError(false);
				}
			}else{
				if(null == inputsrlnodto.getNmoney()){
					MessageDialog.openMessageDialog(null, "支出类业务：文件总金额必须填写！");
					return super.upload(o);
				}
				fileResultDto.setFsumamt(inputsrlnodto.getNmoney());
			}*/
		}else if(biztype.trim().equals("17") || biztype.trim().equals("23")){ //实拨资金数据导入
			
		}else if(biztype.trim().equals("01")){ //直接支付额度导入
			
		}else if(biztype.trim().equals("02")){ //授权支付额度导入
			
		}else if(biztype.trim().equals("13")){ //退库凭证数据导入
			
		}else if(biztype.trim().equals("15")){ //更正凭证数据导入
			
		}
		return "1";
	}

	public String sendBizSeriData(String params) throws ITFEBizException {
		// TODO Auto-generated method stub
		try {
			TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
			Map<String, String> paramMap = JOSNUtils.getJOSNContent(params);
			if (null == paramMap || paramMap.size() == 0) {
				throw new ITFEBizException("参数信息不能为空！");
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.TRECODE))) {
				throw new ITFEBizException("国库代码不能为空！");
			} else {
				// 国库代码
				incomedto.setStrecode(paramMap.get(JOSNUtils.TRECODE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.ACCTDATE))) {
				throw new ITFEBizException("报表日期不能为空！");
			} else {
				// 日期
				incomedto.setSrptdate(paramMap.get(JOSNUtils.ACCTDATE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.TRIMFLAG))) {
				throw new ITFEBizException("调整期标识不能为空！");
			} else {
				// 调整期标志
				incomedto.setStrimflag(paramMap.get(JOSNUtils.TRIMFLAG));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BUDGETTYPE))) {
				throw new ITFEBizException("预算种类不能为空！");
			} else {
				// 预算种类
				incomedto.setSbudgettype(paramMap.get(JOSNUtils.BUDGETTYPE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.TAXORGCODE))) {
				throw new ITFEBizException("征收机关代码不能为空！");
			} else {
				// 征收机关代码
				incomedto.setStaxorgcode(paramMap.get(JOSNUtils.TAXORGCODE));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BELONGFLAG))) {
				throw new ITFEBizException("辖属标识不能为空！");
			} else {
				// 辖属标志
				incomedto.setSbelongflag(paramMap.get(JOSNUtils.BELONGFLAG));
			}
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BELONGFLAG))) {
				throw new ITFEBizException("含款合计标识不能为空！");
			} else {
				// 是否含款合计
				incomedto.setSdividegroup(paramMap.get(JOSNUtils.BELONGFLAG));
			}
			List<TdEnumvalueDto> enumvalueList = null;
			if (StringUtils.isBlank(paramMap.get(JOSNUtils.BILLTYPE))) {
				throw new ITFEBizException("报表格式不能为空！");
			} else {
				TdEnumvalueDto tdEnumvalueDto = new TdEnumvalueDto();
				tdEnumvalueDto.setStypecode(StateConstant.BILL_TYPE);
				tdEnumvalueDto.setSvalue(paramMap.get(JOSNUtils.BILLTYPE));
				enumvalueList = CommonFacade.getODB().findRsByDto(
						tdEnumvalueDto);
				if (null == enumvalueList || enumvalueList.size() == 0) {
					throw new ITFEBizException("导出报表列表参数有误！");
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
			log.error("查询数据库失败！", e);
			throw new ITFEBizException(e);
		} catch (FileOperateException e) {
			log.error("读取文件失败！", e);
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			log.error("查询数据库失败！", e);
			throw new ITFEBizException(e);
		}
	}
	
	
	/**
	 * 报文信息处理
	 * 
	 * @param String
	 *            filepath 文件路径
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            orgcode 机构代码
	 * @param ISequenceHelperService
	 *            sequenceHelperService SEQ服务SERVER
	 * @return
	 * @throws ITFEBizException
	 */
	public FileResultDto dealFile(String filepath, String filename, String orgcode,
			ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		
		// 根据文件名称取得文件DTO对象 - 然后取业务类型
		/*FileObjDto fileobj = getFileObjByFileName(filename);
		String sbiztype = fileobj.getSbiztype();
		String csourceflag = fileobj.getCsourceflag();
		
		if(MsgConstant.MSG_SOURCE_PLACE.equals(csourceflag)){
			// 地税接口数据格式
			FileResultDto rtndto = new FileResultDto();
			rtndto.setCsourceflag(csourceflag);
			rtndto.setSbiztype(sbiztype);
			rtndto.setSmsgno(MsgConstant.MSG_NO_7211);
			return rtndto;
		}
		if(MsgConstant.MSG_SOURCE_NATION.equals(csourceflag)){
			// 国税接口数据格式
			return 	FileOperFacade.dealIncomeFileForNation(filepath, filename, orgcode);
		}
		if(MsgConstant.MSG_SOURCE_TIPS.equals(csourceflag)){
			// TIPS接口数据格式
			return  FileOperFacade.dealIncomeFileTips(filepath, filename, orgcode);
		}
		
		if(BizTypeConstant.BIZ_TYPE_INCOME.equals(sbiztype)){
			// 收入业务
			return 	FileOperFacade.dealIncomeFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(sbiztype)){
			// 支出业务
			return FileOperFacade.dealPayOutFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(sbiztype)){
			// 直接支付额度业务
			return FileOperFacade.dealDirectPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else if(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(sbiztype)){
			// 授权支付额度业务
			return FileOperFacade.dealGrantPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else{
			throw new ITFEBizException("业务类型[" + sbiztype + "]不对!");
		}*/
		return null;
	}
	
	
	/**
	 * 根据文件名称转化DTO
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename) throws ITFEBizException{
		return PublicSearchFacade.getFileObjByFileName(filename);
	}

	/**
	 * 对上传的收入文件(国税)处理，得到处理后的一个文件对象DTO(客户端调用)
	 * 
	 * @param String
	 *            filepath 文件路径
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            orgcode 机构代码
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
				tmpdto.setStaxticketno((String)change(rowObjects[j++]));//税票号码
				j++;//隶属关系名称
				tmpdto.setScorptypename((String)change(rowObjects[j++])); //登记注册类型名称
				tmpdto.setSbilldate((String)change(rowObjects[j++]));// 开票日期
				tmpdto.setStaxorgcode(splitString((String)change(rowObjects[j++]), "|", null));//征收机关代码
				tmpdto.setScorpcode((String)change(rowObjects[j++]));//纳税人识别号
				tmpdto.setScorpname((String)change(rowObjects[j++]));//纳税人名称
				tmpdto.setSpaybnkno(splitString((String)change(rowObjects[j++]), "|", null));//银行代码
				tmpdto.setSpayacct((String)change(rowObjects[j++]));//银行账号
				tmpdto.setSbudgetsubcode((String)change(rowObjects[j++]));//预算科目代码
				j++;//预算科目名称
				tmpdto.setSbudgetlevelcode(splitString((String)change(rowObjects[j++]), "|", null));//预算级次代码
				tmpdto.setSpayeetrecode(splitString((String)change(rowObjects[j++]), "(", ")"));//收款国库名称
				tmpdto.setStaxstartdate((String)change(rowObjects[j++]));// 所属时期-起
				tmpdto.setStaxenddate((String)change(rowObjects[j++]));// 所属时期-止
				tmpdto.setSlimitdate((String)change(rowObjects[j++]));// 税款限缴期限
				tmpdto.setStaxtypename((String)change(rowObjects[j++]));//征收品目名称
				j++;//课税数量
				j++;//销售收入 
				j++;//税率
				tmpdto.setNfacttaxamt(new BigDecimal((String)change(rowObjects[j++])));//实缴金额
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
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} catch (IOException e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} finally{
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("读取文件[" + filename + "]出现异常", e);
					throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
				}
			}
		}*/
		return null;
		
	}
}
