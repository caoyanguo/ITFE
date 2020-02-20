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
	 * 四舍五入数
	 */
	private static final int DEF_DIV_SCALE = 2;

	/**
	 *  
	 * 提供精确的加法运算。
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */

	public static BigDecimal add(BigDecimal b1, BigDecimal b2)
	{
		return b1.add(b2);
	}
	
	/**
	 *  
	 * 提供精确的加法运算。
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
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
	 * 提供精确的减法运算。
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
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
	 * 提供精确的乘法运算。
	 * @param b1
	 *            被乘数
	 * @param b2
	 *            乘数
	 * @return 两个参数的积
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
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
	 * 小数点以后2位，以后的数字四舍五入。
	 * @param b1
	 *            被除数 
	 * @param b2
	 *            除数
	 * @return 两个参数的商
	 */

	public static BigDecimal div(BigDecimal b1, BigDecimal b2)
	{
		return div(b1, b2, DEF_DIV_SCALE);

	}

	/**
	 * 
	 * 
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	 * 定精度，以后的数字四舍五入。
	 * @param b1
	 *            被除数
	 * @param b2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
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
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
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
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
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
