package com.cfcc.itfe.webservice.guangdong;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.encrypt.Base64;
import com.cfcc.itfe.webservice.guangdong.income.FileOperFacade;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 用于生成webService返回值<br>
 * <?xml version="1.0" encoding="GBK"?><br>
 * <root><br>
 * <head><br>
 * <result>0</result><br>
 * <status></status><br>
 * <message>文件不存在</message><br>
 * </head><br>
 * <body><br>
 * <report>第一个数据</report><br>
 * </body><br>
 * </root><br>
 * 
 * @author hua
 * 
 */
public class WsUtils {
	private static Log log = LogFactory.getLog(WsUtils.class);

	/**
	 * 生成返回数据(由于结构简单,就直接采取字符串拼接,而没有使用dom操作)
	 * 
	 * @param result
	 *            0-失败 1-成功
	 * @param message
	 *            需要返回的详细信息
	 * @return
	 */
	public static String generateResultXml(String result, String message) {
		return generateResultXml(result, "", message, "");
	}

	/**
	 * 生成返回数据(由于结构简单,就直接采取字符串拼接,而没有使用dom操作)
	 * 
	 * @param result
	 *            0-失败 1-成功
	 * @param message
	 *            需要返回的详细信息
	 * @param report
	 *            报表数据
	 * @return
	 */
	public static String generateResultXml(String result, String message, String report) {
		return generateResultXml(result, "", message, report);
	}

	/**
	 * 生成返回数据(由于结构简单,就直接采取字符串拼接,而没有使用dom操作)
	 * 
	 * @param result
	 *            0-失败 1-成功
	 * @param status
	 *            文件处理状态 0-未销号 1-已销号 2-已提交 3-处理中 4-处理成功 5-处理失败
	 * @param message
	 *            需要返回的详细信息
	 * @param report
	 *            报表数据
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	public static String generateResultXml(String result, String status, String message, String report) {
		StringBuffer retXml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		retXml.append("<root>");
		retXml.append("<head>");
		retXml.append("<result>" + handleNull(result) + "</result>");
		retXml.append("<status>" + handleNull(status) + "</status>");
		retXml.append("<message>" + handleNull(message) + "</message>");
		retXml.append("</head>");
		retXml.append("<body>");
		retXml.append("<report>");
		try {
			retXml.append(StringUtils.isBlank(handleNull(report)) ? handleNull(report) : base64Encode(handleNull(report)));
		} catch (Exception e) {
			log.error("返回WebService数据出现异常(GD)", e);
		}
		retXml.append("</report>");
		retXml.append("</body>");
		retXml.append("</root>");
		String ls_Return = null;
		try {
			//将返回的String转换编码为GBK
			ls_Return = new String(retXml.toString().getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls_Return;
	}
	
	/**
	 * 生成返回数据(由于结构简单,就直接采取字符串拼接,而没有使用dom操作)
	 * 
	 * @param result
	 *            0-失败 1-成功
	 * @param status
	 *            文件处理状态 0-未销号 1-已销号 2-已提交 3-处理中 4-处理成功 5-处理失败
	 * @param message
	 *            需要返回的详细信息
	 * @param reports
	 *            报表数据
	 *            多条
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	public static String generateResultXml(String result, String status, String message, List<String> reports) {
		StringBuffer retXml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		retXml.append("<root>");
		retXml.append("<head>");
		retXml.append("<result>" + handleNull(result) + "</result>");
		retXml.append("<status>" + handleNull(status) + "</status>");
		retXml.append("<message>" + handleNull(message) + "</message>");
		retXml.append("</head>");
		retXml.append("<body>");
		for(String report:reports){
			retXml.append("<report>");
			try {
				retXml.append(StringUtils.isBlank(handleNull(report)) ? handleNull(report) : base64Encode(handleNull(report)));
			} catch (Exception e) {
				log.error("返回WebService数据出现异常(GD)", e);
			}
			retXml.append("</report>");
		}
		retXml.append("</body>");
		retXml.append("</root>");
		String ls_Return = null;
		try {
			//将返回的String转换编码为GBK
			ls_Return = new String(retXml.toString().getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls_Return;
	}

	/**
	 * 处理为Null的情况
	 * 
	 * @param value
	 * @return
	 */
	public static String handleNull(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		return value;
	}

	/**
	 * 对字符串进行base64编码
	 * 
	 * @param src字符串
	 * @return base64字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Encode(String src) throws UnsupportedEncodingException, Exception {
		byte[] outByte = Base64.encode(src.getBytes());
		String ret = null;
		ret = new String(outByte, "GBK");
		return ret;
	}

	/**
	 * 由base64编码的字符串，生成源字符串 gbk
	 * 
	 * @param src
	 *            base64编码的字符串
	 * @return 源字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Decode(String src) throws UnsupportedEncodingException, Exception {
		byte[] out = Base64.decode(src);

		String ret = null;
		ret = new String(out, "GBK");
		return ret;
	}

	/**
	 * 校验传入的文件名是否符合规范
	 * 
	 * @param biztype
	 *            业务类型
	 * @param fileName
	 *            文件名
	 * @return true 符合 false 不符合
	 */
	public static boolean checkFileName(String biztype, String srcFileName) {
		if (StringUtils.isBlank(biztype)) {
			return Boolean.FALSE;
		}
		if (StringUtils.isBlank(srcFileName)) {
			return Boolean.FALSE;
		}
		String newFileName = srcFileName.toLowerCase();
		if (StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN)// 直接额度
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN)// 授权额度
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_INCOME)// 电子税票
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_RET_TREASURY)// 退库
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAY_OUT)// 一般预算支出
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAY_OUT2)// 调拨支出
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)// 直接划款
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)// 直接退款
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)// 授权划款
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) { // 授权退款
			// 规则：8位日期+2位或4位批次号+两位业务类型+调整期标志
			String regular = "^20[0-9]{6}(\\w{2}||\\w{4})[0-9]{2}[01]{1}.txt$";
			boolean match = newFileName.matches(regular);
			if (match) {
				String fileNameBizType = newFileName.substring(newFileName.length() - 7, newFileName.lastIndexOf(".") - 1);
				if (StringUtils.isBlank(verifyBizType(fileNameBizType)) || !StringUtils.equals(fileNameBizType, biztype)) {
					return Boolean.FALSE;
				}
			} else {
				return Boolean.FALSE;
			}

		} else {
			// 其他业务暂不校验
		}

		return Boolean.TRUE;
	}

	/**
	 * 根据业务类型biztype，返回报文编号
	 */
	public static String verifyBizType(String biztype) {
		String ls_Return = "";
		Map<String, String> bizTypeMap = new HashMap<String, String>();
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_INCOME, "电子税票");
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAY_OUT, MsgConstant.SHIBO_SHANDONG_DAORU);// 实拨资金
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAY_OUT2, MsgConstant.SHIBO_SHANDONG_DAORU);// 实拨资金(调拨支出)
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN, MsgConstant.ZHIJIE_DAORU);// 直接支付额度
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, MsgConstant.SHOUQUAN_DAORU);// 授权支付额度
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_RET_TREASURY, MsgConstant.TUIKU_SHANDONG_DAORU);// 退库（广东采取山东接口）
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_CORRECT, MsgConstant.GENGZHENG_DAORU);// 更正
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY, MsgConstant.APPLYPAY_DAORU);// 集中支付清算，直接支付划款申请
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY, MsgConstant.APPLYPAY_DAORU);// 集中支付清算
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK, MsgConstant.APPLYPAY_BACK_DAORU);// 集中支付退款清算
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK, MsgConstant.APPLYPAY_BACK_DAORU);// 集中支付退款清算
		if (bizTypeMap.containsKey(biztype))
			ls_Return = bizTypeMap.get(biztype);
		return ls_Return;
	}

	/**
	 * 将文件解析返回的错误信息列表拼接成字符串
	 * 
	 * @param errorList
	 * @return
	 */
	public static String packErrorInfo(List<String> errorList) {
		StringBuffer sb = new StringBuffer("");
		if (errorList != null && errorList.size() > 0) {
			for (String error : errorList) {
				sb.append(error + "\r\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 校验是否重复导入(从VerfyFileName.java移植过来, 主要是添加国库代码条件, 这样就可以按照国库代码力度判重)
	 * 
	 * @param sorgcode
	 * @param sfilename
	 * @throws ITFEBizException
	 */
	public static boolean verifyImportRepeat(String sorgcode, String trecode, String sfilename) throws ITFEBizException {
		TvFilepackagerefDto finddto = new TvFilepackagerefDto();
		finddto.setSorgcode(sorgcode);
		finddto.setStrecode(trecode);
		finddto.setSfilename(sfilename);
		try {
			List list = CommonFacade.getODB().findRsByDto(finddto);
			if (null == list || list.size() == 0) {
				return false;
			}
			return true;
		} catch (JAFDatabaseException e) {
			log.error("校验导入文件的重复性时出现数据库异常!", e);
			throw new ITFEBizException("校验导入文件的重复性时出现数据库异常!", e);
		} catch (ValidateException e) {
			log.error("校验导入文件的重复性时出现数据库异常!", e);
			throw new ITFEBizException("校验导入文件的重复性时出现数据库异常!", e);
		}
	}

	/**
	 * 删除错误文件
	 * 
	 * @param filelist
	 * @throws ITFEBizException
	 */
	public static void delServerWrongFile(List<String> filelist) throws ITFEBizException {
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		// 文件上传根路径
		String root = sysconfig.getRoot();
		try {
			for (int i = 0; i < filelist.size(); i++) {
				File tmpfile = new File(root + filelist.get(i));
				if (tmpfile != null && tmpfile.isFile() && tmpfile.exists()) {
					tmpfile.getAbsoluteFile().delete();
					if (((String) filelist.get(i)).endsWith(".pas")) {
						File pasfile = new File(root + ((String) filelist.get(i)).replaceAll(".pas", ".tmp"));
						if (pasfile != null && pasfile.isFile() && pasfile.exists())
							pasfile.getAbsoluteFile().delete();

					} else {
						File pasfile = new File(root + ((String) filelist.get(i)).replaceAll(".txt", ".tmp"));
						if (pasfile != null && pasfile.isFile() && pasfile.exists())
							pasfile.getAbsoluteFile().delete();
					}
				}
			}
		} catch (Exception e) {
			log.error("删除服务器文件失败！", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 校验退款补录信息
	 * 
	 * @param paramStr
	 * @return
	 */
	public static String checkPayreckBackParam(String[] tempParams) {
		String famt = tempParams[0]; // 来帐资金总额
		String paydictno = tempParams[1]; // 支付交易序号
		String payentrustdate = tempParams[2]; // 支付委托日期
		String paymsgno = tempParams[3]; // 支付报文编号
		String paysndbnkno = tempParams[4]; // 支付发起行行号
		String errorInfoHead = "集中支付退款清算业务参数信息中";
		if (StringUtils.isBlank(famt)) {
			return errorInfoHead + "[来帐资金总额]不能为空!";
		}
		if (StringUtils.isBlank(paydictno)) {
			return errorInfoHead + "[支付交易序号]不能为空!";
		} else {
			if (!paydictno.matches("\\d{8}")) {
				return errorInfoHead + "[支付交易序号]格式非法, 应为8位数字!";
			}
		}
		if (StringUtils.isBlank(payentrustdate)) {
			return errorInfoHead + "[支付委托日期]不能为空!";
		} else {
			if (!payentrustdate.matches("20\\d{6}")) {
				return errorInfoHead + "[支付委托日期]格式非法, 格式应为[YYYYMMDD]!";
			}
		}
		if (StringUtils.isBlank(paymsgno)) {
			return errorInfoHead + "[支付报文编号]不能为空!";
		}
		if (StringUtils.isBlank(paysndbnkno)) {
			return errorInfoHead + "[支付发起行行号]不能为空!";
		} else {
			if (!paysndbnkno.matches("\\d{12}")) {
				return errorInfoHead + "[支付发起行行号]格式非法, 应为12位数字!";
			}
		}
		return null;
	}

	/**
	 * 移植客户端收入导入代码
	 * 
	 * @param filename
	 *            文件名
	 * @param filepath
	 *            文件路径
	 * @param orgcode
	 *            机构代码
	 * @param trasrlno
	 *            资金收纳流水号
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto processIncome(String filename, String filepath, String orgcode, String trasrlno) throws ITFEBizException {
		FileResultDto fileResultDto = FileOperFacade.dealIncomeFile(filepath, filename, orgcode);

		if (fileResultDto.getFileColumnLen() != 13) {
			// 收入业务，要校验资金收纳流水号是否填写
			if (null == trasrlno || "".equals(trasrlno.trim()) || trasrlno.trim().length() != 18) {
				// MessageDialog.openMessageDialog(null, "收入业务：资金收纳流水号必须填写，且必须为18位！");
				trasrlno = "111111111111111111"; // 默认资金收纳流水号为18个1
			}
			fileResultDto.setIsError(false);
			fileResultDto.setStrasrlno(trasrlno.trim());
		} else {
			fileResultDto.setIsError(false);
		}
		return fileResultDto;
	}
}
