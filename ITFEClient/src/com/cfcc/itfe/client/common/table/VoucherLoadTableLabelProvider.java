package com.cfcc.itfe.client.common.table;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.rcp.control.table.JafTableLabelProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;


@SuppressWarnings("unchecked")
public class VoucherLoadTableLabelProvider extends JafTableLabelProvider {

	
	public VoucherLoadTableLabelProvider(TableViewer tableViewer,
			List columnDefines, PagingContext pagingContext) {
		super(tableViewer, columnDefines, pagingContext);
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
//		return SWTResourceManager.getFont("宋体", 13, SWT.NORMAL);
		 return null;
	}

	@Override
	public Color getForeground(Object element) {
		//10已读取 15签收成功 16签收失败 30校验失败62TIPS处理失败 72TCBS处理失败
		//80处理成功90退回成功100读取回单成功
		if(element instanceof TvVoucherinfoDto){
			TvVoucherinfoDto dto =  (TvVoucherinfoDto)element;
			if (DealCodeConstants.VOUCHER_ACCEPT.equals(dto.getSstatus())||DealCodeConstants.VOUCHER_FAIL.equals(dto.getSstatus()) || DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(dto.getSstatus()) || DealCodeConstants.VOUCHER_RECEIVE_FAIL.equals(dto.getSstatus()) || DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(dto.getSstatus()) || DealCodeConstants.VOUCHER_FAIL_TIPS.equals(dto.getSstatus()) || DealCodeConstants.VOUCHER_FAIL_TCBS.equals(dto.getSstatus())) {
				return PlatformUI.getWorkbench().getDisplay().getSystemColor(
						SWT.COLOR_RED);
			}else
			{
				if("3507,3508,3509,3510,3511,3512,3513,3580,3582,3583,3587,3588,".contains(dto.getSvtcode()))
				{
					if(DealCodeConstants.VOUCHER_READRETURN.equals(dto.getSstatus()))
					{
						if(dto.getSdemo()!=null&&dto.getSdemo().contains("对账失败"))
						{
							return PlatformUI.getWorkbench().getDisplay().getSystemColor(
									SWT.COLOR_RED);
						}else
						{
							return PlatformUI.getWorkbench().getDisplay().getSystemColor(
									SWT.COLOR_DARK_GREEN);
						}
					}else if(dto.getSdemo()!=null&&dto.getSdemo().contains("对账失败"))
					{
						return PlatformUI.getWorkbench().getDisplay().getSystemColor(
								SWT.COLOR_RED);
					}
				}
				else
				{
					if(DealCodeConstants.VOUCHER_SUCCESS.equals(dto.getSstatus())||DealCodeConstants.VOUCHER_READRETURN.equals(dto.getSstatus()))
					{
						return PlatformUI.getWorkbench().getDisplay().getSystemColor(
								SWT.COLOR_DARK_GREEN);
					}
				}
			}
		}
		return null;

	}

}
