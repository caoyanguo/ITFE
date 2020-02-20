package com.cfcc.itfe.service.sendbiz.infocertsend;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author sjz
 * @time   09-11-11 21:50:33
 * codecomment: 
 */

public class MessDataUploadService extends AbstractMessDataUploadService {
	private static Log log = LogFactory.getLog(MessDataUploadService.class);	


	/**
	 * 信息凭证上传	 
	 * @generated
	 * @param strTitle
	 * @param strContent
	 * @param recvOrgList
	 * @param upLoadFileList
	 * @throws ITFEBizException	 
	 */
    public void upload(String strTitle, String strContent, List recvOrgList, List upLoadFileList) throws ITFEBizException {
		ITFELoginInfo user = getLoginInfo();
		try{
			log.debug("将上载的附件从临时目录转移到正式目录中。");
			//获取当前时间
			Timestamp now = TSystemFacade.getDBSystemTime();
			//系统当前日期
			String today = DateUtil.date2String2(new Date(now.getTime()));
			//用日期和业务凭证类型来生成文件保存路径
			String fileSavePath = "/recv/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
				+ today.substring(4) + "/other/" + user.getSorgcode() + "/";
			
			////将所有的本次上载的文件从服务器的临时目录转移到文件上载目录中
			List<String> fileList = new ArrayList<String>();
			for (Object o : upLoadFileList){
				//得到上传文件的完整路径
				String fileSrcName = ITFECommonConstant.FILE_ROOT_PATH + o.toString();
				//打开源文件
				File srcFile = new File(fileSrcName);
				//文件名
				String fileName = srcFile.getName();
				//目的文件完整路径
				String fileDistName = ITFECommonConstant.FILE_ROOT_PATH + fileSavePath + fileName;
				FileUtil.getInstance().createDir(ITFECommonConstant.FILE_ROOT_PATH + fileSavePath);
				//打开目的文件
				File distFile = new File(fileDistName);
				FileCopyUtils.copy(srcFile, distFile);
				srcFile.delete();//删除临时文件
				//将目标文件的相对路径存放在集合中，以便保存使用
				fileList.add(fileSavePath + fileName);
			}
			
			//记录发送日志信息
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//发送流水号
			sendLog.setSsendorgcode(user.getSorgcode());//发送机构代码
			sendLog.setSdate(today);//所属日期
			sendLog.setSoperationtypecode("99");//业务凭证类型
			sendLog.setStitle(strTitle);//凭证标题
			sendLog.setSsendtime(now);//发送时间
			sendLog.setSretcode(ITFECommonConstant.STATUS_SUCCESS);//处理码为成功发送
			sendLog.setSretcodedesc("发送成功");//处理码说明
			sendLog.setSusercode(user.getSuserCode());//发送人
			
			//记录文件内容信息
			IDto[] fileDto;
			if (fileList.size() == 0){
				fileDto = new TvFilesDto[1];
				String fileSeq = StampFacade.getStampSendSeq("");
				TvFilesDto dto = new TvFilesDto();
				dto.setIno(new Integer(fileSeq));//流水号
				dto.setSdate(sendLog.getSdate());//所属日期
				dto.setSno(sendSeq);//发送流水号
				dto.setScontent(strContent);//信息凭证内容
				dto.setSsavepath("");
				fileDto[0] = dto;
			}else{
				fileDto = new TvFilesDto[fileList.size()];
				for (int i=0; i<fileList.size(); i++){
					String fileSeq = StampFacade.getStampSendSeq("");
					TvFilesDto dto = new TvFilesDto();
					dto.setIno(new Integer(fileSeq));//流水号
					dto.setSdate(sendLog.getSdate());//所属日期
					dto.setSno(sendSeq);//发送流水号
					dto.setScontent(strContent);//信息凭证内容
					dto.setSsavepath(fileList.get(i));//存放路径
					fileDto[i] = dto;
				}
			}
			
			//记录接收日志信息
			IDto[] recvLogs = new TvRecvlogDto[recvOrgList.size()];
			String recvOrgNames = "";
			for (int i=0; i<recvOrgList.size(); i++){
				//接收机构信息
				TsOrganDto recvOrg = (TsOrganDto)recvOrgList.get(i);
				//为每一个接收接收写一条接收日志
				TvRecvlogDto recvLog = new TvRecvlogDto();
				String recvSeq = StampFacade.getStampSendSeq("JS");
				recvLog.setSrecvno(recvSeq);//接收流水号
				recvLog.setSsendno(sendSeq);//对应发送日志流水号
				recvLog.setSrecvorgcode(recvOrg.getSorgcode());//接收机构代码
				recvLog.setSdate(today);//所属日期
				recvLog.setSoperationtypecode("99");//业务凭证类型
				recvLog.setSsendorgcode(user.getSorgcode());//发送机构代码
				recvLog.setStitle(strTitle);//凭证标题
				recvLog.setSrecvtime(now);//接收时间
				recvLog.setSretcode(ITFECommonConstant.STATUS_SUCCESS);//未接收
				recvLogs[i] = recvLog;
				recvOrgNames += recvOrg.getSorgname() + ";";
			}
			//设置发送日志的接收机构名称
			sendLog.setSrecvorgcode(recvOrgNames);//接收机构名称
			
			log.debug("保存收发日志");
			//保存文件内容信息
			DatabaseFacade.getDb().create(fileDto);
			//保存发送日志
			DatabaseFacade.getDb().create(sendLog);
			//保存接收日志
			DatabaseFacade.getDb().create(recvLogs);
		}catch(Exception e){
			setRollbackOnly();
			log.error("信息文件发送时错误",e);
			throw new ITFEBizException(e);
		}
    }

	/**
	 * 记录错误发送日志
	 */
    public void addErrorSendLog(String title, String errMsg) throws ITFEBizException {
		//记录发送失败日志信息
		ITFELoginInfo user = getLoginInfo();
		try{
			//获取当前时间
			Timestamp now = TSystemFacade.getDBSystemTime();
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//发送流水号
			sendLog.setSsendorgcode(user.getSorgcode());//发送机构代码
			sendLog.setSdate(DateUtil.date2String2(new Date(now.getTime())));//所属日期
			sendLog.setSoperationtypecode("99");//业务凭证类型
			sendLog.setStitle(title);//凭证标题
			sendLog.setSsendtime(now);//发送时间
			sendLog.setSretcode(ITFECommonConstant.STATUS_FAILED);//处理码为发送失败
			sendLog.setSretcodedesc(errMsg);//处理码说明
			sendLog.setSusercode(user.getSuserCode());//发送人
			//保存发送日志
			DatabaseFacade.getDb().create(sendLog);
		}catch(Exception ex){
			setRollbackOnly();
			log.error("记录错误发送日志时错误",ex);
			throw new ITFEBizException(ex);
		}
	}

	/**
	 * 获得全部连通机构信息	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List<TsOrganDto> getAllConnOrgs() throws ITFEBizException {
		ITFELoginInfo user = getLoginInfo();
		log.debug("获得用户所属机构" + user.getSorgcode() + "的全部可连通机构。");
		try{
			return StampFacade.getAllConnOrgs(user.getSorgcode());
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
    }

	/**
	 * 删除一个已经上载的文件
	 */
    public void deleteOneFile(String filePath) throws ITFEBizException {
		try {
			String fileName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			FileUtil.getInstance().deleteFile(fileName);
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

}