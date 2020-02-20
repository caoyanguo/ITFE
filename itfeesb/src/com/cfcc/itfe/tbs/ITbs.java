/**
 * 
 */
package com.cfcc.itfe.tbs;

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
public interface ITbs {

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
	  @param admDivCode 区划代码
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @param vouNo		凭证编号
	 * @return 返回操作结果 例如(0:成功，-1:失败)
	 * @throws ITFEBizException 
	 */
	String confirmVoucher(@WebParam(name = "admDivCode") String admDivCode,
				           @WebParam(name = "vtCode") String vtCode,
				           @WebParam(name = "treCode") String treCode, 
				           @WebParam(name = "vouDate") String vouDate, 
				           @WebParam(name = "vouNo") String vouNo) ;

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
	
	/**凭证状态同步
	  @param admDivCode 区划代码
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @param vouNo		凭证编号
	 * @return	凭证状态
	 * @throws ITFEBizException 
	 */
	String synchronousVoucherStatus(@WebParam(name = "admDivCode") String admDivCode,
							   @WebParam(name = "vtCode") String vtCode,
							   @WebParam(name = "treCode") String treCode, 
							   @WebParam(name = "vouDate") String vouDate, 
							   @WebParam(name = "vouNo") String vouNo) ;
	
	
	/**
	 * 测试性连接
	 * @throws ITFEBizException
	 */
	void testConnect(@WebParam(name = "desOrgCode") String desOrgCode,@WebParam(name = "vouDate") String vouDate,@WebParam(name = "orgCode") String orgCode) ;
	/**
	 * 资金清算接口
	 * @param vtCode	凭证编号
	 * @param treCode	国库代码
	 * @param vouDate	凭证日期
	 * @param vouNo		凭证编号
	 * @throws ITFEBizException
	 */
	String fundLiquidation(@WebParam(name = "admDivCode") String admDivCode,
			   @WebParam(name = "vtCode") String vtCode,
			   @WebParam(name = "treCode") String treCode, 
			   @WebParam(name = "vouDate") String vouDate, 
			   @WebParam(name = "vouNo") String vouNo) ;
	/**
	 * 退库清算接口
	 * @param fileContent
	 * @return
	 * @throws ITFEBizException
	 */
//	String fundLiquidationDwbk(@WebParam(name = "fileContent") String fileContent) throws ITFEBizException;
	
	/**
	 * 
	 * @param orgCode	机构代码
	 * @param userCode	用户代码
	 * @param password	用户密码
	 * @param operate	0:登陆 1:退出
	 * @return
	 * @throws ITFEBizException
	 */
	String userLoginOrOut(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "password")String password,@WebParam(name = "operate")String operate) ;
	
	
	/**
	 * 
	 * @param voucherType 报文类型编号
	 * @param fileContent	文件内容
	 * @return
	 * @throws ITFEBizException
	 */
	String genVoucherTKXml(@WebParam(name = "voucherType") String voucherType,@WebParam(name = "fileContent") String fileContent) ;
	
	/**
	 * tbs的参数同步到ITFE(科目代码，法人代码)
	 * @param orgCode 核算主体代码
	 * @param paraType 代表是科目代码还是法人代码 0--科目代码   1--法人代码
	 * @param paramContent 同步的数据
	 * @return 是否成功 0:操作成功 -1:操作失败
	 */
	String synchronousParam(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "paraType") String paraType,@WebParam(name = "paramContent") String paramContent);
	
	/**
	 * tbs的报表数据同步到ITFE(收入，支出)
	 * @param orgCode 核算主体代码
	 * @param paraType 代表一个占位符为0
	 * @param paramContent 同步的数据
	 * @return 是否成功 0:操作成功 -1:操作失败
	 */
	String synchronousIncomeDayRpt(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "paraType") String paraType,@WebParam(name = "paramContent") String paramContent);
	
	/**
	 * tbs的库存同步到ITFE(库存)
	 * @param orgCode 核算主体代码
	 * @param paraType 代表一个占位符为0
	 * @param paramContent 同步的数据
	 * @return 是否成功 0:操作成功 -1:操作失败
	 */
	String synchronousStockRpt(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "checkDate") String paraType,@WebParam(name = "paramContent") String paramContent);
	
	/**
	 * 无纸化对账结果同步到tbs
	 * @param strecode 国库代码
	 * @param paraType 对账日期
	 * @param checkNum 对账次数
	 * @return
	 */
	String synchronousAcctBalance(@WebParam(name = "strecode")String strecode,@WebParam(name = "checkDate") String checkDate,@WebParam(name = "checkNum")String checkNum);
	
	/**
	 * tbs错误信息记录
	 * @param msgNo 功能编号或报文编号或凭证编号
	 * @param errorMsg 报错消息
	 */
	void tbsErrorLog(@WebParam(name = "msgNo")String msgNo,@WebParam(name = "errorMsg")String errorMsg);
	
	/**
	 * 更新凭证状态
	 * @param admDivCode 区划代码
	 * @param vtCode     凭证代码
	 * @param treCode    国库代码
	 * @param vouDate    接收日期
	 * @param vouNo      凭证编号
	 * @return
	 */
	String updateVoucherStatus(@WebParam(name = "admDivCode") String admDivCode,
	           @WebParam(name = "vtCode") String vtCode,
	           @WebParam(name = "treCode") String treCode, 
	           @WebParam(name = "vouDate") String vouDate, 
	           @WebParam(name = "vouNo") String vouNo);
}
