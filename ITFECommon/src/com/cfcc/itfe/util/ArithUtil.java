package com.cfcc.itfe.util;

import java.math.BigDecimal;

public class ArithUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BigDecimal a = new BigDecimal("133");
		BigDecimal b = new BigDecimal("0.10");
		System.out.println(add(a,b,2));
		System.out.println(sub(a,b,2));
		System.out.println(mul(a,b,2));
		System.out.println(div(a,b,2));

	}

	/**
	 * ����������
	 */
	private static final int DEF_DIV_SCALE = 2;

	/**
	 *  
	 * �ṩ��ȷ�ļӷ����㡣
	 * @param v1
	 *            ������
	 * @param v2
	 *            ����
	 * @return ���������ĺ�
	 */

	public static BigDecimal add(BigDecimal b1, BigDecimal b2)
	{
		return b1.add(b2);
	}
	
	/**
	 *  
	 * �ṩ��ȷ�ļӷ����㡣
	 * @param v1
	 *            ������
	 * @param v2
	 *            ����
	 * @return ���������ĺ�
	 */

	public static BigDecimal add(BigDecimal b1, String b2)
	{
		return b1.add(new BigDecimal(b2));
	}
	
	public static BigDecimal add(BigDecimal b1, BigDecimal b2, int scale)
	{
		if (scale < 0)

		{
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		
		return round(add(b1,b2),scale);
	}
	public static BigDecimal addround(BigDecimal b1, BigDecimal b2)
	{	
		return round(add(b1,b2),DEF_DIV_SCALE);
	}

	/**
	 * 
	 * �ṩ��ȷ�ļ������㡣
	 * @param v1
	 *            ������
	 * @param v2
	 *            ����
	 * @return ���������Ĳ�
	 */

	public static BigDecimal sub(BigDecimal b1, BigDecimal b2)
	{
		return b1.subtract(b2);
	}
	
	public static BigDecimal sub(BigDecimal b1, BigDecimal b2, int scale)
	{
		if (scale < 0)

		{
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		
		return round(sub(b1,b2),scale);
	}
	
	public static BigDecimal subround(BigDecimal b1, BigDecimal b2)
	{
		return round(sub(b1,b2),DEF_DIV_SCALE);
	}

	/**
	 * �ṩ��ȷ�ĳ˷����㡣
	 * @param b1
	 *            ������
	 * @param b2
	 *            ����
	 * @return ���������Ļ�
	 */

	public static BigDecimal mul(BigDecimal b1, BigDecimal b2)
	{
		return b1.multiply(b2);
	}
	
	public static BigDecimal mul(BigDecimal b1, BigDecimal b2, int scale)
	{
		if (scale < 0)

		{
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		
		return round(mul(b1,b2),scale);
	}
	
	public static BigDecimal mulround(BigDecimal b1, BigDecimal b2)
	{
		return round(mul(b1,b2),DEF_DIV_SCALE);
	}
	
	/**
	 * 
	 * �ṩ����ԣ���ȷ�ĳ������㣬�����������������ʱ����ȷ��
	 * С�����Ժ�2λ���Ժ�������������롣
	 * @param b1
	 *            ������ 
	 * @param b2
	 *            ����
	 * @return ������������
	 */

	public static BigDecimal div(BigDecimal b1, BigDecimal b2)
	{
		return div(b1, b2, DEF_DIV_SCALE);

	}

	/**
	 * 
	 * 
	 * �ṩ����ԣ���ȷ�ĳ������㡣�����������������ʱ����scale����ָ
	 * �����ȣ��Ժ�������������롣
	 * @param b1
	 *            ������
	 * @param b2
	 *            ����
	 * @param scale
	 *            ��ʾ��ʾ��Ҫ��ȷ��С�����Ժ�λ��
	 * @return ������������
	 */

	public static BigDecimal div(BigDecimal b1, BigDecimal b2, int scale)
	{
		if (scale < 0)

		{
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);

	}

	/**
	 * 
	 * �ṩ��ȷ��С��λ�������봦��
	 * 
	 * @param v
	 *            ��Ҫ�������������
	 * @param scale
	 *            С���������λ
	 * @return ���������Ľ��
	 */

	public static BigDecimal round(BigDecimal b, int scale) 
	{
		if (scale < 0)

		{
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal one = new BigDecimal("1");

		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 
	 * �ṩ��ȷ��С��λ�������봦��
	 * 
	 * @param v
	 *            ��Ҫ�������������
	 * @param scale
	 *            С���������λ
	 * @return ���������Ľ��
	 */

	public static BigDecimal round(String Str, int scale) 
	{
		BigDecimal b= new BigDecimal(Str);
		if (scale < 0)

		{
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal one = new BigDecimal("1");

		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP);
	}

}
