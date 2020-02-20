package com.cfcc.itfe.service.para.modifypassword;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.service.para.modifypassword.AbstractModifyPasswordService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * @author caoyg
 * @time   09-11-20 14:34:55
 * codecomment: 
 */

public class ModifyPasswordService extends AbstractModifyPasswordService {
	private static Log log = LogFactory.getLog(ModifyPasswordService.class);	


	/**
	 * –ﬁ∏ƒ√‹¬Î
	 	 
	 * @generated
	 * @param password
	 * @throws ITFEBizException	 
	 */
    public void modifyPassword(String password) throws ITFEBizException {
		TsUsersPK pk = new TsUsersPK();
		pk.setSorgcode(getLoginInfo().getSorgcode());
		pk.setSusercode(getLoginInfo().getSuserCode());
		try {
			TsUsersDto userDto = (TsUsersDto) DatabaseFacade.getDb().find(pk);
			if (userDto != null) {
				userDto.setSpassword(password);
				DatabaseFacade.getODB().update(userDto);
			}else{
				throw new ITFEBizException("√‹¬Î–ﬁ∏ƒ ß∞‹£°");
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("√‹¬Î–ﬁ∏ƒ ß∞‹£°", e);
		}
      
    }

}