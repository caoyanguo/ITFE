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
	 * ��ҳ��ѯ���ͱ�����Ϣ
	 * 
	 * @generated
	 * @param finddto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageRequest
	 * @throws ITFEBizException
	 */
	public PageResponse findMsgByPage(TvFilepackagerefDto finddto, PageRequest pageRequest,String startdate,String enddate) throws ITFEBizException {
		try {
			// ȡ�ò���Ա�Ļ�������
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
			log.error("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
		}
	}

	/**
	 * �ط�������Ϣ
	 * 
	 * @generated
	 * @param senddto
	 * @throws ITFEBizException
	 */
	public void sendMsgRepeat(TvFilepackagerefDto senddto) throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();

		if (!orgcode.equals(senddto.getSorgcode())) {
			log.error("û��Ȩ�޲���[" + senddto.getSorgcode() + "]����������!");
			throw new ITFEBizException("û��Ȩ�޲���[" + senddto.getSorgcode() + "]����������!");
		}
		
		if (!DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING.equals(senddto.getSretcode())
				&& !DealCodeConstants.DEALCODE_ITFE_DEALING.equals(senddto.getSretcode())) {
			log.error("ֻ���ط�����״̬��[������]��[������]�ļ�¼��");
			throw new ITFEBizException("ֻ���ط�����״̬��[������]��[������]�ļ�¼��");
		}

		String smsgno = PublicSearchFacade.getMsgNoByBizType(senddto.getSoperationtypecode());

		// ȡ�ö�Ӧ�ı��Ĵ�����
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
				MsgConstant.SPRING_SERVICE_SERVER + smsgno);

		iservice.sendMsg(senddto.getSfilename(), senddto.getSorgcode(), senddto.getSpackageno(), smsgno, senddto.getScommitdate(), null, true);
	}

	/**
	 * ��ѯ��ӡ���ͱ�����Ϣ
	 * 
	 * @generated
	 * @param printdto
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List findMsgByPrint(TvFilepackagerefDto printdto) throws ITFEBizException {
		try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			printdto.setSorgcode(orgcode);

			return CommonFacade.getODB().findRsByDto(printdto);
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���ͱ�����Ϣʱ����!", e);
		}
	}

}