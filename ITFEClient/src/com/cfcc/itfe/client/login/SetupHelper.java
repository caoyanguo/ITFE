package com.cfcc.itfe.client.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.cfcc.jaf.core.invoker.http.HttpConfig;
import com.cfcc.jaf.core.loader.ContextFactory;

public class SetupHelper {
	
	private static  String sServerAddress;
	
	private static String sPort;
	
	private  static String sPortSSL;
	
	private static String loginSetupdiskFileName = "itfeloginSetupNew.obj";
	
	private static String loginSetupdiskFile = "itfeloginorgNew.obj";

	 /**
	 * @return the sServerAddress
	 */
	public static String getSServerAddress() {
		return sServerAddress;
	}

	/**
	 * @param serverAddress the sServerAddress to set
	 */
	public static void setSServerAddress(String serverAddress) {
		sServerAddress = serverAddress;
	}

	/**
	 * @return the sPort
	 */
	public static String getSPort() {
		return sPort;
	}

	/**
	 * @param port the sPort to set
	 */
	public static void setSPort(String port) {
		sPort = port;
	}

	/**
	 * @return the sPortSSL
	 */
	public static String getSPortSSL() {
		return sPortSSL;
	}

	/**
	 * @param portSSL the sPortSSL to set
	 */
	public static void setSPortSSL(String portSSL) {
		sPortSSL = portSSL;
	}

	/***************************************************************************
     * add by Lihf
     * 
     * @author fph
     */
    public static  void loginSetupWriteToDisk()
    {

        HttpConfig config = (HttpConfig) ContextFactory.getApplicationContext()
                .getBean("httpConfig");
        config.setHost(sServerAddress);

        config.setPort(Integer.valueOf(sPort).intValue());
        config.setSSL_Port(Integer.valueOf(sPortSSL).intValue());
        
        try
        {
            FileOutputStream fos = null;
            fos = new FileOutputStream(System.getProperty("user.home")
                    + System.getProperty("file.separator")
                    + loginSetupdiskFileName);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(sServerAddress);
            oos.writeObject(sPort);
            oos.writeObject(sPortSSL);
    
            
            oos.close();
            fos.close();
            
        } catch (Exception e)
        {
           e.printStackTrace();
        }
    }

    public static  void loginSetupReadFromDisk()
    {
        HttpConfig config = (HttpConfig) ContextFactory.getApplicationContext()
                .getBean("httpConfig");

        sServerAddress = config.getHost();

        sPort = Integer.toString(config.getPort());
        sPortSSL = Integer.toString(config.getSSL_Port());

       

        FileInputStream fis = null;
        try
        {
        	File f = new File(System.getProperty("user.home")+ System.getProperty("file.separator")+ loginSetupdiskFileName);
        	if(!f.exists())
        		return;
            fis = new FileInputStream(System.getProperty("user.home")
                    + System.getProperty("file.separator")
                    + loginSetupdiskFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            sServerAddress = (String) ois.readObject();
            sPort = (String) ois.readObject();
            sPortSSL = (String) ois.readObject();

         

            config.setHost(sServerAddress);
            config.setPort(Integer.valueOf(sPort).intValue());
            config.setSSL_Port(Integer.valueOf(sPortSSL).intValue());

          
        } catch (Exception e)
        {
        	 e.printStackTrace();
        }

    }
	
    public static  String  getloginorg()
    {   
        FileInputStream fis = null;
        String  defaultorg = null;
        String usercode = null;
        try
        {
        	String filename = System.getProperty("user.home")
            + System.getProperty("file.separator")
            + loginSetupdiskFile;
        	File f = new File(filename);
        	if (!f.exists()) {
				setloginorg("","");
			} 
            fis = new FileInputStream(System.getProperty("user.home")
                    + System.getProperty("file.separator")
                    + loginSetupdiskFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            defaultorg = (String) ois.readObject();
            usercode = (String)ois.readObject();
        } catch (Exception e)
        {
        	 e.printStackTrace();
        }
		return "*ORG*"+defaultorg+"-"+"*US*"+usercode;
    }
    
    public static  void  setloginorg(String orgcode,String usercode)
    {   
    	 try
         {
             FileOutputStream fos = null;
             fos = new FileOutputStream(System.getProperty("user.home")
                     + System.getProperty("file.separator")
                     + loginSetupdiskFile);

             ObjectOutputStream oos = new ObjectOutputStream(fos);
             oos.writeObject(orgcode);
             oos.writeObject(usercode);
             oos.close();
             fos.close();
         } catch (Exception e)
         {
            e.printStackTrace();
         }
    }
	
}
