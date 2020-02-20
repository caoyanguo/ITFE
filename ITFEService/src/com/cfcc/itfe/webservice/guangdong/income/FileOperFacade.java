package com.cfcc.itfe.webservice.guangdong.income;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;

/**
 * Ϊ�˹㶫webservice������ӿڶ��ӿͻ�����ֲ����
 * 
 * @author hua
 * 
 */
public class FileOperFacade {

	private static Log logger = LogFactory.getLog(FileOperFacade.class);

	/**
	 * �����ļ�����ת��DTO
	 * 
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename) throws ITFEBizException {
		return PublicSearchFacade.getFileObjByFileName(filename);
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
	public static FileResultDto dealIncomeFile(String filepath, String filename, String orgcode) throws ITFEBizException {
		StringBuffer sb_rtn = new StringBuffer();
		BufferedReader br = null;
		int recordNum = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
			FileResultDto fileRstDto = new FileResultDto();
			String data = null;
			int i = 0;
			String srlno = "";
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}
				int lineWithSplitLen = data.split(",").length;
				// �ж������13λ����˵���Ǵ����ʽ�������ˮ�ŵ�
				if (lineWithSplitLen == 13) {
					if (i == 0) {
						fileRstDto.setFileColumnLen(13);
						srlno = data.trim().substring(data.lastIndexOf(",") + 1);
						// �����һ�е����ɺž�Ϊ������ʾ�����������Ϊ18λҲ�������ʾ
						if (null == srlno || "".equals(srlno)) {
							throw new ITFEBizException("�ļ����ʽ�������ˮ��Ϊ�գ����֤!");
						} else {
							int srlnoLen = srlno.length();
							if (srlnoLen != 18) {
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

				recordNum++;

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
}
