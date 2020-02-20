package com.cfcc.test.test;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.security.DESPlus;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;

public class TestDesPlus {
	
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	/**
	 * @param args
	 * @throws FileOperateException 
	 */
	public static void main(String[] args) throws FileOperateException {

		// String test = "username";
		String test = "   这注定是永载世界航空历史的一幕，也注定是将被好莱坞翻拍的一部。\n"
				+ "因为创造了一百五十多名乘客与机组人员无伤亡的奇迹，杰斯里·苏兰伯\n"
				+ "格的大名现如今是无人不晓，他与他的宝贵座驾A-320近日来频繁占据各\n"
				+ "大媒体的头条，在美国这个已经被飞机弄得有如惊弓之鸟的国度，将其称\n"
				+ "为民族英雄，也并不为过。没吃一堑也得长上一智，“哈德逊河上的奇迹”\n"
				+ "的诞生并非是瞎子摸象误打误撞，完全是有理可循的，一得益于没有大喊“\n"
				+ "让机长先走”的乘务人员、二来源于扶老携幼井然有序的机载旅客、最后加\n"
				+ "上经验丰富临危不惧的飞机驾驶，外带一点点运气。";
		
		test = FileUtil.getInstance().readFile("D:\\国库前置资料文件\\杭州\\测试数据\\payout\\2009111611170.xml");

		// DESPlus des = new DESPlus();//默认密钥
		DESPlus des;
		try {
			des = new DESPlus();
			// 自定义密钥
			System.out.println("【加密前的字符：】\n" + test);
			System.out.println("【加密后的字符：】\n" + des.encrypt(test));
			System.out.println("【解密后的字符：】\n" + des.decrypt(des.encrypt(test)));
			
			FileUtil.getInstance().writeFile("D:\\国库前置资料文件\\杭州\\测试数据\\payout\\2009111611170_bak.xml", des.encrypt(test));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
