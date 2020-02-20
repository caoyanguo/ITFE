package com.cfcc.itfe.client.common.file.core;

import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;

public interface IFileParser {

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
	public abstract FileResultDto dealFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException;
}
