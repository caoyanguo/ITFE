package process6103;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.cfcc.jaf.core.loader.ContextFactory;

public class CallShell {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_02.xml");
	}
	private final static String db_win = "db2cmd.exe -c -w -i db2 -tvf ";
	private final static String db_os = "db2 -tvf ";
	
	private static String dbCommand() {
		String command = "";

		if (isWin()) {
			command = db_win;
		} else {
			command = db_os;
		}
		return command;
	}

	/**
	 * @return
	 */
	private static boolean isWin() {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") >=0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * �����ؽ������CallShell
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public static byte[] dbCallShellWithRes(String sqlFile) throws Exception {

		try {
			String command = dbCommand() + sqlFile;
			System.out.println("Shell:" + command);

			return callShellWithRes(command);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception(e.toString());

		}

	}
	
	
	/**
	 * �����ؽ������CallShell
	 * 
	 * @param command
	 *            ���õ�shell����
	 * @return ������Ϣ
	 * @throws Exception
	 */
	public static byte[] callShellWithRes(String command) throws Exception {

		try {

			System.out.println("Shell:" + command);

			Process child = Runtime.getRuntime().exec(command);

			InputStream in = child.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes = new byte[64 * 1024];
			int length = 0; // read content
			for (int i = in.read(bytes); i != -1; i = in.read(bytes)) {

				out.write(bytes, 0, i);

				length += i;
			}
			in.close();

			child.waitFor();
			return out.toByteArray();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception(e.toString());

		}

	}
	
	public static void main(String [] args){
		/*//���BasicDataSource�������ֵ
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
		String clearData=" import from D:\\empty.del of del replace into HTV_FIN_INCOME_DETAIL ;\r\n";
		//�������ݿ�SQL
		String exportData=" export to D:\\HTV_FIN_INCOME_DETAIL.del of del select * from HTV_FIN_INCOME_DETAIL ;\r\n";
		//���������ݿ�SQLд���ļ�exportData.sql����ִ��SQL
		String exportResult=CallShell.ProcessData(connectDb+exportData,"D:\\exportData.sql");
		System.out.println(exportResult);
		//���������ݿ�SQLд���ļ�clearData.sql����ִ��SQL
		String clearResult=CallShell.ProcessData(connectDb+clearData,"D:\\clearData.sql");
		System.out.println(clearResult);*/
		try {
			System.out.println(new String(callShellWithRes("ftp 10.1.3.117")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String ProcessData(String Sql,String fileName){
		String executeResult="";
		Exists(fileName);
		FileUtil.getInstance().writeFile(fileName, Sql);
		byte[] bytes=null;
		try {
			bytes= dbCallShellWithRes(fileName);
			executeResult=new String(bytes);
		} catch (Exception e) {
			System.out.println("����Shellִ�����ݿ�ű�ʱ����"+e.getMessage());
			e.printStackTrace();
		}
		new File(fileName).delete();
		return executeResult;
	}
	
	
	public static void Exists(String fileName){
		File file=new File(fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
