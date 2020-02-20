package com.cfcc.test;

import java.sql.Date;

public class Test {
	public static void main(String[] args) {
		String date = "20191208";
		Date d = java.sql.Date.valueOf(date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6));
		System.out.println(d);
}
	
	
	
}
