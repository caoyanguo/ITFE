package com.cfcc.itfe.service.dataquery.grantpayquery;

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
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.IServiceManagerServer;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time 09-11-07 17:12:15 codecomment:
 */
@SuppressWarnings("unchecked")
public class GrantPayService extends AbstractGrantPayService {

	private static Log log = LogFactory.getLog(GrantPayService.class);
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;
	/**
	 * ��ҳ��ѯ��Ȩ֧���������Ϣ
	 * 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findMainByPage(TvGrantpaymsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
		try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			
			// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
			String wherestr = PublicSearchFacade.changeFileName(null, mainDto.getSfilename());
			mainDto.setSfilename(null);
			if(mainDto.getSdemo()!=null)
			{
				if(wherestr!=null&&!"".equals(wherestr))
					wherestr=" and "+mainDto.getSdemo();
				else
					wherestr =mainDto.getSdemo();
				mainDto.setSdemo(null);
				mainDto.setScommitdate(null);
			}
			wherestr = CommonUtil.getFuncCodeSql(wherestr, mainDto, expfunccode, payamt);
			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, null, " S_ORGCODE,S_TRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, wherestr, " S_ORGCODE,S_TRECODE ");
			}
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
		}
	}

	/**
	 * ��ҳ��ѯ��Ȩ֧���������Ϣ
	 * 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException
	 */
	public PageResponse findSubByPage(TvGrantpaymsgmainDto mainDto, String sstatus, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
		if (null == mainDto || null == mainDto.getSorgcode() || "".equals(mainDto.getSorgcode().trim()) || null == mainDto.getSlimitid()
				|| "".equals(mainDto.getSlimitid().trim()) || null == mainDto.getSofyear() || "".equals(mainDto.getSofyear().trim())
				|| null == mainDto.getSfilename() || "".equals(mainDto.getSfilename().trim()) || null == mainDto.getSpackageno()
				|| "".equals(mainDto.getSpackageno().trim())) {
			return null;
		}

		TvGrantpaymsgsubDto subdto = new TvGrantpaymsgsubDto();
		subdto.setIvousrlno(mainDto.getIvousrlno());
		subdto.setSpackageticketno(mainDto.getSpackageticketno());
//		subdto.setSorgcode(mainDto.getSorgcode());
//		subdto.setSlimitid(mainDto.getSlimitid());
//		subdto.setSofyear(mainDto.getSofyear());
//		subdto.setSfilename(mainDto.getSfilename());
//		subdto.setSpackageno(mainDto.getSpackageno());
		
		if(null != sstatus && !"".equals(sstatus.trim())){
			subdto.setSstatus(sstatus);
		}
		if(null != expfunccode && !"".equals(expfunccode.trim())){
			subdto.setSfunsubjectcode(expfunccode);
		}
		if(null != payamt && !"".equals(payamt.trim())){
			subdto.setNmoney(BigDecimal.valueOf(Long.valueOf(payamt)));
		}
		
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " S_DEALNO,S_LINE,S_BUDGETUNITCODE,S_FUNSUBJECTCODE ");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧������Ϣʱ����!", e);
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
		
		String smsgno = MsgConstant.MSG_NO_5103;

		// ȡ�ö�Ӧ�ı��Ĵ�����
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
				MsgConstant.SPRING_SERVICE_SERVER + smsgno);

		iservice.sendMsg(sfilename, sorgcode, spackageno, smsgno, scommitdate, null,true);

		return null;
	}

	public PageResponse findMainByPageForHis(HtvGrantpaymsgmainDto mainDto,
			PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
		try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			
			// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
			String wherestr = PublicSearchFacade.changeFileName(null, mainDto.getSfilename());
			mainDto.setSfilename(null);
			if(mainDto.getSdemo()!=null)
			{
				if(wherestr!=null&&!"".equals(wherestr))
					wherestr+=" and "+mainDto.getSdemo();
				else
					wherestr =mainDto.getSdemo();
				mainDto.setSdemo(null);
				mainDto.setScommitdate(null);
			}
			wherestr = CommonUtil.getFuncCodeSql(wherestr, mainDto, expfunccode, payamt);
			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, null, " S_ORGCODE,S_TRECODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, wherestr, " S_ORGCODE,S_TRECODE ");
			}
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
		}
	}

	public PageResponse findSubByPageForHis(HtvGrantpaymsgmainDto mainDto,
			String sstatus, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
		if (null == mainDto || null == mainDto.getSorgcode() || "".equals(mainDto.getSorgcode().trim()) || null == mainDto.getSlimitid()
				|| "".equals(mainDto.getSlimitid().trim()) || null == mainDto.getSofyear() || "".equals(mainDto.getSofyear().trim())
				|| null == mainDto.getSfilename() || "".equals(mainDto.getSfilename().trim()) || null == mainDto.getSpackageno()
				|| "".equals(mainDto.getSpackageno().trim())) {
			return null;
		}

		HtvGrantpaymsgsubDto subdto = new HtvGrantpaymsgsubDto();
		subdto.setSorgcode(mainDto.getSorgcode());
		subdto.setSlimitid(mainDto.getSlimitid());
		subdto.setSofyear(mainDto.getSofyear());
		subdto.setSfilename(mainDto.getSfilename());
		subdto.setSpackageno(mainDto.getSpackageno());
		
		if(null != sstatus && !"".equals(sstatus.trim())){
			subdto.setSstatus(sstatus);
		}
		if(null != expfunccode && !"".equals(expfunccode.trim())){
			subdto.setSfunsubjectcode(expfunccode);
		}
		if(null != payamt && !"".equals(payamt.trim())){
			subdto.setNmoney(BigDecimal.valueOf(Long.valueOf(payamt)));
		}
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " S_DEALNO,S_LINE,S_BUDGETUNITCODE,S_FUNSUBJECTCODE ");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯ��Ȩ֧����ʷ����Ϣʱ����!", e);
		}
	}
	
	/**
	 * ����txt	 
	 * @generated
	 * @param mainDto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
  
public String dataexport(IDto mainDto, String selectedtable) throws ITFEBizException{
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
				TvGrantpaymsgmainDto mdto=(TvGrantpaymsgmainDto)mainDto;
				mdto.setSorgcode(orgcode);//				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				list=CommonFacade.getODB().findRsByDto(mdto);
			}else if(selectedtable.equals("1")){
				TvGrantpaymsgmainDto mdto=(TvGrantpaymsgmainDto)mainDto;
				HtvGrantpaymsgmainDto htvdto=new HtvGrantpaymsgmainDto();
				htvdto.setStrecode(mdto.getStrecode());
				htvdto.setSpackageticketno(mdto.getSpackageticketno());
				htvdto.setSlimitid(mdto.getSlimitid());
				htvdto.setScommitdate(mdto.getScommitdate());
				htvdto.setSpayunit(mdto.getSpayunit());
				htvdto.setNmoney(mdto.getNmoney());
				htvdto.setSpackageno(mdto.getSpackageno());
				htvdto.setSofyear(mdto.getSofyear());
				htvdto.setSfilename(mdto.getSfilename());
				htvdto.setSstatus(mdto.getSstatus());//				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				htvdto.setSorgcode(orgcode);
				list=CommonFacade.getODB().findRsByDto(htvdto);
			}
			filename=TimeFacade.getCurrentStringTime()+"02"+(pici<10?"0"+pici:pici)+"hd.txt";
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = ",";//"\t"; // �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
//				int index=1;
				if(selectedtable.equals("0")){
					filebuf.append("�ļ�����,�������,���д���,ƾ֤���,���κ�,��������,Ԥ�㵥λ,�����·�,Ԥ������,���,����ɹ�,˵��\r\n");
					for (TvGrantpaymsgmainDto _dto :(List<TvGrantpaymsgmainDto>) list) {
						TvGrantpaymsgsubDto subPara = new TvGrantpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
//						subPara.setSlimitid(_dto.getSlimitid());
//						subPara.setSofyear(_dto.getSofyear());
//						subPara.setSfilename(_dto.getSfilename());
//						subPara.setSpackageno(_dto.getSpackageno());
//						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);					
						filebuf.append(_dto.getSfilename());//�ļ�����
						filebuf.append(splitSign);
						filebuf.append(_dto.getStrecode());//�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransactunit());//���д���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpackageticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSlimitid());//���κ�
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransbankcode());//�������д���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgetunitcode());//Ԥ�㵥λ����
						filebuf.append(splitSign);
						filebuf.append((_dto.getSofmonth()!=null&&_dto.getSofmonth().length()==1)?"0"+_dto.getSofmonth():_dto.getSofmonth());//�����·�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//Ԥ������
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//��������
						filebuf.append(splitSign);
//						filebuf.append("0.00");//С���ֽ�����
//						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						if("80000".equals(_dto.getSstatus()))
							filebuf.append("�ɹ�");//˵��
						else if("80001".equals(_dto.getSstatus()))
							filebuf.append("ʧ��");//˵��
						else if("80002".equals(_dto.getSstatus()))
							filebuf.append("������");//˵��
						else if("80008".equals(_dto.getSstatus()))
							filebuf.append("δ����");//˵��
						else if("80009".equals(_dto.getSstatus()))
							filebuf.append("������");//˵��
						else
							filebuf.append(_dto.getSdemo());//˵��
						filebuf.append("\r\n");
//						//�ӱ���Ϣ
//						for(TvGrantpaymsgsubDto sub : (List<TvGrantpaymsgsubDto>)sublist){
//							filebuf.append(sub.getSfunsubjectcode());//���ܿ�Ŀ����
//							filebuf.append(splitSign);
//							filebuf.append(sub.getSecosubjectcode());//���ÿ�Ŀ����
//							filebuf.append(splitSign);
//							filebuf.append(sub.getNmoney());//��������
//							filebuf.append(splitSign);
//							filebuf.append("0.00");//С���ֽ�����
//							filebuf.append("\r\n");
//						}
						
					}
				}else if(selectedtable.equals("1")){
					for (HtvGrantpaymsgmainDto _dto :(List<HtvGrantpaymsgmainDto>) list) {
						HtvGrantpaymsgsubDto subPara = new HtvGrantpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
//						subPara.setSlimitid(_dto.getSlimitid());
//						subPara.setSofyear(_dto.getSofyear());
//						subPara.setSfilename(_dto.getSfilename());
//						subPara.setSpackageno(_dto.getSpackageno());
//						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						filebuf.append(_dto.getSfilename());//�ļ�����
						filebuf.append(splitSign);
						filebuf.append(_dto.getStrecode());//�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransactunit());//���д���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpackageticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSlimitid());//���κ�
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransbankcode());//�������д���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgetunitcode());//Ԥ�㵥λ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSofmonth());//�����·�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//Ԥ������
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//��������
						filebuf.append(splitSign);
						filebuf.append("0.00");//С���ֽ�����
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						if("80000".equals(_dto.getSstatus()))
							filebuf.append("�ɹ�");//˵��
						else if("80001".equals(_dto.getSstatus()))
							filebuf.append("ʧ��");//˵��
						else if("80002".equals(_dto.getSstatus()))
							filebuf.append("������");//˵��
						else if("80008".equals(_dto.getSstatus()))
							filebuf.append("δ����");//˵��
						else if("80009".equals(_dto.getSstatus()))
							filebuf.append("������");//˵��
						else
							filebuf.append(_dto.getSdemo());//˵��
						filebuf.append("\r\n");
						//�ӱ���Ϣ
//						for(TvGrantpaymsgsubDto sub : (List<TvGrantpaymsgsubDto>)sublist){
//							filebuf.append(sub.getSfunsubjectcode());//���ܿ�Ŀ����
//							filebuf.append(splitSign);
//							filebuf.append(sub.getSecosubjectcode());//���ÿ�Ŀ����
//							filebuf.append(splitSign);
//							filebuf.append(sub.getNmoney());//��������
//							filebuf.append(splitSign);
//							filebuf.append("0.00");//С���ֽ�����
//							filebuf.append("\r\n");
//						}
					}
				}
			
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
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
	 * ����CSV	 
	 * @generated
	 * @param mainDto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
	public String exportFile(IDto mainDto, String selectedtable)
			throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		try {
			if(selectedtable.equals("0")){
				TvGrantpaymsgmainDto mdto=(TvGrantpaymsgmainDto)mainDto;
				mdto.setSorgcode(orgcode);
				String where = null;
				if(mdto.getSdemo()!=null)
				{
					where=" and "+mdto.getSdemo();
					mdto.setSdemo(null);
					mdto.setScommitdate(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto, where);
			}else if(selectedtable.equals("1")){
				TvGrantpaymsgmainDto mdto=(TvGrantpaymsgmainDto)mainDto;
				HtvGrantpaymsgmainDto htvdto=new HtvGrantpaymsgmainDto();
				htvdto.setStrecode(mdto.getStrecode());
				htvdto.setSpackageticketno(mdto.getSpackageticketno());
				htvdto.setSlimitid(mdto.getSlimitid());
				htvdto.setScommitdate(mdto.getScommitdate());
				htvdto.setSpayunit(mdto.getSpayunit());
				htvdto.setNmoney(mdto.getNmoney());
				htvdto.setSpackageno(mdto.getSpackageno());
				htvdto.setSofyear(mdto.getSofyear());
				htvdto.setSfilename(mdto.getSfilename());
				htvdto.setSstatus(mdto.getSstatus());
				htvdto.setSorgcode(orgcode);
				String where = null;
				if(mdto.getSdemo()!=null)
				{
					where=" and "+mdto.getSdemo();
					htvdto.setSdemo(null);
					htvdto.setScommitdate(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(htvdto,where);
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+"02.csv";
			String splitSign = ",";// �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					for (TvGrantpaymsgmainDto _dto :(List<TvGrantpaymsgmainDto>) list) {
						filebuf.append(_dto.getStrecode());//�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpackageticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransactunit());//�������д���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgetunitcode());//Ԥ�㵥λ����
						filebuf.append(splitSign);
						filebuf.append((_dto.getSofmonth()!=null&&_dto.getSofmonth().length()==1)?"0"+_dto.getSofmonth():_dto.getSofmonth());//�����·�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//Ԥ������
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//��������
						filebuf.append(splitSign);
						filebuf.append("0.00");//С���ֽ�����
						filebuf.append("\r\n");
						//�ӱ���Ϣ
						TvGrantpaymsgsubDto subPara = new TvGrantpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
						subPara.setIvousrlno(_dto.getIvousrlno());
						subPara.setSpackageticketno(_dto.getSpackageticketno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);	
						for(TvGrantpaymsgsubDto sub : (List<TvGrantpaymsgsubDto>)sublist){
							filebuf.append(sub.getSfunsubjectcode());//���ܿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getSecosubjectcode()==null?"":sub.getSecosubjectcode());//���ÿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getNmoney());//��������
							filebuf.append(splitSign);
							filebuf.append("0.00");//С���ֽ�����
							filebuf.append("\r\n");
						}
					}
				}else if(selectedtable.equals("1")){
					for (HtvGrantpaymsgmainDto _dto :(List<HtvGrantpaymsgmainDto>) list) {
						filebuf.append(_dto.getStrecode());//�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpackageticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getStransactunit());//�������д���
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgetunitcode());//Ԥ�㵥λ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSofmonth());//�����·�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSbudgettype());//Ԥ������
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//��������
						filebuf.append(splitSign);
						filebuf.append("0.00");//С���ֽ�����
						filebuf.append("\r\n");
						//�ӱ���Ϣ
						HtvGrantpaymsgsubDto subPara = new HtvGrantpaymsgsubDto();
						subPara.setSorgcode(_dto.getSorgcode());
						subPara.setIvousrlno(_dto.getIvousrlno());
						subPara.setSpackageticketno(_dto.getSpackageticketno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						for(HtvGrantpaymsgsubDto sub : (List<HtvGrantpaymsgsubDto>)sublist){
							filebuf.append(sub.getSfunsubjectcode());//���ܿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getSecosubjectcode()==null?"":sub.getSecosubjectcode());//���ÿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getNmoney());//��������
							filebuf.append(splitSign);
							filebuf.append("0.00");//С���ֽ�����
							filebuf.append("\r\n");
						}
					}
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
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