package com.cfcc.itfe.client.dataquery.budgetsubcodemonthlyreport;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dataquery.budgetincomequerysubjectreport.BudgetIncomeQuerySubjectReportBean;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;

public class SubjectCodeByMonthListener extends KeyAdapter{
	private static Log log = LogFactory.getLog(SubjectCodeByMonthListener.class);
	private Composite composite;
	private ContainerMetaData metadata;
	AbstractMetaDataEditorPart editor;
	private String maname ;
	private BudgetsubcodemonthlyreportBean bean;

	public SubjectCodeByMonthListener(Composite composite, ContainerMetaData metadata,BasicModel bean,String metaname) {
		this.composite = composite;
		this.metadata = metadata;
		this.editor = MVCUtils.getEditor(metadata.editorid);
		this.bean = (BudgetsubcodemonthlyreportBean) bean;
		this.maname = metaname;
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode != SWT.Selection && e.keyCode != SWT.KEYPAD_CR) {
			return;
		}
		if (e.getSource() instanceof Text) {
			composite.setFocus();
			
			ITFELoginInfo loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
			
			List controls = metadata.controlMetadatas;
			
			if ("查询条件".equals(metadata.name)) {
					for (int i = 0; i < controls.size(); i++) {
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textMeta = (TextMetaData) controls.get(i);
							if ("预算科目代码".equals(textMeta.caption)) {
								List<TsBudgetsubjectDto> subcodes=bean.openDialog(metadata.editorid);
								
								StringBuffer sbf=new StringBuffer();
								String subjectcodes="";
								if(null!=subcodes&&subcodes.size()>0){
									for (TsBudgetsubjectDto tsBudgetsubjectDto : subcodes) {
										String subjectcode=tsBudgetsubjectDto.getSsubjectcode();
										sbf.append(subjectcode).append(",");
									}
									subjectcodes=sbf.toString();
									subjectcodes=subjectcodes.substring(0, subjectcodes.length()-1);
									bean.setSubcode(subjectcodes);
								}else{
									bean.setSubcode(null);
								}
								this.editor.fireModelChanged();
								continue;
							}
						}
					}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		super.keyReleased(e);
	}

}
