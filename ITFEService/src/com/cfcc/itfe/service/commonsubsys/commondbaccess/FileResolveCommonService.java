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
 * @time 12-02-23 09:57:50 codecomment: 解析文件公共服务
 */

public class FileResolveCommonService extends AbstractFileResolveCommonService {
	private static Log log = LogFactory.getLog(FileResolveCommonService.class);

	/**
	 * 解析文件
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
		

		// 1.首先得到进行文件解析服务的类实例
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

		// 2.对文件进行解析
		MulitTableDto mudto = null;
		// 存放错误信息
		List<String> errorList = new ArrayList<String>();

		// 存放错误文件个数
		int errorFileCount = 0;
		
		// 存放错误文件名称
		List<String> errFileNameList = new ArrayList<String>();

		// 用于厦门接口
		MulitTableDto allmudto = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());

		// 用于额度多文件组包
		MulitTableDto allPaydto = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());
		
		// 用于实拨资金文件组包(主要用于判断多文件之间凭证编号)
		MulitTableDto allPay17dto = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());
		
		MulitTableDto allHksqdtos = new MulitTableDto(new ArrayList<IDto>(),
				new ArrayList<IDto>(), new ArrayList<IDto>(),
				new ArrayList<String>());

		MulitTableDto allmudtoforxm = new MulitTableDto();

		List<MulitTableDto> l=new ArrayList<MulitTableDto>();
		List<IDto> li = new ArrayList<IDto>();

		// 得到文件上传配置
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
				.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");

		// 文件上传根路径
		String root = sysconfig.getRoot();

		// 得到科目
		Map<String, TsBudgetsubjectDto> smap = this
				.makeSubjectMap(getLoginInfo().getSorgcode());

		StringBuffer sb; // 放置服务器上传根路径

		for (Object obj : filelist) {

			// 将前台传来的相对路径加上根路径
			sb = new StringBuffer(root);

			sb.append(obj.toString().replace("/", File.separator).replace("\\",
					File.separator));

			String file = sb.toString();

			String bztype="";
			File fl = new File(file);
			//获取文件的业务类型
			if(biztype.equals("")){
				String tmpfilename_new = fl.getName().trim().toLowerCase();
				String tmpfilename = tmpfilename_new.replace(".txt", "");
				if (tmpfilename.length() == 13 || tmpfilename.length() == 15) {
					// 8位日期，2位批次号，2位业务类型，1位调整期标志组成
					if (tmpfilename.length() == 13) {
						bztype=tmpfilename.substring(10, 12); // 业务类型
					} else {
						bztype=tmpfilename.substring(12, 14); // 业务类型
					}
				}
			}
			
			//划款申请退回，需要单独做处理 增加一个传入参数
			 if (bztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)|| bztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				 idto=paramdto;
				 paramdto=null;
			 }
			// 修改传入的参数，给前台传入的null的param赋值，如果前台传入不是null，则不再赋值，解决支持四川需求按照国库设置密钥进行解密
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
			//临时存储文件路径
			String tmpFile = file;
			// 根据登陆信息中得 加密方式、密钥设置模式等配置解密对应的文件，修改日期20120629
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
				
				if(StateConstant.ENCRYPT_FAIL_INFO.equals(file)) { //解密或验签失败
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
				log.error(fl.getName()+"文件出错 :\n"+e.getMessage());
				throw new ITFEBizException(fl.getName()+"文件出错 :\n",e);
			}catch(FileOperateException e)
			{
				log.error(fl.getName()+"文件出错 :\n"+e.getMessage());
				throw new ITFEBizException(fl.getName()+"文件出错 :\n",e);
			}catch(ITFEBizException e)
			{
				log.error(fl.getName()+"文件出错 :\n"+e.getMessage());
				throw new ITFEBizException(fl.getName()+"文件出错 :\n"+e.getMessage(),e);
			}catch (Exception e2) {
				log.error(e2);
				throw new ITFEBizException("服务器端解密文件出错1！", e2);
			}
			if (mudto.getErrorList() != null && mudto.getErrorList().size() > 0) {
				errorList.addAll(mudto.getErrorList());
				errFileNameList.add(obj.toString());
				errorFileCount++;
				//删除错误文件
				this.deleteFileOnServer(file);
				continue;
			}
			if(mudto.getFatherDtos()!=null&&mudto.getFatherDtos().size()>0)
			{
					IDto dateDto = mudto.getFatherDtos().get(0);
					Date vDate = Date.valueOf((DateUtil.dateBefore(TimeFacade.getCurrentDateTime(), 20, "D")));//YYYY-MM-DD
					Date voudate = null;
					String svouno = null;
					if(dateDto instanceof TbsTvPbcpayDto)//mainDto.setDvoucher(CommonUtil.strToDate(strs[12])); //凭证日期
					{
						voudate = ((TbsTvPbcpayDto)dateDto).getDvoucher();
						svouno = ((TbsTvPbcpayDto)dateDto).getSvouno();//凭证编号
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20天前的文件不能导入系统!");
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
									throw new ITFEBizException("凭证号为"+svouno+"的数据系统已经成功处理过!");
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
							throw new ITFEBizException("20天前的文件不能导入系统!");
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
									throw new ITFEBizException("凭证号为"+svouno+"的数据系统已经成功处理过!");
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
							throw new ITFEBizException("20天前的文件不能导入系统!");
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
									throw new ITFEBizException("凭证号为"+svouno+"的数据系统已经成功处理过!");
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
							throw new ITFEBizException("20天前的文件不能导入系统!");
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
									throw new ITFEBizException("凭证号为"+svouno+"的数据系统已经成功处理过!");
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
										throw new ITFEBizException("凭证号为"+svouno+"的数据系统已经成功处理过!");
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
							throw new ITFEBizException("20天前的文件不能导入系统!");
						}
					}else if(dateDto instanceof TbsTvBnkpayMainDto)
					{
						voudate = ((TbsTvBnkpayMainDto)dateDto).getDvoucher();
						svouno = ((TbsTvBnkpayMainDto)dateDto).getSvouno();
						if(voudate.before(vDate))
						{
							throw new ITFEBizException("20天前的文件不能导入系统!");
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
									throw new ITFEBizException("凭证号为"+svouno+"的数据系统已经成功处理过!");
								}
							} catch (Exception e) {
							}
						}
					}
			}
			if (filekind.equals(MsgConstant.TUIKU_XIAMEN_DAORU)
					|| filekind.equals(MsgConstant.SHIBO_XIAMEN_DAORU)) {

				if (file.toLowerCase().endsWith("txt")) { // 明细岗接口

					allmudto.getFatherDtos().addAll(mudto.getFatherDtos());

					allmudto.getPackDtos().addAll(mudto.getPackDtos());

					allmudto.getVoulist().addAll(mudto.getVoulist());

					try {

						this.checkVouRepeat(allmudto, filekind, fl.getName()); // 当日凭证编号重复性校验

					} catch (JAFDatabaseException e) {

						log.error(e);

						throw new ITFEBizException("凭证编号校验异常", e);

					} catch (ValidateException e) {

						log.error(e);

						throw new ITFEBizException("凭证编号校验异常", e);

					}

				} else if (file.toLowerCase().endsWith("tmp")) { // 资金岗接口

					allmudto.getSonDtos().addAll(mudto.getSonDtos());

				}

			} else if (filekind.equals(MsgConstant.PILIANG_DAORU)) {

				this.createFileDto(mudto, filekind); // 批量拨付 直接保存

				/**
				 * 直接支付额度与授权支付额度需按小于1000个文件组一个包
				 */
			} else if (filekind.equals(MsgConstant.ZHIJIE_DAORU)
					|| filekind.equals(MsgConstant.SHOUQUAN_DAORU)) {
				// 保存之前先校验当日凭证编号是否重复
				try {
					this.checkVouRepeat(mudto, filekind, fl.getName());
					
					allPaydto.getFatherDtos().addAll(mudto.getFatherDtos());
					allPaydto.getPackDtos().addAll(mudto.getPackDtos());
					allPaydto.getSonDtos().addAll(mudto.getSonDtos());
					allPaydto.getVoulist().addAll(mudto.getVoulist());
					
					//此处的校验 为校验正在导入的files之间的凭证编号重复 20120705修改
					this.checkVouRepeat(allPaydto, filekind, fl.getName());
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("凭证编号校验异常", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("凭证编号校验异常", e);
				}
				
			} else if(filekind.equals(MsgConstant.TAXORG_PAYOUT)||filekind.equals(MsgConstant.MSG_NO_3452)||filekind.equals(MsgConstant.MSG_NO_3403)||filekind.equals("3208")||filekind.equals("3251")||filekind.equals("3210")){
//			} else if(filekind.equals(MsgConstant.TAXORG_PAYOUT)||filekind.equals(MsgConstant.MSG_NO_3403)||filekind.equals("3208")||filekind.equals("3251")||filekind.equals("3210")){
				this.createFileDto(mudto, filekind);
			}else if(filekind.equals(MsgConstant.MSG_NO_JZZF)){
				this.createFileDto(mudto, filekind);
			}else if (filekind.equals(MsgConstant.APPLYPAY_DAORU)) {//划款申请
				// 用于商行办理支付划款申请文件组包
				MulitTableDto allHksqdto = new MulitTableDto(new ArrayList<IDto>(),
						new ArrayList<IDto>(), new ArrayList<IDto>(),
						new ArrayList<String>());
				// 保存之前先校验当日凭证编号是否重复
				try {
					
					allHksqdto.getFatherDtos().addAll(mudto.getFatherDtos());
					allHksqdto.getSonDtos().addAll(mudto.getSonDtos());
					allHksqdto.getVoulist().addAll(mudto.getVoulist());
					
					//此处的校验 为校验正在导入的files之间的凭证编号重复 20120705修改
					this.checkVouRepeat(allHksqdto, filekind, fl.getName());
					//划款申请按收款人开户行行号行进行分包
					if(null != allHksqdto && allHksqdto.getFatherDtos() !=null && allHksqdto.getFatherDtos().size() > 0) {
						allHksqdto = this.splitPackageWithBank(mudto.getFatherDtos(), bztype);
						allHksqdto.setSonDtos(mudto.getSonDtos());
//						this.createFileDto(allHksqdto, filekind); // 保存按收款行分包后的数据
						l.add(allHksqdto);
					}
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("划款申请分包异常！", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("划款申请分包异常！", e);
				} catch (SequenceException e) {
					log.error(e);
					throw new ITFEBizException("划款申请分包异常！", e);
				}
			}else if (filekind.equals(MsgConstant.APPLYPAY_BACK_DAORU)) {//划款申请退回
				// 用于商行办理支付划款申请文件组包
				MulitTableDto allHksqdto = new MulitTableDto(new ArrayList<IDto>(),
						new ArrayList<IDto>(), new ArrayList<IDto>(),
						new ArrayList<String>());
				// 保存之前先校验当日凭证编号是否重复
				try {
					
					allHksqdto.getFatherDtos().addAll(mudto.getFatherDtos());
					allHksqdto.getSonDtos().addAll(mudto.getSonDtos());
					allHksqdto.getVoulist().addAll(mudto.getVoulist());
					
					//此处的校验 为校验正在导入的files之间的凭证编号重复 20120705修改
					this.checkVouRepeat(allHksqdto, filekind, fl.getName());
					//划款申请按原凭证编号行进行分包
					if(null != allHksqdto && allHksqdto.getFatherDtos() !=null && allHksqdto.getFatherDtos().size() > 0) {
						allHksqdto = this.splitPackageWithVouno(mudto.getFatherDtos(), mudto.getSonDtos(),bztype);
						l.add(allHksqdto);
					}
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("划款申请退回分包异常！", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("划款申请退回分包异常！", e);
				} catch (SequenceException e) {
					log.error(e);
					throw new ITFEBizException("划款申请退回分包异常！", e);
				}
			}else if (filekind.startsWith(MsgConstant.SHIBO_FUJIAN_DAORU)) {//校验福建工资导入（允许多个凭证编号）
				try {
					// 保存之前先校验当日凭证编号是否重复
					this.checkVouRepeatForFJ(mudto, filekind, fl.getName());
					
					allPay17dto.getFatherDtos().addAll(mudto.getFatherDtos());
					allPay17dto.getPackDtos().addAll(mudto.getPackDtos());
					allPay17dto.getVoulist().addAll(mudto.getVoulist());
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("凭证编号校验异常", e);
				} catch (ValidateException e) {
					log.error(e);
					throw new ITFEBizException("凭证编号校验异常", e);
				}
			}else {

				try {

					this.checkVouRepeat(mudto, filekind, fl.getName());// 保存之前先校验当日凭证编号是否重复
					
					allPay17dto.getFatherDtos().addAll(mudto.getFatherDtos());
					allPay17dto.getPackDtos().addAll(mudto.getPackDtos());
					allPay17dto.getVoulist().addAll(mudto.getVoulist());
					
					//此处的校验 为校验正在导入的files之间的凭证编号重复 20120801修改
					this.checkVouRepeat(allPay17dto, filekind, fl.getName());

				} catch (JAFDatabaseException e) {

					log.error(e);

					throw new ITFEBizException("凭证编号校验异常", e);

				} catch (ValidateException e) {

					log.error(e);

					throw new ITFEBizException("凭证编号校验异常", e);

				}
				/*
				this.createFileDto(mudto, filekind); // 所有都校验完毕，保存
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
		 * 对于直接支付额度与授权支付额度，多个文件数据按不同国库主体代码组包
		 */
		if ((allPaydto.getFatherDtos() != null
				&& allPaydto.getFatherDtos().size() > 0)) {
			setPayDataOnePackage(allPaydto, filekind);
		}

		// 3.由于实拨和退库的厦门接口分明细岗和资金岗，需要进行数据填充,
		// 根据收款账号和金额进行关联
		if (allmudto.getFatherDtos() != null
				&& allmudto.getFatherDtos().size() > 0) {
			for (IDto dto : allmudto.getFatherDtos()) {
				if (dto instanceof TbsTvPayoutDto) { // 实拨资金XM
					TbsTvPayoutDto pdto = (TbsTvPayoutDto) dto;
					for (IDto dto1 : allmudto.getSonDtos()) {
						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
						if (pdto.getSpayeeacct().trim().equals(
								fidto.getSpayeeacct().trim())) {
							if (pdto.getFamt().compareTo(fidto.getFamt()) == 0) {
								pdto.setSpayeeopnbnkno(fidto
										.getSpayeeopnbnkno()); // 填入资金岗的收款人开户行行号
								pdto.setSpayeename(fidto.getSpayeename()); // 填入资金岗收款人名称
								pdto.setSbdgorgname(fidto.getSpayeename()); // 填入预算单位名称
								pdto.setSpayeebankno(fidto.getSrcvbnkno()); // 填入<清算行行号>改为填入接收行行号
								pdto.setSaddword(fidto.getSaddword());// 附言
								if ("".equals(pdto.getSbdgorgcode().trim())) { // 如果预算单位代码为空则填入
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
				} else if (dto instanceof TbsTvDwbkDto) { // 退库XM
					TbsTvDwbkDto dwdto = (TbsTvDwbkDto) dto;
					for (IDto dto1 : allmudto.getSonDtos()) {
						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
						if (dwdto.getSpayeeacct().trim().equals(
								fidto.getSpayeeacct().trim())) {
							if (dwdto.getFamt().compareTo(fidto.getFamt()) == 0) {
								dwdto.setSpayeeopnbnkno(fidto
										.getSpayeeopnbnkno()); // 填入资金岗的收款人开户行行号
								dwdto.setSpayeename(fidto.getSpayeename()); // 填入资金岗收款人名称
								li.add(dwdto);
								break;
							}
						}
					}
				}
			}
			// 如果填充完之后的dto数目小于没有填充之前
			if (allmudto.getFatherDtos().size() != li.size()) {

				throw new ITFEBizException("通过明细岗付款人账号和金额未找到对应资金岗记录,请查证");

			}

			allmudto.setSonDtos(null); // 将不需要写入数据库的资金岗清空

			if (filekind.equals(MsgConstant.SHIBO_XIAMEN_DAORU)) { // 厦门实拨

				try {

					allmudtoforxm = this.splitPackageWithBank(li, biztype); // 厦门实拨需按清算行分包

				} catch (JAFDatabaseException e) {

					log.error(e);

					throw new ITFEBizException("数据解析异常", e);

				} catch (ValidateException e) {

					log.error(e);

					throw new ITFEBizException("数据解析异常", e);

				} catch (SequenceException e) {

					log.error(e);

					throw new ITFEBizException("数据解析异常", e);

				}

				this.createFileDto(allmudtoforxm, filekind); // 保存按清算行分包后的数据

				return allmudtoforxm;

			} else if (filekind.equals(MsgConstant.TUIKU_XIAMEN_DAORU)) { // 厦门退库

				allmudto.setFatherDtos(li); // 将填充完数据的List放入

				this.createFileDto(allmudto, filekind); // 保存数据

				return allmudto;

			}
		}
		// 在返回的时候需要将所有错误信息/错误文件个数/错误文件名返回
		if(null == mudto || null == mudto.getErrorList()) {
			mudto = new MulitTableDto();
		}
		mudto.setErrorList(errorList);
		mudto.setErrorCount(errorFileCount);
		mudto.setErrNameList(errFileNameList);
		return mudto;
	}

	/**
	 * 对于直接支付额度与授权支付额度，多个文件数据按不同国库主体代码组包
	 * 
	 * @param mulTbldto所导入文件数据
	 * @param filekind文件类型
	 * @throws ITFEBizException
	 */
	private void setPayDataOnePackage(MulitTableDto mulTbldto, String filekind)
			throws ITFEBizException {

		// 用于存放不同的国库主体代码
		Set<String> treCodeSet = new HashSet<String>();
		// 取出不同国库主体代码
		if (mulTbldto.getPackDtos() != null
				&& mulTbldto.getPackDtos().size() > 0) {
			for (IDto dto : mulTbldto.getPackDtos()) {
				if (dto instanceof TvFilepackagerefDto) {
					TvFilepackagerefDto filePagrefdto = (TvFilepackagerefDto) dto;
					// 国库主体代码
					treCodeSet.add(filePagrefdto.getStrecode());
				}

			}
		}
		// 获取包流水号
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
				throw new ITFEBizException("获取包流水号异常 \n" + e.getMessage(), e);
			}
			// 设置文件与包对应关系的包流水号
			for (int i = 0; i < mulTbldto.getPackDtos().size(); i++) {
				// 同国库主体代码的设置同一个包流水号
				if (streCode.equals(((TvFilepackagerefDto) mulTbldto
						.getPackDtos().get(i)).getStrecode())) {
					((TvFilepackagerefDto) mulTbldto.getPackDtos().get(i))
							.setSpackageno(tmpPackNo);
				}
			}
			// 设置主表的包流水号
			for (int i = 0; i < mulTbldto.getFatherDtos().size(); i++) {
				/**
				 * 同国库主体代码的设置同一个包流水号
				 */
				// 直接支付额度
				if (MsgConstant.ZHIJIE_DAORU.equals(filekind)) {

					if (streCode.equals(((TbsTvDirectpayplanMainDto) mulTbldto
							.getFatherDtos().get(i)).getStrecode())) {
						((TbsTvDirectpayplanMainDto) mulTbldto.getFatherDtos()
								.get(i)).setSpackageno(tmpPackNo);
					}
					// 授权支付额度
				} else if (MsgConstant.SHOUQUAN_DAORU.equals(filekind)) {
					if (streCode.equals(((TbsTvGrantpayplanMainDto) mulTbldto
							.getFatherDtos().get(i)).getStrecode())) {
						((TbsTvGrantpayplanMainDto) mulTbldto.getFatherDtos()
								.get(i)).setSpackageno(tmpPackNo);
					}
				}
			}
		}
		// 保存额度数据
		this.createFileDto(mulTbldto, filekind);
	}

	/**
	 * 提供对MulitTableDto 类型的数据进行直接保存
	 */
	public void createFileDto(MulitTableDto multidto, String filekind)
			throws ITFEBizException {

		// 保存文件解析的结果
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

			throw new ITFEBizException("文件解析内容保存异常", e);

		}
	}

	/**
	 * 判断当日凭证编号是否重复
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public void checkVouRepeat(MulitTableDto mdto, String filekind,
			String filename) throws JAFDatabaseException, ValidateException,
			ITFEBizException {

		List<String> voulist = new ArrayList<String>(); // 初始化

		Date today = TimeFacade.getCurrentDateTime(); // 当前日期

		String bookorgcode = getLoginInfo().getSorgcode();

		int oldSize = 0;

		int newSize = 0;

		Set<String> sets = new HashSet<String>();

		if (mdto != null && mdto.getVoulist().size() > 0) {

			voulist.addAll(mdto.getVoulist()); // 凭证编号集

		} else {

			return;

		}

		if (filekind.equals(MsgConstant.ZHIJIE_DAORU)) {
			// 直接支付额度
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
						throw new ITFEBizException("直接支付额度文件[" + filename
								+ "]中凭证编号[" + item.split(",")[1]
								+ "]和正在导入的其他文件凭证编号重复,请查证");
					} else {
						throw new ITFEBizException("直接支付额度文件[" + filename
								+ "]中凭证编号[" + item.split(",")[1]
								+ "]和当日已导入文件凭证编号重复,请查证");
					}
				}
			}

		} else if (filekind.equals(MsgConstant.SHOUQUAN_DAORU)) {
			// 授权支付额度
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
						throw new ITFEBizException("授权支付额度文件[" + filename
								+ "]中凭证编号[" + item.split(",")[1]
								+ "]和正在导入的其他文件凭证编号重复,请查证");
					} else {
						throw new ITFEBizException("授权支付额度文件[" + filename
								+ "]中凭证编号[" + item.split(",")[1]
								+ "]和当日已导入文件凭证编号重复,请查证");
					}
				}
			}

		} else if (filekind.startsWith(MsgConstant.MSG_NO_1104)) {
			// 退库
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
						throw new ITFEBizException("退库文件[" + filename
								+ "]中凭证编号[" + item.split(",")[1]
								+ "]和当日已导入凭证编号重复,请查证");
					}
				}
			}

		} else if (filekind.startsWith(MsgConstant.MSG_NO_5101)) {
			// 实拨资金
			//判断文件间凭证编号重复
			int oldSize_tmp = 0;
			int newSize_tmp = 0;
			Set<String> sets_tmp = new HashSet<String>();
			for(String vou : voulist) {
				oldSize_tmp = sets_tmp.size();
				sets_tmp.add(vou.trim());
				newSize_tmp = sets_tmp.size();
				if (newSize_tmp == oldSize_tmp) {
					throw new ITFEBizException("实拨资金文件[" + filename
							+ "]中凭证编号[" + vou.split(",")[1]
							+ "]和正在导入的其他文件凭证编号重复,请查证");
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
					throw new ITFEBizException("实拨资金文件[" + filename
							+ "]中凭证编号[" + item.split(",")[1]
							+ "]和当日已导入凭证编号重复,请查证");
				}
			}

		} else if (filekind.equals(MsgConstant.GENGZHENG_DAORU)) {
			// 更正
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
						throw new ITFEBizException("更正文件[" + filename
								+ "]中凭证编号[" + item + "]和当日已导入凭证编号重复,请查证");
					}
				}
			}
		} else if (filekind.equals(MsgConstant.TAX_FREE_DAORU)) {
			// 免抵调
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
						throw new ITFEBizException("免抵调文件[" + filename
								+ "]中凭证编号[" + item + "]和当日已导入凭证编号重复,请查证");
					}
				}
			}
		} else if (filekind.equals(MsgConstant.PBC_DIRECT_IMPORT)) {
			// 人行办理直接支付
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
						throw new ITFEBizException("人行办理直接支付文件[" + filename
								+ "]中凭证编号[" + item + "]和已导入凭证编号重复,请查证");
					}
				}
			}else if (filekind.equals(MsgConstant.APPLYPAY_DAORU)
					||filekind.equals(MsgConstant.APPLYPAY_BACK_DAORU)) {
				// 商行办理支付划款申请
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
							throw new ITFEBizException("商行办理支付划款申请文件[" + filename
									+ "]中凭证编号[" + item.split(",")[1]
									+ "]和正在导入的其他文件凭证编号重复,请查证");
						} else {
							throw new ITFEBizException("商行办理支付划款申请文件[" + filename
									+ "]中凭证编号[" + item.split(",")[1]
									+ "]和当日已导入文件凭证编号重复,请查证");
						}
					}
				}
			}
		}

	}
	
	
	
	
	/**
	 * 判断当日凭证编号是否重复(福建工资导入)
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public void checkVouRepeatForFJ(MulitTableDto mdto, String filekind,
			String filename) throws JAFDatabaseException, ValidateException,
			ITFEBizException {
		List<String> voulist = new ArrayList<String>(); // 初始化
		Date today = TimeFacade.getCurrentDateTime(); // 当前日期
		String bookorgcode = getLoginInfo().getSorgcode();
		
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		if (mdto != null && mdto.getVoulist().size() > 0) {
			voulist.addAll(mdto.getVoulist()); // 凭证编号集
		} else {
			return;
		}

		if (filekind.startsWith(MsgConstant.MSG_NO_5101)) {
			//实拨资金判断文件间凭证编号重复
			int oldSize_tmp = 0;
			int newSize_tmp = 0;
			Set<String> sets_tmp = new HashSet<String>();
			for(String vou : voulist) {
				oldSize_tmp = sets_tmp.size();
				sets_tmp.add(vou.trim());
				newSize_tmp = sets_tmp.size();
				if (newSize_tmp == oldSize_tmp) {
					throw new ITFEBizException("实拨资金文件[" + filename
							+ "]中凭证编号[" + vou.split(",")[1]
							+ "]和正在导入的其他文件凭证编号重复,请查证");
				}
			}
			
			TbsTvPayoutDto dto = new TbsTvPayoutDto();
			dto.setDaccept(today);
			dto.setSbookorgcode(bookorgcode);
			List<TbsTvPayoutDto> paylist = (List<TbsTvPayoutDto>) CommonFacade
					.getODB().findRsByDtoWithUR(dto);
			List<String> allvoulist = new ArrayList<String>(); // 初始化
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
					throw new ITFEBizException("实拨资金文件[" + filename
							+ "]中凭证编号[" + item.split(",")[1]
							+ "]和当日已导入凭证编号重复,请查证");
				}
			}
		}
	}
	
	
	

	/**
	 * 对实拨资金 厦门接口进行按清算行拆包
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

		String tmpPackNo = ""; // 包流水号
		BigDecimal famtPack = new BigDecimal("0.00"); // 包内总金额

		int fbcount = 0; // 包内总笔数
		Set<String> bnkset = new HashSet<String>(); // 清算行集
		Set<String> fileset = new HashSet<String>(); // 文件集
		Map<String, Map<String, List<IDto>>> filemap = new HashMap<String, Map<String, List<IDto>>>();
		List<IDto> fadtos = new ArrayList<IDto>(); // 记录数
		List<IDto> packdtos = new ArrayList<IDto>(); // 分包数量
		List<IDto> hasBankdtos = new ArrayList<IDto>(); //存放有收款人开户行行号的dto
		List<IDto> noBankdtos = new ArrayList<IDto>(); //存放没有收款人开户行行号的dto

		if (paylist != null && paylist.size() > 0) {
			if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					||biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){

				// 1.首先得到<清算行>开户行数量(5.3修改)
				for (IDto pay : paylist) {
					TbsTvBnkpayMainDto outd = (TbsTvBnkpayMainDto) pay;
					if(null != outd.getSpayeeopnbnkno() && !"".equals(outd.getSpayeeopnbnkno())) { //收款人开户行行号为空的先不做
						bnkset.add(outd.getSpayeeopnbnkno()); // <得到清算行>改为按开户行分包
						fileset.add(outd.getSfilename()); // 得到文件数(明细岗)
						hasBankdtos.add(outd);
					}else {
						noBankdtos.add(outd);
					}
				}
				// 2.首先根据文件分开
				for (String fe : fileset) {
					Map<String, List<IDto>> newmap = new HashMap<String, List<IDto>>();
					// 然后根据<清算行>开户行分开
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
				// 3.具体的分包过程
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
									// 设置包流水号
									bkdto.setSpackno(tmpPackNo);
									// 进行金额统计，用于分包
									famtPack = famtPack.add(bkdto.getFsmallsumamt().add(bkdto.getFzerosumamt()));
									// 进行数量的统计
									fbcount++;
									// 将所有填充数据完成的DTO保存
									fadtos.add(bkdto);
								}
								// 生成文件名与包流水号对应关系
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
									throw new ITFEBizException("划款申请文件[" + fe
											+ "] 中根据核算主体代码："
											+ getLoginInfo().getSorgcode()
											+ "、 国库主体代码：" + trecode
											+ " 没有在 '财政机构信息维护参数'中找到对应财政机构代码，请查证");
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
				//对收款人开户行行号没有补录的记录进行处理
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
						// 生成文件名与包流水号对应关系
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
							throw new ITFEBizException("划款申请文件[" + noBankMainDto.getSfilename()
									+ "] 中根据核算主体代码："
									+ getLoginInfo().getSorgcode()
									+ "、 国库主体代码：" + trecode
									+ " 没有在 '财政机构信息维护参数'中找到对应财政机构代码，请查证");
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
				// 1.首先得到<清算行>开户行数量(5.3修改)
				for (IDto pay : paylist) {
					TbsTvPayoutDto outd = (TbsTvPayoutDto) pay;
					bnkset.add(outd.getSpayeeopnbnkno()); // <得到清算行>改为按开户行分包
					fileset.add(outd.getSfilename()); // 得到文件数(明细岗)
				}
				// 2.首先根据文件分开
				for (String fe : fileset) {
					Map<String, List<IDto>> newmap = new HashMap<String, List<IDto>>();
					// 然后根据<清算行>开户行分开
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
				// 3.具体的分包过程
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
									// 设置包流水号
									bkdto.setSpackageno(tmpPackNo);
									// 进行金额统计，用于分包
									famtPack = famtPack.add(bkdto.getFamt());
									// 进行数量的统计
									fbcount++;
									// 将所有填充数据完成的DTO保存
									fadtos.add(bkdto);
								}
								// 生成文件名与包流水号对应关系
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
									throw new ITFEBizException("实拨资金文件[" + fe
											+ "] 中根据核算主体代码："
											+ getLoginInfo().getSorgcode()
											+ "、 国库主体代码：" + trecode
											+ " 没有在 '财政机构信息维护参数'中找到对应财政机构代码，请查证");
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
	 * 划款申请退回 按照原凭证编号分包
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
		List<IDto> packdtos = new ArrayList<IDto>(); // 分包数量
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
				throw new ITFEBizException("划款申请退回文件中[" 
						+ " 按国库主体代码：" + maindto.getStrecode()
						+ " 在 '财政机构信息维护参数'中没有找到对应财政机构代码，请查证");
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
	 * 得到预算科目
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
			throw new ITFEBizException("获取科目代码异常!");
		}
	}
	
	/**
	 * 删除服务器文件
	 * @throws ITFEBizException 
	 */
	public void deleteFileOnServer(String fileName) throws ITFEBizException {
		//删除错误文件
		try {
			if(new File(fileName).exists()) {
				FileUtil.getInstance().deleteFile(fileName);
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("删除服务器错误文件操作异常", e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException("删除服务器错误文件操作异常", e);
		} 
	}
	
	/**
	 * 根据业务类型获取对应的业务名称
	 */
	public static String getBizname(String bizType){
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
		    return "直接支付额度文件";
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			return "授权支付额度文件";
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// 实拨资金
			return "实拨资金文件";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// 更正
			return "更正文件";
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
			return "退库文件";
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// 批量拨付
			return "批量拨付";
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行办理支付
			return "人行办理直接支付文件";
		}else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY.equals(bizType)
				||BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY.equals(bizType)) {// 商行办理支付划款申请
			return "商行办理支付划款申请文件";
		}else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK.equals(bizType)
				||BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK.equals(bizType)) {// 商行办理支付划款申请
			return "商行办理支付划款申请退回文件";
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
