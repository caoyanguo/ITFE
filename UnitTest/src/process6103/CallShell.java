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
	 * 带返回结果集的CallShell
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
	 * 带返回结果集的CallShell
	 * 
	 * @param command
	 *            调用的shell命令
	 * @return 调用信息
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
		/*//获得BasicDataSource类的属性值
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
		String clearData=" import from D:\\empty.del of del replace into HTV_FIN_INCOME_DETAIL ;\r\n";
		//导出数据库SQL
		String exportData=" export to D:\\HTV_FIN_INCOME_DETAIL.del of del select * from HTV_FIN_INCOME_DETAIL ;\r\n";
		//将导出数据库SQL写入文件exportData.sql，并执行SQL
		String exportResult=CallShell.ProcessData(connectDb+exportData,"D:\\exportData.sql");
		System.out.println(exportResult);
		//将清理数据库SQL写入文件clearData.sql，并执行SQL
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
			System.out.println("调用Shell执行数据库脚本时出错！"+e.getMessage());
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
