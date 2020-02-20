package com.cfcc.itfe.component.common;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.util.datamove.incomeShareMove;

public class DayCutIncomeShareComponent implements Callable {


	public Object onCall(MuleEventContext eventContext) throws Exception {
		// TODO Auto-generated method stub
		incomeShareMove.timerTaskForDataMove();

		return eventContext.getMessage();
	}

}
