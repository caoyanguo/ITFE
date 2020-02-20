package com.cfcc.itfe.webservice.guangdong.income;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;

/**
 * 为了广东webservice的收入接口而从客户端移植过来
 * 
 * @author hua
 * 
 */
public class FileOperFacade {

	private static Log logger = LogFactory.getLog(FileOperFacade.class);

	/**
	 * 根据文件名称转化DTO
	 * 
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename) throws ITFEBizException {
		return PublicSearchFacade.getFileObjByFileName(filename);
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
	public static FileResultDto dealIncomeFile(String filepath, String filename, String orgcode) throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
			FileResultDto fileRstDto = new FileResultDto();
			String data = null;
			int i = 0;
			String srlno = "";
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				int lineWithSplitLen = data.split(",").length;
				// 判断如果是13位，则说明是带有资金收纳流水号的
				if (lineWithSplitLen == 13) {
					if (i == 0) {
						fileRstDto.setFileColumnLen(13);
						srlno = data.trim().substring(data.lastIndexOf(",") + 1);
						// 如果第一行的收纳号就为空则提示，并且如果不为18位也需进行提示
						if (null == srlno || "".equals(srlno)) {
							throw new ITFEBizException("文件中资金收纳流水号为空，请查证!");
						} else {
							int srlnoLen = srlno.length();
							if (srlnoLen != 18) {
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

				recordNum++;

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
}
