/**
 * @author ������
 * ˵��:�Թ������ݼ��ܡ�����
 */

/**
 * update by zhangzhendong
 * �����Դ�KEY���ܽ����ļ�����:EncryptUseKey(),DecryptUseKey()
 * �о��˱���������м��ܷ�ʽ��Demo
 */
 
package com.cfcc.itfe.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Random;


public class TreasuryEncrypt {
	
	//��ά����,61*8;
	private final static String ENCRYPT_KEY [][]=  
		{{"?1i%d3Kx"},{"s8@8$!*i"},{"d7J:SFw4"},{"l6piz3Gn"},
		 {"Rs5t|o&#"},{"4s*m-y4;"},{"3o34%x:W"},{"a2e+FDKQ"},
		 {"s{OCS$1?"},{"sAUI<>2a"},{"2_gf<3vQ"},{"b8&o<fH]"},
		 {"7ds?.asd"},{"sd6sft4V"},{"ds5QTP{q"},{"n4^/?Sys"},
		 {"sfdteib8"},{"b2s?>LK}"},{"1/dti[qr"},{"a!bfo[[e"},
		 {"U3v$@h89"},{"8n$Lfv?2"},{"b7bwetjw"},{"bc6ertlx"},
		 {"h4532:KE"},{"k46SFTTR"},{"3XdrFGty"},{"2nVI%#dM"},
		 {"1af;mes,"},{"cb$yu.sx"},{"G4s?2+-s"},{"v83+V#OU"},
		 {"b7|ET&Nd"},{"n6b(G$><"},{"n5fsre65"},{"4lQfaftk"},
		 {"SDF{}3rd"},{"fert2<db"},{"s246<M19"},{"%dfgd45d"},
		 {"d5gsb2x!"},{"s8f:oewZ"},{"7d~twpot"},{"y6dsf34s"},
		 {"k59{_JH:"},{"4hvet*&e"},{"3m;SFsft"},{"sd2JLYI^"},
		 {"bf*F&^Cn"},{"h^sft.e4"},{"b6[do94)"},{"8gq=0&Fk"},
		 {"b`ewU<X|"},{"zb6rtx#@"},{"5oj79h:h"},{"sf7lakv%"},
		 {"SFETE3xd"},{"bvler-2s"},{"1d)(*V>d"},{"&ftwetrr"},
		 {"00000000"}};
	

	/*
	 * ��Կ�ĳ���
	 */
	private final static int KEY_LENGTH = ENCRYPT_KEY[0][0].length();
	
	//�ļ���־(windows)
	private final static byte[] WIN_FILE_SIGNAL = {13,10};	
	private final static byte[] WIN_STR_SIGNAL = {35,35};
	private final static int MAX_HEAD_LEN = 4;
	
	//�ֽڷ�����
	public final static int SIGNAL_TYPE_NULL = -1;
	public final static int SIGNAL_TYPE_STR = 0;
	public final static int SIGNAL_TYPE_FILE = 1;
	
	//���ܺͽ����ַ���ʱʹ��,����ַ����в����������õ�key,�����ַ���ʱ�����ֵ.
	public final static int KEY_NULL = -1;
	
	
	
	/*
	 * ˵��: ��TCBS����Ҫʹ���˶��ļ��е����ݽ��мӽ���.
	 * ��TBS��,���ַ�������ʹ�������ַ�ʽ,һ����ʹ�ûس�����(����)��Ϊ�ָ���Կ���ַ����ı�־,
	 * һ����ʹ������'#', ����WIN_FILE_SIGNAL �� WIN_STR_SIGNAL.
	 * �ļ����ݵļ���ʹ��WIN_STR_SIGNAL.
	 * �޸Ľ��ܷ������ļ����������֣�һ������Ϊ��Կ������һ�ֲ���
	 */
	
	
	/**
	 * ���ַ�������
	 * @param srcStr
	 * @param sinalType �ֽڷ����� 0:�ַ������� ����:�ļ�����
	 * @return
	 * @throws Exception
	 */
	public static String Encrypt(String srcStr, int sinalType) throws Exception{		
		if(srcStr == null || srcStr.trim().length() == 0){
			throw new Exception("�����ܵ��ַ�������Ϊ��!");
		}
		
		//
		if(sinalType == SIGNAL_TYPE_STR){
			return encryptStr(srcStr, WIN_STR_SIGNAL);
		}else{
			return encryptStr(srcStr, WIN_FILE_SIGNAL);
		}		
	}
	
	/**
	 * ���ַ������� (gkfxjmzjstr)
	 * @param srcStr	: �����ܺ���ַ���,���Դ���ԿҲ���Բ�����Կ. ������Կʱ��Ҫ�ڲ���nKeyIndex��������ʹ�õ���Կ;
	 * @param nKeyIndex : ����srcStr��û������Կʱ,ʹ�� nKeyIndex <= 60 ���� nKeyIndex >= 0;
	 * @param sinalType : �ַ�����û����Կʱ,��ֵ��Ч;�ļ���ʹ�ûس�����(\r\n��\n)��Ϊ�ָ��.
	 * @return
	 * @throws Exception
	 */
	public static String Decrypt(String srcStr, int nKeyIndex, int sinalType) throws Exception {
		//
		byte keyIndex = 0;
		byte decrypt[] = null;
		
		//����key��Χ�ڵ��ַ�����key,��Ϊ�ַ����а���key, �ַ����а���key
		if(nKeyIndex < 0 || nKeyIndex > 60){
			if(srcStr == null || srcStr.trim().length() == 0){
				throw new Exception("�����ܵ��ַ�������Ϊ��!");
			}else if(srcStr.trim().length() <= MAX_HEAD_LEN){
				throw new Exception("�����ܵ��ַ�����ʽ����!");
			}
			
			//
			if(sinalType < SIGNAL_TYPE_STR){
				throw new Exception("�����ܵ��ַ�����ʽ����!");
			}
			
			//ȡkey
			int idx = 0;
			if(sinalType == SIGNAL_TYPE_STR){
				idx = srcStr.indexOf(new String(WIN_STR_SIGNAL));
				keyIndex = (byte)Integer.parseInt(srcStr.substring(0, idx));
			}else{
				idx = srcStr.indexOf(new String(WIN_FILE_SIGNAL));
				keyIndex = (byte)readStrSignal(srcStr);				
			}
			
			//
			if(keyIndex > 60) {
				throw new Exception("�ַ�����ʽ����!");
			}		
			if(idx < 0) return null;
			
			decrypt = srcStr.substring(idx + 2, srcStr.length()).getBytes();
		}else{			
			keyIndex = (byte)nKeyIndex;
			decrypt = srcStr.getBytes();
		}
		
		return new String(decrypt(decrypt, decrypt.length, keyIndex));
	}
	
	
	/**
	 * ���ļ�����(gkjmzj)
	 * @param srcFile
	 * @param dstFile
	 * @throws Exception
	 */
	public static void Encrypt(String srcFile, String dstFile) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("�����ܵ��ļ�������!");
		}		
		//���ļ�;
		if(inFile.length() == 0){
			throw new Exception("�����ܵ��ļ�Ϊ��!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//ʹ�û���ķ�ʽ����
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//ʹ�û���ķ�ʽ���
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		//		
		byte nKeyIndex = genKeyIndex();
		
		//�ļ���ʼ��־
		fout.write(String.valueOf(nKeyIndex).getBytes());
		fout.write(WIN_FILE_SIGNAL);		

		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//�����ļ�
		try {
			while(true){
				
				readLen = fin.read(srcBuff);
				if(readLen < 0){
					break;
				}
				
				fout.write(encrypt(srcBuff, readLen, nKeyIndex));
			}
		}catch(EOFException e){
			throw new Exception(e.getMessage());
		}finally{
			fout.flush();
			fin.close();
			fout.close();
		}
	}
	
	
	/**
	 * ���ļ�����(gkjmzj)
	 * @param srcFile
	 * @param dstFile
	 * @param key
	 * @throws Exception
	 */
	public static void gkEncryptKey(String srcFile, String dstFile,String key) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("�����ܵ��ļ�������!");
		}		
		//���ļ�;
		if(inFile.length() == 0){
			throw new Exception("�����ܵ��ļ�Ϊ��!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//ʹ�û���ķ�ʽ����
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//ʹ�û���ķ�ʽ���
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		//		
		byte nKeyIndex = genKeyIndex();
		
		//�ļ���ʼ��־
		fout.write(String.valueOf(nKeyIndex).getBytes());
		fout.write(WIN_FILE_SIGNAL);		

		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//�����ļ�
		try {
			while(true){
				
				readLen = fin.read(srcBuff);
				if(readLen < 0){
					break;
				}
				
				fout.write(encrypt(srcBuff, readLen, nKeyIndex));
			}
		}catch(EOFException e){
			throw new Exception(e.getMessage());
		}finally{
			fout.flush();
			fin.close();
			fout.close();
		}
	}
	
	/**
	 * ���Դ�KEY���ܽ����ļ�
	 * @param srcFile Դ�ļ�
	 * @param dstFile Ŀ���ļ�
	 * @param key ��Կ
	 * @throws Exception
	 */
	public static void EncryptUseKey(String srcFile, String dstFile,String key) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("�����ܵ��ļ�������!");
		}		
		//���ļ�;
		if(inFile.length() == 0){
			throw new Exception("�����ܵ��ļ�Ϊ��!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//ʹ�û���ķ�ʽ����
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//ʹ�û���ķ�ʽ���
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		//		
		byte nKeyIndex = genKeyIndex();
		
		//�ļ���ʼ��־
//		fout.write(String.valueOf(nKeyIndex).getBytes());
//		fout.write(WIN_FILE_SIGNAL);		

		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//�����ļ�
		try {
			while(true){
				
				readLen = fin.read(srcBuff);
				if(readLen < 0){
					break;
				}
				
				fout.write(encryptUseKey(srcBuff, readLen, key.getBytes(),key.length()));
			}
		}catch(EOFException e){
			throw new Exception(e.getMessage());
		}finally{
			fout.flush();
			fin.close();
			fout.close();
		}
	}
	
	
	
	
	/**
	 * ���Դ�KEY�����ļ�
	 * @param srcFile Դ�ļ�
	 * @param dstFile Ŀ���ļ�
	 * @param key ��Կ
	 * @throws Exception
	 */
	public static void DecryptUseKey(String srcFile, String dstFile, String key) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("�����ܵ��ļ�������!");
		}
		
		//���ļ�;
		if(inFile.length() == 0){
			throw new Exception("�����ܵ��ļ�Ϊ��!");
		}
		
		File outFile = new File(dstFile);
		//�ж��ļ��ܷ��
		if(!outFile.exists()){
			outFile.createNewFile();
		}

		//
		outFile.createNewFile();		
		//ʹ�û���ķ�ʽ����
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//ʹ�û���ķ�ʽ���
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		byte keyIndex = 0;
		//���ļ�������
//		if(!bKeyIsFirstLine){
//			//���ļ������У�ֱ��д���ļ���
//			String line = readLine(fin);
//			if(line == null || line.trim().length() == 0){
//				throw new Exception("�ļ���ʽ����!");
//			}
//			//
//			line += "\r\n";
//			fout.write(line.getBytes());
//		}
		
		//����Կ
//		keyIndex = (byte)readFileSignal(fin);
//		if(keyIndex > ENCRYPT_KEY.length || keyIndex < 0){
//			throw new Exception("�ļ���ʽ����!");
//		}
		
		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//�����ļ�
		try{
			while(true){				
				readLen = fin.read(srcBuff);
				if(readLen < 0){
					break;
				}
				
				fout.write(decryptUseKey(srcBuff, readLen, key.getBytes(),key.length()));				
			}
		}catch(EOFException e){
			throw new Exception(e.getMessage());
		}finally{
			fin.close();
			fout.flush();
			fout.close();
		}
	}
	
	
	
	
	
	
	/**
	 * �����ļ�(gkfxjmzj)
	 * @param srcFile
	 * @param dstFile
	 * @bFisrtLineEncrypt �ļ����ݵ���������Կ��
	 * @throws Exception
	 */
	public static void Decrypt(String srcFile, String dstFile, boolean bKeyIsFirstLine) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("�����ܵ��ļ�������!");
		}
		
		//���ļ�;
		if(inFile.length() == 0){
			throw new Exception("�����ܵ��ļ�Ϊ��!");
		}
		
		File outFile = new File(dstFile);
		//�ж��ļ��ܷ��
		if(!outFile.exists()){
			outFile.createNewFile();
		}

		//
		outFile.createNewFile();		
		//ʹ�û���ķ�ʽ����
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//ʹ�û���ķ�ʽ���
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		byte keyIndex = 0;
		//���ļ�������
		if(!bKeyIsFirstLine){
			//���ļ������У�ֱ��д���ļ���
			String line = readLine(fin);
			if(line == null || line.trim().length() == 0){
				throw new Exception("�ļ���ʽ����!");
			}
			//
			line += "\r\n";
			fout.write(line.getBytes());
		}
		
		//����Կ
		keyIndex = (byte)readFileSignal(fin);
		if(keyIndex > ENCRYPT_KEY.length || keyIndex < 0){
			throw new Exception("�ļ���ʽ����!");
		}
		
		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//�����ļ�
		try{
			while(true){				
				readLen = fin.read(srcBuff);
				if(readLen < 0){
					break;
				}
				
				fout.write(decrypt(srcBuff, readLen, keyIndex));				
			}
		}catch(EOFException e){
			throw new Exception(e.getMessage());
		}finally{
			fin.close();
			fout.flush();
			fout.close();
		}
	}
	

	/**
	 * @deprecated
	 * gkjmzjfl
	 * @param inFileName
	 * @param outFileName
	 * @param key
	 * @return
	 */
	public static void EncryptUnix(String srcFile, String dstFile) throws Exception{
		//�ж��ļ��ܷ��		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("�����ܵ��ļ�������!");
		}
		//���ļ�;
		if(inFile.length() == 0){
			throw new Exception("�����ܵ��ļ�Ϊ��!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//ʹ�û���ķ�ʽ����
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//ʹ�û���ķ�ʽ���
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		//
		byte keyIndex = genKeyIndex();

		byte firstLine = 0;
		byte srcByte;
		byte dstByte;
		//
		byte keyBytes[] = ENCRYPT_KEY[keyIndex][0].getBytes(); 
		int keyBytesIdx = 0;
		
		try{
			while(true){
				//
				srcByte = fin.readByte();
				
				if(firstLine == 2){
					dstByte = xor(srcByte, keyBytes[keyBytesIdx++ % KEY_LENGTH]);	
					//
					if(keyBytesIdx == KEY_LENGTH){
						keyBytesIdx = 0;
					}
				}else{
					dstByte = srcByte;
				}
				
				fout.write(dstByte);
				
				//
				if(dstByte == 13 && firstLine == 0){
					firstLine = 1;
				}
				
				if(dstByte == 10 && firstLine == 1){
					fout.write(keyIndex);
					firstLine = 2;
				}
			}
		}catch(EOFException e){
			
		}
		
		fin.close();
		fout.close();
	}
	
	/**
	 * @deprecated
	 * ԭ��:DLL_Encrypt
	 * @param inKey : ��Կ
	 * @param inData: �����ܻ���ܵ�����
	 * @param bFlag : true ʹ�� inKey �� inData ����
	 * 				  false ʹ�� inKey �� inData ����;
	 * @return
	 */
	
	public static String Encrypt(String inKey, String inData, boolean bFlag) throws Exception {
		
		if(inKey == null || inKey.trim().length() == 0 || 
			inData == null || inData.trim().length() == 0){			
			throw new Exception("��Կ�����ݲ���Ϊ��!");
		}
		//
		byte[] keyBytes = inKey.getBytes();
		byte[] dataBytes = inData.getBytes();
		
		int keyLen = keyBytes.length;
		int dataLen = dataBytes.length;
		
		ByteBuffer buff = null;
		
		//����key�ĺ�
		int keySum = 0;
		for(int i = 0; i < keyLen; i++){
			keySum += keyBytes[i];
		}
		
		//
		int keyValue = 0;
		//����
		if(bFlag){
			//
			buff = ByteBuffer.allocate(dataLen * 5);
			//
			for(int i = 0; i < dataLen; i++){
				keyValue = keySum;
				//
				keyValue += dataBytes[i];		
				//
				keyValue ^= keyBytes[0];
				//
				buff.put(String.format("%05d", keyValue).getBytes());
			}
		} else {
			
			//����
			String subData = "";
			//�����ܺ��������5��������
			if(dataLen % 5!=0){
				throw new Exception("���ݸ�ʽ����!");
			}
			//ʵ�����ݵĳ���
			dataLen = dataLen / 5;
			buff = ByteBuffer.allocate(dataLen);
			
			//
			for(int i = 0; i < dataLen; i++)
			{
				subData = inData.substring(i * 5, (i + 1) * 5);
				//
				keyValue = Integer.valueOf(subData).intValue();
				//
				keyValue ^= keyBytes[0];
				//
				keyValue -= keySum;
				//				
				buff.put((byte)keyValue);
			}
		}
		
		return new String(buff.array());
	}
	
	
	/**
	 * ���ַ�������(gkjmzjstr)
	 * @param srcStr
	 * @param sinal : �ֽڷ����� �ַ����ͺ��ļ�����
	 * @return
	 * @throws Exception
	 */
	private static String encryptStr(String srcStr, byte sinal[]) throws Exception{		
		if(srcStr == null || srcStr.trim().length() == 0){
			throw new Exception("�����ܵ��ַ�������Ϊ��!");
		}
		
		//ȡ��Կ
		byte keyIndex = genKeyIndex();
		
		//ȡ�ֽ���
		byte srcBytes[] = srcStr.getBytes();
		ByteBuffer buff = ByteBuffer.allocate(srcBytes.length + 2);
		
		//д��Կ��־
		buff.put(sinal);
		
		//
		buff.put(encrypt(srcBytes, srcBytes.length, keyIndex));
		
		return Integer.toString(keyIndex)+ new String(buff.array());
	}
	
	/**
	 * ���ֽ��������
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyIndex
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] srcBytes, int nSrcBytes, byte keyIndex) throws Exception{
		ByteBuffer buff = ByteBuffer.allocate(nSrcBytes);		
		//����
		byte keyBytes[] = ENCRYPT_KEY[keyIndex][0].getBytes();
		int  keyByteIdx = 0;
		for(int i = 0; i< nSrcBytes; i++) {
			buff.put(xor(srcBytes[i], keyBytes[keyByteIdx++]));
			//
			if(keyByteIdx == KEY_LENGTH){
				keyByteIdx  = 0;
			}
		}
		
		return buff.array();
	}
	
	/**
	 * ���ֽ����ݽ���
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyIndex
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] srcBytes, int nSrcBytes, byte keyIndex) throws Exception {
		//У����Կ����
		if(keyIndex > 60) {
			throw new Exception("�ַ�����ʽ����!");
		}

		//����
		ByteBuffer outBuff = ByteBuffer.allocate(nSrcBytes);
		byte keyItems[] = ENCRYPT_KEY[keyIndex][0].getBytes();
		int keyItemIdx = 0;		
		for(int i = 0; i < nSrcBytes; i++)
		{
			outBuff.put(xor(srcBytes[i], keyItems[keyItemIdx++]));
			
			if(keyItemIdx == KEY_LENGTH){
				keyItemIdx  = 0;
			}
		}

		return outBuff.array();
	}
	
	/**
	 * ���Դ�KEY�����ļ�
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyBytes
	 * @return
	 * @throws Exception
	 */
	private static byte[] encryptUseKey(byte[] srcBytes, int nSrcBytes, byte[] keyBytes, int keyLength) throws Exception{
		ByteBuffer buff = ByteBuffer.allocate(nSrcBytes);		
		//����
		int  keyByteIdx = 0;
		for(int i = 0; i< nSrcBytes; i++) {
			buff.put(xor(srcBytes[i], keyBytes[keyByteIdx++]));
			//
			if(keyByteIdx == keyLength){
				keyByteIdx  = 0;
			}
		}
		
		return buff.array();
	}
	
	
	/**
	 * ���Դ�KEY�����ļ�
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyBytes
	 * @return
	 * @throws Exception
	 */
	private static byte[] decryptUseKey(byte[] srcBytes, int nSrcBytes, byte[] keyItems, int keyLength) throws Exception {

		//����
		ByteBuffer outBuff = ByteBuffer.allocate(nSrcBytes);
		int keyItemIdx = 0;		
		for(int i = 0; i < nSrcBytes; i++)
		{
			outBuff.put(xor(srcBytes[i], keyItems[keyItemIdx++]));
			
			if(keyItemIdx == keyLength){
				keyItemIdx  = 0;
			}
		}

		return outBuff.array();
	}
	
	
	
	
	
	
	
	/**
	 * ʹ�� �������α�׼ʱ����Ϊ�����������,������Կ����
	 * @return
	 */
	private static byte genKeyIndex(){
		//���������
		Random rand = new Random(Calendar.getInstance().getTimeInMillis());
		long randValue = rand.nextInt();
		
		//ȡ�޷�������;
		if(randValue < 0){
			randValue = (0xFFFFFFFFL + randValue) + 1;
		}
		
		//ȡģ
		Long result = randValue % 60;
		
		//
		return result.byteValue();
	}
	
	/**
	 * ������(���ܶ��ֽڽ���������,������Ӹ÷���)
	 * @param src1
	 * @param src2
	 * @return
	 */
	private static byte xor(byte src1, byte src2){
		int result = Byte.valueOf(src1).intValue() ^ Byte.valueOf(src2).intValue();		
		return Integer.valueOf(result).byteValue(); 
	}
	
	/**
	 * ȡ�ļ�ͷ���ı�־
	 * @param fin
	 * @return
	 * @throws IOException
	 * Windows��ʹ��'\r\n'(0xD 0xA)�س�����, Linux �� Unix ��ʹ��'\n'(0xA)��ʾ�س�����;
	 */
	private static int readFileSignal(DataInputStream fin) throws IOException{

		String sIndex = readKeyIndex(fin);
		if(sIndex != null){
			return Integer.parseInt(sIndex);
		}else{
			return -1;
		}
	}
	
	/**
	 * ȡ�ļ�ͷ���ı�־
	 * @param fin
	 * @return
	 * @throws IOException
	 */
	private static int readStrSignal(String fin) throws IOException{

		String sIndex = readKeyIndex(fin);
		if(sIndex != null){
			return Integer.parseInt(sIndex);
		}else{
			return -1;
		}
	}
	
	/**
	 * ȡ�ļ�����Կ������
	 * @param fin
	 * @return
	 * @throws IOException
	 */
	private static String readKeyIndex(DataInputStream fin) throws IOException {
		char c = 0;
		String sIndex = "";
		int len = 0;

		//���� �� ��ǲ����� 6 ���ֽ�
		while(len < 6){				
			c = (char)fin.readByte();
			len++;
			
			if(c == '\n' || c == '\r'){
				if(c == '\r'){
					c = (char)fin.readByte();
					len++;
					if(c == '\n'){
						return sIndex;
					}else{
						sIndex += c;
					}
				}else{
					return sIndex;
				}
			}else{
				sIndex += c;
			}				
		}
		
		return null;
	}
	
	/**
	 * ȡ�ļ�����Կ������
	 * @param lines
	 * @return
	 * @throws IOException
	 */
	private static String readKeyIndex(String lines) throws IOException {
		char c = 0;
		String sIndex = "";
		int len = 0;

		//���� �� ��ǲ����� 6 ���ֽ�, ����Կ�����С����
		while(len < 6){				
			c = lines.charAt(len++);
			
			if(c == '\n' || c == '\r'){
				if(c == '\r'){
					c = lines.charAt(len++);
					if(c == '\n'){
						return sIndex;
					}else{
						sIndex += c;
					}
				}else{
					return sIndex;
				}
			}else{
				sIndex += c;
			}				
		}
		
		return null;
	}
	
	
	/**
	 * ȡ�ļ�����Կ������
	 * @param fin
	 * @return
	 * @throws IOException
	 */
	private static String readLine(DataInputStream fin) throws IOException {
		char c = 0;
		String sLine = "";
		int len = 0;

		try{		
			//���� �� ��ǲ����� 6 ���ֽ�
			while(true){
				c = (char)fin.readByte();
				len++;
				
				if(c == '\n' || c == '\r'){
					if(c == '\r'){
						c = (char)fin.readByte();
						len++;
						if(c == '\n'){
							return sLine;
						}else{
							sLine += c;
						}
					}else{
						return sLine;
					}
				}else{
					sLine += c;
				}				
			}
		}catch(EOFException e){
			return sLine;
		}
	}
	
	
	
	//������ȫ
	public static void main(String[] args) throws Exception{
		//String encrypt test
		//�ڲ�����KEY
		String str = "���Hello1234567890ABcd!!!???";
//		String str = "���,�й�,�Ұ���!!";
		String miwen = TreasuryEncrypt.Encrypt(str, 0);
		System.out.println(miwen);
		String mingwen = TreasuryEncrypt.Decrypt(miwen, -2, 0);
		System.out.println(mingwen);
		
		
		//�ⲿָ��KEY
		String key = "@#%esde3ddd";
		String miwen1 = TreasuryEncrypt.Encrypt(key, str, true);
		String mingwen1 = TreasuryEncrypt.Encrypt(key, miwen1, false);
		System.out.println("my key-->"+miwen1);
		System.out.println(mingwen1);
		
		//File encrypt test
		//�ڲ�����KEY
		String fileName = "c:\\Hello.txt";
		String newFile = "c:\\HelloDes.txt";
		String newFile1 = "c:\\HelloDes1.txt"; 
		TreasuryEncrypt.Encrypt(fileName, newFile);
		TreasuryEncrypt.Decrypt(newFile, newFile1, true);
		
		//�ⲿָ��KEY
		String testDes = "c:\\TestDes.txt";
		String testEnc = "c:\\TestEnc.txt";
		TreasuryEncrypt.EncryptUseKey(fileName, testDes, key);
		TreasuryEncrypt.DecryptUseKey(testDes, testEnc, key);
	}
	
	
}