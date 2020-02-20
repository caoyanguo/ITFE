package com.cfcc.itfe.client.common.table;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.BankValidateDto;
import com.cfcc.jaf.rcp.control.table.JafTableLabelProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.SWTResourceManager;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;

/**
 * 为商行校验凭证结果列表提供颜色显示
 * 
 * @author hua
 * 
 */
public class BankVoucherValidateColorProvider extends JafTableLabelProvider {
	private static Log log = LogFactory.getLog(BankVoucherValidateColorProvider.class);

	private static final Color COLOR_DARK_RED = SWTResourceManager.getColor(SWT.COLOR_DARK_RED);

	public BankVoucherValidateColorProvider(TableViewer tableViewer, List<ColumnMetaData> columnDefines, PagingContext pagingContext) {
		super(tableViewer, columnDefines, pagingContext);
	}

	@Override
	public Color getForeground(Object element) {
		if (element != null && element instanceof BankValidateDto) {
			BankValidateDto validateDto = (BankValidateDto) element;
			String result = validateDto.getRealResult();
			if (StateConstant.MERGE_VALIDATE_SUCCESS.equals(result)) {
				return new Color(null, 0, 128, 0);
			} else if (StateConstant.MERGE_VALIDATE_FAILURE.equals(result)) {
				return COLOR_DARK_RED;
			} else if (StateConstant.MERGE_VALIDATE_NOTCOMPARE.equals(result) || StringUtils.isBlank(result)) {
				return null;
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	@Override
	public Font getFont(Object element) {
		return null;
	}
}
