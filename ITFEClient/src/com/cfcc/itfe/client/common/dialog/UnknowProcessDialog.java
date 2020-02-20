package com.cfcc.itfe.client.common.dialog;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.progress.IProgressService;

import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.RunnableWithException;

public class UnknowProcessDialog {

	/**
	 * 
	 * 在的任务(runnable)运行过程中打开一个未知进度的进度对话框
	 * 
	 * 
	 * 
	 * @param controller
	 * 
	 * @param runnable
	 */

	public static int openUnkownProgressDialogRunUI(Shell shell,

	final RunnableWithException runnable) {

		IProgressService service = PlatformUI.getWorkbench()

		.getProgressService();

		try {

			IRunnableWithProgress op = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)

				throws InvocationTargetException, InterruptedException {

					try {

						monitor.beginTask("处理中，请稍候...",

						IProgressMonitor.UNKNOWN);

						runnable.run();

					} catch (Throwable e) {

						throw new InvocationTargetException(e);

					} finally {

						monitor.done();

					}

				}

			};

			service.runInUI(PlatformUI.getWorkbench().getProgressService(), op,

			null);

		} catch (InvocationTargetException e) {

			MessageDialog.openMessageDialog(shell, e.getTargetException().getMessage());

			return -1;

		} catch (InterruptedException e) {

			MessageDialog.openErrorDialog(shell, e);

			return -1;

		}

		return 0;

	}

	public static int openUnkownProgressDialog(Shell shell,

	final RunnableWithException runnable) {

		ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);

		IRunnableWithProgress rp;

		rp = new IRunnableWithProgress() {

			public void run(IProgressMonitor progressMonitor)

			throws InterruptedException, InvocationTargetException {

				progressMonitor.beginTask("处理中，请稍候...",

				IProgressMonitor.UNKNOWN);

				try {

					runnable.run();

				} catch (Throwable e) {

					throw new InvocationTargetException(e);

				} finally {

					progressMonitor.done();

				}

			}

		};

		try {

			monitor.run(true, false, rp);

		} catch (InvocationTargetException e) {

			MessageDialog.openErrorDialog(shell, e.getTargetException());

			return -1;

		} catch (InterruptedException e) {

			MessageDialog.openErrorDialog(shell, e);

			return -1;

		}

		return 0;

	}

}
