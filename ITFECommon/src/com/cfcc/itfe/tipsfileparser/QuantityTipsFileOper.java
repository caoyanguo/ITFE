package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.PiLiangDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.util.ChinaTest;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua 批量拨付文件导入解析类
 */
@SuppressWarnings("unchecked")
public class QuantityTipsFileOper extends AbstractTipsFileOper {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))
			return fileParserGx(file,bookorgCode,userid,biztype,filekind,paramdto,dmap);
		else
			return fileParserOther(file,bookorgCode,userid,biztype,filekind,paramdto,dmap);
	}
	public MulitTableDto fileParserOther(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		PiLiangDto pldto = (PiLiangDto)paramdto;
		MulitTableDto multi = new MulitTableDto();
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		File fi = new File(file);
		String finame =  fi.getName().replaceAll(".tmp", ".pas") ;
		int fbcount = 0; // 分包记数器
		int recordNum = 1; // 记录记录器
		String tmpPackNo = "";
//		String srcvreno = fi.getName().substring(0, 12);
		try {
			List<String[]> fileContent = super.readFile(file, ",");
			String[] castr = fileContent.get(fileContent.size()-1);
			if("</CA>".equals(castr[0].toUpperCase())) {
				//将解析出来的文件中关于CA的部分remove掉
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
			}
			
			/*
			 * 开始分包 ，初始化如下容器
			 */
			Map<String, Map<String,List<String[]>>> splitAllMap = new HashMap<String, Map<String,List<String[]>>>(); //解释为：<付款人账号,Map<接受行,对应记录>
			Set<String> recvSet = new HashSet<String>(); //存放接受行行号
			Set<String> paySet = new HashSet<String>(); //存放付款人账号
			/*1.首先取得付款人账号及接受行行号数量*/
			for(String[] strBuff : fileContent) {
				paySet.add(strBuff[4].trim()); //付款人账号
				recvSet.add(strBuff[1].trim()); //接受行行号
			}
			/*2.然后先按照付款人账号，接受行行号分开*/
			for(String payAcct : paySet) {
				Map<String,List<String[]>> recvSplitMap = new HashMap<String, List<String[]>>(); //存放接收行及对应记录关系
				for(String rbank : recvSet) {
					List<String[]> tempList = new ArrayList<String[]>(); //同付款人、接收行记录
					for(String[] dtoStr : fileContent) {
						if(payAcct.trim().equals(dtoStr[4].trim()) && rbank.trim().equals(dtoStr[1].trim())) {
							tempList.add(dtoStr);
						}
					}
					recvSplitMap.put(rbank, tempList);
				}
				splitAllMap.put(payAcct, recvSplitMap);
			}
			/*3.下面是具体的分包过程，利用equals方法将所有记录在逻辑上分割开，然后分别生成相应包流水号*/
			for(String payAt : paySet) {
				Map<String,List<String[]>> recvSplitMap = splitAllMap.get(payAt); //得到循环中的付款人对应记录
				for(String rebank : recvSet) {
					List<String[]> list = recvSplitMap.get(rebank);
					if(list != null && list.size() > 0) {
						int li_Detail = (list.size()-1) / StateConstant.QUANTITY_PACKAGE_COUNT;
						for (int k = 0; k <= li_Detail; k++) {
							int li_TempCount = 0;
							if (li_Detail == k) {
								li_TempCount = list.size();
							} else {
								li_TempCount = (k + 1) * StateConstant.QUANTITY_PACKAGE_COUNT;
							}
							tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
									.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
											SequenceName.TRAID_SEQ_CACHE,
											SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
							for (int j = k * StateConstant.QUANTITY_PACKAGE_COUNT; j < li_TempCount; j++) {
								String[] strs = (String[]) list.get(j);
								if("<CA>".equals(strs[0].toUpperCase())) {
									break ;
								}
								TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
								dto.setStrecode(pldto.getTrecode());//国库代码
								dto.setSfuncsbtcode(pldto.getFunccode()); //功能类科目代码
								dto.setSbudgettype(pldto.getBudgetype()); //预算种类

								dto.setSsndbnkno(strs[0]);   //发起行行号
								dto.setSrcvbnkno(strs[1]);   //接受行行号

								/*5.3修改为填入接收行行号
								 * dto.setSrcvreckbnkno(srcvreno);
								 */
								dto.setSrcvreckbnkno(strs[1].trim());
								
								dto.setFamt(new BigDecimal(strs[2]));  //金额
								famt = famt.add(new BigDecimal(strs[2]));
								famtPack = famtPack.add(new BigDecimal(strs[2]));
								dto.setSpayeropnbnkno(strs[3]);    //付款人开户行行号
								if(strs[4].length() < 16) {
									dto.setSpayeracct(bookorgCode+strs[4]);   //付款人账号(小于16位则前面加核算主体)
								}else {
									dto.setSpayeracct(strs[4]);//付款人账号
								}					
								dto.setSpayername(strs[5]);   //付款人名称
								dto.setSpayeraddr(strs[6]);   //付款人地址
								dto.setSpayeeopnbnkno(strs[7]);   //收款人开户行行号
								if(ChinaTest.isChinese(strs[8])) {
									throw new ITFEBizException("批量拨付文件["+finame+"] 中收款人账号'"+strs[8]+"' 包含中文字符，请查证!");
								}
								dto.setSpayeeacct(strs[8]);     //收款人账号	
								String errChi_9 = VerifyParamTrans.verifyNotUsableChinese(strs[9]);
								if(null != errChi_9 && !"".equals(errChi_9)) {
									throw new ITFEBizException("批量拨付文件["+finame+"] 中收款人名称存在非法字符："+errChi_9);
								}
								dto.setSpayeename(strs[9]);		//收款人名称
								dto.setSpayeeaddr(strs[10]);	//收款人地址
								dto.setSpaybizkind(strs[11]);   //支付业务种类
								dto.setSbiztype(strs[12]);   //业务类型号
								if(null != strs[13] && !"".equals(strs[13])) {
									String errChi_13 = VerifyParamTrans.verifyNotUsableChinese(strs[13]);
									if(null != errChi_13 && !"".equals(errChi_13)) {
										throw new ITFEBizException("批量拨付文件["+finame+"] 中附言存在非法字符："+errChi_13);
									}
								}
								if(pldto.getType() == null||"".equals(pldto.getType())){
									dto.setSaddword(strs[13]);      //附言
								}else{
									dto.setSaddword("库");      //附言(山东库区移民专用)
								}
								dto.setDentrust(TimeFacade.getCurrentDateTime()); //委托日期不能为空
								dto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期
								if(pldto.getSbdgorgcode()!=null&&!pldto.getSbdgorgcode().equals("")){
									dto.setSbdgorgcode(pldto.getSbdgorgcode());
								}else{
									if(strs[4].length() > 15) {
										dto.setSbdgorgcode(strs[4].substring(11));//预算单位代码不能为空(填充付款人账号)
									}else {
										dto.setSbdgorgcode(strs[4]);//预算单位代码不能为空(填充付款人账号)
									}
								}
								dto.setSpackageno(tmpPackNo);  //包流水号
								dto.setSbookorgcode(bookorgCode); //核算主体代码
								dto.setSfilename(finame); //导入文件名
								dto.setSstatus(StateConstant.CONFIRMSTATE_NO);  //交易状态
								fbcount ++;
								fatherdtos.add(dto);
							}
							TvFilepackagerefDto packdto = new TvFilepackagerefDto();
							packdto.setSorgcode(bookorgCode);
							packdto.setStrecode(pldto.getTrecode()); // 国库主体代码
							packdto.setSfilename(finame);
							TsConvertfinorgDto condto = new TsConvertfinorgDto();
							condto.setSorgcode(bookorgCode);
							condto.setStrecode(pldto.getTrecode());
							List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
							if(conlist == null || conlist.size() == 0 ){
								throw new ITFEBizException("批量拨付文件["+finame+"] 中根据核算主体代码："+bookorgCode+"、 国库主体代码："+pldto.getTrecode()+" 没有在 '财政机构信息维护参数'中找到对应财政机构代码，请查证");
							}
							TsConvertfinorgDto gd = (TsConvertfinorgDto)conlist.get(0);
							packdto.setStaxorgcode(gd.getSfinorgcode());
							packdto.setScommitdate(TimeFacade.getCurrentStringTime());
							packdto.setSaccdate(TimeFacade.getCurrentStringTime());
							packdto.setSpackageno(tmpPackNo);
							packdto.setSoperationtypecode(biztype);
							packdto.setIcount(fbcount);
							packdto.setNmoney(famtPack);
							packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND);
							packdto.setSusercode(userid);
							packdto.setImodicount(0);
							fbcount = 0;
							famtPack = new BigDecimal("0.00");
							packdtos.add(packdto);
						}
					
					}					
				}
			}
			if(pldto.getType()!=null&&"ftppljz".equals(pldto.getType()))
			{
				TvBatchpayDto dto = pldto.getIdtoMap().get(fi.getName());
				if(dto!=null)
				{
					dto.setSstatus(StateConstant.FTPFILESTATE_ADDLOAD);
					multi.addUpdateIdtoList(dto);
				}
			}
			multi.setFatherDtos(fatherdtos);
			multi.setPackDtos(packdtos);
			multi.setFamt(famt); //总金额
			multi.setTotalCount(recordNum); //总笔数
			return multi;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("批量拨付文件解析出错 \n" +e.getMessage(), e);
		}
	}
	public MulitTableDto fileParserGx(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		PiLiangDto pldto = (PiLiangDto)paramdto;
		MulitTableDto multi = new MulitTableDto();
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		File fi = new File(file);
		String finame =  fi.getName().replaceAll(".tmp", ".pas") ;
		int fbcount = 0; // 分包记数器
		int recordNum = 1; // 记录记录器
		String tmpPackNo = "";
		try {
			List<String[]> fileContent = super.readFile(file, ",");
			String[] castr = fileContent.get(fileContent.size()-1);
			if("</CA>".equals(castr[0].toUpperCase())) {
				//将解析出来的文件中关于CA的部分remove掉
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
			}
			
			/*
			 * 开始分包 ，初始化如下容器
			 */
			Map<String, Map<String,Map<String,List<String[]>>>> splitAllMap = new HashMap<String, Map<String,Map<String,List<String[]>>>>(); //解释为：<付款人账号,Map<接受行,Map<附言,对应记录>>>
			Set<String> recvSet = new HashSet<String>(); //存放接受行行号
			Set<String> paySet = new HashSet<String>(); //存放付款人账号
			Set<String> addwordSet = new HashSet<String>();//广西功能新增附言分包
			/*1.首先取得付款人账号及接受行行号数量*/
			for(String[] strBuff : fileContent) {
				paySet.add(strBuff[4].trim()); //付款人账号
				recvSet.add(strBuff[1].trim()); //接受行行号
				addwordSet.add(strBuff[13]==null?"--@#@--":strBuff[13].trim());//附言
			}
			/*2.然后先按照付款人账号，接受行行号,附言分开*/
			for(String payAcct : paySet) 
			{
				Map<String,Map<String,List<String[]>>> recvSplitMap = new HashMap<String,Map<String,List<String[]>>>();//存放接收行及附言对应记录关系
				for(String rbank : recvSet)
				{
					Map<String,List<String[]>> addwordMap = new HashMap<String, List<String[]>>(); //存放附言及对应记录关系
					for(String addword:addwordSet)
					{
						List<String[]> tempList = new ArrayList<String[]>(); //同付款人、接收行、附言记录
						for(String[] dtoStr : fileContent) {
							if(payAcct.trim().equals(dtoStr[4].trim()) && rbank.trim().equals(dtoStr[1].trim())&&addword.equals(dtoStr[13]==null?"--@#@--":dtoStr[13].trim())) {
								tempList.add(dtoStr);
							}
						}
						addwordMap.put(addword, tempList);
					}
					recvSplitMap.put(rbank, addwordMap);
				}
				splitAllMap.put(payAcct, recvSplitMap);
			}
			/*3.下面是具体的分包过程，利用equals方法将所有记录在逻辑上分割开，然后分别生成相应包流水号*/
			for(String payAt : paySet) {
				Map<String,Map<String,List<String[]>>> recvSplitMap = splitAllMap.get(payAt); //得到循环中的付款人对应记录
				for(String rebank : recvSet) {
					Map<String,List<String[]>> addwordSplitMap = recvSplitMap.get(rebank);//得到附言对应记录
					for(String addword:addwordSet)
					{
						List<String[]> list = addwordSplitMap.get(addword);
						if(list != null && list.size() > 0) {
							int li_Detail = (list.size()-1) / StateConstant.QUANTITY_PACKAGE_COUNT;
							for (int k = 0; k <= li_Detail; k++) {
								int li_TempCount = 0;
								if (li_Detail == k) {
									li_TempCount = list.size();
								} else {
									li_TempCount = (k + 1) * StateConstant.QUANTITY_PACKAGE_COUNT;
								}
								tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
										.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
												SequenceName.TRAID_SEQ_CACHE,
												SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
								for (int j = k * StateConstant.QUANTITY_PACKAGE_COUNT; j < li_TempCount; j++) {
									String[] strs = (String[]) list.get(j);
									if("<CA>".equals(strs[0].toUpperCase())) {
										break ;
									}
									TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
									dto.setStrecode(pldto.getTrecode());//国库代码
									dto.setSfuncsbtcode(pldto.getFunccode()); //功能类科目代码
									dto.setSbudgettype(pldto.getBudgetype()); //预算种类
	
									dto.setSsndbnkno(strs[0]);   //发起行行号
									dto.setSrcvbnkno(strs[1]);   //接受行行号
	
									/*5.3修改为填入接收行行号
									 * dto.setSrcvreckbnkno(srcvreno);
									 */
									dto.setSrcvreckbnkno(strs[1].trim());
									
									dto.setFamt(new BigDecimal(strs[2]));  //金额
									famt = famt.add(new BigDecimal(strs[2]));
									famtPack = famtPack.add(new BigDecimal(strs[2]));
									dto.setSpayeropnbnkno(strs[3]);    //付款人开户行行号
									if(strs[4].length() < 16) {
										dto.setSpayeracct(bookorgCode+strs[4]);   //付款人账号(小于16位则前面加核算主体)
									}else {
										dto.setSpayeracct(strs[4]);//付款人账号
									}					
									dto.setSpayername(strs[5]);   //付款人名称
									dto.setSpayeraddr(strs[6]);   //付款人地址
									dto.setSpayeeopnbnkno(strs[7]);   //收款人开户行行号
									if(ChinaTest.isChinese(strs[8])) {
										throw new ITFEBizException("批量拨付文件["+finame+"] 中收款人账号'"+strs[8]+"' 包含中文字符，请查证!");
									}
									dto.setSpayeeacct(strs[8]);     //收款人账号	
									String errChi_9 = VerifyParamTrans.verifyNotUsableChinese(strs[9]);
									if(null != errChi_9 && !"".equals(errChi_9)) {
										throw new ITFEBizException("批量拨付文件["+finame+"] 中收款人名称存在非法字符："+errChi_9);
									}
									dto.setSpayeename(strs[9]);		//收款人名称
									dto.setSpayeeaddr(strs[10]);	//收款人地址
									dto.setSpaybizkind(strs[11]);   //支付业务种类
									dto.setSbiztype(strs[12]);   //业务类型号
									if(null != strs[13] && !"".equals(strs[13])) {
										String errChi_13 = VerifyParamTrans.verifyNotUsableChinese(strs[13]);
										if(null != errChi_13 && !"".equals(errChi_13)) {
											throw new ITFEBizException("批量拨付文件["+finame+"] 中附言存在非法字符："+errChi_13);
										}
									}
									if(pldto.getType() == null||"".equals(pldto.getType())){
										dto.setSaddword(strs[13]);      //附言
									}else{
										dto.setSaddword("库");      //附言(山东库区移民专用)
									}
									dto.setDentrust(TimeFacade.getCurrentDateTime()); //委托日期不能为空
									dto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期
									if(pldto.getSbdgorgcode()!=null&&!pldto.getSbdgorgcode().equals("")){
										dto.setSbdgorgcode(pldto.getSbdgorgcode());
									}else{
										if(strs[4].length() > 15) {
											dto.setSbdgorgcode(strs[4].substring(11));//预算单位代码不能为空(填充付款人账号)
										}else {
											dto.setSbdgorgcode(strs[4]);//预算单位代码不能为空(填充付款人账号)
										}
									}
									dto.setSpackageno(tmpPackNo);  //包流水号
									dto.setSbookorgcode(bookorgCode); //核算主体代码
									dto.setSfilename(finame); //导入文件名
									dto.setSstatus(StateConstant.CONFIRMSTATE_NO);  //交易状态
									fbcount ++;
									fatherdtos.add(dto);
								}
								TvFilepackagerefDto packdto = new TvFilepackagerefDto();
								packdto.setSorgcode(bookorgCode);
								packdto.setStrecode(pldto.getTrecode()); // 国库主体代码
								packdto.setSfilename(finame);
								TsConvertfinorgDto condto = new TsConvertfinorgDto();
								condto.setSorgcode(bookorgCode);
								condto.setStrecode(pldto.getTrecode());
								List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
								if(conlist == null || conlist.size() == 0 ){
									throw new ITFEBizException("批量拨付文件["+finame+"] 中根据核算主体代码："+bookorgCode+"、 国库主体代码："+pldto.getTrecode()+" 没有在 '财政机构信息维护参数'中找到对应财政机构代码，请查证");
								}
								TsConvertfinorgDto gd = (TsConvertfinorgDto)conlist.get(0);
								packdto.setStaxorgcode(gd.getSfinorgcode());
								packdto.setScommitdate(TimeFacade.getCurrentStringTime());
								packdto.setSaccdate(TimeFacade.getCurrentStringTime());
								packdto.setSpackageno(tmpPackNo);
								packdto.setSoperationtypecode(biztype);
								packdto.setIcount(fbcount);
								packdto.setNmoney(famtPack);
								packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND);
								packdto.setSusercode(userid);
								packdto.setImodicount(0);
								fbcount = 0;
								famtPack = new BigDecimal("0.00");
								packdtos.add(packdto);
							}
						}
					}					
				}
			}
			if(pldto.getType()!=null&&"ftppljz".equals(pldto.getType()))
			{
				TvBatchpayDto dto = pldto.getIdtoMap().get(fi.getName());
				if(dto!=null)
				{
					dto.setSstatus(StateConstant.FTPFILESTATE_ADDLOAD);
					multi.addUpdateIdtoList(dto);
				}
			}
			multi.setFatherDtos(fatherdtos);
			multi.setPackDtos(packdtos);
			multi.setFamt(famt); //总金额
			multi.setTotalCount(recordNum); //总笔数
			return multi;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("批量拨付文件解析出错 \n" +e.getMessage(), e);
		}
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}
}
