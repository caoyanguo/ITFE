/*
 * �������� 2005-7-13
 *
 * �����������ļ�ģ��Ϊ
 * ���� > ��ѡ�� > Java > �������� > �����ע��
 */
package com.cfcc.ormap;

import java.io.File;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cfcc.jaf.persistence.jaform.MapGenerator;
import com.cfcc.jaf.persistence.jaform.UnifiedMapGenerator;

/**
 * @author lhuifei
 * 
 */
public class TASOrmapGen {
	/**
	 * commons Logger for this class
	 */

	public static void main(String[] args) {
		deleteFiles("c:/CodeGen");
		genDto();

	}

	private static void genDto() {
		try {
			long lBegin = System.currentTimeMillis();
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					"com/cfcc/ormap/Generator.xml");

			// ClassPathXmlApplicationContext context = new
			// ClassPathXmlApplicationContext(
			// "com/cfcc/tips/persistency/ormap/GeneratorQdb.xml");

			MapGenerator generator = (MapGenerator) context
					.getBean("mapGenerator.ORM.GEN.ID");
			System.out.println("Dto�������У����Եȡ�����������");
			generator.init();
			generator.generate();
			long lEnd = System.currentTimeMillis();

			System.out.println("Process is OK,The Whole times="
					+ (lEnd - lBegin));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteFiles(String filePath) {
		File tmp1 = new File(filePath);
		// ���ݾ���·������ɾ������
		File file = new File(tmp1.getAbsolutePath());
		if (file.isFile()) {
			file.delete();
		} else {
			File[] f = file.listFiles();
			if (f != null && f.length > 0) {
				for (int i = 0; i < f.length; i++) {
					File tmp = f[i];
					if (tmp.isDirectory()) {
						deleteFiles(tmp.getAbsolutePath());

					}
					tmp.delete();
				}

			}

		}

	}
}
