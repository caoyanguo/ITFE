/**
 * 
 */
package com.cfcc.itfe.client.processor;

import org.eclipse.swt.widgets.Composite;

import com.cfcc.jaf.rcpx.processor.ICompositeProcessor;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;

/**
 * 为控件加上下键控制
 * 
 * @author gaoym
 * 
 */
public class ArrowKeyTraverseProcessor implements ICompositeProcessor {

	public ArrowKeyTraverseProcessor() {
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
		AddListenerHelper.setArrowKeyTraversable(composite);
		return composite;
	}

}
