package testca.windows;


import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import testca.actionlistener.AddCAButtonListener;
import testca.actionlistener.SelectFileButtonListener;
import testca.constant.DefaultConstant;

public class AddCAFrame extends JFrame {
	public JPanel contentPane;
	public JLabel label1 = new JLabel();
	public JTextField text1 = new JTextField();
	public JButton jButton1 = new JButton();

	//山东加密
	public JLabel label_key = new JLabel();
	public JTextField text_key = new JTextField();
	
	// 加密类型选择
	public ButtonGroup encryptTypeGroup = new ButtonGroup();;
	public JRadioButton jb1;
	public JRadioButton jb2;
	public JRadioButton jb3;

	public JButton jButton2 = new JButton();

	public AddCAFrame() {
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(null);
		setBounds(300, 150, 700, 300);
		setTitle("AddCAFrame");

		// 选择文件按钮行
		label1.setText("加签前文件：");
		label1.setBounds(0, 0, 100, 30);
		text1.setBounds(150, 0, 300, 30);
		jButton1.setText("浏览");
		jButton1.addActionListener(new SelectFileButtonListener(this));
		jButton1.setBounds(500, 0, 100, 30);

		//山东加密key值
		label_key.setText("山东Key：");
		label_key.setBounds(0, 50, 100, 30);
		text_key.setText(DefaultConstant.ENCRYPT_KEY__DEFAULT_VALUE);
		text_key.setBounds(150, 50, 300,30);
		// 加密类型：
		jb1 = new JRadioButton("1校验码计算");
		jb2 = new JRadioButton("2山东退库文件");
		jb3 = new JRadioButton("3山东实拨文件");
		jb1.setBounds(0, 100, 150, 30);
		jb2.setBounds(170, 100, 150, 30);
		jb3.setBounds(340, 100, 150, 30);
		encryptTypeGroup.add(jb1);
		encryptTypeGroup.add(jb2);
		encryptTypeGroup.add(jb3);

		jButton2.setText("加密");
		jButton2.addActionListener(new AddCAButtonListener(this));
		jButton2.setBounds(250, 150, 100, 30);

		contentPane.add(label1);
		contentPane.add(text1);
		contentPane.add(jButton1);
		contentPane.add(label_key);
		contentPane.add(text_key);
		
		contentPane.add(jb1); // cont 是得到的内容面板
		contentPane.add(jb2);
		contentPane.add(jb3);
		contentPane.add(jButton2);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setVisible(true);

	}

}
