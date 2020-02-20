package com.cfcc.itfe.client.common.table;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.rcp.control.table.JafTableLabelProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.SWTResourceManager;



public class SuperviseTableLabelProvider extends JafTableLabelProvider {

	public SuperviseTableLabelProvider(TableViewer tableViewer,
			List columnDefines, PagingContext pagingContext) {
		super(tableViewer, columnDefines, pagingContext);
	}

	@Override
	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Font getFont(Object element) {
//		return SWTResourceManager.getFont("ËÎÌו", 13, SWT.NORMAL);
		 return null;
	}

	@Override
	public Color getForeground(Object element) {
		if(element instanceof TvVoucherinfoDto){
			TvVoucherinfoDto dto =  (TvVoucherinfoDto)element;
			if ("1".equals(dto.getShold1())) {
				return PlatformUI.getWorkbench().getDisplay().getSystemColor(
						SWT.COLOR_RED);
			}
		}
//		else if(element instanceof SadTrlSmallCmt301Dto){
//			SadTrlSmallCmt301Dto dto =  (SadTrlSmallCmt301Dto)element;
//			if (StateConstant.HAVING_APPLY_VALUE.equals(dto.getSishavingapply())) {
//				return PlatformUI.getWorkbench().getDisplay().getSystemColor(
//						SWT.COLOR_RED);
//			}
//		}else if(element instanceof SadTvInnerQueryDto){
//			SadTvInnerQueryDto dto =  (SadTvInnerQueryDto)element;
//			if (StateConstant.HAVING_APPLY_VALUE.equals(dto.getSishavingapply())) {
//				return PlatformUI.getWorkbench().getDisplay().getSystemColor(
//						SWT.COLOR_RED);
//			}
//		}
		return null;

	}

}
