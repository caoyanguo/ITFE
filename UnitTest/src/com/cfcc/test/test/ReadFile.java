package com.cfcc.test.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;

public class ReadFile {
	
	public static final String INCOME_SPLIT = ","; // 收入业务的分割符号
	public static final String PAYOUT_SPLIT = "|"; // 支出业务的分割符号
	
	/**
	 * @param args
	 * @throws ITFEBizException 
	 */
	public static void main(String[] args) throws ITFEBizException {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"D:\\国库前置资料文件\\杭州\\20091111010130199.xml")));
			String data = null;
			boolean headflag = false; // 报文头标志
			boolean mainflag = false; // 报文主体信息标志
			boolean detailflag = false; // 报文明细信息标志
			StringBuffer mainbuf = new StringBuffer(); // 存放主体信息字段
			StringBuffer detailbuf = new StringBuffer(); // 存放子信息字段
			int maincount = 1; // 主体信息分包记数器
			int recordNum = 0; // 主体信息记录总条数

			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}

				String tmpdata = data.toLowerCase();
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_START) >= 0) {
					if (mainflag || detailflag) {
						throw new ITFEBizException("文件格式错误! [文件头位置不正确]");
					}
					headflag = true;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_END) >= 0) {
					headflag = false;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_START) >= 0) {
					if (headflag || detailflag) {
						throw new ITFEBizException("文件格式错误! [文件头没有结束标志]");
					}
					mainflag = true;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_END) >= 0) {
					mainflag = false;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_START) >= 0) {
					if (headflag || mainflag) {
						throw new ITFEBizException("文件格式错误! [文件头或文件主体没有结束标志]");
					}
					detailflag = true;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_END) >= 0) {
					detailflag = false;
				}

				if (mainflag) {
					// 在读取文件主体信息
					if (tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >= 0) {
						String maininfo = parserMsgBody(data);
						maininfo = maininfo.replace(PAYOUT_SPLIT,
								INCOME_SPLIT);
						mainbuf.append(maininfo);
						mainbuf.append("\r\n");

						recordNum++;
						maincount++;
						
						System.out.println(maininfo);
					}
				}

				if (detailflag) {
					// 在读取文件明细信息
					if (tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >= 0) {
						String detailinfo = parserMsgBody(data);
						detailinfo = detailinfo.replaceAll(PAYOUT_SPLIT,
								INCOME_SPLIT);

						detailbuf.append(detailinfo);
						detailbuf.append(INCOME_SPLIT);
						detailbuf.append("\r\n");
					}
				}
			}

		} catch (Exception e) {
			throw new ITFEBizException("读取文件[" + "123" + "]出现异常", e);
		}
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
	
	

}
