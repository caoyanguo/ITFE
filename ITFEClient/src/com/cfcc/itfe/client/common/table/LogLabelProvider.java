/**
 * 
 */
package com.cfcc.itfe.client.common.table;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.JafTableLabelProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.SWTResourceManager;

/**
 * 接收或发送日志颜色修改类
 * @author sjz
 */
public class LogLabelProvider extends JafTableLabelProvider {
	private static Log log = LogFactory.getLog(LogLabelProvider.class);
	private static final Color COLOR_BLUE = SWTResourceManager.getColor(SWT.COLOR_BLUE);
	private static final Color COLOR_RED = SWTResourceManager.getColor(SWT.COLOR_RED);
	private static final Color COLOR_BLACK = SWTResourceManager.getColor(SWT.COLOR_BLACK);

	public LogLabelProvider(TableViewer tableViewer, List columnDefines, PagingContext pagingContext) {
		super(tableViewer, columnDefines, pagingContext);
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.control.table.JafTableLabelProvider#getBackground(java.lang.Object)
	 */
	@Override
	public Color getBackground(Object arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.control.table.JafTableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.control.table.JafTableLabelProvider#getFont(java.lang.Object)
	 */
	@Override
	public Font getFont(Object arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.control.table.JafTableLabelProvider#getForeground(java.lang.Object)
	 */
	@Override
	public Color getForeground(Object element) {
		if (element instanceof IDto) {
			String sretcode = "";
			try {
				sretcode = BeanUtils.getProperty(element, "sretcode");
			} catch (Exception e) {
				log.debug("列表中没有处理码字段:" + e.getMessage());
				return null;
			}
			if ((ITFECommonConstant.STATUS_SUCCESS).equals(sretcode)) {// 发送成功
				return COLOR_BLUE;
			} else if ((ITFECommonConstant.STATUS_CANCELED).equals(sretcode)) {// 作废
				return COLOR_RED;
			} else {// 其他
				return COLOR_BLACK;
			} 
		}
		return null;
	}

}
