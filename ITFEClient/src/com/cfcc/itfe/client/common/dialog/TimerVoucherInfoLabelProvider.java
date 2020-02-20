package com.cfcc.itfe.client.common.dialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.persistence.dto.TimerVoucherInfoDto;
import com.cfcc.jaf.rcp.util.SWTResourceManager;


public class TimerVoucherInfoLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider {
	private FontRegistry registry = new FontRegistry();

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof TimerVoucherInfoDto) {
			TimerVoucherInfoDto p = (TimerVoucherInfoDto) element;
			if (columnIndex == 0) {
				return p.getStrecode();
			} else if (columnIndex == 1) {
				return p.getSbizname().toString();
			} else if (columnIndex == 2) {
				return p.getCount1() + "";
			} else if (columnIndex == 3) {
				return p.getCount2() + "";
			} else if (columnIndex == 4) {
				return p.getCount3() + "";
			} else if (columnIndex == 5) {
				return p.getCount4() + "";
			}
		}
		return null;
	}

	public Color getForeground(Object element, int columnIndex) {
		if (element instanceof TimerVoucherInfoDto) {
			TimerVoucherInfoDto p = (TimerVoucherInfoDto) element;
			if(TimerVoucherInfoDialog.TIMER_VOUCHER_INFO_ERROR.equals(p.getSvtcode())) {
				return SWTResourceManager.getColor(SWT.COLOR_RED);
			}
			if (p.getCount3() > 0) {
				return SWTResourceManager.getColor(SWT.COLOR_DARK_RED);
			}
		}
		return null;
	}

	public Font getFont(Object element, int columnIndex) {
		if (element instanceof TimerVoucherInfoDto) {
			TimerVoucherInfoDto p = (TimerVoucherInfoDto) element;
			if (p.getCount3() > 0) {
				return registry.getBold(Display.getCurrent().getSystemFont().getFontData()[0].getName());
			}
		}
		return null;
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public Color getBackground(Object element, int columnIndex) {
		return null;
	}
}