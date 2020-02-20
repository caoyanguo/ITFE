package com.cfcc.itfe.msgmanager.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractServiceManagerServer;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.security.DESPlus;
import com.cfcc.itfe.util.ParserXml;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class PayOutServiceImpl extends AbstractServiceManagerServer {
	
	private static Log logger = LogFactory.getLog(PayOutServiceImpl.class);
	
	/**
	 * ����ӿͻ��˴��͹������ļ�����(ʵ���ʽ�ҵ����)
	 * 
	 * @param FileResultDto
	 *            fileResultDto �ļ����������DTO
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	public void dealFileDto(FileResultDto fileResultDto, String sorgcode,
			String susercode) throws ITFEBizException {

		try {
			// ��һ��У�鵼���ļ����ظ���
			boolean repeatflag = VerifyFileName.verifyImportRepeat(sorgcode, fileResultDto.getSfilename());
			if (repeatflag) {
				// ���ظ�����,��ʾ�û�������Ϣ.
				logger.error("���ļ�[" + fileResultDto.getSfilename() + "]�Ѿ�����ɹ�,�����ظ�����!");
				throw new ITFEBizException("���ļ�[" + fileResultDto.getSfilename() + "]�Ѿ�����ɹ�,�����ظ�����!");
			}
			
			FileObjDto fileobj = PublicSearchFacade.getFileObjByFileName(fileResultDto.getSfilename());
			
			// ��һ�� �õ���������
			String msgcontent = fileResultDto.getSmaininfo();
			
			// �ڶ��� ȡ�û�����Կ(����-����)
			TsInfoconnorgDto keydto = PublicSearchFacade.findOrgKeyDto(sorgcode, fileobj.getStaxorgcode());
			String encryptKey = keydto.getSkey();
			DESPlus deskey = new DESPlus();
			String key = deskey.decrypt(encryptKey); 
			
			// ������ �Ա��Ľ��н�����
			DESPlus desmsg = new DESPlus(key);
			String msg = null;
			try{
				msg = desmsg.decrypt(msgcontent);
			}catch(Exception e)
			{
				logger.error("ʵ���ʽ����ʧ�ܣ�", e);
				throw new ITFEBizException("ʵ���ʽ����ʧ�ܣ�", e);
			}
			
			// ���Ĳ� �����ܺ���ļ����ص�������
			String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String tmpfilepath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
			FileUtil.getInstance().writeFile(tmpfilepath, msg);
			
			ParserXml parse = new ParserXml();
			List list = parse.parser5101Xml(tmpfilepath, sorgcode, fileResultDto.getSfilename(), susercode);
			
			BigDecimal inputamt = fileResultDto.getFsumamt(); // ����¼���ܽ��
			BigDecimal fileamt = (BigDecimal) list.get(1); // �ļ��������ܽ��
			
			if(inputamt.compareTo(fileamt) != 0){
				logger.error("¼���ļ����[" + inputamt +"]���ļ�ʵ�ʽ��[" + fileamt +"]�����,��ȷ��!");
				throw new ITFEBizException("¼���ļ����[" + inputamt +"]���ļ�ʵ�ʽ��[" + fileamt +"]�����,��ȷ��!");
			}
			
			// ���Ĳ�  �����¼
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSorgcode(sorgcode); // ��������
			packdto.setStrecode(fileobj.getStrecode()); // �������
			packdto.setSfilename(fileResultDto.getSfilename()); // �����ļ���
			packdto.setStaxorgcode(fileobj.getStaxorgcode()); // ���ش���
			packdto.setScommitdate(fileobj.getSdate()); // ί������
			packdto.setSaccdate(TimeFacade.getCurrentStringTime()); // ��������
			packdto.setSpackageno(fileobj.getSpackno()); // ����ˮ��
			packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_PAY_OUT); // ҵ������
			packdto.setIcount((Integer) list.get(0)); // �ܱ���
			packdto.setNmoney(fileamt); // �ܽ��
			packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
			packdto.setSusercode(susercode); // ����Ա����
			packdto.setImodicount(1);
			
			List<TvPayoutmsgmainDto> mainlist = (List<TvPayoutmsgmainDto>) list.get(2);
			List<TvPayoutmsgsubDto> sublist = (List<TvPayoutmsgsubDto>) list.get(3);
			
			TvPayoutmsgmainDto[] mains = new TvPayoutmsgmainDto[mainlist.size()];
			mains = mainlist.toArray(mains);
			
			TvPayoutmsgsubDto[] subs = new TvPayoutmsgsubDto[sublist.size()];
			subs = sublist.toArray(subs);

			try {
				DatabaseFacade.getDb().create(mains);
				//�տ����кŽ���У�飺�����˺ű��в�������ϵͳ����
				VerifyParamTrans.verifyBankno(sorgcode, fileResultDto.getSfilename());
				DatabaseFacade.getDb().create(subs);
				
				//DatabaseFacade.getDb().create(packdto);
			} catch (JAFDatabaseException e) {
				logger.error("����ʵ���ʽ�ҵ����Ϣ���ʱ������쳣��", e);
				throw new ITFEBizException("���汣��ʵ���ʽ�ҵ����Ϣ���ʱ������쳣��", e);
			}
			
			// ���岽 ���ս������кŷ����������,��¼���ͱ�����־,����ҵ������İ���ˮ��
			List<TvFilepackagerefDto> packlist = new ArrayList<TvFilepackagerefDto>();
			String selectSQL = "select COUNT(N_MONEY),SUM(N_MONEY),S_PAYEEBANKNO from TV_PAYOUTMSGMAIN where "
					+ "S_FILENAME = ? and S_ORGCODE = ? group by S_PAYEEBANKNO";

			String updateSQL = "update TV_PAYOUTMSGMAIN  set S_PACKAGENO = ? where "
				+ "S_FILENAME = ? and S_ORGCODE = ? and S_PAYEEBANKNO = ?";
			SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults sqlRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = sqlRs.getRowCount();
			String spackno = "";
			SQLExecutor updatesqlExec = null;
			for (int i = 0; i < count; i++) {
				TvFilepackagerefDto packetdto = new TvFilepackagerefDto();
				packetdto = (TvFilepackagerefDto)packdto.clone();
				packetdto.setIcount(sqlRs.getInt(i, 0));
				packetdto.setNmoney(sqlRs.getBigDecimal(i, 1));
				spackno =  String.format("%03d", i+1);
				spackno = spackno + packdto.getSpackageno().substring(3);
				packetdto.setSpackageno(spackno);
				packlist.add(packetdto);
				
				//������ ����ʵ���ʽ�����İ���ˮ�ţ����ͷ��Ϣ���Ӧ
				updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				updatesqlExec.addParam(spackno);
				updatesqlExec.addParam(fileResultDto.getSfilename());
				updatesqlExec.addParam(sorgcode);
				updatesqlExec.addParam(sqlRs.getString(i, 2));
				updatesqlExec.runQuery(updateSQL);
			}
			try {
				TvFilepackagerefDto packetdtos[] = new TvFilepackagerefDto[packlist.size()];
				packetdtos = packlist.toArray(packetdtos);
				DatabaseFacade.getDb().create(packetdtos);
			} catch (JAFDatabaseException e) {
				logger.error("���汨��ͷ��ʱ������쳣��", e);
				throw new ITFEBizException("���汨��ͷ��ʱ������쳣��", e);
			}finally {
				if (null != updatesqlExec) {
					updatesqlExec.closeConnection();
				}
			}
			
			// ���Ĳ� ���ͱ���
//			this.sendMsg(fileResultDto.getSfilename(), sorgcode, null, MsgConstant.MSG_NO_5101, null, msg ,false);
		} catch (Exception e) {
			logger.error("����ʵ���ʽ�ҵ��ʱ�����쳣!" , e );
			throw new ITFEBizException("����ʵ���ʽ�ҵ��ʱ�����쳣!" , e );
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
