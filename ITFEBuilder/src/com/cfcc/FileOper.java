package com.cfcc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;

public class FileOper {
	public static void saveFile(String str, String fileName) {
		File f = new File(fileName);
		File dir = new File(f.getParent());
		OutputStream out = null;
		try {
			if(!dir.exists()){
				dir.mkdirs();
			}
			out = new FileOutputStream(f);
			out.write(str.getBytes());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
		}

	}
	public static List<String> listFile(String filePath){
		List<String> listFile = new ArrayList<String>();
		list(filePath,listFile);
		return listFile;
	}
	private static void list(String filePath,List<String> filesStr){
		File tmp1 = new File(filePath);
		// 根据绝对路径进行删除操作
		String str = tmp1.getAbsolutePath();
		String str1 = tmp1.getName();
		File file = new File(str);
		if (file.isFile()) {
			filesStr.add(str1);
		} else{
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++) {
				File tmp = f[i];
				String strF = tmp.getAbsolutePath();
				String strx = tmp.getName();
				if (tmp.isDirectory()) {
					
					list(strF,filesStr);

				}
				filesStr.add(strx);
			}
		}
	}
	
	public static List<String> listAbsFile(String filePath){
		List<String> listFile = new ArrayList<String>();
		listAbs(filePath,listFile);
		return listFile;
	}
	private static void listAbs(String filePath,List<String> filesStr){
		File tmp1 = new File(filePath);
		// 根据绝对路径进行删除操作
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		if (file.isFile()) {
			filesStr.add(str);
		} else{
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++) {
				File tmp = f[i];
				String strF = tmp.getAbsolutePath();
				if (tmp.isDirectory()) {
					
					listAbs(strF,filesStr);

				}else{
					filesStr.add(strF);
				}
				
			}
		}
	}
	public static String readFile(String fileName) throws FileOperateException {
		long lBegin = 0;
		
		FileInputStream input = null;
		String message = null;
		try {
			input = new FileInputStream(fileName);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[ITFECommonConstant.FILE_BUF_SIZE];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			byte[] data = byteArray.toByteArray();
			message = new String(data, ITFECommonConstant.CHARSET_NAME);

		} catch (FileNotFoundException e) {
			String msg = new String("读取文件失败,未找到文件." + fileName);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("读取文件失败,IO错误." + fileName);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("读取文件失败，运行时异常." + fileName);
			throw new FileOperateException(msg, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();

				}
			}
		}

	
		return message;

	}
	
	public static String readLineFile(String fileName) throws FileOperateException {
		long lBegin = 0;
		FileInputStream input = null;
		String message = null;
		try {
			input = new FileInputStream(fileName);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[ITFECommonConstant.FILE_BUF_SIZE];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			byte[] data = byteArray.toByteArray();
			message = new String(data, ITFECommonConstant.CHARSET_NAME);

		} catch (FileNotFoundException e) {
			String msg = new String("读取文件失败,未找到文件." + fileName);
			throw new FileOperateException(msg, e);
		} catch (IOException e) {
			String msg = new String("读取文件失败,IO错误." + fileName);
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("读取文件失败，运行时异常." + fileName);
			throw new FileOperateException(msg, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();

				}
			}
		}

	
		return message;

	}
}
