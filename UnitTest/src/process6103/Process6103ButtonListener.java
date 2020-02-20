package process6103;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JOptionPane;

import org.apache.commons.dbcp.BasicDataSource;

import com.cfcc.jaf.core.loader.ContextFactory;


public class Process6103ButtonListener implements ActionListener {
	private PrcocessWindow jframe;

	public Process6103ButtonListener(PrcocessWindow prcocessWindow) {
		this.jframe = prcocessWindow;
	}

	public void actionPerformed(ActionEvent event) {
		try {
			// Ҫ�������ļ�·��
			String fileName = jframe.text1.getText();
			if (fileName == null || fileName.length() == 0) {
				JOptionPane.showMessageDialog(jframe, "��ѡ��Ҫ�������ļ�");
				return;
			}
			String finorgcode= jframe.text2.getText();
			if(finorgcode==null||finorgcode.trim().length()==0){
				JOptionPane.showMessageDialog(jframe, "����д������������");
				return;
			}
			String split = ";";
			String[] paths=fileName.split(split); 
			StringBuffer msgSbf=new StringBuffer("");
			StringBuffer orderSbf=new StringBuffer("");
			int sum=0;
			if(paths.length>0){
				for(int i=0;i<paths.length;i++){
					String msg=Process6103Main.Process6130(paths[i].toString(),finorgcode);
					String tip=msg.split("=")[0];
					String number=msg.split("=")[1];
					sum+=new Integer(number);
					orderSbf.append("====================================================")
					.append("�ļ�")
					.append("��")
					.append(new File(paths[i].toString()).getName())
					.append("��")
					.append(",")
					.append("��")
					.append("��")
					.append(number)
					.append("��")
					.append("����¼")
					.append("=====================================================")
					.append("\r\n");
					if(!tip.equals("")){
						msgSbf.append(tip+"\r\n");
					}
				}
			}
			orderSbf.append("****************************************************")
			.append("������")
			.append("��")
			.append(paths.length)
			.append("��")
			.append("���ļ���")
			.append("�ܼ�¼Ϊ")
			.append("��")
			.append(sum)
			.append("��")
			.append("*******************************************************");
			
			String tips=msgSbf.toString();
			if (!tips.equals("")) {
				JOptionPane.showMessageDialog(jframe, tips);
				return;
			} else {
				System.out.println(orderSbf.toString());
				JOptionPane.showMessageDialog(jframe, "�ļ������ɹ���");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(jframe, "�ļ�����ʧ�ܣ�");
			return;
		}
	}

}
