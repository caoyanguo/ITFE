package itferesourcepackage;

import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.rcpx.processor.ICompositeMetadataProcessor;
import com.cfcc.jaf.ui.metadata.ButtonMetaData;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ComboMetaData;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TableMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;

/**
 * @author db2itfe
 * @time   13-09-12 09:59:18
 */
public class WzhWidgetisvisbleProcessor implements ICompositeMetadataProcessor {
	/**
	 * 操作一个生成的Composite
	 * 
	 * @param composite
	 * @param metadata
	 * @return
	 */
	public ContainerMetaData process(ContainerMetaData metadata){
		List controls = metadata.controlMetadatas;
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		//无纸化系统隐藏控件
		if(loginfo.getSysflag().equals("0")){
			if(loginfo.getOrgKind().equals("000000000000")){
				if ("信息录入".equals(metadata.caption) || "信息修改".equals(metadata.caption)) {
					for (int i = 0; i < controls.size(); i++) {
						if (controls.get(i) instanceof TextMetaData) {
							TextMetaData textmetadata = (TextMetaData) controls.get(i);
							if (textmetadata.caption.equals("对应支付行号")) {
								textmetadata.visible = false;
							}
						}
						if (controls.get(i) instanceof ComboMetaData) {
							ComboMetaData combo = (ComboMetaData) controls.get(i);
							if (combo.caption.equals("机构级次") || combo.caption.equals("收入是否汇总")) {
								combo.visible = false;
							}
						}
					}
				} 
				if ("维护查询结果".equals(metadata.caption)){
					TableMetaData tablemd = (TableMetaData) controls.get(0);
					for (int i = 0; i < tablemd.columnList.size(); i++) {
						if (tablemd.columnList.get(i) instanceof ColumnMetaData) {
							ColumnMetaData coletadata = (ColumnMetaData) tablemd.columnList.get(i);
							if (coletadata.title.equals("对应支付行号")||coletadata.title.equals("机构级次") || coletadata.title.equals("收入是否汇总")) {
								coletadata.visible = false;
							}
						}
					}
				}
				
			}else{
				for (int i = 0; i < controls.size(); i++) {
					if (controls.get(i) instanceof ButtonMetaData) {
						ButtonMetaData btn = (ButtonMetaData) controls.get(i);
						if(loginfo.getSuserType().equals(StateConstant.User_Type_MainBiz)){//会计主管可以编辑数据
							if (btn.caption.equals("录入")||btn.caption.equals("删除")||btn.caption.equals("修改")
									|| btn.caption.equals("导出全部回单") || btn.caption.equals("导出选中回单") ) {
								btn.visible = true;
							}
						}else{
							if (btn.caption.equals("录入")||btn.caption.equals("删除")||btn.caption.equals("修改")
									|| btn.caption.equals("导出全部回单") || btn.caption.equals("导出选中回单") ) {
								btn.visible = false;
							}
						}
						
						if(loginfo.getSorgcode().startsWith("11")){//浙江实拨资金查询处理界面隐藏控件
							if (btn.caption.equals("复核")||btn.caption.equals("发送")||btn.caption.equals("修改")) {
								btn.visible = true;
							}
						}
						if(loginfo.getPublicparam().indexOf(",sh,")>=0){
							if (btn.id.equals("bc51c53b-be20-4530-a39d-33184c5aa61c")) {
								btn.visible = true;
							}
						}
						if (btn.caption.equals("报表打印")&& btn.id.equals("565bf6e8-4aa2-4ebe-886b-60caccb250d0")) {
							if(loginfo.getSorgcode().startsWith("3302")){
								btn.visible = true;
							}else{
								btn.visible = false;
							}
							
						}
						
					}
					if (controls.get(i) instanceof ComboMetaData) {
						ComboMetaData combo = (ComboMetaData) controls.get(i);
						if(loginfo.getPublicparam().indexOf(",sh,")>=0){
							if (combo.id.equals("b716ab51-c1ad-4e65-80cf-22e9d6682a46")) {
								combo.visible = true;
							}
							if (combo.id.equals("f914967f-8d9f-4558-bf86-1218e9c55007")) {
								combo.visible = false;
							}
						}
					}
				}
			}
		}
		
		return metadata;
	}

}
