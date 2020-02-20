package com.cfcc.itfe.client;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.cfcc.itfe.client.init.ColumnSortProcessor;
import com.cfcc.jaf.rcp.control.menu.JAFMenuUtils;
import com.cfcc.jaf.rcp.control.skin.LookAndFeel;
import com.cfcc.jaf.rcp.preferences.FontAndControlPreferValueProcessor;
import com.cfcc.jaf.rcp.util.BundlePathUtil;
import com.cfcc.jaf.ui.emf.GenerationModelDataSource;
import com.cfcc.jaf.ui.emf.adapter.FontAndControlProcessorRegister;
import com.cfcc.jaf.ui.emf.adapter.MetaDataFactory;
import com.cfcc.jaf.ui.emf.defautvalue.CompositelMetaDataDefaultValueProcessor;
import com.cfcc.jaf.ui.emf.defautvalue.ControlMetaDataDefaultValueProcessor;

/**
 * 
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
				Font font = LookAndFeel.getDefault().getMenuFont();
		if (font != null) {
			JAFMenuUtils.setSystemMenuFont(font);
		}
		MetaDataFactory
				.registerMetaDataProcessor(new ControlMetaDataDefaultValueProcessor());
		MetaDataFactory
		.registerMetaDataProcessor(new ColumnSortProcessor());
		MetaDataFactory
				.registerMetaDataProcessor(new CompositelMetaDataDefaultValueProcessor());
		FontAndControlProcessorRegister
				.registerMetaDataProcessor(new FontAndControlPreferValueProcessor());
		GenerationModelDataSource.MODEL_DIRECTORY = BundlePathUtil.getRealPath(
				"itfe", "model");
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			if (JAFMenuUtils.isExitRestore()) {
				JAFMenuUtils.clearSystemMenuFont();
			}
			display.dispose();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
					if (JAFMenuUtils.isExitRestore()) {
					JAFMenuUtils.clearSystemMenuFont();
				}
					
			}
			
		});
	}
}
