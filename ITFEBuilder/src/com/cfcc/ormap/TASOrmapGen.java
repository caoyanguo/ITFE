/*
 * 创建日期 2005-7-13
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
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
			System.out.println("Dto在生成中，请稍等。。。。。。");
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
		// 根据绝对路径进行删除操作
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
