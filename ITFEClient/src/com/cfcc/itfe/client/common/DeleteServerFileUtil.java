/**
 * 
 */
package com.cfcc.itfe.client.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;

/**
 * @author Administrator
 * 
 */
public class DeleteServerFileUtil {

	public static void delFile(
			ICommonDataAccessService commonDataAccessService, List filelist)
			throws ITFEBizException {
		if (null != filelist && filelist.size() > 0) {
			commonDataAccessService.delServerWrongFile(filelist);
		}
	}

	/**
	 * 用于将srcList的信息中 去除outList
	 * 
	 * @param srcList
	 * @param outList
	 * @return
	 */
	public static List wipeFileOut(List<String> srcList, List<String> outList) {
		List<String> desList = new ArrayList<String>();
		if (outList != null && outList.size() > 0) {
			for (String str : srcList) {
				for (String out : outList) {
					if (!str.replace("/", File.separator).replace("\\",
							File.separator).equals(
							out.replace("/", File.separator).replace("\\",
									File.separator))) {
						desList.add(str);
					}
				}
			}
			return desList;
		} else {
			return srcList;
		}

	}
}
