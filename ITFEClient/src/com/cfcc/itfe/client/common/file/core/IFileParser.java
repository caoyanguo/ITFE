package com.cfcc.itfe.client.common.file.core;

import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;

public interface IFileParser {

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
	public abstract FileResultDto dealFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException;
}
