package com.cfcc.itfe.client.common.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.cfcc.jaf.rcp.control.table.TableFacadeX;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;

public class SuperviseTableFacade extends TableFacadeX {

	TableViewer viewer;

	public SuperviseTableFacade(final TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		IBaseLabelProvider labelProvider = viewer.getLabelProvider();
		
		viewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				// int clientWidth = viewer.getTable().getClientArea().width;
				// event.width = clientWidth + 5;
				// event.height = event.gc.getFontMetrics().getHeight() + 5;
				event.height = event.gc.getFontMetrics().getHeight() + 6;
			}
		});

	}

	@Override
	public void addColumn(List column) {
		List collist = new ArrayList();
		for(Object obj : column) {
			ColumnMetaData metadata = (ColumnMetaData)obj;
			metadata.width=200;
			collist.add(metadata);
		}
		super.addColumn(collist);
	}

}
