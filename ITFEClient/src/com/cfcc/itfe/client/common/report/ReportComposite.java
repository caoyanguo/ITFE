package com.cfcc.itfe.client.common.report;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.jasperassistant.designer.viewer.ReportViewer;
import com.jasperassistant.designer.viewer.ViewerComposite;
import com.jasperassistant.designer.viewer.util.JasperConstants;

/**
 * 基本上从旧的rcp中照搬过来
 * sjz根据ITFE系统的需要进行了修改
 * @author fengqing
 */
public class ReportComposite extends Composite {
	//可以加载报表数据的对象
	private ViewerComposite viewerComposite;
	//报表显示形态
	private int reportStyle = JasperConstants.ALL;
	//存放报表显示控件的容器
	protected ScrolledComposite scrolled;
	
	/**
	 * 创建IReport报表对象
	 * @param parent 父容器
	 * @param Style 报表类型，注意不是SWT的类型
	 */
	public ReportComposite(Composite parent, int Style) {
		super(parent, SWT.NONE);
		this.reportStyle = Style;
		createGUI();
	}
	
	private void createGUI() {
		setLayout(new GridLayout());
		scrolled = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData newPageData = new GridData(GridData.FILL_BOTH);
		scrolled.setLayoutData(newPageData);

		final Composite scrolledContent = new Composite(scrolled, SWT.NONE);
		scrolledContent.setLayout(new FillLayout());
		
		scrolled.setExpandVertical(true);
		scrolled.setExpandHorizontal(true);
		scrolled.setContent(scrolledContent);
		scrolled.setMinSize(scrolledContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		//创建报表显示对象的容器
		viewerComposite = new ViewerComposite(scrolledContent,SWT.NONE, reportStyle);
		this.layout(true);
	}

	/**
	 * 返回报表显示对象容器
	 * @return
	 */
	public ReportViewer getReportViewer() {
		return (ReportViewer) viewerComposite.getReportViewer();
	}

	public ScrolledComposite getScrolled() {
		return scrolled;
	}

	public void setScrolled(ScrolledComposite scrolled) {
		this.scrolled = scrolled;
	}
}