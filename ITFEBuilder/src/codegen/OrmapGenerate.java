package codegen;

import java.io.File;
import org.apache.log4j.BasicConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.cfcc.jaf.persistence.jaform.MapGenerator;

/**
 * @author caoyg
 * @time   09-10-14 14:24:03
 *
 */
public class OrmapGenerate {
	/**
	 * commons Logger for this class
	 */

	public static void main(String[] args) {	
		genDto();

	}

	private static void genDto() {
		BasicConfigurator.configure();
		try {
			long lBegin = System.currentTimeMillis();
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					"codegen/Generator.xml");
					
			MapGenerator generator = (MapGenerator) context
					.getBean("mapGenerator.ORM.GEN.ID");
			System.out.println("Dto在生成中，请稍等......");
			generator.init();
//			context.get
			generator.generate();
			
			long lEnd = System.currentTimeMillis();

			System.out.println("Process is OK,The Whole time="
					+ (lEnd - lBegin)/1000 + "s");
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
