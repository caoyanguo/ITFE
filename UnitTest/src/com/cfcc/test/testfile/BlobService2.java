package com.cfcc.test.testfile;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;  
import javax.activation.DataHandler;  
import javax.activation.FileDataSource;  
  
/* 
 * DataHandler处理方式 
 */  
public class BlobService2 {  
  
    /* 
     * 文件上传服务 
     */  
    public boolean uploadFile(String fileName,DataHandler dataHandler)  
    {  
        OutputStream os = null;  
        try{  
            os = new FileOutputStream("F:\\"+fileName);  
            dataHandler.writeTo(os);//大附件也会出现内存溢出  
            os.flush();  
        }catch (Exception e){  
            e.printStackTrace();  
            return false;  
        }finally{  
            try {  
                os.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }     
        }  
        return true;  
    }  
    /* 
     * 文件下载服务 
     */  
    public DataHandler downloadFile()  
    {  
        String filepath = "F:\\head.jpg";  
        DataHandler dataHandler = new DataHandler(new FileDataSource(filepath));  
        return dataHandler;  
    }  
}  
