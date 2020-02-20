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
	 * 业务凭证上载的处理方法	 
	 * @generated
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
	public Integer upload(TsOperationmodelDto model,String filePath,List recvOrgs) throws ITFEBizException {
		String fileName = "";//发送文件名
		//当前登录用户信息
		ITFELoginInfo user = getLoginInfo();
		try{
			log.debug("开始检查文件" + filePath);
			//得到上传文件的完整路径
			String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			//读取文件的全部内容，这个变量的内容以后就不要用修改了，可以直接把这些内容写入到接收目录中
			File file = new File(fileFullName);
			fileName = file.getName();//文件名
			String content = FileUtil.getInstance().readFile(fileFullName);//文件的内容
			
			//解数字信封
			String unEncrypt = KoalPkcs7Encrypt.getInstance().pkcs7UnEnvelop(content);
			if (unEncrypt == ""){
				//解数字信封的时候出现错误
				throw new ITFEBizException("解数字信封错误：" + KoalPkcs7Encrypt.getInstance().getLastError());
			}
			
			//验证数字签名
			List<String> ret = KoalPkcs7Encrypt.getInstance().pkcs7VerifySign(unEncrypt);
			if (ret.size() == 0){
				//验证签名的时候出现错误
				throw new ITFEBizException("验证数字签名错误：" + KoalPkcs7Encrypt.getInstance().getLastError());
			}else{
				//解析证书中的CN
				String cn = KoalPkcs7Encrypt.getInstance().parseCertSubject(ret.get(0));
				log.debug("文件" + filePath + "的发起人CN为" + cn);
			}
			String strXml = ret.get(1);//报文原文
			
			//检查业务凭证模版的状态，1-需要验章的凭证，其他-不需要验章
			if (model.getSisuse().charAt(0) == '1'){
				log.debug("验证业务凭证" + fileName + "的电子印鉴");
				//需要验章的业务凭证，其他类型的业务凭证不需要验章
				//获得模版信息
				String modelXml = FileUtil.getInstance().readFileUtf8(ITFECommonConstant.FILE_ROOT_PATH + model.getSmodelsavepath());
				//获得盖章位置
				List<IDto> places = StampFacade.getPlaceByModelId(model.getSmodelid());
				for (IDto one : places){
					TsOperationplaceDto place = (TsOperationplaceDto)one;
					//循环验证电子印鉴
					int flag = StampSendHelper.getInstance().verifyFormStamp(strXml, modelXml, place.getSformid(), place.getSplaceid(), "", 0);
					if (flag != 0){
						String error = "盖章位置" + place.getSplaceid() + "验章不通过：" + (StampSendHelper.getInstance().getLastError()==null?"":StampSendHelper.getInstance().getLastError());
						log.info(error);
						throw new ITFEBizException(error);
					}
				}
			}
			
			//获取当前时间
			Timestamp now = TSystemFacade.getDBSystemTime();
			//将文件保存到接收目录中
			String today = DateUtil.date2String2(new Date(now.getTime()));
			//用日期和业务凭证类型来生成文件保存路径
			String fileSavePath = "/recv/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
				+ today.substring(4) + "/" + model.getSoperationtypecode() + "/";
			//创建保存目录
			FileUtil.getInstance().createDir(ITFECommonConstant.FILE_ROOT_PATH + fileSavePath);
			//将接收到的文件转移到接收目录中
			String fileSaveName = ITFECommonConstant.FILE_ROOT_PATH + fileSavePath + fileName;
			FileUtil.getInstance().writeFile(fileSaveName, content);
			//删除已经上传的文件
			file.delete();
			
			log.debug("生成收发日志");
			//记录发送日志信息
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//发送流水号
			sendLog.setSsendorgcode(user.getSorgcode());//发送机构代码
			sendLog.setSdate(today);//所属日期
			sendLog.setSoperationtypecode(model.getSoperationtypecode());//业务凭证类型
			sendLog.setStitle(fileName);//凭证标题
			sendLog.setSsendtime(now);//发送时间
			sendLog.setSretcode(ITFECommonConstant.STATUS_SUCCESS);//处理码为成功发送
			sendLog.setSretcodedesc("发送成功");//处理码说明
			sendLog.setSusercode(user.getSuserCode());//发送人
			
			//记录文件内容信息
			TvFilesDto fileDto = new TvFilesDto();
			String fileSeq = StampFacade.getStampSendSeq("");
			fileDto.setIno(new Integer(fileSeq));//流水号
			fileDto.setSdate(sendLog.getSdate());//所属日期
			fileDto.setSno(sendSeq);//发送流水号
			fileDto.setIfilelength(new Integer((int)file.length()));//文件长度
			fileDto.setSsavepath(fileSavePath + fileName);//存放路径
			
			//记录接收日志信息
			IDto[] recvLogs = new TvRecvlogDto[recvOrgs.size()];
			String recvOrgNames = "";
			for (int i=0; i<recvOrgs.size(); i++){
				//接收机构信息
				TsOrganDto recvOrg = (TsOrganDto)recvOrgs.get(i);
				//为每一个接收接收写一条接收日志
				TvRecvlogDto recvLog = new TvRecvlogDto();
				String recvSeq = StampFacade.getStampSendSeq("JS");
				recvLog.setSrecvno(recvSeq);//接收流水号
				recvLog.setSsendno(sendSeq);//对应发送日志流水号
				recvLog.setSrecvorgcode(recvOrg.getSorgcode());//接收机构代码
				recvLog.setSdate(today);//所属日期
				recvLog.setSoperationtypecode(model.getSoperationtypecode());//业务凭证类型
				recvLog.setSsendorgcode(user.getSorgcode());//发送机构代码
				recvLog.setStitle(fileName);//凭证标题
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
			//修改业务凭证编号流水中的发送流水号字段，使得每个凭证编号都与已经发送的凭证关联起来
			//使用S_USERCODE字段记录凭证发送流水号
			String sql = "update TV_GRANTPAYFILESUB set S_USERCODE='" + sendSeq + "' where S_ORGCODE='" + user.getSorgcode() 
				+ "' and S_FUNSUBJECTCODE='" + fileName.trim() + "' and S_USERCODE='00'";
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
		}catch(Exception e){
			setRollbackOnly();
			log.error("业务凭证发送时错误：",e);
			throw new ITFEBizException(e);
		}
		return null;
	}
	
	/**
	 * 增加错误发送日志
	 */
	public void addErrorSendLog(TsOperationmodelDto model,String title,String errMsg) throws ITFEBizException {
		//当前登录用户信息
		ITFELoginInfo user = getLoginInfo();
		//记录发送失败日志信息
		try{
			//获取当前时间
			Timestamp now = TSystemFacade.getDBSystemTime();
			TvSendlogDto sendLog = new TvSendlogDto();
			String sendSeq = StampFacade.getStampSendSeq("FS");
			sendLog.setSsendno(sendSeq);//发送流水号
			sendLog.setSsendorgcode(user.getSorgcode());//发送机构代码
			sendLog.setSdate(DateUtil.date2String2(new Date(now.getTime())));//所属日期
			sendLog.setSoperationtypecode(model.getSoperationtypecode());//业务凭证类型
			sendLog.setStitle(title);//凭证标题
			sendLog.setSsendtime(now);//发送时间
			sendLog.setSretcode(ITFECommonConstant.STATUS_FAILED);//处理码为成功发送
			sendLog.setSretcodedesc(errMsg);//处理码说明
			sendLog.setSusercode(user.getSuserCode());//发送人
			//保存发送日志
			DatabaseFacade.getDb().create(sendLog);
		}catch(Exception ex){
			setRollbackOnly();
			log.error("记录错误发送日志时发生错误",ex);
			throw new ITFEBizException(ex);
		}
	}

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

	public TsOrganDto getDefaultConnOrgs() throws ITFEBizException {
		try{
			return StampFacade.getDefaultConnOrgs();
		}catch(Exception e){
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 返回公共证书的内容
	 */
	public String getCertContent() throws ITFEBizException {
		return KoalPkcs7Encrypt.getInstance().getStrCerContent();
	}

	/**
	 * 检查待发送的文件是否已经发送，如果发送那么提示用户
	 */
	public boolean isFileExists(String fileName) throws ITFEBizException {
		//当前登录用户信息
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
			log.error("查询已经接收的业务凭证错误", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 *  获得指定业务凭证的凭证编号
	 *  如果业务凭证的凭证编号已经存在，那么返回该凭证编号
	 *  目前使用TV_GRANTPAYFILESUB表作为编号流水存放表，各字段内容如下：
	 *  S_ORGCODE： 机构代码
	 *  I_NOBEFOREPACKAGE： 凭证编号
	 *  S_FUNSUBJECTCODE：  凭证名称（文件名）
	 *  S_ECOSUBJECTCODE：  凭证种类
	 *  N_MONEY：           凭证编号
	 *  S_USERCODE：        对应发送流水号，没有发送的为'00'
	 *  其他字段无内容
	 *  @param vouType： 凭证种类
	 *  @param fileName: 凭证名称（文件名）
	 *  @return
	 *  >0: 业务凭证编号
	 */
	public int addVouNo(String vouType, String fileName) throws ITFEBizException {
		try{
			//当前登录用户信息
			ITFELoginInfo user = getLoginInfo();
			//获取当前时间
			Timestamp now = TSystemFacade.getDBSystemTime();
			//获取当前年度
			String year = DateUtil.date2String2(new Date(now.getTime())).substring(0,4);
			//检查本年该种类的业务凭证是否已经成功发送的凭证的个数（含作废凭证）
			String sql = "select count(*) from tv_sendlog where s_sendorgcode='" + user.getSorgcode() + "' and S_OPERATIONTYPECODE='" + vouType.trim() + "'"
				+ " and substr(S_DATE,1,4)='" + year + "' and s_retcode>='" + ITFECommonConstant.STATUS_SUCCESS + "'";
			SQLResults result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			int count = result.getInt(0, 0) + 1;//凭证编号
			//检查本年已经打开，但是没有发送的凭证的个数，这些凭证占用了凭证编号
			sql = "select count(*) from TV_GRANTPAYFILESUB where S_ORGCODE='" + user.getSorgcode() + "' and S_ECOSUBJECTCODE='" + vouType.trim() + "'"
			+ " and N_MONEY=" + year + " and S_USERCODE='00'";
			result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			count += result.getInt(0, 0);
			//保存已经生成的凭证编号记录
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
			log.error("生成业务凭证编号时发生错误", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 *  获得指定业务凭证的凭证编号
	 *  如果业务凭证的凭证编号已经存在，那么返回该凭证编号
	 *  目前使用TV_GRANTPAYFILESUB表作为编号流水存放表，各字段内容如下：
	 *  S_ORGCODE： 机构代码
	 *  I_NOBEFOREPACKAGE： 凭证编号
	 *  S_FUNSUBJECTCODE：  凭证名称（文件名）
	 *  S_ECOSUBJECTCODE：  凭证种类
	 *  N_MONEY：           年度
	 *  S_USERCODE：        对应发送流水号，没有发送的为'00'
	 *  其他字段无内容
	 *  @param vouType： 凭证种类
	 *  @param fileName: 凭证名称（文件名）
	 *  @return
	 *  >0: 业务凭证编号
	 *  -1：业务凭证已经发送，不能再次发送
	 *  -2：业务凭证已经作废，需要重新生成编号
	 *  -3：业务凭证不存在，需要生成编号
	 */
	public int getVouNo(String vouType, String fileName) throws ITFEBizException {
		try{
			int ret = 0;
			//当前登录用户信息
			ITFELoginInfo user = getLoginInfo();
			//检查业务凭证是否已经成功发送，成功发送的凭证只能打印
			String sql = "select count(*) from tv_sendlog where s_sendorgcode='" + user.getSorgcode() + "' and s_title='" + fileName.trim() + "'"
				+ " and (s_retcode='" + ITFECommonConstant.STATUS_FINISHED + "' or s_retcode='" + ITFECommonConstant.STATUS_SUCCESS + "')";
			SQLResults result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			int count = result.getInt(0, 0);
			if (count > 0){
				return -1;
			}
			//检查业务凭证是否已经作废，作废的凭证需要重新生成凭证编号
			sql = "select count(*) from tv_sendlog where s_sendorgcode='" + user.getSorgcode() + "' and s_title='" + fileName.trim() + "'"
				+ " and s_retcode='" + ITFECommonConstant.STATUS_CANCELED + "'";
			result = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			count = result.getInt(0, 0);
			if (count > 0){
				ret = -2;//已作废凭证做新凭证处理
			}
			//如果业务凭证没有被发送过，那么检查编号记录中是否存在该业务凭证的编号
			//系统要为所有打开的业务凭证生成凭证编号，有时用户只是打开了业务凭证，并没有发送，这时没有发送记录
			//但是凭证编号已经生成了，对于这种只打开没有发送凭证，应该使用相同的凭证编号
			//目前使用TV_GRANTPAYFILESUB表作为编号流水存放表，只检索未发送过的业务凭证
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
			log.error("查询业务凭证编号时发生错误", e);
			throw new ITFEBizException(e);
		}
	}

}