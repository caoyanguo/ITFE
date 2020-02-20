package com.cfcc.test.test;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cfcc.itfe.security.CASecurityUtil;
import com.cfcc.itfe.security.DESPlus;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

public class TestInt {
	
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}
	
	public static void main(String[] argv) throws Exception {
//		String srcfile = "c:\\testencrypt.txt";
//		String filecontent = FileUtil.getInstance().readFile(srcfile);
//		
//		String signfile = CASecurityUtil.getXmlSign(filecontent);
//		
//		CASecurityUtil.verifySignDN(signfile);
		Pattern payTypePattern = Pattern.compile("[0-9]{2,6}");// 匹配2-位数字
		Matcher match  = payTypePattern.matcher("001001");
		if (match.matches() == false) {
			System.out.println(false);
		}else{
			System.out.println(true);
		}
		String aa= "国库前置资料1文件a";
		System.out.println(aa.length());
		System.out.print(aa.substring(0, 8));
		
//		String content = encrypt("testhz");
//		decrypt("testhz53", content);
	}
	
	public static void decrypt(String key,String srccontent) throws Exception{
		DESPlus des = new DESPlus(key);
		System.out.println("==============>" + des.decrypt(srccontent));
	}
	
	public static String encrypt(String key) throws Exception{
		String srcfile = "D:\\国库前置资料文件\\杭州\\测试数据\\payout\\2009101611170_bak.xml";
		String desfile = "D:\\国库前置资料文件\\杭州\\测试数据\\payout\\2009101611170.xml";
		
		DESPlus des = new DESPlus(key);
		String filecontent = FileUtil.getInstance().readFile(srcfile);
		
		String str = des.encrypt(filecontent);
		return str;
		
//		FileUtil.getInstance().writeFile(desfile, str);
//		
//		System.out.println(des.decrypt(str));
////		int count = 112 ;
////		for(int i = 0 ; i < count ; i++){
////			if( ((i+1) %10 == 0) || (i+1) == count){
////				System.out.println("=========" + i);
////			}
////		}
	}
	
	public void test1(){
		boolean isok = false;

		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		String url = "jdbc:db2://10.1.3.112:61000/tmisrpdb";
		Integer PO_I_RETCODE = 10;
		String PO_S_RETTEXT = "";
		String PO_S_RETINFO = "";
		Connection con = null;
		try {
			// con = DriverManager.getConnection( url, "db2admin","db2admin" );
			con = DriverManager.getConnection(url, "db2inst2", "tmis1234");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CallableStatement cs = null;
		try {
			int i = 0;
			cs = con.prepareCall("{call PRC_STAT_INC_PAY_SHT('2009-06-30','3','130','0000000000','1','1','0','0','1',?,?)}");

			cs.registerOutParameter(1,Types.INTEGER );
			cs.registerOutParameter(2, Types.VARCHAR);
//			cs.registerOutParameter(3, Types.VARCHAR);
			cs.execute();
			
			String address=cs.getString(2); //获得输出参数 
			ResultSet rs = cs.getResultSet();
			while (rs.next()) {
				System.out.println(rs.getString(3));
			}

			

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (cs != null)
					cs.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// db.disconnect();
		}
	}
	
	public void test(){
		String ss = "<value>7877|505|2009|413632|1|A33005|省旅游信息中心|2160599|其他旅游业管理与服务支出|1.|预算内|-5000.00</value>";
		int i = 1299999999;
		
		System.out.println(ss.toLowerCase());
	}
}
