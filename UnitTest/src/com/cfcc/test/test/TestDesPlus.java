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
		String test = "   ��ע�����������纽����ʷ��һĻ��Ҳע���ǽ��������뷭�ĵ�һ����\n"
				+ "��Ϊ������һ����ʮ�����˿��������Ա���������漣����˹�������\n"
				+ "��Ĵ�������������˲������������ı�������A-320������Ƶ��ռ�ݸ�\n"
				+ "��ý���ͷ��������������Ѿ����ɻ�Ū�����羪��֮��Ĺ��ȣ������\n"
				+ "Ϊ����Ӣ�ۣ�Ҳ����Ϊ����û��һǵҲ�ó���һ�ǣ�������ѷ���ϵ��漣��\n"
				+ "�ĵ���������Ϲ�����������ײ����ȫ�������ѭ�ģ�һ������û�д󺰡�\n"
				+ "�û������ߡ��ĳ�����Ա������Դ�ڷ���Я�׾�Ȼ����Ļ����ÿ͡�����\n"
				+ "�Ͼ���ḻ��Σ����ķɻ���ʻ�����һ���������";
		
		test = FileUtil.getInstance().readFile("D:\\����ǰ�������ļ�\\����\\��������\\payout\\2009111611170.xml");

		// DESPlus des = new DESPlus();//Ĭ����Կ
		DESPlus des;
		try {
			des = new DESPlus();
			// �Զ�����Կ
			System.out.println("������ǰ���ַ�����\n" + test);
			System.out.println("�����ܺ���ַ�����\n" + des.encrypt(test));
			System.out.println("�����ܺ���ַ�����\n" + des.decrypt(des.encrypt(test)));
			
			FileUtil.getInstance().writeFile("D:\\����ǰ�������ļ�\\����\\��������\\payout\\2009111611170_bak.xml", des.encrypt(test));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
