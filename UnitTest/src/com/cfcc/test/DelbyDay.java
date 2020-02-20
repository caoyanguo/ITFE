package com.cfcc.test;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class DelbyDay {
	public static void main(String[] args) {
		DelbyDay del = new DelbyDay();
		del.deleteFileWithDays("C:\\client\\errInfo", 1);
	}
	
	public boolean deleteFileWithDays(String dirPath, int days) {
        Date pointDate = new Date();   
        long timeInterval = pointDate.getTime() - convertDaysToMilliseconds(days);   
        pointDate.setTime(timeInterval);  
  
        // 设置文件过滤条件   
        IOFileFilter timeFileFilter = FileFilterUtils.ageFileFilter(pointDate, true);   
        IOFileFilter fileFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, timeFileFilter);   
  
        // 删除符合条件的文件   
        File deleteRootFolder = new File(dirPath);   
        Iterator itFile = FileUtils.iterateFiles(deleteRootFolder, fileFiles, TrueFileFilter.INSTANCE);   
        while (itFile.hasNext()) {   
            File file = (File) itFile.next();   
            boolean result = file.delete();   
            if (!result) {   
                return false;   
            }   
        } 
  
        return true;   

	}
	
	 /**  
     * 天与毫秒的转换  
     *   
     * @param days  
     * @return  
     */  
    private static long convertDaysToMilliseconds(int days) {   
        return days * 24L * 3600 * 1000;   
    }
}
