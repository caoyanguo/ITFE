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
	 * ��Ϣƾ֤�ϴ�	 
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
			log.debug("�����صĸ�������ʱĿ¼ת�Ƶ���ʽĿ¼�С�");
			//��ȡ��ǰʱ��
			Timestamp now = TSystemFacade.getDBSystemTime();
			//ϵͳ��ǰ����
			String today = DateUtil.date2String2(new Date(now.getTime()));
			//�����ں�ҵ��ƾ֤�����������ļ�����·��
			String fileSavePath = "/recv/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
				+ today.substring(4) + "/other/" + user.getSorgcode() + "/";
			
			////�����еı������ص��ļ��ӷ���������ʱĿ¼ת�Ƶ��ļ�����Ŀ¼��
			List<String> fileList = new ArrayList<String>();
			for (Object o : upLoadFileList){
				//�õ��ϴ��ļ�������·��
				String fileSrcName = ITFECommonConstant.FILE_ROOT_PATH + o.toString();
				//��Դ�ļ�
				File srcFile = new File(fileSrcName);
				//�ļ���
				String fileName = srcFile.getName();
				//Ŀ���ļ�����·��
				String fileDistName = ITFECommonConstant.FILE_ROOT_PATH + fileSavePath + fileName;
				FileUtil.getInstance().createDir(ITFECommonConstant.FILE_ROOT_PATH + fileSavePath);
				//��Ŀ���ļ�
				File distFile = new File(fileDistName);
				FileCopyUtils.copy(srcFile, distFile);
				srcFile.delete();//ɾ����ʱ�ļ�
				//��Ŀ���ļ������·������ڼ����У��Ա㱣��ʹ��
				fileList.add(fileSavePath + fileName);
			}
			
			//��¼������־��Ϣ
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//������ˮ��
			sendLog.setSsendorgcode(user.getSorgcode());//���ͻ�������
			sendLog.setSdate(today);//��������
			sendLog.setSoperationtypecode("99");//ҵ��ƾ֤����
			sendLog.setStitle(strTitle);//ƾ֤����
			sendLog.setSsendtime(now);//����ʱ��
			sendLog.setSretcode(ITFECommonConstant.STATUS_SUCCESS);//������Ϊ�ɹ�����
			sendLog.setSretcodedesc("���ͳɹ�");//������˵��
			sendLog.setSusercode(user.getSuserCode());//������
			
			//��¼�ļ�������Ϣ
			IDto[] fileDto;
			if (fileList.size() == 0){
				fileDto = new TvFilesDto[1];
				String fileSeq = StampFacade.getStampSendSeq("");
				TvFilesDto dto = new TvFilesDto();
				dto.setIno(new Integer(fileSeq));//��ˮ��
				dto.setSdate(sendLog.getSdate());//��������
				dto.setSno(sendSeq);//������ˮ��
				dto.setScontent(strContent);//��Ϣƾ֤����
				dto.setSsavepath("");
				fileDto[0] = dto;
			}else{
				fileDto = new TvFilesDto[fileList.size()];
				for (int i=0; i<fileList.size(); i++){
					String fileSeq = StampFacade.getStampSendSeq("");
					TvFilesDto dto = new TvFilesDto();
					dto.setIno(new Integer(fileSeq));//��ˮ��
					dto.setSdate(sendLog.getSdate());//��������
					dto.setSno(sendSeq);//������ˮ��
					dto.setScontent(strContent);//��Ϣƾ֤����
					dto.setSsavepath(fileList.get(i));//���·��
					fileDto[i] = dto;
				}
			}
			
			//��¼������־��Ϣ
			IDto[] recvLogs = new TvRecvlogDto[recvOrgList.size()];
			String recvOrgNames = "";
			for (int i=0; i<recvOrgList.size(); i++){
				//���ջ�����Ϣ
				TsOrganDto recvOrg = (TsOrganDto)recvOrgList.get(i);
				//Ϊÿһ�����ս���дһ��������־
				TvRecvlogDto recvLog = new TvRecvlogDto();
				String recvSeq = StampFacade.getStampSendSeq("JS");
				recvLog.setSrecvno(recvSeq);//������ˮ��
				recvLog.setSsendno(sendSeq);//��Ӧ������־��ˮ��
				recvLog.setSrecvorgcode(recvOrg.getSorgcode());//���ջ�������
				recvLog.setSdate(today);//��������
				recvLog.setSoperationtypecode("99");//ҵ��ƾ֤����
				recvLog.setSsendorgcode(user.getSorgcode());//���ͻ�������
				recvLog.setStitle(strTitle);//ƾ֤����
				recvLog.setSrecvtime(now);//����ʱ��
				recvLog.setSretcode(ITFECommonConstant.STATUS_SUCCESS);//δ����
				recvLogs[i] = recvLog;
				recvOrgNames += recvOrg.getSorgname() + ";";
			}
			//���÷�����־�Ľ��ջ�������
			sendLog.setSrecvorgcode(recvOrgNames);//���ջ�������
			
			log.debug("�����շ���־");
			//�����ļ�������Ϣ
			DatabaseFacade.getDb().create(fileDto);
			//���淢����־
			DatabaseFacade.getDb().create(sendLog);
			//���������־
			DatabaseFacade.getDb().create(recvLogs);
		}catch(Exception e){
			setRollbackOnly();
			log.error("��Ϣ�ļ�����ʱ����",e);
			throw new ITFEBizException(e);
		}
    }

	/**
	 * ��¼��������־
	 */
    public void addErrorSendLog(String title, String errMsg) throws ITFEBizException {
		//��¼����ʧ����־��Ϣ
		ITFELoginInfo user = getLoginInfo();
		try{
			//��ȡ��ǰʱ��
			Timestamp now = TSystemFacade.getDBSystemTime();
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//������ˮ��
			sendLog.setSsendorgcode(user.getSorgcode());//���ͻ�������
			sendLog.setSdate(DateUtil.date2String2(new Date(now.getTime())));//��������
			sendLog.setSoperationtypecode("99");//ҵ��ƾ֤����
			sendLog.setStitle(title);//ƾ֤����
			sendLog.setSsendtime(now);//����ʱ��
			sendLog.setSretcode(ITFECommonConstant.STATUS_FAILED);//������Ϊ����ʧ��
			sendLog.setSretcodedesc(errMsg);//������˵��
			sendLog.setSusercode(user.getSuserCode());//������
			//���淢����־
			DatabaseFacade.getDb().create(sendLog);
		}catch(Exception ex){
			setRollbackOnly();
			log.error("��¼��������־ʱ����",ex);
			throw new ITFEBizException(ex);
		}
	}

	/**
	 * ���ȫ����ͨ������Ϣ	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List<TsOrganDto> getAllConnOrgs() throws ITFEBizException {
		ITFELoginInfo user = getLoginInfo();
		log.debug("����û���������" + user.getSorgcode() + "��ȫ������ͨ������");
		try{
			return StampFacade.getAllConnOrgs(user.getSorgcode());
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
    }

	/**
	 * ɾ��һ���Ѿ����ص��ļ�
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