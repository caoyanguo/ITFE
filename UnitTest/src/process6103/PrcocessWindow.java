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
	
	public JLabel label1 = new JLabel();//ѡ���ļ���ǩ
	public JTextField text1 = new JTextField();//�ļ�����·���ı���
	public JButton jButton1 = new JButton();//ѡ����Ҫ�����ļ��İ�ť
	
	public JLabel label2 = new JLabel();//���������ǩ
	public JTextField text2 = new JTextField();//���������ı���
	public JButton jButton2 = new JButton();//����XMl�ļ���ť
	public JButton jButton3 = new JButton();//����DEL�ļ���ť

	public PrcocessWindow() {
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(null);
		setBounds(300, 150, 700, 300);
		setTitle("���������ˮ����");

		// ѡ���ļ���ť��
		label1.setText("��ѡ���ļ���");
		label1.setBounds(50, 50, 100, 30);
		text1.setBounds(150, 50, 350, 30);
		jButton1.setText("���");
		jButton1.addActionListener(new SelectFileDirButtonListener(this));
		jButton1.setBounds(500, 50, 100, 30);


		label2.setText("�������룺");
		label2.setBounds(50, 100, 100, 30);
		text2.setBounds(150, 100, 450, 30);
		jButton2.setText("����");
		jButton2.addActionListener(new Process6103ButtonListener(this));
		jButton2.setBounds(200, 200, 100, 30);
		
		jButton3.setText("����");
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
