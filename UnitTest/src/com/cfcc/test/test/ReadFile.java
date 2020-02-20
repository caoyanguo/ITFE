package com.cfcc.test.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;

public class ReadFile {
	
	public static final String INCOME_SPLIT = ","; // ����ҵ��ķָ����
	public static final String PAYOUT_SPLIT = "|"; // ֧��ҵ��ķָ����
	
	/**
	 * @param args
	 * @throws ITFEBizException 
	 */
	public static void main(String[] args) throws ITFEBizException {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"D:\\����ǰ�������ļ�\\����\\20091111010130199.xml")));
			String data = null;
			boolean headflag = false; // ����ͷ��־
			boolean mainflag = false; // ����������Ϣ��־
			boolean detailflag = false; // ������ϸ��Ϣ��־
			StringBuffer mainbuf = new StringBuffer(); // ���������Ϣ�ֶ�
			StringBuffer detailbuf = new StringBuffer(); // �������Ϣ�ֶ�
			int maincount = 1; // ������Ϣ�ְ�������
			int recordNum = 0; // ������Ϣ��¼������

			while ((data = br.readLine()) != null) {
				if (data.trim().equals("")) {
					continue;
				}

				String tmpdata = data.toLowerCase();
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_START) >= 0) {
					if (mainflag || detailflag) {
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷλ�ò���ȷ]");
					}
					headflag = true;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_HEAD_END) >= 0) {
					headflag = false;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_START) >= 0) {
					if (headflag || detailflag) {
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷû�н�����־]");
					}
					mainflag = true;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_MAIN_END) >= 0) {
					mainflag = false;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_START) >= 0) {
					if (headflag || mainflag) {
						throw new ITFEBizException("�ļ���ʽ����! [�ļ�ͷ���ļ�����û�н�����־]");
					}
					detailflag = true;
				}
				if (tmpdata.indexOf(MsgConstant.MSG_FLAG_DETAIL_END) >= 0) {
					detailflag = false;
				}

				if (mainflag) {
					// �ڶ�ȡ�ļ�������Ϣ
					if (tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >= 0) {
						String maininfo = parserMsgBody(data);
						maininfo = maininfo.replace(PAYOUT_SPLIT,
								INCOME_SPLIT);
						mainbuf.append(maininfo);
						mainbuf.append("\r\n");

						recordNum++;
						maincount++;
						
						System.out.println(maininfo);
					}
				}

				if (detailflag) {
					// �ڶ�ȡ�ļ���ϸ��Ϣ
					if (tmpdata.indexOf(MsgConstant.MSG_FLAG_CONTENT_START) >= 0) {
						String detailinfo = parserMsgBody(data);
						detailinfo = detailinfo.replaceAll(PAYOUT_SPLIT,
								INCOME_SPLIT);

						detailbuf.append(detailinfo);
						detailbuf.append(INCOME_SPLIT);
						detailbuf.append("\r\n");
					}
				}
			}

		} catch (Exception e) {
			throw new ITFEBizException("��ȡ�ļ�[" + "123" + "]�����쳣", e);
		}
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
	
	

}
