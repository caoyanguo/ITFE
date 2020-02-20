package com.cfcc.itfe.service.dataquery.tipsmsgproc;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.msgmanager.core.IServiceManagerServer;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time 09-12-10 13:27:07 codecomment:
 */

public class DealSendMsgService extends AbstractDealSendMsgService {

	private static Log log = LogFactory.getLog(DealSendMsgService.class);

	/**
	 * 分页查询发送报文信息
	 * 
	 * @generated
	 * @param finddto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageRequest
	 * @throws ITFEBizException
	 */
	public PageResponse findMsgByPage(TvFilepackagerefDto finddto, PageRequest pageRequest,String startdate,String enddate) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			finddto.setSorgcode(orgcode);
			String wherestr=null;
			
			if(null != finddto.getSfilename() && !"".equals(finddto.getSfilename())){
				String tmpfile = finddto.getSfilename();
				String upcase = tmpfile.toUpperCase();
				String lowcase = tmpfile.toLowerCase();
				
				wherestr = " (S_FILENAME = '" + upcase + "' or S_FILENAME = '" + lowcase + "' or S_FILENAME = '" + upcase + ".TXT' or S_FILENAME = '" + lowcase + ".txt' )";
				if (null != startdate && !"".equals(startdate)&&null != enddate && !"".equals(enddate)) {
					if(Integer.parseInt(startdate)>Integer.parseInt(enddate)){
						return null;
					}
					wherestr = wherestr + " and S_COMMITDATE between '"+startdate+"' and '"+enddate+"'";
				}else if(null != startdate && !"".equals(startdate)){
					wherestr = wherestr + " and S_COMMITDATE = '"+startdate+"'";
				}else if(null != enddate && !"".equals(enddate)){
					wherestr = wherestr + " and S_COMMITDATE = '"+enddate+"'";
				}
				finddto.setSfilename(null);
				return CommonFacade.getODB().findRsByDtoPaging(finddto, pageRequest, wherestr, " S_COMMITDATE,S_TAXORGCODE,S_PACKAGENO ");
			}
			if (null != startdate && !"".equals(startdate)&&null != enddate && !"".equals(enddate)) {
				if(Integer.parseInt(startdate)>Integer.parseInt(enddate)){
					return null;
				}
				wherestr = "  S_COMMITDATE between '"+startdate+"' and '"+enddate+"'";
			}else if(null != startdate && !"".equals(startdate)){
				wherestr =  "  S_COMMITDATE = '"+startdate+"'";
			}else if(null != enddate && !"".equals(enddate)){
				wherestr =  "  S_COMMITDATE = '"+enddate+"'";
			}
			return CommonFacade.getODB().findRsByDtoPaging(finddto, pageRequest, wherestr, " S_COMMITDATE,S_TAXORGCODE,S_PACKAGENO ");
		} catch (JAFDatabaseException e) {
			log.error("分页查询发送报文信息时错误!", e);
			throw new ITFEBizException("分页查询发送报文信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询发送报文信息时错误!", e);
			throw new ITFEBizException("分页查询发送报文信息时错误!", e);
		}
	}

	/**
	 * 重发报文信息
	 * 
	 * @generated
	 * @param senddto
	 * @throws ITFEBizException
	 */
	public void sendMsgRepeat(TvFilepackagerefDto senddto) throws ITFEBizException {
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();

		if (!orgcode.equals(senddto.getSorgcode())) {
			log.error("没有权限操作[" + senddto.getSorgcode() + "]机构的数据!");
			throw new ITFEBizException("没有权限操作[" + senddto.getSorgcode() + "]机构的数据!");
		}
		
		if (!DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING.equals(senddto.getSretcode())
				&& !DealCodeConstants.DEALCODE_ITFE_DEALING.equals(senddto.getSretcode())) {
			log.error("只能重发交易状态是[待处理]或[处理中]的记录！");
			throw new ITFEBizException("只能重发交易状态是[待处理]或[处理中]的记录！");
		}

		String smsgno = PublicSearchFacade.getMsgNoByBizType(senddto.getSoperationtypecode());

		// 取得对应的报文处理类
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
				MsgConstant.SPRING_SERVICE_SERVER + smsgno);

		iservice.sendMsg(senddto.getSfilename(), senddto.getSorgcode(), senddto.getSpackageno(), smsgno, senddto.getScommitdate(), null, true);
	}

	/**
	 * 查询打印发送报文信息
	 * 
	 * @generated
	 * @param printdto
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List findMsgByPrint(TvFilepackagerefDto printdto) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			printdto.setSorgcode(orgcode);

			return CommonFacade.getODB().findRsByDto(printdto);
		} catch (JAFDatabaseException e) {
			log.error("分页查询发送报文信息时错误!", e);
			throw new ITFEBizException("分页查询发送报文信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询发送报文信息时错误!", e);
			throw new ITFEBizException("分页查询发送报文信息时错误!", e);
		}
	}

}