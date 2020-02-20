package com.cfcc.itfe.client.common.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpCountryDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;

public class FileOperFacade {
	
	private static Log logger = LogFactory.getLog(FileOperFacade.class);
	
	/**
	 * 根据文件名称转化DTO
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename) throws ITFEBizException{
		String tmpfilename_new = filename.trim().toLowerCase();
		String tmpfilename = tmpfilename_new.replace(".txt", "");
		if(tmpfilename.length()==13&&MsgConstant.TIME_FLAG_TRIM.equals(tmpfilename.substring(12, 13)))
		{
			ICommonDataAccessService cdas = (ICommonDataAccessService) ServiceFactory.getService(ICommonDataAccessService.class);
			Date adjustdate = cdas.getAdjustDate();
			String str = cdas.getSysDBDate();
			return PublicSearchFacade.getFileObjByFileName(filename,adjustdate,str);
		}else
		{
			return PublicSearchFacade.getFileObjByFileName(filename);
		}
	}
	/**
	 * 对上传的收入文件处理，得到处理后的一个文件对象DTO(客户端调用)
	 * 
	 * @param String
	 *            filepath 文件路径
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            orgcode 机构代码
	 * @param ISequenceHelperService
	 *            sequenceHelperService 取得包流水号服务
	 * @return
	 * @deprecated
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealIncomeFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		List<String> packList = new ArrayList<String>();
		int count = 1;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			String packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
			String tmpPackNo = packno + "000";
			packList.add(tmpPackNo);
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				
				if(count == (MsgConstant.TIPS_MAX_OF_PACK + 1) ){
					packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
					tmpPackNo = packno + "000";
					packList.add(tmpPackNo);
					count = 1;
				}

				sb_rtn.append(data);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(filename);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(orgcode);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(SequenceGenerator.changeTraSrlNo(packno, count-1));
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(tmpPackNo);
				sb_rtn.append("\r\n");
				
				recordNum ++ ;
				count ++ ;
			}
			br.close();
			
			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_1103);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setPacknos(packList);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(sb_rtn.toString());
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("读取文件[" + filename + "]出现异常", e);
					throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
				}
			}
		}
	}
	
	/**
	 * 对上传的收入文件处理，得到处理后的一个文件对象DTO(客户端调用)
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
	public static FileResultDto dealIncomeFile(String filepath, String filename,
			String orgcode)
			throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			FileResultDto fileRstDto = new FileResultDto();
			String data = null;
			int i = 0 ;
			String srlno = "";
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				int lineWithSplitLen = data.split(",").length;
				//判断如果是13位，则说明是带有资金收纳流水号的
				if(lineWithSplitLen == 13) {
					if(i == 0) {
						fileRstDto.setFileColumnLen(13);
						srlno = data.trim().substring(data.lastIndexOf(",")+1);
						//如果第一行的收纳号就为空则提示，并且如果不为18位也需进行提示
						if(null == srlno || "".equals(srlno)) {
							throw new ITFEBizException("文件中资金收纳流水号为空，请查证!");
						}else {
							int srlnoLen = srlno.length();
							if(srlnoLen != 18) {
								throw new ITFEBizException("文件中资金收纳流水号必须为18位，请查证!");
							}
						}
						fileRstDto.setStrasrlno(srlno);
					}
					data = data.substring(0, data.lastIndexOf(","));
					i++;
				}
				sb_rtn.append(data);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(filename);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(orgcode);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(String.valueOf(recordNum + 1));
				sb_rtn.append("\r\n");
				
				recordNum ++ ;

			}
			br.close();
			
			
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_7211);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(sb_rtn.toString());
			fileRstDto.setCsourceflag(MsgConstant.MSG_SOURCE_TBS);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("读取文件[" + filename + "]出现异常", e);
					throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
				}
			}
		}

	}

	/**
	 * 对上传的收入文件处理，得到处理后的一个文件对象DTO(客户端调用)
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
	public static FileResultDto dealIncomeFileTips(String filepath, String filename,
			String orgcode)
			throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}

				sb_rtn.append(data);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(filename);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(orgcode);
				sb_rtn.append("\r\n");
				
				recordNum ++ ;

			}
			br.close();
			
			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_7211);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(sb_rtn.toString());
			fileRstDto.setCsourceflag(MsgConstant.MSG_SOURCE_TIPS);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("读取文件[" + filename + "]出现异常", e);
					throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
				}
			}
		}

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
		
		InputStream inputStream = null ;
	
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
		}
		
		
	}
	
	
	public static FileResultDto dealIncomeFileForITFE(String filepath, String filename,String orgcode) throws ITFEBizException{
		return null;
	}
	
	/**
	 * 对上传的直接支付额度文件处理，得到处理后的一个文件对象DTO(客户端调用)
	 * 
	 * @param String
	 *            filepath 文件路径
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            orgcode 机构代码
	 * @param ISequenceHelperService
	 *            sequenceHelperService 取得包流水号服务
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealDirectPayFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		BufferedReader br = null;
		List<String> packList = new ArrayList<String>();

		HashMap<String, Object> map = new HashMap<String, Object>(); // 存放报文头关键字段值
		boolean headflag = false; // 报文头标志
		boolean mainflag = false; // 报文主体信息标志
		boolean detailflag = false; // 报文明细信息标志
		StringBuffer mainbuf = new StringBuffer(); // 存放主体信息字段
		StringBuffer detailbuf = new StringBuffer(); // 存放子信息字段
		int maincount = 1 ; // 主体信息分包记数器
		int recordNum = 0; // 主体信息记录总条数
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			String packno = SequenceGenerator
					.changePackNo(sequenceHelperService
							.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
			String tmpPackNo = packno + "000";
			packList.add(tmpPackNo);
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				
				String tmpdata = data.toLowerCase();
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_START) >= 0){
					if(mainflag || detailflag){
						logger.error("文件格式错误! [文件头位置不正确]");
						throw new ITFEBizException("文件格式错误! [文件头位置不正确]");
					}
					headflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_END) >= 0){
					headflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_START) >= 0){
					if(headflag || detailflag){
						logger.error("文件格式错误! [文件头没有结束标志]");
						throw new ITFEBizException("文件格式错误! [文件头没有结束标志]");
					}
					mainflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_END) >= 0){
					mainflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_START) >= 0){
					if(headflag || mainflag){
						logger.error("文件格式错误! [文件头或文件主体没有结束标志]");
						throw new ITFEBizException("文件格式错误! [文件头或文件主体没有结束标志]");
					}
					detailflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_END) >= 0){
					detailflag = false;
				}
				
				if(headflag){
					// 在读取文件头信息
					parserMsgHead(data, map);
				}
				
				if(mainflag){
					// 在读取文件主体信息
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String maininfo = parserMsgBody(data);
						maininfo = maininfo.replace(StateConstant.INCOME_SPLIT, "");
						maininfo = maininfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);
						
						if(maincount == ( MsgConstant.TIPS_MAX_OF_PACK + 1) ){
							packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
							tmpPackNo = packno + "000";
							packList.add(tmpPackNo);
							maincount = 1;
						}

						mainbuf.append(maininfo);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(filename);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(orgcode);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(SequenceGenerator.changeTraSrlNo(packno, maincount-1));
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(tmpPackNo);
						mainbuf.append("\r\n");
						
						recordNum ++ ;
						maincount ++ ;
					}
				}
				
				if(detailflag){
					// 在读取文件明细信息
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String detailinfo = parserMsgBody(data);
						detailinfo = detailinfo.replace(StateConstant.INCOME_SPLIT, "");
						detailinfo = detailinfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);

						detailbuf.append(detailinfo);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(filename);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(orgcode);
						detailbuf.append("\r\n");
					}
				}
			}
			
			br.close();

			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_5102);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setPacknos(packList);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(mainbuf.toString());
			fileRstDto.setSdetailinfo(detailbuf.toString());
			fileRstDto.setMap(map);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("读取文件[" + filename + "]出现异常", e);
					throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
				}
			}
		}
	}
	
	/**
	 * 对上传的授权支付额度文件处理，得到处理后的一个文件对象DTO(客户端调用)
	 * 
	 * @param String
	 *            filepath 文件路径
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            orgcode 机构代码
	 * @param ISequenceHelperService
	 *            sequenceHelperService 取得包流水号服务
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealGrantPayFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		BufferedReader br = null;
		List<String> packList = new ArrayList<String>();

		HashMap<String, Object> map = new HashMap<String, Object>(); // 存放报文头关键字段值
		boolean headflag = false; // 报文头标志
		boolean mainflag = false; // 报文主体信息标志
		boolean detailflag = false; // 报文明细信息标志
		StringBuffer mainbuf = new StringBuffer(); // 存放主体信息字段
		StringBuffer detailbuf = new StringBuffer(); // 存放子信息字段
		int subcount = 1 ; // 明细信息分包记数器
		int recordNum = 0; // 明细信息记录总条数
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			
			String packno = SequenceGenerator
					.changePackNo(sequenceHelperService
							.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
			String tmpPackNo = packno + "000";
			packList.add(tmpPackNo);
			
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				
				String tmpdata = data.toLowerCase();
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_START) >= 0){
					if(mainflag || detailflag){
						logger.error("文件格式错误! [文件头位置不正确]");
						throw new ITFEBizException("文件格式错误! [文件头位置不正确]");
					}
					headflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_END) >= 0){
					headflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_START) >= 0){
					if(headflag || detailflag){
						logger.error("文件格式错误! [文件头没有结束标志]");
						throw new ITFEBizException("文件格式错误! [文件头没有结束标志]");
					}
					mainflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_END) >= 0){
					mainflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_START) >= 0){
					if(headflag || mainflag){
						logger.error("文件格式错误! [文件头或文件主体没有结束标志]");
						throw new ITFEBizException("文件格式错误! [文件头或文件主体没有结束标志]");
					}
					detailflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_END) >= 0){
					detailflag = false;
				}
				
				if(headflag){
					// 在读取文件头信息
					parserMsgHead(data, map);
				}
				
				if(mainflag){
					// 在读取文件主体信息
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String maininfo = parserMsgBody(data);
						maininfo = maininfo.replace(StateConstant.INCOME_SPLIT, "");
						maininfo = maininfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);
				
						mainbuf.append(maininfo);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(filename);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(orgcode);
						mainbuf.append("\r\n");
					}
				}
				
				if(detailflag){
					// 在读取文件明细信息
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String detailinfo = parserMsgBody(data);
						detailinfo = detailinfo.replace(StateConstant.INCOME_SPLIT, "");
						detailinfo = detailinfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);

						if(subcount == ( MsgConstant.TIPS_MAX_OF_PACK + 1) ){
							packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
							tmpPackNo = packno + "000";
							packList.add(tmpPackNo);
							subcount = 1;
						}
						
						detailbuf.append(detailinfo);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(tmpPackNo);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(SequenceGenerator.changeTraSrlNo(packno, subcount-1));
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(filename);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(orgcode);
						detailbuf.append("\r\n");

						recordNum ++ ;
						subcount ++ ;
					}
				}
			}
			
			br.close();

			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_5103);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setPacknos(packList);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(mainbuf.toString());
			fileRstDto.setSdetailinfo(detailbuf.toString());
			fileRstDto.setMap(map);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("读取文件[" + filename + "]出现异常", e);
					throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
				}
			}
		}
	}
	
	/**
	 * 对上传的实拨资金业务文件处理，得到处理后的一个文件对象DTO(客户端调用)
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
	public static FileResultDto dealPayOutFile(String filepath, String filename,
			String orgcode) throws ITFEBizException{
		
		String message = null;
		try {
			message = readFile(filepath);
		} catch (FileNotFoundException e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		} catch (IOException e) {
			logger.error("读取文件[" + filename + "]出现异常", e);
			throw new ITFEBizException("读取文件[" + filename + "]出现异常", e);
		}
		
		FileResultDto fileRstDto = new FileResultDto();
		fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT);
		fileRstDto.setSmsgno(MsgConstant.MSG_NO_5101);
		fileRstDto.setSfilename(filename.toLowerCase());
		fileRstDto.setSmaininfo(message);
		
		return fileRstDto;
	}
	
	
	/**
	 * 解析文件体内容
	 * 
	 * @param String
	 *            values 文件体信息
	 * @return
	 */
	public static String parserMsgBody(String values) throws ITFEBizException {
		String value = values.toLowerCase();

		if (value.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >= 0
				&& value.indexOf(MsgConstant.MSG_FLAG_CONTENT_END) >= 0) {
			return value.substring(value
					.indexOf(MsgConstant.MSG_FLAG_CONTENT_START)
					+ MsgConstant.MSG_FLAG_CONTENT_START.length(), value
					.indexOf(MsgConstant.MSG_FLAG_CONTENT_END));
		}

		throw new ITFEBizException("文件体[" + value +  "]没有结尾标志符!");
	}
	
	/**
	 * 解析报文头信息
	 * 
	 * @param String
	 *            values 解析字段
	 * @param HashMap
	 *            <String, Object> map 存储关键字段
	 */
	public static void parserMsgHead(String values, HashMap<String, Object> map) {

		/**
		 * 文件头格式
		 * <pub>
		 * <code>301</code>
		 * <msgnumber>0101</msgnumber>
		 * <czjname>zjsczt</czjname>  <重要字段>
		 * <sender>01</sender>
		 * <receiver>0300</receiver>  <重要字段>
		 * <msgstatus>0</msgstatus>
		 * </pub>
		 */

		String value = values.toLowerCase();
		
		if (value.indexOf(MsgConstant.MSG_FLAG_CZJNAME_START) >= 0
				&& value.indexOf(MsgConstant.MSG_FLAG_CZJNAME_END) >= 0) {
			String czjname = value.substring(value
					.indexOf(MsgConstant.MSG_FLAG_CZJNAME_START)
					+ MsgConstant.MSG_FLAG_CZJNAME_START.length(), value
					.indexOf(MsgConstant.MSG_FLAG_CZJNAME_END));
			
			map.put(MsgConstant.MSG_FLAG_CZJNAME, czjname);
		}
		
		if (value.indexOf(MsgConstant.MSG_FLAG_RECEIVER_START) >= 0
				&& value.indexOf(MsgConstant.MSG_FLAG_RECEIVER_END) >= 0) {
			String czjname = value.substring(value
					.indexOf(MsgConstant.MSG_FLAG_RECEIVER_START)
					+ MsgConstant.MSG_FLAG_RECEIVER_START.length(), value
					.indexOf(MsgConstant.MSG_FLAG_RECEIVER_END));
			
			map.put(MsgConstant.MSG_FLAG_RECEIVER, czjname);
		}
	}
	
	/**
	 * 将InputStream中的内容读取到字符串中
	 * @param in 读入流
	 * @return 记录流中内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer();
		if (in == null) {
			// 如果流为空，那么返回空字符串
			return content.toString();
		}
		char[] buf = new char[4096];
		int ret = 0;
		BufferedReader bin = new BufferedReader(new InputStreamReader(in,"gb2312"));
		ret = bin.read(buf);
		while (ret > 0) {
			content.append(new String(buf, 0, ret));
			ret = bin.read(buf);
		}
		bin.close();
		return content.toString();
	}

	/**
	 * 将文件中的内容读取到字符串中
	 * @param file 文件对象
	 * @return 保存文件内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(File file) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(file));
	}
	
	/**
	 * 将文件中的内容读取到字符串中
	 * @param fileName 文件的绝对路径
	 * @return 保存文件内容的字符串
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(fileName));
	}

	/**
	 * 将字符串中的内容，写入指定文件中
	 * 如果指定文件名的文件已经存在，那么会被覆盖
	 * @param fileName 要写入的文件名
	 * @param content  文件内容
	 * @return
	 */
	public static void writeFile(String fileName, String content) throws FileNotFoundException,IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
		out.write(content.toCharArray());
		out.flush();
		out.close();
	}
	
	/**
	 * 将字符串开头位置的ASCII码小于等于30（空格）的字符去掉
	 * 尤其是回车换行等字符
	 * @param src 源字符串
	 * @return 开头不含回车、换行、空格等不可见字符的字符串
	 */
	public static String ltrim(String src){
		int i=0;
		for (i=0; i<src.length(); i++){
			if (src.charAt(i) > 32){
				break;
			}
		}
		return src.substring(i);
	}
	
	/**
	 * 将字符串结尾的ASCII码小于等于30（空格）的字符去掉
	 * 尤其是回车换行等字符
	 * @param src 源字符串
	 * @return 结尾不含回车、换行、空格等不可见字符的字符串
	 */
	public static String rtrim(String src){
		int i=src.length() - 1;
		for (; i>=0; i--){
			if (src.charAt(i) > 32){
				break;
			}
		}
		return src.substring(0,i + 1);
	}
	
	/**
	 * 将字符串开头和结尾的ASCII码小于等于30（空格）的字符去掉
	 * 尤其是回车换行等字符
	 * @param src 源字符串
	 * @return 开头和结尾不含回车、换行、空格等不可见字符的字符串
	 */
	public static String trim(String src){
		return ltrim(rtrim(src));
	}
	
	/**
	 * 判断传入的路径是否存在，如果不存在那么创建目录，否则不做任何操作
	 * @param dir 要检查的目录
	 * @return true-目录已经存在，false-目录创建失败
	 */
	public static boolean createDir(String dir){
		File file = new File(dir);
		if (!file.exists()){
			file.mkdirs();
		}
		return true;
	}
	
	/**
	 * 根据传入的文件相对路径或者绝对路径，获得文件名
	 * @param file 文件相对路径或者绝对路径
	 * @return 文件名
	 */
	public static String getFileName(String file){
		int pos = file.lastIndexOf('/');
		if (pos == -1){
			pos = file.lastIndexOf('\\');
		}
		return file.substring(pos + 1);
	}
	
	/**
	 * 删除指定的文件
	 * @param filePath 要删除的文件的绝对路径
	 */
	public static void deleteFile(String filePath){
		File file = new File(filePath);
		file.deleteOnExit();
	}

	/**
	 * 简单文件转化
	 * @param obj
	 * @return
	 */
	public static Object change(Object obj){
		if(null == obj){
			return "";
		}else{
			if(obj instanceof String){
				return ((String) obj).trim();
			}else if(obj instanceof Integer){
				return obj.toString();
			}else if(obj instanceof Double){
				return obj.toString();
			}else if(obj instanceof java.util.Date){
				long ltime = ((java.util.Date)(obj)).getTime();
				return DateUtil.date2String2(new java.sql.Date(ltime));
			}
			
			return obj;
		}
	}
	
	public static String splitString(String obj, String split1, String split2) throws ITFEBizException {
		if ("|".equals(split1)) {
			int iPos = obj.indexOf("|");
			if (iPos >= 0) {
				return obj.substring(iPos + 1);// 征收机关代码
			} else {
				throw new ITFEBizException("数据[" + obj + "]不符合规范！");
			}
		} else if (("(".equals(split1) || "（".equals(split1)) && (")".equals(split2) || "）".equals(split2))) {
			int iPos = obj.indexOf("(");
			if (iPos < 0) {
				iPos = obj.indexOf("（");
			}
			if (iPos >= 0) {
				return obj.substring(iPos + 1, obj.length() - 1);
			}else{
				throw new ITFEBizException("数据[" + obj + "]不符合规范！");
			}
		} else {
			throw new ITFEBizException("数据[" + obj + "]不符合规范！");
		}
	}
}
