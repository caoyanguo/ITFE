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
 * �����ϴӾɵ�rcp���հ����
 * sjz����ITFEϵͳ����Ҫ�������޸�
 * @author fengqing
 */
public class ReportComposite extends Composite {
	//���Լ��ر������ݵĶ���
	private ViewerComposite viewerComposite;
	//������ʾ��̬
	private int reportStyle = JasperConstants.ALL;
	//��ű�����ʾ�ؼ�������
	protected ScrolledComposite scrolled;
	
	/**
	 * ����IReport�������
	 * @param parent ������
	 * @param Style �������ͣ�ע�ⲻ��SWT������
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
		
		//����������ʾ���������
		viewerComposite = new ViewerComposite(scrolledContent,SWT.NONE, reportStyle);
		this.layout(true);
	}

	/**
	 * ���ر�����ʾ��������
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