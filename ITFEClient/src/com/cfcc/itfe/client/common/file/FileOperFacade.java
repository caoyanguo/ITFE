package com.cfcc.itfe.client.common.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpCountryDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;

public class FileOperFacade {
	
	private static Log logger = LogFactory.getLog(FileOperFacade.class);
	
	/**
	 * �����ļ�����ת��DTO
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename) throws ITFEBizException{
		String tmpfilename_new = filename.trim().toLowerCase();
		String tmpfilename = tmpfilename_new.replace(".txt", "");
		if(tmpfilename.length()==13&&MsgConstant.TIME_FLAG_TRIM.equals(tmpfilename.substring(12, 13)))
		{
			ICommonDataAccessService cdas = (ICommonDataAccessService) ServiceFactory.getService(ICommonDataAccessService.class);
			Date adjustdate = cdas.getAdjustDate();
			String str = cdas.getSysDBDate();
			return PublicSearchFacade.getFileObjByFileName(filename,adjustdate,str);
		}else
		{
			return PublicSearchFacade.getFileObjByFileName(filename);
		}
	}
	/**
	 * ���ϴ��������ļ������õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @param ISequenceHelperService
	 *            sequenceHelperService ȡ�ð���ˮ�ŷ���
	 * @return
	 * @deprecated
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealIncomeFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		List<String> packList = new ArrayList<String>();
		int count = 1;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			String packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
			String tmpPackNo = packno + "000";
			packList.add(tmpPackNo);
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				
				if(count == (MsgConstant.TIPS_MAX_OF_PACK + 1) ){
					packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
					tmpPackNo = packno + "000";
					packList.add(tmpPackNo);
					count = 1;
				}

				sb_rtn.append(data);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(filename);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(orgcode);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(SequenceGenerator.changeTraSrlNo(packno, count-1));
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(tmpPackNo);
				sb_rtn.append("\r\n");
				
				recordNum ++ ;
				count ++ ;
			}
			br.close();
			
			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_1103);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setPacknos(packList);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(sb_rtn.toString());
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
					throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
				}
			}
		}
	}
	
	/**
	 * ���ϴ��������ļ������õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealIncomeFile(String filepath, String filename,
			String orgcode)
			throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			FileResultDto fileRstDto = new FileResultDto();
			String data = null;
			int i = 0 ;
			String srlno = "";
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				int lineWithSplitLen = data.split(",").length;
				//�ж������13λ����˵���Ǵ����ʽ�������ˮ�ŵ�
				if(lineWithSplitLen == 13) {
					if(i == 0) {
						fileRstDto.setFileColumnLen(13);
						srlno = data.trim().substring(data.lastIndexOf(",")+1);
						//�����һ�е����ɺž�Ϊ������ʾ�����������Ϊ18λҲ�������ʾ
						if(null == srlno || "".equals(srlno)) {
							throw new ITFEBizException("�ļ����ʽ�������ˮ��Ϊ�գ����֤!");
						}else {
							int srlnoLen = srlno.length();
							if(srlnoLen != 18) {
								throw new ITFEBizException("�ļ����ʽ�������ˮ�ű���Ϊ18λ�����֤!");
							}
						}
						fileRstDto.setStrasrlno(srlno);
					}
					data = data.substring(0, data.lastIndexOf(","));
					i++;
				}
				sb_rtn.append(data);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(filename);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(orgcode);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(String.valueOf(recordNum + 1));
				sb_rtn.append("\r\n");
				
				recordNum ++ ;

			}
			br.close();
			
			
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_7211);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(sb_rtn.toString());
			fileRstDto.setCsourceflag(MsgConstant.MSG_SOURCE_TBS);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
					throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
				}
			}
		}

	}

	/**
	 * ���ϴ��������ļ������õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealIncomeFileTips(String filepath, String filename,
			String orgcode)
			throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}

				sb_rtn.append(data);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(filename);
				sb_rtn.append(StateConstant.INCOME_SPLIT);
				sb_rtn.append(orgcode);
				sb_rtn.append("\r\n");
				
				recordNum ++ ;

			}
			br.close();
			
			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_7211);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(sb_rtn.toString());
			fileRstDto.setCsourceflag(MsgConstant.MSG_SOURCE_TIPS);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
					throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
				}
			}
		}

	}
	
	/**
	 * ���ϴ��������ļ�(��˰)�����õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealIncomeFileForNation(String filepath, String filename,
			String orgcode) throws ITFEBizException{
		
		InputStream inputStream = null ;
	
		try {
			inputStream = new FileInputStream(filepath); 
			DBFReader reader = new DBFReader(inputStream);
			reader.setCharactersetName("GBK");

			List<TvInfileTmpCountryDto> rtnlist = new ArrayList<TvInfileTmpCountryDto>();
			
			Object[] rowObjects;
			int j = 0 ;	
			while ((rowObjects = reader.nextRecord()) != null) {
				TvInfileTmpCountryDto tmpdto = new TvInfileTmpCountryDto();
				
				j = 0 ;
				tmpdto.setStaxticketno((String)change(rowObjects[j++]));//˰Ʊ����
				j++;//������ϵ����
				tmpdto.setScorptypename((String)change(rowObjects[j++])); //�Ǽ�ע����������
				tmpdto.setSbilldate((String)change(rowObjects[j++]));// ��Ʊ����
				tmpdto.setStaxorgcode(splitString((String)change(rowObjects[j++]), "|", null));//���ջ��ش���
				tmpdto.setScorpcode((String)change(rowObjects[j++]));//��˰��ʶ���
				tmpdto.setScorpname((String)change(rowObjects[j++]));//��˰������
				tmpdto.setSpaybnkno(splitString((String)change(rowObjects[j++]), "|", null));//���д���
				tmpdto.setSpayacct((String)change(rowObjects[j++]));//�����˺�
				tmpdto.setSbudgetsubcode((String)change(rowObjects[j++]));//Ԥ���Ŀ����
				j++;//Ԥ���Ŀ����
				tmpdto.setSbudgetlevelcode(splitString((String)change(rowObjects[j++]), "|", null));//Ԥ�㼶�δ���
				tmpdto.setSpayeetrecode(splitString((String)change(rowObjects[j++]), "(", ")"));//�տ��������
				tmpdto.setStaxstartdate((String)change(rowObjects[j++]));// ����ʱ��-��
				tmpdto.setStaxenddate((String)change(rowObjects[j++]));// ����ʱ��-ֹ
				tmpdto.setSlimitdate((String)change(rowObjects[j++]));// ˰���޽�����
				tmpdto.setStaxtypename((String)change(rowObjects[j++]));//����ƷĿ����
				j++;//��˰����
				j++;//�������� 
				j++;//˰��
				tmpdto.setNfacttaxamt(new BigDecimal((String)change(rowObjects[j++])));//ʵ�ɽ��
				tmpdto.setSfilename(filename);
				tmpdto.setSorgcode(orgcode);
				
				rtnlist.add(tmpdto);
			}

			inputStream.close();
			
			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_7211);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setMainlist(rtnlist);
			fileRstDto.setCsourceflag(MsgConstant.MSG_SOURCE_NATION);
			
			return fileRstDto;
		} catch (DBFException e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} catch (IOException e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} finally{
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
					throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
				}
			}
		}
		
		
	}
	
	
	public static FileResultDto dealIncomeFileForITFE(String filepath, String filename,String orgcode) throws ITFEBizException{
		return null;
	}
	
	/**
	 * ���ϴ���ֱ��֧������ļ������õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @param ISequenceHelperService
	 *            sequenceHelperService ȡ�ð���ˮ�ŷ���
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealDirectPayFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		BufferedReader br = null;
		List<String> packList = new ArrayList<String>();

		HashMap<String, Object> map = new HashMap<String, Object>(); // ��ű���ͷ�ؼ��ֶ�ֵ
		boolean headflag = false; // ����ͷ��־
		boolean mainflag = false; // ����������Ϣ��־
		boolean detailflag = false; // ������ϸ��Ϣ��־
		StringBuffer mainbuf = new StringBuffer(); // ���������Ϣ�ֶ�
		StringBuffer detailbuf = new StringBuffer(); // �������Ϣ�ֶ�
		int maincount = 1 ; // ������Ϣ�ְ�������
		int recordNum = 0; // ������Ϣ��¼������
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			String packno = SequenceGenerator
					.changePackNo(sequenceHelperService
							.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
			String tmpPackNo = packno + "000";
			packList.add(tmpPackNo);
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				
				String tmpdata = data.toLowerCase();
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_START) >= 0){
					if(mainflag || detailflag){
						logger.error("�ļ���ʽ����! [�ļ�ͷλ�ò���ȷ]");
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷλ�ò���ȷ]");
					}
					headflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_END) >= 0){
					headflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_START) >= 0){
					if(headflag || detailflag){
						logger.error("�ļ���ʽ����! [�ļ�ͷû�н�����־]");
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷû�н�����־]");
					}
					mainflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_END) >= 0){
					mainflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_START) >= 0){
					if(headflag || mainflag){
						logger.error("�ļ���ʽ����! [�ļ�ͷ���ļ�����û�н�����־]");
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷ���ļ�����û�н�����־]");
					}
					detailflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_END) >= 0){
					detailflag = false;
				}
				
				if(headflag){
					// �ڶ�ȡ�ļ�ͷ��Ϣ
					parserMsgHead(data, map);
				}
				
				if(mainflag){
					// �ڶ�ȡ�ļ�������Ϣ
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String maininfo = parserMsgBody(data);
						maininfo = maininfo.replace(StateConstant.INCOME_SPLIT, "");
						maininfo = maininfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);
						
						if(maincount == ( MsgConstant.TIPS_MAX_OF_PACK + 1) ){
							packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
							tmpPackNo = packno + "000";
							packList.add(tmpPackNo);
							maincount = 1;
						}

						mainbuf.append(maininfo);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(filename);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(orgcode);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(SequenceGenerator.changeTraSrlNo(packno, maincount-1));
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(tmpPackNo);
						mainbuf.append("\r\n");
						
						recordNum ++ ;
						maincount ++ ;
					}
				}
				
				if(detailflag){
					// �ڶ�ȡ�ļ���ϸ��Ϣ
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String detailinfo = parserMsgBody(data);
						detailinfo = detailinfo.replace(StateConstant.INCOME_SPLIT, "");
						detailinfo = detailinfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);

						detailbuf.append(detailinfo);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(filename);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(orgcode);
						detailbuf.append("\r\n");
					}
				}
			}
			
			br.close();

			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_5102);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setPacknos(packList);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(mainbuf.toString());
			fileRstDto.setSdetailinfo(detailbuf.toString());
			fileRstDto.setMap(map);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
					throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
				}
			}
		}
	}
	
	/**
	 * ���ϴ�����Ȩ֧������ļ������õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @param ISequenceHelperService
	 *            sequenceHelperService ȡ�ð���ˮ�ŷ���
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealGrantPayFile(String filepath, String filename,
			String orgcode, ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		BufferedReader br = null;
		List<String> packList = new ArrayList<String>();

		HashMap<String, Object> map = new HashMap<String, Object>(); // ��ű���ͷ�ؼ��ֶ�ֵ
		boolean headflag = false; // ����ͷ��־
		boolean mainflag = false; // ����������Ϣ��־
		boolean detailflag = false; // ������ϸ��Ϣ��־
		StringBuffer mainbuf = new StringBuffer(); // ���������Ϣ�ֶ�
		StringBuffer detailbuf = new StringBuffer(); // �������Ϣ�ֶ�
		int subcount = 1 ; // ��ϸ��Ϣ�ְ�������
		int recordNum = 0; // ��ϸ��Ϣ��¼������
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filepath)));
			String data = null;
			
			String packno = SequenceGenerator
					.changePackNo(sequenceHelperService
							.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
			String tmpPackNo = packno + "000";
			packList.add(tmpPackNo);
			
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				
				String tmpdata = data.toLowerCase();
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_START) >= 0){
					if(mainflag || detailflag){
						logger.error("�ļ���ʽ����! [�ļ�ͷλ�ò���ȷ]");
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷλ�ò���ȷ]");
					}
					headflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_END) >= 0){
					headflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_START) >= 0){
					if(headflag || detailflag){
						logger.error("�ļ���ʽ����! [�ļ�ͷû�н�����־]");
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷû�н�����־]");
					}
					mainflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_END) >= 0){
					mainflag = false;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_START) >= 0){
					if(headflag || mainflag){
						logger.error("�ļ���ʽ����! [�ļ�ͷ���ļ�����û�н�����־]");
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷ���ļ�����û�н�����־]");
					}
					detailflag = true;
				}
				if(tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_END) >= 0){
					detailflag = false;
				}
				
				if(headflag){
					// �ڶ�ȡ�ļ�ͷ��Ϣ
					parserMsgHead(data, map);
				}
				
				if(mainflag){
					// �ڶ�ȡ�ļ�������Ϣ
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String maininfo = parserMsgBody(data);
						maininfo = maininfo.replace(StateConstant.INCOME_SPLIT, "");
						maininfo = maininfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);
				
						mainbuf.append(maininfo);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(filename);
						mainbuf.append(StateConstant.INCOME_SPLIT);
						mainbuf.append(orgcode);
						mainbuf.append("\r\n");
					}
				}
				
				if(detailflag){
					// �ڶ�ȡ�ļ���ϸ��Ϣ
					if(tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >=0){
						String detailinfo = parserMsgBody(data);
						detailinfo = detailinfo.replace(StateConstant.INCOME_SPLIT, "");
						detailinfo = detailinfo.replace(StateConstant.PAYOUT_SPLIT, StateConstant.INCOME_SPLIT);

						if(subcount == ( MsgConstant.TIPS_MAX_OF_PACK + 1) ){
							packno = SequenceGenerator.changePackNo(sequenceHelperService.getSeqNo(SequenceName.TRA_ID_SEQUENCE_KEY));
							tmpPackNo = packno + "000";
							packList.add(tmpPackNo);
							subcount = 1;
						}
						
						detailbuf.append(detailinfo);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(tmpPackNo);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(SequenceGenerator.changeTraSrlNo(packno, subcount-1));
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(filename);
						detailbuf.append(StateConstant.INCOME_SPLIT);
						detailbuf.append(orgcode);
						detailbuf.append("\r\n");

						recordNum ++ ;
						subcount ++ ;
					}
				}
			}
			
			br.close();

			FileResultDto fileRstDto = new FileResultDto();
			fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ);
			fileRstDto.setSmsgno(MsgConstant.MSG_NO_5103);
			fileRstDto.setIallnum(recordNum);
			fileRstDto.setPacknos(packList);
			fileRstDto.setSfilename(filename.toLowerCase());
			fileRstDto.setSmaininfo(mainbuf.toString());
			fileRstDto.setSdetailinfo(detailbuf.toString());
			fileRstDto.setMap(map);
			
			return fileRstDto;
		} catch (Exception e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
					throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
				}
			}
		}
	}
	
	/**
	 * ���ϴ���ʵ���ʽ�ҵ���ļ������õ�������һ���ļ�����DTO(�ͻ��˵���)
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto dealPayOutFile(String filepath, String filename,
			String orgcode) throws ITFEBizException{
		
		String message = null;
		try {
			message = readFile(filepath);
		} catch (FileNotFoundException e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		} catch (IOException e) {
			logger.error("��ȡ�ļ�[" + filename + "]�����쳣", e);
			throw new ITFEBizException("��ȡ�ļ�[" + filename + "]�����쳣", e);
		}
		
		FileResultDto fileRstDto = new FileResultDto();
		fileRstDto.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT);
		fileRstDto.setSmsgno(MsgConstant.MSG_NO_5101);
		fileRstDto.setSfilename(filename.toLowerCase());
		fileRstDto.setSmaininfo(message);
		
		return fileRstDto;
	}
	
	
	/**
	 * �����ļ�������
	 * 
	 * @param String
	 *            values �ļ�����Ϣ
	 * @return
	 */
	public static String parserMsgBody(String values) throws ITFEBizException {
		String value = values.toLowerCase();

		if (value.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >= 0
				&& value.indexOf(MsgConstant.MSG_FLAG_CONTENT_END) >= 0) {
			return value.substring(value
					.indexOf(MsgConstant.MSG_FLAG_CONTENT_START)
					+ MsgConstant.MSG_FLAG_CONTENT_START.length(), value
					.indexOf(MsgConstant.MSG_FLAG_CONTENT_END));
		}

		throw new ITFEBizException("�ļ���[" + value +  "]û�н�β��־��!");
	}
	
	/**
	 * ��������ͷ��Ϣ
	 * 
	 * @param String
	 *            values �����ֶ�
	 * @param HashMap
	 *            <String, Object> map �洢�ؼ��ֶ�
	 */
	public static void parserMsgHead(String values, HashMap<String, Object> map) {

		/**
		 * �ļ�ͷ��ʽ
		 * <pub>
		 * <code>301</code>
		 * <msgnumber>0101</msgnumber>
		 * <czjname>zjsczt</czjname>  <��Ҫ�ֶ�>
		 * <sender>01</sender>
		 * <receiver>0300</receiver>  <��Ҫ�ֶ�>
		 * <msgstatus>0</msgstatus>
		 * </pub>
		 */

		String value = values.toLowerCase();
		
		if (value.indexOf(MsgConstant.MSG_FLAG_CZJNAME_START) >= 0
				&& value.indexOf(MsgConstant.MSG_FLAG_CZJNAME_END) >= 0) {
			String czjname = value.substring(value
					.indexOf(MsgConstant.MSG_FLAG_CZJNAME_START)
					+ MsgConstant.MSG_FLAG_CZJNAME_START.length(), value
					.indexOf(MsgConstant.MSG_FLAG_CZJNAME_END));
			
			map.put(MsgConstant.MSG_FLAG_CZJNAME, czjname);
		}
		
		if (value.indexOf(MsgConstant.MSG_FLAG_RECEIVER_START) >= 0
				&& value.indexOf(MsgConstant.MSG_FLAG_RECEIVER_END) >= 0) {
			String czjname = value.substring(value
					.indexOf(MsgConstant.MSG_FLAG_RECEIVER_START)
					+ MsgConstant.MSG_FLAG_RECEIVER_START.length(), value
					.indexOf(MsgConstant.MSG_FLAG_RECEIVER_END));
			
			map.put(MsgConstant.MSG_FLAG_RECEIVER, czjname);
		}
	}
	
	/**
	 * ��InputStream�е����ݶ�ȡ���ַ�����
	 * @param in ������
	 * @return ��¼�������ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer();
		if (in == null) {
			// �����Ϊ�գ���ô���ؿ��ַ���
			return content.toString();
		}
		char[] buf = new char[4096];
		int ret = 0;
		BufferedReader bin = new BufferedReader(new InputStreamReader(in,"gb2312"));
		ret = bin.read(buf);
		while (ret > 0) {
			content.append(new String(buf, 0, ret));
			ret = bin.read(buf);
		}
		bin.close();
		return content.toString();
	}

	/**
	 * ���ļ��е����ݶ�ȡ���ַ�����
	 * @param file �ļ�����
	 * @return �����ļ����ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(File file) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(file));
	}
	
	/**
	 * ���ļ��е����ݶ�ȡ���ַ�����
	 * @param fileName �ļ��ľ���·��
	 * @return �����ļ����ݵ��ַ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws FileNotFoundException,IOException{
		return readFile(new FileInputStream(fileName));
	}

	/**
	 * ���ַ����е����ݣ�д��ָ���ļ���
	 * ���ָ���ļ������ļ��Ѿ����ڣ���ô�ᱻ����
	 * @param fileName Ҫд����ļ���
	 * @param content  �ļ�����
	 * @return
	 */
	public static void writeFile(String fileName, String content) throws FileNotFoundException,IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
		out.write(content.toCharArray());
		out.flush();
		out.close();
	}
	
	/**
	 * ���ַ�����ͷλ�õ�ASCII��С�ڵ���30���ո񣩵��ַ�ȥ��
	 * �����ǻس����е��ַ�
	 * @param src Դ�ַ���
	 * @return ��ͷ�����س������С��ո�Ȳ��ɼ��ַ����ַ���
	 */
	public static String ltrim(String src){
		int i=0;
		for (i=0; i<src.length(); i++){
			if (src.charAt(i) > 32){
				break;
			}
		}
		return src.substring(i);
	}
	
	/**
	 * ���ַ�����β��ASCII��С�ڵ���30���ո񣩵��ַ�ȥ��
	 * �����ǻس����е��ַ�
	 * @param src Դ�ַ���
	 * @return ��β�����س������С��ո�Ȳ��ɼ��ַ����ַ���
	 */
	public static String rtrim(String src){
		int i=src.length() - 1;
		for (; i>=0; i--){
			if (src.charAt(i) > 32){
				break;
			}
		}
		return src.substring(0,i + 1);
	}
	
	/**
	 * ���ַ�����ͷ�ͽ�β��ASCII��С�ڵ���30���ո񣩵��ַ�ȥ��
	 * �����ǻس����е��ַ�
	 * @param src Դ�ַ���
	 * @return ��ͷ�ͽ�β�����س������С��ո�Ȳ��ɼ��ַ����ַ���
	 */
	public static String trim(String src){
		return ltrim(rtrim(src));
	}
	
	/**
	 * �жϴ����·���Ƿ���ڣ������������ô����Ŀ¼���������κβ���
	 * @param dir Ҫ����Ŀ¼
	 * @return true-Ŀ¼�Ѿ����ڣ�false-Ŀ¼����ʧ��
	 */
	public static boolean createDir(String dir){
		File file = new File(dir);
		if (!file.exists()){
			file.mkdirs();
		}
		return true;
	}
	
	/**
	 * ���ݴ�����ļ����·�����߾���·��������ļ���
	 * @param file �ļ����·�����߾���·��
	 * @return �ļ���
	 */
	public static String getFileName(String file){
		int pos = file.lastIndexOf('/');
		if (pos == -1){
			pos = file.lastIndexOf('\\');
		}
		return file.substring(pos + 1);
	}
	
	/**
	 * ɾ��ָ�����ļ�
	 * @param filePath Ҫɾ�����ļ��ľ���·��
	 */
	public static void deleteFile(String filePath){
		File file = new File(filePath);
		file.deleteOnExit();
	}

	/**
	 * ���ļ�ת��
	 * @param obj
	 * @return
	 */
	public static Object change(Object obj){
		if(null == obj){
			return "";
		}else{
			if(obj instanceof String){
				return ((String) obj).trim();
			}else if(obj instanceof Integer){
				return obj.toString();
			}else if(obj instanceof Double){
				return obj.toString();
			}else if(obj instanceof java.util.Date){
				long ltime = ((java.util.Date)(obj)).getTime();
				return DateUtil.date2String2(new java.sql.Date(ltime));
			}
			
			return obj;
		}
	}
	
	public static String splitString(String obj, String split1, String split2) throws ITFEBizException {
		if ("|".equals(split1)) {
			int iPos = obj.indexOf("|");
			if (iPos >= 0) {
				return obj.substring(iPos + 1);// ���ջ��ش���
			} else {
				throw new ITFEBizException("����[" + obj + "]�����Ϲ淶��");
			}
		} else if (("(".equals(split1) || "��".equals(split1)) && (")".equals(split2) || "��".equals(split2))) {
			int iPos = obj.indexOf("(");
			if (iPos < 0) {
				iPos = obj.indexOf("��");
			}
			if (iPos >= 0) {
				return obj.substring(iPos + 1, obj.length() - 1);
			}else{
				throw new ITFEBizException("����[" + obj + "]�����Ϲ淶��");
			}
		} else {
			throw new ITFEBizException("����[" + obj + "]�����Ϲ淶��");
		}
	}
}
