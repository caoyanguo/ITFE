package com.cfcc.test.testfile;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;  
import javax.activation.DataHandler;  
import javax.activation.FileDataSource;  
  
/* 
 * DataHandler����ʽ 
 */  
public class BlobService2 {  
  
    /* 
     * �ļ��ϴ����� 
     */  
    public boolean uploadFile(String fileName,DataHandler dataHandler)  
    {  
        OutputStream os = null;  
        try{  
            os = new FileOutputStream("F:\\"+fileName);  
            dataHandler.writeTo(os);//�󸽼�Ҳ������ڴ����  
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
     * �ļ����ط��� 
     */  
    public DataHandler downloadFile()  
    {  
        String filepath = "F:\\head.jpg";  
        DataHandler dataHandler = new DataHandler(new FileDataSource(filepath));  
        return dataHandler;  
    }  
}  
