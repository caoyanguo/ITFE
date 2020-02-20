/**
 * 
 */
package com.cfcc.itfe.twcs;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 *
 */

@WebService
public interface ITwcs {

	/**
	 * 测试
	 * @param text
	 * @return
	 * @throws ITFEBizException 
	 */
	String sayHi(@WebParam(name = "text") String text);
	
	/**读取凭证
	 * @param admDivCode区划代码
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @return	返回凭证内容，用Base64加密
	 * @throws ITFEBizException 
	 */
	String readVoucher(@WebParam(name = "admDivCode") String admDivCode,
			           @WebParam(name = "vtCode") String vtCode,
			           @WebParam(name = "treCode") String treCode, 
			           @WebParam(name = "vouDate") String vouDate) ;
	/**重新读取凭证
	 * @param admDivCode区划代码
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @return	返回凭证内容，用Base64加密
	 * @throws ITFEBizException 
	 */
	String readVoucherAgain(@WebParam(name = "admDivCode") String admDivCode,
			           @WebParam(name = "vtCode") String vtCode,
			           @WebParam(name = "treCode") String treCode, 
			           @WebParam(name = "vouDate") String vouDate) ;
	/**凭证签收
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @return 返回操作结果 例如(0:成功，-1:失败)
	 * @throws ITFEBizException 
	 */
	String updateConPayState(
				           @WebParam(name = "vtCode") String vtCode,
				           @WebParam(name = "treCode") String treCode, 
				           @WebParam(name = "vouDate") String vouDate) ;

	/**凭证退回
	  @param admDivCode 区划代码
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @param vouNo		凭证编号
	 * @param errMsg	失败原因
	 * @return	返回操作结果 例如(0:成功，-1:失败)
	 * @throws ITFEBizException 
	 */
	String returnVoucherBack(@WebParam(name = "admDivCode") String admDivCode,
					           @WebParam(name = "vtCode") String vtCode,
					           @WebParam(name = "treCode") String treCode, 
					           @WebParam(name = "vouDate") String vouDate, 
					           @WebParam(name = "vouNo") String vouNo,
					           @WebParam(name = "errMsg") String errMsg) ;
	
	/**发送凭证
	  @param admDivCode 区划代码
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @param vouNo		凭证编号
	 * @return  返回操作结果 例如(0:成功，-1:失败)
	 * @throws ITFEBizException 
	 */
	String sendVoucher(@WebParam(name = "admDivCode") String admDivCode,
					   @WebParam(name = "vtCode") String vtCode,
					   @WebParam(name = "treCode") String treCode, 
					   @WebParam(name = "vouDate") String vouDate, 
					   @WebParam(name = "vouNo") String vouNo) ;
	

	/**根据收支监管系统的清算状态更新无纸化的状态，状态更新后，才能进行下一步处理
	 * @param vtCode	凭证编号
	 * @param msgInfo  更新内容
	 * @return	更新情况
	 * @throws ITFEBizException 
	 */
	String updateItfeVoucherInfo(@WebParam(name = "vtCode") String vtCode,
							   @WebParam(name = "msgInfo") String msgInfo) ;
	
	String synchronousVoucherStatus(
			   @WebParam(name = "vtCode") String vtCode, 
			   @WebParam(name = "dealsInfo") String dealsInfo) ;
	
	
	/**
	 * 测试性连接
	 * @throws ITFEBizException
	 */
	void testConnect(@WebParam(name = "desOrgCode") String desOrgCode,@WebParam(name = "vouDate") String vouDate,@WebParam(name = "orgCode") String orgCode) ;
	
	
}
