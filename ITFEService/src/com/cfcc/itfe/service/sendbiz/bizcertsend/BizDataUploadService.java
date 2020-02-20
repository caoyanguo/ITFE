package com.cfcc.itfe.service.sendbiz.bizcertsend;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOperationplaceDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvGrantpayfilesubDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.encrypt.KoalPkcs7Encrypt;
import com.cfcc.itfe.util.stamp.StampSendHelper;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author Administrator
 * @time   09-10-29 02:16:46
 * codecomment: 
 */

public class BizDataUploadService extends AbstractBizDataUploadService {
	private static Log log = LogFactory.getLog(BizDataUploadService.class);	

	public void showReport() throws ITFEBizException {
	}

	/**
	 * ҵ��ƾ֤���صĴ�����	 
	 * @generated
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
	public Integer upload(TsOperationmodelDto model,String filePath,List recvOrgs) throws ITFEBizException {
		String fileName = "";//�����ļ���
		//��ǰ��¼�û���Ϣ
		ITFELoginInfo user = getLoginInfo();
		try{
			log.debug("��ʼ����ļ�" + filePath);
			//�õ��ϴ��ļ�������·��
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			//��ȡ�ļ���ȫ�����ݣ���������������Ժ�Ͳ�Ҫ���޸��ˣ�����ֱ�Ӱ���Щ����д�뵽����Ŀ¼��
			File file = new File(fileFullName);
			fileName = file.getName();//�ļ���
			String content = FileUtil.getInstance().readFile(fileFullName);//�ļ�������
			
			//�������ŷ�
			String unEncrypt = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelop(content);
			if (unEncrypt == ""){
				//�������ŷ��ʱ����ִ���
				throw new ITFEBizException("�������ŷ����" + KoalPkcs7Encrypt.getInstance().getLastError());
			}
			
			//��֤����ǩ��
			List<String> ret = KoalPkcs7Encrypt.getInstance().pkcs7VerifySign(unEncrypt);
			if (ret.size() == 0){
				//��֤ǩ����ʱ����ִ���
				throw new ITFEBizException("��֤����ǩ������" + KoalPkcs7Encrypt.getInstance().getLastError());
			}else{
				//����֤���е�CN
				String cn = KoalPkcs7Encrypt.getInstance().parseCertSubject(ret.get(0));
				log.debug("�ļ�" + filePath + "�ķ�����CNΪ" + cn);
			}
			String strXml = ret.get(1);//����ԭ��
			
			//���ҵ��ƾ֤ģ���״̬��1-��Ҫ���µ�ƾ֤������-����Ҫ����
			if (model.getSisuse().charAt(0) == '1'){
				log.debug("��֤ҵ��ƾ֤" + fileName + "�ĵ���ӡ��");
				//��Ҫ���µ�ҵ��ƾ֤���������͵�ҵ��ƾ֤����Ҫ����
				//���ģ����Ϣ
				String modelXml = FileUtil.getInstance().readFileUtf8(ITFECommonConstant.FILE_ROOT_PATH + model.getSmodelsavepath());
				//��ø���λ��
				List<IDto> places = StampFacade.getPlaceByModelId(model.getSmodelid());
				for (IDto one : places){
					TsOperationplaceDto place = (TsOperationplaceDto)one;
					//ѭ����֤����ӡ��
					int flag = StampSendHelper.getInstance().verifyFormStamp(strXml, modelXml, place.getSformid(), place.getSplaceid(), "", 0);
					if (flag != 0){
						String error = "����λ��" + place.getSplaceid() + "���²�ͨ����" + (StampSendHelper.getInstance().getLastError()==null?"":StampSendHelper.getInstance().getLastError());
						log.info(error);
						throw new ITFEBizException(error);
					}
				}
			}
			
			//��ȡ��ǰʱ��
			Timestamp now = TSystemFacade.getDBSystemTime();
			//���ļ����浽����Ŀ¼��
			String today = DateUtil.date2String2(new Date(now.getTime()));
			//�����ں�ҵ��ƾ֤�����������ļ�����·��
			String fileSavePath = "/recv/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
				+ today.substring(4) + "/" + model.getSoperationtypecode() + "/";
			//��������Ŀ¼
			FileUtil.getInstance().createDir(ITFECommonConstant.FILE_ROOT_PATH + fileSavePath);
			//�����յ����ļ�ת�Ƶ�����Ŀ¼��
			String fileSaveName = ITFECommonConstant.FILE_ROOT_PATH + fileSavePath + fileName;
			FileUtil.getInstance().writeFile(fileSaveName, content);
			//ɾ���Ѿ��ϴ����ļ�
			file.delete();
			
			log.debug("�����շ���־");
			//��¼������־��Ϣ
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//������ˮ��
			sendLog.setSsendorgcode(user.getSorgcode());//���ͻ�������
			sendLog.setSdate(today);//��������
			sendLog.setSoperationtypecode(model.getSoperationtypecode());//ҵ��ƾ֤����
			sendLog.setStitle(fileName);//ƾ֤����
			sendLog.setSsendtime(now);//����ʱ��
			sendLog.setSretcode(ITFECommonConstant.STATUS_SUCCESS);//������Ϊ�ɹ�����
			sendLog.setSretcodedesc("���ͳɹ�");//������˵��
			sendLog.setSusercode(user.getSuserCode());//������
			
			//��¼�ļ�������Ϣ
			TvFilesDto fileDto = new TvFilesDto();
			String fileSeq = StampFacade.getStampSendSeq("");
			fileDto.setIno(new Integer(fileSeq));//��ˮ��
			fileDto.setSdate(sendLog.getSdate());//��������
			fileDto.setSno(sendSeq);//������ˮ��
			fileDto.setIfilelength(new Integer((int)file.length()));//�ļ�����
			fileDto.setSsavepath(fileSavePath + fileName);//���·��
			
			//��¼������־��Ϣ
			IDto[] recvLogs = new TvRecvlogDto[recvOrgs.size()];
			String recvOrgNames = "";
			for (int i=0; i<recvOrgs.size(); i++){
				//���ջ�����Ϣ
				TsOrganDto recvOrg = (TsOrganDto)recvOrgs.get(i);
				//Ϊÿһ�����ս���дһ��������־
				TvRecvlogDto recvLog = new TvRecvlogDto();
				String recvSeq = StampFacade.getStampSendSeq("JS");
				recvLog.setSrecvno(recvSeq);//������ˮ��
				recvLog.setSsendno(sendSeq);//��Ӧ������־��ˮ��
				recvLog.setSrecvorgcode(recvOrg.getSorgcode());//���ջ�������
				recvLog.setSdate(today);//��������
				recvLog.setSoperationtypecode(model.getSoperationtypecode());//ҵ��ƾ֤����
				recvLog.setSsendorgcode(user.getSorgcode());//���ͻ�������
				recvLog.setStitle(fileName);//ƾ֤����
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
			//�޸�ҵ��ƾ֤�����ˮ�еķ�����ˮ���ֶΣ�ʹ��ÿ��ƾ֤��Ŷ����Ѿ����͵�ƾ֤��������
			//ʹ��S_USERCODE�ֶμ�¼ƾ֤������ˮ��
			String sql = "update TV_GRANTPAYFILESUB set S_USERCODE='" + sendSeq + "' where S_ORGCODE='" + user.getSorgcode() 
				+ "' and S_FUNSUBJECTCODE='" + fileName.trim() + "' and S_USERCODE='00'";
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
		}catch(Exception e){
			setRollbackOnly();
			log.error("ҵ��ƾ֤����ʱ����",e);
			throw new ITFEBizException(e);
		}
		return null;
	}
	
	/**
	 * ���Ӵ�������־
	 */
	public void addErrorSendLog(TsOperationmodelDto model,String title,String errMsg) throws ITFEBizException {
		//��ǰ��¼�û���Ϣ
		ITFELoginInfo user = getLoginInfo();
		//��¼����ʧ����־��Ϣ
		try{
			//��ȡ��ǰʱ��
			Timestamp now = TSystemFacade.getDBSystemTime();
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//������ˮ��
			sendLog.setSsendorgcode(user.getSorgcode());//���ͻ�������
			sendLog.setSdate(DateUtil.date2String2(new Date(now.getTime())));//��������
			sendLog.setSoperationtypecode(model.getSoperationtypecode());//ҵ��ƾ֤����
			sendLog.setStitle(title);//ƾ֤����
			sendLog.setSsendtime(now);//����ʱ��
			sendLog.setSretcode(ITFECommonConstant.STATUS_FAILED);//������Ϊ�ɹ�����
			sendLog.setSretcodedesc(errMsg);//������˵��
			sendLog.setSusercode(user.getSuserCode());//������
			//���淢����־
			DatabaseFacade.getDb().create(sendLog);
		}catch(Exception ex){
			setRollbackOnly();
			log.error("��¼��������־ʱ��������",ex);
			throw new ITFEBizException(ex);
		}
	}

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

	public TsOrganDto getDefaultConnOrgs() throws ITFEBizException {
		try{
			return StampFacade.getDefaultConnOrgs();
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * ���ع���֤�������
	 */
	public String getCertContent() throws ITFEBizException {
		return KoalPkcs7Encrypt.getInstance().getStrCerContent();
	}

	/**
	 * �������͵��ļ��Ƿ��Ѿ����ͣ����������ô��ʾ�û�
	 */
	public boolean isFileExists(String fileName) throws ITFEBizException {
		//��ǰ��¼�û���Ϣ
		try{
			ITFELoginInfo user = getLoginInfo();
			String sql = "select count(*) from tv_sendlog where s_sendorgcode='" + user.getSorgcode() + "' and s_title='" + fileName.trim() + "'"
				+ " and (s_retcode='" + ITFECommonConstant.STATUS_FINISHED + "' or s_retcode='" + ITFECommonConstant.STATUS_SUCCESS + "')";
			SQLResults result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			int count = result.getInt(0, 0);
			if (count > 0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			log.error("��ѯ�Ѿ����յ�ҵ��ƾ֤����", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 *  ���ָ��ҵ��ƾ֤��ƾ֤���
	 *  ���ҵ��ƾ֤��ƾ֤����Ѿ����ڣ���ô���ظ�ƾ֤���
	 *  Ŀǰʹ��TV_GRANTPAYFILESUB����Ϊ�����ˮ��ű����ֶ��������£�
	 *  S_ORGCODE�� ��������
	 *  I_NOBEFOREPACKAGE�� ƾ֤���
	 *  S_FUNSUBJECTCODE��  ƾ֤���ƣ��ļ�����
	 *  S_ECOSUBJECTCODE��  ƾ֤����
	 *  N_MONEY��           ƾ֤���
	 *  S_USERCODE��        ��Ӧ������ˮ�ţ�û�з��͵�Ϊ'00'
	 *  �����ֶ�������
	 *  @param vouType�� ƾ֤����
	 *  @param fileName: ƾ֤���ƣ��ļ�����
	 *  @return
	 *  >0: ҵ��ƾ֤���
	 */
	public int addVouNo(String vouType, String fileName) throws ITFEBizException {
		try{
			//��ǰ��¼�û���Ϣ
			ITFELoginInfo user = getLoginInfo();
			//��ȡ��ǰʱ��
			Timestamp now = TSystemFacade.getDBSystemTime();
			//��ȡ��ǰ���
			String year = DateUtil.date2String2(new Date(now.getTime())).substring(0,4);
			//��鱾��������ҵ��ƾ֤�Ƿ��Ѿ��ɹ����͵�ƾ֤�ĸ�����������ƾ֤��
			String sql = "select count(*) from tv_sendlog where s_sendorgcode='" + user.getSorgcode() + "' and S_OPERATIONTYPECODE='" + vouType.trim() + "'"
				+ " and substr(S_DATE,1,4)='" + year + "' and s_retcode>='" + ITFECommonConstant.STATUS_SUCCESS + "'";
			SQLResults result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			int count = result.getInt(0, 0) + 1;//ƾ֤���
			//��鱾���Ѿ��򿪣�����û�з��͵�ƾ֤�ĸ�������Щƾ֤ռ����ƾ֤���
			sql = "select count(*) from TV_GRANTPAYFILESUB where S_ORGCODE='" + user.getSorgcode() + "' and S_ECOSUBJECTCODE='" + vouType.trim() + "'"
			+ " and N_MONEY=" + year + " and S_USERCODE='00'";
			result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			count += result.getInt(0, 0);
			//�����Ѿ����ɵ�ƾ֤��ż�¼
			TvGrantpayfilesubDto one = new TvGrantpayfilesubDto();
			one.setSorgcode(user.getSorgcode());
			one.setInobeforepackage(count);
			one.setSfunsubjectcode(fileName);
			one.setSecosubjectcode(vouType);
			one.setNmoney(new BigDecimal(year));
			one.setSusercode("00");
			DatabaseFacade.getDb().create(one);
			return count;
		}catch(Exception e){
			setRollbackOnly();
			log.error("����ҵ��ƾ֤���ʱ��������", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 *  ���ָ��ҵ��ƾ֤��ƾ֤���
	 *  ���ҵ��ƾ֤��ƾ֤����Ѿ����ڣ���ô���ظ�ƾ֤���
	 *  Ŀǰʹ��TV_GRANTPAYFILESUB����Ϊ�����ˮ��ű����ֶ��������£�
	 *  S_ORGCODE�� ��������
	 *  I_NOBEFOREPACKAGE�� ƾ֤���
	 *  S_FUNSUBJECTCODE��  ƾ֤���ƣ��ļ�����
	 *  S_ECOSUBJECTCODE��  ƾ֤����
	 *  N_MONEY��           ���
	 *  S_USERCODE��        ��Ӧ������ˮ�ţ�û�з��͵�Ϊ'00'
	 *  �����ֶ�������
	 *  @param vouType�� ƾ֤����
	 *  @param fileName: ƾ֤���ƣ��ļ�����
	 *  @return
	 *  >0: ҵ��ƾ֤���
	 *  -1��ҵ��ƾ֤�Ѿ����ͣ������ٴη���
	 *  -2��ҵ��ƾ֤�Ѿ����ϣ���Ҫ�������ɱ��
	 *  -3��ҵ��ƾ֤�����ڣ���Ҫ���ɱ��
	 */
	public int getVouNo(String vouType, String fileName) throws ITFEBizException {
		try{
			int ret = 0;
			//��ǰ��¼�û���Ϣ
			ITFELoginInfo user = getLoginInfo();
			//���ҵ��ƾ֤�Ƿ��Ѿ��ɹ����ͣ��ɹ����͵�ƾֻ֤�ܴ�ӡ
			String sql = "select count(*) from tv_sendlog where s_sendorgcode='" + user.getSorgcode() + "' and s_title='" + fileName.trim() + "'"
				+ " and (s_retcode='" + ITFECommonConstant.STATUS_FINISHED + "' or s_retcode='" + ITFECommonConstant.STATUS_SUCCESS + "')";
			SQLResults result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			int count = result.getInt(0, 0);
			if (count > 0){
				return -1;
			}
			//���ҵ��ƾ֤�Ƿ��Ѿ����ϣ����ϵ�ƾ֤��Ҫ��������ƾ֤���
			sql = "select count(*) from tv_sendlog where s_sendorgcode='" + user.getSorgcode() + "' and s_title='" + fileName.trim() + "'"
				+ " and s_retcode='" + ITFECommonConstant.STATUS_CANCELED + "'";
			result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			count = result.getInt(0, 0);
			if (count > 0){
				ret = -2;//������ƾ֤����ƾ֤����
			}
			//���ҵ��ƾ֤û�б����͹�����ô����ż�¼���Ƿ���ڸ�ҵ��ƾ֤�ı��
			//ϵͳҪΪ���д򿪵�ҵ��ƾ֤����ƾ֤��ţ���ʱ�û�ֻ�Ǵ���ҵ��ƾ֤����û�з��ͣ���ʱû�з��ͼ�¼
			//����ƾ֤����Ѿ������ˣ���������ֻ��û�з���ƾ֤��Ӧ��ʹ����ͬ��ƾ֤���
			//Ŀǰʹ��TV_GRANTPAYFILESUB����Ϊ�����ˮ��ű�ֻ����δ���͹���ҵ��ƾ֤
			sql = "select S_ORGCODE,I_NOBEFOREPACKAGE,S_FUNSUBJECTCODE,S_ECOSUBJECTCODE,N_MONEY,S_ACCATTRIB,S_USERCODE from TV_GRANTPAYFILESUB" 
				+ " where S_ORGCODE='" + user.getSorgcode() + "' and S_FUNSUBJECTCODE='" + fileName.trim() + "' and S_USERCODE='00'";
			Collection list = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql, TvGrantpayfilesubDto.class).getDtoCollection();
			if ((list == null) || (list.size() == 0)){
				if (ret < 0)
					return ret;
				else
					return -3;
			}
			return ((TvGrantpayfilesubDto)list.toArray()[0]).getInobeforepackage();
		}catch(Exception e){
			log.error("��ѯҵ��ƾ֤���ʱ��������", e);
			throw new ITFEBizException(e);
		}
	}

}