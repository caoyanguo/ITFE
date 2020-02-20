package com.cfcc.itfe.util;


  
import java.io.BufferedInputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.util.zip.CRC32;  
import java.util.zip.CheckedOutputStream;  
import java.util.zip.ZipEntry;  
import java.util.zip.ZipOutputStream;  
  
public class ZipCompressorBack {   
      
    static final int BUFFER = 8192;  
  
    private File zipFile;  
  
    public ZipCompressorBack(String pathName) {   
            zipFile = new File(pathName);   
    }  
  
    public void compress(String srcPathName) {   
        File file = new File(srcPathName);   
        if (!file.exists())   
            throw new RuntimeException(srcPathName + "�����ڣ�");   
        try {   
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);   
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,   
                    new CRC32());   
            ZipOutputStream out = new ZipOutputStream(cos);   
            String basedir = "";   
            compress(file, out, basedir);   
            out.close();   
        } catch (Exception e) {   
            throw new RuntimeException(e);   
        }   
    }  
  
    private void compress(File file, ZipOutputStream out, String basedir) {   
        /* �ж���Ŀ¼�����ļ� */   
        if (file.isDirectory()) {   
            System.out.println("ѹ����" + basedir + file.getName());   
            this.compressDirectory(file, out, "");   
        } else {   
            System.out.println("ѹ����" + basedir + file.getName());   
            this.compressFile(file, out, "");   
        }   
    }  
  
    /** ѹ��һ��Ŀ¼ */   
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {   
        if (!dir.exists())   
            return;  
  
        File[] files = dir.listFiles();   
        for (int i = 0; i < files.length; i++) {   
            /* �ݹ� */   
            compress(files[i], out, basedir + dir.getName() + "/");   
        }   
    }  
  
    /** ѹ��һ���ļ� */   
    private void compressFile(File file, ZipOutputStream out, String basedir) {   
        if (!file.exists()) {   
            return;   
        }   
        try {   
        	if(!file.getName().endsWith(".zip")) {
        		BufferedInputStream bis = new BufferedInputStream(   
                        new FileInputStream(file));   
                ZipEntry entry = new ZipEntry(basedir + file.getName());   
                out.putNextEntry(entry);   
                int count;   
                byte data[] = new byte[BUFFER];   
                while ((count = bis.read(data, 0, BUFFER)) != -1) {   
                    out.write(data, 0, count);   
                }   
                bis.close();   
        	}
        } catch (Exception e) {   
            throw new RuntimeException(e);   
        }   
    }   

    /**�yԇ�Ƿ���ԉ��s*/  
    public static void main(String[] args) {   
        ZipCompressorBack zc = new ZipCompressorBack("D:\\szhzip\\02000000000\\02000000000020111031143503.zip");   
        zc.compress("D:\\szhzip\\02000000000\\");   
        System.out.println("OK....");  
    }  
}