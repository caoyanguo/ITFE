/**
 * @author 王俊杰
 * 说明:对国库数据加密、解密
 */

/**
 * update by zhangzhendong
 * 增加自带KEY加密解密文件方法:EncryptUseKey(),DecryptUseKey()
 * 列举了本类包含所有加密方式的Demo
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
	
	//二维数组,61*8;
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
	 * 密钥的长度
	 */
	private final static int KEY_LENGTH = ENCRYPT_KEY[0][0].length();
	
	//文件标志(windows)
	private final static byte[] WIN_FILE_SIGNAL = {13,10};	
	private final static byte[] WIN_STR_SIGNAL = {35,35};
	private final static int MAX_HEAD_LEN = 4;
	
	//分节符类型
	public final static int SIGNAL_TYPE_NULL = -1;
	public final static int SIGNAL_TYPE_STR = 0;
	public final static int SIGNAL_TYPE_FILE = 1;
	
	//加密和解密字符串时使用,如果字符串中不包含解密用的key,解密字符串时传入改值.
	public final static int KEY_NULL = -1;
	
	
	
	/*
	 * 说明: 在TCBS中主要使用了对文件中的内容进行加解密.
	 * 在TBS中,对字符串加密使用了两种方式,一种是使用回车换行(换行)做为分割密钥和字符串的标志,
	 * 一种是使用两个'#', 就是WIN_FILE_SIGNAL 和 WIN_STR_SIGNAL.
	 * 文件内容的加密使用WIN_STR_SIGNAL.
	 * 修改解密方法，文件加密有两种：一种首行为密钥索引，一种不是
	 */
	
	
	/**
	 * 对字符串加密
	 * @param srcStr
	 * @param sinalType 分节符类型 0:字符串类型 其它:文件类型
	 * @return
	 * @throws Exception
	 */
	public static String Encrypt(String srcStr, int sinalType) throws Exception{		
		if(srcStr == null || srcStr.trim().length() == 0){
			throw new Exception("被加密的字符串不能为空!");
		}
		
		//
		if(sinalType == SIGNAL_TYPE_STR){
			return encryptStr(srcStr, WIN_STR_SIGNAL);
		}else{
			return encryptStr(srcStr, WIN_FILE_SIGNAL);
		}		
	}
	
	/**
	 * 对字符串解密 (gkfxjmzjstr)
	 * @param srcStr	: 被加密后的字符串,可以带密钥也可以不带密钥. 不带密钥时需要在参数nKeyIndex传入密文使用的密钥;
	 * @param nKeyIndex : 参数srcStr中没有有密钥时,使用 nKeyIndex <= 60 并且 nKeyIndex >= 0;
	 * @param sinalType : 字符串中没有密钥时,该值无效;文件中使用回车换行(\r\n或\n)做为分割符.
	 * @return
	 * @throws Exception
	 */
	public static String Decrypt(String srcStr, int nKeyIndex, int sinalType) throws Exception {
		//
		byte keyIndex = 0;
		byte decrypt[] = null;
		
		//不在key范围内的字符串的key,认为字符串中包含key, 字符串中包含key
		if(nKeyIndex < 0 || nKeyIndex > 60){
			if(srcStr == null || srcStr.trim().length() == 0){
				throw new Exception("被解密的字符串不能为空!");
			}else if(srcStr.trim().length() <= MAX_HEAD_LEN){
				throw new Exception("被解密的字符串格式错误!");
			}
			
			//
			if(sinalType < SIGNAL_TYPE_STR){
				throw new Exception("被解密的字符串格式错误!");
			}
			
			//取key
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
				throw new Exception("字符串格式错误!");
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
	 * 对文件加密(gkjmzj)
	 * @param srcFile
	 * @param dstFile
	 * @throws Exception
	 */
	public static void Encrypt(String srcFile, String dstFile) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("被加密的文件不存在!");
		}		
		//空文件;
		if(inFile.length() == 0){
			throw new Exception("被加密的文件为空!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//使用缓存的方式输入
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//使用缓存的方式输出
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		//		
		byte nKeyIndex = genKeyIndex();
		
		//文件开始标志
		fout.write(String.valueOf(nKeyIndex).getBytes());
		fout.write(WIN_FILE_SIGNAL);		

		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//加密文件
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
	 * 对文件加密(gkjmzj)
	 * @param srcFile
	 * @param dstFile
	 * @param key
	 * @throws Exception
	 */
	public static void gkEncryptKey(String srcFile, String dstFile,String key) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("被加密的文件不存在!");
		}		
		//空文件;
		if(inFile.length() == 0){
			throw new Exception("被加密的文件为空!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//使用缓存的方式输入
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//使用缓存的方式输出
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		//		
		byte nKeyIndex = genKeyIndex();
		
		//文件开始标志
		fout.write(String.valueOf(nKeyIndex).getBytes());
		fout.write(WIN_FILE_SIGNAL);		

		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//加密文件
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
	 * 用自带KEY加密解密文件
	 * @param srcFile 源文件
	 * @param dstFile 目标文件
	 * @param key 密钥
	 * @throws Exception
	 */
	public static void EncryptUseKey(String srcFile, String dstFile,String key) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("被加密的文件不存在!");
		}		
		//空文件;
		if(inFile.length() == 0){
			throw new Exception("被加密的文件为空!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//使用缓存的方式输入
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//使用缓存的方式输出
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		//		
		byte nKeyIndex = genKeyIndex();
		
		//文件开始标志
//		fout.write(String.valueOf(nKeyIndex).getBytes());
//		fout.write(WIN_FILE_SIGNAL);		

		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//加密文件
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
	 * 用自带KEY解密文件
	 * @param srcFile 源文件
	 * @param dstFile 目标文件
	 * @param key 密钥
	 * @throws Exception
	 */
	public static void DecryptUseKey(String srcFile, String dstFile, String key) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("被解密的文件不存在!");
		}
		
		//空文件;
		if(inFile.length() == 0){
			throw new Exception("被解密的文件为空!");
		}
		
		File outFile = new File(dstFile);
		//判断文件能否打开
		if(!outFile.exists()){
			outFile.createNewFile();
		}

		//
		outFile.createNewFile();		
		//使用缓存的方式输入
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//使用缓存的方式输出
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		byte keyIndex = 0;
		//读文件的首行
//		if(!bKeyIsFirstLine){
//			//读文件的首行，直接写入文件；
//			String line = readLine(fin);
//			if(line == null || line.trim().length() == 0){
//				throw new Exception("文件格式错误!");
//			}
//			//
//			line += "\r\n";
//			fout.write(line.getBytes());
//		}
		
		//读密钥
//		keyIndex = (byte)readFileSignal(fin);
//		if(keyIndex > ENCRYPT_KEY.length || keyIndex < 0){
//			throw new Exception("文件格式错误!");
//		}
		
		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//解密文件
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
	 * 解密文件(gkfxjmzj)
	 * @param srcFile
	 * @param dstFile
	 * @bFisrtLineEncrypt 文件内容的首行是密钥吗？
	 * @throws Exception
	 */
	public static void Decrypt(String srcFile, String dstFile, boolean bKeyIsFirstLine) throws Exception{
		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("被解密的文件不存在!");
		}
		
		//空文件;
		if(inFile.length() == 0){
			throw new Exception("被解密的文件为空!");
		}
		
		File outFile = new File(dstFile);
		//判断文件能否打开
		if(!outFile.exists()){
			outFile.createNewFile();
		}

		//
		outFile.createNewFile();		
		//使用缓存的方式输入
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//使用缓存的方式输出
		DataOutputStream fout = new DataOutputStream(
								new BufferedOutputStream(
								new FileOutputStream(outFile)));
		
		
		byte keyIndex = 0;
		//读文件的首行
		if(!bKeyIsFirstLine){
			//读文件的首行，直接写入文件；
			String line = readLine(fin);
			if(line == null || line.trim().length() == 0){
				throw new Exception("文件格式错误!");
			}
			//
			line += "\r\n";
			fout.write(line.getBytes());
		}
		
		//读密钥
		keyIndex = (byte)readFileSignal(fin);
		if(keyIndex > ENCRYPT_KEY.length || keyIndex < 0){
			throw new Exception("文件格式错误!");
		}
		
		byte[] srcBuff = new byte[1024];
		int readLen = -1;
		//解密文件
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
		//判断文件能否打开		
		File inFile = new File(srcFile);
		if(!inFile.exists()){
			throw new Exception("被加密的文件不存在!");
		}
		//空文件;
		if(inFile.length() == 0){
			throw new Exception("被加密的文件为空!");
		}
		
		File outFile = new File(dstFile);
		//
		if(!outFile.exists()){
			outFile.createNewFile();
		}
		
		//使用缓存的方式输入
		DataInputStream fin = new DataInputStream(
							  new BufferedInputStream(
							  new FileInputStream(inFile)));
		
		
		//使用缓存的方式输出
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
	 * 原名:DLL_Encrypt
	 * @param inKey : 密钥
	 * @param inData: 被加密或解密的数据
	 * @param bFlag : true 使用 inKey 对 inData 加密
	 * 				  false 使用 inKey 对 inData 解密;
	 * @return
	 */
	
	public static String Encrypt(String inKey, String inData, boolean bFlag) throws Exception {
		
		if(inKey == null || inKey.trim().length() == 0 || 
			inData == null || inData.trim().length() == 0){			
			throw new Exception("密钥和数据不能为空!");
		}
		//
		byte[] keyBytes = inKey.getBytes();
		byte[] dataBytes = inData.getBytes();
		
		int keyLen = keyBytes.length;
		int dataLen = dataBytes.length;
		
		ByteBuffer buff = null;
		
		//计算key的和
		int keySum = 0;
		for(int i = 0; i < keyLen; i++){
			keySum += keyBytes[i];
		}
		
		//
		int keyValue = 0;
		//加密
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
			
			//解密
			String subData = "";
			//被加密后的数据是5的整数倍
			if(dataLen % 5!=0){
				throw new Exception("数据格式错误!");
			}
			//实际数据的长度
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
	 * 对字符串加密(gkjmzjstr)
	 * @param srcStr
	 * @param sinal : 分节符类型 字符类型和文件类型
	 * @return
	 * @throws Exception
	 */
	private static String encryptStr(String srcStr, byte sinal[]) throws Exception{		
		if(srcStr == null || srcStr.trim().length() == 0){
			throw new Exception("被加密的字符串不能为空!");
		}
		
		//取密钥
		byte keyIndex = genKeyIndex();
		
		//取字节流
		byte srcBytes[] = srcStr.getBytes();
		ByteBuffer buff = ByteBuffer.allocate(srcBytes.length + 2);
		
		//写密钥标志
		buff.put(sinal);
		
		//
		buff.put(encrypt(srcBytes, srcBytes.length, keyIndex));
		
		return Integer.toString(keyIndex)+ new String(buff.array());
	}
	
	/**
	 * 对字节数组加密
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyIndex
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] srcBytes, int nSrcBytes, byte keyIndex) throws Exception{
		ByteBuffer buff = ByteBuffer.allocate(nSrcBytes);		
		//加密
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
	 * 对字节数据解密
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyIndex
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] srcBytes, int nSrcBytes, byte keyIndex) throws Exception {
		//校验密钥索引
		if(keyIndex > 60) {
			throw new Exception("字符串格式错误!");
		}

		//解密
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
	 * 用自带KEY加密文件
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyBytes
	 * @return
	 * @throws Exception
	 */
	private static byte[] encryptUseKey(byte[] srcBytes, int nSrcBytes, byte[] keyBytes, int keyLength) throws Exception{
		ByteBuffer buff = ByteBuffer.allocate(nSrcBytes);		
		//加密
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
	 * 用自带KEY解密文件
	 * @param srcBytes
	 * @param nSrcBytes
	 * @param keyBytes
	 * @return
	 * @throws Exception
	 */
	private static byte[] decryptUseKey(byte[] srcBytes, int nSrcBytes, byte[] keyItems, int keyLength) throws Exception {

		//解密
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
	 * 使用 格林威治标准时间作为随机数的种子,生成密钥索引
	 * @return
	 */
	private static byte genKeyIndex(){
		//生成随机数
		Random rand = new Random(Calendar.getInstance().getTimeInMillis());
		long randValue = rand.nextInt();
		
		//取无符号数据;
		if(randValue < 0){
			randValue = (0xFFFFFFFFL + randValue) + 1;
		}
		
		//取模
		Long result = randValue % 60;
		
		//
		return result.byteValue();
	}
	
	/**
	 * 异或操作(不能对字节进行异或操作,所以添加该方法)
	 * @param src1
	 * @param src2
	 * @return
	 */
	private static byte xor(byte src1, byte src2){
		int result = Byte.valueOf(src1).intValue() ^ Byte.valueOf(src2).intValue();		
		return Integer.valueOf(result).byteValue(); 
	}
	
	/**
	 * 取文件头部的标志
	 * @param fin
	 * @return
	 * @throws IOException
	 * Windows中使用'\r\n'(0xD 0xA)回车换行, Linux 或 Unix 中使用'\n'(0xA)表示回车换行;
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
	 * 取文件头部的标志
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
	 * 取文件中密钥的索引
	 * @param fin
	 * @return
	 * @throws IOException
	 */
	private static String readKeyIndex(DataInputStream fin) throws IOException {
		char c = 0;
		String sIndex = "";
		int len = 0;

		//索引 ＋ 标记不超过 6 个字节
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
	 * 取文件中密钥的索引
	 * @param lines
	 * @return
	 * @throws IOException
	 */
	private static String readKeyIndex(String lines) throws IOException {
		char c = 0;
		String sIndex = "";
		int len = 0;

		//索引 ＋ 标记不超过 6 个字节, 由密钥数组大小决定
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
	 * 取文件中密钥的索引
	 * @param fin
	 * @return
	 * @throws IOException
	 */
	private static String readLine(DataInputStream fin) throws IOException {
		char c = 0;
		String sLine = "";
		int len = 0;

		try{		
			//索引 ＋ 标记不超过 6 个字节
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
	
	
	
	//用例大全
	public static void main(String[] args) throws Exception{
		//String encrypt test
		//内部生成KEY
		String str = "你好Hello1234567890ABcd!!!???";
//		String str = "你好,中国,我爱你!!";
		String miwen = TreasuryEncrypt.Encrypt(str, 0);
		System.out.println(miwen);
		String mingwen = TreasuryEncrypt.Decrypt(miwen, -2, 0);
		System.out.println(mingwen);
		
		
		//外部指定KEY
		String key = "@#%esde3ddd";
		String miwen1 = TreasuryEncrypt.Encrypt(key, str, true);
		String mingwen1 = TreasuryEncrypt.Encrypt(key, miwen1, false);
		System.out.println("my key-->"+miwen1);
		System.out.println(mingwen1);
		
		//File encrypt test
		//内部生成KEY
		String fileName = "c:\\Hello.txt";
		String newFile = "c:\\HelloDes.txt";
		String newFile1 = "c:\\HelloDes1.txt"; 
		TreasuryEncrypt.Encrypt(fileName, newFile);
		TreasuryEncrypt.Decrypt(newFile, newFile1, true);
		
		//外部指定KEY
		String testDes = "c:\\TestDes.txt";
		String testEnc = "c:\\TestEnc.txt";
		TreasuryEncrypt.EncryptUseKey(fileName, testDes, key);
		TreasuryEncrypt.DecryptUseKey(testDes, testEnc, key);
	}
	
	
}