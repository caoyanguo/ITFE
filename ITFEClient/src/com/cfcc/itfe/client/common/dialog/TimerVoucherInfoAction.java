package com.cfcc.itfe.client.common.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.Workbench;

import com.cfcc.itfe.client.common.TimerVoucherInfoTask;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.SimpleImageUtil;

/**
 * ����ͨ���˵�������ʾ��ʱ����ƾ֤���������Ϣ
 * 
 * @author hua
 * 
 */
public class TimerVoucherInfoAction extends Action {
	private static Log log = LogFactory.getLog(TimerVoucherInfoAction.class);

	public TimerVoucherInfoAction() {
		setImageDescriptor(SimpleImageUtil.getDescriptor(SimpleImageUtil.HELP));
	}

	public void run() {
		try {
			final TimerVoucherInfoTask timerVoucherTask = (TimerVoucherInfoTask) ContextFactory.getBeanFromApplicationContext("TimerVoucherInfo");

			final Display display = Workbench.getInstance().getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					try {
						if (timerVoucherTask == null) {
							new TimerVoucherInfoDialog(null).open();
						} else {
							timerVoucherTask.setAutoTimerTask(false);
							timerVoucherTask.run();
							timerVoucherTask.setAutoTimerTask(true);
						}
					} catch (Exception e) {
						log.error("ƾ֤�������ҳ���쳣!", e);
						MessageDialog.openErrorDialog(null, e);
					}
				}
			});
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error("��ƾ֤���������Ϣҳ������쳣, �����µ�¼���Ի���ϵ����Ա.");
			MessageDialog.openErrorDialog(null, e);
		}

	}
}
