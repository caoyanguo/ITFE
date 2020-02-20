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
 * ��������С�����ϴ������أ�10M���¡� 
 */  
public class BlobRPCClient2  
{  
    public static void main(String[] args) throws Exception  
    {  
        RPCServiceClient serviceClient = new RPCServiceClient();  
        Options options = serviceClient.getOptions();  
        EndpointReference targetEPR = new EndpointReference("http://localhost:8080/axis2/services/BlobService");  
        options.setTo(targetEPR);  
         
        //=================�����ļ��ϴ�==================================  
          
        String filePath = "f:\\head.jpg";  
        DataHandler dataHandler = new DataHandler(new FileDataSource(filePath));  
        
        //������Σ�1���ļ�����2��DataHandler��  
        Object[] opAddEntryArgs = new Object[]{"�����ϴ����ļ�.jpg", dataHandler};  
          
        //����ֵ����  
        Class [] classes = new Class[]{ Boolean.class };  
          
        //ָ��Ҫ���õķ�������WSDL�ļ��������ռ�  
        QName opAddEntry = new QName("http://ws.apache.org/axis2","uploadFile");  
       
        //ִ���ļ��ϴ�  
        System.out.println(new Date()+" �ļ��ϴ���ʼ");  
        Object returnValue = serviceClient.invokeBlocking(opAddEntry,opAddEntryArgs, classes)[0]; 
//        Object returnValue = serviceClient.invokeBlocking(null,null, null)[0]; 
        
        System.out.println(new Date()+" �ļ��ϴ�����������ֵ="+returnValue);  
        
        //=================�����ļ�����==================================  
  
        opAddEntry = new QName("http://ws.apache.org/axis2", "downloadFile");  
        opAddEntryArgs = new Object[]{};  
        classes =  new Class<?>[]{ DataHandler.class };  
          
        System.out.println(new Date()+" �ļ����ؿ�ʼ");  
        DataHandler returnHandler = (DataHandler) serviceClient.invokeBlocking(opAddEntry, opAddEntryArgs, classes)[0];  
        FileOutputStream fileOutPutStream = new FileOutputStream("F:\\�������ص��ļ�.jpg");  
        returnHandler.writeTo(fileOutPutStream);  
        fileOutPutStream.flush();  
        fileOutPutStream.close();  
        System.out.println(new Date()+" �ļ��������");  
    }  
}  
