package com.cfcc.itfe.service.sendbiz.exporttbsforbj;

import com.cfcc.itfe.facade.data.MulitTableDto;

public interface IProcessHandler {
	MulitTableDto process(String fullFileName);
}
