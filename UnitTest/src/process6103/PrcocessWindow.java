package process6103;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class PrcocessWindow extends JFrame {
	/**
	 * @author db2admin
	 * @category
	 */
	private static final long serialVersionUID = 1L;
	
	public JPanel contentPane;
	
	public JLabel label1 = new JLabel();//选择文件标签
	public JTextField text1 = new JTextField();//文件加载路径文本框
	public JButton jButton1 = new JButton();//选择所要加载文件的按钮
	
	public JLabel label2 = new JLabel();//财政代码标签
	public JTextField text2 = new JTextField();//财政代码文本框
	public JButton jButton2 = new JButton();//解析XMl文件按钮
	public JButton jButton3 = new JButton();//导出DEL文件按钮

	public PrcocessWindow() {
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(null);
		setBounds(300, 150, 700, 300);
		setTitle("处理入库流水报文");

		// 选择文件按钮行
		label1.setText("请选择文件：");
		label1.setBounds(50, 50, 100, 30);
		text1.setBounds(150, 50, 350, 30);
		jButton1.setText("浏览");
		jButton1.addActionListener(new SelectFileDirButtonListener(this));
		jButton1.setBounds(500, 50, 100, 30);


		label2.setText("财政代码：");
		label2.setBounds(50, 100, 100, 30);
		text2.setBounds(150, 100, 450, 30);
		jButton2.setText("解析");
		jButton2.addActionListener(new Process6103ButtonListener(this));
		jButton2.setBounds(200, 200, 100, 30);
		
		jButton3.setText("导出");
		jButton3.addActionListener(new Export6103ButtonListener(this));
		jButton3.setBounds(350, 200, 100, 30);

		contentPane.add(label1);
		contentPane.add(text1);
		contentPane.add(jButton1);
		contentPane.add(label2);
		contentPane.add(text2);
		contentPane.add(jButton2);
		contentPane.add(jButton3);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setVisible(true);

	}

}
