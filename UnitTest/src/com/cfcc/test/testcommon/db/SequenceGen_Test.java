package com.cfcc.test.testcommon.db;

import junit.framework.TestCase;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.facade.SequenceGenerator;

public class SequenceGen_Test extends TestCase {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	public void testGetNextByDb2() {
		try {
			for (int i = 0; i < 1000; i++) {
				System.out.println(SequenceGenerator.getNextByDb2(
						SequenceName.PARAMSNO, 1000, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
