package com.cfcc.itfe.verify;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * ��Ҫ����:У�����SHELL�����Ƿ�ɹ� 
 * @author zhouchuan
 *
 */
public class VerifyCallShellRs {

	/**
	 * У�����SHELL��Ĵ�����(˰Ʊ����)
	 * 
	 * @param String
	 *            results ����SHELL�ķ��ؽ��
	 */
	public static void verifyShellForIncomeRs(String results) throws ITFEBizException{
		// ָ����ʾ�����¼����  
		boolean rsFlag = true;
		int count = 0;
		StringBuffer errbuf = new StringBuffer(); // �ܵĴ�������
		StringBuffer buf3125 = new StringBuffer(); // �ضϴ���
		StringBuffer buf3124 = new StringBuffer(); // �������ת������
		StringBuffer buf3137 = new StringBuffer(); // ������ȱ�ٴ���
		StringBuffer resultbuf = new StringBuffer(); // ִ�е���Ľ��(��ʱ�鿴�ܾ�������)
		
		String[] strs = results.split(StateConstant.SPACE_SPLIT);
		for(int i = 0 ; i < strs.length ; i++){
			if(null != strs[i]){
				if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3125W) >= 0){
					/**
					 * ��ʾ�����ݱ��ض�
					 * ����:SQL3125W  ���� "1" �� "3" �е��ַ����ݱ��ضϣ���Ϊ�����ݱ�Ŀ�����ݿ��и�����
					 */
					count ++;
					buf3125 = buf3125.append(getErrInfo(DealCodeConstants.DB2_SQL3125W, strs[i]));
				}else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3124W) >= 0){
					/**
					 * ��ʾ������ת���ɽ���ֶ�ʧ�� 
					 * ����:SQL3124W  ���� "1" �� "12" �е��ֶ�ֵ����ת��Ϊ PACKED DECIMAL
					 */
					count ++;
					buf3124 = buf3124.append(getErrInfo(DealCodeConstants.DB2_SQL3124W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3137W) >= 0){
					/**
					 * ��ʾ��������̫��,ȱ���ֶ�
					 * ����:SQL3137W  �� "1" ̫�̡����ٶ�ʧ��һ����װ��ǿɿ��е�����ֵ��δװ����С�
					 */
					count++;
					buf3137 = buf3137.append(getErrInfo(DealCodeConstants.DB2_SQL3137W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_REJECT_LINE) >= 0
						||( strs[i].toLowerCase().indexOf(DealCodeConstants.DB2_REJECT_LINE_ENGLISH) >= 0 &&
								strs[i].indexOf("=") >= 0)){
					/**
					 * ��ʾ�оܾ�������
					 */
					resultbuf = resultbuf.append(getErrInfo(DealCodeConstants.DB2_REJECT_LINE, strs[i]));
					break;
				}
				
				if(count >= MsgConstant.DB_IMPORT_ERROR_NUM){
					break;
				}
			}
		}
		
		if(resultbuf.length() > 1){
			// ��ʾִ�н�����оܾ���¼
			rsFlag = false;
			errbuf = errbuf.append("ִ�н������:\r\n\t" + resultbuf.toString() + "\r\n");
		}
		if(buf3125.length() > 1){
			// ��ʾ�нضϵ�����
			rsFlag = false;
			errbuf = errbuf.append("���¼�¼���ض�:\r\n\t" + buf3125 + "\r\n");
		}
		if(buf3124.length() > 1){
			// ��ʾ�н������ת������
			rsFlag = false;
			errbuf = errbuf.append("���¼�¼�������ת������:\r\n\t" + buf3124 + "\r\n");
		}
		if(buf3137.length() > 1){
			// ��ʾ�м�¼ȱ�������ֶ�
			rsFlag = false;
			errbuf = errbuf.append("���¼�¼ȱ�������ֶ�:\r\n\t" + buf3137 + "\r\n");
		}
		
		
		if(!rsFlag){
			throw new ITFEBizException(errbuf.toString());
		}
	}
	
	/**
	 * У�����SHELL��Ĵ�����(֧��ҵ��-ֱ��֧������Ȩ֧��)
	 * 
	 * @param String
	 *            results ����SHELL�ķ��ؽ��
	 */
	public static void verifyShellForPayoutRs(String results) throws ITFEBizException{
		// ָ����ʾ�����¼����  
		boolean rsFlag = true;
		boolean mainFlag = true;
		int count = 0;
		StringBuffer errbuf = new StringBuffer(); // �ܵĴ�������
		StringBuffer buf3125 = new StringBuffer(); // �ضϴ���
		StringBuffer buf3124 = new StringBuffer(); // �������ת������
		StringBuffer buf3137 = new StringBuffer(); // ������ȱ�ٴ���
		StringBuffer buf3116 = new StringBuffer(); // �����ֶβ���Ϊ��
		StringBuffer resultbuf = new StringBuffer(); // ִ�е���Ľ��(��ʱ�鿴�ܾ�������)
		
		String[] strs = results.split(StateConstant.SPACE_SPLIT);
		for(int i = 0 ; i < strs.length ; i++){
			if(null != strs[i]){
				if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3125W) >= 0){
					/**
					 * ��ʾ�����ݱ��ض�
					 * ����:SQL3125W  ���� "1" �� "3" �е��ַ����ݱ��ضϣ���Ϊ�����ݱ�Ŀ�����ݿ��и�����
					 */
					count ++;
					buf3125 = buf3125.append(getErrInfo(DealCodeConstants.DB2_SQL3125W, strs[i]));
				}else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3124W) >= 0){
					/**
					 * ��ʾ������ת���ɽ���ֶ�ʧ�� 
					 * ����:SQL3124W  ���� "1" �� "12" �е��ֶ�ֵ����ת��Ϊ PACKED DECIMAL
					 */
					count ++;
					buf3124 = buf3124.append(getErrInfo(DealCodeConstants.DB2_SQL3124W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_SQL3137W) >= 0){
					/**
					 * ��ʾ��������̫��,ȱ���ֶ�
					 * ����:SQL3137W  �� "1" ̫�̡����ٶ�ʧ��һ����װ��ǿɿ��е�����ֵ��δװ����С�
					 */
					count++;
					buf3137 = buf3137.append(getErrInfo(DealCodeConstants.DB2_SQL3137W, strs[i]));
				} else if(strs[i].toUpperCase().indexOf("SQL3116W") >= 0){
					/**
					 * ��ʾ�������ֶβ���Ϊ��
					 * ����:SQL3116W  ��ʧ���� "3" �� "8" �е��ֶ�ֵ������Ŀ���в���Ϊ�ա�
					 */
					count++;
					buf3116 = buf3116.append(getErrInfo("SQL3116W", strs[i]));
				} 
				
				else if(strs[i].toUpperCase().indexOf(DealCodeConstants.DB2_REJECT_LINE) >= 0
						|| (strs[i].toLowerCase().indexOf(DealCodeConstants.DB2_REJECT_LINE_ENGLISH) >= 0 &&
								strs[i].indexOf("=") >= 0)){
					/**
					 * ��ʾ�оܾ�������
					 */
					resultbuf = resultbuf.append(getErrInfo(DealCodeConstants.DB2_REJECT_LINE, strs[i]));
					if(mainFlag){
						// ���������������ļ�¼
						if(resultbuf.length() > 1){
							// ��ʾִ�н�����оܾ���¼
							rsFlag = false;
							errbuf = errbuf.append("����ִ�н������:\r\n\t" + resultbuf.toString() + "\r\n");
						}
						if(buf3125.length() > 1){
							// ��ʾ�нضϵ�����
							rsFlag = false;
							errbuf = errbuf.append("�������¼�¼���ض�:\r\n\t" + buf3125 + "\r\n");
						}
						if(buf3124.length() > 1){
							// ��ʾ�н������ת������
							rsFlag = false;
							errbuf = errbuf.append("�������¼�¼�������ת������:\r\n\t" + buf3124 + "\r\n");
						}
						if(buf3137.length() > 1){
							// ��ʾ�м�¼ȱ�������ֶ�
							rsFlag = false;
							errbuf = errbuf.append("�������¼�¼ȱ�������ֶ�:\r\n\t" + buf3137 + "\r\n");
						}
						if(buf3116.length() > 1){
							// ��ʾ�м�¼ȱ�������ֶ�
							rsFlag = false;
							errbuf = errbuf.append("�������¼�¼�����ֶβ���Ϊ��:\r\n\t" + buf3116 + "\r\n");
						}
						
						
						if(!rsFlag){
							errbuf = errbuf.append("\r\n\r\n");
						}
						
						mainFlag = false;
					}
				}
				
				if(count >= MsgConstant.DB_IMPORT_ERROR_NUM){
					break;
				}
			}
		}
		
		if(resultbuf.length() > 1){
			// ��ʾִ�н�����оܾ���¼
			rsFlag = false;
			errbuf = errbuf.append("�ӱ�ִ�н������:\r\n\t" + resultbuf.toString() + "\r\n");
		}
		if(buf3125.length() > 1){
			// ��ʾ�нضϵ�����
			rsFlag = false;
			errbuf = errbuf.append("�ӱ����¼�¼���ض�:\r\n\t" + buf3125 + "\r\n");
		}
		if(buf3124.length() > 1){
			// ��ʾ�н������ת������
			rsFlag = false;
			errbuf = errbuf.append("�ӱ����¼�¼�������ת������:\r\n\t" + buf3124 + "\r\n");
		}
		if(buf3137.length() > 1){
			// ��ʾ�м�¼ȱ�������ֶ�
			rsFlag = false;
			errbuf = errbuf.append("�ӱ����¼�¼ȱ�������ֶ�:\r\n\t" + buf3137 + "\r\n");
		}
		if(buf3116.length() > 1){
			// ��ʾ�м�¼ȱ�������ֶ�
			rsFlag = false;
			errbuf = errbuf.append("�ӱ����¼�¼�����ֶβ���Ϊ��:\r\n\t" + buf3116 + "\r\n");
		}
		
		if(!rsFlag){
			throw new ITFEBizException(errbuf.toString());
		}
	}
	
	
	
	/**
	 * ת��������Ϣ���û���ʾ
	 * 
	 * @param String
	 *            errFlag �����־
	 * @param String
	 *            errStr ������Ϣ
	 * @return
	 */
	public static String getErrInfo(String errType, String errStr) {
		String[] strs = errStr.split("\"");

		if (DealCodeConstants.DB2_SQL3125W.equals(errType)) {
			return "��\"" + strs[1] + "\"��\"" + strs[3] + "\"�С�";
		} else if (DealCodeConstants.DB2_SQL3124W.equals(errType)) {
			return "��\"" + strs[1] + "\"��\"" + strs[3] + "\"�С�";
		} else if (DealCodeConstants.DB2_SQL3137W.equals(errType)) {
			return "��\"" + strs[1] + "\"��";
		} else if ("SQL3116W".equals(errType)) {
			return "��\"" + strs[1] + "\"��\"" + strs[3] + "\"�С�";
		} else if (DealCodeConstants.DB2_REJECT_LINE.equals(errType) || DealCodeConstants.DB2_REJECT_LINE_ENGLISH.equals(errType.toLowerCase())) {
			strs = errStr.split("=");
			int rejectnum = Integer.valueOf(strs[1].trim());
			if(rejectnum != 0){
				return "�ܾ�\"" + rejectnum + "\"����¼!";
			}
		}

		return "";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String result = "connect to itfedbts user db2inst1 using         " + "\r"+ "\n"

		  + "Database Connection Information"  + "\r"+ "\n"

		 + "Database server        = DB2/LINUXX8664 9.5.5" + "\r"+ "\n"
		 + "SQL authorization ID   = DB2INST1" + "\r"+ "\n"
		 + "Local database alias   = ITFEDBTS" + "\r"+ "\n"


		 + "delete from TV_INFILE_TMP where S_ORGCODE = '010000000002' and S_FILENAME = '2010090810110.txt'" + "\r"+ "\n"
		 + "DB20000I  The SQL command completed successfully."

		 + "import from /itfe/root/temp/20120308/010000000002/2010090810110.txt of del modified by compound=100 insert into TV_INFILE_TMP" + "\r"+ "\n"
		 + "SQL3109N  The utility is beginning to load data from file " + "\r"+ "\n"
		 + "/itfe/root/temp/20120308/010000000002/2010090810110.txt." + "\r"+ "\n"

		 + "SQL3110N  The utility has completed processing.  \"1399\" rows were read from " + "\r"+ "\n"
		 + "the input file." + "\r"+ "\n"

		 + "SQL3221W  ...Begin COMMIT WORK. Input Record Count = \"1399\"." + "\r"+ "\n"

		 + "SQL3222W  ...COMMIT of any database changes was successful. "+ "\r"+ "\n"

		 + "SQL3149N  \"1399\" rows were processed from the input file.  \"1399\" rows were"  + "\r"+ "\n"
		 + "successfully inserted into the table.  \"0\" rows were rejected."+ "\r"+ "\n"


		 + "Number of rows read         = 1399"+ "\r"+ "\n"
		 + "Number of rows skipped      = 0"+ "\r"+ "\n"
		 + "Number of rows inserted     = 1399"+ "\r"+ "\n"
		 + "Number of rows updated      = 0"+ "\r"+ "\n"
		 + "Number of rows rejected     = 0"+ "\r"+ "\n"
		 + "Number of rows committed    = 1399"+ "\r"+ "\n";
		try {
			verifyShellForIncomeRs(result);
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
