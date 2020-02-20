package com.cfcc.test.testfile;  
  
import java.io.FileOutputStream;  
import java.util.Date;  
import javax.activation.DataHandler;  
import javax.activation.FileDataSource;  
import javax.xml.namespace.QName;  
import org.apache.axis2.addressing.EndpointReference;  
import org.apache.axis2.client.Options;  
import org.apache.axis2.rpc.client.RPCServiceClient;  
  
/* 
 * 仅适用于小附件上传、下载，10M以下。 
 */  
public class BlobRPCClient2  
{  
    public static void main(String[] args) throws Exception  
    {  
        RPCServiceClient serviceClient = new RPCServiceClient();  
        Options options = serviceClient.getOptions();  
        EndpointReference targetEPR = new EndpointReference("http://localhost:8080/axis2/services/BlobService");  
        options.setTo(targetEPR);  
         
        //=================测试文件上传==================================  
          
        String filePath = "f:\\head.jpg";  
        DataHandler dataHandler = new DataHandler(new FileDataSource(filePath));  
        
        //设置入参（1、文件名，2、DataHandler）  
        Object[] opAddEntryArgs = new Object[]{"我是上传的文件.jpg", dataHandler};  
          
        //返回值类型  
        Class [] classes = new Class[]{ Boolean.class };  
          
        //指定要调用的方法名及WSDL文件的命名空间  
        QName opAddEntry = new QName("http://ws.apache.org/axis2","uploadFile");  
       
        //执行文件上传  
        System.out.println(new Date()+" 文件上传开始");  
        Object returnValue = serviceClient.invokeBlocking(opAddEntry,opAddEntryArgs, classes)[0]; 
//        Object returnValue = serviceClient.invokeBlocking(null,null, null)[0]; 
        
        System.out.println(new Date()+" 文件上传结束，返回值="+returnValue);  
        
        //=================测试文件下载==================================  
  
        opAddEntry = new QName("http://ws.apache.org/axis2", "downloadFile");  
        opAddEntryArgs = new Object[]{};  
        classes =  new Class<?>[]{ DataHandler.class };  
          
        System.out.println(new Date()+" 文件下载开始");  
        DataHandler returnHandler = (DataHandler) serviceClient.invokeBlocking(opAddEntry, opAddEntryArgs, classes)[0];  
        FileOutputStream fileOutPutStream = new FileOutputStream("F:\\我是下载的文件.jpg");  
        returnHandler.writeTo(fileOutPutStream);  
        fileOutPutStream.flush();  
        fileOutPutStream.close();  
        System.out.println(new Date()+" 文件下载完成");  
    }  
}  
