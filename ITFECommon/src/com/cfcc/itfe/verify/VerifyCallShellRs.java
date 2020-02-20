package com.cfcc.itfe.verify;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * 主要功能:校验调用SHELL处理是否成功 
 * @author zhouchuan
 *
 */
public class VerifyCallShellRs {

	/**
	 * 校验调用SHELL后的处理结果(税票收入)
	 * 
	 * @param String
	 *            results 调用SHELL的返回结果
	 */
	public static void verifyShellForIncomeRs(String results) throws ITFEBizException{
		// 指定提示错误记录条数  
		boolean rsFlag = true;
		int count = 0;
		StringBuffer errbuf = new StringBuffer(); // 总的错误描述
		StringBuffer buf3125 = new StringBuffer(); // 截断错误
		StringBuffer buf3124 = new StringBuffer(); // 金额类型转化错误
		StringBuffer buf3137 = new StringBuffer(); // 行数据缺少错误
		StringBuffer resultbuf = new StringBuffer(); // 执行导入的结果(暂时查看拒绝的行数)
		
		String[] strs = results.split(StateConstant.SPACE_SPLIT);
		for(int i = 0 ; i < strs.length ; i++){
			if(null != strs[i]){
				if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3125W) >= 0){
					/**
					 * 表示有数据被截断
					 * 例如:SQL3125W  在行 "1" 列 "3" 中的字符数据被截断，因为该数据比目标数据库列更长。
					 */
					count ++;
					buf3125 = buf3125.append(getErrInfo(DealCodeConstants.DB2_SQL3125W, strs[i]));
				}else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3124W) >= 0){
					/**
					 * 表示有数据转化成金额字段失败 
					 * 例如:SQL3124W  在行 "1" 列 "12" 中的字段值不能转换为 PACKED DECIMAL
					 */
					count ++;
					buf3124 = buf3124.append(getErrInfo(DealCodeConstants.DB2_SQL3124W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3137W) >= 0){
					/**
					 * 表示有数据行太短,缺少字段
					 * 例如:SQL3137W  行 "1" 太短。至少丢失了一个已装入非可空列的输入值。未装入该行。
					 */
					count++;
					buf3137 = buf3137.append(getErrInfo(DealCodeConstants.DB2_SQL3137W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_REJECT_LINE) >= 0
						||( strs[i].toLowerCase().indexOf(DealCodeConstants.DB2_REJECT_LINE_ENGLISH) >= 0 &&
								strs[i].indexOf("=") >= 0)){
					/**
					 * 表示有拒绝的行数
					 */
					resultbuf = resultbuf.append(getErrInfo(DealCodeConstants.DB2_REJECT_LINE, strs[i]));
					break;
				}
				
				if(count >= MsgConstant.DB_IMPORT_ERROR_NUM){
					break;
				}
			}
		}
		
		if(resultbuf.length() > 1){
			// 表示执行结果中有拒绝记录
			rsFlag = false;
			errbuf = errbuf.append("执行结果如下:\r\n\t" + resultbuf.toString() + "\r\n");
		}
		if(buf3125.length() > 1){
			// 表示有截断的数据
			rsFlag = false;
			errbuf = errbuf.append("如下记录被截断:\r\n\t" + buf3125 + "\r\n");
		}
		if(buf3124.length() > 1){
			// 表示有金额类型转化错误
			rsFlag = false;
			errbuf = errbuf.append("如下记录金额类型转化错误:\r\n\t" + buf3124 + "\r\n");
		}
		if(buf3137.length() > 1){
			// 表示有记录缺少数据字段
			rsFlag = false;
			errbuf = errbuf.append("如下记录缺少数据字段:\r\n\t" + buf3137 + "\r\n");
		}
		
		
		if(!rsFlag){
			throw new ITFEBizException(errbuf.toString());
		}
	}
	
	/**
	 * 校验调用SHELL后的处理结果(支付业务-直接支付和授权支付)
	 * 
	 * @param String
	 *            results 调用SHELL的返回结果
	 */
	public static void verifyShellForPayoutRs(String results) throws ITFEBizException{
		// 指定提示错误记录条数  
		boolean rsFlag = true;
		boolean mainFlag = true;
		int count = 0;
		StringBuffer errbuf = new StringBuffer(); // 总的错误描述
		StringBuffer buf3125 = new StringBuffer(); // 截断错误
		StringBuffer buf3124 = new StringBuffer(); // 金额类型转化错误
		StringBuffer buf3137 = new StringBuffer(); // 行数据缺少错误
		StringBuffer buf3116 = new StringBuffer(); // 数据字段不能为空
		StringBuffer resultbuf = new StringBuffer(); // 执行导入的结果(暂时查看拒绝的行数)
		
		String[] strs = results.split(StateConstant.SPACE_SPLIT);
		for(int i = 0 ; i < strs.length ; i++){
			if(null != strs[i]){
				if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3125W) >= 0){
					/**
					 * 表示有数据被截断
					 * 例如:SQL3125W  在行 "1" 列 "3" 中的字符数据被截断，因为该数据比目标数据库列更长。
					 */
					count ++;
					buf3125 = buf3125.append(getErrInfo(DealCodeConstants.DB2_SQL3125W, strs[i]));
				}else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3124W) >= 0){
					/**
					 * 表示有数据转化成金额字段失败 
					 * 例如:SQL3124W  在行 "1" 列 "12" 中的字段值不能转换为 PACKED DECIMAL
					 */
					count ++;
					buf3124 = buf3124.append(getErrInfo(DealCodeConstants.DB2_SQL3124W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3137W) >= 0){
					/**
					 * 表示有数据行太短,缺少字段
					 * 例如:SQL3137W  行 "1" 太短。至少丢失了一个已装入非可空列的输入值。未装入该行。
					 */
					count++;
					buf3137 = buf3137.append(getErrInfo(DealCodeConstants.DB2_SQL3137W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf("SQL3116W") >= 0){
					/**
					 * 表示有数据字段不能为空
					 * 例如:SQL3116W  丢失了行 "3" 列 "8" 中的字段值，但是目标列不可为空。
					 */
					count++;
					buf3116 = buf3116.append(getErrInfo("SQL3116W", strs[i]));
				} 
				
				else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_REJECT_LINE) >= 0
						|| (strs[i].toLowerCase().indexOf(DealCodeConstants.DB2_REJECT_LINE_ENGLISH) >= 0 &&
								strs[i].indexOf("=") >= 0)){
					/**
					 * 表示有拒绝的行数
					 */
					resultbuf = resultbuf.append(getErrInfo(DealCodeConstants.DB2_REJECT_LINE, strs[i]));
					if(mainFlag){
						// 如果操作的是主表的记录
						if(resultbuf.length() > 1){
							// 表示执行结果中有拒绝记录
							rsFlag = false;
							errbuf = errbuf.append("主表执行结果如下:\r\n\t" + resultbuf.toString() + "\r\n");
						}
						if(buf3125.length() > 1){
							// 表示有截断的数据
							rsFlag = false;
							errbuf = errbuf.append("主表如下记录被截断:\r\n\t" + buf3125 + "\r\n");
						}
						if(buf3124.length() > 1){
							// 表示有金额类型转化错误
							rsFlag = false;
							errbuf = errbuf.append("主表如下记录金额类型转化错误:\r\n\t" + buf3124 + "\r\n");
						}
						if(buf3137.length() > 1){
							// 表示有记录缺少数据字段
							rsFlag = false;
							errbuf = errbuf.append("主表如下记录缺少数据字段:\r\n\t" + buf3137 + "\r\n");
						}
						if(buf3116.length() > 1){
							// 表示有记录缺少数据字段
							rsFlag = false;
							errbuf = errbuf.append("主表如下记录数据字段不能为空:\r\n\t" + buf3116 + "\r\n");
						}
						
						
						if(!rsFlag){
							errbuf = errbuf.append("\r\n\r\n");
						}
						
						mainFlag = false;
					}
				}
				
				if(count >= MsgConstant.DB_IMPORT_ERROR_NUM){
					break;
				}
			}
		}
		
		if(resultbuf.length() > 1){
			// 表示执行结果中有拒绝记录
			rsFlag = false;
			errbuf = errbuf.append("子表执行结果如下:\r\n\t" + resultbuf.toString() + "\r\n");
		}
		if(buf3125.length() > 1){
			// 表示有截断的数据
			rsFlag = false;
			errbuf = errbuf.append("子表如下记录被截断:\r\n\t" + buf3125 + "\r\n");
		}
		if(buf3124.length() > 1){
			// 表示有金额类型转化错误
			rsFlag = false;
			errbuf = errbuf.append("子表如下记录金额类型转化错误:\r\n\t" + buf3124 + "\r\n");
		}
		if(buf3137.length() > 1){
			// 表示有记录缺少数据字段
			rsFlag = false;
			errbuf = errbuf.append("子表如下记录缺少数据字段:\r\n\t" + buf3137 + "\r\n");
		}
		if(buf3116.length() > 1){
			// 表示有记录缺少数据字段
			rsFlag = false;
			errbuf = errbuf.append("子表如下记录数据字段不能为空:\r\n\t" + buf3116 + "\r\n");
		}
		
		if(!rsFlag){
			throw new ITFEBizException(errbuf.toString());
		}
	}
	
	
	
	/**
	 * 转化错误信息给用户显示
	 * 
	 * @param String
	 *            errFlag 错误标志
	 * @param String
	 *            errStr 错误信息
	 * @return
	 */
	public static String getErrInfo(String errType, String errStr) {
		String[] strs = errStr.split("\"");

		if (DealCodeConstants.DB2_SQL3125W.equals(errType)) {
			return "第\"" + strs[1] + "\"行\"" + strs[3] + "\"列、";
		} else if (DealCodeConstants.DB2_SQL3124W.equals(errType)) {
			return "第\"" + strs[1] + "\"行\"" + strs[3] + "\"列、";
		} else if (DealCodeConstants.DB2_SQL3137W.equals(errType)) {
			return "第\"" + strs[1] + "\"行";
		} else if ("SQL3116W".equals(errType)) {
			return "第\"" + strs[1] + "\"行\"" + strs[3] + "\"列、";
		} else if (DealCodeConstants.DB2_REJECT_LINE.equals(errType) || DealCodeConstants.DB2_REJECT_LINE_ENGLISH.equals(errType.toLowerCase())) {
			strs = errStr.split("=");
			int rejectnum = Integer.valueOf(strs[1].trim());
			if(rejectnum != 0){
				return "拒绝\"" + rejectnum + "\"条记录!";
			}
		}

		return "";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String result = "connect to itfedbts user db2inst1 using         " + "\r"+ "\n"

		  + "Database Connection Information"  + "\r"+ "\n"

		 + "Database server        = DB2/LINUXX8664 9.5.5" + "\r"+ "\n"
		 + "SQL authorization ID   = DB2INST1" + "\r"+ "\n"
		 + "Local database alias   = ITFEDBTS" + "\r"+ "\n"


		 + "delete from TV_INFILE_TMP where S_ORGCODE = '010000000002' and S_FILENAME = '2010090810110.txt'" + "\r"+ "\n"
		 + "DB20000I  The SQL command completed successfully."

		 + "import from /itfe/root/temp/20120308/010000000002/2010090810110.txt of del modified by compound=100 insert into TV_INFILE_TMP" + "\r"+ "\n"
		 + "SQL3109N  The utility is beginning to load data from file " + "\r"+ "\n"
		 + "/itfe/root/temp/20120308/010000000002/2010090810110.txt." + "\r"+ "\n"

		 + "SQL3110N  The utility has completed processing.  \"1399\" rows were read from " + "\r"+ "\n"
		 + "the input file." + "\r"+ "\n"

		 + "SQL3221W  ...Begin COMMIT WORK. Input Record Count = \"1399\"." + "\r"+ "\n"

		 + "SQL3222W  ...COMMIT of any database changes was successful. "+ "\r"+ "\n"

		 + "SQL3149N  \"1399\" rows were processed from the input file.  \"1399\" rows were"  + "\r"+ "\n"
		 + "successfully inserted into the table.  \"0\" rows were rejected."+ "\r"+ "\n"


		 + "Number of rows read         = 1399"+ "\r"+ "\n"
		 + "Number of rows skipped      = 0"+ "\r"+ "\n"
		 + "Number of rows inserted     = 1399"+ "\r"+ "\n"
		 + "Number of rows updated      = 0"+ "\r"+ "\n"
		 + "Number of rows rejected     = 0"+ "\r"+ "\n"
		 + "Number of rows committed    = 1399"+ "\r"+ "\n";
		try {
			verifyShellForIncomeRs(result);
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
