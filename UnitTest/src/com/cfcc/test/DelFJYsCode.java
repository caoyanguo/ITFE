package com.cfcc.test;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DelFJYsCode {
	public static void main(String[] args) {
		// args[0]shi di yi ge
		// args[1]shi di er ge
		String path = "E:\\itfe\\ssss";
		String delcon ="413000";
		String type = "sq";
		DelFJYsCode df = new DelFJYsCode();
		List<String> filelist = df.listFileAbspath(path);
		for (String file : filelist) {
			File newF = new File(file);
			try {
				StringBuffer sb = new StringBuffer("");
				List<String> strs = df.readFileWithLine(file, ",");
				if (type.toUpperCase().equals("ZJ")) {
					for (int i = 0; i < strs.size(); i++) {
						String data = strs.get(i);
						if (i == 0) {
							sb.append(data).append("\r\n");
						} else {
							String[] strArray = data.split(",");
							sb.append(strArray[0]).append(",").append(
									strArray[1]).append(",").append(
									strArray[2].replace(delcon, ""))
									.append(",").append(strArray[3])
									.append(",").append("\r\n");
						}
					}
					df.writeFile(file, sb.toString(), false);
				}else if(type.toUpperCase().equals("SQ")) {
					for (int i = 0; i < strs.size(); i++) {
						String data = strs.get(i);
						if (i == 0) {
							String[] strArray = data.split(",");
							sb.append(strArray[0]).append(",").append(
										strArray[1]).append(",").append(
										strArray[2]).append(",").append(
										strArray[3].replace(delcon, "")).append(",").append(
										strArray[4]).append(",").append(
										strArray[5]).append(",").append(
										strArray[6]).append(",").append(
										strArray[7]).append("\r\n");
						} else {
							sb.append(data).append("\r\n");							
						}
					}
					df.writeFile(file, sb.toString(), false);
				}

			} catch (Exception e) {
				File logfile = new File("c:\\fjlog.log");
				try {
					if (!logfile.exists()) {
						logfile.createNewFile();
					}
					df.writeFile("c:\\fjlog.log",
							e.toString(),true);
				} catch (Exception e1) {
				}
			}
		}
	}

	/**
	 * 按照行读取文件，每行根据split进行分割成字符串数组
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws Exception
	 */
	public List<String> readFileWithLine(String file, String split)
			throws Exception {
		List<String> listStr = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				// if(split.equals(data.substring(data.length()-1))){
				// data += " ";
				// }
				// String[] strArray = data.split(split);
				listStr.add(data);

			}
			br.close();
			return listStr;
		} catch (Exception e) {
			throw new Exception("读取文件出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					throw new Exception("读取文件出现异常", e);
				}

			}
		}

	}
	
	/**
	 * 获取路径下所有文件的绝对文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public List<String> listFileAbspath(String filePath) {
		List<String> listFile = new ArrayList<String>();
		listAbsPath(filePath, listFile);
		return listFile;
	}
	
	private void listAbsPath(String filePath, List<String> filesStr) {
		File tmp1 = new File(filePath);
		// 根据绝对路径进行删除操作
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		if (file.isFile()) {
			filesStr.add(str);
		} else {
			File[] f = file.listFiles();
			if (f == null || f.length <= 0) {
				filesStr.add(str);
			}
			for (int i = 0; i < f.length; i++) {
				File tmp = f[i];
				String strF = tmp.getAbsolutePath();
				if (tmp.isDirectory()) {

					listAbsPath(strF, filesStr);

				} else {
					filesStr.add(strF);
				}

			}
		}
	}
	
	/**
	 * 把指定的内容写入到指定的文件中
	 * 
	 * @param fileName
	 *            文件名
	 * @param fileContent
	 *            文件内容
	 * @param append
	 *            是否追加方式
	 * @throws Exception
	 *             操作文件失败
	 */
	public void writeFile(String fileName, String fileContent,boolean append)
			throws Exception {
		long lBegin = 0;

		File file = new File(fileName);
		File dir = new File(file.getParent());

		FileOutputStream output = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(fileName, append);
			output.write(fileContent.getBytes("GBK"));

		} catch (IOException e) {
			String msg = new String("写文件失败,IO错误." + fileName);
			throw new Exception(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
			throw new Exception(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {

				}
			}
		}
	}
}
