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
 * 用于通过菜单主动显示定时提醒凭证处理情况信息
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
						log.error("凭证处理情况页面异常!", e);
						MessageDialog.openErrorDialog(null, e);
					}
				}
			});
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error("打开凭证处理情况信息页面出现异常, 请重新登录后尝试或联系管理员.");
			MessageDialog.openErrorDialog(null, e);
		}

	}
}
