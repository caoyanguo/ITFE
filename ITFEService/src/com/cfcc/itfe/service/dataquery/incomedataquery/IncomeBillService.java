package com.cfcc.itfe.service.dataquery.incomedataquery;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.msgmanager.core.IServiceManagerServer;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time 09-11-06 18:18:43 codecomment:
 */

public class IncomeBillService extends AbstractIncomeBillService {
	private static Log log = LogFactory.getLog(IncomeBillService.class);

	/**
	 * ��ҳ��ѯ˰Ʊ������Ϣ
	 * 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findIncomeByPage(TvInfileDto findDto, PageRequest pageRequest) throws ITFEBizException {
		try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			findDto.setSorgcode(orgcode);
			// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
			String wherestr = PublicSearchFacade.changeFileName(null, findDto.getSfilename());
			findDto.setSfilename(null);

			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, null, " S_ORGCODE,S_RECVTRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, wherestr, " S_ORGCODE,S_RECVTRECODE ");
			}
			
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
		}
	}
	/**
	 * ��ҳ��ѯ˰Ʊ������Ϣ
	 * 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findIncomeByPage(TvInfileDetailDto findDto, PageRequest pageRequest) throws ITFEBizException {
		try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			findDto.setSorgcode(orgcode);
			// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
			String wherestr = PublicSearchFacade.changeFileName(null, findDto.getSfilename());
			findDto.setSfilename(null);

			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, null, " S_ORGCODE,S_RECVTRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, wherestr, " S_ORGCODE,S_RECVTRECODE ");
			}
			
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ˰Ʊ������Ϣʱ����!", e);
		}
	}

	/**
	 * �ط�TIPSû���յ��ı���
	 * 
	 * @generated
	 * @param scommitdate
	 * @param spackageno
	 * @param sorgcode
	 * @param sfilename
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String reSendMsg(String scommitdate, String spackageno, String sorgcode, String sfilename) throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		
		if(!orgcode.equals(sorgcode)){
			log.error("û��Ȩ�޲���[" + sorgcode +"]����������!");
			throw new ITFEBizException("û��Ȩ�޲���[" + sorgcode +"]����������!");
		}
		
		String smsgno = MsgConstant.MSG_NO_7211;

		// ȡ�ö�Ӧ�ı��Ĵ�����
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
				MsgConstant.SPRING_SERVICE_SERVER + smsgno);

		iservice.sendMsg(sfilename, sorgcode, spackageno, smsgno, scommitdate, null,true);

		return null;
	}

	/**
	 * ��������˰Ʊ��Ϣ���Ա��޸ĺ����·��ͣ�
	 * 
	 * @generated
	 * @param String spackageno
	 * @param TvInfileDto findDt
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String exportIncomeData(String spackageno, TvInfileDto exportDto) throws ITFEBizException {
		try {
			List<TvInfileDto> list = CommonFacade.getODB().findRsByDto(exportDto);
			if(null == list || list.size() == 0){
				return null;
			}
			
			StringBuffer sbuf = new StringBuffer();
			for(int i = 0 ; i < list.size(); i++){
				TvInfileDto tmpdto = list.get(i);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSunitcode())); // ��λ����
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // �տ�������
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // Ŀ�Ĺ������
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStbstaxorgcode())); // TBS���ջ��ش���
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(""); // ƾ֤���
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybookkind())); // �ɿ����������
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // ��������
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // Ԥ�㼶��	
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // Ԥ������
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // ��Ŀ����
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStbsassitsign())); // ������־
//				sbuf.append(StateConstant.INCOME_SPLIT);
//				sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // ������(#.00)
//				sbuf.append(StateConstant.SPACE_SPLIT);
				
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxorgcode())); // ���ջ��ش���
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // �տ�������
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // ������(#.00)
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSgenticketdate())); // ��Ʊ����
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // Ԥ���Ŀ
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // Ԥ�㼶��	
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // Ԥ������
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSassitsign())); // ������־ 	
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpaycode())); // ��˰�˱���
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpayname())); // ��˰������
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybnkno())); // �������к�
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpayacct())); // �����ʻ�
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // ������к�
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxstartdate())); // ������ʼ����
				sbuf.append(StateConstant.INCOME_SPLIT);
				sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxenddate())); // ������ֹ����
				sbuf.append(StateConstant.SPACE_SPLIT);
			}
			
			return sbuf.toString();
		} catch (JAFDatabaseException e) {
			log.error("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
			throw new ITFEBizException("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
		} catch (ValidateException e) {
			log.error("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
			throw new ITFEBizException("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
		}
	}
	
	/**
	 * ��������ѯ������˰Ʊ��Ϣ
	 * 
	 * @generated
	 * @param List exportAlldataList
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String exportAllIncomeData(List exportAlldataList, String ifdetail)
			throws ITFEBizException {
		StringBuffer sbuf = new StringBuffer();
		try{
			if("1".equals(ifdetail)) {
				List<TvInfileDetailDto> list=(List<TvInfileDetailDto>)exportAlldataList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDetailDto tmp = list.get(i);
					TvInfileDto tmpdto = new TvInfileDto();
					tmpdto.setSrecvtrecode(tmp.getSrecvtrecode());
					tmpdto.setScommitdate(tmp.getScommitdate());
					tmpdto.setNmoney(tmp.getNmoney());
					tmpdto.setSfilename(tmp.getSfilename());
					tmpdto.setSpackageno(tmp.getSpackageno());
					tmpdto.setSdealno(tmp.getSdealno());
					tmpdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
					tmpdto.setStaxorgcode(tmp.getStaxorgcode());
					tmpdto.setStbsassitsign(tmp.getStbsassitsign());
					tmpdto.setSassitsign(tmp.getSassitsign());
					tmpdto.setSbudgettype(tmp.getSbudgettype());
					tmpdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
					tmpdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
					tmpdto.setSpaybookkind(tmp.getSpaybookkind());
					tmpdto.setStrasrlno(tmp.getStrasrlno());
					
					List<TvInfileDto> list1 = CommonFacade.getODB().findRsByDto(tmpdto);
					if(null!=list1&&list1.size()>0){
						for(int j = 0 ; j < list1.size(); j++){
							TvInfileDto tmpdto1 = list1.get(j);
							
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxorgcode())); // ���ջ��ش���
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSrecvtrecode())); // �տ�������
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getNmoney())); // ������(#.00)
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSgenticketdate())); // ��Ʊ����
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetsubcode())); // Ԥ���Ŀ
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetlevelcode())); // Ԥ�㼶��	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgettype())); // Ԥ������
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSassitsign())); // ������־ 	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpaycode())); // ��˰�˱���
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpayname())); // ��˰������
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpaybnkno())); // �������к�
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpayacct())); // �����ʻ�
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSopenaccbankcode())); // ������к�
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxstartdate())); // ������ʼ����
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxenddate())); // ������ֹ����
							sbuf.append(StateConstant.SPACE_SPLIT);
						}
					}
				}
			}else if("0".equals(ifdetail)){
				List<TvInfileDto> list=(List<TvInfileDto>)exportAlldataList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDto tmpdto = list.get(i);
					
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxorgcode())); // ���ջ��ش���
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // �տ�������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // ������(#.00)
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSgenticketdate())); // ��Ʊ����
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // Ԥ���Ŀ
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // Ԥ�㼶��	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // Ԥ������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSassitsign())); // ������־ 	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpaycode())); // ��˰�˱���
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpayname())); // ��˰������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybnkno())); // �������к�
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpayacct())); // �����ʻ�
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // ������к�
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxstartdate())); // ������ʼ����
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxenddate())); // ������ֹ����
					sbuf.append(StateConstant.SPACE_SPLIT);
				}
			}
		
		} catch (JAFDatabaseException e) {
			log.error("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
			throw new ITFEBizException("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
		} catch (ValidateException e) {
			log.error("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
			throw new ITFEBizException("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
		}
		return sbuf.toString();
	}
	
	
	/**
	 * ��������ѯ��ѡ��˰Ʊ��Ϣ
	 * 
	 * @generated
	 * @param List exportSelectedList
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String exportSelectedIncomeData(List exportSelectedList, String ifdetail)
			throws ITFEBizException {
		StringBuffer sbuf = new StringBuffer();
		try{
			if("1".equals(ifdetail)) {
				List<TvInfileDetailDto> list=(List<TvInfileDetailDto>)exportSelectedList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDetailDto tmp = list.get(i);
					TvInfileDto tmpdto = new TvInfileDto();
					tmpdto.setSrecvtrecode(tmp.getSrecvtrecode());
					tmpdto.setScommitdate(tmp.getScommitdate());
					tmpdto.setNmoney(tmp.getNmoney());
					tmpdto.setSfilename(tmp.getSfilename());
					tmpdto.setSpackageno(tmp.getSpackageno());
					tmpdto.setSdealno(tmp.getSdealno());
					tmpdto.setStbstaxorgcode(tmp.getStbstaxorgcode());
					tmpdto.setStaxorgcode(tmp.getStaxorgcode());
					tmpdto.setStbsassitsign(tmp.getStbsassitsign());
					tmpdto.setSassitsign(tmp.getSassitsign());
					tmpdto.setSbudgettype(tmp.getSbudgettype());
					tmpdto.setSbudgetlevelcode(tmp.getSbudgetlevelcode());
					tmpdto.setSbudgetsubcode(tmp.getSbudgetsubcode());
					tmpdto.setSpaybookkind(tmp.getSpaybookkind());
					tmpdto.setStrasrlno(tmp.getStrasrlno());
					
					List<TvInfileDto> list1 = CommonFacade.getODB().findRsByDto(tmpdto);
					
					if(null!=list1&&list1.size()>0){
						for(int j = 0 ; j < list1.size(); j++){
							TvInfileDto tmpdto1 = list1.get(j);
							
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxorgcode())); // ���ջ��ش���
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSrecvtrecode())); // �տ�������
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getNmoney())); // ������(#.00)
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSgenticketdate())); // ��Ʊ����
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetsubcode())); // Ԥ���Ŀ
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgetlevelcode())); // Ԥ�㼶��	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSbudgettype())); // Ԥ������
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSassitsign())); // ������־ 	
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpaycode())); // ��˰�˱���
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxpayname())); // ��˰������
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpaybnkno())); // �������к�
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSpayacct())); // �����ʻ�
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getSopenaccbankcode())); // ������к�
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxstartdate())); // ������ʼ����
							sbuf.append(StateConstant.INCOME_SPLIT);
							sbuf.append(MtoCodeTrans.transformString(tmpdto1.getStaxenddate())); // ������ֹ����
							sbuf.append(StateConstant.SPACE_SPLIT);
						}
					}
				}
			}else if("0".equals(ifdetail)){
				List<TvInfileDto> list=(List<TvInfileDto>)exportSelectedList;
				
				for(int i = 0 ; i < list.size(); i++){
					TvInfileDto tmpdto = list.get(i);
					
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxorgcode())); // ���ջ��ش���
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSrecvtrecode())); // �տ�������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getNmoney())); // ������(#.00)
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSgenticketdate())); // ��Ʊ����
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetsubcode())); // Ԥ���Ŀ
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgetlevelcode())); // Ԥ�㼶��	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSbudgettype())); // Ԥ������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSassitsign())); // ������־ 	
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpaycode())); // ��˰�˱���
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxpayname())); // ��˰������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpaybnkno())); // �������к�
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSpayacct())); // �����ʻ�
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getSopenaccbankcode())); // ������к�
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxstartdate())); // ������ʼ����
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(MtoCodeTrans.transformString(tmpdto.getStaxenddate())); // ������ֹ����
					sbuf.append(StateConstant.SPACE_SPLIT);
				}
			}
		
		} catch (JAFDatabaseException e) {
			log.error("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
			throw new ITFEBizException("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
		} catch (ValidateException e) {
			log.error("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
			throw new ITFEBizException("���ݵ�������������ѯ��Ӧ˰Ʊ��Ϣ��ʱ����ִ���!", e);
		}
		
		return sbuf.toString();
	}
	
	
	/**
	 * ��ҳ��ѯ˰Ʊ������Ϣ
	 * 
	 * @generated
	 * @param findDto
	 * @return List
	 * @throws ITFEBizException
	 */
	public List findIncomeByDto(IDto findDto, String ifdetail) throws ITFEBizException {
		List list=new ArrayList();
		try {
			if("1".equals(ifdetail)) {
				TvInfileDetailDto tvinfiledetaildto=(TvInfileDetailDto)findDto;
				// ȡ�ò���Ա�Ļ�������
				String orgcode = this.getLoginInfo().getSorgcode();
				tvinfiledetaildto.setSorgcode(orgcode);
				// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
				String wherestr = PublicSearchFacade.changeFileName(null, tvinfiledetaildto.getSfilename());
				tvinfiledetaildto.setSfilename(null);

				if (null == wherestr) {
					list= CommonFacade.getODB().findRsByDto(tvinfiledetaildto);
				} else {
					list= CommonFacade.getODB().findRsByDtoForWhere(tvinfiledetaildto, " and "+wherestr);
				}
			}else if("0".equals(ifdetail)){
				TvInfileDto tvinfileDto=(TvInfileDto)findDto;
				// ȡ�ò���Ա�Ļ�������
				String orgcode = this.getLoginInfo().getSorgcode();
				tvinfileDto.setSorgcode(orgcode);
				// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
				String wherestr = PublicSearchFacade.changeFileName(null, tvinfileDto.getSfilename());
				tvinfileDto.setSfilename(null);

				if (null == wherestr) {
					list= CommonFacade.getODB().findRsByDto(tvinfileDto);
				} else {
					list= CommonFacade.getODB().findRsByDtoForWhere(tvinfileDto, " and "+wherestr);
				}
			}
			return list;
		} catch (JAFDatabaseException e) {
			log.error("��ѯ˰Ʊ������Ϣʱ����!", e);
			throw new ITFEBizException("��ѯ˰Ʊ������Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ѯ˰Ʊ������Ϣʱ����!", e);
			throw new ITFEBizException("��ѯ˰Ʊ������Ϣʱ����!", e);
		}
	}
}