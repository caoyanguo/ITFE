package process6103;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.apache.commons.dbcp.BasicDataSource;

import com.cfcc.jaf.core.loader.ContextFactory;


public class Export6103ButtonListener implements ActionListener {
	private PrcocessWindow jframe;

	public Export6103ButtonListener(PrcocessWindow prcocessWindow) {
		this.jframe = prcocessWindow;
	}

	public void actionPerformed(ActionEvent event) {
		try {
			if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(jframe, "你确定要导出数据吗？")){
				//获得BasicDataSource类的属性值
				BasicDataSource basicDataSource = (BasicDataSource) ContextFactory.getApplicationContext().getBean("DataSource.DB.ODB");
				String url=basicDataSource.getUrl();//数据源
				url=url.substring(url.lastIndexOf("/")+1);//获得数据库名字
				String userName=basicDataSource.getUsername();//数据库用户名
				String password=basicDataSource.getPassword();//数据库密码
				//判断文件是否存在
				CallShell.Exists("D:\\empty.del");
				//连接数据库的SQL
				String connectDb=" connect to "+url+" user "+userName+" using "+password+" ;\r\n";
				//清理数据库SQL
				String clearData=" import from D:\\empty.del of del replace into TV_FIN_INCOME_DETAIL ;\r\n";
				//导出数据库SQL
				String exportData=" export to D:\\TV_FIN_INCOME_DETAIL.del of del select * from TV_FIN_INCOME_DETAIL ;\r\n";
				//将导出数据库SQL写入文件exportData.sql，并执行SQL
				String exportResult=CallShell.ProcessData(connectDb+exportData,"D:\\exportData.sql");
				System.out.println(exportResult);
				//将清理数据库SQL写入文件clearData.sql，并执行SQL
				String clearResult=CallShell.ProcessData(connectDb+clearData,"D:\\clearData.sql");
				System.out.println(clearResult);
				JOptionPane.showMessageDialog(jframe, "导出数据成功，文件路径为D:\\TV_FIN_INCOME_DETAIL.del！");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(jframe, "导出数据失败！");
			return;
		}
	}

}
