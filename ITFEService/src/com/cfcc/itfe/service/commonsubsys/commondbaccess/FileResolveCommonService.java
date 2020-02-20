package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpaySubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.common.DiffDecryptUtil;
import com.cfcc.itfe.tipsfileparser.ITipsFileOper;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;



/**
 * @author hua
 * @time 12-02-23 09:57:50 codecomment: �����ļ���������
 */

public class FileResolveCommonService extends AbstractFileResolveCommonService {
	private static Log log = LogFactory.getLog(FileResolveCommonService.class);

	/**
	 * �����ļ�
	 * 
	 * @generated
	 * @param filelist
	 * @param biztype
	 * @param filekind
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public MulitTableDto loadFile(List filelist, String biztype,
			String filekind, IDto paramdto) throws ITFEBizException {
		
		IDto  idto = null ;
		

		// 1.���ȵõ������ļ������������ʵ��
		ITipsFileOper fileOper = null;
		if(ITFECommonConstant.ISMATCHBANKNAME.equals("1") && BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(biztype)){
			fileOper = (ITipsFileOper) ContextFactory
			.getApplicationContext().getBean(
					MsgConstant.SPRING_FILEPRA_SERVER + "5101_SH");
		}else{
			fileOper = (ITipsFileOper) ContextFactory
				.getApplicationContext().getBean(
						MsgConstant.SPRING_FILEPRA_SERVER + filekind);
		}

		// 2.���ļ����н���
		MulitTableDto mudto = null;
		// ��Ŵ�����Ϣ
		List<String> errorList = new ArrayList<String>();

		// ��Ŵ����ļ�����
		int errorFileCount = 0;
		
		// ��Ŵ����ļ�����
		List<String> errFileNameList = new ArrayList<String>();

		// �������Žӿ�
		MulitTableDto allmudto = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());

		// ���ڶ�ȶ��ļ����
		MulitTableDto allPaydto = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());
		
		// ����ʵ���ʽ��ļ����(��Ҫ�����ж϶��ļ�֮��ƾ֤���)
		MulitTableDto allPay17dto = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());
		
		MulitTableDto allHksqdtos = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());

		MulitTableDto allmudtoforxm = new MulitTableDto();

		List<MulitTableDto> l=new ArrayList<MulitTableDto>();
		List<IDto> li = new ArrayList<IDto>();

		// �õ��ļ��ϴ�����
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
				.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");

		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();

		// �õ���Ŀ
		Map<String, TsBudgetsubjectDto> smap = this
				.makeSubjectMap(getLoginInfo().getSorgcode());

		StringBuffer sb; // ���÷������ϴ���·��

		for (Object obj : filelist) {

			// ��ǰ̨���������·�����ϸ�·��
			sb = new StringBuffer(root);

			sb.append(obj.toString().replace("/", File.separator).replace("\\",
					File.separator));

			String file = sb.toString();

			String bztype="";
			File fl = new File(file);
			//��ȡ�ļ���ҵ������
			if(biztype.equals("")){
				String tmpfilename_new = fl.getName().trim().toLowerCase();
				String tmpfilename = tmpfilename_new.replace(".txt", "");
				if (tmpfilename.length() == 13 || tmpfilename.length() == 15) {
					// 8λ���ڣ�2λ���κţ�2λҵ�����ͣ�1λ�����ڱ�־���
					if (tmpfilename.length() == 13) {
						bztype=tmpfilename.substring(10, 12); // ҵ������
					} else {
						bztype=tmpfilename.substring(12, 14); // ҵ������
					}
				}
			}
			
			//���������˻أ���Ҫ���������� ����һ���������
			 if (bztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)|| bztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				 idto=paramdto;
				 paramdto=null;
			 }
			// �޸Ĵ���Ĳ�������ǰ̨�����null��param��ֵ�����ǰ̨���벻��null�����ٸ�ֵ�����֧���Ĵ������չ���������Կ���н���
			if (null == paramdto) {
				paramdto = new CommonParamDto();
				((CommonParamDto) paramdto).setArea(getLoginInfo().getArea());
				((CommonParamDto) paramdto).setKeyMode(getLoginInfo().getMankeyMode());
				if(biztype.equals("")){
					((CommonParamDto) paramdto).setEncryptMode(getLoginInfo().getEncryptMode().get(bztype));
				}else{
					((CommonParamDto) paramdto).setEncryptMode(getLoginInfo().getEncryptMode().get(biztype));
				}
			}
			//��ʱ�洢�ļ�·��
			String tmpFile = file;
			// ���ݵ�½��Ϣ�е� ���ܷ�ʽ����Կ����ģʽ�����ý��ܶ�Ӧ���ļ����޸�����20120629
			ITFELoginInfo loginfo = getLoginInfo();
			try {
				String bizName="";
				if(biztype.equals("")){
					file = DiffDecryptUtil.commonDecrypt(loginfo, file, bztype);
					 bizName = getBizname(bztype);
				}else {
					file = DiffDecryptUtil.commonDecrypt(loginfo, file, biztype);
					 bizName = getBizname(biztype);
				}
				
				if(StateConstant.ENCRYPT_FAIL_INFO.equals(file)) { //���ܻ���ǩʧ��
					errorList.add(bizName+" ["+fl.getName()+"] "+StateConstant.ENCRYPT_FAIL_INFO);
					errFileNameList.add(obj.toString());
					errorFileCount++;
					this.deleteFileOnServer(file);
					continue;
				} else if(StateConstant.ENCRYPT_FIAL_INFO_NOKEY.equals(file)) {
					errorList.add(bizName+" ["+fl.getName()+"] "+StateConstant.ENCRYPT_FIAL_INFO_NOKEY);
					errFileNameList.add(obj.toString());
					errorFileCount++;
					this.deleteFileOnServer(file);
					continue;
				}
	            if (bztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)|| bztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
	            	mudto = fileOper.fileParser(file, getLoginInfo().getSorgcode(),
							getLoginInfo().getSuserCode(), bztype,
							ITFECommonConstant.IFNEWINTERFACE, paramdto, smap,idto);
				}else if(biztype.equals(BizTypeConstant.BIZ_TYPE_TAXORG_PAYOUT)){
					mudto = fileOper.fileParser(file, getLoginInfo().getSorgcode(),
							getLoginInfo().getSuserCode(), bztype,
							ITFECommonConstant.IFNEWINTERFACE, paramdto, smap);
				}else if(biztype.equals(BizTypeConstant.BIZ_TYPE_JZZF)){
					mudto = fileOper.fileParser(file, getLoginInfo().getSorgcode(),
							getLoginInfo().getSuserCode(), biztype,
							ITFECommonConstant.IFNEWINTERFACE, paramdto, smap);
				}else if(biztype.equals("")){
					mudto = fileOper.fileParser(file, getLoginInfo().getSorgcode(),
							getLoginInfo().getSuserCode(), bztype,
							ITFECommonConstant.IFNEWINTERFACE, paramdto, smap);
				}else{
					mudto = fileOper.fileParser(file, getLoginInfo().getSorgcode(),
							getLoginInfo().getSuserCode(), biztype,
							ITFECommonConstant.IFNEWINTERFACE, paramdto, smap);
				}
			}catch(ArrayIndexOutOfBoundsException e)
			{
				log.error(fl.getName()+"�ļ����� :\n"+e.getMessage());
				throw new ITFEBizException(fl.getName()+"�ļ����� :\n",e);
			}catch(FileOperateException e)
			{
				log.error(fl.getName()+"�ļ����� :\n"+e.getMessage());
				throw new ITFEBizException(fl.getName()+"�ļ����� :\n",e);
			}catch(ITFEBizException e)
			{
				log.error(fl.getName()+"�ļ����� :\n"+e.getMessage());
				throw new ITFEBizException(fl.getName()+"�ļ����� :\n"+e.getMessage(),e);
			}catch (Exception e2) {
				log.error(e2);
				throw new ITFEBizException("�������˽����ļ�����1��", e2);
			}
			if (mudto.getErrorList() != null && mudto.getErrorList().size() > 0) {
				errorList.addAll(mudto.getErrorList());
				errFileNameList.add(obj.toString());
				errorFileCount++;
				//ɾ�������ļ�
				this.deleteFileOnServer(file);
				continue;
			}
			if(mudto.getFatherDtos()!=null&&mudto.getFatherDtos().size()>0)
			{
					IDto dateDto = mudto.getFatherDtos().get(0);
					Date vDate = Date.valueOf((DateUtil.dateBefore(TimeFacade.getCurrentDateTime(), 20, "D")));//YYYY-MM-DD
					Date voudate = null;
					String svouno = null;
					if(dateDto instanceof TbsTvPbcpayDto)//mainDto.setDvoucher(CommonUtil.strToDate(strs[12])); //ƾ֤����
					{
						voudate = ((TbsTvPbcpayDto)dateDto).getDvoucher();
						svouno = ((TbsTvPbcpayDto)dateDto).getSvouno();//ƾ֤���
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20��ǰ���ļ����ܵ���ϵͳ!");
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",hn=checkvouno,"))
						{
							TvPbcpayMainDto sdto = new TvPbcpayMainDto();
							sdto.setSorgcode(((TbsTvPbcpayDto)dateDto).getSbookorgcode());
							sdto.setStrecode(((TbsTvPbcpayDto)dateDto).getStrecode());
							sdto.setSvouno(svouno);
							try {
								List searchlist = null;
								searchlist = CommonFacade.getODB().findRsByDto(sdto);
								if(searchlist!=null&&searchlist.size()>0)
								{
									throw new ITFEBizException("ƾ֤��Ϊ"+svouno+"������ϵͳ�Ѿ��ɹ������!");
								}
							} catch (Exception e) {
							}
						}						
					}else if(dateDto instanceof TbsTvPayoutDto)
					{
						voudate = ((TbsTvPayoutDto)dateDto).getDvoucher();
						svouno = ((TbsTvPayoutDto)dateDto).getSvouno();
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20��ǰ���ļ����ܵ���ϵͳ!");
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",hn=checkvouno,"))
						{
							TvPayoutmsgmainDto sdto = new TvPayoutmsgmainDto();
							sdto.setSorgcode(((TbsTvPayoutDto)dateDto).getSbookorgcode());
							sdto.setStrecode(((TbsTvPayoutDto)dateDto).getStrecode());
							sdto.setStaxticketno(svouno);
							try {
								List searchlist = null;
								searchlist = CommonFacade.getODB().findRsByDto(sdto);
								if(searchlist!=null&&searchlist.size()>0)
								{
									throw new ITFEBizException("ƾ֤��Ϊ"+svouno+"������ϵͳ�Ѿ��ɹ������!");
								}
							} catch (Exception e) {
							}
						}
					}
					else if(dateDto instanceof TbsTvDwbkDto)
					{
						voudate = ((TbsTvDwbkDto)dateDto).getDvoucher();
						svouno = ((TbsTvDwbkDto)dateDto).getSdwbkvoucode();
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20��ǰ���ļ����ܵ���ϵͳ!");
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",hn=checkvouno,"))
						{
							TvDwbkDto sdto = new TvDwbkDto();
							sdto.setSbookorgcode(((TbsTvDwbkDto)dateDto).getSbookorgcode());
							sdto.setSaimtrecode(((TbsTvDwbkDto)dateDto).getSaimtrecode());
							sdto.setSelecvouno(svouno);
							try {
								List searchlist = null;
								searchlist = CommonFacade.getODB().findRsByDto(sdto);
								if(searchlist!=null&&searchlist.size()>0)
								{
									throw new ITFEBizException("ƾ֤��Ϊ"+svouno+"������ϵͳ�Ѿ��ɹ������!");
								}
							} catch (Exception e) {
							}
						}
					}
					else if(dateDto instanceof TbsTvDirectpayplanMainDto)
					{
						voudate = ((TbsTvDirectpayplanMainDto)dateDto).getDvoucher();
						svouno = ((TbsTvDirectpayplanMainDto)dateDto).getSvouno();
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20��ǰ���ļ����ܵ���ϵͳ!");
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",hn=checkvouno,"))
						{
							TvDirectpaymsgmainDto sdto = new TvDirectpaymsgmainDto();
							sdto.setSorgcode(((TbsTvDirectpayplanMainDto)dateDto).getSbookorgcode());
							sdto.setStrecode(((TbsTvDirectpayplanMainDto)dateDto).getStrecode());
							sdto.setStaxticketno(svouno);
							try {
								List searchlist = null;
								searchlist = CommonFacade.getODB().findRsByDto(sdto);
								if(searchlist!=null&&searchlist.size()>0)
								{
									throw new ITFEBizException("ƾ֤��Ϊ"+svouno+"������ϵͳ�Ѿ��ɹ������!");
								}
							} catch (Exception e) {
							}
						}
					}else if(dateDto instanceof TbsTvGrantpayplanMainDto)
					{
						
						voudate = null;
						try{
							String filename = ((TbsTvGrantpayplanMainDto)dateDto).getSfilename();
							svouno = ((TbsTvGrantpayplanMainDto)dateDto).getSvouno();
							if(filename!=null)
							{
								voudate = Date.valueOf(filename.substring(0,4)+"-"+filename.substring(4,6)+"-"+filename.substring(6,8));
							}
							if(ITFECommonConstant.PUBLICPARAM.contains(",hn=checkvouno,"))
							{
								TvGrantpaymsgmainDto sdto = new TvGrantpaymsgmainDto();
								sdto.setSorgcode(((TbsTvGrantpayplanMainDto)dateDto).getSbookorgcode());
								sdto.setStrecode(((TbsTvGrantpayplanMainDto)dateDto).getStrecode());
								sdto.setSpackageticketno(svouno);
								try {
									List searchlist = null;
									searchlist = CommonFacade.getODB().findRsByDto(sdto);
									if(searchlist!=null&&searchlist.size()>0)
									{
										throw new ITFEBizException("ƾ֤��Ϊ"+svouno+"������ϵͳ�Ѿ��ɹ������!");
									}
								} catch (Exception e) {
								}
							}
						}catch(Exception e)
						{
							voudate = TimeFacade.getCurrentDateTime();
						}
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20��ǰ���ļ����ܵ���ϵͳ!");
						}
					}else if(dateDto instanceof TbsTvBnkpayMainDto)
					{
						voudate = ((TbsTvBnkpayMainDto)dateDto).getDvoucher();
						svouno = ((TbsTvBnkpayMainDto)dateDto).getSvouno();
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20��ǰ���ļ����ܵ���ϵͳ!");
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",hn=checkvouno,"))
						{
							TvPayreckBankDto sdto = new TvPayreckBankDto();
							sdto.setSbookorgcode(((TbsTvBnkpayMainDto)dateDto).getSbookorgcode());
							sdto.setStrecode(((TbsTvBnkpayMainDto)dateDto).getStrecode());
							sdto.setSvouno(svouno);
							try {
								List searchlist = null;
								searchlist = CommonFacade.getODB().findRsByDto(sdto);
								if(searchlist!=null&&searchlist.size()>0)
								{
									throw new ITFEBizException("ƾ֤��Ϊ"+svouno+"������ϵͳ�Ѿ��ɹ������!");
								}
							} catch (Exception e) {
							}
						}
					}
			}
			if (filekind.equals(MsgConstant.TUIKU_XIAMEN_DAORU)
					|| filekind.equals(MsgConstant.SHIBO_XIAMEN_DAORU)) {

				if (file.toLowerCase().endsWith("txt")) { // ��ϸ�ڽӿ�

					allmudto.getFatherDtos().addAll(mudto.getFatherDtos());

					allmudto.getPackDtos().addAll(mudto.getPackDtos());

					allmudto.getVoulist().addAll(mudto.getVoulist());

					try {

						this.checkVouRepeat(allmudto, filekind, fl.getName()); // ����ƾ֤����ظ���У��

					} catch (JAFDatabaseException e) {

						log.error(e);

						throw new ITFEBizException("ƾ֤���У���쳣", e);

					} catch (ValidateException e) {

						log.error(e);

						throw new ITFEBizException("ƾ֤���У���쳣", e);

					}

				} else if (file.toLowerCase().endsWith("tmp")) { // �ʽ�ڽӿ�

					allmudto.getSonDtos().addAll(mudto.getSonDtos());

				}

			} else if (filekind.equals(MsgConstant.PILIANG_DAORU)) {

				this.createFileDto(mudto, filekind); // �������� ֱ�ӱ���

				/**
				 * ֱ��֧���������Ȩ֧������谴С��1000���ļ���һ����
				 */
			} else if (filekind.equals(MsgConstant.ZHIJIE_DAORU)
					|| filekind.equals(MsgConstant.SHOUQUAN_DAORU)) {
				// ����֮ǰ��У�鵱��ƾ֤����Ƿ��ظ�
				try {
					this.checkVouRepeat(mudto, filekind, fl.getName());
					
					allPaydto.getFatherDtos().addAll(mudto.getFatherDtos());
					allPaydto.getPackDtos().addAll(mudto.getPackDtos());
					allPaydto.getSonDtos().addAll(mudto.getSonDtos());
					allPaydto.getVoulist().addAll(mudto.getVoulist());
					
					//�˴���У�� ΪУ�����ڵ����files֮���ƾ֤����ظ� 20120705�޸�
					this.checkVouRepeat(allPaydto, filekind, fl.getName());
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("ƾ֤���У���쳣", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("ƾ֤���У���쳣", e);
				}
				
			} else if(filekind.equals(MsgConstant.TAXORG_PAYOUT)||filekind.equals(MsgConstant.MSG_NO_3452)||filekind.equals(MsgConstant.MSG_NO_3403)||filekind.equals("3208")||filekind.equals("3251")||filekind.equals("3210")){
//			} else if(filekind.equals(MsgConstant.TAXORG_PAYOUT)||filekind.equals(MsgConstant.MSG_NO_3403)||filekind.equals("3208")||filekind.equals("3251")||filekind.equals("3210")){
				this.createFileDto(mudto, filekind);
			}else if(filekind.equals(MsgConstant.MSG_NO_JZZF)){
				this.createFileDto(mudto, filekind);
			}else if (filekind.equals(MsgConstant.APPLYPAY_DAORU)) {//��������
				// �������а���֧�����������ļ����
				MulitTableDto allHksqdto = new MulitTableDto(new ArrayList<IDto>(),
						new ArrayList<IDto>(), new ArrayList<IDto>(),
						new ArrayList<String>());
				// ����֮ǰ��У�鵱��ƾ֤����Ƿ��ظ�
				try {
					
					allHksqdto.getFatherDtos().addAll(mudto.getFatherDtos());
					allHksqdto.getSonDtos().addAll(mudto.getSonDtos());
					allHksqdto.getVoulist().addAll(mudto.getVoulist());
					
					//�˴���У�� ΪУ�����ڵ����files֮���ƾ֤����ظ� 20120705�޸�
					this.checkVouRepeat(allHksqdto, filekind, fl.getName());
					//�������밴�տ��˿������к��н��зְ�
					if(null != allHksqdto && allHksqdto.getFatherDtos() !=null && allHksqdto.getFatherDtos().size() > 0) {
						allHksqdto = this.splitPackageWithBank(mudto.getFatherDtos(), bztype);
						allHksqdto.setSonDtos(mudto.getSonDtos());
//						this.createFileDto(allHksqdto, filekind); // ���水�տ��зְ��������
						l.add(allHksqdto);
					}
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("��������ְ��쳣��", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("��������ְ��쳣��", e);
				} catch (SequenceException e) {
					log.error(e);
					throw new ITFEBizException("��������ְ��쳣��", e);
				}
			}else if (filekind.equals(MsgConstant.APPLYPAY_BACK_DAORU)) {//���������˻�
				// �������а���֧�����������ļ����
				MulitTableDto allHksqdto = new MulitTableDto(new ArrayList<IDto>(),
						new ArrayList<IDto>(), new ArrayList<IDto>(),
						new ArrayList<String>());
				// ����֮ǰ��У�鵱��ƾ֤����Ƿ��ظ�
				try {
					
					allHksqdto.getFatherDtos().addAll(mudto.getFatherDtos());
					allHksqdto.getSonDtos().addAll(mudto.getSonDtos());
					allHksqdto.getVoulist().addAll(mudto.getVoulist());
					
					//�˴���У�� ΪУ�����ڵ����files֮���ƾ֤����ظ� 20120705�޸�
					this.checkVouRepeat(allHksqdto, filekind, fl.getName());
					//�������밴ԭƾ֤����н��зְ�
					if(null != allHksqdto && allHksqdto.getFatherDtos() !=null && allHksqdto.getFatherDtos().size() > 0) {
						allHksqdto = this.splitPackageWithVouno(mudto.getFatherDtos(), mudto.getSonDtos(),bztype);
						l.add(allHksqdto);
					}
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("���������˻طְ��쳣��", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("���������˻طְ��쳣��", e);
				} catch (SequenceException e) {
					log.error(e);
					throw new ITFEBizException("���������˻طְ��쳣��", e);
				}
			}else if (filekind.startsWith(MsgConstant.SHIBO_FUJIAN_DAORU)) {//У�鸣�����ʵ��루������ƾ֤��ţ�
				try {
					// ����֮ǰ��У�鵱��ƾ֤����Ƿ��ظ�
					this.checkVouRepeatForFJ(mudto, filekind, fl.getName());
					
					allPay17dto.getFatherDtos().addAll(mudto.getFatherDtos());
					allPay17dto.getPackDtos().addAll(mudto.getPackDtos());
					allPay17dto.getVoulist().addAll(mudto.getVoulist());
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("ƾ֤���У���쳣", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("ƾ֤���У���쳣", e);
				}
			}else {

				try {

					this.checkVouRepeat(mudto, filekind, fl.getName());// ����֮ǰ��У�鵱��ƾ֤����Ƿ��ظ�
					
					allPay17dto.getFatherDtos().addAll(mudto.getFatherDtos());
					allPay17dto.getPackDtos().addAll(mudto.getPackDtos());
					allPay17dto.getVoulist().addAll(mudto.getVoulist());
					
					//�˴���У�� ΪУ�����ڵ����files֮���ƾ֤����ظ� 20120801�޸�
					this.checkVouRepeat(allPay17dto, filekind, fl.getName());

				} catch (JAFDatabaseException e) {

					log.error(e);

					throw new ITFEBizException("ƾ֤���У���쳣", e);

				} catch (ValidateException e) {

					log.error(e);

					throw new ITFEBizException("ƾ֤���У���쳣", e);

				}
				/*
				this.createFileDto(mudto, filekind); // ���ж�У����ϣ�����
				*/

			}

		}
		
		if(null != allPay17dto && allPay17dto.getFatherDtos() !=null && allPay17dto.getFatherDtos().size() > 0) {
			this.createFileDto(allPay17dto, filekind);
		}
		if(null!=l&&l.size()>0){
			List fatherlists=new ArrayList();
			List sonlists=new ArrayList();
			List packagelists=new ArrayList();
			 for(MulitTableDto dto:l){
				 fatherlists.addAll(dto.getFatherDtos());
				 sonlists.addAll(dto.getSonDtos());
				 packagelists.addAll(dto.getPackDtos());
			 }
			 allHksqdtos.setFatherDtos(fatherlists);
			 allHksqdtos.setSonDtos(sonlists);
			 allHksqdtos.setPackDtos(packagelists);
			 this.createFileDto(allHksqdtos, filekind);
		}
		
		/**
		 * ����ֱ��֧���������Ȩ֧����ȣ�����ļ����ݰ���ͬ��������������
		 */
		if ((allPaydto.getFatherDtos() != null
				&& allPaydto.getFatherDtos().size() > 0)) {
			setPayDataOnePackage(allPaydto, filekind);
		}

		// 3.����ʵ�����˿�����Žӿڷ���ϸ�ں��ʽ�ڣ���Ҫ�����������,
		// �����տ��˺źͽ����й���
		if (allmudto.getFatherDtos() != null
				&& allmudto.getFatherDtos().size() > 0) {
			for (IDto dto : allmudto.getFatherDtos()) {
				if (dto instanceof TbsTvPayoutDto) { // ʵ���ʽ�XM
					TbsTvPayoutDto pdto = (TbsTvPayoutDto) dto;
					for (IDto dto1 : allmudto.getSonDtos()) {
						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
						if (pdto.getSpayeeacct().trim().equals(
								fidto.getSpayeeacct().trim())) {
							if (pdto.getFamt().compareTo(fidto.getFamt()) == 0) {
								pdto.setSpayeeopnbnkno(fidto
										.getSpayeeopnbnkno()); // �����ʽ�ڵ��տ��˿������к�
								pdto.setSpayeename(fidto.getSpayeename()); // �����ʽ���տ�������
								pdto.setSbdgorgname(fidto.getSpayeename()); // ����Ԥ�㵥λ����
								pdto.setSpayeebankno(fidto.getSrcvbnkno()); // ����<�������к�>��Ϊ����������к�
								pdto.setSaddword(fidto.getSaddword());// ����
								if ("".equals(pdto.getSbdgorgcode().trim())) { // ���Ԥ�㵥λ����Ϊ��������
									if (fidto.getSpayeeacct().length() > 15) {
										pdto.setSbdgorgcode(fidto
												.getSpayeeacct().trim()
												.substring(0, 15));
									} else {
										pdto.setSbdgorgcode(fidto
												.getSpayeeacct().trim());
									}
								}
								li.add(pdto);
								break;
							}
						}
					}
				} else if (dto instanceof TbsTvDwbkDto) { // �˿�XM
					TbsTvDwbkDto dwdto = (TbsTvDwbkDto) dto;
					for (IDto dto1 : allmudto.getSonDtos()) {
						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
						if (dwdto.getSpayeeacct().trim().equals(
								fidto.getSpayeeacct().trim())) {
							if (dwdto.getFamt().compareTo(fidto.getFamt()) == 0) {
								dwdto.setSpayeeopnbnkno(fidto
										.getSpayeeopnbnkno()); // �����ʽ�ڵ��տ��˿������к�
								dwdto.setSpayeename(fidto.getSpayeename()); // �����ʽ���տ�������
								li.add(dwdto);
								break;
							}
						}
					}
				}
			}
			// ��������֮���dto��ĿС��û�����֮ǰ
			if (allmudto.getFatherDtos().size() != li.size()) {

				throw new ITFEBizException("ͨ����ϸ�ڸ������˺źͽ��δ�ҵ���Ӧ�ʽ�ڼ�¼,���֤");

			}

			allmudto.setSonDtos(null); // ������Ҫд�����ݿ���ʽ�����

			if (filekind.equals(MsgConstant.SHIBO_XIAMEN_DAORU)) { // ����ʵ��

				try {

					allmudtoforxm = this.splitPackageWithBank(li, biztype); // ����ʵ���谴�����зְ�

				} catch (JAFDatabaseException e) {

					log.error(e);

					throw new ITFEBizException("���ݽ����쳣", e);

				} catch (ValidateException e) {

					log.error(e);

					throw new ITFEBizException("���ݽ����쳣", e);

				} catch (SequenceException e) {

					log.error(e);

					throw new ITFEBizException("���ݽ����쳣", e);

				}

				this.createFileDto(allmudtoforxm, filekind); // ���水�����зְ��������

				return allmudtoforxm;

			} else if (filekind.equals(MsgConstant.TUIKU_XIAMEN_DAORU)) { // �����˿�

				allmudto.setFatherDtos(li); // ����������ݵ�List����

				this.createFileDto(allmudto, filekind); // ��������

				return allmudto;

			}
		}
		// �ڷ��ص�ʱ����Ҫ�����д�����Ϣ/�����ļ�����/�����ļ�������
		if(null == mudto || null == mudto.getErrorList()) {
			mudto = new MulitTableDto();
		}
		mudto.setErrorList(errorList);
		mudto.setErrorCount(errorFileCount);
		mudto.setErrNameList(errFileNameList);
		return mudto;
	}

	/**
	 * ����ֱ��֧���������Ȩ֧����ȣ�����ļ����ݰ���ͬ��������������
	 * 
	 * @param mulTbldto�������ļ�����
	 * @param filekind�ļ�����
	 * @throws ITFEBizException
	 */
	private void setPayDataOnePackage(MulitTableDto mulTbldto, String filekind)
			throws ITFEBizException {

		// ���ڴ�Ų�ͬ�Ĺ����������
		Set<String> treCodeSet = new HashSet<String>();
		// ȡ����ͬ�����������
		if (mulTbldto.getPackDtos() != null
				&& mulTbldto.getPackDtos().size() > 0) {
			for (IDto dto : mulTbldto.getPackDtos()) {
				if (dto instanceof TvFilepackagerefDto) {
					TvFilepackagerefDto filePagrefdto = (TvFilepackagerefDto) dto;
					// �����������
					treCodeSet.add(filePagrefdto.getStrecode());
				}

			}
		}
		// ��ȡ����ˮ��
		String tmpPackNo = "";
		for (String streCode : treCodeSet) {
			try {
				tmpPackNo = SequenceGenerator
						.changePackNoForLocal(SequenceGenerator.getNextByDb2(
								SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,
								MsgConstant.SEQUENCE_MAX_DEF_VALUE));
			} catch (SequenceException e) {
				log.error(e);
				throw new ITFEBizException("��ȡ����ˮ���쳣 \n" + e.getMessage(), e);
			}
			// �����ļ������Ӧ��ϵ�İ���ˮ��
			for (int i = 0; i < mulTbldto.getPackDtos().size(); i++) {
				// ͬ����������������ͬһ������ˮ��
				if (streCode.equals(((TvFilepackagerefDto) mulTbldto
						.getPackDtos().get(i)).getStrecode())) {
					((TvFilepackagerefDto) mulTbldto.getPackDtos().get(i))
							.setSpackageno(tmpPackNo);
				}
			}
			// ��������İ���ˮ��
			for (int i = 0; i < mulTbldto.getFatherDtos().size(); i++) {
				/**
				 * ͬ����������������ͬһ������ˮ��
				 */
				// ֱ��֧�����
				if (MsgConstant.ZHIJIE_DAORU.equals(filekind)) {

					if (streCode.equals(((TbsTvDirectpayplanMainDto) mulTbldto
							.getFatherDtos().get(i)).getStrecode())) {
						((TbsTvDirectpayplanMainDto) mulTbldto.getFatherDtos()
								.get(i)).setSpackageno(tmpPackNo);
					}
					// ��Ȩ֧�����
				} else if (MsgConstant.SHOUQUAN_DAORU.equals(filekind)) {
					if (streCode.equals(((TbsTvGrantpayplanMainDto) mulTbldto
							.getFatherDtos().get(i)).getStrecode())) {
						((TbsTvGrantpayplanMainDto) mulTbldto.getFatherDtos()
								.get(i)).setSpackageno(tmpPackNo);
					}
				}
			}
		}
		// ����������
		this.createFileDto(mulTbldto, filekind);
	}

	/**
	 * �ṩ��MulitTableDto ���͵����ݽ���ֱ�ӱ���
	 */
	public void createFileDto(MulitTableDto multidto, String filekind)
			throws ITFEBizException {

		// �����ļ������Ľ��
		try {

			if (multidto != null) {

				List<IDto> fatherDtos = multidto.getFatherDtos();

				List<IDto> sonDtos = multidto.getSonDtos();

				List<IDto> packDtos = multidto.getPackDtos();

				if (fatherDtos != null && fatherDtos.size() > 0) {

					DatabaseFacade.getODB().create(
							CommonUtil.listTArray(fatherDtos));

				}

				if (sonDtos != null && sonDtos.size() > 0) {

					DatabaseFacade.getODB().create(
							CommonUtil.listTArray(sonDtos));

				}

				if (packDtos != null && packDtos.size() > 0) {

					DatabaseFacade.getODB().create(
							CommonUtil.listTArray(packDtos));

				}
				if(multidto.getUpdateIdtoList()!=null&&multidto.getUpdateIdtoList().size()>0)
					DatabaseFacade.getODB().update(CommonUtil.listTArray(multidto.getUpdateIdtoList()));
			}

		} catch (JAFDatabaseException e) {

			log.error(e);

			throw new ITFEBizException("�ļ��������ݱ����쳣", e);

		}
	}

	/**
	 * �жϵ���ƾ֤����Ƿ��ظ�
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public void checkVouRepeat(MulitTableDto mdto, String filekind,
			String filename) throws JAFDatabaseException, ValidateException,
			ITFEBizException {

		List<String> voulist = new ArrayList<String>(); // ��ʼ��

		Date today = TimeFacade.getCurrentDateTime(); // ��ǰ����

		String bookorgcode = getLoginInfo().getSorgcode();

		int oldSize = 0;

		int newSize = 0;

		Set<String> sets = new HashSet<String>();

		if (mdto != null && mdto.getVoulist().size() > 0) {

			voulist.addAll(mdto.getVoulist()); // ƾ֤��ż�

		} else {

			return;

		}

		if (filekind.equals(MsgConstant.ZHIJIE_DAORU)) {
			// ֱ��֧�����
			TbsTvDirectpayplanMainDto dto = new TbsTvDirectpayplanMainDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvDirectpayplanMainDto> direlist = (List<TbsTvDirectpayplanMainDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			for (TbsTvDirectpayplanMainDto dt : direlist) {
				voulist.add(dt.getStrecode().trim() + ","
						+ dt.getSvouno().trim());
			}
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					if(direlist == null || direlist.size() == 0) {
						throw new ITFEBizException("ֱ��֧������ļ�[" + filename
								+ "]��ƾ֤���[" + item.split(",")[1]
								+ "]�����ڵ���������ļ�ƾ֤����ظ�,���֤");
					} else {
						throw new ITFEBizException("ֱ��֧������ļ�[" + filename
								+ "]��ƾ֤���[" + item.split(",")[1]
								+ "]�͵����ѵ����ļ�ƾ֤����ظ�,���֤");
					}
				}
			}

		} else if (filekind.equals(MsgConstant.SHOUQUAN_DAORU)) {
			// ��Ȩ֧�����
			TbsTvGrantpayplanMainDto dto = new TbsTvGrantpayplanMainDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvGrantpayplanMainDto> granlist = (List<TbsTvGrantpayplanMainDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			for (TbsTvGrantpayplanMainDto gt : granlist) {
				voulist.add(gt.getStrecode().trim() + ","
						+ gt.getSvouno().trim());
			}
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					if(granlist == null || granlist.size() == 0) {
						throw new ITFEBizException("��Ȩ֧������ļ�[" + filename
								+ "]��ƾ֤���[" + item.split(",")[1]
								+ "]�����ڵ���������ļ�ƾ֤����ظ�,���֤");
					} else {
						throw new ITFEBizException("��Ȩ֧������ļ�[" + filename
								+ "]��ƾ֤���[" + item.split(",")[1]
								+ "]�͵����ѵ����ļ�ƾ֤����ظ�,���֤");
					}
				}
			}

		} else if (filekind.startsWith(MsgConstant.MSG_NO_1104)) {
			// �˿�
			TbsTvDwbkDto dto = new TbsTvDwbkDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvDwbkDto> dwbklist = (List<TbsTvDwbkDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			if (dwbklist != null && dwbklist.size() > 0) {
				for (TbsTvDwbkDto dt : dwbklist) {
					voulist.add(dt.getStaxorgcode().trim() + ","
							+ dt.getSdwbkvoucode().trim());
				}
				for (String item : voulist) {
					oldSize = sets.size();
					sets.add(item.trim());
					newSize = sets.size();
					if (newSize == oldSize) {
						throw new ITFEBizException("�˿��ļ�[" + filename
								+ "]��ƾ֤���[" + item.split(",")[1]
								+ "]�͵����ѵ���ƾ֤����ظ�,���֤");
					}
				}
			}

		} else if (filekind.startsWith(MsgConstant.MSG_NO_5101)) {
			// ʵ���ʽ�
			//�ж��ļ���ƾ֤����ظ�
			int oldSize_tmp = 0;
			int newSize_tmp = 0;
			Set<String> sets_tmp = new HashSet<String>();
			for(String vou : voulist) {
				oldSize_tmp = sets_tmp.size();
				sets_tmp.add(vou.trim());
				newSize_tmp = sets_tmp.size();
				if (newSize_tmp == oldSize_tmp) {
					throw new ITFEBizException("ʵ���ʽ��ļ�[" + filename
							+ "]��ƾ֤���[" + vou.split(",")[1]
							+ "]�����ڵ���������ļ�ƾ֤����ظ�,���֤");
				}
			}
			
			TbsTvPayoutDto dto = new TbsTvPayoutDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvPayoutDto> paylist = (List<TbsTvPayoutDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			for (TbsTvPayoutDto pt : paylist) {
				voulist.add(pt.getStrecode().trim() + ","
						+ pt.getSvouno().trim());
			}
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {					
					throw new ITFEBizException("ʵ���ʽ��ļ�[" + filename
							+ "]��ƾ֤���[" + item.split(",")[1]
							+ "]�͵����ѵ���ƾ֤����ظ�,���֤");
				}
			}

		} else if (filekind.equals(MsgConstant.GENGZHENG_DAORU)) {
			// ����
			TbsTvInCorrhandbookDto dto = new TbsTvInCorrhandbookDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvInCorrhandbookDto> corrlist = (List<TbsTvInCorrhandbookDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			if (corrlist != null && corrlist.size() > 0) {
				for (TbsTvInCorrhandbookDto it : corrlist) {
					voulist.add(it.getScorrvouno().trim());
				}
				for (String item : voulist) {
					oldSize = sets.size();
					sets.add(item.trim());
					newSize = sets.size();
					if (newSize == oldSize) {
						throw new ITFEBizException("�����ļ�[" + filename
								+ "]��ƾ֤���[" + item + "]�͵����ѵ���ƾ֤����ظ�,���֤");
					}
				}
			}
		} else if (filekind.equals(MsgConstant.TAX_FREE_DAORU)) {
			// ��ֵ�
			TbsTvFreeDto dto = new TbsTvFreeDto();
			dto.setDacceptdate(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvFreeDto> freelist = (List<TbsTvFreeDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			if (freelist != null && freelist.size() > 0) {
				for (TbsTvFreeDto it : freelist) {
					voulist.add(it.getSfreevouno().trim());
				}
				for (String item : voulist) {
					oldSize = sets.size();
					sets.add(item.trim());
					newSize = sets.size();
					if (newSize == oldSize) {
						throw new ITFEBizException("��ֵ��ļ�[" + filename
								+ "]��ƾ֤���[" + item + "]�͵����ѵ���ƾ֤����ظ�,���֤");
					}
				}
			}
		} else if (filekind.equals(MsgConstant.PBC_DIRECT_IMPORT)) {
			// ���а���ֱ��֧��
			TbsTvPbcpayDto dto = new TbsTvPbcpayDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvPbcpayDto> pbclist = (List<TbsTvPbcpayDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			if (pbclist != null && pbclist.size() > 0) {
				for (TbsTvPbcpayDto pbc : pbclist) {
					voulist.add(pbc.getStrecode().trim() + ","
							+ pbc.getSvouno().trim());
				}
				for (String item : voulist) {
					oldSize = sets.size();
					sets.add(item.trim());
					newSize = sets.size();
					if (newSize == oldSize) {
						throw new ITFEBizException("���а���ֱ��֧���ļ�[" + filename
								+ "]��ƾ֤���[" + item + "]���ѵ���ƾ֤����ظ�,���֤");
					}
				}
			}else if (filekind.equals(MsgConstant.APPLYPAY_DAORU)
					||filekind.equals(MsgConstant.APPLYPAY_BACK_DAORU)) {
				// ���а���֧����������
				TbsTvBnkpayMainDto bankpaydto = new TbsTvBnkpayMainDto();
				bankpaydto.setDaccept(today);
				bankpaydto.setSbookorgcode(bookorgcode);
				List<TbsTvBnkpayMainDto> granlist = (List<TbsTvBnkpayMainDto>) CommonFacade
						.getODB().findRsByDtoWithUR(bankpaydto);
				for (TbsTvBnkpayMainDto gt : granlist) {
					voulist.add(gt.getStrecode().trim() + ","
							+ gt.getSvouno().trim());
				}
				for (String item : voulist) {
					oldSize = sets.size();
					sets.add(item.trim());
					newSize = sets.size();
					if (newSize == oldSize) {
						if(granlist == null || granlist.size() == 0) {
							throw new ITFEBizException("���а���֧�����������ļ�[" + filename
									+ "]��ƾ֤���[" + item.split(",")[1]
									+ "]�����ڵ���������ļ�ƾ֤����ظ�,���֤");
						} else {
							throw new ITFEBizException("���а���֧�����������ļ�[" + filename
									+ "]��ƾ֤���[" + item.split(",")[1]
									+ "]�͵����ѵ����ļ�ƾ֤����ظ�,���֤");
						}
					}
				}
			}
		}

	}
	
	
	
	
	/**
	 * �жϵ���ƾ֤����Ƿ��ظ�(�������ʵ���)
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public void checkVouRepeatForFJ(MulitTableDto mdto, String filekind,
			String filename) throws JAFDatabaseException, ValidateException,
			ITFEBizException {
		List<String> voulist = new ArrayList<String>(); // ��ʼ��
		Date today = TimeFacade.getCurrentDateTime(); // ��ǰ����
		String bookorgcode = getLoginInfo().getSorgcode();
		
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		if (mdto != null && mdto.getVoulist().size() > 0) {
			voulist.addAll(mdto.getVoulist()); // ƾ֤��ż�
		} else {
			return;
		}

		if (filekind.startsWith(MsgConstant.MSG_NO_5101)) {
			//ʵ���ʽ��ж��ļ���ƾ֤����ظ�
			int oldSize_tmp = 0;
			int newSize_tmp = 0;
			Set<String> sets_tmp = new HashSet<String>();
			for(String vou : voulist) {
				oldSize_tmp = sets_tmp.size();
				sets_tmp.add(vou.trim());
				newSize_tmp = sets_tmp.size();
				if (newSize_tmp == oldSize_tmp) {
					throw new ITFEBizException("ʵ���ʽ��ļ�[" + filename
							+ "]��ƾ֤���[" + vou.split(",")[1]
							+ "]�����ڵ���������ļ�ƾ֤����ظ�,���֤");
				}
			}
			
			TbsTvPayoutDto dto = new TbsTvPayoutDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvPayoutDto> paylist = (List<TbsTvPayoutDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			List<String> allvoulist = new ArrayList<String>(); // ��ʼ��
			for (TbsTvPayoutDto pt : paylist) {
				if (!allvoulist.contains(pt.getStrecode().trim() + ","
						+ pt.getSvouno().trim())) {
					allvoulist.add(pt.getStrecode().trim() + ","
							+ pt.getSvouno().trim());
				}
			}
			voulist.addAll(allvoulist);
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {					
					throw new ITFEBizException("ʵ���ʽ��ļ�[" + filename
							+ "]��ƾ֤���[" + item.split(",")[1]
							+ "]�͵����ѵ���ƾ֤����ظ�,���֤");
				}
			}
		}
	}
	
	
	

	/**
	 * ��ʵ���ʽ� ���Žӿڽ��а������в��
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws SequenceException
	 * @throws ITFEBizException
	 */
	public MulitTableDto splitPackageWithBank(List<IDto> paylist, String biztype)
			throws JAFDatabaseException, ValidateException, SequenceException,
			ITFEBizException {
		MulitTableDto tdto = new MulitTableDto();

		String tmpPackNo = ""; // ����ˮ��
		BigDecimal famtPack = new BigDecimal("0.00"); // �����ܽ��

		int fbcount = 0; // �����ܱ���
		Set<String> bnkset = new HashSet<String>(); // �����м�
		Set<String> fileset = new HashSet<String>(); // �ļ���
		Map<String, Map<String, List<IDto>>> filemap = new HashMap<String, Map<String, List<IDto>>>();
		List<IDto> fadtos = new ArrayList<IDto>(); // ��¼��
		List<IDto> packdtos = new ArrayList<IDto>(); // �ְ�����
		List<IDto> hasBankdtos = new ArrayList<IDto>(); //������տ��˿������кŵ�dto
		List<IDto> noBankdtos = new ArrayList<IDto>(); //���û���տ��˿������кŵ�dto

		if (paylist != null && paylist.size() > 0) {
			if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					||biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){

				// 1.���ȵõ�<������>����������(5.3�޸�)
				for (IDto pay : paylist) {
					TbsTvBnkpayMainDto outd = (TbsTvBnkpayMainDto) pay;
					if(null != outd.getSpayeeopnbnkno() && !"".equals(outd.getSpayeeopnbnkno())) { //�տ��˿������к�Ϊ�յ��Ȳ���
						bnkset.add(outd.getSpayeeopnbnkno()); // <�õ�������>��Ϊ�������зְ�
						fileset.add(outd.getSfilename()); // �õ��ļ���(��ϸ��)
						hasBankdtos.add(outd);
					}else {
						noBankdtos.add(outd);
					}
				}
				// 2.���ȸ����ļ��ֿ�
				for (String fe : fileset) {
					Map<String, List<IDto>> newmap = new HashMap<String, List<IDto>>();
					// Ȼ�����<������>�����зֿ�
					for (String st : bnkset) {
						List<IDto> nlist = new ArrayList<IDto>();
						for (IDto dto : hasBankdtos) {
							TbsTvBnkpayMainDto bkdto = (TbsTvBnkpayMainDto) dto;
							if (st.equals(bkdto.getSpayeeopnbnkno())
									&& fe.equals(bkdto.getSfilename())) {
								nlist.add(bkdto);
							}
						}
						newmap.put(st, nlist);
					}
					filemap.put(fe, newmap);
				}
				// 3.����ķְ�����
				for (String fe : fileset) {
					Map<String, List<IDto>> smap = filemap.get(fe);
					for (String st : bnkset) {
						List<IDto> detlist = smap.get(st);
						if (detlist != null && detlist.size() > 0) {
							int li_Detail = (detlist.size() - 1) / 1000;
							for (int k = 0; k <= li_Detail; k++) {
								int li_TempCount = 0;
								if (li_Detail == k) {
									li_TempCount = detlist.size();
								} else {
									li_TempCount = (k + 1) * 1000;
								}
								tmpPackNo = SequenceGenerator
										.changePackNoForLocal(SequenceGenerator
												.getNextByDb2(
														SequenceName.FILENAME_PACKNO_REF_SEQ,
														SequenceName.TRAID_SEQ_CACHE,
														SequenceName.TRAID_SEQ_STARTWITH,
														MsgConstant.SEQUENCE_MAX_DEF_VALUE));
								String trecode = "";
								for (int j = k * 1000; j < li_TempCount; j++) {
									TbsTvBnkpayMainDto bkdto = (TbsTvBnkpayMainDto) detlist
											.get(j);
									trecode = bkdto.getStrecode();
									// ���ð���ˮ��
									bkdto.setSpackno(tmpPackNo);
									// ���н��ͳ�ƣ����ڷְ�
									famtPack = famtPack.add(bkdto.getFsmallsumamt().add(bkdto.getFzerosumamt()));
									// ����������ͳ��
									fbcount++;
									// ���������������ɵ�DTO����
									fadtos.add(bkdto);
								}
								// �����ļ��������ˮ�Ŷ�Ӧ��ϵ
								TvFilepackagerefDto packdto = new TvFilepackagerefDto();
								packdto.setSorgcode(getLoginInfo().getSorgcode());
								packdto.setStrecode(trecode);
								packdto.setSfilename(fe);
								TsConvertfinorgDto condto = new TsConvertfinorgDto();
								condto.setSorgcode(getLoginInfo().getSorgcode());
								condto.setStrecode(trecode);
								List conlist = CommonFacade.getODB()
										.findRsByDtoWithUR(condto);
								if (conlist == null || conlist.size() == 0) {
									throw new ITFEBizException("���������ļ�[" + fe
											+ "] �и��ݺ���������룺"
											+ getLoginInfo().getSorgcode()
											+ "�� ����������룺" + trecode
											+ " û���� '����������Ϣά������'���ҵ���Ӧ�����������룬���֤");
								}
								TsConvertfinorgDto gd = (TsConvertfinorgDto) conlist
										.get(0);
								packdto.setStaxorgcode(gd.getSfinorgcode());
								packdto.setScommitdate(TimeFacade
										.getCurrentStringTime());
								packdto.setSaccdate(TimeFacade
										.getCurrentStringTime());
								packdto.setSpackageno(tmpPackNo);
								packdto.setSoperationtypecode(biztype);
								packdto.setIcount(fbcount);
								packdto.setNmoney(famtPack);
								packdto
										.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
								packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
								packdto.setSusercode(getLoginInfo().getSuserCode());
								packdto.setImodicount(0);
								fbcount = 0;
								famtPack = new BigDecimal("0.00");
								packdtos.add(packdto);
							}
						}
					}
				}
				//���տ��˿������к�û�в�¼�ļ�¼���д���
				if(null != noBankdtos && noBankdtos.size() > 0) {
					for(IDto nobankdto : noBankdtos) {
						TbsTvBnkpayMainDto noBankMainDto = (TbsTvBnkpayMainDto)nobankdto;
						tmpPackNo = SequenceGenerator
						.changePackNoForLocal(SequenceGenerator
								.getNextByDb2(
										SequenceName.FILENAME_PACKNO_REF_SEQ,
										SequenceName.TRAID_SEQ_CACHE,
										SequenceName.TRAID_SEQ_STARTWITH,
										MsgConstant.SEQUENCE_MAX_DEF_VALUE));
						String trecode = noBankMainDto.getStrecode();
						noBankMainDto.setSpackno(tmpPackNo);
						fadtos.add(noBankMainDto);
						// �����ļ��������ˮ�Ŷ�Ӧ��ϵ
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(getLoginInfo().getSorgcode());
						packdto.setStrecode(trecode);
						packdto.setSfilename(noBankMainDto.getSfilename());
						TsConvertfinorgDto condto = new TsConvertfinorgDto();
						condto.setSorgcode(getLoginInfo().getSorgcode());
						condto.setStrecode(trecode);
						List conlist = CommonFacade.getODB()
								.findRsByDtoWithUR(condto);
						if (conlist == null || conlist.size() == 0) {
							throw new ITFEBizException("���������ļ�[" + noBankMainDto.getSfilename()
									+ "] �и��ݺ���������룺"
									+ getLoginInfo().getSorgcode()
									+ "�� ����������룺" + trecode
									+ " û���� '����������Ϣά������'���ҵ���Ӧ�����������룬���֤");
						}
						TsConvertfinorgDto gd = (TsConvertfinorgDto) conlist.get(0);
						packdto.setStaxorgcode(gd.getSfinorgcode());
						packdto.setScommitdate(TimeFacade.getCurrentStringTime());
						packdto.setSaccdate(TimeFacade.getCurrentStringTime());
						packdto.setSpackageno(tmpPackNo);
						packdto.setSoperationtypecode(biztype);
						BigDecimal tmpFamt = noBankMainDto.getFsmallsumamt().add(noBankMainDto.getFzerosumamt());
						packdto.setIcount(1);
						packdto.setNmoney(tmpFamt);
						packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
						packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
						packdto.setSusercode(getLoginInfo().getSuserCode());
						packdto.setImodicount(0);
						fbcount = 0;
						famtPack = new BigDecimal("0.00");
						packdtos.add(packdto);
					}
				}
			}else {
				// 1.���ȵõ�<������>����������(5.3�޸�)
				for (IDto pay : paylist) {
					TbsTvPayoutDto outd = (TbsTvPayoutDto) pay;
					bnkset.add(outd.getSpayeeopnbnkno()); // <�õ�������>��Ϊ�������зְ�
					fileset.add(outd.getSfilename()); // �õ��ļ���(��ϸ��)
				}
				// 2.���ȸ����ļ��ֿ�
				for (String fe : fileset) {
					Map<String, List<IDto>> newmap = new HashMap<String, List<IDto>>();
					// Ȼ�����<������>�����зֿ�
					for (String st : bnkset) {
						List<IDto> nlist = new ArrayList<IDto>();
						for (IDto dto : paylist) {
							TbsTvPayoutDto bkdto = (TbsTvPayoutDto) dto;
							if (st.equals(bkdto.getSpayeeopnbnkno())
									&& fe.equals(bkdto.getSfilename())) {
								nlist.add(bkdto);
							}
						}
						newmap.put(st, nlist);
					}
					filemap.put(fe, newmap);
				}
				// 3.����ķְ�����
				for (String fe : fileset) {
					Map<String, List<IDto>> smap = filemap.get(fe);
					for (String st : bnkset) {
						List<IDto> detlist = smap.get(st);
						if (detlist != null && detlist.size() > 0) {
							int li_Detail = (detlist.size() - 1) / 1000;
							for (int k = 0; k <= li_Detail; k++) {
								int li_TempCount = 0;
								if (li_Detail == k) {
									li_TempCount = detlist.size();
								} else {
									li_TempCount = (k + 1) * 1000;
								}
								tmpPackNo = SequenceGenerator
										.changePackNoForLocal(SequenceGenerator
												.getNextByDb2(
														SequenceName.FILENAME_PACKNO_REF_SEQ,
														SequenceName.TRAID_SEQ_CACHE,
														SequenceName.TRAID_SEQ_STARTWITH,
														MsgConstant.SEQUENCE_MAX_DEF_VALUE));
								String trecode = "";
								for (int j = k * 1000; j < li_TempCount; j++) {
									TbsTvPayoutDto bkdto = (TbsTvPayoutDto) detlist
											.get(j);
									Long i=bkdto.getIvousrlno();
									trecode = bkdto.getStrecode();
									// ���ð���ˮ��
									bkdto.setSpackageno(tmpPackNo);
									// ���н��ͳ�ƣ����ڷְ�
									famtPack = famtPack.add(bkdto.getFamt());
									// ����������ͳ��
									fbcount++;
									// ���������������ɵ�DTO����
									fadtos.add(bkdto);
								}
								// �����ļ��������ˮ�Ŷ�Ӧ��ϵ
								TvFilepackagerefDto packdto = new TvFilepackagerefDto();
								packdto.setSorgcode(getLoginInfo().getSorgcode());
								packdto.setStrecode(trecode);
								packdto.setSfilename(fe);
								TsConvertfinorgDto condto = new TsConvertfinorgDto();
								condto.setSorgcode(getLoginInfo().getSorgcode());
								condto.setStrecode(trecode);
								List conlist = CommonFacade.getODB()
										.findRsByDtoWithUR(condto);
								if (conlist == null || conlist.size() == 0) {
									throw new ITFEBizException("ʵ���ʽ��ļ�[" + fe
											+ "] �и��ݺ���������룺"
											+ getLoginInfo().getSorgcode()
											+ "�� ����������룺" + trecode
											+ " û���� '����������Ϣά������'���ҵ���Ӧ�����������룬���֤");
								}
								TsConvertfinorgDto gd = (TsConvertfinorgDto) conlist
										.get(0);
								packdto.setStaxorgcode(gd.getSfinorgcode());
								packdto.setScommitdate(TimeFacade
										.getCurrentStringTime());
								packdto.setSaccdate(TimeFacade
										.getCurrentStringTime());
								packdto.setSpackageno(tmpPackNo);
								packdto.setSoperationtypecode(biztype);
								packdto.setIcount(fbcount);
								packdto.setNmoney(famtPack);
								packdto
										.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
								packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
								packdto.setSusercode(getLoginInfo().getSuserCode());
								packdto.setImodicount(0);
								fbcount = 0;
								famtPack = new BigDecimal("0.00");
								packdtos.add(packdto);
							}
						}
					}
				}
			}
		}
		tdto.setFatherDtos(fadtos);
		tdto.setPackDtos(packdtos);	
		return tdto;
	}

	
	/**
	 * ���������˻� ����ԭƾ֤��ŷְ�
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws SequenceException
	 * @throws ITFEBizException
	 */
	public MulitTableDto splitPackageWithVouno(List<IDto> paylist,List<IDto> sonlists, String biztype)
			throws JAFDatabaseException, ValidateException, SequenceException,
			ITFEBizException {
		MulitTableDto tdto = new MulitTableDto();
		List<IDto> packdtos = new ArrayList<IDto>(); // �ְ�����
		if (paylist != null && paylist.size() > 0) {
			for (IDto _dto :paylist) {
				TbsTvBnkpayMainDto maindto = (TbsTvBnkpayMainDto) _dto;
		
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSorgcode(maindto.getSbookorgcode());
			packdto.setStrecode(maindto.getStrecode());
			packdto.setSfilename(maindto.getSfilename());
			HashMap<String, TsConvertfinorgDto> mapFincInfo = SrvCacheFacade.cacheFincInfo(maindto.getSbookorgcode());
			String taxorg =mapFincInfo.get(maindto.getStrecode()).getSfinorgcode();
			if (null ==taxorg) {
				throw new ITFEBizException("���������˻��ļ���[" 
						+ " ������������룺" + maindto.getStrecode()
						+ " �� '����������Ϣά������'��û���ҵ���Ӧ�����������룬���֤");
			}
			packdto.setStaxorgcode(taxorg);
			packdto.setScommitdate(TimeFacade
					.getCurrentStringTime());
			packdto.setSaccdate(TimeFacade
					.getCurrentStringTime());
			packdto.setSpackageno(maindto.getSpackno());
			packdto.setSoperationtypecode(maindto.getSbiztype());
			packdto.setIcount(1);
			packdto.setNmoney(maindto.getFsmallsumamt().add(maindto.getFzerosumamt()));
			packdto
					.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
			packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
			packdto.setSusercode(getLoginInfo().getSuserCode());
			packdto.setImodicount(0);
			packdtos.add(packdto);
			}
		}
		tdto.setFatherDtos(paylist);
		tdto.setSonDtos(sonlists);
		tdto.setPackDtos(packdtos);	
		return tdto;
	}
	
	/**
	 * �õ�Ԥ���Ŀ
	 * 
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public Map<String, TsBudgetsubjectDto> makeSubjectMap(String orgcode)
			throws ITFEBizException {
		try {
			return SrvCacheFacade.cacheTsBdgsbtInfo(orgcode);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ȡ��Ŀ�����쳣!");
		}
	}
	
	/**
	 * ɾ���������ļ�
	 * @throws ITFEBizException 
	 */
	public void deleteFileOnServer(String fileName) throws ITFEBizException {
		//ɾ�������ļ�
		try {
			if(new File(fileName).exists()) {
				FileUtil.getInstance().deleteFile(fileName);
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������������ļ������쳣", e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������������ļ������쳣", e);
		} 
	}
	
	/**
	 * ����ҵ�����ͻ�ȡ��Ӧ��ҵ������
	 */
	public static String getBizname(String bizType){
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
		    return "ֱ��֧������ļ�";
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			return "��Ȩ֧������ļ�";
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// ʵ���ʽ�
			return "ʵ���ʽ��ļ�";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// ����
			return "�����ļ�";
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// �˿�
			return "�˿��ļ�";
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// ��������
			return "��������";
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// ���а���֧��
			return "���а���ֱ��֧���ļ�";
		}else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY.equals(bizType)
				||BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY.equals(bizType)) {// ���а���֧����������
			return "���а���֧�����������ļ�";
		}else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK.equals(bizType)
				||BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK.equals(bizType)) {// ���а���֧����������
			return "���а���֧�����������˻��ļ�";
		}
		return bizType;
		
	}
	
	/**
	 * 
	 */
	public void createFileDto(MulitTableDto multidto) throws ITFEBizException {
		// TODO Auto-generated method stub

	}

	public void updatefundinfo(TvPayreckBankBackDto payreckbackdto)
			throws ITFEBizException {
	
	}
}
