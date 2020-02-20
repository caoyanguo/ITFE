package com.cfcc.itfe.client.sendbiz.exporttbsforbj;

import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public interface IProcessHandler {
	TbsFileInfo process(List<IDto> mainlist,String fileName)  throws ITFEBizException;
}
