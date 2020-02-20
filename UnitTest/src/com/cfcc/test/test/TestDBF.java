package com.cfcc.test.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;

public class TestDBF {

	public static void main(String args[]) {

		String filepath = "D:\\国库前置资料文件\\杭州\\测试数据\\一户通扣款数据0315\\国税一户通扣款数据0315\\03160601.dbf";
		try {
			// create a DBFReader object
			InputStream inputStream = new FileInputStream(filepath); 
			
			// take dbf file as program argument
			DBFReader reader = new DBFReader(inputStream);
			reader.setCharactersetName("GBK");

			// get the field count if you want for some reasons like the following
			int numberOfFields = reader.getFieldCount();
			int numberOfRecords = reader.getRecordCount();

			System.out.println(numberOfFields + "-" + numberOfRecords);

			// Now, lets us start reading the rows
			Object[] rowObjects;
			int j = 0 ;	
			while ((rowObjects = reader.nextRecord()) != null) {
				System.out.println();
				j = 0 ;
				System.out.print(change(rowObjects[j++])); //税票号码
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //隶属关系名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //登记注册类型名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //开票日期
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //征收机关名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //纳税人识别号
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //纳税人名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //银行名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //银行账号
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //预算科目代码
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //预算科目名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //预算分配比例（预算级次）
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //收款国库名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //所属时期-起
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //所属时期-止
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //税款限缴期限
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //征收品目名称
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //课税数量
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //销售收入
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //税率
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //实缴金额
				
				
//				for (int i = 0; i < rowObjects.length; i++) {
//					System.out.print(change(rowObjects[i]));
//					System.out.print("===");
//				}
			}

			// use this count to fetch all field information
			// for( int i=0; i<numberOfFields; i++) {
			//
			// DBFField field = reader.getField( i);
			//
			// // do something with it if you want
			// // refer the JavaDoc API reference for more details
			// System.out.println( field.getName());
			// }
			//

			// By now, we have itereated through all of the rows
			inputStream.close();
		} catch (DBFException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static Object change(Object obj){
		if(null == obj){
			return "";
		}else{
			if(obj instanceof String){
				return ((String) obj).trim();
			}else if(obj instanceof Integer){
				return obj.toString();
			}else if(obj instanceof Double){
				return obj.toString();
			}else if(obj instanceof java.util.Date){
				long ltime = ((java.util.Date)(obj)).getTime();
				return (new java.sql.Date(ltime)).toString();
			}
			
			return obj;
		}
	}


}
