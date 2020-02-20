package com.cfcc.itfe.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceSubDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
@SuppressWarnings("unchecked")
public class FtpUtil{
 
    /**
     * ��ftp����������zip�ļ�
     * @param 
     *@throws Exception
     **/
	private static String split = File.separator;//"/";
	private static String serverSplit = null;
	private static String dateString = TimeFacade.getCurrentStringTime();
	private static String piciStartDate = TimeFacade.getCurrentStringTime();
	private static String piciEndDate = TimeFacade.getCurrentStringTime();
	private static Log logger = LogFactory.getLog(FtpUtil.class);
	private static Map<String, String> treorgMap = new HashMap<String,String>();
	private static Map<String,List<TsTreasuryDto>> orgtreMap = new HashMap<String,List<TsTreasuryDto>>();
	private static Map<String,Integer> picimap = new HashMap<String,Integer>();
	static{
		List<TsOrganDto> orgList = null;
		try {
			orgList = DatabaseFacade.getDb().find(TsOrganDto.class);
		} catch (JAFDatabaseException e) {
			logger.error("ftp return pici init------"+e.toString());
		}
		String localPathString = ITFECommonConstant.LOCALPATHSTRING;//"D:";//���ص����ص�·��
		if(orgList!=null&&orgList.size()>0)
		{
			TsOrganDto dto = null;
			String path = null;
			File[] fileList = null;
			File file = null;
			File tempFile = null;
			for(int i=0;i<orgList.size();i++)
			{
				dto = orgList.get(i);
				path = localPathString+split+"FFJG"+split+dto.getSorgcode()+split+TimeFacade.getCurrentStringTime()+split;
				file = new File(path);
				if(file.exists())
				{
					fileList = file.listFiles();
					for(int j=0;j<fileList.length;j++)
					{
						tempFile = fileList[j];
						getPici(tempFile.getName().substring(0,10));
					}
				}
			}
		}
	}
    public static void downLoadZipFile(String sDate,String orgcode) throws Exception {
    	if(sDate!=null&&!"".equals(sDate))
    		dateString = sDate;
    	String ftpIpString = ITFECommonConstant.FTPIPSTRING;//"10.1.5.181";//Զ��Ftp IP
    	String ftpUserString = ITFECommonConstant.FTPUSERSTRING;//"root";//Զ��Ftp��¼�û�
    	String ftpPassWordString = ITFECommonConstant.FTPPASSWORDSTRING;//"rootroot";//Զ��Ftp��¼����
    	String ftpPathString = ITFECommonConstant.FTPPATHSTRING;//"/itfe";//Զ��FtpĿ¼
    	String localPathString = ITFECommonConstant.LOCALPATHSTRING;//"D:";//���ص����ص�·��
        /**   
          * �ͷ�������������   
        */
        FtpClient ftp = new FtpClient(ftpIpString); //���ݷ�����ip��������                          
        String str = ftp.getResponseString(); //�����Ӧ��Ϣ
        logger.info("���ӷ�����:" + str);
        /**   
          * ��½��Ftp������   
        */
        ftp.login(ftpUserString, ftpPassWordString); //�����û����������¼������
        str = ftp.getResponseString();
        logger.info("��¼:"+str);
        /**   
         * �򿪲���λ��������Ŀ¼   
        */
        if(ftpPathString.indexOf("\\")>0)
        {
        	serverSplit = "\\";
        	ftp.cd(ftpPathString+serverSplit+dateString); //�򿪷������ϵ��ļ�Ŀ¼
        }
        else if(ftpPathString.indexOf("/")>=0)
        {
        	serverSplit = "/";
        	ftp.cd(ftpPathString+serverSplit+dateString); //�򿪷������ϵ��ļ�Ŀ¼
        }
        else 
        	ftp.cd(ftpPathString+split+dateString); //�򿪷������ϵ��ļ�Ŀ¼
        str = ftp.getResponseString() ;
        logger.info(ftp.pwd()+"�򿪷�����Ŀ¼:"+str) ;
        ftp.binary();//ת��Ϊ�����Ƶ��ļ�
        logger.info("��ȡĿ¼"+ftp.pwd()+"���ļ��б�") ;
        List<String> ftpFileNameList = FtpUtil.getServerFileNameList(ftp,ftp.pwd(),orgcode);//�õ�ftp�ļ��б�
        logger.info("��鱾���Ƿ��Ѿ���ȡ���ļ�") ;
        List<String> fileNameList = FtpUtil.getLocalFileNameArray(ftpFileNameList);//ȥ�������Ѿ����ع����ļ�
        logger.info("��Ч�ļ��ĸ�����ʼ") ;
        logger.info("��Ч�ļ��ĸ���"+fileNameList==null?0:fileNameList.size()) ;
        logger.info("��Ч�ļ��ĸ�������") ;
        if(fileNameList!=null&&fileNameList.size()>0)
        {
        	TelnetInputStream ftpIn = null;
        	FileOutputStream ftpOut = null;
        	byte[] buf = null;
        	String localzip = null;
        	String zipname = null;
        	List<IDto> dtoLists = new ArrayList<IDto>();
        	logger.info("�õ��ļ��б��е��ļ���ʼ") ;
        	for(int i=0;i<fileNameList.size();i++)
        	{
        		ftp.sendServer("quote PASV");
        		logger.info("�õ��ļ��б��е��ļ�"+(i+1)) ;
        		ftpIn = ftp.get(fileNameList.get(i));
        		logger.info("�ѵõ��ļ��б��е��ļ�"+(i+1)) ;
        		buf = new byte[204800];
                int bufsize = 0;
                zipname = fileNameList.get(i).substring(fileNameList.get(i).lastIndexOf(serverSplit)+1);
                localzip = localPathString+split+dateString+split+zipname;
                File dir = new File(localPathString+split+dateString);
				if(!dir.exists())
					dir.mkdirs();
                ftpOut = new FileOutputStream(localzip);
                logger.info("��ȡ�ļ���ʼ") ;
                while ((bufsize = ftpIn.read(buf, 0, buf.length)) != -1) {
                    ftpOut.write(buf, 0, bufsize);
                }
                logger.info("��ȡ�ļ�����") ;
                ftpOut.close();
                ftpIn.close();
                dtoLists = FtpUtil.zipToFile(localzip, localPathString+split+dateString+split+zipname.substring(0,10)+split+zipname.substring(20,26)+split);
                if(dtoLists!=null&&dtoLists.size()>0)
                	DatabaseFacade.getDb().create(CommonUtil.listTArray(dtoLists));
        	}
        	ftp.closeServer();
        	logger.info("�õ��ļ��б��е��ļ�����") ;
        }else if(ftpFileNameList!=null&&ftpFileNameList.size()>0) {
			throw new Exception("�����Ѿ���ȡ����������");
		}else {
			throw new Exception("ftp��������û������");
		}
    }
   //    �ϴ��ļ�;�������ϴ��ļ�����Ϣ
    @SuppressWarnings("unused")
	private static String upLoadZipToServer(String filename) throws Exception {
        String str; //�����Ϣ�ַ���
        String timeStr = TimeFacade.getCurrentStringTime();//��õ�ǰʱ��
        String recordStr = "�ϴ�ʱ��:" + timeStr + "\r\n";//��Ϣ��¼�ַ���
        /**   
         * �ͷ�������������   
         */
        FtpClient ftp = new FtpClient("192.168.39.189"); //���ݷ�����ip��������                          
        str = ftp.getResponseString(); //�����Ӧ��Ϣ
        System.out.println(str);
        recordStr += "���ӷ�����:" + str + "\r\n";
        /**   
         * ��½��Ftp������   
         */
        ftp.login("test", "test"); //�����û����������¼������
        str = ftp.getResponseString();
        System.out.println(str);
        recordStr += "��¼:" + str + "\r\n";
        /**   
         * �򿪲���λ��testĿ¼   
         */
        ftp.cd("uptest"); //�򿪷������ϵ�test�ļ���
        ftp.binary();//ת��Ϊ�����Ƶ��ļ�
        str = ftp.getResponseString();
        System.out.println(str);
 
        FileInputStream is = null ;
        TelnetOutputStream os = null ;
        try {
          //"upftpfile"��ftp�ϴ�������ļ���
          os = ftp.put("uptest.zip");
          File file_in = new java.io.File(filename);
          if (file_in.length()==0) {
             return "�ϴ��ļ�Ϊ��!";
          }
          is = new FileInputStream(file_in);
          byte[] bytes = new byte[1024];
          int c;
          while ((c = is.read(bytes))!= -1) {
             os.write(bytes, 0, c);
          }
      }finally {
         if (is != null) {
            is.close();
         }
        if (os != null) {
          os.close();
        }
     }
      return "�ϴ��ļ��ɹ�!";
    }
 
    /**  
     * zipѹ������,ѹ��sourceFile(�ļ���Ŀ¼)�������ļ���������Ŀ¼
     * @param  sourceFile,��ѹ��Ŀ¼; toFolerName,ѹ��������ɵ�Ŀ¼
     * @throws Exception  
     */
	public static void fileToZip(String sourceFile, String toFolerName) throws Exception {
 
        List fileList = getSubFiles(new File(sourceFile)); //�õ���ѹ�����ļ��е���������  
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
                toFolerName));
 
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        int readLen = 0;
        for (int i = 0; i < fileList.size(); i++) { //����Ҫѹ�����������ļ�  
            File file = (File) fileList.get(i);
            System.out.println("ѹ�������ļ���:" + file.getName());
            ze = new ZipEntry(getAbsFileName(sourceFile, file));
            ze.setSize(file.length());
            ze.setTime(file.lastModified());
            zos.putNextEntry(ze);
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                zos.write(buf, 0, readLen);
            }
            is.close();
        }
        zos.close();
        System.out.println("ѹ�����!");
    }
 
    /**
     * ��ѹzip�ļ�
     * @param sourceFile,����ѹ��zip�ļ�; toFolder,��ѹ��Ĵ��·��
     * @throws Exception
     **/
    public static List<IDto> zipToFile(String sourceFile, String toFolder,String orgcode) throws Exception {
    	
        String toDisk = toFolder;//���ս�ѹ��Ĵ��·��
        File base = new File(toFolder);
        String zipname = sourceFile.substring(sourceFile.lastIndexOf(split)+1);
        if(!base.exists())
        	base.mkdirs();
        ZipFile zfile = new ZipFile(sourceFile);//���Ӵ���ѹ�ļ�
        System.out.println("Ҫ��ѹ���ļ���:" + zipname);
        
        Enumeration zList = zfile.getEntries();//�õ�zip���������Ԫ��
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        List<IDto> dtoList = new ArrayList<IDto>();
        TvBatchpayDto dto = null;
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                System.out.println("��zip�ļ�����ļ���:" + ze.getName()+ " skipped...");
                continue;
            }
            System.out.println("zip������ļ�: " + ze.getName() + "\t" + "��СΪ:"+ ze.getSize() + "KB");
 
            //��ZipEntryΪ�����õ�һ��InputStream����д��OutputStream��
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(getRealFileName(toDisk, ze.getName())));
            InputStream inputStream = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = inputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, readLen);
            }
            inputStream.close();
            outputStream.close();
            dto = new TvBatchpayDto();
            dto.setIno(Integer.valueOf(SequenceGenerator.getNextByDb2(SequenceName.QUANTITY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH)));
            dto.setSorgcode(orgcode);
            dto.setStrecode(zipname.substring(0,10));
            dto.setSdate(TimeFacade.getCurrentStringTime());
            dto.setScountycode(zipname.substring(22,28));
            dto.setSfilename(ze.getName().substring(ze.getName().indexOf(serverSplit)+1));
            if(ze.getName().indexOf(serverSplit)>=0)
            	dto.setSfilepath(toDisk+ze.getName().substring(0,ze.getName().indexOf(serverSplit))+split);
            else if(ze.getName().indexOf(split)>=0)
            	dto.setSfilepath(toDisk+ze.getName().substring(0,ze.getName().indexOf(split))+split);
            else
            	dto.setSfilepath(toDisk);
            if(dto.getSfilename().length()==30)
            	dto.setSstatus(StateConstant.FTPFILESTATE_SUMMARY);//�����ļ�
            else if(dto.getSfilename().length()==34)
            	dto.setSstatus(StateConstant.FTPFILESTATE_DOWNLOAD);//�ɼ��ص��ļ�
            else 
            	dto.setSstatus(StateConstant.FTPFILESTATE_UNKNOWN);//δ֪�ļ�
            dto.setSofzipname(zipname);
            dto.setSzippath(sourceFile.substring(0,sourceFile.lastIndexOf(split)));
            dto.setTssysupdate(TSystemFacade.getDBSystemTime());
            dtoList.add(dto);
        }
        zfile.close();
        return dtoList;
    }
    /**
     * ��ѹzip�ļ�
     * @param sourceFile,����ѹ��zip�ļ�; toFolder,��ѹ��Ĵ��·��
     * @throws Exception
     **/
    public static List<IDto> zipToFile(String sourceFile, String toFolder) throws Exception {
    	
        String toDisk = toFolder;//���ս�ѹ��Ĵ��·��
        File base = new File(toFolder);
        String zipname = sourceFile.substring(sourceFile.lastIndexOf(split)+1);
        if(!base.exists())
        	base.mkdirs();
        ZipFile zfile = new ZipFile(sourceFile);//���Ӵ���ѹ�ļ�
        logger.info("Ҫ��ѹ���ļ���:" + zipname);
        
        Enumeration zList = zfile.getEntries();//�õ�zip���������Ԫ��
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        List<IDto> dtoList = new ArrayList<IDto>();
        TvBatchpayDto dto = null;
        String trecode = null;
        String orgcode = null;
        SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
        List<TsTreasuryDto> trecodeList = null;
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
            	 logger.info("��zip�ļ�����ļ���:" + ze.getName()+ " skipped...");
                continue;
            }
            logger.info("zip������ļ�: " + ze.getName() + "\t" + "��СΪ:"+ ze.getSize() + "KB");
 
            //��ZipEntryΪ�����õ�һ��InputStream����д��OutputStream��
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(getRealFileName(toDisk, ze.getName())));
            InputStream inputStream = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = inputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, readLen);
            }
            inputStream.close();
            outputStream.close();
            dto = new TvBatchpayDto();
            trecode = zipname.substring(0,10);
            dto.setIno(Integer.valueOf(SequenceGenerator.getNextByDb2(SequenceName.QUANTITY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH)));
            orgcode = treorgMap.get(trecode);
            if(orgcode==null||"".equals(orgcode)||"null".equals(orgcode.toLowerCase()))
            {
	            trecodeList  = (List<TsTreasuryDto>)sqlExec.runQuery("select * from TS_TREASURY where S_TRECODE='"+trecode+"'",TsTreasuryDto.class).getDtoCollection();
	            if(trecodeList!=null)
	            {
	            	orgcode = trecodeList.get(0).getSorgcode();
	            	treorgMap.put(trecode, orgcode);
	            }
            }
            dto.setSorgcode(orgcode);
            dto.setStrecode(trecode);
            dto.setSdate(TimeFacade.getCurrentStringTime());
            dto.setScountycode(zipname.substring(20,26));
            if(ze.getName().indexOf(serverSplit)>=0)
            	dto.setSfilename(ze.getName().substring(ze.getName().indexOf(serverSplit)+1));
            else
            	dto.setSfilename(ze.getName());
            if(ze.getName().indexOf(serverSplit)>=0)
            	dto.setSfilepath(toDisk+ze.getName().substring(0,ze.getName().indexOf(serverSplit))+split);
            else if(ze.getName().indexOf(split)>=0)
            	dto.setSfilepath(toDisk+ze.getName().substring(0,ze.getName().indexOf(split))+split);
            else
            	dto.setSfilepath(toDisk);
            if(dto.getSfilename().length()==30)
            	dto.setSstatus(StateConstant.FTPFILESTATE_SUMMARY);//�����ļ�
            else if(dto.getSfilename().length()==34)
            	dto.setSstatus(StateConstant.FTPFILESTATE_DOWNLOAD);//�ɼ��ص��ļ�
            else 
            	dto.setSstatus(StateConstant.FTPFILESTATE_UNKNOWN);//δ֪�ļ�
            dto.setSofzipname(zipname);
            dto.setSzippath(sourceFile.substring(0,sourceFile.lastIndexOf(split)));
            dto.setTssysupdate(TSystemFacade.getDBSystemTime());
            dtoList.add(dto);
        }
        zfile.close();
        if(sqlExec!=null){
        	sqlExec.closeConnection();
        }
        return dtoList;
    }
    /**  
     * ������Ŀ¼��������һ���ļ��������·��������zip�ļ��е�·��.  
     * @param baseDir java.lang.String ��Ŀ¼  
     * @param realFileName java.io.File ʵ�ʵ��ļ���  
     * @return ����ļ���  
     */
    private static String getAbsFileName(String baseDir, File realFileName) {
        File real = realFileName;
        File base = new File(baseDir);
        String ret = real.getName();
        while (true) {
            real = real.getParentFile();
            if (real == null)
                break;
            if (real.equals(base))
                break;
            else
                ret = real.getName() + "/" + ret;
        }
        return ret;
    }
 
    /**  
     * ȡ��ָ��Ŀ¼�µ������ļ��б�������Ŀ¼.  
     * @param baseDir File ָ����Ŀ¼  
     * @return ����java.io.File��List  
     */
    private static List<File> getSubFiles(File baseDir) {
        List<File> ret = new ArrayList<File>();
        File[] tmp = baseDir.listFiles();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].isFile())
                ret.add(tmp[i]);
            if (tmp[i].isDirectory())
                ret.addAll(getSubFiles(tmp[i]));
        }
        return ret;
    }
 
    /**
     * ������Ŀ¼������һ�����·������Ӧ��ʵ���ļ���.
     * @param zippath ָ����Ŀ¼
     * @param absFileName ���·������������ZipEntry�е�name
     * @return java.io.File ʵ�ʵ��ļ�
     */
    private static File getRealFileName(String zippath, String absFileName){
 
        String[] dirs = absFileName.split("/", absFileName.length());
        File ret = new File(zippath);// �����ļ�����
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {// ����ļ��Ƿ����
            ret.mkdirs();// �����˳���·����ָ����Ŀ¼
        }
        ret = new File(ret, dirs[dirs.length - 1]);// ���� ret ����·������ child ·�����ַ�������һ���� File ʵ��
        return ret;
    }
    /**
     * ȡ��ftp��������ĳ��Ŀ¼�µ������ļ���
     * @param ftp, FtpClient��ʵ��; folderName,���������ļ�����
     * @throws Exception
     * @return list ĳĿ¼���ļ����б�
     **/
	private static List getServerFileNameList(FtpClient ftp,String folderName,String orgcode) throws Exception{
		ftp.sendServer("quote PASV");
        BufferedReader dr = new BufferedReader(new InputStreamReader(ftp.nameList(folderName)));
        logger.info(ftp.pwd()+"Ŀ¼���ļ��б��Ѷ�ȡ") ;
        List<String> list = new ArrayList<String>() ;
        String filename = null;
        String s = null;
        if(orgcode!=null&&!"".equals(orgcode)&&!"null".equals(orgcode.toLowerCase()))
        {
        	List treList = null;
        	TsTreasuryDto tredto = null;
        	treList = orgtreMap.get(orgcode);
        	if(treList==null||treList.size()<=0)
        	{
	        	tredto = new TsTreasuryDto();
	        	tredto.setSorgcode(orgcode);
	        	treList = CommonFacade.getODB().findRsByDto(tredto);
	        	orgtreMap.put(orgcode, treList);
        	}
        	StringBuffer treBuffer = new StringBuffer("");
        	if(treList!=null&&treList.size()>0)
        	{
        		for(int i=0;i<treList.size();i++)
        		{
        			tredto = (TsTreasuryDto)treList.get(i);
        			treBuffer.append(tredto.getStrecode()+",");
        		}
        	}
	        while((s=dr.readLine())!=null){
	        	filename = s.substring(s.lastIndexOf(serverSplit)+1);
	        	if(s.indexOf(".zip")>0&&filename.length()==33&&treBuffer.toString().indexOf(filename.substring(0,10)+",")>=0)
	        		list.add(s) ;
	        }
        }else {
        	 while((s=dr.readLine())!=null){
 	        	filename = s.substring(s.lastIndexOf(serverSplit)+1);
 	        	if(s.indexOf(".zip")>0&&filename.length()==33)
 	        		list.add(s) ;
 	        }
		}
        return list ;
    }
    /**
     * ����ftp�ļ��б��жϱ����Ƿ��Ѿ����ع�������δ���ص��ļ��б�
     * @param ftpFileNameList ftp�ļ������б�
     * @return δ���ص��ļ��б�
     * @throws JAFDatabaseException 
     * **/
    private static List<String> getLocalFileNameArray(List<String> ftpFileNameList) throws JAFDatabaseException{
        List<String> fileNameList = new ArrayList<String>();
        SQLExecutor sqlExec = null;
        try {
        	 if(ftpFileNameList!=null&&ftpFileNameList.size()>0)
             {
             	sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
             	String sqlString = " select * from TV_BATCHPAY where S_OFZIPNAME=?";
             	SQLResults rs = null;
             	for(int i=0;i<ftpFileNameList.size();i++)
             	{
             		sqlExec.clearParams();
             		sqlExec.addParam(ftpFileNameList.get(i).substring(ftpFileNameList.get(i).lastIndexOf(serverSplit)+1));
             		rs = sqlExec.runQuery(sqlString);
             		if(rs.getRowCount()<=0)
             			fileNameList.add(ftpFileNameList.get(i));
             	}
             	sqlExec.closeConnection();
             }
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw e;
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		logger.debug("����ļ����");
        return fileNameList;
    }
    public static void uploadZipFile(List dtoList) throws Exception {
    	 FileInputStream is = null ;
         TelnetOutputStream os = null;
    	try {
    		String ftpIpString = ITFECommonConstant.FTPIPSTRING;//Զ��Ftp IP"10.1.5.181";//
        	String ftpUserString = ITFECommonConstant.FTPUSERSTRING;//Զ��Ftp��¼�û�"root";//
        	String ftpPassWordString = ITFECommonConstant.FTPPASSWORDSTRING;//Զ��Ftp��¼����"rootroot";//
        	String ftpPathString = ITFECommonConstant.FTPUPLOADPATH;//�ϴ���Զ��FtpĿ¼"/itfe/pbc";//
        	String localPathString = ITFECommonConstant.LOCALPATHSTRING;//"D:";//���ص����ص�·��
        	TvBatchpayDto dto = null;
        	if(dtoList!=null&&dtoList.size()>0)
        		dto = (TvBatchpayDto)dtoList.get(0);
        	else
        		return;
        	String filepathString = writeFile(dtoList, localPathString);
        	String pici = getPici(dto.getStrecode());
        	String zippath = filepathString.substring(0,filepathString.lastIndexOf(split));
        	zippath = zippath.replace("temp", "");
        	String zipname = dto.getStrecode()+"_"+TimeFacade.getCurrentStringTime()+"_"+dto.getScountycode()+"_"+pici+".zip";
        	fileToZip(filepathString, zippath+zipname);
    	    /**   
    	       * �ͷ�������������   
    	    */
    	    FtpClient ftp = new FtpClient(ftpIpString); //���ݷ�����ip��������                          
    	    String str = ftp.getResponseString(); //�����Ӧ��Ϣ
    	    logger.info("���ӷ�����:" + str);
    	    /**   
    	      * ��½��Ftp������   
    	    */
    	    ftp.login(ftpUserString, ftpPassWordString); //�����û����������¼������
    	    str = ftp.getResponseString();
    	    logger.info("��¼:"+str);
    	    /**   
    	      * �򿪲���λ��������Ŀ¼   
    	    */
    	    createDirExists(ftp,ftpPathString); //�򿪷������ϵ��ļ�Ŀ¼
    	    createDirExists(ftp,TimeFacade.getCurrentStringTime()); //�򿪷������ϵ��ļ�Ŀ¼
    	    str = ftp.getResponseString() ;
    	    logger.info("�򿪷�����Ŀ¼:"+ftp.pwd()) ;
    	    ftp.binary();
    	    ftp.sendServer("quote PASV");
    	    os =  ftp.put(zipname);
            File file_in = new java.io.File(zippath+zipname);
            if (file_in.length()==0) {
               throw new Exception( "�ϴ��ļ�Ϊ��!");
            }
            is = new FileInputStream(file_in);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes))!= -1) {
               os.write(bytes, 0, c);
            }
            is.close();
            os.close();
            for(int i=0;i<dtoList.size();i++)
            {
            	dto = (TvBatchpayDto)dtoList.get(i);
            	dto.setSstatus(StateConstant.FTPFILESTATE_RETURN);
            }
            DatabaseFacade.getDb().update(CommonUtil.listTArray(dtoList));
            FileUtils.deleteDirectory(new File(filepathString.substring(0,filepathString.lastIndexOf(split))));
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}finally
		{
			if(is!=null)
				is.close();
			if(os!=null)
				os.close();
		}
    }
	private static String writeFile(List dtoList,String localPathString) throws Exception
	{
		SQLExecutor sqlExec = null;
		String ffjgpath = null;
		try {
			if(dtoList!=null&&dtoList.size()>0)
	    	{
	    		TvBatchpayDto dto = null;
	    		List<TvPayoutfinanceMainDto> mainList = null;
	    		TvPayoutfinanceMainDto mainDto = null;
	    		List<TvPayoutfinanceSubDto> subList = null;
	    		TvPayoutfinanceSubDto subDto = null;
	    		StringBuffer fileContent = new StringBuffer("");
	    		StringBuffer tempBuffer = null;
	    		sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
	    		String ffjgname = null;
				File file = null;
				Md5App md5XOr = null;
				Md5App md5 = new Md5App();// ����Ҫ����Կ������MD5��ǩ
				StringBuffer strCA = null;
	    		for(int i=0;i<dtoList.size();i++)
	    		{
	    			dto = (TvBatchpayDto)dtoList.get(i);
	    			if(ffjgpath==null||"".equals(ffjgpath))
	    				ffjgpath = localPathString+split+"FFJG"+split+dto.getSorgcode()+split+TimeFacade.getCurrentStringTime()+split+"temp"+split;
	    			md5XOr = new Md5App(true, dto.getSkey());// ��Ҫ����Կ�������
	    			mainList = (List<TvPayoutfinanceMainDto>)sqlExec.runQuery("select * from TV_PAYOUTFINANCE_MAIN where s_filename='"+dto.getSfilename()+"'", TvPayoutfinanceMainDto.class).getDtoCollection();
	    			strCA = new StringBuffer();
	    			if(mainList!=null&&mainList.size()>0)
	    			{
	    				for(int j=0;j<mainList.size();j++)
	    				{
	    					mainDto = mainList.get(j);
	    					subList = (List<TvPayoutfinanceSubDto>)sqlExec.runQuery("select * from TV_PAYOUTFINANCE_SUB where I_VOUSRLNO="+mainDto.getIvousrlno(),TvPayoutfinanceSubDto.class).getDtoCollection();
	    					if(subList!=null&&subList.size()>0)
	    					{
	    						for(int k=0;k<subList.size();k++)
	    						{
	    							subDto = subList.get(k);
	    							tempBuffer = new StringBuffer("");
	    							tempBuffer.append(subDto.getNsubamt()+",");
	    							tempBuffer.append(mainDto.getSpayeracct()+",");
	    							tempBuffer.append(mainDto.getSpayername()+",");
	    							tempBuffer.append(mainDto.getSpayeraddr()+",");
	    							tempBuffer.append(subDto.getSpayeeopnbnkno()+",");
	    							tempBuffer.append(subDto.getSpayeeacct()+",");
	    							tempBuffer.append(subDto.getSpayeename()+",");
	    							tempBuffer.append(subDto.getSpayeeaddr()+",");
	    							tempBuffer.append(mainDto.getSaddword()+",");
	    							if("80000".equals(mainDto.getSstatus()))
	    								tempBuffer.append("1,");
	    							else if("800001".equals(mainDto.getSstatus()))
	    								tempBuffer.append("0,");
	    							else 
	    								tempBuffer.append(",");
	    							tempBuffer.append(" "+mainDto.getSresult()+System.getProperty("line.separator"));
	    							strCA.append(md5XOr.makeMd5(tempBuffer.toString()));
	    							fileContent.append(tempBuffer.toString());
	    						}
	    					}
	    				}
	    			}
	    			fileContent.append("<CA>"+System.getProperty("line.separator"));
	    			fileContent.append(md5.makeMd5(strCA.toString())+System.getProperty("line.separator"));
	    			fileContent.append("</CA>"+System.getProperty("line.separator"));
	    			ffjgname = "FFJG"+dto.getSfilename();
	    			file = new File(ffjgpath+ffjgname);
	    			if(!file.getParentFile().exists())
	    				file.getParentFile().mkdirs();
	    			FileUtils.writeStringToFile(file, fileContent.toString(), "GBK");
	    		}
	    	}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return ffjgpath;
	}
	public static List queryFtpReturnFileList(TvBatchpayDto queryDto)	throws ITFEBizException {
		List queryList = null;
		SQLExecutor sqlExec = null;
		try{
			if(queryDto!=null)
			{
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				StringBuffer sql = new StringBuffer("SELECT * FROM TV_BATCHPAY a WHERE length(a.S_FILENAME)=? AND a.S_STATUS=? and S_DATE=? and");
				sql.append(" (select max(b.S_FILENAME) FROM TV_PAYOUTFINANCE_MAIN b WHERE b.S_FILENAME=a.S_FILENAME AND b.S_BOOKORGCODE = a.s_orgcode) IS NOT NULL and");
				sqlExec.addParam(34);
				sqlExec.addParam(StateConstant.FTPFILESTATE_ADDLOAD);
				sqlExec.addParam(queryDto.getSdate());
				if(queryDto.getStrecode()!=null&&!"".equals(queryDto.getStrecode())&&!"null".equals(queryDto.getStrecode().toLowerCase()))
				{
					sql.append(" S_TRECODE=? and ");
					sqlExec.addParam(queryDto.getStrecode());
				}
				sql.append("((SELECT count(*) FROM TV_PAYOUTFINANCE_MAIN b WHERE b.S_FILENAME=a.S_FILENAME AND b.S_BOOKORGCODE = a.s_orgcode)=");
				sql.append("(SELECT count(*) FROM TV_PAYOUTFINANCE_MAIN b WHERE b.S_FILENAME=a.S_FILENAME AND b.S_BOOKORGCODE = a.s_orgcode AND b.S_STATUS IN('80000','80001')))");
				queryList = (List)sqlExec.runQuery(sql.toString(),TvBatchpayDto.class).getDtoCollection();
				sqlExec.closeConnection();
			}
		}catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return queryList;
		}
	private static String getPici(String mapkey)
	{
		String getString = "";
		if(mapkey!=null)
		{
			piciEndDate = TimeFacade.getCurrentStringTime();
			Integer pici = picimap.get(mapkey);
			if(pici==null)
			{
				pici = 1;
				getString = "0"+pici;
				picimap.put(mapkey, pici);
			}else
			{
				if(piciStartDate.equals(piciEndDate))
				{
					pici++;
					picimap.put(mapkey, pici);
					if(pici<10)
						getString = "0"+pici;
					else
						getString = String.valueOf(pici);
				}else
				{
					Integer resetpici = 1;
					getString = "0"+resetpici;
					picimap.put(mapkey, resetpici);
					piciStartDate = piciEndDate;
				}
			}
		}
		return getString;
	}
    private static boolean createDirExists(FtpClient ftp,String ftpPath)
    {
    	boolean isexists = false;
    	if(ftp!=null&&ftpPath!=null)
    	{
    		try {
				ftp.cd(ftpPath);
				isexists = true;
			} catch (IOException e) {
		        try {
		        	ftp.ascii();
		        	ftp.sendServer("mkd " + ftpPath + "\r\n");
		        	ftp.readServerResponse();
		        	ftp.cd(ftpPath);
		        	ftp.binary();
		        	isexists = true;
		        } catch (Exception ex) {
		        	logger.error(ex);
		        		isexists = false;
		        }
			}
    	}
    	return isexists;
    }
    public static void main(String args[]){
//    	String filenameString = "200000000002_20140226_000000_02.zip";
//    	System.out.println(filenameString.substring(0,12));
//    	System.out.println(filenameString.substring(22,28));
        try {
            FtpUtil.zipToFile("E:/workspace/2000000000_20140317_111111_02.zip","E:/workspace") ;
        	FtpUtil.uploadZipFile(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
} 