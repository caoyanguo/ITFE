package com.cfcc.test.test;

public class TestSplit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String xmlcontent = "><APP>ITFE</APP><MsgNo>9110</MsgNo><MsgID>20091";
		String lowlabel = "MsgNo".toLowerCase();
		String lowcontent = xmlcontent.toLowerCase();
		
		String label1 = "<" + lowlabel +">";
		String label2 = "</" + lowlabel +">";
		
		if(lowcontent.indexOf(label1) < 0 || lowcontent.indexOf(label2) < 0){
			System.out.println("�Ҳ���ƥ��[" + lowlabel + "]�ı�ǩ!");
		}
		
		int start1 = lowcontent.indexOf(label1);
		int start2 = lowcontent.indexOf(label2);
		
		if(start1 >= start2){
			System.out.println("��ʽ��ƥ�� start1 >= start2 !  " + start1 + ">" + start2);
			return ;
		}
		
		System.out.println(xmlcontent.substring(start1 + label1.length() , start2));
		
	

	}

}
