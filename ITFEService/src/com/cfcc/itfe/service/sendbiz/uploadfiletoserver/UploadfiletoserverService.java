package com.cfcc.itfe.service.sendbiz.uploadfiletoserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import org.apache.commons.logging.*;
import com.cfcc.itfe.service.sendbiz.uploadfiletoserver.AbstractUploadfiletoserverService;
import com.cfcc.itfe.util.FileRootPathUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.exception.ITFEBizException;
/**
 * @author zhangliang
 * @time   17-06-06 13:55:58
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class UploadfiletoserverService extends AbstractUploadfiletoserverService {
	private static Log log = LogFactory.getLog(UploadfiletoserverService.class);

	public void deletefile(Map parammap) throws ITFEBizException {
		
		if(parammap!=null)
		{
			String rootpath=FileRootPathUtil.getInstance().getRoot();
			String trecode = String.valueOf(parammap.get("trecode"));
			List filelist = (List)parammap.get("filelist");
			String filedir = rootpath+"filetoserver"+"/"+trecode+"/";
			if(filelist!=null&&filelist.size()>0)
			{
				Map filemap = null;
				File file = null;
				for(int i=0;i<filelist.size();i++)
				{
					filemap = (Map)filelist.get(i);
					if(!String.valueOf(filemap.get("trecode")).contains("itfelogs"))
					{
						file = new File(filedir+filemap.get("filename"));
						if(file.exists())
							file.delete();
					}
				}
			}
		}
	}

	public void downloadfile(Map parammap) throws ITFEBizException {
		
		String rootpath=FileRootPathUtil.getInstance().getRoot();
		String trecode = String.valueOf(parammap.get("trecode"));
		if("itfelogs".equals(trecode))
		{
			File oldFile = new File(rootpath.replace("/itfe/root/temp/", "")+"/itfe/logs/"+parammap.get("filename"));
			File newFile = new File(rootpath+parammap.get("filename"));
			if(newFile.exists())
				newFile.delete();
			FileUtil.copyFile(oldFile.getAbsolutePath(), newFile.getAbsolutePath());
		}
	}

	public List searchfile(Map parammap) throws ITFEBizException {
		List filelist = null;
		if(parammap!=null)
		{
			String rootpath=FileRootPathUtil.getInstance().getRoot();
			String trecode = String.valueOf(parammap.get("trecode"));
			String filedir = rootpath+"filetoserver"+"/"+trecode+"/";
			filelist = getDirFileList(filedir,trecode);
			String serverlogpath = String.valueOf(parammap.get("serverlogpath"));
			if(serverlogpath!=null&&serverlogpath.contains("/itfe/logs"))
			{
				File itfelog = new File(rootpath.replace("/itfe/root/temp/", "")+serverlogpath);
				File[] logs = itfelog.listFiles();
				if(logs!=null&&logs.length>0)
				{
					Map<String,String> filemap = null;
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					for(int i=0;i<logs.length;i++)
					{
						if(logs[i].getName().startsWith("Server.log"))
						{
							filemap = new HashMap<String,String>();
							filemap.put("trecode", "itfelogs");
							filemap.put("filename", logs[i].getName());
							filemap.put("filesize", String.valueOf(logs[i].length()));
							filemap.put("filetime", String.valueOf(df.format(logs[i].lastModified())));
							filelist.add(filemap);
						}
					}
				}
				
			}
		}
		return filelist;
	}
	private List<Map<String,String>> getDirFileList(String filePath,String trecode) {
		File tmp1 = new File(filePath);
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		List<Map<String,String>> filesStr = new ArrayList<Map<String,String>>(); 
		Map<String,String> filemap = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (file.isFile()) {
			filemap = new HashMap<String,String>();
			filemap.put("trecode", trecode);
			filemap.put("filename", file.getName());
			filemap.put("filesize", String.valueOf(file.length()));
			filemap.put("filetime", String.valueOf(df.format(file.lastModified())));
			filesStr.add(filemap);
		} else {
			File[] f = file.listFiles();
			if (f == null || f.length <= 0) {
			}else
			{
				for (int i = 0; i < f.length; i++) {
					File tmp = f[i];
					if (tmp.isFile())
					{
						filemap = new HashMap<String,String>();
						filemap.put("trecode", trecode);
						filemap.put("filename", tmp.getName());
						filemap.put("filesize", String.valueOf(tmp.length()/1024+1)+"kb");
						filemap.put("filetime", String.valueOf(df.format(tmp.lastModified())));
						filesStr.add(filemap);
					}
				}
			}
		}
		return filesStr;
	}
	public void uploadfile(Map parammap) throws ITFEBizException {
		
		
		if(parammap!=null&&parammap.get("filelist")!=null)
		{
			String rootpath=FileRootPathUtil.getInstance().getRoot();
			String trecode = String.valueOf(parammap.get("trecode"));
			List<String> filelist = (List<String>)parammap.get("filelist");
			File oldfile = null;
			File newfile = null;
			File dir = null;
			for(String tempfile:filelist)
			{
				oldfile = new File(rootpath+tempfile);
				if(tempfile.contains("/"))
					newfile = new File(rootpath+"filetoserver"+"/"+trecode+"/"+tempfile.substring(tempfile.lastIndexOf("/")));
				else
					newfile = new File(rootpath+"filetoserver"+"\\"+trecode+"\\"+tempfile.substring(tempfile.lastIndexOf("\\")));
				dir = new File(newfile.getParent());
				if(newfile.exists())
					newfile.delete();
				else if(!dir.exists())
					dir.mkdirs();
				oldfile.renameTo(newfile);
			}
		}
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		UploadfiletoserverService.log = log;
	}	


}