package com.cfcc.itfe.client.common;

import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.Workbench;

import com.cfcc.itfe.client.common.dialog.TimerVoucherInfoDialog;
import com.cfcc.itfe.persistence.dto.TimerVoucherInfoDto;

/**
 * 客户端定时提醒Spring定时任务类
 * 
 * @author hua
 * 
 */
public class TimerVoucherInfoTask extends TimerTask {
	public static TimerVoucherInfoDialog timerDialog = null;
	private static Log log = LogFactory.getLog(TimerVoucherInfoTask.class);
	public boolean isAutoTimer = true;
	@Override
	public void run() {
		final Display display = Workbench.getInstance().getDisplay();
//		display.syncExec(new Runnable() {
//			public void run() {
				try {
					if (display != null && !display.isDisposed()) {
						int timerComboIndex = 0;
						if (timerDialog != null) {
							try {
								if(timerDialog!=null&&timerDialog.getTimerCombo()!=null)
									timerComboIndex = timerDialog.getTimerCombo().getSelectionIndex();
								timerDialog.close(); // 在这里先关闭是为了解决当dialog打开多次时swt缓存失效导致不显示标题行问题
							} catch (Exception e) {
							}
						}
						if (isNeedTimer() || !isAutoTimer) { // 只有当有数据时才打开对话框
							if(timerDialog==null)
								timerDialog = new TimerVoucherInfoDialog(null);
							timerDialog.open();
//							timerDialog.getTimerCombo().select(timerComboIndex);
						}
					}

				} catch (Exception e) {
					log.error("客户端定时凭证处理情况提醒异常!", e);
				}
//			}
//		});
	}

	/**
	 * 检查当天待处理凭证状态
	 * 
	 * @return
	 */
	private boolean isNeedTimer() {
		if(timerDialog==null)
			timerDialog = new TimerVoucherInfoDialog(null);
		List<TimerVoucherInfoDto> voucherList = timerDialog.retriveTimerInfo();
		int size = voucherList.size();
		if (size == 0) {
			return Boolean.FALSE;
		} else if (size == 1) { // 当大小为1时，有可能是一条提示消息
			if (timerDialog.TIMER_VOUCHER_INFO_ERROR.equals(voucherList.get(0).getSvtcode())) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		} else {
			return Boolean.TRUE;
		}
	}

	public void setAutoTimerTask(boolean isTimerAuto) {
		this.isAutoTimer = isTimerAuto;
	}
}
