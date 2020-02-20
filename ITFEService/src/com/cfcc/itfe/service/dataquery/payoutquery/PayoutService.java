package com.cfcc.itfe.service.dataquery.payoutquery;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.IServiceManagerServer;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayOutReportDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time   10-02-04 11:39:17
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class PayoutService extends AbstractPayoutService {
	
	private static Log log = LogFactory.getLog(PayoutService.class);	
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;
	/**
	 * ��ҳ��ѯʵ���ʽ�����Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findMainByPage(TvPayoutmsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			
			// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
			String wherestr = PublicSearchFacade.changeFileName(null, mainDto.getSfilename());
			mainDto.setSfilename(null);
			if(mainDto.getSaddword()!=null)
			{
				if(wherestr==null||"".equals(wherestr))
					wherestr = mainDto.getSaddword();
				else
					wherestr = wherestr+" and "+ mainDto.getSaddword();
				mainDto.setSaddword(null);
			}
			wherestr = CommonUtil.getFuncCodeSql(wherestr, mainDto, expfunccode, payamt);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, wherestr, " S_ORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
		}
    }

	/**
	 * ��ҳ��ѯʵ���ʽ�����Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findSubByPage(TvPayoutmsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
    	if (null == mainDto || null == mainDto.getSorgcode() || null == mainDto.getSbizno()) {
			return null;
		}

    	TvPayoutmsgsubDto subdto = new TvPayoutmsgsubDto();
		subdto.setSbizno(mainDto.getSbizno());
		if(expfunccode!=null && !"".equals(expfunccode)){
			subdto.setSfunsubjectcode(expfunccode);
		}
		if(payamt!=null && !"".equals(payamt)){
			subdto.setNmoney(BigDecimal.valueOf(Long.valueOf(payamt)));
		}
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " S_SEQNO");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ�����Ϣʱ����!", e);
		}
    }

	/**
	 * �ط�TIPSû���յ��ı���	 
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
		
		String smsgno = MsgConstant.MSG_NO_5101;

		// ȡ�ö�Ӧ�ı��Ĵ�����
		IServiceManagerServer iservice = (IServiceManagerServer) ContextFactory.getApplicationContext().getBean(
				MsgConstant.SPRING_SERVICE_SERVER + smsgno);

		iservice.sendMsg(sfilename, sorgcode, spackageno, smsgno+"_OUT", scommitdate, null,true);

		return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param updateDto
	 * @throws ITFEBizException	 
	 */
    public void saveInfo(TvPayoutmsgmainDto updateDto) throws ITFEBizException
    {
		//���������кͿ�����
		String updateSql = " update TV_PAYOUTMSGMAIN "
				+ " set S_PAYEEBANKNO = ? , S_RECBANKNO = ?"
				+ " where S_BIZNO = ?";

		SQLExecutor updatesqlExec = null;
		try {
			//���������кͿ�����
			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(updateDto.getSpayeebankno());
			updatesqlExec.addParam(updateDto.getSrecbankno());
			updatesqlExec.addParam(updateDto.getSbizno());
			updatesqlExec.runQuery(updateSql);
			
		} catch (JAFDatabaseException e) {
			log.error(" ʵ���ʽ�ҵ����Ϣ������µ�ʱ������쳣!", e);
			throw new ITFEBizException("ʵ���ʽ�ҵ����Ϣ������µ�ʱ������쳣!", e);
		}finally {
			if (updatesqlExec != null)
				updatesqlExec.closeConnection();
		}
    }

	/**
	 * ȡ�÷��Ͱ�����Ϣ	 
	 * @generated
	 * @param sorgcode
	 * @param strecode
	 * @param sfilename
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List getsendmsg(String sorgcode, String strecode, String sfilename)
			throws ITFEBizException {
		TvFilepackagerefDto filepack = new TvFilepackagerefDto();
		List<TvFilepackagerefDto> packlist = new ArrayList<TvFilepackagerefDto>();
		String selectSQL = "select S_PACKAGENO,S_COMMITDATE from TV_FILEPACKAGEREF where "
				+ "S_ORGCODE = ? and S_TRECODE = ?  and S_FILENAME = ? and S_RETCODE = ? and S_OPERATIONTYPECODE = ?";

		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(sorgcode);
			sqlExec.addParam(strecode);
			sqlExec.addParam(sfilename);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);
			sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAY_OUT);
			SQLResults sqlRs = sqlExec.runQueryCloseCon(selectSQL);
			
			filepack.setSorgcode(sorgcode);
			filepack.setStrecode(strecode);
			filepack.setSfilename(sfilename);
			int count = sqlRs.getRowCount();
			TvFilepackagerefDto filepackdto = new TvFilepackagerefDto();
			for (int i = 0; i < count; i++) {
				filepackdto = (TvFilepackagerefDto)filepack.clone();
				filepackdto.setSpackageno(sqlRs.getString(i, 0));
				filepackdto.setScommitdate(sqlRs.getString(i, 1));
				packlist.add(filepackdto);
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ����ͷ��ʱ������쳣��", e);
			throw new ITFEBizException("��ѯ����ͷ��ʱ������쳣��", e);
		}
		return packlist;
	}

	/**
	 * ȡ�÷���ͷ�Ĺ��������ļ������б�	 
	 * @generated
	 * @param sorgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List sendfilelist(String sorgcode) throws ITFEBizException {
		TvFilepackagerefDto filepack = new TvFilepackagerefDto();
		List<TvFilepackagerefDto> packlist = new ArrayList<TvFilepackagerefDto>();
		String selectSQL = "select S_TRECODE,S_FILENAME from TV_FILEPACKAGEREF where "
				+ "S_ORGCODE = ? and S_RETCODE = ? and S_OPERATIONTYPECODE = ? group by S_TRECODE,S_FILENAME";

		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(sorgcode);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);
			sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAY_OUT);
			SQLResults sqlRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = sqlRs.getRowCount();
			TvFilepackagerefDto filepackdto = new TvFilepackagerefDto();
			for (int i = 0; i < count; i++) {
				filepackdto = (TvFilepackagerefDto)filepack.clone();
				filepackdto.setStrecode(sqlRs.getString(i, 0));
				filepackdto.setSfilename(sqlRs.getString(i, 1));
				packlist.add(filepackdto);
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ����ͷ��ʱ������쳣��", e);
			throw new ITFEBizException("��ѯ����ͷ��ʱ������쳣��", e);
		}
		return packlist;
	}

	public PageResponse findMainByPageForHis(HtvPayoutmsgmainDto mainDto,
			PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
		try {
			// ȡ�ò���Ա�Ļ�������
			String orgcode = this.getLoginInfo().getSorgcode();
			mainDto.setSorgcode(orgcode);
			
			// ȡ��Ʊ�ݱ�� - ֧��ģ����ѯ
			String wherestr = PublicSearchFacade.changeFileName(null, mainDto.getSfilename());
			mainDto.setSfilename(null);
			if(mainDto.getSaddword()!=null)
			{
				if(wherestr==null||"".equals(wherestr))
					wherestr = mainDto.getSaddword();
				else
					wherestr = wherestr+" and "+ mainDto.getSaddword();
				mainDto.setSaddword(null);
			}
			wherestr = CommonUtil.getFuncCodeSql(wherestr, mainDto, expfunccode, payamt);
			return CommonFacade.getODB().findRsByDtoPaging(mainDto, pageRequest, wherestr, " S_ORGCODE,S_TRECODE ");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
		}
	}

	public PageResponse findSubByPageForHis(HtvPayoutmsgmainDto mainDto,
			PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException {
		if (null == mainDto || null == mainDto.getSorgcode() || null == mainDto.getSbizno()) {
			return null;
		}

    	HtvPayoutmsgsubDto subdto = new HtvPayoutmsgsubDto();
		subdto.setSbizno(mainDto.getSbizno());
		if(expfunccode!=null && !"".equals(expfunccode)){
			subdto.setSfunsubjectcode(expfunccode);
		}
		if(payamt!=null && !"".equals(payamt)){
			subdto.setNmoney(BigDecimal.valueOf(Long.valueOf(payamt)));
		}
		try {
			return CommonFacade.getODB().findRsByDtoPaging(subdto, pageRequest, null, " S_SEQNO");
		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯʵ���ʽ���ʷ����Ϣʱ����!", e);
		}
	}

	public List findPayOutByPrint(IDto finddto, String selectedtable)
			throws ITFEBizException {
		SQLExecutor sqlExecutor=null;
		SQLResults rs = null;
		List list = new ArrayList();
		try{
			TvPayoutmsgmainDto dto=(TvPayoutmsgmainDto)finddto;
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT pmain.S_ORGCODE AS O_RGAN,pmain.S_TRECODE AS T_REASURY,pmain.S_FILENAME,";
			sql+="pmain.S_BIZNO,pmain.S_PAYERACCT,pmain.S_PAYERNAME,pmain.S_RECACCT,pmain.S_RECNAME,";
			sql+="evalue2.S_VALUECMT AS S_STATUS,pmain.N_MONEY,organ.S_ORGNAME AS S_ORGNAME,tsury.S_TRENAME AS S_TRENAME ";
			if(selectedtable.equals("0")){
				sql+=" FROM TV_PAYOUTMSGMAIN pmain ";
			}else if(selectedtable.equals("1")){
				sql+=" FROM HTV_PAYOUTMSGMAIN pmain";
			}
			sql+=" LEFT JOIN TS_ORGAN organ ON organ.S_ORGCODE=pmain.S_ORGCODE ";
			sql+=" LEFT JOIN TS_TREASURY tsury ON tsury.S_TRECODE=pmain.S_TRECODE AND tsury.S_ORGCODE='"+this.getLoginInfo().getSorgcode()+"' ";
			sql+=" LEFT JOIN TD_ENUMVALUE evalue2 on pmain.S_STATUS=evalue2.S_VALUE AND evalue2.S_TYPECODE='0412'";
			sql+=" where 1=1 ";
			if(dto.getStrecode()!=null && !dto.getStrecode().equals("")){
				sql+=" AND pmain.S_TRECODE='"+dto.getStrecode()+"'";
			}
			if(dto.getSaddword()!=null&&!"".equals(dto.getSaddword()))
			{
				sql+=" and "+StringUtil.replace(dto.getSaddword(), "s_commitdate", "pmain.s_commitdate");
				dto.setSaddword(null);
			}
//			if(dto.getScommitdate()!=null && !dto.getScommitdate().equals("")){
//				sql+=" AND pmain.S_COMMITDATE='"+dto.getScommitdate()+"'";
//			}
			if(dto.getSpackageno()!=null&&!dto.getSpackageno().equals("")){
				sql+=" AND pmain.S_PACKAGENO='"+dto.getSpackageno()+"' ";
			}
			if(dto.getSpayunit()!=null&&!dto.getSpayunit().equals("")){
				sql+=" AND pmain.S_PAYUNIT='"+dto.getSpayunit()+"' ";
			}
			if(dto.getSpayeebankno()!=null&&!dto.getSpayeebankno().equals("")){
				sql+=" AND pmain.S_PAYEEBANKNO='"+dto.getSpayeebankno()+"' ";
			}
			if(dto.getSdealno()!=null&&!dto.getSdealno().equals("")){
				sql+=" AND pmain.S_DEALNO='"+dto.getSdealno()+"' ";
			}
			if(dto.getStaxticketno()!=null&&!dto.getStaxticketno().equals("")){
				sql+=" AND pmain.S_TAXTICKETNO='"+dto.getStaxticketno()+"' ";
			}
			if(dto.getSgenticketdate()!=null&&!dto.getSgenticketdate().equals("")){
				sql+=" AND pmain.S_GENTICKETDATE='"+dto.getSgenticketdate()+"' ";
			}
			if(dto.getNmoney()!=null&&!dto.getNmoney().equals("")){
				sql+=" AND pmain.N_MONEY='"+dto.getNmoney()+"' ";
			}
			if(dto.getSpayeracct()!=null&&!dto.getSpayeracct().equals("")){
				sql+=" AND pmain.S_PAYERACCT='"+dto.getSpayeracct()+"' ";
			}
			
			if(dto.getSrecacct()!=null&&!dto.getSrecacct().equals("")){
				sql+=" AND pmain.S_RECACCT='"+dto.getSrecacct()+"' ";
			}
			if(dto.getSrecbankno()!=null&&!dto.getSrecbankno().equals("")){
				sql+=" AND pmain.S_RECBANKNO='"+dto.getSrecbankno()+"' ";
			}
			if(dto.getSbudgetunitcode()!=null&&!dto.getSbudgetunitcode().equals("")){
				sql+=" AND pmain.S_BUDGETUNITCODE='"+dto.getSbudgetunitcode()+"' ";
			}
			if(dto.getSofyear()!=null&&!dto.getSofyear().equals("")){
				sql+=" AND pmain.S_OFYEAR='"+dto.getSofyear()+"' ";
			}
			if(dto.getSstatus()!=null&&!dto.getSstatus().equals("")){
				sql+=" AND pmain.S_STATUS='"+dto.getSstatus()+"' ";
			}
			if(dto.getStrimflag()!=null&&!dto.getStrimflag().equals("")){
				sql+=" AND pmain.S_TRIMFLAG='"+dto.getStrimflag()+"' ";
			}
			if(dto.getSbudgettype()!=null&&!dto.getSbudgettype().equals("")){
				sql+=" AND pmain.S_BUDGETTYPE='"+dto.getSbudgettype()+"' ";
			}
			if(dto.getSfilename()!=null&&!dto.getSfilename().equals("")){
				sql+=" AND pmain.S_FILENAME='"+dto.getSfilename()+"' ";
			}
			if(dto.getSorgcode()!=null&&!dto.getSorgcode().equals("")){
				sql+=" AND pmain.S_ORGCODE='"+dto.getSorgcode()+"' ";
			}
			sql+=" ORDER BY S_FILENAME ";
			rs = sqlExecutor.runQueryCloseCon(sql,TvPayOutReportDto.class);
		}catch(JAFDatabaseException ex){
			log.error("��ѯʵ���ʽ�ҵ������ʱ�����쳣!",ex);
			throw new ITFEBizException("��ѯʵ���ʽ�ҵ������ʱ�����쳣!",ex);
		}
		list.addAll(rs.getDtoCollection());
		return list;
	}

	public String dataexport(IDto finddto, String selectedtable)
			throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		String filename="";
		startdate = TimeFacade.getCurrentStringTime();
		if(startdate.equals(enddate))
			pici++;
		else
			enddate = TimeFacade.getCurrentStringTime();
		try {
			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			if(selectedtable.equals("0")){
				TvPayoutmsgmainDto mdto=(TvPayoutmsgmainDto)finddto;
				mdto.setSorgcode(orgcode);
//				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				String wherestr=null;
				if(mdto.getSaddword()!=null)
				{
					wherestr = " and "+ mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,wherestr);
				filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAY_OUT+(pici<10?"0"+pici:pici)+"hd.txt";
			}else if(selectedtable.equals("1")){
				HtvPayoutmsgmainDto mdto=(HtvPayoutmsgmainDto)finddto;
//				mdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				mdto.setSorgcode(orgcode);
				String wherestr=null;
				if(mdto.getSaddword()!=null)
				{
					wherestr = " and "+ mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,wherestr);
				filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAY_OUT+(pici<10?"0"+pici:pici)+"hd.txt";
			}
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
					for (TvPayoutmsgmainDto _dto :(List<TvPayoutmsgmainDto>) list) {
						TvPayoutmsgsubDto subPara = new TvPayoutmsgsubDto();
						subPara.setSbizno(_dto.getSbizno());
						List l = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						TvPayoutmsgsubDto sub = (TvPayoutmsgsubDto)l.get(0);						
						
						filebuf.append(_dto.getScommitdate());//ί������
						filebuf.append(splitSign);
//						filebuf.append(index++);//֧���������
//						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//���
						filebuf.append(splitSign);
						String bankno = _dto.getSrecbankno();//�����к�
						String bankname = bankMap.get(bankno).getSbankname();//��������
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecacct());//�տ����˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecname());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//����������
						filebuf.append(splitSign);
						filebuf.append(sub.getSfunsubjectcode()); //���ܿ�Ŀ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSaddword());//����
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdemo()+splitSign);//ԭ��
						if("1".equals(_dto.getSbackflag()))
						{
							filebuf.append("ʵ���ʽ��˿�"+splitSign);
							filebuf.append(_dto.getShold2());
						}else
						{
							filebuf.append("ʵ���ʽ�");
						}
						filebuf.append("\r\n");
					}
				}else if(selectedtable.equals("1")){
					for (HtvPayoutmsgmainDto _dto :(List<HtvPayoutmsgmainDto>) list) {
						HtvPayoutmsgsubDto subPara = new HtvPayoutmsgsubDto();
						subPara.setSbizno(_dto.getSbizno());
						List l = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						TvPayoutmsgsubDto sub = (TvPayoutmsgsubDto)l.get(0);
						
						filebuf.append(_dto.getScommitdate());//ί������
						filebuf.append(splitSign);
//						filebuf.append(index++);//֧���������
//						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//���
						filebuf.append(splitSign);
						String bankno = _dto.getSrecbankno();//�����к�
						String bankname = bankMap.get(bankno).getSbankname();//��������
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecacct());//�տ����˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecname());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//����������
						filebuf.append(splitSign);
						filebuf.append(sub.getSfunsubjectcode()); //���ܿ�Ŀ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSaddword());//����
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdemo()+splitSign);//ԭ��
						if("1".equals(_dto.getSbackflag()))
						{
							filebuf.append("ʵ���ʽ��˿�"+splitSign);
							filebuf.append(_dto.getShold2());
						}else
						{
							filebuf.append("ʵ���ʽ�");
						}
						filebuf.append("\r\n");
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

	public List findPayOutByPrint(TvPayoutmsgmainDto finddto)
			throws ITFEBizException {
		return null;
	}
	
	/**
	 * ����ѡ�лص�	 
	 * @generated
	 * @param selectedtable
	 * @param selectedlist
	 * @param filePath
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public String exportSelectData(String selectedtable, List selectedlist, String filePath) throws ITFEBizException{
	// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		String filename="";
		startdate = TimeFacade.getCurrentStringTime();
		if(startdate.equals(enddate))
			pici++;
		else
			enddate = TimeFacade.getCurrentStringTime();
		try {
			HashMap<String, TsPaybankDto> bankMap = SrvCacheFacade.cachePayBankInfo();
			if(selectedtable.equals("0")){
				filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAY_OUT+(pici<10?"0"+pici:pici)+"hd.txt";
			}else if(selectedtable.equals("1")){
				filename=TimeFacade.getCurrentStringTime()+BizTypeConstant.BIZ_TYPE_PAY_OUT+(pici<10?"0"+pici:pici)+"hd.txt";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = filePath + dirsep + filename;
			String splitSign = ",";//"\t"; // �ļ���¼�ָ�����
			if (selectedlist.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
//				int index=1;
				if(selectedtable.equals("0")){
					for (TvPayoutmsgmainDto _dto :(List<TvPayoutmsgmainDto>) selectedlist) {
						TvPayoutmsgsubDto subPara = new TvPayoutmsgsubDto();
						subPara.setSbizno(_dto.getSbizno());
						List l = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						TvPayoutmsgsubDto sub = (TvPayoutmsgsubDto)l.get(0);						
						
						filebuf.append(_dto.getScommitdate());//ί������
						filebuf.append(splitSign);
//						filebuf.append(index++);//֧���������
//						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//���
						filebuf.append(splitSign);
						String bankno = _dto.getSrecbankno();//�����к�
						String bankname = bankMap.get(bankno).getSbankname();//��������
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecacct());//�տ����˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecname());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//����������
						filebuf.append(splitSign);
						filebuf.append(sub.getSfunsubjectcode()); //���ܿ�Ŀ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSaddword());//����
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdemo()+splitSign);//ԭ��
						if("1".equals(_dto.getSbackflag()))
						{
							filebuf.append("ʵ���ʽ��˿�"+splitSign);
							filebuf.append(_dto.getShold2());
						}else
						{
							filebuf.append("ʵ���ʽ�");
						}
						filebuf.append("\r\n");
					}
				}else if(selectedtable.equals("1")){
					for (HtvPayoutmsgmainDto _dto :(List<HtvPayoutmsgmainDto>) selectedlist) {
						HtvPayoutmsgsubDto subPara = new HtvPayoutmsgsubDto();
						subPara.setSbizno(_dto.getSbizno());
						List l = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						TvPayoutmsgsubDto sub = (TvPayoutmsgsubDto)l.get(0);
						
						filebuf.append(_dto.getScommitdate());//ί������
						filebuf.append(splitSign);
//						filebuf.append(index++);//֧���������
//						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//���
						filebuf.append(splitSign);
						String bankno = _dto.getSrecbankno();//�����к�
						String bankname = bankMap.get(bankno).getSbankname();//��������
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecacct());//�տ����˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecname());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//����������
						filebuf.append(splitSign);
						filebuf.append(sub.getSfunsubjectcode()); //���ܿ�Ŀ����
						filebuf.append(splitSign);
						filebuf.append(_dto.getSaddword());//����
						filebuf.append(splitSign);
						if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto.getSstatus()))
							filebuf.append("1");//������1�ɹ���0ʧ��
						else
							filebuf.append("0");//������1�ɹ���0ʧ��
						filebuf.append(splitSign);
						filebuf.append(_dto.getSdemo()+splitSign);//ԭ��
						if("1".equals(_dto.getSbackflag()))
						{
							filebuf.append("ʵ���ʽ��˿�"+splitSign);
							filebuf.append(_dto.getShold2());
						}else
						{
							filebuf.append("ʵ���ʽ�");
						}
						filebuf.append("\r\n");
					}
				}
			
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
			    return fullpath;
				
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

	public String exportfile(IDto finddto, String selectedtable)throws ITFEBizException {
		// ȡ�ò���Ա�Ļ�������
		String orgcode = this.getLoginInfo().getSorgcode();
		List list=new ArrayList();
		try {
			HashMap<String, TsPaybankDto> bankMap = null;//SrvCacheFacade.cachePayBankInfo();
			if(selectedtable.equals("0")){
				TvPayoutmsgmainDto mdto=(TvPayoutmsgmainDto)finddto;
				mdto.setSorgcode(orgcode);
				String wherestr=null;
				if(mdto.getSaddword()!=null)
				{
					wherestr = " and "+ mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,wherestr);
			}else if(selectedtable.equals("1")){
				TvPayoutmsgmainDto mdto=(TvPayoutmsgmainDto)finddto;
				HtvPayoutmsgmainDto hdto= new HtvPayoutmsgmainDto();
				hdto.setSorgcode(mdto.getSorgcode());
				hdto.setStrecode(mdto.getStrecode());
				hdto.setSpackageno(mdto.getSpackageno());
				hdto.setScommitdate(mdto.getScommitdate());
				hdto.setSdealno(mdto.getSdealno());
				hdto.setSpayunit(mdto.getSpayunit());
				hdto.setSgenticketdate(mdto.getSgenticketdate());
				hdto.setSbudgettype(mdto.getSbudgettype());
				hdto.setStaxticketno(mdto.getStaxticketno());
				hdto.setSstatus(mdto.getSstatus());
				hdto.setStrimflag(mdto.getStrimflag());
				hdto.setNmoney(mdto.getNmoney());
				hdto.setSpayeebankno(mdto.getSpayeebankno());
				hdto.setSpayeracct(mdto.getSpayeracct());
				hdto.setSrecacct(mdto.getSrecacct());
				hdto.setSbudgetunitcode(mdto.getSbudgetunitcode());
				mdto.setSorgcode(orgcode);
				String wherestr=null;
				if(mdto.getSaddword()!=null)
				{
					wherestr = " and "+ mdto.getSaddword();
					mdto.setSaddword(null);
				}
				list=CommonFacade.getODB().findRsByDtoForWhere(hdto,wherestr);
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = "";
			if(ITFECommonConstant.SRC_NODE.equals("202100000010")){
				fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+"17.txt";
			}else{
				fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+"17.csv";
			}
			
			String splitSign = ",";//"\t"; // �ļ���¼�ָ�����
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
				if(selectedtable.equals("0")){
					for (TvPayoutmsgmainDto _dto :(List<TvPayoutmsgmainDto>) list) {
						filebuf.append(_dto.getScommitdate());//ί������
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//���
						filebuf.append(splitSign);
						String bankno = _dto.getSrecbankno();//�����к�
						String bankname = "";
						if(bankMap != null){
							bankname = bankMap.get(bankno)==null ? "": bankMap.get(bankno).getSbankname();//��������
						}
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecacct());//�տ����˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecname());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//����������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSaddword()+splitSign);//����
						if("1".equals(_dto.getSbackflag()))
						{
							filebuf.append("ʵ���ʽ��˿�"+splitSign);
							filebuf.append(_dto.getShold2());
						}else
						{
							filebuf.append("ʵ���ʽ�");
						}
						if(ITFECommonConstant.SRC_NODE.equals("202100000010")){
							filebuf.append(";");
						}else{
							filebuf.append("\r\n");
						}
						
						//���������ȡ��ǰ�ӱ���Ϣ
						TvPayoutmsgsubDto subPara = new TvPayoutmsgsubDto();
						subPara.setSbizno(_dto.getSbizno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						for(TvPayoutmsgsubDto sub : (List<TvPayoutmsgsubDto>)sublist){
							filebuf.append(sub.getSfunsubjectcode());//���ܿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getSecnomicsubjectcode()==null?"":sub.getSecnomicsubjectcode());//���ÿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getNmoney());//������
							if(ITFECommonConstant.SRC_NODE.equals("202100000010")){
								filebuf.append(";");
							}else{
								filebuf.append("\r\n");
							}
						}
						if(ITFECommonConstant.SRC_NODE.equals("202100000010")){
							filebuf.append("\r\n");
						}
					}
				}else if(selectedtable.equals("1")){
					for (HtvPayoutmsgmainDto _dto :(List<HtvPayoutmsgmainDto>) list) {
						filebuf.append(_dto.getScommitdate());//ί������
						filebuf.append(splitSign);
						filebuf.append(_dto.getStaxticketno());//ƾ֤���
						filebuf.append(splitSign);
						filebuf.append(_dto.getNmoney());//���
						filebuf.append(splitSign);
						String bankno = _dto.getSrecbankno();//�����к�
						String bankname = "";
						if(bankMap != null){
							bankname = bankMap.get(bankno)==null ? "" : bankMap.get(bankno).getSbankname();//��������
						}
						filebuf.append(bankno);
						filebuf.append(splitSign);
						filebuf.append(bankname);
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecacct());//�տ����˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSrecname());//�տ�������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayeracct());//�������˺�
						filebuf.append(splitSign);
						filebuf.append(_dto.getSpayername());//����������
						filebuf.append(splitSign);
						filebuf.append(_dto.getSaddword()+splitSign);//����
						if("1".equals(_dto.getSbackflag()))
						{
							filebuf.append("ʵ���ʽ��˿�"+splitSign);
							filebuf.append(_dto.getShold2());
						}else
						{
							filebuf.append("ʵ���ʽ�");
						}
						if(ITFECommonConstant.SRC_NODE.equals("")){
							filebuf.append(";");
						}else{
							filebuf.append("\r\n");
						}
						HtvPayoutmsgsubDto subPara = new HtvPayoutmsgsubDto();
						subPara.setSbizno(_dto.getSbizno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
						for(HtvPayoutmsgsubDto sub : (List<HtvPayoutmsgsubDto>)sublist){
							filebuf.append(sub.getSfunsubjectcode());//���ܿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getSecnomicsubjectcode()==null?"":sub.getSecnomicsubjectcode());//���ÿ�Ŀ����
							filebuf.append(splitSign);
							filebuf.append(sub.getNmoney());//������
							if(ITFECommonConstant.SRC_NODE.equals("")){
								filebuf.append(";");
							}else{
								filebuf.append("\r\n");
							}
						}
						if(ITFECommonConstant.SRC_NODE.equals("")){
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