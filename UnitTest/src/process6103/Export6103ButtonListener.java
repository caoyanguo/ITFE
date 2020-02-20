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
			if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(jframe, "��ȷ��Ҫ����������")){
				//���BasicDataSource�������ֵ
				BasicDataSource basicDataSource = (BasicDataSource) ContextFactory.getApplicationContext().getBean("DataSource.DB.ODB");
				String url=basicDataSource.getUrl();//����Դ
				url=url.substring(url.lastIndexOf("/")+1);//������ݿ�����
				String userName=basicDataSource.getUsername();//���ݿ��û���
				String password=basicDataSource.getPassword();//���ݿ�����
				//�ж��ļ��Ƿ����
				CallShell.Exists("D:\\empty.del");
				//�������ݿ��SQL
				String connectDb=" connect to "+url+" user "+userName+" using "+password+" ;\r\n";
				//�������ݿ�SQL
				String clearData=" import from D:\\empty.del of del replace into TV_FIN_INCOME_DETAIL ;\r\n";
				//�������ݿ�SQL
				String exportData=" export to D:\\TV_FIN_INCOME_DETAIL.del of del select * from TV_FIN_INCOME_DETAIL ;\r\n";
				//���������ݿ�SQLд���ļ�exportData.sql����ִ��SQL
				String exportResult=CallShell.ProcessData(connectDb+exportData,"D:\\exportData.sql");
				System.out.println(exportResult);
				//���������ݿ�SQLд���ļ�clearData.sql����ִ��SQL
				String clearResult=CallShell.ProcessData(connectDb+clearData,"D:\\clearData.sql");
				System.out.println(clearResult);
				JOptionPane.showMessageDialog(jframe, "�������ݳɹ����ļ�·��ΪD:\\TV_FIN_INCOME_DETAIL.del��");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(jframe, "��������ʧ�ܣ�");
			return;
		}
	}

}
