package com.cfcc.test.facade;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.util.datamove.DataMoveUtil;

public class DeleteTableTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			DataMoveUtil.deletePlaceNoUseTable();
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
