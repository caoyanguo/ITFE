/**
 * 
 */
package com.cfcc.itfe.client.processor;

import org.eclipse.swt.widgets.Composite;

import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * Ϊ�ؼ������¼�����
 * 
 * @author gaoym
 * 
 */
public class ArrowKeyTraverseProcessor2 implements ICompositeProcessor {

	public ArrowKeyTraverseProcessor2() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcpx.processor.ICompositeProcessor#process(org.eclipse.swt
	 * .widgets.Composite, com.cfcc.jaf.ui.metadata.ContainerMetaData)
	 */
	public Composite process(Composite composite, ContainerMetaData metadata) {
		AddListenerHelper2.setArrowKeyTraversable(composite);
		return composite;
	}

}
