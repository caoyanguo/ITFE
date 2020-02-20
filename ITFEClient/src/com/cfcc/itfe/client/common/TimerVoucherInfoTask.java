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
 * �ͻ��˶�ʱ����Spring��ʱ������
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
								timerDialog.close(); // �������ȹر���Ϊ�˽����dialog�򿪶��ʱswt����ʧЧ���²���ʾ����������
							} catch (Exception e) {
							}
						}
						if (isNeedTimer() || !isAutoTimer) { // ֻ�е�������ʱ�Ŵ򿪶Ի���
							if(timerDialog==null)
								timerDialog = new TimerVoucherInfoDialog(null);
							timerDialog.open();
//							timerDialog.getTimerCombo().select(timerComboIndex);
						}
					}

				} catch (Exception e) {
					log.error("�ͻ��˶�ʱƾ֤������������쳣!", e);
				}
//			}
//		});
	}

	/**
	 * ��鵱�������ƾ֤״̬
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
		} else if (size == 1) { // ����СΪ1ʱ���п�����һ����ʾ��Ϣ
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
