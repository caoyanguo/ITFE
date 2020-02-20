package testca.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

public class EncryptFacade {

	public static String xymEncrypt(String fileName,String desFileName) {
		String result = "";
		result += FileUtil.getInstance().readFile(fileName);
		result +="\r\n";
		result +=  TestCA.calculate_XYM(fileName, 0);
		FileUtil.getInstance().writeFile(desFileName, result);
		return "";
	}

	public static String tkEncrypt(String fileName, String key, String split,
			String desFileName) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		String msgInfo="";
		try {
			// 读文件
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName)));
			// 写文件
			File desFile = new File(desFileName);
			if(!desFile.exists()){
				desFile.createNewFile();
			}
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(desFileName)));

			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				String[] ss = data.split(split);
				String encryptData = TestCA.getMD5(ss[0], ss[6], ss[15],
						new BigDecimal(ss[13]), key);
				data += split + encryptData;
				bw.write(data);
				bw.newLine();
			}
			return msgInfo;
		} catch (IOException e) {
			msgInfo = "加密失败";
			return msgInfo;
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				msgInfo = "加密失败";
				return msgInfo;
			}
		}
	}
	
	public static String sbEncrypt(String fileName, String key, String split,
			String desFileName) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		String msgInfo = "";
		try {
			// 读文件
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName)));
			// 写文件
			File desFile = new File(desFileName);
			if(!desFile.exists()){
				desFile.createNewFile();
			}
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(desFileName)));

			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				String[] ss = data.split(split);
				String encryptData = TestCA.getMD5(ss[12], ss[7], ss[13],
						new BigDecimal(ss[11]), key);
				data += split + encryptData;
				bw.write(data);
				bw.newLine();
			}
			return msgInfo;
		} catch (IOException e) {
			msgInfo = "加密失败";
			return msgInfo;
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				msgInfo = "加密失败";
				return msgInfo;
			}
		}
	}
}
