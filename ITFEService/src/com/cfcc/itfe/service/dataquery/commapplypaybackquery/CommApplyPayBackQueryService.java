package com.cfcc.itfe.service.dataquery.commapplypaybackquery;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time   13-02-21 14:34:21
 * codecomment: 
 */

public class CommApplyPayBackQueryService extends AbstractCommApplyPayBackQueryService {
	private static Log log = LogFactory.getLog(CommApplyPayBackQueryService.class);	
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;

	/**
	 * �˻����а���֧����������������Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findMainByPage(TvPayreckBankBackDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSbookorgcode(orgcode);
			String where = null;
			if(mainDto.getSaddword()!=null)
			{
				where = mainDto.getSaddword();
				mainDto.setSaddword(null);
			}
			where = CommonUtil.getFuncCodeSql(where, mainDto, expfunccode, payamt);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, where, " S_BOOKORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ���а���֧�����������˻�������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���а���֧�����������˻�������Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ���а���֧�����������˻�������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���а���֧�����������˻�������Ϣʱ����!", e);
		}
    }

	/**
	 * ���а���֧�����������˻���ϸ��Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findSubByPage(TvPayreckBankBackDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	if (null == mainDto  || null == mainDto.getSpackno()) {
			return null;
		}
    	TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
    	subdto.setIvousrlno(mainDto.getIvousrlno());//ƾ֤��ˮ��
    	if(expfunccode!=null && !"".equals(expfunccode)){
    		subdto.setSfuncbdgsbtcode(expfunccode);
    	}
    	if(payamt!=null && !"".equals(payamt)){
    		subdto.setFamt(BigDecimal.valueOf(Long.valueOf(payamt)));
    	}
    	/*subdto.setDentrustdate(mainDto.getDentrustdate());
    	subdto.setSpackno(mainDto.getSpackno());
    	subdto.setSagentbnkcode(mainDto.getSagentbnkcode());
    	subdto.setStrano(mainDto.getStrano());*/
    	
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " I_SEQNO");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ���а���֧�����������˻���ϸ��Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���а���֧�����������˻���ϸ��Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ���а���֧�����������˻���ϸ��Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ���а���֧�����������˻���ϸ��Ϣʱ����!", e);
		}
    }

	@SuppressWarnings("unchecked")
	public String exportCommApplyPayBack(IDto finddto, String selectedtable)throws ITFEBizException {
		
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		String filename="";
		
		try {
//			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			startdate = TimeFacade.getCurrentStringTime();
			if(startdate.equals(enddate))
				pici++;
			else
				enddate = TimeFacade.getCurrentStringTime();
			if(selectedtable.equals("0")){
				TvPayreckBankBackDto mdto=(TvPayreckBankBackDto)finddto;
				mdto.setSbookorgcode(orgcode);
				String where = null;
				if(mdto.getSaddword()!=null)
				{
					where = " and "+mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,where);
				if("0".equals(mdto.getSpaymode()))//0ֱ��1��Ȩ
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+(pici<10?"0"+pici:pici)+"hd.txt";
				else
					filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+(pici<10?"0"+pici:pici)+"hd.txt";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = ",";//"\t"; // �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					TvPayreckBankBackListDto subPara = null;
					for (TvPayreckBankBackDto _dto :(List<TvPayreckBankBackDto>) list) {
						subPara = new TvPayreckBankBackListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
//						if(list.size()>1)
//							filebuf.append("**");
						filebuf.append(_dto.getStrecode());//�տ�������
						filebuf.append(splitSign+_dto.getSpayername());//����������-����������
						filebuf.append(splitSign+_dto.getSpayeracct());//�������տ��˻�-�������˺�
						filebuf.append(splitSign+_dto.getSagentbnkcode());//�������д���
						filebuf.append(splitSign+"");//�������п������к�-�����˿����д���
						filebuf.append(splitSign+_dto.getSpayeename());//�����տ�������-�տ�������
						filebuf.append(splitSign+_dto.getSpayeeacct());//�����տ����˻�-�տ����˺�
						filebuf.append(splitSign+"");//�����������д���-�տ��˿����д���
						filebuf.append(splitSign+"");//�������������к�-�տ��˿������к�
//						String bankname = bankMap.get(_dto.getSrecbankno()).getSbankname();//��������
						filebuf.append(splitSign+_dto.getSbudgettype());//Ԥ���������
						filebuf.append(splitSign+_dto.getFamt());//��������
						filebuf.append(splitSign+"0.00");//С���ֽ�����
						filebuf.append(splitSign);//ժҪ����
						filebuf.append(splitSign+_dto.getSvouno());//ƾ֤���
						filebuf.append(splitSign+_dto.getDvoudate().toString().replaceAll("-", ""));//ƾ֤����
						filebuf.append(splitSign+_dto.getSorivouno());//ԭƾ֤���
						filebuf.append(splitSign+_dto.getDorivoudate().toString().replaceAll("-", ""));//ԭƾ֤����
						filebuf.append(splitSign);//�Ƿ�ɹ�
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						if(_dto.getSaddword()!=null){
							filebuf.append(splitSign+_dto.getSaddword());//ԭ��
						}else{
							filebuf.append(splitSign+"");//ԭ��
						}
						
						filebuf.append("\r\n");
//						List<TvPayreckBankBackListDto> sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
//						for (TvPayreckBankBackListDto sub : sublist) {
//							filebuf.append(sub.getSfuncbdgsbtcode());//ԭƾ֤���
//							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//ԭƾ֤����
//							filebuf.append(splitSign+sub.getSbdgorgcode());//Ԥ�㵥λ����
//							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//�������Ŀ����
//							filebuf.append(splitSign+sub.getSecnomicsubjectcode());//�������Ŀ����
//							filebuf.append(splitSign+sub.getSacctprop());//�˻�����
//							filebuf.append(splitSign+sub.getFamt());//֧�����
//							filebuf.append("\r\n");
//						}
					}
				}
			File f = new File(fullpath);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(fullpath);
			}
			FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
			   return fullpath.replaceAll(root, "");
				
			} else {
				throw new ITFEBizException("��ѯ������");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	
	/**
	 * �������ӱ���Ϣ	 
	 * @generated
	 * @param finddto
	 * @param selectedtable
	 * @return String
	 * @throws ITFEBizException	 
	 */
	@SuppressWarnings("unchecked")
	public String exportFile(IDto finddto, String selectedtable)
			throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		List<TvPayreckBankBackDto> list=new ArrayList<TvPayreckBankBackDto>();
		String filename="";
		try {
			if(selectedtable.equals("0")){
				TvPayreckBankBackDto mdto=(TvPayreckBankBackDto)finddto;
				mdto.setSbookorgcode(orgcode);
				String where = null;
				if(mdto.getSaddword()!=null)
				{
					where = " and "+mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,where);
				if("0".equals(mdto.getSpaymode()))//0ֱ��1��Ȩ
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+".csv";
				else
					filename=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+".csv";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep +new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())	+ filename;
			String splitSign = ",";// �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					for (TvPayreckBankBackDto _dto :(List<TvPayreckBankBackDto>) list) {
						filebuf.append(_dto.getStrecode());//�տ�������
						filebuf.append(splitSign+_dto.getSpayername());//����������-����������
						filebuf.append(splitSign+_dto.getSpayeracct());//�������տ��˻�-�������˺�
						filebuf.append(splitSign+_dto.getSagentbnkcode());//�������д���
						filebuf.append(splitSign+"");//�������п������к�-�����˿����д���
						filebuf.append(splitSign+_dto.getSpayeename());//�����տ�������-�տ�������
						filebuf.append(splitSign+_dto.getSpayeeacct());//�����տ����˻�-�տ����˺�
						filebuf.append(splitSign+"");//�����������д���-�տ��˿����д���
						filebuf.append(splitSign+"");//�������������к�-�տ��˿������к�
						filebuf.append(splitSign+_dto.getSbudgettype());//Ԥ���������
						filebuf.append(splitSign+_dto.getFamt());//��������
						filebuf.append(splitSign+"0.00");//С���ֽ�����
						filebuf.append(splitSign);//ժҪ����
						filebuf.append(splitSign+_dto.getSvouno());//ƾ֤���
						filebuf.append(splitSign+_dto.getDvoudate().toString().replaceAll("-", ""));//ƾ֤����
						filebuf.append(splitSign+_dto.getSorivouno());//ԭƾ֤���
						filebuf.append(splitSign+_dto.getDorivoudate().toString().replaceAll("-", ""));//ԭƾ֤����
						filebuf.append("\r\n");
						TvPayreckBankBackListDto subPara =  new TvPayreckBankBackListDto();
						subPara.setIvousrlno(_dto.getIvousrlno());
						List<TvPayreckBankBackListDto> sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
						for (TvPayreckBankBackListDto sub : sublist) {
							filebuf.append(sub.getSorivouno());//ԭƾ֤���
							filebuf.append(splitSign+(sub.getDorivoudate()==null?"":sub.getDorivoudate().toString().replaceAll("-", "")));//ԭƾ֤����
							filebuf.append(splitSign+sub.getSbdgorgcode());//Ԥ�㵥λ����
							filebuf.append(splitSign+sub.getSfuncbdgsbtcode());//�������Ŀ����
							filebuf.append(splitSign+((sub.getSecnomicsubjectcode()==null)?"":sub.getSecnomicsubjectcode()));//�������Ŀ����
							filebuf.append(splitSign+sub.getSacctprop());//�˻�����
							filebuf.append(splitSign+sub.getFamt());//֧�����
							filebuf.append("\r\n");
						}
					}
				}
			File f = new File(fullpath);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(fullpath);
			}
			FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
			   return fullpath.replaceAll(root, "");
				
			} else {
				throw new ITFEBizException("��ѯ������");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}
	
}