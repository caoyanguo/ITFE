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
     * 从ftp服务器下载zip文件
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
		String localPathString = ITFECommonConstant.LOCALPATHSTRING;//"D:";//下载到本地的路径
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
    	String ftpIpString = ITFECommonConstant.FTPIPSTRING;//"10.1.5.181";//远程Ftp IP
    	String ftpUserString = ITFECommonConstant.FTPUSERSTRING;//"root";//远程Ftp登录用户
    	String ftpPassWordString = ITFECommonConstant.FTPPASSWORDSTRING;//"rootroot";//远程Ftp登录密码
    	String ftpPathString = ITFECommonConstant.FTPPATHSTRING;//"/itfe";//远程Ftp目录
    	String localPathString = ITFECommonConstant.LOCALPATHSTRING;//"D:";//下载到本地的路径
        /**   
          * 和服务器建立连接   
        */
        FtpClient ftp = new FtpClient(ftpIpString); //根据服务器ip建立连接                          
        String str = ftp.getResponseString(); //获得响应信息
        logger.info("连接服务器:" + str);
        /**   
          * 登陆到Ftp服务器   
        */
        ftp.login(ftpUserString, ftpPassWordString); //根据用户名和密码登录服务器
        str = ftp.getResponseString();
        logger.info("登录:"+str);
        /**   
         * 打开并定位到服务器目录   
        */
        if(ftpPathString.indexOf("\\")>0)
        {
        	serverSplit = "\\";
        	ftp.cd(ftpPathString+serverSplit+dateString); //打开服务器上的文件目录
        }
        else if(ftpPathString.indexOf("/")>=0)
        {
        	serverSplit = "/";
        	ftp.cd(ftpPathString+serverSplit+dateString); //打开服务器上的文件目录
        }
        else 
        	ftp.cd(ftpPathString+split+dateString); //打开服务器上的文件目录
        str = ftp.getResponseString() ;
        logger.info(ftp.pwd()+"打开服务器目录:"+str) ;
        ftp.binary();//转化为二进制的文件
        logger.info("读取目录"+ftp.pwd()+"下文件列表") ;
        List<String> ftpFileNameList = FtpUtil.getServerFileNameList(ftp,ftp.pwd(),orgcode);//得到ftp文件列表
        logger.info("检查本地是否已经读取过文件") ;
        List<String> fileNameList = FtpUtil.getLocalFileNameArray(ftpFileNameList);//去掉本地已经下载过的文件
        logger.info("有效文件的个数开始") ;
        logger.info("有效文件的个数"+fileNameList==null?0:fileNameList.size()) ;
        logger.info("有效文件的个数结束") ;
        if(fileNameList!=null&&fileNameList.size()>0)
        {
        	TelnetInputStream ftpIn = null;
        	FileOutputStream ftpOut = null;
        	byte[] buf = null;
        	String localzip = null;
        	String zipname = null;
        	List<IDto> dtoLists = new ArrayList<IDto>();
        	logger.info("得到文件列表中的文件开始") ;
        	for(int i=0;i<fileNameList.size();i++)
        	{
        		ftp.sendServer("quote PASV");
        		logger.info("得到文件列表中的文件"+(i+1)) ;
        		ftpIn = ftp.get(fileNameList.get(i));
        		logger.info("已得到文件列表中的文件"+(i+1)) ;
        		buf = new byte[204800];
                int bufsize = 0;
                zipname = fileNameList.get(i).substring(fileNameList.get(i).lastIndexOf(serverSplit)+1);
                localzip = localPathString+split+dateString+split+zipname;
                File dir = new File(localPathString+split+dateString);
				if(!dir.exists())
					dir.mkdirs();
                ftpOut = new FileOutputStream(localzip);
                logger.info("读取文件开始") ;
                while ((bufsize = ftpIn.read(buf, 0, buf.length)) != -1) {
                    ftpOut.write(buf, 0, bufsize);
                }
                logger.info("读取文件结束") ;
                ftpOut.close();
                ftpIn.close();
                dtoLists = FtpUtil.zipToFile(localzip, localPathString+split+dateString+split+zipname.substring(0,10)+split+zipname.substring(20,26)+split);
                if(dtoLists!=null&&dtoLists.size()>0)
                	DatabaseFacade.getDb().create(CommonUtil.listTArray(dtoLists));
        	}
        	ftp.closeServer();
        	logger.info("得到文件列表中的文件结束") ;
        }else if(ftpFileNameList!=null&&ftpFileNameList.size()>0) {
			throw new Exception("数据已经读取，无新数据");
		}else {
			throw new Exception("ftp服务器上没有数据");
		}
    }
   //    上传文件;并返回上传文件的信息
    @SuppressWarnings("unused")
	private static String upLoadZipToServer(String filename) throws Exception {
        String str; //输出信息字符串
        String timeStr = TimeFacade.getCurrentStringTime();//获得当前时间
        String recordStr = "上传时间:" + timeStr + "\r\n";//信息记录字符串
        /**   
         * 和服务器建立连接   
         */
        FtpClient ftp = new FtpClient("192.168.39.189"); //根据服务器ip建立连接                          
        str = ftp.getResponseString(); //获得响应信息
        System.out.println(str);
        recordStr += "连接服务器:" + str + "\r\n";
        /**   
         * 登陆到Ftp服务器   
         */
        ftp.login("test", "test"); //根据用户名和密码登录服务器
        str = ftp.getResponseString();
        System.out.println(str);
        recordStr += "登录:" + str + "\r\n";
        /**   
         * 打开并定位到test目录   
         */
        ftp.cd("uptest"); //打开服务器上的test文件夹
        ftp.binary();//转化为二进制的文件
        str = ftp.getResponseString();
        System.out.println(str);
 
        FileInputStream is = null ;
        TelnetOutputStream os = null ;
        try {
          //"upftpfile"用ftp上传后的新文件名
          os = ftp.put("uptest.zip");
          File file_in = new java.io.File(filename);
          if (file_in.length()==0) {
             return "上传文件为空!";
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
      return "上传文件成功!";
    }
 
    /**  
     * zip压缩功能,压缩sourceFile(文件夹目录)下所有文件，包括子目录
     * @param  sourceFile,待压缩目录; toFolerName,压缩完毕生成的目录
     * @throws Exception  
     */
	public static void fileToZip(String sourceFile, String toFolerName) throws Exception {
 
        List fileList = getSubFiles(new File(sourceFile)); //得到待压缩的文件夹的所有内容  
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
                toFolerName));
 
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        int readLen = 0;
        for (int i = 0; i < fileList.size(); i++) { //遍历要压缩的所有子文件  
            File file = (File) fileList.get(i);
            System.out.println("压缩到的文件名:" + file.getName());
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
        System.out.println("压缩完成!");
    }
 
    /**
     * 解压zip文件
     * @param sourceFile,待解压的zip文件; toFolder,解压后的存放路径
     * @throws Exception
     **/
    public static List<IDto> zipToFile(String sourceFile, String toFolder,String orgcode) throws Exception {
    	
        String toDisk = toFolder;//接收解压后的存放路径
        File base = new File(toFolder);
        String zipname = sourceFile.substring(sourceFile.lastIndexOf(split)+1);
        if(!base.exists())
        	base.mkdirs();
        ZipFile zfile = new ZipFile(sourceFile);//连接待解压文件
        System.out.println("要解压的文件是:" + zipname);
        
        Enumeration zList = zfile.getEntries();//得到zip包里的所有元素
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        List<IDto> dtoList = new ArrayList<IDto>();
        TvBatchpayDto dto = null;
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                System.out.println("打开zip文件里的文件夹:" + ze.getName()+ " skipped...");
                continue;
            }
            System.out.println("zip包里的文件: " + ze.getName() + "\t" + "大小为:"+ ze.getSize() + "KB");
 
            //以ZipEntry为参数得到一个InputStream，并写到OutputStream中
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
            	dto.setSstatus(StateConstant.FTPFILESTATE_SUMMARY);//汇总文件
            else if(dto.getSfilename().length()==34)
            	dto.setSstatus(StateConstant.FTPFILESTATE_DOWNLOAD);//可加载的文件
            else 
            	dto.setSstatus(StateConstant.FTPFILESTATE_UNKNOWN);//未知文件
            dto.setSofzipname(zipname);
            dto.setSzippath(sourceFile.substring(0,sourceFile.lastIndexOf(split)));
            dto.setTssysupdate(TSystemFacade.getDBSystemTime());
            dtoList.add(dto);
        }
        zfile.close();
        return dtoList;
    }
    /**
     * 解压zip文件
     * @param sourceFile,待解压的zip文件; toFolder,解压后的存放路径
     * @throws Exception
     **/
    public static List<IDto> zipToFile(String sourceFile, String toFolder) throws Exception {
    	
        String toDisk = toFolder;//接收解压后的存放路径
        File base = new File(toFolder);
        String zipname = sourceFile.substring(sourceFile.lastIndexOf(split)+1);
        if(!base.exists())
        	base.mkdirs();
        ZipFile zfile = new ZipFile(sourceFile);//连接待解压文件
        logger.info("要解压的文件是:" + zipname);
        
        Enumeration zList = zfile.getEntries();//得到zip包里的所有元素
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
            	 logger.info("打开zip文件里的文件夹:" + ze.getName()+ " skipped...");
                continue;
            }
            logger.info("zip包里的文件: " + ze.getName() + "\t" + "大小为:"+ ze.getSize() + "KB");
 
            //以ZipEntry为参数得到一个InputStream，并写到OutputStream中
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
            	dto.setSstatus(StateConstant.FTPFILESTATE_SUMMARY);//汇总文件
            else if(dto.getSfilename().length()==34)
            	dto.setSstatus(StateConstant.FTPFILESTATE_DOWNLOAD);//可加载的文件
            else 
            	dto.setSstatus(StateConstant.FTPFILESTATE_UNKNOWN);//未知文件
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
     * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.  
     * @param baseDir java.lang.String 根目录  
     * @param realFileName java.io.File 实际的文件名  
     * @return 相对文件名  
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
     * 取得指定目录下的所有文件列表，包括子目录.  
     * @param baseDir File 指定的目录  
     * @return 包含java.io.File的List  
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
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param zippath 指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    private static File getRealFileName(String zippath, String absFileName){
 
        String[] dirs = absFileName.split("/", absFileName.length());
        File ret = new File(zippath);// 创建文件对象
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {// 检测文件是否存在
            ret.mkdirs();// 创建此抽象路径名指定的目录
        }
        ret = new File(ret, dirs[dirs.length - 1]);// 根据 ret 抽象路径名和 child 路径名字符串创建一个新 File 实例
        return ret;
    }
    /**
     * 取得ftp服务器上某个目录下的所有文件名
     * @param ftp, FtpClient类实例; folderName,服务器的文件夹名
     * @throws Exception
     * @return list 某目录下文件名列表
     **/
	private static List getServerFileNameList(FtpClient ftp,String folderName,String orgcode) throws Exception{
		ftp.sendServer("quote PASV");
        BufferedReader dr = new BufferedReader(new InputStreamReader(ftp.nameList(folderName)));
        logger.info(ftp.pwd()+"目录下文件列表已读取") ;
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
     * 传入ftp文件列表判断本地是否已经下载过，返回未下载的文件列表
     * @param ftpFileNameList ftp文件名称列表
     * @return 未下载的文件列表
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
		logger.debug("检查文件完毕");
        return fileNameList;
    }
    public static void uploadZipFile(List dtoList) throws Exception {
    	 FileInputStream is = null ;
         TelnetOutputStream os = null;
    	try {
    		String ftpIpString = ITFECommonConstant.FTPIPSTRING;//远程Ftp IP"10.1.5.181";//
        	String ftpUserString = ITFECommonConstant.FTPUSERSTRING;//远程Ftp登录用户"root";//
        	String ftpPassWordString = ITFECommonConstant.FTPPASSWORDSTRING;//远程Ftp登录密码"rootroot";//
        	String ftpPathString = ITFECommonConstant.FTPUPLOADPATH;//上传到远程Ftp目录"/itfe/pbc";//
        	String localPathString = ITFECommonConstant.LOCALPATHSTRING;//"D:";//下载到本地的路径
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
    	       * 和服务器建立连接   
    	    */
    	    FtpClient ftp = new FtpClient(ftpIpString); //根据服务器ip建立连接                          
    	    String str = ftp.getResponseString(); //获得响应信息
    	    logger.info("连接服务器:" + str);
    	    /**   
    	      * 登陆到Ftp服务器   
    	    */
    	    ftp.login(ftpUserString, ftpPassWordString); //根据用户名和密码登录服务器
    	    str = ftp.getResponseString();
    	    logger.info("登录:"+str);
    	    /**   
    	      * 打开并定位到服务器目录   
    	    */
    	    createDirExists(ftp,ftpPathString); //打开服务器上的文件目录
    	    createDirExists(ftp,TimeFacade.getCurrentStringTime()); //打开服务器上的文件目录
    	    str = ftp.getResponseString() ;
    	    logger.info("打开服务器目录:"+ftp.pwd()) ;
    	    ftp.binary();
    	    ftp.sendServer("quote PASV");
    	    os =  ftp.put(zipname);
            File file_in = new java.io.File(zippath+zipname);
            if (file_in.length()==0) {
               throw new Exception( "上传文件为空!");
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
				Md5App md5 = new Md5App();// 不需要带密钥和异或的MD5加签
				StringBuffer strCA = null;
	    		for(int i=0;i<dtoList.size();i++)
	    		{
	    			dto = (TvBatchpayDto)dtoList.get(i);
	    			if(ffjgpath==null||"".equals(ffjgpath))
	    				ffjgpath = localPathString+split+"FFJG"+split+dto.getSorgcode()+split+TimeFacade.getCurrentStringTime()+split+"temp"+split;
	    			md5XOr = new Md5App(true, dto.getSkey());// 需要带密钥和异或功能
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