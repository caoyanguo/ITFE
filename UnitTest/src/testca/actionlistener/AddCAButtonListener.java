package testca.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import testca.constant.DefaultConstant;
import testca.util.EncryptFacade;
import testca.windows.AddCAFrame;

public class AddCAButtonListener implements ActionListener {
	private AddCAFrame jframe;

	public AddCAButtonListener(AddCAFrame jframe) {
		this.jframe = jframe;
	}

	public void actionPerformed(ActionEvent event) {
		try {
			// 要加密的文件路径
			String fileName = jframe.text1.getText();
			if (fileName == null || fileName.length() == 0) {
				JOptionPane.showMessageDialog(jframe, "请选择要加签的文件");
				return;
			}
			String desFileName = fileName.replace(".txt", "_jm.txt");
			String key = jframe.text_key.getText();
			if(key==null||key.trim().length()==0){
				key = DefaultConstant.ENCRYPT_KEY__DEFAULT_VALUE;
			}
			String split = ",";

			// 要加密的文件类型
			String msg = "";

			if (jframe.jb1.getModel().isSelected()) {
				msg = EncryptFacade.xymEncrypt(fileName, desFileName);
			} else if (jframe.jb2.getModel().isSelected()) {
				msg = EncryptFacade
						.tkEncrypt(fileName, key, split, desFileName);
			} else if (jframe.jb3.getModel().isSelected()) {
				msg = EncryptFacade
						.sbEncrypt(fileName, key, split, desFileName);
			} else {
				JOptionPane.showMessageDialog(jframe, "请选择加密类型");
				return;
			}

			if (msg != "") {
				JOptionPane.showMessageDialog(jframe, "加密失败");
				return;
			} else {
				JOptionPane.showMessageDialog(jframe, "加密成功,文件为"+desFileName);
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(jframe, "加密失败");
			return;
		}
	}

}
