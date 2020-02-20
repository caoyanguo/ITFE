package com.cfcc.test.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;

public class TestDBF {

	public static void main(String args[]) {

		String filepath = "D:\\����ǰ�������ļ�\\����\\��������\\һ��ͨ�ۿ�����0315\\��˰һ��ͨ�ۿ�����0315\\03160601.dbf";
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
				System.out.print(change(rowObjects[j++])); //˰Ʊ����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //������ϵ����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //�Ǽ�ע����������
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //��Ʊ����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //���ջ�������
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //��˰��ʶ���
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //��˰������
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //��������
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //�����˺�
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //Ԥ���Ŀ����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //Ԥ���Ŀ����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //Ԥ����������Ԥ�㼶�Σ�
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //�տ��������
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //����ʱ��-��
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //����ʱ��-ֹ
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //˰���޽�����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //����ƷĿ����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //��˰����
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //��������
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //˰��
				System.out.print("===");
				System.out.print(change(rowObjects[j++])); //ʵ�ɽ��
				
				
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
