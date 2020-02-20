package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.util.BatchConfirmUtilByBiztype;
import com.cfcc.itfe.service.util.BatchDeleteUtilByBiztype;
import com.cfcc.itfe.service.util.CheckBizParam;
import com.cfcc.itfe.service.util.DirectConfirmUtilByBiztype;
import com.cfcc.itfe.service.util.EachConfirmUtilByBiztype;
import com.cfcc.itfe.service.util.EachDeleteUtilByBiztype;
import com.cfcc.itfe.service.util.sendMsgUtil;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileOprUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.FtpUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhuguozhen
 * @time 12-02-21 08:45:49 �������ţ�������ţ�ֱ���ύ
 */
@SuppressWarnings("unchecked")
public class UploadConfirmService extends AbstractUploadConfirmService {
	private static Log logger = LogFactory.getLog(UploadConfirmService.class);

	/**
	 * batchQuery ��������-��ѯ(��ҵ�����͡��ܽ���ѯ)
	 * 
	 * @generated
	 * @param bizTypeҵ������
	 * @param checkMoney�ܽ��
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List batchQuery(String bizType, BigDecimal checkMoney)
			throws ITFEBizException {
		String selectSQL="";
		if (bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
		   selectSQL = "SELECT S_FILENAME,I_COUNT,N_MONEY,S_ORGCODE,S_TRECODE FROM ("
				+ "SELECT S_ORGCODE,S_FILENAME,S_TRECODE,sum(I_COUNT) I_COUNT,sum(N_MONEY) N_MONEY FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				+ " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND ( S_OPERATIONTYPECODE= ? or S_OPERATIONTYPECODE= ? )"
				+ " GROUP BY S_ORGCODE,S_FILENAME,S_TRECODE) WHERE N_MONEY= ? ";
		} else if (bizType.equals(BizTypeConstant.BIZ_TYPE_RET_TREASURY)
				|| bizType.equals(BizTypeConstant.BIZ_TYPE_PAY_OUT)
				|| bizType.equals(BizTypeConstant.BIZ_TYPE_PAY_OUT2)) {// �˿�ҵ���������Ų�ѯ
			selectSQL = "SELECT S_FILENAME,I_COUNT,N_MONEY,S_ORGCODE,S_TRECODE FROM ("
				+ "SELECT S_ORGCODE,S_TRECODE,S_FILENAME,sum(I_COUNT) I_COUNT,sum(N_MONEY) N_MONEY FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				+ " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? "
				+ " GROUP BY S_ORGCODE,S_TRECODE,S_FILENAME) WHERE N_MONEY= ? ";
		}else if (bizType.equals(BizTypeConstant.BIZ_TYPE_BATCH_OUT)) {// ���������������Ų�ѯ
			selectSQL = "SELECT S_FILENAME,I_COUNT,N_MONEY,S_ORGCODE,S_TRECODE FROM ("
				+ "SELECT S_ORGCODE,S_FILENAME,S_TRECODE,sum(I_COUNT) I_COUNT,sum(N_MONEY) N_MONEY FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				+ " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? "
				+ " GROUP BY S_ORGCODE,S_FILENAME,S_TRECODE)";
		}else {
			selectSQL = "SELECT S_FILENAME,I_COUNT,N_MONEY,S_ORGCODE,S_TRECODE FROM ("
				+ "SELECT S_ORGCODE,S_FILENAME,S_TRECODE,sum(I_COUNT) I_COUNT,sum(N_MONEY) N_MONEY FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				+ " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? "
				+ " GROUP BY S_ORGCODE,S_FILENAME,S_TRECODE) WHERE N_MONEY= ? ";
		}
		
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// �����������
			sqlExec.addParam(this.getLoginInfo().getSorgcode());
			// ����״̬
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// ҵ������
			if (bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else{
				sqlExec.addParam(bizType);
			}
			// �����ܽ��
			if (!bizType.equals(BizTypeConstant.BIZ_TYPE_BATCH_OUT)){
				sqlExec.addParam(checkMoney);
			}
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			int row = trasrlnoRs.getRowCount();

			List<TvFilepackagerefDto> dtolist = new ArrayList<TvFilepackagerefDto>();
			for (int i = 0; i < row; i++) {
				TvFilepackagerefDto tmpdto = new TvFilepackagerefDto();
				// �ļ���
				tmpdto.setSfilename(trasrlnoRs.getString(i, 0));
				// �ܱ���
				tmpdto.setIcount(trasrlnoRs.getInt(i, 1));
				// �ܽ��
				tmpdto.setNmoney(trasrlnoRs.getBigDecimal(i, 2));
				// ��������
				tmpdto.setSorgcode(trasrlnoRs.getString(i, 3));
				// ��������
				tmpdto.setStrecode(trasrlnoRs.getString(i, 4));
//				if (bizType.equals(BizTypeConstant.BIZ_TYPE_RET_TREASURY)
//						|| bizType.equals(BizTypeConstant.BIZ_TYPE_PAY_OUT)
//						|| bizType.equals(BizTypeConstant.BIZ_TYPE_PAY_OUT2)) {
//					tmpdto.setStrecode("");
//				}else{
//					tmpdto.setStrecode(trasrlnoRs.getString(i, 4));
//				}
				
				dtolist.add(tmpdto);
			}
			return dtolist;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * batchConfirm ��������-ȷ���ύ(��ҵ�����͡��ļ���ȷ���ύ)
	 * 
	 * @generated
	 * @param bizTypeҵ������
	 * @param fileName�ļ���
	 * @return java.lang.Integer ���ؽ����0ʧ��,1�ɹ���2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer batchConfirm(String bizType, IDto idto)
			throws ITFEBizException {
		TvFilepackagerefDto fpdto = (TvFilepackagerefDto) idto;
		return BatchConfirmUtilByBiztype.bizBatchConfirm(bizType, fpdto,
				getLoginInfo());
	}

	/**
	 * batchDelete��������-ɾ��
	 * 
	 * @generated
	 * @param bizType
	 * @param fileName
	 * @return java.lang.Integer���ؽ����0ʧ��,1�ɹ���2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer batchDelete(String bizType, IDto idto)
			throws ITFEBizException {
		TvFilepackagerefDto fpdto = (TvFilepackagerefDto) idto;
		return BatchDeleteUtilByBiztype.batchDelete(bizType, fpdto,
				getLoginInfo());
	}

	/**
	 * �������-��ѯ
	 * 
	 * @param bizType
	 * @param idto
	 * @throws ITFEBizException
	 */
	public List eachQuery(String bizType, IDto idto) throws ITFEBizException {
		List resList = new ArrayList();
		String sqlw  = "";
		if(bizType.length() > 9) {			
			if("grpnull".equals(bizType.split(",")[1])) {
				sqlw = " AND S_GROUPID IS NULL ";
			}else {
				sqlw = " 1=1 ";
			}
			bizType = bizType.split(",")[0];
		}
		if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
			sqlw=" and ( s_biztype = '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY
			+"' or s_biztype = '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY+"' ) AND ( S_STATE = '0' OR S_STATE IS NULL ) ";
		}
		if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
			sqlw=" and ( s_biztype = '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK
			+"' or s_biztype = '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+"' ) AND ( S_STATE = '0' OR S_STATE IS NULL ) ";
		}
		if(bizType.length()==2&&(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
				||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK))){
			sqlw=" and s_biztype = '"+bizType+"' AND ( S_STATE = '0' OR S_STATE IS NULL )";
		}
		if(bizType.equals("") ){
			sqlw=" AND ( S_STATE = '0' OR S_STATE IS NULL )";
		}
		if (bizType.equals(BizTypeConstant.BIZ_TYPE_RET_TREASURY)) {
			sqlw=" AND ( S_STATUS  = '0' OR S_STATUS  IS NULL )";
		} 
		String bizname = CheckBizParam.getBizname(bizType);
		try {
			resList = CommonFacade.getODB().findRsByDtoForWhere(idto, sqlw);
		} catch (JAFDatabaseException e) {
			log.error("��ѯ" + bizname + "��Ϣʱ����1");
			throw new ITFEBizException("��ѯ" + bizname + "��Ϣʱ����1", e);
		} catch (ValidateException e) {
			log.error("��ѯ" + bizname + "��Ϣʱ����2");
			throw new ITFEBizException("��ѯ" + bizname + "��Ϣʱ����2", e);
		}
		return resList;
	}

	/**
	 * �������-ȷ���ύ
	 * 
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer���ؽ����0ʧ��,1�ɹ���2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer eachConfirm(String bizType, IDto idto)
			throws ITFEBizException {
		return EachConfirmUtilByBiztype.bizEachConfirm(bizType, idto,
				getLoginInfo());
	}

	/**
	 * ֱ���ύ
	 * 
	 * @param bizType
	 *            ҵ������
	 * @return java.lang.Integer ���ؽ����0ʧ��,1�ɹ�,2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer directSubmit(String bizType) throws ITFEBizException {
		return DirectConfirmUtilByBiztype.bizDirectConfirm(bizType,
				getLoginInfo());

	}

	public void checkAndSendMsg(IDto idto, String msgno, String tablename,
			Long vousrlno) throws ITFEBizException {
		sendMsgUtil.checkAndSendMsg(idto, msgno, tablename, vousrlno,
				getLoginInfo());
	}

	/**
	 * ���ɾ��
	 * 
	 * @param bizType
	 * @param idto
	 * @return java.lang.Integer���ؽ����0ʧ��,1�ɹ���2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer eachDelete(String bizType, IDto idto)
			throws ITFEBizException {
		return EachDeleteUtilByBiztype
				.eachDelete(bizType, idto, getLoginInfo());
	}

	public Integer setFail(String bizType, IDto idto) throws ITFEBizException {
		try {
			Class c = Class.forName("com.cfcc.itfe.service.util.SetFail"+bizType);
			Object o = Class.forName("com.cfcc.itfe.service.util.SetFail"+bizType).newInstance();
			Method m = c.getMethod("setFail",IDto.class,String.class);
			m.invoke(o, new Object[]{idto,this.getLoginInfo().getSorgcode()});
		} catch (Exception e) {
			log.error("���Ŵ���ҵ�����쳣",e);
			throw new ITFEBizException("���Ŵ���ҵ�����쳣",e);
		}
		 return 0;
	}

	
	/**
	 * ���������˻������ύ
	 * 
	 * @param bizType  
	 *            ҵ������
	 * @return java.lang.Integer ���ؽ����0ʧ��,1�ɹ�,2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer applyBackBatchConfirm(String bizType, IDto idto, IDto _dto)
			throws ITFEBizException {
		TvFilepackagerefDto fpdto = (TvFilepackagerefDto) idto;
		return BatchConfirmUtilByBiztype.bizApplyBackBatchConfirm(bizType, fpdto,
				getLoginInfo(),_dto);
	}

	
	/**
	 * ���������˻�ֱ���ύ
	 * 
	 * @param bizType  idto
	 *            ҵ������  DTO
	 * @return java.lang.Integer ���ؽ����0ʧ��,1�ɹ�,2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer applyBackDirectSubmit(String bizType, IDto idto)
			throws ITFEBizException {
		return DirectConfirmUtilByBiztype.bizApplyBackDirectConfirm(bizType, getLoginInfo(), idto);
	}

	
	/**
	 * ���������˻�����ύ
	 * 
	 * @param bizType
	 *            ҵ������
	 * @return java.lang.Integer ���ؽ����0ʧ��,1�ɹ�,2�����Ź�
	 * @throws ITFEBizException
	 */
	public Integer applyBackEachConfirm(String bizType, IDto idto, IDto _dto)
			throws ITFEBizException {
		return EachConfirmUtilByBiztype.bizApplyBackEachConfirm(bizType, idto,
				getLoginInfo(),_dto);
	}

	public void addDirectGrantFile(String filedir, List filenamelist)
			throws ITFEBizException {
		
		
	}

	public void deleteDirectGrantErorFile(String mainfiledir,
			List deleteFileList) throws ITFEBizException {
		
		
	}
	private List<String> listAbsPath(String filePath) {
		
		File tmp1 = new File(filePath);
		List<String> filesStr = new ArrayList<String>();
		// ���ݾ���·������ɾ������
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		if (file.isFile()) {
			filesStr.add(str);
		} else {
			File[] f = file.listFiles();
			if(f!=null&&f.length>0)
			{
				for (int i = 0; i < f.length; i++) {
					File tmp = f[i];
					String strF = tmp.getAbsolutePath();
					if (tmp.isDirectory()) {
	
						listAbsPath(strF);
	
					} else {
						filesStr.add(strF);
					}
	
				}
			}
		}
		return filesStr;
	}
	private List<String> getDirFileList(String filePath) {
		File tmp1 = new File(filePath);
		// ���ݾ���·������ɾ������
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		List<String> filesStr = new ArrayList<String>(); 
		if (file.isFile()) {
			filesStr.add(str);
		} else {
			File[] f = file.listFiles();
			if (f == null || f.length <= 0) {
				filesStr.add(str);
			}else
			{
				for (int i = 0; i < f.length; i++) {
					File tmp = f[i];
					String strF = tmp.getAbsolutePath();
					if (tmp.isFile())
						filesStr.add(strF);
				}
			}
		}
		return filesStr;
	}
	// �����ļ�
    private static void copyFile(String sourceFile, String targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
        	File souFile = new File(sourceFile);
        	File tarFile = new File(targetFile);
        	if(!tarFile.getParentFile().exists())
        		tarFile.getParentFile().mkdirs();
            // �½��ļ����������������л���
            inBuff = new BufferedInputStream(new FileInputStream(souFile));
            // �½��ļ���������������л���
            outBuff = new BufferedOutputStream(new FileOutputStream(tarFile));
            // ��������
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // ˢ�´˻���������
            outBuff.flush();
        } finally {
            // �ر���
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
	 private boolean isWin()
	 {
			String osName = System.getProperty("os.name");
			if (osName.indexOf("Windows") >= 0) {
				return true;
			} else {
				return false;
			}
	}
	private List getFtpFileList(String sfilepath,String sbiztype)
	{
		List<String> fileList = new ArrayList<String>();
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		sfilepath = sfilepath.replace("/", File.separator).replace("\\",File.separator);
		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();
		String tarFilename = null;
		String sourFilename = null;
		if(isWin())
			sfilepath="D:"+sfilepath;
		if("ftp".equals(sbiztype))
		{
			fileList = listAbsPath(sfilepath);
			List<Map> getFtpList = new ArrayList<Map>();
			if(fileList!=null&&fileList.size()>0)
			{
				Map fileMap = null;
				for(String str : fileList){
					fileMap = new HashMap();
						fileMap.put("filepath", str.replace("/", File.separator).replace("\\",File.separator));
						fileMap.put("filename", new File(str).getName());
						getFtpList.add(fileMap);
				}
			}
			return getFtpList;
		}else if(sbiztype!=null&&sbiztype.startsWith("ftpupload"))
		{
			File file = new File(root+sfilepath);
			if(sbiztype.indexOf(File.separator+"itfe"+File.separator+"czftp")>0)
				tarFilename = File.separator+"itfe"+File.separator+"czftp"+File.separator+"swap"+File.separator+file.getName();
			else if(sbiztype.indexOf(File.separator+"itfe"+File.separator+"nnczftp")>0)
				tarFilename = File.separator+"itfe"+File.separator+"nnczftp"+File.separator+"swap"+File.separator+file.getName();
			else if(sbiztype.indexOf(File.separator+"itfe"+File.separator+"dsftp")>0)
				tarFilename = File.separator+"itfe"+File.separator+"dsftp"+File.separator+"swap"+File.separator+file.getName();
			File tarFile = new File(tarFilename);
			if(!tarFile.getParentFile().exists())
				tarFile.getParentFile().mkdirs();
			file.renameTo(tarFile);
			fileList.add(tarFilename);
		}else if("ftpdownload".equals(sbiztype))
		{
			sourFilename = sfilepath;
			if(sourFilename.indexOf(File.separator+"itfe"+File.separator+"czftp")>=0)
				tarFilename = "ITFEDATA"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+"czftpswap"+File.separator+TimeFacade.getCurrentStringTime()+File.separator+sourFilename.substring(sourFilename.lastIndexOf(File.separator)+1);
			else if(sourFilename.indexOf(File.separator+"itfe"+File.separator+"nnczftp")>=0)
				tarFilename = "ITFEDATA"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+"nnczftpswap"+File.separator+TimeFacade.getCurrentStringTime()+File.separator+sourFilename.substring(sourFilename.lastIndexOf(File.separator)+1);
			else if(sourFilename.indexOf(File.separator+"itfe"+File.separator+"dsftp")>=0)
				tarFilename = "ITFEDATA"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+"dsftpswap"+File.separator+TimeFacade.getCurrentStringTime()+File.separator+sourFilename.substring(sourFilename.lastIndexOf(File.separator)+1);
			try {
				copyFile(sourFilename,root+tarFilename);
			} catch (IOException e) {
				logger.error(e);
			}
			fileList.add(tarFilename);
		}
		return fileList;
	}
	public List getDirectGrantFileList(String sfilepath, String sbiztype)
			throws ITFEBizException {
		List<String> fileList = null;
		if(sbiztype!=null&&!"".equals(sbiztype)&&sbiztype.startsWith("ftp"))
		{
			fileList = getFtpFileList(sfilepath,sbiztype);
			return fileList;
		}else
		{
			fileList = getDirFileList(sfilepath);
		}
		List<Map> getFileList = new ArrayList<Map>();
		Map fileMap = null;
		// �õ��ļ��ϴ�����
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();
		if(fileList!=null&&fileList.size()>0)
		{
			String oldfile = null;
			String newfilename = null;
			String fileconent;
			try
			{
				for(int i=0;i<fileList.size();i++)
				{
					fileMap = new HashMap();
					File file = null;
					if(fileList.get(i).indexOf(sbiztype+".txt")>0)
					{
						oldfile = fileList.get(i);
						oldfile = oldfile.replace("/", File.separator).replace("\\",File.separator);
						if(oldfile.startsWith(File.separator+"itfe"+File.separator+"czftp"+File.separator)||oldfile.startsWith(File.separator+"itfe"+File.separator+"nnczftp"+File.separator))
						{
							newfilename = "ITFEDATA"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+TimeFacade.getCurrentStringTime()+File.separator+oldfile.substring(oldfile.lastIndexOf(File.separator)+1);
							file = new File(root+newfilename);
							if(!file.exists())
							{
								fileconent = FileUtil.getInstance().readFile(oldfile);
								FileUtil.getInstance().writeFile(root+newfilename, fileconent);
							}
							fileMap.put("newfilepath", newfilename);
						}
						fileMap.put("oldfilepath", oldfile);
						fileMap.put("filename", oldfile.substring(oldfile.lastIndexOf(File.separator)+1));
						getFileList.add(fileMap);
					}else if(fileList.get(i).toLowerCase().endsWith(".pas")){	//���������ļ���.pas��β           ������ת�Ƶ�ָ��Ŀ¼��
						File oldFile = new File(fileList.get(i));
						newfilename = root+ File.separator + this.getLoginInfo().getSorgcode() + File.separator + "PLBF" + File.separator + oldFile.getName();
						if(!new File(root+ File.separator + this.getLoginInfo().getSorgcode() + File.separator + "PLBF" + File.separator).exists()){
							new File(root+ File.separator + this.getLoginInfo().getSorgcode() + File.separator + "PLBF" + File.separator).mkdirs();
						}
						oldFile.renameTo(new File(newfilename));
					}
				}
			}catch(Exception e)
			{
				logger.error(e);
				throw new ITFEBizException("�ƶ��ļ���ϵͳͳһ�ļ�����·���³���"+e.toString());
			}
		}
		if(sbiztype.equals(BizTypeConstant.BIZ_TYPE_BATCH_OUT)){	
			fileList = getDirFileList(root+ File.separator + this.getLoginInfo().getSorgcode() + File.separator + "PLBF" + File.separator);
			for(String str : fileList){
				fileMap = new HashMap();
				if(str.toLowerCase().endsWith(".pas")){
					fileMap.put("filepath", str.replace("\\", "/").replace(root, ""));
					fileMap.put("filename", new File(str).getName());
					getFileList.add(fileMap);
				}
			}
		}
		return getFileList;
	}

	public void delfilelist(List delfilelist,String deletetype) throws ITFEBizException 
	{
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();
		try {
			String newfilename = null;
			String filename = null;
			for (int i = 0; i < delfilelist.size(); i++) {
				File tmpfile =null;
				filename = String.valueOf(delfilelist.get(i));
				if(filename.startsWith(File.separator+"itfe"+File.separator+"czftp"+File.separator)||filename.startsWith(File.separator+"itfe"+File.separator+"nnczftp"+File.separator)||filename.startsWith(File.separator+"itfe"+File.separator+"dsftp"+File.separator))
				{
					tmpfile = new File(filename);
					if("0".equals(deletetype))
					{
						newfilename = tmpfile.getParent()+File.separator+"temp"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+filename.substring(filename.lastIndexOf(File.separator)+1,filename.lastIndexOf(File.separator)+9)+File.separator;
						File newfile =new File(newfilename+filename.substring(filename.lastIndexOf(File.separator)+1));
						File dir = new File(newfile.getParent());
						if(!dir.exists())
							dir.mkdirs();
						tmpfile.renameTo(newfile);
					}else if("1".equals(deletetype))
					{
						newfilename = tmpfile.getParent()+File.separator+"temp"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+"error"+File.separator;
						File newfile =new File(newfilename+filename.substring(filename.lastIndexOf(File.separator)+1));
						File dir = new File(newfile.getParent());
						if(!dir.exists())
							dir.mkdirs();
						tmpfile.renameTo(newfile);
					}else
					{
						if (tmpfile.exists())
							tmpfile.delete();
					}
				}
				else
				{
					tmpfile = new File(root + delfilelist.get(i));
					if (tmpfile.exists())
						tmpfile.delete();
				}
			}
		} catch (Exception e) {
			log.error("ɾ���������ļ�ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
		
	}

	public List ftpfileadd(List selectAddList) throws ITFEBizException {
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		String root = sysconfig.getRoot();
		if(selectAddList!=null&&selectAddList.size()>0)
		{
			TvBatchpayDto dto = null;
			String newfilename = null;
			File newFile = null;
			for(int i=0;i<selectAddList.size();i++)
			{
				dto = (TvBatchpayDto)selectAddList.get(i);
				newfilename = "ITFEDATA"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+TimeFacade.getCurrentStringTime()+File.separator+dto.getSfilename();
				newFile = new File(root+newfilename);
				if(!newFile.getParentFile().exists())
					newFile.getParentFile().mkdirs();
				if(!newFile.exists())
				{
					try {
						FileUtils.copyFile(new File(dto.getSfilepath()+dto.getSfilename()),newFile);
					} catch (IOException e) {
						log.error(e);
						new ITFEBizException(e.getMessage());
					}
				}
			}
		}
		return null;
	}

	public void readFtpFile(String date) throws ITFEBizException {
		
		try {
			FtpUtil.downLoadZipFile(date,this.getLoginInfo().getSorgcode());
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	public Map checkSignFileForSd(String filenamepath,String tempfilenamepath, String privatekey) throws ITFEBizException {
		Map getMap = new HashMap();
		// �õ��ļ��ϴ�����
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();
		FileOprUtil util = new FileOprUtil();
		Md5App md5XOr = new Md5App(true, privatekey);// ��Ҫ����Կ�������
		List<String> list = null;
		try {
			list = util.readFileWithLine(filenamepath);
		} catch (FileOperateException e) {
			log.error(e);
			new ITFEBizException(e);
		}
		StringBuffer filebuf = new StringBuffer("");
		String fileSign = "";
		StringBuffer strCA = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if ("<CA>".equals(list.get(i).toUpperCase())) {
				fileSign = list.get(i + 1);
				break;
			} else {
				// ��ÿһ�н��д����ʽ��MD5��ǩ
				String linestr = list.get(i);
				String linestrmd5 = md5XOr.makeMd5(linestr);
				strCA.append(linestrmd5);
				filebuf.append(linestr + "\r\n");
			}
		}
		// ����Ҫ����Կ������MD5��ǩ
		Md5App md5 = new Md5App();
		String checkSign = md5.makeMd5(strCA.toString());
		if (!fileSign.equals(checkSign)) {
			getMap.put("yzm", "-1");
		}else
			getMap.put("yzm", fileSign);
		try {
			util.writeFile(tempfilenamepath, filebuf.toString());
			String newfilename = "ITFEDATA"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+TimeFacade.getCurrentStringTime()+File.separator+filenamepath.substring(filenamepath.lastIndexOf(File.separator)+1);
			File newFile = new File(root+newfilename);
			if(!newFile.getParentFile().exists())
				newFile.getParentFile().mkdirs();
			String newtempfilename = "ITFEDATA"+File.separator+this.getLoginInfo().getSorgcode()+File.separator+TimeFacade.getCurrentStringTime()+File.separator+tempfilenamepath.substring(tempfilenamepath.lastIndexOf(File.separator)+1);
			File tempFile = new File(root+newtempfilename);
			if(!tempFile.getParentFile().exists())
				tempFile.getParentFile().mkdirs();
			if(!newFile.exists())
				FileUtils.copyFile(new File(filenamepath),newFile);
			if(!tempFile.exists())
				FileUtils.copyFile(new File(tempfilenamepath),tempFile);
			getMap.put("newfilename", newfilename);
			getMap.put("newtempfilename",newtempfilename);
			util.deleteFile(tempfilenamepath);
		} catch (FileOperateException e) {
			log.error(e);
			new ITFEBizException(e);
		} catch (FileNotFoundException e) {
			log.error(e);
			new ITFEBizException(e);
		} catch (IOException e) {
			log.error(e);
			new ITFEBizException(e);
		}
		return getMap;
	}

	public void updateIdtoList(List idtoList) throws ITFEBizException {
		if(idtoList!=null&&idtoList.size()>0)
		{
			try {
				DatabaseFacade.getDb().update(CommonUtil.listTArray(idtoList));
			} catch (JAFDatabaseException e1) {
				log.error(e1);
				throw new ITFEBizException("������������ʧ��", e1);
			}
		}
		
	}

	public List queryFtpReturnFileList(TvBatchpayDto queryDto)
			throws ITFEBizException {
		List queryList = null;
		try{
			queryList = FtpUtil.queryFtpReturnFileList(queryDto);
		}catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
		return queryList;
	}

	public void sendReturnToFtp(List sendFileList) throws ITFEBizException {
		try {
			FtpUtil.uploadZipFile(sendFileList);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
		
	}
}