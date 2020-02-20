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
			// 要解析的文件路径
			String fileName = jframe.text1.getText();
			if (fileName == null || fileName.length() == 0) {
				JOptionPane.showMessageDialog(jframe, "请选择要解析的文件");
				return;
			}
			String finorgcode= jframe.text2.getText();
			if(finorgcode==null||finorgcode.trim().length()==0){
				JOptionPane.showMessageDialog(jframe, "请填写财政机构代码");
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
					.append("文件")
					.append("【")
					.append(new File(paths[i].toString()).getName())
					.append("】")
					.append(",")
					.append("共")
					.append("【")
					.append(number)
					.append("】")
					.append("条记录")
					.append("=====================================================")
					.append("\r\n");
					if(!tip.equals("")){
						msgSbf.append(tip+"\r\n");
					}
				}
			}
			orderSbf.append("****************************************************")
			.append("共解析")
			.append("【")
			.append(paths.length)
			.append("】")
			.append("个文件，")
			.append("总记录为")
			.append("【")
			.append(sum)
			.append("】")
			.append("*******************************************************");
			
			String tips=msgSbf.toString();
			if (!tips.equals("")) {
				JOptionPane.showMessageDialog(jframe, tips);
				return;
			} else {
				System.out.println(orderSbf.toString());
				JOptionPane.showMessageDialog(jframe, "文件解析成功！");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(jframe, "文件解析失败！");
			return;
		}
	}

}
